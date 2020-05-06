package com.miui.antispam.policy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import b.b.a.e.n;
import b.b.c.j.v;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;
import miui.os.Build;
import miui.telephony.PhoneNumberUtils;

public class OverseaPolicy extends a {
    private static final String KEY_CALL_FIRST_OVERSEA = "key_call_first_oversea";

    public OverseaPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
    }

    public a.C0035a dbQuery(e eVar) {
        return null;
    }

    public int getType() {
        return 0;
    }

    public a.C0035a handleData(e eVar) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return null;
        }
        if (com.miui.antispam.db.d.a(this.mContext, "oversea_call_mode", eVar.f2370c, 1) == 0) {
            String countryCode = PhoneNumberUtils.PhoneNumber.parse(eVar.f2368a).getCountryCode();
            if (TextUtils.isEmpty(countryCode) || countryCode.contains("86") || countryCode.contains("852") || countryCode.contains("853") || countryCode.contains("886")) {
                return null;
            }
            return new a.C0035a(this, true, 17);
        } else if (b.a(KEY_CALL_FIRST_OVERSEA, false)) {
            return null;
        } else {
            String countryCode2 = PhoneNumberUtils.PhoneNumber.parse(eVar.f2368a).getCountryCode();
            if (TextUtils.isEmpty(countryCode2) || countryCode2.contains("86") || countryCode2.contains("852") || countryCode2.contains("853") || countryCode2.contains("886")) {
                return null;
            }
            NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
            Intent intent = new Intent("miui.intent.action.CALL_FIREWALL");
            intent.addFlags(67108864);
            intent.putExtra("is_from_intercept_notification", true);
            Resources resources = this.mContext.getResources();
            v.a(notificationManager, "com.miui.antispam", n.f1454a, 2);
            Notification build = v.a(this.mContext, "com.miui.antispam").setTicker(this.mContext.getString(R.string.fw_blocked)).setWhen(System.currentTimeMillis()).setContentTitle(this.mContext.getString(R.string.fw_oversea)).setContentText(this.mContext.getString(R.string.fw_oversea_content)).setContentIntent(PendingIntent.getActivity(this.mContext, 0, intent, 0)).setSmallIcon(R.drawable.antispam_small).setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_anti_spam)).build();
            build.flags |= 16;
            b.b.o.a.a.a(build, true);
            notificationManager.notify(796, build);
            b.b(KEY_CALL_FIRST_OVERSEA, true);
            b.b.a.a.a.a("oversea_function_guide", "oversea_intercept", "show");
            return null;
        }
    }
}
