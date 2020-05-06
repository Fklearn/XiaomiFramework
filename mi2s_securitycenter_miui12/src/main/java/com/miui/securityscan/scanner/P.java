package com.miui.securityscan.scanner;

import android.util.Log;
import com.miui.securityscan.b.g;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.ArrayList;
import java.util.List;

class P implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f7860a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ U f7861b;

    P(U u, g gVar) {
        this.f7861b = u;
        this.f7860a = gVar;
    }

    public void run() {
        try {
            Log.d("SystemCheckManager", "scanSystemConfig start");
            this.f7860a.b();
            List<GroupModel> produceSystemGroupModel = ModelFactory.produceSystemGroupModel(this.f7861b.f7875b);
            if (produceSystemGroupModel != null) {
                Log.d("SystemCheckManager", "scanSystemConfig groupList size is " + produceSystemGroupModel.size());
                ArrayList arrayList = new ArrayList();
                for (GroupModel next : produceSystemGroupModel) {
                    next.scan();
                    AbsModel curModel = next.getCurModel();
                    if (!curModel.isScanHide()) {
                        arrayList.add(curModel);
                    }
                }
                Log.d("SystemCheckManager", "scanSystemConfig modelList size is " + arrayList.size());
                int i = 0;
                while (i < arrayList.size()) {
                    int i2 = i + 1;
                    this.f7860a.a(i2, arrayList.size(), ((AbsModel) arrayList.get(i)).getDesc());
                    i = i2;
                }
                this.f7860a.a(produceSystemGroupModel, 11);
            }
            Log.d("SystemCheckManager", "scanSystemConfig end");
        } catch (InterruptedException e) {
            this.f7860a.a();
            Log.e("SystemCheckManager", "scanSystemConfig() ScanCancelException has appeared", e);
        }
    }
}
