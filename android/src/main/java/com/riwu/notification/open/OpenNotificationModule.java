package com.riwu.notification.open;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class OpenNotificationModule extends ReactContextBaseJavaModule {
    public static final String LOG_TAG = "RNOpenNotification";// all logging should use this tag
    private static final int REQUEST_CODE = 1;

    private OpenNotificationConfig config;


    public OpenNotificationModule(ReactApplicationContext reactContext) {
        super(reactContext);

        Application applicationContext = (Application) reactContext.getApplicationContext();

        this.config = new OpenNotificationConfig(applicationContext);
    }

    @Override
    public String getName() {
        return "OpenNotification";
    }

    @ReactMethod
    public void open() {
        ReactContext reactContext = getReactApplicationContext();
        String packageName = reactContext.getPackageName();
        Intent intent = new Intent();

        checkOrCreateChannel(getNotificationManager());
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
            intent.putExtra("app_package", packageName);
            intent.putExtra("app_uid", reactContext.getApplicationInfo().uid);
            intent.putExtra("android.provider.extra.CHANNEL_ID", this.config.getChannelId());
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", packageName);
            intent.putExtra("app_package", packageName);
            intent.putExtra("app_uid", reactContext.getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + packageName));
        }

        getReactApplicationContext().startActivityForResult(intent, REQUEST_CODE, null);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getReactApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void checkOrCreateChannel(NotificationManager manager) {
        if (manager == null)
            return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        NotificationChannel channel = manager.getNotificationChannel(this.config.getChannelId());

        if (channel != null)
            return;


        Bundle bundle = new Bundle();

        int importance = NotificationManager.IMPORTANCE_HIGH;
        final String importanceString = bundle.getString("importance");


        if (importanceString != null) {
            switch(importanceString.toLowerCase()) {
                case "default":
                    importance = NotificationManager.IMPORTANCE_DEFAULT;
                    break;
                case "max":
                    importance = NotificationManager.IMPORTANCE_MAX;
                    break;
                case "high":
                    importance = NotificationManager.IMPORTANCE_HIGH;
                    break;
                case "low":
                    importance = NotificationManager.IMPORTANCE_LOW;
                    break;
                case "min":
                    importance = NotificationManager.IMPORTANCE_MIN;
                    break;
                case "none":
                    importance = NotificationManager.IMPORTANCE_NONE;
                    break;
                case "unspecified":
                    importance = NotificationManager.IMPORTANCE_UNSPECIFIED;
                    break;
                default:
                    importance = NotificationManager.IMPORTANCE_HIGH;
            }
        }

        channel = new NotificationChannel(this.config.getChannelId(), this.config.getChannelName(), importance);
        channel.setDescription(this.config.getChannelDescription());
        channel.enableLights(true);
        channel.enableVibration(true);

        manager.createNotificationChannel(channel);
    }
}
