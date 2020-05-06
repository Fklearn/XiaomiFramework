package com.miui.appcompatibility;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.appcompatibility.m;

class l extends AsyncTask<Void, m.a, m.a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3092a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3093b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ boolean f3094c;

    l(Context context, String str, boolean z) {
        this.f3092a = context;
        this.f3093b = str;
        this.f3094c = z;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public m.a doInBackground(Void... voidArr) {
        return m.b(this.f3092a);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(m.a aVar) {
        m.a(this.f3092a, this.f3093b, this.f3094c, aVar);
    }
}
