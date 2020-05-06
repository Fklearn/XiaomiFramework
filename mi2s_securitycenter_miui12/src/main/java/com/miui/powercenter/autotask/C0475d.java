package com.miui.powercenter.autotask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.B;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.powercenter.provider.PowerSaveService;
import com.miui.powercenter.utils.j;
import com.miui.powercenter.utils.u;
import java.util.Calendar;

/* renamed from: com.miui.powercenter.autotask.d  reason: case insensitive filesystem */
public class C0475d {
    private static Long a() {
        Calendar instance = Calendar.getInstance();
        instance.set(11, 0);
        instance.set(12, 0);
        instance.set(13, 0);
        return Long.valueOf(instance.getTimeInMillis());
    }

    public static void a(Context context, long j) {
        if (j <= 0) {
            Log.e("AutoTaskAlarmHelper", "cancelCountDownAlarm, error task id " + j);
            return;
        }
        ((AlarmManager) context.getSystemService("alarm")).cancel(c(context, 0, false));
        C0495y.a(context);
        C0489s.c(context, j);
    }

    public static void a(Context context, long j, boolean z) {
        Log.d("AutoTaskAlarmHelper", "applyOrRestoreTasks");
        AutoTask b2 = C0489s.b(context, j);
        if (b2 == null) {
            Log.e("AutoTaskAlarmHelper", "cannot find auto task, id " + j);
        } else if (!z) {
            Log.d("AutoTaskAlarmHelper", "apply task id " + b2.getId());
            ba.a(b2);
        } else {
            Log.d("AutoTaskAlarmHelper", "restore task id " + b2.getId());
            ba.a(context, b2);
        }
    }

    public static void b(Context context, long j, boolean z) {
        Context context2 = context;
        long j2 = j;
        boolean z2 = z;
        if (!u.d()) {
            Log.w("AutoTaskAlarmHelper", "not support in international build");
        } else if (!b()) {
            if (B.g()) {
                C0495y.b(context, j);
            } else {
                C0495y.c(context, j);
            }
        } else if (j2 <= 0) {
            Log.e("AutoTaskAlarmHelper", "setCountDownAlarm, error task id " + j2);
        } else {
            AutoTask b2 = C0489s.b(context, j);
            if (b2 == null) {
                Log.e("AutoTaskAlarmHelper", "task null, id " + j2);
                return;
            }
            if (b2.hasCondition("hour_minute")) {
                int intValue = ((Integer) b2.getCondition("hour_minute")).intValue();
                long currentTimeMillis = System.currentTimeMillis() + 1000;
                long j3 = ((long) (intValue * 60)) * 1000;
                if (currentTimeMillis - (a().longValue() + j3) <= 0 || currentTimeMillis - (a().longValue() + j3) >= 60000) {
                    return;
                }
            }
            if (b2.hasCondition("hour_minute_duration")) {
                int intValue2 = ((Integer) b2.getCondition("hour_minute_duration")).intValue();
                int i = intValue2 >> 16;
                int i2 = intValue2 & 65535;
                long currentTimeMillis2 = System.currentTimeMillis() + 1000;
                if (!z2) {
                    if (i < i2) {
                        if (currentTimeMillis2 - (a().longValue() + (((long) (i * 60)) * 1000)) <= 0 || currentTimeMillis2 - (a().longValue() + (((long) (i2 * 60)) * 1000)) >= 0) {
                            return;
                        }
                    } else if (i > i2 && currentTimeMillis2 - (a().longValue() + (((long) (i * 60)) * 1000)) <= 0 && currentTimeMillis2 - (a().longValue() + (((long) (i2 * 60)) * 1000)) >= 0) {
                        return;
                    }
                } else if (i < i2) {
                    if (currentTimeMillis2 - (a().longValue() + (((long) (i * 60)) * 1000)) >= 0 && currentTimeMillis2 - (a().longValue() + (((long) (i2 * 60)) * 1000)) <= 0) {
                        return;
                    }
                } else if (i > i2 && (currentTimeMillis2 - (a().longValue() + (((long) (i * 60)) * 1000)) >= 0 || currentTimeMillis2 - (a().longValue() + (((long) (i2 * 60)) * 1000)) <= 0)) {
                    return;
                }
            }
            ((AlarmManager) context2.getSystemService("alarm")).setExact(0, System.currentTimeMillis() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, c(context, j, z));
            C0495y.a(context2, ea.c(context2, b2), j2, z2);
        }
    }

    private static boolean b() {
        return j.b();
    }

    private static PendingIntent c(Context context, long j, boolean z) {
        Intent intent = new Intent(context, PowerSaveService.class);
        intent.setAction("com.miui.powercenter.action.APPLY_AUTO_TASK_ALARM");
        intent.putExtra("task_id", j);
        intent.putExtra("task_restore", z);
        return PendingIntent.getService(context, 0, intent, 134217728);
    }
}
