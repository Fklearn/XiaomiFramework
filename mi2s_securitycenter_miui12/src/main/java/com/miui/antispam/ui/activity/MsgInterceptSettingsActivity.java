package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.a.e.n;
import b.b.c.c.b.l;
import com.miui.antispam.db.d;
import com.miui.securitycenter.R;
import miui.os.Build;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class MsgInterceptSettingsActivity extends C0224s {

    /* renamed from: d  reason: collision with root package name */
    private boolean f2561d = true;

    public static class a extends l implements Preference.c, Preference.b {

        /* renamed from: a  reason: collision with root package name */
        private PreferenceCategory f2562a;

        /* renamed from: b  reason: collision with root package name */
        private DropDownPreference f2563b;

        /* renamed from: c  reason: collision with root package name */
        private DropDownPreference f2564c;

        /* renamed from: d  reason: collision with root package name */
        private DropDownPreference f2565d;
        /* access modifiers changed from: private */
        public TextPreference e;
        /* access modifiers changed from: private */
        public TextPreference f;
        /* access modifiers changed from: private */
        public TextPreference g;
        private CheckBoxPreference h;
        /* access modifiers changed from: private */
        public TextPreference i;
        /* access modifiers changed from: private */
        public TextPreference j;
        /* access modifiers changed from: private */
        public Context k;
        /* access modifiers changed from: private */
        public String[] l;
        /* access modifiers changed from: private */
        public String[] m;
        /* access modifiers changed from: private */
        public int n;

        private void a(Preference preference) {
            String str;
            int i2;
            if (preference == this.e) {
                i2 = R.string.st_antispam_title_anoy_sms;
                str = "stranger_sms_mode";
            } else if (preference == this.f) {
                i2 = R.string.st_antispam_title_service_sms;
                str = "service_sms_mode";
            } else {
                i2 = R.string.st_antispam_title_mms;
                str = "mms_mode";
            }
            new AlertDialog.Builder(this.k).setTitle(i2).setSingleChoiceItems(preference == this.g ? this.m : this.l, d.a(this.k, str, this.n, str.equals("mms_mode") ? 2 : 1), new S(this, str, preference)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        public void onCreate(Bundle bundle) {
            Preference preference;
            PreferenceCategory preferenceCategory;
            super.onCreate(bundle);
            this.k = getActivity();
            this.n = getActivity().getIntent().getIntExtra("key_sim_id", 1);
            addPreferencesFromResource(a() ? R.xml.antispam_msg_intercept_settings_v12 : R.xml.antispam_msg_intercept_settings);
            this.f2562a = (PreferenceCategory) findPreference("msg_stranger_group");
            if (a()) {
                this.f2563b = (DropDownPreference) findPreference("key_msg_stranger");
                this.f2564c = (DropDownPreference) findPreference("key_msg_notification");
                this.f2565d = (DropDownPreference) findPreference("key_msg_mms");
            } else {
                this.e = (TextPreference) findPreference("key_msg_stranger");
                this.f = (TextPreference) findPreference("key_msg_notification");
                this.g = (TextPreference) findPreference("key_msg_mms");
            }
            this.h = (CheckBoxPreference) findPreference("key_msg_contacts");
            this.i = (TextPreference) findPreference("key_msg_keyword_black");
            this.j = (TextPreference) findPreference("key_msg_keyword_white");
            this.m = getResources().getStringArray(R.array.st_antispam_handle_methods_mms);
            int a2 = d.a(this.k, "stranger_sms_mode", this.n, 1);
            int a3 = d.a(this.k, "service_sms_mode", this.n, 1);
            int a4 = d.a(this.k, "mms_mode", this.n, 2);
            this.h.setChecked(d.a(this.k, "contact_sms_mode", this.n, 1) == 0);
            if (Build.IS_INTERNATIONAL_BUILD) {
                this.l = getResources().getStringArray(R.array.st_antispam_handle_methods_normal_intl);
                if (a()) {
                    this.f2562a.d(this.f2564c);
                    preferenceCategory = this.f2562a;
                    preference = this.f2565d;
                } else {
                    this.f2562a.d(this.f);
                    preferenceCategory = this.f2562a;
                    preference = this.g;
                }
                preferenceCategory.d(preference);
                if (a2 > 1) {
                    a2 = 1;
                }
            } else {
                this.l = getResources().getStringArray(R.array.st_antispam_handle_methods_normal);
                if (a()) {
                    this.f2564c.a(a3);
                    this.f2565d.a(a4);
                } else {
                    this.f.a(this.l[a3]);
                    this.g.a(this.m[a4]);
                }
            }
            if (a()) {
                this.f2563b.a(a2);
            } else {
                this.e.a(this.l[a2]);
            }
            if (a()) {
                this.f2563b.setOnPreferenceChangeListener(this);
                this.f2564c.setOnPreferenceChangeListener(this);
                this.f2565d.setOnPreferenceChangeListener(this);
            } else {
                this.e.setOnPreferenceClickListener(this);
                this.f.setOnPreferenceClickListener(this);
                this.g.setOnPreferenceClickListener(this);
            }
            this.h.setOnPreferenceChangeListener(this);
            this.i.setOnPreferenceClickListener(this);
            this.j.setOnPreferenceClickListener(this);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            if (preference == this.h) {
                d.b(this.k, "contact_sms_mode", this.n, ((Boolean) obj).booleanValue() ^ true ? 1 : 0);
                return true;
            } else if (preference != this.f2563b && preference != this.f2564c && preference != this.f2565d) {
                return false;
            } else {
                DropDownPreference dropDownPreference = (DropDownPreference) preference;
                dropDownPreference.b((String) obj);
                d.b(this.k, preference == this.f2563b ? "stranger_sms_mode" : preference == this.f2564c ? "service_sms_mode" : "mms_mode", this.n, dropDownPreference.d());
                return false;
            }
        }

        public boolean onPreferenceClick(Preference preference) {
            Intent intent;
            if (preference == this.e || preference == this.f || preference == this.g) {
                a(preference);
            } else {
                if (preference == this.i) {
                    intent = new Intent(this.k, KeywordListActivity.class);
                    intent.putExtra("key_sim_id", this.n);
                    intent.putExtra("is_black", true);
                } else if (preference == this.j) {
                    intent = new Intent(this.k, KeywordListActivity.class);
                    intent.putExtra("key_sim_id", this.n);
                    intent.putExtra("is_black", false);
                }
                startActivity(intent);
            }
            return false;
        }

        public void onResume() {
            super.onResume();
            new Q(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    public static class b extends Fragment {
        /* access modifiers changed from: private */
        public static b b() {
            return new b();
        }

        @Nullable
        public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
            View inflate = layoutInflater.inflate(R.layout.fw_log_list_empty, (ViewGroup) null);
            ((ImageView) inflate.findViewById(R.id.emptyImage)).setImageResource(R.drawable.no_mslog);
            ((TextView) inflate.findViewById(R.id.emptyText)).setText(R.string.antispam_mms_text_setting);
            return inflate;
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.miui.antispam.ui.activity.MsgInterceptSettingsActivity] */
    /* access modifiers changed from: protected */
    public Fragment c() {
        if (n.c((Context) this)) {
            this.f2561d = true;
            return a.b();
        }
        this.f2561d = false;
        return b.b();
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.antispam.ui.activity.MsgInterceptSettingsActivity, com.miui.antispam.ui.activity.r, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if ((!n.c((Context) this) && this.f2561d) || (n.c((Context) this) && !this.f2561d)) {
            getFragmentManager().beginTransaction().replace(16908290, c()).commit();
        }
    }
}
