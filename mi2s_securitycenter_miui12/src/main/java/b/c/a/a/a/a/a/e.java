package b.c.a.a.a.a.a;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

class e extends ByteArrayOutputStream {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ f f1935a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    e(f fVar, int i) {
        super(i);
        this.f1935a = fVar;
    }

    public String toString() {
        int i = this.count;
        try {
            return new String(this.buf, 0, (i <= 0 || this.buf[i + -1] != 13) ? this.count : i - 1, this.f1935a.f1937b.name());
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
