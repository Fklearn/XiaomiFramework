package com.miui.antispam.policy.a;

import android.content.Context;
import android.util.Log;
import b.d.e.a;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import miui.os.Build;

public class b {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public Context f2359a;

    /* renamed from: b  reason: collision with root package name */
    private a f2360b;

    /* renamed from: c  reason: collision with root package name */
    private ExecutorService f2361c = Executors.newSingleThreadExecutor();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f2362d;

    public b(Context context) {
        this.f2359a = context.getApplicationContext();
        this.f2360b = new a();
        this.f2362d = this.f2359a.getFilesDir().getPath() + File.separator + "SmsEngineResource" + File.separator;
    }

    private void a(File file) {
        File[] listFiles;
        if (file.isDirectory() && (listFiles = file.listFiles()) != null && listFiles.length > 0) {
            for (File a2 : listFiles) {
                a(a2);
            }
        }
        file.delete();
    }

    private void a(ZipInputStream zipInputStream, String str) {
        FileOutputStream fileOutputStream;
        while (true) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (nextEntry != null) {
                File file = new File(str, nextEntry.getName());
                if (!nextEntry.isDirectory()) {
                    if (file.exists() && file.isFile() && !file.delete()) {
                        Log.e("SmsEngineHandler", "delete file " + file.getName() + " failed. ");
                    }
                    if (file.createNewFile()) {
                        try {
                            fileOutputStream = new FileOutputStream(file);
                            byte[] bArr = new byte[65536];
                            while (true) {
                                int read = zipInputStream.read(bArr);
                                if (read == -1) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                                fileOutputStream.flush();
                            }
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (Throwable th) {
                            r1.addSuppressed(th);
                            break;
                        }
                    } else {
                        continue;
                    }
                } else {
                    file.mkdirs();
                }
            } else {
                return;
            }
        }
        throw th;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x006c, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(android.content.Context r8) {
        /*
            r7 = this;
            java.io.File r0 = new java.io.File
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.io.File r2 = r8.getFilesDir()
            r1.append(r2)
            java.lang.String r2 = java.io.File.separator
            r1.append(r2)
            java.lang.String r2 = "resource.zip"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            boolean r0 = r0.exists()
            r1 = 0
            if (r0 != 0) goto L_0x0027
            return r1
        L_0x0027:
            java.util.zip.ZipInputStream r0 = new java.util.zip.ZipInputStream     // Catch:{ IOException -> 0x0085 }
            java.io.FileInputStream r8 = r8.openFileInput(r2)     // Catch:{ IOException -> 0x0085 }
            r0.<init>(r8)     // Catch:{ IOException -> 0x0085 }
            r8 = 0
        L_0x0031:
            java.util.zip.ZipEntry r2 = r0.getNextEntry()     // Catch:{ Throwable -> 0x0074 }
            if (r2 == 0) goto L_0x006d
            boolean r3 = r2.isDirectory()     // Catch:{ Throwable -> 0x0074 }
            if (r3 != 0) goto L_0x0031
            java.io.File r3 = new java.io.File     // Catch:{ Throwable -> 0x0074 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0074 }
            r4.<init>()     // Catch:{ Throwable -> 0x0074 }
            java.lang.String r5 = r7.f2362d     // Catch:{ Throwable -> 0x0074 }
            r4.append(r5)     // Catch:{ Throwable -> 0x0074 }
            java.lang.String r5 = r2.getName()     // Catch:{ Throwable -> 0x0074 }
            r4.append(r5)     // Catch:{ Throwable -> 0x0074 }
            java.lang.String r4 = r4.toString()     // Catch:{ Throwable -> 0x0074 }
            r3.<init>(r4)     // Catch:{ Throwable -> 0x0074 }
            boolean r4 = r3.exists()     // Catch:{ Throwable -> 0x0074 }
            if (r4 == 0) goto L_0x0069
            long r3 = r3.length()     // Catch:{ Throwable -> 0x0074 }
            long r5 = r2.getSize()     // Catch:{ Throwable -> 0x0074 }
            int r2 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r2 == 0) goto L_0x0031
        L_0x0069:
            r0.close()     // Catch:{ IOException -> 0x0085 }
            return r1
        L_0x006d:
            r8 = 1
            r0.close()     // Catch:{ IOException -> 0x0085 }
            return r8
        L_0x0072:
            r2 = move-exception
            goto L_0x0076
        L_0x0074:
            r8 = move-exception
            throw r8     // Catch:{ all -> 0x0072 }
        L_0x0076:
            if (r8 == 0) goto L_0x0081
            r0.close()     // Catch:{ Throwable -> 0x007c }
            goto L_0x0084
        L_0x007c:
            r0 = move-exception
            r8.addSuppressed(r0)     // Catch:{ IOException -> 0x0085 }
            goto L_0x0084
        L_0x0081:
            r0.close()     // Catch:{ IOException -> 0x0085 }
        L_0x0084:
            throw r2     // Catch:{ IOException -> 0x0085 }
        L_0x0085:
            r8 = move-exception
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "check resource failed. "
            r0.append(r2)
            r0.append(r8)
            java.lang.String r8 = r0.toString()
            java.lang.String r0 = "SmsEngineHandler"
            android.util.Log.e(r0, r8)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.policy.a.b.a(android.content.Context):boolean");
    }

    /* access modifiers changed from: private */
    public boolean a(Context context, String str) {
        ZipInputStream zipInputStream;
        try {
            zipInputStream = new ZipInputStream(context.openFileInput("resource.zip"));
            a(zipInputStream, str);
            zipInputStream.close();
            return true;
        } catch (IOException e) {
            Log.w("SmsEngineHandler", "init resource failed. " + e);
            try {
                File file = new File(str);
                if (!file.exists()) {
                    return false;
                }
                a(file);
                return false;
            } catch (Exception unused) {
                Log.w("SmsEngineHandler", "delete resource failed. " + e);
                return false;
            }
        } catch (Throwable th) {
            r3.addSuppressed(th);
        }
        throw th;
    }

    private boolean b(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.android.mms", 0).versionCode < 11000249;
        } catch (Exception unused) {
            return true;
        }
    }

    public int a(String str, String str2) {
        int i = 0;
        try {
            if (b.d.a.a.a.b() && b.d.a.a.a.a(str, str2)) {
                Log.w("SmsEngineHandler", "important sms.");
                return 0;
            } else if (!b(this.f2359a) || (i = this.f2360b.a(this.f2359a, str, str2)) <= 0) {
                if (b.d.a.a.a.c() && b.d.a.a.a.b(str2)) {
                    Log.w("SmsEngineHandler", "new sms engine blocked.");
                    return 2;
                }
                return i;
            } else {
                Log.w("SmsEngineHandler", "old sms engine blocked.");
                return 2;
            }
        } catch (Exception e) {
            Log.e("SmsEngineHandler", "getSMSJudgeResult failed. " + e);
        }
    }

    public void a(boolean z) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            this.f2361c.execute(new a(this, z));
        }
    }
}
