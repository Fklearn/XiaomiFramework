package com.miui.powercenter.batteryhistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.miui.securitycenter.R;
import java.util.Locale;

class ja implements ViewSwitcher.ViewFactory {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ oa f6898a;

    ja(oa oaVar) {
        this.f6898a = oaVar;
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity] */
    public View makeView() {
        int i;
        TextView textView = (TextView) LayoutInflater.from(this.f6898a.o).inflate(R.layout.pc_custom_battery_title_view, (ViewGroup) null).findViewById(R.id.custom_text);
        if (Locale.getDefault().getCountry().equals("CN") || Locale.getDefault().getCountry().equals("TW")) {
            textView.setSingleLine();
            i = 8388627;
        } else {
            textView.setMaxLines(2);
            textView.setMinLines(2);
            i = 8388691;
        }
        textView.setGravity(i);
        textView.setTextColor(this.f6898a.o.getResources().getColor(R.color.pc_battery_statics_tile_title_color));
        textView.setTextSize(0, (float) this.f6898a.o.getResources().getDimensionPixelSize(R.dimen.view_dimen_36));
        textView.setTextAlignment(5);
        return textView;
    }
}
