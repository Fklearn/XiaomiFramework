package com.miui.permcenter.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.common.stickydecoration.c;
import com.miui.common.stickydecoration.d;
import java.util.ArrayList;

public class b extends c {
    private Paint m;
    /* access modifiers changed from: private */
    public int n;
    /* access modifiers changed from: private */
    public View o;
    private int p;
    private com.miui.common.stickydecoration.a.c<Bitmap> q;
    private com.miui.common.stickydecoration.a.c<View> r;
    private com.miui.permcenter.b.a s;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        b f6607a;

        private a(com.miui.permcenter.b.a aVar) {
            this.f6607a = new b(aVar);
        }

        public static a a(com.miui.permcenter.b.a aVar) {
            return new a(aVar);
        }

        public a a(int i) {
            int unused = this.f6607a.n = i;
            return this;
        }

        public a a(View view) {
            View unused = this.f6607a.o = view;
            return this;
        }

        public a a(com.miui.common.stickydecoration.b.b bVar) {
            this.f6607a.a(bVar);
            return this;
        }

        public b a() {
            return this.f6607a;
        }

        public a b(int i) {
            int unused = this.f6607a.f3842b = i;
            return this;
        }
    }

    private b(com.miui.permcenter.b.a aVar) {
        this.n = 358;
        this.o = null;
        this.p = 0;
        this.q = new com.miui.common.stickydecoration.a.c<>();
        this.r = new com.miui.common.stickydecoration.a.c<>();
        this.s = aVar;
        this.m = new Paint();
        this.m.setColor(0);
    }

    private void a(Canvas canvas, int i, int i2, int i3) {
        View a2;
        Bitmap bitmap;
        com.miui.permcenter.b.a aVar = this.s;
        if (aVar != null && (a2 = aVar.a()) != null) {
            a(a2, i, i2, i3);
            if (this.q.a(-1) != null) {
                bitmap = this.q.a(-1);
            } else {
                bitmap = Bitmap.createBitmap(a2.getDrawingCache());
                this.q.a(-1, bitmap);
            }
            canvas.drawBitmap(bitmap, (float) i, (float) (i3 - this.f3842b), (Paint) null);
            if (this.h != null) {
                b(a2, i, i3, -1);
            }
        }
    }

    private void a(Canvas canvas, int i, int i2, int i3, int i4) {
        View view;
        Bitmap bitmap;
        int a2 = a(i);
        if (this.r.a(a2) == null) {
            view = f(a2);
            if (view != null) {
                a(view, i2, i3, i4);
                this.r.a(a2, view);
            } else {
                return;
            }
        } else {
            view = this.r.a(a2);
        }
        if (this.q.a(a2) != null) {
            bitmap = this.q.a(a2);
        } else {
            Bitmap createBitmap = Bitmap.createBitmap(view.getDrawingCache());
            this.q.a(a2, createBitmap);
            bitmap = createBitmap;
        }
        canvas.drawBitmap(bitmap, (float) i2, (float) (i4 - this.f3842b), (Paint) null);
    }

    private void a(View view, int i, int i2, int i3) {
        view.setDrawingCacheEnabled(true);
        int i4 = i2 - i;
        view.setLayoutParams(new ViewGroup.LayoutParams(i4, this.f3842b));
        view.measure(View.MeasureSpec.makeMeasureSpec(i4, 1073741824), View.MeasureSpec.makeMeasureSpec(this.f3842b, 1073741824));
        view.layout(i, i3 - this.f3842b, i2, i3);
    }

    private void b(View view, int i, int i2, int i3) {
        int i4 = i2 - this.f3842b;
        ArrayList arrayList = new ArrayList();
        for (View next : com.miui.common.stickydecoration.c.a.a(view)) {
            int top = next.getTop() + i4;
            int bottom = next.getBottom() + i4;
            arrayList.add(new d.a(next.getId(), next.getLeft() + i, next.getRight() + i, top, bottom));
        }
        d dVar = new d(i2, arrayList);
        dVar.f3846b = view.getId();
        this.j.put(Integer.valueOf(i3), dVar);
    }

    private View f(int i) {
        com.miui.permcenter.b.a aVar = this.s;
        if (aVar != null) {
            return aVar.getGroupView(i);
        }
        return null;
    }

    public String b(int i) {
        com.miui.permcenter.b.a aVar = this.s;
        if (aVar != null) {
            return aVar.getGroupName(i);
        }
        return null;
    }

    public void b(Canvas canvas, RecyclerView recyclerView, RecyclerView.r rVar) {
        super.b(canvas, recyclerView, rVar);
        int a2 = rVar.a();
        int childCount = recyclerView.getChildCount();
        int paddingLeft = recyclerView.getPaddingLeft();
        int width = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int i = this.n;
        int i2 = width - i > 0 ? width - i : width;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = recyclerView.getChildAt(i3);
            int f = recyclerView.f(childAt);
            int c2 = c(f);
            if (d(c2) || a(c2, i3)) {
                int bottom = childAt.getBottom();
                int max = Math.max(this.f3842b, childAt.getTop() + recyclerView.getPaddingTop());
                a(canvas, c2, paddingLeft, i2, (f + 1 >= a2 || !a(recyclerView, c2) || bottom >= max) ? max : bottom);
            } else {
                a(canvas, recyclerView, childAt, c2, paddingLeft, i2);
            }
        }
        a(canvas, i2, width, this.f3842b);
    }
}
