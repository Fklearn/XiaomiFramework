package b.b.b.c;

import android.content.Context;
import android.view.View;
import b.b.b.a.b;
import com.miui.securitycenter.Application;

class c implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f1489a;

    c(e eVar) {
        this.f1489a = eVar;
    }

    public void onClick(View view) {
        if (!this.f1489a.g && this.f1489a.k) {
            if (this.f1489a.f1494d != null) {
                e.a((Context) Application.d(), this.f1489a.f1494d.g());
                boolean unused = this.f1489a.h = true;
            }
            this.f1489a.l.removeMessages(1);
            boolean unused2 = this.f1489a.g = true;
            b.a.a();
            this.f1489a.b();
        }
    }
}
