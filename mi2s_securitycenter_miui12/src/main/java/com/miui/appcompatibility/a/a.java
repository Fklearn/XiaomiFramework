package com.miui.appcompatibility.a;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.appcompatibility.d;

class a extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3066a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3067b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f3068c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f3069d;
    final /* synthetic */ c e;

    a(c cVar, Context context, String str, String str2, int i) {
        this.e = cVar;
        this.f3066a = context;
        this.f3067b = str;
        this.f3068c = str2;
        this.f3069d = i;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        d.b(this.f3066a).a(this.f3067b, this.f3068c, this.f3069d);
        return null;
    }
}
