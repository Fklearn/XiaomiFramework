package com.miui.antivirus.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import b.b.b.b;
import b.b.c.c.a;
import b.b.c.j.r;
import com.miui.antivirus.model.k;
import com.miui.securitycenter.R;

public class VirusMonitorDialogActivity extends a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private LinearLayout f2707a;

    /* renamed from: b  reason: collision with root package name */
    private ImageView f2708b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f2709c;

    /* renamed from: d  reason: collision with root package name */
    private k f2710d;
    private TextView e;
    private Button f;
    private Button g;

    private String a(b.c cVar) {
        int i;
        if (cVar == b.c.RISK) {
            i = R.string.antivirus_monitor_btn_text_clean_risk;
        } else if (cVar != b.c.VIRUS) {
            return null;
        } else {
            i = R.string.antivirus_monitor_btn_text_clean_virus;
        }
        return getString(i);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antivirus.activity.VirusMonitorDialogActivity] */
    private void a(k kVar) {
        b.a((Context) this).a(kVar);
    }

    private String b(b.c cVar) {
        int i;
        if (cVar == b.c.RISK) {
            i = R.string.antivirus_monitor_risk_advice;
        } else if (cVar != b.c.VIRUS) {
            return null;
        } else {
            i = R.string.antivirus_monitor_virus_advice;
        }
        return getString(i);
    }

    private void b(k kVar) {
        r.a("pkg_icon://" + kVar.b(), this.f2708b, r.f);
        this.f2709c.setText(kVar.b());
        this.e.setText(b(kVar.d()));
        this.f.setText(a(kVar.d()));
    }

    private void l() {
        finish();
        overridePendingTransition(0, 0);
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService("activity");
            Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", new Class[]{String.class}).invoke(activityManager, new Object[]{"com.google.android.packageinstaller"});
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void m() {
        this.f2708b = (ImageView) findViewById(R.id.monitor_virus_icon);
        this.f2709c = (TextView) findViewById(R.id.monitor_virus_packagename);
        this.e = (TextView) findViewById(R.id.monitor_virus_description);
        this.f = (Button) findViewById(R.id.monitor_clean);
        this.g = (Button) findViewById(R.id.monitor_cancel);
        this.f2707a = (LinearLayout) findViewById(R.id.monitor_layout);
        this.f.setOnClickListener(this);
        this.g.setOnClickListener(this);
        this.f2707a.setOnClickListener(this);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.antivirus.activity.VirusMonitorDialogActivity] */
    private void n() {
        startActivity(new Intent(this, MainActivity.class));
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, miui.app.Activity, com.miui.antivirus.activity.VirusMonitorDialogActivity] */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.monitor_cancel /*2131297340*/:
                finish();
                overridePendingTransition(0, 0);
                return;
            case R.id.monitor_clean /*2131297341*/:
                a(this.f2710d);
                Toast.makeText(this, getResources().getString(R.string.antivirus_monitor_clean_tips, new Object[]{this.f2710d.a()}), 0).show();
                break;
            case R.id.monitor_layout /*2131297342*/:
                n();
                break;
            default:
                return;
        }
        l();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.v_activity_monitor_layout);
        if (Build.VERSION.SDK_INT != 26) {
            setRequestedOrientation(1);
        }
        m();
        this.f2710d = (k) getIntent().getExtras().get("virus_info_key");
        b(this.f2710d);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
