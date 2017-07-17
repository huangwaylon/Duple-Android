package com.waylonhuang.notifydesktop;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;

public class AppListFragment extends Fragment {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Apps");

        List<AppItem> appItems = getAppList();
        Collections.sort(appItems);
        FlexibleAdapter<AppItem> adapter = new FlexibleAdapter<>(appItems);
        final FastScroller fastScroller = (FastScroller) view.findViewById(R.id.app_list_fs);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.app_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Add fast scroller after rv is added to adapter.
        adapter.setFastScroller(fastScroller);

        adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
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
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System application.
            } else {
                // Installed by user.
                Drawable icon = pm.getApplicationIcon(info);
                String name = (String) pm.getApplicationLabel(info);
                String packageName = info.packageName;

                AppItem item = new AppItem(name, packageName, icon);
                items.add(item);
            }
        }
        return items;
    }
}
