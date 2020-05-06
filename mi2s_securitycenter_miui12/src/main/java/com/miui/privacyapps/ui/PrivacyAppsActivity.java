package com.miui.privacyapps.ui;

import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.b.b;
import b.b.c.j.g;
import b.b.c.j.r;
import b.b.k.c;
import com.miui.applicationlock.PrivacyAppsConfirmAccessControl;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.security.SecurityManager;

public class PrivacyAppsActivity extends b.b.c.c.a implements View.OnClickListener, AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<c>> {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f7375a = "PrivacyAppsActivity";

    /* renamed from: b  reason: collision with root package name */
    private TextView f7376b;

    /* renamed from: c  reason: collision with root package name */
    private TextView f7377c;

    /* renamed from: d  reason: collision with root package name */
    private GridView f7378d;
    private a e;
    private b.b.k.b.a f;
    private PackageManager g;
    /* access modifiers changed from: private */
    public SecurityManager h;
    private boolean i = false;
    private boolean j = false;
    /* access modifiers changed from: private */
    public List<c> k = new ArrayList();
    private final BroadcastReceiver l = new a(this);

    class a extends BaseAdapter {

        /* renamed from: a  reason: collision with root package name */
        private LayoutInflater f7379a;

        /* renamed from: b  reason: collision with root package name */
        private b f7380b;

        /* renamed from: com.miui.privacyapps.ui.PrivacyAppsActivity$a$a  reason: collision with other inner class name */
        class C0065a {

            /* renamed from: a  reason: collision with root package name */
            ImageView f7382a;

            /* renamed from: b  reason: collision with root package name */
            TextView f7383b;

            C0065a() {
            }
        }

        public a(Context context) {
            this.f7379a = LayoutInflater.from(context);
            this.f7380b = b.a(context);
        }

        public int getCount() {
            return PrivacyAppsActivity.this.k.size();
        }

        public Object getItem(int i) {
            return PrivacyAppsActivity.this.k.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            C0065a aVar;
            String str;
            String str2;
            if (view == null) {
                view = this.f7379a.inflate(R.layout.privacy_apps_grid_item, (ViewGroup) null);
                aVar = new C0065a();
                aVar.f7382a = (ImageView) view.findViewById(R.id.image);
                aVar.f7383b = (TextView) view.findViewById(R.id.label);
                view.setTag(aVar);
            } else {
                aVar = (C0065a) view.getTag();
            }
            c cVar = (c) PrivacyAppsActivity.this.k.get(i);
            if (cVar.e() == 999) {
                str2 = cVar.c();
                str = "pkg_icon_xspace://";
            } else {
                str2 = cVar.c();
                str = "pkg_icon://";
            }
            r.a(str.concat(str2), aVar.f7382a, r.f);
            try {
                aVar.f7383b.setText(this.f7380b.a(cVar.c()).a());
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(PrivacyAppsActivity.f7375a, "getAppInfo error", e);
            }
            return view;
        }
    }

    /* renamed from: a */
    public void onLoadFinished(Loader<List<c>> loader, List<c> list) {
        this.k = new ArrayList(list);
        this.e.notifyDataSetChanged();
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i2, int i3, Intent intent) {
        PrivacyAppsActivity.super.onActivityResult(i2, i3, intent);
        if (i2 != 2020 && i2 != 2021) {
            return;
        }
        if (i3 == -1) {
            this.i = false;
        } else {
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsActivity, miui.app.Activity] */
    public void onClick(View view) {
        if (view == this.f7376b) {
            finish();
        } else if (view == this.f7377c) {
            Intent intent = new Intent(this, PrivacyAppsManageActivity.class);
            intent.putExtra("enter_from_privacyapps_page", true);
            startActivityForResult(intent, 2021);
        }
    }

    /* JADX WARNING: type inference failed for: r6v0, types: [b.b.c.c.a, android.content.Context, android.app.LoaderManager$LoaderCallbacks, android.view.View$OnClickListener, com.miui.privacyapps.ui.PrivacyAppsActivity, miui.app.Activity, android.widget.AdapterView$OnItemClickListener] */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.privacy_apps_layout);
        if (bundle != null && bundle.containsKey("needConfirmed")) {
            this.i = true;
        }
        com.miui.securityscan.f.c.b(getWindow());
        this.f = new b.b.k.b.a(this);
        this.g = getPackageManager();
        this.h = (SecurityManager) getSystemService("security");
        this.f7376b = (TextView) findViewById(R.id.close);
        this.f7376b.setOnClickListener(this);
        this.f7377c = (TextView) findViewById(R.id.manage);
        this.f7377c.setOnClickListener(this);
        this.f7378d = (GridView) findViewById(R.id.privacy_apps_gridview);
        this.e = new a(this);
        this.f7378d.setAdapter(this.e);
        this.f7378d.setOnItemClickListener(this);
        this.f7378d.setSelector(new ColorDrawable(0));
        Loader loader = getLoaderManager().getLoader(321);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(321, (Bundle) null, this);
        if (!(Build.VERSION.SDK_INT < 24 || bundle == null || loader == null)) {
            loaderManager.restartLoader(321, (Bundle) null, this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        registerReceiver(this.l, intentFilter);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsActivity] */
    public Loader<List<c>> onCreateLoader(int i2, Bundle bundle) {
        return new b(this, this);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PrivacyAppsActivity.super.onDestroy();
        unregisterReceiver(this.l);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsActivity, miui.app.Activity] */
    public void onItemClick(AdapterView<?> adapterView, View view, int i2, long j2) {
        c cVar = this.k.get(i2);
        String c2 = cVar.c();
        try {
            g.b((Context) this, this.f.a(this.g, c2, cVar.e()), new UserHandle(cVar.e()));
            finish();
        } catch (Exception e2) {
            Log.e(f7375a, "startPrivacyApps error", e2);
        }
        b.b.k.a.a.b(c2);
    }

    public void onLoaderReset(Loader<List<c>> loader) {
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        getWindow().clearFlags(8192);
        this.i = true;
        this.j = true;
    }

    public void onResume() {
        super.onResume();
        if (this.j) {
            getLoaderManager().restartLoader(321, (Bundle) null, this);
        }
        getWindow().addFlags(8192);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        PrivacyAppsActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean("needConfirmed", this.i);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        PrivacyAppsActivity.super.onStart();
        if (this.i) {
            startActivityForResult(new Intent(this, PrivacyAppsConfirmAccessControl.class), 2020);
        }
    }
}
