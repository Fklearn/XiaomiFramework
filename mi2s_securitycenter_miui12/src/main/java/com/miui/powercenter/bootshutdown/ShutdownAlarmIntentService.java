package com.miui.powercenter.bootshutdown;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.y;
import java.util.Calendar;

public class ShutdownAlarmIntentService extends IntentService {
    public ShutdownAlarmIntentService() {
        super("ShutdownAlarmIntentService");
    }

    private void a() {
        p.b(this);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService("alarm");
        alarmManager.cancel(d());
        alarmManager.cancel(c());
    }

    private boolean b() {
        if (y.q() >= System.currentTimeMillis() || y.s() != 0) {
            return true;
        }
        y.e(false);
        return false;
    }

    private PendingIntent c() {
        Intent intent = new Intent(this, ShutdownAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.SHUTDOWN_NOW");
        return PendingIntent.getService(getApplicationContext(), 0, intent, 0);
    }

    private PendingIntent d() {
        Intent intent = new Intent(this, ShutdownAlarmIntentService.class);
        intent.setAction("com.miui.powercenter.SHUTDOWN_ORNOT");
        return PendingIntent.getService(getApplicationContext(), 0, intent, 0);
    }

    private void e() {
        if (y.r()) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            int t = y.t();
            instance.set(11, t / 60);
            instance.set(12, t % 60);
            instance.set(13, 0);
            instance.set(14, 0);
            m.a(this, y.s(), instance, false);
            ((AlarmManager) getApplicationContext().getSystemService("alarm")).setExact(0, instance.getTimeInMillis(), d());
        }
    }

    private void f() {
        ((AlarmManager) getApplicationContext().getSystemService("alarm")).setExact(0, System.currentTimeMillis() + 20000, c());
    }

    private void g() {
        Intent intent = Build.VERSION.SDK_INT > 25 ? new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN") : new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
        intent.addFlags(268435456);
        startActivity(intent);
    }

    private void h() {
        PowerManager.WakeLock newWakeLock = ((PowerManager) getApplicationContext().getSystemService("power")).newWakeLock(268435462, "tag");
        newWakeLock.acquire();
        newWakeLock.release();
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            if (intent.getAction().equals("com.miui.powercenter.CANCEL_SHUTDOWN")) {
                Log.d("ShutdownOnService", "ACTION_CANCEL_SHUTDOWN");
                a();
                return;
            }
            if (intent.getAction().equals("com.miui.powercenter.SET_SHUTDOWN_ALARM")) {
                Log.d("ShutdownOnService", "ACTION_SET_SHUTDOWN_ALARM");
                p.b(this);
                a();
            } else if (intent.getAction().equals("com.miui.powercenter.RESET_SHUTDOWNTIME")) {
                Log.d("ShutdownOnService", "ACTION_RESET_SHUTDOWNTIME");
                a();
                if (!b()) {
                    return;
                }
            } else if (intent.getAction().equals("com.miui.powercenter.SHUTDOWN_ORNOT")) {
                Log.d("ShutdownOnService", "ACTION_SHUTDOWN_ORNOT");
                p.e(this);
                f();
                h();
                return;
            } else if (intent.getAction().equals("com.miui.powercenter.SHUTDOWN_NOW")) {
                Log.d("ShutdownOnService", "ACTION_SHUTDOWN_NOW");
                p.b(this);
                boolean a2 = p.a(this);
                boolean a3 = u.a();
                if (!a2) {
                    Log.d("ShutdownOnService", "canceled, phone not idle");
                    p.c(this);
                    return;
                } else if (a3) {
                    Log.d("ShutdownOnService", "canceled, playing game");
                    p.d(this);
                    return;
                } else {
                    Log.d("ShutdownOnService", "shutdown now");
                    a.c(this);
                    g();
                    return;
                }
            } else {
                return;
            }
            e();
        }
    }
}
