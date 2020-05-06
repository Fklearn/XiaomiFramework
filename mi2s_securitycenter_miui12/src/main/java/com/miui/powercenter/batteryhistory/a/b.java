package com.miui.powercenter.batteryhistory.a;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miui.powercenter.batteryhistory.BatteryHistogramItem;
import com.miui.powercenter.batteryhistory.BatteryLevelChart;
import com.miui.powercenter.batteryhistory.a.a;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.securitycenter.R;
import java.util.List;

public class b extends a {

    /* renamed from: a  reason: collision with root package name */
    private BatteryLevelChart f6862a;

    public void a() {
        this.f6862a.a();
    }

    public void a(a.C0061a aVar) {
        aVar.a();
    }

    public void a(List<aa> list, List<BatteryHistogramItem> list2) {
        this.f6862a.a(list, list2);
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.pc_battery_statics_chart_model_chart, viewGroup, false);
        this.f6862a = (BatteryLevelChart) inflate.findViewById(R.id.chart);
        return inflate;
    }
}
