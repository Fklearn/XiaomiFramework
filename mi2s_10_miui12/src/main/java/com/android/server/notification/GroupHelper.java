package com.android.server.notification;

import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class GroupHelper {
    protected static final String AUTOGROUP_KEY = "ranker_group";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final String TAG = "GroupHelper";
    private final int mAutoGroupAtCount;
    private final Callback mCallback;
    Map<Integer, Map<String, LinkedHashSet<String>>> mUngroupedNotifications = new HashMap();

    protected interface Callback {
        void addAutoGroup(String str);

        void addAutoGroupSummary(int i, String str, String str2);

        void removeAutoGroup(String str);

        void removeAutoGroupSummary(int i, String str);
    }

    public GroupHelper(int autoGroupAtCount, Callback callback) {
        this.mAutoGroupAtCount = autoGroupAtCount;
        this.mCallback = callback;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void onNotificationPosted(StatusBarNotification sbn, boolean autogroupSummaryExists) {
        if (DEBUG) {
            Log.i(TAG, "POSTED " + sbn.getKey());
        }
        try {
            List<String> notificationsToGroup = new ArrayList<>();
            if (!sbn.isAppGroup()) {
                synchronized (this.mUngroupedNotifications) {
                    Map<String, LinkedHashSet<String>> ungroupedNotificationsByUser = this.mUngroupedNotifications.get(Integer.valueOf(sbn.getUserId()));
                    if (ungroupedNotificationsByUser == null) {
                        ungroupedNotificationsByUser = new HashMap<>();
                    }
                    this.mUngroupedNotifications.put(Integer.valueOf(sbn.getUserId()), ungroupedNotificationsByUser);
                    LinkedHashSet<String> notificationsForPackage = ungroupedNotificationsByUser.get(sbn.getPackageName());
                    if (notificationsForPackage == null) {
                        notificationsForPackage = new LinkedHashSet<>();
                    }
                    notificationsForPackage.add(sbn.getKey());
                    ungroupedNotificationsByUser.put(sbn.getPackageName(), notificationsForPackage);
                    if (notificationsForPackage.size() >= this.mAutoGroupAtCount || autogroupSummaryExists) {
                        notificationsToGroup.addAll(notificationsForPackage);
                    }
                }
                if (notificationsToGroup.size() > 0) {
                    adjustAutogroupingSummary(sbn.getUserId(), sbn.getPackageName(), notificationsToGroup.get(0), true);
                    adjustNotificationBundling(notificationsToGroup, true);
                    return;
                }
                return;
            }
            maybeUngroup(sbn, false, sbn.getUserId());
        } catch (Exception e) {
            Slog.e(TAG, "Failure processing new notification", e);
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        try {
            maybeUngroup(sbn, true, sbn.getUserId());
        } catch (Exception e) {
            Slog.e(TAG, "Error processing canceled notification", e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0059, code lost:
        if (r1 == false) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005b, code lost:
        adjustAutogroupingSummary(r9, r7.getPackageName(), (java.lang.String) null, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0067, code lost:
        if (r0.size() <= 0) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0069, code lost:
        adjustNotificationBundling(r0, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0070, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void maybeUngroup(android.service.notification.StatusBarNotification r7, boolean r8, int r9) {
        /*
            r6 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            java.util.Map<java.lang.Integer, java.util.Map<java.lang.String, java.util.LinkedHashSet<java.lang.String>>> r2 = r6.mUngroupedNotifications
            monitor-enter(r2)
            java.util.Map<java.lang.Integer, java.util.Map<java.lang.String, java.util.LinkedHashSet<java.lang.String>>> r3 = r6.mUngroupedNotifications     // Catch:{ all -> 0x0071 }
            int r4 = r7.getUserId()     // Catch:{ all -> 0x0071 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0071 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ all -> 0x0071 }
            java.util.Map r3 = (java.util.Map) r3     // Catch:{ all -> 0x0071 }
            if (r3 == 0) goto L_0x006f
            int r4 = r3.size()     // Catch:{ all -> 0x0071 }
            if (r4 != 0) goto L_0x0022
            goto L_0x006f
        L_0x0022:
            java.lang.String r4 = r7.getPackageName()     // Catch:{ all -> 0x0071 }
            java.lang.Object r4 = r3.get(r4)     // Catch:{ all -> 0x0071 }
            java.util.LinkedHashSet r4 = (java.util.LinkedHashSet) r4     // Catch:{ all -> 0x0071 }
            if (r4 == 0) goto L_0x006d
            int r5 = r4.size()     // Catch:{ all -> 0x0071 }
            if (r5 != 0) goto L_0x0036
            goto L_0x006d
        L_0x0036:
            java.lang.String r5 = r7.getKey()     // Catch:{ all -> 0x0071 }
            boolean r5 = r4.remove(r5)     // Catch:{ all -> 0x0071 }
            if (r5 == 0) goto L_0x0049
            if (r8 != 0) goto L_0x0049
            java.lang.String r5 = r7.getKey()     // Catch:{ all -> 0x0071 }
            r0.add(r5)     // Catch:{ all -> 0x0071 }
        L_0x0049:
            int r5 = r4.size()     // Catch:{ all -> 0x0071 }
            if (r5 != 0) goto L_0x0057
            java.lang.String r5 = r7.getPackageName()     // Catch:{ all -> 0x0071 }
            r3.remove(r5)     // Catch:{ all -> 0x0071 }
            r1 = 1
        L_0x0057:
            monitor-exit(r2)     // Catch:{ all -> 0x0071 }
            r2 = 0
            if (r1 == 0) goto L_0x0063
            java.lang.String r3 = r7.getPackageName()
            r4 = 0
            r6.adjustAutogroupingSummary(r9, r3, r4, r2)
        L_0x0063:
            int r3 = r0.size()
            if (r3 <= 0) goto L_0x006c
            r6.adjustNotificationBundling(r0, r2)
        L_0x006c:
            return
        L_0x006d:
            monitor-exit(r2)     // Catch:{ all -> 0x0071 }
            return
        L_0x006f:
            monitor-exit(r2)     // Catch:{ all -> 0x0071 }
            return
        L_0x0071:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0071 }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.GroupHelper.maybeUngroup(android.service.notification.StatusBarNotification, boolean, int):void");
    }

    private void adjustAutogroupingSummary(int userId, String packageName, String triggeringKey, boolean summaryNeeded) {
        if (summaryNeeded) {
            this.mCallback.addAutoGroupSummary(userId, packageName, triggeringKey);
        } else {
            this.mCallback.removeAutoGroupSummary(userId, packageName);
        }
    }

    private void adjustNotificationBundling(List<String> keys, boolean group) {
        for (String key : keys) {
            if (DEBUG) {
                Log.i(TAG, "Sending grouping adjustment for: " + key + " group? " + group);
            }
            if (group) {
                this.mCallback.addAutoGroup(key);
            } else {
                this.mCallback.removeAutoGroup(key);
            }
        }
    }
}
