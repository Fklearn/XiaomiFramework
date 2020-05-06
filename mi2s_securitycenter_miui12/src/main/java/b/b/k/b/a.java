package b.b.k.b;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import b.b.c.j.x;
import com.miui.common.persistence.b;
import java.util.List;
import miui.security.SecurityManager;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private Context f1831a;

    /* renamed from: b  reason: collision with root package name */
    private UserManager f1832b;

    /* renamed from: c  reason: collision with root package name */
    private SecurityManager f1833c;

    public a(Context context) {
        this.f1831a = context;
        this.f1832b = (UserManager) context.getSystemService("user");
        this.f1833c = (SecurityManager) context.getSystemService("security");
    }

    public int a() {
        int i = 0;
        for (UserHandle identifier : this.f1832b.getUserProfiles()) {
            int identifier2 = identifier.getIdentifier();
            for (String a2 : this.f1833c.getAllPrivacyApps(identifier2)) {
                if (x.a(this.f1831a, a2, identifier2)) {
                    i++;
                }
            }
        }
        return i;
    }

    public Intent a(PackageManager packageManager, String str, int i) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.INFO");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, i);
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            intent.removeCategory("android.intent.category.INFO");
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setPackage(str);
            queryIntentActivities = packageManager.queryIntentActivities(intent, i);
        }
        if (queryIntentActivities == null || queryIntentActivities.size() <= 0) {
            return null;
        }
        Intent intent2 = new Intent(intent);
        intent2.setFlags(268435456);
        intent2.setClassName(queryIntentActivities.get(0).activityInfo.packageName, queryIntentActivities.get(0).activityInfo.name);
        return intent2;
    }

    public void a(boolean z) {
        b.b("is_first_open_privacy_apps", z);
    }

    public void b(boolean z) {
        b.b("is_first_setting_privacyapp", z);
    }

    public boolean b() {
        return b.a("is_first_open_privacy_apps", true);
    }

    public void c(boolean z) {
        Settings.Secure.putInt(this.f1831a.getContentResolver(), "is_privacy_apps_enable", z ? 1 : 0);
    }

    public boolean c() {
        return b.a("is_first_setting_privacyapp", true);
    }

    public void d(boolean z) {
        Settings.Secure.putInt(this.f1831a.getContentResolver(), "privacy_apps_shield_message_enable", z ? 1 : 0);
    }

    public boolean d() {
        return Settings.Secure.getInt(this.f1831a.getContentResolver(), "is_privacy_apps_enable", 0) == 1;
    }

    public boolean e() {
        return Settings.Secure.getInt(this.f1831a.getContentResolver(), "privacy_apps_shield_message_enable", 1) == 1;
    }
}
