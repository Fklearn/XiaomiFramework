package com.miui.applicationlock;

import android.os.AsyncTask;
import b.b.c.j.i;
import com.miui.applicationlock.C0312y;
import com.miui.applicationlock.c.q;

class r extends AsyncTask<Void, Void, Integer> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3374a;

    r(C0312y yVar) {
        this.f3374a = yVar;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0042, code lost:
        if (com.miui.common.persistence.b.a("cancel_fingerprint_guide_times", 0) < 2) goto L_0x0046;
     */
    /* renamed from: a */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Integer doInBackground(java.lang.Void... r3) {
        /*
            r2 = this;
            com.miui.applicationlock.y r3 = r2.f3374a
            com.miui.applicationlock.c.E r3 = r3.l
            boolean r3 = r3.d()
            r0 = 2
            r1 = 0
            if (r3 == 0) goto L_0x0045
            com.miui.applicationlock.y r3 = r2.f3374a
            com.miui.applicationlock.c.c r3 = r3.t
            boolean r3 = r3.i()
            if (r3 != 0) goto L_0x0045
            com.miui.applicationlock.y r3 = r2.f3374a
            android.app.Activity r3 = r3.getActivity()
            boolean r3 = com.miui.applicationlock.TransitionHelper.a(r3)
            if (r3 == 0) goto L_0x003c
            com.miui.applicationlock.y r3 = r2.f3374a
            com.miui.applicationlock.c.E r3 = r3.l
            boolean r3 = r3.c()
            if (r3 == 0) goto L_0x003c
            java.lang.String r3 = "cancel_fingerprint_verify_times"
            int r3 = com.miui.common.persistence.b.a((java.lang.String) r3, (int) r1)
            if (r3 >= r0) goto L_0x0045
            r0 = 1
            goto L_0x0046
        L_0x003c:
            java.lang.String r3 = "cancel_fingerprint_guide_times"
            int r3 = com.miui.common.persistence.b.a((java.lang.String) r3, (int) r1)
            if (r3 >= r0) goto L_0x0045
            goto L_0x0046
        L_0x0045:
            r0 = r1
        L_0x0046:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.r.doInBackground(java.lang.Void[]):java.lang.Integer");
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Integer num) {
        if (this.f3374a.z != null && !this.f3374a.z.isFinishing() && !this.f3374a.z.isDestroyed()) {
            if (num.intValue() == 1) {
                if (i.d()) {
                    this.f3374a.h();
                } else {
                    this.f3374a.g();
                }
                this.f3374a.l.a((q) new C0312y.a(this.f3374a, (C0283k) null), 1);
            } else if (num.intValue() == 2) {
                this.f3374a.j();
            }
        }
    }
}
