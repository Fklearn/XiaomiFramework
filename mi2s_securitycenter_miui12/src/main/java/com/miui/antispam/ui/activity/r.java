package com.miui.antispam.ui.activity;

import android.app.UiModeManager;
import android.os.Bundle;
import b.b.a.e.n;
import b.b.c.j.i;
import miui.app.Activity;

public abstract class r extends Activity {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f2610a = (n.a() == 7);

    /* renamed from: b  reason: collision with root package name */
    private boolean f2611b = false;

    /* renamed from: c  reason: collision with root package name */
    protected boolean f2612c = i.f();

    public boolean b() {
        return this.f2611b;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        r.super.onCreate(bundle);
        UiModeManager uiModeManager = (UiModeManager) getSystemService("uimode");
        if (uiModeManager != null) {
            this.f2611b = uiModeManager.getNightMode() == 2;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        r.super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        r.super.onResume();
    }
}
