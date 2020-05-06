package com.miui.activityutil;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class r {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2316a = "StorageHelper";

    /* renamed from: b  reason: collision with root package name */
    private static final int f2317b = 0;

    /* renamed from: c  reason: collision with root package name */
    private static final int f2318c = 2;

    /* renamed from: d  reason: collision with root package name */
    private static List f2319d;

    static {
        ArrayList arrayList = new ArrayList();
        f2319d = arrayList;
        arrayList.add("mione");
        f2319d.add("mione_plus");
        f2319d.add("taurus");
        f2319d.add("taurus_td");
        f2319d.add("pisces");
        f2319d.add("HM2013022");
        f2319d.add("HM2013023");
    }

    private static long a(long j) {
        long pow = ((long) Math.pow(2.0d, Math.ceil(Math.log((double) (j / C.NANOS_PER_SECOND)) / Math.log(2.0d)))) * C.NANOS_PER_SECOND;
        return pow < j ? b(j) : pow;
    }

    public static t a(Context context) {
        long j;
        long j2;
        t tVar = new t();
        int i = Build.VERSION.SDK_INT;
        List e = i > 23 ? e(context) : i == 23 ? d(context) : c(context);
        int i2 = 0;
        if (!(!f2319d.contains(Build.DEVICE) || "mixed".equals(v.a("ro.boot.sdcard.type")))) {
            while (i2 < e.size()) {
                t a2 = a((s) e.get(i2));
                if (a2 != null) {
                    tVar.f2325b += a2.f2325b;
                    tVar.f2324a += a2.f2324a;
                }
                i2++;
            }
            t a3 = a(Environment.getDataDirectory().getPath());
            tVar.f2324a += a3.f2324a;
            tVar.f2325b += a3.f2325b;
            tVar.f2324a = a(tVar.f2324a);
        } else {
            while (i2 < e.size()) {
                s sVar = (s) e.get(i2);
                t a4 = a(sVar);
                if (a4 != null) {
                    if (sVar.c()) {
                        tVar.f2325b += a4.f2325b;
                        j2 = tVar.f2324a;
                        j = a(a4.f2324a);
                    } else {
                        tVar.f2325b += a4.f2325b;
                        j2 = tVar.f2324a;
                        j = a4.f2324a;
                    }
                    tVar.f2324a = j2 + j;
                }
                i2++;
            }
        }
        return tVar;
    }

    private static t a(s sVar) {
        if (sVar == null || sVar.b() == null) {
            return new t();
        }
        return !sVar.a() ? new t() : a(sVar.b());
    }

    private static t a(String str) {
        t tVar = new t();
        if (TextUtils.isEmpty(str)) {
            return new t();
        }
        try {
            StatFs statFs = new StatFs(str);
            long blockCount = (long) statFs.getBlockCount();
            long blockSize = (long) statFs.getBlockSize();
            tVar.f2324a = blockCount * blockSize;
            tVar.f2325b = ((long) statFs.getAvailableBlocks()) * blockSize;
            if (new StatFs(Environment.getDataDirectory().getPath()).getBlockCount() == statFs.getBlockCount()) {
                tVar.f2325b -= v.b("sys.memory.threshold.low");
            }
            return tVar;
        } catch (IllegalArgumentException unused) {
            return new t();
        }
    }

    private static boolean a() {
        return !f2319d.contains(Build.DEVICE) || "mixed".equals(v.a("ro.boot.sdcard.type"));
    }

    private static long b(long j) {
        long j2 = 1;
        long j3 = 1;
        while (true) {
            long j4 = j2 * j3;
            if (j4 >= j) {
                return j4;
            }
            j2 <<= 1;
            if (j2 > 512) {
                j3 *= 1000;
                j2 = 1;
            }
        }
    }

    private static List b(Context context) {
        int i = Build.VERSION.SDK_INT;
        return i > 23 ? e(context) : i == 23 ? d(context) : c(context);
    }

    private static List c(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        ArrayList arrayList = new ArrayList();
        try {
            Method method = StorageManager.class.getMethod("getVolumeList", new Class[0]);
            method.setAccessible(true);
            Object[] objArr = (Object[]) method.invoke(storageManager, new Object[0]);
            if (objArr != null) {
                for (Object obj : objArr) {
                    String str = (String) obj.getClass().getMethod("getPath", new Class[0]).invoke(obj, new Object[0]);
                    File file = new File(str);
                    if (file.exists() && file.isDirectory() && file.canWrite()) {
                        String str2 = (String) StorageManager.class.getMethod("getVolumeState", new Class[]{String.class}).invoke(storageManager, new Object[]{str});
                        String str3 = (String) obj.getClass().getMethod("getDescription", new Class[]{Context.class}).invoke(obj, new Object[]{context});
                        boolean booleanValue = ((Boolean) obj.getClass().getMethod("isPrimary", new Class[0]).invoke(obj, new Object[0])).booleanValue();
                        if (!booleanValue || str.equalsIgnoreCase(Environment.getExternalStorageDirectory().getPath())) {
                            s sVar = new s(str, str3, str2);
                            sVar.a(booleanValue);
                            arrayList.add(sVar);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(f2316a, "exception when listAvailableStorageCompat : ", e);
        }
        arrayList.trimToSize();
        return arrayList;
    }

    private static List d(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        ArrayList arrayList = new ArrayList();
        try {
            Method method = StorageManager.class.getMethod("getVolumes", new Class[0]);
            method.setAccessible(true);
            List list = (List) method.invoke(storageManager, new Object[0]);
            if (list != null) {
                for (Object next : list) {
                    int intValue = ((Integer) next.getClass().getMethod("getType", new Class[0]).invoke(next, new Object[0])).intValue();
                    File file = (File) next.getClass().getMethod("getPath", new Class[0]).invoke(next, new Object[0]);
                    if (file != null) {
                        File file2 = new File(file.getPath());
                        if ((intValue == 0 || intValue == 2) && file2.exists() && file2.isDirectory()) {
                            int intValue2 = ((Integer) next.getClass().getMethod("getState", new Class[0]).invoke(next, new Object[0])).intValue();
                            Class<?> cls = Class.forName("android.os.storage.VolumeInfo");
                            Method method2 = cls.getMethod("getEnvironmentForState", new Class[]{Integer.TYPE});
                            Object[] objArr = {Integer.valueOf(intValue2)};
                            s sVar = new s(file2.getPath(), (String) next.getClass().getMethod("getDescription", new Class[0]).invoke(next, new Object[0]), (String) method2.invoke(cls, objArr));
                            sVar.a(intValue == 2);
                            if (sVar.c() && sVar.b() != null && "mounted".equals(Environment.getExternalStorageState())) {
                                String path = Environment.getExternalStorageDirectory().getPath();
                                if (!sVar.b().equalsIgnoreCase(path)) {
                                    sVar.a(path);
                                }
                            }
                            arrayList.add(sVar);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(f2316a, "exception when listAvailableStorageCompat23 : ", e);
        }
        arrayList.trimToSize();
        return arrayList;
    }

    private static List e(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        ArrayList arrayList = new ArrayList();
        Class<List> cls = List.class;
        try {
            for (Object next : (List) q.a((Object) storageManager, "getStorageVolumes", (Class[]) null, new Object[0])) {
                Class<File> cls2 = File.class;
                File file = (File) q.a(next, "getPathFile", (Class[]) null, new Object[0]);
                if (file != null) {
                    File file2 = new File(file.getPath());
                    Class<String> cls3 = String.class;
                    Class<String> cls4 = String.class;
                    String str = (String) q.a(next, "getDescription", new Class[]{Context.class}, context);
                    s sVar = new s(file2.getPath(), str, (String) q.a(next, "getState", (Class[]) null, new Object[0]));
                    Class cls5 = Boolean.TYPE;
                    sVar.a(((Boolean) q.a(next, "isPrimary", (Class[]) null, new Object[0])).booleanValue());
                    arrayList.add(sVar);
                }
            }
        } catch (Exception e) {
            Log.e(f2316a, "exception when listAvailableStorageCompat24 : ", e);
        }
        arrayList.trimToSize();
        return arrayList;
    }
}
