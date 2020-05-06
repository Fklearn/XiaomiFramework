package com.miui.permcenter.autostart;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.CompoundButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b.b.c.j.x;
import com.miui.common.stickydecoration.b.c;
import com.miui.common.stickydecoration.f;
import com.miui.permcenter.autostart.b;
import com.miui.permcenter.s;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miuix.recyclerview.widget.RecyclerView;

public class AutoStartDetailManagementActivity extends b.b.c.c.a implements b.a, CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private RecyclerView f6040a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public String f6041b;

    /* renamed from: c  reason: collision with root package name */
    private String f6042c;

    /* renamed from: d  reason: collision with root package name */
    private int f6043d;
    /* access modifiers changed from: private */
    public int e;
    /* access modifiers changed from: private */
    public boolean f;
    private Handler g = new Handler();
    /* access modifiers changed from: private */
    public b h;
    private RecyclerView.f i;
    private a j;

    static class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private int f6044a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f6045b;

        /* renamed from: c  reason: collision with root package name */
        private WeakReference<AutoStartDetailManagementActivity> f6046c;

        a(int i, boolean z, AutoStartDetailManagementActivity autoStartDetailManagementActivity) {
            this.f6044a = i;
            this.f6045b = z;
            this.f6046c = new WeakReference<>(autoStartDetailManagementActivity);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            AutoStartDetailManagementActivity autoStartDetailManagementActivity;
            if (!isCancelled() && (autoStartDetailManagementActivity = (AutoStartDetailManagementActivity) this.f6046c.get()) != null && !autoStartDetailManagementActivity.isFinishing() && !autoStartDetailManagementActivity.isDestroyed()) {
                int i = this.f6044a;
                if (i == 1) {
                    PermissionManager.getInstance(autoStartDetailManagementActivity.getApplicationContext()).setApplicationPermission(PermissionManager.PERM_ID_AUTOSTART, autoStartDetailManagementActivity.e, autoStartDetailManagementActivity.f6041b);
                    if (!this.f6045b) {
                        x.a((ActivityManager) autoStartDetailManagementActivity.getSystemService("activity"), autoStartDetailManagementActivity.f6041b);
                    }
                } else if (i == 2) {
                    s.a(autoStartDetailManagementActivity.getApplicationContext(), autoStartDetailManagementActivity.f6041b, autoStartDetailManagementActivity.f);
                }
            }
            return null;
        }
    }

    private void a(int i2, boolean z) {
        int i3 = 1;
        if (i2 == 1) {
            if (z) {
                i3 = 3;
            }
            this.e = i3;
        } else if (i2 == 2) {
            this.f = z;
        }
        this.j = new a(i2, z, this);
        this.j.execute(new Void[0]);
    }

    /* access modifiers changed from: private */
    public void l() {
        this.f6040a.b(this.i);
        this.i = f.a.a((c) new d(this)).a();
        this.f6040a.a(this.i);
    }

    private Intent m() {
        Intent intent = new Intent();
        intent.putExtra("pkg_position", this.f6043d);
        intent.putExtra("auto_start_detail_result_permission_action", this.e);
        intent.putExtra("auto_start_detail_result_wakepath_accepted", this.f);
        return intent;
    }

    private void n() {
        this.g.post(new c(this));
    }

    public void a(int i2, b.C0057b bVar) {
        boolean z = !bVar.f6066b.isChecked();
        a(bVar.f6065a, z);
        bVar.f6066b.setChecked(z);
    }

    public void onBackPressed() {
        setResult(-1, m());
        finish();
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        b.C0057b bVar = (b.C0057b) compoundButton.getTag();
        if (bVar != null) {
            a(bVar.f6065a, z);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, com.miui.permcenter.autostart.b$a, miui.app.Activity, android.widget.CompoundButton$OnCheckedChangeListener, com.miui.permcenter.autostart.AutoStartDetailManagementActivity] */
    public void onCreate(Bundle bundle) {
        Bundle extras;
        super.onCreate(bundle);
        setContentView(R.layout.pm_activity_auto_start_detail_management);
        if (!(getIntent() == null || (extras = getIntent().getExtras()) == null)) {
            this.f6041b = extras.getString("pkg_name", "");
            this.f6042c = extras.getString("pkg_label", "");
            this.e = extras.getInt("action", 3);
            this.f6043d = extras.getInt("pkg_position", -1);
            this.f = extras.getBoolean("white_list", false);
            if (!TextUtils.isEmpty(this.f6042c)) {
                setTitle(this.f6042c);
            }
        }
        this.f6040a = (miuix.recyclerview.widget.RecyclerView) findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.j(1);
        this.f6040a.setLayoutManager(linearLayoutManager);
        this.h = new b(this);
        this.h.a((CompoundButton.OnCheckedChangeListener) this);
        this.h.a((b.a) this);
        this.f6040a.setAdapter(this.h);
        n();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        AutoStartDetailManagementActivity.super.onDestroy();
        a aVar = this.j;
        if (aVar != null) {
            aVar.cancel(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return AutoStartDetailManagementActivity.super.onOptionsItemSelected(menuItem);
        }
        setResult(-1, m());
        finish();
        return true;
    }
}
