package com.android.server.wifi;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;

public class MiuiScanRequestProxy {
    private static final String CLOUD_BACKGROUND_SCAN_WHITELIST = "cloud_backgound_scan_whitelist";
    private static final String CLOUD_FORGROUND_SCAN_WHITELIST = "cloud_forgound_scan_whitelist";
    private static final String CLOUD_HIGH_ACCURACY_SCAN_APP_LIST = "cloud_high_accuracy_scan_app_list";
    private static final boolean DEBUG = false;
    private static final String TAG = "MiuiScanRequestProxy";
    /* access modifiers changed from: private */
    public Set<String> mBKWhiteList;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public Set<String> mFGWhiteList;
    /* access modifiers changed from: private */
    public Set<String> mHighAccuracyList;
    /* access modifiers changed from: private */
    public final Object mHighAccuracyListLock;
    /* access modifiers changed from: private */
    public String mLocalBKWhiteList = "com.xiaomi.metoknlp,com.autonavi.minimap,com.autonavi.amapauto,com.baidu.BaiduMap,com.tencent.map,com.sogou.map.android.maps";
    /* access modifiers changed from: private */
    public String mLocalFGWhiteList = "com.xiaomi.metoknlp,com.autonavi.minimap,com.autonavi.amapauto,com.baidu.BaiduMap,com.tencent.map,com.sogou.map.android.maps,com.miui.huanji";
    /* access modifiers changed from: private */
    public String mLocalHighAccuracyList = "com.miui.huanji";
    /* access modifiers changed from: private */
    public final Object mWhiteListLock;

    public MiuiScanRequestProxy(Context context) {
        this.mContext = context;
        this.mWhiteListLock = new Object();
        this.mHighAccuracyListLock = new Object();
        this.mBKWhiteList = new HashSet();
        this.mFGWhiteList = new HashSet();
        this.mHighAccuracyList = new HashSet();
        String[] fgpackages = this.mLocalFGWhiteList.split(",");
        if (fgpackages != null) {
            for (String add : fgpackages) {
                this.mFGWhiteList.add(add);
            }
        }
        String[] bkpackages = this.mLocalBKWhiteList.split(",");
        if (bkpackages != null) {
            for (String add2 : bkpackages) {
                this.mBKWhiteList.add(add2);
            }
        }
        registerWhiteListChangedObserver();
        registerHighAccuracyScanAppListChangedObserver();
    }

    private void registerHighAccuracyScanAppListChangedObserver() {
        ContentObserver observer = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                synchronized (MiuiScanRequestProxy.this.mHighAccuracyListLock) {
                    MiuiScanRequestProxy.this.mHighAccuracyList.clear();
                }
                String cloudString = Settings.System.getStringForUser(MiuiScanRequestProxy.this.mContext.getContentResolver(), MiuiScanRequestProxy.CLOUD_HIGH_ACCURACY_SCAN_APP_LIST, -2);
                if (cloudString == null || TextUtils.isEmpty(cloudString)) {
                    cloudString = MiuiScanRequestProxy.this.mLocalHighAccuracyList;
                }
                String[] packages = cloudString.split(",");
                if (packages != null) {
                    synchronized (MiuiScanRequestProxy.this.mHighAccuracyListLock) {
                        for (String add : packages) {
                            MiuiScanRequestProxy.this.mHighAccuracyList.add(add);
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_HIGH_ACCURACY_SCAN_APP_LIST), false, observer, -2);
        new Thread(new Runnable(observer) {
            private final /* synthetic */ ContentObserver f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.onChange(false);
            }
        }).start();
    }

    private void registerWhiteListChangedObserver() {
        ContentObserver observer = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                synchronized (MiuiScanRequestProxy.this.mWhiteListLock) {
                    MiuiScanRequestProxy.this.mBKWhiteList.clear();
                    MiuiScanRequestProxy.this.mFGWhiteList.clear();
                }
                String whiteString = Settings.System.getStringForUser(MiuiScanRequestProxy.this.mContext.getContentResolver(), MiuiScanRequestProxy.CLOUD_BACKGROUND_SCAN_WHITELIST, -2);
                if (whiteString == null || TextUtils.isEmpty(whiteString)) {
                    whiteString = MiuiScanRequestProxy.this.mLocalBKWhiteList;
                }
                String[] packages = whiteString.split(",");
                if (packages != null) {
                    synchronized (MiuiScanRequestProxy.this.mWhiteListLock) {
                        for (String add : packages) {
                            MiuiScanRequestProxy.this.mBKWhiteList.add(add);
                        }
                    }
                }
                String whiteString2 = Settings.System.getStringForUser(MiuiScanRequestProxy.this.mContext.getContentResolver(), MiuiScanRequestProxy.CLOUD_FORGROUND_SCAN_WHITELIST, -2);
                if (whiteString2 == null || TextUtils.isEmpty(whiteString2)) {
                    whiteString2 = MiuiScanRequestProxy.this.mLocalFGWhiteList;
                }
                String[] packages2 = whiteString2.split(",");
                if (packages2 != null) {
                    synchronized (MiuiScanRequestProxy.this.mWhiteListLock) {
                        for (String add2 : packages2) {
                            MiuiScanRequestProxy.this.mFGWhiteList.add(add2);
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_BACKGROUND_SCAN_WHITELIST), false, observer, -2);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(CLOUD_FORGROUND_SCAN_WHITELIST), false, observer, -2);
        new Thread(new Runnable(observer) {
            private final /* synthetic */ ContentObserver f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.onChange(false);
            }
        }).start();
    }

    public boolean isPackageInWhiteList(boolean isBackGround, String packageName) {
        Set<String> list;
        if (isBackGround) {
            list = this.mBKWhiteList;
        } else {
            list = this.mFGWhiteList;
        }
        synchronized (this.mWhiteListLock) {
            for (String pkg : list) {
                if (packageName.equals(pkg)) {
                    Log.d(TAG, "Package " + packageName + " is in white list");
                    return true;
                }
            }
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getScanType(java.lang.String r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mHighAccuracyListLock
            monitor-enter(r0)
            r1 = 0
            if (r6 == 0) goto L_0x002d
            java.util.Set<java.lang.String> r2 = r5.mHighAccuracyList     // Catch:{ all -> 0x002f }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x002f }
            if (r2 == 0) goto L_0x000f
            goto L_0x002d
        L_0x000f:
            java.util.Set<java.lang.String> r2 = r5.mHighAccuracyList     // Catch:{ all -> 0x002f }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x002f }
        L_0x0015:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x002f }
            if (r3 == 0) goto L_0x002b
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x002f }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x002f }
            boolean r4 = r6.equals(r3)     // Catch:{ all -> 0x002f }
            if (r4 == 0) goto L_0x002a
            r1 = 2
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r1
        L_0x002a:
            goto L_0x0015
        L_0x002b:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r1
        L_0x002d:
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            return r1
        L_0x002f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.MiuiScanRequestProxy.getScanType(java.lang.String):int");
    }
}
