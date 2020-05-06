package com.android.server.usb;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.usb.AccessoryFilter;
import android.hardware.usb.DeviceFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.dump.DumpUtils;
import com.android.server.inputmethod.MiuiSecurityInputMethodHelper;
import com.android.server.pm.CloudControlPreinstallService;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import java.util.ArrayList;
import java.util.List;

class UsbUserSettingsManager {
    private static final boolean DEBUG = false;
    private static final String TAG = UsbUserSettingsManager.class.getSimpleName();
    private final Object mLock = new Object();
    private final PackageManager mPackageManager;
    private final UsbPermissionManager mUsbPermissionManager;
    private final UserHandle mUser;
    private final Context mUserContext;

    UsbUserSettingsManager(Context context, UserHandle user, UsbPermissionManager usbPermissionManager) {
        try {
            this.mUserContext = context.createPackageContextAsUser(PackageManagerService.PLATFORM_PACKAGE_NAME, 0, user);
            this.mPackageManager = this.mUserContext.getPackageManager();
            this.mUser = user;
            this.mUsbPermissionManager = usbPermissionManager;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Missing android package");
        }
    }

    /* access modifiers changed from: package-private */
    public void removeDevicePermissions(UsbDevice device) {
        this.mUsbPermissionManager.removeDevicePermissions(device);
    }

    /* access modifiers changed from: package-private */
    public void removeAccessoryPermissions(UsbAccessory accessory) {
        this.mUsbPermissionManager.removeAccessoryPermissions(accessory);
    }

