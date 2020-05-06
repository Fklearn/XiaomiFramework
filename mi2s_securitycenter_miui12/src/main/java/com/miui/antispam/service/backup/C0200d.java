package com.miui.antispam.service.backup;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.miui.antispam.service.backup.C0203g;
import com.miui.antispam.service.backup.v;
import com.miui.antispam.service.backup.y;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* renamed from: com.miui.antispam.service.backup.d  reason: case insensitive filesystem */
public final class C0200d extends GeneratedMessageLite implements C0201e {

    /* renamed from: a  reason: collision with root package name */
    private static final C0200d f2443a = new C0200d(true);

    /* renamed from: b  reason: collision with root package name */
    public static Parser<C0200d> f2444b = new C0199c();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public int f2445c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public List<C0206j> f2446d;
    /* access modifiers changed from: private */
    public List<E> e;
    /* access modifiers changed from: private */
    public List<m> f;
    /* access modifiers changed from: private */
    public List<s> g;
    /* access modifiers changed from: private */
    public List<B> h;
    /* access modifiers changed from: private */
    public List<p> i;
    /* access modifiers changed from: private */
    public C0203g j;
    /* access modifiers changed from: private */
    public y k;
    /* access modifiers changed from: private */
    public v l;
    private byte m;
    private int n;

    /* renamed from: com.miui.antispam.service.backup.d$a */
    public static final class a extends GeneratedMessageLite.Builder<C0200d, a> implements C0201e {

        /* renamed from: a  reason: collision with root package name */
        private int f2447a;

        /* renamed from: b  reason: collision with root package name */
        private List<C0206j> f2448b = Collections.emptyList();

        /* renamed from: c  reason: collision with root package name */
        private List<E> f2449c = Collections.emptyList();

        /* renamed from: d  reason: collision with root package name */
        private List<m> f2450d = Collections.emptyList();
        private List<s> e = Collections.emptyList();
        private List<B> f = Collections.emptyList();
        private List<p> g = Collections.emptyList();
        private C0203g h = C0203g.a();
        private y i = y.a();
        private v j = v.b();

        private a() {
            i();
        }

        /* access modifiers changed from: private */
        public static a b() {
            return new a();
        }

        private void c() {
            if ((this.f2447a & 1) != 1) {
                this.f2448b = new ArrayList(this.f2448b);
                this.f2447a |= 1;
            }
        }

        private void d() {
            if ((this.f2447a & 4) != 4) {
                this.f2450d = new ArrayList(this.f2450d);
                this.f2447a |= 4;
            }
        }

        private void e() {
            if ((this.f2447a & 32) != 32) {
                this.g = new ArrayList(this.g);
                this.f2447a |= 32;
            }
        }

        private void f() {
            if ((this.f2447a & 8) != 8) {
                this.e = new ArrayList(this.e);
                this.f2447a |= 8;
            }
        }

        private void g() {
            if ((this.f2447a & 16) != 16) {
                this.f = new ArrayList(this.f);
                this.f2447a |= 16;
            }
        }

        private void h() {
            if ((this.f2447a & 2) != 2) {
                this.f2449c = new ArrayList(this.f2449c);
                this.f2447a |= 2;
            }
        }

        private void i() {
        }

        public a a(B b2) {
            if (b2 != null) {
                g();
                this.f.add(b2);
                return this;
            }
            throw new NullPointerException();
        }

        public a a(E e2) {
            if (e2 != null) {
                h();
                this.f2449c.add(e2);
                return this;
            }
            throw new NullPointerException();
        }

