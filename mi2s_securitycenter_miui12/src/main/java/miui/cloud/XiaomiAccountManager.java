package miui.cloud;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import b.d.b.b.b;
import c.a.a.a.a;
import c.a.b.a.c;

public class XiaomiAccountManager {
    private static final String TAG = "XiaomiAccountManager";

    public static String getSnsAccessToken(Context context, final String str) {
        return (String) new b<String>(context) {
            /* access modifiers changed from: protected */
            public boolean bindService(Context context, ServiceConnection serviceConnection) {
                return a.a(context, serviceConnection);
            }

            /* access modifiers changed from: protected */
            public String invokeRemoteMethod(IBinder iBinder) {
                return c.a(iBinder, str);
            }
        }.invoke();
    }

    public static boolean invalidateSnsAccessToken(Context context, final String str, final String str2) {
        Boolean bool = (Boolean) new b<Boolean>(context) {
            /* access modifiers changed from: protected */
            public boolean bindService(Context context, ServiceConnection serviceConnection) {
                return a.a(context, serviceConnection);
            }

            /* access modifiers changed from: protected */
            public Boolean invokeRemoteMethod(IBinder iBinder) {
                return Boolean.valueOf(c.a(iBinder, str, str2));
            }
        }.invoke();
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }
}
