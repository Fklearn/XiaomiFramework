package com.miui.antispam.ui.activity;

/* renamed from: com.miui.antispam.ui.activity.i  reason: case insensitive filesystem */
class C0215i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AddPhoneListActivity f2595a;

    C0215i(AddPhoneListActivity addPhoneListActivity) {
        this.f2595a = addPhoneListActivity;
    }

    public void run() {
        this.f2595a.f2510d.requestFocus();
        this.f2595a.q.showSoftInput(this.f2595a.f2510d, 0);
    }
}
