package com.miui.monthreport;

import android.location.Location;
import com.miui.monthreport.d;
import com.miui.monthreport.l;

class j implements d.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f5647a;

    j(l lVar) {
        this.f5647a = lVar;
    }

    public void a(boolean z, Location location) {
        if (z) {
            l lVar = this.f5647a;
            new l.a(lVar.e, location).execute(new Void[0]);
        }
    }
}
