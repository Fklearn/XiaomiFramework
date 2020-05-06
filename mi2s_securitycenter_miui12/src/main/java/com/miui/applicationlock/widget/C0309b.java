package com.miui.applicationlock.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import com.miui.applicationlock.c.E;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

/* renamed from: com.miui.applicationlock.widget.b  reason: case insensitive filesystem */
public class C0309b extends AlertDialog {

    /* renamed from: a  reason: collision with root package name */
    private E f3430a;

    /* renamed from: b  reason: collision with root package name */
    private Context f3431b;

    public C0309b(Context context, int i, E e) {
        super(context, i);
        this.f3431b = context;
        this.f3430a = e;
    }

    public void dismiss() {
        C0309b.super.dismiss();
        E e = this.f3430a;
        if (e != null) {
            e.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        getWindow().setLayout(-1, -1);
        getWindow().setBackgroundDrawable(new ColorDrawable(this.f3431b.getResources().getColor(R.color.fod_dialog_window_background)));
        getWindow().addFlags(4);
        getWindow().getDecorView().setSystemUiVisibility(4866);
    }
}
