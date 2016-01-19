package com.afilimonov.gitbrowser.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afilimonov.gitbrowser.R;
import com.afilimonov.gitbrowser.utils.Constants;
import com.afilimonov.gitbrowser.utils.Logger;
import com.afilimonov.gitbrowser.utils.Preferences;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-15.
 * screen with changing user name
 */
public class ChangeUserNameFragment extends SetUserNameFragment {

    public static ChangeUserNameFragment newInstance() {
        return new ChangeUserNameFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected void initLoadReposButton() {
        loadReposButtonProgressBar = view.findViewById(R.id.loadReposButtonProgressBar);
        loadReposButton = view.findViewById(R.id.loadReposButton);
        ((TextView) loadReposButton).setText(getString(R.string.saveButtonTitle));
        loadReposButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameField.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    userNameField.setError(getString(R.string.userNameFieldEmptyErrorText));

                } else {
                    Preferences.putString(Constants.USER_NAME_CHANGED_KEY, userName, getContext());

                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
    }
}
