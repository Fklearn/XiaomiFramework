package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class B extends GeneratedMessageLite implements C {

    /* renamed from: a  reason: collision with root package name */
    private static final B f2415a = new B(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<B> f2416b = new A();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2417c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2418d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public int g;
    private byte h;
    private int i;

    public static final class a extends GeneratedMessageLite.Builder<B, a> implements C {

        /* renamed from: a  reason: collision with root package name */
        private int f2419a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2420b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2421c = "";

        /* renamed from: d  reason: collision with root package name */
        private boolean f2422d = true;
        private int e = 1;

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
            this.f2419a |= 8;
            this.e = i;
            return this;
        }

        public a a(B b2) {
            if (b2 == B.a()) {
                return this;
            }
            if (b2.f()) {
                this.f2419a |= 1;
                this.f2420b = b2.f2418d;
            }
            if (b2.g()) {
                this.f2419a |= 2;
                this.f2421c = b2.e;
            }
            if (b2.i()) {
                a(b2.e());
            }
            if (b2.h()) {
                a(b2.d());
            }
            return this;
        }

        public a a(boolean z) {
            this.f2419a |= 4;
            this.f2422d = z;
            return this;
        }

        public B build() {
            B buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public B buildPartial() {
            B b2 = new B((GeneratedMessageLite.Builder) this);
            int i = this.f2419a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = b2.f2418d = this.f2420b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = b2.e = this.f2421c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            boolean unused3 = b2.f = this.f2422d;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            int unused4 = b2.g = this.e;
            int unused5 = b2.f2417c = i2;
            return b2;
        }

        public a clear() {
            B.super.clear();
            this.f2420b = "";
            this.f2419a &= -2;
            this.f2421c = "";
            this.f2419a &= -3;
            this.f2422d = true;
            this.f2419a &= -5;
            this.e = 1;
            this.f2419a &= -9;
            return this;
        }

        public B getDefaultInstanceForType() {
            return B.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((B) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            B b2;
            B b3 = null;
            try {
                B b4 = (B) B.f2416b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (b4 != null) {
                    a(b4);
                }
                return this;
            } catch (InvalidProtocolBufferException e2) {
                b2 = (B) e2.getUnfinishedMessage();
                throw e2;
            } catch (Throwable th) {
                th = th;
                b3 = b2;
            }
            if (b3 != null) {
                a(b3);
            }
            throw th;
        }
    }

    static {
        f2415a.k();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private B(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
        /*
            r4 = this;
            r4.<init>()
            r6 = -1
            r4.h = r6
            r4.i = r6
            r4.k()
            com.google.protobuf.ByteString$Output r6 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r6 = com.google.protobuf.CodedOutputStream.newInstance(r6)
            r0 = 0
        L_0x0014:
            if (r0 != 0) goto L_0x0084
            int r1 = r5.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r2 = 1
            if (r1 == 0) goto L_0x0061
            r3 = 10
            if (r1 == r3) goto L_0x0055
            r2 = 18
            if (r1 == r2) goto L_0x0048
            r2 = 24
            if (r1 == r2) goto L_0x003b
            r2 = 32
            if (r1 == r2) goto L_0x002e
            goto L_0x0014
        L_0x002e:
            int r1 = r4.f2417c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r1 = r1 | 8
            r4.f2417c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.g = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x003b:
            int r1 = r4.f2417c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r1 = r1 | 4
            r4.f2417c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            boolean r1 = r5.readBool()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x0048:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r2 = r4.f2417c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r2 = r2 | 2
            r4.f2417c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x0055:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r3 = r4.f2417c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r2 = r2 | r3
            r4.f2417c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.f2418d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x0061:
            r0 = r2
            goto L_0x0014
        L_0x0063:
            r5 = move-exception
            goto L_0x007a
        L_0x0065:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r0 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x0063 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x0063 }
            r0.<init>(r5)     // Catch:{ all -> 0x0063 }
            com.google.protobuf.InvalidProtocolBufferException r5 = r0.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0063 }
            throw r5     // Catch:{ all -> 0x0063 }
        L_0x0074:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r5 = r5.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0063 }
            throw r5     // Catch:{ all -> 0x0063 }
        L_0x007a:
            r6.flush()     // Catch:{ IOException -> 0x0080, all -> 0x007e }
            goto L_0x0080
        L_0x007e:
            r5 = move-exception
            throw r5
        L_0x0080:
            r4.makeExtensionsImmutable()
            throw r5
        L_0x0084:
            r6.flush()     // Catch:{ IOException -> 0x008a, all -> 0x0088 }
            goto L_0x008a
        L_0x0088:
            r5 = move-exception
            throw r5
        L_0x008a:
            r4.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.B.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private B(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.h = -1;
        this.i = -1;
    }

    private B(boolean z) {
        this.h = -1;
        this.i = -1;
    }

    public static B a() {
        return f2415a;
    }

    public static a c(B b2) {
        a j = j();
        j.a(b2);
        return j;
    }

    public static a j() {
        return a.b();
    }

    private void k() {
        this.f2418d = "";
        this.e = "";
        this.f = true;
        this.g = 1;
    }

    public ByteString b() {
        Object obj = this.f2418d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2418d = copyFromUtf8;
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

    public int d() {
        return this.g;
    }

    public boolean e() {
        return this.f;
    }

    public boolean f() {
        return (this.f2417c & 1) == 1;
    }

    public boolean g() {
        return (this.f2417c & 2) == 2;
    }

    public B getDefaultInstanceForType() {
        return f2415a;
    }

    public Parser<B> getParserForType() {
        return f2416b;
    }

    public int getSerializedSize() {
        int i2 = this.i;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        if ((this.f2417c & 1) == 1) {
            i3 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2417c & 2) == 2) {
            i3 += CodedOutputStream.computeBytesSize(2, c());
        }
        if ((this.f2417c & 4) == 4) {
            i3 += CodedOutputStream.computeBoolSize(3, this.f);
        }
        if ((this.f2417c & 8) == 8) {
            i3 += CodedOutputStream.computeInt32Size(4, this.g);
        }
        this.i = i3;
        return i3;
    }

    public boolean h() {
        return (this.f2417c & 8) == 8;
    }

    public boolean i() {
        return (this.f2417c & 4) == 4;
    }

    public final boolean isInitialized() {
        byte b2 = this.h;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.h = 1;
        return true;
    }

    public a newBuilderForType() {
        return j();
    }

    public a toBuilder() {
        return c(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return B.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2417c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2417c & 2) == 2) {
            codedOutputStream.writeBytes(2, c());
        }
        if ((this.f2417c & 4) == 4) {
            codedOutputStream.writeBool(3, this.f);
        }
        if ((this.f2417c & 8) == 8) {
            codedOutputStream.writeInt32(4, this.g);
        }
    }
}
