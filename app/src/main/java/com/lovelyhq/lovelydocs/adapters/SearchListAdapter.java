package com.lovelyhq.lovelydocs.adapters;

import android.content.Context;
import android.content.res.Resources;

import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.models.CondensedType;
import com.lovelyhq.lovelydocs.models.Type;
import com.lovelyhq.lovelydocs.models.TypeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchListAdapter extends BaseAdapter {
    private ArrayList mCondensedTypes = (ArrayList) Collections.emptyList();
    private Context mContext;
    private ArrayList mItems = (ArrayList) Collections.emptyList();

    private String mSearchQuery;
    private int mSelectedPosition;

    class ViewHolder {
        @BindView(R.id.itemLayout)
        LinearLayout itemLayout;
        @BindView(R.id.imgIcon)
        ImageView menuItemIconImg;
        @BindView(R.id.tvTitle)
        TextView menuItemTitleTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public SearchListAdapter(Context context) {
        this.mContext = context;

        this.mSearchQuery = "";
        this.mItems = new ArrayList();
        this.mCondensedTypes = new ArrayList();
        this.mSelectedPosition = -1;
    }

    public void setData(List<TypeItem> items, String searchQuery) {
        this.mCondensedTypes.clear();
        for (TypeItem item : items) {
            mCondensedTypes.add(new CondensedType(new Type(this.mContext, item.getType()).getAlias()));
        }
        this.mSearchQuery = searchQuery;
        this.mItems = (ArrayList) items;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int selectedPosition) {
        this.mSelectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mItems.size();
    }

    public Object getItem(int position) {
        return this.mItems.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String text = ((TypeItem) getItem(position)).getName();
        if (text.equals("")) {
            holder.menuItemTitleTv.setText(text);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {  //API 24以上
                holder.menuItemTitleTv.setText(Html.fromHtml(setTextHighlighting(text), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.menuItemTitleTv.setText(Html.fromHtml(setTextHighlighting(text)));
            }

        }
        if (((CondensedType) this.mCondensedTypes.get(position)).getIcon() != 0) {
            holder.menuItemIconImg.setImageResource(((CondensedType) this.mCondensedTypes.get(position)).getIcon());
        }
        if (position == this.mSelectedPosition) {
            holder.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.orange));
            holder.menuItemTitleTv.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        } else {
            holder.itemLayout.setBackgroundResource(R.drawable.selector_list_item);
            holder.menuItemTitleTv.setTextColor(ContextCompat.getColorStateList(mContext,R.color.list_item_text));
        }
        return view;
    }

    private String setTextHighlighting(String text) {
        StringBuilder sb = new StringBuilder();
        this.mSearchQuery = searchQueryToRegex();
        if (text.toLowerCase().contains(this.mSearchQuery.toLowerCase())) {
            int indexToSwap = text.toLowerCase().indexOf(this.mSearchQuery.toLowerCase());
            sb.append(text.substring(0, indexToSwap)).append("<font color='red'>").append(text.substring(indexToSwap, this.mSearchQuery.length() + indexToSwap)).append("</font>").append(text.substring(this.mSearchQuery.length() + indexToSwap, text.length()));
            return sb.toString();
        }
        sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String currentLetter = text.substring(i, i + 1);
            if (this.mSearchQuery.toLowerCase().contains(currentLetter.toLowerCase())) {
                sb.append("<font color='red'>").append(currentLetter).append("</font>");
            } else {
                sb.append(currentLetter);
            }
        }
        return sb.toString();
    }

    private String searchQueryToRegex() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSearchQuery.length(); i++) {
            String currentLetter = this.mSearchQuery.substring(i, i + 1);
            if (currentLetter.equals("(") || currentLetter.equals(")") || currentLetter.equals("*") || currentLetter.equals("+") || currentLetter.equals("?") || currentLetter.equals("+") || currentLetter.equals("^") || currentLetter.equals("$") || currentLetter.equals("|") || currentLetter.equals("[") || currentLetter.equals("]") || currentLetter.equals("{")) {
                sb.append("\\").append(currentLetter);
            } else {
                sb.append(currentLetter);
            }
        }
        return sb.toString();
    }
}
