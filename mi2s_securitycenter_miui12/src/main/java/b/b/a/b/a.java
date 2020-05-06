package b.b.a.b;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static final String f1316a = (Build.VERSION.SDK_INT > 20 ? "com.android.server.telecom" : "com.android.phone");

    public static Intent a(Context context, Intent intent, String str) {
        if (intent != null) {
            intent.setPackage(str);
        }
        return intent;
    }
}
