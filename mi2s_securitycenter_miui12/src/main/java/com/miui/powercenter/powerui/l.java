package com.miui.powercenter.powerui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class l {

    /* renamed from: a  reason: collision with root package name */
    private static final ExecutorService f7161a = Executors.newSingleThreadExecutor();

    public static void a(Runnable runnable) {
        f7161a.submit(runnable);
    }
}
