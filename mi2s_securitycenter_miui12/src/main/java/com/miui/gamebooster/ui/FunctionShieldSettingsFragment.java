package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.C0388t;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import java.lang.ref.WeakReference;
import miuix.preference.s;

public class FunctionShieldSettingsFragment extends s {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Activity f4877a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public CheckBoxPreference f4878b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CheckBoxPreference f4879c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public CheckBoxPreference f4880d;
    /* access modifiers changed from: private */
    public CheckBoxPreference e;
    /* access modifiers changed from: private */
    public CheckBoxPreference f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public boolean h;
    /* access modifiers changed from: private */
    public boolean i;
    /* access modifiers changed from: private */
    public boolean j;
    /* access modifiers changed from: private */
    public boolean k;
    /* access modifiers changed from: private */
    public int l = 0;
    /* access modifiers changed from: private */
    public com.miui.gamebooster.c.a m;
    private a n;
    private Preference.b o = new T(this);

    private static class a extends AsyncTask<Void, Void, Integer> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<FunctionShieldSettingsFragment> f4881a;

        public a(FunctionShieldSettingsFragment functionShieldSettingsFragment) {
            this.f4881a = new WeakReference<>(functionShieldSettingsFragment);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Integer doInBackground(Void... voidArr) {
            FunctionShieldSettingsFragment functionShieldSettingsFragment = (FunctionShieldSettingsFragment) this.f4881a.get();
            if (functionShieldSettingsFragment == null || isCancelled()) {
                return null;
            }
            com.miui.gamebooster.c.a.a((Context) functionShieldSettingsFragment.f4877a);
            int i = 0;
            boolean unused = functionShieldSettingsFragment.g = com.miui.gamebooster.c.a.r(false);
            boolean unused2 = functionShieldSettingsFragment.h = com.miui.gamebooster.c.a.s(false);
            boolean unused3 = functionShieldSettingsFragment.i = com.miui.gamebooster.c.a.u(false);
            boolean unused4 = functionShieldSettingsFragment.j = com.miui.gamebooster.c.a.t(false);
            boolean unused5 = functionShieldSettingsFragment.k = com.miui.gamebooster.c.a.g(false);
            if (functionShieldSettingsFragment.g) {
                i = 1;
            }
            if (functionShieldSettingsFragment.h) {
                i++;
            }
            if (functionShieldSettingsFragment.i) {
                i++;
            }
            if (functionShieldSettingsFragment.j) {
                i++;
            }
            if (functionShieldSettingsFragment.k) {
                i++;
            }
            com.miui.gamebooster.c.a.b(i);
            return Integer.valueOf(i);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Integer num) {
            super.onPostExecute(num);
            FunctionShieldSettingsFragment functionShieldSettingsFragment = (FunctionShieldSettingsFragment) this.f4881a.get();
            if (functionShieldSettingsFragment != null && num != null) {
                int unused = functionShieldSettingsFragment.l = num.intValue();
                functionShieldSettingsFragment.f4878b.setChecked(functionShieldSettingsFragment.g);
                functionShieldSettingsFragment.f4879c.setChecked(functionShieldSettingsFragment.h);
                functionShieldSettingsFragment.f4880d.setChecked(functionShieldSettingsFragment.i);
                functionShieldSettingsFragment.e.setChecked(functionShieldSettingsFragment.j);
                functionShieldSettingsFragment.f.setChecked(functionShieldSettingsFragment.k);
            }
        }
    }

    static /* synthetic */ int a(FunctionShieldSettingsFragment functionShieldSettingsFragment) {
        int i2 = functionShieldSettingsFragment.l + 1;
        functionShieldSettingsFragment.l = i2;
        return i2;
    }

    private void a() {
        this.n = new a(this);
        this.n.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    static /* synthetic */ int b(FunctionShieldSettingsFragment functionShieldSettingsFragment) {
        int i2 = functionShieldSettingsFragment.l - 1;
        functionShieldSettingsFragment.l = i2;
        return i2;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        this.f4877a = getActivity();
        if (!Utils.a(this.f4877a)) {
            addPreferencesFromResource(R.xml.gb_function_shied_settings);
            this.m = com.miui.gamebooster.c.a.a((Context) this.f4877a);
            this.f4878b = (CheckBoxPreference) findPreference("pref_auto_bright");
            this.f4879c = (CheckBoxPreference) findPreference("pref_eye_shield");
            this.f4880d = (CheckBoxPreference) findPreference("pref_three_finger");
            this.e = (CheckBoxPreference) findPreference("pref_pull_notification_bar");
            this.f = (CheckBoxPreference) findPreference("pref_disable_voicetrigger");
            this.n = new a(this);
            this.f4878b.setOnPreferenceChangeListener(this.o);
            this.f4879c.setOnPreferenceChangeListener(this.o);
            this.f4880d.setOnPreferenceChangeListener(this.o);
            this.e.setOnPreferenceChangeListener(this.o);
            this.f.setOnPreferenceChangeListener(this.o);
            if (!C0388t.e()) {
                getPreferenceScreen().d(this.f4879c);
                if (p.a() < 12) {
                    getPreferenceScreen().d(this.f4880d);
                }
            }
            if (!C0388t.a(this.f4877a)) {
                getPreferenceScreen().d(this.f);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        a aVar = this.n;
        if (aVar != null) {
            aVar.cancel(true);
        }
    }

    public void onResume() {
        super.onResume();
        a();
    }
}
