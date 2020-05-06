package com.miui.luckymoney.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import miui.util.IOUtils;

public class ImageUtil {
    public static final int REVERSE_TYPE_HORIZONTAL = 0;
    public static final int REVERSE_TYPE_VERTICAL = 1;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap createBitmap = (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) ? Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) : Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return createBitmap;
    }

    public static Bitmap loadBitmapfromFile(File file, Context context) {
        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        if (file == null) {
            return null;
        }
        try {
            fileInputStream = new FileInputStream(file);
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeStream(fileInputStream, (Rect) null, options);
            } catch (FileNotFoundException e) {
                e = e;
                try {
                    e.printStackTrace();
                    IOUtils.closeQuietly(fileInputStream);
                    return bitmap;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(fileInputStream);
                    throw th;
                }
            }
        } catch (FileNotFoundException e2) {
            e = e2;
            fileInputStream = null;
            e.printStackTrace();
            IOUtils.closeQuietly(fileInputStream);
            return bitmap;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
            IOUtils.closeQuietly(fileInputStream);
            throw th;
        }
        IOUtils.closeQuietly(fileInputStream);
        return bitmap;
    }
}
