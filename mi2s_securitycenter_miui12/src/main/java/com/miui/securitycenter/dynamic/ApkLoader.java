package com.miui.securitycenter.dynamic;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.j;
import b.b.c.j.f;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.common.persistence.b;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securityscan.i.k;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import miui.os.Build;
import miui.util.IOUtils;
import org.json.JSONObject;

class ApkLoader {
    private static final boolean DEBUG = false;
    private static final String DYNAMIC_APK = "dynamic.apk";
    private static final String KEY_DYNAMIC_APK_MD5 = "dynamic_apk_md5";
    private static final String KEY_DYNAMIC_CURRENT_PATH = "dynamic_current_path";
    private static final String KEY_DYNAMIC_FIRST_UPDATE = "dynamic_first_update";
    private static final String KEY_DYNAMIC_LAST_ONCE_LOG = "dynamic_last_once_log";
    private static final String ONCE_APK = "once.apk";
    private static final String PKG = "com.miui.securitycenter.dynamic";
    private static final String PKG_1 = "com.miui.securitycenter.dynamic-1";
    private static final String PKG_2 = "com.miui.securitycenter.dynamic-2";
    private static final String PKG_ONCE = "com.miui.securitycenter.once";
    public static final String PLATFORM_SHA256 = "C9:00:9D:01:EB:F9:F5:D0:30:2B:C7:1B:2F:E9:AA:9A:47:A4:32:BB:A1:73:08:A3:11:1B:75:D7:B2:14:90:25";
    private static final String SALT = "21da76da-224c-2313-ac60-abcd70139283";
    private static final int STATUS_OK = 1;
    private static final String TAG = "ApkLoader";
    private static final String URL = (Build.IS_INTERNATIONAL_BUILD ? "https://api.sec.intl.miui.com/common/whiteList/packageList/" : "https://api.sec.miui.com/common/whiteList/packageList/");
    private final Context mContext;
    private Context mDynamicContext;
    /* access modifiers changed from: private */
    public boolean mEagerUpdate;
    private boolean mIgnoreCta;
    private long mLastUpdateTime;
    private PackageInfo mPackageInfo;
    private DynamicServiceManager mService;
    /* access modifiers changed from: private */
    public boolean mUpdating;
    /* access modifiers changed from: private */
    public Handler mWorkHandler;

    private static class DownloadInfo {
        String md5;
        String path;

        private DownloadInfo() {
        }
    }

    public ApkLoader(Context context, DynamicServiceManager dynamicServiceManager, Handler handler) {
        this.mContext = context;
        this.mService = dynamicServiceManager;
        this.mWorkHandler = handler;
    }

    /* access modifiers changed from: private */
    public void applayConfig(DownloadInfo downloadInfo) {
        b.b(KEY_DYNAMIC_APK_MD5, downloadInfo.md5);
        b.b(KEY_DYNAMIC_CURRENT_PATH, downloadInfo.path);
        this.mDynamicContext = null;
        this.mPackageInfo = null;
        this.mService.onDynamicContextChangeWT();
    }

