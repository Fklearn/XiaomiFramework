package com.android.server;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.miui.R;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.view.IInputFilter;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.android.server.input.InputManagerService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.List;
import libcore.io.IoUtils;
import miui.app.AlertDialog;
import miui.util.HandyModeUtils;
import miui.util.ScreenshotUtils;

public class HandyMode {
    private static final String ACTION_CHANGEMODE = "miui.action.handymode.changemode";
    public static final String ACTION_HANDYMODE_CHANGE = "miui.action.handymode_change";
    private static String BLURED_WALLPAPER_FILE = "/data/system/blured_wallpaper.png";
    public static final int COMBINATION_CLICK_TIMEOUT = SystemProperties.getInt("persist.sys.handy_mode_cct", 80);
    public static final String HANDYMODE = "handymode";
    public static final String HANDYMODETIME = "handymodetime";
    public static final int MODE_LEFT = 1;
    public static final int MODE_NONE = 0;
    public static final int MODE_RIGHT = 2;
    private static final String NOTCH_FORCE_BLACK_V2 = "force_black_v2";
    private static String SETTING_ICON_FILE = "/data/system/setting_icon_for_handymode.png";
    private static String TITLE_IMAGE_FILE = "/data/system/title_image_for_handymode.png";
    private static boolean isdDisplayOled;
    static boolean mHideNotch;
    static int mNotchHeight;
    private static SoftReference<Dialog> sAlertDialog;
    static boolean sBootCompleted;
    static Context sContext;
    static boolean sDeviceProvisioned;
    static boolean sEnable;
    static String sFiledTitleLanguage;
    static Handler sHandler;
    static MiuiInputFilter sHandyModeInputFilter;
    static HandyModeUtils sHandyModeUtils;
    static int sIconHeight;
    static int sIconWidth;
    static InputManagerService sInputManager;
    /* access modifiers changed from: private */
    public static int sMode = 0;
    static PowerManager sPowerManager;
    static BroadcastReceiver sReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (HandyMode.sMode != 0) {
                HandyMode.changeMode(0);
            }
        }
    };
    static boolean sRegistered;
    static int sRotation;
    static RotationWatcher sRotationWatcher;
    static float sScale;
    static int sScreenHeight;
    static int sScreenWidth;
    static Runnable sSettingClickListener = new Runnable() {
        public void run() {
            HandyMode.gotoHandyModeSetting();
        }
    };
    static int sSettingIconPadding;
    static long sTimeEnter;
    static WallpaperManager sWallpaperManager;
    static IWindowManager sWindowManager;

    static {
        boolean z = false;
        if ("oled".equals(SystemProperties.get("ro.vendor.display.type")) || "oled".equals(SystemProperties.get("ro.display.type"))) {
            z = true;
        }
        isdDisplayOled = z;
    }

    static class RotationWatcher extends IRotationWatcher.Stub {
        RotationWatcher() {
        }

        public void onRotationChanged(int rotation) {
            if (HandyModeUtils.isFeatureVisible() && rotation != 0 && HandyMode.sMode != 0) {
                HandyMode.sHandler.post(new Runnable() {
                    public void run() {
                        HandyMode.changeMode(0);
                    }
                });
            }
        }
    }

    public static void initialize(Context context, InputManagerService inputManager) {
        if (sContext == null) {
            sContext = context;
            sHandler = new Handler(context.getMainLooper());
            sHandyModeUtils = HandyModeUtils.getInstance(context);
            mNotchHeight = context.getResources().getDimensionPixelSize(17105467);
            mHideNotch = MiuiSettings.Global.getBoolean(sContext.getContentResolver(), NOTCH_FORCE_BLACK_V2);
            sInputManager = inputManager;
            sWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
            sHandyModeInputFilter = new MiuiInputFilter(context);
            sPowerManager = (PowerManager) context.getSystemService("power");
            Point size = new Point();
            ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getSize(size);
            sScreenWidth = Math.min(size.x, size.y);
            sScreenHeight = Math.max(size.x, size.y);
            sSettingIconPadding = Math.round(context.getResources().getDisplayMetrics().density * 5.0f);
            sEnable = sHandyModeUtils.isEnable();
            sScale = sHandyModeUtils.getScale();
            if (sEnable) {
                sInputManager.setInputFilter(sHandyModeInputFilter);
            }
            if (HandyModeUtils.isFeatureVisible()) {
                ContentObserver observer = new ContentObserver(new Handler(sContext.getMainLooper())) {
                    public void onChange(boolean selfChange, Uri uri) {
                        List<String> pathSegments = uri != null ? uri.getPathSegments() : null;
                        if (pathSegments != null && pathSegments.size() == 2) {
                            if ("handy_mode_size".equals(pathSegments.get(1))) {
                                HandyMode.refreshStatus();
                            } else if ("handy_mode_state".equals(pathSegments.get(1))) {
                                if (HandyMode.sHandyModeUtils.isEnable()) {
                                    HandyMode.sInputManager.setInputFilter(HandyMode.sHandyModeInputFilter);
                                } else {
                                    HandyMode.sInputManager.setInputFilter((IInputFilter) null);
                                }
                                HandyMode.refreshStatus();
                            }
                        }
                    }
                };
                sContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("handy_mode_size"), false, observer, -1);
                sContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("handy_mode_state"), false, observer, -1);
                sContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("auto_test_mode_on"), true, new ContentObserver(sHandler) {
                    public void onChange(boolean selfChange) {
                        boolean citTestEnabled = false;
                        if (Settings.Global.getInt(HandyMode.sContext.getContentResolver(), "auto_test_mode_on", 0) != 0) {
                            citTestEnabled = true;
                        }
                        HandyMode.sHandyModeInputFilter.setCitTestEnabled(citTestEnabled);
                    }
                }, -1);
                sContext.registerReceiver(new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        HandyMode.mHideNotch = MiuiSettings.Global.getBoolean(HandyMode.sContext.getContentResolver(), HandyMode.NOTCH_FORCE_BLACK_V2);
                        HandyMode.sHandyModeInputFilter.updateOutsideClickableRect(new Rect(HandyMode.getSettingIconClickRect()), HandyMode.sSettingClickListener);
                    }
                }, new IntentFilter("android.intent.action.CONFIGURATION_CHANGED"));
                sContext.registerReceiver(new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        HandyMode.destroyBluredWallpaper();
                    }
                }, new IntentFilter("android.intent.action.WALLPAPER_CHANGED"));
                sContext.registerReceiver(new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        int newMode = intent.getIntExtra("mode", 0);
                        if (HandyMode.sHandyModeUtils.isEnable() && HandyMode.sHandyModeInputFilter.isInstalled()) {
                            if (HandyMode.getMode() != 0 || HandyMode.sHandyModeUtils.isEnterDirect()) {
                                HandyMode.changeMode(newMode);
                            } else {
                                HandyMode.alertToEnter(newMode);
                            }
                        }
                    }
                }, new IntentFilter(ACTION_CHANGEMODE));
                File file = new File(SETTING_ICON_FILE);
                if (file.exists()) {
                    file.delete();
                }
                File file2 = new File(TITLE_IMAGE_FILE);
                if (file2.exists()) {
                    file2.delete();
                }
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                miui.graphics.BitmapFactory.decodeResource(sContext.getResources(), R.drawable.handy_mode_quit_icon, opts);
                sIconWidth = opts.outWidth;
                sIconHeight = opts.outHeight;
            }
        }
    }

    /* access modifiers changed from: private */
    public static void refreshStatus() {
        if (sEnable != sHandyModeUtils.isEnable()) {
            sEnable = sHandyModeUtils.isEnable();
            if (!sEnable && sMode != 0) {
                changeMode(0);
            }
        }
        if (sScale != sHandyModeUtils.getScale()) {
            sScale = sHandyModeUtils.getScale();
            if (sMode != 0) {
                final int mode = sMode;
                changeMode(0);
                new Handler(sContext.getMainLooper()).postDelayed(new Runnable() {
                    public void run() {
                        HandyMode.changeMode(mode);
                    }
                }, 300);
            }
        }
    }

    static void gotoHandyModeSetting() {
        Intent settingIntent = new Intent("android.intent.action.MAIN");
        settingIntent.setClassName("com.android.settings", "com.android.settings.Settings");
        settingIntent.putExtra(":android:show_fragment", "com.android.settings.display.HandyModeFragment");
        settingIntent.putExtra(":android:show_fragment_title", 0);
        settingIntent.putExtra(":android:show_fragment_short_title", 0);
        settingIntent.putExtra(":android:no_headers", true);
        settingIntent.addFlags(268435456);
        sContext.startActivityAsUser(settingIntent, UserHandle.CURRENT);
    }

    public static void alertToEnter(final int newMode) {
        if (canEnterHandyMode() && !((KeyguardManager) sContext.getSystemService("keyguard")).isKeyguardLocked()) {
            SoftReference<Dialog> softReference = sAlertDialog;
            Dialog dlg = softReference == null ? null : softReference.get();
            if (dlg == null || !dlg.isShowing()) {
                Dialog dlg2 = new AlertDialog.Builder(sContext).setTitle(R.string.handy_mode_enter_dlg_title).setMessage(R.string.handy_mode_enter_dlg_msg).setPositiveButton(17039370, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HandyMode.changeMode(newMode);
                    }
                }).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
                dlg2.getWindow().setType(2008);
                sAlertDialog = new SoftReference<>(dlg2);
                dlg2.show();
            }
        }
    }

    static boolean canEnterHandyMode() {
        if (!HandyModeUtils.isFeatureVisible()) {
            return false;
        }
        if (!sBootCompleted) {
            sBootCompleted = SystemProperties.getBoolean("sys.boot_completed", false);
            if (!sBootCompleted) {
                return false;
            }
        }
        if (!sDeviceProvisioned) {
            sDeviceProvisioned = Settings.Secure.getInt(sContext.getContentResolver(), "device_provisioned", 0) != 0;
            if (!sDeviceProvisioned) {
                return false;
            }
        }
        try {
            if (sWindowManager.getDefaultDisplayRotation() != 0) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public static void changeMode(final int mode) {
        if (!Thread.currentThread().getName().equals(sHandler.getLooper().getThread().getName())) {
            sHandler.post(new Runnable() {
                public void run() {
                    HandyMode.changeMode(mode);
                }
            });
            return;
        }
        sScale = sHandyModeUtils.getScale();
        if (mode == 0 || canEnterHandyMode()) {
            sPowerManager.userActivity(SystemClock.uptimeMillis(), false);
            if (mode == 0 || mode != sMode) {
                sMode = mode;
                sHandyModeInputFilter.removeOutsideClickableRect(sSettingClickListener);
                Intent intent = new Intent(ACTION_HANDYMODE_CHANGE);
                intent.putExtra(HANDYMODE, sMode);
                if (mode != 0) {
                    if (sWallpaperManager == null) {
                        sWallpaperManager = (WallpaperManager) sContext.getSystemService("wallpaper");
                    }
                    if (!sRegistered) {
                        sContext.registerReceiver(sReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
                        if (sRotationWatcher == null) {
                            sRotationWatcher = new RotationWatcher();
                        }
                        try {
                            sWindowManager.watchRotation(sRotationWatcher, sContext.getDisplay().getDisplayId());
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        sRegistered = true;
                    }
                    ensureAllImages();
                    ensureBluredWallpaper();
                    sHandyModeInputFilter.addOutsideClickableRect(getSettingIconClickRect(), sSettingClickListener);
                    if (!sHandyModeUtils.hasShowed()) {
                        sHandyModeUtils.setEnterDirect(false);
                        gotoHandyModeSetting();
                    }
                    sTimeEnter = SystemClock.elapsedRealtime();
                } else {
                    if (sRegistered) {
                        sContext.unregisterReceiver(sReceiver);
                        try {
                            sWindowManager.removeRotationWatcher(sRotationWatcher);
                        } catch (RemoteException ex2) {
                            ex2.printStackTrace();
                        }
                        sRegistered = false;
                    }
                    intent.putExtra(HANDYMODETIME, SystemClock.elapsedRealtime() - sTimeEnter);
                }
                sContext.sendStickyBroadcast(intent);
                try {
                    IBinder flinger = ServiceManager.getService("SurfaceFlinger");
                    if (flinger != null) {
                        Parcel data = Parcel.obtain();
                        data.writeInterfaceToken("android.ui.ISurfaceComposer");
                        data.writeInt(mode);
                        data.writeFloat(sScale);
                        flinger.transact(1098, data, (Parcel) null, 0);
                        data.recycle();
                    }
                } catch (RemoteException ex3) {
                    ex3.printStackTrace();
                }
            } else {
                changeMode(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void destroyBluredWallpaper() {
        File file = new File(BLURED_WALLPAPER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    private static void ensureAllImages() {
        ensureIcon(SETTING_ICON_FILE, R.drawable.handy_mode_setting_icon);
        ensureTitleImage();
    }

    private static void ensureTitleImage() {
        File file = new File(TITLE_IMAGE_FILE);
        if (!file.exists() || !sContext.getResources().getConfiguration().locale.getLanguage().equals(sFiledTitleLanguage)) {
            sFiledTitleLanguage = sContext.getResources().getConfiguration().locale.getLanguage();
            View rootView = LayoutInflater.from(sContext).inflate(R.layout.handy_mode_title, (ViewGroup) null);
            rootView.measure(View.MeasureSpec.makeMeasureSpec(sContext.getResources().getDisplayMetrics().widthPixels - (((int) (sContext.getResources().getDisplayMetrics().density * 60.0f)) * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
            rootView.layout(0, 0, rootView.getMeasuredWidth(), rootView.getMeasuredHeight());
            Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
            rootView.draw(new Canvas(bitmap));
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Throwable th) {
                IoUtils.closeQuietly(stream);
                throw th;
            }
            IoUtils.closeQuietly(stream);
        }
    }

    private static void ensureIcon(String fileName, int iconId) {
        File file = new File(fileName);
        if (!file.exists()) {
            InputStream is = sContext.getResources().openRawResource(iconId);
            FileUtils.copyToFile(is, file);
            IoUtils.closeQuietly(is);
        }
    }

    private static void ensureBluredWallpaper() {
        Bitmap wallpaper;
        File file = new File(BLURED_WALLPAPER_FILE);
        if (sWallpaperManager.getWallpaperInfo() != null) {
            if (file.exists()) {
                file.delete();
            }
        } else if (!file.exists() && (wallpaper = sWallpaperManager.getBitmap()) != null) {
            saveBitmapToPNG(buildBluredWallpaper(wallpaper), file);
            sWallpaperManager.forgetLoadedWallpaper();
        }
    }

    private static Bitmap buildBluredWallpaper(Bitmap wallpaper) {
        int dstWidth = Math.max(sScreenWidth / ScreenshotUtils.REAL_BLUR_MINIFY, 1);
        int dstHeight = Math.max(sScreenHeight / ScreenshotUtils.REAL_BLUR_MINIFY, 1);
        Bitmap newBitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas(newBitmap);
        Rect dstRect = new Rect(0, 0, dstWidth, dstHeight);
        Rect srcRect = new Rect();
        if (wallpaper.getWidth() / dstWidth > wallpaper.getHeight() / dstHeight) {
            int srcWidth = (int) ((((float) wallpaper.getHeight()) / ((float) dstHeight)) * ((float) dstWidth));
            int left = (wallpaper.getWidth() - srcWidth) / 2;
            srcRect.set(left, 0, left + srcWidth, wallpaper.getHeight());
        } else {
            int srcHeight = (int) ((((float) wallpaper.getWidth()) / ((float) dstWidth)) * ((float) dstHeight));
            int top = (wallpaper.getHeight() - srcHeight) / 2;
            srcRect.set(0, top, wallpaper.getWidth(), top + srcHeight);
        }
        newCanvas.drawBitmap(wallpaper, srcRect, dstRect, new Paint(2));
        newCanvas.drawColor(Color.argb((int) (ScreenshotUtils.REAL_BLUR_BLACK * 255.0f), 0, 0, 0));
        newCanvas.drawColor(sContext.getResources().getColor(isdDisplayOled ? R.color.realtimeblur_bg_oled : R.color.realtimeblur_bg));
        if (Build.VERSION.SDK_INT >= 24) {
            return fastBlur(newBitmap);
        }
        return miui.graphics.BitmapFactory.fastBlur(newBitmap, (int) (((float) ScreenshotUtils.REAL_BLUR_RADIUS) * Resources.getSystem().getDisplayMetrics().density));
    }

    private static Bitmap fastBlur(Bitmap background) {
        int i;
        Resources resources = sContext.getResources();
        if (isdDisplayOled) {
            i = R.color.realtimeblur_bg_oled;
        } else {
            i = R.color.realtimeblur_bg;
        }
        int color_realtimeblur_bg = resources.getColor(i);
        Bitmap background2 = Bitmap.createScaledBitmap(background, 1, 1, true);
        new Canvas(background2).drawColor(color_realtimeblur_bg);
        return background2;
    }

    private static void saveBitmapToPNG(Bitmap bitmap, File file) {
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getMode() {
        return sMode;
    }

    /* access modifiers changed from: private */
    public static Rect getSettingIconClickRect() {
        int iconToBorder = (Math.round(((float) sScreenWidth) * (1.0f - sScale)) - sIconWidth) / 2;
        int left = sMode == 2 ? iconToBorder : Math.round(((float) sScreenWidth) * sScale) + iconToBorder;
        int right = sIconWidth + left;
        int top = (sScreenHeight - iconToBorder) - sIconHeight;
        if (mHideNotch) {
            top -= mNotchHeight;
        }
        int i = sSettingIconPadding;
        return new Rect(left - i, top - i, right + i, i + sIconHeight + top);
    }
}
