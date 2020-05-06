package com.miui.securityscan.i;

import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FuncTopBannerScrollCnModel;
import com.miui.common.card.models.FuncTopBannerScrollGlobalModel;
import java.util.ArrayList;
import java.util.Locale;
import miui.os.Build;

public class e {
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:18:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<com.miui.common.card.models.BaseCardModel> a(android.content.Context r4) {
        /*
            android.content.Context r4 = r4.getApplicationContext()
            boolean r0 = com.miui.securitycenter.h.i(r4)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "isHomeListCacheDeleted "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "CardModelUtil"
            android.util.Log.d(r2, r1)
            r1 = 1
            java.lang.String r3 = "securityscan_homelist_cache"
            if (r0 != 0) goto L_0x0029
            com.miui.securityscan.i.h.a(r4, r3)
            com.miui.securitycenter.h.c(r4, r1)
        L_0x0029:
            r0 = 0
            java.lang.String r4 = com.miui.securityscan.i.h.b(r4, r3)     // Catch:{ JSONException -> 0x003e }
            boolean r3 = android.text.TextUtils.isEmpty(r4)     // Catch:{ JSONException -> 0x003e }
            if (r3 != 0) goto L_0x0044
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ JSONException -> 0x003e }
            r3.<init>(r4)     // Catch:{ JSONException -> 0x003e }
            com.miui.securityscan.cards.d r4 = com.miui.securityscan.cards.d.a((org.json.JSONObject) r3, (int) r1)     // Catch:{ JSONException -> 0x003e }
            goto L_0x0045
        L_0x003e:
            r4 = move-exception
            java.lang.String r1 = "create datamodel error:"
            android.util.Log.e(r2, r1, r4)
        L_0x0044:
            r4 = r0
        L_0x0045:
            if (r4 == 0) goto L_0x005b
            boolean r1 = r4.a()
            if (r1 != 0) goto L_0x0053
            boolean r1 = r4.b()
            if (r1 == 0) goto L_0x005b
        L_0x0053:
            java.util.ArrayList r4 = r4.f()
            java.util.ArrayList r0 = com.miui.securityscan.cards.d.a((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r4)
        L_0x005b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.i.e.a(android.content.Context):java.util.ArrayList");
    }

    public static void a(ArrayList<BaseCardModel> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            BaseCardModel baseCardModel = arrayList.get(i);
            if (baseCardModel instanceof FuncTopBannerScrollCnModel) {
                ((FuncTopBannerScrollCnModel) baseCardModel).setCurrentIndex(-1);
            }
            if (baseCardModel instanceof FuncTopBannerScrollGlobalModel) {
                ((FuncTopBannerScrollGlobalModel) baseCardModel).setCurrentIndex(-1);
            }
        }
    }

    public static boolean a(long j, ArrayList<BaseCardModel> arrayList) {
        if (arrayList == null || arrayList.isEmpty() || Math.abs(j) >= 10000) {
            return true;
        }
        BaseCardModel baseCardModel = arrayList.get(0);
        return !(Build.IS_INTERNATIONAL_BUILD && baseCardModel != null && baseCardModel.isOverseaChannel() && baseCardModel.getLanguage() != null && baseCardModel.getLanguage().equalsIgnoreCase(Locale.getDefault().toString())) && !(!Build.IS_INTERNATIONAL_BUILD && baseCardModel != null && !baseCardModel.isOverseaChannel() && baseCardModel.getLanguage() != null && baseCardModel.getLanguage().equalsIgnoreCase(Locale.getDefault().toString()) && "zh_CN".equalsIgnoreCase(Locale.getDefault().toString()));
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<com.miui.common.card.models.BaseCardModel> b(android.content.Context r3) {
        /*
            android.content.Context r3 = r3.getApplicationContext()
            r0 = 0
            java.lang.String r1 = "phonemanage_data_cache"
            java.lang.String r3 = com.miui.securityscan.i.h.b(r3, r1)     // Catch:{ Exception -> 0x001c }
            boolean r1 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x001c }
            if (r1 != 0) goto L_0x0024
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x001c }
            r1.<init>(r3)     // Catch:{ Exception -> 0x001c }
            r3 = 5
            com.miui.securityscan.cards.d r3 = com.miui.securityscan.cards.d.a((org.json.JSONObject) r1, (int) r3)     // Catch:{ Exception -> 0x001c }
            goto L_0x0025
        L_0x001c:
            r3 = move-exception
            java.lang.String r1 = "CardModelUtil"
            java.lang.String r2 = "filter function models error"
            android.util.Log.e(r1, r2, r3)
        L_0x0024:
            r3 = r0
        L_0x0025:
            if (r3 == 0) goto L_0x003b
            boolean r1 = r3.a()
            if (r1 != 0) goto L_0x0033
            boolean r1 = r3.b()
            if (r1 == 0) goto L_0x003b
        L_0x0033:
            java.util.ArrayList r3 = r3.f()
            java.util.ArrayList r0 = com.miui.securityscan.cards.d.a((java.util.ArrayList<com.miui.common.card.models.BaseCardModel>) r3)
        L_0x003b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.i.e.b(android.content.Context):java.util.List");
    }
}
