package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import com.miui.securitycenter.R;

/* renamed from: com.miui.antispam.ui.activity.k  reason: case insensitive filesystem */
class C0217k implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f2597a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f2598b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AntiSpamAddressActivity f2599c;

    C0217k(AntiSpamAddressActivity antiSpamAddressActivity, boolean z, int i) {
        this.f2599c = antiSpamAddressActivity;
        this.f2597a = z;
        this.f2598b = i;
    }

    /* JADX WARNING: type inference failed for: r4v14, types: [android.content.Context, com.miui.antispam.ui.activity.AntiSpamAddressActivity] */
    public void onClick(View view) {
        this.f2599c.k.clear();
        this.f2599c.l.clear();
        for (int i = 0; i < this.f2599c.f2511d.e.size(); i++) {
            if (this.f2599c.f2511d.i.get(i)) {
                AntiSpamAddressActivity antiSpamAddressActivity = this.f2599c;
                antiSpamAddressActivity.l.add(Integer.valueOf(antiSpamAddressActivity.f2511d.e.get(i).f1326b));
                AntiSpamAddressActivity antiSpamAddressActivity2 = this.f2599c;
                antiSpamAddressActivity2.k.add(antiSpamAddressActivity2.f2511d.e.get(i).f1325a);
            }
        }
        this.f2599c.g.setChecked(true);
        this.f2599c.h.setChecked(true);
        if (this.f2599c.k.size() > 0) {
            ? r4 = this.f2599c;
            AlertDialog alertDialog = r4.j;
            if (alertDialog == null) {
                r4.j = new AlertDialog.Builder(r4).setTitle(this.f2597a ? R.string.dlg_black_antispam_hint : R.string.dlg_white_antispam_hint).setMessage(this.f2597a ? R.string.dlg_black_address_antispam_message : R.string.dlg_white_address_antispam_message).setView(this.f2599c.i).setPositiveButton(17039370, new C0216j(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
            } else {
                alertDialog.show();
            }
        }
    }
}
