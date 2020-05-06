package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import b.b.a.e.c;
import b.b.a.e.n;
import b.b.c.j.v;
import b.b.o.a.a;
import b.b.o.g.d;
import com.miui.securitycenter.R;

public class MarkNumGuideActivity extends r {

    /* renamed from: d  reason: collision with root package name */
    private static final String f2556d = "MarkNumGuideActivity";

    private void c() {
        if (Build.VERSION.SDK_INT > 21) {
            try {
                Window window = getWindow();
                window.addFlags(Integer.MIN_VALUE);
                window.getDecorView().setSystemUiVisibility(768);
                window.getClass().getMethod("setNavigationBarColor", new Class[]{Integer.TYPE}).invoke(window, new Object[]{0});
            } catch (Exception e) {
                Log.e(f2556d, "set virtual keys exception", e);
            }
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.content.Context, com.miui.antispam.ui.activity.MarkNumGuideActivity, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        c();
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra("mark_guide_yellowpage_cid", 0);
        int intExtra2 = intent.getIntExtra("mark_guide_type", 0);
        c.b((Context) this, c.b(intExtra), true);
        if (intExtra2 == 3) {
            String string = getString(intExtra == 1 ? R.string.mark_number_fraud : intExtra == 2 ? R.string.mark_number_agent : intExtra == 3 ? R.string.mark_number_sell : R.string.mark_number_harass);
            new AlertDialog.Builder(this).setCancelable(false).setTitle(R.string.mark_number_setting_guide_title).setMessage(getString(R.string.mark_number_setting_guide_content3, new Object[]{string, string})).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.mark_number_setting_guide_forward, new O(this)).setOnDismissListener(new N(this)).show();
            return;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        v.a(notificationManager, "com.miui.antispam", n.f1454a, 2);
        Notification build = v.a((Context) this, "com.miui.antispam").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.stat_notify_firewall_hit).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MarkNumberBlockActivity.class), 134217728)).setTicker(getString(R.string.mark_number_setting_guide_content1)).setContentTitle(getString(R.string.mark_number_setting_guide_content1)).setContentText(getString(R.string.mark_number_setting_guide_content2)).build();
        build.flags |= 16;
        Object a2 = d.a(f2556d, (Object) build, "extraNotification");
        d.a(f2556d, a2, "setEnableFloat", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
        d.a(f2556d, a2, "setEnableKeyguard", (Class<?>[]) new Class[]{Boolean.TYPE}, false);
        a.a(build, true);
        notificationManager.notify(799, build);
        finish();
    }
}
