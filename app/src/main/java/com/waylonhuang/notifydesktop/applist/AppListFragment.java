package com.waylonhuang.notifydesktop.applist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waylonhuang.notifydesktop.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;

import static com.waylonhuang.notifydesktop.MainActivity.PREFS_FILE;
import static com.waylonhuang.notifydesktop.applist.AppItem.APP_ITEM_INTENT;
import static com.waylonhuang.notifydesktop.applist.AppItem.NOTIFY_OFF_INTENT;

public class AppListFragment extends Fragment {
    private Set<String> offList;
    private Set<String> titleList;

    private FlexibleAdapter<AbstractItem> adapter;

    private BroadcastReceiver appItemReceiver;
    private IntentFilter appItemFilter;

    public AppListFragment() {
    }

    public static AppListFragment newInstance() {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup filter and receiver for receiving the done signal from the setup wizard.
        appItemFilter = new IntentFilter(APP_ITEM_INTENT);
        appItemReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String pkg = intent.getStringExtra("package");
                String app = intent.getStringExtra("app");
                boolean off = intent.getBooleanExtra("off", true);
                if (intent.getIntExtra("type", NOTIFY_OFF_INTENT) == NOTIFY_OFF_INTENT) {
                    if (off) {
                        offList.add(pkg);
                    } else {
                        offList.remove(pkg);
                    }
                } else {
                    if (off) {
                        titleList.add(pkg);
                    } else {
                        titleList.remove(pkg);
                    }
                }
                View view = getView();
                if (view != null) {
                    Snackbar.make(view, "Updated settings for " + app, Snackbar.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();

                saveSettings();
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();

        saveSettings();

        if (appItemReceiver != null) {
            getActivity().unregisterReceiver(appItemReceiver);
        }
    }

    private void saveSettings() {
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("offApps", TextUtils.join(",", offList));
        editor.putString("titleOnlyApps", TextUtils.join(",", titleList));

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(appItemReceiver, appItemFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Apps");

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        String offApps = settings.getString("offApps", "");
        String titleOnlyApps = settings.getString("titleOnlyApps", "");
        String[] offArr = offApps.split(",", -1);
        String[] titleApps = titleOnlyApps.split(",", -1);

        System.out.println(offApps);
        System.out.println(titleOnlyApps);

        offList = new HashSet<>(Arrays.asList(offArr));
        titleList = new HashSet<>(Arrays.asList(titleApps));

        List<AbstractItem> appItems = getAppList();
        Collections.sort(appItems);
        adapter = new FlexibleAdapter<>(appItems);
        adapter.addScrollableHeader(new ScrollableLayoutItem("headerItem", "Toggle off to disable notification mirroring.", "Toggle off to hide the content of mirrored notifications."));

        final FastScroller fastScroller = (FastScroller) view.findViewById(R.id.app_list_fs);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.app_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider());

        // Add fast scroller after rv is added to adapter.
        adapter.setFastScroller(fastScroller);
        adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                AbstractItem item = adapter.getItem(position);
                if (item instanceof AppItem) {
                    showAlert((AppItem) item);
                }
                return false;
            }
        });

        return view;
    }

    private List<AbstractItem> getAppList() {
        List<AbstractItem> items = new ArrayList<>();
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo info : packages) {
            if ((pm.getLaunchIntentForPackage(info.packageName) != null) || (info.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
                // Installed by user.
                Drawable icon = pm.getApplicationIcon(info);
                String name = (String) pm.getApplicationLabel(info);
                String packageName = info.packageName;

                boolean isOff = offList.contains(packageName);
                boolean isTitleOnly = titleList.contains(packageName);

                AppItem item = new AppItem(getActivity(), name, packageName, icon, isOff, isTitleOnly);
                items.add(item);
            } else {
                // System App.
                // if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
            }
        }
        return items;
    }

    private void showAlert(final AppItem item) {
        AlertDialog dialog;

        final CharSequence[] items = {"Turn off notifications", "Don't show content"};

        final boolean defOff = item.isOff();
        final boolean defTitle = item.isTitleOnly();

        boolean[] selectedItems = new boolean[2];
        selectedItems[0] = defOff;
        selectedItems[1] = defTitle;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(item.getId());
        builder.setMultiChoiceItems(items, selectedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (indexSelected == 0) {
                            item.setOff(isChecked);
                        } else {
                            item.setTitleOnly(isChecked);
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (item.isOff()) {
                            offList.add(item.getPackageName());
                        } else {
                            offList.remove(item.getPackageName());
                        }
                        if (item.isTitleOnly()) {
                            titleList.add(item.getPackageName());
                        } else {
                            titleList.remove(item.getPackageName());
                        }
                        adapter.notifyDataSetChanged();

                        View view = getView();
                        if (view != null) {
                            Snackbar.make(view, "Updated settings for " + item.getId(), Snackbar.LENGTH_SHORT).show();
                        }

                        saveSettings();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        item.setOff(defOff);
                        item.setTitleOnly(defTitle);
                    }
                });

        dialog = builder.create();
        dialog.show();
    }
}
