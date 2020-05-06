package b.b.b;

import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

class h implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VirusObserver f1550a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ i f1551b;

    h(i iVar, VirusObserver virusObserver) {
        this.f1551b = iVar;
        this.f1550a = virusObserver;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        try {
            iAntiVirusServer.a(this.f1550a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
