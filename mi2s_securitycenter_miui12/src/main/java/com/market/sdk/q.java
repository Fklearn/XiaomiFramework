package com.market.sdk;

import android.content.ServiceConnection;
import com.market.sdk.a.a;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class q<T> extends a<T> implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    private static ExecutorService f2244a = Executors.newCachedThreadPool();
}
