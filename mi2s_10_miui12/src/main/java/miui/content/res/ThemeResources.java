package miui.content.res;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.MiuiResources;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.miui.ResourcesManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeFallback;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.graphics.BitmapFactory;
import miui.telephony.phonenumber.Prefix;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ThemeResources {
    private static final String ATTR_FILTER_PATH = "path";
    static boolean DBG = false;
    public static final String DISABLE_PROVISION_THEME = (THEME_MAGIC_PATH + "disable_provision_theme");
    private static final String FILTER_DESCRIPTION_FILE = "filters.xml";
    public static final String FRAMEWORK_NAME = "framework-res";
    public static final String FRAMEWORK_PACKAGE = "android";
    public static final String ICONS_NAME = "icons";
    public static final String LANGUAGE_THEME_PATH = "/data/system/language/";
    public static final String LOCKSCREEN_NAME = "lockscreen";
    public static final String LOCKSCREEN_WALLPAPER_NAME = "lock_wallpaper";
    public static final String MIUI_NAME = "framework-miui-res";
    public static final String MIUI_PACKAGE = "miui";
    public static final String PROVISION_THEME_PATH = "/system/media/theme/provision/";
    public static final String SYSTEMUI_NAME = "com.android.systemui";
    public static final String SYSTEM_LANGUAGE_THEME_PATH = "/system/language/";
    public static final String SYSTEM_THEME_PATH = "/system/media/theme/default/";
    private static final String TAG_FILTER = "filter";
    private static final String TAG_PACKAGE = "package";
    public static final String THEME_DATA_CONFIG_DIR_PATH = "/data/system/theme_config/";
    public static final String THEME_MAGIC_PATH = (Build.VERSION.SDK_INT > 22 ? "/data/system/theme_magic/" : "/data/system/");
    public static final String THEME_PATH = "/data/system/theme/";
    public static final MetaData[] THEME_PATHS = {new MetaData(SYSTEM_THEME_PATH, true, true), new MetaData(PROVISION_THEME_PATH, true, true), new MetaData("/data/system/theme/", true, true)};
    public static final String THEME_RIGHTS_PATH = "/data/system/theme/rights/";
    public static final String THEME_VERSION_COMPATIBILITY_PATH = "/data/system/theme/compatibility-v12/";
    public static final String WALLPAPER_NAME = "wallpaper";
    public static final String sAppliedLockstyleConfigPath = "/data/system/theme/config.config";
    protected static int sCookieFramework = -1;
    protected static int sCookieMiuiExtFramework = -1;
    protected static int sCookieMiuiFramework = -1;
    protected static int sCookieMiuiSdk = -1;
    private static boolean sHasUpdatedAfterZygote = false;
    protected static boolean sIsZygote = false;
    private static Drawable sLockWallpaperCache;
    private static long sLockWallpaperModifiedTime;
    private static ThemeResourcesSystem sSystem;
    private ArrayList<FilterInfo> mFilterInfos = new ArrayList<>();
    private boolean mHasInitedDefaultValue;
    protected boolean mIsTop = true;
    protected boolean mIsUserThemePath = false;
    private LoadThemeConfigHelper mLoadThemeValuesCallback = new LoadThemeConfigHelper();
    protected MetaData mMetaData;
    protected boolean mNightMode;
    protected String mPackageName;
    protected ThemeZipFile mPackageZipFile;
    protected MiuiResources mResources;
    protected boolean mShouldFallbackDeeper;
    protected boolean mSupportWrapper;
    protected long mUpdatedTime;
    protected ThemeResources mWrapped;

    public enum ConfigType {
        THEME_VALUES,
        THEME_FALLBACK
    }

    public interface LoadThemeConfigCallback {
        void load(InputStream inputStream, ConfigType configType);
    }

    protected static final class MetaData {
        public boolean mSupportFile;
        public boolean mSupportValue;
        public String mThemePath;

        public MetaData(String themePath, boolean supportValue, boolean supportFile) {
            this.mThemePath = themePath;
            this.mSupportValue = supportValue;
            this.mSupportFile = supportFile;
        }
    }

    public static boolean needProvisionTheme() {
        return "scorpio".equals(miui.os.Build.DEVICE) && !new File(DISABLE_PROVISION_THEME).exists();
    }

    public static ThemeResourcesSystem getSystem() {
        ThemeResourcesSystem themeResourcesSystem;
        if (!sIsZygote && !sHasUpdatedAfterZygote && (themeResourcesSystem = sSystem) != null) {
            themeResourcesSystem.checkUpdate();
            sHasUpdatedAfterZygote = true;
        }
        return sSystem;
    }

    public static ThemeResources getSystem(MiuiResources resources) {
        if (sSystem == null) {
            sSystem = ThemeResourcesSystem.getTopLevelThemeResources(resources);
        }
        return sSystem;
    }

    public static ThemeResources getTopLevelThemeResources(MiuiResources resources, String componentName) {
        ThemeResources themeResources = null;
        boolean needProvisionTheme = needProvisionTheme();
        int i = 0;
        while (true) {
            MetaData[] metaDataArr = THEME_PATHS;
            if (i >= metaDataArr.length) {
                return themeResources;
            }
            if (needProvisionTheme || !PROVISION_THEME_PATH.equals(metaDataArr[i].mThemePath)) {
                themeResources = new ThemeResources(themeResources, resources, componentName, THEME_PATHS[i]);
            }
            i++;
        }
    }

    private static final String getPackageName(String componentName) {
        if (FRAMEWORK_NAME.equals(componentName) || "icons".equals(componentName)) {
            return FRAMEWORK_PACKAGE;
        }
        if (MIUI_NAME.equals(componentName) || "lockscreen".equals(componentName)) {
            return MIUI_PACKAGE;
        }
        return componentName;
    }

    protected static class FilterInfo {
        public ThemeFallback mFallback;
        public boolean mNightMode;
        public HashSet<String> mPackages;
        public String mPath;
        public ThemeValues mValues;

        public FilterInfo(String path, HashSet<String> packages) {
            this(path, packages, false);
        }

        public FilterInfo(String path, HashSet<String> packages, boolean nightMode) {
            this.mValues = new ThemeValues();
            this.mFallback = new ThemeFallback();
            this.mPath = path;
            this.mPackages = packages;
            this.mNightMode = nightMode;
        }

        public boolean match(String packageName, boolean nightMode) {
            HashSet<String> hashSet = this.mPackages;
            return (hashSet == null || hashSet.contains(packageName)) && this.mNightMode == nightMode;
        }
    }

    protected ThemeResources(ThemeResources wrapped, MiuiResources resources, String componentName, MetaData metaData) {
        boolean z = true;
        initSystemCookies(resources);
        if (wrapped != null) {
            this.mWrapped = wrapped;
            wrapped.mIsTop = false;
        }
        this.mResources = resources;
        this.mPackageName = getPackageName(componentName);
        this.mMetaData = metaData;
        this.mIsUserThemePath = "/data/system/theme/".equals(metaData.mThemePath);
        this.mPackageZipFile = ThemeZipFile.getThemeZipFile(metaData, componentName);
        this.mSupportWrapper = ("icons".equals(componentName) || "lockscreen".equals(componentName)) ? false : z;
        checkUpdate();
    }

    public long checkUpdate() {
        if (!sIsZygote) {
            long updatedTime = this.mPackageZipFile.checkUpdate();
            boolean z = false;
            if (this.mUpdatedTime != updatedTime) {
                this.mUpdatedTime = updatedTime;
                initBasePaths();
                loadThemeValues();
                this.mHasInitedDefaultValue = false;
            }
            if (this.mWrapped != null && (this.mSupportWrapper || !this.mPackageZipFile.isValid())) {
                z = true;
            }
            this.mShouldFallbackDeeper = z;
            if (this.mShouldFallbackDeeper) {
                if (PROVISION_THEME_PATH.equals(this.mWrapped.mMetaData.mThemePath) && !needProvisionTheme()) {
                    this.mWrapped = this.mWrapped.mWrapped;
                    this.mUpdatedTime = Math.max(this.mUpdatedTime, System.currentTimeMillis());
                }
                this.mUpdatedTime = Math.max(this.mUpdatedTime, this.mWrapped.checkUpdate());
            }
        }
        return this.mUpdatedTime;
    }

    public long getUpdateTime() {
        return this.mUpdatedTime;
    }

    public void setNightModeEnable(boolean enable) {
        this.mNightMode = enable;
        if (this.mShouldFallbackDeeper) {
            this.mWrapped.setNightModeEnable(enable);
        }
    }

    /* access modifiers changed from: protected */
    public ArrayList<FilterInfo> getFilterInfos() {
        return this.mFilterInfos;
    }

    public InputStream getThemeStream(String name, long[] size) {
        MiuiResources.ThemeFileInfoOption info = new MiuiResources.ThemeFileInfoOption(-1, name, true);
        if (getThemeFile(info) && size != null) {
            size[0] = info.outSize;
        }
        return info.outInputStream;
    }

    public boolean hasThemeFile(String path) {
        return getThemeFile(new MiuiResources.ThemeFileInfoOption(-1, path, false), this.mPackageName);
    }

    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption info) {
        return getThemeFile(info, this.mPackageName);
    }

    /* access modifiers changed from: protected */
    public boolean getThemeFileNonFallback(MiuiResources.ThemeFileInfoOption info) {
        return getThemeFileWithPath(info, this.mPackageName);
    }

    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption info, String filterKey) {
        return getThemeFile(info, filterKey, this.mPackageName);
    }

    /* access modifiers changed from: protected */
    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption info, String filterKey, String fallbackPkgName) {
        if (getThemeFileWithPath(info, filterKey)) {
            return true;
        }
        return getThemeFileWithFallback(info, filterKey, fallbackPkgName);
    }

    private boolean getThemeFileWithFallback(MiuiResources.ThemeFileInfoOption info, String filterKey, String fallbackPkgName) {
        MiuiResources.ThemeFileInfoOption themeFileInfoOption = info;
        if (!this.mIsUserThemePath) {
            String str = fallbackPkgName;
        } else if (!this.mPackageZipFile.isValid()) {
            String str2 = fallbackPkgName;
        } else {
            List<ThemeDefinition.FallbackInfo> compatiblityFallback = ThemeCompatibility.getMayFilterFallbackList(fallbackPkgName, ThemeDefinition.ResourceType.DRAWABLE, themeFileInfoOption.inResourcePath);
            if (compatiblityFallback != null) {
                String backup = themeFileInfoOption.inResourcePath;
                for (ThemeDefinition.FallbackInfo fallback : compatiblityFallback) {
                    if (fallback.mResType == ThemeDefinition.ResourceType.DRAWABLE && fallback.mResFallbackPkgName == null) {
                        themeFileInfoOption.inResourcePath = getFallbackDrawablePath(backup, fallback.mResOriginalName, fallback.mResFallbackName);
                        if (themeFileInfoOption.inResourcePath == null) {
                            continue;
                        } else if (fallback.mResPreferredConfigs != null) {
                            String prefix = null;
                            String suffix = null;
                            String[] strArr = fallback.mResPreferredConfigs;
                            int length = strArr.length;
                            int i = 0;
                            while (true) {
                                if (i >= length) {
                                    break;
                                }
                                String config = strArr[i];
                                int index = themeFileInfoOption.inResourcePath.indexOf(config);
                                if (index > 0) {
                                    prefix = themeFileInfoOption.inResourcePath.substring(0, index);
                                    suffix = themeFileInfoOption.inResourcePath.substring(config.length() + index);
                                    break;
                                }
                                i++;
                            }
                            if (prefix != null) {
                                for (String config2 : fallback.mResPreferredConfigs) {
                                    themeFileInfoOption.inResourcePath = prefix + config2 + suffix;
                                    if (getThemeFileWithPath(info, filterKey)) {
                                        themeFileInfoOption.inResourcePath = backup;
                                        return true;
                                    }
                                }
                                continue;
                            } else {
                                continue;
                            }
                        } else if (getThemeFileWithPath(info, filterKey)) {
                            themeFileInfoOption.inResourcePath = backup;
                            return true;
                        }
                    }
                }
                themeFileInfoOption.inResourcePath = backup;
            }
            return false;
        }
        return false;
    }

    private static String getFallbackDrawablePath(String resourcePath, String originalName, String fallbackName) {
        int resPathIndex = resourcePath.lastIndexOf(47) + 1;
        int i = 0;
        int j = 0;
        while (resPathIndex + i < resourcePath.length() && j < originalName.length()) {
            char resCh = resourcePath.charAt(resPathIndex + i);
            if (resCh != originalName.charAt(j)) {
                return null;
            }
            if (resCh == '.') {
                break;
            }
            i++;
            j++;
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(resourcePath, resPathIndex);
        buffer.append(fallbackName);
        String ret = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return ret;
    }

    private boolean getThemeFileWithPath(MiuiResources.ThemeFileInfoOption info, String filterKey) {
        if (this.mPackageZipFile.isValid()) {
            ArrayList<FilterInfo> filterInfos = getFilterInfos();
            for (int i = filterInfos.size() - 1; i >= 0; i--) {
                FilterInfo filter = filterInfos.get(i);
                if (filter.match(filterKey, this.mNightMode)) {
                    info.outFilterPath = filter.mPath;
                    if (this.mPackageZipFile.getThemeFile(info)) {
                        return true;
                    }
                    String originName = ThemeToolUtils.getNameFromPath(info.inResourcePath);
                    ThemeDefinition.FallbackInfo fallback = filter.mFallback.mFallbackInfoMap.get(originName);
                    if (fallback != null && fallback.mResType == ThemeDefinition.ResourceType.DRAWABLE && this.mPackageName.equals(fallback.mResFallbackPkgName)) {
                        String backup = info.inResourcePath;
                        info.inResourcePath = backup.replace(originName, fallback.mResFallbackName);
                        boolean result = this.mPackageZipFile.getThemeFile(info);
                        info.inResourcePath = backup;
                        if (result) {
                            return true;
                        }
                    }
                }
            }
            info.outFilterPath = null;
        }
        if (this.mShouldFallbackDeeper) {
            return this.mWrapped.getThemeFile(info, filterKey);
        }
        return false;
    }

    public void mergeThemeValues(String filterKey, ThemeValues values) {
        if (this.mShouldFallbackDeeper) {
            this.mWrapped.mergeThemeValues(filterKey, values);
        }
        if (this.mMetaData.mSupportValue) {
            boolean updateValues = false;
            ArrayList<FilterInfo> filterInfos = getFilterInfos();
            for (int i = 0; i < filterInfos.size(); i++) {
                FilterInfo filter = filterInfos.get(i);
                if (filter.match(filterKey, this.mNightMode) && !filter.mValues.isEmpty()) {
                    values.putAll(filter.mValues);
                    updateValues = true;
                }
            }
            if ((this.mHasInitedDefaultValue == 0 && this.mPackageZipFile.isValid()) || updateValues) {
                this.mHasInitedDefaultValue = true;
                values.mergeNewDefaultValueIfNeed(this.mResources, this.mPackageName);
            }
        }
    }

    private class LoadThemeConfigHelper implements LoadThemeConfigCallback {
        private FilterInfo mFilter;

        private LoadThemeConfigHelper() {
        }

        public void newTarget(FilterInfo filter) {
            this.mFilter = filter;
            ThemeResources.this.mPackageZipFile.loadThemeConfig(this, filter.mPath);
        }

        public void load(InputStream is, ConfigType type) {
            if (is == null) {
                return;
            }
            if (type == ConfigType.THEME_VALUES) {
                this.mFilter.mValues = ThemeValues.parseThemeValues(ThemeResources.this.mResources, is, ThemeResources.this.mPackageName);
            } else if (type == ConfigType.THEME_FALLBACK) {
                this.mFilter.mFallback = ThemeFallback.parseThemeFallback(ThemeResources.this.mResources, is, ThemeResources.this.mPackageName);
            }
        }
    }

    private void loadThemeValues() {
        ArrayList<FilterInfo> filterInfos = getFilterInfos();
        for (int i = 0; i < filterInfos.size(); i++) {
            this.mLoadThemeValuesCallback.newTarget(filterInfos.get(i));
        }
    }

    private List<FilterInfo> getFilterInfos(boolean isNightMode) {
        DocumentBuilderFactory factory;
        boolean z = isNightMode;
        ArrayList<FilterInfo> filterInfos = new ArrayList<>();
        String basePath = z ? "nightmode/" : Prefix.EMPTY;
        filterInfos.add(new FilterInfo(basePath, (HashSet<String>) null, z));
        InputStream input = this.mPackageZipFile.getZipInputStream(basePath + FILTER_DESCRIPTION_FILE);
        if (input == null) {
            return filterInfos;
        }
        try {
            DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
            NodeList filters = factory2.newDocumentBuilder().parse(input).getElementsByTagName(TAG_FILTER);
            int i = 0;
            int i2 = 0;
            while (i2 < filters.getLength()) {
                Element filter = (Element) filters.item(i2);
                String path = filter.getAttribute(ATTR_FILTER_PATH);
                if (TextUtils.isEmpty(path) || path.indexOf("/") != -1) {
                    factory = factory2;
                } else if ("res".equals(path)) {
                    factory = factory2;
                } else {
                    NodeList packages = filter.getElementsByTagName(TAG_PACKAGE);
                    HashSet<String> packageSet = new HashSet<>();
                    for (int j = i; j < packages.getLength(); j++) {
                        packageSet.add(packages.item(j).getFirstChild().getNodeValue());
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(basePath);
                    sb.append(path);
                    factory = factory2;
                    sb.append('/');
                    filterInfos.add(new FilterInfo(sb.toString(), packageSet, z));
                }
                i2++;
                factory2 = factory;
                i = 0;
            }
            try {
                input.close();
            } catch (IOException e) {
            }
        } catch (Exception e2) {
            input.close();
        } catch (Throwable th) {
            Throwable th2 = th;
            try {
                input.close();
            } catch (IOException e3) {
            }
            throw th2;
        }
        return filterInfos;
    }

    private void initBasePaths() {
        ArrayList<FilterInfo> filterInfos = new ArrayList<>();
        filterInfos.addAll(getFilterInfos(false));
        filterInfos.addAll(getFilterInfos(true));
        this.mFilterInfos = filterInfos;
    }

    private static void initSystemCookies(Resources resources) {
        if (resources != null) {
            if (Build.VERSION.SDK_INT > 27) {
                AssetManager assetManager = resources.getAssets();
                int cookieFramework = AssetManagerUtil.findCookieForPath(assetManager, "/system/framework/framework-res.apk");
                if (cookieFramework > 0) {
                    sCookieFramework = cookieFramework;
                }
                int cookieMiuiExtFramework = AssetManagerUtil.findCookieForPath(assetManager, ResourcesManager.FRAMEWORK_EXT_RES_PATH);
                if (cookieMiuiExtFramework > 0) {
                    sCookieMiuiExtFramework = cookieMiuiExtFramework;
                }
                int miuiFrameworkCookie = AssetManagerUtil.findCookieForPath(assetManager, ResourcesManager.MIUI_FRAMEWORK_RES_PATH);
                if (miuiFrameworkCookie > 0) {
                    sCookieMiuiFramework = miuiFrameworkCookie;
                } else if (AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.system-1.apk") > 0) {
                    sCookieMiuiFramework = AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.system-1.apk");
                } else if (AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.system-2.apk") > 0) {
                    sCookieMiuiFramework = AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.system-2.apk");
                }
                int miuiSDKCookie = AssetManagerUtil.findCookieForPath(assetManager, ResourcesManager.MIUI_SDK_RES_PATH);
                if (miuiSDKCookie > 0) {
                    sCookieMiuiSdk = miuiSDKCookie;
                } else if (AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.core-1.apk") > 0) {
                    sCookieMiuiSdk = AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.core-1.apk");
                } else if (AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.core-2.apk") > 0) {
                    sCookieMiuiSdk = AssetManagerUtil.findCookieForPath(assetManager, "/data/app/com.miui.core-2.apk");
                }
            } else {
                int cookie = 0;
                while (cookie < 100) {
                    if (sCookieFramework < 0 || sCookieMiuiExtFramework < 0 || sCookieMiuiFramework < 0 || sCookieMiuiSdk < 0) {
                        try {
                            String name = AssetManagerUtil.getCookieName(resources.getAssets(), cookie);
                            if ("/system/framework/framework-res.apk".equals(name)) {
                                sCookieFramework = cookie;
                            } else if (ResourcesManager.isMiuiExtFrameworkPath(name)) {
                                sCookieMiuiExtFramework = cookie;
                            } else if (ResourcesManager.isMiuiSystemSdkPath(name)) {
                                sCookieMiuiFramework = cookie;
                            } else if (ResourcesManager.isMiuiSdkPath(name)) {
                                sCookieMiuiSdk = cookie;
                            }
                        } catch (Exception e) {
                        }
                        cookie++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    protected static boolean isMiuiResourceCookie(int cookie) {
        return cookie == sCookieMiuiSdk || cookie == sCookieMiuiFramework || cookie == sCookieMiuiExtFramework;
    }

    public static boolean isAppResourceCookie(int cookie) {
        return sCookieFramework != cookie && !isMiuiResourceCookie(cookie);
    }

    public static final void clearLockWallpaperCache() {
        sLockWallpaperModifiedTime = 0;
        sLockWallpaperCache = null;
    }

    private static void getRealSize(Display display, Point outPoint) {
        Class<Display> cls = Display.class;
        try {
            cls.getDeclaredMethod("getRealSize", new Class[]{Point.class, Boolean.TYPE}).invoke(display, new Object[]{outPoint, true});
        } catch (Exception e) {
            Log.e("LockWallpaper", "no getRealSize hack method");
            display.getRealSize(outPoint);
        }
    }

    public static final Drawable getLockWallpaperCache(Context context) {
        File file = sSystem.getLockscreenWallpaper();
        if (file == null || !file.exists()) {
            return null;
        }
        if (sLockWallpaperModifiedTime == file.lastModified()) {
            return sLockWallpaperCache;
        }
        sLockWallpaperCache = null;
        try {
            Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            Point size = new Point();
            getRealSize(display, size);
            int width = size.x;
            int height = size.y;
            if (width > height) {
                Log.e("LockWallpaper", "Wrong display metrics for width = " + width + " and height = " + height);
                int tmp = width;
                width = height;
                height = tmp;
            }
            Bitmap bitmap = BitmapFactory.decodeBitmap(file.getAbsolutePath(), width, height, false);
            if (bitmap != null) {
                sLockWallpaperCache = new BitmapDrawable(context.getResources(), bitmap);
                sLockWallpaperModifiedTime = file.lastModified();
            }
        } catch (Exception e) {
            Log.e("ThemeResources", e.getMessage(), e);
        } catch (OutOfMemoryError error) {
            Log.e("ThemeResources", error.getMessage(), error);
        }
        return sLockWallpaperCache;
    }
}
