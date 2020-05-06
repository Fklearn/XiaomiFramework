package com.miui.powercenter.autotask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.miui.powercenter.autotask.AutoTask;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.powercenter.utils.h;
import com.miui.powercenter.utils.s;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.miui.powercenter.autotask.f  reason: case insensitive filesystem */
public class C0477f extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    private List<a> f6744a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private Context f6745b;

    /* renamed from: com.miui.powercenter.autotask.f$a */
    private static class a implements Comparable<a> {

        /* renamed from: a  reason: collision with root package name */
        long f6746a;

        /* renamed from: b  reason: collision with root package name */
        long f6747b;

        /* renamed from: c  reason: collision with root package name */
        boolean f6748c;

        private a() {
            this.f6748c = false;
        }

        /* renamed from: a */
        public int compareTo(a aVar) {
            return (int) (this.f6747b - aVar.f6747b);
        }
    }

    public C0477f(Context context) {
        this.f6745b = context;
        b();
        d();
    }

    private long a(AutoTask.a aVar, int i) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        instance.set(11, aVar.f6673a);
        instance.set(12, aVar.f6674b);
        instance.set(13, 0);
        instance.set(14, 0);
        h.a(this.f6745b, i, instance);
        return instance.getTimeInMillis();
    }

    private void a() {
        Intent intent = new Intent(this.f6745b, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.TIME_AUTO_TASK_ALARM");
        ((AlarmManager) this.f6745b.getSystemService("alarm")).cancel(PendingIntent.getService(this.f6745b, 0, intent, 134217728));
    }

    private void a(long j) {
        a aVar = new a();
        aVar.f6746a = j;
        b(aVar);
        d();
    }

    private void a(long j, long j2, boolean z) {
        Intent intent = new Intent(this.f6745b, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.TIME_AUTO_TASK_ALARM");
        intent.putExtra("task_id", j2);
        intent.putExtra("task_restore", z);
        ((AlarmManager) this.f6745b.getSystemService("alarm")).setExact(0, j, PendingIntent.getService(this.f6745b, 0, intent, 134217728));
    }

    private void a(AutoTask autoTask) {
        a b2 = b(autoTask);
        if (b2 != null) {
            a(b2);
        }
    }

    private void a(a aVar) {
        b(aVar);
        this.f6744a.add(aVar);
    }

    private a b(AutoTask autoTask) {
        long a2;
        a aVar = new a();
        aVar.f6746a = autoTask.getId();
        int repeatType = autoTask.getRepeatType();
        if (autoTask.hasCondition("hour_minute")) {
            a2 = a(AutoTask.getHourMinute(((Integer) autoTask.getCondition("hour_minute")).intValue()), repeatType);
        } else if (!autoTask.hasCondition("hour_minute_duration")) {
            return null;
        } else {
            AutoTask.b bVar = new AutoTask.b(((Integer) autoTask.getCondition("hour_minute_duration")).intValue());
            a2 = a(AutoTask.getHourMinute(bVar.f6675a), repeatType);
            long a3 = a(AutoTask.getHourMinute(bVar.f6676b), 127);
            if (autoTask.getStarted() && a3 < a2) {
                aVar.f6747b = a3;
                aVar.f6748c = true;
                return aVar;
            }
        }
        aVar.f6747b = a2;
        return aVar;
    }

    private void b() {
        c();
    }

    private void b(long j) {
        AutoTask b2 = C0489s.b(this.f6745b, j);
        if (b2 == null) {
            Log.e("AutoTaskAlarmReceiver", "get task null, id " + j);
            return;
        }
        c(b2);
    }

    private void b(a aVar) {
        Iterator<a> it = this.f6744a.iterator();
        while (it.hasNext()) {
            if (it.next().f6746a == aVar.f6746a) {
                it.remove();
            }
        }
    }

    private void c() {
        Cursor c2 = C0489s.c(this.f6745b);
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

    private void c(AutoTask autoTask) {
        if (!autoTask.getEnabled()) {
            a aVar = new a();
            aVar.f6746a = autoTask.getId();
            b(aVar);
        } else {
            a(autoTask);
        }
        d();
    }

    private void d() {
        if (this.f6744a.isEmpty()) {
            a();
            return;
        }
        Collections.sort(this.f6744a);
        ArrayList arrayList = new ArrayList();
        long j = 0;
        for (a next : this.f6744a) {
            if (j != 0) {
                if (next.f6747b > j) {
                    break;
                }
                arrayList.add(next);
            } else {
                long j2 = next.f6747b;
                arrayList.add(next);
                j = j2;
            }
        }
        if (j == 0) {
            a();
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            Log.d("AutoTaskAlarmReceiver", "enabled task id " + ((a) arrayList.get(i)).f6746a + " minTime " + s.b(j));
            a(j, ((a) arrayList.get(i)).f6746a, ((a) arrayList.get(i)).f6748c);
        }
    }

    private void e() {
        this.f6744a.clear();
        c();
        d();
    }

    public void a(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.powercenter.action.TASK_UPDATE");
        intentFilter.addAction("com.miui.powercenter.action.TASK_DELETE");
        intentFilter.addAction("com.miui.powercenter.action.TASK_RESET");
        LocalBroadcastManager.getInstance(context).registerReceiver(this, intentFilter);
    }

    public void a(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("AutoTaskAlarmReceiver info begin");
        printWriter.println("Task size " + this.f6744a.size());
        for (a next : this.f6744a) {
            printWriter.println("id " + next.f6746a + " time " + s.b(next.f6747b) + " restore " + next.f6748c);
        }
        printWriter.println(TtmlNode.END);
    }

    public void b(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    public void onReceive(Context context, Intent intent) {
        if ("com.miui.powercenter.action.TASK_UPDATE".equals(intent.getAction())) {
            long longExtra = intent.getLongExtra("id", -1);
            if (longExtra >= 0) {
                b(longExtra);
            }
        } else if ("com.miui.powercenter.action.TASK_DELETE".equals(intent.getAction())) {
            long[] longArrayExtra = intent.getLongArrayExtra("ids");
            if (longArrayExtra != null && longArrayExtra.length > 0) {
                for (long a2 : longArrayExtra) {
                    a(a2);
                }
            }
        } else if ("com.miui.powercenter.action.TASK_RESET".equals(intent.getAction())) {
            Log.i("AutoTaskAlarmReceiver", "time changed");
            e();
        }
    }
}
