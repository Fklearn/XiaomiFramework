package com.miui.permcenter.privacymanager.b;

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

class d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AlertDialog f6351a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f6352b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Activity f6353c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ String f6354d;
    final /* synthetic */ long e;
    final /* synthetic */ n.c f;

    d(AlertDialog alertDialog, int i, Activity activity, String str, long j, n.c cVar) {
        this.f6351a = alertDialog;
        this.f6352b = i;
        this.f6353c = activity;
        this.f6354d = str;
        this.e = j;
        this.f = cVar;
    }

    public void onClick(View view) {
        int i;
        this.f6351a.dismiss();
        int i2 = this.f6352b;
        switch (view.getId()) {
            case R.id.select_allow /*2131297640*/:
                i = 3;
                break;
            case R.id.select_ask /*2131297641*/:
                i2 = 2;
                break;
            case R.id.select_deny /*2131297642*/:
                i = 1;
                break;
            case R.id.select_foreground /*2131297644*/:
                i2 = 6;
                break;
            case R.id.select_virtual /*2131297645*/:
                i = 7;
                break;
        }
        i = i2;
        if (i != this.f6352b) {
            try {
                ApplicationInfo applicationInfo = this.f6353c.getPackageManager().getApplicationInfo(this.f6354d, 8192);
                if (i == 3 || !n.a(this.e) || applicationInfo == null || applicationInfo.targetSdkVersion >= 23) {
                    boolean z = i == 7;
                    PermissionManagerCompat.setApplicationPermission(PermissionManager.getInstance(this.f6353c), this.e, z ? 3 : i, 2, this.f6354d);
                    if (PermissionManager.virtualMap.containsKey(Long.valueOf(this.e))) {
                        PermissionManagerCompat.setApplicationPermission(PermissionManager.getInstance(this.f6353c), PermissionManager.virtualMap.get(Long.valueOf(this.e)).longValue(), z ? 1 : 3, 2, this.f6354d);
                    }
                    if (this.f != null) {
                        this.f.a(this.f6354d, i);
                        return;
                    }
                    return;
                }
                new AlertDialog.Builder(this.f6353c).setMessage(R.string.old_sdk_deny_warning).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.ok, new n.a(this.f6353c, this.f6354d, i, this.e, this.f)).show();
            } catch (Exception e2) {
                Log.e("IntlPermDialogHelper", "getApplicationInfo", e2);
            }
        }
    }
}
