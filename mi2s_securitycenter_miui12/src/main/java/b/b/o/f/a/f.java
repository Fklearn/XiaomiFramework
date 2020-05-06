package b.b.o.f.a;

import com.miui.applicationlock.widget.LockPatternView;
import java.util.List;

public class f extends e {

    /* renamed from: a  reason: collision with root package name */
    private static f f1878a;

    private f() {
    }

    public static synchronized f a() {
        f fVar;
        synchronized (f.class) {
            if (f1878a == null) {
                f1878a = new f();
            }
            fVar = f1878a;
        }
        return fVar;
    }

    public String a(List<LockPatternView.a> list) {
        if (list == null) {
            return "";
        }
        int size = list.size();
        byte[] bArr = new byte[size];
        for (int i = 0; i < size; i++) {
            LockPatternView.a aVar = list.get(i);
            bArr[i] = (byte) ((aVar.b() * 3) + aVar.a());
        }
        return new String(bArr);
    }
}
