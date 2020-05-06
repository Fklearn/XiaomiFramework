package b.b.b.d;

import android.os.RemoteException;
import android.util.Log;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.aidl.IWifiDetectObserver;

public class o {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1541a = "o";

    /* renamed from: b  reason: collision with root package name */
    private boolean f1542b = false;

    public void a(IAntiVirusServer iAntiVirusServer, IWifiDetectObserver iWifiDetectObserver, boolean z) {
        try {
            iAntiVirusServer.b("TENCENT", iWifiDetectObserver);
            iAntiVirusServer.c("TENCENT", iWifiDetectObserver);
            iAntiVirusServer.a("TENCENT", iWifiDetectObserver);
            if (z) {
                iAntiVirusServer.a("TENCENT", iWifiDetectObserver, "mdetector");
            }
        } catch (RemoteException e) {
            Log.e(f1541a, "error when start wifi detect !", e);
        }
    }
}
