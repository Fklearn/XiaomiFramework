package com.miui.earthquakewarning.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import b.b.c.j.i;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.service.EarthquakeWarningService;
import com.miui.earthquakewarning.utils.FileUtils;
import com.miui.earthquakewarning.utils.UserNoticeUtil;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.l;
import java.util.List;
import miui.app.AlertDialog;
import miuix.preference.s;

public class EarthquakeWarningMainFragment extends s {
    private static final int FAST_CLICK_DELAY_TIME = 500;
    private static final String TAG = "EarthquakeWarningMain";
    protected boolean isPreloadGoogleCsp = i.f();
    /* access modifiers changed from: private */
    public Preference llContact;
    /* access modifiers changed from: private */
    public Preference llEmergencyCard;
    /* access modifiers changed from: private */
    public Preference llSafePlace;
    /* access modifiers changed from: private */
    public Preference llSetting;
    /* access modifiers changed from: private */
    public Preference llWarnintList;
    private Activity mActivity;
    private Preference.b mChangeListener = new Preference.b() {
        public boolean onPreferenceChange(Preference preference, Object obj) {
            if (preference != EarthquakeWarningMainFragment.this.mWarningToggle) {
                return true;
            }
            if (System.currentTimeMillis() - EarthquakeWarningMainFragment.this.mLastClickTime < 500) {
                return false;
            }
            long unused = EarthquakeWarningMainFragment.this.mLastClickTime = System.currentTimeMillis();
            if (((Boolean) obj).booleanValue()) {
                EarthquakeWarningMainFragment.this.showUserNoticeDialog();
                return true;
            }
            EarthquakeWarningMainFragment.this.showCloseWarningDialog();
            return true;
        }
    };
    private Preference.c mClickListener = new Preference.c() {
        public boolean onPreferenceClick(Preference preference) {
            if (preference == EarthquakeWarningMainFragment.this.llSetting) {
                EarthquakeWarningMainFragment.this.settingActivity();
                return true;
            } else if (preference == EarthquakeWarningMainFragment.this.llContact) {
                EarthquakeWarningMainFragment.this.pickContactsBefore();
                return true;
            } else if (preference == EarthquakeWarningMainFragment.this.llEmergencyCard) {
                EarthquakeWarningMainFragment.this.editEmergencyCard();
                return true;
            } else if (preference == EarthquakeWarningMainFragment.this.llSafePlace) {
                AnalyticHelper.trackMainResultActionModuleClick(AnalyticHelper.MAIN_SAFE_PLACE);
                EarthquakeWarningManager.getInstance().searchSafePlace(EarthquakeWarningMainFragment.this.mContext);
                return true;
            } else if (preference != EarthquakeWarningMainFragment.this.llWarnintList) {
                return true;
            } else {
                EarthquakeWarningMainFragment.this.warningListActivity();
                return true;
            }
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public long mLastClickTime = 0;
    /* access modifiers changed from: private */
    public CheckBoxPreference mWarningToggle;

    /* access modifiers changed from: private */
    public void editEmergencyCard() {
        Class cls;
        Context context;
        Intent intent = new Intent();
        if (Utils.isEmergencyInfoEmpty()) {
            context = this.mContext;
            cls = EarthquakeWarningEmergencyEditActivity.class;
        } else {
            context = this.mContext;
            cls = EarthquakeWarningEmergencyActivity.class;
        }
        intent.setClass(context, cls);
        this.mContext.startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void guideActivityWithResult() {
        this.mActivity.startActivityForResult(new Intent(this.mContext, EarthquakeWarningGuideActivity.class), 1002);
    }

    /* access modifiers changed from: private */
    public void pickContactsBefore() {
        if (TextUtils.isEmpty(Utils.getContact())) {
            pickFromContacts();
        } else {
            showPickContactsOperateDialog();
        }
    }

    private void pickFromAndroidContacts() {
        try {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setData(ContactsContract.Contacts.CONTENT_URI);
            intent.setPackage("com.google.android.contacts");
            intent.addCategory(Constants.System.CATEGORY_DEFALUT);
            this.mActivity.startActivityForResult(intent, 1001);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setContactText(String str, String str2) {
        Preference preference = this.llContact;
        Context context = this.mContext;
        Object[] objArr = new Object[1];
        if (TextUtils.isEmpty(str)) {
            str = str2;
        }
        objArr[0] = str;
        preference.setSummary((CharSequence) context.getString(R.string.ew_main_contact_summary, objArr));
    }

    /* access modifiers changed from: private */
    public void settingActivity() {
        this.mContext.startActivity(new Intent(this.mContext, EarthquakeWarningSettingActivity.class));
    }

    /* access modifiers changed from: private */
    public void showCloseWarningDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.ew_main_close_title).setMessage(R.string.ew_main_close_text).setCancelable(false).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                EarthquakeWarningMainFragment.this.mWarningToggle.setChecked(false);
                EarthquakeWarningManager.getInstance().closeEarthquakeWarning();
                FileUtils.deleteSignature(EarthquakeWarningMainFragment.this.mContext);
                EarthquakeWarningMainFragment.this.stopEwService();
                EarthquakeWarningMainFragment.this.llSetting.setEnabled(false);
                AnalyticHelper.trackMainResultActionModuleClick(AnalyticHelper.MAIN_SWITCH_OFF);
                dialogInterface.dismiss();
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                EarthquakeWarningMainFragment.this.mWarningToggle.setChecked(true);
                dialogInterface.dismiss();
            }
        }).show();
    }

