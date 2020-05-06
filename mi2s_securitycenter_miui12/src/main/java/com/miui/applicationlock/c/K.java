package com.miui.applicationlock.c;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import b.b.c.j.j;
import java.util.ArrayList;
import java.util.Collections;
import miui.cloud.Constants;

public class K {
    public static String a(Context context) {
        String d2 = d(context);
        if (!TextUtils.isEmpty(d2)) {
            return j.d(d2.getBytes());
        }
        return null;
    }

    public static void a(Activity activity, Bundle bundle) {
        if (activity != null) {
            AccountManager.get(activity).addAccount(Constants.XIAOMI_ACCOUNT_TYPE, "passportapi", (String[]) null, bundle, activity, new J(activity), (Handler) null);
        }
    }

    public static void a(Activity activity, Bundle bundle, C0259c cVar) {
        AccountManager.get(activity).addAccount(Constants.XIAOMI_ACCOUNT_TYPE, "passportapi", (String[]) null, bundle, activity, new I(cVar, activity), (Handler) null);
    }

    public static Account b(Context context) {
        ArrayList arrayList = new ArrayList();
        Collections.addAll(arrayList, AccountManager.get(context).getAccountsByType(Constants.XIAOMI_ACCOUNT_TYPE));
        if (!arrayList.isEmpty()) {
            return (Account) arrayList.get(0);
        }
        return null;
    }

    public static boolean c(Context context) {
        return d(context) != null;
    }

    public static String d(Context context) {
        Account b2 = b(context);
        if (b2 == null) {
            return null;
        }
        return b2.name;
    }
}
