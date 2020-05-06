package com.miui.gamebooster.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import b.b.c.c.a;
import b.b.c.f.a;
import com.miui.applicationlock.c.K;
import com.miui.common.customview.AdImageView;
import com.miui.common.persistence.b;
import com.miui.gamebooster.gamead.e;
import com.miui.gamebooster.l.a;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.m.ia;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.C0396b;
import com.miui.gamebooster.model.n;
import com.miui.gamebooster.model.o;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.gamebooster.ui.N;
import com.miui.gamebooster.xunyou.MainMiuiVpnManageServiceCallback;
import com.miui.gamebooster.xunyou.c;
import com.miui.gamebooster.xunyou.d;
import com.miui.gamebooster.xunyou.f;
import com.miui.gamebooster.xunyou.i;
import com.miui.gamebooster.xunyou.m;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.a.C0536b;
import com.miui.securityscan.cards.g;
import com.miui.securityscan.i.l;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import miui.app.ActionBar;
import miui.app.AlertDialog;
import miui.security.SecurityManager;

public class GameBoosterRealMainActivity extends a implements com.miui.gamebooster.xunyou.a, n, o {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f4885a = "GameBoosterRealMainActivity";
    private Handler A = new C0420da(this);

    /* renamed from: b  reason: collision with root package name */
    private boolean f4886b = (!com.miui.gamebooster.d.a.a());

    /* renamed from: c  reason: collision with root package name */
    public m f4887c;

    /* renamed from: d  reason: collision with root package name */
    private i f4888d;
    private AlertDialog e;
    private N.a f;
    public IMiuiVpnManageService g;
    /* access modifiers changed from: private */
    public LocalBroadcastManager h;
    private SecurityManager i;
    public d j;
    /* access modifiers changed from: private */
    public MainMiuiVpnManageServiceCallback k;
    public boolean l;
    public boolean m = false;
    public boolean n;
    public boolean o = b.a("key_gamebooster_support_sign_function", false);
    public boolean p;
    /* access modifiers changed from: private */
    public IGameBooster q;
    public String r;
    private com.miui.gamebooster.l.a s;
    private a.C0047a t;
    private g.a u;
    public List<AsyncTask<Void, Void, Boolean>> v = new CopyOnWriteArrayList();
    private Context w;
    private Fragment x;
    private ServiceConnection y = new W(this);
    a.C0027a z = new X(this);

    /* JADX WARNING: type inference failed for: r2v0, types: [android.app.Activity] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.app.Activity r8) {
        /*
            r7 = this;
            android.app.Application r0 = r8.getApplication()
            com.miui.gamebooster.m.ba.b(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 28
            if (r0 < r1) goto L_0x005a
            boolean r0 = com.miui.gamebooster.m.C0388t.s()
            if (r0 != 0) goto L_0x005a
            boolean r0 = com.miui.securitycenter.h.i()
            if (r0 == 0) goto L_0x005a
            android.content.Context r0 = r8.getApplicationContext()
            boolean r0 = com.miui.securityscan.i.c.f(r0)
            if (r0 != 0) goto L_0x0024
            goto L_0x005a
        L_0x0024:
            com.miui.gamebooster.ui.Z r0 = new com.miui.gamebooster.ui.Z
            r0.<init>(r7, r8)
            r7.t = r0
            com.miui.gamebooster.l.a r0 = new com.miui.gamebooster.l.a
            com.miui.gamebooster.l.a$a r5 = r7.t
            java.lang.String r3 = "gamebooster"
            java.lang.String r4 = "appinfo"
            java.lang.String r6 = "com.miui.securityadd"
            r1 = r0
            r2 = r7
            r1.<init>(r2, r3, r4, r5, r6)
            r7.s = r0
            com.miui.gamebooster.l.a r0 = r7.s
            java.util.concurrent.Executor r1 = android.os.AsyncTask.THREAD_POOL_EXECUTOR
            r2 = 0
            java.lang.Void[] r2 = new java.lang.Void[r2]
            r0.executeOnExecutor(r1, r2)
            com.miui.gamebooster.ui.aa r0 = new com.miui.gamebooster.ui.aa
            r0.<init>(r7, r8)
            r7.u = r0
            android.content.Context r8 = r8.getApplicationContext()
            com.miui.securityscan.cards.g r8 = com.miui.securityscan.cards.g.a((android.content.Context) r8)
            com.miui.securityscan.cards.g$a r0 = r7.u
            r8.b((com.miui.securityscan.cards.g.a) r0)
        L_0x005a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.ui.GameBoosterRealMainActivity.a(android.app.Activity):void");
    }

    /* access modifiers changed from: private */
    public void a(C0396b bVar, Activity activity) {
        this.e = new AlertDialog.Builder(activity).setTitle(getResources().getString(R.string.securityadd_update_tips_title)).setMessage(getResources().getString(R.string.securityadd_update_tips_message, new Object[]{Integer.valueOf((bVar.a() / 1024) / 1024)})).setNegativeButton(getResources().getString(R.string.securityadd_update_tips_cancle), new C0418ca(this)).setPositiveButton(getResources().getString(R.string.securityadd_update_tips_now), new C0416ba(this, bVar, activity)).create();
        this.e.show();
    }

