package com.miui.antivirus.whitelist;

import android.view.View;
import com.miui.antivirus.whitelist.WhiteListActivity;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WhiteListActivity.b.a f3034a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ WhiteListActivity.b f3035b;

    e(WhiteListActivity.b bVar, WhiteListActivity.b.a aVar) {
        this.f3035b = bVar;
        this.f3034a = aVar;
    }

    public void onClick(View view) {
        this.f3034a.f3022c.setChecked(!this.f3034a.f3022c.isChecked());
    }
}
