package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.h;

public class SwitchPreference extends TwoStatePreference {
    private CharSequence f;
    private CharSequence g;
    private final a mListener;

    private class a implements CompoundButton.OnCheckedChangeListener {
        a() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (!SwitchPreference.this.callChangeListener(Boolean.valueOf(z))) {
                compoundButton.setChecked(!z);
            } else {
                SwitchPreference.this.setChecked(z);
            }
        }
    }

    public SwitchPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, h.a(context, B.switchPreferenceStyle, 16843629));
    }

    public SwitchPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SwitchPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mListener = new a();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.SwitchPreference, i, i2);
        b(h.b(obtainStyledAttributes, I.SwitchPreference_summaryOn, I.SwitchPreference_android_summaryOn));
        a((CharSequence) h.b(obtainStyledAttributes, I.SwitchPreference_summaryOff, I.SwitchPreference_android_summaryOff));
        d(h.b(obtainStyledAttributes, I.SwitchPreference_switchTextOn, I.SwitchPreference_android_switchTextOn));
        c((CharSequence) h.b(obtainStyledAttributes, I.SwitchPreference_switchTextOff, I.SwitchPreference_android_switchTextOff));
        a(h.a(obtainStyledAttributes, I.SwitchPreference_disableDependentsState, I.SwitchPreference_android_disableDependentsState, false));
        obtainStyledAttributes.recycle();
    }

    private void b(View view) {
        boolean z = view instanceof Switch;
        if (z) {
            ((Switch) view).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(this.f1029a);
        }
        if (z) {
            Switch switchR = (Switch) view;
            switchR.setTextOn(this.f);
            switchR.setTextOff(this.g);
            switchR.setOnCheckedChangeListener(this.mListener);
        }
    }

    private void c(View view) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
            b(view.findViewById(16908352));
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
        b(a2.b(16908352));
        a(a2);
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY})
    public void performClick(View view) {
        super.performClick(view);
        c(view);
    }
}
