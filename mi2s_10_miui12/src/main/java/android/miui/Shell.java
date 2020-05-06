package android.miui;

import android.os.Build;
import android.os.Environment;
import android.os.UserHandle;
import android.text.TextUtils;

public class Shell {
    private static final String EMULATED_EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH;
    private static final String EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH;
    private static final boolean SUPPORT_MULTI_USERS_EXTERNAL_STORAGE;
    private static final String TAG = "Shell";

    private static native boolean nativeBoostCpuPulse();

    private static native boolean nativeChmod(String str, int i, int i2);

    private static native boolean nativeChown(String str, int i, int i2, int i3);

    private static native boolean nativeCopy(String str, String str2);

    private static native long nativeGetRuntimeSharedValue(String str);

    private static native boolean nativeLink(String str, String str2);

    private static native boolean nativeMkdirs(String str);

    private static native boolean nativeMove(String str, String str2);

    private static native byte[] nativeReadByteArray(String str);

    private static native boolean nativeRemove(String str);

    private static native boolean nativeRun(String str);

    private static native boolean nativeRunShell(String str);

    private static native boolean nativeSetProperty(String str, String str2);

    private static native boolean nativeSetRuntimeSharedValue(String str, long j);

    private static native boolean nativeSetfilecon(String str, String str2);

    private static native boolean nativeWrite(String str, String str2);

    private static native boolean nativeWriteByteArray(String str, boolean z, byte[] bArr);

    static {
        String str;
        String str2;
        System.loadLibrary("shell_jni");
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
        if (externalStoragePath.endsWith("/")) {
            str = externalStoragePath;
        } else {
            str = externalStoragePath.concat("/");
        }
        EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH = str;
        String emulatedExternalStoragePath = getEmulatedStorageSource();
        if (emulatedExternalStoragePath.endsWith("/")) {
            str2 = emulatedExternalStoragePath;
        } else {
            str2 = emulatedExternalStoragePath.concat("/");
        }
        EMULATED_EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH = str2;
        SUPPORT_MULTI_USERS_EXTERNAL_STORAGE = !externalStoragePath.equals(Environment.getLegacyExternalStorageDirectory().getPath());
    }

    public static boolean chmod(String path, int mode) {
        return nativeChmod(getSystemVisibleExternalPath(path), mode, 0);
    }

    public static boolean chmodRecursion(String path, int mode) {
        return nativeChmod(getSystemVisibleExternalPath(path), mode, 1);
    }

    public static boolean chown(String path, int owner, int group) {
        return nativeChown(getSystemVisibleExternalPath(path), owner, group, 0);
    }

    public static boolean chownRecursion(String path, int owner, int group) {
        return nativeChown(getSystemVisibleExternalPath(path), owner, group, 1);
    }

    public static boolean write(String path, String buffer) {
        return nativeWrite(getSystemVisibleExternalPath(path), buffer);
    }

    public static boolean copy(String source, String dest) {
        return nativeCopy(getSystemVisibleExternalPath(source), getSystemVisibleExternalPath(dest));
    }

    public static boolean link(String oldPath, String newPath) {
        return nativeLink(getSystemVisibleExternalPath(oldPath), getSystemVisibleExternalPath(newPath));
    }

    public static boolean mkdirs(String path) {
        return nativeMkdirs(getSystemVisibleExternalPath(path));
    }

    public static boolean move(String oldPath, String newPath) {
        return nativeMove(getSystemVisibleExternalPath(oldPath), getSystemVisibleExternalPath(newPath));
    }

    public static boolean remove(String path) {
        return nativeRemove(getSystemVisibleExternalPath(path));
    }

    public static boolean run(String command, Object... args) {
        return nativeRun(args.length > 0 ? String.format(command, args) : command);
    }

    public static boolean runShell(String command, Object... args) {
        return nativeRunShell(args.length > 0 ? String.format(command, args) : command);
    }

    public static final boolean boostCpuPulse() {
        return false;
    }

    public static final boolean setfilecon(String path, String scontext) {
        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }
        return nativeSetfilecon(getSystemVisibleExternalPath(path), scontext);
    }

    public static final boolean setProperty(String key, String value) {
        return nativeSetProperty(key, value);
    }

    public static final boolean writeByteArray(String path, boolean append, byte[] bytes) {
        return nativeWriteByteArray(path, append, bytes);
    }

    public static final byte[] readByteArray(String path) {
        return nativeReadByteArray(path);
    }

    private static String getSystemVisibleExternalPath(String path) {
        String ret = path;
        if (!SUPPORT_MULTI_USERS_EXTERNAL_STORAGE || path == null || !path.startsWith(EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH)) {
            return ret;
        }
        return EMULATED_EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH + path.substring(EXTERNAL_STORAGE_PATH_WITH_ENDING_SLASH.length());
    }

    public static long getRuntimeSharedValue(String key) {
        if (TextUtils.isEmpty(key)) {
            return -1;
        }
        return nativeGetRuntimeSharedValue(key);
    }

    public static boolean setRuntimeSharedValue(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return nativeSetRuntimeSharedValue(key, value);
    }

    private static String getEmulatedStorageSource() {
        String emulatedExternalSource = System.getenv("EMULATED_STORAGE_SOURCE");
        if (emulatedExternalSource == null) {
            return Environment.getExternalStorageDirectory().getPath();
        }
        return emulatedExternalSource + "/" + String.valueOf(UserHandle.myUserId());
    }
}
