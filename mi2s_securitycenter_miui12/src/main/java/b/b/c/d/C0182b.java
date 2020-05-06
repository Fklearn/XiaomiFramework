package b.b.c.d;

import android.content.Context;
import com.miui.applicationlock.c.y;
import com.miui.securitycenter.Application;
import com.xiaomi.ad.feedback.IAdFeedbackListener;

/* renamed from: b.b.c.d.b  reason: case insensitive filesystem */
class C0182b extends IAdFeedbackListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0184d f1670a;

    C0182b(C0184d dVar) {
        this.f1670a = dVar;
    }

    public void onFinished(int i) {
        if (i > 0) {
            this.f1670a.o();
        }
        y.b().b((Context) Application.d());
    }
}
