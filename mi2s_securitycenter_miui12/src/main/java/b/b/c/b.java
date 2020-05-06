package b.b.c;

import b.b.c.j.i;
import b.b.o.g.c;
import com.miui.networkassistant.config.Constants;
import miui.os.Build;
import miui.util.Log;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static String f1604a = "Constants";

    /* renamed from: b  reason: collision with root package name */
    public static final String f1605b = (Build.IS_INTERNATIONAL_BUILD ? Constants.App.ACTION_NETWORK_ASSISTANT_MONTH_PACKAGE_SETTING : Constants.App.ACTION_NETWORK_ASSISTANT_OPERATOR_SETTING);

    /* renamed from: c  reason: collision with root package name */
    public static final CharSequence f1606c = "root";

    /* renamed from: d  reason: collision with root package name */
    public static final CharSequence f1607d = "Interactive Shell";
    public static final String[] e = {"com.mi.globalbrowser", "com.android.browser"};

    public static final class a {

        /* renamed from: a  reason: collision with root package name */
        public static final String f1608a = i.a();
    }

    public static int a() {
        try {
            return ((Integer) c.a(Class.forName("com.android.internal.R$dimen"), "navigation_bar_width")).intValue();
        } catch (Exception e2) {
            Log.e(f1604a, "getNavigationBarSize exception: ", e2);
            return 0;
        }
    }
}
