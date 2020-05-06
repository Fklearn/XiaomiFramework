package com.miui.appmanager.widget;

import android.view.View;
import android.widget.AdapterView;

class c implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f3721a;

    c(d dVar) {
        this.f3721a = dVar;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        int unused = this.f3721a.f3724c = i;
        if (this.f3721a.f3725d != null) {
            this.f3721a.f3725d.a(this.f3721a, i);
        }
        this.f3721a.a();
    }
}
