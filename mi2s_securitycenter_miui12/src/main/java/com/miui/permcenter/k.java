package com.miui.permcenter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.view.View;
import com.miui.permcenter.compact.PermissionManagerCompat;
import com.miui.permcenter.n;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

class k implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AlertDialog f6172a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f6173b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Activity f6174c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f6175d;
    final /* synthetic */ long e;
    final /* synthetic */ n.c f;

    k(AlertDialog alertDialog, int i, Activity activity, String str, long j, n.c cVar) {
        this.f6172a = alertDialog;
        this.f6173b = i;
        this.f6174c = activity;
        this.f6175d = str;
        this.e = j;
        this.f = cVar;
    }

    public void onClick(View view) {
        this.f6172a.dismiss();
        int i = this.f6173b;
        switch (view.getId()) {
            case R.id.select_allow /*2131297640*/:
                i = 3;
                break;
            case R.id.select_ask /*2131297641*/:
                i = 2;
                break;
            case R.id.select_deny /*2131297642*/:
                i = 1;
                break;
            case R.id.select_foreground /*2131297644*/:
                i = 6;
                break;
            case R.id.select_virtual /*2131297645*/:
                i = 7;
                break;
        }
        if (i != this.f6173b) {
            try {
                ApplicationInfo applicationInfo = this.f6174c.getPackageManager().getApplicationInfo(this.f6175d, 8192);
                if (i == 3 || i == 7 || !n.a(this.e) || applicationInfo == null || applicationInfo.targetSdkVersion >= 23) {
                    PermissionManagerCompat.setApplicationPermissionWithVirtual(PermissionManager.getInstance(this.f6174c), this.e, i, 2, this.f6175d);
                    if (this.f != null) {
                        this.f.a(this.f6175d, i);
                        return;
                    }
                    return;
                }
                new AlertDialog.Builder(this.f6174c).setMessage(R.string.old_sdk_deny_warning).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok, new n.a(this.f6174c, this.f6175d, i, this.e, this.f)).show();
            } catch (Exception e2) {
                Log.e("PermissionUtils", "getApplicationInfo", e2);
            }
        }
    }
}
