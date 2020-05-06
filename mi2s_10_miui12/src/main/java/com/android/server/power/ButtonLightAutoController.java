package com.android.server.power;

import android.content.Context;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import com.android.server.lights.Light;

public class ButtonLightAutoController {
    private static final int ANIMATION_FPS = 20;
    private static final int ANIMATION_RATE = 160;
    private static final int BUTTON_LIGHT_HIGH = 150;
    private static final int BUTTON_LIGHT_LOW = 20;
    private static final int BUTTON_LIGHT_MEDIUM = 80;
    private static final float BUTTON_LUX_LEVEL0_D = 5.0f;
    private static final float BUTTON_LUX_LEVEL0_U = 15.0f;
    private static final float BUTTON_LUX_LEVEL1_D = 1000.0f;
    private static final float BUTTON_LUX_LEVEL1_U = 1500.0f;
    static Light sButtonLight;
    private static Runnable sButtonLightAnimation = new Runnable() {
        public void run() {
            ButtonLightAutoController.sSetNextStepBrightness();
        }
    };
    private static boolean sButtonLightEnabled;
    static int sButtonLightTimeout;
    static Runnable sButtonLightTimeoutTask = new Runnable() {
        public void run() {
            ButtonLightAutoController.turnOffHWButtonLight();
        }
    };
    static boolean sButtonLightTurnOff;
    static int sCurrentBrightenss;
    static int sDeltaBrightness;
    static Handler sHandler;
    static boolean sIsScreenOn;
    private static Sensor sLightSensor;
    static int sLightSensorButtonBrightness;
    private static final SensorEventListener sLightSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            int oldBrightness = ButtonLightAutoController.sLightSensorButtonBrightness;
            if (!ButtonLightAutoController.sButtonLightTurnOff) {
                ButtonLightAutoController.sLightSensorButtonBrightness = ButtonLightAutoController.sGetNewBrightess(lux);
                if (oldBrightness != ButtonLightAutoController.sLightSensorButtonBrightness) {
                    ButtonLightAutoController.sAnimateButtonLight(oldBrightness, ButtonLightAutoController.sLightSensorButtonBrightness, 160);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private static SensorManager sSensorManager;
    static int sTargetBrightness;

    /* access modifiers changed from: private */
    public static final int sGetNewBrightess(float lux) {
        int i = sLightSensorButtonBrightness;
        int i2 = sLightSensorButtonBrightness;
        if (i2 == 20) {
            if (lux > 1500.0f) {
                return 150;
            }
            if (lux > BUTTON_LUX_LEVEL0_U) {
                return 80;
            }
            return 20;
        } else if (i2 == 80) {
            if (lux > 1500.0f) {
                return 150;
            }
            if (lux < BUTTON_LUX_LEVEL0_D) {
                return 20;
            }
            return 80;
        } else if (lux < BUTTON_LUX_LEVEL0_D) {
            return 20;
        } else {
            if (lux < BUTTON_LUX_LEVEL1_D) {
                return 80;
            }
            return 150;
        }
    }

    /* access modifiers changed from: private */
    public static void sSetNextStepBrightness() {
        boolean needChange = false;
        sButtonLight.setBrightness(sCurrentBrightenss);
        if (Math.abs(sDeltaBrightness) <= Math.abs(sCurrentBrightenss - sTargetBrightness)) {
            sCurrentBrightenss += sDeltaBrightness;
            needChange = true;
        } else {
            int i = sCurrentBrightenss;
            int i2 = sTargetBrightness;
            if (i != i2) {
                sCurrentBrightenss = i2;
                needChange = true;
            }
        }
        if (needChange) {
            sHandler.removeCallbacks(sButtonLightAnimation);
            sHandler.postAtTime(sButtonLightAnimation, SystemClock.uptimeMillis() + 50);
        }
    }

    /* access modifiers changed from: private */
    public static void sAnimateButtonLight(int from, int target, int rate) {
        sDeltaBrightness = (from > target ? -1 : 1) * Math.max(rate / 20, 1);
        sTargetBrightness = target;
        sCurrentBrightenss = from;
        sSetNextStepBrightness();
    }

    static void setButtonLight(final Context context, Handler handler, Light buttonLight) {
        sHandler = handler;
        sButtonLight = buttonLight;
        sSensorManager = (SensorManager) context.getSystemService("sensor");
        SensorManager sensorManager = sSensorManager;
        if (sensorManager != null) {
            sLightSensor = sensorManager.getDefaultSensor(5);
        }
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_buttons_timeout"), false, new ContentObserver(sHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                ButtonLightAutoController.updateButtonLightTimeout(context);
            }
        }, -1);
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("screen_buttons_turn_on"), true, new ContentObserver(sHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                ButtonLightAutoController.updateButtonLightTimeout(context);
            }
        });
        sButtonLightTurnOff = true;
        sIsScreenOn = true;
        sLightSensorButtonBrightness = 80;
        updateButtonLightTimeout(context);
    }

    static void turnOffHWButtonLight() {
        if (!sButtonLightTurnOff) {
            SensorManager sensorManager = sSensorManager;
            if (!(sensorManager == null || sLightSensor == null)) {
                sensorManager.unregisterListener(sLightSensorListener);
            }
            sHandler.removeCallbacks(sButtonLightAnimation);
            sButtonLight.turnOff();
            sButtonLightTurnOff = true;
        }
    }

    static void updateButtonLightTimeout(Context context) {
        int buttonLightTimeout = Settings.System.getIntForUser(context.getContentResolver(), "screen_buttons_timeout", 5000, -2);
        boolean z = true;
        if (Settings.Secure.getInt(context.getContentResolver(), "screen_buttons_turn_on", 1) != 1) {
            z = false;
        }
        boolean buttonLightEnabled = z;
        if (buttonLightTimeout != sButtonLightTimeout) {
            sButtonLightTimeout = buttonLightTimeout;
            setButtonLightTimeout();
        }
        if (buttonLightEnabled != sButtonLightEnabled) {
            sButtonLightEnabled = buttonLightEnabled;
            if (!buttonLightEnabled) {
                turnOffHWButtonLight();
            }
        }
    }

    static void setButtonLightTimeout() {
        Light light;
        Sensor sensor;
        if (sIsScreenOn && sHandler != null && (light = sButtonLight) != null && sButtonLightEnabled) {
            if (sButtonLightTurnOff) {
                sButtonLightTurnOff = false;
                light.setBrightness(sLightSensorButtonBrightness);
                SensorManager sensorManager = sSensorManager;
                if (!(sensorManager == null || (sensor = sLightSensor) == null)) {
                    sensorManager.registerListener(sLightSensorListener, sensor, 3, sHandler);
                }
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
            turnOffHWButtonLight();
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
