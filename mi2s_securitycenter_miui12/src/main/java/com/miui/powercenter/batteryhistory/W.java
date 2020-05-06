package com.miui.powercenter.batteryhistory;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.securitycenter.R;
import java.util.List;

public class W extends LinearLayout {

    /* renamed from: a  reason: collision with root package name */
    private Context f6853a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public BatteryHardwareBar f6854b;

    /* renamed from: c  reason: collision with root package name */
    private a f6855c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public C0514s.a f6856d;
    /* access modifiers changed from: private */
    public List<BatteryHistogramItem> e;

    private class a extends C0497a {
        private a() {
        }

        public void a(C0514s.a aVar, List<BatteryData> list, List<BatteryHistogramItem> list2) {
            C0514s.a unused = W.this.f6856d = aVar;
            List unused2 = W.this.e = list2;
            new Handler(Looper.getMainLooper()).post(new V(this, aVar));
        }
    }

    public W(Context context) {
        this(context, (AttributeSet) null);
    }

    public W(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public W(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.f6853a = context;
        a();
    }

    private void a() {
        LayoutInflater.from(this.f6853a).inflate(R.layout.pc_battery_rank_hardware, this);
        this.f6854b = (BatteryHardwareBar) findViewById(R.id.battery_hardware_bar);
        this.f6855c = new a();
        Context context = this.f6853a;
        if (context instanceof BatteryHistoryDetailActivity) {
            ((BatteryHistoryDetailActivity) context).l().a((ca) this.f6855c);
        }
    }

    public void a(int i, int i2) {
        long j;
        List<BatteryHistogramItem> list = this.e;
        if (list != null && this.f6856d != null && i < list.size() && i2 >= i) {
            if (i < 0) {
                BatteryHardwareBar batteryHardwareBar = this.f6854b;
                if (batteryHardwareBar != null) {
                    batteryHardwareBar.a(this.f6856d.f6922a, AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    return;
                }
            } else {
                long j2 = this.e.get(i).startTime;
                if (i2 >= this.e.size() - 1) {
                    List<aa> list2 = this.f6856d.f6922a;
                    j = list2.get(list2.size() - 1).a();
                } else {
                    j = this.e.get(i2 + 1).startTime;
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= this.f6856d.f6922a.size()) {
                        i3 = -1;
                        break;
                    } else if (this.f6856d.f6922a.get(i3).a() >= j2) {
                        break;
                    } else {
                        i3++;
                    }
                }
                if (i3 >= 0 && i3 < this.f6856d.f6922a.size()) {
                    int i4 = i3;
                    while (true) {
                        if (i4 >= this.f6856d.f6922a.size()) {
                            i4 = -1;
                            break;
                        } else if (this.f6856d.f6922a.get(i4).a() >= j) {
                            break;
                        } else {
                            i4++;
                        }
                    }
                    List<aa> subList = this.f6856d.f6922a.subList(i3, i4);
                    BatteryHardwareBar batteryHardwareBar2 = this.f6854b;
                    if (batteryHardwareBar2 != null) {
                        batteryHardwareBar2.a(subList, AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            this.f6854b.invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.f6855c != null) {
            Context context = this.f6853a;
            if (context instanceof BatteryHistoryDetailActivity) {
                ((BatteryHistoryDetailActivity) context).l().b(this.f6855c);
            }
        }
    }
}
