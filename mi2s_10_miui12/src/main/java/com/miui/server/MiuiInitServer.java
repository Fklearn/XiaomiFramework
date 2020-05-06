package com.miui.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.miui.Manifest;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.server.pm.CloudControlPreinstallService;
import com.android.server.pm.DumpState;
import com.android.server.pm.PreinstallApp;
import com.android.server.wm.WindowManagerService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import miui.content.res.GlobalConfiguration;
import miui.os.Build;
import miui.os.IMiuiInit;
import miui.os.IMiuiInitObserver;
import miui.util.CustomizeUtil;
import miui.util.FeatureParser;
import miui.util.MiuiFeatureUtils;

public class MiuiInitServer extends IMiuiInit.Stub {
    private static final String CUST_PROPERTIES_FILE_NAME = "cust.prop";
    private static final String PREINSTALL_APP_HISTORY_FILE = "/data/app/preinstall_history";
    private static final String PREINSTALL_PACKAGE_LIST = "/data/system/preinstall.list";
    private static final String TAG = "MiuiInitServer";
    MiuiCompatModePackages mCompatModePackages;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public boolean mDoing;
    boolean mNeedAspectSettings = ((WindowManagerService) ServiceManager.getService("window")).hasNavigationBar(0);
    private HashMap<String, String> mPreinstallHistoryMap;
    private ArrayList<String> mPreinstalledChannels;

