package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.view.ActionMode;

class x implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionMode f2620a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f2621b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ z f2622c;

    x(z zVar, ActionMode actionMode, boolean z) {
        this.f2622c = zVar;
        this.f2620a = actionMode;
        this.f2621b = z;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2620a.finish();
        this.f2622c.d();
        if (this.f2621b) {
            this.f2622c.g();
        }
    }
}
