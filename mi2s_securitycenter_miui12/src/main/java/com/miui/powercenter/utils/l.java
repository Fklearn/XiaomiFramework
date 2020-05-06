package com.miui.powercenter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import com.miui.securitycenter.R;
import miui.content.res.IconCustomizer;

public class l {
    public static Bitmap a(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.pc_power_center_icon_bg, options).copy(Bitmap.Config.ARGB_8888, true);
    }

    public static Bitmap b(Context context) {
        return IconCustomizer.generateIconStyleDrawable(new BitmapDrawable(context.getResources(), a(context)), true).getBitmap();
    }
}
