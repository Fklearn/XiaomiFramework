package com.miui.optimizecenter.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import b.b.c.c.a;
import b.b.c.j.A;
import b.b.i.b.e;
import b.b.i.b.f;
import com.miui.cleanmaster.g;
import com.miui.optimizecenter.storage.d.c;
import com.miui.optimizecenter.storage.view.PreferenceCategoryView;
import com.miui.optimizecenter.storage.view.PreferenceListView;
import com.miui.optimizecenter.storage.view.StorageActionBar;
import com.miui.optimizecenter.storage.view.StorageScrollView;
import com.miui.optimizecenter.widget.storage.StorageViewGroup;
import com.miui.optimizecenter.widget.storage.d;
import com.miui.securitycenter.R;
import java.util.List;

public class StorageActivity extends a implements l, View.OnClickListener, StorageScrollView.a, StorageActionBar.a, PreferenceCategoryView.a {

    /* renamed from: a  reason: collision with root package name */
    private StorageActionBar f5692a;

    /* renamed from: b  reason: collision with root package name */
    private StorageScrollView f5693b;

    /* renamed from: c  reason: collision with root package name */
    private StorageViewGroup f5694c;

    /* renamed from: d  reason: collision with root package name */
    private TextView f5695d;
    private TextView e;
    private d f;
    private s g;
    private v h = v.DEFAULT;
    private PreferenceListView i;
    private boolean j;
    private final BroadcastReceiver k = new p(this);

    private void a(Intent intent) {
        v vVar;
        if (intent != null) {
            String stringExtra = intent.getStringExtra("key_channel");
            if (TextUtils.equals(stringExtra, "miui_file_explore")) {
                vVar = v.FILE_EXPLORE;
            } else {
                if (TextUtils.equals(stringExtra, "miui_settings")) {
                    vVar = v.MIUI_SETTINGS;
                }
                e.a(stringExtra);
                Log.i("StorageActivity", "StorageStyle=" + this.h);
            }
            this.h = vVar;
            e.a(stringExtra);
            Log.i("StorageActivity", "StorageStyle=" + this.h);
        }
    }

    private void b(u uVar) {
        long j2 = uVar.a().f5755c;
        switch (q.f5767a[uVar.ordinal()]) {
            case 1:
                this.f.a(j2);
                return;
            case 2:
                this.f.d(j2);
                return;
            case 3:
                this.f.f(j2);
                return;
            case 4:
                this.f.h(j2);
                return;
            case 5:
                this.f.g(j2);
                return;
            case 6:
                this.f.b(j2);
                return;
            case 7:
                this.f.c(j2);
                return;
            case 8:
                this.f.e(j2);
                return;
            default:
                return;
        }
    }

    private void l() {
        if (!this.j) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
            intentFilter.addAction("android.intent.action.MEDIA_EJECT");
            intentFilter.addDataScheme("file");
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.os.storage.action.VOLUME_STATE_CHANGED");
            registerReceiver(this.k, intentFilter);
            registerReceiver(this.k, intentFilter2);
            this.j = true;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.optimizecenter.storage.view.PreferenceCategoryView$a, com.miui.optimizecenter.storage.StorageActivity] */
    /* access modifiers changed from: private */
    public void m() {
        PreferenceListView preferenceListView = this.i;
        if (preferenceListView != null) {
            preferenceListView.a(c.a((Context) this).a(), this);
        }
    }

    public void a() {
        StorageActivity.super.onBackPressed();
    }

    public void a(View view, int i2, int i3, int i4, int i5) {
        StorageActionBar storageActionBar = this.f5692a;
        if (storageActionBar != null) {
            storageActionBar.a(i3);
        }
    }

    public void a(u uVar) {
        b(uVar);
        this.f5694c.a(this.f);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, android.app.Activity, com.miui.optimizecenter.storage.StorageActivity] */
    public void a(PreferenceCategoryView preferenceCategoryView, com.miui.optimizecenter.storage.view.a aVar) {
        Log.i("StorageActivity", "onPreferenceClicked: " + aVar);
        int i2 = q.f5768b[aVar.ordinal()];
        if (i2 == 1) {
            return;
        }
        if (i2 == 2) {
            com.miui.optimizecenter.storage.b.c.a(this, preferenceCategoryView.getmVolumeInfo().b());
        } else if (i2 == 3) {
            startActivity(new Intent(this, StorageSettingsActivity.class));
        }
    }

