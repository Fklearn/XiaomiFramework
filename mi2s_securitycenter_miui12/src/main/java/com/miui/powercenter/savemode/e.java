package com.miui.powercenter.savemode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.j.d;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.u;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.support.provider.f;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;
import miui.app.TimePickerDialog;
import miui.util.FeatureParser;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class e extends s {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public CheckBoxPreference f7279a;

    /* renamed from: b  reason: collision with root package name */
    private CheckBoxPreference f7280b;

    /* renamed from: c  reason: collision with root package name */
    private CheckBoxPreference f7281c;

    /* renamed from: d  reason: collision with root package name */
    private CheckBoxPreference f7282d;
    private PreferenceCategory e;
    private CheckBoxPreference f;
    /* access modifiers changed from: private */
    public TextPreference g;
    private TextPreference h;
    /* access modifiers changed from: private */
    public CheckBoxPreference i;
    private a j;
    private Preference.b k = new b(this, (b) null);
    TimePickerDialog.OnTimeSetListener l = new b(this);
    TimePickerDialog.OnTimeSetListener m = new c(this);
    Preference.c n = new d(this);

    private static class a extends BroadcastReceiver {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<e> f7283a;

        private a(e eVar) {
            this.f7283a = new WeakReference<>(eVar);
        }

        /* synthetic */ a(e eVar, b bVar) {
            this(eVar);
        }

        public void onReceive(Context context, Intent intent) {
            e eVar = (e) this.f7283a.get();
            if (eVar != null && "miui.intent.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                eVar.f7279a.setChecked(intent.getBooleanExtra("POWER_SAVE_MODE_OPEN", false));
            }
        }
    }

    private static class b implements Preference.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<e> f7284a;

        private b(e eVar) {
            this.f7284a = new WeakReference<>(eVar);
        }

        /* synthetic */ b(e eVar, b bVar) {
            this(eVar);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            e eVar = (e) this.f7284a.get();
            if (eVar == null || !(preference instanceof CheckBoxPreference)) {
                return false;
            }
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if ("enable_power_save_mode".equals(preference.getKey())) {
                d.a(new f(this, eVar, booleanValue));
                com.miui.powercenter.a.a.g(booleanValue);
                com.miui.powercenter.a.a.b(o.e(eVar.getActivity()));
            } else if ("key_ontime_enabled".equals(preference.getKey())) {
                y.f(booleanValue);
                if (booleanValue) {
                    a.b(eVar.getActivity());
                } else {
                    a.a(eVar.getActivity());
                }
                if (eVar != null) {
                    eVar.a(booleanValue);
                }
                com.miui.powercenter.a.a.f(booleanValue);
            } else if ("auto_exit_power_save_mode".equals(preference.getKey())) {
                if (!booleanValue) {
                    new AlertDialog.Builder(eVar.getActivity()).setTitle(R.string.power_save_mode_close_warn_title).setMessage(R.string.power_save_mode_close_warn_text).setPositiveButton(17039370, new i(this)).setNegativeButton(17039360, new h(this, eVar)).setOnCancelListener(new g(this, eVar)).show();
                } else {
                    y.a(true);
                }
                com.miui.powercenter.a.a.e(booleanValue);
            } else if ("close_notification_wakeup".equals(preference.getKey())) {
                y.b(booleanValue);
                com.miui.powercenter.a.a.c(booleanValue);
            } else if ("close_xiaoai_voice_wakeup".equals(preference.getKey())) {
                y.c(booleanValue);
                com.miui.powercenter.a.a.d(booleanValue);
            } else if ("close_aod_display".equals(preference.getKey())) {
                f.b(eVar.getActivity().getContentResolver(), "permit_disable_aod_in_power_save_mode", booleanValue ? 1 : 0);
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void a(int i2) {
        this.h.a(u.b(i2));
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        if (z) {
            this.e.b((Preference) this.g);
            this.e.b((Preference) this.h);
            return;
        }
        this.e.d(this.g);
        this.e.d(this.h);
    }

    private boolean a() {
        return FeatureParser.getBoolean("support_aod", false);
    }

    /* access modifiers changed from: private */
    public void b(int i2) {
        this.g.a(u.b(i2));
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.pc_power_save, str);
        this.f7279a = (CheckBoxPreference) findPreference("enable_power_save_mode");
        this.f7279a.setOnPreferenceChangeListener(this.k);
        this.f7280b = (CheckBoxPreference) findPreference("close_notification_wakeup");
        this.f7280b.setOnPreferenceChangeListener(this.k);
        this.f7281c = (CheckBoxPreference) findPreference("close_xiaoai_voice_wakeup");
        this.f7281c.setOnPreferenceChangeListener(this.k);
        this.f7282d = (CheckBoxPreference) findPreference("close_aod_display");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_other_optimization");
        preferenceCategory.d(this.f7281c);
        if (!a()) {
            preferenceCategory.d(this.f7282d);
        } else {
            CheckBoxPreference checkBoxPreference = this.f7282d;
            boolean z = true;
            if (f.a(getActivity().getContentResolver(), "permit_disable_aod_in_power_save_mode", 1) != 1) {
                z = false;
            }
            checkBoxPreference.setChecked(z);
            this.f7282d.setOnPreferenceChangeListener(this.k);
        }
        this.e = (PreferenceCategory) findPreference("key_schedule_category");
        this.f = (CheckBoxPreference) findPreference("key_ontime_enabled");
        this.f.setOnPreferenceChangeListener(this.k);
        this.g = (TextPreference) findPreference("key_ontime_open_time");
        this.g.setOnPreferenceClickListener(this.n);
        b(y.w());
        this.h = (TextPreference) findPreference("key_ontime_close_time");
        this.h.setOnPreferenceClickListener(this.n);
        a(y.v());
        this.i = (CheckBoxPreference) findPreference("auto_exit_power_save_mode");
        this.i.setOnPreferenceChangeListener(this.k);
        this.j = new a(this, (b) null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("miui.intent.action.POWER_SAVE_MODE_CHANGED");
        getActivity().registerReceiver(this.j, intentFilter);
    }

    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(this.j);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        getActivity().finish();
        return true;
    }

    public void onResume() {
        super.onResume();
        this.f7279a.setChecked(o.l(getActivity()));
        this.f.setChecked(y.u());
        this.i.setChecked(y.a());
        a(this.f.isChecked());
    }
}
