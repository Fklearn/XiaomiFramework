package com.miui.antispam.service.backup;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.miui.antispam.service.backup.C0200d;
import java.io.InputStream;

public final class I extends GeneratedMessageLite implements J {

    /* renamed from: a  reason: collision with root package name */
    private static final I f2431a = new I(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<I> f2432b = new H();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2433c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public C0200d f2434d;
    private byte e;
    private int f;

    public static final class a extends GeneratedMessageLite.Builder<I, a> implements J {

        /* renamed from: a  reason: collision with root package name */
        private int f2435a;

        /* renamed from: b  reason: collision with root package name */
        private C0200d f2436b = C0200d.c();

        private a() {
            c();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        private void c() {
        }

        public a a(I i) {
            if (i != I.b() && i.c()) {
                a(i.a());
            }
            return this;
        }

        public a a(C0200d dVar) {
            if ((this.f2435a & 1) == 1 && this.f2436b != C0200d.c()) {
                C0200d.a g = C0200d.g(this.f2436b);
                g.a(dVar);
                dVar = g.buildPartial();
            }
            this.f2436b = dVar;
            this.f2435a |= 1;
            return this;
        }

        public a b(C0200d dVar) {
            if (dVar != null) {
                this.f2436b = dVar;
                this.f2435a |= 1;
                return this;
            }
            throw new NullPointerException();
        }

        public I build() {
            I buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public I buildPartial() {
            I i = new I((GeneratedMessageLite.Builder) this);
            int i2 = 1;
            if ((this.f2435a & 1) != 1) {
                i2 = 0;
            }
            C0200d unused = i.f2434d = this.f2436b;
            int unused2 = i.f2433c = i2;
            return i;
        }

        public a clear() {
            I.super.clear();
            this.f2436b = C0200d.c();
            this.f2435a &= -2;
            return this;
        }

        public a clone() {
            a b2 = b();
            b2.a(buildPartial());
            return b2;
        }

        public I getDefaultInstanceForType() {
            return I.b();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((I) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            I i;
            I i2 = null;
            try {
                I i3 = (I) I.f2432b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (i3 != null) {
                    a(i3);
                }
                return this;
            } catch (InvalidProtocolBufferException e) {
                i = (I) e.getUnfinishedMessage();
                throw e;
            } catch (Throwable th) {
                th = th;
                i2 = i;
            }
            if (i2 != null) {
                a(i2);
            }
            throw th;
        }
    }

    static {
        f2431a.e();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private I(com.google.protobuf.CodedInputStream r6, com.google.protobuf.ExtensionRegistryLite r7) {
        /*
            r5 = this;
            r5.<init>()
            r0 = -1
            r5.e = r0
            r5.f = r0
            r5.e()
            com.google.protobuf.ByteString$Output r0 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r0 = com.google.protobuf.CodedOutputStream.newInstance(r0)
            r1 = 0
        L_0x0014:
            if (r1 != 0) goto L_0x006e
            int r2 = r6.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            r3 = 1
            if (r2 == 0) goto L_0x004b
            r4 = 66
            if (r2 == r4) goto L_0x0022
            goto L_0x0014
        L_0x0022:
            r2 = 0
            int r4 = r5.f2433c     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            r4 = r4 & r3
            if (r4 != r3) goto L_0x002e
            com.miui.antispam.service.backup.d r2 = r5.f2434d     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            com.miui.antispam.service.backup.d$a r2 = r2.toBuilder()     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
        L_0x002e:
            com.google.protobuf.Parser<com.miui.antispam.service.backup.d> r4 = com.miui.antispam.service.backup.C0200d.f2444b     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            com.google.protobuf.MessageLite r4 = r6.readMessage(r4, r7)     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            com.miui.antispam.service.backup.d r4 = (com.miui.antispam.service.backup.C0200d) r4     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            r5.f2434d = r4     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            if (r2 == 0) goto L_0x0045
            com.miui.antispam.service.backup.d r4 = r5.f2434d     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            r2.a((com.miui.antispam.service.backup.C0200d) r4)     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            com.miui.antispam.service.backup.d r2 = r2.buildPartial()     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            r5.f2434d = r2     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
        L_0x0045:
            int r2 = r5.f2433c     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            r2 = r2 | r3
            r5.f2433c = r2     // Catch:{ InvalidProtocolBufferException -> 0x005e, IOException -> 0x004f }
            goto L_0x0014
        L_0x004b:
            r1 = r3
            goto L_0x0014
        L_0x004d:
            r6 = move-exception
            goto L_0x0064
        L_0x004f:
            r6 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r7 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x004d }
            java.lang.String r6 = r6.getMessage()     // Catch:{ all -> 0x004d }
            r7.<init>(r6)     // Catch:{ all -> 0x004d }
            com.google.protobuf.InvalidProtocolBufferException r6 = r7.setUnfinishedMessage(r5)     // Catch:{ all -> 0x004d }
            throw r6     // Catch:{ all -> 0x004d }
        L_0x005e:
            r6 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r6 = r6.setUnfinishedMessage(r5)     // Catch:{ all -> 0x004d }
            throw r6     // Catch:{ all -> 0x004d }
        L_0x0064:
            r0.flush()     // Catch:{ IOException -> 0x006a, all -> 0x0068 }
            goto L_0x006a
        L_0x0068:
            r6 = move-exception
            throw r6
        L_0x006a:
            r5.makeExtensionsImmutable()
            throw r6
        L_0x006e:
            r0.flush()     // Catch:{ IOException -> 0x0074, all -> 0x0072 }
            goto L_0x0074
        L_0x0072:
            r6 = move-exception
            throw r6
        L_0x0074:
            r5.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.I.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private I(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.e = -1;
        this.f = -1;
    }

    private I(boolean z) {
        this.e = -1;
        this.f = -1;
    }

    public static a a(I i) {
        a d2 = d();
        d2.a(i);
        return d2;
    }

    public static I a(InputStream inputStream) {
        return (I) f2432b.parseFrom(inputStream);
    }

    public static I b() {
        return f2431a;
    }

    public static a d() {
        return a.b();
    }

    private void e() {
        this.f2434d = C0200d.c();
    }

    public C0200d a() {
        return this.f2434d;
    }

    public boolean c() {
        return (this.f2433c & 1) == 1;
    }

    public I getDefaultInstanceForType() {
        return f2431a;
    }

    public Parser<I> getParserForType() {
        return f2432b;
    }

    public int getSerializedSize() {
        int i = this.f;
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        if ((this.f2433c & 1) == 1) {
            i2 = 0 + CodedOutputStream.computeMessageSize(8, this.f2434d);
        }
        this.f = i2;
        return i2;
    }

    public final boolean isInitialized() {
        byte b2 = this.e;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.e = 1;
        return true;
    }

    public a newBuilderForType() {
        return d();
    }

    public a toBuilder() {
        return a(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return I.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        if ((this.f2433c & 1) == 1) {
            codedOutputStream.writeMessage(8, this.f2434d);
        }
    }
}
