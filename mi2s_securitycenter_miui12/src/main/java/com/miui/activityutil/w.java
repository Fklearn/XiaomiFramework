package com.miui.activityutil;

import android.content.Context;
import android.content.pm.PackageManager;
import java.io.File;

public class w {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2326a = "com.miui.activityutil.w";

    /* renamed from: b  reason: collision with root package name */
    private static final boolean f2327b = false;

    private static void a(Context context, z zVar) {
        String packageName = context.getPackageName();
        String unused = zVar.h = packageName;
        try {
            String unused2 = zVar.i = String.valueOf(context.getPackageManager().getPackageInfo(packageName, 0).versionCode);
        } catch (PackageManager.NameNotFoundException unused3) {
        }
    }

    public final void a(Context context, String[] strArr, File file, x xVar) {
        z zVar = new z(this, strArr, file, xVar);
        a(context, zVar);
        zVar.start();
    }

    public final void a(Context context, String[] strArr, byte[] bArr, x xVar) {
        z zVar = new z(this, strArr, bArr, xVar);
        a(context, zVar);
        zVar.start();
    }
}