        public a a(C0200d dVar) {
            if (dVar == C0200d.c()) {
                return this;
            }
            if (!dVar.f2446d.isEmpty()) {
                if (this.f2448b.isEmpty()) {
                    this.f2448b = dVar.f2446d;
                    this.f2447a &= -2;
                } else {
                    c();
                    this.f2448b.addAll(dVar.f2446d);
                }
            }
            if (!dVar.e.isEmpty()) {
                if (this.f2449c.isEmpty()) {
                    this.f2449c = dVar.e;
                    this.f2447a &= -3;
                } else {
                    h();
                    this.f2449c.addAll(dVar.e);
                }
            }
            if (!dVar.f.isEmpty()) {
                if (this.f2450d.isEmpty()) {
                    this.f2450d = dVar.f;
                    this.f2447a &= -5;
                } else {
                    d();
                    this.f2450d.addAll(dVar.f);
                }
            }
            if (!dVar.g.isEmpty()) {
                if (this.e.isEmpty()) {
                    this.e = dVar.g;
                    this.f2447a &= -9;
                } else {
                    f();
                    this.e.addAll(dVar.g);
                }
            }
            if (!dVar.h.isEmpty()) {
                if (this.f.isEmpty()) {
                    this.f = dVar.h;
                    this.f2447a &= -17;
                } else {
                    g();
                    this.f.addAll(dVar.h);
                }
            }
            if (!dVar.i.isEmpty()) {
                if (this.g.isEmpty()) {
                    this.g = dVar.i;
                    this.f2447a &= -33;
                } else {
                    e();
                    this.g.addAll(dVar.i);
                }
            }
            if (dVar.k()) {
                a(dVar.a());
            }
            if (dVar.m()) {
                a(dVar.h());
            }
            if (dVar.l()) {
                a(dVar.g());
            }
            return this;
        }

        public a a(C0203g gVar) {
            if ((this.f2447a & 64) == 64 && this.h != C0203g.a()) {
                C0203g.a c2 = C0203g.c(this.h);
                c2.a(gVar);
                gVar = c2.buildPartial();
            }
            this.h = gVar;
            this.f2447a |= 64;
            return this;
        }

        public a a(C0206j jVar) {
            if (jVar != null) {
                c();
                this.f2448b.add(jVar);
                return this;
            }
            throw new NullPointerException();
        }

        public a a(m mVar) {
            if (mVar != null) {
                d();
                this.f2450d.add(mVar);
                return this;
            }
            throw new NullPointerException();
        }

        public a a(p pVar) {
            if (pVar != null) {
                e();
                this.g.add(pVar);
                return this;
            }
            throw new NullPointerException();
        }

        public a a(s sVar) {
            if (sVar != null) {
                f();
                this.e.add(sVar);
                return this;
            }
            throw new NullPointerException();
        }

        public a a(v vVar) {
            if ((this.f2447a & 256) == 256 && this.j != v.b()) {
                v.a c2 = v.c(this.j);
                c2.a(vVar);
                vVar = c2.buildPartial();
            }
            this.j = vVar;
            this.f2447a |= 256;
            return this;
        }

        public a a(y yVar) {
            if ((this.f2447a & 128) == 128 && this.i != y.a()) {
                y.a c2 = y.c(this.i);
                c2.a(yVar);
                yVar = c2.buildPartial();
            }
            this.i = yVar;
            this.f2447a |= 128;
            return this;
        }

        public a b(C0203g gVar) {
            if (gVar != null) {
                this.h = gVar;
                this.f2447a |= 64;
                return this;
            }
            throw new NullPointerException();
        }

        public a b(v vVar) {
            if (vVar != null) {
                this.j = vVar;
                this.f2447a |= 256;
                return this;
            }
            throw new NullPointerException();
        }

        public a b(y yVar) {
            if (yVar != null) {
                this.i = yVar;
                this.f2447a |= 128;
                return this;
            }
            throw new NullPointerException();
        }

        public C0200d build() {
            C0200d buildPartial = buildPartial();
            if (buildPartial.isInitialized()) {
                return buildPartial;
            }
            throw GeneratedMessageLite.Builder.newUninitializedMessageException(buildPartial);
        }

