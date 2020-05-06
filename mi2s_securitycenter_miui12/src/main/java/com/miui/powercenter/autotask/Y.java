package com.miui.powercenter.autotask;

import android.os.AsyncTask;
import android.util.Log;

class Y extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Object f6740a;

    Y(Object obj) {
        this.f6740a = obj;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Log.d("OperationsHelper", "apply airplane mode");
        ba.b("airplane_mode", this.f6740a);
        return null;
    }
}
