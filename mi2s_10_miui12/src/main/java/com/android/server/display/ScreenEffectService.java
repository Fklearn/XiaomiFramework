package com.android.server.display;

import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.miui.R;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.IntProperty;
import android.util.MathUtils;
import android.util.Slog;
import com.android.internal.os.SomeArgs;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.display.MiuiRampAnimator;
import com.android.server.display.expertmode.ExpertModeUtil;
import java.util.HashMap;
import miui.hardware.display.DisplayFeatureManager;
import miui.hardware.display.IDisplayFeatureCallback;
import miui.os.DeviceFeature;
import miui.util.FeatureParser;

public class ScreenEffectService extends SystemService {
    /* access modifiers changed from: private */
    public static final boolean IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT = FeatureParser.getBoolean("is_compatible_paper_and_screen_effect", false);
    private static final int MSG_SET_COLOR_MODE = 13;
    private static final int MSG_SET_DC_PARSE_STATE = 14;
    private static final int MSG_SET_GRAY_VALUE = 1;
    private static final int MSG_SET_NIGHT_LIGHT_BRIGHTNESS = 0;
    private static final int MSG_SWITCH_DARK_MODE = 10;
    private static final int MSG_UPDATE_DFPS_MODE = 11;
    private static final int MSG_UPDATE_EXPERT_MODE = 12;
    private static final int MSG_UPDATE_MONOCHROME_MODE = 3;
    private static final int MSG_UPDATE_NIGHT_LIGHT_COLOR = 5;
    private static final int MSG_UPDATE_PAPER_MODE = 1;
    private static final int MSG_UPDATE_PAPER_MODE_CACHE = 4;
    private static final int MSG_UPDATE_PCC_LEVEL = 7;
    private static final int MSG_UPDATE_SCREEN_OPTIMIZE = 2;
    private static final int MSG_UPDATE_UNLIMITED_COLOR_LEVEL = 8;
    private static final int MSG_UPDATE_WCG_STATE = 6;
    private static final float PAPER_MODE_MIN_LEVEL = FeatureParser.getFloat("paper_mode_min_level", 1.0f).floatValue();
    private static final String PERSISTENT_PROPERTY_DISPLAY_COLOR = "persist.sys.sf.native_mode";
    /* access modifiers changed from: private */
    public static final boolean SUPPORT_MONOCHROME_MODE = ((MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED & 8) != 0);
    /* access modifiers changed from: private */
    public static final boolean SUPPORT_UNLIMITED_COLOR_MODE = MiuiSettings.ScreenEffect.SUPPORT_UNLIMITED_COLOR_MODE;
    private static final String SURFACE_FLINGER = "SurfaceFlinger";
    private static final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE = 1100;
    private static final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_DC_PARSE_STATE = 1036;
    private static final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_DFPS = 1035;
    private static final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_PCC = 1101;
    private static final int SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_SET_MODE = 1023;
    private static final String TAG = "ScreenEffectService";
    private static final int TEMP_PAPER_MODE_LEVEL = -1;
    static LocalService sScreenEffectManager;
    private boolean mBootCompleted;
    /* access modifiers changed from: private */
    public BrightnessHandler mBrightnessHandler;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public DisplayFeatureManager mDisplayFeatureManager;
    /* access modifiers changed from: private */
    public DisplayPowerController mDisplayPowerController;
    /* access modifiers changed from: private */
    public int mDisplayState = 0;
    /* access modifiers changed from: private */
    public int mDriveMode;
    private boolean mForceDisableEyecare;
    private boolean mGameHdrEnabled;
    /* access modifiers changed from: private */
    public float mGrayScale = Float.NaN;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private HandlerThread mHandlerThread;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public boolean mMonochromeModeEnabled;
    /* access modifiers changed from: private */
    public int mMonochromeModeType;
    /* access modifiers changed from: private */
    public HashMap<String, Boolean> mMonochromeWhiteList;
    /* access modifiers changed from: private */
    public int mNightLightBrightness;
    /* access modifiers changed from: private */
    public int mNightLightColor;
    /* access modifiers changed from: private */
    public MiuiRampAnimator<DisplayFeatureManager> mPaperModeAnimator;
    /* access modifiers changed from: private */
    public boolean mPaperModeEnabled;
    /* access modifiers changed from: private */
    public int mPaperModeLevel;
    private int mPaperModeMinRate;
    /* access modifiers changed from: private */
    public int mScreenColorLevel;
    /* access modifiers changed from: private */
    public int mScreenOptimizeMode;
    private SettingsObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public String mTopAppPkg;

    public ScreenEffectService(Context context) {
        super(context);
        this.mContext = context;
        this.mHandlerThread = new HandlerThread("ScreenEffectThread");
        this.mHandlerThread.start();
        this.mHandler = new ScreenEffectHandler(this.mHandlerThread.getLooper());
    }

