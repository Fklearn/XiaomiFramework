package com.miui.powercenter.batteryhistory.a;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miui.powercenter.batteryhistory.BatteryHistogramItem;
import com.miui.powercenter.batteryhistory.BatteryLevelHistogram;
import com.miui.powercenter.batteryhistory.a.a;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.securitycenter.R;
import java.util.List;

public class c extends a {

    /* renamed from: a  reason: collision with root package name */
    private BatteryLevelHistogram f6863a;

    public void a() {
        this.f6863a.a();
    }

    public void a(a.C0061a aVar) {
        this.f6863a.a(aVar);
    }

    public void a(List<aa> list, List<BatteryHistogramItem> list2) {
        this.f6863a.a(list, list2);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.pc_battery_statics_chart_model_histogram, viewGroup, false);
        this.f6863a = (BatteryLevelHistogram) inflate.findViewById(R.id.chart);
        return inflate;
    }
}
