package com.miui.gamebooster.videobox.settings;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.miui.gamebooster.service.IVideoToolBox;

class b implements ServiceConnection {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoBoxAppManageActivity f5191a;

    b(VideoBoxAppManageActivity videoBoxAppManageActivity) {
        this.f5191a = videoBoxAppManageActivity;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        IVideoToolBox unused = this.f5191a.g = IVideoToolBox.Stub.a(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        IVideoToolBox unused = this.f5191a.g = null;
    }
}
