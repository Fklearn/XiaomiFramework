package com.miui.googlebase.b;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.util.Log;
import b.b.o.g.d;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import miui.util.IOUtils;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f5446a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f5447b;

    e(Context context, String str) {
        this.f5446a = context;
        this.f5447b = str;
    }

    public void run() {
        FileInputStream fileInputStream;
        OutputStream openWrite;
        PackageInstaller.Session session = null;
        try {
            PackageInstaller packageInstaller = this.f5446a.getPackageManager().getPackageInstaller();
            File file = new File(this.f5447b);
            PackageInstaller.SessionParams sessionParams = new PackageInstaller.SessionParams(1);
            d.a("Utils", (Object) sessionParams, "referrerUri", (Object) Uri.fromFile(new File(this.f5447b)));
            int createSession = packageInstaller.createSession(sessionParams);
            byte[] bArr = new byte[65536];
            session = packageInstaller.openSession(createSession);
            fileInputStream = new FileInputStream(file);
            openWrite = session.openWrite("PackageInstaller", 0, file.length());
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                openWrite.write(bArr, 0, read);
            }
            session.fsync(openWrite);
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(openWrite);
            session.commit(PendingIntent.getBroadcast(this.f5446a, createSession, new Intent("com.android.packageinstaller.ACTION_INSTALL_COMMIT" + f.f5448a.incrementAndGet()), 134217728).getIntentSender());
            if (session == null) {
            }
        } catch (Exception e) {
            try {
                Log.e("Utils", "installApkSilently.", e);
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        } catch (Throwable th) {
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(openWrite);
            throw th;
        }
    }
}
