package com.android.server.notification;

import android.app.AppOpsManager;
import android.content.Context;
import android.provider.MiuiSettings;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import com.android.server.pm.PackageManagerService;
import com.miui.server.AccessController;

public class ZenModeHelperInjector {
    private ZenModeHelperInjector() {
    }

    static int applyRingerModeToZen(ZenModeHelper helper, Context context, int ringerMode) {
        int zenMode = helper.getZenMode();
        if (ringerMode != 0) {
            int newZen = 1;
            if ((ringerMode != 1 && ringerMode != 2) || zenMode == 0 || zenMode == 1) {
                return -1;
            }
            if (!MiuiSettings.AntiSpam.isQuietModeEnable(context)) {
                newZen = 0;
            }
            return newZen;
        } else if (zenMode == 3 || zenMode == 2) {
            return -1;
        } else {
            return 3;
        }
    }

    static int applyRingerModeToZen(ZenModeHelper helper, Context context, int ringerModeOld, int ringerModeNew, int newZen) {
        if (!MiuiSettings.SilenceMode.isSupported) {
            return applyRingerModeToZen(helper, context, ringerModeNew);
        }
        boolean isChange = ringerModeNew != ringerModeOld;
        int zenMode = helper.getZenMode();
        if (ringerModeNew == 0 || ringerModeNew == 1) {
            if (isChange) {
                if (zenMode == 0) {
                    return 4;
                }
                if (4 == zenMode) {
                    return -1;
                }
                return newZen;
            } else if (newZen != 0) {
                return newZen;
            } else {
                int newZen2 = zenMode;
                Log.d("ZenModeHelperInjector", "RINGER MODE is not Change");
                return newZen2;
            }
        } else if (ringerModeNew == 2 && isChange && zenMode == 4) {
            return 0;
        } else {
            return newZen;
        }
    }

    static void applyMiuiRestrictions(ZenModeHelper helper, AppOpsManager mAppOps) {
        boolean allowNotification;
        boolean allowRingtone;
        if (MiuiSettings.SilenceMode.isSupported) {
            String[] defaultException = {"com.android.cellbroadcastreceiver"};
            String[] exceptionPackages = {AccessController.PACKAGE_SYSTEMUI, PackageManagerService.PLATFORM_PACKAGE_NAME, "com.android.cellbroadcastreceiver", "com.android.server.telecom"};
            int mode = helper.getZenMode();
            ZenModeConfig config = helper.getConfig();
            boolean hasException = false;
            boolean z = true;
            if (mode != 1) {
                allowNotification = true;
                allowRingtone = true;
            } else {
                allowRingtone = false;
                allowNotification = false;
                if (!config.allowCalls && !config.allowRepeatCallers) {
                    z = false;
                }
                hasException = z;
            }
            applyRestriction(allowRingtone, 6, mAppOps, hasException ? exceptionPackages : defaultException);
            applyRestriction(allowNotification, 5, mAppOps, hasException ? exceptionPackages : defaultException);
        }
    }

    private static void applyRestriction(boolean allow, int usage, AppOpsManager appOps, String[] exception) {
        appOps.setRestriction(28, usage, allow ^ true ? 1 : 0, exception);
        appOps.setRestriction(3, usage, allow ^ true ? 1 : 0, exception);
    }

    static int getOutRingerMode(int newZen, int curZen, int ringerModeNew, int out) {
        if (!MiuiSettings.SilenceMode.isSupported) {
            return out;
        }
        return (newZen == -1 ? curZen : newZen) == 1 ? out : ringerModeNew;
    }

    private static int zenSeverity(int zen) {
        if (zen == 1) {
            return 1;
        }
        if (zen == 2) {
            return 3;
        }
        if (zen != 3) {
            return 0;
        }
        return 2;
    }

    static int miuiComputeZenMode(String reason, ZenModeConfig config) {
        int zen = 0;
        if (config == null) {
            return 0;
        }
        if (config.manualRule != null && !"conditionChanged".equals(reason) && !"setNotificationPolicy".equals(reason) && !"updateAutomaticZenRule".equals(reason) && !"onSystemReady".equals(reason) && !"readXml".equals(reason) && !"init".equals(reason) && !"zmc.onServiceAdded".equals(reason) && !"cleanUpZenRules".equals(reason)) {
            return config.manualRule.zenMode;
        }
        if (config.manualRule != null) {
            zen = config.manualRule.zenMode;
        }
        for (ZenModeConfig.ZenRule automaticRule : config.automaticRules.values()) {
            if (automaticRule.isAutomaticActive() && (zenSeverity(automaticRule.zenMode) > zenSeverity(zen) || (automaticRule.zenMode == 4 && zen == 0))) {
                zen = automaticRule.zenMode;
            }
        }
        return zen;
    }
}
