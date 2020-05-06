package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;

/* compiled from: lambda */
public final /* synthetic */ class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    private final /* synthetic */ BannerCardBean f4402a;

    /* renamed from: b  reason: collision with root package name */
    private final /* synthetic */ GameListItem f4403b;

    /* renamed from: c  reason: collision with root package name */
    private final /* synthetic */ Context f4404c;

    public /* synthetic */ c(BannerCardBean bannerCardBean, GameListItem gameListItem, Context context) {
        this.f4402a = bannerCardBean;
        this.f4403b = gameListItem;
        this.f4404c = context;
    }

    public final void run() {
        g.a(this.f4402a, this.f4403b, this.f4404c);
    }
}
