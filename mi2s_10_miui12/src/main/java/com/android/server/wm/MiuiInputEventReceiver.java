package com.android.server.wm;

import android.os.Looper;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;

public class MiuiInputEventReceiver extends InputEventReceiver {
    public MiuiInputEventReceiver(InputChannel inputChannel, Looper looper) {
        super(inputChannel, looper);
    }

    public void onInputEvent(InputEvent event) {
        MiuiInputEventReceiver.super.onInputEvent(event);
    }
}
