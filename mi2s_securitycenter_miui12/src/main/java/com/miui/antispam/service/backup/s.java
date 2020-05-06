package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class s extends GeneratedMessageLite implements t {

    /* renamed from: a  reason: collision with root package name */
    private static final s f2483a = new s(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<s> f2484b = new r();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2485c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2486d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public Object f;
    /* access modifiers changed from: private */
    public int g;
    /* access modifiers changed from: private */
    public int h;
    private byte i;
    private int j;

    public static final class a extends GeneratedMessageLite.Builder<s, a> implements t {

        /* renamed from: a  reason: collision with root package name */
        private int f2487a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2488b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2489c = "";

        /* renamed from: d  reason: collision with root package name */
        private Object f2490d = "";
        private int e = 1;
        private int f = 1;

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
            this.f2487a |= 16;
            this.f = i;
            return this;
        }

        public a a(s sVar) {
            if (sVar == s.a()) {
                return this;
            }
            if (sVar.h()) {
                this.f2487a |= 1;
                this.f2488b = sVar.f2486d;
            }
            if (sVar.i()) {
                this.f2487a |= 2;
                this.f2489c = sVar.e;
            }
            if (sVar.j()) {
                this.f2487a |= 4;
                this.f2490d = sVar.f;
            }
            if (sVar.l()) {
                b(sVar.g());
            }
            if (sVar.k()) {
                a(sVar.f());
            }
            return this;
        }

        public a a(String str) {
            if (str != null) {
                this.f2487a |= 4;
                this.f2490d = str;
                return this;
            }
            throw new NullPointerException();
        }

        public a b(int i) {
            this.f2487a |= 8;
            this.e = i;
            return this;
        }

        public s build() {
            s buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public s buildPartial() {
            s sVar = new s((GeneratedMessageLite.Builder) this);
            int i = this.f2487a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = sVar.f2486d = this.f2488b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = sVar.e = this.f2489c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            Object unused3 = sVar.f = this.f2490d;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            int unused4 = sVar.g = this.e;
            if ((i & 16) == 16) {
                i2 |= 16;
            }
            int unused5 = sVar.h = this.f;
            int unused6 = sVar.f2485c = i2;
            return sVar;
        }

        public a clear() {
            s.super.clear();
            this.f2488b = "";
            this.f2487a &= -2;
            this.f2489c = "";
            this.f2487a &= -3;
            this.f2490d = "";
            this.f2487a &= -5;
            this.e = 1;
            this.f2487a &= -9;
            this.f = 1;
            this.f2487a &= -17;
            return this;
        }

        public s getDefaultInstanceForType() {
            return s.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((s) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            s sVar;
            s sVar2 = null;
            try {
                s sVar3 = (s) s.f2484b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (sVar3 != null) {
                    a(sVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e2) {
                sVar = (s) e2.getUnfinishedMessage();
                throw e2;
            } catch (Throwable th) {
                th = th;
                sVar2 = sVar;
            }
            if (sVar2 != null) {
                a(sVar2);
            }
            throw th;
        }
    }

    static {
        f2483a.n();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private s(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
        /*
            r4 = this;
            r4.<init>()
            r6 = -1
            r4.i = r6
            r4.j = r6
            r4.n()
            com.google.protobuf.ByteString$Output r6 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r6 = com.google.protobuf.CodedOutputStream.newInstance(r6)
            r0 = 0
        L_0x0014:
            if (r0 != 0) goto L_0x0095
            int r1 = r5.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r2 = 1
            if (r1 == 0) goto L_0x0072
            r3 = 10
            if (r1 == r3) goto L_0x0066
            r2 = 18
            if (r1 == r2) goto L_0x0059
            r2 = 26
            if (r1 == r2) goto L_0x004c
            r2 = 32
            if (r1 == r2) goto L_0x003f
            r2 = 40
            if (r1 == r2) goto L_0x0032
            goto L_0x0014
        L_0x0032:
            int r1 = r4.f2485c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r1 = r1 | 16
            r4.f2485c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.h = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x003f:
            int r1 = r4.f2485c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r1 = r1 | 8
            r4.f2485c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r1 = r5.readInt32()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.g = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x004c:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r2 = r4.f2485c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r2 = r2 | 4
            r4.f2485c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x0059:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r2 = r4.f2485c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r2 = r2 | 2
            r4.f2485c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x0066:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r3 = r4.f2485c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r2 = r2 | r3
            r4.f2485c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.f2486d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x0072:
            r0 = r2
            goto L_0x0014
        L_0x0074:
            r5 = move-exception
            goto L_0x008b
        L_0x0076:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r0 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x0074 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x0074 }
            r0.<init>(r5)     // Catch:{ all -> 0x0074 }
            com.google.protobuf.InvalidProtocolBufferException r5 = r0.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0074 }
            throw r5     // Catch:{ all -> 0x0074 }
        L_0x0085:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r5 = r5.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0074 }
            throw r5     // Catch:{ all -> 0x0074 }
        L_0x008b:
            r6.flush()     // Catch:{ IOException -> 0x0091, all -> 0x008f }
            goto L_0x0091
        L_0x008f:
            r5 = move-exception
            throw r5
        L_0x0091:
            r4.makeExtensionsImmutable()
            throw r5
        L_0x0095:
            r6.flush()     // Catch:{ IOException -> 0x009b, all -> 0x0099 }
            goto L_0x009b
        L_0x0099:
            r5 = move-exception
            throw r5
        L_0x009b:
            r4.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.s.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private s(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.i = -1;
        this.j = -1;
    }

    private s(boolean z) {
        this.i = -1;
        this.j = -1;
    }

    public static s a() {
        return f2483a;
    }

    public static a d(s sVar) {
        a m = m();
        m.a(sVar);
        return m;
    }

    public static a m() {
        return a.b();
    }

    private void n() {
        this.f2486d = "";
        this.e = "";
        this.f = "";
        this.g = 1;
        this.h = 1;
    }

    public ByteString b() {
        Object obj = this.f2486d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2486d = copyFromUtf8;
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

    public ByteString e() {
        Object obj = this.f;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f = copyFromUtf8;
        return copyFromUtf8;
    }

    public int f() {
        return this.h;
    }

    public int g() {
        return this.g;
    }

    public s getDefaultInstanceForType() {
        return f2483a;
    }

    public Parser<s> getParserForType() {
        return f2484b;
    }

    public int getSerializedSize() {
        int i2 = this.j;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        if ((this.f2485c & 1) == 1) {
            i3 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2485c & 2) == 2) {
            i3 += CodedOutputStream.computeBytesSize(2, c());
        }
        if ((this.f2485c & 4) == 4) {
            i3 += CodedOutputStream.computeBytesSize(3, e());
        }
        if ((this.f2485c & 8) == 8) {
            i3 += CodedOutputStream.computeInt32Size(4, this.g);
        }
        if ((this.f2485c & 16) == 16) {
            i3 += CodedOutputStream.computeInt32Size(5, this.h);
        }
        this.j = i3;
        return i3;
    }

    public boolean h() {
        return (this.f2485c & 1) == 1;
    }

    public boolean i() {
        return (this.f2485c & 2) == 2;
    }

    public final boolean isInitialized() {
        byte b2 = this.i;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.i = 1;
        return true;
    }

    public boolean j() {
        return (this.f2485c & 4) == 4;
    }

    public boolean k() {
        return (this.f2485c & 16) == 16;
    }

    public boolean l() {
        return (this.f2485c & 8) == 8;
    }

    public a newBuilderForType() {
        return m();
    }

    public a toBuilder() {
        return d(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return s.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2485c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2485c & 2) == 2) {
            codedOutputStream.writeBytes(2, c());
        }
        if ((this.f2485c & 4) == 4) {
            codedOutputStream.writeBytes(3, e());
        }
        if ((this.f2485c & 8) == 8) {
            codedOutputStream.writeInt32(4, this.g);
        }
        if ((this.f2485c & 16) == 16) {
            codedOutputStream.writeInt32(5, this.h);
        }
    }
}
