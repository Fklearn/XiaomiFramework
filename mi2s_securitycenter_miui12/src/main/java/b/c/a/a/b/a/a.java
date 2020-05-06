package b.c.a.a.b.a;

import android.graphics.Bitmap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public class a implements b.c.a.a.b.a {

    /* renamed from: a  reason: collision with root package name */
    private final b.c.a.a.b.a f1942a;

    /* renamed from: b  reason: collision with root package name */
    private final Comparator<String> f1943b;

    public a(b.c.a.a.b.a aVar, Comparator<String> comparator) {
        this.f1942a = aVar;
        this.f1943b = comparator;
    }

    public Collection<String> a() {
        return this.f1942a.a();
    }

    public boolean a(String str, Bitmap bitmap) {
        synchronized (this.f1942a) {
            String str2 = null;
            Iterator<String> it = this.f1942a.a().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String next = it.next();
                if (this.f1943b.compare(str, next) == 0) {
                    str2 = next;
                    break;
                }
            }
            if (str2 != null) {
                this.f1942a.remove(str2);
            }
        }
        return this.f1942a.a(str, bitmap);
    }

    public Bitmap get(String str) {
        return this.f1942a.get(str);
    }

    public Bitmap remove(String str) {
        return this.f1942a.remove(str);
    }
}
