package com.afilimonov.gitbrowser.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Alejandro on 09.01.2016.
 * Base fragment realization
 */
public class BaseFragment extends Fragment {

    protected View view;

    public BaseFragment() {
    }

    protected void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
