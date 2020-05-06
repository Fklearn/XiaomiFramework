package com.miui.earthquakewarning.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import b.b.c.c.a;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.utils.MsgObservable;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.securitycenter.R;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import miui.app.AlertDialog;
import miui.os.Build;

public class EarthquakeWarningEmergencyActivity extends a implements Observer {
    private TextView mAllergy;
    private TextView mBirthday;
    private TextView mBloodType;
    private Context mContext;
    private TextView mIdcard;
    private TextView mMedical;
    private TextView mMedicine;
    private TextView mName;
    private TextView mOrganDonation;

    /* access modifiers changed from: private */
    public void deleteAllInfo() {
        b.b(Constants.PREF_KEY_NAME, "");
        b.b(Constants.PREF_KEY_BIRTHDAY, "");
        b.b(Constants.PREF_KEY_IDCARD, "");
        b.b(Constants.PREF_KEY_BLOOD_TYPE, -1);
        b.b(Constants.PREF_KEY_ALLERGY, "");
        b.b(Constants.PREF_KEY_MEDICINE, "");
        b.b(Constants.PREF_KEY_MEDICAL, "");
        b.b(Constants.PREF_KEY_ORGAN_DONATION, -1);
        Utils.setEmergencyDeleteAll(true);
    }

    private void initData() {
        String a2 = b.a(Constants.PREF_KEY_NAME, "");
        if (!TextUtils.isEmpty(a2)) {
            this.mName.setText(a2);
        } else {
            this.mName.setVisibility(8);
        }
        String a3 = b.a(Constants.PREF_KEY_BIRTHDAY, "");
        if (!TextUtils.isEmpty(a3)) {
            this.mBirthday.setText(a3);
        } else {
            this.mBirthday.setVisibility(8);
        }
        String a4 = b.a(Constants.PREF_KEY_IDCARD, "");
        if (!TextUtils.isEmpty(a4)) {
            this.mIdcard.setText(a4);
        } else {
            this.mIdcard.setVisibility(8);
        }
        int a5 = b.a(Constants.PREF_KEY_BLOOD_TYPE, -1);
        if (a5 > -1) {
            this.mBloodType.setText(getResources().getStringArray(R.array.ew_emergency_blood_type_arrays)[a5]);
        } else {
            this.mBloodType.setVisibility(8);
        }
        String a6 = b.a(Constants.PREF_KEY_ALLERGY, "");
        if (!TextUtils.isEmpty(a6)) {
            this.mAllergy.setText(a6);
        } else {
            this.mAllergy.setVisibility(8);
        }
        String a7 = b.a(Constants.PREF_KEY_MEDICINE, "");
        if (!TextUtils.isEmpty(a7)) {
            this.mMedicine.setText(a7);
        } else {
            this.mMedicine.setVisibility(8);
        }
        String a8 = b.a(Constants.PREF_KEY_MEDICAL, "");
        if (!TextUtils.isEmpty(a8)) {
            this.mMedical.setText(a8);
        } else {
            this.mMedical.setVisibility(8);
        }
        int a9 = b.a(Constants.PREF_KEY_ORGAN_DONATION, -1);
        if (a9 > -1) {
            this.mOrganDonation.setText(getResources().getStringArray(R.array.ew_emergency_organ_donation_arrays)[a9]);
            return;
        }
        this.mOrganDonation.setVisibility(8);
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [android.content.Context, com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyActivity] */
    private void showDeleteAllDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.ew_main_close_title).setMessage(R.string.ew_emergency_delete_text).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                EarthquakeWarningEmergencyActivity.this.deleteAllInfo();
                EarthquakeWarningEmergencyActivity.this.finish();
                dialogInterface.dismiss();
            }
        }).setNegativeButton(17039360, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x002f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateData(java.util.HashMap<java.lang.String, java.lang.String> r5) {
        /*
            r4 = this;
            java.lang.String r0 = "name"
            boolean r1 = r5.containsKey(r0)
            r2 = 0
            r3 = 8
            if (r1 == 0) goto L_0x0022
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0022
            android.widget.TextView r1 = r4.mName
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mName
            r1.setText(r0)
            goto L_0x0027
        L_0x0022:
            android.widget.TextView r0 = r4.mName
            r0.setVisibility(r3)
        L_0x0027:
            java.lang.String r0 = "birthday"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x0046
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0046
            android.widget.TextView r1 = r4.mBirthday
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mBirthday
            r1.setText(r0)
            goto L_0x004b
        L_0x0046:
            android.widget.TextView r0 = r4.mBirthday
            r0.setVisibility(r3)
        L_0x004b:
            java.lang.String r0 = "idcard"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x006a
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x006a
            android.widget.TextView r1 = r4.mIdcard
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mIdcard
            r1.setText(r0)
            goto L_0x006f
        L_0x006a:
            android.widget.TextView r0 = r4.mIdcard
            r0.setVisibility(r3)
        L_0x006f:
            java.lang.String r0 = "bloodType"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x008e
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x008e
            android.widget.TextView r1 = r4.mBloodType
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mBloodType
            r1.setText(r0)
            goto L_0x0093
        L_0x008e:
            android.widget.TextView r0 = r4.mBloodType
            r0.setVisibility(r3)
        L_0x0093:
            java.lang.String r0 = "allergy"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x00b2
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x00b2
            android.widget.TextView r1 = r4.mAllergy
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mAllergy
            r1.setText(r0)
            goto L_0x00b7
        L_0x00b2:
            android.widget.TextView r0 = r4.mAllergy
            r0.setVisibility(r3)
        L_0x00b7:
            java.lang.String r0 = "medicine"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x00d6
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x00d6
            android.widget.TextView r1 = r4.mMedicine
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mMedicine
            r1.setText(r0)
            goto L_0x00db
        L_0x00d6:
            android.widget.TextView r0 = r4.mMedicine
            r0.setVisibility(r3)
        L_0x00db:
            java.lang.String r0 = "medical"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x00fa
            java.lang.Object r0 = r5.get(r0)
            java.lang.String r0 = (java.lang.String) r0
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x00fa
            android.widget.TextView r1 = r4.mMedical
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.mMedical
            r1.setText(r0)
            goto L_0x00ff
        L_0x00fa:
            android.widget.TextView r0 = r4.mMedical
            r0.setVisibility(r3)
        L_0x00ff:
            java.lang.String r0 = "organDonation"
            boolean r1 = r5.containsKey(r0)
            if (r1 == 0) goto L_0x011e
            java.lang.Object r5 = r5.get(r0)
            java.lang.String r5 = (java.lang.String) r5
            boolean r0 = android.text.TextUtils.isEmpty(r5)
            if (r0 != 0) goto L_0x011e
            android.widget.TextView r0 = r4.mOrganDonation
            r0.setVisibility(r2)
            android.widget.TextView r0 = r4.mOrganDonation
            r0.setText(r5)
            goto L_0x0123
        L_0x011e:
            android.widget.TextView r5 = r4.mOrganDonation
            r5.setVisibility(r3)
        L_0x0123:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyActivity.updateData(java.util.HashMap):void");
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [b.b.c.c.a, java.util.Observer, android.content.Context, com.miui.earthquakewarning.ui.EarthquakeWarningEmergencyActivity, miui.app.Activity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.earthquake_warning_activity_emergency);
        if (Build.IS_INTERNATIONAL_BUILD) {
            finish();
            return;
        }
        this.mContext = this;
        MsgObservable.getInstance().addObserver(this);
        this.mName = (TextView) findViewById(R.id.name);
        this.mBirthday = (TextView) findViewById(R.id.birthday);
        this.mIdcard = (TextView) findViewById(R.id.idcard);
        this.mBloodType = (TextView) findViewById(R.id.blood_type);
        this.mAllergy = (TextView) findViewById(R.id.allergy);
        this.mMedicine = (TextView) findViewById(R.id.medicine);
        this.mMedical = (TextView) findViewById(R.id.medical);
        this.mOrganDonation = (TextView) findViewById(R.id.organ_donation);
        initData();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ew_emergency, menu);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        EarthquakeWarningEmergencyActivity.super.onDestroy();
        MsgObservable.getInstance().deleteObserver(this);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.em_emergency_delete /*2131296743*/:
                showDeleteAllDialog();
                break;
            case R.id.em_emergency_edit /*2131296744*/:
                startActivity(new Intent(this.mContext, EarthquakeWarningEmergencyEditActivity.class));
                break;
        }
        return EarthquakeWarningEmergencyActivity.super.onOptionsItemSelected(menuItem);
    }

    public void update(Observable observable, Object obj) {
        if (obj instanceof Message) {
            Message message = (Message) obj;
            if (message.what == 1) {
                updateData((HashMap) message.obj);
            }
        }
    }
}
