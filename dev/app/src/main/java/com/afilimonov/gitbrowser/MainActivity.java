package com.afilimonov.gitbrowser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.afilimonov.gitbrowser.fragments.SetUserNameFragment;
import com.afilimonov.gitbrowser.utils.Logger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("================== MainActivity.onCreate() ===================");
        setContentView(R.layout.activity_main);
        showStartFragment();
    }

    private void showStartFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SetUserNameFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // will be used in the future
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
