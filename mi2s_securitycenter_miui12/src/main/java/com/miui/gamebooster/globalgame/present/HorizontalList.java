package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.market.sdk.utils.b;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.gamebooster.globalgame.view.GameItemView;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class HorizontalList implements a {

    @Keep
    public static class VH extends GlobalCardVH {
        ViewGroup container;

        public void custom(View view, boolean z, boolean z2) {
            super.custom(view, z, z2);
            this.container = (ViewGroup) this.rootView.findViewById(R.id.container);
        }
    }

    private static void a(Context context, View view, List<GameListItem> list, BannerCardBean bannerCardBean) {
        VH vh = (VH) view.getTag();
        vh.refreshPadding(bannerCardBean.isFirst, bannerCardBean.isLast);
        if (vh.container.getChildCount() == (list.size() / 4) + 1) {
            for (int i = 0; i < list.size(); i++) {
                GameListItem gameListItem = list.get(i);
                ViewGroup viewGroup = (ViewGroup) vh.container.getChildAt(i / 4);
                int i2 = i % 4;
                if (i2 == 0) {
                    for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                        if (viewGroup.getChildAt(i3) instanceof GameItemView) {
                            ((GameItemView) viewGroup.getChildAt(i3)).vanishDetail();
                        }
                    }
                }
                View childAt = viewGroup.getChildAt(i2);
                if (childAt instanceof GameItemView) {
                    ((GameItemView) childAt).update(bannerCardBean, gameListItem, bannerCardBean.getButtonText());
                }
            }
            return;
        }
        vh.container.removeAllViews();
        ArrayList arrayList = new ArrayList();
        for (int i4 = 0; i4 < list.size(); i4++) {
            GameListItem gameListItem2 = list.get(i4);
            int i5 = i4 / 4;
            if (arrayList.size() <= i5) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(0);
                linearLayout.setWeightSum(4.0f);
                arrayList.add(linearLayout);
                vh.container.addView(linearLayout, new LinearLayout.LayoutParams(-1, -2));
            }
            GameItemView gameItemView = new GameItemView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.weight = 1.0f;
            gameItemView.update(bannerCardBean, gameListItem2, bannerCardBean.getButtonText());
            ((LinearLayout) arrayList.get(i5)).addView(gameItemView, layoutParams);
        }
        if (list.size() % 4 != 0) {
            int size = 4 - (list.size() % 4);
            for (int i6 = 0; i6 < size; i6++) {
                View view2 = new View(context);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                layoutParams2.weight = 1.0f;
                ((LinearLayout) arrayList.get(arrayList.size() - 1)).addView(view2, layoutParams2);
            }
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        List<GameListItem> gameList = bannerCardBean.getGameList();
        if (!b.a(gameList)) {
            ArrayList arrayList = new ArrayList();
            int min = Math.min(gameList.size(), 20);
            for (int i = 0; i < min; i++) {
                GameListItem gameListItem = gameList.get(i);
                gameListItem.setGameIndex(i);
                arrayList.add(gameListItem);
            }
            a(context, view, arrayList, bannerCardBean);
        }
    }

    public int b() {
        return R.layout.gbg_card_horizontal_list;
    }
}
