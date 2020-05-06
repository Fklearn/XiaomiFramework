package com.miui.permcenter.permissions;

import android.os.Bundle;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import b.b.c.c.a;
import b.b.o.g.d;

public class SecondPermissionAppsActivity extends a {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            getFragmentManager().beginTransaction().add(16908290, new E()).commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        SecondPermissionAppsActivity.super.onDestroy();
        d.a("SecondPermissionAppsActivity", (Object) (InputMethodManager) getApplicationContext().getSystemService("input_method"), "windowDismissed", (Class<?>[]) new Class[]{IBinder.class}, getWindow().getDecorView().getWindowToken());
    }
}
