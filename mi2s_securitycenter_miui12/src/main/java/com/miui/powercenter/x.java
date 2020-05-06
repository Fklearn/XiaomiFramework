package com.miui.powercenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import b.b.c.c.b.l;
import b.b.c.j.B;
import b.b.c.j.i;
import b.b.o.g.e;
import com.miui.powercenter.bootshutdown.PowerShutdownOnTime;
import com.miui.powercenter.savemode.PowerSaveActivity;
import com.miui.powercenter.utils.g;
import com.miui.powercenter.utils.r;
import com.miui.securitycenter.R;
import com.miui.superpower.SuperPowerSettings;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import miui.app.AlertDialog;
import miui.os.Build;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public class x extends l {

    /* renamed from: a  reason: collision with root package name */
    private static Set<String> f7366a = new HashSet(Arrays.asList(new String[]{"jason", "chiron", "polaris", "equuleus", "ursa", "beryllium", "sirius", "platina", "dipper", "nitrogen"}));
    /* access modifiers changed from: private */
    public int[] A;
    /* access modifiers changed from: private */
    public String[] B;
    private String[] C;
    private String[] D;
    private String[] E = {"default", "performance"};
    private Preference.b F = new p(this);
    private Preference.b G = new q(this);
    private Preference.c H = new r(this);
    private ContentObserver I = new s(this, new Handler(Looper.getMainLooper()));
    private BroadcastReceiver J = new w(this);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public DropDownPreference f7367b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public TextPreference f7368c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public DropDownPreference f7369d;
    /* access modifiers changed from: private */
    public TextPreference e;
    private TextPreference f;
    private TextPreference g;
    /* access modifiers changed from: private */
    public CheckBoxPreference h;
    private CheckBoxPreference i;
    private TextPreference j;
    /* access modifiers changed from: private */
    public TextPreference k;
    private TextPreference l;
    private TextPreference m;
    private ListPreference n;
    private PreferenceCategory o;
    private PreferenceCategory p;
    private PreferenceScreen q;
    private PreferenceCategory r;
    /* access modifiers changed from: private */
    public CheckBoxPreference s;
    private boolean t;
    /* access modifiers changed from: private */
    public com.miui.powercenter.g.a u;
    /* access modifiers changed from: private */
    public int v;
    /* access modifiers changed from: private */
    public int[] w;
    /* access modifiers changed from: private */
    public String[] x;
    private String[] y;
    /* access modifiers changed from: private */
    public int z;

    private static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<x> f7370a;

        public a(x xVar) {
            this.f7370a = new WeakReference<>(xVar);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            x xVar = (x) this.f7370a.get();
            if (xVar != null) {
                g.a(xVar.getActivity(), 0);
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        }
    }

    private static class b implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<x> f7371a;

        public b(x xVar) {
            this.f7371a = new WeakReference<>(xVar);
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            x xVar = (x) this.f7371a.get();
            if (xVar != null) {
                xVar.h.setChecked(true);
                com.miui.powercenter.a.a.b("5g_close_later");
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        }
    }

    private String a(int i2) {
        Resources resources;
        int i3;
        if (i2 == 0) {
            resources = getResources();
            i3 = R.string.power_settings_shape;
        } else if (i2 == 1) {
            resources = getResources();
            i3 = R.string.power_settings_number;
        } else if (i2 != 2) {
            return "";
        } else {
            resources = getResources();
            i3 = R.string.power_settings_top;
        }
        return resources.getString(i3);
    }

    private void a(String str) {
        if (this.n != null) {
            r.a((Context) getActivity(), str);
            Settings.System.putString(getActivity().getContentResolver(), "warm_control", str);
            this.n.b(str);
            if (r.c()) {
                int i2 = !"default".equals(str);
                Log.i("PowerSettings", "saveThermalConfig------>" + i2);
                this.n.setSummary(this.D[i2]);
                r.a(i2, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z2) {
        this.u.a(z2);
    }

    private String b(int i2) {
        if (i2 == 0) {
            return getString(R.string.deep_clean_never_memory_clean);
        }
        return getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, i2, new Object[]{Integer.valueOf(i2)});
    }

    /* access modifiers changed from: private */
    public void b(String str) {
        a(str);
        r.a(str);
    }

    public static boolean b() {
        return f7366a.contains(Build.DEVICE);
    }

    /* access modifiers changed from: private */
    public String c(int i2) {
        if (i2 == 0) {
            return getString(R.string.deep_clean_never_memory_clean);
        }
        return getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, i2, new Object[]{Integer.valueOf(i2)});
    }

    /* access modifiers changed from: private */
    public void c() {
        if (this.n == null) {
            return;
        }
        if ("ultimate_extra".equals(r.a((Context) getActivity(), 0))) {
            this.n.setEnabled(false);
            return;
        }
        String a2 = r.a((Context) getActivity());
        this.n.b(a2);
        this.n.setSummary(this.D[!"default".equals(a2)]);
    }

    private int d() {
        try {
            String str = (String) e.a(Class.forName("android.provider.MiuiSettings$System"), "BATTERY_INDICATOR_STYLE", String.class);
            int intValue = ((Integer) e.a(Class.forName("android.provider.MiuiSettings$System"), "BATTERY_INDICATOR_STYLE_DEFAULT", Integer.TYPE)).intValue();
            if (!TextUtils.isEmpty(str)) {
                return Settings.System.getInt(getActivity().getContentResolver(), str, intValue);
            }
            return 0;
        } catch (Exception e2) {
            Log.e("PowerSettings", "getBatteryStyleValue: ", e2);
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void d(int i2) {
        try {
            String str = (String) e.a(Class.forName("android.provider.MiuiSettings$System"), "BATTERY_INDICATOR_STYLE", String.class);
            if (!TextUtils.isEmpty(str)) {
                Settings.System.putInt(getActivity().getContentResolver(), str, i2);
            }
        } catch (Exception e2) {
            Log.e("PowerSettings", "getBatteryStyleValue: ", e2);
        }
    }

    private int e() {
        return y.i() / 60;
    }

    /* access modifiers changed from: private */
    public int f() {
        return y.l() / 60;
    }

    private boolean g() {
        Sensor sensor;
        try {
            sensor = (Sensor) e.b((SensorManager) getActivity().getSystemService("sensor"), Sensor.class, "getDefaultSensor", new Class[]{Integer.TYPE, Boolean.TYPE}, 33171027, true);
        } catch (Exception e2) {
            e2.printStackTrace();
            sensor = null;
        }
        return sensor != null;
    }

    private void h() {
        if (!i.e()) {
            this.k.a(a(d()));
        }
        CheckBoxPreference checkBoxPreference = this.h;
        if (checkBoxPreference != null) {
            checkBoxPreference.setEnabled(!g.a(getContext()) && g.c(getContext()));
        }
    }

    /* access modifiers changed from: private */
    public void i() {
        try {
            startActivity(new Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST"));
            com.miui.powercenter.a.a.d("app_smart_save");
        } catch (Exception e2) {
            miui.util.Log.d("PowerSettings", "can not find hide mode action", e2);
        }
    }

    private void initData() {
        this.A = getResources().getIntArray(R.array.pc_disconnect_data_time_choice_items);
        int i2 = 0;
        this.z = 0;
        int e2 = e();
        int i3 = 0;
        while (true) {
            int[] iArr = this.A;
            if (i3 >= iArr.length) {
                break;
            } else if (iArr[i3] == e2) {
                this.z = i3;
                break;
            } else {
                i3++;
            }
        }
        int[] iArr2 = this.A;
        this.B = new String[iArr2.length];
        this.C = new String[iArr2.length];
        int i4 = 0;
        while (true) {
            String[] strArr = this.B;
            if (i4 >= strArr.length) {
                break;
            }
            strArr[i4] = b(this.A[i4]);
            this.C[i4] = String.valueOf(i4);
            i4++;
        }
        this.w = getResources().getIntArray(R.array.pc_time_choice_items);
        this.v = 0;
        int f2 = f();
        int i5 = 0;
        while (true) {
            int[] iArr3 = this.w;
            if (i5 >= iArr3.length) {
                break;
            } else if (iArr3[i5] == f2) {
                this.v = i5;
                break;
            } else {
                i5++;
            }
        }
        int[] iArr4 = this.w;
        this.x = new String[iArr4.length];
        this.y = new String[iArr4.length];
        while (true) {
            String[] strArr2 = this.x;
            if (i2 < strArr2.length) {
                strArr2[i2] = c(this.w[i2]);
                this.y[i2] = String.valueOf(i2);
                i2++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void j() {
        int[] intArray = getResources().getIntArray(R.array.pc_choice_dialog_battery_style_values);
        int d2 = d();
        int i2 = 0;
        while (true) {
            if (i2 >= intArray.length) {
                i2 = 0;
                break;
            } else if (intArray[i2] == d2) {
                break;
            } else {
                i2++;
            }
        }
        String[] strArr = new String[intArray.length];
        for (int i3 = 0; i3 < strArr.length; i3++) {
            strArr[i3] = a(intArray[i3]);
        }
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.power_settings_category_title)).setSingleChoiceItems(strArr, i2, new v(this, intArray, strArr)).setNegativeButton(getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void k() {
        startActivity(new Intent(getActivity(), PowerShutdownOnTime.class));
    }

    /* access modifiers changed from: private */
    public void l() {
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.power_center_5g_save_close_title)).setMessage(getResources().getString(R.string.power_center_5g_save_close_detail)).setCancelable(false).setPositiveButton(getResources().getString(R.string.power_center_5g_save_close_later), new b(this)).setNegativeButton(17039370, new a(this)).show();
        com.miui.powercenter.a.a.a();
    }

    /* access modifiers changed from: private */
    public void m() {
        startActivity(new Intent("miui.intent.action.POWER_SCENARIO_POLICY_ACTION"));
    }

    /* access modifiers changed from: private */
    public void n() {
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.deep_clean_disconnect_data_title)).setSingleChoiceItems(this.B, this.z, new u(this)).setNegativeButton(getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void o() {
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.deep_clean_memory_clean_title)).setSingleChoiceItems(this.x, this.v, new t(this)).setNegativeButton(getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void p() {
        startActivity(new Intent(getActivity(), PowerSaveActivity.class));
        com.miui.powercenter.a.a.d("save_mode");
    }

    /* access modifiers changed from: private */
    public void q() {
        startActivity(new Intent(getActivity(), SuperPowerSettings.class));
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(a() ? R.xml.pc_power_settings_v12 : R.xml.pc_power_settings, str);
        this.q = getPreferenceScreen();
        initData();
        if (a()) {
            this.f7367b = (DropDownPreference) findPreference("preference_key_deep_save_disconnect_in_lockscreen");
            this.f7367b.a((CharSequence[]) this.B);
            this.f7367b.b((CharSequence[]) this.C);
            this.f7367b.a(this.z);
            this.f7367b.setOnPreferenceChangeListener(this.G);
        } else {
            this.f7368c = (TextPreference) findPreference("preference_key_deep_save_disconnect_in_lockscreen");
            this.f7368c.setOnPreferenceClickListener(this.H);
            this.f7368c.a(b(e()));
        }
        if (a()) {
            this.f7369d = (DropDownPreference) findPreference("preference_key_deep_save_memory_clean_in_lockscreen");
            this.f7369d.a((CharSequence[]) this.x);
            this.f7369d.b((CharSequence[]) this.y);
            this.f7369d.a(this.v);
            this.f7369d.setOnPreferenceChangeListener(this.G);
        } else {
            this.e = (TextPreference) findPreference("preference_key_deep_save_memory_clean_in_lockscreen");
            this.e.setOnPreferenceClickListener(this.H);
            this.e.a(c(f()));
        }
        this.f = (TextPreference) findPreference("preference_key_settings_power_save");
        this.f.setOnPreferenceClickListener(this.H);
        this.g = (TextPreference) findPreference("preference_key_settings_super_save");
        this.g.setOnPreferenceClickListener(this.H);
        this.o = (PreferenceCategory) findPreference("preference_key_settings_power_save_category");
        this.i = (CheckBoxPreference) findPreference("preference_key_battery_consume_abnormal");
        this.i.setChecked(y.x());
        this.i.setOnPreferenceChangeListener(this.G);
        if (!k.o(getActivity())) {
            this.o.d(this.g);
        }
        this.h = (CheckBoxPreference) findPreference("preference_key_settings_5g_save");
        if (g.b()) {
            this.h.setEnabled(!g.a(getContext()) && g.c(getContext()));
            this.h.setChecked(g.b(getActivity()) == 1);
            this.h.setOnPreferenceChangeListener(this.G);
        } else {
            this.o.d(this.h);
            this.h = null;
        }
        this.r = (PreferenceCategory) findPreference("preference_key_category_features");
        this.s = (CheckBoxPreference) findPreference("preference_key_wireless_reverse_charging");
        this.u = new com.miui.powercenter.g.b(getActivity());
        if (!this.u.a()) {
            this.r.d(this.s);
            this.s = null;
            this.q.d(this.r);
            this.r = null;
            this.u = null;
        } else {
            this.s.setChecked(this.u.b());
            this.s.setOnPreferenceChangeListener(this.G);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("miui.intent.action.ACTION_WIRELESS_CHARGING");
        intentFilter.addAction("miui.intent.action.EXTREME_POWER_SAVE_MODE_CHANGED");
        getActivity().registerReceiver(this.J, intentFilter);
        this.t = true;
        this.p = (PreferenceCategory) findPreference("preference_key_other_category");
        if (B.f() || !i.e()) {
            this.j = (TextPreference) findPreference("preference_key_boot_shutdown_ontime");
            this.j.setOnPreferenceClickListener(this.H);
            this.p.d(this.j);
            this.l = (TextPreference) findPreference("preference_key_background_app_save");
            this.l.setOnPreferenceClickListener(this.H);
            this.k = (TextPreference) findPreference("preference_key_battery_style");
            this.k.setOnPreferenceClickListener(this.H);
            this.k.a(a(d()));
            this.p.d(this.k);
            this.m = (TextPreference) findPreference("preference_key_config_scenario_policies");
            this.m.setOnPreferenceClickListener(this.H);
            if (Build.IS_INTERNATIONAL_BUILD && (!g() || !b())) {
                this.p.d(this.m);
                this.m = null;
            }
            this.n = (ListPreference) findPreference("preference_key_thermal_configure");
            this.n.setOnPreferenceClickListener(this.H);
            this.n.setOnPreferenceChangeListener(this.F);
            this.D = getResources().getStringArray(R.array.pc_settings_thermal_summaries);
            this.n.a((CharSequence[]) this.E);
            if (!r.b((Context) getActivity())) {
                Log.i("PowerSettings", "sIsWarmControlModeSupported---false");
                this.p.d(this.n);
                this.n = null;
            }
            if (B.j() != 0) {
                ListPreference listPreference = this.n;
                if (listPreference != null) {
                    this.p.d(listPreference);
                    this.n = null;
                }
                TextPreference textPreference = this.m;
                if (textPreference != null) {
                    this.p.d(textPreference);
                    this.m = null;
                }
            }
            c();
            getActivity().getContentResolver().registerContentObserver(Uri.withAppendedPath(Uri.parse("content://com.miui.securitycenter.remoteprovider"), "key_memory_clean_time"), false, this.I);
            return;
        }
        this.q.d(this.p);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.I != null) {
            getActivity().getContentResolver().unregisterContentObserver(this.I);
        }
        if (this.t && this.J != null) {
            getActivity().unregisterReceiver(this.J);
            this.t = false;
        }
    }

    public void onResume() {
        h();
        super.onResume();
    }
}
