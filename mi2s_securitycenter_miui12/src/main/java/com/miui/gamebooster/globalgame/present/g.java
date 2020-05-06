package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import b.b.c.j.r;
import b.c.a.b.d;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.GameListItem;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.globalgame.view.RoundedImageView;
import com.miui.gamebooster.i.a.c;
import com.miui.securitycenter.R;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static final d f4411a;

    /* renamed from: b  reason: collision with root package name */
    private static final d f4412b;

    public static class a implements b.c.a.b.f.a {
        public void a(String str, View view) {
        }

        public void a(String str, View view, Bitmap bitmap) {
        }

        public void b(String str, View view) {
        }
    }

    static {
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(true);
        aVar.c(true);
        f4411a = aVar.a();
        d.a aVar2 = new d.a();
        aVar2.a(true);
        aVar2.b(true);
        aVar2.c(true);
        aVar2.c((int) R.drawable.gb_def_icon);
        aVar2.a((int) R.drawable.gb_def_icon);
        aVar2.b((int) R.drawable.gb_def_icon);
        f4412b = aVar2.a();
    }

    private static int a(Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.gbg_card_button_radius);
    }

    public static void a(Context context, ImageView imageView) {
        int a2 = a(context);
        if (imageView instanceof RoundedImageView) {
            ((RoundedImageView) imageView).setCornerRadius((float) a2);
        }
    }

    public static void a(Context context, BannerCardBean bannerCardBean, GameListItem gameListItem, View... viewArr) {
        Utils.a((Runnable) new c(bannerCardBean, gameListItem, context), viewArr);
    }

    public static void a(Context context, String str, ImageView imageView) {
        a(context, imageView);
        r.a(str, imageView, f4411a);
    }

    public static void a(Context context, String str, ImageView imageView, int i) {
        if (imageView instanceof RoundedImageView) {
            ((RoundedImageView) imageView).setCornerRadius((float) i);
        }
        r.a(str, imageView, f4412b);
    }

    public static void a(Context context, String str, ImageView imageView, d dVar, @Nullable Runnable runnable) {
        a(context, imageView);
        r.a(str, imageView, dVar, (b.c.a.b.f.a) new f(runnable));
    }

    static /* synthetic */ void a(BannerCardBean bannerCardBean, GameListItem gameListItem, Context context) {
        String str;
        String str2;
        String str3 = "";
        if (!TextUtils.isEmpty(bannerCardBean.getLink())) {
            str2 = bannerCardBean.getLink();
            str = bannerCardBean.getTitle();
        } else if (!TextUtils.isEmpty(gameListItem.getGameLink())) {
            str2 = gameListItem.getGameLink();
            str = gameListItem.getName();
        } else {
            str2 = str3;
            str = str2;
        }
        if (!TextUtils.isEmpty(str2)) {
            if (Utils.c(str2)) {
                Utils.a(context, str2);
            } else {
                if (TextUtils.isEmpty(str)) {
                    str = context.getString(R.string.app_name);
                }
                Utils.a(context, str2, str);
            }
        }
        int i = bannerCardBean.index;
        String str4 = bannerCardBean.title;
        int i2 = bannerCardBean.type;
        if (gameListItem != null) {
            str3 = gameListItem.name;
        }
        c.a(i, str4, i2, str3, gameListItem == null ? 1 : gameListItem.gameIndex);
    }

    public static void b(Context context, String str, ImageView imageView) {
        int a2 = a(context);
        if (imageView instanceof RoundedImageView) {
            float f = (float) a2;
            ((RoundedImageView) imageView).setCornerRadius(f, f, 0.0f, 0.0f);
        }
        r.a(str, imageView, f4411a);
    }
}
