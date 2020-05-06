package com.miui.securityscan;

import android.app.Fragment;
import com.miui.common.card.CardViewAdapter;
import com.miui.common.card.models.AdvCardModel;
import com.miui.common.card.models.BaseCardModel;
import com.miui.securityscan.scanner.C0554a;
import java.util.Iterator;

/* renamed from: com.miui.securityscan.b  reason: case insensitive filesystem */
public abstract class C0542b extends Fragment {

    /* renamed from: a  reason: collision with root package name */
    private C0554a f7600a = new C0554a();

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

    public void onDestroy() {
        C0554a aVar = this.f7600a;
        if (aVar != null) {
            aVar.removeCallbacksAndMessages((Object) null);
        }
        super.onDestroy();
    }
}
