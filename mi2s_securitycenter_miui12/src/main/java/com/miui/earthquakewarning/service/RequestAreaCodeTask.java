package com.miui.earthquakewarning.service;

import android.os.AsyncTask;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.EarthquakeWarningManager;
import com.miui.earthquakewarning.model.AreaCodeResult;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.k;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class RequestAreaCodeTask extends AsyncTask<String, Void, AreaCodeResult> {
    private static final String TAG = "RequestAreaCodeTask";

    private AreaCodeResult parse(String str) {
        AreaCodeResult areaCodeResult = new AreaCodeResult();
        try {
            JSONObject jSONObject = new JSONObject(str);
            areaCodeResult.setErr(jSONObject.optBoolean("err"));
            JSONObject jSONObject2 = new JSONObject(jSONObject.optString(DataSchemeDataSource.SCHEME_DATA));
            AreaCodeResult.DataBean dataBean = new AreaCodeResult.DataBean();
            dataBean.setCityId(jSONObject2.optInt("cityId"));
            dataBean.setDistrictId(jSONObject2.optInt("districtId"));
            areaCodeResult.setData(dataBean);
        } catch (Exception unused) {
            Log.e(TAG, "parse json failed");
        }
        return areaCodeResult;
    }

    /* access modifiers changed from: protected */
    public AreaCodeResult doInBackground(String... strArr) {
        if (!h.i()) {
            return null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put(WarningModel.Columns.LONGITUDE, strArr[0]);
        hashMap.put(WarningModel.Columns.LATITUDE, strArr[1]);
        return parse(k.a((Map<String, String>) hashMap, Constants.REQUEST_AREA_CODE_URL, "7htr5238-a8cf-3k79-ec73-75382145ns5c", new j("ew_requestareacode")));
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(AreaCodeResult areaCodeResult) {
        if (areaCodeResult != null && !areaCodeResult.isErr() && areaCodeResult.getData() != null) {
            int i = 0;
            if (areaCodeResult.getData().getCityId() > 0) {
                i = areaCodeResult.getData().getCityId();
            }
            if (i > 0) {
                int previousAreaCode = Utils.getPreviousAreaCode();
                if (previousAreaCode > 0 && previousAreaCode != i) {
                    EarthquakeWarningManager.getInstance().unsetTopicForPush(Application.d(), String.valueOf(previousAreaCode));
                }
                Utils.setPreviousAreaCode(i);
                Utils.setPreviousAreaDistrictCode(areaCodeResult.getData().getDistrictId());
                EarthquakeWarningManager.getInstance().setTopicForPush(Application.d(), String.valueOf(i));
            }
        }
    }
}
