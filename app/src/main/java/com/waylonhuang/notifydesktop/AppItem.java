package com.waylonhuang.notifydesktop;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Waylon on 7/15/2017.
 */

public class AppItem extends AbstractFlexibleItem<AppItem.MyViewHolder> implements Comparable<AppItem> {

    private Drawable icon;
    private String appName;
    private String packageName;

    private boolean isOff, isTitleOnly;

    public AppItem(String appName, String packageName, Drawable icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int compareTo(@NonNull AppItem o) {
        return this.appName.compareTo(o.appName);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof AppItem) {
            AppItem inItem = (AppItem) other;
            return this.packageName.equals(inItem.packageName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return packageName.hashCode();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.app_list_item;
    }

    @Override
    public MyViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new MyViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MyViewHolder holder, int position, List payloads) {
        holder.app_icon_iv.setImageDrawable(icon);
        holder.app_name_tv.setText(appName);
        holder.app_detail_tv.setText(packageName);

    }

    public static class MyViewHolder extends FlexibleViewHolder {
        public ImageView app_icon_iv;
        public TextView app_name_tv;
        public TextView app_detail_tv;

        public MyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            app_icon_iv = (ImageView) view.findViewById(R.id.app_icon_iv);
            app_name_tv = (TextView) view.findViewById(R.id.app_name_tv);
            app_detail_tv = (TextView) view.findViewById(R.id.app_detail_tv);
        }
    }

}
