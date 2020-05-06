package com.miui.earthquakewarning.service;

import android.os.AsyncTask;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.earthquakewarning.model.WarningResult;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.k;
import java.util.ArrayList;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class RequestWarningListTask extends AsyncTask<String, Void, WarningResult> {
    private static final String TAG = "RequestWarningListTask";
    private Listener listener;

    public interface Listener {
        void onPost(WarningResult warningResult);
    }

    private WarningResult parse(String str) {
        WarningResult warningResult = new WarningResult();
        try {
            JSONObject jSONObject = new JSONObject(str);
            warningResult.setCode(jSONObject.optInt("code"));
            JSONArray jSONArray = jSONObject.getJSONArray(DataSchemeDataSource.SCHEME_DATA);
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                WarningModel warningModel = new WarningModel();
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                warningModel.eventID = jSONObject2.optInt("event_id");
                warningModel.latitude = Double.parseDouble(jSONObject2.optString(WarningModel.Columns.LATITUDE));
                warningModel.longitude = Double.parseDouble(jSONObject2.optString(WarningModel.Columns.LONGITUDE));
                warningModel.magnitude = Float.parseFloat(jSONObject2.optString(WarningModel.Columns.MAGNITUDE));
                warningModel.depth = jSONObject2.optInt(WarningModel.Columns.DEPTH);
                warningModel.signature = jSONObject2.optString(WarningModel.Columns.SIGNATURE);
                warningModel.startTime = jSONObject2.optLong("startAt");
                warningModel.epicenter = jSONObject2.optString(WarningModel.Columns.EPICENTER);
                arrayList.add(warningModel);
            }
            warningResult.setData(arrayList);
        } catch (Exception unused) {
            Log.e(TAG, "parse json failed");
        }
        return warningResult;
    }

    /* access modifiers changed from: protected */
    public WarningResult doInBackground(String... strArr) {
        if (!h.i()) {
            return null;
        }
        return parse(k.a((Map<String, String>) null, Constants.REQUEST_WARNLIST_URL, "7htr5238-a8cf-3k79-ec73-75382145ns5c", new j("ew_requestwarninglist")));
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(WarningResult warningResult) {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onPost(warningResult);
        }
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
