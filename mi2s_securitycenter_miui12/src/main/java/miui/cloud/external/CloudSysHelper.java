package miui.cloud.external;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.ArrayMap;
import android.util.Log;
import b.d.b.c.g;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;
import miui.cloud.os.MultiuserUtils;
import miui.cloud.util.DeviceFeatureUtils;
import miui.cloud.util.SysHelper;

public class CloudSysHelper {
    private static final Map<String, String> MAIN_SYNCS_WITH_PKG = new ArrayMap();
    private static final String TAG = "CloudSysHelper";

    static {
        MAIN_SYNCS_WITH_PKG.put("sms", "com.android.mms");
        MAIN_SYNCS_WITH_PKG.put("com.android.contacts", "com.android.contacts");
        MAIN_SYNCS_WITH_PKG.put("call_log", "com.android.contacts");
    }

    public static boolean isAllMiCloudSyncOff(Context context) {
        return isMiCloudMainSyncItemsOff(context);
    }

    public static boolean isMiCloudMainSyncItemsOff(Context context) {
        String str;
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            str = "Account is null in isMainSyncsOff()";
        } else if (!ContentResolver.getMasterSyncAutomatically()) {
            str = "Master sync is off in isMainSyncsOff()";
        } else {
            List<String> a2 = g.a(context, xiaomiAccount);
            ArrayList arrayList = new ArrayList();
            PackageManager packageManager = context.getPackageManager();
            for (Map.Entry next : MAIN_SYNCS_WITH_PKG.entrySet()) {
                String str2 = (String) next.getKey();
                try {
                    packageManager.getApplicationInfo((String) next.getValue(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "isMiCloudMainSyncItemsOff: ", e);
                    arrayList.add(str2);
                }
            }
            if (DeviceFeatureUtils.hasDeviceFeature("support_google_csp_sync")) {
                arrayList.remove("com.android.contacts");
                arrayList.remove("call_log");
                arrayList.remove("sms");
            }
            a2.removeAll(arrayList);
            if (!SysHelper.hasTelephonyFeature(context)) {
                a2.remove("call_log");
                a2.remove("sms");
            }
            if (MultiuserUtils.myUserId() != MultiuserUtils.get_USER_OWNER()) {
                a2.remove("sms");
            }
            for (String syncAutomatically : a2) {
                if (ContentResolver.getSyncAutomatically(xiaomiAccount, syncAutomatically)) {
                    return false;
                }
            }
            str = "all available authorities sync off";
        }
        Log.d(TAG, str);
        return true;
    }

    public static boolean isXiaomiAccountPresent(Context context) {
        return ExtraAccountManager.getXiaomiAccount(context) != null;
    }

    public static void promptEnableAllMiCloudSync(Context context) {
        startMiCloudInfoSettingsAcitivity(context);
    }

    public static void promptEnableFindDevice(Context context) {
        startMiCloudInfoSettingsAcitivity(context);
    }

    public static void startMiCloudInfoSettingsAcitivity(Context context) {
        Intent intent = new Intent(Constants.Intents.ACTION_MICLOUD_INFO_SETTINGS);
        intent.addFlags(268435456);
        context.startActivity(intent);
    }
}
