package com.lovelyhq.lovelydocs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.helpers.FileHelper;

import java.util.List;

public class VersionsSpinnerAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private String mDocsetName;
    private List<String> mVersions;

    public VersionsSpinnerAdapter(Context context, int tvResId, List<String> versions, String docsetName) {
        super(context, tvResId, versions);
        this.mContext = context;
        this.mVersions = versions;
        this.mDocsetName = docsetName;
    }

    public boolean isEnabled(int position) {
        boolean latest;
        if (position == 0) {
            latest = true;
        } else {
            latest = false;
        }
        return !FileHelper.isDocsetVersionSaved(this.mDocsetName, (String) this.mVersions.get(position), latest, this.mContext);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(this.mContext).inflate(R.layout.verions_spinner_dropdown_item, parent, false);
        convertView.setEnabled(isEnabled(position));
        return super.getDropDownView(position, convertView, parent);
    }

    public int getFirstEnabledPosition() {
        for (int i = 0; i < this.mVersions.size(); i++) {
            if (isEnabled(i)) {
                return i;
            }
        }
        return -1;
    }
}
