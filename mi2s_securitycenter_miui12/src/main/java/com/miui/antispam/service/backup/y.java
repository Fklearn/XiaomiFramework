package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

public final class y extends GeneratedMessageLite implements z {

    /* renamed from: a  reason: collision with root package name */
    private static final y f2499a = new y(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<y> f2500b = new x();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2501c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2502d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public boolean f;
    private byte g;
    private int h;

    public static final class a extends GeneratedMessageLite.Builder<y, a> implements z {

        /* renamed from: a  reason: collision with root package name */
        private int f2503a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2504b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2505c = "";

        /* renamed from: d  reason: collision with root package name */
        private boolean f2506d = true;

        private a() {
            c();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        private void c() {
        }

        public a a(y yVar) {
            if (yVar == y.a()) {
                return this;
            }
            if (yVar.e()) {
                this.f2503a |= 1;
                this.f2504b = yVar.f2502d;
            }
            if (yVar.f()) {
                this.f2503a |= 2;
                this.f2505c = yVar.e;
            }
            if (yVar.g()) {
                a(yVar.d());
            }
            return this;
        }

        public a a(boolean z) {
            this.f2503a |= 4;
            this.f2506d = z;
            return this;
        }

        public y build() {
            y buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public y buildPartial() {
            y yVar = new y((GeneratedMessageLite.Builder) this);
            int i = this.f2503a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = yVar.f2502d = this.f2504b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = yVar.e = this.f2505c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            boolean unused3 = yVar.f = this.f2506d;
            int unused4 = yVar.f2501c = i2;
            return yVar;
        }

        public a clear() {
            y.super.clear();
            this.f2504b = "";
            this.f2503a &= -2;
            this.f2505c = "";
            this.f2503a &= -3;
            this.f2506d = true;
            this.f2503a &= -5;
            return this;
        }

        public y getDefaultInstanceForType() {
            return y.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((y) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            y yVar;
            y yVar2 = null;
            try {
                y yVar3 = (y) y.f2500b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (yVar3 != null) {
                    a(yVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e) {
                yVar = (y) e.getUnfinishedMessage();
                throw e;
            } catch (Throwable th) {
                th = th;
                yVar2 = yVar;
            }
            if (yVar2 != null) {
                a(yVar2);
            }
            throw th;
        }
    }

    static {
        f2499a.i();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private y(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
        /*
            r4 = this;
            r4.<init>()
            r6 = -1
            r4.g = r6
            r4.h = r6
            r4.i()
            com.google.protobuf.ByteString$Output r6 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r6 = com.google.protobuf.CodedOutputStream.newInstance(r6)
            r0 = 0
        L_0x0014:
            if (r0 != 0) goto L_0x0073
            int r1 = r5.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r2 = 1
            if (r1 == 0) goto L_0x0050
            r3 = 10
            if (r1 == r3) goto L_0x0044
            r2 = 18
            if (r1 == r2) goto L_0x0037
            r2 = 24
            if (r1 == r2) goto L_0x002a
            goto L_0x0014
        L_0x002a:
            int r1 = r4.f2501c     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r1 = r1 | 4
            r4.f2501c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            boolean r1 = r5.readBool()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            goto L_0x0014
        L_0x0037:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            int r2 = r4.f2501c     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r2 = r2 | 2
            r4.f2501c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            goto L_0x0014
        L_0x0044:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            int r3 = r4.f2501c     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r2 = r2 | r3
            r4.f2501c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r4.f2502d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            goto L_0x0014
        L_0x0050:
            r0 = r2
            goto L_0x0014
        L_0x0052:
            r5 = move-exception
            goto L_0x0069
        L_0x0054:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r0 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x0052 }
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x0052 }
            r0.<init>(r5)     // Catch:{ all -> 0x0052 }
            com.google.protobuf.InvalidProtocolBufferException r5 = r0.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0052 }
            throw r5     // Catch:{ all -> 0x0052 }
        L_0x0063:
            r5 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r5 = r5.setUnfinishedMessage(r4)     // Catch:{ all -> 0x0052 }
            throw r5     // Catch:{ all -> 0x0052 }
        L_0x0069:
            r6.flush()     // Catch:{ IOException -> 0x006f, all -> 0x006d }
            goto L_0x006f
        L_0x006d:
            r5 = move-exception
            throw r5
        L_0x006f:
            r4.makeExtensionsImmutable()
            throw r5
        L_0x0073:
            r6.flush()     // Catch:{ IOException -> 0x0079, all -> 0x0077 }
            goto L_0x0079
        L_0x0077:
            r5 = move-exception
            throw r5
        L_0x0079:
            r4.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.y.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private y(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.g = -1;
        this.h = -1;
    }

    private y(boolean z) {
        this.g = -1;
        this.h = -1;
    }

    public static y a() {
        return f2499a;
    }

    public static a c(y yVar) {
        a h2 = h();
        h2.a(yVar);
        return h2;
    }

    public static a h() {
        return a.b();
    }

    private void i() {
        this.f2502d = "";
        this.e = "";
        this.f = true;
    }

    public ByteString b() {
        Object obj = this.f2502d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2502d = copyFromUtf8;
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

    public boolean d() {
        return this.f;
    }

    public boolean e() {
        return (this.f2501c & 1) == 1;
    }

    public boolean f() {
        return (this.f2501c & 2) == 2;
    }

    public boolean g() {
        return (this.f2501c & 4) == 4;
    }

    public y getDefaultInstanceForType() {
        return f2499a;
    }

    public Parser<y> getParserForType() {
        return f2500b;
    }

    public int getSerializedSize() {
        int i = this.h;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        if ((this.f2501c & 1) == 1) {
            i2 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2501c & 2) == 2) {
            i2 += CodedOutputStream.computeBytesSize(2, c());
        }
        if ((this.f2501c & 4) == 4) {
            i2 += CodedOutputStream.computeBoolSize(3, this.f);
        }
        this.h = i2;
        return i2;
    }

    public final boolean isInitialized() {
        byte b2 = this.g;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.g = 1;
        return true;
    }

    public a newBuilderForType() {
        return h();
    }

    public a toBuilder() {
        return c(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return y.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2501c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2501c & 2) == 2) {
            codedOutputStream.writeBytes(2, c());
        }
        if ((this.f2501c & 4) == 4) {
            codedOutputStream.writeBool(3, this.f);
        }
    }
}
