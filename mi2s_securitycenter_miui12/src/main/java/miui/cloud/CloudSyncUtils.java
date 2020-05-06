package miui.cloud;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import b.d.b.c.c;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;
import miui.cloud.sync.data.SyncSettingState;
import miui.cloud.util.AnalyticsHelper;
import miui.cloud.util.MiCloudSyncUtils;
import miui.cloud.util.SyncStateChangedHelper;
import miui.telephony.CloudTelephonyManager;

public class CloudSyncUtils {
    @Deprecated
    public static final String AUTHORITY_CLOUD_BACKUP = "micloud_cloud_backup";
    @Deprecated
    public static final String AUTHORITY_FIND_DEVICE = "micloud_find_device";
    @Deprecated
    public static final String CLOUD_APP_AUTHORITY = "com.miui.backup.cloud.CloudAppProvider";
    @Deprecated
    public static final String MMSLITE_PROVIDER_AUTHORITY = "com.xiaomi.mms.providers.SmsProvider";
    @Deprecated
    public static final String SYNC_AUTHORITY = "authority";
    @Deprecated
    public static final String SYNC_CHANGE_SOURCE = "change_source";
    @Deprecated
    public static final String SYNC_STATUS = "status";
    private static final String TAG = "CloudSyncUtils";

    @Deprecated
    public static void analyseActivateSource(Context context, String str, boolean z) {
        AnalyticsHelper.analyseActivateSource(context, str, z);
    }

    @Deprecated
    public static void analyseActivateSource(Context context, String str, boolean z, String str2) {
        AnalyticsHelper.analyseActivateSource(context, str, z, str2);
    }

    @Deprecated
    public static void analyseActivateSource(Context context, String str, boolean z, String str2, String str3) {
        AnalyticsHelper.analyseActivateSource(context, str, z, str2, str3);
    }

    @Deprecated
    public static void analyseMiCloudLoginEnable(Context context, boolean z, boolean z2, boolean z3) {
        AnalyticsHelper.analyseMiCloudLoginEnable(context, z, z2, z3);
    }

    @Deprecated
    public static void analyseMiCloudLoginEnable(Context context, boolean z, boolean z2, boolean z3, boolean z4) {
        AnalyticsHelper.analyseMiCloudLoginEnable(context, z, z2, z3, z4);
    }

    @Deprecated
    public static void clearAllSyncChangedLog(Context context) {
        SyncStateChangedHelper.clearAllSyncChangedLog(context);
    }

    @Deprecated
    public static void clearLocalWatermark(Context context, String str) {
    }

    @Deprecated
    public static void clearLocalWatermark(Context context, String str, long j, int i) {
    }

    @Deprecated
    public static void clearShouldPullAndUpdateLocalWatermark(Context context, long j, String str) {
    }

    @Deprecated
    public static void clearShouldPullAndUpdateLocalWatermark(Context context, long j, String str, long j2, int i) {
    }

    public static List<String> getAllAuthorities(Account account) {
        return getAllAuthorities(account, new String[0]);
    }

