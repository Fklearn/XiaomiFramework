package com.miui.antispam.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import b.b.a.e.q;
import com.miui.securitycenter.R;

/* renamed from: com.miui.antispam.ui.activity.s  reason: case insensitive filesystem */
public abstract class C0224s extends r {
    /* access modifiers changed from: protected */
    public abstract Fragment c();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!q.b()) {
            setContentView(R.layout.antispam_xpace_layout);
        } else if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, c()).commit();
        }
    }
}
