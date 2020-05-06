package com.miui.earthquakewarning.service;

import android.os.AsyncTask;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.model.SignatureReuslt;
import com.miui.earthquakewarning.utils.FileUtils;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.k;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class RequestSignatureTask extends AsyncTask<String, Void, SignatureReuslt> {
    private static final String TAG = "RequestSignatureTask";
    private Listener listener;

    public interface Listener {
        void onPost(SignatureReuslt signatureReuslt);
    }

    private SignatureReuslt parse(String str) {
        SignatureReuslt signatureReuslt = new SignatureReuslt();
        try {
            JSONObject jSONObject = new JSONObject(str);
            signatureReuslt.setCode(jSONObject.optInt("code", -1));
            JSONArray jSONArray = jSONObject.getJSONArray("datas");
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                SignatureReuslt.DatasBean datasBean = new SignatureReuslt.DatasBean();
                datasBean.setChannel(jSONObject2.optString("channel"));
                JSONArray jSONArray2 = jSONObject2.getJSONArray(DataSchemeDataSource.SCHEME_DATA);
                ArrayList arrayList2 = new ArrayList();
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    JSONObject jSONObject3 = jSONArray2.getJSONObject(i2);
                    SignatureReuslt.DataBean dataBean = new SignatureReuslt.DataBean();
                    dataBean.setCode(jSONObject3.optInt("code"));
                    dataBean.setDistrict(jSONObject3.optString("district"));
                    JSONArray optJSONArray = jSONObject3.optJSONArray("signs");
                    ArrayList arrayList3 = new ArrayList();
                    for (int i3 = 0; i3 < optJSONArray.length(); i3++) {
                        arrayList3.add(optJSONArray.getString(i3));
                    }
                    dataBean.setSigns(arrayList3);
                    arrayList2.add(dataBean);
                }
                datasBean.setData(arrayList2);
                arrayList.add(datasBean);
            }
            signatureReuslt.setDatas(arrayList);
        } catch (Exception unused) {
            Log.e(TAG, "parse json failed");
        }
        return signatureReuslt;
    }

    /* access modifiers changed from: protected */
    public SignatureReuslt doInBackground(String... strArr) {
        if (!h.i()) {
            return null;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("channel", strArr[0]);
        String a2 = k.a((Map<String, String>) hashMap, Constants.REQUEST_SIGNATURE_URL, "7htr5238-a8cf-3k79-ec73-75382145ns5c", new j("ew_requestsignature"));
        FileUtils.saveSignatureToData(a2, Application.d());
        return parse(a2);
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(SignatureReuslt signatureReuslt) {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onPost(signatureReuslt);
        }
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
