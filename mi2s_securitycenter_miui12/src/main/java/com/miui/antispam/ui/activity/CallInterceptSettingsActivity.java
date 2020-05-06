package com.miui.antispam.ui.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.a.e.c;
import b.b.a.e.n;
import b.b.c.c.b.l;
import com.miui.antispam.db.d;
import com.miui.securitycenter.R;
import com.xiaomi.stat.MiStat;
import miui.cloud.CloudPushConstants;
import miui.cloud.common.XSimChangeNotification;
import miui.os.Build;
import miui.provider.ExtraTelephony;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;
import miui.util.IOUtils;
import miuix.preference.TextPreference;

public class CallInterceptSettingsActivity extends C0224s {

    public static class a extends l implements Preference.c, Preference.b {

        /* renamed from: a  reason: collision with root package name */
        private CheckBoxPreference f2523a;

        /* renamed from: b  reason: collision with root package name */
        private CheckBoxPreference f2524b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public CheckBoxPreference f2525c;

        /* renamed from: d  reason: collision with root package name */
        private TextPreference f2526d;
        /* access modifiers changed from: private */
        public CheckBoxPreference e;
        private CheckBoxPreference f;
        private PreferenceCategory g;
        private TextPreference h;
        private TextPreference i;
        /* access modifiers changed from: private */
        public Context j;
        /* access modifiers changed from: private */
        public int k;

