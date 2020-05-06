package com.android.server.hdmi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiHotplugEvent;
import android.hardware.hdmi.HdmiPortInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiInputChangeListener;
import android.hardware.hdmi.IHdmiMhlVendorCommandListener;
import android.hardware.hdmi.IHdmiRecordListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.hardware.hdmi.IHdmiVendorCommandListener;
import android.media.AudioManager;
import android.media.tv.TvInputManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import com.android.server.hdmi.HdmiAnnotations;
import com.android.server.hdmi.HdmiCecController;
import com.android.server.hdmi.HdmiCecLocalDevice;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import libcore.util.EmptyArray;

public class HdmiControlService extends SystemService {
    static final int INITIATED_BY_BOOT_UP = 1;
    static final int INITIATED_BY_ENABLE_CEC = 0;
    static final int INITIATED_BY_HOTPLUG = 4;
    static final int INITIATED_BY_SCREEN_ON = 2;
    static final int INITIATED_BY_WAKE_UP_MESSAGE = 3;
    static final String PERMISSION = "android.permission.HDMI_CEC";
    static final int STANDBY_SCREEN_OFF = 0;
    static final int STANDBY_SHUTDOWN = 1;
    private static final String TAG = "HdmiControlService";
    private static final boolean isHdmiCecNeverClaimPlaybackLogicAddr = SystemProperties.getBoolean("ro.hdmi.property_hdmi_cec_never_claim_playback_logical_address", false);
    /* access modifiers changed from: private */
    public static final Map<String, String> mTerminologyToBibliographicMap = new HashMap();
    /* access modifiers changed from: private */
    public final Locale HONG_KONG = new Locale("zh", "HK");
    /* access modifiers changed from: private */
    public final Locale MACAU = new Locale("zh", "MO");
    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public int mActivePortId = -1;
    @GuardedBy({"mLock"})
    protected final HdmiCecLocalDevice.ActiveSource mActiveSource = new HdmiCecLocalDevice.ActiveSource();
    /* access modifiers changed from: private */
    public boolean mAddressAllocated = false;
    /* access modifiers changed from: private */
    public HdmiCecController mCecController;
    /* access modifiers changed from: private */
    public final CecMessageBuffer mCecMessageBuffer = new CecMessageBuffer();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayList<DeviceEventListenerRecord> mDeviceEventListenerRecords = new ArrayList<>();
    private final Handler mHandler = new Handler();
    private final HdmiControlBroadcastReceiver mHdmiControlBroadcastReceiver = new HdmiControlBroadcastReceiver();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mHdmiControlEnabled;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayList<HotplugEventListenerRecord> mHotplugEventListenerRecords = new ArrayList<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public InputChangeListenerRecord mInputChangeListenerRecord;
    private Looper mIoLooper;
    private final HandlerThread mIoThread = new HandlerThread("Hdmi Control Io Thread");
    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public String mLanguage = Locale.getDefault().getISO3Language();
    @HdmiAnnotations.ServiceThreadOnly
    private int mLastInputMhl = -1;
    /* access modifiers changed from: private */
    public final List<Integer> mLocalDevices = getIntList(SystemProperties.get("ro.hdmi.device_type"));
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private HdmiCecMessageValidator mMessageValidator;
    /* access modifiers changed from: private */
    public HdmiMhlControllerStub mMhlController;
    @GuardedBy({"mLock"})
    private List<HdmiDeviceInfo> mMhlDevices;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mMhlInputChangeEnabled;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayList<HdmiMhlVendorCommandListenerRecord> mMhlVendorCommandListenerRecords = new ArrayList<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public int mPhysicalAddress = 65535;
    private UnmodifiableSparseArray<HdmiDeviceInfo> mPortDeviceMap;
    private UnmodifiableSparseIntArray mPortIdMap;
    /* access modifiers changed from: private */
    public List<HdmiPortInfo> mPortInfo;
    private UnmodifiableSparseArray<HdmiPortInfo> mPortInfoMap;
    private PowerManager mPowerManager;
    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public int mPowerStatus = 1;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mProhibitMode;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public HdmiRecordListenerRecord mRecordListenerRecord;
    /* access modifiers changed from: private */
    public final SelectRequestBuffer mSelectRequestBuffer = new SelectRequestBuffer();
    private final SettingsObserver mSettingsObserver = new SettingsObserver(this.mHandler);
    @HdmiAnnotations.ServiceThreadOnly
    private boolean mStandbyMessageReceived = false;
    @GuardedBy({"mLock"})
    private boolean mSystemAudioActivated = false;
    /* access modifiers changed from: private */
    public final ArrayList<SystemAudioModeChangeListenerRecord> mSystemAudioModeChangeListenerRecords = new ArrayList<>();
    private TvInputManager mTvInputManager;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final ArrayList<VendorCommandListenerRecord> mVendorCommandListenerRecords = new ArrayList<>();
    @HdmiAnnotations.ServiceThreadOnly
    private boolean mWakeUpMessageReceived = false;

    interface DevicePollingCallback {
        void onPollingFinished(List<Integer> list);
    }

    interface SendMessageCallback {
        void onSendCompleted(int i);
    }

    static {
        mTerminologyToBibliographicMap.put("sqi", "alb");
        mTerminologyToBibliographicMap.put("hye", "arm");
        mTerminologyToBibliographicMap.put("eus", "baq");
        mTerminologyToBibliographicMap.put("mya", "bur");
        mTerminologyToBibliographicMap.put("ces", "cze");
        mTerminologyToBibliographicMap.put("nld", "dut");
        mTerminologyToBibliographicMap.put("kat", "geo");
        mTerminologyToBibliographicMap.put("deu", "ger");
        mTerminologyToBibliographicMap.put("ell", "gre");
        mTerminologyToBibliographicMap.put("fra", "fre");
        mTerminologyToBibliographicMap.put("isl", "ice");
        mTerminologyToBibliographicMap.put("mkd", "mac");
        mTerminologyToBibliographicMap.put("mri", "mao");
        mTerminologyToBibliographicMap.put("msa", "may");
        mTerminologyToBibliographicMap.put("fas", "per");
        mTerminologyToBibliographicMap.put("ron", "rum");
        mTerminologyToBibliographicMap.put("slk", "slo");
        mTerminologyToBibliographicMap.put("bod", "tib");
        mTerminologyToBibliographicMap.put("cym", "wel");
    }

