package com.waylonhuang.notifydesktop.applist;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.R;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.utils.DrawableUtils;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Waylon on 7/23/2017.
 */

public class ScrollableLayoutItem extends AbstractItem<ScrollableLayoutItem.LayoutViewHolder> {
    private String id;
    private String title1, title2;

    public ScrollableLayoutItem(String id, String title1, String title2) {
        this.id = id;
        this.title1 = title1;
        this.title2 = title2;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ScrollableLayoutItem) {
            ScrollableLayoutItem item = (ScrollableLayoutItem) o;
            return id.equals(item.id);
        }
        return false;
    }

    @Override
    public int getSpanSize(int spanCount, int position) {
        return spanCount;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recycler_scrollable_layout_item;
    }

    @Override
    public LayoutViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new LayoutViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter adapter, LayoutViewHolder holder, int position, List payloads) {
        holder.mTitle.setText(title1);
        holder.mSubtitle.setText(title2);
    }

    public static class LayoutViewHolder extends FlexibleViewHolder {

        public TextView mTitle;
        public TextView mSubtitle;

        public LayoutViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true);
            mTitle = (TextView) view.findViewById(R.id.title);
            mSubtitle = (TextView) view.findViewById(R.id.subtitle);

            // Support for StaggeredGridLayoutManager
            setFullSpan(true);
        }

        @Override
        public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
            AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.getRecyclerView());
        }
    }

    @Override
    public String toString() {
        return "ScrollableLayoutItem[" + super.toString() + "]";
    }
}