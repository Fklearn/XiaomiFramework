package com.miui.optimizemanage;

import android.app.Activity;
import android.content.Context;
import b.b.c.i.a;
import com.miui.optimizemanage.memoryclean.i;
import com.miui.optimizemanage.memoryclean.j;
import java.util.List;

class c extends a<List<j>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f5881b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    c(m mVar, Context context) {
        super(context);
        this.f5881b = mVar;
    }

    public List<j> loadInBackground() {
        List<com.miui.optimizemanage.memoryclean.a> a2 = i.a();
        Activity activity = this.f5881b.getActivity();
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        List<j> a3 = this.f5881b.m.a((Context) activity, a2);
        this.f5881b.l.a(a3);
        return a3;
    }
}
