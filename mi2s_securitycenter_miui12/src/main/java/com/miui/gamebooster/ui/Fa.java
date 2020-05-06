package com.miui.gamebooster.ui;

import android.app.ActionBar;
import android.view.View;
import b.b.c.c.b.d;
import b.c.a.b.f.c;
import com.miui.gamebooster.m.na;
import com.miui.securitycenter.R;

public class Fa extends d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private c f4876a;

    public void a(Object obj) {
        if (obj instanceof c) {
            this.f4876a = (c) obj;
        }
    }

    /* access modifiers changed from: protected */
    public void initView() {
        View findViewById = findViewById(R.id.backBtn);
        if (findViewById != null) {
            if (na.c()) {
                findViewById.setRotation(180.0f);
            }
            findViewById.setOnClickListener(this);
        }
    }

    public void onClick(View view) {
        c cVar;
        if (R.id.backBtn == view.getId() && (cVar = this.f4876a) != null) {
            cVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public int onCreateViewLayout() {
        return R.layout.gb_fragment_wonderful_moment_setting;
    }

    /* access modifiers changed from: protected */
    public int onCustomizeActionBar(ActionBar actionBar) {
        return 0;
    }
}
