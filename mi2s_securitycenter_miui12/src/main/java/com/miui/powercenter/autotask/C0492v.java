package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ActionMode;
import miui.app.Activity;

/* renamed from: com.miui.powercenter.autotask.v  reason: case insensitive filesystem */
class C0492v implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionMode f6768a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoTaskManageActivity f6769b;

    C0492v(AutoTaskManageActivity autoTaskManageActivity, ActionMode actionMode) {
        this.f6769b = autoTaskManageActivity;
        this.f6768a = actionMode;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Long[] lArr = new Long[this.f6769b.e.size()];
        this.f6769b.e.toArray(lArr);
        Activity activity = this.f6769b;
        C0489s.a((Context) activity, activity.getFragmentManager(), lArr);
        this.f6768a.finish();
    }
}
