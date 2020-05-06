package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.UserHandle;
import android.util.Log;
import b.b.c.j.g;
import b.b.c.j.x;
import com.miui.gamebooster.service.M;
import com.miui.gamebooster.service.MiuiVpnManageServiceCallback;
import com.miui.gamebooster.service.r;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import miui.os.Build;

public class L extends m {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4340a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f4341b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f4342c;

    /* renamed from: d  reason: collision with root package name */
    private Context f4343d;
    /* access modifiers changed from: private */
    public r e;
    /* access modifiers changed from: private */
    public IMiuiVpnManageService f;
    /* access modifiers changed from: private */
    public M g = M.NOTINIT;
    private ServiceConnection h = new I(this);
    /* access modifiers changed from: private */
    public MiuiVpnManageServiceCallback i = new K(this);

    public L(Context context, r rVar) {
        this.f4343d = context;
        this.e = rVar;
    }

    /* access modifiers changed from: private */
    public void f() {
        try {
            this.f.init("xunyou");
        } catch (Exception e2) {
            Log.e("XunyouBoosterService", "mMiuiVpnService Exception:" + e2);
        }
    }

    public void a() {
        if (this.f4340a) {
            try {
                this.f.unregisterCallback(this.i);
                if (this.h != null && this.f4342c) {
                    this.f.disConnectVpn();
                    this.f4343d.unbindService(this.h);
                    this.f4342c = false;
                    Log.i("GameBoosterService", "xunyoubooster...stop");
                }
            } catch (Exception e2) {
                Log.e("XunyouBoosterService", "mMiuiVpnService Exception:" + e2);
            }
        }
    }

    public boolean b() {
        return !Build.IS_INTERNATIONAL_BUILD;
    }

    public void c() {
        String a2 = this.e.a();
        String charSequence = a2 != null ? x.j(this.f4343d, a2).toString() : null;
        if (this.f4340a && this.e.f().contains(charSequence)) {
            Intent intent = new Intent();
            intent.setPackage("com.miui.securitycenter");
            intent.setAction("com.miui.networkassistant.vpn.MIUI_VPN_MANAGE_SERVICE");
            this.f4342c = g.a(this.f4343d, intent, this.h, 1, UserHandle.OWNER);
            Log.i("GameBoosterService", "xunyoubooster...start");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001c, code lost:
        if (com.miui.gamebooster.c.a.o(true) != false) goto L_0x0020;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void d() {
        /*
            r3 = this;
            boolean r0 = com.miui.gamebooster.d.a.a()
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x001f
            android.content.Context r0 = r3.f4343d
            com.miui.gamebooster.c.a.a((android.content.Context) r0)
            boolean r0 = com.miui.gamebooster.c.a.y(r2)
            if (r0 == 0) goto L_0x001f
            android.content.Context r0 = r3.f4343d
            com.miui.gamebooster.c.a.a((android.content.Context) r0)
            boolean r0 = com.miui.gamebooster.c.a.o(r1)
            if (r0 == 0) goto L_0x001f
            goto L_0x0020
        L_0x001f:
            r1 = r2
        L_0x0020:
            r3.f4340a = r1
            com.miui.gamebooster.service.M r0 = com.miui.gamebooster.service.M.NOTINIT
            r3.g = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.gbservices.L.d():void");
    }

    public int e() {
        return 7;
    }
}
