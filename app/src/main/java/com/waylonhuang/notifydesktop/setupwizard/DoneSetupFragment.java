package com.waylonhuang.notifydesktop.setupwizard;


import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.NotificationItem;
import com.waylonhuang.notifydesktop.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoneSetupFragment extends Fragment {

    public DoneSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_done_setup, container, false);

        Button enableNotifyButton = (Button) view.findViewById(R.id.test_notification_button);
        enableNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTestNotification();
                Snackbar.make(view, "Created test notification", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void createTestNotification() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault());
        String timeStr = dateFormat.format(new Date(time));

        NotificationManager nManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity());
        notificationBuilder.setContentTitle("Notify Desktop");
        notificationBuilder.setContentText("Notification sent at: " + timeStr);
        notificationBuilder.setTicker("Test Notification Ticker Text");
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setAutoCancel(true);
        nManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }

}
