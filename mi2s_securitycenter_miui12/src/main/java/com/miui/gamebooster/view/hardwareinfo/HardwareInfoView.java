package com.miui.gamebooster.view.hardwareinfo;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.viewPointwidget.b;
import com.miui.securitycenter.R;
import java.util.Locale;

public class HardwareInfoView extends RelativeLayout implements b, b {

    /* renamed from: a  reason: collision with root package name */
    private TextView f5279a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f5280b;

    /* renamed from: c  reason: collision with root package name */
    private View f5281c;

    /* renamed from: d  reason: collision with root package name */
    private View f5282d;
    private Context e;
    private d f;
    private Pair<Integer, Integer> g;
    private boolean h;

    public HardwareInfoView(Context context) {
        this(context, (AttributeSet) null);
    }

    public HardwareInfoView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HardwareInfoView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.g = null;
        this.h = false;
        a(context);
    }

    private void a(Context context) {
        this.e = context;
        View inflate = View.inflate(context, R.layout.hardware_info_home_page_layout, this);
        this.f5279a = (TextView) findViewById(R.id.cpuInfo);
        this.f5280b = (TextView) findViewById(R.id.gpuInfo);
        this.f5281c = findViewById(R.id.gpuInfoContainer);
        this.f5282d = findViewById(R.id.cpuInfoContainer);
        if (na.c()) {
            Utils.b(inflate.findViewById(R.id.startBandage), inflate.findViewById(R.id.endBandage));
        }
    }

    private void a(Pair<Integer, Integer> pair, boolean z) {
        if (z) {
            this.f5281c.setVisibility(8);
            Utils.b(this.f5281c);
            Utils.a(this.f5282d, (Runnable) new a(this));
        }
        if (pair != null) {
            a(((Integer) pair.first).intValue(), ((Integer) pair.second).intValue());
        } else {
            a(0, 0);
        }
    }

    public void a() {
        d dVar = this.f;
        if (dVar != null) {
            dVar.a();
        }
        this.f = new d(this, Long.valueOf(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS));
        this.h = this.f.c();
        a(this.g, this.h);
    }

    @UiThread
    public void a(int i, int i2) {
        Locale i3 = Utils.i();
        com.miui.gamebooster.globalgame.util.b.c(String.format(i3, "hardware::cpu: %s, gpu: %s", new Object[]{Integer.valueOf(i), Integer.valueOf(i2)}));
        this.f5279a.setText(String.format(i3, "%2d%%", new Object[]{Integer.valueOf(i)}));
        if (!this.h) {
            this.f5280b.setText(String.format(i3, "%2d%%", new Object[]{Integer.valueOf(i2)}));
        }
        this.g = new Pair<>(Integer.valueOf(i), Integer.valueOf(i2));
    }

    public void b() {
    }

    public /* synthetic */ void c() {
        ViewGroup.LayoutParams layoutParams = this.f5282d.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.setMargins(0, marginLayoutParams.topMargin, 0, marginLayoutParams.bottomMargin);
            this.f5282d.postInvalidate();
        }
    }

    public void onPause() {
        d dVar = this.f;
        if (dVar != null) {
            dVar.a();
        }
    }

    public void onStop() {
    }
}
