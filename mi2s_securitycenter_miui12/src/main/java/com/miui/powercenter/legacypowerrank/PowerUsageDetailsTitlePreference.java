package com.miui.powercenter.legacypowerrank;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.securitycenter.R;

public class PowerUsageDetailsTitlePreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private CharSequence f7085a;

    /* renamed from: b  reason: collision with root package name */
    private int f7086b;
    private Drawable mIcon;
    private CharSequence mSummary;
    private CharSequence mTitle;

    public PowerUsageDetailsTitlePreference(Context context) {
        this(context, (AttributeSet) null);
    }

    public PowerUsageDetailsTitlePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PowerUsageDetailsTitlePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTitle = getTitle();
        setLayoutResource(R.layout.pc_power_usage_details_title_pref);
    }

    public void a(int i) {
        if (this.f7086b != i) {
            this.f7086b = i;
            notifyChanged();
        }
    }

    public void a(CharSequence charSequence) {
        if ((charSequence == null && this.f7085a != null) || (charSequence != null && !charSequence.equals(this.f7085a))) {
            this.f7085a = charSequence;
            notifyChanged();
        }
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        ((ImageView) a2.b((int) R.id.icon)).setImageDrawable(this.mIcon);
        TextView textView = (TextView) a2.b((int) R.id.title);
        TextView textView2 = (TextView) a2.b((int) R.id.summary);
        TextView textView3 = (TextView) a2.b((int) R.id.content);
        if (TextUtils.isEmpty(this.mTitle)) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
        }
        if (TextUtils.isEmpty(this.mSummary)) {
            textView2.setVisibility(8);
        } else {
            textView2.setVisibility(0);
        }
        if (TextUtils.isEmpty(this.f7085a)) {
            textView3.setVisibility(8);
        } else {
            textView3.setVisibility(0);
        }
        textView.setText(this.mTitle);
        textView2.setText(this.mSummary);
        textView3.setText(this.f7085a);
        ((ProgressBar) a2.b((int) R.id.progress)).setProgress(this.f7086b);
    }

    public void setIcon(Drawable drawable) {
        if (this.mIcon != drawable) {
            this.mIcon = drawable;
            notifyChanged();
        }
    }

    public void setSummary(int i) {
        setSummary((CharSequence) getContext().getResources().getString(i));
    }

    public void setSummary(CharSequence charSequence) {
        if ((charSequence == null && this.mSummary != null) || (charSequence != null && !charSequence.equals(this.mSummary))) {
            this.mSummary = charSequence;
            notifyChanged();
        }
    }

    public void setTitle(int i) {
        setTitle((CharSequence) getContext().getResources().getString(i));
    }

    public void setTitle(CharSequence charSequence) {
        if ((charSequence == null && this.mTitle != null) || (charSequence != null && !charSequence.equals(this.mTitle))) {
            this.mTitle = charSequence;
            notifyChanged();
        }
    }
}
