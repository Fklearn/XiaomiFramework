package com.miui.antispam.policy;

import android.content.Context;
import b.b.a.a;
import com.miui.activityutil.o;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.provider.ExtraTelephony;

public class CloudBlackListPolicy extends a {
    public CloudBlackListPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        if (!ExtraTelephony.isInCloudPhoneList(this.mContext, eVar.f2369b, eVar.f2371d, o.e)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 16);
    }

    public int getType() {
        return a.c.f1314d;
    }

    public a.C0035a handleData(e eVar) {
        if (!this.mJudge.b()) {
            return dbQuery(eVar);
        }
        if (!this.mJudge.b(eVar)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 16);
    }
}
