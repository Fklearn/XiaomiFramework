package com.miui.antivirus.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import b.b.b.a.b;
import b.b.b.d.n;
import b.b.c.j.r;
import com.miui.securitycenter.R;
import java.util.ArrayList;

public class MonitorSafeResultView extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private Button f2942a;

    /* renamed from: b  reason: collision with root package name */
    private Context f2943b;

    /* renamed from: c  reason: collision with root package name */
    private ImageView[] f2944c = new ImageView[5];

    public MonitorSafeResultView(Context context) {
        super(context);
        this.f2943b = context;
    }

    public MonitorSafeResultView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f2943b = context;
    }

    public void a() {
        ImageView imageView;
        ArrayList<String> c2 = n.c(this.f2943b);
        PackageManager packageManager = this.f2943b.getPackageManager();
        this.f2944c[0].setVisibility(4);
        this.f2944c[1].setVisibility(4);
        this.f2944c[2].setVisibility(4);
        this.f2944c[3].setVisibility(4);
        this.f2944c[4].setVisibility(4);
        if (c2.isEmpty()) {
            this.f2944c[0].setVisibility(0);
            this.f2944c[0].setImageResource(R.drawable.sp_app_add);
            this.f2944c[0].setOnClickListener(this);
            imageView = this.f2944c[0];
        } else if (c2.size() > 4) {
            this.f2944c[0].setVisibility(0);
            r.a("pkg_icon://" + c2.get(0), this.f2944c[0], r.f);
            this.f2944c[1].setVisibility(0);
            r.a("pkg_icon://" + c2.get(1), this.f2944c[1], r.f);
            this.f2944c[2].setVisibility(0);
            r.a("pkg_icon://" + c2.get(2), this.f2944c[2], r.f);
            this.f2944c[3].setVisibility(0);
            r.a("pkg_icon://" + c2.get(3), this.f2944c[3], r.f);
            this.f2944c[4].setVisibility(0);
            this.f2944c[4].setImageResource(R.drawable.sp_app_add);
            this.f2944c[4].setOnClickListener(this);
            this.f2944c[4].setContentDescription(this.f2943b.getString(R.string.monitor_app_more));
            for (int i = 0; i < 4; i++) {
                try {
                    this.f2944c[i].setContentDescription(packageManager.getApplicationInfo(c2.get(i), 0).loadLabel(packageManager));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return;
        } else {
            int i2 = 0;
            while (i2 < c2.size()) {
                this.f2944c[i2].setVisibility(0);
                r.a("pkg_icon://" + c2.get(i2), this.f2944c[i2], r.f);
                try {
                    this.f2944c[i2].setContentDescription(packageManager.getApplicationInfo(c2.get(i2), 0).loadLabel(packageManager));
                } catch (PackageManager.NameNotFoundException e2) {
                    e2.printStackTrace();
                }
                i2++;
            }
            this.f2944c[i2].setVisibility(0);
            this.f2944c[i2].setImageResource(R.drawable.sp_app_add);
            this.f2944c[i2].setOnClickListener(this);
            imageView = this.f2944c[i2];
        }
        imageView.setContentDescription(this.f2943b.getString(R.string.monitor_app_add));
    }

    public void onClick(View view) {
        this.f2943b.startActivity(new Intent("miui.intent.action.SAFE_PAY_MONITOR_SETTINGS"));
        b.C0023b.c();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.f2942a = (Button) findViewById(R.id.btn_optimize);
        this.f2942a.setOnClickListener(this);
        this.f2944c[0] = (ImageView) findViewById(R.id.icon1);
        this.f2944c[1] = (ImageView) findViewById(R.id.icon2);
        this.f2944c[2] = (ImageView) findViewById(R.id.icon3);
        this.f2944c[3] = (ImageView) findViewById(R.id.icon4);
        this.f2944c[4] = (ImageView) findViewById(R.id.icon5);
    }
}
