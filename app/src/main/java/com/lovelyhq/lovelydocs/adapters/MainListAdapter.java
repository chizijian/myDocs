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
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.models.DocsetVersion;
import de.greenrobot.event.EventBus;
import java.util.Collections;
import java.util.List;

public class MainListAdapter extends BaseAdapter {
    private static final int ROTATE_DURATION = 10000;
    private static final float ROTATE_FROM = 0.0f;
    private static final float ROTATE_TO = 3600.0f;
    private Context mContext;
    private List<DocsetVersion> mDocsetVersions = Collections.emptyList();

    private RotateAnimation rotation;

    public class StartUpdateEvent {
        private int position;

        StartUpdateEvent(int position) {
            this.position = position;
        }

        public int getPosition() {
            return this.position;
        }

    }

    class ViewHolder {
        @BindView(R.id.imgIcon)
        ImageView docsetIcon;

        @BindView(R.id.tvTitle)
        TextView docsetNameTv;

        @BindView(R.id.btnUpdate)
        ImageButton docsetUpdateBtn;

        @BindView(R.id.tvSubtitle)
        TextView docsetVersionTv;

        @BindView(R.id.itemLayout)
        LinearLayout itemLayout;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MainListAdapter(Context context) {
        this.mContext = context;

        this.rotation = new RotateAnimation(ROTATE_FROM, ROTATE_TO, 1, 0.5f, 1, 0.5f);
        this.rotation.setRepeatCount(-1);
        this.rotation.setInterpolator(new LinearInterpolator());
        this.rotation.setDuration(ROTATE_DURATION);
    }

    public void update(List<DocsetVersion> docsetVersions) {
        this.mDocsetVersions = docsetVersions;
        notifyDataSetChanged();
    }

    public boolean isEnabled(int position) {
        return ((DocsetVersion) getItem(position)).getStatus() == 1;
    }

    public int getCount() {
        return this.mDocsetVersions.size();
    }

    public Object getItem(int position) {
        return this.mDocsetVersions.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        DocsetVersion docsetVersion = (DocsetVersion) getItem(position);
        if (docsetVersion.getDocset().getIcon() != 0) {
            holder.docsetIcon.setVisibility(View.VISIBLE);
            holder.docsetIcon.setImageDrawable(ContextCompat.getDrawable(mContext,docsetVersion.getDocset().getIcon()));
        } else {
            holder.docsetIcon.setVisibility(View.GONE);
        }
        holder.docsetNameTv.setText(docsetVersion.getDocset().getName());
        holder.docsetVersionTv.setText(docsetVersion.getVersion());
        if (docsetVersion.isUpdateExists() || docsetVersion.getStatus() == 0) {
            holder.docsetUpdateBtn.setVisibility(View.VISIBLE);
        } else {
            holder.docsetUpdateBtn.setVisibility(View.INVISIBLE);
        }
        if (docsetVersion.getStatus() == 0) {
            holder.docsetUpdateBtn.setEnabled(false);
            holder.docsetUpdateBtn.startAnimation(this.rotation);
            holder.itemLayout.setEnabled(false);
        } else {
            holder.docsetUpdateBtn.setEnabled(true);
            holder.docsetUpdateBtn.setAnimation(null);
            holder.itemLayout.setEnabled(true);
        }
        if (docsetVersion.hasTarix()) {
            holder.itemLayout.setEnabled(true);
        } else {
            holder.itemLayout.setEnabled(false);
        }
        holder.docsetUpdateBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EventBus.getDefault().post(new StartUpdateEvent(position));
            }
        });
        return view;
    }
}
