package com.miui.antivirus.service;

import com.miui.antispam.policy.a.i;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VirusAutoUpdateJobService f2903a;

    k(VirusAutoUpdateJobService virusAutoUpdateJobService) {
        this.f2903a = virusAutoUpdateJobService;
    }

    public void run() {
        new i(this.f2903a).a();
    }
}
