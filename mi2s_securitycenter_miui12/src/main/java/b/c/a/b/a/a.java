package b.c.a.b.a;

import java.io.InputStream;

public class a extends InputStream {

    /* renamed from: a  reason: collision with root package name */
    private final InputStream f1951a;

    /* renamed from: b  reason: collision with root package name */
    private final int f1952b;

    public a(InputStream inputStream, int i) {
        this.f1951a = inputStream;
        this.f1952b = i;
    }

    public int available() {
        return this.f1952b;
    }

    public void close() {
        this.f1951a.close();
    }

    public void mark(int i) {
        this.f1951a.mark(i);
    }

    public boolean markSupported() {
        return this.f1951a.markSupported();
    }

    public int read() {
        return this.f1951a.read();
    }

    public int read(byte[] bArr) {
        return this.f1951a.read(bArr);
    }

    public int read(byte[] bArr, int i, int i2) {
        return this.f1951a.read(bArr, i, i2);
    }

    public void reset() {
        this.f1951a.reset();
    }

    public long skip(long j) {
        return this.f1951a.skip(j);
    }
}
