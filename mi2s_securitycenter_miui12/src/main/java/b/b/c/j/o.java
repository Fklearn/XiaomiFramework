package b.b.c.j;

import android.os.Build;
import b.b.m.a;
import com.miui.gamebooster.m.C0381l;
import com.miui.gamebooster.mutiwindow.f;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.securitycenter.Application;
import com.miui.superpower.b.k;
import miui.util.OldmanUtil;

public class o {
    public static boolean a() {
        return e.b() > 9 && Build.VERSION.SDK_INT > 27;
    }

    public static boolean a(String str) {
        Application d2 = Application.d();
        if ("#Intent;action=com.miui.powercenter.SUPERPOWER_SAVE_NEW;end".equals(str)) {
            return k.o(d2);
        }
        if ("#Intent;action=miui.intent.action.KIDMODE_ENTRANCE;end".equals(str)) {
            return a.a(d2);
        }
        if ("#Intent;action=miui.intent.action.PRIVATE_SPACE_SETTING;end".equals(str)) {
            return c();
        }
        if ("#Intent;action=miui.intent.action.XSPACE_SETTING;end".equals(str)) {
            return b();
        }
        if ("#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end".equals(str)) {
            return C0381l.b(d2);
        }
        if ("#Intent;action=miui.intent.action.QUICK_REPLY_SETTINGS;end".equals(str)) {
            return f.d();
        }
        if ("#Intent;component=com.miui.cloudservice/com.miui.cloudservice.ui.MiCloudFindDeviceStatusActivity;end".equals(str) || "#Intent;action=miui.powercenter.intent.action.BOOT_SHUTDOWN_ONTIME;end".equals(str) || "#Intent;component=com.android.phone/com.android.phone.settings.CallRecordSetting;end".equals(str)) {
            return true ^ B.g();
        }
        if ("#Intent;action=com.miui.gamebooster.action.VIDEOBOX_SETTINGS_ALL;end".equals(str)) {
            return e.a();
        }
        if ("#Intent;action=miui.intent.action.PRIVACY_SETTINGS;end".equals(str)) {
            return a();
        }
        return true;
    }

    private static boolean b() {
        return B.f() && Build.VERSION.SDK_INT >= 21 && !OldmanUtil.IS_ELDER_MODE;
    }

    private static boolean c() {
        return Build.VERSION.SDK_INT >= 21 && !OldmanUtil.IS_ELDER_MODE && !y.a("persist.sys.ent_activated", false);
    }
}
