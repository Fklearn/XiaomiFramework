package com.miui.superpower.statusbar.icon;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.R;
import com.miui.superpower.statusbar.h;

public class WifiSignalView extends ImageView {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public WifiManager f8193a;

    /* renamed from: b  reason: collision with root package name */
    private a f8194b;

    /* renamed from: c  reason: collision with root package name */
    private Drawable[] f8195c;

    private class a extends com.miui.superpower.statusbar.a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            this.f8155c.addAction("android.net.wifi.RSSI_CHANGED");
            this.f8155c.addAction("android.net.wifi.STATE_CHANGE");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                    if (intent.getIntExtra(VariableNames.WIFI_STATE, 4) != 1) {
                        return;
                    }
                } else if (action.equals("android.net.wifi.RSSI_CHANGED")) {
                    WifiSignalView.this.a();
                    return;
                } else if (action.equals("android.net.wifi.STATE_CHANGE")) {
                    WifiInfo connectionInfo = WifiSignalView.this.f8193a.getConnectionInfo();
                    if (!(connectionInfo == null || connectionInfo.getNetworkId() == -1)) {
                        WifiSignalView.this.a();
                        WifiSignalView.this.setVisibility(0);
                        return;
                    }
                } else {
                    return;
                }
                WifiSignalView.this.setVisibility(8);
            }
        }
    }

    public WifiSignalView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WifiSignalView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WifiSignalView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f8195c = new Drawable[5];
        a(context);
    }

    /* access modifiers changed from: private */
    public void a() {
        Drawable drawable;
        int strength = getStrength();
        if (strength == 0) {
            drawable = this.f8195c[0];
        } else {
            if (strength > 0) {
                Drawable[] drawableArr = this.f8195c;
                if (strength < drawableArr.length) {
                    drawable = drawableArr[strength];
                }
            }
            Drawable[] drawableArr2 = this.f8195c;
            if (strength == drawableArr2.length) {
                drawable = drawableArr2[4];
            } else {
                return;
            }
        }
        setImageDrawable(drawable);
    }

    private void a(Context context) {
        this.f8193a = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        this.f8194b = new a(context);
        this.f8195c[0] = h.b(context, "stat_sys_wifi_signal_0", R.drawable.superpower_stat_sys_wifi_signal_0);
        this.f8195c[1] = h.b(context, "stat_sys_wifi_signal_1", R.drawable.superpower_stat_sys_wifi_signal_1);
        this.f8195c[2] = h.b(context, "stat_sys_wifi_signal_2", R.drawable.superpower_stat_sys_wifi_signal_2);
        this.f8195c[3] = h.b(context, "stat_sys_wifi_signal_3", R.drawable.superpower_stat_sys_wifi_signal_3);
        this.f8195c[4] = h.b(context, "stat_sys_wifi_signal_4", R.drawable.superpower_stat_sys_wifi_signal_4);
    }

    private int getStrength() {
        WifiInfo connectionInfo = this.f8193a.getConnectionInfo();
        if (connectionInfo == null || connectionInfo.getSSID().equals("<unknown ssid>")) {
            return 0;
        }
        return WifiManager.calculateSignalLevel(connectionInfo.getRssi(), this.f8195c.length + 1);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        a();
        this.f8194b.a();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.f8194b.b();
    }
}
