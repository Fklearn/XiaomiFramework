package com.miui.securityscan.a;

import android.content.Context;
import android.os.AsyncTask;
import com.miui.securityscan.i.m;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.ArrayList;
import java.util.List;

class t extends AsyncTask<Void, Void, List<AbsModel>> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7596a;

    t(Context context) {
        this.f7596a = context;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public List<AbsModel> doInBackground(Void... voidArr) {
        List<GroupModel> produceManualGroupModel;
        List<String> b2 = m.b();
        if (b2 == null || b2.isEmpty() || (produceManualGroupModel = ModelFactory.produceManualGroupModel(this.f7596a)) == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (GroupModel modelList : produceManualGroupModel) {
            for (AbsModel next : modelList.getModelList()) {
                if (b2.contains(next.getItemKey())) {
                    arrayList.add(next);
                }
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(List<AbsModel> list) {
        G.b("toggle_suggest_neglect", (list == null || list.isEmpty()) ? 0 : 1);
    }
}
