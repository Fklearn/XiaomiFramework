package com.android.server;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.UEventObserver;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import com.android.server.ExtconUEventObserver;
import com.android.server.input.InputManagerService;
import com.android.server.slice.SliceClientPermissions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class WiredAccessoryManager implements InputManagerService.WiredAccessoryCallbacks {
    private static final int BIT_HDMI_AUDIO = 16;
    private static final int BIT_HEADSET = 1;
    private static final int BIT_HEADSET_NO_MIC = 2;
    private static final int BIT_LINEOUT = 32;
    private static final int BIT_USB_HEADSET_ANLG = 4;
    private static final int BIT_USB_HEADSET_DGTL = 8;
    /* access modifiers changed from: private */
    public static final String[] DP_AUDIO_CONNS = {"soc:qcom,msm-ext-disp/1/0", "soc:qcom,msm-ext-disp/0/0"};
    private static final boolean LOG = false;
    private static final int MSG_NEW_DEVICE_STATE = 1;
    private static final int MSG_SYSTEM_READY = 2;
    private static final String NAME_DP_AUDIO = "soc:qcom,msm-ext-disp";
    private static final String NAME_H2W = "h2w";
    private static final String NAME_HDMI = "hdmi";
    private static final String NAME_HDMI_AUDIO = "hdmi_audio";
    private static final String NAME_USB_AUDIO = "usb_audio";
    private static final int SUPPORTED_HEADSETS = 63;
    /* access modifiers changed from: private */
    public static final String TAG = WiredAccessoryManager.class.getSimpleName();
    private final AudioManager mAudioManager;
    private int mDpCount;
    private final WiredAccessoryExtconObserver mExtconObserver;
    private final Handler mHandler = new Handler(Looper.myLooper(), (Handler.Callback) null, true) {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                WiredAccessoryManager.this.setDevicesState(msg.arg1, msg.arg2, (String) msg.obj);
                WiredAccessoryManager.this.mWakeLock.release();
            } else if (i == 2) {
                WiredAccessoryManager.this.onSystemReady();
                WiredAccessoryManager.this.mWakeLock.release();
            }
        }
    };
    /* access modifiers changed from: private */
    public int mHeadsetState;
    private final InputManagerService mInputManager;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private final WiredAccessoryObserver mObserver;
    private int mSwitchValues;
    /* access modifiers changed from: private */
    public final boolean mUseDevInputEventForAudioJack;
    /* access modifiers changed from: private */
    public final PowerManager.WakeLock mWakeLock;

    public WiredAccessoryManager(Context context, InputManagerService inputManager) {
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "WiredAccessoryManager");
        this.mWakeLock.setReferenceCounted(false);
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mInputManager = inputManager;
        this.mUseDevInputEventForAudioJack = context.getResources().getBoolean(17891563);
        this.mExtconObserver = new WiredAccessoryExtconObserver();
        this.mObserver = new WiredAccessoryObserver();
    }

    /* access modifiers changed from: private */
    public void onSystemReady() {
        if (this.mUseDevInputEventForAudioJack) {
            int switchValues = 0;
            if (this.mInputManager.getSwitchState(-1, -256, 2) == 1) {
                switchValues = 0 | 4;
            }
            if (this.mInputManager.getSwitchState(-1, -256, 4) == 1) {
                switchValues |= 16;
            }
            if (this.mInputManager.getSwitchState(-1, -256, 6) == 1) {
                switchValues |= 64;
            }
            notifyWiredAccessoryChanged(0, switchValues, 84);
        }
        if (ExtconUEventObserver.extconExists() == 0 || this.mExtconObserver.uEventCount() <= 0) {
            this.mObserver.init();
            return;
        }
        if (this.mUseDevInputEventForAudioJack) {
            Log.w(TAG, "Both input event and extcon are used for audio jack, please just choose one.");
        }
        this.mExtconObserver.init();
    }

    public void notifyWiredAccessoryChanged(long whenNanos, int switchValues, int switchMask) {
        int headset;
        synchronized (this.mLock) {
            this.mSwitchValues = (this.mSwitchValues & (~switchMask)) | switchValues;
            int i = this.mSwitchValues & 84;
            if (i == 0) {
                headset = 0;
            } else if (i == 4) {
                headset = 2;
            } else if (i == 16) {
                headset = 1;
            } else if (i == 20) {
                headset = 1;
            } else if (i != 64) {
                headset = 0;
            } else {
                headset = 32;
            }
            updateLocked(NAME_H2W, "", (this.mHeadsetState & -36) | headset);
        }
    }

    public void systemReady() {
        synchronized (this.mLock) {
            this.mWakeLock.acquire();
            this.mHandler.sendMessage(this.mHandler.obtainMessage(2, 0, 0, (Object) null));
        }
    }

    /* access modifiers changed from: private */
    public void updateLocked(String newName, String address, int newState) {
        Message msg;
        int i;
        int i2;
        String str = newName;
        String str2 = address;
        int headsetState = newState & SUPPORTED_HEADSETS;
        int newDpState = newState & 16;
        int usb_headset_anlg = headsetState & 4;
        int usb_headset_dgtl = headsetState & 8;
        int h2w_headset = headsetState & 35;
        boolean h2wStateChange = true;
        boolean usbStateChange = true;
        boolean dpCountState = false;
        boolean dpBitState = (this.mHeadsetState & 16) > 0;
        if (this.mDpCount != 0) {
            dpCountState = true;
        }
        if (this.mHeadsetState != headsetState || str.startsWith(NAME_DP_AUDIO)) {
            if (h2w_headset == 35) {
                Log.e(TAG, "Invalid combination, unsetting h2w flag");
                h2wStateChange = false;
            }
            if (usb_headset_anlg == 4 && usb_headset_dgtl == 8) {
                Log.e(TAG, "Invalid combination, unsetting usb flag");
                usbStateChange = false;
            }
            if (h2wStateChange || usbStateChange) {
                if (str.startsWith(NAME_DP_AUDIO)) {
                    if (newDpState > 0 && (i2 = this.mDpCount) < DP_AUDIO_CONNS.length && dpBitState == dpCountState) {
                        this.mDpCount = i2 + 1;
                    } else if (newDpState != 0 || (i = this.mDpCount) <= 0) {
                        Log.e(TAG, "No state change for DP.");
                        return;
                    } else {
                        this.mDpCount = i - 1;
                    }
                }
                this.mWakeLock.acquire();
                Log.i(TAG, "MSG_NEW_DEVICE_STATE");
                if (str.startsWith(NAME_DP_AUDIO)) {
                    int pseudoHeadsetState = this.mHeadsetState;
                    if (dpBitState && newDpState != 0) {
                        pseudoHeadsetState = this.mHeadsetState & -17;
                    }
                    msg = this.mHandler.obtainMessage(1, headsetState, pseudoHeadsetState, "soc:qcom,msm-ext-disp/" + str2);
                    if (headsetState == 0 && this.mDpCount != 0) {
                        headsetState |= 16;
                    }
                } else {
                    msg = this.mHandler.obtainMessage(1, headsetState, this.mHeadsetState, str + SliceClientPermissions.SliceAuthority.DELIMITER + str2);
                }
                this.mHandler.sendMessage(msg);
                this.mHeadsetState = headsetState;
                return;
            }
            Log.e(TAG, "invalid transition, returning ...");
            return;
        }
        Log.e(TAG, "No state change.");
    }

    /* access modifiers changed from: private */
    public void setDevicesState(int headsetState, int prevHeadsetState, String headsetNameAddr) {
        synchronized (this.mLock) {
            int allHeadsets = SUPPORTED_HEADSETS;
            int curHeadset = 1;
            while (allHeadsets != 0) {
                if ((curHeadset & allHeadsets) != 0) {
                    setDeviceStateLocked(curHeadset, headsetState, prevHeadsetState, headsetNameAddr);
                    allHeadsets &= ~curHeadset;
                }
                curHeadset <<= 1;
            }
        }
    }

    private void setDeviceStateLocked(int headset, int headsetState, int prevHeadsetState, String headsetNameAddr) {
        int state;
        int outDevice;
        if ((headsetState & headset) != (prevHeadsetState & headset)) {
            int inDevice = 0;
            if ((headsetState & headset) != 0) {
                state = 1;
            } else {
                state = 0;
            }
            if (headset == 1) {
                outDevice = 4;
                inDevice = -2147483632;
            } else if (headset == 2) {
                outDevice = 8;
            } else if (headset == 32) {
                outDevice = 131072;
            } else if (headset == 4) {
                outDevice = 2048;
            } else if (headset == 8) {
                outDevice = 4096;
            } else if (headset == 16) {
                outDevice = 1024;
            } else {
                String str = TAG;
                Slog.e(str, "setDeviceState() invalid headset type: " + headset);
                return;
            }
            String[] hs = headsetNameAddr.split(SliceClientPermissions.SliceAuthority.DELIMITER);
            String str2 = "";
            if (outDevice != 0) {
                this.mAudioManager.setWiredDeviceConnectionState(outDevice, state, hs.length > 1 ? hs[1] : str2, hs[0]);
            }
            if (inDevice != 0) {
                AudioManager audioManager = this.mAudioManager;
                if (hs.length > 1) {
                    str2 = hs[1];
                }
                audioManager.setWiredDeviceConnectionState(inDevice, state, str2, hs[0]);
            }
        }
    }

    private String switchCodeToString(int switchValues, int switchMask) {
        StringBuffer sb = new StringBuffer();
        if (!((switchMask & 4) == 0 || (switchValues & 4) == 0)) {
            sb.append("SW_HEADPHONE_INSERT ");
        }
        if (!((switchMask & 16) == 0 || (switchValues & 16) == 0)) {
            sb.append("SW_MICROPHONE_INSERT");
        }
        return sb.toString();
    }

    class WiredAccessoryObserver extends UEventObserver {
        private final List<UEventInfo> mUEventInfo = makeObservedUEventList();

        public WiredAccessoryObserver() {
        }

        /* access modifiers changed from: package-private */
        public void init() {
            synchronized (WiredAccessoryManager.this.mLock) {
                char[] buffer = new char[1024];
                for (int i = 0; i < this.mUEventInfo.size(); i++) {
                    UEventInfo uei = this.mUEventInfo.get(i);
                    try {
                        FileReader file = new FileReader(uei.getSwitchStatePath());
                        int len = file.read(buffer, 0, 1024);
                        file.close();
                        int curState = Integer.parseInt(new String(buffer, 0, len).trim());
                        if (curState > 0) {
                            updateStateLocked(uei.getDevPath(), uei.getDevName(), curState);
                        }
                    } catch (FileNotFoundException e) {
                        Slog.w(WiredAccessoryManager.TAG, uei.getSwitchStatePath() + " not found while attempting to determine initial switch state");
                    } catch (Exception e2) {
                        Slog.e(WiredAccessoryManager.TAG, "Error while attempting to determine initial switch state for " + uei.getDevName(), e2);
                    }
                }
            }
            for (int i2 = 0; i2 < this.mUEventInfo.size(); i2++) {
                startObserving("DEVPATH=" + this.mUEventInfo.get(i2).getDevPath());
            }
        }

        private List<UEventInfo> makeObservedUEventList() {
            List<UEventInfo> retVal = new ArrayList<>();
            if (!WiredAccessoryManager.this.mUseDevInputEventForAudioJack) {
                UEventInfo uEventInfo = new UEventInfo(WiredAccessoryManager.NAME_H2W, 1, 2, 32);
                if (uEventInfo.checkSwitchExists()) {
                    retVal.add(uEventInfo);
                } else {
                    Slog.w(WiredAccessoryManager.TAG, "This kernel does not have wired headset support");
                }
            }
            UEventInfo uEventInfo2 = new UEventInfo(WiredAccessoryManager.NAME_USB_AUDIO, 4, 8, 0);
            if (uEventInfo2.checkSwitchExists()) {
                retVal.add(uEventInfo2);
            } else {
                Slog.w(WiredAccessoryManager.TAG, "This kernel does not have usb audio support");
            }
            UEventInfo uei = new UEventInfo(WiredAccessoryManager.NAME_HDMI_AUDIO, 16, 0, 0);
            if (uei.checkSwitchExists()) {
                retVal.add(uei);
            } else {
                UEventInfo uei2 = new UEventInfo(WiredAccessoryManager.NAME_HDMI, 16, 0, 0);
                if (uei2.checkSwitchExists()) {
                    retVal.add(uei2);
                } else {
                    Slog.w(WiredAccessoryManager.TAG, "This kernel does not have HDMI audio support");
                }
            }
            for (String conn : WiredAccessoryManager.DP_AUDIO_CONNS) {
                UEventInfo uei3 = new UEventInfo(conn, 16, 0, 0);
                if (uei3.checkSwitchExists()) {
                    retVal.add(uei3);
                } else {
                    Slog.w(WiredAccessoryManager.TAG, "Conn " + conn + " does not have DP audio support");
                }
            }
            return retVal;
        }

        public void onUEvent(UEventObserver.UEvent event) {
            String devPath = event.get("DEVPATH");
            String name = event.get("NAME");
            int state = 0;
            if (name == null) {
                name = event.get("SWITCH_NAME");
            }
            try {
                if (name.startsWith(WiredAccessoryManager.NAME_DP_AUDIO)) {
                    String state_str = event.get("STATE");
                    int offset = 0;
                    int length = state_str.length();
                    while (true) {
                        if (offset < length) {
                            int equals = state_str.indexOf(61, offset);
                            if (equals > offset && state_str.substring(offset, equals).equals("DP")) {
                                state = Integer.parseInt(state_str.substring(equals + 1, equals + 2));
                                break;
                            }
                            offset = equals + 3;
                        } else {
                            break;
                        }
                    }
                } else {
                    state = Integer.parseInt(event.get("SWITCH_STATE"));
                }
            } catch (NumberFormatException e) {
                Slog.i(WiredAccessoryManager.TAG, "couldn't get state from event, checking node");
                int i = 0;
                while (true) {
                    if (i >= this.mUEventInfo.size()) {
                        break;
                    }
                    UEventInfo uei = this.mUEventInfo.get(i);
                    if (name.equals(uei.getDevName())) {
                        char[] buffer = new char[1024];
                        int len = 0;
                        try {
                            FileReader file = new FileReader(uei.getSwitchStatePath());
                            len = file.read(buffer, 0, 1024);
                            file.close();
                        } catch (FileNotFoundException e2) {
                            Slog.e(WiredAccessoryManager.TAG, "file not found");
                        } catch (Exception e11) {
                            Slog.e(WiredAccessoryManager.TAG, "", e11);
                        }
                        try {
                            state = Integer.parseInt(new String(buffer, 0, len).trim());
                            break;
                        } catch (NumberFormatException e3) {
                            Slog.e(WiredAccessoryManager.TAG, "could not convert to number");
                        }
                    } else {
                        i++;
                    }
                }
            }
            synchronized (WiredAccessoryManager.this.mLock) {
                updateStateLocked(devPath, name, state);
            }
        }

        private void updateStateLocked(String devPath, String name, int state) {
            for (int i = 0; i < this.mUEventInfo.size(); i++) {
                UEventInfo uei = this.mUEventInfo.get(i);
                if (devPath.equals(uei.getDevPath())) {
                    WiredAccessoryManager.this.updateLocked(name, uei.getDevAddress(), uei.computeNewHeadsetState(WiredAccessoryManager.this.mHeadsetState, state));
                    return;
                }
            }
        }

        private final class UEventInfo {
            static final /* synthetic */ boolean $assertionsDisabled = false;
            private int mCableIndex;
            private String mDevAddress = "controller=0;stream=0";
            private int mDevIndex;
            private final String mDevName;
            private final int mState1Bits;
            private final int mState2Bits;
            private final int mStateNbits;

            static {
                Class<WiredAccessoryManager> cls = WiredAccessoryManager.class;
            }

            public UEventInfo(String devName, int state1Bits, int state2Bits, int stateNbits) {
                int idx;
                this.mDevName = devName;
                this.mState1Bits = state1Bits;
                this.mState2Bits = state2Bits;
                this.mStateNbits = stateNbits;
                this.mDevIndex = -1;
                this.mCableIndex = -1;
                if (this.mDevName.startsWith(WiredAccessoryManager.NAME_DP_AUDIO) && (idx = this.mDevName.indexOf(SliceClientPermissions.SliceAuthority.DELIMITER)) != -1) {
                    int idx2 = this.mDevName.indexOf(SliceClientPermissions.SliceAuthority.DELIMITER, idx + 1);
                    int dev = Integer.parseInt(this.mDevName.substring(idx + 1, idx2));
                    int cable = Integer.parseInt(this.mDevName.substring(idx2 + 1));
                    this.mDevAddress = "controller=" + cable + ";stream=" + dev;
                    checkDevIndex(dev);
                    checkCableIndex(cable);
                }
            }

            private void checkDevIndex(int dev_index) {
                int index = 0;
                char[] buffer = new char[1024];
                while (true) {
                    String devPath = String.format(Locale.US, "/sys/devices/platform/soc/%s/extcon/extcon%d/name", new Object[]{WiredAccessoryManager.NAME_DP_AUDIO, Integer.valueOf(index)});
                    File f = new File(devPath);
                    if (!f.exists()) {
                        String access$500 = WiredAccessoryManager.TAG;
                        Slog.e(access$500, "file " + devPath + " not found");
                        return;
                    }
                    try {
                        FileReader file = new FileReader(f);
                        int len = file.read(buffer, 0, 1024);
                        file.close();
                        if (!new String(buffer, 0, len).trim().startsWith(WiredAccessoryManager.NAME_DP_AUDIO) || index != dev_index) {
                            index++;
                        } else {
                            String access$5002 = WiredAccessoryManager.TAG;
                            Slog.e(access$5002, "set dev_index " + dev_index);
                            this.mDevIndex = dev_index;
                            return;
                        }
                    } catch (Exception e) {
                        Slog.e(WiredAccessoryManager.TAG, "checkDevIndex exception ", e);
                        return;
                    }
                }
            }

            private void checkCableIndex(int cable_index) {
                if (this.mDevIndex != -1) {
                    int index = 0;
                    char[] buffer = new char[1024];
                    while (true) {
                        String cablePath = String.format(Locale.US, "/sys/devices/platform/soc/%s/extcon/extcon%d/cable.%d/name", new Object[]{WiredAccessoryManager.NAME_DP_AUDIO, Integer.valueOf(this.mDevIndex), Integer.valueOf(index)});
                        File f = new File(cablePath);
                        if (!f.exists()) {
                            String access$500 = WiredAccessoryManager.TAG;
                            Slog.e(access$500, "file " + cablePath + " not found");
                            return;
                        }
                        try {
                            FileReader file = new FileReader(f);
                            int len = file.read(buffer, 0, 1024);
                            file.close();
                            if (!new String(buffer, 0, len).trim().equals("DP") || index != cable_index) {
                                Slog.w(WiredAccessoryManager.TAG, "checkCableIndex no name match, skip ");
                                index++;
                            } else {
                                this.mCableIndex = index;
                                String access$5002 = WiredAccessoryManager.TAG;
                                Slog.w(access$5002, "checkCableIndex set cable " + cable_index);
                                return;
                            }
                        } catch (Exception e) {
                            Slog.e(WiredAccessoryManager.TAG, "checkCableIndex exception", e);
                            return;
                        }
                    }
                }
            }

            public String getDevName() {
                return this.mDevName;
            }

            public String getDevAddress() {
                return this.mDevAddress;
            }

            public String getDevPath() {
                if (this.mDevName.startsWith(WiredAccessoryManager.NAME_DP_AUDIO)) {
                    return String.format(Locale.US, "/devices/platform/soc/%s/extcon/extcon%d", new Object[]{WiredAccessoryManager.NAME_DP_AUDIO, Integer.valueOf(this.mDevIndex)});
                }
                return String.format(Locale.US, "/devices/virtual/switch/%s", new Object[]{this.mDevName});
            }

            public String getSwitchStatePath() {
                if (this.mDevName.startsWith(WiredAccessoryManager.NAME_DP_AUDIO)) {
                    return String.format(Locale.US, "/sys/devices/platform/soc/%s/extcon/extcon%d/cable.%d/state", new Object[]{WiredAccessoryManager.NAME_DP_AUDIO, Integer.valueOf(this.mDevIndex), Integer.valueOf(this.mCableIndex)});
                }
                return String.format(Locale.US, "/sys/class/switch/%s/state", new Object[]{this.mDevName});
            }

            public boolean checkSwitchExists() {
                return new File(getSwitchStatePath()).exists();
            }

            public int computeNewHeadsetState(int headsetState, int switchState) {
                int setBits = this.mState1Bits;
                int i = this.mState2Bits;
                int i2 = this.mStateNbits;
                int preserveMask = ~(setBits | i | i2);
                if (switchState != 1) {
                    if (switchState == 2) {
                        setBits = i;
                    } else {
                        setBits = switchState == i2 ? i2 : 0;
                    }
                }
                return (headsetState & preserveMask) | setBits;
            }
        }
    }

    private class WiredAccessoryExtconObserver extends ExtconStateObserver<Pair<Integer, Integer>> {
        private final List<ExtconUEventObserver.ExtconInfo> mExtconInfos = ExtconUEventObserver.ExtconInfo.getExtconInfos(".*audio.*");

        WiredAccessoryExtconObserver() {
        }

        /* access modifiers changed from: private */
        public void init() {
            for (ExtconUEventObserver.ExtconInfo extconInfo : this.mExtconInfos) {
                Pair<Integer, Integer> state = null;
                try {
                    state = (Pair) parseStateFromFile(extconInfo);
                } catch (FileNotFoundException e) {
                    String access$500 = WiredAccessoryManager.TAG;
                    Slog.w(access$500, extconInfo.getStatePath() + " not found while attempting to determine initial state", e);
                } catch (IOException e2) {
                    String access$5002 = WiredAccessoryManager.TAG;
                    Slog.e(access$5002, "Error reading " + extconInfo.getStatePath() + " while attempting to determine initial state", e2);
                }
                if (state != null) {
                    updateState(extconInfo, extconInfo.getName(), state);
                }
                startObserving(extconInfo);
            }
        }

        public int uEventCount() {
            return this.mExtconInfos.size();
        }

        public Pair<Integer, Integer> parseState(ExtconUEventObserver.ExtconInfo extconInfo, String status) {
            int[] maskAndState = {0, 0};
            WiredAccessoryManager.updateBit(maskAndState, 2, status, "HEADPHONE");
            WiredAccessoryManager.updateBit(maskAndState, 1, status, "MICROPHONE");
            WiredAccessoryManager.updateBit(maskAndState, 16, status, "HDMI");
            WiredAccessoryManager.updateBit(maskAndState, 32, status, "LINE-OUT");
            return Pair.create(Integer.valueOf(maskAndState[0]), Integer.valueOf(maskAndState[1]));
        }

        public void updateState(ExtconUEventObserver.ExtconInfo extconInfo, String name, Pair<Integer, Integer> maskAndState) {
            synchronized (WiredAccessoryManager.this.mLock) {
                int mask = ((Integer) maskAndState.first).intValue();
                int state = ((Integer) maskAndState.second).intValue();
                WiredAccessoryManager.this.updateLocked(name, "", WiredAccessoryManager.this.mHeadsetState | (mask & state & (~((~state) & mask))));
            }
        }
    }

    /* access modifiers changed from: private */
    public static void updateBit(int[] maskAndState, int position, String state, String name) {
        maskAndState[0] = maskAndState[0] | position;
        if (state.contains(name + "=1")) {
            maskAndState[0] = maskAndState[0] | position;
            maskAndState[1] = maskAndState[1] | position;
            return;
        }
        if (state.contains(name + "=0")) {
            maskAndState[0] = maskAndState[0] | position;
            maskAndState[1] = maskAndState[1] & (~position);
        }
    }
}
