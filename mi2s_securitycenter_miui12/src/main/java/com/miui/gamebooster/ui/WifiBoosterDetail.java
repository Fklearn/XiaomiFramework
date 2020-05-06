package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.UserHandle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import b.b.c.c.a;
import b.b.c.j.g;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class WifiBoosterDetail extends a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f5030a = "com.miui.gamebooster.ui.WifiBoosterDetail";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public IMiuiVpnManageService f5031b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public SlidingButton f5032c;

    /* renamed from: d  reason: collision with root package name */
    private ImageView f5033d;
    private TextView e;
    private TextView f;
    private LinearLayout g;
    /* access modifiers changed from: private */
    public Boolean h = false;
    /* access modifiers changed from: private */
    public String i;
    private CompoundButton.OnCheckedChangeListener j = new bb(this);
    private ServiceConnection k = new cb(this);

    /* JADX WARNING: type inference failed for: r3v0, types: [b.b.c.c.a, android.content.Context, com.miui.gamebooster.ui.WifiBoosterDetail, miui.app.Activity] */
    public void onCreate(Bundle bundle) {
        SlidingButton slidingButton;
        boolean j2;
        super.onCreate(bundle);
        setContentView(R.layout.gb_activity_wifi_detail);
        this.f5032c = findViewById(R.id.sliding_button);
        this.f5032c.setOnPerformCheckedChangeListener(this.j);
        this.f5033d = (ImageView) findViewById(R.id.icon);
        this.e = (TextView) findViewById(R.id.title);
        this.g = (LinearLayout) findViewById(R.id.wifi_detail);
        this.f = (TextView) findViewById(R.id.detail);
        this.i = getIntent().getAction();
        if (this.i.equals("action_detail_wifibooster")) {
            Intent intent = new Intent();
            intent.setPackage("com.miui.securitycenter");
            intent.setAction("com.miui.networkassistant.vpn.MIUI_VPN_MANAGE_SERVICE");
            g.a((Context) this, intent, this.k, 1, UserHandle.OWNER);
            return;
        }
        if (this.i.equals("action_handsfree_mute")) {
            this.g.setVisibility(8);
            this.f5033d.setImageDrawable(getResources().getDrawable(R.drawable.empty));
            this.e.setText(getResources().getString(R.string.gs_call_handsfree_mute));
            slidingButton = this.f5032c;
            j2 = com.miui.gamebooster.c.a.m(true);
        } else if (this.i.equals("action_detail_gwsd")) {
            this.g.setVisibility(8);
            this.f.setVisibility(0);
            this.f5033d.setVisibility(8);
            this.e.setText(getResources().getString(R.string.function_gwsd_title));
            slidingButton = this.f5032c;
            j2 = com.miui.gamebooster.c.a.j(false);
        } else {
            return;
        }
        slidingButton.setChecked(j2);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        ServiceConnection serviceConnection;
        WifiBoosterDetail.super.onDestroy();
        if (this.i.equals("action_detail_wifibooster") && this.f5031b != null && (serviceConnection = this.k) != null) {
            unbindService(serviceConnection);
        }
    }
}
