package com.miui.applicationlock.widget;

import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.miui.applicationlock.b.b;
import com.miui.applicationlock.c.p;
import com.miui.applicationlock.widget.LockPatternView;
import com.miui.applicationlock.widget.MiuiNumericInputView;
import com.miui.securitycenter.R;
import miui.security.SecurityManager;

public class x extends C0308a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public boolean f3459a;

    /* renamed from: b  reason: collision with root package name */
    private Vibrator f3460b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public p f3461c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public MiuiNumericInputView f3462d;
    /* access modifiers changed from: private */
    public LinearLayout e;
    /* access modifiers changed from: private */
    public StringBuilder f = new StringBuilder();
    private boolean g;
    /* access modifiers changed from: private */
    public NumberPasswordEditText h;
    private SecurityManager i;
    /* access modifiers changed from: private */
    public boolean j;
    private final MiuiNumericInputView.b k = new t(this);

    public x(Context context, boolean z) {
        super(context, (AttributeSet) null);
        this.g = z;
        this.f3460b = (Vibrator) context.getSystemService("vibrator");
        this.i = (SecurityManager) context.getSystemService("security");
        l();
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        if (this.g) {
            this.f3461c.a(str);
            i();
            return;
        }
        setPasswordEntryInputEnabled(false);
        if (this.i.checkAccessControlPassword("numeric", str)) {
            this.f3461c.b();
        } else {
            k();
        }
        setPasswordEntryInputEnabled(true);
    }

    /* access modifiers changed from: private */
    public void i() {
        if (!this.g) {
            StringBuilder sb = this.f;
            sb.delete(0, sb.length());
            for (int i2 = 0; i2 < this.e.getChildCount(); i2++) {
                ((ImageView) this.e.getChildAt(i2)).setImageResource(this.f3459a ? R.drawable.numeric_dot_empty_light : R.drawable.numeric_dot_empty);
            }
        }
    }

    /* access modifiers changed from: private */
    public void j() {
        if (!TextUtils.isEmpty(this.h.getText().toString())) {
            a(this.h.getText().toString());
        }
    }

    private void k() {
        this.f3461c.a();
        this.f3460b.vibrate(150);
        TranslateAnimation translateAnimation = new TranslateAnimation(1, -0.1f, 1, 0.1f, 1, 0.0f, 1, 0.0f);
        translateAnimation.setDuration(30);
        translateAnimation.setRepeatCount(3);
        translateAnimation.setRepeatMode(2);
        translateAnimation.setAnimationListener(new w(this));
        this.e.startAnimation(translateAnimation);
        this.f3462d.setEnabled(false);
    }

    private void l() {
        setOrientation(1);
        setClipChildren(false);
        if (this.g) {
            setGravity(1);
            View.inflate(getContext(), R.layout.applock_numeric_password_securitycenter, this);
            this.h = (NumberPasswordEditText) findViewById(R.id.password_entry);
            this.h.requestFocus();
            this.h.setOnEditorActionListener(new u(this));
            this.h.addTextChangedListener(new v(this));
        } else {
            setGravity(81);
            View.inflate(getContext(), R.layout.applock_number_password, this);
            this.f3462d = (MiuiNumericInputView) findViewById(R.id.numeric_inputview);
            this.e = (LinearLayout) findViewById(R.id.password_encrypt_dots);
            this.f3462d.setNumericInputListener(this.k);
            this.e.requestFocus();
        }
        setFocusableInTouchMode(true);
    }

    public void a() {
    }

    public void a(Context context, b bVar) {
    }

    public void b() {
        if (!this.g) {
            this.f3462d.setEnabled(false);
        }
    }

    public boolean c() {
        return this.f3462d.isEnabled();
    }

    public void d() {
        if (this.g) {
            this.h.setText("");
            return;
        }
        StringBuilder sb = this.f;
        sb.delete(0, sb.length());
        for (int i2 = 0; i2 < this.e.getChildCount(); i2++) {
            ((ImageView) this.e.getChildAt(i2)).setImageResource(this.f3459a ? R.drawable.numeric_dot_empty_light : R.drawable.numeric_dot_empty);
        }
    }

    public void e() {
        h();
        this.f3462d.a();
    }

    public EditText f() {
        this.h.requestFocus();
        return this.h;
    }

    public void g() {
        if (this.g) {
            this.h.setEnabled(true);
        } else {
            this.f3462d.setEnabled(true);
        }
    }

    /* access modifiers changed from: protected */
    public void h() {
        if (this.f3462d.getVisibility() == 0) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(400);
            this.f3462d.startAnimation(alphaAnimation);
        }
    }

    public void setAppPage(boolean z) {
    }

    public void setApplockUnlockCallback(p pVar) {
        if (pVar != null) {
            this.f3461c = pVar;
        }
    }

    public void setDisplayMode(LockPatternView.b bVar) {
    }

    public void setLightMode(boolean z) {
        this.f3459a = z;
        if (!this.g) {
            if (this.f3459a) {
                for (int i2 = 0; i2 < this.e.getChildCount(); i2++) {
                    ((ImageView) this.e.getChildAt(i2)).setImageResource(R.drawable.numeric_dot_empty_light);
                }
            }
            this.f3462d.setLightMode(z);
        }
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryInputEnabled(boolean z) {
        this.f3462d.setEnabled(z);
    }
}