    public static List<String> getAllAuthorities(Account account, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        if (account == null) {
            return arrayList;
        }
        if (strArr == null) {
            strArr = new String[0];
        }
        for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypes()) {
            if (syncAdapterType.isUserVisible() && Constants.XIAOMI_ACCOUNT_TYPE.equals(syncAdapterType.accountType) && !Arrays.asList(strArr).contains(syncAdapterType.authority)) {
                arrayList.add(syncAdapterType.authority);
            }
        }
        return arrayList;
    }

    @Deprecated
    public static boolean getAutoSyncForSim(ContentResolver contentResolver, int i, Account account, String str) {
        return MiCloudSyncUtils.getAutoSyncForSim(contentResolver, i, account, str);
    }

    @Deprecated
    public static HashMap<String, SyncSettingState> getCurrentSyncSettingState(Context context) {
        return SyncStateChangedHelper.getCurrentSyncSettingState(context);
    }

    public static List<String> getEnabledAuthorities(Account account) {
        return getEnabledAuthorities(account, new String[0]);
    }

    public static List<String> getEnabledAuthorities(Account account, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        if (account == null) {
            return arrayList;
        }
        for (String next : getAllAuthorities(account, strArr)) {
            if (ContentResolver.getSyncAutomatically(account, next)) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static boolean getSyncEnabledWithActivation(Context context, Account account, String str, int i) {
        if (context == null || account == null) {
            throw new IllegalArgumentException("context or account is null");
        } else if (TextUtils.isEmpty(str) || !needActivate(str)) {
            throw new IllegalStateException("only support need activate authority");
        } else {
            int multiSimCount = CloudTelephonyManager.getMultiSimCount();
            if (i >= multiSimCount) {
                throw new IllegalArgumentException("simIndex must be smaller than simCount");
            } else if (i == -1) {
                throw new IllegalArgumentException(String.format("simIndex cannot be -1 when authority(%s) need activate", new Object[]{str}));
            } else if (!ContentResolver.getSyncAutomatically(account, str)) {
                return false;
            } else {
                if (multiSimCount > 1) {
                    return getAutoSyncForSim(context.getContentResolver(), i, account, str);
                }
                return true;
            }
        }
    }

    public static int getSyncPrefDefaultSimIndex(Context context, String str) {
        return getSyncPrefDefaultSimIndex(str);
    }

    public static int getSyncPrefDefaultSimIndex(String str) {
        if (!needActivate(str)) {
            return -1;
        }
        int defaultSlotId = CloudTelephonyManager.getDefaultSlotId();
        if (defaultSlotId == -1) {
            return 0;
        }
        return defaultSlotId;
    }

    public static boolean isAllSyncsOff(Context context) {
        Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(context);
        if (xiaomiAccount == null) {
            Log.d(TAG, "Account is null in isAllSyncsOff()");
            return true;
        }
        for (String syncAutomatically : getAllAuthorities(xiaomiAccount)) {
            if (ContentResolver.getSyncAutomatically(xiaomiAccount, syncAutomatically)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFindDeviceEnabled(Context context, Account account) {
        if (account != null) {
            return "true".equals(((AccountManager) context.getSystemService("account")).getUserData(account, Constants.UserData.KEY_FIND_DEVICE_ENABLED));
        }
        Log.d(TAG, "account is null in isFindDeviceEnabled()");
        return false;
    }

    @Deprecated
    public static boolean needActivate(String str) {
        return MiCloudSyncUtils.needActivate(str);
    }

    @Deprecated
    public static void setAutoSyncForSim(ContentResolver contentResolver, int i, Account account, String str, boolean z) {
        MiCloudSyncUtils.setAutoSyncForSim(contentResolver, i, account, str, z);
    }

    @Deprecated
    public static void setMiCloudSync(Context context, Account account, String str, String str2, boolean z) {
        SyncStateChangedHelper.setMiCloudSync(context, account, str, str2, z);
    }

    @Deprecated
    public static boolean setShouldPullFromServer(Context context, long j, String str, long j2) {
        return true;
    }

    @Deprecated
    public static void setSyncChanged(Context context, String str, String str2, boolean z) {
        SyncStateChangedHelper.setSyncChanged(context, str, str2, z);
    }

    @Deprecated
    public static void setSyncTag(Context context, String str, String str2) {
    }

    @Deprecated
    public static boolean shouldPullFromServer(Context context, String str) {
        return true;
    }

    @Deprecated
    public static boolean shouldPullFromServer(Context context, String str, long j, int i) {
        return true;
    }

    public static void startMiCloudMemberActivity(Context context, String str) {
        String str2 = c.f2136a ? "http://account.preview.n.xiaomi.net/pass/serviceLogin?callback=http%3A%2F%2Fmicloudweb.preview.n.xiaomi.com%2Fsts%3Fsign%3DLMx3DWB%252FO%252FtjMckaek2OtO0%252BkzQ%253D%26followup%3Dhttp%253A%252F%252Fmicloudweb.preview.n.xiaomi.com%252Fvip&sid=i.mi.com" : c.f2137b ? "https://account.xiaomi.com/pass/serviceLogin?callback=https%3A%2F%2Fdaily.i.mi.com%2Fsts%3Fsign%3DGTIyREMRa%252Bf1eVmlJubCFg%252FK3eA%253D%26followup%3Dhttps%253A%252F%252Fdaily.i.mi.com%252Fvip&sid=i.mi.com&_locale=zh_CN" : "https://account.xiaomi.com/pass/serviceLogin?callback=https%3A%2F%2Fi.mi.com%2Fsts%3Fsign%3Dn9zfyPtPHlxmLf0eYJmwASvEjEo%253D%26followup%3Dhttps%253A%252F%252Fi.mi.com%252Fvip&sid=i.mi.com";
        Bundle bundle = new Bundle();
        bundle.putString("extra_url", str2);
        bundle.putBoolean("extra_is_sso_url", true);
        if (TextUtils.isEmpty(str)) {
            bundle.putString("extra_membership_source", str);
        }
        Intent intent = new Intent("com.xiaomi.action.MICLOUD_MEMBER");
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
