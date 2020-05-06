package com.miui.antivirus.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import b.b.c.j.d;
import com.miui.securitycenter.R;

public class y implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static y f3007a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f3008b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public WifiInfo f3009c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public t f3010d;

    private y(Context context) {
        this.f3008b = context.getApplicationContext();
    }

    public static synchronized y a(Context context) {
        y yVar;
        synchronized (y.class) {
            if (f3007a == null) {
                f3007a = new y(context);
            }
            yVar = f3007a;
        }
        return yVar;
    }

    private void a() {
        d.a(new w(this));
    }

    private void b() {
        d.a(new x(this));
    }

    public void a(WifiInfo wifiInfo) {
        if (wifiInfo != null) {
            t tVar = this.f3010d;
            if (tVar == null || !tVar.isShowing()) {
                this.f3009c = wifiInfo;
                String string = this.f3008b.getString(R.string.button_text_disconnect_now);
                String string2 = this.f3008b.getString(R.string.button_text_ignore);
                String string3 = this.f3008b.getString(R.string.button_text_trust);
                this.f3010d = new t(this.f3008b);
                this.f3010d.setButton(-1, string, this);
                this.f3010d.setButton(-2, string2, this);
                this.f3010d.setButton(-3, string3, this);
                this.f3010d.setTitle(R.string.wifi_danger_dialog_title);
                this.f3010d.setMessage(this.f3008b.getString(R.string.wifi_danger_dialog_messgae));
                this.f3010d.a(this.f3008b.getString(R.string.wifi_danger_dialog_tips));
                this.f3010d.a(false);
                this.f3010d.setOnDismissListener(new v(this));
                this.f3010d.show();
            }
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -3) {
            b();
        } else if (i != -2 && i == -1) {
            a();
        }
        if (dialogInterface != null) {
            dialogInterface.dismiss();
        }
    }
}
