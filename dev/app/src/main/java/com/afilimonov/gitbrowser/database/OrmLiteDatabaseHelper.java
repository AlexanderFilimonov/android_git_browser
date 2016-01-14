package com.afilimonov.gitbrowser.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.afilimonov.gitbrowser.model.Repo;
import com.afilimonov.gitbrowser.utils.Logger;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created with IntelliJ IDEA.
 * User: afilimonov
 * Date: 08.12.2015
 * Time: 12:00
 */
public class OrmLiteDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "main.db";
    private static final int DATABASE_VERSION = 1;

    private static OrmLiteDatabaseHelper databaseHelper;

    private Dao<Repo, Integer> repoDao = null;
    private RuntimeExceptionDao<Repo, Integer> repoRuntimeDao = null;

    public static OrmLiteDatabaseHelper getHelper() {
        return databaseHelper;
    }

    public static void initHelper(Context context) {
        databaseHelper = OpenHelperManager.getHelper(context, OrmLiteDatabaseHelper.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
        databaseHelper = null;
    }

    public OrmLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Repo.class);
        } catch (SQLException e) {
            Logger.e("Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Logger.d("database onUpgrade");
            //Так делают ленивые, гораздо предпочтительнее не удаляя БД аккуратно вносить изменения
            TableUtils.dropTable(connectionSource, Repo.class, true);
            onCreate(db, connectionSource);

        } catch (SQLException e) {
            Logger.e("Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Repo, Integer> getRepoDao() throws SQLException {
        if (repoDao == null) {
            repoDao = getDao(Repo.class);
        }
        return repoDao;
    }

    public RuntimeExceptionDao<Repo, Integer> getRepoRuntimeDao() {
        if (repoRuntimeDao == null) {
            repoRuntimeDao = getRuntimeExceptionDao(Repo.class);
        }
        return repoRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        repoDao = null;
        repoRuntimeDao = null;
    }

    public void deleteAllRepos() {
        try {
            getRepoDao().delete(getRepoDao().queryForAll());
        } catch (SQLException e) {
            Logger.e("deleteAllRepos", e);
        }
    }

    public void addRepos(final List<Repo> repos) {
        try {
            TransactionManager.callInTransaction(getConnectionSource(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Dao<Repo, Integer> dao = getRepoDao();
                    for (Repo repo : repos) {
                        dao.createOrUpdate(repo);
                    }
                    return null;
                }
            });
        } catch (SQLException e) {
            Logger.e("addRepos", e);
        }
    }

    public List<Repo> getAllRepos() {
        try {
            List<Repo> list = getRepoDao().queryForAll();
            if (list != null) {
                return list;
            }
        } catch (SQLException e) {
            Logger.e("getAllRepos", e);
        }
        return new ArrayList<>();
    }
}

