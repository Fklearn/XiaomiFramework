package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.market.sdk.utils.b;
import com.miui.gamebooster.globalgame.global.CoverRatioFixedVH;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.gamebooster.globalgame.view.GameItemView;
import com.miui.securitycenter.R;

public class BigPost implements a {

    @Keep
    public static class VH extends CoverRatioFixedVH {
        public static final float HW_RATIO = 0.65f;
        GameItemView gameItem;
        TextView title;

        public void custom(View view, boolean z, boolean z2) {
            super.custom(view, z, z2);
            this.title = (TextView) view.findViewById(R.id.title);
            this.gameItem = (GameItemView) view.findViewById(R.id.gameItemView);
        }

        /* access modifiers changed from: protected */
        public String keyForStore() {
            return "gbg_key_cover_height_big_post_0x07";
        }

        /* access modifiers changed from: protected */
        public float parseRatio() {
            return 0.65f;
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        VH vh = (VH) view.getTag();
        vh.refreshPadding(bannerCardBean.isFirst, bannerCardBean.isLast);
        Bitmap bitmap = bannerCardBean.loadedBitmap;
        if (bitmap != null && !bitmap.isRecycled()) {
            g.a(context, vh.cover);
            vh.cover.setImageBitmap(bannerCardBean.loadedBitmap);
        } else if (!TextUtils.isEmpty(bannerCardBean.getCover())) {
            g.a(context, bannerCardBean.getCover(), vh.cover);
        }
        vh.title.setText(bannerCardBean.getTitle());
        if (!b.a(bannerCardBean.getGameList())) {
            GameListItem gameListItem = bannerCardBean.getGameList().get(0);
            vh.gameItem.update(bannerCardBean, gameListItem, bannerCardBean.getButtonText());
            g.a(context, bannerCardBean, gameListItem, vh.cover);
        }
    }

    public int b() {
        return R.layout.gbg_card_post;
    }
}
