package com.miui.earthquakewarning.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import b.b.c.c.a;
import b.b.c.j.i;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.analytics.AnalyticHelper;
import com.miui.earthquakewarning.utils.MsgObservable;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.securitycenter.R;
import java.util.HashMap;
import java.util.Map;
import miui.app.AlertDialog;
import miui.cloud.CloudPushConstants;
import miui.os.Build;

public class EarthquakeWarningEmergencyEditActivity extends a implements View.OnClickListener {
    private EditText mAllergy;
    private ImageView mBack;
    /* access modifiers changed from: private */
    public EditText mBirthday;
    /* access modifiers changed from: private */
    public EditText mBloodType;
    /* access modifiers changed from: private */
    public String[] mChoiceBloodType;
    /* access modifiers changed from: private */
    public String[] mChoiceOrganDonation;
    /* access modifiers changed from: private */
    public int mChooseBloodType = -1;
    /* access modifiers changed from: private */
    public int mChooseOrganDonation = -1;
    private EditText mIdcard;
    private EditText mMedical;
    private EditText mMedicine;
    private EditText mName;
    private ImageView mOk;
    /* access modifiers changed from: private */
    public EditText mOrganDonation;
    private Map<String, String> mResutMap = new HashMap();
    private ViewGroup mViewRoot;

    private String getEmergencyAllergy() {
        String a2 = b.a(Constants.PREF_KEY_ALLERGY, "");
        return !TextUtils.isEmpty(a2) ? a2 : "";
    }

    private String getEmergencyBirthday() {
        String a2 = b.a(Constants.PREF_KEY_BIRTHDAY, "");
        return !TextUtils.isEmpty(a2) ? a2 : "";
    }

    private String getEmergencyBloodType() {
        int a2 = b.a(Constants.PREF_KEY_BLOOD_TYPE, -1);
        if (a2 <= -1) {
            return "";
        }
        this.mChooseBloodType = a2;
        return this.mChoiceBloodType[a2];
    }

    private String getEmergencyIdcard() {
        String a2 = b.a(Constants.PREF_KEY_IDCARD, "");
        return !TextUtils.isEmpty(a2) ? a2 : "";
    }

    private String getEmergencyMedical() {
        String a2 = b.a(Constants.PREF_KEY_MEDICAL, "");
        return !TextUtils.isEmpty(a2) ? a2 : "";
    }

    private String getEmergencyMedicine() {
        String a2 = b.a(Constants.PREF_KEY_MEDICINE, "");
        return !TextUtils.isEmpty(a2) ? a2 : "";
    }

    private String getEmergencyName() {
        String a2 = b.a(Constants.PREF_KEY_NAME, "");
        return !TextUtils.isEmpty(a2) ? a2 : "";
    }

    private String getEmergencyOrganDonation() {
        int a2 = b.a(Constants.PREF_KEY_ORGAN_DONATION, -1);
        if (a2 <= -1) {
            return "";
        }
        this.mChooseOrganDonation = a2;
        return this.mChoiceOrganDonation[a2];
    }

