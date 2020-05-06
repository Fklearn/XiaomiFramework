package com.miui.gamebooster.globalgame.global;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.miui.gamebooster.gamead.e;
import com.miui.gamebooster.gamead.g;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.CardItem;
import com.miui.gamebooster.globalgame.present.h;
import com.miui.gamebooster.i.a.c;
import com.miui.gamebooster.model.f;
import java.util.ArrayList;
import java.util.List;

public class b extends g implements h, f {
    private boolean e = false;
    private Context f;

    public b(Context context, List<e> list, b.b.c.i.b bVar) {
        super(context, list, bVar);
        this.f = context;
    }

    public void a() {
        clear();
    }

    public void a(List<BannerCardBean> list) {
        ArrayList arrayList = new ArrayList();
        for (BannerCardBean cardItem : list) {
            arrayList.add(new CardItem(cardItem));
        }
        addAll(arrayList);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        BannerCardBean bannerCardBean;
        BannerCardBean bannerCardBean2;
        e eVar = (e) getItem(i);
        int layoutId = eVar.getLayoutId();
        GlobalCardVH globalCardVH = null;
        if (view == null) {
            view = View.inflate(this.f, layoutId, (ViewGroup) null);
        }
        CardItem cardItem = eVar instanceof CardItem ? (CardItem) eVar : null;
        if (view.getTag() == null) {
            if (cardItem != null) {
                try {
                    if (cardItem.bean != null) {
                        globalCardVH = (GlobalCardVH) cardItem.getCardUpdate().a().newInstance();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (globalCardVH == null) {
                globalCardVH = new GlobalCardVH();
            }
            if (cardItem == null || (bannerCardBean2 = cardItem.bean) == null) {
                globalCardVH.custom(view, false, false);
            } else {
                globalCardVH.custom(view, bannerCardBean2.isFirst, bannerCardBean2.isLast);
            }
            view.setTag(globalCardVH);
        }
        eVar.bindView(i, view, this.f, this);
        if (!this.e) {
            this.e = true;
            c.c();
        }
        if (i == getCount() - 1) {
            c.b();
        }
        if (!(cardItem == null || (bannerCardBean = cardItem.bean) == null)) {
            c.a(i, bannerCardBean);
        }
        return view;
    }
}