    public MiuiInitServer(Context context) {
        this.mContext = context;
        EnableStateManager.updateApplicationEnableState(this.mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        this.mContext.registerReceiver(new BootCompletedReceiver(), filter);
        MiuiFeatureUtils.setMiuisdkProperties();
        boolean z = false;
        if (this.mNeedAspectSettings && !Build.IS_TABLET) {
            z = true;
        }
        this.mNeedAspectSettings = z;
        if (this.mNeedAspectSettings || CustomizeUtil.HAS_NOTCH) {
            this.mCompatModePackages = new MiuiCompatModePackages(this.mContext);
        }
    }

    private final class BootCompletedReceiver extends BroadcastReceiver {
        private BootCompletedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(180000);
                    } catch (InterruptedException e) {
                    }
                    PreinstallApp.installNewUpdatedSystemPreinstallApps(MiuiInitServer.this.mContext);
                }
            }.start();
        }
    }

    private class InitCustEnvironmentTask extends Thread {
        private String mCustVarinat;
        private IMiuiInitObserver mObs;

        InitCustEnvironmentTask(String custVariant, IMiuiInitObserver obs) {
            this.mCustVarinat = custVariant;
            this.mObs = obs;
        }

        public void run() {
            boolean ret = initCustEnvironment(this.mCustVarinat);
            IMiuiInitObserver iMiuiInitObserver = this.mObs;
            if (iMiuiInitObserver != null) {
                try {
                    iMiuiInitObserver.initDone(ret);
                } catch (RemoteException e) {
                }
            }
            boolean unused = MiuiInitServer.this.mDoing = false;
            try {
                Configuration curConfig = GlobalConfiguration.get();
                GlobalConfiguration.getExtraConfig(curConfig).updateTheme(0);
                GlobalConfiguration.update(curConfig);
            } catch (RemoteException e2) {
                e2.printStackTrace();
            }
            EnableStateManager.updateApplicationEnableState(MiuiInitServer.this.mContext);
            MiuiInitServer.this.mContext.sendBroadcast(new Intent("miui.intent.action.MIUI_INIT_COMPLETED"), Manifest.permission.INIT_MIUI_ENVIRONMENT);
            Intent intent = new Intent("miui.intent.action.MIUI_REGION_CHANGED");
            intent.addFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
            intent.putExtra(CloudControlPreinstallService.ConnectEntity.REGION, Build.getRegion());
            MiuiInitServer.this.mContext.sendBroadcast(intent);
        }

        private boolean isTimeZoneAuto() {
            try {
                return Settings.Global.getInt(MiuiInitServer.this.mContext.getContentResolver(), "auto_time_zone") > 0;
            } catch (Settings.SettingNotFoundException e) {
                Log.i(MiuiInitServer.TAG, "AUTO_TIME_ZONE can't found : " + e);
                return false;
            }
        }

        private boolean isDeviceNotInProvision() {
            return Settings.Secure.getInt(MiuiInitServer.this.mContext.getContentResolver(), "device_provisioned", 0) != 0;
        }

        private boolean initCustEnvironment(String custVariant) {
            CustomizeUtil.setMiuiCustVariatDir(custVariant);
            File custVariantDir = CustomizeUtil.getMiuiCustVariantDir(true);
            if (custVariantDir == null) {
                return false;
            }
            importCustProperties(new File(custVariantDir, MiuiInitServer.CUST_PROPERTIES_FILE_NAME), isTimeZoneAuto());
            saveCustVariantToFile(custVariant);
            if (TextUtils.isEmpty(Settings.Global.getString(MiuiInitServer.this.mContext.getContentResolver(), "wifi_country_code"))) {
                String countryCode = Build.getRegion();
                if (!TextUtils.isEmpty(countryCode)) {
                    ((WifiManager) MiuiInitServer.this.mContext.getSystemService("wifi")).setCountryCode(countryCode);
                }
            }
            installVanwardCustApps();
            return custVariantDir.exists();
        }

        private void installVanwardCustApps() {
            PreinstallApp.installVanwardCustApps(MiuiInitServer.this.mContext);
        }

        private void importCustProperties(File custProp, boolean isTimezoneAuto) {
            if (custProp.exists()) {
                try {
                    FileReader fileReader = new FileReader(custProp);
                    BufferedReader bufferReader = new BufferedReader(fileReader);
                    while (true) {
                        String readLine = bufferReader.readLine();
                        String line = readLine;
                        if (readLine != null) {
                            String line2 = line.trim();
                            if (!line2.startsWith("#")) {
                                String[] ss = line2.split("=");
                                if (ss != null) {
                                    if (ss.length == 2) {
                                        if (!"persist.sys.timezone".equals(ss[0]) || !isTimezoneAuto || !isDeviceNotInProvision()) {
                                            SystemProperties.set(ss[0], ss[1]);
                                        } else {
                                            Log.i(MiuiInitServer.TAG, "persist.sys.timezone will not be changed when AUTO_TIME_ZONE is open!");
                                        }
                                    }
                                }
                            }
                        } else {
                            pokeSystemProperties();
                            bufferReader.close();
                            fileReader.close();
                            return;
                        }
                    }
                } catch (IOException e) {
                }
            }
        }

        private void pokeSystemProperties() {
            try {
                for (String service : ServiceManager.listServices()) {
                    IBinder obj = ServiceManager.checkService(service);
                    if (obj != null) {
                        Parcel data = Parcel.obtain();
                        try {
                            obj.transact(1599295570, data, (Parcel) null, 0);
                        } catch (RemoteException e) {
                        } catch (Exception e2) {
                            Log.i(MiuiInitServer.TAG, "Someone wrote a bad service '" + service + "' that doesn't like to be poked: " + e2);
                        }
                        data.recycle();
                    }
                }
            } catch (Exception e3) {
            }
        }

        private void saveCustVariantToFile(String custVariant) {
            File custVariantFile = CustomizeUtil.getMiuiCustVariantFile();
            try {
                if (!custVariantFile.exists()) {
                    custVariantFile.getParentFile().mkdirs();
                    custVariantFile.createNewFile();
                }
                FileWriter fileWriter = new FileWriter(custVariantFile, false);
                BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
                bufferWriter.write(custVariant);
                bufferWriter.close();
                fileWriter.close();
                custVariantFile.setReadable(true, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean initCustEnvironment(String custVariant, IMiuiInitObserver obs) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INIT_MIUI_ENVIRONMENT, (String) null);
        Slog.i(TAG, "check status, cust variant[" + custVariant + "]");
        synchronized (this) {
            if (this.mDoing) {
                Slog.w(TAG, "skip, initializing cust environment");
                return false;
            } else if (TextUtils.isEmpty(custVariant)) {
                Slog.w(TAG, "skip, cust variant[" + custVariant + "] is empty");
                return false;
            } else {
                this.mDoing = true;
                Slog.i(TAG, "initializing cust environment");
                new InitCustEnvironmentTask(custVariant, obs).start();
                return true;
            }
        }
    }

    public void installPreinstallApp() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INIT_MIUI_ENVIRONMENT, (String) null);
        PreinstallApp.installCustApps(this.mContext);
    }

    public String[] getCustVariants() throws RemoteException {
        ArrayList<String> regionList = new ArrayList<>();
        File cust = CustomizeUtil.getMiuiCustPropDir();
        String[] cs = Locale.getISOCountries();
        File[] resgions = cust.listFiles();
        if (resgions != null) {
            for (File region : resgions) {
                if (region.isDirectory()) {
                    String r = region.getName();
                    for (String c : cs) {
                        if (c.equalsIgnoreCase(r)) {
                            regionList.add(r);
                        }
                    }
                }
            }
        }
        return (String[]) regionList.toArray(new String[0]);
    }

    public void doFactoryReset(boolean keepUserApps) throws RemoteException {
        if (Build.IS_GLOBAL_BUILD) {
            CustomizeUtil.setMiuiCustVariatDir("");
            File file = CustomizeUtil.getMiuiCustVariantFile();
            if (file.exists()) {
                file.delete();
            }
        }
        if (!keepUserApps) {
            File file2 = new File(PREINSTALL_APP_HISTORY_FILE);
            if (file2.exists()) {
                file2.delete();
            }
        }
    }

    public boolean isPreinstalledPackage(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        return PreinstallApp.isPreinstalledPackage(pkg);
    }

    public boolean isPreinstalledPAIPackage(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return false;
        }
        return PreinstallApp.isPreinstalledPAIPackage(pkg);
    }

    public String getMiuiChannelPath(String pkg) {
        if (TextUtils.isEmpty(pkg) || !isPreinstalledPackage(pkg)) {
            return "";
        }
        if (this.mPreinstalledChannels == null) {
            this.mPreinstalledChannels = PreinstallApp.getPeinstalledChannelList();
        }
        Iterator<String> it = this.mPreinstalledChannels.iterator();
        while (it.hasNext()) {
            String channel = it.next();
            if (channel.contains(pkg) && new File(channel).exists()) {
                return channel;
            }
        }
        return "";
    }

    public void removeFromPreinstallList(String pkg) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INIT_MIUI_ENVIRONMENT, (String) null);
        if (!TextUtils.isEmpty(pkg)) {
            PreinstallApp.removeFromPreinstallList(pkg);
        }
    }

    public void removeFromPreinstallPAIList(String pkg) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INIT_MIUI_ENVIRONMENT, (String) null);
        if (!TextUtils.isEmpty(pkg)) {
            PreinstallApp.removeFromPreinstallPAIList(pkg);
        }
    }

    public void writePreinstallPAIPackage(String pkg) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INIT_MIUI_ENVIRONMENT, (String) null);
        if (!TextUtils.isEmpty(pkg)) {
            PreinstallApp.writePreinstallPAIPackage(pkg);
        }
    }

    public void copyPreinstallPAITrackingFile(String type, String fileName, String content) throws RemoteException {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.INIT_MIUI_ENVIRONMENT, (String) null);
        if (!TextUtils.isEmpty(type) && !TextUtils.isEmpty(content)) {
            PreinstallApp.copyPreinstallPAITrackingFile(type, fileName, content);
        }
    }

    public int getPreinstalledAppVersion(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return -1;
        }
        return PreinstallApp.getPreinstalledAppVersion(pkg);
    }

    public String getMiuiPreinstallAppPath(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return "";
        }
        if (this.mPreinstallHistoryMap == null) {
            this.mPreinstallHistoryMap = new HashMap<>();
            try {
                String[] pkgNameList = FeatureParser.getStringArray("system_data_packagename_list");
                String[] appPathList = FeatureParser.getStringArray("system_data_path_list");
                if (!(pkgNameList == null || appPathList == null || pkgNameList.length != appPathList.length)) {
                    for (int i = 0; i < pkgNameList.length; i++) {
                        this.mPreinstallHistoryMap.put(pkgNameList[i], appPathList[i]);
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "Error occurs while get miui preinstall app path + " + e);
            }
        }
        if (this.mPreinstallHistoryMap.get(pkg) == null) {
            return "";
        }
        return this.mPreinstallHistoryMap.get(pkg);
    }

    public void setRestrictAspect(String pkg, boolean restrict) {
        if (this.mNeedAspectSettings) {
            checkPermission("setRestrictAspect");
            this.mCompatModePackages.setRestrictAspect(pkg, restrict);
        }
    }

    public boolean isRestrictAspect(String packageName) {
        if (!this.mNeedAspectSettings) {
            return Build.VERSION.SDK_INT >= 26;
        }
        return this.mCompatModePackages.isRestrictAspect(packageName);
    }

    public float getAspectRatio(String pkg) {
        if (!this.mNeedAspectSettings) {
            return 3.0f;
        }
        return this.mCompatModePackages.getAspectRatio(pkg);
    }

    public int getDefaultAspectType(String pkg) {
        if (!this.mNeedAspectSettings) {
            return 0;
        }
        return this.mCompatModePackages.getDefaultAspectType(pkg);
    }

    public int getNotchConfig(String pkg) {
        if (!CustomizeUtil.HAS_NOTCH) {
            return 0;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            return this.mCompatModePackages.getNotchConfig(pkg);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public void setNotchSpecialMode(String pkg, boolean special) {
        if (CustomizeUtil.HAS_NOTCH) {
            checkPermission("setNotchSpecialMode");
            this.mCompatModePackages.setNotchSpecialMode(pkg, special);
        }
    }

    public void setCutoutMode(String pkg, int mode) {
        if (CustomizeUtil.HAS_NOTCH) {
            checkPermission("setCutoutMode");
            this.mCompatModePackages.setCutoutMode(pkg, mode);
        }
    }

    public int getCutoutMode(String pkg) {
        if (!CustomizeUtil.HAS_NOTCH) {
            return 0;
        }
        long identity = Binder.clearCallingIdentity();
        try {
            return this.mCompatModePackages.getCutoutMode(pkg);
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private void checkPermission(String reason) {
        if (this.mContext.checkCallingPermission("android.permission.SET_SCREEN_COMPATIBILITY") != 0) {
            throw new SecurityException("Permission Denial: " + reason + " pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        }
    }
}
