package com.miui.permcenter.install;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class i extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PackageManagerActivity f6157a;

    i(PackageManagerActivity packageManagerActivity) {
        this.f6157a = packageManagerActivity;
    }

    public void onReceive(Context context, Intent intent) {
        this.f6157a.m();
        this.f6157a.getLoaderManager().getLoader(50).forceLoad();
    }
}
