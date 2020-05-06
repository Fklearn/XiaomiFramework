package miui.util;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;
import miui.reflect.Method;

public class FBEUtils {
    private FBEUtils() {
    }

    public static Context getSafeStorageContext(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            return createDeviceProtectedStorageContext(context);
        }
        if (Build.VERSION.SDK_INT < 24 || isDeviceProtectedStorage(context) || !isFileEncryptedNativeOrEmulated()) {
            return context;
        }
        return createDeviceProtectedStorageContext(context);
    }

    private static boolean isDeviceProtectedStorage(Context context) {
        try {
            return Method.of(context.getClass(), "isDeviceProtectedStorage", Boolean.TYPE, new Class[0]).invokeBoolean(context.getClass(), context, new Object[0]);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isFileEncryptedNativeOrEmulated() {
        try {
            return Method.of(StorageManager.class, "isFileEncryptedNativeOrEmulated", Boolean.TYPE, new Class[0]).invokeBoolean(StorageManager.class, (Object) null, new Object[0]);
        } catch (Exception e) {
            return false;
        }
    }

    private static Context createDeviceProtectedStorageContext(Context context) {
        try {
            return (Context) Method.of(context.getClass(), "createDeviceProtectedStorageContext", Context.class, new Class[0]).invokeObject(context.getClass(), context, new Object[0]);
        } catch (Exception e) {
            return context;
        }
    }
}
