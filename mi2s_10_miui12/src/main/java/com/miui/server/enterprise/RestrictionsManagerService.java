package com.miui.server.enterprise;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AppOpsManager;
import android.app.admin.IDevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.service.persistentdata.IPersistentDataBlockService;
import android.util.Slog;
import com.android.server.content.MiSyncConstants;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.pm.UserManagerService;
import com.android.server.wm.WindowManagerService;
import com.miui.enterprise.IRestrictionsManager;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.lang.reflect.Method;
import java.util.Iterator;
import miui.securityspace.CrossUserUtils;

public class RestrictionsManagerService extends IRestrictionsManager.Stub {
    private static final String TAG = "Enterprise-restric";
    private AppOpsManager mAppOpsManager;
    /* access modifiers changed from: private */
    public Context mContext;
    private ComponentName mDeviceOwner;
    private IDevicePolicyManager mDevicePolicyManager = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"));
    private Handler mHandler;
    private PackageManagerService mPMS = ((PackageManagerService) ServiceManager.getService(Settings.ATTR_PACKAGE));
    private IPersistentDataBlockService mPdbService = IPersistentDataBlockService.Stub.asInterface(ServiceManager.getService("persistent_data_block"));
    private UserManagerService mUserManager = ((UserManagerService) ServiceManager.getService("user"));
    private WindowManagerService mWindowManagerService = ((WindowManagerService) ServiceManager.getService("window"));

