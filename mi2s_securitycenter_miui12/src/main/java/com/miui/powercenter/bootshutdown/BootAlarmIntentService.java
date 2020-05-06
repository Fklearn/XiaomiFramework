package com.miui.powercenter.bootshutdown;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.y;
import java.util.Calendar;
import miui.security.SecurityManager;

public class BootAlarmIntentService extends IntentService {
    public BootAlarmIntentService() {
        super("BootAlarmIntentService");
    }

    private void a() {
        a(b().getTimeInMillis() / 1000);
    }

    private void a(long j) {
        ((SecurityManager) getSystemService("security")).setWakeUpTime("com.miui.powercenter.provider.BootAlarmIntentService", j);
        Log.i("BootAlarmIntentService", "setwakeuptime " + j);
    }

    private Calendar b() {
        int o = y.o();
        int n = y.n();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        instance.set(11, o / 60);
        instance.set(12, o % 60);
        instance.set(13, 0);
        instance.set(14, 0);
        m.a(this, n, instance, true);
        return instance;
    }

    private void c() {
        if (y.m()) {
            if (!a.a()) {
                y.d(false);
            } else {
                a();
                return;
            }
        }
        a(0);
    }

    private void d() {
        if (y.m()) {
            a();
        } else {
            a(0);
        }
    }

    private void e() {
        Calendar b2 = b();
        Intent intent = new Intent(this, BootAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.RESET_BOOT_TIME");
        ((AlarmManager) getApplicationContext().getSystemService("alarm")).setExact(0, b2.getTimeInMillis(), PendingIntent.getService(this, 0, intent, 0));
    }

    public void onCreate() {
        super.onCreate();
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equals("com.miui.powercenter.SET_BOOT_TIME")) {
                Log.i("BootAlarmIntentService", "Set boot time action");
                d();
            } else if (intent.getAction().equals("com.miui.powercenter.RESET_BOOT_TIME") && u.e()) {
                Log.i("BootAlarmIntentService", "Reset boot time action");
                c();
            } else {
                return;
            }
            e();
        }
    }
}
