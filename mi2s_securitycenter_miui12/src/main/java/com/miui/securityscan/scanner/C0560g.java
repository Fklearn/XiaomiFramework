package com.miui.securityscan.scanner;

import com.miui.securityscan.b.g;
import com.miui.securityscan.i.m;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.List;
import miui.util.Log;

/* renamed from: com.miui.securityscan.scanner.g  reason: case insensitive filesystem */
class C0560g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f7894a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0561h f7895b;

    C0560g(C0561h hVar, g gVar) {
        this.f7895b = hVar;
        this.f7894a = gVar;
    }

    public void run() {
        try {
            Log.e("ManualItemManager", "startScan");
            this.f7894a.b();
            List<GroupModel> produceManualGroupModel = ModelFactory.produceManualGroupModel(this.f7895b.f7897b);
            List<String> b2 = m.b();
            if (produceManualGroupModel != null) {
                this.f7895b.a(produceManualGroupModel, b2);
                int i = 0;
                while (i < produceManualGroupModel.size()) {
                    GroupModel groupModel = produceManualGroupModel.get(i);
                    groupModel.scan();
                    i++;
                    this.f7894a.a(i, produceManualGroupModel.size(), groupModel.getDesc());
                }
            }
            this.f7894a.a(produceManualGroupModel, 0);
        } catch (InterruptedException unused) {
            this.f7894a.a();
            Log.e("ManualItemManager", "startScan() InterruptedException has appeared");
        }
    }
}
