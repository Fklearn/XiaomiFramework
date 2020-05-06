package miui.content.res;

import android.content.res.AssetManager;
import java.io.IOException;
import miui.theme.ThemeFileUtils;
import miui.theme.ThemePermissionUtils;

public class ThemeNativeUtils {
    private static native boolean nIsContainXXXhdpiResource(AssetManager assetManager);

    private static native void nTerminateAtlas();

    static {
        System.loadLibrary("themeutils_jni");
    }

    public static void terminateAtlas() {
        nTerminateAtlas();
    }

    public static boolean isContainXXXhdpiResource(AssetManager asset) {
        return nIsContainXXXhdpiResource(asset);
    }

    public static boolean updateFilePermissionWithThemeContext(String path) {
        return ThemePermissionUtils.updateFilePermissionWithThemeContext(path);
    }

    public static boolean updateFilePermissionWithThemeContext(String path, boolean systemReady) {
        return ThemePermissionUtils.updateFilePermissionWithThemeContext(path, systemReady);
    }

    public static boolean copy(String src, String dest) {
        return ThemeFileUtils.copy(src, dest);
    }

    public static boolean remove(String path) {
        return ThemeFileUtils.remove(path);
    }

    public static boolean mkdirs(String path) {
        return ThemeFileUtils.mkdirs(path);
    }

    public static void write(String fileName, String data) throws IOException {
        ThemeFileUtils.write(fileName, data);
    }

    public static void link(String oldPath, String newPath) throws IOException {
        ThemeFileUtils.link(oldPath, newPath);
    }

    public static void deleteContents(String path) {
        ThemeFileUtils.deleteContents(path);
    }
}
