package com.miui.antispam.policy;

import android.content.Context;
import b.b.a.a;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.provider.ExtraTelephony;

public class WhiteListPolicy extends a {
    public WhiteListPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        if (ExtraTelephony.isInWhiteList(this.mContext, eVar.f2368a, eVar.f2371d, eVar.f2370c)) {
            return new a.C0035a(this, true, 0);
        }
        return null;
    }

    public int getType() {
        return a.c.f1312b;
    }

    public a.C0035a handleData(e eVar) {
        if (!this.mJudge.b()) {
            return dbQuery(eVar);
        }
        if (this.mJudge.f(eVar)) {
            return new a.C0035a(this, true, 0);
        }
        return null;
    }
}
