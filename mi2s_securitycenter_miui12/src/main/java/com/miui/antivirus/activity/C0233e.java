package com.miui.antivirus.activity;

import b.b.b.o;
import com.miui.guardprovider.aidl.IAntiVirusServer;

/* renamed from: com.miui.antivirus.activity.e  reason: case insensitive filesystem */
class C0233e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAntiVirusServer f2716a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0234f f2717b;

    C0233e(C0234f fVar, IAntiVirusServer iAntiVirusServer) {
        this.f2717b = fVar;
        this.f2716a = iAntiVirusServer;
    }

    public void run() {
        this.f2717b.f2718a.r.a(this.f2716a, (o.d) this.f2717b.f2718a.s);
    }
}
