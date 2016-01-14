package com.afilimonov.gitbrowser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
}
