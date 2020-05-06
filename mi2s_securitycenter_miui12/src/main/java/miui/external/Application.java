package miui.external;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import miui.external.SdkConstants;

public class Application extends android.app.Application implements SdkConstants {
    private static final String PACKAGE_NAME = "com.miui.core";
    private ApplicationDelegate mApplicationDelegate;
    private boolean mInitialized;
    private boolean mStarted;

    public Application() {
        if (loadSdk() && initializeSdk()) {
            this.mInitialized = true;
        }
    }

    private void handleGenericError(Throwable th) {
        while (th != null && th.getCause() != null && ((th instanceof InvocationTargetException) || (th instanceof ExceptionInInitializerError))) {
            th = th.getCause();
        }
        Log.e(SdkConstants.LOG_TAG, "MIUI SDK encounter errors, please contact miuisdk@xiaomi.com for support.", th);
        SdkErrorInstrumentation.handleSdkError(SdkConstants.SdkError.GENERIC);
    }

    private void handleUnknownError(String str, int i) {
        Log.e(SdkConstants.LOG_TAG, "MIUI SDK encounter errors, please contact miuisdk@xiaomi.com for support. phase: " + str + " code: " + i);
        SdkErrorInstrumentation.handleSdkError(SdkConstants.SdkError.GENERIC);
    }

    private boolean initializeSdk() {
        try {
            HashMap hashMap = new HashMap();
            int intValue = ((Integer) SdkEntranceHelper.getSdkEntrance().getMethod("initialize", new Class[]{android.app.Application.class, Map.class}).invoke((Object) null, new Object[]{this, hashMap})).intValue();
            if (intValue == 0) {
                return true;
            }
            handleUnknownError("initialize", intValue);
            return false;
        } catch (Throwable th) {
            handleGenericError(th);
            return false;
        }
    }

    private boolean loadSdk() {
        try {
            if (SdkHelper.isMiuiSystem() || SdkLoader.load(SdkHelper.getApkPath((Context) null, PACKAGE_NAME, "miui"), (String) null, SdkHelper.getLibPath((Context) null, PACKAGE_NAME), Application.class.getClassLoader())) {
                return true;
            }
            SdkErrorInstrumentation.handleSdkError(SdkConstants.SdkError.NO_SDK);
            return false;
        } catch (Throwable th) {
            handleGenericError(th);
            return false;
        }
    }

    private boolean startSdk() {
        try {
            HashMap hashMap = new HashMap();
            int intValue = ((Integer) SdkEntranceHelper.getSdkEntrance().getMethod(TtmlNode.START, new Class[]{Map.class}).invoke((Object) null, new Object[]{hashMap})).intValue();
            if (intValue == 1) {
                SdkErrorInstrumentation.handleSdkError(SdkConstants.SdkError.LOW_SDK_VERSION);
                return false;
            } else if (intValue == 0) {
                return true;
            } else {
                handleUnknownError(TtmlNode.START, intValue);
                return false;
            }
        } catch (Throwable th) {
            handleGenericError(th);
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        if (this.mInitialized && startSdk()) {
            this.mApplicationDelegate = onCreateApplicationDelegate();
            ApplicationDelegate applicationDelegate = this.mApplicationDelegate;
            if (applicationDelegate != null) {
                applicationDelegate.attach(this);
            }
            this.mStarted = true;
        }
    }

    public final ApplicationDelegate getApplicationDelegate() {
        return this.mApplicationDelegate;
    }

    public final void onConfigurationChanged(Configuration configuration) {
        ApplicationDelegate applicationDelegate = this.mApplicationDelegate;
        if (applicationDelegate != null) {
            applicationDelegate.onConfigurationChanged(configuration);
        } else {
            superOnConfigurationChanged(configuration);
        }
    }

    public final void onCreate() {
        if (this.mStarted) {
            ApplicationDelegate applicationDelegate = this.mApplicationDelegate;
            if (applicationDelegate != null) {
                applicationDelegate.onCreate();
            } else {
                superOnCreate();
            }
        }
    }

    public ApplicationDelegate onCreateApplicationDelegate() {
        return null;
    }

    public final void onLowMemory() {
        ApplicationDelegate applicationDelegate = this.mApplicationDelegate;
        if (applicationDelegate != null) {
            applicationDelegate.onLowMemory();
        } else {
            superOnLowMemory();
        }
    }

    public final void onTerminate() {
        ApplicationDelegate applicationDelegate = this.mApplicationDelegate;
        if (applicationDelegate != null) {
            applicationDelegate.onTerminate();
        } else {
            superOnTerminate();
        }
    }

    public final void onTrimMemory(int i) {
        ApplicationDelegate applicationDelegate = this.mApplicationDelegate;
        if (applicationDelegate != null) {
            applicationDelegate.onTrimMemory(i);
        } else {
            superOnTrimMemory(i);
        }
    }

    /* access modifiers changed from: package-private */
    public final void superOnConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    /* access modifiers changed from: package-private */
    public final void superOnCreate() {
        super.onCreate();
    }

    /* access modifiers changed from: package-private */
    public final void superOnLowMemory() {
        super.onLowMemory();
    }

    /* access modifiers changed from: package-private */
    public final void superOnTerminate() {
        super.onTerminate();
    }

    /* access modifiers changed from: package-private */
    public final void superOnTrimMemory(int i) {
        super.onTrimMemory(i);
    }
}
