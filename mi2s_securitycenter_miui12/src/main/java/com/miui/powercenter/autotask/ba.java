package com.miui.powercenter.autotask;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.miui.powercenter.utils.j;
import com.miui.powercenter.utils.n;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.y;
import com.miui.securitycenter.Application;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ba {

    private static class a implements Comparator<String> {
        private a() {
        }

        /* synthetic */ a(Y y) {
            this();
        }

        /* renamed from: a */
        public int compare(String str, String str2) {
            if (str.equals("airplane_mode")) {
                return -1;
            }
            if (str2.equals("airplane_mode")) {
                return 1;
            }
            return str.compareTo(str2);
        }

        public boolean equals(Object obj) {
            return false;
        }
    }

    private static Object a(String str) {
        int d2;
        if ("airplane_mode".equals(str)) {
            return Integer.valueOf(n.a((Context) Application.d()).a() ? 1 : 0);
        }
        if ("auto_clean_memory".equals(str)) {
            d2 = Integer.valueOf(y.l()).intValue() / 60;
        } else if ("bluetooth".equals(str)) {
            return Integer.valueOf(n.a((Context) Application.d()).b() ? 1 : 0);
        } else {
            if ("brightness".equals(str)) {
                d2 = n.a((Context) Application.d()).c();
            } else if ("gps".equals(str)) {
                d2 = o.g(Application.d());
            } else if ("internet".equals(str)) {
                return Integer.valueOf(n.a((Context) Application.d()).f() ? 1 : 0);
            } else {
                if ("sleep".equals(str)) {
                    d2 = n.a((Context) Application.d()).g();
                } else if ("synchronization".equals(str)) {
                    return Integer.valueOf(n.a((Context) Application.d()).h() ? 1 : 0);
                } else {
                    if ("vibration".equals(str)) {
                        return Integer.valueOf(n.a((Context) Application.d()).k() ? 1 : 0);
                    }
                    if ("wifi".equals(str)) {
                        return Integer.valueOf(n.a((Context) Application.d()).l() ? 1 : 0);
                    }
                    if ("mute".equals(str)) {
                        return Integer.valueOf(n.a((Context) Application.d()).n() ? 1 : 0);
                    }
                    if ("auto_brightness".equals(str)) {
                        d2 = n.a((Context) Application.d()).d();
                    } else if ("save_mode".equals(str)) {
                        return Integer.valueOf(o.l(Application.d()) ? 1 : 0);
                    } else {
                        throw new IllegalArgumentException("Unknown " + str);
                    }
                }
            }
        }
        return Integer.valueOf(d2);
    }

    public static void a(Context context, AutoTask autoTask) {
        Object obj;
        boolean z;
        Log.d("OperationsHelper", "Restore task id " + autoTask.getId());
        HashMap hashMap = new HashMap();
        if (b()) {
            List<String> restoreOperationNames = autoTask.getRestoreOperationNames();
            Collections.sort(restoreOperationNames, new a((Y) null));
            obj = null;
            z = false;
            for (String next : restoreOperationNames) {
                Object operation = autoTask.getOperation(next);
                if (a(context, next) || operation.equals(a(next))) {
                    if ("airplane_mode".equals(next)) {
                        z = true;
                        obj = autoTask.getRestoreOperation(next);
                    } else {
                        hashMap.put(next, autoTask.getRestoreOperation(next));
                    }
                }
            }
        } else {
            obj = null;
            z = false;
        }
        autoTask.setStarted(false);
        autoTask.removeAllRestoreOperation();
        if (autoTask.getRepeatType() == 0) {
            autoTask.setEnabled(false);
        }
        C0489s.a((Context) Application.d(), autoTask);
        if (b()) {
            C0495y.a((Context) Application.d(), ea.c(Application.d(), autoTask));
        }
        long j = 0;
        if (z) {
            a(obj);
            j = 1000;
        }
        if (!hashMap.isEmpty()) {
            a((Map<String, Object>) hashMap, j);
        }
    }

    public static void a(AutoTask autoTask) {
        a(autoTask, false);
    }

    public static void a(AutoTask autoTask, boolean z) {
        boolean z2;
        Log.d("OperationsHelper", "Apply task id " + autoTask.getId());
        boolean z3 = z || a();
        boolean a2 = u.a();
        autoTask.removeAllRestoreOperation();
        Application d2 = Application.d();
        HashMap hashMap = new HashMap();
        Object obj = null;
        if (!b() || !z3 || a2) {
            z2 = false;
        } else {
            List<String> operationNames = autoTask.getOperationNames();
            Collections.sort(operationNames, new a((Y) null));
            int size = operationNames.size();
            z2 = false;
            Object obj2 = null;
            for (int i = 0; i < size; i++) {
                String str = operationNames.get(i);
                autoTask.setRestoreOperation(str, a(str));
                if ("airplane_mode".equals(str)) {
                    obj2 = autoTask.getOperation(str);
                    z2 = true;
                } else {
                    hashMap.put(str, autoTask.getOperation(str));
                }
            }
            if (autoTask.isPeriodTask()) {
                autoTask.setStarted(true);
            }
            obj = obj2;
        }
        if (autoTask.getRepeatType() == 0 && !autoTask.isPeriodTask()) {
            autoTask.setEnabled(false);
        }
        C0489s.a((Context) d2, autoTask);
        if (!z3) {
            C0495y.a((Context) d2, autoTask.getId());
        }
        if (a2) {
            C0495y.d(d2, autoTask.getId());
        } else if (b()) {
            C0495y.a((Context) d2, ea.c(d2, autoTask), autoTask.getId());
        }
        long j = 0;
        if (z2) {
            a(obj);
            j = 1000;
        }
        if (!hashMap.isEmpty()) {
            a((Map<String, Object>) hashMap, j);
        }
    }

    private static void a(Object obj) {
        new Y(obj).execute(new Void[0]);
    }

    private static void a(Map<String, Object> map, long j) {
        new Handler().postDelayed(new aa(map), j);
    }

    private static boolean a() {
        return ((TelephonyManager) Application.d().getSystemService("phone")).getCallState() == 0;
    }

    private static boolean a(Context context, String str) {
        return "brightness".equals(str) && Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode", 0) == 1;
    }

    /* access modifiers changed from: private */
    public static void b(String str, Object obj) {
        boolean z = true;
        if ("airplane_mode".equals(str)) {
            Integer num = (Integer) obj;
            if (num.intValue() != 2) {
                n a2 = n.a((Context) Application.d());
                if (num.intValue() == 0) {
                    z = false;
                }
                a2.a(z);
            }
        } else if ("auto_clean_memory".equals(str)) {
            y.d(((Integer) obj).intValue() * 60);
        } else if ("bluetooth".equals(str)) {
            Integer num2 = (Integer) obj;
            if (num2.intValue() != 2) {
                n a3 = n.a((Context) Application.d());
                if (num2.intValue() == 0) {
                    z = false;
                }
                a3.b(z);
            }
        } else if ("brightness".equals(str)) {
            n.a((Context) Application.d()).a(((Integer) obj).intValue());
        } else if ("gps".equals(str)) {
            o.a((Context) Application.d(), ((Integer) obj).intValue());
        } else if ("internet".equals(str)) {
            n.a((Context) Application.d()).c(((Integer) obj).intValue());
        } else if ("sleep".equals(str)) {
            n.a((Context) Application.d()).d(((Integer) obj).intValue());
        } else if ("synchronization".equals(str)) {
            Integer num3 = (Integer) obj;
            if (num3.intValue() != 2) {
                n a4 = n.a((Context) Application.d());
                if (num3.intValue() == 0) {
                    z = false;
                }
                a4.d(z);
            }
        } else if ("vibration".equals(str)) {
            Integer num4 = (Integer) obj;
            if (num4.intValue() != 2) {
                n a5 = n.a((Context) Application.d());
                if (num4.intValue() == 0) {
                    z = false;
                }
                a5.g(z);
            }
        } else if ("wifi".equals(str)) {
            Integer num5 = (Integer) obj;
            if (num5.intValue() != 2) {
                n a6 = n.a((Context) Application.d());
                if (num5.intValue() == 0) {
                    z = false;
                }
                a6.h(z);
            }
        } else if ("mute".equals(str)) {
            Integer num6 = (Integer) obj;
            if (num6.intValue() != 2) {
                n a7 = n.a((Context) Application.d());
                if (num6.intValue() == 0) {
                    z = false;
                }
                a7.c(z);
            }
        } else if ("auto_brightness".equals(str)) {
            if (((Integer) obj).intValue() == 0) {
                z = false;
            }
            n.a((Context) Application.d()).b(z ? 1 : 0);
        } else if ("save_mode".equals(str)) {
            Integer num7 = (Integer) obj;
            if (num7.intValue() != 2) {
                Application d2 = Application.d();
                if (num7.intValue() == 0) {
                    z = false;
                }
                o.a((Context) d2, z);
            }
        }
    }

    private static boolean b() {
        return j.b();
    }
}
