package com.android.server.hdmi;

import com.android.server.hdmi.HdmiCecLocalDeviceAudioSystem;
import com.android.server.hdmi.HdmiControlService;

public class DetectTvSystemAudioModeSupportAction extends HdmiCecFeatureAction {
    private static final int STATE_WAITING_FOR_FEATURE_ABORT = 1;
    private HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback mCallback;
    private int mState;

    DetectTvSystemAudioModeSupportAction(HdmiCecLocalDevice source, HdmiCecLocalDeviceAudioSystem.TvSystemAudioModeSupportedCallback callback) {
        super(source);
        this.mCallback = callback;
    }

    /* access modifiers changed from: package-private */
    public boolean start() {
        this.mState = 1;
        addTimer(this.mState, 2000);
        sendSetSystemAudioMode();
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean processCommand(HdmiCecMessage cmd) {
        if (cmd.getOpcode() != 0 || this.mState != 1 || (cmd.getParams()[0] & 255) != 114) {
            return false;
        }
        finishAction(false);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void handleTimerEvent(int state) {
        int i = this.mState;
        if (i == state && i == 1) {
            finishAction(true);
        }
    }

    /* access modifiers changed from: protected */
    public void sendSetSystemAudioMode() {
        sendCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(getSourceAddress(), 0, true), new HdmiControlService.SendMessageCallback() {
            public final void onSendCompleted(int i) {
                DetectTvSystemAudioModeSupportAction.this.lambda$sendSetSystemAudioMode$0$DetectTvSystemAudioModeSupportAction(i);
            }
        });
    }

    public /* synthetic */ void lambda$sendSetSystemAudioMode$0$DetectTvSystemAudioModeSupportAction(int result) {
        if (result != 0) {
            finishAction(false);
        }
    }

    private void finishAction(boolean supported) {
        this.mCallback.onResult(supported);
        audioSystem().setTvSystemAudioModeSupport(supported);
        finish();
    }
}
