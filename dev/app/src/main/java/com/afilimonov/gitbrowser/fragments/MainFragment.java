package com.afilimonov.gitbrowser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.model.Repo;
import com.afilimonov.gitbrowser.utils.ReposLoader;
import com.afilimonov.gitbrowser.utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 09.01.2016.
 */
public class MainFragment extends BaseFragment {

    private static final int REPOS_LOADER_ID = 1;

    private RecyclerView recyclerView;
    private Adapter adapter;
    private View settingsView;
    private EditText userNameField;

    private List<Repo> repos;
    private LoaderCallback loaderCallback;

    public MainFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        settingsView = view.findViewById(R.id.settingsContainer);
        userNameField = (EditText) view.findViewById(R.id.userNameField);
        initRecyclerView();
        initShowReposButton();
        initLoader();

        return view;
    }

    private void loadRepos() {
        Loader<List<Repo>> loader = getLoaderManager().getLoader(REPOS_LOADER_ID);
        if (loader == null) {
            Bundle bundle = new Bundle();
            bundle.putString("user", userNameField.getText().toString());
            loader = getLoaderManager().restartLoader(REPOS_LOADER_ID, bundle, loaderCallback);
        }
        loader.forceLoad();
    }

    private void initLoader() {
        loaderCallback = new LoaderCallback();
        Bundle bundle = new Bundle();
        bundle.putString("user", getContext().getString(R.string.userNameDefault));
        getLoaderManager().initLoader(REPOS_LOADER_ID, bundle, loaderCallback);
    }

    private void initShowReposButton() {
        view.findViewById(R.id.showReposButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameField.getText().toString();
                if (!TextUtils.isEmpty(userName)) {
                    loadRepos();
                }
            }
        });
    }

    private void showListView() {
        settingsView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        recyclerView.invalidate();
        Logger.d("showListView [" + repos.size() + "]");
    }

    private void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void initRecyclerView() {
        repos = new ArrayList<>();
        adapter = new Adapter();
        recyclerView = (RecyclerView) view.findViewById(R.id.listView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static Fragment newInstance() {
        return new MainFragment();
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.repos_list_item_view, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Repo repo = repos.get(i);
            viewHolder.titleView.setText(repo.name);
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
            if (data != null) {
                repos.clear();
                repos.addAll(data);
                showListView();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Repo>> loader) {
            Logger.d("LoaderCallback.onLoaderReset");
        }
    }
}
