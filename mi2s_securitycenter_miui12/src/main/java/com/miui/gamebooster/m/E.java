package com.miui.gamebooster.m;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.util.Log;
import android.view.WindowManager;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import java.util.List;

public class E {
    public static void a() {
        try {
            Object a2 = e.a(Class.forName("android.app.ActivityManager"), "getService", (Class<?>[]) null, new Object[0]);
            for (Object next : (List) e.a(a2, "getAllStackInfos", (Class<?>[]) null, new Object[0])) {
                if (((Integer) e.a(e.a(e.a(next, "configuration"), "windowConfiguration"), "getWindowingMode", (Class<?>[]) null, new Object[0])).intValue() == ((Integer) e.a(Class.forName("android.app.WindowConfiguration"), "WINDOWING_MODE_FREEFORM")).intValue()) {
                    e.a(a2, "setTaskWindowingMode", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE, Boolean.TYPE}, Integer.valueOf(((int[]) e.a(next, "taskIds"))[0]), 1, false);
                }
            }
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", e.toString());
        }
    }

    public static void a(WindowManager.LayoutParams layoutParams) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                e.a((Object) layoutParams, "layoutInDisplayCutoutMode", (Object) new Integer(1));
            } catch (Exception e) {
                Log.i("GameBoosterReflectUtils", e.toString());
            }
        }
    }

    public static void a(String str, Context context) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("android.app.IActivityManager");
            obtain.writeString(str);
            int intValue = ((Integer) e.a(Class.forName("android.os.MiuiBinderTransaction$IActivityManager"), "TRANSACT_ID_SET_PACKAGE_HOLD_ON", Integer.TYPE)).intValue();
            c.a a2 = c.a.a("android.app.ActivityManager");
            a2.b("getService", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("asBinder", (Class<?>[]) null, new Object[0]);
            a2.e();
            a2.a("transact", new Class[]{Integer.TYPE, Parcel.class, Parcel.class, Integer.TYPE}, Integer.valueOf(intValue), obtain, obtain2, 0);
            C0393y.a(context, str, true);
            b.b("key_hang_up_pkg", str);
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
        } catch (Throwable th) {
            obtain2.recycle();
            obtain.recycle();
            throw th;
        }
        obtain2.recycle();
        obtain.recycle();
    }

    public static boolean a(Context context) {
        return C0393y.a(context, "com.xiaomi.gamecenter");
    }
}
