package com.waylonhuang.notifydesktop;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.waylonhuang.notifydesktop.MainActivity.PREFS_FILE;

/**
 * Created by Waylon on 6/19/2017.
 */

public class NotificationListener8 extends NotificationListenerService {
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        SharedPreferences settings = getSharedPreferences(PREFS_FILE, 0);
        boolean signedIn = settings.getBoolean("signedIn", false);
        boolean history = settings.getBoolean("history", true);
        String offApps = settings.getString("offApps", "");
        String titleOnlyApps = settings.getString("titleOnlyApps", "");
        String[] offArr = offApps.split(",", -1);
        String[] titleApps = titleOnlyApps.split(",", -1);

        if (!signedIn) {
            System.out.println("Not signed in!");
            return;
        }

        // UID from the signed in user.
        String uid = settings.getString("uid", null);
        if (uid == null) {
            System.out.println("uid is null!");
            return;
        }

        long postTime = sbn.getPostTime();

        String packageName = sbn.getPackageName();
        if (Arrays.asList(offArr).contains(packageName)) {
            System.out.println("Notifications turned off for " + packageName);
            return;
        }

        PackageManager packageManager = getApplicationContext().getPackageManager();
        String appName = "";
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        String title = extras.getString("android.title");
        String text = extras.getString("android.text");

        // Check if the user does not want to include the content of the notification.
        if (Arrays.asList(titleApps).contains(packageName)) {
            text = "";
        }

        // Send notification to server.
        post(uid, appName, title, text, postTime);

        System.out.println("Posted!");

        // Check if history is enabled.
        if (history) {
            long _id = System.currentTimeMillis();
            NotificationItem item = new NotificationItem(getApplicationContext(), _id, appName, packageName, title, text, postTime);

            // Save the notification to history record.
            saveNotification(item);
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, params[0]);
            Request request = new Request.Builder()
                    .url("https://notifydesktop.herokuapp.com/android")
                    .post(body)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .build();

            System.out.println(params[0]);

            Response response;
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void post(String uid, String appName, String title, String text, long time) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("appName", appName);
        params.put("title", title);
        params.put("text", text);
        params.put("time", Long.toString(time));

        String body = null;
        try {
            body = getDataString(params);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
        asyncTaskRunner.execute(body);
    }

    private String getDataString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    private void saveNotification(NotificationItem item) {
        System.out.println("Saved notification!");

        Context context = getApplicationContext();
        NotificationSQLiteHelper helper = NotificationSQLiteHelper.getInstance(context);
        helper.addNotificationItem(item);
    }
}
