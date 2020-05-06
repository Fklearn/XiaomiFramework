package com.miui.securityscan.i;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.d;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class q {

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public long f7740a;

        /* renamed from: b  reason: collision with root package name */
        public long f7741b;
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        private String f7742a;

        /* renamed from: b  reason: collision with root package name */
        private String f7743b;

        /* renamed from: c  reason: collision with root package name */
        private String f7744c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f7745d;
        private String e;
        private boolean f;

        public b(String str, String str2, String str3) {
            this.f7742a = str;
            this.f7744c = str2;
            this.f7743b = str3;
        }

        public String a() {
            String str = this.f7742a;
            return str == null ? "" : str;
        }

        public void a(String str) {
            this.f7742a = str;
        }

        public void a(boolean z) {
            this.f7745d = z;
        }

        public void b(String str) {
            this.e = str;
        }

        public void b(boolean z) {
            this.f = z;
        }

        public boolean b() {
            return "mounted".equals(this.f7743b);
        }

        public boolean c() {
            return this.f7745d;
        }
    }

    public static a a(Context context) {
        a aVar = new a();
        List<b> b2 = b(context);
        for (int i = 0; i < b2.size(); i++) {
            a a2 = a(b2.get(i));
            if (a2 != null) {
                aVar.f7741b += a2.f7741b;
                aVar.f7740a += a2.f7740a;
            }
        }
        return aVar;
    }

    public static a a(b bVar) {
        if (bVar == null || bVar.a() == null) {
            return new a();
        }
        return !bVar.b() ? new a() : a(bVar.a());
    }

    public static a a(String str) {
        a aVar = new a();
        if (TextUtils.isEmpty(str)) {
            return new a();
        }
        try {
            StatFs statFs = new StatFs(str);
            long blockCount = (long) statFs.getBlockCount();
            long blockSize = (long) statFs.getBlockSize();
            aVar.f7740a = blockCount * blockSize;
            aVar.f7741b = ((long) statFs.getAvailableBlocks()) * blockSize;
            if (new StatFs(Environment.getDataDirectory().getPath()).getBlockCount() == statFs.getBlockCount()) {
                try {
                    long longValue = ((Long) d.a("StorageUtils", Class.forName("android.os.SystemProperties"), Long.TYPE, "getLong", (Class<?>[]) new Class[]{String.class, Long.TYPE}, "sys.memory.threshold.low", 0)).longValue();
                    Log.d("StorageUtils", "memLowThreshold  " + longValue);
                    aVar.f7741b = aVar.f7741b - longValue;
                } catch (Exception e) {
                    Log.e("StorageUtils", "getStorageInfoForPath", e);
                }
            }
            return aVar;
        } catch (IllegalArgumentException unused) {
            return new a();
        }
    }

    public static List<b> b(Context context) {
        return Build.VERSION.SDK_INT >= 23 ? d(context) : c(context);
    }

    public static List<b> c(Context context) {
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
                            b bVar = new b(str, str3, str2);
                            bVar.a(booleanValue);
                            bVar.b((String) obj.getClass().getMethod("getUuid", new Class[0]).invoke(obj, new Object[0]));
                            bVar.b(true);
                            arrayList.add(bVar);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayList.trimToSize();
        return arrayList;
    }

    public static List<b> d(Context context) {
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
                            String str = (String) next.getClass().getMethod("getDescription", new Class[0]).invoke(next, new Object[0]);
                            String str2 = (String) next.getClass().getMethod("getFsUuid", new Class[0]).invoke(next, new Object[0]);
                            b bVar = new b(file2.getPath(), str, (String) method2.invoke(cls, objArr));
                            bVar.a(intValue == 2);
                            bVar.b(str2);
                            if (bVar.c()) {
                                bVar.b(true);
                            } else {
                                bVar.b(((Boolean) next.getClass().getMethod("isVisible", new Class[0]).invoke(next, new Object[0])).booleanValue());
                            }
                            next.getClass().getMethod("getDisk", new Class[0]).invoke(next, new Object[0]);
                            if (bVar.c() && bVar.a() != null && "mounted".equals(Environment.getExternalStorageState())) {
                                String path = Environment.getExternalStorageDirectory().getPath();
                                if (!bVar.a().equalsIgnoreCase(path)) {
                                    bVar.a(path);
                                }
                            }
                            arrayList.add(bVar);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayList.trimToSize();
        return arrayList;
    }
}
