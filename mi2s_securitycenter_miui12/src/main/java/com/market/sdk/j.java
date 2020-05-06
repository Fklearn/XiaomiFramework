package com.market.sdk;

import android.os.Bundle;
import b.a.c;

class j implements c.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Bundle f2231a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ FloatService f2232b;

    j(FloatService floatService, Bundle bundle) {
        this.f2232b = floatService;
        this.f2231a = bundle;
    }

    public void run() {
        if (this.f2232b.k != null) {
            this.f2232b.k.a(this.f2231a);
        } else {
            com.market.sdk.utils.c.a("FloatService", "IAppDownloadManager is null");
        }
    }
}
