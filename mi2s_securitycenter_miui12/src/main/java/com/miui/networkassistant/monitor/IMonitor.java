package com.miui.networkassistant.monitor;

import android.content.Context;
import android.content.Intent;

public interface IMonitor {
    void invoke(Context context, Intent intent);

    void register();
}
