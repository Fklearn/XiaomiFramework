package com.miui.optimizemanage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.i.a;
import com.miui.optimizemanage.c.f;
import com.miui.securityscan.c.e;
import com.miui.securityscan.i.h;
import org.json.JSONObject;

class q extends a<f> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v f5985b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    q(v vVar, Context context) {
        super(context);
        this.f5985b = vVar;
    }

    public f loadInBackground() {
        f fVar = null;
        try {
            String b2 = h.b(this.f5985b.getActivity(), "om_adv_data");
            if (!TextUtils.isEmpty(b2) && (fVar = f.a(new JSONObject(b2))) != null) {
                e a2 = e.a((Context) this.f5985b.getActivity(), "data_config");
                if (fVar.c() != null) {
                    a2.b("dataVersionOm", fVar.c());
                }
            }
        } catch (Exception e) {
            Log.e("ResultFragment", "omdatamodel create error", e);
        }
        return fVar;
    }
}
