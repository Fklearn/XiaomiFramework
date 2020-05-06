package com.miui.powercenter.quickoptimize;

import android.content.Context;
import b.b.c.f.a;
import com.miui.securitycenter.memory.MemoryModel;
import java.util.List;
import java.util.Map;

/* renamed from: com.miui.powercenter.quickoptimize.i  reason: case insensitive filesystem */
public class C0530i {

    /* renamed from: a  reason: collision with root package name */
    private static C0530i f7224a;

    /* renamed from: b  reason: collision with root package name */
    private Context f7225b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public b.b.c.f.a f7226c;

    /* renamed from: com.miui.powercenter.quickoptimize.i$a */
    public interface a {
        void a(List<MemoryModel> list);
    }

    /* renamed from: com.miui.powercenter.quickoptimize.i$b */
    public interface b {
        void a(Map<Integer, List<String>> map);
    }

    private C0530i(Context context) {
        this.f7225b = context;
        this.f7226c = b.b.c.f.a.a(context);
    }

    public static C0530i a(Context context) {
        if (f7224a == null) {
            f7224a = new C0530i(context.getApplicationContext());
        }
        return f7224a;
    }

    public void a(a aVar) {
        this.f7226c.a("miui.intent.action.MEMORY_CHECK_SERVICE", this.f7225b.getPackageName(), (a.C0027a) new C0528g(this, aVar));
    }

    public void a(b bVar) {
        this.f7226c.a("miui.intent.action.MEMORY_CHECK_SERVICE", this.f7225b.getPackageName(), (a.C0027a) new C0529h(this, bVar));
    }
}
