package com.miui.firstaidkit;

import android.content.Context;
import android.os.Handler;
import b.b.c.j.d;
import com.miui.firstaidkit.a.a;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.ModelFactory;
import java.util.ArrayList;
import java.util.List;
import miui.util.Log;

public class l {

    /* renamed from: a  reason: collision with root package name */
    private static l f3955a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f3956b;

    public l(Context context) {
        this.f3956b = context.getApplicationContext();
    }

    public static synchronized l a(Context context) {
        l lVar;
        synchronized (l.class) {
            if (f3955a == null) {
                f3955a = new l(context.getApplicationContext());
            }
            lVar = f3955a;
        }
        return lVar;
    }

    public void a(Handler handler, String str, a aVar) {
        d.a(new k(this, aVar, str, handler));
    }

    public boolean a() {
        try {
            ArrayList arrayList = new ArrayList();
            List<AbsModel> produceFirstAidKitGroupModel = ModelFactory.produceFirstAidKitGroupModel(this.f3956b, "Performance");
            List<AbsModel> produceFirstAidKitGroupModel2 = ModelFactory.produceFirstAidKitGroupModel(this.f3956b, "Internet");
            List<AbsModel> produceFirstAidKitGroupModel3 = ModelFactory.produceFirstAidKitGroupModel(this.f3956b, "Operation");
            List<AbsModel> produceFirstAidKitGroupModel4 = ModelFactory.produceFirstAidKitGroupModel(this.f3956b, "ConsumePower");
            List<AbsModel> produceFirstAidKitGroupModel5 = ModelFactory.produceFirstAidKitGroupModel(this.f3956b, "Other");
            arrayList.addAll(produceFirstAidKitGroupModel);
            arrayList.addAll(produceFirstAidKitGroupModel2);
            arrayList.addAll(produceFirstAidKitGroupModel3);
            arrayList.addAll(produceFirstAidKitGroupModel4);
            arrayList.addAll(produceFirstAidKitGroupModel5);
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                AbsModel absModel = (AbsModel) arrayList.get(i2);
                absModel.scan();
                if (absModel.isSafe() != AbsModel.State.SAFE) {
                    i++;
                }
            }
            return i > 0;
        } catch (Exception e) {
            Log.e("FirstAidKitManualItemManager", "isFirstAidKitDanger", e);
            return false;
        }
    }
}
