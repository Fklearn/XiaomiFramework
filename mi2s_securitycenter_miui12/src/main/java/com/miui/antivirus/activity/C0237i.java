package com.miui.antivirus.activity;

import android.os.RemoteException;
import b.b.b.o;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

/* renamed from: com.miui.antivirus.activity.i  reason: case insensitive filesystem */
class C0237i implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2721a;

    C0237i(MainActivity mainActivity) {
        this.f2721a = mainActivity;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        try {
            this.f2721a.r.a(iAntiVirusServer, this.f2721a.u, (o.d) this.f2721a.s);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
