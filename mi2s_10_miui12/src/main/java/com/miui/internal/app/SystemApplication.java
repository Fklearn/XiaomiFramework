package com.miui.internal.app;

import android.app.Application;
import java.util.Map;
import miui.core.SdkManager;

public class SystemApplication extends Application {
    public SystemApplication() {
        SdkManager.initialize(this, (Map) null);
    }

    public void onCreate() {
        super.onCreate();
        SdkManager.start((Map) null);
    }
}
