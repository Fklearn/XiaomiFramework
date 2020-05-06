package com.miui.powercenter.autotask;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import b.b.c.c.a;
import miui.R;
import miui.app.ActionBar;

public abstract class B extends a {

    /* renamed from: a  reason: collision with root package name */
    protected ImageView f6690a;

    /* renamed from: b  reason: collision with root package name */
    protected ImageView f6691b;

    /* JADX WARNING: type inference failed for: r4v0, types: [com.miui.powercenter.autotask.B, android.content.Context, miui.app.Activity] */
    private void o() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(m());
            this.f6690a = new ImageView(this);
            this.f6690a.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.f6690a.setImageResource(R.drawable.action_mode_immersion_done_light);
            actionBar.setEndView(this.f6690a);
            this.f6690a.setOnClickListener(l());
            this.f6691b = new ImageView(this);
            this.f6691b.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            this.f6691b.setImageResource(R.drawable.action_mode_immersion_close_light);
            actionBar.setStartView(this.f6691b);
            this.f6691b.setOnClickListener(l());
        }
    }

    /* access modifiers changed from: protected */
    public abstract View.OnClickListener l();

    /* access modifiers changed from: protected */
    public abstract String m();

    /* access modifiers changed from: protected */
    public abstract void n();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        o();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return B.super.onKeyDown(i, keyEvent);
        }
        n();
        return true;
    }
}
