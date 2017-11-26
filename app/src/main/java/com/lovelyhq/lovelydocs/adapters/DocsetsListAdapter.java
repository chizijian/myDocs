package com.lovelyhq.lovelydocs.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.activities.DocsetsActivity;
import com.lovelyhq.lovelydocs.dialogs.AddDocsetDialog;
import com.lovelyhq.lovelydocs.dialogs.WarningDialog;
import com.lovelyhq.lovelydocs.events.StartDialogEvent;
import com.lovelyhq.lovelydocs.models.Docset;


import cn.bmob.v3.BmobUser;
import de.greenrobot.event.EventBus;
import docs_app.bean.User;

import java.util.List;

public class DocsetsListAdapter extends BaseAdapter {
    private static final int ROTATE_DURATION = 10000;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 3600.0f;
    private boolean mAllItemsEnabled = true;

    private Context mContext;
    private List<Docset> mDocsets;

    private RotateAnimation rotation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, 1, 0.5f, 1, 0.5f);

    private class StatusButtonListener implements OnClickListener {
        private int position;

        StatusButtonListener(int position) {
            this.position = position;
        }

        public void onClick(View view) {
            if (BmobUser.getCurrentUser(User.class) != null)
                EventBus.getDefault().post(new StartDialogEvent(AddDocsetDialog.newInstance((Docset) DocsetsListAdapter.this.getItem(this.position))));
            else {
                EventBus.getDefault().post(new StartDialogEvent(WarningDialog.newInstance()));
                Toast.makeText(DocsetsActivity.docsetsActivity,"请登陆",Toast.LENGTH_LONG);
            }
        }
    }

    class ViewHolder {
        @BindView(R.id.imgIcon)
        ImageView docsetIconImg;

        @BindView(R.id.tvTitle)
        TextView docsetNameTv;

        @BindView(R.id.btnStatus)
        ImageButton docsetStatusBtn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public DocsetsListAdapter(List<Docset> docsets, Context context) {
        this.mDocsets = docsets;
        this.mContext = context;
        this.rotation.setRepeatCount(-1);
        this.rotation.setInterpolator(new LinearInterpolator());
        this.rotation.setDuration(ROTATE_DURATION);
    }

    public void setAllItemsEnabled(boolean isConnected) {
        this.mAllItemsEnabled = isConnected;
        notifyDataSetChanged();
    }

    public void update(List<Docset> docsets) {
        this.mDocsets = docsets;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mDocsets.size();
    }

    public Object getItem(int position) {
        return this.mDocsets.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        int statusButtonImage;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item_alt, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Docset docset = (Docset) getItem(position);
        if (docset.getIcon() != 0) {
            holder.docsetIconImg.setVisibility(View.VISIBLE);
            holder.docsetIconImg.setImageDrawable(ContextCompat.getDrawable(mContext, docset.getIcon()));
        } else {
            holder.docsetIconImg.setVisibility(View.GONE);
        }
        holder.docsetNameTv.setText(docset.getName());
        boolean isRotating = false;
        if (docset.getStatus() == 0) {
            statusButtonImage = R.drawable.icon_download;
        } else if (docset.getStatus() == 1) {
            statusButtonImage = R.drawable.icon_loading;
            isRotating = true;
        } else {
            statusButtonImage = R.drawable.icon_heart;
        }
        holder.docsetStatusBtn.setBackgroundResource(statusButtonImage);
        holder.docsetStatusBtn.setOnClickListener(new StatusButtonListener(position));
        holder.docsetStatusBtn.setEnabled(this.mAllItemsEnabled);
        if (isRotating) {
            holder.docsetStatusBtn.startAnimation(this.rotation);
        } else {
            holder.docsetStatusBtn.setAnimation(null);
        }
        return view;
    }
}
