package com.miui.applicationlock;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import b.b.c.j.A;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0259c;
import com.miui.applicationlock.c.K;
import com.miui.securitycenter.R;

/* renamed from: com.miui.applicationlock.ra  reason: case insensitive filesystem */
class C0297ra implements AccountManagerCallback<Bundle> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0259c f3375a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Activity f3376b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ConfirmAccountActivity f3377c;

    C0297ra(ConfirmAccountActivity confirmAccountActivity, C0259c cVar, Activity activity) {
        this.f3377c = confirmAccountActivity;
        this.f3375a = cVar;
        this.f3376b = activity;
    }

    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            if (accountManagerFuture.getResult().getBoolean("booleanResult")) {
                h.a(this.f3377c.f ? "app_binding_result" : "binding_result", "not_logged_binding");
                this.f3375a.a(K.d(this.f3376b));
                A.a((Context) this.f3376b, this.f3376b.getResources().getString(R.string.bind_xiaomi_account_success));
                Intent intent = new Intent(this.f3376b, PrivacyAndAppLockManageActivity.class);
                intent.putExtra("extra_data", "not_home_start");
                this.f3377c.setResult(-1, intent);
                this.f3376b.finish();
                return;
            }
            this.f3375a.a((String) null);
        } catch (Exception unused) {
            Log.e("ConfirmAccountActivity", "applicationlock error,e");
            boolean unused2 = this.f3377c.g = true;
        }
    }
}
