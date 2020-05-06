package com.miui.antispam.policy;

import android.content.Context;
import b.b.a.a;
import com.miui.activityutil.o;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.provider.ExtraTelephony;

public class CloudWhiteListPolicy extends a {
    public CloudWhiteListPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        if (ExtraTelephony.isInCloudPhoneList(this.mContext, eVar.f2369b, eVar.f2371d, o.f)) {
            return new a.C0035a(this, true, 0);
        }
        return null;
    }

    public int getType() {
        return a.c.e;
    }

    public a.C0035a handleData(e eVar) {
        if (!this.mJudge.b()) {
            return dbQuery(eVar);
        }
        if (this.mJudge.c(eVar)) {
            return new a.C0035a(this, true, 0);
        }
        return null;
    }
}
