package com.miui.cleanmaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class CleanMasterInstallActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra("cleanMasterAction");
        if (!TextUtils.isEmpty(stringExtra)) {
            Intent intent = new Intent(stringExtra);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                intent.putExtras(extras);
            }
            intent.setPackage("com.miui.cleanmaster");
            g.b(this, intent);
        } else {
            Intent intent2 = new Intent("miui.intent.action.GARBAGE_CLEANUP");
            intent2.setPackage("com.miui.cleanmaster");
            g.b(this, intent2);
        }
        finish();
    }
}
