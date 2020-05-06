package com.market.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.market.sdk.utils.a;

public class i {
    @SuppressLint({"StaticFieldLeak"})

    /* renamed from: a  reason: collision with root package name */
    private static volatile i f2230a;

    public static i a(Application application) {
        if (f2230a == null) {
            synchronized (i.class) {
                if (f2230a == null) {
                    f2230a = new i();
                    application.registerActivityLifecycleCallbacks(new c());
                }
            }
        }
        return f2230a;
    }

    public boolean a(Activity activity, int i) {
        try {
            FloatService.a(a.a()).a(activity.toString(), i);
            return true;
        } catch (Exception e) {
            Log.e("MarketManager", e.toString());
            return false;
        }
    }

    public boolean a(String str) {
        if (!TextUtils.isEmpty(str) && str.contains("&overlayPosition=")) {
            try {
                FloatService.a(a.a()).c(Uri.parse(str));
                return true;
            } catch (Exception e) {
                Log.e("MarketManager", e.toString());
            }
        }
        return false;
    }
}
