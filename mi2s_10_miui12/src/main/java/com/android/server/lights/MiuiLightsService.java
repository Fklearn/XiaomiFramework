package com.android.server.lights;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManagerInternal;
import android.database.ContentObserver;
import android.hardware.display.DisplayManagerGlobal;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.miui.R;
import android.net.Uri;
import android.os.BatteryManagerInternal;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.util.Slog;
import com.android.internal.util.CollectionUtils;
import com.android.server.LocalServices;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.inputmethod.MiuiSecurityInputMethodHelper;
import com.android.server.lights.LightsService;
import com.android.server.lights.MiuiLightsService;
import com.android.server.lights.VisualizerHolder;
import com.android.server.notification.NotificationManagerService;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import miui.lights.ILightsManager;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.os.DeviceFeature;
import miui.util.FeatureParser;
import org.json.JSONObject;

public class MiuiLightsService extends LightsService {
    private static final long LED_END_WORKTIME_DEF = 82800000;
    private static final long LED_START_WORKTIME_DEF = 25200000;
    public static final int LIGHT_ID_COLORFUL = 8;
    public static final int LIGHT_ID_MUSIC = 9;
    private static final int LIGHT_ON_MS = 500;
    private static final long ONE_DAY = 86400000;
    private static final long ONE_HOUR = 3600000;
    private static final long ONE_MINUTE = 60000;
    private static final int STOP_FLASH_MSG = 1;
    private static MiuiLightsService sInstance;
    private ArrayList<DataCaptureListener> dataCaptureListeners = null;
    /* access modifiers changed from: private */
    public long light_end_time = 86400000;
    /* access modifiers changed from: private */
    public long light_start_time = 0;
    /* access modifiers changed from: private */
    public AudioManager mAudioManager;
    private final AudioManagerPlaybackListener mAudioManagerPlaybackCb = new AudioManagerPlaybackListener();
    /* access modifiers changed from: private */
    public BatteryManagerInternal mBatteryManagerInternal;
    /* access modifiers changed from: private */
    public final LightImpl mColorfulLight;
    /* access modifiers changed from: private */
    public Context mContext;
    private VisualizerHolder.OnDataCaptureListener mDataCaptureListener;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ((LightImpl) msg.obj).turnOff();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsLedTurnOn;
    private boolean mIsWorkTime = true;
    private LedDataCaptureListener mLedDataCaptureListener;
    /* access modifiers changed from: private */
    public List<String> mLedEvents;
    private LightContentObserver mLightContentObserver;
    /* access modifiers changed from: private */
    public Handler mLightHandler;
    /* access modifiers changed from: private */
    public final LightStyleLoader mLightStyleLoader;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final LightImpl mMusicLight;
    /* access modifiers changed from: private */
    public PackageManagerInternal mPackageManagerInt;
    /* access modifiers changed from: private */
    public int mPlayingPid = -1;
    private final LinkedList<LightState> mPreviousLights;
    private final int mPreviousLightsLimit = 100;
    /* access modifiers changed from: private */
    public ContentResolver mResolver;
    private final IBinder mService = new ILightsManager.Stub() {
        public void setColorfulLight(String callingPackage, int styleType, int userId) throws RemoteException {
            MiuiLightsService.this.checkCallerVerify(callingPackage);
            MiuiLightsService.this.mLightHandler.post(new Runnable(callingPackage, styleType) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    MiuiLightsService.AnonymousClass2.this.lambda$setColorfulLight$0$MiuiLightsService$2(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$setColorfulLight$0$MiuiLightsService$2(String callingPackage, int styleType) {
            Slog.d("LightsService", "callingPackage: " + callingPackage + " mLastLightStyle: " + styleType);
            List<LightState> lightStyle = MiuiLightsService.this.mLightStyleLoader.getLightStyle(styleType);
            synchronized (MiuiLightsService.this.mLock) {
                MiuiLightsService.this.mColorfulLight.setColorfulLightLocked(callingPackage, Binder.getCallingUid(), styleType, lightStyle);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mSupportButtonLight;
    private boolean mSupportColorfulLed;
    /* access modifiers changed from: private */
    public boolean mSupportLedLight;
    private boolean mSupportLedSchedule;
    /* access modifiers changed from: private */
    public boolean mSupportTapFingerprint;
    private boolean mSupportVirtualLed;
    /* access modifiers changed from: private */
    public LightsThread mThread;
    /* access modifiers changed from: private */
    public final WorkSource mTmpWorkSource = new WorkSource();
    /* access modifiers changed from: private */
    public final PowerManager.WakeLock mWakeLock;

    public interface DataCaptureListener {
        void onFrequencyCapture(Context context, int i, float[] fArr);

        void onSetLightCallback(Context context, int i, int i2, int i3, int i4, int i5, int i6);
    }

    /* JADX WARNING: type inference failed for: r2v1, types: [com.android.server.lights.MiuiLightsService$2, android.os.IBinder] */
    public MiuiLightsService(Context context) {
        super(context);
        this.mContext = context;
        this.mResolver = this.mContext.getContentResolver();
        this.mLightStyleLoader = new LightStyleLoader(context);
        this.mColorfulLight = new LightImpl(this.mContext, 8);
        this.mMusicLight = new LightImpl(this.mContext, 9);
        this.mLights[2] = new LightImpl(this.mContext, 2);
        this.mLights[4] = new LightImpl(this.mContext, 4);
        this.mLights[3] = new LightImpl(this.mContext, 3);
        this.mLights[0] = new LightImpl(this.mContext, 0);
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "*lights*");
        this.mWakeLock.setReferenceCounted(true);
        HandlerThread miuiLightsHandlerThread = new HandlerThread("MiuiLightsHandlerThread");
        miuiLightsHandlerThread.start();
        this.mLightHandler = new Handler(miuiLightsHandlerThread.getLooper());
        this.mPreviousLights = new LinkedList<>();
    }

    public void onStart() {
        super.onStart();
        sInstance = this;
    }

    public void onBootPhase(int phase) {
        if (phase == 1000) {
            this.mSupportLedLight = FeatureParser.getBoolean("support_led_light", true);
            this.mSupportButtonLight = FeatureParser.getBoolean("support_button_light", true);
            this.mSupportTapFingerprint = FeatureParser.getBoolean("support_tap_fingerprint_sensor_to_home", false);
            this.mSupportColorfulLed = FeatureParser.getBoolean("support_led_colorful", false);
            this.mSupportLedSchedule = FeatureParser.getBoolean("support_led_schedule", false);
            this.mSupportVirtualLed = FeatureParser.getBoolean("support_virtual_led", false);
            this.mLightContentObserver = new LightContentObserver();
            this.mLightContentObserver.observe();
            this.mBatteryManagerInternal = (BatteryManagerInternal) getLocalService(BatteryManagerInternal.class);
            Settings.Secure.putIntForUser(this.mResolver, "screen_buttons_state", 0, -2);
            if (this.mSupportButtonLight) {
                ((LightImpl) this.mLights[2]).updateLight();
            }
            if (this.mSupportLedLight) {
                ((LightImpl) this.mLights[3]).updateLight();
            }
            this.mContext.registerReceiver(new UserSwitchReceiver(), new IntentFilter("android.intent.action.USER_SWITCHED"), (String) null, this.mLightHandler);
            this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
            this.mPackageManagerInt = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            registerAudioPlaybackCallback();
            if (this.mSupportLedSchedule) {
                this.light_end_time = Settings.Secure.getLongForUser(this.mResolver, "light_turn_on_endTime", LED_END_WORKTIME_DEF, -2);
                this.light_start_time = Settings.Secure.getLongForUser(this.mResolver, "light_turn_on_startTime", LED_START_WORKTIME_DEF, -2);
                updateWorkState();
                this.mContext.registerReceiver(new TimeTickReceiver(), new IntentFilter("android.intent.action.TIME_TICK"), (String) null, this.mLightHandler);
            }
            if (this.mSupportVirtualLed) {
                this.mLedDataCaptureListener = new LedDataCaptureListener(this.mContext, Looper.getMainLooper());
                addDataCaptureListener(this.mLedDataCaptureListener);
            }
        }
    }

    public class LightImpl extends LightsService.LightImpl {
        private List<LightState> lightStates;
        /* access modifiers changed from: private */
        public int mBrightnessMode;
        /* access modifiers changed from: private */
        public int mColor;
        /* access modifiers changed from: private */
        public boolean mDisabled;
        /* access modifiers changed from: private */
        public int mId;
        private boolean mIsShutDown;
        private int mLastColor;
        public int mLastLightStyle;
        /* access modifiers changed from: private */
        public int mMode;
        /* access modifiers changed from: private */
        public int mOffMS;
        /* access modifiers changed from: private */
        public int mOnMS;
        private int mUid;
        private String pkg_name;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        private LightImpl(Context context, int id) {
            super((LightsService) MiuiLightsService.this, context, id, 0);
            boolean z = false;
            this.mLastLightStyle = -1;
            this.mIsShutDown = false;
            this.mId = id;
            int i = this.mId;
            this.mDisabled = (i == 2 || i == 3) ? true : z;
        }

        /* access modifiers changed from: package-private */
        public void setFlashing(String colorSettingKey, String freqSettingKey) {
            setFlashing(Settings.System.getIntForUser(MiuiLightsService.this.mResolver, colorSettingKey, MiuiLightsService.this.mContext.getResources().getColor(R.color.android_config_defaultNotificationColor), -2), 1, 500, 0);
            MiuiLightsService.this.mHandler.removeMessages(1);
            MiuiLightsService.this.mHandler.sendMessageDelayed(Message.obtain(MiuiLightsService.this.mHandler, 1, this), 500);
        }

        /* access modifiers changed from: package-private */
        public void updateLight() {
            int i = this.mId;
            if (8 == i) {
                synchronized (MiuiLightsService.this.mLock) {
                    setColorfulLightLocked(this.pkg_name, this.mUid, this.mLastLightStyle, this.lightStates);
                }
                return;
            }
            boolean z = true;
            if (2 == i) {
                if (!MiuiLightsService.this.isDisableButtonLight() && MiuiLightsService.this.isTurnOnButtonLight()) {
                    z = false;
                }
                this.mDisabled = z;
            } else if (3 == i) {
                this.mDisabled = !MiuiLightsService.this.isTurnOnBatteryLight();
            } else if (4 == i) {
                this.mDisabled = !MiuiLightsService.this.isTurnOnNotificationLight();
            } else if (9 == i) {
                this.mDisabled = !MiuiLightsService.this.isTurnOnMusicLight();
            }
            synchronized (MiuiLightsService.this.mLock) {
                setLightLocked(this.mColor, this.mMode, this.mOnMS, this.mOffMS, this.mBrightnessMode);
            }
        }

        /* access modifiers changed from: package-private */
        public void setLightLocked(int color, int mode, int onMS, int offMS, int brightnessMode) {
            if (this.mId == 0) {
                realSetLightLocked(color, mode, onMS, offMS, brightnessMode);
            } else if (!MiuiLightsService.this.isLightEnable() || this.mDisabled) {
                updateState(color, mode, onMS, offMS, brightnessMode);
                realSetLightLocked(0, 0, 0, 0, 0);
            } else {
                int i = this.mId;
                if (i == 3 || i == 4 || i == 9) {
                    if (MiuiLightsService.this.mColorfulLight.mLastLightStyle == 1) {
                        return;
                    }
                    if (this.mId != 9 && MiuiLightsService.this.isMusicLightPlaying()) {
                        return;
                    }
                    if (MiuiLightsService.this.isSceneUncomfort(this.mId)) {
                        updateState(color, mode, onMS, offMS, brightnessMode);
                        realSetLightLocked(0, 0, 0, 0, 0);
                        return;
                    } else if (this.mId == 3 && MiuiLightsService.this.mBatteryManagerInternal != null) {
                        if (MiuiLightsService.this.mBatteryManagerInternal.getBatteryLevel() == 100) {
                            color = 0;
                        }
                        if (!MiuiLightsService.this.mBatteryManagerInternal.isPowered(7) && color != 0) {
                            color = 0;
                        }
                    }
                }
                int i2 = this.mId;
                if (i2 == 4) {
                    if (color != 0 && this.mColor == 0) {
                        MiuiLightsService.this.turnoffBatteryLight();
                    }
                } else if (i2 == 3 && !(((LightImpl) MiuiLightsService.this.mLights[4]).mColor == 0 && MiuiLightsService.this.mColorfulLight.mLastLightStyle == -1)) {
                    updateState(color, mode, onMS, offMS, brightnessMode);
                    return;
                }
                int i3 = color;
                int i4 = mode;
                int i5 = onMS;
                int i6 = offMS;
                int i7 = brightnessMode;
                updateState(i3, i4, i5, i6, i7);
                realSetLightLocked(i3, i4, i5, i6, i7);
                if (this.mId == 4 && this.mColor == 0) {
                    MiuiLightsService.this.recoveryBatteryLight();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setColorfulLightLocked(String pkg_name2, int mUid2, int styleType, List<LightState> lightStates2) {
            if (this.mId != 8 || lightStates2 == null) {
                Slog.e("LightsService", "Illegal Argument mLastLightStyle:" + styleType + " lightStates:" + lightStates2);
            } else if (!MiuiLightsService.this.isLightEnable()) {
                updateState(pkg_name2, mUid2, styleType, lightStates2);
                MiuiLightsService.this.doCancelColorfulLightLocked();
            } else if (MiuiLightsService.this.isSceneUncomfort(this.mId)) {
                Slog.i("LightsService", "Scene is uncomfort , lightstyle phone skip");
            } else if (this.mLastLightStyle != 1 && !lightStates2.isEmpty()) {
                MiuiLightsService.this.reportLedEventLocked(styleType, true, 0, 0);
                MiuiLightsService.this.doCancelColorfulLightLocked();
                MiuiLightsService miuiLightsService = MiuiLightsService.this;
                LightsThread unused = miuiLightsService.mThread = new LightsThread(lightStates2, styleType, mUid2);
                MiuiLightsService.this.mThread.start();
                updateState(pkg_name2, mUid2, styleType, lightStates2);
                MiuiLightsService.this.addToLightCollectionLocked(new LightState(pkg_name2, styleType));
            } else if (styleType == -1) {
                updateState(pkg_name2, mUid2, styleType, lightStates2);
                MiuiLightsService.this.doCancelColorfulLightLocked();
                MiuiLightsService.this.recoveryBatteryLight();
            }
        }

        public void setBrightness(int brightness, boolean isShutDown) {
            this.mIsShutDown = isShutDown;
            super.setBrightness(brightness);
        }

        /* access modifiers changed from: private */
        public void realSetLightLocked(int color, int mode, int onMS, int offMS, int brightnessMode) {
            if (this.mIsShutDown) {
                color = 0;
            }
            MiuiLightsService miuiLightsService = MiuiLightsService.this;
            miuiLightsService.notifySetLightCallback(miuiLightsService.getContext(), this.mId, color, mode, onMS, offMS, brightnessMode);
            int i = this.mId;
            if (i == 8 || i == 9) {
                LightsService.setLight_native(4, color, mode, onMS, offMS, brightnessMode);
                return;
            }
            if ((i == 3 || i == 4) && this.mLastColor != color) {
                Slog.v("LightsService", "realSetLightLocked #" + this.mId + ": color=#" + Integer.toHexString(color) + ": onMS=" + onMS + " offMS=" + offMS + " mode=" + mode);
                MiuiLightsService.this.addToLightCollectionLocked(new LightState(this.mId, color, mode, onMS, offMS, brightnessMode));
                if (MiuiLightsService.this.mLedEvents == null) {
                    List unused = MiuiLightsService.this.mLedEvents = new ArrayList();
                }
                if (color == 0) {
                    if (MiuiLightsService.this.mIsLedTurnOn) {
                        boolean unused2 = MiuiLightsService.this.mIsLedTurnOn = false;
                        MiuiLightsService.this.reportLedEventLocked(this.mId, false, onMS, offMS);
                    }
                } else if (!MiuiLightsService.this.mIsLedTurnOn) {
                    boolean unused3 = MiuiLightsService.this.mIsLedTurnOn = true;
                    MiuiLightsService.this.reportLedEventLocked(this.mId, true, onMS, offMS);
                }
            }
            super.setLightLocked(color, mode, onMS, offMS, brightnessMode);
            this.mLastColor = color;
        }

        private void updateState(int color, int mode, int onMS, int offMS, int brightnessMode) {
            this.mColor = color;
            this.mMode = mode;
            this.mOnMS = onMS;
            this.mOffMS = offMS;
            this.mBrightnessMode = brightnessMode;
        }

        private void updateState(String pkg_name2, int mUid2, int styleType, List<LightState> lightStates2) {
            this.pkg_name = pkg_name2;
            this.mUid = mUid2;
            this.mLastLightStyle = styleType;
            this.lightStates = lightStates2;
        }

        public String toString() {
            return "LightImpl{mDisabled=" + this.mDisabled + ", mColor=" + this.mColor + ", mMode=" + this.mMode + ", mOnMS=" + this.mOnMS + ", mOffMS=" + this.mOffMS + ", mBrightnessMode=" + this.mBrightnessMode + ", mId=" + this.mId + ", mLastColor=" + this.mLastColor + ", pkg_name='" + this.pkg_name + '\'' + ", mUid=" + this.mUid + ", mLastLightStyle=" + this.mLastLightStyle + ", mIsShutDown=" + this.mIsShutDown + '}';
        }
    }

    /* access modifiers changed from: private */
    public void reportLedEventLocked(int mId, boolean isTurnOn, int onMS, int offMs) {
        JSONObject info = new JSONObject();
        try {
            info.put(DatabaseHelper.SoundModelContract.KEY_TYPE, String.valueOf(mId));
            info.put("isTurnOn", String.valueOf(isTurnOn ? 1 : 0));
            info.put("onMs", String.valueOf(onMS));
            info.put("offMs", String.valueOf(offMs));
            info.put(SplitScreenReporter.STR_DEAL_TIME, String.valueOf(System.currentTimeMillis()));
            if (this.mLedEvents == null) {
                this.mLedEvents = new ArrayList();
            }
            this.mLedEvents.add(info.toString());
            if (this.mLedEvents.size() >= 30) {
                MQSEventManagerDelegate.getInstance().reportEvents("led", this.mLedEvents, false);
                this.mLedEvents.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MiuiLightsService getInstance() {
        return sInstance;
    }

    public IBinder getBinderService() {
        return this.mService;
    }

    private class LightsThread extends Thread {
        private final int LOOP_LIMIT = 35;
        private final List<LightState> lightStateList;
        private int loop_index = 0;
        private boolean mForceStop;
        private final int styleType;

        public LightsThread(List<LightState> lightStateList2, int styleType2, int mUid) {
            this.lightStateList = lightStateList2;
            this.styleType = styleType2;
            MiuiLightsService.this.mTmpWorkSource.set(mUid);
            MiuiLightsService.this.mWakeLock.setWorkSource(MiuiLightsService.this.mTmpWorkSource);
        }

        public void run() {
            Process.setThreadPriority(-8);
            MiuiLightsService.this.mWakeLock.acquire();
            try {
                MiuiLightsService.this.turnoffBatteryLight();
                if (playLight(this.styleType)) {
                    MiuiLightsService.this.recoveryBatteryLight();
                    MiuiLightsService.this.reportLedEventLocked(this.styleType, false, 0, 0);
                }
            } finally {
                MiuiLightsService.this.mWakeLock.release();
            }
        }

        public boolean playLight(int styleType2) {
            synchronized (this) {
                int size = this.lightStateList.size();
                int index = 0;
                while (!this.mForceStop) {
                    if (index < size) {
                        int index2 = index + 1;
                        LightState lightState = this.lightStateList.get(index);
                        MiuiLightsService.this.mColorfulLight.realSetLightLocked(lightState.colorARGB, lightState.flashMode, lightState.onMS, lightState.offMS, lightState.brightnessMode);
                        delayLocked((long) (lightState.onMS + lightState.offMS));
                        index = index2;
                    } else if (styleType2 != 1 || this.loop_index >= 35) {
                        cancel();
                        MiuiLightsService.this.mColorfulLight.realSetLightLocked(0, 0, 0, 0, 0);
                        MiuiLightsService.this.mColorfulLight.mLastLightStyle = -1;
                    } else {
                        index = 0;
                        this.loop_index++;
                    }
                }
            }
            return this.mForceStop;
        }

        private long delayLocked(long duration) {
            long durationRemaining = duration;
            if (duration <= 0) {
                return 0;
            }
            long bedtime = SystemClock.uptimeMillis() + duration;
            do {
                try {
                    wait(durationRemaining);
                } catch (InterruptedException e) {
                }
                if (this.mForceStop) {
                    break;
                }
                durationRemaining = bedtime - SystemClock.uptimeMillis();
            } while (durationRemaining > 0);
            return duration - durationRemaining;
        }

        public void cancel() {
            synchronized (this) {
                MiuiLightsService.this.mThread.mForceStop = true;
                MiuiLightsService.this.mThread.notifyAll();
            }
        }
    }

    /* access modifiers changed from: private */
    public void doCancelColorfulLightLocked() {
        LightsThread lightsThread = this.mThread;
        if (lightsThread != null) {
            lightsThread.cancel();
            this.mThread = null;
            LightImpl lightImpl = this.mColorfulLight;
            lightImpl.mLastLightStyle = -1;
            lightImpl.realSetLightLocked(0, 0, 0, 0, 0);
        }
    }

    private final class AudioManagerPlaybackListener extends AudioManager.AudioPlaybackCallback {
        private AudioManagerPlaybackListener() {
        }

        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) {
            AudioPlaybackConfiguration config;
            super.onPlaybackConfigChanged(configs);
            if (!MiuiLightsService.this.isTurnOnMusicLight() || !MiuiLightsService.this.isLightEnable() || DisplayManagerGlobal.getInstance().getWifiDisplayStatus().getActiveDisplayState() == 2) {
                MiuiLightsService.this.releaseVisualizer();
                return;
            }
            Iterator<AudioPlaybackConfiguration> it = configs.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                config = it.next();
                if (config.getPlayerState() == 2) {
                    if (VisualizerHolder.getInstance().isAllowed(ExtraActivityManagerService.getProcessNameByPid(config.getClientPid()))) {
                        int unused = MiuiLightsService.this.mPlayingPid = config.getClientPid();
                        MiuiLightsService.this.turnoffBatteryLight();
                        synchronized (MiuiLightsService.this.mLock) {
                            MiuiLightsService.this.addToLightCollectionLocked(new LightState(MiuiLightsService.this.mPackageManagerInt.getNameForUid(config.getClientUid()), 3));
                        }
                        VisualizerHolder.getInstance().setOnDataCaptureListener(MiuiLightsService.this.getDataCaptureListener());
                    } else {
                        return;
                    }
                } else if (!(config.getClientPid() == MiuiLightsService.this.mPlayingPid && (config.getPlayerState() == 3 || config.getPlayerState() == 4 || config.getPlayerState() == 0))) {
                }
            }
            synchronized (MiuiLightsService.this.mLock) {
                MiuiLightsService.this.addToLightCollectionLocked(new LightState(MiuiLightsService.this.mPackageManagerInt.getNameForUid(config.getClientUid()), 3));
            }
            MiuiLightsService.this.releaseVisualizer();
            if (MiuiLightsService.this.mPlayingPid != -1) {
                for (AudioPlaybackConfiguration config2 : configs) {
                    if (config2.getClientPid() == MiuiLightsService.this.mPlayingPid) {
                        return;
                    }
                }
                MiuiLightsService.this.releaseVisualizer();
            }
        }
    }

    /* access modifiers changed from: private */
    public VisualizerHolder.OnDataCaptureListener getDataCaptureListener() {
        if (this.mDataCaptureListener == null) {
            this.mDataCaptureListener = new VisualizerHolder.OnDataCaptureListener() {
                public void onFrequencyCapture(int frequency, float[] frequencies) {
                    synchronized (MiuiLightsService.this.mLock) {
                        MiuiLightsService.this.mMusicLight.setLightLocked(frequency | -16777216, 1, 100, 0, 0);
                    }
                    MiuiLightsService miuiLightsService = MiuiLightsService.this;
                    miuiLightsService.notifyFrequencyCapture(miuiLightsService.getContext(), frequency, frequencies);
                    if (frequency == 0) {
                        MiuiLightsService.this.mLightHandler.postDelayed(new Runnable() {
                            public final void run() {
                                MiuiLightsService.AnonymousClass3.this.lambda$onFrequencyCapture$0$MiuiLightsService$3();
                            }
                        }, 3000);
                    }
                }

                public /* synthetic */ void lambda$onFrequencyCapture$0$MiuiLightsService$3() {
                    if (!MiuiLightsService.this.mAudioManager.isMusicActive()) {
                        if (!new File("/proc/" + MiuiLightsService.this.mPlayingPid).exists()) {
                            MiuiLightsService.this.releaseVisualizer();
                        }
                    }
                }
            };
        }
        return this.mDataCaptureListener;
    }

    /* access modifiers changed from: private */
    public void registerAudioPlaybackCallback() {
        if (this.mSupportColorfulLed && isTurnOnLight() && isTurnOnMusicLight()) {
            this.mAudioManager.registerAudioPlaybackCallback(this.mAudioManagerPlaybackCb, this.mLightHandler);
        }
    }

    /* access modifiers changed from: private */
    public void unregisterAudioPlaybackCallback() {
        AudioManagerPlaybackListener audioManagerPlaybackListener = this.mAudioManagerPlaybackCb;
        if (audioManagerPlaybackListener != null) {
            this.mAudioManager.unregisterAudioPlaybackCallback(audioManagerPlaybackListener);
        }
    }

    /* access modifiers changed from: private */
    public void releaseVisualizer() {
        VisualizerHolder.getInstance().release();
        this.mMusicLight.turnOff();
        recoveryBatteryLight();
        this.mPlayingPid = -1;
    }

    /* access modifiers changed from: private */
    public boolean isMusicLightPlaying() {
        return this.mPlayingPid != -1;
    }

    /* access modifiers changed from: private */
    public void turnoffBatteryLight() {
        LightImpl batteryLight = (LightImpl) this.mLights[3];
        if (batteryLight.mColor != 0) {
            batteryLight.realSetLightLocked(0, 0, 0, 0, 0);
        }
    }

    /* access modifiers changed from: private */
    public void recoveryBatteryLight() {
        if (this.mColorfulLight.mLastLightStyle != -1) {
            Slog.i("LightsService", "skip light bat , cur light id :" + this.mColorfulLight.mLastLightStyle);
            return;
        }
        LightImpl batteryLight = (LightImpl) this.mLights[3];
        if (batteryLight.mColor != 0 && !batteryLight.mDisabled && !isSceneUncomfort(batteryLight.mId) && isLightEnable()) {
            batteryLight.realSetLightLocked(batteryLight.mColor, batteryLight.mMode, batteryLight.mOnMS, batteryLight.mOffMS, batteryLight.mBrightnessMode);
        }
    }

    /* access modifiers changed from: private */
    public void checkCallerVerify(String callingPackage) {
        if (callingPackage == null) {
            throw new IllegalArgumentException("callingPackage is invalid!");
        } else if (this.mSupportColorfulLed) {
            int uid = Binder.getCallingUid();
            int appid = UserHandle.getAppId(uid);
            Slog.d("LightsService", "callingPackage:" + callingPackage + " uid:" + uid + " appid:" + appid);
            if (appid != 1000 && appid != 1001 && appid != 1013 && uid != 0 && uid != 2000) {
                throw new SecurityException("Disallowed call for uid " + Binder.getCallingUid());
            }
        } else {
            throw new IllegalStateException("Current devices doesn't support ColorfulLed!");
        }
    }

    private class UserSwitchReceiver extends BroadcastReceiver {
        private UserSwitchReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (MiuiLightsService.this.mSupportButtonLight) {
                ((LightImpl) MiuiLightsService.this.mLights[2]).updateLight();
            }
            if (MiuiLightsService.this.mSupportLedLight) {
                ((LightImpl) MiuiLightsService.this.mLights[3]).updateLight();
            }
        }
    }

    private class TimeTickReceiver extends BroadcastReceiver {
        private TimeTickReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            MiuiLightsService.this.updateWorkState();
        }
    }

    /* access modifiers changed from: private */
    public void updateWorkState() {
        if (this.mSupportLedSchedule) {
            Calendar cal = Calendar.getInstance();
            long now_stamp = (((long) cal.get(11)) * 3600000) + (((long) cal.get(12)) * 60000);
            long j = this.light_end_time;
            long j2 = this.light_start_time;
            if (j >= j2) {
                if (now_stamp > j || now_stamp < j2) {
                    this.mIsWorkTime = false;
                    return;
                }
            } else if (now_stamp > j && now_stamp < j2) {
                this.mIsWorkTime = false;
                return;
            }
            this.mIsWorkTime = true;
        }
    }

    private class LightContentObserver extends ContentObserver {
        public final Uri BATTERY_LIGHT_TURN_ON_URI = Settings.Secure.getUriFor("battery_light_turn_on");
        public final Uri BREATHING_LIGHT_COLOR_URI = Settings.System.getUriFor("breathing_light_color");
        public final Uri CALL_BREATHING_LIGHT_COLOR_URI = Settings.System.getUriFor("call_breathing_light_color");
        public final Uri LIGHT_TURN_ON_ENDTIME_URI = Settings.Secure.getUriFor("light_turn_on_endTime");
        public final Uri LIGHT_TURN_ON_STARTTIME_URI = Settings.Secure.getUriFor("light_turn_on_startTime");
        public final Uri LIGHT_TURN_ON_URI = Settings.Secure.getUriFor("light_turn_on");
        public final Uri MMS_BREATHING_LIGHT_COLOR_URI = Settings.System.getUriFor("mms_breathing_light_color");
        public final Uri MUSIC_LIGHT_TURN_ON_URI = Settings.Secure.getUriFor("music_light_turn_on");
        public final Uri NOTIFICATION_LIGHT_TURN_ON_URI = Settings.Secure.getUriFor("notification_light_turn_on");
        public final Uri SCREEN_BUTTONS_STATE_URI = Settings.Secure.getUriFor("screen_buttons_state");
        public final Uri SCREEN_BUTTONS_TURN_ON_URI = Settings.Secure.getUriFor("screen_buttons_turn_on");
        public final Uri SINGLE_KEY_USE_ACTION_URI = Settings.System.getUriFor("single_key_use_enable");

        public LightContentObserver() {
            super(MiuiLightsService.this.mLightHandler);
        }

        public void observe() {
            if (MiuiLightsService.this.mSupportButtonLight) {
                MiuiLightsService.this.mResolver.registerContentObserver(this.SCREEN_BUTTONS_STATE_URI, false, this, -1);
                if (MiuiLightsService.this.mSupportTapFingerprint) {
                    MiuiLightsService.this.mResolver.registerContentObserver(this.SINGLE_KEY_USE_ACTION_URI, false, this, -1);
                }
                MiuiLightsService.this.mResolver.registerContentObserver(this.SCREEN_BUTTONS_TURN_ON_URI, false, this, -1);
            }
            if (MiuiLightsService.this.mSupportLedLight) {
                MiuiLightsService.this.mResolver.registerContentObserver(this.BREATHING_LIGHT_COLOR_URI, false, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.CALL_BREATHING_LIGHT_COLOR_URI, false, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.MMS_BREATHING_LIGHT_COLOR_URI, false, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.BATTERY_LIGHT_TURN_ON_URI, true, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.NOTIFICATION_LIGHT_TURN_ON_URI, true, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.LIGHT_TURN_ON_URI, true, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.MUSIC_LIGHT_TURN_ON_URI, true, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.LIGHT_TURN_ON_STARTTIME_URI, true, this, -1);
                MiuiLightsService.this.mResolver.registerContentObserver(this.LIGHT_TURN_ON_ENDTIME_URI, true, this, -1);
            }
        }

        public void onChange(boolean selfChange, Uri uri) {
            if (this.SCREEN_BUTTONS_STATE_URI.equals(uri) || this.SINGLE_KEY_USE_ACTION_URI.equals(uri) || this.SCREEN_BUTTONS_TURN_ON_URI.equals(uri)) {
                ((LightImpl) MiuiLightsService.this.mLights[2]).updateLight();
            } else if (this.BREATHING_LIGHT_COLOR_URI.equals(uri) || this.CALL_BREATHING_LIGHT_COLOR_URI.equals(uri) || this.MMS_BREATHING_LIGHT_COLOR_URI.equals(uri)) {
                ((LightImpl) MiuiLightsService.this.mLights[4]).setFlashing(uri.getLastPathSegment(), (String) null);
            } else if (this.BATTERY_LIGHT_TURN_ON_URI.equals(uri)) {
                ((LightImpl) MiuiLightsService.this.mLights[3]).updateLight();
            } else if (this.NOTIFICATION_LIGHT_TURN_ON_URI.equals(uri)) {
                ((LightImpl) MiuiLightsService.this.mLights[4]).updateLight();
            } else if (this.LIGHT_TURN_ON_URI.equals(uri)) {
                if (MiuiLightsService.this.isTurnOnLight()) {
                    MiuiLightsService.this.registerAudioPlaybackCallback();
                    if (MiuiLightsService.this.isTurnOnMusicLight() && MiuiLightsService.this.mAudioManager.isMusicActive()) {
                        MiuiLightsService.this.turnoffBatteryLight();
                        VisualizerHolder.getInstance().setOnDataCaptureListener(MiuiLightsService.this.getDataCaptureListener());
                    }
                } else {
                    MiuiLightsService.this.releaseVisualizer();
                    MiuiLightsService.this.unregisterAudioPlaybackCallback();
                }
                MiuiLightsService.this.updateLightState();
            } else if (this.MUSIC_LIGHT_TURN_ON_URI.equals(uri)) {
                MiuiLightsService.this.mMusicLight.updateLight();
                if (!MiuiLightsService.this.isTurnOnMusicLight()) {
                    MiuiLightsService.this.releaseVisualizer();
                    MiuiLightsService.this.unregisterAudioPlaybackCallback();
                } else if (MiuiLightsService.this.isTurnOnLight() && MiuiLightsService.this.isTurnOnMusicLight()) {
                    MiuiLightsService.this.registerAudioPlaybackCallback();
                    if (MiuiLightsService.this.mAudioManager.isMusicActive()) {
                        MiuiLightsService.this.turnoffBatteryLight();
                        VisualizerHolder.getInstance().setOnDataCaptureListener(MiuiLightsService.this.getDataCaptureListener());
                    }
                }
            } else if (this.LIGHT_TURN_ON_STARTTIME_URI.equals(uri)) {
                MiuiLightsService miuiLightsService = MiuiLightsService.this;
                long unused = miuiLightsService.light_start_time = Settings.Secure.getLongForUser(miuiLightsService.mResolver, "light_turn_on_startTime", MiuiLightsService.LED_START_WORKTIME_DEF, -2);
                if (MiuiLightsService.this.light_start_time < 0 || MiuiLightsService.this.light_start_time > 86400000) {
                    Settings.Secure.putLong(MiuiLightsService.this.mContext.getContentResolver(), "light_turn_on_startTime", MiuiLightsService.LED_START_WORKTIME_DEF);
                    long unused2 = MiuiLightsService.this.light_start_time = MiuiLightsService.LED_START_WORKTIME_DEF;
                }
                MiuiLightsService.this.updateWorkState();
                MiuiLightsService.this.updateLightState();
            } else if (this.LIGHT_TURN_ON_ENDTIME_URI.equals(uri)) {
                MiuiLightsService miuiLightsService2 = MiuiLightsService.this;
                long unused3 = miuiLightsService2.light_end_time = Settings.Secure.getLongForUser(miuiLightsService2.mResolver, "light_turn_on_endTime", MiuiLightsService.LED_END_WORKTIME_DEF, -2);
                if (MiuiLightsService.this.light_end_time < 0 || MiuiLightsService.this.light_end_time > 86400000) {
                    Settings.Secure.putLong(MiuiLightsService.this.mContext.getContentResolver(), "light_turn_on_endTime", MiuiLightsService.LED_END_WORKTIME_DEF);
                    long unused4 = MiuiLightsService.this.light_end_time = MiuiLightsService.LED_END_WORKTIME_DEF;
                }
                MiuiLightsService.this.updateWorkState();
                MiuiLightsService.this.updateLightState();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateLightState() {
        this.mColorfulLight.updateLight();
        ((LightImpl) this.mLights[3]).updateLight();
        ((LightImpl) this.mLights[4]).updateLight();
    }

    /* access modifiers changed from: private */
    public boolean isSceneUncomfort(int mId) {
        if (mId == 3 || MiuiSettings.SilenceMode.getZenMode(this.mContext) != 1) {
            return false;
        }
        Slog.i("LightsService", "Scene is uncomfort , lights skip!");
        return true;
    }

    /* access modifiers changed from: private */
    public boolean isLightEnable() {
        return this.mSupportLedLight && this.mIsWorkTime && isTurnOnLight();
    }

    /* access modifiers changed from: private */
    public boolean isTurnOnLight() {
        return Settings.Secure.getIntForUser(this.mResolver, "light_turn_on", 1, -2) == 1;
    }

    /* access modifiers changed from: private */
    public boolean isTurnOnButtonLight() {
        return Settings.Secure.getIntForUser(this.mResolver, "screen_buttons_turn_on", 1, -2) == 1;
    }

    /* access modifiers changed from: private */
    public boolean isTurnOnBatteryLight() {
        if (Settings.Secure.getIntForUser(this.mResolver, "battery_light_turn_on", FeatureParser.getBoolean("default_battery_led_on", true), -2) == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isTurnOnNotificationLight() {
        return Settings.Secure.getIntForUser(this.mResolver, "notification_light_turn_on", 1, -2) == 1;
    }

    /* access modifiers changed from: private */
    public boolean isTurnOnMusicLight() {
        return this.mSupportColorfulLed && Settings.Secure.getIntForUser(this.mResolver, "music_light_turn_on", 1, -2) == 1;
    }

    /* access modifiers changed from: private */
    public boolean isDisableButtonLight() {
        if (this.mSupportTapFingerprint) {
            if (Settings.Secure.getIntForUser(this.mResolver, "screen_buttons_state", 0, -2) != 0 || Settings.System.getIntForUser(this.mResolver, "single_key_use_enable", 0, -2) == 1) {
                return true;
            }
            return false;
        } else if (Settings.Secure.getIntForUser(this.mResolver, "screen_buttons_state", 0, -2) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void addToLightCollectionLocked(LightState lightState) {
        if (this.mPreviousLights.size() > 100) {
            this.mPreviousLights.removeFirst();
        }
        this.mPreviousLights.addLast(lightState);
    }

    static int brightnessToColor(int id, int brightness, int lastColor) {
        int color = brightness;
        if (id != 0 || DeviceFeature.BACKLIGHT_BIT <= 8 || DeviceFeature.BACKLIGHT_BIT > 12) {
            int color2 = color & 255;
            return color2 | -16777216 | (color2 << 16) | (color2 << 8);
        } else if (brightness >= 0) {
            return brightness & MiuiSecurityInputMethodHelper.TEXT_MASK;
        } else {
            Slog.e("LightsService", "invalid backlight " + brightness + " !!!");
            return lastColor;
        }
    }

    public void addDataCaptureListener(DataCaptureListener onDataCaptureListener) {
        if (this.dataCaptureListeners == null) {
            this.dataCaptureListeners = new ArrayList<>();
        }
        if (!this.dataCaptureListeners.contains(onDataCaptureListener)) {
            this.dataCaptureListeners.add(onDataCaptureListener);
        }
    }

    public boolean removeDataCaptureListener(DataCaptureListener onDataCaptureListener) {
        if (!CollectionUtils.isEmpty(this.dataCaptureListeners)) {
            return this.dataCaptureListeners.remove(onDataCaptureListener);
        }
        return false;
    }

    public void notifySetLightCallback(Context mContext2, int lightId, int color, int mode, int onMS, int offMS, int brightnessMode) {
        if (!CollectionUtils.isEmpty(this.dataCaptureListeners)) {
            Iterator<DataCaptureListener> it = this.dataCaptureListeners.iterator();
            while (it.hasNext()) {
                it.next().onSetLightCallback(mContext2, lightId, color, mode, onMS, offMS, brightnessMode);
            }
        }
    }

    public void notifyFrequencyCapture(Context mContext2, int magnitude_max, float[] frequencies) {
        if (!CollectionUtils.isEmpty(this.dataCaptureListeners)) {
            Iterator<DataCaptureListener> it = this.dataCaptureListeners.iterator();
            while (it.hasNext()) {
                it.next().onFrequencyCapture(mContext2, magnitude_max, frequencies);
            }
        }
    }

    public void dumpLight(PrintWriter pw, NotificationManagerService.DumpFilter filter) {
        pw.println("MiuiLightsService Status:");
        synchronized (this.mLock) {
            pw.println(" ZenMode:" + MiuiSettings.SilenceMode.getZenMode(this.mContext));
            pw.println(" mSupportColorFulLight:" + this.mSupportColorfulLed);
            pw.println(" Led Working Time: state " + this.mIsWorkTime + " start:" + this.light_start_time + " end:" + this.light_end_time);
            StringBuilder sb = new StringBuilder();
            sb.append(" mSupportTapFingerprint:");
            sb.append(this.mSupportTapFingerprint);
            pw.println(sb.toString());
            pw.println(" mSupportButtonLight:" + this.mSupportButtonLight);
            pw.println(" mSupportLedLight:" + this.mSupportLedLight);
            pw.println(" isLightEnable: " + isLightEnable());
            pw.println(" isTurnOnLight: " + isTurnOnLight());
            pw.println(" isTurnOnButtonLight: " + isTurnOnButtonLight());
            pw.println(" isTurnOnBatteryLight: " + isTurnOnBatteryLight());
            pw.println(" isTurnOnNotificationLight: " + isTurnOnNotificationLight());
            pw.println(" isTurnOnMusicLight: " + isTurnOnMusicLight());
            for (int i = 0; i < this.mLights.length; i++) {
                pw.println(" " + this.mLights[i].toString());
            }
            pw.println(" " + this.mColorfulLight.toString());
            pw.println(" " + this.mMusicLight.toString());
            pw.println("  Previous Lights:");
            Iterator it = this.mPreviousLights.iterator();
            while (it.hasNext()) {
                pw.print("    ");
                pw.println(((LightState) it.next()).toString());
            }
        }
    }
}
