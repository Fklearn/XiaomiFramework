package com.miui.applicationlock.c;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.applicationlock.C0312y;

/* renamed from: com.miui.applicationlock.c.e  reason: case insensitive filesystem */
class C0261e extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3301a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3302b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ boolean f3303c;

    C0261e(Context context, String str, boolean z) {
        this.f3301a = context;
        this.f3302b = str;
        this.f3303c = z;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Context context;
        String str;
        int i;
        long j;
        if (!C0267k.h(this.f3301a)) {
            return null;
        }
        if (o.f3320d.contains(this.f3302b)) {
            if (o.l() < 1) {
                context = this.f3301a;
                j = o.b(20, 30);
                i = 3;
                str = "competitive_app_installed";
            }
            return null;
        }
        if (!this.f3303c && C0312y.f3468b.contains(this.f3302b) && o.m() < 2) {
            context = this.f3301a;
            j = 86400000;
            i = 4;
            str = "recommend_app_installed";
        }
        return null;
        C0267k.b(context, j, str, i);
        return null;
    }
}
