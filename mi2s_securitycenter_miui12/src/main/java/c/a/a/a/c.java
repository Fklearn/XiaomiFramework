package c.a.a.a;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

class c extends b {
    c() {
    }

    private static boolean a(Context context, String str, ServiceConnection serviceConnection) {
        Intent intent = new Intent(str);
        intent.setPackage("com.xiaomi.account");
        return context.bindService(intent, serviceConnection, 1);
    }

    public boolean a(Context context, ServiceConnection serviceConnection) {
        return a(context, "com.xiaomi.account.action.BIND_XIAOMI_ACCOUNT_SERVICE", serviceConnection) || a(context, "android.intent.action.BIND_XIAOMI_ACCOUNT_SERVICE", serviceConnection);
    }
}
