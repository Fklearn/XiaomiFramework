package com.miui.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.miui.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.android.server.MiuiNetworkManagementService;
import com.android.server.pm.DumpState;
import com.android.server.pm.Settings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class RestrictAppNetManager {
    private static final int RULE_ALLOW = 0;
    private static final int RULE_RESTRICT = 1;
    private static final String TAG = "RestrictAppNetManager";
    private static final int TYPE_ALL = 3;
    private static final Uri URI_CLOUD_DEVICE_RELEASED_NOTIFY = Uri.parse("content://com.android.settings.cloud.CloudSettings/device_released");
    private static BroadcastReceiver mAppInstallReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String pkgName = intent.getDataString().substring(8);
            boolean replacing = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
            int uid = intent.getIntExtra("android.intent.extra.UID", 0);
            if (RestrictAppNetManager.sService != null && !TextUtils.isEmpty(pkgName) && uid > 0 && !replacing && !RestrictAppNetManager.isAllowAccessInternet(pkgName)) {
                RestrictAppNetManager.tryDownloadCloudData(context);
                Log.i(RestrictAppNetManager.TAG, "RULE_RESTRICT packageName: " + pkgName);
                RestrictAppNetManager.sService.setMiuiFirewallRule(pkgName, uid, 1, 3);
            }
        }
    };
    private static long sLastUpdateTime = 0;
    private static HashSet<String> sReleasedDeviceList = new HashSet<>();
    private static ArrayList<String> sRestrictedAppListBeforeRelease;
    /* access modifiers changed from: private */
    public static MiuiNetworkManagementService sService;

    static {
        sReleasedDeviceList.add("gemini");
        sReleasedDeviceList.add("scorpio");
        sReleasedDeviceList.add("capricorn");
        sReleasedDeviceList.add("lithium");
        sReleasedDeviceList.add("natrium");
        sReleasedDeviceList.add("sagit");
        sReleasedDeviceList.add("riva");
        sReleasedDeviceList.add("cactus");
        sReleasedDeviceList.add("cereus");
        sReleasedDeviceList.add("jason");
        sReleasedDeviceList.add("rosy");
        sReleasedDeviceList.add("daisy");
        sReleasedDeviceList.add("sakura");
        sReleasedDeviceList.add("sakura_india");
        sReleasedDeviceList.add("tiffany");
        sReleasedDeviceList.add("tissot");
        sReleasedDeviceList.add("wayne");
        sReleasedDeviceList.add("jasmine");
        sReleasedDeviceList.add("platina");
        sReleasedDeviceList.add("chiron");
        sReleasedDeviceList.add("polaris");
        sReleasedDeviceList.add("clover");
        sReleasedDeviceList.add("dipper");
        sReleasedDeviceList.add("beryllium");
        sReleasedDeviceList.add("equuleus");
        sReleasedDeviceList.add("sirius");
        sReleasedDeviceList.add("nitrogen");
        sReleasedDeviceList.add("perseus");
        sReleasedDeviceList.add("ysl");
        sReleasedDeviceList.add("vince");
        sReleasedDeviceList.add("whyred");
        sReleasedDeviceList.add("tulip");
        sReleasedDeviceList.add("ursa");
        sReleasedDeviceList.add("lotus");
        sReleasedDeviceList.add("lavender");
        sReleasedDeviceList.add("cepheus");
        sReleasedDeviceList.add("grus");
        sReleasedDeviceList.add("violet");
        sReleasedDeviceList.add("onc");
        sReleasedDeviceList.add("davinci");
        sReleasedDeviceList.add("raphael");
        sReleasedDeviceList.add("pine");
        sReleasedDeviceList.add("laurus");
        sReleasedDeviceList.add("pyxis");
        sReleasedDeviceList.add("vela");
        sReleasedDeviceList.add("davinciin");
        sReleasedDeviceList.add("raphaelin");
        sReleasedDeviceList.add("begonia");
        sReleasedDeviceList.add("ginkgo");
        sReleasedDeviceList.add("crux");
        sReleasedDeviceList.add("draco");
        sReleasedDeviceList.add("begoniain");
        sReleasedDeviceList.add("phoenix");
        sReleasedDeviceList.add("vangogh");
    }

    static boolean isRestrictedAppNet(Context context, String packageName) {
        return false;
    }

    static void init(Context context) {
        boolean hasReleased = SystemProperties.getBoolean("persist.sys.released", false);
        Log.i(TAG, "init released : " + hasReleased);
        if (!hasReleased) {
            if (sReleasedDeviceList.contains(Build.DEVICE)) {
                Log.i(TAG, "Device is released");
                SystemProperties.set("persist.sys.released", "true");
            } else if (Build.VERSION.SDK_INT < 28) {
                SystemProperties.set("persist.sys.released", "true");
            } else {
                sService = MiuiNetworkManagementService.getInstance();
                registerCloudDataObserver(context);
                registerCloudDataObserver1(context);
                registerAppInstallReceiver(context);
                sRestrictedAppListBeforeRelease = new ArrayList<>();
                sRestrictedAppListBeforeRelease.add("com.antutu.ABenchMark");
                sRestrictedAppListBeforeRelease.add("com.antutu.ABenchMark5");
                sRestrictedAppListBeforeRelease.add("com.antutu.benchmark.bench64");
                sRestrictedAppListBeforeRelease.add("com.antutu.videobench");
                sRestrictedAppListBeforeRelease.add("com.antutu.ABenchMark.GL2");
                sRestrictedAppListBeforeRelease.add("com.antutu.tester");
                sRestrictedAppListBeforeRelease.add("com.antutu.benchmark.full");
                sRestrictedAppListBeforeRelease.add("com.music.videogame");
                sRestrictedAppListBeforeRelease.add("com.ludashi.benchmark");
                sRestrictedAppListBeforeRelease.add("com.ludashi.benchmarkhd");
                sRestrictedAppListBeforeRelease.add("com.qihoo360.ludashi.cooling");
                sRestrictedAppListBeforeRelease.add("cn.opda.android.activity");
                sRestrictedAppListBeforeRelease.add("com.shouji.cesupaofen");
                sRestrictedAppListBeforeRelease.add("com.colola.mobiletest");
                sRestrictedAppListBeforeRelease.add("ws.j7uxli.a6urcd");
                sRestrictedAppListBeforeRelease.add("com.gamebench.metricscollector");
                sRestrictedAppListBeforeRelease.add("com.huahua.test");
                sRestrictedAppListBeforeRelease.add("com.futuremark.dmandroid.application");
                sRestrictedAppListBeforeRelease.add("com.eembc.coremark");
                sRestrictedAppListBeforeRelease.add("com.rightware.BasemarkOSII");
                sRestrictedAppListBeforeRelease.add("com.glbenchmark.glbenchmark27");
                sRestrictedAppListBeforeRelease.add("com.greenecomputing.linpack");
                sRestrictedAppListBeforeRelease.add("eu.chainfire.cfbench");
                sRestrictedAppListBeforeRelease.add("com.primatelabs.geekbench");
                sRestrictedAppListBeforeRelease.add("com.primatelabs.geekbench3");
                sRestrictedAppListBeforeRelease.add("com.quicinc.vellamo");
                sRestrictedAppListBeforeRelease.add("com.aurorasoftworks.quadrant.ui.advanced");
                sRestrictedAppListBeforeRelease.add("com.aurorasoftworks.quadrant.ui.standard");
                sRestrictedAppListBeforeRelease.add("eu.chainfire.perfmon");
                sRestrictedAppListBeforeRelease.add("com.evozi.deviceid");
                sRestrictedAppListBeforeRelease.add("com.finalwire.aida64");
                sRestrictedAppListBeforeRelease.add("com.cpuid.cpu_z");
                sRestrictedAppListBeforeRelease.add("rs.in.luka.android.pi");
                sRestrictedAppListBeforeRelease.add("com.uzywpq.cqlzahm");
                sRestrictedAppListBeforeRelease.add("com.xidige.androidinfo");
                sRestrictedAppListBeforeRelease.add("com.appems.hawkeye");
                sRestrictedAppListBeforeRelease.add("com.tyyj89.androidsuperinfo");
                sRestrictedAppListBeforeRelease.add("com.ft1gp");
                sRestrictedAppListBeforeRelease.add("ws.k6t2we.b4zyjdjv");
                sRestrictedAppListBeforeRelease.add("com.myapp.dongxie_app1");
                sRestrictedAppListBeforeRelease.add("com.shoujijiance.zj");
                sRestrictedAppListBeforeRelease.add("com.qrj.test");
                sRestrictedAppListBeforeRelease.add("com.appems.testonetest");
                updateFirewallRule(context, 1);
            }
        }
    }

    /* access modifiers changed from: private */
    public static boolean isAllowAccessInternet(String packageName) {
        ArrayList<String> arrayList;
        if (SystemProperties.getBoolean("persist.sys.released", false) || (arrayList = sRestrictedAppListBeforeRelease) == null) {
            return true;
        }
        return true ^ arrayList.contains(packageName);
    }

    /* access modifiers changed from: private */
    public static void updateFirewallRule(Context context, int rule) {
        if (sRestrictedAppListBeforeRelease != null && sService != null) {
            Log.i(TAG, "updateFirewallRule : " + rule);
            Iterator<String> it = sRestrictedAppListBeforeRelease.iterator();
            while (it.hasNext()) {
                String pkgName = it.next();
                int uid = getUidByPackageName(context, pkgName);
                if (uid >= 0) {
                    sService.setMiuiFirewallRule(pkgName, uid, rule, 3);
                }
            }
        }
    }

    private static void registerCloudDataObserver(final Context context) {
        context.getContentResolver().registerContentObserver(MiuiSettings.SettingsCloudData.getCloudDataNotifyUri(), true, new ContentObserver(BackgroundThread.getHandler()) {
            public void onChange(boolean selfChange) {
                RestrictAppNetManager.updateRestrictAppNetProp(context);
            }
        });
    }

    private static void registerCloudDataObserver1(final Context context) {
        context.getContentResolver().registerContentObserver(URI_CLOUD_DEVICE_RELEASED_NOTIFY, true, new ContentObserver(BackgroundThread.getHandler()) {
            public void onChange(boolean selfChange) {
                Log.i(RestrictAppNetManager.TAG, "registerCloudDataObserver1");
                RestrictAppNetManager.updateFirewallRule(context, 0);
            }
        });
    }

    private static void registerAppInstallReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme(Settings.ATTR_PACKAGE);
        context.registerReceiver(mAppInstallReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    public static void updateRestrictAppNetProp(Context context) {
        try {
            if (!SystemProperties.getBoolean("persist.sys.released", false)) {
                Log.i(TAG, "updateRestrictAppNetProp");
                String deviceMode = Build.DEVICE;
                List<MiuiSettings.SettingsCloudData.CloudData> dataList = MiuiSettings.SettingsCloudData.getCloudDataList(context.getContentResolver(), "RestrictAppControl");
                if (dataList == null) {
                    return;
                }
                if (dataList.size() != 0) {
                    for (MiuiSettings.SettingsCloudData.CloudData data : dataList) {
                        if ("released".equals(data.getString(deviceMode, (String) null))) {
                            SystemProperties.set("persist.sys.released", "true");
                            updateFirewallRule(context, 0);
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "update released prop exception", e);
        }
    }

    private static int getUidByPackageName(Context context, String pkgName) {
        try {
            return context.getPackageManager().getApplicationInfo(pkgName, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "not find packageName :" + pkgName);
            return -1;
        }
    }

    /* access modifiers changed from: private */
    public static void tryDownloadCloudData(Context context) {
        if (System.currentTimeMillis() - sLastUpdateTime > 86400000) {
            sLastUpdateTime = System.currentTimeMillis();
            Intent intent = new Intent("com.miui.action.UPDATE_RESTRICT_APP_DATA");
            if (Build.VERSION.SDK_INT >= 26) {
                intent.setFlags(DumpState.DUMP_SERVICE_PERMISSIONS);
            }
            context.sendBroadcastAsUser(intent, UserHandle.OWNER, Manifest.permission.UPDATE_RESTRICT_DATA);
            Log.w(TAG, "send： com.miui.action.UPDATE_RESTRICT_APP_DATA");
        }
    }
}
