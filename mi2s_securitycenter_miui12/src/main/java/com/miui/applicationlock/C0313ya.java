package com.miui.applicationlock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import b.b.c.i.a;
import b.b.c.j.B;
import b.b.c.j.x;
import com.miui.applicationlock.FirstUseAppLockActivity;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.o;
import java.util.ArrayList;
import java.util.List;

/* renamed from: com.miui.applicationlock.ya  reason: case insensitive filesystem */
class C0313ya extends a<ArrayList<C0257a>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ FirstUseAppLockActivity f3474b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ FirstUseAppLockActivity.a f3475c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0313ya(FirstUseAppLockActivity.a aVar, Context context, FirstUseAppLockActivity firstUseAppLockActivity) {
        super(context);
        this.f3475c = aVar;
        this.f3474b = firstUseAppLockActivity;
    }

    /* JADX WARNING: type inference failed for: r5v1, types: [android.content.Context, com.miui.applicationlock.FirstUseAppLockActivity] */
    public ArrayList<C0257a> loadInBackground() {
        List<ApplicationInfo> c2 = o.c();
        ArrayList<String> arrayList = C0312y.f3468b;
        ArrayList<C0257a> arrayList2 = new ArrayList<>();
        for (ApplicationInfo next : c2) {
            String str = next.packageName;
            if (arrayList.indexOf(str) != -1) {
                arrayList2.add(new C0257a(x.a((Context) this.f3474b, next), (Integer) null, str, B.c(next.uid)));
            }
        }
        return arrayList2;
    }
}
