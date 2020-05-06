package com.miui.antispam.policy;

import android.content.Context;
import b.b.a.a;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.provider.ExtraTelephony;

public class BlackPrefixPolicy extends a {
    public BlackPrefixPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        if (!ExtraTelephony.isPrefixInBlack(this.mContext, eVar.f2369b, eVar.f2371d, eVar.f2370c) && !ExtraTelephony.isPrefixInBlack(this.mContext, eVar.f2368a, eVar.f2371d, eVar.f2370c)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 6);
    }

    public int getType() {
        return a.c.f1311a;
    }

    public a.C0035a handleData(e eVar) {
        if (!this.mJudge.b()) {
            return dbQuery(eVar);
        }
        if (!this.mJudge.c(eVar.f2369b, eVar.f2371d, a.c.f1311a, eVar.f2370c) && !this.mJudge.c(eVar.f2368a, eVar.f2371d, a.c.f1311a, eVar.f2370c)) {
            return null;
        }
        int i = eVar.f2371d;
        return new a.C0035a(this, true, 6);
    }
}
