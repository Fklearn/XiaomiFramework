package miui.content.res;

import android.content.res.MiuiResources;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.miui.internal.content.res.ThemeDensityFallbackUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import miui.content.res.ThemeResources;

public final class ThemeResourcesSystem extends ThemeResources {
    private static final String ADVANCE_LOCKSCREEN_NAME = "advance/";
    private static ThemeResources sIcons;
    private static ThemeResources sLockscreen;
    private static ThemeResources sMiui;
    private static long sUpdatedTimeIcon;
    private static long sUpdatedTimeLockscreen;
    private String mThemePath;

    static ThemeResourcesSystem getTopLevelThemeResources(MiuiResources resources) {
        ThemeResourcesSystem frameworkResource = null;
        sIsZygote = true;
        if (ThemeCompatibility.isThemeEnabled()) {
            sIcons = ThemeResources.getTopLevelThemeResources(resources, "icons");
            sLockscreen = ThemeResources.getTopLevelThemeResources(resources, "lockscreen");
            sMiui = ThemeResources.getTopLevelThemeResources(resources, ThemeResources.MIUI_NAME);
            boolean needProvisionTheme = needProvisionTheme();
            for (int i = 0; i < THEME_PATHS.length; i++) {
                if (needProvisionTheme || !ThemeResources.PROVISION_THEME_PATH.equals(THEME_PATHS[i].mThemePath)) {
                    frameworkResource = new ThemeResourcesSystem(frameworkResource, resources, ThemeResources.FRAMEWORK_NAME, THEME_PATHS[i]);
                }
            }
        } else {
            sIcons = ThemeResourcesEmpty.sInstance;
            sLockscreen = ThemeResourcesEmpty.sInstance;
            sMiui = ThemeResourcesEmpty.sInstance;
            frameworkResource = new ThemeResourcesSystem((ThemeResourcesSystem) null, resources, "FakeForEmpty", THEME_PATHS[0]);
        }
        sIsZygote = false;
        if ((resources.getConfiguration().uiMode & 32) != 0) {
            frameworkResource.setNightModeEnable(true);
        }
        return frameworkResource;
    }

    public void setNightModeEnable(boolean enable) {
        super.setNightModeEnable(enable);
        sMiui.setNightModeEnable(enable);
    }

    protected ThemeResourcesSystem(ThemeResourcesSystem wrapped, MiuiResources resources, String componentName, ThemeResources.MetaData metaData) {
        super(wrapped, resources, componentName, metaData);
        this.mThemePath = metaData.mThemePath;
    }

