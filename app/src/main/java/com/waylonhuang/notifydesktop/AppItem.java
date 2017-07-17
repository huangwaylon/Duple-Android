package com.waylonhuang.notifydesktop;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
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

    public AppItem(String appName, String packageName, Drawable icon, boolean isOff, boolean isTitleOnly) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;

        this.isOff = isOff;
        this.isTitleOnly = isTitleOnly;
    }

    public boolean isOff() {
        return isOff;
    }

    public boolean isTitleOnly() {
        return isTitleOnly;
    }

    public void setOff(boolean off) {
        isOff = off;
    }

    public void setTitleOnly(boolean titleOnly) {
        isTitleOnly = titleOnly;
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

        if (isOff && isTitleOnly) {
            holder.app_off.setVisibility(View.VISIBLE);
            holder.app_title_only.setVisibility(View.VISIBLE);

            holder.app_name_tv.setPadding(0, 0, 60, 0);
            setMargins(holder.app_title_only, 0, 0, 60, 0);
        } else if (isOff) {
            holder.app_off.setVisibility(View.VISIBLE);
            holder.app_title_only.setVisibility(View.GONE);

            holder.app_name_tv.setPadding(0, 0, 40, 0);
            setMargins(holder.app_title_only, 0, 0, 60, 0);
        } else if (isTitleOnly) {
            holder.app_off.setVisibility(View.GONE);
            holder.app_title_only.setVisibility(View.VISIBLE);

            holder.app_name_tv.setPadding(0, 0, 30, 0);
            setMargins(holder.app_title_only, 0, 0, 0, 0);
        } else {
            holder.app_off.setVisibility(View.GONE);
            holder.app_title_only.setVisibility(View.GONE);

            holder.app_name_tv.setPadding(0, 0, 0, 0);
            setMargins(holder.app_title_only, 0, 0, 60, 0);
        }
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public static class MyViewHolder extends FlexibleViewHolder {
        public ImageView app_icon_iv;
        public TextView app_name_tv;
        public TextView app_detail_tv;

        public TextView app_off;
        public TextView app_title_only;

        public MyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            app_icon_iv = (ImageView) view.findViewById(R.id.app_icon_iv);
            app_name_tv = (TextView) view.findViewById(R.id.app_name_tv);
            app_detail_tv = (TextView) view.findViewById(R.id.app_detail_tv);

            app_off = (TextView) view.findViewById(R.id.app_off);
            app_title_only = (TextView) view.findViewById(R.id.app_title_only);
        }
    }
}
