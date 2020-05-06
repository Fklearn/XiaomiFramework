package com.miui.powercenter.batteryhistory;

import android.view.View;
import android.widget.LinearLayout;
import com.miui.powercenter.a.a;
import miui.widget.DropDownSingleChoiceMenu;

/* renamed from: com.miui.powercenter.batteryhistory.g  reason: case insensitive filesystem */
class C0503g implements DropDownSingleChoiceMenu.OnMenuListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BatteryDetailPannel f6887a;

    C0503g(BatteryDetailPannel batteryDetailPannel) {
        this.f6887a = batteryDetailPannel;
    }

    public void onDismiss() {
    }

    public void onItemSelected(DropDownSingleChoiceMenu dropDownSingleChoiceMenu, int i) {
        View view;
        LinearLayout linearLayout;
        int unused = this.f6887a.i = i;
        this.f6887a.f6785c.setText(this.f6887a.h[i]);
        boolean z = i == 0;
        if (this.f6887a.f != z) {
            boolean unused2 = this.f6887a.f = z;
            if (i == 0) {
                if (this.f6887a.f6786d == null) {
                    BatteryDetailPannel batteryDetailPannel = this.f6887a;
                    C0500d unused3 = batteryDetailPannel.f6786d = new C0500d(batteryDetailPannel.f6783a);
                }
                this.f6887a.f6784b.removeAllViews();
                linearLayout = this.f6887a.f6784b;
                view = this.f6887a.f6786d;
            } else {
                this.f6887a.f6784b.removeAllViews();
                if (this.f6887a.e == null) {
                    BatteryDetailPannel batteryDetailPannel2 = this.f6887a;
                    W unused4 = batteryDetailPannel2.e = new W(batteryDetailPannel2.f6783a);
                }
                linearLayout = this.f6887a.f6784b;
                view = this.f6887a.e;
            }
            linearLayout.addView(view);
            a.b(this.f6887a.f);
        }
    }

    public void onShow() {
    }
}
