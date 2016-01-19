package com.afilimonov.gitbrowser.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.afilimonov.gitbrowser.R;

/**
 * Created by Aliaksandr_Filimonau on 2016-01-16.
 * helper class for open camera with requesting permission if needed
 */
public class CameraHelper {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    public static final int CAMERA_ACTIVITY_REQUEST_CODE = 101;

    private Activity activity;
    private ActivityListeners.ActivityResultListener callback;

    public CameraHelper(Activity activity, ActivityListeners.ActivityResultListener callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void runCamera() {
        if (!isPermissionGranted()) {
            Logger.d("camera permission not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                Logger.d("camera shouldShowRequestPermissionRationale");
                showPermissionExplanationDialog();

            } else {
                Logger.d("camera doesn't shouldShowRequestPermissionRationale");
                requestPermissions();
            }
        } else {
            Logger.d("permission granted, show camera");
            startCameraActivity();
        }
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.cameraPermExplDialogTitle))
                .setMessage(activity.getString(R.string.cameraPermExplDialogTitle))
                .setPositiveButton(activity.getString(R.string.cameraPermExplDialogOkButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(activity.getString(R.string.cameraPermExplDialogCancelButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private boolean isPermissionGranted() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
    }

    private void startCameraActivity() {
        if (activity instanceof ActivityListeners) {
            ((ActivityListeners) activity).getContainer().getActivityResultListeners().add(new ActivityListeners.ActivityResultListener() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
                        ((ActivityListeners) activity).getContainer().getActivityResultListeners().remove(this);
                        if (callback != null) {
                            callback.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            });
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(cameraIntent, CAMERA_ACTIVITY_REQUEST_CODE);
    }

    private void requestPermissions() {
        Logger.d("request camera permissions");
        if (activity instanceof ActivityListeners) {
            ((ActivityListeners) activity).getContainer().getPermissionListeners().add(new ActivityListeners.RequestPermissionListener() {
                @Override
                public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                    ((ActivityListeners) activity).getContainer().getPermissionListeners().remove(this);
                    CameraHelper.this.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            });
        }

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (Manifest.permission.CAMERA.equals(permissions[i])) {
                    Logger.d("onRequestPermissionsResult for camera");
                    int result = grantResults[i];
                    if (PackageManager.PERMISSION_GRANTED == result) {
                        Logger.d("camera permisson granted");
                        startCameraActivity();
                    } else {
                        Logger.d("camera permisson not granted");
                        Toast.makeText(activity, "Permisson not granted, unable to run camera", Toast.LENGTH_SHORT).show(); // debug
                    }
                    break;
                }
            }
        }
    }
}
