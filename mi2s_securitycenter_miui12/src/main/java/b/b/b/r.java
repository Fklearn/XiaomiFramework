package b.b.b;

import android.content.Context;
import com.miui.antivirus.model.k;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.b;

class r implements b.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f1584a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f1585b;

    r(k kVar, Context context) {
        this.f1584a = kVar;
        this.f1585b = context;
    }

    public void a(IAntiVirusServer iAntiVirusServer) {
        new Thread(new q(this, iAntiVirusServer)).start();
    }
}
