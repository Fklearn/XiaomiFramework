package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import b.b.c.f.a;
import b.b.c.j.e;
import b.b.c.j.y;
import com.miui.activityutil.h;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.m.C0371b;
import com.miui.gamebooster.m.C0379j;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.G;
import com.miui.gamebooster.m.Z;
import com.miui.gamebooster.m.ia;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import miui.app.AlertDialog;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;
import miuix.preference.s;

public class GameBoosterSettingFragment extends s {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f4889a = "com.miui.gamebooster.ui.GameBoosterSettingFragment";
    /* access modifiers changed from: private */
    public boolean A;
    /* access modifiers changed from: private */
    public boolean B;
    /* access modifiers changed from: private */
    public boolean C;
    /* access modifiers changed from: private */
    public boolean D;
    /* access modifiers changed from: private */
    public boolean E;
    /* access modifiers changed from: private */
    public boolean F;
    /* access modifiers changed from: private */
    public boolean G;
    /* access modifiers changed from: private */
    public boolean H;
    /* access modifiers changed from: private */
    public com.miui.gamebooster.c.a I;
    /* access modifiers changed from: private */
    public int J;
    /* access modifiers changed from: private */
    public IFeedbackControl K;
    private a L;
    private d M;
    private String N;
    /* access modifiers changed from: private */
    public Activity O;
    private ServiceConnection P = new C0426ga(this);
    a.C0027a Q = new C0428ha(this);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public IFeedbackControl f4890b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Handler f4891c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f4892d = 0;
    /* access modifiers changed from: private */
    public IGameBooster e;
    /* access modifiers changed from: private */
    public PreferenceCategory f;
    private PreferenceCategory g;
    private PreferenceCategory h;
    /* access modifiers changed from: private */
    public CheckBoxPreference i;
    /* access modifiers changed from: private */
    public CheckBoxPreference j;
    /* access modifiers changed from: private */
    public CheckBoxPreference k;
    /* access modifiers changed from: private */
    public CheckBoxPreference l;
    /* access modifiers changed from: private */
    public CheckBoxPreference m;
    /* access modifiers changed from: private */
    public CheckBoxPreference n;
    /* access modifiers changed from: private */
    public CheckBoxPreference o;
    /* access modifiers changed from: private */
    public CheckBoxPreference p;
    /* access modifiers changed from: private */
    public CheckBoxPreference q;
    private TextPreference r;
    /* access modifiers changed from: private */
    public TextPreference s;
    /* access modifiers changed from: private */
    public TextPreference t;
    private TextPreference u;
    /* access modifiers changed from: private */
    public TextPreference v;
    /* access modifiers changed from: private */
    public TextPreference w;
    /* access modifiers changed from: private */
    public DropDownPreference x;
    /* access modifiers changed from: private */
    public TextPreference y;
    /* access modifiers changed from: private */
    public boolean z;

    private static class a extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<GameBoosterSettingFragment> f4893a;

