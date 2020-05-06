package com.miui.powercenter.autotask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import com.miui.powercenter.utils.o;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/* renamed from: com.miui.powercenter.autotask.h  reason: case insensitive filesystem */
public class C0479h extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private SortedSet<c> f6749a = new TreeSet(new b());

    /* renamed from: b  reason: collision with root package name */
    private SortedSet<c> f6750b = new TreeSet(new a());

    /* renamed from: c  reason: collision with root package name */
    private int f6751c = -1;

    /* renamed from: d  reason: collision with root package name */
    private boolean f6752d = false;

    /* renamed from: com.miui.powercenter.autotask.h$a */
    private static class a implements Comparator<c> {
        private a() {
        }

        /* renamed from: a */
        public int compare(c cVar, c cVar2) {
            if (cVar.f6753a == cVar2.f6753a) {
                return 0;
            }
            return cVar.f6754b - cVar2.f6754b;
        }
    }

    /* renamed from: com.miui.powercenter.autotask.h$b */
    private static class b implements Comparator<c> {
        private b() {
        }

        /* renamed from: a */
        public int compare(c cVar, c cVar2) {
            if (cVar.f6753a == cVar2.f6753a) {
                return 0;
            }
            return cVar2.f6754b - cVar.f6754b;
        }
    }

    /* renamed from: com.miui.powercenter.autotask.h$c */
    private static class c {

        /* renamed from: a  reason: collision with root package name */
        long f6753a;

        /* renamed from: b  reason: collision with root package name */
        int f6754b;

        /* renamed from: c  reason: collision with root package name */
        boolean f6755c;

        /* renamed from: d  reason: collision with root package name */
        int f6756d;

        private c() {
        }
    }

    public C0479h(Context context) {
        c(context);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0033, code lost:
        r2 = (com.miui.powercenter.autotask.C0479h.c) r1.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0013, code lost:
        r2 = (com.miui.powercenter.autotask.C0479h.c) r1.next();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.content.Context r6, int r7, boolean r8) {
        /*
            r5 = this;
            int r0 = r5.f6751c
            if (r0 == r7) goto L_0x004c
            r0 = 0
            if (r8 != 0) goto L_0x0027
            java.util.SortedSet<com.miui.powercenter.autotask.h$c> r1 = r5.f6749a
            java.util.Iterator r1 = r1.iterator()
        L_0x000d:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x004a
            java.lang.Object r2 = r1.next()
            com.miui.powercenter.autotask.h$c r2 = (com.miui.powercenter.autotask.C0479h.c) r2
            int r3 = r2.f6754b
            if (r3 < r7) goto L_0x004a
            int r4 = r5.f6751c
            if (r3 >= r4) goto L_0x004a
            long r2 = r2.f6753a
            com.miui.powercenter.autotask.C0475d.b(r6, r2, r0)
            goto L_0x000d
        L_0x0027:
            java.util.SortedSet<com.miui.powercenter.autotask.h$c> r1 = r5.f6750b
            java.util.Iterator r1 = r1.iterator()
        L_0x002d:
            boolean r2 = r1.hasNext()
            if (r2 == 0) goto L_0x004a
            java.lang.Object r2 = r1.next()
            com.miui.powercenter.autotask.h$c r2 = (com.miui.powercenter.autotask.C0479h.c) r2
            int r3 = r2.f6754b
            int r4 = r5.f6751c
            if (r3 <= r4) goto L_0x004a
            if (r3 > r7) goto L_0x004a
            r3 = -1
            if (r4 == r3) goto L_0x004a
            long r2 = r2.f6753a
            com.miui.powercenter.autotask.C0475d.b(r6, r2, r0)
            goto L_0x002d
        L_0x004a:
            r5.f6751c = r7
        L_0x004c:
            boolean r7 = r5.f6752d
            if (r7 == r8) goto L_0x0075
            if (r8 == 0) goto L_0x0073
            java.util.SortedSet<com.miui.powercenter.autotask.h$c> r7 = r5.f6749a
            java.util.Iterator r7 = r7.iterator()
        L_0x0058:
            boolean r0 = r7.hasNext()
            if (r0 == 0) goto L_0x0073
            java.lang.Object r0 = r7.next()
            com.miui.powercenter.autotask.h$c r0 = (com.miui.powercenter.autotask.C0479h.c) r0
            boolean r1 = r0.f6755c
            if (r1 == 0) goto L_0x0058
            int r1 = r0.f6756d
            r2 = 1
            if (r1 != r2) goto L_0x0058
            long r0 = r0.f6753a
            com.miui.powercenter.autotask.C0475d.b(r6, r0, r2)
            goto L_0x0058
        L_0x0073:
            r5.f6752d = r8
        L_0x0075:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.autotask.C0479h.a(android.content.Context, int, boolean):void");
    }

    private void a(Context context, long j) {
        c cVar = new c();
        cVar.f6753a = j;
        this.f6749a.remove(cVar);
        this.f6750b.remove(cVar);
    }

    private void a(AutoTask autoTask) {
        SortedSet<c> sortedSet;
        c b2 = b(autoTask);
        if (autoTask.hasCondition("battery_level_down")) {
            this.f6749a.remove(b2);
            sortedSet = this.f6749a;
        } else if (autoTask.hasCondition("battery_level_up")) {
            this.f6750b.remove(b2);
            sortedSet = this.f6750b;
        } else {
            return;
        }
        sortedSet.add(b2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0033, code lost:
        if (r4.hasCondition(r1) != false) goto L_0x0020;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.miui.powercenter.autotask.C0479h.c b(com.miui.powercenter.autotask.AutoTask r4) {
        /*
            r3 = this;
            com.miui.powercenter.autotask.h$c r0 = new com.miui.powercenter.autotask.h$c
            r1 = 0
            r0.<init>()
            long r1 = r4.getId()
            r0.f6753a = r1
            boolean r1 = r4.getStarted()
            r0.f6755c = r1
            int r1 = r4.getRestoreLevel()
            r0.f6756d = r1
            java.lang.String r1 = "battery_level_down"
            boolean r2 = r4.hasCondition(r1)
            if (r2 == 0) goto L_0x002d
        L_0x0020:
            java.lang.Object r4 = r4.getCondition(r1)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            r0.f6754b = r4
            goto L_0x0036
        L_0x002d:
            java.lang.String r1 = "battery_level_up"
            boolean r2 = r4.hasCondition(r1)
            if (r2 == 0) goto L_0x0036
            goto L_0x0020
        L_0x0036:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.autotask.C0479h.b(com.miui.powercenter.autotask.AutoTask):com.miui.powercenter.autotask.h$c");
    }

    private void b(Context context, long j) {
        AutoTask b2 = C0489s.b(context, j);
        if (b2 == null) {
            a(context, j);
        } else if (!b2.getEnabled()) {
            c cVar = new c();
            cVar.f6753a = j;
            this.f6749a.remove(cVar);
            this.f6750b.remove(cVar);
        } else {
            a(b2);
        }
    }

    private void c(Context context) {
        this.f6751c = o.e(context);
        Cursor c2 = C0489s.c(context);
        try {
            if (c2.moveToFirst()) {
                do {
                    AutoTask autoTask = new AutoTask(c2);
                    if (autoTask.getEnabled()) {
                        a(autoTask);
                    }
                } while (c2.moveToNext());
            }
        } finally {
            c2.close();
        }
    }

    public void a(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        context.registerReceiver(this, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("com.miui.powercenter.action.TASK_UPDATE");
        intentFilter2.addAction("com.miui.powercenter.action.TASK_DELETE");
        LocalBroadcastManager.getInstance(context).registerReceiver(this, intentFilter2);
    }

    public void a(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("AutoTaskBatteryReceiver info begin");
        printWriter.println("Prev percent " + this.f6751c);
        printWriter.println("Level down size " + this.f6749a.size());
        for (c cVar : this.f6749a) {
            printWriter.println("id " + cVar.f6753a + " level " + cVar.f6754b + " started " + cVar.f6755c + " restore level " + cVar.f6756d);
        }
        printWriter.println("Level up size " + this.f6750b.size());
        for (c cVar2 : this.f6750b) {
            printWriter.println("id " + cVar2.f6753a + " level " + cVar2.f6754b + " started " + cVar2.f6755c + " restore level " + cVar2.f6756d);
        }
        printWriter.println(TtmlNode.END);
    }

    public void b(Context context) {
        context.unregisterReceiver(this);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r1v1 */
    /* JADX WARNING: type inference failed for: r1v2, types: [int] */
    /* JADX WARNING: type inference failed for: r1v5 */
    /* JADX WARNING: type inference failed for: r1v6 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onReceive(android.content.Context r6, android.content.Intent r7) {
        /*
            r5 = this;
            java.lang.String r0 = r7.getAction()
            java.lang.String r1 = "android.intent.action.BATTERY_CHANGED"
            boolean r0 = r1.equals(r0)
            r1 = 0
            if (r0 == 0) goto L_0x002e
            r0 = -1
            java.lang.String r2 = "status"
            int r2 = r7.getIntExtra(r2, r0)
            java.lang.String r3 = "level"
            int r3 = r7.getIntExtra(r3, r0)
            java.lang.String r4 = "scale"
            int r7 = r7.getIntExtra(r4, r0)
            int r3 = r3 * 100
            int r3 = r3 / r7
            r7 = 2
            if (r2 == r7) goto L_0x0029
            r7 = 5
            if (r2 != r7) goto L_0x002a
        L_0x0029:
            r1 = 1
        L_0x002a:
            r5.a((android.content.Context) r6, (int) r3, (boolean) r1)
            goto L_0x006e
        L_0x002e:
            java.lang.String r0 = r7.getAction()
            java.lang.String r2 = "com.miui.powercenter.action.TASK_UPDATE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x004c
            r0 = -1
            java.lang.String r2 = "id"
            long r0 = r7.getLongExtra(r2, r0)
            r2 = 0
            int r7 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r7 < 0) goto L_0x006e
            r5.b(r6, r0)
            goto L_0x006e
        L_0x004c:
            java.lang.String r0 = r7.getAction()
            java.lang.String r2 = "com.miui.powercenter.action.TASK_DELETE"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x006e
            java.lang.String r0 = "ids"
            long[] r7 = r7.getLongArrayExtra(r0)
            if (r7 == 0) goto L_0x006e
            int r0 = r7.length
            if (r0 <= 0) goto L_0x006e
        L_0x0063:
            int r0 = r7.length
            if (r1 >= r0) goto L_0x006e
            r2 = r7[r1]
            r5.a(r6, r2)
            int r1 = r1 + 1
            goto L_0x0063
        L_0x006e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.autotask.C0479h.onReceive(android.content.Context, android.content.Intent):void");
    }
}
