package com.android.server.connectivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.UserInfo;
import android.net.INetd;
import android.net.UidRange;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.os.UserHandle;
import android.os.UserManager;
import android.system.OsConstants;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class PermissionMonitor {
    private static final boolean DBG = true;
    protected static final Boolean NETWORK = Boolean.FALSE;
    protected static final Boolean SYSTEM = Boolean.TRUE;
    private static final String TAG = "PermissionMonitor";
    private static final int VERSION_Q = 29;
    @GuardedBy({"this"})
    private final Set<Integer> mAllApps = new HashSet();
    @GuardedBy({"this"})
    private final Map<Integer, Boolean> mApps = new HashMap();
    private final INetd mNetd;
    /* access modifiers changed from: private */
    public final PackageManager mPackageManager;
    private final UserManager mUserManager;
    @GuardedBy({"this"})
    private final Set<Integer> mUsers = new HashSet();
    @GuardedBy({"this"})
    private final Map<String, Set<UidRange>> mVpnUidRanges = new HashMap();

    private class PackageListObserver implements PackageManagerInternal.PackageListObserver {
        private PackageListObserver() {
        }

        private int getPermissionForUid(int uid) {
            int permission = 0;
            String[] packages = PermissionMonitor.this.mPackageManager.getPackagesForUid(uid);
            if (packages == null || packages.length <= 0) {
                return -1;
            }
            for (String name : packages) {
                PackageInfo app = PermissionMonitor.this.getPackageInfo(name);
                if (!(app == null || app.requestedPermissions == null)) {
                    permission |= PermissionMonitor.getNetdPermissionMask(app.requestedPermissions, app.requestedPermissionsFlags);
                }
            }
            return permission;
        }

        public void onPackageAdded(String packageName, int uid) {
            PermissionMonitor.this.sendPackagePermissionsForUid(uid, getPermissionForUid(uid));
        }

        public void onPackageChanged(String packageName, int uid) {
            PermissionMonitor.this.sendPackagePermissionsForUid(uid, getPermissionForUid(uid));
        }

        public void onPackageRemoved(String packageName, int uid) {
            PermissionMonitor.this.sendPackagePermissionsForUid(uid, getPermissionForUid(uid));
        }
    }

    public PermissionMonitor(Context context, INetd netd) {
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mNetd = netd;
    }

    public synchronized void startMonitoring() {
        Boolean permission;
        log("Monitoring");
        PackageManagerInternal pmi = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        if (pmi != null) {
            pmi.getPackageList(new PackageListObserver());
        } else {
            loge("failed to get the PackageManagerInternal service");
        }
        List<PackageInfo> apps = this.mPackageManager.getInstalledPackages(4198400);
        if (apps == null) {
            loge("No apps");
            return;
        }
        SparseIntArray netdPermsUids = new SparseIntArray();
        for (PackageInfo app : apps) {
            int uid = app.applicationInfo != null ? app.applicationInfo.uid : -1;
            if (uid >= 0) {
                this.mAllApps.add(Integer.valueOf(UserHandle.getAppId(uid)));
                boolean isNetwork = hasNetworkPermission(app);
                boolean hasRestrictedPermission = hasRestrictedNetworkPermission(app);
                if ((isNetwork || hasRestrictedPermission) && ((permission = this.mApps.get(Integer.valueOf(uid))) == null || permission == NETWORK)) {
                    this.mApps.put(Integer.valueOf(uid), Boolean.valueOf(hasRestrictedPermission));
                }
                netdPermsUids.put(uid, netdPermsUids.get(uid) | getNetdPermissionMask(app.requestedPermissions, app.requestedPermissionsFlags));
            }
        }
        List<UserInfo> users = this.mUserManager.getUsers(true);
        if (users != null) {
            for (UserInfo user : users) {
                this.mUsers.add(Integer.valueOf(user.id));
            }
        }
        SparseArray<ArraySet<String>> systemPermission = SystemConfig.getInstance().getSystemPermissions();
        for (int i = 0; i < systemPermission.size(); i++) {
            ArraySet<String> perms = systemPermission.valueAt(i);
            int uid2 = systemPermission.keyAt(i);
            int netdPermission = 0;
            if (perms != null) {
                netdPermission = 0 | (perms.contains("android.permission.UPDATE_DEVICE_STATS") ? 8 : 0) | (perms.contains("android.permission.INTERNET") ? 4 : 0);
            }
            netdPermsUids.put(uid2, netdPermsUids.get(uid2) | netdPermission);
        }
        log("Users: " + this.mUsers.size() + ", Apps: " + this.mApps.size());
        update(this.mUsers, this.mApps, true);
        sendPackagePermissionsToNetd(netdPermsUids);
    }

    @VisibleForTesting
    static boolean isVendorApp(ApplicationInfo appInfo) {
        return appInfo.isVendor() || appInfo.isOem() || appInfo.isProduct();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public int getDeviceFirstSdkInt() {
        return Build.VERSION.FIRST_SDK_INT;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean hasPermission(PackageInfo app, String permission) {
        int index;
        if (app.requestedPermissions == null || app.requestedPermissionsFlags == null || (index = ArrayUtils.indexOf(app.requestedPermissions, permission)) < 0 || index >= app.requestedPermissionsFlags.length || (app.requestedPermissionsFlags[index] & 2) == 0) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean hasNetworkPermission(PackageInfo app) {
        return hasPermission(app, "android.permission.CHANGE_NETWORK_STATE");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean hasRestrictedNetworkPermission(PackageInfo app) {
        if (app.applicationInfo != null) {
            if (app.applicationInfo.uid == 1000 && getDeviceFirstSdkInt() < 29) {
                return true;
            }
            if (app.applicationInfo.targetSdkVersion < 29 && isVendorApp(app.applicationInfo)) {
                return true;
            }
        }
        if (hasPermission(app, "android.permission.CONNECTIVITY_INTERNAL") || hasPermission(app, "android.permission.NETWORK_STACK") || hasPermission(app, "android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS")) {
            return true;
        }
        return false;
    }

    public synchronized boolean hasUseBackgroundNetworksPermission(int uid) {
        return this.mApps.containsKey(Integer.valueOf(uid));
    }

    private int[] toIntArray(Collection<Integer> list) {
        int[] array = new int[list.size()];
        int i = 0;
        for (Integer item : list) {
            array[i] = item.intValue();
            i++;
        }
        return array;
    }

    private void update(Set<Integer> users, Map<Integer, Boolean> apps, boolean add) {
        List<Integer> network = new ArrayList<>();
        List<Integer> system = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> app : apps.entrySet()) {
            List<Integer> list = app.getValue().booleanValue() ? system : network;
            for (Integer intValue : users) {
                list.add(Integer.valueOf(UserHandle.getUid(intValue.intValue(), app.getKey().intValue())));
            }
        }
        if (add) {
            try {
                this.mNetd.networkSetPermissionForUser(1, toIntArray(network));
                this.mNetd.networkSetPermissionForUser(2, toIntArray(system));
            } catch (RemoteException e) {
                loge("Exception when updating permissions: " + e);
            }
        } else {
            this.mNetd.networkClearPermissionForUser(toIntArray(network));
            this.mNetd.networkClearPermissionForUser(toIntArray(system));
        }
    }

    public synchronized void onUserAdded(int user) {
        if (user < 0) {
            loge("Invalid user in onUserAdded: " + user);
            return;
        }
        this.mUsers.add(Integer.valueOf(user));
        Set<Integer> users = new HashSet<>();
        users.add(Integer.valueOf(user));
        update(users, this.mApps, true);
    }

    public synchronized void onUserRemoved(int user) {
        if (user < 0) {
            loge("Invalid user in onUserRemoved: " + user);
            return;
        }
        this.mUsers.remove(Integer.valueOf(user));
        Set<Integer> users = new HashSet<>();
        users.add(Integer.valueOf(user));
        update(users, this.mApps, false);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public Boolean highestPermissionForUid(Boolean currentPermission, String name) {
        if (currentPermission == SYSTEM) {
            return currentPermission;
        }
        try {
            PackageInfo app = this.mPackageManager.getPackageInfo(name, 4096);
            boolean isNetwork = hasNetworkPermission(app);
            boolean hasRestrictedPermission = hasRestrictedNetworkPermission(app);
            if (isNetwork || hasRestrictedPermission) {
                return Boolean.valueOf(hasRestrictedPermission);
            }
            return currentPermission;
        } catch (PackageManager.NameNotFoundException e) {
            loge("NameNotFoundException " + name);
            return currentPermission;
        }
    }

    public synchronized void onPackageAdded(String packageName, int uid) {
        Boolean permission = highestPermissionForUid(this.mApps.get(Integer.valueOf(uid)), packageName);
        if (permission != this.mApps.get(Integer.valueOf(uid))) {
            this.mApps.put(Integer.valueOf(uid), permission);
            Map<Integer, Boolean> apps = new HashMap<>();
            apps.put(Integer.valueOf(uid), permission);
            update(this.mUsers, apps, true);
        }
        for (Map.Entry<String, Set<UidRange>> vpn : this.mVpnUidRanges.entrySet()) {
            if (UidRange.containsUid(vpn.getValue(), uid)) {
                Set<Integer> changedUids = new HashSet<>();
                changedUids.add(Integer.valueOf(uid));
                removeBypassingUids(changedUids, -1);
                updateVpnUids(vpn.getKey(), changedUids, true);
            }
        }
        this.mAllApps.add(Integer.valueOf(UserHandle.getAppId(uid)));
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00b9, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onPackageRemoved(int r9) {
        /*
            r8 = this;
            monitor-enter(r8)
            java.util.Map<java.lang.String, java.util.Set<android.net.UidRange>> r0 = r8.mVpnUidRanges     // Catch:{ all -> 0x00ba }
            java.util.Set r0 = r0.entrySet()     // Catch:{ all -> 0x00ba }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x00ba }
        L_0x000b:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x00ba }
            r2 = 0
            if (r1 == 0) goto L_0x003e
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x00ba }
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1     // Catch:{ all -> 0x00ba }
            java.lang.Object r3 = r1.getValue()     // Catch:{ all -> 0x00ba }
            java.util.Collection r3 = (java.util.Collection) r3     // Catch:{ all -> 0x00ba }
            boolean r3 = android.net.UidRange.containsUid(r3, r9)     // Catch:{ all -> 0x00ba }
            if (r3 == 0) goto L_0x003d
            java.util.HashSet r3 = new java.util.HashSet     // Catch:{ all -> 0x00ba }
            r3.<init>()     // Catch:{ all -> 0x00ba }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ba }
            r3.add(r4)     // Catch:{ all -> 0x00ba }
            r4 = -1
            r8.removeBypassingUids(r3, r4)     // Catch:{ all -> 0x00ba }
            java.lang.Object r4 = r1.getKey()     // Catch:{ all -> 0x00ba }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x00ba }
            r8.updateVpnUids(r4, r3, r2)     // Catch:{ all -> 0x00ba }
        L_0x003d:
            goto L_0x000b
        L_0x003e:
            android.content.pm.PackageManager r0 = r8.mPackageManager     // Catch:{ all -> 0x00ba }
            java.lang.String r0 = r0.getNameForUid(r9)     // Catch:{ all -> 0x00ba }
            if (r0 != 0) goto L_0x0053
            java.util.Set<java.lang.Integer> r0 = r8.mAllApps     // Catch:{ all -> 0x00ba }
            int r1 = android.os.UserHandle.getAppId(r9)     // Catch:{ all -> 0x00ba }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00ba }
            r0.remove(r1)     // Catch:{ all -> 0x00ba }
        L_0x0053:
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x00ba }
            r0.<init>()     // Catch:{ all -> 0x00ba }
            r1 = 0
            android.content.pm.PackageManager r3 = r8.mPackageManager     // Catch:{ all -> 0x00ba }
            java.lang.String[] r3 = r3.getPackagesForUid(r9)     // Catch:{ all -> 0x00ba }
            if (r3 == 0) goto L_0x007a
            int r4 = r3.length     // Catch:{ all -> 0x00ba }
            if (r4 <= 0) goto L_0x007a
            int r4 = r3.length     // Catch:{ all -> 0x00ba }
            r5 = r1
            r1 = r2
        L_0x0067:
            if (r1 >= r4) goto L_0x0079
            r6 = r3[r1]     // Catch:{ all -> 0x00ba }
            java.lang.Boolean r7 = r8.highestPermissionForUid(r5, r6)     // Catch:{ all -> 0x00ba }
            r5 = r7
            java.lang.Boolean r7 = SYSTEM     // Catch:{ all -> 0x00ba }
            if (r5 != r7) goto L_0x0076
            monitor-exit(r8)
            return
        L_0x0076:
            int r1 = r1 + 1
            goto L_0x0067
        L_0x0079:
            r1 = r5
        L_0x007a:
            java.util.Map<java.lang.Integer, java.lang.Boolean> r4 = r8.mApps     // Catch:{ all -> 0x00ba }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ba }
            java.lang.Object r4 = r4.get(r5)     // Catch:{ all -> 0x00ba }
            if (r1 != r4) goto L_0x0088
            monitor-exit(r8)
            return
        L_0x0088:
            if (r1 == 0) goto L_0x00a1
            java.util.Map<java.lang.Integer, java.lang.Boolean> r2 = r8.mApps     // Catch:{ all -> 0x00ba }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ba }
            r2.put(r4, r1)     // Catch:{ all -> 0x00ba }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ba }
            r0.put(r2, r1)     // Catch:{ all -> 0x00ba }
            java.util.Set<java.lang.Integer> r2 = r8.mUsers     // Catch:{ all -> 0x00ba }
            r4 = 1
            r8.update(r2, r0, r4)     // Catch:{ all -> 0x00ba }
            goto L_0x00b8
        L_0x00a1:
            java.util.Map<java.lang.Integer, java.lang.Boolean> r4 = r8.mApps     // Catch:{ all -> 0x00ba }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ba }
            r4.remove(r5)     // Catch:{ all -> 0x00ba }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ba }
            java.lang.Boolean r5 = NETWORK     // Catch:{ all -> 0x00ba }
            r0.put(r4, r5)     // Catch:{ all -> 0x00ba }
            java.util.Set<java.lang.Integer> r4 = r8.mUsers     // Catch:{ all -> 0x00ba }
            r8.update(r4, r0, r2)     // Catch:{ all -> 0x00ba }
        L_0x00b8:
            monitor-exit(r8)
            return
        L_0x00ba:
            r9 = move-exception
            monitor-exit(r8)
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.PermissionMonitor.onPackageRemoved(int):void");
    }

    /* access modifiers changed from: private */
    public static int getNetdPermissionMask(String[] requestedPermissions, int[] requestedPermissionsFlags) {
        int permissions = 0;
        if (requestedPermissions == null || requestedPermissionsFlags == null) {
            return 0;
        }
        for (int i = 0; i < requestedPermissions.length; i++) {
            if (requestedPermissions[i].equals("android.permission.INTERNET") && (requestedPermissionsFlags[i] & 2) != 0) {
                permissions |= 4;
            }
            if (requestedPermissions[i].equals("android.permission.UPDATE_DEVICE_STATS") && (requestedPermissionsFlags[i] & 2) != 0) {
                permissions |= 8;
            }
        }
        return permissions;
    }

    /* access modifiers changed from: private */
    public PackageInfo getPackageInfo(String packageName) {
        try {
            return this.mPackageManager.getPackageInfo(packageName, 4198400);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public synchronized void onVpnUidRangesAdded(String iface, Set<UidRange> rangesToAdd, int vpnAppUid) {
        Set<Integer> changedUids = intersectUids(rangesToAdd, this.mAllApps);
        removeBypassingUids(changedUids, vpnAppUid);
        updateVpnUids(iface, changedUids, true);
        if (this.mVpnUidRanges.containsKey(iface)) {
            this.mVpnUidRanges.get(iface).addAll(rangesToAdd);
        } else {
            this.mVpnUidRanges.put(iface, new HashSet(rangesToAdd));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onVpnUidRangesRemoved(java.lang.String r5, java.util.Set<android.net.UidRange> r6, int r7) {
        /*
            r4 = this;
            monitor-enter(r4)
            java.util.Set<java.lang.Integer> r0 = r4.mAllApps     // Catch:{ all -> 0x003f }
            java.util.Set r0 = r4.intersectUids(r6, r0)     // Catch:{ all -> 0x003f }
            r4.removeBypassingUids(r0, r7)     // Catch:{ all -> 0x003f }
            r1 = 0
            r4.updateVpnUids(r5, r0, r1)     // Catch:{ all -> 0x003f }
            java.util.Map<java.lang.String, java.util.Set<android.net.UidRange>> r1 = r4.mVpnUidRanges     // Catch:{ all -> 0x003f }
            r2 = 0
            java.lang.Object r1 = r1.getOrDefault(r5, r2)     // Catch:{ all -> 0x003f }
            java.util.Set r1 = (java.util.Set) r1     // Catch:{ all -> 0x003f }
            if (r1 != 0) goto L_0x002f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x003f }
            r2.<init>()     // Catch:{ all -> 0x003f }
            java.lang.String r3 = "Attempt to remove unknown vpn uid Range iface = "
            r2.append(r3)     // Catch:{ all -> 0x003f }
            r2.append(r5)     // Catch:{ all -> 0x003f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x003f }
            loge(r2)     // Catch:{ all -> 0x003f }
            monitor-exit(r4)
            return
        L_0x002f:
            r1.removeAll(r6)     // Catch:{ all -> 0x003f }
            int r2 = r1.size()     // Catch:{ all -> 0x003f }
            if (r2 != 0) goto L_0x003d
            java.util.Map<java.lang.String, java.util.Set<android.net.UidRange>> r2 = r4.mVpnUidRanges     // Catch:{ all -> 0x003f }
            r2.remove(r5)     // Catch:{ all -> 0x003f }
        L_0x003d:
            monitor-exit(r4)
            return
        L_0x003f:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.connectivity.PermissionMonitor.onVpnUidRangesRemoved(java.lang.String, java.util.Set, int):void");
    }

    private Set<Integer> intersectUids(Set<UidRange> ranges, Set<Integer> appIds) {
        Set<Integer> result = new HashSet<>();
        for (UidRange range : ranges) {
            for (int userId = range.getStartUser(); userId <= range.getEndUser(); userId++) {
                for (Integer intValue : appIds) {
                    int uid = UserHandle.getUid(userId, intValue.intValue());
                    if (range.contains(uid)) {
                        result.add(Integer.valueOf(uid));
                    }
                }
            }
        }
        return result;
    }

    private void removeBypassingUids(Set<Integer> uids, int vpnAppUid) {
        uids.remove(Integer.valueOf(vpnAppUid));
        uids.removeIf(new Predicate() {
            public final boolean test(Object obj) {
                return PermissionMonitor.this.lambda$removeBypassingUids$0$PermissionMonitor((Integer) obj);
            }
        });
    }

    public /* synthetic */ boolean lambda$removeBypassingUids$0$PermissionMonitor(Integer uid) {
        return this.mApps.getOrDefault(uid, NETWORK) == SYSTEM;
    }

    private void updateVpnUids(String iface, Set<Integer> uids, boolean add) {
        if (uids.size() != 0) {
            if (add) {
                try {
                    this.mNetd.firewallAddUidInterfaceRules(iface, toIntArray(uids));
                } catch (ServiceSpecificException e) {
                    if (e.errorCode != OsConstants.EOPNOTSUPP) {
                        loge("Exception when updating permissions: ", e);
                    }
                } catch (RemoteException e2) {
                    loge("Exception when updating permissions: ", e2);
                }
            } else {
                this.mNetd.firewallRemoveUidInterfaceRules(toIntArray(uids));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void sendPackagePermissionsForUid(int uid, int permissions) {
        SparseIntArray netdPermissionsAppIds = new SparseIntArray();
        netdPermissionsAppIds.put(uid, permissions);
        sendPackagePermissionsToNetd(netdPermissionsAppIds);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void sendPackagePermissionsToNetd(SparseIntArray netdPermissionsAppIds) {
        if (this.mNetd == null) {
            Log.e(TAG, "Failed to get the netd service");
            return;
        }
        ArrayList<Integer> allPermissionAppIds = new ArrayList<>();
        ArrayList<Integer> internetPermissionAppIds = new ArrayList<>();
        ArrayList<Integer> updateStatsPermissionAppIds = new ArrayList<>();
        ArrayList<Integer> noPermissionAppIds = new ArrayList<>();
        ArrayList<Integer> uninstalledAppIds = new ArrayList<>();
        for (int i = 0; i < netdPermissionsAppIds.size(); i++) {
            int permissions = netdPermissionsAppIds.valueAt(i);
            if (permissions != -1) {
                if (permissions == 0) {
                    noPermissionAppIds.add(Integer.valueOf(netdPermissionsAppIds.keyAt(i)));
                } else if (permissions == 4) {
                    internetPermissionAppIds.add(Integer.valueOf(netdPermissionsAppIds.keyAt(i)));
                } else if (permissions == 8) {
                    updateStatsPermissionAppIds.add(Integer.valueOf(netdPermissionsAppIds.keyAt(i)));
                } else if (permissions == 12) {
                    allPermissionAppIds.add(Integer.valueOf(netdPermissionsAppIds.keyAt(i)));
                }
            } else {
                uninstalledAppIds.add(Integer.valueOf(netdPermissionsAppIds.keyAt(i)));
            }
            Log.e(TAG, "unknown permission type: " + permissions + "for uid: " + netdPermissionsAppIds.keyAt(i));
        }
        try {
            if (allPermissionAppIds.size() != 0) {
                this.mNetd.trafficSetNetPermForUids(12, ArrayUtils.convertToIntArray(allPermissionAppIds));
            }
            if (internetPermissionAppIds.size() != 0) {
                this.mNetd.trafficSetNetPermForUids(4, ArrayUtils.convertToIntArray(internetPermissionAppIds));
            }
            if (updateStatsPermissionAppIds.size() != 0) {
                this.mNetd.trafficSetNetPermForUids(8, ArrayUtils.convertToIntArray(updateStatsPermissionAppIds));
            }
            if (noPermissionAppIds.size() != 0) {
                this.mNetd.trafficSetNetPermForUids(0, ArrayUtils.convertToIntArray(noPermissionAppIds));
            }
            if (uninstalledAppIds.size() != 0) {
                this.mNetd.trafficSetNetPermForUids(-1, ArrayUtils.convertToIntArray(uninstalledAppIds));
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Pass appId list of special permission failed." + e);
        }
    }

    @VisibleForTesting
    public Set<UidRange> getVpnUidRanges(String iface) {
        return this.mVpnUidRanges.get(iface);
    }

    public void dump(IndentingPrintWriter pw) {
        pw.println("Interface filtering rules:");
        pw.increaseIndent();
        for (Map.Entry<String, Set<UidRange>> vpn : this.mVpnUidRanges.entrySet()) {
            pw.println("Interface: " + vpn.getKey());
            pw.println("UIDs: " + vpn.getValue().toString());
            pw.println();
        }
        pw.decreaseIndent();
    }

    private static void log(String s) {
        Log.d(TAG, s);
    }

    private static void loge(String s) {
        Log.e(TAG, s);
    }

    private static void loge(String s, Throwable e) {
        Log.e(TAG, s, e);
    }
}
