package com.miui.permcenter.install;

import android.os.Build;
import com.miui.activityutil.o;
import com.miui.permcenter.compact.SystemPropertiesCompat;
import java.util.ArrayList;
import miui.os.Build;

public class UnknownSourceVerifyActivity extends AdbInstallVerifyActivity {
    private static final String f = (Build.IS_INTERNATIONAL_BUILD ? "https://srv.sec.intl.miui.com/data/unknownSources" : "https://srv.sec.miui.com/data/unknownSources");

    public static void a(boolean z) {
        SystemPropertiesCompat.set("persist.security.uks_opened", z ? o.f2310b : o.f2309a);
    }

    public static boolean d() {
        return SystemPropertiesCompat.getBoolean("persist.security.uks_opened", false);
    }

    /* access modifiers changed from: protected */
    public void b() {
        String str = android.os.Build.DEVICE;
        if (Build.VERSION.SDK_INT == 24 || d()) {
            c();
            finish();
            return;
        }
        if (Build.VERSION.SDK_INT >= 25) {
            ArrayList arrayList = new ArrayList();
            arrayList.add("meri");
            arrayList.add("rolex");
            arrayList.add("gemini");
            arrayList.add("natrium");
            arrayList.add("lithium");
            arrayList.add("scorpio");
            arrayList.add("santoni");
            arrayList.add("chiron");
            arrayList.add("sagit");
            arrayList.add("tiffany");
            arrayList.add("oxygen");
            arrayList.add("jason");
            arrayList.add("riva");
            arrayList.add("ugglite");
            arrayList.add("ugg");
            if (arrayList.contains(str)) {
                c();
                finish();
                return;
            }
        }
        this.f6122d = f;
        a();
    }

    /* access modifiers changed from: protected */
    public void c() {
        setResult(-1);
        a(true);
    }
}
