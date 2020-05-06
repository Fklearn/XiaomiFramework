package com.miui.privacyapps.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.applicationlock.PrivacyAppsConfirmAccessControl;

public class PrivacyAppsHelper extends a {
    /* JADX WARNING: type inference failed for: r0v0, types: [com.miui.privacyapps.ui.PrivacyAppsHelper, android.content.Context, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        PrivacyAppsHelper.super.onActivityResult(i, i2, intent);
        if (i == 102) {
            if (i2 == -1) {
                startActivity(new Intent(this, PrivacyAppsActivity.class));
            }
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.privacyapps.ui.PrivacyAppsHelper, b.b.c.c.a, android.content.Context, miui.app.Activity] */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        if (new b.b.k.b.a(this).a() > 0) {
            startActivityForResult(new Intent(this, PrivacyAppsConfirmAccessControl.class), 102);
        } else {
            finish();
        }
    }
}
