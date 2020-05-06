package com.miui.wakepath.ui;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.permcenter.s;

class a extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f8238a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmStartActivity f8239b;

    a(ConfirmStartActivity confirmStartActivity, Context context) {
        this.f8239b = confirmStartActivity;
        this.f8238a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        s.a(this.f8238a, this.f8239b.f8236c, this.f8239b.f8237d);
        return null;
    }
}
