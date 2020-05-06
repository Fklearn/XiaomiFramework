package c.a.b.a;

import android.os.IBinder;
import com.xiaomi.micloudsdk.utils.IXiaomiAccountServiceProxy;

class e extends d {
    e() {
    }

    public String a(IBinder iBinder, String str) {
        return IXiaomiAccountServiceProxy.getSnsAccessToken(iBinder, str);
    }

    public boolean a(IBinder iBinder, String str, String str2) {
        return IXiaomiAccountServiceProxy.invalidateSnsAccessToken(iBinder, str, str2);
    }
}
