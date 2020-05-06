package com.android.server.display;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.miui.R;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.MathUtils;
import android.util.Slog;
import android.util.Spline;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.os.DeviceFeature;

public class AutomaticBrightnessControllerInjector {
    private static int ALS_FOV = Resources.getSystem().getInteger(R.integer.config_brightness_fov);
    private static final float ASSISTSENSOR_BRIGHTENINGRATIO = 2.0f;
    private static final float ASSISTSENSOR_BRIGHTENING_MINTHRES = 5.0f;
    private static final float ASSISTSENSOR_DARKENINGRATIO = 0.2f;
    private static final float ASSISTSENSOR_DATA_THRESHOLD = ((float) Resources.getSystem().getInteger(R.integer.config_assistsensor_threshold));
    private static final long ASSISTSENSOR_DEBOUNCETIME = 10000;
    private static final float ASSISTSENSOR_MAXTHRES = ((float) Resources.getSystem().getInteger(R.integer.config_assistsensor_maxthreshold));
    private static final int ASSISTSENSOR_TYPE = 33171055;
    private static final int AUTOBRIGHTNESS_RATE_SLOW = Resources.getSystem().getInteger(R.integer.config_auto_brightness_rates_slow);
    private static final String AUTO_BRIGHTNESS_DEBUG = "sys.sensor.autobacklight.dbg";
    private static Spline AUTO_BRIGHTNESS_MIN_NIT_SPLINE = null;
    private static final int BRIGHTNESS_12BIT = 4095;
    private static final int BRIGHTNESS_BRIGHTEN_RATE = Resources.getSystem().getInteger(R.integer.config_brightness_brightening_rate);
    private static float BRIGHTNESS_DARK = (((float) Resources.getSystem().getInteger(R.integer.config_autobrightness_dark_brightness)) / 1000.0f);
    private static final int BRIGHTNESS_DARKTIME_1SECOND = Resources.getSystem().getInteger(R.integer.config_brightness_darktime1_seconds);
    private static final int BRIGHTNESS_DARKTIME_2SECOND = Resources.getSystem().getInteger(R.integer.config_brightness_darktime2_seconds);
    private static float BRIGHTNESS_DARK_RAMPRATE = (((float) Resources.getSystem().getInteger(R.integer.config_autobrightness_dark_rate)) / 1000.0f);
    private static final int BRIGHTNESS_DARK_THREHOLD = Resources.getSystem().getInteger(R.integer.config_brightness_darken_threhold);
    private static final int BRIGHTNESS_IN_BRIGHTENING = 2;
    private static final int BRIGHTNESS_IN_DARKENING = 1;
    private static final int BRIGHTNESS_IN_STABLE = 0;
    private static final boolean BRIGHTNESS_RATE_ADJUSTIBLE = Resources.getSystem().getBoolean(R.bool.config_autobrightness_rate_adjustible);
    private static float DARKENING_HYSTERESIS_FOV2SEVENTY = (((float) Resources.getSystem().getInteger(R.integer.config_fov2seventy_hysteresis)) / 10.0f);
    private static float DARKENING_HYSTERESIS_SEVENTY2NINETY = (((float) Resources.getSystem().getInteger(R.integer.config_seventy2ninety_hysteresis)) / 10.0f);
    private static float DARKENING_HYSTERESIS_ZERO2FOV = (((float) Resources.getSystem().getInteger(R.integer.config_zero2fov_hysteresis)) / 10.0f);
    private static boolean DEBUG = false;
    private static final int DEFAULT_SLIDER_DURATION = 3000;
    private static final int DYNAMIC_DARKENING_DEBOUNCE1 = Resources.getSystem().getInteger(R.integer.config_dynamicdebounce1);
    private static final int DYNAMIC_DARKENING_DEBOUNCE2 = Resources.getSystem().getInteger(R.integer.config_dynamicdebounce2);
    private static final int DYNAMIC_DARKENING_DEFAULTDEBOUNCE = Resources.getSystem().getInteger(R.integer.config_defaultdebounce);
    private static final float DYNAMIC_DARKENING_LUXTHRESHOLD = ((float) Resources.getSystem().getInteger(R.integer.config_dynamic_luxthreshold));
    private static float HIGHANGLE_THRESHOLD = ((float) Resources.getSystem().getInteger(R.integer.config_brightness_highangle_threshold));
    private static final int HIGHEST_LUX = 4000;
    private static final int INIT_BRIGHTNESS = -1;
    private static final boolean IS_UMI_0B_DISPLAY_PANEL;
    private static final float MOTIONSENSOR_STATIC_LUXTHRESHOLD = ((float) Resources.getSystem().getInteger(R.integer.config_motionsensor_threshold));
    private static final int MOTIONSENSOR_TYPE = 33171039;
    private static final int MOTION_MOVE = 1;
    private static final int MOTION_STATIC = 2;
    private static final float NIT_LEVEL = 40.0f;
    private static final float NIT_LEVEL1 = 35.0f;
    private static final float NIT_LEVEL2 = 87.450005f;
    private static final float NIT_LEVEL3 = 265.0f;
    private static final String OLED_PANEL_ID = SystemProperties.get("ro.boot.oled_panel_id", "");
    private static final int PROXIMITY_NEGATIVE = 0;
    private static final int PROXIMITY_POSITIVE = 1;
    private static final int PROXIMITY_UNKNOWN = -1;
    private static final int RATE_LEVEL = 40;
    private static final String REASON_DURATION_AUTO_BRIGHTEN = "auto_brighten";
    private static final String REASON_DURATION_AUTO_DARKEN = "auto_darken";
    private static final String REASON_DURATION_FIRST_LUX = "first_lux";
    private static final String REASON_DURATION_MANUAL = "manual";
    private static final int SEVERAL_CHANGE_THRESHOLD = 30;
    private static final int SINGLE_CHANGE_THRESHOLD = 20;
    private static final int SKIP_DEBOUNCE = Resources.getSystem().getInteger(R.integer.config_brightness_skipdebounce);
    private static final boolean SKIP_DEBOUNCE_ENABLED = Resources.getSystem().getBoolean(R.bool.config_brightness_useskipdebounce);
    private static final float SKIP_LUX_DEBOUNCE1 = ((float) Resources.getSystem().getInteger(R.integer.config_skipdebounce_lux1));
    private static final float SKIP_LUX_DEBOUNCE2 = ((float) Resources.getSystem().getInteger(R.integer.config_skipdebounce_lux2));
    private static final int SLOW_RATIO = Resources.getSystem().getInteger(R.integer.config_slow_ratio);
    private static final String TAG = "AutomaticBrightnessControllerInjector";
    private static final float TIME_1 = 0.0f;
    private static final float TIME_2 = 0.8f;
    private static final float TIME_3 = 1.8f;
    private static final float TIME_4 = 4.0f;
    private static final float TIME_5 = 24.0f;
    private static final int TIME_THRESHOLD = 10000;
    private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0f;
    private static final boolean USE_ACCELEROMETER_ENABLED = Resources.getSystem().getBoolean(R.bool.config_brightness_useacc);
    private static final boolean USE_ASSISTSENSOR_ENABLED = Resources.getSystem().getBoolean(R.bool.config_brightness_useassistsensor);
    private static final boolean USE_DYNAMIC_DEBOUNCE = Resources.getSystem().getBoolean(R.bool.config_brightness_usedynamicdebounce);
    private static final boolean USE_MOTIONSENSOR_ENABLED = Resources.getSystem().getBoolean(R.bool.config_brightness_usemotionsensor);
    private static final boolean USE_PROXIMITY_ENABLED = (Resources.getSystem().getBoolean(R.bool.config_brightness_useprox) && !DeviceFeature.hasSupportAudioPromity());
    private static final boolean WAIT_FOR_AUTOBRIGHTNESS = Resources.getSystem().getBoolean(R.bool.config_wait_for_autobrightness);
    private static final int WAIT_TIME = Resources.getSystem().getInteger(R.integer.config_wait_time);
    private static AmbientLightFlickBuffer mAmbientLightFlickBuffer;
    private static final float[] sALevels = {800.0f, 569.48f, 344.89f, 237.75f, 179.71f, 135.19f, 113.59f, 62.84f, 676.87f};
    private static boolean sAccSensorEnabled = false;
    private static Sensor sAccelerometer;
    private static boolean sAmbientLuxFirstEvent = false;
    private static float sAngleXY2Horizon = 0.0f;
    private static float sAssistBrighteningThres = -1.0f;
    private static float sAssistDarkeningThres = -1.0f;
    private static Sensor sAssistSensor;
    private static float sAssistSensorData = -1.0f;
    private static boolean sAssistSensorEnabled = false;
    private static long sAssistSensorTime = -1;
    private static long sAutoBrightnessEnableTime = 0;
    private static long sAutoBrightnessStartTime = 0;
    private static boolean sAutomaticBrightnessEnable = false;
    private static final float[] sBLevels = {0.9887f, 0.992f, 0.995f, 0.9965f, 0.9973f, 0.9979f, 0.9982f, 0.999f, 0.996f};
    private static int sBrightnessStatus = 0;
    private static float sCurrentLux = -1.0f;
    private static int sCurrentUserId;
    private static long sDynamicEnvStartTime = 0;
    private static LinkedList<BrightnessEvent> sEvents = new LinkedList<>();
    private static float sFirstLux = -1.0f;
    private static int sLastBrightness = -1;
    private static float sLastLux = -1.0f;
    private static int sLastOriginalRate;
    private static int sLastTargetValue;
    private static float sMaxNitsProperty;
    private static Sensor sMotionSensor;
    private static boolean sMotionSensorEnabled = false;
    private static int sMotionStatus = 1;
    private static final float[] sNitsLevels = {800.0f, 251.0f, 150.0f, 100.0f, 70.0f, 50.0f, NIT_LEVEL, 30.0f, 28.5f};
    private static int sProximity = -1;
    private static Sensor sProximitySensor;
    private static boolean sProximitySensorEnabled = false;
    private static float sProximityThreshold;
    private static float sRealLux;
    private static final SensorEventListener sSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            int type = event.sensor.getType();
            if (type == 1) {
                AutomaticBrightnessControllerInjector.onAccelerometerSensorChanged(event);
            } else if (type == 5) {
            } else {
                if (type == 8) {
                    AutomaticBrightnessControllerInjector.onProximitySensorChanged(event);
                } else if (type == AutomaticBrightnessControllerInjector.MOTIONSENSOR_TYPE) {
                    AutomaticBrightnessControllerInjector.onMotionSensorChanged(event);
                } else if (type == AutomaticBrightnessControllerInjector.ASSISTSENSOR_TYPE) {
                    AutomaticBrightnessControllerInjector.onAssistSensorChanged(event);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private static SensorManager sSensorManager;
    private static IBinder sSensorService;
    private static boolean sSlowChange = false;
    private static float sStableLux;
    private static int sStartBrightness = -1;
    static StateChangeCallback sStateChangeCallback;
    private static float sUnadjustedBrightness = -1.0f;
    private static HashMap<Integer, UserDataPoint> sUserDataPoint = new HashMap<>();

    protected interface StateChangeCallback {
        void onSliderDurationChanged(int i);
    }

    static {
        boolean z = false;
        if ("0B".equals(OLED_PANEL_ID) && ("umi".equals(Build.DEVICE) || "umiin".equals(Build.DEVICE))) {
            z = true;
        }
        IS_UMI_0B_DISPLAY_PANEL = z;
        try {
            AUTO_BRIGHTNESS_MIN_NIT_SPLINE = Spline.createSpline(getFloatArray(Resources.getSystem().obtainTypedArray(R.array.config_originalLux)), getFloatArray(Resources.getSystem().obtainTypedArray(R.array.config_autoBrightnessMinNitForOriginalLux)));
            Slog.i(TAG, "AUTO_BRIGHTNESS_MIN_NIT_SPLINE: " + AUTO_BRIGHTNESS_MIN_NIT_SPLINE);
        } catch (IllegalArgumentException e) {
            Slog.w(TAG, "min brightness changes with original lux not support");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean needToUpdateAssistSensorData() {
        /*
            boolean r0 = DEBUG
            java.lang.String r1 = ", sAssistSensorTime="
            java.lang.String r2 = "USE_ASSISTSENSOR_ENABLED="
            java.lang.String r3 = "AutomaticBrightnessControllerInjector"
            if (r0 == 0) goto L_0x0026
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            boolean r4 = USE_ASSISTSENSOR_ENABLED
            r0.append(r4)
            r0.append(r1)
            long r4 = sAssistSensorTime
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            android.util.Slog.d(r3, r0)
        L_0x0026:
            boolean r0 = USE_ASSISTSENSOR_ENABLED
            if (r0 == 0) goto L_0x00c3
            long r4 = sAssistSensorTime
            r6 = -1
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 != 0) goto L_0x0034
            goto L_0x00c3
        L_0x0034:
            long r4 = android.os.SystemClock.uptimeMillis()
            long r6 = sAssistSensorTime
            long r6 = r4 - r6
            r0 = 0
            float r8 = sAssistBrighteningThres
            r9 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r10 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r10 == 0) goto L_0x006b
            float r10 = sAssistDarkeningThres
            int r9 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
            if (r9 == 0) goto L_0x006b
            float r9 = sAssistSensorData
            int r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r8 > 0) goto L_0x0055
            int r8 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r8 >= 0) goto L_0x006b
        L_0x0055:
            boolean r8 = checkAssistSensorValid()
            if (r8 == 0) goto L_0x006b
            float r8 = sAssistSensorData
            float r9 = ASSISTSENSOR_MAXTHRES
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 >= 0) goto L_0x006b
            r8 = 10000(0x2710, double:4.9407E-320)
            int r8 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x006b
            r0 = 1
            goto L_0x006c
        L_0x006b:
            r0 = 0
        L_0x006c:
            boolean r8 = DEBUG
            if (r8 == 0) goto L_0x00c2
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r2)
            boolean r2 = USE_ASSISTSENSOR_ENABLED
            r8.append(r2)
            r8.append(r1)
            long r1 = sAssistSensorTime
            r8.append(r1)
            java.lang.String r1 = ", currTime="
            r8.append(r1)
            r8.append(r4)
            java.lang.String r1 = ", deltaTime="
            r8.append(r1)
            r8.append(r6)
            java.lang.String r1 = ", sAssistSensorData="
            r8.append(r1)
            float r1 = sAssistSensorData
            r8.append(r1)
            java.lang.String r1 = ", sAssistBrighteningThres="
            r8.append(r1)
            float r1 = sAssistBrighteningThres
            r8.append(r1)
            java.lang.String r1 = ", sAssistDarkeningThres="
            r8.append(r1)
            float r1 = sAssistDarkeningThres
            r8.append(r1)
            java.lang.String r1 = ", ret="
            r8.append(r1)
            r8.append(r0)
            java.lang.String r1 = r8.toString()
            android.util.Slog.d(r3, r1)
        L_0x00c2:
            return r0
        L_0x00c3:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.AutomaticBrightnessControllerInjector.needToUpdateAssistSensorData():boolean");
    }

    public static float setSensorEnabled(boolean enable, Sensor sensor) {
        return setLightSensorEnabled(enable, sensor);
    }

    public static float setLightSensorEnabled(boolean enable, Sensor sensor) {
        Slog.d(TAG, "setLightSensorEnabled: " + enable);
        float value = getLatestData(sensor);
        if (enable) {
            sSensorManager.registerListener(sSensorListener, sensor, 3);
        } else {
            sSensorManager.unregisterListener(sSensorListener, sensor);
        }
        return value;
    }

    private static float getLatestData(Sensor sensor) {
        float res = -1.0f;
        if (sSensorService == null || sensor == null) {
            return -1.0f;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken("android.gui.SensorServer");
            data.writeInt(sensor.getHandle());
            sSensorService.transact(16777214, data, reply, 0);
            res = reply.readFloat();
        } catch (RemoteException e) {
            Slog.d(TAG, "RemoteException!");
        } catch (Throwable th) {
            reply.recycle();
            data.recycle();
            throw th;
        }
        reply.recycle();
        data.recycle();
        Slog.d(TAG, "res: " + res);
        return res;
    }

    static void initialize(SensorManager sensorManager) {
        sSensorManager = sensorManager;
        if (USE_PROXIMITY_ENABLED) {
            sProximitySensor = sSensorManager.getDefaultSensor(8);
            Sensor sensor = sProximitySensor;
            if (sensor != null) {
                sProximityThreshold = Math.min(sensor.getMaximumRange(), 5.0f);
            }
        }
        if (USE_ACCELEROMETER_ENABLED) {
            sAccelerometer = sSensorManager.getDefaultSensor(1);
        }
        if (USE_MOTIONSENSOR_ENABLED) {
            sMotionSensor = sSensorManager.getDefaultSensor(MOTIONSENSOR_TYPE);
        }
        if (USE_ASSISTSENSOR_ENABLED) {
            sAssistSensor = sSensorManager.getDefaultSensor(ASSISTSENSOR_TYPE);
        }
        AutomaticBrightnessTouchHelper.initialize();
        sSensorService = ServiceManager.getService("sensorservice");
        getMaxNit();
    }

    private static boolean isInteractivePolicy(int policy) {
        return policy == 3 || policy == 2 || policy == 4;
    }

    public static int configure(boolean enable, int screenautobrightness) {
        return configure(enable, screenautobrightness, 3, 3);
    }

    public static int configure(boolean enable, int screenautobrightness, int oldPolicy, int newPolicy) {
        if (USE_PROXIMITY_ENABLED) {
            setProximitySensorEnabled(enable);
        }
        if (USE_ACCELEROMETER_ENABLED) {
            setAccSensorEnabled(enable);
        }
        if (USE_MOTIONSENSOR_ENABLED) {
            setMotionSensorEnabled(enable);
        }
        if (USE_ASSISTSENSOR_ENABLED) {
            setAssistSensorEnabled(enable, oldPolicy, newPolicy);
        }
        AutomaticBrightnessTouchHelper.configure(enable);
        if (!enable) {
            sAutomaticBrightnessEnable = false;
            clearAmbientLightFlickBuffer();
            return -1;
        }
        sAutomaticBrightnessEnable = true;
        return screenautobrightness;
    }

    public static void initAmbientLightFlickBuffer(Context context) {
        mAmbientLightFlickBuffer = new AmbientLightFlickBuffer(context);
    }

    public static void clearAmbientLightFlickBuffer() {
        AmbientLightFlickBuffer ambientLightFlickBuffer = mAmbientLightFlickBuffer;
        if (ambientLightFlickBuffer != null) {
            ambientLightFlickBuffer.clear();
        }
    }

    public static int checkFlickBrightness(int brightness, float lux) {
        AmbientLightFlickBuffer ambientLightFlickBuffer = mAmbientLightFlickBuffer;
        if (ambientLightFlickBuffer != null) {
            try {
                ambientLightFlickBuffer.getCurrentBrightness(brightness, SystemClock.uptimeMillis(), lux);
            } catch (Exception e) {
                Slog.w(TAG, "checkFlickBrightness error " + e);
            }
        }
        return brightness;
    }

    public static boolean waitForAutoBrightness(boolean autobrightnessenabled) {
        if (!WAIT_FOR_AUTOBRIGHTNESS || !autobrightnessenabled || ((long) WAIT_TIME) <= getAutoBrightnessEnableTimeLength()) {
            if (!DEBUG) {
                return false;
            }
            Slog.d(TAG, "waitForAutoBrightness false!");
            return false;
        } else if (!DEBUG) {
            return true;
        } else {
            Slog.d(TAG, "waitForAutoBrightness true!");
            return true;
        }
    }

    private static long getAutoBrightnessEnableTimeLength() {
        if (sAccSensorEnabled) {
            sAutoBrightnessEnableTime = SystemClock.uptimeMillis() - sAutoBrightnessStartTime;
            if (sAutoBrightnessEnableTime < 0) {
                sAutoBrightnessEnableTime = 0;
                sAutoBrightnessStartTime = 0;
            }
            if (DEBUG) {
                Slog.d(TAG, "sAutoBrightnessEnableTime=" + sAutoBrightnessEnableTime);
            }
            return sAutoBrightnessEnableTime;
        }
        if (DEBUG) {
            Slog.d(TAG, "sAutoBrightnessEnableTime=" + sAutoBrightnessEnableTime);
        }
        return 0;
    }

    private static void setProximitySensorEnabled(boolean enable) {
        if (enable && !sProximitySensorEnabled) {
            Slog.d(TAG, "setProximitySensorEnabled enable");
            sProximitySensorEnabled = true;
            sSensorManager.registerListener(sSensorListener, sProximitySensor, 3);
        } else if (!enable && sProximitySensorEnabled) {
            Slog.d(TAG, "setProximitySensorEnabled disable");
            sProximitySensorEnabled = false;
            sProximity = -1;
            sSensorManager.unregisterListener(sSensorListener, sProximitySensor);
        }
    }

    private static void setAccSensorEnabled(boolean enable) {
        if (enable && !sAccSensorEnabled) {
            Slog.d(TAG, "setAccSensorEnabled enable");
            sAccSensorEnabled = true;
            sSensorManager.registerListener(sSensorListener, sAccelerometer, 3);
            sAutoBrightnessStartTime = SystemClock.uptimeMillis();
        } else if (!enable && sAccSensorEnabled) {
            Slog.d(TAG, "setAccSensorEnabled disable");
            sAccSensorEnabled = false;
            sFirstLux = -1.0f;
            sLastLux = -1.0f;
            sCurrentLux = -1.0f;
            sBrightnessStatus = 0;
            sAutoBrightnessEnableTime = 0;
            sAutoBrightnessStartTime = 0;
            sDynamicEnvStartTime = 0;
            sSensorManager.unregisterListener(sSensorListener, sAccelerometer);
        }
    }

    private static void setMotionSensorEnabled(boolean enable) {
        if (enable && !sMotionSensorEnabled) {
            Slog.d(TAG, "setMotionSensorEnabled enable");
            sMotionSensorEnabled = true;
            sSensorManager.registerListener(sSensorListener, sMotionSensor, 3);
        } else if (!enable && sMotionSensorEnabled) {
            Slog.d(TAG, "setMotionSensorEnabled disable");
            sMotionSensorEnabled = false;
            sMotionStatus = 1;
            sSensorManager.unregisterListener(sSensorListener, sMotionSensor);
        }
    }

    private static void setAssistSensorEnabled(boolean enable, int oldPolicy, int newPolicy) {
        if (enable && !sAssistSensorEnabled) {
            Slog.d(TAG, "setAssistSensorEnabled enable");
            sAssistSensorEnabled = true;
            sSensorManager.registerListener(sSensorListener, sAssistSensor, 3);
        } else if (!enable && sAssistSensorEnabled && isInteractivePolicy(newPolicy)) {
            Slog.d(TAG, "setAssistSensorEnabled disable");
            sAssistSensorEnabled = false;
            sAssistSensorData = -1.0f;
            sAssistSensorTime = -1;
            sSensorManager.unregisterListener(sSensorListener, sAssistSensor);
        }
        if (enable && !isInteractivePolicy(oldPolicy) && isInteractivePolicy(newPolicy)) {
            sAssistSensorData = getLatestData(sAssistSensor);
        }
    }

    /* access modifiers changed from: private */
    public static void onProximitySensorChanged(SensorEvent event) {
        if (sProximitySensorEnabled) {
            long uptimeMillis = SystemClock.uptimeMillis();
            float distance = event.values[0];
            if (distance >= 0.0f && distance < sProximityThreshold) {
                sProximity = 1;
            } else {
                sProximity = 0;
            }
        }
    }

    /* access modifiers changed from: private */
    public static void onAccelerometerSensorChanged(SensorEvent event) {
        if (sAccSensorEnabled) {
            float[] values = event.values;
            float ax = values[0];
            float ay = values[1];
            float az = values[2];
            double g = Math.sqrt((double) ((ax * ax) + (ay * ay) + (az * az)));
            double cos = ((double) az) / g;
            if (cos > 1.0d) {
                cos = 1.0d;
            } else if (cos < -1.0d) {
                cos = -1.0d;
            }
            double angle = (Math.acos(cos) * 180.0d) / 3.141592653589793d;
            sAngleXY2Horizon = (float) angle;
            if (DEBUG) {
                Slog.e(TAG, "Auto-brightness acc: x=" + ax + ", y=" + ay + ", z=" + az + ", xyz=" + g + ", angle=" + angle + ", angle_xy2horizon" + sAngleXY2Horizon);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void onMotionSensorChanged(SensorEvent event) {
        if (sMotionSensorEnabled) {
            sMotionStatus = (int) event.values[0];
            if (DEBUG) {
                Slog.e(TAG, "Auto-brightness motion status: " + sMotionStatus);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void onAssistSensorChanged(SensorEvent event) {
        if (sAssistSensorEnabled) {
            sAssistSensorData = event.values[0];
            if (DEBUG) {
                Slog.e(TAG, "Auto-brightness assistsensor lux: " + sAssistSensorData);
            }
        }
    }

    static boolean checkProximityStatus(float lux, boolean luxValid) {
        if (AutomaticBrightnessTouchHelper.checkTouchStatus(lux, luxValid)) {
            return true;
        }
        if (DEBUG || !luxValid) {
            Slog.d(TAG, "sProximity=" + sProximity + ", lux=" + lux);
        }
        if (sFirstLux == -1.0f) {
            sFirstLux = lux;
        }
        if (!USE_PROXIMITY_ENABLED || sProximity != 1 || !luxValid) {
            sRealLux = lux;
            return false;
        }
        Slog.d(TAG, "drop the lightsensor event! lux=" + lux);
        return true;
    }

    static float getCurrentRealLux() {
        return sRealLux;
    }

    static float getCurrentLux(float ambientlux) {
        if (DEBUG) {
            Slog.d(TAG, "sRealLux = " + sRealLux + ", ambientlux = " + ambientlux);
        }
        float f = sRealLux;
        if (f < TIME_2 * ambientlux || f > 1.2f * ambientlux) {
            return ambientlux;
        }
        return f;
    }

    static boolean checkAssistSensorValid() {
        if (!USE_ASSISTSENSOR_ENABLED) {
            return false;
        }
        float f = sAssistSensorData;
        if (f == -1.0f || f >= ASSISTSENSOR_DATA_THRESHOLD) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000b, code lost:
        if (r1 > r5) goto L_0x000f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static float getCurrentLux(float r5, float r6, float r7) {
        /*
            r0 = r5
            boolean r1 = checkAssistSensorValid()
            if (r1 == 0) goto L_0x000e
            float r1 = sAssistSensorData
            int r2 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r2 <= 0) goto L_0x000e
            goto L_0x000f
        L_0x000e:
            r1 = r5
        L_0x000f:
            r0 = r1
            float r1 = sAssistSensorData
            int r2 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r2 != 0) goto L_0x0035
            r2 = 1045220557(0x3e4ccccd, float:0.2)
            float r2 = r2 * r1
            sAssistDarkeningThres = r2
            r2 = 1073741824(0x40000000, float:2.0)
            float r2 = r2 * r1
            sAssistBrighteningThres = r2
            float r2 = sAssistBrighteningThres
            r3 = 1084227584(0x40a00000, float:5.0)
            float r4 = r1 + r3
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 >= 0) goto L_0x002e
            float r1 = r1 + r3
            sAssistBrighteningThres = r1
        L_0x002e:
            long r1 = android.os.SystemClock.uptimeMillis()
            sAssistSensorTime = r1
            goto L_0x0039
        L_0x0035:
            r1 = -1
            sAssistSensorTime = r1
        L_0x0039:
            sCurrentLux = r0
            int r1 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
            if (r1 != 0) goto L_0x0043
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x00a4
        L_0x0043:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "maxLux="
            r1.append(r2)
            r1.append(r6)
            java.lang.String r2 = ", minLux="
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = ", lux="
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = ", sAssistSensorData="
            r1.append(r2)
            float r2 = sAssistSensorData
            r1.append(r2)
            java.lang.String r2 = ", sAssistDarkeningThres="
            r1.append(r2)
            float r2 = sAssistDarkeningThres
            r1.append(r2)
            java.lang.String r2 = ", sAssistBrighteningThres="
            r1.append(r2)
            float r2 = sAssistBrighteningThres
            r1.append(r2)
            java.lang.String r2 = ", sAssistSensorTime="
            r1.append(r2)
            long r2 = sAssistSensorTime
            r1.append(r2)
            java.lang.String r2 = ", currLux="
            r1.append(r2)
            r1.append(r0)
            java.lang.String r2 = ", sBrightnessStatus="
            r1.append(r2)
            int r2 = sBrightnessStatus
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AutomaticBrightnessControllerInjector"
            android.util.Slog.d(r2, r1)
        L_0x00a4:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.AutomaticBrightnessControllerInjector.getCurrentLux(float, float, float):float");
    }

    static void checkBrightening(float lux, float brightenThreshold, float darkThreshold) {
        if (lux > brightenThreshold) {
            sBrightnessStatus = 2;
            sDynamicEnvStartTime = SystemClock.uptimeMillis();
        } else if (lux < darkThreshold) {
            sBrightnessStatus = 1;
            sDynamicEnvStartTime = SystemClock.uptimeMillis();
        } else {
            sBrightnessStatus = 0;
        }
    }

    static boolean checkDynamicDebounce() {
        long currTime = SystemClock.uptimeMillis();
        long deltaTime = currTime - sDynamicEnvStartTime;
        if (DEBUG) {
            Slog.d(TAG, "sDynamicEnvStartTime=" + sDynamicEnvStartTime + ", deltaTime=" + deltaTime + ", currTime=" + currTime);
        }
        if (sDynamicEnvStartTime == 0 || deltaTime > 2000) {
            return true;
        }
        return false;
    }

    static boolean checkMotionStatus() {
        if (!USE_MOTIONSENSOR_ENABLED || sMotionStatus != 2) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x003f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static long updateDarkeningDebounce(float r4, float r5, float r6) {
        /*
            boolean r0 = checkMotionStatus()
            r1 = -1082130432(0xffffffffbf800000, float:-1.0)
            if (r0 == 0) goto L_0x001e
            float r0 = sCurrentLux
            float r2 = MOTIONSENSOR_STATIC_LUXTHRESHOLD
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x001e
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x001e
            boolean r0 = checkDynamicDebounce()
            if (r0 == 0) goto L_0x001e
            int r0 = DYNAMIC_DARKENING_DEBOUNCE1
            long r0 = (long) r0
            goto L_0x003b
        L_0x001e:
            boolean r0 = USE_DYNAMIC_DEBOUNCE
            if (r0 == 0) goto L_0x0038
            float r0 = sCurrentLux
            float r2 = DYNAMIC_DARKENING_LUXTHRESHOLD
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0038
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0038
            boolean r0 = checkDynamicDebounce()
            if (r0 == 0) goto L_0x0038
            int r0 = DYNAMIC_DARKENING_DEBOUNCE2
            long r0 = (long) r0
            goto L_0x003b
        L_0x0038:
            int r0 = DYNAMIC_DARKENING_DEFAULTDEBOUNCE
            long r0 = (long) r0
        L_0x003b:
            boolean r2 = DEBUG
            if (r2 == 0) goto L_0x005f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "DarkeningLightDebounce="
            r2.append(r3)
            r2.append(r0)
            java.lang.String r3 = ", sCurrentLux = "
            r2.append(r3)
            float r3 = sCurrentLux
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AutomaticBrightnessControllerInjector"
            android.util.Slog.d(r3, r2)
        L_0x005f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.AutomaticBrightnessControllerInjector.updateDarkeningDebounce(float, float, float):long");
    }

    static float getDarkenThreshold(float darkThreshold, float brightenThreshold, float lux) {
        float mAmbientDarkeningThreshold = darkThreshold;
        float f = sAngleXY2Horizon;
        if (f > HIGHANGLE_THRESHOLD) {
            mAmbientDarkeningThreshold *= 1.0f - DARKENING_HYSTERESIS_SEVENTY2NINETY;
        } else if (f > ((float) ALS_FOV)) {
            mAmbientDarkeningThreshold *= 1.0f - DARKENING_HYSTERESIS_FOV2SEVENTY;
        }
        Slog.d(TAG, "AmbientLux=" + lux + ", mAmbientDarkeningThreshold=" + mAmbientDarkeningThreshold + ", mAmbientBrighteningThreshold=" + brightenThreshold + ", angle_xy2horizon=" + sAngleXY2Horizon);
        return mAmbientDarkeningThreshold;
    }

    static boolean checkSkipDebounceStatus(long enableTime, long time) {
        boolean retVal;
        boolean retVal2 = true;
        if (!SKIP_DEBOUNCE_ENABLED || ((long) SKIP_DEBOUNCE) + enableTime < time) {
            sAmbientLuxFirstEvent = false;
            retVal = false;
        } else {
            Slog.d(TAG, "skip debounce!");
            sAmbientLuxFirstEvent = true;
            retVal = true;
        }
        if (!retVal && !needToUpdateAssistSensorData()) {
            retVal2 = false;
        }
        return retVal2;
    }

    static boolean checkSkipDebounceStatus(long enableTime, long time, float lux) {
        if (DEBUG) {
            Slog.d(TAG, "checkSkipDebounceStatus, enableTime=" + enableTime + ", time=" + time + ", SKIP_DEBOUNCE_ENABLED=" + SKIP_DEBOUNCE_ENABLED + ", SKIP_DEBOUNCE=" + SKIP_DEBOUNCE + ", enableTime=" + enableTime + ", sLastLux=" + sLastLux + ", sFirstLux=" + sFirstLux + ", lux=" + lux);
        }
        sAmbientLuxFirstEvent = false;
        if (SKIP_DEBOUNCE_ENABLED && ((long) SKIP_DEBOUNCE) + enableTime >= time) {
            float f = sLastLux;
            if (f == -1.0f || (f != -1.0f && lux > f + SKIP_LUX_DEBOUNCE2 && lux > sFirstLux + SKIP_LUX_DEBOUNCE1)) {
                sAmbientLuxFirstEvent = true;
                Slog.d(TAG, "skip debounce!");
                sLastLux = lux;
                return true;
            }
        }
        sLastLux = lux;
        if (sRealLux == lux && lux != 0.0f) {
            sStableLux = lux;
        }
        return needToUpdateAssistSensorData();
    }

    static boolean isAmbientLuxFirstEvent() {
        return sAmbientLuxFirstEvent;
    }

    public static int changeBrightness(float lux, int brightness) {
        if (lux >= 4000.0f) {
            return PowerManager.BRIGHTNESS_ON;
        }
        checkFlickBrightness(brightness, lux);
        return brightness;
    }

    public static int changeBrightness(float lux, int brightness, Spline nitToBrightnessSpline) {
        if (lux >= 4000.0f) {
            return PowerManager.BRIGHTNESS_ON;
        }
        if (AUTO_BRIGHTNESS_MIN_NIT_SPLINE == null || nitToBrightnessSpline == null || lux != 0.0f) {
            return brightness;
        }
        Slog.i(TAG, "mOriginalLux: " + sStableLux + " oldMinBrightness: " + brightness);
        int brightness2 = Math.round(nitToBrightnessSpline.interpolate(AUTO_BRIGHTNESS_MIN_NIT_SPLINE.interpolate(sStableLux)) * ((float) PowerManager.BRIGHTNESS_ON));
        StringBuilder sb = new StringBuilder();
        sb.append("newMinBrightness: ");
        sb.append(brightness2);
        Slog.i(TAG, sb.toString());
        return brightness2;
    }

    static int getScreenDarkenRate(int previousBrightness, int currentBrightness, int currentRate, float colorFadeLevel) {
        int rate;
        if (DEBUG) {
            Slog.d(TAG, "getScreenDarkenRate: previousBrightness=" + previousBrightness + ", currentBrightness=" + currentBrightness + ", sAmbientLuxFirstEvent=" + sAmbientLuxFirstEvent + ", currentRate=" + currentRate + ", colorFadeLevel=" + colorFadeLevel + ", testdata=" + ((1 << Math.max(DeviceFeature.BACKLIGHT_BIT - 8, 0)) * 58));
        }
        if (previousBrightness == currentBrightness) {
        }
        if (sAmbientLuxFirstEvent) {
            rate = BRIGHTNESS_BRIGHTEN_RATE * 3;
        } else if (currentBrightness > previousBrightness) {
            if ((1 << Math.max(DeviceFeature.BACKLIGHT_BIT - 8, 0)) * 58 > currentBrightness) {
                rate = (currentBrightness - previousBrightness) / 4;
                int i = BRIGHTNESS_BRIGHTEN_RATE;
                int i2 = SLOW_RATIO;
                if (rate < i / i2) {
                    rate = i / i2;
                }
            } else {
                rate = BRIGHTNESS_BRIGHTEN_RATE;
            }
        } else if (currentBrightness > BRIGHTNESS_DARK_THREHOLD) {
            int i3 = previousBrightness - currentBrightness;
            int i4 = AUTOBRIGHTNESS_RATE_SLOW;
            int i5 = BRIGHTNESS_DARKTIME_1SECOND;
            if (i3 > i4 * i5) {
                rate = (previousBrightness - currentBrightness) / i5;
            } else {
                rate = AUTOBRIGHTNESS_RATE_SLOW;
            }
        } else {
            int rate2 = previousBrightness - currentBrightness;
            int i6 = AUTOBRIGHTNESS_RATE_SLOW;
            int i7 = BRIGHTNESS_DARKTIME_2SECOND;
            if (rate2 > i6 * i7) {
                rate = (previousBrightness - currentBrightness) / i7;
            } else {
                rate = AUTOBRIGHTNESS_RATE_SLOW;
            }
        }
        if (colorFadeLevel == 0.0f) {
            rate = 0;
        }
        if (DEBUG) {
            Slog.d(TAG, "final rate=" + rate);
        }
        return rate;
    }

    static void updateSlowChangeStatus(boolean slowChange, int startBrightness) {
        sSlowChange = slowChange;
        sStartBrightness = startBrightness;
        if (DEBUG) {
            Slog.d(TAG, "updateSlowChangeStatus: " + sSlowChange + ", startBrightness: " + sStartBrightness);
        }
    }

    static void getMaxNit() {
        sMaxNitsProperty = Float.parseFloat(miui.os.SystemProperties.get("persist.vendor.max.brightness", "0"));
        if (DEBUG) {
            Slog.d(TAG, "maxNit: " + sMaxNitsProperty);
        }
    }

    static float convert2Nit(int brightness) {
        return (((float) brightness) * sMaxNitsProperty) / ((float) PowerManager.BRIGHTNESS_ON);
    }

    static int convert2Brightness(float nit) {
        if (sMaxNitsProperty != 0.0f) {
            return (int) ((((float) PowerManager.BRIGHTNESS_ON) * nit) / sMaxNitsProperty);
        }
        return 0;
    }

    static int getIndex(int brightness) {
        float nit = convert2Nit(brightness);
        int index = 1;
        while (true) {
            float[] fArr = sNitsLevels;
            if (fArr.length > index && nit < fArr[index]) {
                index++;
            }
        }
        if (DEBUG) {
            Slog.d(TAG, "brightness: " + brightness + ", nit: " + nit + ", index: " + index);
        }
        return index - 1;
    }

    static float getTime(int brightness) {
        float nit = convert2Nit(brightness);
        int index = getIndex(brightness);
        float a = sALevels[index];
        float b = sBLevels[index];
        float time = (MathUtils.log(nit / a) / MathUtils.log(b)) / TIME_5;
        if (DEBUG) {
            Slog.d(TAG, "brightness: " + brightness + ", time: " + time + ", a: " + a + ", b: " + b);
        }
        return MathUtils.abs(time);
    }

    static int getDarkeningRate(int brightness) {
        float nit = convert2Nit(brightness);
        int index = getIndex(brightness);
        float rate = (((float) PowerManager.BRIGHTNESS_ON) * MathUtils.abs(((sALevels[index] * TIME_5) * MathUtils.pow(sBLevels[index], getTime(brightness) * TIME_5)) * MathUtils.log(sBLevels[index]))) / sMaxNitsProperty;
        if (PowerManager.BRIGHTNESS_ON < 4095 && nit <= NIT_LEVEL) {
            rate = NIT_LEVEL;
        } else if (IS_UMI_0B_DISPLAY_PANEL && nit <= NIT_LEVEL) {
            rate = 80.0f;
        }
        if (DEBUG) {
            Slog.d(TAG, "rate: " + rate);
        }
        return (int) MathUtils.max(rate, 1.0f);
    }

    static float getExpRate(float begin, float end, float nit, float time1, float time2) {
        float a = MathUtils.log(end / begin) / (time2 - time1);
        float b = MathUtils.log(end) - (a * time2);
        return (((float) PowerManager.BRIGHTNESS_ON) * (MathUtils.exp((a * ((MathUtils.log(nit) - b) / a)) + b) * a)) / sMaxNitsProperty;
    }

    static int getBrighteningRate(int brightness, int startBrightness, int tgtBrightness) {
        float ret;
        float nit = convert2Nit(brightness);
        float startnit = convert2Nit(startBrightness);
        float tgtnit = convert2Nit(tgtBrightness);
        if (startnit < NIT_LEVEL1) {
            if (tgtnit < NIT_LEVEL1) {
                ret = ((float) (tgtBrightness - startBrightness)) / TIME_4;
            } else if (tgtnit < NIT_LEVEL3) {
                if (nit < NIT_LEVEL1) {
                    ret = ((float) (convert2Brightness(NIT_LEVEL1) - startBrightness)) / TIME_3;
                } else {
                    ret = getExpRate(NIT_LEVEL1, tgtnit, nit, TIME_3, TIME_4);
                }
            } else if (nit < NIT_LEVEL1) {
                ret = ((float) (convert2Brightness(NIT_LEVEL1) - startBrightness)) / TIME_2;
            } else if (nit < NIT_LEVEL2) {
                ret = getExpRate(NIT_LEVEL1, NIT_LEVEL2, nit, TIME_2, TIME_3);
            } else {
                ret = getExpRate(NIT_LEVEL2, tgtnit, nit, TIME_3, TIME_4);
            }
        } else if (startnit >= NIT_LEVEL3) {
            ret = getExpRate(startnit, tgtnit, nit, 0.0f, TIME_4);
        } else if (tgtnit < NIT_LEVEL3) {
            ret = getExpRate(startnit, tgtnit, nit, 0.0f, TIME_4);
        } else if (nit < NIT_LEVEL2) {
            ret = getExpRate(startnit, NIT_LEVEL2, nit, 0.0f, TIME_3);
        } else {
            ret = getExpRate(NIT_LEVEL2, tgtnit, nit, TIME_3, TIME_4);
        }
        return (int) MathUtils.max(ret, 1.0f);
    }

    static int computeRate(int rate, int currBrightness, int tgtBrightness) {
        int ret = rate;
        if (BRIGHTNESS_RATE_ADJUSTIBLE && sAutomaticBrightnessEnable && sSlowChange) {
            if (rate == 0) {
                ret = 0;
            } else if (sAmbientLuxFirstEvent) {
                ret = rate;
            } else {
                int i = sStartBrightness;
                ret = i < tgtBrightness ? getBrighteningRate(currBrightness, i, tgtBrightness) : getDarkeningRate(currBrightness);
            }
        }
        estimateAnimationDuration(tgtBrightness, rate);
        if (DEBUG) {
            Slog.d(TAG, "computeRate: " + ret + ", rate=" + rate + ", sAmbientLuxFirstEvent=" + sAmbientLuxFirstEvent + ", sStartBrightness=" + sStartBrightness + ", tgtBrightness=" + tgtBrightness + ", currBrightness=" + currBrightness);
        }
        return ret;
    }

    static int computeRate(long startTime, long time, int rate, int currBrightness, int tgtBrightness) {
        int ret = rate;
        if (sMaxNitsProperty != 0.0f) {
            return computeRate(rate, currBrightness, tgtBrightness);
        }
        if (BRIGHTNESS_RATE_ADJUSTIBLE && sAutomaticBrightnessEnable && sSlowChange) {
            ret = rate == 0 ? 0 : (sStartBrightness < tgtBrightness || sAmbientLuxFirstEvent) ? rate : ((float) currBrightness) / ((float) PowerManager.BRIGHTNESS_ON) < BRIGHTNESS_DARK ? Math.max((int) (BRIGHTNESS_DARK_RAMPRATE * ((float) PowerManager.BRIGHTNESS_ON)), 1) : Math.max((int) (((double) (sStartBrightness - tgtBrightness)) * 0.2d * ((double) MathUtils.exp((float) (((double) (time - startTime)) * -0.2d * 9.999999717180685E-10d)))), 1);
        }
        if (DEBUG) {
            Slog.d(TAG, "computeRate: " + ret + ", rate=" + rate + ", sAmbientLuxFirstEvent=" + sAmbientLuxFirstEvent + ", sStartBrightness=" + sStartBrightness + ", tgtBrightness=" + tgtBrightness + ", currBrightness=" + currBrightness + ", startTime=" + startTime + ", time=" + time);
        }
        return ret;
    }

    static void updateUnadjustedBrightness(float lux, float brightness, float unadjustedbrightness) {
        if (DEBUG) {
            Slog.d(TAG, "userLux=" + lux + ", userBrightness=" + brightness + ", unadjustedbrightness=" + unadjustedbrightness);
        }
        sUnadjustedBrightness = unadjustedbrightness;
    }

    static void smoothNewCurve(float[] lux, float[] brightness, int idx) {
        if (DEBUG) {
            Slog.d(TAG, "userLux=" + lux[idx] + ", userBrightness=" + brightness[idx] + ", unadjustedbrightness=" + sUnadjustedBrightness);
        }
        for (int i = idx + 1; i < lux.length; i++) {
            brightness[i] = brightness[i] - (sUnadjustedBrightness - brightness[idx]);
            brightness[i] = MathUtils.max(brightness[i], brightness[i - 1]);
        }
        for (int i2 = idx - 1; i2 >= 0; i2--) {
            float f = brightness[i2];
            float f2 = sUnadjustedBrightness;
            brightness[i2] = f - (((f2 - brightness[idx]) * brightness[i2]) / f2);
            brightness[i2] = MathUtils.min(brightness[i2], brightness[i2 + 1]);
        }
    }

    private static void estimateAnimationDuration(int tgtBrightness, int originalRate) {
        if (tgtBrightness != sLastTargetValue || sLastOriginalRate != originalRate) {
            if (DEBUG) {
                Slog.d(TAG, "estimateAnimationDuration: brightness: [" + sStartBrightness + ", " + tgtBrightness + "], transfer in nit: [" + convert2Nit(sStartBrightness) + ", " + convert2Nit(tgtBrightness) + "]");
            }
            sLastTargetValue = tgtBrightness;
            sLastOriginalRate = originalRate;
            if (!BRIGHTNESS_RATE_ADJUSTIBLE || !sAutomaticBrightnessEnable) {
                estimateIdealAnimationDuration(sStartBrightness, tgtBrightness, originalRate, REASON_DURATION_MANUAL);
            } else if (sAmbientLuxFirstEvent) {
                estimateIdealAnimationDuration(sStartBrightness, tgtBrightness, originalRate, REASON_DURATION_FIRST_LUX);
            } else if (sStartBrightness < tgtBrightness) {
                estimateBrightenAnimationDuration(tgtBrightness);
            } else {
                estimateDarkenAnimationDuration(tgtBrightness);
            }
        }
    }

    private static void estimateIdealAnimationDuration(int sStartBrightness2, int tgtBrightness, int rate, String reason) {
        int maxDuration = PowerManager.BRIGHTNESS_ON < 4095 ? 850 : 1700;
        updateSlideAnimationDuration(MathUtils.constrain(calculateSlideBarDuration(sStartBrightness2, tgtBrightness, (float) rate), maxDuration / 2, maxDuration), reason);
    }

    private static void estimateBrightenAnimationDuration(int targetBrightness) {
        int i = sStartBrightness;
        if (i < targetBrightness) {
            float startNit = convert2Nit(i);
            float targetNit = convert2Nit(targetBrightness);
            List<RateSegmentMapping> rateSegmentMappings = new ArrayList<>();
            if (startNit < NIT_LEVEL1) {
                if (targetNit < NIT_LEVEL1) {
                    int i2 = sStartBrightness;
                    rateSegmentMappings.add(new RateSegmentMapping(i2, targetBrightness, ((float) (targetBrightness - i2)) / TIME_4));
                } else if (targetNit < NIT_LEVEL3) {
                    int convert2Brightness = convert2Brightness(NIT_LEVEL1);
                    int i3 = sStartBrightness;
                    rateSegmentMappings.add(new RateSegmentMapping(i3, convert2Brightness(NIT_LEVEL1), ((float) (convert2Brightness - i3)) / TIME_3));
                    rateSegmentMappings.add(new RateSegmentMapping((float) NIT_LEVEL1, targetNit, getExpRate(NIT_LEVEL1, targetNit, (targetNit + NIT_LEVEL1) / ASSISTSENSOR_BRIGHTENINGRATIO, TIME_3, TIME_4)));
                } else {
                    int convert2Brightness2 = convert2Brightness(NIT_LEVEL1);
                    int i4 = sStartBrightness;
                    rateSegmentMappings.add(new RateSegmentMapping(i4, convert2Brightness(NIT_LEVEL1), ((float) (convert2Brightness2 - i4)) / TIME_2));
                    rateSegmentMappings.add(new RateSegmentMapping((float) NIT_LEVEL1, (float) NIT_LEVEL2, getExpRate(NIT_LEVEL1, NIT_LEVEL2, 61.225002f, 0.0f, TIME_3)));
                    rateSegmentMappings.add(new RateSegmentMapping((float) NIT_LEVEL2, targetNit, getExpRate(NIT_LEVEL2, targetNit, (targetNit + NIT_LEVEL2) / ASSISTSENSOR_BRIGHTENINGRATIO, TIME_3, TIME_4)));
                }
            } else if (startNit >= NIT_LEVEL3) {
                rateSegmentMappings.add(new RateSegmentMapping(convert2Brightness(startNit), convert2Brightness(targetNit), getExpRate(startNit, targetNit, (startNit + targetNit) / ASSISTSENSOR_BRIGHTENINGRATIO, 0.0f, TIME_4)));
            } else if (targetNit < NIT_LEVEL3) {
                rateSegmentMappings.add(new RateSegmentMapping(startNit, targetNit, getExpRate(startNit, targetNit, (startNit + targetNit) / ASSISTSENSOR_BRIGHTENINGRATIO, 0.0f, TIME_4)));
            } else {
                float largerValue = startNit > NIT_LEVEL2 ? startNit : 87.450005f;
                if (startNit < NIT_LEVEL2) {
                    rateSegmentMappings.add(new RateSegmentMapping(startNit, (float) NIT_LEVEL2, getExpRate(startNit, NIT_LEVEL2, (startNit + NIT_LEVEL2) / ASSISTSENSOR_BRIGHTENINGRATIO, 0.0f, TIME_3)));
                }
                RateSegmentMapping mapping = new RateSegmentMapping(largerValue, targetNit, getExpRate(largerValue, targetNit, (largerValue + targetNit) / ASSISTSENSOR_BRIGHTENINGRATIO, TIME_3, TIME_4));
                rateSegmentMappings.add(mapping);
                RateSegmentMapping rateSegmentMapping = mapping;
            }
            sumUpAutoBrightnessDuration(rateSegmentMappings, true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0090 A[LOOP:0: B:3:0x001f->B:22:0x0090, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0093 A[EDGE_INSN: B:24:0x0093->B:23:0x0093 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void estimateDarkenAnimationDuration(int r14) {
        /*
            int r0 = sStartBrightness
            if (r0 <= r14) goto L_0x0097
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = sStartBrightness
            float r1 = convert2Nit(r1)
            float r2 = convert2Nit(r14)
            int r3 = sStartBrightness
            int r3 = getIndex(r3)
            int r4 = getIndex(r14)
            r5 = r1
            r6 = r3
        L_0x001f:
            if (r6 > r4) goto L_0x0093
            int r7 = r6 + 1
            if (r7 >= r4) goto L_0x002e
            float[] r8 = sNitsLevels
            int r9 = r8.length
            if (r7 < r9) goto L_0x002b
            goto L_0x002e
        L_0x002b:
            r8 = r8[r7]
            goto L_0x002f
        L_0x002e:
            r8 = r2
        L_0x002f:
            float[] r9 = sALevels
            r9 = r9[r6]
            r10 = 1103101952(0x41c00000, float:24.0)
            float r9 = r9 * r10
            float[] r11 = sBLevels
            r11 = r11[r6]
            float r12 = r5 + r8
            r13 = 1073741824(0x40000000, float:2.0)
            float r12 = r12 / r13
            int r12 = convert2Brightness(r12)
            float r12 = getTime(r12)
            float r12 = r12 * r10
            float r10 = android.util.MathUtils.pow(r11, r12)
            float r9 = r9 * r10
            float[] r10 = sBLevels
            r10 = r10[r6]
            float r10 = android.util.MathUtils.log(r10)
            float r9 = r9 * r10
            float r9 = android.util.MathUtils.abs(r9)
            int r10 = android.os.PowerManager.BRIGHTNESS_ON
            float r10 = (float) r10
            float r9 = r9 * r10
            float r10 = sMaxNitsProperty
            float r9 = r9 / r10
            int r10 = android.os.PowerManager.BRIGHTNESS_ON
            r11 = 4095(0xfff, float:5.738E-42)
            r12 = 1109393408(0x42200000, float:40.0)
            if (r10 >= r11) goto L_0x0074
            float[] r10 = sNitsLevels
            r10 = r10[r6]
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 > 0) goto L_0x0074
            r9 = 1109393408(0x42200000, float:40.0)
            goto L_0x0082
        L_0x0074:
            boolean r10 = IS_UMI_0B_DISPLAY_PANEL
            if (r10 == 0) goto L_0x0082
            float[] r10 = sNitsLevels
            r10 = r10[r6]
            int r10 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r10 > 0) goto L_0x0082
            r9 = 1117782016(0x42a00000, float:80.0)
        L_0x0082:
            com.android.server.display.AutomaticBrightnessControllerInjector$RateSegmentMapping r10 = new com.android.server.display.AutomaticBrightnessControllerInjector$RateSegmentMapping
            r10.<init>((float) r5, (float) r8, (float) r9)
            r0.add(r10)
            r5 = r8
            int r11 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r11 != 0) goto L_0x0090
            goto L_0x0093
        L_0x0090:
            int r6 = r6 + 1
            goto L_0x001f
        L_0x0093:
            r6 = 0
            sumUpAutoBrightnessDuration(r0, r6)
        L_0x0097:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.AutomaticBrightnessControllerInjector.estimateDarkenAnimationDuration(int):void");
    }

    private static void sumUpAutoBrightnessDuration(List<RateSegmentMapping> mappings, boolean brighten) {
        int duration = 0;
        for (RateSegmentMapping mapping : mappings) {
            if (DEBUG) {
                Slog.d(TAG, "sumUpAutoBrightnessDuration: mapping.begin:" + mapping.beginBrightness + ", mapping.end: " + mapping.endBrightness + ", mapping.rate: " + mapping.rate);
            }
            duration += calculateSlideBarDuration(mapping.beginBrightness, mapping.endBrightness, mapping.rate);
        }
        updateSlideAnimationDuration(duration > 0 ? duration : DEFAULT_SLIDER_DURATION, brighten ? REASON_DURATION_AUTO_BRIGHTEN : REASON_DURATION_AUTO_DARKEN);
    }

    private static void updateSlideAnimationDuration(int duration, String reason) {
        Slog.d(TAG, "updateSlideAnimationDuration: reason: " + reason + ", new duration: " + duration);
        StateChangeCallback stateChangeCallback = sStateChangeCallback;
        if (stateChangeCallback != null) {
            stateChangeCallback.onSliderDurationChanged(duration);
        }
    }

    private static int calculateSlideBarDuration(int startBrightness, int endBrightness, float temporaryRate) {
        if (temporaryRate > 0.0f) {
            return (int) ((MathUtils.abs((float) (startBrightness - endBrightness)) / (temporaryRate * 0.016f)) * 0.016f * 1000.0f);
        }
        return 0;
    }

    private static class RateSegmentMapping {
        int beginBrightness;
        int endBrightness;
        float rate;

        public RateSegmentMapping(int beginBrightness2, int endBrightness2, float rate2) {
            this.beginBrightness = beginBrightness2;
            this.endBrightness = endBrightness2;
            this.rate = rate2;
        }

        public RateSegmentMapping(float beginBrightNit, float endBrightnessNit, float rate2) {
            this.beginBrightness = AutomaticBrightnessControllerInjector.convert2Brightness(beginBrightNit);
            this.endBrightness = AutomaticBrightnessControllerInjector.convert2Brightness(endBrightnessNit);
            this.rate = rate2;
        }
    }

    static void setStateChangeCallback(StateChangeCallback listener) {
        sStateChangeCallback = listener;
    }

    static boolean dump(PrintWriter pw) {
        DEBUG = isDebuggable();
        pw.println("  USE_PROXIMITY_ENABLED=" + USE_PROXIMITY_ENABLED);
        pw.println("  DARKENING_HYSTERESIS_ZERO2FOV=" + DARKENING_HYSTERESIS_ZERO2FOV);
        pw.println("  DARKENING_HYSTERESIS_FOV2SEVENTY=" + DARKENING_HYSTERESIS_FOV2SEVENTY);
        pw.println("  DARKENING_HYSTERESIS_SEVENTY2NINETY=" + DARKENING_HYSTERESIS_SEVENTY2NINETY);
        pw.println("  ALS_FOV=" + ALS_FOV);
        pw.println("  HIGHANGLE_THRESHOLD=" + HIGHANGLE_THRESHOLD);
        pw.println("  SKIP_DEBOUNCE_ENABLED=" + SKIP_DEBOUNCE_ENABLED);
        pw.println("  SKIP_DEBOUNCE=" + SKIP_DEBOUNCE);
        pw.println("  SKIP_LUX_DEBOUNCE1=" + SKIP_LUX_DEBOUNCE1);
        pw.println("  SKIP_LUX_DEBOUNCE2=" + SKIP_LUX_DEBOUNCE2);
        pw.println("  USE_ACCELEROMETER_ENABLED=" + USE_ACCELEROMETER_ENABLED);
        pw.println("  BACKLIGHT_BIT=" + DeviceFeature.BACKLIGHT_BIT);
        pw.println("  BRIGHTNESS_BRIGHTEN_RATE=" + BRIGHTNESS_BRIGHTEN_RATE);
        pw.println("  BRIGHTNESS_DARK_THREHOLD=" + BRIGHTNESS_DARK_THREHOLD);
        pw.println("  BRIGHTNESS_DARKTIME_1SECOND=" + BRIGHTNESS_DARKTIME_1SECOND);
        pw.println("  BRIGHTNESS_DARKTIME_2SECOND=" + BRIGHTNESS_DARKTIME_2SECOND);
        pw.println("  AUTOBRIGHTNESS_RATE_SLOW=" + AUTOBRIGHTNESS_RATE_SLOW);
        pw.println("  BRIGHTNESS_RATE_ADJUSTIBLE=" + BRIGHTNESS_RATE_ADJUSTIBLE);
        pw.println("  BRIGHTNESS_DARK=" + BRIGHTNESS_DARK);
        pw.println("  BRIGHTNESS_DARK_RAMPRATE=" + BRIGHTNESS_DARK_RAMPRATE);
        pw.println("  SLOW_RATIO=" + SLOW_RATIO);
        pw.println("  WAIT_FOR_AUTOBRIGHTNESS=" + WAIT_FOR_AUTOBRIGHTNESS);
        pw.println("  WAIT_TIME=" + WAIT_TIME);
        pw.println("  MAX_NIT=" + sMaxNitsProperty);
        AutomaticBrightnessTouchHelper.dump(pw, DEBUG);
        return isDebuggable();
    }

    static boolean isDebuggable() {
        return SystemProperties.getBoolean(AUTO_BRIGHTNESS_DEBUG, false);
    }

    public static void recordAutoBrightnessChange(int brightness) {
        long now = System.currentTimeMillis();
        int i = sLastBrightness;
        if (i == -1) {
            sLastBrightness = brightness;
            sEvents.add(new BrightnessEvent(now, brightness));
            return;
        }
        if (Math.abs(brightness - i) >= 20) {
            MQSEventManagerDelegate.getInstance().reportBrightnessEvent(sLastBrightness, brightness, 1, "");
            sEvents.clear();
        } else {
            BrightnessEvent earliestEvent = sEvents.peekFirst();
            if (earliestEvent == null) {
                sEvents.add(new BrightnessEvent(now, brightness));
            } else if (now - earliestEvent.time > 10000) {
                while (earliestEvent != null && now - earliestEvent.time > 10000) {
                    sEvents.pop();
                    earliestEvent = sEvents.peekFirst();
                }
                sEvents.add(new BrightnessEvent(now, brightness));
            } else if (Math.abs(brightness - earliestEvent.brightness) >= 30) {
                MQSEventManagerDelegate.getInstance().reportBrightnessEvent(earliestEvent.brightness, brightness, 1, "");
                sEvents.clear();
            } else {
                sEvents.add(new BrightnessEvent(now, brightness));
            }
        }
        sLastBrightness = brightness;
    }

    private static class BrightnessEvent {
        int brightness;
        long time;

        BrightnessEvent(long time2, int brightness2) {
            this.time = time2;
            this.brightness = brightness2;
        }

        public String toString() {
            return "[" + this.time + "," + this.brightness + "]";
        }
    }

    private static float[] getFloatArray(TypedArray array) {
        int length = array.length();
        float[] floatArray = new float[length];
        for (int i = 0; i < length; i++) {
            floatArray[i] = array.getFloat(i, Float.NaN);
        }
        array.recycle();
        return floatArray;
    }

    public static void saveCurrentUserDataPoint(float lux, float brightness) {
        if (sUserDataPoint.containsKey(Integer.valueOf(sCurrentUserId))) {
            UserDataPoint currentUserDataPoint = getUserDataPoint();
            currentUserDataPoint.setUserLux(lux);
            currentUserDataPoint.setUserBrightness(brightness);
            return;
        }
        sUserDataPoint.put(Integer.valueOf(sCurrentUserId), new UserDataPoint(lux, brightness));
    }

    public static void resetCurrentUserDataPoint() {
        if (sUserDataPoint.containsKey(Integer.valueOf(sCurrentUserId))) {
            UserDataPoint currentUserDataPoint = getUserDataPoint();
            currentUserDataPoint.setUserLux(-1.0f);
            currentUserDataPoint.setUserBrightness(-1.0f);
        }
    }

    public static UserDataPoint getUserDataPoint() {
        return sUserDataPoint.get(Integer.valueOf(sCurrentUserId));
    }

    public static int getCurrentUserId() {
        return sCurrentUserId;
    }

    public static void setCurrentUserId(int currentUserId) {
        sCurrentUserId = currentUserId;
    }
}
