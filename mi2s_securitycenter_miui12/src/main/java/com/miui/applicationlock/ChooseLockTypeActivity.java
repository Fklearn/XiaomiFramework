package com.miui.applicationlock;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import b.b.c.c.c;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.C0259c;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.securitycenter.R;

public class ChooseLockTypeActivity extends c implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    /* renamed from: a  reason: collision with root package name */
    private C0259c f3131a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f3132b = true;

    /* renamed from: c  reason: collision with root package name */
    private boolean f3133c;

    /* renamed from: d  reason: collision with root package name */
    private Intent f3134d;
    private String e = "pattern";

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.applicationlock.ChooseLockTypeActivity, miui.preference.PreferenceActivity] */
    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        ChooseLockTypeActivity.super.onActivityResult(i, i2, intent);
        switch (i) {
            case 1022110:
                if (i2 == -1) {
                    h.f(this.e);
                    if (intent == null) {
                        setResult(-1);
                        break;
                    } else {
                        setResult(-1, intent);
                        break;
                    }
                } else {
                    this.f3132b = true;
                    if (intent != null) {
                        if (intent.getBooleanExtra("cancel_setting_password", false)) {
                            setResult(0, intent);
                            break;
                        } else if (intent.getBooleanExtra("home_cancel_current_pwd_page", false)) {
                            this.f3132b = false;
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            case 1022111:
                if (i2 != -1) {
                    this.f3132b = false;
                    Intent intent2 = new Intent(this, SettingLockActivity.class);
                    intent2.putExtra("cancel_back_to_home", true);
                    setResult(0, intent2);
                    break;
                } else {
                    this.f3132b = true;
                    return;
                }
            default:
                return;
        }
        finish();
    }

    /* JADX WARNING: type inference failed for: r9v0, types: [android.content.Context, com.miui.applicationlock.ChooseLockTypeActivity, miui.preference.PreferenceActivity, android.preference.Preference$OnPreferenceClickListener] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        ChooseLockTypeActivity.super.onCreate(bundle);
        addPreferencesFromResource(R.xml.password_type_picker);
        if (bundle == null || !bundle.containsKey(AdvancedSlider.STATE)) {
            this.f3132b = true;
        } else {
            this.f3132b = false;
        }
        this.f3131a = C0259c.b(getApplicationContext());
        String stringExtra = getIntent().getStringExtra("extra_data");
        String stringExtra2 = getIntent().getStringExtra("external_app_name");
        Preference findPreference = findPreference("unlock_set_pattern");
        Preference findPreference2 = findPreference("unlock_set_pin");
        Preference findPreference3 = findPreference("unlock_set_password");
        findPreference.setOnPreferenceClickListener(this);
        findPreference2.setOnPreferenceClickListener(this);
        findPreference3.setOnPreferenceClickListener(this);
        boolean booleanExtra = getIntent().getBooleanExtra("forgot_password_reset", false);
        boolean booleanExtra2 = getIntent().getBooleanExtra("setting_password_reset", false);
        if (!this.f3131a.d() || booleanExtra) {
            getPreferenceScreen().removePreference(findPreference);
            this.f3132b = true;
        }
        if ("ModifyPassword".equals(stringExtra)) {
            this.f3134d = new Intent(this, ResetChooseAccessControl.class);
            this.f3134d.putExtra("setting_password_reset", booleanExtra2);
            this.f3133c = true;
        } else {
            this.f3134d = new Intent(this, LockChooseAccessControl.class);
        }
        this.f3134d.putExtra("extra_data", stringExtra);
        this.f3134d.putExtra("external_app_name", stringExtra2);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        String str;
        String key = preference.getKey();
        if ("unlock_set_pattern".equals(key)) {
            str = "pattern";
        } else if ("unlock_set_pin".equals(key)) {
            str = "numeric";
        } else {
            if ("unlock_set_password".equals(key)) {
                str = "mixed";
            }
            this.f3134d.putExtra("passwordType", this.e);
            startActivityForResult(this.f3134d, 1022110);
            return true;
        }
        this.e = str;
        this.f3134d.putExtra("passwordType", this.e);
        startActivityForResult(this.f3134d, 1022110);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        ChooseLockTypeActivity.super.onSaveInstanceState(bundle);
        bundle.putBoolean(AdvancedSlider.STATE, this.f3132b);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.applicationlock.ChooseLockTypeActivity, miui.preference.PreferenceActivity] */
    /* access modifiers changed from: protected */
    public void onStart() {
        ChooseLockTypeActivity.super.onStart();
        if (!this.f3131a.d() || this.f3132b || !this.f3133c) {
            this.f3132b = true;
            return;
        }
        Intent intent = new Intent(this, ConfirmAccessControl.class);
        intent.putExtra("extra_data", "HappyCodingMain");
        startActivityForResult(intent, 1022111);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        if (this.f3132b) {
            this.f3132b = false;
        }
        ChooseLockTypeActivity.super.onStop();
    }
}
