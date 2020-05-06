package com.miui.antispam.service.backup;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;

/* renamed from: com.miui.antispam.service.backup.g  reason: case insensitive filesystem */
public final class C0203g extends GeneratedMessageLite implements C0204h {

    /* renamed from: a  reason: collision with root package name */
    private static final C0203g f2451a = new C0203g(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<C0203g> f2452b = new C0202f();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2453c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Object f2454d;
    /* access modifiers changed from: private */
    public Object e;
    /* access modifiers changed from: private */
    public boolean f;
    private byte g;
    private int h;

    /* renamed from: com.miui.antispam.service.backup.g$a */
    public static final class a extends GeneratedMessageLite.Builder<C0203g, a> implements C0204h {

        /* renamed from: a  reason: collision with root package name */
        private int f2455a;

        /* renamed from: b  reason: collision with root package name */
        private Object f2456b = "";

        /* renamed from: c  reason: collision with root package name */
        private Object f2457c = "";

        /* renamed from: d  reason: collision with root package name */
        private boolean f2458d = true;

        private a() {
            c();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        private void c() {
        }

        public a a(C0203g gVar) {
            if (gVar == C0203g.a()) {
                return this;
            }
            if (gVar.e()) {
                this.f2455a |= 1;
                this.f2456b = gVar.f2454d;
            }
            if (gVar.f()) {
                this.f2455a |= 2;
                this.f2457c = gVar.e;
            }
            if (gVar.g()) {
                a(gVar.d());
            }
            return this;
        }

        public a a(boolean z) {
            this.f2455a |= 4;
            this.f2458d = z;
            return this;
        }

        public C0203g build() {
            C0203g buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public C0203g buildPartial() {
            C0203g gVar = new C0203g((GeneratedMessageLite.Builder) this);
            int i = this.f2455a;
            int i2 = 1;
            if ((i & 1) != 1) {
                i2 = 0;
            }
            Object unused = gVar.f2454d = this.f2456b;
            if ((i & 2) == 2) {
                i2 |= 2;
            }
            Object unused2 = gVar.e = this.f2457c;
            if ((i & 4) == 4) {
                i2 |= 4;
            }
            boolean unused3 = gVar.f = this.f2458d;
            int unused4 = gVar.f2453c = i2;
            return gVar;
        }

        public a clear() {
            C0203g.super.clear();
            this.f2456b = "";
            this.f2455a &= -2;
            this.f2457c = "";
            this.f2455a &= -3;
            this.f2458d = true;
            this.f2455a &= -5;
            return this;
        }

        public C0203g getDefaultInstanceForType() {
            return C0203g.a();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((C0203g) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            C0203g gVar;
            C0203g gVar2 = null;
            try {
                C0203g gVar3 = (C0203g) C0203g.f2452b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (gVar3 != null) {
                    a(gVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e) {
                gVar = (C0203g) e.getUnfinishedMessage();
                throw e;
            } catch (Throwable th) {
                th = th;
                gVar2 = gVar;
            }
            if (gVar2 != null) {
                a(gVar2);
            }
            throw th;
        }
    }

    static {
        f2451a.i();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private C0203g(com.google.protobuf.CodedInputStream r5, com.google.protobuf.ExtensionRegistryLite r6) {
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
            int r1 = r4.f2453c     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r1 = r1 | 4
            r4.f2453c = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            boolean r1 = r5.readBool()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r4.f = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            goto L_0x0014
        L_0x0037:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            int r2 = r4.f2453c     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r2 = r2 | 2
            r4.f2453c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r4.e = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            goto L_0x0014
        L_0x0044:
            com.google.protobuf.ByteString r1 = r5.readBytes()     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            int r3 = r4.f2453c     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r2 = r2 | r3
            r4.f2453c = r2     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
            r4.f2454d = r1     // Catch:{ InvalidProtocolBufferException -> 0x0063, IOException -> 0x0054 }
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
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.C0203g.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private C0203g(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.g = -1;
        this.h = -1;
    }

    private C0203g(boolean z) {
        this.g = -1;
        this.h = -1;
    }

    public static C0203g a() {
        return f2451a;
    }

    public static a c(C0203g gVar) {
        a h2 = h();
        h2.a(gVar);
        return h2;
    }

    public static a h() {
        return a.b();
    }

    private void i() {
        this.f2454d = "";
        this.e = "";
        this.f = true;
    }

    public ByteString b() {
        Object obj = this.f2454d;
        if (!(obj instanceof String)) {
            return (ByteString) obj;
        }
        ByteString copyFromUtf8 = ByteString.copyFromUtf8((String) obj);
        this.f2454d = copyFromUtf8;
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
        return (this.f2453c & 1) == 1;
    }

    public boolean f() {
        return (this.f2453c & 2) == 2;
    }

    public boolean g() {
        return (this.f2453c & 4) == 4;
    }

    public C0203g getDefaultInstanceForType() {
        return f2451a;
    }

    public Parser<C0203g> getParserForType() {
        return f2452b;
    }

    public int getSerializedSize() {
        int i = this.h;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        if ((this.f2453c & 1) == 1) {
            i2 = 0 + CodedOutputStream.computeBytesSize(1, b());
        }
        if ((this.f2453c & 2) == 2) {
            i2 += CodedOutputStream.computeBytesSize(2, c());
        }
        if ((this.f2453c & 4) == 4) {
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
        return C0203g.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2453c & 1) == 1) {
            codedOutputStream.writeBytes(1, b());
        }
        if ((this.f2453c & 2) == 2) {
            codedOutputStream.writeBytes(2, c());
        }
        if ((this.f2453c & 4) == 4) {
            codedOutputStream.writeBool(3, this.f);
        }
    }
}