    public void g() {
        for (u b2 : s.f5771b) {
            b(b2);
        }
        this.f5694c.a(this.f);
        this.g.a(false);
    }

    /* JADX WARNING: type inference failed for: r12v0, types: [android.content.Context, miui.app.Activity, com.miui.optimizecenter.storage.StorageActivity] */
    public void i() {
        long e2 = AppSystemDataManager.a((Context) this).e();
        long b2 = e2 - AppSystemDataManager.a((Context) this).b();
        this.e.setText(getString(R.string.storage_available_total_size1, new Object[]{f.a(this, b2, "%.1f"), f.a(this, e2, "%.0f")}));
        this.f5695d.setText(getString(R.string.storage_available_total_size1, new Object[]{f.a(this, b2, "%.1f"), f.a(this, e2, "%.0f")}));
    }

    public void j() {
        this.f5694c.setScanFinished(true);
        this.f5694c.a(this.f);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, miui.app.Activity, com.miui.optimizecenter.storage.StorageActivity] */
    public void onClick(View view) {
        String str;
        if (this.f5694c.a()) {
            switch (view.getId()) {
                case R.id.storage_app_details /*2131297730*/:
                    startActivity(new Intent("miui.intent.action.STORAGE_APP_INFO_LIST"));
                    str = "appStorageDetails";
                    break;
                case R.id.storage_deepclean_entry /*2131297731*/:
                    Intent intent = new Intent("miui.intent.action.GARBAGE_DEEPCLEAN");
                    intent.putExtra("enter_homepage_way", "storage_main");
                    g.b(this, intent);
                    str = "deepcleanEntry";
                    break;
                default:
                    return;
            }
            e.b(str);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, com.miui.optimizecenter.storage.view.StorageScrollView$a, android.view.View$OnClickListener, miui.app.Activity, com.miui.optimizecenter.storage.view.StorageActionBar$a, com.miui.optimizecenter.storage.l, com.miui.optimizecenter.storage.StorageActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        a(getIntent());
        this.g = s.a((Context) this);
        this.g.a((l) this);
        setContentView(R.layout.activity_storage);
        this.f5692a = (StorageActionBar) findViewById(R.id.title_content);
        this.f5692a.setBackClickListener(this);
        this.f5693b = (StorageScrollView) findViewById(R.id.scroll);
        this.f5693b.setOnScrollListener(this);
        this.f5694c = (StorageViewGroup) findViewById(R.id.column_view);
        this.f5694c.setStorageStyle(this.h);
        this.f5695d = (TextView) findViewById(R.id.summary1);
        this.e = (TextView) findViewById(R.id.summary2);
        View findViewById = findViewById(R.id.storage_deepclean_entry);
        findViewById.setOnClickListener(this);
        if (A.a()) {
            b.b.i.b.a.b(findViewById);
        }
        View findViewById2 = findViewById(R.id.storage_app_details);
        findViewById2.setOnClickListener(this);
        if (A.a()) {
            b.b.i.b.a.b(findViewById2);
        }
        this.i = (PreferenceListView) findViewById(R.id.external_panel);
        long e2 = AppSystemDataManager.a((Context) this).e();
        i();
        this.f = new d(e2);
        this.f5694c.setStorageInfo(this.f);
        List<u> b2 = this.g.b();
        if (b2 == null || b2.size() <= 0) {
            this.g.h();
        } else {
            for (u a2 : b2) {
                a(a2);
            }
            j();
        }
        m();
        l();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        StorageActivity.super.onDestroy();
        if (this.j) {
            try {
                unregisterReceiver(this.k);
                this.j = false;
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        StorageActivity.super.onNewIntent(intent);
        a(intent);
        this.f5694c.setStorageStyle(this.h);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.g.a(true);
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        StorageActivity.super.onRestart();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.g.c()) {
            this.g.f();
        }
        PreferenceListView preferenceListView = this.i;
        if (preferenceListView != null) {
            preferenceListView.a();
        }
    }
}
