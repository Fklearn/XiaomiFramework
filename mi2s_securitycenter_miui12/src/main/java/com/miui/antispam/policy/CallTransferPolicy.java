package com.miui.antispam.policy;

import android.content.Context;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;

public class CallTransferPolicy extends a {
    public CallTransferPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        if (com.miui.antispam.db.d.b(eVar.f2370c)) {
            return eVar.f ? new a.C0035a(this, true, 15) : new a.C0035a(this, false, -1);
        }
        return null;
    }
}
