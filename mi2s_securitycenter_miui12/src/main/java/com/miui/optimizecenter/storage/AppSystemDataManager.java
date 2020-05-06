package com.miui.optimizecenter.storage;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StatFs;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import b.b.o.e.a;
import b.b.o.g.e;
import com.miui.antivirus.model.DangerousInfo;
import com.miui.appmanager.AppManageUtils;
import com.miui.luckymoney.config.AppConstants;
import com.miui.optimizecenter.storage.model.b;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import miui.os.Build;

public class AppSystemDataManager {

    /* renamed from: a  reason: collision with root package name */
    private static AppSystemDataManager f5685a;

    /* renamed from: b  reason: collision with root package name */
    public static final ArrayList<String> f5686b = new ArrayList<>();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f5687c;

    /* renamed from: d  reason: collision with root package name */
    final IPackageStatsObserver.Stub f5688d = new i(this);

    public static class AllDataObserver extends IPackageDataObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        Handler f5689a;

        public AllDataObserver(Handler handler) {
            this.f5689a = handler;
        }

        public void onRemoveCompleted(String str, boolean z) {
            this.f5689a.obtainMessage(DangerousInfo.INVALID_VERSION_CODE, Boolean.valueOf(z)).sendToTarget();
        }
    }

    public static class CacheDataObserver extends AppManageUtils.ClearCacheObserver {

        /* renamed from: b  reason: collision with root package name */
        Handler f5690b;

        public CacheDataObserver(Handler handler) {
            super(handler);
            this.f5690b = handler;
        }

        public void onRemoveCompleted(String str, boolean z) {
            this.f5690b.obtainMessage(-1002, Boolean.valueOf(z)).sendToTarget();
            Log.i("AppSystemDataManager", "onRemoveCompleted: " + str);
        }
    }

    public static class UninstallPkgObserver extends IPackageDeleteObserver.Stub {

        /* renamed from: a  reason: collision with root package name */
        Handler f5691a;

        public UninstallPkgObserver(Handler handler) {
            this.f5691a = handler;
        }

        public void packageDeleted(String str, int i) {
            if (i == 1) {
                this.f5691a.obtainMessage(-1003).sendToTarget();
            }
        }
    }

    static {
        f5686b.add("com.facebook.appmanager");
        f5686b.add("com.facebook.services");
        f5686b.add("com.facebook.system");
        if (Build.IS_INTERNATIONAL_BUILD && "fr_sfr".equals(a.a("ro.miui.customized.region", ""))) {
            f5686b.add("com.altice.android.myapps");
            f5686b.add("com.sfr.android.sfrjeux");
        }
    }

    private AppSystemDataManager(Context context) {
        this.f5687c = context;
    }

    private long a(int i, PackageInfo packageInfo, ArraySet<String> arraySet) {
        Class<UserHandle> cls = UserHandle.class;
        try {
            Class[] clsArr = {Integer.TYPE};
            Object[] objArr = {Integer.valueOf(i)};
            StorageStatsManager storageStatsManager = (StorageStatsManager) this.f5687c.getSystemService(StorageStatsManager.class);
            UUID uuid = packageInfo.applicationInfo.storageUuid;
            StorageStats queryStatsForPackage = storageStatsManager.queryStatsForPackage(uuid, packageInfo.packageName, (UserHandle) e.a((Class<?>) cls, "of", (Class<?>[]) clsArr, objArr));
            long dataBytes = queryStatsForPackage.getDataBytes();
            long longValue = ((Long) e.a((Object) storageStatsManager, "getCacheQuotaBytes", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, uuid.toString(), Integer.valueOf(packageInfo.applicationInfo.uid))).longValue();
            long cacheBytes = queryStatsForPackage.getCacheBytes();
            if (longValue < cacheBytes) {
                dataBytes = (dataBytes - cacheBytes) + longValue;
            }
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (!arraySet.contains(applicationInfo.packageName)) {
                if (applicationInfo.sourceDir.startsWith("/data")) {
                    dataBytes += queryStatsForPackage.getAppBytes();
                }
                arraySet.add(applicationInfo.packageName);
            }
            return 0 + dataBytes;
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "getAppSystemSize26: failed", e);
            return 0;
        }
    }

    public static long a(long j) {
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

    public static AppSystemDataManager a(Context context) {
        if (f5685a == null) {
            f5685a = new AppSystemDataManager(context.getApplicationContext());
        }
        return f5685a;
    }

    private void a(Activity activity, Intent intent, int i, UserHandle userHandle) {
        try {
            e.b(activity, "startActivityForResultAsUser", new Class[]{Intent.class, Integer.TYPE, UserHandle.class}, intent, Integer.valueOf(i), userHandle);
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "startActivityForResultAsUser: start ManagerSpace activity failed", e);
        }
    }

    private void a(ApplicationInfo applicationInfo, b bVar) {
        try {
            Object systemService = this.f5687c.getSystemService("storagestats");
            Object a2 = e.a((Class<?>) UserHandle.class, "getUserHandleForUid", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(applicationInfo.uid));
            Object a3 = e.a(systemService, "queryStatsForPackage", (Class<?>[]) new Class[]{String.class, String.class, UserHandle.class}, (String) e.a((Object) applicationInfo, "volumeUuid"), applicationInfo.packageName, a2);
            long longValue = ((Long) e.a(a3, "getDataBytes", (Class<?>[]) null, new Object[0])).longValue();
            long longValue2 = ((Long) e.a(a3, "getCacheBytes", (Class<?>[]) null, new Object[0])).longValue();
            long longValue3 = ((Long) e.a(a3, "getCodeBytes", (Class<?>[]) null, new Object[0])).longValue();
            long j = longValue + longValue3;
            bVar.l = longValue;
            bVar.m = longValue3;
            bVar.n = j;
            bVar.p = longValue2;
            bVar.k = j;
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "setPackageSystemInfo26: failed", e);
        }
    }

    private void a(b bVar) {
        a(bVar.f5762d, bVar.f5760b, (IPackageStatsObserver) bVar.r);
    }

    private long c(int i) {
        Object a2;
        long j = 0;
        try {
            PackageManager packageManager = this.f5687c.getPackageManager();
            new ArrayList();
            if (Build.VERSION.SDK_INT > 23) {
                a2 = e.a((Object) packageManager, "getInstalledPackagesAsUser", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, 0, Integer.valueOf(i));
            } else {
                a2 = e.a((Object) packageManager, "getInstalledPackages", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, 0, Integer.valueOf(i));
            }
            List<PackageInfo> list = (List) a2;
            Log.i("AppSystemDataManager", "getAppStorageForUser: all app size = " + list.size() + "\t uid = " + i);
            ArraySet arraySet = new ArraySet();
            if (list != null) {
                for (PackageInfo packageInfo : list) {
                    if (Build.VERSION.SDK_INT > 25) {
                        j += a(i, packageInfo, (ArraySet<String>) arraySet);
                    } else {
                        a(packageInfo.packageName, packageInfo.applicationInfo.uid, (IPackageStatsObserver) this.f5688d);
                    }
                }
            }
        } catch (Exception e) {
            Log.w("AppSystemDataManager", "App unexpectedly not found", e);
        }
        return j;
    }

    private Object f() {
        try {
            IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package");
            return e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "ReflectUtil", e);
            return null;
        }
    }

    private List<PackageInfo> g() {
        Object a2;
        ArrayList arrayList = new ArrayList();
        try {
            PackageManager packageManager = this.f5687c.getPackageManager();
            for (Object a3 : (List) e.a(this.f5687c.getSystemService("user"), "getUsers", (Class<?>[]) null, new Object[0])) {
                int intValue = ((Integer) e.a(a3, "id", Integer.TYPE)).intValue();
                new ArrayList();
                if (Build.VERSION.SDK_INT > 23) {
                    a2 = e.a((Object) packageManager, "getInstalledPackagesAsUser", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, 0, Integer.valueOf(intValue));
                } else {
                    a2 = e.a((Object) packageManager, "getInstalledPackages", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, 0, Integer.valueOf(intValue));
                }
                for (PackageInfo packageInfo : (List) a2) {
                    if ((packageInfo.applicationInfo.flags & 1) == 0) {
                        arrayList.add(packageInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public int a(int i) {
        try {
            return ((Integer) e.a((Class<?>) UserHandle.class, "getUserId", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public ApplicationInfo a(String str, int i, int i2) {
        try {
            Object f = f();
            if (f == null) {
                return null;
            }
            return (ApplicationInfo) e.a(f, "getApplicationInfo", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Integer.TYPE}, str, Integer.valueOf(i), Integer.valueOf(i2));
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "getApplicationInfoAsUser: failed", e);
            return null;
        }
    }

    public b a(Context context, ApplicationInfo applicationInfo, int i) {
        try {
            Object a2 = e.a(context.getSystemService("storagestats"), "queryStatsForPackage", (Class<?>[]) new Class[]{String.class, String.class, UserHandle.class}, (String) e.a((Object) applicationInfo, "volumeUuid"), applicationInfo.packageName, UserHandle.getUserHandleForUid(i));
            long longValue = ((Long) e.a(a2, "getDataBytes", (Class<?>[]) null, new Object[0])).longValue();
            long longValue2 = ((Long) e.a(a2, "getCacheBytes", (Class<?>[]) null, new Object[0])).longValue();
            long longValue3 = ((Long) e.a(a2, "getCodeBytes", (Class<?>[]) null, new Object[0])).longValue();
            b bVar = new b();
            bVar.l = longValue;
            bVar.p = longValue2;
            bVar.m = longValue3;
            return bVar;
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "reflect error while query stats for pacakge", e);
            return new b();
        }
    }

    public List<b> a() {
        ArrayList arrayList = new ArrayList();
        PackageManager packageManager = this.f5687c.getPackageManager();
        for (PackageInfo next : g()) {
            b bVar = new b();
            ApplicationInfo applicationInfo = next.applicationInfo;
            bVar.h = applicationInfo;
            bVar.f5760b = applicationInfo.uid;
            bVar.g = next.versionCode;
            bVar.f5762d = next.packageName;
            bVar.e = next.versionName;
            bVar.i = a(this.f5687c).b(bVar.f5760b);
            bVar.f = "pkg_icon://".concat(next.packageName);
            if (bVar.i) {
                bVar.f = "pkg_icon_xspace://".concat(next.packageName);
            }
            if (AppConstants.Package.PACKAGE_NAME_MM.equals(next.packageName) || AppConstants.Package.PACKAGE_NAME_QQ.equals(next.packageName)) {
                bVar.a(2);
            }
            if (next.applicationInfo.manageSpaceActivityName != null) {
                bVar.a(1);
            }
            a(next.packageName, next.applicationInfo, bVar);
            bVar.f5761c = String.valueOf(packageManager.getApplicationLabel(next.applicationInfo));
            arrayList.add(bVar);
        }
        Collections.sort(arrayList, new b.a());
        return arrayList;
    }

    public void a(Context context, String str, String str2, int i, int i2) {
        if (!ActivityManager.isUserAMonkey()) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setClassName(str, str2);
                Activity activity = (Activity) context;
                a(activity, intent, i2, (UserHandle) e.a((Class<?>) UserHandle.class, "of", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i)));
            } catch (Exception e) {
                Log.e("AppSystemDataManager", "startActivityForResultAsUser error", e);
            }
        }
    }

    public void a(String str, int i, IPackageDeleteObserver iPackageDeleteObserver, int i2, int i3) {
        try {
            Object f = f();
            if (Build.VERSION.SDK_INT > 25) {
                e.a(f, "deletePackageAsUser", (Class<?>[]) new Class[]{String.class, Integer.TYPE, IPackageDeleteObserver.class, Integer.TYPE, Integer.TYPE}, str, Integer.valueOf(i), iPackageDeleteObserver, Integer.valueOf(i2), Integer.valueOf(i3));
                return;
            }
            e.a(f, "deletePackageAsUser", (Class<?>[]) new Class[]{String.class, IPackageDeleteObserver.class, Integer.TYPE, Integer.TYPE}, str, iPackageDeleteObserver, Integer.valueOf(i2), Integer.valueOf(i3));
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "deletePackage error", e);
        }
    }

    public void a(String str, int i, IPackageStatsObserver iPackageStatsObserver) {
        String str2;
        Class[] clsArr;
        Object[] objArr;
        try {
            PackageManager packageManager = this.f5687c.getPackageManager();
            if (Build.VERSION.SDK_INT > 23) {
                str2 = "getPackageSizeInfoAsUser";
                clsArr = new Class[]{String.class, Integer.TYPE, IPackageStatsObserver.class};
                objArr = new Object[]{str, Integer.valueOf(i), iPackageStatsObserver};
            } else {
                str2 = "getPackageSizeInfo";
                clsArr = new Class[]{String.class, Integer.TYPE, IPackageStatsObserver.class};
                objArr = new Object[]{str, Integer.valueOf(i), iPackageStatsObserver};
            }
            e.a((Object) packageManager, str2, (Class<?>[]) clsArr, objArr);
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "reflect error while get package size info", e);
        }
    }

    public void a(String str, ApplicationInfo applicationInfo, b bVar) {
        if (!TextUtils.isEmpty(str)) {
            try {
                if (Build.VERSION.SDK_INT > 25) {
                    a(applicationInfo, bVar);
                } else {
                    a(bVar);
                }
            } catch (Exception e) {
                Log.e("AppSystemDataManager", "reflect error while query stats for pacakge", e);
            }
        }
    }

    public boolean a(String str) {
        Object f = f();
        if (f == null) {
            return false;
        }
        return b.b.o.b.a.a.a(f, str);
    }

    public boolean a(String str, int i, AllDataObserver allDataObserver) {
        try {
            Object a2 = e.a(Class.forName("android.app.ActivityManagerNative"), "getDefault", (Class<?>[]) null, new Object[0]);
            if (Build.VERSION.SDK_INT > 27) {
                return ((Boolean) e.a(a2, "clearApplicationUserData", (Class<?>[]) new Class[]{String.class, Boolean.TYPE, IPackageDataObserver.class, Integer.TYPE}, str, false, allDataObserver, Integer.valueOf(i))).booleanValue();
            }
            return ((Boolean) e.a(a2, "clearApplicationUserData", (Class<?>[]) new Class[]{String.class, IPackageDataObserver.class, Integer.TYPE}, str, allDataObserver, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            Log.e("AppSystemDataManager", "Couldnt clear application user data for package:" + str, e);
            return false;
        }
    }

    public long b() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    public boolean b(int i) {
        try {
            Class<?> cls = Class.forName("miui.securityspace.XSpaceUserHandle");
            int intValue = ((Integer) e.a((Class<?>) UserHandle.class, "getUserId", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).intValue();
            return ((Boolean) e.a(cls, "isXSpaceUserId", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(intValue))).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public long c() {
        long j = 0;
        try {
            for (Object a2 : (List) e.a(this.f5687c.getSystemService("user"), "getUsers", (Class<?>[]) null, new Object[0])) {
                j += c(((Integer) e.a(a2, "id", Integer.TYPE)).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return j;
    }

    public long d() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return statFs.getBlockCountLong() * statFs.getBlockSizeLong();
    }

    public long e() {
        try {
            return Build.VERSION.SDK_INT > 24 ? ((Long) e.a((Object) (StorageManager) this.f5687c.getSystemService("storage"), "getPrimaryStorageSize", (Class<?>[]) null, new Object[0])).longValue() : a(Environment.getDataDirectory().getTotalSpace() + Environment.getRootDirectory().getTotalSpace());
        } catch (Exception unused) {
            return 0;
        }
    }
}
