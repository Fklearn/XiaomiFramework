package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.server.pm.Settings;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CloudUtils {
    private static final String BLACK_LIST = "cloud_slave_wifi_only_blacklist";
    /* access modifiers changed from: private */
    public Set<String> mBlacklistPackageNames;
    /* access modifiers changed from: private */
    public Set<Integer> mBlacklistUids;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final Handler mHandler;
    private String mLocalBlackListPackageNames = "com.android.htmlviewer,com.tencent.cmocmna";
    /* access modifiers changed from: private */
    public Object mLock;

    public CloudUtils(Context context, String tag) {
        this.mContext = context;
        this.mLock = new Object();
        HandlerThread thread = new HandlerThread(tag);
        thread.start();
        this.mHandler = new Handler(thread.getLooper());
        this.mBlacklistUids = new HashSet();
        this.mBlacklistPackageNames = new HashSet();
        registerSlaveOnlyBlacklistChangedObserver();
        initBroadcastReceiver();
    }

    public boolean isUidInSlaveOnlyBlackList(int uid) {
        Set<Integer> set = this.mBlacklistUids;
        if (set == null || set.isEmpty()) {
            return false;
        }
        return this.mBlacklistUids.contains(Integer.valueOf(uid));
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme(Settings.ATTR_PACKAGE);
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String packageName = intent.getData().getSchemeSpecificPart();
                if (!TextUtils.isEmpty(packageName)) {
                    synchronized (CloudUtils.this.mLock) {
                        if (CloudUtils.this.mBlacklistPackageNames.contains(packageName)) {
                            int uid = intent.getIntExtra("android.intent.extra.UID", -1);
                            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
                                CloudUtils.this.mBlacklistUids.add(Integer.valueOf(uid));
                            } else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
                                CloudUtils.this.mBlacklistUids.remove(Integer.valueOf(uid));
                            }
                        }
                    }
                }
            }
        }, intentFilter);
    }

    private void registerSlaveOnlyBlacklistChangedObserver() {
        final ContentObserver observer = new ContentObserver(Handler.getMain()) {
            public void onChange(boolean selfChange) {
                PackageManager pm = CloudUtils.this.mContext.getPackageManager();
                List<UserInfo> users = ((UserManager) CloudUtils.this.mContext.getSystemService("user")).getUsers();
                synchronized (CloudUtils.this.mLock) {
                    Set unused = CloudUtils.this.mBlacklistPackageNames = CloudUtils.this.getBlackListPackageNames(CloudUtils.this.mContext);
                    CloudUtils.this.mBlacklistUids.clear();
                    if (!CloudUtils.this.mBlacklistPackageNames.isEmpty()) {
                        for (UserInfo user : users) {
                            for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                                if (!(app.packageName == null || app.applicationInfo == null || !CloudUtils.this.mBlacklistPackageNames.contains(app.packageName))) {
                                    CloudUtils.this.mBlacklistUids.add(Integer.valueOf(UserHandle.getUid(user.id, app.applicationInfo.uid)));
                                }
                            }
                        }
                    }
                }
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(BLACK_LIST), false, observer, -2);
        this.mHandler.post(new Runnable() {
            public void run() {
                observer.onChange(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public Set<String> getBlackListPackageNames(Context context) {
        String[] packages;
        String blacklistString = Settings.System.getStringForUser(context.getContentResolver(), BLACK_LIST, -2);
        if (blacklistString == null || TextUtils.isEmpty(blacklistString)) {
            blacklistString = this.mLocalBlackListPackageNames;
        }
        Set<String> blacklist = new HashSet<>();
        if (!TextUtils.isEmpty(blacklistString) && (packages = blacklistString.split(",")) != null) {
            for (String add : packages) {
                blacklist.add(add);
            }
        }
        return blacklist;
    }
}
