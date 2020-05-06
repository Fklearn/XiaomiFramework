package androidx.appcompat.widget;

import a.a.j;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.menu.v;
import androidx.core.view.ViewCompat;
import androidx.core.widget.i;
import java.lang.reflect.Method;

public class U implements v {

    /* renamed from: a  reason: collision with root package name */
    private static Method f554a;

    /* renamed from: b  reason: collision with root package name */
    private static Method f555b;

    /* renamed from: c  reason: collision with root package name */
    private static Method f556c;
    private final d A;
    private final c B;
    private final a C;
    private Runnable D;
    final Handler E;
    private final Rect F;
    private Rect G;
    private boolean H;
    PopupWindow I;

    /* renamed from: d  reason: collision with root package name */
    private Context f557d;
    private ListAdapter e;
    O f;
    private int g;
    private int h;
    private int i;
    private int j;
    private int k;
    private boolean l;
    private boolean m;
    private boolean n;
    private int o;
    private boolean p;
    private boolean q;
    int r;
    private View s;
    private int t;
    private DataSetObserver u;
    private View v;
    private Drawable w;
    private AdapterView.OnItemClickListener x;
    private AdapterView.OnItemSelectedListener y;
    final e z;

    private class a implements Runnable {
        a() {
        }

        public void run() {
            U.this.a();
        }
    }

    private class b extends DataSetObserver {
        b() {
        }

        public void onChanged() {
            if (U.this.isShowing()) {
                U.this.b();
            }
        }

        public void onInvalidated() {
            U.this.dismiss();
        }
    }

    private class c implements AbsListView.OnScrollListener {
        c() {
        }

        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }

