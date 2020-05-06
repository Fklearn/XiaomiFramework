package com.miui.gamebooster.gbservices;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.common.persistence.b;
import com.miui.gamebooster.e.a.a;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.O;
import com.miui.gamebooster.service.r;
import miui.util.Log;

public class n extends m {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4364a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4365b;

    /* renamed from: c  reason: collision with root package name */
    private r f4366c;

    public n(Context context, r rVar) {
        this.f4365b = context;
        this.f4366c = rVar;
    }

    private void a(boolean z) {
        if (C0388t.c()) {
            try {
                AudioManager audioManager = (AudioManager) this.f4365b.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                StringBuilder sb = new StringBuilder();
                sb.append("audio_game_sound_effect_switch=");
                String str = "on;";
                sb.append(z ? str : "off;");
                sb.append("audio_game_package_name");
                sb.append("=");
                sb.append(this.f4366c.a());
                audioManager.setParameters(sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("audio_game_sound_effect_switch=");
                if (!z) {
                    str = "off;";
                }
                sb2.append(str);
                sb2.append("audio_game_package_name");
                sb2.append("=");
                sb2.append(this.f4366c.a());
                Log.i("CompetitionModeService", sb2.toString());
            } catch (Exception e) {
                Log.i("GameBoosterService", e.toString());
            }
        }
    }

    private void b(boolean z) {
        ContentResolver.setMasterSyncAutomatically(z);
    }

    private void c(boolean z) {
        if (z) {
            boolean f = f();
            b.b("gb_function_user_auto_sync", f);
            if (f) {
                b(false);
            }
        } else if (!f()) {
            b(b.a("gb_function_user_auto_sync", false));
        }
    }

    private boolean f() {
        return ContentResolver.getMasterSyncAutomatically();
    }

    public void a() {
        if (this.f4364a) {
            a(false);
            a.a(this.f4365b, false);
            O.a(0);
            Log.i("GameBoosterService", "mIsNetPriority...stop");
        }
        Log.i("GameBoosterService", "mIsAutoSync...stop ");
        c(false);
    }

    public boolean b() {
        return true;
    }

    public void c() {
        if (this.f4364a) {
            a(true);
            a.a(this.f4365b, true);
            com.miui.gamebooster.c.a.a(this.f4365b);
            if (!com.miui.gamebooster.c.a.y(false) || !com.miui.gamebooster.c.a.o(true)) {
                O.a(2);
                Log.i("GameBoosterService", "mIsNetPriority...start ");
            }
        }
        Log.i("GameBoosterService", "mIsAutoSync...start ");
        c(true);
    }

    public void d() {
        this.f4364a = com.miui.gamebooster.c.a.e(true);
    }

    public int e() {
        return 3;
    }
}
