package com.miui.securityscan.i;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.d;
import com.miui.analytics.AnalyticsUtil;
import com.miui.permcenter.n;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import java.util.Locale;

public class l {
    public static Intent a(Context context, String str, String str2, String str3, String str4) {
        try {
            String string = context.getString(R.string.gdpr_msg, new Object[]{Locale.getDefault().getLanguage(), Locale.getDefault().getCountry()});
            String str5 = (String) d.a("PrivacyUtils", Class.forName("android.provider.MiuiSettings$Privacy"), "ACTION_PRIVACY_AUTHORIZATION_DIALOG", String.class);
            Log.d("PrivacyUtils", "action = " + str5);
            if (TextUtils.isEmpty(str5)) {
                str5 = "miui.intent.action.PRIVACY_AUTHORIZATION_DIALOG";
            }
            Intent intent = new Intent(str5);
            intent.putExtra("key", "com.miui.securitycenter");
            intent.putExtra("msg", string);
            intent.putExtra("negButton", str3);
            intent.putExtra("language", Locale.getDefault().toString());
            return intent;
        } catch (Exception e) {
            Log.e("PrivacyUtils", "getPrivacyAuthorizationDialogIntent error ", e);
            return null;
        }
    }

    public static String a() {
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        return "https://privacy.mi.com/security/" + language + "_" + country;
    }

    public static void a(Context context, boolean z) {
        h.b(z);
        n.d(context.getApplicationContext());
        AnalyticsUtil.setDataUploadingEnabled(z);
    }
}
