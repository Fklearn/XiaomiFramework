package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.c.a;
import miuix.preference.TextPreference;

/* renamed from: com.miui.gamebooster.ui.ka  reason: case insensitive filesystem */
class C0434ka implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f5080a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f5081b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoosterSettingFragment f5082c;

    C0434ka(GameBoosterSettingFragment gameBoosterSettingFragment, String str, String str2) {
        this.f5082c = gameBoosterSettingFragment;
        this.f5080a = str;
        this.f5081b = str2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        TextPreference textPreference;
        String str;
        if (i != 0) {
            if (i == 1) {
                a unused = this.f5082c.I;
                a.c(1);
                textPreference = this.f5082c.w;
                str = this.f5081b;
            }
            dialogInterface.dismiss();
        }
        a unused2 = this.f5082c.I;
        a.c(0);
        textPreference = this.f5082c.w;
        str = this.f5080a;
        textPreference.a(str);
        dialogInterface.dismiss();
    }
}
