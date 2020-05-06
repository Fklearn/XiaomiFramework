package com.miui.gamebooster.ui;

import android.app.Activity;
import android.os.Bundle;
import b.b.c.c.a;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.F;
import com.miui.gamebooster.m.Q;
import com.miui.gamebooster.m.fa;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;
import miui.os.Build;

public class WonderfulMomentActivity extends a {
    /* JADX WARNING: type inference failed for: r1v0, types: [b.b.c.c.a, com.miui.gamebooster.ui.WonderfulMomentActivity, miui.app.Activity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setTheme(R.style.GBWonderfulMomentVideo);
        super.onCreate(bundle);
        if (Build.IS_INTERNATIONAL_BUILD || !C0388t.t()) {
            finish();
        } else if (!F.a(getIntent().getStringExtra("gamePkg"))) {
            finish();
        } else {
            Q.a((Activity) this);
            setContentView(R.layout.gb_activity_manual_record_setting);
            na.a((Activity) this);
            fa.a(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        C0373d.a();
    }
}
