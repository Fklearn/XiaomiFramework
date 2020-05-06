package com.miui.securitycenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import b.b.c.j.m;
import com.miui.securitycenter.service.NotificationService;
import com.miui.securitycenter.service.RemoteService;

public class o {
    public static void a(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(context, RemoteService.class));
        m.a(context, intent);
        if (h.a(context.getContentResolver())) {
            m.a(context, new Intent(context, NotificationService.class));
        }
    }
}
