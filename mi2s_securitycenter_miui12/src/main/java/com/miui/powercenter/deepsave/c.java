package com.miui.powercenter.deepsave;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.e;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class c extends AsyncTask<Void, Void, List<IdeaModel>> {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7048a = "https://adv.sec.miui.com/info/layout";

    /* renamed from: b  reason: collision with root package name */
    private static final String f7049b = "https://adv.sec.intl.miui.com/info/layout";

    /* renamed from: c  reason: collision with root package name */
    private a f7050c;

    /* renamed from: d  reason: collision with root package name */
    private Context f7051d;
    private b e;

    public interface a {
        void a(List<IdeaModel> list);
    }

    public c(Context context, a aVar) {
        this.f7051d = context;
        this.f7050c = aVar;
        this.e = new b(context);
    }

    private List<IdeaModel> a(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.e("BatterySaveIdeaLoadTask", "Response is empty!");
            return null;
        }
        ArrayList arrayList = new ArrayList();
        JSONObject jSONObject = new JSONObject(str);
        if (jSONObject.has(DataSchemeDataSource.SCHEME_DATA)) {
            JSONArray jSONArray = jSONObject.getJSONArray(DataSchemeDataSource.SCHEME_DATA);
            if (jSONArray.length() == 0) {
                return null;
            }
            JSONObject jSONObject2 = jSONArray.getJSONObject(0);
            if (jSONObject2.has("list")) {
                JSONArray jSONArray2 = jSONObject2.getJSONArray("list");
                for (int i = 0; i < jSONArray2.length(); i++) {
                    JSONObject jSONObject3 = jSONArray2.getJSONObject(i).getJSONObject(DataSchemeDataSource.SCHEME_DATA);
                    IdeaModel ideaModel = new IdeaModel();
                    ideaModel.packageName = jSONObject3.getString("pkgName");
                    ideaModel.title = jSONObject3.getString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
                    ideaModel.url = jSONObject3.getString(MijiaAlertModel.KEY_URL);
                    if (!TextUtils.isEmpty(ideaModel.packageName) && !TextUtils.isEmpty(ideaModel.title) && !TextUtils.isEmpty(ideaModel.url)) {
                        arrayList.add(ideaModel);
                    }
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public List<IdeaModel> doInBackground(Void... voidArr) {
        List<IdeaModel> a2;
        String a3 = this.e.a();
        String a4 = this.e.a(a3);
        try {
            if (!TextUtils.isEmpty(a4) && (a2 = a(a4)) != null && a2.size() > 0) {
                return a2;
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("channel", "01-5");
            jSONObject.put("pkgNames", a3);
            String b2 = e.b(this.f7051d, Build.IS_INTERNATIONAL_BUILD ? f7049b : f7048a, jSONObject, "5cdd8678-cddf-4269-ab73-48387445bba6", new j("powercenter_batterysaveidealoadtask"));
            List<IdeaModel> a5 = a(b2);
            if (a5 != null && a5.size() > 0 && !this.e.a(a3, b2)) {
                Log.e("BatterySaveIdeaLoadTask", "Save install apps battery idea failed!");
            }
            return a5;
        } catch (Exception e2) {
            Log.e("BatterySaveIdeaLoadTask", "BatterySaveIdeaLoadTask error", e2);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(List<IdeaModel> list) {
        super.onPostExecute(list);
        this.f7050c.a(list);
    }
}
