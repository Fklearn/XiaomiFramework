package b.b.o.f.a;

import com.miui.applicationlock.widget.LockPatternView;
import java.util.List;

public class g extends e {

    /* renamed from: a  reason: collision with root package name */
    private static g f1879a;

    private g() {
    }

    public static synchronized g a() {
        g gVar;
        synchronized (g.class) {
            if (f1879a == null) {
                f1879a = new g();
            }
            gVar = f1879a;
        }
        return gVar;
    }

    public String a(List<LockPatternView.a> list) {
        if (list == null) {
            return "";
        }
        int size = list.size();
        byte[] bArr = new byte[size];
        for (int i = 0; i < size; i++) {
            LockPatternView.a aVar = list.get(i);
            bArr[i] = (byte) ((aVar.b() * 3) + aVar.a() + 49);
        }
        return new String(bArr);
    }
}