    private boolean isCameraDevicePresent(UsbDevice device) {
        if (device.getDeviceClass() == 14) {
            return true;
        }
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            if (device.getInterface(i).getInterfaceClass() == 14) {
                return true;
            }
        }
        return false;
    }

    private boolean isCameraPermissionGranted(String packageName, int uid) {
        try {
            ApplicationInfo aInfo = this.mPackageManager.getApplicationInfo(packageName, 0);
            if (aInfo.uid != uid) {
                String str = TAG;
                Slog.i(str, "Package " + packageName + " does not match caller's uid " + uid);
                return false;
            } else if (aInfo.targetSdkVersion < 28 || -1 != this.mUserContext.checkCallingPermission("android.permission.CAMERA")) {
                return true;
            } else {
                Slog.i(TAG, "Camera permission required for USB video class devices");
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Slog.i(TAG, "Package not found, likely due to invalid package name!");
            return false;
        }
    }

    public boolean hasPermission(UsbDevice device, String packageName, int uid) {
        if (!isCameraDevicePresent(device) || isCameraPermissionGranted(packageName, uid)) {
            return this.mUsbPermissionManager.hasPermission(device, uid);
        }
        return false;
    }

    public boolean hasPermission(UsbAccessory accessory, int uid) {
        return this.mUsbPermissionManager.hasPermission(accessory, uid);
    }

    public void checkPermission(UsbDevice device, String packageName, int uid) {
        if (!hasPermission(device, packageName, uid)) {
            throw new SecurityException("User has not given " + uid + SliceClientPermissions.SliceAuthority.DELIMITER + packageName + " permission to access device " + device.getDeviceName());
        }
    }

    public void checkPermission(UsbAccessory accessory, int uid) {
        if (!hasPermission(accessory, uid)) {
            throw new SecurityException("User has not given " + uid + " permission to accessory " + accessory);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    private void requestPermissionDialog(UsbDevice device, UsbAccessory accessory, boolean canBeDefault, String packageName, PendingIntent pi, int uid) {
        String str = packageName;
        int i = uid;
        try {
            if (this.mPackageManager.getApplicationInfo(str, 0).uid == i) {
                this.mUsbPermissionManager.requestPermissionDialog(device, accessory, canBeDefault, packageName, uid, this.mUserContext, pi);
                return;
            }
            throw new IllegalArgumentException("package " + str + " does not match caller's uid " + i);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("package " + str + " not found");
        }
    }

    public void requestPermission(UsbDevice device, String packageName, PendingIntent pi, int uid) {
        Intent intent = new Intent();
        if (hasPermission(device, packageName, uid)) {
            intent.putExtra(CloudControlPreinstallService.ConnectEntity.DEVICE, device);
            intent.putExtra("permission", true);
            try {
                pi.send(this.mUserContext, 0, intent);
            } catch (PendingIntent.CanceledException e) {
            }
        } else if (!isCameraDevicePresent(device) || isCameraPermissionGranted(packageName, uid)) {
            requestPermissionDialog(device, (UsbAccessory) null, canBeDefault(device, packageName), packageName, pi, uid);
        } else {
            intent.putExtra(CloudControlPreinstallService.ConnectEntity.DEVICE, device);
            intent.putExtra("permission", false);
            try {
                pi.send(this.mUserContext, 0, intent);
            } catch (PendingIntent.CanceledException e2) {
            }
        }
    }

    public void requestPermission(UsbAccessory accessory, String packageName, PendingIntent pi, int uid) {
        if (hasPermission(accessory, uid)) {
            Intent intent = new Intent();
            intent.putExtra("accessory", accessory);
            intent.putExtra("permission", true);
            try {
                pi.send(this.mUserContext, 0, intent);
            } catch (PendingIntent.CanceledException e) {
            }
        } else {
            requestPermissionDialog((UsbDevice) null, accessory, canBeDefault(accessory, packageName), packageName, pi, uid);
        }
    }

    public void grantDevicePermission(UsbDevice device, int uid) {
        this.mUsbPermissionManager.grantDevicePermission(device, uid);
    }

    public void grantAccessoryPermission(UsbAccessory accessory, int uid) {
        this.mUsbPermissionManager.grantAccessoryPermission(accessory, uid);
    }

    /* access modifiers changed from: package-private */
    public List<ResolveInfo> queryIntentActivities(Intent intent) {
        return this.mPackageManager.queryIntentActivitiesAsUser(intent, 128, this.mUser.getIdentifier());
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004d, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        $closeResource(r5, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0051, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canBeDefault(android.hardware.usb.UsbDevice r10, java.lang.String r11) {
        /*
            r9 = this;
            android.content.pm.ActivityInfo[] r0 = r9.getPackageActivities(r11)
            if (r0 == 0) goto L_0x0070
            int r1 = r0.length
            r2 = 0
        L_0x0008:
            if (r2 >= r1) goto L_0x0070
            r3 = r0[r2]
            android.content.pm.PackageManager r4 = r9.mPackageManager     // Catch:{ Exception -> 0x0052 }
            java.lang.String r5 = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
            android.content.res.XmlResourceParser r4 = r3.loadXmlMetaData(r4, r5)     // Catch:{ Exception -> 0x0052 }
            r5 = 0
            if (r4 != 0) goto L_0x001d
            if (r4 == 0) goto L_0x006d
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            goto L_0x006d
        L_0x001d:
            com.android.internal.util.XmlUtils.nextElement(r4)     // Catch:{ all -> 0x004b }
        L_0x0020:
            int r6 = r4.getEventType()     // Catch:{ all -> 0x004b }
            r7 = 1
            if (r6 == r7) goto L_0x0047
            java.lang.String r6 = "usb-device"
            java.lang.String r8 = r4.getName()     // Catch:{ all -> 0x004b }
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x004b }
            if (r6 == 0) goto L_0x0043
            android.hardware.usb.DeviceFilter r6 = android.hardware.usb.DeviceFilter.read(r4)     // Catch:{ all -> 0x004b }
            boolean r8 = r6.matches(r10)     // Catch:{ all -> 0x004b }
            if (r8 == 0) goto L_0x0043
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            return r7
        L_0x0043:
            com.android.internal.util.XmlUtils.nextElement(r4)     // Catch:{ all -> 0x004b }
            goto L_0x0020
        L_0x0047:
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            goto L_0x006d
        L_0x004b:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x004d }
        L_0x004d:
            r6 = move-exception
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            throw r6     // Catch:{ Exception -> 0x0052 }
        L_0x0052:
            r4 = move-exception
            java.lang.String r5 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Unable to load component info "
            r6.append(r7)
            java.lang.String r7 = r3.toString()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.w(r5, r6, r4)
        L_0x006d:
            int r2 = r2 + 1
            goto L_0x0008
        L_0x0070:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbUserSettingsManager.canBeDefault(android.hardware.usb.UsbDevice, java.lang.String):boolean");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004d, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        $closeResource(r5, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0051, code lost:
        throw r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canBeDefault(android.hardware.usb.UsbAccessory r10, java.lang.String r11) {
        /*
            r9 = this;
            android.content.pm.ActivityInfo[] r0 = r9.getPackageActivities(r11)
            if (r0 == 0) goto L_0x0070
            int r1 = r0.length
            r2 = 0
        L_0x0008:
            if (r2 >= r1) goto L_0x0070
            r3 = r0[r2]
            android.content.pm.PackageManager r4 = r9.mPackageManager     // Catch:{ Exception -> 0x0052 }
            java.lang.String r5 = "android.hardware.usb.action.USB_ACCESSORY_ATTACHED"
            android.content.res.XmlResourceParser r4 = r3.loadXmlMetaData(r4, r5)     // Catch:{ Exception -> 0x0052 }
            r5 = 0
            if (r4 != 0) goto L_0x001d
            if (r4 == 0) goto L_0x006d
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            goto L_0x006d
        L_0x001d:
            com.android.internal.util.XmlUtils.nextElement(r4)     // Catch:{ all -> 0x004b }
        L_0x0020:
            int r6 = r4.getEventType()     // Catch:{ all -> 0x004b }
            r7 = 1
            if (r6 == r7) goto L_0x0047
            java.lang.String r6 = "usb-accessory"
            java.lang.String r8 = r4.getName()     // Catch:{ all -> 0x004b }
            boolean r6 = r6.equals(r8)     // Catch:{ all -> 0x004b }
            if (r6 == 0) goto L_0x0043
            android.hardware.usb.AccessoryFilter r6 = android.hardware.usb.AccessoryFilter.read(r4)     // Catch:{ all -> 0x004b }
            boolean r8 = r6.matches(r10)     // Catch:{ all -> 0x004b }
            if (r8 == 0) goto L_0x0043
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            return r7
        L_0x0043:
            com.android.internal.util.XmlUtils.nextElement(r4)     // Catch:{ all -> 0x004b }
            goto L_0x0020
        L_0x0047:
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            goto L_0x006d
        L_0x004b:
            r5 = move-exception
            throw r5     // Catch:{ all -> 0x004d }
        L_0x004d:
            r6 = move-exception
            $closeResource(r5, r4)     // Catch:{ Exception -> 0x0052 }
            throw r6     // Catch:{ Exception -> 0x0052 }
        L_0x0052:
            r4 = move-exception
            java.lang.String r5 = TAG
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Unable to load component info "
            r6.append(r7)
            java.lang.String r7 = r3.toString()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.w(r5, r6, r4)
        L_0x006d:
            int r2 = r2 + 1
            goto L_0x0008
        L_0x0070:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbUserSettingsManager.canBeDefault(android.hardware.usb.UsbAccessory, java.lang.String):boolean");
    }

    private ActivityInfo[] getPackageActivities(String packageName) {
        try {
            return this.mPackageManager.getPackageInfo(packageName, MiuiSecurityInputMethodHelper.TEXT_PASSWORD).activities;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        int numDeviceAttachedActivities;
        DualDumpOutputStream dualDumpOutputStream = dump;
        long token = dump.start(idName, id);
        synchronized (this.mLock) {
            dualDumpOutputStream.write("user_id", 1120986464257L, this.mUser.getIdentifier());
            this.mUsbPermissionManager.dump(dualDumpOutputStream);
            List<ResolveInfo> deviceAttachedActivities = queryIntentActivities(new Intent("android.hardware.usb.action.USB_DEVICE_ATTACHED"));
            int numDeviceAttachedActivities2 = deviceAttachedActivities.size();
            for (int activityNum = 0; activityNum < numDeviceAttachedActivities2; activityNum++) {
                ResolveInfo deviceAttachedActivity = deviceAttachedActivities.get(activityNum);
                long deviceAttachedActivityToken = dualDumpOutputStream.start("device_attached_activities", 2246267895812L);
                DumpUtils.writeComponentName(dualDumpOutputStream, "activity", 1146756268033L, new ComponentName(deviceAttachedActivity.activityInfo.packageName, deviceAttachedActivity.activityInfo.name));
                ArrayList<DeviceFilter> deviceFilters = UsbProfileGroupSettingsManager.getDeviceFilters(this.mPackageManager, deviceAttachedActivity);
                if (deviceFilters != null) {
                    int numDeviceFilters = deviceFilters.size();
                    int filterNum = 0;
                    while (filterNum < numDeviceFilters) {
                        deviceFilters.get(filterNum).dump(dualDumpOutputStream, "filters", 2246267895810L);
                        filterNum++;
                        deviceFilters = deviceFilters;
                        numDeviceFilters = numDeviceFilters;
                    }
                    int i = numDeviceFilters;
                }
                dualDumpOutputStream.end(deviceAttachedActivityToken);
            }
            List<ResolveInfo> accessoryAttachedActivities = queryIntentActivities(new Intent("android.hardware.usb.action.USB_ACCESSORY_ATTACHED"));
            int numAccessoryAttachedActivities = accessoryAttachedActivities.size();
            int activityNum2 = 0;
            while (activityNum2 < numAccessoryAttachedActivities) {
                ResolveInfo accessoryAttachedActivity = accessoryAttachedActivities.get(activityNum2);
                long accessoryAttachedActivityToken = dualDumpOutputStream.start("accessory_attached_activities", 2246267895813L);
                List<ResolveInfo> deviceAttachedActivities2 = deviceAttachedActivities;
                int numDeviceAttachedActivities3 = numDeviceAttachedActivities2;
                List<ResolveInfo> accessoryAttachedActivities2 = accessoryAttachedActivities;
                DumpUtils.writeComponentName(dualDumpOutputStream, "activity", 1146756268033L, new ComponentName(accessoryAttachedActivity.activityInfo.packageName, accessoryAttachedActivity.activityInfo.name));
                ArrayList<AccessoryFilter> accessoryFilters = UsbProfileGroupSettingsManager.getAccessoryFilters(this.mPackageManager, accessoryAttachedActivity);
                if (accessoryFilters != null) {
                    int numAccessoryFilters = accessoryFilters.size();
                    int filterNum2 = 0;
                    while (filterNum2 < numAccessoryFilters) {
                        accessoryFilters.get(filterNum2).dump(dualDumpOutputStream, "filters", 2246267895810L);
                        filterNum2++;
                        numDeviceAttachedActivities3 = numDeviceAttachedActivities3;
                        accessoryFilters = accessoryFilters;
                        numAccessoryFilters = numAccessoryFilters;
                    }
                    numDeviceAttachedActivities = numDeviceAttachedActivities3;
                    ArrayList<AccessoryFilter> arrayList = accessoryFilters;
                    int i2 = numAccessoryFilters;
                } else {
                    numDeviceAttachedActivities = numDeviceAttachedActivities3;
                    ArrayList<AccessoryFilter> arrayList2 = accessoryFilters;
                }
                dualDumpOutputStream.end(accessoryAttachedActivityToken);
                activityNum2++;
                accessoryAttachedActivities = accessoryAttachedActivities2;
                numDeviceAttachedActivities2 = numDeviceAttachedActivities;
                deviceAttachedActivities = deviceAttachedActivities2;
            }
            int i3 = numDeviceAttachedActivities2;
            List<ResolveInfo> list = accessoryAttachedActivities;
        }
        dualDumpOutputStream.end(token);
    }
}
