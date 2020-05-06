package com.miui.hybrid.accessory.sdk.icondialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Pair;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.io.ByteArrayInputStream;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final float[] f5601a = {1.5f, 2.0f, 2.75f, 3.0f, 4.0f};

    /* renamed from: b  reason: collision with root package name */
    private static final int[] f5602b = {90, 136, 168, PsExtractor.AUDIO_STREAM, 224};

    private static int a(Context context) {
        float f = context.getResources().getDisplayMetrics().density;
        if (f <= f5601a[0]) {
            return 0;
        }
        int i = 1;
        while (true) {
            float[] fArr = f5601a;
            if (i >= fArr.length) {
                return fArr.length - 1;
            }
            if (f <= fArr[i]) {
                int i2 = i - 1;
                return fArr[i] - f < f - fArr[i2] ? i : i2;
            }
            i++;
        }
    }

    public static Bitmap a(Context context, byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        return a(bArr, f5602b[a(context)]);
    }

    private static Bitmap a(byte[] bArr, int i) {
        Pair<Integer, Integer> a2 = a(bArr);
        if (a2 == null) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        int intValue = ((Integer) a2.first).intValue();
        int intValue2 = ((Integer) a2.second).intValue();
        if (intValue == i && intValue2 == i) {
            return BitmapFactory.decodeStream(byteArrayInputStream, (Rect) null, (BitmapFactory.Options) null);
        }
        int min = Math.min(intValue, intValue2) / i;
        if (min == 0) {
            min = 1;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = min;
        return BitmapFactory.decodeStream(byteArrayInputStream, (Rect) null, options);
    }

    private static Pair<Integer, Integer> a(byte[] bArr) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new ByteArrayInputStream(bArr), (Rect) null, options);
        int i = options.outWidth;
        if (i == 0 || options.outHeight == 0) {
            return null;
        }
        return new Pair<>(Integer.valueOf(i), Integer.valueOf(options.outHeight));
    }
}
