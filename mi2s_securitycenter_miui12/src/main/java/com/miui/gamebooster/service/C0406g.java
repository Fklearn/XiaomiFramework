package com.miui.gamebooster.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.S;
import com.miui.gamebooster.m.X;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.utils.DateUtil;
import java.util.Date;

/* renamed from: com.miui.gamebooster.service.g  reason: case insensitive filesystem */
class C0406g extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4815a;

    C0406g(GameBoosterService gameBoosterService) {
        this.f4815a = gameBoosterService;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String str = intent.getPackage();
        if (TextUtils.equals(action, Constants.System.ACTION_SCREEN_OFF)) {
            this.f4815a.a(false, context);
            if (!b.a("gb_show_window", false) && !a.n(false)) {
                this.f4815a.h();
                if (X.c(context) || !X.a()) {
                    return;
                }
            } else {
                return;
            }
        } else if (!TextUtils.equals(action, Constants.System.ACTION_USER_PRESENT)) {
            if (intent.getAction().equals("android.intent.action.CLOSE_SYSTEM_DIALOGS")) {
                this.f4815a.a(false, context);
                String stringExtra = intent.getStringExtra("reason");
                if (stringExtra != null && stringExtra.equals("homekey")) {
                    this.f4815a.h();
                    return;
                }
                return;
            } else if (intent.getAction().equals("com.miui.gamebooster.action.SIGN_NOTIFICATION")) {
                if (!b.a("key_gamebooster_signed_day", "").equals(DateUtil.getDateFormat(2).format(new Date()))) {
                    S.e(this.f4815a.p);
                    return;
                }
                return;
            } else if (intent.getAction().equals("com.miui.gamebooster.service.action.SWITCHANTIMSG")) {
                r.a(context, this.f4815a.i).q();
                return;
            } else if (TextUtils.equals(action, "com.miui.gamebooster.action.START_GAMEMODE")) {
                this.f4815a.a(true, context);
                return;
            } else if (TextUtils.equals(action, "com.miui.gamebooster.action.STOP_GAMEMODE") && TextUtils.equals(str, this.f4815a.getPackageName())) {
                GameBoosterService gameBoosterService = this.f4815a;
                r.a((Context) gameBoosterService, gameBoosterService.i).o();
                return;
            } else if (TextUtils.equals(action, "com.miui.gamebooster.action.RESET_USERSTATUS")) {
                boolean unused = this.f4815a.y = b.a("gamebooster_xunyou_first_user", false);
                return;
            } else if (TextUtils.equals(action, "android.intent.action.HEADSET_PLUG") && a.e(true)) {
                if (intent.getIntExtra(AdvancedSlider.STATE, 0) == 1) {
                    AudioManager audioManager = (AudioManager) this.f4815a.p.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                    if (audioManager.isSpeakerphoneOn()) {
                        b.b("gamebooster_head_plug", true);
                        audioManager.setSpeakerphoneOn(false);
                        return;
                    }
                    return;
                } else if (b.a("gamebooster_head_plug", false)) {
                    ((AudioManager) this.f4815a.p.getSystemService(MimeTypes.BASE_TYPE_AUDIO)).setSpeakerphoneOn(true);
                    b.b("gamebooster_head_plug", false);
                    return;
                } else {
                    return;
                }
            } else {
                return;
            }
        }
        this.f4815a.a(context, false);
    }
}
