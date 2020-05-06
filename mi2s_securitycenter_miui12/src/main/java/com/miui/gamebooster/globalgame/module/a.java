package com.miui.gamebooster.globalgame.module;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import com.miui.gamebooster.globalgame.global.GlobalCardVH;

public interface a {
    Class<? extends GlobalCardVH> a();

    void a(Context context, View view, BannerCardBean bannerCardBean);

    @LayoutRes
    int b();
}
