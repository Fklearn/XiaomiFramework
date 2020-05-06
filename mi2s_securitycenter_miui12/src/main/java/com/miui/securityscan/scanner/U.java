package com.miui.securityscan.scanner;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import b.b.b.b;
import b.b.b.d.j;
import b.b.b.d.l;
import b.b.b.d.n;
import b.b.c.f.a;
import b.b.c.j.d;
import b.b.c.j.x;
import com.miui.antivirus.model.k;
import com.miui.appmanager.C0322e;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.IAntiVirusServer;
import com.miui.guardprovider.aidl.VirusInfo;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.b.f;
import com.miui.securityscan.b.g;
import com.miui.securityscan.model.GroupModel;
import com.miui.securityscan.model.system.VirusScanModel;
import com.miui.securityscan.scanner.O;
import com.xiaomi.stat.MiStat;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.util.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

class U {

    /* renamed from: a  reason: collision with root package name */
    private static U f7874a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f7875b;

    /* renamed from: c  reason: collision with root package name */
    private b f7876c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public b.b.c.f.a f7877d;

    private static class a extends VirusObserver implements a.C0027a {
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public int f7878c = -1;

        /* renamed from: d  reason: collision with root package name */
        private g f7879d;
        /* access modifiers changed from: private */
        public Map<String, k> e;
        private Object f = new Object();
        private boolean g;
        private Context h;
        private b.b.c.f.a i;
        private b j;
        /* access modifiers changed from: private */
        public IAntiVirusServer k;
        private boolean l;
        private List<String> m = new ArrayList();

        public a(Context context, g gVar, boolean z) {
            this.h = context;
            this.f7879d = gVar;
            this.l = z;
            a();
        }

        private void a() {
            this.j = b.a(this.h);
            this.i = b.b.c.f.a.a(this.h);
            this.e = this.l ? U.e(this.h) : U.d(this.h);
            this.m = n.a();
        }

        private void b() {
            synchronized (this.f) {
                if (!this.g) {
                    this.i.b("com.miui.guardprovider.action.antivirusservice");
                    this.g = true;
                }
            }
        }

