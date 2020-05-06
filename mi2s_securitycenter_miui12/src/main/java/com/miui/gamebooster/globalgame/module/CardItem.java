package com.miui.gamebooster.globalgame.module;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import com.miui.gamebooster.gamead.e;
import com.miui.gamebooster.gamead.g;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;

@Keep
public class CardItem extends e {
    public BannerCardBean bean;

    public CardItem(BannerCardBean bannerCardBean) {
        this.bean = bannerCardBean;
    }

    public void bindView(int i, View view, Context context, g gVar) {
        View view2;
        super.bindView(i, view, context, gVar);
        if (view != null && (view.getTag() instanceof GlobalCardVH) && (view2 = ((GlobalCardVH) view.getTag()).rootView) != null) {
            getCardUpdate().a(context, view2, this.bean.setIndex(i));
        }
    }

    public a getCardUpdate() {
        BannerCardBean bannerCardBean = this.bean;
        return com.miui.gamebooster.globalgame.present.e.a(bannerCardBean, bannerCardBean.type);
    }

    public int getLayoutId() {
        return getCardUpdate().b();
    }
}
