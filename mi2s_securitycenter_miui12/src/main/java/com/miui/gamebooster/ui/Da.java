package com.miui.gamebooster.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import b.b.c.b.b;
import b.b.c.i.a;
import com.miui.appmanager.AppManageUtils;
import com.miui.gamebooster.e;
import com.miui.gamebooster.mutiwindow.f;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class Da extends a<C0455va> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ QuickReplySettingsActivity f4871b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Da(QuickReplySettingsActivity quickReplySettingsActivity, Context context) {
        super(context);
        this.f4871b = quickReplySettingsActivity;
    }

    public C0455va loadInBackground() {
        String str;
        C0455va vaVar = new C0455va();
        Context applicationContext = this.f4871b.getApplicationContext();
        b a2 = b.a(applicationContext);
        ArrayList<PackageInfo> arrayList = new ArrayList<>(a2.a());
        HashSet<ComponentName> h = AppManageUtils.h(applicationContext);
        QuickReplySettingsActivity quickReplySettingsActivity = this.f4871b;
        List a3 = quickReplySettingsActivity.a(quickReplySettingsActivity.e, 0, h);
        vaVar.f5122b = QuickReplySettingsActivity.a(applicationContext);
        boolean unused = this.f4871b.k = f.d(applicationContext);
        List arrayList2 = new ArrayList();
        if (this.f4871b.k && !this.f4871b.j && vaVar.f5122b.isEmpty()) {
            arrayList2 = f.b(applicationContext);
        }
        for (PackageInfo packageInfo : arrayList) {
            if (!AppManageUtils.g.contains(packageInfo.packageName) && a3.contains(packageInfo.packageName) && !QuickReplySettingsActivity.f4966a.contains(packageInfo.packageName)) {
                String concat = "pkg_icon://".concat(packageInfo.packageName);
                try {
                    str = a2.a(packageInfo.packageName).a();
                } catch (Exception e) {
                    Log.e("QuickReplySettings", "getAppInfo error", e);
                    str = null;
                }
                if (str != null) {
                    boolean contains = arrayList2.contains(packageInfo.packageName);
                    boolean z = vaVar.f5122b.contains(packageInfo.packageName) || contains;
                    if (contains) {
                        com.miui.gamebooster.provider.a.a(applicationContext, packageInfo.packageName, packageInfo.applicationInfo.uid);
                        vaVar.f5122b.add(packageInfo.packageName);
                    }
                    vaVar.f5121a.add(new e(packageInfo.packageName, concat, str, packageInfo.applicationInfo.uid, z, 1));
                }
            }
        }
        return vaVar;
    }
}
