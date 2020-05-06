package com.miui.optimizecenter.storage.d;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;
import b.b.o.g.e;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static c f5733a;

    /* renamed from: b  reason: collision with root package name */
    private StorageManager f5734b;

    private c(Context context) {
        this.f5734b = (StorageManager) context.getSystemService(StorageManager.class);
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (f5733a == null) {
                f5733a = new c(context);
            }
            cVar = f5733a;
        }
        return cVar;
    }

    public a a(String str) {
        try {
            Object a2 = e.a((Object) this.f5734b, Object.class, "findDiskById", (Class<?>[]) new Class[]{String.class}, str);
            if (a2 != null) {
                return new a(a2);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<d> a() {
        try {
            Method method = StorageManager.class.getMethod("getVolumes", new Class[0]);
            method.setAccessible(true);
            List list = (List) method.invoke(this.f5734b, new Object[0]);
            if (list == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            for (Object next : list) {
                if (((Integer) next.getClass().getMethod("getType", new Class[0]).invoke(next, new Object[0])).intValue() == 0) {
                    arrayList.add(new d(next));
                }
            }
            return arrayList;
        } catch (Exception e) {
            Log.i("StorageManagerCompat", "getVolumes: " + e.toString());
            return null;
        }
    }

    public d b(String str) {
        try {
            Object a2 = e.a((Object) this.f5734b, Object.class, "findVolumeById", (Class<?>[]) new Class[]{String.class}, str);
            if (a2 != null) {
                return new d(a2);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void c(String str) {
        try {
            e.a((Object) this.f5734b, "mount", (Class<?>[]) new Class[]{String.class}, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void d(String str) {
        try {
            e.a((Object) this.f5734b, "unmount", (Class<?>[]) new Class[]{String.class}, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
