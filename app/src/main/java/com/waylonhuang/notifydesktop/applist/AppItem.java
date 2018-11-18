package com.waylonhuang.notifydesktop.applist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Waylon on 7/15/2017.
 */

public class AppItem extends AbstractItem<AppItem.MyViewHolder> {
    public static final int TITLE_ONLY_INTENT = 0;
    public static final int NOTIFY_OFF_INTENT = 1;

    public static final String APP_ITEM_INTENT = "APP_ITEM_INTENT";

    private Drawable icon;
    private String packageName;

    private boolean isOff, isTitleOnly;
    private Context context;

    public AppItem(Context context, String appName, String packageName, Drawable icon, boolean isOff, boolean isTitleOnly) {
        this.context = context;

        this.id = appName;
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

    public String getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
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
    public MyViewHolder createViewHolder(final View view, FlexibleAdapter adapter) {
        return new MyViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, MyViewHolder holder, int position, List payloads) {
        holder.app_icon_iv.setImageDrawable(icon);
        holder.app_name_tv.setText(id);
        holder.app_detail_tv.setText(packageName);

        if (isTitleOnly()) {
            holder.titleButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_visibility_off_black_24dp));
            holder.titleButton.setColorFilter(ContextCompat.getColor(context, R.color.icon_disable_color));
        } else {
            holder.titleButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_visibility_black_24dp));
            holder.titleButton.setColorFilter(ContextCompat.getColor(context, R.color.icon_enable_color));
        }

        if (isOff()) {
            holder.notificationButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp));
            holder.notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.icon_disable_color));
        } else {
            holder.notificationButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_notifications_black_24dp));
            holder.notificationButton.setColorFilter(ContextCompat.getColor(context, R.color.icon_enable_color));
        }

        holder.titleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitleOnly(!isTitleOnly);
                sendIntent(TITLE_ONLY_INTENT, isTitleOnly);
            }
        });

        holder.notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOff(!isOff);
                sendIntent(NOTIFY_OFF_INTENT, isOff);
            }
        });
    }

    private void sendIntent(int intentType, boolean state) {
        Intent intent = new Intent(APP_ITEM_INTENT);
        intent.putExtra("package", packageName);
        intent.putExtra("type", intentType);
        intent.putExtra("off", state);
        intent.putExtra("app", id);
        context.sendBroadcast(intent);
    }


    public static class MyViewHolder extends FlexibleViewHolder {
        public ImageView app_icon_iv;
        public TextView app_name_tv;
        public TextView app_detail_tv;

        public ImageButton titleButton;
        public ImageButton notificationButton;

        public MyViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            app_icon_iv = (ImageView) view.findViewById(R.id.app_icon_iv);
            app_name_tv = (TextView) view.findViewById(R.id.app_name_tv);
            app_detail_tv = (TextView) view.findViewById(R.id.app_detail_tv);

            titleButton = (ImageButton) view.findViewById(R.id.visible_toggle);
            notificationButton = (ImageButton) view.findViewById(R.id.notification_toggle);
        }
    }
}
