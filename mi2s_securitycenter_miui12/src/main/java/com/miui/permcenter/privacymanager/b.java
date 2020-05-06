package com.miui.permcenter.privacymanager;

import com.miui.permcenter.privacymanager.AppBackgroundManagerActivity;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.c;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppBackgroundManagerActivity f6343a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppBackgroundManagerActivity.b f6344b;

    b(AppBackgroundManagerActivity.b bVar, AppBackgroundManagerActivity appBackgroundManagerActivity) {
        this.f6344b = bVar;
        this.f6343a = appBackgroundManagerActivity;
    }

    public void run() {
        if (!this.f6343a.isFinishing()) {
            c.a(this.f6344b.f6315a, (int) R.string.uninstall_app_done);
            this.f6343a.b();
        }
    }
}
