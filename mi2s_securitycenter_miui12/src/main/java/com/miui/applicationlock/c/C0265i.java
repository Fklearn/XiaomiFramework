package com.miui.applicationlock.c;

import android.text.TextUtils;
import b.b.c.j.B;
import com.miui.applicationlock.C0312y;
import com.miui.applicationlock.a.h;
import miui.os.UserHandle;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;

/* renamed from: com.miui.applicationlock.c.i  reason: case insensitive filesystem */
class C0265i extends IForegroundInfoListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0267k f3307a;

    C0265i(C0267k kVar) {
        this.f3307a = kVar;
    }

    public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) {
        String str = foregroundInfo.mForegroundPackageName;
        if (B.c() == UserHandle.myUserId() && B.j() == 0) {
            if (C0267k.g(this.f3307a.f3310a) && "com.miui.home".equals(str) && o.e()) {
                String n = o.n();
                if (!TextUtils.isEmpty(n) && !o.o().contains(n)) {
                    o.e(n);
                    C0267k.b(this.f3307a.f3310a, n);
                }
            }
            if (C0267k.g(this.f3307a.f3310a) && C0312y.f3468b.contains(str) && !C0259c.b(this.f3307a.f3310a).d()) {
                o.d(str);
                o.e(true);
                h.l();
            }
        }
    }
}
