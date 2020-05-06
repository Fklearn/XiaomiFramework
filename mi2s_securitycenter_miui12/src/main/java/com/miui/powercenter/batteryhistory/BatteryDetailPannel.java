package com.miui.powercenter.batteryhistory;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.powercenter.utils.i;
import com.miui.securitycenter.R;
import java.util.Observable;
import java.util.Observer;
import miui.widget.DropDownSingleChoiceMenu;

public class BatteryDetailPannel extends RelativeLayout implements Observer {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f6783a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public LinearLayout f6784b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextView f6785c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public C0500d f6786d;
    /* access modifiers changed from: private */
    public W e;
    /* access modifiers changed from: private */
    public boolean f;
    private boolean g;
    /* access modifiers changed from: private */
    public String[] h;
    /* access modifiers changed from: private */
    public int i;

    public BatteryDetailPannel(Context context) {
        this(context, (AttributeSet) null);
    }

    public BatteryDetailPannel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BatteryDetailPannel(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f = true;
        this.g = true;
        this.i = 0;
        this.f6783a = context;
        a();
    }

    private void a() {
        LayoutInflater.from(this.f6783a).inflate(R.layout.pc_battery_history_pannel, this);
        this.f6784b = (LinearLayout) findViewById(R.id.container);
        this.f6786d = new C0500d(this.f6783a);
        this.f6784b.addView(this.f6786d);
        this.h = getResources().getStringArray(R.array.pc_battery_history_spinner_choice);
        this.f6785c = (TextView) findViewById(R.id.spinner_choice);
        this.f6785c.setOnClickListener(new C0502f(this));
        i.a().addObserver(this);
    }

    /* access modifiers changed from: private */
    public void a(View view) {
        DropDownSingleChoiceMenu dropDownSingleChoiceMenu = new DropDownSingleChoiceMenu(this.f6783a);
        dropDownSingleChoiceMenu.setItems(this.h);
        dropDownSingleChoiceMenu.setSelectedItem(this.i);
        dropDownSingleChoiceMenu.setAnchorView(view);
        dropDownSingleChoiceMenu.setOnMenuListener(new C0503g(this));
        dropDownSingleChoiceMenu.show();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        i.a().deleteObserver(this);
    }

    public void update(Observable observable, Object obj) {
        if (obj instanceof Message) {
            Message message = (Message) obj;
            int i2 = message.what;
            if (i2 == 10003) {
                if (this.f) {
                    this.f6786d.a(message.arg1, message.arg2);
                } else {
                    this.e.a(message.arg1, message.arg2);
                }
            } else if (i2 == 10004) {
                int i3 = message.arg1;
                boolean z = true;
                if (i3 != 1) {
                    z = false;
                }
                if (this.g != z) {
                    this.g = z;
                    this.f6786d.a(this.g);
                }
            }
        }
    }
}
