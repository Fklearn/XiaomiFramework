package com.miui.antivirus.activity;

import android.os.RemoteException;
import android.util.Log;
import b.b.b.b;
import com.miui.antivirus.activity.SettingsActivity;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;
import java.util.List;

class C implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f2652a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f2653b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ SettingsActivity.c f2654c;

    C(SettingsActivity.c cVar, List list, String str) {
        this.f2654c = cVar;
        this.f2652a = list;
        this.f2653b = str;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        try {
            if (this.f2652a != null) {
                for (b.a aVar : this.f2652a) {
                    if (aVar.f1472a.equals(this.f2653b)) {
                        iAntiVirusServer.a(aVar.f1472a, true);
                    } else {
                        iAntiVirusServer.a(aVar.f1472a, false);
                    }
                }
            }
            this.f2654c.a(this.f2654c.t.d());
        } catch (RemoteException e) {
            Log.e("SettingsActivity", "msg", e);
        }
    }
}
