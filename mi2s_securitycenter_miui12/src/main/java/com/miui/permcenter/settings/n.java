package com.miui.permcenter.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.miui.permcenter.permissions.AppPermissionsTabActivity;
import com.miui.permcenter.settings.model.DangerPermissionPreference;
import com.miui.permcenter.settings.model.MaskIdPreference;
import com.miui.permcenter.settings.model.TitleValuePreference;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.g;
import miui.app.AlertDialog;
import miuix.preference.s;

public class n extends s {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6553a = "n";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Preference f6554b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TitleValuePreference f6555c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Preference f6556d;
    private DangerPermissionPreference e;
    private PreferenceCategory f;
    /* access modifiers changed from: private */
    public Preference g;
    private MaskIdPreference h;
    private Preference.c i = new l(this);

    private String a() {
        return g.a(Application.d().getApplicationContext());
    }

    /* access modifiers changed from: private */
    public void b() {
        View inflate = View.inflate(getActivity(), R.layout.pm_setting_dialog_danger_permission, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.message)).setText(R.string.privacy_dialog_camera_message);
        ((ImageView) inflate.findViewById(R.id.image)).setImageResource(R.drawable.pm_setting_icon_use_camera);
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.privacy_dialog_camera_title)).setView(inflate).setPositiveButton(getResources().getString(R.string.button_text_known), new m(this)).create().show();
    }

    /* access modifiers changed from: private */
    public void c() {
        Intent intent = new Intent(getActivity(), AppPermissionsTabActivity.class);
        intent.putExtra("select_navi_item", 0);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void d() {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    /* access modifiers changed from: private */
    public void e() {
        try {
            startActivity(Intent.parseUri(Build.VERSION.SDK_INT <= 28 ? "#Intent;component=com.android.settings/.SubSettings;S.:settings:show_fragment=com.android.settings.applications.SpecialAccessSettings;end" : "#Intent;component=com.android.settings/.SubSettings;S.:settings:show_fragment=com.android.settings.applications.specialaccess.SpecialAccessSettings;end", 0));
        } catch (Exception unused) {
            Log.e(f6553a, "startSpecPermissionSetting error");
        }
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.pm_settings_manage, str);
        this.f6554b = findPreference("key_pm_setting_location_info");
        this.f6555c = (TitleValuePreference) findPreference("key_pm_setting_permission_camera");
        this.f6556d = findPreference("key_pm_setting_special_permission");
        this.e = (DangerPermissionPreference) findPreference("key_pm_setting_danger_permission");
        this.f = (PreferenceCategory) findPreference("key_pm_setting_camera_phone");
        this.g = findPreference("key_pm_setting_other_permission");
        this.h = (MaskIdPreference) findPreference("key_pm_mask");
        this.f6554b.setOnPreferenceClickListener(this.i);
        this.f6555c.setOnPreferenceClickListener(this.i);
        this.f6556d.setOnPreferenceClickListener(this.i);
        this.g.setOnPreferenceClickListener(this.i);
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            this.f6554b.setVisible(false);
            this.f.setVisible(false);
            this.g.setVisible(true);
        }
    }

    public void onResume() {
        boolean z;
        MaskIdPreference maskIdPreference;
        super.onResume();
        this.e.a();
        if (TextUtils.isEmpty(a())) {
            maskIdPreference = this.h;
            z = false;
        } else {
            maskIdPreference = this.h;
            z = true;
        }
        maskIdPreference.setVisible(z);
    }
}
