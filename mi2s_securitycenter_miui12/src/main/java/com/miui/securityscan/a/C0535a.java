package com.miui.securityscan.a;

import android.content.Context;
import android.os.AsyncTask;
import b.b.c.d.C0184d;
import com.miui.antivirus.result.C0243f;
import com.miui.appmanager.c.c;
import com.miui.common.card.models.AdvCardModel;
import com.miui.gamebooster.gamead.d;
import com.miui.luckymoney.config.Constants;
import com.miui.securityscan.a.C0536b;
import com.xiaomi.analytics.Actions;
import com.xiaomi.analytics.AdAction;
import com.xiaomi.analytics.Analytics;
import com.xiaomi.analytics.Tracker;
import java.util.ArrayList;
import java.util.Iterator;
import miui.os.Build;

/* renamed from: com.miui.securityscan.a.a  reason: case insensitive filesystem */
class C0535a extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f7567a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f7568b;

    C0535a(ArrayList arrayList, Context context) {
        this.f7567a = arrayList;
        this.f7568b = context;
    }

    private void a(String str, Tracker tracker, String str2, String[] strArr, String[] strArr2) {
        AdAction a2 = Actions.a(str);
        a2.b("e", str);
        a2.b("ex", str2);
        a2.b(Constants.JSON_KEY_T, String.valueOf(System.currentTimeMillis()));
        if ("VIEW".equals(str)) {
            C0536b.b(a2, strArr);
        } else if ("CLICK".equals(str)) {
            C0536b.b(a2, strArr2);
        }
        tracker.a(a2);
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        String[] strArr;
        String[] strArr2;
        String str;
        String str2;
        String str3 = "";
        if (Build.IS_INTERNATIONAL_BUILD) {
            str3 = "com.miui.securitycenter_globaladevent";
        } else {
            Object obj = this.f7567a.get(0);
            if (obj instanceof C0536b.a) {
                int usePosition = ((C0536b.a) obj).f7570b.getUsePosition();
                if (usePosition == 1) {
                    str3 = "com.miui.securitycenter_homepage";
                } else if (usePosition == 2 || usePosition == 3) {
                    str3 = "com.miui.securitycenter_scanresult";
                }
            } else if (obj instanceof C0536b.C0066b) {
                str3 = "com.miui.securitycenter_datamodel";
            } else if (obj instanceof C0536b.c) {
                str3 = "com.miui.securitycenter_appmanager";
            } else if (obj instanceof C0536b.d) {
                str3 = "com.miui.securitycenter_virusresult";
            } else if (obj instanceof C0536b.e) {
                str3 = "com.miui.securitycenter_gamebooster";
            }
        }
        Analytics a2 = Analytics.a(this.f7568b);
        a2.a(false);
        Tracker a3 = a2.a(str3);
        Iterator it = this.f7567a.iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof C0536b.a) {
                C0536b.a aVar = (C0536b.a) next;
                AdvCardModel a4 = aVar.f7570b;
                if (!a4.isLocal()) {
                    str2 = aVar.f7569a;
                    str = a4.getEx();
                    strArr2 = a4.getViewMonitorUrls();
                    strArr = a4.getClickMonitorUrls();
                }
            } else if (next instanceof C0536b.C0066b) {
                C0536b.C0066b bVar = (C0536b.C0066b) next;
                C0184d a5 = bVar.f7572b;
                str2 = bVar.f7571a;
                str = a5.d();
                strArr2 = a5.m();
                strArr = a5.b();
            } else if (next instanceof C0536b.c) {
                C0536b.c cVar = (C0536b.c) next;
                c a6 = cVar.f7574b;
                if (!a6.l()) {
                    str2 = cVar.f7573a;
                    str = a6.c();
                    strArr2 = a6.i();
                    strArr = a6.b();
                }
            } else if (next instanceof C0536b.d) {
                C0536b.d dVar = (C0536b.d) next;
                C0243f a7 = dVar.f7576b;
                str2 = dVar.f7575a;
                str = a7.f();
                strArr2 = a7.p();
                strArr = a7.d();
            } else if (next instanceof C0536b.e) {
                C0536b.e eVar = (C0536b.e) next;
                d a8 = eVar.f7578b;
                str2 = eVar.f7577a;
                str = a8.b();
                strArr2 = a8.e();
                strArr = a8.a();
            }
            a(str2, a3, str, strArr2, strArr);
        }
        return null;
    }
}
