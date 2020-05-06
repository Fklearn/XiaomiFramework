package com.miui.gamebooster.gbservices;

import java.util.HashMap;

/* renamed from: com.miui.gamebooster.gbservices.c  reason: case insensitive filesystem */
class C0360c extends HashMap<String, String> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AntiMsgAccessibilityService f4347a;

    C0360c(AntiMsgAccessibilityService antiMsgAccessibilityService) {
        this.f4347a = antiMsgAccessibilityService;
        put("com.tencent.av.ui.VideoInviteLock", "QQ语音");
        put("com.tencent.av.ui.VideoInviteFull", "QQ电话");
        put("com.tencent.av.ui.VideoInviteActivity", "QQ电话");
        put("com.tencent.mm.plugin.voip.ui.VideoActivity", "微信电话");
    }
}
