package com.miui.powercenter.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.miui.securitycenter.R;

public class HistoryCheckGroup extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private int f7329a;

    /* renamed from: b  reason: collision with root package name */
    private a f7330b;

    /* renamed from: c  reason: collision with root package name */
    private ShadowButton f7331c;

    /* renamed from: d  reason: collision with root package name */
    private ShadowButton f7332d;
    private boolean e;

    public interface a {
        void a(int i);
    }

    public HistoryCheckGroup(Context context) {
        this(context, (AttributeSet) null);
    }

    public HistoryCheckGroup(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HistoryCheckGroup(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f7329a = 0;
        this.e = true;
        LayoutInflater.from(context).inflate(R.layout.pc_battery_statics_title_checkgroup, this);
        this.f7331c = (ShadowButton) findViewById(R.id.button_chart);
        this.f7332d = (ShadowButton) findViewById(R.id.button_histogram);
        this.f7331c.setOnClickListener(this);
        this.f7332d.setOnClickListener(this);
        this.f7331c.setImageResources(new int[]{R.drawable.pc_button_chart_enable, R.drawable.pc_button_chart});
        this.f7332d.setImageResources(new int[]{R.drawable.pc_button_histogram_enable, R.drawable.pc_button_histogram});
        a(false);
    }

    private void a(boolean z) {
        a aVar;
        if (this.f7329a == 0) {
            this.f7331c.setChecked(true);
            this.f7332d.setChecked(false);
        } else {
            this.f7331c.setChecked(false);
            this.f7332d.setChecked(true);
        }
        if (z && (aVar = this.f7330b) != null) {
            aVar.a(this.f7329a);
        }
    }

    public int getCurrentCheckItem() {
        return this.f7329a;
    }

    public void onClick(View view) {
        if (this.e) {
            if (view == this.f7331c && this.f7329a == 1) {
                this.f7329a = 0;
            } else if (view == this.f7332d && this.f7329a == 0) {
                this.f7329a = 1;
            } else {
                return;
            }
            a(true);
        }
    }

    public void setEnabled(boolean z) {
        this.e = z;
        this.f7331c.setEnabled(z);
        this.f7332d.setEnabled(z);
    }

    public void setOnCheckChangeListener(a aVar) {
        this.f7330b = aVar;
    }
}
