package com.miui.gamebooster.h;

import android.content.Context;
import android.content.pm.ResolveInfo;
import com.miui.common.persistence.b;
import com.miui.gamebooster.d.c;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.F;
import com.miui.gamebooster.model.g;
import com.miui.gamebooster.model.j;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class a {
    private static String a() {
        return b.a("key_currentbooster_pkg_uid", (String) null);
    }

    public static List<j> a(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.DND, R.drawable.gamebox_dnd_button, R.string.gamebox_dnd), R.layout.gamebox_function_item));
        arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.WIFI, R.drawable.gamebox_wifi_button, R.string.gamebox_wifi), R.layout.gamebox_function_item));
        arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.SIMCARD, R.drawable.gamebox_simcard_one_button, R.string.gamebox_simcard), R.layout.gamebox_function_item));
        if (!Build.IS_INTERNATIONAL_BUILD && C0388t.t() && F.a(a())) {
            arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.WONDERFULE_MOMENT, R.drawable.gamebox_record_button, R.string.gamebox_manual_record), R.layout.gamebox_function_item));
        }
        if (C0388t.u() && !b(context, str)) {
            arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.VOICECHANGER, R.drawable.gamebox_voicechanger_button, R.string.gamebox_voicechanger), R.layout.gamebox_function_item));
        }
        if (C0388t.h()) {
            arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.DISPLAY, R.drawable.gamebox_yuanse_button, R.string.gamebox_display_1), R.layout.gamebox_function_item));
        }
        if (C0388t.f()) {
            arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.MILINK, R.drawable.gamebox_milink_button, R.string.gamebox_milink), R.layout.gamebox_function_item));
        }
        if (C0388t.i()) {
            arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.HANGUP, R.drawable.gamebox_hangup_button, R.string.gamebox_hangup), R.layout.gamebox_function_item));
        }
        arrayList.add(new j(c.FUNCTION, (ResolveInfo) null, new g(d.SETTINGS, R.drawable.gamebox_setting_button, R.string.setting), R.layout.gamebox_function_item));
        return arrayList;
    }

    public static boolean b(Context context, String str) {
        List<String> a2 = com.miui.gamebooster.o.a.a.a(context);
        if (str == null || a2 == null || a2.isEmpty()) {
            return false;
        }
        return a2.contains(str);
    }
}
