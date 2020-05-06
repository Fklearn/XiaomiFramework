package com.miui.superpower.statusbar.icon;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import com.miui.superpower.b.j;
import com.miui.superpower.statusbar.h;
import com.xiaomi.stat.MiStat;
import java.util.Locale;

public class BatteryView extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f8178a;

    /* renamed from: b  reason: collision with root package name */
    private a f8179b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f8180c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f8181d;
    private ImageView e;
    private Drawable f;
    private Drawable g;
    private Drawable h;
    private ClipDrawable i;
    private Boolean j;
    private int k;

    private class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction("android.intent.action.BATTERY_CHANGED");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.BATTERY_CHANGED")) {
                boolean z = false;
                int intExtra = intent.getIntExtra("status", 0);
                int intExtra2 = intent.getIntExtra(MiStat.Param.LEVEL, 0);
                int intExtra3 = intent.getIntExtra("scale", 100);
                BatteryView batteryView = BatteryView.this;
                if (intExtra == 2) {
                    z = true;
                }
                batteryView.a(intExtra2, intExtra3, z);
            }
        }
    }

    public BatteryView(Context context) {
        this(context, (AttributeSet) null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        setOrientation(0);
        setGravity(16);
        this.f8178a = context;
        this.f8179b = new a(context);
        View.inflate(context, R.layout.superpower_statusbar_batteryview, this);
        this.f8181d = (ImageView) findViewById(R.id.battery_progress);
        this.e = (ImageView) findViewById(R.id.battery_background);
        this.f8180c = (TextView) findViewById(R.id.battery_text);
        this.f8180c.setTypeface(j.a(context));
        a();
        this.f8181d.setImageDrawable(this.g);
        this.e.setBackground(this.h);
    }

    private void a() {
        this.f = h.b(this.f8178a, "battery_meter_progress_charging", R.drawable.superpower_battery_meter_progress_charging);
        this.g = h.b(this.f8178a, "battery_meter_progress_power_save", R.drawable.superpower_battery_meter_progress_save);
        this.h = h.b(this.f8178a, "battery_meter_bg", R.drawable.superpower_battery_meter_bg);
        this.k = h.a(this.f8178a, "battery_meter_progress_gravity_start", R.bool.battery_gravity_start).booleanValue() ? 8388611 : 8388613;
        if (this.k == 8388611) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, -2);
            layoutParams.setMargins(this.f8178a.getResources().getDimensionPixelSize(R.dimen.superpower_battery_margin_left), 0, 0, 0);
            layoutParams.gravity = 17;
            this.f8181d.setLayoutParams(layoutParams);
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2, int i3, boolean z) {
        if (i3 == 0) {
            i3 = 100;
        }
        int i4 = (i2 * 100) / i3;
        TextView textView = this.f8180c;
        if (textView != null) {
            textView.setText(String.format(Locale.getDefault(), "%d%%", new Object[]{Integer.valueOf(i4)}));
        }
        Boolean bool = this.j;
        if (bool == null || bool.booleanValue() != z) {
            this.j = Boolean.valueOf(z);
            this.i = z ? new ClipDrawable(this.f, this.k, 1) : new ClipDrawable(this.g, this.k, 1);
            this.f8181d.setImageDrawable(this.i);
        }
        ClipDrawable clipDrawable = this.i;
        if (clipDrawable != null) {
            clipDrawable.setLevel(i4 * 100);
            this.i.invalidateSelf();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Intent a2 = this.f8179b.a();
        if (a2 != null) {
            boolean z = false;
            int intExtra = a2.getIntExtra(MiStat.Param.LEVEL, 0);
            int intExtra2 = a2.getIntExtra("scale", 100);
            if (a2.getIntExtra("plugged", -1) != 0) {
                z = true;
            }
            a(intExtra, intExtra2, z);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.f8179b.b();
    }
}
