package com.miui.cleanmaster;

import android.content.DialogInterface;

class l implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ LowMemoryIntentDispatchActivity f3757a;

    l(LowMemoryIntentDispatchActivity lowMemoryIntentDispatchActivity) {
        this.f3757a = lowMemoryIntentDispatchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3757a.b();
    }
}
