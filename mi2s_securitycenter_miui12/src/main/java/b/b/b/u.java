package b.b.b;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import b.b.b.d.o;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.guardprovider.WifiCheckObserver;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import miui.os.Build;

public class u {

    /* renamed from: a  reason: collision with root package name */
    private static u f1591a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1592b;

    /* renamed from: c  reason: collision with root package name */
    private WifiManager f1593c;

    private u(Context context) {
        this.f1592b = context.getApplicationContext();
        this.f1593c = (WifiManager) context.getApplicationContext().getSystemService("wifi");
    }

    public static synchronized u a(Context context) {
        u uVar;
        synchronized (u.class) {
            if (f1591a == null) {
                f1591a = new u(context);
            }
            uVar = f1591a;
        }
        return uVar;
    }

    public void a(int i) {
        ArrayList<String> a2 = b.a("WlanTrustList", (ArrayList<String>) new ArrayList());
        a2.remove(String.valueOf(i));
        b.b("WlanTrustList", a2);
    }

    public void a(WifiInfo wifiInfo) {
        try {
            Class<?> cls = Class.forName("android.net.wifi.WifiManager$ActionListener");
            e.a((Object) this.f1593c, "forget", (Class<?>[]) new Class[]{Integer.TYPE, cls}, Integer.valueOf(wifiInfo.getNetworkId()), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void a(IAntiVirusServer iAntiVirusServer, WifiInfo wifiInfo) {
        if (!Build.IS_INTERNATIONAL_BUILD && h.i()) {
            o oVar = new o();
            WifiCheckObserver wifiCheckObserver = new WifiCheckObserver(this.f1592b);
            wifiCheckObserver.a(oVar);
            wifiCheckObserver.a(wifiInfo);
            oVar.a(iAntiVirusServer, wifiCheckObserver, false);
            Log.e("WifiCheckManager", "start wifi scan task ...");
        }
    }

    public boolean b(WifiInfo wifiInfo) {
        return b.a("WlanTrustList", (ArrayList<String>) new ArrayList()).contains(String.valueOf(wifiInfo.getNetworkId()));
    }

    public void c(WifiInfo wifiInfo) {
        ArrayList<String> a2 = b.a("WlanTrustList", (ArrayList<String>) new ArrayList());
        String valueOf = String.valueOf(wifiInfo.getNetworkId());
        if (!a2.contains(valueOf)) {
            a2.add(valueOf);
            b.b("WlanTrustList", a2);
        }
    }
}
