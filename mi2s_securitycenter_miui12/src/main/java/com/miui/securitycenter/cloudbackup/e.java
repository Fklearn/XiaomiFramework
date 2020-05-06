package com.miui.securitycenter.cloudbackup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

public class e {
    public static JSONObject a(Context context) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(Constants.PREFERENCE_KEY_OPEN_EARTHQUAKE_WARNING, Utils.isEarthquakeWarningOpen());
            jSONObject.put(Constants.PREFERENCE_KEY_OPEN_LOW_EARTHQUAKE_WARNING, Utils.isLowEarthquakeWarningOpen());
            if (!TextUtils.isEmpty(Utils.getContact())) {
                jSONObject.put(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT, Utils.getContact());
                jSONObject.put(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT_NAME, Utils.getContactName());
            }
            if (!TextUtils.isEmpty(b.a(Constants.PREF_KEY_NAME, ""))) {
                jSONObject.put(Constants.PREF_KEY_NAME, b.a(Constants.PREF_KEY_NAME, ""));
            }
            if (!TextUtils.isEmpty(b.a(Constants.PREF_KEY_BIRTHDAY, ""))) {
                jSONObject.put(Constants.PREF_KEY_BIRTHDAY, b.a(Constants.PREF_KEY_BIRTHDAY, ""));
            }
            if (!TextUtils.isEmpty(b.a(Constants.PREF_KEY_IDCARD, ""))) {
                jSONObject.put(Constants.PREF_KEY_IDCARD, b.a(Constants.PREF_KEY_IDCARD, ""));
            }
            if (b.a(Constants.PREF_KEY_BLOOD_TYPE, -1) > -1) {
                jSONObject.put(Constants.PREF_KEY_BLOOD_TYPE, b.a(Constants.PREF_KEY_BLOOD_TYPE, -1));
            }
            if (!TextUtils.isEmpty(b.a(Constants.PREF_KEY_ALLERGY, ""))) {
                jSONObject.put(Constants.PREF_KEY_ALLERGY, b.a(Constants.PREF_KEY_ALLERGY, ""));
            }
            if (!TextUtils.isEmpty(b.a(Constants.PREF_KEY_MEDICINE, ""))) {
                jSONObject.put(Constants.PREF_KEY_MEDICINE, b.a(Constants.PREF_KEY_MEDICINE, ""));
            }
            if (!TextUtils.isEmpty(b.a(Constants.PREF_KEY_MEDICAL, ""))) {
                jSONObject.put(Constants.PREF_KEY_MEDICAL, b.a(Constants.PREF_KEY_MEDICAL, ""));
            }
            if (b.a(Constants.PREF_KEY_ORGAN_DONATION, -1) > -1) {
                jSONObject.put(Constants.PREF_KEY_ORGAN_DONATION, b.a(Constants.PREF_KEY_ORGAN_DONATION, -1));
            }
            jSONObject.put(Constants.PREF_KEY_ALL_INFO_DELETE, Utils.isEmergencyInfoEmpty());
        } catch (JSONException unused) {
            Log.v("EWSettingsCloudBackupHelper", "Save settings to cloud failed. ");
        }
        return jSONObject;
    }

    public static void a(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            if (jSONObject.has(Constants.PREFERENCE_KEY_OPEN_EARTHQUAKE_WARNING)) {
                boolean optBoolean = jSONObject.optBoolean(Constants.PREFERENCE_KEY_OPEN_EARTHQUAKE_WARNING);
                Utils.toggle(optBoolean);
                if (optBoolean) {
                    EarthquakeWarningManager.getInstance().openEarthquakeWarning(context);
                }
            }
            if (jSONObject.has(Constants.PREFERENCE_KEY_OPEN_LOW_EARTHQUAKE_WARNING)) {
                Utils.setLowEarthquakeWarningOpen(jSONObject.optBoolean(Constants.PREFERENCE_KEY_OPEN_LOW_EARTHQUAKE_WARNING));
            }
            if (jSONObject.has(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT)) {
                Utils.setContact(jSONObject.optString(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT));
                Utils.setContactName(jSONObject.optString(Constants.PREFERENCE_KEY_EARTHQUAKE_WARNING_CONTACT_NAME));
            }
            if (jSONObject.has(Constants.PREF_KEY_NAME)) {
                b.b(Constants.PREF_KEY_NAME, jSONObject.optString(Constants.PREF_KEY_NAME));
            }
            if (jSONObject.has(Constants.PREF_KEY_BIRTHDAY)) {
                b.b(Constants.PREF_KEY_BIRTHDAY, jSONObject.optString(Constants.PREF_KEY_BIRTHDAY));
            }
            if (jSONObject.has(Constants.PREF_KEY_IDCARD)) {
                b.b(Constants.PREF_KEY_IDCARD, jSONObject.optString(Constants.PREF_KEY_IDCARD));
            }
            if (jSONObject.has(Constants.PREF_KEY_BLOOD_TYPE)) {
                b.b(Constants.PREF_KEY_BLOOD_TYPE, jSONObject.optInt(Constants.PREF_KEY_BLOOD_TYPE));
            }
            if (jSONObject.has(Constants.PREF_KEY_ALLERGY)) {
                b.b(Constants.PREF_KEY_ALLERGY, jSONObject.optString(Constants.PREF_KEY_ALLERGY));
            }
            if (jSONObject.has(Constants.PREF_KEY_MEDICINE)) {
                b.b(Constants.PREF_KEY_MEDICINE, jSONObject.optString(Constants.PREF_KEY_MEDICINE));
            }
            if (jSONObject.has(Constants.PREF_KEY_MEDICAL)) {
                b.b(Constants.PREF_KEY_MEDICAL, jSONObject.optString(Constants.PREF_KEY_MEDICAL));
            }
            if (jSONObject.has(Constants.PREF_KEY_ORGAN_DONATION)) {
                b.b(Constants.PREF_KEY_ORGAN_DONATION, jSONObject.optInt(Constants.PREF_KEY_ORGAN_DONATION));
            }
            if (jSONObject.has(Constants.PREF_KEY_ALL_INFO_DELETE)) {
                Utils.setEmergencyDeleteAll(jSONObject.optBoolean(Constants.PREF_KEY_ALL_INFO_DELETE));
            }
        }
    }
}
