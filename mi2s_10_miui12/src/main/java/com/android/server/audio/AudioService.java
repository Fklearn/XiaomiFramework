package com.android.server.audio;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.IUidObserver;
import android.app.NotificationManager;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.ContentObserver;
import android.hardware.hdmi.HdmiAudioSystemClient;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.hardware.hdmi.HdmiTvClient;
import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioManagerInternal;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecordingConfiguration;
import android.media.AudioRoutesInfo;
import android.media.AudioServiceInjector;
import android.media.AudioSystem;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioRoutesObserver;
import android.media.IAudioServerStateDispatcher;
import android.media.IAudioService;
import android.media.IPlaybackConfigDispatcher;
import android.media.IRecordingConfigDispatcher;
import android.media.IRingtonePlayer;
import android.media.IVolumeController;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MiuiAudioRecord;
import android.media.PlayerBase;
import android.media.SoundPool;
import android.media.VolumePolicy;
import android.media.audiopolicy.AudioMix;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.AudioProductStrategy;
import android.media.audiopolicy.AudioVolumeGroup;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionCallback;
import android.media.projection.IMediaProjectionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.SparseIntArray;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.BatteryService;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.audio.AudioEventLogger;
import com.android.server.audio.AudioServiceEvents;
import com.android.server.pm.DumpState;
import com.android.server.pm.UserManagerService;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.utils.PriorityDump;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.miui.server.AudioQueryWeatherService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.util.AudioManagerHelper;
import org.xmlpull.v1.XmlPullParserException;

public class AudioService extends IAudioService.Stub implements AccessibilityManager.TouchExplorationStateChangeListener, AccessibilityManager.AccessibilityServicesStateChangeListener {
    private static final String ASSET_FILE_VERSION = "1.0";
    private static final String ATTR_ASSET_FILE = "file";
    private static final String ATTR_ASSET_ID = "id";
    private static final String ATTR_GROUP_NAME = "name";
    private static final String ATTR_VERSION = "version";
    static final int CONNECTION_STATE_CONNECTED = 1;
    static final int CONNECTION_STATE_DISCONNECTED = 0;
    protected static final boolean DEBUG_AP = true;
    protected static final boolean DEBUG_DEVICES = true;
    protected static final boolean DEBUG_MODE = true;
    protected static final boolean DEBUG_SCO = true;
    protected static final boolean DEBUG_VOL = true;
    private static final int DEFAULT_STREAM_TYPE_OVERRIDE_DELAY_MS = 0;
    protected static final int DEFAULT_VOL_STREAM_NO_PLAYBACK = 3;
    private static final int DEVICE_MEDIA_UNMUTED_ON_PLUG = 67266444;
    private static final int FLAG_ADJUST_VOLUME = 1;
    private static final String GROUP_TOUCH_SOUNDS = "touch_sounds";
    private static final int INDICATE_SYSTEM_READY_RETRY_DELAY_MS = 1000;
    private static final String LOCK_VOICE_ASSIST_STREAM = "lock_voiceassit_stream";
    static final int LOG_NB_EVENTS_DEVICE_CONNECTION = 30;
    static final int LOG_NB_EVENTS_DYN_POLICY = 10;
    static final int LOG_NB_EVENTS_FORCE_USE = 20;
    static final int LOG_NB_EVENTS_PHONE_STATE = 20;
    static final int LOG_NB_EVENTS_VOLUME = 40;
    protected static int[] MAX_STREAM_VOLUME = {5, 7, 7, 15, 7, 7, 15, 7, 15, 15, 15, 15};
    protected static int[] MIN_STREAM_VOLUME = {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    private static final int MSG_ACCESSORY_PLUG_MEDIA_UNMUTE = 21;
    private static final int MSG_AUDIO_SERVER_DIED = 4;
    private static final int MSG_BT_HEADSET_CNCT_FAILED = 9;
    private static final int MSG_CHECK_MUSIC_ACTIVE = 11;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME = 12;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED = 13;
    private static final int MSG_DISABLE_AUDIO_FOR_UID = 100;
    private static final int MSG_DISPATCH_AUDIO_SERVER_STATE = 23;
    private static final int MSG_DYN_POLICY_MIX_STATE_UPDATE = 19;
    private static final int MSG_ENABLE_SURROUND_FORMATS = 24;
    private static final int MSG_HDMI_VOLUME_CHECK = 28;
    private static final int MSG_INDICATE_SYSTEM_READY = 20;
    private static final int MSG_LOAD_SOUND_EFFECTS = 7;
    private static final int MSG_NOTIFY_VOL_EVENT = 22;
    private static final int MSG_OBSERVE_DEVICES_FOR_ALL_STREAMS = 27;
    private static final int MSG_PERSIST_MUSIC_ACTIVE_MS = 17;
    private static final int MSG_PERSIST_RINGER_MODE = 3;
    private static final int MSG_PERSIST_SAFE_VOLUME_STATE = 14;
    private static final int MSG_PERSIST_VOLUME = 1;
    private static final int MSG_PLAYBACK_CONFIG_CHANGE = 29;
    private static final int MSG_PLAY_SOUND_EFFECT = 5;
    private static final int MSG_SET_ALL_VOLUMES = 10;
    private static final int MSG_SET_DEVICE_STREAM_VOLUME = 26;
    private static final int MSG_SET_DEVICE_VOLUME = 0;
    private static final int MSG_SET_FORCE_USE = 8;
    private static final int MSG_SYSTEM_READY = 16;
    private static final int MSG_UNLOAD_SOUND_EFFECTS = 15;
    private static final int MSG_UNMUTE_STREAM = 18;
    private static final int MSG_UPDATE_RINGER_MODE = 25;
    private static final int MUSIC_ACTIVE_POLL_PERIOD_MS = 60000;
    private static final int NUM_SOUNDPOOL_CHANNELS = 4;
    private static final int PERSIST_DELAY = 500;
    private static final String[] RINGER_MODE_NAMES = {"SILENT", "VIBRATE", PriorityDump.PRIORITY_ARG_NORMAL};
    private static final int SAFE_MEDIA_VOLUME_ACTIVE = 3;
    private static final int SAFE_MEDIA_VOLUME_DISABLED = 1;
    private static final int SAFE_MEDIA_VOLUME_INACTIVE = 2;
    private static final int SAFE_MEDIA_VOLUME_NOT_CONFIGURED = 0;
    private static final int SAFE_VOLUME_CONFIGURE_TIMEOUT_MS = 30000;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int SENDMSG_REPLACE = 0;
    private static final int SOUND_EFFECTS_LOAD_TIMEOUT_MS = 5000;
    private static final String SOUND_EFFECTS_PATH = "/media/audio/ui/";
    /* access modifiers changed from: private */
    public static final List<String> SOUND_EFFECT_FILES = new ArrayList();
    private static final int[] STREAM_VOLUME_OPS = {34, 36, 35, 36, 37, 38, 39, 36, 36, 36, 64, 36};
    private static final String TAG = "AS.AudioService";
    private static final String TAG_ASSET = "asset";
    private static final String TAG_AUDIO_ASSETS = "audio_assets";
    private static final String TAG_GROUP = "group";
    private static final int TOUCH_EXPLORE_STREAM_TYPE_OVERRIDE_DELAY_MS = 1000;
    private static final int UNMUTE_STREAM_DELAY = 350;
    private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = 72000000;
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    protected static int[] mStreamVolumeAlias;
    static final AudioEventLogger sDeviceLogger = new AudioEventLogger(30, "wired/A2DP/hearing aid device connection");
    static final AudioEventLogger sForceUseLogger = new AudioEventLogger(20, "force use (logged before setForceUse() is executed)");
    private static boolean sIndependentA11yVolume = false;
    /* access modifiers changed from: private */
    public static int sSoundEffectVolumeDb;
    private static int sStreamOverrideDelayMs;
    static final AudioEventLogger sVolumeLogger = new AudioEventLogger(40, "volume changes (logged when command received by AudioService)");
    /* access modifiers changed from: private */
    public final int[][] SOUND_EFFECT_FILES_MAP = ((int[][]) Array.newInstance(int.class, new int[]{10, 2}));
    private final int[] STREAM_VOLUME_ALIAS_DEFAULT = {0, 2, 2, 3, 4, 2, 6, 2, 2, 3, 3, 11};
    private final int[] STREAM_VOLUME_ALIAS_TELEVISION = {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
    private final int[] STREAM_VOLUME_ALIAS_VOICE = {0, 2, 2, 3, 4, 2, 6, 2, 2, 3, 3, 11};
    int mAbsVolumeMultiModeCaseDevices = 134217728;
    /* access modifiers changed from: private */
    public int[] mAccessibilityServiceUids;
    /* access modifiers changed from: private */
    public final Object mAccessibilityServiceUidsLock = new Object();
    private final ActivityManagerInternal mActivityManagerInternal;
    private final AppOpsManager mAppOps;
    @GuardedBy({"mSettingsLock"})
    private int mAssistantUid;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock mAudioEventWakeLock;
    /* access modifiers changed from: private */
    public AudioHandler mAudioHandler;
    /* access modifiers changed from: private */
    public final HashMap<IBinder, AudioPolicyProxy> mAudioPolicies = new HashMap<>();
    @GuardedBy({"mAudioPolicies"})
    private int mAudioPolicyCounter = 0;
    private final AudioQueryWeatherService mAudioQueryWeatherService;
    /* access modifiers changed from: private */
    public HashMap<IBinder, AsdProxy> mAudioServerStateListeners = new HashMap<>();
    private final AudioSystem.ErrorCallback mAudioSystemCallback = new AudioSystem.ErrorCallback() {
        public void onError(int error) {
            if (error == 100) {
                AudioService.this.mRecordMonitor.onAudioServerDied();
                AudioService.sendMsg(AudioService.this.mAudioHandler, 4, 1, 0, 0, (Object) null, 0);
                AudioService.sendMsg(AudioService.this.mAudioHandler, 23, 2, 0, 0, (Object) null, 0);
            }
        }
    };
    private AudioSystemThread mAudioSystemThread;
    /* access modifiers changed from: private */
    @GuardedBy({"mSettingsLock"})
    public boolean mCameraSoundForced;
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final AudioDeviceBroker mDeviceBroker;
    private boolean mDockAudioMediaEnabled = true;
    /* access modifiers changed from: private */
    public int mDockState = 0;
    private final AudioSystem.DynamicPolicyCallback mDynPolicyCallback = new AudioSystem.DynamicPolicyCallback() {
        public void onDynamicPolicyMixStateUpdate(String regId, int state) {
            if (!TextUtils.isEmpty(regId)) {
                AudioService.sendMsg(AudioService.this.mAudioHandler, 19, 2, state, 0, regId, 0);
            }
        }
    };
    private final AudioEventLogger mDynPolicyLogger = new AudioEventLogger(10, "dynamic policy events (logged when command received by AudioService)");
    /* access modifiers changed from: private */
    public String mEnabledSurroundFormats;
    /* access modifiers changed from: private */
    public int mEncodedSurroundMode;
    /* access modifiers changed from: private */
    public IAudioPolicyCallback mExtVolumeController;
    /* access modifiers changed from: private */
    public final Object mExtVolumeControllerLock = new Object();
    int mFixedVolumeDevices = 2890752;
    /* access modifiers changed from: private */
    public ForceControlStreamClient mForceControlStreamClient = null;
    /* access modifiers changed from: private */
    public final Object mForceControlStreamLock = new Object();
    int mFullVolumeDevices = 0;
    private final boolean mHasVibrator;
    @GuardedBy({"mHdmiClientLock"})
    private HdmiAudioSystemClient mHdmiAudioSystemClient;
    /* access modifiers changed from: private */
    public boolean mHdmiCecSink;
    /* access modifiers changed from: private */
    public final Object mHdmiClientLock = new Object();
    private MyDisplayStatusCallback mHdmiDisplayStatusCallback = new MyDisplayStatusCallback();
    /* access modifiers changed from: private */
    @GuardedBy({"mHdmiClientLock"})
    public HdmiControlManager mHdmiManager;
    @GuardedBy({"mHdmiClientLock"})
    private HdmiPlaybackClient mHdmiPlaybackClient;
    private boolean mHdmiSystemAudioSupported = false;
    @GuardedBy({"mHdmiClientLock"})
    private HdmiTvClient mHdmiTvClient;
    /* access modifiers changed from: private */
    public final boolean mIsSingleVolume;
    /* access modifiers changed from: private */
    public boolean mLockVoiseAssistStream = false;
    private long mLoweredFromNormalToVibrateTime;
    private int mMcc = 0;
    /* access modifiers changed from: private */
    public final MediaFocusControl mMediaFocusControl;
    private int mMode = 0;
    private final AudioEventLogger mModeLogger = new AudioEventLogger(20, "phone state (logged after successfull call to AudioSystem.setPhoneState(int))");
    /* access modifiers changed from: private */
    public final boolean mMonitorRotation;
    private int mMusicActiveMs;
    private int mMuteAffectedStreams;
    private NotificationManager mNm;
    private StreamVolumeCommand mPendingVolumeCommand;
    private final int mPlatformType;
    /* access modifiers changed from: private */
    public final PlaybackActivityMonitor mPlaybackMonitor;
    /* access modifiers changed from: private */
    public float[] mPrescaleAbsoluteVolume = {0.5f, 0.7f, 0.85f, 0.9f, 0.95f};
    private int mPrevVolDirection = 0;
    private IMediaProjectionManager mProjectionService;
    private final BroadcastReceiver mReceiver = new AudioServiceBroadcastReceiver();
    /* access modifiers changed from: private */
    public final RecordingActivityMonitor mRecordMonitor;
    private int mRingerAndZenModeMutedStreams;
    @GuardedBy({"mSettingsLock"})
    private int mRingerMode;
    private int mRingerModeAffectedStreams = 0;
    /* access modifiers changed from: private */
    public AudioManagerInternal.RingerModeDelegate mRingerModeDelegate;
    @GuardedBy({"mSettingsLock"})
    private int mRingerModeExternal = -1;
    private volatile IRingtonePlayer mRingtonePlayer;
    private ArrayList<RmtSbmxFullVolDeathHandler> mRmtSbmxFullVolDeathHandlers = new ArrayList<>();
    private int mRmtSbmxFullVolRefCount = 0;
    RoleObserver mRoleObserver;
    final int mSafeMediaVolumeDevices = 67108876;
    private int mSafeMediaVolumeIndex;
    private int mSafeMediaVolumeState;
    private final Object mSafeMediaVolumeStateLock = new Object();
    private float mSafeUsbMediaVolumeDbfs;
    private int mSafeUsbMediaVolumeIndex;
    @GuardedBy({"mDeviceBroker.mSetModeLock"})
    final ArrayList<SetModeDeathHandler> mSetModeDeathHandlers = new ArrayList<>();
    /* access modifiers changed from: private */
    public final Object mSettingsLock = new Object();
    private SettingsObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public final Object mSoundEffectsLock = new Object();
    /* access modifiers changed from: private */
    public SoundPool mSoundPool;
    /* access modifiers changed from: private */
    public SoundPoolCallback mSoundPoolCallBack;
    /* access modifiers changed from: private */
    public SoundPoolListenerThread mSoundPoolListenerThread;
    /* access modifiers changed from: private */
    public Looper mSoundPoolLooper = null;
    /* access modifiers changed from: private */
    public VolumeStreamState[] mStreamStates;
    /* access modifiers changed from: private */
    public boolean mSurroundModeChanged;
    /* access modifiers changed from: private */
    public boolean mSystemReady;
    private final IUidObserver mUidObserver = new IUidObserver.Stub() {
        public void onUidStateChanged(int uid, int procState, long procStateSeq) {
        }

        public void onUidGone(int uid, boolean disabled) {
            disableAudioForUid(false, uid);
        }

        public void onUidActive(int uid) throws RemoteException {
        }

        public void onUidIdle(int uid, boolean disabled) {
        }

        public void onUidCachedChanged(int uid, boolean cached) {
            disableAudioForUid(cached, uid);
        }

        private void disableAudioForUid(boolean disable, int uid) {
            AudioService audioService = AudioService.this;
            audioService.queueMsgUnderWakeLock(audioService.mAudioHandler, 100, disable ? 1 : 0, uid, (Object) null, 0);
        }
    };
    /* access modifiers changed from: private */
    public final boolean mUseFixedVolume;
    private final UserManagerInternal mUserManagerInternal;
    private final UserManagerInternal.UserRestrictionsListener mUserRestrictionsListener = new AudioServiceUserRestrictionsListener();
    /* access modifiers changed from: private */
    public boolean mUserSelectedVolumeControlStream = false;
    /* access modifiers changed from: private */
    public boolean mUserSwitchedReceived;
    private int mVibrateSetting;
    private Vibrator mVibrator;
    private AtomicBoolean mVoiceActive = new AtomicBoolean(false);
    private final IPlaybackConfigDispatcher mVoiceActivityMonitor = new IPlaybackConfigDispatcher.Stub() {
        public void dispatchPlaybackConfigChange(List<AudioPlaybackConfiguration> configs, boolean flush) {
            AudioService.sendMsg(AudioService.this.mAudioHandler, 29, 0, 0, 0, configs, 0);
        }
    };
    /* access modifiers changed from: private */
    public int mVolumeControlStream = -1;
    /* access modifiers changed from: private */
    public final VolumeController mVolumeController = new VolumeController();
    private VolumePolicy mVolumePolicy = VolumePolicy.DEFAULT;
    /* access modifiers changed from: private */
    public int mZenMode;
    private int mZenModeAffectedStreams = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BtProfileConnectionState {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectionState {
    }

    static /* synthetic */ int access$9708(AudioService x0) {
        int i = x0.mAudioPolicyCounter;
        x0.mAudioPolicyCounter = i + 1;
        return i;
    }

    private void persistVolumeIfNeeded(int device, VolumeStreamState streamState, boolean persist) {
        if (persist) {
            sendMsg(this.mAudioHandler, 1, 2, device, 0, streamState, 0);
        }
    }

    private boolean isPlatformVoice() {
        return this.mPlatformType == 1;
    }

    /* access modifiers changed from: package-private */
    public boolean isPlatformTelevision() {
        return this.mPlatformType == 2;
    }

    /* access modifiers changed from: package-private */
    public boolean isPlatformAutomotive() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }

    /* access modifiers changed from: package-private */
    public int getVssVolumeForDevice(int stream, int device) {
        return this.mStreamStates[stream].getIndex(device);
    }

    public static String makeAlsaAddressString(int card, int device) {
        return "card=" + card + ";device=" + device + ";";
    }

    public static final class Lifecycle extends SystemService {
        private AudioService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new AudioService(context);
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.audio.AudioService, android.os.IBinder] */
        public void onStart() {
            publishBinderService("audio", this.mService);
        }

        public void onBootPhase(int phase) {
            if (phase == 550) {
                this.mService.systemReady();
            }
        }
    }

    public AudioService(Context context) {
        Context context2 = context;
        AudioServiceInjector.adjustMaxStreamVolume(MAX_STREAM_VOLUME);
        AudioServiceInjector.adjustMinStreamVolume(MIN_STREAM_VOLUME);
        this.mContext = context2;
        this.mContentResolver = context.getContentResolver();
        this.mAppOps = (AppOpsManager) context2.getSystemService("appops");
        this.mPlatformType = AudioSystem.getPlatformType(context);
        this.mIsSingleVolume = AudioSystem.isSingleVolume(context);
        this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mAudioEventWakeLock = ((PowerManager) context2.getSystemService("power")).newWakeLock(1, "handleAudioEvent");
        this.mVibrator = (Vibrator) context2.getSystemService("vibrator");
        Vibrator vibrator = this.mVibrator;
        this.mHasVibrator = vibrator == null ? false : vibrator.hasVibrator();
        if (AudioProductStrategy.getAudioProductStrategies().size() > 0) {
            for (int streamType = AudioSystem.getNumStreamTypes() - 1; streamType >= 0; streamType--) {
                AudioAttributes attr = AudioProductStrategy.getAudioAttributesForStrategyWithLegacyStreamType(streamType);
                int maxVolume = AudioSystem.getMaxVolumeIndexForAttributes(attr);
                if (maxVolume != -1) {
                    MAX_STREAM_VOLUME[streamType] = maxVolume;
                }
                int minVolume = AudioSystem.getMinVolumeIndexForAttributes(attr);
                if (minVolume != -1) {
                    MIN_STREAM_VOLUME[streamType] = minVolume;
                }
            }
        }
        int maxCallVolume = SystemProperties.getInt("ro.config.vc_call_vol_steps", -1);
        if (maxCallVolume != -1) {
            MAX_STREAM_VOLUME[0] = maxCallVolume;
        }
        int defaultCallVolume = SystemProperties.getInt("ro.config.vc_call_vol_default", -1);
        if (defaultCallVolume == -1 || defaultCallVolume > MAX_STREAM_VOLUME[0] || defaultCallVolume < MIN_STREAM_VOLUME[0]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[0] = (maxCallVolume * 3) / 4;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[0] = defaultCallVolume;
        }
        int maxMusicVolume = SystemProperties.getInt("ro.config.media_vol_steps", -1);
        if (maxMusicVolume != -1) {
            MAX_STREAM_VOLUME[3] = maxMusicVolume;
        }
        int defaultMusicVolume = SystemProperties.getInt("ro.config.media_vol_default", -1);
        if (defaultMusicVolume != -1 && defaultMusicVolume <= MAX_STREAM_VOLUME[3] && defaultMusicVolume >= MIN_STREAM_VOLUME[3]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[3] = defaultMusicVolume;
        } else if (isPlatformTelevision()) {
            AudioSystem.DEFAULT_STREAM_VOLUME[3] = MAX_STREAM_VOLUME[3] / 4;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[3] = MAX_STREAM_VOLUME[3] / 3;
        }
        int maxAlarmVolume = SystemProperties.getInt("ro.config.alarm_vol_steps", -1);
        if (maxAlarmVolume != -1) {
            MAX_STREAM_VOLUME[4] = maxAlarmVolume;
        }
        int defaultAlarmVolume = SystemProperties.getInt("ro.config.alarm_vol_default", -1);
        if (defaultAlarmVolume == -1 || defaultAlarmVolume > MAX_STREAM_VOLUME[4]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[4] = (MAX_STREAM_VOLUME[4] * 6) / 7;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[4] = defaultAlarmVolume;
        }
        int maxSystemVolume = SystemProperties.getInt("ro.config.system_vol_steps", -1);
        if (maxSystemVolume != -1) {
            MAX_STREAM_VOLUME[1] = maxSystemVolume;
        }
        int defaultSystemVolume = SystemProperties.getInt("ro.config.system_vol_default", -1);
        if (defaultSystemVolume == -1 || defaultSystemVolume > MAX_STREAM_VOLUME[1]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[1] = MAX_STREAM_VOLUME[1];
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[1] = defaultSystemVolume;
        }
        sSoundEffectVolumeDb = context.getResources().getInteger(17694898);
        createAudioSystemThread();
        AudioSystem.setErrorCallback(this.mAudioSystemCallback);
        boolean cameraSoundForced = readCameraSoundForced();
        this.mCameraSoundForced = new Boolean(cameraSoundForced).booleanValue();
        sendMsg(this.mAudioHandler, 8, 2, 4, cameraSoundForced ? 11 : 0, new String("AudioService ctor"), 0);
        this.mSafeMediaVolumeState = Settings.Global.getInt(this.mContentResolver, "audio_safe_volume_state", 0);
        this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(17694881) * 10;
        this.mUseFixedVolume = this.mContext.getResources().getBoolean(17891564);
        this.mDeviceBroker = new AudioDeviceBroker(this.mContext, this);
        updateStreamVolumeAlias(false, TAG);
        readPersistedSettings();
        readUserRestrictions();
        this.mSettingsObserver = new SettingsObserver();
        createStreamStates();
        this.mSafeUsbMediaVolumeIndex = getSafeUsbMediaVolumeIndex();
        this.mPlaybackMonitor = new PlaybackActivityMonitor(context2, MAX_STREAM_VOLUME[4]);
        this.mMediaFocusControl = new MediaFocusControl(this.mContext, this.mPlaybackMonitor);
        this.mAudioQueryWeatherService = new AudioQueryWeatherService(this.mContext);
        this.mAudioQueryWeatherService.onCreate();
        this.mRecordMonitor = new RecordingActivityMonitor(this.mContext);
        readAndSetLowRamDevice();
        this.mRingerAndZenModeMutedStreams = 0;
        setRingerModeInt(getRingerModeInternal(), false);
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED");
        intentFilter.addAction("android.intent.action.DOCK_EVENT");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_BACKGROUND");
        intentFilter.addAction("android.intent.action.USER_FOREGROUND");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        this.mMonitorRotation = SystemProperties.getBoolean("ro.audio.monitorRotation", false);
        if (this.mMonitorRotation) {
            RotationHelper.init(this.mContext, this.mAudioHandler);
        }
        intentFilter.addAction("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION");
        intentFilter.addAction("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION");
        int i = defaultAlarmVolume;
        int i2 = maxAlarmVolume;
        int i3 = defaultMusicVolume;
        context.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, intentFilter, (String) null, (Handler) null);
        LocalServices.addService(AudioManagerInternal.class, new AudioServiceInternal());
        this.mUserManagerInternal.addUserRestrictionsListener(this.mUserRestrictionsListener);
        this.mRecordMonitor.initMonitor();
        float[] preScale = {this.mContext.getResources().getFraction(18022403, 1, 1), this.mContext.getResources().getFraction(18022404, 1, 1), this.mContext.getResources().getFraction(18022405, 1, 1)};
        for (int i4 = 0; i4 < preScale.length; i4++) {
            if (0.0f <= preScale[i4] && preScale[i4] <= 1.0f) {
                this.mPrescaleAbsoluteVolume[i4] = preScale[i4];
            }
        }
        this.mZenMode = MiuiSettings.SilenceMode.getZenMode(this.mContext);
        AudioServiceInjector.updateNotificationMode(this.mContext);
    }

    public void systemReady() {
        sendMsg(this.mAudioHandler, 16, 2, 0, 0, (Object) null, 0);
    }

