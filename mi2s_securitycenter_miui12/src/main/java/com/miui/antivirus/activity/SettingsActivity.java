package com.miui.antivirus.activity;

import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.b.b;
import b.b.b.d.h;
import b.b.b.d.n;
import b.b.b.i;
import b.b.b.p;
import b.b.c.c.b.l;
import b.b.c.j.f;
import com.miui.antivirus.service.GuardService;
import com.miui.antivirus.whitelist.WhiteListActivity;
import com.miui.antivirus.whitelist.j;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.UpdateInfo;
import com.miui.guardprovider.b;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miui.app.Activity;
import miui.app.AlertDialog;
import miui.app.ProgressDialog;
import miui.os.Build;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class SettingsActivity extends b.b.c.c.a {

    /* renamed from: a  reason: collision with root package name */
    private c f2686a;

    private static class a extends AsyncTask<Void, Void, List<b.a>> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f2687a;

        a(c cVar) {
            this.f2687a = new WeakReference<>(cVar);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public List<b.a> doInBackground(Void... voidArr) {
            c cVar = (c) this.f2687a.get();
            if (cVar == null || cVar.t == null) {
                return null;
            }
            return cVar.t.d();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(List<b.a> list) {
            c cVar = (c) this.f2687a.get();
            if (cVar != null && !cVar.isRemoving() && !cVar.isDetached()) {
                cVar.a(list);
            }
        }
    }

    private static class b extends b.b.c.i.a<Pair> {

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<Context> f2688b;

        b(Context context) {
            super(context);
            this.f2688b = new WeakReference<>(context);
        }

        public Pair loadInBackground() {
            Context context = (Context) this.f2688b.get();
            if (context == null) {
                return new Pair(0, 0);
            }
            return new Pair(Integer.valueOf(j.a(context).c()), Integer.valueOf(n.i(context)));
        }
    }

    public static class c extends l implements Preference.c, Preference.b, LoaderManager.LoaderCallbacks<Pair> {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public SettingsActivity f2689a;

        /* renamed from: b  reason: collision with root package name */
        private PreferenceCategory f2690b;

        /* renamed from: c  reason: collision with root package name */
        private TextPreference f2691c;

        /* renamed from: d  reason: collision with root package name */
        private DropDownPreference f2692d;
        private CheckBoxPreference e;
        private CheckBoxPreference f;
        private CheckBoxPreference g;
        private TextPreference h;
        private PreferenceCategory i;
        private PreferenceCategory j;
        private CheckBoxPreference k;
        private CheckBoxPreference l;
        private CheckBoxPreference m;
        private CheckBoxPreference n;
        private CheckBoxPreference o;
        private TextPreference p;
        private TextPreference q;
        private ProgressDialog r;
        private com.miui.guardprovider.b s;
        /* access modifiers changed from: private */
        public b.b.b.b t;
        /* access modifiers changed from: private */
        public i u;
        /* access modifiers changed from: private */
        public e v;
        private a w;
        private d x;
        /* access modifiers changed from: private */
        public List<b.a> y = new ArrayList();

        private String a(long j2) {
            if (j2 == 0) {
                return getString(R.string.hints_virus_lib_update_default_summary);
            }
            return getString(R.string.menu_item_virus_lib_auto_update_summary, new Object[]{DateFormat.format("yyyy-MM-dd", j2)});
        }

        private void a(int i2) {
            Toast.makeText(this.f2689a.getApplicationContext(), i2, 0).show();
            ProgressDialog progressDialog = this.r;
            if (progressDialog != null && progressDialog.isShowing() && !this.f2689a.isFinishing() && !this.f2689a.isDestroyed()) {
                try {
                    this.r.dismiss();
                    this.r = null;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        /* access modifiers changed from: private */
        public void a(UpdateInfo updateInfo) {
            int i2 = updateInfo.updateResult;
            int i3 = R.string.antivirus_toast_update_failed;
            if (i2 == 0) {
                this.u.a(System.currentTimeMillis(), updateInfo.engineName);
                this.u.b(System.currentTimeMillis());
                this.g.setSummary((CharSequence) a(com.miui.common.persistence.b.a(getString(R.string.preference_key_database_auto_update_time, new Object[]{updateInfo.engineName}), 0)));
                i3 = R.string.antivirus_toast_update_success;
            } else if (i2 != 2 && i2 == 3) {
                this.u.a(System.currentTimeMillis(), updateInfo.engineName);
                this.u.b(System.currentTimeMillis());
                this.g.setSummary((CharSequence) a(com.miui.common.persistence.b.a(getString(R.string.preference_key_database_auto_update_time, new Object[]{updateInfo.engineName}), 0)));
                i3 = R.string.antivirus_toast_already_update;
            }
            a(i3);
        }

        /* access modifiers changed from: private */
        public void a(String str, List<b.a> list) {
            this.s.b((b.a) new C(this, list, str));
        }

        /* access modifiers changed from: private */
        public void a(List<b.a> list) {
            if (list != null) {
                this.y = list;
                if (this.f2692d != null) {
                    c();
                }
                for (b.a next : list) {
                    if (next.f1475d) {
                        TextPreference textPreference = this.f2691c;
                        if (textPreference != null) {
                            textPreference.a(next.f1473b);
                        }
                        p.b(next.f1472a);
                        String string = getString(R.string.preference_key_database_auto_update_enabled, new Object[]{next.f1472a});
                        this.g.setTitle((CharSequence) getString(R.string.virus_auto_update_engine, new Object[]{next.f1473b}));
                        this.g.setSummary((CharSequence) a(com.miui.common.persistence.b.a(getString(R.string.preference_key_database_auto_update_time, new Object[]{next.f1472a}), 0)));
                        this.g.setChecked(p.a(string));
                        this.g.setOnPreferenceChangeListener(new A(this, string));
                        if (!next.e || Build.IS_INTERNATIONAL_BUILD) {
                            this.f2690b.d(this.e);
                        } else {
                            this.f2690b.b((Preference) this.e);
                        }
                    }
                }
                if (list.isEmpty() || b.b.b.d.l.a(this.f2689a.getApplicationContext(), "com.miui.guardprovider") < 101) {
                    this.f2690b.d(this.h);
                } else {
                    this.f2690b.b((Preference) this.h);
                }
            }
        }

        private void c() {
            List<b.a> list = this.y;
            if (list != null) {
                String[] strArr = new String[list.size()];
                String[] strArr2 = new String[this.y.size()];
                int i2 = -1;
                for (int i3 = 0; i3 < this.y.size(); i3++) {
                    b.a aVar = this.y.get(i3);
                    strArr[i3] = getString(R.string.antivirus_choose_engine, new Object[]{aVar.f1473b});
                    strArr2[i3] = String.valueOf(i3);
                    if (aVar.f1475d) {
                        i2 = i3;
                    }
                }
                this.f2692d.a((CharSequence[]) strArr);
                this.f2692d.b((CharSequence[]) strArr2);
                this.f2692d.a(i2);
            }
        }

        /* JADX WARNING: type inference failed for: r4v2, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        private void d() {
            List<b.a> list = this.y;
            if (list != null) {
                String[] strArr = new String[list.size()];
                int i2 = -1;
                for (int i3 = 0; i3 < this.y.size(); i3++) {
                    b.a aVar = this.y.get(i3);
                    strArr[i3] = getString(R.string.antivirus_choose_engine, new Object[]{aVar.f1473b});
                    if (aVar.f1475d) {
                        i2 = i3;
                    }
                }
                h hVar = new h((DialogInterface.OnClickListener) new B(this));
                AlertDialog create = new AlertDialog.Builder(this.f2689a).setTitle(R.string.antivirus_choose_engine_dialog_title).setSingleChoiceItems(strArr, i2, hVar).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                create.show();
                hVar.a(create);
            }
        }

        /* JADX WARNING: type inference failed for: r6v0, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* JADX WARNING: type inference failed for: r4v2, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        private void e() {
            if (Build.IS_INTERNATIONAL_BUILD) {
                startActivityForResult(com.miui.securityscan.i.l.a(this.f2689a, getString(R.string.virus_update_tips_title), getString(R.string.antivirus_sec_network_unavailable), getString(17039360), getString(R.string.antivirus_update_btn_open)), 202);
                return;
            }
            h hVar = new h((DialogInterface.OnClickListener) new D(this));
            AlertDialog create = new AlertDialog.Builder(this.f2689a).setTitle(R.string.virus_update_tips_title).setMessage(R.string.antivirus_sec_network_unavailable).setPositiveButton(R.string.antivirus_update_btn_open, hVar).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            create.show();
            hVar.a(create);
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* access modifiers changed from: private */
        public void f() {
            this.r = ProgressDialog.show(this.f2689a, (CharSequence) null, getString(R.string.antivirus_toast_updating), true, true);
            this.x = new d(this);
            this.x.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* JADX WARNING: type inference failed for: r0v5, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        private void g() {
            if (!f.b(this.f2689a)) {
                Toast.makeText(this.f2689a, R.string.antivirus_toast_network_unavailable, 0).show();
            } else if (!com.miui.securitycenter.h.i()) {
                e();
            } else if (f.a(this.f2689a)) {
                b();
            } else {
                f();
            }
        }

        /* renamed from: a */
        public void onLoadFinished(Loader<Pair> loader, Pair pair) {
            String str;
            String str2;
            TextPreference textPreference = this.p;
            boolean z = false;
            if (((Integer) pair.first).intValue() == 0) {
                str = getString(R.string.sp_settings_exception_count_zero);
            } else {
                str = getResources().getQuantityString(R.plurals.sp_settings_exception_count, ((Integer) pair.first).intValue(), new Object[]{pair.first});
            }
            textPreference.a(str);
            TextPreference textPreference2 = this.q;
            if (((Integer) pair.second).intValue() == 0) {
                str2 = getString(R.string.sp_settings_exception_count_zero);
            } else {
                str2 = getResources().getQuantityString(R.plurals.sp_settings_exception_count, ((Integer) pair.second).intValue(), new Object[]{pair.second});
            }
            textPreference2.a(str2);
            if (((Integer) pair.first).intValue() != 0) {
                z = true;
            }
            p.h(z);
        }

        /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        public void b() {
            h hVar = new h((DialogInterface.OnClickListener) new E(this));
            AlertDialog create = new AlertDialog.Builder(this.f2689a).setTitle(R.string.virus_update_tips_title).setMessage(R.string.virus_wait_network_dialog_message).setPositiveButton(R.string.antivirus_update_btn_contiue, hVar).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
            create.show();
            hVar.a(create);
        }

        /* JADX WARNING: type inference failed for: r4v5, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* JADX WARNING: type inference failed for: r0v5, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* JADX WARNING: type inference failed for: r4v79, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        /* JADX WARNING: type inference failed for: r4v81, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.f2689a = (SettingsActivity) getActivity();
            addPreferencesFromResource(a() ? R.xml.v_settings_v12 : R.xml.v_settings);
            this.t = b.b.b.b.a((Context) this.f2689a);
            this.f2690b = (PreferenceCategory) findPreference(this.f2689a.getString(R.string.preference_category_key_antivirus_setting));
            if (a()) {
                this.f2692d = (DropDownPreference) findPreference(this.f2689a.getString(R.string.preference_key_antivirus_choose_engine));
                this.f2692d.setOnPreferenceChangeListener(this);
            } else {
                this.f2691c = (TextPreference) findPreference(this.f2689a.getString(R.string.preference_key_antivirus_choose_engine));
                this.f2691c.setOnPreferenceClickListener(this);
            }
            this.e = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_open_virus_cloud_scan));
            this.e.setChecked(p.n());
            this.e.setOnPreferenceChangeListener(this);
            this.f = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_open_virus_install_monitor));
            this.f.setChecked(b.b.b.d.a.a(this.f2689a));
            this.f.setOnPreferenceChangeListener(this);
            this.g = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_virus_lib_auto_update));
            this.h = (TextPreference) findPreference(this.f2689a.getString(R.string.preference_key_manual_update_virus_db));
            this.h.setOnPreferenceClickListener(this);
            this.i = (PreferenceCategory) findPreference(this.f2689a.getString(R.string.preference_key_category_monitor));
            this.k = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_settings_monitor));
            this.k.setChecked(p.j());
            this.k.setOnPreferenceChangeListener(this);
            this.l = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_settings_input_method));
            this.l.setChecked(p.l());
            this.l.setOnPreferenceChangeListener(this);
            this.j = (PreferenceCategory) findPreference(this.f2689a.getString(R.string.preference_key_category_check_item));
            this.m = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_check_item_wifi));
            this.m.setChecked(p.p());
            this.m.setOnPreferenceChangeListener(this);
            this.n = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_check_item_root));
            this.n.setChecked(p.k());
            this.n.setOnPreferenceChangeListener(this);
            this.o = (CheckBoxPreference) findPreference(this.f2689a.getString(R.string.preference_key_check_item_update));
            this.o.setChecked(p.m());
            this.o.setOnPreferenceChangeListener(this);
            this.p = (TextPreference) findPreference(this.f2689a.getString(R.string.preference_key_virus_white_list));
            this.q = (TextPreference) findPreference(this.f2689a.getString(R.string.preference_key_sign_exception));
            this.u = i.a((Context) this.f2689a);
            this.s = com.miui.guardprovider.b.a((Context) this.f2689a);
            this.s.a((b.a) null);
            this.v = new e(this);
            this.w = new a(this);
            this.w.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            if (com.miui.securitycenter.p.a() < 5) {
                this.j.d(this.p);
            } else {
                getLoaderManager().initLoader(100, (Bundle) null, this);
            }
            if (Build.IS_INTERNATIONAL_BUILD) {
                if (!Build.checkRegion("IN") || !n.b()) {
                    getPreferenceScreen().d(this.i);
                }
                this.j.d(this.m);
                this.j.d(this.q);
            }
            if (Build.IS_ALPHA_BUILD) {
                this.j.d(this.n);
            }
        }

        /* JADX WARNING: type inference failed for: r2v1, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        public Loader<Pair> onCreateLoader(int i2, Bundle bundle) {
            return new b(this.f2689a);
        }

        public void onCreatePreferences(Bundle bundle, String str) {
        }

        public void onDestroy() {
            super.onDestroy();
            this.s.a();
            d dVar = this.x;
            if (dVar != null) {
                dVar.cancel(true);
            }
            a aVar = this.w;
            if (aVar != null) {
                aVar.cancel(true);
            }
        }

        public void onLoaderReset(Loader<Pair> loader) {
        }

        /* JADX WARNING: type inference failed for: r0v8, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        public boolean onPreferenceChange(Preference preference, Object obj) {
            DropDownPreference dropDownPreference = this.f2692d;
            if (preference == dropDownPreference) {
                dropDownPreference.b((String) obj);
                a(this.y.get(this.f2692d.d()).f1472a, this.y);
                return true;
            }
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (preference == this.e) {
                p.g(booleanValue);
                return true;
            } else if (preference == this.f) {
                b.b.b.d.a.a(this.f2689a.getApplicationContext(), booleanValue);
                return true;
            } else if (preference == this.k) {
                Intent intent = new Intent(this.f2689a, GuardService.class);
                intent.setAction(booleanValue ? "action_register_foreground_notification" : "action_unregister_foreground_notification");
                this.f2689a.startService(intent);
                return true;
            } else if (preference == this.l) {
                p.e(booleanValue);
                return true;
            } else if (preference == this.m) {
                p.i(booleanValue);
                return true;
            } else if (preference == this.n) {
                p.d(booleanValue);
                return true;
            } else if (preference != this.o) {
                return false;
            } else {
                p.f(booleanValue);
                return true;
            }
        }

        /* JADX WARNING: type inference failed for: r0v4, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        public boolean onPreferenceClick(Preference preference) {
            if (preference == this.f2691c) {
                d();
                return true;
            } else if (preference == this.p) {
                startActivity(new Intent(this.f2689a, WhiteListActivity.class));
                return true;
            } else if (preference != this.h) {
                return false;
            } else {
                try {
                    g();
                    return true;
                } catch (Exception e2) {
                    Log.e("SettingsActivity", "exception when update engine: ", e2);
                    return false;
                }
            }
        }

        public void onResume() {
            super.onResume();
            getLoaderManager().restartLoader(100, (Bundle) null, this);
        }
    }

    private static class d extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<c> f2693a;

        d(c cVar) {
            this.f2693a = new WeakReference<>(cVar);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            c cVar = (c) this.f2693a.get();
            if (cVar == null || cVar.u == null || cVar.v == null) {
                return null;
            }
            cVar.u.a((VirusObserver) cVar.v);
            return null;
        }
    }

    private static class e extends VirusObserver {

        /* renamed from: c  reason: collision with root package name */
        private WeakReference<c> f2694c;

        e(c cVar) {
            this.f2694c = new WeakReference<>(cVar);
        }

        public void a(UpdateInfo updateInfo) {
            c cVar;
            if (!"MiEngine".equals(updateInfo.engineName) && (cVar = (c) this.f2694c.get()) != null && !cVar.isDetached() && cVar.f2689a != null && !cVar.f2689a.isDestroyed()) {
                cVar.f2689a.runOnUiThread(new F(this, cVar, updateInfo));
            }
        }

        /* JADX WARNING: type inference failed for: r3v5, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity] */
        public void p(int i) {
            Log.i("SettingsActivity", "onUpdateFinished : " + i);
            c cVar = (c) this.f2694c.get();
            if (cVar != null && cVar.f2689a != null) {
                com.miui.guardprovider.b.a((Context) cVar.f2689a).a();
            }
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.antivirus.activity.SettingsActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        SettingsActivity.super.onActivityResult(i, i2, intent);
        if (i == 202 && i2 == -1) {
            com.miui.securityscan.i.l.a(getApplicationContext(), true);
            if (f.a(this)) {
                this.f2686a.b();
            } else {
                this.f2686a.f();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        int intExtra;
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (!(intent == null || (intExtra = intent.getIntExtra("extra_settings_title_res", -1)) == -1)) {
            setTitle(intExtra);
        }
        FragmentTransaction beginTransaction = getFragmentManager().beginTransaction();
        c cVar = new c();
        this.f2686a = cVar;
        beginTransaction.replace(16908290, cVar, (String) null).commit();
        k.a((Activity) this);
    }
}
