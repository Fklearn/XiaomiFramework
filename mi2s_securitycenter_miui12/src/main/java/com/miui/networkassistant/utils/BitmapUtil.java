package com.miui.networkassistant.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import java.io.File;

public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    @TargetApi(21)
    public static Bitmap getSvgBitmap(Context context, int i) {
        Drawable drawable = ContextCompat.getDrawable(context, i);
        if (drawable instanceof VectorDrawable) {
            Bitmap createBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return createBitmap;
        }
        Log.e(TAG, "image not svg");
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0024 A[SYNTHETIC, Splitter:B:11:0x0024] */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean saveBitmapToFile(java.io.File r2, java.lang.String r3, android.graphics.Bitmap r4, android.graphics.Bitmap.CompressFormat r5, int r6) {
        /*
            java.io.File r0 = new java.io.File
            r0.<init>(r2, r3)
            r2 = 0
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0015 }
            r3.<init>(r0)     // Catch:{ IOException -> 0x0015 }
            r4.compress(r5, r6, r3)     // Catch:{ IOException -> 0x0013 }
            r3.close()     // Catch:{ IOException -> 0x0013 }
            r2 = 1
            return r2
        L_0x0013:
            r2 = move-exception
            goto L_0x0019
        L_0x0015:
            r3 = move-exception
            r1 = r3
            r3 = r2
            r2 = r1
        L_0x0019:
            java.lang.String r2 = r2.getMessage()
            java.lang.String r4 = "BitmapUtil"
            android.util.Log.e(r4, r2)
            if (r3 == 0) goto L_0x002c
            r3.close()     // Catch:{ IOException -> 0x0028 }
            goto L_0x002c
        L_0x0028:
            r2 = move-exception
            r2.printStackTrace()
        L_0x002c:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.utils.BitmapUtil.saveBitmapToFile(java.io.File, java.lang.String, android.graphics.Bitmap, android.graphics.Bitmap$CompressFormat, int):boolean");
    }

    public static void saveDrawableResToFile(Context context, File file, String str, int i) {
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i);
        if (!file.exists() ? file.mkdirs() : true) {
            saveBitmapToFile(file, str, decodeResource, Bitmap.CompressFormat.PNG, 100);
        } else {
            Log.e(TAG, "Couldn't create target directory.");
        }
    }
}
