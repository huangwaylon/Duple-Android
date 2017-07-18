package com.waylonhuang.notifydesktop.history;

import android.view.View;
import android.widget.TextView;

import com.waylonhuang.notifydesktop.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractHeaderItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by Waylon on 5/8/2017.
 */

public class HistoryItemHeader extends AbstractHeaderItem<HistoryItemHeader.HeaderViewHolder> implements IFilterable {
    private String title;

    public HistoryItemHeader(String title) {
        super();
        this.title = title;
        setEnabled(false);
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof HistoryItemHeader) {
            HistoryItemHeader inItem = (HistoryItemHeader) inObject;
            return this.title.equals(inItem.title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.history_item_header;
    }

    @Override
    public HeaderViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        return new HeaderViewHolder(view, adapter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bindViewHolder(FlexibleAdapter adapter, HeaderViewHolder holder, int position, List payloads) {
        holder.mTitle.setText(getTitle());
    }

    @Override
    public boolean filter(String constraint) {
        return getTitle() != null && getTitle().toLowerCase().trim().contains(constraint);
    }

    public static class HeaderViewHolder extends FlexibleViewHolder {
        TextView mTitle;

        HeaderViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter, true); //True for sticky.
            mTitle = (TextView) view.findViewById(R.id.kanji_detail_header_title);
        }
    }

    @Override
    public String toString() {
        return "HeaderItem[title=" + title + "]";
    }
}