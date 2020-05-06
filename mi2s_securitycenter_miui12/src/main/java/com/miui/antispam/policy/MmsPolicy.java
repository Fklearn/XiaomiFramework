package com.miui.antispam.policy;

import android.content.Context;
import android.util.Log;
import b.b.a.e.n;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.yellowpage.YellowPagePhone;
import miui.yellowpage.YellowPageUtils;

public class MmsPolicy extends a {
    private static final String TAG = "MmsPolicy";

    public MmsPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        int a2 = com.miui.antispam.db.d.a(this.mContext, "mms_mode", eVar.f2370c, 2);
        if (a2 == 0) {
            Log.d(TAG, "Mms is blocked.");
            return new a.C0035a(this, true, 7);
        } else if (a2 == 2) {
            Log.d(TAG, "Mms is permitted.");
            return new a.C0035a(this, true, 0);
        } else if (n.e(this.mContext, eVar.f2369b)) {
            Log.d(TAG, "Mms addresser is a contact.");
            return new a.C0035a(this, true, 0);
        } else {
            YellowPagePhone phoneInfo = YellowPageUtils.getPhoneInfo(this.mContext, eVar.f2369b, false);
            if (phoneInfo == null || !phoneInfo.isYellowPage()) {
                Log.d(TAG, "Mms is blocked.");
                return new a.C0035a(this, true, 4);
            }
            Log.d(TAG, "Mms addresser is a known service provider.");
            return new a.C0035a(this, true, 0);
        }
    }
}