        public void a(int i2, int i3, VirusInfo[] virusInfoArr) {
            StringBuilder sb = new StringBuilder();
            sb.append("GPObserver onScanProgress total : ");
            sb.append(i3);
            sb.append(" , current : ");
            int i4 = i2 + 1;
            sb.append(i4);
            Log.w("SystemCheckManager", sb.toString());
            try {
                if (virusInfoArr.length != 0) {
                    VirusInfo virusInfo = virusInfoArr[0];
                    Log.w("SystemCheckManager", "GPObserver app:" + virusInfo.packageName);
                    b.c a2 = this.j.a(virusInfo.virusLevel);
                    k kVar = this.e.get(virusInfo.path);
                    kVar.a(a2);
                    String a3 = C0322e.a(this.h, virusInfo.packageName);
                    if (this.m.contains(a3) && a2 != b.c.SAFE) {
                        kVar.a(b.c.SAFE);
                        Log.i("SystemCheckManager", "Not report because installer is in white list! installer = " + a3 + ", virusLevel: " + a2);
                    }
                    this.f7879d.a(i4, this.e.size(), kVar);
                    String charSequence = DateFormat.format("yyyy-MM-dd", com.miui.common.persistence.b.a(this.h.getString(R.string.preference_key_database_auto_update_time, new Object[]{virusInfo.engineName}), 0)).toString();
                    if (b.c.SAFE != a2) {
                        try {
                            JSONObject jSONObject = new JSONObject();
                            jSONObject.put("pkgName", virusInfo.packageName);
                            jSONObject.put(Constants.JSON_KEY_APP_VERSION, virusInfo.versionName);
                            jSONObject.put("appName", kVar.a());
                            jSONObject.put(WarningModel.Columns.SIGNATURE, l.c(this.h, kVar.e()));
                            jSONObject.put("virusName", virusInfo.virusName);
                            jSONObject.put(MiStat.Param.LEVEL, b.c.RISK == a2 ? "RISK" : VirusScanModel.KEY_DEFAULT);
                            jSONObject.put("appType", b.C0024b.INSTALLED_APP == kVar.c() ? "INSTALLED_APP" : "UNINSTALLED_APK");
                            jSONObject.put("virusDesc", virusInfo.virusDescription);
                            jSONObject.put("reportSource", "HOMEPAGE_SCAN");
                            jSONObject.put("reportEngine", virusInfo.engineName);
                            jSONObject.put("reportEngineVersion", charSequence);
                            jSONObject.put("installationSource", a3);
                            j.a(this.h, jSONObject);
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e3) {
                Log.e("SystemCheckManager", "GPObserver onScanProgress() InterruptedException ", e3);
                try {
                    this.k.e(this.f7878c);
                } catch (RemoteException e4) {
                    Log.e("SystemCheckManager", "GPObserver onScanProgress() InterruptedException$RemoteException ", e4);
                }
                this.f7879d.a();
                b();
            }
        }

        public void a(int i2, VirusInfo[] virusInfoArr) {
            Log.w("SystemCheckManager", "GPObserver onScanFinish");
            this.f7879d.a((List<GroupModel>) null, 10);
            b();
        }

        public boolean a(IBinder iBinder) {
            this.k = IAntiVirusServer.Stub.a(iBinder);
            d.a(new T(this));
            return false;
        }

        public void d(int i2) {
            super.d(i2);
            this.f7879d.b();
            Log.w("SystemCheckManager", "GPObserver onScanStart");
            if (i2 == -1) {
                a(i2, (VirusInfo[]) null);
            }
        }
    }

    private U(Context context) {
        this.f7875b = context;
        this.f7876c = b.a(context);
        this.f7877d = b.b.c.f.a.a(context);
    }

    public static synchronized U c(Context context) {
        U u;
        synchronized (U.class) {
            if (f7874a == null) {
                f7874a = new U(context.getApplicationContext());
            }
            u = f7874a;
        }
        return u;
    }

    /* access modifiers changed from: private */
    public static Map<String, k> d(Context context) {
        Log.d("SystemCheckManager", "getAllScanAppPaths start");
        List<PackageInfo> a2 = b.b.c.b.b.a(context).a();
        HashMap hashMap = new HashMap();
        for (PackageInfo packageInfo : a2) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((applicationInfo.flags & 1) == 0) {
                k kVar = new k();
                kVar.a(b.C0024b.INSTALLED_APP);
                kVar.b(applicationInfo.packageName);
                kVar.a(x.j(context, applicationInfo.packageName).toString());
                kVar.c(applicationInfo.sourceDir);
                hashMap.put(applicationInfo.sourceDir, kVar);
            }
        }
        Log.d("SystemCheckManager", "getAllScanAppPaths start apks");
        Cursor cursor = null;
        PackageManager packageManager = context.getPackageManager();
        ContentResolver contentResolver = context.getContentResolver();
        try {
            cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), new String[]{"_data", "date_modified"}, "_data LIKE '%.apk'", (String[]) null, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String string = cursor.getString(0);
                    PackageInfo d2 = x.d(context, string);
                    if (d2 != null) {
                        k kVar2 = new k();
                        kVar2.a(d2.applicationInfo.loadLabel(packageManager).toString());
                        kVar2.b(d2.packageName);
                        kVar2.c(string);
                        kVar2.a(b.C0024b.UNINSTALLED_APK);
                        hashMap.put(string, kVar2);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("SystemCheckManager", "getAllScanAppPaths Exception", e);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        Log.d("SystemCheckManager", "getAllScanAppPaths end");
        return hashMap;
    }

    /* access modifiers changed from: private */
    public static Map<String, k> e(Context context) {
        Log.d("SystemCheckManager", "getRunningAppPaths start");
        List<ActivityManager.RunningAppProcessInfo> b2 = b.b.c.b.b.a(context).b();
        HashMap hashMap = new HashMap();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : b2) {
            String[] strArr = runningAppProcessInfo.pkgList;
            for (int i = 0; i < strArr.length; i++) {
                try {
                    ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(strArr[i], 0);
                    if ((applicationInfo.flags & 1) == 0) {
                        k kVar = new k();
                        kVar.a(b.C0024b.INSTALLED_APP);
                        kVar.b(applicationInfo.packageName);
                        kVar.a(x.j(context, applicationInfo.packageName).toString());
                        kVar.c(applicationInfo.sourceDir);
                        hashMap.put(applicationInfo.sourceDir, kVar);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("SystemCheckManager", "getRunningAppPaths NameNotFoundException", e);
                }
            }
        }
        Log.d("SystemCheckManager", "getRunningAppPaths end");
        return hashMap;
    }

    public void a(g gVar) {
        d.a(new P(this, gVar));
    }

    public void a(List<k> list) {
        for (k a2 : list) {
            this.f7876c.a(a2);
        }
    }

    public void a(List<GroupModel> list, O.c cVar, f fVar) {
        d.a(new S(this, list, cVar, fVar));
    }

    public void a(boolean z, g gVar) {
        d.a(new Q(this, gVar, z));
    }
}