    private void showPickContactsOperateDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.ew_main_contact_change_dialog_title).setMessage(R.string.ew_main_contact_change_dialog_content).setPositiveButton(R.string.ew_main_contact_change, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                EarthquakeWarningMainFragment.this.pickFromContacts();
                dialogInterface.dismiss();
            }
        }).setNegativeButton(R.string.ew_main_contact_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Utils.setContactName((String) null);
                Utils.setContact((String) null);
                EarthquakeWarningMainFragment.this.llContact.setSummary((int) R.string.ew_main_contact_tips);
                dialogInterface.dismiss();
            }
        }).show();
    }

    /* access modifiers changed from: private */
    public void showUserNoticeDialog() {
        UserNoticeUtil.showUserNoticeDialog(this.mContext, this.mActivity.getFragmentManager(), new UserNoticeUtil.ClickButtonListener() {
            public void onAccept() {
                if (!h.i()) {
                    l.a(EarthquakeWarningMainFragment.this.mContext.getApplicationContext(), true);
                }
                EarthquakeWarningMainFragment.this.guideActivityWithResult();
            }

            public void onNotAccept() {
                EarthquakeWarningMainFragment.this.stopEwService();
                EarthquakeWarningMainFragment.this.mWarningToggle.setChecked(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public void stopEwService() {
        this.mContext.stopService(new Intent(this.mContext, EarthquakeWarningService.class));
    }

    /* access modifiers changed from: private */
    public void warningListActivity() {
        this.mContext.startActivity(new Intent(this.mContext, EarthquakeWarningListActivity.class));
    }

    public void agreeResult() {
        this.mWarningToggle.setChecked(true);
        EarthquakeWarningManager.getInstance().openEarthquakeWarning(this.mContext);
        AnalyticHelper.trackMainResultActionModuleClick(AnalyticHelper.MAIN_SWITCH_ON);
        this.llSetting.setEnabled(true);
    }

    public void disAgreeResult() {
        this.mWarningToggle.setChecked(false);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.ew_main, str);
        this.mContext = getContext();
        this.mActivity = getActivity();
        this.mWarningToggle = (CheckBoxPreference) findPreference("slide_earthquake_warning");
        this.mWarningToggle.setOnPreferenceChangeListener(this.mChangeListener);
        this.llSetting = findPreference("ll_setting");
        boolean isEarthquakeWarningOpen = Utils.isEarthquakeWarningOpen();
        this.llSetting.setEnabled(isEarthquakeWarningOpen);
        this.mWarningToggle.setChecked(isEarthquakeWarningOpen);
        this.llContact = findPreference("container_contact");
        this.llEmergencyCard = findPreference("container_emergency_card");
        this.llSafePlace = findPreference("container_safe_place");
        this.llWarnintList = findPreference("container_warning_list");
        this.llSetting.setOnPreferenceClickListener(this.mClickListener);
        this.llContact.setOnPreferenceClickListener(this.mClickListener);
        this.llEmergencyCard.setOnPreferenceClickListener(this.mClickListener);
        this.llSafePlace.setOnPreferenceClickListener(this.mClickListener);
        this.llWarnintList.setOnPreferenceClickListener(this.mClickListener);
    }

    public void onResume() {
        super.onResume();
        boolean isEarthquakeWarningOpen = Utils.isEarthquakeWarningOpen();
        this.llSetting.setEnabled(isEarthquakeWarningOpen);
        this.mWarningToggle.setChecked(isEarthquakeWarningOpen);
    }

    public void pickFromContacts() {
        if (this.isPreloadGoogleCsp) {
            pickFromAndroidContacts();
            return;
        }
        try {
            Intent intent = new Intent("com.android.contacts.action.GET_MULTIPLE_PHONES");
            intent.addCategory(Constants.System.CATEGORY_DEFALUT);
            intent.setType("vnd.android.cursor.dir/phone_v2");
            intent.putExtra("android.intent.extra.include_unknown_numbers", true);
            intent.putExtra("android.intent.extra.initial_picker_tab", 1);
            intent.putExtra("com.android.contacts.extra.MAX_COUNT", 1);
            this.mActivity.startActivityForResult(intent, 1000);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void setContacts(List<String> list, List<String> list2) {
        if (list2.size() > 0) {
            setContactText(list.get(0), list2.get(0));
            Utils.setContact(list2.get(0));
            Utils.setContactName(list.get(0));
            AnalyticHelper.trackMainResultActionModuleClick(AnalyticHelper.MAIN_ADD_CONTACT);
            return;
        }
        this.llContact.setSummary((int) R.string.ew_main_contact_tips);
    }
}
