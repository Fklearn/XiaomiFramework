package com.miui.antivirus.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.miui.antivirus.model.a;
import com.miui.antivirus.model.l;
import com.miui.securitycenter.R;

public class WifiResultView extends e {

    /* renamed from: d  reason: collision with root package name */
    private RelativeLayout f2951d;
    private RelativeLayout e;
    private RelativeLayout f;
    private RelativeLayout g;
    private RelativeLayout h;
    private RelativeLayout i;
    private Button j;

    public WifiResultView(Context context) {
        super(context);
    }

    public WifiResultView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void onClick(View view) {
        this.f2961b.a(1039, (Object) null);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f2951d = (RelativeLayout) findViewById(R.id.wifi_item_connection);
        this.e = (RelativeLayout) findViewById(R.id.wifi_item_encryption);
        this.f = (RelativeLayout) findViewById(R.id.wifi_item_fake);
        this.g = (RelativeLayout) findViewById(R.id.wifi_item_dns);
        this.h = (RelativeLayout) findViewById(R.id.wifi_item_arp_attack);
        this.i = (RelativeLayout) findViewById(R.id.button_layout);
        this.j = (Button) findViewById(R.id.btn_optimize);
        this.j.setOnClickListener(this);
        this.f2951d.setVisibility(8);
        this.e.setVisibility(8);
        this.f.setVisibility(8);
        this.g.setVisibility(8);
        this.h.setVisibility(8);
    }

    public boolean onLongClick(View view) {
        a aVar = new a();
        aVar.a(0);
        aVar.a(this.f2962c.getString(R.string.sp_settings_check_item_title_wifi));
        a(aVar, this.f2962c);
        return true;
    }

    public void setWifiStatusInfo(l lVar) {
        int i2 = 0;
        if (!lVar.z()) {
            this.f2951d.setVisibility(0);
        }
        if (!lVar.B()) {
            this.e.setVisibility(0);
        }
        if (lVar.C()) {
            this.f.setVisibility(0);
        }
        if (lVar.A()) {
            this.g.setVisibility(0);
        }
        if (lVar.y()) {
            this.h.setVisibility(0);
        }
        RelativeLayout relativeLayout = this.i;
        if (lVar.x() <= 0) {
            i2 = 8;
        }
        relativeLayout.setVisibility(i2);
    }
}
