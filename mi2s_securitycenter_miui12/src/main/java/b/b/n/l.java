package b.b.n;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import b.b.c.j.d;
import b.b.c.j.j;
import b.b.n.g;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.analytics.AnalyticsUtil;
import com.miui.applicationlock.a.h;
import com.miui.common.persistence.b;
import com.miui.systemAdSolution.common.AdInfo;
import com.miui.systemAdSolution.common.AdTrackType;
import com.miui.systemAdSolution.common.Material;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import miui.util.IOUtils;

public class l {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1872a = (Build.IS_INTERNATIONAL_BUILD ? "1.306.18.1" : "1.19.y.1");

    /* renamed from: b  reason: collision with root package name */
    public static final String f1873b = (Build.IS_INTERNATIONAL_BUILD ? "1.306.18.2" : "1.19.y.2");

    /* renamed from: c  reason: collision with root package name */
    public static final Map<String, String> f1874c = new ArrayMap();

    static {
        f1874c.put("securitycenterScan", f1872a);
        f1874c.put("applicationlock", f1873b);
    }

    public static int a(String str) {
        String[] list;
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        File file = new File(str);
        if (!file.exists() || (list = file.list()) == null) {
            return 0;
        }
        return list.length;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x002f A[SYNTHETIC, Splitter:B:20:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x003a A[SYNTHETIC, Splitter:B:26:0x003a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap a(java.io.File r4, android.content.Context r5) {
        /*
            java.lang.String r0 = "getFirstFrame inputStream failed"
            java.lang.String r1 = "RemoteUnifiedAdService"
            com.miui.common.customview.gif.GifImageView r2 = new com.miui.common.customview.gif.GifImageView
            r2.<init>(r5)
            r5 = 0
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0026, all -> 0x0023 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0026, all -> 0x0023 }
            r2.setStream(r3)     // Catch:{ Exception -> 0x0021 }
            android.graphics.Bitmap r4 = r2.getFirstFrame()     // Catch:{ Exception -> 0x0021 }
            r3.close()     // Catch:{ IOException -> 0x001a }
            goto L_0x001e
        L_0x001a:
            r5 = move-exception
            android.util.Log.d(r1, r0, r5)
        L_0x001e:
            return r4
        L_0x001f:
            r4 = move-exception
            goto L_0x0038
        L_0x0021:
            r4 = move-exception
            goto L_0x0028
        L_0x0023:
            r4 = move-exception
            r3 = r5
            goto L_0x0038
        L_0x0026:
            r4 = move-exception
            r3 = r5
        L_0x0028:
            java.lang.String r2 = "getFirstFrame failed"
            android.util.Log.d(r1, r2, r4)     // Catch:{ all -> 0x001f }
            if (r3 == 0) goto L_0x0037
            r3.close()     // Catch:{ IOException -> 0x0033 }
            goto L_0x0037
        L_0x0033:
            r4 = move-exception
            android.util.Log.d(r1, r0, r4)
        L_0x0037:
            return r5
        L_0x0038:
            if (r3 == 0) goto L_0x0042
            r3.close()     // Catch:{ IOException -> 0x003e }
            goto L_0x0042
        L_0x003e:
            r5 = move-exception
            android.util.Log.d(r1, r0, r5)
        L_0x0042:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.n.l.a(java.io.File, android.content.Context):android.graphics.Bitmap");
    }

    public static Material a(AdInfo adInfo) {
        List<Material> materials;
        if (adInfo == null || adInfo.isInvalid() || (materials = adInfo.getMaterials()) == null || materials.size() <= 0) {
            return null;
        }
        return materials.get(0);
    }

    public static String a(Context context, String str) {
        return a(context, str, b.a(str, false) ? "skin_resource_A" : "skin_resource_B");
    }

    public static String a(Context context, String str, AdInfo adInfo, Material material, Material.Resource resource) {
        StringBuilder sb = new StringBuilder();
        sb.append(a(context, str));
        sb.append(File.separator);
        sb.append(adInfo.getId());
        sb.append(File.separator);
        sb.append(material.getId());
        sb.append("_");
        sb.append(resource.getId());
        sb.append("9.png".equalsIgnoreCase(resource.getExtra()) ? ".9.png" : "gif".equalsIgnoreCase(resource.getExtra()) ? ".gif" : "mp4".equalsIgnoreCase(resource.getExtra()) ? ".mp4" : "zip".equalsIgnoreCase(resource.getExtra()) ? ".zip" : ".png");
        return sb.toString();
    }

    public static String a(Context context, String str, String str2) {
        if (context == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(context.getFilesDir().getAbsolutePath());
        sb.append(File.separator);
        sb.append("skin_resource");
        sb.append(File.separator);
        sb.append(str);
        sb.append(File.separator);
        if (!TextUtils.isEmpty(str2)) {
            sb.append(str2);
        }
        return sb.toString();
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
                Log.e("RemoteUnifiedAdService", "readInputStream fail!");
                IOUtils.closeQuietly(byteArrayOutputStream);
                return null;
            } catch (Throwable th) {
                IOUtils.closeQuietly(byteArrayOutputStream);
                throw th;
            }
        }
    }

    public static String a(String str, String str2, Context context) {
        FileInputStream fileInputStream;
        String str3 = null;
        try {
            fileInputStream = new FileInputStream(context.getFilesDir().getPath() + File.separator + str + File.separator + str2);
            try {
                str3 = a(fileInputStream);
            } catch (IOException unused) {
                try {
                    Log.e("RemoteUnifiedAdService", "getJsonFromFile fail!");
                    IOUtils.closeQuietly(fileInputStream);
                    return str3;
                } catch (Throwable th) {
                    th = th;
                }
            }
        } catch (IOException unused2) {
            fileInputStream = null;
            Log.e("RemoteUnifiedAdService", "getJsonFromFile fail!");
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

    private static void a() {
        h.d();
        h.b();
    }

    public static void a(Context context, long j, g.a aVar, String str, String str2) {
        if (context != null && aVar != null && !TextUtils.isEmpty(str)) {
            String b2 = aVar.b();
            String a2 = aVar.a();
            String d2 = aVar.d();
            e.a(context).a(str, str2, AdTrackType.Type.TRACK_CLICK, j);
            if (!TextUtils.isEmpty(a2)) {
                boolean z = false;
                try {
                    Intent parseUri = Intent.parseUri(a2, 0);
                    parseUri.setFlags(268435456);
                    context.startActivity(parseUri);
                } catch (Exception e) {
                    Log.w("RemoteUnifiedAdService", "jumpAndDeepJump exception", e);
                    if (!TextUtils.isEmpty(d2)) {
                        z = c(context, d2);
                    }
                    if (z || TextUtils.isEmpty(b2)) {
                    }
                }
            } else {
                if (!TextUtils.isEmpty(d2)) {
                    if (c(context, d2) || TextUtils.isEmpty(b2)) {
                        return;
                    }
                } else if (TextUtils.isEmpty(b2)) {
                    return;
                }
                b(context, b2);
            }
        }
    }

    private static void a(Context context, String str, AdTrackType.Type type, AdInfo adInfo, Material material, long j) {
        AdTrackType adTrackType = new AdTrackType(type);
        if (context != null && !TextUtils.isEmpty(str) && adInfo != null && material != null) {
            d.a(new k(context, str, adTrackType, adInfo, material, j));
        }
    }

    public static void a(Context context, List<AdInfo> list, String str, String str2) {
        boolean z = true;
        for (AdInfo next : list) {
            List<Material> materials = next.getMaterials();
            if (materials != null) {
                Iterator<Material> it = materials.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (!a(context, it.next(), next, str)) {
                            break;
                        }
                    } else {
                        continue;
                        break;
                    }
                }
                z = false;
                continue;
            } else {
                z = false;
                continue;
            }
            if (!z) {
                break;
            }
        }
        if (z) {
            a();
            b.b(str2, !b.a(str2, false));
            Log.d("RemoteUnifiedAdService", "module:" + str2 + ", material is in: " + b.a(str2, false));
            return;
        }
        File file = new File(str);
        if (file.exists()) {
            a(file);
        }
        b.b("skin_ad_" + f1874c.get(str2), (String) null);
    }

    public static void a(File file) {
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                file.delete();
                return;
            }
            for (File a2 : listFiles) {
                a(a2);
            }
            file.delete();
        }
    }

    public static void a(String str, String str2, String str3, Context context) {
        File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + str);
        file.mkdirs();
        BufferedWriter bufferedWriter = null;
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file, str2))));
            try {
                bufferedWriter2.write(str3);
                IOUtils.closeQuietly(bufferedWriter2);
            } catch (IOException unused) {
                bufferedWriter = bufferedWriter2;
                try {
                    Log.e("RemoteUnifiedAdService", "writeJSONToFile fail!");
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
            Log.e("RemoteUnifiedAdService", "writeJSONToFile fail!");
            IOUtils.closeQuietly(bufferedWriter);
        }
    }

    private static void a(boolean z, boolean z2) {
        if (!z) {
            h.a();
        } else {
            h.b();
            if (z2) {
                return;
            }
        }
        h.c();
    }

    public static boolean a(int i, int i2, long j, String str) {
        if (i <= 0 || i2 <= 0) {
            return true;
        }
        Calendar instance = Calendar.getInstance();
        if (b.a("skin_frequency_day_" + str, 0) != instance.get(5)) {
            b.b("skin_frequency_enter_times_" + str, 0);
            b.b("skin_frequency_day_" + str, instance.get(5));
        }
        if (b.a("skin_frequency_adInfo_id", 0) == j) {
            b.b("skin_frequency_enter_times_" + str, b.a("skin_frequency_enter_times_" + str, 0) + 1);
        } else {
            b.b("skin_frequency_adInfo_id", j);
            b.b("skin_frequency_enter_times_" + str, 1);
        }
        int a2 = b.a("skin_frequency_enter_times_" + str, 0);
        return i <= 1 ? a2 <= i2 : a2 / i < i2 && a2 % i == 1;
    }

    private static boolean a(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.w("RemoteUnifiedAdService", "parcelStartActivity exception", e);
            return false;
        }
    }

    private static boolean a(Context context, Uri uri, File file) {
        InputStream openInputStream;
        try {
            openInputStream = context.getContentResolver().openInputStream(uri);
            boolean a2 = a(openInputStream, file);
            openInputStream.close();
            return a2;
        } catch (Exception e) {
            Log.w("RemoteUnifiedAdService", "copyFile exception var8", e);
            AnalyticsUtil.trackException(e);
            return false;
        } catch (Throwable th) {
            openInputStream.close();
            throw th;
        }
    }

    private static boolean a(Context context, Material material, AdInfo adInfo, String str) {
        Uri uri;
        boolean z;
        String str2 = str + File.separator + adInfo.getId();
        if (material.getResources() == null) {
            return true;
        }
        for (Material.Resource next : material.getResources()) {
            if (next.getResourceDatas() != null) {
                for (Material.Resource.ResourceData next2 : next.getResourceDatas()) {
                    String localPathUri = next2.getLocalPathUri();
                    String localPath = next2.getLocalPath();
                    if (TextUtils.isEmpty(localPath)) {
                        return false;
                    }
                    File file = null;
                    if (!TextUtils.isEmpty(localPathUri)) {
                        uri = Uri.parse(localPathUri);
                    } else {
                        File file2 = new File(localPath);
                        if (!file2.exists()) {
                            return false;
                        }
                        file = file2;
                        uri = null;
                    }
                    StringBuilder sb = new StringBuilder(str2 + File.separator + material.getId() + "_" + next.getId());
                    sb.append("9.png".equalsIgnoreCase(next.getExtra()) ? ".9.png" : "gif".equalsIgnoreCase(next.getExtra()) ? ".gif" : "mp4".equalsIgnoreCase(next.getExtra()) ? ".mp4" : "zip".equalsIgnoreCase(next.getExtra()) ? ".zip" : "webg".equalsIgnoreCase(next.getExtra()) ? ".webg" : "jpeg".equalsIgnoreCase(next.getExtra()) ? ".jpeg" : "jpg".equalsIgnoreCase(next.getExtra()) ? ".jpg" : ".png");
                    String sb2 = sb.toString();
                    File file3 = new File(sb2);
                    file3.mkdirs();
                    if (uri != null) {
                        z = a(context, uri, file3);
                    } else {
                        Context context2 = context;
                        z = a(file, file3);
                    }
                    boolean a2 = a(sb2, next2.getMd5());
                    if (z) {
                        if (!a2) {
                        }
                    }
                    a(context, "com.miui.securitycenter_skinview", AdTrackType.Type.TRACK_FAIL, adInfo, material, -1);
                    a(z, a2);
                    return false;
                }
                continue;
            }
            Context context3 = context;
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x005e A[SYNTHETIC, Splitter:B:29:0x005e] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0069 A[SYNTHETIC, Splitter:B:35:0x0069] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(com.miui.common.customview.gif.GifImageView r5, java.io.File r6, android.content.Context r7, int r8, int r9) {
        /*
            java.lang.String r0 = "InputStream failed"
            java.lang.String r1 = "RemoteUnifiedAdService"
            r2 = 0
            r3 = 0
            java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0056 }
            r4.<init>(r6)     // Catch:{ Exception -> 0x0056 }
            r5.setStream(r4)     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            int r6 = r5.getGifHeight()     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            int r3 = r5.getGifWidth()     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            if (r6 != 0) goto L_0x0021
            r4.close()     // Catch:{ IOException -> 0x001c }
            goto L_0x0020
        L_0x001c:
            r5 = move-exception
            android.util.Log.d(r1, r0, r5)
        L_0x0020:
            return r2
        L_0x0021:
            android.content.res.Resources r7 = r7.getResources()     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            int r7 = r7.getDimensionPixelSize(r8)     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r8 = 1065353216(0x3f800000, float:1.0)
            float r7 = (float) r7     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            float r7 = r7 * r8
            float r6 = (float) r6     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            float r7 = r7 / r6
            android.view.ViewGroup$LayoutParams r6 = r5.getLayoutParams()     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            float r8 = (float) r3     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            float r7 = r7 * r8
            int r7 = (int) r7     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r6.width = r7     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r5.setLayoutParams(r6)     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r5.setVisibility(r2)     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r5.setRepeatCounts(r9)     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r5.c()     // Catch:{ Exception -> 0x0050, all -> 0x004e }
            r5 = 1
            r4.close()     // Catch:{ IOException -> 0x0049 }
            goto L_0x004d
        L_0x0049:
            r6 = move-exception
            android.util.Log.d(r1, r0, r6)
        L_0x004d:
            return r5
        L_0x004e:
            r5 = move-exception
            goto L_0x0067
        L_0x0050:
            r5 = move-exception
            r3 = r4
            goto L_0x0057
        L_0x0053:
            r5 = move-exception
            r4 = r3
            goto L_0x0067
        L_0x0056:
            r5 = move-exception
        L_0x0057:
            java.lang.String r6 = "InitGifImageView failed"
            android.util.Log.d(r1, r6, r5)     // Catch:{ all -> 0x0053 }
            if (r3 == 0) goto L_0x0066
            r3.close()     // Catch:{ IOException -> 0x0062 }
            goto L_0x0066
        L_0x0062:
            r5 = move-exception
            android.util.Log.d(r1, r0, r5)
        L_0x0066:
            return r2
        L_0x0067:
            if (r4 == 0) goto L_0x0071
            r4.close()     // Catch:{ IOException -> 0x006d }
            goto L_0x0071
        L_0x006d:
            r6 = move-exception
            android.util.Log.d(r1, r0, r6)
        L_0x0071:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.n.l.a(com.miui.common.customview.gif.GifImageView, java.io.File, android.content.Context, int, int):boolean");
    }

    static boolean a(File file, File file2) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            boolean a2 = a((InputStream) fileInputStream, file2);
            fileInputStream.close();
            return a2;
        } catch (Exception e) {
            Log.w("RemoteUnifiedAdService", "copyFile exception var8", e);
            AnalyticsUtil.trackException(e);
            return false;
        } catch (Throwable th) {
            fileInputStream.close();
            throw th;
        }
    }

    private static boolean a(InputStream inputStream, File file) {
        FileOutputStream fileOutputStream;
        try {
            if (file.exists()) {
                file.delete();
            }
            fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
            while (true) {
                int read = inputStream.read(bArr);
                if (read < 0) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.flush();
            try {
                fileOutputStream.getFD().sync();
            } catch (IOException e) {
                Log.w("RemoteUnifiedAdService", "copyToFile exception var11", e);
                AnalyticsUtil.trackException(e);
            }
            fileOutputStream.close();
            return true;
        } catch (IOException e2) {
            Log.w("RemoteUnifiedAdService", "copyToFile exception var13", e2);
            AnalyticsUtil.trackException(e2);
            return false;
        } catch (Throwable th) {
            fileOutputStream.flush();
            try {
                fileOutputStream.getFD().sync();
            } catch (IOException e3) {
                Log.w("RemoteUnifiedAdService", "copyToFile exception var11", e3);
                AnalyticsUtil.trackException(e3);
            }
            fileOutputStream.close();
            throw th;
        }
    }

    private static boolean a(String str, String str2) {
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream(str);
            try {
                boolean equals = str2.equals(j.b((InputStream) fileInputStream2));
                IOUtils.closeQuietly(fileInputStream2);
                return equals;
            } catch (Exception e) {
                e = e;
                fileInputStream = fileInputStream2;
                try {
                    Log.w("RemoteUnifiedAdService", "Handle exception", e);
                    AnalyticsUtil.trackException(e);
                    IOUtils.closeQuietly(fileInputStream);
                    return false;
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
            Log.w("RemoteUnifiedAdService", "Handle exception", e);
            AnalyticsUtil.trackException(e);
            IOUtils.closeQuietly(fileInputStream);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0036, code lost:
        if (b.b.c.j.x.g(r3, "com.android.browser") != false) goto L_0x0022;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void b(android.content.Context r3, java.lang.String r4) {
        /*
            android.net.Uri r4 = android.net.Uri.parse(r4)
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "android.intent.action.VIEW"
            r0.<init>(r1, r4)
            r4 = 268435456(0x10000000, float:2.5243549E-29)
            r0.addFlags(r4)
            r3.startActivity(r0)     // Catch:{ Exception -> 0x0014 }
            goto L_0x003c
        L_0x0014:
            r4 = move-exception
            java.lang.String r1 = "RemoteUnifiedAdService"
            java.lang.String r2 = "Uri parse exception"
            android.util.Log.w(r1, r2, r4)
            boolean r4 = miui.os.Build.IS_INTERNATIONAL_BUILD
            java.lang.String r1 = "com.android.browser"
            if (r4 != 0) goto L_0x0026
        L_0x0022:
            r0.setPackage(r1)
            goto L_0x0039
        L_0x0026:
            java.lang.String r4 = "com.mi.globalbrowser"
            boolean r2 = b.b.c.j.x.g(r3, r4)
            if (r2 == 0) goto L_0x0032
            r0.setPackage(r4)
            goto L_0x0039
        L_0x0032:
            boolean r4 = b.b.c.j.x.g(r3, r1)
            if (r4 == 0) goto L_0x0039
            goto L_0x0022
        L_0x0039:
            a((android.content.Context) r3, (android.content.Intent) r0)
        L_0x003c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.n.l.b(android.content.Context, java.lang.String):void");
    }

    private static boolean c(Context context, String str) {
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(str);
        if (launchIntentForPackage == null) {
            return false;
        }
        launchIntentForPackage.setFlags(268435456);
        return a(context, launchIntentForPackage);
    }
}
