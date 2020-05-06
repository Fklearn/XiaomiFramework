package com.miui.luckymoney.ui.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.CompoundButton;
import b.b.c.j.x;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.ui.customview.IconCheckBoxPreference;
import com.miui.securitycenter.R;

public class LuckyAlarmSettingActivity extends BasePreferenceActivity implements Preference.OnPreferenceChangeListener {
    private static final String CATEGORY_LUCKY_ACTIVITY = "category_lucky_activity";
    private static final String PREF_KEY_ALARM_SOUND_WARNING = "pref_key_alarm_sound_warning";
    private static final String PREF_KEY_OPEN_LUCKY_ALARM = "pref_key_open_lucky_alarm";
    private CommonConfig commonConfig;
    private CheckBoxPreference mAlarmSoundWarning;
    private Context mContext;
    private PreferenceCategory mLuckyActivityCategory;
    private CheckBoxPreference mOpenLuckyAlarm;
    private PreferenceScreen mPreferenceScreen;
    private String[] packages;
    private PackageManager pm;

    private static class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private CommonConfig mCommonConfig;
        private String mPackageName;

        public CheckedChangeListener(Context context, String str) {
            this.mCommonConfig = CommonConfig.getInstance(context.getApplicationContext());
            this.mPackageName = str;
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            this.mCommonConfig.setLuckyAlarmPackageOpen(this.mPackageName, z);
        }
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, miui.preference.PreferenceActivity, com.miui.luckymoney.ui.activity.LuckyAlarmSettingActivity] */
    private Preference createItemViewByPackageName(String str) {
        try {
            Drawable applicationIcon = this.pm.getApplicationIcon(str);
            CharSequence j = x.j(this.mContext, str);
            if (applicationIcon == null || j == null) {
                return null;
            }
            IconCheckBoxPreference iconCheckBoxPreference = new IconCheckBoxPreference(this);
            iconCheckBoxPreference.setIcon(applicationIcon);
            iconCheckBoxPreference.setTextView(j + "红包活动");
            iconCheckBoxPreference.setChecked(this.commonConfig.getLuckyAlarmPackageOpen(str));
            iconCheckBoxPreference.setSlidingButtonListener(new CheckedChangeListener(getApplicationContext(), str));
            return iconCheckBoxPreference;
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        LuckyAlarmSettingActivity.super.onCreate(bundle);
        addPreferencesFromResource(R.xml.activity_lucky_alarm_setting);
        this.mPreferenceScreen = getPreferenceScreen();
        this.mContext = getApplicationContext();
        this.mOpenLuckyAlarm = (CheckBoxPreference) findPreference(PREF_KEY_OPEN_LUCKY_ALARM);
        this.mAlarmSoundWarning = (CheckBoxPreference) findPreference(PREF_KEY_ALARM_SOUND_WARNING);
        this.mLuckyActivityCategory = (PreferenceCategory) findPreference(CATEGORY_LUCKY_ACTIVITY);
        this.mOpenLuckyAlarm.setOnPreferenceChangeListener(this);
        this.mAlarmSoundWarning.setOnPreferenceChangeListener(this);
        this.commonConfig = CommonConfig.getInstance(this.mContext);
        this.mOpenLuckyAlarm.setChecked(this.commonConfig.getLuckyAlarmEnable());
        this.mAlarmSoundWarning.setChecked(this.commonConfig.getLuckyAlarmSoundEnable());
        this.packages = getIntent().getStringArrayExtra(LuckyAlarmActivity.PACKAGENAME);
        this.pm = getPackageManager();
        String[] strArr = this.packages;
        if (strArr == null || strArr.length <= 0) {
            this.mPreferenceScreen.removePreference(this.mLuckyActivityCategory);
            return;
        }
        this.mPreferenceScreen.addPreference(this.mLuckyActivityCategory);
        for (String createItemViewByPackageName : this.packages) {
            Preference createItemViewByPackageName2 = createItemViewByPackageName(createItemViewByPackageName);
            if (createItemViewByPackageName2 != null) {
                this.mLuckyActivityCategory.addPreference(createItemViewByPackageName2);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mOpenLuckyAlarm) {
            this.commonConfig.setLuckyAlarmEnable(booleanValue);
            return true;
        } else if (preference != this.mAlarmSoundWarning) {
            return true;
        } else {
            this.commonConfig.setLuckyAlarmSoundEnable(booleanValue);
            return true;
        }
    }
}
