package com.android.server.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.ParcelableUsbPort;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UsbService extends IUsbManager.Stub {
    private static final String TAG = "UsbService";
    private final UsbAlsaManager mAlsaManager;
    private final Context mContext;
    @GuardedBy({"mLock"})
    private int mCurrentUserId;
    /* access modifiers changed from: private */
    public UsbDeviceManager mDeviceManager;
    private UsbHostManager mHostManager;
    private final Object mLock = new Object();
    private UsbPortManager mPortManager;
    private final UsbSettingsManager mSettingsManager;
    private final UserManager mUserManager;

    public static class Lifecycle extends SystemService {
        private UsbService mUsbService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v1, types: [com.android.server.usb.UsbService, android.os.IBinder] */
        public void onStart() {
            this.mUsbService = new UsbService(getContext());
            publishBinderService("usb", this.mUsbService);
        }

        public void onBootPhase(int phase) {
            if (phase == 550) {
                this.mUsbService.systemReady();
            } else if (phase == 1000) {
                this.mUsbService.bootCompleted();
            }
        }

        public void onSwitchUser(int newUserId) {
            this.mUsbService.onSwitchUser(newUserId);
        }

        public void onStopUser(int userHandle) {
            this.mUsbService.onStopUser(UserHandle.of(userHandle));
        }

        public void onUnlockUser(int userHandle) {
            this.mUsbService.onUnlockUser(userHandle);
        }
    }

    private UsbUserSettingsManager getSettingsForUser(int userIdInt) {
        return this.mSettingsManager.getSettingsForUser(userIdInt);
    }

    public UsbService(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mSettingsManager = new UsbSettingsManager(context);
        this.mAlsaManager = new UsbAlsaManager(context);
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.usb.host")) {
            this.mHostManager = new UsbHostManager(context, this.mAlsaManager, this.mSettingsManager);
        }
        if (new File("/sys/class/android_usb").exists()) {
            this.mDeviceManager = new UsbDeviceManager(context, this.mAlsaManager, this.mSettingsManager);
        }
        if (!(this.mHostManager == null && this.mDeviceManager == null)) {
            this.mPortManager = new UsbPortManager(context);
        }
        onSwitchUser(0);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(intent.getAction()) && UsbService.this.mDeviceManager != null) {
                    UsbService.this.mDeviceManager.updateUserRestrictions();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
        this.mContext.registerReceiver(receiver, filter, (String) null, (Handler) null);
    }

    /* access modifiers changed from: private */
    public void onSwitchUser(int newUserId) {
        synchronized (this.mLock) {
            this.mCurrentUserId = newUserId;
            UsbProfileGroupSettingsManager settings = this.mSettingsManager.getSettingsForProfileGroup(UserHandle.of(newUserId));
            if (this.mHostManager != null) {
                this.mHostManager.setCurrentUserSettings(settings);
            }
            if (this.mDeviceManager != null) {
                this.mDeviceManager.setCurrentUser(newUserId, settings);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onStopUser(UserHandle stoppedUser) {
        this.mSettingsManager.remove(stoppedUser);
    }

    public void systemReady() {
        this.mAlsaManager.systemReady();
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            usbDeviceManager.systemReady();
        }
        UsbHostManager usbHostManager = this.mHostManager;
        if (usbHostManager != null) {
            usbHostManager.systemReady();
        }
        UsbPortManager usbPortManager = this.mPortManager;
        if (usbPortManager != null) {
            usbPortManager.systemReady();
        }
    }

    public void bootCompleted() {
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            usbDeviceManager.bootCompleted();
        }
    }

    public void onUnlockUser(int user) {
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            usbDeviceManager.onUnlockUser(user);
        }
    }

    public void getDeviceList(Bundle devices) {
        UsbHostManager usbHostManager = this.mHostManager;
        if (usbHostManager != null) {
            usbHostManager.getDeviceList(devices);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public ParcelFileDescriptor openDevice(String deviceName, String packageName) {
        ParcelFileDescriptor fd = null;
        if (!(this.mHostManager == null || deviceName == null)) {
            int uid = Binder.getCallingUid();
            int user = UserHandle.getUserId(uid);
            long ident = clearCallingIdentity();
            try {
                synchronized (this.mLock) {
                    if (this.mUserManager.isSameProfileGroup(user, this.mCurrentUserId)) {
                        fd = this.mHostManager.openDevice(deviceName, getSettingsForUser(user), packageName, uid);
                    } else {
                        Slog.w(TAG, "Cannot open " + deviceName + " for user " + user + " as user is not active.");
                    }
                }
                restoreCallingIdentity(ident);
            } catch (Throwable th) {
                restoreCallingIdentity(ident);
                throw th;
            }
        }
        return fd;
    }

    public UsbAccessory getCurrentAccessory() {
        UsbDeviceManager usbDeviceManager = this.mDeviceManager;
        if (usbDeviceManager != null) {
            return usbDeviceManager.getCurrentAccessory();
        }
        return null;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public ParcelFileDescriptor openAccessory(UsbAccessory accessory) {
        if (this.mDeviceManager == null) {
            return null;
        }
        int uid = Binder.getCallingUid();
        int user = UserHandle.getUserId(uid);
        long ident = clearCallingIdentity();
        try {
            synchronized (this.mLock) {
                if (this.mUserManager.isSameProfileGroup(user, this.mCurrentUserId)) {
                    ParcelFileDescriptor openAccessory = this.mDeviceManager.openAccessory(accessory, getSettingsForUser(user), uid);
                    restoreCallingIdentity(ident);
                    return openAccessory;
                }
                Slog.w(TAG, "Cannot open " + accessory + " for user " + user + " as user is not active.");
                restoreCallingIdentity(ident);
                return null;
            }
        } catch (Throwable th) {
            restoreCallingIdentity(ident);
            throw th;
        }
    }

    public ParcelFileDescriptor getControlFd(long function) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_MTP", (String) null);
        return this.mDeviceManager.getControlFd(function);
    }

    public void setDevicePackage(UsbDevice device, String packageName, int userId) {
        UsbDevice device2 = (UsbDevice) Preconditions.checkNotNull(device);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        UserHandle user = UserHandle.of(userId);
        long token = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(user).setDevicePackage(device2, packageName, user);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void setAccessoryPackage(UsbAccessory accessory, String packageName, int userId) {
        UsbAccessory accessory2 = (UsbAccessory) Preconditions.checkNotNull(accessory);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        UserHandle user = UserHandle.of(userId);
        long token = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(user).setAccessoryPackage(accessory2, packageName, user);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public boolean hasDevicePermission(UsbDevice device, String packageName) {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        long token = Binder.clearCallingIdentity();
        try {
            return getSettingsForUser(userId).hasPermission(device, packageName, uid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public boolean hasAccessoryPermission(UsbAccessory accessory) {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        long token = Binder.clearCallingIdentity();
        try {
            return getSettingsForUser(userId).hasPermission(accessory, uid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void requestDevicePermission(UsbDevice device, String packageName, PendingIntent pi) {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        long token = Binder.clearCallingIdentity();
        try {
            getSettingsForUser(userId).requestPermission(device, packageName, pi, uid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void requestAccessoryPermission(UsbAccessory accessory, String packageName, PendingIntent pi) {
        int uid = Binder.getCallingUid();
        int userId = UserHandle.getUserId(uid);
        long token = Binder.clearCallingIdentity();
        try {
            getSettingsForUser(userId).requestPermission(accessory, packageName, pi, uid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void grantDevicePermission(UsbDevice device, int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        int userId = UserHandle.getUserId(uid);
        long token = Binder.clearCallingIdentity();
        try {
            getSettingsForUser(userId).grantDevicePermission(device, uid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void grantAccessoryPermission(UsbAccessory accessory, int uid) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        int userId = UserHandle.getUserId(uid);
        long token = Binder.clearCallingIdentity();
        try {
            getSettingsForUser(userId).grantAccessoryPermission(accessory, uid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public boolean hasDefaults(String packageName, int userId) {
        String packageName2 = (String) Preconditions.checkStringNotEmpty(packageName);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        UserHandle user = UserHandle.of(userId);
        long token = Binder.clearCallingIdentity();
        try {
            return this.mSettingsManager.getSettingsForProfileGroup(user).hasDefaults(packageName2, user);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void clearDefaults(String packageName, int userId) {
        String packageName2 = (String) Preconditions.checkStringNotEmpty(packageName);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        UserHandle user = UserHandle.of(userId);
        long token = Binder.clearCallingIdentity();
        try {
            this.mSettingsManager.getSettingsForProfileGroup(user).clearDefaults(packageName2, user);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void setCurrentFunctions(long functions) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        Preconditions.checkArgument(UsbManager.areSettableFunctions(functions));
        Preconditions.checkState(this.mDeviceManager != null);
        this.mDeviceManager.setCurrentFunctions(functions);
    }

    public void setCurrentFunction(String functions, boolean usbDataUnlocked) {
        setCurrentFunctions(UsbManager.usbFunctionsFromString(functions));
    }

    public boolean isFunctionEnabled(String function) {
        return (getCurrentFunctions() & UsbManager.usbFunctionsFromString(function)) != 0;
    }

    public long getCurrentFunctions() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        Preconditions.checkState(this.mDeviceManager != null);
        return this.mDeviceManager.getCurrentFunctions();
    }

    public void setScreenUnlockedFunctions(long functions) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        Preconditions.checkArgument(UsbManager.areSettableFunctions(functions));
        Preconditions.checkState(this.mDeviceManager != null);
        this.mDeviceManager.setScreenUnlockedFunctions(functions);
    }

    public long getScreenUnlockedFunctions() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        Preconditions.checkState(this.mDeviceManager != null);
        return this.mDeviceManager.getScreenUnlockedFunctions();
    }

    public List<ParcelableUsbPort> getPorts() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mPortManager == null) {
                return null;
            }
            ArrayList<ParcelableUsbPort> parcelablePorts = new ArrayList<>();
            for (UsbPort of : this.mPortManager.getPorts()) {
                parcelablePorts.add(ParcelableUsbPort.of(of));
            }
            Binder.restoreCallingIdentity(ident);
            return parcelablePorts;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public UsbPortStatus getPortStatus(String portId) {
        Preconditions.checkNotNull(portId, "portId must not be null");
        UsbPortStatus usbPortStatus = null;
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mPortManager != null) {
                usbPortStatus = this.mPortManager.getPortStatus(portId);
            }
            return usbPortStatus;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public void setPortRoles(String portId, int powerRole, int dataRole) {
        Preconditions.checkNotNull(portId, "portId must not be null");
        UsbPort.checkRoles(powerRole, dataRole);
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mPortManager != null) {
                this.mPortManager.setPortRoles(portId, powerRole, dataRole, (IndentingPrintWriter) null);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public void enableContaminantDetection(String portId, boolean enable) {
        Preconditions.checkNotNull(portId, "portId must not be null");
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mPortManager != null) {
                this.mPortManager.enableContaminantDetection(portId, enable, (IndentingPrintWriter) null);
            }
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void setUsbDeviceConnectionHandler(ComponentName usbDeviceConnectionHandler) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_USB", (String) null);
        synchronized (this.mLock) {
            if (this.mCurrentUserId != UserHandle.getCallingUserId()) {
                throw new IllegalArgumentException("Only the current user can register a usb connection handler");
            } else if (this.mHostManager != null) {
                this.mHostManager.setUsbDeviceConnectionHandler(usbDeviceConnectionHandler);
            }
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0220  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x024b  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0254  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0271 A[SYNTHETIC, Splitter:B:179:0x0271] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x027c  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x02be  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x02cf  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x02dc  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0304 A[Catch:{ all -> 0x0337 }] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0330  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x052f A[Catch:{ all -> 0x0525, all -> 0x053c }] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x053e A[Catch:{ all -> 0x05a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x0553 A[Catch:{ all -> 0x05a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0563 A[Catch:{ all -> 0x05a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0574 A[Catch:{ all -> 0x05a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a0 A[Catch:{ all -> 0x05a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00cf A[Catch:{ all -> 0x05a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00f0 A[Catch:{ all -> 0x05a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00fd A[Catch:{ all -> 0x05a5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x011f  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0125 A[Catch:{ all -> 0x05a5 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:76:0x0121=Splitter:B:76:0x0121, B:115:0x01b3=Splitter:B:115:0x01b3, B:49:0x00c4=Splitter:B:49:0x00c4} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.io.FileDescriptor r26, java.io.PrintWriter r27, java.lang.String[] r28) {
        /*
            r25 = this;
            r1 = r25
            r2 = r27
            r3 = r28
            java.lang.String r0 = "  dumpsys usb add-port \"matrix\" dual"
            java.lang.String r4 = "?"
            java.lang.String r5 = "  dumpsys usb reset"
            android.content.Context r6 = r1.mContext
            java.lang.String r7 = "UsbService"
            boolean r6 = com.android.internal.util.DumpUtils.checkDumpPermission(r6, r7, r2)
            if (r6 != 0) goto L_0x0017
            return
        L_0x0017:
            com.android.internal.util.IndentingPrintWriter r6 = new com.android.internal.util.IndentingPrintWriter
            java.lang.String r7 = "  "
            r6.<init>(r2, r7)
            long r17 = android.os.Binder.clearCallingIdentity()
            android.util.ArraySet r8 = new android.util.ArraySet     // Catch:{ all -> 0x05a5 }
            r8.<init>()     // Catch:{ all -> 0x05a5 }
            r15 = r8
            java.util.Collections.addAll(r15, r3)     // Catch:{ all -> 0x05a5 }
            r8 = 0
            java.lang.String r9 = "--proto"
            boolean r9 = r15.contains(r9)     // Catch:{ all -> 0x05a5 }
            if (r9 == 0) goto L_0x0038
            r8 = 1
            r19 = r8
            goto L_0x003a
        L_0x0038:
            r19 = r8
        L_0x003a:
            if (r3 == 0) goto L_0x052b
            int r8 = r3.length     // Catch:{ all -> 0x0525 }
            if (r8 == 0) goto L_0x052b
            r8 = 0
            r9 = r3[r8]     // Catch:{ all -> 0x0525 }
            java.lang.String r10 = "-a"
            boolean r9 = r9.equals(r10)     // Catch:{ all -> 0x0525 }
            if (r9 != 0) goto L_0x052b
            if (r19 == 0) goto L_0x0050
            r8 = r3
            r3 = r15
            goto L_0x052d
        L_0x0050:
            java.lang.String r9 = "set-port-roles"
            r10 = r3[r8]     // Catch:{ all -> 0x0525 }
            boolean r9 = r9.equals(r10)     // Catch:{ all -> 0x0525 }
            java.lang.String r10 = "source"
            r13 = 4
            java.lang.String r8 = ""
            r14 = 3
            r11 = 2
            r12 = 1
            if (r9 == 0) goto L_0x0143
            int r9 = r3.length     // Catch:{ all -> 0x05a5 }
            if (r9 != r13) goto L_0x0143
            r0 = r3[r12]     // Catch:{ all -> 0x05a5 }
            r4 = r3[r11]     // Catch:{ all -> 0x05a5 }
            int r5 = r4.hashCode()     // Catch:{ all -> 0x05a5 }
            r9 = -896505829(0xffffffffca90681b, float:-4731917.5)
            if (r5 == r9) goto L_0x0095
            r9 = -440560135(0xffffffffe5bd95f9, float:-1.1191172E23)
            if (r5 == r9) goto L_0x008a
            r9 = 3530387(0x35de93, float:4.947126E-39)
            if (r5 == r9) goto L_0x007f
        L_0x007e:
            goto L_0x009d
        L_0x007f:
            java.lang.String r5 = "sink"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x007e
            r4 = r12
            goto L_0x009e
        L_0x008a:
            java.lang.String r5 = "no-power"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x007e
            r4 = r11
            goto L_0x009e
        L_0x0095:
            boolean r4 = r4.equals(r10)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x007e
            r4 = 0
            goto L_0x009e
        L_0x009d:
            r4 = -1
        L_0x009e:
            if (r4 == 0) goto L_0x00c2
            if (r4 == r12) goto L_0x00c0
            if (r4 == r11) goto L_0x00be
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a5 }
            r4.<init>()     // Catch:{ all -> 0x05a5 }
            java.lang.String r5 = "Invalid power role: "
            r4.append(r5)     // Catch:{ all -> 0x05a5 }
            r5 = r3[r11]     // Catch:{ all -> 0x05a5 }
            r4.append(r5)     // Catch:{ all -> 0x05a5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x05a5 }
            r6.println(r4)     // Catch:{ all -> 0x05a5 }
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x00be:
            r4 = 0
            goto L_0x00c4
        L_0x00c0:
            r4 = 2
            goto L_0x00c4
        L_0x00c2:
            r4 = 1
        L_0x00c4:
            r5 = r3[r14]     // Catch:{ all -> 0x05a5 }
            int r9 = r5.hashCode()     // Catch:{ all -> 0x05a5 }
            r10 = -1335157162(0xffffffffb06b1e56, float:-8.553561E-10)
            if (r9 == r10) goto L_0x00f0
            r10 = 3208616(0x30f5a8, float:4.496229E-39)
            if (r9 == r10) goto L_0x00e5
            r10 = 2063627318(0x7b007436, float:6.66971E35)
            if (r9 == r10) goto L_0x00da
        L_0x00d9:
            goto L_0x00fa
        L_0x00da:
            java.lang.String r9 = "no-data"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x05a5 }
            if (r5 == 0) goto L_0x00d9
            r5 = r11
            goto L_0x00fb
        L_0x00e5:
            java.lang.String r9 = "host"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x05a5 }
            if (r5 == 0) goto L_0x00d9
            r5 = 0
            goto L_0x00fb
        L_0x00f0:
            java.lang.String r9 = "device"
            boolean r5 = r5.equals(r9)     // Catch:{ all -> 0x05a5 }
            if (r5 == 0) goto L_0x00d9
            r5 = r12
            goto L_0x00fb
        L_0x00fa:
            r5 = -1
        L_0x00fb:
            if (r5 == 0) goto L_0x011f
            if (r5 == r12) goto L_0x011d
            if (r5 == r11) goto L_0x011b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a5 }
            r5.<init>()     // Catch:{ all -> 0x05a5 }
            java.lang.String r7 = "Invalid data role: "
            r5.append(r7)     // Catch:{ all -> 0x05a5 }
            r7 = r3[r14]     // Catch:{ all -> 0x05a5 }
            r5.append(r7)     // Catch:{ all -> 0x05a5 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x05a5 }
            r6.println(r5)     // Catch:{ all -> 0x05a5 }
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x011b:
            r5 = 0
            goto L_0x0121
        L_0x011d:
            r5 = 2
            goto L_0x0121
        L_0x011f:
            r5 = 1
        L_0x0121:
            com.android.server.usb.UsbPortManager r9 = r1.mPortManager     // Catch:{ all -> 0x05a5 }
            if (r9 == 0) goto L_0x013e
            com.android.server.usb.UsbPortManager r9 = r1.mPortManager     // Catch:{ all -> 0x05a5 }
            r9.setPortRoles(r0, r4, r5, r6)     // Catch:{ all -> 0x05a5 }
            r6.println()     // Catch:{ all -> 0x05a5 }
            com.android.server.usb.UsbPortManager r9 = r1.mPortManager     // Catch:{ all -> 0x05a5 }
            com.android.internal.util.dump.DualDumpOutputStream r10 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x05a5 }
            com.android.internal.util.IndentingPrintWriter r11 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x05a5 }
            r11.<init>(r6, r7)     // Catch:{ all -> 0x05a5 }
            r10.<init>(r11)     // Catch:{ all -> 0x05a5 }
            r11 = 0
            r9.dump(r10, r8, r11)     // Catch:{ all -> 0x05a5 }
        L_0x013e:
            r4 = r26
            r8 = r3
            goto L_0x059e
        L_0x0143:
            java.lang.String r9 = "add-port"
            r21 = 0
            r13 = r3[r21]     // Catch:{ all -> 0x0525 }
            boolean r9 = r9.equals(r13)     // Catch:{ all -> 0x0525 }
            if (r9 == 0) goto L_0x01d5
            int r9 = r3.length     // Catch:{ all -> 0x05a5 }
            if (r9 != r14) goto L_0x01d5
            r0 = r3[r12]     // Catch:{ all -> 0x05a5 }
            r4 = r3[r11]     // Catch:{ all -> 0x05a5 }
            int r5 = r4.hashCode()     // Catch:{ all -> 0x05a5 }
            switch(r5) {
                case 99374: goto L_0x017e;
                case 115711: goto L_0x0173;
                case 3094652: goto L_0x0169;
                case 3387192: goto L_0x015e;
                default: goto L_0x015d;
            }     // Catch:{ all -> 0x05a5 }
        L_0x015d:
            goto L_0x0188
        L_0x015e:
            java.lang.String r5 = "none"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x015d
            r4 = r14
            goto L_0x0189
        L_0x0169:
            java.lang.String r5 = "dual"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x015d
            r4 = r11
            goto L_0x0189
        L_0x0173:
            java.lang.String r5 = "ufp"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x015d
            r4 = 0
            goto L_0x0189
        L_0x017e:
            java.lang.String r5 = "dfp"
            boolean r4 = r4.equals(r5)     // Catch:{ all -> 0x05a5 }
            if (r4 == 0) goto L_0x015d
            r4 = r12
            goto L_0x0189
        L_0x0188:
            r4 = -1
        L_0x0189:
            if (r4 == 0) goto L_0x01b1
            if (r4 == r12) goto L_0x01af
            if (r4 == r11) goto L_0x01ad
            if (r4 == r14) goto L_0x01ab
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a5 }
            r4.<init>()     // Catch:{ all -> 0x05a5 }
            java.lang.String r5 = "Invalid mode: "
            r4.append(r5)     // Catch:{ all -> 0x05a5 }
            r5 = r3[r11]     // Catch:{ all -> 0x05a5 }
            r4.append(r5)     // Catch:{ all -> 0x05a5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x05a5 }
            r6.println(r4)     // Catch:{ all -> 0x05a5 }
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x01ab:
            r4 = 0
            goto L_0x01b3
        L_0x01ad:
            r4 = 3
            goto L_0x01b3
        L_0x01af:
            r4 = 2
            goto L_0x01b3
        L_0x01b1:
            r4 = 1
        L_0x01b3:
            com.android.server.usb.UsbPortManager r5 = r1.mPortManager     // Catch:{ all -> 0x05a5 }
            if (r5 == 0) goto L_0x01d0
            com.android.server.usb.UsbPortManager r5 = r1.mPortManager     // Catch:{ all -> 0x05a5 }
            r5.addSimulatedPort(r0, r4, r6)     // Catch:{ all -> 0x05a5 }
            r6.println()     // Catch:{ all -> 0x05a5 }
            com.android.server.usb.UsbPortManager r5 = r1.mPortManager     // Catch:{ all -> 0x05a5 }
            com.android.internal.util.dump.DualDumpOutputStream r9 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x05a5 }
            com.android.internal.util.IndentingPrintWriter r10 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x05a5 }
            r10.<init>(r6, r7)     // Catch:{ all -> 0x05a5 }
            r9.<init>(r10)     // Catch:{ all -> 0x05a5 }
            r10 = 0
            r5.dump(r9, r8, r10)     // Catch:{ all -> 0x05a5 }
        L_0x01d0:
            r4 = r26
            r8 = r3
            goto L_0x059e
        L_0x01d5:
            r22 = 0
            java.lang.String r9 = "connect-port"
            r13 = 0
            r14 = r3[r13]     // Catch:{ all -> 0x0525 }
            boolean r9 = r9.equals(r14)     // Catch:{ all -> 0x0525 }
            if (r9 == 0) goto L_0x033e
            int r9 = r3.length     // Catch:{ all -> 0x0337 }
            r13 = 5
            if (r9 != r13) goto L_0x033e
            r9 = r3[r12]     // Catch:{ all -> 0x0337 }
            r0 = r3[r11]     // Catch:{ all -> 0x0337 }
            boolean r0 = r0.endsWith(r4)     // Catch:{ all -> 0x0337 }
            if (r0 == 0) goto L_0x01f7
            r5 = r3[r11]     // Catch:{ all -> 0x05a5 }
            java.lang.String r5 = removeLastChar(r5)     // Catch:{ all -> 0x05a5 }
            goto L_0x01f9
        L_0x01f7:
            r5 = r3[r11]     // Catch:{ all -> 0x0337 }
        L_0x01f9:
            int r13 = r5.hashCode()     // Catch:{ all -> 0x0337 }
            r14 = 99374(0x1842e, float:1.39253E-40)
            if (r13 == r14) goto L_0x0213
            r14 = 115711(0x1c3ff, float:1.62146E-40)
            if (r13 == r14) goto L_0x0208
        L_0x0207:
            goto L_0x021d
        L_0x0208:
            java.lang.String r13 = "ufp"
            boolean r5 = r5.equals(r13)     // Catch:{ all -> 0x05a5 }
            if (r5 == 0) goto L_0x0207
            r5 = 0
            goto L_0x021e
        L_0x0213:
            java.lang.String r13 = "dfp"
            boolean r5 = r5.equals(r13)     // Catch:{ all -> 0x0337 }
            if (r5 == 0) goto L_0x0207
            r5 = r12
            goto L_0x021e
        L_0x021d:
            r5 = -1
        L_0x021e:
            if (r5 == 0) goto L_0x023e
            if (r5 == r12) goto L_0x023c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a5 }
            r4.<init>()     // Catch:{ all -> 0x05a5 }
            java.lang.String r5 = "Invalid mode: "
            r4.append(r5)     // Catch:{ all -> 0x05a5 }
            r5 = r3[r11]     // Catch:{ all -> 0x05a5 }
            r4.append(r5)     // Catch:{ all -> 0x05a5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x05a5 }
            r6.println(r4)     // Catch:{ all -> 0x05a5 }
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x023c:
            r5 = 2
            goto L_0x0240
        L_0x023e:
            r5 = 1
        L_0x0240:
            r11 = 3
            r13 = r3[r11]     // Catch:{ all -> 0x0337 }
            boolean r11 = r13.endsWith(r4)     // Catch:{ all -> 0x0337 }
            r24 = r11
            if (r24 == 0) goto L_0x0254
            r11 = 3
            r13 = r3[r11]     // Catch:{ all -> 0x05a5 }
            java.lang.String r11 = removeLastChar(r13)     // Catch:{ all -> 0x05a5 }
            r13 = r11
            goto L_0x0257
        L_0x0254:
            r11 = 3
            r13 = r3[r11]     // Catch:{ all -> 0x0337 }
        L_0x0257:
            int r11 = r13.hashCode()     // Catch:{ all -> 0x0337 }
            r14 = -896505829(0xffffffffca90681b, float:-4731917.5)
            if (r11 == r14) goto L_0x0271
            r14 = 3530387(0x35de93, float:4.947126E-39)
            if (r11 == r14) goto L_0x0266
        L_0x0265:
            goto L_0x0279
        L_0x0266:
            java.lang.String r10 = "sink"
            boolean r10 = r13.equals(r10)     // Catch:{ all -> 0x05a5 }
            if (r10 == 0) goto L_0x0265
            r10 = r12
            goto L_0x027a
        L_0x0271:
            boolean r10 = r13.equals(r10)     // Catch:{ all -> 0x0337 }
            if (r10 == 0) goto L_0x0265
            r10 = 0
            goto L_0x027a
        L_0x0279:
            r10 = -1
        L_0x027a:
            if (r10 == 0) goto L_0x029d
            if (r10 == r12) goto L_0x0299
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a5 }
            r4.<init>()     // Catch:{ all -> 0x05a5 }
            java.lang.String r7 = "Invalid power role: "
            r4.append(r7)     // Catch:{ all -> 0x05a5 }
            r7 = 3
            r7 = r3[r7]     // Catch:{ all -> 0x05a5 }
            r4.append(r7)     // Catch:{ all -> 0x05a5 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x05a5 }
            r6.println(r4)     // Catch:{ all -> 0x05a5 }
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x0299:
            r10 = 2
            r20 = r10
            goto L_0x02a0
        L_0x029d:
            r10 = 1
            r20 = r10
        L_0x02a0:
            r10 = 4
            r11 = r3[r10]     // Catch:{ all -> 0x0337 }
            boolean r4 = r11.endsWith(r4)     // Catch:{ all -> 0x0337 }
            if (r4 == 0) goto L_0x02b1
            r10 = 4
            r11 = r3[r10]     // Catch:{ all -> 0x05a5 }
            java.lang.String r10 = removeLastChar(r11)     // Catch:{ all -> 0x05a5 }
            goto L_0x02b5
        L_0x02b1:
            r10 = 4
            r11 = r3[r10]     // Catch:{ all -> 0x0337 }
            r10 = r11
        L_0x02b5:
            int r11 = r10.hashCode()     // Catch:{ all -> 0x0337 }
            r13 = -1335157162(0xffffffffb06b1e56, float:-8.553561E-10)
            if (r11 == r13) goto L_0x02cf
            r13 = 3208616(0x30f5a8, float:4.496229E-39)
            if (r11 == r13) goto L_0x02c4
        L_0x02c3:
            goto L_0x02d9
        L_0x02c4:
            java.lang.String r11 = "host"
            boolean r10 = r10.equals(r11)     // Catch:{ all -> 0x05a5 }
            if (r10 == 0) goto L_0x02c3
            r10 = 0
            goto L_0x02da
        L_0x02cf:
            java.lang.String r11 = "device"
            boolean r10 = r10.equals(r11)     // Catch:{ all -> 0x0337 }
            if (r10 == 0) goto L_0x02c3
            r10 = r12
            goto L_0x02da
        L_0x02d9:
            r10 = -1
        L_0x02da:
            if (r10 == 0) goto L_0x02fd
            if (r10 == r12) goto L_0x02f9
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x05a5 }
            r7.<init>()     // Catch:{ all -> 0x05a5 }
            java.lang.String r8 = "Invalid data role: "
            r7.append(r8)     // Catch:{ all -> 0x05a5 }
            r8 = 4
            r8 = r3[r8]     // Catch:{ all -> 0x05a5 }
            r7.append(r8)     // Catch:{ all -> 0x05a5 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x05a5 }
            r6.println(r7)     // Catch:{ all -> 0x05a5 }
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x02f9:
            r10 = 2
            r21 = r10
            goto L_0x0300
        L_0x02fd:
            r10 = 1
            r21 = r10
        L_0x0300:
            com.android.server.usb.UsbPortManager r10 = r1.mPortManager     // Catch:{ all -> 0x0337 }
            if (r10 == 0) goto L_0x0330
            com.android.server.usb.UsbPortManager r10 = r1.mPortManager     // Catch:{ all -> 0x0337 }
            r14 = r8
            r8 = r10
            r10 = r5
            r12 = r22
            r11 = r0
            r2 = r12
            r12 = r20
            r13 = r24
            r2 = r14
            r14 = r21
            r3 = r15
            r15 = r4
            r16 = r6
            r8.connectSimulatedPort(r9, r10, r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0337 }
            r6.println()     // Catch:{ all -> 0x0337 }
            com.android.server.usb.UsbPortManager r8 = r1.mPortManager     // Catch:{ all -> 0x0337 }
            com.android.internal.util.dump.DualDumpOutputStream r10 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x0337 }
            com.android.internal.util.IndentingPrintWriter r11 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x0337 }
            r11.<init>(r6, r7)     // Catch:{ all -> 0x0337 }
            r10.<init>(r11)     // Catch:{ all -> 0x0337 }
            r13 = 0
            r8.dump(r10, r2, r13)     // Catch:{ all -> 0x0337 }
            goto L_0x0331
        L_0x0330:
            r3 = r15
        L_0x0331:
            r4 = r26
            r8 = r28
            goto L_0x059e
        L_0x0337:
            r0 = move-exception
            r4 = r26
            r8 = r28
            goto L_0x05a9
        L_0x033e:
            r2 = r8
            r3 = r15
            r13 = r22
            java.lang.String r4 = "disconnect-port"
            r8 = r28
            r9 = r13
            r13 = 0
            r14 = r8[r13]     // Catch:{ all -> 0x053c }
            boolean r4 = r4.equals(r14)     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x0374
            int r4 = r8.length     // Catch:{ all -> 0x053c }
            if (r4 != r11) goto L_0x0374
            r0 = r8[r12]     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r4 = r1.mPortManager     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x0370
            com.android.server.usb.UsbPortManager r4 = r1.mPortManager     // Catch:{ all -> 0x053c }
            r4.disconnectSimulatedPort(r0, r6)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r4 = r1.mPortManager     // Catch:{ all -> 0x053c }
            com.android.internal.util.dump.DualDumpOutputStream r5 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x053c }
            com.android.internal.util.IndentingPrintWriter r11 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x053c }
            r11.<init>(r6, r7)     // Catch:{ all -> 0x053c }
            r5.<init>(r11)     // Catch:{ all -> 0x053c }
            r4.dump(r5, r2, r9)     // Catch:{ all -> 0x053c }
        L_0x0370:
            r4 = r26
            goto L_0x059e
        L_0x0374:
            java.lang.String r4 = "remove-port"
            r13 = 0
            r14 = r8[r13]     // Catch:{ all -> 0x053c }
            boolean r4 = r4.equals(r14)     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x03a4
            int r4 = r8.length     // Catch:{ all -> 0x053c }
            if (r4 != r11) goto L_0x03a4
            r0 = r8[r12]     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r4 = r1.mPortManager     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x03a0
            com.android.server.usb.UsbPortManager r4 = r1.mPortManager     // Catch:{ all -> 0x053c }
            r4.removeSimulatedPort(r0, r6)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r4 = r1.mPortManager     // Catch:{ all -> 0x053c }
            com.android.internal.util.dump.DualDumpOutputStream r5 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x053c }
            com.android.internal.util.IndentingPrintWriter r11 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x053c }
            r11.<init>(r6, r7)     // Catch:{ all -> 0x053c }
            r5.<init>(r11)     // Catch:{ all -> 0x053c }
            r4.dump(r5, r2, r9)     // Catch:{ all -> 0x053c }
        L_0x03a0:
            r4 = r26
            goto L_0x059e
        L_0x03a4:
            java.lang.String r4 = "reset"
            r13 = 0
            r14 = r8[r13]     // Catch:{ all -> 0x053c }
            boolean r4 = r4.equals(r14)     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x03d6
            int r4 = r8.length     // Catch:{ all -> 0x053c }
            if (r4 != r12) goto L_0x03d6
            com.android.server.usb.UsbPortManager r0 = r1.mPortManager     // Catch:{ all -> 0x053c }
            if (r0 == 0) goto L_0x03d2
            com.android.server.usb.UsbPortManager r0 = r1.mPortManager     // Catch:{ all -> 0x053c }
            r0.resetSimulation(r6)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r0 = r1.mPortManager     // Catch:{ all -> 0x053c }
            com.android.internal.util.dump.DualDumpOutputStream r4 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x053c }
            com.android.internal.util.IndentingPrintWriter r5 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x053c }
            r5.<init>(r6, r7)     // Catch:{ all -> 0x053c }
            r4.<init>(r5)     // Catch:{ all -> 0x053c }
            r0.dump(r4, r2, r9)     // Catch:{ all -> 0x053c }
            r4 = r26
            goto L_0x059e
        L_0x03d2:
            r4 = r26
            goto L_0x059e
        L_0x03d6:
            java.lang.String r4 = "set-contaminant-status"
            r13 = 0
            r14 = r8[r13]     // Catch:{ all -> 0x053c }
            boolean r4 = r4.equals(r14)     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x0415
            int r4 = r8.length     // Catch:{ all -> 0x053c }
            r13 = 3
            if (r4 != r13) goto L_0x0415
            r0 = r8[r12]     // Catch:{ all -> 0x053c }
            r4 = r8[r11]     // Catch:{ all -> 0x053c }
            boolean r4 = java.lang.Boolean.parseBoolean(r4)     // Catch:{ all -> 0x053c }
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r5 = r1.mPortManager     // Catch:{ all -> 0x053c }
            if (r5 == 0) goto L_0x0411
            com.android.server.usb.UsbPortManager r5 = r1.mPortManager     // Catch:{ all -> 0x053c }
            boolean r11 = r4.booleanValue()     // Catch:{ all -> 0x053c }
            r5.simulateContaminantStatus(r0, r11, r6)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            com.android.server.usb.UsbPortManager r5 = r1.mPortManager     // Catch:{ all -> 0x053c }
            com.android.internal.util.dump.DualDumpOutputStream r11 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x053c }
            com.android.internal.util.IndentingPrintWriter r12 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x053c }
            r12.<init>(r6, r7)     // Catch:{ all -> 0x053c }
            r11.<init>(r12)     // Catch:{ all -> 0x053c }
            r5.dump(r11, r2, r9)     // Catch:{ all -> 0x053c }
        L_0x0411:
            r4 = r26
            goto L_0x059e
        L_0x0415:
            java.lang.String r4 = "ports"
            r11 = 0
            r13 = r8[r11]     // Catch:{ all -> 0x053c }
            boolean r4 = r4.equals(r13)     // Catch:{ all -> 0x053c }
            if (r4 == 0) goto L_0x043f
            int r4 = r8.length     // Catch:{ all -> 0x053c }
            if (r4 != r12) goto L_0x043f
            com.android.server.usb.UsbPortManager r0 = r1.mPortManager     // Catch:{ all -> 0x053c }
            if (r0 == 0) goto L_0x043b
            com.android.server.usb.UsbPortManager r0 = r1.mPortManager     // Catch:{ all -> 0x053c }
            com.android.internal.util.dump.DualDumpOutputStream r4 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x053c }
            com.android.internal.util.IndentingPrintWriter r5 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x053c }
            r5.<init>(r6, r7)     // Catch:{ all -> 0x053c }
            r4.<init>(r5)     // Catch:{ all -> 0x053c }
            r0.dump(r4, r2, r9)     // Catch:{ all -> 0x053c }
            r4 = r26
            goto L_0x059e
        L_0x043b:
            r4 = r26
            goto L_0x059e
        L_0x043f:
            java.lang.String r2 = "dump-descriptors"
            r4 = 0
            r4 = r8[r4]     // Catch:{ all -> 0x053c }
            boolean r2 = r2.equals(r4)     // Catch:{ all -> 0x053c }
            if (r2 == 0) goto L_0x0453
            com.android.server.usb.UsbHostManager r0 = r1.mHostManager     // Catch:{ all -> 0x053c }
            r0.dumpDescriptors(r6, r8)     // Catch:{ all -> 0x053c }
            r4 = r26
            goto L_0x059e
        L_0x0453:
            java.lang.String r2 = "Dump current USB state or issue command:"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  ports"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  set-port-roles <id> <source|sink|no-power> <host|device|no-data>"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  add-port <id> <ufp|dfp|dual|none>"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  connect-port <id> <ufp|dfp><?> <source|sink><?> <host|device><?>"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "    (add ? suffix if mode, power role, or data role can be changed)"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  disconnect-port <id>"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  remove-port <id>"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  reset"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "Example USB type C port role switch:"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  dumpsys usb set-port-roles \"default\" source device"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "Example USB type C port simulation with full capabilities:"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  dumpsys usb connect-port \"matrix\" ufp? sink? device?"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  dumpsys usb ports"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  dumpsys usb disconnect-port \"matrix\""
            r6.println(r2)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  dumpsys usb remove-port \"matrix\""
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println(r5)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "Example USB type C port where only power role can be changed:"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "  dumpsys usb connect-port \"matrix\" dfp source? host"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println(r5)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r2 = "Example USB OTG port where id pin determines function:"
            r6.println(r2)     // Catch:{ all -> 0x053c }
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb connect-port \"matrix\" dfp source host"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            r6.println(r5)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "Example USB device-only port:"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb add-port \"matrix\" ufp"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb connect-port \"matrix\" ufp sink device"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            r6.println(r5)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "Example simulate contaminant status:"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb add-port \"matrix\" ufp"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb set-contaminant-status \"matrix\" true"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb set-contaminant-status \"matrix\" false"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            r6.println()     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "Example USB device descriptors:"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb dump-descriptors -dump-short"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb dump-descriptors -dump-tree"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb dump-descriptors -dump-list"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            java.lang.String r0 = "  dumpsys usb dump-descriptors -dump-raw"
            r6.println(r0)     // Catch:{ all -> 0x053c }
            r4 = r26
            goto L_0x059e
        L_0x0521:
            r0 = move-exception
            r8 = r28
            goto L_0x0527
        L_0x0525:
            r0 = move-exception
            r8 = r3
        L_0x0527:
            r4 = r26
            goto L_0x05a9
        L_0x052b:
            r8 = r3
            r3 = r15
        L_0x052d:
            if (r19 == 0) goto L_0x053e
            com.android.internal.util.dump.DualDumpOutputStream r0 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x053c }
            android.util.proto.ProtoOutputStream r2 = new android.util.proto.ProtoOutputStream     // Catch:{ all -> 0x053c }
            r4 = r26
            r2.<init>(r4)     // Catch:{ all -> 0x05a3 }
            r0.<init>(r2)     // Catch:{ all -> 0x05a3 }
            goto L_0x054f
        L_0x053c:
            r0 = move-exception
            goto L_0x0527
        L_0x053e:
            r4 = r26
            java.lang.String r0 = "USB MANAGER STATE (dumpsys usb):"
            r6.println(r0)     // Catch:{ all -> 0x05a3 }
            com.android.internal.util.dump.DualDumpOutputStream r0 = new com.android.internal.util.dump.DualDumpOutputStream     // Catch:{ all -> 0x05a3 }
            com.android.internal.util.IndentingPrintWriter r2 = new com.android.internal.util.IndentingPrintWriter     // Catch:{ all -> 0x05a3 }
            r2.<init>(r6, r7)     // Catch:{ all -> 0x05a3 }
            r0.<init>(r2)     // Catch:{ all -> 0x05a3 }
        L_0x054f:
            com.android.server.usb.UsbDeviceManager r2 = r1.mDeviceManager     // Catch:{ all -> 0x05a3 }
            if (r2 == 0) goto L_0x055f
            com.android.server.usb.UsbDeviceManager r2 = r1.mDeviceManager     // Catch:{ all -> 0x05a3 }
            java.lang.String r5 = "device_manager"
            r9 = 1146756268033(0x10b00000001, double:5.66572876188E-312)
            r2.dump(r0, r5, r9)     // Catch:{ all -> 0x05a3 }
        L_0x055f:
            com.android.server.usb.UsbHostManager r2 = r1.mHostManager     // Catch:{ all -> 0x05a3 }
            if (r2 == 0) goto L_0x0570
            com.android.server.usb.UsbHostManager r2 = r1.mHostManager     // Catch:{ all -> 0x05a3 }
            java.lang.String r5 = "host_manager"
            r9 = 1146756268034(0x10b00000002, double:5.665728761887E-312)
            r2.dump(r0, r5, r9)     // Catch:{ all -> 0x05a3 }
        L_0x0570:
            com.android.server.usb.UsbPortManager r2 = r1.mPortManager     // Catch:{ all -> 0x05a3 }
            if (r2 == 0) goto L_0x0581
            com.android.server.usb.UsbPortManager r2 = r1.mPortManager     // Catch:{ all -> 0x05a3 }
            java.lang.String r5 = "port_manager"
            r9 = 1146756268035(0x10b00000003, double:5.66572876189E-312)
            r2.dump(r0, r5, r9)     // Catch:{ all -> 0x05a3 }
        L_0x0581:
            com.android.server.usb.UsbAlsaManager r2 = r1.mAlsaManager     // Catch:{ all -> 0x05a3 }
            java.lang.String r5 = "alsa_manager"
            r9 = 1146756268036(0x10b00000004, double:5.665728761897E-312)
            r2.dump(r0, r5, r9)     // Catch:{ all -> 0x05a3 }
            com.android.server.usb.UsbSettingsManager r2 = r1.mSettingsManager     // Catch:{ all -> 0x05a3 }
            java.lang.String r5 = "settings_manager"
            r9 = 1146756268037(0x10b00000005, double:5.6657287619E-312)
            r2.dump(r0, r5, r9)     // Catch:{ all -> 0x05a3 }
            r0.flush()     // Catch:{ all -> 0x05a3 }
        L_0x059e:
            android.os.Binder.restoreCallingIdentity(r17)
            return
        L_0x05a3:
            r0 = move-exception
            goto L_0x05a9
        L_0x05a5:
            r0 = move-exception
            r4 = r26
            r8 = r3
        L_0x05a9:
            android.os.Binder.restoreCallingIdentity(r17)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    private static String removeLastChar(String value) {
        return value.substring(0, value.length() - 1);
    }
}
