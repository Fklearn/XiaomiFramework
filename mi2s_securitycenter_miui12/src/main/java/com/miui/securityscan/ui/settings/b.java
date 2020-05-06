package com.miui.securityscan.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.j.A;
import b.b.c.j.g;
import b.b.c.j.m;
import b.b.c.j.x;
import com.miui.cleanmaster.f;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securitycenter.service.NotificationService;
import com.miui.securityscan.M;
import com.miui.securityscan.a.G;
import com.miui.securityscan.i.l;
import com.miui.securityscan.shortcut.ShortcutActivity;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.app.AlertDialog;
import miui.os.Build;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class b extends s implements Preference.b, Preference.c {

    /* renamed from: a  reason: collision with root package name */
    private final String f8028a = "preference_key_manual_item_white_list";

    /* renamed from: b  reason: collision with root package name */
    private Preference f8029b;

    /* renamed from: c  reason: collision with root package name */
    private TextPreference f8030c;

    /* renamed from: d  reason: collision with root package name */
    private Preference f8031d;
    private CheckBoxPreference e;
    /* access modifiers changed from: private */
    public CheckBoxPreference f;
    private CheckBoxPreference g;
    private CheckBoxPreference h;
    private Preference i;
    private Preference j;
    private Preference k;
    private Preference l;
    private Preference m;
    private Preference n;
    private String o;
    private AlertDialog p;
    private long q = 0;
    /* access modifiers changed from: private */
    public Context r;
    /* access modifiers changed from: private */
    public Activity s;

    private class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<b> f8032a;

        /* renamed from: b  reason: collision with root package name */
        private Context f8033b;

        public a(b bVar) {
            this.f8032a = new WeakReference<>(bVar);
            this.f8033b = bVar.getActivity().getApplicationContext();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            b bVar = (b) this.f8032a.get();
            if (bVar != null) {
                G.a(true);
                bVar.f.setChecked(false);
                l.a(this.f8033b, false);
                if (Utils.isEarthquakeWarningOpen()) {
                    EarthquakeWarningManager.getInstance().closeEarthquakeWarning(this.f8033b);
                }
                dialogInterface.dismiss();
            }
        }
    }

    /* renamed from: com.miui.securityscan.ui.settings.b$b  reason: collision with other inner class name */
    private class C0068b implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<b> f8035a;

        public C0068b(b bVar) {
            this.f8035a = new WeakReference<>(bVar);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            b bVar = (b) this.f8035a.get();
            if (bVar != null) {
                bVar.f.setChecked(true);
                G.a(false);
                dialogInterface.dismiss();
            }
        }
    }

    private class c implements DialogInterface.OnShowListener {

        /* renamed from: a  reason: collision with root package name */
        private Context f8037a;

        public c(b bVar) {
            this.f8037a = bVar.getActivity().getApplicationContext();
        }

        public void onShow(DialogInterface dialogInterface) {
            AlertDialog alertDialog = (AlertDialog) dialogInterface;
            alertDialog.getButton(-1).setTextColor(this.f8037a.getResources().getColor(R.color.settings_dialog_postive_button_color));
            alertDialog.getButton(-2).setTextColor(-65536);
        }
    }

    private void a() {
        AlertDialog create = new AlertDialog.Builder(this.r).setTitle(R.string.cta_close_dialog_title).setMessage(R.string.cta_close_dialog_content).setCancelable(false).setPositiveButton(R.string.cta_close_dialog_cancel, new C0068b(this)).setNegativeButton(R.string.cta_close_dialog_ok, new a(this)).create();
        create.setOnShowListener(new c(this));
        create.show();
        G.n();
    }

    private void a(boolean z) {
        new a(this, z).start();
    }

    private Intent b(String str, int i2) {
        Intent intent = new Intent(str);
        String string = getString(i2);
        intent.putExtra(":miui:starting_window_label", string);
        intent.putExtra(Constants.System.EXTRA_SETTINGS_TITLE, string);
        intent.putExtra("extra_settings_title_res", i2);
        intent.putExtra("enter_way", "security_settings");
        return intent;
    }

    private void b() {
        this.p = new AlertDialog.Builder(this.r).setTitle(R.string.menu_item_about_title).setMessage(getString(R.string.menu_item_about_content, new Object[]{this.o})).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).show();
    }

    public void onActivityResult(int i2, int i3, Intent intent) {
        super.onActivityResult(i2, i3, intent);
        if (i2 != 300) {
            return;
        }
        if (i3 == 0) {
            this.f.setChecked(false);
        } else if (i3 == 1) {
            this.f.setChecked(true);
            l.a(this.r.getApplicationContext(), true);
        }
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.m_settings, str);
        this.r = getContext();
        this.s = getActivity();
        this.o = x.a(this.r);
        this.f8029b = findPreference(getString(R.string.preference_key_create_shortcut));
        this.f8030c = (TextPreference) findPreference(getString(R.string.preference_key_about_version));
        this.f8031d = findPreference("preference_key_manual_item_white_list");
        this.f8030c.a(getString(R.string.menu_item_about_summary, new Object[]{this.o}));
        this.f8031d.setIntent(new Intent(this.r, WhiteListActivity.class));
        this.f8029b.setIntent(new Intent(this.r, ShortcutActivity.class));
        this.f8030c.setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_key_about_privacy)).setOnPreferenceClickListener(this);
        this.e = (CheckBoxPreference) findPreference(getString(R.string.preference_key_create_permanent_notification));
        this.e.setOnPreferenceChangeListener(this);
        this.e.setChecked(h.a(this.r.getContentResolver()));
        this.f = (CheckBoxPreference) findPreference(getString(R.string.preference_key_cta_settings));
        this.f.setOnPreferenceChangeListener(this);
        this.f.setChecked(h.i());
        if (Build.IS_INTERNATIONAL_BUILD) {
            ((PreferenceCategory) findPreference(getString(R.string.preference_key_category_title_module_settings))).d(this.f);
        }
        this.i = findPreference(getString(R.string.preference_key_module_garbage_cleanup));
        this.i.setOnPreferenceClickListener(this);
        this.j = findPreference(getString(R.string.preference_key_module_network_assistant));
        this.j.setIntent(b(Constants.App.ACTION_NETWORK_ASSISTANT_SETTING, R.string.Settings_title_network_assistants));
        this.k = findPreference(getString(R.string.preference_key_module_antipam));
        this.k.setOnPreferenceClickListener(this);
        this.l = findPreference(getString(R.string.preference_key_module_power_center));
        this.l.setIntent(b("com.miui.securitycenter.action.POWER_SETTINGS", R.string.Settings_title_power_center));
        this.m = findPreference(getString(R.string.preference_key_module_antivirus));
        this.m.setIntent(b("com.miui.securitycenter.action.ANTIVIRUS_SETTINGS", R.string.Settings_title_anti_virus));
        this.n = findPreference(getString(R.string.preference_key_optimize_manage));
        this.n.setIntent(b("miui.intent.action.OPTIMIZE_MANAGE_SETTINGS", R.string.om_settings_title));
        this.g = (CheckBoxPreference) findPreference(getString(R.string.preference_key_information_setting_wlan));
        this.g.setOnPreferenceChangeListener(this);
        this.g.setChecked(M.l());
        this.h = (CheckBoxPreference) findPreference(getString(R.string.preference_key_information_setting_close));
        this.h.setOnPreferenceChangeListener(this);
        this.h.setChecked(M.m());
        if (!f.a(this.r)) {
            ((PreferenceCategory) findPreference(getString(R.string.preference_key_category_title_module_feature_settings))).d(this.i);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.p;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.p.dismiss();
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference.getKey().equals(getString(R.string.preference_key_create_permanent_notification))) {
            h.a(this.r.getContentResolver(), booleanValue);
            Intent intent = new Intent(this.r, NotificationService.class);
            if (booleanValue) {
                intent.putExtra("notify", false);
                m.a(this.r, intent);
            } else {
                this.r.stopService(intent);
            }
            return true;
        } else if (preference.getKey().equals(getString(R.string.preference_key_cta_settings))) {
            if (System.currentTimeMillis() - this.q < 500) {
                return false;
            }
            this.q = System.currentTimeMillis();
            if (booleanValue) {
                String locale = Locale.getDefault().toString();
                String region = Build.getRegion();
                Intent intent2 = new Intent("miui.intent.action.SYSTEM_PERMISSION_DECLARE");
                intent2.putExtra("all_purpose", getResources().getString(R.string.cta_main_purpose));
                intent2.putExtra("agree_desc", getResources().getString(R.string.cta_agree_desc));
                intent2.putExtra("privacy_policy", "https://api.sec.miui.com/res/docs/disclaimer/privacy?lang=" + locale + "&region=" + region);
                intent2.putExtra("mandatory_permission", false);
                intent2.putExtra("runtime_perm", new String[]{"android.permission-group.LOCATION"});
                intent2.putExtra("runtime_perm_desc", new String[]{getResources().getString(R.string.cta_HIPS_Perm_Location_Desc)});
                startActivityForResult(intent2, 300);
            } else {
                a();
            }
            return true;
        } else if (preference.getKey().equals(getString(R.string.preference_key_information_setting_wlan))) {
            M.c(booleanValue);
            return true;
        } else if (!preference.getKey().equals(getString(R.string.preference_key_information_setting_close))) {
            return false;
        } else {
            M.d(booleanValue);
            com.miui.securityscan.f.a.a(this.r, booleanValue);
            a(!booleanValue);
            return true;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        Intent intent;
        if (preference.getKey().equals(getString(R.string.preference_key_about_privacy))) {
            if (Build.IS_INTERNATIONAL_BUILD) {
                String language = Locale.getDefault().getLanguage();
                String country = Locale.getDefault().getCountry();
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://privacy.mi.com/all/" + language + "_" + country));
            } else {
                String locale = Locale.getDefault().toString();
                String region = Build.getRegion();
                intent = new Intent("android.intent.action.VIEW", Uri.parse("https://api.sec.miui.com/res/docs/disclaimer/privacy?lang=" + locale + "&region=" + region));
            }
            startActivity(intent);
            return true;
        } else if (preference.getKey().equals(getString(R.string.preference_key_about_version))) {
            b();
            return true;
        } else if (preference.getKey().equals(getString(R.string.preference_key_module_garbage_cleanup))) {
            if (!x.c(this.r, b("com.miui.securitycenter.action.GARBAGE_CLEANUP_SETTINGS", R.string.Settings_title_garbage_cleanup))) {
                A.a(this.r, (int) R.string.app_not_installed_toast);
            }
            return true;
        } else if (!preference.getKey().equals(getString(R.string.preference_key_module_antipam))) {
            return false;
        } else {
            if (g.a(this.r) != 0) {
                Toast.makeText(this.r, R.string.antispam_xpace_text, 0).show();
                return true;
            }
            startActivity(b("com.miui.antispam.action.ANTISPAM_SETTINGS", R.string.Settings_title_anti_spam));
            return true;
        }
    }

    public void onResume() {
        super.onResume();
        this.f.setChecked(h.i());
    }
}