        public C0200d buildPartial() {
            C0200d dVar = new C0200d((GeneratedMessageLite.Builder) this);
            int i2 = this.f2447a;
            int i3 = 1;
            if ((i2 & 1) == 1) {
                this.f2448b = Collections.unmodifiableList(this.f2448b);
                this.f2447a &= -2;
            }
            List unused = dVar.f2446d = this.f2448b;
            if ((this.f2447a & 2) == 2) {
                this.f2449c = Collections.unmodifiableList(this.f2449c);
                this.f2447a &= -3;
            }
            List unused2 = dVar.e = this.f2449c;
            if ((this.f2447a & 4) == 4) {
                this.f2450d = Collections.unmodifiableList(this.f2450d);
                this.f2447a &= -5;
            }
            List unused3 = dVar.f = this.f2450d;
            if ((this.f2447a & 8) == 8) {
                this.e = Collections.unmodifiableList(this.e);
                this.f2447a &= -9;
            }
            List unused4 = dVar.g = this.e;
            if ((this.f2447a & 16) == 16) {
                this.f = Collections.unmodifiableList(this.f);
                this.f2447a &= -17;
            }
            List unused5 = dVar.h = this.f;
            if ((this.f2447a & 32) == 32) {
                this.g = Collections.unmodifiableList(this.g);
                this.f2447a &= -33;
            }
            List unused6 = dVar.i = this.g;
            if ((i2 & 64) != 64) {
                i3 = 0;
            }
            C0203g unused7 = dVar.j = this.h;
            if ((i2 & 128) == 128) {
                i3 |= 2;
            }
            y unused8 = dVar.k = this.i;
            if ((i2 & 256) == 256) {
                i3 |= 4;
            }
            v unused9 = dVar.l = this.j;
            int unused10 = dVar.f2445c = i3;
            return dVar;
        }

        public a clear() {
            C0200d.super.clear();
            this.f2448b = Collections.emptyList();
            this.f2447a &= -2;
            this.f2449c = Collections.emptyList();
            this.f2447a &= -3;
            this.f2450d = Collections.emptyList();
            this.f2447a &= -5;
            this.e = Collections.emptyList();
            this.f2447a &= -9;
            this.f = Collections.emptyList();
            this.f2447a &= -17;
            this.g = Collections.emptyList();
            this.f2447a &= -33;
            this.h = C0203g.a();
            this.f2447a &= -65;
            this.i = y.a();
            this.f2447a &= -129;
            this.j = v.b();
            this.f2447a &= -257;
            return this;
        }

        public C0200d getDefaultInstanceForType() {
            return C0200d.c();
        }

        public final boolean isInitialized() {
            return true;
        }

        public /* bridge */ /* synthetic */ GeneratedMessageLite.Builder mergeFrom(GeneratedMessageLite generatedMessageLite) {
            a((C0200d) generatedMessageLite);
            return this;
        }

