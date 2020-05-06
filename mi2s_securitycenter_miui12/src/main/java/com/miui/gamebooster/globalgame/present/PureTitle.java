package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.support.annotation.Keep;
import android.view.View;
import android.widget.TextView;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.securitycenter.R;

public class PureTitle implements a {

    @Keep
    public static class VH extends GlobalCardVH {
        TextView title;

        public void custom(View view, boolean z, boolean z2) {
            super.custom(view, z, z2);
            this.title = (TextView) view.findViewById(R.id.title);
        }
    }

    public Class<? extends GlobalCardVH> a() {
        return VH.class;
    }

    public void a(Context context, View view, BannerCardBean bannerCardBean) {
        VH vh = (VH) view.getTag();
        vh.refreshPadding(bannerCardBean.isFirst, bannerCardBean.isLast);
        vh.title.setText(bannerCardBean.getTopTitle());
    }

    public int b() {
        return R.layout.gbg_pure_title;
    }
}
