package com.miui.securityscan.cards;

import android.content.Context;
import android.content.res.Resources;
import b.b.j.b.c;
import b.b.j.b.h;
import b.b.j.d.a;
import com.miui.common.card.GridFunctionData;
import com.miui.common.card.functions.FuncTopBannerScrollData;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.FuncGrid6CardModel;
import com.miui.common.card.models.FuncTopBannerScrollCnModel;
import com.miui.common.card.models.LineCardModel;
import com.miui.common.card.models.TopCardModel;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miui.os.Build;

public final class b {

    /* renamed from: a  reason: collision with root package name */
    private static final ArrayList<BaseCardModel> f7637a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    private static final Resources f7638b = Application.c();

    static {
        f7637a.add(new TopCardModel());
    }

    public static ArrayList<BaseCardModel> a() {
        return new ArrayList<>(f7637a);
    }

    public static ArrayList<BaseCardModel> a(Context context) {
        ArrayList<BaseCardModel> arrayList = new ArrayList<>();
        c cVar = new c();
        cVar.setTitle(f7638b.getString(R.string.phone_manage_system_tool_title));
        cVar.setSummary(f7638b.getString(R.string.phone_manage_system_tool_summary));
        cVar.setSubVisible(true);
        arrayList.add(cVar);
        ArrayList<BaseCardModel> a2 = a((List<GridFunctionData>) a.k());
        arrayList.addAll(a2);
        cVar.addSubCardModelList(a2);
        c cVar2 = new c();
        cVar2.setTitle(f7638b.getString(R.string.phone_manage_clean_title));
        cVar2.setSummary(f7638b.getString(R.string.phone_manage_clean_summary));
        cVar2.setSubVisible(true);
        arrayList.add(cVar2);
        ArrayList<BaseCardModel> a3 = a((List<GridFunctionData>) a.j());
        arrayList.addAll(a3);
        cVar2.addSubCardModelList(a3);
        c cVar3 = new c();
        cVar3.setTitle(f7638b.getString(R.string.title_of_app_manage));
        cVar3.setSummary(f7638b.getString(R.string.phone_manage_app_manager_summary));
        cVar3.setSubVisible(true);
        arrayList.add(cVar3);
        ArrayList<BaseCardModel> a4 = a((List<GridFunctionData>) a.i());
        arrayList.addAll(a4);
        cVar3.addSubCardModelList(a4);
        c cVar4 = new c();
        cVar4.setTitle(f7638b.getString(R.string.phone_manage_electric_title));
        cVar4.setSummary(f7638b.getString(R.string.phone_manage_electric_summary));
        cVar4.setSubVisible(true);
        arrayList.add(cVar4);
        ArrayList<BaseCardModel> a5 = a((List<GridFunctionData>) a.b(context));
        arrayList.addAll(a5);
        cVar4.addSubCardModelList(a5);
        ArrayList<GridFunctionData> a6 = a.a(context);
        if (!a6.isEmpty()) {
            c cVar5 = new c();
            cVar5.setTitle(f7638b.getString(R.string.phone_manage_special_title));
            cVar5.setSummary(f7638b.getString(R.string.phone_manage_special_summary));
            cVar5.setSubVisible(true);
            arrayList.add(cVar5);
            ArrayList<BaseCardModel> a7 = a((List<GridFunctionData>) a6);
            arrayList.addAll(a7);
            cVar5.addSubCardModelList(a7);
        }
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r3 == (r2 - 1)) goto L_0x001c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0031  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0032 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.ArrayList<com.miui.common.card.models.BaseCardModel> a(java.util.List<com.miui.common.card.GridFunctionData> r7) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = r7.size()
            r2 = 4
            int r2 = com.miui.securityscan.cards.d.a((int) r1, (int) r2)
            r3 = 0
        L_0x000f:
            if (r3 >= r2) goto L_0x003f
            b.b.j.b.h r4 = new b.b.j.b.h
            r4.<init>()
            r5 = 1
            if (r2 != r5) goto L_0x0020
            r4.setTopRow(r5)
        L_0x001c:
            r4.setBottomRow(r5)
            goto L_0x002b
        L_0x0020:
            if (r3 != 0) goto L_0x0026
            r4.setTopRow(r5)
            goto L_0x002b
        L_0x0026:
            int r6 = r2 + -1
            if (r3 != r6) goto L_0x002b
            goto L_0x001c
        L_0x002b:
            int r5 = r3 * 4
            int r6 = r5 + 4
            if (r6 <= r1) goto L_0x0032
            r6 = r1
        L_0x0032:
            java.util.List r5 = r7.subList(r5, r6)
            r4.setGridFunctionDataList(r5)
            r0.add(r4)
            int r3 = r3 + 1
            goto L_0x000f
        L_0x003f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.cards.b.a(java.util.List):java.util.ArrayList");
    }

    public static List<BaseCardModel> a(Context context, List<BaseCardModel> list) {
        if (context == null) {
            return null;
        }
        ArrayList<String> a2 = a.a();
        ArrayList arrayList = new ArrayList();
        if (a2 != null && a2.size() > 0) {
            ArrayList arrayList2 = new ArrayList();
            HashMap hashMap = new HashMap();
            for (BaseCardModel next : list) {
                if (next instanceof h) {
                    for (GridFunctionData next2 : ((h) next).getGridFunctionDataList()) {
                        if (a2.contains(next2.getAction())) {
                            hashMap.put(next2.getAction(), next2);
                        }
                    }
                }
            }
            ArrayList arrayList3 = new ArrayList();
            for (int i = 0; i < a2.size(); i++) {
                GridFunctionData gridFunctionData = (GridFunctionData) hashMap.get(a2.get(i));
                if (gridFunctionData != null) {
                    arrayList2.add(gridFunctionData);
                    arrayList3.add(a2.get(i));
                }
            }
            if (arrayList3.size() != a2.size()) {
                a.a((List<String>) arrayList3);
            }
            if (!arrayList2.isEmpty()) {
                c cVar = new c();
                cVar.setTitle(context.getResources().getString(R.string.phone_manage_recent_use));
                cVar.setSubVisible(false);
                ArrayList<BaseCardModel> a3 = a((List<GridFunctionData>) arrayList2);
                arrayList.add(cVar);
                arrayList.addAll(a3);
            }
        }
        return arrayList;
    }

    public static ArrayList<BaseCardModel> b() {
        return Build.IS_INTERNATIONAL_BUILD ? d() : c();
    }

    private static ArrayList<BaseCardModel> c() {
        ArrayList<BaseCardModel> arrayList = new ArrayList<>();
        arrayList.add(new TopCardModel());
        FuncGrid6CardModel funcGrid6CardModel = new FuncGrid6CardModel();
        funcGrid6CardModel.setCurrentRowIndex(0);
        funcGrid6CardModel.setHomePageFunc(true);
        funcGrid6CardModel.setGridFunctionDataList(a.e());
        arrayList.add(funcGrid6CardModel);
        FuncGrid6CardModel funcGrid6CardModel2 = new FuncGrid6CardModel();
        funcGrid6CardModel2.setCurrentRowIndex(1);
        funcGrid6CardModel2.setHomePageFunc(true);
        funcGrid6CardModel2.setGridFunctionDataList(a.f());
        arrayList.add(funcGrid6CardModel2);
        FuncGrid6CardModel funcGrid6CardModel3 = new FuncGrid6CardModel();
        funcGrid6CardModel3.setCurrentRowIndex(2);
        funcGrid6CardModel3.setHomePageFunc(true);
        funcGrid6CardModel3.setGridFunctionDataList(a.g());
        arrayList.add(funcGrid6CardModel3);
        arrayList.add(new LineCardModel());
        ArrayList<FuncTopBannerScrollData> a2 = a.a();
        FuncTopBannerScrollCnModel funcTopBannerScrollCnModel = new FuncTopBannerScrollCnModel();
        funcTopBannerScrollCnModel.setFuncTopBannerScrollDataList(a2);
        arrayList.add(funcTopBannerScrollCnModel);
        return arrayList;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x00e3, code lost:
        if (r2 == (r5 - 1)) goto L_0x00d7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00ed A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.util.ArrayList<com.miui.common.card.models.BaseCardModel> d() {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            com.miui.common.card.models.TopCardModel r1 = new com.miui.common.card.models.TopCardModel
            r1.<init>()
            r0.add(r1)
            com.miui.common.card.models.FuncGrid6CardModel r1 = new com.miui.common.card.models.FuncGrid6CardModel
            r1.<init>()
            r2 = 0
            r1.setCurrentRowIndex(r2)
            r3 = 1
            r1.setHomePageFunc(r3)
            java.util.ArrayList r4 = com.miui.securityscan.cards.a.b()
            r1.setGridFunctionDataList(r4)
            r0.add(r1)
            com.miui.common.card.models.FuncGrid6CardModel r1 = new com.miui.common.card.models.FuncGrid6CardModel
            r1.<init>()
            r1.setCurrentRowIndex(r3)
            r1.setHomePageFunc(r3)
            java.util.ArrayList r4 = com.miui.securityscan.cards.a.c()
            r1.setGridFunctionDataList(r4)
            r0.add(r1)
            com.miui.common.card.models.FuncGrid6CardModel r1 = new com.miui.common.card.models.FuncGrid6CardModel
            r1.<init>()
            r4 = 2
            r1.setCurrentRowIndex(r4)
            r1.setHomePageFunc(r3)
            java.util.ArrayList r4 = com.miui.securityscan.cards.a.d()
            r1.setGridFunctionDataList(r4)
            r0.add(r1)
            com.miui.common.card.models.LineCardModel r1 = new com.miui.common.card.models.LineCardModel
            r1.<init>()
            r0.add(r1)
            java.lang.String r1 = "#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end"
            android.content.Intent r1 = android.content.Intent.parseUri(r1, r2)     // Catch:{ Exception -> 0x00a0 }
            com.miui.common.card.models.FuncTopBannerNew2CardModel r4 = new com.miui.common.card.models.FuncTopBannerNew2CardModel     // Catch:{ Exception -> 0x00a0 }
            r4.<init>()     // Catch:{ Exception -> 0x00a0 }
            r4.setHomePageFunc(r3)     // Catch:{ Exception -> 0x00a0 }
            android.content.res.Resources r5 = f7638b     // Catch:{ Exception -> 0x00a0 }
            r6 = 2131755766(0x7f1002f6, float:1.914242E38)
            java.lang.String r5 = r5.getString(r6)     // Catch:{ Exception -> 0x00a0 }
            r4.setTitle(r5)     // Catch:{ Exception -> 0x00a0 }
            android.content.res.Resources r5 = f7638b     // Catch:{ Exception -> 0x00a0 }
            r6 = 2131755765(0x7f1002f5, float:1.9142419E38)
            java.lang.String r5 = r5.getString(r6)     // Catch:{ Exception -> 0x00a0 }
            r4.setSummary(r5)     // Catch:{ Exception -> 0x00a0 }
            java.lang.String r5 = "assets://img/icon_game_booster.png"
            r4.setIcon(r5)     // Catch:{ Exception -> 0x00a0 }
            java.lang.String r5 = "drawable://2131231235"
            r4.setImgUrl(r5)     // Catch:{ Exception -> 0x00a0 }
            java.lang.String r5 = "game_boost_international"
            r4.setStatKey(r5)     // Catch:{ Exception -> 0x00a0 }
            com.miui.common.card.functions.CommonFunction r5 = new com.miui.common.card.functions.CommonFunction     // Catch:{ Exception -> 0x00a0 }
            r5.<init>(r1)     // Catch:{ Exception -> 0x00a0 }
            r4.setFunction(r5)     // Catch:{ Exception -> 0x00a0 }
            r0.add(r4)     // Catch:{ Exception -> 0x00a0 }
            com.miui.common.card.models.LineCardModel r1 = new com.miui.common.card.models.LineCardModel     // Catch:{ Exception -> 0x00a0 }
            r1.<init>()     // Catch:{ Exception -> 0x00a0 }
            r0.add(r1)     // Catch:{ Exception -> 0x00a0 }
            goto L_0x00a4
        L_0x00a0:
            r1 = move-exception
            r1.printStackTrace()
        L_0x00a4:
            com.miui.common.card.models.ListTitleCardModel r1 = new com.miui.common.card.models.ListTitleCardModel
            r1.<init>()
            r1.setHomePageFunc(r3)
            android.content.res.Resources r4 = f7638b
            r5 = 2131755780(0x7f100304, float:1.9142449E38)
            java.lang.String r4 = r4.getString(r5)
            r1.setTitle(r4)
            r0.add(r1)
            java.util.ArrayList r1 = com.miui.securityscan.cards.a.h()
            int r4 = r1.size()
            r5 = 3
            int r5 = com.miui.securityscan.cards.d.a((int) r4, (int) r5)
        L_0x00c8:
            if (r2 >= r5) goto L_0x00fa
            com.miui.common.card.models.FuncGrid9ColorfulCardModel r6 = new com.miui.common.card.models.FuncGrid9ColorfulCardModel
            r6.<init>()
            r6.setHomePageFunc(r3)
            if (r5 != r3) goto L_0x00db
            r6.setTopRow(r3)
        L_0x00d7:
            r6.setBottomRow(r3)
            goto L_0x00e6
        L_0x00db:
            if (r2 != 0) goto L_0x00e1
            r6.setTopRow(r3)
            goto L_0x00e6
        L_0x00e1:
            int r7 = r5 + -1
            if (r2 != r7) goto L_0x00e6
            goto L_0x00d7
        L_0x00e6:
            int r7 = r2 * 3
            int r8 = r7 + 3
            if (r8 <= r4) goto L_0x00ed
            r8 = r4
        L_0x00ed:
            java.util.List r7 = r1.subList(r7, r8)
            r6.setGridFunctionDataList(r7)
            r0.add(r6)
            int r2 = r2 + 1
            goto L_0x00c8
        L_0x00fa:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.cards.b.d():java.util.ArrayList");
    }
}
