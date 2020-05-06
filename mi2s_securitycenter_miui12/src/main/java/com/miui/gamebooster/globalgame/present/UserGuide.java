package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import com.miui.gamebooster.globalgame.global.CoverRatioFixedVH;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.securitycenter.R;

public class UserGuide implements a {

    @Keep
    public static class VH extends CoverRatioFixedVH {
        View close;

        public void custom(View view, boolean z, boolean z2) {
            super.custom(view, z, z2);
            this.close = view.findViewById(R.id.close);
        }

        /* access modifiers changed from: protected */
        public String keyForStore() {
            return "gbg_key_cover_height_user_guide_0x05";
        }

        /* access modifiers changed from: protected */
        public float parseRatio() {
            return 0.29444444f;
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        VH vh = (VH) view.getTag();
        vh.refreshPadding(bannerCardBean.isFirst, bannerCardBean.isLast);
        g.a(context, "https://g0.market.mi-img.com/download/webp/GlobalGameBooster/097c141027710d1de9da4c6b9492c6bb388435e99/a.png", vh.cover);
        ViewGroup viewGroup = (ViewGroup) view;
        viewGroup.getClass();
        Utils.a((Runnable) new d(viewGroup), vh.close);
    }

    @LayoutRes
    public int b() {
        return R.layout.gbg_user_guide;
    }
}
