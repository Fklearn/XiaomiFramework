package com.miui.superpower.statusbar.button;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.View;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import com.miui.superpower.statusbar.h;
import miui.util.FeatureParser;

public class GPSButton extends a {
    private ContentResolver h;
    private final ContentObserver i;

    public GPSButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public GPSButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GPSButton(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.i = new c(this, this.g);
        this.h = context.getContentResolver();
    }

    private boolean c() {
        try {
            return Settings.Secure.getInt(this.h, "location_mode") != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void b() {
        setChecked(c());
    }

    public Drawable getEnableDrawableImpl() {
        boolean z = FeatureParser.getBoolean("support_dual_gps", false);
        return h.b(this.f8160b, z ? "ic_qs_dual_location_enabled" : "ic_signal_location_enable", z ? R.drawable.ic_qs_dual_location_enabled : R.drawable.ic_qs_signal_location_enable);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.h.registerContentObserver(Settings.Secure.getUriFor("location_providers_allowed"), false, this.i);
        super.onAttachedToWindow();
    }

    public void onClick(View view) {
        toggle();
        o.a(this.f8160b, isChecked() ? 2 : 0);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.h.unregisterContentObserver(this.i);
        super.onDetachedFromWindow();
    }
}
