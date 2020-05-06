package com.miui.powercenter.autotask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.miui.maml.util.BaseMobileDataUtils;
import com.miui.powercenter.autotask.X;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miuix.preference.TextPreference;

class N extends A<U> {

    /* renamed from: d  reason: collision with root package name */
    private PreferenceScreen f6718d;
    private TextPreference e;
    private TextPreference f;
    private TextPreference g;
    private TextPreference h;
    private TextPreference i;
    private TextPreference j;
    private TextPreference k;
    private TextPreference l;
    private TextPreference m;
    private TextPreference n;
    private TextPreference o;
    private Preference.c p;

    private class a implements Preference.c {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<U> f6719a;

        private a(U u) {
            this.f6719a = new WeakReference<>(u);
        }

        /* synthetic */ a(N n, U u, K k) {
            this(u);
        }

        public boolean onPreferenceClick(Preference preference) {
            U u = (U) this.f6719a.get();
            if (u == null) {
                return false;
            }
            String key = preference.getKey();
            if (X.b(key)) {
                N.this.a((Context) u.getActivity(), key);
                return false;
            } else if ("auto_clean_memory".equals(key)) {
                N.this.b((Context) u.getActivity());
                return false;
            } else if (!"brightness".equals(key)) {
                return false;
            } else {
                N.this.a((Context) u.getActivity());
                return false;
            }
        }
    }

    public N(AutoTask autoTask, AutoTask autoTask2) {
        super(autoTask, autoTask2);
    }

    /* access modifiers changed from: private */
    public void a(Context context) {
        X.a(context, this.f6671b, (X.a) new M(this));
    }

    /* access modifiers changed from: private */
    public void a(Context context, String str) {
        X.a(context, this.f6671b, str, (X.a) new K(this));
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        TextPreference textPreference = (TextPreference) this.f6718d.a((CharSequence) str);
        if (textPreference != null) {
            X.a(textPreference, this.f6671b, str);
            b();
        }
    }

    private boolean a() {
        return this.f6671b.hasOperation("airplane_mode") && ((Integer) this.f6671b.getOperation("airplane_mode")).intValue() == 1;
    }

    private void b() {
        if (a()) {
            this.f6671b.removeOperation("internet");
            this.f.setEnabled(false);
            this.f.a("");
            return;
        }
        this.f.setEnabled(true);
    }

    /* access modifiers changed from: private */
    public void b(Context context) {
        X.b(context, this.f6671b, new L(this));
    }

    private void c() {
        TextPreference textPreference;
        for (String next : this.f6671b.getOperationNames()) {
            if ("auto_clean_memory".equals(next)) {
                textPreference = this.e;
            } else if ("internet".equals(next)) {
                textPreference = this.f;
            } else if ("wifi".equals(next)) {
                textPreference = this.g;
            } else if ("mute".equals(next)) {
                textPreference = this.h;
            } else if ("vibration".equals(next)) {
                textPreference = this.i;
            } else if ("bluetooth".equals(next)) {
                textPreference = this.j;
            } else if ("auto_brightness".equals(next)) {
                textPreference = this.k;
            } else if ("brightness".equals(next)) {
                textPreference = this.l;
            } else if ("airplane_mode".equals(next)) {
                textPreference = this.m;
            } else if ("gps".equals(next)) {
                textPreference = this.n;
            } else if ("synchronization".equals(next)) {
                textPreference = this.o;
            }
            X.a(textPreference, this.f6671b, next);
        }
        b();
    }

    public void a(int i2, int i3, Intent intent) {
    }

    public void a(Bundle bundle) {
        ((U) this.f6672c).addPreferencesFromResource(R.xml.pc_operations_edit);
        this.p = new a(this, (U) this.f6672c, (K) null);
        this.f6718d = (PreferenceScreen) ((U) this.f6672c).findPreference("screen");
        this.e = (TextPreference) ((U) this.f6672c).findPreference("memory_clean");
        this.e.setOnPreferenceClickListener(this.p);
        this.e.setKey("auto_clean_memory");
        this.f = (TextPreference) ((U) this.f6672c).findPreference(BaseMobileDataUtils.MOBILE_DATA);
        this.f.setOnPreferenceClickListener(this.p);
        this.f.setKey("internet");
        this.g = (TextPreference) ((U) this.f6672c).findPreference("wifi");
        this.g.setOnPreferenceClickListener(this.p);
        this.g.setKey("wifi");
        this.h = (TextPreference) ((U) this.f6672c).findPreference("mute");
        this.h.setOnPreferenceClickListener(this.p);
        this.h.setKey("mute");
        this.i = (TextPreference) ((U) this.f6672c).findPreference("vibration");
        this.i.setOnPreferenceClickListener(this.p);
        this.i.setKey("vibration");
        this.j = (TextPreference) ((U) this.f6672c).findPreference("bluetooth");
        this.j.setOnPreferenceClickListener(this.p);
        this.j.setKey("bluetooth");
        this.k = (TextPreference) ((U) this.f6672c).findPreference("auto_brightness");
        this.k.setOnPreferenceClickListener(this.p);
        this.k.setKey("auto_brightness");
        this.l = (TextPreference) ((U) this.f6672c).findPreference("brightness");
        this.l.setOnPreferenceClickListener(this.p);
        this.l.setKey("brightness");
        this.m = (TextPreference) ((U) this.f6672c).findPreference("aireplane_mode");
        this.m.setOnPreferenceClickListener(this.p);
        this.m.setKey("airplane_mode");
        this.n = (TextPreference) ((U) this.f6672c).findPreference("gps");
        this.n.setOnPreferenceClickListener(this.p);
        this.n.setKey("gps");
        this.o = (TextPreference) ((U) this.f6672c).findPreference("sync");
        this.o.setOnPreferenceClickListener(this.p);
        this.o.setKey("synchronization");
    }

    public void b(Bundle bundle) {
        c();
    }
}
