package b.d.b.c;

import android.util.Log;

public class d {

    /* renamed from: a  reason: collision with root package name */
    public static final int f2138a = a();

    static {
        Log.i("MiCloudSDKDependencyUtil", "MiCloudSDK environment: " + f2138a);
    }

    private static int a() {
        Class a2 = f.a("com.xiaomi.micloudsdk.os.MiCloudSdkVersion");
        if (a2 != null) {
            return f.a(a2, "version");
        }
        if (f.a("miui.cloud.helper.BroadcastIntentHelper") != null) {
            return 25;
        }
        return f.a("com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants") != null ? 18 : -1;
    }
}
