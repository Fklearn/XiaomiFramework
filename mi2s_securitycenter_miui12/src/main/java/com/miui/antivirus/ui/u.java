package com.miui.antivirus.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import b.b.b.a.b;
import b.b.b.p;
import com.miui.antivirus.activity.MainActivity;
import com.miui.antivirus.service.GuardService;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class u implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static u f3002a;

    /* renamed from: b  reason: collision with root package name */
    private Context f3003b;

    public u(Context context) {
        this.f3003b = context.getApplicationContext();
    }

    public static synchronized u a(Context context) {
        u uVar;
        synchronized (u.class) {
            if (f3002a == null) {
                f3002a = new u(context);
            }
            uVar = f3002a;
        }
        return uVar;
    }

    public void a(int i, ArrayList<Integer> arrayList) {
        String str;
        String str2;
        String str3;
        String str4;
        boolean z = true;
        if (i != 1 || p.b()) {
            if (i == 1) {
                str3 = this.f3003b.getString(R.string.sp_background_risk_dialog_title_wifi_approve);
                str2 = this.f3003b.getString(R.string.sp_background_risk_dialog_summary_wifi_approve);
                str4 = this.f3003b.getString(R.string.safepay_alert_dialog_button_continue);
                str = this.f3003b.getString(17039360);
            } else {
                str3 = this.f3003b.getString(R.string.sp_background_risk_dialog_title);
                str2 = this.f3003b.getString(R.string.sp_background_risk_dialog_summary_new);
                String string = this.f3003b.getString(R.string.safepay_alert_dialog_button_fix);
                str = this.f3003b.getString(R.string.safepay_alert_dialog_button_continue);
                str4 = string;
            }
            t tVar = new t(this.f3003b);
            if (i != 1) {
                tVar.a(i, arrayList);
            }
            tVar.setButton(-1, str4, this);
            tVar.setButton(-2, str, this);
            tVar.setTitle(str3);
            tVar.setMessage(str2);
            if (i != 1) {
                z = false;
            }
            tVar.a(z);
            tVar.show();
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        t tVar = (t) dialogInterface;
        int b2 = tVar.b();
        if (i == -2) {
            if (dialogInterface != null) {
                dialogInterface.dismiss();
            }
            if (b2 != 1) {
                b.C0023b.c("continue");
            }
            Intent intent = new Intent(this.f3003b, GuardService.class);
            intent.setAction("action_pay_safe_dialog_click_ignore");
            this.f3003b.startService(intent);
        } else if (i == -1) {
            if (dialogInterface != null) {
                dialogInterface.dismiss();
            }
            if (b2 != 1) {
                Intent intent2 = new Intent(this.f3003b, MainActivity.class);
                intent2.addFlags(402653184);
                this.f3003b.startActivity(intent2);
                b.C0023b.c("fix");
                return;
            }
            p.b(!tVar.a());
        }
    }
}
