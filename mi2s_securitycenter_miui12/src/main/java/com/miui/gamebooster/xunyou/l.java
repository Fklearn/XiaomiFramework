package com.miui.gamebooster.xunyou;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0371b;

class l extends AsyncTask<Void, Void, Boolean> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f5418a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f5419b;

    l(m mVar, Context context) {
        this.f5419b = mVar;
        this.f5418a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Boolean doInBackground(Void... voidArr) {
        boolean z = true;
        if (a.h(true)) {
            a.G(false);
            a.ba(true);
        } else {
            z = a.v(false);
        }
        return Boolean.valueOf(z);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        this.f5419b.i.setChecked(bool.booleanValue());
        if (bool.booleanValue()) {
            C0371b.b(this.f5418a);
        } else {
            C0371b.a(this.f5418a);
        }
    }
}
