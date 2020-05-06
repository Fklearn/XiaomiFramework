package com.android.server.wm;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Surface;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.LocalServices;
import com.android.server.UiModeManagerService;
import com.android.server.UiThread;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.policy.WindowOrientationListener;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DisplayRotation {
    private static final String ACTION_WIFI_DISPLAY_VIDEO = "org.codeaurora.intent.action.WIFI_DISPLAY_VIDEO";
    static final int FIXED_TO_USER_ROTATION_DEFAULT = 0;
    static final int FIXED_TO_USER_ROTATION_DISABLED = 1;
    static final int FIXED_TO_USER_ROTATION_ENABLED = 2;
    private static final String TAG = "WindowManager";
    public final boolean isDefaultDisplay;
    private int mAllowAllRotations;
    private final int mCarDockRotation;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentAppOrientation;
    private boolean mDefaultFixedToUserRotation;
    private int mDemoHdmiRotation;
    private boolean mDemoHdmiRotationLock;
    private int mDemoRotation;
    private boolean mDemoRotationLock;
    private final int mDeskDockRotation;
    private final DisplayContent mDisplayContent;
    private final DisplayPolicy mDisplayPolicy;
    private final DisplayWindowSettings mDisplayWindowSettings;
    private int mFixedToUserRotation;
    @VisibleForTesting
    int mLandscapeRotation;
    private final int mLidOpenRotation;
    private final Object mLock;
    private OrientationListener mOrientationListener;
    @VisibleForTesting
    int mPortraitRotation;
    BroadcastReceiver mReceiver;
    @VisibleForTesting
    int mSeascapeRotation;
    /* access modifiers changed from: private */
    public final WindowManagerService mService;
    private SettingsObserver mSettingsObserver;
    private int mShowRotationSuggestions;
    private StatusBarManagerInternal mStatusBarManagerInternal;
    private final boolean mSupportAutoRotation;
    private final int mUndockedHdmiRotation;
    @VisibleForTesting
    int mUpsideDownRotation;
    private int mUserRotation;
    private int mUserRotationMode;
    /* access modifiers changed from: private */
    public boolean mWifiDisplayConnected;
    /* access modifiers changed from: private */
    public int mWifiDisplayRotation;

    @VisibleForTesting
    interface ContentObserverRegister {
        void registerContentObserver(Uri uri, boolean z, ContentObserver contentObserver, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface FixedToUserRotation {
    }

    DisplayRotation(WindowManagerService service, DisplayContent displayContent) {
        this(service, displayContent, displayContent.getDisplayPolicy(), service.mDisplayWindowSettings, service.mContext, service.getWindowManagerLock());
    }

    @VisibleForTesting
    DisplayRotation(WindowManagerService service, DisplayContent displayContent, DisplayPolicy displayPolicy, DisplayWindowSettings displayWindowSettings, Context context, Object lock) {
        this.mCurrentAppOrientation = -1;
        this.mAllowAllRotations = -1;
        this.mUserRotationMode = 0;
        this.mUserRotation = 0;
        this.mFixedToUserRotation = 0;
        this.mWifiDisplayConnected = false;
        this.mWifiDisplayRotation = -1;
        this.mService = service;
        this.mDisplayContent = displayContent;
        this.mDisplayPolicy = displayPolicy;
        this.mDisplayWindowSettings = displayWindowSettings;
        this.mContext = context;
        this.mLock = lock;
        this.isDefaultDisplay = displayContent.isDefaultDisplay;
        this.mSupportAutoRotation = this.mContext.getResources().getBoolean(17891535);
        this.mLidOpenRotation = readRotation(17694819);
        this.mCarDockRotation = readRotation(17694761);
        this.mDeskDockRotation = readRotation(17694785);
        this.mUndockedHdmiRotation = readRotation(17694907);
        if (this.isDefaultDisplay) {
            Handler uiHandler = UiThread.getHandler();
            this.mOrientationListener = new OrientationListener(this.mContext, uiHandler);
            this.mOrientationListener.setCurrentRotation(displayContent.getRotation());
            this.mSettingsObserver = new SettingsObserver(uiHandler);
            this.mSettingsObserver.observe();
        }
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(DisplayRotation.ACTION_WIFI_DISPLAY_VIDEO)) {
                    if (intent.getIntExtra("state", 0) == 1) {
                        boolean unused = DisplayRotation.this.mWifiDisplayConnected = true;
                    } else {
                        boolean unused2 = DisplayRotation.this.mWifiDisplayConnected = false;
                    }
                    int rotation = intent.getIntExtra("wfd_UIBC_rot", -1);
                    if (rotation == 0) {
                        int unused3 = DisplayRotation.this.mWifiDisplayRotation = 0;
                    } else if (rotation == 1) {
                        int unused4 = DisplayRotation.this.mWifiDisplayRotation = 1;
                    } else if (rotation == 2) {
                        int unused5 = DisplayRotation.this.mWifiDisplayRotation = 2;
                    } else if (rotation != 3) {
                        int unused6 = DisplayRotation.this.mWifiDisplayRotation = -1;
                    } else {
                        int unused7 = DisplayRotation.this.mWifiDisplayRotation = 3;
                    }
                    DisplayRotation.this.mService.updateRotation(true, false);
                }
            }
        };
        if (this.mService.mH != null) {
            this.mService.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$DisplayRotation$vsASfZaGIZKuCu0vbQD7k4BTKM.INSTANCE, this));
        }
    }

    /* access modifiers changed from: private */
    public void registerReceiver() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter(ACTION_WIFI_DISPLAY_VIDEO), (String) null, UiThread.getHandler());
    }

    /* access modifiers changed from: private */
    public void unregisterReceiver() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: package-private */
    public void onDisplayRemoved() {
        if (this.mService.mH != null) {
            this.mService.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$DisplayRotation$PNlLUUJqoRNJdnA6zk3rUjPQzJQ.INSTANCE, this));
        }
    }

    private int readRotation(int resID) {
        try {
            int rotation = this.mContext.getResources().getInteger(resID);
            if (rotation == 0) {
                return 0;
            }
            if (rotation == 90) {
                return 1;
            }
            if (rotation == 180) {
                return 2;
            }
            if (rotation != 270) {
                return -1;
            }
            return 3;
        } catch (Resources.NotFoundException e) {
            return -1;
        }
    }

    /* access modifiers changed from: package-private */
    public void configure(int width, int height, int shortSizeDp, int longSizeDp) {
        Resources res = this.mContext.getResources();
        boolean z = true;
        if (width > height) {
            this.mLandscapeRotation = 0;
            this.mSeascapeRotation = 2;
            if (res.getBoolean(17891507)) {
                this.mPortraitRotation = 1;
                this.mUpsideDownRotation = 3;
            } else {
                this.mPortraitRotation = 3;
                this.mUpsideDownRotation = 1;
            }
        } else {
            this.mPortraitRotation = 0;
            this.mUpsideDownRotation = 2;
            if (res.getBoolean(17891507)) {
                this.mLandscapeRotation = 3;
                this.mSeascapeRotation = 1;
            } else {
                this.mLandscapeRotation = 1;
                this.mSeascapeRotation = 3;
            }
        }
        if ("portrait".equals(SystemProperties.get("persist.demo.hdmirotation"))) {
            this.mDemoHdmiRotation = this.mPortraitRotation;
        } else {
            this.mDemoHdmiRotation = this.mLandscapeRotation;
        }
        this.mDemoHdmiRotationLock = SystemProperties.getBoolean("persist.demo.hdmirotationlock", false);
        if ("portrait".equals(SystemProperties.get("persist.demo.remoterotation"))) {
            this.mDemoRotation = this.mPortraitRotation;
        } else {
            this.mDemoRotation = this.mLandscapeRotation;
        }
        this.mDemoRotationLock = SystemProperties.getBoolean("persist.demo.rotationlock", false);
        boolean isCar = this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
        boolean isTv = this.mContext.getPackageManager().hasSystemFeature("android.software.leanback");
        boolean forceDesktopMode = this.mService.mForceDesktopModeOnExternalDisplays && !this.isDefaultDisplay;
        if ((!isCar && !isTv && !this.mService.mIsPc && !forceDesktopMode) || "true".equals(SystemProperties.get("config.override_forced_orient"))) {
            z = false;
        }
        this.mDefaultFixedToUserRotation = z;
    }

    /* access modifiers changed from: package-private */
    public void setRotation(int rotation) {
        OrientationListener orientationListener = this.mOrientationListener;
        if (orientationListener != null) {
            orientationListener.setCurrentRotation(rotation);
        }
    }

    /* access modifiers changed from: package-private */
    public void setCurrentOrientation(int newOrientation) {
        if (newOrientation != this.mCurrentAppOrientation) {
            this.mCurrentAppOrientation = newOrientation;
            if (this.isDefaultDisplay) {
                updateOrientationListenerLw();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void restoreSettings(int userRotationMode, int userRotation, int fixedToUserRotation) {
        this.mFixedToUserRotation = fixedToUserRotation;
        if (!this.isDefaultDisplay) {
            if (!(userRotationMode == 0 || userRotationMode == 1)) {
                Slog.w("WindowManager", "Trying to restore an invalid user rotation mode " + userRotationMode + " for " + this.mDisplayContent);
                userRotationMode = 0;
            }
            if (userRotation < 0 || userRotation > 3) {
                Slog.w("WindowManager", "Trying to restore an invalid user rotation " + userRotation + " for " + this.mDisplayContent);
                userRotation = 0;
            }
            this.mUserRotationMode = userRotationMode;
            this.mUserRotation = userRotation;
        }
    }

    /* access modifiers changed from: package-private */
    public void setFixedToUserRotation(int fixedToUserRotation) {
        if (this.mFixedToUserRotation != fixedToUserRotation) {
            this.mFixedToUserRotation = fixedToUserRotation;
            this.mDisplayWindowSettings.setFixedToUserRotation(this.mDisplayContent, fixedToUserRotation);
            this.mService.updateRotation(true, false);
        }
    }

    private void setUserRotation(int userRotationMode, int userRotation) {
        int accelerometerRotation = 0;
        if (this.isDefaultDisplay) {
            ContentResolver res = this.mContext.getContentResolver();
            if (userRotationMode != 1) {
                accelerometerRotation = 1;
            }
            Settings.System.putIntForUser(res, "accelerometer_rotation", accelerometerRotation, -2);
            Settings.System.putIntForUser(res, "user_rotation", userRotation, -2);
            return;
        }
        boolean changed = false;
        if (this.mUserRotationMode != userRotationMode) {
            this.mUserRotationMode = userRotationMode;
            changed = true;
        }
        if (this.mUserRotation != userRotation) {
            this.mUserRotation = userRotation;
            changed = true;
        }
        this.mDisplayWindowSettings.setUserRotation(this.mDisplayContent, userRotationMode, userRotation);
        if (changed) {
            this.mService.updateRotation(true, false);
        }
    }

    /* access modifiers changed from: package-private */
    public void freezeRotation(int rotation) {
        if (this.mAllowAllRotations != 1 && rotation == 2) {
            rotation = 0;
        }
        setUserRotation(1, rotation == -1 ? this.mDisplayContent.getRotation() : rotation);
    }

    /* access modifiers changed from: package-private */
    public void thawRotation() {
        setUserRotation(0, this.mUserRotation);
    }

    /* access modifiers changed from: package-private */
    public boolean isRotationFrozen() {
        if (!this.isDefaultDisplay) {
            if (this.mUserRotationMode == 1) {
                return true;
            }
            return false;
        } else if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "accelerometer_rotation", 0, -2) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFixedToUserRotation() {
        int i = this.mFixedToUserRotation;
        if (i == 1) {
            return false;
        }
        if (i != 2) {
            return this.mDefaultFixedToUserRotation;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean respectAppRequestedOrientation() {
        return !isFixedToUserRotation();
    }

    public int getLandscapeRotation() {
        return this.mLandscapeRotation;
    }

    public int getSeascapeRotation() {
        return this.mSeascapeRotation;
    }

    public int getPortraitRotation() {
        return this.mPortraitRotation;
    }

    public int getUpsideDownRotation() {
        return this.mUpsideDownRotation;
    }

    public int getCurrentAppOrientation() {
        return this.mCurrentAppOrientation;
    }

    public DisplayPolicy getDisplayPolicy() {
        return this.mDisplayPolicy;
    }

    public WindowOrientationListener getOrientationListener() {
        return this.mOrientationListener;
    }

    public int getUserRotation() {
        return this.mUserRotation;
    }

    public int getUserRotationMode() {
        return this.mUserRotationMode;
    }

    public void updateOrientationListener() {
        synchronized (this.mLock) {
            updateOrientationListenerLw();
        }
    }

    private void updateOrientationListenerLw() {
        OrientationListener orientationListener = this.mOrientationListener;
        if (orientationListener != null && orientationListener.canDetectOrientation()) {
            boolean screenOnEarly = this.mDisplayPolicy.isScreenOnEarly();
            boolean awake = this.mDisplayPolicy.isAwake();
            boolean keyguardDrawComplete = this.mDisplayPolicy.isKeyguardDrawComplete();
            boolean windowManagerDrawComplete = this.mDisplayPolicy.isWindowManagerDrawComplete();
            boolean disable = true;
            if (screenOnEarly && awake && keyguardDrawComplete && windowManagerDrawComplete && needSensorRunning()) {
                disable = false;
                if (!this.mOrientationListener.mEnabled) {
                    this.mOrientationListener.enable(true);
                }
            }
            if (disable && this.mOrientationListener.mEnabled) {
                this.mOrientationListener.disable();
            }
        }
    }

    private boolean needSensorRunning() {
        int i;
        if (isFixedToUserRotation()) {
            return false;
        }
        if (this.mSupportAutoRotation && ((i = this.mCurrentAppOrientation) == 4 || i == 10 || i == 7 || i == 6)) {
            return true;
        }
        int dockMode = this.mDisplayPolicy.getDockMode();
        if ((this.mDisplayPolicy.isCarDockEnablesAccelerometer() && dockMode == 2) || (this.mDisplayPolicy.isDeskDockEnablesAccelerometer() && (dockMode == 1 || dockMode == 3 || dockMode == 4))) {
            return true;
        }
        if (this.mUserRotationMode != 1) {
            return this.mSupportAutoRotation;
        }
        if (!this.mSupportAutoRotation || this.mShowRotationSuggestions != 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public int rotationForOrientation(int orientation, int lastRotation) {
        int sensorRotation;
        int preferredRotation;
        int i = orientation;
        int i2 = lastRotation;
        if (isFixedToUserRotation()) {
            return this.mUserRotation;
        }
        OrientationListener orientationListener = this.mOrientationListener;
        if (orientationListener != null) {
            sensorRotation = orientationListener.getProposedRotation();
        } else {
            sensorRotation = -1;
        }
        if (sensorRotation < 0) {
            sensorRotation = lastRotation;
        }
        int lidState = this.mDisplayPolicy.getLidState();
        int dockMode = this.mDisplayPolicy.getDockMode();
        boolean hdmiPlugged = this.mDisplayPolicy.isHdmiPlugged();
        boolean carDockEnablesAccelerometer = this.mDisplayPolicy.isCarDockEnablesAccelerometer();
        boolean deskDockEnablesAccelerometer = this.mDisplayPolicy.isDeskDockEnablesAccelerometer();
        if (!this.isDefaultDisplay) {
            preferredRotation = this.mUserRotation;
        } else if (lidState == 1 && this.mLidOpenRotation >= 0) {
            preferredRotation = this.mLidOpenRotation;
        } else if (dockMode == 2 && (carDockEnablesAccelerometer || this.mCarDockRotation >= 0)) {
            preferredRotation = carDockEnablesAccelerometer ? sensorRotation : this.mCarDockRotation;
        } else if ((dockMode == 1 || dockMode == 3 || dockMode == 4) && (deskDockEnablesAccelerometer || this.mDeskDockRotation >= 0)) {
            preferredRotation = deskDockEnablesAccelerometer ? sensorRotation : this.mDeskDockRotation;
        } else if ((hdmiPlugged || this.mWifiDisplayConnected) && this.mDemoHdmiRotationLock) {
            preferredRotation = this.mDemoHdmiRotation;
        } else if (this.mWifiDisplayConnected && this.mWifiDisplayRotation > -1) {
            preferredRotation = this.mWifiDisplayRotation;
        } else if (hdmiPlugged && dockMode == 0 && this.mUndockedHdmiRotation >= 0) {
            preferredRotation = this.mUndockedHdmiRotation;
        } else if (this.mDemoRotationLock) {
            preferredRotation = this.mDemoRotation;
        } else if (this.mDisplayPolicy.isPersistentVrModeEnabled()) {
            preferredRotation = this.mPortraitRotation;
        } else if (i == 14) {
            preferredRotation = lastRotation;
        } else if (!this.mSupportAutoRotation) {
            preferredRotation = -1;
        } else if ((this.mUserRotationMode == 0 && (i == 2 || i == -1 || i == 11 || i == 12 || i == 13)) || i == 4 || i == 10 || i == 6 || i == 7) {
            if (this.mAllowAllRotations < 0) {
                this.mAllowAllRotations = this.mContext.getResources().getBoolean(17891340) ? 1 : 0;
            }
            preferredRotation = (sensorRotation != 2 || this.mAllowAllRotations == 1 || i == 10 || i == 13) ? sensorRotation : lastRotation;
        } else {
            preferredRotation = (this.mUserRotationMode != 1 || i == 5) ? -1 : this.mUserRotation;
        }
        if (i != 0) {
            if (i != 1) {
                if (i != 11) {
                    if (i != 12) {
                        switch (i) {
                            case 6:
                                break;
                            case 7:
                                break;
                            case 8:
                                if (isLandscapeOrSeascape(preferredRotation)) {
                                    return preferredRotation;
                                }
                                return this.mSeascapeRotation;
                            case 9:
                                if (isAnyPortrait(preferredRotation)) {
                                    return preferredRotation;
                                }
                                return this.mUpsideDownRotation;
                            default:
                                if (preferredRotation >= 0) {
                                    return preferredRotation;
                                }
                                return 0;
                        }
                    }
                    if (isAnyPortrait(preferredRotation)) {
                        return preferredRotation;
                    }
                    if (isAnyPortrait(i2)) {
                        return i2;
                    }
                    return this.mPortraitRotation;
                }
                if (isLandscapeOrSeascape(preferredRotation)) {
                    return preferredRotation;
                }
                if (isLandscapeOrSeascape(i2)) {
                    return i2;
                }
                return this.mLandscapeRotation;
            } else if (isAnyPortrait(preferredRotation)) {
                return preferredRotation;
            } else {
                return this.mPortraitRotation;
            }
        } else if (isLandscapeOrSeascape(preferredRotation)) {
            return preferredRotation;
        } else {
            return this.mLandscapeRotation;
        }
    }

    private boolean isLandscapeOrSeascape(int rotation) {
        return rotation == this.mLandscapeRotation || rotation == this.mSeascapeRotation;
    }

    private boolean isAnyPortrait(int rotation) {
        return rotation == this.mPortraitRotation || rotation == this.mUpsideDownRotation;
    }

    /* access modifiers changed from: private */
    public boolean isValidRotationChoice(int preferredRotation) {
        int i = this.mCurrentAppOrientation;
        if (i != -1 && i != 2) {
            switch (i) {
                case 11:
                    return isLandscapeOrSeascape(preferredRotation);
                case 12:
                    if (preferredRotation == this.mPortraitRotation) {
                        return true;
                    }
                    return false;
                case 13:
                    if (preferredRotation >= 0) {
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        } else if (preferredRotation < 0 || preferredRotation == this.mUpsideDownRotation) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: private */
    public boolean isRotationChoicePossible(int orientation) {
        int dockMode;
        if (this.mUserRotationMode != 1 || isFixedToUserRotation()) {
            return false;
        }
        if ((this.mDisplayPolicy.getLidState() == 1 && this.mLidOpenRotation >= 0) || (dockMode = this.mDisplayPolicy.getDockMode()) == 2) {
            return false;
        }
        boolean deskDockEnablesAccelerometer = this.mDisplayPolicy.isDeskDockEnablesAccelerometer();
        if ((dockMode == 1 || dockMode == 3 || dockMode == 4) && !deskDockEnablesAccelerometer) {
            return false;
        }
        boolean hdmiPlugged = this.mDisplayPolicy.isHdmiPlugged();
        if (hdmiPlugged && this.mDemoHdmiRotationLock) {
            return false;
        }
        if ((hdmiPlugged && dockMode == 0 && this.mUndockedHdmiRotation >= 0) || this.mDemoRotationLock || this.mDisplayPolicy.isPersistentVrModeEnabled() || !this.mSupportAutoRotation) {
            return false;
        }
        if (!(orientation == -1 || orientation == 2)) {
            switch (orientation) {
                case 11:
                case 12:
                case 13:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void sendProposedRotationChangeToStatusBarInternal(int rotation, boolean isValid) {
        if (this.mStatusBarManagerInternal == null) {
            this.mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
        }
        StatusBarManagerInternal statusBarManagerInternal = this.mStatusBarManagerInternal;
        if (statusBarManagerInternal != null) {
            statusBarManagerInternal.onProposedRotationChanged(rotation, isValid);
        }
    }

    private static String allowAllRotationsToString(int allowAll) {
        if (allowAll == -1) {
            return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
        }
        if (allowAll == 0) {
            return "false";
        }
        if (allowAll != 1) {
            return Integer.toString(allowAll);
        }
        return "true";
    }

    public void onUserSwitch() {
        SettingsObserver settingsObserver = this.mSettingsObserver;
        if (settingsObserver != null) {
            settingsObserver.onChange(false);
        }
    }

    /* access modifiers changed from: private */
    public boolean updateSettings() {
        int showRotationSuggestions;
        ContentResolver resolver = this.mContext.getContentResolver();
        boolean shouldUpdateRotation = false;
        synchronized (this.mLock) {
            boolean shouldUpdateOrientationListener = false;
            int i = 0;
            if (ActivityManager.isLowRamDeviceStatic()) {
                showRotationSuggestions = 0;
            } else {
                showRotationSuggestions = Settings.Secure.getIntForUser(resolver, "show_rotation_suggestions", 0, -2);
            }
            if (this.mShowRotationSuggestions != showRotationSuggestions) {
                this.mShowRotationSuggestions = showRotationSuggestions;
                shouldUpdateOrientationListener = true;
            }
            int userRotation = Settings.System.getIntForUser(resolver, "user_rotation", 0, -2);
            if (this.mUserRotation != userRotation) {
                this.mUserRotation = userRotation;
                shouldUpdateRotation = true;
            }
            if (Settings.System.getIntForUser(resolver, "accelerometer_rotation", 0, -2) == 0) {
                i = 1;
            }
            int userRotationMode = i;
            if (this.mUserRotationMode != userRotationMode) {
                this.mUserRotationMode = userRotationMode;
                shouldUpdateOrientationListener = true;
                shouldUpdateRotation = true;
            }
            if (shouldUpdateOrientationListener) {
                updateOrientationListenerLw();
            }
        }
        return shouldUpdateRotation;
    }

    /* access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        pw.println(prefix + "DisplayRotation");
        pw.println(prefix + "  mCurrentAppOrientation=" + ActivityInfo.screenOrientationToString(this.mCurrentAppOrientation));
        pw.print(prefix + "  mLandscapeRotation=" + Surface.rotationToString(this.mLandscapeRotation));
        StringBuilder sb = new StringBuilder();
        sb.append(" mSeascapeRotation=");
        sb.append(Surface.rotationToString(this.mSeascapeRotation));
        pw.println(sb.toString());
        pw.print(prefix + "  mPortraitRotation=" + Surface.rotationToString(this.mPortraitRotation));
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" mUpsideDownRotation=");
        sb2.append(Surface.rotationToString(this.mUpsideDownRotation));
        pw.println(sb2.toString());
        pw.println(prefix + "  mSupportAutoRotation=" + this.mSupportAutoRotation);
        OrientationListener orientationListener = this.mOrientationListener;
        if (orientationListener != null) {
            orientationListener.dump(pw, prefix + "  ");
        }
        pw.println();
        pw.print(prefix + "  mCarDockRotation=" + Surface.rotationToString(this.mCarDockRotation));
        StringBuilder sb3 = new StringBuilder();
        sb3.append(" mDeskDockRotation=");
        sb3.append(Surface.rotationToString(this.mDeskDockRotation));
        pw.println(sb3.toString());
        pw.print(prefix + "  mUserRotationMode=" + WindowManagerPolicy.userRotationModeToString(this.mUserRotationMode));
        StringBuilder sb4 = new StringBuilder();
        sb4.append(" mUserRotation=");
        sb4.append(Surface.rotationToString(this.mUserRotation));
        pw.print(sb4.toString());
        pw.println(" mAllowAllRotations=" + allowAllRotationsToString(this.mAllowAllRotations));
        pw.print(prefix + "  mDemoHdmiRotation=" + Surface.rotationToString(this.mDemoHdmiRotation));
        StringBuilder sb5 = new StringBuilder();
        sb5.append(" mDemoHdmiRotationLock=");
        sb5.append(this.mDemoHdmiRotationLock);
        pw.print(sb5.toString());
        pw.println(" mUndockedHdmiRotation=" + Surface.rotationToString(this.mUndockedHdmiRotation));
        pw.println(prefix + "  mLidOpenRotation=" + Surface.rotationToString(this.mLidOpenRotation));
        pw.println(prefix + "  mFixedToUserRotation=" + isFixedToUserRotation());
    }

    private class OrientationListener extends WindowOrientationListener {
        boolean mEnabled;
        final SparseArray<Runnable> mRunnableCache = new SparseArray<>(5);

        OrientationListener(Context context, Handler handler) {
            super(context, handler);
        }

        private class UpdateRunnable implements Runnable {
            final int mRotation;

            UpdateRunnable(int rotation) {
                this.mRotation = rotation;
            }

            public void run() {
                DisplayRotation.this.mService.mPowerManagerInternal.powerHint(2, 0);
                if (DisplayRotation.this.isRotationChoicePossible(DisplayRotation.this.mCurrentAppOrientation)) {
                    DisplayRotation.this.sendProposedRotationChangeToStatusBarInternal(this.mRotation, DisplayRotation.this.isValidRotationChoice(this.mRotation));
                    return;
                }
                DisplayRotation.this.mService.updateRotation(false, false);
            }
        }

        public void onProposedRotationChanged(int rotation) {
            Runnable r = this.mRunnableCache.get(rotation, (Object) null);
            if (r == null) {
                r = new UpdateRunnable(rotation);
                this.mRunnableCache.put(rotation, r);
            }
            getHandler().post(r);
        }

        public void enable(boolean clearCurrentRotation) {
            super.enable(clearCurrentRotation);
            this.mEnabled = true;
        }

        public void disable() {
            super.disable();
            this.mEnabled = false;
        }
    }

    private class SettingsObserver extends ContentObserver {
        SettingsObserver(Handler handler) {
            super(handler);
        }

        /* access modifiers changed from: package-private */
        public void observe() {
            ContentResolver resolver = DisplayRotation.this.mContext.getContentResolver();
            resolver.registerContentObserver(Settings.Secure.getUriFor("show_rotation_suggestions"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("accelerometer_rotation"), false, this, -1);
            resolver.registerContentObserver(Settings.System.getUriFor("user_rotation"), false, this, -1);
            boolean unused = DisplayRotation.this.updateSettings();
        }

        public void onChange(boolean selfChange) {
            if (DisplayRotation.this.updateSettings()) {
                DisplayRotation.this.mService.updateRotation(true, false);
            }
        }
    }
}
