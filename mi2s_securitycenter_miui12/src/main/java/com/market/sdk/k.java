package com.market.sdk;

import android.net.Uri;
import b.a.c;

class k implements c.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Uri f2233a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ FloatService f2234b;

    k(FloatService floatService, Uri uri) {
        this.f2234b = floatService;
        this.f2233a = uri;
    }

    public void run() {
        if (this.f2234b.k != null) {
            this.f2234b.k.c(this.f2233a);
        } else {
            com.market.sdk.utils.c.a("FloatService", "IAppDownloadManager is null");
        }
    }
}
