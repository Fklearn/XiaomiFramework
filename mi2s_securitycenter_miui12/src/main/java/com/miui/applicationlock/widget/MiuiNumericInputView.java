package com.miui.applicationlock.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;

public class MiuiNumericInputView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    public a[] f3409a = new a[12];

    /* renamed from: b  reason: collision with root package name */
    public LinearLayout[] f3410b = new LinearLayout[10];
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f3411c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f3412d;
    /* access modifiers changed from: private */
    public b e;
    private c f;
    private c g;
    private c h;
    private c i;
    /* access modifiers changed from: private */
    public View j;
    /* access modifiers changed from: private */
    public Context k;

    public class a extends FrameLayout {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public final int f3413a;

        public a(Context context, int i) {
            super(context);
            this.f3413a = i;
            if (this.f3413a != 11) {
                setOnClickListener(new f(this, MiuiNumericInputView.this));
            }
            addView(a(i), new FrameLayout.LayoutParams(-1, -1));
        }

        private View a(int i) {
            if (i == -1) {
                return new View(MiuiNumericInputView.this.k);
            }
            if (i == 11) {
                TextView textView = new TextView(MiuiNumericInputView.this.k);
                textView.setText(R.string.forget_password_button_text_normal);
                textView.setGravity(17);
                textView.setTextColor(getResources().getColorStateList(R.color.applock_numeric_keyboard_cancel_all_text_color_light));
                textView.setTextSize(0, (float) getResources().getDimensionPixelSize(R.dimen.applock_numeric_keyboard_cancel_all_text_size));
                View unused = MiuiNumericInputView.this.j = textView;
                return textView;
            } else if (i == 10) {
                TextView textView2 = new TextView(MiuiNumericInputView.this.k);
                textView2.setText(R.string.delete_input);
                textView2.setGravity(17);
                textView2.setTextColor(getResources().getColorStateList(R.color.applock_numeric_keyboard_cancel_all_text_color_light));
                textView2.setTextSize(0, (float) getResources().getDimensionPixelSize(R.dimen.applock_numeric_keyboard_cancel_all_text_size));
                return textView2;
            } else {
                LinearLayout linearLayout = new LinearLayout(MiuiNumericInputView.this.k);
                linearLayout.setOrientation(1);
                linearLayout.setGravity(17);
                linearLayout.setBackgroundResource(R.drawable.miui_numeric_keyboard_button_light);
                MiuiNumericInputView miuiNumericInputView = MiuiNumericInputView.this;
                miuiNumericInputView.f3410b[miuiNumericInputView.f3412d] = linearLayout;
                MiuiNumericInputView.e(MiuiNumericInputView.this);
                TextView textView3 = new TextView(MiuiNumericInputView.this.k);
                textView3.setText(Integer.toString(i));
                textView3.setTextSize(0, getResources().getDimension(R.dimen.applock_numeric_keyboard_number_text_size));
                textView3.setTextColor(getResources().getColor(R.color.unlock_text_light));
                textView3.setTypeface(Typeface.create("miui-light", 0));
                textView3.setLineSpacing(0.0f, 1.0f);
                textView3.setIncludeFontPadding(false);
                linearLayout.addView(textView3, new FrameLayout.LayoutParams(-2, -2));
                return linearLayout;
            }
        }
    }

    interface b {
        void a(int i);
    }

    private class c extends LinearLayout {
        public c(Context context) {
            super(context);
            setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            setLayoutDirection(0);
            setGravity(48);
        }

        /* access modifiers changed from: private */
        public void a(int i, int i2, int i3) {
            a aVar = new a(getContext(), i);
            a aVar2 = new a(getContext(), i2);
            a aVar3 = new a(getContext(), i3);
            MiuiNumericInputView miuiNumericInputView = MiuiNumericInputView.this;
            miuiNumericInputView.f3409a[miuiNumericInputView.f3411c] = aVar;
            MiuiNumericInputView miuiNumericInputView2 = MiuiNumericInputView.this;
            miuiNumericInputView2.f3409a[miuiNumericInputView2.f3411c + 1] = aVar2;
            MiuiNumericInputView miuiNumericInputView3 = MiuiNumericInputView.this;
            miuiNumericInputView3.f3409a[miuiNumericInputView3.f3411c + 2] = aVar3;
            MiuiNumericInputView miuiNumericInputView4 = MiuiNumericInputView.this;
            int unused = miuiNumericInputView4.f3411c = miuiNumericInputView4.f3411c + 3;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, getResources().getDimensionPixelSize(R.dimen.applock_numeric_keyboard_item_height), 1.0f);
            aVar.setLayoutParams(layoutParams);
            aVar2.setLayoutParams(layoutParams);
            aVar3.setLayoutParams(layoutParams);
            addView(aVar);
            addView(aVar2);
            addView(aVar3);
        }

        public void setEnabled(boolean z) {
            super.setEnabled(z);
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setEnabled(z);
            }
        }
    }

    public MiuiNumericInputView(Context context) {
        super(context);
        this.k = context;
        a(this.k);
    }

    public MiuiNumericInputView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.k = context;
        a(this.k);
    }

    private Animation a(View view) {
        view.setVisibility(4);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(400);
        animationSet.setInterpolator(this.k, 17563656);
        animationSet.addAnimation(new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f));
        animationSet.addAnimation(new AlphaAnimation(0.0f, 1.0f));
        animationSet.setAnimationListener(new e(this, view));
        return animationSet;
    }

    private void a(Context context) {
        setOrientation(1);
        this.f = new c(context);
        this.f.a(1, 2, 3);
        this.g = new c(context);
        this.g.a(4, 5, 6);
        this.h = new c(context);
        this.h.a(7, 8, 9);
        this.i = new c(context);
        this.i.a(11, 0, 10);
        addView(this.f);
        addView(this.g);
        addView(this.h);
        addView(this.i);
    }

    static /* synthetic */ int e(MiuiNumericInputView miuiNumericInputView) {
        int i2 = miuiNumericInputView.f3412d;
        miuiNumericInputView.f3412d = i2 + 1;
        return i2;
    }

    public void a() {
        Animation a2 = a((View) this.f);
        a2.setStartOffset(50);
        this.f.startAnimation(a2);
        Animation a3 = a((View) this.g);
        a3.setStartOffset(100);
        this.g.startAnimation(a3);
        Animation a4 = a((View) this.h);
        a4.setStartOffset(150);
        this.h.startAnimation(a4);
        Animation a5 = a((View) this.i);
        a5.setStartOffset(200);
        this.i.startAnimation(a5);
    }

    public void a(boolean z) {
        int i2 = 0;
        while (true) {
            a[] aVarArr = this.f3409a;
            if (i2 < aVarArr.length) {
                if (!(i2 == 9 || i2 == 11)) {
                    ((LinearLayout) aVarArr[i2].getChildAt(0)).setBackgroundResource(z ? R.drawable.miui_numeric_keyboard_button_light_split : R.drawable.miui_numeric_keyboard_button_split);
                }
                i2++;
            } else {
                return;
            }
        }
    }

    public a[] getCell() {
        return this.f3409a;
    }

    public View getForgetPasswordView() {
        return this.j;
    }

    public void setCellHeight(int i2) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, i2, 1.0f);
        for (a layoutParams2 : this.f3409a) {
            layoutParams2.setLayoutParams(layoutParams);
        }
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).setEnabled(z);
        }
    }

    public void setLightMode(boolean z) {
        int i2 = 0;
        while (true) {
            a[] aVarArr = this.f3409a;
            if (i2 < aVarArr.length) {
                if (i2 == 9 || i2 == 11) {
                    ((TextView) this.f3409a[i2].getChildAt(0)).setTextColor(getResources().getColorStateList(z ? R.color.applock_numeric_keyboard_cancel_all_text_color_light : R.color.applock_numeric_keyboard_cancel_all_text_color));
                } else {
                    LinearLayout linearLayout = (LinearLayout) aVarArr[i2].getChildAt(0);
                    linearLayout.setBackgroundResource(z ? R.drawable.miui_numeric_keyboard_button_light : R.drawable.miui_numeric_keyboard_button);
                    ((TextView) linearLayout.getChildAt(0)).setTextColor(getResources().getColor(z ? R.color.unlock_text_light : R.color.applock_numeric_keyboard_number_text_color));
                }
                i2++;
            } else {
                return;
            }
        }
    }

    public void setNumericInputListener(b bVar) {
        this.e = bVar;
    }
}