        private void a(Preference preference) {
            new AlertDialog.Builder(this.j).setTitle(R.string.report_warning_title).setMessage(preference == this.e ? R.string.st_confirm_dialog_summary_contact_call : preference == this.f2525c ? R.string.st_confirm_dialog_summary_oversea_call : R.string.st_confirm_dialog_summary_stranger_call).setPositiveButton(R.string.st_confirm_dialog_btn_open, new B(this, preference)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setOnDismissListener(new A(this)).show();
        }

        private void a(TextPreference textPreference, String str, int i2, int i3, boolean z) {
            String str2;
            TextPreference textPreference2 = textPreference;
            String str3 = str;
            int i4 = i2;
            boolean z2 = z;
            SubscriptionInfo subscriptionInfoForSlot = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(i4);
            if (str3 == null || (subscriptionInfoForSlot == null && z2)) {
                Context context = this.j;
                textPreference2.setTitle((CharSequence) context.getString(R.string.st_title_sim_card, new Object[]{context.getString(R.string.st_title_not_sim), String.valueOf(i3)}));
                textPreference2.setEnabled(false);
            } else {
                textPreference2.setOnPreferenceClickListener(new C(this, str3, z2, i4));
                if (!z2) {
                    str2 = this.j.getString(R.string.st_title_anticomintcall_backsound_setting);
                } else if (n.d(this.j, subscriptionInfoForSlot.getSlotId())) {
                    str2 = n.b(this.j);
                } else {
                    str2 = this.j.getString(R.string.st_title_sim_card, new Object[]{subscriptionInfoForSlot.getDisplayName(), String.valueOf(i3)});
                }
                textPreference2.setTitle((CharSequence) str2);
                textPreference.setKey(str);
                textPreference2.a(this.j.getString(R.string.st_title_back_sound_busy));
                Cursor cursor = null;
                try {
                    ContentResolver contentResolver = this.j.getContentResolver();
                    Uri uri = ExtraTelephony.AntiSpamSim.CONTENT_URI;
                    cursor = contentResolver.query(uri, (String[]) null, "sim_id='" + str3 + "'", (String[]) null, (String) null);
                    if (cursor != null) {
                        if (cursor.moveToNext()) {
                            String[] stringArray = this.j.getResources().getStringArray(R.array.st_antispam_mode_backsound_defined);
                            int i5 = cursor.getInt(cursor.getColumnIndex("backsound_mode"));
                            if (i5 > 0) {
                                textPreference2.a(stringArray[i5]);
                                d.a(this.k, i5);
                            }
                            IOUtils.closeQuietly(cursor);
                        }
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(XSimChangeNotification.BROADCAST_EXTRA_KEY_SIM_ID, str3);
                    contentValues.put("backsound_mode", 0);
                    if (!z2) {
                        str2 = "";
                    }
                    contentValues.put(CloudPushConstants.XML_NAME, str2);
                    this.j.getContentResolver().insert(ExtraTelephony.AntiSpamSim.CONTENT_URI, contentValues);
                } catch (Exception e2) {
                    e2.printStackTrace();
                } catch (Throwable th) {
                    IOUtils.closeQuietly(cursor);
                    throw th;
                }
                IOUtils.closeQuietly(cursor);
            }
            textPreference2.setVisible(true);
        }

        private void b() {
            int i2;
            int i3;
            String str;
            TextPreference textPreference;
            boolean z;
            if (TelephonyManager.getDefault().getPhoneCount() == 1) {
                textPreference = this.h;
                str = TelephonyManager.getDefault().getSubscriberId();
                i3 = -1;
                i2 = 1;
                z = false;
            } else {
                if (!c.e(this.j)) {
                    textPreference = this.h;
                    str = TelephonyManager.getDefault().getSubscriberIdForSlot(this.k - 1);
                    i2 = this.k;
                    i3 = i2 - 1;
                } else {
                    a(this.h, TelephonyManager.getDefault().getSubscriberIdForSlot(0), 0, 1, true);
                    textPreference = this.i;
                    str = TelephonyManager.getDefault().getSubscriberIdForSlot(1);
                    i3 = 1;
                    i2 = 2;
                }
                z = true;
            }
            a(textPreference, str, i3, i2, z);
        }

        /* access modifiers changed from: private */
        public static a c() {
            return new a();
        }

        /* access modifiers changed from: private */
        public void d() {
            boolean z = true;
            int a2 = d.a(this.j, "stranger_call_mode", this.k, 1);
            int a3 = d.a(this.j, "contact_call_mode", this.k, 1);
            int a4 = d.a(this.j, "oversea_call_mode", this.k, 1);
            int a5 = d.a(this.j, "empty_call_mode", this.k, 1);
            this.f2523a.setChecked(a2 == 0);
            this.e.setChecked(a3 == 0);
            this.f2525c.setChecked(a4 == 0);
            CheckBoxPreference checkBoxPreference = this.f;
            if (a5 != 0) {
                z = false;
            }
            checkBoxPreference.setChecked(z);
            this.f2524b.setChecked(d.b(this.k));
        }

        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.j = getActivity();
            Intent intent = getActivity().getIntent();
            this.k = intent.getIntExtra("key_sim_id", 1);
            if (intent.getBooleanExtra("is_from_intercept_notification", false)) {
                b.b.a.a.a.a("oversea_function_guide", "oversea_intercept", MiStat.Event.CLICK);
            }
            addPreferencesFromResource(R.xml.antispam_call_intercept_settings);
            this.f2523a = (CheckBoxPreference) findPreference("key_call_stranger");
            this.f2524b = (CheckBoxPreference) findPreference("key_call_forwarding");
            this.f2525c = (CheckBoxPreference) findPreference("key_call_oversea");
            this.f2526d = (TextPreference) findPreference("key_call_reported");
            this.e = (CheckBoxPreference) findPreference("key_call_contacts");
            this.f = (CheckBoxPreference) findPreference("key_call_unknown");
            this.g = (PreferenceCategory) findPreference("backsound_group");
            this.h = (TextPreference) findPreference("backsound_1");
            this.i = (TextPreference) findPreference("backsound_2");
            this.f2523a.setOnPreferenceChangeListener(this);
            this.f2524b.setOnPreferenceChangeListener(this);
            this.f2525c.setOnPreferenceChangeListener(this);
            this.f2526d.setOnPreferenceClickListener(this);
            this.e.setOnPreferenceChangeListener(this);
            this.f.setOnPreferenceChangeListener(this);
            if (Build.IS_INTERNATIONAL_BUILD) {
                ((PreferenceCategory) findPreference("call_stranger_group")).d(this.f2526d);
                ((PreferenceCategory) findPreference("call_stranger_group")).d(this.f2525c);
            }
            if (Build.IS_INTERNATIONAL_BUILD || Build.IS_CU_CUSTOMIZATION_TEST) {
                getPreferenceScreen().d(this.g);
            }
            d();
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (preference == this.f2523a || preference == this.e || preference == this.f2525c) {
                if (booleanValue) {
                    a(preference);
                } else {
                    d.b(this.j, preference == this.e ? "contact_call_mode" : preference == this.f2525c ? "oversea_call_mode" : "stranger_call_mode", this.k, 1);
                }
            } else if (preference == this.f2524b) {
                d.a(this.k, booleanValue);
            } else if (preference == this.f) {
                d.b(this.j, "empty_call_mode", this.k, booleanValue ^ true ? 1 : 0);
            }
            return true;
        }

        public boolean onPreferenceClick(Preference preference) {
            if (preference != this.f2526d) {
                return false;
            }
            Intent intent = new Intent(this.j, MarkNumberBlockActivity.class);
            intent.putExtra("key_sim_id", this.k);
            startActivity(intent);
            return false;
        }

        public void onResume() {
            super.onResume();
            if (!Build.IS_INTERNATIONAL_BUILD && !Build.IS_CU_CUSTOMIZATION_TEST) {
                b();
            }
        }
    }

    /* access modifiers changed from: protected */
    public Fragment c() {
        return a.c();
    }
}
