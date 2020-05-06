package com.miui.antivirus.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import miui.app.AlertDialog;

class t extends AlertDialog {

    /* renamed from: a  reason: collision with root package name */
    private View f2998a = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.sp_warning_dialog, (ViewGroup) null);

    /* renamed from: b  reason: collision with root package name */
    private CheckBox f2999b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f3000c;

    /* renamed from: d  reason: collision with root package name */
    private int f3001d;

    public t(Context context) {
        super(context, 2131821043);
        setView(this.f2998a);
        this.f2999b = (CheckBox) this.f2998a.findViewById(R.id.noLongerRemind);
        this.f3000c = (TextView) this.f2998a.findViewById(R.id.tips);
        Window window = getWindow();
        window.setType(2003);
        window.setFlags(131072, 131072);
        setCanceledOnTouchOutside(true);
    }

    public void a(int i, ArrayList<Integer> arrayList) {
        int i2;
        View view;
        this.f3001d = i;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            int intValue = arrayList.get(i3).intValue();
            if (intValue == 2) {
                view = this.f2998a;
                i2 = R.id.risk_root;
            } else if (intValue == 3) {
                view = this.f2998a;
                i2 = R.id.risk_sign;
            } else if (intValue == 4) {
                view = this.f2998a;
                i2 = R.id.risk_virus;
            } else if (intValue == 5) {
                view = this.f2998a;
                i2 = R.id.risk_messaging;
            } else if (intValue != 6) {
            } else {
                view = this.f2998a;
                i2 = R.id.risk_wifi;
            }
            view.findViewById(i2).setVisibility(0);
        }
    }

    public void a(String str) {
        this.f3000c.setText(str);
        this.f3000c.setVisibility(0);
    }

    public void a(boolean z) {
        this.f2999b.setVisibility(z ? 0 : 8);
    }

    public boolean a() {
        return this.f2999b.isChecked();
    }

    public int b() {
        return this.f3001d;
    }
}
