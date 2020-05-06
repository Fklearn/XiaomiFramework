package com.miui.permcenter.permissions;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.securitycenter.R;
import com.miui.securitycenter.i;

public class ValuePreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private CharSequence f6248a;

    /* renamed from: b  reason: collision with root package name */
    private int f6249b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f6250c = false;

    /* renamed from: d  reason: collision with root package name */
    private Drawable f6251d;

    public ValuePreference(Context context) {
        super(context);
        a(context, (AttributeSet) null);
    }

    public ValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a(context, attributeSet);
    }

    private void a(Context context, AttributeSet attributeSet) {
        setLayoutResource(R.layout.preference_value);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.ValuePreference);
            boolean z = false;
            TypedValue peekValue = obtainStyledAttributes.peekValue(0);
            if (peekValue != null) {
                if (peekValue.type == 18 && peekValue.data != 0) {
                    z = true;
                }
                this.f6250c = z;
            }
            obtainStyledAttributes.recycle();
        }
        if (this.f6250c && getIntent() == null && getFragment() == null && getOnPreferenceClickListener() == null) {
            setIntent(new Intent("com.android.settings.TEST_ARROW"));
        }
    }

    public CharSequence a() {
        return this.f6248a;
    }

    public void a(String str) {
        if (!TextUtils.equals(str, this.f6248a)) {
            this.f6249b = 0;
            this.f6248a = str;
            notifyChanged();
        }
    }

    public void a(boolean z) {
        this.f6250c = z;
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        TextView textView = (TextView) a2.b((int) R.id.value_right);
        if (textView != null) {
            CharSequence a3 = a();
            if (!TextUtils.isEmpty(a3)) {
                textView.setText(a3);
                textView.setVisibility(0);
            } else {
                textView.setVisibility(8);
            }
        }
        ImageView imageView = (ImageView) a2.b((int) R.id.arrow_right);
        if (imageView != null) {
            imageView.setVisibility(this.f6250c ? 0 : 8);
            if (this.f6250c) {
                imageView.setVisibility(0);
                Drawable drawable = this.f6251d;
                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
                    return;
                }
                return;
            }
            imageView.setVisibility(8);
        }
    }

    public void performClick() {
        if (getIntent() != null && getIntent().resolveActivityInfo(getContext().getPackageManager(), 65536) == null) {
            setIntent((Intent) null);
        }
        super.performClick();
    }
}
