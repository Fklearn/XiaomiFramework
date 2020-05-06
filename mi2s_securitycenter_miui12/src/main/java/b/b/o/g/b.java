package b.b.o.g;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.c;
import java.util.HashMap;
import java.util.Map;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static Map<String, String> f1888a = new HashMap();

    static {
        f1888a.put("陕西咸阳", "293");
    }

    public static String a(Context context, CharSequence charSequence) {
        c.a a2 = c.a.a("miui.telephony.PhoneNumberUtils$PhoneNumber");
        a2.b("getLocation", new Class[]{Context.class, CharSequence.class}, context, charSequence);
        return a2.f();
    }

    public static String b(Context context, CharSequence charSequence) {
        String a2 = a(context, charSequence);
        if (!TextUtils.isEmpty(a2)) {
            String str = f1888a.get(a2);
            if (!TextUtils.isEmpty(str)) {
                return str;
            }
        } else {
            Log.i("PhoneNumberUtil", "get location failed");
        }
        c.a a3 = c.a.a("miui.telephony.PhoneNumberUtils$PhoneNumber");
        a3.b("getLocationAreaCode", new Class[]{Context.class, String.class}, context, charSequence);
        return a3.f();
    }
}
