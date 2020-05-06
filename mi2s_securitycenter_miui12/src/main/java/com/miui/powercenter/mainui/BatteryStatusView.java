package com.miui.powercenter.mainui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.view.BatteryStatusValueText;
import com.miui.securitycenter.R;
import java.util.List;

public class BatteryStatusView extends FrameLayout {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static boolean f7103a = false;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static int f7104b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static long f7105c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public static int f7106d;
    private static int e;
    private TextView f;
    /* access modifiers changed from: private */
    public BatteryStatusValueText g;
    /* access modifiers changed from: private */
    public TextView h;
    private BatteryStatusValueText i;
    private BatteryStatusValueText j;
    private Context k;
    private boolean l;
    private BroadcastReceiver m = new a(this);

    class a extends AsyncTask<Void, Void, Long> {

        /* renamed from: a  reason: collision with root package name */
        Context f7107a;

        a(Context context) {
            this.f7107a = context;
            BatteryStatusView.this.g.setText(R.string.battery_charge_estimating);
            BatteryStatusView.this.h.setVisibility(8);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Long doInBackground(Void... voidArr) {
            List<aa> b2 = C0514s.c().b();
            return Long.valueOf(o.k(this.f7107a) ? C0501e.a(this.f7107a, b2).f6879a : C0520y.a(this.f7107a, b2));
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Long l) {
            super.onPostExecute(l);
            long unused = BatteryStatusView.f7105c = l.longValue();
            boolean unused2 = BatteryStatusView.f7103a = o.k(this.f7107a);
            int unused3 = BatteryStatusView.f7104b = o.e(this.f7107a);
            int unused4 = BatteryStatusView.f7106d = o.f(this.f7107a);
            BatteryStatusView.this.c(l.longValue());
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            super.onPreExecute();
        }
    }

    public BatteryStatusView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.k = context;
        LayoutInflater.from(context).inflate(R.layout.pc_list_item_battery_status, this);
        this.f = (TextView) findViewById(R.id.estimate_time_title);
        this.g = (BatteryStatusValueText) findViewById(R.id.estimate_time_value);
        this.h = (TextView) findViewById(R.id.estimate_time_unit);
        this.i = (BatteryStatusValueText) findViewById(R.id.temperature_value);
        this.j = (BatteryStatusValueText) findViewById(R.id.capacity_value);
        if (e == 0) {
            e = o.c(getContext());
        }
        c(e);
        a();
        b();
    }

    private String b(long j2) {
        return s.c(j2);
    }

    private void b() {
        if (!this.l) {
            if (this.m != null) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
                this.k.registerReceiver(this.m, intentFilter);
            }
            this.l = true;
        }
    }

    private void c() {
        new a(getContext()).execute(new Void[0]);
    }

    private void c(int i2) {
        this.j.setText(u.a(i2));
    }

    /* access modifiers changed from: private */
    public void c(long j2) {
        int i2;
        BatteryStatusValueText batteryStatusValueText;
        if (o.e(getContext()) >= 95 && j2 < 600000) {
            batteryStatusValueText = this.g;
            i2 = R.string.charging_text_battery_nearly_full;
        } else if (j2 == 0) {
            batteryStatusValueText = this.g;
            i2 = R.string.battery_charge_estimating;
        } else {
            this.g.setText(b(j2));
            this.h.setVisibility(0);
            return;
        }
        batteryStatusValueText.setText(i2);
        this.h.setVisibility(8);
    }

    private void d() {
        BroadcastReceiver broadcastReceiver;
        if (this.l && (broadcastReceiver = this.m) != null) {
            this.k.unregisterReceiver(broadcastReceiver);
            this.l = false;
        }
    }

    private void e() {
        boolean k2 = o.k(getContext());
        int e2 = o.e(getContext());
        int f2 = o.f(getContext());
        if (f7103a == k2 && f7104b == e2 && f7106d == f2) {
            c(f7105c);
        } else if (!k2 || e2 != 100) {
            c();
        } else {
            this.g.setText(R.string.battery_charge_full);
            this.h.setVisibility(8);
        }
    }

    private int getTemperature() {
        Intent registerReceiver = getContext().registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver == null) {
            return 0;
        }
        return registerReceiver.getIntExtra("temperature", 0) / 10;
    }

    public void a() {
        int i2;
        TextView textView;
        if (o.k(getContext())) {
            textView = this.f;
            i2 = R.string.battery_estimate_charge_time_title;
        } else {
            textView = this.f;
            i2 = R.string.battery_estimate_usage_time_title;
        }
        textView.setText(i2);
        e();
        this.i.setText(u.a(getTemperature()));
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        b();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        d();
    }
}
