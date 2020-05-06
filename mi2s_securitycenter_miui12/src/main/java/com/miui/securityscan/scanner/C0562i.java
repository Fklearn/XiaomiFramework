package com.miui.securityscan.scanner;

import android.app.ActivityManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.securitycenter.memory.b;
import com.miui.securitycenter.memory.d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miui.securitycenter.utils.SecurityCenterHelper;

/* renamed from: com.miui.securityscan.scanner.i  reason: case insensitive filesystem */
class C0562i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b f7898a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0564k f7899b;

    C0562i(C0564k kVar, b bVar) {
        this.f7899b = kVar;
        this.f7898a = bVar;
    }

    public void run() {
        b bVar;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        try {
            if (this.f7898a != null) {
                this.f7898a.b();
            }
            List a2 = C0564k.c(this.f7899b.f7904b);
            int c2 = B.c();
            HashMap hashMap = new HashMap();
            List<ActivityManager.RecentTaskInfo> recentTasks = this.f7899b.f7906d.getRecentTasks(1001, 6);
            int i = 999;
            int i2 = 0;
            if (recentTasks != null) {
                ResolveInfo a3 = this.f7899b.a(recentTasks.get(0));
                if (!(a3 == null || a3.activityInfo == null || a3.activityInfo.packageName == null)) {
                    a2.add(a3.activityInfo.packageName);
                }
                int i3 = 1;
                while (i3 < recentTasks.size()) {
                    ActivityManager.RecentTaskInfo recentTaskInfo = recentTasks.get(i3);
                    Integer num = (Integer) e.b(recentTaskInfo, UserConfigure.Columns.USER_ID);
                    ResolveInfo a4 = this.f7899b.a(recentTaskInfo);
                    if (!(a4 == null || a4.activityInfo == null || a4.activityInfo.packageName == null)) {
                        String str = a4.activityInfo.packageName;
                        if (!a2.contains(str) && !x.i(this.f7899b.f7904b, str) && ((num.intValue() == c2 || (num.intValue() == i && c2 == 0)) && !b.b.c.j.e.a(this.f7899b.f7904b, str, i2))) {
                            String str2 = str + "_" + num;
                            d dVar = new d();
                            dVar.b(str);
                            dVar.a(this.f7899b.a(str, num.intValue()));
                            dVar.a(0);
                            dVar.a(x.j(this.f7899b.f7904b, str).toString());
                            dVar.a(num.intValue());
                            dVar.c(str2);
                            hashMap.put(str2, dVar);
                        }
                        if (this.f7898a != null && this.f7898a.e()) {
                            b bVar2 = this.f7898a;
                            if (bVar2 != null) {
                                bVar2.a(arrayList);
                                return;
                            }
                            return;
                        }
                    }
                    i3++;
                    i = 999;
                    i2 = 0;
                }
            }
            HashMap hashMap2 = new HashMap();
            for (ActivityManager.RunningAppProcessInfo next : this.f7899b.f7906d.getRunningAppProcesses()) {
                String str3 = next.pkgList != null ? next.pkgList[0] : null;
                int[] iArr = {next.pid};
                int i4 = next.uid;
                long[] processPss = SecurityCenterHelper.getProcessPss(iArr);
                if (str3 != null) {
                    int c3 = B.c(i4);
                    if (!a2.contains(str3) && !x.i(this.f7899b.f7904b, str3)) {
                        if (c3 != c2) {
                            if (c3 == 999 && c2 == 0) {
                            }
                        }
                        if (!b.b.c.j.e.a(this.f7899b.f7904b, str3, 0)) {
                            String str4 = str3 + "_" + c3;
                            d dVar2 = (d) hashMap2.get(str4);
                            long j = processPss[0] * 1024;
                            if (dVar2 == null) {
                                dVar2 = new d();
                                dVar2.b(str3);
                                dVar2.a(this.f7899b.a(str3, c3));
                                dVar2.a(0);
                                dVar2.a(x.j(this.f7899b.f7904b, str3).toString());
                                dVar2.a(c3);
                                dVar2.c(str4);
                                hashMap2.put(str4, dVar2);
                            }
                            if (hashMap2.containsKey(str4)) {
                                dVar2.a(dVar2.b() + j);
                            }
                            if (this.f7898a != null && this.f7898a.e()) {
                                b bVar3 = this.f7898a;
                                if (bVar3 != null) {
                                    bVar3.a(arrayList);
                                    return;
                                }
                                return;
                            }
                        }
                    }
                }
            }
            for (String str5 : hashMap2.keySet()) {
                arrayList2.add(str5);
                arrayList.add(hashMap2.get(str5));
            }
            for (String str6 : hashMap.keySet()) {
                if (!arrayList2.contains(str6)) {
                    arrayList.add(hashMap.get(str6));
                }
            }
            bVar = this.f7898a;
            if (bVar == null) {
                return;
            }
        } catch (Exception e) {
            Log.e("MemoryCheckManager", "startScan:", e);
            bVar = this.f7898a;
            if (bVar == null) {
                return;
            }
        } catch (Throwable th) {
            b bVar4 = this.f7898a;
            if (bVar4 != null) {
                bVar4.a(arrayList);
            }
            throw th;
        }
        bVar.a(arrayList);
    }
}
