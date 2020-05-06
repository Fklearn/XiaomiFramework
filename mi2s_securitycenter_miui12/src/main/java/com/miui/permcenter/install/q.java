package com.miui.permcenter.install;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import miui.cloud.Constants;

public class q {
    public static String a(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType(Constants.XIAOMI_ACCOUNT_TYPE);
        if (accountsByType == null || accountsByType.length <= 0) {
            return null;
        }
        return accountsByType[0].name;
    }

    public static void a(Activity activity, Bundle bundle) {
        AccountManager.get(activity).addAccount(Constants.XIAOMI_ACCOUNT_TYPE, "passportapi", (String[]) null, bundle, activity, new p(), (Handler) null);
    }
}
