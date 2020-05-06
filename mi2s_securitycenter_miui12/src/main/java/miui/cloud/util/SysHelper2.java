package miui.cloud.util;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import miui.cloud.CloudSyncUtils;

public class SysHelper2 extends SysHelper {
    public static Intent getCustomSyncSettings(Context context, Account account, String str) {
        Intent intent = new Intent(str + ".SYNC_SETTINGS");
        intent.putExtra("account", account);
        intent.putExtra(CloudSyncUtils.SYNC_AUTHORITY, str);
        if (context.getPackageManager().queryIntentActivities(intent, 32).isEmpty()) {
            return null;
        }
        return intent;
    }
}
