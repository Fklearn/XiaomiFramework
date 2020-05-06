package com.miui.permcenter.install;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import miui.yellowpage.Log;

class p implements AccountManagerCallback<Bundle> {
    p() {
    }

    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        Log.d("XiaomiAccountUtils", "login done");
    }
}
