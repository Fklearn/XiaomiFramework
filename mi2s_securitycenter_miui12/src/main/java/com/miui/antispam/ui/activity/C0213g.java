package com.miui.antispam.ui.activity;

import android.widget.CompoundButton;

/* renamed from: com.miui.antispam.ui.activity.g  reason: case insensitive filesystem */
class C0213g implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AddPhoneListActivity f2593a;

    C0213g(AddPhoneListActivity addPhoneListActivity) {
        this.f2593a = addPhoneListActivity;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        AddPhoneListActivity addPhoneListActivity = this.f2593a;
        boolean unused = addPhoneListActivity.p = addPhoneListActivity.c() && this.f2593a.f2510d.getText().length() > 0;
        this.f2593a.g.getOk().setEnabled(this.f2593a.p);
    }
}
