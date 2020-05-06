package com.android.server.display;

import android.content.Context;
import android.miui.R;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Slog;
import com.android.server.display.AutomaticBrightnessControllerInjector;
import com.android.server.display.SunlightController;
import java.io.PrintWriter;

public class DisplayPowerControllerInjector implements SunlightController.Callback {
    private static final boolean DEBUG = false;
    private static final String TAG = "DisplayPowerControllerInjector";
    private static final int TRANSACTION_NOTIFY_BRIGHTNESS = 1104;
    private int mActualScreenOnBrightness;
    private boolean mAppliedSunlightMode;
    private final AutomaticBrightnessControllerInjector.StateChangeCallback mCallback = new AutomaticBrightnessControllerInjector.StateChangeCallback() {
        public void onSliderDurationChanged(int duration) {
            if (DisplayPowerControllerInjector.this.mLastSlideProgressDuration != duration) {
                int unused = DisplayPowerControllerInjector.this.mLastSlideProgressDuration = duration;
                Settings.System.putInt(DisplayPowerControllerInjector.this.mContext.getContentResolver(), "slider_animation_duration", duration);
            }
        }
    };
    /* access modifiers changed from: private */
    public Context mContext;
    private int mDesiredBrightness;
    private boolean mDozeInLowBrightness;
    private int mDozeScreenBrightness = -1;
    private IBinder mISurfaceFlinger;
    private int mLastSettingsBrightnessBeforeApplySunlight;
    /* access modifiers changed from: private */
    public int mLastSlideProgressDuration;
    private SunlightController mSunlightController;
    private boolean mSunlightModeActive;
    private boolean mSunlightModeAvailable;
    private SunlightStateChangedListener mSunlightStateListener;

    public interface SunlightStateChangedListener {
        void onSunlightStateChange();

        void updateScreenBrightnessSettingDueToSunlight(int i);
    }

    public DisplayPowerControllerInjector(Context context, Looper looper) {
        this.mContext = context;
        this.mSunlightModeAvailable = this.mContext.getResources().getBoolean(R.bool.config_sunlight_mode_available);
        if (this.mSunlightModeAvailable) {
            this.mSunlightController = new SunlightController(context, this, looper);
        }
        this.mISurfaceFlinger = ServiceManager.getService("SurfaceFlinger");
        AutomaticBrightnessControllerInjector.setStateChangeCallback(this.mCallback);
    }

    public void mayBeReportUserDisableSunlightTemporary(int tempBrightness) {
        if (this.mSunlightModeAvailable && this.mAppliedSunlightMode && tempBrightness != -1) {
            this.mSunlightController.setSunlightModeDisabledByUserTemporary();
        }
    }

    public void setSunlightListener(SunlightStateChangedListener listener) {
        this.mSunlightStateListener = listener;
    }

    private boolean isAllowedUseSunlightMode() {
        return this.mSunlightModeActive && !isSunlightModeDisabledByUser();
    }

    public int canApplyingSunlightBrightness(boolean useAutoBrightness, int state, int currentScreenBrightness, int brightness) {
        SunlightStateChangedListener sunlightStateChangedListener;
        int tempBrightness = brightness;
        if (this.mSunlightModeAvailable && !useAutoBrightness && state == 2 && isAllowedUseSunlightMode()) {
            tempBrightness = PowerManager.BRIGHTNESS_ON;
            if (!this.mAppliedSunlightMode) {
                this.mLastSettingsBrightnessBeforeApplySunlight = currentScreenBrightness;
            }
            this.mAppliedSunlightMode = true;
            SunlightStateChangedListener sunlightStateChangedListener2 = this.mSunlightStateListener;
            if (sunlightStateChangedListener2 != null) {
                sunlightStateChangedListener2.updateScreenBrightnessSettingDueToSunlight(tempBrightness);
            }
        } else if (this.mAppliedSunlightMode) {
            this.mAppliedSunlightMode = false;
            if (!isSunlightModeDisabledByUser() && (sunlightStateChangedListener = this.mSunlightStateListener) != null) {
                sunlightStateChangedListener.updateScreenBrightnessSettingDueToSunlight(this.mLastSettingsBrightnessBeforeApplySunlight);
            }
        }
        return tempBrightness;
    }

    public boolean isSunlightModeDisabledByUser() {
        return this.mSunlightController.isSunlightModeDisabledByUser();
    }

    public void notifySunlightStateChange(boolean active) {
        this.mSunlightModeActive = active;
        SunlightStateChangedListener sunlightStateChangedListener = this.mSunlightStateListener;
        if (sunlightStateChangedListener != null) {
            sunlightStateChangedListener.onSunlightStateChange();
        }
    }

    private void sendSurfaceFlingerActualBrightness(int brightness) {
        if (this.mISurfaceFlinger != null) {
            Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            data.writeInt(brightness);
            try {
                this.mISurfaceFlinger.transact(TRANSACTION_NOTIFY_BRIGHTNESS, data, (Parcel) null, 1);
            } catch (RemoteException | SecurityException ex) {
                Slog.e(TAG, "Failed to send brightness to SurfaceFlinger", ex);
            } catch (Throwable th) {
                data.recycle();
                throw th;
            }
            data.recycle();
        }
    }

    public void recordBrightnessChange(int currentState, int target, int dozeBrightness, boolean isDimming) {
        boolean z = true;
        if (currentState != 1) {
            if (currentState != 2) {
                if (currentState != 3) {
                    return;
                }
            } else if (target != this.mActualScreenOnBrightness && !isDimming) {
                this.mActualScreenOnBrightness = this.mSunlightModeActive ? this.mLastSettingsBrightnessBeforeApplySunlight : target;
                return;
            } else {
                return;
            }
        }
        if (dozeBrightness != this.mDozeScreenBrightness) {
            this.mDozeScreenBrightness = target;
            if (!(target == dozeBrightness && dozeBrightness == 1)) {
                z = false;
            }
            this.mDozeInLowBrightness = z;
        }
        int pendingBrightness = this.mDozeInLowBrightness ? this.mDozeScreenBrightness : this.mActualScreenOnBrightness;
        if (pendingBrightness != this.mDesiredBrightness) {
            this.mDesiredBrightness = pendingBrightness;
            sendSurfaceFlingerActualBrightness(this.mDesiredBrightness);
        }
    }

    public void dump(PrintWriter pw) {
        if (this.mSunlightModeAvailable) {
            this.mSunlightController.dump(pw);
            pw.println("  mAppliedSunlightMode=" + this.mAppliedSunlightMode);
            pw.println("  mLastSettingsBrightnessBeforeApplySunlight=" + this.mLastSettingsBrightnessBeforeApplySunlight);
            pw.println("  mLastSlideProgressDuration=" + this.mLastSlideProgressDuration);
        }
    }
}
