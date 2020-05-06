package com.miui.powercenter.quickoptimize;

import android.content.Context;
import b.b.c.i.b;
import com.miui.antivirus.result.C0238a;
import com.miui.powercenter.abnormalscan.e;
import com.miui.powercenter.deepsave.g;
import java.util.ArrayList;
import miui.os.Build;

public class B {

    /* renamed from: a  reason: collision with root package name */
    private Context f7184a;

    /* renamed from: b  reason: collision with root package name */
    private ScanResultFrame f7185b;

    /* renamed from: c  reason: collision with root package name */
    private r f7186c;

    /* renamed from: d  reason: collision with root package name */
    private e f7187d;

    public static class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public Context f7188a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public ScanResultFrame f7189b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public r f7190c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public e f7191d;
        private ArrayList<C0238a> e = new ArrayList<>();

        public a(Context context) {
            this.f7188a = context;
        }

        public a a(ScanResultFrame scanResultFrame) {
            this.f7189b = scanResultFrame;
            return this;
        }

        public a a(r rVar, e eVar) {
            this.f7190c = rVar;
            this.f7191d = eVar;
            return this;
        }

        public B a() {
            return new B(this);
        }
    }

    private B(a aVar) {
        this.f7184a = aVar.f7188a;
        this.f7186c = aVar.f7190c;
        this.f7187d = aVar.f7191d;
        this.f7185b = aVar.f7189b;
        this.f7185b.a(this.f7184a, this.f7186c, this.f7187d);
        this.f7185b.a();
    }

    public static void a(Context context) {
        com.miui.powercenter.deepsave.e.b().a(context);
        if (Build.IS_INTERNATIONAL_BUILD) {
            g.a("02-13");
        }
    }

    public void a() {
        this.f7185b.a(this.f7186c.getSectionCount());
    }

    public void a(b bVar) {
        this.f7185b.setEventHandler(bVar);
    }
}
