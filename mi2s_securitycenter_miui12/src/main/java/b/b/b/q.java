package b.b.b;

import android.os.RemoteException;
import b.b.b.t;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.aidl.IVirusObserver;

class q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IAntiVirusServer f1582a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ r f1583b;

    q(r rVar, IAntiVirusServer iAntiVirusServer) {
        this.f1583b = rVar;
        this.f1582a = iAntiVirusServer;
    }

    public void run() {
        try {
            this.f1582a.a(new String[]{this.f1583b.f1584a.e()}, (IVirusObserver) new t.a(this.f1583b.f1585b, this.f1583b.f1584a), false);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