        public a(GameBoosterSettingFragment gameBoosterSettingFragment) {
            this.f4893a = new WeakReference<>(gameBoosterSettingFragment);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            GameBoosterSettingFragment gameBoosterSettingFragment = (GameBoosterSettingFragment) this.f4893a.get();
            if (Utils.a(gameBoosterSettingFragment.O) || isCancelled()) {
                return false;
            }
            boolean unused = gameBoosterSettingFragment.C = gameBoosterSettingFragment.I.k(true);
            com.miui.gamebooster.c.a unused2 = gameBoosterSettingFragment.I;
            boolean unused3 = gameBoosterSettingFragment.D = com.miui.gamebooster.c.a.w(true);
            com.miui.gamebooster.c.a unused4 = gameBoosterSettingFragment.I;
            boolean unused5 = gameBoosterSettingFragment.E = com.miui.gamebooster.c.a.a(true);
            com.miui.gamebooster.c.a unused6 = gameBoosterSettingFragment.I;
            boolean unused7 = gameBoosterSettingFragment.A = com.miui.gamebooster.c.a.o(true);
            com.miui.gamebooster.c.a unused8 = gameBoosterSettingFragment.I;
            boolean unused9 = gameBoosterSettingFragment.z = com.miui.gamebooster.c.a.d(true);
            com.miui.gamebooster.c.a unused10 = gameBoosterSettingFragment.I;
            boolean unused11 = gameBoosterSettingFragment.A = com.miui.gamebooster.c.a.o(true);
            com.miui.gamebooster.c.a unused12 = gameBoosterSettingFragment.I;
            boolean unused13 = gameBoosterSettingFragment.B = com.miui.gamebooster.c.a.l(true);
            com.miui.gamebooster.c.a unused14 = gameBoosterSettingFragment.I;
            boolean unused15 = gameBoosterSettingFragment.F = com.miui.gamebooster.c.a.e(true);
            if (gameBoosterSettingFragment.J == 1) {
                com.miui.gamebooster.c.a unused16 = gameBoosterSettingFragment.I;
                boolean unused17 = gameBoosterSettingFragment.G = com.miui.gamebooster.c.a.q(false);
            } else if (gameBoosterSettingFragment.J == 2) {
                try {
                    IFeedbackControl unused18 = gameBoosterSettingFragment.K = gameBoosterSettingFragment.c();
                    if (gameBoosterSettingFragment.K != null) {
                        boolean unused19 = gameBoosterSettingFragment.G = gameBoosterSettingFragment.K.p();
                    }
                } catch (RemoteException e) {
                    Log.i("LoadSettingPrefTask", e.toString());
                }
            }
            boolean unused20 = gameBoosterSettingFragment.H = Z.b(gameBoosterSettingFragment.O, (String) null);
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            GameBoosterSettingFragment gameBoosterSettingFragment = (GameBoosterSettingFragment) this.f4893a.get();
            if (!Utils.a(gameBoosterSettingFragment.O)) {
                gameBoosterSettingFragment.i.setChecked(gameBoosterSettingFragment.C);
                gameBoosterSettingFragment.j.setChecked(gameBoosterSettingFragment.E);
                gameBoosterSettingFragment.k.setChecked(gameBoosterSettingFragment.D);
                gameBoosterSettingFragment.m.setChecked(gameBoosterSettingFragment.z);
                gameBoosterSettingFragment.l.setChecked(gameBoosterSettingFragment.A);
                gameBoosterSettingFragment.n.setChecked(gameBoosterSettingFragment.B);
                gameBoosterSettingFragment.s.setEnabled(gameBoosterSettingFragment.A);
                gameBoosterSettingFragment.o.setChecked(gameBoosterSettingFragment.F);
                if (gameBoosterSettingFragment.J != 0) {
                    gameBoosterSettingFragment.p.setChecked(gameBoosterSettingFragment.G);
                }
                gameBoosterSettingFragment.q.setChecked(gameBoosterSettingFragment.H);
            }
        }
    }

    public static class b implements Preference.b {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<GameBoosterSettingFragment> f4894a;

        public b(GameBoosterSettingFragment gameBoosterSettingFragment) {
            this.f4894a = new WeakReference<>(gameBoosterSettingFragment);
        }

        public boolean onPreferenceChange(Preference preference, Object obj) {
            GameBoosterSettingFragment gameBoosterSettingFragment = (GameBoosterSettingFragment) this.f4894a.get();
            if (Utils.a(gameBoosterSettingFragment.O)) {
                return false;
            }
            boolean booleanValue = obj instanceof Boolean ? ((Boolean) obj).booleanValue() : false;
            if ("pref_shield_keyboard".equals(preference.getKey())) {
                com.miui.gamebooster.c.a unused = gameBoosterSettingFragment.I;
                com.miui.gamebooster.c.a.B(booleanValue);
                return true;
            } else if ("pref_net_booster".equals(preference.getKey())) {
                com.miui.gamebooster.c.a unused2 = gameBoosterSettingFragment.I;
                com.miui.gamebooster.c.a.T(booleanValue);
                gameBoosterSettingFragment.s.setEnabled(booleanValue);
                return true;
            } else if ("pref_call_handsfree".equals(preference.getKey())) {
                C0379j.a(booleanValue, gameBoosterSettingFragment.O);
                return true;
            } else if ("pref_game_shortcut".equals(preference.getKey())) {
                gameBoosterSettingFragment.a(booleanValue);
                IGameBooster iGameBooster = null;
                if (gameBoosterSettingFragment instanceof GameBoosterSettingFragment) {
                    iGameBooster = gameBoosterSettingFragment.d();
                }
                G.a(booleanValue, gameBoosterSettingFragment.O, iGameBooster);
                return true;
            } else if ("pref_slip".equals(preference.getKey())) {
                boolean unused3 = gameBoosterSettingFragment.D = booleanValue;
                com.miui.gamebooster.c.a unused4 = gameBoosterSettingFragment.I;
                com.miui.gamebooster.c.a.ca(booleanValue);
                return true;
            } else if ("pref_game_box".equals(preference.getKey())) {
                boolean unused5 = gameBoosterSettingFragment.E = booleanValue;
                G.a(booleanValue);
                return true;
            } else if ("pref_game_net_priority".equals(preference.getKey())) {
                com.miui.gamebooster.c.a unused6 = gameBoosterSettingFragment.I;
                com.miui.gamebooster.c.a.D(booleanValue);
                return true;
            } else if (!"pref_performance_booster".equals(preference.getKey())) {
                if ("pref_shortcut".equals(preference.getKey())) {
                    boolean unused7 = gameBoosterSettingFragment.H = booleanValue;
                    gameBoosterSettingFragment.q.setChecked(booleanValue);
                    G.a(booleanValue, gameBoosterSettingFragment.O);
                } else if ("pref_gamebooster_show_way_in_new".equals(preference.getKey())) {
                    if (obj instanceof String) {
                        com.miui.gamebooster.c.a unused8 = gameBoosterSettingFragment.I;
                        com.miui.gamebooster.c.a.c(TextUtils.equals((String) obj, gameBoosterSettingFragment.O.getString(R.string.gs_show_way_horizontal)) ^ true ? 1 : 0);
                    }
                    gameBoosterSettingFragment.x.b((String) obj);
                }
                return false;
            } else if (!booleanValue || gameBoosterSettingFragment.J != 1) {
                if (gameBoosterSettingFragment.J == 1) {
                    com.miui.gamebooster.c.a unused9 = gameBoosterSettingFragment.I;
                    com.miui.gamebooster.c.a.W(booleanValue);
                } else if (gameBoosterSettingFragment.J == 2) {
                    try {
                        IFeedbackControl unused10 = gameBoosterSettingFragment.K = gameBoosterSettingFragment.c();
                        if (gameBoosterSettingFragment.K != null) {
                            gameBoosterSettingFragment.K.b(booleanValue);
                        }
                    } catch (RemoteException e) {
                        Log.i(GameBoosterSettingFragment.f4889a, e.toString());
                    }
                }
                return true;
            } else {
                gameBoosterSettingFragment.b();
                return false;
            }
        }
    }

    public static class c implements Preference.c {

        /* renamed from: a  reason: collision with root package name */
        private final WeakReference<GameBoosterSettingFragment> f4895a;

        public c(GameBoosterSettingFragment gameBoosterSettingFragment) {
            this.f4895a = new WeakReference<>(gameBoosterSettingFragment);
        }

        public boolean onPreferenceClick(Preference preference) {
            Intent intent;
            String str;
            GameBoosterSettingFragment gameBoosterSettingFragment = (GameBoosterSettingFragment) this.f4895a.get();
            if (Utils.a(gameBoosterSettingFragment.O)) {
                return false;
            }
            if ("pref_setting_detail".equals(preference.getKey())) {
                intent = new Intent(gameBoosterSettingFragment.O, WhiteListActivity.class);
            } else {
                if ("pref_net_booster_wifi".equals(preference.getKey())) {
                    intent = new Intent(gameBoosterSettingFragment.O, WifiBoosterDetail.class);
                    str = "action_detail_wifibooster";
                } else if ("pref_value_performance_booster".equals(preference.getKey())) {
                    intent = new Intent(gameBoosterSettingFragment.O, CompetitionDetailActivity.class);
                } else if ("pref_advanced_setting".equals(preference.getKey())) {
                    intent = new Intent(gameBoosterSettingFragment.O, AdvanceSettingsActivity.class);
                } else if ("pref_function_shield".equals(preference.getKey())) {
                    intent = new Intent(gameBoosterSettingFragment.O, FunctionShieldSettingsActivity.class);
                } else {
                    if ("pref_gamebooster_show_way".equals(preference.getKey())) {
                        gameBoosterSettingFragment.i();
                    } else if ("pref_function_gwsd".equals(preference.getKey())) {
                        intent = new Intent(gameBoosterSettingFragment.O, WifiBoosterDetail.class);
                        str = "action_detail_gwsd";
                    }
                    return false;
                }
                intent.setAction(str);
            }
            gameBoosterSettingFragment.startActivity(intent);
            return false;
        }
    }

    private static class d extends AsyncTask<Void, Void, Boolean> {

        /* renamed from: a  reason: collision with root package name */
        private boolean f4896a = false;

        /* renamed from: b  reason: collision with root package name */
        private boolean f4897b = false;

        /* renamed from: c  reason: collision with root package name */
        private boolean f4898c = false;

        /* renamed from: d  reason: collision with root package name */
        private int f4899d = 0;
        private int e = 0;
        private final WeakReference<GameBoosterSettingFragment> f;

        public d(GameBoosterSettingFragment gameBoosterSettingFragment) {
            this.f = new WeakReference<>(gameBoosterSettingFragment);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Boolean doInBackground(Void... voidArr) {
            GameBoosterSettingFragment gameBoosterSettingFragment = (GameBoosterSettingFragment) this.f.get();
            if (Utils.a(gameBoosterSettingFragment.O) || isCancelled()) {
                return false;
            }
            com.miui.gamebooster.c.a unused = gameBoosterSettingFragment.I;
            this.f4896a = com.miui.gamebooster.c.a.p(false);
            com.miui.gamebooster.c.a unused2 = gameBoosterSettingFragment.I;
            this.f4897b = com.miui.gamebooster.c.a.e(true);
            com.miui.gamebooster.c.a unused3 = gameBoosterSettingFragment.I;
            this.f4899d = com.miui.gamebooster.c.a.a(0);
            com.miui.gamebooster.c.a unused4 = gameBoosterSettingFragment.I;
            if (com.miui.gamebooster.c.a.g(false) && !C0388t.a(gameBoosterSettingFragment.O)) {
                this.f4899d--;
            }
            com.miui.gamebooster.c.a unused5 = gameBoosterSettingFragment.I;
            this.e = com.miui.gamebooster.c.a.b();
            if (C0388t.w()) {
                com.miui.gamebooster.c.a unused6 = gameBoosterSettingFragment.I;
                this.f4898c = com.miui.gamebooster.c.a.j(false);
            }
            return true;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Boolean bool) {
            String str;
            TextPreference textPreference;
            String str2;
            TextPreference textPreference2;
            int i;
            TextPreference textPreference3;
            super.onPostExecute(bool);
            GameBoosterSettingFragment gameBoosterSettingFragment = (GameBoosterSettingFragment) this.f.get();
            if (!Utils.a(gameBoosterSettingFragment.O)) {
                if (this.f4896a) {
                    textPreference = gameBoosterSettingFragment.s;
                    str = gameBoosterSettingFragment.getResources().getString(R.string.start);
                } else {
                    textPreference = gameBoosterSettingFragment.s;
                    str = gameBoosterSettingFragment.getResources().getString(R.string.function_close);
                }
                textPreference.a(str);
                if (this.f4897b) {
                    textPreference2 = gameBoosterSettingFragment.t;
                    str2 = gameBoosterSettingFragment.getResources().getString(R.string.start);
                } else {
                    textPreference2 = gameBoosterSettingFragment.t;
                    str2 = gameBoosterSettingFragment.getResources().getString(R.string.function_close);
                }
                textPreference2.a(str2);
                TextPreference g = gameBoosterSettingFragment.v;
                Resources resources = gameBoosterSettingFragment.getResources();
                int i2 = this.f4899d;
                g.a(resources.getQuantityString(R.plurals.function_shield_num, i2, new Object[]{Integer.valueOf(i2)}));
                if (gameBoosterSettingFragment.w != null) {
                    int i3 = this.e;
                    if (i3 == 0) {
                        textPreference3 = gameBoosterSettingFragment.w;
                        i = R.string.gs_show_way_horizontal;
                    } else if (i3 == 1) {
                        textPreference3 = gameBoosterSettingFragment.w;
                        i = R.string.gs_show_way_vertical;
                    }
                    textPreference3.a(gameBoosterSettingFragment.getString(i));
                }
                if (!C0388t.w()) {
                    return;
                }
                if (this.f4898c) {
                    gameBoosterSettingFragment.y.a((int) R.string.start);
                } else {
                    gameBoosterSettingFragment.y.a((int) R.string.function_close);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z2) {
        CheckBoxPreference checkBoxPreference;
        boolean z3;
        PreferenceCategory preferenceCategory;
        Preference preference;
        PreferenceScreen preferenceScreen;
        Preference preference2;
        if (z2) {
            getPreferenceScreen().b((Preference) this.i);
            if (C0388t.o()) {
                getPreferenceScreen().b((Preference) this.k);
                checkBoxPreference = this.j;
                z3 = this.D;
            } else {
                getPreferenceScreen().b((Preference) this.j);
                checkBoxPreference = this.j;
                z3 = this.E;
            }
            checkBoxPreference.setChecked(z3);
            if (!C0388t.s()) {
                getPreferenceScreen().d(this.w);
                getPreferenceScreen().d(this.x);
            } else {
                if (e.b() < 10) {
                    preferenceScreen = getPreferenceScreen();
                    preference2 = this.w;
                } else {
                    preferenceScreen = getPreferenceScreen();
                    preference2 = this.x;
                }
                preferenceScreen.b(preference2);
            }
            getPreferenceScreen().b((Preference) this.q);
            getPreferenceScreen().b((Preference) this.f);
            getPreferenceScreen().b((Preference) this.g);
            getPreferenceScreen().b((Preference) this.h);
            com.miui.gamebooster.c.a aVar = this.I;
            if (!com.miui.gamebooster.c.a.y(false)) {
                this.f.d(this.l);
                this.f.d(this.s);
            }
            if (!C0388t.d()) {
                getPreferenceScreen().d(this.h);
            }
            if (C0388t.n()) {
                preferenceCategory = this.f;
                preference = this.o;
            } else {
                preferenceCategory = this.f;
                preference = this.t;
            }
            preferenceCategory.d(preference);
            if (!C0388t.w()) {
                this.g.d(this.y);
                return;
            }
            return;
        }
        C0371b.a(this.O);
        getPreferenceScreen().e();
        getPreferenceScreen().b((Preference) this.i);
        getPreferenceScreen().b((Preference) this.q);
        com.miui.common.persistence.b.b("game_IsAntiMsg", false);
    }

    private void f() {
        this.L = new a(this);
        this.L.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void g() {
        this.M = new d(this);
        this.M.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private void h() {
        int i2;
        DropDownPreference dropDownPreference;
        if (this.x != null) {
            int b2 = com.miui.gamebooster.c.a.b();
            if (b2 == 0) {
                dropDownPreference = this.x;
                i2 = R.string.gs_show_way_horizontal;
            } else if (b2 == 1) {
                dropDownPreference = this.x;
                i2 = R.string.gs_show_way_vertical;
            } else {
                return;
            }
            dropDownPreference.b(getString(i2));
        }
    }

    /* access modifiers changed from: private */
    public void i() {
        String string = getString(R.string.gs_show_way_horizontal);
        String string2 = getString(R.string.gs_show_way_vertical);
        int i2 = 0;
        String[] strArr = {string, string2};
        com.miui.gamebooster.c.a aVar = this.I;
        int b2 = com.miui.gamebooster.c.a.b();
        if (b2 != 0 && b2 == 1) {
            i2 = 1;
        }
        new AlertDialog.Builder(this.O).setTitle(R.string.choose_gs_show_way).setSingleChoiceItems(strArr, i2, new C0434ka(this, string, string2)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }

    public void b() {
        new AlertDialog.Builder(this.O).setTitle(R.string.thermal_tips_title).setMessage(R.string.thermal_tips_message).setPositiveButton(17039370, new C0432ja(this)).setNegativeButton(17039360, new C0430ia(this)).create().show();
    }

    public IFeedbackControl c() {
        return this.f4890b;
    }

    public IGameBooster d() {
        return this.e;
    }

    public int e() {
        return this.f4892d;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        CheckBoxPreference checkBoxPreference;
        PreferenceScreen preferenceScreen;
        Preference preference;
        PreferenceCategory preferenceCategory;
        Preference preference2;
        PreferenceScreen preferenceScreen2;
        this.O = getActivity();
        if (!Utils.a(this.O)) {
            this.N = y.a("ro.product.locale", h.f2289a);
            this.f4891c = new Handler(Looper.getMainLooper());
            C0390v.a((Context) this.O).a(this.Q);
            Intent intent = new Intent();
            intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.feedbackcontrol.FeedbackControlService");
            this.O.bindService(intent, this.P, 1);
            addPreferencesFromResource(R.xml.gs_setting);
            this.I = com.miui.gamebooster.c.a.a((Context) this.O);
            this.i = (CheckBoxPreference) findPreference("pref_game_shortcut");
            this.j = (CheckBoxPreference) findPreference("pref_game_box");
            this.k = (CheckBoxPreference) findPreference("pref_slip");
            this.q = (CheckBoxPreference) findPreference("pref_shortcut");
            this.w = (TextPreference) findPreference("pref_gamebooster_show_way");
            this.x = (DropDownPreference) findPreference("pref_gamebooster_show_way_in_new");
            this.f = (PreferenceCategory) findPreference("preference_category_key_performance_booster");
            this.g = (PreferenceCategory) findPreference("preference_category_key_anti_disturb_msg");
            this.h = (PreferenceCategory) findPreference("preference_category_key_else_function");
            this.t = (TextPreference) findPreference("pref_value_performance_booster");
            this.l = (CheckBoxPreference) findPreference("pref_net_booster");
            this.m = (CheckBoxPreference) findPreference("pref_shield_keyboard");
            this.r = (TextPreference) findPreference("pref_setting_detail");
            this.s = (TextPreference) findPreference("pref_net_booster_wifi");
            this.n = (CheckBoxPreference) findPreference("pref_call_handsfree");
            this.v = (TextPreference) findPreference("pref_function_shield");
            this.u = (TextPreference) findPreference("pref_advanced_setting");
            this.o = (CheckBoxPreference) findPreference("pref_game_net_priority");
            this.p = (CheckBoxPreference) findPreference("pref_performance_booster");
            this.y = (TextPreference) findPreference("pref_function_gwsd");
            b bVar = new b(this);
            this.p.setOnPreferenceChangeListener(bVar);
            this.l.setOnPreferenceChangeListener(bVar);
            this.m.setOnPreferenceChangeListener(bVar);
            this.n.setOnPreferenceChangeListener(bVar);
            this.i.setOnPreferenceChangeListener(bVar);
            this.j.setOnPreferenceChangeListener(bVar);
            this.k.setOnPreferenceChangeListener(bVar);
            this.v.setOnPreferenceChangeListener(bVar);
            this.o.setOnPreferenceChangeListener(bVar);
            this.q.setOnPreferenceChangeListener(bVar);
            this.x.setOnPreferenceChangeListener(bVar);
            c cVar = new c(this);
            this.r.setOnPreferenceClickListener(cVar);
            this.s.setOnPreferenceClickListener(cVar);
            this.t.setOnPreferenceClickListener(cVar);
            this.u.setOnPreferenceClickListener(cVar);
            this.v.setOnPreferenceClickListener(cVar);
            this.w.setOnPreferenceClickListener(cVar);
            this.y.setOnPreferenceClickListener(cVar);
            this.C = this.I.k(true);
            if (!this.C) {
                a(false);
            }
            f();
            this.J = e();
            if (this.J == 0) {
                this.f.d(this.p);
            }
            com.miui.gamebooster.c.a aVar = this.I;
            if (!com.miui.gamebooster.c.a.y(false) || !com.miui.gamebooster.d.a.a()) {
                this.f.d(this.l);
                this.f.d(this.s);
            }
            if (C0388t.o()) {
                preferenceScreen = getPreferenceScreen();
                checkBoxPreference = this.j;
            } else {
                preferenceScreen = getPreferenceScreen();
                checkBoxPreference = this.k;
            }
            preferenceScreen.d(checkBoxPreference);
            if (!C0388t.d()) {
                getPreferenceScreen().d(this.h);
            }
            if (C0388t.n()) {
                preferenceCategory = this.f;
                preference = this.o;
            } else {
                preferenceCategory = this.f;
                preference = this.t;
            }
            preferenceCategory.d(preference);
            if (!C0388t.s()) {
                getPreferenceScreen().d(this.w);
                getPreferenceScreen().d(this.x);
            }
            if (e.b() < 10) {
                preferenceScreen2 = getPreferenceScreen();
                preference2 = this.x;
            } else {
                preferenceScreen2 = getPreferenceScreen();
                preference2 = this.w;
            }
            preferenceScreen2.d(preference2);
            if (!C0388t.w()) {
                this.g.d(this.y);
            }
            this.v.setSummary((int) R.string.function_shield_summary);
        }
    }

    public void onDestroy() {
        C0390v.a((Context) this.O).a();
        ia.a().b();
        if (this.f4890b != null) {
            this.O.unbindService(this.P);
        }
        super.onDestroy();
        this.L.cancel(true);
        this.M.cancel(true);
    }

    public void onResume() {
        super.onResume();
        g();
        h();
    }
}
