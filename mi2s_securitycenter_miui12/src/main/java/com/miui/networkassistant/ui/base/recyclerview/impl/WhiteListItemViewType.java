package com.miui.networkassistant.ui.base.recyclerview.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import b.b.c.j.r;
import com.miui.networkassistant.model.WhiteListItem;
import com.miui.networkassistant.ui.base.recyclerview.ItemViewType;
import com.miui.networkassistant.ui.base.recyclerview.MultiTypeAdapter;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class WhiteListItemViewType implements ItemViewType<WhiteListItem> {
    private Context mContext;
    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    public static class ViewHolder extends MultiTypeAdapter.ViewHolder {
        ImageView appIcon;
        SlidingButton slidingButton;
        TextView summary;
        TextView title;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.title);
            this.summary = (TextView) view.findViewById(R.id.summary);
            this.appIcon = (ImageView) view.findViewById(R.id.icon);
            this.slidingButton = view.findViewById(R.id.sliding_button);
        }
    }

    public WhiteListItemViewType(Context context, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mContext = context;
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public boolean checkType(WhiteListItem whiteListItem, int i) {
        return true;
    }

    public void onBindViewHolder(MultiTypeAdapter.ViewHolder viewHolder, WhiteListItem whiteListItem, int i) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        r.a("pkg_icon://".concat(whiteListItem.getPkgName()), viewHolder2.appIcon, r.f, this.mContext.getResources().getDrawable(R.drawable.gb_def_icon));
        viewHolder2.slidingButton.setOnPerformCheckedChangeListener(this.mOnCheckedChangeListener);
        viewHolder2.slidingButton.setChecked(whiteListItem.isEnabled());
        viewHolder2.slidingButton.setTag(Integer.valueOf(i));
        viewHolder2.title.setText(whiteListItem.getAppLabel());
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.listitem_white_app_view, viewGroup, false));
    }
}
