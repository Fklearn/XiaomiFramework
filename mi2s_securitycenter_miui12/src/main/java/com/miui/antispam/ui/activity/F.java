package com.miui.antispam.ui.activity;

import android.widget.EditText;

class F implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ EditText f2530a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity f2531b;

    F(KeywordListActivity keywordListActivity, EditText editText) {
        this.f2531b = keywordListActivity;
        this.f2530a = editText;
    }

    public void run() {
        this.f2530a.requestFocus();
        this.f2531b.p.showSoftInput(this.f2530a, 0);
    }
}
