package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import com.miui.gamebooster.globalgame.global.CoverRatioFixedVH;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.securitycenter.R;

public class PureImage implements a {

    @Keep
    public static class VH extends CoverRatioFixedVH {
        /* access modifiers changed from: protected */
        public String keyForStore() {
            return "gbg_key_cover_height_pure_image_0x06";
        }

        /* access modifiers changed from: protected */
        public float parseRatio() {
            return 0.5277778f;
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        VH vh = (VH) view.getTag();
        vh.refreshPadding(bannerCardBean.isFirst, bannerCardBean.isLast);
        if (bannerCardBean.getCover() != null) {
            g.a(context, bannerCardBean.getCover(), vh.cover);
        }
        g.a(context, bannerCardBean, (GameListItem) null, vh.cover);
    }

    public int b() {
        return R.layout.gbg_pure_image;
    }
}
