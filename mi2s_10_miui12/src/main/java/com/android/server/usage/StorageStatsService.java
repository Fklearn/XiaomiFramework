package com.android.server.usage;

import android.app.AppOpsManager;
import android.app.usage.ExternalStorageStats;
import android.app.usage.IStorageStatsManager;
import android.app.usage.StorageStats;
import android.app.usage.UsageStatsManagerInternal;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelableException;
import android.os.StatFs;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.DataUnit;
import android.util.Slog;
import android.util.SparseLongArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.server.IoThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.pm.Installer;
import com.android.server.pm.PackageManagerService;
import com.android.server.storage.CacheQuotaStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StorageStatsService extends IStorageStatsManager.Stub {
    private static final long DEFAULT_QUOTA = DataUnit.MEBIBYTES.toBytes(64);
    private static final long DELAY_IN_MILLIS = 30000;
    private static final String PROP_DISABLE_QUOTA = "fw.disable_quota";
    private static final String PROP_VERIFY_STORAGE = "fw.verify_storage";
    private static final String TAG = "StorageStatsService";
    private final AppOpsManager mAppOps;
    /* access modifiers changed from: private */
    public final ArrayMap<String, SparseLongArray> mCacheQuotas = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final Context mContext;
    private final H mHandler;
    /* access modifiers changed from: private */
    public final Installer mInstaller;
    private final PackageManager mPackage;
    private final StorageManager mStorage;
    private final UserManager mUser;

    public static class Lifecycle extends SystemService {
        private StorageStatsService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [android.os.IBinder, com.android.server.usage.StorageStatsService] */
        public void onStart() {
            this.mService = new StorageStatsService(getContext());
            publishBinderService("storagestats", this.mService);
        }
    }

    public StorageStatsService(Context context) {
        this.mContext = (Context) Preconditions.checkNotNull(context);
        this.mAppOps = (AppOpsManager) Preconditions.checkNotNull((AppOpsManager) context.getSystemService(AppOpsManager.class));
        this.mUser = (UserManager) Preconditions.checkNotNull((UserManager) context.getSystemService(UserManager.class));
        this.mPackage = (PackageManager) Preconditions.checkNotNull(context.getPackageManager());
        this.mStorage = (StorageManager) Preconditions.checkNotNull((StorageManager) context.getSystemService(StorageManager.class));
        this.mInstaller = new Installer(context);
        this.mInstaller.onStart();
        invalidateMounts();
        this.mHandler = new H(IoThread.get().getLooper());
        this.mHandler.sendEmptyMessage(101);
        this.mStorage.registerListener(new StorageEventListener() {
            public void onVolumeStateChanged(VolumeInfo vol, int oldState, int newState) {
                int i = vol.type;
                if ((i == 0 || i == 1 || i == 2) && newState == 2) {
                    StorageStatsService.this.invalidateMounts();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void invalidateMounts() {
        try {
            this.mInstaller.invalidateMounts();
        } catch (Installer.InstallerException e) {
            Slog.wtf(TAG, "Failed to invalidate mounts", e);
        }
    }

    private void enforcePermission(int callingUid, String callingPackage) {
        int mode = this.mAppOps.noteOp(43, callingUid, callingPackage);
        if (mode == 0) {
            return;
        }
        if (mode == 3) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", TAG);
            return;
        }
        throw new SecurityException("Package " + callingPackage + " from UID " + callingUid + " blocked by mode " + mode);
    }

    public boolean isQuotaSupported(String volumeUuid, String callingPackage) {
        try {
            return this.mInstaller.isQuotaSupported(volumeUuid);
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public boolean isReservedSupported(String volumeUuid, String callingPackage) {
        if (volumeUuid != StorageManager.UUID_PRIVATE_INTERNAL) {
            return false;
        }
        if (SystemProperties.getBoolean("vold.has_reserved", false) || Build.IS_CONTAINER) {
            return true;
        }
        return false;
    }

    public long getTotalBytes(String volumeUuid, String callingPackage) {
        if (volumeUuid == StorageManager.UUID_PRIVATE_INTERNAL) {
            return FileUtils.roundStorageSize(this.mStorage.getPrimaryStorageSize());
        }
        VolumeInfo vol = this.mStorage.findVolumeByUuid(volumeUuid);
        if (vol != null) {
            return FileUtils.roundStorageSize(vol.disk.size);
        }
        throw new ParcelableException(new IOException("Failed to find storage device for UUID " + volumeUuid));
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    public long getFreeBytes(String volumeUuid, String callingPackage) {
        long token = Binder.clearCallingIdentity();
        try {
            File path = this.mStorage.findPathForUuid(volumeUuid);
            if (isQuotaSupported(volumeUuid, PackageManagerService.PLATFORM_PACKAGE_NAME)) {
                long usableSpace = path.getUsableSpace() + Math.max(0, getCacheBytes(volumeUuid, PackageManagerService.PLATFORM_PACKAGE_NAME) - this.mStorage.getStorageCacheBytes(path, 0));
                Binder.restoreCallingIdentity(token);
                return usableSpace;
            }
            long cacheTotal = path.getUsableSpace();
            Binder.restoreCallingIdentity(token);
            return cacheTotal;
        } catch (FileNotFoundException e) {
            throw new ParcelableException(e);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    public long getCacheBytes(String volumeUuid, String callingPackage) {
        enforcePermission(Binder.getCallingUid(), callingPackage);
        long cacheBytes = 0;
        for (UserInfo user : this.mUser.getUsers()) {
            cacheBytes += queryStatsForUser(volumeUuid, user.id, (String) null).cacheBytes;
        }
        return cacheBytes;
    }

    public long getCacheQuotaBytes(String volumeUuid, int uid, String callingPackage) {
        enforcePermission(Binder.getCallingUid(), callingPackage);
        if (this.mCacheQuotas.containsKey(volumeUuid)) {
            return this.mCacheQuotas.get(volumeUuid).get(uid, DEFAULT_QUOTA);
        }
        return DEFAULT_QUOTA;
    }

    /* JADX WARNING: type inference failed for: r4v8, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.app.usage.StorageStats queryStatsForPackage(java.lang.String r20, java.lang.String r21, int r22, java.lang.String r23) {
        /*
            r19 = this;
            r1 = r19
            r2 = r21
            r12 = r22
            r13 = r23
            int r0 = android.os.UserHandle.getCallingUserId()
            java.lang.String r3 = "StorageStatsService"
            if (r12 == r0) goto L_0x0017
            android.content.Context r0 = r1.mContext
            java.lang.String r4 = "android.permission.INTERACT_ACROSS_USERS"
            r0.enforceCallingOrSelfPermission(r4, r3)
        L_0x0017:
            android.content.pm.PackageManager r0 = r1.mPackage     // Catch:{ NameNotFoundException -> 0x00a9 }
            r4 = 8192(0x2000, float:1.14794E-41)
            android.content.pm.ApplicationInfo r0 = r0.getApplicationInfoAsUser(r2, r4, r12)     // Catch:{ NameNotFoundException -> 0x00a9 }
            r14 = r0
            int r0 = android.os.Binder.getCallingUid()
            int r4 = r14.uid
            if (r0 != r4) goto L_0x002a
            goto L_0x0031
        L_0x002a:
            int r0 = android.os.Binder.getCallingUid()
            r1.enforcePermission(r0, r13)
        L_0x0031:
            android.content.pm.PackageManager r0 = r1.mPackage
            int r4 = r14.uid
            java.lang.String[] r0 = r0.getPackagesForUid(r4)
            java.lang.String[] r0 = com.android.internal.util.ArrayUtils.defeatNullable(r0)
            int r0 = r0.length
            r4 = 1
            if (r0 != r4) goto L_0x004a
            int r0 = r14.uid
            r15 = r20
            android.app.usage.StorageStats r0 = r1.queryStatsForUid(r15, r0, r13)
            return r0
        L_0x004a:
            r15 = r20
            int r0 = r14.uid
            int r16 = android.os.UserHandle.getUserId(r0)
            java.lang.String[] r5 = new java.lang.String[r4]
            r0 = 0
            r5[r0] = r2
            long[] r10 = new long[r4]
            java.lang.String[] r0 = new java.lang.String[r0]
            boolean r4 = r14.isSystemApp()
            if (r4 == 0) goto L_0x006a
            boolean r4 = r14.isUpdatedSystemApp()
            if (r4 != 0) goto L_0x006a
            r17 = r0
            goto L_0x0079
        L_0x006a:
            java.lang.Class<java.lang.String> r4 = java.lang.String.class
            java.lang.String r6 = r14.getCodePath()
            java.lang.Object[] r4 = com.android.internal.util.ArrayUtils.appendElement(r4, r0, r6)
            r0 = r4
            java.lang.String[] r0 = (java.lang.String[]) r0
            r17 = r0
        L_0x0079:
            android.content.pm.PackageStats r11 = new android.content.pm.PackageStats
            r11.<init>(r3)
            com.android.server.pm.Installer r3 = r1.mInstaller     // Catch:{ InstallerException -> 0x0097 }
            r7 = 0
            r4 = r20
            r6 = r22
            r8 = r16
            r9 = r10
            r18 = r10
            r10 = r17
            r3.getAppSize(r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ InstallerException -> 0x0095 }
            android.app.usage.StorageStats r0 = translate(r11)
            return r0
        L_0x0095:
            r0 = move-exception
            goto L_0x009a
        L_0x0097:
            r0 = move-exception
            r18 = r10
        L_0x009a:
            android.os.ParcelableException r3 = new android.os.ParcelableException
            java.io.IOException r4 = new java.io.IOException
            java.lang.String r6 = r0.getMessage()
            r4.<init>(r6)
            r3.<init>(r4)
            throw r3
        L_0x00a9:
            r0 = move-exception
            r15 = r20
            android.os.ParcelableException r3 = new android.os.ParcelableException
            r3.<init>(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.StorageStatsService.queryStatsForPackage(java.lang.String, java.lang.String, int, java.lang.String):android.app.usage.StorageStats");
    }

    public StorageStats queryStatsForUid(String volumeUuid, int uid, String callingPackage) {
        PackageStats stats;
        int i = uid;
        int userId = UserHandle.getUserId(uid);
        int appId = UserHandle.getAppId(uid);
        if (userId != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", TAG);
        }
        if (Binder.getCallingUid() == i) {
            String str = callingPackage;
        } else {
            enforcePermission(Binder.getCallingUid(), callingPackage);
        }
        String[] packageNames = ArrayUtils.defeatNullable(this.mPackage.getPackagesForUid(i));
        long[] ceDataInodes = new long[packageNames.length];
        int i2 = 0;
        String[] codePaths = new String[0];
        while (i2 < packageNames.length) {
            try {
                ApplicationInfo appInfo = this.mPackage.getApplicationInfoAsUser(packageNames[i2], 8192, userId);
                if (!appInfo.isSystemApp() || appInfo.isUpdatedSystemApp()) {
                    codePaths = (String[]) ArrayUtils.appendElement(String.class, codePaths, appInfo.getCodePath());
                }
                i2++;
            } catch (PackageManager.NameNotFoundException e) {
                throw new ParcelableException(e);
            }
        }
        PackageStats stats2 = new PackageStats(TAG);
        try {
            PackageStats stats3 = stats2;
            String[] codePaths2 = codePaths;
            long[] ceDataInodes2 = ceDataInodes;
            String[] packageNames2 = packageNames;
            try {
                this.mInstaller.getAppSize(volumeUuid, packageNames, userId, getDefaultFlags(), appId, ceDataInodes, codePaths2, stats3);
                if (SystemProperties.getBoolean(PROP_VERIFY_STORAGE, false)) {
                    PackageStats manualStats = new PackageStats(TAG);
                    this.mInstaller.getAppSize(volumeUuid, packageNames2, userId, 0, appId, ceDataInodes2, codePaths2, manualStats);
                    stats = stats3;
                    try {
                        checkEquals("UID " + i, manualStats, stats);
                    } catch (Installer.InstallerException e2) {
                        e = e2;
                    }
                } else {
                    stats = stats3;
                }
                return translate(stats);
            } catch (Installer.InstallerException e3) {
                e = e3;
                PackageStats packageStats = stats3;
                throw new ParcelableException(new IOException(e.getMessage()));
            }
        } catch (Installer.InstallerException e4) {
            e = e4;
            PackageStats packageStats2 = stats2;
            String[] strArr = codePaths;
            long[] jArr = ceDataInodes;
            String[] strArr2 = packageNames;
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public StorageStats queryStatsForUser(String volumeUuid, int userId, String callingPackage) {
        if (userId != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", TAG);
        }
        enforcePermission(Binder.getCallingUid(), callingPackage);
        int[] appIds = getAppIds(userId);
        PackageStats stats = new PackageStats(TAG);
        try {
            this.mInstaller.getUserSize(volumeUuid, userId, getDefaultFlags(), appIds, stats);
            if (SystemProperties.getBoolean(PROP_VERIFY_STORAGE, false)) {
                PackageStats manualStats = new PackageStats(TAG);
                this.mInstaller.getUserSize(volumeUuid, userId, 0, appIds, manualStats);
                checkEquals("User " + userId, manualStats, stats);
            }
            return translate(stats);
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    public ExternalStorageStats queryExternalStatsForUser(String volumeUuid, int userId, String callingPackage) {
        if (userId != UserHandle.getCallingUserId()) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", TAG);
        }
        enforcePermission(Binder.getCallingUid(), callingPackage);
        int[] appIds = getAppIds(userId);
        try {
            long[] stats = this.mInstaller.getExternalSize(volumeUuid, userId, getDefaultFlags(), appIds);
            if (SystemProperties.getBoolean(PROP_VERIFY_STORAGE, false)) {
                long[] manualStats = this.mInstaller.getExternalSize(volumeUuid, userId, 0, appIds);
                checkEquals("External " + userId, manualStats, stats);
            }
            ExternalStorageStats res = new ExternalStorageStats();
            res.totalBytes = stats[0];
            res.audioBytes = stats[1];
            res.videoBytes = stats[2];
            res.imageBytes = stats[3];
            res.appBytes = stats[4];
            res.obbBytes = stats[5];
            return res;
        } catch (Installer.InstallerException e) {
            throw new ParcelableException(new IOException(e.getMessage()));
        }
    }

    private int[] getAppIds(int userId) {
        int[] appIds = null;
        for (ApplicationInfo app : this.mPackage.getInstalledApplicationsAsUser(8192, userId)) {
            int appId = UserHandle.getAppId(app.uid);
            if (!ArrayUtils.contains(appIds, appId)) {
                appIds = ArrayUtils.appendInt(appIds, appId);
            }
        }
        return appIds;
    }

    private static int getDefaultFlags() {
        if (SystemProperties.getBoolean(PROP_DISABLE_QUOTA, false)) {
            return 0;
        }
        return 4096;
    }

    private static void checkEquals(String msg, long[] a, long[] b) {
        for (int i = 0; i < a.length; i++) {
            checkEquals(msg + "[" + i + "]", a[i], b[i]);
        }
    }

    private static void checkEquals(String msg, PackageStats a, PackageStats b) {
        checkEquals(msg + " codeSize", a.codeSize, b.codeSize);
        checkEquals(msg + " dataSize", a.dataSize, b.dataSize);
        checkEquals(msg + " cacheSize", a.cacheSize, b.cacheSize);
        checkEquals(msg + " externalCodeSize", a.externalCodeSize, b.externalCodeSize);
        checkEquals(msg + " externalDataSize", a.externalDataSize, b.externalDataSize);
        checkEquals(msg + " externalCacheSize", a.externalCacheSize, b.externalCacheSize);
    }

    private static void checkEquals(String msg, long expected, long actual) {
        if (expected != actual) {
            Slog.e(TAG, msg + " expected " + expected + " actual " + actual);
        }
    }

    private static StorageStats translate(PackageStats stats) {
        StorageStats res = new StorageStats();
        res.codeBytes = stats.codeSize + stats.externalCodeSize;
        res.dataBytes = stats.dataSize + stats.externalDataSize;
        res.cacheBytes = stats.cacheSize + stats.externalCacheSize;
        return res;
    }

    private class H extends Handler {
        private static final boolean DEBUG = false;
        private static final double MINIMUM_CHANGE_DELTA = 0.05d;
        private static final int MSG_CHECK_STORAGE_DELTA = 100;
        private static final int MSG_LOAD_CACHED_QUOTAS_FROM_FILE = 101;
        private static final int UNSET = -1;
        private double mMinimumThresholdBytes = (((double) this.mStats.getTotalBytes()) * MINIMUM_CHANGE_DELTA);
        private long mPreviousBytes = this.mStats.getAvailableBytes();
        private final StatFs mStats = new StatFs(Environment.getDataDirectory().getAbsolutePath());

        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (StorageStatsService.isCacheQuotaCalculationsEnabled(StorageStatsService.this.mContext.getContentResolver())) {
                int i = msg.what;
                if (i == 100) {
                    if (((double) Math.abs(this.mPreviousBytes - this.mStats.getAvailableBytes())) > this.mMinimumThresholdBytes) {
                        this.mPreviousBytes = this.mStats.getAvailableBytes();
                        recalculateQuotas(getInitializedStrategy());
                        StorageStatsService.this.notifySignificantDelta();
                    }
                    sendEmptyMessageDelayed(100, 30000);
                } else if (i == 101) {
                    CacheQuotaStrategy strategy = getInitializedStrategy();
                    this.mPreviousBytes = -1;
                    try {
                        this.mPreviousBytes = strategy.setupQuotasFromFile();
                    } catch (IOException e) {
                        Slog.e(StorageStatsService.TAG, "An error occurred while reading the cache quota file.", e);
                    } catch (IllegalStateException e2) {
                        Slog.e(StorageStatsService.TAG, "Cache quota XML file is malformed?", e2);
                    }
                    if (this.mPreviousBytes < 0) {
                        this.mPreviousBytes = this.mStats.getAvailableBytes();
                        recalculateQuotas(strategy);
                    }
                    sendEmptyMessageDelayed(100, 30000);
                }
            }
        }

        private void recalculateQuotas(CacheQuotaStrategy strategy) {
            strategy.recalculateQuotas();
        }

        private CacheQuotaStrategy getInitializedStrategy() {
            return new CacheQuotaStrategy(StorageStatsService.this.mContext, (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class), StorageStatsService.this.mInstaller, StorageStatsService.this.mCacheQuotas);
        }
    }

    @VisibleForTesting
    static boolean isCacheQuotaCalculationsEnabled(ContentResolver resolver) {
        return Settings.Global.getInt(resolver, "enable_cache_quota_calculation", 1) != 0;
    }

    /* access modifiers changed from: package-private */
    public void notifySignificantDelta() {
        this.mContext.getContentResolver().notifyChange(Uri.parse("content://com.android.externalstorage.documents/"), (ContentObserver) null, false);
    }
}
