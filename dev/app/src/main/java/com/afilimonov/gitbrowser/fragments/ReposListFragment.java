package com.afilimonov.gitbrowser.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.database.OrmLiteDatabaseHelper;
import com.afilimonov.gitbrowser.model.Repo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-14.
 * screen with list of repos for selected user
 */
public class ReposListFragment extends BaseFragment {

    private List<Repo> repos;

    public static ReposListFragment newInstance() {
        return new ReposListFragment();
    }

    public ReposListFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_repos_list, container, false);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        repos = new ArrayList<>();
        repos.addAll(OrmLiteDatabaseHelper.getHelper().getAllRepos());

        Adapter adapter = new Adapter();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.listView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void showRepoDetails(Repo repo, View v) {
        View titleView = v.findViewById(R.id.titleView);
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
        fragment.setTransitionName(transitionName);
        setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_move));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));
        // Set shared and scene transitions on 2nd fragment
        fragment.setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.trans_move));
        fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));
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
}
