package com.miui.gamebooster.videobox.utils;

import android.util.Log;
import b.b.c.j.y;
import java.util.ArrayList;
import java.util.List;
import miui.security.SecurityManager;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f5205a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static final List<String> f5206b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private static final String f5207c = "true";

    /* renamed from: d  reason: collision with root package name */
    private static final String f5208d = "false";

    static {
        f5205a.add(SecurityManager.SKIP_INTERCEPT_PACKAGE);
        f5205a.add("com.miui.video");
        f5205a.add("com.qiyi.video");
        f5205a.add("tv.pps.mobile");
        f5205a.add("air.tv.douyu.android");
        f5205a.add("com.ss.android.ugc.aweme");
        f5205a.add("com.tencent.qqlive");
        f5205a.add("com.tencent.qqsports");
        f5205a.add("com.duowan.kiwi");
        f5205a.add("com.youku.phone");
        f5205a.add("com.cmcc.cmvideo");
        f5205a.add("com.tencent.weishi");
        f5205a.add("com.mxtech.videoplayer.ad");
        f5205a.add("tv.danmaku.bili");
        f5205a.add("com.sina.weibo");
        f5205a.add("com.ss.android.article.video");
        f5205a.add("com.miui.videoplayer");
        f5205a.add("com.iqiyi.i18n");
        f5205a.add("com.ss.android.ugc.aweme.lite");
    }

    public static void a(boolean z) {
        Log.i("VideoEffectUtils", "setVppEnable: " + z);
        y.b("debug.media.vpp.enable", z ? f5207c : f5208d);
    }

    public static boolean a() {
        return f5207c.equals(y.a("ro.vendor.media.video.vpp.support", f5208d));
    }

    public static boolean a(String str) {
        if (f5206b.isEmpty()) {
            f5206b.addAll(com.miui.gamebooster.videobox.settings.f.g());
        }
        return f5206b.contains(str) || f5205a.contains(str);
    }

    public static void b(boolean z) {
        Log.i("VideoEffectUtils", "setVppStatus: " + z + "\tisSupportVpp=" + a());
        if (a(com.miui.gamebooster.videobox.settings.f.a())) {
            y.b("debug.media.video.vpp", z ? f5207c : f5208d);
        }
    }
}
