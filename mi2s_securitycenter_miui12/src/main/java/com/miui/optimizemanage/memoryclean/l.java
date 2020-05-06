package com.miui.optimizemanage.memoryclean;

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
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.j.B;
import b.b.c.j.e;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.optimizemanage.b.c;
import com.miui.optimizemanage.memoryclean.j;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class l {

    /* renamed from: a  reason: collision with root package name */
    private String[] f5980a;

    /* renamed from: b  reason: collision with root package name */
    private c f5981b = new c(false);

    public l(Context context) {
        this.f5980a = context.getResources().getStringArray(R.array.clean_cached_process_white_list);
        this.f5981b.b();
    }

    private ResolveInfo a(PackageManager packageManager, ActivityManager.RecentTaskInfo recentTaskInfo) {
        Intent intent = new Intent(recentTaskInfo.baseIntent);
        ComponentName componentName = recentTaskInfo.origActivity;
        if (componentName != null) {
            intent.setComponent(componentName);
        }
        intent.setFlags((intent.getFlags() & -2097153) | 268435456);
        return packageManager.resolveActivity(intent, 0);
    }

    private boolean a(int i) {
        int c2 = B.c(i);
        int c3 = B.c();
        return c2 == c3 || (c3 == 0 && c2 == 999);
    }

    private boolean a(String str) {
        for (String equals : this.f5980a) {
            if (equals.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private boolean a(String str, int i) {
        return e.a(str, B.c(i));
    }

    private boolean a(ArrayList<String> arrayList, String str) {
        return arrayList.contains(str);
    }

    public ArrayList<String> a(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("com.miui.securitycenter");
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

    public List<j> a(Context context, List<a> list) {
        int i;
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ArrayList<String> a2 = a(context);
        HashMap hashMap = new HashMap();
        List<ActivityManager.RecentTaskInfo> recentTasks = activityManager.getRecentTasks(1001, 6);
        if (recentTasks != null) {
            for (int i2 = 1; i2 < recentTasks.size(); i2++) {
                try {
                    ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(i2);
                    ResolveInfo a3 = a(packageManager, recentTaskInfo);
                    if (!(a3 == null || a3.activityInfo == null || a3.activityInfo.packageName == null)) {
                        String str = a3.activityInfo.packageName;
                        if (!a(a2, str)) {
                            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
                            j jVar = new j();
                            jVar.f5972a = str;
                            try {
                                i = ((Integer) b.b.o.g.e.b(recentTaskInfo, UserConfigure.Columns.USER_ID)).intValue();
                            } catch (IllegalAccessException | NoSuchFieldException e) {
                                Log.e("RunningProcessLoader", UserConfigure.Columns.USER_ID, e);
                                i = 0;
                            }
                            jVar.f5973b = B.a(i, applicationInfo.uid);
                            jVar.e = a(str, jVar.f5973b);
                            if (hashMap.containsKey(jVar.a())) {
                                ((j) hashMap.get(jVar.a())).i.add(Integer.valueOf(recentTaskInfo.persistentId));
                            } else {
                                jVar.i.add(Integer.valueOf(recentTaskInfo.persistentId));
                                hashMap.put(jVar.a(), jVar);
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        }
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(200);
        SparseArray sparseArray = new SparseArray();
        for (ActivityManager.RunningServiceInfo next : runningServices) {
            List list2 = (List) sparseArray.get(next.pid);
            if (list2 == null) {
                sparseArray.append(next.pid, new ArrayList());
                list2 = (List) sparseArray.get(next.pid);
            }
            j.a aVar = new j.a();
            aVar.f5977b = next.service;
            aVar.f5976a = next.pid;
            aVar.f5978c = next.activeSince;
            list2.add(aVar);
        }
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        HashMap hashMap2 = new HashMap();
        for (ActivityManager.RunningAppProcessInfo next2 : runningAppProcesses) {
            String[] strArr = next2.pkgList;
            String str2 = strArr != null ? strArr[0] : null;
            if (str2 != null && !a(a2, str2)) {
                String a4 = j.a(str2, next2.uid);
                j jVar2 = (j) hashMap.get(a4);
                if (jVar2 == null) {
                    jVar2 = new j();
                    jVar2.f5972a = str2;
                    jVar2.e = a(str2, next2.uid);
                    jVar2.f5973b = next2.uid;
                }
                List list3 = (List) hashMap2.get(a4);
                if (list3 == null) {
                    list3 = new ArrayList();
                }
                list3.add(Integer.valueOf(next2.pid));
                hashMap2.put(a4, list3);
                try {
                    jVar2.f = ((((Integer) b.b.o.g.e.a((Object) next2, "flags")).intValue() & 4) != 0) | jVar2.f;
                } catch (NoSuchFieldException e2) {
                    e2.printStackTrace();
                } catch (IllegalAccessException e3) {
                    e3.printStackTrace();
                }
                int i3 = 11;
                if (Build.VERSION.SDK_INT >= 23) {
                    i3 = 14;
                }
                try {
                    jVar2.g = (((Integer) b.b.o.g.e.a((Object) next2, "processState")).intValue() >= i3) | jVar2.g;
                } catch (IllegalAccessException | NoSuchFieldException unused2) {
                }
                if (next2.importance == 100) {
                    jVar2.h = true;
                }
                hashMap.put(a4, jVar2);
            }
        }
        SparseArray sparseArray2 = new SparseArray();
        this.f5981b.c();
        int a5 = this.f5981b.a();
        for (int i4 = 0; i4 < a5; i4++) {
            c.a a6 = this.f5981b.a(i4);
            sparseArray2.put(a6.f5879c, Long.valueOf(a6.l));
        }
        ArrayList arrayList = new ArrayList();
        for (String str3 : hashMap.keySet()) {
            j jVar3 = (j) hashMap.get(str3);
            if ((!jVar3.g || !a(jVar3.f5972a)) && a(jVar3.f5973b)) {
                List list4 = (List) hashMap2.get(str3);
                if (list4 != null && !list4.isEmpty()) {
                    jVar3.f5974c = new int[list4.size()];
                    jVar3.k = new long[list4.size()];
                    for (int i5 = 0; i5 < list4.size(); i5++) {
                        jVar3.f5974c[i5] = ((Integer) list4.get(i5)).intValue();
                        List list5 = (List) sparseArray.get(jVar3.f5974c[i5]);
                        if (list5 != null) {
                            if (jVar3.l == null) {
                                jVar3.l = new ArrayList();
                            }
                            jVar3.l.addAll(list5);
                        }
                        Long l = (Long) sparseArray2.get(jVar3.f5974c[i5]);
                        if (l != null) {
                            jVar3.k[i5] = l.longValue();
                        } else {
                            jVar3.k[i5] = 0;
                        }
                    }
                }
                arrayList.add(jVar3);
            }
        }
        return arrayList;
    }
}
