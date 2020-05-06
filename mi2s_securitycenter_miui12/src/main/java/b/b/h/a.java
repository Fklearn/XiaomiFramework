package b.b.h;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class a {
    public a(Context context, String str) {
        Log.d("NativeInterstitialAd", "cn constructor");
    }

    public void a(Activity activity) {
        Log.d("NativeInterstitialAd", "cn show");
    }

    public boolean a() {
        Log.d("NativeInterstitialAd", "cn isReady");
        return false;
    }

    public void b() {
        Log.d("NativeInterstitialAd", "cn loadAd");
    }
}
