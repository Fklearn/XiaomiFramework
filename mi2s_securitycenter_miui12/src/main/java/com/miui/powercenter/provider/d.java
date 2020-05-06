package com.miui.powercenter.provider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import com.miui.powercenter.a.a;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.y;
import com.xiaomi.stat.MiStat;

public class d extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private boolean f7174a = false;

    /* renamed from: b  reason: collision with root package name */
    private int f7175b = 0;

    /* renamed from: c  reason: collision with root package name */
    private boolean f7176c = true;

    /* renamed from: d  reason: collision with root package name */
    private long f7177d = SystemClock.elapsedRealtime();
    /* access modifiers changed from: private */
    public C0501e.a e;
    private b f = new b();
    private e g = new e();

    private void a(int i, int i2) {
        if (i2 < i) {
            int c2 = (y.c() + i) - i2;
            int i3 = 100;
            if (c2 <= 100) {
                i3 = c2;
            }
            y.a(i3);
            y.b(y.d() + (SystemClock.elapsedRealtime() - this.f7177d));
        }
    }

    private void a(Context context, Intent intent) {
        int intExtra = (intent.getIntExtra(MiStat.Param.LEVEL, -1) * 100) / intent.getIntExtra("scale", -1);
        boolean a2 = o.a(intent);
        if (this.f7175b != intExtra) {
            if (a2) {
                c(context);
            } else {
                if (y.x()) {
                    this.f.a(intExtra);
                    long currentTimeMillis = System.currentTimeMillis();
                    if (Math.abs(currentTimeMillis - y.j()) >= 1800000) {
                        String a3 = this.f.a(context, intExtra);
                        if (!TextUtils.isEmpty(a3)) {
                            this.g.a(context, a3);
                            y.e(currentTimeMillis);
                            Log.i("BatteryInfoReceiver", "Show battery consume abnormal notification");
                        }
                    }
                }
                a(this.f7175b, intExtra);
            }
            this.f7175b = intExtra;
            this.f7177d = SystemClock.elapsedRealtime();
        }
        if (this.f7176c != a2) {
            if (a2) {
                a.a(o.e(context));
            } else if (this.e != null) {
                Log.d("BatteryInfoReceiver", "Save charge details,  startLevel " + this.e.f6882d + " endLevel " + this.e.e + " totalChargedTime " + this.e.f6880b);
                b(this.e);
                a(this.e);
                this.e = null;
            }
            Log.i("BatteryInfoReceiver", "Charge status changed, prev status " + this.f7176c + ", status " + a2);
            this.f7176c = a2;
            this.f.a();
        }
    }

    private void a(C0501e.a aVar) {
        if (aVar.f6880b > 300000 && aVar.e - aVar.f6882d >= 2) {
            y.a(System.currentTimeMillis());
            y.a(0);
            y.b(0);
        }
    }

    private void b(C0501e.a aVar) {
        int i;
        StringBuilder sb;
        String str;
        Log.d("BatteryInfoReceiver", "charge detail, startLevel " + aVar.f6882d + " endLevel " + aVar.e + " plugType " + aVar.g + " useMaxOrMin " + aVar.f + " chargedTime " + aVar.f6881c);
        int i2 = aVar.e;
        if (i2 >= 90 && (i = aVar.f6882d) <= 50 && !aVar.f) {
            long j = (aVar.f6881c * 100) / ((long) (i2 - i));
            if (j <= 0) {
                Log.e("BatteryInfoReceiver", "chargeFullTime 0");
                return;
            }
            int i3 = aVar.g;
            if (i3 == 1) {
                long f2 = y.f();
                if (f2 != 0) {
                    y.c((f2 + j) / 2);
                } else {
                    y.c(j);
                }
                sb = new StringBuilder();
                str = "plugType ac, charge full time ";
            } else if (i3 == 2) {
                long g2 = y.g();
                if (g2 != 0) {
                    y.d((g2 + j) / 2);
                } else {
                    y.d(j);
                }
                sb = new StringBuilder();
                str = "plugType usb, charge full time ";
            } else {
                return;
            }
            sb.append(str);
            sb.append(j);
            Log.i("BatteryInfoReceiver", sb.toString());
        }
    }

    private void c(Context context) {
        new c(this, context).execute(new Void[0]);
    }

    public void a(Context context) {
        if (!this.f7174a) {
            this.f7174a = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            Intent registerReceiver = context.registerReceiver(this, intentFilter);
            if (registerReceiver != null) {
                a(context, registerReceiver);
            }
        }
    }

    public void b(Context context) {
        if (this.f7174a) {
            this.f7174a = false;
            context.unregisterReceiver(this);
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
            Log.i("BatteryInfoReceiver", "ACTION_BATTERY_CHANGED");
            a(context, intent);
        }
    }
}
