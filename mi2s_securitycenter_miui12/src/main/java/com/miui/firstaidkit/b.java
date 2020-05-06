package com.miui.firstaidkit;

import android.os.Handler;
import android.os.Message;
import java.lang.ref.WeakReference;

public class b extends Handler {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<FirstAidKitActivity> f3898a;

    public b(FirstAidKitActivity firstAidKitActivity) {
        this.f3898a = new WeakReference<>(firstAidKitActivity);
    }

    public void handleMessage(Message message) {
        super.handleMessage(message);
        FirstAidKitActivity firstAidKitActivity = (FirstAidKitActivity) this.f3898a.get();
        if (firstAidKitActivity != null && !firstAidKitActivity.isFinishing() && !firstAidKitActivity.isDestroyed()) {
            int i = message.what;
            if (i == 200) {
                firstAidKitActivity.p();
            } else if (i == 201) {
                firstAidKitActivity.o();
            }
        }
    }
}
