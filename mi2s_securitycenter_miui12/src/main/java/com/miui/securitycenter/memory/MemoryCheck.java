package com.miui.securitycenter.memory;

import android.app.ActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import b.b.c.j.B;
import b.b.c.j.i;
import b.b.c.j.x;
import b.b.o.f.b.a;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.memory.IMemoryCheck;
import com.miui.securitycenter.utils.f;
import com.miui.securityscan.d.c;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import miui.security.SecurityManager;
import miui.securitycenter.utils.SecurityCenterHelper;
import miui.util.Log;

public class MemoryCheck extends IMemoryCheck.Stub {

    /* renamed from: a  reason: collision with root package name */
    private Context f7489a;

    /* renamed from: b  reason: collision with root package name */
    private PackageManager f7490b;

    /* renamed from: c  reason: collision with root package name */
    private ActivityManager f7491c;

    /* renamed from: d  reason: collision with root package name */
    private c f7492d;

    public MemoryCheck(Context context) {
        this.f7489a = context;
        this.f7492d = c.a(context);
        this.f7490b = context.getPackageManager();
        this.f7491c = (ActivityManager) context.getSystemService("activity");
    }

    private int a(Context context, String str) {
        c.a a2 = c.a.a("android.miui.AppOpsUtils");
        a2.b("getApplicationAutoStart", new Class[]{Context.class, String.class}, context, str);
        return a2.c();
    }

    private ResolveInfo a(ActivityManager.RecentTaskInfo recentTaskInfo) {
        Intent intent = new Intent(recentTaskInfo.baseIntent);
        ComponentName componentName = recentTaskInfo.origActivity;
        if (componentName != null) {
            intent.setComponent(componentName);
        }
        intent.setFlags((intent.getFlags() & -2097153) | 268435456);
        return this.f7490b.resolveActivity(intent, 0);
    }

    private SparseBooleanArray a(String str) {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        boolean z = true;
        sparseBooleanArray.put(1, new HashSet(a(this.f7489a, B.c())).contains(str));
        sparseBooleanArray.put(0, a(this.f7489a).contains(str));
        sparseBooleanArray.put(2, this.f7492d.b().contains(str));
        if (a(this.f7489a.getApplicationContext(), str) != 0) {
            z = false;
        }
        sparseBooleanArray.put(3, z);
        return sparseBooleanArray;
    }

