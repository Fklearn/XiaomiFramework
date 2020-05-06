package com.miui.antivirus.activity;

import miui.util.Log;

class l implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2724a;

    l(MainActivity mainActivity) {
        this.f2724a = mainActivity;
    }

    public void run() {
        if (this.f2724a.f2669d) {
            Log.i("AntiVirusMainActivity", "PRELOAD FINISHED : startCleanResultFrame");
            MainActivity.f2666a = true;
            this.f2724a.l();
        }
    }
}