    /* access modifiers changed from: private */
    public void checkOnceUpdate() {
        File file = new File(this.mContext.getCacheDir(), ONCE_APK);
        if (file.exists()) {
            file.delete();
        }
        DownloadInfo downloadPackage = downloadPackage(1, PKG_ONCE, file, false);
        if (downloadPackage == null) {
            file.delete();
            return;
        }
        try {
            PackageInfo createPackageInfo = createPackageInfo(file.getAbsolutePath(), 0);
            if (createPackageInfo != null) {
                b.b(KEY_DYNAMIC_LAST_ONCE_LOG, "code : " + createPackageInfo.versionCode + " time : " + System.currentTimeMillis() + " md5 : " + downloadPackage.md5);
                Context createApplicationContext = ContextCompat.createApplicationContext(((Application) this.mContext.getApplicationContext()).getBaseContext(), createPackageInfo.applicationInfo, true);
                createApplicationContext.getClassLoader().loadClass("com.miui.securitycenter.once.God").getMethod("run", new Class[]{Context.class}).invoke((Object) null, new Object[]{createApplicationContext});
            }
        } catch (Throwable th) {
            Log.e(TAG, "loadonce error ", th);
            EventTrack.track(th);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x005b A[SYNTHETIC, Splitter:B:28:0x005b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.io.File copyApkFile(android.content.Context r6, java.io.File r7, boolean r8) {
        /*
            r5 = this;
            r0 = 0
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream     // Catch:{ IOException -> 0x0048, all -> 0x0045 }
            android.content.res.Resources r6 = r6.getResources()     // Catch:{ IOException -> 0x0048, all -> 0x0045 }
            android.content.res.AssetManager r6 = r6.getAssets()     // Catch:{ IOException -> 0x0048, all -> 0x0045 }
            java.lang.String r2 = "dynamic.apk"
            java.io.InputStream r6 = r6.open(r2)     // Catch:{ IOException -> 0x0048, all -> 0x0045 }
            r1.<init>(r6)     // Catch:{ IOException -> 0x0048, all -> 0x0045 }
            boolean r6 = r7.exists()     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            if (r6 == 0) goto L_0x001d
            r7.delete()     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
        L_0x001d:
            r6 = 4096(0x1000, float:5.74E-42)
            byte[] r6 = new byte[r6]     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r3.<init>(r7)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0042, all -> 0x0040 }
        L_0x002b:
            int r3 = r1.read(r6)     // Catch:{ IOException -> 0x003e }
            r4 = -1
            if (r3 == r4) goto L_0x0037
            r4 = 0
            r2.write(r6, r4, r3)     // Catch:{ IOException -> 0x003e }
            goto L_0x002b
        L_0x0037:
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r2)
            return r7
        L_0x003e:
            r6 = move-exception
            goto L_0x004b
        L_0x0040:
            r6 = move-exception
            goto L_0x0065
        L_0x0042:
            r6 = move-exception
            r2 = r0
            goto L_0x004b
        L_0x0045:
            r6 = move-exception
            r1 = r0
            goto L_0x0065
        L_0x0048:
            r6 = move-exception
            r1 = r0
            r2 = r1
        L_0x004b:
            java.lang.String r7 = "ApkLoader"
            java.lang.String r3 = "copyApkFile"
            android.util.Log.e(r7, r3, r6)     // Catch:{ all -> 0x0063 }
            if (r8 != 0) goto L_0x005b
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r2)
            return r0
        L_0x005b:
            java.io.IOException r7 = new java.io.IOException     // Catch:{ all -> 0x0063 }
            java.lang.String r8 = "copyApkFile fail"
            r7.<init>(r8, r6)     // Catch:{ all -> 0x0063 }
            throw r7     // Catch:{ all -> 0x0063 }
        L_0x0063:
            r6 = move-exception
            r0 = r2
        L_0x0065:
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r0)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.securitycenter.dynamic.ApkLoader.copyApkFile(android.content.Context, java.io.File, boolean):java.io.File");
    }

    /* access modifiers changed from: private */
    public File createApkFileDir(String str) {
        File filesDir = this.mContext.getFilesDir();
        File file = new File(filesDir, "dynamic" + File.separator + str);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private PackageInfo createPackageInfo(String str, int i) {
        PackageInfo packageArchiveInfo = this.mContext.getPackageManager().getPackageArchiveInfo(str, i);
        if (packageArchiveInfo != null) {
            ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
            applicationInfo.publicSourceDir = str;
            applicationInfo.sourceDir = str;
            applicationInfo.dataDir = str;
        }
        return packageArchiveInfo;
    }

    /* access modifiers changed from: private */
    public boolean deleteFile(File file) {
        if (file.isFile()) {
            return file.delete();
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File deleteFile : listFiles) {
                deleteFile(deleteFile);
            }
        }
        return file.delete();
    }

    /* access modifiers changed from: private */
    public DownloadInfo downloadPackage(int i, String str, File file, boolean z) {
        InputStream inputStream;
        BufferedOutputStream bufferedOutputStream;
        PackageInfo packageInfo;
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("versionCode", String.valueOf(i));
            hashMap.put("androidVersion", String.valueOf(Build.VERSION.SDK_INT));
            hashMap.put("packageName", str);
            JSONObject jSONObject = new JSONObject(k.a((Map<String, String>) hashMap, URL, "21da76da-224c-2313-ac60-abcd70139283", this.mIgnoreCta, new j("securitycenter_apkloader")));
            int optInt = jSONObject.optInt("status", -1);
            b.b(KEY_DYNAMIC_FIRST_UPDATE, true);
            boolean z2 = false;
            if (optInt != 1) {
                if (z) {
                    this.mEagerUpdate = false;
                }
                return null;
            }
            JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
            if (optJSONObject != null) {
                String optString = optJSONObject.optString(MijiaAlertModel.KEY_URL);
                String optString2 = optJSONObject.optString("md5");
                if (TextUtils.isEmpty(optString2)) {
                    return null;
                }
                inputStream = new URL(optString).openConnection().getInputStream();
                try {
                    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                    try {
                        byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                        while (true) {
                            int read = inputStream.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            bufferedOutputStream.write(bArr, 0, read);
                        }
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(bufferedOutputStream);
                        if (!verifyMd5(file, optString2) || !verifySignature(file)) {
                            packageInfo = null;
                        } else {
                            packageInfo = createPackageInfo(file.getAbsolutePath(), 0);
                            z2 = true;
                        }
                        if (z2) {
                            if (z) {
                                EventTrack.trackVersion(packageInfo.versionCode);
                            }
                            DownloadInfo downloadInfo = new DownloadInfo();
                            downloadInfo.md5 = optString2;
                            return downloadInfo;
                        }
                        file.delete();
                    } catch (Exception e) {
                        e = e;
                        Log.e(TAG, " donwload error ", e);
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(bufferedOutputStream);
                        return null;
                    }
                } catch (Exception e2) {
                    e = e2;
                    bufferedOutputStream = null;
                    Log.e(TAG, " donwload error ", e);
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(bufferedOutputStream);
                    return null;
                } catch (Throwable th) {
                    th = th;
                    bufferedOutputStream = null;
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(bufferedOutputStream);
                    throw th;
                }
            }
            return null;
        } catch (Throwable th2) {
            Log.e(TAG, "update error ", th2);
        }
    }

    /* access modifiers changed from: private */
    public String getCurrentPath() {
        return b.a(KEY_DYNAMIC_CURRENT_PATH, (String) null);
    }

    private String getFileMd5(File file) {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return b.b.c.j.j.b((InputStream) fileInputStream);
        } finally {
            IOUtils.closeQuietly(fileInputStream);
        }
    }

    private File getLocalApkFile() {
        File file = new File(createApkFileDir(PKG), DYNAMIC_APK);
        if (needCopy(this.mContext, file)) {
            File copyApkFile = copyApkFile(this.mContext, file, false);
            if (copyApkFile != null) {
                file = copyApkFile;
            }
            if (needCopy(this.mContext, file)) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException unused) {
                }
                file = copyApkFile(this.mContext, file, true);
            }
        }
        if (file != null) {
            return file;
        }
        throw new NullPointerException("copy apk fail file is null ");
    }

