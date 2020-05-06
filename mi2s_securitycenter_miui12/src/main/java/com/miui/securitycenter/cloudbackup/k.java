package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.util.Log;
import com.xiaomi.settingsdk.backup.ICloudBackup;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import org.json.JSONObject;

public class k implements ICloudBackup {
    public int getCurrentVersion(Context context) {
        return 1;
    }

    public void onBackupSettings(Context context, DataPackage dataPackage) {
        Log.v("SecurityCenterSettingsCloudBackupService", "start settings backup. ");
        dataPackage.addKeyJson("SecurityCenter", j.a(context));
        dataPackage.addKeyJson("AntivirusSettings", d.a(context));
        dataPackage.addKeyJson("PowerSettings", h.a(context));
        dataPackage.addKeyJson("GeneralSettings", g.a(context));
        dataPackage.addKeyJson("SafePaySettings", i.a(context));
        dataPackage.addKeyJson("AntiSpamSettings", b.a(context));
        dataPackage.addKeyJson("AppManagerSettings", a.a(context));
        dataPackage.addKeyJson("GameBoosterSettings", f.a(context));
        dataPackage.addKeyJson("EarthquakeWarningSettings", e.a(context));
        Log.v("SecurityCenterSettingsCloudBackupService", "end settings backup. ");
    }

    public void onRestoreSettings(Context context, DataPackage dataPackage, int i) {
        Log.v("SecurityCenterSettingsCloudBackupService", "start settings restore. ");
        if (dataPackage != null) {
            if (dataPackage.get("SecurityCenter") != null) {
                j.a(context, (JSONObject) dataPackage.get("SecurityCenter").getValue());
            }
            if (dataPackage.get("AntivirusSettings") != null) {
                d.a(context, (JSONObject) dataPackage.get("AntivirusSettings").getValue());
            }
            if (dataPackage.get("PowerSettings") != null) {
                h.a(context, (JSONObject) dataPackage.get("PowerSettings").getValue());
            }
            if (dataPackage.get("GeneralSettings") != null) {
                g.a(context, (JSONObject) dataPackage.get("GeneralSettings").getValue());
            }
            if (dataPackage.get("SafePaySettings") != null) {
                i.a(context, (JSONObject) dataPackage.get("SafePaySettings").getValue());
            }
            if (dataPackage.get("AntiSpamSettings") != null) {
                b.a(context, (JSONObject) dataPackage.get("AntiSpamSettings").getValue());
            }
            if (dataPackage.get("AppManagerSettings") != null) {
                a.a(context, (JSONObject) dataPackage.get("AppManagerSettings").getValue());
            }
            if (dataPackage.get("GameBoosterSettings") != null) {
                f.a(context, (JSONObject) dataPackage.get("GameBoosterSettings").getValue());
            }
            if (dataPackage.get("EarthquakeWarningSettings") != null) {
                e.a(context, (JSONObject) dataPackage.get("EarthquakeWarningSettings").getValue());
            }
        }
        Log.v("SecurityCenterSettingsCloudBackupService", "end settings restore. ");
    }
}
