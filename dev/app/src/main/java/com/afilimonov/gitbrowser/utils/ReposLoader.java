package com.afilimonov.gitbrowser.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.afilimonov.gitbrowser.database.OrmLiteDatabaseHelper;
import com.afilimonov.gitbrowser.model.Repo;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Alejandro on 10.01.2016.
 * Loader for getting the list of repositories
 */
public class ReposLoader extends Loader<List<Repo>> {

    public static final String USER_NAME_KEY = "userName";

    private String userName;

    public ReposLoader(Context context, Bundle args) {
        super(context);
        userName = args.getString(USER_NAME_KEY);
    }

    @Override
    public void deliverResult(List<Repo> data) {
        Logger.d("ReposLoader.deliverResult(" + data + ")");
        if (isReset()) {
            Logger.d("ReposLoader.deliverResult. isReset() == true");
            return;
        }
        if (isStarted()) {
            Logger.d("ReposLoader.super.deliverResult");
            super.deliverResult(data);
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        if (TextUtils.isEmpty(userName)) {
            Logger.d("user name is empty");
            return;
        }

        Logger.d("ReposLoader.onForceLoad");
        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<List<Repo>> call = retrofitHelper.getApiInterface(getContext()).listRepos(userName);
        call.enqueue(new Callback<List<Repo>>() {
            @Override
            public void onResponse(Response<List<Repo>> response, Retrofit retrofit) {
                onReceivedResponse(response);
            }

            @Override
            public void onFailure(Throwable t) {
                Logger.e(t);
                deliverResult(null);
            }
        });
    }

    private void onReceivedResponse(Response<List<Repo>> response) {
        Logger.d("onResponse " + response);
        if (response.isSuccess()) {
            List<Repo> repos = response.body();
            if (repos != null) {
                // TODO: 2016-01-14 save repos to db
                OrmLiteDatabaseHelper.getHelper().deleteAllRepos();
                OrmLiteDatabaseHelper.getHelper().addRepos(repos);
                deliverResult(repos);
            }
        } else {
            deliverResult(null);
        }
    }

    @Override
    protected void onAbandon() {
        super.onAbandon();
        Logger.d("ReposLoader.onAbandon");
    }

    @Override
    protected void onReset() {
        super.onReset();
        Logger.d("ReposLoader.onReset");
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        Logger.d("ReposLoader.onStartLoading");
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        Logger.d("ReposLoader.onStopLoading");
    }

}
