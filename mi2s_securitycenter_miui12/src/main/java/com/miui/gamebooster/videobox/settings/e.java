package com.miui.gamebooster.videobox.settings;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoBoxAppManageActivity f5194a;

    e(VideoBoxAppManageActivity videoBoxAppManageActivity) {
        this.f5194a = videoBoxAppManageActivity;
    }

    public void run() {
        if (this.f5194a.f5184d != null && !this.f5194a.isDestroyed()) {
            this.f5194a.f5184d.notifyDataSetChanged();
        }
    }
}
