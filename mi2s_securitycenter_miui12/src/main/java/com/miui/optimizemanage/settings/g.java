package com.miui.optimizemanage.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.miui.optimizemanage.memoryclean.LockAppManageActivity;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.app.AlertDialog;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class g extends s {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public TextPreference f5997a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public DropDownPreference f5998b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public DropDownPreference f5999c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public CheckBoxPreference f6000d;
    private PreferenceCategory e;
    private AlertDialog f;
    private AlertDialog g;
    /* access modifiers changed from: private */
    public TextPreference h;
    private Preference.c i = new e(this);
    private Preference.b j = new f(this);

    private static class a implements DialogInterface.OnClickListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<g> f6001a;

        /* renamed from: b  reason: collision with root package name */
        private int[] f6002b;

        /* renamed from: c  reason: collision with root package name */
        private String[] f6003c;

        public a(g gVar, int[] iArr, String[] strArr) {
            this.f6001a = new WeakReference<>(gVar);
            this.f6002b = iArr;
            this.f6003c = strArr;
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            g gVar = (g) this.f6001a.get();
            if (gVar != null) {
                y.d(this.f6002b[i] * 60);
                gVar.h.a(this.f6003c[i]);
                dialogInterface.dismiss();
            }
        }
    }

    /* access modifiers changed from: private */
    public String a(int i2) {
        if (i2 == 0) {
            return getString(R.string.deep_clean_never_memory_clean);
        }
        return getResources().getQuantityString(R.plurals.deep_clean_auto_memory_clean, i2, new Object[]{Integer.valueOf(i2)});
    }

    private int b() {
        return y.l() / 60;
    }

    /* access modifiers changed from: private */
    public String b(int i2) {
        if (i2 == 0) {
            return getString(R.string.om_settings_memory_occupy_notify_never);
        }
        return i2 + "%";
    }

    /* access modifiers changed from: private */
    public void c() {
        int[] intArray = getResources().getIntArray(R.array.pc_time_choice_items);
        int b2 = b();
        int i2 = 0;
        while (true) {
            if (i2 >= intArray.length) {
                i2 = 0;
                break;
            } else if (intArray[i2] == b2) {
                break;
            } else {
                i2++;
            }
        }
        String[] strArr = new String[intArray.length];
        for (int i3 = 0; i3 < strArr.length; i3++) {
            strArr[i3] = a(intArray[i3]);
        }
        AlertDialog alertDialog = this.f;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.f = null;
        }
        this.f = new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.deep_clean_memory_clean_title)).setSingleChoiceItems(strArr, i2, new a(this, intArray, strArr)).setNegativeButton(getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    /* access modifiers changed from: private */
    public void d() {
        startActivity(new Intent(getActivity(), LockAppManageActivity.class));
    }

    private void e() {
        this.f5999c.b(b(c.g()));
    }

    public void a() {
        Activity activity = getActivity();
        if (activity != null) {
            int l = ((SettingsActivity) activity).l();
            this.f5997a.a(String.format(Locale.getDefault(), "%d", new Object[]{Integer.valueOf(l)}));
            c.b(l);
        }
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        a();
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.om_settings, str);
        this.f5997a = (TextPreference) findPreference("preference_key_lock_app_manage");
        this.f5998b = (DropDownPreference) findPreference("preference_key_memory_clean_lock_screen");
        this.f5999c = (DropDownPreference) findPreference("preference_key_memory_occupy_notify");
        this.f6000d = (CheckBoxPreference) findPreference("preference_key_cpu_over_load");
        this.e = (PreferenceCategory) findPreference("preference_key_notify_manage_category");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("category_key_memory_clean_lock_screen");
        this.f5997a.setOnPreferenceClickListener(this.i);
        this.f5998b.setOnPreferenceChangeListener(this.j);
        this.f5999c.setOnPreferenceChangeListener(this.j);
        if (!c.j()) {
            this.e.d(this.f5999c);
        }
        if (k.a() < 10) {
            preferenceCategory.d(this.f5998b);
            this.h = new TextPreference(getPreferenceManager().a());
            this.h.setKey("preference_key_memory_clean_lock_screen_old");
            this.h.setTitle((int) R.string.om_settings_memory_clean_lock_screen);
            this.h.setOnPreferenceClickListener(this.i);
            preferenceCategory.b((Preference) this.h);
        }
        this.f6000d.setOnPreferenceChangeListener(this.j);
        getPreferenceScreen().d(this.e);
    }

    public void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.f;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        AlertDialog alertDialog2 = this.g;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        getActivity().onBackPressed();
        return true;
    }

    public void onResume() {
        super.onResume();
        a();
        e();
        DropDownPreference dropDownPreference = this.f5998b;
        if (dropDownPreference != null) {
            dropDownPreference.b(a(b()));
        }
        TextPreference textPreference = this.h;
        if (textPreference != null) {
            textPreference.a(a(b()));
        }
        this.f6000d.setChecked(c.i());
    }
}
