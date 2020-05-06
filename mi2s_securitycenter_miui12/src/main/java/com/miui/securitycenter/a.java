package com.miui.securitycenter;

import android.content.Context;
import android.os.AsyncTask;
import b.b.c.j.s;
import b.b.c.j.x;
import miui.os.Build;

class a extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Application f7464a;

    a(Application application) {
        this.f7464a = application;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        if (!Build.IS_TABLET) {
            return null;
        }
        s.b("No securitycenter installed on pad");
        Application application = this.f7464a;
        x.b((Context) application, application.getPackageName());
        return null;
    }
}
