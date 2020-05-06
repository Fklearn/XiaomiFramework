package b.b.b;

import android.os.Binder;
import android.text.format.DateFormat;
import android.util.Log;
import b.b.b.d.j;
import b.b.b.d.l;
import b.b.b.d.n;
import b.b.b.o;
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
import org.json.JSONObject;

class k extends VirusObserver {

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ o f1556c;

    k(o oVar) {
        this.f1556c = oVar;
    }

    public void a(int i, int i2, VirusInfo[] virusInfoArr) {
        VirusInfo virusInfo = virusInfoArr[0];
        o.g a2 = this.f1556c.a(virusInfo.virusLevel);
        if (a2 != o.g.SAFE) {
            String a3 = C0322e.a(this.f1556c.f, virusInfo.packageName);
            Log.i("PaySafetyCheckManager", "background scan : virus risk = " + virusInfo.packageName + "; virusLevel = " + virusInfo.virusLevel);
            if (!this.f1556c.f1561b.contains(a3)) {
                this.f5457b.a(4);
            } else {
                Log.i("PaySafetyCheckManager", "Not report because installer is in white list! installer = " + a3);
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            String charSequence = DateFormat.format("yyyy-MM-dd", b.a(this.f1556c.f.getString(R.string.preference_key_database_auto_update_time, new Object[]{virusInfo.engineName}), 0)).toString();
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("pkgName", virusInfo.packageName);
                jSONObject.put(Constants.JSON_KEY_APP_VERSION, virusInfo.versionName);
                jSONObject.put("appName", this.f1556c.h.getApplicationInfo(virusInfo.packageName, 0).loadLabel(this.f1556c.h).toString());
                jSONObject.put(WarningModel.Columns.SIGNATURE, l.b(this.f1556c.f, virusInfo.packageName));
                jSONObject.put("virusName", virusInfo.virusName);
                jSONObject.put(MiStat.Param.LEVEL, o.g.RISK == a2 ? "RISK" : VirusScanModel.KEY_DEFAULT);
                jSONObject.put("appType", "INSTALLED_APP");
                jSONObject.put("virusDesc", virusInfo.virusDescription);
                jSONObject.put("reportSource", "SECURITY_SCAN_BACKGROUND");
                jSONObject.put("reportEngine", virusInfo.engineName);
                jSONObject.put("reportEngineVersion", charSequence);
                jSONObject.put("installationSource", a3);
                j.a(this.f1556c.f, jSONObject);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void a(int i, VirusInfo[] virusInfoArr) {
        this.f5457b.e();
    }

    public void d(int i) {
        super.d(i);
        List unused = this.f1556c.f1561b = n.a();
    }
}
