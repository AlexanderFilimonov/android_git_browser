package com.afilimonov.gitbrowser.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afilimonov.gitbrowser.MainActivity;
import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.database.OrmLiteDatabaseHelper;
import com.afilimonov.gitbrowser.model.Repo;
import com.afilimonov.gitbrowser.utils.CameraHelper;
import com.afilimonov.gitbrowser.utils.Constants;
import com.afilimonov.gitbrowser.utils.LocationServiceHelper;
import com.afilimonov.gitbrowser.utils.Logger;
import com.afilimonov.gitbrowser.utils.Preferences;
import com.afilimonov.gitbrowser.utils.ReposLoader;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-14.
 * screen with list of repos for selected user
 */
public class ReposListFragment extends BaseFragment {

    private List<Repo> repos;
    private LoaderCallback loaderCallback;
    private Adapter adapter;

    public static ReposListFragment newInstance() {
        return new ReposListFragment();
    }

    public ReposListFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("ReposListFragment.onCreateView");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_repos_list, container, false);
            initRecyclerView();
        }
        initLoader();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("ReposListFragment.onStart");
        String newUserName = Preferences.getString(Constants.USER_NAME_CHANGED_KEY, null, getContext());
        if (!TextUtils.isEmpty(newUserName)) {
            Preferences.remove(Constants.USER_NAME_CHANGED_KEY, getContext());
            if (!newUserName.equals(getUserName())) {
                loadRepos(newUserName);
            }
        }
    }

    private void initLoader() {
        if (loaderCallback == null) {
            loaderCallback = new LoaderCallback();
        }
        Loader<List<Repo>> loader = getLoaderManager().getLoader(LoaderCallback.REPOS_LOADER_ID);
        if (loader == null) {
            Bundle bundle = new Bundle();
            bundle.putString(ReposLoader.USER_NAME_KEY, getUserName());
            getLoaderManager().initLoader(LoaderCallback.REPOS_LOADER_ID, bundle, loaderCallback);
        }
    }

    @NonNull
    private String getUserName() {
        return Preferences.getString(Constants.USER_NAME_KEY, getContext().getString(R.string.userNameDefault), getContext());
    }

    private void loadRepos(String newUserName) {
        Loader<List<Repo>> loader = getLoaderManager().getLoader(LoaderCallback.REPOS_LOADER_ID);
        if (loader == null || !newUserName.equals(getUserName())) {
            Bundle bundle = new Bundle();
            bundle.putString(ReposLoader.USER_NAME_KEY, newUserName);
            loader = getLoaderManager().restartLoader(LoaderCallback.REPOS_LOADER_ID, bundle, loaderCallback);
        }

        showProgressBar();
        loader.forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.repos_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeUserOption:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ChangeUserNameFragment.newInstance())
                        .addToBackStack(SetUserNameFragment.class.getName())
                        .commit();
                break;

            case R.id.cameraOption:
                new CameraHelper(getActivity(), new MainActivity.ActivityResultListener() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        Logger.d("handle photo provided by camera");
                    }
                }).runCamera();
                break;

            case R.id.locationOption:
                LocationServiceHelper.getInstance(getActivity()).connect(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Logger.d("finally received location " + location);
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        repos = new ArrayList<>();
        repos.addAll(OrmLiteDatabaseHelper.getHelper().getAllRepos());

        adapter = new Adapter();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void showRepoDetails(Repo repo, View listItemView) {
        View titleView = listItemView.findViewById(R.id.titleView);
        RepoDetailsFragment fragment = RepoDetailsFragment.newInstance(repo);
        applyTransitions(fragment, titleView);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .addSharedElement(titleView, getTransitionName(titleView))
                .commit();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyTransitions(RepoDetailsFragment fragment, View v) {
        String transitionName = v.getTransitionName();
        fragment.setTitleViewTransitionName(transitionName);
        setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_move));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));

        fragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_move));
        fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private String getTransitionName(View view) {
        return view.getTransitionName();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repos_list_item_view, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            Repo repo = repos.get(i);
            viewHolder.titleView.setText(repo.name);
            applyTransitionName(viewHolder, i);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRepoDetails(repos.get(i), v);
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private void applyTransitionName(ViewHolder viewHolder, int i) {
            viewHolder.titleView.setTransitionName("titleView." + i);
        }

        @Override
        public int getItemCount() {
            return repos.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.titleView);
        }
    }

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<List<Repo>> {

        public static final int REPOS_LOADER_ID = 1;

        @Override
        public Loader<List<Repo>> onCreateLoader(int id, Bundle args) {
            Loader<List<Repo>> loader = null;
            if (id == REPOS_LOADER_ID) {
                loader = new ReposLoader(getContext(), args);
            }
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<Repo>> loader, List<Repo> data) {
            Logger.d("LoaderCallback.onLoadFinished");
            hideProgressBar();
            if (data != null) {
                repos.clear();
                repos.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Repo>> loader) {
            Logger.d("LoaderCallback.onLoaderReset");
        }
    }

    private void hideProgressBar() {
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.listView).setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.listView).setVisibility(View.GONE);
    }
}