    public void onSystemReady() {
        this.mSystemReady = true;
        scheduleLoadSoundEffects();
        this.mDeviceBroker.onSystemReady();
        int i = 0;
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.hdmi.cec")) {
            synchronized (this.mHdmiClientLock) {
                this.mHdmiManager = (HdmiControlManager) this.mContext.getSystemService(HdmiControlManager.class);
                this.mHdmiTvClient = this.mHdmiManager.getTvClient();
                if (this.mHdmiTvClient != null) {
                    this.mFixedVolumeDevices &= -2883587;
                }
                this.mHdmiPlaybackClient = this.mHdmiManager.getPlaybackClient();
                if (this.mHdmiPlaybackClient != null) {
                    this.mFixedVolumeDevices &= -1025;
                    this.mFullVolumeDevices |= 1024;
                }
                this.mHdmiCecSink = false;
                this.mHdmiAudioSystemClient = this.mHdmiManager.getAudioSystemClient();
            }
        }
        this.mNm = (NotificationManager) this.mContext.getSystemService("notification");
        AudioHandler audioHandler = this.mAudioHandler;
        if (!SystemProperties.getBoolean("audio.safemedia.bypass", false)) {
            i = SAFE_VOLUME_CONFIGURE_TIMEOUT_MS;
        }
        sendMsg(audioHandler, 13, 0, 0, 0, TAG, i);
        initA11yMonitoring();
        this.mRoleObserver = new RoleObserver();
        this.mRoleObserver.register();
        onIndicateSystemReady();
    }

    class RoleObserver implements OnRoleHoldersChangedListener {
        private final Executor mExecutor;
        private RoleManager mRm;

        RoleObserver() {
            this.mExecutor = AudioService.this.mContext.getMainExecutor();
        }

        public void register() {
            this.mRm = (RoleManager) AudioService.this.mContext.getSystemService("role");
            RoleManager roleManager = this.mRm;
            if (roleManager != null) {
                roleManager.addOnRoleHoldersChangedListenerAsUser(this.mExecutor, this, UserHandle.ALL);
                AudioService.this.updateAssistantUId(true);
            }
        }

        public void onRoleHoldersChanged(String roleName, UserHandle user) {
            if ("android.app.role.ASSISTANT".equals(roleName)) {
                AudioService.this.updateAssistantUId(false);
            }
        }

        public String getAssistantRoleHolder() {
            RoleManager roleManager = this.mRm;
            if (roleManager == null) {
                return "";
            }
            List<String> assistants = roleManager.getRoleHolders("android.app.role.ASSISTANT");
            return assistants.size() == 0 ? "" : assistants.get(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void onIndicateSystemReady() {
        if (AudioSystem.systemReady() != 0) {
            sendMsg(this.mAudioHandler, 20, 0, 0, 0, (Object) null, 1000);
        }
    }

    public void onAudioServerDied() {
        int forDock;
        int forSys;
        if (!this.mSystemReady || AudioSystem.checkAudioFlinger() != 0) {
            Log.e(TAG, "Audioserver died.");
            sendMsg(this.mAudioHandler, 4, 1, 0, 0, (Object) null, 500);
            return;
        }
        Log.e(TAG, "Audioserver started.");
        AudioSystem.setParameters("restarting=true");
        readAndSetLowRamDevice();
        this.mDeviceBroker.onAudioServerDied();
        if (AudioSystem.setPhoneState(this.mMode) == 0) {
            this.mModeLogger.log(new AudioEventLogger.StringEvent("onAudioServerDied causes setPhoneState(" + AudioSystem.modeToString(this.mMode) + ")"));
        }
        synchronized (this.mSettingsLock) {
            forDock = 0;
            forSys = this.mCameraSoundForced ? 11 : 0;
        }
        this.mDeviceBroker.setForceUse_Async(4, forSys, "onAudioServerDied");
        for (int streamType = AudioSystem.getNumStreamTypes() - 1; streamType >= 0; streamType--) {
            VolumeStreamState streamState = this.mStreamStates[streamType];
            AudioSystem.initStreamVolume(streamType, streamState.mIndexMin / 10, streamState.mIndexMax / 10);
            streamState.applyAllVolumes();
        }
        updateMasterMono(this.mContentResolver);
        updateMasterBalance(this.mContentResolver);
        setRingerModeInt(getRingerModeInternal(), false);
        if (this.mMonitorRotation) {
            RotationHelper.updateOrientation();
        }
        synchronized (this.mSettingsLock) {
            if (this.mDockAudioMediaEnabled) {
                forDock = 8;
            }
            this.mDeviceBroker.setForceUse_Async(3, forDock, "onAudioServerDied");
            sendEncodedSurroundMode(this.mContentResolver, "onAudioServerDied");
            sendEnabledSurroundFormats(this.mContentResolver, true);
            updateAssistantUId(true);
            updateRttEanbled(this.mContentResolver);
        }
        synchronized (this.mAccessibilityServiceUidsLock) {
            AudioSystem.setA11yServicesUids(this.mAccessibilityServiceUids);
        }
        synchronized (this.mHdmiClientLock) {
            if (!(this.mHdmiManager == null || this.mHdmiTvClient == null)) {
                setHdmiSystemAudioSupported(this.mHdmiSystemAudioSupported);
            }
        }
        synchronized (this.mAudioPolicies) {
            for (AudioPolicyProxy policy : this.mAudioPolicies.values()) {
                policy.connectMixes();
            }
        }
        onIndicateSystemReady();
        AudioSystem.setParameters("restarting=false");
        sendMsg(this.mAudioHandler, 23, 2, 1, 0, (Object) null, 0);
    }

    /* access modifiers changed from: private */
    public void onDispatchAudioServerStateChange(boolean state) {
        synchronized (this.mAudioServerStateListeners) {
            for (AsdProxy asdp : this.mAudioServerStateListeners.values()) {
                try {
                    asdp.callback().dispatchAudioServerStateChange(state);
                } catch (RemoteException e) {
                    Log.w(TAG, "Could not call dispatchAudioServerStateChange()", e);
                }
            }
        }
    }

    private void createAudioSystemThread() {
        this.mAudioSystemThread = new AudioSystemThread();
        this.mAudioSystemThread.start();
        waitForAudioHandlerCreation();
    }

    private void waitForAudioHandlerCreation() {
        synchronized (this) {
            while (this.mAudioHandler == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting on volume handler.");
                }
            }
        }
    }

    public List<AudioProductStrategy> getAudioProductStrategies() {
        return AudioProductStrategy.getAudioProductStrategies();
    }

    public List<AudioVolumeGroup> getAudioVolumeGroups() {
        return AudioVolumeGroup.getAudioVolumeGroups();
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    private void checkAllAliasStreamVolumes() {
        synchronized (this.mDeviceBroker.mFixLock) {
            synchronized (this.mSettingsLock) {
                synchronized (VolumeStreamState.class) {
                    int numStreamTypes = AudioSystem.getNumStreamTypes();
                    for (int streamType = 0; streamType < numStreamTypes; streamType++) {
                        this.mStreamStates[streamType].setAllIndexes(this.mStreamStates[mStreamVolumeAlias[streamType]], TAG);
                        if (!this.mStreamStates[streamType].mIsMuted) {
                            this.mStreamStates[streamType].applyAllVolumes();
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void postCheckVolumeCecOnHdmiConnection(int state, String caller) {
        sendMsg(this.mAudioHandler, 28, 0, state, 0, caller, 0);
    }

    /* access modifiers changed from: private */
    public void onCheckVolumeCecOnHdmiConnection(int state, String caller) {
        if (state == 1) {
            if (isPlatformTelevision()) {
                checkAddAllFixedVolumeDevices(1024, caller);
                synchronized (this.mHdmiClientLock) {
                    if (!(this.mHdmiManager == null || this.mHdmiPlaybackClient == null)) {
                        this.mHdmiCecSink = false;
                        this.mHdmiPlaybackClient.queryDisplayStatus(this.mHdmiDisplayStatusCallback);
                    }
                }
            }
            sendEnabledSurroundFormats(this.mContentResolver, true);
        } else if (isPlatformTelevision()) {
            synchronized (this.mHdmiClientLock) {
                if (this.mHdmiManager != null) {
                    this.mHdmiCecSink = false;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkAddAllFixedVolumeDevices(int device, String caller) {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = 0; streamType < numStreamTypes; streamType++) {
            if (!this.mStreamStates[streamType].hasIndexForDevice(device)) {
                VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                volumeStreamStateArr[streamType].setIndex(volumeStreamStateArr[mStreamVolumeAlias[streamType]].getIndex(1073741824), device, caller);
            }
            this.mStreamStates[streamType].checkFixedVolumeDevices();
        }
    }

    private void checkAllFixedVolumeDevices() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = 0; streamType < numStreamTypes; streamType++) {
            this.mStreamStates[streamType].checkFixedVolumeDevices();
        }
    }

    private void checkAllFixedVolumeDevices(int streamType) {
        this.mStreamStates[streamType].checkFixedVolumeDevices();
    }

    private void checkMuteAffectedStreams() {
        int i = 0;
        while (true) {
            VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
            if (i < volumeStreamStateArr.length) {
                VolumeStreamState vss = volumeStreamStateArr[i];
                if (!(vss.mIndexMin <= 0 || vss.mStreamType == 0 || vss.mStreamType == 6)) {
                    this.mMuteAffectedStreams &= ~(1 << vss.mStreamType);
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void createStreamStates() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        VolumeStreamState[] streams = new VolumeStreamState[numStreamTypes];
        this.mStreamStates = streams;
        for (int i = 0; i < numStreamTypes; i++) {
            streams[i] = new VolumeStreamState(Settings.System.VOLUME_SETTINGS_INT[mStreamVolumeAlias[i]], i);
        }
        checkAllFixedVolumeDevices();
        checkAllAliasStreamVolumes();
        checkMuteAffectedStreams();
        updateDefaultVolumes();
    }

    private void updateDefaultVolumes() {
        for (int stream = 0; stream < this.mStreamStates.length; stream++) {
            if (stream != mStreamVolumeAlias[stream]) {
                int[] iArr = AudioSystem.DEFAULT_STREAM_VOLUME;
                int[] iArr2 = AudioSystem.DEFAULT_STREAM_VOLUME;
                int[] iArr3 = mStreamVolumeAlias;
                iArr[stream] = rescaleIndex(iArr2[iArr3[stream]], iArr3[stream], stream);
            }
        }
    }

    private void dumpStreamStates(PrintWriter pw) {
        pw.println("\nStream volumes (device: index)");
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            pw.println("- " + AudioSystem.STREAM_NAMES[i] + ":");
            this.mStreamStates[i].dump(pw);
            pw.println("");
        }
        pw.print("\n- mute affected streams = 0x");
        pw.println(Integer.toHexString(this.mMuteAffectedStreams));
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:48:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateStreamVolumeAlias(boolean r23, java.lang.String r24) {
        /*
            r22 = this;
            r1 = r22
            r2 = r24
            boolean r0 = sIndependentA11yVolume
            r3 = 10
            if (r0 == 0) goto L_0x000c
            r0 = r3
            goto L_0x000d
        L_0x000c:
            r0 = 3
        L_0x000d:
            r4 = r0
            boolean r0 = r1.mIsSingleVolume
            if (r0 == 0) goto L_0x0018
            int[] r0 = r1.STREAM_VOLUME_ALIAS_TELEVISION
            mStreamVolumeAlias = r0
            r0 = 3
            goto L_0x0029
        L_0x0018:
            int r0 = r1.mPlatformType
            r5 = 1
            if (r0 == r5) goto L_0x0023
            int[] r0 = r1.STREAM_VOLUME_ALIAS_DEFAULT
            mStreamVolumeAlias = r0
            r0 = 3
            goto L_0x0029
        L_0x0023:
            int[] r0 = r1.STREAM_VOLUME_ALIAS_VOICE
            mStreamVolumeAlias = r0
            r0 = 2
        L_0x0029:
            boolean r5 = r1.mIsSingleVolume
            r6 = 0
            if (r5 == 0) goto L_0x0031
            r1.mRingerModeAffectedStreams = r6
            goto L_0x0046
        L_0x0031:
            boolean r5 = r22.isInCommunication()
            if (r5 == 0) goto L_0x0040
            r0 = 0
            int r5 = r1.mRingerModeAffectedStreams
            r5 = r5 & -257(0xfffffffffffffeff, float:NaN)
            r1.mRingerModeAffectedStreams = r5
            r5 = r0
            goto L_0x0047
        L_0x0040:
            int r5 = r1.mRingerModeAffectedStreams
            r5 = r5 | 256(0x100, float:3.59E-43)
            r1.mRingerModeAffectedStreams = r5
        L_0x0046:
            r5 = r0
        L_0x0047:
            int[] r0 = mStreamVolumeAlias
            r7 = 8
            r0[r7] = r5
            r0[r3] = r4
            if (r23 == 0) goto L_0x00bc
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates
            if (r0 == 0) goto L_0x00bc
            r22.updateDefaultVolumes()
            java.lang.Object r8 = r1.mSettingsLock
            monitor-enter(r8)
            java.lang.Class<com.android.server.audio.AudioService$VolumeStreamState> r9 = com.android.server.audio.AudioService.VolumeStreamState.class
            monitor-enter(r9)     // Catch:{ all -> 0x00b9 }
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates     // Catch:{ all -> 0x00b6 }
            r0 = r0[r7]     // Catch:{ all -> 0x00b6 }
            com.android.server.audio.AudioService$VolumeStreamState[] r10 = r1.mStreamStates     // Catch:{ all -> 0x00b6 }
            r10 = r10[r5]     // Catch:{ all -> 0x00b6 }
            r0.setAllIndexes(r10, r2)     // Catch:{ all -> 0x00b6 }
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates     // Catch:{ all -> 0x00b6 }
            r0 = r0[r3]     // Catch:{ all -> 0x00b6 }
            java.lang.String[] r10 = android.provider.Settings.System.VOLUME_SETTINGS_INT     // Catch:{ all -> 0x00b6 }
            r10 = r10[r4]     // Catch:{ all -> 0x00b6 }
            java.lang.String unused = r0.mVolumeIndexSettingName = r10     // Catch:{ all -> 0x00b6 }
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates     // Catch:{ all -> 0x00b6 }
            r0 = r0[r3]     // Catch:{ all -> 0x00b6 }
            com.android.server.audio.AudioService$VolumeStreamState[] r10 = r1.mStreamStates     // Catch:{ all -> 0x00b6 }
            r10 = r10[r4]     // Catch:{ all -> 0x00b6 }
            r0.setAllIndexes(r10, r2)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r9)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r8)     // Catch:{ all -> 0x00b9 }
            boolean r0 = sIndependentA11yVolume
            if (r0 == 0) goto L_0x008c
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates
            r0 = r0[r3]
            r0.readSettings()
        L_0x008c:
            int r0 = r22.getRingerModeInternal()
            r1.setRingerModeInt(r0, r6)
            com.android.server.audio.AudioService$AudioHandler r8 = r1.mAudioHandler
            r9 = 10
            r10 = 2
            r11 = 0
            r12 = 0
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates
            r13 = r0[r7]
            r14 = 0
            sendMsg(r8, r9, r10, r11, r12, r13, r14)
            com.android.server.audio.AudioService$AudioHandler r15 = r1.mAudioHandler
            r16 = 10
            r17 = 2
            r18 = 0
            r19 = 0
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r1.mStreamStates
            r20 = r0[r3]
            r21 = 0
            sendMsg(r15, r16, r17, r18, r19, r20, r21)
            goto L_0x00bc
        L_0x00b6:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x00b6 }
            throw r0     // Catch:{ all -> 0x00b9 }
        L_0x00b9:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00b9 }
            throw r0
        L_0x00bc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.updateStreamVolumeAlias(boolean, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public void readDockAudioSettings(ContentResolver cr) {
        int i = 0;
        boolean z = true;
        if (Settings.Global.getInt(cr, "dock_audio_media_enabled", 0) != 1) {
            z = false;
        }
        this.mDockAudioMediaEnabled = z;
        AudioHandler audioHandler = this.mAudioHandler;
        if (this.mDockAudioMediaEnabled) {
            i = 8;
        }
        sendMsg(audioHandler, 8, 2, 3, i, new String("readDockAudioSettings"), 0);
    }

    /* access modifiers changed from: private */
    public void updateMasterMono(ContentResolver cr) {
        boolean masterMono = Settings.System.getIntForUser(cr, "master_mono", 0, -2) == 1;
        Log.d(TAG, String.format("Master mono %b", new Object[]{Boolean.valueOf(masterMono)}));
        AudioSystem.setMasterMono(masterMono);
    }

    /* access modifiers changed from: private */
    public void updateMasterBalance(ContentResolver cr) {
        float masterBalance = Settings.System.getFloatForUser(cr, "master_balance", 0.0f, -2);
        Log.d(TAG, String.format("Master balance %f", new Object[]{Float.valueOf(masterBalance)}));
        if (AudioSystem.setMasterBalance(masterBalance) != 0) {
            Log.e(TAG, String.format("setMasterBalance failed for %f", new Object[]{Float.valueOf(masterBalance)}));
        }
    }

    private void sendEncodedSurroundMode(ContentResolver cr, String eventSource) {
        sendEncodedSurroundMode(Settings.Global.getInt(cr, "encoded_surround_output", 0), eventSource);
    }

    /* access modifiers changed from: private */
    public void sendEncodedSurroundMode(int encodedSurroundMode, String eventSource) {
        int forceSetting = 17;
        if (encodedSurroundMode == 0) {
            forceSetting = 0;
        } else if (encodedSurroundMode == 1) {
            forceSetting = 13;
        } else if (encodedSurroundMode == 2) {
            forceSetting = 14;
        } else if (encodedSurroundMode != 3) {
            Log.e(TAG, "updateSurroundSoundSettings: illegal value " + encodedSurroundMode);
        } else {
            forceSetting = 15;
        }
        if (forceSetting != 17) {
            this.mDeviceBroker.setForceUse_Async(6, forceSetting, eventSource);
        }
    }

    /* access modifiers changed from: private */
    public void sendEnabledSurroundFormats(ContentResolver cr, boolean forceUpdate) {
        String enabledSurroundFormats;
        if (this.mEncodedSurroundMode == 3) {
            String enabledSurroundFormats2 = Settings.Global.getString(cr, "encoded_surround_output_enabled_formats");
            if (enabledSurroundFormats2 == null) {
                enabledSurroundFormats = "";
            } else {
                enabledSurroundFormats = enabledSurroundFormats2;
            }
            if (forceUpdate || !TextUtils.equals(enabledSurroundFormats, this.mEnabledSurroundFormats)) {
                this.mEnabledSurroundFormats = enabledSurroundFormats;
                String[] surroundFormats = TextUtils.split(enabledSurroundFormats, ",");
                ArrayList<Integer> formats = new ArrayList<>();
                for (String format : surroundFormats) {
                    try {
                        int audioFormat = Integer.valueOf(format).intValue();
                        boolean isSurroundFormat = false;
                        int[] iArr = AudioFormat.SURROUND_SOUND_ENCODING;
                        int length = iArr.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                break;
                            } else if (iArr[i] == audioFormat) {
                                isSurroundFormat = true;
                                break;
                            } else {
                                i++;
                            }
                        }
                        if (isSurroundFormat && !formats.contains(Integer.valueOf(audioFormat))) {
                            formats.add(Integer.valueOf(audioFormat));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Invalid enabled surround format:" + format);
                    }
                }
                Settings.Global.putString(this.mContext.getContentResolver(), "encoded_surround_output_enabled_formats", TextUtils.join(",", formats));
                sendMsg(this.mAudioHandler, 24, 2, 0, 0, formats, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void onEnableSurroundFormats(ArrayList<Integer> enabledSurroundFormats) {
        for (int surroundFormat : AudioFormat.SURROUND_SOUND_ENCODING) {
            boolean enabled = enabledSurroundFormats.contains(Integer.valueOf(surroundFormat));
            Log.i(TAG, "enable surround format:" + surroundFormat + " " + enabled + " " + AudioSystem.setSurroundFormatEnabled(surroundFormat, enabled));
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mSettingsLock"})
    public void updateAssistantUId(boolean forceUpdate) {
        int assistantUid = 0;
        String packageName = "";
        RoleObserver roleObserver = this.mRoleObserver;
        if (roleObserver != null) {
            packageName = roleObserver.getAssistantRoleHolder();
        }
        if (TextUtils.isEmpty(packageName)) {
            String assistantName = Settings.Secure.getStringForUser(this.mContentResolver, "voice_interaction_service", -2);
            if (TextUtils.isEmpty(assistantName)) {
                assistantName = Settings.Secure.getStringForUser(this.mContentResolver, "assistant", -2);
            }
            if (!TextUtils.isEmpty(assistantName)) {
                ComponentName componentName = ComponentName.unflattenFromString(assistantName);
                if (componentName == null) {
                    Slog.w(TAG, "Invalid service name for voice_interaction_service: " + assistantName);
                    return;
                }
                packageName = componentName.getPackageName();
            }
        }
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager pm = this.mContext.getPackageManager();
            if (pm.checkPermission("android.permission.CAPTURE_AUDIO_HOTWORD", packageName) == 0) {
                try {
                    assistantUid = pm.getPackageUid(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "updateAssistantUId() could not find UID for package: " + packageName);
                }
            }
        }
        if (assistantUid != this.mAssistantUid || forceUpdate) {
            AudioSystem.setAssistantUid(assistantUid);
            this.mAssistantUid = assistantUid;
        }
    }

    /* access modifiers changed from: private */
    public void updateRttEanbled(ContentResolver cr) {
        boolean rttEnabled = false;
        if (Settings.Secure.getIntForUser(cr, "rtt_calling_mode", 0, -2) != 0) {
            rttEnabled = true;
        }
        AudioSystem.setRttEnabled(rttEnabled);
    }

    private void readPersistedSettings() {
        int i;
        ContentResolver cr = this.mContentResolver;
        int i2 = 2;
        int ringerModeFromSettings = Settings.Global.getInt(cr, "mode_ringer", 2);
        int ringerMode = ringerModeFromSettings;
        if (!isValidRingerMode(ringerMode)) {
            ringerMode = 2;
        }
        if (ringerMode == 1 && !this.mHasVibrator) {
            ringerMode = 0;
        }
        if (ringerMode != ringerModeFromSettings) {
            Settings.Global.putInt(cr, "mode_ringer", ringerMode);
        }
        if (this.mUseFixedVolume || this.mIsSingleVolume) {
            ringerMode = 2;
        }
        synchronized (this.mSettingsLock) {
            this.mRingerMode = ringerMode;
            if (this.mRingerModeExternal == -1) {
                this.mRingerModeExternal = this.mRingerMode;
            }
            if (this.mHasVibrator) {
                i = 2;
            } else {
                i = 0;
            }
            this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(0, 1, i);
            int i3 = this.mVibrateSetting;
            if (!this.mHasVibrator) {
                i2 = 0;
            }
            this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(i3, 0, i2);
            updateRingerAndZenModeAffectedStreams();
            readDockAudioSettings(cr);
            sendEncodedSurroundMode(cr, "readPersistedSettings");
            sendEnabledSurroundFormats(cr, true);
            updateAssistantUId(true);
            updateRttEanbled(cr);
        }
        this.mMuteAffectedStreams = Settings.System.getIntForUser(cr, "mute_streams_affected", 2095, -2);
        updateMasterMono(cr);
        updateMasterBalance(cr);
        broadcastRingerMode("android.media.RINGER_MODE_CHANGED", this.mRingerModeExternal);
        broadcastRingerMode("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", this.mRingerMode);
        broadcastVibrateSetting(0);
        broadcastVibrateSetting(1);
        this.mVolumeController.loadSettings(cr);
    }

    private void readUserRestrictions() {
        int currentUser = getCurrentUserId();
        boolean masterMute = this.mUserManagerInternal.getUserRestriction(currentUser, "disallow_unmute_device") || this.mUserManagerInternal.getUserRestriction(currentUser, "no_adjust_volume");
        if (this.mUseFixedVolume) {
            masterMute = false;
            AudioSystem.setMasterVolume(1.0f);
        }
        Log.d(TAG, String.format("Master mute %s, user=%d", new Object[]{Boolean.valueOf(masterMute), Integer.valueOf(currentUser)}));
        setSystemAudioMute(masterMute);
        AudioSystem.setMasterMute(masterMute);
        broadcastMasterMuteStatus(masterMute);
        boolean microphoneMute = this.mUserManagerInternal.getUserRestriction(currentUser, "no_unmute_microphone");
        Log.d(TAG, String.format("Mic mute %s, user=%d", new Object[]{Boolean.valueOf(microphoneMute), Integer.valueOf(currentUser)}));
        AudioSystem.muteMicrophone(microphoneMute);
    }

    /* access modifiers changed from: private */
    public int rescaleIndex(int index, int srcStream, int dstStream) {
        int srcRange = this.mStreamStates[srcStream].getMaxIndex() - this.mStreamStates[srcStream].getMinIndex();
        int dstRange = this.mStreamStates[dstStream].getMaxIndex() - this.mStreamStates[dstStream].getMinIndex();
        if (srcRange != 0) {
            return this.mStreamStates[dstStream].getMinIndex() + ((((index - this.mStreamStates[srcStream].getMinIndex()) * dstRange) + (srcRange / 2)) / srcRange);
        }
        Log.e(TAG, "rescaleIndex : index range should not be zero");
        return this.mStreamStates[dstStream].getMinIndex();
    }

    public void adjustSuggestedStreamVolume(int direction, int suggestedStreamType, int flags, String callingPackage, String caller) {
        IAudioPolicyCallback extVolCtlr;
        synchronized (this.mExtVolumeControllerLock) {
            extVolCtlr = this.mExtVolumeController;
        }
        if (extVolCtlr != null) {
            sendMsg(this.mAudioHandler, 22, 2, direction, 0, extVolCtlr, 0);
            return;
        }
        adjustSuggestedStreamVolume(direction, suggestedStreamType, flags, callingPackage, caller, Binder.getCallingUid());
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x00d3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void adjustSuggestedStreamVolume(int r18, int r19, int r20, java.lang.String r21, java.lang.String r22, int r23) {
        /*
            r17 = this;
            r8 = r17
            r9 = r19
            r7 = r20
            r10 = r22
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "adjustSuggestedStreamVolume() stream="
            r0.append(r1)
            r0.append(r9)
            java.lang.String r1 = ", flags="
            r0.append(r1)
            r0.append(r7)
            java.lang.String r1 = ", caller="
            r0.append(r1)
            r0.append(r10)
            java.lang.String r1 = ", volControlStream="
            r0.append(r1)
            int r1 = r8.mVolumeControlStream
            r0.append(r1)
            java.lang.String r1 = ", userSelect="
            r0.append(r1)
            boolean r1 = r8.mUserSelectedVolumeControlStream
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AS.AudioService"
            android.util.Log.d(r1, r0)
            if (r18 == 0) goto L_0x0074
            com.android.server.audio.AudioEventLogger r0 = sVolumeLogger
            com.android.server.audio.AudioServiceEvents$VolumeEvent r11 = new com.android.server.audio.AudioServiceEvents$VolumeEvent
            r2 = 0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r12 = r21
            r1.<init>(r12)
            java.lang.String r3 = "/"
            r1.append(r3)
            r1.append(r10)
            java.lang.String r3 = " uid:"
            r1.append(r3)
            r13 = r23
            r1.append(r13)
            java.lang.String r6 = r1.toString()
            r1 = r11
            r3 = r19
            r4 = r18
            r5 = r20
            r1.<init>(r2, r3, r4, r5, r6)
            r0.log(r11)
            goto L_0x0078
        L_0x0074:
            r12 = r21
            r13 = r23
        L_0x0078:
            java.lang.Object r1 = r8.mForceControlStreamLock
            monitor-enter(r1)
            java.lang.String r0 = "AS.AudioService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0124 }
            r2.<init>()     // Catch:{ all -> 0x0124 }
            java.lang.String r3 = "adjustSuggestedStreamVolume() stream="
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            r2.append(r9)     // Catch:{ all -> 0x0124 }
            java.lang.String r3 = ", flags="
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            r2.append(r7)     // Catch:{ all -> 0x0124 }
            java.lang.String r3 = ", caller="
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            r2.append(r10)     // Catch:{ all -> 0x0124 }
            java.lang.String r3 = ", volControlStream="
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            int r3 = r8.mVolumeControlStream     // Catch:{ all -> 0x0124 }
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            java.lang.String r3 = ", userSelect="
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            boolean r3 = r8.mUserSelectedVolumeControlStream     // Catch:{ all -> 0x0124 }
            r2.append(r3)     // Catch:{ all -> 0x0124 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0124 }
            android.util.Log.d(r0, r2)     // Catch:{ all -> 0x0124 }
            boolean r0 = r8.mUserSelectedVolumeControlStream     // Catch:{ all -> 0x0124 }
            r2 = 2
            if (r0 == 0) goto L_0x00bd
            int r0 = r8.mVolumeControlStream     // Catch:{ all -> 0x0124 }
            goto L_0x00de
        L_0x00bd:
            int r0 = r8.getActiveStreamType(r9)     // Catch:{ all -> 0x0124 }
            r3 = 0
            if (r0 == r2) goto L_0x00cd
            r4 = 5
            if (r0 != r4) goto L_0x00c8
            goto L_0x00cd
        L_0x00c8:
            boolean r3 = android.media.AudioSystem.isStreamActive(r0, r3)     // Catch:{ all -> 0x0124 }
            goto L_0x00d1
        L_0x00cd:
            boolean r3 = r8.wasStreamActiveRecently(r0, r3)     // Catch:{ all -> 0x0124 }
        L_0x00d1:
            if (r3 != 0) goto L_0x00dd
            int r4 = r8.mVolumeControlStream     // Catch:{ all -> 0x0124 }
            r5 = -1
            if (r4 != r5) goto L_0x00d9
            goto L_0x00dd
        L_0x00d9:
            int r4 = r8.mVolumeControlStream     // Catch:{ all -> 0x0124 }
            r0 = r4
            goto L_0x00de
        L_0x00dd:
            r4 = r0
        L_0x00de:
            monitor-exit(r1)     // Catch:{ all -> 0x0124 }
            boolean r11 = r17.isMuteAdjust(r18)
            r8.ensureValidStreamType(r0)
            int[] r1 = mStreamVolumeAlias
            r14 = r1[r0]
            r1 = r7 & 4
            if (r1 == 0) goto L_0x00f3
            if (r14 == r2) goto L_0x00f3
            r1 = r7 & -5
            goto L_0x00f4
        L_0x00f3:
            r1 = r7
        L_0x00f4:
            com.android.server.audio.AudioService$VolumeController r2 = r8.mVolumeController
            boolean r2 = r2.suppressAdjustment(r14, r1, r11)
            if (r2 == 0) goto L_0x0110
            boolean r2 = r8.mIsSingleVolume
            if (r2 != 0) goto L_0x0110
            r2 = 0
            r1 = r1 & -5
            r1 = r1 & -17
            java.lang.String r3 = "AS.AudioService"
            java.lang.String r4 = "Volume controller suppressed adjustment"
            android.util.Log.d(r3, r4)
            r16 = r1
            r15 = r2
            goto L_0x0114
        L_0x0110:
            r15 = r18
            r16 = r1
        L_0x0114:
            r1 = r17
            r2 = r0
            r3 = r15
            r4 = r16
            r5 = r21
            r6 = r22
            r7 = r23
            r1.adjustStreamVolume(r2, r3, r4, r5, r6, r7)
            return
        L_0x0124:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0124 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.adjustSuggestedStreamVolume(int, int, int, java.lang.String, java.lang.String, int):void");
    }

    public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) {
        if (streamType != 10 || canChangeAccessibilityVolume()) {
            String str = callingPackage;
            sVolumeLogger.log(new AudioServiceEvents.VolumeEvent(1, streamType, direction, flags, str));
            adjustStreamVolume(streamType, direction, flags, callingPackage, str, Binder.getCallingUid());
            return;
        }
        Log.w(TAG, "Trying to call adjustStreamVolume() for a11y withoutCHANGE_ACCESSIBILITY_VOLUME / callingPackage=" + callingPackage);
    }

    /* Debug info: failed to restart local var, previous not found, register: 25 */
    /* JADX INFO: finally extract failed */
    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    protected void adjustStreamVolume(int r26, int r27, int r28, java.lang.String r29, java.lang.String r30, int r31) {
        /*
            r25 = this;
            r8 = r25
            r9 = r26
            r10 = r27
            r1 = r28
            r11 = r30
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "adjustStreamVolume() stream="
            r0.append(r2)
            r0.append(r9)
            java.lang.String r2 = ", dir="
            r0.append(r2)
            r0.append(r10)
            java.lang.String r2 = ", flags="
            r0.append(r2)
            r0.append(r1)
            java.lang.String r2 = ", caller="
            r0.append(r2)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AS.AudioService"
            android.util.Log.d(r2, r0)
            java.lang.String r0 = "vendor.mls.audio.session.status"
            java.lang.String r2 = "default"
            java.lang.String r12 = android.os.SystemProperties.get(r0, r2)
            java.lang.String r0 = "started"
            boolean r0 = r12.equals(r0)
            r13 = 1
            if (r13 != r0) goto L_0x0053
            java.lang.String r0 = "AS.AudioService"
            java.lang.String r2 = "adjustStreamVolume() Ignore volume change during MirrorLink session"
            android.util.Log.e(r0, r2)
            return
        L_0x0053:
            boolean r0 = r8.mUseFixedVolume
            if (r0 == 0) goto L_0x0058
            return
        L_0x0058:
            r8.ensureValidDirection(r10)
            r25.ensureValidStreamType(r26)
            boolean r14 = r8.isMuteAdjust(r10)
            if (r14 == 0) goto L_0x006b
            boolean r0 = r25.isStreamAffectedByMute(r26)
            if (r0 != 0) goto L_0x006b
            return
        L_0x006b:
            if (r14 == 0) goto L_0x00a3
            if (r9 == 0) goto L_0x0072
            r0 = 6
            if (r9 != r0) goto L_0x00a3
        L_0x0072:
            android.content.Context r0 = r8.mContext
            java.lang.String r2 = "android.permission.MODIFY_PHONE_STATE"
            int r0 = r0.checkCallingOrSelfPermission(r2)
            if (r0 == 0) goto L_0x00a3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "MODIFY_PHONE_STATE Permission Denial: adjustStreamVolume from pid="
            r0.append(r2)
            int r2 = android.os.Binder.getCallingPid()
            r0.append(r2)
            java.lang.String r2 = ", uid="
            r0.append(r2)
            int r2 = android.os.Binder.getCallingUid()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "AS.AudioService"
            android.util.Log.w(r2, r0)
            return
        L_0x00a3:
            int[] r0 = mStreamVolumeAlias
            r15 = r0[r9]
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r8.mStreamStates
            r7 = r0[r15]
            int r6 = r8.getDeviceForStream(r15)
            int r2 = r7.getIndex(r6)
            r16 = 1
            r0 = r6 & 896(0x380, float:1.256E-42)
            if (r0 != 0) goto L_0x00be
            r0 = r1 & 64
            if (r0 == 0) goto L_0x00be
            return
        L_0x00be:
            r0 = 1000(0x3e8, float:1.401E-42)
            r3 = r31
            if (r3 != r0) goto L_0x00d2
            int r0 = r25.getCurrentUserId()
            int r4 = android.os.UserHandle.getAppId(r31)
            int r0 = android.os.UserHandle.getUid(r0, r4)
            r5 = r0
            goto L_0x00d3
        L_0x00d2:
            r5 = r3
        L_0x00d3:
            android.app.AppOpsManager r0 = r8.mAppOps
            int[] r3 = STREAM_VOLUME_OPS
            r3 = r3[r15]
            r4 = r29
            int r0 = r0.noteOp(r3, r5, r4)
            if (r0 == 0) goto L_0x00e2
            return
        L_0x00e2:
            java.lang.Object r3 = r8.mSafeMediaVolumeStateLock
            monitor-enter(r3)
            r0 = 0
            r8.mPendingVolumeCommand = r0     // Catch:{ all -> 0x03dc }
            monitor-exit(r3)     // Catch:{ all -> 0x03dc }
            r0 = r1 & -33
            r3 = 3
            if (r15 != r3) goto L_0x0115
            int r1 = r8.mFixedVolumeDevices
            r1 = r1 & r6
            if (r1 == 0) goto L_0x0115
            r0 = r0 | 32
            int r1 = r8.mSafeMediaVolumeState
            if (r1 != r3) goto L_0x0104
            r1 = 67108876(0x400000c, float:1.504635E-36)
            r1 = r1 & r6
            if (r1 == 0) goto L_0x0104
            int r1 = r8.safeMediaVolumeIndex(r6)
            goto L_0x0108
        L_0x0104:
            int r1 = r7.getMaxIndex()
        L_0x0108:
            if (r2 == 0) goto L_0x0110
            r2 = r1
            r18 = r1
            r17 = r2
            goto L_0x011f
        L_0x0110:
            r18 = r1
            r17 = r2
            goto L_0x011f
        L_0x0115:
            r1 = 10
            int r1 = r8.rescaleIndex(r1, r9, r15)
            r18 = r1
            r17 = r2
        L_0x011f:
            r1 = r0 & 2
            r2 = 0
            if (r1 != 0) goto L_0x0132
            int r1 = r25.getUiSoundsStreamType()
            if (r15 != r1) goto L_0x012b
            goto L_0x0132
        L_0x012b:
            r23 = r5
            r13 = r6
            r31 = r7
            r7 = r0
            goto L_0x016f
        L_0x0132:
            int r1 = r25.getRingerModeInternal()
            if (r1 != r13) goto L_0x013a
            r0 = r0 & -17
        L_0x013a:
            boolean r19 = r7.mIsMuted
            r20 = r1
            r1 = r25
            r2 = r17
            r3 = r27
            r4 = r18
            r23 = r5
            r5 = r19
            r13 = r6
            r6 = r29
            r31 = r7
            r7 = r0
            int r1 = r1.checkForRingerModeChange(r2, r3, r4, r5, r6, r7)
            r2 = r1 & 1
            if (r2 == 0) goto L_0x015d
            r2 = 1
            goto L_0x015e
        L_0x015d:
            r2 = 0
        L_0x015e:
            r16 = r2
            r2 = r1 & 128(0x80, float:1.794E-43)
            if (r2 == 0) goto L_0x0166
            r0 = r0 | 128(0x80, float:1.794E-43)
        L_0x0166:
            r2 = r1 & 2048(0x800, float:2.87E-42)
            if (r2 == 0) goto L_0x016e
            r0 = r0 | 2048(0x800, float:2.87E-42)
            r7 = r0
            goto L_0x016f
        L_0x016e:
            r7 = r0
        L_0x016f:
            boolean r0 = r8.volumeAdjustmentAllowedByDnd(r15, r7)
            if (r0 != 0) goto L_0x0177
            r16 = 0
        L_0x0177:
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r8.mStreamStates
            r0 = r0[r9]
            int r6 = r0.getIndex(r13)
            if (r16 == 0) goto L_0x03b5
            if (r10 == 0) goto L_0x03b5
            com.android.server.audio.AudioService$AudioHandler r0 = r8.mAudioHandler
            r1 = 18
            r0.removeMessages(r1)
            r0 = -1
            r5 = 101(0x65, float:1.42E-43)
            if (r14 == 0) goto L_0x01d6
            if (r10 != r5) goto L_0x0198
            boolean r1 = r31.mIsMuted
            r2 = 1
            r1 = r1 ^ r2
            goto L_0x019f
        L_0x0198:
            r1 = -100
            if (r10 != r1) goto L_0x019e
            r1 = 1
            goto L_0x019f
        L_0x019e:
            r1 = 0
        L_0x019f:
            r4 = 3
            if (r15 != r4) goto L_0x01a5
            r8.setSystemAudioMute(r1)
        L_0x01a5:
            r2 = 0
        L_0x01a6:
            com.android.server.audio.AudioService$VolumeStreamState[] r3 = r8.mStreamStates
            int r3 = r3.length
            if (r2 >= r3) goto L_0x01ce
            int[] r3 = mStreamVolumeAlias
            r3 = r3[r2]
            if (r15 != r3) goto L_0x01c9
            boolean r3 = r25.readCameraSoundForced()
            if (r3 == 0) goto L_0x01c2
            com.android.server.audio.AudioService$VolumeStreamState[] r3 = r8.mStreamStates
            r3 = r3[r2]
            int r3 = r3.getStreamType()
            r5 = 7
            if (r3 == r5) goto L_0x01c9
        L_0x01c2:
            com.android.server.audio.AudioService$VolumeStreamState[] r3 = r8.mStreamStates
            r3 = r3[r2]
            r3.mute(r1)
        L_0x01c9:
            int r2 = r2 + 1
            r5 = 101(0x65, float:1.42E-43)
            goto L_0x01a6
        L_0x01ce:
            r20 = r31
            r0 = r4
            r24 = r6
            r11 = r7
            goto L_0x02bb
        L_0x01d6:
            r4 = 3
            r1 = 1
            if (r10 != r1) goto L_0x0205
            int r1 = r17 + r18
            boolean r1 = r8.checkSafeMediaVolume(r15, r1, r13)
            if (r1 != 0) goto L_0x0205
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "adjustStreamVolume() safe volume index = "
            r1.append(r2)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AS.AudioService"
            android.util.Log.e(r2, r1)
            com.android.server.audio.AudioService$VolumeController r1 = r8.mVolumeController
            r1.postDisplaySafeVolumeWarning(r7)
            r20 = r31
            r0 = r4
            r24 = r6
            r11 = r7
            goto L_0x02bb
        L_0x0205:
            com.android.server.audio.AudioService$VolumeStreamState[] r1 = r8.mStreamStates
            r1 = r1[r4]
            int r1 = r1.getMaxIndex()
            android.content.Context r2 = r8.mContext
            boolean r1 = android.media.AudioServiceInjector.shouldAdjustHiFiVolume(r9, r10, r6, r1, r2)
            if (r1 == 0) goto L_0x0222
            android.content.Context r1 = r8.mContext
            android.media.AudioServiceInjector.adjustHiFiVolume(r10, r1)
            r20 = r31
            r0 = r4
            r24 = r6
            r11 = r7
            goto L_0x02bb
        L_0x0222:
            int r1 = r8.mFullVolumeDevices
            r1 = r1 & r13
            if (r1 != 0) goto L_0x02b5
            int r1 = r10 * r18
            r5 = r31
            boolean r1 = r5.adjustIndex(r1, r13, r11)
            if (r1 != 0) goto L_0x0240
            boolean r1 = r5.mIsMuted
            if (r1 == 0) goto L_0x0238
            goto L_0x0240
        L_0x0238:
            r0 = r4
            r20 = r5
            r24 = r6
            r11 = r7
            goto L_0x02bb
        L_0x0240:
            boolean r1 = r5.mIsMuted
            if (r1 == 0) goto L_0x0295
            r1 = 2
            if (r15 != r1) goto L_0x0256
            boolean r1 = android.media.AudioServiceInjector.isXOptMode()
            if (r1 != 0) goto L_0x0256
            r0 = r4
            r20 = r5
            r24 = r6
            r11 = r7
            goto L_0x029b
        L_0x0256:
            r1 = 1
            if (r10 != r1) goto L_0x0264
            r3 = 0
            r5.mute(r3)
            r0 = r4
            r20 = r5
            r24 = r6
            r11 = r7
            goto L_0x029b
        L_0x0264:
            r3 = 0
            if (r10 != r0) goto L_0x028e
            boolean r1 = r8.mIsSingleVolume
            if (r1 == 0) goto L_0x0287
            com.android.server.audio.AudioService$AudioHandler r1 = r8.mAudioHandler
            r2 = 18
            r20 = 2
            r21 = 0
            r22 = 350(0x15e, float:4.9E-43)
            r3 = r20
            r0 = r4
            r4 = r15
            r20 = r5
            r5 = r7
            r24 = r6
            r6 = r21
            r11 = r7
            r7 = r22
            sendMsg(r1, r2, r3, r4, r5, r6, r7)
            goto L_0x029b
        L_0x0287:
            r0 = r4
            r20 = r5
            r24 = r6
            r11 = r7
            goto L_0x029b
        L_0x028e:
            r0 = r4
            r20 = r5
            r24 = r6
            r11 = r7
            goto L_0x029b
        L_0x0295:
            r0 = r4
            r20 = r5
            r24 = r6
            r11 = r7
        L_0x029b:
            com.android.server.audio.AudioService$AudioHandler r1 = r8.mAudioHandler
            r2 = 0
            r3 = 2
            r5 = 0
            r7 = 0
            r4 = r13
            r6 = r20
            sendMsg(r1, r2, r3, r4, r5, r6, r7)
            android.content.Context r1 = r8.mContext
            com.android.server.audio.AudioService$VolumeStreamState[] r2 = r8.mStreamStates
            r2 = r2[r9]
            int r2 = r2.getIndex(r13)
            android.media.AudioServiceInjector.handleZenModeVolumeChanged(r1, r15, r13, r2)
            goto L_0x02bb
        L_0x02b5:
            r20 = r31
            r0 = r4
            r24 = r6
            r11 = r7
        L_0x02bb:
            com.android.server.audio.AudioService$VolumeStreamState[] r1 = r8.mStreamStates
            r1 = r1[r9]
            int r1 = r1.getIndex(r13)
            if (r15 != r0) goto L_0x02f3
            r2 = r13 & 896(0x380, float:1.256E-42)
            if (r2 == 0) goto L_0x02f3
            r2 = r11 & 64
            if (r2 != 0) goto L_0x02f3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "adjustSreamVolume: postSetAvrcpAbsoluteVolumeIndex index="
            r2.append(r3)
            r2.append(r1)
            java.lang.String r3 = "stream="
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AS.AudioService"
            android.util.Log.d(r3, r2)
            com.android.server.audio.AudioDeviceBroker r2 = r8.mDeviceBroker
            int r3 = r1 / 10
            r2.postSetAvrcpAbsoluteVolumeIndex(r3)
        L_0x02f3:
            r2 = 134217728(0x8000000, float:3.85186E-34)
            r2 = r2 & r13
            if (r2 == 0) goto L_0x0321
            int r2 = r25.getHearingAidStreamType()
            if (r9 != r2) goto L_0x0321
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "adjustSreamVolume postSetHearingAidVolumeIndex index="
            r2.append(r3)
            r2.append(r1)
            java.lang.String r3 = " stream="
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "AS.AudioService"
            android.util.Log.d(r3, r2)
            com.android.server.audio.AudioDeviceBroker r2 = r8.mDeviceBroker
            r2.postSetHearingAidVolumeIndex(r1, r9)
        L_0x0321:
            if (r15 != r0) goto L_0x032d
            int r2 = r25.getStreamMaxVolume(r26)
            r7 = r24
            r8.setSystemAudioVolume(r7, r1, r2, r11)
            goto L_0x032f
        L_0x032d:
            r7 = r24
        L_0x032f:
            java.lang.Object r2 = r8.mHdmiClientLock
            monitor-enter(r2)
            android.hardware.hdmi.HdmiControlManager r3 = r8.mHdmiManager     // Catch:{ all -> 0x03ae }
            if (r3 == 0) goto L_0x03aa
            boolean r3 = r8.mHdmiCecSink     // Catch:{ all -> 0x03ae }
            if (r3 == 0) goto L_0x0377
            if (r15 != r0) goto L_0x0377
            int r3 = r8.mFullVolumeDevices     // Catch:{ all -> 0x0373 }
            r3 = r3 & r13
            if (r3 == 0) goto L_0x0377
            r3 = 0
            r4 = -1
            if (r10 == r4) goto L_0x0353
            r4 = 1
            if (r10 == r4) goto L_0x0350
            r4 = 101(0x65, float:1.42E-43)
            if (r10 == r4) goto L_0x034d
            goto L_0x0356
        L_0x034d:
            r3 = 164(0xa4, float:2.3E-43)
            goto L_0x0356
        L_0x0350:
            r3 = 24
            goto L_0x0356
        L_0x0353:
            r3 = 25
        L_0x0356:
            if (r3 == 0) goto L_0x0371
            long r4 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0373 }
            android.hardware.hdmi.HdmiPlaybackClient r6 = r8.mHdmiPlaybackClient     // Catch:{ all -> 0x036c }
            r0 = 1
            r6.sendKeyEvent(r3, r0)     // Catch:{ all -> 0x036c }
            android.hardware.hdmi.HdmiPlaybackClient r0 = r8.mHdmiPlaybackClient     // Catch:{ all -> 0x036c }
            r6 = 0
            r0.sendKeyEvent(r3, r6)     // Catch:{ all -> 0x036c }
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x0373 }
            goto L_0x0378
        L_0x036c:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r4)     // Catch:{ all -> 0x0373 }
            throw r0     // Catch:{ all -> 0x0373 }
        L_0x0371:
            r6 = 0
            goto L_0x0378
        L_0x0373:
            r0 = move-exception
            r28 = r1
            goto L_0x03b1
        L_0x0377:
            r6 = 0
        L_0x0378:
            android.hardware.hdmi.HdmiAudioSystemClient r0 = r8.mHdmiAudioSystemClient     // Catch:{ all -> 0x03ae }
            if (r0 == 0) goto L_0x03a7
            boolean r0 = r8.mHdmiSystemAudioSupported     // Catch:{ all -> 0x03ae }
            if (r0 == 0) goto L_0x03a7
            r0 = 3
            if (r15 != r0) goto L_0x03a7
            if (r7 != r1) goto L_0x038b
            if (r14 == 0) goto L_0x0388
            goto L_0x038b
        L_0x0388:
            r28 = r1
            goto L_0x03ac
        L_0x038b:
            long r3 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x03ae }
            android.hardware.hdmi.HdmiAudioSystemClient r0 = r8.mHdmiAudioSystemClient     // Catch:{ all -> 0x03ae }
            r5 = 3
            int r6 = r8.getStreamVolume(r5)     // Catch:{ all -> 0x03ae }
            r28 = r1
            int r1 = r8.getStreamMaxVolume(r5)     // Catch:{ all -> 0x03b3 }
            boolean r5 = r8.isStreamMute(r5)     // Catch:{ all -> 0x03b3 }
            r0.sendReportAudioStatusCecCommand(r14, r6, r1, r5)     // Catch:{ all -> 0x03b3 }
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x03b3 }
            goto L_0x03ac
        L_0x03a7:
            r28 = r1
            goto L_0x03ac
        L_0x03aa:
            r28 = r1
        L_0x03ac:
            monitor-exit(r2)     // Catch:{ all -> 0x03b3 }
            goto L_0x03b9
        L_0x03ae:
            r0 = move-exception
            r28 = r1
        L_0x03b1:
            monitor-exit(r2)     // Catch:{ all -> 0x03b3 }
            throw r0
        L_0x03b3:
            r0 = move-exception
            goto L_0x03b1
        L_0x03b5:
            r20 = r31
            r11 = r7
            r7 = r6
        L_0x03b9:
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r8.mStreamStates
            r0 = r0[r9]
            int r0 = r0.getIndex(r13)
            r1 = r25
            r2 = r26
            r3 = r7
            r4 = r0
            r5 = r11
            r21 = 0
            r6 = r13
            r1.sendVolumeUpdate(r2, r3, r4, r5, r6)
            if (r16 != 0) goto L_0x03d4
            r2 = 1
            if (r10 != r2) goto L_0x03d4
            goto L_0x03d6
        L_0x03d4:
            r2 = r21
        L_0x03d6:
            r4 = r20
            r8.persistVolumeIfNeeded(r13, r4, r2)
            return
        L_0x03dc:
            r0 = move-exception
            r23 = r5
            r13 = r6
            r4 = r7
        L_0x03e1:
            monitor-exit(r3)     // Catch:{ all -> 0x03e3 }
            throw r0
        L_0x03e3:
            r0 = move-exception
            goto L_0x03e1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.adjustStreamVolume(int, int, int, java.lang.String, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    public void onUnmuteStream(int stream, int flags) {
        this.mStreamStates[stream].mute(false);
        int device = getDeviceForStream(stream);
        int index = this.mStreamStates[stream].getIndex(device);
        sendVolumeUpdate(stream, index, index, flags, device);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x002b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSystemAudioVolume(int r5, int r6, int r7, int r8) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mHdmiClientLock
            monitor-enter(r0)
            android.hardware.hdmi.HdmiControlManager r1 = r4.mHdmiManager     // Catch:{ all -> 0x002c }
            if (r1 == 0) goto L_0x002a
            android.hardware.hdmi.HdmiTvClient r1 = r4.mHdmiTvClient     // Catch:{ all -> 0x002c }
            if (r1 == 0) goto L_0x002a
            if (r5 == r6) goto L_0x002a
            r1 = r8 & 256(0x100, float:3.59E-43)
            if (r1 != 0) goto L_0x002a
            boolean r1 = r4.mHdmiSystemAudioSupported     // Catch:{ all -> 0x002c }
            if (r1 != 0) goto L_0x0016
            goto L_0x002a
        L_0x0016:
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x002c }
            android.hardware.hdmi.HdmiTvClient r3 = r4.mHdmiTvClient     // Catch:{ all -> 0x0025 }
            r3.setSystemAudioVolume(r5, r6, r7)     // Catch:{ all -> 0x0025 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x0025:
            r3 = move-exception
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x002c }
            throw r3     // Catch:{ all -> 0x002c }
        L_0x002a:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.setSystemAudioVolume(int, int, int, int):void");
    }

    class StreamVolumeCommand {
        public final int mDevice;
        public final int mFlags;
        public final int mIndex;
        public final int mStreamType;

        StreamVolumeCommand(int streamType, int index, int flags, int device) {
            this.mStreamType = streamType;
            this.mIndex = index;
            this.mFlags = flags;
            this.mDevice = device;
        }

        public String toString() {
            return "{streamType=" + this.mStreamType + ",index=" + this.mIndex + ",flags=" + this.mFlags + ",device=" + this.mDevice + '}';
        }
    }

    private int getNewRingerMode(int stream, int index, int flags) {
        if (this.mIsSingleVolume) {
            return getRingerModeExternal();
        }
        if ((flags & 2) == 0 && stream != getUiSoundsStreamType()) {
            return getRingerModeExternal();
        }
        if (index != 0) {
            return 2;
        }
        if (this.mHasVibrator) {
            return 1;
        }
        if (this.mVolumePolicy.volumeDownToEnterSilent) {
            return 0;
        }
        return 2;
    }

    private boolean isAndroidNPlus(String caller) {
        try {
            if (this.mContext.getPackageManager().getApplicationInfoAsUser(caller, 0, UserHandle.getUserId(Binder.getCallingUid())).targetSdkVersion >= 24) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    private boolean wouldToggleZenMode(int newMode) {
        if (getRingerModeExternal() == 0 && newMode != 0) {
            return true;
        }
        if (getRingerModeExternal() == 0 || newMode != 0) {
            return false;
        }
        return true;
    }

    private void onSetStreamVolume(int streamType, int index, int flags, int device, String caller) {
        int stream = mStreamVolumeAlias[streamType];
        AudioServiceInjector.setStreamVolumeIntAlt(this, streamType, index, device, this.mStreamStates[streamType].getMaxIndex(), mStreamVolumeAlias, this.mContext);
        if (!AudioServiceInjector.isOnlyAdjustVolume(flags, stream, this.mZenMode)) {
            boolean z = false;
            if ((flags & 2) != 0 || stream == getUiSoundsStreamType()) {
                setRingerMode(AudioManagerHelper.getValidatedRingerMode(this.mContext, getNewRingerMode(stream, index, flags)), "AS.AudioService.onSetStreamVolume", false);
            }
            if (index > 0) {
                this.mStreamStates[stream].mute(false);
            }
            if (streamType != 6) {
                VolumeStreamState volumeStreamState = this.mStreamStates[stream];
                if (index == 0) {
                    z = true;
                }
                volumeStreamState.mute(z);
            }
        }
    }

    private void enforceModifyAudioRoutingPermission() {
        if (this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0) {
            throw new SecurityException("Missing MODIFY_AUDIO_ROUTING permission");
        }
    }

    public void setVolumeIndexForAttributes(AudioAttributes attr, int index, int flags, String callingPackage) {
        AudioAttributes audioAttributes = attr;
        enforceModifyAudioRoutingPermission();
        Preconditions.checkNotNull(audioAttributes, "attr must not be null");
        int device = getDeviceForStream(AudioProductStrategy.getLegacyStreamTypeForStrategyWithAudioAttributes(attr));
        int volumeIndexForAttributes = AudioSystem.getVolumeIndexForAttributes(audioAttributes, device);
        AudioSystem.setVolumeIndexForAttributes(audioAttributes, index, device);
        AudioVolumeGroup avg = getAudioVolumeGroupById(getVolumeGroupIdForAttributes(attr));
        if (avg != null) {
            int[] legacyStreamTypes = avg.getLegacyStreamTypes();
            int i = 0;
            for (int length = legacyStreamTypes.length; i < length; length = length) {
                setStreamVolume(legacyStreamTypes[i], index, flags, callingPackage, callingPackage, Binder.getCallingUid());
                i++;
            }
        }
    }

    private AudioVolumeGroup getAudioVolumeGroupById(int volumeGroupId) {
        for (AudioVolumeGroup avg : AudioVolumeGroup.getAudioVolumeGroups()) {
            if (avg.getId() == volumeGroupId) {
                return avg;
            }
        }
        Log.e(TAG, ": invalid volume group id: " + volumeGroupId + " requested");
        return null;
    }

    public int getVolumeIndexForAttributes(AudioAttributes attr) {
        enforceModifyAudioRoutingPermission();
        Preconditions.checkNotNull(attr, "attr must not be null");
        return AudioSystem.getVolumeIndexForAttributes(attr, getDeviceForStream(AudioProductStrategy.getLegacyStreamTypeForStrategyWithAudioAttributes(attr)));
    }

    public int getMaxVolumeIndexForAttributes(AudioAttributes attr) {
        enforceModifyAudioRoutingPermission();
        Preconditions.checkNotNull(attr, "attr must not be null");
        return AudioSystem.getMaxVolumeIndexForAttributes(attr);
    }

    public int getMinVolumeIndexForAttributes(AudioAttributes attr) {
        enforceModifyAudioRoutingPermission();
        Preconditions.checkNotNull(attr, "attr must not be null");
        return AudioSystem.getMinVolumeIndexForAttributes(attr);
    }

    public void setStreamVolume(int streamType, int index, int flags, String callingPackage) {
        if (streamType == 10 && !canChangeAccessibilityVolume()) {
            Log.w(TAG, "Trying to call setStreamVolume() for a11y without CHANGE_ACCESSIBILITY_VOLUME  callingPackage=" + callingPackage);
        } else if (streamType == 0 && index == 0 && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            Log.w(TAG, "Trying to call setStreamVolume() for STREAM_VOICE_CALL and index 0 without MODIFY_PHONE_STATE  callingPackage=" + callingPackage);
        } else {
            String str = callingPackage;
            sVolumeLogger.log(new AudioServiceEvents.VolumeEvent(2, streamType, index, flags, str));
            setStreamVolume(streamType, index, flags, callingPackage, str, Binder.getCallingUid());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canChangeAccessibilityVolume() {
        /*
            r6 = this;
            java.lang.Object r0 = r6.mAccessibilityServiceUidsLock
            monitor-enter(r0)
            android.content.Context r1 = r6.mContext     // Catch:{ all -> 0x002c }
            java.lang.String r2 = "android.permission.CHANGE_ACCESSIBILITY_VOLUME"
            int r1 = r1.checkCallingOrSelfPermission(r2)     // Catch:{ all -> 0x002c }
            r2 = 1
            if (r1 != 0) goto L_0x0010
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return r2
        L_0x0010:
            int[] r1 = r6.mAccessibilityServiceUids     // Catch:{ all -> 0x002c }
            r3 = 0
            if (r1 == 0) goto L_0x002a
            int r1 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x002c }
            r4 = r3
        L_0x001a:
            int[] r5 = r6.mAccessibilityServiceUids     // Catch:{ all -> 0x002c }
            int r5 = r5.length     // Catch:{ all -> 0x002c }
            if (r4 >= r5) goto L_0x002a
            int[] r5 = r6.mAccessibilityServiceUids     // Catch:{ all -> 0x002c }
            r5 = r5[r4]     // Catch:{ all -> 0x002c }
            if (r5 != r1) goto L_0x0027
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return r2
        L_0x0027:
            int r4 = r4 + 1
            goto L_0x001a
        L_0x002a:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return r3
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.canChangeAccessibilityVolume():boolean");
    }

    /* access modifiers changed from: package-private */
    public int getHearingAidStreamType() {
        return getHearingAidStreamType(this.mMode);
    }

    private int getHearingAidStreamType(int mode) {
        return (mode == 2 || mode == 3 || this.mVoiceActive.get()) ? 0 : 3;
    }

    /* access modifiers changed from: private */
    public void onPlaybackConfigChange(List<AudioPlaybackConfiguration> configs) {
        boolean voiceActive = false;
        Iterator<AudioPlaybackConfiguration> it = configs.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AudioPlaybackConfiguration config = it.next();
            int usage = config.getAudioAttributes().getUsage();
            if ((usage == 2 || usage == 3) && config.getPlayerState() == 2) {
                voiceActive = true;
                break;
            }
        }
        if (this.mVoiceActive.getAndSet(voiceActive) != voiceActive) {
            updateHearingAidVolumeOnVoiceActivityUpdate();
        }
    }

    private void updateHearingAidVolumeOnVoiceActivityUpdate() {
        int streamType = getHearingAidStreamType();
        int index = getStreamVolume(streamType);
        sVolumeLogger.log(new AudioServiceEvents.VolumeEvent(6, this.mVoiceActive.get(), streamType, index));
        this.mDeviceBroker.postSetHearingAidVolumeIndex(index * 10, streamType);
    }

    /* access modifiers changed from: package-private */
    public void updateAbsVolumeMultiModeDevices(int oldMode, int newMode) {
        if (oldMode != newMode) {
            if (newMode != 0) {
                if (newMode == 1) {
                    return;
                }
                if (!(newMode == 2 || newMode == 3)) {
                    return;
                }
            }
            int streamType = getHearingAidStreamType(newMode);
            int device = AudioSystem.getDevicesForStream(streamType);
            int i = this.mAbsVolumeMultiModeCaseDevices;
            if ((device & i) != 0 && (i & device) == 134217728) {
                int index = getStreamVolume(streamType);
                sVolumeLogger.log(new AudioServiceEvents.VolumeEvent(7, newMode, streamType, index));
                this.mDeviceBroker.postSetHearingAidVolumeIndex(index * 10, streamType);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01e9, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01ea, code lost:
        r17 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:?, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x01ed, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x01ee, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x01a6, code lost:
        r1 = r7.mHdmiClientLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01a8, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01ab, code lost:
        if (r7.mHdmiManager == null) goto L_0x01d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01af, code lost:
        if (r7.mHdmiAudioSystemClient == null) goto L_0x01d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01b3, code lost:
        if (r7.mHdmiSystemAudioSupported == false) goto L_0x01d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01b6, code lost:
        if (r11 != 3) goto L_0x01d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01b8, code lost:
        if (r9 == r6) goto L_0x01d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01ba, code lost:
        r2 = android.os.Binder.clearCallingIdentity();
        r7.mHdmiAudioSystemClient.sendReportAudioStatusCecCommand(false, getStreamVolume(3), getStreamMaxVolume(3), isStreamMute(3));
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01d5, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x01d6, code lost:
        r17 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:?, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01da, code lost:
        r17 = r6;
        sendVolumeUpdate(r19, r9, r6, r16, r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01e8, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x016d A[Catch:{ all -> 0x01fe }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x018b A[Catch:{ all -> 0x01f8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setStreamVolume(int r19, int r20, int r21, java.lang.String r22, java.lang.String r23, int r24) {
        /*
            r18 = this;
            r7 = r18
            r8 = r19
            r1 = r20
            r2 = r21
            r9 = r22
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "setStreamVolume(stream="
            r0.append(r3)
            r0.append(r8)
            java.lang.String r3 = ", index="
            r0.append(r3)
            r0.append(r1)
            java.lang.String r3 = ", calling="
            r0.append(r3)
            r0.append(r9)
            java.lang.String r3 = ")"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "AS.AudioService"
            android.util.Log.d(r3, r0)
            java.lang.String r0 = "vendor.mls.audio.session.status"
            java.lang.String r3 = "default"
            java.lang.String r10 = android.os.SystemProperties.get(r0, r3)
            java.lang.String r0 = "started"
            boolean r0 = r10.equals(r0)
            r3 = 1
            if (r3 != r0) goto L_0x0052
            java.lang.String r0 = "AS.AudioService"
            java.lang.String r3 = "setStreamVolume() Ignore volume change during MirrorLink session"
            android.util.Log.e(r0, r3)
            return
        L_0x0052:
            boolean r0 = r7.mUseFixedVolume
            if (r0 == 0) goto L_0x0057
            return
        L_0x0057:
            r18.ensureValidStreamType(r19)
            int[] r0 = mStreamVolumeAlias
            r11 = r0[r8]
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r7.mStreamStates
            r12 = r0[r11]
            int r13 = r18.getDeviceForStream(r19)
            r0 = r13 & 896(0x380, float:1.256E-42)
            if (r0 != 0) goto L_0x006f
            r0 = r2 & 64
            if (r0 == 0) goto L_0x006f
            return
        L_0x006f:
            r0 = 1000(0x3e8, float:1.401E-42)
            r3 = r24
            if (r3 != r0) goto L_0x0083
            int r0 = r18.getCurrentUserId()
            int r4 = android.os.UserHandle.getAppId(r24)
            int r0 = android.os.UserHandle.getUid(r0, r4)
            r14 = r0
            goto L_0x0084
        L_0x0083:
            r14 = r3
        L_0x0084:
            android.app.AppOpsManager r0 = r7.mAppOps
            int[] r3 = STREAM_VOLUME_OPS
            r3 = r3[r11]
            int r0 = r0.noteOp(r3, r14, r9)
            if (r0 == 0) goto L_0x0091
            return
        L_0x0091:
            boolean r0 = r7.isAndroidNPlus(r9)
            if (r0 == 0) goto L_0x00b2
            int r0 = r7.getNewRingerMode(r11, r1, r2)
            boolean r0 = r7.wouldToggleZenMode(r0)
            if (r0 == 0) goto L_0x00b2
            android.app.NotificationManager r0 = r7.mNm
            boolean r0 = r0.isNotificationPolicyAccessGrantedForPackage(r9)
            if (r0 == 0) goto L_0x00aa
            goto L_0x00b2
        L_0x00aa:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r3 = "Not allowed to change Do Not Disturb state"
            r0.<init>(r3)
            throw r0
        L_0x00b2:
            boolean r0 = r7.volumeAdjustmentAllowedByDnd(r11, r2)
            if (r0 != 0) goto L_0x00b9
            return
        L_0x00b9:
            java.lang.Object r15 = r7.mSafeMediaVolumeStateLock
            monitor-enter(r15)
            r0 = 0
            r7.mPendingVolumeCommand = r0     // Catch:{ all -> 0x0208 }
            int r0 = r12.getIndex(r13)     // Catch:{ all -> 0x0208 }
            r6 = r0
            android.media.AudioServiceInjector.mOriginalIndexWhenSetStreamVolume = r1     // Catch:{ all -> 0x0208 }
            int r0 = r1 * 10
            int r0 = r7.rescaleIndex(r0, r8, r11)     // Catch:{ all -> 0x0208 }
            r1 = r0
            r0 = 3
            if (r11 != r0) goto L_0x00ff
            r3 = r13 & 896(0x380, float:1.256E-42)
            if (r3 == 0) goto L_0x00ff
            r3 = r2 & 64
            if (r3 != 0) goto L_0x00ff
            java.lang.String r3 = "AS.AudioService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x012f }
            r4.<init>()     // Catch:{ all -> 0x012f }
            java.lang.String r5 = "setStreamVolume postSetAvrcpAbsoluteVolumeIndex index="
            r4.append(r5)     // Catch:{ all -> 0x012f }
            r4.append(r1)     // Catch:{ all -> 0x012f }
            java.lang.String r5 = "stream="
            r4.append(r5)     // Catch:{ all -> 0x012f }
            r4.append(r8)     // Catch:{ all -> 0x012f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x012f }
            android.util.Log.d(r3, r4)     // Catch:{ all -> 0x012f }
            com.android.server.audio.AudioDeviceBroker r3 = r7.mDeviceBroker     // Catch:{ all -> 0x012f }
            int r4 = r1 / 10
            r3.postSetAvrcpAbsoluteVolumeIndex(r4)     // Catch:{ all -> 0x012f }
        L_0x00ff:
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r3 = r3 & r13
            if (r3 == 0) goto L_0x0132
            int r3 = r18.getHearingAidStreamType()     // Catch:{ all -> 0x012f }
            if (r8 != r3) goto L_0x0132
            java.lang.String r3 = "AS.AudioService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x012f }
            r4.<init>()     // Catch:{ all -> 0x012f }
            java.lang.String r5 = "setStreamVolume postSetHearingAidVolumeIndex index="
            r4.append(r5)     // Catch:{ all -> 0x012f }
            r4.append(r1)     // Catch:{ all -> 0x012f }
            java.lang.String r5 = " stream="
            r4.append(r5)     // Catch:{ all -> 0x012f }
            r4.append(r8)     // Catch:{ all -> 0x012f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x012f }
            android.util.Log.i(r3, r4)     // Catch:{ all -> 0x012f }
            com.android.server.audio.AudioDeviceBroker r3 = r7.mDeviceBroker     // Catch:{ all -> 0x012f }
            r3.postSetHearingAidVolumeIndex(r1, r8)     // Catch:{ all -> 0x012f }
            goto L_0x0132
        L_0x012f:
            r0 = move-exception
            goto L_0x0209
        L_0x0132:
            if (r11 != r0) goto L_0x013b
            int r3 = r18.getStreamMaxVolume(r19)     // Catch:{ all -> 0x012f }
            r7.setSystemAudioVolume(r6, r1, r3, r2)     // Catch:{ all -> 0x012f }
        L_0x013b:
            r2 = r2 & -33
            if (r11 != r0) goto L_0x0165
            int r3 = r7.mFixedVolumeDevices     // Catch:{ all -> 0x020b }
            r3 = r3 & r13
            if (r3 == 0) goto L_0x0165
            r2 = r2 | 32
            if (r1 == 0) goto L_0x0162
            int r3 = r7.mSafeMediaVolumeState     // Catch:{ all -> 0x020b }
            if (r3 != r0) goto L_0x015a
            r3 = 67108876(0x400000c, float:1.504635E-36)
            r3 = r3 & r13
            if (r3 == 0) goto L_0x015a
            int r3 = r7.safeMediaVolumeIndex(r13)     // Catch:{ all -> 0x020b }
            r1 = r3
            r5 = r1
            r4 = r2
            goto L_0x0167
        L_0x015a:
            int r3 = r12.getMaxIndex()     // Catch:{ all -> 0x020b }
            r1 = r3
            r5 = r1
            r4 = r2
            goto L_0x0167
        L_0x0162:
            r5 = r1
            r4 = r2
            goto L_0x0167
        L_0x0165:
            r5 = r1
            r4 = r2
        L_0x0167:
            boolean r1 = r7.checkSafeMediaVolume(r11, r5, r13)     // Catch:{ all -> 0x01fe }
            if (r1 != 0) goto L_0x018b
            com.android.server.audio.AudioService$VolumeController r1 = r7.mVolumeController     // Catch:{ all -> 0x01fe }
            r1.postDisplaySafeVolumeWarning(r4)     // Catch:{ all -> 0x01fe }
            com.android.server.audio.AudioService$StreamVolumeCommand r3 = new com.android.server.audio.AudioService$StreamVolumeCommand     // Catch:{ all -> 0x01fe }
            r1 = r3
            r2 = r18
            r0 = r3
            r3 = r19
            r16 = r4
            r4 = r5
            r17 = r5
            r5 = r16
            r9 = r6
            r6 = r13
            r1.<init>(r3, r4, r5, r6)     // Catch:{ all -> 0x01f8 }
            r7.mPendingVolumeCommand = r0     // Catch:{ all -> 0x01f8 }
            r6 = r17
            goto L_0x01a5
        L_0x018b:
            r16 = r4
            r17 = r5
            r9 = r6
            r1 = r18
            r2 = r19
            r3 = r17
            r5 = r13
            r6 = r23
            r1.onSetStreamVolume(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x01f8 }
            com.android.server.audio.AudioService$VolumeStreamState[] r0 = r7.mStreamStates     // Catch:{ all -> 0x01f8 }
            r0 = r0[r8]     // Catch:{ all -> 0x01f8 }
            int r0 = r0.getIndex(r13)     // Catch:{ all -> 0x01f8 }
            r6 = r0
        L_0x01a5:
            monitor-exit(r15)     // Catch:{ all -> 0x01f0 }
            java.lang.Object r1 = r7.mHdmiClientLock
            monitor-enter(r1)
            android.hardware.hdmi.HdmiControlManager r0 = r7.mHdmiManager     // Catch:{ all -> 0x01e9 }
            if (r0 == 0) goto L_0x01d9
            android.hardware.hdmi.HdmiAudioSystemClient r0 = r7.mHdmiAudioSystemClient     // Catch:{ all -> 0x01d5 }
            if (r0 == 0) goto L_0x01d9
            boolean r0 = r7.mHdmiSystemAudioSupported     // Catch:{ all -> 0x01d5 }
            if (r0 == 0) goto L_0x01d9
            r0 = 3
            if (r11 != r0) goto L_0x01d9
            if (r9 == r6) goto L_0x01d9
            long r2 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x01d5 }
            android.hardware.hdmi.HdmiAudioSystemClient r0 = r7.mHdmiAudioSystemClient     // Catch:{ all -> 0x01d5 }
            r5 = 3
            int r15 = r7.getStreamVolume(r5)     // Catch:{ all -> 0x01d5 }
            int r4 = r7.getStreamMaxVolume(r5)     // Catch:{ all -> 0x01d5 }
            boolean r5 = r7.isStreamMute(r5)     // Catch:{ all -> 0x01d5 }
            r7 = 0
            r0.sendReportAudioStatusCecCommand(r7, r15, r4, r5)     // Catch:{ all -> 0x01d5 }
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x01d5 }
            goto L_0x01d9
        L_0x01d5:
            r0 = move-exception
            r17 = r6
            goto L_0x01ec
        L_0x01d9:
            monitor-exit(r1)     // Catch:{ all -> 0x01e9 }
            r1 = r18
            r2 = r19
            r3 = r9
            r4 = r6
            r5 = r16
            r17 = r6
            r6 = r13
            r1.sendVolumeUpdate(r2, r3, r4, r5, r6)
            return
        L_0x01e9:
            r0 = move-exception
            r17 = r6
        L_0x01ec:
            monitor-exit(r1)     // Catch:{ all -> 0x01ee }
            throw r0
        L_0x01ee:
            r0 = move-exception
            goto L_0x01ec
        L_0x01f0:
            r0 = move-exception
            r17 = r6
            r2 = r16
            r1 = r17
            goto L_0x0209
        L_0x01f8:
            r0 = move-exception
            r2 = r16
            r1 = r17
            goto L_0x0209
        L_0x01fe:
            r0 = move-exception
            r16 = r4
            r17 = r5
            r2 = r16
            r1 = r17
            goto L_0x0209
        L_0x0208:
            r0 = move-exception
        L_0x0209:
            monitor-exit(r15)     // Catch:{ all -> 0x020b }
            throw r0
        L_0x020b:
            r0 = move-exception
            goto L_0x0209
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.setStreamVolume(int, int, int, java.lang.String, java.lang.String, int):void");
    }

    private int getVolumeGroupIdForAttributes(AudioAttributes attributes) {
        Preconditions.checkNotNull(attributes, "attributes must not be null");
        int volumeGroupId = getVolumeGroupIdForAttributesInt(attributes);
        if (volumeGroupId != -1) {
            return volumeGroupId;
        }
        return getVolumeGroupIdForAttributesInt(AudioProductStrategy.sDefaultAttributes);
    }

    private int getVolumeGroupIdForAttributesInt(AudioAttributes attributes) {
        Preconditions.checkNotNull(attributes, "attributes must not be null");
        for (AudioProductStrategy productStrategy : AudioProductStrategy.getAudioProductStrategies()) {
            int volumeGroupId = productStrategy.getVolumeGroupIdForAudioAttributes(attributes);
            if (volumeGroupId != -1) {
                return volumeGroupId;
            }
        }
        return -1;
    }

    private boolean volumeAdjustmentAllowedByDnd(int streamTypeAlias, int flags) {
        int zenMode = this.mNm.getZenMode();
        if (zenMode == 0) {
            return true;
        }
        if ((zenMode == 1 || zenMode == 2 || zenMode == 3) && isStreamMutedByRingerOrZenMode(streamTypeAlias) && streamTypeAlias != getUiSoundsStreamType() && (flags & 2) == 0) {
            return false;
        }
        return true;
    }

    public void forceVolumeControlStream(int streamType, IBinder cb) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") == 0) {
            Log.d(TAG, String.format("forceVolumeControlStream(%d)", new Object[]{Integer.valueOf(streamType)}));
            synchronized (this.mForceControlStreamLock) {
                if (!(this.mVolumeControlStream == -1 || streamType == -1)) {
                    this.mUserSelectedVolumeControlStream = true;
                }
                this.mVolumeControlStream = streamType;
                if (this.mVolumeControlStream == -1) {
                    if (this.mForceControlStreamClient != null) {
                        this.mForceControlStreamClient.release();
                        this.mForceControlStreamClient = null;
                    }
                    this.mUserSelectedVolumeControlStream = false;
                } else if (this.mForceControlStreamClient == null) {
                    this.mForceControlStreamClient = new ForceControlStreamClient(cb);
                } else if (this.mForceControlStreamClient.getBinder() == cb) {
                    Log.d(TAG, "forceVolumeControlStream cb:" + cb + " is already linked.");
                } else {
                    this.mForceControlStreamClient.release();
                    this.mForceControlStreamClient = new ForceControlStreamClient(cb);
                }
            }
        }
    }

    private class ForceControlStreamClient implements IBinder.DeathRecipient {
        private IBinder mCb;

        ForceControlStreamClient(IBinder cb) {
            if (cb != null) {
                try {
                    cb.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Log.w(AudioService.TAG, "ForceControlStreamClient() could not link to " + cb + " binder death");
                    cb = null;
                }
            }
            this.mCb = cb;
        }

        public void binderDied() {
            synchronized (AudioService.this.mForceControlStreamLock) {
                Log.w(AudioService.TAG, "SCO client died");
                if (AudioService.this.mForceControlStreamClient != this) {
                    Log.w(AudioService.TAG, "unregistered control stream client died");
                } else {
                    ForceControlStreamClient unused = AudioService.this.mForceControlStreamClient = null;
                    int unused2 = AudioService.this.mVolumeControlStream = -1;
                    boolean unused3 = AudioService.this.mUserSelectedVolumeControlStream = false;
                }
            }
        }

        public void release() {
            IBinder iBinder = this.mCb;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mCb = null;
            }
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    /* access modifiers changed from: private */
    public void sendBroadcastToAll(Intent intent) {
        intent.addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
        intent.addFlags(268435456);
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private void sendStickyBroadcastToAll(Intent intent) {
        intent.addFlags(268435456);
        long ident = Binder.clearCallingIdentity();
        try {
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* JADX INFO: finally extract failed */
    private int getCurrentUserId() {
        long ident = Binder.clearCallingIdentity();
        try {
            int i = ActivityManager.getService().getCurrentUser().id;
            Binder.restoreCallingIdentity(ident);
            return i;
        } catch (RemoteException e) {
            Binder.restoreCallingIdentity(ident);
            return 0;
        } catch (Throwable currentUser) {
            Binder.restoreCallingIdentity(ident);
            throw currentUser;
        }
    }

    /* access modifiers changed from: protected */
    public void sendVolumeUpdate(int streamType, int oldIndex, int index, int flags, int device) {
        int streamType2 = mStreamVolumeAlias[streamType];
        if (streamType2 == 3) {
            flags = updateFlagsForTvPlatform(flags);
            if ((this.mFullVolumeDevices & device) != 0) {
                flags &= -2;
            }
        }
        this.mVolumeController.postVolumeChanged(streamType2, flags);
    }

    private int updateFlagsForTvPlatform(int flags) {
        synchronized (this.mHdmiClientLock) {
            if (this.mHdmiTvClient != null && this.mHdmiSystemAudioSupported && (flags & 256) == 0) {
                flags &= -2;
            }
        }
        return flags;
    }

    private void sendMasterMuteUpdate(boolean muted, int flags) {
        this.mVolumeController.postMasterMuteChanged(updateFlagsForTvPlatform(flags));
        broadcastMasterMuteStatus(muted);
    }

    private void broadcastMasterMuteStatus(boolean muted) {
        Intent intent = new Intent("android.media.MASTER_MUTE_CHANGED_ACTION");
        intent.putExtra("android.media.EXTRA_MASTER_VOLUME_MUTED", muted);
        intent.addFlags(603979776);
        sendStickyBroadcastToAll(intent);
    }

    private void setStreamVolumeInt(int streamType, int index, int device, boolean force, String caller) {
        if ((this.mFullVolumeDevices & device) == 0) {
            VolumeStreamState streamState = this.mStreamStates[streamType];
            if (streamState.setIndex(index, device, caller) || force) {
                sendMsg(this.mAudioHandler, 0, 2, device, 0, streamState, 0);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0025, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSystemAudioMute(boolean r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mHdmiClientLock
            monitor-enter(r0)
            android.hardware.hdmi.HdmiControlManager r1 = r4.mHdmiManager     // Catch:{ all -> 0x0026 }
            if (r1 == 0) goto L_0x0024
            android.hardware.hdmi.HdmiTvClient r1 = r4.mHdmiTvClient     // Catch:{ all -> 0x0026 }
            if (r1 == 0) goto L_0x0024
            boolean r1 = r4.mHdmiSystemAudioSupported     // Catch:{ all -> 0x0026 }
            if (r1 != 0) goto L_0x0010
            goto L_0x0024
        L_0x0010:
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0026 }
            android.hardware.hdmi.HdmiTvClient r3 = r4.mHdmiTvClient     // Catch:{ all -> 0x001f }
            r3.setSystemAudioMute(r5)     // Catch:{ all -> 0x001f }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0026 }
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return
        L_0x001f:
            r3 = move-exception
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0026 }
            throw r3     // Catch:{ all -> 0x0026 }
        L_0x0024:
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            return
        L_0x0026:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0026 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.setSystemAudioMute(boolean):void");
    }

    public boolean isStreamMute(int streamType) {
        boolean access$1000;
        if (streamType == Integer.MIN_VALUE) {
            streamType = getActiveStreamType(streamType);
        }
        synchronized (VolumeStreamState.class) {
            ensureValidStreamType(streamType);
            access$1000 = this.mStreamStates[streamType].mIsMuted;
        }
        return access$1000;
    }

    private class RmtSbmxFullVolDeathHandler implements IBinder.DeathRecipient {
        private IBinder mICallback;

        RmtSbmxFullVolDeathHandler(IBinder cb) {
            this.mICallback = cb;
            try {
                cb.linkToDeath(this, 0);
            } catch (RemoteException e) {
                Log.e(AudioService.TAG, "can't link to death", e);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isHandlerFor(IBinder cb) {
            return this.mICallback.equals(cb);
        }

        /* access modifiers changed from: package-private */
        public void forget() {
            try {
                this.mICallback.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
                Log.e(AudioService.TAG, "error unlinking to death", e);
            }
        }

        public void binderDied() {
            Log.w(AudioService.TAG, "Recorder with remote submix at full volume died " + this.mICallback);
            AudioService.this.forceRemoteSubmixFullVolume(false, this.mICallback);
        }
    }

    private boolean discardRmtSbmxFullVolDeathHandlerFor(IBinder cb) {
        Iterator<RmtSbmxFullVolDeathHandler> it = this.mRmtSbmxFullVolDeathHandlers.iterator();
        while (it.hasNext()) {
            RmtSbmxFullVolDeathHandler handler = it.next();
            if (handler.isHandlerFor(cb)) {
                handler.forget();
                this.mRmtSbmxFullVolDeathHandlers.remove(handler);
                return true;
            }
        }
        return false;
    }

    private boolean hasRmtSbmxFullVolDeathHandlerFor(IBinder cb) {
        Iterator<RmtSbmxFullVolDeathHandler> it = this.mRmtSbmxFullVolDeathHandlers.iterator();
        while (it.hasNext()) {
            if (it.next().isHandlerFor(cb)) {
                return true;
            }
        }
        return false;
    }

    public void forceRemoteSubmixFullVolume(boolean startForcing, IBinder cb) {
        if (cb != null) {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.CAPTURE_AUDIO_OUTPUT") != 0) {
                Log.w(TAG, "Trying to call forceRemoteSubmixFullVolume() without CAPTURE_AUDIO_OUTPUT");
                return;
            }
            synchronized (this.mRmtSbmxFullVolDeathHandlers) {
                boolean applyRequired = false;
                if (startForcing) {
                    if (!hasRmtSbmxFullVolDeathHandlerFor(cb)) {
                        this.mRmtSbmxFullVolDeathHandlers.add(new RmtSbmxFullVolDeathHandler(cb));
                        if (this.mRmtSbmxFullVolRefCount == 0) {
                            this.mFullVolumeDevices |= 32768;
                            this.mFixedVolumeDevices |= 32768;
                            applyRequired = true;
                        }
                        this.mRmtSbmxFullVolRefCount++;
                    }
                } else if (discardRmtSbmxFullVolDeathHandlerFor(cb) && this.mRmtSbmxFullVolRefCount > 0) {
                    this.mRmtSbmxFullVolRefCount--;
                    if (this.mRmtSbmxFullVolRefCount == 0) {
                        this.mFullVolumeDevices &= -32769;
                        this.mFixedVolumeDevices &= -32769;
                        applyRequired = true;
                    }
                }
                if (applyRequired) {
                    checkAllFixedVolumeDevices(3);
                    this.mStreamStates[3].applyAllVolumes();
                }
            }
        }
    }

    private void setMasterMuteInternal(boolean mute, int flags, String callingPackage, int uid, int userId) {
        if (uid == 1000) {
            uid = UserHandle.getUid(userId, UserHandle.getAppId(uid));
        }
        if (!mute && this.mAppOps.noteOp(33, uid, callingPackage) != 0) {
            return;
        }
        if (userId == UserHandle.getCallingUserId() || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            setMasterMuteInternalNoCallerCheck(mute, flags, userId);
        }
    }

    /* access modifiers changed from: private */
    public void setMasterMuteInternalNoCallerCheck(boolean mute, int flags, int userId) {
        Log.d(TAG, String.format("Master mute %s, %d, user=%d", new Object[]{Boolean.valueOf(mute), Integer.valueOf(flags), Integer.valueOf(userId)}));
        if (!isPlatformAutomotive() && this.mUseFixedVolume) {
            return;
        }
        if (((isPlatformAutomotive() && userId == 0) || getCurrentUserId() == userId) && mute != AudioSystem.getMasterMute()) {
            setSystemAudioMute(mute);
            AudioSystem.setMasterMute(mute);
            sendMasterMuteUpdate(mute, flags);
            Intent intent = new Intent("android.media.MASTER_MUTE_CHANGED_ACTION");
            intent.putExtra("android.media.EXTRA_MASTER_VOLUME_MUTED", mute);
            sendBroadcastToAll(intent);
        }
    }

    public boolean isMasterMute() {
        return AudioSystem.getMasterMute();
    }

    public void setMasterMute(boolean mute, int flags, String callingPackage, int userId) {
        setMasterMuteInternal(mute, flags, callingPackage, Binder.getCallingUid(), userId);
    }

    public int getStreamVolume(int streamType) {
        int calculateStreamVolume;
        ensureValidStreamType(streamType);
        int device = getDeviceForStream(streamType);
        synchronized (VolumeStreamState.class) {
            int index = this.mStreamStates[streamType].getIndex(device);
            if (this.mStreamStates[streamType].mIsMuted) {
                index = 0;
            }
            if (!(index == 0 || mStreamVolumeAlias[streamType] != 3 || (this.mFixedVolumeDevices & device) == 0)) {
                index = this.mStreamStates[streamType].getMaxIndex();
            }
            calculateStreamVolume = AudioServiceInjector.calculateStreamVolume(streamType, index, this.mContext);
        }
        return calculateStreamVolume;
    }

    public int getStreamMaxVolume(int streamType) {
        ensureValidStreamType(streamType);
        return AudioServiceInjector.calculateStreamMaxVolume(streamType, this.mStreamStates[streamType].getMaxIndex(), this.mContext);
    }

    public int getStreamMinVolume(int streamType) {
        ensureValidStreamType(streamType);
        return (this.mStreamStates[streamType].getMinIndex() + 5) / 10;
    }

    public int getLastAudibleStreamVolume(int streamType) {
        ensureValidStreamType(streamType);
        return (this.mStreamStates[streamType].getIndex(getDeviceForStream(streamType)) + 5) / 10;
    }

    public int getUiSoundsStreamType() {
        return mStreamVolumeAlias[1];
    }

    public void setMicrophoneMute(boolean on, String callingPackage, int userId) {
        int uid = Binder.getCallingUid();
        if (uid == 1000) {
            uid = UserHandle.getUid(userId, UserHandle.getAppId(uid));
        }
        if ((!on && this.mAppOps.noteOp(44, uid, callingPackage) != 0) || !checkAudioSettingsPermission("setMicrophoneMute()")) {
            return;
        }
        if (userId == UserHandle.getCallingUserId() || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            setMicrophoneMuteNoCallerCheck(on, userId);
        }
    }

    /* access modifiers changed from: private */
    public void setMicrophoneMuteNoCallerCheck(boolean on, int userId) {
        Log.d(TAG, String.format("Mic mute %s, user=%d", new Object[]{Boolean.valueOf(on), Integer.valueOf(userId)}));
        if (getCurrentUserId() == userId) {
            boolean currentMute = AudioSystem.isMicrophoneMuted();
            long identity = Binder.clearCallingIdentity();
            AudioSystem.muteMicrophone(on);
            Binder.restoreCallingIdentity(identity);
            if (on != currentMute) {
                this.mContext.sendBroadcast(new Intent("android.media.action.MICROPHONE_MUTE_CHANGED").setFlags(1073741824));
            }
        }
    }

    public int getRingerModeExternal() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerModeExternal;
        }
        return i;
    }

    public int getRingerModeInternal() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerMode;
        }
        return i;
    }

    private void ensureValidRingerMode(int ringerMode) {
        if (!isValidRingerMode(ringerMode)) {
            throw new IllegalArgumentException("Bad ringer mode " + ringerMode);
        }
    }

    public boolean isValidRingerMode(int ringerMode) {
        return ringerMode >= 0 && ringerMode <= 2;
    }

    public void setRingerModeExternal(int ringerMode, String caller) {
        if (!isAndroidNPlus(caller) || !wouldToggleZenMode(ringerMode) || this.mNm.isNotificationPolicyAccessGrantedForPackage(caller)) {
            setRingerMode(ringerMode, caller, true);
            return;
        }
        throw new SecurityException("Not allowed to change Do Not Disturb state");
    }

    public void setRingerModeInternal(int ringerMode, String caller) {
        enforceVolumeController("setRingerModeInternal");
        setRingerMode(ringerMode, caller, false);
    }

    public void silenceRingerModeInternal(String reason) {
        VibrationEffect effect = null;
        int ringerMode = 0;
        int toastText = 0;
        int silenceRingerSetting = 0;
        if (this.mContext.getResources().getBoolean(17891578)) {
            silenceRingerSetting = Settings.Secure.getIntForUser(this.mContentResolver, "volume_hush_gesture", 0, -2);
        }
        if (silenceRingerSetting == 1) {
            effect = VibrationEffect.get(5);
            ringerMode = 1;
            toastText = 17041316;
        } else if (silenceRingerSetting == 2) {
            effect = VibrationEffect.get(1);
            ringerMode = 0;
            toastText = 17041315;
        }
        maybeVibrate(effect, reason);
        setRingerModeInternal(ringerMode, reason);
        Toast.makeText(this.mContext, toastText, 0).show();
    }

    private boolean maybeVibrate(VibrationEffect effect, String reason) {
        if (!this.mHasVibrator) {
            return false;
        }
        if ((Settings.System.getIntForUser(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0, -2) == 0) || effect == null) {
            return false;
        }
        this.mVibrator.vibrate(Binder.getCallingUid(), this.mContext.getOpPackageName(), effect, reason, VIBRATION_ATTRIBUTES);
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    private void setRingerMode(int ringerMode, String caller, boolean external) {
        int ringerMode2;
        String str = caller;
        if (this.mUseFixedVolume) {
            int i = ringerMode;
        } else if (this.mIsSingleVolume) {
            int i2 = ringerMode;
        } else if (str == null || caller.length() == 0) {
            int i3 = ringerMode;
            throw new IllegalArgumentException("Bad caller: " + str);
        } else {
            ensureValidRingerMode(ringerMode);
            int ringerMode3 = ringerMode;
            if (ringerMode3 != 1 || this.mHasVibrator) {
                ringerMode2 = ringerMode3;
            } else {
                ringerMode2 = 0;
            }
            long identity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mSettingsLock) {
                    int ringerModeInternal = getRingerModeInternal();
                    int ringerModeExternal = getRingerModeExternal();
                    if (external) {
                        setRingerModeExt(ringerMode2);
                        if (this.mRingerModeDelegate != null) {
                            ringerMode2 = this.mRingerModeDelegate.onSetRingerModeExternal(ringerModeExternal, ringerMode2, caller, ringerModeInternal, this.mVolumePolicy);
                        }
                        if (ringerMode2 != ringerModeInternal) {
                            setRingerModeInt(ringerMode2, true);
                        }
                    } else {
                        if (ringerMode2 != ringerModeInternal) {
                            setRingerModeInt(ringerMode2, true);
                        }
                        if (this.mRingerModeDelegate != null) {
                            ringerMode2 = this.mRingerModeDelegate.onSetRingerModeInternal(ringerModeInternal, ringerMode2, caller, ringerModeExternal, this.mVolumePolicy);
                        }
                        setRingerModeExt(ringerMode2);
                    }
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }
    }

    private void setRingerModeExt(int ringerMode) {
        synchronized (this.mSettingsLock) {
            if (ringerMode != this.mRingerModeExternal) {
                this.mRingerModeExternal = ringerMode;
                muteRingerModeStreams();
                broadcastRingerMode("android.media.RINGER_MODE_CHANGED", ringerMode);
            }
        }
    }

    @GuardedBy({"mSettingsLock"})
    private void muteRingerModeStreams() {
        int numStreamTypes;
        boolean numStreamTypes2;
        int numStreamTypes3;
        int numStreamTypes4 = AudioSystem.getNumStreamTypes();
        if (this.mNm == null) {
            this.mNm = (NotificationManager) this.mContext.getSystemService("notification");
        }
        int ringerMode = this.mRingerMode;
        boolean z = false;
        boolean z2 = true;
        boolean ringerModeMute = ringerMode == 1 || ringerMode == 0 || this.mRingerModeExternal == 0;
        boolean shouldRingSco = ringerMode == 1 && isBluetoothScoOn();
        sendMsg(this.mAudioHandler, 8, 2, 7, shouldRingSco ? 3 : 0, "muteRingerModeStreams() from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid(), 0);
        int streamType = numStreamTypes4 - 1;
        while (streamType >= 0) {
            boolean isMuted = isStreamMutedByRingerOrZenMode(streamType);
            boolean shouldMute = (shouldZenMuteStream(streamType) || (ringerModeMute && isStreamAffectedByRingerMode(streamType) && ((!shouldRingSco || streamType != 2) ? z2 : z))) ? z2 : z;
            if (isMuted == shouldMute) {
                numStreamTypes = numStreamTypes4;
                numStreamTypes2 = z;
            } else if (!shouldMute) {
                if (mStreamVolumeAlias[streamType] != 2) {
                    numStreamTypes = numStreamTypes4;
                } else if (AudioServiceInjector.isXOptMode()) {
                    synchronized (VolumeStreamState.class) {
                        try {
                            VolumeStreamState vss = this.mStreamStates[streamType];
                            int i = z;
                            while (i < vss.mIndexMap.size()) {
                                int device = vss.mIndexMap.keyAt(i);
                                int value = vss.mIndexMap.valueAt(i);
                                if (value == 0) {
                                    int i2 = value;
                                    numStreamTypes3 = numStreamTypes4;
                                    vss.setIndex(10, device, TAG);
                                } else {
                                    numStreamTypes3 = numStreamTypes4;
                                }
                                i++;
                                numStreamTypes4 = numStreamTypes3;
                            }
                            numStreamTypes = numStreamTypes4;
                            sendMsg(this.mAudioHandler, 1, 2, getDeviceForStream(streamType), 0, this.mStreamStates[streamType], 500);
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                } else {
                    numStreamTypes = numStreamTypes4;
                }
                numStreamTypes2 = false;
                this.mStreamStates[streamType].mute(false);
                z2 = true;
                this.mRingerAndZenModeMutedStreams &= ~(1 << streamType);
            } else {
                numStreamTypes = numStreamTypes4;
                numStreamTypes2 = z;
                this.mStreamStates[streamType].mute(z2);
                this.mRingerAndZenModeMutedStreams |= (z2 ? 1 : 0) << streamType;
            }
            streamType--;
            z = numStreamTypes2;
            numStreamTypes4 = numStreamTypes;
        }
    }

    private boolean isAlarm(int streamType) {
        return streamType == 4;
    }

    private boolean isNotificationOrRinger(int streamType) {
        return streamType == 5 || streamType == 2;
    }

    private boolean isMedia(int streamType) {
        return streamType == 3;
    }

    private boolean isSystem(int streamType) {
        return streamType == 1;
    }

    /* access modifiers changed from: private */
    public void setRingerModeInt(int ringerMode, boolean persist) {
        boolean change;
        synchronized (this.mSettingsLock) {
            change = this.mRingerMode != ringerMode;
            this.mRingerMode = ringerMode;
            muteRingerModeStreams();
        }
        if (persist) {
            sendMsg(this.mAudioHandler, 3, 0, 0, 0, (Object) null, 500);
        }
        if (change) {
            broadcastRingerMode("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", ringerMode);
        }
    }

    /* access modifiers changed from: package-private */
    public void postUpdateRingerModeServiceInt() {
        sendMsg(this.mAudioHandler, 25, 2, 0, 0, (Object) null, 0);
    }

    /* access modifiers changed from: private */
    public void onUpdateRingerModeServiceInt() {
        setRingerModeInt(getRingerModeInternal(), false);
    }

    public boolean shouldVibrate(int vibrateType) {
        int vibrateSetting;
        if (!this.mHasVibrator || (vibrateSetting = getVibrateSetting(vibrateType)) == 0) {
            return false;
        }
        if (vibrateSetting != 1) {
            if (vibrateSetting == 2 && getRingerModeExternal() == 1) {
                return true;
            }
            return false;
        } else if (getRingerModeExternal() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public int getVibrateSetting(int vibrateType) {
        if (!this.mHasVibrator) {
            return 0;
        }
        return (this.mVibrateSetting >> (vibrateType * 2)) & 3;
    }

    public void setVibrateSetting(int vibrateType, int vibrateSetting) {
        if (this.mHasVibrator) {
            this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(this.mVibrateSetting, vibrateType, vibrateSetting);
            broadcastVibrateSetting(vibrateType);
        }
    }

    /* access modifiers changed from: package-private */
    public int getModeOwnerPid() {
        try {
            return this.mSetModeDeathHandlers.get(0).getPid();
        } catch (Exception e) {
            return 0;
        }
    }

    private class SetModeDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mMode = 0;
        /* access modifiers changed from: private */
        public int mPid;

        SetModeDeathHandler(IBinder cb, int pid) {
            this.mCb = cb;
            this.mPid = pid;
        }

        public void binderDied() {
            int oldModeOwnerPid = 0;
            int newModeOwnerPid = 0;
            synchronized (AudioService.this.mDeviceBroker.mSetModeLock) {
                Log.w(AudioService.TAG, "setMode() client died");
                if (!AudioService.this.mSetModeDeathHandlers.isEmpty()) {
                    oldModeOwnerPid = AudioService.this.mSetModeDeathHandlers.get(0).getPid();
                }
                if (AudioService.this.mSetModeDeathHandlers.indexOf(this) < 0) {
                    Log.w(AudioService.TAG, "unregistered setMode() client died");
                } else {
                    newModeOwnerPid = AudioService.this.setModeInt(0, this.mCb, this.mPid, AudioService.TAG);
                }
            }
            if (newModeOwnerPid != oldModeOwnerPid && newModeOwnerPid != 0) {
                Log.i(AudioService.TAG, "In binderDied(), calling disconnectBluetoothSco()");
                AudioService.this.mDeviceBroker.postDisconnectBluetoothSco(newModeOwnerPid);
            }
        }

        public int getPid() {
            return this.mPid;
        }

        public void setMode(int mode) {
            this.mMode = mode;
        }

        public int getMode() {
            return this.mMode;
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    public void setMode(int mode, IBinder cb, String callingPackage) {
        int newModeOwnerPid;
        Log.i(TAG, "setMode(mode = " + mode + ", callingPackage = " + callingPackage + ", Process ID: " + Binder.getCallingPid());
        StringBuilder sb = new StringBuilder();
        sb.append("setMode(mode=");
        sb.append(mode);
        sb.append(", callingPackage=");
        sb.append(callingPackage);
        sb.append(")");
        Log.v(TAG, sb.toString());
        if (checkAudioSettingsPermission("setMode()")) {
            if (mode == 2 && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
                Log.w(TAG, "MODIFY_PHONE_STATE Permission Denial: setMode(MODE_IN_CALL) from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            } else if (mode >= -1 && mode < 4) {
                int oldModeOwnerPid = 0;
                synchronized (this.mDeviceBroker.mSetModeLock) {
                    if (!this.mSetModeDeathHandlers.isEmpty()) {
                        oldModeOwnerPid = this.mSetModeDeathHandlers.get(0).getPid();
                    }
                    if (mode == -1) {
                        mode = this.mMode;
                    }
                    newModeOwnerPid = setModeInt(mode, cb, Binder.getCallingPid(), callingPackage);
                }
                if (newModeOwnerPid != oldModeOwnerPid && newModeOwnerPid != 0) {
                    Log.i(TAG, "In setMode(), calling disconnectBluetoothSco()");
                    this.mDeviceBroker.postDisconnectBluetoothSco(newModeOwnerPid);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01b1  */
    @com.android.internal.annotations.GuardedBy({"mDeviceBroker.mSetModeLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setModeInt(int r19, android.os.IBinder r20, int r21, java.lang.String r22) {
        /*
            r18 = this;
            r7 = r18
            r1 = r20
            r8 = r21
            r9 = r22
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "setModeInt(mode="
            r0.append(r2)
            r2 = r19
            r0.append(r2)
            java.lang.String r3 = ", pid="
            r0.append(r3)
            r0.append(r8)
            java.lang.String r3 = ", caller="
            r0.append(r3)
            r0.append(r9)
            java.lang.String r3 = ")"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "AS.AudioService"
            android.util.Log.v(r3, r0)
            r4 = 0
            if (r1 != 0) goto L_0x0040
            java.lang.String r0 = "setModeInt() called with null binder"
            android.util.Log.e(r3, r0)
            return r4
        L_0x0040:
            r0 = 0
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r5 = r7.mSetModeDeathHandlers
            java.util.Iterator r10 = r5.iterator()
        L_0x0047:
            boolean r5 = r10.hasNext()
            r6 = 0
            if (r5 == 0) goto L_0x007a
            java.lang.Object r5 = r10.next()
            com.android.server.audio.AudioService$SetModeDeathHandler r5 = (com.android.server.audio.AudioService.SetModeDeathHandler) r5
            int r11 = r5.getPid()
            if (r11 != r8) goto L_0x0079
            r11 = r5
            r10.remove()
            android.os.IBinder r0 = r11.getBinder()     // Catch:{ NoSuchElementException -> 0x006f }
            r0.unlinkToDeath(r11, r6)     // Catch:{ NoSuchElementException -> 0x006f }
            android.os.IBinder r0 = r11.getBinder()     // Catch:{ NoSuchElementException -> 0x006f }
            if (r1 == r0) goto L_0x006d
            r0 = 0
            goto L_0x006e
        L_0x006d:
            r0 = r11
        L_0x006e:
            goto L_0x007a
        L_0x006f:
            r0 = move-exception
            r11 = 0
            java.lang.String r12 = "unlinkToDeath failed "
            android.util.Log.e(r3, r12)
            r0 = r11
            goto L_0x007a
        L_0x0079:
            goto L_0x0047
        L_0x007a:
            int r11 = r7.mMode
            r5 = 0
        L_0x007d:
            r12 = r2
            if (r2 != 0) goto L_0x00bd
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r13 = r7.mSetModeDeathHandlers
            boolean r13 = r13.isEmpty()
            if (r13 != 0) goto L_0x00bb
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r13 = r7.mSetModeDeathHandlers
            java.lang.Object r13 = r13.get(r6)
            r0 = r13
            com.android.server.audio.AudioService$SetModeDeathHandler r0 = (com.android.server.audio.AudioService.SetModeDeathHandler) r0
            android.os.IBinder r1 = r0.getBinder()
            int r12 = r0.getMode()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = " using mode="
            r13.append(r14)
            r13.append(r2)
            java.lang.String r14 = " instead due to death hdlr at pid="
            r13.append(r14)
            int r14 = r0.mPid
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            android.util.Log.w(r3, r13)
            r13 = r1
            goto L_0x00f2
        L_0x00bb:
            r13 = r1
            goto L_0x00f2
        L_0x00bd:
            if (r0 != 0) goto L_0x00c6
            com.android.server.audio.AudioService$SetModeDeathHandler r13 = new com.android.server.audio.AudioService$SetModeDeathHandler
            r13.<init>(r1, r8)
            r0 = r13
            goto L_0x00c7
        L_0x00c6:
            r13 = r0
        L_0x00c7:
            r1.linkToDeath(r13, r6)     // Catch:{ RemoteException -> 0x00cb }
            goto L_0x00e8
        L_0x00cb:
            r0 = move-exception
            r14 = r0
            r0 = r14
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.String r15 = "setMode() could not link to "
            r14.append(r15)
            r14.append(r1)
            java.lang.String r15 = " binder death"
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            android.util.Log.w(r3, r14)
        L_0x00e8:
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r0 = r7.mSetModeDeathHandlers
            r0.add(r6, r13)
            r13.setMode(r2)
            r0 = r13
            r13 = r1
        L_0x00f2:
            int r1 = r7.mMode
            if (r12 == r1) goto L_0x012e
            long r14 = android.os.Binder.clearCallingIdentity()
            int r1 = android.media.AudioSystem.setPhoneState(r12)
            android.os.Binder.restoreCallingIdentity(r14)
            if (r1 != 0) goto L_0x011a
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = " mode successfully set to "
            r5.append(r6)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            android.util.Log.v(r3, r5)
            r7.mMode = r12
            goto L_0x012b
        L_0x011a:
            if (r0 == 0) goto L_0x0125
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r5 = r7.mSetModeDeathHandlers
            r5.remove(r0)
            r5 = 0
            r13.unlinkToDeath(r0, r5)
        L_0x0125:
            java.lang.String r5 = " mode set to MODE_NORMAL after phoneState pb"
            android.util.Log.w(r3, r5)
            r2 = 0
        L_0x012b:
            r14 = r1
            r15 = r2
            goto L_0x0131
        L_0x012e:
            r1 = 0
            r14 = r1
            r15 = r2
        L_0x0131:
            if (r14 == 0) goto L_0x0142
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r1 = r7.mSetModeDeathHandlers
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x013c
            goto L_0x0142
        L_0x013c:
            r1 = r13
            r5 = r14
            r2 = r15
            r6 = 0
            goto L_0x007d
        L_0x0142:
            if (r14 != 0) goto L_0x01b1
            if (r12 == 0) goto L_0x0164
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r1 = r7.mSetModeDeathHandlers
            boolean r1 = r1.isEmpty()
            if (r1 == 0) goto L_0x0155
            java.lang.String r1 = "setMode() different from MODE_NORMAL with empty mode client stack"
            android.util.Log.e(r3, r1)
            goto L_0x0164
        L_0x0155:
            java.util.ArrayList<com.android.server.audio.AudioService$SetModeDeathHandler> r1 = r7.mSetModeDeathHandlers
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            com.android.server.audio.AudioService$SetModeDeathHandler r1 = (com.android.server.audio.AudioService.SetModeDeathHandler) r1
            int r1 = r1.getPid()
            r6 = r1
            goto L_0x0165
        L_0x0164:
            r6 = r4
        L_0x0165:
            com.android.server.audio.AudioEventLogger r5 = r7.mModeLogger
            com.android.server.audio.AudioServiceEvents$PhoneStateEvent r4 = new com.android.server.audio.AudioServiceEvents$PhoneStateEvent
            r1 = r4
            r2 = r22
            r3 = r21
            r19 = r0
            r0 = r4
            r4 = r15
            r8 = r5
            r5 = r6
            r16 = r10
            r10 = r6
            r6 = r12
            r1.<init>(r2, r3, r4, r5, r6)
            r8.log(r0)
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            int r0 = r7.getActiveStreamType(r0)
            int r8 = r7.getDeviceForStream(r0)
            com.android.server.audio.AudioService$VolumeStreamState[] r1 = r7.mStreamStates
            int[] r2 = mStreamVolumeAlias
            r2 = r2[r0]
            r1 = r1[r2]
            int r17 = r1.getIndex(r8)
            int[] r1 = mStreamVolumeAlias
            r2 = r1[r0]
            r5 = 1
            r1 = r18
            r3 = r17
            r4 = r8
            r6 = r22
            r1.setStreamVolumeInt(r2, r3, r4, r5, r6)
            r1 = 1
            r7.updateStreamVolumeAlias(r1, r9)
            android.content.Context r1 = r7.mContext
            android.media.AudioServiceInjector.handleModeChanged(r1, r10, r12)
            r7.updateAbsVolumeMultiModeDevices(r11, r12)
            r4 = r10
            goto L_0x01b5
        L_0x01b1:
            r19 = r0
            r16 = r10
        L_0x01b5:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.setModeInt(int, android.os.IBinder, int, java.lang.String):int");
    }

    public int getMode() {
        return this.mMode;
    }

    class LoadSoundEffectReply {
        public int mStatus = 1;

        LoadSoundEffectReply() {
        }
    }

    private void loadTouchSoundAssetDefaults() {
        SOUND_EFFECT_FILES.add("Effect_Tick.ogg");
        for (int i = 0; i < 10; i++) {
            int[][] iArr = this.SOUND_EFFECT_FILES_MAP;
            iArr[i][0] = 0;
            iArr[i][1] = -1;
        }
    }

    /* access modifiers changed from: private */
    public void loadTouchSoundAssets() {
        XmlResourceParser parser = null;
        if (SOUND_EFFECT_FILES.isEmpty()) {
            loadTouchSoundAssetDefaults();
            try {
                parser = this.mContext.getResources().getXml(18284545);
                XmlUtils.beginDocument(parser, TAG_AUDIO_ASSETS);
                boolean inTouchSoundsGroup = false;
                if (ASSET_FILE_VERSION.equals(parser.getAttributeValue((String) null, ATTR_VERSION))) {
                    while (true) {
                        XmlUtils.nextElement(parser);
                        String element = parser.getName();
                        if (element != null) {
                            if (element.equals(TAG_GROUP) && GROUP_TOUCH_SOUNDS.equals(parser.getAttributeValue((String) null, "name"))) {
                                inTouchSoundsGroup = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    while (true) {
                        if (!inTouchSoundsGroup) {
                            break;
                        }
                        XmlUtils.nextElement(parser);
                        String element2 = parser.getName();
                        if (element2 != null) {
                            if (!element2.equals(TAG_ASSET)) {
                                break;
                            }
                            String id = parser.getAttributeValue((String) null, ATTR_ASSET_ID);
                            String file = parser.getAttributeValue((String) null, ATTR_ASSET_FILE);
                            try {
                                int fx = AudioManager.class.getField(id).getInt((Object) null);
                                int i = SOUND_EFFECT_FILES.indexOf(file);
                                if (i == -1) {
                                    i = SOUND_EFFECT_FILES.size();
                                    SOUND_EFFECT_FILES.add(file);
                                }
                                this.SOUND_EFFECT_FILES_MAP[fx][0] = i;
                            } catch (Exception e) {
                                Log.w(TAG, "Invalid touch sound ID: " + id);
                            }
                        } else {
                            break;
                        }
                    }
                }
            } catch (Resources.NotFoundException e2) {
                Log.w(TAG, "audio assets file not found", e2);
                if (parser == null) {
                    return;
                }
            } catch (XmlPullParserException e3) {
                Log.w(TAG, "XML parser exception reading touch sound assets", e3);
                if (parser == null) {
                    return;
                }
            } catch (IOException e4) {
                Log.w(TAG, "I/O exception reading touch sound assets", e4);
                if (parser == null) {
                    return;
                }
            } catch (Throwable th) {
                if (parser != null) {
                    parser.close();
                }
                throw th;
            }
            parser.close();
        }
    }

    public void playSoundEffect(int effectType) {
        playSoundEffectVolume(effectType, -1.0f);
    }

    public void playSoundEffectVolume(int effectType, float volume) {
        if (!isStreamMutedByRingerOrZenMode(1)) {
            if (effectType >= 10 || effectType < 0) {
                Log.w(TAG, "AudioService effectType value " + effectType + " out of range");
                return;
            }
            sendMsg(this.mAudioHandler, 5, 2, effectType, (int) (1000.0f * volume), (Object) null, 0);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0031, code lost:
        if (r1.mStatus != 0) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0034, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadSoundEffects() {
        /*
            r10 = this;
            r0 = 3
            com.android.server.audio.AudioService$LoadSoundEffectReply r1 = new com.android.server.audio.AudioService$LoadSoundEffectReply
            r1.<init>()
            monitor-enter(r1)
            com.android.server.audio.AudioService$AudioHandler r2 = r10.mAudioHandler     // Catch:{ all -> 0x0036 }
            r3 = 7
            r4 = 2
            r5 = 0
            r6 = 0
            r8 = 0
            r7 = r1
            sendMsg(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0036 }
        L_0x0012:
            int r2 = r1.mStatus     // Catch:{ all -> 0x0036 }
            r3 = 1
            if (r2 != r3) goto L_0x002e
            int r2 = r0 + -1
            if (r0 <= 0) goto L_0x002d
            r3 = 5000(0x1388, double:2.4703E-320)
            r1.wait(r3)     // Catch:{ InterruptedException -> 0x0021 }
            goto L_0x002b
        L_0x0021:
            r0 = move-exception
            java.lang.String r3 = "AS.AudioService"
            java.lang.String r4 = "loadSoundEffects Interrupted while waiting sound pool loaded."
            android.util.Log.w(r3, r4)     // Catch:{ all -> 0x003c }
        L_0x002b:
            r0 = r2
            goto L_0x0012
        L_0x002d:
            r0 = r2
        L_0x002e:
            monitor-exit(r1)     // Catch:{ all -> 0x0036 }
            int r2 = r1.mStatus
            if (r2 != 0) goto L_0x0034
            goto L_0x0035
        L_0x0034:
            r3 = 0
        L_0x0035:
            return r3
        L_0x0036:
            r2 = move-exception
            r9 = r2
            r2 = r0
            r0 = r9
        L_0x003a:
            monitor-exit(r1)     // Catch:{ all -> 0x003c }
            throw r0
        L_0x003c:
            r0 = move-exception
            goto L_0x003a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.loadSoundEffects():boolean");
    }

    /* access modifiers changed from: protected */
    public void scheduleLoadSoundEffects() {
        sendMsg(this.mAudioHandler, 7, 2, 0, 0, (Object) null, 0);
    }

    public void unloadSoundEffects() {
        sendMsg(this.mAudioHandler, 15, 2, 0, 0, (Object) null, 0);
    }

    class SoundPoolListenerThread extends Thread {
        public SoundPoolListenerThread() {
            super("SoundPoolListenerThread");
        }

        public void run() {
            Looper.prepare();
            Looper unused = AudioService.this.mSoundPoolLooper = Looper.myLooper();
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mSoundPool != null) {
                    SoundPoolCallback unused2 = AudioService.this.mSoundPoolCallBack = new SoundPoolCallback();
                    AudioService.this.mSoundPool.setOnLoadCompleteListener(AudioService.this.mSoundPoolCallBack);
                }
                AudioService.this.mSoundEffectsLock.notify();
            }
            Looper.loop();
        }
    }

    private final class SoundPoolCallback implements SoundPool.OnLoadCompleteListener {
        List<Integer> mSamples;
        int mStatus;

        private SoundPoolCallback() {
            this.mStatus = 1;
            this.mSamples = new ArrayList();
        }

        public int status() {
            return this.mStatus;
        }

        public void setSamples(int[] samples) {
            for (int i = 0; i < samples.length; i++) {
                if (samples[i] > 0) {
                    this.mSamples.add(Integer.valueOf(samples[i]));
                }
            }
        }

        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
            synchronized (AudioService.this.mSoundEffectsLock) {
                int i = this.mSamples.indexOf(Integer.valueOf(sampleId));
                if (i >= 0) {
                    this.mSamples.remove(i);
                }
                if (status != 0 || this.mSamples.isEmpty()) {
                    this.mStatus = status;
                    AudioService.this.mSoundEffectsLock.notify();
                }
            }
        }
    }

    public void reloadMusicVolume() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = 0; streamType < numStreamTypes; streamType++) {
            VolumeStreamState streamState = this.mStreamStates[streamType];
            if (mStreamVolumeAlias[streamType] == 3) {
                streamState.readSettings();
                Log.d(TAG, "reloadMusicVolume stream=" + streamType);
            }
        }
        checkAllFixedVolumeDevices();
    }

    public void reloadAudioSettings() {
        readAudioSettings(false);
    }

    /* access modifiers changed from: private */
    public void readAudioSettings(boolean userSwitch) {
        readPersistedSettings();
        readUserRestrictions();
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int streamType = 0; streamType < numStreamTypes; streamType++) {
            VolumeStreamState streamState = this.mStreamStates[streamType];
            if (!userSwitch || mStreamVolumeAlias[streamType] != 3) {
                streamState.readSettings();
                synchronized (VolumeStreamState.class) {
                    if (streamState.mIsMuted && ((!isStreamAffectedByMute(streamType) && !isStreamMutedByRingerOrZenMode(streamType)) || this.mUseFixedVolume)) {
                        boolean unused = streamState.mIsMuted = false;
                    }
                }
            }
        }
        setRingerModeInt(getRingerModeInternal(), false);
        checkAllFixedVolumeDevices();
        checkAllAliasStreamVolumes();
        checkMuteAffectedStreams();
        synchronized (this.mSafeMediaVolumeStateLock) {
            this.mMusicActiveMs = MathUtils.constrain(Settings.Secure.getIntForUser(this.mContentResolver, "unsafe_volume_music_active_ms", 0, -2), 0, UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX);
            if (this.mSafeMediaVolumeState == 3) {
                enforceSafeMediaVolume(TAG);
            }
        }
    }

    public void setSpeakerphoneOn(boolean on) {
        if (checkAudioSettingsPermission("setSpeakerphoneOn()")) {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
                synchronized (this.mSetModeDeathHandlers) {
                    Iterator<SetModeDeathHandler> it = this.mSetModeDeathHandlers.iterator();
                    while (it.hasNext()) {
                        if (it.next().getMode() == 2) {
                            Log.w(TAG, "getMode is call, Permission Denial: setSpeakerphoneOn from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
                            return;
                        }
                    }
                }
            }
            String eventSource = "setSpeakerphoneOn(" + on + ") from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid();
            Log.i(TAG, "In setSpeakerphoneOn(), on: " + on + ", eventSource: " + eventSource);
            if (this.mDeviceBroker.setSpeakerphoneOn(on, eventSource)) {
                long ident = Binder.clearCallingIdentity();
                try {
                    this.mContext.sendBroadcastAsUser(new Intent("android.media.action.SPEAKERPHONE_STATE_CHANGED").setFlags(1073741824), UserHandle.ALL);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
            AudioServiceInjector.handleSpeakerChanged(this.mContext, Binder.getCallingPid(), isSpeakerphoneOn());
        }
    }

    public boolean isSpeakerphoneOn() {
        return this.mDeviceBroker.isSpeakerphoneOn();
    }

    public void setBluetoothScoOn(boolean on) {
        if (checkAudioSettingsPermission("setBluetoothScoOn()")) {
            if (UserHandle.getCallingAppId() >= 10000) {
                Log.i(TAG, "In setBluetoothScoOn(), on: " + on + ". The calling application Uid: " + Binder.getCallingUid() + ", is greater than FIRST_APPLICATION_UID exiting from setBluetoothScoOn()");
                this.mDeviceBroker.setBluetoothScoOnByApp(on);
                return;
            }
            String eventSource = "setBluetoothScoOn(" + on + ") from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid();
            Log.i(TAG, "In setBluetoothScoOn(), eventSource: " + eventSource);
            this.mDeviceBroker.setBluetoothScoOn(on, eventSource);
        }
    }

    public boolean isBluetoothScoOn() {
        return this.mDeviceBroker.isBluetoothScoOnForApp();
    }

    public void setBluetoothA2dpOn(boolean on) {
        this.mDeviceBroker.setBluetoothA2dpOn_Async(on, "setBluetoothA2dpOn(" + on + ") from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid());
    }

    public boolean isBluetoothA2dpOn() {
        return this.mDeviceBroker.isBluetoothA2dpOn();
    }

    public void startBluetoothSco(IBinder cb, int targetSdkVersion) {
        Log.i(TAG, "In startBluetoothSco()");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || adapter.getState() != 12) {
            Log.i(TAG, "startBluetoothSco(), BT is not turned ON or adapter is null");
            return;
        }
        int scoAudioMode = targetSdkVersion < 18 ? 0 : -1;
        startBluetoothScoInt(cb, scoAudioMode, "startBluetoothSco()" + ") from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid());
    }

    public void startBluetoothScoVirtualCall(IBinder cb) {
        Log.i(TAG, "In startBluetoothScoVirtualCall()");
        startBluetoothScoInt(cb, 0, "startBluetoothScoVirtualCall()" + ") from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid());
    }

    /* access modifiers changed from: package-private */
    public void startBluetoothScoInt(IBinder cb, int scoAudioMode, String eventSource) {
        Log.i(TAG, "In startBluetoothScoInt(), scoAudioMode: " + scoAudioMode);
        if (checkAudioSettingsPermission("startBluetoothSco()") && this.mSystemReady) {
            synchronized (this.mDeviceBroker.mSetModeLock) {
                this.mDeviceBroker.startBluetoothScoForClient_Sync(cb, scoAudioMode, eventSource);
            }
        }
    }

    public void stopBluetoothSco(IBinder cb) {
        Log.i(TAG, "In stopBluetoothSco()");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || adapter.getState() != 12) {
            Log.i(TAG, "stopBluetoothSco(), BT is not turned ON or adapter is null");
        } else if (checkAudioSettingsPermission("stopBluetoothSco()") && this.mSystemReady) {
            String eventSource = "stopBluetoothSco()" + ") from u/pid:" + Binder.getCallingUid() + SliceClientPermissions.SliceAuthority.DELIMITER + Binder.getCallingPid();
            synchronized (this.mDeviceBroker.mSetModeLock) {
                this.mDeviceBroker.stopBluetoothScoForClient_Sync(cb, eventSource);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    /* access modifiers changed from: private */
    public void onCheckMusicActive(String caller) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            if (this.mSafeMediaVolumeState == 2) {
                int device = getDeviceForStream(3);
                if ((67108876 & device) != 0) {
                    sendMsg(this.mAudioHandler, 11, 0, 0, 0, caller, MUSIC_ACTIVE_POLL_PERIOD_MS);
                    int index = this.mStreamStates[3].getIndex(device);
                    if (AudioSystem.isStreamActive(3, 0) && index > safeMediaVolumeIndex(device)) {
                        this.mMusicActiveMs += MUSIC_ACTIVE_POLL_PERIOD_MS;
                        if (this.mMusicActiveMs > UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX) {
                            setSafeMediaVolumeEnabled(true, caller);
                            this.mMusicActiveMs = 0;
                        }
                        saveMusicActiveMs();
                    }
                }
            }
        }
    }

    private void saveMusicActiveMs() {
        this.mAudioHandler.obtainMessage(17, this.mMusicActiveMs, 0).sendToTarget();
    }

    private int getSafeUsbMediaVolumeIndex() {
        int min = MIN_STREAM_VOLUME[3];
        int max = MAX_STREAM_VOLUME[3];
        this.mSafeUsbMediaVolumeDbfs = ((float) this.mContext.getResources().getInteger(17694882)) / 100.0f;
        while (true) {
            if (Math.abs(max - min) <= 1) {
                break;
            }
            int index = (max + min) / 2;
            float gainDB = AudioSystem.getStreamVolumeDB(3, index, BroadcastQueueInjector.FLAG_IMMUTABLE);
            if (Float.isNaN(gainDB)) {
                break;
            }
            float f = this.mSafeUsbMediaVolumeDbfs;
            if (gainDB == f) {
                min = index;
                break;
            } else if (gainDB < f) {
                min = index;
            } else {
                max = index;
            }
        }
        return min * 10;
    }

    /* access modifiers changed from: private */
    public void onConfigureSafeVolume(boolean force, String caller) {
        boolean safeMediaVolumeEnabled;
        int persistedState;
        synchronized (this.mSafeMediaVolumeStateLock) {
            int mcc = this.mContext.getResources().getConfiguration().mcc;
            if (this.mMcc != mcc || (this.mMcc == 0 && force)) {
                this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(17694881) * 10;
                this.mSafeUsbMediaVolumeIndex = getSafeUsbMediaVolumeIndex();
                if (!SystemProperties.getBoolean("audio.safemedia.force", false)) {
                    if (!this.mContext.getResources().getBoolean(17891509)) {
                        safeMediaVolumeEnabled = false;
                        boolean safeMediaVolumeBypass = SystemProperties.getBoolean("audio.safemedia.bypass", false);
                        if (safeMediaVolumeEnabled || safeMediaVolumeBypass) {
                            this.mSafeMediaVolumeState = 1;
                            persistedState = 1;
                        } else {
                            persistedState = 3;
                            if (this.mSafeMediaVolumeState != 2) {
                                this.mSafeMediaVolumeState = 3;
                                enforceSafeMediaVolume(caller);
                            }
                        }
                        this.mMcc = mcc;
                        sendMsg(this.mAudioHandler, 14, 2, persistedState, 0, (Object) null, 0);
                    }
                }
                safeMediaVolumeEnabled = true;
                boolean safeMediaVolumeBypass2 = SystemProperties.getBoolean("audio.safemedia.bypass", false);
                if (safeMediaVolumeEnabled) {
                }
                this.mSafeMediaVolumeState = 1;
                persistedState = 1;
                this.mMcc = mcc;
                sendMsg(this.mAudioHandler, 14, 2, persistedState, 0, (Object) null, 0);
            }
        }
    }

    private int checkForRingerModeChange(int oldIndex, int direction, int step, boolean isMuted, String caller, int flags) {
        int result = 1;
        if (isPlatformTelevision() || this.mIsSingleVolume || !AudioServiceInjector.isXOptMode()) {
            return 1;
        }
        int ringerMode = getRingerModeInternal();
        if (ringerMode == 0) {
            if (this.mIsSingleVolume && direction == -1 && oldIndex >= step * 2 && isMuted) {
                ringerMode = 2;
            } else if (direction == 1 || direction == 101 || direction == 100) {
                if (!this.mVolumePolicy.volumeUpToExitSilent) {
                    result = 1 | 128;
                } else {
                    ringerMode = (!this.mHasVibrator || direction != 1) ? 2 : 2;
                }
            }
            result &= -2;
        } else if (ringerMode != 1) {
            if (ringerMode != 2) {
                Log.e(TAG, "checkForRingerModeChange() wrong ringer mode: " + ringerMode);
            } else if (direction == -1) {
                if (this.mHasVibrator) {
                    if (step <= oldIndex && oldIndex < step * 2) {
                        ringerMode = 1;
                        this.mLoweredFromNormalToVibrateTime = SystemClock.uptimeMillis();
                    }
                } else if (oldIndex == step && this.mVolumePolicy.volumeDownToEnterSilent) {
                    ringerMode = 0;
                }
            } else if (this.mIsSingleVolume && (direction == 101 || direction == -100)) {
                if (this.mHasVibrator) {
                    ringerMode = 1;
                } else {
                    ringerMode = 0;
                }
                result = 1 & -2;
            }
        } else if (!this.mHasVibrator) {
            Log.e(TAG, "checkForRingerModeChange() current ringer mode is vibratebut no vibrator is present");
        } else {
            if (direction == -1) {
                if (this.mIsSingleVolume && oldIndex >= step * 2 && isMuted) {
                    ringerMode = 2;
                } else if (this.mPrevVolDirection != -1) {
                    if (!this.mVolumePolicy.volumeDownToEnterSilent) {
                        result = 1 | 2048;
                    } else if (SystemClock.uptimeMillis() - this.mLoweredFromNormalToVibrateTime > ((long) this.mVolumePolicy.vibrateToSilentDebounce) && this.mRingerModeDelegate.canVolumeDownEnterSilent()) {
                        ringerMode = 0;
                    }
                }
            } else if (direction == 1 || direction == 101 || direction == 100) {
                ringerMode = 2;
            }
            result &= -2;
        }
        if (!isAndroidNPlus(caller) || !wouldToggleZenMode(ringerMode) || this.mNm.isNotificationPolicyAccessGrantedForPackage(caller) || (flags & 4096) != 0) {
            setRingerMode(ringerMode, "AS.AudioService.checkForRingerModeChange", false);
            this.mPrevVolDirection = direction;
            return result;
        }
        throw new SecurityException("Not allowed to change Do Not Disturb state");
    }

    public boolean isStreamAffectedByRingerMode(int streamType) {
        return (this.mRingerModeAffectedStreams & (1 << streamType)) != 0;
    }

    private boolean shouldZenMuteStream(int streamType) {
        if (this.mNm.getZenMode() != 1) {
            return false;
        }
        NotificationManager.Policy zenPolicy = this.mNm.getConsolidatedNotificationPolicy();
        boolean muteAlarms = (zenPolicy.priorityCategories & 32) == 0;
        boolean muteMedia = (zenPolicy.priorityCategories & 64) == 0;
        boolean muteSystem = (zenPolicy.priorityCategories & 128) == 0;
        boolean muteNotificationAndRing = ZenModeConfig.areAllPriorityOnlyNotificationZenSoundsMuted(this.mNm.getConsolidatedNotificationPolicy());
        if ((!muteAlarms || !isAlarm(streamType)) && ((!muteMedia || !isMedia(streamType)) && ((!muteSystem || !isSystem(streamType)) && (!muteNotificationAndRing || !isNotificationOrRinger(streamType))))) {
            return false;
        }
        return true;
    }

    private boolean isStreamMutedByRingerOrZenMode(int streamType) {
        return (this.mRingerAndZenModeMutedStreams & (1 << streamType)) != 0;
    }

    private boolean updateZenModeAffectedStreams() {
        int zenModeAffectedStreams = 0;
        if (this.mSystemReady && this.mNm.getZenMode() == 1) {
            NotificationManager.Policy zenPolicy = this.mNm.getConsolidatedNotificationPolicy();
            if ((zenPolicy.priorityCategories & 32) == 0) {
                zenModeAffectedStreams = 0 | 16;
            }
            if ((zenPolicy.priorityCategories & 64) == 0) {
                zenModeAffectedStreams |= 8;
            }
            if ((zenPolicy.priorityCategories & 128) == 0) {
                zenModeAffectedStreams |= 2;
            }
        }
        if (this.mZenModeAffectedStreams == zenModeAffectedStreams) {
            return false;
        }
        this.mZenModeAffectedStreams = zenModeAffectedStreams;
        return true;
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mSettingsLock"})
    public boolean updateRingerAndZenModeAffectedStreams() {
        int ringerModeAffectedStreams;
        int ringerModeAffectedStreams2;
        boolean updatedZenModeAffectedStreams = updateZenModeAffectedStreams();
        int ringerModeAffectedStreams3 = Settings.System.getIntForUser(this.mContentResolver, "mode_ringer_streams_affected", 166, -2);
        if (this.mIsSingleVolume) {
            ringerModeAffectedStreams3 = 0;
        } else {
            AudioManagerInternal.RingerModeDelegate ringerModeDelegate = this.mRingerModeDelegate;
            if (ringerModeDelegate != null) {
                ringerModeAffectedStreams3 = ringerModeDelegate.getRingerModeAffectedStreams(ringerModeAffectedStreams3);
            }
        }
        if (this.mCameraSoundForced) {
            ringerModeAffectedStreams = ringerModeAffectedStreams3 & -129;
        } else {
            ringerModeAffectedStreams = ringerModeAffectedStreams3 | 128;
        }
        if (mStreamVolumeAlias[8] == 2) {
            ringerModeAffectedStreams2 = ringerModeAffectedStreams | 256;
        } else {
            ringerModeAffectedStreams2 = ringerModeAffectedStreams & -257;
        }
        if (ringerModeAffectedStreams2 == this.mRingerModeAffectedStreams) {
            return updatedZenModeAffectedStreams;
        }
        Settings.System.putIntForUser(this.mContentResolver, "mode_ringer_streams_affected", ringerModeAffectedStreams2, -2);
        this.mRingerModeAffectedStreams = ringerModeAffectedStreams2;
        return true;
    }

    public boolean isStreamAffectedByMute(int streamType) {
        return (this.mMuteAffectedStreams & (1 << streamType)) != 0;
    }

    private void ensureValidDirection(int direction) {
        if (direction != -100 && direction != -1 && direction != 0 && direction != 1 && direction != 100 && direction != 101) {
            throw new IllegalArgumentException("Bad direction " + direction);
        }
    }

    private void ensureValidStreamType(int streamType) {
        if (streamType < 0 || streamType >= this.mStreamStates.length) {
            throw new IllegalArgumentException("Bad stream type " + streamType);
        }
    }

    private boolean isMuteAdjust(int adjust) {
        return adjust == -100 || adjust == 100 || adjust == 101;
    }

    /* access modifiers changed from: package-private */
    public boolean isInCommunication() {
        long ident = Binder.clearCallingIdentity();
        boolean IsInCall = ((TelecomManager) this.mContext.getSystemService("telecom")).isInCall();
        Binder.restoreCallingIdentity(ident);
        return IsInCall || getMode() == 3 || getMode() == 2;
    }

    private boolean wasStreamActiveRecently(int stream, int delay_ms) {
        return AudioSystem.isStreamActive(stream, delay_ms) || AudioSystem.isStreamActiveRemotely(stream, delay_ms);
    }

    private int getActiveStreamType(int suggestedStreamType) {
        if (this.mIsSingleVolume && suggestedStreamType == Integer.MIN_VALUE) {
            return 3;
        }
        if (!AudioServiceInjector.isXOptMode()) {
            return AudioServiceInjector.getActiveStreamType(isInCommunication(), this.mPlatformType, suggestedStreamType, sStreamOverrideDelayMs, true, this.mLockVoiseAssistStream);
        }
        if (this.mPlatformType == 1) {
            if (isInCommunication()) {
                return AudioSystem.getForceUse(0) == 3 ? 6 : 0;
            }
            if (suggestedStreamType == Integer.MIN_VALUE) {
                if (wasStreamActiveRecently(2, sStreamOverrideDelayMs)) {
                    Log.v(TAG, "getActiveStreamType: Forcing STREAM_RING stream active");
                    return 2;
                } else if (wasStreamActiveRecently(5, sStreamOverrideDelayMs)) {
                    Log.v(TAG, "getActiveStreamType: Forcing STREAM_NOTIFICATION stream active");
                    return 5;
                } else {
                    Log.v(TAG, "getActiveStreamType: Forcing DEFAULT_VOL_STREAM_NO_PLAYBACK(3) b/c default");
                    return 3;
                }
            } else if (wasStreamActiveRecently(5, sStreamOverrideDelayMs)) {
                Log.v(TAG, "getActiveStreamType: Forcing STREAM_NOTIFICATION stream active");
                return 5;
            } else if (wasStreamActiveRecently(2, sStreamOverrideDelayMs)) {
                Log.v(TAG, "getActiveStreamType: Forcing STREAM_RING stream active");
                return 2;
            }
        }
        if (isInCommunication()) {
            if (AudioSystem.getForceUse(0) == 3) {
                Log.v(TAG, "getActiveStreamType: Forcing STREAM_BLUETOOTH_SCO");
                return 6;
            }
            Log.v(TAG, "getActiveStreamType: Forcing STREAM_VOICE_CALL");
            return 0;
        } else if (AudioSystem.isStreamActive(5, sStreamOverrideDelayMs)) {
            Log.v(TAG, "getActiveStreamType: Forcing STREAM_NOTIFICATION");
            return 5;
        } else if (AudioSystem.isStreamActive(2, sStreamOverrideDelayMs)) {
            Log.v(TAG, "getActiveStreamType: Forcing STREAM_RING");
            return 2;
        } else if (suggestedStreamType != Integer.MIN_VALUE) {
            Log.v(TAG, "getActiveStreamType: Returning suggested type " + suggestedStreamType);
            return suggestedStreamType;
        } else if (AudioSystem.isStreamActive(5, sStreamOverrideDelayMs)) {
            Log.v(TAG, "getActiveStreamType: Forcing STREAM_NOTIFICATION");
            return 5;
        } else if (AudioSystem.isStreamActive(2, sStreamOverrideDelayMs)) {
            Log.v(TAG, "getActiveStreamType: Forcing STREAM_RING");
            return 2;
        } else {
            Log.v(TAG, "getActiveStreamType: Forcing DEFAULT_VOL_STREAM_NO_PLAYBACK(3) b/c default");
            return 3;
        }
    }

    /* access modifiers changed from: private */
    public void broadcastRingerMode(String action, int ringerMode) {
        Intent broadcast = new Intent(action);
        broadcast.putExtra("android.media.EXTRA_RINGER_MODE", ringerMode);
        broadcast.addFlags(603979776);
        sendStickyBroadcastToAll(broadcast);
    }

    private void broadcastVibrateSetting(int vibrateType) {
        if (this.mActivityManagerInternal.isSystemReady()) {
            Intent broadcast = new Intent("android.media.VIBRATE_SETTING_CHANGED");
            broadcast.putExtra("android.media.EXTRA_VIBRATE_TYPE", vibrateType);
            broadcast.putExtra("android.media.EXTRA_VIBRATE_SETTING", getVibrateSetting(vibrateType));
            sendBroadcastToAll(broadcast);
        }
    }

    /* access modifiers changed from: private */
    public void queueMsgUnderWakeLock(Handler handler, int msg, int arg1, int arg2, Object obj, int delay) {
        long ident = Binder.clearCallingIdentity();
        this.mAudioEventWakeLock.acquire();
        Binder.restoreCallingIdentity(ident);
        sendMsg(handler, msg, 2, arg1, arg2, obj, delay);
    }

    /* access modifiers changed from: private */
    public static void sendMsg(Handler handler, int msg, int existingMsgPolicy, int arg1, int arg2, Object obj, int delay) {
        if (existingMsgPolicy == 0) {
            handler.removeMessages(msg);
        } else if (existingMsgPolicy == 1 && handler.hasMessages(msg)) {
            return;
        }
        handler.sendMessageAtTime(handler.obtainMessage(msg, arg1, arg2, obj), SystemClock.uptimeMillis() + ((long) delay));
    }

    /* access modifiers changed from: package-private */
    public boolean checkAudioSettingsPermission(String method) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_SETTINGS") == 0) {
            return true;
        }
        Log.w(TAG, "Audio Settings Permission Denial: " + method + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return false;
    }

    /* access modifiers changed from: package-private */
    public int getDeviceForStream(int stream) {
        int device = getDevicesForStream(stream);
        if (((device - 1) & device) == 0) {
            return device;
        }
        if ((device & 2) != 0) {
            return 2;
        }
        if ((262144 & device) != 0) {
            return DumpState.DUMP_DOMAIN_PREFERRED;
        }
        if ((524288 & device) != 0) {
            return DumpState.DUMP_FROZEN;
        }
        if ((2097152 & device) != 0) {
            return DumpState.DUMP_COMPILER_STATS;
        }
        return device & 896;
    }

    /* access modifiers changed from: private */
    public int getDevicesForStream(int stream) {
        return getDevicesForStream(stream, true);
    }

    private int getDevicesForStream(int stream, boolean checkOthers) {
        int observeDevicesForStream_syncVSS;
        ensureValidStreamType(stream);
        synchronized (VolumeStreamState.class) {
            observeDevicesForStream_syncVSS = this.mStreamStates[stream].observeDevicesForStream_syncVSS(checkOthers);
        }
        return observeDevicesForStream_syncVSS;
    }

    /* access modifiers changed from: private */
    public void observeDevicesForStreams(int skipStream) {
        synchronized (VolumeStreamState.class) {
            for (int stream = 0; stream < this.mStreamStates.length; stream++) {
                if (stream != skipStream) {
                    this.mStreamStates[stream].observeDevicesForStream_syncVSS(false);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void postObserveDevicesForAllStreams() {
        sendMsg(this.mAudioHandler, MSG_OBSERVE_DEVICES_FOR_ALL_STREAMS, 2, 0, 0, (Object) null, 0);
    }

    /* access modifiers changed from: private */
    public void onObserveDevicesForAllStreams() {
        observeDevicesForStreams(-1);
    }

    public void setWiredDeviceConnectionState(int type, int state, String address, String name, String caller) {
        if (state == 1 || state == 0) {
            this.mDeviceBroker.setWiredDeviceConnectionState(type, state, address, name, caller);
            return;
        }
        throw new IllegalArgumentException("Invalid state " + state);
    }

    public void setBluetoothHearingAidDeviceConnectionState(BluetoothDevice device, int state, boolean suppressNoisyIntent, int musicDevice) {
        if (device == null) {
            throw new IllegalArgumentException("Illegal null device");
        } else if (state == 2 || state == 0) {
            if (state == 2) {
                this.mPlaybackMonitor.registerPlaybackCallback(this.mVoiceActivityMonitor, true);
            } else {
                this.mPlaybackMonitor.unregisterPlaybackCallback(this.mVoiceActivityMonitor);
            }
            this.mDeviceBroker.postBluetoothHearingAidDeviceConnectionState(device, state, suppressNoisyIntent, musicDevice, "AudioService");
        } else {
            throw new IllegalArgumentException("Illegal BluetoothProfile state for device  (dis)connection, got " + state);
        }
    }

    public void setBluetoothA2dpDeviceConnectionStateSuppressNoisyIntent(BluetoothDevice device, int state, int profile, boolean suppressNoisyIntent, int a2dpVolume) {
        if (device == null) {
            throw new IllegalArgumentException("Illegal null device");
        } else if (state == 2 || state == 0) {
            this.mDeviceBroker.postBluetoothA2dpDeviceConnectionStateSuppressNoisyIntent(device, state, profile, suppressNoisyIntent, a2dpVolume);
        } else {
            throw new IllegalArgumentException("Illegal BluetoothProfile state for device  (dis)connection, got " + state);
        }
    }

    public void handleBluetoothA2dpDeviceConfigChange(BluetoothDevice device) {
        if (device != null) {
            this.mDeviceBroker.postBluetoothA2dpDeviceConfigChange(device);
            return;
        }
        throw new IllegalArgumentException("Illegal null device");
    }

    public void handleBluetoothA2dpActiveDeviceChange(BluetoothDevice device, int state, int profile, boolean suppressNoisyIntent, int a2dpVolume) {
        if (device == null) {
            throw new IllegalArgumentException("Illegal null device");
        } else if (profile != 2 && profile != 11) {
            throw new IllegalArgumentException("invalid profile " + profile);
        } else if (state == 2 || state == 0) {
            this.mDeviceBroker.postBluetoothA2dpDeviceConfigChangeExt(device, state, profile, suppressNoisyIntent, a2dpVolume);
        } else {
            throw new IllegalArgumentException("Invalid state " + state);
        }
    }

    /* access modifiers changed from: package-private */
    public void postAccessoryPlugMediaUnmute(int newDevice) {
        sendMsg(this.mAudioHandler, 21, 2, newDevice, 0, (Object) null, 0);
    }

    /* access modifiers changed from: private */
    public void onAccessoryPlugMediaUnmute(int newDevice) {
        Log.i(TAG, String.format("onAccessoryPlugMediaUnmute newDevice=%d [%s]", new Object[]{Integer.valueOf(newDevice), AudioSystem.getOutputDeviceName(newDevice)}));
        if (this.mNm.getZenMode() != 2 && (DEVICE_MEDIA_UNMUTED_ON_PLUG & newDevice) != 0 && this.mStreamStates[3].mIsMuted && this.mStreamStates[3].getIndex(newDevice) != 0 && (AudioSystem.getDevicesForStream(3) & newDevice) != 0) {
            Log.i(TAG, String.format(" onAccessoryPlugMediaUnmute unmuting device=%d [%s]", new Object[]{Integer.valueOf(newDevice), AudioSystem.getOutputDeviceName(newDevice)}));
            this.mStreamStates[3].mute(false);
        }
    }

    public boolean hasHapticChannels(Uri uri) {
        MediaExtractor extractor = new MediaExtractor();
        try {
            extractor.setDataSource(this.mContext, uri, (Map) null);
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                if (format.containsKey("haptic-channel-count") && format.getInteger("haptic-channel-count") > 0) {
                    return true;
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "hasHapticChannels failure:" + e);
        }
        return false;
    }

    private class VolumeStreamState {
        /* access modifiers changed from: private */
        public final SparseIntArray mIndexMap;
        /* access modifiers changed from: private */
        public int mIndexMax;
        /* access modifiers changed from: private */
        public int mIndexMin;
        /* access modifiers changed from: private */
        public boolean mIsMuted;
        private int mObservedDevices;
        private final Intent mStreamDevicesChanged;
        /* access modifiers changed from: private */
        public final int mStreamType;
        private final Intent mVolumeChanged;
        /* access modifiers changed from: private */
        public String mVolumeIndexSettingName;

        private VolumeStreamState(String settingName, int streamType) {
            this.mIndexMap = new SparseIntArray(8);
            this.mVolumeIndexSettingName = settingName;
            this.mStreamType = streamType;
            this.mIndexMin = AudioService.MIN_STREAM_VOLUME[streamType] * 10;
            this.mIndexMax = AudioService.MAX_STREAM_VOLUME[streamType] * 10;
            AudioSystem.initStreamVolume(streamType, this.mIndexMin / 10, this.mIndexMax / 10);
            readSettings();
            this.mVolumeChanged = new Intent("android.media.VOLUME_CHANGED_ACTION");
            this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", this.mStreamType);
            this.mStreamDevicesChanged = new Intent("android.media.STREAM_DEVICES_CHANGED_ACTION");
            this.mStreamDevicesChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", this.mStreamType);
        }

        public int observeDevicesForStream_syncVSS(boolean checkOthers) {
            int devices = AudioSystem.getDevicesForStream(this.mStreamType);
            if (devices == this.mObservedDevices) {
                return devices;
            }
            int prevDevices = this.mObservedDevices;
            this.mObservedDevices = devices;
            if (checkOthers) {
                AudioService.this.observeDevicesForStreams(this.mStreamType);
            }
            int[] iArr = AudioService.mStreamVolumeAlias;
            int i = this.mStreamType;
            if (iArr[i] == i) {
                EventLogTags.writeStreamDevicesChanged(i, prevDevices, devices);
            }
            AudioService.this.sendBroadcastToAll(this.mStreamDevicesChanged.putExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", prevDevices).putExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", devices));
            return devices;
        }

        public String getSettingNameForDevice(int device) {
            if (!hasValidSettingsName()) {
                return null;
            }
            String suffix = AudioSystem.getOutputDeviceName(device);
            if (suffix.isEmpty()) {
                return this.mVolumeIndexSettingName;
            }
            return this.mVolumeIndexSettingName + "_" + suffix;
        }

        /* access modifiers changed from: private */
        public boolean hasValidSettingsName() {
            String str = this.mVolumeIndexSettingName;
            return str != null && !str.isEmpty();
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x002b, code lost:
            r1 = com.android.server.audio.AudioService.VolumeStreamState.class;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x002d, code lost:
            monitor-enter(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x002e, code lost:
            r0 = 1342177279;
            r2 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0032, code lost:
            if (r0 == 0) goto L_0x0071;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0034, code lost:
            r5 = 1 << r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0038, code lost:
            if ((r5 & r0) != 0) goto L_0x003b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x003b, code lost:
            r0 = r0 & (~r5);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x003e, code lost:
            if (r5 != 1073741824) goto L_0x0047;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
            r7 = android.media.AudioSystem.DEFAULT_STREAM_VOLUME[r11.mStreamType];
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x0047, code lost:
            r7 = -1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x004c, code lost:
            if (hasValidSettingsName() != false) goto L_0x0050;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x004e, code lost:
            r8 = r7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x0050, code lost:
            r8 = android.provider.Settings.System.getIntForUser(com.android.server.audio.AudioService.access$3300(r11.this$0), getSettingNameForDevice(r5), r7, -2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x0060, code lost:
            if (r8 != -1) goto L_0x0063;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0063, code lost:
            r11.mIndexMap.put(r5, getValidIndex(r8 * 10));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x006e, code lost:
            r2 = r2 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0071, code lost:
            monitor-exit(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x0072, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void readSettings() {
            /*
                r11 = this;
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this
                java.lang.Object r0 = r0.mSettingsLock
                monitor-enter(r0)
                java.lang.Class<com.android.server.audio.AudioService$VolumeStreamState> r1 = com.android.server.audio.AudioService.VolumeStreamState.class
                monitor-enter(r1)     // Catch:{ all -> 0x0094 }
                com.android.server.audio.AudioService r2 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0091 }
                boolean r2 = r2.mUseFixedVolume     // Catch:{ all -> 0x0091 }
                r3 = 1073741824(0x40000000, float:2.0)
                if (r2 == 0) goto L_0x001e
                android.util.SparseIntArray r2 = r11.mIndexMap     // Catch:{ all -> 0x0091 }
                int r4 = r11.mIndexMax     // Catch:{ all -> 0x0091 }
                r2.put(r3, r4)     // Catch:{ all -> 0x0091 }
                monitor-exit(r1)     // Catch:{ all -> 0x0091 }
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                return
            L_0x001e:
                int r2 = r11.mStreamType     // Catch:{ all -> 0x0091 }
                r4 = 1
                if (r2 == r4) goto L_0x0076
                int r2 = r11.mStreamType     // Catch:{ all -> 0x0091 }
                r5 = 7
                if (r2 != r5) goto L_0x0029
                goto L_0x0076
            L_0x0029:
                monitor-exit(r1)     // Catch:{ all -> 0x0091 }
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                java.lang.Class<com.android.server.audio.AudioService$VolumeStreamState> r1 = com.android.server.audio.AudioService.VolumeStreamState.class
                monitor-enter(r1)
                r0 = 1342177279(0x4fffffff, float:8.5899341E9)
                r2 = 0
            L_0x0032:
                if (r0 == 0) goto L_0x0071
                int r5 = r4 << r2
                r6 = r5 & r0
                if (r6 != 0) goto L_0x003b
                goto L_0x006e
            L_0x003b:
                int r6 = ~r5
                r0 = r0 & r6
                r6 = -1
                if (r5 != r3) goto L_0x0047
                int[] r7 = android.media.AudioSystem.DEFAULT_STREAM_VOLUME     // Catch:{ all -> 0x0073 }
                int r8 = r11.mStreamType     // Catch:{ all -> 0x0073 }
                r7 = r7[r8]     // Catch:{ all -> 0x0073 }
                goto L_0x0048
            L_0x0047:
                r7 = r6
            L_0x0048:
                boolean r8 = r11.hasValidSettingsName()     // Catch:{ all -> 0x0073 }
                if (r8 != 0) goto L_0x0050
                r8 = r7
                goto L_0x0060
            L_0x0050:
                java.lang.String r8 = r11.getSettingNameForDevice(r5)     // Catch:{ all -> 0x0073 }
                com.android.server.audio.AudioService r9 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0073 }
                android.content.ContentResolver r9 = r9.mContentResolver     // Catch:{ all -> 0x0073 }
                r10 = -2
                int r9 = android.provider.Settings.System.getIntForUser(r9, r8, r7, r10)     // Catch:{ all -> 0x0073 }
                r8 = r9
            L_0x0060:
                if (r8 != r6) goto L_0x0063
                goto L_0x006e
            L_0x0063:
                android.util.SparseIntArray r6 = r11.mIndexMap     // Catch:{ all -> 0x0073 }
                int r9 = r8 * 10
                int r9 = r11.getValidIndex(r9)     // Catch:{ all -> 0x0073 }
                r6.put(r5, r9)     // Catch:{ all -> 0x0073 }
            L_0x006e:
                int r2 = r2 + 1
                goto L_0x0032
            L_0x0071:
                monitor-exit(r1)     // Catch:{ all -> 0x0073 }
                return
            L_0x0073:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0073 }
                throw r0
            L_0x0076:
                int[] r2 = android.media.AudioSystem.DEFAULT_STREAM_VOLUME     // Catch:{ all -> 0x0091 }
                int r4 = r11.mStreamType     // Catch:{ all -> 0x0091 }
                r2 = r2[r4]     // Catch:{ all -> 0x0091 }
                int r2 = r2 * 10
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0091 }
                boolean r4 = r4.mCameraSoundForced     // Catch:{ all -> 0x0091 }
                if (r4 == 0) goto L_0x0089
                int r4 = r11.mIndexMax     // Catch:{ all -> 0x0091 }
                r2 = r4
            L_0x0089:
                android.util.SparseIntArray r4 = r11.mIndexMap     // Catch:{ all -> 0x0091 }
                r4.put(r3, r2)     // Catch:{ all -> 0x0091 }
                monitor-exit(r1)     // Catch:{ all -> 0x0091 }
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                return
            L_0x0091:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0091 }
                throw r2     // Catch:{ all -> 0x0094 }
            L_0x0094:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0094 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.VolumeStreamState.readSettings():void");
        }

        private int getAbsoluteVolumeIndex(int index) {
            if (index == 0) {
                return 0;
            }
            if (index <= 0 || index > 5) {
                return (this.mIndexMax + 5) / 10;
            }
            return ((int) (((float) this.mIndexMax) * AudioService.this.mPrescaleAbsoluteVolume[index - 1])) / 10;
        }

        private void setStreamVolumeIndex(int index, int device) {
            if (this.mStreamType == 6 && index == 0 && !this.mIsMuted) {
                index = 1;
            }
            AudioSystem.setStreamVolumeIndexAS(this.mStreamType, index, device);
        }

        /* access modifiers changed from: package-private */
        public void applyDeviceVolume_syncVSS(int device, boolean isAvrcpAbsVolSupported) {
            int index;
            if (this.mIsMuted) {
                index = 0;
            } else if ((device & 896) != 0 && isAvrcpAbsVolSupported) {
                index = getAbsoluteVolumeIndex((getIndex(device) + 5) / 10);
            } else if ((AudioService.this.mFullVolumeDevices & device) != 0) {
                index = (this.mIndexMax + 5) / 10;
            } else if ((134217728 & device) != 0) {
                index = (this.mIndexMax + 5) / 10;
            } else {
                index = (getIndex(device) + 5) / 10;
            }
            setStreamVolumeIndex(index, device);
        }

        public void applyAllVolumes() {
            int index;
            int index2;
            boolean isAvrcpAbsVolSupported = AudioService.this.mDeviceBroker.isAvrcpAbsoluteVolumeSupported();
            synchronized (VolumeStreamState.class) {
                for (int i = 0; i < this.mIndexMap.size(); i++) {
                    int device = this.mIndexMap.keyAt(i);
                    if (device != 1073741824) {
                        if (this.mIsMuted) {
                            index2 = 0;
                        } else if ((device & 896) != 0 && isAvrcpAbsVolSupported) {
                            index2 = getAbsoluteVolumeIndex((getIndex(device) + 5) / 10);
                        } else if ((AudioService.this.mFullVolumeDevices & device) != 0) {
                            index2 = (this.mIndexMax + 5) / 10;
                        } else if ((134217728 & device) != 0) {
                            index2 = (this.mIndexMax + 5) / 10;
                        } else {
                            index2 = (this.mIndexMap.valueAt(i) + 5) / 10;
                        }
                        setStreamVolumeIndex(index2, device);
                    }
                }
                if (this.mIsMuted != 0) {
                    index = 0;
                } else {
                    index = (getIndex(1073741824) + 5) / 10;
                }
                setStreamVolumeIndex(index, 1073741824);
            }
        }

        public boolean adjustIndex(int deltaIndex, int device, String caller) {
            return setIndex(getIndex(device) + deltaIndex, device, caller);
        }

        /* Debug info: failed to restart local var, previous not found, register: 11 */
        public boolean setIndex(int index, int device, String caller) {
            int oldIndex;
            int index2;
            boolean changed;
            synchronized (AudioService.this.mSettingsLock) {
                synchronized (VolumeStreamState.class) {
                    oldIndex = getIndex(device);
                    index2 = getValidIndex(index);
                    if (this.mStreamType == 7 && AudioService.this.mCameraSoundForced) {
                        index2 = this.mIndexMax;
                    }
                    this.mIndexMap.put(device, index2);
                    boolean isCurrentDevice = true;
                    changed = oldIndex != index2;
                    if (device != AudioService.this.getDeviceForStream(this.mStreamType)) {
                        isCurrentDevice = false;
                    }
                    for (int streamType = AudioSystem.getNumStreamTypes() - 1; streamType >= 0; streamType--) {
                        VolumeStreamState aliasStreamState = AudioService.this.mStreamStates[streamType];
                        if (streamType != this.mStreamType && AudioService.mStreamVolumeAlias[streamType] == this.mStreamType && (changed || !aliasStreamState.hasIndexForDevice(device))) {
                            int scaledIndex = AudioService.this.rescaleIndex(index2, this.mStreamType, streamType);
                            aliasStreamState.setIndex(scaledIndex, device, caller);
                            if (isCurrentDevice) {
                                aliasStreamState.setIndex(scaledIndex, AudioService.this.getDeviceForStream(streamType), caller);
                            }
                        }
                    }
                    if (changed && this.mStreamType == 2 && device == 2) {
                        for (int i = 0; i < this.mIndexMap.size(); i++) {
                            int otherDevice = this.mIndexMap.keyAt(i);
                            if ((otherDevice & HdmiCecKeycode.UI_BROADCAST_DIGITAL_CABLE) != 0) {
                                this.mIndexMap.put(otherDevice, index2);
                            }
                        }
                    }
                }
            }
            if (changed) {
                int oldIndex2 = (oldIndex + 5) / 10;
                int index3 = (index2 + 5) / 10;
                int[] iArr = AudioService.mStreamVolumeAlias;
                int i2 = this.mStreamType;
                if (iArr[i2] == i2) {
                    if (caller == null) {
                        Log.w(AudioService.TAG, "No caller for volume_changed event", new Throwable());
                    }
                    EventLogTags.writeVolumeChanged(this.mStreamType, oldIndex2, index3, this.mIndexMax / 10, caller);
                }
                this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", index3);
                this.mVolumeChanged.putExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", oldIndex2);
                this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE_ALIAS", AudioService.mStreamVolumeAlias[this.mStreamType]);
                AudioService.this.sendBroadcastToAll(this.mVolumeChanged);
            }
            return changed;
        }

        public int getIndex(int device) {
            int index;
            synchronized (VolumeStreamState.class) {
                index = this.mIndexMap.get(device, -1);
                if (index == -1) {
                    index = this.mIndexMap.get(1073741824);
                }
            }
            return index;
        }

        public boolean hasIndexForDevice(int device) {
            boolean z;
            synchronized (VolumeStreamState.class) {
                z = this.mIndexMap.get(device, -1) != -1;
            }
            return z;
        }

        public int getMaxIndex() {
            return this.mIndexMax;
        }

        public int getMinIndex() {
            return this.mIndexMin;
        }

        @GuardedBy({"VolumeStreamState.class"})
        public void setAllIndexes(VolumeStreamState srcStream, String caller) {
            if (this.mStreamType != srcStream.mStreamType) {
                int srcStreamType = srcStream.getStreamType();
                int index = AudioService.this.rescaleIndex(srcStream.getIndex(1073741824), srcStreamType, this.mStreamType);
                for (int i = 0; i < this.mIndexMap.size(); i++) {
                    SparseIntArray sparseIntArray = this.mIndexMap;
                    sparseIntArray.put(sparseIntArray.keyAt(i), index);
                }
                SparseIntArray srcMap = srcStream.mIndexMap;
                for (int i2 = 0; i2 < srcMap.size(); i2++) {
                    setIndex(AudioService.this.rescaleIndex(srcMap.valueAt(i2), srcStreamType, this.mStreamType), srcMap.keyAt(i2), caller);
                }
            }
        }

        @GuardedBy({"VolumeStreamState.class"})
        public void setAllIndexesToMax() {
            for (int i = 0; i < this.mIndexMap.size(); i++) {
                SparseIntArray sparseIntArray = this.mIndexMap;
                sparseIntArray.put(sparseIntArray.keyAt(i), this.mIndexMax);
            }
        }

        public void mute(boolean state) {
            boolean changed = false;
            synchronized (VolumeStreamState.class) {
                if (state != this.mIsMuted) {
                    changed = true;
                    this.mIsMuted = state;
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, this, 0);
                }
            }
            if (changed) {
                Intent intent = new Intent("android.media.STREAM_MUTE_CHANGED_ACTION");
                intent.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", this.mStreamType);
                intent.putExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", state);
                AudioService.this.sendBroadcastToAll(intent);
            }
        }

        public int getStreamType() {
            return this.mStreamType;
        }

        public void checkFixedVolumeDevices() {
            boolean isAvrcpAbsVolSupported = AudioService.this.mDeviceBroker.isAvrcpAbsoluteVolumeSupported();
            synchronized (VolumeStreamState.class) {
                if (AudioService.mStreamVolumeAlias[this.mStreamType] == 3) {
                    for (int i = 0; i < this.mIndexMap.size(); i++) {
                        int device = this.mIndexMap.keyAt(i);
                        int index = this.mIndexMap.valueAt(i);
                        if (!((AudioService.this.mFullVolumeDevices & device) == 0 && ((AudioService.this.mFixedVolumeDevices & device) == 0 || index == 0))) {
                            this.mIndexMap.put(device, this.mIndexMax);
                        }
                        applyDeviceVolume_syncVSS(device, isAvrcpAbsVolSupported);
                    }
                }
            }
        }

        private int getValidIndex(int index) {
            int i = this.mIndexMin;
            if (index < i) {
                return i;
            }
            if (AudioService.this.mUseFixedVolume || index > this.mIndexMax) {
                return this.mIndexMax;
            }
            return index;
        }

        /* access modifiers changed from: private */
        public void dump(PrintWriter pw) {
            String deviceName;
            pw.print("   Muted: ");
            pw.println(this.mIsMuted);
            pw.print("   Min: ");
            pw.println((this.mIndexMin + 5) / 10);
            pw.print("   Max: ");
            pw.println((this.mIndexMax + 5) / 10);
            pw.print("   streamVolume:");
            pw.println(AudioService.this.getStreamVolume(this.mStreamType));
            pw.print("   Current: ");
            for (int i = 0; i < this.mIndexMap.size(); i++) {
                if (i > 0) {
                    pw.print(", ");
                }
                int device = this.mIndexMap.keyAt(i);
                pw.print(Integer.toHexString(device));
                if (device == 1073741824) {
                    deviceName = BatteryService.HealthServiceWrapper.INSTANCE_VENDOR;
                } else {
                    deviceName = AudioSystem.getOutputDeviceName(device);
                }
                if (!deviceName.isEmpty()) {
                    pw.print(" (");
                    pw.print(deviceName);
                    pw.print(")");
                }
                pw.print(": ");
                pw.print((this.mIndexMap.valueAt(i) + 5) / 10);
            }
            pw.println();
            pw.print("   Devices: ");
            int devices = AudioService.this.getDevicesForStream(this.mStreamType);
            int i2 = 0;
            int n = 0;
            while (true) {
                int i3 = 1 << i2;
                int device2 = i3;
                if (i3 != 1073741824) {
                    if ((devices & device2) != 0) {
                        int n2 = n + 1;
                        if (n > 0) {
                            pw.print(", ");
                        }
                        pw.print(AudioSystem.getOutputDeviceName(device2));
                        n = n2;
                    }
                    i2++;
                } else {
                    return;
                }
            }
        }
    }

    private class AudioSystemThread extends Thread {
        AudioSystemThread() {
            super("AudioService");
        }

        public void run() {
            Looper.prepare();
            synchronized (AudioService.this) {
                AudioHandler unused = AudioService.this.mAudioHandler = new AudioHandler();
                AudioService.this.notify();
            }
            Looper.loop();
        }
    }

    private static final class DeviceVolumeUpdate {
        private static final int NO_NEW_INDEX = -2049;
        final String mCaller;
        final int mDevice;
        final int mStreamType;
        private final int mVssVolIndex;

        DeviceVolumeUpdate(int streamType, int vssVolIndex, int device, String caller) {
            this.mStreamType = streamType;
            this.mVssVolIndex = vssVolIndex;
            this.mDevice = device;
            this.mCaller = caller;
        }

        DeviceVolumeUpdate(int streamType, int device, String caller) {
            this.mStreamType = streamType;
            this.mVssVolIndex = NO_NEW_INDEX;
            this.mDevice = device;
            this.mCaller = caller;
        }

        /* access modifiers changed from: package-private */
        public boolean hasVolumeIndex() {
            return this.mVssVolIndex != NO_NEW_INDEX;
        }

        /* access modifiers changed from: package-private */
        public int getVolumeIndex() throws IllegalStateException {
            Preconditions.checkState(this.mVssVolIndex != NO_NEW_INDEX);
            return this.mVssVolIndex;
        }
    }

    /* access modifiers changed from: package-private */
    public void postSetVolumeIndexOnDevice(int streamType, int vssVolIndex, int device, String caller) {
        sendMsg(this.mAudioHandler, MSG_SET_DEVICE_STREAM_VOLUME, 2, 0, 0, new DeviceVolumeUpdate(streamType, vssVolIndex, device, caller), 0);
    }

    /* access modifiers changed from: package-private */
    public void postApplyVolumeOnDevice(int streamType, int device, String caller) {
        sendMsg(this.mAudioHandler, MSG_SET_DEVICE_STREAM_VOLUME, 2, 0, 0, new DeviceVolumeUpdate(streamType, device, caller), 0);
    }

    /* access modifiers changed from: private */
    public void onSetVolumeIndexOnDevice(DeviceVolumeUpdate update) {
        VolumeStreamState streamState = this.mStreamStates[update.mStreamType];
        if (update.hasVolumeIndex()) {
            int index = update.getVolumeIndex();
            streamState.setIndex(index, update.mDevice, update.mCaller);
            AudioEventLogger audioEventLogger = sVolumeLogger;
            audioEventLogger.log(new AudioEventLogger.StringEvent(update.mCaller + " dev:0x" + Integer.toHexString(update.mDevice) + " volIdx:" + index));
        } else {
            AudioEventLogger audioEventLogger2 = sVolumeLogger;
            audioEventLogger2.log(new AudioEventLogger.StringEvent(update.mCaller + " update vol on dev:0x" + Integer.toHexString(update.mDevice)));
        }
        setDeviceVolume(streamState, update.mDevice);
    }

    /* access modifiers changed from: package-private */
    public void setDeviceVolume(VolumeStreamState streamState, int device) {
        boolean isAvrcpAbsVolSupported = this.mDeviceBroker.isAvrcpAbsoluteVolumeSupported();
        synchronized (VolumeStreamState.class) {
            streamState.applyDeviceVolume_syncVSS(device, isAvrcpAbsVolSupported);
            for (int streamType = AudioSystem.getNumStreamTypes() - 1; streamType >= 0; streamType--) {
                if (streamType != streamState.mStreamType && mStreamVolumeAlias[streamType] == streamState.mStreamType) {
                    int streamDevice = getDeviceForStream(streamType);
                    if (!(device == streamDevice || !isAvrcpAbsVolSupported || (device & 896) == 0)) {
                        this.mStreamStates[streamType].applyDeviceVolume_syncVSS(device, isAvrcpAbsVolSupported);
                    }
                    this.mStreamStates[streamType].applyDeviceVolume_syncVSS(streamDevice, isAvrcpAbsVolSupported);
                }
            }
        }
        sendMsg(this.mAudioHandler, 1, 2, device, 0, streamState, 500);
    }

    private class AudioHandler extends Handler {
        private AudioHandler() {
        }

        private void setAllVolumes(VolumeStreamState streamState) {
            streamState.applyAllVolumes();
            for (int streamType = AudioSystem.getNumStreamTypes() - 1; streamType >= 0; streamType--) {
                if (streamType != streamState.mStreamType && AudioService.mStreamVolumeAlias[streamType] == streamState.mStreamType) {
                    AudioService.this.mStreamStates[streamType].applyAllVolumes();
                }
            }
        }

        private void persistVolume(VolumeStreamState streamState, int device) {
            if (!AudioService.this.mUseFixedVolume) {
                if ((!AudioService.this.mIsSingleVolume || streamState.mStreamType == 3) && streamState.hasValidSettingsName()) {
                    Settings.System.putIntForUser(AudioService.this.mContentResolver, streamState.getSettingNameForDevice(device), (streamState.getIndex(device) + 5) / 10, -2);
                }
            }
        }

        private void persistRingerMode(int ringerMode) {
            if (!AudioService.this.mUseFixedVolume) {
                Settings.Global.putInt(AudioService.this.mContentResolver, "mode_ringer", ringerMode);
            }
        }

        private String getSoundEffectFilePath(int effectType) {
            return Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(AudioService.this.SOUND_EFFECT_FILES_MAP[effectType][0]));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:103:?, code lost:
            return true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:104:?, code lost:
            return false;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0204, code lost:
            if (r5 != 0) goto L_?;
         */
        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean onLoadSoundEffects() {
            /*
                r16 = this;
                r1 = r16
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this
                java.lang.Object r2 = r0.mSoundEffectsLock
                monitor-enter(r2)
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                boolean r0 = r0.mSystemReady     // Catch:{ all -> 0x0208 }
                r3 = 0
                if (r0 != 0) goto L_0x001c
                java.lang.String r0 = "AS.AudioService"
                java.lang.String r4 = "onLoadSoundEffects() called before boot complete"
                android.util.Log.w(r0, r4)     // Catch:{ all -> 0x0208 }
                monitor-exit(r2)     // Catch:{ all -> 0x0208 }
                return r3
            L_0x001c:
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.media.SoundPool r0 = r0.mSoundPool     // Catch:{ all -> 0x0208 }
                r4 = 1
                if (r0 == 0) goto L_0x0027
                monitor-exit(r2)     // Catch:{ all -> 0x0208 }
                return r4
            L_0x0027:
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                r0.loadTouchSoundAssets()     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.media.SoundPool$Builder r5 = new android.media.SoundPool$Builder     // Catch:{ all -> 0x0208 }
                r5.<init>()     // Catch:{ all -> 0x0208 }
                r6 = 4
                android.media.SoundPool$Builder r5 = r5.setMaxStreams(r6)     // Catch:{ all -> 0x0208 }
                android.media.AudioAttributes$Builder r7 = new android.media.AudioAttributes$Builder     // Catch:{ all -> 0x0208 }
                r7.<init>()     // Catch:{ all -> 0x0208 }
                r8 = 13
                android.media.AudioAttributes$Builder r7 = r7.setUsage(r8)     // Catch:{ all -> 0x0208 }
                android.media.AudioAttributes$Builder r6 = r7.setContentType(r6)     // Catch:{ all -> 0x0208 }
                android.media.AudioAttributes r6 = r6.build()     // Catch:{ all -> 0x0208 }
                android.media.SoundPool$Builder r5 = r5.setAudioAttributes(r6)     // Catch:{ all -> 0x0208 }
                android.media.SoundPool r5 = r5.build()     // Catch:{ all -> 0x0208 }
                android.media.SoundPool unused = r0.mSoundPool = r5     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                r5 = 0
                com.android.server.audio.AudioService.SoundPoolCallback unused = r0.mSoundPoolCallBack = r5     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService$SoundPoolListenerThread r6 = new com.android.server.audio.AudioService$SoundPoolListenerThread     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                r6.<init>()     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService.SoundPoolListenerThread unused = r0.mSoundPoolListenerThread = r6     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService$SoundPoolListenerThread r0 = r0.mSoundPoolListenerThread     // Catch:{ all -> 0x0208 }
                r0.start()     // Catch:{ all -> 0x0208 }
                r0 = 3
            L_0x0072:
                com.android.server.audio.AudioService r6 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService$SoundPoolCallback r6 = r6.mSoundPoolCallBack     // Catch:{ all -> 0x0208 }
                r7 = 5000(0x1388, double:2.4703E-320)
                if (r6 != 0) goto L_0x0096
                int r6 = r0 + -1
                if (r0 <= 0) goto L_0x0095
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ InterruptedException -> 0x008a }
                java.lang.Object r0 = r0.mSoundEffectsLock     // Catch:{ InterruptedException -> 0x008a }
                r0.wait(r7)     // Catch:{ InterruptedException -> 0x008a }
                goto L_0x0093
            L_0x008a:
                r0 = move-exception
                java.lang.String r7 = "AS.AudioService"
                java.lang.String r8 = "Interrupted while waiting sound pool listener thread."
                android.util.Log.w(r7, r8)     // Catch:{ all -> 0x0208 }
            L_0x0093:
                r0 = r6
                goto L_0x0072
            L_0x0095:
                r0 = r6
            L_0x0096:
                com.android.server.audio.AudioService r6 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService$SoundPoolCallback r6 = r6.mSoundPoolCallBack     // Catch:{ all -> 0x0208 }
                if (r6 != 0) goto L_0x00d1
                java.lang.String r4 = "AS.AudioService"
                java.lang.String r6 = "onLoadSoundEffects() SoundPool listener or thread creation error"
                android.util.Log.w(r4, r6)     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.os.Looper r4 = r4.mSoundPoolLooper     // Catch:{ all -> 0x0208 }
                if (r4 == 0) goto L_0x00bc
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.os.Looper r4 = r4.mSoundPoolLooper     // Catch:{ all -> 0x0208 }
                r4.quit()     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.os.Looper unused = r4.mSoundPoolLooper = r5     // Catch:{ all -> 0x0208 }
            L_0x00bc:
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService.SoundPoolListenerThread unused = r4.mSoundPoolListenerThread = r5     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.media.SoundPool r4 = r4.mSoundPool     // Catch:{ all -> 0x0208 }
                r4.release()     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.media.SoundPool unused = r4.mSoundPool = r5     // Catch:{ all -> 0x0208 }
                monitor-exit(r2)     // Catch:{ all -> 0x0208 }
                return r3
            L_0x00d1:
                java.util.List r6 = com.android.server.audio.AudioService.SOUND_EFFECT_FILES     // Catch:{ all -> 0x0208 }
                int r6 = r6.size()     // Catch:{ all -> 0x0208 }
                int[] r6 = new int[r6]     // Catch:{ all -> 0x0208 }
                r9 = r3
            L_0x00dc:
                java.util.List r10 = com.android.server.audio.AudioService.SOUND_EFFECT_FILES     // Catch:{ all -> 0x0208 }
                int r10 = r10.size()     // Catch:{ all -> 0x0208 }
                r11 = -1
                if (r9 >= r10) goto L_0x00ec
                r6[r9] = r11     // Catch:{ all -> 0x0208 }
                int r9 = r9 + 1
                goto L_0x00dc
            L_0x00ec:
                r9 = 0
                r10 = 0
            L_0x00ee:
                r12 = 10
                if (r10 >= r12) goto L_0x0167
                com.android.server.audio.AudioService r12 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r12 = r12.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r12 = r12[r10]     // Catch:{ all -> 0x0208 }
                r12 = r12[r4]     // Catch:{ all -> 0x0208 }
                if (r12 != 0) goto L_0x00ff
                goto L_0x0163
            L_0x00ff:
                com.android.server.audio.AudioService r12 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r12 = r12.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r12 = r12[r10]     // Catch:{ all -> 0x0208 }
                r12 = r12[r3]     // Catch:{ all -> 0x0208 }
                r12 = r6[r12]     // Catch:{ all -> 0x0208 }
                if (r12 != r11) goto L_0x014d
                java.lang.String r12 = r1.getSoundEffectFilePath(r10)     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r13 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.media.SoundPool r13 = r13.mSoundPool     // Catch:{ all -> 0x0208 }
                int r13 = r13.load(r12, r3)     // Catch:{ all -> 0x0208 }
                if (r13 > 0) goto L_0x0134
                java.lang.String r14 = "AS.AudioService"
                java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x0208 }
                r15.<init>()     // Catch:{ all -> 0x0208 }
                java.lang.String r5 = "Soundpool could not load file: "
                r15.append(r5)     // Catch:{ all -> 0x0208 }
                r15.append(r12)     // Catch:{ all -> 0x0208 }
                java.lang.String r5 = r15.toString()     // Catch:{ all -> 0x0208 }
                android.util.Log.w(r14, r5)     // Catch:{ all -> 0x0208 }
                goto L_0x014c
            L_0x0134:
                com.android.server.audio.AudioService r5 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r5 = r5.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r5 = r5[r10]     // Catch:{ all -> 0x0208 }
                r5[r4] = r13     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r5 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r5 = r5.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r5 = r5[r10]     // Catch:{ all -> 0x0208 }
                r5 = r5[r3]     // Catch:{ all -> 0x0208 }
                r6[r5] = r13     // Catch:{ all -> 0x0208 }
                int r9 = r9 + 1
            L_0x014c:
                goto L_0x0163
            L_0x014d:
                com.android.server.audio.AudioService r5 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r5 = r5.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r5 = r5[r10]     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r12 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r12 = r12.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r12 = r12[r10]     // Catch:{ all -> 0x0208 }
                r12 = r12[r3]     // Catch:{ all -> 0x0208 }
                r12 = r6[r12]     // Catch:{ all -> 0x0208 }
                r5[r4] = r12     // Catch:{ all -> 0x0208 }
            L_0x0163:
                int r10 = r10 + 1
                r5 = 0
                goto L_0x00ee
            L_0x0167:
                if (r9 <= 0) goto L_0x019c
                com.android.server.audio.AudioService r5 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService$SoundPoolCallback r5 = r5.mSoundPoolCallBack     // Catch:{ all -> 0x0208 }
                r5.setSamples(r6)     // Catch:{ all -> 0x0208 }
                r0 = 3
                r5 = 1
            L_0x0174:
                if (r5 != r4) goto L_0x019d
                int r10 = r0 + -1
                if (r0 <= 0) goto L_0x019a
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ InterruptedException -> 0x0190 }
                java.lang.Object r0 = r0.mSoundEffectsLock     // Catch:{ InterruptedException -> 0x0190 }
                r0.wait(r7)     // Catch:{ InterruptedException -> 0x0190 }
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this     // Catch:{ InterruptedException -> 0x0190 }
                com.android.server.audio.AudioService$SoundPoolCallback r0 = r0.mSoundPoolCallBack     // Catch:{ InterruptedException -> 0x0190 }
                int r0 = r0.status()     // Catch:{ InterruptedException -> 0x0190 }
                r5 = r0
                r0 = r10
                goto L_0x0174
            L_0x0190:
                r0 = move-exception
                java.lang.String r13 = "AS.AudioService"
                java.lang.String r14 = "Interrupted while waiting sound pool callback."
                android.util.Log.w(r13, r14)     // Catch:{ all -> 0x0208 }
                r0 = r10
                goto L_0x0174
            L_0x019a:
                r0 = r10
                goto L_0x019d
            L_0x019c:
                r5 = r11
            L_0x019d:
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.os.Looper r7 = r7.mSoundPoolLooper     // Catch:{ all -> 0x0208 }
                if (r7 == 0) goto L_0x01b4
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.os.Looper r7 = r7.mSoundPoolLooper     // Catch:{ all -> 0x0208 }
                r7.quit()     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                r8 = 0
                android.os.Looper unused = r7.mSoundPoolLooper = r8     // Catch:{ all -> 0x0208 }
            L_0x01b4:
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                r8 = 0
                com.android.server.audio.AudioService.SoundPoolListenerThread unused = r7.mSoundPoolListenerThread = r8     // Catch:{ all -> 0x0208 }
                if (r5 == 0) goto L_0x0203
                java.lang.String r7 = "AS.AudioService"
                java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0208 }
                r8.<init>()     // Catch:{ all -> 0x0208 }
                java.lang.String r10 = "onLoadSoundEffects(), Error "
                r8.append(r10)     // Catch:{ all -> 0x0208 }
                r8.append(r5)     // Catch:{ all -> 0x0208 }
                java.lang.String r10 = " while loading samples"
                r8.append(r10)     // Catch:{ all -> 0x0208 }
                java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0208 }
                android.util.Log.w(r7, r8)     // Catch:{ all -> 0x0208 }
                r7 = 0
            L_0x01d9:
                if (r7 >= r12) goto L_0x01f4
                com.android.server.audio.AudioService r8 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r8 = r8.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r8 = r8[r7]     // Catch:{ all -> 0x0208 }
                r8 = r8[r4]     // Catch:{ all -> 0x0208 }
                if (r8 <= 0) goto L_0x01f1
                com.android.server.audio.AudioService r8 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                int[][] r8 = r8.SOUND_EFFECT_FILES_MAP     // Catch:{ all -> 0x0208 }
                r8 = r8[r7]     // Catch:{ all -> 0x0208 }
                r8[r4] = r11     // Catch:{ all -> 0x0208 }
            L_0x01f1:
                int r7 = r7 + 1
                goto L_0x01d9
            L_0x01f4:
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                android.media.SoundPool r7 = r7.mSoundPool     // Catch:{ all -> 0x0208 }
                r7.release()     // Catch:{ all -> 0x0208 }
                com.android.server.audio.AudioService r7 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0208 }
                r8 = 0
                android.media.SoundPool unused = r7.mSoundPool = r8     // Catch:{ all -> 0x0208 }
            L_0x0203:
                monitor-exit(r2)     // Catch:{ all -> 0x0208 }
                if (r5 != 0) goto L_0x0207
                r3 = r4
            L_0x0207:
                return r3
            L_0x0208:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0208 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.AudioHandler.onLoadSoundEffects():boolean");
        }

        private void onUnloadSoundEffects() {
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mSoundPool != null) {
                    int[] poolId = new int[AudioService.SOUND_EFFECT_FILES.size()];
                    for (int fileIdx = 0; fileIdx < AudioService.SOUND_EFFECT_FILES.size(); fileIdx++) {
                        poolId[fileIdx] = 0;
                    }
                    for (int effect = 0; effect < 10; effect++) {
                        if (AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] > 0) {
                            if (poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]] == 0) {
                                AudioService.this.mSoundPool.unload(AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1]);
                                AudioService.this.SOUND_EFFECT_FILES_MAP[effect][1] = -1;
                                poolId[AudioService.this.SOUND_EFFECT_FILES_MAP[effect][0]] = -1;
                            }
                        }
                    }
                    AudioService.this.mSoundPool.release();
                    SoundPool unused = AudioService.this.mSoundPool = null;
                }
            }
        }

        private void onPlaySoundEffect(int effectType, int volume) {
            float volFloat;
            synchronized (AudioService.this.mSoundEffectsLock) {
                onLoadSoundEffects();
                if (AudioService.this.mSoundPool != null) {
                    if (volume < 0) {
                        volFloat = (float) Math.pow(10.0d, (double) (((float) AudioService.sSoundEffectVolumeDb) / 20.0f));
                    } else {
                        volFloat = ((float) volume) / 1000.0f;
                    }
                    if (AudioService.this.SOUND_EFFECT_FILES_MAP[effectType][1] > 0) {
                        AudioService.this.mSoundPool.play(AudioService.this.SOUND_EFFECT_FILES_MAP[effectType][1], volFloat, volFloat, 0, 0, 1.0f);
                    } else {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(getSoundEffectFilePath(effectType));
                            mediaPlayer.setAudioStreamType(1);
                            mediaPlayer.prepare();
                            mediaPlayer.setVolume(volFloat);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    AudioHandler.this.cleanupPlayer(mp);
                                }
                            });
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                public boolean onError(MediaPlayer mp, int what, int extra) {
                                    AudioHandler.this.cleanupPlayer(mp);
                                    return true;
                                }
                            });
                            mediaPlayer.start();
                        } catch (IOException ex) {
                            Log.w(AudioService.TAG, "MediaPlayer IOException: " + ex);
                        } catch (IllegalArgumentException ex2) {
                            Log.w(AudioService.TAG, "MediaPlayer IllegalArgumentException: " + ex2);
                        } catch (IllegalStateException ex3) {
                            Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + ex3);
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void cleanupPlayer(MediaPlayer mp) {
            if (mp != null) {
                try {
                    mp.stop();
                    mp.release();
                } catch (IllegalStateException ex) {
                    Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + ex);
                }
            }
        }

        private void onPersistSafeVolumeState(int state) {
            Settings.Global.putInt(AudioService.this.mContentResolver, "audio_safe_volume_state", state);
        }

        private void onNotifyVolumeEvent(IAudioPolicyCallback apc, int direction) {
            try {
                apc.notifyVolumeAdjust(direction);
            } catch (Exception e) {
            }
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 0) {
                boolean z = true;
                if (i == 1) {
                    persistVolume((VolumeStreamState) msg.obj, msg.arg1);
                } else if (i == 3) {
                    persistRingerMode(AudioService.this.getRingerModeInternal());
                } else if (i == 4) {
                    AudioService.this.onAudioServerDied();
                } else if (i != 5) {
                    int i2 = 0;
                    if (i == 7) {
                        boolean loaded = onLoadSoundEffects();
                        if (msg.obj != null) {
                            LoadSoundEffectReply reply = (LoadSoundEffectReply) msg.obj;
                            synchronized (reply) {
                                if (!loaded) {
                                    i2 = -1;
                                }
                                reply.mStatus = i2;
                                reply.notify();
                            }
                        }
                    } else if (i == 8) {
                        String eventSource = (String) msg.obj;
                        int useCase = msg.arg1;
                        int config = msg.arg2;
                        if (useCase == 1) {
                            Log.wtf(AudioService.TAG, "Invalid force use FOR_MEDIA in AudioService from " + eventSource);
                            return;
                        }
                        AudioService.sForceUseLogger.log(new AudioServiceEvents.ForceUseEvent(useCase, config, eventSource));
                        AudioSystem.setForceUse(useCase, config);
                    } else if (i != 100) {
                        switch (i) {
                            case 10:
                                setAllVolumes((VolumeStreamState) msg.obj);
                                return;
                            case 11:
                                AudioService.this.onCheckMusicActive((String) msg.obj);
                                return;
                            case 12:
                            case 13:
                                AudioService audioService = AudioService.this;
                                if (msg.what != 13) {
                                    z = false;
                                }
                                audioService.onConfigureSafeVolume(z, (String) msg.obj);
                                return;
                            case 14:
                                onPersistSafeVolumeState(msg.arg1);
                                return;
                            case 15:
                                onUnloadSoundEffects();
                                return;
                            case 16:
                                AudioService.this.onSystemReady();
                                return;
                            case 17:
                                Settings.Secure.putIntForUser(AudioService.this.mContentResolver, "unsafe_volume_music_active_ms", msg.arg1, -2);
                                return;
                            case 18:
                                AudioService.this.onUnmuteStream(msg.arg1, msg.arg2);
                                return;
                            case 19:
                                AudioService.this.onDynPolicyMixStateUpdate((String) msg.obj, msg.arg1);
                                return;
                            case 20:
                                AudioService.this.onIndicateSystemReady();
                                return;
                            case 21:
                                AudioService.this.onAccessoryPlugMediaUnmute(msg.arg1);
                                return;
                            case 22:
                                onNotifyVolumeEvent((IAudioPolicyCallback) msg.obj, msg.arg1);
                                return;
                            case 23:
                                AudioService audioService2 = AudioService.this;
                                if (msg.arg1 != 1) {
                                    z = false;
                                }
                                audioService2.onDispatchAudioServerStateChange(z);
                                return;
                            case 24:
                                AudioService.this.onEnableSurroundFormats((ArrayList) msg.obj);
                                return;
                            case 25:
                                AudioService.this.onUpdateRingerModeServiceInt();
                                return;
                            case AudioService.MSG_SET_DEVICE_STREAM_VOLUME /*26*/:
                                AudioService.this.onSetVolumeIndexOnDevice((DeviceVolumeUpdate) msg.obj);
                                return;
                            case AudioService.MSG_OBSERVE_DEVICES_FOR_ALL_STREAMS /*27*/:
                                AudioService.this.onObserveDevicesForAllStreams();
                                return;
                            case 28:
                                AudioService.this.onCheckVolumeCecOnHdmiConnection(msg.arg1, (String) msg.obj);
                                return;
                            case 29:
                                AudioService.this.onPlaybackConfigChange((List) msg.obj);
                                return;
                            default:
                                return;
                        }
                    } else {
                        PlaybackActivityMonitor access$4900 = AudioService.this.mPlaybackMonitor;
                        if (msg.arg1 != 1) {
                            z = false;
                        }
                        access$4900.disableAudioForUid(z, msg.arg2);
                        AudioService.this.mAudioEventWakeLock.release();
                    }
                } else {
                    onPlaySoundEffect(msg.arg1, msg.arg2);
                }
            } else {
                AudioService.this.setDeviceVolume((VolumeStreamState) msg.obj, msg.arg1);
            }
        }
    }

    private class SettingsObserver extends ContentObserver {
        SettingsObserver() {
            super(new Handler());
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("zen_mode_config_etag"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("mode_ringer_streams_affected"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("dock_audio_media_enabled"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("master_mono"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("master_balance"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this);
            int unused = AudioService.this.mEncodedSurroundMode = Settings.Global.getInt(AudioService.this.mContentResolver, "encoded_surround_output", 0);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("encoded_surround_output"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor(AudioService.LOCK_VOICE_ASSIST_STREAM), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("notification_sound"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("calendar_alert"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("sms_received_sound"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("sms_received_sound_slot_1"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("sms_received_sound_slot_2"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("random_note_mode_random_sound_number"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("random_note_mode_sequence_sound_number"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("random_note_mode_sequence_time_interval_ms"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("random_note_mode_mute_time_interval_ms"), false, this);
            String unused2 = AudioService.this.mEnabledSurroundFormats = Settings.Global.getString(AudioService.this.mContentResolver, "encoded_surround_output_enabled_formats");
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("encoded_surround_output_enabled_formats"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("voice_interaction_service"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("rtt_calling_mode"), false, this);
        }

        public void onChange(boolean selfChange, Uri uri, int userId) {
            boolean z;
            super.onChange(selfChange);
            synchronized (AudioService.this.mSettingsLock) {
                z = false;
                if (AudioService.this.updateRingerAndZenModeAffectedStreams()) {
                    AudioService.this.setRingerModeInt(AudioService.this.getRingerModeInternal(), false);
                }
                AudioService.this.readDockAudioSettings(AudioService.this.mContentResolver);
                AudioService.this.updateMasterMono(AudioService.this.mContentResolver);
                AudioService.this.updateMasterBalance(AudioService.this.mContentResolver);
                updateEncodedSurroundOutput();
                AudioService.this.sendEnabledSurroundFormats(AudioService.this.mContentResolver, AudioService.this.mSurroundModeChanged);
                AudioService.this.updateAssistantUId(false);
                AudioService.this.updateRttEanbled(AudioService.this.mContentResolver);
            }
            if (uri.equals(Settings.Global.getUriFor(AudioService.LOCK_VOICE_ASSIST_STREAM))) {
                AudioService audioService = AudioService.this;
                if (Settings.Global.getInt(audioService.mContentResolver, AudioService.LOCK_VOICE_ASSIST_STREAM, -1) != -1) {
                    z = true;
                }
                boolean unused = audioService.mLockVoiseAssistStream = z;
                return;
            }
            AudioServiceInjector.updateRestriction(AudioService.this.mContext);
            int preZenMode = AudioService.this.mZenMode;
            AudioService audioService2 = AudioService.this;
            int unused2 = audioService2.mZenMode = MiuiSettings.SilenceMode.getZenMode(audioService2.mContext);
            AudioService audioService3 = AudioService.this;
            audioService3.broadcastRingerMode("android.media.RINGER_MODE_CHANGED", audioService3.getRingerModeInternal());
            if (uri.equals(Settings.Global.getUriFor("zen_mode_config_etag"))) {
                AudioService audioService4 = AudioService.this;
                AudioServiceInjector.handleZenModeChangedForMusic(audioService4, audioService4.mContext, preZenMode, AudioService.this.mZenMode, AudioService.this.mStreamStates[3].getMaxIndex(), AudioService.this.mStreamStates[AudioService.mStreamVolumeAlias[3]].getMaxIndex(), AudioService.mStreamVolumeAlias);
                AudioService.this.mVolumeController.postVolumeChanged(3, 0);
            }
            AudioServiceInjector.updateNotificationMode(AudioService.this.mContext);
        }

        private void updateEncodedSurroundOutput() {
            int newSurroundMode = Settings.Global.getInt(AudioService.this.mContentResolver, "encoded_surround_output", 0);
            if (AudioService.this.mEncodedSurroundMode != newSurroundMode) {
                AudioService.this.sendEncodedSurroundMode(newSurroundMode, "SettingsObserver");
                AudioService.this.mDeviceBroker.toggleHdmiIfConnected_Async();
                int unused = AudioService.this.mEncodedSurroundMode = newSurroundMode;
                boolean unused2 = AudioService.this.mSurroundModeChanged = true;
                return;
            }
            boolean unused3 = AudioService.this.mSurroundModeChanged = false;
        }
    }

    public void avrcpSupportsAbsoluteVolume(String address, boolean support) {
        AudioEventLogger audioEventLogger = sVolumeLogger;
        audioEventLogger.log(new AudioEventLogger.StringEvent("avrcpSupportsAbsoluteVolume addr=" + address + " support=" + support));
        this.mDeviceBroker.setAvrcpAbsoluteVolumeSupported(support);
        sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[3], 0);
    }

    /* access modifiers changed from: package-private */
    public boolean hasMediaDynamicPolicy() {
        synchronized (this.mAudioPolicies) {
            if (this.mAudioPolicies.isEmpty()) {
                return false;
            }
            for (AudioPolicyProxy app : this.mAudioPolicies.values()) {
                if (app.hasMixAffectingUsage(1)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void checkMusicActive(int deviceType, String caller) {
        if ((67108876 & deviceType) != 0) {
            sendMsg(this.mAudioHandler, 11, 0, 0, 0, caller, MUSIC_ACTIVE_POLL_PERIOD_MS);
        }
    }

    private class AudioServiceBroadcastReceiver extends BroadcastReceiver {
        private AudioServiceBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int config;
            String action = intent.getAction();
            if (action.equals("android.intent.action.DOCK_EVENT")) {
                int dockState = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
                if (dockState == 1) {
                    config = 7;
                } else if (dockState == 2) {
                    config = 6;
                } else if (dockState == 3) {
                    config = 8;
                } else if (dockState != 4) {
                    config = 0;
                } else {
                    config = 9;
                }
                if (!(dockState == 3 || (dockState == 0 && AudioService.this.mDockState == 3))) {
                    AudioService.this.mDeviceBroker.setForceUse_Async(3, config, "ACTION_DOCK_EVENT intent");
                }
                int unused = AudioService.this.mDockState = dockState;
            } else if (action.equals("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED") || action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                AudioService.this.mDeviceBroker.receiveBtEvent(intent);
            } else if (action.equals("android.intent.action.SCREEN_ON")) {
                if (AudioService.this.mMonitorRotation) {
                    RotationHelper.enable();
                }
                AudioSystem.setParameters("screen_state=on");
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                if (AudioService.this.mMonitorRotation) {
                    RotationHelper.disable();
                }
                AudioSystem.setParameters("screen_state=off");
            } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                AudioService.this.handleConfigurationChanged(context);
            } else if (action.equals("android.intent.action.USER_SWITCHED")) {
                if (AudioService.this.mUserSwitchedReceived) {
                    AudioService.this.mDeviceBroker.postBroadcastBecomingNoisy();
                }
                boolean unused2 = AudioService.this.mUserSwitchedReceived = true;
                AudioService.this.mMediaFocusControl.discardAudioFocusOwner();
                AudioService.this.readAudioSettings(true);
                AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, AudioService.this.mStreamStates[3], 0);
            } else if (action.equals("android.intent.action.USER_BACKGROUND")) {
                int userId = intent.getIntExtra("android.intent.extra.user_handle", -1);
                if (userId >= 0) {
                    AudioService.this.killBackgroundUserProcessesWithRecordAudioPermission(UserManagerService.getInstance().getUserInfo(userId));
                }
                UserManagerService.getInstance().setUserRestriction("no_record_audio", true, userId);
            } else if (action.equals("android.intent.action.USER_FOREGROUND")) {
                UserManagerService.getInstance().setUserRestriction("no_record_audio", false, intent.getIntExtra("android.intent.extra.user_handle", -1));
            } else if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int state = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
                if (state == 10 || state == 13) {
                    AudioService.this.mDeviceBroker.disconnectAllBluetoothProfiles();
                }
            } else if (action.equals("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION") || action.equals("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION")) {
                AudioService.this.handleAudioEffectBroadcast(context, intent);
            } else if (action.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                int[] suspendedUids = intent.getIntArrayExtra("android.intent.extra.changed_uid_list");
                String[] suspendedPackages = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                if (suspendedPackages != null && suspendedUids != null && suspendedPackages.length == suspendedUids.length) {
                    for (int i = 0; i < suspendedUids.length; i++) {
                        if (!TextUtils.isEmpty(suspendedPackages[i])) {
                            AudioService.this.mMediaFocusControl.noFocusForSuspendedApp(suspendedPackages[i], suspendedUids[i]);
                        }
                    }
                }
            }
        }
    }

    private class AudioServiceUserRestrictionsListener implements UserManagerInternal.UserRestrictionsListener {
        private AudioServiceUserRestrictionsListener() {
        }

        public void onUserRestrictionsChanged(int userId, Bundle newRestrictions, Bundle prevRestrictions) {
            boolean wasRestricted = prevRestrictions.getBoolean("no_unmute_microphone");
            boolean isRestricted = newRestrictions.getBoolean("no_unmute_microphone");
            if (wasRestricted != isRestricted) {
                AudioService.this.setMicrophoneMuteNoCallerCheck(isRestricted, userId);
            }
            boolean z = true;
            boolean wasRestricted2 = prevRestrictions.getBoolean("no_adjust_volume") || prevRestrictions.getBoolean("disallow_unmute_device");
            if (!newRestrictions.getBoolean("no_adjust_volume") && !newRestrictions.getBoolean("disallow_unmute_device")) {
                z = false;
            }
            boolean isRestricted2 = z;
            if (wasRestricted2 != isRestricted2) {
                AudioService.this.setMasterMuteInternalNoCallerCheck(isRestricted2, 0, userId);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleAudioEffectBroadcast(Context context, Intent intent) {
        ResolveInfo ri;
        String target = intent.getPackage();
        if (target != null) {
            Log.w(TAG, "effect broadcast already targeted to " + target);
            return;
        }
        intent.addFlags(32);
        List<ResolveInfo> ril = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (ril == null || ril.size() == 0 || (ri = ril.get(0)) == null || ri.activityInfo == null || ri.activityInfo.packageName == null) {
            Log.w(TAG, "couldn't find receiver package for effect intent");
            return;
        }
        intent.setPackage(ri.activityInfo.packageName);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public void killBackgroundUserProcessesWithRecordAudioPermission(UserInfo oldUser) {
        PackageManager pm = this.mContext.getPackageManager();
        ComponentName homeActivityName = null;
        if (!oldUser.isManagedProfile()) {
            homeActivityName = ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).getHomeActivityForUser(oldUser.id);
        }
        try {
            List<PackageInfo> packages = AppGlobals.getPackageManager().getPackagesHoldingPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 0, oldUser.id).getList();
            for (int j = packages.size() - 1; j >= 0; j--) {
                PackageInfo pkg = packages.get(j);
                if (UserHandle.getAppId(pkg.applicationInfo.uid) >= 10000 && pm.checkPermission("android.permission.INTERACT_ACROSS_USERS", pkg.packageName) != 0 && !AudioServiceInjector.isPackageProtectedWhenUserBackground(oldUser.id, pkg) && (homeActivityName == null || !pkg.packageName.equals(homeActivityName.getPackageName()) || !pkg.applicationInfo.isSystemApp())) {
                    try {
                        int uid = pkg.applicationInfo.uid;
                        ActivityManager.getService().killUid(UserHandle.getAppId(uid), UserHandle.getUserId(uid), "killBackgroundUserProcessesWithAudioRecordPermission");
                    } catch (RemoteException e) {
                        Log.w(TAG, "Error calling killUid", e);
                    }
                }
            }
        } catch (RemoteException e2) {
            throw new AndroidRuntimeException(e2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x003f, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean forceFocusDuckingForAccessibility(android.media.AudioAttributes r8, int r9, int r10) {
        /*
            r7 = this;
            r0 = 0
            if (r8 == 0) goto L_0x0044
            int r1 = r8.getUsage()
            r2 = 11
            if (r1 != r2) goto L_0x0044
            r1 = 3
            if (r9 == r1) goto L_0x000f
            goto L_0x0044
        L_0x000f:
            android.os.Bundle r1 = r8.getBundle()
            if (r1 == 0) goto L_0x0043
            java.lang.String r2 = "a11y_force_ducking"
            boolean r2 = r1.getBoolean(r2)
            if (r2 != 0) goto L_0x001e
            goto L_0x0043
        L_0x001e:
            r2 = 1
            if (r10 != 0) goto L_0x0022
            return r2
        L_0x0022:
            java.lang.Object r3 = r7.mAccessibilityServiceUidsLock
            monitor-enter(r3)
            int[] r4 = r7.mAccessibilityServiceUids     // Catch:{ all -> 0x0040 }
            if (r4 == 0) goto L_0x003e
            int r4 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0040 }
            r5 = r0
        L_0x002e:
            int[] r6 = r7.mAccessibilityServiceUids     // Catch:{ all -> 0x0040 }
            int r6 = r6.length     // Catch:{ all -> 0x0040 }
            if (r5 >= r6) goto L_0x003e
            int[] r6 = r7.mAccessibilityServiceUids     // Catch:{ all -> 0x0040 }
            r6 = r6[r5]     // Catch:{ all -> 0x0040 }
            if (r6 != r4) goto L_0x003b
            monitor-exit(r3)     // Catch:{ all -> 0x0040 }
            return r2
        L_0x003b:
            int r5 = r5 + 1
            goto L_0x002e
        L_0x003e:
            monitor-exit(r3)     // Catch:{ all -> 0x0040 }
            return r0
        L_0x0040:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0040 }
            throw r0
        L_0x0043:
            return r0
        L_0x0044:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.forceFocusDuckingForAccessibility(android.media.AudioAttributes, int, int):boolean");
    }

    public int requestAudioFocus(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, int flags, IAudioPolicyCallback pcb, int sdk) {
        AudioAttributes audioAttributes = aa;
        String str = clientId;
        if ((flags & 4) == 4) {
            if (!"AudioFocus_For_Phone_Ring_And_Calls".equals(str)) {
                synchronized (this.mAudioPolicies) {
                    if (!this.mAudioPolicies.containsKey(pcb.asBinder())) {
                        Log.e(TAG, "Invalid unregistered AudioPolicy to (un)lock audio focus");
                        return 0;
                    }
                }
            } else if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
                Log.e(TAG, "Invalid permission to (un)lock audio focus", new Exception());
                return 0;
            }
        }
        if (callingPackageName == null || str == null) {
            int i = durationHint;
        } else if (audioAttributes == null) {
            int i2 = durationHint;
        } else {
            return this.mMediaFocusControl.requestAudioFocus(aa, durationHint, cb, fd, clientId, callingPackageName, flags, sdk, forceFocusDuckingForAccessibility(audioAttributes, durationHint, Binder.getCallingUid()));
        }
        Log.e(TAG, "Invalid null parameter to request audio focus");
        return 0;
    }

    public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa, String callingPackageName) {
        return this.mMediaFocusControl.abandonAudioFocus(fd, clientId, aa, callingPackageName);
    }

    public void unregisterAudioFocusClient(String clientId) {
        this.mMediaFocusControl.unregisterAudioFocusClient(clientId);
    }

    public int getCurrentAudioFocus() {
        return this.mMediaFocusControl.getCurrentAudioFocus();
    }

    public int getFocusRampTimeMs(int focusGain, AudioAttributes attr) {
        MediaFocusControl mediaFocusControl = this.mMediaFocusControl;
        return MediaFocusControl.getFocusRampTimeMs(focusGain, attr);
    }

    /* access modifiers changed from: package-private */
    public boolean hasAudioFocusUsers() {
        return this.mMediaFocusControl.hasAudioFocusUsers();
    }

    private boolean readCameraSoundForced() {
        if (SystemProperties.getBoolean("audio.camerasound.force", false) || this.mContext.getResources().getBoolean(17891386)) {
            return true;
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* access modifiers changed from: private */
    public void handleConfigurationChanged(Context context) {
        try {
            Configuration config = context.getResources().getConfiguration();
            sendMsg(this.mAudioHandler, 12, 0, 0, 0, TAG, 0);
            boolean cameraSoundForced = readCameraSoundForced();
            synchronized (this.mSettingsLock) {
                int i = 0;
                boolean cameraSoundForcedChanged = cameraSoundForced != this.mCameraSoundForced;
                this.mCameraSoundForced = cameraSoundForced;
                if (cameraSoundForcedChanged) {
                    if (!this.mIsSingleVolume) {
                        synchronized (VolumeStreamState.class) {
                            VolumeStreamState s = this.mStreamStates[7];
                            if (cameraSoundForced) {
                                s.setAllIndexesToMax();
                                this.mRingerModeAffectedStreams &= -129;
                            } else {
                                s.setAllIndexes(this.mStreamStates[1], TAG);
                                this.mRingerModeAffectedStreams |= 128;
                            }
                        }
                        setRingerModeInt(getRingerModeInternal(), false);
                    }
                    AudioDeviceBroker audioDeviceBroker = this.mDeviceBroker;
                    if (cameraSoundForced) {
                        i = 11;
                    }
                    audioDeviceBroker.setForceUse_Async(4, i, "handleConfigurationChanged");
                    sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[7], 0);
                }
            }
            this.mVolumeController.setLayoutDirection(config.getLayoutDirection());
        } catch (Exception e) {
            Log.e(TAG, "Error handling configuration change: ", e);
        }
    }

    public void setRingtonePlayer(IRingtonePlayer player) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.REMOTE_AUDIO_PLAYBACK", (String) null);
        this.mRingtonePlayer = player;
    }

    public IRingtonePlayer getRingtonePlayer() {
        return this.mRingtonePlayer;
    }

    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) {
        return this.mDeviceBroker.startWatchingRoutes(observer);
    }

    private int safeMediaVolumeIndex(int device) {
        if ((67108876 & device) == 0) {
            return MAX_STREAM_VOLUME[3];
        }
        if (device == 67108864) {
            return this.mSafeUsbMediaVolumeIndex;
        }
        return this.mSafeMediaVolumeIndex;
    }

    private void setSafeMediaVolumeEnabled(boolean on, String caller) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            if (!(this.mSafeMediaVolumeState == 0 || this.mSafeMediaVolumeState == 1)) {
                if (on && this.mSafeMediaVolumeState == 2) {
                    this.mSafeMediaVolumeState = 3;
                    enforceSafeMediaVolume(caller);
                } else if (!on && this.mSafeMediaVolumeState == 3) {
                    this.mSafeMediaVolumeState = 2;
                    this.mMusicActiveMs = 0;
                    sendMsg(this.mAudioHandler, 11, 0, 0, 0, caller, MUSIC_ACTIVE_POLL_PERIOD_MS);
                }
            }
        }
    }

    private void enforceSafeMediaVolume(String caller) {
        VolumeStreamState streamState = this.mStreamStates[3];
        int devices = 67108876;
        int i = 0;
        while (devices != 0) {
            int i2 = i + 1;
            int device = 1 << i;
            if ((device & devices) == 0) {
                i = i2;
            } else {
                if (streamState.getIndex(device) > safeMediaVolumeIndex(device)) {
                    streamState.setIndex(safeMediaVolumeIndex(device), device, caller);
                    sendMsg(this.mAudioHandler, 0, 2, device, 0, streamState, 0);
                }
                devices &= ~device;
                i = i2;
            }
        }
        AudioManagerHelper.setHiFiVolume(this.mContext, 0);
    }

    private boolean checkSafeMediaVolume(int streamType, int index, int device) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            if (this.mSafeMediaVolumeState != 3 || mStreamVolumeAlias[streamType] != 3 || (67108876 & device) == 0 || index <= safeMediaVolumeIndex(device)) {
                return true;
            }
            return false;
        }
    }

    public void disableSafeMediaVolume(String callingPackage) {
        enforceVolumeController("disable the safe media volume");
        synchronized (this.mSafeMediaVolumeStateLock) {
            setSafeMediaVolumeEnabled(false, callingPackage);
            if (this.mPendingVolumeCommand != null) {
                onSetStreamVolume(this.mPendingVolumeCommand.mStreamType, this.mPendingVolumeCommand.mIndex, this.mPendingVolumeCommand.mFlags, this.mPendingVolumeCommand.mDevice, callingPackage);
                this.mPendingVolumeCommand = null;
            }
        }
    }

    private class MyDisplayStatusCallback implements HdmiPlaybackClient.DisplayStatusCallback {
        private MyDisplayStatusCallback() {
        }

        public void onComplete(int status) {
            synchronized (AudioService.this.mHdmiClientLock) {
                if (AudioService.this.mHdmiManager != null) {
                    boolean unused = AudioService.this.mHdmiCecSink = status != -1;
                    if (AudioService.this.mHdmiCecSink) {
                        Log.d(AudioService.TAG, "CEC sink: setting HDMI as full vol device");
                        AudioService.this.mFullVolumeDevices |= 1024;
                    } else {
                        Log.d(AudioService.TAG, "TV, no CEC: setting HDMI as regular vol device");
                        AudioService.this.mFullVolumeDevices &= -1025;
                    }
                    AudioService.this.checkAddAllFixedVolumeDevices(1024, "HdmiPlaybackClient.DisplayStatusCallback");
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0036, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int setHdmiSystemAudioSupported(boolean r7) {
        /*
            r6 = this;
            r0 = 0
            java.lang.Object r1 = r6.mHdmiClientLock
            monitor-enter(r1)
            android.hardware.hdmi.HdmiControlManager r2 = r6.mHdmiManager     // Catch:{ all -> 0x0037 }
            if (r2 == 0) goto L_0x0035
            android.hardware.hdmi.HdmiTvClient r2 = r6.mHdmiTvClient     // Catch:{ all -> 0x0037 }
            if (r2 != 0) goto L_0x0019
            android.hardware.hdmi.HdmiAudioSystemClient r2 = r6.mHdmiAudioSystemClient     // Catch:{ all -> 0x0037 }
            if (r2 != 0) goto L_0x0019
            java.lang.String r2 = "AS.AudioService"
            java.lang.String r3 = "Only Hdmi-Cec enabled TV or audio system device supportssystem audio mode."
            android.util.Log.w(r2, r3)     // Catch:{ all -> 0x0037 }
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            return r0
        L_0x0019:
            boolean r2 = r6.mHdmiSystemAudioSupported     // Catch:{ all -> 0x0037 }
            if (r2 == r7) goto L_0x002f
            r6.mHdmiSystemAudioSupported = r7     // Catch:{ all -> 0x0037 }
            if (r7 == 0) goto L_0x0024
            r2 = 12
            goto L_0x0025
        L_0x0024:
            r2 = 0
        L_0x0025:
            com.android.server.audio.AudioDeviceBroker r3 = r6.mDeviceBroker     // Catch:{ all -> 0x0037 }
            r4 = 5
            java.lang.String r5 = "setHdmiSystemAudioSupported"
            r3.setForceUse_Async(r4, r2, r5)     // Catch:{ all -> 0x0037 }
        L_0x002f:
            r2 = 3
            int r2 = r6.getDevicesForStream(r2)     // Catch:{ all -> 0x0037 }
            r0 = r2
        L_0x0035:
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            return r0
        L_0x0037:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.setHdmiSystemAudioSupported(boolean):int");
    }

    public boolean isHdmiSystemAudioSupported() {
        return this.mHdmiSystemAudioSupported;
    }

    private void initA11yMonitoring() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        updateDefaultStreamOverrideDelay(accessibilityManager.isTouchExplorationEnabled());
        updateA11yVolumeAlias(accessibilityManager.isAccessibilityVolumeStreamActive());
        accessibilityManager.addTouchExplorationStateChangeListener(this, (Handler) null);
        accessibilityManager.addAccessibilityServicesStateChangeListener(this, (Handler) null);
    }

    public void onTouchExplorationStateChanged(boolean enabled) {
        updateDefaultStreamOverrideDelay(enabled);
    }

    private void updateDefaultStreamOverrideDelay(boolean touchExploreEnabled) {
        if (touchExploreEnabled) {
            sStreamOverrideDelayMs = 1000;
        } else {
            sStreamOverrideDelayMs = 0;
        }
        Log.d(TAG, "Touch exploration enabled=" + touchExploreEnabled + " stream override delay is now " + sStreamOverrideDelayMs + " ms");
    }

    public void onAccessibilityServicesStateChanged(AccessibilityManager accessibilityManager) {
        updateA11yVolumeAlias(accessibilityManager.isAccessibilityVolumeStreamActive());
    }

    private void updateA11yVolumeAlias(boolean a11VolEnabled) {
        Log.d(TAG, "Accessibility volume enabled = " + a11VolEnabled);
        if (sIndependentA11yVolume != a11VolEnabled) {
            sIndependentA11yVolume = a11VolEnabled;
            int i = 1;
            updateStreamVolumeAlias(true, TAG);
            VolumeController volumeController = this.mVolumeController;
            if (!sIndependentA11yVolume) {
                i = 0;
            }
            volumeController.setA11yMode(i);
            this.mVolumeController.postVolumeChanged(10, 0);
        }
    }

    public boolean isCameraSoundForced() {
        boolean z;
        synchronized (this.mSettingsLock) {
            z = this.mCameraSoundForced;
        }
        return z;
    }

    private void dumpRingerMode(PrintWriter pw) {
        pw.println("\nRinger mode: ");
        pw.println("- mode (internal) = " + RINGER_MODE_NAMES[this.mRingerMode]);
        pw.println("- mode (external) = " + RINGER_MODE_NAMES[this.mRingerModeExternal]);
        dumpRingerModeStreams(pw, "affected", this.mRingerModeAffectedStreams);
        dumpRingerModeStreams(pw, "muted", this.mRingerAndZenModeMutedStreams);
        pw.print("- delegate = ");
        pw.println(this.mRingerModeDelegate);
    }

    private void dumpRingerModeStreams(PrintWriter pw, String type, int streams) {
        pw.print("- ringer mode ");
        pw.print(type);
        pw.print(" streams = 0x");
        pw.print(Integer.toHexString(streams));
        if (streams != 0) {
            pw.print(" (");
            boolean first = true;
            for (int i = 0; i < AudioSystem.STREAM_NAMES.length; i++) {
                int stream = 1 << i;
                if ((streams & stream) != 0) {
                    if (!first) {
                        pw.print(',');
                    }
                    pw.print(AudioSystem.STREAM_NAMES[i]);
                    streams &= ~stream;
                    first = false;
                }
            }
            if (streams != 0) {
                if (!first) {
                    pw.print(',');
                }
                pw.print(streams);
            }
            pw.print(')');
        }
        pw.println();
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("focusControl")) {
                    this.mMediaFocusControl.dump(pw, args);
                    return;
                }
            }
            this.mMediaFocusControl.dump(pw);
            dumpStreamStates(pw);
            dumpRingerMode(pw);
            pw.println("\nAudio routes:");
            pw.print("  mMainType=0x");
            pw.println(Integer.toHexString(this.mDeviceBroker.getCurAudioRoutes().mainType));
            pw.print("  mBluetoothName=");
            pw.println(this.mDeviceBroker.getCurAudioRoutes().bluetoothName);
            pw.println("\nOther state:");
            pw.print("  mVolumeController=");
            pw.println(this.mVolumeController);
            pw.print("  mSafeMediaVolumeState=");
            pw.println(safeMediaVolumeStateToString(this.mSafeMediaVolumeState));
            pw.print("  mSafeMediaVolumeIndex=");
            pw.println(this.mSafeMediaVolumeIndex);
            pw.print("  mSafeUsbMediaVolumeIndex=");
            pw.println(this.mSafeUsbMediaVolumeIndex);
            pw.print("  mSafeUsbMediaVolumeDbfs=");
            pw.println(this.mSafeUsbMediaVolumeDbfs);
            pw.print("  sIndependentA11yVolume=");
            pw.println(sIndependentA11yVolume);
            pw.print("  mPendingVolumeCommand=");
            pw.println(this.mPendingVolumeCommand);
            pw.print("  mMusicActiveMs=");
            pw.println(this.mMusicActiveMs);
            pw.print("  mMcc=");
            pw.println(this.mMcc);
            pw.print("  mCameraSoundForced=");
            pw.println(this.mCameraSoundForced);
            pw.print("  mHasVibrator=");
            pw.println(this.mHasVibrator);
            pw.print("  mVolumePolicy=");
            pw.println(this.mVolumePolicy);
            pw.print("  mAvrcpAbsVolSupported=");
            pw.println(this.mDeviceBroker.isAvrcpAbsoluteVolumeSupported());
            pw.print("  mIsSingleVolume=");
            pw.println(this.mIsSingleVolume);
            pw.print("  mUseFixedVolume=");
            pw.println(this.mUseFixedVolume);
            pw.print("  mFixedVolumeDevices=0x");
            pw.println(Integer.toHexString(this.mFixedVolumeDevices));
            pw.print("  mHdmiCecSink=");
            pw.println(this.mHdmiCecSink);
            pw.print("  mHdmiAudioSystemClient=");
            pw.println(this.mHdmiAudioSystemClient);
            pw.print("  mHdmiPlaybackClient=");
            pw.println(this.mHdmiPlaybackClient);
            pw.print("  mHdmiTvClient=");
            pw.println(this.mHdmiTvClient);
            pw.print("  mHdmiSystemAudioSupported=");
            pw.println(this.mHdmiSystemAudioSupported);
            dumpAudioPolicies(pw);
            this.mDynPolicyLogger.dump(pw);
            this.mPlaybackMonitor.dump(pw);
            this.mRecordMonitor.dump(pw);
            pw.println("\n");
            pw.println("\nEvent logs:");
            this.mModeLogger.dump(pw);
            pw.println("\n");
            sDeviceLogger.dump(pw);
            pw.println("\n");
            sForceUseLogger.dump(pw);
            pw.println("\n");
            sVolumeLogger.dump(pw);
        }
    }

    private static String safeMediaVolumeStateToString(int state) {
        if (state == 0) {
            return "SAFE_MEDIA_VOLUME_NOT_CONFIGURED";
        }
        if (state == 1) {
            return "SAFE_MEDIA_VOLUME_DISABLED";
        }
        if (state == 2) {
            return "SAFE_MEDIA_VOLUME_INACTIVE";
        }
        if (state != 3) {
            return null;
        }
        return "SAFE_MEDIA_VOLUME_ACTIVE";
    }

    private static void readAndSetLowRamDevice() {
        boolean isLowRamDevice = ActivityManager.isLowRamDeviceStatic();
        long totalMemory = 1073741824;
        try {
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            ActivityManager.getService().getMemoryInfo(info);
            totalMemory = info.totalMem;
        } catch (RemoteException e) {
            Log.w(TAG, "Cannot obtain MemoryInfo from ActivityManager, assume low memory device");
            isLowRamDevice = true;
        }
        int status = AudioSystem.setLowRamDevice(isLowRamDevice, totalMemory);
        if (status != 0) {
            Log.w(TAG, "AudioFlinger informed of device's low RAM attribute; status " + status);
        }
    }

    private void enforceVolumeController(String action) {
        Context context = this.mContext;
        context.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "Only SystemUI can " + action);
    }

    public void setVolumeController(final IVolumeController controller) {
        enforceVolumeController("set the volume controller");
        if (!this.mVolumeController.isSameBinder(controller)) {
            this.mVolumeController.postDismiss();
            if (controller != null) {
                try {
                    controller.asBinder().linkToDeath(new IBinder.DeathRecipient() {
                        public void binderDied() {
                            if (AudioService.this.mVolumeController.isSameBinder(controller)) {
                                Log.w(AudioService.TAG, "Current remote volume controller died, unregistering");
                                AudioService.this.setVolumeController((IVolumeController) null);
                            }
                        }
                    }, 0);
                } catch (RemoteException e) {
                }
            }
            this.mVolumeController.setController(controller);
            Log.d(TAG, "Volume controller: " + this.mVolumeController);
        }
    }

    public void notifyVolumeControllerVisible(IVolumeController controller, boolean visible) {
        enforceVolumeController("notify about volume controller visibility");
        if (this.mVolumeController.isSameBinder(controller)) {
            this.mVolumeController.setVisible(visible);
            Log.d(TAG, "Volume controller visible: " + visible);
        }
    }

    public void setVolumePolicy(VolumePolicy policy) {
        enforceVolumeController("set volume policy");
        if (policy != null && !policy.equals(this.mVolumePolicy)) {
            this.mVolumePolicy = policy;
            Log.d(TAG, "Volume policy changed: " + this.mVolumePolicy);
        }
    }

    public static class VolumeController {
        private static final String TAG = "VolumeController";
        private IVolumeController mController;
        private int mLongPressTimeout;
        private long mNextLongPress;
        private boolean mVisible;

        public void setController(IVolumeController controller) {
            this.mController = controller;
            this.mVisible = false;
        }

        public void loadSettings(ContentResolver cr) {
            this.mLongPressTimeout = Settings.Secure.getIntForUser(cr, "long_press_timeout", 500, -2);
        }

        public boolean suppressAdjustment(int resolvedStream, int flags, boolean isMute) {
            if (isMute || resolvedStream == 3 || this.mController == null) {
                return false;
            }
            long now = SystemClock.uptimeMillis();
            if ((flags & 1) == 0 || this.mVisible) {
                long j = this.mNextLongPress;
                if (j <= 0) {
                    return false;
                }
                if (now <= j) {
                    return true;
                }
                this.mNextLongPress = 0;
                return false;
            }
            if (this.mNextLongPress < now) {
                this.mNextLongPress = ((long) this.mLongPressTimeout) + now;
            }
            return true;
        }

        public void setVisible(boolean visible) {
            this.mVisible = visible;
        }

        public boolean isSameBinder(IVolumeController controller) {
            return Objects.equals(asBinder(), binder(controller));
        }

        public IBinder asBinder() {
            return binder(this.mController);
        }

        private static IBinder binder(IVolumeController controller) {
            if (controller == null) {
                return null;
            }
            return controller.asBinder();
        }

        public String toString() {
            return "VolumeController(" + asBinder() + ",mVisible=" + this.mVisible + ")";
        }

        public void postDisplaySafeVolumeWarning(int flags) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController != null) {
                try {
                    iVolumeController.displaySafeVolumeWarning(flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling displaySafeVolumeWarning", e);
                }
            }
        }

        public void postVolumeChanged(int streamType, int flags) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController != null) {
                try {
                    iVolumeController.volumeChanged(streamType, flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling volumeChanged", e);
                }
            }
        }

        public void postMasterMuteChanged(int flags) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController != null) {
                try {
                    iVolumeController.masterMuteChanged(flags);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling masterMuteChanged", e);
                }
            }
        }

        public void setLayoutDirection(int layoutDirection) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController != null) {
                try {
                    iVolumeController.setLayoutDirection(layoutDirection);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling setLayoutDirection", e);
                }
            }
        }

        public void postDismiss() {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController != null) {
                try {
                    iVolumeController.dismiss();
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling dismiss", e);
                }
            }
        }

        public void setA11yMode(int a11yMode) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController != null) {
                try {
                    iVolumeController.setA11yMode(a11yMode);
                } catch (RemoteException e) {
                    Log.w(TAG, "Error calling setA11Mode", e);
                }
            }
        }
    }

    final class AudioServiceInternal extends AudioManagerInternal {
        AudioServiceInternal() {
        }

        public void setRingerModeDelegate(AudioManagerInternal.RingerModeDelegate delegate) {
            AudioManagerInternal.RingerModeDelegate unused = AudioService.this.mRingerModeDelegate = delegate;
            if (AudioService.this.mRingerModeDelegate != null) {
                synchronized (AudioService.this.mSettingsLock) {
                    boolean unused2 = AudioService.this.updateRingerAndZenModeAffectedStreams();
                }
                setRingerModeInternal(getRingerModeInternal(), "AS.AudioService.setRingerModeDelegate");
            }
        }

        public void adjustSuggestedStreamVolumeForUid(int streamType, int direction, int flags, String callingPackage, int uid) {
            AudioService.this.adjustSuggestedStreamVolume(direction, streamType, flags, callingPackage, callingPackage, uid);
        }

        public void adjustStreamVolumeForUid(int streamType, int direction, int flags, String callingPackage, int uid) {
            if (direction != 0) {
                AudioEventLogger audioEventLogger = AudioService.sVolumeLogger;
                audioEventLogger.log(new AudioServiceEvents.VolumeEvent(5, streamType, direction, flags, callingPackage + " uid:" + uid));
            }
            AudioService.this.adjustStreamVolume(streamType, direction, flags, callingPackage, callingPackage, uid);
        }

        public void setStreamVolumeForUid(int streamType, int direction, int flags, String callingPackage, int uid) {
            AudioService.this.setStreamVolume(streamType, direction, flags, callingPackage, callingPackage, uid);
        }

        public int getRingerModeInternal() {
            return AudioService.this.getRingerModeInternal();
        }

        public void setRingerModeInternal(int ringerMode, String caller) {
            AudioService.this.setRingerModeInternal(ringerMode, caller);
        }

        public void silenceRingerModeInternal(String caller) {
            AudioService.this.silenceRingerModeInternal(caller);
        }

        public void updateRingerModeAffectedStreamsInternal() {
            synchronized (AudioService.this.mSettingsLock) {
                if (AudioService.this.updateRingerAndZenModeAffectedStreams()) {
                    AudioService.this.setRingerModeInt(getRingerModeInternal(), false);
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0031 A[LOOP:0: B:14:0x0031->B:19:0x004a, LOOP_START, PHI: r2 
          PHI: (r2v2 'i' int) = (r2v0 'i' int), (r2v3 'i' int) binds: [B:13:0x002e, B:19:0x004a] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x004f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setAccessibilityServiceUids(android.util.IntArray r6) {
            /*
                r5 = this;
                com.android.server.audio.AudioService r0 = com.android.server.audio.AudioService.this
                java.lang.Object r0 = r0.mAccessibilityServiceUidsLock
                monitor-enter(r0)
                int r1 = r6.size()     // Catch:{ all -> 0x0063 }
                if (r1 != 0) goto L_0x0014
                com.android.server.audio.AudioService r1 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                r2 = 0
                int[] unused = r1.mAccessibilityServiceUids = r2     // Catch:{ all -> 0x0063 }
                goto L_0x0058
            L_0x0014:
                com.android.server.audio.AudioService r1 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                int[] r1 = r1.mAccessibilityServiceUids     // Catch:{ all -> 0x0063 }
                r2 = 0
                if (r1 == 0) goto L_0x002d
                com.android.server.audio.AudioService r1 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                int[] r1 = r1.mAccessibilityServiceUids     // Catch:{ all -> 0x0063 }
                int r1 = r1.length     // Catch:{ all -> 0x0063 }
                int r3 = r6.size()     // Catch:{ all -> 0x0063 }
                if (r1 == r3) goto L_0x002b
                goto L_0x002d
            L_0x002b:
                r1 = r2
                goto L_0x002e
            L_0x002d:
                r1 = 1
            L_0x002e:
                if (r1 != 0) goto L_0x004d
            L_0x0031:
                com.android.server.audio.AudioService r3 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                int[] r3 = r3.mAccessibilityServiceUids     // Catch:{ all -> 0x0063 }
                int r3 = r3.length     // Catch:{ all -> 0x0063 }
                if (r2 >= r3) goto L_0x004d
                int r3 = r6.get(r2)     // Catch:{ all -> 0x0063 }
                com.android.server.audio.AudioService r4 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                int[] r4 = r4.mAccessibilityServiceUids     // Catch:{ all -> 0x0063 }
                r4 = r4[r2]     // Catch:{ all -> 0x0063 }
                if (r3 == r4) goto L_0x004a
                r1 = 1
                goto L_0x004d
            L_0x004a:
                int r2 = r2 + 1
                goto L_0x0031
            L_0x004d:
                if (r1 == 0) goto L_0x0058
                com.android.server.audio.AudioService r2 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                int[] r3 = r6.toArray()     // Catch:{ all -> 0x0063 }
                int[] unused = r2.mAccessibilityServiceUids = r3     // Catch:{ all -> 0x0063 }
            L_0x0058:
                com.android.server.audio.AudioService r1 = com.android.server.audio.AudioService.this     // Catch:{ all -> 0x0063 }
                int[] r1 = r1.mAccessibilityServiceUids     // Catch:{ all -> 0x0063 }
                android.media.AudioSystem.setA11yServicesUids(r1)     // Catch:{ all -> 0x0063 }
                monitor-exit(r0)     // Catch:{ all -> 0x0063 }
                return
            L_0x0063:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0063 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.AudioServiceInternal.setAccessibilityServiceUids(android.util.IntArray):void");
        }
    }

    public String registerAudioPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb, boolean hasFocusListener, boolean isFocusPolicy, boolean isTestFocusPolicy, boolean isVolumeController, IMediaProjection projection) {
        HashMap<IBinder, AudioPolicyProxy> hashMap;
        AudioPolicyConfig audioPolicyConfig = policyConfig;
        IAudioPolicyCallback iAudioPolicyCallback = pcb;
        AudioSystem.setDynamicPolicyCallback(this.mDynPolicyCallback);
        if (!isPolicyRegisterAllowed(audioPolicyConfig, isFocusPolicy || isTestFocusPolicy || hasFocusListener, isVolumeController, projection)) {
            Slog.w(TAG, "Permission denied to register audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING or MediaProjection that can project audio");
            return null;
        }
        AudioEventLogger audioEventLogger = this.mDynPolicyLogger;
        audioEventLogger.log(new AudioEventLogger.StringEvent("registerAudioPolicy for " + pcb.asBinder() + " with config:" + audioPolicyConfig).printLog(TAG));
        HashMap<IBinder, AudioPolicyProxy> hashMap2 = this.mAudioPolicies;
        synchronized (hashMap2) {
            try {
                if (this.mAudioPolicies.containsKey(pcb.asBinder())) {
                    Slog.e(TAG, "Cannot re-register policy");
                } else {
                    try {
                        hashMap = hashMap2;
                        try {
                            AudioPolicyProxy audioPolicyProxy = new AudioPolicyProxy(policyConfig, pcb, hasFocusListener, isFocusPolicy, isTestFocusPolicy, isVolumeController, projection);
                            AudioPolicyProxy app = audioPolicyProxy;
                            pcb.asBinder().linkToDeath(app, 0);
                            String regId = app.getRegistrationId();
                            this.mAudioPolicies.put(pcb.asBinder(), app);
                            return regId;
                        } catch (RemoteException e) {
                            e = e;
                        } catch (IllegalStateException e2) {
                            e = e2;
                            Slog.w(TAG, "Audio policy registration failed for binder " + iAudioPolicyCallback, e);
                            return null;
                        } catch (Throwable th) {
                            e = th;
                            throw e;
                        }
                    } catch (RemoteException e3) {
                        e = e3;
                        hashMap = hashMap2;
                        Slog.w(TAG, "Audio policy registration failed, could not link to " + iAudioPolicyCallback + " binder death", e);
                        return null;
                    } catch (IllegalStateException e4) {
                        e = e4;
                        hashMap = hashMap2;
                        Slog.w(TAG, "Audio policy registration failed for binder " + iAudioPolicyCallback, e);
                        return null;
                    }
                }
            } catch (Throwable th2) {
                e = th2;
                hashMap = hashMap2;
                throw e;
            }
        }
        return null;
    }

    private boolean isPolicyRegisterAllowed(AudioPolicyConfig policyConfig, boolean hasFocusAccess, boolean isVolumeController, IMediaProjection projection) {
        boolean requireValidProjection = false;
        boolean requireCaptureAudioOrMediaOutputPerm = false;
        boolean requireModifyRouting = false;
        if (hasFocusAccess || isVolumeController) {
            requireModifyRouting = false | true;
        } else if (policyConfig.getMixes().isEmpty()) {
            requireModifyRouting = false | true;
        }
        Iterator it = policyConfig.getMixes().iterator();
        while (it.hasNext()) {
            AudioMix mix = (AudioMix) it.next();
            if (mix.getRule().allowPrivilegedPlaybackCapture()) {
                requireCaptureAudioOrMediaOutputPerm |= true;
                String error = AudioMix.canBeUsedForPrivilegedCapture(mix.getFormat());
                if (error != null) {
                    Log.e(TAG, error);
                    return false;
                }
            }
            if (mix.getRouteFlags() != 3 || projection == null) {
                requireModifyRouting |= true;
            } else {
                requireValidProjection |= true;
            }
        }
        if (requireCaptureAudioOrMediaOutputPerm && !callerHasPermission("android.permission.CAPTURE_MEDIA_OUTPUT") && !callerHasPermission("android.permission.CAPTURE_AUDIO_OUTPUT")) {
            Log.e(TAG, "Privileged audio capture requires CAPTURE_MEDIA_OUTPUT or CAPTURE_AUDIO_OUTPUT system permission");
            return false;
        } else if (requireValidProjection && !canProjectAudio(projection)) {
            return false;
        } else {
            if (!requireModifyRouting || callerHasPermission("android.permission.MODIFY_AUDIO_ROUTING")) {
                return true;
            }
            Log.e(TAG, "Can not capture audio without MODIFY_AUDIO_ROUTING");
            return false;
        }
    }

    private boolean callerHasPermission(String permission) {
        return this.mContext.checkCallingPermission(permission) == 0;
    }

    private boolean canProjectAudio(IMediaProjection projection) {
        if (projection == null) {
            Log.e(TAG, "MediaProjection is null");
            return false;
        }
        IMediaProjectionManager projectionService = getProjectionService();
        if (projectionService == null) {
            Log.e(TAG, "Can't get service IMediaProjectionManager");
            return false;
        }
        try {
            if (!projectionService.isValidMediaProjection(projection)) {
                Log.w(TAG, "App passed invalid MediaProjection token");
                return false;
            }
            try {
                if (projection.canProjectAudio()) {
                    return true;
                }
                Log.w(TAG, "App passed MediaProjection that can not project audio");
                return false;
            } catch (RemoteException e) {
                Log.e(TAG, "Can't call .canProjectAudio() on valid IMediaProjection" + projection.asBinder(), e);
                return false;
            }
        } catch (RemoteException e2) {
            Log.e(TAG, "Can't call .isValidMediaProjection() on IMediaProjectionManager" + projectionService.asBinder(), e2);
            return false;
        }
    }

    private IMediaProjectionManager getProjectionService() {
        if (this.mProjectionService == null) {
            this.mProjectionService = IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection"));
        }
        return this.mProjectionService;
    }

    public void unregisterAudioPolicyAsync(IAudioPolicyCallback pcb) {
        unregisterAudioPolicy(pcb);
    }

    public void unregisterAudioPolicy(IAudioPolicyCallback pcb) {
        if (pcb != null) {
            unregisterAudioPolicyInt(pcb);
        }
    }

    private void unregisterAudioPolicyInt(IAudioPolicyCallback pcb) {
        AudioEventLogger audioEventLogger = this.mDynPolicyLogger;
        audioEventLogger.log(new AudioEventLogger.StringEvent("unregisterAudioPolicyAsync for " + pcb.asBinder()).printLog(TAG));
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy app = this.mAudioPolicies.remove(pcb.asBinder());
            if (app == null) {
                Slog.w(TAG, "Trying to unregister unknown audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid());
                return;
            }
            pcb.asBinder().unlinkToDeath(app, 0);
            app.release();
        }
    }

    @GuardedBy({"mAudioPolicies"})
    private AudioPolicyProxy checkUpdateForPolicy(IAudioPolicyCallback pcb, String errorMsg) {
        if (!(this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0)) {
            Slog.w(TAG, errorMsg + " for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING");
            return null;
        }
        AudioPolicyProxy app = this.mAudioPolicies.get(pcb.asBinder());
        if (app != null) {
            return app;
        }
        Slog.w(TAG, errorMsg + " for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", unregistered policy");
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003e, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int addMixForPolicy(android.media.audiopolicy.AudioPolicyConfig r5, android.media.audiopolicy.IAudioPolicyCallback r6) {
        /*
            r4 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "addMixForPolicy for "
            r0.append(r1)
            android.os.IBinder r1 = r6.asBinder()
            r0.append(r1)
            java.lang.String r1 = " with config:"
            r0.append(r1)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AS.AudioService"
            android.util.Log.d(r1, r0)
            java.util.HashMap<android.os.IBinder, com.android.server.audio.AudioService$AudioPolicyProxy> r0 = r4.mAudioPolicies
            monitor-enter(r0)
            java.lang.String r1 = "Cannot add AudioMix in audio policy"
            com.android.server.audio.AudioService$AudioPolicyProxy r1 = r4.checkUpdateForPolicy(r6, r1)     // Catch:{ all -> 0x003f }
            r2 = -1
            if (r1 != 0) goto L_0x0030
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return r2
        L_0x0030:
            java.util.ArrayList r3 = r5.getMixes()     // Catch:{ all -> 0x003f }
            int r3 = r1.addMixes(r3)     // Catch:{ all -> 0x003f }
            if (r3 != 0) goto L_0x003c
            r2 = 0
            goto L_0x003d
        L_0x003c:
        L_0x003d:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return r2
        L_0x003f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.addMixForPolicy(android.media.audiopolicy.AudioPolicyConfig, android.media.audiopolicy.IAudioPolicyCallback):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003f, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int removeMixForPolicy(android.media.audiopolicy.AudioPolicyConfig r5, android.media.audiopolicy.IAudioPolicyCallback r6) {
        /*
            r4 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "removeMixForPolicy for "
            r0.append(r1)
            android.os.IBinder r1 = r6.asBinder()
            r0.append(r1)
            java.lang.String r1 = " with config:"
            r0.append(r1)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AS.AudioService"
            android.util.Log.d(r1, r0)
            java.util.HashMap<android.os.IBinder, com.android.server.audio.AudioService$AudioPolicyProxy> r0 = r4.mAudioPolicies
            monitor-enter(r0)
            java.lang.String r1 = "Cannot add AudioMix in audio policy"
            com.android.server.audio.AudioService$AudioPolicyProxy r1 = r4.checkUpdateForPolicy(r6, r1)     // Catch:{ all -> 0x0040 }
            r2 = -1
            if (r1 != 0) goto L_0x0031
            monitor-exit(r0)     // Catch:{ all -> 0x0040 }
            return r2
        L_0x0031:
            java.util.ArrayList r3 = r5.getMixes()     // Catch:{ all -> 0x0040 }
            int r3 = r1.removeMixes(r3)     // Catch:{ all -> 0x0040 }
            if (r3 != 0) goto L_0x003d
            r2 = 0
            goto L_0x003e
        L_0x003d:
        L_0x003e:
            monitor-exit(r0)     // Catch:{ all -> 0x0040 }
            return r2
        L_0x0040:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0040 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioService.removeMixForPolicy(android.media.audiopolicy.AudioPolicyConfig, android.media.audiopolicy.IAudioPolicyCallback):int");
    }

    public int setUidDeviceAffinity(IAudioPolicyCallback pcb, int uid, int[] deviceTypes, String[] deviceAddresses) {
        Log.d(TAG, "setUidDeviceAffinity for " + pcb.asBinder() + " uid:" + uid);
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy app = checkUpdateForPolicy(pcb, "Cannot change device affinity in audio policy");
            if (app == null) {
                return -1;
            }
            if (!app.hasMixRoutedToDevices(deviceTypes, deviceAddresses)) {
                return -1;
            }
            int uidDeviceAffinities = app.setUidDeviceAffinities(uid, deviceTypes, deviceAddresses);
            return uidDeviceAffinities;
        }
    }

    public int removeUidDeviceAffinity(IAudioPolicyCallback pcb, int uid) {
        Log.d(TAG, "removeUidDeviceAffinity for " + pcb.asBinder() + " uid:" + uid);
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy app = checkUpdateForPolicy(pcb, "Cannot remove device affinity in audio policy");
            if (app == null) {
                return -1;
            }
            int removeUidDeviceAffinities = app.removeUidDeviceAffinities(uid);
            return removeUidDeviceAffinities;
        }
    }

    public int setFocusPropertiesForPolicy(int duckingBehavior, IAudioPolicyCallback pcb) {
        Log.d(TAG, "setFocusPropertiesForPolicy() duck behavior=" + duckingBehavior + " policy " + pcb.asBinder());
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy app = checkUpdateForPolicy(pcb, "Cannot change audio policy focus properties");
            if (app == null) {
                return -1;
            }
            if (!this.mAudioPolicies.containsKey(pcb.asBinder())) {
                Slog.e(TAG, "Cannot change audio policy focus properties, unregistered policy");
                return -1;
            }
            boolean z = true;
            if (duckingBehavior == 1) {
                for (AudioPolicyProxy policy : this.mAudioPolicies.values()) {
                    if (policy.mFocusDuckBehavior == 1) {
                        Slog.e(TAG, "Cannot change audio policy ducking behavior, already handled");
                        return -1;
                    }
                }
            }
            app.mFocusDuckBehavior = duckingBehavior;
            MediaFocusControl mediaFocusControl = this.mMediaFocusControl;
            if (duckingBehavior != 1) {
                z = false;
            }
            mediaFocusControl.setDuckingInExtPolicyAvailable(z);
            return 0;
        }
    }

    public boolean hasRegisteredDynamicPolicy() {
        boolean z;
        synchronized (this.mAudioPolicies) {
            z = !this.mAudioPolicies.isEmpty();
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void setExtVolumeController(IAudioPolicyCallback apc) {
        if (!this.mContext.getResources().getBoolean(17891465)) {
            Log.e(TAG, "Cannot set external volume controller: device not set for volume keys handled in PhoneWindowManager");
            return;
        }
        synchronized (this.mExtVolumeControllerLock) {
            if (this.mExtVolumeController != null && !this.mExtVolumeController.asBinder().pingBinder()) {
                Log.e(TAG, "Cannot set external volume controller: existing controller");
            }
            this.mExtVolumeController = apc;
        }
    }

    private void dumpAudioPolicies(PrintWriter pw) {
        pw.println("\nAudio policies:");
        synchronized (this.mAudioPolicies) {
            for (AudioPolicyProxy policy : this.mAudioPolicies.values()) {
                pw.println(policy.toLogFriendlyString());
            }
        }
    }

    /* access modifiers changed from: private */
    public void onDynPolicyMixStateUpdate(String regId, int state) {
        Log.d(TAG, "onDynamicPolicyMixStateUpdate(" + regId + ", " + state + ")");
        synchronized (this.mAudioPolicies) {
            for (AudioPolicyProxy policy : this.mAudioPolicies.values()) {
                Iterator it = policy.getMixes().iterator();
                while (it.hasNext()) {
                    if (((AudioMix) it.next()).getRegistration().equals(regId)) {
                        try {
                            policy.mPolicyCallback.notifyMixStateUpdate(regId, state);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Can't call notifyMixStateUpdate() on IAudioPolicyCallback " + policy.mPolicyCallback.asBinder(), e);
                        }
                    }
                }
            }
        }
    }

    public void registerRecordingCallback(IRecordingConfigDispatcher rcdb) {
        this.mRecordMonitor.registerRecordingCallback(rcdb, this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public void unregisterRecordingCallback(IRecordingConfigDispatcher rcdb) {
        this.mRecordMonitor.unregisterRecordingCallback(rcdb);
    }

    public List<AudioRecordingConfiguration> getActiveRecordingConfigurations() {
        return this.mRecordMonitor.getActiveRecordingConfigurations(this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public int trackRecorder(IBinder recorder) {
        return this.mRecordMonitor.trackRecorder(recorder);
    }

    public void recorderEvent(int riid, int event) {
        this.mRecordMonitor.recorderEvent(riid, event);
    }

    public void releaseRecorder(int riid) {
        this.mRecordMonitor.releaseRecorder(riid);
    }

    public void disableRingtoneSync(int userId) {
        if (UserHandle.getCallingUserId() != userId) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "disable sound settings syncing for another profile");
        }
        long token = Binder.clearCallingIdentity();
        try {
            Settings.Secure.putIntForUser(this.mContentResolver, "sync_parent_sounds", 0, userId);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void registerPlaybackCallback(IPlaybackConfigDispatcher pcdb) {
        this.mPlaybackMonitor.registerPlaybackCallback(pcdb, this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public void unregisterPlaybackCallback(IPlaybackConfigDispatcher pcdb) {
        this.mPlaybackMonitor.unregisterPlaybackCallback(pcdb);
    }

    public List<AudioPlaybackConfiguration> getActivePlaybackConfigurations() {
        return this.mPlaybackMonitor.getActivePlaybackConfigurations(this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public int trackPlayer(PlayerBase.PlayerIdCard pic) {
        return this.mPlaybackMonitor.trackPlayer(pic);
    }

    public void playerAttributes(int piid, AudioAttributes attr) {
        this.mPlaybackMonitor.playerAttributes(piid, attr, Binder.getCallingUid());
    }

    public void playerEvent(int piid, int event) {
        this.mPlaybackMonitor.playerEvent(piid, event, Binder.getCallingUid());
    }

    public void playerHasOpPlayAudio(int piid, boolean hasOpPlayAudio) {
        this.mPlaybackMonitor.playerHasOpPlayAudio(piid, hasOpPlayAudio, Binder.getCallingUid());
    }

    public void releasePlayer(int piid) {
        this.mPlaybackMonitor.releasePlayer(piid, Binder.getCallingUid());
    }

    private static final class AudioDeviceArray {
        final String[] mDeviceAddresses;
        final int[] mDeviceTypes;

        AudioDeviceArray(int[] types, String[] addresses) {
            this.mDeviceTypes = types;
            this.mDeviceAddresses = addresses;
        }
    }

    public class AudioPolicyProxy extends AudioPolicyConfig implements IBinder.DeathRecipient {
        private static final String TAG = "AudioPolicyProxy";
        int mFocusDuckBehavior = 0;
        final boolean mHasFocusListener;
        boolean mIsFocusPolicy = false;
        boolean mIsTestFocusPolicy = false;
        final boolean mIsVolumeController;
        final IAudioPolicyCallback mPolicyCallback;
        final IMediaProjection mProjection;
        UnregisterOnStopCallback mProjectionCallback;
        final HashMap<Integer, AudioDeviceArray> mUidDeviceAffinities = new HashMap<>();

        private final class UnregisterOnStopCallback extends IMediaProjectionCallback.Stub {
            private UnregisterOnStopCallback() {
            }

            public void onStop() {
                AudioService.this.unregisterAudioPolicyAsync(AudioPolicyProxy.this.mPolicyCallback);
            }
        }

        AudioPolicyProxy(AudioPolicyConfig config, IAudioPolicyCallback token, boolean hasFocusListener, boolean isFocusPolicy, boolean isTestFocusPolicy, boolean isVolumeController, IMediaProjection projection) {
            super(config);
            setRegistration(new String(config.hashCode() + ":ap:" + AudioService.access$9708(AudioService.this)));
            this.mPolicyCallback = token;
            this.mHasFocusListener = hasFocusListener;
            this.mIsVolumeController = isVolumeController;
            this.mProjection = projection;
            if (this.mHasFocusListener) {
                AudioService.this.mMediaFocusControl.addFocusFollower(this.mPolicyCallback);
                if (isFocusPolicy) {
                    this.mIsFocusPolicy = true;
                    this.mIsTestFocusPolicy = isTestFocusPolicy;
                    AudioService.this.mMediaFocusControl.setFocusPolicy(this.mPolicyCallback, this.mIsTestFocusPolicy);
                }
            }
            if (this.mIsVolumeController) {
                AudioService.this.setExtVolumeController(this.mPolicyCallback);
            }
            if (this.mProjection != null) {
                this.mProjectionCallback = new UnregisterOnStopCallback();
                try {
                    this.mProjection.registerCallback(this.mProjectionCallback);
                } catch (RemoteException e) {
                    release();
                    throw new IllegalStateException("MediaProjection callback registration failed, could not link to " + projection + " binder death", e);
                }
            }
            int status = connectMixes();
            if (status != 0) {
                release();
                throw new IllegalStateException("Could not connect mix, error: " + status);
            }
        }

        public void binderDied() {
            synchronized (AudioService.this.mAudioPolicies) {
                Log.i(TAG, "audio policy " + this.mPolicyCallback + " died");
                release();
                AudioService.this.mAudioPolicies.remove(this.mPolicyCallback.asBinder());
            }
            if (this.mIsVolumeController) {
                synchronized (AudioService.this.mExtVolumeControllerLock) {
                    IAudioPolicyCallback unused = AudioService.this.mExtVolumeController = null;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public String getRegistrationId() {
            return getRegistration();
        }

        /* access modifiers changed from: package-private */
        public void release() {
            if (this.mIsFocusPolicy) {
                AudioService.this.mMediaFocusControl.unsetFocusPolicy(this.mPolicyCallback, this.mIsTestFocusPolicy);
            }
            if (this.mFocusDuckBehavior == 1) {
                AudioService.this.mMediaFocusControl.setDuckingInExtPolicyAvailable(false);
            }
            if (this.mHasFocusListener) {
                AudioService.this.mMediaFocusControl.removeFocusFollower(this.mPolicyCallback);
            }
            UnregisterOnStopCallback unregisterOnStopCallback = this.mProjectionCallback;
            if (unregisterOnStopCallback != null) {
                try {
                    this.mProjection.unregisterCallback(unregisterOnStopCallback);
                } catch (RemoteException e) {
                    Log.e(TAG, "Fail to unregister Audiopolicy callback from MediaProjection");
                }
            }
            long identity = Binder.clearCallingIdentity();
            AudioSystem.registerPolicyMixes(this.mMixes, false);
            Binder.restoreCallingIdentity(identity);
        }

        /* access modifiers changed from: package-private */
        public boolean hasMixAffectingUsage(int usage) {
            Iterator it = this.mMixes.iterator();
            while (it.hasNext()) {
                if (((AudioMix) it.next()).isAffectingUsage(usage)) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean hasMixRoutedToDevices(int[] deviceTypes, String[] deviceAddresses) {
            for (int i = 0; i < deviceTypes.length; i++) {
                boolean hasDevice = false;
                Iterator it = this.mMixes.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (((AudioMix) it.next()).isRoutedToDevice(deviceTypes[i], deviceAddresses[i])) {
                            hasDevice = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!hasDevice) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public int addMixes(ArrayList<AudioMix> mixes) {
            int registerPolicyMixes;
            synchronized (this.mMixes) {
                AudioSystem.registerPolicyMixes(this.mMixes, false);
                add(mixes);
                registerPolicyMixes = AudioSystem.registerPolicyMixes(this.mMixes, true);
            }
            return registerPolicyMixes;
        }

        /* access modifiers changed from: package-private */
        public int removeMixes(ArrayList<AudioMix> mixes) {
            int registerPolicyMixes;
            synchronized (this.mMixes) {
                AudioSystem.registerPolicyMixes(this.mMixes, false);
                remove(mixes);
                registerPolicyMixes = AudioSystem.registerPolicyMixes(this.mMixes, true);
            }
            return registerPolicyMixes;
        }

        /* access modifiers changed from: package-private */
        public int connectMixes() {
            long identity = Binder.clearCallingIdentity();
            int status = AudioSystem.registerPolicyMixes(this.mMixes, true);
            Binder.restoreCallingIdentity(identity);
            return status;
        }

        /* access modifiers changed from: package-private */
        public int setUidDeviceAffinities(int uid, int[] types, String[] addresses) {
            Integer Uid = new Integer(uid);
            if (this.mUidDeviceAffinities.remove(Uid) != null) {
                long identity = Binder.clearCallingIdentity();
                int res = AudioSystem.removeUidDeviceAffinities(uid);
                Binder.restoreCallingIdentity(identity);
                if (res != 0) {
                    Log.e(TAG, "AudioSystem. removeUidDeviceAffinities(" + uid + ") failed,  cannot call AudioSystem.setUidDeviceAffinities");
                    return -1;
                }
            }
            long identity2 = Binder.clearCallingIdentity();
            int res2 = AudioSystem.setUidDeviceAffinities(uid, types, addresses);
            Binder.restoreCallingIdentity(identity2);
            if (res2 == 0) {
                this.mUidDeviceAffinities.put(Uid, new AudioDeviceArray(types, addresses));
                return 0;
            }
            Log.e(TAG, "AudioSystem. setUidDeviceAffinities(" + uid + ") failed");
            return -1;
        }

        /* access modifiers changed from: package-private */
        public int removeUidDeviceAffinities(int uid) {
            if (this.mUidDeviceAffinities.remove(new Integer(uid)) != null) {
                long identity = Binder.clearCallingIdentity();
                int res = AudioSystem.removeUidDeviceAffinities(uid);
                Binder.restoreCallingIdentity(identity);
                if (res == 0) {
                    return 0;
                }
            }
            Log.e(TAG, "AudioSystem. removeUidDeviceAffinities failed");
            return -1;
        }

        public String toLogFriendlyString() {
            String textDump = (AudioService.super.toLogFriendlyString() + " Proxy:\n") + "   is focus policy= " + this.mIsFocusPolicy + "\n";
            if (this.mIsFocusPolicy) {
                textDump = ((textDump + "     focus duck behaviour= " + this.mFocusDuckBehavior + "\n") + "     is test focus policy= " + this.mIsTestFocusPolicy + "\n") + "     has focus listener= " + this.mHasFocusListener + "\n";
            }
            return textDump + "   media projection= " + this.mProjection + "\n";
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public int dispatchFocusChange(AudioFocusInfo afi, int focusChange, IAudioPolicyCallback pcb) {
        int dispatchFocusChange;
        if (afi == null) {
            throw new IllegalArgumentException("Illegal null AudioFocusInfo");
        } else if (pcb != null) {
            synchronized (this.mAudioPolicies) {
                if (this.mAudioPolicies.containsKey(pcb.asBinder())) {
                    dispatchFocusChange = this.mMediaFocusControl.dispatchFocusChange(afi, focusChange);
                } else {
                    throw new IllegalStateException("Unregistered AudioPolicy for focus dispatch");
                }
            }
            return dispatchFocusChange;
        } else {
            throw new IllegalArgumentException("Illegal null AudioPolicy callback");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    public void setFocusRequestResultFromExtPolicy(AudioFocusInfo afi, int requestResult, IAudioPolicyCallback pcb) {
        if (afi == null) {
            throw new IllegalArgumentException("Illegal null AudioFocusInfo");
        } else if (pcb != null) {
            synchronized (this.mAudioPolicies) {
                if (this.mAudioPolicies.containsKey(pcb.asBinder())) {
                    this.mMediaFocusControl.setFocusRequestResultFromExtPolicy(afi, requestResult);
                } else {
                    throw new IllegalStateException("Unregistered AudioPolicy for external focus");
                }
            }
        } else {
            throw new IllegalArgumentException("Illegal null AudioPolicy callback");
        }
    }

    private class AsdProxy implements IBinder.DeathRecipient {
        private final IAudioServerStateDispatcher mAsd;

        AsdProxy(IAudioServerStateDispatcher asd) {
            this.mAsd = asd;
        }

        public void binderDied() {
            synchronized (AudioService.this.mAudioServerStateListeners) {
                AudioService.this.mAudioServerStateListeners.remove(this.mAsd.asBinder());
            }
        }

        /* access modifiers changed from: package-private */
        public IAudioServerStateDispatcher callback() {
            return this.mAsd;
        }
    }

    private void checkMonitorAudioServerStatePermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0) {
            throw new SecurityException("Not allowed to monitor audioserver state");
        }
    }

    public void registerAudioServerStateDispatcher(IAudioServerStateDispatcher asd) {
        checkMonitorAudioServerStatePermission();
        synchronized (this.mAudioServerStateListeners) {
            if (this.mAudioServerStateListeners.containsKey(asd.asBinder())) {
                Slog.w(TAG, "Cannot re-register audio server state dispatcher");
                return;
            }
            AsdProxy asdp = new AsdProxy(asd);
            try {
                asd.asBinder().linkToDeath(asdp, 0);
            } catch (RemoteException e) {
            }
            this.mAudioServerStateListeners.put(asd.asBinder(), asdp);
        }
    }

    public void unregisterAudioServerStateDispatcher(IAudioServerStateDispatcher asd) {
        checkMonitorAudioServerStatePermission();
        synchronized (this.mAudioServerStateListeners) {
            AsdProxy asdp = this.mAudioServerStateListeners.remove(asd.asBinder());
            if (asdp == null) {
                Slog.w(TAG, "Trying to unregister unknown audioserver state dispatcher for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid());
                return;
            }
            asd.asBinder().unlinkToDeath(asdp, 0);
        }
    }

    public boolean isAudioServerRunning() {
        checkMonitorAudioServerStatePermission();
        return AudioSystem.checkAudioFlinger() == 0;
    }

    public IBinder createAudioRecordForLoopback(ParcelFileDescriptor sharedMem, long size) {
        return new MiuiAudioRecord(sharedMem.getFileDescriptor(), size);
    }

    public String getNotificationUri(String type) {
        int SunriseTimeHours = this.mAudioQueryWeatherService.getSunriseTimeHours();
        int SunriseTimeMins = this.mAudioQueryWeatherService.getSunriseTimeMins();
        int SunsetTimeHours = this.mAudioQueryWeatherService.getSunsetTimeHours();
        int SunsetTimeMins = this.mAudioQueryWeatherService.getSunsetTimeMins();
        AudioServiceInjector.setDefaultTimeZoneStatus(this.mAudioQueryWeatherService.getDefaultTimeZoneStatus());
        AudioServiceInjector.setSunriseAndSunsetTime(SunriseTimeHours, SunriseTimeMins, SunsetTimeHours, SunsetTimeMins);
        AudioServiceInjector.checkSunriseAndSunsetTimeUpdate(this.mContext);
        return AudioServiceInjector.getNotificationUri(type);
    }
}
