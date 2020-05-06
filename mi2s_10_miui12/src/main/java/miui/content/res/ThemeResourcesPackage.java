package miui.content.res;

import android.content.res.MiuiResources;
import android.os.Process;
import android.os.StrictMode;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.content.res.ThemeResources;

public final class ThemeResourcesPackage extends ThemeResources {
    private static final Map<String, WeakReference<ThemeResourcesPackage>> sPackageResources = new HashMap();

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: miui.content.res.ThemeResourcesPackage} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static miui.content.res.ThemeResourcesPackage getThemeResources(android.content.res.MiuiResources r6, java.lang.String r7) {
        /*
            android.os.StrictMode$ThreadPolicy r0 = allowDiskReads()
            r1 = 0
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<miui.content.res.ThemeResourcesPackage>> r2 = sPackageResources
            boolean r2 = r2.containsKey(r7)
            if (r2 == 0) goto L_0x001e
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<miui.content.res.ThemeResourcesPackage>> r2 = sPackageResources
            java.lang.Object r2 = r2.get(r7)
            java.lang.ref.WeakReference r2 = (java.lang.ref.WeakReference) r2
            if (r2 == 0) goto L_0x001e
            java.lang.Object r3 = r2.get()
            r1 = r3
            miui.content.res.ThemeResourcesPackage r1 = (miui.content.res.ThemeResourcesPackage) r1
        L_0x001e:
            if (r1 != 0) goto L_0x0054
            miui.content.res.ThemeResourcesPackage r1 = getTopLevelThemeResources(r6, r7)
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<miui.content.res.ThemeResourcesPackage>> r2 = sPackageResources
            monitor-enter(r2)
            r3 = 0
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<miui.content.res.ThemeResourcesPackage>> r4 = sPackageResources     // Catch:{ all -> 0x0051 }
            boolean r4 = r4.containsKey(r7)     // Catch:{ all -> 0x0051 }
            if (r4 == 0) goto L_0x0041
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<miui.content.res.ThemeResourcesPackage>> r4 = sPackageResources     // Catch:{ all -> 0x0051 }
            java.lang.Object r4 = r4.get(r7)     // Catch:{ all -> 0x0051 }
            java.lang.ref.WeakReference r4 = (java.lang.ref.WeakReference) r4     // Catch:{ all -> 0x0051 }
            if (r4 == 0) goto L_0x0041
            java.lang.Object r5 = r4.get()     // Catch:{ all -> 0x0051 }
            miui.content.res.ThemeResourcesPackage r5 = (miui.content.res.ThemeResourcesPackage) r5     // Catch:{ all -> 0x0051 }
            r3 = r5
        L_0x0041:
            if (r3 != 0) goto L_0x004e
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<miui.content.res.ThemeResourcesPackage>> r4 = sPackageResources     // Catch:{ all -> 0x0051 }
            java.lang.ref.WeakReference r5 = new java.lang.ref.WeakReference     // Catch:{ all -> 0x0051 }
            r5.<init>(r1)     // Catch:{ all -> 0x0051 }
            r4.put(r7, r5)     // Catch:{ all -> 0x0051 }
            goto L_0x004f
        L_0x004e:
            r1 = r3
        L_0x004f:
            monitor-exit(r2)     // Catch:{ all -> 0x0051 }
            goto L_0x0054
        L_0x0051:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0051 }
            throw r3
        L_0x0054:
            resetOldPolicy(r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.content.res.ThemeResourcesPackage.getThemeResources(android.content.res.MiuiResources, java.lang.String):miui.content.res.ThemeResourcesPackage");
    }

    public static ThemeResourcesPackage getTopLevelThemeResources(MiuiResources resources, String packageName) {
        ThemeResourcesPackage themeResources = null;
        boolean needProvisionTheme = needProvisionTheme();
        for (int i = 0; i < THEME_PATHS.length; i++) {
            if (needProvisionTheme || !ThemeResources.PROVISION_THEME_PATH.equals(THEME_PATHS[i].mThemePath)) {
                themeResources = new ThemeResourcesPackage(themeResources, resources, packageName, THEME_PATHS[i]);
            }
        }
        if ((resources.getConfiguration().uiMode & 32) != 0) {
            themeResources.setNightModeEnable(true);
        }
        return themeResources;
    }

    protected ThemeResourcesPackage(ThemeResourcesPackage wrapped, MiuiResources resources, String packageName, ThemeResources.MetaData metaData) {
        super(wrapped, resources, packageName, metaData);
    }

    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption info) {
        if (!isAppResourceCookie(info.inCookie)) {
            return loadFrameworkThemeFile(info);
        }
        if (super.getThemeFile(info)) {
            return true;
        }
        if (!this.mPackageZipFile.isValid()) {
            return false;
        }
        List<ThemeDefinition.FallbackInfo> fList = ThemeCompatibility.getMayFilterFallbackList(this.mPackageName, ThemeDefinition.ResourceType.DRAWABLE, info.inResourcePath);
        if (fList != null) {
            for (ThemeDefinition.FallbackInfo fallback : fList) {
                if (loadAppThemeFileFromMiuiFramework(info, fallback)) {
                    return true;
                }
            }
        }
        Iterator<ThemeResources.FilterInfo> it = getFilterInfos().iterator();
        while (it.hasNext()) {
            ThemeResources.FilterInfo filter = it.next();
            if (filter.match(this.mPackageName, this.mNightMode)) {
                if (loadAppThemeFileFromMiuiFramework(info, filter.mFallback.mFallbackInfoMap.get(ThemeToolUtils.getNameFromPath(info.inResourcePath)))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean loadAppThemeFileFromMiuiFramework(MiuiResources.ThemeFileInfoOption info, ThemeDefinition.FallbackInfo fallback) {
        if (fallback == null || fallback.mResType != ThemeDefinition.ResourceType.DRAWABLE || !ThemeResources.MIUI_PACKAGE.equals(fallback.mResFallbackPkgName) || !info.inResourcePath.endsWith(fallback.mResOriginalName)) {
            return false;
        }
        int backupCookie = info.inCookie;
        String backupPath = info.inResourcePath;
        info.inCookie = sCookieMiuiFramework;
        info.inResourcePath = backupPath.replace(fallback.mResOriginalName, fallback.mResFallbackName);
        boolean result = loadFrameworkThemeFile(info);
        info.inResourcePath = backupPath;
        info.inCookie = backupCookie;
        return result;
    }

    private boolean loadFrameworkThemeFile(MiuiResources.ThemeFileInfoOption info) {
        List<ThemeDefinition.FallbackInfo> fList;
        if (this.mPackageZipFile.isValid()) {
            if (isMiuiResourceCookie(info.inCookie) && (fList = ThemeCompatibility.getMayFilterFallbackList(ThemeResources.MIUI_PACKAGE, ThemeDefinition.ResourceType.DRAWABLE, info.inResourcePath)) != null) {
                String backup = info.inResourcePath;
                for (ThemeDefinition.FallbackInfo fallback : fList) {
                    if (this.mPackageName.equals(fallback.mResFallbackPkgName) && backup.endsWith(fallback.mResOriginalName)) {
                        info.inResourcePath = backup.replace(fallback.mResOriginalName, fallback.mResFallbackName);
                        boolean result = super.getThemeFileNonFallback(info);
                        info.inResourcePath = backup;
                        if (result) {
                            return true;
                        }
                    }
                }
            }
            boolean result2 = false;
            String path = info.inResourcePath;
            if (sCookieFramework == info.inCookie) {
                info.inResourcePath = "framework-res/" + path;
                result2 = super.getThemeFile(info, this.mPackageName, ThemeResources.FRAMEWORK_PACKAGE);
            } else if (isMiuiResourceCookie(info.inCookie)) {
                info.inResourcePath = "framework-miui-res/" + path;
                result2 = super.getThemeFile(info, this.mPackageName, ThemeResources.MIUI_PACKAGE);
            }
            info.inResourcePath = path;
            if (result2) {
                info.outFilterPath = "package/only";
                return true;
            }
        }
        return getSystem().getThemeFile(info, this.mPackageName);
    }

    public void mergeThemeValues(String filterKey, ThemeValues values) {
        StrictMode.ThreadPolicy oldPolicy = allowDiskReads();
        if (this.mIsTop) {
            getSystem().mergeThemeValues(filterKey, values);
        }
        super.mergeThemeValues(filterKey, values);
        resetOldPolicy(oldPolicy);
    }

    private static StrictMode.ThreadPolicy allowDiskReads() {
        if (Process.myUid() == 0) {
            return null;
        }
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
        StrictMode.allowThreadDiskWrites();
        return oldPolicy;
    }

    private static void resetOldPolicy(StrictMode.ThreadPolicy oldPolicy) {
        if (Process.myUid() != 0) {
            StrictMode.setThreadPolicy(oldPolicy);
        }
    }
}
