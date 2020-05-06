package com.miui.common.stickydecoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.common.stickydecoration.b.b;
import java.util.HashMap;

public abstract class c extends RecyclerView.f {
    @ColorInt

    /* renamed from: a  reason: collision with root package name */
    int f3841a = Color.parseColor("#00000000");

    /* renamed from: b  reason: collision with root package name */
    protected int f3842b = 80;
    @ColorInt

    /* renamed from: c  reason: collision with root package name */
    int f3843c = Color.parseColor("#ffffffff");

    /* renamed from: d  reason: collision with root package name */
    int f3844d = 0;
    int e;
    Paint f = new Paint();
    private SparseIntArray g = new SparseIntArray(100);
    protected b h;
    private boolean i;
    protected HashMap<Integer, d> j = new HashMap<>();
    /* access modifiers changed from: private */
    public GestureDetector k;
    private GestureDetector.OnGestureListener l = new b(this);

    public c() {
        this.f.setColor(this.f3843c);
    }

    private void c(int i2, int i3) {
        b bVar = this.h;
        if (bVar != null) {
            bVar.a(i2, i3);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0085, code lost:
        if (r2 == false) goto L_0x0087;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean c(android.view.MotionEvent r9) {
        /*
            r8 = this;
            java.util.HashMap<java.lang.Integer, com.miui.common.stickydecoration.d> r0 = r8.j
            java.util.Set r0 = r0.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x000a:
            boolean r1 = r0.hasNext()
            r2 = 0
            if (r1 == 0) goto L_0x0097
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.util.HashMap<java.lang.Integer, com.miui.common.stickydecoration.d> r3 = r8.j
            java.lang.Object r4 = r1.getKey()
            java.lang.Object r3 = r3.get(r4)
            com.miui.common.stickydecoration.d r3 = (com.miui.common.stickydecoration.d) r3
            float r4 = r9.getY()
            float r5 = r9.getX()
            int r6 = r3.f3845a
            int r7 = r8.f3842b
            int r7 = r6 - r7
            float r7 = (float) r7
            int r7 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r7 > 0) goto L_0x000a
            float r6 = (float) r6
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 > 0) goto L_0x000a
            java.util.List<com.miui.common.stickydecoration.d$a> r9 = r3.f3847c
            r0 = 1
            if (r9 == 0) goto L_0x0087
            int r9 = r9.size()
            if (r9 != 0) goto L_0x0047
            goto L_0x0087
        L_0x0047:
            java.util.List<com.miui.common.stickydecoration.d$a> r9 = r3.f3847c
            java.util.Iterator r9 = r9.iterator()
        L_0x004d:
            boolean r6 = r9.hasNext()
            if (r6 == 0) goto L_0x0085
            java.lang.Object r6 = r9.next()
            com.miui.common.stickydecoration.d$a r6 = (com.miui.common.stickydecoration.d.a) r6
            int r7 = r6.f3851d
            float r7 = (float) r7
            int r7 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r7 > 0) goto L_0x004d
            int r7 = r6.e
            float r7 = (float) r7
            int r7 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r7 > 0) goto L_0x004d
            int r7 = r6.f3849b
            float r7 = (float) r7
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 > 0) goto L_0x004d
            int r7 = r6.f3850c
            float r7 = (float) r7
            int r7 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r7 < 0) goto L_0x004d
            java.lang.Object r9 = r1.getKey()
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            int r2 = r6.f3848a
            r8.c(r9, r2)
            r2 = r0
        L_0x0085:
            if (r2 != 0) goto L_0x0096
        L_0x0087:
            java.lang.Object r9 = r1.getKey()
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            int r1 = r3.f3846b
            r8.c(r9, r1)
        L_0x0096:
            return r0
        L_0x0097:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.stickydecoration.c.c(android.view.MotionEvent):boolean");
    }

    private int f(int i2) {
        if (i2 <= 0) {
            return 0;
        }
        return d(i2) ? i2 : f(i2 - 1);
    }

    /* access modifiers changed from: protected */
    public int a(int i2) {
        return f(i2);
    }

    /* access modifiers changed from: protected */
    public void a(Canvas canvas, RecyclerView recyclerView, View view, int i2, int i3, int i4) {
        float f2;
        if (this.f3844d != 0 && !e(i2)) {
            RecyclerView.g layoutManager = recyclerView.getLayoutManager();
            if (!(layoutManager instanceof GridLayoutManager)) {
                f2 = (float) view.getTop();
                if (f2 < ((float) this.f3842b)) {
                    return;
                }
            } else if (!b(i2, ((GridLayoutManager) layoutManager).M())) {
                f2 = (float) (view.getTop() + recyclerView.getPaddingTop());
                if (f2 < ((float) this.f3842b)) {
                    return;
                }
            } else {
                return;
            }
            canvas.drawRect((float) i3, f2 - ((float) this.f3844d), (float) i4, f2, this.f);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0035, code lost:
        r2 = r0.f3844d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0023, code lost:
        if (b(r2, r3) != false) goto L_0x0032;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0030, code lost:
        if (d(r2) != false) goto L_0x0032;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(@android.support.annotation.NonNull android.graphics.Rect r1, @android.support.annotation.NonNull android.view.View r2, @android.support.annotation.NonNull androidx.recyclerview.widget.RecyclerView r3, @android.support.annotation.NonNull androidx.recyclerview.widget.RecyclerView.r r4) {
        /*
            r0 = this;
            super.a(r1, r2, r3, r4)
            int r2 = r3.f((android.view.View) r2)
            int r2 = r0.c((int) r2)
            androidx.recyclerview.widget.RecyclerView$g r3 = r3.getLayoutManager()
            boolean r4 = r3 instanceof androidx.recyclerview.widget.GridLayoutManager
            if (r4 == 0) goto L_0x0026
            androidx.recyclerview.widget.GridLayoutManager r3 = (androidx.recyclerview.widget.GridLayoutManager) r3
            int r3 = r3.M()
            boolean r4 = r0.e(r2)
            if (r4 != 0) goto L_0x0039
            boolean r2 = r0.b(r2, r3)
            if (r2 == 0) goto L_0x0035
            goto L_0x0032
        L_0x0026:
            boolean r3 = r0.e(r2)
            if (r3 != 0) goto L_0x0039
            boolean r2 = r0.d(r2)
            if (r2 == 0) goto L_0x0035
        L_0x0032:
            int r2 = r0.f3842b
            goto L_0x0037
        L_0x0035:
            int r2 = r0.f3844d
        L_0x0037:
            r1.top = r2
        L_0x0039:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.common.stickydecoration.c.a(android.graphics.Rect, android.view.View, androidx.recyclerview.widget.RecyclerView, androidx.recyclerview.widget.RecyclerView$r):void");
    }

    public void a(MotionEvent motionEvent) {
        boolean z = false;
        if (motionEvent == null) {
            this.i = false;
            return;
        }
        if (motionEvent.getY() > 0.0f && motionEvent.getY() < ((float) this.f3842b)) {
            z = true;
        }
        this.i = z;
    }

    /* access modifiers changed from: protected */
    public void a(b bVar) {
        this.h = bVar;
    }

    /* access modifiers changed from: protected */
    public boolean a(int i2, int i3) {
        return i2 >= 0 && i3 == 0;
    }

    /* access modifiers changed from: protected */
    public boolean a(RecyclerView recyclerView, int i2) {
        int i3;
        String str;
        if (i2 < 0) {
            return true;
        }
        String b2 = b(i2);
        RecyclerView.g layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            int M = ((GridLayoutManager) layoutManager).M();
            i3 = M - ((i2 - a(i2)) % M);
        } else {
            i3 = 1;
        }
        try {
            str = b(i2 + i3);
        } catch (Exception unused) {
            str = b2;
        }
        if (str == null) {
            return true;
        }
        return !TextUtils.equals(b2, str);
    }

    /* access modifiers changed from: protected */
    public abstract String b(int i2);

    public void b(Canvas canvas, RecyclerView recyclerView, RecyclerView.r rVar) {
        super.b(canvas, recyclerView, rVar);
        if (this.k == null) {
            this.k = new GestureDetector(recyclerView.getContext(), this.l);
            recyclerView.setOnTouchListener(new a(this));
        }
        this.j.clear();
    }

    /* access modifiers changed from: protected */
    public boolean b(int i2, int i3) {
        if (i2 < 0) {
            return false;
        }
        return i2 == 0 || i2 - a(i2) < i3;
    }

    public boolean b(MotionEvent motionEvent) {
        if (this.i) {
            float y = motionEvent.getY();
            if (y > 0.0f && y < ((float) this.f3842b)) {
                return c(motionEvent);
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public int c(int i2) {
        return i2 - this.e;
    }

    /* access modifiers changed from: protected */
    public boolean d(int i2) {
        if (i2 < 0) {
            return false;
        }
        if (i2 == 0) {
            return true;
        }
        String b2 = i2 <= 0 ? null : b(i2 - 1);
        String b3 = b(i2);
        if (b3 == null) {
            return false;
        }
        return !TextUtils.equals(b2, b3);
    }

    /* access modifiers changed from: protected */
    public boolean e(int i2) {
        return i2 < 0;
    }
}
