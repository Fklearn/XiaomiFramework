package com.miui.powercenter.batteryhistory;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import miui.net.ConnectivityHelper;

public class BatteryHardwareBar extends View {

    /* renamed from: a  reason: collision with root package name */
    Z f6787a;

    /* renamed from: b  reason: collision with root package name */
    TextPaint f6788b;

    /* renamed from: c  reason: collision with root package name */
    Path f6789c;

    /* renamed from: d  reason: collision with root package name */
    Path f6790d;
    Path e;
    Path f;
    Path g;
    int h;
    int i;
    int j;
    int k;
    int l;
    int m;
    int n;
    int o;
    int p;
    boolean q;
    List<aa> r;

    class a extends AsyncTask<Integer, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private List<aa> f6791a = new ArrayList();

        /* renamed from: b  reason: collision with root package name */
        private boolean f6792b = false;

        a(List<aa> list) {
            this.f6791a.clear();
            this.f6791a.addAll(list);
            this.f6792b = BatteryHardwareBar.this.b();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Integer... numArr) {
            C0504h hVar = new C0504h();
            BatteryHardwareBar batteryHardwareBar = BatteryHardwareBar.this;
            hVar.k = batteryHardwareBar.f6787a;
            batteryHardwareBar.f6789c.reset();
            BatteryHardwareBar batteryHardwareBar2 = BatteryHardwareBar.this;
            hVar.f6893a = batteryHardwareBar2.f6789c;
            hVar.f6894b = batteryHardwareBar2.k;
            batteryHardwareBar2.f6790d.reset();
            BatteryHardwareBar batteryHardwareBar3 = BatteryHardwareBar.this;
            hVar.f6895c = batteryHardwareBar3.f6790d;
            hVar.f6896d = batteryHardwareBar3.l;
            batteryHardwareBar3.e.reset();
            BatteryHardwareBar batteryHardwareBar4 = BatteryHardwareBar.this;
            hVar.e = batteryHardwareBar4.e;
            hVar.f = batteryHardwareBar4.m;
            batteryHardwareBar4.f.reset();
            BatteryHardwareBar batteryHardwareBar5 = BatteryHardwareBar.this;
            hVar.g = batteryHardwareBar5.f;
            hVar.h = batteryHardwareBar5.n;
            batteryHardwareBar5.g.reset();
            BatteryHardwareBar batteryHardwareBar6 = BatteryHardwareBar.this;
            hVar.i = batteryHardwareBar6.g;
            hVar.j = batteryHardwareBar6.o;
            C0505i.a(hVar, this.f6791a, numArr[0].intValue(), this.f6792b);
            return null;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            BatteryHardwareBar.this.invalidate();
        }
    }

    public BatteryHardwareBar(Context context) {
        this(context, (AttributeSet) null);
    }

    public BatteryHardwareBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f6787a = new Z();
        this.f6788b = new TextPaint(1);
        this.f6789c = new Path();
        this.f6790d = new Path();
        this.e = new Path();
        this.f = new Path();
        this.g = new Path();
        this.r = new ArrayList();
        this.f6787a.a(new int[]{getResources().getColor(R.color.pc_power_center_signal_strength_0), getResources().getColor(R.color.pc_power_center_signal_strength_1), getResources().getColor(R.color.pc_power_center_signal_strength_2), getResources().getColor(R.color.pc_power_center_signal_strength_3), getResources().getColor(R.color.pc_power_center_signal_strength_4), getResources().getColor(R.color.pc_power_center_signal_strength_5), getResources().getColor(R.color.pc_power_center_signal_strength_6)});
        this.f6788b.density = getResources().getDisplayMetrics().density;
        this.f6788b.setTextSize(context.getResources().getDimension(R.dimen.battery_history_textsize));
        this.f6788b.setTypeface(Typeface.create("mipro", 0));
        this.f6788b.setColor(getResources().getColor(R.color.pc_battery_detail_pop_window_text_normal));
        this.q = !ConnectivityHelper.getInstance().isWifiOnly();
        a();
    }

    private int a(int i2) {
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i2);
        if (mode == 1073741824) {
            return size;
        }
        int i3 = this.k + this.h;
        return mode == Integer.MIN_VALUE ? Math.min(i3, size) : i3;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        this.f6788b.setStrokeWidth((float) ((int) TypedValue.applyDimension(1, 1.0f, getResources().getDisplayMetrics())));
        this.i = ((int) this.f6788b.descent()) - ((int) this.f6788b.ascent());
        this.h = (int) getContext().getResources().getDimension(R.dimen.battery_history_hardware_line_width);
        this.j = (int) getContext().getResources().getDimension(R.dimen.battery_history_hardware_textline_padding);
        int dimensionPixelOffset = getContext().getResources().getDimensionPixelOffset(R.dimen.battery_history_hardware_padding_top);
        int i2 = this.i;
        int i3 = this.j;
        int i4 = i2 + i3 + dimensionPixelOffset;
        this.p = i2 + i3;
        this.m = this.p + i4;
        this.n = this.m + i4;
        this.o = this.n + i4;
        this.l = this.o + i4;
        this.k = this.l + i4;
    }

    /* access modifiers changed from: package-private */
    public void a(Canvas canvas) {
        boolean b2 = b();
        int width = getWidth();
        if (!b2) {
            width = 0;
        }
        int i2 = width;
        if (this.q) {
            Canvas canvas2 = canvas;
            int i3 = i2;
            this.f6787a.a(canvas2, i3, this.p, this.h, b2);
            canvas.drawText(getContext().getString(R.string.battery_stats_phone_signal_label), (float) i2, (float) (this.p - this.j), this.f6788b);
        }
        if (!this.e.isEmpty()) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.pc_battery_statics_hardware_blue));
            paint.setStrokeWidth((float) this.h);
            canvas.drawPath(this.e, paint);
        }
        float f2 = (float) i2;
        canvas.drawText(getContext().getString(R.string.battery_stats_gps_on_label), f2, (float) (this.m - this.j), this.f6788b);
        if (!this.f.isEmpty()) {
            Paint paint2 = new Paint();
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setColor(getResources().getColor(R.color.pc_battery_statics_hardware_blue));
            paint2.setStrokeWidth((float) this.h);
            canvas.drawPath(this.f, paint2);
        }
        canvas.drawText(getContext().getString(R.string.battery_stats_wifi_running_label), f2, (float) (this.n - this.j), this.f6788b);
        if (!this.g.isEmpty()) {
            Paint paint3 = new Paint();
            paint3.setStyle(Paint.Style.STROKE);
            paint3.setColor(getResources().getColor(R.color.pc_battery_statics_hardware_blue));
            paint3.setStrokeWidth((float) this.h);
            canvas.drawPath(this.g, paint3);
        }
        canvas.drawText(getContext().getString(R.string.battery_stats_wake_lock_label), f2, (float) (this.o - this.j), this.f6788b);
        if (!this.f6790d.isEmpty()) {
            Paint paint4 = new Paint();
            paint4.setStyle(Paint.Style.STROKE);
            paint4.setColor(getResources().getColor(R.color.pc_battery_statics_hardware_blue));
            paint4.setStrokeWidth((float) this.h);
            canvas.drawPath(this.f6790d, paint4);
        }
        canvas.drawText(getContext().getString(R.string.battery_stats_screen_on_label), f2, (float) (this.l - this.j), this.f6788b);
        if (!this.f6789c.isEmpty()) {
            Paint paint5 = new Paint();
            paint5.setStyle(Paint.Style.STROKE);
            paint5.setColor(getResources().getColor(R.color.pc_battery_statics_hardware_green));
            paint5.setStrokeWidth((float) this.h);
            canvas.drawPath(this.f6789c, paint5);
        }
        canvas.drawText(getContext().getString(R.string.battery_stats_charging_label), f2, (float) (this.k - this.j), this.f6788b);
    }

    public void a(List<aa> list, Executor executor) {
        this.r.clear();
        this.r.addAll(list);
        new a(this.r).executeOnExecutor(executor, new Integer[]{Integer.valueOf(getWidth())});
    }

    public boolean b() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.f6788b.setTextAlign(b() ? Paint.Align.RIGHT : Paint.Align.LEFT);
        a(canvas);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        super.onMeasure(i2, i3);
        setMeasuredDimension(View.MeasureSpec.getSize(i2), a(i3));
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        this.f6787a.b(i2);
    }
}
