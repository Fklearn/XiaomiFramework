package com.miui.powercenter.autotask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.miui.securitycenter.R;
import miui.widget.TimePicker;

public class DayTimePicker extends FrameLayout {

    /* renamed from: a  reason: collision with root package name */
    private TextView f6706a;

    /* renamed from: b  reason: collision with root package name */
    private TextView f6707b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f6708c;

    /* renamed from: d  reason: collision with root package name */
    private TimePicker f6709d;

    public DayTimePicker(Context context) {
        super(context);
        a();
    }

    public DayTimePicker(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        a();
    }

    private void a() {
        LayoutInflater.from(getContext()).inflate(R.layout.pc_day_hour_minute_picker, this);
        this.f6706a = (TextView) findViewById(R.id.title);
        this.f6707b = (TextView) findViewById(R.id.day_title);
        this.f6708c = (TextView) findViewById(R.id.day_value);
        this.f6709d = findViewById(R.id.hour_minute);
        this.f6709d.setIs24HourView(true);
    }

    public void setDayTitle(int i) {
        setDayTitle(getResources().getString(i));
    }

    public void setDayTitle(String str) {
        this.f6707b.setText(str);
    }

    public void setDayValue(int i) {
        setDayValue(getResources().getString(i));
    }

    public void setDayValue(String str) {
        this.f6708c.setText(str);
    }

    public void setOnTimeChangedListener(TimePicker.OnTimeChangedListener onTimeChangedListener) {
        this.f6709d.setOnTimeChangedListener(onTimeChangedListener);
    }

    public void setTitle(int i) {
        setTitle(getResources().getString(i));
    }

    public void setTitle(String str) {
        this.f6706a.setText(str);
    }
}
