package com.miui.applicationlock.c;

import android.hardware.miuiface.IMiuiFaceManager;
import android.hardware.miuiface.Miuiface;
import android.os.CancellationSignal;
import android.util.Log;

class B extends IMiuiFaceManager.AuthenticationCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C f3271a;

    B(C c2) {
        this.f3271a = c2;
    }

    public void onAuthenticationError(int i, CharSequence charSequence) {
        Log.i("applock_face_unlock", "authenCallback, onAuthenticationError code:" + i + " msg:" + charSequence);
        if (2001 != i) {
            if (2002 == i) {
                CancellationSignal unused = this.f3271a.o = null;
                this.f3271a.q.sendEmptyMessage(1007);
                return;
            }
            boolean unused2 = this.f3271a.r = false;
            this.f3271a.g();
        }
    }

    public void onAuthenticationFailed() {
        Log.i("applock_face_unlock", "authenCallback, onAuthenticationFailed");
        boolean unused = this.f3271a.r = false;
        this.f3271a.g();
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:31:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onAuthenticationHelp(int r5, java.lang.CharSequence r6) {
        /*
            r4 = this;
            super.onAuthenticationHelp(r5, r6)
            r6 = 5
            r0 = 1
            if (r5 == r6) goto L_0x000c
            com.miui.applicationlock.c.C r1 = r4.f3271a
            boolean unused = r1.m = r0
        L_0x000c:
            r1 = 14
            r2 = 3
            if (r5 != r1) goto L_0x0029
            com.miui.applicationlock.c.C r1 = r4.f3271a
            int r3 = r1.i
            int r3 = r3 + r0
            int unused = r1.i = r3
            com.miui.applicationlock.c.C r1 = r4.f3271a
            int r1 = r1.i
            if (r1 < r2) goto L_0x002f
            com.miui.applicationlock.c.C r1 = r4.f3271a
            boolean unused = r1.j = r0
            goto L_0x002f
        L_0x0029:
            com.miui.applicationlock.c.C r0 = r4.f3271a
            r1 = 0
            int unused = r0.i = r1
        L_0x002f:
            if (r5 == r2) goto L_0x0071
            r0 = 4
            r1 = 2131756181(0x7f100495, float:1.9143262E38)
            if (r5 == r0) goto L_0x006b
            if (r5 == r6) goto L_0x0065
            r6 = 2131756182(0x7f100496, float:1.9143264E38)
            switch(r5) {
                case 8: goto L_0x006b;
                case 9: goto L_0x006b;
                case 10: goto L_0x006b;
                case 11: goto L_0x006b;
                case 12: goto L_0x0042;
                case 13: goto L_0x005a;
                case 14: goto L_0x005a;
                default: goto L_0x003f;
            }
        L_0x003f:
            switch(r5) {
                case 21: goto L_0x0054;
                case 22: goto L_0x004e;
                case 23: goto L_0x0048;
                default: goto L_0x0042;
            }
        L_0x0042:
            com.miui.applicationlock.c.C r5 = r4.f3271a
        L_0x0044:
            int unused = r5.l = r6
            goto L_0x0077
        L_0x0048:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            r6 = 2131756191(0x7f10049f, float:1.9143283E38)
            goto L_0x0044
        L_0x004e:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            r6 = 2131756189(0x7f10049d, float:1.9143278E38)
            goto L_0x0044
        L_0x0054:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            r6 = 2131756190(0x7f10049e, float:1.914328E38)
            goto L_0x0044
        L_0x005a:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            boolean r0 = r5.j
            if (r0 == 0) goto L_0x0063
            goto L_0x0044
        L_0x0063:
            r6 = r1
            goto L_0x0044
        L_0x0065:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            r6 = 2131756188(0x7f10049c, float:1.9143276E38)
            goto L_0x0044
        L_0x006b:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            int unused = r5.l = r1
            goto L_0x0077
        L_0x0071:
            com.miui.applicationlock.c.C r5 = r4.f3271a
            r6 = 2131758465(0x7f100d81, float:1.9147895E38)
            goto L_0x0044
        L_0x0077:
            long r5 = java.lang.System.currentTimeMillis()
            com.miui.applicationlock.c.C r0 = r4.f3271a
            long r0 = r0.g
            long r5 = r5 - r0
            r0 = 1000(0x3e8, double:4.94E-321)
            int r5 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r5 <= 0) goto L_0x0093
            com.miui.applicationlock.c.C r5 = r4.f3271a
            android.os.Handler r5 = r5.q
            r6 = 1003(0x3eb, float:1.406E-42)
            r5.sendEmptyMessage(r6)
        L_0x0093:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.c.B.onAuthenticationHelp(int, java.lang.CharSequence):void");
    }

    public void onAuthenticationSucceeded(Miuiface miuiface) {
        super.onAuthenticationSucceeded(miuiface);
        Log.i("applock_face_unlock", " authenCallback, onAuthenticationSucceeded");
        Log.d("applock_face_unlock", "receive verify passed time=" + (System.currentTimeMillis() - this.f3271a.g));
        CancellationSignal unused = this.f3271a.o = null;
        boolean unused2 = this.f3271a.n = true;
        boolean unused3 = this.f3271a.r = false;
        this.f3271a.q.sendEmptyMessage(1002);
    }
}
