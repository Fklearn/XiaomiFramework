package com.miui.powercenter.powerui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import com.miui.powercenter.powerui.h;

class b implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7143a;

    b(h hVar) {
        this.f7143a = hVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.f7143a.n()) {
            new h.b(this.f7143a, (a) null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            this.f7143a.a(false);
            return;
        }
        k.a(this.f7143a.e, "5percent_dialog");
    }
}
