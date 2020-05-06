package com.miui.permcenter.privacymanager.behaviorrecord;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import b.b.c.j.x;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.permcenter.privacymanager.a.c;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;
import miuix.preference.s;

public class SingleAppPrivacyManagerActivity extends b.b.c.c.a {

    public static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<b> f6420a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f6421b;

        public a(b bVar, boolean z) {
            this.f6420a = new WeakReference<>(bVar);
            this.f6421b = z;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            b bVar = (b) this.f6420a.get();
            Activity activity = bVar.getActivity();
            if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
                if (this.f6421b) {
                    bVar.f.b(1);
                }
                bVar.f6425d.setChecked(bVar.f.a(1));
                bVar.b(bVar.f.a(), bVar.f.b());
            }
        }
    }

    public static class b extends s implements Preference.b {

        /* renamed from: a  reason: collision with root package name */
        private CheckBoxPreference f6422a;

        /* renamed from: b  reason: collision with root package name */
        private CheckBoxPreference f6423b;

        /* renamed from: c  reason: collision with root package name */
        private CheckBoxPreference f6424c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public CheckBoxPreference f6425d;
        private Context e;
        /* access modifiers changed from: private */
        public c f;

        private void a(int i, boolean z) {
            if (z) {
                this.f.c(i);
            } else {
                this.f.b(i);
            }
            b(this.f.a(), this.f.b());
            this.e.sendBroadcast(new Intent("com.miui.action.sync_status_bar"), "miui.permission.READ_AND_WIRTE_PERMISSION_MANAGER");
        }

        private void a(c cVar, boolean z) {
            if (z) {
                cVar.c(1);
            } else if (cVar.a(5)) {
                a aVar = new a(this, true);
                new AlertDialog.Builder(this.e).setTitle(R.string.app_behavior_monitor_off).setMessage(R.string.app_behavior_monitor_off_content).setPositiveButton(R.string.app_behavior_monitor_off_positive, aVar).setNegativeButton(R.string.cancel, new a(this, false)).setCancelable(false).show();
                cVar.b(5);
            } else {
                cVar.b(1);
            }
            b(cVar.a(), cVar.b());
            this.f6425d.setChecked(this.f.a(1));
        }

        /* access modifiers changed from: private */
        public void b(String str, int i) {
            com.miui.common.persistence.b.b(str, i);
        }

        public void onCreatePreferences(Bundle bundle, String str) {
            setPreferencesFromResource(R.xml.pm_single_app_privacy_manager, str);
            this.e = getContext();
            Bundle arguments = getArguments();
            if (arguments != null) {
                this.f = new c(arguments.getString("pkgName"), arguments.getInt(UserConfigure.Columns.USER_ID));
                c cVar = this.f;
                cVar.d(com.miui.common.persistence.b.a(cVar.a(), 0));
            }
            if (this.f == null) {
                getActivity().finish();
                return;
            }
            this.f6422a = (CheckBoxPreference) findPreference("key_location_setting");
            this.f6422a.setOnPreferenceChangeListener(this);
            this.f6423b = (CheckBoxPreference) findPreference("key_audio_setting");
            this.f6423b.setOnPreferenceChangeListener(this);
            this.f6424c = (CheckBoxPreference) findPreference("key_camera_setting");
            this.f6424c.setOnPreferenceChangeListener(this);
            this.f6425d = (CheckBoxPreference) findPreference("key_protect_privacy");
            this.f6425d.setOnPreferenceChangeListener(this);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (preference == this.f6425d) {
                a(this.f, booleanValue);
                return true;
            } else if (preference == this.f6423b) {
                a(3, booleanValue);
                return true;
            } else if (preference == this.f6422a) {
                a(2, booleanValue);
                return true;
            } else if (preference != this.f6424c) {
                return false;
            } else {
                a(4, booleanValue);
                return true;
            }
        }

        public void onResume() {
            super.onResume();
            this.f6425d.setChecked(this.f.a(1));
            this.f6422a.setChecked(this.f.a(2));
            this.f6423b.setChecked(this.f.a(3));
            this.f6424c.setChecked(this.f.a(4));
            boolean d2 = o.d(this.e);
            this.f6425d.setEnabled(d2);
            this.f6422a.setEnabled(d2);
            this.f6423b.setEnabled(d2);
            this.f6424c.setEnabled(d2);
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [b.b.c.c.a, android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.SingleAppPrivacyManagerActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        PackageInfo packageInfo;
        super.onCreate(bundle);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("am_app_pkgname");
        int intExtra = intent.getIntExtra("am_app_uid", UserHandle.myUserId());
        try {
            packageInfo = getPackageManager().getPackageInfo(stringExtra, 128);
        } catch (Exception e) {
            Log.e("BehaviorRecord-Manager", "not found package", e);
            packageInfo = null;
        }
        if (TextUtils.isEmpty(stringExtra) || packageInfo == null) {
            finish();
            return;
        }
        setTitle(x.j(this, stringExtra).toString());
        if (bundle == null) {
            b bVar = new b();
            Bundle bundle2 = new Bundle();
            bundle2.putString("pkgName", stringExtra);
            bundle2.putInt(UserConfigure.Columns.USER_ID, intExtra);
            bVar.setArguments(bundle2);
            getFragmentManager().beginTransaction().replace(16908290, bVar).commit();
        }
    }
}
