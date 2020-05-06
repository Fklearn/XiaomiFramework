package com.android.server.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.metrics.LogMaker;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.Preconditions;
import com.android.server.job.JobSchedulerShellCommand;
import com.android.server.notification.NotificationManagerService;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PreferencesHelper implements RankingConfig {
    private static final String ATT_ALLOW_BUBBLE = "allow_bubble";
    private static final String ATT_APP_USER_LOCKED_FIELDS = "app_user_locked_fields";
    private static final String ATT_ENABLED = "enabled";
    private static final String ATT_HIDE_SILENT = "hide_gentle";
    private static final String ATT_ID = "id";
    private static final String ATT_IMPORTANCE = "importance";
    private static final String ATT_NAME = "name";
    private static final String ATT_PRIORITY = "priority";
    private static final String ATT_SHOW_BADGE = "show_badge";
    private static final String ATT_UID = "uid";
    private static final String ATT_USER_ALLOWED = "allowed";
    private static final String ATT_VERSION = "version";
    private static final String ATT_VISIBILITY = "visibility";
    private static final boolean DEFAULT_ALLOW_BUBBLE = true;
    private static final boolean DEFAULT_APP_LOCKED_IMPORTANCE = false;
    @VisibleForTesting
    static final boolean DEFAULT_HIDE_SILENT_STATUS_BAR_ICONS = false;
    private static final int DEFAULT_IMPORTANCE = -1000;
    private static final int DEFAULT_LOCKED_APP_FIELDS = 0;
    private static final boolean DEFAULT_OEM_LOCKED_IMPORTANCE = false;
    private static final int DEFAULT_PRIORITY = 0;
    private static final boolean DEFAULT_SHOW_BADGE = true;
    private static final int DEFAULT_VISIBILITY = -1000;
    private static final String NON_BLOCKABLE_CHANNEL_DELIM = ":";
    private static final String TAG = "NotificationPrefHelper";
    private static final String TAG_CHANNEL = "channel";
    private static final String TAG_DELEGATE = "delegate";
    private static final String TAG_GROUP = "channelGroup";
    private static final String TAG_PACKAGE = "package";
    @VisibleForTesting
    static final String TAG_RANKING = "ranking";
    private static final String TAG_STATUS_ICONS = "silent_status_icons";
    private static final int UNKNOWN_UID = -10000;
    private static final int XML_VERSION = 1;
    private boolean mAreChannelsBypassingDnd;
    private SparseBooleanArray mBadgingEnabled;
    private SparseBooleanArray mBubblesEnabled;
    private final Context mContext;
    private boolean mHideSilentStatusBarIcons = false;
    private final ArrayMap<String, PackagePreferences> mPackagePreferences = new ArrayMap<>();
    private final PackageManager mPm;
    private final RankingHandler mRankingHandler;
    private final ArrayMap<String, PackagePreferences> mRestoredWithoutUids = new ArrayMap<>();
    private final ZenModeHelper mZenModeHelper;

    public @interface LockableAppFields {
        public static final int USER_LOCKED_BUBBLE = 2;
        public static final int USER_LOCKED_IMPORTANCE = 1;
    }

    public PreferencesHelper(Context context, PackageManager pm, RankingHandler rankingHandler, ZenModeHelper zenHelper) {
        this.mContext = context;
        this.mZenModeHelper = zenHelper;
        this.mRankingHandler = rankingHandler;
        this.mPm = pm;
        updateBadgingEnabled();
        updateBubblesEnabled();
        syncChannelsBypassingDnd(this.mContext.getUserId());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
        deleteDefaultChannelIfNeededLocked(r1);
     */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0115 A[Catch:{ NameNotFoundException -> 0x01db, all -> 0x01f6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x01d7 A[EDGE_INSN: B:92:0x01d7->B:93:? ?: BREAK  , SYNTHETIC, Splitter:B:92:0x01d7] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readXml(org.xmlpull.v1.XmlPullParser r23, boolean r24, int r25) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r22 = this;
            r9 = r22
            r10 = r23
            r11 = r25
            int r1 = r23.getEventType()
            r12 = 2
            if (r1 == r12) goto L_0x000e
            return
        L_0x000e:
            java.lang.String r2 = r23.getName()
            java.lang.String r0 = "ranking"
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x001c
            return
        L_0x001c:
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r13 = r9.mPackagePreferences
            monitor-enter(r13)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r0 = r9.mRestoredWithoutUids     // Catch:{ all -> 0x0223 }
            r0.clear()     // Catch:{ all -> 0x0223 }
        L_0x0024:
            int r0 = r23.next()     // Catch:{ all -> 0x0223 }
            r14 = r0
            r15 = 1
            if (r0 == r15) goto L_0x0217
            java.lang.String r0 = r23.getName()     // Catch:{ all -> 0x0220 }
            r8 = r0
            r7 = 3
            if (r14 != r7) goto L_0x0044
            java.lang.String r0 = "ranking"
            boolean r0 = r0.equals(r8)     // Catch:{ all -> 0x003f }
            if (r0 == 0) goto L_0x0044
            monitor-exit(r13)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r0 = move-exception
            r2 = r8
            r1 = r14
            goto L_0x0224
        L_0x0044:
            if (r14 != r12) goto L_0x020f
            java.lang.String r0 = "silent_status_icons"
            boolean r0 = r0.equals(r8)     // Catch:{ all -> 0x0208 }
            r6 = 0
            if (r0 == 0) goto L_0x0065
            if (r24 == 0) goto L_0x0058
            if (r11 == 0) goto L_0x0058
            r18 = r8
            goto L_0x0211
        L_0x0058:
            java.lang.String r0 = "hide_gentle"
            boolean r0 = com.android.internal.util.XmlUtils.readBooleanAttribute(r10, r0, r6)     // Catch:{ all -> 0x003f }
            r9.mHideSilentStatusBarIcons = r0     // Catch:{ all -> 0x003f }
            r18 = r8
            goto L_0x0211
        L_0x0065:
            java.lang.String r0 = "package"
            boolean r0 = r0.equals(r8)     // Catch:{ all -> 0x0208 }
            if (r0 == 0) goto L_0x0205
            java.lang.String r0 = "uid"
            r5 = -10000(0xffffffffffffd8f0, float:NaN)
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r0, r5)     // Catch:{ all -> 0x0208 }
            r1 = r0
            java.lang.String r0 = "name"
            r4 = 0
            java.lang.String r0 = r10.getAttributeValue(r4, r0)     // Catch:{ all -> 0x0208 }
            r3 = r0
            boolean r0 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x0208 }
            if (r0 != 0) goto L_0x01fb
            if (r24 == 0) goto L_0x0093
            android.content.pm.PackageManager r0 = r9.mPm     // Catch:{ NameNotFoundException -> 0x0092 }
            int r0 = r0.getPackageUidAsUser(r3, r11)     // Catch:{ NameNotFoundException -> 0x0092 }
            r16 = r0
            goto L_0x0095
        L_0x0092:
            r0 = move-exception
        L_0x0093:
            r16 = r1
        L_0x0095:
            java.lang.String r0 = "importance"
            r2 = -1000(0xfffffffffffffc18, float:NaN)
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r0, r2)     // Catch:{ all -> 0x0208 }
            java.lang.String r1 = "priority"
            int r17 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r1, r6)     // Catch:{ all -> 0x0208 }
            java.lang.String r1 = "visibility"
            int r18 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r1, r2)     // Catch:{ all -> 0x0208 }
            java.lang.String r1 = "show_badge"
            boolean r19 = com.android.internal.util.XmlUtils.readBooleanAttribute(r10, r1, r15)     // Catch:{ all -> 0x0208 }
            java.lang.String r1 = "allow_bubble"
            boolean r20 = com.android.internal.util.XmlUtils.readBooleanAttribute(r10, r1, r15)     // Catch:{ all -> 0x0208 }
            r1 = r22
            r12 = r2
            r2 = r3
            r21 = r3
            r3 = r16
            r4 = r0
            r5 = r17
            r15 = r6
            r6 = r18
            r7 = r19
            r18 = r8
            r8 = r20
            com.android.server.notification.PreferencesHelper$PackagePreferences r0 = r1.getOrCreatePackagePreferencesLocked(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x01f6 }
            r1 = r0
            java.lang.String r0 = "importance"
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r0, r12)     // Catch:{ all -> 0x01f6 }
            r1.importance = r0     // Catch:{ all -> 0x01f6 }
            java.lang.String r0 = "priority"
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r0, r15)     // Catch:{ all -> 0x01f6 }
            r1.priority = r0     // Catch:{ all -> 0x01f6 }
            java.lang.String r0 = "visibility"
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r0, r12)     // Catch:{ all -> 0x01f6 }
            r1.visibility = r0     // Catch:{ all -> 0x01f6 }
            java.lang.String r0 = "show_badge"
            r2 = 1
            boolean r0 = com.android.internal.util.XmlUtils.readBooleanAttribute(r10, r0, r2)     // Catch:{ all -> 0x01f6 }
            r1.showBadge = r0     // Catch:{ all -> 0x01f6 }
            java.lang.String r0 = "app_user_locked_fields"
            int r0 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r0, r15)     // Catch:{ all -> 0x01f6 }
            r1.lockedAppFields = r0     // Catch:{ all -> 0x01f6 }
            int r0 = r23.getDepth()     // Catch:{ all -> 0x01f6 }
            r2 = r0
        L_0x0104:
            int r0 = r23.next()     // Catch:{ all -> 0x01f6 }
            r14 = r0
            r3 = 1
            if (r0 == r3) goto L_0x01d7
            r3 = 3
            if (r14 != r3) goto L_0x0115
            int r0 = r23.getDepth()     // Catch:{ all -> 0x01f6 }
            if (r0 <= r2) goto L_0x01d7
        L_0x0115:
            if (r14 == r3) goto L_0x01d2
            r0 = 4
            if (r14 != r0) goto L_0x011e
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
            r15 = 1
            goto L_0x0104
        L_0x011e:
            java.lang.String r0 = r23.getName()     // Catch:{ all -> 0x01f6 }
            java.lang.String r4 = "channelGroup"
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x01f6 }
            if (r4 == 0) goto L_0x014d
            java.lang.String r4 = "id"
            r5 = 0
            java.lang.String r4 = r10.getAttributeValue(r5, r4)     // Catch:{ all -> 0x01f6 }
            java.lang.String r6 = "name"
            java.lang.String r6 = r10.getAttributeValue(r5, r6)     // Catch:{ all -> 0x01f6 }
            boolean r7 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x01f6 }
            if (r7 != 0) goto L_0x014e
            android.app.NotificationChannelGroup r7 = new android.app.NotificationChannelGroup     // Catch:{ all -> 0x01f6 }
            r7.<init>(r4, r6)     // Catch:{ all -> 0x01f6 }
            r7.populateFromXml(r10)     // Catch:{ all -> 0x01f6 }
            java.util.Map<java.lang.String, android.app.NotificationChannelGroup> r8 = r1.groups     // Catch:{ all -> 0x01f6 }
            r8.put(r4, r7)     // Catch:{ all -> 0x01f6 }
            goto L_0x014e
        L_0x014d:
            r5 = 0
        L_0x014e:
            java.lang.String r4 = "channel"
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x01f6 }
            if (r4 == 0) goto L_0x0191
            java.lang.String r4 = "id"
            java.lang.String r4 = r10.getAttributeValue(r5, r4)     // Catch:{ all -> 0x01f6 }
            java.lang.String r6 = "name"
            java.lang.String r6 = r10.getAttributeValue(r5, r6)     // Catch:{ all -> 0x01f6 }
            java.lang.String r7 = "importance"
            int r7 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r7, r12)     // Catch:{ all -> 0x01f6 }
            boolean r8 = android.text.TextUtils.isEmpty(r4)     // Catch:{ all -> 0x01f6 }
            if (r8 != 0) goto L_0x0191
            boolean r8 = android.text.TextUtils.isEmpty(r6)     // Catch:{ all -> 0x01f6 }
            if (r8 != 0) goto L_0x0191
            android.app.NotificationChannel r8 = new android.app.NotificationChannel     // Catch:{ all -> 0x01f6 }
            r8.<init>(r4, r6, r7)     // Catch:{ all -> 0x01f6 }
            if (r24 == 0) goto L_0x0184
            android.content.Context r15 = r9.mContext     // Catch:{ all -> 0x01f6 }
            r8.populateFromXmlForRestore(r10, r15)     // Catch:{ all -> 0x01f6 }
            goto L_0x0187
        L_0x0184:
            r8.populateFromXml(r10)     // Catch:{ all -> 0x01f6 }
        L_0x0187:
            boolean r15 = r1.defaultAppLockedImportance     // Catch:{ all -> 0x01f6 }
            r8.setImportanceLockedByCriticalDeviceFunction(r15)     // Catch:{ all -> 0x01f6 }
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r15 = r1.channels     // Catch:{ all -> 0x01f6 }
            r15.put(r4, r8)     // Catch:{ all -> 0x01f6 }
        L_0x0191:
            java.lang.String r4 = "delegate"
            boolean r4 = r4.equals(r0)     // Catch:{ all -> 0x01f6 }
            if (r4 == 0) goto L_0x01cd
            java.lang.String r4 = "uid"
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
            int r4 = com.android.internal.util.XmlUtils.readIntAttribute(r10, r4, r6)     // Catch:{ all -> 0x01f6 }
            java.lang.String r7 = "name"
            java.lang.String r7 = com.android.internal.util.XmlUtils.readStringAttribute(r10, r7)     // Catch:{ all -> 0x01f6 }
            java.lang.String r8 = "enabled"
            r15 = 1
            boolean r8 = com.android.internal.util.XmlUtils.readBooleanAttribute(r10, r8, r15)     // Catch:{ all -> 0x01f6 }
            java.lang.String r3 = "allowed"
            boolean r3 = com.android.internal.util.XmlUtils.readBooleanAttribute(r10, r3, r15)     // Catch:{ all -> 0x01f6 }
            r17 = 0
            if (r4 == r6) goto L_0x01c8
            boolean r19 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x01f6 }
            if (r19 != 0) goto L_0x01c8
            com.android.server.notification.PreferencesHelper$Delegate r5 = new com.android.server.notification.PreferencesHelper$Delegate     // Catch:{ all -> 0x01f6 }
            r5.<init>(r7, r4, r8, r3)     // Catch:{ all -> 0x01f6 }
            r17 = r5
            goto L_0x01ca
        L_0x01c8:
            r5 = r17
        L_0x01ca:
            r1.delegate = r5     // Catch:{ all -> 0x01f6 }
            goto L_0x01d0
        L_0x01cd:
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
            r15 = 1
        L_0x01d0:
            goto L_0x0104
        L_0x01d2:
            r6 = -10000(0xffffffffffffd8f0, float:NaN)
            r15 = 1
            goto L_0x0104
        L_0x01d7:
            r9.deleteDefaultChannelIfNeededLocked(r1)     // Catch:{ NameNotFoundException -> 0x01db }
            goto L_0x01f4
        L_0x01db:
            r0 = move-exception
            r3 = r0
            r0 = r3
            java.lang.String r3 = "NotificationPrefHelper"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01f6 }
            r4.<init>()     // Catch:{ all -> 0x01f6 }
            java.lang.String r5 = "deleteDefaultChannelIfNeededLocked - Exception: "
            r4.append(r5)     // Catch:{ all -> 0x01f6 }
            r4.append(r0)     // Catch:{ all -> 0x01f6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01f6 }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x01f6 }
        L_0x01f4:
            r1 = r14
            goto L_0x0200
        L_0x01f6:
            r0 = move-exception
            r1 = r14
            r2 = r18
            goto L_0x0224
        L_0x01fb:
            r21 = r3
            r18 = r8
            r1 = r14
        L_0x0200:
            r2 = r18
            r12 = 2
            goto L_0x0024
        L_0x0205:
            r18 = r8
            goto L_0x0211
        L_0x0208:
            r0 = move-exception
            r18 = r8
            r1 = r14
            r2 = r18
            goto L_0x0224
        L_0x020f:
            r18 = r8
        L_0x0211:
            r1 = r14
            r2 = r18
            r12 = 2
            goto L_0x0024
        L_0x0217:
            monitor-exit(r13)     // Catch:{ all -> 0x0220 }
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Failed to reach END_DOCUMENT"
            r0.<init>(r1)
            throw r0
        L_0x0220:
            r0 = move-exception
            r1 = r14
            goto L_0x0224
        L_0x0223:
            r0 = move-exception
        L_0x0224:
            monitor-exit(r13)     // Catch:{ all -> 0x0223 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.readXml(org.xmlpull.v1.XmlPullParser, boolean, int):void");
    }

    private PackagePreferences getPackagePreferencesLocked(String pkg, int uid) {
        return this.mPackagePreferences.get(packagePreferencesKey(pkg, uid));
    }

    private PackagePreferences getOrCreatePackagePreferencesLocked(String pkg, int uid) {
        return getOrCreatePackagePreferencesLocked(pkg, uid, JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE, 0, JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE, true, true);
    }

    private PackagePreferences getOrCreatePackagePreferencesLocked(String pkg, int uid, int importance, int priority, int visibility, boolean showBadge, boolean allowBubble) {
        PackagePreferences r;
        String key = packagePreferencesKey(pkg, uid);
        if (uid == -10000) {
            r = this.mRestoredWithoutUids.get(pkg);
        } else {
            r = this.mPackagePreferences.get(key);
        }
        if (r == null) {
            r = new PackagePreferences();
            r.pkg = pkg;
            r.uid = uid;
            r.importance = importance;
            r.priority = priority;
            r.visibility = visibility;
            r.showBadge = showBadge;
            r.allowBubble = allowBubble;
            try {
                createDefaultChannelIfNeededLocked(r);
            } catch (PackageManager.NameNotFoundException e) {
                Slog.e(TAG, "createDefaultChannelIfNeededLocked - Exception: " + e);
            }
            if (r.uid == -10000) {
                this.mRestoredWithoutUids.put(pkg, r);
            } else {
                this.mPackagePreferences.put(key, r);
            }
        }
        return r;
    }

    private boolean shouldHaveDefaultChannel(PackagePreferences r) throws PackageManager.NameNotFoundException {
        if (this.mPm.getApplicationInfoAsUser(r.pkg, 0, UserHandle.getUserId(r.uid)).targetSdkVersion >= 26) {
            return false;
        }
        return true;
    }

    private boolean deleteDefaultChannelIfNeededLocked(PackagePreferences r) throws PackageManager.NameNotFoundException {
        if (!r.channels.containsKey("miscellaneous") || shouldHaveDefaultChannel(r)) {
            return false;
        }
        r.channels.remove("miscellaneous");
        return true;
    }

    private boolean createDefaultChannelIfNeededLocked(PackagePreferences r) throws PackageManager.NameNotFoundException {
        boolean z = false;
        if (r.channels.containsKey("miscellaneous")) {
            r.channels.get("miscellaneous").setName(this.mContext.getString(17039879));
            return false;
        } else if (!shouldHaveDefaultChannel(r)) {
            return false;
        } else {
            NotificationChannel channel = new NotificationChannel("miscellaneous", this.mContext.getString(17039879), r.importance);
            if (r.priority == 2) {
                z = true;
            }
            channel.setBypassDnd(z);
            channel.setLockscreenVisibility(r.visibility);
            if (r.importance != -1000) {
                channel.lockFields(4);
            }
            if (r.priority != 0) {
                channel.lockFields(1);
            }
            if (r.visibility != -1000) {
                channel.lockFields(2);
            }
            r.channels.put(channel.getId(), channel);
            return true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x018c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeXml(org.xmlpull.v1.XmlSerializer r11, boolean r12, int r13) throws java.io.IOException {
        /*
            r10 = this;
            r0 = 0
            java.lang.String r1 = "ranking"
            r11.startTag(r0, r1)
            r1 = 1
            java.lang.String r2 = java.lang.Integer.toString(r1)
            java.lang.String r3 = "version"
            r11.attribute(r0, r3, r2)
            boolean r2 = r10.mHideSilentStatusBarIcons
            if (r2 == 0) goto L_0x0032
            if (r12 == 0) goto L_0x001a
            if (r13 != 0) goto L_0x0032
        L_0x001a:
            java.lang.String r2 = "silent_status_icons"
            r11.startTag(r0, r2)
            boolean r2 = r10.mHideSilentStatusBarIcons
            java.lang.String r2 = java.lang.String.valueOf(r2)
            java.lang.String r3 = "hide_gentle"
            r11.attribute(r0, r3, r2)
            java.lang.String r2 = "silent_status_icons"
            r11.endTag(r0, r2)
        L_0x0032:
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r2 = r10.mPackagePreferences
            monitor-enter(r2)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r3 = r10.mPackagePreferences     // Catch:{ all -> 0x0198 }
            int r3 = r3.size()     // Catch:{ all -> 0x0198 }
            r4 = 0
        L_0x003c:
            if (r4 >= r3) goto L_0x0190
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r5 = r10.mPackagePreferences     // Catch:{ all -> 0x0198 }
            java.lang.Object r5 = r5.valueAt(r4)     // Catch:{ all -> 0x0198 }
            com.android.server.notification.PreferencesHelper$PackagePreferences r5 = (com.android.server.notification.PreferencesHelper.PackagePreferences) r5     // Catch:{ all -> 0x0198 }
            if (r12 == 0) goto L_0x0052
            int r6 = r5.uid     // Catch:{ all -> 0x0198 }
            int r6 = android.os.UserHandle.getUserId(r6)     // Catch:{ all -> 0x0198 }
            if (r6 == r13) goto L_0x0052
            goto L_0x018c
        L_0x0052:
            int r6 = r5.importance     // Catch:{ all -> 0x0198 }
            r7 = -1000(0xfffffffffffffc18, float:NaN)
            if (r6 != r7) goto L_0x0083
            int r6 = r5.priority     // Catch:{ all -> 0x0198 }
            if (r6 != 0) goto L_0x0083
            int r6 = r5.visibility     // Catch:{ all -> 0x0198 }
            if (r6 != r7) goto L_0x0083
            boolean r6 = r5.showBadge     // Catch:{ all -> 0x0198 }
            if (r6 != r1) goto L_0x0083
            int r6 = r5.lockedAppFields     // Catch:{ all -> 0x0198 }
            if (r6 != 0) goto L_0x0083
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r6 = r5.channels     // Catch:{ all -> 0x0198 }
            int r6 = r6.size()     // Catch:{ all -> 0x0198 }
            if (r6 > 0) goto L_0x0083
            java.util.Map<java.lang.String, android.app.NotificationChannelGroup> r6 = r5.groups     // Catch:{ all -> 0x0198 }
            int r6 = r6.size()     // Catch:{ all -> 0x0198 }
            if (r6 > 0) goto L_0x0083
            com.android.server.notification.PreferencesHelper$Delegate r6 = r5.delegate     // Catch:{ all -> 0x0198 }
            if (r6 != 0) goto L_0x0083
            boolean r6 = r5.allowBubble     // Catch:{ all -> 0x0198 }
            if (r6 == r1) goto L_0x0081
            goto L_0x0083
        L_0x0081:
            r6 = 0
            goto L_0x0084
        L_0x0083:
            r6 = r1
        L_0x0084:
            if (r6 == 0) goto L_0x018c
            java.lang.String r8 = "package"
            r11.startTag(r0, r8)     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = "name"
            java.lang.String r9 = r5.pkg     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r8, r9)     // Catch:{ all -> 0x0198 }
            int r8 = r5.importance     // Catch:{ all -> 0x0198 }
            if (r8 == r7) goto L_0x00a4
            java.lang.String r8 = "importance"
            int r9 = r5.importance     // Catch:{ all -> 0x0198 }
            java.lang.String r9 = java.lang.Integer.toString(r9)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r8, r9)     // Catch:{ all -> 0x0198 }
        L_0x00a4:
            int r8 = r5.priority     // Catch:{ all -> 0x0198 }
            if (r8 == 0) goto L_0x00b4
            java.lang.String r8 = "priority"
            int r9 = r5.priority     // Catch:{ all -> 0x0198 }
            java.lang.String r9 = java.lang.Integer.toString(r9)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r8, r9)     // Catch:{ all -> 0x0198 }
        L_0x00b4:
            int r8 = r5.visibility     // Catch:{ all -> 0x0198 }
            if (r8 == r7) goto L_0x00c4
            java.lang.String r7 = "visibility"
            int r8 = r5.visibility     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Integer.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
        L_0x00c4:
            boolean r7 = r5.allowBubble     // Catch:{ all -> 0x0198 }
            if (r7 == r1) goto L_0x00d3
            java.lang.String r7 = "allow_bubble"
            boolean r8 = r5.allowBubble     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Boolean.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
        L_0x00d3:
            java.lang.String r7 = "show_badge"
            boolean r8 = r5.showBadge     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Boolean.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
            java.lang.String r7 = "app_user_locked_fields"
            int r8 = r5.lockedAppFields     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Integer.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
            if (r12 != 0) goto L_0x00f8
            java.lang.String r7 = "uid"
            int r8 = r5.uid     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Integer.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
        L_0x00f8:
            com.android.server.notification.PreferencesHelper$Delegate r7 = r5.delegate     // Catch:{ all -> 0x0198 }
            if (r7 == 0) goto L_0x0144
            java.lang.String r7 = "delegate"
            r11.startTag(r0, r7)     // Catch:{ all -> 0x0198 }
            java.lang.String r7 = "name"
            com.android.server.notification.PreferencesHelper$Delegate r8 = r5.delegate     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = r8.mPkg     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
            java.lang.String r7 = "uid"
            com.android.server.notification.PreferencesHelper$Delegate r8 = r5.delegate     // Catch:{ all -> 0x0198 }
            int r8 = r8.mUid     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Integer.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
            com.android.server.notification.PreferencesHelper$Delegate r7 = r5.delegate     // Catch:{ all -> 0x0198 }
            boolean r7 = r7.mEnabled     // Catch:{ all -> 0x0198 }
            if (r7 == r1) goto L_0x012c
            java.lang.String r7 = "enabled"
            com.android.server.notification.PreferencesHelper$Delegate r8 = r5.delegate     // Catch:{ all -> 0x0198 }
            boolean r8 = r8.mEnabled     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Boolean.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
        L_0x012c:
            com.android.server.notification.PreferencesHelper$Delegate r7 = r5.delegate     // Catch:{ all -> 0x0198 }
            boolean r7 = r7.mUserAllowed     // Catch:{ all -> 0x0198 }
            if (r7 == r1) goto L_0x013f
            java.lang.String r7 = "allowed"
            com.android.server.notification.PreferencesHelper$Delegate r8 = r5.delegate     // Catch:{ all -> 0x0198 }
            boolean r8 = r8.mUserAllowed     // Catch:{ all -> 0x0198 }
            java.lang.String r8 = java.lang.Boolean.toString(r8)     // Catch:{ all -> 0x0198 }
            r11.attribute(r0, r7, r8)     // Catch:{ all -> 0x0198 }
        L_0x013f:
            java.lang.String r7 = "delegate"
            r11.endTag(r0, r7)     // Catch:{ all -> 0x0198 }
        L_0x0144:
            java.util.Map<java.lang.String, android.app.NotificationChannelGroup> r7 = r5.groups     // Catch:{ all -> 0x0198 }
            java.util.Collection r7 = r7.values()     // Catch:{ all -> 0x0198 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x0198 }
        L_0x014e:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x0198 }
            if (r8 == 0) goto L_0x015e
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x0198 }
            android.app.NotificationChannelGroup r8 = (android.app.NotificationChannelGroup) r8     // Catch:{ all -> 0x0198 }
            r8.writeXml(r11)     // Catch:{ all -> 0x0198 }
            goto L_0x014e
        L_0x015e:
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r7 = r5.channels     // Catch:{ all -> 0x0198 }
            java.util.Collection r7 = r7.values()     // Catch:{ all -> 0x0198 }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x0198 }
        L_0x0168:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x0198 }
            if (r8 == 0) goto L_0x0186
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x0198 }
            android.app.NotificationChannel r8 = (android.app.NotificationChannel) r8     // Catch:{ all -> 0x0198 }
            if (r12 == 0) goto L_0x0182
            boolean r9 = r8.isDeleted()     // Catch:{ all -> 0x0198 }
            if (r9 != 0) goto L_0x0185
            android.content.Context r9 = r10.mContext     // Catch:{ all -> 0x0198 }
            r8.writeXmlForBackup(r11, r9)     // Catch:{ all -> 0x0198 }
            goto L_0x0185
        L_0x0182:
            r8.writeXml(r11)     // Catch:{ all -> 0x0198 }
        L_0x0185:
            goto L_0x0168
        L_0x0186:
            java.lang.String r7 = "package"
            r11.endTag(r0, r7)     // Catch:{ all -> 0x0198 }
        L_0x018c:
            int r4 = r4 + 1
            goto L_0x003c
        L_0x0190:
            monitor-exit(r2)     // Catch:{ all -> 0x0198 }
            java.lang.String r1 = "ranking"
            r11.endTag(r0, r1)
            return
        L_0x0198:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0198 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.writeXml(org.xmlpull.v1.XmlSerializer, boolean, int):void");
    }

    public void setBubblesAllowed(String pkg, int uid, boolean allowed) {
        boolean changed;
        synchronized (this.mPackagePreferences) {
            PackagePreferences p = getOrCreatePackagePreferencesLocked(pkg, uid);
            changed = p.allowBubble != allowed;
            p.allowBubble = allowed;
            p.lockedAppFields |= 2;
        }
        if (changed) {
            updateConfig();
        }
    }

    public boolean areBubblesAllowed(String pkg, int uid) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(pkg, uid).allowBubble;
        }
        return z;
    }

    public int getAppLockedFields(String pkg, int uid) {
        int i;
        synchronized (this.mPackagePreferences) {
            i = getOrCreatePackagePreferencesLocked(pkg, uid).lockedAppFields;
        }
        return i;
    }

    public int getImportance(String packageName, int uid) {
        int i;
        synchronized (this.mPackagePreferences) {
            i = getOrCreatePackagePreferencesLocked(packageName, uid).importance;
        }
        return i;
    }

    public boolean getIsAppImportanceLocked(String packageName, int uid) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = (getOrCreatePackagePreferencesLocked(packageName, uid).lockedAppFields & 1) != 0;
        }
        return z;
    }

    public boolean canShowBadge(String packageName, int uid) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            z = getOrCreatePackagePreferencesLocked(packageName, uid).showBadge;
        }
        return z;
    }

    public void setShowBadge(String packageName, int uid, boolean showBadge) {
        synchronized (this.mPackagePreferences) {
            getOrCreatePackagePreferencesLocked(packageName, uid).showBadge = showBadge;
        }
        updateConfig();
    }

    public boolean isGroupBlocked(String packageName, int uid, String groupId) {
        if (groupId == null) {
            return false;
        }
        synchronized (this.mPackagePreferences) {
            NotificationChannelGroup group = getOrCreatePackagePreferencesLocked(packageName, uid).groups.get(groupId);
            if (group == null) {
                return false;
            }
            boolean isBlocked = group.isBlocked();
            return isBlocked;
        }
    }

    /* access modifiers changed from: package-private */
    public int getPackagePriority(String pkg, int uid) {
        int i;
        synchronized (this.mPackagePreferences) {
            i = getOrCreatePackagePreferencesLocked(pkg, uid).priority;
        }
        return i;
    }

    /* access modifiers changed from: package-private */
    public int getPackageVisibility(String pkg, int uid) {
        int i;
        synchronized (this.mPackagePreferences) {
            i = getOrCreatePackagePreferencesLocked(pkg, uid).visibility;
        }
        return i;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void createNotificationChannelGroup(String pkg, int uid, NotificationChannelGroup group, boolean fromTargetApp) {
        Preconditions.checkNotNull(pkg);
        Preconditions.checkNotNull(group);
        Preconditions.checkNotNull(group.getId());
        Preconditions.checkNotNull(Boolean.valueOf(!TextUtils.isEmpty(group.getName())));
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getOrCreatePackagePreferencesLocked(pkg, uid);
            if (r != null) {
                NotificationChannelGroup oldGroup = r.groups.get(group.getId());
                if (!group.equals(oldGroup)) {
                    MetricsLogger.action(getChannelGroupLog(group.getId(), pkg));
                }
                if (oldGroup != null) {
                    group.setChannels(oldGroup.getChannels());
                    if (fromTargetApp) {
                        group.setBlocked(oldGroup.isBlocked());
                        group.unlockFields(group.getUserLockedFields());
                        group.lockFields(oldGroup.getUserLockedFields());
                    } else if (group.isBlocked() != oldGroup.isBlocked()) {
                        group.lockFields(1);
                        updateChannelsBypassingDnd(this.mContext.getUserId());
                    }
                }
                r.groups.put(group.getId(), group);
            } else {
                throw new IllegalArgumentException("Invalid package");
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public boolean createNotificationChannel(String pkg, int uid, NotificationChannel channel, boolean fromTargetApp, boolean hasDndAccess) {
        boolean bypassDnd;
        Preconditions.checkNotNull(pkg);
        Preconditions.checkNotNull(channel);
        Preconditions.checkNotNull(channel.getId());
        Preconditions.checkArgument(!TextUtils.isEmpty(channel.getName()));
        boolean needsPolicyFileChange = false;
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getOrCreatePackagePreferencesLocked(pkg, uid);
            if (r != null) {
                if (channel.getGroup() != null) {
                    if (!r.groups.containsKey(channel.getGroup())) {
                        throw new IllegalArgumentException("NotificationChannelGroup doesn't exist");
                    }
                }
                if (!"miscellaneous".equals(channel.getId())) {
                    NotificationChannel existing = r.channels.get(channel.getId());
                    if (existing != null && fromTargetApp) {
                        if (existing.isDeleted()) {
                            existing.setDeleted(false);
                            needsPolicyFileChange = true;
                            MetricsLogger.action(getChannelLog(channel, pkg).setType(1));
                        }
                        if (!Objects.equals(channel.getName().toString(), existing.getName().toString())) {
                            existing.setName(channel.getName().toString());
                            needsPolicyFileChange = true;
                        }
                        if (!Objects.equals(channel.getDescription(), existing.getDescription())) {
                            existing.setDescription(channel.getDescription());
                            needsPolicyFileChange = true;
                        }
                        if (channel.isBlockableSystem() != existing.isBlockableSystem()) {
                            existing.setBlockableSystem(channel.isBlockableSystem());
                            needsPolicyFileChange = true;
                        }
                        if (channel.getGroup() != null && existing.getGroup() == null) {
                            existing.setGroup(channel.getGroup());
                            needsPolicyFileChange = true;
                        }
                        int previousExistingImportance = existing.getImportance();
                        if (existing.getUserLockedFields() == 0 && channel.getImportance() < existing.getImportance()) {
                            existing.setImportance(channel.getImportance());
                            needsPolicyFileChange = true;
                        }
                        if (existing.getUserLockedFields() == 0 && hasDndAccess && (bypassDnd = channel.canBypassDnd()) != existing.canBypassDnd()) {
                            existing.setBypassDnd(bypassDnd);
                            needsPolicyFileChange = true;
                            if (!(bypassDnd == this.mAreChannelsBypassingDnd && previousExistingImportance == existing.getImportance())) {
                                updateChannelsBypassingDnd(this.mContext.getUserId());
                            }
                        }
                        updateConfig();
                        return needsPolicyFileChange;
                    } else if (channel.getImportance() < 0 || channel.getImportance() > 5) {
                        throw new IllegalArgumentException("Invalid importance level");
                    } else {
                        if (fromTargetApp && !hasDndAccess) {
                            channel.setBypassDnd(r.priority == 2);
                        }
                        if (fromTargetApp) {
                            channel.setLockscreenVisibility(r.visibility);
                        }
                        clearLockedFieldsLocked(channel);
                        channel.setImportanceLockedByOEM(r.oemLockedImportance);
                        if (!channel.isImportanceLockedByOEM() && r.futureOemLockedChannels.remove(channel.getId())) {
                            channel.setImportanceLockedByOEM(true);
                        }
                        channel.setImportanceLockedByCriticalDeviceFunction(r.defaultAppLockedImportance);
                        if (channel.getLockscreenVisibility() == 1) {
                            channel.setLockscreenVisibility(JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE);
                        }
                        if (!r.showBadge) {
                            channel.setShowBadge(false);
                        }
                        r.channels.put(channel.getId(), channel);
                        if (channel.canBypassDnd() != this.mAreChannelsBypassingDnd) {
                            updateChannelsBypassingDnd(this.mContext.getUserId());
                        }
                        MetricsLogger.action(getChannelLog(channel, pkg).setType(1));
                        return true;
                    }
                } else {
                    throw new IllegalArgumentException("Reserved id");
                }
            } else {
                throw new IllegalArgumentException("Invalid package");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearLockedFieldsLocked(NotificationChannel channel) {
        channel.unlockFields(channel.getUserLockedFields());
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void updateNotificationChannel(String pkg, int uid, NotificationChannel updatedChannel, boolean fromUser) {
        Preconditions.checkNotNull(updatedChannel);
        Preconditions.checkNotNull(updatedChannel.getId());
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getOrCreatePackagePreferencesLocked(pkg, uid);
            if (r != null) {
                NotificationChannel channel = r.channels.get(updatedChannel.getId());
                if (channel == null || channel.isDeleted()) {
                    throw new IllegalArgumentException("Channel does not exist");
                }
                int i = 1;
                if (updatedChannel.getLockscreenVisibility() == 1) {
                    updatedChannel.setLockscreenVisibility(JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE);
                }
                if (fromUser) {
                    updatedChannel.lockFields(channel.getUserLockedFields());
                    lockFieldsForUpdateLocked(channel, updatedChannel);
                } else {
                    updatedChannel.unlockFields(updatedChannel.getUserLockedFields());
                }
                updatedChannel.setImportanceLockedByOEM(channel.isImportanceLockedByOEM());
                if (updatedChannel.isImportanceLockedByOEM()) {
                    updatedChannel.setImportance(channel.getImportance());
                }
                updatedChannel.setImportanceLockedByCriticalDeviceFunction(r.defaultAppLockedImportance);
                if (updatedChannel.isImportanceLockedByCriticalDeviceFunction() && updatedChannel.getImportance() == 0) {
                    updatedChannel.setImportance(channel.getImportance());
                }
                r.channels.put(updatedChannel.getId(), updatedChannel);
                if (onlyHasDefaultChannel(pkg, uid)) {
                    r.importance = updatedChannel.getImportance();
                    r.priority = updatedChannel.canBypassDnd() ? 2 : 0;
                    r.visibility = updatedChannel.getLockscreenVisibility();
                    r.showBadge = updatedChannel.canShowBadge();
                }
                if (!channel.equals(updatedChannel)) {
                    LogMaker channelLog = getChannelLog(updatedChannel, pkg);
                    if (!fromUser) {
                        i = 0;
                    }
                    MetricsLogger.action(channelLog.setSubtype(i));
                }
                if (!(updatedChannel.canBypassDnd() == this.mAreChannelsBypassingDnd && channel.getImportance() == updatedChannel.getImportance())) {
                    updateChannelsBypassingDnd(this.mContext.getUserId());
                }
            } else {
                throw new IllegalArgumentException("Invalid package");
            }
        }
        updateConfig();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002a, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.NotificationChannel getNotificationChannel(java.lang.String r6, int r7, java.lang.String r8, boolean r9) {
        /*
            r5 = this;
            com.android.internal.util.Preconditions.checkNotNull(r6)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r0 = r5.mPackagePreferences
            monitor-enter(r0)
            com.android.server.notification.PreferencesHelper$PackagePreferences r1 = r5.getOrCreatePackagePreferencesLocked(r6, r7)     // Catch:{ all -> 0x002b }
            r2 = 0
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            return r2
        L_0x000f:
            if (r8 != 0) goto L_0x0015
            java.lang.String r3 = "miscellaneous"
            r8 = r3
        L_0x0015:
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r3 = r1.channels     // Catch:{ all -> 0x002b }
            java.lang.Object r3 = r3.get(r8)     // Catch:{ all -> 0x002b }
            android.app.NotificationChannel r3 = (android.app.NotificationChannel) r3     // Catch:{ all -> 0x002b }
            if (r3 == 0) goto L_0x0029
            if (r9 != 0) goto L_0x0027
            boolean r4 = r3.isDeleted()     // Catch:{ all -> 0x002b }
            if (r4 != 0) goto L_0x0029
        L_0x0027:
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            return r3
        L_0x0029:
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            return r2
        L_0x002b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.getNotificationChannel(java.lang.String, int, java.lang.String, boolean):android.app.NotificationChannel");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0038, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deleteNotificationChannel(java.lang.String r6, int r7, java.lang.String r8) {
        /*
            r5 = this;
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r0 = r5.mPackagePreferences
            monitor-enter(r0)
            com.android.server.notification.PreferencesHelper$PackagePreferences r1 = r5.getPackagePreferencesLocked(r6, r7)     // Catch:{ all -> 0x0039 }
            if (r1 != 0) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            return
        L_0x000b:
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r2 = r1.channels     // Catch:{ all -> 0x0039 }
            java.lang.Object r2 = r2.get(r8)     // Catch:{ all -> 0x0039 }
            android.app.NotificationChannel r2 = (android.app.NotificationChannel) r2     // Catch:{ all -> 0x0039 }
            if (r2 == 0) goto L_0x0037
            r3 = 1
            r2.setDeleted(r3)     // Catch:{ all -> 0x0039 }
            android.metrics.LogMaker r3 = r5.getChannelLog(r2, r6)     // Catch:{ all -> 0x0039 }
            r4 = 2
            r3.setType(r4)     // Catch:{ all -> 0x0039 }
            com.android.internal.logging.MetricsLogger.action(r3)     // Catch:{ all -> 0x0039 }
            boolean r4 = r5.mAreChannelsBypassingDnd     // Catch:{ all -> 0x0039 }
            if (r4 == 0) goto L_0x0037
            boolean r4 = r2.canBypassDnd()     // Catch:{ all -> 0x0039 }
            if (r4 == 0) goto L_0x0037
            android.content.Context r4 = r5.mContext     // Catch:{ all -> 0x0039 }
            int r4 = r4.getUserId()     // Catch:{ all -> 0x0039 }
            r5.updateChannelsBypassingDnd(r4)     // Catch:{ all -> 0x0039 }
        L_0x0037:
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            return
        L_0x0039:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0039 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.deleteNotificationChannel(java.lang.String, int, java.lang.String):void");
    }

    @VisibleForTesting
    public void permanentlyDeleteNotificationChannel(String pkg, int uid, String channelId) {
        Preconditions.checkNotNull(pkg);
        Preconditions.checkNotNull(channelId);
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r != null) {
                r.channels.remove(channelId);
            }
        }
    }

    public void permanentlyDeleteNotificationChannels(String pkg, int uid) {
        Preconditions.checkNotNull(pkg);
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r != null) {
                int size = r.channels.size() - 1;
                for (String key : r.channels.keySet()) {
                    if (!"miscellaneous".equals(key)) {
                        r.channels.remove(key);
                    }
                }
            }
        }
    }

    public boolean shouldHideSilentStatusIcons() {
        return this.mHideSilentStatusBarIcons;
    }

    public void setHideSilentStatusIcons(boolean hide) {
        this.mHideSilentStatusBarIcons = hide;
    }

    public void lockChannelsForOEM(String[] appOrChannelList) {
        String[] appSplit;
        if (appOrChannelList != null) {
            for (String appOrChannel : appOrChannelList) {
                if (!TextUtils.isEmpty(appOrChannel) && (appSplit = appOrChannel.split(NON_BLOCKABLE_CHANNEL_DELIM)) != null && appSplit.length > 0) {
                    String appName = appSplit[0];
                    String channelId = appSplit.length == 2 ? appSplit[1] : null;
                    synchronized (this.mPackagePreferences) {
                        for (PackagePreferences r : this.mPackagePreferences.values()) {
                            if (r.pkg.equals(appName)) {
                                if (channelId == null) {
                                    r.oemLockedImportance = true;
                                    for (NotificationChannel channel : r.channels.values()) {
                                        channel.setImportanceLockedByOEM(true);
                                    }
                                } else {
                                    NotificationChannel channel2 = r.channels.get(channelId);
                                    if (channel2 != null) {
                                        channel2.setImportanceLockedByOEM(true);
                                    } else {
                                        r.futureOemLockedChannels.add(channelId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateDefaultApps(int userId, ArraySet<String> toRemove, ArraySet<Pair<String, Integer>> toAdd) {
        synchronized (this.mPackagePreferences) {
            for (PackagePreferences p : this.mPackagePreferences.values()) {
                if (userId == UserHandle.getUserId(p.uid) && toRemove != null && toRemove.contains(p.pkg)) {
                    p.defaultAppLockedImportance = false;
                    for (NotificationChannel channel : p.channels.values()) {
                        channel.setImportanceLockedByCriticalDeviceFunction(false);
                    }
                }
            }
            if (toAdd != null) {
                Iterator<Pair<String, Integer>> it = toAdd.iterator();
                while (it.hasNext()) {
                    Pair<String, Integer> approvedApp = it.next();
                    PackagePreferences p2 = getOrCreatePackagePreferencesLocked((String) approvedApp.first, ((Integer) approvedApp.second).intValue());
                    p2.defaultAppLockedImportance = true;
                    for (NotificationChannel channel2 : p2.channels.values()) {
                        channel2.setImportanceLockedByCriticalDeviceFunction(true);
                    }
                }
            }
        }
    }

    public NotificationChannelGroup getNotificationChannelGroupWithChannels(String pkg, int uid, String groupId, boolean includeDeleted) {
        Preconditions.checkNotNull(pkg);
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (!(r == null || groupId == null)) {
                if (r.groups.containsKey(groupId)) {
                    NotificationChannelGroup group = r.groups.get(groupId).clone();
                    group.setChannels(new ArrayList());
                    for (NotificationChannel nc : r.channels.values()) {
                        if ((includeDeleted || !nc.isDeleted()) && groupId.equals(nc.getGroup())) {
                            group.addChannel(nc);
                        }
                    }
                    return group;
                }
            }
            return null;
        }
    }

    public NotificationChannelGroup getNotificationChannelGroup(String groupId, String pkg, int uid) {
        Preconditions.checkNotNull(pkg);
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r == null) {
                return null;
            }
            NotificationChannelGroup notificationChannelGroup = r.groups.get(groupId);
            return notificationChannelGroup;
        }
    }

    public ParceledListSlice<NotificationChannelGroup> getNotificationChannelGroups(String pkg, int uid, boolean includeDeleted, boolean includeNonGrouped, boolean includeEmpty) {
        Preconditions.checkNotNull(pkg);
        Map<String, NotificationChannelGroup> groups = new ArrayMap<>();
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r == null) {
                ParceledListSlice<NotificationChannelGroup> emptyList = ParceledListSlice.emptyList();
                return emptyList;
            }
            NotificationChannelGroup nonGrouped = new NotificationChannelGroup((String) null, (CharSequence) null);
            for (NotificationChannel nc : r.channels.values()) {
                if (includeDeleted || !nc.isDeleted()) {
                    if (nc.getGroup() == null) {
                        nonGrouped.addChannel(nc);
                    } else if (r.groups.get(nc.getGroup()) != null) {
                        NotificationChannelGroup ncg = groups.get(nc.getGroup());
                        if (ncg == null) {
                            ncg = r.groups.get(nc.getGroup()).clone();
                            ncg.setChannels(new ArrayList());
                            groups.put(nc.getGroup(), ncg);
                        }
                        ncg.addChannel(nc);
                    }
                }
            }
            if (includeNonGrouped && nonGrouped.getChannels().size() > 0) {
                groups.put((Object) null, nonGrouped);
            }
            if (includeEmpty) {
                for (NotificationChannelGroup group : r.groups.values()) {
                    if (!groups.containsKey(group.getId())) {
                        groups.put(group.getId(), group);
                    }
                }
            }
            ParceledListSlice<NotificationChannelGroup> parceledListSlice = new ParceledListSlice<>(new ArrayList(groups.values()));
            return parceledListSlice;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0045, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<android.app.NotificationChannel> deleteNotificationChannelGroup(java.lang.String r7, int r8, java.lang.String r9) {
        /*
            r6 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r1 = r6.mPackagePreferences
            monitor-enter(r1)
            com.android.server.notification.PreferencesHelper$PackagePreferences r2 = r6.getPackagePreferencesLocked(r7, r8)     // Catch:{ all -> 0x0046 }
            if (r2 == 0) goto L_0x0044
            boolean r3 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x0046 }
            if (r3 == 0) goto L_0x0015
            goto L_0x0044
        L_0x0015:
            java.util.Map<java.lang.String, android.app.NotificationChannelGroup> r3 = r2.groups     // Catch:{ all -> 0x0046 }
            r3.remove(r9)     // Catch:{ all -> 0x0046 }
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r3 = r2.channels     // Catch:{ all -> 0x0046 }
            java.util.Collection r3 = r3.values()     // Catch:{ all -> 0x0046 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0046 }
        L_0x0024:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0046 }
            if (r4 == 0) goto L_0x0042
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0046 }
            android.app.NotificationChannel r4 = (android.app.NotificationChannel) r4     // Catch:{ all -> 0x0046 }
            java.lang.String r5 = r4.getGroup()     // Catch:{ all -> 0x0046 }
            boolean r5 = r9.equals(r5)     // Catch:{ all -> 0x0046 }
            if (r5 == 0) goto L_0x0041
            r5 = 1
            r4.setDeleted(r5)     // Catch:{ all -> 0x0046 }
            r0.add(r4)     // Catch:{ all -> 0x0046 }
        L_0x0041:
            goto L_0x0024
        L_0x0042:
            monitor-exit(r1)     // Catch:{ all -> 0x0046 }
            return r0
        L_0x0044:
            monitor-exit(r1)     // Catch:{ all -> 0x0046 }
            return r0
        L_0x0046:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0046 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.deleteNotificationChannelGroup(java.lang.String, int, java.lang.String):java.util.List");
    }

    public Collection<NotificationChannelGroup> getNotificationChannelGroups(String pkg, int uid) {
        List<NotificationChannelGroup> groups = new ArrayList<>();
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r == null) {
                return groups;
            }
            groups.addAll(r.groups.values());
            return groups;
        }
    }

    public ParceledListSlice<NotificationChannel> getNotificationChannels(String pkg, int uid, boolean includeDeleted) {
        Preconditions.checkNotNull(pkg);
        List<NotificationChannel> channels = new ArrayList<>();
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r == null) {
                ParceledListSlice<NotificationChannel> emptyList = ParceledListSlice.emptyList();
                return emptyList;
            }
            for (NotificationChannel nc : r.channels.values()) {
                if (includeDeleted || !nc.isDeleted()) {
                    channels.add(nc);
                }
            }
            ParceledListSlice<NotificationChannel> parceledListSlice = new ParceledListSlice<>(channels);
            return parceledListSlice;
        }
    }

    public ParceledListSlice<NotificationChannel> getNotificationChannelsBypassingDnd(String pkg, int userId) {
        List<NotificationChannel> channels = new ArrayList<>();
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = this.mPackagePreferences.get(packagePreferencesKey(pkg, userId));
            if (!(r == null || r.importance == 0)) {
                for (NotificationChannel channel : r.channels.values()) {
                    if (channelIsLiveLocked(r, channel) && channel.canBypassDnd()) {
                        channels.add(channel);
                    }
                }
            }
        }
        return new ParceledListSlice<>(channels);
    }

    public boolean onlyHasDefaultChannel(String pkg, int uid) {
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getOrCreatePackagePreferencesLocked(pkg, uid);
            if (r.channels.size() != 1 || !r.channels.containsKey("miscellaneous")) {
                return false;
            }
            return true;
        }
    }

    public int getDeletedChannelCount(String pkg, int uid) {
        Preconditions.checkNotNull(pkg);
        int deletedCount = 0;
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r == null) {
                return 0;
            }
            for (NotificationChannel nc : r.channels.values()) {
                if (nc.isDeleted()) {
                    deletedCount++;
                }
            }
            return deletedCount;
        }
    }

    public int getBlockedChannelCount(String pkg, int uid) {
        Preconditions.checkNotNull(pkg);
        int blockedCount = 0;
        synchronized (this.mPackagePreferences) {
            PackagePreferences r = getPackagePreferencesLocked(pkg, uid);
            if (r == null) {
                return 0;
            }
            for (NotificationChannel nc : r.channels.values()) {
                if (!nc.isDeleted() && nc.getImportance() == 0) {
                    blockedCount++;
                }
            }
            return blockedCount;
        }
    }

    public int getBlockedAppCount(int userId) {
        int count = 0;
        synchronized (this.mPackagePreferences) {
            int N = this.mPackagePreferences.size();
            for (int i = 0; i < N; i++) {
                PackagePreferences r = this.mPackagePreferences.valueAt(i);
                if (userId == UserHandle.getUserId(r.uid) && r.importance == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getAppsBypassingDndCount(int userId) {
        int count = 0;
        synchronized (this.mPackagePreferences) {
            int numPackagePreferences = this.mPackagePreferences.size();
            for (int i = 0; i < numPackagePreferences; i++) {
                PackagePreferences r = this.mPackagePreferences.valueAt(i);
                if (userId == UserHandle.getUserId(r.uid)) {
                    if (r.importance != 0) {
                        Iterator<NotificationChannel> it = r.channels.values().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            NotificationChannel channel = it.next();
                            if (channelIsLiveLocked(r, channel) && channel.canBypassDnd()) {
                                count++;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    private void syncChannelsBypassingDnd(int userId) {
        boolean z = true;
        if ((this.mZenModeHelper.getNotificationPolicy().state & 1) != 1) {
            z = false;
        }
        this.mAreChannelsBypassingDnd = z;
        updateChannelsBypassingDnd(userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x004e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0056, code lost:
        if (r7.mAreChannelsBypassingDnd == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0058, code lost:
        r7.mAreChannelsBypassingDnd = false;
        updateZenPolicy(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateChannelsBypassingDnd(int r8) {
        /*
            r7 = this;
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r0 = r7.mPackagePreferences
            monitor-enter(r0)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r1 = r7.mPackagePreferences     // Catch:{ all -> 0x005f }
            int r1 = r1.size()     // Catch:{ all -> 0x005f }
            r2 = 0
        L_0x000a:
            if (r2 >= r1) goto L_0x0053
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r3 = r7.mPackagePreferences     // Catch:{ all -> 0x005f }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x005f }
            com.android.server.notification.PreferencesHelper$PackagePreferences r3 = (com.android.server.notification.PreferencesHelper.PackagePreferences) r3     // Catch:{ all -> 0x005f }
            int r4 = r3.uid     // Catch:{ all -> 0x005f }
            int r4 = android.os.UserHandle.getUserId(r4)     // Catch:{ all -> 0x005f }
            if (r8 != r4) goto L_0x0050
            int r4 = r3.importance     // Catch:{ all -> 0x005f }
            if (r4 != 0) goto L_0x0021
            goto L_0x0050
        L_0x0021:
            android.util.ArrayMap<java.lang.String, android.app.NotificationChannel> r4 = r3.channels     // Catch:{ all -> 0x005f }
            java.util.Collection r4 = r4.values()     // Catch:{ all -> 0x005f }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x005f }
        L_0x002b:
            boolean r5 = r4.hasNext()     // Catch:{ all -> 0x005f }
            if (r5 == 0) goto L_0x0050
            java.lang.Object r5 = r4.next()     // Catch:{ all -> 0x005f }
            android.app.NotificationChannel r5 = (android.app.NotificationChannel) r5     // Catch:{ all -> 0x005f }
            boolean r6 = r7.channelIsLiveLocked(r3, r5)     // Catch:{ all -> 0x005f }
            if (r6 == 0) goto L_0x004f
            boolean r6 = r5.canBypassDnd()     // Catch:{ all -> 0x005f }
            if (r6 == 0) goto L_0x004f
            boolean r4 = r7.mAreChannelsBypassingDnd     // Catch:{ all -> 0x005f }
            if (r4 != 0) goto L_0x004d
            r4 = 1
            r7.mAreChannelsBypassingDnd = r4     // Catch:{ all -> 0x005f }
            r7.updateZenPolicy(r4)     // Catch:{ all -> 0x005f }
        L_0x004d:
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            return
        L_0x004f:
            goto L_0x002b
        L_0x0050:
            int r2 = r2 + 1
            goto L_0x000a
        L_0x0053:
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            boolean r0 = r7.mAreChannelsBypassingDnd
            if (r0 == 0) goto L_0x005e
            r0 = 0
            r7.mAreChannelsBypassingDnd = r0
            r7.updateZenPolicy(r0)
        L_0x005e:
            return
        L_0x005f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x005f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.updateChannelsBypassingDnd(int):void");
    }

    private boolean channelIsLiveLocked(PackagePreferences pkgPref, NotificationChannel channel) {
        if (!isGroupBlocked(pkgPref.pkg, pkgPref.uid, channel.getGroup()) && !channel.isDeleted() && channel.getImportance() != 0) {
            return true;
        }
        return false;
    }

    public void updateZenPolicy(boolean areChannelsBypassingDnd) {
        int i;
        NotificationManager.Policy policy = this.mZenModeHelper.getNotificationPolicy();
        ZenModeHelper zenModeHelper = this.mZenModeHelper;
        int i2 = policy.priorityCategories;
        int i3 = policy.priorityCallSenders;
        int i4 = policy.priorityMessageSenders;
        int i5 = policy.suppressedVisualEffects;
        if (areChannelsBypassingDnd) {
            i = 1;
        } else {
            i = 0;
        }
        zenModeHelper.setNotificationPolicy(new NotificationManager.Policy(i2, i3, i4, i5, i));
    }

    public boolean areChannelsBypassingDnd() {
        return this.mAreChannelsBypassingDnd;
    }

    public void setImportance(String pkgName, int uid, int importance) {
        synchronized (this.mPackagePreferences) {
            getOrCreatePackagePreferencesLocked(pkgName, uid).importance = importance;
        }
        updateConfig();
    }

    public void setEnabled(String packageName, int uid, boolean enabled) {
        int i = 0;
        if ((getImportance(packageName, uid) != 0) != enabled) {
            if (enabled) {
                i = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            }
            setImportance(packageName, uid, i);
        }
    }

    public void setAppImportanceLocked(String packageName, int uid) {
        synchronized (this.mPackagePreferences) {
            PackagePreferences prefs = getOrCreatePackagePreferencesLocked(packageName, uid);
            if ((prefs.lockedAppFields & 1) == 0) {
                prefs.lockedAppFields |= 1;
                updateConfig();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0023, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0025, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getNotificationDelegate(java.lang.String r5, int r6) {
        /*
            r4 = this;
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r0 = r4.mPackagePreferences
            monitor-enter(r0)
            com.android.server.notification.PreferencesHelper$PackagePreferences r1 = r4.getPackagePreferencesLocked(r5, r6)     // Catch:{ all -> 0x0026 }
            r2 = 0
            if (r1 == 0) goto L_0x0024
            com.android.server.notification.PreferencesHelper$Delegate r3 = r1.delegate     // Catch:{ all -> 0x0026 }
            if (r3 != 0) goto L_0x000f
            goto L_0x0024
        L_0x000f:
            com.android.server.notification.PreferencesHelper$Delegate r3 = r1.delegate     // Catch:{ all -> 0x0026 }
            boolean r3 = r3.mUserAllowed     // Catch:{ all -> 0x0026 }
            if (r3 == 0) goto L_0x0022
            com.android.server.notification.PreferencesHelper$Delegate r3 = r1.delegate     // Catch:{ all -> 0x0026 }
            boolean r3 = r3.mEnabled     // Catch:{ all -> 0x0026 }
            if (r3 != 0) goto L_0x001c
            goto L_0x0022
        L_0x001c:
            com.android.server.notification.PreferencesHelper$Delegate r2 = r1.delegate     // Catch:{ all -> 0x0026 }
            java.lang.String r2 = r2.mPkg     // Catch:{ all -> 0x0026 }
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return r2
        L_0x0022:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return r2
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return r2
        L_0x0026:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.getNotificationDelegate(java.lang.String, int):java.lang.String");
    }

    public void setNotificationDelegate(String sourcePkg, int sourceUid, String delegatePkg, int delegateUid) {
        boolean userAllowed;
        synchronized (this.mPackagePreferences) {
            PackagePreferences prefs = getOrCreatePackagePreferencesLocked(sourcePkg, sourceUid);
            if (prefs.delegate != null) {
                if (!prefs.delegate.mUserAllowed) {
                    userAllowed = false;
                    prefs.delegate = new Delegate(delegatePkg, delegateUid, true, userAllowed);
                }
            }
            userAllowed = true;
            prefs.delegate = new Delegate(delegatePkg, delegateUid, true, userAllowed);
        }
        updateConfig();
    }

    public void revokeNotificationDelegate(String sourcePkg, int sourceUid) {
        boolean changed = false;
        synchronized (this.mPackagePreferences) {
            PackagePreferences prefs = getPackagePreferencesLocked(sourcePkg, sourceUid);
            if (!(prefs == null || prefs.delegate == null)) {
                prefs.delegate.mEnabled = false;
                changed = true;
            }
        }
        if (changed) {
            updateConfig();
        }
    }

    public void toggleNotificationDelegate(String sourcePkg, int sourceUid, boolean userAllowed) {
        boolean changed = false;
        synchronized (this.mPackagePreferences) {
            PackagePreferences prefs = getPackagePreferencesLocked(sourcePkg, sourceUid);
            if (!(prefs == null || prefs.delegate == null)) {
                prefs.delegate.mUserAllowed = userAllowed;
                changed = true;
            }
        }
        if (changed) {
            updateConfig();
        }
    }

    public boolean isDelegateAllowed(String sourcePkg, int sourceUid, String potentialDelegatePkg, int potentialDelegateUid) {
        boolean z;
        synchronized (this.mPackagePreferences) {
            PackagePreferences prefs = getPackagePreferencesLocked(sourcePkg, sourceUid);
            z = prefs != null && prefs.isValidDelegate(potentialDelegatePkg, potentialDelegateUid);
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void lockFieldsForUpdateLocked(NotificationChannel original, NotificationChannel update) {
        if (original.canBypassDnd() != update.canBypassDnd()) {
            update.lockFields(1);
        }
        if (original.getLockscreenVisibility() != update.getLockscreenVisibility()) {
            update.lockFields(2);
        }
        if (original.getImportance() != update.getImportance()) {
            update.lockFields(4);
        }
        if (!(original.shouldShowLights() == update.shouldShowLights() && original.getLightColor() == update.getLightColor())) {
            update.lockFields(8);
        }
        if (!Objects.equals(original.getSound(), update.getSound())) {
            update.lockFields(32);
        }
        if (!Arrays.equals(original.getVibrationPattern(), update.getVibrationPattern()) || original.shouldVibrate() != update.shouldVibrate()) {
            update.lockFields(16);
        }
        if (original.canShowBadge() != update.canShowBadge()) {
            update.lockFields(128);
        }
        if (original.canBubble() != update.canBubble()) {
            update.lockFields(256);
        }
    }

    public void dump(PrintWriter pw, String prefix, NotificationManagerService.DumpFilter filter) {
        pw.print(prefix);
        pw.println("per-package config:");
        pw.println("PackagePreferences:");
        synchronized (this.mPackagePreferences) {
            dumpPackagePreferencesLocked(pw, prefix, filter, this.mPackagePreferences);
        }
        pw.println("Restored without uid:");
        dumpPackagePreferencesLocked(pw, prefix, filter, this.mRestoredWithoutUids);
    }

    public void dump(ProtoOutputStream proto, NotificationManagerService.DumpFilter filter) {
        synchronized (this.mPackagePreferences) {
            dumpPackagePreferencesLocked(proto, 2246267895810L, filter, this.mPackagePreferences);
        }
        dumpPackagePreferencesLocked(proto, 2246267895811L, filter, this.mRestoredWithoutUids);
    }

    private static void dumpPackagePreferencesLocked(PrintWriter pw, String prefix, NotificationManagerService.DumpFilter filter, ArrayMap<String, PackagePreferences> packagePreferences) {
        int N = packagePreferences.size();
        for (int i = 0; i < N; i++) {
            PackagePreferences r = packagePreferences.valueAt(i);
            if (filter.matches(r.pkg)) {
                pw.print(prefix);
                pw.print("  AppSettings: ");
                pw.print(r.pkg);
                pw.print(" (");
                pw.print(r.uid == -10000 ? "UNKNOWN_UID" : Integer.toString(r.uid));
                pw.print(')');
                if (r.importance != -1000) {
                    pw.print(" importance=");
                    pw.print(NotificationListenerService.Ranking.importanceToString(r.importance));
                }
                if (r.priority != 0) {
                    pw.print(" priority=");
                    pw.print(Notification.priorityToString(r.priority));
                }
                if (r.visibility != -1000) {
                    pw.print(" visibility=");
                    pw.print(Notification.visibilityToString(r.visibility));
                }
                if (!r.showBadge) {
                    pw.print(" showBadge=");
                    pw.print(r.showBadge);
                }
                if (r.defaultAppLockedImportance) {
                    pw.print(" defaultAppLocked=");
                    pw.print(r.defaultAppLockedImportance);
                }
                if (r.oemLockedImportance) {
                    pw.print(" oemLocked=");
                    pw.print(r.oemLockedImportance);
                }
                if (!r.futureOemLockedChannels.isEmpty()) {
                    pw.print(" futureLockedChannels=");
                    pw.print(r.futureOemLockedChannels);
                }
                pw.println();
                for (NotificationChannel channel : r.channels.values()) {
                    pw.print(prefix);
                    channel.dump(pw, "    ", filter.redact);
                }
                for (NotificationChannelGroup group : r.groups.values()) {
                    pw.print(prefix);
                    pw.print("  ");
                    pw.print("  ");
                    pw.println(group);
                }
            }
        }
    }

    private static void dumpPackagePreferencesLocked(ProtoOutputStream proto, long fieldId, NotificationManagerService.DumpFilter filter, ArrayMap<String, PackagePreferences> packagePreferences) {
        int N = packagePreferences.size();
        for (int i = 0; i < N; i++) {
            PackagePreferences r = packagePreferences.valueAt(i);
            if (filter.matches(r.pkg)) {
                long fToken = proto.start(fieldId);
                proto.write(1138166333441L, r.pkg);
                proto.write(1120986464258L, r.uid);
                proto.write(1172526071811L, r.importance);
                proto.write(1120986464260L, r.priority);
                proto.write(1172526071813L, r.visibility);
                proto.write(1133871366150L, r.showBadge);
                for (NotificationChannel channel : r.channels.values()) {
                    channel.writeToProto(proto, 2246267895815L);
                }
                for (NotificationChannelGroup group : r.groups.values()) {
                    group.writeToProto(proto, 2246267895816L);
                }
                proto.end(fToken);
            }
        }
    }

    public JSONObject dumpJson(NotificationManagerService.DumpFilter filter) {
        JSONObject ranking = new JSONObject();
        JSONArray PackagePreferencess = new JSONArray();
        try {
            ranking.put("noUid", this.mRestoredWithoutUids.size());
        } catch (JSONException e) {
        }
        synchronized (this.mPackagePreferences) {
            int N = this.mPackagePreferences.size();
            for (int i = 0; i < N; i++) {
                PackagePreferences r = this.mPackagePreferences.valueAt(i);
                if (filter == null || filter.matches(r.pkg)) {
                    JSONObject PackagePreferences2 = new JSONObject();
                    try {
                        PackagePreferences2.put("userId", UserHandle.getUserId(r.uid));
                        PackagePreferences2.put("packageName", r.pkg);
                        if (r.importance != -1000) {
                            PackagePreferences2.put(ATT_IMPORTANCE, NotificationListenerService.Ranking.importanceToString(r.importance));
                        }
                        if (r.priority != 0) {
                            PackagePreferences2.put(ATT_PRIORITY, Notification.priorityToString(r.priority));
                        }
                        if (r.visibility != -1000) {
                            PackagePreferences2.put(ATT_VISIBILITY, Notification.visibilityToString(r.visibility));
                        }
                        if (!r.showBadge) {
                            PackagePreferences2.put("showBadge", Boolean.valueOf(r.showBadge));
                        }
                        JSONArray channels = new JSONArray();
                        for (NotificationChannel channel : r.channels.values()) {
                            channels.put(channel.toJson());
                        }
                        PackagePreferences2.put("channels", channels);
                        JSONArray groups = new JSONArray();
                        for (NotificationChannelGroup group : r.groups.values()) {
                            groups.put(group.toJson());
                        }
                        PackagePreferences2.put("groups", groups);
                    } catch (JSONException e2) {
                    }
                    PackagePreferencess.put(PackagePreferences2);
                }
            }
        }
        try {
            ranking.put("PackagePreferencess", PackagePreferencess);
        } catch (JSONException e3) {
        }
        return ranking;
    }

    public JSONArray dumpBansJson(NotificationManagerService.DumpFilter filter) {
        JSONArray bans = new JSONArray();
        for (Map.Entry<Integer, String> ban : getPackageBans().entrySet()) {
            int userId = UserHandle.getUserId(ban.getKey().intValue());
            String packageName = ban.getValue();
            if (filter == null || filter.matches(packageName)) {
                JSONObject banJson = new JSONObject();
                try {
                    banJson.put("userId", userId);
                    banJson.put("packageName", packageName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bans.put(banJson);
            }
        }
        return bans;
    }

    public Map<Integer, String> getPackageBans() {
        ArrayMap<Integer, String> packageBans;
        synchronized (this.mPackagePreferences) {
            int N = this.mPackagePreferences.size();
            packageBans = new ArrayMap<>(N);
            for (int i = 0; i < N; i++) {
                PackagePreferences r = this.mPackagePreferences.valueAt(i);
                if (r.importance == 0) {
                    packageBans.put(Integer.valueOf(r.uid), r.pkg);
                }
            }
        }
        return packageBans;
    }

    public JSONArray dumpChannelsJson(NotificationManagerService.DumpFilter filter) {
        JSONArray channels = new JSONArray();
        for (Map.Entry<String, Integer> channelCount : getPackageChannels().entrySet()) {
            String packageName = channelCount.getKey();
            if (filter == null || filter.matches(packageName)) {
                JSONObject channelCountJson = new JSONObject();
                try {
                    channelCountJson.put("packageName", packageName);
                    channelCountJson.put("channelCount", channelCount.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                channels.put(channelCountJson);
            }
        }
        return channels;
    }

    private Map<String, Integer> getPackageChannels() {
        ArrayMap<String, Integer> packageChannels = new ArrayMap<>();
        synchronized (this.mPackagePreferences) {
            for (int i = 0; i < this.mPackagePreferences.size(); i++) {
                PackagePreferences r = this.mPackagePreferences.valueAt(i);
                int channelCount = 0;
                for (NotificationChannel nc : r.channels.values()) {
                    if (!nc.isDeleted()) {
                        channelCount++;
                    }
                }
                packageChannels.put(r.pkg, Integer.valueOf(channelCount));
            }
        }
        return packageChannels;
    }

    public void onUserSwitched(int userId) {
        syncChannelsBypassingDnd(userId);
    }

    public void onUserUnlocked(int userId) {
        syncChannelsBypassingDnd(userId);
    }

    public void onUserRemoved(int userId) {
        synchronized (this.mPackagePreferences) {
            for (int i = this.mPackagePreferences.size() - 1; i >= 0; i--) {
                if (UserHandle.getUserId(this.mPackagePreferences.valueAt(i).uid) == userId) {
                    this.mPackagePreferences.removeAt(i);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLocaleChanged(Context context, int userId) {
        synchronized (this.mPackagePreferences) {
            int N = this.mPackagePreferences.size();
            for (int i = 0; i < N; i++) {
                PackagePreferences PackagePreferences2 = this.mPackagePreferences.valueAt(i);
                if (UserHandle.getUserId(PackagePreferences2.uid) == userId && PackagePreferences2.channels.containsKey("miscellaneous")) {
                    PackagePreferences2.channels.get("miscellaneous").setName(context.getResources().getString(17039879));
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processLoop(RegionMaker.java:225)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public boolean onPackagesChanged(boolean r10, int r11, java.lang.String[] r12, int[] r13) {
        /*
            r9 = this;
            r0 = 0
            if (r12 == 0) goto L_0x008f
            int r1 = r12.length
            if (r1 != 0) goto L_0x0008
            goto L_0x008f
        L_0x0008:
            r1 = 0
            if (r10 == 0) goto L_0x0032
            int r0 = r12.length
            int r2 = r13.length
            int r0 = java.lang.Math.min(r0, r2)
            r2 = 0
        L_0x0012:
            if (r2 >= r0) goto L_0x0031
            r3 = r12[r2]
            r4 = r13[r2]
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r5 = r9.mPackagePreferences
            monitor-enter(r5)
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r6 = r9.mPackagePreferences     // Catch:{ all -> 0x002e }
            java.lang.String r7 = packagePreferencesKey(r3, r4)     // Catch:{ all -> 0x002e }
            r6.remove(r7)     // Catch:{ all -> 0x002e }
            monitor-exit(r5)     // Catch:{ all -> 0x002e }
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r5 = r9.mRestoredWithoutUids
            r5.remove(r3)
            r1 = 1
            int r2 = r2 + 1
            goto L_0x0012
        L_0x002e:
            r6 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x002e }
            throw r6
        L_0x0031:
            goto L_0x0089
        L_0x0032:
            int r2 = r12.length
        L_0x0033:
            if (r0 >= r2) goto L_0x0089
            r3 = r12[r0]
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r4 = r9.mRestoredWithoutUids
            java.lang.Object r4 = r4.get(r3)
            com.android.server.notification.PreferencesHelper$PackagePreferences r4 = (com.android.server.notification.PreferencesHelper.PackagePreferences) r4
            if (r4 == 0) goto L_0x0067
            android.content.pm.PackageManager r5 = r9.mPm     // Catch:{ NameNotFoundException -> 0x0066 }
            java.lang.String r6 = r4.pkg     // Catch:{ NameNotFoundException -> 0x0066 }
            int r5 = r5.getPackageUidAsUser(r6, r11)     // Catch:{ NameNotFoundException -> 0x0066 }
            r4.uid = r5     // Catch:{ NameNotFoundException -> 0x0066 }
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r5 = r9.mRestoredWithoutUids     // Catch:{ NameNotFoundException -> 0x0066 }
            r5.remove(r3)     // Catch:{ NameNotFoundException -> 0x0066 }
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r5 = r9.mPackagePreferences     // Catch:{ NameNotFoundException -> 0x0066 }
            monitor-enter(r5)     // Catch:{ NameNotFoundException -> 0x0066 }
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r6 = r9.mPackagePreferences     // Catch:{ all -> 0x0063 }
            java.lang.String r7 = r4.pkg     // Catch:{ all -> 0x0063 }
            int r8 = r4.uid     // Catch:{ all -> 0x0063 }
            java.lang.String r7 = packagePreferencesKey(r7, r8)     // Catch:{ all -> 0x0063 }
            r6.put(r7, r4)     // Catch:{ all -> 0x0063 }
            monitor-exit(r5)     // Catch:{ all -> 0x0063 }
            r1 = 1
            goto L_0x0067
        L_0x0063:
            r6 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0063 }
            throw r6     // Catch:{ NameNotFoundException -> 0x0066 }
        L_0x0066:
            r5 = move-exception
        L_0x0067:
            android.util.ArrayMap<java.lang.String, com.android.server.notification.PreferencesHelper$PackagePreferences> r5 = r9.mPackagePreferences     // Catch:{ NameNotFoundException -> 0x0085 }
            monitor-enter(r5)     // Catch:{ NameNotFoundException -> 0x0085 }
            android.content.pm.PackageManager r6 = r9.mPm     // Catch:{ all -> 0x0082 }
            int r6 = r6.getPackageUidAsUser(r3, r11)     // Catch:{ all -> 0x0082 }
            com.android.server.notification.PreferencesHelper$PackagePreferences r6 = r9.getPackagePreferencesLocked(r3, r6)     // Catch:{ all -> 0x0082 }
            if (r6 == 0) goto L_0x0080
            boolean r7 = r9.createDefaultChannelIfNeededLocked(r6)     // Catch:{ all -> 0x0082 }
            r1 = r1 | r7
            boolean r7 = r9.deleteDefaultChannelIfNeededLocked(r6)     // Catch:{ all -> 0x0082 }
            r1 = r1 | r7
        L_0x0080:
            monitor-exit(r5)     // Catch:{ all -> 0x0082 }
            goto L_0x0086
        L_0x0082:
            r6 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0082 }
            throw r6     // Catch:{ NameNotFoundException -> 0x0085 }
        L_0x0085:
            r5 = move-exception
        L_0x0086:
            int r0 = r0 + 1
            goto L_0x0033
        L_0x0089:
            if (r1 == 0) goto L_0x008e
            r9.updateConfig()
        L_0x008e:
            return r1
        L_0x008f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.notification.PreferencesHelper.onPackagesChanged(boolean, int, java.lang.String[], int[]):boolean");
    }

    public void clearData(String pkg, int uid) {
        synchronized (this.mPackagePreferences) {
            PackagePreferences p = getPackagePreferencesLocked(pkg, uid);
            if (p != null) {
                p.channels = new ArrayMap<>();
                p.groups = new ArrayMap();
                p.delegate = null;
                p.lockedAppFields = 0;
                p.allowBubble = true;
                p.importance = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
                p.priority = 0;
                p.visibility = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
                p.showBadge = true;
            }
        }
    }

    private LogMaker getChannelLog(NotificationChannel channel, String pkg) {
        return new LogMaker(856).setType(6).setPackageName(pkg).addTaggedData(857, channel.getId()).addTaggedData(858, Integer.valueOf(channel.getImportance()));
    }

    private LogMaker getChannelGroupLog(String groupId, String pkg) {
        return new LogMaker(859).setType(6).addTaggedData(860, groupId).setPackageName(pkg);
    }

    public void updateBubblesEnabled() {
        if (this.mBubblesEnabled == null) {
            this.mBubblesEnabled = new SparseBooleanArray();
        }
        boolean changed = false;
        for (int index = 0; index < this.mBubblesEnabled.size(); index++) {
            int userId = this.mBubblesEnabled.keyAt(index);
            boolean oldValue = this.mBubblesEnabled.get(userId);
            boolean z = true;
            boolean newValue = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_bubbles", 1, userId) != 0;
            this.mBubblesEnabled.put(userId, newValue);
            if (oldValue == newValue) {
                z = false;
            }
            changed |= z;
        }
        if (changed) {
            updateConfig();
        }
    }

    public boolean bubblesEnabled(UserHandle userHandle) {
        int userId = userHandle.getIdentifier();
        boolean z = false;
        if (userId == -1) {
            return false;
        }
        if (this.mBubblesEnabled.indexOfKey(userId) < 0) {
            SparseBooleanArray sparseBooleanArray = this.mBubblesEnabled;
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_bubbles", 1, userId) != 0) {
                z = true;
            }
            sparseBooleanArray.put(userId, z);
        }
        return this.mBubblesEnabled.get(userId, true);
    }

    public void updateBadgingEnabled() {
        if (this.mBadgingEnabled == null) {
            this.mBadgingEnabled = new SparseBooleanArray();
        }
        boolean changed = false;
        for (int index = 0; index < this.mBadgingEnabled.size(); index++) {
            int userId = this.mBadgingEnabled.keyAt(index);
            boolean oldValue = this.mBadgingEnabled.get(userId);
            boolean z = true;
            boolean newValue = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_badging", 1, userId) != 0;
            this.mBadgingEnabled.put(userId, newValue);
            if (oldValue == newValue) {
                z = false;
            }
            changed |= z;
        }
        if (changed) {
            updateConfig();
        }
    }

    public boolean badgingEnabled(UserHandle userHandle) {
        int userId = userHandle.getIdentifier();
        boolean z = false;
        if (userId == -1) {
            return false;
        }
        if (this.mBadgingEnabled.indexOfKey(userId) < 0) {
            SparseBooleanArray sparseBooleanArray = this.mBadgingEnabled;
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "notification_badging", 1, userId) != 0) {
                z = true;
            }
            sparseBooleanArray.put(userId, z);
        }
        return this.mBadgingEnabled.get(userId, true);
    }

    private void updateConfig() {
        this.mRankingHandler.requestSort();
    }

    private static String packagePreferencesKey(String pkg, int uid) {
        return pkg + "|" + uid;
    }

    private static class PackagePreferences {
        boolean allowBubble;
        ArrayMap<String, NotificationChannel> channels;
        boolean defaultAppLockedImportance;
        Delegate delegate;
        List<String> futureOemLockedChannels;
        Map<String, NotificationChannelGroup> groups;
        int importance;
        int lockedAppFields;
        boolean oemLockedImportance;
        String pkg;
        int priority;
        boolean showBadge;
        int uid;
        int visibility;

        private PackagePreferences() {
            this.uid = -10000;
            this.importance = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            this.priority = 0;
            this.visibility = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            this.showBadge = true;
            this.allowBubble = true;
            this.lockedAppFields = 0;
            this.oemLockedImportance = false;
            this.futureOemLockedChannels = new ArrayList();
            this.defaultAppLockedImportance = false;
            this.delegate = null;
            this.channels = new ArrayMap<>();
            this.groups = new ConcurrentHashMap();
        }

        public boolean isValidDelegate(String pkg2, int uid2) {
            Delegate delegate2 = this.delegate;
            return delegate2 != null && delegate2.isAllowed(pkg2, uid2);
        }
    }

    private static class Delegate {
        static final boolean DEFAULT_ENABLED = true;
        static final boolean DEFAULT_USER_ALLOWED = true;
        boolean mEnabled = true;
        String mPkg;
        int mUid = -10000;
        boolean mUserAllowed = true;

        Delegate(String pkg, int uid, boolean enabled, boolean userAllowed) {
            this.mPkg = pkg;
            this.mUid = uid;
            this.mEnabled = enabled;
            this.mUserAllowed = userAllowed;
        }

        public boolean isAllowed(String pkg, int uid) {
            if (pkg == null || uid == -10000 || !pkg.equals(this.mPkg) || uid != this.mUid || !this.mUserAllowed || !this.mEnabled) {
                return false;
            }
            return true;
        }
    }
}
