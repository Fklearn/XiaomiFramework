package com.miui.antivirus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import b.b.b.p;
import b.b.o.g.d;
import com.miui.antivirus.service.GuardService;
import miui.os.Build;

public class b extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (d.a("UserSwitchReceiver", (Class<?>) Intent.class, "ACTION_USER_SWITCHED").equals(intent.getAction())) {
            int intExtra = intent.getIntExtra((String) d.a("UserSwitchReceiver", (Class<?>) Intent.class, "EXTRA_USER_HANDLE"), -1);
            if (!Build.IS_INTERNATIONAL_BUILD && UserHandle.myUserId() == intExtra) {
                boolean j = p.j();
                Intent intent2 = new Intent(context, GuardService.class);
                intent2.setAction(j ? "action_register_foreground_notification" : "action_unregister_foreground_notification");
                context.startService(intent2);
            }
        }
    }
}