    private class HdmiControlBroadcastReceiver extends BroadcastReceiver {
        private HdmiControlBroadcastReceiver() {
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        @com.android.server.hdmi.HdmiAnnotations.ServiceThreadOnly
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r8, android.content.Intent r9) {
            /*
                r7 = this;
                com.android.server.hdmi.HdmiControlService r0 = com.android.server.hdmi.HdmiControlService.this
                r0.assertRunOnServiceThread()
                java.lang.String r0 = "sys.shutdown.requested"
                java.lang.String r0 = android.os.SystemProperties.get(r0)
                java.lang.String r1 = "1"
                boolean r0 = r0.contains(r1)
                java.lang.String r1 = r9.getAction()
                int r2 = r1.hashCode()
                r3 = 0
                r4 = 3
                r5 = 2
                r6 = 1
                switch(r2) {
                    case -2128145023: goto L_0x0040;
                    case -1454123155: goto L_0x0036;
                    case 158859398: goto L_0x002c;
                    case 1947666138: goto L_0x0022;
                    default: goto L_0x0021;
                }
            L_0x0021:
                goto L_0x004a
            L_0x0022:
                java.lang.String r2 = "android.intent.action.ACTION_SHUTDOWN"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = r4
                goto L_0x004b
            L_0x002c:
                java.lang.String r2 = "android.intent.action.CONFIGURATION_CHANGED"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = r5
                goto L_0x004b
            L_0x0036:
                java.lang.String r2 = "android.intent.action.SCREEN_ON"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = r6
                goto L_0x004b
            L_0x0040:
                java.lang.String r2 = "android.intent.action.SCREEN_OFF"
                boolean r1 = r1.equals(r2)
                if (r1 == 0) goto L_0x0021
                r1 = r3
                goto L_0x004b
            L_0x004a:
                r1 = -1
            L_0x004b:
                if (r1 == 0) goto L_0x0088
                if (r1 == r6) goto L_0x007a
                if (r1 == r5) goto L_0x0064
                if (r1 == r4) goto L_0x0054
                goto L_0x0097
            L_0x0054:
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                boolean r1 = r1.isPowerOnOrTransient()
                if (r1 == 0) goto L_0x0097
                if (r0 != 0) goto L_0x0097
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                r1.onStandby(r6)
                goto L_0x0097
            L_0x0064:
                java.lang.String r1 = r7.getMenuLanguage()
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                java.lang.String r2 = r2.mLanguage
                boolean r2 = r2.equals(r1)
                if (r2 != 0) goto L_0x0097
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                r2.onLanguageChanged(r1)
                goto L_0x0097
            L_0x007a:
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                boolean r1 = r1.isPowerStandbyOrTransient()
                if (r1 == 0) goto L_0x0097
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                r1.onWakeUp()
                goto L_0x0097
            L_0x0088:
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                boolean r1 = r1.isPowerOnOrTransient()
                if (r1 == 0) goto L_0x0097
                if (r0 != 0) goto L_0x0097
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                r1.onStandby(r3)
            L_0x0097:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.hdmi.HdmiControlService.HdmiControlBroadcastReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }

        private String getMenuLanguage() {
            Locale locale = Locale.getDefault();
            if (locale.equals(Locale.TAIWAN) || locale.equals(HdmiControlService.this.HONG_KONG) || locale.equals(HdmiControlService.this.MACAU)) {
                return "chi";
            }
            String language = locale.getISO3Language();
            if (HdmiControlService.mTerminologyToBibliographicMap.containsKey(language)) {
                return (String) HdmiControlService.mTerminologyToBibliographicMap.get(language);
            }
            return language;
        }
    }

    private final class CecMessageBuffer {
        private List<HdmiCecMessage> mBuffer;

        private CecMessageBuffer() {
            this.mBuffer = new ArrayList();
        }

        public boolean bufferMessage(HdmiCecMessage message) {
            int opcode = message.getOpcode();
            if (opcode == 4 || opcode == 13) {
                bufferImageOrTextViewOn(message);
                return true;
            } else if (opcode != 130) {
                return false;
            } else {
                bufferActiveSource(message);
                return true;
            }
        }

        public void processMessages() {
            for (final HdmiCecMessage message : this.mBuffer) {
                HdmiControlService.this.runOnServiceThread(new Runnable() {
                    public void run() {
                        HdmiControlService.this.handleCecCommand(message);
                    }
                });
            }
            this.mBuffer.clear();
        }

        private void bufferActiveSource(HdmiCecMessage message) {
            if (!replaceMessageIfBuffered(message, 130)) {
                this.mBuffer.add(message);
            }
        }

        private void bufferImageOrTextViewOn(HdmiCecMessage message) {
            if (!replaceMessageIfBuffered(message, 4) && !replaceMessageIfBuffered(message, 13)) {
                this.mBuffer.add(message);
            }
        }

        private boolean replaceMessageIfBuffered(HdmiCecMessage message, int opcode) {
            for (int i = 0; i < this.mBuffer.size(); i++) {
                if (this.mBuffer.get(i).getOpcode() == opcode) {
                    this.mBuffer.set(i, message);
                    return true;
                }
            }
            return false;
        }
    }

    public HdmiControlService(Context context) {
        super(context);
    }

    protected static List<Integer> getIntList(String string) {
        ArrayList<Integer> list = new ArrayList<>();
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(',');
        splitter.setString(string);
        Iterator<String> it = splitter.iterator();
        while (it.hasNext()) {
            String item = it.next();
            try {
                list.add(Integer.valueOf(Integer.parseInt(item)));
            } catch (NumberFormatException e) {
                Slog.w(TAG, "Can't parseInt: " + item);
            }
        }
        return Collections.unmodifiableList(list);
    }

    /* JADX WARNING: type inference failed for: r0v8, types: [android.os.IBinder, com.android.server.hdmi.HdmiControlService$BinderService] */
    public void onStart() {
        if (this.mIoLooper == null) {
            this.mIoThread.start();
            this.mIoLooper = this.mIoThread.getLooper();
        }
        this.mPowerStatus = 2;
        this.mProhibitMode = false;
        this.mHdmiControlEnabled = readBooleanSetting("hdmi_control_enabled", true);
        this.mMhlInputChangeEnabled = readBooleanSetting("mhl_input_switching_enabled", true);
        if (this.mCecController == null) {
            this.mCecController = HdmiCecController.create(this);
        }
        HdmiCecController hdmiCecController = this.mCecController;
        if (hdmiCecController != null) {
            if (this.mHdmiControlEnabled) {
                initializeCec(1);
            } else {
                hdmiCecController.setOption(2, false);
            }
            if (this.mMhlController == null) {
                this.mMhlController = HdmiMhlControllerStub.create(this);
            }
            if (!this.mMhlController.isReady()) {
                Slog.i(TAG, "Device does not support MHL-control.");
            }
            this.mMhlDevices = Collections.emptyList();
            initPortInfo();
            if (this.mMessageValidator == null) {
                this.mMessageValidator = new HdmiCecMessageValidator(this);
            }
            publishBinderService("hdmi_control", new BinderService());
            if (this.mCecController != null) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.SCREEN_OFF");
                filter.addAction("android.intent.action.SCREEN_ON");
                filter.addAction("android.intent.action.ACTION_SHUTDOWN");
                filter.addAction("android.intent.action.CONFIGURATION_CHANGED");
                getContext().registerReceiver(this.mHdmiControlBroadcastReceiver, filter);
                registerContentObserver();
            }
            this.mMhlController.setOption(HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION, 1);
            return;
        }
        Slog.i(TAG, "Device does not support HDMI-CEC.");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setCecController(HdmiCecController cecController) {
        this.mCecController = cecController;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setHdmiMhlController(HdmiMhlControllerStub hdmiMhlController) {
        this.mMhlController = hdmiMhlController;
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            this.mTvInputManager = (TvInputManager) getContext().getSystemService("tv_input");
            this.mPowerManager = (PowerManager) getContext().getSystemService("power");
        }
    }

    /* access modifiers changed from: package-private */
    public TvInputManager getTvInputManager() {
        return this.mTvInputManager;
    }

    /* access modifiers changed from: package-private */
    public void registerTvInputCallback(TvInputManager.TvInputCallback callback) {
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager != null) {
            tvInputManager.registerCallback(callback, this.mHandler);
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterTvInputCallback(TvInputManager.TvInputCallback callback) {
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager != null) {
            tvInputManager.unregisterCallback(callback);
        }
    }

    /* access modifiers changed from: package-private */
    public PowerManager getPowerManager() {
        return this.mPowerManager;
    }

    /* access modifiers changed from: private */
    public void onInitializeCecComplete(int initiatedBy) {
        if (this.mPowerStatus == 2) {
            this.mPowerStatus = 0;
        }
        this.mWakeUpMessageReceived = false;
        if (isTvDeviceEnabled()) {
            this.mCecController.setOption(1, tv().getAutoWakeup());
        }
        int reason = -1;
        if (initiatedBy == 0) {
            reason = 1;
        } else if (initiatedBy == 1) {
            reason = 0;
        } else if (initiatedBy == 2 || initiatedBy == 3) {
            reason = 2;
        }
        if (reason != -1) {
            invokeVendorCommandListenersOnControlStateChanged(true, reason);
        }
    }

    private void registerContentObserver() {
        ContentResolver resolver = getContext().getContentResolver();
        for (String s : new String[]{"hdmi_control_enabled", "hdmi_control_auto_wakeup_enabled", "hdmi_control_auto_device_off_enabled", "hdmi_system_audio_control_enabled", "mhl_input_switching_enabled", "mhl_power_charge_enabled", "hdmi_cec_switch_enabled", "device_name"}) {
            resolver.registerContentObserver(Settings.Global.getUriFor(s), false, this.mSettingsObserver, -1);
        }
    }

    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r6, android.net.Uri r7) {
            /*
                r5 = this;
                java.lang.String r0 = r7.getLastPathSegment()
                com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this
                r2 = 1
                boolean r1 = r1.readBooleanSetting(r0, r2)
                int r3 = r0.hashCode()
                switch(r3) {
                    case -2009736264: goto L_0x005f;
                    case -1573020421: goto L_0x0054;
                    case -1543071020: goto L_0x004a;
                    case -1489007315: goto L_0x003f;
                    case -1262529811: goto L_0x0034;
                    case -885757826: goto L_0x0029;
                    case 726613192: goto L_0x001e;
                    case 1628046095: goto L_0x0013;
                    default: goto L_0x0012;
                }
            L_0x0012:
                goto L_0x006a
            L_0x0013:
                java.lang.String r3 = "hdmi_control_auto_device_off_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 2
                goto L_0x006b
            L_0x001e:
                java.lang.String r3 = "hdmi_control_auto_wakeup_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = r2
                goto L_0x006b
            L_0x0029:
                java.lang.String r3 = "mhl_power_charge_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 6
                goto L_0x006b
            L_0x0034:
                java.lang.String r3 = "mhl_input_switching_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 5
                goto L_0x006b
            L_0x003f:
                java.lang.String r3 = "hdmi_system_audio_control_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 3
                goto L_0x006b
            L_0x004a:
                java.lang.String r3 = "device_name"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 7
                goto L_0x006b
            L_0x0054:
                java.lang.String r3 = "hdmi_cec_switch_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 4
                goto L_0x006b
            L_0x005f:
                java.lang.String r3 = "hdmi_control_enabled"
                boolean r3 = r0.equals(r3)
                if (r3 == 0) goto L_0x0012
                r3 = 0
                goto L_0x006b
            L_0x006a:
                r3 = -1
            L_0x006b:
                java.lang.String r4 = "HdmiControlService"
                switch(r3) {
                    case 0: goto L_0x012e;
                    case 1: goto L_0x0117;
                    case 2: goto L_0x00ec;
                    case 3: goto L_0x00bb;
                    case 4: goto L_0x0099;
                    case 5: goto L_0x0092;
                    case 6: goto L_0x0081;
                    case 7: goto L_0x0072;
                    default: goto L_0x0070;
                }
            L_0x0070:
                goto L_0x0134
            L_0x0072:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                java.lang.String r3 = android.os.Build.MODEL
                java.lang.String r2 = r2.readStringSetting(r0, r3)
                com.android.server.hdmi.HdmiControlService r3 = com.android.server.hdmi.HdmiControlService.this
                r3.setDisplayName(r2)
                goto L_0x0134
            L_0x0081:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiMhlControllerStub r2 = r2.mMhlController
                r3 = 102(0x66, float:1.43E-43)
                int r4 = com.android.server.hdmi.HdmiControlService.toInt(r1)
                r2.setOption(r3, r4)
                goto L_0x0134
            L_0x0092:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                r2.setMhlInputChangeEnabled(r1)
                goto L_0x0134
            L_0x0099:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                boolean r2 = r2.isAudioSystemDevice()
                if (r2 == 0) goto L_0x0134
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem r2 = r2.audioSystem()
                if (r2 != 0) goto L_0x00b0
                java.lang.String r2 = "Switch device has not registered yet. Can't turn routing on."
                android.util.Slog.w(r4, r2)
                goto L_0x0134
            L_0x00b0:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem r2 = r2.audioSystem()
                r2.setRoutingControlFeatureEnables(r1)
                goto L_0x0134
            L_0x00bb:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                boolean r2 = r2.isTvDeviceEnabled()
                if (r2 == 0) goto L_0x00cc
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecLocalDeviceTv r2 = r2.tv()
                r2.setSystemAudioControlFeatureEnabled(r1)
            L_0x00cc:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                boolean r2 = r2.isAudioSystemDevice()
                if (r2 == 0) goto L_0x0134
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem r2 = r2.audioSystem()
                if (r2 != 0) goto L_0x00e2
                java.lang.String r2 = "Audio System device has not registered yet. Can't turn system audio mode on."
                android.util.Slog.e(r4, r2)
                goto L_0x0134
            L_0x00e2:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem r2 = r2.audioSystem()
                r2.onSystemAduioControlFeatureSupportChanged(r1)
                goto L_0x0134
            L_0x00ec:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                java.util.List r2 = r2.mLocalDevices
                java.util.Iterator r2 = r2.iterator()
            L_0x00f6:
                boolean r3 = r2.hasNext()
                if (r3 == 0) goto L_0x0116
                java.lang.Object r3 = r2.next()
                java.lang.Integer r3 = (java.lang.Integer) r3
                int r3 = r3.intValue()
                com.android.server.hdmi.HdmiControlService r4 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecController r4 = r4.mCecController
                com.android.server.hdmi.HdmiCecLocalDevice r4 = r4.getLocalDevice(r3)
                if (r4 == 0) goto L_0x0115
                r4.setAutoDeviceOff(r1)
            L_0x0115:
                goto L_0x00f6
            L_0x0116:
                goto L_0x0134
            L_0x0117:
                com.android.server.hdmi.HdmiControlService r3 = com.android.server.hdmi.HdmiControlService.this
                boolean r3 = r3.isTvDeviceEnabled()
                if (r3 == 0) goto L_0x0128
                com.android.server.hdmi.HdmiControlService r3 = com.android.server.hdmi.HdmiControlService.this
                com.android.server.hdmi.HdmiCecLocalDeviceTv r3 = r3.tv()
                r3.setAutoWakeup(r1)
            L_0x0128:
                com.android.server.hdmi.HdmiControlService r3 = com.android.server.hdmi.HdmiControlService.this
                r3.setCecOption(r2, r1)
                goto L_0x0134
            L_0x012e:
                com.android.server.hdmi.HdmiControlService r2 = com.android.server.hdmi.HdmiControlService.this
                r2.setControlEnabled(r1)
            L_0x0134:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.hdmi.HdmiControlService.SettingsObserver.onChange(boolean, android.net.Uri):void");
        }
    }

    /* access modifiers changed from: private */
    public static int toInt(boolean enabled) {
        return enabled;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean readBooleanSetting(String key, boolean defVal) {
        return Settings.Global.getInt(getContext().getContentResolver(), key, toInt(defVal)) == 1;
    }

    /* access modifiers changed from: package-private */
    public void writeBooleanSetting(String key, boolean value) {
        Settings.Global.putInt(getContext().getContentResolver(), key, toInt(value));
    }

    /* access modifiers changed from: package-private */
    public void writeStringSystemProperty(String key, String value) {
        SystemProperties.set(key, value);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean readBooleanSystemProperty(String key, boolean defVal) {
        return SystemProperties.getBoolean(key, defVal);
    }

    /* access modifiers changed from: package-private */
    public String readStringSetting(String key, String defVal) {
        String content = Settings.Global.getString(getContext().getContentResolver(), key);
        if (TextUtils.isEmpty(content)) {
            return defVal;
        }
        return content;
    }

    private void initializeCec(int initiatedBy) {
        this.mAddressAllocated = false;
        this.mCecController.setOption(3, true);
        this.mCecController.setLanguage(this.mLanguage);
        initializeLocalDevices(initiatedBy);
    }

    @HdmiAnnotations.ServiceThreadOnly
    private void initializeLocalDevices(int initiatedBy) {
        assertRunOnServiceThread();
        ArrayList<HdmiCecLocalDevice> localDevices = new ArrayList<>();
        for (Integer intValue : this.mLocalDevices) {
            int type = intValue.intValue();
            if (type != 4 || !isHdmiCecNeverClaimPlaybackLogicAddr) {
                HdmiCecLocalDevice localDevice = this.mCecController.getLocalDevice(type);
                if (localDevice == null) {
                    localDevice = HdmiCecLocalDevice.create(this, type);
                }
                localDevice.init();
                localDevices.add(localDevice);
            }
        }
        clearLocalDevices();
        allocateLogicalAddress(localDevices, initiatedBy);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    @HdmiAnnotations.ServiceThreadOnly
    public void allocateLogicalAddress(ArrayList<HdmiCecLocalDevice> allocatingDevices, int initiatedBy) {
        assertRunOnServiceThread();
        this.mCecController.clearLogicalAddress();
        final ArrayList<HdmiCecLocalDevice> allocatedDevices = new ArrayList<>();
        int[] finished = new int[1];
        this.mAddressAllocated = allocatingDevices.isEmpty();
        this.mSelectRequestBuffer.clear();
        Iterator<HdmiCecLocalDevice> it = allocatingDevices.iterator();
        while (it.hasNext()) {
            HdmiCecLocalDevice localDevice = it.next();
            final HdmiCecLocalDevice hdmiCecLocalDevice = localDevice;
            final ArrayList<HdmiCecLocalDevice> arrayList = allocatingDevices;
            final int[] iArr = finished;
            final int i = initiatedBy;
            this.mCecController.allocateLogicalAddress(localDevice.getType(), localDevice.getPreferredAddress(), new HdmiCecController.AllocateAddressCallback() {
                public void onAllocated(int deviceType, int logicalAddress) {
                    if (logicalAddress == 15) {
                        Slog.e(HdmiControlService.TAG, "Failed to allocate address:[device_type:" + deviceType + "]");
                    } else {
                        hdmiCecLocalDevice.setDeviceInfo(HdmiControlService.this.createDeviceInfo(logicalAddress, deviceType, 0));
                        HdmiControlService.this.mCecController.addLocalDevice(deviceType, hdmiCecLocalDevice);
                        HdmiControlService.this.mCecController.addLogicalAddress(logicalAddress);
                        allocatedDevices.add(hdmiCecLocalDevice);
                    }
                    int size = arrayList.size();
                    int[] iArr = iArr;
                    int i = iArr[0] + 1;
                    iArr[0] = i;
                    if (size == i) {
                        boolean unused = HdmiControlService.this.mAddressAllocated = true;
                        int i2 = i;
                        if (i2 != 4) {
                            HdmiControlService.this.onInitializeCecComplete(i2);
                        }
                        HdmiControlService.this.notifyAddressAllocated(allocatedDevices, i);
                        HdmiControlService.this.mCecMessageBuffer.processMessages();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void notifyAddressAllocated(ArrayList<HdmiCecLocalDevice> devices, int initiatedBy) {
        assertRunOnServiceThread();
        Iterator<HdmiCecLocalDevice> it = devices.iterator();
        while (it.hasNext()) {
            HdmiCecLocalDevice device = it.next();
            device.handleAddressAllocated(device.getDeviceInfo().getLogicalAddress(), initiatedBy);
        }
        if (isTvDeviceEnabled()) {
            tv().setSelectRequestBuffer(this.mSelectRequestBuffer);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAddressAllocated() {
        return this.mAddressAllocated;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    @HdmiAnnotations.ServiceThreadOnly
    public void initPortInfo() {
        assertRunOnServiceThread();
        HdmiPortInfo[] cecPortInfo = null;
        synchronized (this.mLock) {
            this.mPhysicalAddress = getPhysicalAddress();
        }
        HdmiCecController hdmiCecController = this.mCecController;
        if (hdmiCecController != null) {
            cecPortInfo = hdmiCecController.getPortInfos();
        }
        if (cecPortInfo != null) {
            SparseArray<HdmiPortInfo> portInfoMap = new SparseArray<>();
            SparseIntArray portIdMap = new SparseIntArray();
            SparseArray<HdmiDeviceInfo> portDeviceMap = new SparseArray<>();
            for (HdmiPortInfo info : cecPortInfo) {
                portIdMap.put(info.getAddress(), info.getId());
                portInfoMap.put(info.getId(), info);
                portDeviceMap.put(info.getId(), new HdmiDeviceInfo(info.getAddress(), info.getId()));
            }
            this.mPortIdMap = new UnmodifiableSparseIntArray(portIdMap);
            this.mPortInfoMap = new UnmodifiableSparseArray<>(portInfoMap);
            this.mPortDeviceMap = new UnmodifiableSparseArray<>(portDeviceMap);
            HdmiMhlControllerStub hdmiMhlControllerStub = this.mMhlController;
            if (hdmiMhlControllerStub != null) {
                HdmiPortInfo[] mhlPortInfo = hdmiMhlControllerStub.getPortInfos();
                ArraySet<Integer> mhlSupportedPorts = new ArraySet<>(mhlPortInfo.length);
                for (HdmiPortInfo info2 : mhlPortInfo) {
                    if (info2.isMhlSupported()) {
                        mhlSupportedPorts.add(Integer.valueOf(info2.getId()));
                    }
                }
                if (mhlSupportedPorts.isEmpty()) {
                    this.mPortInfo = Collections.unmodifiableList(Arrays.asList(cecPortInfo));
                    return;
                }
                ArrayList<HdmiPortInfo> result = new ArrayList<>(cecPortInfo.length);
                for (HdmiPortInfo info3 : cecPortInfo) {
                    if (mhlSupportedPorts.contains(Integer.valueOf(info3.getId()))) {
                        result.add(new HdmiPortInfo(info3.getId(), info3.getType(), info3.getAddress(), info3.isCecSupported(), true, info3.isArcSupported()));
                    } else {
                        result.add(info3);
                    }
                }
                this.mPortInfo = Collections.unmodifiableList(result);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public List<HdmiPortInfo> getPortInfo() {
        return this.mPortInfo;
    }

    /* access modifiers changed from: package-private */
    public HdmiPortInfo getPortInfo(int portId) {
        return this.mPortInfoMap.get(portId, null);
    }

    /* access modifiers changed from: package-private */
    public int portIdToPath(int portId) {
        HdmiPortInfo portInfo = getPortInfo(portId);
        if (portInfo != null) {
            return portInfo.getAddress();
        }
        Slog.e(TAG, "Cannot find the port info: " + portId);
        return 65535;
    }

    /* access modifiers changed from: package-private */
    public int pathToPortId(int path) {
        int physicalAddress;
        int mask = 61440;
        int finalMask = 61440;
        synchronized (this.mLock) {
            physicalAddress = this.mPhysicalAddress;
        }
        int maskedAddress = physicalAddress;
        while (maskedAddress != 0) {
            maskedAddress = physicalAddress & mask;
            finalMask |= mask;
            mask >>= 4;
        }
        return this.mPortIdMap.get(path & finalMask, -1);
    }

    /* access modifiers changed from: package-private */
    public boolean isValidPortId(int portId) {
        return getPortInfo(portId) != null;
    }

    /* access modifiers changed from: package-private */
    public Looper getIoLooper() {
        return this.mIoLooper;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setIoLooper(Looper ioLooper) {
        this.mIoLooper = ioLooper;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setMessageValidator(HdmiCecMessageValidator messageValidator) {
        this.mMessageValidator = messageValidator;
    }

    /* access modifiers changed from: package-private */
    public Looper getServiceLooper() {
        return this.mHandler.getLooper();
    }

    /* access modifiers changed from: package-private */
    public int getPhysicalAddress() {
        return this.mCecController.getPhysicalAddress();
    }

    /* access modifiers changed from: package-private */
    public int getVendorId() {
        return this.mCecController.getVendorId();
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public HdmiDeviceInfo getDeviceInfo(int logicalAddress) {
        assertRunOnServiceThread();
        if (tv() == null) {
            return null;
        }
        return tv().getCecDeviceInfo(logicalAddress);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public HdmiDeviceInfo getDeviceInfoByPort(int port) {
        assertRunOnServiceThread();
        HdmiMhlLocalDeviceStub info = this.mMhlController.getLocalDevice(port);
        if (info != null) {
            return info.getInfo();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getCecVersion() {
        return this.mCecController.getVersion();
    }

    /* access modifiers changed from: package-private */
    public boolean isConnectedToArcPort(int physicalAddress) {
        int portId = pathToPortId(physicalAddress);
        if (portId != -1) {
            return this.mPortInfoMap.get(portId).isArcSupported();
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public boolean isConnected(int portId) {
        assertRunOnServiceThread();
        return this.mCecController.isConnected(portId);
    }

    /* access modifiers changed from: package-private */
    public void runOnServiceThread(Runnable runnable) {
        this.mHandler.post(runnable);
    }

    /* access modifiers changed from: package-private */
    public void runOnServiceThreadAtFrontOfQueue(Runnable runnable) {
        this.mHandler.postAtFrontOfQueue(runnable);
    }

    /* access modifiers changed from: private */
    public void assertRunOnServiceThread() {
        if (Looper.myLooper() != this.mHandler.getLooper()) {
            throw new IllegalStateException("Should run on service thread.");
        }
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void sendCecCommand(HdmiCecMessage command, SendMessageCallback callback) {
        assertRunOnServiceThread();
        if (this.mMessageValidator.isValid(command) == 0) {
            this.mCecController.sendCommand(command, callback);
            return;
        }
        HdmiLogger.error("Invalid message type:" + command, new Object[0]);
        if (callback != null) {
            callback.onSendCompleted(3);
        }
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void sendCecCommand(HdmiCecMessage command) {
        assertRunOnServiceThread();
        sendCecCommand(command, (SendMessageCallback) null);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void maySendFeatureAbortCommand(HdmiCecMessage command, int reason) {
        assertRunOnServiceThread();
        this.mCecController.maySendFeatureAbortCommand(command, reason);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public boolean handleCecCommand(HdmiCecMessage message) {
        assertRunOnServiceThread();
        int errorCode = this.mMessageValidator.isValid(message);
        if (errorCode != 0) {
            if (errorCode == 3) {
                maySendFeatureAbortCommand(message, 3);
            }
            return true;
        } else if (dispatchMessageToLocalDevice(message)) {
            return true;
        } else {
            if (!this.mAddressAllocated) {
                return this.mCecMessageBuffer.bufferMessage(message);
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void enableAudioReturnChannel(int portId, boolean enabled) {
        this.mCecController.enableAudioReturnChannel(portId, enabled);
    }

    @HdmiAnnotations.ServiceThreadOnly
    private boolean dispatchMessageToLocalDevice(HdmiCecMessage message) {
        assertRunOnServiceThread();
        for (HdmiCecLocalDevice device : this.mCecController.getLocalDeviceList()) {
            if (device.dispatchMessage(message) && message.getDestination() != 15) {
                return true;
            }
        }
        if (message.getDestination() != 15) {
            HdmiLogger.warning("Unhandled cec command:" + message, new Object[0]);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void onHotplug(int portId, boolean connected) {
        assertRunOnServiceThread();
        if (connected && !isTvDevice() && getPortInfo(portId).getType() == 1) {
            if (isSwitchDevice()) {
                initPortInfo();
                HdmiLogger.debug("initPortInfo for switch device when onHotplug from tx.", new Object[0]);
            }
            ArrayList<HdmiCecLocalDevice> localDevices = new ArrayList<>();
            for (Integer intValue : this.mLocalDevices) {
                int type = intValue.intValue();
                if (type != 4 || !isHdmiCecNeverClaimPlaybackLogicAddr) {
                    HdmiCecLocalDevice localDevice = this.mCecController.getLocalDevice(type);
                    if (localDevice == null) {
                        localDevice = HdmiCecLocalDevice.create(this, type);
                        localDevice.init();
                    }
                    localDevices.add(localDevice);
                }
            }
            allocateLogicalAddress(localDevices, 4);
        }
        for (HdmiCecLocalDevice device : this.mCecController.getLocalDeviceList()) {
            device.onHotplug(portId, connected);
        }
        announceHotplugEvent(portId, connected);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void pollDevices(DevicePollingCallback callback, int sourceAddress, int pickStrategy, int retryCount) {
        assertRunOnServiceThread();
        this.mCecController.pollDevices(callback, sourceAddress, checkPollStrategy(pickStrategy), retryCount);
    }

    private int checkPollStrategy(int pickStrategy) {
        int strategy = pickStrategy & 3;
        if (strategy != 0) {
            int iterationStrategy = 196608 & pickStrategy;
            if (iterationStrategy != 0) {
                return strategy | iterationStrategy;
            }
            throw new IllegalArgumentException("Invalid iteration strategy:" + pickStrategy);
        }
        throw new IllegalArgumentException("Invalid poll strategy:" + pickStrategy);
    }

    /* access modifiers changed from: package-private */
    public List<HdmiCecLocalDevice> getAllLocalDevices() {
        assertRunOnServiceThread();
        return this.mCecController.getLocalDeviceList();
    }

    /* access modifiers changed from: package-private */
    public Object getServiceLock() {
        return this.mLock;
    }

    /* access modifiers changed from: package-private */
    public void setAudioStatus(boolean mute, int volume) {
        if (isTvDeviceEnabled() && tv().isSystemAudioActivated()) {
            AudioManager audioManager = getAudioManager();
            boolean muted = audioManager.isStreamMute(3);
            if (!mute) {
                if (muted) {
                    audioManager.setStreamMute(3, false);
                }
                if (volume >= 0 && volume <= 100) {
                    Slog.i(TAG, "volume: " + volume);
                    audioManager.setStreamVolume(3, volume, 1 | 256);
                }
            } else if (!muted) {
                audioManager.setStreamMute(3, true);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void announceSystemAudioModeChange(boolean enabled) {
        synchronized (this.mLock) {
            Iterator<SystemAudioModeChangeListenerRecord> it = this.mSystemAudioModeChangeListenerRecords.iterator();
            while (it.hasNext()) {
                invokeSystemAudioModeChangeLocked(it.next().mListener, enabled);
            }
        }
    }

    /* access modifiers changed from: private */
    public HdmiDeviceInfo createDeviceInfo(int logicalAddress, int deviceType, int powerStatus) {
        return new HdmiDeviceInfo(logicalAddress, getPhysicalAddress(), pathToPortId(getPhysicalAddress()), deviceType, getVendorId(), readStringSetting("device_name", Build.MODEL), powerStatus);
    }

    /* access modifiers changed from: private */
    public void setDisplayName(String newDisplayName) {
        for (HdmiCecLocalDevice device : getAllLocalDevices()) {
            HdmiDeviceInfo deviceInfo = device.getDeviceInfo();
            if (!deviceInfo.getDisplayName().equals(newDisplayName)) {
                device.setDeviceInfo(new HdmiDeviceInfo(deviceInfo.getLogicalAddress(), deviceInfo.getPhysicalAddress(), deviceInfo.getPortId(), deviceInfo.getDeviceType(), deviceInfo.getVendorId(), newDisplayName, deviceInfo.getDevicePowerStatus()));
                sendCecCommand(HdmiCecMessageBuilder.buildSetOsdNameCommand(device.mAddress, 0, newDisplayName));
            }
        }
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void handleMhlHotplugEvent(int portId, boolean connected) {
        assertRunOnServiceThread();
        if (connected) {
            HdmiMhlLocalDeviceStub newDevice = new HdmiMhlLocalDeviceStub(this, portId);
            HdmiMhlLocalDeviceStub oldDevice = this.mMhlController.addLocalDevice(newDevice);
            if (oldDevice != null) {
                oldDevice.onDeviceRemoved();
                Slog.i(TAG, "Old device of port " + portId + " is removed");
            }
            invokeDeviceEventListeners(newDevice.getInfo(), 1);
            updateSafeMhlInput();
        } else {
            HdmiMhlLocalDeviceStub device = this.mMhlController.removeLocalDevice(portId);
            if (device != null) {
                device.onDeviceRemoved();
                invokeDeviceEventListeners(device.getInfo(), 2);
                updateSafeMhlInput();
            } else {
                Slog.w(TAG, "No device to remove:[portId=" + portId);
            }
        }
        announceHotplugEvent(portId, connected);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void handleMhlBusModeChanged(int portId, int busmode) {
        assertRunOnServiceThread();
        HdmiMhlLocalDeviceStub device = this.mMhlController.getLocalDevice(portId);
        if (device != null) {
            device.setBusMode(busmode);
            return;
        }
        Slog.w(TAG, "No mhl device exists for bus mode change[portId:" + portId + ", busmode:" + busmode + "]");
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void handleMhlBusOvercurrent(int portId, boolean on) {
        assertRunOnServiceThread();
        HdmiMhlLocalDeviceStub device = this.mMhlController.getLocalDevice(portId);
        if (device != null) {
            device.onBusOvercurrentDetected(on);
            return;
        }
        Slog.w(TAG, "No mhl device exists for bus overcurrent event[portId:" + portId + "]");
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void handleMhlDeviceStatusChanged(int portId, int adopterId, int deviceId) {
        assertRunOnServiceThread();
        HdmiMhlLocalDeviceStub device = this.mMhlController.getLocalDevice(portId);
        if (device != null) {
            device.setDeviceStatusChange(adopterId, deviceId);
            return;
        }
        Slog.w(TAG, "No mhl device exists for device status event[portId:" + portId + ", adopterId:" + adopterId + ", deviceId:" + deviceId + "]");
    }

    @HdmiAnnotations.ServiceThreadOnly
    private void updateSafeMhlInput() {
        assertRunOnServiceThread();
        List<HdmiDeviceInfo> inputs = Collections.emptyList();
        SparseArray<HdmiMhlLocalDeviceStub> devices = this.mMhlController.getAllLocalDevices();
        for (int i = 0; i < devices.size(); i++) {
            HdmiMhlLocalDeviceStub device = devices.valueAt(i);
            if (device.getInfo() != null) {
                if (inputs.isEmpty()) {
                    inputs = new ArrayList<>();
                }
                inputs.add(device.getInfo());
            }
        }
        synchronized (this.mLock) {
            this.mMhlDevices = inputs;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public List<HdmiDeviceInfo> getMhlDevicesLocked() {
        return this.mMhlDevices;
    }

    private class HdmiMhlVendorCommandListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final IHdmiMhlVendorCommandListener mListener;

        public HdmiMhlVendorCommandListenerRecord(IHdmiMhlVendorCommandListener listener) {
            this.mListener = listener;
        }

        public void binderDied() {
            HdmiControlService.this.mMhlVendorCommandListenerRecords.remove(this);
        }
    }

    private final class HotplugEventListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final IHdmiHotplugEventListener mListener;

        public HotplugEventListenerRecord(IHdmiHotplugEventListener listener) {
            this.mListener = listener;
        }

        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mHotplugEventListenerRecords.remove(this);
            }
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof HotplugEventListenerRecord)) {
                return false;
            }
            if (obj == this || ((HotplugEventListenerRecord) obj).mListener == this.mListener) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return this.mListener.hashCode();
        }
    }

    private final class DeviceEventListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final IHdmiDeviceEventListener mListener;

        public DeviceEventListenerRecord(IHdmiDeviceEventListener listener) {
            this.mListener = listener;
        }

        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mDeviceEventListenerRecords.remove(this);
            }
        }
    }

    private final class SystemAudioModeChangeListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final IHdmiSystemAudioModeChangeListener mListener;

        public SystemAudioModeChangeListenerRecord(IHdmiSystemAudioModeChangeListener listener) {
            this.mListener = listener;
        }

        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mSystemAudioModeChangeListenerRecords.remove(this);
            }
        }
    }

    class VendorCommandListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final int mDeviceType;
        /* access modifiers changed from: private */
        public final IHdmiVendorCommandListener mListener;

        public VendorCommandListenerRecord(IHdmiVendorCommandListener listener, int deviceType) {
            this.mListener = listener;
            this.mDeviceType = deviceType;
        }

        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mVendorCommandListenerRecords.remove(this);
            }
        }
    }

    private class HdmiRecordListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final IHdmiRecordListener mListener;

        public HdmiRecordListenerRecord(IHdmiRecordListener listener) {
            this.mListener = listener;
        }

        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                if (HdmiControlService.this.mRecordListenerRecord == this) {
                    HdmiRecordListenerRecord unused = HdmiControlService.this.mRecordListenerRecord = null;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void enforceAccessPermission() {
        getContext().enforceCallingOrSelfPermission(PERMISSION, TAG);
    }

    private final class BinderService extends IHdmiControlService.Stub {
        private BinderService() {
        }

        public int[] getSupportedTypes() {
            HdmiControlService.this.enforceAccessPermission();
            int[] localDevices = new int[HdmiControlService.this.mLocalDevices.size()];
            for (int i = 0; i < localDevices.length; i++) {
                localDevices[i] = ((Integer) HdmiControlService.this.mLocalDevices.get(i)).intValue();
            }
            return localDevices;
        }

        public HdmiDeviceInfo getActiveSource() {
            HdmiControlService.this.enforceAccessPermission();
            HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
            if (tv != null) {
                HdmiCecLocalDevice.ActiveSource activeSource = tv.getActiveSource();
                if (activeSource.isValid()) {
                    return new HdmiDeviceInfo(activeSource.logicalAddress, activeSource.physicalAddress, -1, -1, 0, "");
                }
                int activePath = tv.getActivePath();
                if (activePath == 65535) {
                    return null;
                }
                HdmiDeviceInfo info = tv.getSafeDeviceInfoByPath(activePath);
                return info != null ? info : new HdmiDeviceInfo(activePath, tv.getActivePortId());
            } else if (HdmiControlService.this.isTvDevice()) {
                Slog.e(HdmiControlService.TAG, "Local tv device not available.");
                return null;
            } else if (!HdmiControlService.this.isPlaybackDevice()) {
                return null;
            } else {
                if (HdmiControlService.this.playback() != null && HdmiControlService.this.playback().mIsActiveSource) {
                    return HdmiControlService.this.playback().getDeviceInfo();
                }
                HdmiCecLocalDevice.ActiveSource activeSource2 = HdmiControlService.this.mActiveSource;
                if (!activeSource2.isValid()) {
                    return null;
                }
                if (HdmiControlService.this.audioSystem() != null) {
                    for (HdmiDeviceInfo info2 : HdmiControlService.this.audioSystem().getSafeCecDevicesLocked()) {
                        if (info2.getLogicalAddress() == activeSource2.logicalAddress) {
                            return info2;
                        }
                    }
                }
                return new HdmiDeviceInfo(activeSource2.logicalAddress, activeSource2.physicalAddress, HdmiControlService.this.pathToPortId(activeSource2.physicalAddress), HdmiUtils.getTypeFromAddress(activeSource2.logicalAddress), 0, HdmiUtils.getDefaultDeviceName(activeSource2.logicalAddress));
            }
        }

        public void deviceSelect(final int deviceId, final IHdmiControlCallback callback) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (callback == null) {
                        Slog.e(HdmiControlService.TAG, "Callback cannot be null");
                        return;
                    }
                    HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
                    if (tv != null) {
                        HdmiMhlLocalDeviceStub device = HdmiControlService.this.mMhlController.getLocalDeviceById(deviceId);
                        if (device == null) {
                            tv.deviceSelect(deviceId, callback);
                        } else if (device.getPortId() == tv.getActivePortId()) {
                            HdmiControlService.this.invokeCallback(callback, 0);
                        } else {
                            device.turnOn(callback);
                            tv.doManualPortSwitching(device.getPortId(), (IHdmiControlCallback) null);
                        }
                    } else if (!HdmiControlService.this.mAddressAllocated) {
                        HdmiControlService.this.mSelectRequestBuffer.set(SelectRequestBuffer.newDeviceSelect(HdmiControlService.this, deviceId, callback));
                    } else if (HdmiControlService.this.isTvDevice()) {
                        Slog.e(HdmiControlService.TAG, "Local tv device not available");
                    } else {
                        HdmiControlService.this.invokeCallback(callback, 2);
                    }
                }
            });
        }

        public void portSelect(final int portId, final IHdmiControlCallback callback) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (callback == null) {
                        Slog.e(HdmiControlService.TAG, "Callback cannot be null");
                        return;
                    }
                    HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
                    if (tv != null) {
                        tv.doManualPortSwitching(portId, callback);
                        return;
                    }
                    HdmiCecLocalDeviceAudioSystem audioSystem = HdmiControlService.this.audioSystem();
                    if (audioSystem != null) {
                        audioSystem.doManualPortSwitching(portId, callback);
                    } else if (!HdmiControlService.this.mAddressAllocated) {
                        HdmiControlService.this.mSelectRequestBuffer.set(SelectRequestBuffer.newPortSelect(HdmiControlService.this, portId, callback));
                    } else {
                        Slog.w(HdmiControlService.TAG, "Local device not available");
                        HdmiControlService.this.invokeCallback(callback, 2);
                    }
                }
            });
        }

        public void sendKeyEvent(final int deviceType, final int keyCode, final boolean isPressed) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiMhlLocalDeviceStub device = HdmiControlService.this.mMhlController.getLocalDevice(HdmiControlService.this.mActivePortId);
                    if (device != null) {
                        device.sendKeyEvent(keyCode, isPressed);
                    } else if (HdmiControlService.this.mCecController != null) {
                        HdmiCecLocalDevice localDevice = HdmiControlService.this.mCecController.getLocalDevice(deviceType);
                        if (localDevice == null) {
                            Slog.w(HdmiControlService.TAG, "Local device not available to send key event.");
                        } else {
                            localDevice.sendKeyEvent(keyCode, isPressed);
                        }
                    }
                }
            });
        }

        public void sendVolumeKeyEvent(final int deviceType, final int keyCode, final boolean isPressed) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (HdmiControlService.this.mCecController == null) {
                        Slog.w(HdmiControlService.TAG, "CEC controller not available to send volume key event.");
                        return;
                    }
                    HdmiCecLocalDevice localDevice = HdmiControlService.this.mCecController.getLocalDevice(deviceType);
                    if (localDevice == null) {
                        Slog.w(HdmiControlService.TAG, "Local device " + deviceType + " not available to send volume key event.");
                        return;
                    }
                    localDevice.sendVolumeKeyEvent(keyCode, isPressed);
                }
            });
        }

        public void oneTouchPlay(final IHdmiControlCallback callback) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiControlService.this.oneTouchPlay(callback);
                }
            });
        }

        public void queryDisplayStatus(final IHdmiControlCallback callback) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiControlService.this.queryDisplayStatus(callback);
                }
            });
        }

        public void addHotplugEventListener(IHdmiHotplugEventListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.addHotplugEventListener(listener);
        }

        public void removeHotplugEventListener(IHdmiHotplugEventListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.removeHotplugEventListener(listener);
        }

        public void addDeviceEventListener(IHdmiDeviceEventListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.addDeviceEventListener(listener);
        }

        public List<HdmiPortInfo> getPortInfo() {
            HdmiControlService.this.enforceAccessPermission();
            return HdmiControlService.this.getPortInfo();
        }

        public boolean canChangeSystemAudioMode() {
            HdmiControlService.this.enforceAccessPermission();
            HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
            if (tv == null) {
                return false;
            }
            return tv.hasSystemAudioDevice();
        }

        public boolean getSystemAudioMode() {
            HdmiControlService.this.enforceAccessPermission();
            HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
            HdmiCecLocalDeviceAudioSystem audioSystem = HdmiControlService.this.audioSystem();
            return (tv != null && tv.isSystemAudioActivated()) || (audioSystem != null && audioSystem.isSystemAudioActivated());
        }

        public int getPhysicalAddress() {
            int access$3700;
            HdmiControlService.this.enforceAccessPermission();
            synchronized (HdmiControlService.this.mLock) {
                access$3700 = HdmiControlService.this.mPhysicalAddress;
            }
            return access$3700;
        }

        public void setSystemAudioMode(final boolean enabled, final IHdmiControlCallback callback) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
                    if (tv == null) {
                        Slog.w(HdmiControlService.TAG, "Local tv device not available");
                        HdmiControlService.this.invokeCallback(callback, 2);
                        return;
                    }
                    tv.changeSystemAudioMode(enabled, callback);
                }
            });
        }

        public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.addSystemAudioModeChangeListner(listener);
        }

        public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.removeSystemAudioModeChangeListener(listener);
        }

