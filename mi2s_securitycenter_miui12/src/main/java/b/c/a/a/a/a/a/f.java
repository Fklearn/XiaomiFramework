package b.c.a.a.a.a.a;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

class f implements Closeable {

    /* renamed from: a  reason: collision with root package name */
    private final InputStream f1936a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public final Charset f1937b;

    /* renamed from: c  reason: collision with root package name */
    private byte[] f1938c;

    /* renamed from: d  reason: collision with root package name */
    private int f1939d;
    private int e;

    public f(InputStream inputStream, int i, Charset charset) {
        if (inputStream == null || charset == null) {
            throw new NullPointerException();
        } else if (i < 0) {
            throw new IllegalArgumentException("capacity <= 0");
        } else if (charset.equals(g.f1940a)) {
            this.f1936a = inputStream;
            this.f1937b = charset;
            this.f1938c = new byte[i];
        } else {
            throw new IllegalArgumentException("Unsupported encoding");
        }
    }

    public f(InputStream inputStream, Charset charset) {
        this(inputStream, 8192, charset);
    }

    private void b() {
        InputStream inputStream = this.f1936a;
        byte[] bArr = this.f1938c;
        int read = inputStream.read(bArr, 0, bArr.length);
        if (read != -1) {
            this.f1939d = 0;
            this.e = read;
            return;
        }
        throw new EOFException();
    }

    public String a() {
        int i;
        int i2;
        synchronized (this.f1936a) {
            if (this.f1938c != null) {
                if (this.f1939d >= this.e) {
                    b();
                }
                for (int i3 = this.f1939d; i3 != this.e; i3++) {
                    if (this.f1938c[i3] == 10) {
                        if (i3 != this.f1939d) {
                            i2 = i3 - 1;
                            if (this.f1938c[i2] == 13) {
                                String str = new String(this.f1938c, this.f1939d, i2 - this.f1939d, this.f1937b.name());
                                this.f1939d = i3 + 1;
                                return str;
                            }
                        }
                        i2 = i3;
                        String str2 = new String(this.f1938c, this.f1939d, i2 - this.f1939d, this.f1937b.name());
                        this.f1939d = i3 + 1;
                        return str2;
                    }
                }
                e eVar = new e(this, (this.e - this.f1939d) + 80);
                loop1:
                while (true) {
                    eVar.write(this.f1938c, this.f1939d, this.e - this.f1939d);
                    this.e = -1;
                    b();
                    i = this.f1939d;
                    while (true) {
                        if (i != this.e) {
                            if (this.f1938c[i] == 10) {
                                break loop1;
                            }
                            i++;
                        }
                    }
                }
                if (i != this.f1939d) {
                    eVar.write(this.f1938c, this.f1939d, i - this.f1939d);
                }
                this.f1939d = i + 1;
                String eVar2 = eVar.toString();
                return eVar2;
            }
            throw new IOException("LineReader is closed");
        }
    }

    public void close() {
        synchronized (this.f1936a) {
            if (this.f1938c != null) {
                this.f1938c = null;
                this.f1936a.close();
            }
        }
    }
}
