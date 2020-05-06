package com.miui.gamebooster.j;

import android.util.Log;
import b.b.c.g.c;
import b.b.c.g.f;
import b.b.c.h.j;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.globalgame.util.b;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f4436a = ",";

    public static String a(List<String> list) {
        String str;
        if (!h.i()) {
            Log.e("DomesticServer", "requestScreenShort: not allow connect to network!!!");
            return "";
        } else if (Utils.a(list)) {
            b.b("empty list");
            return "";
        } else {
            boolean z = true;
            if (list.size() == 1) {
                str = list.get(0);
            } else {
                StringBuilder sb = new StringBuilder();
                int size = list.size();
                int i = 0;
                while (i < size) {
                    sb.append(list.get(i));
                    sb.append(i == size + -1 ? "" : f4436a);
                    i++;
                }
                str = sb.toString();
            }
            String trim = str.trim();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(b() ? "https://adv.sec.intl.miui.com" : "https://adv.sec.miui.com");
            sb2.append("/game/screen_shot");
            String sb3 = sb2.toString();
            ArrayList<c> arrayList = new ArrayList<>();
            arrayList.add(new c("package", trim));
            arrayList.add(new c("sign", f.a(arrayList, "45b7a6c1-dcf4-4a69-8a5c-f263933ab358")));
            StringBuilder sb4 = new StringBuilder();
            for (c cVar : arrayList) {
                sb4.append(!z ? "&" : "?");
                sb4.append(cVar.a());
                sb4.append("=");
                sb4.append(cVar.b());
                z = false;
            }
            String str2 = sb3 + sb4.toString();
            b.a((Object) "url:" + str2);
            return com.miui.googlebase.b.c.a(str2, new j("gamebooster_domesticserver"));
        }
    }

    public static boolean a() {
        return b();
    }

    private static boolean b() {
        return Build.IS_INTERNATIONAL_BUILD;
    }
}
