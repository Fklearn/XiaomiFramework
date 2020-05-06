package b.c.a.b.d;

import android.graphics.Bitmap;
import java.io.InputStream;

public class c extends InputStream {

    /* renamed from: a  reason: collision with root package name */
    private Bitmap f2018a;

    public c(Bitmap bitmap) {
        this.f2018a = bitmap;
    }

    public Bitmap a() {
        return this.f2018a;
    }

    public int read() {
        throw new UnsupportedOperationException(" Unsupported , use getBitmap()");
    }
}
