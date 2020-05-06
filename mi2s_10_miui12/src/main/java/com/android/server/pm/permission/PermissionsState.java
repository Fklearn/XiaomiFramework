package com.android.server.pm.permission;

import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class PermissionsState {
    private static final int[] NO_GIDS = new int[0];
    public static final int PERMISSION_OPERATION_FAILURE = -1;
    public static final int PERMISSION_OPERATION_SUCCESS = 0;
    public static final int PERMISSION_OPERATION_SUCCESS_GIDS_CHANGED = 1;
    private int[] mGlobalGids = NO_GIDS;
    private final Object mLock = new Object();
    private SparseBooleanArray mPermissionReviewRequired;
    @GuardedBy({"mLock"})
    private ArrayMap<String, PermissionData> mPermissions;

    public PermissionsState() {
    }

    public PermissionsState(PermissionsState prototype) {
        copyFrom(prototype);
    }

    public void setGlobalGids(int[] globalGids) {
        if (!ArrayUtils.isEmpty(globalGids)) {
            this.mGlobalGids = Arrays.copyOf(globalGids, globalGids.length);
        }
    }

    public void copyFrom(PermissionsState other) {
        if (other != this) {
            synchronized (this.mLock) {
                if (this.mPermissions != null) {
                    if (other.mPermissions == null) {
                        this.mPermissions = null;
                    } else {
                        this.mPermissions.clear();
                    }
                }
                if (other.mPermissions != null) {
                    if (this.mPermissions == null) {
                        this.mPermissions = new ArrayMap<>();
                    }
                    int permissionCount = other.mPermissions.size();
                    for (int i = 0; i < permissionCount; i++) {
                        this.mPermissions.put(other.mPermissions.keyAt(i), new PermissionData(other.mPermissions.valueAt(i)));
                    }
                }
            }
            int[] iArr = NO_GIDS;
            this.mGlobalGids = iArr;
            int[] iArr2 = other.mGlobalGids;
            if (iArr2 != iArr) {
                this.mGlobalGids = Arrays.copyOf(iArr2, iArr2.length);
            }
            SparseBooleanArray sparseBooleanArray = this.mPermissionReviewRequired;
            if (sparseBooleanArray != null) {
                if (other.mPermissionReviewRequired == null) {
                    this.mPermissionReviewRequired = null;
                } else {
                    sparseBooleanArray.clear();
                }
            }
            if (other.mPermissionReviewRequired != null) {
                if (this.mPermissionReviewRequired == null) {
                    this.mPermissionReviewRequired = new SparseBooleanArray();
                }
                int userCount = other.mPermissionReviewRequired.size();
                for (int i2 = 0; i2 < userCount; i2++) {
                    this.mPermissionReviewRequired.put(other.mPermissionReviewRequired.keyAt(i2), other.mPermissionReviewRequired.valueAt(i2));
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0030, code lost:
        r2 = r5.mPermissionReviewRequired;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0032, code lost:
        if (r2 != null) goto L_0x0039;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0036, code lost:
        if (r1.mPermissionReviewRequired == null) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0038, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x003f, code lost:
        if (r2.equals(r1.mPermissionReviewRequired) != false) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0041, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004a, code lost:
        return java.util.Arrays.equals(r5.mGlobalGids, r1.mGlobalGids);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r6) {
        /*
            r5 = this;
            if (r5 != r6) goto L_0x0004
            r0 = 1
            return r0
        L_0x0004:
            r0 = 0
            if (r6 != 0) goto L_0x0008
            return r0
        L_0x0008:
            java.lang.Class r1 = r5.getClass()
            java.lang.Class r2 = r6.getClass()
            if (r1 == r2) goto L_0x0013
            return r0
        L_0x0013:
            r1 = r6
            com.android.server.pm.permission.PermissionsState r1 = (com.android.server.pm.permission.PermissionsState) r1
            java.lang.Object r2 = r5.mLock
            monitor-enter(r2)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r3 = r5.mPermissions     // Catch:{ all -> 0x004b }
            if (r3 != 0) goto L_0x0023
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r3 = r1.mPermissions     // Catch:{ all -> 0x004b }
            if (r3 == 0) goto L_0x002f
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            return r0
        L_0x0023:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r3 = r5.mPermissions     // Catch:{ all -> 0x004b }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r4 = r1.mPermissions     // Catch:{ all -> 0x004b }
            boolean r3 = r3.equals(r4)     // Catch:{ all -> 0x004b }
            if (r3 != 0) goto L_0x002f
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            return r0
        L_0x002f:
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            android.util.SparseBooleanArray r2 = r5.mPermissionReviewRequired
            if (r2 != 0) goto L_0x0039
            android.util.SparseBooleanArray r2 = r1.mPermissionReviewRequired
            if (r2 == 0) goto L_0x0042
            return r0
        L_0x0039:
            android.util.SparseBooleanArray r3 = r1.mPermissionReviewRequired
            boolean r2 = r2.equals(r3)
            if (r2 != 0) goto L_0x0042
            return r0
        L_0x0042:
            int[] r0 = r5.mGlobalGids
            int[] r2 = r1.mGlobalGids
            boolean r0 = java.util.Arrays.equals(r0, r2)
            return r0
        L_0x004b:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004b }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionsState.equals(java.lang.Object):boolean");
    }

    public boolean isPermissionReviewRequired(int userId) {
        SparseBooleanArray sparseBooleanArray = this.mPermissionReviewRequired;
        return sparseBooleanArray != null && sparseBooleanArray.get(userId);
    }

    public int grantInstallPermission(BasePermission permission) {
        return grantPermission(permission, -1);
    }

    public int revokeInstallPermission(BasePermission permission) {
        return revokePermission(permission, -1);
    }

    public int grantRuntimePermission(BasePermission permission, int userId) {
        enforceValidUserId(userId);
        if (userId == -1) {
            return -1;
        }
        return grantPermission(permission, userId);
    }

    public int revokeRuntimePermission(BasePermission permission, int userId) {
        enforceValidUserId(userId);
        if (userId == -1) {
            return -1;
        }
        return revokePermission(permission, userId);
    }

    public boolean hasRuntimePermission(String name, int userId) {
        enforceValidUserId(userId);
        return !hasInstallPermission(name) && hasPermission(name, userId);
    }

    public boolean hasInstallPermission(String name) {
        return hasPermission(name, -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001f, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasPermission(java.lang.String r5, int r6) {
        /*
            r4 = this;
            enforceValidUserId(r6)
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r1 = r4.mPermissions     // Catch:{ all -> 0x0020 }
            r2 = 0
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return r2
        L_0x000d:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r1 = r4.mPermissions     // Catch:{ all -> 0x0020 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0020 }
            com.android.server.pm.permission.PermissionsState$PermissionData r1 = (com.android.server.pm.permission.PermissionsState.PermissionData) r1     // Catch:{ all -> 0x0020 }
            if (r1 == 0) goto L_0x001e
            boolean r3 = r1.isGranted(r6)     // Catch:{ all -> 0x0020 }
            if (r3 == 0) goto L_0x001e
            r2 = 1
        L_0x001e:
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return r2
        L_0x0020:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionsState.hasPermission(java.lang.String, int):boolean");
    }

    public boolean hasRequestedPermission(ArraySet<String> names) {
        synchronized (this.mLock) {
            if (this.mPermissions == null) {
                return false;
            }
            for (int i = names.size() - 1; i >= 0; i--) {
                if (this.mPermissions.get(names.valueAt(i)) != null) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean hasRequestedPermission(String name) {
        ArrayMap<String, PermissionData> arrayMap = this.mPermissions;
        return (arrayMap == null || arrayMap.get(name) == null) ? false : true;
    }

    public Set<String> getPermissions(int userId) {
        enforceValidUserId(userId);
        synchronized (this.mLock) {
            if (this.mPermissions == null) {
                Set<String> emptySet = Collections.emptySet();
                return emptySet;
            }
            Set<String> permissions = new ArraySet<>(this.mPermissions.size());
            int permissionCount = this.mPermissions.size();
            for (int i = 0; i < permissionCount; i++) {
                String permission = this.mPermissions.keyAt(i);
                if (hasInstallPermission(permission)) {
                    permissions.add(permission);
                } else if (userId != -1 && hasRuntimePermission(permission, userId)) {
                    permissions.add(permission);
                }
            }
            return permissions;
        }
    }

    public PermissionState getInstallPermissionState(String name) {
        return getPermissionState(name, -1);
    }

    public PermissionState getRuntimePermissionState(String name, int userId) {
        enforceValidUserId(userId);
        return getPermissionState(name, userId);
    }

    public List<PermissionState> getInstallPermissionStates() {
        return getPermissionStatesInternal(-1);
    }

    public List<PermissionState> getRuntimePermissionStates(int userId) {
        enforceValidUserId(userId);
        return getPermissionStatesInternal(userId);
    }

    public int getPermissionFlags(String name, int userId) {
        PermissionState installPermState = getInstallPermissionState(name);
        if (installPermState != null) {
            return installPermState.getFlags();
        }
        PermissionState runtimePermState = getRuntimePermissionState(name, userId);
        if (runtimePermState != null) {
            return runtimePermState.getFlags();
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001c, code lost:
        r4 = r7.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001f, code lost:
        monitor-enter(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
        r3 = r7.mPermissions.get(r8.getName());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002d, code lost:
        monitor-exit(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
        if (r3 != null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0030, code lost:
        if (r2 != false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0032, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0033, code lost:
        r3 = ensurePermissionData(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0037, code lost:
        r1 = r3.getFlags(r9);
        r4 = r3.updateFlags(r9, r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003f, code lost:
        if (r4 == false) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0041, code lost:
        r5 = r3.getFlags(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0047, code lost:
        if ((r1 & 64) != 0) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x004b, code lost:
        if ((r5 & 64) == 0) goto L_0x005e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x004f, code lost:
        if (r7.mPermissionReviewRequired != null) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0051, code lost:
        r7.mPermissionReviewRequired = new android.util.SparseBooleanArray();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0058, code lost:
        r7.mPermissionReviewRequired.put(r9, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0060, code lost:
        if ((r1 & 64) == 0) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0064, code lost:
        if ((r5 & 64) != 0) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0068, code lost:
        if (r7.mPermissionReviewRequired == null) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x006e, code lost:
        if (hasPermissionRequiringReview(r9) != false) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0070, code lost:
        r7.mPermissionReviewRequired.delete(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x007b, code lost:
        if (r7.mPermissionReviewRequired.size() > 0) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x007d, code lost:
        r7.mPermissionReviewRequired = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0080, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean updatePermissionFlags(com.android.server.pm.permission.BasePermission r8, int r9, int r10, int r11) {
        /*
            r7 = this;
            enforceValidUserId(r9)
            r0 = 1
            r1 = 0
            if (r11 != 0) goto L_0x000c
            if (r10 == 0) goto L_0x000a
            goto L_0x000c
        L_0x000a:
            r2 = r1
            goto L_0x000d
        L_0x000c:
            r2 = r0
        L_0x000d:
            java.lang.Object r3 = r7.mLock
            monitor-enter(r3)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r4 = r7.mPermissions     // Catch:{ all -> 0x0084 }
            if (r4 != 0) goto L_0x001b
            if (r2 != 0) goto L_0x0018
            monitor-exit(r3)     // Catch:{ all -> 0x0084 }
            return r1
        L_0x0018:
            r7.ensurePermissionData(r8)     // Catch:{ all -> 0x0084 }
        L_0x001b:
            monitor-exit(r3)     // Catch:{ all -> 0x0084 }
            r3 = 0
            java.lang.Object r4 = r7.mLock
            monitor-enter(r4)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r5 = r7.mPermissions     // Catch:{ all -> 0x0081 }
            java.lang.String r6 = r8.getName()     // Catch:{ all -> 0x0081 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x0081 }
            com.android.server.pm.permission.PermissionsState$PermissionData r5 = (com.android.server.pm.permission.PermissionsState.PermissionData) r5     // Catch:{ all -> 0x0081 }
            r3 = r5
            monitor-exit(r4)     // Catch:{ all -> 0x0081 }
            if (r3 != 0) goto L_0x0037
            if (r2 != 0) goto L_0x0033
            return r1
        L_0x0033:
            com.android.server.pm.permission.PermissionsState$PermissionData r3 = r7.ensurePermissionData(r8)
        L_0x0037:
            int r1 = r3.getFlags(r9)
            boolean r4 = r3.updateFlags(r9, r10, r11)
            if (r4 == 0) goto L_0x0080
            int r5 = r3.getFlags(r9)
            r6 = r1 & 64
            if (r6 != 0) goto L_0x005e
            r6 = r5 & 64
            if (r6 == 0) goto L_0x005e
            android.util.SparseBooleanArray r6 = r7.mPermissionReviewRequired
            if (r6 != 0) goto L_0x0058
            android.util.SparseBooleanArray r6 = new android.util.SparseBooleanArray
            r6.<init>()
            r7.mPermissionReviewRequired = r6
        L_0x0058:
            android.util.SparseBooleanArray r6 = r7.mPermissionReviewRequired
            r6.put(r9, r0)
            goto L_0x0080
        L_0x005e:
            r0 = r1 & 64
            if (r0 == 0) goto L_0x0080
            r0 = r5 & 64
            if (r0 != 0) goto L_0x0080
            android.util.SparseBooleanArray r0 = r7.mPermissionReviewRequired
            if (r0 == 0) goto L_0x0080
            boolean r0 = r7.hasPermissionRequiringReview(r9)
            if (r0 != 0) goto L_0x0080
            android.util.SparseBooleanArray r0 = r7.mPermissionReviewRequired
            r0.delete(r9)
            android.util.SparseBooleanArray r0 = r7.mPermissionReviewRequired
            int r0 = r0.size()
            if (r0 > 0) goto L_0x0080
            r0 = 0
            r7.mPermissionReviewRequired = r0
        L_0x0080:
            return r4
        L_0x0081:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0081 }
            throw r0
        L_0x0084:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0084 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionsState.updatePermissionFlags(com.android.server.pm.permission.BasePermission, int, int, int):boolean");
    }

    private boolean hasPermissionRequiringReview(int userId) {
        synchronized (this.mLock) {
            int permissionCount = this.mPermissions.size();
            for (int i = 0; i < permissionCount; i++) {
                if ((this.mPermissions.valueAt(i).getFlags(userId) & 64) != 0) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean updatePermissionFlagsForAllPermissions(int userId, int flagMask, int flagValues) {
        enforceValidUserId(userId);
        synchronized (this.mLock) {
            if (this.mPermissions == null) {
                return false;
            }
            boolean changed = false;
            int permissionCount = this.mPermissions.size();
            for (int i = 0; i < permissionCount; i++) {
                changed |= this.mPermissions.valueAt(i).updateFlags(userId, flagMask, flagValues);
            }
            return changed;
        }
    }

    public int[] computeGids(int userId) {
        enforceValidUserId(userId);
        int[] gids = this.mGlobalGids;
        synchronized (this.mLock) {
            if (this.mPermissions != null) {
                int permissionCount = this.mPermissions.size();
                for (int i = 0; i < permissionCount; i++) {
                    if (hasPermission(this.mPermissions.keyAt(i), userId)) {
                        int[] permGids = this.mPermissions.valueAt(i).computeGids(userId);
                        if (permGids != NO_GIDS) {
                            gids = appendInts(gids, permGids);
                        }
                    }
                }
            }
        }
        return gids;
    }

    public int[] computeGids(int[] userIds) {
        int[] gids = this.mGlobalGids;
        for (int userId : userIds) {
            gids = appendInts(gids, computeGids(userId));
        }
        return gids;
    }

    public void reset() {
        this.mGlobalGids = NO_GIDS;
        synchronized (this.mLock) {
            this.mPermissions = null;
        }
        this.mPermissionReviewRequired = null;
    }

    private PermissionState getPermissionState(String name, int userId) {
        synchronized (this.mLock) {
            if (this.mPermissions == null) {
                return null;
            }
            PermissionData permissionData = this.mPermissions.get(name);
            if (permissionData == null) {
                return null;
            }
            PermissionState permissionState = permissionData.getPermissionState(userId);
            return permissionState;
        }
    }

    private List<PermissionState> getPermissionStatesInternal(int userId) {
        enforceValidUserId(userId);
        synchronized (this.mLock) {
            if (this.mPermissions == null) {
                List<PermissionState> emptyList = Collections.emptyList();
                return emptyList;
            }
            List<PermissionState> permissionStates = new ArrayList<>();
            int permissionCount = this.mPermissions.size();
            for (int i = 0; i < permissionCount; i++) {
                PermissionState permissionState = this.mPermissions.valueAt(i).getPermissionState(userId);
                if (permissionState != null) {
                    permissionStates.add(permissionState);
                }
            }
            return permissionStates;
        }
    }

    private int grantPermission(BasePermission permission, int userId) {
        if (hasPermission(permission.getName(), userId)) {
            return 0;
        }
        boolean hasGids = !ArrayUtils.isEmpty(permission.computeGids(userId));
        int[] oldGids = hasGids ? computeGids(userId) : NO_GIDS;
        if (!ensurePermissionData(permission).grant(userId)) {
            return -1;
        }
        if (hasGids) {
            if (oldGids.length != computeGids(userId).length) {
                return 1;
            }
        }
        return 0;
    }

    private int revokePermission(BasePermission permission, int userId) {
        PermissionData permissionData;
        String permName = permission.getName();
        if (!hasPermission(permName, userId)) {
            return 0;
        }
        boolean hasGids = !ArrayUtils.isEmpty(permission.computeGids(userId));
        int[] oldGids = hasGids ? computeGids(userId) : NO_GIDS;
        synchronized (this.mLock) {
            permissionData = this.mPermissions.get(permName);
        }
        if (!permissionData.revoke(userId)) {
            return -1;
        }
        if (permissionData.isDefault()) {
            ensureNoPermissionData(permName);
        }
        if (hasGids) {
            if (oldGids.length != computeGids(userId).length) {
                return 1;
            }
        }
        return 0;
    }

    private static int[] appendInts(int[] current, int[] added) {
        if (!(current == null || added == null)) {
            for (int guid : added) {
                current = ArrayUtils.appendInt(current, guid);
            }
        }
        return current;
    }

    private static void enforceValidUserId(int userId) {
        if (userId != -1 && userId < 0) {
            throw new IllegalArgumentException("Invalid userId:" + userId);
        }
    }

    private PermissionData ensurePermissionData(BasePermission permission) {
        PermissionData permissionData;
        String permName = permission.getName();
        synchronized (this.mLock) {
            if (this.mPermissions == null) {
                this.mPermissions = new ArrayMap<>();
            }
            permissionData = this.mPermissions.get(permName);
            if (permissionData == null) {
                permissionData = new PermissionData(permission);
                this.mPermissions.put(permName, permissionData);
            }
        }
        return permissionData;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void ensureNoPermissionData(java.lang.String r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mLock
            monitor-enter(r0)
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r1 = r2.mPermissions     // Catch:{ all -> 0x001b }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x001b }
            return
        L_0x0009:
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r1 = r2.mPermissions     // Catch:{ all -> 0x001b }
            r1.remove(r3)     // Catch:{ all -> 0x001b }
            android.util.ArrayMap<java.lang.String, com.android.server.pm.permission.PermissionsState$PermissionData> r1 = r2.mPermissions     // Catch:{ all -> 0x001b }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x001b }
            if (r1 == 0) goto L_0x0019
            r1 = 0
            r2.mPermissions = r1     // Catch:{ all -> 0x001b }
        L_0x0019:
            monitor-exit(r0)     // Catch:{ all -> 0x001b }
            return
        L_0x001b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001b }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.permission.PermissionsState.ensureNoPermissionData(java.lang.String):void");
    }

    private static final class PermissionData {
        private final BasePermission mPerm;
        private SparseArray<PermissionState> mUserStates;

        public PermissionData(BasePermission perm) {
            this.mUserStates = new SparseArray<>();
            this.mPerm = perm;
        }

        public PermissionData(PermissionData other) {
            this(other.mPerm);
            int otherStateCount = other.mUserStates.size();
            for (int i = 0; i < otherStateCount; i++) {
                this.mUserStates.put(other.mUserStates.keyAt(i), new PermissionState(other.mUserStates.valueAt(i)));
            }
        }

        public int[] computeGids(int userId) {
            return this.mPerm.computeGids(userId);
        }

        public boolean isGranted(int userId) {
            if (isInstallPermission()) {
                userId = -1;
            }
            PermissionState userState = this.mUserStates.get(userId);
            if (userState == null) {
                return false;
            }
            return userState.mGranted;
        }

        public boolean grant(int userId) {
            if (!isCompatibleUserId(userId) || isGranted(userId)) {
                return false;
            }
            PermissionState userState = this.mUserStates.get(userId);
            if (userState == null) {
                userState = new PermissionState(this.mPerm.getName());
                this.mUserStates.put(userId, userState);
            }
            boolean unused = userState.mGranted = true;
            return true;
        }

        public boolean revoke(int userId) {
            if (!isCompatibleUserId(userId) || !isGranted(userId)) {
                return false;
            }
            PermissionState userState = this.mUserStates.get(userId);
            boolean unused = userState.mGranted = false;
            if (!userState.isDefault()) {
                return true;
            }
            this.mUserStates.remove(userId);
            return true;
        }

        public PermissionState getPermissionState(int userId) {
            return this.mUserStates.get(userId);
        }

        public int getFlags(int userId) {
            PermissionState userState = this.mUserStates.get(userId);
            if (userState != null) {
                return userState.mFlags;
            }
            return 0;
        }

        public boolean isDefault() {
            return this.mUserStates.size() <= 0;
        }

        public static boolean isInstallPermissionKey(int userId) {
            return userId == -1;
        }

        public boolean updateFlags(int userId, int flagMask, int flagValues) {
            if (isInstallPermission()) {
                userId = -1;
            }
            if (!isCompatibleUserId(userId)) {
                return false;
            }
            int newFlags = flagValues & flagMask;
            PermissionState userState = this.mUserStates.get(userId);
            if (userState != null) {
                int oldFlags = userState.mFlags;
                int unused = userState.mFlags = (userState.mFlags & (~flagMask)) | newFlags;
                if (userState.isDefault()) {
                    this.mUserStates.remove(userId);
                }
                if (userState.mFlags != oldFlags) {
                    return true;
                }
                return false;
            } else if (newFlags == 0) {
                return false;
            } else {
                PermissionState userState2 = new PermissionState(this.mPerm.getName());
                int unused2 = userState2.mFlags = newFlags;
                this.mUserStates.put(userId, userState2);
                return true;
            }
        }

        private boolean isCompatibleUserId(int userId) {
            return isDefault() || !(isInstallPermission() ^ isInstallPermissionKey(userId));
        }

        private boolean isInstallPermission() {
            if (this.mUserStates.size() != 1 || this.mUserStates.get(-1) == null) {
                return false;
            }
            return true;
        }
    }

    public static final class PermissionState {
        /* access modifiers changed from: private */
        public int mFlags;
        /* access modifiers changed from: private */
        public boolean mGranted;
        private final String mName;

        public PermissionState(String name) {
            this.mName = name;
        }

        public PermissionState(PermissionState other) {
            this.mName = other.mName;
            this.mGranted = other.mGranted;
            this.mFlags = other.mFlags;
        }

        public boolean isDefault() {
            return !this.mGranted && this.mFlags == 0;
        }

        public String getName() {
            return this.mName;
        }

        public boolean isGranted() {
            return this.mGranted;
        }

        public int getFlags() {
            return this.mFlags;
        }
    }
}
