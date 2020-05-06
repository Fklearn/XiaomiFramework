package com.miui.gamebooster.n.b;

import android.content.Context;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.n.c.a;
import com.miui.gamebooster.n.d.c;
import com.miui.gamebooster.n.d.d;
import com.miui.gamebooster.n.d.g;
import com.miui.gamebooster.n.d.j;
import com.miui.gamebooster.n.d.l;
import com.miui.gamebooster.n.d.m;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.MiSoundEffectUtils;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static List<j> f4666a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static HashMap<com.miui.gamebooster.n.c.b, j> f4667b = new HashMap<>();

    /* renamed from: c  reason: collision with root package name */
    private static HashMap<a, List<com.miui.gamebooster.n.d.b>> f4668c = new HashMap<>();

    public static List<j> a(Context context) {
        if (f4666a.isEmpty()) {
            f4666a.add(new l(context.getResources().getString(R.string.vb_settings), com.miui.gamebooster.n.c.b.SETTINGS));
            j jVar = new j("", com.miui.gamebooster.n.c.b.QUICK_FUNC);
            jVar.a(new g(R.string.vb_quick_func_screen_record, R.drawable.vtb_btn_screening_bg, a.SCREEN_RECORD));
            jVar.a(new g(R.string.vb_quick_func_screen_capture, R.drawable.vtb_btn_capture_bg, a.SCREEN_CAPTURE));
            if (C0388t.f()) {
                jVar.a(new g(R.string.vb_quick_func_screening, R.drawable.vtb_btn_milink_bg, a.MILINK_SCREENING));
            }
            if (C0388t.i()) {
                jVar.a(new g(R.string.vb_quick_func_hangup_listening, R.drawable.vtb_btn_hangup_bg, a.HANGUP_LISTENING));
            }
            f4666a.add(jVar);
            f4667b.put(com.miui.gamebooster.n.c.b.QUICK_FUNC, jVar);
            j jVar2 = new j("", com.miui.gamebooster.n.c.b.VIDEO_EFFECTS);
            if (com.miui.gamebooster.videobox.utils.a.b()) {
                jVar2.a(new g(R.string.vb_video_effects_display_style, R.drawable.vtb_btn_display_style_bg, a.DISPLAY_STYLE));
            }
            if (MiSoundEffectUtils.b() || MiSoundEffectUtils.c()) {
                jVar2.a(new g(R.string.vb_video_effects_srs_premium_sound, R.drawable.vtb_btn_srs_bg, a.SRS_PREMIUM_SOUND));
            }
            String a2 = f.a();
            if (com.miui.gamebooster.videobox.utils.f.a() && com.miui.gamebooster.videobox.utils.f.a(a2)) {
                jVar2.a(new g(R.string.vb_advanced_settings, R.drawable.vtb_btn_advanced_bg, a.ADVANCED_SETTINGS));
            }
            if (!jVar2.g()) {
                f4666a.add(jVar2);
                f4667b.put(com.miui.gamebooster.n.c.b.VIDEO_EFFECTS, jVar2);
            }
            j jVar3 = new j("", com.miui.gamebooster.n.c.b.FLOATING_APPS);
            f4666a.add(jVar3);
            f4667b.put(com.miui.gamebooster.n.c.b.FLOATING_APPS, jVar3);
        }
        j jVar4 = f4667b.get(com.miui.gamebooster.n.c.b.FLOATING_APPS);
        List<m> a3 = d.a().a(context);
        if (jVar4 != null) {
            jVar4.a((List<g>) new ArrayList(a3));
        }
        return f4666a;
    }

    public static List<com.miui.gamebooster.n.d.b> a(Context context, a aVar) {
        Object obj;
        List<com.miui.gamebooster.n.d.b> list = f4668c.get(aVar);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        ArrayList arrayList = new ArrayList();
        f4668c.put(aVar, arrayList);
        int i = a.f4665a[aVar.ordinal()];
        if (i == 1) {
            arrayList.add(new c(R.string.vb_video_effects_setting_raw, R.drawable.vtb_display_style_raw, 1));
            arrayList.add(new c(R.string.vb_video_effects_setting_outside, R.drawable.vtb_display_style_outside, 3));
            arrayList.add(new c(R.string.vb_video_effects_setting_cinema, R.drawable.vtb_display_style_cinema, 2));
            arrayList.add(new c(R.string.vb_video_effects_setting_old_movie, R.drawable.vtb_display_style_old_movie, 4));
            obj = new c(R.string.vb_video_effects_setting_black_white, R.drawable.vtb_display_style_black, 5);
        } else if (i != 2) {
            if (i == 3) {
                obj = new com.miui.gamebooster.n.d.a(R.string.vb_ve_settings_pic_border_stronger, R.drawable.vtb_video_effect_vpp_before_cn, R.drawable.vtb_video_effect_vpp_after_cn, 9);
            }
            return arrayList;
        } else {
            arrayList.add(new d(R.string.vb_video_effects_settings_liquid, R.drawable.vtb_srs_vocal_img, 6));
            obj = new d(R.string.vb_video_effects_settings_stereo, R.drawable.vtb_srs_surround_img, 7);
        }
        arrayList.add(obj);
        return arrayList;
    }

    public static void a() {
        f4666a.clear();
        f4667b.clear();
    }
}
