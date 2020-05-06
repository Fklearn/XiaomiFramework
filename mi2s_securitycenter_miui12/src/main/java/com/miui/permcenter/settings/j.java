package com.miui.permcenter.settings;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import b.b.b.d.k;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.permcenter.settings.model.LayoutButtonPreference;
import com.miui.permcenter.settings.model.PermissionUseTotalPreference;
import com.miui.permcenter.settings.model.SloganPreference;
import com.miui.permcenter.settings.model.TitleOnlyPreference;
import com.miui.permcenter.settings.model.TitleValuePreference;
import com.miui.securitycenter.R;
import java.lang.ref.WeakReference;
import java.util.Locale;
import miui.app.AlertDialog;
import miui.os.Build;
import miuix.preference.s;

public class j extends s {

    /* renamed from: a  reason: collision with root package name */
    public static final String f6519a = "j";
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public SloganPreference f6520b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public CheckBoxPreference f6521c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public TitleValuePreference f6522d;
    /* access modifiers changed from: private */
    public TitleOnlyPreference e;
    private PermissionUseTotalPreference f;
    /* access modifiers changed from: private */
    public LayoutButtonPreference g;
    /* access modifiers changed from: private */
    public a h;
    private Preference.c i = new b(this);
    private Preference.b j = new c(this);
    int k = 0;
    /* access modifiers changed from: private */
    public boolean l;
    /* access modifiers changed from: private */
    public int[] m = {R.drawable.pm_setting_icon_dialog_camera, R.drawable.pm_setting_icon_dialog_record, R.drawable.pm_setting_icon_dialog_location};
    /* access modifiers changed from: private */
    public AnimatorSet n;
    /* access modifiers changed from: private */
    public ImageView o;

    private static class a extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<j> f6523a;

        private a(j jVar) {
            this.f6523a = new WeakReference<>(jVar);
        }

        /* synthetic */ a(j jVar, b bVar) {
            this(jVar);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            j jVar = (j) this.f6523a.get();
            if (jVar != null && message.what == 1) {
                boolean unused = jVar.l = false;
                jVar.d();
            }
        }
    }

    /* access modifiers changed from: private */
    public void a() {
        getActivity().sendBroadcast(new Intent("com.miui.action.sync_status_bar"), "miui.permission.READ_AND_WIRTE_PERMISSION_MANAGER");
    }

    /* access modifiers changed from: private */
    public void b() {
        View inflate = View.inflate(getActivity(), R.layout.pm_setting_dialog_danger_permission, (ViewGroup) null);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.image);
        ((TextView) inflate.findViewById(R.id.message)).setText(R.string.privacy_dialog_danger_mission_message);
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.privacy_dialog_danger_mission_title)).setView(inflate).setPositiveButton(getResources().getString(R.string.button_text_known), new i(this)).create().show();
    }

    /* access modifiers changed from: private */
    public void c() {
        View inflate = View.inflate(getActivity(), R.layout.pm_setting_dialog_permission_use, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.message);
        this.o = (ImageView) inflate.findViewById(R.id.image);
        CheckBox checkBox = (CheckBox) inflate.findViewById(R.id.checkbox);
        checkBox.setVisibility(!x.c() ? 0 : 8);
        textView.setText(R.string.privacy_guide_permission_guide_text);
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.privacy_permission_use_warning)).setView(inflate).setPositiveButton(getResources().getString(R.string.privacy_dialog_open), new h(this, checkBox)).setNegativeButton(getResources().getString(R.string.cancel), new g(this, checkBox)).setOnDismissListener(new f(this)).create().show();
        this.h.sendEmptyMessageDelayed(1, 1000);
    }

    /* access modifiers changed from: private */
    public void d() {
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.o, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.0f, 1.0f});
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.o, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.0f, 1.0f});
        ofFloat.setDuration(500);
        ofFloat2.setDuration(500);
        this.n = new AnimatorSet();
        this.n.play(ofFloat).with(ofFloat2);
        ofFloat.addListener(new d(this));
        ofFloat.addUpdateListener(new e(this));
        this.n.setInterpolator(new AccelerateDecelerateInterpolator());
        this.n.start();
    }

    /* access modifiers changed from: private */
    public void e() {
        String locale = Locale.getDefault().toString();
        String region = Build.getRegion();
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.miui.com/res/doc/privacy.html?region=" + region + "&lang=" + locale)));
    }

    /* access modifiers changed from: private */
    public void f() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://privacy.miui.com")));
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        String str2;
        setPreferencesFromResource(R.xml.pm_settings_informed, str);
        this.h = new a(this, (b) null);
        this.f6520b = (SloganPreference) findPreference("key_pm_setting_slogan");
        this.f6521c = (CheckBoxPreference) findPreference("key_pm_setting_permission_use_warning");
        this.f6522d = (TitleValuePreference) findPreference("key_pm_setting_danger_permission_warning");
        this.e = (TitleOnlyPreference) findPreference("key_pm_setting_more_info_title");
        this.g = (LayoutButtonPreference) findPreference("key_pm_setting_privacy");
        this.f = (PermissionUseTotalPreference) findPreference("key_pm_setting_use_total");
        this.f.a(getActivity());
        String locale = Locale.getDefault().toString();
        boolean z = true;
        if (!k.a(getActivity()) || !"zh_CN".equals(locale) || Build.IS_INTERNATIONAL_BUILD) {
            this.f6520b.setVisible(false);
            this.e.setVisible(false);
        } else {
            if (x.b()) {
                this.f6520b.setVisible(true);
                this.e.setVisible(false);
                str2 = "privacy_item_slogan_show";
            } else {
                this.f6520b.setVisible(false);
                this.e.setVisible(true);
                str2 = "privacy_item_station_show";
            }
            com.miui.permcenter.a.a.d(str2);
        }
        this.f6520b.setOnPreferenceClickListener(this.i);
        this.f6522d.setOnPreferenceClickListener(this.i);
        this.e.setOnPreferenceClickListener(this.i);
        this.g.setOnPreferenceClickListener(this.i);
        this.f6521c.setOnPreferenceChangeListener(this.j);
        int i2 = Settings.Secure.getInt(getActivity().getContentResolver(), "PERMISSION_USE_WARNING", 0);
        if (i2 != 0 || !a.a(getActivity())) {
            CheckBoxPreference checkBoxPreference = this.f6521c;
            if (i2 != 1) {
                z = false;
            }
            checkBoxPreference.setChecked(z);
            return;
        }
        Settings.Secure.putInt(getActivity().getContentResolver(), "PERMISSION_USE_WARNING", 1);
        a();
        this.f6521c.setChecked(true);
    }

    public void onDestroyView() {
        super.onDestroyView();
        AnimatorSet animatorSet = this.n;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    public void onResume() {
        super.onResume();
        if (x.b()) {
            x.b(false);
        }
        PermissionUseTotalPreference permissionUseTotalPreference = this.f;
        if (permissionUseTotalPreference != null) {
            permissionUseTotalPreference.a(false);
        }
    }
}
