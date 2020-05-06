package b.b.b;

import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.antivirus.model.k;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;
import miui.util.IOUtils;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static b f1469a;

    /* renamed from: b  reason: collision with root package name */
    private Context f1470b;

    /* renamed from: c  reason: collision with root package name */
    private final IPackageDeleteObserver.Stub f1471c = new a(this);

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public String f1472a;

        /* renamed from: b  reason: collision with root package name */
        public String f1473b;

        /* renamed from: c  reason: collision with root package name */
        public String f1474c;

        /* renamed from: d  reason: collision with root package name */
        public boolean f1475d;
        public boolean e;
    }

    /* renamed from: b.b.b.b$b  reason: collision with other inner class name */
    public enum C0024b {
        INSTALLED_APP,
        UNINSTALLED_APK
    }

    public enum c {
        SAFE,
        RISK,
        VIRUS
    }

    private b(Context context) {
        this.f1470b = context.getApplicationContext();
    }

    public static synchronized b a(Context context) {
        b bVar;
        synchronized (b.class) {
            if (f1469a == null) {
                f1469a = new b(context);
            }
            bVar = f1469a;
        }
        return bVar;
    }

    public c a(int i) {
        return i != 2 ? i != 3 ? c.SAFE : c.RISK : c.VIRUS;
    }

    public String a() {
        String string;
        boolean z;
        Cursor c2 = c();
        if (c2 != null) {
            do {
                try {
                    if (c2.moveToNext()) {
                        string = c2.getString(c2.getColumnIndex("Name"));
                        z = true;
                        if (c2.getInt(c2.getColumnIndex("Enable")) != 1) {
                            z = false;
                            continue;
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(c2);
                }
            } while (!z);
            return string;
        }
        IOUtils.closeQuietly(c2);
        return null;
    }

    public void a(k kVar) {
        try {
            if (kVar.c() == C0024b.INSTALLED_APP) {
                IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package");
                Object a2 = e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
                if (b.b.o.b.a.a.a(a2, kVar.b())) {
                    b.b.o.b.a.a.a(a2, kVar.b(), x.e(this.f1470b, kVar.b()), (IPackageDeleteObserver) null, 999, 0);
                }
                b.b.o.b.a.a.a(this.f1470b.getPackageManager(), kVar.b(), this.f1471c, 0);
                return;
            }
            b.b.c.j.k.a(kVar.e());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String b() {
        String string;
        boolean z;
        Cursor c2 = c();
        if (c2 != null) {
            do {
                try {
                    if (c2.moveToNext()) {
                        string = c2.getString(c2.getColumnIndex("DescriptionName"));
                        z = true;
                        if (c2.getInt(c2.getColumnIndex("Enable")) != 1) {
                            z = false;
                            continue;
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(c2);
                }
            } while (!z);
            return string;
        }
        IOUtils.closeQuietly(c2);
        return null;
    }

    public Cursor c() {
        try {
            return this.f1470b.getContentResolver().query(Uri.parse("content://guard/engine_info_list"), (String[]) null, (String) null, (String[]) null, (String) null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<a> d() {
        Cursor c2 = c();
        ArrayList arrayList = new ArrayList();
        if (c2 != null) {
            while (c2.moveToNext()) {
                try {
                    a aVar = new a();
                    aVar.f1472a = c2.getString(c2.getColumnIndex("Name"));
                    aVar.f1474c = c2.getString(c2.getColumnIndex("DescriptionName"));
                    aVar.f1473b = c2.getString(c2.getColumnIndex("NameString"));
                    boolean z = false;
                    aVar.f1475d = c2.getInt(c2.getColumnIndex("Enable")) == 1;
                    if (c2.getInt(c2.getColumnIndex("SupportCloud")) == 1) {
                        z = true;
                    }
                    aVar.e = z;
                    arrayList.add(aVar);
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    IOUtils.closeQuietly(c2);
                    throw th;
                }
            }
            IOUtils.closeQuietly(c2);
            return arrayList;
        }
        IOUtils.closeQuietly(c2);
        return null;
    }

    public boolean e() {
        Cursor c2 = c();
        if (c2 != null) {
            while (c2.moveToNext()) {
                try {
                    String string = c2.getString(c2.getColumnIndex("Name"));
                    boolean z = c2.getInt(c2.getColumnIndex("Enable")) == 1;
                    String string2 = this.f1470b.getString(R.string.preference_key_database_auto_update_enabled, new Object[]{string});
                    if (z && !p.a(string2)) {
                        return false;
                    }
                } finally {
                    IOUtils.closeQuietly(c2);
                }
            }
        }
        IOUtils.closeQuietly(c2);
        return true;
    }

    public void f() {
        Cursor c2 = c();
        if (c2 != null) {
            while (c2.moveToNext()) {
                try {
                    String string = c2.getString(c2.getColumnIndex("Name"));
                    boolean z = c2.getInt(c2.getColumnIndex("Enable")) == 1;
                    String string2 = this.f1470b.getString(R.string.preference_key_database_auto_update_enabled, new Object[]{string});
                    if (z && !p.a(string2)) {
                        com.miui.common.persistence.b.b(string2, true);
                    }
                } catch (Throwable th) {
                    IOUtils.closeQuietly(c2);
                    throw th;
                }
            }
        }
        IOUtils.closeQuietly(c2);
    }
}
