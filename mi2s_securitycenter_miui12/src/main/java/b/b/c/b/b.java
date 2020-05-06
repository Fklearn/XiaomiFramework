package b.b.c.b;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.miui.networkassistant.config.Constants;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f1609a;

    /* renamed from: b  reason: collision with root package name */
    private PackageManager f1610b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public final Object f1611c = new Object();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Map<String, c> f1612d = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public SoftReference<ArrayList<PackageInfo>> e = new SoftReference<>(new ArrayList());
    private ActivityManager f;
    /* access modifiers changed from: private */
    public WeakReference<c> g;

    private class a extends BroadcastReceiver {
        private a() {
        }

        public void onReceive(Context context, Intent intent) {
            c cVar;
            if (intent != null && intent.getAction() != null) {
                if (intent.getAction().equals(Constants.System.ACTION_PACKAGE_REMOVED) || intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
                    Uri data = intent.getData();
                    if (data != null) {
                        b.this.f1612d.remove(data.getSchemeSpecificPart());
                        if (!(b.this.g == null || (cVar = (c) b.this.g.get()) == null)) {
                            cVar.h();
                        }
                    } else {
                        return;
                    }
                }
                synchronized (b.this.f1611c) {
                    b.this.e.clear();
                }
            }
        }
    }

    /* renamed from: b.b.c.b.b$b  reason: collision with other inner class name */
    private class C0026b extends BroadcastReceiver {
        private C0026b() {
        }

        public void onReceive(Context context, Intent intent) {
            b.this.f1612d.clear();
        }
    }

    public interface c {
        void h();
    }

    private b(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.f1610b = applicationContext.getPackageManager();
        this.f = (ActivityManager) applicationContext.getSystemService("activity");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addDataScheme("package");
        applicationContext.registerReceiver(new a(), intentFilter);
        applicationContext.registerReceiver(new C0026b(), new IntentFilter(Constants.System.ACTION_LOCALE_CHANGED));
    }

    public static synchronized b a(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f1609a == null) {
                f1609a = new b(context);
            }
            bVar = f1609a;
        }
        return bVar;
    }

    public c a(ApplicationInfo applicationInfo) {
        String str = applicationInfo.packageName;
        c cVar = this.f1612d.get(str);
        if (cVar != null) {
            return cVar;
        }
        String charSequence = applicationInfo.loadLabel(this.f1610b).toString();
        c cVar2 = new c();
        cVar2.a(charSequence);
        cVar2.a(applicationInfo.uid);
        cVar2.b(applicationInfo.packageName);
        this.f1612d.put(str, cVar2);
        return cVar2;
    }

    public c a(String str) {
        if (str == null) {
            return null;
        }
        c cVar = this.f1612d.get(str);
        if (cVar != null) {
            return cVar;
        }
        ApplicationInfo applicationInfo = this.f1610b.getApplicationInfo(str, 0);
        String charSequence = applicationInfo.loadLabel(this.f1610b).toString();
        c cVar2 = new c();
        cVar2.a(charSequence);
        cVar2.a(applicationInfo.uid);
        cVar2.b(applicationInfo.packageName);
        this.f1612d.put(str, cVar2);
        return cVar2;
    }

    public List<PackageInfo> a() {
        synchronized (this.f1611c) {
            ArrayList arrayList = this.e.get();
            if (arrayList != null && !arrayList.isEmpty()) {
                return arrayList;
            }
            ArrayList arrayList2 = new ArrayList();
            arrayList2.addAll(this.f1610b.getInstalledPackages(PsExtractor.AUDIO_STREAM));
            this.e = new SoftReference<>(arrayList2);
            return arrayList2;
        }
    }

    public void a(c cVar) {
        if (cVar != null) {
            this.g = new WeakReference<>(cVar);
        }
    }

    public List<ActivityManager.RunningAppProcessInfo> b() {
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses;
        synchronized (this.f1611c) {
            runningAppProcesses = this.f.getRunningAppProcesses();
        }
        return runningAppProcesses;
    }
}