    private void p() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity] */
    /* access modifiers changed from: private */
    public void q() {
        new AlertDialog.Builder(this).setTitle(R.string.gamebooster_network_dialog_title).setMessage(R.string.gamebooster_network_dialog_message).setPositiveButton(17039370, new V(this)).setNegativeButton(17039360, new C0422ea(this)).create().show();
    }

    /* access modifiers changed from: private */
    public void r() {
        f fVar = new f(this, false);
        this.v.add(fVar);
        fVar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, com.miui.gamebooster.xunyou.a] */
    private void s() {
        if (this.f4888d == null) {
            this.f4887c = new m(this);
            this.f4887c.setGiftCallBack(new c(this));
            this.f4888d = new i(this, this.f4887c, R.style.gb_gift_dialog);
        }
        this.f4888d.show();
        C0373d.b("show", "time");
        this.f4887c.d();
    }

    private void t() {
        if (this.x == null) {
            N n2 = new N();
            n2.a((n) this);
            n2.a((Runnable) new Y(this));
            this.x = n2;
            FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.fragment, this.x);
            beginTransaction.show(this.x);
            beginTransaction.commit();
        }
    }

    private void u() {
        f fVar = new f(this, true);
        this.v.add(fVar);
        fVar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public void a(int i2, String str) {
        o g2;
        Fragment fragment = this.x;
        if ((fragment instanceof N) && (g2 = ((N) fragment).g()) != null) {
            g2.a(i2, str);
        }
    }

    public void a(AdImageView adImageView, int i2, com.miui.gamebooster.gamead.d dVar) {
        if (com.miui.gamebooster.c.b.f4100a) {
            String str = f4885a;
            Log.d(str, " startAdCountdown : " + dVar.c());
        }
        adImageView.a(this.A, i2, dVar);
    }

    public void a(e eVar) {
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity] */
    public void a(String str) {
        b();
        C0393y.a((Context) this, this.r + "&gift=" + str, getResources().getString(R.string.xunyou_pay_webview));
    }

    public void a(String str, com.miui.gamebooster.gamead.d dVar) {
        if (!dVar.d()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new C0536b.e(str, dVar));
            C0536b.a(getApplicationContext(), (List<Object>) arrayList);
            if (com.miui.gamebooster.c.b.f4100a) {
                String str2 = f4885a;
                Log.d(str2, " addAdvertisementEvent : " + str + " id : " + dVar.c());
            }
        }
    }

    public void b() {
        i iVar = this.f4888d;
        if (iVar != null && iVar.isShowing()) {
            this.f4888d.dismiss();
        }
    }

    public void c() {
        o g2;
        Fragment fragment = this.x;
        if ((fragment instanceof N) && (g2 = ((N) fragment).g()) != null) {
            g2.c();
        }
    }

    public void d() {
        if (h.i()) {
            u();
        } else {
            q();
        }
    }

    public void e() {
        o g2;
        Fragment fragment = this.x;
        if ((fragment instanceof N) && (g2 = ((N) fragment).g()) != null) {
            g2.e();
        }
    }

    public void f() {
        o g2;
        Fragment fragment = this.x;
        if ((fragment instanceof N) && (g2 = ((N) fragment).g()) != null) {
            g2.f();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity] */
    public void k() {
        startActivity(new Intent(this, GameBoosterSettingActivity.class));
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void m() {
        if (!K.c(this)) {
            if (!com.miui.applicationlock.c.o.a(this.i, "com.xiaomi.account")) {
                com.miui.applicationlock.c.o.b(this.i, "com.xiaomi.account");
            }
            K.a(this, new Bundle());
            return;
        }
        try {
            if (this.n) {
                s();
                if (com.miui.gamebooster.k.b.b().c() >= 8) {
                    this.g.init("xunyou");
                    this.f4887c.e();
                    this.p = true;
                    return;
                }
                return;
            }
            o();
        } catch (Exception e2) {
            String str = f4885a;
            Log.i(str, "MiuiVpnServiceException:" + e2.toString());
        }
    }

    public IGameBooster n() {
        return this.q;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity] */
    public void o() {
        Toast.makeText(this, getResources().getString(R.string.gb_network_status_bad), 0).show();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        GameBoosterRealMainActivity.super.onActivityResult(i2, i3, intent);
        if (i2 == 201 && i3 == -1) {
            l.a(getApplicationContext(), true);
            N.a aVar = this.f;
            if (aVar != null) {
                aVar.a();
            }
        }
    }

    public void onBackPressed() {
        Fragment fragment = this.x;
        if (!(fragment instanceof com.miui.gamebooster.model.m) || !((com.miui.gamebooster.model.m) fragment).c()) {
            GameBoosterRealMainActivity.super.onBackPressed();
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity, android.app.Activity] */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        na.b((Activity) this);
        na.c((Activity) this);
        this.w = getApplicationContext();
        setContentView(R.layout.gb_activity_main);
        p();
        t();
        this.h = LocalBroadcastManager.getInstance(this);
        this.i = (SecurityManager) getSystemService("security");
        this.j = new d(this);
        this.k = new MainMiuiVpnManageServiceCallback(this);
        C0390v.a((Context) this).a(this.z);
        if (!this.f4886b) {
            Intent intent = new Intent();
            intent.setPackage("com.miui.securitycenter");
            intent.setAction("com.miui.networkassistant.vpn.MIUI_VPN_MANAGE_SERVICE");
            b.b.c.j.g.a((Context) this, intent, this.y, 1, UserHandle.OWNER);
            if (h.i()) {
                r();
            }
            if (b.a("key_gamebooster_red_point_press", "").equals(DateUtil.getDateFormat(2).format(new Date()))) {
                c();
            }
        }
        a((Activity) this);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.gamebooster.ui.GameBoosterRealMainActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        ServiceConnection serviceConnection;
        for (AsyncTask next : this.v) {
            if (next != null) {
                next.cancel(true);
            }
        }
        com.miui.gamebooster.l.a aVar = this.s;
        if (aVar != null) {
            aVar.cancel(true);
        }
        C0390v.a((Context) this).a();
        ia.a().b();
        if (!(this.g == null || (serviceConnection = this.y) == null)) {
            unbindService(serviceConnection);
            try {
                this.g.unregisterCallback(this.k);
            } catch (Exception e2) {
                Log.i(f4885a, e2.toString());
            }
        }
        if (this.u != null) {
            g.a(getApplicationContext()).d(this.u);
        }
        GameBoosterRealMainActivity.super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        Fragment fragment = this.x;
        if (fragment instanceof com.miui.gamebooster.xunyou.b) {
            ((com.miui.gamebooster.xunyou.b) fragment).d();
        }
    }
}
