package com.miui.networkassistant.vpn.miui;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;

public class MiuiVpnUtils {
    private static final int KEEP_PROC_LIVE_LEVEL_RUNNING = 1;
    private static final int KEEP_PROC_LIVE_TYPE = 512;
    private static final String KEEP_PROC_LIVE_URI = "content://com.miui.whetstone/activepolicymanager/insertbyapp";
    private static final String TAG = "MiuiVpnUtils";

    static class MiuiVpnDetailInfo {
        private boolean mAutoStart = true;
        private int mMinAndroidSdkVer = 0;
        private int mOperator;
        private List<String> mPackages;
        private String mPurchaseNotificationSummary;
        private String mPurchaseNotificationTitle;
        private int mType;

        public MiuiVpnDetailInfo(int i, int i2, List<String> list, int i3, boolean z, String str, String str2) {
            this.mType = i;
            this.mOperator = i2;
            this.mPackages = list != null ? new ArrayList(list) : new ArrayList();
            this.mMinAndroidSdkVer = i3;
            this.mAutoStart = z;
            this.mPurchaseNotificationTitle = str;
            this.mPurchaseNotificationSummary = str2;
        }

        public synchronized void addPackage(String str) {
            if (this.mPackages == null) {
                this.mPackages = new ArrayList();
            }
            if (!this.mPackages.contains(str)) {
                this.mPackages.add(str);
            }
        }

        public boolean getAutoStart() {
            return this.mAutoStart;
        }

        public int getOperator() {
            return this.mOperator;
        }

        public synchronized List<String> getPackages() {
            return new ArrayList(this.mPackages);
        }

        public int getType() {
            return this.mType;
        }
    }

    static class WatchPackageInfo {
        public boolean mIsRunning;
        public String mPackageName;
        public int mPid;
        public int mUid;

        public WatchPackageInfo(int i, String str, boolean z, int i2) {
            this.mUid = i;
            this.mPackageName = str;
            this.mIsRunning = z;
            this.mPid = i2;
        }
    }

    public static void keepVpnProcAlive(Context context, String str, int i, int i2, String str2, int i3, int i4) {
        if (context == null || TextUtils.isEmpty(str) || i == 0 || i2 == 0 || TextUtils.isEmpty(str2) || i3 == 0 || i4 == 0) {
            Log.e(TAG, "keepVpnProcAlive, invalid parameter");
            return;
        }
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", Integer.valueOf(KEEP_PROC_LIVE_TYPE));
            contentValues.put("package_name", str2);
            contentValues.put(MijiaAlertModel.KEY_UID, Integer.valueOf(i3));
            contentValues.put("pid", Integer.valueOf(i4));
            contentValues.put("status", 1);
            contentValues.put("linked_procname", str);
            contentValues.put("linked_pid", Integer.valueOf(i2));
            contentValues.put("linked_uid", Integer.valueOf(i));
            context.getContentResolver().insert(Uri.parse(KEEP_PROC_LIVE_URI), contentValues);
        } catch (Exception e) {
            Log.e(TAG, "keepVpnProcAlive", e);
        }
    }
}
