package com.afilimonov.gitbrowser.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.model.Repo;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-14.
 * screen with repository details
 */
public class RepoDetailsFragment extends BaseFragment {

    private static final String REPO_KEY = "repo";

    private Repo repo;
    private String titleViewTransitionName;

    public static RepoDetailsFragment newInstance(Repo repo) {
        RepoDetailsFragment fragment = new RepoDetailsFragment();
        Bundle args = new Bundle();
        args.putString(REPO_KEY, repo.toJson());
        fragment.setArguments(args);
        return fragment;
    }

    public RepoDetailsFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            repo = Repo.fromJson(getArguments().getString(REPO_KEY));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            repo = Repo.fromJson(savedInstanceState.getString(REPO_KEY));
        }
        view = inflater.inflate(R.layout.fragment_repo_details, container, false);

        TextView titleView = (TextView) view.findViewById(R.id.titleView);
        titleView.setText(repo.name);

        TextView detailsView = (TextView) view.findViewById(R.id.detailsView);
        detailsView.setText(getActivity().getString(R.string.repoDetailsText, repo.id, repo.fullName, repo.htmlUrl));

        applyTransitionName(view);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void applyTransitionName(View view) {
        view.findViewById(R.id.titleView).setTransitionName(titleViewTransitionName);
    }

    public void setTitleViewTransitionName(String titleViewTransitionName) {
        this.titleViewTransitionName = titleViewTransitionName;
    }
}
