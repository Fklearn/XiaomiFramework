package b.b.a.d.b;

import android.content.DialogInterface;
import b.b.c.j.f;
import com.miui.securitycenter.h;

class p implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f1403a;

    p(s sVar) {
        this.f1403a = sVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        h.b(true);
        if (f.a(this.f1403a.getActivity())) {
            this.f1403a.c();
        } else {
            this.f1403a.h();
        }
    }
}
