package com.miui.powercenter;

import android.content.DialogInterface;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.u;
import com.miui.securitycenter.R;
import com.miui.superpower.b.g;
import com.miui.superpower.b.h;
import miui.app.AlertDialog;

class j implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerMainActivity f7072a;

    j(PowerMainActivity powerMainActivity) {
        this.f7072a = powerMainActivity;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        AlertDialog.Builder negativeButton;
        DialogInterface.OnCancelListener iVar;
        if (compoundButton.getId() == R.id.slide_power_save_mode) {
            if (!z || !u.c()) {
                this.f7072a.a(z);
                return;
            }
            View inflate = View.inflate(this.f7072a.s, R.layout.pc_dialog_power_save, (ViewGroup) null);
            ((TextView) inflate.findViewById(R.id.pc_main_dialog_power_save_content)).setText(o.i(this.f7072a.s));
            ((TextView) inflate.findViewById(R.id.txtview_powersave_title)).setText(Html.fromHtml(this.f7072a.m()));
            negativeButton = new AlertDialog.Builder(this.f7072a.s).setTitle(R.string.power_center_scan_item_title_power_saver).setView(inflate).setPositiveButton(17039370, new g(this, z)).setNegativeButton(17039360, new f(this));
            iVar = new e(this);
        } else if (compoundButton.getId() != R.id.slide_super_save_mode) {
            return;
        } else {
            if (!z) {
                o.a(this.f7072a.s, false, true);
                return;
            } else if (!g.c()) {
                h.a("home");
                o.a(this.f7072a.s, true, true);
                return;
            } else {
                View inflate2 = View.inflate(this.f7072a.s, R.layout.pc_dialog_super_save, (ViewGroup) null);
                ((TextView) inflate2.findViewById(R.id.pc_main_dialog_super_save_content)).setText((!com.miui.powercenter.utils.g.b() || !com.miui.powercenter.utils.g.a()) ? R.string.power_dialog_super_save_msg : R.string.power_dialog_super_save_msg_5g);
                h hVar = new h(this, (CheckBox) inflate2.findViewById(R.id.checkbox));
                negativeButton = new AlertDialog.Builder(this.f7072a.s).setTitle(R.string.power_dialog_super_save_title).setView(inflate2).setPositiveButton(17039370, hVar).setNegativeButton(17039360, hVar);
                iVar = new i(this);
            }
        }
        negativeButton.setOnCancelListener(iVar).show();
    }
}
