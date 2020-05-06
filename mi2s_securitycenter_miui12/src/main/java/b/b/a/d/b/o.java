package b.b.a.d.b;

import com.miui.antispam.service.a.b;
import com.miui.securitycenter.R;

class o implements b.C0036b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ s f1402a;

    o(s sVar) {
        this.f1402a = sVar;
    }

    public void a(int i) {
        s sVar;
        int i2;
        if (i != 0) {
            if (i == 1) {
                sVar = this.f1402a;
                i2 = R.string.toast_update_notneed;
            } else if (i == 2) {
                sVar = this.f1402a;
                i2 = R.string.toast_update_success;
            } else {
                return;
            }
            sVar.a(i2);
            this.f1402a.e();
            return;
        }
        this.f1402a.a((int) R.string.toast_update_fail);
    }
}
