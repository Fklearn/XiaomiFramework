package com.miui.gamebooster.m;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;
import miui.util.IOUtils;

/* renamed from: com.miui.gamebooster.m.e  reason: case insensitive filesystem */
public class C0374e {
    public static String a(Context context, String str) {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(str);
            try {
                String iOUtils = IOUtils.toString(inputStream);
                IOUtils.closeQuietly(inputStream);
                return iOUtils;
            } catch (IOException e) {
                e = e;
                try {
                    e.printStackTrace();
                    IOUtils.closeQuietly(inputStream);
                    return null;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(inputStream);
                    throw th;
                }
            }
        } catch (IOException e2) {
            e = e2;
            inputStream = null;
            e.printStackTrace();
            IOUtils.closeQuietly(inputStream);
            return null;
        } catch (Throwable th2) {
            th = th2;
            inputStream = null;
            IOUtils.closeQuietly(inputStream);
            throw th;
        }
    }
}
