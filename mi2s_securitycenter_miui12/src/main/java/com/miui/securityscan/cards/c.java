package com.miui.securityscan.cards;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.AdvInternationalCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.card.models.LineCardModel;
import com.miui.common.card.models.ScanResultBottomCardModelNew;
import com.miui.common.card.models.ScanResultTopCardModel;
import com.miui.firstaidkit.FirstAidKitActivity;
import com.miui.firstaidkit.b.a;
import com.miui.firstaidkit.b.b;
import com.miui.firstaidkit.b.d;
import com.miui.firstaidkit.b.h;
import com.miui.firstaidkit.model.FeedBackModel;
import com.miui.firstaidkit.o;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.scanner.C0569p;
import com.miui.securityscan.scanner.C0570q;
import com.miui.securityscan.scanner.O;
import com.miui.securityscan.scanner.ScoreManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.Build;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final ArrayList<BaseCardModel> f7639a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    private static final ArrayList<BaseCardModel> f7640b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    private static final ArrayList<BaseCardModel> f7641c = new ArrayList<>();

    /* renamed from: d  reason: collision with root package name */
    private static final ArrayList<BaseCardModel> f7642d = new ArrayList<>();
    private static ScoreManager e = ScoreManager.e();
    private static final Resources f = Application.c();

    public static ArrayList<BaseCardModel> a(ArrayList<BaseCardModel> arrayList, int i) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            int size = arrayList.size();
            ArrayList arrayList2 = new ArrayList();
            int i2 = -1;
            for (int i3 = 0; i3 < size; i3++) {
                BaseCardModel baseCardModel = arrayList.get(i3);
                if (!(baseCardModel instanceof LineCardModel)) {
                    if (baseCardModel instanceof AdvInternationalCardModel) {
                        AdvInternationalCardModel advInternationalCardModel = (AdvInternationalCardModel) baseCardModel;
                        if (!advInternationalCardModel.isLoaded()) {
                            String positionId = advInternationalCardModel.getPositionId();
                            Log.d("CardResultHelper", "advFacebookCardModel is not loaded placeid : " + positionId);
                            AdvInternationalCardModel a2 = h.a(i, positionId, advInternationalCardModel.getTemplate());
                            if (a2 == null || !a2.isLoaded()) {
                                Log.d("CardResultHelper", "international ad hide");
                            } else {
                                advInternationalCardModel.fillAd(a2);
                            }
                        } else {
                            Log.d("CardResultHelper", "advFacebookCardModel is loaded");
                        }
                    }
                    i2 = -1;
                } else if (i2 == -1) {
                    i2 = i3;
                } else {
                    arrayList2.add(baseCardModel);
                }
            }
            arrayList.removeAll(arrayList2);
        }
        return arrayList;
    }

    public static void a() {
        if (f7641c.size() != 1 || !(f7641c.get(0) instanceof ScanResultTopCardModel)) {
            f7641c.add(new LineCardModel());
        }
        f7641c.addAll(f());
    }

    public static void a(Context context) {
        f7639a.addAll(b(context));
    }

    public static void a(CardViewAdapter cardViewAdapter, String str) {
        if (cardViewAdapter != null) {
            Iterator<BaseCardModel> it = cardViewAdapter.getModelList().iterator();
            while (it.hasNext()) {
                BaseCardModel next = it.next();
                if ((next instanceof AdvCardModel) && str.equals(((AdvCardModel) next).getPackageName())) {
                    cardViewAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    public static void a(O.a aVar, boolean z) {
        f7641c.addAll(b(aVar, z));
    }

    public static void a(ArrayList<BaseCardModel> arrayList) {
        if (arrayList != null && arrayList.size() > 0) {
            f7641c.addAll(arrayList);
        }
    }

    public static void a(List<BaseCardModel> list, BaseCardModel baseCardModel) {
        if (list != null && baseCardModel != null) {
            int indexOf = list.indexOf(baseCardModel);
            if (indexOf > 0 && indexOf < list.size() - 1) {
                int i = indexOf - 1;
                int i2 = indexOf + 1;
                if (i >= 0 && i2 < list.size() && (list.get(i) instanceof LineCardModel)) {
                    list.remove(i);
                }
            }
            list.remove(baseCardModel);
        }
    }

    public static ArrayList<BaseCardModel> b(Context context) {
        List<AbsModel> c2;
        List<AbsModel> a2;
        List<AbsModel> b2;
        List<AbsModel> e2;
        List<AbsModel> d2;
        f7640b.clear();
        o f2 = o.f();
        boolean z = true;
        boolean z2 = f2.d().size() > 0;
        boolean z3 = f2.e().size() > 0;
        boolean z4 = f2.b().size() > 0;
        boolean z5 = f2.a().size() > 0;
        if (f2.c().size() <= 0) {
            z = false;
        }
        if (z2 && (d2 = f2.d()) != null && !d2.isEmpty()) {
            for (int i = 0; i < d2.size(); i++) {
                h hVar = new h();
                hVar.a(d2.get(i));
                hVar.a((FirstAidKitActivity) context);
                f7640b.add(hVar);
            }
        }
        if (z3 && (e2 = f2.e()) != null && !e2.isEmpty()) {
            for (int i2 = 0; i2 < e2.size(); i2++) {
                h hVar2 = new h();
                hVar2.a(e2.get(i2));
                hVar2.a((FirstAidKitActivity) context);
                f7640b.add(hVar2);
            }
        }
        if (z4 && (b2 = f2.b()) != null && !b2.isEmpty()) {
            for (int i3 = 0; i3 < b2.size(); i3++) {
                h hVar3 = new h();
                hVar3.a(b2.get(i3));
                hVar3.a((FirstAidKitActivity) context);
                f7640b.add(hVar3);
            }
        }
        if (z5 && (a2 = f2.a()) != null && !a2.isEmpty()) {
            for (int i4 = 0; i4 < a2.size(); i4++) {
                h hVar4 = new h();
                hVar4.a(a2.get(i4));
                hVar4.a((FirstAidKitActivity) context);
                f7640b.add(hVar4);
            }
        }
        if (z && (c2 = f2.c()) != null && !c2.isEmpty()) {
            for (int i5 = 0; i5 < c2.size(); i5++) {
                h hVar5 = new h();
                hVar5.a(c2.get(i5));
                hVar5.a((FirstAidKitActivity) context);
                f7640b.add(hVar5);
            }
        }
        d dVar = new d();
        dVar.a((FirstAidKitActivity) context);
        dVar.setIcon("drawable://2131231070");
        dVar.a(context.getString(R.string.first_aid_result_feedback_title));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FeedBackModel("", 0));
        dVar.a((List<AbsModel>) arrayList);
        f7640b.add(dVar);
        b bVar = new b();
        bVar.e(z2);
        bVar.b(z3);
        bVar.c(z4);
        bVar.a(z5);
        bVar.d(z);
        f7640b.add(bVar);
        return new ArrayList<>(f7640b);
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x021f  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x000f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<com.miui.common.card.models.BaseCardModel> b(com.miui.securityscan.scanner.O.a r11, boolean r12) {
        /*
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r0 = f7642d
            r0.clear()
            com.miui.securityscan.scanner.ScoreManager r0 = e
            java.util.List r0 = r0.g()
            java.util.Iterator r0 = r0.iterator()
        L_0x000f:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0232
            java.lang.Object r1 = r0.next()
            r4 = r1
            com.miui.securityscan.model.GroupModel r4 = (com.miui.securityscan.model.GroupModel) r4
            com.miui.securityscan.model.AbsModel r1 = r4.getCurModel()
            com.miui.securityscan.model.AbsModel$State r2 = r1.isSafe()
            com.miui.securityscan.model.AbsModel$State r3 = com.miui.securityscan.model.AbsModel.State.DANGER
            if (r2 != r3) goto L_0x000f
            r2 = 0
            int r3 = r1.getIndex()
            r5 = 11
            if (r3 == r5) goto L_0x01f4
            r5 = 26
            r6 = 2131757075(0x7f100813, float:1.9145076E38)
            if (r3 == r5) goto L_0x01e3
            r5 = 27
            r7 = 2131757082(0x7f10081a, float:1.914509E38)
            if (r3 == r5) goto L_0x01d1
            r5 = 58
            if (r3 == r5) goto L_0x01bc
            r5 = 59
            if (r3 == r5) goto L_0x01a7
            java.lang.String r5 = "drawable://2131231687"
            switch(r3) {
                case 1: goto L_0x0190;
                case 2: goto L_0x017e;
                case 3: goto L_0x0166;
                case 4: goto L_0x01f4;
                case 5: goto L_0x0151;
                case 6: goto L_0x013e;
                case 7: goto L_0x01f4;
                case 8: goto L_0x012b;
                case 9: goto L_0x0118;
                default: goto L_0x004c;
            }
        L_0x004c:
            switch(r3) {
                case 13: goto L_0x00ec;
                case 14: goto L_0x00ec;
                case 15: goto L_0x00ec;
                case 16: goto L_0x00ec;
                case 17: goto L_0x00ec;
                case 18: goto L_0x00ec;
                case 19: goto L_0x00ec;
                case 20: goto L_0x00ec;
                default: goto L_0x004f;
            }
        L_0x004f:
            r6 = 2131757071(0x7f10080f, float:1.9145067E38)
            switch(r3) {
                case 22: goto L_0x01f4;
                case 23: goto L_0x01f4;
                case 24: goto L_0x00d7;
                default: goto L_0x0055;
            }
        L_0x0055:
            switch(r3) {
                case 31: goto L_0x01f4;
                case 32: goto L_0x00c2;
                case 33: goto L_0x00ad;
                case 34: goto L_0x009a;
                default: goto L_0x0058;
            }
        L_0x0058:
            switch(r3) {
                case 36: goto L_0x0086;
                case 37: goto L_0x021d;
                case 38: goto L_0x0070;
                case 39: goto L_0x01f4;
                case 40: goto L_0x005d;
                default: goto L_0x005b;
            }
        L_0x005b:
            goto L_0x021d
        L_0x005d:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131757072(0x7f100810, float:1.914507E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            goto L_0x00e6
        L_0x0070:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131755793(0x7f100311, float:1.9142475E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231693"
            goto L_0x01a1
        L_0x0086:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131757076(0x7f100814, float:1.9145078E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            goto L_0x01a1
        L_0x009a:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r6)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231694"
            goto L_0x01a1
        L_0x00ad:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            com.miui.securityscan.model.manualitem.ConsumePowerRankModel r1 = (com.miui.securityscan.model.manualitem.ConsumePowerRankModel) r1
            android.content.res.Resources r3 = f
            r4 = 2131757068(0x7f10080c, float:1.9145061E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.models.ListTitleConsumePowerRankCardModel r2 = r2.getListTitleConsumePowerRankCardModel(r11, r1, r3)
            goto L_0x021d
        L_0x00c2:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            com.miui.securityscan.model.manualitem.FlowRankModel r1 = (com.miui.securityscan.model.manualitem.FlowRankModel) r1
            android.content.res.Resources r3 = f
            r4 = 2131757078(0x7f100816, float:1.9145082E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.models.ListTitleFlowRankCardModel r2 = r2.getListTitleFlowRankCardModel(r11, r1, r3)
            goto L_0x021d
        L_0x00d7:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r6)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
        L_0x00e6:
            com.miui.common.card.models.FunNoIconCardModel r2 = r2.getFuncTopBannerCardModel(r1, r3, r4)
            goto L_0x021d
        L_0x00ec:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r1 = f
            r3 = 2131757086(0x7f10081e, float:1.9145098E38)
            java.lang.String r5 = r1.getString(r3)
            android.content.res.Resources r1 = f
            r3 = 2131757084(0x7f10081c, float:1.9145094E38)
            java.lang.String r6 = r1.getString(r3)
            android.content.res.Resources r1 = f
            r3 = 2131757074(0x7f100812, float:1.9145073E38)
            java.lang.String r7 = r1.getString(r3)
            android.content.res.Resources r1 = f
            r3 = 2131757088(0x7f100820, float:1.9145102E38)
            java.lang.String r8 = r1.getString(r3)
            r9 = 1
            r10 = 0
            goto L_0x0218
        L_0x0118:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r6)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231764"
            goto L_0x01a1
        L_0x012b:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131757070(0x7f10080e, float:1.9145065E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            goto L_0x01a1
        L_0x013e:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131757081(0x7f100819, float:1.9145088E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            goto L_0x01a1
        L_0x0151:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131757077(0x7f100815, float:1.914508E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231768"
            goto L_0x01a1
        L_0x0166:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131757067(0x7f10080b, float:1.914506E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            com.miui.common.card.models.FuncCloudSpaceCardModel r2 = r2.getFuncCloudSpaceCardModel(r1, r3, r4)
            goto L_0x021d
        L_0x017e:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r6)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231765"
            goto L_0x01a1
        L_0x0190:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r7)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "pkg_icon://com.android.updater"
        L_0x01a1:
            com.miui.common.card.models.FuncBtnBottomCardModel r2 = r2.getFuncBtnBottomCardModel(r1, r3, r5, r4)
            goto L_0x021d
        L_0x01a7:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131755712(0x7f1002c0, float:1.9142311E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231766"
            goto L_0x01a1
        L_0x01bc:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            r4 = 2131755715(0x7f1002c3, float:1.9142317E38)
            java.lang.String r3 = r3.getString(r4)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "drawable://2131231767"
            goto L_0x01a1
        L_0x01d1:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r7)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            java.lang.String r5 = "pkg_icon://com.xiaomi.market"
            goto L_0x01a1
        L_0x01e3:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r3 = f
            java.lang.String r3 = r3.getString(r6)
            com.miui.common.card.functions.OptimizeFunction r4 = new com.miui.common.card.functions.OptimizeFunction
            r4.<init>(r11, r1)
            goto L_0x00e6
        L_0x01f4:
            com.miui.common.card.models.CardModelMaker r2 = com.miui.common.card.models.CardModelMaker.getInstance()
            android.content.res.Resources r1 = f
            r3 = 2131757085(0x7f10081d, float:1.9145096E38)
            java.lang.String r5 = r1.getString(r3)
            android.content.res.Resources r1 = f
            r3 = 2131757083(0x7f10081b, float:1.9145092E38)
            java.lang.String r6 = r1.getString(r3)
            android.content.res.Resources r1 = f
            r3 = 2131757073(0x7f100811, float:1.9145071E38)
            java.lang.String r7 = r1.getString(r3)
            r8 = 0
            r9 = 1
            r10 = 2131232274(0x7f080612, float:1.8080653E38)
        L_0x0218:
            r3 = r11
            com.miui.common.card.models.ListTitleCheckboxCardModel r2 = r2.getListTitleCheckboxCardModel(r3, r4, r5, r6, r7, r8, r9, r10)
        L_0x021d:
            if (r2 == 0) goto L_0x000f
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = f7642d
            r1.add(r2)
            if (r12 == 0) goto L_0x000f
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r1 = f7642d
            com.miui.common.card.models.LineCardModel r2 = new com.miui.common.card.models.LineCardModel
            r2.<init>()
            r1.add(r2)
            goto L_0x000f
        L_0x0232:
            java.util.ArrayList r11 = new java.util.ArrayList
            java.util.ArrayList<com.miui.common.card.models.BaseCardModel> r12 = f7642d
            r11.<init>(r12)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securityscan.cards.c.b(com.miui.securityscan.scanner.O$a, boolean):java.util.ArrayList");
    }

    public static void b() {
        f7641c.add(new ScanResultTopCardModel());
    }

    public static void b(ArrayList<BaseCardModel> arrayList) {
        if (arrayList != null && arrayList.size() > 0) {
            f7639a.addAll(arrayList);
        }
    }

    public static ArrayList<BaseCardModel> c(ArrayList<BaseCardModel> arrayList) {
        if (arrayList == null) {
            return new ArrayList<>();
        }
        ArrayList arrayList2 = new ArrayList();
        for (int size = arrayList.size() - 1; size > 0; size--) {
            if ((arrayList.get(size) instanceof LineCardModel) && (arrayList.get(size - 1) instanceof LineCardModel)) {
                arrayList2.add(arrayList.get(size));
            }
        }
        arrayList.removeAll(arrayList2);
        int size2 = arrayList.size();
        if (size2 > 0) {
            int i = size2 - 1;
            if (arrayList.get(i) instanceof LineCardModel) {
                arrayList.remove(i);
            }
        }
        return arrayList;
    }

    public static void c() {
        f7639a.add(new a());
    }

    public static void d() {
        f7641c.clear();
    }

    public static void e() {
        f7639a.clear();
    }

    public static ArrayList<BaseCardModel> f() {
        ArrayList<BaseCardModel> arrayList = new ArrayList<>();
        Map<String, C0569p> a2 = C0570q.b().a(C0570q.a.SYSTEM);
        Map<String, C0569p> a3 = C0570q.b().a(C0570q.a.CLEANUP);
        Map<String, C0569p> a4 = C0570q.b().a(C0570q.a.SECURITY);
        ScanResultBottomCardModelNew scanResultBottomCardModelNew = new ScanResultBottomCardModelNew();
        scanResultBottomCardModelNew.setSystemResId(R.drawable.card_icon_system);
        scanResultBottomCardModelNew.setSystemTitle(f.getString(R.string.optmizingbar_title_system));
        scanResultBottomCardModelNew.setSystemStatus(f.getQuantityString(R.plurals.system_check_content, a2.size(), new Object[]{Integer.valueOf(a2.size())}));
        scanResultBottomCardModelNew.setSystemMap(a2);
        scanResultBottomCardModelNew.setCleanupResId(R.drawable.card_icon_memory);
        scanResultBottomCardModelNew.setCleanupTitle(f.getString(R.string.optmizingbar_title_clear));
        scanResultBottomCardModelNew.setCleanupStatus(f.getQuantityString(R.plurals.system_check_content, a3.size(), new Object[]{Integer.valueOf(a3.size())}));
        scanResultBottomCardModelNew.setCleanupMap(a3);
        scanResultBottomCardModelNew.setSecurityResId(R.drawable.card_icon_cache);
        scanResultBottomCardModelNew.setSecurityTitle(f.getString(R.string.optmizingbar_title_security));
        scanResultBottomCardModelNew.setSecurityStatus(f.getQuantityString(R.plurals.system_check_content, a4.size(), new Object[]{Integer.valueOf(a4.size())}));
        scanResultBottomCardModelNew.setSecurityMap(a4);
        arrayList.add(scanResultBottomCardModelNew);
        return arrayList;
    }

    public static ArrayList<BaseCardModel> g() {
        return new ArrayList<>(f7639a);
    }

    public static ArrayList<BaseCardModel> h() {
        return c(new ArrayList(f7641c));
    }

    public static void i() {
        f7639a.clear();
        f7640b.clear();
    }
}
