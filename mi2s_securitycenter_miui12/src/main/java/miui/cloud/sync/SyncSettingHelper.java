package miui.cloud.sync;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import miui.accounts.ExtraAccountManager;
import miui.cloud.Constants;

public class SyncSettingHelper {
    public static void openFindDeviceSettingUI(Activity activity) {
        if (ExtraAccountManager.getXiaomiAccount(activity) == null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("show_sync_settings", true);
            AccountManager.get(activity).addAccount(Constants.XIAOMI_ACCOUNT_TYPE, (String) null, (String[]) null, bundle, activity, (AccountManagerCallback) null, (Handler) null);
            return;
        }
        Intent intent = new Intent(Constants.Intents.ACTION_FIND_DEVICE_GUIDE);
        intent.putExtra("extra_micloud_find_device_guide_source", activity.getPackageName());
        intent.setPackage(Constants.CLOUDSERVICE_PACKAGE_NAME);
        activity.startActivity(intent);
    }
}
