package com.waylonhuang.notifydesktop;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.history.HistoryItemHeader;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Waylon on 7/15/2017.
 */

public class NotificationItem extends AbstractFlexibleItem<NotificationItem.MyViewHolder> implements ISectionable<NotificationItem.MyViewHolder, HistoryItemHeader>, Parcelable {
    private long _id, time;
    private String appName, packageName, title, text;
    private Drawable icon;
    private Context context;

    private String timeStr;

    private HistoryItemHeader header;

    public NotificationItem(Context context, long _id, String appName, String packageName, String title, String text, long time) {
        this._id = _id;
        this.appName = appName;
        this.packageName = packageName;
        this.title = title;
        this.text = text;
        this.time = time;

        try {
            icon = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            icon = context.getResources().getDrawable(android.R.mipmap.sym_def_app_icon);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
        timeStr = dateFormat.format(new Date(time));
    }

    @Override
    public void setHeader(HistoryItemHeader header) {
        this.header = header;
    }

    @Override
    public HistoryItemHeader getHeader() {
        return header;
    }


    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public long get_id() {
        return _id;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof NotificationItem) {
            NotificationItem inItem = (NotificationItem) other;
            return this._id == inItem._id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(_id).hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.history_list_item;
    }

    @Override
    public MyViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new MyViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MyViewHolder holder, int position, List payloads) {
        holder.history_icon_tv.setImageDrawable(icon);
        holder.history_name_tv.setText(title);
        holder.history_detail_tv.setText(text);
        holder.history_time_tv.setText(timeStr);
    }

    public static class MyViewHolder extends FlexibleViewHolder {
        public ImageView history_icon_tv;
        public TextView history_name_tv;
        public TextView history_detail_tv;
        public TextView history_time_tv;

        public MyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            history_icon_tv = (ImageView) view.findViewById(R.id.history_icon_tv);
            history_name_tv = (TextView) view.findViewById(R.id.history_name_tv);
            history_detail_tv = (TextView) view.findViewById(R.id.history_detail_tv);
            history_time_tv = (TextView) view.findViewById(R.id.history_time_tv);
        }
    }

    public NotificationItem(Parcel parcel) {
        Bundle bundle = parcel.readBundle();
        this._id = bundle.getLong("_id");
        this.appName = bundle.getString("appName");
        this.packageName = bundle.getString("packageName");
        this.title = bundle.getString("title");
        this.text = bundle.getString("text");
        this.time = bundle.getLong("time");

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong("_id", _id);
        bundle.putString("appName", appName);
        bundle.putString("packageName", packageName);
        bundle.putString("title", title);
        bundle.putString("text", text);
        bundle.putLong("time", time);
        dest.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NotificationItem createFromParcel(Parcel in) {
            return new NotificationItem(in);
        }

        public NotificationItem[] newArray(int size) {
            return new NotificationItem[size];
        }
    };

}
