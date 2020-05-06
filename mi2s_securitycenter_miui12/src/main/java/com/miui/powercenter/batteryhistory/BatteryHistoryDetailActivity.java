package com.miui.powercenter.batteryhistory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import b.b.c.c.a;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import miui.app.ProgressDialog;
import miuix.recyclerview.widget.RecyclerView;

public class BatteryHistoryDetailActivity extends a {

    /* renamed from: a  reason: collision with root package name */
    private boolean f6794a;

    /* renamed from: b  reason: collision with root package name */
    private ProgressDialog f6795b;

    /* renamed from: c  reason: collision with root package name */
    private C0517v f6796c;

    /* renamed from: d  reason: collision with root package name */
    private C0508l f6797d;

    private void a(Context context) {
        if (this.f6795b == null) {
            this.f6795b = ProgressDialog.show(context, (CharSequence) null, getResources().getString(R.string.battery_charge_estimating), true, true);
        }
    }

    public C0517v l() {
        return this.f6796c;
    }

    public void m() {
        ProgressDialog progressDialog = this.f6795b;
        if (progressDialog != null) {
            progressDialog.cancel();
            this.f6795b = null;
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity, miui.app.Activity, android.app.Activity] */
    public void onBackPressed() {
        BatteryHistoryDetailActivity.super.onBackPressed();
        if (this.f6794a) {
            o.a((Activity) this);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, com.miui.powercenter.batteryhistory.BatteryHistoryDetailActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.pc_activity_battery_history);
        if (getIntent().getBooleanExtra("overried_transition", false)) {
            this.f6794a = true;
        }
        a(this);
        this.f6796c = new C0517v(this);
        this.f6796c.a();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.f6797d = new C0508l(this);
        this.f6797d.b();
        recyclerView.setAdapter(this.f6797d);
        com.miui.powercenter.a.a.d();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        BatteryHistoryDetailActivity.super.onDestroy();
        this.f6796c.b();
        this.f6797d.c();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }
}