    /* JADX WARNING: type inference failed for: r8v0, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r8v1, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r8v2, types: [android.os.IBinder] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bootComplete() {
        /*
            r11 = this;
            java.lang.String r0 = "Enterprise-restric"
            java.lang.String r1 = "Restriction init"
            android.util.Slog.d(r0, r1)
            android.content.Context r0 = r11.mContext
            java.lang.String r1 = "user"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.UserManager r0 = (android.os.UserManager) r0
            java.util.List r1 = r0.getUsers()
            java.util.Iterator r2 = r1.iterator()
        L_0x0019:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0084
            java.lang.Object r3 = r2.next()
            android.content.pm.UserInfo r3 = (android.content.pm.UserInfo) r3
            android.content.Context r4 = r11.mContext
            int r5 = r3.id
            java.lang.String r6 = "disallow_screencapture"
            boolean r4 = com.miui.enterprise.RestrictionsHelper.hasRestriction(r4, r6, r5)
            if (r4 == 0) goto L_0x003b
            com.android.server.wm.WindowManagerService r4 = r11.mWindowManagerService
            android.content.Context r5 = r11.mContext
            int r6 = r3.id
            r7 = 1
            com.miui.server.enterprise.RestrictionManagerServiceProxy.setScreenCaptureDisabled(r4, r5, r6, r7)
        L_0x003b:
            android.content.Context r4 = r11.mContext
            int r5 = r3.id
            java.lang.String r6 = "disallow_vpn"
            boolean r4 = com.miui.enterprise.RestrictionsHelper.hasRestriction(r4, r6, r5)
            if (r4 == 0) goto L_0x0053
            android.app.AppOpsManager r5 = r11.mAppOpsManager
            r6 = 47
            r7 = 1
            r9 = 0
            int r10 = r3.id
            r8 = r11
            r5.setUserRestrictionForUser(r6, r7, r8, r9, r10)
        L_0x0053:
            android.content.Context r4 = r11.mContext
            int r5 = r3.id
            java.lang.String r6 = "disallow_fingerprint"
            boolean r4 = com.miui.enterprise.RestrictionsHelper.hasRestriction(r4, r6, r5)
            if (r4 == 0) goto L_0x006b
            android.app.AppOpsManager r5 = r11.mAppOpsManager
            r6 = 55
            r7 = 1
            r9 = 0
            int r10 = r3.id
            r8 = r11
            r5.setUserRestrictionForUser(r6, r7, r8, r9, r10)
        L_0x006b:
            android.content.Context r4 = r11.mContext
            int r5 = r3.id
            java.lang.String r6 = "disallow_imeiread"
            boolean r4 = com.miui.enterprise.RestrictionsHelper.hasRestriction(r4, r6, r5)
            if (r4 == 0) goto L_0x0083
            android.app.AppOpsManager r5 = r11.mAppOpsManager
            r6 = 51
            r7 = 1
            r9 = 0
            int r10 = r3.id
            r8 = r11
            r5.setUserRestrictionForUser(r6, r7, r8, r9, r10)
        L_0x0083:
            goto L_0x0019
        L_0x0084:
            r11.startWatchLocationRestriction()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.enterprise.RestrictionsManagerService.bootComplete():void");
    }

    RestrictionsManagerService(Context context) {
        this.mContext = context;
        try {
            this.mDeviceOwner = this.mDevicePolicyManager.getDeviceOwnerComponent(true);
        } catch (RemoteException e) {
        }
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mHandler = new Handler();
    }

    private void startWatchLocationRestriction() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("location_providers_allowed"), true, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                int currentUserId = CrossUserUtils.getCurrentUserId();
                int mode = EnterpriseSettings.getInt(RestrictionsManagerService.this.mContext, "gps_state", 1, currentUserId);
                if (mode == 4) {
                    Slog.d(RestrictionsManagerService.TAG, "FORCE_OPEN GPS");
                    Settings.Secure.putIntForUser(RestrictionsManagerService.this.mContext.getContentResolver(), "location_mode", 3, currentUserId);
                } else if (mode == 0) {
                    Slog.d(RestrictionsManagerService.TAG, "Close GPS");
                    Settings.Secure.putIntForUser(RestrictionsManagerService.this.mContext.getContentResolver(), "location_mode", 0, currentUserId);
                }
            }
        }, -1);
    }

    public void setControlStatus(String key, int value, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        if (CrossUserUtils.getCurrentUserId() == userId) {
            int originState = EnterpriseSettings.getInt(this.mContext, key, 1, userId);
            if ((originState != 0 && originState != 4) || (value != 2 && value != 3)) {
                EnterpriseSettings.putInt(this.mContext, key, 1, userId);
                char c = 65535;
                switch (key.hashCode()) {
                    case -2044718916:
                        if (key.equals("gps_state")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -1612402752:
                        if (key.equals("bluetooth_state")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 486699751:
                        if (key.equals("wifi_state")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 919577853:
                        if (key.equals("nfc_state")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1628995940:
                        if (key.equals("airplane_state")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    WifiManager manager = (WifiManager) this.mContext.getSystemService("wifi");
                    if (shouldClose(value)) {
                        manager.setWifiEnabled(false);
                    }
                    if (shouldOpen(value)) {
                        manager.setWifiEnabled(true);
                    }
                } else if (c == 1) {
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (shouldClose(value)) {
                        btAdapter.disable();
                    }
                    if (shouldOpen(value)) {
                        btAdapter.enable();
                    }
                } else if (c == 2) {
                    if (shouldClose(value)) {
                        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "location_mode", 0, userId);
                    }
                    if (shouldOpen(value)) {
                        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "location_mode", 3, userId);
                    }
                } else if (c == 3) {
                    NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this.mContext);
                    if (adapter != null) {
                        if (shouldClose(value)) {
                            adapter.disable();
                        }
                        if (shouldOpen(value)) {
                            adapter.enable();
                        }
                    }
                } else if (c == 4) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService("connectivity");
                    if (shouldClose(value)) {
                        connectivityManager.setAirplaneMode(false);
                    }
                    if (shouldOpen(value)) {
                        connectivityManager.setAirplaneMode(true);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown restriction item: " + key);
                }
                EnterpriseSettings.putInt(this.mContext, key, value, userId);
            }
        }
    }

    private boolean shouldOpen(int state) {
        return state == 2 || state == 4;
    }

    private boolean shouldClose(int state) {
        return state == 0 || state == 3;
    }

    /* JADX WARNING: type inference failed for: r4v1, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r4v6, types: [android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r4v7, types: [android.os.IBinder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setRestriction(java.lang.String r9, boolean r10, int r11) {
        /*
            r8 = this;
            android.content.Context r0 = r8.mContext
            com.miui.server.enterprise.ServiceUtils.checkPermission(r0)
            android.content.Context r0 = r8.mContext
            com.miui.enterprise.settings.EnterpriseSettings.putInt(r0, r9, r10, r11)
            r0 = -1
            int r1 = r9.hashCode()
            r2 = 4
            r3 = 8
            r4 = 0
            switch(r1) {
                case -1915200762: goto L_0x0126;
                case -1886279575: goto L_0x011c;
                case -1859967211: goto L_0x0111;
                case -1552410727: goto L_0x0106;
                case -1425744347: goto L_0x00fc;
                case -1395678890: goto L_0x00f2;
                case -1094316185: goto L_0x00e7;
                case -831735134: goto L_0x00dc;
                case -453687405: goto L_0x00d2;
                case -429823771: goto L_0x00c8;
                case -429815248: goto L_0x00bd;
                case -208396879: goto L_0x00b1;
                case -170808536: goto L_0x00a6;
                case 82961609: goto L_0x009a;
                case 408143697: goto L_0x008e;
                case 411690763: goto L_0x0082;
                case 411883267: goto L_0x0076;
                case 412022659: goto L_0x006a;
                case 470637462: goto L_0x005f;
                case 480222787: goto L_0x0053;
                case 487960096: goto L_0x0047;
                case 724096394: goto L_0x003b;
                case 907380174: goto L_0x002f;
                case 1157197624: goto L_0x0023;
                case 1846688878: goto L_0x0018;
                default: goto L_0x0016;
            }
        L_0x0016:
            goto L_0x0130
        L_0x0018:
            java.lang.String r1 = "disallow_microphone"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 3
            goto L_0x0130
        L_0x0023:
            java.lang.String r1 = "disable_accelerometer"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 13
            goto L_0x0130
        L_0x002f:
            java.lang.String r1 = "disallow_mi_account"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 20
            goto L_0x0130
        L_0x003b:
            java.lang.String r1 = "disallow_status_bar"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 19
            goto L_0x0130
        L_0x0047:
            java.lang.String r1 = "disallow_fingerprint"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 9
            goto L_0x0130
        L_0x0053:
            java.lang.String r1 = "disallow_change_language"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 17
            goto L_0x0130
        L_0x005f:
            java.lang.String r1 = "disallow_screencapture"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = r2
            goto L_0x0130
        L_0x006a:
            java.lang.String r1 = "disallow_key_menu"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 23
            goto L_0x0130
        L_0x0076:
            java.lang.String r1 = "disallow_key_home"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 22
            goto L_0x0130
        L_0x0082:
            java.lang.String r1 = "disallow_key_back"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 21
            goto L_0x0130
        L_0x008e:
            java.lang.String r1 = "disallow_safe_mode"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 16
            goto L_0x0130
        L_0x009a:
            java.lang.String r1 = "disallow_factoryreset"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 10
            goto L_0x0130
        L_0x00a6:
            java.lang.String r1 = "disable_usb_device"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 7
            goto L_0x0130
        L_0x00b1:
            java.lang.String r1 = "disallow_timeset"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 11
            goto L_0x0130
        L_0x00bd:
            java.lang.String r1 = "disallow_vpn"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = r4
            goto L_0x0130
        L_0x00c8:
            java.lang.String r1 = "disallow_mtp"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 6
            goto L_0x0130
        L_0x00d2:
            java.lang.String r1 = "disallow_usbdebug"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = r3
            goto L_0x0130
        L_0x00dc:
            java.lang.String r1 = "disallow_imeiread"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 12
            goto L_0x0130
        L_0x00e7:
            java.lang.String r1 = "disallow_auto_sync"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 15
            goto L_0x0130
        L_0x00f2:
            java.lang.String r1 = "disallow_tether"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 1
            goto L_0x0130
        L_0x00fc:
            java.lang.String r1 = "disallow_sdcard"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 5
            goto L_0x0130
        L_0x0106:
            java.lang.String r1 = "disallow_landscape_statusbar"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 24
            goto L_0x0130
        L_0x0111:
            java.lang.String r1 = "disallow_system_update"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 18
            goto L_0x0130
        L_0x011c:
            java.lang.String r1 = "disallow_camera"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 2
            goto L_0x0130
        L_0x0126:
            java.lang.String r1 = "disallow_backup"
            boolean r1 = r9.equals(r1)
            if (r1 == 0) goto L_0x0016
            r0 = 14
        L_0x0130:
            java.lang.String r1 = "Enterprise-restric"
            switch(r0) {
                case 0: goto L_0x0281;
                case 1: goto L_0x0264;
                case 2: goto L_0x025b;
                case 3: goto L_0x0252;
                case 4: goto L_0x0249;
                case 5: goto L_0x023c;
                case 6: goto L_0x0224;
                case 7: goto L_0x0217;
                case 8: goto L_0x01fd;
                case 9: goto L_0x01f0;
                case 10: goto L_0x01e2;
                case 11: goto L_0x01c3;
                case 12: goto L_0x01b6;
                case 13: goto L_0x01b4;
                case 14: goto L_0x018e;
                case 15: goto L_0x0184;
                case 16: goto L_0x017b;
                case 17: goto L_0x0174;
                case 18: goto L_0x0172;
                case 19: goto L_0x0156;
                case 20: goto L_0x0154;
                case 21: goto L_0x0152;
                case 22: goto L_0x0150;
                case 23: goto L_0x014e;
                case 24: goto L_0x014c;
                default: goto L_0x0135;
            }
        L_0x0135:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Unknown restriction item: "
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x014c:
            goto L_0x02e2
        L_0x014e:
            goto L_0x02e2
        L_0x0150:
            goto L_0x02e2
        L_0x0152:
            goto L_0x02e2
        L_0x0154:
            goto L_0x02e2
        L_0x0156:
            android.content.Context r0 = r8.mContext
            java.lang.String r2 = "statusbar"
            java.lang.Object r0 = r0.getSystemService(r2)
            android.app.StatusBarManager r0 = (android.app.StatusBarManager) r0
            if (r0 != 0) goto L_0x0169
            java.lang.String r2 = "statusBarManager is null!"
            android.util.Log.e(r1, r2)
            goto L_0x02e2
        L_0x0169:
            if (r10 == 0) goto L_0x016d
            r4 = 65536(0x10000, float:9.18355E-41)
        L_0x016d:
            r0.disable(r4)
            goto L_0x02e2
        L_0x0172:
            goto L_0x02e2
        L_0x0174:
            java.util.Locale r0 = java.util.Locale.CHINA
            com.android.internal.app.LocalePicker.updateLocale(r0)
            goto L_0x02e2
        L_0x017b:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r1 = "no_safe_boot"
            r0.setUserRestriction(r1, r10, r11)
            goto L_0x02e2
        L_0x0184:
            if (r10 == 0) goto L_0x02e2
            android.content.ContentResolver.setMasterSyncAutomaticallyAsUser(r4, r11)
            r8.closeCloudBackup(r11)
            goto L_0x02e2
        L_0x018e:
            if (r10 == 0) goto L_0x01a2
            com.android.server.pm.PackageManagerService r1 = r8.mPMS
            r3 = 2
            r4 = 0
            android.content.Context r0 = r8.mContext
            java.lang.String r6 = r0.getPackageName()
            java.lang.String r2 = "com.miui.backup"
            r5 = r11
            r1.setApplicationEnabledSetting(r2, r3, r4, r5, r6)
            goto L_0x02e2
        L_0x01a2:
            com.android.server.pm.PackageManagerService r1 = r8.mPMS
            r3 = 1
            r4 = 0
            android.content.Context r0 = r8.mContext
            java.lang.String r6 = r0.getPackageName()
            java.lang.String r2 = "com.miui.backup"
            r5 = r11
            r1.setApplicationEnabledSetting(r2, r3, r4, r5, r6)
            goto L_0x02e2
        L_0x01b4:
            goto L_0x02e2
        L_0x01b6:
            android.app.AppOpsManager r1 = r8.mAppOpsManager
            r2 = 51
            r5 = 0
            r3 = r10
            r4 = r8
            r6 = r11
            r1.setUserRestrictionForUser(r2, r3, r4, r5, r6)
            goto L_0x02e2
        L_0x01c3:
            int r0 = miui.securityspace.CrossUserUtils.getCurrentUserId()
            if (r0 != r11) goto L_0x01d4
            android.content.Context r0 = r8.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "auto_time"
            android.provider.Settings.Global.putInt(r0, r1, r10)
        L_0x01d4:
            android.content.Context r0 = r8.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "time_change_disallow"
            android.provider.Settings.Secure.putIntForUser(r0, r1, r10, r11)
            goto L_0x02e2
        L_0x01e2:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r1 = "no_factory_reset"
            r0.setUserRestriction(r1, r10, r11)
            android.service.persistentdata.IPersistentDataBlockService r0 = r8.mPdbService
            com.miui.server.enterprise.RestrictionManagerServiceProxy.setDisableRecoveryClearData(r0, r10)
            goto L_0x02e2
        L_0x01f0:
            android.app.AppOpsManager r1 = r8.mAppOpsManager
            r2 = 55
            r5 = 0
            r3 = r10
            r4 = r8
            r6 = r11
            r1.setUserRestrictionForUser(r2, r3, r4, r5, r6)
            goto L_0x02e2
        L_0x01fd:
            int r0 = miui.securityspace.CrossUserUtils.getCurrentUserId()
            if (r0 != r11) goto L_0x020e
            android.content.Context r0 = r8.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r1 = "adb_enabled"
            android.provider.Settings.Global.putInt(r0, r1, r4)
        L_0x020e:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r1 = "no_debugging_features"
            r0.setUserRestriction(r1, r10, r11)
            goto L_0x02e2
        L_0x0217:
            if (r10 == 0) goto L_0x02e2
            int r0 = miui.securityspace.CrossUserUtils.getCurrentUserId()
            if (r0 != r11) goto L_0x02e2
            r8.unmountPublicVolume(r3)
            goto L_0x02e2
        L_0x0224:
            android.content.Context r0 = r8.mContext
            java.lang.String r1 = "usb"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.hardware.usb.UsbManager r0 = (android.hardware.usb.UsbManager) r0
            com.android.server.pm.UserManagerService r1 = r8.mUserManager
            java.lang.String r2 = "no_usb_file_transfer"
            r1.setUserRestriction(r2, r10, r11)
            java.lang.String r1 = "none"
            r8.setUsbFunction(r0, r1)
            goto L_0x02e2
        L_0x023c:
            if (r10 == 0) goto L_0x02e2
            int r0 = miui.securityspace.CrossUserUtils.getCurrentUserId()
            if (r0 != r11) goto L_0x02e2
            r8.unmountPublicVolume(r2)
            goto L_0x02e2
        L_0x0249:
            com.android.server.wm.WindowManagerService r0 = r8.mWindowManagerService
            android.content.Context r1 = r8.mContext
            com.miui.server.enterprise.RestrictionManagerServiceProxy.setScreenCaptureDisabled(r0, r1, r11, r10)
            goto L_0x02e2
        L_0x0252:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r1 = "no_record_audio"
            r0.setUserRestriction(r1, r10, r11)
            goto L_0x02e2
        L_0x025b:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r1 = "no_camera"
            r0.setUserRestriction(r1, r10, r11)
            goto L_0x02e2
        L_0x0264:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r1 = "no_config_tethering"
            boolean r0 = r0.hasUserRestriction(r1, r11)
            if (r10 == 0) goto L_0x027b
            int r2 = miui.securityspace.CrossUserUtils.getCurrentUserId()
            if (r11 != r2) goto L_0x027b
            if (r0 != 0) goto L_0x027b
            android.content.Context r2 = r8.mContext
            com.miui.server.enterprise.RestrictionManagerServiceProxy.setWifiApEnabled(r2, r4)
        L_0x027b:
            com.android.server.pm.UserManagerService r2 = r8.mUserManager
            r2.setUserRestriction(r1, r10, r11)
            goto L_0x02e2
        L_0x0281:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            java.lang.String r2 = "no_config_vpn"
            boolean r7 = r0.hasUserRestriction(r2, r11)
            if (r10 == 0) goto L_0x02d1
            int r0 = miui.securityspace.CrossUserUtils.getCurrentUserId()
            if (r0 != r11) goto L_0x02d1
            if (r7 != 0) goto L_0x02d1
            java.lang.String r0 = "connectivity"
            android.os.IBinder r0 = android.os.ServiceManager.getService(r0)     // Catch:{ Exception -> 0x02bc }
            android.net.IConnectivityManager r0 = android.net.IConnectivityManager.Stub.asInterface(r0)     // Catch:{ Exception -> 0x02bc }
            int r3 = android.os.UserHandle.myUserId()     // Catch:{ Exception -> 0x02bc }
            com.android.internal.net.VpnConfig r3 = r0.getVpnConfig(r3)     // Catch:{ Exception -> 0x02bc }
            if (r3 == 0) goto L_0x02ac
            android.app.PendingIntent r4 = r3.configureIntent     // Catch:{ Exception -> 0x02bc }
            r4.send()     // Catch:{ Exception -> 0x02bc }
        L_0x02ac:
            int r4 = android.os.UserHandle.myUserId()     // Catch:{ Exception -> 0x02bc }
            com.android.internal.net.LegacyVpnInfo r4 = r0.getLegacyVpnInfo(r4)     // Catch:{ Exception -> 0x02bc }
            if (r4 == 0) goto L_0x02bb
            android.app.PendingIntent r5 = r4.intent     // Catch:{ Exception -> 0x02bc }
            r5.send()     // Catch:{ Exception -> 0x02bc }
        L_0x02bb:
            goto L_0x02d1
        L_0x02bc:
            r0 = move-exception
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Something wrong while close vpn: "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            android.util.Slog.d(r1, r3)
        L_0x02d1:
            com.android.server.pm.UserManagerService r0 = r8.mUserManager
            r0.setUserRestriction(r2, r10, r11)
            android.app.AppOpsManager r1 = r8.mAppOpsManager
            r2 = 47
            r5 = 0
            r3 = r10
            r4 = r8
            r6 = r11
            r1.setUserRestrictionForUser(r2, r3, r4, r5, r6)
        L_0x02e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.enterprise.RestrictionsManagerService.setRestriction(java.lang.String, boolean, int):void");
    }

    public int getControlStatus(String key, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, key, 1, userId);
    }

    public boolean hasRestriction(String key, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, key, 0, userId) == 1;
    }

    private void unmountPublicVolume(int volFlag) {
        final StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        VolumeInfo usbVol = null;
        Iterator it = storageManager.getVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            VolumeInfo vol = (VolumeInfo) it.next();
            if (vol.getType() == 0 && (vol.getDisk().flags & volFlag) == volFlag) {
                usbVol = vol;
                break;
            }
        }
        if (usbVol != null && usbVol.getState() == 2) {
            final String volId = usbVol.getId();
            new Thread(new Runnable() {
                public void run() {
                    storageManager.unmount(volId);
                }
            }).start();
        }
    }

    private void closeCloudBackup(int userId) {
        Account account = null;
        Account[] accounts = AccountManager.get(this.mContext).getAccountsByTypeAsUser(MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE, new UserHandle(userId));
        if (accounts.length > 0) {
            account = accounts[0];
        }
        if (account != null) {
            ContentValues values = new ContentValues();
            values.put("account_name", account.name);
            values.put("is_open", false);
            this.mContext.getContentResolver().update(ContentProvider.maybeAddUserId(Uri.parse("content://com.miui.micloud").buildUpon().appendPath("cloud_backup_info").build(), userId), values, (String) null, (String[]) null);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.cloudbackup", "com.miui.cloudbackup.service.CloudBackupService"));
            intent.setAction("close_cloud_back_up");
            this.mContext.startServiceAsUser(intent, new UserHandle(userId));
        }
    }

    private void setUsbFunction(UsbManager usbManager, String function) {
        try {
            Method method = UsbManager.class.getDeclaredMethod("setCurrentFunction", new Class[]{String.class});
            method.setAccessible(true);
            method.invoke(usbManager, new Object[]{function});
        } catch (Exception e) {
            Class<UsbManager> cls = UsbManager.class;
            try {
                Method method2 = cls.getDeclaredMethod("setCurrentFunction", new Class[]{String.class, Boolean.TYPE});
                method2.setAccessible(true);
                method2.invoke(usbManager, new Object[]{function, false});
            } catch (Exception e1) {
                Slog.d(TAG, "Failed to set usb function", e1);
            }
        }
    }
}
