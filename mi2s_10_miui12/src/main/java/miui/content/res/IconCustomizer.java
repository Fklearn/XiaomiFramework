package miui.content.res;

import android.app.MiuiThemeHelper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MiuiDisplayMetrics;
import android.util.TypedValue;
import com.android.server.wifi.hotspot2.anqp.Constants;
import com.miui.system.internal.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.graphics.BitmapFactory;
import miui.graphics.BitmapUtil;
import miui.imagefilters.IImageFilter;
import miui.imagefilters.ImageData;
import miui.imagefilters.ImageFilterBuilder;
import miui.io.FileStat;
import miui.os.Build;
import miui.theme.IconCustomizerUtils;
import miui.theme.ThemeFileUtils;
import miui.util.AppConstants;
import miui.util.PlayerActions;
import org.ksoap2.SoapEnvelope;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class IconCustomizer {
    private static final String ANIMATING_ICONS_INNER_PATH = "animating_icons/";
    public static final String CUSTOMIZED_ICON_PATH = FINAL_MOD_ICONS;
    private static final String FANCY_ICONS_INNER_PATH = "fancy_icons/";
    private static final String FINAL_MOD_ICONS = (ThemeResources.THEME_MAGIC_PATH + "customized_icons/");
    private static final String FINAL_MOD_ICONS_MIUI_VERSION = (FINAL_MOD_ICONS + "miui_version");
    private static final String ICON_NAME_SUFFIX = ".png";
    private static final String ICON_TRANSFORM_CONFIG = "transform_config.xml";
    private static final String LOG_TAG = "IconCustomizer";
    private static final String MIUI_MOD_BUILT_IN_ICONS = "/system/media/theme/miui_mod_icons/";
    private static final String MULTI_ANIM_ICONS_INNER_PATH = "layer_animating_icons/";
    private static Map<String, SoftReference<Drawable>> adaptiveIconCache = new HashMap();
    private static final int sAlphaShift = 24;
    private static final int sAlphaThreshold = 50;
    private static final Canvas sCanvas = new Canvas();
    private static final int sColorByteSize = 4;
    private static final int sColorShift = 8;
    private static int sCustomizedIconContentHeight = -1;
    private static int sCustomizedIconContentWidth = -1;
    private static int sCustomizedIconHeight = -1;
    private static int sCustomizedIconWidth = -1;
    private static int sCustomizedIrregularContentHeight = -1;
    private static int sCustomizedIrregularContentWidth = -1;
    private static volatile Paint sCutPaint = null;
    private static volatile Holder sHolder = null;
    private static Map<String, WeakReference<Bitmap>> sIconCache = new HashMap();
    private static volatile IconConfig sIconConfig = null;
    private static Map<String, String> sIconMapping = new HashMap();
    private static Matrix sIconTransformMatrix = null;
    private static boolean sIconTransformNeeded = false;
    private static final float sMaxContentRatio = 2.0f;
    private static Set<String> sModIconPkgWhiteList = new HashSet();
    private static Paint sPaintForTransformBitmap = new Paint(3);
    private static final int sRGBMask = 16777215;
    private static Map<String, SoftReference<Bitmap>> sRawIconCache = new HashMap();
    private static volatile ThemeRuntimeManager sThemeRuntimeManager;

    public interface CustomizedIconsListener {
        void beforePrepareIcon(int i);

        void finishAllIcons();

        void finishPrepareIcon(int i);
    }

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        sModIconPkgWhiteList.add("com.android.calendar");
        sModIconPkgWhiteList.add("com.android.settings");
        sModIconPkgWhiteList.add("com.xiaomi.market");
        sModIconPkgWhiteList.add("com.duokan.reader");
        sModIconPkgWhiteList.add(MiuiConfiguration.CONTACTS_PKG_NAME);
        sModIconPkgWhiteList.add("com.miui.notes");
        sModIconPkgWhiteList.add("com.miui.securitycenter");
        sModIconPkgWhiteList.add("com.miui.gallery");
        sModIconPkgWhiteList.add("com.xiaomi.scanner");
        sModIconPkgWhiteList.add("com.duokan.phone.remotecontroller");
        sModIconPkgWhiteList.add("com.android.phone");
        sModIconPkgWhiteList.add("com.android.camera");
        sModIconPkgWhiteList.add("com.miui.calculator");
        sModIconPkgWhiteList.add("com.miui.virtualsim");
        sModIconPkgWhiteList.add("com.android.soundrecorder");
        sModIconPkgWhiteList.add("com.android.browser");
        sModIconPkgWhiteList.add("com.android.thememanager");
        sModIconPkgWhiteList.add("com.miui.screenrecorder");
        sModIconPkgWhiteList.add("com.android.updater");
        sModIconPkgWhiteList.add("com.android.deskclock");
        sModIconPkgWhiteList.add("com.mi.health");
        sModIconPkgWhiteList.add("com.xiaomi.gamecenter");
        sModIconPkgWhiteList.add("com.miui.compass");
        sModIconPkgWhiteList.add("com.android.providers.downloads.ui");
        sModIconPkgWhiteList.add("com.miui.weather2");
        sModIconPkgWhiteList.add(PlayerActions.BROADCAST_PREFIX);
        sModIconPkgWhiteList.add("com.xiaomi.shop");
        sModIconPkgWhiteList.add("com.miui.huanji");
        sModIconPkgWhiteList.add("com.miui.miservice");
        sModIconPkgWhiteList.add("com.android.fileexplorer");
        sModIconPkgWhiteList.add("com.xiaomi.smarthome");
        sModIconPkgWhiteList.add(MiuiConfiguration.MMS_PKG_NAME);
        sModIconPkgWhiteList.add("com.miui.voiceassist");
        sModIconPkgWhiteList.add("com.miui.video");
        sModIconPkgWhiteList.add("com.android.email");
        sIconMapping.put("com.android.contacts.activities.TwelveKeyDialer.png", "com.android.contacts.TwelveKeyDialer.png");
        sIconMapping.put("com.miui.weather2.png", "com.miui.weather.png");
        sIconMapping.put("com.miui.gallery.png", "com.android.gallery.png");
        sIconMapping.put("com.android.gallery3d.png", "com.cooliris.media.png");
        sIconMapping.put("com.xiaomi.market.png", "com.miui.supermarket.png");
        sIconMapping.put("com.wali.miui.networkassistant.png", "com.android.monitor.png");
        sIconMapping.put("com.xiaomi.scanner.png", "com.miui.barcodescanner.png");
        sIconMapping.put("com.miui.calculator.png", "com.android.calculator2.png");
        sIconMapping.put("com.android.camera.CameraEntry.png", "com.android.camera.png");
        sIconMapping.put("com.htc.album.png", "com.miui.gallery.png");
        sIconMapping.put("com.htc.fm.activity.FMRadioMain.png", "com.miui.fmradio.png");
        sIconMapping.put("com.htc.fm.FMRadio.png", "com.miui.fmradio.png");
        sIconMapping.put("com.htc.fm.png", "com.miui.fmradio.png");
        sIconMapping.put("com.sec.android.app.camera.Camera.png", "com.android.camera.png");
        sIconMapping.put("com.sec.android.app.fm.png", "com.miui.fmradio.png");
        sIconMapping.put("com.android.gallery3d#com.android.camera.CameraLauncher.png", "com.android.camera.png");
        sIconMapping.put("com.android.hwcamera.png", "com.android.camera.png");
        sIconMapping.put("com.huawei.android.FMRadio.png", "com.miui.fmradio.png");
        sIconMapping.put("com.sonyericsson.android.camera.png", "com.android.camera.png");
        sIconMapping.put("com.sonyericsson.fmradio.png", "com.miui.fmradio.png");
        sIconMapping.put("com.motorola.Camera.Camera.png", "com.android.camera.png");
        sIconMapping.put("com.lge.camera.png", "com.android.camera.png");
        sIconMapping.put("com.oppo.camera.OppoCamera.png", "com.android.camera.png");
        sIconMapping.put("com.lenovo.scg#com.android.camera.CameraLauncher.png", "com.android.camera.png");
        sIconMapping.put("com.lenovo.fm.png", "com.miui.fmradio.png");
        sIconMapping.put("com.android.camera2#com.android.camera.CameraLauncher.png", "com.android.camera.png");
    }

    private static class IconConfig {
        float mCameraX;
        float mCameraY;
        IImageFilter.ImageFilterGroup mIconFilters;
        int mOverridedIrregularContentHeight;
        int mOverridedIrregularContentWidth;
        float[] mPointsMappingFrom;
        float[] mPointsMappingTo;
        float mRotateX;
        float mRotateY;
        float mRotateZ;
        float mScaleX;
        float mScaleY;
        float mSkewX;
        float mSkewY;
        boolean mSupportLayerIcon;
        float mTransX;
        float mTransY;
        boolean mUseModIcon;

        private IconConfig() {
            this.mScaleX = 1.0f;
            this.mScaleY = 1.0f;
            this.mUseModIcon = true;
            this.mSupportLayerIcon = false;
        }
    }

    public static BitmapDrawable getRawIconDrawable(String filename) {
        BitmapDrawable drawable = getDrawableFromMemoryCache(filename);
        if (drawable == null && (drawable = getDrawble(getRawIcon(filename))) != null) {
            synchronized (sIconCache) {
                sIconCache.put(filename, new WeakReference(drawable.getBitmap()));
            }
        }
        return drawable;
    }

    public static Bitmap getRawIcon(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return null;
        }
        Bitmap icon = getIconFromMemoryCache(filename);
        if (icon == null) {
            icon = getIconFromTheme(filename);
            if (icon == null) {
                icon = getMiuiModIcon(filename);
            }
            if (icon != null) {
                synchronized (sRawIconCache) {
                    sRawIconCache.put(filename, new SoftReference(icon));
                }
            }
        }
        return icon;
    }

    private static Bitmap getIconFromTheme(String filename) {
        if (ThemeResources.getSystem() == null) {
            return null;
        }
        return scaleBitmap(ThemeResources.getSystem().getIconBitmap(filename));
    }

    private static int getCustomizedIrregularContentWidth() {
        int ret = sCustomizedIrregularContentWidth;
        if (ret == -1) {
            if (sIconConfig.mOverridedIrregularContentWidth > 0) {
                ret = sIconConfig.mOverridedIrregularContentWidth;
            } else {
                ret = (AppConstants.getCurrentApplication() == null ? Resources.getSystem() : AppConstants.getCurrentApplication().getResources()).getDimensionPixelSize(R.dimen.customizer_irregular_content_size);
            }
            sCustomizedIrregularContentWidth = ret;
        }
        return ret;
    }

    private static int getCustomizedIrregularContentHeight() {
        int ret = sCustomizedIrregularContentHeight;
        if (ret == -1) {
            if (sIconConfig.mOverridedIrregularContentHeight > 0) {
                ret = sIconConfig.mOverridedIrregularContentHeight;
            } else {
                ret = (AppConstants.getCurrentApplication() == null ? Resources.getSystem() : AppConstants.getCurrentApplication().getResources()).getDimensionPixelSize(R.dimen.customizer_irregular_content_size);
            }
            sCustomizedIrregularContentHeight = ret;
        }
        return ret;
    }

    private static int getCustomizedIconContentWidth() {
        int ret = sCustomizedIconContentWidth;
        if (ret != -1) {
            return ret;
        }
        int ret2 = (AppConstants.getCurrentApplication() == null ? Resources.getSystem() : AppConstants.getCurrentApplication().getResources()).getDimensionPixelSize(R.dimen.customizer_icon_content_size);
        sCustomizedIconContentWidth = ret2;
        return ret2;
    }

    private static int getCustomizedIconContentHeight() {
        int ret = sCustomizedIconContentHeight;
        if (ret != -1) {
            return ret;
        }
        int ret2 = (AppConstants.getCurrentApplication() == null ? Resources.getSystem() : AppConstants.getCurrentApplication().getResources()).getDimensionPixelSize(R.dimen.customizer_icon_content_size);
        sCustomizedIconContentHeight = ret2;
        return ret2;
    }

    public static int getCustomizedIconWidth() {
        int ret = sCustomizedIconWidth;
        if (ret != -1) {
            return ret;
        }
        int ret2 = (AppConstants.getCurrentApplication() == null ? Resources.getSystem() : AppConstants.getCurrentApplication().getResources()).getDimensionPixelSize(R.dimen.customizer_icon_size);
        sCustomizedIconWidth = ret2;
        return ret2;
    }

    public static int getCustomizedIconHeight() {
        int ret = sCustomizedIconHeight;
        if (ret != -1) {
            return ret;
        }
        int ret2 = (AppConstants.getCurrentApplication() == null ? Resources.getSystem() : AppConstants.getCurrentApplication().getResources()).getDimensionPixelSize(R.dimen.customizer_icon_size);
        sCustomizedIconHeight = ret2;
        return ret2;
    }

    private static Bitmap scaleBitmap(Bitmap icon) {
        if (icon == null) {
            return null;
        }
        int density = Resources.getSystem().getConfiguration().densityDpi;
        icon.setDensity(density);
        if (icon.getWidth() == getCustomizedIconWidth() && icon.getHeight() == getCustomizedIconHeight()) {
            return icon;
        }
        float scaleReverse = Math.min(((float) icon.getWidth()) / ((float) getCustomizedIconWidth()), ((float) icon.getHeight()) / ((float) getCustomizedIconWidth())) + 0.1f;
        if (scaleReverse >= sMaxContentRatio && icon.getConfig() == Bitmap.Config.ARGB_8888) {
            icon = BitmapFactory.fastBlur(icon, (int) (scaleReverse - 1.0f));
        }
        Bitmap bitmap = Bitmap.createScaledBitmap(icon, getCustomizedIconWidth(), getCustomizedIconHeight(), true);
        bitmap.setDensity(density);
        return bitmap;
    }

    private static String getFileName(String packageName, String className) {
        if (className == null) {
            return packageName + ICON_NAME_SUFFIX;
        } else if (className.startsWith(packageName)) {
            return className + ICON_NAME_SUFFIX;
        } else {
            return packageName + '#' + className + ICON_NAME_SUFFIX;
        }
    }

    public static void clearCache() {
        clearCache((String) null);
    }

    public static void clearCache(String packageName) {
        if (packageName == null) {
            synchronized (sRawIconCache) {
                sRawIconCache.clear();
            }
            synchronized (sIconCache) {
                sIconCache.clear();
            }
            synchronized (adaptiveIconCache) {
                adaptiveIconCache.clear();
            }
            sIconConfig = null;
            sHolder = null;
            sIconTransformNeeded = false;
            sCustomizedIrregularContentWidth = -1;
            sCustomizedIrregularContentHeight = -1;
            sCustomizedIconContentWidth = -1;
            sCustomizedIconContentHeight = -1;
            sCustomizedIconWidth = -1;
            sCustomizedIconHeight = -1;
            return;
        }
        synchronized (sIconCache) {
            Iterator<Map.Entry<String, WeakReference<Bitmap>>> i = sIconCache.entrySet().iterator();
            while (i.hasNext()) {
                if (((String) i.next().getKey()).startsWith(packageName)) {
                    i.remove();
                }
            }
        }
        synchronized (adaptiveIconCache) {
            Iterator<Map.Entry<String, SoftReference<Drawable>>> i2 = adaptiveIconCache.entrySet().iterator();
            while (i2.hasNext()) {
                if (((String) i2.next().getKey()).startsWith(packageName)) {
                    i2.remove();
                }
            }
        }
    }

    public static void checkModIconsTimestamp() {
        File file = new File(FINAL_MOD_ICONS);
        if (file.exists()) {
            String clearReason = null;
            try {
                if (new File(getMiuiModDownloadIconDir()).lastModified() <= FileStat.getCreatedTime(FINAL_MOD_ICONS)) {
                    File lastVersionFile = new File(FINAL_MOD_ICONS_MIUI_VERSION);
                    if (lastVersionFile.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(lastVersionFile));
                        if (!getCustomizedIconVersionContent().equals(reader.readLine())) {
                            clearReason = "miui version update";
                        }
                        reader.close();
                    } else {
                        clearReason = "miui version flag miss";
                    }
                } else {
                    clearReason = "mod download icon update";
                }
            } catch (Exception e) {
                clearReason = "miui version read exception";
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "check time stamp: " + clearReason);
            if (clearReason != null) {
                ThemeNativeUtils.deleteContents(file.getPath());
                clearCache();
            }
        }
        if (!file.exists()) {
            ThemeFileUtils.mkdirs(file.getPath());
        }
    }

    public static void ensureMiuiVersionFlagExist(Context context) {
        if (!new File(FINAL_MOD_ICONS_MIUI_VERSION).exists()) {
            String tmpPath = FINAL_MOD_ICONS_MIUI_VERSION;
            if (context != null && !ThemeResources.FRAMEWORK_PACKAGE.equals(context.getPackageName())) {
                tmpPath = context.getFileStreamPath("customized_icons_version").getAbsolutePath();
            }
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(tmpPath));
                out.write(getCustomizedIconVersionContent());
                out.close();
            } catch (Exception e) {
            }
            if (!FINAL_MOD_ICONS_MIUI_VERSION.equals(tmpPath)) {
                ThemeNativeUtils.copy(tmpPath, FINAL_MOD_ICONS_MIUI_VERSION);
                ThemeNativeUtils.remove(tmpPath);
                ThemeNativeUtils.updateFilePermissionWithThemeContext(FINAL_MOD_ICONS_MIUI_VERSION);
            }
        }
    }

    public static void clearCustomizedIcons(String packageName) {
        if (Build.IS_MIUI) {
            if (TextUtils.isEmpty(packageName)) {
                ThemeNativeUtils.deleteContents(FINAL_MOD_ICONS);
                clearCache();
                return;
            }
            String[] fileNames = new File(FINAL_MOD_ICONS).list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    if (fileName.startsWith(packageName)) {
                        ThemeNativeUtils.remove(FINAL_MOD_ICONS + fileName);
                    }
                }
            }
            clearCache(packageName);
        }
    }

    public static void prepareCustomizedIcons(Context context, CustomizedIconsListener l) {
        CustomizedIconsListener customizedIconsListener = l;
        Bitmap mask = getRawIcon("icon_mask.png");
        Bitmap background = getRawIcon("icon_background.png");
        Bitmap rawIcon = getRawIcon("icon_pattern.png");
        Bitmap rawIcon2 = getRawIcon("icon_border.png");
        Intent launcherIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        launcherIntent.addCategory("android.intent.category.LAUNCHER");
        final PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(launcherIntent, 0);
        if (customizedIconsListener != null) {
            customizedIconsListener.beforePrepareIcon(list.size() + 1);
        }
        int taskCnt = 1;
        ExecutorService execService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long time = System.currentTimeMillis();
        Log.d(LOG_TAG, "prepareCustomizedIcons start");
        List<Future<?>> futures = new ArrayList<>();
        for (final ResolveInfo info : list) {
            futures.add(execService.submit(new Runnable() {
                public void run() {
                    info.activityInfo.loadIcon(pm);
                }
            }));
            taskCnt = taskCnt;
        }
        int taskCnt2 = taskCnt;
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e2) {
                e2.printStackTrace();
            }
            if (customizedIconsListener != null) {
                customizedIconsListener.finishPrepareIcon(taskCnt2);
                taskCnt2++;
            }
        }
        execService.shutdown();
        StringBuilder sb = new StringBuilder();
        sb.append("prepareCustomizedIcons end ");
        Bitmap bitmap = mask;
        Bitmap bitmap2 = background;
        sb.append(System.currentTimeMillis() - time);
        Log.d(LOG_TAG, sb.toString());
        if (customizedIconsListener != null) {
            l.finishAllIcons();
        }
    }

    public static void saveCustomizedIconBitmap(String packageName, String className, Drawable dr, Context context) {
        if (dr instanceof BitmapDrawable) {
            saveCustomizedIconBitmap(getFileName(packageName, className), ((BitmapDrawable) dr).getBitmap(), context);
        }
    }

    private static String getCustomizedIconVersionContent() {
        return Build.VERSION.INCREMENTAL + "_" + MiuiDisplayMetrics.DENSITY_DEVICE;
    }

    private static void saveCustomizedIconBitmap(String fileName, Bitmap icon, Context context) {
        getServiceManager(context).saveIcon(fileName, icon);
    }

    private static ThemeRuntimeManager getServiceManager(Context context) {
        if (sThemeRuntimeManager == null) {
            synchronized (IconCustomizer.class) {
                if (sThemeRuntimeManager == null) {
                    Context saveContext = context.getApplicationContext();
                    if (saveContext == null) {
                        saveContext = context;
                    }
                    sThemeRuntimeManager = new ThemeRuntimeManager(saveContext);
                }
            }
        }
        return sThemeRuntimeManager;
    }

    private static class Holder {
        boolean sModIconEnabled;

        private Holder() {
            this.sModIconEnabled = MiuiThemeHelper.isModIconEnabled();
        }
    }

    private static boolean isModIconEnabled() {
        if (sHolder == null) {
            synchronized (IconCustomizer.class) {
                if (sHolder == null) {
                    sHolder = new Holder();
                }
            }
        }
        return sHolder.sModIconEnabled;
    }

    public static boolean isModIconEnabledForPackageName(String packageName) {
        return sModIconPkgWhiteList.contains(packageName) || isModIconEnabled();
    }

    public static String getAnimatingIconRelativePath(PackageItemInfo info, String packageName, String className) {
        return getIconRelativePath(info, packageName, className, ANIMATING_ICONS_INNER_PATH, "fancy/manifest.xml");
    }

    public static String getFancyIconRelativePath(PackageItemInfo info, String packageName, String className) {
        return getIconRelativePath(info, packageName, className, FANCY_ICONS_INNER_PATH, "manifest.xml");
    }

    public static String getMamlAdaptiveIconRelativePath(PackageItemInfo info, String packageName, String className) {
        return getIconRelativePath(info, packageName, className, MULTI_ANIM_ICONS_INNER_PATH, "0/fancy/manifest.xml");
    }

    private static String getIconRelativePath(PackageItemInfo info, String packageName, String className, String innerPath, String checkFilePath) {
        List<String> names = getIconNames(packageName, className);
        if (!(className == null || info == null || info.icon != 0)) {
            names.add(packageName);
        }
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (name.endsWith(ICON_NAME_SUFFIX)) {
                name = name.substring(0, name.length() - ICON_NAME_SUFFIX.length());
            }
            String relativePath = innerPath + name + "/";
            if (ThemeResources.getSystem() != null) {
                if (ThemeResources.getSystem().hasIcon(relativePath + checkFilePath)) {
                    return relativePath;
                }
            }
        }
        return null;
    }

    private static BitmapDrawable getDrawableFromStaticCache(String filename) {
        Bitmap icon = null;
        String pathName = FINAL_MOD_ICONS + filename;
        File iconFile = new File(pathName);
        if (iconFile.exists() && (icon = BitmapFactory.decodeFile(pathName)) == null) {
            iconFile.delete();
        }
        return getDrawble(icon);
    }

    private static BitmapDrawable getDrawableFromMemoryCache(String name) {
        WeakReference<Bitmap> ref;
        synchronized (sIconCache) {
            ref = sIconCache.get(name);
        }
        if (ref != null) {
            return getDrawble((Bitmap) ref.get());
        }
        return null;
    }

    private static Bitmap getIconFromMemoryCache(String name) {
        SoftReference<Bitmap> ref;
        synchronized (sRawIconCache) {
            ref = sRawIconCache.get(name);
        }
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public static BitmapDrawable generateShortcutIconDrawable(Drawable base) {
        Bitmap icon = drawableToBitmap(base, getScaleRatio(base, false));
        if (miui.os.Build.IS_MIUI) {
            icon = composeIconWithTransform(icon, "icon_mask.png", (String) null, "icon_shortcut.png", "icon_shortcut_arrow.png");
        }
        return getDrawble(icon);
    }

    public static BitmapDrawable generateIconStyleDrawable(Drawable base) {
        return generateIconStyleDrawable(base, false);
    }

    public static BitmapDrawable generateIconStyleDrawable(Drawable base, boolean cropOutside) {
        Bitmap icon = drawableToBitmap(base, getScaleRatio(base, cropOutside));
        if (miui.os.Build.IS_MIUI) {
            icon = composeIconWithTransform(icon, "icon_mask.png", "icon_background.png", "icon_pattern.png", "icon_border.png");
        }
        return getDrawble(icon);
    }

    public static BitmapDrawable generateIconStyleDrawable(Drawable base, Bitmap mask, Bitmap background, Bitmap pattern, Bitmap cover, boolean cropOutside) {
        return getDrawble(composeIcon(drawableToBitmap(base, getScaleRatio(base, cropOutside)), mask, background, pattern, cover));
    }

    public static BitmapDrawable getCustomizedIconFromCache(String packageName, String className) {
        return getDrawableFromMemoryCache(getFileName(packageName, className));
    }

    public static BitmapDrawable getCustomizedIcon(Context context, String packageName, String className, Drawable original) {
        ensureIconConfigLoaded();
        return getCustomizedIconInner(context, getIconNames(packageName, className), original, sIconConfig.mUseModIcon);
    }

    public static BitmapDrawable getCustomizedIcon(Context context, String filename) {
        List<String> names = new ArrayList<>();
        names.add(filename);
        return getCustomizedIconInner(context, names, (Drawable) null, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0045  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.drawable.BitmapDrawable getCustomizedIcon(android.content.Context r16, java.lang.String r17, java.lang.String r18, int r19, android.content.pm.ApplicationInfo r20, boolean r21) {
        /*
            r7 = r16
            r8 = r19
            r9 = r20
            ensureIconConfigLoaded()
            java.util.List r10 = getIconNames(r17, r18)
            android.content.pm.PackageManager r11 = r16.getPackageManager()
            r0 = 0
            java.lang.Object r0 = r10.get(r0)
            r12 = r0
            java.lang.String r12 = (java.lang.String) r12
            miui.content.res.IconCustomizer$IconConfig r0 = sIconConfig
            boolean r0 = r0.mUseModIcon
            r1 = 0
            android.graphics.drawable.BitmapDrawable r0 = getCustomizedIconInner(r7, r10, r1, r0)
            if (r0 == 0) goto L_0x0025
            return r0
        L_0x0025:
            r2 = 0
            if (r8 == 0) goto L_0x003c
            if (r9 == 0) goto L_0x003c
            int r3 = r9.icon
            if (r8 != r3) goto L_0x0034
            if (r21 == 0) goto L_0x0031
            goto L_0x0034
        L_0x0031:
            r13 = r17
            goto L_0x003e
        L_0x0034:
            r13 = r17
            android.graphics.drawable.Drawable r2 = r11.getDrawable(r13, r8, r9)
            r14 = r2
            goto L_0x003f
        L_0x003c:
            r13 = r17
        L_0x003e:
            r14 = r2
        L_0x003f:
            android.graphics.drawable.BitmapDrawable r15 = transToMiuiModIcon(r7, r1, r14, r12)
            if (r15 == 0) goto L_0x005b
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<android.graphics.Bitmap>> r1 = sIconCache
            monitor-enter(r1)
            java.util.Map<java.lang.String, java.lang.ref.WeakReference<android.graphics.Bitmap>> r0 = sIconCache     // Catch:{ all -> 0x0058 }
            java.lang.ref.WeakReference r2 = new java.lang.ref.WeakReference     // Catch:{ all -> 0x0058 }
            android.graphics.Bitmap r3 = r15.getBitmap()     // Catch:{ all -> 0x0058 }
            r2.<init>(r3)     // Catch:{ all -> 0x0058 }
            r0.put(r12, r2)     // Catch:{ all -> 0x0058 }
            monitor-exit(r1)     // Catch:{ all -> 0x0058 }
            goto L_0x005b
        L_0x0058:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0058 }
            throw r0
        L_0x005b:
            if (r15 != 0) goto L_0x0071
            if (r8 == 0) goto L_0x0071
            if (r9 == 0) goto L_0x0071
            if (r18 == 0) goto L_0x0071
            r3 = 0
            r6 = 1
            r1 = r16
            r2 = r17
            r4 = r19
            r5 = r20
            android.graphics.drawable.BitmapDrawable r15 = getCustomizedIcon(r1, r2, r3, r4, r5, r6)
        L_0x0071:
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.content.res.IconCustomizer.getCustomizedIcon(android.content.Context, java.lang.String, java.lang.String, int, android.content.pm.ApplicationInfo, boolean):android.graphics.drawable.BitmapDrawable");
    }

    private static Drawable getAdaptiveIconFromCache(String fileName) {
        SoftReference<Drawable> ref;
        Drawable drawable;
        Drawable.ConstantState constantState;
        synchronized (adaptiveIconCache) {
            ref = adaptiveIconCache.get(fileName);
        }
        if (ref == null || (drawable = ref.get()) == null || (constantState = drawable.getConstantState()) == null) {
            return null;
        }
        return constantState.newDrawable(Resources.getSystem());
    }

    public static Drawable getIcon(Context context, String packageName, String className, int resid, ApplicationInfo appInfo, boolean isPackageIcon) {
        ensureIconConfigLoaded();
        Drawable icon = null;
        if (Build.VERSION.SDK_INT > 28 && sIconConfig.mSupportLayerIcon) {
            icon = getAdaptiveIcon(context, packageName, className, resid, appInfo, isPackageIcon);
        }
        if (icon == null) {
            return getCustomizedIcon(context, packageName, className, resid, appInfo, isPackageIcon);
        }
        return icon;
    }

    private static Drawable getAdaptiveIcon(Context context, String packageName, String className, int resid, ApplicationInfo appInfo, boolean isPackageIcon) {
        Drawable adaptiveIcon;
        int i = resid;
        ApplicationInfo applicationInfo = appInfo;
        List<String> names = getIconNames(packageName, className);
        String fileName = names.get(0);
        Drawable adaptiveIcon2 = getAdaptiveIconFromCache(fileName);
        if (adaptiveIcon2 == null) {
            adaptiveIcon2 = IconCustomizerUtils.transformToAdaptiveIcon(getIconBitmapsFromTheme(names));
        }
        if (adaptiveIcon2 != null || i == 0 || applicationInfo == null) {
            Context context2 = context;
            String str = packageName;
        } else if (i != applicationInfo.icon || isPackageIcon) {
            Context context3 = context;
            String str2 = packageName;
            adaptiveIcon2 = IconCustomizerUtils.getAdaptiveIconFromPackage(context, packageName, i, applicationInfo);
        } else {
            Context context4 = context;
            String str3 = packageName;
        }
        if (adaptiveIcon2 != null || !sIconConfig.mUseModIcon || !isModIconEnabledForPackageName(packageName)) {
            adaptiveIcon = adaptiveIcon2;
        } else {
            adaptiveIcon = IconCustomizerUtils.transformToAdaptiveIcon(getMiuiModIconBitamps(names));
        }
        if (adaptiveIcon != null) {
            synchronized (adaptiveIconCache) {
                adaptiveIconCache.put(fileName, new SoftReference(adaptiveIcon));
            }
        }
        if (adaptiveIcon != null || i == 0 || applicationInfo == null || className == null) {
            return adaptiveIcon;
        }
        return getAdaptiveIcon(context, packageName, (String) null, resid, appInfo, true);
    }

    private static Bitmap[] getIconBitmapsFromTheme(List<String> names) {
        Bitmap[] iconBitmaps = null;
        if (ThemeResources.getSystem() != null) {
            int n = names.size();
            for (int i = 0; i < n; i++) {
                iconBitmaps = ThemeResources.getSystem().getIconBitmaps(names.get(i));
                if (iconBitmaps != null) {
                    return iconBitmaps;
                }
            }
            for (int i2 = 0; i2 < n; i2++) {
                Bitmap icon = ThemeResources.getSystem().getIconBitmap(names.get(i2));
                if (icon != null) {
                    return new Bitmap[]{icon};
                }
            }
        }
        return iconBitmaps;
    }

    private static Bitmap[] getMiuiModIconBitamps(List<String> names) {
        Bitmap[] iconBitmaps = null;
        int n = names.size();
        for (int i = 0; i < n; i++) {
            iconBitmaps = getMiuiModBitmaps(names.get(i));
            if (iconBitmaps != null) {
                return iconBitmaps;
            }
        }
        for (int i2 = 0; i2 < n; i2++) {
            Bitmap icon = getMiuiModIcon(names.get(i2));
            if (icon != null) {
                return new Bitmap[]{icon};
            }
        }
        return iconBitmaps;
    }

    private static Bitmap[] getMiuiModBitmaps(String name) {
        ArrayList<Bitmap> iconBitmaps = new ArrayList<>();
        int suffixIndex = name.lastIndexOf(46);
        if (suffixIndex > 0) {
            String iconFileDirName = name.substring(0, suffixIndex);
            File iconFileDir = new File(MIUI_MOD_BUILT_IN_ICONS + iconFileDirName);
            if (iconFileDir.exists() && iconFileDir.isDirectory()) {
                String iconFileDirPathString = iconFileDir.getAbsolutePath();
                for (int i = 0; i < 5; i++) {
                    Bitmap icon = BitmapFactory.decodeFile(String.format("%s/%d.png", new Object[]{iconFileDirPathString, Integer.valueOf(i)}));
                    if (icon == null) {
                        break;
                    }
                    iconBitmaps.add(icon);
                }
            }
        }
        if (iconBitmaps.size() > 0) {
            return (Bitmap[]) iconBitmaps.toArray(new Bitmap[iconBitmaps.size()]);
        }
        return null;
    }

    public static void refreshIconShapeMask() {
        ensureIconConfigLoaded();
        try {
            IconCustomizerUtils.setIconShapeOverlayEnable(!sIconConfig.mSupportLayerIcon);
        } catch (Exception e) {
            Log.e(LOG_TAG, String.format("setIconShapeOverlayEnable err : %s", new Object[]{e.getMessage()}));
        }
    }

    private static BitmapDrawable transToMiuiModIcon(Context context, Bitmap icon, Drawable original, String filename) {
        boolean isAdaptiveIconDrawale = isAdaptiveIconDrawale(original);
        if (icon == null && original != null) {
            icon = drawableToBitmap(original, getScaleRatio(original, false), isAdaptiveIconDrawale);
        }
        if (icon != null) {
            Log.d(LOG_TAG, String.format("Generate customized icon for %s", new Object[]{filename}));
            icon = composeIconWithTransform(icon, "icon_mask.png", "icon_background.png", "icon_pattern.png", "icon_border.png");
            if (!isAdaptiveIconDrawale) {
                saveCustomizedIconBitmap(filename, icon, context);
            }
        }
        return getDrawble(icon);
    }

    private static BitmapDrawable getCustomizedIconInner(Context context, List<String> names, Drawable original, boolean tryModIcon) {
        String filename = names.get(0);
        BitmapDrawable drawable = getDrawableFromMemoryCache(filename);
        if (drawable != null) {
            return drawable;
        }
        BitmapDrawable drawable2 = getDrawableFromStaticCache(filename);
        int i = 0;
        while (drawable2 == null && i < names.size()) {
            drawable2 = getDrawble(getIconFromTheme(names.get(i)));
            i++;
        }
        if (drawable2 == null) {
            Bitmap icon = null;
            if (tryModIcon && context != null && isModIconEnabledForPackageName(context.getPackageName())) {
                int i2 = 0;
                while (icon == null && i2 < names.size()) {
                    icon = getMiuiModIcon(names.get(i2));
                    i2++;
                }
            }
            drawable2 = transToMiuiModIcon(context, icon, original, filename);
        }
        if (drawable2 != null) {
            synchronized (sIconCache) {
                sIconCache.put(filename, new WeakReference(drawable2.getBitmap()));
            }
        }
        return drawable2;
    }

    private static String getMiuiModDownloadIconDir() {
        return "/data/user/" + UserHandle.myUserId() + "/com.xiaomi.market/files/miui_mod_icons/";
    }

    private static Bitmap getMiuiModIcon(String fileName) {
        if (miui.os.Build.IS_CU_CUSTOMIZATION_TEST) {
            if ("com.android.stk.png".equals(fileName)) {
                fileName = "com.android.stk.cu.png";
            } else if ("com.android.stk.StkLauncherActivity2.png".equals(fileName)) {
                fileName = "com.android.stk.cu.2.png";
            }
        }
        File iconFile = new File(MIUI_MOD_BUILT_IN_ICONS + fileName);
        if (iconFile.exists()) {
            return scaleBitmap(BitmapFactory.decodeFile(iconFile.getAbsolutePath()));
        }
        return null;
    }

    private static List<String> getIconNames(String packageName, String className) {
        ArrayList<String> paths = new ArrayList<>();
        String fileName = getFileName(packageName, className);
        String mappingName = sIconMapping.get(fileName);
        if (mappingName != null) {
            paths.add(mappingName);
        }
        paths.add(fileName);
        if (className != null && !className.startsWith(packageName)) {
            paths.add(String.format("%s.png", new Object[]{className}));
        }
        return paths;
    }

    private static BitmapDrawable getDrawble(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    private static float getScaleRatio(Drawable icon, boolean cropOutside) {
        if (icon instanceof PaintDrawable) {
            return 1.0f;
        }
        int sourceWidth = icon.getIntrinsicWidth();
        int sourceHeight = icon.getIntrinsicHeight();
        if (sourceWidth <= 0 || sourceHeight <= 0) {
            return 1.0f;
        }
        float ratioW = ((float) getCustomizedIconContentWidth()) / ((float) sourceWidth);
        float ratioH = ((float) getCustomizedIconContentHeight()) / ((float) sourceHeight);
        if (cropOutside) {
            return Math.max(((float) getCustomizedIconWidth()) / ((float) sourceWidth), ((float) getCustomizedIconHeight()) / ((float) sourceHeight));
        }
        float contentRatio = getContentRatio(icon);
        Log.d(LOG_TAG, "Content Ratio = " + contentRatio);
        if (contentRatio > 0.0f) {
            return contentRatio;
        }
        return Math.min(1.0f, Math.min(ratioW, ratioH));
    }

    private static float getContentRatio(Drawable icon) {
        ensureIconConfigLoaded();
        if (!(icon instanceof BitmapDrawable)) {
            return -1.0f;
        }
        Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
        if (bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            return -1.0f;
        }
        boolean isRegularShape = true;
        int top = getEdgePosition(bitmap, true, false);
        int bottom = getEdgePosition(bitmap, true, true);
        int left = getEdgePosition(bitmap, false, false);
        int right = getEdgePosition(bitmap, false, true);
        int contentWidth = getCustomizedIconContentWidth();
        int contentHeight = getCustomizedIconContentHeight();
        float iconContentWidth = (float) Math.min(icon.getIntrinsicWidth(), (right - left) + 1);
        float iconContentHeight = (float) Math.min(icon.getIntrinsicHeight(), (bottom - top) + 1);
        if (iconContentWidth < iconContentHeight * 0.8f || iconContentHeight < 0.8f * iconContentWidth) {
            isRegularShape = false;
        } else if (!isRegularShape(bitmap, left, top, right, bottom)) {
            isRegularShape = false;
        }
        if (isRegularShape) {
            return Math.min(((float) contentWidth) / iconContentWidth, ((float) contentHeight) / iconContentHeight);
        }
        return Math.min(((float) getCustomizedIrregularContentWidth()) / iconContentWidth, ((float) getCustomizedIrregularContentHeight()) / iconContentHeight);
    }

    private static boolean isRegularShape(Bitmap b, int left, int top, int right, int bottom) {
        int rowBytes = b.getRowBytes();
        byte[] pixels = BitmapUtil.getBuffer(b);
        if (pixels == null) {
            return true;
        }
        for (int x = ((right - left) / 4) + left; x < (((right - left) * 3) / 4) + left; x++) {
            if ((pixels[(top * rowBytes) + (x << 2) + 3] & 255) < 50 || (pixels[(bottom * rowBytes) + (x << 2) + 3] & 255) < 50) {
                return false;
            }
        }
        for (int y = ((bottom - top) / 4) + top; y < (((bottom - top) * 3) / 4) + top; y++) {
            if ((pixels[(y * rowBytes) + (left << 2) + 3] & 255) < 50 || (pixels[(y * rowBytes) + (right << 2) + 3] & 255) < 50) {
                return false;
            }
        }
        return true;
    }

    private static int getEdgePosition(Bitmap bitmap, boolean isHorizontal, boolean isInvert) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int rowBytes = bitmap.getRowBytes();
        byte[] pixels = BitmapUtil.getBuffer(bitmap);
        int dy = -1;
        int x = !isInvert ? -1 : width;
        int y = !isInvert ? -1 : height;
        int dx = isInvert ? -1 : 1;
        if (!isInvert) {
            dy = 1;
        }
        if (pixels != null) {
            int length = 0;
            while (length == 0) {
                if (!isHorizontal) {
                    x += dx;
                    if (x < 0 || x >= width) {
                        break;
                    }
                    int y2 = 0;
                    while (y < height) {
                        if ((pixels[(y * rowBytes) + (x << 2) + 3] & 255) > 50) {
                            length++;
                        }
                        y2 = y + 1;
                    }
                } else {
                    y += dy;
                    if (y < 0 || y >= height) {
                        break;
                    }
                    int x2 = 0;
                    while (x < width) {
                        if ((pixels[(y * rowBytes) + (x << 2) + 3] & 255) > 50) {
                            length++;
                        }
                        x2 = x + 1;
                    }
                }
            }
        }
        return isHorizontal ? y : x;
    }

    private static Bitmap drawableToBitmap(Drawable icon, float ratio) {
        return drawableToBitmap(icon, ratio, false);
    }

    private static Bitmap drawableToBitmap(Drawable icon, float ratio, boolean isAdaptiveIconDrawable) {
        Bitmap bitmap;
        synchronized (sCanvas) {
            int targetWidth = getCustomizedIconWidth();
            int targetHeight = getCustomizedIconHeight();
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(targetWidth);
                painter.setIntrinsicHeight(targetHeight);
            } else if (icon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                int bitmapDensity = bitmapDrawable.getBitmap().getDensity();
                if (Build.VERSION.SDK_INT >= 28) {
                    if (bitmapDensity != displayMetrics.densityDpi) {
                        bitmapDrawable.setTargetDensity(bitmapDensity);
                        ratio = getScaleRatio(icon, false);
                        Log.d(LOG_TAG, "BitmapDensity = " + bitmapDensity + "  setTargetDensity = " + bitmapDensity);
                    }
                } else if (bitmapDensity == 0) {
                    bitmapDrawable.setTargetDensity(displayMetrics);
                }
            }
            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();
            if (isAdaptiveIconDrawable) {
                sourceWidth = targetWidth;
                sourceHeight = targetHeight;
                ratio = 1.0f;
            }
            icon.setBounds(0, 0, sourceWidth, sourceHeight);
            bitmap = Bitmap.createBitmap(displayMetrics, targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = sCanvas;
            canvas.setBitmap(bitmap);
            canvas.save();
            canvas.translate((((float) targetWidth) - (((float) sourceWidth) * ratio)) / sMaxContentRatio, (((float) targetHeight) - (((float) sourceHeight) * ratio)) / sMaxContentRatio);
            canvas.scale(ratio, ratio);
            icon.draw(canvas);
            canvas.restore();
            canvas.setBitmap((Bitmap) null);
        }
        return bitmap;
    }

    private static boolean isAdaptiveIconDrawale(Drawable dr) {
        if (Build.VERSION.SDK_INT < 26 || dr == null) {
            return false;
        }
        try {
            return Class.forName("android.graphics.drawable.AdaptiveIconDrawable").isInstance(dr);
        } catch (Exception e) {
            Log.e(LOG_TAG, "check adaptive icon fail");
            return false;
        }
    }

    private static Bitmap transformBitmap(Bitmap base, Matrix matrix) {
        Bitmap outBitmap = Bitmap.createBitmap(base.getWidth(), base.getHeight(), Bitmap.Config.ARGB_8888);
        outBitmap.setDensity(base.getDensity());
        new Canvas(outBitmap).drawBitmap(base, matrix, sPaintForTransformBitmap);
        return outBitmap;
    }

    private static Bitmap composeIconWithTransform(Bitmap base, String modMask, String modBackground, String modPattern, String modCover) {
        ensureIconConfigLoaded();
        if (sIconConfig.mIconFilters != null) {
            base = ImageData.imageDataToBitmap(sIconConfig.mIconFilters.processAll(base));
        }
        if (sIconTransformNeeded) {
            base = transformBitmap(base, sIconTransformMatrix);
        }
        return composeIcon(base, getRawIcon(modMask), getRawIcon(modBackground), getRawIcon(modPattern), getRawIcon(modCover));
    }

    private static void ensureIconConfigLoaded() {
        if (sIconConfig == null) {
            synchronized (IconCustomizer.class) {
                if (sIconConfig == null) {
                    IconConfig iconConfig = loadIconConfig();
                    sIconTransformMatrix = makeIconMatrix(iconConfig);
                    sIconConfig = iconConfig;
                }
            }
        }
    }

    private static Bitmap composeIcon(Bitmap base, Bitmap modMask, Bitmap modBackground, Bitmap modPattern, Bitmap modCover) {
        Canvas canvas;
        float[] bgColor;
        int strideSize;
        int[] basePixels;
        Bitmap bitmap = modMask;
        Bitmap bitmap2 = modBackground;
        Bitmap bitmap3 = modPattern;
        Bitmap bitmap4 = modCover;
        int baseWidth = base.getWidth();
        int baseHeight = base.getHeight();
        int pixelSize = base.getByteCount() / 4;
        int strideSize2 = base.getRowBytes() / 4;
        int[] basePixels2 = new int[pixelSize];
        int i = strideSize2;
        int[] basePixels3 = basePixels2;
        base.getPixels(basePixels2, 0, i, 0, 0, baseWidth, baseHeight);
        base.recycle();
        Bitmap result = Bitmap.createBitmap(getCustomizedIconWidth(), getCustomizedIconHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(result);
        Canvas canvas3 = canvas2;
        Bitmap result2 = result;
        int strideSize3 = strideSize2;
        int pixelSize2 = pixelSize;
        canvas2.drawBitmap(basePixels3, 0, i, 0, 0, baseWidth, baseHeight, true, (Paint) null);
        if (bitmap != null) {
            if (sCutPaint == null) {
                synchronized (IconCustomizer.class) {
                    if (sCutPaint == null) {
                        Paint paint = new Paint();
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                        sCutPaint = paint;
                    }
                }
            }
            canvas = canvas3;
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, sCutPaint);
            result2.getPixels(basePixels3, 0, strideSize3, 0, 0, baseWidth, baseHeight);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        } else {
            canvas = canvas3;
        }
        if (bitmap2 == null || modBackground.getByteCount() / 4 != pixelSize2) {
            basePixels = basePixels3;
            strideSize = strideSize3;
            bgColor = null;
        } else {
            basePixels = basePixels3;
            strideSize = strideSize3;
            bgColor = calcBackgroundColor(pixelSize2, baseWidth, strideSize, basePixels, bitmap2);
        }
        if (!(bgColor == null || bgColor[3] == 0.0f)) {
            Paint bgPaint = new Paint();
            ColorMatrix cm = new ColorMatrix();
            cm.set(new float[]{bgColor[0], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, bgColor[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, bgColor[2], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
            bgPaint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(bitmap2, 0.0f, 0.0f, bgPaint);
        }
        if ((!(bgColor == null || bgColor[3] == 0.0f) || bgColor == null) && bitmap3 != null) {
            canvas.drawBitmap(bitmap3, 0.0f, 0.0f, (Paint) null);
        }
        int[] iArr = basePixels;
        int i2 = strideSize;
        Canvas canvas4 = canvas;
        canvas.drawBitmap(basePixels, 0, strideSize, 0, 0, baseWidth, baseHeight, true, (Paint) null);
        int i3 = pixelSize2;
        Bitmap bitmap5 = modCover;
        if (bitmap5 != null) {
            canvas4.drawBitmap(bitmap5, 0.0f, 0.0f, (Paint) null);
        }
        return result2;
    }

    private static Matrix makeIconMatrix(IconConfig iconConfig) {
        Matrix matrix = new Matrix();
        if (iconConfig.mPointsMappingFrom != null) {
            matrix.setPolyToPoly(iconConfig.mPointsMappingFrom, 0, iconConfig.mPointsMappingTo, 0, iconConfig.mPointsMappingFrom.length / 2);
        } else {
            Camera camera = new Camera();
            camera.rotateX(iconConfig.mRotateX);
            camera.rotateY(iconConfig.mRotateY);
            camera.rotateZ(iconConfig.mRotateZ);
            camera.getMatrix(matrix);
            matrix.preTranslate((((float) (-getCustomizedIconWidth())) / sMaxContentRatio) - iconConfig.mCameraX, (((float) (-getCustomizedIconHeight())) / sMaxContentRatio) - iconConfig.mCameraY);
            matrix.postTranslate((((float) getCustomizedIconWidth()) / sMaxContentRatio) + iconConfig.mCameraX, (((float) getCustomizedIconHeight()) / sMaxContentRatio) + iconConfig.mCameraY);
            matrix.postScale(iconConfig.mScaleX, iconConfig.mScaleY);
            matrix.postSkew(iconConfig.mSkewX, iconConfig.mSkewY);
        }
        return matrix;
    }

    private static IImageFilter.ImageFilterGroup loadIconFilters(NodeList configs) {
        String IGNORE_WHEN_NOT_SUPPORT;
        String IGNORE_WHEN_NOT_SUPPORT2;
        String IGNORE_WHEN_NOT_SUPPORT3;
        NodeList nodeList = configs;
        String IGNORE_WHEN_NOT_SUPPORT4 = "ignoreWhenNotSupported";
        List<IImageFilter> list = new ArrayList<>();
        int i = 0;
        while (i < configs.getLength()) {
            short s = 1;
            if (nodeList.item(i).getNodeType() != 1) {
                IGNORE_WHEN_NOT_SUPPORT = IGNORE_WHEN_NOT_SUPPORT4;
            } else {
                ImageFilterBuilder builder = new ImageFilterBuilder();
                Element ele = (Element) nodeList.item(i);
                if ("Filter".equals(ele.getTagName())) {
                    NodeList filterChildren = ele.getChildNodes();
                    int j = 0;
                    while (j < filterChildren.getLength()) {
                        if (filterChildren.item(j).getNodeType() != s) {
                            IGNORE_WHEN_NOT_SUPPORT2 = IGNORE_WHEN_NOT_SUPPORT4;
                        } else {
                            Element paramEle = (Element) filterChildren.item(j);
                            if ("Param".equals(paramEle.getNodeName())) {
                                ArrayList<Object> paramValues = new ArrayList<>();
                                String strParamIgnoreWhenNotSupported = paramEle.getAttribute("ignoreWhenNotSupported");
                                if (paramEle.hasChildNodes()) {
                                    NodeList paramChildNodes = paramEle.getChildNodes();
                                    int k = 0;
                                    while (k < paramChildNodes.getLength()) {
                                        if (paramChildNodes.item(k).getNodeType() != 1) {
                                            IGNORE_WHEN_NOT_SUPPORT3 = IGNORE_WHEN_NOT_SUPPORT4;
                                        } else {
                                            Element paramChildEle = (Element) paramChildNodes.item(k);
                                            IGNORE_WHEN_NOT_SUPPORT3 = IGNORE_WHEN_NOT_SUPPORT4;
                                            if ("IconFilters".equals(paramChildEle.getTagName())) {
                                                paramValues.add(loadIconFilters(paramChildEle.getChildNodes()));
                                            }
                                        }
                                        k++;
                                        NodeList nodeList2 = configs;
                                        IGNORE_WHEN_NOT_SUPPORT4 = IGNORE_WHEN_NOT_SUPPORT3;
                                    }
                                    IGNORE_WHEN_NOT_SUPPORT2 = IGNORE_WHEN_NOT_SUPPORT4;
                                } else {
                                    IGNORE_WHEN_NOT_SUPPORT2 = IGNORE_WHEN_NOT_SUPPORT4;
                                }
                                if (paramValues.size() == 0) {
                                    for (String str : paramEle.getAttribute("value").split("\\|")) {
                                        if (!TextUtils.isEmpty(str)) {
                                            paramValues.add(str);
                                        }
                                    }
                                }
                                builder.addParam(paramEle.getAttribute("name"), paramValues, TextUtils.isEmpty(strParamIgnoreWhenNotSupported) ? false : Boolean.TRUE.toString().equalsIgnoreCase(strParamIgnoreWhenNotSupported));
                            } else {
                                IGNORE_WHEN_NOT_SUPPORT2 = IGNORE_WHEN_NOT_SUPPORT4;
                            }
                        }
                        j++;
                        s = 1;
                        NodeList nodeList3 = configs;
                        IGNORE_WHEN_NOT_SUPPORT4 = IGNORE_WHEN_NOT_SUPPORT2;
                    }
                    IGNORE_WHEN_NOT_SUPPORT = IGNORE_WHEN_NOT_SUPPORT4;
                    builder.setFilterName(ele.getAttribute("name"));
                    String strFilterIgnoreWhenNotSupported = ele.getAttribute("ignoreWhenNotSupported");
                    builder.setIgnoreWhenNotSupported(TextUtils.isEmpty(strFilterIgnoreWhenNotSupported) ? false : Boolean.TRUE.toString().equalsIgnoreCase(strFilterIgnoreWhenNotSupported));
                    try {
                        IImageFilter filter = builder.createImageFilter();
                        if (filter != null) {
                            list.add(filter);
                        }
                    } catch (ImageFilterBuilder.NoSupportException e) {
                        e.printStackTrace();
                        list.clear();
                    }
                } else {
                    IGNORE_WHEN_NOT_SUPPORT = IGNORE_WHEN_NOT_SUPPORT4;
                }
            }
            i++;
            nodeList = configs;
            IGNORE_WHEN_NOT_SUPPORT4 = IGNORE_WHEN_NOT_SUPPORT;
        }
        return new IImageFilter.ImageFilterGroup((IImageFilter[]) list.toArray(new IImageFilter[0]));
    }

    public static float hdpiIconSizeToCurrent(float pixelSize) {
        return (((float) getCustomizedIconWidth()) * pixelSize) / 90.0f;
    }

    public static int hdpiIconSizeToCurrent(int pixelSize) {
        return (int) ((((float) (getCustomizedIconWidth() * pixelSize)) / 90.0f) + 0.5f);
    }

    public static double hdpiIconSizeToCurrent(double pixelSize) {
        return (((double) getCustomizedIconWidth()) * pixelSize) / 90.0d;
    }

    private static IconConfig loadIconConfig() {
        IconConfig iconConfig = new IconConfig();
        if (!miui.os.Build.IS_MIUI || ThemeResources.getSystem() == null) {
            Log.w(LOG_TAG, "can't load ThemeResources");
            return iconConfig;
        }
        InputStream input = ThemeResources.getSystem().getIconStream(ICON_TRANSFORM_CONFIG, (long[]) null);
        if (input == null) {
            Log.w(LOG_TAG, "can't load transform_config.xml");
            return iconConfig;
        }
        try {
            Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input).getDocumentElement();
            try {
                input.close();
                if (root != null) {
                    NodeList configs = root.getChildNodes();
                    int i = 0;
                    int i2 = 0;
                    while (true) {
                        try {
                            short s = 1;
                            if (i2 >= configs.getLength()) {
                                break;
                            }
                            if (configs.item(i2).getNodeType() == 1) {
                                Element config = (Element) configs.item(i2);
                                String name = config.getTagName();
                                if ("IconFilters".equals(name)) {
                                    iconConfig.mIconFilters = loadIconFilters(config.getChildNodes());
                                } else if ("PointsMapping".equals(name)) {
                                    NodeList points = config.getChildNodes();
                                    List<Float> pointsMappingFrom = new ArrayList<>();
                                    List<Float> pointsMappingTo = new ArrayList<>();
                                    int j = i;
                                    while (j < points.getLength()) {
                                        if (points.item(j).getNodeType() == s) {
                                            Element point = (Element) points.item(j);
                                            if ("Point".equals(point.getNodeName())) {
                                                pointsMappingFrom.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(point.getAttribute("fromX")))));
                                                pointsMappingFrom.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(point.getAttribute("fromY")))));
                                                pointsMappingTo.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(point.getAttribute("toX")))));
                                                pointsMappingTo.add(Float.valueOf(hdpiIconSizeToCurrent(Float.parseFloat(point.getAttribute("toY")))));
                                            }
                                        }
                                        j++;
                                        s = 1;
                                    }
                                    int size = pointsMappingFrom.size();
                                    if (size > 0 && size <= 8) {
                                        iconConfig.mPointsMappingFrom = new float[size];
                                        iconConfig.mPointsMappingTo = new float[size];
                                        for (int j2 = 0; j2 < size; j2++) {
                                            iconConfig.mPointsMappingFrom[j2] = pointsMappingFrom.get(j2).floatValue();
                                            iconConfig.mPointsMappingTo[j2] = pointsMappingTo.get(j2).floatValue();
                                        }
                                    }
                                } else if ("Config".equals(name)) {
                                    String configName = config.getAttribute("name");
                                    String configValue = config.getAttribute("value");
                                    if ("UseModIcon".equalsIgnoreCase(configName)) {
                                        iconConfig.mUseModIcon = Boolean.parseBoolean(configValue);
                                    } else if ("SupportLayerIcon".equalsIgnoreCase(configName)) {
                                        iconConfig.mSupportLayerIcon = Boolean.parseBoolean(configValue);
                                    }
                                } else if ("ScaleX".equals(name)) {
                                    iconConfig.mScaleX = Float.parseFloat(config.getAttribute("value"));
                                } else if ("ScaleY".equals(name)) {
                                    iconConfig.mScaleY = Float.parseFloat(config.getAttribute("value"));
                                } else if ("SkewX".equals(name)) {
                                    iconConfig.mSkewX = Float.parseFloat(config.getAttribute("value"));
                                } else if ("SkewY".equals(name)) {
                                    iconConfig.mSkewY = Float.parseFloat(config.getAttribute("value"));
                                } else if ("TransX".equals(name)) {
                                    iconConfig.mTransX = hdpiIconSizeToCurrent(Float.parseFloat(config.getAttribute("value")));
                                } else if ("TransY".equals(name)) {
                                    iconConfig.mTransY = hdpiIconSizeToCurrent(Float.parseFloat(config.getAttribute("value")));
                                } else if ("RotateX".equals(name)) {
                                    iconConfig.mRotateX = Float.parseFloat(config.getAttribute("value"));
                                } else if ("RotateY".equals(name)) {
                                    iconConfig.mRotateY = Float.parseFloat(config.getAttribute("value"));
                                } else if ("RotateZ".equals(name)) {
                                    iconConfig.mRotateZ = Float.parseFloat(config.getAttribute("value"));
                                } else if ("CameraX".equals(name)) {
                                    iconConfig.mCameraX = hdpiIconSizeToCurrent(Float.parseFloat(config.getAttribute("value")));
                                } else if ("CameraY".equals(name)) {
                                    iconConfig.mCameraY = hdpiIconSizeToCurrent(Float.parseFloat(config.getAttribute("value")));
                                } else if ("OverridedIrregularContentWidth".equals(name)) {
                                    iconConfig.mOverridedIrregularContentWidth = getDimension(config.getAttribute("value"));
                                } else if ("OverridedIrregularContentHeight".equals(name)) {
                                    iconConfig.mOverridedIrregularContentHeight = getDimension(config.getAttribute("value"));
                                }
                            }
                            i2++;
                            i = 0;
                        } catch (Exception e) {
                            Log.w(LOG_TAG, "transform_config.xml parse failed.", e);
                        }
                    }
                    sIconTransformNeeded = true;
                }
                return iconConfig;
            } catch (IOException e2) {
                e2.printStackTrace();
                return iconConfig;
            }
        } catch (Exception e3) {
            Log.w(LOG_TAG, "load icon config failed.", e3);
            return iconConfig;
        }
    }

    private static int getDimension(String value) {
        return TypedValue.complexToDimensionPixelSize(MiuiThemeHelper.parseDimension(value).intValue(), Resources.getSystem().getDisplayMetrics());
    }

    private static float[] calcBackgroundColor(int pixelSize, int width, int strideSize, int[] basePixels, Bitmap bg) {
        int color;
        int sum = 0;
        int[] sumRGBA = {0, 0, 0, 0};
        int[] RGB = {0, 0, 0};
        int i = 0;
        byte[] bgPixels = BitmapUtil.getBuffer(bg);
        while (i < pixelSize) {
            for (int j = 0; j < width; j++) {
                int color2 = basePixels[i + j];
                if ((sRGBMask & color2) > 0) {
                    sumRGBA[0] = sumRGBA[0] + ((16711680 & color2) >> 16);
                    sumRGBA[1] = sumRGBA[1] + ((65280 & color2) >> 8);
                    sumRGBA[2] = sumRGBA[2] + (color2 & Constants.BYTE_MASK);
                    sum++;
                }
                if (sumRGBA[3] == 0 && bgPixels != null) {
                    sumRGBA[3] = sumRGBA[3] + ((color2 >> 24) - bgPixels[((i + j) << 2) + 3]);
                }
            }
            i += strideSize;
        }
        int i2 = width;
        if (sum > 0) {
            sumRGBA[0] = sumRGBA[0] / sum;
            sumRGBA[1] = sumRGBA[1] / sum;
            sumRGBA[2] = sumRGBA[2] / sum;
        }
        int color3 = RGBToColor(sumRGBA);
        if (((double) getSaturation(color3, RGB)) < 0.02d) {
            color = 0;
        } else {
            int[][] mappingSections = {new int[]{100, SoapEnvelope.VER11}, new int[]{190, 275}};
            int sum2 = 0;
            for (int i3 = 0; i3 < mappingSections.length; i3++) {
                sum2 += mappingSections[i3][1] - mappingSections[i3][0];
            }
            float hue = (((float) sum2) * getHue(color3, RGB)) / 360.0f;
            int i4 = 0;
            while (true) {
                if (i4 >= mappingSections.length) {
                    break;
                }
                int length = mappingSections[i4][1] - mappingSections[i4][0];
                if (hue <= ((float) length)) {
                    hue += (float) mappingSections[i4][0];
                    break;
                }
                hue -= (float) length;
                i4++;
            }
            color = setSaturation(setValue(setHue(color3, hue, RGB), 0.6f, RGB), 0.4f, RGB);
        }
        colorToRGB(color, sumRGBA);
        return new float[]{((float) sumRGBA[0]) / 255.0f, ((float) sumRGBA[1]) / 255.0f, ((float) sumRGBA[2]) / 255.0f, ((float) sumRGBA[3]) / 255.0f};
    }

    private static void colorToRGB(int color, int[] rgb) {
        rgb[0] = (16711680 & color) >> 16;
        rgb[1] = (65280 & color) >> 8;
        rgb[2] = color & Constants.BYTE_MASK;
    }

    private static int RGBToColor(int[] RGB) {
        return (((RGB[0] << 8) + RGB[1]) << 8) + RGB[2];
    }

    private static float getValue(int color, int[] RGB) {
        colorToRGB(color, RGB);
        return (((float) Math.max(RGB[0], Math.max(RGB[1], RGB[2]))) * 1.0f) / 255.0f;
    }

    private static int setValue(int color, float value, int[] RGB) {
        colorToRGB(color, RGB);
        int max = Math.max(RGB[0], Math.max(RGB[1], RGB[2]));
        if (max == 0) {
            return color;
        }
        float currentValue = (((float) max) * 1.0f) / 255.0f;
        RGB[0] = (int) ((((float) RGB[0]) * value) / currentValue);
        RGB[1] = (int) ((((float) RGB[1]) * value) / currentValue);
        RGB[2] = (int) ((((float) RGB[2]) * value) / currentValue);
        return RGBToColor(RGB);
    }

    private static float getSaturation(int color, int[] RGB) {
        colorToRGB(color, RGB);
        int min = Math.min(RGB[0], Math.min(RGB[1], RGB[2]));
        int max = Math.max(RGB[0], Math.max(RGB[1], RGB[2]));
        if (max == 0 || max == min) {
            return (float) color;
        }
        return (((float) (max - min)) * 1.0f) / ((float) max);
    }

    private static int setSaturation(int color, float saturation, int[] RGB) {
        colorToRGB(color, RGB);
        int min = Math.min(RGB[0], Math.min(RGB[1], RGB[2]));
        int max = Math.max(RGB[0], Math.max(RGB[1], RGB[2]));
        if (max == 0 || max == min) {
            return color;
        }
        float currentSaturation = (((float) (max - min)) * 1.0f) / ((float) max);
        RGB[0] = (int) (((float) max) - ((((float) (max - RGB[0])) * saturation) / currentSaturation));
        RGB[1] = (int) (((float) max) - ((((float) (max - RGB[1])) * saturation) / currentSaturation));
        RGB[2] = (int) (((float) max) - ((((float) (max - RGB[2])) * saturation) / currentSaturation));
        return RGBToColor(RGB);
    }

    private static float getHue(int color, int[] RGB) {
        colorToRGB(color, RGB);
        int min = Math.min(RGB[0], Math.min(RGB[1], RGB[2]));
        int max = Math.max(RGB[0], Math.max(RGB[1], RGB[2]));
        int range = max - min;
        if (range == 0) {
            return 0.0f;
        }
        int index = 0;
        while (index < 2 && min != RGB[index]) {
            index++;
        }
        return ((float) (((index + 1) % 3) * SoapEnvelope.VER12)) + ((((float) (RGB[(index + 2) % 3] - min)) * 60.0f) / ((float) range)) + ((((float) (max - RGB[(index + 1) % 3])) * 60.0f) / ((float) range));
    }

    private static int setHue(int color, float hue, int[] RGB) {
        colorToRGB(color, RGB);
        int min = Math.min(RGB[0], Math.min(RGB[1], RGB[2]));
        int max = Math.max(RGB[0], Math.max(RGB[1], RGB[2]));
        int range = max - min;
        if (range == 0) {
            return color;
        }
        while (hue < 0.0f) {
            hue += 360.0f;
        }
        while (hue > 360.0f) {
            hue -= 360.0f;
        }
        int index = (int) Math.floor((double) (hue / 120.0f));
        float hue2 = hue - ((float) (index * SoapEnvelope.VER12));
        int index2 = (index + 2) % 3;
        RGB[index2] = min;
        RGB[(index2 + 2) % 3] = (int) (((float) min) + ((((float) range) * Math.min(hue2, 60.0f)) / 60.0f));
        RGB[(index2 + 1) % 3] = (int) (((float) max) - ((((float) range) * Math.max(0.0f, hue2 - 60.0f)) / 60.0f));
        return RGBToColor(RGB);
    }
}