    private void saveAllInfo() {
        String trim = this.mName.getText().toString().trim();
        boolean isEmpty = TextUtils.isEmpty(trim);
        this.mResutMap.put(CloudPushConstants.XML_NAME, trim);
        b.b(Constants.PREF_KEY_NAME, trim);
        String trim2 = this.mBirthday.getText().toString().trim();
        if (!TextUtils.isEmpty(trim2)) {
            isEmpty = false;
        }
        this.mResutMap.put("birthday", trim2);
        b.b(Constants.PREF_KEY_BIRTHDAY, trim2);
        String trim3 = this.mIdcard.getText().toString().trim();
        if (!TextUtils.isEmpty(trim3)) {
            isEmpty = false;
        }
        this.mResutMap.put("idcard", trim3);
        b.b(Constants.PREF_KEY_IDCARD, trim3);
        String trim4 = this.mBloodType.getText().toString().trim();
        if (!TextUtils.isEmpty(trim4)) {
            isEmpty = false;
        }
        this.mResutMap.put("bloodType", trim4);
        b.b(Constants.PREF_KEY_BLOOD_TYPE, this.mChooseBloodType);
        String trim5 = this.mAllergy.getText().toString().trim();
        if (!TextUtils.isEmpty(trim5)) {
            isEmpty = false;
        }
        this.mResutMap.put("allergy", trim5);
        b.b(Constants.PREF_KEY_ALLERGY, trim5);
        String trim6 = this.mMedicine.getText().toString().trim();
        if (!TextUtils.isEmpty(trim6)) {
            isEmpty = false;
        }
        this.mResutMap.put("medicine", trim6);
        b.b(Constants.PREF_KEY_MEDICINE, trim6);
        String trim7 = this.mMedical.getText().toString().trim();
        if (!TextUtils.isEmpty(trim7)) {
            isEmpty = false;
        }
        this.mResutMap.put("medical", trim7);
        b.b(Constants.PREF_KEY_MEDICAL, trim7);
        String trim8 = this.mOrganDonation.getText().toString().trim();
        if (!TextUtils.isEmpty(trim8)) {
            isEmpty = false;
        }
        this.mResutMap.put("organDonation", trim8);
        b.b(Constants.PREF_KEY_ORGAN_DONATION, this.mChooseOrganDonation);
        Utils.setEmergencyDeleteAll(isEmpty);
        MsgObservable.getInstance().sendMessage(1, this.mResutMap);
        AnalyticHelper.trackMainResultActionModuleClick(AnalyticHelper.MAIN_EMERGENCY);
        finish();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyEditActivity, miui.app.Activity] */
    private void showBloodTypeChooseDialog() {
        int a2 = b.a(Constants.PREF_KEY_BLOOD_TYPE, -1);
        if (this.mChooseBloodType == -1) {
            this.mChooseBloodType = a2;
        }
        new AlertDialog.Builder(this).setTitle(getString(R.string.ew_emergency_choose)).setSingleChoiceItems(this.mChoiceBloodType, this.mChooseBloodType, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                int unused = EarthquakeWarningEmergencyEditActivity.this.mChooseBloodType = i;
                EarthquakeWarningEmergencyEditActivity.this.mBloodType.setText(EarthquakeWarningEmergencyEditActivity.this.mChoiceBloodType[i]);
                dialogInterface.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showChooseBirthdayDialog() {
        /*
            r9 = this;
            java.util.Calendar r0 = java.util.Calendar.getInstance()
            long r1 = java.lang.System.currentTimeMillis()
            r0.setTimeInMillis(r1)
            miui.app.DatePickerDialog r1 = new miui.app.DatePickerDialog
            com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyEditActivity$1 r5 = new com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyEditActivity$1
            r5.<init>()
            r2 = 1
            int r6 = r0.get(r2)
            r2 = 2
            int r7 = r0.get(r2)
            r2 = 5
            int r8 = r0.get(r2)
            r3 = r1
            r4 = r9
            r3.<init>(r4, r5, r6, r7, r8)
            r1.show()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyEditActivity.showChooseBirthdayDialog():void");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyEditActivity, miui.app.Activity] */
    private void showOrganDonationChooseDialog() {
        int a2 = b.a(Constants.PREF_KEY_ORGAN_DONATION, -1);
        if (this.mChooseOrganDonation == -1) {
            this.mChooseOrganDonation = a2;
        }
        new AlertDialog.Builder(this).setTitle(getString(R.string.ew_emergency_organ_donation)).setSingleChoiceItems(this.mChoiceOrganDonation, this.mChooseOrganDonation, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                int unused = EarthquakeWarningEmergencyEditActivity.this.mChooseOrganDonation = i;
                EarthquakeWarningEmergencyEditActivity.this.mOrganDonation.setText(EarthquakeWarningEmergencyEditActivity.this.mChoiceOrganDonation[i]);
                dialogInterface.dismiss();
            }
        }).setNegativeButton(getString(R.string.cancel), (DialogInterface.OnClickListener) null).show();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.birthday /*2131296525*/:
                showChooseBirthdayDialog();
                return;
            case R.id.blood_type /*2131296527*/:
                showBloodTypeChooseDialog();
                return;
            case R.id.cancel /*2131296578*/:
                finish();
                return;
            case R.id.ok /*2131297398*/:
                saveAllInfo();
                return;
            case R.id.organ_donation /*2131297413*/:
                showOrganDonationChooseDialog();
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [b.b.c.c.a, android.content.Context, com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyEditActivity, android.view.View$OnClickListener, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.earthquake_warning_activity_emergency_edit);
        if (Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        this.mChoiceBloodType = getResources().getStringArray(R.array.ew_emergency_blood_type_arrays);
        this.mChoiceOrganDonation = getResources().getStringArray(R.array.ew_emergency_organ_donation_arrays);
        this.mViewRoot = (ViewGroup) findViewById(R.id.root_view);
        this.mOk = (ImageView) findViewById(R.id.ok);
        this.mBack = (ImageView) findViewById(R.id.cancel);
        this.mName = (EditText) findViewById(R.id.name);
        this.mBirthday = (EditText) findViewById(R.id.birthday);
        this.mIdcard = (EditText) findViewById(R.id.idcard);
        this.mBloodType = (EditText) findViewById(R.id.blood_type);
        this.mAllergy = (EditText) findViewById(R.id.allergy);
        this.mMedicine = (EditText) findViewById(R.id.medicine);
        this.mMedical = (EditText) findViewById(R.id.medical);
        this.mOrganDonation = (EditText) findViewById(R.id.organ_donation);
        if (isDarkModeEnable()) {
            this.mBack.setBackgroundResource(miui.R.drawable.action_mode_immersion_close_dark);
            this.mOk.setBackgroundResource(miui.R.drawable.action_mode_immersion_done_dark);
        }
        if (i.e()) {
            int f = i.f(this) - ((int) getResources().getDimension(R.dimen.pc_status_bar_margin_top));
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mViewRoot.getLayoutParams();
            marginLayoutParams.topMargin += f;
            this.mViewRoot.setLayoutParams(marginLayoutParams);
        }
        this.mBirthday.setOnClickListener(this);
        this.mBloodType.setOnClickListener(this);
        this.mOrganDonation.setOnClickListener(this);
        this.mOk.setOnClickListener(this);
        this.mBack.setOnClickListener(this);
        if (!TextUtils.isEmpty(getEmergencyName())) {
            this.mName.setText(getEmergencyName());
        }
        if (!TextUtils.isEmpty(getEmergencyBirthday())) {
            this.mBirthday.setText(getEmergencyBirthday());
        }
        if (!TextUtils.isEmpty(getEmergencyIdcard())) {
            this.mIdcard.setText(getEmergencyIdcard());
        }
        if (!TextUtils.isEmpty(getEmergencyBloodType())) {
            this.mBloodType.setText(getEmergencyBloodType());
        }
        if (!TextUtils.isEmpty(getEmergencyAllergy())) {
            this.mAllergy.setText(getEmergencyAllergy());
        }
        if (!TextUtils.isEmpty(getEmergencyMedicine())) {
            this.mMedicine.setText(getEmergencyMedicine());
        }
        if (!TextUtils.isEmpty(getEmergencyMedical())) {
            this.mMedical.setText(getEmergencyMedical());
        }
        if (!TextUtils.isEmpty(getEmergencyOrganDonation())) {
            this.mOrganDonation.setText(getEmergencyOrganDonation());
        }
    }
}
