package com.afilimonov.gitbrowser.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afilimonov.gitbrowser.R;

/**
 * Created by Alejandro on 09.01.2016.
 */
public class MainFragment extends BaseFragment {

    public MainFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    public static Fragment newInstance() {
        return new MainFragment();
    }
}
