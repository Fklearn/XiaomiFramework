package com.miui.securitycenter.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import b.b.c.h.e;
import b.b.c.h.j;
import b.b.c.i.d;
import b.b.c.j.f;
import b.b.c.j.z;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securitycenter.utils.a;
import com.miui.securityscan.M;
import com.miui.securityscan.MainActivity;
import com.xiaomi.stat.MiStat;
import java.util.Calendar;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

public class SpecificDeviceSystemCheckService extends IntentService {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7525a = "SpecificDeviceSystemCheckService";

    /* renamed from: b  reason: collision with root package name */
    private static final String f7526b = "https://api.sec.miui.com/config/customizedPhone/chinaMoblie";

    /* renamed from: c  reason: collision with root package name */
    private static final String f7527c = "https://api.sec.intl.miui.com/config/customizedPhone/chinaMoblie";

    public SpecificDeviceSystemCheckService() {
        super(f7525a);
    }

    private void a() {
        if (f.b(this) && h.i()) {
            String a2 = e.a(this, Build.IS_INTERNATIONAL_BUILD ? f7527c : f7526b, (JSONObject) null, "5cdd8678-cddf-4269-ab73-48187445bba3", new j("securitycenter_sdsystemcheckservice"));
            if (!TextUtils.isEmpty(a2)) {
                a(a2);
            }
        }
    }

    private void a(int i) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(67108864);
        intent.putExtra("extra_auto_optimize", true);
        PendingIntent activity = PendingIntent.getActivity(this, 10002, intent, 1073741824);
        d.a((Context) this).a(20001, getResources().getQuantityString(R.plurals.notification_title_cmcc_app_check, i, new Object[]{Integer.valueOf(i)}), getString(R.string.notification_summary_cmcc_app_check), activity);
    }

    private void a(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has("error_code")) {
                int optInt = jSONObject.optInt(MiStat.Param.SCORE, -1);
                int optInt2 = jSONObject.optInt("days", -1);
                boolean optBoolean = jSONObject.optBoolean("used", true);
                if (optInt == -1) {
                    return;
                }
                if (optInt2 != -1) {
                    h.b(optInt);
                    h.a(optInt2);
                    h.a(optBoolean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        int b2;
        int i;
        if (!a.c()) {
            a.a();
            return;
        }
        if (h.f() == -1) {
            if (Calendar.getInstance().get(1) > 2014) {
                long currentTimeMillis = System.currentTimeMillis();
                h.d(currentTimeMillis);
                h.a(currentTimeMillis);
            } else {
                return;
            }
        }
        a();
        if (h.c() && z.a(h.a()) >= (b2 = h.b())) {
            long b3 = M.b(-1);
            if (b3 == -1) {
                i = z.a(h.f());
            } else {
                int a2 = z.a(b3);
                if (a2 >= b2) {
                    i = a2;
                } else {
                    return;
                }
            }
            h.a(System.currentTimeMillis());
            a(i);
        }
    }
}
