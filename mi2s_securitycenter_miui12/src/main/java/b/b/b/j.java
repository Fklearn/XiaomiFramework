package b.b.b;

import android.text.format.DateFormat;
import android.util.Log;
import b.b.b.d.l;
import b.b.b.d.n;
import b.b.b.o;
import com.miui.antivirus.model.e;
import com.miui.appmanager.C0322e;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.VirusInfo;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.system.VirusScanModel;
import com.xiaomi.stat.MiStat;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

class j extends VirusObserver {

    /* renamed from: c  reason: collision with root package name */
    private long f1554c = 0;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ o f1555d;

    j(o oVar) {
        this.f1555d = oVar;
    }

    public void a(int i, int i2, VirusInfo[] virusInfoArr) {
        o.g gVar;
        VirusInfo virusInfo = virusInfoArr[0];
        o.g a2 = this.f1555d.a(virusInfo.virusLevel);
        e a3 = this.f1555d.b(virusInfo.path);
        if (a3 != null) {
            a3.a(a2);
            a3.i(virusInfo.virusDescription);
            a3.j(virusInfo.virusName);
            a3.a(e.b.APP);
            a3.a(e.a.VIRUS);
            String a4 = C0322e.a(this.f1555d.f, virusInfo.packageName);
            if (this.f1555d.f1561b.contains(a4) && a2 != (gVar = o.g.SAFE)) {
                a3.a(gVar);
                Log.i("PaySafetyCheckManager", "Not report because installer is in white list! installer = " + a4 + ", virusLevel: " + a2);
            }
            this.f5456a.a(a3);
            String charSequence = DateFormat.format("yyyy-MM-dd", b.a(this.f1555d.f.getString(R.string.preference_key_database_auto_update_time, new Object[]{virusInfo.engineName}), 0)).toString();
            if (o.g.SAFE != a2) {
                Log.i("PaySafetyCheckManager", "foreground scan : virus risk = " + virusInfo.packageName + "; virusLevel = " + virusInfo.virusLevel);
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("pkgName", a3.m());
                    jSONObject.put(Constants.JSON_KEY_APP_VERSION, a3.i());
                    jSONObject.put("appName", a3.h());
                    jSONObject.put(WarningModel.Columns.SIGNATURE, l.c(this.f1555d.f, a3.q()));
                    jSONObject.put("virusName", a3.u());
                    jSONObject.put(MiStat.Param.LEVEL, o.g.RISK == a3.p() ? "RISK" : VirusScanModel.KEY_DEFAULT);
                    jSONObject.put("virusDesc", a3.t());
                    jSONObject.put("appType", o.f.INSTALLED_APP == a3.o() ? "INSTALLED_APP" : "UNINSTALLED_APK");
                    jSONObject.put("reportSource", "SECURITY_SCAN_FOREGROUND");
                    jSONObject.put("reportEngine", virusInfo.engineName);
                    jSONObject.put("reportEngineVersion", charSequence);
                    jSONObject.put("installationSource", a4);
                    b.b.b.d.j.a(this.f1555d.f, jSONObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0055, code lost:
        if (r4.f1555d.A.getAndSet(!r4.f1555d.A.get()) != false) goto L_0x0057;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r5, com.miui.guardprovider.aidl.VirusInfo[] r6) {
        /*
            r4 = this;
            java.lang.String r5 = "PaySafetyCheckManager"
            java.lang.String r6 = "virus scan finished ..."
            android.util.Log.w(r5, r6)
            b.b.b.o r5 = r4.f1555d
            java.lang.Object r5 = r5.o
            monitor-enter(r5)
            b.b.b.o r6 = r4.f1555d     // Catch:{ all -> 0x0065 }
            java.lang.Long r6 = r6.g     // Catch:{ all -> 0x0065 }
            long r0 = r6.longValue()     // Catch:{ all -> 0x0065 }
            r2 = 0
            int r6 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x0063
            b.b.b.o r6 = r4.f1555d     // Catch:{ all -> 0x0065 }
            java.lang.Long r0 = java.lang.Long.valueOf(r2)     // Catch:{ all -> 0x0065 }
            java.lang.Long unused = r6.g = r0     // Catch:{ all -> 0x0065 }
            b.b.b.o$d r6 = r4.f5456a     // Catch:{ all -> 0x0065 }
            r6.f()     // Catch:{ all -> 0x0065 }
            b.b.b.o r6 = r4.f1555d     // Catch:{ all -> 0x0065 }
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0065 }
            long r2 = r4.f1554c     // Catch:{ all -> 0x0065 }
            long r0 = r0 - r2
            long unused = r6.t = r0     // Catch:{ all -> 0x0065 }
            boolean r6 = miui.os.Build.IS_INTERNATIONAL_BUILD     // Catch:{ all -> 0x0065 }
            if (r6 != 0) goto L_0x0057
            b.b.b.o r6 = r4.f1555d     // Catch:{ all -> 0x0065 }
            java.util.concurrent.atomic.AtomicBoolean r6 = r6.A     // Catch:{ all -> 0x0065 }
            b.b.b.o r0 = r4.f1555d     // Catch:{ all -> 0x0065 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r0.A     // Catch:{ all -> 0x0065 }
            boolean r0 = r0.get()     // Catch:{ all -> 0x0065 }
            if (r0 != 0) goto L_0x0050
            r0 = 1
            goto L_0x0051
        L_0x0050:
            r0 = 0
        L_0x0051:
            boolean r6 = r6.getAndSet(r0)     // Catch:{ all -> 0x0065 }
            if (r6 == 0) goto L_0x0063
        L_0x0057:
            java.lang.String r6 = "PaySafetyCheckManager"
            java.lang.String r0 = "signature scan first finished , now virus scan finished !"
            android.util.Log.w(r6, r0)     // Catch:{ all -> 0x0065 }
            b.b.b.o$d r6 = r4.f5456a     // Catch:{ all -> 0x0065 }
            r6.c()     // Catch:{ all -> 0x0065 }
        L_0x0063:
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            return
        L_0x0065:
            r6 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.b.j.a(int, com.miui.guardprovider.aidl.VirusInfo[]):void");
    }

    public void d(int i) {
        this.f1554c = System.currentTimeMillis();
        List unused = this.f1555d.f1561b = n.a();
        if (i == -1) {
            a(i, (VirusInfo[]) null);
        }
    }
}
