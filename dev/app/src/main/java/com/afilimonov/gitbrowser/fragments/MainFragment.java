package com.afilimonov.gitbrowser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.afilimonov.gitbrowser.utils.Logger;
import com.afilimonov.gitbrowser.utils.RetrofitHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Alejandro on 09.01.2016.
 */
public class MainFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private View settingsView;
    private EditText userNameField;

    private List<Repo> repos;

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
        return view;
    }

    private void initShowReposButton() {
        view.findViewById(R.id.showReposButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameField.getText().toString();
                if (!TextUtils.isEmpty(userName)) {
                    getReposList();
                }
            }
        });
    }

    private void getReposList() {
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<List<Repo>> call = retrofitHelper.getApiInterface(getContext()).listRepos("AlexanderFilimonov");
        Logger.d("call.enqueue");
        call.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
                Logger.d("onResponse " + response);
                if (response.isSuccess()) {
                    List<Repo> responseRepos = response.body();
                    if (responseRepos != null) {
                        repos.clear();
                        repos.addAll(responseRepos);
                        for (Repo repo : repos) {
                            Logger.d(new Gson().toJson(repo));
                        }
                        showListView();
                    }
                } else {
                    showToast("received error response " + response);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Logger.e(t);
                showToast("failure while sending request");
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
}