        public void setInputChangeListener(IHdmiInputChangeListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.setInputChangeListener(listener);
        }

        public List<HdmiDeviceInfo> getInputDevices() {
            List<HdmiDeviceInfo> cecDevices;
            List<T> mergeToUnmodifiableList;
            HdmiControlService.this.enforceAccessPermission();
            HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
            synchronized (HdmiControlService.this.mLock) {
                if (tv == null) {
                    cecDevices = Collections.emptyList();
                } else {
                    cecDevices = tv.getSafeExternalInputsLocked();
                }
                mergeToUnmodifiableList = HdmiUtils.mergeToUnmodifiableList(cecDevices, HdmiControlService.this.getMhlDevicesLocked());
            }
            return mergeToUnmodifiableList;
        }

        public List<HdmiDeviceInfo> getDeviceList() {
            List<HdmiDeviceInfo> list;
            List<HdmiDeviceInfo> safeCecDevicesLocked;
            HdmiControlService.this.enforceAccessPermission();
            HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
            if (tv != null) {
                synchronized (HdmiControlService.this.mLock) {
                    safeCecDevicesLocked = tv.getSafeCecDevicesLocked();
                }
                return safeCecDevicesLocked;
            }
            HdmiCecLocalDeviceAudioSystem audioSystem = HdmiControlService.this.audioSystem();
            synchronized (HdmiControlService.this.mLock) {
                if (audioSystem == null) {
                    list = Collections.emptyList();
                } else {
                    list = audioSystem.getSafeCecDevicesLocked();
                }
            }
            return list;
        }

