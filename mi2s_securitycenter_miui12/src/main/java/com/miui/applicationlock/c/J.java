package com.miui.applicationlock.c;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.google.android.exoplayer2.C;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;
import java.io.UnsupportedEncodingException;

class J implements AccountManagerCallback<Bundle> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f3290a;

    J(Activity activity) {
        this.f3290a = activity;
    }

    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
        try {
            if (accountManagerFuture.getResult().getBoolean("booleanResult")) {
                String str = null;
                try {
                    str = Base64.encodeToString(K.d(this.f3290a).getBytes(C.UTF8_NAME), 0);
                } catch (UnsupportedEncodingException e) {
                    Log.i("XiaomiAccountUtils", e.toString());
                }
                b.b("gb_xiaomi_id_md5_key", str);
                Toast.makeText(this.f3290a, this.f3290a.getResources().getString(R.string.bind_xiaomi_account_success), 1).show();
            }
        } catch (Exception unused) {
            Log.e("XiaomiAccountUtils", "forgetPrivacyPassword error,e");
        }
    }
}
