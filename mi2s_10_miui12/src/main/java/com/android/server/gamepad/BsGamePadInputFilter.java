package com.android.server.gamepad;

import android.content.Context;
import android.view.InputEvent;
import android.view.InputFilter;
import java.util.Map;

public class BsGamePadInputFilter extends InputFilter {
    private static final boolean DEBUG = true;
    private static final String TAG = "BsGamePadInputFilter";
    private Context mContext;
    private BsGamePadWorker mGameWorker = new BsGamePadWorker(this.mContext);

    BsGamePadInputFilter(Context context) {
        super(context.getMainLooper());
        this.mContext = context;
    }

    public void loadKeyMap(Map map, boolean isChooseMove, int rotation) {
        this.mGameWorker.loadKeyMap(map, isChooseMove, rotation);
    }

    public void unloadKeyMap() {
        this.mGameWorker.unloadKeyMap();
    }

    public void enableKeyMap(boolean enable) {
        this.mGameWorker.enableKeyMap(enable);
    }

    public void onInputEvent(InputEvent event, int policyFlags) {
        BsGamePadWorker bsGamePadWorker = this.mGameWorker;
        if (bsGamePadWorker != null && !bsGamePadWorker.onInputEvent(event)) {
            sendInputEvent(event, policyFlags);
        }
    }
}
