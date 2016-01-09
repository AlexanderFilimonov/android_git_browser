package com.afilimonov.gitbrowser.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alejandro on 09.01.2016.
 */
public class Repo {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("full_name")
    public String fullName;

    @SerializedName("html_url")
    public String htmlUrl;
}
