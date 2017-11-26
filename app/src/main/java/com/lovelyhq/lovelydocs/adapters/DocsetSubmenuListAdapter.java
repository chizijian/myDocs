package com.lovelyhq.lovelydocs.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.lovelyhq.lovelydocs.models.TypeItem;
import java.util.Collections;
import java.util.List;

public class DocsetSubmenuListAdapter extends BaseAdapter {
    private Context mContext;
    private int mIcon;
    private List<TypeItem> mItems = Collections.emptyList();
    private int mSelectedPosition = -1;

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

    public DocsetSubmenuListAdapter(Context context, int icon) {
        this.mContext = context;
        this.mIcon = icon;
    }

    public void update(List<TypeItem> typeItems) {
        this.mItems = typeItems;
        notifyDataSetChanged();
    }

    public void update(int selectedPosition) {
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
        holder.menuItemTitleTv.setText(((TypeItem) getItem(position)).getName());
        if (this.mIcon > 0) {
            holder.menuItemIconImg.setImageDrawable(ContextCompat.getDrawable(mContext,mIcon));
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
}
