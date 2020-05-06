package com.miui.firstaidkit;

import android.os.Handler;
import com.miui.firstaidkit.a.a;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.List;
import miui.util.Log;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a f3951a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3952b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Handler f3953c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ l f3954d;

    k(l lVar, a aVar, String str, Handler handler) {
        this.f3954d = lVar;
        this.f3951a = aVar;
        this.f3952b = str;
        this.f3953c = handler;
    }

    public void run() {
        int i;
        try {
            Log.d("FirstAidKitManualItemManager", "startScan");
            this.f3951a.b();
            List<AbsModel> produceFirstAidKitGroupModel = ModelFactory.produceFirstAidKitGroupModel(this.f3954d.f3956b, this.f3952b);
            if (produceFirstAidKitGroupModel != null) {
                int i2 = 0;
                i = 0;
                while (i2 < produceFirstAidKitGroupModel.size()) {
                    AbsModel absModel = produceFirstAidKitGroupModel.get(i2);
                    absModel.scan();
                    i2++;
                    this.f3951a.a(i2, produceFirstAidKitGroupModel.size(), absModel.getDesc());
                    if (absModel.isSafe() != AbsModel.State.SAFE) {
                        i++;
                    }
                    absModel.setFirstAidEventHandler(this.f3953c);
                }
            } else {
                i = 0;
            }
            this.f3951a.a(produceFirstAidKitGroupModel, 0, i);
        } catch (Exception e) {
            Log.e("FirstAidKitManualItemManager", "startScan", e);
        }
    }
}
