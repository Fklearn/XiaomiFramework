package miui.cloud.util;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.util.Log;
import java.util.HashMap;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;

public class AnalyticsHelper {
    private static final String TAG = "AnalyticsHelper";

    public static void analyseActivateSource(Context context, String str, boolean z) {
        analyseActivateSource(context, str, z, "");
    }

    public static void analyseActivateSource(Context context, String str, boolean z, String str2) {
        analyseActivateSource(context, str, z, str2, "");
    }

    public static void analyseActivateSource(Context context, String str, boolean z, String str2, String str3) {
        HashMap hashMap = new HashMap();
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_ACTIVATE_SOURCE, str);
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_ACTIVATE_INIT, String.valueOf(isAllMiCloudSyncOff(context)));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_ACTIVATE_RESULT, String.valueOf(z));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_ACTIVATE_PACKAGE_NAME, str2);
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_ACTIVATE_AUTHORITY, str3);
        sendAnalyticsRequest(context, Constants.Analytics.EVENT_ID_MICLOUD_ACTIVATE_SOURCE, hashMap);
    }

    public static void analyseMiCloudLoginEnable(Context context, boolean z, boolean z2, boolean z3) {
        HashMap hashMap = new HashMap();
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_SETUP_GUIDE, String.valueOf(z));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_SYNC_ENABLE, String.valueOf(z2));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_FIND_DEVICE_ENABLE, String.valueOf(z3));
        sendAnalyticsRequest(context, Constants.Analytics.EVENT_ID_MICLOUD_LOGIN_ENABLE, hashMap);
    }

    public static void analyseMiCloudLoginEnable(Context context, boolean z, boolean z2, boolean z3, boolean z4) {
        HashMap hashMap = new HashMap();
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_SETUP_GUIDE, String.valueOf(z));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_SYNC_ENABLE, String.valueOf(z2));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_FIND_DEVICE_ENABLE, String.valueOf(z3));
        hashMap.put(Constants.Analytics.EVENT_KEY_MICLOUD_MX_ENABLE, String.valueOf(z4));
        sendAnalyticsRequest(context, Constants.Analytics.EVENT_ID_MICLOUD_LOGIN_ENABLE, hashMap);
    }

    private static Intent createAnalyticsIntent(Context context, String str, HashMap<String, String> hashMap) {
        Intent intent = new Intent(Constants.Analytics.ACTION);
        intent.putExtra(Constants.Analytics.EXTRA_ANALYTICS_EVENT_ID, Constants.Analytics.EVENT_ID_MICLOUD_ACTIVATE_SOURCE);
        intent.putExtra(Constants.Analytics.EXTRA_ANALYTICS_EVENT_PARAMETERS, hashMap);
        intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
        return intent;
    }

    private static boolean isAllMiCloudSyncOff(Context context) {
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            Log.d(TAG, "account is null in isAllMiCloudSyncOff()");
            return true;
        }
        for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypes()) {
            if (syncAdapterType.accountType.equals(Constants.XIAOMI_ACCOUNT_TYPE) && ContentResolver.getSyncAutomatically(xiaomiAccount, syncAdapterType.authority)) {
                return false;
            }
        }
        return true;
    }

    private static void sendAnalyticsRequest(Context context, String str, HashMap<String, String> hashMap) {
        context.sendBroadcast(createAnalyticsIntent(context, str, hashMap));
    }
}
