package com.waylonhuang.notifydesktop.applist;

import android.support.annotation.NonNull;

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Waylon on 7/23/2017.
 */

public abstract class AbstractItem<VH extends FlexibleViewHolder> extends AbstractFlexibleItem<VH> implements Comparable<AbstractItem<VH>> {
    public String id;

    public AbstractItem() {
    }

    @Override
    public int compareTo(@NonNull AbstractItem<VH> o) {
        return this.id.compareTo(o.id);
    }
}