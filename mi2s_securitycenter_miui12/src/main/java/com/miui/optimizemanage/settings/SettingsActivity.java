package com.miui.optimizemanage.settings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.optimizemanage.memoryclean.b;

public class SettingsActivity extends a {
    private Fragment m() {
        return new g();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.optimizemanage.settings.SettingsActivity] */
    public int l() {
        return b.a(this);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int intExtra;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (!(intent == null || (intExtra = intent.getIntExtra("extra_settings_title_res", -1)) == -1)) {
            setTitle(intExtra);
        }
        getFragmentManager().beginTransaction().replace(16908290, m()).commit();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Fragment findFragmentById = getFragmentManager().findFragmentById(16908290);
        if (findFragmentById != null) {
            ((g) findFragmentById).a();
        }
    }
}
