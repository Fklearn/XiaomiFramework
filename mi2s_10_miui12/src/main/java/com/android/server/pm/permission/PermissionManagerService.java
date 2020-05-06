package com.android.server.pm.permission;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManagerInternal;
import android.content.pm.PackageParser;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.metrics.LogMaker;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.os.storage.StorageManager;
import android.permission.PermissionControllerManager;
import android.permission.PermissionManager;
import android.permission.PermissionManagerInternal;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.RoSystemProperties;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.SystemConfig;
import com.android.server.Watchdog;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.PackageManagerServiceUtils;
import com.android.server.pm.PackageSetting;
import com.android.server.pm.SharedUserSetting;
import com.android.server.pm.UserManagerService;
import com.android.server.pm.permission.PermissionManagerServiceInternal;
import com.android.server.pm.permission.PermissionsState;
import com.android.server.policy.PermissionPolicyInternal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import libcore.util.EmptyArray;
import miui.content.pm.ExtraPackageManager;
import miui.securityspace.XSpaceUserHandle;

public class PermissionManagerService {
    private static final long BACKUP_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(60);
    private static final int BLOCKING_PERMISSION_FLAGS = 52;
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Map<String, String> FULLER_PERMISSION_MAP = new HashMap();
    private static final int GRANT_DENIED = 1;
    private static final int GRANT_INSTALL = 2;
    private static final int GRANT_RUNTIME = 3;
    private static final int GRANT_UPGRADE = 4;
    private static final int MAX_PERMISSION_TREE_FOOTPRINT = 32768;
    private static final String TAG = "PackageManager";
    private static final int UPDATE_PERMISSIONS_ALL = 1;
    private static final int UPDATE_PERMISSIONS_REPLACE_ALL = 4;
    private static final int UPDATE_PERMISSIONS_REPLACE_PKG = 2;
    private static final int USER_PERMISSION_FLAGS = 3;
    @GuardedBy({"mLock"})
    private ArrayMap<String, List<String>> mBackgroundPermissions;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final DefaultPermissionGrantPolicy mDefaultPermissionGrantPolicy;
    private final int[] mGlobalGids;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    @GuardedBy({"mLock"})
    private final SparseBooleanArray mHasNoDelayedPermBackup = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final Object mLock;
    private final MetricsLogger mMetricsLogger = new MetricsLogger();
    private final PackageManagerInternal mPackageManagerInt;
    private PermissionControllerManager mPermissionControllerManager;
    @GuardedBy({"mLock"})
    private PermissionPolicyInternal mPermissionPolicyInternal;
    @GuardedBy({"mLock"})
    private ArraySet<String> mPrivappPermissionsViolations;
    @GuardedBy({"mLock"})
    private final ArrayList<PermissionManagerInternal.OnRuntimePermissionStateChangedListener> mRuntimePermissionStateChangedListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final PermissionSettings mSettings;
    private final SparseArray<ArraySet<String>> mSystemPermissions;
    @GuardedBy({"mLock"})
    private boolean mSystemReady;
    private final UserManagerInternal mUserManagerInt;

    static {
        FULLER_PERMISSION_MAP.put("android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION");
        FULLER_PERMISSION_MAP.put("android.permission.INTERACT_ACROSS_USERS", "android.permission.INTERACT_ACROSS_USERS_FULL");
    }

    PermissionManagerService(Context context, Object externalLock) {
        this.mContext = context;
        this.mLock = externalLock;
        this.mPackageManagerInt = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mUserManagerInt = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        this.mSettings = new PermissionSettings(this.mLock);
        this.mHandlerThread = new ServiceThread(TAG, 10, true);
        this.mHandlerThread.start();
        this.mHandler = new Handler(this.mHandlerThread.getLooper());
        Watchdog.getInstance().addThread(this.mHandler);
        this.mDefaultPermissionGrantPolicy = new DefaultPermissionGrantPolicy(context, this.mHandlerThread.getLooper(), this);
        SystemConfig systemConfig = SystemConfig.getInstance();
        this.mSystemPermissions = systemConfig.getSystemPermissions();
        this.mGlobalGids = systemConfig.getGlobalGids();
        ArrayMap<String, SystemConfig.PermissionEntry> permConfig = SystemConfig.getInstance().getPermissions();
        synchronized (this.mLock) {
            for (int i = 0; i < permConfig.size(); i++) {
                SystemConfig.PermissionEntry perm = permConfig.valueAt(i);
                BasePermission bp = this.mSettings.getPermissionLocked(perm.name);
                if (bp == null) {
                    bp = new BasePermission(perm.name, PackageManagerService.PLATFORM_PACKAGE_NAME, 1);
                    this.mSettings.putPermissionLocked(perm.name, bp);
                }
                if (perm.gids != null) {
                    bp.setGids(perm.gids, perm.perUser);
                }
            }
        }
        PermissionManagerServiceInternalImpl localService = new PermissionManagerServiceInternalImpl();
        LocalServices.addService(PermissionManagerServiceInternal.class, localService);
        LocalServices.addService(PermissionManagerInternal.class, localService);
    }