        public a mergeFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) {
            C0200d dVar;
            C0200d dVar2 = null;
            try {
                C0200d dVar3 = (C0200d) C0200d.f2444b.parsePartialFrom(codedInputStream, extensionRegistryLite);
                if (dVar3 != null) {
                    a(dVar3);
                }
                return this;
            } catch (InvalidProtocolBufferException e2) {
                dVar = (C0200d) e2.getUnfinishedMessage();
                throw e2;
            } catch (Throwable th) {
                th = th;
                dVar2 = dVar;
            }
            if (dVar2 != null) {
                a(dVar2);
            }
            throw th;
        }
    }

    static {
        f2443a.o();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: com.miui.antispam.service.backup.g$a} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: com.miui.antispam.service.backup.y$a} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: com.miui.antispam.service.backup.v$a} */
    /* JADX WARNING: type inference failed for: r11v0 */
    /* JADX WARNING: type inference failed for: r11v7 */
    /* JADX WARNING: type inference failed for: r11v8 */
    /* JADX WARNING: type inference failed for: r11v9 */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private C0200d(com.google.protobuf.CodedInputStream r13, com.google.protobuf.ExtensionRegistryLite r14) {
        /*
            r12 = this;
            r12.<init>()
            r0 = -1
            r12.m = r0
            r12.n = r0
            r12.o()
            com.google.protobuf.ByteString$Output r0 = com.google.protobuf.ByteString.newOutput()
            com.google.protobuf.CodedOutputStream r0 = com.google.protobuf.CodedOutputStream.newInstance(r0)
            r1 = 0
            r2 = r1
        L_0x0015:
            r3 = 32
            r4 = 16
            r5 = 8
            r6 = 4
            r7 = 2
            r8 = 1
            if (r1 != 0) goto L_0x01ba
            int r9 = r13.readTag()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            if (r9 == 0) goto L_0x014e
            r10 = 10
            if (r9 == r10) goto L_0x0138
            r10 = 18
            if (r9 == r10) goto L_0x0122
            r10 = 26
            if (r9 == r10) goto L_0x010c
            r10 = 34
            if (r9 == r10) goto L_0x00f6
            r10 = 42
            if (r9 == r10) goto L_0x00e0
            r10 = 50
            if (r9 == r10) goto L_0x00c6
            r10 = 58
            r11 = 0
            if (r9 == r10) goto L_0x009d
            r10 = 66
            if (r9 == r10) goto L_0x0074
            r10 = 74
            if (r9 == r10) goto L_0x004c
            goto L_0x0015
        L_0x004c:
            int r9 = r12.f2445c     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9 = r9 & r6
            if (r9 != r6) goto L_0x0057
            com.miui.antispam.service.backup.v r9 = r12.l     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.v$a r11 = r9.toBuilder()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x0057:
            com.google.protobuf.Parser<com.miui.antispam.service.backup.v> r9 = com.miui.antispam.service.backup.v.f2492b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r9 = r13.readMessage(r9, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.v r9 = (com.miui.antispam.service.backup.v) r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.l = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            if (r11 == 0) goto L_0x006e
            com.miui.antispam.service.backup.v r9 = r12.l     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r11.a((com.miui.antispam.service.backup.v) r9)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.v r9 = r11.buildPartial()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.l = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x006e:
            int r9 = r12.f2445c     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9 = r9 | r6
            r12.f2445c = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x0015
        L_0x0074:
            int r9 = r12.f2445c     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9 = r9 & r7
            if (r9 != r7) goto L_0x007f
            com.miui.antispam.service.backup.y r9 = r12.k     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.y$a r11 = r9.toBuilder()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x007f:
            com.google.protobuf.Parser<com.miui.antispam.service.backup.y> r9 = com.miui.antispam.service.backup.y.f2500b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r9 = r13.readMessage(r9, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.y r9 = (com.miui.antispam.service.backup.y) r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.k = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            if (r11 == 0) goto L_0x0096
            com.miui.antispam.service.backup.y r9 = r12.k     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r11.a((com.miui.antispam.service.backup.y) r9)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.y r9 = r11.buildPartial()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.k = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x0096:
            int r9 = r12.f2445c     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9 = r9 | r7
            r12.f2445c = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x0015
        L_0x009d:
            int r9 = r12.f2445c     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9 = r9 & r8
            if (r9 != r8) goto L_0x00a8
            com.miui.antispam.service.backup.g r9 = r12.j     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.g$a r11 = r9.toBuilder()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x00a8:
            com.google.protobuf.Parser<com.miui.antispam.service.backup.g> r9 = com.miui.antispam.service.backup.C0203g.f2452b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r9 = r13.readMessage(r9, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.g r9 = (com.miui.antispam.service.backup.C0203g) r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.j = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            if (r11 == 0) goto L_0x00bf
            com.miui.antispam.service.backup.g r9 = r12.j     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r11.a((com.miui.antispam.service.backup.C0203g) r9)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.miui.antispam.service.backup.g r9 = r11.buildPartial()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.j = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x00bf:
            int r9 = r12.f2445c     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9 = r9 | r8
            r12.f2445c = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x0015
        L_0x00c6:
            r9 = r2 & 32
            if (r9 == r3) goto L_0x00d3
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9.<init>()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.i = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r2 = r2 | 32
        L_0x00d3:
            java.util.List<com.miui.antispam.service.backup.p> r9 = r12.i     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.Parser<com.miui.antispam.service.backup.p> r10 = com.miui.antispam.service.backup.p.f2476b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r10 = r13.readMessage(r10, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
        L_0x00db:
            r9.add(r10)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x0015
        L_0x00e0:
            r9 = r2 & 16
            if (r9 == r4) goto L_0x00ed
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9.<init>()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.h = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r2 = r2 | 16
        L_0x00ed:
            java.util.List<com.miui.antispam.service.backup.B> r9 = r12.h     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.Parser<com.miui.antispam.service.backup.B> r10 = com.miui.antispam.service.backup.B.f2416b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r10 = r13.readMessage(r10, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x00db
        L_0x00f6:
            r9 = r2 & 8
            if (r9 == r5) goto L_0x0103
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9.<init>()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.g = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r2 = r2 | 8
        L_0x0103:
            java.util.List<com.miui.antispam.service.backup.s> r9 = r12.g     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.Parser<com.miui.antispam.service.backup.s> r10 = com.miui.antispam.service.backup.s.f2484b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r10 = r13.readMessage(r10, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x00db
        L_0x010c:
            r9 = r2 & 4
            if (r9 == r6) goto L_0x0119
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9.<init>()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.f = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r2 = r2 | 4
        L_0x0119:
            java.util.List<com.miui.antispam.service.backup.m> r9 = r12.f     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.Parser<com.miui.antispam.service.backup.m> r10 = com.miui.antispam.service.backup.m.f2468b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r10 = r13.readMessage(r10, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x00db
        L_0x0122:
            r9 = r2 & 2
            if (r9 == r7) goto L_0x012f
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9.<init>()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.e = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r2 = r2 | 2
        L_0x012f:
            java.util.List<com.miui.antispam.service.backup.E> r9 = r12.e     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.Parser<com.miui.antispam.service.backup.E> r10 = com.miui.antispam.service.backup.E.f2424b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r10 = r13.readMessage(r10, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x00db
        L_0x0138:
            r9 = r2 & 1
            if (r9 == r8) goto L_0x0145
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r9.<init>()     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r12.f2446d = r9     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            r2 = r2 | 1
        L_0x0145:
            java.util.List<com.miui.antispam.service.backup.j> r9 = r12.f2446d     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.Parser<com.miui.antispam.service.backup.j> r10 = com.miui.antispam.service.backup.C0206j.f2460b     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            com.google.protobuf.MessageLite r10 = r13.readMessage(r10, r14)     // Catch:{ InvalidProtocolBufferException -> 0x0162, IOException -> 0x0153 }
            goto L_0x00db
        L_0x014e:
            r1 = r8
            goto L_0x0015
        L_0x0151:
            r13 = move-exception
            goto L_0x0168
        L_0x0153:
            r13 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r14 = new com.google.protobuf.InvalidProtocolBufferException     // Catch:{ all -> 0x0151 }
            java.lang.String r13 = r13.getMessage()     // Catch:{ all -> 0x0151 }
            r14.<init>(r13)     // Catch:{ all -> 0x0151 }
            com.google.protobuf.InvalidProtocolBufferException r13 = r14.setUnfinishedMessage(r12)     // Catch:{ all -> 0x0151 }
            throw r13     // Catch:{ all -> 0x0151 }
        L_0x0162:
            r13 = move-exception
            com.google.protobuf.InvalidProtocolBufferException r13 = r13.setUnfinishedMessage(r12)     // Catch:{ all -> 0x0151 }
            throw r13     // Catch:{ all -> 0x0151 }
        L_0x0168:
            r14 = r2 & 1
            if (r14 != r8) goto L_0x0174
            java.util.List<com.miui.antispam.service.backup.j> r14 = r12.f2446d
            java.util.List r14 = java.util.Collections.unmodifiableList(r14)
            r12.f2446d = r14
        L_0x0174:
            r14 = r2 & 2
            if (r14 != r7) goto L_0x0180
            java.util.List<com.miui.antispam.service.backup.E> r14 = r12.e
            java.util.List r14 = java.util.Collections.unmodifiableList(r14)
            r12.e = r14
        L_0x0180:
            r14 = r2 & 4
            if (r14 != r6) goto L_0x018c
            java.util.List<com.miui.antispam.service.backup.m> r14 = r12.f
            java.util.List r14 = java.util.Collections.unmodifiableList(r14)
            r12.f = r14
        L_0x018c:
            r14 = r2 & 8
            if (r14 != r5) goto L_0x0198
            java.util.List<com.miui.antispam.service.backup.s> r14 = r12.g
            java.util.List r14 = java.util.Collections.unmodifiableList(r14)
            r12.g = r14
        L_0x0198:
            r14 = r2 & 16
            if (r14 != r4) goto L_0x01a4
            java.util.List<com.miui.antispam.service.backup.B> r14 = r12.h
            java.util.List r14 = java.util.Collections.unmodifiableList(r14)
            r12.h = r14
        L_0x01a4:
            r14 = r2 & 32
            if (r14 != r3) goto L_0x01b0
            java.util.List<com.miui.antispam.service.backup.p> r14 = r12.i
            java.util.List r14 = java.util.Collections.unmodifiableList(r14)
            r12.i = r14
        L_0x01b0:
            r0.flush()     // Catch:{ IOException -> 0x01b6, all -> 0x01b4 }
            goto L_0x01b6
        L_0x01b4:
            r13 = move-exception
            throw r13
        L_0x01b6:
            r12.makeExtensionsImmutable()
            throw r13
        L_0x01ba:
            r13 = r2 & 1
            if (r13 != r8) goto L_0x01c6
            java.util.List<com.miui.antispam.service.backup.j> r13 = r12.f2446d
            java.util.List r13 = java.util.Collections.unmodifiableList(r13)
            r12.f2446d = r13
        L_0x01c6:
            r13 = r2 & 2
            if (r13 != r7) goto L_0x01d2
            java.util.List<com.miui.antispam.service.backup.E> r13 = r12.e
            java.util.List r13 = java.util.Collections.unmodifiableList(r13)
            r12.e = r13
        L_0x01d2:
            r13 = r2 & 4
            if (r13 != r6) goto L_0x01de
            java.util.List<com.miui.antispam.service.backup.m> r13 = r12.f
            java.util.List r13 = java.util.Collections.unmodifiableList(r13)
            r12.f = r13
        L_0x01de:
            r13 = r2 & 8
            if (r13 != r5) goto L_0x01ea
            java.util.List<com.miui.antispam.service.backup.s> r13 = r12.g
            java.util.List r13 = java.util.Collections.unmodifiableList(r13)
            r12.g = r13
        L_0x01ea:
            r13 = r2 & 16
            if (r13 != r4) goto L_0x01f6
            java.util.List<com.miui.antispam.service.backup.B> r13 = r12.h
            java.util.List r13 = java.util.Collections.unmodifiableList(r13)
            r12.h = r13
        L_0x01f6:
            r13 = r2 & 32
            if (r13 != r3) goto L_0x0202
            java.util.List<com.miui.antispam.service.backup.p> r13 = r12.i
            java.util.List r13 = java.util.Collections.unmodifiableList(r13)
            r12.i = r13
        L_0x0202:
            r0.flush()     // Catch:{ IOException -> 0x0208, all -> 0x0206 }
            goto L_0x0208
        L_0x0206:
            r13 = move-exception
            throw r13
        L_0x0208:
            r12.makeExtensionsImmutable()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.service.backup.C0200d.<init>(com.google.protobuf.CodedInputStream, com.google.protobuf.ExtensionRegistryLite):void");
    }

    private C0200d(GeneratedMessageLite.Builder builder) {
        super(builder);
        this.m = -1;
        this.n = -1;
    }

    private C0200d(boolean z) {
        this.m = -1;
        this.n = -1;
    }

    public static C0200d c() {
        return f2443a;
    }

    public static a g(C0200d dVar) {
        a n2 = n();
        n2.a(dVar);
        return n2;
    }

    public static a n() {
        return a.b();
    }

    private void o() {
        this.f2446d = Collections.emptyList();
        this.e = Collections.emptyList();
        this.f = Collections.emptyList();
        this.g = Collections.emptyList();
        this.h = Collections.emptyList();
        this.i = Collections.emptyList();
        this.j = C0203g.a();
        this.k = y.a();
        this.l = v.b();
    }

    public C0203g a() {
        return this.j;
    }

    public List<C0206j> b() {
        return this.f2446d;
    }

    public List<m> d() {
        return this.f;
    }

    public List<p> e() {
        return this.i;
    }

    public List<s> f() {
        return this.g;
    }

    public v g() {
        return this.l;
    }

    public C0200d getDefaultInstanceForType() {
        return f2443a;
    }

    public Parser<C0200d> getParserForType() {
        return f2444b;
    }

    public int getSerializedSize() {
        int i2 = this.n;
        if (i2 != -1) {
            return i2;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < this.f2446d.size(); i4++) {
            i3 += CodedOutputStream.computeMessageSize(1, this.f2446d.get(i4));
        }
        for (int i5 = 0; i5 < this.e.size(); i5++) {
            i3 += CodedOutputStream.computeMessageSize(2, this.e.get(i5));
        }
        for (int i6 = 0; i6 < this.f.size(); i6++) {
            i3 += CodedOutputStream.computeMessageSize(3, this.f.get(i6));
        }
        for (int i7 = 0; i7 < this.g.size(); i7++) {
            i3 += CodedOutputStream.computeMessageSize(4, this.g.get(i7));
        }
        for (int i8 = 0; i8 < this.h.size(); i8++) {
            i3 += CodedOutputStream.computeMessageSize(5, this.h.get(i8));
        }
        for (int i9 = 0; i9 < this.i.size(); i9++) {
            i3 += CodedOutputStream.computeMessageSize(6, this.i.get(i9));
        }
        if ((this.f2445c & 1) == 1) {
            i3 += CodedOutputStream.computeMessageSize(7, this.j);
        }
        if ((this.f2445c & 2) == 2) {
            i3 += CodedOutputStream.computeMessageSize(8, this.k);
        }
        if ((this.f2445c & 4) == 4) {
            i3 += CodedOutputStream.computeMessageSize(9, this.l);
        }
        this.n = i3;
        return i3;
    }

    public y h() {
        return this.k;
    }

    public List<B> i() {
        return this.h;
    }

    public final boolean isInitialized() {
        byte b2 = this.m;
        if (b2 == 1) {
            return true;
        }
        if (b2 == 0) {
            return false;
        }
        this.m = 1;
        return true;
    }

    public List<E> j() {
        return this.e;
    }

    public boolean k() {
        return (this.f2445c & 1) == 1;
    }

    public boolean l() {
        return (this.f2445c & 4) == 4;
    }

    public boolean m() {
        return (this.f2445c & 2) == 2;
    }

    public a newBuilderForType() {
        return n();
    }

    public a toBuilder() {
        return g(this);
    }

    /* access modifiers changed from: protected */
    public Object writeReplace() {
        return C0200d.super.writeReplace();
    }

    public void writeTo(CodedOutputStream codedOutputStream) {
        getSerializedSize();
        for (int i2 = 0; i2 < this.f2446d.size(); i2++) {
            codedOutputStream.writeMessage(1, this.f2446d.get(i2));
        }
        for (int i3 = 0; i3 < this.e.size(); i3++) {
            codedOutputStream.writeMessage(2, this.e.get(i3));
        }
        for (int i4 = 0; i4 < this.f.size(); i4++) {
            codedOutputStream.writeMessage(3, this.f.get(i4));
        }
        for (int i5 = 0; i5 < this.g.size(); i5++) {
            codedOutputStream.writeMessage(4, this.g.get(i5));
        }
        for (int i6 = 0; i6 < this.h.size(); i6++) {
            codedOutputStream.writeMessage(5, this.h.get(i6));
        }
        for (int i7 = 0; i7 < this.i.size(); i7++) {
            codedOutputStream.writeMessage(6, this.i.get(i7));
        }
        if ((this.f2445c & 1) == 1) {
            codedOutputStream.writeMessage(7, this.j);
        }
        if ((this.f2445c & 2) == 2) {
            codedOutputStream.writeMessage(8, this.k);
        }
        if ((this.f2445c & 4) == 4) {
            codedOutputStream.writeMessage(9, this.l);
        }
    }
}
