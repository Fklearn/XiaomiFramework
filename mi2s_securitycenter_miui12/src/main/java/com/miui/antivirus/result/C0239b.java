package com.miui.antivirus.result;

import android.view.View;
import android.view.ViewGroup;
import b.b.o.g.a;

/* renamed from: com.miui.antivirus.result.b  reason: case insensitive filesystem */
class C0239b extends a<View> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0243f f2825a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0239b(C0243f fVar, String str) {
        super(str);
        this.f2825a = fVar;
    }

    /* renamed from: a */
    public Integer get(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        return Integer.valueOf(layoutParams == null ? 0 : layoutParams.height);
    }

    public void a(View view, int i) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = i;
            view.setLayoutParams(layoutParams);
        }
    }
}
