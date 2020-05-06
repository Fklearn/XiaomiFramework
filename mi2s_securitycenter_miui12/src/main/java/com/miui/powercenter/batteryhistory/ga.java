package com.miui.powercenter.batteryhistory;

import android.app.FragmentManager;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import com.miui.luckymoney.config.CommonPerConstants;
import com.miui.powercenter.batteryhistory.C0508l;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.a.b;
import com.miui.powercenter.batteryhistory.a.c;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class ga extends C0508l.a {

    /* renamed from: a  reason: collision with root package name */
    private FragmentManager f6888a;

    /* renamed from: b  reason: collision with root package name */
    private com.miui.powercenter.batteryhistory.a.a f6889b = this.f6890c;

    /* renamed from: c  reason: collision with root package name */
    private com.miui.powercenter.batteryhistory.a.a f6890c = new b();

    /* renamed from: d  reason: collision with root package name */
    private com.miui.powercenter.batteryhistory.a.a f6891d = new c();
    /* access modifiers changed from: private */
    public ba e;
    private a f;
    private BatteryHistoryDetailActivity g;

    private class a extends C0497a {
        private a() {
        }

        /* synthetic */ a(ga gaVar, ea eaVar) {
            this();
        }

        public void a(C0514s.a aVar, List<BatteryData> list, List<BatteryHistogramItem> list2) {
            ba baVar = new ba();
            baVar.f6870b = aVar;
            baVar.f6871c = list2;
            List<aa> list3 = aVar.f6922a;
            if (list3 != null && !list3.isEmpty()) {
                long a2 = list3.get(0).a();
                long a3 = list3.get(list3.size() - 1).a();
                if (a3 - a2 > CommonPerConstants.DEFAULT.DEFAULT_UPDATE_FREQUENCY_DEFAULT) {
                    long j = a3 - CommonPerConstants.DEFAULT.DEFAULT_UPDATE_FREQUENCY_DEFAULT;
                    ArrayList arrayList = new ArrayList();
                    for (int i = 0; i < list3.size(); i++) {
                        if (list3.get(i).a() > j) {
                            arrayList.add(list3.get(i));
                        }
                    }
                    int i2 = 1;
                    while (i2 < arrayList.size() - 1) {
                        if (((aa) arrayList.get(i2)).f6866c == ((aa) arrayList.get(i2 - 1)).f6866c && ((aa) arrayList.get(i2)).f6866c == ((aa) arrayList.get(i2 + 1)).f6866c) {
                            arrayList.remove(arrayList.get(i2));
                            i2--;
                        }
                        i2++;
                    }
                    baVar.f6869a = arrayList;
                } else {
                    int i3 = 1;
                    while (i3 < list3.size() - 1) {
                        if (list3.get(i3).f6866c == list3.get(i3 - 1).f6866c && list3.get(i3).f6866c == list3.get(i3 + 1).f6866c) {
                            list3.remove(list3.get(i3));
                            i3--;
                        }
                        i3++;
                    }
                    baVar.f6869a = list3;
                }
                new Handler(Looper.getMainLooper()).post(new fa(this, baVar));
            }
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ga(android.view.ViewGroup r4, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity r5) {
        /*
            r3 = this;
            android.view.LayoutInflater r0 = android.view.LayoutInflater.from(r5)
            r1 = 2131493313(0x7f0c01c1, float:1.8610103E38)
            r2 = 0
            android.view.View r4 = r0.inflate(r1, r4, r2)
            r3.<init>(r4)
            r3.g = r5
            com.miui.powercenter.batteryhistory.a.b r4 = new com.miui.powercenter.batteryhistory.a.b
            r4.<init>()
            r3.f6890c = r4
            com.miui.powercenter.batteryhistory.a.c r4 = new com.miui.powercenter.batteryhistory.a.c
            r4.<init>()
            r3.f6891d = r4
            com.miui.powercenter.batteryhistory.a.a r4 = r3.f6890c
            r3.f6889b = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.ga.<init>(android.view.ViewGroup, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity):void");
    }

    /* access modifiers changed from: private */
    public void e() {
        com.miui.powercenter.batteryhistory.a.a aVar;
        if (this.f6889b == this.f6890c) {
            this.g.getFragmentManager().beginTransaction().setTransition(4099).show(this.f6891d).hide(this.f6890c).commit();
            this.f6889b.a();
            aVar = this.f6891d;
        } else {
            this.g.getFragmentManager().beginTransaction().setTransition(4099).show(this.f6890c).hide(this.f6891d).commit();
            this.f6889b.a();
            aVar = this.f6890c;
        }
        this.f6889b = aVar;
        d();
    }

    private void f() {
        this.f = new a(this, (ea) null);
        this.g.l().a((ca) this.f);
    }

    public void a() {
        super.a();
        if (this.f6888a == null) {
            Point point = new Point();
            this.g.getWindowManager().getDefaultDisplay().getRealSize(point);
            if (point.y <= 1920) {
                View findViewById = this.itemView.findViewById(R.id.content);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) findViewById.getLayoutParams();
                layoutParams.height = this.g.getResources().getDimensionPixelSize(R.dimen.pc_battery_statics_chart_height_mini);
                findViewById.setLayoutParams(layoutParams);
            }
            this.f6888a = this.g.getFragmentManager();
            this.f6888a.beginTransaction().add(R.id.content, this.f6891d).commit();
            this.f6888a.beginTransaction().add(R.id.content, this.f6890c).commit();
            this.f6888a.beginTransaction().show(this.f6890c).hide(this.f6891d).commit();
        }
        f();
    }

    public void b() {
        BatteryHistoryDetailActivity batteryHistoryDetailActivity;
        super.b();
        if (this.f != null && (batteryHistoryDetailActivity = this.g) != null) {
            batteryHistoryDetailActivity.l().b(this.f);
        }
    }

    public void c() {
        this.f6889b.a(new ea(this));
    }

    public void d() {
        List<aa> list;
        com.miui.powercenter.batteryhistory.a.a aVar;
        ba baVar = this.e;
        if (baVar != null && (list = baVar.f6869a) != null && (aVar = this.f6889b) != null) {
            aVar.a(list, baVar.f6871c);
        }
    }
}
