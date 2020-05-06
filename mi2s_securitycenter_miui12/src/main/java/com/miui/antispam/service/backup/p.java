package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class p extends GeneratedMessageLite implements q {

    /* renamed from: a  reason: collision with root package name */
    private static final p f2475a = new p(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<p> f2476b = new o();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2477c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2478d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public int f;
    /* access modifiers changed from: private */
    public int g;
    private byte h;
    private int i;

    public static final class a extends GeneratedMessageLite.Builder<p, a> implements q {

        /* renamed from: a  reason: collision with root package name */
        private int f2479a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2480b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2481c = "";

        /* renamed from: d  reason: collision with root package name */
        private int f2482d;
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
            this.f2479a |= 8;
            this.e = i;
            return this;
        }

        public a a(p pVar) {
            if (pVar == p.a()) {
                return this;
            }
            if (pVar.f()) {
                this.f2479a |= 1;
                this.f2480b = pVar.f2478d;
            }
            if (pVar.g()) {
                this.f2479a |= 2;
                this.f2481c = pVar.e;
            }
            if (pVar.i()) {
                b(pVar.e());
            }
            if (pVar.h()) {
                a(pVar.d());
            }
            return this;
        }

        public a b(int i) {
            this.f2479a |= 4;
            this.f2482d = i;
            return this;
        }

        public p build() {
            p buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public p buildPartial() {
            p pVar = new p((GeneratedMessageLite.Builder) this);
            int i = this.f2479a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = pVar.f2478d = this.f2480b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = pVar.e = this.f2481c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            int unused3 = pVar.f = this.f2482d;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            int unused4 = pVar.g = this.e;
            int unused5 = pVar.f2477c = i2;
            return pVar;
        }

        public a clear() {
            p.super.clear();
            this.f2480b = "";
            this.f2479a &= -2;
            this.f2481c = "";
            this.f2479a &= -3;
            this.f2482d = 0;
            this.f2479a &= -5;
            this.e = 1;
            this.f2479a &= -9;
            return this;
        }

        public p getDefaultInstanceForType() {
            return p.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((p) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            p pVar;
            p pVar2 = null;
            try {
                p pVar3 = (p) p.f2476b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (pVar3 != null) {
                    a(pVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e2) {
                pVar = (p) e2.getUnfinishedMessage();
                throw e2;
            } catch (Throwable th) {
                th = th;
                pVar2 = pVar;
            }
            if (pVar2 != null) {
                a(pVar2);
            }
            throw th;
        }
    }

    static {
        f2475a.k();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private p(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
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
            int r1 = r4.f2477c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r1 = r1 | 8
            r4.f2477c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.g = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x003b:
            int r1 = r4.f2477c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r1 = r1 | 4
            r4.f2477c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x0048:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r2 = r4.f2477c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r2 = r2 | 2
            r4.f2477c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            goto L_0x0014
        L_0x0055:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            int r3 = r4.f2477c     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r2 = r2 | r3
            r4.f2477c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
            r4.f2478d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0074, IOException -> 0x0065 }
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
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.p.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private p(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.h = -1;
        this.i = -1;
    }

    private p(boolean z) {
        this.h = -1;
        this.i = -1;
    }

    public static p a() {
        return f2475a;
    }

    public static a c(p pVar) {
        a j = j();
        j.a(pVar);
        return j;
    }

    public static a j() {
        return a.b();
    }

    private void k() {
        this.f2478d = "";
        this.e = "";
        this.f = 0;
        this.g = 1;
    }

    public ByteString b() {
        Object obj = this.f2478d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2478d = copyFromUtf8;
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

    public int e() {
        return this.f;
    }

    public boolean f() {
        return (this.f2477c & 1) == 1;
    }

    public boolean g() {
        return (this.f2477c & 2) == 2;
    }

    public p getDefaultInstanceForType() {
        return f2475a;
    }

    public Parser<p> getParserForType() {
        return f2476b;
    }

    public int getSerializedSize() {
        int i2 = this.i;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        if ((this.f2477c & 1) == 1) {
            i3 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2477c & 2) == 2) {
            i3 += CodedOutputStream.computeBytesSize(2, c());
        }
        if ((this.f2477c & 4) == 4) {
            i3 += CodedOutputStream.computeInt32Size(3, this.f);
        }
        if ((this.f2477c & 8) == 8) {
            i3 += CodedOutputStream.computeInt32Size(4, this.g);
        }
        this.i = i3;
        return i3;
    }

    public boolean h() {
        return (this.f2477c & 8) == 8;
    }

    public boolean i() {
        return (this.f2477c & 4) == 4;
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
        return p.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2477c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2477c & 2) == 2) {
            codedOutputStream.writeBytes(2, c());
        }
        if ((this.f2477c & 4) == 4) {
            codedOutputStream.writeInt32(3, this.f);
        }
        if ((this.f2477c & 8) == 8) {
            codedOutputStream.writeInt32(4, this.g);
        }
    }
}