        public void powerOffRemoteDevice(final int logicalAddress, final int powerStatus) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    Slog.w(HdmiControlService.TAG, "Device " + logicalAddress + " power status is " + powerStatus + " before standby command sent out");
                    HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildStandby(HdmiControlService.this.getRemoteControlSourceAddress(), logicalAddress));
                }
            });
        }

        public void powerOnRemoteDevice(int logicalAddress, int powerStatus) {
        }

        public void askRemoteDeviceToBecomeActiveSource(final int physicalAddress) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiCecMessage setStreamPath = HdmiCecMessageBuilder.buildSetStreamPath(HdmiControlService.this.getRemoteControlSourceAddress(), physicalAddress);
                    if (HdmiControlService.this.pathToPortId(physicalAddress) != -1) {
                        if (HdmiControlService.this.getSwitchDevice() != null) {
                            HdmiControlService.this.getSwitchDevice().handleSetStreamPath(setStreamPath);
                        } else {
                            Slog.e(HdmiControlService.TAG, "Can't get the correct local device to handle routing.");
                        }
                    }
                    HdmiControlService.this.sendCecCommand(setStreamPath);
                }
            });
        }

        public void setSystemAudioVolume(final int oldIndex, final int newIndex, final int maxIndex) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
                    if (tv == null) {
                        Slog.w(HdmiControlService.TAG, "Local tv device not available");
                        return;
                    }
                    int i = oldIndex;
                    tv.changeVolume(i, newIndex - i, maxIndex);
                }
            });
        }

        public void setSystemAudioMute(final boolean mute) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiCecLocalDeviceTv tv = HdmiControlService.this.tv();
                    if (tv == null) {
                        Slog.w(HdmiControlService.TAG, "Local tv device not available");
                    } else {
                        tv.changeMute(mute);
                    }
                }
            });
        }

        public void setArcMode(boolean enabled) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (HdmiControlService.this.tv() == null) {
                        Slog.w(HdmiControlService.TAG, "Local tv device not available to change arc mode.");
                    }
                }
            });
        }

        public void setProhibitMode(boolean enabled) {
            HdmiControlService.this.enforceAccessPermission();
            if (HdmiControlService.this.isTvDevice()) {
                HdmiControlService.this.setProhibitMode(enabled);
            }
        }

        public void addVendorCommandListener(IHdmiVendorCommandListener listener, int deviceType) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.addVendorCommandListener(listener, deviceType);
        }

        public void sendVendorCommand(int deviceType, int targetAddress, byte[] params, boolean hasVendorId) {
            HdmiControlService.this.enforceAccessPermission();
            final int i = deviceType;
            final boolean z = hasVendorId;
            final int i2 = targetAddress;
            final byte[] bArr = params;
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiCecLocalDevice device = HdmiControlService.this.mCecController.getLocalDevice(i);
                    if (device == null) {
                        Slog.w(HdmiControlService.TAG, "Local device not available");
                    } else if (z) {
                        HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildVendorCommandWithId(device.getDeviceInfo().getLogicalAddress(), i2, HdmiControlService.this.getVendorId(), bArr));
                    } else {
                        HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildVendorCommand(device.getDeviceInfo().getLogicalAddress(), i2, bArr));
                    }
                }
            });
        }

        public void sendStandby(final int deviceType, final int deviceId) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiMhlLocalDeviceStub mhlDevice = HdmiControlService.this.mMhlController.getLocalDeviceById(deviceId);
                    if (mhlDevice != null) {
                        mhlDevice.sendStandby();
                        return;
                    }
                    HdmiCecLocalDevice device = HdmiControlService.this.mCecController.getLocalDevice(deviceType);
                    if (device == null) {
                        device = HdmiControlService.this.audioSystem();
                    }
                    if (device == null) {
                        Slog.w(HdmiControlService.TAG, "Local device not available");
                    } else {
                        device.sendStandby(deviceId);
                    }
                }
            });
        }

        public void setHdmiRecordListener(IHdmiRecordListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.setHdmiRecordListener(listener);
        }

        public void startOneTouchRecord(final int recorderAddress, final byte[] recordSource) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w(HdmiControlService.TAG, "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.tv().startOneTouchRecord(recorderAddress, recordSource);
                    }
                }
            });
        }

        public void stopOneTouchRecord(final int recorderAddress) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w(HdmiControlService.TAG, "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.tv().stopOneTouchRecord(recorderAddress);
                    }
                }
            });
        }

        public void startTimerRecording(final int recorderAddress, final int sourceType, final byte[] recordSource) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w(HdmiControlService.TAG, "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.tv().startTimerRecording(recorderAddress, sourceType, recordSource);
                    }
                }
            });
        }

        public void clearTimerRecording(final int recorderAddress, final int sourceType, final byte[] recordSource) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w(HdmiControlService.TAG, "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.tv().clearTimerRecording(recorderAddress, sourceType, recordSource);
                    }
                }
            });
        }

        public void sendMhlVendorCommand(int portId, int offset, int length, byte[] data) {
            HdmiControlService.this.enforceAccessPermission();
            final int i = portId;
            final int i2 = offset;
            final int i3 = length;
            final byte[] bArr = data;
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (!HdmiControlService.this.isControlEnabled()) {
                        Slog.w(HdmiControlService.TAG, "Hdmi control is disabled.");
                    } else if (HdmiControlService.this.mMhlController.getLocalDevice(i) == null) {
                        Slog.w(HdmiControlService.TAG, "Invalid port id:" + i);
                    } else {
                        HdmiControlService.this.mMhlController.sendVendorCommand(i, i2, i3, bArr);
                    }
                }
            });
        }

        public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener listener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.addHdmiMhlVendorCommandListener(listener);
        }

        public void setStandbyMode(final boolean isStandbyModeOn) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    HdmiControlService.this.setStandbyMode(isStandbyModeOn);
                }
            });
        }

        public void reportAudioStatus(final int deviceType, int volume, int maxVolume, boolean isMute) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (HdmiControlService.this.mCecController.getLocalDevice(deviceType) == null) {
                        Slog.w(HdmiControlService.TAG, "Local device not available");
                    } else if (HdmiControlService.this.audioSystem() == null) {
                        Slog.w(HdmiControlService.TAG, "audio system is not available");
                    } else if (!HdmiControlService.this.audioSystem().isSystemAudioActivated()) {
                        Slog.w(HdmiControlService.TAG, "audio system is not in system audio mode");
                    } else {
                        HdmiControlService.this.audioSystem().reportAudioStatus(0);
                    }
                }
            });
        }

        public void setSystemAudioModeOnForAudioOnlySource() {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.runOnServiceThread(new Runnable() {
                public void run() {
                    if (!HdmiControlService.this.isAudioSystemDevice()) {
                        Slog.e(HdmiControlService.TAG, "Not an audio system device. Won't set system audio mode on");
                    } else if (HdmiControlService.this.audioSystem() == null) {
                        Slog.e(HdmiControlService.TAG, "Audio System local device is not registered");
                    } else if (!HdmiControlService.this.audioSystem().checkSupportAndSetSystemAudioMode(true)) {
                        Slog.e(HdmiControlService.TAG, "System Audio Mode is not supported.");
                    } else {
                        HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(HdmiControlService.this.audioSystem().mAddress, 15, true));
                    }
                }
            });
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
            if (DumpUtils.checkDumpPermission(HdmiControlService.this.getContext(), HdmiControlService.TAG, writer)) {
                IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
                pw.println("mProhibitMode: " + HdmiControlService.this.mProhibitMode);
                pw.println("mPowerStatus: " + HdmiControlService.this.mPowerStatus);
                pw.println("System_settings:");
                pw.increaseIndent();
                pw.println("mHdmiControlEnabled: " + HdmiControlService.this.mHdmiControlEnabled);
                pw.println("mMhlInputChangeEnabled: " + HdmiControlService.this.mMhlInputChangeEnabled);
                pw.println("mSystemAudioActivated: " + HdmiControlService.this.isSystemAudioActivated());
                pw.decreaseIndent();
                pw.println("mMhlController: ");
                pw.increaseIndent();
                HdmiControlService.this.mMhlController.dump(pw);
                pw.decreaseIndent();
                HdmiUtils.dumpIterable(pw, "mPortInfo:", HdmiControlService.this.mPortInfo);
                if (HdmiControlService.this.mCecController != null) {
                    pw.println("mCecController: ");
                    pw.increaseIndent();
                    HdmiControlService.this.mCecController.dump(pw);
                    pw.decreaseIndent();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public int getRemoteControlSourceAddress() {
        if (isAudioSystemDevice()) {
            return audioSystem().getDeviceInfo().getLogicalAddress();
        }
        if (isPlaybackDevice()) {
            return playback().getDeviceInfo().getLogicalAddress();
        }
        return 15;
    }

    /* access modifiers changed from: private */
    public HdmiCecLocalDeviceSource getSwitchDevice() {
        if (isAudioSystemDevice()) {
            return audioSystem();
        }
        if (isPlaybackDevice()) {
            return playback();
        }
        return null;
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void oneTouchPlay(IHdmiControlCallback callback) {
        assertRunOnServiceThread();
        HdmiCecLocalDeviceSource source = playback();
        if (source == null) {
            source = audioSystem();
        }
        if (source == null) {
            Slog.w(TAG, "Local source device not available");
            invokeCallback(callback, 2);
            return;
        }
        source.oneTouchPlay(callback);
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void queryDisplayStatus(IHdmiControlCallback callback) {
        assertRunOnServiceThread();
        HdmiCecLocalDevicePlayback source = playback();
        if (source == null) {
            Slog.w(TAG, "Local playback device not available");
            invokeCallback(callback, 2);
            return;
        }
        source.queryDisplayStatus(callback);
    }

    /* access modifiers changed from: private */
    public void addHotplugEventListener(final IHdmiHotplugEventListener listener) {
        final HotplugEventListenerRecord record = new HotplugEventListenerRecord(listener);
        try {
            listener.asBinder().linkToDeath(record, 0);
            synchronized (this.mLock) {
                this.mHotplugEventListenerRecords.add(record);
            }
            runOnServiceThread(new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
                    if (r0.hasNext() == false) goto L_0x0058;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:11:0x0028, code lost:
                    r1 = (android.hardware.hdmi.HdmiPortInfo) r0.next();
                    r2 = new android.hardware.hdmi.HdmiHotplugEvent(r1.getId(), com.android.server.hdmi.HdmiControlService.access$1100(r6.this$0).isConnected(r1.getId()));
                    r3 = com.android.server.hdmi.HdmiControlService.access$2200(r6.this$0);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x004b, code lost:
                    monitor-enter(r3);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
                    com.android.server.hdmi.HdmiControlService.access$5200(r6.this$0, r5, r2);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:15:0x0053, code lost:
                    monitor-exit(r3);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:20:0x0058, code lost:
                    return;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:8:0x0018, code lost:
                    r0 = com.android.server.hdmi.HdmiControlService.access$5100(r6.this$0).iterator();
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r6 = this;
                        com.android.server.hdmi.HdmiControlService r0 = com.android.server.hdmi.HdmiControlService.this
                        java.lang.Object r0 = r0.mLock
                        monitor-enter(r0)
                        com.android.server.hdmi.HdmiControlService r1 = com.android.server.hdmi.HdmiControlService.this     // Catch:{ all -> 0x0059 }
                        java.util.ArrayList r1 = r1.mHotplugEventListenerRecords     // Catch:{ all -> 0x0059 }
                        com.android.server.hdmi.HdmiControlService$HotplugEventListenerRecord r2 = r0     // Catch:{ all -> 0x0059 }
                        boolean r1 = r1.contains(r2)     // Catch:{ all -> 0x0059 }
                        if (r1 != 0) goto L_0x0017
                        monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                        return
                    L_0x0017:
                        monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                        com.android.server.hdmi.HdmiControlService r0 = com.android.server.hdmi.HdmiControlService.this
                        java.util.List r0 = r0.mPortInfo
                        java.util.Iterator r0 = r0.iterator()
                    L_0x0022:
                        boolean r1 = r0.hasNext()
                        if (r1 == 0) goto L_0x0058
                        java.lang.Object r1 = r0.next()
                        android.hardware.hdmi.HdmiPortInfo r1 = (android.hardware.hdmi.HdmiPortInfo) r1
                        android.hardware.hdmi.HdmiHotplugEvent r2 = new android.hardware.hdmi.HdmiHotplugEvent
                        int r3 = r1.getId()
                        com.android.server.hdmi.HdmiControlService r4 = com.android.server.hdmi.HdmiControlService.this
                        com.android.server.hdmi.HdmiCecController r4 = r4.mCecController
                        int r5 = r1.getId()
                        boolean r4 = r4.isConnected(r5)
                        r2.<init>(r3, r4)
                        com.android.server.hdmi.HdmiControlService r3 = com.android.server.hdmi.HdmiControlService.this
                        java.lang.Object r3 = r3.mLock
                        monitor-enter(r3)
                        com.android.server.hdmi.HdmiControlService r4 = com.android.server.hdmi.HdmiControlService.this     // Catch:{ all -> 0x0055 }
                        android.hardware.hdmi.IHdmiHotplugEventListener r5 = r5     // Catch:{ all -> 0x0055 }
                        r4.invokeHotplugEventListenerLocked(r5, r2)     // Catch:{ all -> 0x0055 }
                        monitor-exit(r3)     // Catch:{ all -> 0x0055 }
                        goto L_0x0022
                    L_0x0055:
                        r0 = move-exception
                        monitor-exit(r3)     // Catch:{ all -> 0x0055 }
                        throw r0
                    L_0x0058:
                        return
                    L_0x0059:
                        r1 = move-exception
                        monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                        throw r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.hdmi.HdmiControlService.AnonymousClass2.run():void");
                }
            });
        } catch (RemoteException e) {
            Slog.w(TAG, "Listener already died");
        }
    }

    /* access modifiers changed from: private */
    public void removeHotplugEventListener(IHdmiHotplugEventListener listener) {
        synchronized (this.mLock) {
            Iterator<HotplugEventListenerRecord> it = this.mHotplugEventListenerRecords.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                HotplugEventListenerRecord record = it.next();
                if (record.mListener.asBinder() == listener.asBinder()) {
                    listener.asBinder().unlinkToDeath(record, 0);
                    this.mHotplugEventListenerRecords.remove(record);
                    break;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void addDeviceEventListener(IHdmiDeviceEventListener listener) {
        DeviceEventListenerRecord record = new DeviceEventListenerRecord(listener);
        try {
            listener.asBinder().linkToDeath(record, 0);
            synchronized (this.mLock) {
                this.mDeviceEventListenerRecords.add(record);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "Listener already died");
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeDeviceEventListeners(HdmiDeviceInfo device, int status) {
        synchronized (this.mLock) {
            Iterator<DeviceEventListenerRecord> it = this.mDeviceEventListenerRecords.iterator();
            while (it.hasNext()) {
                try {
                    it.next().mListener.onStatusChanged(device, status);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Failed to report device event:" + e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void addSystemAudioModeChangeListner(IHdmiSystemAudioModeChangeListener listener) {
        SystemAudioModeChangeListenerRecord record = new SystemAudioModeChangeListenerRecord(listener);
        try {
            listener.asBinder().linkToDeath(record, 0);
            synchronized (this.mLock) {
                this.mSystemAudioModeChangeListenerRecords.add(record);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "Listener already died");
        }
    }

    /* access modifiers changed from: private */
    public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener listener) {
        synchronized (this.mLock) {
            Iterator<SystemAudioModeChangeListenerRecord> it = this.mSystemAudioModeChangeListenerRecords.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SystemAudioModeChangeListenerRecord record = it.next();
                if (record.mListener.asBinder() == listener) {
                    listener.asBinder().unlinkToDeath(record, 0);
                    this.mSystemAudioModeChangeListenerRecords.remove(record);
                    break;
                }
            }
        }
    }

    private final class InputChangeListenerRecord implements IBinder.DeathRecipient {
        /* access modifiers changed from: private */
        public final IHdmiInputChangeListener mListener;

        public InputChangeListenerRecord(IHdmiInputChangeListener listener) {
            this.mListener = listener;
        }

        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                if (HdmiControlService.this.mInputChangeListenerRecord == this) {
                    InputChangeListenerRecord unused = HdmiControlService.this.mInputChangeListenerRecord = null;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setInputChangeListener(IHdmiInputChangeListener listener) {
        synchronized (this.mLock) {
            this.mInputChangeListenerRecord = new InputChangeListenerRecord(listener);
            try {
                listener.asBinder().linkToDeath(this.mInputChangeListenerRecord, 0);
            } catch (RemoteException e) {
                Slog.w(TAG, "Listener already died");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeInputChangeListener(HdmiDeviceInfo info) {
        synchronized (this.mLock) {
            if (this.mInputChangeListenerRecord != null) {
                try {
                    this.mInputChangeListenerRecord.mListener.onChanged(info);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Exception thrown by IHdmiInputChangeListener: " + e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setHdmiRecordListener(IHdmiRecordListener listener) {
        synchronized (this.mLock) {
            this.mRecordListenerRecord = new HdmiRecordListenerRecord(listener);
            try {
                listener.asBinder().linkToDeath(this.mRecordListenerRecord, 0);
            } catch (RemoteException e) {
                Slog.w(TAG, "Listener already died.", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] invokeRecordRequestListener(int recorderAddress) {
        synchronized (this.mLock) {
            if (this.mRecordListenerRecord != null) {
                try {
                    byte[] oneTouchRecordSource = this.mRecordListenerRecord.mListener.getOneTouchRecordSource(recorderAddress);
                    return oneTouchRecordSource;
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to start record.", e);
                }
            }
            byte[] bArr = EmptyArray.BYTE;
            return bArr;
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeOneTouchRecordResult(int recorderAddress, int result) {
        synchronized (this.mLock) {
            if (this.mRecordListenerRecord != null) {
                try {
                    this.mRecordListenerRecord.mListener.onOneTouchRecordResult(recorderAddress, result);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to call onOneTouchRecordResult.", e);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeTimerRecordingResult(int recorderAddress, int result) {
        synchronized (this.mLock) {
            if (this.mRecordListenerRecord != null) {
                try {
                    this.mRecordListenerRecord.mListener.onTimerRecordingResult(recorderAddress, result);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to call onTimerRecordingResult.", e);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeClearTimerRecordingResult(int recorderAddress, int result) {
        synchronized (this.mLock) {
            if (this.mRecordListenerRecord != null) {
                try {
                    this.mRecordListenerRecord.mListener.onClearTimerRecordingResult(recorderAddress, result);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to call onClearTimerRecordingResult.", e);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void invokeCallback(IHdmiControlCallback callback, int result) {
        try {
            callback.onComplete(result);
        } catch (RemoteException e) {
            Slog.e(TAG, "Invoking callback failed:" + e);
        }
    }

    private void invokeSystemAudioModeChangeLocked(IHdmiSystemAudioModeChangeListener listener, boolean enabled) {
        try {
            listener.onStatusChanged(enabled);
        } catch (RemoteException e) {
            Slog.e(TAG, "Invoking callback failed:" + e);
        }
    }

    private void announceHotplugEvent(int portId, boolean connected) {
        HdmiHotplugEvent event = new HdmiHotplugEvent(portId, connected);
        synchronized (this.mLock) {
            Iterator<HotplugEventListenerRecord> it = this.mHotplugEventListenerRecords.iterator();
            while (it.hasNext()) {
                invokeHotplugEventListenerLocked(it.next().mListener, event);
            }
        }
    }

    /* access modifiers changed from: private */
    public void invokeHotplugEventListenerLocked(IHdmiHotplugEventListener listener, HdmiHotplugEvent event) {
        try {
            listener.onReceived(event);
        } catch (RemoteException e) {
            Slog.e(TAG, "Failed to report hotplug event:" + event.toString(), e);
        }
    }

    public HdmiCecLocalDeviceTv tv() {
        return (HdmiCecLocalDeviceTv) this.mCecController.getLocalDevice(0);
    }

    /* access modifiers changed from: package-private */
    public boolean isTvDevice() {
        return this.mLocalDevices.contains(0);
    }

    /* access modifiers changed from: package-private */
    public boolean isAudioSystemDevice() {
        return this.mLocalDevices.contains(5);
    }

    /* access modifiers changed from: package-private */
    public boolean isPlaybackDevice() {
        return this.mLocalDevices.contains(4);
    }

    /* access modifiers changed from: package-private */
    public boolean isSwitchDevice() {
        return SystemProperties.getBoolean("ro.hdmi.property_is_device_hdmi_cec_switch", false);
    }

    /* access modifiers changed from: package-private */
    public boolean isTvDeviceEnabled() {
        return isTvDevice() && tv() != null;
    }

    /* access modifiers changed from: protected */
    public HdmiCecLocalDevicePlayback playback() {
        return (HdmiCecLocalDevicePlayback) this.mCecController.getLocalDevice(4);
    }

    public HdmiCecLocalDeviceAudioSystem audioSystem() {
        return (HdmiCecLocalDeviceAudioSystem) this.mCecController.getLocalDevice(5);
    }

    /* access modifiers changed from: package-private */
    public AudioManager getAudioManager() {
        return (AudioManager) getContext().getSystemService("audio");
    }

    /* access modifiers changed from: package-private */
    public boolean isControlEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mHdmiControlEnabled;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public int getPowerStatus() {
        assertRunOnServiceThread();
        return this.mPowerStatus;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public boolean isPowerOnOrTransient() {
        assertRunOnServiceThread();
        int i = this.mPowerStatus;
        return i == 0 || i == 2;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public boolean isPowerStandbyOrTransient() {
        assertRunOnServiceThread();
        int i = this.mPowerStatus;
        return i == 1 || i == 3;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public boolean isPowerStandby() {
        assertRunOnServiceThread();
        return this.mPowerStatus == 1;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void wakeUp() {
        assertRunOnServiceThread();
        this.mWakeUpMessageReceived = true;
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 8, "android.server.hdmi:WAKE");
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void standby() {
        assertRunOnServiceThread();
        if (canGoToStandby()) {
            this.mStandbyMessageReceived = true;
            this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 5, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isWakeUpMessageReceived() {
        return this.mWakeUpMessageReceived;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isStandbyMessageReceived() {
        return this.mStandbyMessageReceived;
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void onWakeUp() {
        assertRunOnServiceThread();
        this.mPowerStatus = 2;
        if (this.mCecController == null) {
            Slog.i(TAG, "Device does not support HDMI-CEC.");
        } else if (this.mHdmiControlEnabled) {
            int startReason = 2;
            if (this.mWakeUpMessageReceived) {
                startReason = 3;
            }
            initializeCec(startReason);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    @HdmiAnnotations.ServiceThreadOnly
    public void onStandby(final int standbyAction) {
        assertRunOnServiceThread();
        this.mPowerStatus = 3;
        invokeVendorCommandListenersOnControlStateChanged(false, 3);
        final List<HdmiCecLocalDevice> devices = getAllLocalDevices();
        if (isStandbyMessageReceived() || canGoToStandby()) {
            disableDevices(new HdmiCecLocalDevice.PendingActionClearedCallback() {
                public void onCleared(HdmiCecLocalDevice device) {
                    Slog.v(HdmiControlService.TAG, "On standby-action cleared:" + device.mDeviceType);
                    devices.remove(device);
                    if (devices.isEmpty()) {
                        HdmiControlService.this.onStandbyCompleted(standbyAction);
                    }
                }
            });
            return;
        }
        this.mPowerStatus = 1;
        for (HdmiCecLocalDevice device : devices) {
            device.onStandby(this.mStandbyMessageReceived, standbyAction);
        }
    }

    private boolean canGoToStandby() {
        for (HdmiCecLocalDevice device : this.mCecController.getLocalDeviceList()) {
            if (!device.canGoToStandby()) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void onLanguageChanged(String language) {
        assertRunOnServiceThread();
        this.mLanguage = language;
        if (isTvDeviceEnabled()) {
            tv().broadcastMenuLanguage(language);
            this.mCecController.setLanguage(language);
        }
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public String getLanguage() {
        assertRunOnServiceThread();
        return this.mLanguage;
    }

    private void disableDevices(HdmiCecLocalDevice.PendingActionClearedCallback callback) {
        HdmiCecController hdmiCecController = this.mCecController;
        if (hdmiCecController != null) {
            for (HdmiCecLocalDevice device : hdmiCecController.getLocalDeviceList()) {
                device.disableDevice(this.mStandbyMessageReceived, callback);
            }
        }
        this.mMhlController.clearAllLocalDevices();
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void clearLocalDevices() {
        assertRunOnServiceThread();
        HdmiCecController hdmiCecController = this.mCecController;
        if (hdmiCecController != null) {
            hdmiCecController.clearLogicalAddress();
            this.mCecController.clearLocalDevices();
        }
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void onStandbyCompleted(int standbyAction) {
        assertRunOnServiceThread();
        Slog.v(TAG, "onStandbyCompleted");
        if (this.mPowerStatus == 3) {
            this.mPowerStatus = 1;
            for (HdmiCecLocalDevice device : this.mCecController.getLocalDeviceList()) {
                device.onStandby(this.mStandbyMessageReceived, standbyAction);
            }
            this.mStandbyMessageReceived = false;
            if (!isAudioSystemDevice()) {
                this.mCecController.setOption(3, false);
                this.mMhlController.setOption(HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION, 0);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addVendorCommandListener(IHdmiVendorCommandListener listener, int deviceType) {
        VendorCommandListenerRecord record = new VendorCommandListenerRecord(listener, deviceType);
        try {
            listener.asBinder().linkToDeath(record, 0);
            synchronized (this.mLock) {
                this.mVendorCommandListenerRecords.add(record);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "Listener already died");
        }
    }

    /* access modifiers changed from: package-private */
    public boolean invokeVendorCommandListenersOnReceived(int deviceType, int srcAddress, int destAddress, byte[] params, boolean hasVendorId) {
        synchronized (this.mLock) {
            if (this.mVendorCommandListenerRecords.isEmpty()) {
                return false;
            }
            Iterator<VendorCommandListenerRecord> it = this.mVendorCommandListenerRecords.iterator();
            while (it.hasNext()) {
                VendorCommandListenerRecord record = it.next();
                if (record.mDeviceType == deviceType) {
                    try {
                        record.mListener.onReceived(srcAddress, destAddress, params, hasVendorId);
                    } catch (RemoteException e) {
                        Slog.e(TAG, "Failed to notify vendor command reception", e);
                    }
                }
            }
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean invokeVendorCommandListenersOnControlStateChanged(boolean enabled, int reason) {
        synchronized (this.mLock) {
            if (this.mVendorCommandListenerRecords.isEmpty()) {
                return false;
            }
            Iterator<VendorCommandListenerRecord> it = this.mVendorCommandListenerRecords.iterator();
            while (it.hasNext()) {
                try {
                    it.next().mListener.onControlStateChanged(enabled, reason);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Failed to notify control-state-changed to vendor handler", e);
                }
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener listener) {
        HdmiMhlVendorCommandListenerRecord record = new HdmiMhlVendorCommandListenerRecord(listener);
        try {
            listener.asBinder().linkToDeath(record, 0);
            synchronized (this.mLock) {
                this.mMhlVendorCommandListenerRecords.add(record);
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "Listener already died.");
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeMhlVendorCommandListeners(int portId, int offest, int length, byte[] data) {
        synchronized (this.mLock) {
            Iterator<HdmiMhlVendorCommandListenerRecord> it = this.mMhlVendorCommandListenerRecords.iterator();
            while (it.hasNext()) {
                try {
                    it.next().mListener.onReceived(portId, offest, length, data);
                } catch (RemoteException e) {
                    Slog.e(TAG, "Failed to notify MHL vendor command", e);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setStandbyMode(boolean isStandbyModeOn) {
        assertRunOnServiceThread();
        if (isPowerOnOrTransient() && isStandbyModeOn) {
            this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 5, 0);
            if (playback() != null) {
                playback().sendStandby(0);
            }
        } else if (isPowerStandbyOrTransient() && !isStandbyModeOn) {
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 8, "android.server.hdmi:WAKE");
            if (playback() != null) {
                oneTouchPlay(new IHdmiControlCallback.Stub() {
                    public void onComplete(int result) {
                        if (result != 0) {
                            Slog.w(HdmiControlService.TAG, "Failed to complete 'one touch play'. result=" + result);
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isProhibitMode() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mProhibitMode;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void setProhibitMode(boolean enabled) {
        synchronized (this.mLock) {
            this.mProhibitMode = enabled;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSystemAudioActivated() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSystemAudioActivated;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void setSystemAudioActivated(boolean on) {
        synchronized (this.mLock) {
            this.mSystemAudioActivated = on;
        }
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void setCecOption(int key, boolean value) {
        assertRunOnServiceThread();
        this.mCecController.setOption(key, value);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void setControlEnabled(boolean enabled) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mHdmiControlEnabled = enabled;
        }
        if (enabled) {
            enableHdmiControlService();
            return;
        }
        invokeVendorCommandListenersOnControlStateChanged(false, 1);
        runOnServiceThread(new Runnable() {
            public void run() {
                HdmiControlService.this.disableHdmiControlService();
            }
        });
    }

    @HdmiAnnotations.ServiceThreadOnly
    private void enableHdmiControlService() {
        this.mCecController.setOption(2, true);
        this.mCecController.setOption(3, true);
        this.mMhlController.setOption(103, 1);
        initializeCec(0);
    }

    /* access modifiers changed from: private */
    @HdmiAnnotations.ServiceThreadOnly
    public void disableHdmiControlService() {
        disableDevices(new HdmiCecLocalDevice.PendingActionClearedCallback() {
            public void onCleared(HdmiCecLocalDevice device) {
                HdmiControlService.this.assertRunOnServiceThread();
                HdmiControlService.this.mCecController.flush(new Runnable() {
                    public void run() {
                        HdmiControlService.this.mCecController.setOption(2, false);
                        HdmiControlService.this.mCecController.setOption(3, false);
                        HdmiControlService.this.mMhlController.setOption(103, 0);
                        HdmiControlService.this.clearLocalDevices();
                    }
                });
            }
        });
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void setActivePortId(int portId) {
        assertRunOnServiceThread();
        this.mActivePortId = portId;
        setLastInputForMhl(-1);
    }

    /* access modifiers changed from: package-private */
    public HdmiCecLocalDevice.ActiveSource getActiveSource() {
        HdmiCecLocalDevice.ActiveSource activeSource;
        synchronized (this.mLock) {
            activeSource = this.mActiveSource;
        }
        return activeSource;
    }

    /* access modifiers changed from: package-private */
    public void setActiveSource(int logicalAddress, int physicalAddress) {
        synchronized (this.mLock) {
            this.mActiveSource.logicalAddress = logicalAddress;
            this.mActiveSource.physicalAddress = physicalAddress;
        }
    }

    /* access modifiers changed from: protected */
    public void setAndBroadcastActiveSource(int physicalAddress, int deviceType, int source) {
        if (deviceType == 4) {
            HdmiCecLocalDevicePlayback playback = playback();
            playback.setIsActiveSource(true);
            playback.wakeUpIfActiveSource();
            playback.maySendActiveSource(source);
            setActiveSource(playback.mAddress, physicalAddress);
        }
        if (deviceType == 5) {
            HdmiCecLocalDeviceAudioSystem audioSystem = audioSystem();
            if (playback() != null) {
                audioSystem.setIsActiveSource(false);
                return;
            }
            audioSystem.setIsActiveSource(true);
            audioSystem.wakeUpIfActiveSource();
            audioSystem.maySendActiveSource(source);
            setActiveSource(audioSystem.mAddress, physicalAddress);
        }
    }

    /* access modifiers changed from: protected */
    public void setAndBroadcastActiveSourceFromOneDeviceType(int sourceAddress, int physicalAddress) {
        HdmiCecLocalDevicePlayback playback = playback();
        HdmiCecLocalDeviceAudioSystem audioSystem = audioSystem();
        if (playback != null) {
            playback.setIsActiveSource(true);
            playback.wakeUpIfActiveSource();
            playback.maySendActiveSource(sourceAddress);
            if (audioSystem != null) {
                audioSystem.setIsActiveSource(false);
            }
            setActiveSource(playback.mAddress, physicalAddress);
        } else if (audioSystem != null) {
            audioSystem.setIsActiveSource(true);
            audioSystem.wakeUpIfActiveSource();
            audioSystem.maySendActiveSource(sourceAddress);
            setActiveSource(audioSystem.mAddress, physicalAddress);
        }
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void setLastInputForMhl(int portId) {
        assertRunOnServiceThread();
        this.mLastInputMhl = portId;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public int getLastInputForMhl() {
        assertRunOnServiceThread();
        return this.mLastInputMhl;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void changeInputForMhl(int portId, boolean contentOn) {
        HdmiDeviceInfo info;
        assertRunOnServiceThread();
        if (tv() != null) {
            final int lastInput = contentOn ? tv().getActivePortId() : -1;
            if (portId != -1) {
                tv().doManualPortSwitching(portId, new IHdmiControlCallback.Stub() {
                    public void onComplete(int result) throws RemoteException {
                        HdmiControlService.this.setLastInputForMhl(lastInput);
                    }
                });
            }
            tv().setActivePortId(portId);
            HdmiMhlLocalDeviceStub device = this.mMhlController.getLocalDevice(portId);
            if (device != null) {
                info = device.getInfo();
            } else {
                info = this.mPortDeviceMap.get(portId, HdmiDeviceInfo.INACTIVE_DEVICE);
            }
            invokeInputChangeListener(info);
        }
    }

    /* access modifiers changed from: package-private */
    public void setMhlInputChangeEnabled(boolean enabled) {
        this.mMhlController.setOption(101, toInt(enabled));
        synchronized (this.mLock) {
            this.mMhlInputChangeEnabled = enabled;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isMhlInputChangeEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mMhlInputChangeEnabled;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void displayOsd(int messageId) {
        assertRunOnServiceThread();
        Intent intent = new Intent("android.hardware.hdmi.action.OSD_MESSAGE");
        intent.putExtra("android.hardware.hdmi.extra.MESSAGE_ID", messageId);
        getContext().sendBroadcastAsUser(intent, UserHandle.ALL, PERMISSION);
    }

    /* access modifiers changed from: package-private */
    @HdmiAnnotations.ServiceThreadOnly
    public void displayOsd(int messageId, int extra) {
        assertRunOnServiceThread();
        Intent intent = new Intent("android.hardware.hdmi.action.OSD_MESSAGE");
        intent.putExtra("android.hardware.hdmi.extra.MESSAGE_ID", messageId);
        intent.putExtra("android.hardware.hdmi.extra.MESSAGE_EXTRA_PARAM1", extra);
        getContext().sendBroadcastAsUser(intent, UserHandle.ALL, PERMISSION);
    }
}
