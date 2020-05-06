package com.miui.applicationlock.c;

import miui.security.SecurityManager;

class m implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SecurityManager f3313a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3314b;

    m(SecurityManager securityManager, String str) {
        this.f3313a = securityManager;
        this.f3314b = str;
    }

    public void run() {
        this.f3313a.removeAccessControlPass(this.f3314b);
    }
}
