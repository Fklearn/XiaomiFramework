package com.miui.applicationlock.widget;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.applicationlock.b.b;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.p;
import com.miui.applicationlock.widget.LockPatternView;
import com.miui.securitycenter.R;
import miui.security.SecurityManager;
import miui.securitycenter.applicationlock.MiuiLockPatternUtilsWrapper;

public class A extends C0308a {

    /* renamed from: a  reason: collision with root package name */
    private MiuiLockPatternUtilsWrapper f3390a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public p f3391b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public LockPatternView f3392c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Runnable f3393d;
    /* access modifiers changed from: private */
    public boolean e;
    private SecurityManager f;
    private Context g;

    public A(Context context, boolean z) {
        super(context);
        this.g = context;
        this.e = z;
        this.f3390a = new MiuiLockPatternUtilsWrapper(context);
        this.f = (SecurityManager) context.getSystemService("security");
        h();
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        if (str.length() < 3) {
            return;
        }
        if (this.f.checkAccessControlPassword("pattern", str)) {
            this.f3391b.b();
        } else {
            this.f3391b.a();
        }
    }

    private void h() {
        int i;
        Context context;
        setOrientation(1);
        if (this.e) {
            context = this.g;
            i = R.layout.applock_pattern_password_securitycenter;
        } else {
            context = this.g;
            i = R.layout.applock_pattern_password;
        }
        View.inflate(context, i, this);
        this.f3392c = (LockPatternView) findViewById(R.id.lockPattern);
        this.f3392c.setTactileFeedbackEnabled(this.f3390a.isTactileFeedbackEnabled());
        this.f3392c.setInStealthMode(!o.i(getContext()));
        this.f3393d = new y(this);
        this.f3392c.setOnPatternListener(new z(this));
        setFocusableInTouchMode(true);
    }

    public void a() {
        this.f3392c.removeCallbacks(this.f3393d);
        this.f3392c.postDelayed(this.f3393d, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
    }

    public void a(Context context, b bVar) {
        this.f3392c.a(context, bVar);
    }

    public void b() {
        this.f3392c.b();
    }

    public boolean c() {
        return this.f3392c.d();
    }

    public void d() {
        this.f3392c.a();
    }

    public void e() {
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 2.0f, 1.0f, 1, 0.0f, 1, 0.0f);
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(150);
        this.f3392c.startAnimation(animationSet);
    }

    public EditText f() {
        return null;
    }

    public void g() {
        this.f3392c.c();
    }

    public void setAppPage(boolean z) {
        this.f3392c.setAppPage(z);
    }

    public void setApplockUnlockCallback(p pVar) {
        if (pVar != null) {
            this.f3391b = pVar;
        }
    }

    public void setDisplayMode(LockPatternView.b bVar) {
        this.f3392c.setDisplayMode(bVar);
    }

    public void setLightMode(boolean z) {
        this.f3392c.setLightMode(z);
    }
}
