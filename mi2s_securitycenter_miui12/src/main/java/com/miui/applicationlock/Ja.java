package com.miui.applicationlock;

import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;

class Ja implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DialogInterface.OnClickListener f3183a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ bb f3184b;

    Ja(bb bbVar, DialogInterface.OnClickListener onClickListener) {
        this.f3184b = bbVar;
        this.f3183a = onClickListener;
    }

    public void onItemClick(AdapterView adapterView, View view, int i, long j) {
        this.f3183a.onClick(new Ia(this), i);
    }
}
