package com.miui.networkassistant.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class LockScreenAppListAdapter extends RecyclerView.a<ViewHolder> {
    private Context mContext;
    private ArrayList<MyAppInfo> mLockScreenAppList = new ArrayList<>();
    /* access modifiers changed from: private */
    public OnItemClickListener mOnItemClickListener;
    private HashMap<Integer, Long> mUidMap;

    public static final class MyAppInfo {
        public AppInfo appInfo;
        long lockScreedBytes;

        public MyAppInfo(AppInfo appInfo2, long j) {
            this.appInfo = appInfo2;
            this.lockScreedBytes = j;
        }
    }

    private static final class MyComparator implements Comparator<MyAppInfo> {
        private MyComparator() {
        }

        public int compare(MyAppInfo myAppInfo, MyAppInfo myAppInfo2) {
            return (int) (myAppInfo2.lockScreedBytes - myAppInfo.lockScreedBytes);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    static class ViewHolder extends RecyclerView.u {
        ImageView icon;
        TextView label;
        TextView traffic;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.imageview_icon);
            this.label = (TextView) view.findViewById(R.id.textview_appname);
            this.traffic = (TextView) view.findViewById(R.id.textview_traffic);
        }
    }

    public LockScreenAppListAdapter(Activity activity) {
        this.mContext = activity;
    }

    public ArrayList<MyAppInfo> getData() {
        return this.mLockScreenAppList;
    }

    public int getItemCount() {
        return this.mLockScreenAppList.size();
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ArrayList<MyAppInfo> arrayList = this.mLockScreenAppList;
        if (arrayList != null && arrayList.size() > i) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    LockScreenAppListAdapter.this.mOnItemClickListener.onItemClick(i);
                }
            });
            MyAppInfo myAppInfo = this.mLockScreenAppList.get(i);
            IconCacheHelper.getInstance().setIconToImageView(viewHolder.icon, myAppInfo.appInfo.packageName.toString());
            viewHolder.label.setText(LabelLoadHelper.loadLabel(this.mContext, myAppInfo.appInfo.packageName.toString()));
            viewHolder.traffic.setText(FormatBytesUtil.formatBytes(this.mContext, myAppInfo.lockScreedBytes));
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_lock_screen, viewGroup, false));
    }

    public void setData(ArrayList<AppInfo> arrayList, HashMap<Integer, Long> hashMap) {
        if (arrayList != null && hashMap != null) {
            this.mUidMap = hashMap;
            this.mLockScreenAppList.clear();
            Iterator<AppInfo> it = arrayList.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                Long l = this.mUidMap.get(Integer.valueOf(next.uid));
                if (l != null && l.longValue() > 0) {
                    this.mLockScreenAppList.add(new MyAppInfo(next, l.longValue()));
                }
            }
            Collections.sort(this.mLockScreenAppList, new MyComparator());
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
