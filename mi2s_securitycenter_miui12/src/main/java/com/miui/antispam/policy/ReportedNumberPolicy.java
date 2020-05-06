package com.miui.antispam.policy;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import b.b.a.e.c;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import miui.yellowpage.YellowPagePhone;
import miui.yellowpage.YellowPageUtils;

public class ReportedNumberPolicy extends a {
    private static final String TAG = "ReportedNumberPolicy";

    public ReportedNumberPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
        com.miui.antispam.db.d.f(context, 1);
        com.miui.antispam.db.d.e(context, 1);
        com.miui.antispam.db.d.g(context, 1);
        com.miui.antispam.db.d.f(context, 2);
        com.miui.antispam.db.d.e(context, 2);
        com.miui.antispam.db.d.g(context, 2);
    }

    private boolean checkMarkedNumberIntercept(Context context, int i, int i2, String str) {
        String str2;
        String str3 = (String) c.g.get(Integer.valueOf(i)).get(Integer.valueOf(i2));
        if (str3 == null) {
            str2 = "the mark type of cid is not found ... allow";
        } else {
            if (!(c.a(context, str3, 1) == 0)) {
                str2 = "the switch of " + str3 + " is not open ... allow";
            } else if (isRelatedNumber(context, str)) {
                str2 = "call number is a related number... allow";
            } else {
                Log.d(TAG, "should intercept this marked call!");
                return true;
            }
        }
        Log.d(TAG, str2);
        return false;
    }

    private boolean isRelatedNumber(Context context, String str) {
        Cursor query = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"type"}, "number = ? OR normalized_number = ? ", new String[]{str, str}, "date DESC");
        if (query != null) {
            do {
                try {
                    if (!query.moveToNext()) {
                        query.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Cursor exception in isRelatedNumber(): ", e);
                } catch (Throwable th) {
                    query.close();
                    throw th;
                }
            } while (query.getInt(0) != 2);
            query.close();
            return true;
        }
        return false;
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        if (!c.c(this.mContext, eVar.f2370c)) {
            return null;
        }
        boolean z = false;
        YellowPagePhone phoneInfo = YellowPageUtils.getPhoneInfo(this.mContext, eVar.f2369b, false);
        if (phoneInfo == null) {
            return new a.C0035a(this, false, -1);
        }
        if (eVar.h && com.miui.antispam.db.d.c(eVar.f2370c)) {
            z = true;
        }
        if (z || !checkMarkedNumberIntercept(this.mContext, eVar.f2370c, phoneInfo.getCid(), phoneInfo.getNumber())) {
            return null;
        }
        return new a.C0035a(this, true, c.a(phoneInfo.getCid()));
    }
}
