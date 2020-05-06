package com.miui.common.stickydecoration;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.common.stickydecoration.a.c;
import com.miui.common.stickydecoration.b.b;
import com.miui.common.stickydecoration.d;
import java.util.ArrayList;

public class f extends c {
    private Paint m;
    private c<Bitmap> n;
    private c<View> o;
    private com.miui.common.stickydecoration.b.c p;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        f f3852a;

        private a(com.miui.common.stickydecoration.b.c cVar) {
            this.f3852a = new f(cVar);
        }

        public static a a(com.miui.common.stickydecoration.b.c cVar) {
            return new a(cVar);
        }

        public a a(int i) {
            this.f3852a.f3842b = i;
            return this;
        }

        public a a(b bVar) {
            this.f3852a.a(bVar);
            return this;
        }

        public f a() {
            return this.f3852a;
        }
    }

    private f(com.miui.common.stickydecoration.b.c cVar) {
        this.n = new c<>();
        this.o = new c<>();
        this.p = cVar;
        this.m = new Paint();
        this.m.setColor(0);
    }

    private void a(Canvas canvas, int i, int i2, int i3, int i4) {
        View view;
        Bitmap bitmap;
        float f = (float) i2;
        canvas.drawRect(f, (float) (i4 - this.f3842b), (float) i3, (float) i4, this.m);
        int a2 = a(i);
        if (this.o.a(a2) == null) {
            view = f(a2);
            if (view != null) {
                a(view, i2, i3);
                this.o.a(a2, view);
            } else {
                return;
            }
        } else {
            view = this.o.a(a2);
        }
        if (this.n.a(a2) != null) {
            bitmap = this.n.a(a2);
        } else {
            bitmap = Bitmap.createBitmap(view.getDrawingCache());
            this.n.a(a2, bitmap);
        }
        canvas.drawBitmap(bitmap, f, (float) (i4 - this.f3842b), (Paint) null);
        if (this.h != null) {
            a(view, i2, i4, i);
        }
    }

    private void a(View view, int i, int i2) {
        view.setDrawingCacheEnabled(true);
        view.setLayoutParams(new ViewGroup.LayoutParams(i2, this.f3842b));
        view.measure(View.MeasureSpec.makeMeasureSpec(i2, 1073741824), View.MeasureSpec.makeMeasureSpec(this.f3842b, 1073741824));
        view.layout(i, 0 - this.f3842b, i2, 0);
    }

    private void a(View view, int i, int i2, int i3) {
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
        com.miui.common.stickydecoration.b.c cVar = this.p;
        if (cVar != null) {
            return cVar.getGroupView(i);
        }
        return null;
    }

    public String b(int i) {
        com.miui.common.stickydecoration.b.c cVar = this.p;
        if (cVar != null) {
            return cVar.getGroupName(i);
        }
        return null;
    }

    public void b(Canvas canvas, RecyclerView recyclerView, RecyclerView.r rVar) {
        super.b(canvas, recyclerView, rVar);
        int a2 = rVar.a();
        int childCount = recyclerView.getChildCount();
        int paddingLeft = recyclerView.getPaddingLeft();
        int width = recyclerView.getWidth() - recyclerView.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerView.getChildAt(i);
            int f = recyclerView.f(childAt);
            int c2 = c(f);
            if (d(c2) || a(c2, i)) {
                int bottom = childAt.getBottom();
                int max = Math.max(this.f3842b, childAt.getTop() + recyclerView.getPaddingTop());
                a(canvas, c2, paddingLeft, width, (f + 1 >= a2 || !a(recyclerView, c2) || bottom >= max) ? max : bottom);
            } else {
                a(canvas, recyclerView, childAt, c2, paddingLeft, width);
            }
        }
    }
}
