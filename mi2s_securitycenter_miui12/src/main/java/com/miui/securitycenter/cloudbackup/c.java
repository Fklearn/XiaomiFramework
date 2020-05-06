package com.miui.securitycenter.cloudbackup;

import android.os.RemoteException;
import android.util.Log;
import b.b.b.b;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;
import java.util.List;

class c implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7472a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f7473b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ b f7474c;

    c(List list, String str, b bVar) {
        this.f7472a = list;
        this.f7473b = str;
        this.f7474c = bVar;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        try {
            if (this.f7472a != null) {
                for (b.a aVar : this.f7472a) {
                    if (aVar.f1472a.equals(this.f7473b)) {
                        iAntiVirusServer.a(aVar.f1472a, true);
                    } else {
                        iAntiVirusServer.a(aVar.f1472a, false);
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e("AntivirusBackupHelper", "msg", e);
        } catch (Throwable th) {
            this.f7474c.a();
            throw th;
        }
        this.f7474c.a();
    }
}
