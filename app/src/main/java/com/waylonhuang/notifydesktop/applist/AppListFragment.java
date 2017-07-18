package com.waylonhuang.notifydesktop.applist;

import android.content.DialogInterface;
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

public class AppListFragment extends Fragment {
    private Set<String> offList;
    private Set<String> titleList;

    private List<AppItem> appItems;

    private FlexibleAdapter<AppItem> adapter;

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
        if (getArguments() != null) {
            // mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("offApps", TextUtils.join(",", offList));
        editor.putString("titleOnlyApps", TextUtils.join(",", titleList));

        editor.apply();
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

        appItems = getAppList();
        Collections.sort(appItems);
        adapter = new FlexibleAdapter<>(appItems);
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
                AppItem item = adapter.getItem(position);
                showAlert(item);
                return false;
            }
        });

        return view;
    }

    private List<AppItem> getAppList() {
        List<AppItem> items = new ArrayList<>();
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo info : packages) {
            if (pm.getLaunchIntentForPackage(info.packageName) != null) {
                // Installed by user.
                Drawable icon = pm.getApplicationIcon(info);
                String name = (String) pm.getApplicationLabel(info);
                String packageName = info.packageName;

                boolean isOff = offList.contains(packageName);
                boolean isTitleOnly = titleList.contains(packageName);

                AppItem item = new AppItem(name, packageName, icon, isOff, isTitleOnly);
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

        final CharSequence[] items = {"Turn off notifications", "Show title only"};

        final boolean defOff = item.isOff();
        final boolean defTitle = item.isTitleOnly();

        boolean[] selectedItems = new boolean[2];
        selectedItems[0] = defOff;
        selectedItems[1] = defTitle;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(item.getAppName());
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
                            Snackbar.make(view, "Updated settings for " + item.getAppName(), Snackbar.LENGTH_LONG).show();
                        }
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