    public long checkUpdate() {
        if (!sIsZygote) {
            super.checkUpdate();
            if (this.mIsTop) {
                long updatedTime = sIcons.checkUpdate();
                if (sUpdatedTimeIcon < updatedTime) {
                    sUpdatedTimeIcon = updatedTime;
                    IconCustomizer.refreshIconShapeMask();
                    IconCustomizer.clearCache();
                }
                long updatedTime2 = sLockscreen.checkUpdate();
                if (sUpdatedTimeLockscreen < updatedTime2) {
                    sUpdatedTimeLockscreen = updatedTime2;
                    if (!hasAwesomeLockscreen()) {
                        new File("/data/system/theme/lockscreen").delete();
                        sUpdatedTimeLockscreen = sLockscreen.checkUpdate();
                    }
                }
                this.mUpdatedTime = Math.max(this.mUpdatedTime, sUpdatedTimeLockscreen);
                this.mUpdatedTime = Math.max(this.mUpdatedTime, sMiui.checkUpdate());
            }
        }
        return this.mUpdatedTime;
    }

    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption info, String filterKey) {
        if (isMiuiResourceCookie(info.inCookie)) {
            return sMiui.getThemeFile(info, filterKey);
        }
        return getThemeFileStreamSystem(info, filterKey);
    }

    private boolean getThemeFileStreamSystem(MiuiResources.ThemeFileInfoOption info, String filterKey) {
        if (!ThemeCompatibility.isThemeEnabled()) {
            return false;
        }
        if (info.inResourcePath.endsWith("sym_def_app_icon.png")) {
            return getIcon(info, "sym_def_app_icon.png");
        }
        if (info.inResourcePath.endsWith("default_wallpaper.jpg")) {
            return false;
        }
        return super.getThemeFile(info, filterKey);
    }

    public void mergeThemeValues(String filterKey, ThemeValues values) {
        super.mergeThemeValues(filterKey, values);
        if (this.mIsTop) {
            sMiui.mergeThemeValues(filterKey, values);
            sLockscreen.mergeThemeValues(filterKey, values);
        }
    }

    public void resetIcons() {
        sIcons.checkUpdate();
    }

    public void resetLockscreen() {
        sLockscreen.checkUpdate();
    }

    public boolean hasIcon(String name) {
        return sIcons.hasThemeFile(name);
    }

    public InputStream getIconStream(String name, long[] size) {
        return sIcons.getThemeStream(name, size);
    }

    public Bitmap getIconBitmap(String name) {
        MiuiResources.ThemeFileInfoOption info = new MiuiResources.ThemeFileInfoOption(true);
        if (!getIcon(info, name)) {
            return null;
        }
        Bitmap icon = null;
        try {
            icon = BitmapFactory.decodeStream(info.outInputStream);
            if (icon != null) {
                icon.setDensity(info.outDensity);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        try {
            info.outInputStream.close();
        } catch (IOException e2) {
        }
        return icon;
    }

    public Bitmap[] getIconBitmaps(String name) {
        ArrayList<Bitmap> iconBitmaps = new ArrayList<>();
        int suffixIndex = name.lastIndexOf(46);
        if (suffixIndex > 0) {
            String iconFileDirName = name.substring(0, suffixIndex);
            for (int i = 0; i < 5; i++) {
                Bitmap bitmap = getIconBitmap(iconFileDirName + "/" + i + ".png");
                if (bitmap == null) {
                    break;
                }
                iconBitmaps.add(bitmap);
            }
        }
        if (iconBitmaps.size() > 0) {
            return (Bitmap[]) iconBitmaps.toArray(new Bitmap[iconBitmaps.size()]);
        }
        return null;
    }

    private boolean getIcon(MiuiResources.ThemeFileInfoOption info, String name) {
        if (!ThemeCompatibility.isThemeEnabled()) {
            return false;
        }
        int density = Resources.getSystem().getConfiguration().densityDpi;
        int i = Resources.getSystem().getConfiguration().smallestScreenWidthDp;
        info.inResourcePath = "res/drawable" + ThemeDensityFallbackUtils.getScreenWidthSuffix(Resources.getSystem().getConfiguration()) + ThemeDensityFallbackUtils.getDensitySuffix(density) + "/" + name;
        info.inDensity = density;
        if (sIcons.getThemeFile(info)) {
            return true;
        }
        info.inResourcePath = "res/drawable" + ThemeDensityFallbackUtils.getDensitySuffix(density) + "/" + name;
        if (sIcons.getThemeFile(info)) {
            return true;
        }
        info.inResourcePath = name;
        info.inDensity = 240;
        return sIcons.getThemeFile(info);
    }

    public File getLockscreenWallpaper() {
        String path = this.mThemePath;
        File ret = new File(path + ThemeResources.LOCKSCREEN_WALLPAPER_NAME);
        if (ret.exists() || this.mWrapped == null) {
            return ret;
        }
        return ((ThemeResourcesSystem) this.mWrapped).getLockscreenWallpaper();
    }

    private boolean hasAwesomeLockscreen() {
        return sLockscreen.hasThemeFile("advance/manifest.xml");
    }

    public boolean containsAwesomeLockscreenEntry(String entry) {
        ThemeResources themeResources = sLockscreen;
        return themeResources.hasThemeFile(ADVANCE_LOCKSCREEN_NAME + entry);
    }

    public InputStream getAwesomeLockscreenFileStream(String name, long[] size) {
        ThemeResources themeResources = sLockscreen;
        return themeResources.getThemeStream(ADVANCE_LOCKSCREEN_NAME + name, size);
    }
}
