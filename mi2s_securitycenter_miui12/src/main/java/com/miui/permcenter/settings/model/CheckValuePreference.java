package com.miui.permcenter.settings.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.A;
import androidx.preference.Preference;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class CheckValuePreference extends Preference {

    /* renamed from: a  reason: collision with root package name */
    private SlidingButton f6530a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f6531b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public a f6532c;

    public interface a {
        void a(boolean z);
    }

    public CheckValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void onBindViewHolder(A a2) {
        super.onBindViewHolder(a2);
        View view = a2.itemView;
        view.setPadding(80, 20, 80, 20);
        ((TextView) view.findViewById(R.id.title)).setText(getTitle());
        this.f6530a = view.findViewById(R.id.checkbox);
        this.f6530a.setChecked(this.f6531b);
        this.f6530a.setOnCheckedChangeListener(new a(this));
    }
}
