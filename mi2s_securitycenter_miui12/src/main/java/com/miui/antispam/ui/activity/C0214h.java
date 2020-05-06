package com.miui.antispam.ui.activity;

import android.widget.CompoundButton;

/* renamed from: com.miui.antispam.ui.activity.h  reason: case insensitive filesystem */
class C0214h implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AddPhoneListActivity f2594a;

    C0214h(AddPhoneListActivity addPhoneListActivity) {
        this.f2594a = addPhoneListActivity;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        AddPhoneListActivity addPhoneListActivity = this.f2594a;
        boolean unused = addPhoneListActivity.p = addPhoneListActivity.c() && this.f2594a.f2510d.getText().length() > 0;
        this.f2594a.g.getOk().setEnabled(this.f2594a.p);
    }
}
