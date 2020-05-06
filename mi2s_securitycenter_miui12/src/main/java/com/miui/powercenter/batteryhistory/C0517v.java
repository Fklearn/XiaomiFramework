package com.miui.powercenter.batteryhistory;

import android.content.ContentProvider;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import b.b.c.j.B;
import b.b.o.g.e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.b.b;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.legacypowerrank.i;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: com.miui.powercenter.batteryhistory.v  reason: case insensitive filesystem */
public class C0517v {

    /* renamed from: a  reason: collision with root package name */
    private Context f6931a;

    /* renamed from: b  reason: collision with root package name */
    private long f6932b;

    /* renamed from: c  reason: collision with root package name */
    private long f6933c;

    /* renamed from: d  reason: collision with root package name */
    private long f6934d;
    private C0514s.a e;
    private List<BatteryData> f;
    private List<BatteryData> g;
    private List<BatteryData> h;
    private List<BatteryHistogramItem> i;
    private List<BatteryShutdownItem> j;
    private WeakHashMap<ca, Integer> k = new WeakHashMap<>();
    private HandlerThread l;
    private Handler m;
    private AtomicBoolean n = new AtomicBoolean(false);
    private AtomicBoolean o = new AtomicBoolean(false);

    /* renamed from: com.miui.powercenter.batteryhistory.v$a */
    private class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            if (message.what == 1) {
                C0517v.this.j();
            }
            super.handleMessage(message);
        }
    }

    public C0517v(Context context) {
        this.f6931a = context.getApplicationContext();
        this.l = new HandlerThread("BatteryHistorySource");
        this.l.start();
        this.m = new a(this.l.getLooper());
    }

    private void a(long j2) {
        List<BatteryHistogramItem> list = this.i;
        if (list != null && list.size() != 0) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(j2);
            int i2 = instance.get(12);
            instance.set(12, 0);
            instance.set(13, 0);
            instance.set(14, 0);
            if (i2 == 0) {
                instance.setTimeInMillis(instance.getTimeInMillis() - 3600000);
            }
            long timeInMillis = instance.getTimeInMillis();
            for (int size = this.i.size() - 1; size >= 0; size += -1) {
                BatteryHistogramItem batteryHistogramItem = this.i.get(size);
                batteryHistogramItem.startUTCTime = timeInMillis;
                if (size == this.i.size() - 1) {
                    batteryHistogramItem.minLastItemHold = (long) i2;
                    b.a("last item min hold : " + i2);
                }
                b.a("histogram time : " + C0519x.a(batteryHistogramItem.startUTCTime));
                timeInMillis -= 3600000;
            }
        }
    }

    private void a(boolean z) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("key_batteryhistory_forceinvalid", z);
        this.f6931a.getContentResolver().call(h(), "checkInvalid", (String) null, bundle);
    }

    private void d() {
        this.f6931a.getContentResolver().call(h(), "checkReset", (String) null, (Bundle) null);
    }

    private void e() {
        C0514s.a aVar;
        List<aa> list;
        boolean z;
        List<BatteryShutdownItem> list2 = this.j;
        if (list2 != null && list2.size() != 0 && (aVar = this.e) != null && (list = aVar.f6922a) != null && list.size() != 0) {
            long j2 = 0;
            for (int i2 = 0; i2 < this.j.size(); i2++) {
                BatteryShutdownItem batteryShutdownItem = this.j.get(i2);
                int i3 = 0;
                while (true) {
                    if (i3 >= this.e.f6922a.size()) {
                        z = false;
                        break;
                    } else if (this.e.f6922a.get(i3).f6864a >= batteryShutdownItem.shutDownTime) {
                        j2 += batteryShutdownItem.shutDownDuration;
                        batteryShutdownItem.shutDownIndex = i3;
                        batteryShutdownItem.shutDownPlusTime = j2;
                        z = true;
                        break;
                    } else {
                        i3++;
                    }
                }
                if (!z) {
                    b.a("correctHistoryLineData not find error");
                }
                if (i3 == this.e.f6922a.size()) {
                    break;
                }
            }
            for (int i4 = 0; i4 < this.j.size(); i4++) {
                BatteryShutdownItem batteryShutdownItem2 = this.j.get(i4);
                int i5 = batteryShutdownItem2.shutDownIndex;
                int size = this.e.f6922a.size();
                if (i4 < this.j.size() - 1) {
                    size = this.j.get(i4 + 1).shutDownIndex;
                }
                if (i5 >= 0 && i5 < this.e.f6922a.size()) {
                    while (i5 < size) {
                        this.e.f6922a.get(i5).f6864a += batteryShutdownItem2.shutDownPlusTime;
                        i5++;
                    }
                }
            }
        }
    }

    private void f() {
        Bundle call = this.f6931a.getContentResolver().call(h(), "getBatteryHistogram", (String) null, (Bundle) null);
        if (call != null) {
            call.setClassLoader(BatteryHistogramItem.class.getClassLoader());
            this.i = call.getParcelableArrayList("key_batteryhistory_histogram");
        }
    }

    private void g() {
        Bundle call = this.f6931a.getContentResolver().call(h(), "getBatteryHistory", (String) null, (Bundle) null);
        if (call != null) {
            call.setClassLoader(BatteryData.class.getClassLoader());
            this.f6932b = call.getLong("key_batteryhistory_firsttime", -1);
            this.f6933c = call.getLong("key_batteryhistory_lasttime", -1);
            this.f6934d = call.getLong("key_batteryhistory_resettime", -1);
            this.g = call.getParcelableArrayList("key_batteryhistory_firsthistory");
            this.h = call.getParcelableArrayList("key_batteryhistory_lasthistory");
        }
    }

    private Uri h() {
        if (B.f()) {
            return Uri.parse("content://com.miui.powercenter.batteryhistory");
        }
        Uri parse = Uri.parse("content://com.miui.powercenter.batteryhistory");
        try {
            return (Uri) e.a((Class<?>) ContentProvider.class, Uri.class, "maybeAddUserId", (Class<?>[]) new Class[]{Uri.class, Integer.TYPE}, parse, 0);
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return parse;
        } catch (IllegalAccessException e3) {
            e3.printStackTrace();
            return parse;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return parse;
        }
    }

    private void i() {
        Bundle call = this.f6931a.getContentResolver().call(h(), "getBatteryShutDown", (String) null, (Bundle) null);
        if (call != null) {
            call.setClassLoader(BatteryShutdownItem.class.getClassLoader());
            this.j = call.getParcelableArrayList("key_batteryhistory_shutdown");
        }
    }

    /* access modifiers changed from: private */
    public void j() {
        if (Build.VERSION.SDK_INT < 24) {
            b.a("loadHistoryFullData to origin 1");
            k();
            return;
        }
        g();
        f();
        i();
        if (!this.n.get()) {
            long currentTimeMillis = System.currentTimeMillis();
            if (this.g == null || this.f6932b < 0) {
                b.a("loadHistoryFullData to origin 2");
                k();
                return;
            }
            ArrayList arrayList = new ArrayList(30);
            i.d();
            List<BatteryData> a2 = i.a();
            List<BatteryData> b2 = i.b();
            arrayList.addAll(a2);
            arrayList.addAll(b2);
            if (!this.n.get()) {
                this.e = C0514s.c().a();
                List<aa> list = this.e.f6922a;
                if (list.size() == 0) {
                    this.f6932b = 0;
                    this.g = null;
                    this.i = null;
                    this.o.set(true);
                    b.a("loadHistoryFullData to origin 3");
                    k();
                    a(true);
                } else if (!this.n.get()) {
                    if (this.f6934d != this.e.f6922a.get(0).f6864a) {
                        b.a("loadHistoryFullData to origin 4");
                        k();
                        a(true);
                        return;
                    }
                    int i2 = 0;
                    while (true) {
                        if (i2 >= list.size()) {
                            i2 = -1;
                            break;
                        } else if (list.get(i2).a() >= this.f6932b) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (i2 == -1) {
                        b.a("loadHistoryFullData to origin 5");
                        k();
                        a(true);
                        return;
                    }
                    List<aa> subList = list.subList(i2, list.size());
                    this.e.f6922a = subList;
                    if (!this.n.get()) {
                        this.f = com.miui.powercenter.batteryhistory.b.a.a(arrayList, this.g);
                        List<BatteryData> a3 = com.miui.powercenter.batteryhistory.b.a.a(arrayList, this.h);
                        BatteryHistogramItem batteryHistogramItem = new BatteryHistogramItem();
                        batteryHistogramItem.type = 0;
                        batteryHistogramItem.startTime = this.f6933c;
                        batteryHistogramItem.endTime = subList.get(subList.size() - 1).a();
                        batteryHistogramItem.batteryDataList = a3;
                        for (BatteryData next : a3) {
                            batteryHistogramItem.totalConsume += next.value;
                            int i3 = next.drainType;
                            if (i3 == 5) {
                                batteryHistogramItem.screenUsageTime = next.usageTime;
                            } else if (i3 == 0) {
                                batteryHistogramItem.idleUsageTime = next.usageTime;
                            }
                        }
                        if (this.i == null) {
                            this.i = new ArrayList();
                        }
                        this.i.add(batteryHistogramItem);
                        if (!com.miui.powercenter.batteryhistory.b.a.a(this.i)) {
                            b.a("loadHistoryFullData to origin 6");
                            k();
                            a(true);
                            return;
                        }
                        e();
                        a(currentTimeMillis);
                        this.o.set(true);
                        b.a("BatteryHistorySource load finished");
                        l();
                    }
                }
            }
        }
    }

    private void k() {
        this.i = new ArrayList();
        if (this.e == null) {
            this.e = C0514s.c().a();
        }
        this.f = new ArrayList(30);
        i.d();
        List<BatteryData> a2 = i.a();
        List<BatteryData> b2 = i.b();
        this.f.addAll(a2);
        this.f.addAll(b2);
        this.o.set(true);
        if (Build.VERSION.SDK_INT >= 24) {
            d();
        }
        l();
    }

    private synchronized void l() {
        if (!this.n.get()) {
            if (this.f == null) {
                this.f = new ArrayList();
            }
            if (this.i == null) {
                this.i = new ArrayList();
            }
            for (ca next : this.k.keySet()) {
                if (next != null) {
                    next.a(this.e, this.f, this.i);
                }
            }
        }
    }

    public synchronized void a() {
        this.m.sendEmptyMessage(1);
    }

    public synchronized void a(ca caVar) {
        this.k.put(caVar, 1);
        if (this.o.get() && caVar != null) {
            caVar.a(this.e, this.f, this.i);
        }
    }

    public synchronized void b() {
        this.m.removeMessages(1);
        this.m.removeMessages(2);
        this.k.clear();
        this.l.quitSafely();
        this.e = null;
        this.g = null;
        this.n.set(false);
    }

    public synchronized void b(ca caVar) {
        this.k.remove(caVar);
    }

    public boolean c() {
        return Build.VERSION.SDK_INT >= 24;
    }
}
