package com.miui.securityscan.a;

import android.content.Context;
import b.b.c.d.C0184d;
import com.miui.antivirus.result.C0243f;
import com.miui.common.card.models.AdvCardModel;
import com.miui.securitycenter.Application;
import com.xiaomi.analytics.AdAction;
import com.xiaomi.analytics.Analytics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* renamed from: com.miui.securityscan.a.b  reason: case insensitive filesystem */
public class C0536b {

    /* renamed from: com.miui.securityscan.a.b$a */
    public static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f7569a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public AdvCardModel f7570b;

        public a(String str, AdvCardModel advCardModel) {
            this.f7569a = str;
            this.f7570b = advCardModel;
        }
    }

    /* renamed from: com.miui.securityscan.a.b$b  reason: collision with other inner class name */
    public static class C0066b {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f7571a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public C0184d f7572b;

        public C0066b(String str, C0184d dVar) {
            this.f7571a = str;
            this.f7572b = dVar;
        }
    }

    /* renamed from: com.miui.securityscan.a.b$c */
    public static class c {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f7573a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public com.miui.appmanager.c.c f7574b;

        public c(String str, com.miui.appmanager.c.c cVar) {
            this.f7573a = str;
            this.f7574b = cVar;
        }

        public String toString() {
            return this.f7573a + " " + this.f7574b.f();
        }
    }

    /* renamed from: com.miui.securityscan.a.b$d */
    public static class d {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f7575a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public C0243f f7576b;

        public d(String str, C0243f fVar) {
            this.f7575a = str;
            this.f7576b = fVar;
        }
    }

    /* renamed from: com.miui.securityscan.a.b$e */
    public static class e {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f7577a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public com.miui.gamebooster.gamead.d f7578b;

        public e(String str, com.miui.gamebooster.gamead.d dVar) {
            this.f7577a = str;
            this.f7578b = dVar;
        }
    }

    static {
        Analytics.b((Context) Application.d());
    }

    public static void a(Context context, List<Object> list) {
        ArrayList arrayList = new ArrayList(list);
        if (arrayList.size() != 0) {
            new C0535a(arrayList, context).execute(new Void[0]);
        }
    }

    /* access modifiers changed from: private */
    public static void b(AdAction adAction, String[] strArr) {
        if (strArr != null && strArr.length > 0) {
            adAction.a(Arrays.asList(strArr));
        }
    }
}
