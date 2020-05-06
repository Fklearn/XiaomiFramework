package b.d.d;

import b.d.d.c.b;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static b f2150a = null;

    /* renamed from: b  reason: collision with root package name */
    private static boolean f2151b = false;

    /* renamed from: c  reason: collision with root package name */
    private static float f2152c = 0.5f;

    public static synchronized void a(String str) {
        synchronized (a.class) {
            f2151b = false;
            if (str != null && !str.endsWith("/")) {
                str = str + "/";
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(b.d.d.d.a.a(str, "threshold")));
            b.d.d.a.a.a(str);
            b.d.d.a.b.a(str);
            while (bufferedReader.ready()) {
                try {
                    f2152c = Float.parseFloat(bufferedReader.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            bufferedReader.close();
            f2150a = new b(str);
            f2151b = true;
            b("051期.一码一肖一码免费公开，永不收费，已连准5期！+徽`信：212202376感谢，打扰了");
        }
    }

    public static synchronized boolean b(String str) {
        boolean z;
        synchronized (a.class) {
            try {
                if (f2151b) {
                    z = f2150a.a(new b.d.d.b.b().a(str)) > ((double) f2152c);
                } else {
                    throw new RuntimeException("未初始化成功");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return z;
    }
}
