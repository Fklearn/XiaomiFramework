package com.miui.maml.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;

public class PortableUtils {
    public static Drawable getUserBadgedIcon(Context context, Drawable drawable, UserHandle userHandle) {
        return context.getPackageManager().getUserBadgedIcon(drawable, userHandle);
    }
}
