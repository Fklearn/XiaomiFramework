package com.miui.gamebooster.m;

import android.content.Context;
import android.os.Build;
import android.system.Os;
import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import miui.util.IOUtils;

/* renamed from: com.miui.gamebooster.m.m  reason: case insensitive filesystem */
public class C0382m {
    public static long a(String str) {
        if (TextUtils.isEmpty(str) || Build.VERSION.SDK_INT < 21) {
            return 0;
        }
        try {
            if (new File(str).exists()) {
                return Os.stat(str).st_size;
            }
            return 0;
        } catch (Exception e) {
            Log.e("FileUtils", "get file size error " + e);
            return 0;
        }
    }

    /* JADX INFO: finally extract failed */
    private static String a(FileInputStream fileInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                int read = fileInputStream.read(bArr, 0, 1024);
                if (read > 0) {
                    byteArrayOutputStream.write(bArr, 0, read);
                } else {
                    String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                    IOUtils.closeQuietly(byteArrayOutputStream);
                    return byteArrayOutputStream2;
                }
            } catch (Exception unused) {
                IOUtils.closeQuietly(byteArrayOutputStream);
                return null;
            } catch (Throwable th) {
                IOUtils.closeQuietly(byteArrayOutputStream);
                throw th;
            }
        }
    }

    public static ArrayList<String> a(String str, Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(str)));
            while (true) {
                try {
                    String readLine = bufferedReader2.readLine();
                    if (readLine == null) {
                        break;
                    }
                    arrayList.add(readLine);
                } catch (IOException unused) {
                    bufferedReader = bufferedReader2;
                    try {
                        Log.e("FileUtils", "getlistfromfile fail!");
                        IOUtils.closeQuietly(bufferedReader);
                        return arrayList;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(bufferedReader);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = bufferedReader2;
                    IOUtils.closeQuietly(bufferedReader);
                    throw th;
                }
            }
            IOUtils.closeQuietly(bufferedReader2);
        } catch (IOException unused2) {
            Log.e("FileUtils", "getlistfromfile fail!");
            IOUtils.closeQuietly(bufferedReader);
            return arrayList;
        }
        return arrayList;
    }

    public static void a(String str, String str2, String str3, Context context) {
        File file = new File(context.getFilesDir().getPath() + File.separator + str);
        file.mkdir();
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file, str2))));
            try {
                bufferedWriter2.write(str3);
                IOUtils.closeQuietly(bufferedWriter2);
            } catch (IOException unused) {
                bufferedWriter = bufferedWriter2;
                try {
                    Log.e("FileUtils", "writePackageListToData fail!");
                    IOUtils.closeQuietly(bufferedWriter);
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(bufferedWriter);
                    throw th;
                }
            } catch (Throwable th2) {
                BufferedWriter bufferedWriter3 = bufferedWriter2;
                th = th2;
                bufferedWriter = bufferedWriter3;
                IOUtils.closeQuietly(bufferedWriter);
                throw th;
            }
        } catch (IOException unused2) {
            Log.e("FileUtils", "writePackageListToData fail!");
            IOUtils.closeQuietly(bufferedWriter);
        }
    }

    public static void a(String str, String str2, ArrayList<String> arrayList, Context context) {
        File file = new File(context.getFilesDir().getPath() + File.separator + str);
        file.mkdir();
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file, str2))));
            try {
                Iterator<String> it = arrayList.iterator();
                while (it.hasNext()) {
                    bufferedWriter2.write(it.next());
                    bufferedWriter2.newLine();
                }
                IOUtils.closeQuietly(bufferedWriter2);
            } catch (IOException unused) {
                bufferedWriter = bufferedWriter2;
                try {
                    Log.e("FileUtils", "writePackageListToData fail!");
                    IOUtils.closeQuietly(bufferedWriter);
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(bufferedWriter);
                    throw th;
                }
            } catch (Throwable th2) {
                BufferedWriter bufferedWriter3 = bufferedWriter2;
                th = th2;
                bufferedWriter = bufferedWriter3;
                IOUtils.closeQuietly(bufferedWriter);
                throw th;
            }
        } catch (IOException unused2) {
            Log.e("FileUtils", "writePackageListToData fail!");
            IOUtils.closeQuietly(bufferedWriter);
        }
    }

    public static boolean a(String str, String str2, Context context) {
        File file = new File(context.getFilesDir().getPath() + File.separator + str + File.separator + str2);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: java.io.FileOutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: java.io.File} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: java.io.File} */
    /* JADX WARNING: type inference failed for: r0v2, types: [java.io.OutputStream] */
    /* JADX WARNING: type inference failed for: r0v5 */
    /* JADX WARNING: type inference failed for: r0v6, types: [java.io.OutputStream] */
    /* JADX WARNING: type inference failed for: r0v12 */
    /* JADX WARNING: type inference failed for: r0v15 */
    /* JADX WARNING: type inference failed for: r0v16 */
    /* JADX WARNING: type inference failed for: r0v17 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 2 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String b(java.lang.String r7) {
        /*
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            java.io.File r0 = new java.io.File
            r0.<init>(r7)
            boolean r7 = r0.exists()
            if (r7 == 0) goto L_0x0084
            java.io.File r7 = com.miui.gamebooster.m.C0378i.a()
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r0.getName()
            r2.<init>(r7, r3)
            boolean r7 = r2.exists()     // Catch:{ Exception -> 0x0062, all -> 0x005d }
            if (r7 != 0) goto L_0x004e
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0062, all -> 0x005d }
            r7.<init>(r0)     // Catch:{ Exception -> 0x0062, all -> 0x005d }
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x004b, all -> 0x0046 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x004b, all -> 0x0046 }
            int r3 = r7.available()     // Catch:{ Exception -> 0x005b }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x005b }
        L_0x0036:
            int r4 = r7.read(r3)     // Catch:{ Exception -> 0x005b }
            r5 = -1
            if (r4 == r5) goto L_0x0042
            r5 = 0
            r0.write(r3, r5, r4)     // Catch:{ Exception -> 0x005b }
            goto L_0x0036
        L_0x0042:
            r0.flush()     // Catch:{ Exception -> 0x005b }
            goto L_0x0050
        L_0x0046:
            r0 = move-exception
            r6 = r1
            r1 = r0
            r0 = r6
            goto L_0x007d
        L_0x004b:
            r2 = move-exception
            r0 = r1
            goto L_0x0065
        L_0x004e:
            r7 = r1
            r0 = r7
        L_0x0050:
            java.lang.String r1 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x005b }
        L_0x0054:
            miui.util.IOUtils.closeQuietly(r7)
            miui.util.IOUtils.closeQuietly(r0)
            goto L_0x0084
        L_0x005b:
            r2 = move-exception
            goto L_0x0065
        L_0x005d:
            r7 = move-exception
            r0 = r1
            r1 = r7
            r7 = r0
            goto L_0x007d
        L_0x0062:
            r2 = move-exception
            r7 = r1
            r0 = r7
        L_0x0065:
            java.lang.String r3 = "FileUtils"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x007c }
            r4.<init>()     // Catch:{ all -> 0x007c }
            java.lang.String r5 = "save file error "
            r4.append(r5)     // Catch:{ all -> 0x007c }
            r4.append(r2)     // Catch:{ all -> 0x007c }
            java.lang.String r2 = r4.toString()     // Catch:{ all -> 0x007c }
            android.util.Log.e(r3, r2)     // Catch:{ all -> 0x007c }
            goto L_0x0054
        L_0x007c:
            r1 = move-exception
        L_0x007d:
            miui.util.IOUtils.closeQuietly(r7)
            miui.util.IOUtils.closeQuietly(r0)
            throw r1
        L_0x0084:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.C0382m.b(java.lang.String):java.lang.String");
    }

    public static String b(String str, String str2, Context context) {
        FileInputStream fileInputStream;
        String str3 = null;
        try {
            fileInputStream = new FileInputStream(context.getFilesDir().getPath() + File.separator + str + File.separator + str2);
            try {
                str3 = a(fileInputStream);
            } catch (IOException unused) {
                try {
                    Log.e("FileUtils", "getPackageListFromData fail!");
                    IOUtils.closeQuietly(fileInputStream);
                    return str3;
                } catch (Throwable th) {
                    th = th;
                }
            }
        } catch (IOException unused2) {
            fileInputStream = null;
            Log.e("FileUtils", "getPackageListFromData fail!");
            IOUtils.closeQuietly(fileInputStream);
            return str3;
        } catch (Throwable th2) {
            th = th2;
            fileInputStream = null;
            IOUtils.closeQuietly(fileInputStream);
            throw th;
        }
        IOUtils.closeQuietly(fileInputStream);
        return str3;
    }

    public static ArrayList<String> c(String str, String str2, Context context) {
        String str3 = context.getFilesDir().getPath() + File.separator + str + File.separator + str2;
        ArrayList<String> arrayList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(str3)));
            while (true) {
                try {
                    String readLine = bufferedReader2.readLine();
                    if (readLine == null) {
                        break;
                    }
                    arrayList.add(readLine);
                } catch (IOException unused) {
                    bufferedReader = bufferedReader2;
                    try {
                        Log.e("FileUtils", "getPackageListFromData fail!");
                        IOUtils.closeQuietly(bufferedReader);
                        return arrayList;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(bufferedReader);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    bufferedReader = bufferedReader2;
                    IOUtils.closeQuietly(bufferedReader);
                    throw th;
                }
            }
            IOUtils.closeQuietly(bufferedReader2);
        } catch (IOException unused2) {
            Log.e("FileUtils", "getPackageListFromData fail!");
            IOUtils.closeQuietly(bufferedReader);
            return arrayList;
        }
        return arrayList;
    }
}
