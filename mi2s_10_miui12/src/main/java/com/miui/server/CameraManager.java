package com.miui.server;

import android.content.Context;

public class CameraManager {
    private static volatile CameraManager sIntance;

    private CameraManager(Context systemContext) {
    }

    public static CameraManager getInstance(Context systemContext) {
        if (sIntance == null) {
            synchronized (CameraManager.class) {
                if (sIntance == null) {
                    sIntance = new CameraManager(systemContext);
                }
            }
        }
        return sIntance;
    }

    public void startService() {
    }
}
