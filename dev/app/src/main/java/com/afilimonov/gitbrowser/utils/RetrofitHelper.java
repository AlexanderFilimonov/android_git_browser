package com.afilimonov.gitbrowser.utils;

import android.content.Context;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.model.Repo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Alejandro on 09.01.2016. 
 */
public class RetrofitHelper {

    private GitApiInterface gitApiInterface;

    public GitApiInterface getApiInterface(Context context) {
        if (gitApiInterface == null) {
            init(context.getString(R.string.githubServerUrl));
        }
        return gitApiInterface;
    }

    public void init(String url) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        gitApiInterface = retrofit.create(GitApiInterface.class);
    }

    public interface GitApiInterface {
        @GET("/users/{user}/repos")
        Call<List<Repo>> listRepos(@Path("user") String user);
    }
}
