package com.miui.applicationlock.c;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.miui.securitycenter.R;

class I implements AccountManagerCallback<Bundle> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0259c f3288a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Activity f3289b;

    I(C0259c cVar, Activity activity) {
        this.f3288a = cVar;
        this.f3289b = activity;
    }

    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            if (accountManagerFuture.getResult().getBoolean("booleanResult")) {
                this.f3288a.a(K.d(this.f3289b));
                Toast.makeText(this.f3289b, this.f3289b.getResources().getString(R.string.bind_xiaomi_account_success), 1).show();
                return;
            }
            this.f3288a.a((String) null);
        } catch (Exception unused) {
            Log.e("XiaomiAccountUtils", "forgetPrivacyPassword error,e");
        }
    }
}
