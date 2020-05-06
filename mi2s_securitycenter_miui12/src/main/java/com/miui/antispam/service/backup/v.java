package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class v extends GeneratedMessageLite implements w {

    /* renamed from: a  reason: collision with root package name */
    private static final v f2491a = new v(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<v> f2492b = new u();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2493c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2494d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public boolean f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public boolean h;
    private byte i;
    private int j;

    public static final class a extends GeneratedMessageLite.Builder<v, a> implements w {

        /* renamed from: a  reason: collision with root package name */
        private int f2495a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2496b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2497c = "";

        /* renamed from: d  reason: collision with root package name */
        private boolean f2498d;
        private boolean e;
        private boolean f;

        private a() {
            c();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        private void c() {
        }

        public a a(v vVar) {
            if (vVar == v.b()) {
                return this;
            }
            if (vVar.i()) {
                this.f2495a |= 1;
                this.f2496b = vVar.f2494d;
            }
            if (vVar.j()) {
                this.f2495a |= 2;
                this.f2497c = vVar.e;
            }
            if (vVar.h()) {
                b(vVar.c());
            }
            if (vVar.g()) {
                a(vVar.a());
            }
            if (vVar.k()) {
                c(vVar.f());
            }
            return this;
        }

        public a a(boolean z) {
            this.f2495a |= 8;
            this.e = z;
            return this;
        }

        public a b(boolean z) {
            this.f2495a |= 4;
            this.f2498d = z;
            return this;
        }

        public v build() {
            v buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public v buildPartial() {
            v vVar = new v((GeneratedMessageLite.Builder) this);
            int i = this.f2495a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = vVar.f2494d = this.f2496b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = vVar.e = this.f2497c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            boolean unused3 = vVar.f = this.f2498d;
            if ((i & 8) == 8) {
                i2 |= 8;
            }
            boolean unused4 = vVar.g = this.e;
            if ((i & 16) == 16) {
                i2 |= 16;
            }
            boolean unused5 = vVar.h = this.f;
            int unused6 = vVar.f2493c = i2;
            return vVar;
        }

        public a c(boolean z) {
            this.f2495a |= 16;
            this.f = z;
            return this;
        }

        public a clear() {
            v.super.clear();
            this.f2496b = "";
            this.f2495a &= -2;
            this.f2497c = "";
            this.f2495a &= -3;
            this.f2498d = false;
            this.f2495a &= -5;
            this.e = false;
            this.f2495a &= -9;
            this.f = false;
            this.f2495a &= -17;
            return this;
        }

        public v getDefaultInstanceForType() {
            return v.b();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((v) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            v vVar;
            v vVar2 = null;
            try {
                v vVar3 = (v) v.f2492b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (vVar3 != null) {
                    a(vVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e2) {
                vVar = (v) e2.getUnfinishedMessage();
                throw e2;
            } catch (Throwable th) {
                th = th;
                vVar2 = vVar;
            }
            if (vVar2 != null) {
                a(vVar2);
            }
            throw th;
        }
    }

    static {
        f2491a.m();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private v(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
        /*
            r4 = this;
            r4.<init>()
            r6 = -1
            r4.i = r6
            r4.j = r6
            r4.m()
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
            r2 = 24
            if (r1 == r2) goto L_0x004c
            r2 = 32
            if (r1 == r2) goto L_0x003f
            r2 = 40
            if (r1 == r2) goto L_0x0032
            goto L_0x0014
        L_0x0032:
            int r1 = r4.f2493c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r1 = r1 | 16
            r4.f2493c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            boolean r1 = r5.readBool()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.h = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x003f:
            int r1 = r4.f2493c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r1 = r1 | 8
            r4.f2493c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            boolean r1 = r5.readBool()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.g = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x004c:
            int r1 = r4.f2493c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r1 = r1 | 4
            r4.f2493c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            boolean r1 = r5.readBool()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x0059:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r2 = r4.f2493c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r2 = r2 | 2
            r4.f2493c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            goto L_0x0014
        L_0x0066:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            int r3 = r4.f2493c     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r2 = r2 | r3
            r4.f2493c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
            r4.f2494d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0085, IOException -> 0x0076 }
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
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.v.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private v(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.i = -1;
        this.j = -1;
    }

    private v(boolean z) {
        this.i = -1;
        this.j = -1;
    }

    public static v b() {
        return f2491a;
    }

    public static a c(v vVar) {
        a l = l();
        l.a(vVar);
        return l;
    }

    public static a l() {
        return a.b();
    }

    private void m() {
        this.f2494d = "";
        this.e = "";
        this.f = false;
        this.g = false;
        this.h = false;
    }

    public boolean a() {
        return this.g;
    }

    public boolean c() {
        return this.f;
    }

    public ByteString d() {
        Object obj = this.f2494d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2494d = copyFromUtf8;
        return copyFromUtf8;
    }

    public ByteString e() {
        Object obj = this.e;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.e = copyFromUtf8;
        return copyFromUtf8;
    }

    public boolean f() {
        return this.h;
    }

    public boolean g() {
        return (this.f2493c & 8) == 8;
    }

    public v getDefaultInstanceForType() {
        return f2491a;
    }

    public Parser<v> getParserForType() {
        return f2492b;
    }

    public int getSerializedSize() {
        int i2 = this.j;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        if ((this.f2493c & 1) == 1) {
            i3 = 0 + CodedOutputStream.computeBytesSize(1, d());
        }
        if ((this.f2493c & 2) == 2) {
            i3 += CodedOutputStream.computeBytesSize(2, e());
        }
        if ((this.f2493c & 4) == 4) {
            i3 += CodedOutputStream.computeBoolSize(3, this.f);
        }
        if ((this.f2493c & 8) == 8) {
            i3 += CodedOutputStream.computeBoolSize(4, this.g);
        }
        if ((this.f2493c & 16) == 16) {
            i3 += CodedOutputStream.computeBoolSize(5, this.h);
        }
        this.j = i3;
        return i3;
    }

    public boolean h() {
        return (this.f2493c & 4) == 4;
    }

    public boolean i() {
        return (this.f2493c & 1) == 1;
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
        return (this.f2493c & 2) == 2;
    }

    public boolean k() {
        return (this.f2493c & 16) == 16;
    }

    public a newBuilderForType() {
        return l();
    }

    public a toBuilder() {
        return c(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return v.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2493c & 1) == 1) {
            codedOutputStream.writeBytes(1, d());
        }
        if ((this.f2493c & 2) == 2) {
            codedOutputStream.writeBytes(2, e());
        }
        if ((this.f2493c & 4) == 4) {
            codedOutputStream.writeBool(3, this.f);
        }
        if ((this.f2493c & 8) == 8) {
            codedOutputStream.writeBool(4, this.g);
        }
        if ((this.f2493c & 16) == 16) {
            codedOutputStream.writeBool(5, this.h);
        }
    }
}
