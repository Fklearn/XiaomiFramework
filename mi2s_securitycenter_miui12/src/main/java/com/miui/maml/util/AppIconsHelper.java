package com.miui.maml.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.UserHandle;
import com.miui.maml.RenderThread;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.RendererCoreCache;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class AppIconsHelper {
    public static final int TIME_DAY = 86400000;
    public static final int TIME_HOUR = 3600000;
    public static final int TIME_MIN = 60000;
    private static HashMap<String, WeakReference<ResourceManager>> mAnimatingIconsResourceManagers = new HashMap<>();
    private static final RendererCoreCache.OnCreateRootCallback mOnCreateRootCallback = new RendererCoreCache.OnCreateRootCallback() {
        public void onCreateRoot(ScreenElementRoot screenElementRoot) {
            if (screenElementRoot != null) {
                screenElementRoot.setScaleByDensity(true);
            }
        }
    };
    private static RendererCoreCache mRendererCoreCache;
    private static int mThemeChanged;

    private AppIconsHelper() {
    }

    private static void checkVersion(Context context) {
        int Configuration_getThemeChanged = HideSdkDependencyUtils.Configuration_getThemeChanged(context.getResources().getConfiguration());
        if (Configuration_getThemeChanged > mThemeChanged) {
            clearCache();
            mThemeChanged = Configuration_getThemeChanged;
        }
    }

    public static void cleanUp() {
        RenderThread.globalThreadStop();
    }

    public static void clearCache() {
        RendererCoreCache rendererCoreCache = mRendererCoreCache;
        if (rendererCoreCache != null) {
            rendererCoreCache.clear();
        }
        HashMap<String, WeakReference<ResourceManager>> hashMap = mAnimatingIconsResourceManagers;
        if (hashMap != null) {
            hashMap.clear();
        }
    }

    public static Drawable getFancyIconDrawable(Context context, String str, String str2, long j, UserHandle userHandle) {
        ActivityInfo activityInfo;
        try {
            activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(str, str2), 0);
        } catch (Exception unused) {
            activityInfo = null;
        }
        return getIconDrawable(context, activityInfo, str, str2, j, userHandle, true);
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager) {
        return getIconDrawable(context, packageItemInfo, packageManager, 0);
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager, long j) {
        return getIconDrawable(context, packageItemInfo, packageManager, j, HideSdkDependencyUtils.UserHandle_getInstance_with_int(HideSdkDependencyUtils.Context_getUserId(context)));
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, PackageManager packageManager, long j, UserHandle userHandle) {
        Drawable iconDrawable = getIconDrawable(context, packageItemInfo, packageItemInfo.packageName, (Build.VERSION.SDK_INT <= 24 || !(packageItemInfo instanceof ApplicationInfo)) ? packageItemInfo.name : null, j, userHandle);
        return iconDrawable != null ? iconDrawable : packageItemInfo.loadIcon(packageManager);
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, String str, String str2, long j) {
        return getIconDrawable(context, packageItemInfo, str, str2, j, HideSdkDependencyUtils.UserHandle_getInstance_with_int(HideSdkDependencyUtils.Context_getUserId(context)));
    }

    public static Drawable getIconDrawable(Context context, PackageItemInfo packageItemInfo, String str, String str2, long j, UserHandle userHandle) {
        return getIconDrawable(context, packageItemInfo, str, str2, j, userHandle, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: com.miui.maml.AnimatingDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: com.miui.maml.FancyDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: com.miui.maml.AnimatingDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: com.miui.maml.AnimatingDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: com.miui.maml.AnimatingDrawable} */
    /* JADX WARNING: type inference failed for: r3v16, types: [com.miui.maml.ResourceManager] */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00cb A[Catch:{ Exception -> 0x00d2 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.Drawable getIconDrawable(android.content.Context r12, android.content.pm.PackageItemInfo r13, java.lang.String r14, java.lang.String r15, long r16, android.os.UserHandle r18, boolean r19) {
        /*
            com.miui.maml.util.RendererCoreCache r0 = mRendererCoreCache
            if (r0 != 0) goto L_0x0014
            com.miui.maml.util.RendererCoreCache r0 = new com.miui.maml.util.RendererCoreCache
            android.os.Handler r1 = new android.os.Handler
            android.os.Looper r2 = android.os.Looper.getMainLooper()
            r1.<init>(r2)
            r0.<init>(r1)
            mRendererCoreCache = r0
        L_0x0014:
            r1 = 0
            checkVersion(r12)     // Catch:{ Exception -> 0x00d2 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2 }
            r0.<init>()     // Catch:{ Exception -> 0x00d2 }
            r4 = r14
            r0.append(r14)     // Catch:{ Exception -> 0x00d2 }
            r5 = r15
            r0.append(r15)     // Catch:{ Exception -> 0x00d2 }
            int r2 = com.miui.maml.util.HideSdkDependencyUtils.UserHandle_getIdentifier(r18)     // Catch:{ Exception -> 0x00d2 }
            r0.append(r2)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = miui.content.res.IconCustomizer.getAnimatingIconRelativePath(r13, r14, r15)     // Catch:{ Exception -> 0x00d2 }
            if (r2 == 0) goto L_0x0089
            if (r19 != 0) goto L_0x0089
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager>> r3 = mAnimatingIconsResourceManagers     // Catch:{ Exception -> 0x00d2 }
            java.lang.Object r3 = r3.get(r0)     // Catch:{ Exception -> 0x00d2 }
            java.lang.ref.WeakReference r3 = (java.lang.ref.WeakReference) r3     // Catch:{ Exception -> 0x00d2 }
            if (r3 != 0) goto L_0x0044
            r3 = r1
            goto L_0x004a
        L_0x0044:
            java.lang.Object r3 = r3.get()     // Catch:{ Exception -> 0x00d2 }
            com.miui.maml.ResourceManager r3 = (com.miui.maml.ResourceManager) r3     // Catch:{ Exception -> 0x00d2 }
        L_0x004a:
            if (r3 != 0) goto L_0x0078
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2 }
            r3.<init>()     // Catch:{ Exception -> 0x00d2 }
            r3.append(r2)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = "quiet/"
            r3.append(r2)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x00d2 }
            com.miui.maml.LifecycleResourceManager r3 = new com.miui.maml.LifecycleResourceManager     // Catch:{ Exception -> 0x00d2 }
            com.miui.maml.util.FancyIconResourceLoader r7 = new com.miui.maml.util.FancyIconResourceLoader     // Catch:{ Exception -> 0x00d2 }
            r7.<init>(r2)     // Catch:{ Exception -> 0x00d2 }
            r8 = 3600000(0x36ee80, double:1.7786363E-317)
            r10 = 360000(0x57e40, double:1.778636E-318)
            r6 = r3
            r6.<init>(r7, r8, r10)     // Catch:{ Exception -> 0x00d2 }
            java.util.HashMap<java.lang.String, java.lang.ref.WeakReference<com.miui.maml.ResourceManager>> r2 = mAnimatingIconsResourceManagers     // Catch:{ Exception -> 0x00d2 }
            java.lang.ref.WeakReference r6 = new java.lang.ref.WeakReference     // Catch:{ Exception -> 0x00d2 }
            r6.<init>(r3)     // Catch:{ Exception -> 0x00d2 }
            r2.put(r0, r6)     // Catch:{ Exception -> 0x00d2 }
        L_0x0078:
            r6 = r3
            if (r6 == 0) goto L_0x0087
            com.miui.maml.AnimatingDrawable r0 = new com.miui.maml.AnimatingDrawable     // Catch:{ Exception -> 0x00d2 }
            r2 = r0
            r3 = r12
            r4 = r14
            r5 = r15
            r7 = r18
            r2.<init>(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00c9
        L_0x0087:
            r0 = r1
            goto L_0x00c9
        L_0x0089:
            com.miui.maml.util.RendererCoreCache r3 = mRendererCoreCache     // Catch:{ Exception -> 0x00d2 }
            r6 = r16
            com.miui.maml.util.RendererCoreCache$RendererCoreInfo r3 = r3.get(r0, r6)     // Catch:{ Exception -> 0x00d2 }
            if (r3 != 0) goto L_0x00bc
            if (r2 == 0) goto L_0x00a7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00d2 }
            r3.<init>()     // Catch:{ Exception -> 0x00d2 }
            r3.append(r2)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = "fancy/"
            r3.append(r2)     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00ab
        L_0x00a7:
            java.lang.String r2 = miui.content.res.IconCustomizer.getFancyIconRelativePath(r13, r14, r15)     // Catch:{ Exception -> 0x00d2 }
        L_0x00ab:
            com.miui.maml.util.RendererCoreCache r3 = mRendererCoreCache     // Catch:{ Exception -> 0x00d2 }
            com.miui.maml.util.FancyIconResourceLoader r8 = new com.miui.maml.util.FancyIconResourceLoader     // Catch:{ Exception -> 0x00d2 }
            r8.<init>(r2)     // Catch:{ Exception -> 0x00d2 }
            com.miui.maml.util.RendererCoreCache$OnCreateRootCallback r9 = mOnCreateRootCallback     // Catch:{ Exception -> 0x00d2 }
            r4 = r0
            r5 = r12
            r6 = r16
            com.miui.maml.util.RendererCoreCache$RendererCoreInfo r3 = r3.get((java.lang.Object) r4, (android.content.Context) r5, (long) r6, (com.miui.maml.ResourceLoader) r8, (com.miui.maml.util.RendererCoreCache.OnCreateRootCallback) r9)     // Catch:{ Exception -> 0x00d2 }
        L_0x00bc:
            if (r3 == 0) goto L_0x0087
            com.miui.maml.RendererCore r0 = r3.r     // Catch:{ Exception -> 0x00d2 }
            if (r0 == 0) goto L_0x0087
            com.miui.maml.FancyDrawable r0 = new com.miui.maml.FancyDrawable     // Catch:{ Exception -> 0x00d2 }
            com.miui.maml.RendererCore r2 = r3.r     // Catch:{ Exception -> 0x00d2 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x00d2 }
        L_0x00c9:
            if (r0 == 0) goto L_0x00d1
            r2 = r12
            r3 = r18
            com.miui.maml.util.PortableUtils.getUserBadgedIcon(r12, r0, r3)     // Catch:{ Exception -> 0x00d2 }
        L_0x00d1:
            return r0
        L_0x00d2:
            r0 = move-exception
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "MAML AppIconsHelper"
            android.util.Log.e(r2, r0)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.AppIconsHelper.getIconDrawable(android.content.Context, android.content.pm.PackageItemInfo, java.lang.String, java.lang.String, long, android.os.UserHandle, boolean):android.graphics.drawable.Drawable");
    }

    public static Drawable getIconDrawable(Context context, ResolveInfo resolveInfo, PackageManager packageManager) {
        return getIconDrawable(context, resolveInfo, packageManager, 0);
    }

    public static Drawable getIconDrawable(Context context, ResolveInfo resolveInfo, PackageManager packageManager, long j) {
        PackageItemInfo packageItemInfo = resolveInfo.activityInfo;
        if (packageItemInfo == null) {
            packageItemInfo = resolveInfo.serviceInfo;
        }
        return getIconDrawable(context, packageItemInfo, packageManager, j);
    }

    public static Drawable getIconDrawable(Context context, String str, String str2, long j) {
        return getIconDrawable(context, str, str2, j, HideSdkDependencyUtils.UserHandle_getInstance_with_int(HideSdkDependencyUtils.Context_getUserId(context)));
    }

    public static Drawable getIconDrawable(Context context, String str, String str2, long j, UserHandle userHandle) {
        ActivityInfo activityInfo;
        try {
            activityInfo = context.getPackageManager().getActivityInfo(new ComponentName(str, str2), 0);
        } catch (Exception unused) {
            activityInfo = null;
        }
        return getIconDrawable(context, activityInfo, str, str2, j, userHandle);
    }
}
