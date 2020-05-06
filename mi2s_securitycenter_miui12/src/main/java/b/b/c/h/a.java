package b.b.c.h;

import android.os.Build;
import b.b.c.j.y;
import com.miui.activityutil.h;
import miui.os.Build;

public final class a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1720a = (Build.IS_INTERNATIONAL_BUILD ? "https://api.sec.intl.miui.com" : "https://api.sec.miui.com");

    /* renamed from: b  reason: collision with root package name */
    protected static final String f1721b = (f1720a + "/health/v1/getOptimizationFile2");

    /* renamed from: c  reason: collision with root package name */
    private static final String f1722c = (b.b.c.a.f1594a ? "http://staging.adv.sec.miui.com" : Build.IS_INTERNATIONAL_BUILD ? "https://adv.sec.intl.miui.com" : "https://adv.sec.miui.com");

    /* renamed from: d  reason: collision with root package name */
    protected static final String f1723d = (f1722c + "/info/layout");
    protected static final String e = y.a("ro.product.device", h.f2289a);
    protected static final String f = y.a("ro.carrier.name", h.f2289a);
    protected static final String g = Build.getRegion();
    protected static final String h = ("MIUI-" + Build.VERSION.INCREMENTAL);
}
