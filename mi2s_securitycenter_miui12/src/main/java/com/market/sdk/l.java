package com.market.sdk;

import b.a.c;

class l implements c.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f2235a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f2236b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ FloatService f2237c;

    l(FloatService floatService, String str, int i) {
        this.f2237c = floatService;
        this.f2235a = str;
        this.f2236b = i;
    }

    public void run() {
        if (this.f2237c.k != null) {
            this.f2237c.k.a(this.f2235a, this.f2236b);
        } else {
            com.market.sdk.utils.c.a("FloatService", "IAppDownloadManager is null");
        }
    }
}
