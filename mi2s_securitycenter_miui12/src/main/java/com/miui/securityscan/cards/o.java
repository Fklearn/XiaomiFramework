package com.miui.securityscan.cards;

import android.content.Context;
import android.os.AsyncTask;
import b.b.c.j.s;
import b.b.c.j.u;
import com.miui.securityscan.cards.n;
import java.util.List;

class o extends AsyncTask<Void, Void, u.a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7680a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ n f7681b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ List f7682c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ n.b f7683d;

    o(n.b bVar, Context context, n nVar, List list) {
        this.f7683d = bVar;
        this.f7680a = context;
        this.f7681b = nVar;
        this.f7682c = list;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public u.a doInBackground(Void... voidArr) {
        u.a c2 = u.c(this.f7680a);
        s.a("networkassist : " + c2);
        return c2;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(u.a aVar) {
        if (aVar != null) {
            long j = aVar.f1763b;
            boolean z = false;
            if (j == -1 || aVar.f1762a == 0) {
                n nVar = this.f7681b;
                nVar.q = true;
                nVar.r = false;
                nVar.s = 0;
            } else {
                n nVar2 = this.f7681b;
                if (j < aVar.f1764c) {
                    z = true;
                }
                nVar2.q = z;
                n nVar3 = this.f7681b;
                nVar3.r = true;
                nVar3.s = aVar.f1762a - aVar.f1763b;
            }
            this.f7681b.t = aVar.f1765d;
            for (n.c onNetworkAssistChange : this.f7682c) {
                n nVar4 = this.f7681b;
                onNetworkAssistChange.onNetworkAssistChange(nVar4.q, nVar4.r, nVar4.s, nVar4.t);
            }
        }
    }
}
