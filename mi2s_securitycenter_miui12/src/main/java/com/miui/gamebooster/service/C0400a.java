package com.miui.gamebooster.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.service.IGameBoosterWindow;

/* renamed from: com.miui.gamebooster.service.a  reason: case insensitive filesystem */
class C0400a implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4805a;

    C0400a(GameBoosterService gameBoosterService) {
        this.f4805a = gameBoosterService;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IGameBoosterWindow unused = this.f4805a.n = IGameBoosterWindow.Stub.a(iBinder);
        Log.i("GameBoosterService", "mGameWindowBinder binder suceessful");
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IGameBoosterWindow unused = this.f4805a.n = null;
    }
}