    public void onStart() {
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mDisplayFeatureManager = DisplayFeatureManager.getInstance();
        sScreenEffectManager = new LocalService();
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            checkSettingsData();
            Settings.System.putInt(this.mContext.getContentResolver(), "screen_game_mode", 0);
            loadSettings();
            this.mSettingsObserver = new SettingsObserver();
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_paper_mode_enabled"), false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_paper_mode_level"), false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_optimize_mode"), false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_color_level"), false, this.mSettingsObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("drive_mode_drive_mode"), false, this.mSettingsObserver, -1);
            this.mContext.registerReceiver(new UserSwitchReceiver(), new IntentFilter("android.intent.action.USER_SWITCHED"));
            if (SUPPORT_MONOCHROME_MODE) {
                this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_monochrome_mode_enabled"), false, this.mSettingsObserver, -1);
                this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_monochrome_mode"), false, this.mSettingsObserver, -1);
                this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_monochrome_mode_white_list"), false, this.mSettingsObserver, -1);
                this.mHandler.obtainMessage(3).sendToTarget();
            }
            if (DeviceFeature.SUPPORT_NIGHT_LIGHT_ADJ) {
                this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("night_light_level"), false, this.mSettingsObserver, -1);
            }
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_game_mode"), false, this.mSettingsObserver, -1);
            if (DeviceFeature.SUPPORT_PAPERMODE_ANIMATION) {
                this.mPaperModeAnimator = new MiuiRampAnimator<>(this.mDisplayFeatureManager, new IntProperty<DisplayFeatureManager>("papermode") {
                    public void setValue(DisplayFeatureManager object, int value) {
                        if (ScreenEffectService.this.mDisplayState != 1 && (value > 0 || ScreenEffectService.this.mPaperModeAnimator.isAnimating())) {
                            object.setScreenEffect(3, value);
                        } else if (ScreenEffectService.IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT && ScreenEffectService.this.mDisplayState != 1 && !ScreenEffectService.this.mPaperModeEnabled) {
                            object.setScreenEffect(3, 0);
                        }
                    }

                    public Integer get(DisplayFeatureManager object) {
                        return 0;
                    }
                });
                this.mPaperModeAnimator.setListener(new PaperModeAnimatListener());
            }
            setScreenModes(true);
            if (DeviceFeature.SUPPORT_NIGHT_LIGHT_ADJ) {
                this.mHandler.obtainMessage(5).sendToTarget();
            }
            if (DeviceFeature.SUPPORT_DISPLAYFEATURE_CALLBACK) {
                this.mDisplayFeatureManager.registerCallback(new IDisplayFeatureCallback.Stub() {
                    public void displayfeatureInfoChanged(int caseId, Object... params) {
                        if (params.length > 0) {
                            if (caseId == 10000) {
                                ScreenEffectService.this.mHandler.obtainMessage(6, params[0].intValue(), 0).sendToTarget();
                            }
                            if (caseId == 10035) {
                                ScreenEffectService.this.mHandler.obtainMessage(11, params[0].intValue(), 0).sendToTarget();
                            }
                            if (ScreenEffectService.this.mBrightnessHandler != null && caseId == 0) {
                                ScreenEffectService.this.mBrightnessHandler.sendMessage(Message.obtain(ScreenEffectService.this.mBrightnessHandler, 1, Integer.valueOf(params[0].intValue())));
                            }
                            if (caseId == 30000) {
                                ScreenEffectService.this.mHandler.obtainMessage(13, params[0].intValue(), 0).sendToTarget();
                            }
                            if (caseId == 40000) {
                                ScreenEffectService.this.mHandler.obtainMessage(14, params[0].intValue(), 0).sendToTarget();
                            }
                        }
                        if (caseId == 20000 && params.length >= 4) {
                            SomeArgs args = SomeArgs.obtain();
                            args.arg1 = params[1];
                            args.arg2 = params[2];
                            args.arg3 = params[3];
                            ScreenEffectService.this.mHandler.obtainMessage(7, params[0].intValue(), 0, args).sendToTarget();
                        }
                    }
                });
            }
        } else if (phase == 1000) {
            this.mBootCompleted = true;
        }
    }

    /* access modifiers changed from: private */
    public void setScreenModes(boolean immediatePaperMode) {
        if (IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT || SUPPORT_UNLIMITED_COLOR_MODE) {
            this.mHandler.obtainMessage(2).sendToTarget();
        }
        if (SUPPORT_UNLIMITED_COLOR_MODE) {
            this.mHandler.obtainMessage(8).sendToTarget();
        }
        this.mHandler.obtainMessage(1, Boolean.valueOf(immediatePaperMode)).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void notifySFWcgState(boolean enable) {
        IBinder flinger = ServiceManager.getService(SURFACE_FLINGER);
        if (flinger != null) {
            Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            data.writeBoolean(enable);
            try {
                flinger.transact(SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE, data, (Parcel) null, 0);
            } catch (RemoteException | SecurityException ex) {
                Slog.e(TAG, "Failed to notifySurfaceFlinger", ex);
            } catch (Throwable th) {
                data.recycle();
                throw th;
            }
            data.recycle();
        }
    }

    /* access modifiers changed from: private */
    public void notifySFDfpsMode(int mode) {
        IBinder flinger = ServiceManager.getService(SURFACE_FLINGER);
        if (flinger != null) {
            Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            data.writeInt(mode);
            try {
                flinger.transact(SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_DFPS, data, (Parcel) null, 0);
            } catch (RemoteException | SecurityException ex) {
                Slog.e(TAG, "Failed to notify dfps mode to SurfaceFlinger", ex);
            } catch (Throwable th) {
                data.recycle();
                throw th;
            }
            data.recycle();
        }
    }

    /* access modifiers changed from: private */
    public void notifySFPccLevel(int level, float red, float green, float blue) {
        IBinder flinger = ServiceManager.getService(SURFACE_FLINGER);
        if (flinger != null) {
            Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            data.writeInt(level);
            data.writeFloat(red);
            data.writeFloat(green);
            data.writeFloat(blue);
            try {
                flinger.transact(SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_PCC, data, (Parcel) null, 0);
            } catch (RemoteException | SecurityException ex) {
                Slog.e(TAG, "Failed to notifySurfaceFlinger", ex);
            } catch (Throwable th) {
                data.recycle();
                throw th;
            }
            data.recycle();
        }
    }

    /* access modifiers changed from: private */
    public void notifySFColorMode(int mode) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "display_color_mode", mode, -2);
        IBinder flinger = ServiceManager.getService(SURFACE_FLINGER);
        if (flinger != null) {
            Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            data.writeInt(mode);
            try {
                flinger.transact(1023, data, (Parcel) null, 0);
                SystemProperties.set(PERSISTENT_PROPERTY_DISPLAY_COLOR, Integer.toString(mode));
            } catch (RemoteException | SecurityException ex) {
                Slog.e(TAG, "Failed to notify dfps mode to SurfaceFlinger", ex);
            } catch (Throwable th) {
                data.recycle();
                throw th;
            }
            data.recycle();
        }
    }

    /* access modifiers changed from: private */
    public void notifySFDCParseState(int state) {
        IBinder flinger = ServiceManager.getService(SURFACE_FLINGER);
        if (flinger != null) {
            Parcel data = Parcel.obtain();
            data.writeInterfaceToken("android.ui.ISurfaceComposer");
            data.writeInt(state);
            try {
                flinger.transact(SURFACE_FLINGER_TRANSACTION_DISPLAY_FEATURE_DC_PARSE_STATE, data, (Parcel) null, 0);
            } catch (RemoteException | SecurityException ex) {
                Slog.e(TAG, "Failed to notify dc parse state to SurfaceFlinger", ex);
            } catch (Throwable th) {
                data.recycle();
                throw th;
            }
            data.recycle();
        }
    }

    private void checkSettingsData() {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "screen_paper_mode", 1) == 2) {
            Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_enabled", 0, -2);
            this.mContext.getContentResolver().delete(Settings.System.getUriFor("screen_paper_mode"), (String) null, (String[]) null);
        }
        if (Settings.System.getInt(this.mContext.getContentResolver(), "screen_paper_mode_level", 0) == 0) {
            String paperModeLevel = SystemProperties.get("persist.sys.eyecare_cache");
            if (!TextUtils.isEmpty(paperModeLevel)) {
                Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_level", Integer.parseInt(paperModeLevel), -2);
            } else {
                Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_level", MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL, -2);
            }
        }
        if (Settings.System.getInt(this.mContext.getContentResolver(), "screen_color_level", 0) == 0 && !TextUtils.isEmpty(SystemProperties.get("persist.sys.display_prefer"))) {
            Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_color_level", DisplayFeatureManager.getInstance().getColorPrefer(), -2);
        }
        if (Settings.System.getInt(this.mContext.getContentResolver(), "screen_optimize_mode", 0) == 0 && !TextUtils.isEmpty(SystemProperties.get("persist.sys.display_ce"))) {
            if (DisplayFeatureManager.getInstance().isAdEnable()) {
                Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_optimize_mode", 1, -2);
            } else if (DisplayFeatureManager.getInstance().getScreenGamut() == 0) {
                Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_optimize_mode", 2, -2);
            } else {
                Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_optimize_mode", 3, -2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadSettings() {
        boolean z = false;
        this.mPaperModeEnabled = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_enabled", 0, -2) != 0;
        this.mPaperModeLevel = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_level", MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL, -2);
        resetLocalPaperLevelIfNeed();
        this.mScreenOptimizeMode = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_optimize_mode", MiuiSettings.ScreenEffect.DEFAULT_SCREEN_OPTIMIZE_MODE, -2);
        this.mScreenColorLevel = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_color_level", 2, -2);
        this.mDriveMode = Settings.System.getIntForUser(this.mContext.getContentResolver(), "drive_mode_drive_mode", 0, -2);
        if (SUPPORT_MONOCHROME_MODE) {
            if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_monochrome_mode_enabled", 0, -2) != 0) {
                z = true;
            }
            this.mMonochromeModeEnabled = z;
            this.mMonochromeModeType = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_monochrome_mode", 2, -2);
            this.mMonochromeWhiteList = MiuiSettings.ScreenEffect.getScreenModePkgList(this.mContext, "screen_monochrome_mode_white_list");
        }
        if (DeviceFeature.SUPPORT_NIGHT_LIGHT_ADJ) {
            int value = Settings.System.getIntForUser(this.mContext.getContentResolver(), "night_light_level", -1, -2);
            if (value != -1) {
                this.mNightLightBrightness = MathUtils.constrain(value & 255, this.mContext.getResources().getInteger(R.integer.config_nightLightBrightnessMinimum), this.mContext.getResources().getInteger(R.integer.config_nightLightBrightnessMaximum));
                this.mNightLightColor = MathUtils.constrain((65280 & value) >> 8, this.mContext.getResources().getInteger(R.integer.config_nightLightColorMinimum), this.mContext.getResources().getInteger(R.integer.config_nightLightColorMaximum));
            } else {
                this.mNightLightBrightness = this.mContext.getResources().getInteger(R.integer.config_nightLightBrightnessDefault);
                this.mNightLightColor = this.mContext.getResources().getInteger(R.integer.config_nightLightColorDefault);
            }
        }
        if (DeviceFeature.SUPPORT_PAPERMODE_ANIMATION != 0) {
            this.mPaperModeMinRate = this.mContext.getResources().getInteger(R.integer.config_paperModeMinRate);
        }
    }

    private void resetLocalPaperLevelIfNeed() {
        if (IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT && ((float) this.mPaperModeLevel) < PAPER_MODE_MIN_LEVEL && Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_level", -1, -2) != -1) {
            this.mPaperModeLevel = MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL;
            Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_paper_mode_level", this.mPaperModeLevel, -2);
        }
    }

    private class SettingsObserver extends ContentObserver {
        public SettingsObserver() {
            super(ScreenEffectService.this.mHandler);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r17, android.net.Uri r18) {
            /*
                r16 = this;
                r0 = r16
                java.lang.String r1 = r18.getLastPathSegment()
                int r2 = r1.hashCode()
                java.lang.String r3 = "screen_paper_mode_enabled"
                java.lang.String r4 = "screen_optimize_mode"
                java.lang.String r5 = "drive_mode_drive_mode"
                java.lang.String r6 = "screen_color_level"
                java.lang.String r7 = "screen_monochrome_mode_enabled"
                java.lang.String r8 = "screen_monochrome_mode_white_list"
                java.lang.String r9 = "screen_monochrome_mode"
                java.lang.String r10 = "screen_paper_mode_level"
                java.lang.String r11 = "night_light_level"
                r12 = -1
                r13 = 2
                r15 = 0
                switch(r2) {
                    case -2085820044: goto L_0x0078;
                    case -1618391570: goto L_0x0070;
                    case -1572043854: goto L_0x0068;
                    case -1457819203: goto L_0x005c;
                    case -946029151: goto L_0x0054;
                    case -548543564: goto L_0x004c;
                    case 671593557: goto L_0x0044;
                    case 1497178783: goto L_0x003b;
                    case 1962624818: goto L_0x0033;
                    case 2119453483: goto L_0x002b;
                    default: goto L_0x002a;
                }
            L_0x002a:
                goto L_0x0080
            L_0x002b:
                boolean r2 = r1.equals(r3)
                if (r2 == 0) goto L_0x002a
                r2 = r15
                goto L_0x0081
            L_0x0033:
                boolean r2 = r1.equals(r4)
                if (r2 == 0) goto L_0x002a
                r2 = r13
                goto L_0x0081
            L_0x003b:
                boolean r2 = r1.equals(r5)
                if (r2 == 0) goto L_0x002a
                r2 = 8
                goto L_0x0081
            L_0x0044:
                boolean r2 = r1.equals(r6)
                if (r2 == 0) goto L_0x002a
                r2 = 3
                goto L_0x0081
            L_0x004c:
                boolean r2 = r1.equals(r7)
                if (r2 == 0) goto L_0x002a
                r2 = 4
                goto L_0x0081
            L_0x0054:
                boolean r2 = r1.equals(r8)
                if (r2 == 0) goto L_0x002a
                r2 = 6
                goto L_0x0081
            L_0x005c:
                java.lang.String r2 = "screen_game_mode"
                boolean r2 = r1.equals(r2)
                if (r2 == 0) goto L_0x002a
                r2 = 9
                goto L_0x0081
            L_0x0068:
                boolean r2 = r1.equals(r9)
                if (r2 == 0) goto L_0x002a
                r2 = 5
                goto L_0x0081
            L_0x0070:
                boolean r2 = r1.equals(r10)
                if (r2 == 0) goto L_0x002a
                r2 = 1
                goto L_0x0081
            L_0x0078:
                boolean r2 = r1.equals(r11)
                if (r2 == 0) goto L_0x002a
                r2 = 7
                goto L_0x0081
            L_0x0080:
                r2 = r12
            L_0x0081:
                r14 = -2
                switch(r2) {
                    case 0: goto L_0x015a;
                    case 1: goto L_0x0138;
                    case 2: goto L_0x011f;
                    case 3: goto L_0x00fc;
                    case 4: goto L_0x00e1;
                    case 5: goto L_0x00c9;
                    case 6: goto L_0x00ba;
                    case 7: goto L_0x00a1;
                    case 8: goto L_0x008e;
                    case 9: goto L_0x0087;
                    default: goto L_0x0085;
                }
            L_0x0085:
                goto L_0x017a
            L_0x0087:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r2.updateGameMode()
                goto L_0x017a
            L_0x008e:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                android.content.ContentResolver r3 = r3.getContentResolver()
                int r3 = android.provider.Settings.System.getIntForUser(r3, r5, r15, r14)
                int unused = r2.mDriveMode = r3
                goto L_0x017a
            L_0x00a1:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r2 = r2.mContext
                android.content.ContentResolver r2 = r2.getContentResolver()
                int r2 = android.provider.Settings.System.getIntForUser(r2, r11, r12, r14)
                com.android.server.display.ScreenEffectService$LocalService r3 = com.android.server.display.ScreenEffectService.sScreenEffectManager
                if (r3 == 0) goto L_0x017a
                com.android.server.display.ScreenEffectService$LocalService r3 = com.android.server.display.ScreenEffectService.sScreenEffectManager
                r3.setNightLight(r2)
                goto L_0x017a
            L_0x00ba:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                java.util.HashMap r3 = android.provider.MiuiSettings.ScreenEffect.getScreenModePkgList(r3, r8)
                java.util.HashMap unused = r2.mMonochromeWhiteList = r3
                goto L_0x017a
            L_0x00c9:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                android.content.ContentResolver r3 = r3.getContentResolver()
                int r3 = android.provider.Settings.System.getIntForUser(r3, r9, r13, r14)
                int unused = r2.mMonochromeModeType = r3
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r2.updateMonochromeMode()
                goto L_0x017a
            L_0x00e1:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                android.content.ContentResolver r3 = r3.getContentResolver()
                int r3 = android.provider.Settings.System.getIntForUser(r3, r7, r15, r14)
                if (r3 == 0) goto L_0x00f2
                r15 = 1
            L_0x00f2:
                boolean unused = r2.mMonochromeModeEnabled = r15
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r2.updateMonochromeMode()
                goto L_0x017a
            L_0x00fc:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                android.content.ContentResolver r3 = r3.getContentResolver()
                int r3 = android.provider.Settings.System.getIntForUser(r3, r6, r13, r14)
                int unused = r2.mScreenColorLevel = r3
                boolean r2 = com.android.server.display.ScreenEffectService.SUPPORT_UNLIMITED_COLOR_MODE
                if (r2 == 0) goto L_0x0119
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r2.updateUnlimitedColorLevel()
                goto L_0x017a
            L_0x0119:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r2.updateScreenOptimize()
                goto L_0x017a
            L_0x011f:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                android.content.ContentResolver r3 = r3.getContentResolver()
                int r5 = android.provider.MiuiSettings.ScreenEffect.DEFAULT_SCREEN_OPTIMIZE_MODE
                int r3 = android.provider.Settings.System.getIntForUser(r3, r4, r5, r14)
                int unused = r2.mScreenOptimizeMode = r3
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r2.updateScreenOptimize()
                goto L_0x017a
            L_0x0138:
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r3 = r2.mContext
                android.content.ContentResolver r3 = r3.getContentResolver()
                int r4 = android.provider.MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL
                int r3 = android.provider.Settings.System.getIntForUser(r3, r10, r4, r14)
                int unused = r2.mPaperModeLevel = r3
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                boolean r2 = r2.mPaperModeEnabled
                if (r2 == 0) goto L_0x017a
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                r4 = 1
                r2.updatePaperMode(r4, r4)
                goto L_0x017a
            L_0x015a:
                r4 = 1
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                android.content.Context r5 = r2.mContext
                android.content.ContentResolver r5 = r5.getContentResolver()
                int r3 = android.provider.Settings.System.getIntForUser(r5, r3, r15, r14)
                if (r3 == 0) goto L_0x016c
                goto L_0x016d
            L_0x016c:
                r4 = r15
            L_0x016d:
                boolean unused = r2.mPaperModeEnabled = r4
                com.android.server.display.ScreenEffectService r2 = com.android.server.display.ScreenEffectService.this
                boolean r3 = r2.mPaperModeEnabled
                r2.updatePaperMode(r3, r15)
            L_0x017a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.ScreenEffectService.SettingsObserver.onChange(boolean, android.net.Uri):void");
        }
    }

    private class ScreenEffectHandler extends Handler {
        public ScreenEffectHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ScreenEffectService screenEffectService = ScreenEffectService.this;
                    screenEffectService.updatePaperMode(screenEffectService.mPaperModeEnabled, ((Boolean) msg.obj).booleanValue());
                    return;
                case 2:
                    ScreenEffectService.this.updateScreenOptimize();
                    return;
                case 3:
                    ScreenEffectService.this.updateMonochromeMode();
                    return;
                case 5:
                    ScreenEffectService.this.updateNightLightColor();
                    return;
                case 6:
                    SomeArgs args = ScreenEffectService.this;
                    boolean z = true;
                    if (msg.arg1 != 1) {
                        z = false;
                    }
                    args.notifySFWcgState(z);
                    return;
                case 7:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    ScreenEffectService.this.notifySFPccLevel(msg.arg1, ((Float) args2.arg1).floatValue(), ((Float) args2.arg2).floatValue(), ((Float) args2.arg3).floatValue());
                    args2.recycle();
                    return;
                case 8:
                    ScreenEffectService.this.updateUnlimitedColorLevel();
                    return;
                case 10:
                    ScreenEffectService screenEffectService2 = ScreenEffectService.this;
                    Context access$700 = screenEffectService2.mContext;
                    ScreenEffectService screenEffectService3 = ScreenEffectService.this;
                    screenEffectService2.setDarkModeEnable(access$700, screenEffectService3.isDarkModeEnable(screenEffectService3.mContext));
                    return;
                case 11:
                    ScreenEffectService.this.notifySFDfpsMode(msg.arg1);
                    return;
                case 12:
                    ExpertModeUtil.updateExpertModeEffect(ScreenEffectService.this.mContext);
                    return;
                case 13:
                    ScreenEffectService.this.notifySFColorMode(msg.arg1);
                    return;
                case 14:
                    ScreenEffectService.this.notifySFDCParseState(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMonochromeMode() {
        int i = 0;
        if (!this.mMonochromeModeEnabled) {
            this.mDisplayFeatureManager.setScreenEffect(4, 0);
        } else if (this.mMonochromeModeType == 1) {
            this.mDisplayFeatureManager.setScreenEffect(4, 1);
        } else {
            Boolean monochromeModepkg = this.mMonochromeWhiteList.get(this.mTopAppPkg);
            boolean isPkgMonochromeMode = monochromeModepkg != null && monochromeModepkg.booleanValue();
            DisplayFeatureManager displayFeatureManager = this.mDisplayFeatureManager;
            if (isPkgMonochromeMode) {
                i = 1;
            }
            displayFeatureManager.setScreenEffect(4, i);
        }
    }

    /* access modifiers changed from: private */
    public void updatePaperMode(boolean enabled, boolean immediate) {
        if (!this.mForceDisableEyecare) {
            setScreenEyeCare(enabled, immediate);
        }
    }

    private void setScreenEyeCare(boolean enabled, boolean immediate) {
        if (DeviceFeature.SUPPORT_PAPERMODE_ANIMATION && this.mPaperModeAnimator != null && (immediate || this.mDisplayState != 1)) {
            if (this.mPaperModeAnimator.animateTo(enabled ? this.mPaperModeLevel : 0, immediate ? 0 : Math.max((this.mPaperModeLevel * 2) / 3, this.mPaperModeMinRate))) {
                return;
            }
        }
        if (enabled) {
            this.mDisplayFeatureManager.setScreenEffect(3, this.mPaperModeLevel);
        } else if (IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT) {
            this.mDisplayFeatureManager.setScreenEffect(3, 0);
        } else {
            updateScreenOptimize();
        }
    }

    /* access modifiers changed from: private */
    public void updateScreenOptimize() {
        if (IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT || ((!this.mPaperModeEnabled && !this.mGameHdrEnabled) || this.mForceDisableEyecare)) {
            int value = this.mScreenColorLevel;
            if (SUPPORT_UNLIMITED_COLOR_MODE) {
                value = 0;
            }
            if (!((MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED & 1) == 0 || this.mScreenOptimizeMode == 1)) {
                value = 2;
            }
            int mode = 0;
            int i = this.mScreenOptimizeMode;
            if (i == 2) {
                mode = 1;
            } else if (i == 3) {
                mode = 2;
            } else if (i == 4) {
                mode = 26;
                if (this.mBootCompleted) {
                    return;
                }
            }
            this.mDisplayFeatureManager.setScreenEffect(mode, value);
        }
    }

    /* access modifiers changed from: private */
    public void updateGameMode() {
        int gameMode = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_game_mode", 0, -2);
        int gameHdrLevel = Settings.System.getIntForUser(this.mContext.getContentResolver(), "game_hdr_level", 0, -2);
        boolean z = true;
        boolean gameHdrEnabled = (gameMode & 2) != 0;
        boolean forceDisableEyecare = (gameMode & 1) != 0;
        if (this.mGameHdrEnabled != gameHdrEnabled) {
            this.mGameHdrEnabled = gameHdrEnabled;
            if (!gameHdrEnabled) {
                this.mDisplayFeatureManager.setScreenEffect(19, 0);
                updateScreenOptimize();
            } else if (IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT || ((forceDisableEyecare && this.mPaperModeEnabled) || !this.mPaperModeEnabled)) {
                if (forceDisableEyecare && this.mPaperModeEnabled) {
                    setScreenEyeCare(!forceDisableEyecare, true);
                }
                this.mDisplayFeatureManager.setScreenEffect(19, gameHdrLevel);
            }
        }
        if (this.mForceDisableEyecare != forceDisableEyecare) {
            this.mForceDisableEyecare = forceDisableEyecare;
        }
        if ((IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT || !gameHdrEnabled) && this.mPaperModeEnabled) {
            if (forceDisableEyecare) {
                z = false;
            }
            setScreenEyeCare(z, false);
        }
    }

    /* access modifiers changed from: private */
    public void updateUnlimitedColorLevel() {
        if (this.mScreenOptimizeMode != 4) {
            this.mDisplayFeatureManager.setScreenEffect(23, this.mScreenColorLevel);
        }
    }

    /* access modifiers changed from: private */
    public void updateNightLightColor() {
        this.mDisplayFeatureManager.setScreenEffect(9, this.mNightLightColor);
    }

    private class UserSwitchReceiver extends BroadcastReceiver {
        private UserSwitchReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            ScreenEffectService.this.loadSettings();
            if (ScreenEffectService.IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT) {
                ScreenEffectService.this.mHandler.removeMessages(2);
                ScreenEffectService.this.mHandler.obtainMessage(2).sendToTarget();
            }
            ScreenEffectService.this.mHandler.removeMessages(1);
            ScreenEffectService.this.mHandler.obtainMessage(1, false).sendToTarget();
            if (ScreenEffectService.SUPPORT_MONOCHROME_MODE) {
                ScreenEffectService.this.mHandler.removeMessages(3);
                ScreenEffectService.this.mHandler.obtainMessage(3).sendToTarget();
            }
            if (DeviceFeature.SUPPORT_NIGHT_LIGHT_ADJ) {
                ScreenEffectService.this.mHandler.removeMessages(5);
                ScreenEffectService.this.mHandler.obtainMessage(5).sendToTarget();
            }
            if (Build.VERSION.SDK_INT >= 29) {
                ScreenEffectService.this.mHandler.removeMessages(10);
                ScreenEffectService.this.mHandler.obtainMessage(10).sendToTarget();
            }
            if (ExpertModeUtil.SUPPORT_DISPLAY_EXPERT_MODE && ScreenEffectService.this.mScreenOptimizeMode == 4) {
                ScreenEffectService.this.mHandler.removeMessages(12);
                ScreenEffectService.this.mHandler.obtainMessage(12).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDarkModeEnable(Context ctx, boolean enable) {
        UiModeManager manager = (UiModeManager) ctx.getSystemService(UiModeManager.class);
        if (manager != null) {
            manager.setNightMode(enable ? 2 : 1);
        }
    }

    /* access modifiers changed from: private */
    public boolean isDarkModeEnable(Context ctx) {
        return 2 == Settings.Secure.getIntForUser(ctx.getContentResolver(), "ui_night_mode", 1, 0);
    }

    class LocalService extends ScreenEffectManager {
        LocalService() {
        }

        public void updateLocalScreenEffect(String packageName) {
            if (packageName != null && !packageName.equals(ScreenEffectService.this.mTopAppPkg)) {
                String unused = ScreenEffectService.this.mTopAppPkg = packageName;
                if (ScreenEffectService.SUPPORT_MONOCHROME_MODE && ScreenEffectService.this.mMonochromeModeEnabled && ScreenEffectService.this.mMonochromeModeType == 2) {
                    ScreenEffectService.this.mHandler.obtainMessage(3).sendToTarget();
                }
            }
        }

        public void updateScreenEffect(int state) {
            int oldState = ScreenEffectService.this.mDisplayState;
            int unused = ScreenEffectService.this.mDisplayState = state;
            if (!DeviceFeature.PERSIST_SCREEN_EFFECT && oldState == 1 && state != oldState) {
                if (!ScreenEffectService.this.mLockPatternUtils.isLockScreenDisabled(-2)) {
                    String unused2 = ScreenEffectService.this.mTopAppPkg = null;
                }
                ScreenEffectService.this.setScreenModes(true);
            }
        }

        public int getNightLightBrightness() {
            return ScreenEffectService.this.mNightLightBrightness;
        }

        public void setNightLight(int value) {
            int brightness = MathUtils.constrain(value & 255, ScreenEffectService.this.mContext.getResources().getInteger(R.integer.config_nightLightBrightnessMinimum), ScreenEffectService.this.mContext.getResources().getInteger(R.integer.config_nightLightBrightnessMaximum));
            int color = MathUtils.constrain((65280 & value) >> 8, ScreenEffectService.this.mContext.getResources().getInteger(R.integer.config_nightLightColorMinimum), ScreenEffectService.this.mContext.getResources().getInteger(R.integer.config_nightLightColorMaximum));
            if (!(brightness == ScreenEffectService.this.mNightLightBrightness || ScreenEffectService.this.mBrightnessHandler == null)) {
                ScreenEffectService.this.mBrightnessHandler.sendMessage(Message.obtain(ScreenEffectService.this.mBrightnessHandler, 0, Integer.valueOf(brightness)));
            }
            if (color != ScreenEffectService.this.mNightLightColor) {
                int unused = ScreenEffectService.this.mNightLightColor = color;
                ScreenEffectService.this.mHandler.obtainMessage(5).sendToTarget();
            }
        }

        public long getDimDurationExtraTime(long extraTimeMillis) {
            if (ScreenEffectService.this.mDriveMode != 1 || extraTimeMillis <= 0) {
                return 0;
            }
            return extraTimeMillis;
        }

        public float getGrayScale() {
            return ScreenEffectService.this.mGrayScale;
        }

        public void initDisplayPowerController(DisplayPowerController controller, Looper looper) {
            DisplayPowerController unused = ScreenEffectService.this.mDisplayPowerController = controller;
            ScreenEffectService screenEffectService = ScreenEffectService.this;
            BrightnessHandler unused2 = screenEffectService.mBrightnessHandler = new BrightnessHandler(looper != null ? looper : screenEffectService.mHandler.getLooper());
        }

        public void updateDozeBrightness(int brightness) {
            ScreenEffectService.this.mDisplayFeatureManager.setDozeBrightness(brightness);
        }
    }

    @Deprecated
    static void setDisplayPowerController(DisplayPowerController controller) {
        LocalService localService = sScreenEffectManager;
        if (localService != null) {
            localService.initDisplayPowerController(controller, (Looper) null);
        }
    }

    static void initDisplayPowerController(DisplayPowerController controller, Looper looper) {
        LocalService localService = sScreenEffectManager;
        if (localService != null) {
            localService.initDisplayPowerController(controller, looper);
        }
    }

    static void updateScreenEffect(int state) {
        LocalService localService = sScreenEffectManager;
        if (localService != null) {
            localService.updateScreenEffect(state);
        }
    }

    public static void updateLocalScreenEffect(String packageName) {
        LocalService localService = sScreenEffectManager;
        if (localService != null) {
            localService.updateLocalScreenEffect(packageName);
        }
    }

    public static void updateDozeBrightness(int brightness) {
        LocalService localService = sScreenEffectManager;
        if (localService != null) {
            localService.updateDozeBrightness(brightness);
        }
    }

    public static void startScreenEffectService() {
        if (MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED != 0 && sScreenEffectManager == null) {
            ((SystemServiceManager) LocalServices.getService(SystemServiceManager.class)).startService(ScreenEffectService.class);
        }
    }

    public static long getDimDurationExtraTime(long extraTimeMillis) {
        LocalService localService = sScreenEffectManager;
        if (localService != null) {
            return localService.getDimDurationExtraTime(extraTimeMillis);
        }
        return 0;
    }

    class PaperModeAnimatListener implements MiuiRampAnimator.Listener {
        PaperModeAnimatListener() {
        }

        public void onAnimationEnd() {
            if (ScreenEffectService.this.mDisplayState != 1 && !ScreenEffectService.IS_COMPATIBLE_PAPER_AND_SCREEN_EFFECT) {
                ScreenEffectService.this.updateScreenOptimize();
            }
        }
    }

    private class BrightnessHandler extends Handler {
        public BrightnessHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                int unused = ScreenEffectService.this.mNightLightBrightness = ((Integer) msg.obj).intValue();
            } else if (i == 1) {
                float unused2 = ScreenEffectService.this.mGrayScale = (((float) ((Integer) msg.obj).intValue()) * 1.0f) / 255.0f;
            }
            ScreenEffectService.this.mDisplayPowerController.updateBrightness();
        }
    }
}
