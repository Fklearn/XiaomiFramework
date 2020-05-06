package com.miui.powercenter.batteryhistory;

import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.miui.powercenter.utils.t;
import com.miui.securitycenter.R;

class ia implements ViewSwitcher.ViewFactory {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ oa f6897a;

    ia(oa oaVar) {
        this.f6897a = oaVar;
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity] */
    public View makeView() {
        TextView textView = new TextView(this.f6897a.o);
        textView.setGravity(8388611);
        textView.setTextColor(this.f6897a.o.getResources().getColor(R.color.pc_battery_statics_bar_title_color));
        textView.setTextSize(0, (float) this.f6897a.o.getResources().getDimensionPixelSize(R.dimen.view_dimen_46));
        textView.setTypeface(t.a(), 1);
        textView.setTextAlignment(5);
        return textView;
    }
}