    private String getRealApkPath() {
        File localApkFile = getLocalApkFile();
        PackageInfo createPackageInfo = createPackageInfo(localApkFile.getAbsolutePath(), 0);
        if (createPackageInfo != null) {
            String currentPath = getCurrentPath();
            if (!TextUtils.isEmpty(currentPath)) {
                File createApkFileDir = createApkFileDir(currentPath);
                File file = new File(createApkFileDir, DYNAMIC_APK);
                if (verifyMd5(file, b.a(KEY_DYNAMIC_APK_MD5, "")) && verifySignature(file)) {
                    PackageInfo createPackageInfo2 = createPackageInfo(file.getAbsolutePath(), 0);
                    if (createPackageInfo2 == null || createPackageInfo.versionCode >= createPackageInfo2.versionCode) {
                        deleteFile(createApkFileDir);
                    } else {
                        localApkFile = file;
                    }
                }
            }
            return localApkFile.getAbsolutePath();
        }
        throw new IllegalStateException("localInfo create fail");
    }

    private String getSignaturesSHA256(Signature[] signatureArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA256");
            instance.update(signatureArr[0].toByteArray());
            StringBuilder sb = new StringBuilder();
            byte[] digest = instance.digest();
            int length = digest.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(Integer.toString((digest[i] & 255) + 256, 16).substring(1));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            Log.e(TAG, " getSignaturesSHA256 error", e);
            return null;
        }
    }

    private void internalUpdate() {
        Log.d(TAG, "start update ");
        if ((!h.i() && !this.mIgnoreCta) || !f.b(this.mContext) || this.mUpdating) {
            return;
        }
        if (this.mLastUpdateTime == 0 || SystemClock.uptimeMillis() - this.mLastUpdateTime >= TimeUnit.MINUTES.toMillis(5)) {
            try {
                final PackageInfo createPackageInfo = createPackageInfo(getRealApkPath(), 0);
                if (createPackageInfo == null) {
                    Log.e(TAG, "update error packageInfo is null");
                    return;
                }
                this.mUpdating = true;
                this.mLastUpdateTime = SystemClock.uptimeMillis();
                new AsyncTask<Void, Void, DownloadInfo>() {
                    /* access modifiers changed from: protected */
                    public DownloadInfo doInBackground(Void... voidArr) {
                        String access$200 = ApkLoader.this.getCurrentPath();
                        String str = ApkLoader.PKG_1;
                        if (str.equals(access$200)) {
                            str = ApkLoader.PKG_2;
                        }
                        File access$300 = ApkLoader.this.createApkFileDir(str);
                        if (access$300.exists()) {
                            boolean unused = ApkLoader.this.deleteFile(access$300);
                        }
                        DownloadInfo access$500 = ApkLoader.this.downloadPackage(createPackageInfo.versionCode, ApkLoader.PKG, new File(ApkLoader.this.createApkFileDir(str), ApkLoader.DYNAMIC_APK), true);
                        if (access$500 != null) {
                            access$500.path = str;
                        }
                        ApkLoader.this.checkOnceUpdate();
                        return access$500;
                    }

                    /* access modifiers changed from: protected */
                    public void onPostExecute(final DownloadInfo downloadInfo) {
                        ApkLoader.this.mWorkHandler.post(new Runnable() {
                            public void run() {
                                boolean unused = ApkLoader.this.mUpdating = false;
                                if (downloadInfo != null) {
                                    boolean unused2 = ApkLoader.this.mEagerUpdate = false;
                                    ApkLoader.this.applayConfig(downloadInfo);
                                }
                            }
                        });
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            } catch (Exception e) {
                Log.e(TAG, "getRealApkPath error", e);
            }
        }
    }

    private boolean needCopy(Context context, File file) {
        FileInputStream fileInputStream = null;
        try {
            if (!file.exists()) {
                IOUtils.closeQuietly((InputStream) null);
                return true;
            }
            FileInputStream fileInputStream2 = new FileInputStream(file);
            try {
                boolean z = !TextUtils.equals(b.b.c.j.j.b((InputStream) fileInputStream2), context.getResources().getString(R.string.dynamic_md5));
                IOUtils.closeQuietly(fileInputStream2);
                return z;
            } catch (Exception e) {
                e = e;
                fileInputStream = fileInputStream2;
                try {
                    Log.e(TAG, "needCopy", e);
                    IOUtils.closeQuietly(fileInputStream);
                    return true;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(fileInputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                fileInputStream = fileInputStream2;
                IOUtils.closeQuietly(fileInputStream);
                throw th;
            }
        } catch (Exception e2) {
            e = e2;
            Log.e(TAG, "needCopy", e);
            IOUtils.closeQuietly(fileInputStream);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void onConnectivityChange() {
        if (this.mEagerUpdate) {
            internalUpdate();
        }
    }

    private boolean verifyMd5(File file, String str) {
        if (!file.exists()) {
            return false;
        }
        try {
            return str.equals(getFileMd5(file));
        } catch (IOException e) {
            Log.e(TAG, " verifyMd5 error", e);
            return false;
        }
    }

    private boolean verifySignature(File file) {
        boolean z = false;
        try {
            String signaturesSHA256 = getSignaturesSHA256(createPackageInfo(file.getAbsolutePath(), 64).signatures);
            z = PLATFORM_SHA256.equals(signaturesSHA256);
            if (!z && getSignaturesSHA256(this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 64).signatures).equals(signaturesSHA256)) {
                z = true;
            }
            if (!z) {
                Log.e(TAG, "verifySignature fail : " + file + " SHA256 : " + signaturesSHA256);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "getPackageInfo", e);
        }
        return z;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        printWriter.println("ApkLoader dump");
        printWriter.println("");
        if (this.mDynamicContext == null) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            this.mWorkHandler.post(new Runnable() {
                public void run() {
                    try {
                        ApkLoader.this.getDynamicContext();
                    } catch (Throwable unused) {
                    }
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException unused) {
            }
        }
        if (this.mPackageInfo == null) {
            str = "mPackageInfo is null";
        } else {
            printWriter.println("Current versionCode : " + this.mPackageInfo.versionCode + " versionName :" + this.mPackageInfo.versionName);
            StringBuilder sb = new StringBuilder();
            sb.append("Package path : ");
            sb.append(this.mPackageInfo.applicationInfo.dataDir);
            str = sb.toString();
        }
        printWriter.println(str);
        printWriter.println("Disk path : " + getCurrentPath() + " , md5: " + b.a(KEY_DYNAMIC_APK_MD5, (String) null));
        printWriter.println("Updating : " + this.mUpdating + " , EagerUpdate: " + this.mEagerUpdate + " , LastUpdateTime : " + this.mLastUpdateTime);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("olog : ");
        sb2.append(b.a(KEY_DYNAMIC_LAST_ONCE_LOG, (String) null));
        printWriter.println(sb2.toString());
    }

    public Context getDynamicContext() {
        if (this.mDynamicContext == null) {
            String realApkPath = getRealApkPath();
            this.mPackageInfo = createPackageInfo(realApkPath, 0);
            if (this.mPackageInfo != null) {
                this.mDynamicContext = ContextCompat.createApplicationContext(((Application) this.mContext.getApplicationContext()).getBaseContext(), this.mPackageInfo.applicationInfo, true);
                if (this.mDynamicContext == null) {
                    throw new NullPointerException("createApplicationContext fail " + realApkPath);
                }
            } else {
                throw new NullPointerException("createPackageInfo fail " + realApkPath);
            }
        }
        return this.mDynamicContext;
    }

    public PackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    public void init() {
        String currentPath = getCurrentPath();
        if (!TextUtils.isEmpty(currentPath)) {
            String str = PKG_1;
            if (str.equals(currentPath)) {
                str = PKG_2;
            }
            File createApkFileDir = createApkFileDir(str);
            if (createApkFileDir.exists()) {
                deleteFile(createApkFileDir);
            }
            File createApkFileDir2 = createApkFileDir(currentPath);
            if (!verifyMd5(new File(createApkFileDir2, DYNAMIC_APK), b.a(KEY_DYNAMIC_APK_MD5, ""))) {
                deleteFile(createApkFileDir2);
            }
        } else if (!b.a(KEY_DYNAMIC_FIRST_UPDATE, false)) {
            this.mEagerUpdate = true;
        }
        LocalBroadcastManager.getInstance(this.mContext).registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ApkLoader.this.mWorkHandler.post(new Runnable() {
                    public void run() {
                        ApkLoader.this.onConnectivityChange();
                    }
                });
            }
        }, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void track() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo != null) {
            EventTrack.trackVersion(packageInfo.versionCode);
        }
    }

    public void update(boolean z) {
        this.mIgnoreCta = z;
        this.mEagerUpdate = true;
        internalUpdate();
    }
}
