package com.android.server.input;

import android.content.Context;
import android.os.Handler;
import com.android.server.HandyMode;

public class MiuiInputManagerService extends MiuiBaseInputManagerService {
    private Context mContext;

    public MiuiInputManagerService(Context context, Handler handler) {
        super(context, handler);
        this.mContext = context;
    }

    public void start() {
        super.start();
        HandyMode.initialize(this.mContext, this);
    }
}
