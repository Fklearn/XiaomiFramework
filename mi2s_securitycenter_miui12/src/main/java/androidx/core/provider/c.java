package androidx.core.provider;

import android.os.Handler;
import androidx.core.content.res.g;
import androidx.core.provider.f;
import androidx.core.provider.k;

class c implements k.a<f.c> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g.a f744a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Handler f745b;

    c(g.a aVar, Handler handler) {
        this.f744a = aVar;
        this.f745b = handler;
    }

    public void a(f.c cVar) {
        int i;
        g.a aVar;
        if (cVar == null) {
            aVar = this.f744a;
            i = 1;
        } else {
            i = cVar.f758b;
            if (i == 0) {
                this.f744a.a(cVar.f757a, this.f745b);
                return;
            }
            aVar = this.f744a;
        }
        aVar.a(i, this.f745b);
    }
}
