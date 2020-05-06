package com.miui.server.enterprise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IPowerManager;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.util.Slog;
import com.android.server.pm.PackageManagerService;
import com.android.server.voiceinteraction.DatabaseHelper;
import com.miui.enterprise.IDeviceManager;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import miui.util.ScreenshotUtils;

public class DeviceManagerService extends IDeviceManager.Stub {
    private static final String TAG = "Enterprise-device";
    private Context mContext;
    private ServiceHandler mServiceHandler;
    /* access modifiers changed from: private */
    public WifiManager mWifiManager = ((WifiManager) this.mContext.getSystemService("wifi"));

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
        }
    }

    DeviceManagerService(Context context) {
        this.mContext = context;
        HandlerThread thread = new HandlerThread("DeviceManagerService");
        thread.start();
        this.mServiceHandler = new ServiceHandler(thread.getLooper());
    }

    public boolean isDeviceRoot() {
        ServiceUtils.checkPermission(this.mContext);
        return new File("/system/xbin/su").exists() || new File("/system/bin/su").exists();
    }

    public void deviceShutDown() {
        ServiceUtils.checkPermission(this.mContext);
        try {
            IPowerManager.Stub.asInterface(ServiceManager.getService("power")).shutdown(false, (String) null, false);
        } catch (RemoteException e) {
        }
    }

    public void deviceReboot() {
        ServiceUtils.checkPermission(this.mContext);
        try {
            IPowerManager.Stub.asInterface(ServiceManager.getService("power")).reboot(false, (String) null, true);
        } catch (RemoteException e) {
        }
    }

    public void formatSdCard() {
        ServiceUtils.checkPermission(this.mContext);
        final StorageManager storageManager = (StorageManager) this.mContext.getSystemService(StorageManager.class);
        VolumeInfo usbVol = null;
        Iterator it = storageManager.getVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            VolumeInfo vol = (VolumeInfo) it.next();
            if (vol.getType() == 0 && (vol.getDisk().flags & 4) == 4) {
                usbVol = vol;
                break;
            }
        }
        if (usbVol != null && usbVol.getState() == 2) {
            final String volId = usbVol.getId();
            new Thread(new Runnable() {
                public void run() {
                    storageManager.format(volId);
                }
            }).start();
        }
    }

    public void setUrlWhiteList(List<String> urls, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_url_white_list", EnterpriseSettings.generateListSettings(urls), userId);
    }

    public void setUrlBlackList(List<String> urls, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_url_white_list", EnterpriseSettings.generateListSettings(urls), userId);
    }

    public List<String> getUrlWhiteList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_url_white_list", userId));
    }

    public List<String> getUrlBlackList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_url_black_list", userId));
    }

    public void recoveryFactory(boolean formatSdcard) {
        ServiceUtils.checkPermission(this.mContext);
        Intent factoryResetIntent = new Intent("android.intent.action.MASTER_CLEAR");
        factoryResetIntent.putExtra("format_sdcard", formatSdcard);
        if (Build.VERSION.SDK_INT >= 26) {
            factoryResetIntent.setAction("android.intent.action.FACTORY_RESET");
            factoryResetIntent.setPackage(PackageManagerService.PLATFORM_PACKAGE_NAME);
        }
        factoryResetIntent.addFlags(268435456);
        this.mContext.sendBroadcast(factoryResetIntent);
    }

    public void setWifiConnRestriction(int mode, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_wifi_conn_restriction_mode", mode, userId);
        rebootWifi();
    }

    public int getWifiConnRestriction(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_wifi_conn_restriction_mode", 0);
    }

    public void setWifiApSsidWhiteList(List<String> ssids, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_wifi_ap_ssid_white_list", EnterpriseSettings.generateListSettings(ssids), userId);
        rebootWifi();
    }

    public void setWifiApBssidWhiteList(List<String> bssid, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_wifi_ap_bssid_white_list", EnterpriseSettings.generateListSettings(bssid), userId);
        rebootWifi();
    }

    public void setWifiApSsidBlackList(List<String> ssids, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_wifi_ap_ssid_black_list", EnterpriseSettings.generateListSettings(ssids), userId);
        rebootWifi();
    }

    public void setWifiApBssidBlackList(List<String> bssid, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_wifi_ap_bssid_black_list", EnterpriseSettings.generateListSettings(bssid), userId);
        rebootWifi();
    }

    public List<String> getWifiApSsidWhiteList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_wifi_ap_ssid_white_list", userId));
    }

    public List<String> getWifiApBssidWhiteList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_wifi_ap_bssid_white_list", userId));
    }

    public List<String> getWifiApSsidBlackList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_wifi_ap_ssid_black_list", userId));
    }

    public List<String> getWifiApBssidBlackList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_wifi_ap_bssid_black_list", userId));
    }

    public void setRingerMode(int ringerMode) {
        ServiceUtils.checkPermission(this.mContext);
        ((AudioManager) this.mContext.getSystemService("audio")).setRingerMode(ringerMode);
    }

    public void setBrowserRestriction(int mode, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_host_restriction_mode", mode, userId);
    }

    public Bitmap captureScreen() {
        ServiceUtils.checkPermission(this.mContext);
        Bitmap bitmap = ScreenshotUtils.getScreenshot(this.mContext);
        if (bitmap == null) {
            return bitmap;
        }
        Bitmap original = bitmap;
        Bitmap bitmap2 = original.copy(Bitmap.Config.ARGB_8888, false);
        original.recycle();
        bitmap2.setHasAlpha(false);
        bitmap2.prepareToDraw();
        return bitmap2;
    }

    public void setIpRestriction(int mode, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_ip_restriction_mode", mode, userId);
    }

    public void setIpWhiteList(List<String> list, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_ip_white_list", EnterpriseSettings.generateListSettings(list), userId);
    }

    public void setIpBlackList(List<String> list, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_ip_black_list", EnterpriseSettings.generateListSettings(list), userId);
    }

    public List<String> getIpWhiteList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_ip_white_list", userId));
    }

    public List<String> getIpBlackList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_ip_black_list", userId));
    }

    public void enableUsbDebug(boolean enable) {
        ServiceUtils.checkPermission(this.mContext);
        Settings.Global.putInt(this.mContext.getContentResolver(), "adb_enabled", enable);
    }

    public boolean setBootAnimation(String path) {
        try {
            ServiceUtils.checkPermission(this.mContext);
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.enterprise.service.EntInstallService"));
                Bundle bundle = new Bundle();
                bundle.putString("apkPath", path);
                bundle.putString(DatabaseHelper.SoundModelContract.KEY_TYPE, "BootAnimation");
                intent.putExtras(bundle);
                this.mContext.startService(intent);
                return true;
            } catch (Exception e) {
                Slog.e(TAG, "setBootAnimation", e);
                return true;
            }
        } catch (SecurityException e2) {
            Slog.e(TAG, "Uid " + Binder.getCallingUid() + " has no permission to access this API", e2);
            return false;
        }
    }

    private void rebootWifi() {
        if (this.mWifiManager.isWifiEnabled()) {
            this.mWifiManager.setWifiEnabled(false);
            this.mServiceHandler.postDelayed(new Runnable() {
                public void run() {
                    DeviceManagerService.this.mWifiManager.setWifiEnabled(true);
                }
            }, 1000);
        }
    }

    public void setDefaultHome(String pkgName) {
        ServiceUtils.checkPermission(this.mContext);
        boolean isPkgInstalled = false;
        ResolveInfo packageInfo = null;
        PackageManager pm = this.mContext.getPackageManager();
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.HOME");
        List<ResolveInfo> lists = pm.queryIntentActivities(homeIntent, 131072);
        ComponentName[] set = new ComponentName[lists.size()];
        int bestMatch = 0;
        for (int i = 0; i < lists.size(); i++) {
            ResolveInfo item = lists.get(i);
            if (pkgName.equals(item.activityInfo.packageName)) {
                isPkgInstalled = true;
                packageInfo = item;
            }
            set[i] = new ComponentName(item.activityInfo.packageName, item.activityInfo.name);
            if (item.match > bestMatch) {
                bestMatch = item.match;
            }
        }
        pm.clearPackagePreferredActivities(pm.resolveActivity(homeIntent, 0).activityInfo.packageName);
        if (isPkgInstalled && packageInfo != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MAIN");
            intentFilter.addCategory("android.intent.category.HOME");
            intentFilter.addCategory("android.intent.category.DEFAULT");
            intentFilter.addCategory("android.intent.category.BROWSABLE");
            pm.addPreferredActivity(intentFilter, bestMatch, set, new ComponentName(packageInfo.activityInfo.packageName, packageInfo.activityInfo.name));
            pm.replacePreferredActivity(intentFilter, bestMatch, set, new ComponentName(packageInfo.activityInfo.packageName, packageInfo.activityInfo.name));
            EnterpriseSettings.putString(this.mContext, "ep_default_home", pkgName);
        }
    }

    public String getDefaultHome() {
        return EnterpriseSettings.getString(this.mContext, "ep_default_home");
    }

    public void setWallPaper(String path) {
        ServiceUtils.checkPermission(this.mContext);
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.enterprise.service.EntInstallService"));
            Bundle bundle = new Bundle();
            bundle.putString("apkPath", path);
            bundle.putString(DatabaseHelper.SoundModelContract.KEY_TYPE, "WallPaper");
            intent.putExtras(bundle);
            this.mContext.startService(intent);
        } catch (Exception e) {
            Slog.e(TAG, "setWallPaper", e);
        }
    }

    public void setLockWallPaper(String path) {
        ServiceUtils.checkPermission(this.mContext);
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.enterprise.service.EntInstallService"));
            Bundle bundle = new Bundle();
            bundle.putString("apkPath", path);
            bundle.putString(DatabaseHelper.SoundModelContract.KEY_TYPE, "LockWallPaper");
            intent.putExtras(bundle);
            this.mContext.startService(intent);
        } catch (Exception e) {
            Slog.e(TAG, "setWallPaper", e);
        }
    }
}
