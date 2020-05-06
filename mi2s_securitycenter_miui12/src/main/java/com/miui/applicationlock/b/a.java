package com.miui.applicationlock.b;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import b.b.c.j.r;
import b.b.n.e;
import b.b.n.g;
import b.b.n.l;
import b.c.a.b.d.d;
import com.miui.common.customview.gif.GifImageView;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;
import com.miui.systemAdSolution.common.AdTrackType;
import java.io.File;

public class a {
    private static Bitmap a(Context context, Bitmap bitmap, int i) {
        float dimensionPixelSize = (((float) context.getResources().getDimensionPixelSize(i)) * 1.0f) / ((float) bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(dimensionPixelSize, dimensionPixelSize);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static Bitmap a(String str) {
        Bitmap a2;
        if (TextUtils.isEmpty(str) || (a2 = r.a(d.a.FILE.c(str), r.f)) == null) {
            return null;
        }
        return a2;
    }

    public static void a(Context context, b bVar, GifImageView gifImageView, ImageView imageView) {
        g.a a2 = bVar.a(209);
        g.a a3 = bVar.a(210);
        e a4 = e.a(context);
        if (a2 != null) {
            a(context, gifImageView, a2, R.dimen.skin_flag_end, "key_flag_top_gif_adinfo_id", bVar.b());
            a4.a("applicationlock", "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_VIEW, 209);
        }
        String c2 = a3 != null ? a3.c() : "";
        if (!TextUtils.isEmpty(c2) && new File(c2).exists()) {
            a(c2, imageView);
            a4.a("applicationlock", "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_VIEW, 210);
        }
    }

    private static void a(Context context, GifImageView gifImageView, g.a aVar, int i, String str, long j) {
        Bitmap a2;
        if (!TextUtils.isEmpty(aVar.c())) {
            File file = new File(aVar.c());
            if (file.exists()) {
                long a3 = b.a(str, 0);
                if (!aVar.e() || a3 == j || !l.a(gifImageView, file, context, i, 1)) {
                    if (aVar.e()) {
                        Bitmap a4 = l.a(file, context);
                        if (a4 != null && i != -1 && (a2 = a(context, a4, i)) != null) {
                            gifImageView.setImageBitmap(a2);
                        } else {
                            return;
                        }
                    } else {
                        a(aVar.c(), gifImageView);
                    }
                    gifImageView.setOnClickListener((View.OnClickListener) context);
                    return;
                }
                gifImageView.setOnClickListener((View.OnClickListener) context);
                b.b(str, j);
            }
        }
    }

    public static void a(String str, ImageView imageView) {
        if (!TextUtils.isEmpty(str)) {
            r.a(d.a.FILE.c(str), imageView, r.f1759c);
        }
    }

    public static boolean a(b bVar) {
        long a2 = b.a("flag_shake_adinfo_id", 0);
        if (bVar == null || a2 == bVar.b()) {
            return false;
        }
        b.b("flag_shake_adinfo_id", bVar.b());
        return true;
    }

    public static Bitmap[] a(Context context, b bVar, int i) {
        if (bVar == null) {
            return null;
        }
        Bitmap[] bitmapArr = new Bitmap[9];
        long j = 0;
        int i2 = 0;
        while (i2 < 3) {
            long j2 = j;
            int i3 = 0;
            while (i3 < 3) {
                long j3 = 1 + j2;
                g.a a2 = bVar.a(((long) i) + 200 + j2);
                if (a2 != null && !TextUtils.isEmpty(a2.c()) && new File(a2.c()).exists()) {
                    int i4 = (i2 * 3) + i3;
                    Bitmap a3 = a(a2.c());
                    if (a3 != null) {
                        bitmapArr[i4] = a(context, a3, (int) R.dimen.skin_pattern);
                        i3++;
                        j2 = j3;
                    }
                }
                return null;
            }
            i2++;
            j = j2;
        }
        return bitmapArr;
    }
}
