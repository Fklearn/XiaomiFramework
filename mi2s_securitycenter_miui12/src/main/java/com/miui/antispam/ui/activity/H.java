package com.miui.antispam.ui.activity;

import android.widget.EditText;

class H implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ EditText f2534a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity f2535b;

    H(KeywordListActivity keywordListActivity, EditText editText) {
        this.f2535b = keywordListActivity;
        this.f2534a = editText;
    }

    public void run() {
        this.f2534a.requestFocus();
        this.f2535b.p.showSoftInput(this.f2534a, 0);
    }
}
