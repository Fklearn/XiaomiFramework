package com.miui.powercenter.autotask;

import android.os.AsyncTask;
import android.util.Log;

class Z extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ aa f6741a;

    Z(aa aaVar) {
        this.f6741a = aaVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        for (String str : this.f6741a.f6742a.keySet()) {
            Log.d("OperationsHelper", "apply " + str);
            ba.b(str, this.f6741a.f6742a.get(str));
        }
        return null;
    }
}
