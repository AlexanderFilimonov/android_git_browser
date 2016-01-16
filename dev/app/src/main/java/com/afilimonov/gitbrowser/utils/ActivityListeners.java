package com.afilimonov.gitbrowser.utils;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-16.
 * inmplement this interface in your Activity
 * and use getContainer() to set listeners for
 * onActivityResult and onRequestPermissionsResult
 * methods
 */
public interface ActivityListeners {

    interface RequestPermissionListener {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }

    interface ActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    Container getContainer();

    class Container {
        final private List<RequestPermissionListener> permissionListeners = new ArrayList<>();
        final private List<ActivityResultListener> activityResultListeners = new ArrayList<>();

        public List<RequestPermissionListener> getPermissionListeners() {
            return permissionListeners;
        }

        public List<ActivityResultListener> getActivityResultListeners() {
            return activityResultListeners;
        }

        public void runActivityResultListeners(int requestCode, int resultCode, Intent data) {
            for (ActivityResultListener listener : getActivityResultListeners()) {
                listener.onActivityResult(requestCode, resultCode, data);
            }
        }

        public void runRequestPermissionListeners(int requestCode, String[] permissions, int[] grantResults) {
            for (RequestPermissionListener listener : getPermissionListeners()) {
                listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
