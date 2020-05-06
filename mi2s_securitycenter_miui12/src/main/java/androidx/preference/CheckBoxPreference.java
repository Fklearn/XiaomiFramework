package androidx.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.h;

public class CheckBoxPreference extends TwoStatePreference {
    private final a mListener;

    private class a implements CompoundButton.OnCheckedChangeListener {
        a() {
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            if (!CheckBoxPreference.this.callChangeListener(Boolean.valueOf(z))) {
                compoundButton.setChecked(!z);
            } else {
                CheckBoxPreference.this.setChecked(z);
            }
        }
    }

    public CheckBoxPreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, h.a(context, B.checkBoxPreferenceStyle, 16842895));
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public CheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mListener = new a();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, I.CheckBoxPreference, i, i2);
        b(h.b(obtainStyledAttributes, I.CheckBoxPreference_summaryOn, I.CheckBoxPreference_android_summaryOn));
        a((CharSequence) h.b(obtainStyledAttributes, I.CheckBoxPreference_summaryOff, I.CheckBoxPreference_android_summaryOff));
        a(h.a(obtainStyledAttributes, I.CheckBoxPreference_disableDependentsState, I.CheckBoxPreference_android_disableDependentsState, false));
        obtainStyledAttributes.recycle();
    }

    private void b(View view) {
        boolean z = view instanceof CompoundButton;
        if (z) {
            ((CompoundButton) view).setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
        }
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(this.f1029a);
        }
        if (z) {
            ((CompoundButton) view).setOnCheckedChangeListener(this.mListener);
        }
    }

    private void c(View view) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled()) {
            b(view.findViewById(16908289));
            a(view.findViewById(16908304));
        }
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        b(a2.b(16908289));
        a(a2);
    }

    /* access modifiers changed from: protected */
    @RestrictTo({RestrictTo.a.LIBRARY})
    public void performClick(View view) {
        super.performClick(view);
        c(view);
    }
}
