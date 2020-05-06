package com.miui.gamebooster.n.b;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.gamebooster.c;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.n.d.m;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static d f4670a;

    /* renamed from: b  reason: collision with root package name */
    private HashMap<String, m> f4671b = new HashMap<>(20, 0.75f);

    /* renamed from: c  reason: collision with root package name */
    private List<m> f4672c = new ArrayList();

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        public int f4673a;

        /* renamed from: b  reason: collision with root package name */
        public String f4674b;

        /* renamed from: c  reason: collision with root package name */
        public String f4675c;

        public a(String str, String str2, int i) {
            this.f4673a = i;
            this.f4674b = str;
            this.f4675c = str2;
        }
    }

    private class b implements Comparator<m> {
        private b() {
        }

        /* synthetic */ b(d dVar, c cVar) {
            this();
        }

        /* renamed from: a */
        public int compare(m mVar, m mVar2) {
            return mVar2.h() != mVar.h() ? mVar2.h() - mVar.h() : mVar.f().compareToIgnoreCase(mVar2.f());
        }
    }

    private d() {
        d();
    }

    public static synchronized d a() {
        d dVar;
        synchronized (d.class) {
            if (f4670a == null) {
                f4670a = new d();
            }
            dVar = f4670a;
        }
        return dVar;
    }

    private List<a> b(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ArrayList arrayList = new ArrayList();
        String c2 = c();
        PackageManager packageManager = context.getPackageManager();
        try {
            for (ActivityManager.RecentTaskInfo recentTaskInfo : activityManager.getRecentTasks(64, 0)) {
                ResolveInfo resolveActivity = packageManager.resolveActivity(recentTaskInfo.baseIntent, 0);
                if (resolveActivity != null) {
                    String str = resolveActivity.activityInfo.packageName;
                    if (TextUtils.isEmpty(c2) || !str.equals(c2)) {
                        int intValue = ((Integer) e.a((Object) resolveActivity.activityInfo, "resizeMode", Integer.TYPE)).intValue();
                        if (C0393y.a(str, context) && intValue != 0) {
                            arrayList.add(new a(str, resolveActivity.activityInfo.name, resolveActivity.activityInfo.applicationInfo.uid));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private String c() {
        return com.miui.common.persistence.b.a("key_currentbooster_pkg_uid", (String) null).split(",")[0];
    }

    @NonNull
    private List<a> c(Context context) {
        HashMap<String, PackageInfo> a2 = com.miui.gamebooster.f.d.a(context).a((List<String>) null);
        ArrayList arrayList = new ArrayList();
        for (String next : a2.keySet()) {
            ResolveInfo a3 = x.a(context, next);
            if (a3 != null) {
                arrayList.add(new a(next, a3.activityInfo.name, a3.activityInfo.applicationInfo.uid));
            }
        }
        return arrayList;
    }

    private void d() {
        Application d2 = Application.d();
        List<c.b> c2 = c.a((Context) d2).c();
        List<a> c3 = c(d2);
        List<a> b2 = b(d2);
        for (a next : c3) {
            HashMap<String, m> hashMap = this.f4671b;
            String str = next.f4674b;
            hashMap.put(str, new m(str, next.f4675c, next.f4673a));
        }
        for (a aVar : b2) {
            m mVar = this.f4671b.get(aVar.f4674b);
            if (mVar != null) {
                mVar.a(1);
                mVar.a(0);
            }
        }
        for (c.b next2 : c2) {
            m mVar2 = this.f4671b.get(next2.f4097a);
            if (mVar2 != null) {
                mVar2.a(next2.f4098b);
                mVar2.a(next2.f4099c);
            }
        }
        this.f4672c.addAll(this.f4671b.values());
        Collections.sort(this.f4672c, new b(this, (c) null));
    }

    private void d(Context context) {
        ArrayList<String> arrayList = new ArrayList<>(com.miui.gamebooster.f.d.a(context).a((List<String>) null).keySet());
        ArrayList<String> arrayList2 = new ArrayList<>();
        for (String next : this.f4671b.keySet()) {
            if (!arrayList.contains(next)) {
                arrayList2.add(next);
            }
            arrayList.remove(next);
        }
        for (String remove : arrayList2) {
            this.f4671b.remove(remove);
        }
        for (String str : arrayList) {
            ResolveInfo a2 = x.a(context, str);
            if (a2 != null) {
                this.f4671b.put(str, new m(str, a2.activityInfo.name, a2.activityInfo.applicationInfo.uid));
            }
        }
        List<a> b2 = b(context);
        Set<String> keySet = this.f4671b.keySet();
        for (a next2 : b2) {
            if (keySet.contains(next2.f4674b)) {
                m mVar = this.f4671b.get(next2.f4674b);
                if (mVar.h() < 2) {
                    mVar.a(1);
                }
            } else {
                ResolveInfo a3 = x.a(context, next2.f4674b);
                if (a3 != null) {
                    m mVar2 = new m(next2.f4674b, a3.activityInfo.name, a3.activityInfo.applicationInfo.uid);
                    mVar2.a(1);
                    this.f4671b.put(next2.f4674b, mVar2);
                }
            }
        }
        this.f4672c.clear();
        this.f4672c.addAll(this.f4671b.values());
        Collections.sort(this.f4672c, new b(this, (c) null));
    }

    public List<m> a(Context context) {
        d(context);
        return this.f4672c;
    }

    public void a(String str) {
        m mVar = this.f4671b.get(str);
        if (mVar != null) {
            int h = mVar.h();
            int i = 2;
            if (h >= 2) {
                i = h + 1;
            }
            mVar.a(i);
            Collections.sort(this.f4672c, new b(this, (c) null));
        }
        n.a().b(new c(this));
    }

    public void b() {
        ArrayList arrayList = new ArrayList();
        for (m next : this.f4672c) {
            if (next.h() >= 2) {
                arrayList.add(next.f() + "," + next.e() + "," + next.h());
            }
        }
        c.a a2 = c.a((Context) Application.d()).a();
        a2.b(arrayList);
        a2.a();
    }
}
