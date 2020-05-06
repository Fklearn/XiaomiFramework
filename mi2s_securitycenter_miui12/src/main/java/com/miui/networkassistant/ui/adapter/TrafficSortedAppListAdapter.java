package com.miui.networkassistant.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.TrafficInfo;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrafficSortedAppListAdapter extends RecyclerView.a<ViewHolder> {
    public static final int NON_SYSTEM_APP = 0;
    public static final int SYSTEM_APP = 1;
    private int mAdapterType;
    private List<TrafficInfo> mAppInfoListShow;
    private Context mContext;
    private IFirewallBinder mFirewallBinder;
    private boolean mIsMiuiHybirdEnable;
    private long mMaxTraffic = 0;
    private int mNetworkType;
    private int mSlotNum;
    private int mTrafficMarkMaxWidth;
    private int mTrafficMarkMinWidth;
    private int mTrafficType = 1;
    /* access modifiers changed from: private */
    public OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    private static class TrafficComparator implements Comparator<TrafficInfo> {
        private int mType;

        public TrafficComparator(int i) {
            this.mType = i;
        }

        public int compare(TrafficInfo trafficInfo, TrafficInfo trafficInfo2) {
            long[] jArr = trafficInfo.mAppStats.mTotalBytes;
            int i = this.mType;
            int i2 = ((trafficInfo2.mAppStats.mTotalBytes[i] - jArr[i]) > 0 ? 1 : ((trafficInfo2.mAppStats.mTotalBytes[i] - jArr[i]) == 0 ? 0 : -1));
            if (i2 > 0) {
                return 1;
            }
            return i2 == 0 ? 0 : -1;
        }
    }

    public class ViewHolder extends RecyclerView.u {
        TextView appName;
        ImageView arrowImageView;
        ImageView icon;
        ImageView imageView;
        ImageView netOffImageView;
        TextView traffic;
        TextView trafficValues;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.imageview_icon);
            this.appName = (TextView) view.findViewById(R.id.textview_appname);
            this.traffic = (TextView) view.findViewById(R.id.text_traffic);
            this.imageView = (ImageView) view.findViewById(R.id.image_traffic);
            this.trafficValues = (TextView) view.findViewById(R.id.text_traffic_values);
            this.netOffImageView = (ImageView) view.findViewById(R.id.image_netoff);
            this.arrowImageView = (ImageView) view.findViewById(R.id.imageview_arrow);
        }
    }

    public TrafficSortedAppListAdapter(Context context, List<TrafficInfo> list, int i, int i2) {
        this.mContext = context;
        this.mAppInfoListShow = list;
        this.mAdapterType = i;
        this.mSlotNum = i2;
        Resources resources = context.getResources();
        this.mTrafficMarkMinWidth = resources.getDimensionPixelSize(R.dimen.traffic_mark_min_width);
        this.mTrafficMarkMaxWidth = resources.getDimensionPixelSize(R.dimen.traffic_mark_max_width);
        this.mIsMiuiHybirdEnable = HybirdServiceUtil.isHybirdIntentExist(context.getApplicationContext());
    }

    private void buildMaxTraffic() {
        this.mMaxTraffic = 0;
        List<TrafficInfo> list = this.mAppInfoListShow;
        if (list != null) {
            for (TrafficInfo trafficInfo : list) {
                long j = this.mMaxTraffic;
                long[] jArr = trafficInfo.mAppStats.mTotalBytes;
                int i = this.mTrafficType;
                if (j < jArr[i]) {
                    this.mMaxTraffic = jArr[i];
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

    public List<TrafficInfo> getData() {
        return this.mAppInfoListShow;
    }

    public int getItemCount() {
        List<TrafficInfo> list = this.mAppInfoListShow;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        ImageView imageView;
        ImageView imageView2;
        int i2;
        List<TrafficInfo> list = this.mAppInfoListShow;
        if (list != null && i < list.size()) {
            if (this.onItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        TrafficSortedAppListAdapter.this.onItemClickListener.onItemClick(i);
                    }
                });
            }
            TrafficInfo trafficInfo = this.mAppInfoListShow.get(i);
            String charSequence = trafficInfo.mAppInfo.packageName.toString();
            IconCacheHelper.getInstance().setIconToImageView(viewHolder.icon, charSequence);
            viewHolder.appName.setText(LabelLoadHelper.loadLabel(this.mContext, charSequence));
            int i3 = 0;
            if ((-10 == trafficInfo.mAppInfo.uid || (this.mIsMiuiHybirdEnable && HybirdServiceUtil.isHybirdService(charSequence))) && this.mAdapterType == 0) {
                viewHolder.arrowImageView.setVisibility(0);
            } else {
                viewHolder.arrowImageView.setVisibility(8);
            }
            long j = trafficInfo.mAppStats.mTotalBytes[this.mTrafficType];
            if (j > 0) {
                viewHolder.imageView.setVisibility(0);
                if (this.mNetworkType == 1) {
                    imageView2 = viewHolder.imageView;
                    i2 = R.drawable.traffic_wifi;
                } else {
                    imageView2 = viewHolder.imageView;
                    i2 = R.drawable.traffic_mobile;
                }
                imageView2.setBackgroundResource(i2);
                viewHolder.imageView.setMinimumWidth(getTrafficImgWidth(j, this.mMaxTraffic, this.mTrafficMarkMinWidth));
                viewHolder.trafficValues.setText(FormatBytesUtil.formatBytes(this.mContext, j));
            } else {
                viewHolder.imageView.setVisibility(8);
                viewHolder.trafficValues.setText(R.string.traffic_none);
            }
            IFirewallBinder iFirewallBinder = this.mFirewallBinder;
            if (iFirewallBinder != null) {
                try {
                    if (this.mNetworkType == 1) {
                        FirewallRule wifiRule = iFirewallBinder.getWifiRule(charSequence);
                        imageView = viewHolder.netOffImageView;
                        if (wifiRule != FirewallRule.Restrict) {
                            i3 = 8;
                        }
                    } else {
                        FirewallRule mobileRule = iFirewallBinder.getMobileRule(charSequence, this.mSlotNum);
                        imageView = viewHolder.netOffImageView;
                        if (mobileRule != FirewallRule.Restrict) {
                            i3 = 8;
                        }
                    }
                    imageView.setVisibility(i3);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_traffic_sorted, viewGroup, false));
    }

    public void setData(List<TrafficInfo> list, int i, int i2, int i3) {
        this.mAppInfoListShow = list;
        this.mNetworkType = i2;
        this.mSlotNum = i3;
        setMode(i);
        buildMaxTraffic();
        notifyDataSetChanged();
    }

    public void setFirewall(IFirewallBinder iFirewallBinder) {
        this.mFirewallBinder = iFirewallBinder;
    }

    public void setMode(int i) {
        List<TrafficInfo> list = this.mAppInfoListShow;
        if (list != null) {
            Collections.sort(list, new TrafficComparator(i));
        }
        this.mTrafficType = i;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }
}
