package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

/* renamed from: com.miui.antispam.service.backup.j  reason: case insensitive filesystem */
public final class C0206j extends GeneratedMessageLite implements k {

    /* renamed from: a  reason: collision with root package name */
    private static final C0206j f2459a = new C0206j(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<C0206j> f2460b = new C0205i();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2461c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2462d;
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
    private byte j;
    private int k;

    /* renamed from: com.miui.antispam.service.backup.j$a */
    public static final class a extends GeneratedMessageLite.Builder<C0206j, a> implements k {

        /* renamed from: a  reason: collision with root package name */
        private int f2463a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2464b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2465c = "";

        /* renamed from: d  reason: collision with root package name */
        private Object f2466d = "";
        private Object e = "";
        private int f;
        private int g = 1;

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
            this.f2463a |= 32;
            this.g = i;
            return this;
        }

        public a a(C0206j jVar) {
            if (jVar == C0206j.a()) {
                return this;
            }
            if (jVar.j()) {
                this.f2463a |= 1;
                this.f2464b = jVar.f2462d;
            }
            if (jVar.k()) {
                this.f2463a |= 2;
                this.f2465c = jVar.e;
            }
            if (jVar.m()) {
                this.f2463a |= 4;
                this.f2466d = jVar.f;
            }
            if (jVar.l()) {
                this.f2463a |= 8;
                this.e = jVar.g;
            }
            if (jVar.o()) {
                b(jVar.i());
            }
            if (jVar.n()) {
                a(jVar.h());
            }
            return this;
        }

        public a a(String str) {
            if (str != null) {
                this.f2463a |= 8;
                this.e = str;
                return this;
            }
            throw new NullPointerException();
        }

        public a b(int i) {
            this.f2463a |= 16;
            this.f = i;
            return this;
        }

        public a b(String str) {
            if (str != null) {
                this.f2463a |= 4;
                this.f2466d = str;
                return this;
            }
            throw new NullPointerException();
        }

        public C0206j build() {
            C0206j buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public C0206j buildPartial() {
            C0206j jVar = new C0206j((GeneratedMessageLite.Builder) this);
            int i = this.f2463a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = jVar.f2462d = this.f2464b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = jVar.e = this.f2465c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            Object unused3 = jVar.f = this.f2466d;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            Object unused4 = jVar.g = this.e;
            if ((i & 16) == 16) {
                i2 |= 16;
            }
            int unused5 = jVar.h = this.f;
            if ((i & 32) == 32) {
                i2 |= 32;
            }
            int unused6 = jVar.i = this.g;
            int unused7 = jVar.f2461c = i2;
            return jVar;
        }

        public a clear() {
            C0206j.super.clear();
            this.f2464b = "";
            this.f2463a &= -2;
            this.f2465c = "";
            this.f2463a &= -3;
            this.f2466d = "";
            this.f2463a &= -5;
            this.e = "";
            this.f2463a &= -9;
            this.f = 0;
            this.f2463a &= -17;
            this.g = 1;
            this.f2463a &= -33;
            return this;
        }

        public C0206j getDefaultInstanceForType() {
            return C0206j.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((C0206j) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            C0206j jVar;
            C0206j jVar2 = null;
            try {
                C0206j jVar3 = (C0206j) C0206j.f2460b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (jVar3 != null) {
                    a(jVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e2) {
                jVar = (C0206j) e2.getUnfinishedMessage();
                throw e2;
            } catch (Throwable th) {
                th = th;
                jVar2 = jVar;
            }
            if (jVar2 != null) {
                a(jVar2);
            }
            throw th;
        }
    }

    static {
        f2459a.q();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private C0206j(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
        /*
            r4 = this;
            r4.<init>()
            r6 = -1
            r4.j = r6
            r4.k = r6
            r4.q()
            com.google.protobuf.ByteString$Output r6 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r6 = com.google.protobuf.CodedOutputStream.newInstance(r6)
            r0 = 0
        L_0x0014:
            if (r0 != 0) goto L_0x00a6
            int r1 = r5.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r2 = 1
            if (r1 == 0) goto L_0x0083
            r3 = 10
            if (r1 == r3) goto L_0x0077
            r2 = 18
            if (r1 == r2) goto L_0x006a
            r2 = 26
            if (r1 == r2) goto L_0x005d
            r2 = 34
            if (r1 == r2) goto L_0x0050
            r2 = 40
            if (r1 == r2) goto L_0x0043
            r2 = 48
            if (r1 == r2) goto L_0x0036
            goto L_0x0014
        L_0x0036:
            int r1 = r4.f2461c     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r1 = r1 | 32
            r4.f2461c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r4.i = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            goto L_0x0014
        L_0x0043:
            int r1 = r4.f2461c     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r1 = r1 | 16
            r4.f2461c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r4.h = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            goto L_0x0014
        L_0x0050:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            int r2 = r4.f2461c     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r2 = r2 | 8
            r4.f2461c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r4.g = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            goto L_0x0014
        L_0x005d:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            int r2 = r4.f2461c     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r2 = r2 | 4
            r4.f2461c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            goto L_0x0014
        L_0x006a:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            int r2 = r4.f2461c     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r2 = r2 | 2
            r4.f2461c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            goto L_0x0014
        L_0x0077:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            int r3 = r4.f2461c     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r2 = r2 | r3
            r4.f2461c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            r4.f2462d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0096, IOException -> 0x0087 }
            goto L_0x0014
        L_0x0083:
            r0 = r2
            goto L_0x0014
        L_0x0085:
            r5 = move-exception
            goto L_0x009c
        L_0x0087:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r0 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x0085 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x0085 }
            r0.<init>(r5)     // Catch:{ all -> 0x0085 }
            com.google.protobuf.InvalidProtocolBufferException r5 = r0.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0085 }
            throw r5     // Catch:{ all -> 0x0085 }
        L_0x0096:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r5 = r5.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0085 }
            throw r5     // Catch:{ all -> 0x0085 }
        L_0x009c:
            r6.flush()     // Catch:{ IOException -> 0x00a2, all -> 0x00a0 }
            goto L_0x00a2
        L_0x00a0:
            r5 = move-exception
            throw r5
        L_0x00a2:
            r4.makeExtensionsImmutable()
            throw r5
        L_0x00a6:
            r6.flush()     // Catch:{ IOException -> 0x00ac, all -> 0x00aa }
            goto L_0x00ac
        L_0x00aa:
            r5 = move-exception
            throw r5
        L_0x00ac:
            r4.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.C0206j.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private C0206j(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.j = -1;
        this.k = -1;
    }

    private C0206j(boolean z) {
        this.j = -1;
        this.k = -1;
    }

    public static C0206j a() {
        return f2459a;
    }

    public static a e(C0206j jVar) {
        a p = p();
        p.a(jVar);
        return p;
    }

    public static a p() {
        return a.b();
    }

    private void q() {
        this.f2462d = "";
        this.e = "";
        this.f = "";
        this.g = "";
        this.h = 0;
        this.i = 1;
    }

    public ByteString b() {
        Object obj = this.f2462d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2462d = copyFromUtf8;
        return copyFromUtf8;
    }

    public ByteString c() {
        Object obj = this.e;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.e = copyFromUtf8;
        return copyFromUtf8;
    }

    public String d() {
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

    public ByteString e() {
        Object obj = this.g;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.g = copyFromUtf8;
        return copyFromUtf8;
    }

    public String f() {
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

    public ByteString g() {
        Object obj = this.f;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f = copyFromUtf8;
        return copyFromUtf8;
    }

    public C0206j getDefaultInstanceForType() {
        return f2459a;
    }

    public Parser<C0206j> getParserForType() {
        return f2460b;
    }

    public int getSerializedSize() {
        int i2 = this.k;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        if ((this.f2461c & 1) == 1) {
            i3 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2461c & 2) == 2) {
            i3 += CodedOutputStream.computeBytesSize(2, c());
        }
        if ((this.f2461c & 4) == 4) {
            i3 += CodedOutputStream.computeBytesSize(3, g());
        }
        if ((this.f2461c & 8) == 8) {
            i3 += CodedOutputStream.computeBytesSize(4, e());
        }
        if ((this.f2461c & 16) == 16) {
            i3 += CodedOutputStream.computeInt32Size(5, this.h);
        }
        if ((this.f2461c & 32) == 32) {
            i3 += CodedOutputStream.computeInt32Size(6, this.i);
        }
        this.k = i3;
        return i3;
    }

    public int h() {
        return this.i;
    }

    public int i() {
        return this.h;
    }

    public final boolean isInitialized() {
        byte b2 = this.j;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.j = 1;
        return true;
    }

    public boolean j() {
        return (this.f2461c & 1) == 1;
    }

    public boolean k() {
        return (this.f2461c & 2) == 2;
    }

    public boolean l() {
        return (this.f2461c & 8) == 8;
    }

    public boolean m() {
        return (this.f2461c & 4) == 4;
    }

    public boolean n() {
        return (this.f2461c & 32) == 32;
    }

    public a newBuilderForType() {
        return p();
    }

    public boolean o() {
        return (this.f2461c & 16) == 16;
    }

    public a toBuilder() {
        return e(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return C0206j.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2461c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2461c & 2) == 2) {
            codedOutputStream.writeBytes(2, c());
        }
        if ((this.f2461c & 4) == 4) {
            codedOutputStream.writeBytes(3, g());
        }
        if ((this.f2461c & 8) == 8) {
            codedOutputStream.writeBytes(4, e());
        }
        if ((this.f2461c & 16) == 16) {
            codedOutputStream.writeInt32(5, this.h);
        }
        if ((this.f2461c & 32) == 32) {
            codedOutputStream.writeInt32(6, this.i);
        }
    }
}
