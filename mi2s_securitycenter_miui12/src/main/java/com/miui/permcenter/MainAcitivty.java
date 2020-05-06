package com.miui.permcenter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.preference.Preference;
import b.b.c.j.B;
import com.miui.permcenter.autostart.AutoStartManagementActivity;
import com.miui.permcenter.install.PackageManagerActivity;
import com.miui.permcenter.permissions.AppPermissionsTabActivity;
import com.miui.permcenter.root.RootManagementActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.utils.d;
import miui.app.ActionBar;
import miui.os.Build;
import miuix.preference.s;

public class MainAcitivty extends b.b.c.c.a {

    public static class a extends s implements Preference.c {

        /* renamed from: a  reason: collision with root package name */
        private Activity f6030a;

        /* renamed from: b  reason: collision with root package name */
        private Preference f6031b;

        /* renamed from: c  reason: collision with root package name */
        private Preference f6032c;

        /* renamed from: d  reason: collision with root package name */
        private Preference f6033d;
        private Preference e;
        private Preference f;
        private int g;

        public void onCreatePreferences(Bundle bundle, String str) {
            Preference preference;
            Intent intent;
            int i;
            Preference preference2;
            addPreferencesFromResource(R.xml.pm_main_page);
            this.f6030a = getActivity();
            this.f6031b = findPreference("handle_item_auto_start");
            this.f6031b.setIntent(new Intent(this.f6030a, AutoStartManagementActivity.class));
            this.f6032c = findPreference("handle_item_permissions");
            if (Build.IS_INTERNATIONAL_BUILD) {
                preference = this.f6032c;
                intent = new Intent("android.intent.action.MANAGE_PERMISSIONS");
            } else {
                preference = this.f6032c;
                intent = new Intent(this.f6030a, AppPermissionsTabActivity.class).putExtra(":miui:starting_window_label", "");
            }
            preference.setIntent(intent);
            this.f6033d = findPreference("handle_item_other_permissions");
            this.f6033d.setIntent(new Intent(this.f6030a, AppPermissionsTabActivity.class).putExtra(":miui:starting_window_label", ""));
            this.e = findPreference("handle_item_adb");
            this.e.setIntent(new Intent(this.f6030a, PackageManagerActivity.class));
            this.f = findPreference("handle_item_root");
            this.f.setOnPreferenceClickListener(this);
            if (Build.IS_STABLE_VERSION || Build.IS_INTERNATIONAL_BUILD || !B.f()) {
                getPreferenceScreen().d(this.f);
            }
            if (!d.b() && !d.e()) {
                if ("unlocked".equals(d.a()) || !d.d()) {
                    preference2 = this.f;
                    i = R.string.activity_title_root_acquired;
                } else {
                    preference2 = this.f;
                    i = R.string.activity_title_root_note;
                }
                preference2.setTitle(i);
            }
            if (!Build.IS_INTERNATIONAL_BUILD) {
                getPreferenceScreen().d(this.f6033d);
            }
        }

        public boolean onPreferenceClick(Preference preference) {
            Intent intent;
            String key = preference.getKey();
            if ("open_debug".equals(key)) {
                this.g++;
                if (this.g > 5) {
                    startActivity(new Intent(this.f6030a, DebugSettingsAcitivty.class));
                }
                return true;
            } else if (!"handle_item_root".equals(key)) {
                return false;
            } else {
                ComponentName componentName = new ComponentName("com.android.updater", "com.miui.permcenter.root.RootAcquiredActivity");
                if (d.b() || d.e()) {
                    intent = new Intent(this.f6030a, RootManagementActivity.class);
                } else if ("unlocked".equals(d.a()) || !d.d()) {
                    Intent intent2 = new Intent();
                    intent2.setComponent(componentName);
                    intent = intent2;
                } else {
                    intent = new Intent("miui.intent.action.PERMISSION_CENTER_SECURITY_WEB_VIEW");
                }
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e2) {
                    Log.e("MainAcitivty", "ActivityNotFoundException", e2);
                    Toast.makeText(this.f6030a, R.string.open_activity_err, 0).show();
                }
                return true;
            }
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.miui.permcenter.MainAcitivty, miui.app.Activity] */
    private void a(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setDisplayOptions(16, 16);
            ImageView imageView = new ImageView(this);
            imageView.setContentDescription(getString(R.string.Setting_lock));
            imageView.setBackgroundResource(R.drawable.applock_settings);
            imageView.setOnClickListener(new e(this));
            actionBar.setEndView(imageView);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (n.a() || com.miui.permcenter.install.d.h()) {
            a(getActionBar());
        }
        if (bundle == null) {
            getFragmentManager().beginTransaction().replace(16908290, new a()).commit();
        }
    }
}
