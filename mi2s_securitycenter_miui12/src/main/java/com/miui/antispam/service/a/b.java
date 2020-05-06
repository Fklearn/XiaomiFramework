package com.miui.antispam.service.a;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import b.b.a.e.c;
import b.b.c.h.j;
import b.b.o.g.e;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.antivirus.service.VirusAutoUpdateJobService;
import com.miui.permcenter.permissions.C0466c;
import com.miui.securityscan.i.k;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import miui.os.Build;
import org.json.JSONObject;
import org.json.JSONTokener;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final String[] f2399a = {"dict", "model", "pattern", "phish", "resource.zip"};

    /* renamed from: b  reason: collision with root package name */
    private static final String[] f2400b = {"a", "b", C0466c.f6254a, "d", "resources"};

    /* renamed from: c  reason: collision with root package name */
    private static final String f2401c = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public static boolean f2402d = false;
    private static b e;
    private Context f;
    private SharedPreferences g = this.f.getSharedPreferences("SmsEnginePreference", 0);

    private class a extends AsyncTask<Void, Integer, Integer> {

        /* renamed from: a  reason: collision with root package name */
        private Context f2403a;

        /* renamed from: b  reason: collision with root package name */
        private Runnable f2404b;

        /* renamed from: c  reason: collision with root package name */
        private C0036b f2405c;

        public a(Context context, Runnable runnable, C0036b bVar) {
            this.f2403a = context;
            this.f2404b = runnable;
            this.f2405c = bVar;
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Integer doInBackground(Void... voidArr) {
            return Integer.valueOf(b.this.a(this.f2403a, this.f2404b, this.f2405c, false));
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Integer num) {
            String str;
            if (num.intValue() == 1) {
                c.a(this.f2403a, System.currentTimeMillis());
                boolean unused = b.f2402d = false;
                Runnable runnable = this.f2404b;
                if (runnable != null) {
                    ((VirusAutoUpdateJobService.b) runnable).a();
                }
                C0036b bVar = this.f2405c;
                if (bVar != null) {
                    bVar.a(1);
                }
                str = " xiaomi engine update success !";
            } else if (num.intValue() == 0) {
                boolean unused2 = b.f2402d = false;
                Runnable runnable2 = this.f2404b;
                if (runnable2 != null) {
                    ((VirusAutoUpdateJobService.b) runnable2).a();
                }
                C0036b bVar2 = this.f2405c;
                if (bVar2 != null) {
                    bVar2.a(0);
                }
                str = " xiaomi engine update failed !";
            } else {
                return;
            }
            Log.e("SmsEngineUpdateManager", str);
        }
    }

    /* renamed from: com.miui.antispam.service.a.b$b  reason: collision with other inner class name */
    public interface C0036b {
        void a(int i);
    }

    private b(Context context) {
        this.f = context.getApplicationContext();
    }

    /* access modifiers changed from: private */
    public int a(Context context, Runnable runnable, C0036b bVar, boolean z) {
        int i;
        DownloadManager downloadManager;
        boolean z2;
        boolean z3 = z;
        StringBuilder sb = new StringBuilder();
        sb.append(z3 ? "http://staging.api.sec.miui.com/GuardProviderV1" : "https://api.sec.miui.com/GuardProviderV1");
        sb.append("/GetVersion?ver=");
        sb.append("1.0");
        try {
            File[] listFiles = new File(f2401c).listFiles();
            if (listFiles != null) {
                for (int i2 = 0; i2 < listFiles.length; i2++) {
                    if (listFiles[i2].getName().endsWith(".tmp")) {
                        listFiles[i2].delete();
                    }
                }
            }
            String a2 = k.a(sb.toString(), (Map<String, String>) null, new j("antispam_checkupdate"));
            Log.d("SmsEngineUpdateManager", "response:" + a2);
            JSONObject jSONObject = new JSONObject(new JSONTokener(a2));
            if (jSONObject.getInt("code") == 0) {
                JSONObject jSONObject2 = jSONObject.getJSONObject("maxVersion");
                int[][] c2 = c(context);
                String string = jSONObject2.getString("file_" + f2400b[4]);
                int i3 = 5;
                boolean[] zArr = new boolean[5];
                boolean z4 = false;
                for (int i4 = 0; i4 < 5; i4++) {
                    zArr[i4] = false;
                    String[] split = jSONObject2.getString("file_" + f2400b[i4]).split("\\.");
                    if (split.length == 2 && Integer.parseInt(split[0]) == 1 && Integer.parseInt(split[1]) > c2[i4][1]) {
                        zArr[i4] = true;
                        z4 = true;
                    }
                }
                if (!z4) {
                    return 1;
                }
                DownloadManager downloadManager2 = (DownloadManager) context.getSystemService("download");
                long[] jArr = new long[5];
                boolean[] zArr2 = new boolean[5];
                for (int i5 = 0; i5 < zArr.length; i5++) {
                    zArr2[i5] = zArr[i5];
                }
                boolean[] zArr3 = zArr2;
                long[] jArr2 = jArr;
                DownloadManager downloadManager3 = downloadManager2;
                a aVar = r10;
                int i6 = 2;
                a aVar2 = new a(this, zArr3, jArr2, zArr, downloadManager3, runnable, bVar, string);
                boolean z5 = false;
                int i7 = 0;
                while (i7 < i3) {
                    if (zArr[i7]) {
                        try {
                            String a3 = a(i7, jSONObject2.getString("file_" + f2400b[i7]), z3);
                            if (TextUtils.isEmpty(a3)) {
                                jArr2[i7] = -1;
                                zArr3[i7] = false;
                                i = i7;
                                downloadManager = downloadManager3;
                            } else {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(a3));
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, f2399a[i7] + ".tmp");
                                request.setNotificationVisibility(i6);
                                if (i7 != 4) {
                                    z2 = true;
                                    try {
                                        e.a((Object) request, "setExtra2", (Class<?>[]) new Class[]{String.class}, String.format("{\"bypass_recommended_size_limit\":%s}", new Object[]{true}));
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                    i = i7;
                                } else {
                                    z2 = true;
                                    try {
                                        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(a3).openConnection();
                                        i = i7;
                                        long contentLength = (long) httpURLConnection.getContentLength();
                                        try {
                                            httpURLConnection.disconnect();
                                            e.a((Object) request, "setFileSize", (Class<?>[]) new Class[]{Long.TYPE}, Long.valueOf(contentLength));
                                        } catch (Exception e3) {
                                            e = e3;
                                        }
                                    } catch (Exception e4) {
                                        e = e4;
                                        i = i7;
                                        e.printStackTrace();
                                        downloadManager = downloadManager3;
                                        jArr2[i] = downloadManager.enqueue(request);
                                        z5 = z2;
                                        i7 = i + 1;
                                        downloadManager3 = downloadManager;
                                        i3 = 5;
                                        i6 = 2;
                                    }
                                }
                                downloadManager = downloadManager3;
                                jArr2[i] = downloadManager.enqueue(request);
                                z5 = z2;
                            }
                        } catch (Exception e5) {
                            e = e5;
                            Log.e("SmsEngineUpdateManager", "Exception when checkUpdate", e);
                            return 0;
                        }
                    } else {
                        i = i7;
                        downloadManager = downloadManager3;
                    }
                    i7 = i + 1;
                    downloadManager3 = downloadManager;
                    i3 = 5;
                    i6 = 2;
                }
                if (!z5) {
                    return 1;
                }
                context.getApplicationContext().registerReceiver(aVar, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));
                return 2;
            }
            Log.e("SmsEngineUpdateManager", "Get server version error:" + jSONObject.getString("desc"));
            return 0;
        } catch (Exception e6) {
            e = e6;
            Log.e("SmsEngineUpdateManager", "Exception when checkUpdate", e);
            return 0;
        }
    }

    public static b a(Context context) {
        if (e == null) {
            e = new b(context);
        }
        return e;
    }

    private String a() {
        return this.g.getString("version", "1.-1");
    }

    private String a(int i, String str, boolean z) {
        String str2;
        StringBuilder sb = new StringBuilder();
        sb.append(z ? "http://staging.api.sec.miui.com/GuardProviderV1" : "https://api.sec.miui.com/GuardProviderV1");
        if (i == f2400b.length - 1) {
            str2 = "/GetResourceZipFile";
        } else {
            sb.append("/GetFile");
            str2 = f2400b[i].toUpperCase();
        }
        sb.append(str2);
        sb.append("?ver");
        sb.append("=" + str);
        String sb2 = sb.toString();
        if (i != f2400b.length - 1) {
            return sb2;
        }
        try {
            String a2 = k.a(sb2, (Map<String, String>) null, new j("antispam_getdownloadurl"));
            Log.d("SmsEngineUpdateManager", "response:" + a2);
            JSONObject jSONObject = new JSONObject(new JSONTokener(a2));
            return jSONObject.getInt("code") == 200 ? jSONObject.getJSONObject(DataSchemeDataSource.SCHEME_DATA).getString("fileUrl") : sb2;
        } catch (Exception unused) {
            return null;
        }
    }

    private void a(String str) {
        this.g.edit().putString("version", str).apply();
    }

    /* access modifiers changed from: private */
    public void a(boolean[] zArr, long[] jArr, DownloadManager downloadManager, Context context, Runnable runnable, C0036b bVar, String str) {
        int i;
        DownloadManager downloadManager2 = downloadManager;
        Context context2 = context;
        C0036b bVar2 = bVar;
        try {
            b.d.e.c.a(true);
            for (int i2 = 0; i2 < 5; i2++) {
                if (zArr[i2] && jArr[i2] != -1) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(new long[]{jArr[i2]});
                    Cursor query2 = downloadManager2.query(query);
                    if (query2 != null) {
                        i = query2.moveToNext() ? query2.getInt(query2.getColumnIndex("status")) : 0;
                        query2.close();
                    } else {
                        i = 0;
                    }
                    if (8 == i) {
                        String str2 = f2399a[i2];
                        String str3 = f2401c + "/" + str2 + ".tmp";
                        context2.deleteFile(str2);
                        FileOutputStream openFileOutput = context2.openFileOutput(str2, 0);
                        FileInputStream fileInputStream = new FileInputStream(str3);
                        byte[] bArr = new byte[1024];
                        while (true) {
                            int read = fileInputStream.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            openFileOutput.write(bArr, 0, read);
                        }
                        fileInputStream.close();
                        openFileOutput.flush();
                        openFileOutput.close();
                        Log.i("SmsEngineUpdateManager", "afterDownload: delete tmpFileName : " + str3);
                    }
                    downloadManager2.remove(new long[]{jArr[i2]});
                }
            }
            c.a(context2, System.currentTimeMillis());
            this.f.getContentResolver().call(Uri.parse("content://antispam"), "initSmsEngine", (String) null, (Bundle) null);
            a(str);
            f2402d = false;
            if (runnable != null) {
                ((VirusAutoUpdateJobService.b) runnable).a();
            }
            if (bVar2 != null) {
                bVar2.a(2);
            }
            Log.i("SmsEngineUpdateManager", " xiaomi engine update success !");
        } catch (IOException e2) {
            Log.e("SmsEngineUpdateManager", "Exception when copy tmp files ! ", e2);
            for (int i3 = 0; i3 < 5; i3++) {
                context2.deleteFile(f2399a[i3]);
            }
        } catch (Throwable th) {
            b.d.e.c.a(false);
            throw th;
        }
        b.d.e.c.a(false);
    }

    private String b(Context context) {
        try {
            InputStream a2 = b.d.e.c.a(context, f2399a[2]);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(a2));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                    sb.append("\n");
                } else {
                    bufferedReader.close();
                    a2.close();
                    return sb.toString();
                }
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.e("SmsEngineUpdateManager", "getPattern wrong, " + e2);
            return null;
        }
    }

    private int[][] c(Context context) {
        int[][] iArr = (int[][]) Array.newInstance(int.class, new int[]{5, 2});
        try {
            InputStream a2 = b.d.e.c.a(context, f2399a[0]);
            DataInputStream dataInputStream = new DataInputStream(a2);
            iArr[0][0] = dataInputStream.readInt();
            iArr[0][1] = dataInputStream.readInt();
            dataInputStream.close();
            a2.close();
            InputStream a3 = b.d.e.c.a(context, f2399a[1]);
            DataInputStream dataInputStream2 = new DataInputStream(a3);
            iArr[1][0] = dataInputStream2.readInt();
            iArr[1][1] = dataInputStream2.readInt();
            dataInputStream2.close();
            a3.close();
            String[] split = new JSONObject(new JSONTokener(b(context))).getString("version").split("\\.");
            if (split.length == 2) {
                iArr[2][0] = Integer.parseInt(split[0]);
                iArr[2][1] = Integer.parseInt(split[1]);
            }
            InputStream a4 = b.d.e.c.a(context, f2399a[3]);
            DataInputStream dataInputStream3 = new DataInputStream(a4);
            String readLine = dataInputStream3.readLine();
            dataInputStream3.close();
            a4.close();
            String[] split2 = readLine.split("\\.");
            if (split2.length == 2) {
                iArr[3][0] = Integer.parseInt(split2[0]);
                iArr[3][1] = Integer.parseInt(split2[1]);
            }
            String[] split3 = a().split("\\.");
            if (split3.length == 2) {
                iArr[4][0] = Integer.parseInt(split3[0]);
                iArr[4][1] = Integer.parseInt(split3[1]);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            Log.e("SmsEngineUpdateManager", "getVersion wrong, " + e2);
        }
        return iArr;
    }

    public void a(Runnable runnable, C0036b bVar, boolean z) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        if (z || !f2402d) {
            long a2 = c.a(this.f);
            if (z || System.currentTimeMillis() - a2 > 604800000) {
                Log.e("SmsEngineUpdateManager", " update start!");
                f2402d = true;
                new a(this.f, runnable, bVar).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    }
}
