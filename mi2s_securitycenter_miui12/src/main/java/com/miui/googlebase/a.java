package com.miui.googlebase;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import b.b.o.g.d;
import com.miui.googlebase.GoogleBaseAppInstallService;
import com.miui.googlebase.b.f;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import miui.os.Build;
import miui.os.Environment;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static final File f5435a = new File(Environment.getExternalStorageMiuiDirectory(), "securitycenter");

    /* renamed from: b  reason: collision with root package name */
    private Context f5436b;

    /* renamed from: c  reason: collision with root package name */
    private LongSparseArray<GoogleBaseAppInstallService.a> f5437c = new LongSparseArray<>();

    a(Context context) {
        this.f5436b = context;
    }

    private void a(int i) {
        if (Build.IS_DEBUGGABLE) {
            Log.d("GoogleBaseApp.Download", "notifyDownloadAllFinish: state = " + i);
        }
        Intent intent = new Intent();
        intent.setAction("finish");
        intent.putExtra(AdvancedSlider.STATE, i);
        intent.setClass(this.f5436b, GoogleBaseAppInstallService.class);
        d.a("GoogleBaseApp.Download", (Object) this.f5436b, "startServiceAsUser", (Class<?>[]) new Class[]{Intent.class, UserHandle.class}, intent, new UserHandle(0));
    }

    private void a(File file) {
        if (file.exists() && !file.delete()) {
            Log.d("TAG", "can't delete obsolete file");
        }
    }

    private void a(String str) {
        b bVar = new b(this.f5436b, str);
        if (!bVar.f() || bVar.e()) {
            f.a(this.f5436b, str);
        }
    }

    private boolean b() {
        File file = new File(Environment.getExternalStorageMiuiDirectory(), "securitycenter");
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    private boolean b(String str, GoogleBaseAppInstallService.a aVar) {
        if (!(str == null || aVar == null || aVar.c() == null || aVar.b() == null)) {
            try {
                if (com.miui.googlebase.b.a.a(new File(str)).equals(aVar.b())) {
                    return true;
                }
            } catch (Exception e) {
                Log.d("GoogleBaseApp.Download", "Error to get file hash: " + e);
            }
        }
        return false;
    }

    private void c() {
        Log.e("GoogleBaseApp.Download", "download failed");
        a();
        a(6);
    }

    public void a() {
        DownloadManager downloadManager = (DownloadManager) this.f5436b.getSystemService("download");
        for (int i = 0; i < this.f5437c.size(); i++) {
            downloadManager.remove(new long[]{this.f5437c.keyAt(i)});
        }
    }

    public void a(String str, GoogleBaseAppInstallService.a aVar) {
        if (b() && !TextUtils.isEmpty(str)) {
            Uri parse = Uri.parse(str);
            String str2 = aVar.a() + ".apk";
            if (TextUtils.isEmpty(str2)) {
                str2 = parse.getLastPathSegment();
            }
            if (TextUtils.isEmpty(str2)) {
                str2 = new SimpleDateFormat("yyyy-MM-dd-HHmmss", Locale.US).format(new Date(System.currentTimeMillis()));
            }
            File file = new File(f5435a, str2);
            if (!file.exists() || !b(file.getPath(), aVar)) {
                a(file);
                DownloadManager.Request request = new DownloadManager.Request(parse);
                request.setDestinationUri(Uri.fromFile(file));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(2);
                this.f5437c.put(((DownloadManager) this.f5436b.getSystemService("download")).enqueue(request), aVar);
                return;
            }
            Log.d("GoogleBaseApp.Download", "avoid to download existing file: " + file.getPath());
            a(file.getPath());
        }
    }

    public boolean a(long j) {
        return this.f5437c.get(j) != null;
    }

    public void b(long j) {
        Log.d("GoogleBaseApp.Download", "downloadId = " + j);
        if (a(j)) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(new long[]{j});
            Cursor query2 = ((DownloadManager) this.f5436b.getSystemService("download")).query(query);
            if (query2 != null) {
                try {
                    if (query2.moveToNext()) {
                        int i = query2.getInt(query2.getColumnIndex("status"));
                        if (i == 8) {
                            int columnIndex = query2.getColumnIndex(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
                            String str = f5435a.toString() + "/" + query2.getString(columnIndex);
                            Log.d("GoogleBaseApp.Download", "install google base apps, path:" + str);
                            if (!str.endsWith(".apk") || !b(str, this.f5437c.get(j))) {
                                Log.d("GoogleBaseApp.Download", "download file hash code not match");
                                a(5);
                            } else {
                                a(str);
                            }
                        } else if (i == 16) {
                            c();
                        }
                    }
                } catch (Exception e) {
                    Log.e("GoogleBaseApp.Download", "Error after download file: " + e);
                    a(1);
                } catch (Throwable th) {
                    query2.close();
                    throw th;
                }
                query2.close();
            }
            this.f5437c.remove(j);
        }
    }
}
