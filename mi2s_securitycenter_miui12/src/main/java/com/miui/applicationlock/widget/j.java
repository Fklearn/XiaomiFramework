package com.miui.applicationlock.widget;

import android.content.Context;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import b.b.o.g.e;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.miui.applicationlock.b.b;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.p;
import com.miui.applicationlock.widget.LockPatternView;
import com.miui.securitycenter.R;
import miui.security.SecurityManager;
import miui.view.MiuiKeyBoardView;

public class j extends C0308a {

    /* renamed from: a  reason: collision with root package name */
    private final Vibrator f3440a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public p f3441b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public EditText f3442c;

    /* renamed from: d  reason: collision with root package name */
    private View f3443d;
    private boolean e;
    /* access modifiers changed from: private */
    public boolean f;
    private SecurityManager g;
    private Context h;
    /* access modifiers changed from: private */
    public boolean i;

    public j(Context context, boolean z) {
        super(context);
        this.h = context;
        this.e = z;
        this.f3440a = (Vibrator) context.getSystemService("vibrator");
        this.g = (SecurityManager) context.getSystemService("security");
        l();
    }

    private void a(String str) {
        if (this.e) {
            this.f3441b.a(str);
        } else if (str.length() >= 3) {
            setPasswordEntryInputEnabled(false);
            if (this.g.checkAccessControlPassword("mixed", str)) {
                this.f3441b.b();
            } else {
                j();
            }
            setPasswordEntryInputEnabled(true);
            this.f3443d.setEnabled(true);
            this.f3442c.setEnabled(true);
        }
    }

    /* access modifiers changed from: private */
    public void i() {
        if (!TextUtils.isEmpty(this.f3442c.getText().toString())) {
            a(this.f3442c.getText().toString());
        }
    }

    private void j() {
        this.f3441b.a();
        this.f3440a.vibrate(150);
        this.f3443d.setEnabled(false);
        this.f3442c.setText("");
    }

    private void k() {
        this.f = false;
        ImageView imageView = (ImageView) findViewById(R.id.show_password_img);
        imageView.setOnClickListener(new i(this, imageView));
        if (o.v()) {
            imageView.setRotation(180.0f);
        }
    }

    private void l() {
        setOrientation(1);
        if (this.e) {
            View.inflate(this.h, R.layout.applock_mixed_password_securitycenter, this);
            k();
        } else {
            View.inflate(this.h, R.layout.applock_mixed_password, this);
        }
        this.f3442c = (EditText) findViewById(R.id.miui_mixed_password_input_field);
        this.f3442c.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        this.f3442c.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        this.f3442c.requestFocus();
        if (this.e) {
            this.f3442c.addTextChangedListener(new g(this));
        }
        this.f3443d = (MiuiKeyBoardView) findViewById(R.id.mixed_password_keyboard_view);
        try {
            e.a((Object) this.f3443d, "addKeyboardListener", (Class<?>[]) new Class[]{MiuiKeyBoardView.OnKeyboardActionListener.class}, new h(this));
        } catch (Exception e2) {
            Log.e("MixedPasswordUnlock", "addKeyboardListener exception:", e2);
        }
        setFocusableInTouchMode(true);
    }

    public void a() {
    }

    public void a(Context context, b bVar) {
    }

    public void b() {
        this.f3443d.setEnabled(false);
    }

    public boolean c() {
        return this.f3443d.isEnabled();
    }

    public void d() {
        this.f3442c.setText("");
    }

    public void e() {
        h();
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 2.0f, 1.0f, 1, 0.0f, 1, 0.0f);
        TranslateAnimation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(150);
        this.f3443d.startAnimation(animationSet);
    }

    public EditText f() {
        return null;
    }

    public void g() {
        this.f3443d.setEnabled(true);
    }

    /* access modifiers changed from: protected */
    public void h() {
        if (this.f3443d.getVisibility() == 0) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(400);
            this.f3443d.startAnimation(alphaAnimation);
        }
    }

    public void setAppPage(boolean z) {
    }

    public void setApplockUnlockCallback(p pVar) {
        if (pVar != null) {
            this.f3441b = pVar;
        }
    }

    public void setDisplayMode(LockPatternView.b bVar) {
    }

    public void setLightMode(boolean z) {
        if (!z) {
            this.f3442c.setTextColor(getResources().getColor(R.color.unlock_text_dark));
            this.f3442c.setHintTextColor(getResources().getColor(R.color.applock_mix_edit_hint_color));
        }
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryInputEnabled(boolean z) {
        this.f3442c.setEnabled(z);
    }
}
