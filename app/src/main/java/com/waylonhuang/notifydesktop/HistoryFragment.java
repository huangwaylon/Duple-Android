package com.waylonhuang.notifydesktop;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration;

public class HistoryFragment extends Fragment {
    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("History");

        final NotificationSQLiteHelper helper = NotificationSQLiteHelper.getInstance(getActivity());

        List<NotificationItem> appItems = organizeItems(helper);
        final FlexibleAdapter<NotificationItem> adapter = new FlexibleAdapter<>(appItems);
        final FastScroller fastScroller = (FastScroller) view.findViewById(R.id.his_list_fs);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.his_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new FlexibleItemDecoration(getContext()).withDefaultDivider());

        // Add fast scroller after rv is added to adapter.
        adapter.setFastScroller(fastScroller);
        adapter.setDisplayHeadersAtStartUp(true).setStickyHeaders(true);

        adapter.addListener(new FlexibleAdapter.OnItemClickListener() {
            @Override
            public boolean onItemClick(int position) {
                return false;
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.history_sfl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.updateDataSet(organizeItems(helper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }


    private List<NotificationItem> organizeItems(NotificationSQLiteHelper helper) {
        List<NotificationItem> appItems = helper.getAllItems(getActivity());

        long curr = 0;
        HistoryItemHeader header = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

        for (NotificationItem item : appItems) {
            int day = 1000 * 60 * 24;
            long droppedMillis = day * (item.getTime() / day);

            if (header == null || curr != droppedMillis) {
                curr = droppedMillis;
                String timeStr = dateFormat.format(new Date(curr));
                header = new HistoryItemHeader(timeStr);
            }
            item.setHeader(header);
        }
        return appItems;
    }
}
