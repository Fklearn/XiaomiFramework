package com.miui.networkassistant.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MIServiceAppDetailListAdapter extends RecyclerView.a<ViewHolder> {
    private List<MiAppInfo> mAppInfoList;
    private Comparator<MiAppInfo> mComparatorByTraffic = new Comparator<MiAppInfo>() {
        public int compare(MiAppInfo miAppInfo, MiAppInfo miAppInfo2) {
            int i = ((miAppInfo2.totalTraffic - miAppInfo.totalTraffic) > 0 ? 1 : ((miAppInfo2.totalTraffic - miAppInfo.totalTraffic) == 0 ? 0 : -1));
            if (i > 0) {
                return 1;
            }
            return i == 0 ? 0 : -1;
        }
    };
    private Context mContext;
    long mMobileMaxTraffic = 0;
    private int mTrafficMarkMaxWidth;
    private int mTrafficMarkMinWidth;
    private int mTrafficType = 0;

    public static class MiAppInfo {
        public CharSequence packageName;
        public long totalTraffic = 0;

        public void addTraffic(long j) {
            this.totalTraffic += j;
        }
    }

    public static class ViewHolder extends RecyclerView.u {
        TextView appName;
        ImageView icon;
        ImageView imageView;
        TextView traffic;
        TextView trafficValues;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.imageview_icon);
            this.appName = (TextView) view.findViewById(R.id.textview_appname);
            this.traffic = (TextView) view.findViewById(R.id.text_traffic);
            this.imageView = (ImageView) view.findViewById(R.id.image_traffic);
            this.trafficValues = (TextView) view.findViewById(R.id.text_traffic_values);
        }
    }

    public MIServiceAppDetailListAdapter(Activity activity, List<MiAppInfo> list) {
        this.mContext = activity;
        this.mAppInfoList = list;
        Resources resources = activity.getResources();
        this.mTrafficMarkMinWidth = resources.getDimensionPixelSize(R.dimen.traffic_mark_min_width);
        this.mTrafficMarkMaxWidth = resources.getDimensionPixelSize(R.dimen.traffic_mark_max_width);
    }

    private void buildMaxTraffic() {
        this.mMobileMaxTraffic = 0;
        List<MiAppInfo> list = this.mAppInfoList;
        if (list != null) {
            for (MiAppInfo miAppInfo : list) {
                long j = this.mMobileMaxTraffic;
                long j2 = miAppInfo.totalTraffic;
                if (j < j2) {
                    this.mMobileMaxTraffic = j2;
                }
            }
        }
    }

    private int getTrafficImgWidth(long j, long j2, int i) {
        if (j >= j2) {
            return this.mTrafficMarkMaxWidth;
        }
        int i2 = (int) (((((double) j) * 1.0d) * ((double) this.mTrafficMarkMaxWidth)) / ((double) j2));
        return i2 < i ? i : i2;
    }

    public List<MiAppInfo> getData() {
        return this.mAppInfoList;
    }

    public int getItemCount() {
        List<MiAppInfo> list = this.mAppInfoList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        long j;
        int i2;
        ImageView imageView;
        if (viewHolder != null && this.mAppInfoList != null && i < getData().size()) {
            MiAppInfo miAppInfo = this.mAppInfoList.get(i);
            IconCacheHelper.getInstance().setIconToImageView(viewHolder.icon, miAppInfo.packageName.toString());
            viewHolder.appName.setText(LabelLoadHelper.loadLabel(this.mContext, miAppInfo.packageName));
            int i3 = this.mTrafficType;
            if (i3 == 0) {
                j = miAppInfo.totalTraffic;
                if (j > 0) {
                    viewHolder.imageView.setVisibility(0);
                    imageView = viewHolder.imageView;
                    i2 = R.drawable.traffic_mobile;
                }
                viewHolder.imageView.setVisibility(8);
                viewHolder.trafficValues.setText(R.string.traffic_none);
                return;
            } else if (1 == i3) {
                j = miAppInfo.totalTraffic;
                if (j > 0) {
                    viewHolder.imageView.setVisibility(0);
                    imageView = viewHolder.imageView;
                    i2 = R.drawable.traffic_wifi;
                }
                viewHolder.imageView.setVisibility(8);
                viewHolder.trafficValues.setText(R.string.traffic_none);
                return;
            } else {
                return;
            }
            imageView.setBackgroundResource(i2);
            viewHolder.imageView.setMinimumWidth(getTrafficImgWidth(j, this.mMobileMaxTraffic, this.mTrafficMarkMinWidth));
            viewHolder.trafficValues.setText(FormatBytesUtil.formatBytes(this.mContext, j));
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_traffic_sorted, viewGroup, false));
    }

    public void setData(List<MiAppInfo> list) {
        this.mAppInfoList = list;
        trafficSorted(this.mTrafficType);
        buildMaxTraffic();
        notifyDataSetChanged();
    }

    public void trafficSorted(int i) {
        List<MiAppInfo> list = this.mAppInfoList;
        if (list != null) {
            Collections.sort(list, this.mComparatorByTraffic);
        }
        this.mTrafficType = i;
        notifyDataSetChanged();
    }
}
