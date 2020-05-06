package com.miui.antispam.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import b.b.a.e.n;
import com.miui.securitycenter.R;

public class RemoveBlacklistActivity extends r {
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String[] f2570d;

    /* access modifiers changed from: private */
    public void c() {
        if (this.f2570d != null) {
            new W(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antispam.ui.activity.r, miui.app.Activity, android.app.Activity, com.miui.antispam.ui.activity.RemoveBlacklistActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (n.b((Activity) this)) {
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Intent intent = getIntent();
        this.f2570d = intent.getStringArrayExtra("numbers");
        if (intent.getBooleanExtra("needConfirm", false)) {
            new AlertDialog.Builder(this).setMessage(R.string.dlg_remove_blacklist).setPositiveButton(17039370, new V(this)).setNegativeButton(17039360, new U(this)).setOnCancelListener(new T(this)).create().show();
        } else {
            c();
        }
    }
}
