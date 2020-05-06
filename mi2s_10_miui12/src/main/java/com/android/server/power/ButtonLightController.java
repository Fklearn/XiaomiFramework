package com.android.server.power;

import android.content.Context;
import android.database.ContentObserver;
import android.miui.R;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import com.android.server.lights.Light;

public class ButtonLightController {
    static Light sButtonLight;
    static int sButtonLightTimeout;
    static Runnable sButtonLightTimeoutTask = new Runnable() {
        public void run() {
            ButtonLightController.sButtonLight.turnOff();
            ButtonLightController.sButtonLightTurnOff = true;
        }
    };
    static boolean sButtonLightTurnOff;
    static Handler sHandler;
    static boolean sIsScreenOn;
    static int sLightSensorButtonBrightness;

    static void setButtonLight(final Context context, Handler handler, Light buttonLight) {
        sHandler = handler;
        sButtonLight = buttonLight;
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_buttons_timeout"), false, new ContentObserver(sHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                ButtonLightController.updateButtonLightTimeout(context);
            }
        }, -1);
        sButtonLightTurnOff = true;
        sIsScreenOn = true;
        sLightSensorButtonBrightness = context.getResources().getInteger(R.integer.config_buttonBrightnessSettingDefault);
        updateButtonLightTimeout(context);
    }

    static void updateButtonLightTimeout(Context context) {
        int buttonLightTimeout = Settings.System.getIntForUser(context.getContentResolver(), "screen_buttons_timeout", 5000, -2);
        if (buttonLightTimeout != sButtonLightTimeout) {
            sButtonLightTimeout = buttonLightTimeout;
            setButtonLightTimeout();
        }
    }

    static void setButtonLightTimeout() {
        Light light;
        if (sIsScreenOn && sHandler != null && (light = sButtonLight) != null) {
            if (sButtonLightTurnOff) {
                sButtonLightTurnOff = false;
                light.setBrightness(sLightSensorButtonBrightness);
            }
            sHandler.removeCallbacks(sButtonLightTimeoutTask);
            if (sButtonLightTimeout > 0) {
                sHandler.postAtTime(sButtonLightTimeoutTask, SystemClock.uptimeMillis() + ((long) sButtonLightTimeout));
            }
        }
    }

    static void turnOffButtonLight(int newScreenState) {
        if (newScreenState == 0 && !sButtonLightTurnOff) {
            doTurnOffButtonLight();
        }
    }

    static void doTurnOffButtonLight() {
        Handler handler = sHandler;
        if (handler != null && sButtonLight != null) {
            handler.removeCallbacks(sButtonLightTimeoutTask);
            sButtonLight.turnOff();
            sButtonLightTurnOff = true;
        }
    }

    static void updateButtonLightState(boolean displayReady, boolean isProximityPositive, int displayPowerPolicy) {
        if (displayReady && displayPowerPolicy == 3 && !isProximityPositive && !sIsScreenOn) {
            sIsScreenOn = true;
            setButtonLightTimeout();
        } else if ((displayPowerPolicy == 0 || isProximityPositive) && sIsScreenOn) {
            sIsScreenOn = false;
            doTurnOffButtonLight();
        }
    }

    public static void setScreenOn(boolean isScreenOn) {
        if (sIsScreenOn != isScreenOn) {
            sIsScreenOn = isScreenOn;
            if (isScreenOn) {
                setButtonLightTimeout();
            } else {
                doTurnOffButtonLight();
            }
        }
    }
}
