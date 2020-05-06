package com.android.server.content;

import android.app.job.JobInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.content.MiSyncConstants;
import com.android.server.job.controllers.JobStatus;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class MiSyncStrategy {
    public static final int CLEVER_MJ_STRATEGY = 1;
    public static final int DEFAULT_STRATEGY = 1;
    public static final int OFFICIAL_STRATEGY = 0;
    private static final String TAG = "Sync";
    private static final int VERSION = 1;
    private static final String XML_ATTR_ACCOUNT_NAME = "account_name";
    private static final String XML_ATTR_STRATEGY = "strategy";
    private static final String XML_ATTR_UID = "uid";
    private static final String XML_ATTR_VERSION = "version";
    public static final String XML_FILE_NAME = "mi_strategy";
    public static final int XML_FILE_VERSION = 1;
    private static final String XML_TAG_ITEM = "sync_strategy_item";
    private String mAccountName;
    private SparseArray<ISyncStrategy> mCache = new SparseArray<>();
    private int mStrategy = 1;
    private int mUid;

    private interface ISyncStrategy {
        void apply(SyncOperation syncOperation, Bundle bundle, JobInfo.Builder builder);

        boolean isAllowedToRun(SyncOperation syncOperation, Bundle bundle);
    }

    public MiSyncStrategy(int uid, String accountName) {
        this.mUid = uid;
        this.mAccountName = accountName;
    }

    public int getUid() {
        return this.mUid;
    }

    public String getAccountName() {
        return this.mAccountName;
    }

    public boolean setStrategy(int strategy) {
        if (strategy == 0 || strategy == 1) {
            this.mStrategy = strategy;
            return true;
        } else if (!Log.isLoggable(TAG, 3)) {
            return false;
        } else {
            Log.d(TAG, "Illegal strategy");
            return false;
        }
    }

    public int getStrategy() {
        return this.mStrategy;
    }

    public void writeToXML(XmlSerializer out) throws IOException {
        out.startTag((String) null, XML_TAG_ITEM);
        out.attribute((String) null, XML_ATTR_VERSION, Integer.toString(1));
        out.attribute((String) null, "uid", Integer.toString(this.mUid));
        out.attribute((String) null, XML_ATTR_ACCOUNT_NAME, this.mAccountName);
        out.attribute((String) null, XML_ATTR_STRATEGY, Integer.toString(this.mStrategy));
        out.endTag((String) null, XML_TAG_ITEM);
    }

    public static MiSyncStrategy readFromXML(XmlPullParser parser) {
        if (!XML_TAG_ITEM.equals(parser.getName())) {
            return null;
        }
        String itemVersionString = parser.getAttributeValue((String) null, XML_ATTR_VERSION);
        if (TextUtils.isEmpty(itemVersionString)) {
            Slog.e(TAG, "the version in mi strategy is null");
            return null;
        }
        try {
            if (Integer.parseInt(itemVersionString) < 1) {
                return null;
            }
            String uidString = parser.getAttributeValue((String) null, "uid");
            String accountName = parser.getAttributeValue((String) null, XML_ATTR_ACCOUNT_NAME);
            String strategyString = parser.getAttributeValue((String) null, XML_ATTR_STRATEGY);
            if (TextUtils.isEmpty(uidString) || TextUtils.isEmpty(accountName) || TextUtils.isEmpty(strategyString)) {
                return null;
            }
            try {
                int uid = Integer.parseInt(uidString);
                int strategy = Integer.parseInt(strategyString);
                MiSyncStrategy miSyncStrategy = new MiSyncStrategy(uid, accountName);
                miSyncStrategy.setStrategy(strategy);
                return miSyncStrategy;
            } catch (NumberFormatException e) {
                Slog.e(TAG, "error parsing item for mi strategy", e);
                return null;
            }
        } catch (NumberFormatException e2) {
            Slog.e(TAG, "error parsing version for mi strategy", e2);
            return null;
        }
    }

    public void apply(SyncOperation syncOperation, Bundle bundle, JobInfo.Builder builder) {
        getSyncStrategyInternal(this.mStrategy).apply(syncOperation, bundle, builder);
    }

    public boolean isAllowedToRun(SyncOperation syncOperation, Bundle bundle) {
        return getSyncStrategyInternal(this.mStrategy).isAllowedToRun(syncOperation, bundle);
    }

    private ISyncStrategy getSyncStrategyInternal(int strategy) {
        ISyncStrategy syncStrategy;
        if (this.mCache == null) {
            this.mCache = new SparseArray<>();
        }
        ISyncStrategy syncStrategy2 = this.mCache.get(strategy);
        if (syncStrategy2 != null) {
            return syncStrategy2;
        }
        if (strategy == 0) {
            syncStrategy = new OfficialStrategy();
        } else if (strategy != 1) {
            syncStrategy = new CleverMJStrategy();
        } else {
            syncStrategy = new CleverMJStrategy();
        }
        this.mCache.put(strategy, syncStrategy);
        return syncStrategy;
    }

    private static class OfficialStrategy implements ISyncStrategy {
        private OfficialStrategy() {
        }

        public void apply(SyncOperation syncOperation, Bundle bundle, JobInfo.Builder builder) {
        }

        public boolean isAllowedToRun(SyncOperation syncOperation, Bundle bundle) {
            return true;
        }
    }

    private static class CleverMJStrategy implements ISyncStrategy {
        private static final int ALLOW_FIRST_SYNC_THRESHOLD = 3;
        private static final int ALLOW_FIRST_SYNC_THRESHOLD_FOR_BROWSER = 8;
        private static final String AUTHORITY_BROWSER = "com.miui.browser";
        private static final String AUTHORITY_CALENDAR = "com.android.calendar";
        private static final String AUTHORITY_CONTACTS = "com.android.contacts";
        private static final String AUTHORITY_GALLERY = "com.miui.gallery.cloud.provider";
        private static final String AUTHORITY_NOTES = "notes";
        private static final Set<String> REAL_TIME_STRATEGY_AUTHORITY_SET = new HashSet();

        private CleverMJStrategy() {
        }

        static {
            REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_CALENDAR);
            REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_NOTES);
            REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_CONTACTS);
            REAL_TIME_STRATEGY_AUTHORITY_SET.add(AUTHORITY_GALLERY);
        }

        public void apply(SyncOperation syncOperation, Bundle bundle, JobInfo.Builder builder) {
            if (syncOperation != null && syncOperation.target != null) {
                String authority = syncOperation.target.provider;
                if (TextUtils.isEmpty(authority)) {
                    if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                        Log.d(MiSyncStrategy.TAG, "injector: apply: null parameter, return");
                    }
                } else if (REAL_TIME_STRATEGY_AUTHORITY_SET.contains(authority)) {
                    if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                        Log.d(MiSyncStrategy.TAG, "injector: apply: authority is not affected by strategy, return");
                    }
                } else if (!isFirstTimes(authority, bundle)) {
                    builder.setRequiresCharging(true);
                } else if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                    Log.d(MiSyncStrategy.TAG, "injector: apply: first full sync, return");
                }
            } else if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                Log.d(MiSyncStrategy.TAG, "injector: apply: null parameter, return");
            }
        }

        public boolean isAllowedToRun(SyncOperation syncOperation, Bundle bundle) {
            SyncOperation syncOperation2 = syncOperation;
            Bundle bundle2 = bundle;
            if (syncOperation2 != null) {
                if (syncOperation2.target != null) {
                    String authority = syncOperation2.target.provider;
                    if (TextUtils.isEmpty(authority)) {
                        if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                            Log.d(MiSyncStrategy.TAG, "injector: isAllowedToRun: null parameter, return true");
                        }
                        return true;
                    } else if (REAL_TIME_STRATEGY_AUTHORITY_SET.contains(authority)) {
                        if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                            Log.d(MiSyncStrategy.TAG, "injector: isAllowedToRun: authority is not affected by strategy, return true");
                        }
                        return true;
                    } else if (isFirstTimes(authority, bundle2)) {
                        if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                            Log.d(MiSyncStrategy.TAG, "injector: isAllowedToRun: first full sync, return true");
                        }
                        return true;
                    } else {
                        long currentTimeMills = System.currentTimeMillis();
                        boolean isInteractive = bundle2.getBoolean(MiSyncConstants.Strategy.EXTRA_KEY_INTERACTIVE);
                        long lastScreenOffTime = bundle2.getLong(MiSyncConstants.Strategy.EXTRA_KEY_LAST_SCREEN_OFF_TIME);
                        if (!bundle2.getBoolean(MiSyncConstants.Strategy.EXTRA_KEY_BATTERY_CHARGING) || isInteractive || currentTimeMills - lastScreenOffTime <= JobStatus.DEFAULT_TRIGGER_MAX_DELAY) {
                            return false;
                        }
                        if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                            Log.d(MiSyncStrategy.TAG, "injector: isAllowedToRun: condition is satisfied, return true");
                        }
                        return true;
                    }
                }
            }
            if (Log.isLoggable(MiSyncStrategy.TAG, 3)) {
                Log.d(MiSyncStrategy.TAG, "injector: isAllowedToRun: null parameter, return true");
            }
            return true;
        }

        /* JADX WARNING: Removed duplicated region for block: B:10:0x001e A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean isFirstTimes(java.lang.String r5, android.os.Bundle r6) {
            /*
                r4 = this;
                r0 = 0
                java.lang.String r1 = "key_num_syncs"
                int r1 = r6.getInt(r1, r0)
                java.lang.String r2 = "com.miui.browser"
                boolean r2 = r2.equals(r5)
                r3 = 1
                if (r2 == 0) goto L_0x0018
                if (r1 < 0) goto L_0x001e
                r2 = 8
                if (r1 >= r2) goto L_0x001e
                return r3
            L_0x0018:
                if (r1 < 0) goto L_0x001e
                r2 = 3
                if (r1 >= r2) goto L_0x001e
                return r3
            L_0x001e:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.MiSyncStrategy.CleverMJStrategy.isFirstTimes(java.lang.String, android.os.Bundle):boolean");
        }
    }
}
