package com.miui.superpower.statusbar.button;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.View;
import com.miui.securitycenter.R;
import com.miui.superpower.statusbar.h;

public class WifiButton extends a {
    private WifiManager h;

    public WifiButton(Context context) {
        this(context, (AttributeSet) null);
    }

    public WifiButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WifiButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private boolean c() {
        WifiManager wifiManager = this.h;
        if (wifiManager != null) {
            return wifiManager.isWifiEnabled();
        }
        return false;
    }

    public void b() {
        setChecked(c());
    }

    public Drawable getEnableDrawableImpl() {
        return h.b(this.f8160b, "ic_qs_wifi_on", R.drawable.ic_qs_wifi_on);
    }

    public void onClick(View view) {
        toggle();
        boolean z = !c();
        WifiManager wifiManager = this.h;
        if (wifiManager != null && wifiManager.setWifiEnabled(z)) {
            setChecked(z);
        }
    }

    public void setWifiManager(WifiManager wifiManager) {
        this.h = wifiManager;
    }
}
