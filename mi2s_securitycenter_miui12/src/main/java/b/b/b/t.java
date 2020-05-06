package b.b.b;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import b.b.b.b;
import b.b.b.d.j;
import b.b.b.d.l;
import b.b.c.j.x;
import com.miui.antivirus.activity.VirusMonitorDialogActivity;
import com.miui.antivirus.model.k;
import com.miui.earthquakewarning.model.WarningModel;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.VirusInfo;
import com.miui.guardprovider.b;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securityscan.model.system.VirusScanModel;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class t {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f1587a = "t";

    /* renamed from: b  reason: collision with root package name */
    private static ArrayList<String> f1588b = new ArrayList<>();

    private static class a extends VirusObserver {

        /* renamed from: c  reason: collision with root package name */
        private b f1589c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public Context f1590d;
        /* access modifiers changed from: private */
        public k e;
        private List<String> f = new ArrayList();

        public a(Context context, k kVar) {
            this.f1590d = context;
            this.e = kVar;
            this.f1589c = b.a((Context) Application.d());
            this.f = t.c();
        }

        public void a(int i, int i2, VirusInfo[] virusInfoArr) {
            VirusInfo virusInfo = virusInfoArr[0];
            b.c a2 = this.f1589c.a(virusInfo.virusLevel);
            if (a2 != b.c.SAFE) {
                String a3 = t.c(this.f1590d, virusInfo.packageName);
                this.e.a(this.f1589c.a(virusInfo.virusLevel));
                this.e.d(virusInfo.virusDescription);
                this.e.e(virusInfo.virusName);
                this.e.a(virusInfo.virusLevel);
                if (!this.f.contains(a3)) {
                    new Handler(Looper.getMainLooper()).post(new s(this));
                } else {
                    String b2 = t.f1587a;
                    Log.i(b2, "Not report because installer is in white list! installer = " + a3 + ", virusLevel: " + a2);
                }
                String b3 = t.f1587a;
                Log.d(b3, "onScanProgress" + virusInfo.toString());
                String charSequence = DateFormat.format("yyyy-MM-dd", com.miui.common.persistence.b.a(this.f1590d.getString(R.string.preference_key_database_auto_update_time, new Object[]{virusInfo.engineName}), 0)).toString();
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("pkgName", virusInfo.packageName);
                    jSONObject.put(Constants.JSON_KEY_APP_VERSION, virusInfo.versionName);
                    jSONObject.put("appName", this.e.a());
                    jSONObject.put(WarningModel.Columns.SIGNATURE, l.c(this.f1590d, this.e.e()));
                    jSONObject.put("virusName", virusInfo.virusName);
                    jSONObject.put(MiStat.Param.LEVEL, b.c.RISK == a2 ? "RISK" : VirusScanModel.KEY_DEFAULT);
                    jSONObject.put("appType", b.C0024b.INSTALLED_APP == this.e.c() ? "INSTALLED_APP" : "UNINSTALLED_APK");
                    jSONObject.put("virusDesc", virusInfo.virusDescription);
                    jSONObject.put("reportSource", "INSTALL_MONITOR");
                    jSONObject.put("reportEngine", virusInfo.engineName);
                    jSONObject.put("reportEngineVersion", charSequence);
                    jSONObject.put("installationSource", a3);
                    j.a(this.f1590d, jSONObject);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }

        public void a(int i, String str) {
            super.a(i, str);
        }

        public void a(int i, VirusInfo[] virusInfoArr) {
            super.a(i, virusInfoArr);
            com.miui.guardprovider.b.a(this.f1590d).a();
        }

        public void d(int i) {
            super.d(i);
        }
    }

    static {
        f1588b.add("com.xiaomi.market");
        f1588b.add("com.android.vending");
        f1588b.add("com.xiaomi.gamecenter");
    }

    /* access modifiers changed from: private */
    public static void b(Context context, Bundle bundle) {
        Intent intent = new Intent(context, VirusMonitorDialogActivity.class);
        intent.addFlags(268435456);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void b(Context context, String str) {
        k kVar = new k();
        try {
            PackageInfo c2 = x.c(context, str);
            if (c2 != null) {
                if ((c2.applicationInfo.flags & 1) == 0) {
                    kVar.a(c2.applicationInfo.loadLabel(context.getPackageManager()).toString());
                    kVar.b(c2.packageName);
                    kVar.c(c2.applicationInfo.sourceDir);
                    kVar.a(b.C0024b.INSTALLED_APP);
                }
                com.miui.guardprovider.b.a(context).a((b.a) new r(kVar, context));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public static String c(Context context, String str) {
        if (Build.VERSION.SDK_INT <= 22) {
            return null;
        }
        try {
            String installerPackageName = context.getPackageManager().getInstallerPackageName(str);
            String str2 = f1587a;
            Log.d(str2, "installer: " + installerPackageName);
            return installerPackageName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static List<String> c() {
        String a2 = com.miui.common.persistence.b.a("key_install_white_list", "");
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(a2)) {
            return f1588b;
        }
        try {
            JSONArray jSONArray = new JSONArray(a2);
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(jSONArray.getString(i));
            }
        } catch (Exception e) {
            Log.e(f1587a, e.toString());
        }
        return arrayList;
    }
}
