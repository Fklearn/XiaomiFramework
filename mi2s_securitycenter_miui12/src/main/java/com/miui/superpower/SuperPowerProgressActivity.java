package com.miui.superpower;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import com.miui.superpower.b.i;
import com.miui.superpower.b.j;
import miui.app.Activity;

public class SuperPowerProgressActivity extends Activity {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8045a = o.class.getSimpleName();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public ImageView f8046b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ImageView f8047c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ImageView f8048d;
    /* access modifiers changed from: private */
    public ImageView e;
    /* access modifiers changed from: private */
    public TextView f;
    /* access modifiers changed from: private */
    public TextView g;
    /* access modifiers changed from: private */
    public TextView h;
    /* access modifiers changed from: private */
    public TextView i;
    /* access modifiers changed from: private */
    public ImageView j;
    private PowerManager.WakeLock k;
    /* access modifiers changed from: private */
    public Handler l = new c(this);

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.superpower.SuperPowerProgressActivity, miui.app.Activity] */
    private void a() {
        this.f8046b = (ImageView) findViewById(R.id.iv_turnoff_items);
        this.f8047c = (ImageView) findViewById(R.id.iv_turnoff_process);
        this.f8048d = (ImageView) findViewById(R.id.iv_turndown_brightness);
        this.e = (ImageView) findViewById(R.id.iv_switch_home);
        this.f = (TextView) findViewById(R.id.txt_turnoff_items);
        this.g = (TextView) findViewById(R.id.txt_turnoff_process);
        this.h = (TextView) findViewById(R.id.txt_turndown_brightness);
        this.i = (TextView) findViewById(R.id.txt_switch_home);
        this.j = (ImageView) findViewById(R.id.iv_cicleview);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.superpower_progress_image_rotate);
        loadAnimation.setInterpolator(new LinearInterpolator());
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.superpower_progress_ring_rotate);
        loadAnimation2.setInterpolator(new LinearInterpolator());
        if (loadAnimation != null) {
            this.f8046b.startAnimation(loadAnimation);
            this.f8047c.startAnimation(loadAnimation);
            this.f8048d.startAnimation(loadAnimation);
            this.e.startAnimation(loadAnimation);
        }
        if (loadAnimation2 != null) {
            this.j.startAnimation(loadAnimation2);
        }
    }

    public static void a(Context context) {
        Intent intent = new Intent(context, SuperPowerProgressActivity.class);
        intent.setFlags(268435456);
        intent.addFlags(8388608);
        context.startActivity(intent);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.superpower.SuperPowerProgressActivity, miui.app.Activity] */
    private void b() {
        TextView textView = (TextView) findViewById(R.id.tv_left_time);
        textView.setTypeface(j.a(this));
        textView.setText(i.b(this, o.c(this), 0));
    }

    private void c() {
        b();
        a();
    }

    public void finish() {
        SuperPowerProgressActivity.super.finish();
        this.l.removeCallbacksAndMessages((Object) null);
        overridePendingTransition(R.anim.superpower_enter, R.anim.superpower_exit);
    }

    public void onBackPressed() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        SuperPowerProgressActivity.super.onCreate(bundle);
        setContentView(R.layout.activity_superpower_progress);
        c();
        getWindow().getDecorView().setSystemUiVisibility(4866);
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new d(this, decorView));
        this.l.sendEmptyMessageDelayed(-1, 1250);
        this.k = ((PowerManager) getSystemService("power")).newWakeLock(1, "SuperPowerProgress");
        this.k.acquire();
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.superpower.SuperPowerProgressActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        SuperPowerProgressActivity.super.onStart();
        if (!o.m(this)) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        SuperPowerProgressActivity.super.onStop();
        PowerManager.WakeLock wakeLock = this.k;
        if (wakeLock != null) {
            wakeLock.release();
            this.k = null;
        }
    }
}
