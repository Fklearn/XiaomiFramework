package com.miui.gamebooster.service;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.e;

class I extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoToolBoxService f4780a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    I(VideoToolBoxService videoToolBoxService, Handler handler) {
        super(handler);
        this.f4780a = videoToolBoxService;
    }

    public void onChange(boolean z) {
        boolean unused = this.f4780a.f = e.a() && f.b(this.f4780a.e);
        if (this.f4780a.f4799b != null) {
            this.f4780a.f4799b.sendEmptyMessage(this.f4780a.f ? 7 : 6);
        }
    }
}
