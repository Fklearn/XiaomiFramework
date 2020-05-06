package com.miui.antispam.policy;

import android.content.Context;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;

public class EmptyNumberPolicy extends a {
    public EmptyNumberPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        if (!"-1".equals(eVar.f2368a)) {
            return null;
        }
        if (com.miui.antispam.db.d.a(this.mContext, "empty_call_mode", eVar.f2370c, 1) == 0) {
            return new a.C0035a(this, true, 4);
        }
        a.C0035a handleData = this.mPc.a(g.CALL_TRANSFER_POLICY).handleData(eVar);
        return (handleData == null || !handleData.f2353a) ? new a.C0035a(this, true, 0) : new a.C0035a(this, true, 15);
    }
}
