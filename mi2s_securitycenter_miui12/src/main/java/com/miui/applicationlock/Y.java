package com.miui.applicationlock;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

class Y implements AccountManagerCallback<Bundle> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3235a;

    Y(ConfirmAccessControl confirmAccessControl) {
        this.f3235a = confirmAccessControl;
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            boolean z = accountManagerFuture.getResult().getBoolean("booleanResult");
            Intent intent = new Intent(this.f3235a, ResetChooseAccessControl.class);
            intent.putExtra("extra_data", "ModifyPassword");
            intent.putExtra("forgot_password_reset", true);
            this.f3235a.a(z, intent);
        } catch (Exception e) {
            Log.w("ConfirmAccessControl", "Fail to varify", e);
        }
    }
}
