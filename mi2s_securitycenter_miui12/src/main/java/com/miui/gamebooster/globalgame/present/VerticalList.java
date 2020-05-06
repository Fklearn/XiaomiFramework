package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import com.market.sdk.utils.b;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.gamebooster.globalgame.view.GameItemView;
import com.miui.securitycenter.R;
import java.util.List;

public class VerticalList implements a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static int[] f4399a = {R.id.gameItemView00, R.id.gameItemView01, R.id.gameItemView02};

    @Keep
    public static class VH extends GlobalCardVH {
        GameItemView[] itemArr;

        public void custom(View view, boolean z, boolean z2) {
            super.custom(view, z, z2);
            this.itemArr = new GameItemView[VerticalList.f4399a.length];
            for (int i = 0; i < VerticalList.f4399a.length; i++) {
                this.itemArr[i] = (GameItemView) view.findViewById(VerticalList.f4399a[i]);
            }
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        List<GameListItem> gameList = bannerCardBean.getGameList();
        if (!b.a(gameList)) {
            VH vh = (VH) view.getTag();
            vh.refreshPadding(bannerCardBean.isFirst, bannerCardBean.isLast);
            int min = Math.min(gameList.size(), vh.itemArr.length);
            for (int i = 0; i < min; i++) {
                GameListItem gameListItem = gameList.get(i);
                gameListItem.setGameIndex(i);
                vh.itemArr[i].update(bannerCardBean, gameListItem, bannerCardBean.getButtonText());
            }
        }
    }

    public int b() {
        return R.layout.gbg_card_game_list_vertical;
    }
}
