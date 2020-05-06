package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import android.view.ViewGroup;
import com.market.sdk.utils.b;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.gamebooster.globalgame.view.GameItemHub;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class H5OneRowList implements a {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static int[] f4396a = {R.id.gameItemHub00};

    @Keep
    public static class VH extends GlobalCardVH {
        ViewGroup container;
        GameItemHub[] hubArr;

        public void custom(View view, boolean z, boolean z2) {
            super.custom(view, z, z2);
            this.container = (ViewGroup) this.rootView.findViewById(R.id.container);
            int limit = getLimit() / 4;
            this.hubArr = new GameItemHub[limit];
            for (int i = 0; i < limit; i++) {
                this.hubArr[i] = (GameItemHub) this.rootView.findViewById(getIdArr()[i]);
            }
        }

        /* access modifiers changed from: protected */
        public int[] getIdArr() {
            return H5OneRowList.f4396a;
        }

        /* access modifiers changed from: protected */
        public int getLimit() {
            return 4;
        }
    }

    private static <H extends VH> void a(Context context, View view, List<GameListItem> list, BannerCardBean bannerCardBean) {
        VH vh = (VH) view.getTag();
        int size = (list.size() / 4) + (list.size() % 4 == 0 ? 0 : 1);
        for (int i = 0; i < size; i++) {
            vh.hubArr[i].vanishDetail();
            int i2 = i * 4;
            vh.hubArr[i].update(context, view, bannerCardBean, list.subList(i2, Math.min(i2 + 4, list.size())));
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        List<GameListItem> gameList = bannerCardBean.getGameList();
        if (!b.a(gameList)) {
            ArrayList arrayList = new ArrayList();
            int min = Math.min(gameList.size(), d());
            for (int i = 0; i < min; i++) {
                GameListItem gameListItem = gameList.get(i);
                gameListItem.setGameIndex(i);
                arrayList.add(gameListItem);
            }
            a(context, view, arrayList, bannerCardBean);
        }
    }

    public int b() {
        return R.layout.gbg_card_h5_1row_list;
    }

    public int d() {
        return 4;
    }
}
