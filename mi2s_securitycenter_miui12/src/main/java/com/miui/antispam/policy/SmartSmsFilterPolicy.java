package com.miui.antispam.policy;

import android.content.Context;
import android.text.TextUtils;
import com.miui.antispam.policy.a;
import com.miui.antispam.policy.a.b;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;
import com.miui.securitycenter.R;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.os.Build;
import miui.provider.ExtraTelephony;

public class SmartSmsFilterPolicy extends a {
    private static Pattern CONTACT_INFO_PTN = Pattern.compile("\\[(.+?)\\]((.|\n)*?);");
    private b mProxy;

    public SmartSmsFilterPolicy(Context context, a.b bVar, d dVar, g gVar) {
        super(context, bVar, dVar, gVar);
        this.mProxy = new b(context);
        initSmsEngine(false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x004f A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0038 A[Catch:{ Exception -> 0x0055, all -> 0x0053 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isInSmsWhiteList(android.content.Context r8, java.lang.String r9) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r9)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            android.content.ContentResolver r2 = r8.getContentResolver()
            android.net.Uri r3 = miui.yellowpage.YellowPageContract.AntispamWhiteList.CONTNET_URI
            java.lang.String r8 = "number"
            java.lang.String[] r4 = new java.lang.String[]{r8}
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "number LIKE '"
            r8.append(r0)
            r8.append(r9)
            java.lang.String r0 = "%'"
            r8.append(r0)
            java.lang.String r5 = r8.toString()
            r6 = 0
            r7 = 0
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)
            if (r8 == 0) goto L_0x005e
        L_0x0032:
            boolean r0 = r8.moveToNext()     // Catch:{ Exception -> 0x0055 }
            if (r0 == 0) goto L_0x004f
            java.lang.String r0 = r8.getString(r1)     // Catch:{ Exception -> 0x0055 }
            boolean r0 = android.text.TextUtils.equals(r9, r0)     // Catch:{ Exception -> 0x0055 }
            if (r0 != 0) goto L_0x004a
            java.lang.String r0 = "106"
            boolean r0 = r9.startsWith(r0)     // Catch:{ Exception -> 0x0055 }
            if (r0 == 0) goto L_0x0032
        L_0x004a:
            r9 = 1
            r8.close()
            return r9
        L_0x004f:
            r8.close()
            goto L_0x005e
        L_0x0053:
            r9 = move-exception
            goto L_0x005a
        L_0x0055:
            r9 = move-exception
            r9.printStackTrace()     // Catch:{ all -> 0x0053 }
            goto L_0x004f
        L_0x005a:
            r8.close()
            throw r9
        L_0x005e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.policy.SmartSmsFilterPolicy.isInSmsWhiteList(android.content.Context, java.lang.String):boolean");
    }

    private static boolean isMiuiPattern(Context context, String str) {
        HashMap hashMap = new HashMap();
        String[] stringArray = context.getResources().getStringArray(R.array.contact_infotype_entries);
        for (String put : stringArray) {
            hashMap.put(put, 1);
        }
        Matcher matcher = CONTACT_INFO_PTN.matcher(str);
        int i = 0;
        int i2 = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (!hashMap.containsKey(matcher.group(1).trim()) || !(start == i + 1 || i2 == 0)) {
                i2 = 0;
            } else {
                i2++;
                if (i2 == 2) {
                    return true;
                }
            }
            i = end;
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
        int shouldFilter;
        if (Build.IS_INTERNATIONAL_BUILD) {
            return null;
        }
        if (((!ExtraTelephony.isServiceNumber(eVar.f2369b) || com.miui.antispam.db.d.a(this.mContext, "service_sms_mode", eVar.f2370c, 1) != 1) && (ExtraTelephony.isServiceNumber(eVar.f2369b) || com.miui.antispam.db.d.a(this.mContext, "stranger_sms_mode", eVar.f2370c, 1) != 1)) || (shouldFilter = shouldFilter(this.mContext, eVar.f2369b, eVar.e)) == 0) {
            return null;
        }
        return new a.C0035a(this, true, shouldFilter == 1 ? 8 : 4);
    }

    public void initSmsEngine(boolean z) {
        this.mProxy.a(z);
    }

    public int shouldFilter(Context context, String str, String str2) {
        if (!TextUtils.isEmpty(str2) && !isMiuiPattern(context, str2) && !isInSmsWhiteList(context, str)) {
            return this.mProxy.a(str, str2);
        }
        return 0;
    }
}
