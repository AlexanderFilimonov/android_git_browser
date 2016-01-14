package com.afilimonov.gitbrowser.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Alejandro on 09.01.2016.
 * model for git repository
 */
@DatabaseTable(tableName = "repos")
public class Repo {

    public final static String COLUMN_ID = "id";

    @DatabaseField(columnName = COLUMN_ID, id = true)
    @SerializedName("id")
    public String id;

    @DatabaseField
    @SerializedName("name")
    public String name;

    @DatabaseField
    @SerializedName("full_name")
    public String fullName;

    @DatabaseField
    @SerializedName("html_url")
    public String htmlUrl;

    public static Repo fromJson(String string) {
        return new Gson().fromJson(string, Repo.class);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
