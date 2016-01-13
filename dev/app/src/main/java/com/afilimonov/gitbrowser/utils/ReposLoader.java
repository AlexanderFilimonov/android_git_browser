package com.afilimonov.gitbrowser.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

import com.afilimonov.gitbrowser.model.Repo;
import com.google.gson.Gson;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Alejandro on 10.01.2016.
 */
public class ReposLoader extends Loader<List<Repo>> {

    public ReposLoader(Context context, Bundle args) {
        super(context);
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
        Logger.d("ReposLoader.onForceLoad");

        RetrofitHelper retrofitHelper = new RetrofitHelper();
        Call<List<Repo>> call = retrofitHelper.getApiInterface(getContext()).listRepos("AlexanderFilimonov");
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
            List<Repo> responseRepos = response.body();
            if (responseRepos != null) {
                deliverResult(responseRepos);
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
