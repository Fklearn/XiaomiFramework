package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.h;

public class SwitchPreferenceCompat extends TwoStatePreference {
    private CharSequence f;
    private CharSequence g;
    private final a mListener;

    private class a implements CompoundButton.OnCheckedChangeListener {
        a() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (!SwitchPreferenceCompat.this.callChangeListener(Boolean.valueOf(z))) {
                compoundButton.setChecked(!z);
            } else {
                SwitchPreferenceCompat.this.setChecked(z);
            }
        }
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, B.switchPreferenceCompatStyle);
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mListener = new a();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.SwitchPreferenceCompat, i, i2);
        b(h.b(obtainStyledAttributes, I.SwitchPreferenceCompat_summaryOn, I.SwitchPreferenceCompat_android_summaryOn));
        a((CharSequence) h.b(obtainStyledAttributes, I.SwitchPreferenceCompat_summaryOff, I.SwitchPreferenceCompat_android_summaryOff));
        d(h.b(obtainStyledAttributes, I.SwitchPreferenceCompat_switchTextOn, I.SwitchPreferenceCompat_android_switchTextOn));
        c((CharSequence) h.b(obtainStyledAttributes, I.SwitchPreferenceCompat_switchTextOff, I.SwitchPreferenceCompat_android_switchTextOff));
        a(h.a(obtainStyledAttributes, I.SwitchPreferenceCompat_disableDependentsState, I.SwitchPreferenceCompat_android_disableDependentsState, false));
        obtainStyledAttributes.recycle();
    }

    private void b(View view) {
        boolean z = view instanceof SwitchCompat;
        if (z) {
            ((SwitchCompat) view).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(this.f1029a);
        }
        if (z) {
            SwitchCompat switchCompat = (SwitchCompat) view;
            switchCompat.setTextOn(this.f);
            switchCompat.setTextOff(this.g);
            switchCompat.setOnCheckedChangeListener(this.mListener);
        }
    }

    private void c(View view) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
            b(view.findViewById(E.switchWidget));
            a(view.findViewById(16908304));
        }
    }

    public void c(CharSequence charSequence) {
        this.g = charSequence;
        notifyChanged();
    }

    public void d(CharSequence charSequence) {
        this.f = charSequence;
        notifyChanged();
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        b(a2.b(E.switchWidget));
        a(a2);
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY})
    public void performClick(View view) {
        super.performClick(view);
        c(view);
    }
}
