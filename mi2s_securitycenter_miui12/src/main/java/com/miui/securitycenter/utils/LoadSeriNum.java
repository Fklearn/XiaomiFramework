package com.miui.securitycenter.utils;

public class LoadSeriNum {
    static {
        System.loadLibrary("jni_load_serinum");
    }

    public static final native byte[] readOTP();
}
