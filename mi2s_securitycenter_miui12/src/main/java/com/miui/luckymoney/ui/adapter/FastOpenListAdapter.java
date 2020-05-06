package com.miui.luckymoney.ui.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import b.b.c.j.x;
import com.miui.common.expandableview.a;
import com.miui.luckymoney.config.FastOpenConfig;
import com.miui.luckymoney.model.FastOpenAppInfo;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.widget.SlidingButton;

public class FastOpenListAdapter extends a {
    private boolean enabled = false;
    private Context mContext;
    private FastOpenConfig mFastOpenConfig;
    private List<FastOpenAppInfo> mHeaders = new ArrayList();
    private LayoutInflater mInflater;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    private static class HeaderViewHolder {
        /* access modifiers changed from: private */
        public TextView title;

        private HeaderViewHolder() {
        }
    }

    public static class ViewHolder {
        public ImageView icon;
        public SlidingButton slidingButton;
        public TextView title;
    }

    public FastOpenListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mFastOpenConfig = FastOpenConfig.getInstance(context);
    }

    public int getCountForSection(int i) {
        return this.mHeaders.get(i).getPackageInfos().size();
    }

    public List<FastOpenAppInfo> getData() {
        return this.mHeaders;
    }

    public Object getItem(int i, int i2) {
        return Integer.valueOf(i2);
    }

    public long getItemId(int i, int i2) {
        return (long) i2;
    }

    public View getItemView(int i, int i2, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        int i3;
        Resources resources;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.luckymoney_fast_open_list_item_view, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.slidingButton = view.findViewById(R.id.sliding_button);
            viewHolder.slidingButton.setOnCheckedChangeListener(this.mOnCheckedChangeListener);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        PackageInfo packageInfo = this.mHeaders.get(i).getPackageInfos().get(i2);
        r.a("pkg_icon://".concat(packageInfo.packageName), viewHolder.icon, r.f);
        viewHolder.title.setText(x.a(this.mContext, packageInfo.applicationInfo));
        viewHolder.slidingButton.setTag(packageInfo);
        viewHolder.slidingButton.setChecked(!this.mFastOpenConfig.contains(packageInfo.packageName));
        viewHolder.slidingButton.setEnabled(this.enabled);
        TextView textView = viewHolder.title;
        if (this.enabled) {
            resources = this.mContext.getResources();
            i3 = R.color.lucky_settings_item_title_color;
        } else {
            resources = this.mContext.getResources();
            i3 = R.color.lucky_settings_item_summary_color;
        }
        textView.setTextColor(resources.getColor(i3));
        return view;
    }

    public int getSectionCount() {
        return this.mHeaders.size();
    }

    public View getSectionHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder headerViewHolder;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.luckymoney_fast_open_list_header_view, (ViewGroup) null);
            headerViewHolder = new HeaderViewHolder();
            TextView unused = headerViewHolder.title = (TextView) view.findViewById(R.id.header_title);
            view.setTag(headerViewHolder);
        } else {
            headerViewHolder = (HeaderViewHolder) view.getTag();
        }
        headerViewHolder.title.setText(this.mHeaders.get(i).getTitle());
        headerViewHolder.title.setTag(Integer.valueOf(i));
        view.setClickable(false);
        view.setLongClickable(false);
        return view;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
        notifyDataSetChanged();
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void updateData(List<FastOpenAppInfo> list) {
        this.mHeaders.clear();
        this.mHeaders.addAll(list);
        notifyDataSetChanged();
    }

    public void updateHeader(View view) {
        Object tag = view.getTag();
        if (tag instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) tag;
            headerViewHolder.title.setText(this.mHeaders.get(((Integer) headerViewHolder.title.getTag()).intValue()).getTitle());
        }
    }
}
