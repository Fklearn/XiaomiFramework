package com.miui.antispam.policy;

import android.content.Context;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.os.Build;
import miui.provider.ExtraTelephony;

public class StrangerPolicy extends a {
    public StrangerPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        int i = eVar.f2371d;
        if (i == 2) {
            if (com.miui.antispam.db.d.a(this.mContext, "stranger_call_mode", eVar.f2370c, 1) == 0) {
                return new a.C0035a(this, true, 7);
            }
            return null;
        } else if (i != 1) {
            return null;
        } else {
            if (!Build.IS_INTERNATIONAL_BUILD) {
                if (ExtraTelephony.isServiceNumber(eVar.f2369b) || com.miui.antispam.db.d.a(this.mContext, "stranger_sms_mode", eVar.f2370c, 1) != 0) {
                    return null;
                }
                return new a.C0035a(this, true, 7);
            } else if (com.miui.antispam.db.d.a(this.mContext, "stranger_sms_mode", eVar.f2370c, 1) == 0) {
                return new a.C0035a(this, true, 7);
            } else {
                return null;
            }
        }
    }
}
