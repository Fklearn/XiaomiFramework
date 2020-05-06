package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.miui.maml.util.BaseMobileDataUtils;
import com.miui.powercenter.autotask.X;
import com.miui.powercenter.utils.n;
import com.miui.securitycenter.R;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

class T extends A<U> {

    /* renamed from: d  reason: collision with root package name */
    private PreferenceScreen f6727d;
    private DropDownPreference e;
    private DropDownPreference f;
    private DropDownPreference g;
    private DropDownPreference h;
    private DropDownPreference i;
    private DropDownPreference j;
    private DropDownPreference k;
    private TextPreference l;
    private DropDownPreference m;
    private DropDownPreference n;
    private DropDownPreference o;
    private Preference.c p = new O(this);
    private Preference.b q = new Q(this);

    public T(AutoTask autoTask, AutoTask autoTask2) {
        super(autoTask, autoTask2);
    }

    /* access modifiers changed from: private */
    public void a(Context context) {
        X.a(context, this.f6671b, (X.a) new S(this));
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        TextPreference textPreference = (TextPreference) this.f6727d.a((CharSequence) str);
        if (textPreference != null) {
            X.a(textPreference, this.f6671b, str);
            b();
        }
    }

    private boolean a() {
        return this.f6671b.hasOperation("airplane_mode") && ((Integer) this.f6671b.getOperation("airplane_mode")).intValue() == 1;
    }

    /* access modifiers changed from: private */
    public void b() {
        if (a()) {
            this.f6671b.removeOperation("internet");
            this.f.setEnabled(false);
            this.f.a(2);
            return;
        }
        this.f.setEnabled(true);
    }

    public void a(int i2, int i3, Intent intent) {
    }

    public void a(Bundle bundle) {
        ((U) this.f6672c).addPreferencesFromResource(R.xml.pc_operations_edit_v12);
        this.f6727d = (PreferenceScreen) ((U) this.f6672c).findPreference("screen");
        this.e = (DropDownPreference) ((U) this.f6672c).findPreference("memory_clean");
        this.e.setOnPreferenceChangeListener(this.q);
        this.e.setKey("auto_clean_memory");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "auto_clean_memory", this.e);
        this.f = (DropDownPreference) ((U) this.f6672c).findPreference(BaseMobileDataUtils.MOBILE_DATA);
        this.f.setOnPreferenceChangeListener(this.q);
        this.f.setKey("internet");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "internet", this.f);
        this.g = (DropDownPreference) ((U) this.f6672c).findPreference("wifi");
        this.g.setOnPreferenceChangeListener(this.q);
        this.g.setKey("wifi");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "wifi", this.g);
        this.h = (DropDownPreference) ((U) this.f6672c).findPreference("mute");
        this.h.setOnPreferenceChangeListener(this.q);
        this.h.setKey("mute");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "mute", this.h);
        this.i = (DropDownPreference) ((U) this.f6672c).findPreference("vibration");
        this.i.setOnPreferenceChangeListener(this.q);
        this.i.setKey("vibration");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "vibration", this.i);
        this.j = (DropDownPreference) ((U) this.f6672c).findPreference("bluetooth");
        this.j.setOnPreferenceChangeListener(this.q);
        this.j.setKey("bluetooth");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "bluetooth", this.j);
        this.k = (DropDownPreference) ((U) this.f6672c).findPreference("auto_brightness");
        this.k.setOnPreferenceChangeListener(this.q);
        this.k.setKey("auto_brightness");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "auto_brightness", this.k);
        this.l = (TextPreference) ((U) this.f6672c).findPreference("brightness");
        this.l.setOnPreferenceClickListener(this.p);
        this.l.setKey("brightness");
        this.m = (DropDownPreference) ((U) this.f6672c).findPreference("aireplane_mode");
        this.m.setOnPreferenceChangeListener(this.q);
        this.m.setKey("airplane_mode");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "airplane_mode", this.m);
        this.n = (DropDownPreference) ((U) this.f6672c).findPreference("gps");
        this.n.setOnPreferenceChangeListener(this.q);
        this.n.setKey("gps");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "gps", this.n);
        this.o = (DropDownPreference) ((U) this.f6672c).findPreference("sync");
        this.o.setOnPreferenceChangeListener(this.q);
        this.o.setKey("synchronization");
        X.a(((U) this.f6672c).getContext(), this.f6671b, "synchronization", this.o);
    }

    public void b(Bundle bundle) {
        for (String next : this.f6671b.getOperationNames()) {
            if ("brightness".equals(next)) {
                int intValue = (((Integer) this.f6671b.getOperation(next)).intValue() * 100) / n.a(((U) this.f6672c).getContext()).e();
                TextPreference textPreference = this.l;
                textPreference.a(intValue + "%");
            }
        }
        b();
    }
}
