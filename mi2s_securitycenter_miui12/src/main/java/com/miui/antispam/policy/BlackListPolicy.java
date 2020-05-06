package com.miui.antispam.policy;

import android.content.Context;
import b.b.a.a;
import com.miui.activityutil.o;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.os.Build;
import miui.provider.ExtraTelephony;

public class BlackListPolicy extends a {
    public BlackListPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        if (!ExtraTelephony.isInBlacklist(this.mContext, eVar.f2369b, eVar.f2371d, eVar.f2370c)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 3);
    }

    public int getType() {
        return a.c.f1311a;
    }

    public a.C0035a handleData(e eVar) {
        if (Build.IS_INTERNATIONAL_BUILD && eVar.f2369b.startsWith(o.f2309a)) {
            eVar.f2369b = eVar.f2369b.substring(1);
        }
        if (!this.mJudge.b()) {
            return dbQuery(eVar);
        }
        if (!this.mJudge.a(eVar)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 3);
    }
}
