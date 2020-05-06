package com.android.server;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.server.am.ProcessManagerService;
import com.android.server.display.ScreenEffectService;
import com.miui.enterprise.settings.EnterpriseSettings;
import com.miui.server.BackupManagerService;
import com.miui.server.MiuiInitServer;
import com.miui.server.PerfShielderService;
import com.miui.server.SecurityManagerService;
import com.miui.server.enterprise.EnterpriseManagerService;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import miui.log.SystemLogSwitchesConfigManager;
import miui.mqsas.sdk.BootEventManager;
import miui.util.ObjectReference;
import miui.util.ReflectionUtils;

class SystemServerInjector {
    private static final boolean DEBUG = true;
    private static final String RUNTIME_REBOOT_PROPERTIY = "sys.miui.runtime.reboot";
    private static final String TAG = "SystemServerI";
    private static Set<String> sVersionPolicyDevices = new HashSet();
    private static final SystemLogSwitchesConfigReceiver systemLogSwitchesReceiver = new SystemLogSwitchesConfigReceiver();

    SystemServerInjector() {
    }

    static {
        addDeviceName("cepheus");
        addDeviceName("onc");
        addDeviceName("onclite");
        addDeviceName("lavender");
        addDeviceName("grus");
        addDeviceName("violet");
        addDeviceName("davinci");
        addDeviceName("raphael");
        addDeviceName("davinciin");
        addDeviceName("raphaelin");
        addDeviceName("andromeda");
        addDeviceName("pavo");
        addDeviceName("crux");
        addDeviceName("pyxis");
        addDeviceName("vela");
        addDeviceName("begonia");
        addDeviceName("begoniain");
        addDeviceName("pine");
        addDeviceName("olive");
        addDeviceName("olivelite");
        addDeviceName("olivewood");
        addDeviceName("ginkgo");
        addDeviceName("willow");
        addDeviceName("tucana");
        addDeviceName("phoenix");
        addDeviceName("phoenixin");
        addDeviceName("picasso");
        addDeviceName("picassoin");
    }

    static void addDeviceName(String name) {
        sVersionPolicyDevices.add(name);
        Set<String> set = sVersionPolicyDevices;
        set.add(name + "_ru");
        Set<String> set2 = sVersionPolicyDevices;
        set2.add(name + "_eea");
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.server.SecurityManagerService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v1, types: [com.miui.server.MiuiInitServer, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v2, types: [android.os.IBinder, com.miui.server.BackupManagerService] */
    /* JADX WARNING: type inference failed for: r0v3, types: [android.os.IBinder, com.android.server.LocationPolicyManagerService] */
    /* JADX WARNING: type inference failed for: r0v4, types: [com.miui.server.PerfShielderService, android.os.IBinder] */
    /* JADX WARNING: type inference failed for: r0v5, types: [com.android.server.am.ProcessManagerService, android.os.IBinder] */
    static final void addExtraServices(Context context, boolean onlyCore) {
        ServiceManager.addService("security", new SecurityManagerService(context, onlyCore));
        ServiceManager.addService("MiuiInit", new MiuiInitServer(context));
        ServiceManager.addService("MiuiBackup", new BackupManagerService(context));
        ServiceManager.addService("locationpolicy", LocationPolicyManagerService.getDefaultService());
        ServiceManager.addService(PerfShielderService.SERVICE_NAME, new PerfShielderService(context));
        ServiceManager.addService("ProcessManager", new ProcessManagerService(context));
        try {
            Class<?> whetstoneService = ReflectionUtils.findClass("com.miui.whetstone.server.WhetstoneActivityManagerService", (ClassLoader) null);
            IBinder whetstoneInstance = (IBinder) ReflectionUtils.tryNewInstance(whetstoneService, new Object[]{context});
            ObjectReference<String> whetstoneServiceName = ReflectionUtils.tryGetStaticObjectField(whetstoneService, "SERVICE", String.class);
            if (!(whetstoneInstance == null || whetstoneServiceName == null)) {
                ServiceManager.addService((String) whetstoneServiceName.get(), whetstoneInstance);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 26) {
            try {
                Class<?> tidaService = Class.forName("com.miui.server.TidaService");
                Constructor<?> ctor = tidaService.getConstructor(new Class[]{Context.class});
                Field nameField = tidaService.getDeclaredField("SERVICE_NAME");
                if (!(ctor == null || nameField == null)) {
                    String name = (String) nameField.get((Object) null);
                    ServiceManager.addService(name, (IBinder) ctor.newInstance(new Object[]{context}));
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        ScreenEffectService.startScreenEffectService();
        MiuiFgThread.initialMiuiFgThread();
        if (EnterpriseSettings.ENTERPRISE_ACTIVATED) {
            EnterpriseManagerService.init(context);
        }
    }

    static void enableLogSwitch() {
        SystemLogSwitchesConfigManager.enableLogSwitch(true);
        SystemLogSwitchesConfigManager.updateProgramName();
    }

    static void registerSystemLogSwitchesReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("miui.intent.action.SWITCH_ON_MIUILOGS");
        filter.addAction("miui.intent.action.SWITCH_OFF_MIUILOGS");
        filter.addAction("miui.intent.action.REVERT_MIUILOG_SWITCHES");
        context.registerReceiver(systemLogSwitchesReceiver, filter);
    }

    static void markSystemRun(long time) {
        try {
            SystemProperties.set(RUNTIME_REBOOT_PROPERTIY, String.valueOf(SystemProperties.getInt(RUNTIME_REBOOT_PROPERTIY, -1) + 1));
        } catch (Exception e) {
            Slog.i("SystemServer", "failed to set runtime reboot count", e);
        }
        BootEventManager.getInstance().setZygotePreload(SystemClock.uptimeMillis() - time);
        BootEventManager.getInstance().setSystemRun(time);
        if ("file".equals(SystemProperties.get("ro.crypto.type")) || "trigger_restart_framework".equals(SystemProperties.get("vold.decrypt"))) {
            enforceVersionPolicy();
        }
    }

    private static void rebootIntoRecovery() {
        BcbUtil.setupBcb("--show_version_mismatch\n");
        SystemProperties.set("sys.powerctl", "reboot,recovery");
    }

    private static boolean isGlobalHaredware(String product) {
        String country = SystemProperties.get("ro.boot.hwc");
        if ("CN".equals(country)) {
            return false;
        }
        if (country == null || !country.startsWith("CN_")) {
            return true;
        }
        return false;
    }

    private static void enforceVersionPolicy() {
        String product = SystemProperties.get("ro.product.name");
        if (!sVersionPolicyDevices.contains(product) && SystemProperties.getInt("ro.product.first_api_level", 0) < 29) {
            Slog.d(TAG, "enforceVersionPolicy: enable_flash_global enabled");
        } else if (!"locked".equals(SystemProperties.get("ro.secureboot.lockstate"))) {
            Slog.d(TAG, "enforceVersionPolicy: device unlocked");
        } else if (isGlobalHaredware(product)) {
            Slog.d(TAG, "enforceVersionPolicy: global device");
        } else if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            Slog.e(TAG, "CN hardware can't run Global build; reboot into recovery!!!");
            rebootIntoRecovery();
        }
    }

    static void markPmsScan(long startTime, long endTime) {
        BootEventManager.getInstance().setPmsScanStart(startTime);
        BootEventManager.getInstance().setPmsScanEnd(endTime);
    }

    static void markBootDexopt(long startTime, long endTime) {
        BootEventManager.getInstance().setBootDexopt(endTime - startTime);
    }
}
