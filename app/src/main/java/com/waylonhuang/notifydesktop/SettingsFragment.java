package com.waylonhuang.notifydesktop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by Waylon on 4/25/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        if (fragment.getArguments() == null) {
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Preference resetPref = (Preference) findPreference("reset");
        resetPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                clearPreferences();
                return true;
            }
        });

        Preference mailPref = (Preference) findPreference("contact");
        mailPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, "wwaylonhuang@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Japanese Dictionary Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, "Feedback regarding app:\n");

                startActivity(Intent.createChooser(intent, "Send Email"));
                return true;
            }
        });

        Preference buildPref = (Preference) findPreference("build");
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        buildPref.setSummary(buildDate.toString());

        Preference sourcePref = (Preference) findPreference("source");
        PackageManager packageManager = getContext().getPackageManager();
        String sourceStr = packageManager.getInstallerPackageName("com.waylonhuang.japanesedictionary");
        if (sourceStr == null) {
            sourceStr = "Side Loaded";
        } else {
            sourceStr = "Google Play";
        }
        sourcePref.setSummary(sourceStr);

        Preference versionPref = (Preference) findPreference("version");
        versionPref.setSummary(BuildConfig.VERSION_NAME);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void clearPreferences() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Clear all preferences set by the user")
                .setTitle("Reset preferences");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), "Preferences reset", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
