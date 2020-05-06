package b.b.l;

import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.j;
import com.miui.common.persistence.b;
import com.miui.gamebooster.model.ActiveModel;
import com.miui.securityscan.i.k;
import java.util.HashMap;
import java.util.Map;
import miui.cloud.CloudPushConstants;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f1841a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b f1842b;

    a(b bVar, String str) {
        this.f1842b = bVar;
        this.f1841a = str;
    }

    public void run() {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            HashMap hashMap = new HashMap();
            hashMap.put("channel", "01-18-02");
            hashMap.put(CloudPushConstants.WATERMARK_TYPE.SUBSCRIPTION, this.f1841a);
            ActiveModel a2 = this.f1842b.o(k.a((Map<String, String>) hashMap, "https://adv.sec.miui.com/info/inputAdv", new j("gamebooster_active")));
            if (a2 != null) {
                ActiveModel g = this.f1842b.g(this.f1841a);
                a2.setPreReqeustTime(currentTimeMillis);
                if (g != null && TextUtils.equals(a2.getId(), g.getId())) {
                    a2.setHasRedPointShow(g.isHasRedPointShow());
                    a2.setHasBubbleShow(g.isHasBubbleShow());
                }
                b.b(this.f1841a, this.f1842b.a(a2));
            }
        } catch (Exception unused) {
            Log.e(b.f1843a, "error proccess active with some exceptions");
        }
    }
}
