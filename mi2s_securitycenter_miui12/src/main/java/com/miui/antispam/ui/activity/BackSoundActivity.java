package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.preference.Preference;
import b.b.c.c.b.l;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.securitycenter.R;
import miui.app.Activity;
import miui.cloud.common.XSimChangeNotification;
import miui.provider.ExtraTelephony;
import miui.telephony.SubscriptionManager;
import miui.util.IOUtils;
import miuix.preference.RadioButtonPreference;
import miuix.preference.RadioButtonPreferenceCategory;
import miuix.preference.RadioSetPreferenceCategory;
import miuix.preference.TextPreference;

public class BackSoundActivity extends C0224s {

    public static class a extends l implements Preference.c {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public static final Uri f2515a = Uri.parse("tel:*74");
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public static final Uri f2516b = Uri.parse("tel:*740");
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public RadioButtonPreferenceCategory f2517c;

        /* renamed from: d  reason: collision with root package name */
        private RadioButtonPreference[] f2518d;
        /* access modifiers changed from: private */
        public Activity e;
        private TextPreference f;
        private String g;
        /* access modifiers changed from: private */
        public int h;
        /* access modifiers changed from: private */
        public Handler i = new C0219m(this, Looper.getMainLooper());

        /* access modifiers changed from: private */
        public void a(int i2) {
            ContentResolver contentResolver = this.e.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put("backsound_mode", Integer.valueOf(i2));
            Uri uri = ExtraTelephony.AntiSpamSim.CONTENT_URI;
            contentResolver.update(uri, contentValues, "sim_id='" + this.g + "'", (String[]) null);
        }

        /* access modifiers changed from: private */
        public void a(int i2, String str, int i3, RadioButtonPreference radioButtonPreference) {
            new AlertDialog.Builder(this.e).setMessage(getString(R.string.backsound_set_hint, new Object[]{str})).setPositiveButton(17039370, new C0220n(this, i2, radioButtonPreference)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show().setOnDismissListener(new C0221o(this, radioButtonPreference));
        }

        /* access modifiers changed from: private */
        public void a(RadioButtonPreference radioButtonPreference) {
            radioButtonPreference.setChecked(false);
            ((RadioButtonPreference) ((RadioSetPreferenceCategory) this.f2517c.a(f())).a(0)).setChecked(true);
        }

        public static a e() {
            return new a();
        }

        /* access modifiers changed from: private */
        public int f() {
            int i2;
            Cursor cursor = null;
            try {
                ContentResolver contentResolver = this.e.getContentResolver();
                Uri uri = ExtraTelephony.AntiSpamSim.CONTENT_URI;
                cursor = contentResolver.query(uri, (String[]) null, "sim_id = '" + this.g + "'", (String[]) null, (String) null);
                if (cursor == null || !cursor.moveToNext() || (i2 = cursor.getInt(cursor.getColumnIndex("backsound_mode"))) <= 0) {
                    i2 = 0;
                }
                return i2;
            } finally {
                IOUtils.closeQuietly(cursor);
            }
        }

        public void d() {
            C0222p pVar = new C0222p(this, getResources().getStringArray(R.array.st_antispam_mode_backsound_defined));
            int f2 = f();
            int i2 = 0;
            while (true) {
                RadioButtonPreference[] radioButtonPreferenceArr = this.f2518d;
                if (i2 < radioButtonPreferenceArr.length) {
                    RadioButtonPreference radioButtonPreference = radioButtonPreferenceArr[i2];
                    radioButtonPreference.setOnPreferenceClickListener(pVar);
                    if (i2 == f2) {
                        radioButtonPreference.setChecked(true);
                    }
                    i2++;
                } else {
                    return;
                }
            }
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.backsound);
            this.e = getActivity();
            this.g = this.e.getIntent().getStringExtra(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID);
            this.h = this.e.getIntent().getIntExtra(ProviderConstant.DataUsageStatusDetailedColumns.SIM_SLOT, -1);
            this.f = (TextPreference) findPreference("key_open_call_wait");
            this.f.setOnPreferenceClickListener(this);
            this.f2517c = (RadioButtonPreferenceCategory) findPreference("key_st_antispam_backsound_mode_defined");
            this.f2518d = new RadioButtonPreference[]{(RadioButtonPreference) findPreference("backsound1"), (RadioButtonPreference) findPreference("backsound2"), (RadioButtonPreference) findPreference("backsound3"), (RadioButtonPreference) findPreference("backsound4")};
            d();
        }

        public boolean onPreferenceClick(Preference preference) {
            if (preference != this.f) {
                return false;
            }
            if (this.g.startsWith("46003")) {
                new AlertDialog.Builder(this.e).setItems(getResources().getStringArray(R.array.st_antispam_callwait_setting), new C0223q(this)).setTitle(R.string.label_callwait).setPositiveButton(17039360, (DialogInterface.OnClickListener) null).show();
                return false;
            }
            Intent intent = new Intent();
            int i2 = this.h;
            if (i2 != -1) {
                SubscriptionManager.putSlotIdExtra(intent, i2);
            }
            intent.setComponent(new ComponentName("com.android.phone", "com.android.phone.settings.GsmUmtsCallWaitingSetting"));
            startActivity(intent);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public Fragment c() {
        return a.e();
    }
}
