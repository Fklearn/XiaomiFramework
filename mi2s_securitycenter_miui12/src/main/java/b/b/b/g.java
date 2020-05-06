package b.b.b;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.a.b;
import b.b.c.j.v;
import com.miui.antivirus.activity.DangerousAlertActivity;
import com.miui.antivirus.model.DangerousInfo;
import com.miui.securitycenter.R;
import java.util.concurrent.atomic.AtomicInteger;
import miui.util.ArrayMap;
import org.json.JSONObject;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static g f1546a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f1547b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public ArrayMap<String, DangerousInfo> f1548c = new ArrayMap<>();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f1549d = new Object();
    private final AtomicInteger e = new AtomicInteger(1);
    private BroadcastReceiver f;

    public g(Context context) {
        this.f1547b = context.getApplicationContext();
    }

    public static synchronized g a(Context context) {
        g gVar;
        synchronized (g.class) {
            if (f1546a == null) {
                f1546a = new g(context);
            }
            gVar = f1546a;
        }
        return gVar;
    }

    /* access modifiers changed from: private */
    public void a(DangerousInfo dangerousInfo) {
        if (dangerousInfo.getNotifyType() == 2) {
            a(dangerousInfo.getPackageName());
        }
        Intent intent = new Intent(this.f1547b, DangerousAlertActivity.class);
        intent.putExtra("info", dangerousInfo);
        intent.putExtra("notify_id", 505);
        intent.setFlags(335544320);
        this.f1547b.startActivity(intent);
        b.a.a(dangerousInfo.getPackageName());
    }

    /* access modifiers changed from: private */
    public void a(String str) {
        ((NotificationManager) this.f1547b.getSystemService("notification")).cancel(str, 505);
        synchronized (this.f1549d) {
            this.f1548c.remove(str);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v10 */
    /* JADX WARNING: type inference failed for: r0v11, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r0v13 */
    /* JADX WARNING: type inference failed for: r0v21 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean a(com.miui.antivirus.model.DangerousInfo r7, android.content.pm.PackageInfo r8) {
        /*
            r6 = this;
            java.lang.String r0 = r7.getPackageName()
            java.lang.String r1 = r7.getSign()
            android.content.pm.Signature[] r2 = r8.signatures
            r3 = 0
            java.lang.String r4 = "DangerousService"
            if (r2 == 0) goto L_0x0102
            int r5 = r2.length
            if (r5 != 0) goto L_0x0014
            goto L_0x0102
        L_0x0014:
            r0 = r2[r3]
            java.lang.String r0 = r0.toCharsString()
            byte[] r0 = r0.getBytes()
            java.lang.String r0 = b.b.c.j.j.d(r0)
            boolean r2 = android.text.TextUtils.equals(r1, r0)
            if (r2 != 0) goto L_0x0045
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "signatures not match , sign : "
            r7.append(r8)
            r7.append(r1)
            java.lang.String r8 = " localSign: "
        L_0x0037:
            r7.append(r8)
            r7.append(r0)
            java.lang.String r7 = r7.toString()
        L_0x0041:
            android.util.Log.i(r4, r7)
            return r3
        L_0x0045:
            int r0 = r7.getVersionCode()
            r1 = -1001(0xfffffffffffffc17, float:NaN)
            if (r0 == r1) goto L_0x0075
            int r0 = r7.getVersionCode()
            int r1 = r8.versionCode
            if (r0 == r1) goto L_0x0075
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "versionCode not match , versionCode : "
            r0.append(r1)
            int r7 = r7.getVersionCode()
            r0.append(r7)
            java.lang.String r7 = " localVersionCode: "
            r0.append(r7)
            int r7 = r8.versionCode
            r0.append(r7)
            java.lang.String r7 = r0.toString()
            goto L_0x0041
        L_0x0075:
            java.lang.String r0 = r7.getMsg()
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00b7
            java.util.Locale r0 = java.util.Locale.getDefault()
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = r7.getLanguage()
            boolean r0 = android.text.TextUtils.equals(r0, r1)
            if (r0 != 0) goto L_0x00b7
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r0 = "language not match , language : "
            r8.append(r0)
            java.lang.String r7 = r7.getLanguage()
            r8.append(r7)
            java.lang.String r7 = " localLanguage: "
            r8.append(r7)
            java.util.Locale r7 = java.util.Locale.getDefault()
            java.lang.String r7 = r7.toString()
            r8.append(r7)
        L_0x00b2:
            java.lang.String r7 = r8.toString()
            goto L_0x0041
        L_0x00b7:
            java.lang.String r7 = r7.getFileMd5()
            boolean r0 = android.text.TextUtils.isEmpty(r7)
            if (r0 != 0) goto L_0x0100
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ IOException -> 0x00d4, all -> 0x00d2 }
            android.content.pm.ApplicationInfo r8 = r8.applicationInfo     // Catch:{ IOException -> 0x00d4, all -> 0x00d2 }
            java.lang.String r8 = r8.publicSourceDir     // Catch:{ IOException -> 0x00d4, all -> 0x00d2 }
            r1.<init>(r8)     // Catch:{ IOException -> 0x00d4, all -> 0x00d2 }
            java.lang.String r0 = b.b.c.j.j.b((java.io.InputStream) r1)     // Catch:{ IOException -> 0x00d0 }
            goto L_0x00db
        L_0x00d0:
            r8 = move-exception
            goto L_0x00d6
        L_0x00d2:
            r7 = move-exception
            goto L_0x00fc
        L_0x00d4:
            r8 = move-exception
            r1 = r0
        L_0x00d6:
            java.lang.String r2 = "read error"
            android.util.Log.d(r4, r2, r8)     // Catch:{ all -> 0x00fa }
        L_0x00db:
            miui.util.IOUtils.closeQuietly(r1)
            boolean r8 = r7.equals(r0)
            if (r8 != 0) goto L_0x0100
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r1 = "file md5 not match , md5 : "
            r8.append(r1)
            r8.append(r7)
            java.lang.String r7 = " localMd5: "
            r8.append(r7)
            r8.append(r0)
            goto L_0x00b2
        L_0x00fa:
            r7 = move-exception
            r0 = r1
        L_0x00fc:
            miui.util.IOUtils.closeQuietly(r0)
            throw r7
        L_0x0100:
            r7 = 1
            return r7
        L_0x0102:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "local signatures is null : "
            goto L_0x0037
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.b.g.a(com.miui.antivirus.model.DangerousInfo, android.content.pm.PackageInfo):boolean");
    }

    /* access modifiers changed from: private */
    public void b(JSONObject jSONObject) {
        DangerousInfo create = DangerousInfo.create(jSONObject);
        String packageName = create.getPackageName();
        if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(create.getSign())) {
            Log.e("DangerousService", "info invalid");
            return;
        }
        NotificationManager notificationManager = (NotificationManager) this.f1547b.getSystemService("notification");
        notificationManager.cancel(packageName, 505);
        int notifyType = create.getNotifyType();
        if (notifyType == 1 || notifyType == 2) {
            PackageManager packageManager = this.f1547b.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                packageInfo = packageManager.getPackageInfo(packageName, 64);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.i("DangerousService", "getPackageInfo NameNotFoundException pkg :" + packageName);
            }
            if (packageInfo == null) {
                Log.i("DangerousService", "pkg not install : " + packageName);
            } else if (!a(create, packageInfo)) {
                Log.i("DangerousService", "verityPackage fail : " + packageName);
            } else {
                a();
                Intent intent = new Intent("com.miui.antivirus.ACTION_DANGEROUS");
                intent.putExtra("android.intent.extra.PACKAGE_NAME", packageName);
                v.a(notificationManager, "com.miui.securitycenter", this.f1547b.getResources().getString(R.string.notify_channel_name_security), 3);
                notificationManager.notify(packageName, 505, v.a(this.f1547b, "com.miui.securitycenter").setOngoing(true).setContentTitle(this.f1547b.getString(R.string.uninstall_danagerous_title)).setContentText(this.f1547b.getString(R.string.uninstall_danagerous_desc, new Object[]{packageInfo.applicationInfo.loadLabel(packageManager)})).setContentIntent(PendingIntent.getBroadcast(this.f1547b, this.e.getAndIncrement(), intent, 134217728)).setSmallIcon(R.drawable.virus_small_icon).build());
                synchronized (this.f1549d) {
                    this.f1548c.put(packageName, create);
                }
                b.a.b(packageName);
            }
        } else {
            Log.e("DangerousService", "info invalid notifyType : " + notifyType);
        }
    }

    public void a() {
        synchronized (this.f1549d) {
            if (this.f == null) {
                this.f = new f(this);
                this.f1547b.registerReceiver(this.f, new IntentFilter("com.miui.antivirus.ACTION_DANGEROUS"), "com.miui.securitycenter.permission.Security", (Handler) null);
            }
        }
    }

    public void a(String str, boolean z) {
        DangerousInfo dangerousInfo;
        synchronized (this.f1549d) {
            dangerousInfo = (DangerousInfo) this.f1548c.get(str);
        }
        if (dangerousInfo != null) {
            if (!z) {
                a(str);
            } else {
                new d(this, str, dangerousInfo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            }
        }
    }

    public void a(JSONObject jSONObject) {
        Log.d("DangerousService", "processPushMsg");
        if (jSONObject == null) {
            Log.e("DangerousService", "processPushMsg json is null");
        } else {
            new e(this, jSONObject).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }
}
