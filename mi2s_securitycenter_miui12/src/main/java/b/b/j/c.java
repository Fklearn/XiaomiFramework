package b.b.j;

import android.app.Activity;
import android.content.Context;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securityscan.cards.b;
import com.miui.securityscan.i.e;
import java.util.ArrayList;
import java.util.List;

class c extends Thread {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f1813a;

    c(h hVar) {
        this.f1813a = hVar;
    }

    public void run() {
        Activity activity = this.f1813a.getActivity();
        if (this.f1813a.a(activity)) {
            List<BaseCardModel> b2 = e.b(activity);
            if (b2 == null || b2.isEmpty()) {
                List unused = this.f1813a.f = b.a((Context) activity);
            } else {
                List unused2 = this.f1813a.f = new ArrayList();
                this.f1813a.f.addAll(b2);
            }
            synchronized (this.f1813a.i) {
                boolean unused3 = this.f1813a.j = true;
                if (this.f1813a.k) {
                    this.f1813a.g.sendEmptyMessage(108);
                }
            }
        }
    }
}
