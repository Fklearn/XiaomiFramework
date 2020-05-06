package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.app.AppGlobals;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import com.miui.enterprise.RestrictionsHelper;
import java.util.ArrayList;

public class SyncSecurityInjector {
    private static final String CLOUD_MANAGER_PERMISSION = "com.xiaomi.permission.CLOUD_MANAGER";
    private static final String TAG = "SyncSecurityInjector";
    private static final String XIAOMI_ACCOUNT_TYPE = "com.xiaomi";

    public static AccountAndUser[] filterOutXiaomiAccount(AccountAndUser[] accountAndUsers, int reason) {
        if (accountAndUsers == null) {
            if (!Log.isLoggable(TAG, 2)) {
                return null;
            }
            Slog.i(TAG, "filterOutXiaomiAccount: null accountAndUsers, abort. ");
            return null;
        } else if (reason < 0) {
            if (Log.isLoggable(TAG, 2)) {
                Slog.i(TAG, "filterOutXiaomiAccount: internal request, abort. ");
            }
            return accountAndUsers;
        } else {
            int appId = UserHandle.getAppId(reason);
            if (appId < 10000) {
                if (Log.isLoggable(TAG, 2)) {
                    Slog.i(TAG, "filterOutXiaomiAccount: system request, abort. ");
                }
                return accountAndUsers;
            }
            try {
                if (AppGlobals.getPackageManager().checkUidPermission(CLOUD_MANAGER_PERMISSION, appId) == 0) {
                    if (Log.isLoggable(TAG, 2)) {
                        Slog.i(TAG, "filterOutXiaomiAccount: CLOUD MANAGER, abort. ");
                    }
                    return accountAndUsers;
                }
            } catch (RemoteException e) {
            }
            if (Log.isLoggable(TAG, 2)) {
                Slog.i(TAG, "filterOutXiaomiAccount: go. ");
            }
            ArrayList<AccountAndUser> filtered = new ArrayList<>();
            for (AccountAndUser au : accountAndUsers) {
                if (au == null || au.account == null || !"com.xiaomi".equals(au.account.type)) {
                    filtered.add(au);
                }
            }
            return (AccountAndUser[]) filtered.toArray(new AccountAndUser[0]);
        }
    }

    public static boolean permitControlSyncForAccount(Context context, Account account) {
        int pid = Binder.getCallingPid();
        int uid = Binder.getCallingUid();
        if (RestrictionsHelper.hasRestriction(context, "disallow_auto_sync")) {
            Slog.d("Enterprise", "Deny sync control");
            return false;
        } else if (uid < 10000) {
            if (Log.isLoggable(TAG, 2)) {
                Slog.i(TAG, "Permit sync control for account " + getAccountType(account) + " by pid " + pid + ". SYSTEM UID. ");
            }
            return true;
        } else if (account == null || !"com.xiaomi".equals(account.type)) {
            if (Log.isLoggable(TAG, 2)) {
                Slog.i(TAG, "Permit sync control for account " + getAccountType(account) + " by pid " + pid + ". OTHER ACCOUNT. ");
            }
            return true;
        } else if (context.checkCallingOrSelfPermission(CLOUD_MANAGER_PERMISSION) == 0) {
            if (Log.isLoggable(TAG, 2)) {
                Slog.i(TAG, "Permit sync control for account " + getAccountType(account) + " by pid " + pid + ". CLOUD MANAGER. ");
            }
            return true;
        } else {
            if (Log.isLoggable(TAG, 2)) {
                Slog.i(TAG, "Deny sync control for account " + getAccountType(account) + " by pid " + pid + ". ");
            }
            return false;
        }
    }

    private static String getAccountType(Account account) {
        if (account == null) {
            return "[NULL]";
        }
        return "[" + account.type + "]";
    }
}
