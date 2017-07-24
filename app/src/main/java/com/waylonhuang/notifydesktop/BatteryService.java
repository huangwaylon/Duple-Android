package com.waylonhuang.notifydesktop;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import static android.os.BatteryManager.BATTERY_STATUS_DISCHARGING;
import static android.os.BatteryManager.BATTERY_STATUS_FULL;

/**
 * Created by Waylon on 7/22/2017.
 */

public class BatteryService extends Service {
    private static final String TAG = BatteryService.class.getSimpleName();

    private BroadcastReceiver batteryChangeReceiver;

    private boolean sendMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.wtf(TAG, "onCreate");

        IntentFilter batteryChangeFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                checkBatteryLevel(intent);
            }
        };

        sendMessage = true;

        registerReceiver(batteryChangeReceiver, batteryChangeFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryChangeReceiver != null) {
            unregisterReceiver(batteryChangeReceiver);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.wtf(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkBatteryLevel(Intent batteryChangeIntent) {
        int status = batteryChangeIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (sendMessage && status == BATTERY_STATUS_FULL) {
            Log.d(TAG, "Battery full.");

            NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle("Battery is now full.");
            notificationBuilder.setContentText("Device is fully charged.");
            notificationBuilder.setTicker("Device is fully charged.");
            notificationBuilder.setSmallIcon(R.drawable.sb_small_icon);
            notificationBuilder.setAutoCancel(true);
            nManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

            sendMessage = false;
        } else if (status == BATTERY_STATUS_DISCHARGING) {
            sendMessage = true;
        }
    }
}
