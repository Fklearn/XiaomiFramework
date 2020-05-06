package com.miui.optimizemanage.b;

import android.util.Log;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import miui.util.IOUtils;

public class d {
    public static int a(String str) {
        try {
            return ((Integer) Class.forName("android.os.FileUtils").getMethod("getUid", new Class[]{String.class}).invoke((Object) null, new Object[]{str})).intValue();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e("ProcessWrapper", "getFileUid", e);
            Log.e("ProcessWrapper", "Fail to get file uid, " + str);
            return 0;
        }
    }

    public static boolean a(String str, int[] iArr, String[] strArr, long[] jArr, float[] fArr) {
        try {
            return ((Boolean) Class.forName("android.os.Process").getMethod("readProcFile", new Class[]{String.class, int[].class, String[].class, long[].class, float[].class}).invoke((Object) null, new Object[]{str, iArr, strArr, jArr, fArr})).booleanValue();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e("ProcessWrapper", "readProcFile", e);
            return false;
        }
    }

    public static int[] a(String str, int[] iArr) {
        try {
            return (int[]) Class.forName("android.os.Process").getMethod("getPids", new Class[]{String.class, int[].class}).invoke((Object) null, new Object[]{str, iArr});
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            Log.e("ProcessWrapper", "getPids", e);
            return null;
        }
    }

    public static int b(String str) {
        BufferedReader bufferedReader;
        BufferedReader bufferedReader2 = null;
        int i = 0;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(str)));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    } else if (readLine.indexOf("Uid:") == 0) {
                        String[] split = readLine.split("\t");
                        if (split.length > 1) {
                            i = Integer.valueOf(split[1]).intValue();
                        }
                    }
                } catch (FileNotFoundException unused) {
                } catch (Exception e) {
                    e = e;
                    bufferedReader2 = bufferedReader;
                    try {
                        Log.e("ProcessWrapper", "getProcUid", e);
                        IOUtils.closeQuietly(bufferedReader2);
                        return i;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(bufferedReader2);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader2 = bufferedReader;
                    IOUtils.closeQuietly(bufferedReader2);
                    throw th;
                }
            }
        } catch (FileNotFoundException unused2) {
            bufferedReader = null;
        } catch (Exception e2) {
            e = e2;
            Log.e("ProcessWrapper", "getProcUid", e);
            IOUtils.closeQuietly(bufferedReader2);
            return i;
        }
        IOUtils.closeQuietly(bufferedReader);
        return i;
    }
}
