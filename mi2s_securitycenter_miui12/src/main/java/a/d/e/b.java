package a.d.e;

import android.util.Log;
import androidx.annotation.RestrictTo;
import java.io.Writer;

@RestrictTo({RestrictTo.a.f224c})
public class b extends Writer {

    /* renamed from: a  reason: collision with root package name */
    private final String f133a;

    /* renamed from: b  reason: collision with root package name */
    private StringBuilder f134b = new StringBuilder(128);

    public b(String str) {
        this.f133a = str;
    }

    private void a() {
        if (this.f134b.length() > 0) {
            Log.d(this.f133a, this.f134b.toString());
            StringBuilder sb = this.f134b;
            sb.delete(0, sb.length());
        }
    }

    public void close() {
        a();
    }

    public void flush() {
        a();
    }

    public void write(char[] cArr, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            char c2 = cArr[i + i3];
            if (c2 == 10) {
                a();
            } else {
                this.f134b.append(c2);
            }
        }
    }
}
