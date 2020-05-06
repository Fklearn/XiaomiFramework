package com.xiaomi.micloudsdk.stat;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private boolean f8332a;

    /* renamed from: b  reason: collision with root package name */
    private a f8333b;

    private static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public static final c f8334a = new c();
    }

    private c() {
        this.f8332a = false;
    }

    public static c a() {
        return a.f8334a;
    }

    public void a(NetFailedStatParam netFailedStatParam) {
        a aVar = this.f8333b;
        if (aVar != null) {
            aVar.a(netFailedStatParam);
        }
    }

    public void a(NetSuccessStatParam netSuccessStatParam) {
        a aVar = this.f8333b;
        if (aVar != null) {
            aVar.a(netSuccessStatParam);
        }
    }
}
