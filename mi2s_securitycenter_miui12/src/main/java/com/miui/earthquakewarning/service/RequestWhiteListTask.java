package com.miui.earthquakewarning.service;

import android.os.AsyncTask;
import android.util.Log;
import b.b.c.g.a;
import b.b.c.g.c;
import b.b.c.g.f;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.earthquakewarning.Constants;
import com.miui.earthquakewarning.db.EarthquakeDBHelper;
import com.miui.earthquakewarning.model.WhiteListResult;
import com.miui.luckymoney.model.message.Impl.MiTalkMessage;
import com.miui.networkassistant.firewall.UserConfigure;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import org.json.JSONObject;

public class RequestWhiteListTask extends AsyncTask<String, Void, WhiteListResult> {
    private static final String TAG = "RequestWhiteListTask";
    private Listener listener;

    public interface Listener {
        void onPost(WhiteListResult whiteListResult);
    }

    /* access modifiers changed from: protected */
    public WhiteListResult doInBackground(String... strArr) {
        if (!h.i()) {
            return null;
        }
        ArrayList<c> arrayList = new ArrayList<>();
        arrayList.add(new c(UserConfigure.Columns.USER_ID, strArr[0]));
        boolean z = true;
        arrayList.add(new c(MiTalkMessage.CONVERSATION_TYPE_GROUP, strArr[1]));
        arrayList.add(new c("biz", EarthquakeDBHelper.TABLE_NAME));
        arrayList.add(new c("sign", f.a(arrayList, Constants.UUID_WHITELIST_CONFIG)));
        StringBuilder sb = new StringBuilder();
        for (c cVar : arrayList) {
            sb.append(!z ? "&" : "?");
            sb.append(cVar.a());
            sb.append("=");
            sb.append(cVar.b());
            z = false;
        }
        String a2 = a.a(Constants.REQUEST_WHITELIST_URL + sb.toString(), new j("ew_requestwhitelist"));
        WhiteListResult whiteListResult = new WhiteListResult();
        try {
            JSONObject jSONObject = new JSONObject(a2);
            whiteListResult.setCode(jSONObject.optInt("code"));
            JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
            WhiteListResult.DataBean dataBean = new WhiteListResult.DataBean();
            dataBean.setCheckResult(optJSONObject.optBoolean("checkResult"));
            whiteListResult.setData(dataBean);
        } catch (Exception unused) {
            Log.e(TAG, "parse json failed");
        }
        return whiteListResult;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(WhiteListResult whiteListResult) {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onPost(whiteListResult);
        }
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }
}
