package com.miui.securityscan;

import android.content.Context;
import b.b.c.c.a;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.common.customview.AdImageView;
import com.miui.securitycenter.Application;
import com.miui.securityscan.a.C0536b;
import com.miui.securityscan.scanner.C0554a;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: com.miui.securityscan.a  reason: case insensitive filesystem */
public abstract class C0534a extends a {

    /* renamed from: a  reason: collision with root package name */
    private C0554a f7565a = new C0554a();

    public static void a(String str, AdvCardModel advCardModel) {
        if (!advCardModel.isLocal()) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(new C0536b.a(str, advCardModel));
            C0536b.a((Context) Application.d(), (List<Object>) arrayList);
        }
    }

    public void a(CardViewAdapter cardViewAdapter, String str, int i, int i2) {
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

    public abstract void a(BaseCardModel baseCardModel, int i);

    public abstract void a(BaseCardModel baseCardModel, List<BaseCardModel> list, int i);

    public void a(AdImageView adImageView, int i, AdvCardModel advCardModel) {
        adImageView.a(this.f7565a, i, advCardModel);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        C0554a aVar = this.f7565a;
        if (aVar != null) {
            aVar.removeCallbacksAndMessages((Object) null);
        }
        C0534a.super.onDestroy();
    }
}
