package com.miui.securityscan.cards;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import b.b.c.h.j;
import b.b.c.j.x;
import b.b.j.b.b;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.activityutil.o;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.common.card.models.ActivityCardModel;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.AdvListTitleCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FuncGridBaseCardModel;
import com.miui.common.card.models.FuncTopBannerScrollCnModel;
import com.miui.common.card.models.FuncTopBannerScrollGlobalModel;
import com.miui.common.card.models.FunctionCardModel;
import com.miui.common.card.models.LineCardModel;
import com.miui.common.card.models.NewsCardModel;
import com.miui.common.card.models.PlaceHolderCardModel;
import com.miui.common.card.models.TitleCardModel;
import com.miui.luckymoney.config.AppConstants;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.Application;
import com.miui.securityscan.M;
import com.miui.securityscan.i.k;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import miui.os.Build;
import org.json.JSONObject;

public class d implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static Object f7643a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private ArrayList<BaseCardModel> f7644b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    private String f7645c;

    /* renamed from: d  reason: collision with root package name */
    private String f7646d;
    private String e;
    private String f;
    private boolean g;
    private boolean h;
    private String i;
    private boolean j;
    private String k = "";
    private boolean l;
    private int m;
    private int n;

    public static int a(int i2, int i3) {
        if (i3 == 0) {
            return 0;
        }
        return i2 % i3 == 0 ? i2 / i3 : (i2 / i3) + 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:125:0x0164 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x01d7 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01d6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.securityscan.cards.d a(org.json.JSONObject r16, int r17) {
        /*
            r0 = r16
            java.lang.Object r1 = f7643a
            monitor-enter(r1)
            com.miui.securityscan.cards.d r9 = new com.miui.securityscan.cards.d     // Catch:{ all -> 0x0272 }
            r9.<init>()     // Catch:{ all -> 0x0272 }
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r2 = r9.f7644b     // Catch:{ all -> 0x0272 }
            r2.clear()     // Catch:{ all -> 0x0272 }
            r10 = 0
            r9.n = r10     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "isOverseaChannel"
            boolean r2 = r0.optBoolean(r2)     // Catch:{ all -> 0x0272 }
            r9.j = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "lang"
            java.lang.String r3 = ""
            java.lang.String r2 = r0.optString(r2, r3)     // Catch:{ all -> 0x0272 }
            r9.k = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "channel"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ all -> 0x0272 }
            r9.f7645c = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "dataVersion"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ all -> 0x0272 }
            r9.f7646d = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "layoutId"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ all -> 0x0272 }
            r9.e = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "tn"
            java.lang.String r2 = r0.optString(r2)     // Catch:{ all -> 0x0272 }
            r9.f = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "status"
            int r2 = r0.optInt(r2)     // Catch:{ all -> 0x0272 }
            r9.m = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "forceRefresh"
            boolean r2 = r0.optBoolean(r2)     // Catch:{ all -> 0x0272 }
            r9.g = r2     // Catch:{ all -> 0x0272 }
            java.lang.String r2 = "extraData"
            org.json.JSONObject r2 = r0.optJSONObject(r2)     // Catch:{ all -> 0x0272 }
            if (r2 == 0) goto L_0x0074
            java.lang.String r3 = "screenInsurance"
            org.json.JSONObject r2 = r2.optJSONObject(r3)     // Catch:{ all -> 0x0272 }
            if (r2 == 0) goto L_0x0074
            java.lang.String r3 = "buy"
            boolean r3 = r2.optBoolean(r3)     // Catch:{ all -> 0x0272 }
            r9.h = r3     // Catch:{ all -> 0x0272 }
            java.lang.String r3 = "url"
            java.lang.String r2 = r2.optString(r3)     // Catch:{ all -> 0x0272 }
            r9.i = r2     // Catch:{ all -> 0x0272 }
        L_0x0074:
            java.lang.String r2 = "data"
            org.json.JSONArray r0 = r0.optJSONArray(r2)     // Catch:{ all -> 0x0272 }
            r11 = 1
            if (r0 == 0) goto L_0x0086
            int r2 = r0.length()     // Catch:{ all -> 0x0272 }
            if (r2 != 0) goto L_0x0086
            r9.l = r11     // Catch:{ all -> 0x0272 }
            goto L_0x0088
        L_0x0086:
            r9.l = r10     // Catch:{ all -> 0x0272 }
        L_0x0088:
            if (r0 == 0) goto L_0x00a2
            r12 = r10
        L_0x008b:
            int r2 = r0.length()     // Catch:{ all -> 0x0272 }
            if (r12 >= r2) goto L_0x00a2
            org.json.JSONObject r7 = r0.getJSONObject(r12)     // Catch:{ all -> 0x0272 }
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r2 = r9
            r8 = r17
            a(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0272 }
            int r12 = r12 + 1
            goto L_0x008b
        L_0x00a2:
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0272 }
            r0.<init>()     // Catch:{ all -> 0x0272 }
            r2 = r10
        L_0x00a8:
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r3 = r9.f7644b     // Catch:{ all -> 0x0272 }
            int r3 = r3.size()     // Catch:{ all -> 0x0272 }
            if (r2 >= r3) goto L_0x022a
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r3 = r9.f7644b     // Catch:{ all -> 0x0272 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x0272 }
            com.miui.common.card.models.BaseCardModel r3 = (com.miui.common.card.models.BaseCardModel) r3     // Catch:{ all -> 0x0272 }
            boolean r4 = r3 instanceof com.miui.common.card.models.TitleCardModel     // Catch:{ all -> 0x0272 }
            if (r4 == 0) goto L_0x0225
            com.miui.common.card.models.TitleCardModel r3 = (com.miui.common.card.models.TitleCardModel) r3     // Catch:{ all -> 0x0272 }
            java.util.List<com.miui.common.card.GridFunctionData> r4 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            if (r4 == 0) goto L_0x01ef
            java.util.List<com.miui.common.card.GridFunctionData> r4 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x0272 }
            if (r4 != 0) goto L_0x01ef
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x0272 }
            r4.<init>()     // Catch:{ all -> 0x0272 }
            java.util.List<com.miui.common.card.GridFunctionData> r5 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            int r5 = r5.size()     // Catch:{ all -> 0x0272 }
            int r6 = r3.getSubCardModelTemplate()     // Catch:{ all -> 0x0272 }
            r7 = 4
            switch(r6) {
                case 1401: goto L_0x01ac;
                case 1402: goto L_0x017b;
                case 1403: goto L_0x013d;
                case 1404: goto L_0x00df;
                default: goto L_0x00dd;
            }     // Catch:{ all -> 0x0272 }
        L_0x00dd:
            goto L_0x0225
        L_0x00df:
            b.b.j.b.d r5 = new b.b.j.b.d     // Catch:{ all -> 0x0272 }
            r5.<init>()     // Catch:{ all -> 0x0272 }
            r5.setTopRow(r11)     // Catch:{ all -> 0x0272 }
            r5.setBottomRow(r11)     // Catch:{ all -> 0x0272 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x0272 }
            r6.<init>()     // Catch:{ all -> 0x0272 }
            java.util.Random r8 = new java.util.Random     // Catch:{ all -> 0x0272 }
            r8.<init>()     // Catch:{ all -> 0x0272 }
            java.util.List<com.miui.common.card.GridFunctionData> r12 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            int r12 = r12.size()     // Catch:{ all -> 0x0272 }
            if (r12 <= r7) goto L_0x0126
            java.util.HashSet r13 = new java.util.HashSet     // Catch:{ all -> 0x0272 }
            r13.<init>()     // Catch:{ all -> 0x0272 }
            r14 = r10
        L_0x0102:
            if (r14 >= r7) goto L_0x012b
            int r15 = r8.nextInt(r12)     // Catch:{ all -> 0x0272 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x0272 }
            boolean r10 = r13.contains(r10)     // Catch:{ all -> 0x0272 }
            if (r10 != 0) goto L_0x0124
            java.lang.Integer r10 = java.lang.Integer.valueOf(r15)     // Catch:{ all -> 0x0272 }
            r13.add(r10)     // Catch:{ all -> 0x0272 }
            int r14 = r14 + 1
            java.util.List<com.miui.common.card.GridFunctionData> r10 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            java.lang.Object r10 = r10.get(r15)     // Catch:{ all -> 0x0272 }
            r6.add(r10)     // Catch:{ all -> 0x0272 }
        L_0x0124:
            r10 = 0
            goto L_0x0102
        L_0x0126:
            java.util.List<com.miui.common.card.GridFunctionData> r7 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            r6.addAll(r7)     // Catch:{ all -> 0x0272 }
        L_0x012b:
            r5.setGridFunctionDataList(r6)     // Catch:{ all -> 0x0272 }
            r4.add(r5)     // Catch:{ all -> 0x0272 }
            r3.addSubCardModelList((com.miui.common.card.models.BaseCardModel) r5)     // Catch:{ all -> 0x0272 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0272 }
        L_0x0138:
            r0.put(r3, r4)     // Catch:{ all -> 0x0272 }
            goto L_0x0225
        L_0x013d:
            int r6 = a((int) r5, (int) r7)     // Catch:{ all -> 0x0272 }
            r7 = 0
        L_0x0142:
            if (r7 >= r6) goto L_0x0176
            b.b.j.b.h r8 = new b.b.j.b.h     // Catch:{ all -> 0x0272 }
            r8.<init>()     // Catch:{ all -> 0x0272 }
            if (r6 != r11) goto L_0x0152
            r8.setTopRow(r11)     // Catch:{ all -> 0x0272 }
        L_0x014e:
            r8.setBottomRow(r11)     // Catch:{ all -> 0x0272 }
            goto L_0x015d
        L_0x0152:
            if (r7 != 0) goto L_0x0158
            r8.setTopRow(r11)     // Catch:{ all -> 0x0272 }
            goto L_0x015d
        L_0x0158:
            int r10 = r6 + -1
            if (r7 != r10) goto L_0x015d
            goto L_0x014e
        L_0x015d:
            int r10 = r7 * 4
            int r12 = r10 + 4
            if (r12 <= r5) goto L_0x0164
            r12 = r5
        L_0x0164:
            java.util.List<com.miui.common.card.GridFunctionData> r13 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            java.util.List r10 = r13.subList(r10, r12)     // Catch:{ all -> 0x0272 }
            r8.setGridFunctionDataList(r10)     // Catch:{ all -> 0x0272 }
            r4.add(r8)     // Catch:{ all -> 0x0272 }
            r3.addSubCardModelList((com.miui.common.card.models.BaseCardModel) r8)     // Catch:{ all -> 0x0272 }
            int r7 = r7 + 1
            goto L_0x0142
        L_0x0176:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0272 }
            goto L_0x0138
        L_0x017b:
            r6 = 2
            int r6 = a((int) r5, (int) r6)     // Catch:{ all -> 0x0272 }
            r7 = 0
        L_0x0181:
            if (r7 >= r6) goto L_0x01a7
            com.miui.common.card.models.FuncGrid6CardModel r8 = new com.miui.common.card.models.FuncGrid6CardModel     // Catch:{ all -> 0x0272 }
            r8.<init>()     // Catch:{ all -> 0x0272 }
            r8.setCurrentRowIndex(r7)     // Catch:{ all -> 0x0272 }
            r8.setHomePageFunc(r11)     // Catch:{ all -> 0x0272 }
            int r10 = r7 * 2
            int r12 = r10 + 2
            if (r12 <= r5) goto L_0x0195
            r12 = r5
        L_0x0195:
            java.util.List<com.miui.common.card.GridFunctionData> r13 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            java.util.List r10 = r13.subList(r10, r12)     // Catch:{ all -> 0x0272 }
            r8.setGridFunctionDataList(r10)     // Catch:{ all -> 0x0272 }
            r4.add(r8)     // Catch:{ all -> 0x0272 }
            r3.addSubCardModelList((com.miui.common.card.models.BaseCardModel) r8)     // Catch:{ all -> 0x0272 }
            int r7 = r7 + 1
            goto L_0x0181
        L_0x01a7:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0272 }
            goto L_0x0138
        L_0x01ac:
            r6 = 3
            int r6 = a((int) r5, (int) r6)     // Catch:{ all -> 0x0272 }
            r7 = 0
        L_0x01b2:
            if (r7 >= r6) goto L_0x01e9
            com.miui.common.card.models.FuncGrid9ColorfulCardModel r8 = new com.miui.common.card.models.FuncGrid9ColorfulCardModel     // Catch:{ all -> 0x0272 }
            r8.<init>()     // Catch:{ all -> 0x0272 }
            r8.setHomePageFunc(r11)     // Catch:{ all -> 0x0272 }
            if (r6 != r11) goto L_0x01c5
            r8.setTopRow(r11)     // Catch:{ all -> 0x0272 }
        L_0x01c1:
            r8.setBottomRow(r11)     // Catch:{ all -> 0x0272 }
            goto L_0x01d0
        L_0x01c5:
            if (r7 != 0) goto L_0x01cb
            r8.setTopRow(r11)     // Catch:{ all -> 0x0272 }
            goto L_0x01d0
        L_0x01cb:
            int r10 = r6 + -1
            if (r7 != r10) goto L_0x01d0
            goto L_0x01c1
        L_0x01d0:
            int r10 = r7 * 3
            int r12 = r10 + 3
            if (r12 <= r5) goto L_0x01d7
            r12 = r5
        L_0x01d7:
            java.util.List<com.miui.common.card.GridFunctionData> r13 = r3.gridFunctionDataList     // Catch:{ all -> 0x0272 }
            java.util.List r10 = r13.subList(r10, r12)     // Catch:{ all -> 0x0272 }
            r8.setGridFunctionDataList(r10)     // Catch:{ all -> 0x0272 }
            r4.add(r8)     // Catch:{ all -> 0x0272 }
            r3.addSubCardModelList((com.miui.common.card.models.BaseCardModel) r8)     // Catch:{ all -> 0x0272 }
            int r7 = r7 + 1
            goto L_0x01b2
        L_0x01e9:
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0272 }
            goto L_0x0138
        L_0x01ef:
            int r4 = r3.getSubCardModelTemplate()     // Catch:{ all -> 0x0272 }
            r5 = 7
            if (r4 == r5) goto L_0x01f7
            goto L_0x0225
        L_0x01f7:
            java.util.List r3 = r3.getSubCardModelList()     // Catch:{ all -> 0x0272 }
            int r4 = r3.size()     // Catch:{ all -> 0x0272 }
            r5 = 0
        L_0x0200:
            if (r5 >= r4) goto L_0x0225
            java.lang.Object r6 = r3.get(r5)     // Catch:{ all -> 0x0272 }
            com.miui.common.card.models.BaseCardModel r6 = (com.miui.common.card.models.BaseCardModel) r6     // Catch:{ all -> 0x0272 }
            boolean r7 = r6 instanceof com.miui.common.card.models.NewsCardModel     // Catch:{ all -> 0x0272 }
            if (r7 == 0) goto L_0x0222
            com.miui.common.card.models.NewsCardModel r6 = (com.miui.common.card.models.NewsCardModel) r6     // Catch:{ all -> 0x0272 }
            if (r4 != r11) goto L_0x0217
            r6.setTopRow(r11)     // Catch:{ all -> 0x0272 }
        L_0x0213:
            r6.setBottomRow(r11)     // Catch:{ all -> 0x0272 }
            goto L_0x0222
        L_0x0217:
            if (r5 != 0) goto L_0x021d
            r6.setTopRow(r11)     // Catch:{ all -> 0x0272 }
            goto L_0x0222
        L_0x021d:
            int r7 = r4 + -1
            if (r5 != r7) goto L_0x0222
            goto L_0x0213
        L_0x0222:
            int r5 = r5 + 1
            goto L_0x0200
        L_0x0225:
            int r2 = r2 + 1
            r10 = 0
            goto L_0x00a8
        L_0x022a:
            boolean r2 = r0.isEmpty()     // Catch:{ all -> 0x0272 }
            if (r2 != 0) goto L_0x0270
            java.util.Set r2 = r0.keySet()     // Catch:{ all -> 0x0272 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0272 }
            java.util.ArrayList r3 = new java.util.ArrayList     // Catch:{ all -> 0x0272 }
            r3.<init>()     // Catch:{ all -> 0x0272 }
        L_0x023d:
            boolean r4 = r2.hasNext()     // Catch:{ all -> 0x0272 }
            if (r4 == 0) goto L_0x024d
            java.lang.Object r4 = r2.next()     // Catch:{ all -> 0x0272 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0272 }
            r3.add(r4)     // Catch:{ all -> 0x0272 }
            goto L_0x023d
        L_0x024d:
            java.util.Collections.sort(r3)     // Catch:{ all -> 0x0272 }
            int r2 = r3.size()     // Catch:{ all -> 0x0272 }
            int r2 = r2 - r11
        L_0x0255:
            if (r2 < 0) goto L_0x0270
            java.lang.Object r4 = r3.get(r2)     // Catch:{ all -> 0x0272 }
            java.lang.Integer r4 = (java.lang.Integer) r4     // Catch:{ all -> 0x0272 }
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r5 = r9.f7644b     // Catch:{ all -> 0x0272 }
            int r6 = r4.intValue()     // Catch:{ all -> 0x0272 }
            int r6 = r6 + r11
            java.lang.Object r4 = r0.get(r4)     // Catch:{ all -> 0x0272 }
            java.util.Collection r4 = (java.util.Collection) r4     // Catch:{ all -> 0x0272 }
            r5.addAll(r6, r4)     // Catch:{ all -> 0x0272 }
            int r2 = r2 + -1
            goto L_0x0255
        L_0x0270:
            monitor-exit(r1)     // Catch:{ all -> 0x0272 }
            return r9
        L_0x0272:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0272 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.cards.d.a(org.json.JSONObject, int):com.miui.securityscan.cards.d");
    }

    public static ArrayList<BaseCardModel> a(ArrayList<BaseCardModel> arrayList) {
        List<FuncTopBannerScrollData> funcTopBannerScrollDataList;
        List<FuncTopBannerScrollData> funcTopBannerScrollDataList2;
        List<FuncTopBannerScrollData> funcTopBannerScrollDataList3;
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        ArrayList arrayList2 = new ArrayList();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            BaseCardModel baseCardModel = arrayList.get(size);
            if ((baseCardModel instanceof AdvCardModel) || (baseCardModel instanceof AdvListTitleCardModel) || (baseCardModel instanceof ActivityCardModel) || (baseCardModel instanceof NewsCardModel)) {
                arrayList2.add(baseCardModel);
            }
        }
        arrayList.removeAll(arrayList2);
        ArrayList arrayList3 = new ArrayList();
        for (int size2 = arrayList.size() - 1; size2 >= 0; size2--) {
            BaseCardModel baseCardModel2 = arrayList.get(size2);
            if ((baseCardModel2 instanceof FuncTopBannerScrollCnModel) && ((funcTopBannerScrollDataList3 = ((FuncTopBannerScrollCnModel) baseCardModel2).getFuncTopBannerScrollDataList()) == null || funcTopBannerScrollDataList3.isEmpty())) {
                arrayList3.add(baseCardModel2);
            }
            if ((baseCardModel2 instanceof FuncTopBannerScrollGlobalModel) && ((funcTopBannerScrollDataList2 = ((FuncTopBannerScrollGlobalModel) baseCardModel2).getFuncTopBannerScrollDataList()) == null || funcTopBannerScrollDataList2.isEmpty())) {
                arrayList3.add(baseCardModel2);
            }
            if ((baseCardModel2 instanceof b) && ((funcTopBannerScrollDataList = ((b) baseCardModel2).getFuncTopBannerScrollDataList()) == null || funcTopBannerScrollDataList.isEmpty())) {
                arrayList3.add(baseCardModel2);
            }
        }
        arrayList.removeAll(arrayList3);
        ArrayList arrayList4 = new ArrayList();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            if (i2 != arrayList.size() - 1) {
                if (arrayList.get(i2) instanceof TitleCardModel) {
                    if (!(arrayList.get(i2 + 1) instanceof LineCardModel)) {
                    }
                }
            } else if (!(arrayList.get(i2) instanceof TitleCardModel)) {
            }
            arrayList4.add(arrayList.get(i2));
        }
        arrayList.removeAll(arrayList4);
        ArrayList arrayList5 = new ArrayList();
        for (int size3 = arrayList.size() - 1; size3 > 0; size3--) {
            if ((arrayList.get(size3) instanceof LineCardModel) && (arrayList.get(size3 - 1) instanceof LineCardModel)) {
                arrayList5.add(arrayList.get(size3));
            }
        }
        if (!arrayList.isEmpty() && (arrayList.get(0) instanceof LineCardModel)) {
            arrayList5.add(arrayList.get(0));
        }
        arrayList.removeAll(arrayList5);
        int size4 = arrayList.size();
        if (size4 <= 0) {
            return arrayList;
        }
        int i3 = size4 - 1;
        if (!(arrayList.get(i3) instanceof LineCardModel)) {
            return arrayList;
        }
        arrayList.remove(i3);
        return arrayList;
    }

    private static void a(d dVar) {
        dVar.f7644b.add(new PlaceHolderCardModel());
    }

    private static void a(d dVar, TitleCardModel titleCardModel, FuncTopBannerScrollCnModel funcTopBannerScrollCnModel, FuncTopBannerScrollGlobalModel funcTopBannerScrollGlobalModel, b bVar, JSONObject jSONObject, int i2) {
        String optString = jSONObject.optString("rowType");
        if (CloudPushConstants.XML_ITEM.equals(optString)) {
            b(dVar, titleCardModel, funcTopBannerScrollCnModel, funcTopBannerScrollGlobalModel, bVar, jSONObject, i2);
        } else if ("card".equals(optString)) {
            a(dVar, jSONObject, i2);
        }
    }

    private static void a(d dVar, JSONObject jSONObject, int i2) {
        String string = jSONObject.getString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        if ("placeholder".equals(string)) {
            a(dVar);
        } else {
            a(dVar, jSONObject, i2, string);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0039, code lost:
        r8 = r17;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:47:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(com.miui.securityscan.cards.d r15, org.json.JSONObject r16, int r17, java.lang.String r18) {
        /*
            r7 = r15
            r0 = r16
            java.lang.String r1 = "template"
            int r1 = r0.getInt(r1)
            r2 = 4
            r3 = 0
            r4 = 1
            if (r1 == r4) goto L_0x005c
            if (r1 == r2) goto L_0x0054
            r5 = 9
            if (r1 == r5) goto L_0x0048
            r5 = 10
            if (r1 == r5) goto L_0x003c
            switch(r1) {
                case 1403: goto L_0x0034;
                case 1404: goto L_0x002e;
                case 1405: goto L_0x0022;
                default: goto L_0x001b;
            }
        L_0x001b:
            r8 = r17
            r9 = r3
            r10 = r9
            r11 = r10
            r12 = r11
            goto L_0x0067
        L_0x0022:
            b.b.j.b.b r5 = new b.b.j.b.b
            r5.<init>()
            r8 = r17
            r9 = r3
            r10 = r9
            r11 = r10
            r12 = r5
            goto L_0x0067
        L_0x002e:
            b.b.j.b.f r5 = new b.b.j.b.f
            r5.<init>()
            goto L_0x0039
        L_0x0034:
            b.b.j.b.c r5 = new b.b.j.b.c
            r5.<init>()
        L_0x0039:
            r8 = r17
            goto L_0x0063
        L_0x003c:
            com.miui.common.card.models.FuncTopBannerScrollGlobalModel r5 = new com.miui.common.card.models.FuncTopBannerScrollGlobalModel
            r5.<init>()
            r8 = r17
            r9 = r3
            r10 = r9
            r12 = r10
            r11 = r5
            goto L_0x0067
        L_0x0048:
            com.miui.common.card.models.FuncTopBannerScrollCnModel r5 = new com.miui.common.card.models.FuncTopBannerScrollCnModel
            r5.<init>()
            r8 = r17
            r9 = r3
            r11 = r9
            r12 = r11
            r10 = r5
            goto L_0x0067
        L_0x0054:
            com.miui.common.card.models.AdvListTitleCardModel r5 = new com.miui.common.card.models.AdvListTitleCardModel
            r8 = r17
            r5.<init>(r8)
            goto L_0x0063
        L_0x005c:
            r8 = r17
            com.miui.common.card.models.ListTitleCardModel r5 = new com.miui.common.card.models.ListTitleCardModel
            r5.<init>()
        L_0x0063:
            r10 = r3
            r11 = r10
            r12 = r11
            r9 = r5
        L_0x0067:
            r3 = 0
            if (r9 == 0) goto L_0x00c9
            java.lang.String r5 = r7.f7645c
            java.lang.String r6 = "11-01"
            boolean r5 = r6.equals(r5)
            if (r5 != 0) goto L_0x0083
            java.lang.String r5 = r7.f7645c
            java.lang.String r6 = "01-20-03"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x007f
            goto L_0x0083
        L_0x007f:
            r9.setHomePageFunc(r3)
            goto L_0x0086
        L_0x0083:
            r9.setHomePageFunc(r4)
        L_0x0086:
            long r5 = java.lang.System.currentTimeMillis()
            r9.setId(r5)
            r5 = r18
            r9.setTitle(r5)
            r5 = 1403(0x57b, float:1.966E-42)
            if (r1 != r5) goto L_0x00a8
            java.lang.String r5 = "subTitle"
            java.lang.String r5 = r0.getString(r5)
            r9.setSummary(r5)
            java.lang.String r5 = "subVisible"
            boolean r5 = r0.optBoolean(r5, r4)
            r9.setSubVisible(r5)
        L_0x00a8:
            java.lang.String r5 = "visible"
            boolean r4 = r0.optBoolean(r5, r4)
            r9.setVisible(r4)
            r4 = -1
            if (r1 != r2) goto L_0x00be
            java.lang.String r1 = "position"
            int r1 = r0.optInt(r1, r4)
            r9.setPosition(r1)
            goto L_0x00c1
        L_0x00be:
            r9.setPosition(r4)
        L_0x00c1:
            r9.clear()
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = r7.f7644b
            r1.add(r9)
        L_0x00c9:
            if (r10 == 0) goto L_0x00d0
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = r7.f7644b
            r1.add(r10)
        L_0x00d0:
            if (r11 == 0) goto L_0x00d7
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = r7.f7644b
            r1.add(r11)
        L_0x00d7:
            if (r12 == 0) goto L_0x00de
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = r7.f7644b
            r1.add(r12)
        L_0x00de:
            java.lang.String r1 = "list"
            org.json.JSONArray r13 = r0.optJSONArray(r1)
            if (r13 == 0) goto L_0x00fe
            r14 = r3
        L_0x00e7:
            int r0 = r13.length()
            if (r14 >= r0) goto L_0x00fe
            org.json.JSONObject r5 = r13.getJSONObject(r14)
            r0 = r15
            r1 = r9
            r2 = r10
            r3 = r11
            r4 = r12
            r6 = r17
            a(r0, r1, r2, r3, r4, r5, r6)
            int r14 = r14 + 1
            goto L_0x00e7
        L_0x00fe:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.cards.d.a(com.miui.securityscan.cards.d, org.json.JSONObject, int, java.lang.String):void");
    }

    public static void a(Map<String, String> map) {
        boolean z;
        Application d2 = Application.d();
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("isAInstalled", String.valueOf(x.h(d2, "com.whatsapp")));
            z = x.h(d2, "com.facebook.katana") || x.h(d2, "com.facebook.orca") || x.h(d2, "com.facebook.lite");
        } else {
            map.put("isAInstalled", String.valueOf(x.h(d2, AppConstants.Package.PACKAGE_NAME_MM)));
            z = x.h(d2, AppConstants.Package.PACKAGE_NAME_QQ);
        }
        map.put("isBInstalled", String.valueOf(z));
    }

    public static boolean a(Intent intent) {
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> queryIntentActivities = Application.d().getPackageManager().queryIntentActivities(intent, 1);
        return (queryIntentActivities != null && !queryIntentActivities.isEmpty()) || x.a((Context) Application.d(), intent, false);
    }

    public static String b(Map<String, String> map) {
        String str;
        if (map == null) {
            map = new HashMap<>();
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("channel", "02-24");
            map.put("nt", o.f2310b);
        } else {
            map.put("channel", "01-24");
            map.put("landingPageUrlType", "market");
        }
        a(map);
        boolean l2 = M.l();
        if (!M.m()) {
            str = "2";
        } else if (l2) {
            map.put("setting", o.f2310b);
            return k.a(map, com.miui.securityscan.c.b.f7626a, new j("securityscan_postfirstaidscanresult"));
        } else {
            str = o.f2312d;
        }
        map.put("setting", str);
        return k.a(map, com.miui.securityscan.c.b.f7626a, new j("securityscan_postfirstaidscanresult"));
    }

    private static void b(d dVar, TitleCardModel titleCardModel, FuncTopBannerScrollCnModel funcTopBannerScrollCnModel, FuncTopBannerScrollGlobalModel funcTopBannerScrollGlobalModel, b bVar, JSONObject jSONObject, int i2) {
        BaseCardModel parse;
        String optString = jSONObject.optString("type");
        int optInt = jSONObject.optInt("template");
        JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
        if ("001".equals(optString)) {
            parse = AdvCardModel.parse(dVar, i2, optInt, optJSONObject);
            if (parse != null) {
                dVar.f7644b.add(parse);
                if (titleCardModel == null) {
                    return;
                }
            } else {
                return;
            }
        } else if ("002".equals(optString)) {
            parse = FunctionCardModel.parse(optInt, optJSONObject, titleCardModel, funcTopBannerScrollCnModel, funcTopBannerScrollGlobalModel, bVar);
            if (parse != null) {
                dVar.f7644b.add(parse);
                if (titleCardModel == null) {
                    return;
                }
            } else {
                return;
            }
        } else if ("003".equals(optString)) {
            parse = ActivityCardModel.parse(i2, optInt, optJSONObject);
            if (parse != null) {
                dVar.f7644b.add(parse);
                if (titleCardModel == null) {
                    return;
                }
            } else {
                return;
            }
        } else if ("004".equals(optString)) {
            parse = NewsCardModel.parse(i2, optInt, optJSONObject, titleCardModel);
            if (parse != null) {
                dVar.f7644b.add(parse);
                if (titleCardModel == null) {
                    return;
                }
            } else {
                return;
            }
        } else if ("005".equals(optString) && i2 != 5) {
            dVar.f7644b.add(new LineCardModel());
            return;
        } else {
            return;
        }
        titleCardModel.addSubCardModelList(parse);
    }

    public static String c(Map<String, String> map) {
        String str;
        if (map == null) {
            map = new HashMap<>();
        }
        Application d2 = Application.d();
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("channel", "01-20-03");
            map.put("isAInstalled", String.valueOf(x.h(d2, "com.whatsapp")));
            map.put("isBInstalled", String.valueOf(x.h(d2, "com.facebook.katana") || x.h(d2, "com.facebook.orca") || x.h(d2, "com.facebook.lite")));
            str = "false";
        } else {
            map.put("channel", "11-01");
            map.put("isAInstalled", String.valueOf(x.h(d2, AppConstants.Package.PACKAGE_NAME_MM)));
            map.put("isBInstalled", String.valueOf(x.h(d2, AppConstants.Package.PACKAGE_NAME_QQ)));
            str = String.valueOf(b.b.c.j.o.a());
        }
        map.put("rep", str);
        map.put("isSupportLB", "true");
        map.put("setting", !M.m() ? "2" : M.l() ? o.f2310b : o.f2312d);
        return k.a(map, com.miui.securityscan.c.b.f7626a, new j("securityscan_posthomepage"));
    }

    public static String d(Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("channel", "11-02");
        map.put("landingPageUrlType", "market");
        a(map);
        map.put("setting", !M.m() ? "2" : M.l() ? o.f2310b : o.f2312d);
        return k.a(map, com.miui.securityscan.c.b.f7626a, new j("securityscan_postphonemanagedata"));
    }

    public static String e(Map<String, String> map) {
        String str;
        if (map == null) {
            map = new HashMap<>();
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("channel", "01-14");
            map.put("nt", o.f2310b);
        } else {
            map.put("channel", "01-6");
            map.put("landingPageUrlType", "market");
        }
        a(map);
        boolean l2 = M.l();
        if (!M.m()) {
            str = "2";
        } else if (l2) {
            map.put("setting", o.f2310b);
            return k.a(map, com.miui.securityscan.c.b.f7626a, new j("securityscan_postscanresult"));
        } else {
            str = o.f2312d;
        }
        map.put("setting", str);
        return k.a(map, com.miui.securityscan.c.b.f7626a, new j("securityscan_postscanresult"));
    }

    public void a(int i2) {
        this.n = i2;
    }

    public boolean a() {
        String e2 = e();
        return Build.IS_INTERNATIONAL_BUILD && j() && e2 != null && e2.equalsIgnoreCase(Locale.getDefault().toString());
    }

    public boolean b() {
        String e2 = e();
        return !Build.IS_INTERNATIONAL_BUILD && !j() && e2 != null && e2.equalsIgnoreCase(Locale.getDefault().toString());
    }

    public String c() {
        return this.f7646d;
    }

    public int d() {
        return this.n;
    }

    public String e() {
        return this.k;
    }

    public ArrayList<BaseCardModel> f() {
        ArrayList<BaseCardModel> arrayList;
        BaseCardModel baseCardModel;
        synchronized (f7643a) {
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < this.f7644b.size(); i2++) {
                BaseCardModel baseCardModel2 = this.f7644b.get(i2);
                baseCardModel2.setOverseaChannel(this.j);
                baseCardModel2.setLanguage(this.k);
                if (baseCardModel2 instanceof TitleCardModel) {
                    TitleCardModel titleCardModel = (TitleCardModel) baseCardModel2;
                    List<BaseCardModel> subCardModelList = titleCardModel.getSubCardModelList();
                    if (subCardModelList.isEmpty() || !titleCardModel.isVisible()) {
                        arrayList2.add(baseCardModel2);
                        if (!subCardModelList.isEmpty() && (baseCardModel = subCardModelList.get(0)) != null) {
                            if (baseCardModel instanceof FuncGridBaseCardModel) {
                                ((FuncGridBaseCardModel) baseCardModel).setPreviousLine(true);
                            } else if (baseCardModel instanceof NewsCardModel) {
                                ((NewsCardModel) baseCardModel).setPreviousLine(true);
                            }
                        }
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                this.f7644b.removeAll(arrayList2);
            }
            arrayList = new ArrayList<>(this.f7644b);
        }
        return arrayList;
    }

    public String g() {
        return this.i;
    }

    public int h() {
        return this.m;
    }

    public boolean i() {
        return this.g;
    }

    public boolean j() {
        return this.j;
    }

    public boolean k() {
        return this.h;
    }

    public boolean l() {
        return this.l;
    }
}
