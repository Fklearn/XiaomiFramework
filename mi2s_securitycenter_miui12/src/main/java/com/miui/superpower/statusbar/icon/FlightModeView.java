package com.miui.superpower.statusbar.icon;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import com.miui.superpower.statusbar.h;

public class FlightModeView extends ImageView {

    /* renamed from: a  reason: collision with root package name */
    private ContentResolver f8183a;

    /* renamed from: b  reason: collision with root package name */
    private a f8184b;

    public class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction("android.intent.action.AIRPLANE_MODE");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals("android.intent.action.AIRPLANE_MODE")) {
                FlightModeView.this.a();
            }
        }
    }

    public FlightModeView(Context context) {
        this(context, (AttributeSet) null);
    }

    public FlightModeView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlightModeView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Drawable b2 = h.b(context, "stat_sys_signal_flightmode", R.drawable.superpower_stat_sys_signal_flightmode);
        this.f8183a = context.getContentResolver();
        this.f8184b = new a(context);
        setImageDrawable(b2);
        a();
    }

    /* access modifiers changed from: private */
    public void a() {
        setVisibility(k.a(this.f8183a) ? 0 : 8);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        a aVar = this.f8184b;
        if (aVar != null) {
            aVar.a();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        a aVar = this.f8184b;
        if (aVar != null) {
            aVar.b();
        }
    }
}
