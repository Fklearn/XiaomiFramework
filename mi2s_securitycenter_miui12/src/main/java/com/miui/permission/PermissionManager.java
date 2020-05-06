package com.miui.permission;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.permission.PermissionContract;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManager {
    public static final int ACTION_ACCEPT = 3;
    public static final int ACTION_BLOCK = 4;
    public static final int ACTION_DEFAULT = 0;
    public static final int ACTION_FOREGROUND = 6;
    public static final int ACTION_NONBLOCK = 5;
    public static final int ACTION_PROMPT = 2;
    public static final int ACTION_REJECT = 1;
    public static final int ACTION_VIRTUAL = 7;
    public static final int FLAG_GRANT_ONTTIME = 4;
    public static final int FLAG_KILL_PROCESS = 2;
    public static final int GET_APP_COUNT = 1;
    public static final int GROUP_CHARGES = 1;
    public static final int GROUP_MEDIA = 4;
    public static final int GROUP_PRIVACY = 2;
    public static final int GROUP_SENSITIVE_PRIVACY = 16;
    public static final int GROUP_SETTINGS = 8;
    public static final long PERM_ID_ACCESS_XIAOMI_ACCOUNT = 4294967296L;
    public static final long PERM_ID_ADD_VOICEMAIL = 281474976710656L;
    public static final long PERM_ID_AUDIO_RECORDER = 131072;
    public static final long PERM_ID_AUTOSTART = 16384;
    public static final long PERM_ID_BACKGROUND_LOCATION = 2305843009213693952L;
    public static final long PERM_ID_BACKGROUND_START_ACTIVITY = 72057594037927936L;
    public static final long PERM_ID_BODY_SENSORS = 70368744177664L;
    public static final long PERM_ID_BOOT_COMPLETED = 134217728;
    public static final long PERM_ID_BT_CONNECTIVITY = 4194304;
    public static final long PERM_ID_CALENDAR = 16777216;
    public static final long PERM_ID_CALLLOG = 16;
    public static final long PERM_ID_CALLMONITOR = 2048;
    public static final long PERM_ID_CALLPHONE = 2;
    public static final long PERM_ID_CALLSTATE = 1024;
    public static final long PERM_ID_CLIPBOARD = 4611686018427387904L;
    public static final long PERM_ID_CONTACT = 8;
    public static final long PERM_ID_DEAMON_NOTIFICATION = 1152921504606846976L;
    public static final long PERM_ID_DISABLE_KEYGUARD = 8388608;
    public static final long PERM_ID_EXTERNAL_STORAGE = 35184372088832L;
    public static final long PERM_ID_GET_ACCOUNTS = 140737488355328L;
    public static final long PERM_ID_GET_INSTALLED_APPS = 144115188075855872L;
    public static final long PERM_ID_GET_TASKS = 18014398509481984L;
    public static final long PERM_ID_INSTALL_PACKAGE = 65536;
    public static final long PERM_ID_INSTALL_SHORTCUT = 4503599627370496L;
    public static final long PERM_ID_LOCATION = 32;
    public static final long PERM_ID_MMSDB = 262144;
    public static final long PERM_ID_MOBILE_CONNECTIVITY = 1048576;
    public static final long PERM_ID_NETDEFAULT = 128;
    public static final long PERM_ID_NETWIFI = 256;
    public static final long PERM_ID_NFC = 2251799813685248L;
    public static final long PERM_ID_NOTIFICATION = 32768;
    public static final long PERM_ID_PHONEINFO = 64;
    public static final long PERM_ID_PROCESS_OUTGOING_CALLS = 1125899906842624L;
    public static final long PERM_ID_READCALLLOG = 1073741824;
    public static final long PERM_ID_READCONTACT = 2147483648L;
    public static final long PERM_ID_READMMS = 536870912;
    public static final long PERM_ID_READSMS = 268435456;
    public static final long PERM_ID_READ_NOTIFICATION_SMS = 9007199254740992L;
    public static final long PERM_ID_REAL_READ_CALENDAR = 4398046511104L;
    public static final long PERM_ID_REAL_READ_CALL_LOG = 8796093022208L;
    public static final long PERM_ID_REAL_READ_CONTACTS = 2199023255552L;
    public static final long PERM_ID_REAL_READ_PHONE_STATE = 17592186044416L;
    public static final long PERM_ID_REAL_READ_SMS = 1099511627776L;
    public static final long PERM_ID_ROOT = 512;
    public static final long PERM_ID_SENDMMS = 524288;
    public static final long PERM_ID_SENDSMS = 1;
    public static final long PERM_ID_SERVICE_FOREGROUND = 288230376151711744L;
    public static final long PERM_ID_SETTINGS = 8192;
    public static final long PERM_ID_SHOW_WHEN_LOCKED = 36028797018963968L;
    public static final long PERM_ID_SMSDB = 4;
    public static final long PERM_ID_SYSTEMALERT = 33554432;
    public static final long PERM_ID_UDEVICEID = 576460752303423488L;
    public static final long PERM_ID_USE_SIP = 562949953421312L;
    public static final long PERM_ID_VIDEO_RECORDER = 4096;
    public static final long PERM_ID_WAKELOCK = 67108864;
    public static final long PERM_ID_WIFI_CONNECTIVITY = 2097152;
    public static final String TAG = "PermissionManager";
    public static final int UPDATE_SOURCE_LBE_ADAPTER_OPS = 16;
    public static final int UPDATE_SOURCE_LBE_PROVIDER_EXCLUDE_FWK_OPS = 64;
    public static final int UPDATE_SOURCE_LBE_PROVIDER_ON_CREATE = 2;
    public static final int UPDATE_SOURCE_LBE_PROVIDER_OPS = 4;
    public static final int UPDATE_SOURCE_LBE_SERVICE_OPS = 8;
    public static final int UPDATE_SOURCE_MIUI_UI_OPS = 32;
    public static final int UPDATE_SOURCE_NATIVE = 1;
    private static int[] sActionToMode = {0, 1, 5, 0, 3, 3, 4, 0};
    private static long sEffectivePermissions = -1;
    private static PermissionManager sInstance;
    public static Map<Long, Long> virtualMap = new HashMap();
    private ContentResolver mContentResolver = this.mContext.getContentResolver();
    private Context mContext;

    static {
        if (Build.VERSION.SDK_INT >= 28 && isSupportVirtualPermControl()) {
            virtualMap.put(Long.valueOf(PERM_ID_READSMS), Long.valueOf(PERM_ID_REAL_READ_SMS));
            virtualMap.put(Long.valueOf(PERM_ID_READCONTACT), Long.valueOf(PERM_ID_REAL_READ_CONTACTS));
            virtualMap.put(Long.valueOf(PERM_ID_CALENDAR), Long.valueOf(PERM_ID_REAL_READ_CALENDAR));
            virtualMap.put(1073741824L, Long.valueOf(PERM_ID_REAL_READ_CALL_LOG));
            virtualMap.put(64L, Long.valueOf(PERM_ID_REAL_READ_PHONE_STATE));
        }
    }

    private PermissionManager(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static int actionToMode(int i) {
        return (Build.VERSION.SDK_INT >= 28 || i != 6) ? sActionToMode[i] : sActionToMode[3];
    }

    public static int calculatePermissionAction(long j, long j2, long j3, long j4, long j5, long j6, long j7) {
        return calculatePermissionAction(j, j2, 0, j3, j4, j5, 0, j6, j7);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0043, code lost:
        if ((r28 & r20) != 0) goto L_0x0028;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0058  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0083  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int calculatePermissionAction(long r20, long r22, long r24, long r26, long r28, long r30, long r32, long r34, long r36) {
        /*
            int r0 = getDefaultAction()
            long r1 = r30 & r20
            r3 = 0
            int r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            r2 = 1
            r5 = 2
            r6 = 6
            r15 = 3
            if (r1 == 0) goto L_0x0012
        L_0x0010:
            r0 = r15
            goto L_0x0046
        L_0x0012:
            long r7 = r32 & r20
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x001a
        L_0x0018:
            r0 = r6
            goto L_0x0046
        L_0x001a:
            long r7 = r34 & r20
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0022
        L_0x0020:
            r0 = r5
            goto L_0x0046
        L_0x0022:
            long r7 = r36 & r20
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x002a
        L_0x0028:
            r0 = r2
            goto L_0x0046
        L_0x002a:
            long r7 = r22 & r20
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0031
            goto L_0x0010
        L_0x0031:
            long r7 = r24 & r20
            int r1 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0038
            goto L_0x0018
        L_0x0038:
            long r6 = r26 & r20
            int r1 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x003f
            goto L_0x0020
        L_0x003f:
            long r5 = r28 & r20
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x0046
            goto L_0x0028
        L_0x0046:
            java.util.Map<java.lang.Long, java.lang.Long> r1 = virtualMap
            java.lang.Long r2 = java.lang.Long.valueOf(r20)
            boolean r1 = r1.containsKey(r2)
            if (r1 == 0) goto L_0x0083
            boolean r1 = isSupportVirtualPermControl()
            if (r1 == 0) goto L_0x0083
            java.util.Map<java.lang.Long, java.lang.Long> r1 = virtualMap
            java.lang.Long r2 = java.lang.Long.valueOf(r20)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Long r1 = (java.lang.Long) r1
            long r1 = r1.longValue()
            r3 = r22
            r5 = r24
            r7 = r26
            r9 = r28
            r11 = r30
            r13 = r32
            r19 = r0
            r0 = r15
            r15 = r34
            r17 = r36
            int r1 = calculatePermissionAction(r1, r3, r5, r7, r9, r11, r13, r15, r17)
            if (r1 == r0) goto L_0x0085
            r0 = 7
            return r0
        L_0x0083:
            r19 = r0
        L_0x0085:
            return r19
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permission.PermissionManager.calculatePermissionAction(long, long, long, long, long, long, long, long, long):int");
    }

    public static final int getDefaultAction() {
        return 1;
    }

    public static final synchronized PermissionManager getInstance(Context context) {
        PermissionManager permissionManager;
        synchronized (PermissionManager.class) {
            if (sInstance == null) {
                sInstance = new PermissionManager(context);
            }
            permissionManager = sInstance;
        }
        return permissionManager;
    }

    public static boolean isExistInMcallAndcontactpermissionlist(Long l) {
        return StaticGroup.mCallandContactPermissionList.contains(l);
    }

    public static boolean isExistInMsmsAndmmspermissionlist(Long l) {
        return StaticGroup.mSMSandMMSPermissionList.contains(l);
    }

    public static boolean isMiuiTwelve() {
        try {
            return Integer.parseInt(SystemProperties.get("ro.miui.ui.version.name", "V0").substring(1)) >= 12;
        } catch (Exception e) {
            Log.e(TAG, "get miuiVersion Exception!", e);
            return false;
        }
    }

    public static boolean isSupportVirtualPermControl() {
        return !miui.os.Build.IS_INTERNATIONAL_BUILD;
    }

    public long calculatePermission(long j, long j2) {
        return j & (j2 ^ j) & getEffectivePermissions();
    }

    public HashMap<Long, Integer> calculatePermissionAction(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8) {
        return calculatePermissionAction(j, j2, 0, j3, j4, j5, j6, 0, j7, j8);
    }

    public HashMap<Long, Integer> calculatePermissionAction(long j, long j2, long j3, long j4, long j5, long j6, long j7, long j8, long j9, long j10) {
        long calculatePermission = calculatePermission(j, j6);
        HashMap<Long, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            long j11 = 1 << i;
            if ((calculatePermission & j11) != 0) {
                hashMap.put(Long.valueOf(j11), Integer.valueOf(calculatePermissionAction(j11, j2, j3, j4, j5, j7, j8, j9, j10)));
            }
        }
        return hashMap;
    }

    public int calculatePermissionCount(long j, long j2) {
        int bitCount = Long.bitCount(calculatePermission(j, j2));
        return (Build.VERSION.SDK_INT < 29 || (32 & j) == 0 || (j & PERM_ID_BACKGROUND_LOCATION) == 0) ? bitCount : bitCount - 1;
    }

    public List<PermissionGroupInfo> getAllPermissionGroups(int i) {
        Bundle call = this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(3), String.valueOf(i), (Bundle) null);
        if (call == null) {
            return Collections.emptyList();
        }
        call.setClassLoader(PermissionManager.class.getClassLoader());
        return call.getParcelableArrayList("extra_data");
    }

    public List<PermissionInfo> getAllPermissions(int i) {
        Bundle call = this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(4), String.valueOf(i), (Bundle) null);
        if (call == null) {
            return Collections.emptyList();
        }
        call.setClassLoader(PermissionManager.class.getClassLoader());
        return call.getParcelableArrayList("extra_data");
    }

    public long getEffectivePermissions() {
        if (sEffectivePermissions == -1) {
            sEffectivePermissions = this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(7), (String) null, (Bundle) null).getLong("extra_data");
        }
        return sEffectivePermissions;
    }

    public PermissionInfo getPermissionForId(long j) {
        Bundle call = this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(12), String.valueOf(j), (Bundle) null);
        if (call == null) {
            return null;
        }
        call.setClassLoader(PermissionManager.class.getClassLoader());
        return (PermissionInfo) call.getParcelable("extra_data");
    }

    public boolean isEnabled() {
        Bundle call = this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(1), (String) null, (Bundle) null);
        return (call == null || (call.getInt("extra_data") & PermissionContract.Method.Flag.FLAG_ENABLED) == 0) ? false : true;
    }

    public void setApplicationPermission(long j, int i, int i2, String... strArr) {
        Bundle bundle = new Bundle();
        bundle.putLong(PermissionContract.Method.SetApplicationPermission.EXTRA_PERMISSION, j);
        bundle.putInt(PermissionContract.Method.SetApplicationPermission.EXTRA_ACTION, i);
        bundle.putStringArray(PermissionContract.Method.SetApplicationPermission.EXTRA_PACKAGE, strArr);
        bundle.putInt("extra_flags", i2);
        this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(6), (String) null, bundle);
    }

    public void setApplicationPermission(long j, int i, String... strArr) {
        setApplicationPermission(j, i, 0, strArr);
    }

    public void setEnabled(boolean z) {
        int i = 0;
        if (z) {
            i = 0 | PermissionContract.Method.Flag.FLAG_ENABLED;
        }
        this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(2), String.valueOf(i), (Bundle) null);
    }

    public void setMode(int i, int i2, String str, int i3, int i4, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putInt(PermissionContract.Method.SetMode.EXTRA_CODE, i);
        bundle.putInt(PermissionContract.Method.SetMode.EXTRA_UID, i2);
        bundle.putString(PermissionContract.Method.SetMode.EXTRA_PACKAGE_NAME, str);
        bundle.putInt(PermissionContract.Method.SetMode.EXTRA_MODE, i3);
        bundle.putInt("extra_flags", i4);
        bundle.putBoolean(PermissionContract.Method.SetMode.EXTRA_SUPPORT_RUNTIME, z);
        try {
            this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(9), (String) null, bundle);
        } catch (Exception e) {
            Log.d(TAG, "setMode error:" + e.toString());
        }
    }

    public void setMode(int i, int i2, String str, int i3, boolean z) {
        setMode(i, i2, str, i3, 0, z);
    }

    public void updateData() {
        this.mContentResolver.call(PermissionContract.CONTENT_URI, String.valueOf(5), (String) null, (Bundle) null);
    }
}
