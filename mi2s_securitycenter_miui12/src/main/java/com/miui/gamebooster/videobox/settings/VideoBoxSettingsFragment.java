package com.miui.gamebooster.videobox.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import b.b.c.j.e;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.videobox.utils.f;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import miui.app.AlertDialog;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class VideoBoxSettingsFragment extends s implements Preference.b, Preference.c {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final SparseIntArray f5187a = new SparseIntArray();

    /* renamed from: b  reason: collision with root package name */
    private Activity f5188b;

    /* renamed from: c  reason: collision with root package name */
    private CheckBoxPreference f5189c;

    /* renamed from: d  reason: collision with root package name */
    private CheckBoxPreference f5190d;
    private PreferenceCategory e;
    /* access modifiers changed from: private */
    public TextPreference f;
    private DropDownPreference g;
    private PreferenceCategory h;
    private TextPreference i;

    static {
        f5187a.put(0, R.string.videobox_settings_line_location_left);
        f5187a.put(1, R.string.videobox_settings_line_location_right);
    }

    private void a() {
        this.f5189c.setChecked(f.b((Context) this.f5188b));
        this.f5190d.setChecked(f.f());
        b();
        int size = f.a((ArrayList<String>) new ArrayList()).size();
        this.i.a(getResources().getQuantityString(R.plurals.videobox_settings_manager_apps_count, size, new Object[]{Integer.valueOf(size)}));
    }

    private void a(boolean z) {
        TextPreference textPreference = this.f;
        if (textPreference != null) {
            textPreference.setEnabled(z);
        }
        DropDownPreference dropDownPreference = this.g;
        if (dropDownPreference != null) {
            dropDownPreference.setEnabled(z);
        }
    }

    private void b() {
        String string = getString(f5187a.get(f.e()));
        TextPreference textPreference = this.f;
        if (textPreference != null) {
            textPreference.a(string);
        }
        DropDownPreference dropDownPreference = this.g;
        if (dropDownPreference != null) {
            dropDownPreference.b(string);
        }
    }

    private void b(boolean z) {
        PreferenceScreen preferenceScreen;
        Preference preference;
        PreferenceCategory preferenceCategory;
        Preference preference2;
        if (z) {
            this.e.b((Preference) this.f5190d);
            if (e.b() < 10) {
                preferenceCategory = this.e;
                preference2 = this.f;
            } else {
                preferenceCategory = this.e;
                preference2 = this.g;
            }
            preferenceCategory.b(preference2);
            getPreferenceScreen().b((Preference) this.e);
            this.h.b((Preference) this.i);
            preferenceScreen = getPreferenceScreen();
            preference = this.h;
        } else {
            this.e.e();
            this.h.e();
            getPreferenceScreen().e();
            preferenceScreen = getPreferenceScreen();
            preference = this.f5189c;
        }
        preferenceScreen.b(preference);
    }

    private void c() {
        new AlertDialog.Builder(this.f5188b).setTitle(R.string.videobox_settings_line_location).setSingleChoiceItems(new String[]{getString(R.string.videobox_settings_line_location_left), getString(R.string.videobox_settings_line_location_right)}, f.e() == 0 ? 0 : 1, new g(this)).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    private void d() {
        startActivity(new Intent(this.f5188b, VideoBoxAppManageActivity.class));
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        this.f5188b = getActivity();
        if (!Utils.a(this.f5188b)) {
            addPreferencesFromResource(R.xml.videobox_settings);
            this.f5189c = (CheckBoxPreference) findPreference(getString(R.string.preference_key_videobox_switch));
            this.f5190d = (CheckBoxPreference) findPreference(getString(R.string.preference_key_videobox_line_status));
            this.e = (PreferenceCategory) findPreference(getString(R.string.preference_category_videobox_line_group));
            this.g = (DropDownPreference) findPreference(getString(R.string.preference_key_videobox_line_location_in_new));
            this.h = (PreferenceCategory) findPreference(getString(R.string.preference_category_videobox_app_group));
            this.i = (TextPreference) findPreference(getString(R.string.preference_key_videobox_manager_apps));
            this.f5189c.setOnPreferenceChangeListener(this);
            this.f5190d.setOnPreferenceChangeListener(this);
            this.g.setOnPreferenceChangeListener(this);
            this.i.setOnPreferenceClickListener(this);
            boolean b2 = f.b((Context) this.f5188b);
            boolean f2 = f.f();
            this.f5190d.setEnabled(b2);
            a(b2 && f2);
            this.i.setEnabled(b2);
            if (e.b() < 10) {
                this.e.d(this.g);
                this.f = new TextPreference(getPreferenceManager().a());
                this.f.setKey(getString(R.string.preference_key_videobox_line_location));
                this.f.setTitle((int) R.string.videobox_settings_line_location);
                this.f.setOnPreferenceClickListener(this);
                this.e.b((Preference) this.f);
            }
            a();
            if (!b2) {
                b(false);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean z = false;
        boolean booleanValue = obj instanceof Boolean ? ((Boolean) obj).booleanValue() : false;
        if (preference.getKey().equals(getString(R.string.preference_key_videobox_switch))) {
            b(booleanValue);
            f.a(this.f5188b, booleanValue);
            if (booleanValue && f.f()) {
                z = true;
            }
            a(z);
            this.i.setEnabled(booleanValue);
            this.f5190d.setEnabled(booleanValue);
            C0373d.a.c(booleanValue);
            f.a(booleanValue);
            return true;
        } else if (preference.getKey().equals(getString(R.string.preference_key_videobox_line_status))) {
            a(booleanValue);
            f.e(booleanValue);
            C0373d.a.b(booleanValue);
            return true;
        } else {
            if (preference.getKey().equals(getString(R.string.preference_key_videobox_line_location_in_new)) && (obj instanceof String)) {
                String str = (String) obj;
                this.g.b(str);
                f.d(TextUtils.equals(str, getString(R.string.videobox_settings_line_location_left)) ^ true ? 1 : 0);
            }
            return false;
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.preference_key_videobox_line_location))) {
            c();
            return true;
        } else if (!preference.getKey().equals(getString(R.string.preference_key_videobox_manager_apps))) {
            return false;
        } else {
            d();
            return true;
        }
    }

    public void onResume() {
        super.onResume();
        a();
    }
}
