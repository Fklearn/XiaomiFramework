package com.miui.powercenter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.c.b;
import com.miui.superpower.b.k;
import miui.app.Activity;

public class PowerSettings extends b {
    public Fragment a() {
        return new x();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int intExtra;
        super.onCreate(bundle);
        k.a((Activity) this);
        Intent intent = getIntent();
        if (intent != null && (intExtra = intent.getIntExtra("extra_settings_title_res", -1)) != -1) {
            setTitle(intExtra);
        }
    }
}
