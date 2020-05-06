package com.miui.securityscan.i;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import miui.util.IOUtils;

public class h {
    public static void a(Context context, String str) {
        try {
            File file = new File(context.getFilesDir(), str);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void a(Context context, String str, String str2) {
        FileOutputStream fileOutputStream;
        PrintWriter printWriter = null;
        try {
            fileOutputStream = context.openFileOutput(str, 0);
            try {
                PrintWriter printWriter2 = new PrintWriter(fileOutputStream);
                try {
                    printWriter2.write(str2);
                    IOUtils.closeQuietly(printWriter2);
                    IOUtils.closeQuietly(fileOutputStream);
                } catch (Throwable th) {
                    th = th;
                    printWriter = printWriter2;
                    IOUtils.closeQuietly(printWriter);
                    IOUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(printWriter);
                IOUtils.closeQuietly(fileOutputStream);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            fileOutputStream = null;
            IOUtils.closeQuietly(printWriter);
            IOUtils.closeQuietly(fileOutputStream);
            throw th;
        }
    }

    public static String b(Context context, String str) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        try {
            if (new File(context.getFilesDir(), str).exists()) {
                fileInputStream = context.openFileInput(str);
                try {
                    String iOUtils = IOUtils.toString(fileInputStream);
                    IOUtils.closeQuietly(fileInputStream);
                    return iOUtils;
                } catch (Exception e) {
                    e = e;
                    try {
                        e.printStackTrace();
                        IOUtils.closeQuietly(fileInputStream);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        fileInputStream2 = fileInputStream;
                        IOUtils.closeQuietly(fileInputStream2);
                        throw th;
                    }
                }
            } else {
                IOUtils.closeQuietly((InputStream) null);
                return null;
            }
        } catch (Exception e2) {
            e = e2;
            fileInputStream = null;
            e.printStackTrace();
            IOUtils.closeQuietly(fileInputStream);
            return null;
        } catch (Throwable th2) {
            th = th2;
            IOUtils.closeQuietly(fileInputStream2);
            throw th;
        }
    }
}
