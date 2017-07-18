package com.waylonhuang.notifydesktop.setupwizard;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.R;

public class WebsiteFragment extends Fragment {
    private static final String URL = "https://notify-desktop-d1548.firebaseapp.com";

    public WebsiteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_website, container, false);

        Button linkButton = (Button) view.findViewById(R.id.webapp_link_button);
        linkButton.setText(URL);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("website", "https://notify-desktop-d1548.firebaseapp.com");
                clipboard.setPrimaryClip(clip);

                Snackbar.make(view, "Copied link to clipboard!", Snackbar.LENGTH_SHORT).show();
            }
        });

        Button siteButton = (Button) view.findViewById(R.id.webapp_link);
        siteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
                startActivity(browserIntent);
            }
        });

        Button shareButton = (Button) view.findViewById(R.id.webapp_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Web app link.");
                i.putExtra(Intent.EXTRA_TEXT, URL);
                startActivity(Intent.createChooser(i, "Notification Desktop"));
            }
        });

        return view;
    }

}
