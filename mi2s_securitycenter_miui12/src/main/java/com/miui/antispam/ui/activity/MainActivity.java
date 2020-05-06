package com.miui.antispam.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import b.b.a.a.a;
import b.b.a.d.b.l;
import b.b.a.d.b.y;
import b.b.a.e.c;
import b.b.a.e.i;
import b.b.a.e.n;
import b.b.a.e.q;
import com.miui.antispam.service.a.d;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.xiaomi.stat.MiStat;
import miui.app.ActionBar;
import miui.app.Activity;

public class MainActivity extends r {

    /* renamed from: d  reason: collision with root package name */
    public String f2555d = c.f;
    private d e;
    private ActionBar f;
    private NotificationManager g;

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity] */
    private void a(Intent intent) {
        if (intent != null) {
            int i = 1;
            if (this.f.getNavigationItemCount() > 1 && c.c(this)) {
                int intExtra = intent.getIntExtra("notification_intercept_content", 2);
                ActionBar actionBar = this.f;
                if (intExtra == 2) {
                    i = 0;
                }
                actionBar.setSelectedNavigationItem(i);
                c.b((Context) this, false);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r12v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    private void a(Bundle bundle) {
        FragmentTransaction fragmentTransaction;
        Fragment fragment;
        if (!c.f1416d.equals(this.f2555d) || this.f2612c) {
            if (!c.e.equals(this.f2555d)) {
                this.f.setFragmentViewPagerMode(this, getFragmentManager());
                if (!this.f2612c) {
                    ActionBar actionBar = this.f;
                    actionBar.addFragmentTab("msg", actionBar.newTab().setText(R.string.tab_sms), y.class, (Bundle) null, true);
                }
                ActionBar actionBar2 = this.f;
                actionBar2.addFragmentTab("call", actionBar2.newTab().setText(R.string.tab_call), l.class, (Bundle) null, true);
                return;
            } else if (bundle == null) {
                fragmentTransaction = getFragmentManager().beginTransaction();
                fragment = new l();
            } else {
                return;
            }
        } else if (bundle == null) {
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragment = new y();
        } else {
            return;
        }
        fragmentTransaction.replace(16908290, fragment).commit();
    }

    /* access modifiers changed from: private */
    public void c() {
        this.g.cancel(798);
        this.g.cancel(797);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0031  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x002e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void d() {
        /*
            r2 = this;
            java.lang.String r0 = b.b.a.e.c.f1416d
            java.lang.String r1 = r2.f2555d
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0013
            miui.app.ActionBar r0 = r2.f
            r1 = 2131755089(0x7f100051, float:1.9141047E38)
        L_0x000f:
            r0.setTitle(r1)
            goto L_0x0023
        L_0x0013:
            java.lang.String r0 = b.b.a.e.c.e
            java.lang.String r1 = r2.f2555d
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0023
            miui.app.ActionBar r0 = r2.f
            r1 = 2131755088(0x7f100050, float:1.9141045E38)
            goto L_0x000f
        L_0x0023:
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r2)
            boolean r1 = r2.b()
            if (r1 == 0) goto L_0x0031
            int r1 = miui.R.drawable.icon_settings_dark
            goto L_0x0033
        L_0x0031:
            int r1 = miui.R.drawable.icon_settings_light
        L_0x0033:
            r0.setBackgroundResource(r1)
            r1 = 2131755105(0x7f100061, float:1.914108E38)
            java.lang.String r1 = r2.getString(r1)
            r0.setContentDescription(r1)
            com.miui.antispam.ui.activity.L r1 = new com.miui.antispam.ui.activity.L
            r1.<init>(r2)
            r0.setOnClickListener(r1)
            miui.app.ActionBar r1 = r2.f
            r1.setEndView(r0)
            boolean r1 = b.b.a.e.q.b()
            if (r1 != 0) goto L_0x0057
            r1 = 4
            r0.setVisibility(r1)
        L_0x0057:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.ui.activity.MainActivity.d():void");
    }

    /* access modifiers changed from: private */
    public void e() {
        com.miui.antispam.db.d.e(0);
        com.miui.antispam.db.d.d(0);
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity, com.miui.antispam.ui.activity.r, miui.app.Activity, android.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        String str;
        if (r.f2610a) {
            setTheme(2131821033);
        }
        super.onCreate(bundle);
        k.a((Activity) this);
        Intent intent = getIntent();
        boolean z = false;
        if (intent != null) {
            String stringExtra = intent.getStringExtra(c.f1415c);
            if (stringExtra != null) {
                this.f2555d = stringExtra;
            }
            boolean booleanExtra = intent.getBooleanExtra("is_from_intercept_notification", false);
            if (booleanExtra) {
                a.a("antispam_noti_action", intent.getIntExtra("notification_block_type", -1) == 17 ? "overseas" : "mainland", MiStat.Event.CLICK);
                int intExtra = intent.getIntExtra("notification_intercept_content", 0);
                if (intExtra > 0) {
                    a.a("antispam_notification", intExtra == 2 ? "sms" : "call", MiStat.Event.CLICK);
                }
            }
            z = booleanExtra;
        }
        if (z) {
            str = "notification";
        } else {
            str = n.a((android.app.Activity) this);
            if (TextUtils.isEmpty(str)) {
                str = "other";
            }
        }
        a.c(str);
        this.f = getActionBar();
        this.e = new d(this);
        this.g = (NotificationManager) getSystemService("notification");
        d();
        a(bundle);
        a(intent);
        if (!i.a((Context) this).c()) {
            i.a((Context) this).d();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        MainActivity.super.onDestroy();
        b.b.o.g.d.a("MainActivity", (Object) (InputMethodManager) getApplicationContext().getSystemService("input_method"), "windowDismissed", (Class<?>[]) new Class[]{IBinder.class}, getWindow().getDecorView().getWindowToken());
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        MainActivity.super.onNewIntent(intent);
        a(intent);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.MainActivity, com.miui.antispam.ui.activity.r] */
    public void onPause() {
        super.onPause();
        c.b((Context) this, false);
    }

    public void onResume() {
        super.onResume();
        if (q.b()) {
            b.b.c.j.d.a(new M(this));
            this.e.a();
        }
    }
}
