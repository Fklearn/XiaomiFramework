package com.android.server.display.color;

import android.animation.TypeEvaluator;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.IColorDisplayManager;
import android.hardware.display.Time;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.MathUtils;
import android.util.Slog;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.server.DisplayThread;
import com.android.server.SystemService;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

public final class ColorDisplayService extends SystemService {
    private static final ColorMatrixEvaluator COLOR_MATRIX_EVALUATOR = new ColorMatrixEvaluator();
    private static final float[] MATRIX_GRAYSCALE = {0.2126f, 0.2126f, 0.2126f, 0.0f, 0.7152f, 0.7152f, 0.7152f, 0.0f, 0.0722f, 0.0722f, 0.0722f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    static final float[] MATRIX_IDENTITY = new float[16];
    private static final float[] MATRIX_INVERT_COLOR = {0.402f, -0.598f, -0.599f, 0.0f, -1.174f, -0.174f, -1.175f, 0.0f, -0.228f, -0.228f, 0.772f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    private static final int MSG_APPLY_DISPLAY_WHITE_BALANCE = 5;
    private static final int MSG_APPLY_GLOBAL_SATURATION = 4;
    private static final int MSG_APPLY_NIGHT_DISPLAY_ANIMATED = 3;
    private static final int MSG_APPLY_NIGHT_DISPLAY_IMMEDIATE = 2;
    private static final int MSG_SET_UP = 1;
    private static final int MSG_USER_CHANGED = 0;
    private static final int NOT_SET = -1;
    static final String TAG = "ColorDisplayService";
    private static final long TRANSITION_DURATION = 3000;
    /* access modifiers changed from: private */
    public final AppSaturationController mAppSaturationController = new AppSaturationController();
    /* access modifiers changed from: private */
    public boolean mBootCompleted;
    private ContentObserver mContentObserver;
    /* access modifiers changed from: private */
    public int mCurrentUser = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    /* access modifiers changed from: private */
    public DisplayWhiteBalanceListener mDisplayWhiteBalanceListener;
    @VisibleForTesting
    final DisplayWhiteBalanceTintController mDisplayWhiteBalanceTintController = new DisplayWhiteBalanceTintController();
    /* access modifiers changed from: private */
    public final TintController mGlobalSaturationTintController = new GlobalSaturationTintController();
    /* access modifiers changed from: private */
    public final Handler mHandler = new TintHandler(DisplayThread.get().getLooper());
    /* access modifiers changed from: private */
    public NightDisplayAutoMode mNightDisplayAutoMode;
    /* access modifiers changed from: private */
    public final NightDisplayTintController mNightDisplayTintController = new NightDisplayTintController();
    /* access modifiers changed from: private */
    public ContentObserver mUserSetupObserver;

    public interface ColorTransformController {
        void applyAppSaturation(float[] fArr, float[] fArr2);
    }

    public interface DisplayWhiteBalanceListener {
        void onDisplayWhiteBalanceStatusChanged(boolean z);
    }

    static {
        Matrix.setIdentityM(MATRIX_IDENTITY, 0);
    }

    public ColorDisplayService(Context context) {
        super(context);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.display.color.ColorDisplayService$BinderService, android.os.IBinder] */
    public void onStart() {
        publishBinderService("color_display", new BinderService());
        publishLocalService(ColorDisplayServiceInternal.class, new ColorDisplayServiceInternal());
        publishLocalService(DisplayTransformManager.class, new DisplayTransformManager());
    }

    public void onBootPhase(int phase) {
        if (phase >= 1000) {
            this.mBootCompleted = true;
            if (this.mCurrentUser != -10000 && this.mUserSetupObserver == null) {
                this.mHandler.sendEmptyMessage(1);
            }
        }
    }

    public void onStartUser(int userHandle) {
        super.onStartUser(userHandle);
        if (this.mCurrentUser == -10000) {
            Message message = this.mHandler.obtainMessage(0);
            message.arg1 = userHandle;
            this.mHandler.sendMessage(message);
        }
    }

    public void onSwitchUser(int userHandle) {
        super.onSwitchUser(userHandle);
        Message message = this.mHandler.obtainMessage(0);
        message.arg1 = userHandle;
        this.mHandler.sendMessage(message);
    }

    public void onStopUser(int userHandle) {
        super.onStopUser(userHandle);
        if (this.mCurrentUser == userHandle) {
            Message message = this.mHandler.obtainMessage(0);
            message.arg1 = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
            this.mHandler.sendMessage(message);
        }
    }

    /* access modifiers changed from: private */
    public void onUserChanged(int userHandle) {
        final ContentResolver cr = getContext().getContentResolver();
        if (this.mCurrentUser != -10000) {
            ContentObserver contentObserver = this.mUserSetupObserver;
            if (contentObserver != null) {
                cr.unregisterContentObserver(contentObserver);
                this.mUserSetupObserver = null;
            } else if (this.mBootCompleted) {
                tearDown();
            }
        }
        this.mCurrentUser = userHandle;
        int i = this.mCurrentUser;
        if (i == -10000) {
            return;
        }
        if (!isUserSetupCompleted(cr, i)) {
            this.mUserSetupObserver = new ContentObserver(this.mHandler) {
                public void onChange(boolean selfChange, Uri uri) {
                    if (ColorDisplayService.isUserSetupCompleted(cr, ColorDisplayService.this.mCurrentUser)) {
                        cr.unregisterContentObserver(this);
                        ContentObserver unused = ColorDisplayService.this.mUserSetupObserver = null;
                        if (ColorDisplayService.this.mBootCompleted) {
                            ColorDisplayService.this.setUp();
                        }
                    }
                }
            };
            cr.registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this.mUserSetupObserver, this.mCurrentUser);
        } else if (this.mBootCompleted) {
            setUp();
        }
    }

    /* access modifiers changed from: private */
    public static boolean isUserSetupCompleted(ContentResolver cr, int userHandle) {
        return Settings.Secure.getIntForUser(cr, "user_setup_complete", 0, userHandle) == 1;
    }

    /* access modifiers changed from: private */
    public void setUp() {
        Slog.d(TAG, "setUp: currentUser=" + this.mCurrentUser);
        if (this.mContentObserver == null) {
            this.mContentObserver = new ContentObserver(this.mHandler) {
                public void onChange(boolean selfChange, Uri uri) {
                    super.onChange(selfChange, uri);
                    String setting = uri == null ? null : uri.getLastPathSegment();
                    if (setting != null) {
                        char c = 65535;
                        switch (setting.hashCode()) {
                            case -2038150513:
                                if (setting.equals("night_display_auto_mode")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case -1761668069:
                                if (setting.equals("night_display_custom_end_time")) {
                                    c = 4;
                                    break;
                                }
                                break;
                            case -969458956:
                                if (setting.equals("night_display_color_temperature")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case -686921934:
                                if (setting.equals("accessibility_display_daltonizer_enabled")) {
                                    c = 7;
                                    break;
                                }
                                break;
                            case -551230169:
                                if (setting.equals("accessibility_display_inversion_enabled")) {
                                    c = 6;
                                    break;
                                }
                                break;
                            case 483353904:
                                if (setting.equals("accessibility_display_daltonizer")) {
                                    c = 8;
                                    break;
                                }
                                break;
                            case 800115245:
                                if (setting.equals("night_display_activated")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1113469195:
                                if (setting.equals("display_white_balance_enabled")) {
                                    c = 9;
                                    break;
                                }
                                break;
                            case 1561688220:
                                if (setting.equals("display_color_mode")) {
                                    c = 5;
                                    break;
                                }
                                break;
                            case 1578271348:
                                if (setting.equals("night_display_custom_start_time")) {
                                    c = 3;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                boolean activated = ColorDisplayService.this.mNightDisplayTintController.isActivatedSetting();
                                if (ColorDisplayService.this.mNightDisplayTintController.isActivatedStateNotSet() || ColorDisplayService.this.mNightDisplayTintController.isActivated() != activated) {
                                    ColorDisplayService.this.mNightDisplayTintController.setActivated(Boolean.valueOf(activated));
                                    return;
                                }
                                return;
                            case 1:
                                int temperature = ColorDisplayService.this.mNightDisplayTintController.getColorTemperatureSetting();
                                if (ColorDisplayService.this.mNightDisplayTintController.getColorTemperature() != temperature) {
                                    ColorDisplayService.this.mNightDisplayTintController.onColorTemperatureChanged(temperature);
                                    return;
                                }
                                return;
                            case 2:
                                ColorDisplayService colorDisplayService = ColorDisplayService.this;
                                colorDisplayService.onNightDisplayAutoModeChanged(colorDisplayService.getNightDisplayAutoModeInternal());
                                return;
                            case 3:
                                ColorDisplayService colorDisplayService2 = ColorDisplayService.this;
                                colorDisplayService2.onNightDisplayCustomStartTimeChanged(colorDisplayService2.getNightDisplayCustomStartTimeInternal().getLocalTime());
                                return;
                            case 4:
                                ColorDisplayService colorDisplayService3 = ColorDisplayService.this;
                                colorDisplayService3.onNightDisplayCustomEndTimeChanged(colorDisplayService3.getNightDisplayCustomEndTimeInternal().getLocalTime());
                                return;
                            case 5:
                                ColorDisplayService colorDisplayService4 = ColorDisplayService.this;
                                colorDisplayService4.onDisplayColorModeChanged(colorDisplayService4.getColorModeInternal());
                                return;
                            case 6:
                                ColorDisplayService.this.onAccessibilityInversionChanged();
                                ColorDisplayService.this.onAccessibilityActivated();
                                return;
                            case 7:
                                ColorDisplayService.this.onAccessibilityDaltonizerChanged();
                                ColorDisplayService.this.onAccessibilityActivated();
                                return;
                            case 8:
                                ColorDisplayService.this.onAccessibilityDaltonizerChanged();
                                return;
                            case 9:
                                ColorDisplayService.this.updateDisplayWhiteBalanceStatus();
                                return;
                            default:
                                return;
                        }
                    }
                }
            };
        }
        ContentResolver cr = getContext().getContentResolver();
        cr.registerContentObserver(Settings.Secure.getUriFor("night_display_activated"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("night_display_color_temperature"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("night_display_auto_mode"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("night_display_custom_start_time"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("night_display_custom_end_time"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.System.getUriFor("display_color_mode"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer"), false, this.mContentObserver, this.mCurrentUser);
        cr.registerContentObserver(Settings.Secure.getUriFor("display_white_balance_enabled"), false, this.mContentObserver, this.mCurrentUser);
        onAccessibilityInversionChanged();
        onAccessibilityDaltonizerChanged();
        onDisplayColorModeChanged(getColorModeInternal());
        if (this.mNightDisplayTintController.isAvailable(getContext())) {
            this.mNightDisplayTintController.setActivated((Boolean) null);
            this.mNightDisplayTintController.setUp(getContext(), DisplayTransformManager.needsLinearColorMatrix());
            NightDisplayTintController nightDisplayTintController = this.mNightDisplayTintController;
            nightDisplayTintController.setMatrix(nightDisplayTintController.getColorTemperatureSetting());
            onNightDisplayAutoModeChanged(getNightDisplayAutoModeInternal());
            if (this.mNightDisplayTintController.isActivatedStateNotSet()) {
                NightDisplayTintController nightDisplayTintController2 = this.mNightDisplayTintController;
                nightDisplayTintController2.setActivated(Boolean.valueOf(nightDisplayTintController2.isActivatedSetting()));
            }
        }
        if (this.mDisplayWhiteBalanceTintController.isAvailable(getContext())) {
            this.mDisplayWhiteBalanceTintController.setUp(getContext(), true);
            updateDisplayWhiteBalanceStatus();
        }
    }

    private void tearDown() {
        Slog.d(TAG, "tearDown: currentUser=" + this.mCurrentUser);
        if (this.mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
        }
        if (this.mNightDisplayTintController.isAvailable(getContext())) {
            NightDisplayAutoMode nightDisplayAutoMode = this.mNightDisplayAutoMode;
            if (nightDisplayAutoMode != null) {
                nightDisplayAutoMode.onStop();
                this.mNightDisplayAutoMode = null;
            }
            this.mNightDisplayTintController.endAnimator();
        }
        if (this.mDisplayWhiteBalanceTintController.isAvailable(getContext())) {
            this.mDisplayWhiteBalanceTintController.endAnimator();
        }
        if (this.mGlobalSaturationTintController.isAvailable(getContext())) {
            this.mGlobalSaturationTintController.setActivated((Boolean) null);
        }
    }

    /* access modifiers changed from: private */
    public void onNightDisplayAutoModeChanged(int autoMode) {
        Slog.d(TAG, "onNightDisplayAutoModeChanged: autoMode=" + autoMode);
        NightDisplayAutoMode nightDisplayAutoMode = this.mNightDisplayAutoMode;
        if (nightDisplayAutoMode != null) {
            nightDisplayAutoMode.onStop();
            this.mNightDisplayAutoMode = null;
        }
        if (autoMode == 1) {
            this.mNightDisplayAutoMode = new CustomNightDisplayAutoMode();
        } else if (autoMode == 2) {
            this.mNightDisplayAutoMode = new TwilightNightDisplayAutoMode();
        }
        NightDisplayAutoMode nightDisplayAutoMode2 = this.mNightDisplayAutoMode;
        if (nightDisplayAutoMode2 != null) {
            nightDisplayAutoMode2.onStart();
        }
    }

    /* access modifiers changed from: private */
    public void onNightDisplayCustomStartTimeChanged(LocalTime startTime) {
        Slog.d(TAG, "onNightDisplayCustomStartTimeChanged: startTime=" + startTime);
        NightDisplayAutoMode nightDisplayAutoMode = this.mNightDisplayAutoMode;
        if (nightDisplayAutoMode != null) {
            nightDisplayAutoMode.onCustomStartTimeChanged(startTime);
        }
    }

    /* access modifiers changed from: private */
    public void onNightDisplayCustomEndTimeChanged(LocalTime endTime) {
        Slog.d(TAG, "onNightDisplayCustomEndTimeChanged: endTime=" + endTime);
        NightDisplayAutoMode nightDisplayAutoMode = this.mNightDisplayAutoMode;
        if (nightDisplayAutoMode != null) {
            nightDisplayAutoMode.onCustomEndTimeChanged(endTime);
        }
    }

    /* access modifiers changed from: private */
    public void onDisplayColorModeChanged(int mode) {
        if (mode != -1) {
            this.mNightDisplayTintController.cancelAnimator();
            this.mDisplayWhiteBalanceTintController.cancelAnimator();
            if (this.mNightDisplayTintController.isAvailable(getContext())) {
                this.mNightDisplayTintController.setUp(getContext(), DisplayTransformManager.needsLinearColorMatrix(mode));
                NightDisplayTintController nightDisplayTintController = this.mNightDisplayTintController;
                nightDisplayTintController.setMatrix(nightDisplayTintController.getColorTemperatureSetting());
            }
            if (this.mDisplayWhiteBalanceTintController.isAvailable(getContext())) {
                updateDisplayWhiteBalanceStatus();
            }
            ((DisplayTransformManager) getLocalService(DisplayTransformManager.class)).setColorMode(mode, this.mNightDisplayTintController.getMatrix());
        }
    }

    /* access modifiers changed from: private */
    public void onAccessibilityActivated() {
        onDisplayColorModeChanged(getColorModeInternal());
    }

    private boolean isAccessiblityDaltonizerEnabled() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "accessibility_display_daltonizer_enabled", 0, this.mCurrentUser) != 0;
    }

    private boolean isAccessiblityInversionEnabled() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "accessibility_display_inversion_enabled", 0, this.mCurrentUser) != 0;
    }

    private boolean isAccessibilityEnabled() {
        return isAccessiblityDaltonizerEnabled() || isAccessiblityInversionEnabled();
    }

    /* access modifiers changed from: private */
    public void onAccessibilityDaltonizerChanged() {
        int daltonizerMode;
        if (this.mCurrentUser != -10000) {
            if (isAccessiblityDaltonizerEnabled()) {
                daltonizerMode = Settings.Secure.getIntForUser(getContext().getContentResolver(), "accessibility_display_daltonizer", 12, this.mCurrentUser);
            } else {
                daltonizerMode = -1;
            }
            DisplayTransformManager dtm = (DisplayTransformManager) getLocalService(DisplayTransformManager.class);
            if (daltonizerMode == 0) {
                dtm.setColorMatrix(200, MATRIX_GRAYSCALE);
                dtm.setDaltonizerMode(-1);
                return;
            }
            dtm.setColorMatrix(200, (float[]) null);
            dtm.setDaltonizerMode(daltonizerMode);
        }
    }

    /* access modifiers changed from: private */
    public void onAccessibilityInversionChanged() {
        if (this.mCurrentUser != -10000) {
            ((DisplayTransformManager) getLocalService(DisplayTransformManager.class)).setColorMatrix(DisplayTransformManager.LEVEL_COLOR_MATRIX_INVERT_COLOR, isAccessiblityInversionEnabled() ? MATRIX_INVERT_COLOR : null);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyTint(final com.android.server.display.color.TintController r8, boolean r9) {
        /*
            r7 = this;
            r8.cancelAnimator()
            java.lang.Class<com.android.server.display.color.DisplayTransformManager> r0 = com.android.server.display.color.DisplayTransformManager.class
            java.lang.Object r0 = r7.getLocalService(r0)
            com.android.server.display.color.DisplayTransformManager r0 = (com.android.server.display.color.DisplayTransformManager) r0
            int r1 = r8.getLevel()
            float[] r1 = r0.getColorMatrix(r1)
            float[] r2 = r8.getMatrix()
            if (r9 == 0) goto L_0x0021
            int r3 = r8.getLevel()
            r0.setColorMatrix(r3, r2)
            goto L_0x0073
        L_0x0021:
            com.android.server.display.color.ColorDisplayService$ColorMatrixEvaluator r3 = COLOR_MATRIX_EVALUATOR
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            if (r1 != 0) goto L_0x002c
            float[] r6 = MATRIX_IDENTITY
            goto L_0x002d
        L_0x002c:
            r6 = r1
        L_0x002d:
            r4[r5] = r6
            r5 = 1
            r4[r5] = r2
            android.animation.ValueAnimator r3 = android.animation.ValueAnimator.ofObject(r3, r4)
            r8.setAnimator(r3)
            android.animation.ValueAnimator r3 = r8.getAnimator()
            r4 = 3000(0xbb8, double:1.482E-320)
            r3.setDuration(r4)
            android.animation.ValueAnimator r3 = r8.getAnimator()
            android.content.Context r4 = r7.getContext()
            r5 = 17563661(0x10c000d, float:2.5713975E-38)
            android.view.animation.Interpolator r4 = android.view.animation.AnimationUtils.loadInterpolator(r4, r5)
            r3.setInterpolator(r4)
            android.animation.ValueAnimator r3 = r8.getAnimator()
            com.android.server.display.color.-$$Lambda$ColorDisplayService$3e7BuPerYILI5JPZm17hU11tDtY r4 = new com.android.server.display.color.-$$Lambda$ColorDisplayService$3e7BuPerYILI5JPZm17hU11tDtY
            r4.<init>(r8)
            r3.addUpdateListener(r4)
            android.animation.ValueAnimator r3 = r8.getAnimator()
            com.android.server.display.color.ColorDisplayService$3 r4 = new com.android.server.display.color.ColorDisplayService$3
            r4.<init>(r0, r8, r2)
            r3.addListener(r4)
            android.animation.ValueAnimator r3 = r8.getAnimator()
            r3.start()
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.color.ColorDisplayService.applyTint(com.android.server.display.color.TintController, boolean):void");
    }

    @VisibleForTesting
    static LocalDateTime getDateTimeBefore(LocalTime localTime, LocalDateTime compareTime) {
        LocalDateTime ldt = LocalDateTime.of(compareTime.getYear(), compareTime.getMonth(), compareTime.getDayOfMonth(), localTime.getHour(), localTime.getMinute());
        return ldt.isAfter(compareTime) ? ldt.minusDays(1) : ldt;
    }

    @VisibleForTesting
    static LocalDateTime getDateTimeAfter(LocalTime localTime, LocalDateTime compareTime) {
        LocalDateTime ldt = LocalDateTime.of(compareTime.getYear(), compareTime.getMonth(), compareTime.getDayOfMonth(), localTime.getHour(), localTime.getMinute());
        return ldt.isBefore(compareTime) ? ldt.plusDays(1) : ldt;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateDisplayWhiteBalanceStatus() {
        boolean oldActivated = this.mDisplayWhiteBalanceTintController.isActivated();
        this.mDisplayWhiteBalanceTintController.setActivated(Boolean.valueOf(isDisplayWhiteBalanceSettingEnabled() && !this.mNightDisplayTintController.isActivated() && !isAccessibilityEnabled() && DisplayTransformManager.needsLinearColorMatrix()));
        boolean activated = this.mDisplayWhiteBalanceTintController.isActivated();
        DisplayWhiteBalanceListener displayWhiteBalanceListener = this.mDisplayWhiteBalanceListener;
        if (!(displayWhiteBalanceListener == null || oldActivated == activated)) {
            displayWhiteBalanceListener.onDisplayWhiteBalanceStatusChanged(activated);
        }
        if (!activated) {
            this.mHandler.sendEmptyMessage(5);
        }
    }

    /* access modifiers changed from: private */
    public boolean setDisplayWhiteBalanceSettingEnabled(boolean enabled) {
        if (this.mCurrentUser == -10000) {
            return false;
        }
        return Settings.Secure.putIntForUser(getContext().getContentResolver(), "display_white_balance_enabled", enabled, this.mCurrentUser);
    }

    /* access modifiers changed from: private */
    public boolean isDisplayWhiteBalanceSettingEnabled() {
        int i;
        if (this.mCurrentUser == -10000) {
            return false;
        }
        ContentResolver contentResolver = getContext().getContentResolver();
        if (getContext().getResources().getBoolean(17891414)) {
            i = 1;
        } else {
            i = 0;
        }
        if (Settings.Secure.getIntForUser(contentResolver, "display_white_balance_enabled", i, this.mCurrentUser) == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isDeviceColorManagedInternal() {
        return ((DisplayTransformManager) getLocalService(DisplayTransformManager.class)).isDeviceColorManaged();
    }

    /* access modifiers changed from: private */
    public int getTransformCapabilitiesInternal() {
        int availabilityFlags = 0;
        if (SurfaceControl.getProtectedContentSupport()) {
            availabilityFlags = 0 | 1;
        }
        Resources res = getContext().getResources();
        if (res.getBoolean(17891511)) {
            availabilityFlags |= 2;
        }
        if (res.getBoolean(17891512)) {
            return availabilityFlags | 4;
        }
        return availabilityFlags;
    }

    /* access modifiers changed from: private */
    public boolean setNightDisplayAutoModeInternal(int autoMode) {
        if (getNightDisplayAutoModeInternal() != autoMode) {
            Settings.Secure.putStringForUser(getContext().getContentResolver(), "night_display_last_activated_time", (String) null, this.mCurrentUser);
        }
        return Settings.Secure.putIntForUser(getContext().getContentResolver(), "night_display_auto_mode", autoMode, this.mCurrentUser);
    }

    /* access modifiers changed from: private */
    public int getNightDisplayAutoModeInternal() {
        int autoMode = getNightDisplayAutoModeRawInternal();
        if (autoMode == -1) {
            autoMode = getContext().getResources().getInteger(17694772);
        }
        if (autoMode == 0 || autoMode == 1 || autoMode == 2) {
            return autoMode;
        }
        Slog.e(TAG, "Invalid autoMode: " + autoMode);
        return 0;
    }

    /* access modifiers changed from: private */
    public int getNightDisplayAutoModeRawInternal() {
        if (this.mCurrentUser == -10000) {
            return -1;
        }
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "night_display_auto_mode", -1, this.mCurrentUser);
    }

    /* access modifiers changed from: private */
    public Time getNightDisplayCustomStartTimeInternal() {
        int startTimeValue = Settings.Secure.getIntForUser(getContext().getContentResolver(), "night_display_custom_start_time", -1, this.mCurrentUser);
        if (startTimeValue == -1) {
            startTimeValue = getContext().getResources().getInteger(17694774);
        }
        return new Time(LocalTime.ofSecondOfDay((long) (startTimeValue / 1000)));
    }

    /* access modifiers changed from: private */
    public boolean setNightDisplayCustomStartTimeInternal(Time startTime) {
        return Settings.Secure.putIntForUser(getContext().getContentResolver(), "night_display_custom_start_time", startTime.getLocalTime().toSecondOfDay() * 1000, this.mCurrentUser);
    }

    /* access modifiers changed from: private */
    public Time getNightDisplayCustomEndTimeInternal() {
        int endTimeValue = Settings.Secure.getIntForUser(getContext().getContentResolver(), "night_display_custom_end_time", -1, this.mCurrentUser);
        if (endTimeValue == -1) {
            endTimeValue = getContext().getResources().getInteger(17694773);
        }
        return new Time(LocalTime.ofSecondOfDay((long) (endTimeValue / 1000)));
    }

    /* access modifiers changed from: private */
    public boolean setNightDisplayCustomEndTimeInternal(Time endTime) {
        return Settings.Secure.putIntForUser(getContext().getContentResolver(), "night_display_custom_end_time", endTime.getLocalTime().toSecondOfDay() * 1000, this.mCurrentUser);
    }

    /* access modifiers changed from: private */
    public LocalDateTime getNightDisplayLastActivatedTimeSetting() {
        String lastActivatedTime = Settings.Secure.getStringForUser(getContext().getContentResolver(), "night_display_last_activated_time", getContext().getUserId());
        if (lastActivatedTime != null) {
            try {
                return LocalDateTime.parse(lastActivatedTime);
            } catch (DateTimeParseException e) {
                try {
                    return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(lastActivatedTime)), ZoneId.systemDefault());
                } catch (NumberFormatException | DateTimeException e2) {
                }
            }
        }
        return LocalDateTime.MIN;
    }

    /* access modifiers changed from: private */
    public boolean setAppSaturationLevelInternal(String packageName, int saturationLevel) {
        return this.mAppSaturationController.setSaturationLevel(packageName, this.mCurrentUser, saturationLevel);
    }

    /* access modifiers changed from: private */
    public void setColorModeInternal(int colorMode) {
        if (isColorModeAvailable(colorMode)) {
            Settings.System.putIntForUser(getContext().getContentResolver(), "display_color_mode", colorMode, this.mCurrentUser);
            return;
        }
        throw new IllegalArgumentException("Invalid colorMode: " + colorMode);
    }

    /* access modifiers changed from: private */
    public int getColorModeInternal() {
        int a11yColorMode;
        ContentResolver cr = getContext().getContentResolver();
        if (isAccessibilityEnabled() && (a11yColorMode = getContext().getResources().getInteger(17694728)) >= 0) {
            return a11yColorMode;
        }
        int colorMode = Settings.System.getIntForUser(cr, "display_color_mode", -1, this.mCurrentUser);
        if (colorMode == -1) {
            colorMode = getCurrentColorModeFromSystemProperties();
            Settings.System.putIntForUser(cr, "display_color_mode", colorMode, this.mCurrentUser);
        }
        if (isColorModeAvailable(colorMode)) {
            return colorMode;
        }
        if (colorMode == 1 && isColorModeAvailable(0)) {
            return 0;
        }
        if (colorMode == 2 && isColorModeAvailable(3)) {
            return 3;
        }
        if (colorMode != 3 || !isColorModeAvailable(2)) {
            return -1;
        }
        return 2;
    }

    private int getCurrentColorModeFromSystemProperties() {
        int displayColorSetting = SystemProperties.getInt("persist.sys.sf.native_mode", 0);
        if (displayColorSetting == 0) {
            if ("1.0".equals(SystemProperties.get("persist.sys.sf.color_saturation"))) {
                return 0;
            }
            return 1;
        } else if (displayColorSetting == 1) {
            return 2;
        } else {
            if (displayColorSetting == 2) {
                return 3;
            }
            if (displayColorSetting < 256 || displayColorSetting > 511) {
                return -1;
            }
            return displayColorSetting;
        }
    }

    private boolean isColorModeAvailable(int colorMode) {
        int[] availableColorModes = getContext().getResources().getIntArray(17235993);
        if (availableColorModes != null) {
            for (int mode : availableColorModes) {
                if (mode == colorMode) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void dumpInternal(PrintWriter pw) {
        pw.println("COLOR DISPLAY MANAGER dumpsys (color_display)");
        pw.println("Night display:");
        if (this.mNightDisplayTintController.isAvailable(getContext())) {
            pw.println("    Activated: " + this.mNightDisplayTintController.isActivated());
            pw.println("    Color temp: " + this.mNightDisplayTintController.getColorTemperature());
        } else {
            pw.println("    Not available");
        }
        pw.println("Global saturation:");
        if (this.mGlobalSaturationTintController.isAvailable(getContext())) {
            pw.println("    Activated: " + this.mGlobalSaturationTintController.isActivated());
        } else {
            pw.println("    Not available");
        }
        this.mAppSaturationController.dump(pw);
        pw.println("Display white balance:");
        if (this.mDisplayWhiteBalanceTintController.isAvailable(getContext())) {
            pw.println("    Activated: " + this.mDisplayWhiteBalanceTintController.isActivated());
            this.mDisplayWhiteBalanceTintController.dump(pw);
        } else {
            pw.println("    Not available");
        }
        pw.println("Color mode: " + getColorModeInternal());
    }

    private abstract class NightDisplayAutoMode {
        public abstract void onActivated(boolean z);

        public abstract void onStart();

        public abstract void onStop();

        private NightDisplayAutoMode() {
        }

        public void onCustomStartTimeChanged(LocalTime startTime) {
        }

        public void onCustomEndTimeChanged(LocalTime endTime) {
        }
    }

    private final class CustomNightDisplayAutoMode extends NightDisplayAutoMode implements AlarmManager.OnAlarmListener {
        private final AlarmManager mAlarmManager;
        private LocalTime mEndTime;
        private LocalDateTime mLastActivatedTime;
        private LocalTime mStartTime;
        private final BroadcastReceiver mTimeChangedReceiver;

        CustomNightDisplayAutoMode() {
            super();
            this.mAlarmManager = (AlarmManager) ColorDisplayService.this.getContext().getSystemService("alarm");
            this.mTimeChangedReceiver = new BroadcastReceiver(ColorDisplayService.this) {
                public void onReceive(Context context, Intent intent) {
                    CustomNightDisplayAutoMode.this.updateActivated();
                }
            };
        }

        /* access modifiers changed from: private */
        public void updateActivated() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = ColorDisplayService.getDateTimeBefore(this.mStartTime, now);
            LocalDateTime end = ColorDisplayService.getDateTimeAfter(this.mEndTime, start);
            boolean activate = now.isBefore(end);
            LocalDateTime localDateTime = this.mLastActivatedTime;
            if (localDateTime != null && localDateTime.isBefore(now) && this.mLastActivatedTime.isAfter(start) && (this.mLastActivatedTime.isAfter(end) || now.isBefore(end))) {
                activate = ColorDisplayService.this.mNightDisplayTintController.isActivatedSetting();
            }
            if (ColorDisplayService.this.mNightDisplayTintController.isActivatedStateNotSet() || ColorDisplayService.this.mNightDisplayTintController.isActivated() != activate) {
                ColorDisplayService.this.mNightDisplayTintController.setActivated(Boolean.valueOf(activate));
            }
            updateNextAlarm(Boolean.valueOf(ColorDisplayService.this.mNightDisplayTintController.isActivated()), now);
        }

        private void updateNextAlarm(Boolean activated, LocalDateTime now) {
            LocalDateTime next;
            if (activated != null) {
                if (activated.booleanValue()) {
                    next = ColorDisplayService.getDateTimeAfter(this.mEndTime, now);
                } else {
                    next = ColorDisplayService.getDateTimeAfter(this.mStartTime, now);
                }
                this.mAlarmManager.setExact(1, next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), ColorDisplayService.TAG, this, (Handler) null);
            }
        }

        public void onStart() {
            IntentFilter intentFilter = new IntentFilter("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            ColorDisplayService.this.getContext().registerReceiver(this.mTimeChangedReceiver, intentFilter);
            this.mStartTime = ColorDisplayService.this.getNightDisplayCustomStartTimeInternal().getLocalTime();
            this.mEndTime = ColorDisplayService.this.getNightDisplayCustomEndTimeInternal().getLocalTime();
            this.mLastActivatedTime = ColorDisplayService.this.getNightDisplayLastActivatedTimeSetting();
            updateActivated();
        }

        public void onStop() {
            ColorDisplayService.this.getContext().unregisterReceiver(this.mTimeChangedReceiver);
            this.mAlarmManager.cancel(this);
            this.mLastActivatedTime = null;
        }

        public void onActivated(boolean activated) {
            this.mLastActivatedTime = ColorDisplayService.this.getNightDisplayLastActivatedTimeSetting();
            updateNextAlarm(Boolean.valueOf(activated), LocalDateTime.now());
        }

        public void onCustomStartTimeChanged(LocalTime startTime) {
            this.mStartTime = startTime;
            this.mLastActivatedTime = null;
            updateActivated();
        }

        public void onCustomEndTimeChanged(LocalTime endTime) {
            this.mEndTime = endTime;
            this.mLastActivatedTime = null;
            updateActivated();
        }

        public void onAlarm() {
            Slog.d(ColorDisplayService.TAG, "onAlarm");
            updateActivated();
        }
    }

    private final class TwilightNightDisplayAutoMode extends NightDisplayAutoMode implements TwilightListener {
        private LocalDateTime mLastActivatedTime;
        private final TwilightManager mTwilightManager;

        TwilightNightDisplayAutoMode() {
            super();
            this.mTwilightManager = (TwilightManager) ColorDisplayService.this.getLocalService(TwilightManager.class);
        }

        private void updateActivated(TwilightState state) {
            if (state != null) {
                boolean activate = state.isNight();
                if (this.mLastActivatedTime != null) {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime sunrise = state.sunrise();
                    LocalDateTime sunset = state.sunset();
                    if (this.mLastActivatedTime.isBefore(now) && (this.mLastActivatedTime.isBefore(sunrise) ^ this.mLastActivatedTime.isBefore(sunset))) {
                        activate = ColorDisplayService.this.mNightDisplayTintController.isActivatedSetting();
                    }
                }
                if (ColorDisplayService.this.mNightDisplayTintController.isActivatedStateNotSet() || ColorDisplayService.this.mNightDisplayTintController.isActivated() != activate) {
                    ColorDisplayService.this.mNightDisplayTintController.setActivated(Boolean.valueOf(activate));
                }
            }
        }

        public void onActivated(boolean activated) {
            this.mLastActivatedTime = ColorDisplayService.this.getNightDisplayLastActivatedTimeSetting();
        }

        public void onStart() {
            this.mTwilightManager.registerListener(this, ColorDisplayService.this.mHandler);
            this.mLastActivatedTime = ColorDisplayService.this.getNightDisplayLastActivatedTimeSetting();
            updateActivated(this.mTwilightManager.getLastTwilightState());
        }

        public void onStop() {
            this.mTwilightManager.unregisterListener(this);
            this.mLastActivatedTime = null;
        }

        public void onTwilightStateChanged(TwilightState state) {
            StringBuilder sb = new StringBuilder();
            sb.append("onTwilightStateChanged: isNight=");
            sb.append(state == null ? null : Boolean.valueOf(state.isNight()));
            Slog.d(ColorDisplayService.TAG, sb.toString());
            updateActivated(state);
        }
    }

    private static class ColorMatrixEvaluator implements TypeEvaluator<float[]> {
        private final float[] mResultMatrix;

        private ColorMatrixEvaluator() {
            this.mResultMatrix = new float[16];
        }

        public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
            int i = 0;
            while (true) {
                float[] fArr = this.mResultMatrix;
                if (i >= fArr.length) {
                    return fArr;
                }
                fArr[i] = MathUtils.lerp(startValue[i], endValue[i], fraction);
                i++;
            }
        }
    }

    private final class NightDisplayTintController extends TintController {
        private Integer mColorTemp;
        private final float[] mColorTempCoefficients;
        private Boolean mIsAvailable;
        private final float[] mMatrix;

        private NightDisplayTintController() {
            this.mMatrix = new float[16];
            this.mColorTempCoefficients = new float[9];
        }

        public void setUp(Context context, boolean needsLinear) {
            int i;
            Resources resources = context.getResources();
            if (needsLinear) {
                i = 17236046;
            } else {
                i = 17236047;
            }
            String[] coefficients = resources.getStringArray(i);
            int i2 = 0;
            while (i2 < 9 && i2 < coefficients.length) {
                this.mColorTempCoefficients[i2] = Float.parseFloat(coefficients[i2]);
                i2++;
            }
        }

        public void setMatrix(int cct) {
            float[] fArr = this.mMatrix;
            if (fArr.length != 16) {
                Slog.d(ColorDisplayService.TAG, "The display transformation matrix must be 4x4");
                return;
            }
            Matrix.setIdentityM(fArr, 0);
            float squareTemperature = (float) (cct * cct);
            float[] fArr2 = this.mColorTempCoefficients;
            float red = (fArr2[0] * squareTemperature) + (((float) cct) * fArr2[1]) + fArr2[2];
            float green = (fArr2[3] * squareTemperature) + (((float) cct) * fArr2[4]) + fArr2[5];
            float blue = (fArr2[6] * squareTemperature) + (((float) cct) * fArr2[7]) + fArr2[8];
            float[] fArr3 = this.mMatrix;
            fArr3[0] = red;
            fArr3[5] = green;
            fArr3[10] = blue;
        }

        public float[] getMatrix() {
            return isActivated() ? this.mMatrix : ColorDisplayService.MATRIX_IDENTITY;
        }

        public void setActivated(Boolean activated) {
            if (activated == null) {
                super.setActivated((Boolean) null);
                return;
            }
            boolean activationStateChanged = activated.booleanValue() != isActivated();
            if (!isActivatedStateNotSet() && activationStateChanged) {
                Settings.Secure.putStringForUser(ColorDisplayService.this.getContext().getContentResolver(), "night_display_last_activated_time", LocalDateTime.now().toString(), ColorDisplayService.this.mCurrentUser);
            }
            if (isActivatedStateNotSet() || activationStateChanged) {
                super.setActivated(activated);
                if (isActivatedSetting() != activated.booleanValue()) {
                    Settings.Secure.putIntForUser(ColorDisplayService.this.getContext().getContentResolver(), "night_display_activated", activated.booleanValue() ? 1 : 0, ColorDisplayService.this.mCurrentUser);
                }
                onActivated(activated.booleanValue());
            }
        }

        public int getLevel() {
            return 100;
        }

        public boolean isAvailable(Context context) {
            if (this.mIsAvailable == null) {
                this.mIsAvailable = Boolean.valueOf(ColorDisplayManager.isNightDisplayAvailable(context));
            }
            return this.mIsAvailable.booleanValue();
        }

        private void onActivated(boolean activated) {
            Slog.i(ColorDisplayService.TAG, activated ? "Turning on night display" : "Turning off night display");
            if (ColorDisplayService.this.mNightDisplayAutoMode != null) {
                ColorDisplayService.this.mNightDisplayAutoMode.onActivated(activated);
            }
            if (ColorDisplayService.this.mDisplayWhiteBalanceTintController.isAvailable(ColorDisplayService.this.getContext())) {
                ColorDisplayService.this.updateDisplayWhiteBalanceStatus();
            }
            ColorDisplayService.this.mHandler.sendEmptyMessage(3);
        }

        /* access modifiers changed from: package-private */
        public int getColorTemperature() {
            Integer num = this.mColorTemp;
            if (num != null) {
                return clampNightDisplayColorTemperature(num.intValue());
            }
            return getColorTemperatureSetting();
        }

        /* access modifiers changed from: package-private */
        public boolean setColorTemperature(int temperature) {
            this.mColorTemp = Integer.valueOf(temperature);
            boolean success = Settings.Secure.putIntForUser(ColorDisplayService.this.getContext().getContentResolver(), "night_display_color_temperature", temperature, ColorDisplayService.this.mCurrentUser);
            onColorTemperatureChanged(temperature);
            return success;
        }

        /* access modifiers changed from: package-private */
        public void onColorTemperatureChanged(int temperature) {
            setMatrix(temperature);
            ColorDisplayService.this.mHandler.sendEmptyMessage(2);
        }

        /* access modifiers changed from: package-private */
        public boolean isActivatedSetting() {
            if (ColorDisplayService.this.mCurrentUser != -10000 && Settings.Secure.getIntForUser(ColorDisplayService.this.getContext().getContentResolver(), "night_display_activated", 0, ColorDisplayService.this.mCurrentUser) == 1) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public int getColorTemperatureSetting() {
            if (ColorDisplayService.this.mCurrentUser == -10000) {
                return -1;
            }
            return clampNightDisplayColorTemperature(Settings.Secure.getIntForUser(ColorDisplayService.this.getContext().getContentResolver(), "night_display_color_temperature", -1, ColorDisplayService.this.mCurrentUser));
        }

        private int clampNightDisplayColorTemperature(int colorTemperature) {
            if (colorTemperature == -1) {
                colorTemperature = ColorDisplayService.this.getContext().getResources().getInteger(17694860);
            }
            int minimumTemperature = ColorDisplayManager.getMinimumColorTemperature(ColorDisplayService.this.getContext());
            int maximumTemperature = ColorDisplayManager.getMaximumColorTemperature(ColorDisplayService.this.getContext());
            if (colorTemperature < minimumTemperature) {
                return minimumTemperature;
            }
            if (colorTemperature > maximumTemperature) {
                return maximumTemperature;
            }
            return colorTemperature;
        }
    }

    public final class ColorDisplayServiceInternal {
        public ColorDisplayServiceInternal() {
        }

        public boolean setDisplayWhiteBalanceColorTemperature(int cct) {
            ColorDisplayService.this.mDisplayWhiteBalanceTintController.setMatrix(cct);
            if (!ColorDisplayService.this.mDisplayWhiteBalanceTintController.isActivated()) {
                return false;
            }
            ColorDisplayService.this.mHandler.sendEmptyMessage(5);
            return true;
        }

        public boolean resetDisplayWhiteBalanceColorTemperature() {
            return setDisplayWhiteBalanceColorTemperature(ColorDisplayService.this.getContext().getResources().getInteger(17694788));
        }

        public boolean setDisplayWhiteBalanceListener(DisplayWhiteBalanceListener listener) {
            DisplayWhiteBalanceListener unused = ColorDisplayService.this.mDisplayWhiteBalanceListener = listener;
            return ColorDisplayService.this.mDisplayWhiteBalanceTintController.isActivated();
        }

        public boolean isDisplayWhiteBalanceEnabled() {
            return ColorDisplayService.this.isDisplayWhiteBalanceSettingEnabled();
        }

        public boolean attachColorTransformController(String packageName, int userId, WeakReference<ColorTransformController> controller) {
            return ColorDisplayService.this.mAppSaturationController.addColorTransformController(packageName, userId, controller);
        }
    }

    private final class TintHandler extends Handler {
        private TintHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                ColorDisplayService.this.onUserChanged(msg.arg1);
            } else if (i == 1) {
                ColorDisplayService.this.setUp();
            } else if (i == 2) {
                ColorDisplayService colorDisplayService = ColorDisplayService.this;
                colorDisplayService.applyTint(colorDisplayService.mNightDisplayTintController, true);
            } else if (i == 3) {
                ColorDisplayService colorDisplayService2 = ColorDisplayService.this;
                colorDisplayService2.applyTint(colorDisplayService2.mNightDisplayTintController, false);
            } else if (i == 4) {
                ColorDisplayService.this.mGlobalSaturationTintController.setMatrix(msg.arg1);
                ColorDisplayService colorDisplayService3 = ColorDisplayService.this;
                colorDisplayService3.applyTint(colorDisplayService3.mGlobalSaturationTintController, false);
            } else if (i == 5) {
                ColorDisplayService colorDisplayService4 = ColorDisplayService.this;
                colorDisplayService4.applyTint(colorDisplayService4.mDisplayWhiteBalanceTintController, false);
            }
        }
    }

    @VisibleForTesting
    final class BinderService extends IColorDisplayManager.Stub {
        BinderService() {
        }

        public void setColorMode(int colorMode) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set display color mode");
            long token = Binder.clearCallingIdentity();
            try {
                ColorDisplayService.this.setColorModeInternal(colorMode);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int getColorMode() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.getColorModeInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean isDeviceColorManaged() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.isDeviceColorManagedInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setSaturationLevel(int level) {
            boolean hasLegacyPermission = false;
            boolean hasTransformsPermission = ColorDisplayService.this.getContext().checkCallingPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS") == 0;
            if (ColorDisplayService.this.getContext().checkCallingPermission("android.permission.CONTROL_DISPLAY_SATURATION") == 0) {
                hasLegacyPermission = true;
            }
            if (hasTransformsPermission || hasLegacyPermission) {
                long token = Binder.clearCallingIdentity();
                try {
                    Message message = ColorDisplayService.this.mHandler.obtainMessage(4);
                    message.arg1 = level;
                    ColorDisplayService.this.mHandler.sendMessage(message);
                    return true;
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new SecurityException("Permission required to set display saturation level");
            }
        }

        public boolean isSaturationActivated() {
            ColorDisplayService.this.getContext().enforceCallingPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to get display saturation level");
            long token = Binder.clearCallingIdentity();
            try {
                return !ColorDisplayService.this.mGlobalSaturationTintController.isActivatedStateNotSet() && ColorDisplayService.this.mGlobalSaturationTintController.isActivated();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setAppSaturationLevel(String packageName, int level) {
            ColorDisplayService.this.getContext().enforceCallingPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set display saturation level");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.setAppSaturationLevelInternal(packageName, level);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int getTransformCapabilities() {
            ColorDisplayService.this.getContext().enforceCallingPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to query transform capabilities");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.getTransformCapabilitiesInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setNightDisplayActivated(boolean activated) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set night display activated");
            long token = Binder.clearCallingIdentity();
            try {
                ColorDisplayService.this.mNightDisplayTintController.setActivated(Boolean.valueOf(activated));
                return true;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean isNightDisplayActivated() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.mNightDisplayTintController.isActivated();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setNightDisplayColorTemperature(int temperature) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set night display temperature");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.mNightDisplayTintController.setColorTemperature(temperature);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int getNightDisplayColorTemperature() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.mNightDisplayTintController.getColorTemperature();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setNightDisplayAutoMode(int autoMode) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set night display auto mode");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.setNightDisplayAutoModeInternal(autoMode);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int getNightDisplayAutoMode() {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to get night display auto mode");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.getNightDisplayAutoModeInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int getNightDisplayAutoModeRaw() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.getNightDisplayAutoModeRawInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setNightDisplayCustomStartTime(Time startTime) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set night display custom start time");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.setNightDisplayCustomStartTimeInternal(startTime);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public Time getNightDisplayCustomStartTime() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.getNightDisplayCustomStartTimeInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setNightDisplayCustomEndTime(Time endTime) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set night display custom end time");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.setNightDisplayCustomEndTimeInternal(endTime);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public Time getNightDisplayCustomEndTime() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.getNightDisplayCustomEndTimeInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean setDisplayWhiteBalanceEnabled(boolean enabled) {
            ColorDisplayService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_COLOR_TRANSFORMS", "Permission required to set night display activated");
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.setDisplayWhiteBalanceSettingEnabled(enabled);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean isDisplayWhiteBalanceEnabled() {
            long token = Binder.clearCallingIdentity();
            try {
                return ColorDisplayService.this.isDisplayWhiteBalanceSettingEnabled();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(ColorDisplayService.this.getContext(), ColorDisplayService.TAG, pw)) {
                long token = Binder.clearCallingIdentity();
                try {
                    ColorDisplayService.this.dumpInternal(pw);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }
    }
}