        public void onScrollStateChanged(AbsListView absListView, int i) {
            if (i == 1 && !U.this.f() && U.this.I.getContentView() != null) {
                U u = U.this;
                u.E.removeCallbacks(u.z);
                U.this.z.run();
            }
        }
    }

    private class d implements View.OnTouchListener {
        d() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            PopupWindow popupWindow;
            int action = motionEvent.getAction();
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (action == 0 && (popupWindow = U.this.I) != null && popupWindow.isShowing() && x >= 0 && x < U.this.I.getWidth() && y >= 0 && y < U.this.I.getHeight()) {
                U u = U.this;
                u.E.postDelayed(u.z, 250);
                return false;
            } else if (action != 1) {
                return false;
            } else {
                U u2 = U.this;
                u2.E.removeCallbacks(u2.z);
                return false;
            }
        }
    }

    private class e implements Runnable {
        e() {
        }

        public void run() {
            O o = U.this.f;
            if (o != null && ViewCompat.r(o) && U.this.f.getCount() > U.this.f.getChildCount()) {
                int childCount = U.this.f.getChildCount();
                U u = U.this;
                if (childCount <= u.r) {
                    u.I.setInputMethodMode(2);
                    U.this.b();
                }
            }
        }
    }

    static {
        if (Build.VERSION.SDK_INT <= 28) {
            Class<PopupWindow> cls = PopupWindow.class;
            try {
                f554a = cls.getDeclaredMethod("setClipToScreenEnabled", new Class[]{Boolean.TYPE});
            } catch (NoSuchMethodException unused) {
                Log.i("ListPopupWindow", "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
            }
            try {
                f556c = PopupWindow.class.getDeclaredMethod("setEpicenterBounds", new Class[]{Rect.class});
            } catch (NoSuchMethodException unused2) {
                Log.i("ListPopupWindow", "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well.");
            }
        }
        if (Build.VERSION.SDK_INT <= 23) {
            try {
                f555b = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", new Class[]{View.class, Integer.TYPE, Boolean.TYPE});
            } catch (NoSuchMethodException unused3) {
                Log.i("ListPopupWindow", "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
            }
        }
    }

    public U(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i2) {
        this(context, attributeSet, i2, 0);
    }

    public U(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int i2, @StyleRes int i3) {
        this.g = -2;
        this.h = -2;
        this.k = 1002;
        this.o = 0;
        this.p = false;
        this.q = false;
        this.r = Integer.MAX_VALUE;
        this.t = 0;
        this.z = new e();
        this.A = new d();
        this.B = new c();
        this.C = new a();
        this.F = new Rect();
        this.f557d = context;
        this.E = new Handler(context.getMainLooper());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, j.ListPopupWindow, i2, i3);
        this.i = obtainStyledAttributes.getDimensionPixelOffset(j.ListPopupWindow_android_dropDownHorizontalOffset, 0);
        this.j = obtainStyledAttributes.getDimensionPixelOffset(j.ListPopupWindow_android_dropDownVerticalOffset, 0);
        if (this.j != 0) {
            this.l = true;
        }
        obtainStyledAttributes.recycle();
        this.I = new C0117u(context, attributeSet, i2, i3);
        this.I.setInputMethodMode(1);
    }

    private int a(View view, int i2, boolean z2) {
        if (Build.VERSION.SDK_INT > 23) {
            return this.I.getMaxAvailableHeight(view, i2, z2);
        }
        Method method = f555b;
        if (method != null) {
            try {
                return ((Integer) method.invoke(this.I, new Object[]{view, Integer.valueOf(i2), Boolean.valueOf(z2)})).intValue();
            } catch (Exception unused) {
                Log.i("ListPopupWindow", "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
            }
        }
        return this.I.getMaxAvailableHeight(view, i2);
    }

    private void c(boolean z2) {
        if (Build.VERSION.SDK_INT <= 28) {
            Method method = f554a;
            if (method != null) {
                try {
                    method.invoke(this.I, new Object[]{Boolean.valueOf(z2)});
                } catch (Exception unused) {
                    Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
                }
            }
        } else {
            this.I.setIsClippedToScreen(z2);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: androidx.appcompat.widget.O} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: androidx.appcompat.widget.O} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: android.widget.LinearLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v30, resolved type: androidx.appcompat.widget.O} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0153  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int h() {
        /*
            r12 = this;
            androidx.appcompat.widget.O r0 = r12.f
            r1 = -2147483648(0xffffffff80000000, float:-0.0)
            r2 = -1
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x00c0
            android.content.Context r0 = r12.f557d
            androidx.appcompat.widget.S r5 = new androidx.appcompat.widget.S
            r5.<init>(r12)
            r12.D = r5
            boolean r5 = r12.H
            r5 = r5 ^ r3
            androidx.appcompat.widget.O r5 = r12.a(r0, r5)
            r12.f = r5
            android.graphics.drawable.Drawable r5 = r12.w
            if (r5 == 0) goto L_0x0024
            androidx.appcompat.widget.O r6 = r12.f
            r6.setSelector(r5)
        L_0x0024:
            androidx.appcompat.widget.O r5 = r12.f
            android.widget.ListAdapter r6 = r12.e
            r5.setAdapter(r6)
            androidx.appcompat.widget.O r5 = r12.f
            android.widget.AdapterView$OnItemClickListener r6 = r12.x
            r5.setOnItemClickListener(r6)
            androidx.appcompat.widget.O r5 = r12.f
            r5.setFocusable(r3)
            androidx.appcompat.widget.O r5 = r12.f
            r5.setFocusableInTouchMode(r3)
            androidx.appcompat.widget.O r5 = r12.f
            androidx.appcompat.widget.T r6 = new androidx.appcompat.widget.T
            r6.<init>(r12)
            r5.setOnItemSelectedListener(r6)
            androidx.appcompat.widget.O r5 = r12.f
            androidx.appcompat.widget.U$c r6 = r12.B
            r5.setOnScrollListener(r6)
            android.widget.AdapterView$OnItemSelectedListener r5 = r12.y
            if (r5 == 0) goto L_0x0056
            androidx.appcompat.widget.O r6 = r12.f
            r6.setOnItemSelectedListener(r5)
        L_0x0056:
            androidx.appcompat.widget.O r5 = r12.f
            android.view.View r6 = r12.s
            if (r6 == 0) goto L_0x00b9
            android.widget.LinearLayout r7 = new android.widget.LinearLayout
            r7.<init>(r0)
            r7.setOrientation(r3)
            android.widget.LinearLayout$LayoutParams r0 = new android.widget.LinearLayout$LayoutParams
            r8 = 1065353216(0x3f800000, float:1.0)
            r0.<init>(r2, r4, r8)
            int r8 = r12.t
            if (r8 == 0) goto L_0x0091
            if (r8 == r3) goto L_0x008a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "Invalid hint position "
            r0.append(r5)
            int r5 = r12.t
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r5 = "ListPopupWindow"
            android.util.Log.e(r5, r0)
            goto L_0x0097
        L_0x008a:
            r7.addView(r5, r0)
            r7.addView(r6)
            goto L_0x0097
        L_0x0091:
            r7.addView(r6)
            r7.addView(r5, r0)
        L_0x0097:
            int r0 = r12.h
            if (r0 < 0) goto L_0x009d
            r5 = r1
            goto L_0x009f
        L_0x009d:
            r0 = r4
            r5 = r0
        L_0x009f:
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r5)
            r6.measure(r0, r4)
            android.view.ViewGroup$LayoutParams r0 = r6.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r0 = (android.widget.LinearLayout.LayoutParams) r0
            int r5 = r6.getMeasuredHeight()
            int r6 = r0.topMargin
            int r5 = r5 + r6
            int r0 = r0.bottomMargin
            int r5 = r5 + r0
            r0 = r5
            r5 = r7
            goto L_0x00ba
        L_0x00b9:
            r0 = r4
        L_0x00ba:
            android.widget.PopupWindow r6 = r12.I
            r6.setContentView(r5)
            goto L_0x00de
        L_0x00c0:
            android.widget.PopupWindow r0 = r12.I
            android.view.View r0 = r0.getContentView()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            android.view.View r0 = r12.s
            if (r0 == 0) goto L_0x00dd
            android.view.ViewGroup$LayoutParams r5 = r0.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r5 = (android.widget.LinearLayout.LayoutParams) r5
            int r0 = r0.getMeasuredHeight()
            int r6 = r5.topMargin
            int r0 = r0 + r6
            int r5 = r5.bottomMargin
            int r0 = r0 + r5
            goto L_0x00de
        L_0x00dd:
            r0 = r4
        L_0x00de:
            android.widget.PopupWindow r5 = r12.I
            android.graphics.drawable.Drawable r5 = r5.getBackground()
            if (r5 == 0) goto L_0x00fa
            android.graphics.Rect r6 = r12.F
            r5.getPadding(r6)
            android.graphics.Rect r5 = r12.F
            int r6 = r5.top
            int r5 = r5.bottom
            int r5 = r5 + r6
            boolean r7 = r12.l
            if (r7 != 0) goto L_0x0100
            int r6 = -r6
            r12.j = r6
            goto L_0x0100
        L_0x00fa:
            android.graphics.Rect r5 = r12.F
            r5.setEmpty()
            r5 = r4
        L_0x0100:
            android.widget.PopupWindow r6 = r12.I
            int r6 = r6.getInputMethodMode()
            r7 = 2
            if (r6 != r7) goto L_0x010a
            goto L_0x010b
        L_0x010a:
            r3 = r4
        L_0x010b:
            android.view.View r4 = r12.d()
            int r6 = r12.j
            int r3 = r12.a(r4, r6, r3)
            boolean r4 = r12.p
            if (r4 != 0) goto L_0x0164
            int r4 = r12.g
            if (r4 != r2) goto L_0x011e
            goto L_0x0164
        L_0x011e:
            int r4 = r12.h
            r6 = -2
            if (r4 == r6) goto L_0x012d
            r1 = 1073741824(0x40000000, float:2.0)
            if (r4 == r2) goto L_0x012d
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r1)
        L_0x012b:
            r7 = r1
            goto L_0x0146
        L_0x012d:
            android.content.Context r2 = r12.f557d
            android.content.res.Resources r2 = r2.getResources()
            android.util.DisplayMetrics r2 = r2.getDisplayMetrics()
            int r2 = r2.widthPixels
            android.graphics.Rect r4 = r12.F
            int r6 = r4.left
            int r4 = r4.right
            int r6 = r6 + r4
            int r2 = r2 - r6
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r1)
            goto L_0x012b
        L_0x0146:
            androidx.appcompat.widget.O r6 = r12.f
            r8 = 0
            r9 = -1
            int r10 = r3 - r0
            r11 = -1
            int r1 = r6.a(r7, r8, r9, r10, r11)
            if (r1 <= 0) goto L_0x0162
            androidx.appcompat.widget.O r2 = r12.f
            int r2 = r2.getPaddingTop()
            androidx.appcompat.widget.O r3 = r12.f
            int r3 = r3.getPaddingBottom()
            int r2 = r2 + r3
            int r5 = r5 + r2
            int r0 = r0 + r5
        L_0x0162:
            int r1 = r1 + r0
            return r1
        L_0x0164:
            int r3 = r3 + r5
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.U.h():int");
    }

    private void i() {
        View view = this.s;
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(this.s);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public O a(Context context, boolean z2) {
        return new O(context, z2);
    }

    public void a() {
        O o2 = this.f;
        if (o2 != null) {
            o2.setListSelectionHidden(true);
            o2.requestLayout();
        }
    }

    public void a(@StyleRes int i2) {
        this.I.setAnimationStyle(i2);
    }

    public void a(@Nullable Rect rect) {
        this.G = rect != null ? new Rect(rect) : null;
    }

    public void a(@Nullable View view) {
        this.v = view;
    }

    public void a(@Nullable AdapterView.OnItemClickListener onItemClickListener) {
        this.x = onItemClickListener;
    }

    public void a(@Nullable PopupWindow.OnDismissListener onDismissListener) {
        this.I.setOnDismissListener(onDismissListener);
    }

    public void a(boolean z2) {
        this.H = z2;
        this.I.setFocusable(z2);
    }

    public void b() {
        int h2 = h();
        boolean f2 = f();
        i.a(this.I, this.k);
        boolean z2 = true;
        if (!this.I.isShowing()) {
            int i2 = this.h;
            if (i2 == -1) {
                i2 = -1;
            } else if (i2 == -2) {
                i2 = d().getWidth();
            }
            int i3 = this.g;
            if (i3 == -1) {
                h2 = -1;
            } else if (i3 != -2) {
                h2 = i3;
            }
            this.I.setWidth(i2);
            this.I.setHeight(h2);
            c(true);
            this.I.setOutsideTouchable(!this.q && !this.p);
            this.I.setTouchInterceptor(this.A);
            if (this.n) {
                i.a(this.I, this.m);
            }
            if (Build.VERSION.SDK_INT <= 28) {
                Method method = f556c;
                if (method != null) {
                    try {
                        method.invoke(this.I, new Object[]{this.G});
                    } catch (Exception e2) {
                        Log.e("ListPopupWindow", "Could not invoke setEpicenterBounds on PopupWindow", e2);
                    }
                }
            } else {
                this.I.setEpicenterBounds(this.G);
            }
            i.a(this.I, d(), this.i, this.j, this.o);
            this.f.setSelection(-1);
            if (!this.H || this.f.isInTouchMode()) {
                a();
            }
            if (!this.H) {
                this.E.post(this.C);
            }
        } else if (ViewCompat.r(d())) {
            int i4 = this.h;
            if (i4 == -1) {
                i4 = -1;
            } else if (i4 == -2) {
                i4 = d().getWidth();
            }
            int i5 = this.g;
            if (i5 == -1) {
                if (!f2) {
                    h2 = -1;
                }
                if (f2) {
                    this.I.setWidth(this.h == -1 ? -1 : 0);
                    this.I.setHeight(0);
                } else {
                    this.I.setWidth(this.h == -1 ? -1 : 0);
                    this.I.setHeight(-1);
                }
            } else if (i5 != -2) {
                h2 = i5;
            }
            PopupWindow popupWindow = this.I;
            if (this.q || this.p) {
                z2 = false;
            }
            popupWindow.setOutsideTouchable(z2);
            this.I.update(d(), this.i, this.j, i4 < 0 ? -1 : i4, h2 < 0 ? -1 : h2);
        }
    }

    public void b(int i2) {
        Drawable background = this.I.getBackground();
        if (background != null) {
            background.getPadding(this.F);
            Rect rect = this.F;
            this.h = rect.left + rect.right + i2;
            return;
        }
        g(i2);
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public void b(boolean z2) {
        this.n = true;
        this.m = z2;
    }

    @Nullable
    public ListView c() {
        return this.f;
    }

    public void c(int i2) {
        this.o = i2;
    }

    @Nullable
    public View d() {
        return this.v;
    }

    public void d(int i2) {
        this.I.setInputMethodMode(i2);
    }

    public void dismiss() {
        this.I.dismiss();
        i();
        this.I.setContentView((View) null);
        this.f = null;
        this.E.removeCallbacks(this.z);
    }

    public int e() {
        return this.h;
    }

    public void e(int i2) {
        this.t = i2;
    }

    public void f(int i2) {
        O o2 = this.f;
        if (isShowing() && o2 != null) {
            o2.setListSelectionHidden(false);
            o2.setSelection(i2);
            if (o2.getChoiceMode() != 0) {
                o2.setItemChecked(i2, true);
            }
        }
    }

    public boolean f() {
        return this.I.getInputMethodMode() == 2;
    }

    public void g(int i2) {
        this.h = i2;
    }

    public boolean g() {
        return this.H;
    }

    @Nullable
    public Drawable getBackground() {
        return this.I.getBackground();
    }

    public int getHorizontalOffset() {
        return this.i;
    }

    public int getVerticalOffset() {
        if (!this.l) {
            return 0;
        }
        return this.j;
    }

    public boolean isShowing() {
        return this.I.isShowing();
    }

    public void setAdapter(@Nullable ListAdapter listAdapter) {
        DataSetObserver dataSetObserver = this.u;
        if (dataSetObserver == null) {
            this.u = new b();
        } else {
            ListAdapter listAdapter2 = this.e;
            if (listAdapter2 != null) {
                listAdapter2.unregisterDataSetObserver(dataSetObserver);
            }
        }
        this.e = listAdapter;
        if (listAdapter != null) {
            listAdapter.registerDataSetObserver(this.u);
        }
        O o2 = this.f;
        if (o2 != null) {
            o2.setAdapter(this.e);
        }
    }

    public void setBackgroundDrawable(@Nullable Drawable drawable) {
        this.I.setBackgroundDrawable(drawable);
    }

    public void setHorizontalOffset(int i2) {
        this.i = i2;
    }

    public void setVerticalOffset(int i2) {
        this.j = i2;
        this.l = true;
    }
}
