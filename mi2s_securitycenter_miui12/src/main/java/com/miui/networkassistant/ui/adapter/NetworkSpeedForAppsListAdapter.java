package com.miui.networkassistant.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.B;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.IconCacheHelper;
import com.miui.networkassistant.utils.LabelLoadHelper;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class NetworkSpeedForAppsListAdapter extends RecyclerView.a<ViewHolder> {
    private static final long NETWORK_SPEED_REFRESH_INTERVAL = 1000;
    private static final String TAG = "ND_AppSpeedAdapter";
    private HashMap<Integer, AppInfo> mAllAppInfoMap = new HashMap<>();
    private ArrayList<String> mAppCloseWhiteList = new ArrayList<>();
    private Context mContext;
    private Object mLock = new Object();
    private ArrayList<AppSpeedInfo> mNetworkSpeedList = new ArrayList<>();
    private HashMap<Integer, AppSpeedInfo> mShowingAppInfoMap = new HashMap<>();
    /* access modifiers changed from: private */
    public OnItemClickListener onItemClickListener;

    public static final class AppSpeedInfo {
        AppInfo appInfo;
        long speedRx;
        long speedTx;
        long total = 0;

        public AppSpeedInfo(AppInfo appInfo2, long j, long j2) {
            this.appInfo = appInfo2;
            this.speedRx = j;
            this.speedTx = j2;
            this.total += j;
            this.total += j2;
        }

        /* access modifiers changed from: package-private */
        public void add(long j, long j2) {
            this.speedRx += j;
            this.speedTx += j2;
            this.total += j;
            this.total += j2;
        }

        public AppInfo getAppInfo() {
            return this.appInfo;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.speedRx = 0;
            this.speedTx = 0;
        }
    }

    private static final class MyComparator implements Comparator<AppSpeedInfo> {
        private MyComparator() {
        }

        public int compare(AppSpeedInfo appSpeedInfo, AppSpeedInfo appSpeedInfo2) {
            return Long.compare(appSpeedInfo2.total, appSpeedInfo.total);
        }
    }

    public static class NetworkSpeedTotalInfo {
        public long rxTotal = 0;
        public long total = 0;
        public long txTotal = 0;
    }

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    protected static class ViewHolder extends RecyclerView.u {
        ImageView icon;
        TextView label;
        String packageName;
        TextView trafficRx;
        TextView trafficTx;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.icon = (ImageView) view.findViewById(R.id.imageview_icon);
            this.label = (TextView) view.findViewById(R.id.textview_appname);
            this.trafficRx = (TextView) view.findViewById(R.id.textview_traffic_rx);
            this.trafficTx = (TextView) view.findViewById(R.id.textview_traffic_tx);
        }
    }

    public NetworkSpeedForAppsListAdapter(Activity activity) {
        this.mContext = activity;
        this.mAppCloseWhiteList.add("com.xiaomi.xmsf");
        this.mAppCloseWhiteList.add("com.lbe.security.miui");
        this.mAppCloseWhiteList.add("com.miui.core");
        this.mAppCloseWhiteList.add("com.xiaomi.vip");
        this.mAppCloseWhiteList.add("com.miui.sdk");
        this.mAppCloseWhiteList.add("com.miui.guardprovider");
        this.mAppCloseWhiteList.add("com.miui.home");
        this.mAppCloseWhiteList.add("com.mi.android.globallauncher");
        this.mAppCloseWhiteList.add("com.android.providers.downloads.ui");
        this.mAppCloseWhiteList.add("com.xiaomi.finddevice");
        this.mAppCloseWhiteList.add("com.google.android.inputmethod.pinyin");
    }

    private AppInfo getAppInfoByUidLock(int i) {
        AppInfo appInfo = this.mAllAppInfoMap.get(Integer.valueOf(i));
        return appInfo == null ? i == -5 ? new AppInfo((CharSequence) "icon_personal_hotpot", -5) : i == 0 ? new AppInfo((CharSequence) "icon_root", 0) : appInfo : appInfo;
    }

    public ArrayList<AppSpeedInfo> getData() {
        return this.mNetworkSpeedList;
    }

    public int getItemCount() {
        int size;
        synchronized (this.mNetworkSpeedList) {
            size = this.mNetworkSpeedList == null ? 0 : this.mNetworkSpeedList.size();
        }
        return size;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001f, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getItemId(int r4) {
        /*
            r3 = this;
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r0 = r3.mNetworkSpeedList
            monitor-enter(r0)
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r1 = r3.mNetworkSpeedList     // Catch:{ all -> 0x0022 }
            if (r1 == 0) goto L_0x001e
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r1 = r3.mNetworkSpeedList     // Catch:{ all -> 0x0022 }
            int r1 = r1.size()     // Catch:{ all -> 0x0022 }
            if (r1 <= r4) goto L_0x001e
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r1 = r3.mNetworkSpeedList     // Catch:{ all -> 0x0022 }
            java.lang.Object r4 = r1.get(r4)     // Catch:{ all -> 0x0022 }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r4 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r4     // Catch:{ all -> 0x0022 }
            com.miui.networkassistant.model.AppInfo r4 = r4.appInfo     // Catch:{ all -> 0x0022 }
            int r4 = r4.uid     // Catch:{ all -> 0x0022 }
            long r1 = (long) r4     // Catch:{ all -> 0x0022 }
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r1
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            r0 = 0
            return r0
        L_0x0022:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.getItemId(int):long");
    }

    public boolean isAppCanClose(AppInfo appInfo) {
        if (appInfo == null) {
            return false;
        }
        int a2 = B.a(appInfo.uid);
        if (PreSetGroup.isGroupUid(a2) || a2 < 10000) {
            return false;
        }
        Iterator<String> it = this.mAppCloseWhiteList.iterator();
        while (it.hasNext()) {
            if (TextUtils.equals(it.next(), PackageUtil.getRealPackageName(appInfo.packageName.toString()))) {
                return false;
            }
        }
        return true;
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (NetworkSpeedForAppsListAdapter.this.onItemClickListener != null) {
                    NetworkSpeedForAppsListAdapter.this.onItemClickListener.onItemClick(i);
                }
            }
        });
        AppSpeedInfo appSpeedInfo = getData().get(i);
        if (!TextUtils.equals(viewHolder.packageName, appSpeedInfo.appInfo.packageName)) {
            IconCacheHelper.getInstance().setIconToImageView(viewHolder.icon, appSpeedInfo.appInfo.packageName.toString());
            viewHolder.label.setText(LabelLoadHelper.loadLabel(this.mContext, appSpeedInfo.appInfo.packageName));
            viewHolder.packageName = appSpeedInfo.appInfo.packageName.toString();
        }
        String[] formatSpeed = FormatBytesUtil.formatSpeed(this.mContext, appSpeedInfo.speedRx);
        if (formatSpeed != null) {
            TextView textView = viewHolder.trafficRx;
            textView.setText(formatSpeed[0] + formatSpeed[1]);
        }
        String[] formatSpeed2 = FormatBytesUtil.formatSpeed(this.mContext, appSpeedInfo.speedTx);
        if (formatSpeed2 != null) {
            TextView textView2 = viewHolder.trafficTx;
            textView2.setText(formatSpeed2[0] + formatSpeed2[1]);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_network_speed_for_apps, viewGroup, false));
    }

    public void packageStoped(int i) {
        this.mShowingAppInfoMap.remove(Integer.valueOf(i));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0242, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.NetworkSpeedTotalInfo refresh(java.util.ArrayList<java.util.Map<java.lang.String, java.lang.String>> r22, java.lang.String r23, long r24) {
        /*
            r21 = this;
            r1 = r21
            r0 = r23
            java.lang.Object r2 = r1.mLock
            monitor-enter(r2)
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$NetworkSpeedTotalInfo r3 = new com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$NetworkSpeedTotalInfo     // Catch:{ all -> 0x0243 }
            r3.<init>()     // Catch:{ all -> 0x0243 }
            if (r22 == 0) goto L_0x0241
            if (r0 == 0) goto L_0x0241
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.model.AppInfo> r4 = r1.mAllAppInfoMap     // Catch:{ all -> 0x0243 }
            int r4 = r4.size()     // Catch:{ all -> 0x0243 }
            if (r4 != 0) goto L_0x001a
            goto L_0x0241
        L_0x001a:
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r4 = r1.mNetworkSpeedList     // Catch:{ all -> 0x0243 }
            r4.clear()     // Catch:{ all -> 0x0243 }
            java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0243 }
            android.content.Context r4 = r1.mContext     // Catch:{ all -> 0x0243 }
            java.lang.String r5 = "activity"
            java.lang.Object r4 = r4.getSystemService(r5)     // Catch:{ all -> 0x0243 }
            android.app.ActivityManager r4 = (android.app.ActivityManager) r4     // Catch:{ all -> 0x0243 }
            java.util.List r4 = r4.getRunningAppProcesses()     // Catch:{ all -> 0x0243 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x0243 }
        L_0x0034:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x0243 }
            if (r5 == 0) goto L_0x0070
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x0243 }
            android.app.ActivityManager$RunningAppProcessInfo r5 = (android.app.ActivityManager.RunningAppProcessInfo) r5     // Catch:{ all -> 0x0243 }
            int r6 = r5.uid     // Catch:{ all -> 0x0243 }
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r6 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            int r7 = r5.uid     // Catch:{ all -> 0x0243 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x0243 }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x0243 }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r6 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r6     // Catch:{ all -> 0x0243 }
            if (r6 != 0) goto L_0x0034
            int r6 = r5.uid     // Catch:{ all -> 0x0243 }
            com.miui.networkassistant.model.AppInfo r8 = r1.getAppInfoByUidLock(r6)     // Catch:{ all -> 0x0243 }
            if (r8 == 0) goto L_0x0034
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r6 = new com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo     // Catch:{ all -> 0x0243 }
            r9 = 0
            r11 = 0
            r7 = r6
            r7.<init>(r8, r9, r11)     // Catch:{ all -> 0x0243 }
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r7 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            int r5 = r5.uid     // Catch:{ all -> 0x0243 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0243 }
            r7.put(r5, r6)     // Catch:{ all -> 0x0243 }
            goto L_0x0034
        L_0x0070:
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r4 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            r5 = -6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0243 }
            java.lang.Object r4 = r4.get(r6)     // Catch:{ all -> 0x0243 }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r4 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r4     // Catch:{ all -> 0x0243 }
            if (r4 != 0) goto L_0x0098
            com.miui.networkassistant.model.AppInfo r7 = r1.getAppInfoByUidLock(r5)     // Catch:{ all -> 0x0243 }
            if (r7 == 0) goto L_0x0098
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r4 = new com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo     // Catch:{ all -> 0x0243 }
            r8 = 0
            r10 = 0
            r6 = r4
            r6.<init>(r7, r8, r10)     // Catch:{ all -> 0x0243 }
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r6 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x0243 }
            r6.put(r7, r4)     // Catch:{ all -> 0x0243 }
        L_0x0098:
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r4 = r1.mNetworkSpeedList     // Catch:{ all -> 0x0243 }
            monitor-enter(r4)     // Catch:{ all -> 0x0243 }
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r6 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x023e }
            java.util.Collection r6 = r6.values()     // Catch:{ all -> 0x023e }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x023e }
        L_0x00a5:
            boolean r7 = r6.hasNext()     // Catch:{ all -> 0x023e }
            if (r7 == 0) goto L_0x00c7
            java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x023e }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r7 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r7     // Catch:{ all -> 0x023e }
            if (r7 == 0) goto L_0x00a5
            r7.reset()     // Catch:{ all -> 0x023e }
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r8 = r1.mNetworkSpeedList     // Catch:{ all -> 0x023e }
            r8.add(r7)     // Catch:{ all -> 0x023e }
            java.util.ArrayList<com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r7 = r1.mNetworkSpeedList     // Catch:{ all -> 0x023e }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$MyComparator r8 = new com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$MyComparator     // Catch:{ all -> 0x023e }
            r9 = 0
            r8.<init>()     // Catch:{ all -> 0x023e }
            java.util.Collections.sort(r7, r8)     // Catch:{ all -> 0x023e }
            goto L_0x00a5
        L_0x00c7:
            monitor-exit(r4)     // Catch:{ all -> 0x023e }
            java.util.Iterator r4 = r22.iterator()     // Catch:{ all -> 0x0243 }
        L_0x00cc:
            boolean r6 = r4.hasNext()     // Catch:{ all -> 0x0243 }
            if (r6 == 0) goto L_0x0239
            java.lang.Object r6 = r4.next()     // Catch:{ all -> 0x0243 }
            java.util.Map r6 = (java.util.Map) r6     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = "iface"
            java.lang.Object r7 = r6.get(r7)     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ all -> 0x0243 }
            java.lang.String r8 = "uid"
            java.lang.Object r8 = r6.get(r8)     // Catch:{ all -> 0x0243 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ all -> 0x0243 }
            int r8 = java.lang.Integer.parseInt(r8)     // Catch:{ all -> 0x0243 }
            java.lang.String r9 = "tag"
            java.lang.Object r9 = r6.get(r9)     // Catch:{ all -> 0x0243 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ all -> 0x0243 }
            int r9 = java.lang.Integer.parseInt(r9)     // Catch:{ all -> 0x0243 }
            java.lang.String r10 = "rxBytes"
            java.lang.Object r10 = r6.get(r10)     // Catch:{ all -> 0x0243 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ all -> 0x0243 }
            long r10 = java.lang.Long.parseLong(r10)     // Catch:{ all -> 0x0243 }
            java.lang.String r12 = "txBytes"
            java.lang.Object r6 = r6.get(r12)     // Catch:{ all -> 0x0243 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x0243 }
            long r12 = java.lang.Long.parseLong(r6)     // Catch:{ all -> 0x0243 }
            r14 = 0
            int r6 = (r24 > r14 ? 1 : (r24 == r14 ? 0 : -1))
            if (r6 <= 0) goto L_0x0120
            r16 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 * r16
            long r10 = r10 / r24
            long r12 = r12 * r16
            long r12 = r12 / r24
        L_0x0120:
            boolean r6 = r0.equals(r7)     // Catch:{ all -> 0x0243 }
            if (r6 == 0) goto L_0x0206
            long r6 = r10 + r12
            int r14 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r14 <= 0) goto L_0x0206
            if (r9 != 0) goto L_0x0206
            r9 = 2147483647(0x7fffffff, float:NaN)
            r14 = -5
            if (r8 != r9) goto L_0x0135
            r8 = r14
        L_0x0135:
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r9 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r8)     // Catch:{ all -> 0x0243 }
            java.lang.Object r9 = r9.get(r15)     // Catch:{ all -> 0x0243 }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r9 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r9     // Catch:{ all -> 0x0243 }
            if (r9 != 0) goto L_0x01cd
            if (r8 != r14) goto L_0x016d
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r9 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x0243 }
            java.lang.Object r9 = r9.get(r15)     // Catch:{ all -> 0x0243 }
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r9 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r9     // Catch:{ all -> 0x0243 }
            if (r9 != 0) goto L_0x01cd
            com.miui.networkassistant.model.AppInfo r16 = r1.getAppInfoByUidLock(r14)     // Catch:{ all -> 0x0243 }
            if (r16 == 0) goto L_0x01cd
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r9 = new com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo     // Catch:{ all -> 0x0243 }
            r17 = 0
            r19 = 0
            r15 = r9
            r15.<init>(r16, r17, r19)     // Catch:{ all -> 0x0243 }
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r15 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x0243 }
            r15.put(r14, r9)     // Catch:{ all -> 0x0243 }
            goto L_0x01cd
        L_0x016d:
            java.lang.String r14 = "ND_AppSpeedAdapter"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0243 }
            r15.<init>()     // Catch:{ all -> 0x0243 }
            java.lang.String r5 = "uid="
            r15.append(r5)     // Catch:{ all -> 0x0243 }
            r15.append(r8)     // Catch:{ all -> 0x0243 }
            java.lang.String r5 = ",rxBytes="
            r15.append(r5)     // Catch:{ all -> 0x0243 }
            r15.append(r10)     // Catch:{ all -> 0x0243 }
            java.lang.String r5 = ",txBytes="
            r15.append(r5)     // Catch:{ all -> 0x0243 }
            r15.append(r12)     // Catch:{ all -> 0x0243 }
            java.lang.String r5 = r15.toString()     // Catch:{ all -> 0x0243 }
            android.util.Log.i(r14, r5)     // Catch:{ all -> 0x0243 }
            r5 = 1000(0x3e8, float:1.401E-42)
            if (r8 >= r5) goto L_0x01a6
            java.util.HashMap<java.lang.Integer, com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo> r5 = r1.mShowingAppInfoMap     // Catch:{ all -> 0x0243 }
            r14 = -6
            java.lang.Integer r9 = java.lang.Integer.valueOf(r14)     // Catch:{ all -> 0x0243 }
            java.lang.Object r5 = r5.get(r9)     // Catch:{ all -> 0x0243 }
            r9 = r5
            com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$AppSpeedInfo r9 = (com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.AppSpeedInfo) r9     // Catch:{ all -> 0x0243 }
            goto L_0x01cd
        L_0x01a6:
            r14 = -6
            java.lang.String r5 = "ND_AppSpeedAdapter"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0243 }
            r15.<init>()     // Catch:{ all -> 0x0243 }
            java.lang.String r14 = "uid "
            r15.append(r14)     // Catch:{ all -> 0x0243 }
            r15.append(r8)     // Catch:{ all -> 0x0243 }
            java.lang.String r14 = " not running? rxBytes="
            r15.append(r14)     // Catch:{ all -> 0x0243 }
            r15.append(r10)     // Catch:{ all -> 0x0243 }
            java.lang.String r14 = ",txBytes="
            r15.append(r14)     // Catch:{ all -> 0x0243 }
            r15.append(r12)     // Catch:{ all -> 0x0243 }
            java.lang.String r14 = r15.toString()     // Catch:{ all -> 0x0243 }
            android.util.Log.i(r5, r14)     // Catch:{ all -> 0x0243 }
        L_0x01cd:
            if (r9 == 0) goto L_0x01e2
            r9.add(r10, r12)     // Catch:{ all -> 0x0243 }
            long r8 = r3.total     // Catch:{ all -> 0x0243 }
            long r8 = r8 + r6
            r3.total = r8     // Catch:{ all -> 0x0243 }
            long r5 = r3.rxTotal     // Catch:{ all -> 0x0243 }
            long r5 = r5 + r10
            r3.rxTotal = r5     // Catch:{ all -> 0x0243 }
            long r5 = r3.txTotal     // Catch:{ all -> 0x0243 }
            long r5 = r5 + r12
            r3.txTotal = r5     // Catch:{ all -> 0x0243 }
            goto L_0x0236
        L_0x01e2:
            java.lang.String r5 = "ND_AppSpeedAdapter"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0243 }
            r6.<init>()     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = "appSpeedInfo is null!! uid="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r8)     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = ",rxBytes="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r10)     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = ",txBytes="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r12)     // Catch:{ all -> 0x0243 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0243 }
            goto L_0x0233
        L_0x0206:
            if (r9 == 0) goto L_0x0236
            java.lang.String r5 = "ND_AppSpeedAdapter"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0243 }
            r6.<init>()     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = "tag="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r9)     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = ",uid="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r8)     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = ",rxBytes="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r10)     // Catch:{ all -> 0x0243 }
            java.lang.String r7 = ",txBytes="
            r6.append(r7)     // Catch:{ all -> 0x0243 }
            r6.append(r12)     // Catch:{ all -> 0x0243 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0243 }
        L_0x0233:
            android.util.Log.i(r5, r6)     // Catch:{ all -> 0x0243 }
        L_0x0236:
            r5 = -6
            goto L_0x00cc
        L_0x0239:
            r21.notifyDataSetChanged()     // Catch:{ all -> 0x0243 }
            monitor-exit(r2)     // Catch:{ all -> 0x0243 }
            return r3
        L_0x023e:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x023e }
            throw r0     // Catch:{ all -> 0x0243 }
        L_0x0241:
            monitor-exit(r2)     // Catch:{ all -> 0x0243 }
            return r3
        L_0x0243:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0243 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter.refresh(java.util.ArrayList, java.lang.String, long):com.miui.networkassistant.ui.adapter.NetworkSpeedForAppsListAdapter$NetworkSpeedTotalInfo");
    }

    public void setAppList(ArrayList<AppInfo> arrayList) {
        synchronized (this.mLock) {
            this.mAllAppInfoMap.clear();
            Iterator<AppInfo> it = arrayList.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                this.mAllAppInfoMap.put(Integer.valueOf(next.uid), next);
            }
            AppInfo appInfo = new AppInfo((CharSequence) "icon_others", -6);
            this.mAllAppInfoMap.put(Integer.valueOf(appInfo.uid), appInfo);
        }
    }

    public void setData(ArrayList<AppSpeedInfo> arrayList) {
        this.mNetworkSpeedList = arrayList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }
}