    private static List<String> a() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("com.miui.cleanmaster");
        return arrayList;
    }

    private static List<String> a(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Constants.System.ANDROID_PACKAGE_NAME);
        arrayList.add("com.android.providers.media");
        arrayList.add("com.android.deskclock");
        arrayList.add("com.google.android.marvin.talkback");
        arrayList.add("com.miui.cleanmaster");
        ActivityInfo resolveActivityInfo = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").resolveActivityInfo(context.getPackageManager(), 0);
        if (resolveActivityInfo != null) {
            arrayList.add(resolveActivityInfo.packageName);
        }
        String string = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        if (!TextUtils.isEmpty(string) && string.contains("/")) {
            arrayList.add(string.substring(0, string.indexOf(47)));
        }
        WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        if (wallpaperInfo != null) {
            arrayList.add(wallpaperInfo.getPackageName());
        }
        return arrayList;
    }

    private static Set<String> a(Context context, int i) {
        List<String> a2 = f.a(i);
        return (a2 == null || a2.isEmpty()) ? new HashSet() : new HashSet(a2);
    }

    private static Set<String> b(Context context) {
        return a(context, 0);
    }

    private void b(String str) {
        if (b(this.f7489a, str)) {
            this.f7491c.killBackgroundProcesses(str);
        } else {
            x.a(this.f7491c, str);
        }
    }

    private boolean b(Context context, String str) {
        try {
            return x.i(context, str) || a(context, str) == 0;
        } catch (Exception e) {
            Log.e("MemoryCheck", "needKillBackgroundProcesses error", e);
            return false;
        }
    }

    public List<String> J() {
        return i(0);
    }

    public void a(IMemoryScanCallback iMemoryScanCallback) {
        String str;
        String str2;
        HashMap hashMap;
        HashMap hashMap2;
        String str3;
        IMemoryScanCallback iMemoryScanCallback2 = iMemoryScanCallback;
        Log.d("MemoryCheck", "impl MemoryCheck startScan");
        int i = 0;
        try {
            str = (String) e.a((Object) (SecurityManager) this.f7489a.getSystemService("security"), "getPackageNameByPid", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(Binder.getCallingPid()));
        } catch (Exception e) {
            Log.e("MemoryCheck", "getPackageNameByPid error", e);
            str = null;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        try {
            iMemoryScanCallback.b();
            List<String> a2 = a(this.f7489a);
            int c2 = B.c();
            List<ActivityManager.RecentTaskInfo> recentTasks = this.f7491c.getRecentTasks(1001, 2);
            if (recentTasks != null) {
                ResolveInfo a3 = a(recentTasks.get(0));
                if (!(a3 == null || a3.activityInfo == null || a3.activityInfo.packageName == null)) {
                    a2.add(a3.activityInfo.packageName);
                }
                Log.d("MemoryCheck", "impl MemoryCheck startScan recentTaskAppsMap add");
                hashMap = new HashMap();
                int i2 = 1;
                while (i2 < recentTasks.size()) {
                    try {
                        ResolveInfo a4 = a(recentTasks.get(i2));
                        if (a4 == null || a4.activityInfo == null || a4.activityInfo.packageName == null) {
                            str3 = str;
                            i2++;
                            str = str3;
                            i = 0;
                        } else {
                            String str4 = a4.activityInfo.packageName;
                            ApplicationInfo applicationInfo = this.f7490b.getApplicationInfo(str4, i);
                            if (a2.contains(str4) || (applicationInfo.flags & 1) != 0 || B.c(applicationInfo.uid) != c2 || b.b.c.j.e.a(this.f7489a, str4, 0)) {
                                str3 = str;
                            } else {
                                MemoryModel memoryModel = new MemoryModel();
                                memoryModel.setPackageName(str4);
                                memoryModel.setLockState(a(str4));
                                str3 = str;
                                try {
                                    memoryModel.setMemorySize(0);
                                    memoryModel.setAppName(x.j(this.f7489a, str4).toString());
                                    hashMap.put(str4, memoryModel);
                                } catch (PackageManager.NameNotFoundException e2) {
                                    e = e2;
                                    e.printStackTrace();
                                    i2++;
                                    str = str3;
                                    i = 0;
                                }
                            }
                            if (iMemoryScanCallback.e()) {
                                try {
                                    Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
                                    iMemoryScanCallback2.a(arrayList);
                                    return;
                                } catch (RemoteException e3) {
                                    Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException2", e3);
                                    return;
                                }
                            } else {
                                i2++;
                                str = str3;
                                i = 0;
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e4) {
                        e = e4;
                        str3 = str;
                        e.printStackTrace();
                        i2++;
                        str = str3;
                        i = 0;
                    }
                }
                str2 = str;
            } else {
                str2 = str;
                hashMap = null;
            }
            Log.d("MemoryCheck", "impl MemoryCheck startScan runningAppsMap add");
            HashMap hashMap3 = new HashMap();
            for (ActivityManager.RunningAppProcessInfo next : this.f7491c.getRunningAppProcesses()) {
                String str5 = next.pkgList != null ? next.pkgList[0] : null;
                try {
                    long[] processPss = SecurityCenterHelper.getProcessPss(new int[]{next.pid});
                    if (str5 != null) {
                        ApplicationInfo applicationInfo2 = this.f7490b.getApplicationInfo(str5, 0);
                        if (!a2.contains(str5)) {
                            if ((applicationInfo2.flags & 1) == 0) {
                                try {
                                    if (B.c(applicationInfo2.uid) == c2 && !b.b.c.j.e.a(this.f7489a, str5, 0)) {
                                        MemoryModel memoryModel2 = (MemoryModel) hashMap3.get(str5);
                                        long j = processPss[0] * 1024;
                                        if (memoryModel2 == null) {
                                            memoryModel2 = new MemoryModel();
                                            memoryModel2.setPackageName(str5);
                                            memoryModel2.setLockState(a(str5));
                                            hashMap2 = hashMap;
                                            try {
                                                memoryModel2.setMemorySize(0);
                                                memoryModel2.setAppName(x.j(this.f7489a, str5).toString());
                                                hashMap3.put(str5, memoryModel2);
                                            } catch (PackageManager.NameNotFoundException unused) {
                                                continue;
                                            }
                                        } else {
                                            hashMap2 = hashMap;
                                        }
                                        if (hashMap3.containsKey(str5)) {
                                            memoryModel2.setMemorySize(memoryModel2.getMemorySize() + j);
                                        }
                                        if (iMemoryScanCallback.e()) {
                                            try {
                                                Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
                                                iMemoryScanCallback2.a(arrayList);
                                                return;
                                            } catch (RemoteException e5) {
                                                Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException2", e5);
                                                return;
                                            }
                                        } else {
                                            hashMap = hashMap2;
                                        }
                                    }
                                } catch (PackageManager.NameNotFoundException unused2) {
                                }
                            }
                            hashMap2 = hashMap;
                            hashMap = hashMap2;
                        }
                    }
                } catch (PackageManager.NameNotFoundException unused3) {
                }
                hashMap2 = hashMap;
                hashMap = hashMap2;
            }
            HashMap hashMap4 = hashMap;
            Log.d("MemoryCheck", "impl MemoryCheck startScan modelPkgList.add");
            for (String str6 : hashMap3.keySet()) {
                if (iMemoryScanCallback2.b(x.j(this.f7489a, str6).toString())) {
                    try {
                        Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
                        iMemoryScanCallback2.a(arrayList);
                        return;
                    } catch (RemoteException e6) {
                        Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException2", e6);
                        return;
                    }
                } else {
                    arrayList2.add(str6);
                    arrayList.add(hashMap3.get(str6));
                }
            }
            Log.d("MemoryCheck", "impl MemoryCheck startScan modelList.add");
            if (!TextUtils.isEmpty(str2) && !a().contains(str2) && hashMap4 != null) {
                for (String str7 : hashMap4.keySet()) {
                    if (iMemoryScanCallback2.b(x.j(this.f7489a, str7).toString())) {
                        try {
                            Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
                            iMemoryScanCallback2.a(arrayList);
                            return;
                        } catch (RemoteException e7) {
                            Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException2", e7);
                            return;
                        }
                    } else if (!arrayList2.contains(str7)) {
                        arrayList.add(hashMap4.get(str7));
                    }
                }
            }
            try {
                Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
                iMemoryScanCallback2.a(arrayList);
            } catch (RemoteException e8) {
                Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException2", e8);
            }
        } catch (RemoteException e9) {
            Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException1", e9);
            Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
            iMemoryScanCallback2.a(arrayList);
        } catch (Throwable th) {
            Throwable th2 = th;
            try {
                Log.d("MemoryCheck", "impl MemoryCheck startScan callback.onFinishScan");
                iMemoryScanCallback2.a(arrayList);
            } catch (RemoteException e10) {
                Log.e("MemoryCheck", "impl MemoryCheck startScan RemoteException2", e10);
            }
            throw th2;
        }
    }

    public void a(String str, int i, int i2) {
        if (i == 1 || i == 0) {
            boolean z = false;
            if (i == 1) {
                z = true;
            }
            b.b.c.j.e.a(str, i2, z);
        }
    }

    public void a(List<String> list, IMemoryCleanupCallback iMemoryCleanupCallback) {
        try {
            Log.d("MemoryCheck", "cleanupAppsMemory callingPid: " + Binder.getCallingPid() + "   ;   callingUid: " + Binder.getCallingUid());
            iMemoryCleanupCallback.f();
            HashMap hashMap = new HashMap();
            List<ActivityManager.RecentTaskInfo> recentTasks = this.f7491c.getRecentTasks(1001, 6);
            if (recentTasks != null) {
                for (int i = 0; i < recentTasks.size(); i++) {
                    ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(i);
                    ResolveInfo a2 = a(recentTaskInfo);
                    if (!(a2 == null || a2.activityInfo == null || a2.activityInfo.packageName == null || !list.contains(a2.activityInfo.packageName))) {
                        hashMap.put(Integer.valueOf(recentTaskInfo.persistentId), a2.activityInfo.packageName);
                    }
                }
            }
            for (Map.Entry key : hashMap.entrySet()) {
                a.a(this.f7489a).a(((Integer) key.getKey()).intValue());
            }
            if (Build.IS_INTERNATIONAL_BUILD) {
                ArrayList arrayList = new ArrayList();
                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = this.f7491c.getRunningAppProcesses();
                if (runningAppProcesses != null) {
                    for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
                        String str = next.pkgList != null ? next.pkgList[0] : null;
                        if (str != null && list.contains(str)) {
                            if (b(this.f7489a, str)) {
                                this.f7491c.killBackgroundProcesses(str);
                            } else {
                                arrayList.add(Integer.valueOf(next.pid));
                            }
                        }
                    }
                }
                int[] iArr = new int[arrayList.size()];
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    iArr[i2] = ((Integer) arrayList.get(i2)).intValue();
                }
                x.a(iArr, "MemoryCheck");
                for (String j : list) {
                    iMemoryCleanupCallback.a(x.j(this.f7489a, j).toString());
                }
            } else {
                for (String next2 : list) {
                    b(next2);
                    iMemoryCleanupCallback.a(x.j(this.f7489a, next2).toString());
                }
            }
            try {
                iMemoryCleanupCallback.d();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            iMemoryCleanupCallback.d();
        } catch (Throwable th) {
            try {
                iMemoryCleanupCallback.d();
            } catch (RemoteException e3) {
                e3.printStackTrace();
            }
            throw th;
        }
    }

    public int b(String str, int i) {
        if (str == null) {
            return -1;
        }
        return b.b.c.j.e.a(str, i) ? 1 : 0;
    }

    public void c(String str, int i) {
        a(str, i, 0);
    }

    public int g(String str) {
        return b(str, 0);
    }

    public List<String> i(int i) {
        ArrayList arrayList = i == 0 ? new ArrayList(b(this.f7489a)) : new ArrayList(a(this.f7489a, i));
        if (!i.i(this.f7489a)) {
            arrayList.addAll(this.f7492d.b());
        }
        return arrayList;
    }

    public Map<Integer, List<String>> t() {
        HashMap hashMap = new HashMap();
        Set<String> b2 = b(this.f7489a);
        ArrayList arrayList = new ArrayList();
        for (String add : b2) {
            arrayList.add(add);
        }
        hashMap.put(1, arrayList);
        hashMap.put(0, a(this.f7489a));
        hashMap.put(2, this.f7492d.b());
        return hashMap;
    }
}
