package com.waylonhuang.notifydesktop.setupwizard;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnableServiceFragment extends Fragment {

    public EnableServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        TextView statusTV = (TextView)view.findViewById(R.id.status_tv);
        Button button = (Button)view.findViewById(R.id.enable_notifications);

        if (NotificationManagerCompat.getEnabledListenerPackages(getActivity()).contains(getActivity().getPackageName())) {
            statusTV.setText("Notification access is enabled.");
            button.setText("Disable");
            statusTV.setTextColor(Color.rgb(76, 175, 80));
        } else {
            statusTV.setText("Notification access is not enabled.");
            button.setText("Enable");
            statusTV.setTextColor(Color.rgb(244, 67, 54));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enable, container, false);

        Button enableNotifyButton = (Button) view.findViewById(R.id.enable_notifications);
        enableNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });

        return view;
    }

}
