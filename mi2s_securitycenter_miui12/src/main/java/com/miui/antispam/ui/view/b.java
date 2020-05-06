package com.miui.antispam.ui.view;

import android.view.ActionMode;
import android.view.View;
import com.miui.antispam.ui.view.RecyclerViewExt;

class b implements View.OnLongClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f2647a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RecyclerViewExt.c f2648b;

    b(RecyclerViewExt.c cVar, int i) {
        this.f2648b = cVar;
        this.f2647a = i;
    }

    public boolean onLongClick(View view) {
        RecyclerViewExt.c cVar = this.f2648b;
        if (!cVar.e) {
            cVar.e = true;
            ActionMode unused = cVar.f2644c = view.startActionMode(cVar.e());
            this.f2648b.a(this.f2647a, true, true);
        }
        return true;
    }
}
