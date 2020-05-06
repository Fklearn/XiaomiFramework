package com.miui.gamebooster.videobox.settings;

import java.util.ArrayList;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoBoxAppManageActivity f5192a;

    c(VideoBoxAppManageActivity videoBoxAppManageActivity) {
        this.f5192a = videoBoxAppManageActivity;
    }

    public void run() {
        f.b((ArrayList<String>) this.f5192a.f);
        if (this.f5192a.g != null) {
            try {
                this.f5192a.g.c(this.f5192a.f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
