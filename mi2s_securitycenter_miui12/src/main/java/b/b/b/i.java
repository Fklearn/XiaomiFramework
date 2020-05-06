package b.b.b;

import android.content.Context;
import com.miui.common.persistence.b;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.b;
import com.miui.securitycenter.R;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static i f1552a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1553b;

    public i(Context context) {
        this.f1553b = context.getApplicationContext();
    }

    public static synchronized i a(Context context) {
        i iVar;
        synchronized (i.class) {
            if (f1552a == null) {
                f1552a = new i(context);
            }
            iVar = f1552a;
        }
        return iVar;
    }

    public long a() {
        return b.a("update_dialog_pop_time", 0);
    }

    public void a(long j) {
        b.b("update_dialog_pop_time", j);
    }

    public void a(long j, String str) {
        b.b(this.f1553b.getString(R.string.preference_key_database_auto_update_time, new Object[]{str}), j);
    }

    public void a(VirusObserver virusObserver) {
        com.miui.guardprovider.b.a(this.f1553b).a((b.a) new h(this, virusObserver));
    }

    public long b() {
        return com.miui.common.persistence.b.a("engine_suggest_update_time", 0);
    }

    public void b(long j) {
        com.miui.common.persistence.b.b("engine_suggest_update_time", j);
    }

    public boolean c() {
        if (System.currentTimeMillis() - a() < 0) {
            a(0);
        }
        return System.currentTimeMillis() - a() > 86400000;
    }

    public boolean d() {
        if (System.currentTimeMillis() - b() < 0) {
            b(0);
        }
        return System.currentTimeMillis() - b() > 604800000;
    }
}
