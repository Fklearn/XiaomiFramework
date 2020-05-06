package com.miui.powercenter.batteryhistory;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.legacypowerrank.i;
import com.miui.powercenter.provider.PowerSaveService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@TargetApi(24)
/* renamed from: com.miui.powercenter.batteryhistory.u  reason: case insensitive filesystem */
public class C0516u implements AlarmManager.OnAlarmListener {

    /* renamed from: a  reason: collision with root package name */
    private static volatile C0516u f6925a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f6926b;

    /* renamed from: c  reason: collision with root package name */
    private HandlerThread f6927c = new HandlerThread("BatteryHistoryManager");

    /* renamed from: d  reason: collision with root package name */
    private Handler f6928d;
    private AlarmManager e;

    /* renamed from: com.miui.powercenter.batteryhistory.u$a */
    private class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                C0516u.this.e();
            } else if (i == 2) {
                C0516u.this.g();
            } else if (i == 3) {
                C0516u.this.c();
            } else if (i == 4) {
                boolean z = false;
                Object obj = message.obj;
                if (obj instanceof Boolean) {
                    z = ((Boolean) obj).booleanValue();
                }
                C0516u.this.b(z);
            }
            super.handleMessage(message);
        }
    }

    /* renamed from: com.miui.powercenter.batteryhistory.u$b */
    private class b extends BroadcastReceiver {
        private b() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.ACTION_SHUTDOWN".equals(intent.getAction())) {
                boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.SHUTDOWN_USERSPACE_ONLY", false);
                Log.w("BatteryHistoryManager", "shut user : " + booleanExtra);
                if (!booleanExtra) {
                    C0513q.a(C0516u.this.f6926b).c(System.currentTimeMillis());
                }
            }
        }
    }

    private C0516u(Context context) {
        this.f6926b = context.getApplicationContext();
        this.f6927c.start();
        this.f6928d = new a(this.f6927c.getLooper());
        this.e = (AlarmManager) this.f6926b.getSystemService("alarm");
        this.f6928d.sendEmptyMessage(1);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        this.f6926b.registerReceiver(new b(), intentFilter);
    }

    public static C0516u a(Context context) {
        if (f6925a == null) {
            synchronized (C0516u.class) {
                if (f6925a == null) {
                    f6925a = new C0516u(context);
                }
            }
        }
        return f6925a;
    }

    private void a(long j, List<BatteryData> list) {
        if (list == null) {
            list = d();
        }
        C0513q.a(this.f6926b).a(j, list);
    }

    private boolean a(long j) {
        if (C0513q.a(this.f6926b).k() == j && j != 0) {
            return false;
        }
        b(j);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005b, code lost:
        com.miui.powercenter.batteryhistory.b.b.a(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005e, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean a(long r11, long r13) {
        /*
            r10 = this;
            android.content.Context r0 = r10.f6926b
            com.miui.powercenter.batteryhistory.q r0 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r0)
            long r0 = r0.g()
            android.content.Context r2 = r10.f6926b
            com.miui.powercenter.batteryhistory.q r2 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r2)
            java.util.List r2 = r2.c()
            r3 = 0
            if (r2 == 0) goto L_0x00cb
            int r4 = r2.size()
            if (r4 != 0) goto L_0x001f
            goto L_0x00cb
        L_0x001f:
            android.content.Context r4 = r10.f6926b
            com.miui.powercenter.batteryhistory.q r4 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r4)
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r4 = r4.d()
            if (r4 == 0) goto L_0x002e
            r2.add(r4)
        L_0x002e:
            r4 = 0
            long r11 = r11 - r0
            long r11 = r11 + r4
            long r11 = r11 + r13
            if (r2 == 0) goto L_0x00cb
            int r13 = r2.size()
            r14 = 1
            int r13 = r13 - r14
        L_0x003b:
            if (r13 < 0) goto L_0x00cb
            java.lang.Object r0 = r2.get(r13)
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r0 = (com.miui.powercenter.batteryhistory.BatteryHistogramItem) r0
            int r1 = r2.size()
            int r1 = r1 - r14
            r4 = 86400000(0x5265c00, double:4.2687272E-316)
            if (r13 != r1) goto L_0x005f
            int r1 = r0.type
            r6 = 2
            if (r1 != r6) goto L_0x005f
            long r0 = r0.shutdownDuration
            long r11 = r11 + r0
            int r0 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r0 < 0) goto L_0x00c4
            java.lang.String r11 = "clearOverageHistory shutdown more than 24"
        L_0x005b:
            com.miui.powercenter.batteryhistory.b.b.a(r11)
            return r14
        L_0x005f:
            int r1 = r0.type
            if (r1 != 0) goto L_0x006f
            if (r13 != 0) goto L_0x006b
            long r6 = r0.endTime
            long r8 = r0.startTime
            long r6 = r6 - r8
            goto L_0x0073
        L_0x006b:
            r6 = 3600000(0x36ee80, double:1.7786363E-317)
            goto L_0x0073
        L_0x006f:
            if (r1 != r14) goto L_0x00c8
            long r6 = r0.shutdownDuration
        L_0x0073:
            long r11 = r11 + r6
            int r1 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r1 < 0) goto L_0x00c4
            int r11 = r0.type
            if (r11 != r14) goto L_0x0092
        L_0x007c:
            int r11 = r2.size()
            int r11 = r11 - r14
            if (r13 >= r11) goto L_0x0092
            int r11 = r13 + 1
            java.lang.Object r12 = r2.get(r11)
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r12 = (com.miui.powercenter.batteryhistory.BatteryHistogramItem) r12
            int r12 = r12.type
            if (r12 == r14) goto L_0x0090
            goto L_0x0092
        L_0x0090:
            r13 = r11
            goto L_0x007c
        L_0x0092:
            if (r13 < 0) goto L_0x00c1
            int r11 = r2.size()
            int r11 = r11 - r14
            if (r13 > r11) goto L_0x00c1
            java.lang.Object r11 = r2.get(r13)
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r11 = (com.miui.powercenter.batteryhistory.BatteryHistogramItem) r11
            long r11 = r11.startTime
            android.content.Context r13 = r10.f6926b
            com.miui.powercenter.batteryhistory.q r13 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r13)
            r13.a((long) r11)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "clearOverageHistory clear time : "
            r13.append(r14)
            r13.append(r11)
            java.lang.String r11 = r13.toString()
            com.miui.powercenter.batteryhistory.b.b.a(r11)
            goto L_0x00cb
        L_0x00c1:
            java.lang.String r11 = "clearOverageHistory clear time error"
            goto L_0x005b
        L_0x00c4:
            int r13 = r13 + -1
            goto L_0x003b
        L_0x00c8:
            java.lang.String r11 = "clearOverageHistory error shutdown placeholder"
            goto L_0x005b
        L_0x00cb:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0516u.a(long, long):boolean");
    }

    private long b(long j, List<BatteryData> list) {
        int i = Calendar.getInstance().get(12);
        long j2 = (long) ((i >= 55 ? 120 - i : 60 - i) * 60 * 1000);
        a(j, list);
        return j2;
    }

    private void b(long j) {
        C0513q.a(this.f6926b).a();
        C0513q.a(this.f6926b).b(j);
        C0513q.a(this.f6926b).c(0);
    }

    /* access modifiers changed from: private */
    public void b(boolean z) {
        if (z) {
            com.miui.powercenter.batteryhistory.b.b.a("checkInvalidInner forceInvalid");
            b(Long.MIN_VALUE);
            c();
        } else if (!f()) {
            com.miui.powercenter.batteryhistory.b.b.a("check invalid inner judgeDataValid");
        }
    }

    /* access modifiers changed from: private */
    public void c() {
        com.miui.powercenter.batteryhistory.b.b.a("check reset inner : " + C0519x.a(System.currentTimeMillis()));
        Map<Long, aa> b2 = com.miui.powercenter.batteryhistory.b.a.b();
        aa aaVar = b2.get(Long.valueOf(((Long) new ArrayList(b2.keySet()).get(0)).longValue()));
        SystemClock.elapsedRealtime();
        List<BatteryData> d2 = d();
        if (a(aaVar != null ? aaVar.a() : 0)) {
            com.miui.powercenter.batteryhistory.b.b.a("checkResetInner reset");
            this.e.cancel(this);
            long a2 = com.miui.powercenter.batteryhistory.b.a.a() + SystemClock.elapsedRealtime();
            long b3 = b(a2, d2);
            com.miui.powercenter.batteryhistory.b.b.a("check reset inner curHistoryTime : " + a2);
            com.miui.powercenter.batteryhistory.b.b.a("check reset inner next record history time(min) : " + (b3 / 60000));
            c(b3 + SystemClock.elapsedRealtime());
        }
    }

    private void c(long j) {
        Intent intent = new Intent(this.f6926b, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.BATTERYHISTORY_RECORD");
        PendingIntent service = PendingIntent.getService(this.f6926b, 1, intent, 134217728);
        this.e.cancel(service);
        this.e.setExact(2, j, service);
    }

    private List<BatteryData> d() {
        i.d();
        List<BatteryData> a2 = i.a();
        List<BatteryData> b2 = i.b();
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(a2);
        arrayList.addAll(b2);
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0086, code lost:
        if (r1 > 60000) goto L_0x0089;
     */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0131  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0137  */
    @android.annotation.TargetApi(24)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void e() {
        /*
            r22 = this;
            r0 = r22
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "init history : "
            r1.append(r2)
            long r2 = java.lang.System.currentTimeMillis()
            java.lang.String r2 = com.miui.powercenter.batteryhistory.C0519x.a(r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BatteryHistoryManager"
            android.util.Log.w(r2, r1)
            android.content.Context r1 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r1 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r1)
            long r1 = r1.m()
            long r3 = java.lang.System.currentTimeMillis()
            long r5 = android.os.SystemClock.elapsedRealtime()
            android.content.Context r7 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r7 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r7)
            r8 = 0
            r7.c(r8)
            java.util.Map r7 = com.miui.powercenter.batteryhistory.b.a.b()
            java.util.ArrayList r10 = new java.util.ArrayList
            java.util.Set r11 = r7.keySet()
            r10.<init>(r11)
            r11 = 0
            java.lang.Object r10 = r10.get(r11)
            java.lang.Long r10 = (java.lang.Long) r10
            long r10 = r10.longValue()
            java.lang.Long r12 = java.lang.Long.valueOf(r10)
            java.lang.Object r7 = r7.get(r12)
            com.miui.powercenter.batteryhistory.aa r7 = (com.miui.powercenter.batteryhistory.aa) r7
            long r12 = android.os.SystemClock.elapsedRealtime()
            long r10 = r10 + r12
            java.util.List r15 = r22.d()
            if (r7 == 0) goto L_0x006f
            long r12 = r7.a()
            goto L_0x0070
        L_0x006f:
            r12 = r8
        L_0x0070:
            r16 = 300000(0x493e0, double:1.482197E-318)
            int r7 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            r20 = 60000(0xea60, double:2.9644E-319)
            if (r7 > 0) goto L_0x0088
            int r7 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r7 <= 0) goto L_0x0088
            long r1 = r3 - r1
            int r3 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r3 < 0) goto L_0x0088
            int r3 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
            if (r3 > 0) goto L_0x0089
        L_0x0088:
            r1 = r8
        L_0x0089:
            boolean r3 = r0.a((long) r12)
            if (r3 == 0) goto L_0x00a0
            java.lang.String r1 = "init history checkreset true"
            com.miui.powercenter.batteryhistory.b.b.a(r1)
        L_0x0094:
            long r1 = com.miui.powercenter.batteryhistory.b.a.a()
            long r3 = android.os.SystemClock.elapsedRealtime()
            long r10 = r1 + r3
            r1 = r8
            goto L_0x00c2
        L_0x00a0:
            android.content.Context r3 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r3 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r3)
            int r3 = r3.l()
            r4 = 1
            if (r3 != r4) goto L_0x00c2
            android.content.Context r3 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r3 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r3)
            int r3 = r3.e()
            if (r3 != 0) goto L_0x00c2
            java.lang.String r1 = "init history no histogram reset"
            com.miui.powercenter.batteryhistory.b.b.a(r1)
            r0.b((long) r12)
            goto L_0x0094
        L_0x00c2:
            android.content.Context r3 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r3 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r3)
            int r3 = r3.l()
            if (r3 != 0) goto L_0x00d8
            java.lang.String r1 = "init history no history save"
            com.miui.powercenter.batteryhistory.b.b.a(r1)
        L_0x00d3:
            long r1 = r0.b(r10, r15)
            goto L_0x012b
        L_0x00d8:
            boolean r3 = r0.a((long) r10, (long) r1)
            if (r3 == 0) goto L_0x00f1
            java.lang.String r1 = "init history overage reset"
            com.miui.powercenter.batteryhistory.b.b.a(r1)
            r0.b((long) r12)
            long r1 = com.miui.powercenter.batteryhistory.b.a.a()
            long r3 = android.os.SystemClock.elapsedRealtime()
            long r10 = r1 + r3
            goto L_0x00d3
        L_0x00f1:
            int r3 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r3 <= 0) goto L_0x010b
            long r3 = r10 - r5
            long r3 = java.lang.Math.max(r3, r8)
            android.content.Context r5 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r12 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r5)
            r13 = r10
            r5 = r15
            r15 = r3
            r17 = r1
            r19 = r5
            r12.a(r13, r15, r17, r19)
        L_0x010b:
            android.content.Context r1 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r1 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r1)
            long r1 = r1.g()
            android.content.Context r3 = r0.f6926b
            com.miui.powercenter.batteryhistory.q r3 = com.miui.powercenter.batteryhistory.C0513q.a((android.content.Context) r3)
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r3 = r3.d()
            long r1 = r10 - r1
            if (r3 == 0) goto L_0x0126
            long r3 = r3.shutdownDuration
            long r1 = r1 + r3
        L_0x0126:
            r3 = 3600000(0x36ee80, double:1.7786363E-317)
            long r1 = r3 - r1
        L_0x012b:
            boolean r3 = r22.f()
            if (r3 != 0) goto L_0x0137
            java.lang.String r1 = "init history judgeDataValid"
            com.miui.powercenter.batteryhistory.b.b.a(r1)
            return
        L_0x0137:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "init history curHistoryTime : "
            r3.append(r4)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            com.miui.powercenter.batteryhistory.b.b.a(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "init history next record history time(min) : "
            r3.append(r4)
            long r4 = r1 / r20
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            com.miui.powercenter.batteryhistory.b.b.a(r3)
            long r3 = android.os.SystemClock.elapsedRealtime()
            long r1 = r1 + r3
            r0.c((long) r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0516u.e():void");
    }

    private boolean f() {
        if (C0513q.a(this.f6926b).o()) {
            return true;
        }
        com.miui.powercenter.batteryhistory.b.b.a("judgeDataValid not valid");
        b(Long.MIN_VALUE);
        c();
        return false;
    }

    /* access modifiers changed from: private */
    @TargetApi(24)
    public void g() {
        com.miui.powercenter.batteryhistory.b.b.a("save history : " + C0519x.a(System.currentTimeMillis()));
        Map<Long, aa> b2 = com.miui.powercenter.batteryhistory.b.a.b();
        long longValue = ((Long) new ArrayList(b2.keySet()).get(0)).longValue();
        aa aaVar = b2.get(Long.valueOf(longValue));
        long elapsedRealtime = longValue + SystemClock.elapsedRealtime();
        long[] h = C0513q.a(this.f6926b).h();
        if (h == null) {
            com.miui.powercenter.batteryhistory.b.b.a("save history get last history time null");
        }
        long a2 = aaVar != null ? aaVar.a() : 0;
        boolean a3 = a(a2);
        if (!a3 && (a3 = a(elapsedRealtime, 0))) {
            com.miui.powercenter.batteryhistory.b.b.a("save history reset");
            b(a2);
        }
        if (a3) {
            elapsedRealtime = com.miui.powercenter.batteryhistory.b.a.a() + SystemClock.elapsedRealtime();
        }
        a(elapsedRealtime, (List<BatteryData>) null);
        if (!f()) {
            com.miui.powercenter.batteryhistory.b.b.a("save history judgeDataValid");
            return;
        }
        long j = 3600000;
        if (a3) {
            com.miui.powercenter.batteryhistory.b.b.a("save history reset");
            int i = Calendar.getInstance().get(12);
            j = (long) ((i >= 55 ? 120 - i : 60 - i) * 60 * 1000);
        } else if (h != null) {
            if (Math.abs((System.currentTimeMillis() - h[1]) - (elapsedRealtime - h[0])) <= 300000) {
                int i2 = Calendar.getInstance().get(12);
                if (i2 >= 10) {
                    com.miui.powercenter.batteryhistory.b.b.a("save history change next time error(min) : " + i2);
                } else {
                    j = (long) ((60 - i2) * 60 * 1000);
                }
            }
        }
        com.miui.powercenter.batteryhistory.b.b.a("save history curHistoryTime : " + elapsedRealtime);
        com.miui.powercenter.batteryhistory.b.b.a("save history next record history time(min) : " + (j / 60000));
        c(SystemClock.elapsedRealtime() + j);
    }

    public void a() {
        this.f6928d.sendEmptyMessage(2);
    }

    public void a(boolean z) {
        Message obtainMessage = this.f6928d.obtainMessage(4);
        obtainMessage.obj = Boolean.valueOf(z);
        this.f6928d.removeMessages(4);
        this.f6928d.sendMessage(obtainMessage);
    }

    public void b() {
        this.f6928d.removeMessages(3);
        this.f6928d.sendEmptyMessage(3);
    }

    public void onAlarm() {
        this.f6928d.sendEmptyMessage(2);
    }
}
