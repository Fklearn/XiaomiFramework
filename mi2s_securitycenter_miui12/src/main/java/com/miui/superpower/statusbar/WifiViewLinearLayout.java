package com.miui.superpower.statusbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.R;
import com.miui.superpower.statusbar.button.WifiButton;

public class WifiViewLinearLayout extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f8148a;

    /* renamed from: b  reason: collision with root package name */
    private WifiManager f8149b;

    /* renamed from: c  reason: collision with root package name */
    private a f8150c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f8151d;
    /* access modifiers changed from: private */
    public WifiButton e;
    private Drawable f;

    private class a extends a {
        public a(Context context) {
            super(context);
            this.f8155c.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            this.f8155c.addAction("android.net.wifi.RSSI_CHANGED");
            this.f8155c.addAction("android.net.wifi.STATE_CHANGE");
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                int intExtra = intent.getIntExtra(VariableNames.WIFI_STATE, 4);
                if (intExtra == 1) {
                    WifiViewLinearLayout.this.e.setChecked(false);
                } else if (intExtra != 2) {
                    if (intExtra == 3) {
                        WifiViewLinearLayout.this.e.setChecked(true);
                        return;
                    }
                    return;
                }
                WifiViewLinearLayout.this.a(false);
            } else if (action.equals("android.net.wifi.RSSI_CHANGED") || action.equals("android.net.wifi.STATE_CHANGE")) {
                WifiViewLinearLayout.this.a(true);
            }
        }
    }

    public WifiViewLinearLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public WifiViewLinearLayout(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WifiViewLinearLayout(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f8148a = context;
        this.f8149b = (WifiManager) context.getApplicationContext().getSystemService("wifi");
        this.f8150c = new a(context);
        this.f = getResources().getDrawable(R.drawable.superpower_button_expand_indicator);
        Drawable drawable = this.f;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), this.f.getMinimumHeight());
    }

    /* access modifiers changed from: private */
    public void a() {
        g.a(this.f8148a).b().sendEmptyMessage(2);
        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
        intent.setFlags(268435456);
        this.f8148a.startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        WifiInfo connectionInfo = this.f8149b.getConnectionInfo();
        if (!(!z || connectionInfo == null || connectionInfo.getNetworkId() == -1)) {
            String ssid = connectionInfo.getSSID();
            if (!ssid.equals("<unknown ssid>")) {
                this.f8151d.setText(ssid.substring(1, ssid.length() - 1));
                this.f8151d.setCompoundDrawables((Drawable) null, (Drawable) null, this.f, (Drawable) null);
                this.f8151d.setPadding(this.f.getMinimumWidth(), 0, 0, 0);
                setClickable(true);
                return;
            }
        }
        this.f8151d.setText(R.string.auto_task_operation_wifi);
        this.f8151d.setCompoundDrawables((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        this.f8151d.setPadding(0, 0, 0, 0);
        setClickable(false);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        this.f8151d = (TextView) findViewById(R.id.wifi_label);
        this.e = (WifiButton) findViewById(R.id.wifi_button);
        this.e.setWifiManager(this.f8149b);
        this.e.b();
        this.f8150c.a();
        this.f8151d.setSelected(true);
        this.f8151d.requestFocusFromTouch();
        this.f8151d.setOnClickListener(new i(this));
        this.e.setOnLongClickListener(new j(this));
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.f8150c.b();
        super.onDetachedFromWindow();
    }
}
