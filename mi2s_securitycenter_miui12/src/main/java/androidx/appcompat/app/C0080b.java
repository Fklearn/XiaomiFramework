package androidx.appcompat.app;

import android.view.View;

/* renamed from: androidx.appcompat.app.b  reason: case insensitive filesystem */
class C0080b implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AlertController f290a;

    C0080b(AlertController alertController) {
        this.f290a = alertController;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0020, code lost:
        r3 = r0.y;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r3) {
        /*
            r2 = this;
            androidx.appcompat.app.AlertController r0 = r2.f290a
            android.widget.Button r1 = r0.o
            if (r3 != r1) goto L_0x000f
            android.os.Message r0 = r0.q
            if (r0 == 0) goto L_0x000f
        L_0x000a:
            android.os.Message r3 = android.os.Message.obtain(r0)
            goto L_0x002a
        L_0x000f:
            androidx.appcompat.app.AlertController r0 = r2.f290a
            android.widget.Button r1 = r0.s
            if (r3 != r1) goto L_0x001a
            android.os.Message r0 = r0.u
            if (r0 == 0) goto L_0x001a
            goto L_0x000a
        L_0x001a:
            androidx.appcompat.app.AlertController r0 = r2.f290a
            android.widget.Button r1 = r0.w
            if (r3 != r1) goto L_0x0029
            android.os.Message r3 = r0.y
            if (r3 == 0) goto L_0x0029
            android.os.Message r3 = android.os.Message.obtain(r3)
            goto L_0x002a
        L_0x0029:
            r3 = 0
        L_0x002a:
            if (r3 == 0) goto L_0x002f
            r3.sendToTarget()
        L_0x002f:
            androidx.appcompat.app.AlertController r3 = r2.f290a
            android.os.Handler r0 = r3.R
            r1 = 1
            androidx.appcompat.app.z r3 = r3.f232b
            android.os.Message r3 = r0.obtainMessage(r1, r3)
            r3.sendToTarget()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.app.C0080b.onClick(android.view.View):void");
    }
}
