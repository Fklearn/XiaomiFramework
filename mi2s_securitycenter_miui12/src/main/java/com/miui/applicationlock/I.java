package com.miui.applicationlock;

import android.content.DialogInterface;

class I implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseAccessControl f3180a;

    I(ChooseAccessControl chooseAccessControl) {
        this.f3180a = chooseAccessControl;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x001f A[Catch:{ Throwable -> 0x00c8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00e7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.content.DialogInterface r10, int r11) {
        /*
            r9 = this;
            java.lang.String r0 = "mixed"
            r1 = 1
            if (r11 != 0) goto L_0x000d
            com.miui.applicationlock.ChooseAccessControl r2 = r9.f3180a
            java.lang.String r3 = "pattern"
        L_0x0009:
            java.lang.String unused = r2.n = r3
            goto L_0x0019
        L_0x000d:
            if (r11 != r1) goto L_0x0014
            com.miui.applicationlock.ChooseAccessControl r2 = r9.f3180a
            java.lang.String r3 = "numeric"
            goto L_0x0009
        L_0x0014:
            com.miui.applicationlock.ChooseAccessControl r2 = r9.f3180a
            java.lang.String unused = r2.n = r0
        L_0x0019:
            boolean r2 = b.b.c.j.A.a()     // Catch:{ Throwable -> 0x00c8 }
            if (r2 == 0) goto L_0x00cf
            android.view.View[] r2 = new android.view.View[r1]     // Catch:{ Throwable -> 0x00c8 }
            com.miui.applicationlock.ChooseAccessControl r3 = r9.f3180a     // Catch:{ Throwable -> 0x00c8 }
            android.widget.TextView r3 = r3.e     // Catch:{ Throwable -> 0x00c8 }
            r4 = 0
            r2[r4] = r3     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IFolme r2 = miui.animation.Folme.useAt(r2)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.visible()     // Catch:{ Throwable -> 0x00c8 }
            r5 = 60
            miui.animation.IVisibleStyle r2 = r2.setShowDelay(r5)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType[] r3 = new miui.animation.IVisibleStyle.VisibleType[r1]     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType r7 = miui.animation.IVisibleStyle.VisibleType.HIDE     // Catch:{ Throwable -> 0x00c8 }
            r3[r4] = r7     // Catch:{ Throwable -> 0x00c8 }
            r7 = 0
            miui.animation.IVisibleStyle r2 = r2.setAlpha(r7, r3)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setHide()     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.base.AnimConfig[] r3 = new miui.animation.base.AnimConfig[r4]     // Catch:{ Throwable -> 0x00c8 }
            r2.show(r3)     // Catch:{ Throwable -> 0x00c8 }
            android.view.View[] r2 = new android.view.View[r1]     // Catch:{ Throwable -> 0x00c8 }
            com.miui.applicationlock.ChooseAccessControl r3 = r9.f3180a     // Catch:{ Throwable -> 0x00c8 }
            android.widget.TextView r3 = r3.r     // Catch:{ Throwable -> 0x00c8 }
            r2[r4] = r3     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IFolme r2 = miui.animation.Folme.useAt(r2)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.visible()     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setShowDelay(r5)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType[] r3 = new miui.animation.IVisibleStyle.VisibleType[r1]     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType r8 = miui.animation.IVisibleStyle.VisibleType.HIDE     // Catch:{ Throwable -> 0x00c8 }
            r3[r4] = r8     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setAlpha(r7, r3)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setHide()     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.base.AnimConfig[] r3 = new miui.animation.base.AnimConfig[r4]     // Catch:{ Throwable -> 0x00c8 }
            r2.show(r3)     // Catch:{ Throwable -> 0x00c8 }
            android.view.View[] r2 = new android.view.View[r1]     // Catch:{ Throwable -> 0x00c8 }
            com.miui.applicationlock.ChooseAccessControl r3 = r9.f3180a     // Catch:{ Throwable -> 0x00c8 }
            android.widget.TextView r3 = r3.f3116b     // Catch:{ Throwable -> 0x00c8 }
            r2[r4] = r3     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IFolme r2 = miui.animation.Folme.useAt(r2)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.visible()     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setShowDelay(r5)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType[] r3 = new miui.animation.IVisibleStyle.VisibleType[r1]     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType r5 = miui.animation.IVisibleStyle.VisibleType.HIDE     // Catch:{ Throwable -> 0x00c8 }
            r3[r4] = r5     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setAlpha(r7, r3)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.setHide()     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.base.AnimConfig[] r3 = new miui.animation.base.AnimConfig[r4]     // Catch:{ Throwable -> 0x00c8 }
            r2.show(r3)     // Catch:{ Throwable -> 0x00c8 }
            android.view.View[] r2 = new android.view.View[r1]     // Catch:{ Throwable -> 0x00c8 }
            com.miui.applicationlock.ChooseAccessControl r3 = r9.f3180a     // Catch:{ Throwable -> 0x00c8 }
            com.miui.applicationlock.widget.PasswordUnlockMediator r3 = r3.p     // Catch:{ Throwable -> 0x00c8 }
            r2[r4] = r3     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IFolme r2 = miui.animation.Folme.useAt(r2)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r2 = r2.visible()     // Catch:{ Throwable -> 0x00c8 }
            r5 = 300(0x12c, double:1.48E-321)
            miui.animation.IVisibleStyle r2 = r2.setShowDelay(r5)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType[] r1 = new miui.animation.IVisibleStyle.VisibleType[r1]     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle$VisibleType r3 = miui.animation.IVisibleStyle.VisibleType.HIDE     // Catch:{ Throwable -> 0x00c8 }
            r1[r4] = r3     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r1 = r2.setAlpha(r7, r1)     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.IVisibleStyle r1 = r1.setHide()     // Catch:{ Throwable -> 0x00c8 }
            miui.animation.base.AnimConfig[] r2 = new miui.animation.base.AnimConfig[r4]     // Catch:{ Throwable -> 0x00c8 }
            r1.show(r2)     // Catch:{ Throwable -> 0x00c8 }
            goto L_0x00cf
        L_0x00c8:
            java.lang.String r1 = "ChooseAccessControl"
            java.lang.String r2 = "not support folme"
            android.util.Log.e(r1, r2)
        L_0x00cf:
            com.miui.applicationlock.ChooseAccessControl r1 = r9.f3180a
            java.lang.String r1 = r1.n
            boolean r0 = r0.equals(r1)
            r1 = 131072(0x20000, float:1.83671E-40)
            if (r0 == 0) goto L_0x00e7
            com.miui.applicationlock.ChooseAccessControl r0 = r9.f3180a
            android.view.Window r0 = r0.getWindow()
            r0.addFlags(r1)
            goto L_0x00f0
        L_0x00e7:
            com.miui.applicationlock.ChooseAccessControl r0 = r9.f3180a
            android.view.Window r0 = r0.getWindow()
            r0.clearFlags(r1)
        L_0x00f0:
            com.miui.applicationlock.ChooseAccessControl r0 = r9.f3180a
            java.lang.String r1 = r0.n
            r0.c((java.lang.String) r1)
            r10.dismiss()
            android.os.Handler r10 = new android.os.Handler
            r10.<init>()
            com.miui.applicationlock.H r0 = new com.miui.applicationlock.H
            r0.<init>(r9, r11)
            r1 = 100
            r10.postDelayed(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.applicationlock.I.onClick(android.content.DialogInterface, int):void");
    }
}
