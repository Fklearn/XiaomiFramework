package com.miui.powercenter.deepsave;

import android.content.Context;
import b.b.c.j.f;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import java.util.List;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static e f7053a;

    /* renamed from: b  reason: collision with root package name */
    private List<IdeaModel> f7054b = new ArrayList();

    private e() {
    }

    /* access modifiers changed from: private */
    public void a(List<IdeaModel> list) {
        this.f7054b.clear();
        if (list != null) {
            this.f7054b.addAll(list);
        }
    }

    public static e b() {
        if (f7053a == null) {
            f7053a = new e();
        }
        return f7053a;
    }

    public List<IdeaModel> a() {
        return this.f7054b;
    }

    public void a(Context context) {
        this.f7054b.clear();
        if (f.b(context) && h.i()) {
            new c(context.getApplicationContext(), new d(this)).execute(new Void[0]);
        }
    }
}
