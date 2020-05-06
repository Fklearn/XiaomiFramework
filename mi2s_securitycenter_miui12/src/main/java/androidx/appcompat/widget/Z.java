package androidx.appcompat.widget;

class Z {

    /* renamed from: a  reason: collision with root package name */
    private int f572a = 0;

    /* renamed from: b  reason: collision with root package name */
    private int f573b = 0;

    /* renamed from: c  reason: collision with root package name */
    private int f574c = Integer.MIN_VALUE;

    /* renamed from: d  reason: collision with root package name */
    private int f575d = Integer.MIN_VALUE;
    private int e = 0;
    private int f = 0;
    private boolean g = false;
    private boolean h = false;

    Z() {
    }

    public int a() {
        return this.g ? this.f572a : this.f573b;
    }

    public void a(int i, int i2) {
        this.h = false;
        if (i != Integer.MIN_VALUE) {
            this.e = i;
            this.f572a = i;
        }
        if (i2 != Integer.MIN_VALUE) {
            this.f = i2;
            this.f573b = i2;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
        if (r2 != Integer.MIN_VALUE) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0028, code lost:
        if (r2 != Integer.MIN_VALUE) goto L_0x0031;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(boolean r2) {
        /*
            r1 = this;
            boolean r0 = r1.g
            if (r2 != r0) goto L_0x0005
            return
        L_0x0005:
            r1.g = r2
            boolean r0 = r1.h
            if (r0 == 0) goto L_0x002b
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r2 == 0) goto L_0x001d
            int r2 = r1.f575d
            if (r2 == r0) goto L_0x0014
            goto L_0x0016
        L_0x0014:
            int r2 = r1.e
        L_0x0016:
            r1.f572a = r2
            int r2 = r1.f574c
            if (r2 == r0) goto L_0x002f
            goto L_0x0031
        L_0x001d:
            int r2 = r1.f574c
            if (r2 == r0) goto L_0x0022
            goto L_0x0024
        L_0x0022:
            int r2 = r1.e
        L_0x0024:
            r1.f572a = r2
            int r2 = r1.f575d
            if (r2 == r0) goto L_0x002f
            goto L_0x0031
        L_0x002b:
            int r2 = r1.e
            r1.f572a = r2
        L_0x002f:
            int r2 = r1.f
        L_0x0031:
            r1.f573b = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.Z.a(boolean):void");
    }

    public int b() {
        return this.f572a;
    }

    public void b(int i, int i2) {
        this.f574c = i;
        this.f575d = i2;
        this.h = true;
        if (this.g) {
            if (i2 != Integer.MIN_VALUE) {
                this.f572a = i2;
            }
            if (i != Integer.MIN_VALUE) {
                this.f573b = i;
                return;
            }
            return;
        }
        if (i != Integer.MIN_VALUE) {
            this.f572a = i;
        }
        if (i2 != Integer.MIN_VALUE) {
            this.f573b = i2;
        }
    }

    public int c() {
        return this.f573b;
    }

    public int d() {
        return this.g ? this.f573b : this.f572a;
    }
}
