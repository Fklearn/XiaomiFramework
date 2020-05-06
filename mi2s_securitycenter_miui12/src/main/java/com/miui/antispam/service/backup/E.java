package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class E extends GeneratedMessageLite implements F {

    /* renamed from: a  reason: collision with root package name */
    private static final E f2423a = new E(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<E> f2424b = new D();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2425c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2426d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public Object f;
    /* access modifiers changed from: private */
    public Object g;
    /* access modifiers changed from: private */
    public int h;
    /* access modifiers changed from: private */
    public int i;
    /* access modifiers changed from: private */
    public int j;
    private byte k;
    private int l;

    public static final class a extends GeneratedMessageLite.Builder<E, a> implements F {

        /* renamed from: a  reason: collision with root package name */
        private int f2427a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2428b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2429c = "";

        /* renamed from: d  reason: collision with root package name */
        private Object f2430d = "";
        private Object e = "";
        private int f;
        private int g = 1;
        private int h = 1;

        private a() {
            c();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        private void c() {
        }

        public a a(int i) {
            this.f2427a |= 32;
            this.g = i;
            return this;
        }

        public a a(E e2) {
            if (e2 == E.a()) {
                return this;
            }
            if (e2.k()) {
                this.f2427a |= 1;
                this.f2428b = e2.f2426d;
            }
            if (e2.m()) {
                this.f2427a |= 2;
                this.f2429c = e2.e;
            }
            if (e2.o()) {
                this.f2427a |= 4;
                this.f2430d = e2.f;
            }
            if (e2.n()) {
                this.f2427a |= 8;
                this.e = e2.g;
            }
            if (e2.q()) {
                c(e2.j());
            }
            if (e2.l()) {
                a(e2.c());
            }
            if (e2.p()) {
                b(e2.i());
            }
            return this;
        }

        public a a(String str) {
            if (str != null) {
                this.f2427a |= 8;
                this.e = str;
                return this;
            }
            throw new NullPointerException();
        }

        public a b(int i) {
            this.f2427a |= 64;
            this.h = i;
            return this;
        }

        public a b(String str) {
            if (str != null) {
                this.f2427a |= 4;
                this.f2430d = str;
                return this;
            }
            throw new NullPointerException();
        }

        public E build() {
            E buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public E buildPartial() {
            E e2 = new E((GeneratedMessageLite.Builder) this);
            int i = this.f2427a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = e2.f2426d = this.f2428b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = e2.e = this.f2429c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            Object unused3 = e2.f = this.f2430d;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            Object unused4 = e2.g = this.e;
            if ((i & 16) == 16) {
                i2 |= 16;
            }
            int unused5 = e2.h = this.f;
            if ((i & 32) == 32) {
                i2 |= 32;
            }
            int unused6 = e2.i = this.g;
            if ((i & 64) == 64) {
                i2 |= 64;
            }
            int unused7 = e2.j = this.h;
            int unused8 = e2.f2425c = i2;
            return e2;
        }

        public a c(int i) {
            this.f2427a |= 16;
            this.f = i;
            return this;
        }

        public a clear() {
            E.super.clear();
            this.f2428b = "";
            this.f2427a &= -2;
            this.f2429c = "";
            this.f2427a &= -3;
            this.f2430d = "";
            this.f2427a &= -5;
            this.e = "";
            this.f2427a &= -9;
            this.f = 0;
            this.f2427a &= -17;
            this.g = 1;
            this.f2427a &= -33;
            this.h = 1;
            this.f2427a &= -65;
            return this;
        }

        public E getDefaultInstanceForType() {
            return E.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((E) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            E e2;
            E e3 = null;
            try {
                E e4 = (E) E.f2424b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (e4 != null) {
                    a(e4);
                }
                return this;
            } catch (InvalidProtocolBufferException e5) {
                e2 = (E) e5.getUnfinishedMessage();
                throw e5;
            } catch (Throwable th) {
                th = th;
                e3 = e2;
            }
            if (e3 != null) {
                a(e3);
            }
            throw th;
        }
    }

    static {
        f2423a.s();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private E(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
        /*
            r4 = this;
            r4.<init>()
            r6 = -1
            r4.k = r6
            r4.l = r6
            r4.s()
            com.google.protobuf.ByteString$Output r6 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r6 = com.google.protobuf.CodedOutputStream.newInstance(r6)
            r0 = 0
        L_0x0014:
            if (r0 != 0) goto L_0x00b8
            int r1 = r5.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r2 = 1
            if (r1 == 0) goto L_0x0094
            r3 = 10
            if (r1 == r3) goto L_0x0088
            r2 = 18
            if (r1 == r2) goto L_0x007b
            r2 = 26
            if (r1 == r2) goto L_0x006e
            r2 = 34
            if (r1 == r2) goto L_0x0061
            r2 = 40
            if (r1 == r2) goto L_0x0054
            r2 = 48
            if (r1 == r2) goto L_0x0047
            r2 = 56
            if (r1 == r2) goto L_0x003a
            goto L_0x0014
        L_0x003a:
            int r1 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r1 = r1 | 64
            r4.f2425c = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.j = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x0047:
            int r1 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r1 = r1 | 32
            r4.f2425c = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.i = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x0054:
            int r1 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r1 = r1 | 16
            r4.f2425c = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.h = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x0061:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r2 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r2 = r2 | 8
            r4.f2425c = r2     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.g = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x006e:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r2 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r2 = r2 | 4
            r4.f2425c = r2     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x007b:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r2 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r2 = r2 | 2
            r4.f2425c = r2     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x0088:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            int r3 = r4.f2425c     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r2 = r2 | r3
            r4.f2425c = r2     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            r4.f2426d = r1     // Catch:{ InvalidProtocolBufferException -> 0x00a8, IOException -> 0x0099 }
            goto L_0x0014
        L_0x0094:
            r0 = r2
            goto L_0x0014
        L_0x0097:
            r5 = move-exception
            goto L_0x00ae
        L_0x0099:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r0 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x0097 }
            r0.<init>(r5)     // Catch:{ all -> 0x0097 }
            com.google.protobuf.InvalidProtocolBufferException r5 = r0.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0097 }
            throw r5     // Catch:{ all -> 0x0097 }
        L_0x00a8:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r5 = r5.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0097 }
            throw r5     // Catch:{ all -> 0x0097 }
        L_0x00ae:
            r6.flush()     // Catch:{ IOException -> 0x00b4, all -> 0x00b2 }
            goto L_0x00b4
        L_0x00b2:
            r5 = move-exception
            throw r5
        L_0x00b4:
            r4.makeExtensionsImmutable()
            throw r5
        L_0x00b8:
            r6.flush()     // Catch:{ IOException -> 0x00be, all -> 0x00bc }
            goto L_0x00be
        L_0x00bc:
            r5 = move-exception
            throw r5
        L_0x00be:
            r4.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.E.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private E(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.k = -1;
        this.l = -1;
    }

    private E(boolean z) {
        this.k = -1;
        this.l = -1;
    }

    public static E a() {
        return f2423a;
    }

    public static a e(E e2) {
        a r = r();
        r.a(e2);
        return r;
    }

    public static a r() {
        return a.b();
    }

    private void s() {
        this.f2426d = "";
        this.e = "";
        this.f = "";
        this.g = "";
        this.h = 0;
        this.i = 1;
        this.j = 1;
    }

    public ByteString b() {
        Object obj = this.f2426d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2426d = copyFromUtf8;
        return copyFromUtf8;
    }

    public int c() {
        return this.i;
    }

    public ByteString d() {
        Object obj = this.e;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.e = copyFromUtf8;
        return copyFromUtf8;
    }

    public String e() {
        Object obj = this.g;
        if (obj instanceof String) {
            return (String) obj;
        }
        ByteString byteString = (ByteString) obj;
        String stringUtf8 = byteString.toStringUtf8();
        if (byteString.isValidUtf8()) {
            this.g = stringUtf8;
        }
        return stringUtf8;
    }

    public ByteString f() {
        Object obj = this.g;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.g = copyFromUtf8;
        return copyFromUtf8;
    }

    public String g() {
        Object obj = this.f;
        if (obj instanceof String) {
            return (String) obj;
        }
        ByteString byteString = (ByteString) obj;
        String stringUtf8 = byteString.toStringUtf8();
        if (byteString.isValidUtf8()) {
            this.f = stringUtf8;
        }
        return stringUtf8;
    }

    public E getDefaultInstanceForType() {
        return f2423a;
    }

    public Parser<E> getParserForType() {
        return f2424b;
    }

    public int getSerializedSize() {
        int i2 = this.l;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        if ((this.f2425c & 1) == 1) {
            i3 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2425c & 2) == 2) {
            i3 += CodedOutputStream.computeBytesSize(2, d());
        }
        if ((this.f2425c & 4) == 4) {
            i3 += CodedOutputStream.computeBytesSize(3, h());
        }
        if ((this.f2425c & 8) == 8) {
            i3 += CodedOutputStream.computeBytesSize(4, f());
        }
        if ((this.f2425c & 16) == 16) {
            i3 += CodedOutputStream.computeInt32Size(5, this.h);
        }
        if ((this.f2425c & 32) == 32) {
            i3 += CodedOutputStream.computeInt32Size(6, this.i);
        }
        if ((this.f2425c & 64) == 64) {
            i3 += CodedOutputStream.computeInt32Size(7, this.j);
        }
        this.l = i3;
        return i3;
    }

    public ByteString h() {
        Object obj = this.f;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f = copyFromUtf8;
        return copyFromUtf8;
    }

    public int i() {
        return this.j;
    }

    public final boolean isInitialized() {
        byte b2 = this.k;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.k = 1;
        return true;
    }

    public int j() {
        return this.h;
    }

    public boolean k() {
        return (this.f2425c & 1) == 1;
    }

    public boolean l() {
        return (this.f2425c & 32) == 32;
    }

    public boolean m() {
        return (this.f2425c & 2) == 2;
    }

    public boolean n() {
        return (this.f2425c & 8) == 8;
    }

    public a newBuilderForType() {
        return r();
    }

    public boolean o() {
        return (this.f2425c & 4) == 4;
    }

    public boolean p() {
        return (this.f2425c & 64) == 64;
    }

    public boolean q() {
        return (this.f2425c & 16) == 16;
    }

    public a toBuilder() {
        return e(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return E.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2425c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2425c & 2) == 2) {
            codedOutputStream.writeBytes(2, d());
        }
        if ((this.f2425c & 4) == 4) {
            codedOutputStream.writeBytes(3, h());
        }
        if ((this.f2425c & 8) == 8) {
            codedOutputStream.writeBytes(4, f());
        }
        if ((this.f2425c & 16) == 16) {
            codedOutputStream.writeInt32(5, this.h);
        }
        if ((this.f2425c & 32) == 32) {
            codedOutputStream.writeInt32(6, this.i);
        }
        if ((this.f2425c & 64) == 64) {
            codedOutputStream.writeInt32(7, this.j);
        }
    }
}
