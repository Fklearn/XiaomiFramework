package b.b.b.d;

import java.io.File;

public class e {

    /* renamed from: a  reason: collision with root package name */
    public static boolean f1524a = new File("/storage/sdcard0/installer/debug").exists();

    /* renamed from: b  reason: collision with root package name */
    public static boolean f1525b = new File("/storage/sdcard0/installer/staging").exists();

    /* renamed from: c  reason: collision with root package name */
    public static boolean f1526c = new File("/storage/sdcard0/installer/dev_staging").exists();

    /* renamed from: d  reason: collision with root package name */
    public static String f1527d = (f1525b ? "http://staging.miuisystem.n.xiaomi.com/pi/" : f1526c ? "https://dev.staging.pinstaller.pt.xiaomi.com/" : "https://a0.app.xiaomi.com/pi/");
    public static String e;

    static {
        a();
    }

    private static void a() {
        e = f1527d + "pinstaller/apkinfos";
    }
}
