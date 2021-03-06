package com.afilimonov.gitbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afilimonov.gitbrowser.fragments.ReposListFragment;
import com.afilimonov.gitbrowser.fragments.SetUserNameFragment;
import com.afilimonov.gitbrowser.utils.ActivityListeners;
import com.afilimonov.gitbrowser.utils.Constants;
import com.afilimonov.gitbrowser.utils.Logger;
import com.afilimonov.gitbrowser.utils.Preferences;

public class MainActivity extends AppCompatActivity implements ActivityListeners {

    private ActivityListeners.Container listenersContainer = new ActivityListeners.Container();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("================== MainActivity.onCreate() ===================");
        setContentView(R.layout.activity_main);
        initToolbar();
        showStartFragment();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void showStartFragment() {
        if (Preferences.getString(Constants.USER_NAME_KEY, null, this) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SetUserNameFragment.newInstance())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ReposListFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        listenersContainer.runRequestPermissionListeners(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        listenersContainer.runActivityResultListeners(requestCode, resultCode, data);
    }

    @Override
    public Container getContainer() {
        return listenersContainer;
    }
}
