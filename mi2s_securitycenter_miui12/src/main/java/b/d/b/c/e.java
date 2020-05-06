package b.d.b.c;

import android.util.Log;

public class e {

    /* renamed from: a  reason: collision with root package name */
    public static final int f2139a;

    static {
        int i = d.f2138a;
        if (i >= 0) {
            f2139a = i;
            Log.i("MiCloudSdkBuild", "MiCloudSdk version: " + f2139a);
            return;
        }
        throw new RuntimeException("No MiCloudSDK runtime!");
    }
}
