package com.android.server.content;

import android.accounts.Account;
import com.android.server.content.MiSyncConstants;

public class SyncOperationInjector {
    public static int compareByXiaomiPriority(SyncOperation operation1, SyncOperation operation2) {
        int priority1 = -1;
        if (isXiaomiAccount(operation1.target.account)) {
            priority1 = getXiaomiAuthorityPriority(operation1.target.provider);
        }
        int priority2 = -1;
        if (isXiaomiAccount(operation2.target.account)) {
            priority2 = getXiaomiAuthorityPriority(operation2.target.provider);
        }
        if (priority1 > priority2) {
            return -1;
        }
        if (priority1 < priority2) {
            return 1;
        }
        return 0;
    }

    private static boolean isXiaomiAccount(Account account) {
        if (account != null && MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE.equals(account.type)) {
            return true;
        }
        return false;
    }

    private static int getXiaomiAuthorityPriority(String authority) {
        if ("wifi".equals(authority)) {
            return 60;
        }
        if ("com.android.contacts".equals(authority)) {
            return 50;
        }
        if ("call_log".equals(authority)) {
            return 40;
        }
        if ("sms".equals(authority)) {
            return 30;
        }
        if ("notes".equals(authority)) {
            return 20;
        }
        if ("com.miui.gallery.cloud.provider".equals(authority)) {
            return 10;
        }
        return 0;
    }
}
