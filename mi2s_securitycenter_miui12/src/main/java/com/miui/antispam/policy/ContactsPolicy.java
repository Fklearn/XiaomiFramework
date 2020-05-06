package com.miui.antispam.policy;

import android.content.Context;
import b.b.a.e.n;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;

public class ContactsPolicy extends a {
    public ContactsPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        if (n.e(this.mContext, eVar.f2369b)) {
            return (eVar.f2371d == 2 && com.miui.antispam.db.d.a(this.mContext, "contact_call_mode", eVar.f2370c, 1) == 0) ? new a.C0035a(this, true, 9) : (eVar.f2371d == 1 && com.miui.antispam.db.d.a(this.mContext, "contact_sms_mode", eVar.f2370c, 1) == 0) ? new a.C0035a(this, true, 9) : new a.C0035a(this, true, 0);
        }
        return null;
    }
}
