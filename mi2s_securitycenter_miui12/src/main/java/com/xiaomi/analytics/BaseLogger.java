package com.xiaomi.analytics;

import android.content.Context;
import android.text.TextUtils;
import com.xiaomi.analytics.a.a.b;
import com.xiaomi.analytics.a.a.p;
import com.xiaomi.analytics.a.b.a;
import com.xiaomi.analytics.a.i;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

class BaseLogger {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static volatile a f8252a;

    /* renamed from: b  reason: collision with root package name */
    private static String f8253b;

    /* renamed from: c  reason: collision with root package name */
    private static Context f8254c;

    /* renamed from: d  reason: collision with root package name */
    private static ConcurrentLinkedQueue<PendingUnit> f8255d = new ConcurrentLinkedQueue<>();
    private static i.a e = new i.a() {
        public void a(a aVar) {
            a unused = BaseLogger.f8252a = aVar;
            BaseLogger.b();
        }
    };
    private String f = "";
    private String g = "";

    private static class PendingUnit {

        /* renamed from: a  reason: collision with root package name */
        String f8256a;

        /* renamed from: b  reason: collision with root package name */
        String f8257b;

        /* renamed from: c  reason: collision with root package name */
        String f8258c;

        /* renamed from: d  reason: collision with root package name */
        LogEvent f8259d;

        public PendingUnit(String str, String str2, String str3, LogEvent logEvent) {
            this.f8257b = str2;
            this.f8258c = str3;
            this.f8259d = logEvent;
            this.f8256a = str;
        }
    }

    BaseLogger(String str) {
        if (f8254c != null) {
            this.g = str;
            return;
        }
        throw new IllegalStateException("Do you forget to do Logger.init ?");
    }

    static synchronized void a(Context context) {
        synchronized (BaseLogger.class) {
            f8254c = b.a(context);
            f8253b = f8254c.getPackageName();
            if (!TextUtils.isEmpty(f8253b)) {
                i.a(f8254c).a(e);
            } else {
                throw new IllegalArgumentException("Context is not a application context.");
            }
        }
    }

    /* access modifiers changed from: private */
    public static void b() {
        if (f8255d.size() > 0 && f8252a != null) {
            com.xiaomi.analytics.a.a.a.a("BaseLogger", "drainPendingEvents ");
            ArrayList arrayList = new ArrayList();
            while (f8255d.size() > 0) {
                PendingUnit poll = f8255d.poll();
                arrayList.add(poll.f8259d.a(poll.f8256a, poll.f8257b, poll.f8258c));
            }
            int i = 0;
            while (i < arrayList.size()) {
                ArrayList arrayList2 = new ArrayList();
                while (arrayList2.size() < 100 && i < arrayList.size()) {
                    arrayList2.add((String) arrayList.get(i));
                    i++;
                }
                com.xiaomi.analytics.a.a.a.a("BaseLogger", "trackEvents " + arrayList2.size());
                f8252a.trackEvents((String[]) p.a(arrayList2, String.class));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(LogEvent logEvent) {
        if (logEvent != null) {
            f8252a = i.a(f8254c).d();
            i.a(f8254c).f();
            if (f8252a != null) {
                f8252a.trackEvent(logEvent.a(f8253b, this.g, this.f));
            } else {
                f8255d.offer(new PendingUnit(f8253b, this.g, this.f, logEvent));
            }
        }
    }
}
