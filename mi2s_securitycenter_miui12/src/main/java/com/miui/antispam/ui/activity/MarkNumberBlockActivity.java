package com.miui.antispam.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import b.b.a.e.c;
import b.b.c.c.b.l;
import com.miui.antispam.db.d;
import com.miui.securitycenter.R;
import miui.yellowpage.YellowPageUtils;

public class MarkNumberBlockActivity extends C0224s {

    public static class a extends l implements Preference.b {

        /* renamed from: a  reason: collision with root package name */
        private CheckBoxPreference f2557a;

        /* renamed from: b  reason: collision with root package name */
        private CheckBoxPreference f2558b;

        /* renamed from: c  reason: collision with root package name */
        private CheckBoxPreference f2559c;

        /* renamed from: d  reason: collision with root package name */
        private CheckBoxPreference f2560d;
        /* access modifiers changed from: private */
        public Activity e;
        private Dialog f;
        private int g;
        private boolean h;

        private void b() {
            boolean z = true;
            boolean z2 = this.h && d.b((Context) this.e, this.g);
            boolean z3 = this.h && d.a((Context) this.e, this.g);
            boolean z4 = this.h && d.d(this.e, this.g);
            this.f2557a.setChecked(z2);
            this.f2558b.setChecked(z3);
            this.f2559c.setChecked(z4);
            CheckBoxPreference checkBoxPreference = this.f2560d;
            if (!this.h || !d.c(this.g)) {
                z = false;
            }
            checkBoxPreference.setChecked(z);
            this.f2557a.setEnabled(this.h);
            this.f2558b.setEnabled(this.h);
            this.f2559c.setEnabled(this.h);
            this.f2560d.setEnabled(this.h);
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.antispam_mark_number_settings);
            this.e = getActivity();
            this.g = this.e.getIntent().getIntExtra("key_sim_id", 1);
            this.f2557a = (CheckBoxPreference) findPreference("key_mark_fraud");
            this.f2558b = (CheckBoxPreference) findPreference("key_mark_agent");
            this.f2559c = (CheckBoxPreference) findPreference("key_mark_sell");
            this.f2560d = (CheckBoxPreference) findPreference("key_repeated_marked_number");
            this.f2557a.setOnPreferenceChangeListener(this);
            this.f2558b.setOnPreferenceChangeListener(this);
            this.f2559c.setOnPreferenceChangeListener(this);
            this.f2560d.setOnPreferenceChangeListener(this);
            this.h = YellowPageUtils.isYellowPageAvailable(this.e);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            Boolean bool = (Boolean) obj;
            if (preference == this.f2560d) {
                d.b(this.g, bool.booleanValue());
            } else if (!YellowPageUtils.isYellowPageEnable(this.e)) {
                if (this.f == null) {
                    this.f = new AlertDialog.Builder(getActivity()).setTitle(R.string.dlg_title_not_open_stranger_identify).setMessage(R.string.dlg_message_not_open_stranger_identify).setPositiveButton(R.string.button_to_setting, new P(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                }
                this.f.show();
                return false;
            } else {
                if (preference == this.f2557a) {
                    d.b(this.e, this.g, bool.booleanValue());
                } else if (preference == this.f2558b) {
                    d.a(this.e, this.g, bool.booleanValue());
                } else if (preference == this.f2559c) {
                    d.d(this.e, this.g, bool.booleanValue());
                }
                c.c((Context) this.e, true);
            }
            return true;
        }

        public void onResume() {
            super.onResume();
            b();
        }
    }

    /* access modifiers changed from: protected */
    public Fragment c() {
        return new a();
    }
}
