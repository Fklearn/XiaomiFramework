package com.miui.antispam.policy;

import android.content.Context;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.os.Build;
import miui.provider.ExtraTelephony;

public class ServiceSmsPolicy extends a {
    public ServiceSmsPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        if (Build.IS_INTERNATIONAL_BUILD || !ExtraTelephony.isServiceNumber(eVar.f2369b) || com.miui.antispam.db.d.a(this.mContext, "service_sms_mode", eVar.f2370c, 1) != 0) {
            return null;
        }
        return new a.C0035a(this, true, 10);
    }
}
