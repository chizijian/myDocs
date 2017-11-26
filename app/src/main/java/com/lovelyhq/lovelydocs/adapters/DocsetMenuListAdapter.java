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
import com.lovelyhq.lovelydocs.models.MenuItem;

import java.util.Collections;
import java.util.List;

public class DocsetMenuListAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mIndexSelected;
    private List<MenuItem> mMenuItems = Collections.emptyList();

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

    public DocsetMenuListAdapter(Context context) {
        this.mContext = context;
    }

    public void update(List<MenuItem> menuItems, boolean isIndexSelected) {
        this.mMenuItems = menuItems;
        this.mIndexSelected = isIndexSelected;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mMenuItems.size();
    }

    public Object getItem(int position) {
        return mMenuItems.get(position);
    }

    public long getItemId(int position) {
        return position;
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

        MenuItem item = (MenuItem) getItem(position);
        if (item.getIcon() != 0) {
            holder.menuItemIconImg.setImageDrawable(ContextCompat.getDrawable(mContext,item.getIcon()));
        }
        holder.menuItemTitleTv.setText(item.getTitle());
        if (position == 0 && this.mIndexSelected) {
            holder.itemLayout.setBackgroundColor(ContextCompat.getColor(mContext,R.color.orange));
            holder.menuItemTitleTv.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        } else {
            holder.itemLayout.setBackgroundResource(R.drawable.selector_list_item);
            holder.menuItemTitleTv.setTextColor(ContextCompat.getColorStateList(mContext,R.color.list_item_text));
        }
        return view;
    }
}