    public static PermissionManagerServiceInternal create(Context context, Object externalLock) {
        PermissionManagerServiceInternal permMgrInt = (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
        if (permMgrInt != null) {
            return permMgrInt;
        }
        new PermissionManagerService(context, externalLock);
        return (PermissionManagerServiceInternal) LocalServices.getService(PermissionManagerServiceInternal.class);
    }

    /* access modifiers changed from: package-private */
    public BasePermission getPermission(String permName) {
        BasePermission permissionLocked;
        synchronized (this.mLock) {
            permissionLocked = this.mSettings.getPermissionLocked(permName);
        }
        return permissionLocked;
    }

    /* access modifiers changed from: private */
    public int checkPermission(String permName, String pkgName, int callingUid, int userId) {
        PackageParser.Package pkg;
        if (!this.mUserManagerInt.exists(userId) || (pkg = this.mPackageManagerInt.getPackage(pkgName)) == null || pkg.mExtras == null || this.mPackageManagerInt.filterAppAccess(pkg, callingUid, userId)) {
            return -1;
        }
        PackageSetting ps = (PackageSetting) pkg.mExtras;
        boolean instantApp = ps.getInstantApp(userId);
        PermissionsState permissionsState = ps.getPermissionsState();
        if (permissionsState.hasPermission(permName, userId)) {
            if (!instantApp) {
                return 0;
            }
            synchronized (this.mLock) {
                BasePermission bp = this.mSettings.getPermissionLocked(permName);
                if (bp != null && bp.isInstant()) {
                    return 0;
                }
            }
        }
        if (isImpliedPermissionGranted(permissionsState, permName, userId)) {
            return 0;
        }
        return -1;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x008c A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int checkUidPermission(java.lang.String r10, android.content.pm.PackageParser.Package r11, int r12, int r13) {
        /*
            r9 = this;
            int r0 = android.os.UserHandle.getUserId(r13)
            android.content.pm.PackageManagerInternal r1 = r9.mPackageManagerInt
            java.lang.String r1 = r1.getInstantAppPackageName(r13)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0010
            r1 = r2
            goto L_0x0011
        L_0x0010:
            r1 = r3
        L_0x0011:
            android.content.pm.PackageManagerInternal r4 = r9.mPackageManagerInt
            java.lang.String r4 = r4.getInstantAppPackageName(r12)
            if (r4 == 0) goto L_0x001a
            goto L_0x001b
        L_0x001a:
            r2 = r3
        L_0x001b:
            int r4 = android.os.UserHandle.getUserId(r12)
            android.os.UserManagerInternal r5 = r9.mUserManagerInt
            boolean r5 = r5.exists(r4)
            r6 = -1
            if (r5 != 0) goto L_0x0029
            return r6
        L_0x0029:
            int r5 = com.android.server.pm.PackageManagerServiceInjector.preCheckUidPermission(r10, r12)
            r7 = -100
            if (r5 == r7) goto L_0x0032
            return r5
        L_0x0032:
            if (r11 == 0) goto L_0x0066
            java.lang.String r7 = r11.mSharedUserId
            if (r7 == 0) goto L_0x003b
            if (r1 == 0) goto L_0x0044
            return r6
        L_0x003b:
            android.content.pm.PackageManagerInternal r7 = r9.mPackageManagerInt
            boolean r7 = r7.filterAppAccess(r11, r13, r0)
            if (r7 == 0) goto L_0x0044
            return r6
        L_0x0044:
            java.lang.Object r7 = r11.mExtras
            com.android.server.pm.PackageSetting r7 = (com.android.server.pm.PackageSetting) r7
            com.android.server.pm.permission.PermissionsState r7 = r7.getPermissionsState()
            boolean r8 = r7.hasPermission(r10, r4)
            if (r8 == 0) goto L_0x005e
            if (r2 == 0) goto L_0x005d
            com.android.server.pm.permission.PermissionSettings r8 = r9.mSettings
            boolean r8 = r8.isPermissionInstant(r10)
            if (r8 == 0) goto L_0x005e
            return r3
        L_0x005d:
            return r3
        L_0x005e:
            boolean r8 = isImpliedPermissionGranted(r7, r10, r4)
            if (r8 == 0) goto L_0x0065
            return r3
        L_0x0065:
            goto L_0x008c
        L_0x0066:
            android.util.SparseArray<android.util.ArraySet<java.lang.String>> r7 = r9.mSystemPermissions
            java.lang.Object r7 = r7.get(r12)
            android.util.ArraySet r7 = (android.util.ArraySet) r7
            if (r7 == 0) goto L_0x008c
            boolean r8 = r7.contains(r10)
            if (r8 == 0) goto L_0x0077
            return r3
        L_0x0077:
            java.util.Map<java.lang.String, java.lang.String> r8 = FULLER_PERMISSION_MAP
            boolean r8 = r8.containsKey(r10)
            if (r8 == 0) goto L_0x008c
            java.util.Map<java.lang.String, java.lang.String> r8 = FULLER_PERMISSION_MAP
            java.lang.Object r8 = r8.get(r10)
            boolean r8 = r7.contains(r8)
            if (r8 == 0) goto L_0x008c
            return r3
        L_0x008c:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.checkUidPermission(java.lang.String, android.content.pm.PackageParser$Package, int, int):int");
    }

    /* access modifiers changed from: private */
    public byte[] backupRuntimePermissions(UserHandle user) {
        CompletableFuture<byte[]> backup = new CompletableFuture<>();
        PermissionControllerManager permissionControllerManager = this.mPermissionControllerManager;
        Executor mainExecutor = this.mContext.getMainExecutor();
        Objects.requireNonNull(backup);
        permissionControllerManager.getRuntimePermissionBackup(user, mainExecutor, new PermissionControllerManager.OnGetRuntimePermissionBackupCallback(backup) {
            private final /* synthetic */ CompletableFuture f$0;

            {
                this.f$0 = r1;
            }

            public final void onGetRuntimePermissionsBackup(byte[] bArr) {
                this.f$0.complete(bArr);
            }
        });
        try {
            return backup.get(BACKUP_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Slog.e(TAG, "Cannot create permission backup for " + user, e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void restoreRuntimePermissions(byte[] backup, UserHandle user) {
        synchronized (this.mLock) {
            this.mHasNoDelayedPermBackup.delete(user.getIdentifier());
            this.mPermissionControllerManager.restoreRuntimePermissionBackup(backup, user);
        }
    }

    /* access modifiers changed from: private */
    public void restoreDelayedRuntimePermissions(String packageName, UserHandle user) {
        synchronized (this.mLock) {
            if (!this.mHasNoDelayedPermBackup.get(user.getIdentifier(), false)) {
                this.mPermissionControllerManager.restoreDelayedRuntimePermissionBackup(packageName, user, this.mContext.getMainExecutor(), new Consumer(user) {
                    private final /* synthetic */ UserHandle f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        PermissionManagerService.this.lambda$restoreDelayedRuntimePermissions$0$PermissionManagerService(this.f$1, (Boolean) obj);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$restoreDelayedRuntimePermissions$0$PermissionManagerService(UserHandle user, Boolean hasMoreBackup) {
        if (!hasMoreBackup.booleanValue()) {
            synchronized (this.mLock) {
                this.mHasNoDelayedPermBackup.put(user.getIdentifier(), true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addOnRuntimePermissionStateChangedListener(PermissionManagerInternal.OnRuntimePermissionStateChangedListener listener) {
        synchronized (this.mLock) {
            this.mRuntimePermissionStateChangedListeners.add(listener);
        }
    }

    /* access modifiers changed from: private */
    public void removeOnRuntimePermissionStateChangedListener(PermissionManagerInternal.OnRuntimePermissionStateChangedListener listener) {
        synchronized (this.mLock) {
            this.mRuntimePermissionStateChangedListeners.remove(listener);
        }
    }

    private void notifyRuntimePermissionStateChanged(String packageName, int userId) {
        FgThread.getHandler().sendMessage(PooledLambda.obtainMessage($$Lambda$PermissionManagerService$NPd9St1HBvGAtg1uhMV2Upfww4g.INSTANCE, this, packageName, Integer.valueOf(userId)));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001a, code lost:
        if (r2 >= r0) goto L_0x0028;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001c, code lost:
        r1.get(r2).onRuntimePermissionStateChanged(r5, r6);
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0015, code lost:
        r0 = r1.size();
        r2 = 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doNotifyRuntimePermissionStateChanged(java.lang.String r5, int r6) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            java.util.ArrayList<android.permission.PermissionManagerInternal$OnRuntimePermissionStateChangedListener> r1 = r4.mRuntimePermissionStateChangedListeners     // Catch:{ all -> 0x0029 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0029 }
            if (r1 == 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            return
        L_0x000d:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0029 }
            java.util.ArrayList<android.permission.PermissionManagerInternal$OnRuntimePermissionStateChangedListener> r2 = r4.mRuntimePermissionStateChangedListeners     // Catch:{ all -> 0x0029 }
            r1.<init>(r2)     // Catch:{ all -> 0x0029 }
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            int r0 = r1.size()
            r2 = 0
        L_0x001a:
            if (r2 >= r0) goto L_0x0028
            java.lang.Object r3 = r1.get(r2)
            android.permission.PermissionManagerInternal$OnRuntimePermissionStateChangedListener r3 = (android.permission.PermissionManagerInternal.OnRuntimePermissionStateChangedListener) r3
            r3.onRuntimePermissionStateChanged(r5, r6)
            int r2 = r2 + 1
            goto L_0x001a
        L_0x0028:
            return
        L_0x0029:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0029 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.doNotifyRuntimePermissionStateChanged(java.lang.String, int):void");
    }

    private static boolean isImpliedPermissionGranted(PermissionsState permissionsState, String permName, int userId) {
        return FULLER_PERMISSION_MAP.containsKey(permName) && permissionsState.hasPermission(FULLER_PERMISSION_MAP.get(permName), userId);
    }

    /* access modifiers changed from: private */
    public PermissionGroupInfo getPermissionGroupInfo(String groupName, int flags, int callingUid) {
        PermissionGroupInfo generatePermissionGroupInfo;
        if (this.mPackageManagerInt.getInstantAppPackageName(callingUid) != null) {
            return null;
        }
        synchronized (this.mLock) {
            generatePermissionGroupInfo = PackageParser.generatePermissionGroupInfo(this.mSettings.mPermissionGroups.get(groupName), flags);
        }
        return generatePermissionGroupInfo;
    }

    /* access modifiers changed from: private */
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags, int callingUid) {
        ArrayList<PermissionGroupInfo> out;
        if (this.mPackageManagerInt.getInstantAppPackageName(callingUid) != null) {
            return null;
        }
        synchronized (this.mLock) {
            out = new ArrayList<>(this.mSettings.mPermissionGroups.size());
            for (PackageParser.PermissionGroup pg : this.mSettings.mPermissionGroups.values()) {
                out.add(PackageParser.generatePermissionGroupInfo(pg, flags));
            }
        }
        return out;
    }

    /* access modifiers changed from: private */
    public PermissionInfo getPermissionInfo(String permName, String packageName, int flags, int callingUid) {
        if (this.mPackageManagerInt.getInstantAppPackageName(callingUid) != null) {
            return null;
        }
        synchronized (this.mLock) {
            BasePermission bp = this.mSettings.getPermissionLocked(permName);
            if (bp == null) {
                return null;
            }
            PermissionInfo generatePermissionInfo = bp.generatePermissionInfo(adjustPermissionProtectionFlagsLocked(bp.getProtectionLevel(), packageName, callingUid), flags);
            return generatePermissionInfo;
        }
    }

    /* access modifiers changed from: private */
    public List<PermissionInfo> getPermissionInfoByGroup(String groupName, int flags, int callingUid) {
        if (this.mPackageManagerInt.getInstantAppPackageName(callingUid) != null) {
            return null;
        }
        synchronized (this.mLock) {
            if (groupName != null) {
                if (!this.mSettings.mPermissionGroups.containsKey(groupName)) {
                    return null;
                }
            }
            ArrayList<PermissionInfo> out = new ArrayList<>(10);
            for (BasePermission bp : this.mSettings.mPermissions.values()) {
                PermissionInfo pi = bp.generatePermissionInfo(groupName, flags);
                if (pi != null) {
                    out.add(pi);
                }
            }
            return out;
        }
    }

    private int adjustPermissionProtectionFlagsLocked(int protectionLevel, String packageName, int uid) {
        int appId;
        PackageParser.Package pkg;
        int protectionLevelMasked = protectionLevel & 3;
        if (protectionLevelMasked == 2 || (appId = UserHandle.getAppId(uid)) == 1000 || appId == 0 || appId == 2000 || (pkg = this.mPackageManagerInt.getPackage(packageName)) == null) {
            return protectionLevel;
        }
        if (pkg.applicationInfo.targetSdkVersion < 26) {
            return protectionLevelMasked;
        }
        PackageSetting ps = (PackageSetting) pkg.mExtras;
        if (ps == null || ps.getAppId() == appId) {
            return protectionLevel;
        }
        return protectionLevel;
    }

    /* access modifiers changed from: private */
    public void revokeRuntimePermissionsIfGroupChanged(PackageParser.Package newPackage, PackageParser.Package oldPackage, ArrayList<String> allPackageNames, PermissionManagerServiceInternal.PermissionCallback permissionCallback) {
        String newPermissionGroupName;
        String oldPermissionGroupName;
        int[] userIds;
        int numUserIds;
        int userIdNum;
        String permissionName;
        PermissionManagerService permissionManagerService = this;
        PackageParser.Package packageR = newPackage;
        PackageParser.Package packageR2 = oldPackage;
        int numOldPackagePermissions = packageR2.permissions.size();
        ArrayMap<String, String> oldPermissionNameToGroupName = new ArrayMap<>(numOldPackagePermissions);
        for (int i = 0; i < numOldPackagePermissions; i++) {
            PackageParser.Permission permission = (PackageParser.Permission) packageR2.permissions.get(i);
            if (permission.group != null) {
                oldPermissionNameToGroupName.put(permission.info.name, permission.group.info.name);
            }
        }
        int numNewPackagePermissions = packageR.permissions.size();
        int newPermissionNum = 0;
        while (newPermissionNum < numNewPackagePermissions) {
            PackageParser.Permission newPermission = (PackageParser.Permission) packageR.permissions.get(newPermissionNum);
            if ((newPermission.info.getProtection() & 1) != 0) {
                String permissionName2 = newPermission.info.name;
                String newPermissionGroupName2 = newPermission.group == null ? null : newPermission.group.info.name;
                String oldPermissionGroupName2 = oldPermissionNameToGroupName.get(permissionName2);
                if (newPermissionGroupName2 == null) {
                    String str = newPermissionGroupName2;
                    String str2 = permissionName2;
                } else if (!newPermissionGroupName2.equals(oldPermissionGroupName2)) {
                    int[] userIds2 = permissionManagerService.mUserManagerInt.getUserIds();
                    int numUserIds2 = userIds2.length;
                    int userIdNum2 = 0;
                    while (userIdNum2 < numUserIds2) {
                        int userId = userIds2[userIdNum2];
                        int numOldPackagePermissions2 = numOldPackagePermissions;
                        int numPackages = allPackageNames.size();
                        ArrayMap<String, String> oldPermissionNameToGroupName2 = oldPermissionNameToGroupName;
                        int packageNum = 0;
                        while (packageNum < numPackages) {
                            int numPackages2 = numPackages;
                            String packageName = allPackageNames.get(packageNum);
                            if (permissionManagerService.checkPermission(permissionName2, packageName, 0, userId) == 0) {
                                userIdNum = userIdNum2;
                                EventLog.writeEvent(1397638484, new Object[]{"72710897", Integer.valueOf(packageR.applicationInfo.uid), "Revoking permission " + permissionName2 + " from package " + packageName + " as the group changed from " + oldPermissionGroupName2 + " to " + newPermissionGroupName2});
                                numUserIds = numUserIds2;
                                userIds = userIds2;
                                oldPermissionGroupName = oldPermissionGroupName2;
                                newPermissionGroupName = newPermissionGroupName2;
                                permissionName = permissionName2;
                                try {
                                    revokeRuntimePermission(permissionName2, packageName, false, userId, permissionCallback);
                                } catch (IllegalArgumentException e) {
                                    Slog.e(TAG, "Could not revoke " + permissionName + " from " + packageName, e);
                                }
                            } else {
                                userIdNum = userIdNum2;
                                numUserIds = numUserIds2;
                                userIds = userIds2;
                                oldPermissionGroupName = oldPermissionGroupName2;
                                newPermissionGroupName = newPermissionGroupName2;
                                permissionName = permissionName2;
                            }
                            packageNum++;
                            permissionName2 = permissionName;
                            numPackages = numPackages2;
                            userIdNum2 = userIdNum;
                            numUserIds2 = numUserIds;
                            userIds2 = userIds;
                            oldPermissionGroupName2 = oldPermissionGroupName;
                            newPermissionGroupName2 = newPermissionGroupName;
                            permissionManagerService = this;
                        }
                        int i2 = numUserIds2;
                        int[] iArr = userIds2;
                        String str3 = oldPermissionGroupName2;
                        String str4 = newPermissionGroupName2;
                        String str5 = permissionName2;
                        int i3 = numPackages;
                        userIdNum2++;
                        PackageParser.Package packageR3 = oldPackage;
                        numOldPackagePermissions = numOldPackagePermissions2;
                        oldPermissionNameToGroupName = oldPermissionNameToGroupName2;
                        permissionManagerService = this;
                    }
                    int i4 = userIdNum2;
                    int i5 = numUserIds2;
                    int[] iArr2 = userIds2;
                    String str6 = oldPermissionGroupName2;
                    String str7 = newPermissionGroupName2;
                    String str8 = permissionName2;
                } else {
                    String str9 = newPermissionGroupName2;
                    String str10 = permissionName2;
                }
            }
            newPermissionNum++;
            permissionManagerService = this;
            PackageParser.Package packageR4 = oldPackage;
            numOldPackagePermissions = numOldPackagePermissions;
            oldPermissionNameToGroupName = oldPermissionNameToGroupName;
        }
    }

    /* access modifiers changed from: private */
    public void addAllPermissions(PackageParser.Package pkg, boolean chatty) {
        int N = pkg.permissions.size();
        for (int i = 0; i < N; i++) {
            PackageParser.Permission p = (PackageParser.Permission) pkg.permissions.get(i);
            p.info.flags &= -1073741825;
            synchronized (this.mLock) {
                if (pkg.applicationInfo.targetSdkVersion > 22) {
                    p.group = this.mSettings.mPermissionGroups.get(p.info.group);
                }
                if (p.tree) {
                    this.mSettings.putPermissionTreeLocked(p.info.name, BasePermission.createOrUpdate(this.mSettings.getPermissionTreeLocked(p.info.name), p, pkg, this.mSettings.getAllPermissionTreesLocked(), chatty));
                } else {
                    this.mSettings.putPermissionLocked(p.info.name, BasePermission.createOrUpdate(this.mSettings.getPermissionLocked(p.info.name), p, pkg, this.mSettings.getAllPermissionTreesLocked(), chatty));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void addAllPermissionGroups(PackageParser.Package pkg, boolean chatty) {
        int N = pkg.permissionGroups.size();
        for (int i = 0; i < N; i++) {
            PackageParser.PermissionGroup pg = (PackageParser.PermissionGroup) pkg.permissionGroups.get(i);
            PackageParser.PermissionGroup cur = this.mSettings.mPermissionGroups.get(pg.info.name);
            boolean isPackageUpdate = pg.info.packageName.equals(cur == null ? null : cur.info.packageName);
            if (cur == null || isPackageUpdate) {
                this.mSettings.mPermissionGroups.put(pg.info.name, pg);
            } else {
                Slog.w(TAG, "Permission group " + pg.info.name + " from package " + pg.info.packageName + " ignored: original from " + cur.info.packageName);
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeAllPermissions(PackageParser.Package pkg, boolean chatty) {
        ArraySet<String> appOpPkgs;
        ArraySet<String> appOpPkgs2;
        synchronized (this.mLock) {
            int N = pkg.permissions.size();
            for (int i = 0; i < N; i++) {
                PackageParser.Permission p = (PackageParser.Permission) pkg.permissions.get(i);
                BasePermission bp = this.mSettings.mPermissions.get(p.info.name);
                if (bp == null) {
                    bp = this.mSettings.mPermissionTrees.get(p.info.name);
                }
                if (bp != null && bp.isPermission(p)) {
                    bp.setPermission((PackageParser.Permission) null);
                }
                if (p.isAppOp() && (appOpPkgs2 = this.mSettings.mAppOpPermissionPackages.get(p.info.name)) != null) {
                    appOpPkgs2.remove(pkg.packageName);
                }
            }
            int N2 = pkg.requestedPermissions.size();
            for (int i2 = 0; i2 < N2; i2++) {
                String perm = (String) pkg.requestedPermissions.get(i2);
                if (this.mSettings.isPermissionAppOp(perm) && (appOpPkgs = this.mSettings.mAppOpPermissionPackages.get(perm)) != null) {
                    appOpPkgs.remove(pkg.packageName);
                    if (appOpPkgs.isEmpty()) {
                        this.mSettings.mAppOpPermissionPackages.remove(perm);
                    }
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* access modifiers changed from: private */
    public boolean addDynamicPermission(PermissionInfo info, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
        boolean added;
        boolean changed;
        if (this.mPackageManagerInt.getInstantAppPackageName(callingUid) != null) {
            throw new SecurityException("Instant apps can't add permissions");
        } else if (info.labelRes == 0 && info.nonLocalizedLabel == null) {
            throw new SecurityException("Label must be specified in permission");
        } else {
            BasePermission tree = this.mSettings.enforcePermissionTree(info.name, callingUid);
            synchronized (this.mLock) {
                BasePermission bp = this.mSettings.getPermissionLocked(info.name);
                added = bp == null;
                int fixedLevel = PermissionInfo.fixProtectionLevel(info.protectionLevel);
                if (added) {
                    enforcePermissionCapLocked(info, tree);
                    bp = new BasePermission(info.name, tree.getSourcePackageName(), 2);
                } else if (!bp.isDynamic()) {
                    throw new SecurityException("Not allowed to modify non-dynamic permission " + info.name);
                }
                changed = bp.addToTree(fixedLevel, info, tree);
                if (added) {
                    this.mSettings.putPermissionLocked(info.name, bp);
                }
            }
            if (changed && callback != null) {
                callback.onPermissionChanged();
            }
            return added;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0042, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void removeDynamicPermission(java.lang.String r7, int r8, com.android.server.pm.permission.PermissionManagerServiceInternal.PermissionCallback r9) {
        /*
            r6 = this;
            android.content.pm.PackageManagerInternal r0 = r6.mPackageManagerInt
            java.lang.String r0 = r0.getInstantAppPackageName(r8)
            if (r0 != 0) goto L_0x0046
            com.android.server.pm.permission.PermissionSettings r0 = r6.mSettings
            com.android.server.pm.permission.BasePermission r0 = r0.enforcePermissionTree(r7, r8)
            java.lang.Object r1 = r6.mLock
            monitor-enter(r1)
            com.android.server.pm.permission.PermissionSettings r2 = r6.mSettings     // Catch:{ all -> 0x0043 }
            com.android.server.pm.permission.BasePermission r2 = r2.getPermissionLocked(r7)     // Catch:{ all -> 0x0043 }
            if (r2 != 0) goto L_0x001b
            monitor-exit(r1)     // Catch:{ all -> 0x0043 }
            return
        L_0x001b:
            boolean r3 = r2.isDynamic()     // Catch:{ all -> 0x0043 }
            if (r3 == 0) goto L_0x0037
            java.lang.String r3 = "PackageManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0043 }
            r4.<init>()     // Catch:{ all -> 0x0043 }
            java.lang.String r5 = "Not allowed to modify non-dynamic permission "
            r4.append(r5)     // Catch:{ all -> 0x0043 }
            r4.append(r7)     // Catch:{ all -> 0x0043 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0043 }
            android.util.Slog.wtf(r3, r4)     // Catch:{ all -> 0x0043 }
        L_0x0037:
            com.android.server.pm.permission.PermissionSettings r3 = r6.mSettings     // Catch:{ all -> 0x0043 }
            r3.removePermissionLocked(r7)     // Catch:{ all -> 0x0043 }
            if (r9 == 0) goto L_0x0041
            r9.onPermissionRemoved()     // Catch:{ all -> 0x0043 }
        L_0x0041:
            monitor-exit(r1)     // Catch:{ all -> 0x0043 }
            return
        L_0x0043:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0043 }
            throw r2
        L_0x0046:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Instant applications don't have access to this method"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.removeDynamicPermission(java.lang.String, int, com.android.server.pm.permission.PermissionManagerServiceInternal$PermissionCallback):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0845, code lost:
        if (r11 != false) goto L_0x084a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0854, code lost:
        if (r29.isSystem() != false) goto L_0x0868;
     */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x02e7 A[Catch:{ all -> 0x03c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x02e9 A[Catch:{ all -> 0x03c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x02f4 A[Catch:{ all -> 0x03c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x02f6 A[Catch:{ all -> 0x03c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x02f9 A[Catch:{ all -> 0x03c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0357 A[Catch:{ all -> 0x03ad }] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x038a A[Catch:{ all -> 0x03ad }] */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0391 A[Catch:{ all -> 0x03ad }] */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0477 A[SYNTHETIC, Splitter:B:281:0x0477] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0494  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x04a6  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x04a8  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x04b5 A[Catch:{ all -> 0x05b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x04b7 A[Catch:{ all -> 0x05b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x04ba A[Catch:{ all -> 0x05b8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x0535 A[Catch:{ all -> 0x05a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0584 A[Catch:{ all -> 0x05a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x058a A[Catch:{ all -> 0x05a0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x08ac  */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08b3 A[LOOP:5: B:488:0x08b1->B:489:0x08b3, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void restorePermissionState(android.content.pm.PackageParser.Package r37, boolean r38, java.lang.String r39, com.android.server.pm.permission.PermissionManagerServiceInternal.PermissionCallback r40) {
        /*
            r36 = this;
            r7 = r36
            r8 = r37
            r9 = r38
            r10 = r39
            r11 = r40
            java.lang.Object r0 = r8.mExtras
            r12 = r0
            com.android.server.pm.PackageSetting r12 = (com.android.server.pm.PackageSetting) r12
            if (r12 != 0) goto L_0x0012
            return
        L_0x0012:
            com.android.server.pm.permission.PermissionsState r13 = r12.getPermissionsState()
            r1 = r13
            com.android.server.pm.UserManagerService r0 = com.android.server.pm.UserManagerService.getInstance()
            int[] r14 = r0.getUserIds()
            r2 = 0
            int[] r3 = EMPTY_INT_ARRAY
            r4 = 0
            r0 = 0
            if (r9 == 0) goto L_0x005e
            r12.setInstallPermissionsFixed(r0)
            boolean r5 = r12.isSharedUser()
            if (r5 != 0) goto L_0x003b
            com.android.server.pm.permission.PermissionsState r5 = new com.android.server.pm.permission.PermissionsState
            r5.<init>(r13)
            r1 = r5
            r13.reset()
            r15 = r1
            r5 = r2
            goto L_0x0060
        L_0x003b:
            java.lang.Object r5 = r7.mLock
            monitor-enter(r5)
            com.android.server.pm.SharedUserSetting r6 = r12.getSharedUser()     // Catch:{ all -> 0x005b }
            com.android.server.pm.UserManagerService r15 = com.android.server.pm.UserManagerService.getInstance()     // Catch:{ all -> 0x005b }
            int[] r15 = r15.getUserIds()     // Catch:{ all -> 0x005b }
            int[] r6 = r7.revokeUnusedSharedUserPermissionsLocked(r6, r15)     // Catch:{ all -> 0x005b }
            r3 = r6
            boolean r6 = com.android.internal.util.ArrayUtils.isEmpty(r3)     // Catch:{ all -> 0x005b }
            if (r6 != 0) goto L_0x0057
            r2 = 1
        L_0x0057:
            monitor-exit(r5)     // Catch:{ all -> 0x005b }
            r15 = r1
            r5 = r2
            goto L_0x0060
        L_0x005b:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x005b }
            throw r0
        L_0x005e:
            r15 = r1
            r5 = r2
        L_0x0060:
            int[] r1 = r7.mGlobalGids
            r13.setGlobalGids(r1)
            java.lang.Object r2 = r7.mLock
            monitor-enter(r2)
            android.util.ArraySet r1 = new android.util.ArraySet     // Catch:{ all -> 0x08e5 }
            r1.<init>()     // Catch:{ all -> 0x08e5 }
            java.util.ArrayList r6 = r8.requestedPermissions     // Catch:{ all -> 0x08e5 }
            int r6 = r6.size()     // Catch:{ all -> 0x08e5 }
            r16 = 0
            r35 = r4
            r4 = r3
            r3 = r16
            r16 = r35
        L_0x007c:
            if (r3 >= r6) goto L_0x0830
            java.util.ArrayList r0 = r8.requestedPermissions     // Catch:{ all -> 0x081a }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x081a }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ all -> 0x081a }
            r18 = r5
            com.android.server.pm.permission.PermissionSettings r5 = r7.mSettings     // Catch:{ all -> 0x0803 }
            com.android.server.pm.permission.BasePermission r5 = r5.getPermissionLocked(r0)     // Catch:{ all -> 0x0803 }
            r19 = r6
            android.content.pm.ApplicationInfo r6 = r8.applicationInfo     // Catch:{ all -> 0x0803 }
            int r6 = r6.targetSdkVersion     // Catch:{ all -> 0x0803 }
            r11 = 23
            if (r6 < r11) goto L_0x009a
            r6 = 1
            goto L_0x009b
        L_0x009a:
            r6 = 0
        L_0x009b:
            r11 = 0
            if (r5 == 0) goto L_0x07bb
            com.android.server.pm.PackageSettingBase r20 = r5.getSourcePackageSetting()     // Catch:{ all -> 0x07ac }
            if (r20 != 0) goto L_0x00b7
            r21 = r0
            r23 = r1
            r25 = r3
            r22 = r4
            r9 = r8
            r20 = r11
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
            goto L_0x07cc
        L_0x00b7:
            boolean r20 = r15.hasRequestedPermission((java.lang.String) r0)     // Catch:{ all -> 0x07ac }
            if (r20 != 0) goto L_0x014f
            r20 = r11
            java.util.ArrayList r11 = r8.implicitPermissions     // Catch:{ all -> 0x0139 }
            boolean r11 = r11.contains(r0)     // Catch:{ all -> 0x0139 }
            if (r11 != 0) goto L_0x00e6
            java.lang.String r11 = "android.permission.ACTIVITY_RECOGNITION"
            boolean r11 = r0.equals(r11)     // Catch:{ all -> 0x00d4 }
            if (r11 == 0) goto L_0x00d0
            goto L_0x00e6
        L_0x00d0:
            r22 = r4
            goto L_0x0153
        L_0x00d4:
            r0 = move-exception
            r1 = r40
            r17 = r2
            r3 = r4
            r11 = r9
            r32 = r14
            r4 = r16
            r14 = r7
            r9 = r8
            r8 = r15
            r7 = r18
            goto L_0x08f0
        L_0x00e6:
            java.util.ArrayList r11 = r8.implicitPermissions     // Catch:{ all -> 0x0139 }
            boolean r11 = r11.contains(r0)     // Catch:{ all -> 0x0139 }
            if (r11 == 0) goto L_0x00f5
            r1.add(r0)     // Catch:{ all -> 0x00d4 }
            r22 = r4
            goto L_0x0153
        L_0x00f5:
            java.util.ArrayList r11 = android.permission.PermissionManager.SPLIT_PERMISSIONS     // Catch:{ all -> 0x0139 }
            int r11 = r11.size()     // Catch:{ all -> 0x0139 }
            r21 = 0
            r22 = r4
            r4 = r21
        L_0x0101:
            if (r4 >= r11) goto L_0x0136
            r21 = r11
            java.util.ArrayList r11 = android.permission.PermissionManager.SPLIT_PERMISSIONS     // Catch:{ all -> 0x0170 }
            java.lang.Object r11 = r11.get(r4)     // Catch:{ all -> 0x0170 }
            android.permission.PermissionManager$SplitPermissionInfo r11 = (android.permission.PermissionManager.SplitPermissionInfo) r11     // Catch:{ all -> 0x0170 }
            java.lang.String r23 = r11.getSplitPermission()     // Catch:{ all -> 0x0170 }
            r24 = r23
            java.util.List r9 = r11.getNewPermissions()     // Catch:{ all -> 0x0170 }
            boolean r9 = r9.contains(r0)     // Catch:{ all -> 0x0170 }
            if (r9 == 0) goto L_0x012d
            r9 = r24
            boolean r23 = r15.hasInstallPermission(r9)     // Catch:{ all -> 0x0170 }
            if (r23 == 0) goto L_0x012f
            r20 = r9
            r1.add(r0)     // Catch:{ all -> 0x0170 }
            r4 = r20
            goto L_0x0155
        L_0x012d:
            r9 = r24
        L_0x012f:
            int r4 = r4 + 1
            r9 = r38
            r11 = r21
            goto L_0x0101
        L_0x0136:
            r21 = r11
            goto L_0x0153
        L_0x0139:
            r0 = move-exception
            r22 = r4
            r11 = r38
            r1 = r40
            r17 = r2
            r9 = r8
            r32 = r14
            r8 = r15
            r4 = r16
            r3 = r22
            r14 = r7
            r7 = r18
            goto L_0x08f0
        L_0x014f:
            r22 = r4
            r20 = r11
        L_0x0153:
            r4 = r20
        L_0x0155:
            android.content.pm.ApplicationInfo r9 = r8.applicationInfo     // Catch:{ all -> 0x0795 }
            boolean r9 = r9.isInstantApp()     // Catch:{ all -> 0x0795 }
            if (r9 == 0) goto L_0x0184
            boolean r9 = r5.isInstant()     // Catch:{ all -> 0x0170 }
            if (r9 != 0) goto L_0x0184
            r23 = r1
            r25 = r3
            r9 = r8
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
            goto L_0x07ea
        L_0x0170:
            r0 = move-exception
            r11 = r38
            r1 = r40
            r17 = r2
            r9 = r8
            r32 = r14
            r8 = r15
            r4 = r16
            r3 = r22
            r14 = r7
            r7 = r18
            goto L_0x08f0
        L_0x0184:
            boolean r9 = r5.isRuntimeOnly()     // Catch:{ all -> 0x0795 }
            if (r9 == 0) goto L_0x0199
            if (r6 != 0) goto L_0x0199
            r23 = r1
            r25 = r3
            r9 = r8
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
            goto L_0x07ea
        L_0x0199:
            java.lang.String r9 = r5.getName()     // Catch:{ all -> 0x0795 }
            r11 = 0
            r20 = 1
            boolean r21 = r5.isAppOp()     // Catch:{ all -> 0x0795 }
            if (r21 == 0) goto L_0x01b2
            r21 = r0
            com.android.server.pm.permission.PermissionSettings r0 = r7.mSettings     // Catch:{ all -> 0x0170 }
            r23 = r1
            java.lang.String r1 = r8.packageName     // Catch:{ all -> 0x0170 }
            r0.addAppOpPackage(r9, r1)     // Catch:{ all -> 0x0170 }
            goto L_0x01b6
        L_0x01b2:
            r21 = r0
            r23 = r1
        L_0x01b6:
            boolean r0 = r5.isNormal()     // Catch:{ all -> 0x0795 }
            if (r0 == 0) goto L_0x01c1
            r20 = 2
            r0 = r20
            goto L_0x01f5
        L_0x01c1:
            boolean r0 = r5.isRuntime()     // Catch:{ all -> 0x0795 }
            if (r0 == 0) goto L_0x01de
            java.lang.String r0 = r5.getName()     // Catch:{ all -> 0x0170 }
            boolean r0 = r15.hasInstallPermission(r0)     // Catch:{ all -> 0x0170 }
            if (r0 != 0) goto L_0x01d9
            if (r4 == 0) goto L_0x01d4
            goto L_0x01d9
        L_0x01d4:
            r20 = 3
            r0 = r20
            goto L_0x01f5
        L_0x01d9:
            r20 = 4
            r0 = r20
            goto L_0x01f5
        L_0x01de:
            boolean r0 = r5.isSignature()     // Catch:{ all -> 0x0795 }
            if (r0 == 0) goto L_0x01f3
            boolean r0 = r7.grantSignaturePermission(r9, r8, r5, r15)     // Catch:{ all -> 0x0170 }
            r11 = r0
            if (r11 == 0) goto L_0x01f0
            r20 = 2
            r0 = r20
            goto L_0x01f5
        L_0x01f0:
            r0 = r20
            goto L_0x01f5
        L_0x01f3:
            r0 = r20
        L_0x01f5:
            r1 = 1
            if (r0 == r1) goto L_0x06d1
            boolean r1 = r12.isSystem()     // Catch:{ all -> 0x06c7 }
            if (r1 != 0) goto L_0x021d
            boolean r1 = r12.areInstallPermissionsFixed()     // Catch:{ all -> 0x0170 }
            if (r1 == 0) goto L_0x021d
            boolean r1 = r5.isRuntime()     // Catch:{ all -> 0x0170 }
            if (r1 != 0) goto L_0x021d
            if (r11 != 0) goto L_0x021d
            boolean r1 = r15.hasInstallPermission(r9)     // Catch:{ all -> 0x0170 }
            if (r1 != 0) goto L_0x021d
            boolean r1 = r7.mSystemReady     // Catch:{ all -> 0x0170 }
            if (r1 == 0) goto L_0x021d
            boolean r1 = r7.isNewPlatformPermissionForPackage(r9, r8)     // Catch:{ all -> 0x0170 }
            if (r1 != 0) goto L_0x021d
            r0 = 1
        L_0x021d:
            r1 = 2
            if (r0 == r1) goto L_0x062b
            r1 = 3
            if (r0 == r1) goto L_0x0424
            r1 = 4
            if (r0 == r1) goto L_0x0244
            if (r10 == 0) goto L_0x0239
            java.lang.String r1 = r8.packageName     // Catch:{ all -> 0x0170 }
            boolean r1 = r10.equals(r1)     // Catch:{ all -> 0x0170 }
            r25 = r3
            r9 = r8
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
            goto L_0x07ea
        L_0x0239:
            r25 = r3
            r9 = r8
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
            goto L_0x07ea
        L_0x0244:
            com.android.server.pm.permission.PermissionsState$PermissionState r1 = r15.getInstallPermissionState(r9)     // Catch:{ all -> 0x040c }
            if (r1 == 0) goto L_0x0250
            int r25 = r1.getFlags()     // Catch:{ all -> 0x0170 }
            goto L_0x0252
        L_0x0250:
            r25 = 0
        L_0x0252:
            if (r4 != 0) goto L_0x0258
            r26 = r0
            r0 = r5
            goto L_0x0260
        L_0x0258:
            r26 = r0
            com.android.server.pm.permission.PermissionSettings r0 = r7.mSettings     // Catch:{ all -> 0x040c }
            com.android.server.pm.permission.BasePermission r0 = r0.getPermissionLocked(r4)     // Catch:{ all -> 0x040c }
        L_0x0260:
            r27 = r4
            int r4 = r15.revokeInstallPermission(r0)     // Catch:{ all -> 0x040c }
            r28 = r11
            r11 = -1
            if (r4 == r11) goto L_0x028c
            r4 = 48127(0xbbff, float:6.744E-41)
            r29 = r12
            r12 = 0
            r15.updatePermissionFlags(r0, r11, r4, r12)     // Catch:{ all -> 0x0276 }
            r4 = 1
            goto L_0x0290
        L_0x0276:
            r0 = move-exception
            r11 = r38
            r1 = r40
            r17 = r2
            r9 = r8
            r32 = r14
            r8 = r15
            r4 = r16
            r3 = r22
            r12 = r29
            r14 = r7
            r7 = r18
            goto L_0x08f0
        L_0x028c:
            r29 = r12
            r4 = r16
        L_0x0290:
            boolean r11 = r5.isHardRestricted()     // Catch:{ all -> 0x03f2 }
            boolean r12 = r5.isSoftRestricted()     // Catch:{ all -> 0x03f2 }
            r30 = r0
            int r0 = r14.length     // Catch:{ all -> 0x03f2 }
            r16 = r4
            r10 = r22
            r4 = 0
            r35 = r25
            r25 = r3
            r3 = r35
        L_0x02a6:
            if (r4 >= r0) goto L_0x03e0
            r22 = r14[r4]     // Catch:{ all -> 0x03c7 }
            r31 = r22
            r32 = r0
            com.android.server.policy.PermissionPolicyInternal r0 = r7.mPermissionPolicyInternal     // Catch:{ all -> 0x03c7 }
            if (r0 == 0) goto L_0x02d6
            com.android.server.policy.PermissionPolicyInternal r0 = r7.mPermissionPolicyInternal     // Catch:{ all -> 0x02be }
            r8 = r31
            boolean r0 = r0.isInitialized(r8)     // Catch:{ all -> 0x02be }
            if (r0 == 0) goto L_0x02d8
            r0 = 1
            goto L_0x02d9
        L_0x02be:
            r0 = move-exception
            r9 = r37
            r11 = r38
            r1 = r40
            r17 = r2
            r3 = r10
            r32 = r14
            r8 = r15
            r4 = r16
            r12 = r29
            r10 = r39
            r14 = r7
            r7 = r18
            goto L_0x08f0
        L_0x02d6:
            r8 = r31
        L_0x02d8:
            r0 = 0
        L_0x02d9:
            r22 = 0
            r31 = r9
            java.lang.String r9 = r5.name     // Catch:{ all -> 0x03c7 }
            int r9 = r15.getPermissionFlags(r9, r8)     // Catch:{ all -> 0x03c7 }
            r9 = r9 & 14336(0x3800, float:2.0089E-41)
            if (r9 == 0) goto L_0x02e9
            r9 = 1
            goto L_0x02ea
        L_0x02e9:
            r9 = 0
        L_0x02ea:
            java.lang.String r7 = r5.name     // Catch:{ all -> 0x03c7 }
            int r7 = r15.getPermissionFlags(r7, r8)     // Catch:{ all -> 0x03c7 }
            r7 = r7 & 16384(0x4000, float:2.2959E-41)
            if (r7 == 0) goto L_0x02f6
            r7 = 1
            goto L_0x02f7
        L_0x02f6:
            r7 = 0
        L_0x02f7:
            if (r6 == 0) goto L_0x0357
            if (r0 == 0) goto L_0x0326
            if (r11 == 0) goto L_0x0326
            if (r9 != 0) goto L_0x0321
            if (r1 == 0) goto L_0x0316
            boolean r33 = r1.isGranted()     // Catch:{ all -> 0x03c7 }
            if (r33 == 0) goto L_0x0316
            r33 = r1
            int r1 = r13.revokeRuntimePermission(r5, r8)     // Catch:{ all -> 0x03c7 }
            r34 = r15
            r15 = -1
            if (r1 == r15) goto L_0x031a
            r1 = 1
            r22 = r1
            goto L_0x031a
        L_0x0316:
            r33 = r1
            r34 = r15
        L_0x031a:
            if (r7 != 0) goto L_0x0336
            r3 = r3 | 16384(0x4000, float:2.2959E-41)
            r22 = 1
            goto L_0x0336
        L_0x0321:
            r33 = r1
            r34 = r15
            goto L_0x0336
        L_0x0326:
            r33 = r1
            r34 = r15
            if (r0 == 0) goto L_0x0336
            if (r12 == 0) goto L_0x0336
            if (r9 != 0) goto L_0x0336
            if (r7 != 0) goto L_0x0336
            r3 = r3 | 16384(0x4000, float:2.2959E-41)
            r22 = 1
        L_0x0336:
            r1 = r3 & 64
            if (r1 == 0) goto L_0x033e
            r3 = r3 & -65
            r22 = 1
        L_0x033e:
            r1 = r3 & 8
            if (r1 == 0) goto L_0x0347
            r3 = r3 & -9
            r22 = 1
            goto L_0x037c
        L_0x0347:
            if (r0 == 0) goto L_0x034d
            if (r11 == 0) goto L_0x034d
            if (r9 == 0) goto L_0x037c
        L_0x034d:
            int r1 = r13.grantRuntimePermission(r5, r8)     // Catch:{ all -> 0x03ad }
            r15 = -1
            if (r1 == r15) goto L_0x037c
            r22 = 1
            goto L_0x037c
        L_0x0357:
            r33 = r1
            r34 = r15
            java.lang.String r1 = r5.name     // Catch:{ all -> 0x03ad }
            boolean r1 = r13.hasRuntimePermission(r1, r8)     // Catch:{ all -> 0x03ad }
            if (r1 != 0) goto L_0x036e
            int r1 = r13.grantRuntimePermission(r5, r8)     // Catch:{ all -> 0x03ad }
            r15 = -1
            if (r1 == r15) goto L_0x036e
            r3 = r3 | 64
            r22 = 1
        L_0x036e:
            if (r0 == 0) goto L_0x037c
            if (r11 != 0) goto L_0x0374
            if (r12 == 0) goto L_0x037c
        L_0x0374:
            if (r9 != 0) goto L_0x037c
            if (r7 != 0) goto L_0x037c
            r3 = r3 | 16384(0x4000, float:2.2959E-41)
            r22 = 1
        L_0x037c:
            if (r0 == 0) goto L_0x038f
            if (r11 != 0) goto L_0x0382
            if (r12 == 0) goto L_0x0384
        L_0x0382:
            if (r9 == 0) goto L_0x038f
        L_0x0384:
            if (r7 == 0) goto L_0x038f
            r1 = r3 & -16385(0xffffffffffffbfff, float:NaN)
            if (r6 != 0) goto L_0x038c
            r1 = r1 | 64
        L_0x038c:
            r22 = 1
            r3 = r1
        L_0x038f:
            if (r22 == 0) goto L_0x0396
            int[] r1 = com.android.internal.util.ArrayUtils.appendInt(r10, r8)     // Catch:{ all -> 0x03ad }
            r10 = r1
        L_0x0396:
            r1 = 64511(0xfbff, float:9.0399E-41)
            r13.updatePermissionFlags(r5, r8, r1, r3)     // Catch:{ all -> 0x03ad }
            int r4 = r4 + 1
            r7 = r36
            r8 = r37
            r9 = r31
            r0 = r32
            r1 = r33
            r15 = r34
            goto L_0x02a6
        L_0x03ad:
            r0 = move-exception
            r9 = r37
            r11 = r38
            r1 = r40
            r17 = r2
            r3 = r10
            r32 = r14
            r4 = r16
            r7 = r18
            r12 = r29
            r8 = r34
            r14 = r36
            r10 = r39
            goto L_0x08f0
        L_0x03c7:
            r0 = move-exception
            r9 = r37
            r11 = r38
            r1 = r40
            r17 = r2
            r3 = r10
            r32 = r14
            r8 = r15
            r4 = r16
            r7 = r18
            r12 = r29
            r14 = r36
            r10 = r39
            goto L_0x08f0
        L_0x03e0:
            r33 = r1
            r31 = r9
            r34 = r15
            r9 = r37
            r4 = r10
            r32 = r14
            r8 = r34
            r7 = 0
            r10 = r39
            goto L_0x07ec
        L_0x03f2:
            r0 = move-exception
            r16 = r4
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r32 = r14
            r8 = r15
            r7 = r18
            r3 = r22
            r12 = r29
            r14 = r36
            goto L_0x08f0
        L_0x040c:
            r0 = move-exception
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r32 = r14
            r8 = r15
            r4 = r16
            r7 = r18
            r3 = r22
            r14 = r36
            goto L_0x08f0
        L_0x0424:
            r26 = r0
            r25 = r3
            r27 = r4
            r31 = r9
            r28 = r11
            r29 = r12
            r34 = r15
            boolean r0 = r5.isHardRestricted()     // Catch:{ all -> 0x0610 }
            boolean r1 = r5.isSoftRestricted()     // Catch:{ all -> 0x0610 }
            int r3 = r14.length     // Catch:{ all -> 0x0610 }
            r7 = r22
            r4 = 0
        L_0x043e:
            if (r4 >= r3) goto L_0x0602
            r8 = r14[r4]     // Catch:{ all -> 0x05e8 }
            r9 = r36
            com.android.server.policy.PermissionPolicyInternal r10 = r9.mPermissionPolicyInternal     // Catch:{ all -> 0x05e8 }
            if (r10 == 0) goto L_0x046b
            com.android.server.policy.PermissionPolicyInternal r10 = r9.mPermissionPolicyInternal     // Catch:{ all -> 0x0452 }
            boolean r10 = r10.isInitialized(r8)     // Catch:{ all -> 0x0452 }
            if (r10 == 0) goto L_0x046b
            r10 = 1
            goto L_0x046c
        L_0x0452:
            r0 = move-exception
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r7
            r32 = r14
            r4 = r16
            r7 = r18
            r12 = r29
            r8 = r34
            r14 = r9
            r9 = r37
            goto L_0x08f0
        L_0x046b:
            r10 = 0
        L_0x046c:
            r12 = r31
            r11 = r34
            com.android.server.pm.permission.PermissionsState$PermissionState r15 = r11.getRuntimePermissionState(r12, r8)     // Catch:{ all -> 0x05cf }
            if (r15 == 0) goto L_0x0494
            int r22 = r15.getFlags()     // Catch:{ all -> 0x047c }
            goto L_0x0496
        L_0x047c:
            r0 = move-exception
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r7
            r8 = r11
            r32 = r14
            r4 = r16
            r7 = r18
            r12 = r29
            r11 = r38
            r14 = r9
            r9 = r37
            goto L_0x08f0
        L_0x0494:
            r22 = 0
        L_0x0496:
            r30 = r22
            r22 = 0
            r31 = r3
            java.lang.String r3 = r5.name     // Catch:{ all -> 0x05cf }
            int r3 = r11.getPermissionFlags(r3, r8)     // Catch:{ all -> 0x05cf }
            r3 = r3 & 14336(0x3800, float:2.0089E-41)
            if (r3 == 0) goto L_0x04a8
            r3 = 1
            goto L_0x04a9
        L_0x04a8:
            r3 = 0
        L_0x04a9:
            r32 = r14
            java.lang.String r14 = r5.name     // Catch:{ all -> 0x05b8 }
            int r14 = r11.getPermissionFlags(r14, r8)     // Catch:{ all -> 0x05b8 }
            r14 = r14 & 16384(0x4000, float:2.2959E-41)
            if (r14 == 0) goto L_0x04b7
            r14 = 1
            goto L_0x04b8
        L_0x04b7:
            r14 = 0
        L_0x04b8:
            if (r6 == 0) goto L_0x0535
            if (r10 == 0) goto L_0x04e8
            if (r0 == 0) goto L_0x04e8
            if (r3 != 0) goto L_0x04e3
            if (r15 == 0) goto L_0x04d5
            boolean r33 = r15.isGranted()     // Catch:{ all -> 0x05b8 }
            if (r33 == 0) goto L_0x04d5
            int r9 = r13.revokeRuntimePermission(r5, r8)     // Catch:{ all -> 0x05b8 }
            r34 = r11
            r11 = -1
            if (r9 == r11) goto L_0x04d7
            r9 = 1
            r22 = r9
            goto L_0x04d7
        L_0x04d5:
            r34 = r11
        L_0x04d7:
            if (r14 != 0) goto L_0x04e0
            r9 = r30
            r9 = r9 | 16384(0x4000, float:2.2959E-41)
            r22 = 1
            goto L_0x04f8
        L_0x04e0:
            r9 = r30
            goto L_0x04f8
        L_0x04e3:
            r34 = r11
            r9 = r30
            goto L_0x04f8
        L_0x04e8:
            r34 = r11
            r9 = r30
            if (r10 == 0) goto L_0x04f8
            if (r1 == 0) goto L_0x04f8
            if (r3 != 0) goto L_0x04f8
            if (r14 != 0) goto L_0x04f8
            r9 = r9 | 16384(0x4000, float:2.2959E-41)
            r22 = 1
        L_0x04f8:
            r11 = r9 & 64
            if (r11 == 0) goto L_0x0500
            r9 = r9 & -65
            r22 = 1
        L_0x0500:
            r11 = r9 & 8
            if (r11 == 0) goto L_0x050c
            r9 = r9 & -9
            r22 = 1
            r30 = r15
            goto L_0x0576
        L_0x050c:
            if (r10 == 0) goto L_0x0516
            if (r0 == 0) goto L_0x0516
            if (r3 == 0) goto L_0x0513
            goto L_0x0516
        L_0x0513:
            r30 = r9
            goto L_0x0530
        L_0x0516:
            if (r15 == 0) goto L_0x052e
            boolean r11 = r15.isGranted()     // Catch:{ all -> 0x05a0 }
            if (r11 == 0) goto L_0x052e
            int r11 = r13.grantRuntimePermission(r5, r8)     // Catch:{ all -> 0x05a0 }
            r30 = r9
            r9 = -1
            if (r11 != r9) goto L_0x0530
            r22 = 1
            r9 = r30
            r30 = r15
            goto L_0x0576
        L_0x052e:
            r30 = r9
        L_0x0530:
            r9 = r30
            r30 = r15
            goto L_0x0576
        L_0x0535:
            r34 = r11
            r9 = r30
            if (r15 != 0) goto L_0x0554
            java.lang.String r11 = "android"
            r30 = r15
            java.lang.String r15 = r5.getSourcePackageName()     // Catch:{ all -> 0x05a0 }
            boolean r11 = r11.equals(r15)     // Catch:{ all -> 0x05a0 }
            if (r11 == 0) goto L_0x0556
            boolean r11 = r5.isRemoved()     // Catch:{ all -> 0x05a0 }
            if (r11 != 0) goto L_0x0556
            r9 = r9 | 72
            r22 = 1
            goto L_0x0556
        L_0x0554:
            r30 = r15
        L_0x0556:
            java.lang.String r11 = r5.name     // Catch:{ all -> 0x05a0 }
            boolean r11 = r13.hasRuntimePermission(r11, r8)     // Catch:{ all -> 0x05a0 }
            if (r11 != 0) goto L_0x0568
            int r11 = r13.grantRuntimePermission(r5, r8)     // Catch:{ all -> 0x05a0 }
            r15 = -1
            if (r11 == r15) goto L_0x0568
            r11 = 1
            r22 = r11
        L_0x0568:
            if (r10 == 0) goto L_0x0576
            if (r0 != 0) goto L_0x056e
            if (r1 == 0) goto L_0x0576
        L_0x056e:
            if (r3 != 0) goto L_0x0576
            if (r14 != 0) goto L_0x0576
            r9 = r9 | 16384(0x4000, float:2.2959E-41)
            r22 = 1
        L_0x0576:
            if (r10 == 0) goto L_0x0588
            if (r0 != 0) goto L_0x057c
            if (r1 == 0) goto L_0x057e
        L_0x057c:
            if (r3 == 0) goto L_0x0588
        L_0x057e:
            if (r14 == 0) goto L_0x0588
            r9 = r9 & -16385(0xffffffffffffbfff, float:NaN)
            if (r6 != 0) goto L_0x0586
            r9 = r9 | 64
        L_0x0586:
            r22 = 1
        L_0x0588:
            if (r22 == 0) goto L_0x058f
            int[] r11 = com.android.internal.util.ArrayUtils.appendInt(r7, r8)     // Catch:{ all -> 0x05a0 }
            r7 = r11
        L_0x058f:
            r11 = 64511(0xfbff, float:9.0399E-41)
            r13.updatePermissionFlags(r5, r8, r11, r9)     // Catch:{ all -> 0x05a0 }
            int r4 = r4 + 1
            r3 = r31
            r14 = r32
            r31 = r12
            goto L_0x043e
        L_0x05a0:
            r0 = move-exception
            r14 = r36
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r7
            r4 = r16
            r7 = r18
            r12 = r29
            r8 = r34
            goto L_0x08f0
        L_0x05b8:
            r0 = move-exception
            r14 = r36
            r9 = r37
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r7
            r8 = r11
            r4 = r16
            r7 = r18
            r12 = r29
            r11 = r38
            goto L_0x08f0
        L_0x05cf:
            r0 = move-exception
            r32 = r14
            r14 = r36
            r9 = r37
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r7
            r8 = r11
            r4 = r16
            r7 = r18
            r12 = r29
            r11 = r38
            goto L_0x08f0
        L_0x05e8:
            r0 = move-exception
            r32 = r14
            r14 = r36
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r7
            r4 = r16
            r7 = r18
            r12 = r29
            r8 = r34
            goto L_0x08f0
        L_0x0602:
            r32 = r14
            r12 = r31
            r9 = r37
            r10 = r39
            r4 = r7
            r8 = r34
            r7 = 0
            goto L_0x07ec
        L_0x0610:
            r0 = move-exception
            r32 = r14
            r14 = r36
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            r12 = r29
            r8 = r34
            goto L_0x08f0
        L_0x062b:
            r26 = r0
            r25 = r3
            r27 = r4
            r28 = r11
            r29 = r12
            r32 = r14
            r34 = r15
            r12 = r9
            com.android.server.pm.UserManagerService r0 = com.android.server.pm.UserManagerService.getInstance()     // Catch:{ all -> 0x06ae }
            int[] r0 = r0.getUserIds()     // Catch:{ all -> 0x06ae }
            int r1 = r0.length     // Catch:{ all -> 0x06ae }
            r4 = r22
            r3 = 0
        L_0x0646:
            if (r3 >= r1) goto L_0x067e
            r7 = r0[r3]     // Catch:{ all -> 0x0666 }
            r8 = r34
            com.android.server.pm.permission.PermissionsState$PermissionState r9 = r8.getRuntimePermissionState(r12, r7)     // Catch:{ all -> 0x0698 }
            if (r9 == 0) goto L_0x0661
            r8.revokeRuntimePermission(r5, r7)     // Catch:{ all -> 0x0698 }
            r9 = 64511(0xfbff, float:9.0399E-41)
            r10 = 0
            r8.updatePermissionFlags(r5, r7, r9, r10)     // Catch:{ all -> 0x0698 }
            int[] r9 = com.android.internal.util.ArrayUtils.appendInt(r4, r7)     // Catch:{ all -> 0x0698 }
            r4 = r9
        L_0x0661:
            int r3 = r3 + 1
            r34 = r8
            goto L_0x0646
        L_0x0666:
            r0 = move-exception
            r8 = r34
            r14 = r36
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r4
            r4 = r16
            r7 = r18
            r12 = r29
            goto L_0x08f0
        L_0x067e:
            r8 = r34
            int r0 = r13.grantInstallPermission(r5)     // Catch:{ all -> 0x0698 }
            r1 = -1
            if (r0 == r1) goto L_0x0691
            r0 = 1
            r9 = r37
            r10 = r39
            r16 = r0
            r7 = 0
            goto L_0x07ec
        L_0x0691:
            r9 = r37
            r10 = r39
            r7 = 0
            goto L_0x07ec
        L_0x0698:
            r0 = move-exception
            r14 = r36
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r3 = r4
            r4 = r16
            r7 = r18
            r12 = r29
            goto L_0x08f0
        L_0x06ae:
            r0 = move-exception
            r8 = r34
            r14 = r36
            r9 = r37
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x06c7:
            r0 = move-exception
            r32 = r14
            r8 = r15
            r14 = r36
            r9 = r37
            goto L_0x079c
        L_0x06d1:
            r25 = r3
            r27 = r4
            r28 = r11
            r29 = r12
            r32 = r14
            r8 = r15
            r12 = r9
            int r1 = r13.revokeInstallPermission(r5)     // Catch:{ all -> 0x077e }
            r3 = -1
            if (r1 == r3) goto L_0x075f
            r1 = 64511(0xfbff, float:9.0399E-41)
            r7 = 0
            r13.updatePermissionFlags(r5, r3, r1, r7)     // Catch:{ all -> 0x077e }
            r4 = 1
            java.lang.String r1 = "PackageManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x074a }
            r3.<init>()     // Catch:{ all -> 0x074a }
            java.lang.String r9 = "Un-granting permission "
            r3.append(r9)     // Catch:{ all -> 0x074a }
            r3.append(r12)     // Catch:{ all -> 0x074a }
            java.lang.String r9 = " from package "
            r3.append(r9)     // Catch:{ all -> 0x074a }
            r9 = r37
            java.lang.String r10 = r9.packageName     // Catch:{ all -> 0x0737 }
            r3.append(r10)     // Catch:{ all -> 0x0737 }
            java.lang.String r10 = " (protectionLevel="
            r3.append(r10)     // Catch:{ all -> 0x0737 }
            int r10 = r5.getProtectionLevel()     // Catch:{ all -> 0x0737 }
            r3.append(r10)     // Catch:{ all -> 0x0737 }
            java.lang.String r10 = " flags=0x"
            r3.append(r10)     // Catch:{ all -> 0x0737 }
            android.content.pm.ApplicationInfo r10 = r9.applicationInfo     // Catch:{ all -> 0x0737 }
            int r10 = r10.flags     // Catch:{ all -> 0x0737 }
            java.lang.String r10 = java.lang.Integer.toHexString(r10)     // Catch:{ all -> 0x0737 }
            r3.append(r10)     // Catch:{ all -> 0x0737 }
            java.lang.String r10 = ")"
            r3.append(r10)     // Catch:{ all -> 0x0737 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0737 }
            android.util.Slog.i(r1, r3)     // Catch:{ all -> 0x0737 }
            r10 = r39
            r16 = r4
            r4 = r22
            goto L_0x07ec
        L_0x0737:
            r0 = move-exception
            r14 = r36
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x074a:
            r0 = move-exception
            r9 = r37
            r14 = r36
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x075f:
            r9 = r37
            r7 = 0
            r5.isAppOp()     // Catch:{ all -> 0x0769 }
            r10 = r39
            goto L_0x07ea
        L_0x0769:
            r0 = move-exception
            r14 = r36
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x077e:
            r0 = move-exception
            r9 = r37
            r14 = r36
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x0795:
            r0 = move-exception
            r9 = r8
            r32 = r14
            r8 = r15
            r14 = r36
        L_0x079c:
            r11 = r38
            r10 = r39
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            goto L_0x08f0
        L_0x07ac:
            r0 = move-exception
            r22 = r4
            r9 = r8
            r32 = r14
            r8 = r15
            r14 = r36
            r11 = r38
            r10 = r39
            goto L_0x080e
        L_0x07bb:
            r21 = r0
            r23 = r1
            r25 = r3
            r22 = r4
            r9 = r8
            r20 = r11
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
        L_0x07cc:
            r10 = r39
            if (r10 == 0) goto L_0x07ea
            java.lang.String r0 = r9.packageName     // Catch:{ all -> 0x07d7 }
            boolean r0 = r10.equals(r0)     // Catch:{ all -> 0x07d7 }
            goto L_0x07ea
        L_0x07d7:
            r0 = move-exception
            r14 = r36
            r11 = r38
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x07ea:
            r4 = r22
        L_0x07ec:
            int r3 = r25 + 1
            r11 = r40
            r0 = r7
            r15 = r8
            r8 = r9
            r5 = r18
            r6 = r19
            r1 = r23
            r12 = r29
            r14 = r32
            r7 = r36
            r9 = r38
            goto L_0x007c
        L_0x0803:
            r0 = move-exception
            r22 = r4
            r9 = r8
            r32 = r14
            r8 = r15
            r14 = r36
            r11 = r38
        L_0x080e:
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            goto L_0x08f0
        L_0x081a:
            r0 = move-exception
            r22 = r4
            r9 = r8
            r32 = r14
            r8 = r15
            r14 = r36
            r11 = r38
            r1 = r40
            r17 = r2
            r7 = r5
            r4 = r16
            r3 = r22
            goto L_0x08f0
        L_0x0830:
            r23 = r1
            r25 = r3
            r22 = r4
            r18 = r5
            r19 = r6
            r9 = r8
            r29 = r12
            r32 = r14
            r8 = r15
            r7 = 0
            if (r16 != 0) goto L_0x0848
            r11 = r38
            if (r11 == 0) goto L_0x0868
            goto L_0x084a
        L_0x0848:
            r11 = r38
        L_0x084a:
            boolean r0 = r29.areInstallPermissionsFixed()     // Catch:{ all -> 0x08d5 }
            if (r0 != 0) goto L_0x0868
            boolean r0 = r29.isSystem()     // Catch:{ all -> 0x0857 }
            if (r0 == 0) goto L_0x086e
            goto L_0x0868
        L_0x0857:
            r0 = move-exception
            r14 = r36
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            r12 = r29
            goto L_0x08f0
        L_0x0868:
            boolean r0 = r29.isUpdatedSystem()     // Catch:{ all -> 0x08d5 }
            if (r0 == 0) goto L_0x0884
        L_0x086e:
            r12 = r29
            r0 = 1
            r12.setInstallPermissionsFixed(r0)     // Catch:{ all -> 0x0875 }
            goto L_0x0886
        L_0x0875:
            r0 = move-exception
            r14 = r36
            r1 = r40
            r17 = r2
            r4 = r16
            r7 = r18
            r3 = r22
            goto L_0x08f0
        L_0x0884:
            r12 = r29
        L_0x0886:
            r14 = r36
            r3 = r22
            int[] r6 = r14.revokePermissionsNoLongerImplicitLocked(r13, r9, r3)     // Catch:{ all -> 0x08cb }
            r0 = r19
            r15 = r23
            r1 = r36
            r17 = r2
            r2 = r8
            r3 = r13
            r4 = r37
            r7 = r18
            r5 = r15
            int[] r1 = r1.setInitialGrantForNewImplicitPermissionsLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x08c4 }
            r3 = r1
            int[] r1 = r14.checkIfLegacyStorageOpsNeedToBeUpdated(r9, r11, r3)     // Catch:{ all -> 0x08be }
            r3 = r1
            monitor-exit(r17)     // Catch:{ all -> 0x08be }
            r1 = r40
            if (r1 == 0) goto L_0x08af
            r1.onPermissionUpdated(r3, r7)
        L_0x08af:
            int r0 = r3.length
            r2 = 0
        L_0x08b1:
            if (r2 >= r0) goto L_0x08bd
            r4 = r3[r2]
            java.lang.String r5 = r9.packageName
            r14.notifyRuntimePermissionStateChanged(r5, r4)
            int r2 = r2 + 1
            goto L_0x08b1
        L_0x08bd:
            return
        L_0x08be:
            r0 = move-exception
            r1 = r40
            r4 = r16
            goto L_0x08f0
        L_0x08c4:
            r0 = move-exception
            r1 = r40
            r3 = r6
            r4 = r16
            goto L_0x08f0
        L_0x08cb:
            r0 = move-exception
            r1 = r40
            r17 = r2
            r7 = r18
            r4 = r16
            goto L_0x08f0
        L_0x08d5:
            r0 = move-exception
            r14 = r36
            r1 = r40
            r17 = r2
            r7 = r18
            r3 = r22
            r12 = r29
            r4 = r16
            goto L_0x08f0
        L_0x08e5:
            r0 = move-exception
            r17 = r2
            r1 = r11
            r32 = r14
            r14 = r7
            r11 = r9
            r7 = r5
            r9 = r8
            r8 = r15
        L_0x08f0:
            monitor-exit(r17)     // Catch:{ all -> 0x08f2 }
            throw r0
        L_0x08f2:
            r0 = move-exception
            goto L_0x08f0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.restorePermissionState(android.content.pm.PackageParser$Package, boolean, java.lang.String, com.android.server.pm.permission.PermissionManagerServiceInternal$PermissionCallback):void");
    }

    private int[] revokePermissionsNoLongerImplicitLocked(PermissionsState ps, PackageParser.Package pkg, int[] updatedUserIds) {
        PermissionsState permissionsState = ps;
        PackageParser.Package packageR = pkg;
        String str = packageR.packageName;
        boolean supportsRuntimePermissions = packageR.applicationInfo.targetSdkVersion >= 23;
        int[] updatedUserIds2 = updatedUserIds;
        for (int userId : UserManagerService.getInstance().getUserIds()) {
            for (String permission : permissionsState.getPermissions(userId)) {
                if (!packageR.implicitPermissions.contains(permission)) {
                    if (!permissionsState.hasInstallPermission(permission)) {
                        int flags = permissionsState.getRuntimePermissionState(permission, userId).getFlags();
                        if ((flags & 128) != 0) {
                            BasePermission bp = this.mSettings.getPermissionLocked(permission);
                            int flagsToRemove = 128;
                            if ((flags & 52) == 0 && supportsRuntimePermissions) {
                                int revokeRuntimePermission = permissionsState.revokeRuntimePermission(bp, userId);
                                flagsToRemove = 128 | 3;
                            }
                            permissionsState.updatePermissionFlags(bp, userId, flagsToRemove, 0);
                            updatedUserIds2 = ArrayUtils.appendInt(updatedUserIds2, userId);
                        }
                    }
                }
            }
        }
        return updatedUserIds2;
    }

    private void inheritPermissionStateToNewImplicitPermissionLocked(ArraySet<String> sourcePerms, String newPerm, PermissionsState ps, PackageParser.Package pkg, int userId) {
        String str = pkg.packageName;
        boolean isGranted = false;
        int flags = 0;
        int numSourcePerm = sourcePerms.size();
        for (int i = 0; i < numSourcePerm; i++) {
            String sourcePerm = sourcePerms.valueAt(i);
            if (ps.hasRuntimePermission(sourcePerm, userId) || ps.hasInstallPermission(sourcePerm)) {
                if (!isGranted) {
                    flags = 0;
                }
                isGranted = true;
                flags |= ps.getPermissionFlags(sourcePerm, userId);
            } else if (!isGranted) {
                flags |= ps.getPermissionFlags(sourcePerm, userId);
            }
        }
        if (isGranted) {
            ps.grantRuntimePermission(this.mSettings.getPermissionLocked(newPerm), userId);
        }
        ps.updatePermissionFlags(this.mSettings.getPermission(newPerm), userId, flags, flags);
    }

    private int[] checkIfLegacyStorageOpsNeedToBeUpdated(PackageParser.Package pkg, boolean replace, int[] updatedUserIds) {
        if (!replace || !pkg.applicationInfo.hasRequestedLegacyExternalStorage() || (!pkg.requestedPermissions.contains("android.permission.READ_EXTERNAL_STORAGE") && !pkg.requestedPermissions.contains("android.permission.WRITE_EXTERNAL_STORAGE"))) {
            return updatedUserIds;
        }
        return UserManagerService.getInstance().getUserIds();
    }

    private int[] setInitialGrantForNewImplicitPermissionsLocked(PermissionsState origPs, PermissionsState ps, PackageParser.Package pkg, ArraySet<String> newImplicitPermissions, int[] updatedUserIds) {
        boolean inheritsFromInstallPerm;
        PermissionsState permissionsState = ps;
        String str = pkg.packageName;
        ArrayMap arrayMap = new ArrayMap();
        int numSplitPerms = PermissionManager.SPLIT_PERMISSIONS.size();
        for (int splitPermNum = 0; splitPermNum < numSplitPerms; splitPermNum++) {
            PermissionManager.SplitPermissionInfo spi = (PermissionManager.SplitPermissionInfo) PermissionManager.SPLIT_PERMISSIONS.get(splitPermNum);
            List<String> newPerms = spi.getNewPermissions();
            int numNewPerms = newPerms.size();
            for (int newPermNum = 0; newPermNum < numNewPerms; newPermNum++) {
                String newPerm = newPerms.get(newPermNum);
                ArraySet<String> splitPerms = (ArraySet) arrayMap.get(newPerm);
                if (splitPerms == null) {
                    splitPerms = new ArraySet<>();
                    arrayMap.put(newPerm, splitPerms);
                }
                splitPerms.add(spi.getSplitPermission());
            }
        }
        int numNewImplicitPerms = newImplicitPermissions.size();
        int newImplicitPermNum = 0;
        int[] updatedUserIds2 = updatedUserIds;
        while (newImplicitPermNum < numNewImplicitPerms) {
            String newPerm2 = newImplicitPermissions.valueAt(newImplicitPermNum);
            ArraySet<String> sourcePerms = (ArraySet) arrayMap.get(newPerm2);
            if (sourcePerms != null && !permissionsState.hasInstallPermission(newPerm2)) {
                BasePermission bp = this.mSettings.getPermissionLocked(newPerm2);
                int[] users = UserManagerService.getInstance().getUserIds();
                int numUsers = users.length;
                int userNum = 0;
                while (true) {
                    if (userNum >= numUsers) {
                        int i = userNum;
                        int i2 = numUsers;
                        int[] iArr = users;
                        BasePermission basePermission = bp;
                        break;
                    }
                    int userId = users[userNum];
                    int userNum2 = userNum;
                    if (!newPerm2.equals("android.permission.ACTIVITY_RECOGNITION")) {
                        permissionsState.updatePermissionFlags(bp, userId, 128, 128);
                    }
                    int[] updatedUserIds3 = ArrayUtils.appendInt(updatedUserIds2, userId);
                    boolean inheritsFromInstallPerm2 = false;
                    int sourcePermNum = 0;
                    while (true) {
                        inheritsFromInstallPerm = inheritsFromInstallPerm2;
                        if (sourcePermNum >= sourcePerms.size()) {
                            break;
                        } else if (permissionsState.hasInstallPermission(sourcePerms.valueAt(sourcePermNum))) {
                            inheritsFromInstallPerm = true;
                            break;
                        } else {
                            sourcePermNum++;
                            inheritsFromInstallPerm2 = inheritsFromInstallPerm;
                        }
                    }
                    if (!origPs.hasRequestedPermission(sourcePerms) && !inheritsFromInstallPerm) {
                        updatedUserIds2 = updatedUserIds3;
                        break;
                    }
                    inheritPermissionStateToNewImplicitPermissionLocked(sourcePerms, newPerm2, ps, pkg, userId);
                    userNum = userNum2 + 1;
                    PackageParser.Package packageR = pkg;
                    updatedUserIds2 = updatedUserIds3;
                    numUsers = numUsers;
                    users = users;
                    bp = bp;
                }
            }
            newImplicitPermNum++;
            PackageParser.Package packageR2 = pkg;
        }
        ArraySet<String> arraySet = newImplicitPermissions;
        return updatedUserIds2;
    }

    private boolean isNewPlatformPermissionForPackage(String perm, PackageParser.Package pkg) {
        int NP = PackageParser.NEW_PERMISSIONS.length;
        int ip = 0;
        while (ip < NP) {
            PackageParser.NewPermissionInfo npi = PackageParser.NEW_PERMISSIONS[ip];
            if (!npi.name.equals(perm) || pkg.applicationInfo.targetSdkVersion >= npi.sdkVersion) {
                ip++;
            } else {
                Log.i(TAG, "Auto-granting " + perm + " to old pkg " + pkg.packageName);
                return true;
            }
        }
        return false;
    }

    private boolean hasPrivappWhitelistEntry(String perm, PackageParser.Package pkg) {
        ArraySet<String> wlPermissions;
        if (pkg.isVendor()) {
            wlPermissions = SystemConfig.getInstance().getVendorPrivAppPermissions(pkg.packageName);
        } else if (pkg.isProduct()) {
            wlPermissions = SystemConfig.getInstance().getProductPrivAppPermissions(pkg.packageName);
        } else if (pkg.isProductServices()) {
            wlPermissions = SystemConfig.getInstance().getProductServicesPrivAppPermissions(pkg.packageName);
        } else {
            wlPermissions = SystemConfig.getInstance().getPrivAppPermissions(pkg.packageName);
        }
        if (wlPermissions != null && wlPermissions.contains(perm)) {
            return true;
        }
        if (pkg.parentPackage == null || !hasPrivappWhitelistEntry(perm, pkg.parentPackage)) {
            return false;
        }
        return true;
    }

    private boolean grantSignaturePermission(String perm, PackageParser.Package pkg, BasePermission bp, PermissionsState origPermissions) {
        boolean allowed;
        boolean allowed2;
        Iterator it;
        PackageSetting disabledChildPs;
        ArraySet<String> deniedPermissions;
        String str = perm;
        PackageParser.Package packageR = pkg;
        boolean oemPermission = bp.isOEM();
        boolean vendorPrivilegedPermission = bp.isVendorPrivileged();
        boolean privilegedPermission = bp.isPrivileged() || bp.isVendorPrivileged();
        boolean privappPermissionsDisable = RoSystemProperties.CONTROL_PRIVAPP_PERMISSIONS_DISABLE;
        boolean platformPermission = PackageManagerService.PLATFORM_PACKAGE_NAME.equals(bp.getSourcePackageName());
        boolean platformPackage = PackageManagerService.PLATFORM_PACKAGE_NAME.equals(packageR.packageName);
        if (!privappPermissionsDisable && privilegedPermission && pkg.isPrivileged() && !platformPackage && platformPermission && !hasPrivappWhitelistEntry(perm, pkg)) {
            if (pkg.isVendor()) {
                deniedPermissions = SystemConfig.getInstance().getVendorPrivAppDenyPermissions(packageR.packageName);
            } else if (pkg.isProduct()) {
                deniedPermissions = SystemConfig.getInstance().getProductPrivAppDenyPermissions(packageR.packageName);
            } else {
                deniedPermissions = SystemConfig.getInstance().getPrivAppDenyPermissions(packageR.packageName);
            }
            if (!(deniedPermissions == null || !deniedPermissions.contains(str))) {
                return false;
            }
            if (!this.mSystemReady && !pkg.isUpdatedSystemApp()) {
                Slog.w(TAG, "Privileged permission " + str + " for package " + packageR.packageName + " - not in privapp-permissions whitelist");
                if (RoSystemProperties.CONTROL_PRIVAPP_PERMISSIONS_ENFORCE) {
                    if (this.mPrivappPermissionsViolations == null) {
                        this.mPrivappPermissionsViolations = new ArraySet<>();
                    }
                    this.mPrivappPermissionsViolations.add(packageR.packageName + ": " + str);
                }
            }
            if (RoSystemProperties.CONTROL_PRIVAPP_PERMISSIONS_ENFORCE) {
                return false;
            }
        }
        String systemPackageName = this.mPackageManagerInt.getKnownPackageName(0, 0);
        PackageParser.Package systemPackage = this.mPackageManagerInt.getPackage(systemPackageName);
        boolean allowed3 = ExtraPackageManager.isTrustedSystemSignature(packageR.mSigningDetails.signatures, bp.name) | (packageR.mSigningDetails.hasAncestorOrSelf(bp.getSourcePackageSetting().getSigningDetails()) || bp.getSourcePackageSetting().getSigningDetails().checkCapability(packageR.mSigningDetails, 4) || packageR.mSigningDetails.hasAncestorOrSelf(systemPackage.mSigningDetails) || systemPackage.mSigningDetails.checkCapability(packageR.mSigningDetails, 4));
        if (allowed3) {
        } else if (!privilegedPermission && !oemPermission) {
            String str2 = systemPackageName;
        } else if (pkg.isSystem()) {
            if (pkg.isUpdatedSystemApp()) {
                PackageParser.Package disabledPkg = this.mPackageManagerInt.getDisabledSystemPackage(packageR.packageName);
                PackageSetting disabledPs = disabledPkg != null ? (PackageSetting) disabledPkg.mExtras : null;
                if (disabledPs != null) {
                    String str3 = systemPackageName;
                    if (disabledPs.getPermissionsState().hasInstallPermission(str)) {
                        allowed = ((!privilegedPermission || !disabledPs.isPrivileged()) && (!oemPermission || !disabledPs.isOem() || !canGrantOemPermission(disabledPs, str))) ? allowed3 : true;
                        allowed3 = allowed;
                    }
                }
                if (disabledPs != null && disabledPkg != null && isPackageRequestingPermission(disabledPkg, str) && ((privilegedPermission && disabledPs.isPrivileged()) || (oemPermission && disabledPs.isOem() && canGrantOemPermission(disabledPs, str)))) {
                    allowed3 = true;
                }
                if (packageR.parentPackage != null) {
                    allowed2 = allowed3;
                    PackageParser.Package disabledParentPkg = this.mPackageManagerInt.getDisabledSystemPackage(packageR.parentPackage.packageName);
                    PackageSetting disabledParentPs = disabledParentPkg != null ? (PackageSetting) disabledParentPkg.mExtras : null;
                    if (disabledParentPkg != null) {
                        if (!privilegedPermission || !disabledParentPs.isPrivileged()) {
                            if (!oemPermission) {
                                PackageSetting packageSetting = disabledParentPs;
                            } else if (!disabledParentPs.isOem()) {
                                PackageSetting packageSetting2 = disabledParentPs;
                            }
                        }
                        if (!isPackageRequestingPermission(disabledParentPkg, str) || !canGrantOemPermission(disabledParentPs, str)) {
                            PackageSetting packageSetting3 = disabledParentPs;
                            if (disabledParentPkg.childPackages != null) {
                                Iterator it2 = disabledParentPkg.childPackages.iterator();
                                while (it2.hasNext()) {
                                    PackageParser.Package disabledParentPkg2 = disabledParentPkg;
                                    PackageParser.Package disabledChildPkg = (PackageParser.Package) it2.next();
                                    if (disabledChildPkg != null) {
                                        it = it2;
                                        disabledChildPs = (PackageSetting) disabledChildPkg.mExtras;
                                    } else {
                                        it = it2;
                                        disabledChildPs = null;
                                    }
                                    if (isPackageRequestingPermission(disabledChildPkg, str) && canGrantOemPermission(disabledChildPs, str)) {
                                        allowed = true;
                                        break;
                                    }
                                    it2 = it;
                                    disabledParentPkg = disabledParentPkg2;
                                }
                            }
                        } else {
                            allowed = true;
                            allowed3 = allowed;
                        }
                    } else {
                        PackageSetting packageSetting4 = disabledParentPs;
                    }
                } else {
                    allowed2 = allowed3;
                }
                allowed = allowed2;
                allowed3 = allowed;
            } else {
                allowed3 = (privilegedPermission && pkg.isPrivileged()) || (oemPermission && pkg.isOem() && canGrantOemPermission((PackageSetting) packageR.mExtras, str));
            }
            if (allowed3 && privilegedPermission && !vendorPrivilegedPermission && pkg.isVendor()) {
                Slog.w(TAG, "Permission " + str + " cannot be granted to privileged vendor apk " + packageR.packageName + " because it isn't a 'vendorPrivileged' permission.");
                allowed3 = false;
            }
        }
        if (!allowed3) {
            if (!allowed3 && bp.isPre23() && packageR.applicationInfo.targetSdkVersion < 23) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isInstaller() && (packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(2, 0)) || packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(6, 0)))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isVerifier() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(3, 0))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isPreInstalled() && pkg.isSystem()) {
                allowed3 = true;
            }
            if (allowed3 || !bp.isDevelopment()) {
                PermissionsState permissionsState = origPermissions;
            } else {
                allowed3 = origPermissions.hasInstallPermission(str);
            }
            if (!allowed3 && bp.isSetup() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(1, 0))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isSystemTextClassifier() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(5, 0))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isConfigurator() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(9, 0))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isWellbeing() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(7, 0))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isDocumenter() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(8, 0))) {
                allowed3 = true;
            }
            if (!allowed3 && bp.isIncidentReportApprover() && packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(10, 0))) {
                allowed3 = true;
            }
            if (allowed3 || !bp.isAppPredictor() || !packageR.packageName.equals(this.mPackageManagerInt.getKnownPackageName(11, 0))) {
                return allowed3;
            }
            return true;
        }
        PermissionsState permissionsState2 = origPermissions;
        return allowed3;
    }

    private static boolean canGrantOemPermission(PackageSetting ps, String permission) {
        if (!ps.isOem()) {
            return false;
        }
        Boolean granted = (Boolean) SystemConfig.getInstance().getOemPermissions(ps.name).get(permission);
        if (granted == null) {
            throw new IllegalStateException("OEM permission" + permission + " requested by package " + ps.name + " must be explicitly declared granted or not");
        } else if (Boolean.TRUE == granted) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean isPermissionsReviewRequired(PackageParser.Package pkg, int userId) {
        if (!PermissionManagerServiceInjector.isPermissionReviewDisabled() && pkg.applicationInfo.targetSdkVersion < 23 && pkg.mExtras != null) {
            return ((PackageSetting) pkg.mExtras).getPermissionsState().isPermissionReviewRequired(userId);
        }
        return false;
    }

    private boolean isPackageRequestingPermission(PackageParser.Package pkg, String permission) {
        int permCount = pkg.requestedPermissions.size();
        for (int j = 0; j < permCount; j++) {
            if (permission.equals((String) pkg.requestedPermissions.get(j))) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void grantRuntimePermissionsGrantedToDisabledPackageLocked(PackageParser.Package pkg, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
        PackageParser.Package disabledPkg;
        int i;
        int i2;
        PackageParser.Package packageR = pkg;
        if (packageR.parentPackage != null && packageR.requestedPermissions != null && (disabledPkg = this.mPackageManagerInt.getDisabledSystemPackage(packageR.parentPackage.packageName)) != null && disabledPkg.mExtras != null) {
            PackageSetting disabledPs = (PackageSetting) disabledPkg.mExtras;
            if (disabledPs.isPrivileged() && !disabledPs.hasChildPackages()) {
                int permCount = packageR.requestedPermissions.size();
                for (int i3 = 0; i3 < permCount; i3++) {
                    String permission = (String) packageR.requestedPermissions.get(i3);
                    BasePermission bp = this.mSettings.getPermissionLocked(permission);
                    if (bp != null && (bp.isRuntime() || bp.isDevelopment())) {
                        int[] userIds = this.mUserManagerInt.getUserIds();
                        int length = userIds.length;
                        int i4 = 0;
                        while (i4 < length) {
                            int userId = userIds[i4];
                            if (disabledPs.getPermissionsState().hasRuntimePermission(permission, userId)) {
                                i2 = i4;
                                i = length;
                                grantRuntimePermission(permission, packageR.packageName, false, callingUid, userId, callback);
                            } else {
                                i2 = i4;
                                i = length;
                            }
                            i4 = i2 + 1;
                            length = i;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void grantRequestedRuntimePermissions(PackageParser.Package pkg, int[] userIds, String[] grantedPermissions, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
        for (int userId : userIds) {
            grantRequestedRuntimePermissionsForUser(pkg, userId, grantedPermissions, callingUid, callback);
        }
    }

    /* access modifiers changed from: private */
    public List<String> getWhitelistedRestrictedPermissions(PackageParser.Package pkg, int whitelistFlags, int userId) {
        PackageSetting packageSetting = (PackageSetting) pkg.mExtras;
        if (packageSetting == null) {
            return null;
        }
        PermissionsState permissionsState = packageSetting.getPermissionsState();
        int queryFlags = 0;
        if ((whitelistFlags & 1) != 0) {
            queryFlags = 0 | 4096;
        }
        if ((whitelistFlags & 4) != 0) {
            queryFlags |= 8192;
        }
        if ((whitelistFlags & 2) != 0) {
            queryFlags |= 2048;
        }
        ArrayList<String> whitelistedPermissions = null;
        int permissionCount = pkg.requestedPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            String permissionName = (String) pkg.requestedPermissions.get(i);
            if ((permissionsState.getPermissionFlags(permissionName, userId) & queryFlags) != 0) {
                if (whitelistedPermissions == null) {
                    whitelistedPermissions = new ArrayList<>();
                }
                whitelistedPermissions.add(permissionName);
            }
        }
        return whitelistedPermissions;
    }

    /* access modifiers changed from: private */
    public void setWhitelistedRestrictedPermissions(PackageParser.Package pkg, int[] userIds, List<String> permissions, int callingUid, int whitelistFlags, PermissionManagerServiceInternal.PermissionCallback callback) {
        for (int userId : userIds) {
            setWhitelistedRestrictedPermissionsForUser(pkg, userId, permissions, callingUid, whitelistFlags, callback);
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00ae, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void grantRequestedRuntimePermissionsForUser(android.content.pm.PackageParser.Package r23, int r24, java.lang.String[] r25, int r26, com.android.server.pm.permission.PermissionManagerServiceInternal.PermissionCallback r27) {
        /*
            r22 = this;
            r10 = r22
            r11 = r23
            r12 = r24
            r13 = r25
            java.lang.Object r0 = r11.mExtras
            r14 = r0
            com.android.server.pm.PackageSetting r14 = (com.android.server.pm.PackageSetting) r14
            if (r14 != 0) goto L_0x0010
            return
        L_0x0010:
            com.android.server.pm.permission.PermissionsState r15 = r14.getPermissionsState()
            r16 = 20
            android.content.pm.ApplicationInfo r0 = r11.applicationInfo
            int r0 = r0.targetSdkVersion
            r1 = 23
            if (r0 < r1) goto L_0x0020
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            r17 = r0
            android.content.pm.PackageManagerInternal r0 = r10.mPackageManagerInt
            java.lang.String r1 = r11.packageName
            boolean r18 = r0.isInstantApp(r1, r12)
            java.util.ArrayList r0 = r11.requestedPermissions
            java.util.Iterator r0 = r0.iterator()
        L_0x0031:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x00b0
            java.lang.Object r1 = r0.next()
            r9 = r1
            java.lang.String r9 = (java.lang.String) r9
            java.lang.Object r1 = r10.mLock
            monitor-enter(r1)
            com.android.server.pm.permission.PermissionSettings r2 = r10.mSettings     // Catch:{ all -> 0x00a9 }
            com.android.server.pm.permission.BasePermission r2 = r2.getPermissionLocked(r9)     // Catch:{ all -> 0x00a9 }
            r19 = r2
            monitor-exit(r1)     // Catch:{ all -> 0x00a9 }
            if (r19 == 0) goto L_0x00a6
            boolean r1 = r19.isRuntime()
            if (r1 != 0) goto L_0x0058
            boolean r1 = r19.isDevelopment()
            if (r1 == 0) goto L_0x00a8
        L_0x0058:
            if (r18 == 0) goto L_0x0060
            boolean r1 = r19.isInstant()
            if (r1 == 0) goto L_0x00a8
        L_0x0060:
            if (r17 != 0) goto L_0x0068
            boolean r1 = r19.isRuntimeOnly()
            if (r1 != 0) goto L_0x00a8
        L_0x0068:
            if (r13 == 0) goto L_0x0070
            boolean r1 = com.android.internal.util.ArrayUtils.contains(r13, r9)
            if (r1 == 0) goto L_0x00a8
        L_0x0070:
            int r20 = r15.getPermissionFlags(r9, r12)
            if (r17 == 0) goto L_0x008a
            r1 = r20 & 20
            if (r1 != 0) goto L_0x00a8
            java.lang.String r3 = r11.packageName
            r4 = 0
            r1 = r22
            r2 = r9
            r5 = r26
            r6 = r24
            r7 = r27
            r1.grantRuntimePermission(r2, r3, r4, r5, r6, r7)
            goto L_0x00a8
        L_0x008a:
            r1 = r20 & 64
            if (r1 == 0) goto L_0x00a3
            java.lang.String r3 = r11.packageName
            r4 = 64
            r5 = 0
            r8 = 0
            r1 = r22
            r2 = r9
            r6 = r26
            r7 = r24
            r21 = r9
            r9 = r27
            r1.updatePermissionFlags(r2, r3, r4, r5, r6, r7, r8, r9)
            goto L_0x00a8
        L_0x00a3:
            r21 = r9
            goto L_0x00a8
        L_0x00a6:
            r21 = r9
        L_0x00a8:
            goto L_0x0031
        L_0x00a9:
            r0 = move-exception
            r21 = r9
        L_0x00ac:
            monitor-exit(r1)     // Catch:{ all -> 0x00ae }
            throw r0
        L_0x00ae:
            r0 = move-exception
            goto L_0x00ac
        L_0x00b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.grantRequestedRuntimePermissionsForUser(android.content.pm.PackageParser$Package, int, java.lang.String[], int, com.android.server.pm.permission.PermissionManagerServiceInternal$PermissionCallback):void");
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0216, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void grantRuntimePermission(java.lang.String r17, java.lang.String r18, boolean r19, int r20, int r21, com.android.server.pm.permission.PermissionManagerServiceInternal.PermissionCallback r22) {
        /*
            r16 = this;
            r8 = r16
            r9 = r17
            r10 = r18
            r11 = r21
            r12 = r22
            android.os.UserManagerInternal r0 = r8.mUserManagerInt
            boolean r0 = r0.exists(r11)
            if (r0 != 0) goto L_0x0029
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "No such user:"
            r0.append(r1)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "PackageManager"
            android.util.Log.e(r1, r0)
            return
        L_0x0029:
            android.content.Context r0 = r8.mContext
            java.lang.String r1 = "android.permission.GRANT_RUNTIME_PERMISSIONS"
            java.lang.String r2 = "grantRuntimePermission"
            r0.enforceCallingOrSelfPermission(r1, r2)
            r4 = 1
            r5 = 1
            r6 = 0
            java.lang.String r7 = "grantRuntimePermission"
            r1 = r16
            r2 = r20
            r3 = r21
            r1.enforceCrossUserPermission(r2, r3, r4, r5, r6, r7)
            android.content.pm.PackageManagerInternal r0 = r8.mPackageManagerInt
            android.content.pm.PackageParser$Package r1 = r0.getPackage(r10)
            if (r1 == 0) goto L_0x0218
            java.lang.Object r0 = r1.mExtras
            if (r0 == 0) goto L_0x0218
            java.lang.Object r2 = r8.mLock
            monitor-enter(r2)
            com.android.server.pm.permission.PermissionSettings r0 = r8.mSettings     // Catch:{ all -> 0x0211 }
            com.android.server.pm.permission.BasePermission r0 = r0.getPermissionLocked(r9)     // Catch:{ all -> 0x0211 }
            r3 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x0211 }
            if (r3 == 0) goto L_0x01f8
            android.content.pm.PackageManagerInternal r0 = r8.mPackageManagerInt
            r4 = r20
            boolean r0 = r0.filterAppAccess(r1, r4, r11)
            if (r0 != 0) goto L_0x01e1
            r3.enforceDeclaredUsedAndRuntimeOrDevelopment(r1)
            android.content.pm.ApplicationInfo r0 = r1.applicationInfo
            int r0 = r0.targetSdkVersion
            r2 = 23
            if (r0 >= r2) goto L_0x0075
            boolean r0 = r3.isRuntime()
            if (r0 == 0) goto L_0x0075
            return
        L_0x0075:
            android.content.pm.ApplicationInfo r0 = r1.applicationInfo
            int r0 = r0.uid
            int r5 = android.os.UserHandle.getUid(r11, r0)
            java.lang.Object r0 = r1.mExtras
            r6 = r0
            com.android.server.pm.PackageSetting r6 = (com.android.server.pm.PackageSetting) r6
            com.android.server.pm.permission.PermissionsState r7 = r6.getPermissionsState()
            int r13 = r7.getPermissionFlags(r9, r11)
            r0 = r13 & 16
            if (r0 == 0) goto L_0x00ad
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Cannot grant system fixed permission "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = " for package "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "PackageManager"
            android.util.Log.e(r2, r0)
            return
        L_0x00ad:
            if (r19 != 0) goto L_0x00d2
            r0 = r13 & 4
            if (r0 == 0) goto L_0x00d2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Cannot grant policy fixed permission "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = " for package "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "PackageManager"
            android.util.Log.e(r2, r0)
            return
        L_0x00d2:
            boolean r0 = r3.isHardRestricted()
            if (r0 == 0) goto L_0x00fb
            r0 = r13 & 14336(0x3800, float:2.0089E-41)
            if (r0 != 0) goto L_0x00fb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Cannot grant hard restricted non-exempt permission "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = " for package "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "PackageManager"
            android.util.Log.e(r2, r0)
            return
        L_0x00fb:
            boolean r0 = r3.isSoftRestricted()
            if (r0 == 0) goto L_0x0132
            android.content.Context r0 = r8.mContext
            android.content.pm.ApplicationInfo r14 = r1.applicationInfo
            android.os.UserHandle r15 = android.os.UserHandle.of(r21)
            com.android.server.policy.SoftRestrictedPermissionPolicy r0 = com.android.server.policy.SoftRestrictedPermissionPolicy.forPermission(r0, r14, r15, r9)
            boolean r0 = r0.canBeGranted()
            if (r0 != 0) goto L_0x0132
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Cannot grant soft restricted permission "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = " for package "
            r0.append(r2)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "PackageManager"
            android.util.Log.e(r2, r0)
            return
        L_0x0132:
            boolean r0 = r3.isDevelopment()
            r14 = -1
            if (r0 == 0) goto L_0x0145
            int r0 = r7.grantInstallPermission(r3)
            if (r0 == r14) goto L_0x0144
            if (r12 == 0) goto L_0x0144
            r22.onInstallPermissionGranted()
        L_0x0144:
            return
        L_0x0145:
            boolean r0 = r6.getInstantApp(r11)
            if (r0 == 0) goto L_0x0171
            boolean r0 = r3.isInstant()
            if (r0 == 0) goto L_0x0152
            goto L_0x0171
        L_0x0152:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "Cannot grant non-ephemeral permission"
            r2.append(r14)
            r2.append(r9)
            java.lang.String r14 = " for package "
            r2.append(r14)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x0171:
            android.content.pm.ApplicationInfo r0 = r1.applicationInfo
            int r0 = r0.targetSdkVersion
            if (r0 >= r2) goto L_0x017f
            java.lang.String r0 = "PackageManager"
            java.lang.String r2 = "Cannot grant runtime permission to a legacy app"
            android.util.Slog.w(r0, r2)
            return
        L_0x017f:
            int r2 = r7.grantRuntimePermission(r3, r11)
            if (r2 == r14) goto L_0x01e0
            r0 = 1
            if (r2 == r0) goto L_0x0189
            goto L_0x0196
        L_0x0189:
            if (r12 == 0) goto L_0x0196
            android.content.pm.ApplicationInfo r0 = r1.applicationInfo
            int r0 = r0.uid
            int r0 = android.os.UserHandle.getAppId(r0)
            r12.onGidsChanged(r0, r11)
        L_0x0196:
            boolean r0 = r3.isRuntime()
            if (r0 == 0) goto L_0x01a1
            r0 = 1243(0x4db, float:1.742E-42)
            r8.logPermission(r0, r9, r10)
        L_0x01a1:
            if (r12 == 0) goto L_0x01a6
            r12.onPermissionGranted(r5, r11)
        L_0x01a6:
            boolean r0 = r3.isRuntime()
            if (r0 == 0) goto L_0x01af
            r8.notifyRuntimePermissionStateChanged(r10, r11)
        L_0x01af:
            java.lang.String r0 = "android.permission.READ_EXTERNAL_STORAGE"
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x01bf
            java.lang.String r0 = "android.permission.WRITE_EXTERNAL_STORAGE"
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x01da
        L_0x01bf:
            long r14 = android.os.Binder.clearCallingIdentity()
            android.os.UserManagerInternal r0 = r8.mUserManagerInt     // Catch:{ all -> 0x01db }
            boolean r0 = r0.isUserInitialized(r11)     // Catch:{ all -> 0x01db }
            if (r0 == 0) goto L_0x01d6
            java.lang.Class<android.os.storage.StorageManagerInternal> r0 = android.os.storage.StorageManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)     // Catch:{ all -> 0x01db }
            android.os.storage.StorageManagerInternal r0 = (android.os.storage.StorageManagerInternal) r0     // Catch:{ all -> 0x01db }
            r0.onExternalStoragePolicyChanged(r5, r10)     // Catch:{ all -> 0x01db }
        L_0x01d6:
            android.os.Binder.restoreCallingIdentity(r14)
        L_0x01da:
            return
        L_0x01db:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r14)
            throw r0
        L_0x01e0:
            return
        L_0x01e1:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Unknown package: "
            r2.append(r5)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x01f8:
            r4 = r20
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Unknown permission: "
            r2.append(r5)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x0211:
            r0 = move-exception
            r4 = r20
        L_0x0214:
            monitor-exit(r2)     // Catch:{ all -> 0x0216 }
            throw r0
        L_0x0216:
            r0 = move-exception
            goto L_0x0214
        L_0x0218:
            r4 = r20
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unknown package: "
            r2.append(r3)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.grantRuntimePermission(java.lang.String, java.lang.String, boolean, int, int, com.android.server.pm.permission.PermissionManagerServiceInternal$PermissionCallback):void");
    }

    /* access modifiers changed from: private */
    public void revokeRuntimePermission(String permName, String packageName, boolean overridePolicy, int userId, PermissionManagerServiceInternal.PermissionCallback callback) {
        revokeRuntimePermission(permName, packageName, overridePolicy, userId, callback, true);
    }

    /* access modifiers changed from: private */
    public void revokeRuntimePermission(String permName, String packageName, boolean overridePolicy, int userId, PermissionManagerServiceInternal.PermissionCallback callback, boolean kill) {
        String str = permName;
        String str2 = packageName;
        int i = userId;
        PermissionManagerServiceInternal.PermissionCallback permissionCallback = callback;
        if (!this.mUserManagerInt.exists(i)) {
            Log.e(TAG, "No such user:" + i);
            return;
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.REVOKE_RUNTIME_PERMISSIONS", "revokeRuntimePermission");
        enforceCrossUserPermission(Binder.getCallingUid(), userId, true, true, false, "revokeRuntimePermission");
        PackageParser.Package pkg = this.mPackageManagerInt.getPackage(str2);
        if (pkg == null || pkg.mExtras == null) {
            boolean z = kill;
            throw new IllegalArgumentException("Unknown package: " + str2);
        } else if (!this.mPackageManagerInt.filterAppAccess(pkg, Binder.getCallingUid(), i)) {
            BasePermission bp = this.mSettings.getPermissionLocked(permName);
            if (bp != null) {
                bp.enforceDeclaredUsedAndRuntimeOrDevelopment(pkg);
                if (pkg.applicationInfo.targetSdkVersion >= 23 || !bp.isRuntime()) {
                    PermissionsState permissionsState = ((PackageSetting) pkg.mExtras).getPermissionsState();
                    int flags = permissionsState.getPermissionFlags(permName, i);
                    if ((flags & 16) != 0 && UserHandle.getCallingAppId() != 1000) {
                        throw new SecurityException("Non-System UID cannot revoke system fixed permission " + permName + " for package " + str2);
                    } else if (!overridePolicy && (flags & 4) != 0) {
                        throw new SecurityException("Cannot revoke policy fixed permission " + permName + " for package " + str2);
                    } else if (bp.isDevelopment()) {
                        if (permissionsState.revokeInstallPermission(bp) != -1 && permissionCallback != null) {
                            callback.onInstallPermissionRevoked();
                        }
                    } else if (permissionsState.hasRuntimePermission(permName, i) && permissionsState.revokeRuntimePermission(bp, i) != -1) {
                        if (bp.isRuntime()) {
                            logPermission(1245, permName, str2);
                        }
                        if (permissionCallback != null) {
                            permissionCallback.onPermissionRevoked(pkg.applicationInfo.uid, i, kill);
                        } else {
                            boolean z2 = kill;
                        }
                        if (bp.isRuntime()) {
                            notifyRuntimePermissionStateChanged(str2, i);
                        }
                    }
                }
            } else {
                boolean z3 = kill;
                throw new IllegalArgumentException("Unknown permission: " + permName);
            }
        } else {
            boolean z4 = kill;
            throw new IllegalArgumentException("Unknown package: " + str2);
        }
    }

    private void setWhitelistedRestrictedPermissionsForUser(PackageParser.Package pkg, int userId, List<String> permissions, int callingUid, int whitelistFlags, PermissionManagerServiceInternal.PermissionCallback callback) {
        int i;
        int permissionCount;
        ArraySet<String> oldGrantedRestrictedPermissions;
        int newFlags;
        int mask;
        PackageParser.Package packageR = pkg;
        int i2 = userId;
        List<String> list = permissions;
        PermissionManagerServiceInternal.PermissionCallback permissionCallback = callback;
        PackageSetting ps = (PackageSetting) packageR.mExtras;
        if (ps != null) {
            PermissionsState permissionsState = ps.getPermissionsState();
            ArraySet<String> oldGrantedRestrictedPermissions2 = null;
            boolean updatePermissions = false;
            int permissionCount2 = packageR.requestedPermissions.size();
            int i3 = 0;
            while (i3 < permissionCount2) {
                String permissionName = (String) packageR.requestedPermissions.get(i3);
                BasePermission bp = this.mSettings.getPermissionLocked(permissionName);
                if (bp == null) {
                    Slog.w(TAG, "Cannot whitelist unknown permission: " + permissionName);
                } else if (bp.isHardOrSoftRestricted()) {
                    if (permissionsState.hasPermission(permissionName, i2)) {
                        if (oldGrantedRestrictedPermissions2 == null) {
                            oldGrantedRestrictedPermissions2 = new ArraySet<>();
                        }
                        oldGrantedRestrictedPermissions2.add(permissionName);
                        oldGrantedRestrictedPermissions = oldGrantedRestrictedPermissions2;
                    } else {
                        oldGrantedRestrictedPermissions = oldGrantedRestrictedPermissions2;
                    }
                    int oldFlags = permissionsState.getPermissionFlags(permissionName, i2);
                    int newFlags2 = oldFlags;
                    int mask2 = 0;
                    int whitelistFlagsCopy = whitelistFlags;
                    while (whitelistFlagsCopy != 0) {
                        int flag = 1 << Integer.numberOfTrailingZeros(whitelistFlagsCopy);
                        whitelistFlagsCopy &= ~flag;
                        if (flag == 1) {
                            mask2 |= 4096;
                            if (list == null || !list.contains(permissionName)) {
                                newFlags2 &= -4097;
                            } else {
                                newFlags2 |= 4096;
                            }
                        } else if (flag == 2) {
                            mask2 |= 2048;
                            if (list == null || !list.contains(permissionName)) {
                                newFlags2 &= -2049;
                            } else {
                                newFlags2 |= 2048;
                            }
                        } else if (flag == 4) {
                            mask2 |= 8192;
                            if (list == null || !list.contains(permissionName)) {
                                newFlags2 &= -8193;
                            } else {
                                newFlags2 |= 8192;
                            }
                        }
                    }
                    if (oldFlags == newFlags2) {
                        i = i3;
                        permissionCount = permissionCount2;
                        oldGrantedRestrictedPermissions2 = oldGrantedRestrictedPermissions;
                    } else {
                        boolean wasWhitelisted = oldFlags & true;
                        boolean isWhitelisted = (newFlags2 & 14336) != 0;
                        if ((oldFlags & 4) != 0) {
                            boolean isGranted = permissionsState.hasPermission(permissionName, i2);
                            if (!isWhitelisted && isGranted) {
                                mask2 |= 4;
                                newFlags2 &= -5;
                            }
                        }
                        if (packageR.applicationInfo.targetSdkVersion >= 23 || wasWhitelisted || !isWhitelisted) {
                            newFlags = newFlags2;
                            mask = mask2;
                        } else {
                            newFlags = newFlags2 | 64;
                            mask = mask2 | 64;
                        }
                        int i4 = oldFlags;
                        String str = permissionName;
                        i = i3;
                        permissionCount = permissionCount2;
                        updatePermissionFlags(permissionName, packageR.packageName, mask, newFlags, callingUid, userId, false, (PermissionManagerServiceInternal.PermissionCallback) null);
                        oldGrantedRestrictedPermissions2 = oldGrantedRestrictedPermissions;
                        updatePermissions = true;
                    }
                    i3 = i + 1;
                    permissionCount2 = permissionCount;
                }
                i = i3;
                permissionCount = permissionCount2;
                i3 = i + 1;
                permissionCount2 = permissionCount;
            }
            int i5 = i3;
            int i6 = permissionCount2;
            if (updatePermissions) {
                restorePermissionState(packageR, false, packageR.packageName, permissionCallback);
                if (oldGrantedRestrictedPermissions2 != null) {
                    int oldGrantedCount = oldGrantedRestrictedPermissions2.size();
                    for (int i7 = 0; i7 < oldGrantedCount; i7++) {
                        if (!ps.getPermissionsState().hasPermission(oldGrantedRestrictedPermissions2.valueAt(i7), i2)) {
                            permissionCallback.onPermissionRevoked(packageR.applicationInfo.uid, i2);
                            return;
                        }
                    }
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    private int[] revokeUnusedSharedUserPermissionsLocked(SharedUserSetting suSetting, int[] allUserIds) {
        char c;
        boolean z;
        char c2;
        boolean z2;
        BasePermission bp;
        PermissionManagerService permissionManagerService = this;
        int[] iArr = allUserIds;
        ArraySet<String> usedPermissions = new ArraySet<>();
        List<PackageParser.Package> pkgList = suSetting.getPackages();
        if (pkgList == null || pkgList.size() == 0) {
            return EmptyArray.INT;
        }
        for (PackageParser.Package pkg : pkgList) {
            if (pkg.requestedPermissions != null) {
                int requestedPermCount = pkg.requestedPermissions.size();
                for (int j = 0; j < requestedPermCount; j++) {
                    String permission = (String) pkg.requestedPermissions.get(j);
                    if (permissionManagerService.mSettings.getPermissionLocked(permission) != null) {
                        usedPermissions.add(permission);
                    }
                }
            }
        }
        PermissionsState permissionsState = suSetting.getPermissionsState();
        List<PermissionsState.PermissionState> installPermStates = permissionsState.getInstallPermissionStates();
        int i = installPermStates.size() - 1;
        while (true) {
            c = 64511;
            z = false;
            if (i < 0) {
                break;
            }
            PermissionsState.PermissionState permissionState = installPermStates.get(i);
            if (!usedPermissions.contains(permissionState.getName()) && (bp = permissionManagerService.mSettings.getPermissionLocked(permissionState.getName())) != null) {
                permissionsState.revokeInstallPermission(bp);
                permissionsState.updatePermissionFlags(bp, -1, 64511, 0);
            }
            i--;
        }
        int[] runtimePermissionChangedUserIds = EmptyArray.INT;
        int length = iArr.length;
        int[] runtimePermissionChangedUserIds2 = runtimePermissionChangedUserIds;
        int i2 = 0;
        while (i2 < length) {
            int userId = iArr[i2];
            List<PermissionsState.PermissionState> runtimePermStates = permissionsState.getRuntimePermissionStates(userId);
            int i3 = runtimePermStates.size() - 1;
            while (i3 >= 0) {
                PermissionsState.PermissionState permissionState2 = runtimePermStates.get(i3);
                if (!usedPermissions.contains(permissionState2.getName())) {
                    BasePermission bp2 = permissionManagerService.mSettings.getPermissionLocked(permissionState2.getName());
                    if (bp2 != null) {
                        permissionsState.revokeRuntimePermission(bp2, userId);
                        z2 = false;
                        c2 = 64511;
                        permissionsState.updatePermissionFlags(bp2, userId, 64511, 0);
                        runtimePermissionChangedUserIds2 = ArrayUtils.appendInt(runtimePermissionChangedUserIds2, userId);
                    } else {
                        z2 = false;
                        c2 = 64511;
                    }
                } else {
                    z2 = z;
                    c2 = 64511;
                }
                i3--;
                c = c2;
                z = z2;
                permissionManagerService = this;
            }
            boolean z3 = z;
            char c3 = c;
            i2++;
            z = z3;
            permissionManagerService = this;
        }
        return runtimePermissionChangedUserIds2;
    }

    /* access modifiers changed from: private */
    public String[] getAppOpPermissionPackages(String permName) {
        if (this.mPackageManagerInt.getInstantAppPackageName(Binder.getCallingUid()) != null) {
            return null;
        }
        synchronized (this.mLock) {
            ArraySet<String> pkgs = this.mSettings.mAppOpPermissionPackages.get(permName);
            if (pkgs == null) {
                return null;
            }
            String[] strArr = (String[]) pkgs.toArray(new String[pkgs.size()]);
            return strArr;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003b, code lost:
        if (r9.mPackageManagerInt.filterAppAccess(r0, r12, r13) == false) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003d, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004a, code lost:
        return ((com.android.server.pm.PackageSetting) r0.mExtras).getPermissionsState().getPermissionFlags(r10, r13);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getPermissionFlags(java.lang.String r10, java.lang.String r11, int r12, int r13) {
        /*
            r9 = this;
            android.os.UserManagerInternal r0 = r9.mUserManagerInt
            boolean r0 = r0.exists(r13)
            r1 = 0
            if (r0 != 0) goto L_0x000a
            return r1
        L_0x000a:
            java.lang.String r0 = "getPermissionFlags"
            r9.enforceGrantRevokeGetRuntimePermissionPermissions(r0)
            r5 = 1
            r6 = 0
            r7 = 0
            java.lang.String r8 = "getPermissionFlags"
            r2 = r9
            r3 = r12
            r4 = r13
            r2.enforceCrossUserPermission(r3, r4, r5, r6, r7, r8)
            android.content.pm.PackageManagerInternal r0 = r9.mPackageManagerInt
            android.content.pm.PackageParser$Package r0 = r0.getPackage(r11)
            if (r0 == 0) goto L_0x004e
            java.lang.Object r2 = r0.mExtras
            if (r2 != 0) goto L_0x0027
            goto L_0x004e
        L_0x0027:
            java.lang.Object r2 = r9.mLock
            monitor-enter(r2)
            com.android.server.pm.permission.PermissionSettings r3 = r9.mSettings     // Catch:{ all -> 0x004b }
            com.android.server.pm.permission.BasePermission r3 = r3.getPermissionLocked(r10)     // Catch:{ all -> 0x004b }
            if (r3 != 0) goto L_0x0034
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            return r1
        L_0x0034:
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            android.content.pm.PackageManagerInternal r2 = r9.mPackageManagerInt
            boolean r2 = r2.filterAppAccess(r0, r12, r13)
            if (r2 == 0) goto L_0x003e
            return r1
        L_0x003e:
            java.lang.Object r1 = r0.mExtras
            com.android.server.pm.PackageSetting r1 = (com.android.server.pm.PackageSetting) r1
            com.android.server.pm.permission.PermissionsState r2 = r1.getPermissionsState()
            int r3 = r2.getPermissionFlags(r10, r13)
            return r3
        L_0x004b:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            throw r1
        L_0x004e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionManagerService.getPermissionFlags(java.lang.String, java.lang.String, int, int):int");
    }

    /* access modifiers changed from: private */
    public void updatePermissions(String packageName, PackageParser.Package pkg, boolean replaceGrant, Collection<PackageParser.Package> allPackages, PermissionManagerServiceInternal.PermissionCallback callback) {
        int i = 0;
        int i2 = pkg != null ? 1 : 0;
        if (replaceGrant) {
            i = 2;
        }
        int flags = i | i2;
        updatePermissions(packageName, pkg, getVolumeUuidForPackage(pkg), flags, allPackages, callback);
        if (pkg != null && pkg.childPackages != null) {
            Iterator it = pkg.childPackages.iterator();
            while (it.hasNext()) {
                PackageParser.Package childPkg = (PackageParser.Package) it.next();
                updatePermissions(childPkg.packageName, childPkg, getVolumeUuidForPackage(childPkg), flags, allPackages, callback);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateAllPermissions(String volumeUuid, boolean sdkUpdated, Collection<PackageParser.Package> allPackages, PermissionManagerServiceInternal.PermissionCallback callback) {
        int i;
        if (sdkUpdated) {
            i = 6;
        } else {
            i = 0;
        }
        updatePermissions((String) null, (PackageParser.Package) null, volumeUuid, i | 1, allPackages, callback);
    }

    private void updatePermissions(String changingPkgName, PackageParser.Package changingPkg, String replaceVolumeUuid, int flags, Collection<PackageParser.Package> allPackages, PermissionManagerServiceInternal.PermissionCallback callback) {
        int flags2 = updatePermissions(changingPkgName, changingPkg, updatePermissionTrees(changingPkgName, changingPkg, flags), callback);
        synchronized (this.mLock) {
            if (this.mBackgroundPermissions == null) {
                this.mBackgroundPermissions = new ArrayMap<>();
                for (BasePermission bp : this.mSettings.getAllPermissionsLocked()) {
                    if (!(bp.perm == null || bp.perm.info == null || bp.perm.info.backgroundPermission == null)) {
                        String fgPerm = bp.name;
                        String bgPerm = bp.perm.info.backgroundPermission;
                        List<String> fgPerms = this.mBackgroundPermissions.get(bgPerm);
                        if (fgPerms == null) {
                            fgPerms = new ArrayList<>();
                            this.mBackgroundPermissions.put(bgPerm, fgPerms);
                        }
                        fgPerms.add(fgPerm);
                    }
                }
            }
        }
        Trace.traceBegin(262144, "restorePermissionState");
        boolean replace = false;
        if ((flags2 & 1) != 0) {
            for (PackageParser.Package pkg : allPackages) {
                if (pkg != changingPkg) {
                    restorePermissionState(pkg, (flags2 & 4) != 0 && Objects.equals(replaceVolumeUuid, getVolumeUuidForPackage(pkg)), changingPkgName, callback);
                }
            }
        }
        if (changingPkg != null) {
            String volumeUuid = getVolumeUuidForPackage(changingPkg);
            if ((flags2 & 2) != 0 && Objects.equals(replaceVolumeUuid, volumeUuid)) {
                replace = true;
            }
            restorePermissionState(changingPkg, replace, changingPkgName, callback);
        }
        Trace.traceEnd(262144);
    }

    private int updatePermissions(String packageName, PackageParser.Package pkg, int flags, PermissionManagerServiceInternal.PermissionCallback callback) {
        Set<BasePermission> needsUpdate = null;
        synchronized (this.mLock) {
            Iterator<BasePermission> it = this.mSettings.mPermissions.values().iterator();
            while (it.hasNext()) {
                BasePermission bp = it.next();
                if (bp.isDynamic()) {
                    bp.updateDynamicPermission(this.mSettings.mPermissionTrees.values());
                }
                if (bp.getSourcePackageSetting() == null) {
                    if (needsUpdate == null) {
                        needsUpdate = new ArraySet<>(this.mSettings.mPermissions.size());
                    }
                    needsUpdate.add(bp);
                } else if (packageName != null && packageName.equals(bp.getSourcePackageName())) {
                    if (pkg == null || !hasPermission(pkg, bp.getName())) {
                        Slog.i(TAG, "Removing old permission tree: " + bp.getName() + " from package " + bp.getSourcePackageName());
                        if (bp.isRuntime()) {
                            for (int userId : this.mUserManagerInt.getUserIds()) {
                                this.mPackageManagerInt.forEachPackage(new Consumer(bp, userId, callback) {
                                    private final /* synthetic */ BasePermission f$1;
                                    private final /* synthetic */ int f$2;
                                    private final /* synthetic */ PermissionManagerServiceInternal.PermissionCallback f$3;

                                    {
                                        this.f$1 = r2;
                                        this.f$2 = r3;
                                        this.f$3 = r4;
                                    }

                                    public final void accept(Object obj) {
                                        PermissionManagerService.this.lambda$updatePermissions$1$PermissionManagerService(this.f$1, this.f$2, this.f$3, (PackageParser.Package) obj);
                                    }
                                });
                            }
                        }
                        flags |= 1;
                        it.remove();
                    }
                }
            }
        }
        if (needsUpdate != null) {
            for (BasePermission bp2 : needsUpdate) {
                PackageParser.Package sourcePkg = this.mPackageManagerInt.getPackage(bp2.getSourcePackageName());
                synchronized (this.mLock) {
                    if (sourcePkg != null) {
                        if (sourcePkg.mExtras != null) {
                            PackageSetting sourcePs = (PackageSetting) sourcePkg.mExtras;
                            if (bp2.getSourcePackageSetting() == null) {
                                bp2.setSourcePackageSetting(sourcePs);
                            }
                        }
                    }
                    Slog.w(TAG, "Removing dangling permission: " + bp2.getName() + " from package " + bp2.getSourcePackageName());
                    this.mSettings.removePermissionLocked(bp2.getName());
                }
            }
        }
        return flags;
    }

    public /* synthetic */ void lambda$updatePermissions$1$PermissionManagerService(BasePermission bp, int userId, PermissionManagerServiceInternal.PermissionCallback callback, PackageParser.Package p) {
        String pName = p.packageName;
        ApplicationInfo appInfo = this.mPackageManagerInt.getApplicationInfo(pName, 0, 1000, 0);
        if (appInfo == null || appInfo.targetSdkVersion >= 23) {
            String permissionName = bp.getName();
            if (checkPermission(permissionName, pName, 1000, userId) == 0) {
                try {
                    revokeRuntimePermission(permissionName, pName, false, userId, callback);
                } catch (IllegalArgumentException e) {
                    Slog.e(TAG, "Failed to revoke " + permissionName + " from " + pName, e);
                }
            }
        }
    }

    private int updatePermissionTrees(String packageName, PackageParser.Package pkg, int flags) {
        Set<BasePermission> needsUpdate = null;
        synchronized (this.mLock) {
            Iterator<BasePermission> it = this.mSettings.mPermissionTrees.values().iterator();
            while (it.hasNext()) {
                BasePermission bp = it.next();
                if (bp.getSourcePackageSetting() == null) {
                    if (needsUpdate == null) {
                        needsUpdate = new ArraySet<>(this.mSettings.mPermissionTrees.size());
                    }
                    needsUpdate.add(bp);
                } else if (packageName != null && packageName.equals(bp.getSourcePackageName())) {
                    if (pkg == null || !hasPermission(pkg, bp.getName())) {
                        Slog.i(TAG, "Removing old permission tree: " + bp.getName() + " from package " + bp.getSourcePackageName());
                        flags |= 1;
                        it.remove();
                    }
                }
            }
        }
        if (needsUpdate != null) {
            for (BasePermission bp2 : needsUpdate) {
                PackageParser.Package sourcePkg = this.mPackageManagerInt.getPackage(bp2.getSourcePackageName());
                synchronized (this.mLock) {
                    if (sourcePkg != null) {
                        if (sourcePkg.mExtras != null) {
                            PackageSetting sourcePs = (PackageSetting) sourcePkg.mExtras;
                            if (bp2.getSourcePackageSetting() == null) {
                                bp2.setSourcePackageSetting(sourcePs);
                            }
                        }
                    }
                    Slog.w(TAG, "Removing dangling permission tree: " + bp2.getName() + " from package " + bp2.getSourcePackageName());
                    this.mSettings.removePermissionLocked(bp2.getName());
                }
            }
        }
        return flags;
    }

    /* access modifiers changed from: private */
    public void updatePermissionFlags(String permName, String packageName, int flagMask, int flagValues, int callingUid, int userId, boolean overridePolicy, PermissionManagerServiceInternal.PermissionCallback callback) {
        int flagValues2;
        int flagValues3;
        BasePermission bp;
        String str = permName;
        String str2 = packageName;
        int i = callingUid;
        int i2 = userId;
        PermissionManagerServiceInternal.PermissionCallback permissionCallback = callback;
        if (this.mUserManagerInt.exists(i2)) {
            enforceGrantRevokeRuntimePermissionPermissions("updatePermissionFlags");
            enforceCrossUserPermission(callingUid, userId, true, true, false, "updatePermissionFlags");
            if ((flagMask & 4) == 0 || overridePolicy) {
                if (i != 1000) {
                    flagValues2 = flagValues & -17 & -33 & -65 & -4097 & -2049 & -8193 & -16385;
                    flagValues3 = flagMask & -17 & -33;
                } else {
                    flagValues3 = flagMask;
                    flagValues2 = flagValues;
                }
                PackageParser.Package pkg = this.mPackageManagerInt.getPackage(str2);
                if (pkg == null || pkg.mExtras == null) {
                    Log.e(TAG, "Unknown package: " + str2);
                } else if (!this.mPackageManagerInt.filterAppAccess(pkg, i, i2)) {
                    synchronized (this.mLock) {
                        bp = this.mSettings.getPermissionLocked(str);
                    }
                    if (bp != null) {
                        PermissionsState permissionsState = ((PackageSetting) pkg.mExtras).getPermissionsState();
                        boolean hadState = permissionsState.getRuntimePermissionState(str, i2) != null;
                        boolean permissionUpdated = permissionsState.updatePermissionFlags(bp, i2, flagValues3, flagValues2);
                        if (permissionUpdated && bp.isRuntime()) {
                            notifyRuntimePermissionStateChanged(str2, i2);
                        }
                        if (permissionUpdated && permissionCallback != null) {
                            if (permissionsState.getInstallPermissionState(str) != null) {
                                callback.onInstallPermissionUpdated();
                            } else if (permissionsState.getRuntimePermissionState(str, i2) != null || hadState) {
                                permissionCallback.onPermissionUpdated(new int[]{i2}, false);
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Unknown permission: " + str);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown package: " + str2);
                }
            } else {
                throw new SecurityException("updatePermissionFlags requires android.permission.ADJUST_RUNTIME_PERMISSIONS_POLICY");
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean updatePermissionFlagsForAllApps(int flagMask, int flagValues, int callingUid, int userId, Collection<PackageParser.Package> packages, PermissionManagerServiceInternal.PermissionCallback callback) {
        if (!this.mUserManagerInt.exists(userId)) {
            return false;
        }
        enforceGrantRevokeRuntimePermissionPermissions("updatePermissionFlagsForAllApps");
        enforceCrossUserPermission(callingUid, userId, true, true, false, "updatePermissionFlagsForAllApps");
        if (callingUid != 1000) {
            flagMask &= -17;
            flagValues &= -17;
        }
        boolean changed = false;
        for (PackageParser.Package pkg : packages) {
            PackageSetting ps = (PackageSetting) pkg.mExtras;
            if (ps != null) {
                changed |= ps.getPermissionsState().updatePermissionFlagsForAllPermissions(userId, flagMask, flagValues);
            }
        }
        return changed;
    }

    /* access modifiers changed from: private */
    public void enforceGrantRevokeRuntimePermissionPermissions(String message) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.GRANT_RUNTIME_PERMISSIONS") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.REVOKE_RUNTIME_PERMISSIONS") != 0) {
            throw new SecurityException(message + " requires " + "android.permission.GRANT_RUNTIME_PERMISSIONS" + " or " + "android.permission.REVOKE_RUNTIME_PERMISSIONS");
        }
    }

    private void enforceGrantRevokeGetRuntimePermissionPermissions(String message) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.GET_RUNTIME_PERMISSIONS") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.GRANT_RUNTIME_PERMISSIONS") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.REVOKE_RUNTIME_PERMISSIONS") != 0) {
            throw new SecurityException(message + " requires " + "android.permission.GRANT_RUNTIME_PERMISSIONS" + " or " + "android.permission.REVOKE_RUNTIME_PERMISSIONS" + " or " + "android.permission.GET_RUNTIME_PERMISSIONS");
        }
    }

    /* access modifiers changed from: private */
    public void enforceCrossUserPermission(int callingUid, int userId, boolean requireFullPermission, boolean checkShell, boolean requirePermissionWhenSameUser, String message) {
        if (userId >= 0) {
            if (checkShell) {
                PackageManagerServiceUtils.enforceShellRestriction("no_debugging_features", callingUid, userId);
            }
            if (!requirePermissionWhenSameUser && userId == UserHandle.getUserId(callingUid)) {
                return;
            }
            if ((!XSpaceUserHandle.isUidBelongtoXSpace(callingUid) || userId != 0) && callingUid != 1000 && callingUid != 0) {
                if (requireFullPermission) {
                    this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", message);
                    return;
                }
                try {
                    this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", message);
                } catch (SecurityException e) {
                    this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", message);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid userId " + userId);
        }
    }

    @GuardedBy({"mSettings.mLock", "mLock"})
    private int calculateCurrentPermissionFootprintLocked(BasePermission tree) {
        int size = 0;
        for (BasePermission perm : this.mSettings.mPermissions.values()) {
            size += tree.calculateFootprint(perm);
        }
        return size;
    }

    @GuardedBy({"mSettings.mLock", "mLock"})
    private void enforcePermissionCapLocked(PermissionInfo info, BasePermission tree) {
        if (tree.getUid() != 1000) {
            if (info.calculateFootprint() + calculateCurrentPermissionFootprintLocked(tree) > 32768) {
                throw new SecurityException("Permission tree size cap exceeded");
            }
        }
    }

    /* access modifiers changed from: private */
    public void systemReady() {
        this.mSystemReady = true;
        if (this.mPrivappPermissionsViolations == null) {
            this.mPermissionControllerManager = (PermissionControllerManager) this.mContext.getSystemService(PermissionControllerManager.class);
            this.mPermissionPolicyInternal = (PermissionPolicyInternal) LocalServices.getService(PermissionPolicyInternal.class);
            return;
        }
        throw new IllegalStateException("Signature|privileged permissions not in privapp-permissions whitelist: " + this.mPrivappPermissionsViolations);
    }

    private static String getVolumeUuidForPackage(PackageParser.Package pkg) {
        if (pkg == null) {
            return StorageManager.UUID_PRIVATE_INTERNAL;
        }
        if (!pkg.isExternal()) {
            return StorageManager.UUID_PRIVATE_INTERNAL;
        }
        if (TextUtils.isEmpty(pkg.volumeUuid)) {
            return "primary_physical";
        }
        return pkg.volumeUuid;
    }

    private static boolean hasPermission(PackageParser.Package pkgInfo, String permName) {
        for (int i = pkgInfo.permissions.size() - 1; i >= 0; i--) {
            if (((PackageParser.Permission) pkgInfo.permissions.get(i)).info.name.equals(permName)) {
                return true;
            }
        }
        return false;
    }

    private void logPermission(int action, String name, String packageName) {
        LogMaker log = new LogMaker(action);
        log.setPackageName(packageName);
        log.addTaggedData(1241, name);
        this.mMetricsLogger.write(log);
    }

    public ArrayMap<String, List<String>> getBackgroundPermissions() {
        return this.mBackgroundPermissions;
    }

    private class PermissionManagerServiceInternalImpl extends PermissionManagerServiceInternal {
        private PermissionManagerServiceInternalImpl() {
        }

        public void systemReady() {
            PermissionManagerService.this.systemReady();
        }

        public boolean isPermissionsReviewRequired(PackageParser.Package pkg, int userId) {
            return PermissionManagerService.this.isPermissionsReviewRequired(pkg, userId);
        }

        public void revokeRuntimePermissionsIfGroupChanged(PackageParser.Package newPackage, PackageParser.Package oldPackage, ArrayList<String> allPackageNames, PermissionManagerServiceInternal.PermissionCallback permissionCallback) {
            PermissionManagerService.this.revokeRuntimePermissionsIfGroupChanged(newPackage, oldPackage, allPackageNames, permissionCallback);
        }

        public void addAllPermissions(PackageParser.Package pkg, boolean chatty) {
            PermissionManagerService.this.addAllPermissions(pkg, chatty);
        }

        public void addAllPermissionGroups(PackageParser.Package pkg, boolean chatty) {
            PermissionManagerService.this.addAllPermissionGroups(pkg, chatty);
        }

        public void removeAllPermissions(PackageParser.Package pkg, boolean chatty) {
            PermissionManagerService.this.removeAllPermissions(pkg, chatty);
        }

        public boolean addDynamicPermission(PermissionInfo info, boolean async, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
            return PermissionManagerService.this.addDynamicPermission(info, callingUid, callback);
        }

        public void removeDynamicPermission(String permName, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.removeDynamicPermission(permName, callingUid, callback);
        }

        public void grantRuntimePermission(String permName, String packageName, boolean overridePolicy, int callingUid, int userId, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.grantRuntimePermission(permName, packageName, overridePolicy, callingUid, userId, callback);
        }

        public void grantRequestedRuntimePermissions(PackageParser.Package pkg, int[] userIds, String[] grantedPermissions, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.grantRequestedRuntimePermissions(pkg, userIds, grantedPermissions, callingUid, callback);
        }

        public List<String> getWhitelistedRestrictedPermissions(PackageParser.Package pkg, int whitelistFlags, int userId) {
            return PermissionManagerService.this.getWhitelistedRestrictedPermissions(pkg, whitelistFlags, userId);
        }

        public void setWhitelistedRestrictedPermissions(PackageParser.Package pkg, int[] userIds, List<String> permissions, int callingUid, int whitelistFlags, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.setWhitelistedRestrictedPermissions(pkg, userIds, permissions, callingUid, whitelistFlags, callback);
        }

        public void grantRuntimePermissionsGrantedToDisabledPackage(PackageParser.Package pkg, int callingUid, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.grantRuntimePermissionsGrantedToDisabledPackageLocked(pkg, callingUid, callback);
        }

        public void revokeRuntimePermission(String permName, String packageName, boolean overridePolicy, int userId, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.revokeRuntimePermission(permName, packageName, overridePolicy, userId, callback);
        }

        public void revokeRuntimePermissionNotKill(String permName, String packageName, boolean overridePolicy, int userId, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.revokeRuntimePermission(permName, packageName, overridePolicy, userId, callback, false);
        }

        public void updatePermissions(String packageName, PackageParser.Package pkg, boolean replaceGrant, Collection<PackageParser.Package> allPackages, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.updatePermissions(packageName, pkg, replaceGrant, allPackages, callback);
        }

        public void updateAllPermissions(String volumeUuid, boolean sdkUpdated, Collection<PackageParser.Package> allPackages, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.updateAllPermissions(volumeUuid, sdkUpdated, allPackages, callback);
        }

        public String[] getAppOpPermissionPackages(String permName) {
            return PermissionManagerService.this.getAppOpPermissionPackages(permName);
        }

        public int getPermissionFlags(String permName, String packageName, int callingUid, int userId) {
            return PermissionManagerService.this.getPermissionFlags(permName, packageName, callingUid, userId);
        }

        public void updatePermissionFlags(String permName, String packageName, int flagMask, int flagValues, int callingUid, int userId, boolean overridePolicy, PermissionManagerServiceInternal.PermissionCallback callback) {
            PermissionManagerService.this.updatePermissionFlags(permName, packageName, flagMask, flagValues, callingUid, userId, overridePolicy, callback);
        }

        public boolean updatePermissionFlagsForAllApps(int flagMask, int flagValues, int callingUid, int userId, Collection<PackageParser.Package> packages, PermissionManagerServiceInternal.PermissionCallback callback) {
            return PermissionManagerService.this.updatePermissionFlagsForAllApps(flagMask, flagValues, callingUid, userId, packages, callback);
        }

        public void enforceCrossUserPermission(int callingUid, int userId, boolean requireFullPermission, boolean checkShell, String message) {
            PermissionManagerService.this.enforceCrossUserPermission(callingUid, userId, requireFullPermission, checkShell, false, message);
        }

        public void enforceCrossUserPermission(int callingUid, int userId, boolean requireFullPermission, boolean checkShell, boolean requirePermissionWhenSameUser, String message) {
            PermissionManagerService.this.enforceCrossUserPermission(callingUid, userId, requireFullPermission, checkShell, requirePermissionWhenSameUser, message);
        }

        public void enforceGrantRevokeRuntimePermissionPermissions(String message) {
            PermissionManagerService.this.enforceGrantRevokeRuntimePermissionPermissions(message);
        }

        public int checkPermission(String permName, String packageName, int callingUid, int userId) {
            return PermissionManagerService.this.checkPermission(permName, packageName, callingUid, userId);
        }

        public int checkUidPermission(String permName, PackageParser.Package pkg, int uid, int callingUid) {
            return PermissionManagerService.this.checkUidPermission(permName, pkg, uid, callingUid);
        }

        public PermissionGroupInfo getPermissionGroupInfo(String groupName, int flags, int callingUid) {
            return PermissionManagerService.this.getPermissionGroupInfo(groupName, flags, callingUid);
        }

        public List<PermissionGroupInfo> getAllPermissionGroups(int flags, int callingUid) {
            return PermissionManagerService.this.getAllPermissionGroups(flags, callingUid);
        }

        public PermissionInfo getPermissionInfo(String permName, String packageName, int flags, int callingUid) {
            return PermissionManagerService.this.getPermissionInfo(permName, packageName, flags, callingUid);
        }

        public List<PermissionInfo> getPermissionInfoByGroup(String group, int flags, int callingUid) {
            return PermissionManagerService.this.getPermissionInfoByGroup(group, flags, callingUid);
        }

        public PermissionSettings getPermissionSettings() {
            return PermissionManagerService.this.mSettings;
        }

        public DefaultPermissionGrantPolicy getDefaultPermissionGrantPolicy() {
            return PermissionManagerService.this.mDefaultPermissionGrantPolicy;
        }

        public BasePermission getPermissionTEMP(String permName) {
            BasePermission permissionLocked;
            synchronized (PermissionManagerService.this.mLock) {
                permissionLocked = PermissionManagerService.this.mSettings.getPermissionLocked(permName);
            }
            return permissionLocked;
        }

        public ArrayList<PermissionInfo> getAllPermissionWithProtectionLevel(int protectionLevel) {
            ArrayList<PermissionInfo> matchingPermissions = new ArrayList<>();
            synchronized (PermissionManagerService.this.mLock) {
                int numTotalPermissions = PermissionManagerService.this.mSettings.mPermissions.size();
                for (int i = 0; i < numTotalPermissions; i++) {
                    BasePermission bp = PermissionManagerService.this.mSettings.mPermissions.valueAt(i);
                    if (!(bp.perm == null || bp.perm.info == null || bp.protectionLevel != protectionLevel)) {
                        matchingPermissions.add(bp.perm.info);
                    }
                }
            }
            return matchingPermissions;
        }

        public byte[] backupRuntimePermissions(UserHandle user) {
            return PermissionManagerService.this.backupRuntimePermissions(user);
        }

        public void restoreRuntimePermissions(byte[] backup, UserHandle user) {
            PermissionManagerService.this.restoreRuntimePermissions(backup, user);
        }

        public void restoreDelayedRuntimePermissions(String packageName, UserHandle user) {
            PermissionManagerService.this.restoreDelayedRuntimePermissions(packageName, user);
        }

        public void addOnRuntimePermissionStateChangedListener(PermissionManagerInternal.OnRuntimePermissionStateChangedListener listener) {
            PermissionManagerService.this.addOnRuntimePermissionStateChangedListener(listener);
        }

        public void removeOnRuntimePermissionStateChangedListener(PermissionManagerInternal.OnRuntimePermissionStateChangedListener listener) {
            PermissionManagerService.this.removeOnRuntimePermissionStateChangedListener(listener);
        }
    }
}
