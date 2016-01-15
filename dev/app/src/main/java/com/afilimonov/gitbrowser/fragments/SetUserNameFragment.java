package com.afilimonov.gitbrowser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.model.Repo;
import com.afilimonov.gitbrowser.utils.Constants;
import com.afilimonov.gitbrowser.utils.Logger;
import com.afilimonov.gitbrowser.utils.Preferences;
import com.afilimonov.gitbrowser.utils.ReposLoader;

import java.util.List;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-14.
 * Screen with first time set of user name
 */
public class SetUserNameFragment extends BaseFragment {

    protected EditText userNameField;
    protected View loadReposButton;
    protected View loadReposButtonProgressBar;

    private LoaderCallback loaderCallback;

    public static SetUserNameFragment newInstance() {
        return new SetUserNameFragment();
    }

    public SetUserNameFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("SetUserNameFragment.onCreateView()");
        view = inflater.inflate(R.layout.fragment_set_user_name, container, false);
        userNameField = (EditText) view.findViewById(R.id.userNameField);
        String userName = Preferences.getString(Constants.USER_NAME_KEY, null, getContext());
        if (userName != null) userNameField.setText(userName);
        initLoadReposButton();
        initLoader();
        return view;
    }

    private void initLoader() {
        loaderCallback = new LoaderCallback();
        Bundle bundle = new Bundle();
        bundle.putString(ReposLoader.USER_NAME_KEY, getContext().getString(R.string.userNameDefault));
        getLoaderManager().initLoader(LoaderCallback.REPOS_LOADER_ID, bundle, loaderCallback);
    }

    protected void initLoadReposButton() {
        loadReposButtonProgressBar = view.findViewById(R.id.loadReposButtonProgressBar);
        loadReposButton = view.findViewById(R.id.loadReposButton);
        loadReposButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameField.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    userNameField.setError(getString(R.string.userNameFieldEmptyErrorText));

                } else {
                    loadReposButton.setVisibility(View.INVISIBLE);
                    loadReposButtonProgressBar.setVisibility(View.VISIBLE);

                    loadRepos(userName);
                }
            }
        });
    }

    private void loadRepos(String enteredUserName) {
        Loader<List<Repo>> loader = getLoaderManager().getLoader(LoaderCallback.REPOS_LOADER_ID);
        if (loader == null || !enteredUserName.equals(((ReposLoader) loader).getUserName())) {
            Bundle bundle = new Bundle();
            bundle.putString(ReposLoader.USER_NAME_KEY, enteredUserName);
            loader = getLoaderManager().restartLoader(LoaderCallback.REPOS_LOADER_ID, bundle, loaderCallback);
        }
        loader.forceLoad();
    }

    private class LoaderCallback implements LoaderManager.LoaderCallbacks<List<Repo>> {

        public static final int REPOS_LOADER_ID = 1;

        @Override
        public Loader<List<Repo>> onCreateLoader(int id, Bundle args) {
            Loader<List<Repo>> loader = null;
            if (id == REPOS_LOADER_ID) {
                loader = new ReposLoader(getContext(), args);
            }
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<Repo>> loader, List<Repo> data) {
            Logger.d("LoaderCallback.onLoadFinished");
            loadReposButton.setVisibility(View.VISIBLE);
            loadReposButtonProgressBar.setVisibility(View.GONE);
            if (data != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ReposListFragment.newInstance())
                        .commitAllowingStateLoss();
                getLoaderManager().destroyLoader(REPOS_LOADER_ID);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Repo>> loader) {
            Logger.d("LoaderCallback.onLoaderReset");
        }
    }
}
