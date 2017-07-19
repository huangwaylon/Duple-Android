package com.waylonhuang.notifydesktop;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.v7.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Waylon on 7/19/2017.
 */

// https://developer.android.com/training/monitoring-device-state/battery-monitoring.html#CurrentLevel
public class PowerConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_FULL) {
            createNotification(context, "Battery full", "Device is fully charged");
        } else if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            createNotification(context, "Plugged in", "Device is currently charging");
        }
    }

    private void createNotification(Context context, String title, String content) {
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
        String timeStr = dateFormat.format(new Date(time));

        NotificationManager nManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(content);
        notificationBuilder.setTicker(content);
        notificationBuilder.setSmallIcon(R.drawable.sb_small_icon);
        notificationBuilder.setAutoCancel(true);
        nManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
}