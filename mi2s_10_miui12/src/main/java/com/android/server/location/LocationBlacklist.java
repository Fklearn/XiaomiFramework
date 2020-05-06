package com.android.server.location;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.util.Slog;
import com.android.server.LocationManagerService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public final class LocationBlacklist extends ContentObserver {
    private static final String BLACKLIST_CONFIG_NAME = "locationPackagePrefixBlacklist";
    private static final boolean D = LocationManagerService.D;
    private static final String TAG = "LocationBlacklist";
    private static final String WHITELIST_CONFIG_NAME = "locationPackagePrefixWhitelist";
    private String[] mBlacklist = new String[0];
    private final Context mContext;
    private int mCurrentUserId = 0;
    private final Object mLock = new Object();
    private String[] mWhitelist = new String[0];

    public LocationBlacklist(Context context, Handler handler) {
        super(handler);
        this.mContext = context;
    }

    public void init() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(BLACKLIST_CONFIG_NAME), false, this, -1);
        reloadBlacklist();
    }

    private void reloadBlacklistLocked() {
        this.mWhitelist = getStringArrayLocked(WHITELIST_CONFIG_NAME);
        if (D) {
            Slog.d(TAG, "whitelist: " + Arrays.toString(this.mWhitelist));
        }
        this.mBlacklist = getStringArrayLocked(BLACKLIST_CONFIG_NAME);
        if (D) {
            Slog.d(TAG, "blacklist: " + Arrays.toString(this.mBlacklist));
        }
    }

    private void reloadBlacklist() {
        synchronized (this.mLock) {
            reloadBlacklistLocked();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003c, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isBlacklisted(java.lang.String r8) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            java.lang.String[] r1 = r7.mBlacklist     // Catch:{ all -> 0x0043 }
            int r2 = r1.length     // Catch:{ all -> 0x0043 }
            r3 = 0
            r4 = r3
        L_0x0008:
            if (r4 >= r2) goto L_0x0041
            r5 = r1[r4]     // Catch:{ all -> 0x0043 }
            boolean r6 = r8.startsWith(r5)     // Catch:{ all -> 0x0043 }
            if (r6 == 0) goto L_0x003e
            boolean r6 = r7.inWhitelist(r8)     // Catch:{ all -> 0x0043 }
            if (r6 == 0) goto L_0x0019
            goto L_0x003e
        L_0x0019:
            boolean r1 = D     // Catch:{ all -> 0x0043 }
            if (r1 == 0) goto L_0x003b
            java.lang.String r1 = "LocationBlacklist"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0043 }
            r2.<init>()     // Catch:{ all -> 0x0043 }
            java.lang.String r3 = "dropping location (blacklisted): "
            r2.append(r3)     // Catch:{ all -> 0x0043 }
            r2.append(r8)     // Catch:{ all -> 0x0043 }
            java.lang.String r3 = " matches "
            r2.append(r3)     // Catch:{ all -> 0x0043 }
            r2.append(r5)     // Catch:{ all -> 0x0043 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0043 }
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x0043 }
        L_0x003b:
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            r0 = 1
            return r0
        L_0x003e:
            int r4 = r4 + 1
            goto L_0x0008
        L_0x0041:
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            return r3
        L_0x0043:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.location.LocationBlacklist.isBlacklisted(java.lang.String):boolean");
    }

    private boolean inWhitelist(String pkg) {
        synchronized (this.mLock) {
            for (String white : this.mWhitelist) {
                if (pkg.startsWith(white)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void onChange(boolean selfChange) {
        reloadBlacklist();
    }

    public void switchUser(int userId) {
        synchronized (this.mLock) {
            this.mCurrentUserId = userId;
            reloadBlacklistLocked();
        }
    }

    private String[] getStringArrayLocked(String key) {
        String flatString;
        synchronized (this.mLock) {
            flatString = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), key, this.mCurrentUserId);
        }
        if (flatString == null) {
            return new String[0];
        }
        String[] splitStrings = flatString.split(",");
        ArrayList<String> result = new ArrayList<>();
        for (String pkg : splitStrings) {
            String pkg2 = pkg.trim();
            if (!pkg2.isEmpty()) {
                result.add(pkg2);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public void dump(PrintWriter pw) {
        pw.println("mWhitelist=" + Arrays.toString(this.mWhitelist) + " mBlacklist=" + Arrays.toString(this.mBlacklist));
    }
}
