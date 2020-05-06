package com.miui.securitycenter.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.gamebooster.mutiwindow.f;
import com.miui.optimizemanage.d.e;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.dynamic.DynamicServiceManager;
import com.miui.securitycenter.h;
import com.miui.securitycenter.utils.c;
import java.util.Calendar;

public class CleanMasterReceiver extends BroadcastReceiver {
    public static void a() {
        Application d2 = Application.d();
        AlarmManager alarmManager = (AlarmManager) d2.getSystemService("alarm");
        Calendar instance = Calendar.getInstance();
        instance.add(10, ((int) (Math.random() * 12.0d)) + 12);
        PendingIntent broadcast = PendingIntent.getBroadcast(d2, 10001, new Intent(d2, CleanMasterReceiver.class), 0);
        long h = h.h();
        long timeInMillis = h == 0 ? instance.getTimeInMillis() : h + 86400000;
        if (timeInMillis > System.currentTimeMillis() + 86400000) {
            timeInMillis = System.currentTimeMillis() + 86400000;
        }
        alarmManager.setRepeating(1, timeInMillis, 86400000, broadcast);
    }

    public void onReceive(Context context, Intent intent) {
        Log.d("CleanMasterReceiver", "receive broadcast");
        h.f(System.currentTimeMillis());
        DynamicServiceManager.getInstance(context).update(false);
        c.a(context);
        e.b(context);
        f.f(context);
    }
}
