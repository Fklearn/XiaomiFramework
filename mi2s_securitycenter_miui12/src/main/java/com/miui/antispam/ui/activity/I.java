package com.miui.antispam.ui.activity;

import android.view.View;
import android.widget.CheckBox;
import com.miui.antispam.ui.activity.KeywordListActivity;

class I implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity.b.C0037b f2536a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f2537b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ KeywordListActivity.b f2538c;

    I(KeywordListActivity.b bVar, KeywordListActivity.b.C0037b bVar2, int i) {
        this.f2538c = bVar;
        this.f2536a = bVar2;
        this.f2537b = i;
    }

    public void onClick(View view) {
        if (!this.f2538c.e) {
            view.showContextMenu();
            return;
        }
        CheckBox checkBox = this.f2536a.f2551b;
        checkBox.setChecked(!checkBox.isChecked());
        this.f2538c.a(this.f2537b, this.f2536a.f2551b.isChecked(), false);
    }
}
