package com.miui.appmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import miui.util.IOUtils;
import org.json.JSONArray;

/* renamed from: com.miui.appmanager.d  reason: case insensitive filesystem */
class C0321d extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3665a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3666b;

    C0321d(Context context, String str) {
        this.f3665a = context;
        this.f3666b = str;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        FileOutputStream fileOutputStream;
        PrintWriter printWriter;
        synchronized (C0322e.f3670a) {
            JSONArray a2 = C0322e.c(this.f3665a);
            int a3 = C0322e.b(a2, this.f3666b);
            PrintWriter printWriter2 = null;
            if (a3 != -1) {
                a2.remove(a3);
                try {
                    fileOutputStream = new FileOutputStream(this.f3665a.getFilesDir() + "/appmanager/" + "appmanager_installer_pkg");
                    try {
                        printWriter = new PrintWriter(fileOutputStream);
                        try {
                            printWriter.write(a2.toString());
                            IOUtils.closeQuietly(printWriter);
                        } catch (FileNotFoundException e) {
                            e = e;
                            try {
                                Log.e("AMInstallerUtils", "FileNotFoundException when removeInstallerFromFile", e);
                                IOUtils.closeQuietly(printWriter);
                                IOUtils.closeQuietly(fileOutputStream);
                                return null;
                            } catch (Throwable th) {
                                th = th;
                                printWriter2 = printWriter;
                                IOUtils.closeQuietly(printWriter2);
                                IOUtils.closeQuietly(fileOutputStream);
                                throw th;
                            }
                        }
                    } catch (FileNotFoundException e2) {
                        e = e2;
                        printWriter = null;
                        Log.e("AMInstallerUtils", "FileNotFoundException when removeInstallerFromFile", e);
                        IOUtils.closeQuietly(printWriter);
                        IOUtils.closeQuietly(fileOutputStream);
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeQuietly(printWriter2);
                        IOUtils.closeQuietly(fileOutputStream);
                        throw th;
                    }
                } catch (FileNotFoundException e3) {
                    e = e3;
                    fileOutputStream = null;
                    printWriter = null;
                    Log.e("AMInstallerUtils", "FileNotFoundException when removeInstallerFromFile", e);
                    IOUtils.closeQuietly(printWriter);
                    IOUtils.closeQuietly(fileOutputStream);
                    return null;
                } catch (Throwable th3) {
                    th = th3;
                    fileOutputStream = null;
                    IOUtils.closeQuietly(printWriter2);
                    IOUtils.closeQuietly(fileOutputStream);
                    throw th;
                }
                IOUtils.closeQuietly(fileOutputStream);
            }
        }
        return null;
    }
}
