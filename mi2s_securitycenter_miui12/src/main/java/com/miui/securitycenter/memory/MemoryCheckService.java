package com.miui.securitycenter.memory;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MemoryCheckService extends Service {
    public IBinder onBind(Intent intent) {
        return new MemoryCheck(this).asBinder();
    }
}
