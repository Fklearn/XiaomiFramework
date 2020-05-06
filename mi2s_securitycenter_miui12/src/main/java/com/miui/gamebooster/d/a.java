package com.miui.gamebooster.d;

import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import miui.os.Build;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static final boolean f4246a = Build.IS_INTERNATIONAL_BUILD;

    /* renamed from: b  reason: collision with root package name */
    public static final String f4247b = (f4246a ? "https://adv.sec.intl.miui.com/game/speedParams" : "https://adv.sec.miui.com/game/speedParams");

    /* renamed from: c  reason: collision with root package name */
    public static final String f4248c = (f4246a ? "https://adv.sec.intl.miui.com/game/popGames" : "https://adv.sec.miui.com/game/popGames");

    /* renamed from: d  reason: collision with root package name */
    public static final String f4249d = (f4246a ? "https://adv.sec.intl.miui.com/game/pkg" : "https://adv.sec.miui.com/game/pkg");
    public static final String e = (f4246a ? "https://adv.sec.intl.miui.com/game/upgrade/pkginfo" : "https://adv.sec.miui.com/game/upgrade/pkginfo");

    public static boolean a() {
        return !Build.IS_INTERNATIONAL_BUILD && Application.d().getResources().getBoolean(R.bool.display_gamebooster_xunyou);
    }
}
