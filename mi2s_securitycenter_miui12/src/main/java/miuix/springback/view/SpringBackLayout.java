package miuix.springback.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.j;
import androidx.core.view.l;
import androidx.core.view.n;
import androidx.core.view.p;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.g;
import java.util.ArrayList;
import java.util.List;

public class SpringBackLayout extends ViewGroup implements n, j, d.c.b.a {
    private final int A;
    private final int B;
    private float C;
    private float D;
    private boolean E;
    private boolean F;
    private List<a> G;
    private b H;
    private int I;

    /* renamed from: a  reason: collision with root package name */
    private View f8939a;

    /* renamed from: b  reason: collision with root package name */
    private int f8940b;

    /* renamed from: c  reason: collision with root package name */
    private int f8941c;

    /* renamed from: d  reason: collision with root package name */
    private float f8942d;
    private float e;
    private float f;
    private float g;
    private boolean h;
    private int i;
    private int j;
    private final p k;
    private final l l;
    private final int[] m;
    private final int[] n;
    private final int[] o;
    private boolean p;
    private boolean q;
    private float r;
    private float s;
    private float t;
    private int u;
    private int v;
    private int w;
    private int x;
    private c y;
    private a z;

    public interface a {
        void a(SpringBackLayout springBackLayout, int i, int i2);

        void onStateChanged(int i, int i2);
    }

    public interface b {
        boolean onSpringBack();
    }

    public SpringBackLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public SpringBackLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.i = -1;
        this.j = 0;
        this.m = new int[2];
        this.n = new int[2];
        this.o = new int[2];
        this.F = true;
        this.G = new ArrayList();
        this.I = 0;
        this.k = new p(this);
        this.l = d.c.b.b.a((View) this);
        this.f8941c = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, d.j.a.SpringBackLayout);
        this.f8940b = obtainStyledAttributes.getResourceId(d.j.a.SpringBackLayout_scrollableView, -1);
        this.w = obtainStyledAttributes.getInt(d.j.a.SpringBackLayout_scrollOrientation, 2);
        this.x = obtainStyledAttributes.getInt(d.j.a.SpringBackLayout_springBackMode, 3);
        obtainStyledAttributes.recycle();
        this.y = new c();
        this.z = new a(this, this.w);
        setNestedScrollingEnabled(true);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        this.A = displayMetrics.widthPixels;
        this.B = displayMetrics.heightPixels;
        if (d.f.a.f8810a) {
            this.F = false;
        }
    }

    private void a() {
        if (getScrollX() != 0) {
            this.h = true;
            float d2 = d((float) Math.abs(getScrollX()), 2);
            this.f = getScrollX() < 0 ? this.f - d2 : this.f + d2;
            this.g = this.f;
            return;
        }
        this.h = false;
    }

    private void a(float f2, int i2) {
        if (i2 == 2) {
            scrollTo(0, (int) (-f2));
        } else {
            scrollTo((int) (-f2), 0);
        }
    }

    private void a(float f2, int i2, boolean z2) {
        b bVar = this.H;
        if (bVar == null || !bVar.onSpringBack()) {
            this.y.b();
            this.y.a((float) getScrollX(), 0.0f, (float) getScrollY(), 0.0f, f2, i2, false);
            c(2);
            if (z2) {
                postInvalidateOnAnimation();
            }
        }
    }

    private void a(int i2, @NonNull int[] iArr, int i3) {
        if (i3 == 2) {
            iArr[1] = i2;
        } else {
            iArr[0] = i2;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0068  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(android.view.MotionEvent r6) {
        /*
            r5 = this;
            miuix.springback.view.a r0 = r5.z
            r0.a(r6)
            int r0 = r6.getActionMasked()
            r1 = 0
            r2 = 2
            r3 = 1
            if (r0 == 0) goto L_0x003a
            if (r0 == r3) goto L_0x002a
            if (r0 == r2) goto L_0x001d
            r4 = 3
            if (r0 == r4) goto L_0x002a
            r1 = 6
            if (r0 == r1) goto L_0x0019
            goto L_0x006b
        L_0x0019:
            r5.d((android.view.MotionEvent) r6)
            goto L_0x006b
        L_0x001d:
            int r6 = r5.v
            if (r6 != 0) goto L_0x006b
            miuix.springback.view.a r6 = r5.z
            int r6 = r6.e
            if (r6 == 0) goto L_0x006b
            r5.v = r6
            goto L_0x006b
        L_0x002a:
            r5.c((boolean) r1)
            int r6 = r5.w
            r6 = r6 & r2
            if (r6 == 0) goto L_0x0036
            r5.h(r2)
            goto L_0x006b
        L_0x0036:
            r5.h(r3)
            goto L_0x006b
        L_0x003a:
            miuix.springback.view.a r6 = r5.z
            float r0 = r6.f8944b
            r5.f8942d = r0
            float r0 = r6.f8945c
            r5.f = r0
            int r6 = r6.f8946d
            r5.i = r6
            int r6 = r5.getScrollY()
            if (r6 == 0) goto L_0x0054
            r5.v = r2
        L_0x0050:
            r5.b((boolean) r3)
            goto L_0x005f
        L_0x0054:
            int r6 = r5.getScrollX()
            if (r6 == 0) goto L_0x005d
            r5.v = r3
            goto L_0x0050
        L_0x005d:
            r5.v = r1
        L_0x005f:
            int r6 = r5.w
            r6 = r6 & r2
            if (r6 == 0) goto L_0x0068
            r5.b((int) r2)
            goto L_0x006b
        L_0x0068:
            r5.b((int) r3)
        L_0x006b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.springback.view.SpringBackLayout.a(android.view.MotionEvent):void");
    }

    private boolean a(MotionEvent motionEvent, int i2, int i3) {
        float f2;
        float f3;
        float f4;
        int i4;
        if (i2 == 0) {
            this.i = motionEvent.getPointerId(0);
            b(i3);
        } else if (i2 != 1) {
            if (i2 == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.i);
                if (findPointerIndex < 0) {
                    Log.e("SpringBackLayout", "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                } else if (this.h) {
                    if (i3 == 2) {
                        f3 = motionEvent.getY(findPointerIndex);
                        f2 = Math.signum(f3 - this.e);
                        f4 = this.e;
                    } else {
                        f3 = motionEvent.getX(findPointerIndex);
                        f2 = Math.signum(f3 - this.g);
                        f4 = this.g;
                    }
                    float c2 = f2 * c(f3 - f4, i3);
                    if (c2 > 0.0f) {
                        b(true);
                        a(c2, i3);
                    } else {
                        a(0.0f, i3);
                        return false;
                    }
                }
            } else if (i2 == 3) {
                return false;
            } else {
                if (i2 == 5) {
                    int findPointerIndex2 = motionEvent.findPointerIndex(this.i);
                    if (findPointerIndex2 < 0) {
                        Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid active pointer id.");
                        return false;
                    }
                    if (i3 == 2) {
                        float y2 = motionEvent.getY(findPointerIndex2) - this.f8942d;
                        i4 = motionEvent.getActionIndex();
                        if (i4 < 0) {
                            Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                            return false;
                        }
                        this.f8942d = motionEvent.getY(i4) - y2;
                        this.e = this.f8942d;
                    } else {
                        float x2 = motionEvent.getX(findPointerIndex2) - this.f;
                        i4 = motionEvent.getActionIndex();
                        if (i4 < 0) {
                            Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                            return false;
                        }
                        this.f = motionEvent.getX(i4) - x2;
                        this.g = this.f;
                    }
                    this.i = motionEvent.getPointerId(i4);
                } else if (i2 == 6) {
                    d(motionEvent);
                }
            }
        } else if (motionEvent.findPointerIndex(this.i) < 0) {
            Log.e("SpringBackLayout", "Got ACTION_UP event but don't have an active pointer id.");
            return false;
        } else {
            if (this.h) {
                this.h = false;
                h(i3);
            }
            this.i = -1;
            return false;
        }
        return true;
    }

    private float b(float f2, int i2) {
        int i3 = i2 == 2 ? this.B : this.A;
        double min = (double) Math.min(f2, 1.0f);
        return ((float) (((Math.pow(min, 3.0d) / 3.0d) - Math.pow(min, 2.0d)) + min)) * ((float) i3);
    }

    private void b() {
        if (getScrollY() != 0) {
            this.h = true;
            float d2 = d((float) Math.abs(getScrollY()), 2);
            this.f8942d = getScrollY() < 0 ? this.f8942d - d2 : this.f8942d + d2;
            this.e = this.f8942d;
            return;
        }
        this.h = false;
    }

    private void b(int i2) {
        if (i2 == 2) {
            b();
        } else {
            a();
        }
    }

    private void b(int i2, @NonNull int[] iArr, int i3) {
        float f2;
        boolean z2 = this.u == 2;
        int i4 = z2 ? 2 : 1;
        int abs = Math.abs(z2 ? getScrollY() : getScrollX());
        float f3 = 0.0f;
        if (i3 == 0) {
            if (i2 > 0) {
                float f4 = this.s;
                if (f4 > 0.0f) {
                    float f5 = (float) i2;
                    if (f5 > f4) {
                        a((int) f4, iArr, i4);
                        this.s = 0.0f;
                    } else {
                        this.s = f4 - f5;
                        a(i2, iArr, i4);
                    }
                    c(1);
                    f2 = c(this.s, i4);
                }
            }
            if (i2 < 0) {
                float f6 = this.t;
                if ((-f6) < 0.0f) {
                    float f7 = (float) i2;
                    if (f7 < (-f6)) {
                        a((int) f6, iArr, i4);
                        this.t = 0.0f;
                    } else {
                        this.t = f6 + f7;
                        a(i2, iArr, i4);
                    }
                    c(1);
                    f2 = -c(this.t, i4);
                } else {
                    return;
                }
            } else {
                return;
            }
        } else {
            float f8 = i4 == 2 ? this.D : this.C;
            if (i2 > 0) {
                float f9 = this.s;
                if (f9 > 0.0f) {
                    if (f8 > 2000.0f) {
                        float c2 = c(f9, i4);
                        float f10 = (float) i2;
                        if (f10 > c2) {
                            a((int) c2, iArr, i4);
                            this.s = 0.0f;
                        } else {
                            a(i2, iArr, i4);
                            f3 = c2 - f10;
                            this.s = d(f3, i4);
                        }
                        a(f3, i4);
                        c(1);
                        return;
                    }
                    if (!this.E) {
                        this.E = true;
                        a(f8, i4, false);
                    }
                    if (this.y.a()) {
                        scrollTo(this.y.c(), this.y.d());
                        this.s = d((float) abs, i4);
                    } else {
                        this.s = 0.0f;
                    }
                    a(i2, iArr, i4);
                    return;
                }
            }
            if (i2 < 0) {
                float f11 = this.t;
                if ((-f11) < 0.0f) {
                    if (f8 < -2000.0f) {
                        float c3 = c(f11, i4);
                        float f12 = (float) i2;
                        if (f12 < (-c3)) {
                            a((int) c3, iArr, i4);
                            this.t = 0.0f;
                        } else {
                            a(i2, iArr, i4);
                            f3 = c3 + f12;
                            this.t = d(f3, i4);
                        }
                        c(1);
                        f2 = -f3;
                    } else {
                        if (!this.E) {
                            this.E = true;
                            a(f8, i4, false);
                        }
                        if (this.y.a()) {
                            scrollTo(this.y.c(), this.y.d());
                            this.t = d((float) abs, i4);
                        } else {
                            this.t = 0.0f;
                        }
                        a(i2, iArr, i4);
                        return;
                    }
                }
            }
            if (i2 != 0) {
                if (!((this.t == 0.0f || this.s == 0.0f) && this.E && getScrollY() == 0)) {
                    return;
                }
                a(i2, iArr, i4);
                return;
            }
            return;
        }
        a(f2, i4);
    }

    private boolean b(MotionEvent motionEvent) {
        float f2;
        String str;
        boolean z2 = false;
        if (!f(1) && !e(1)) {
            return false;
        }
        if (f(1) && !e()) {
            return false;
        }
        if (e(1) && !d()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int i2 = this.i;
                    if (i2 == -1) {
                        str = "Got ACTION_MOVE event but don't have an active pointer id.";
                    } else {
                        int findPointerIndex = motionEvent.findPointerIndex(i2);
                        if (findPointerIndex < 0) {
                            str = "Got ACTION_MOVE event but have an invalid active pointer id.";
                        } else {
                            f2 = motionEvent.getX(findPointerIndex);
                            if (e(1) && f(1)) {
                                z2 = true;
                            }
                            if ((z2 || !f(1)) && (!z2 || f2 <= this.f) ? !(this.f - f2 <= ((float) this.f8941c) || this.h) : !(f2 - this.f <= ((float) this.f8941c) || this.h)) {
                                this.h = true;
                                c(1);
                            }
                        }
                    }
                    Log.e("SpringBackLayout", str);
                    return false;
                } else if (actionMasked != 3) {
                    if (actionMasked == 6) {
                        d(motionEvent);
                    }
                }
                return this.h;
            }
            this.h = false;
            this.i = -1;
            return this.h;
        }
        this.i = motionEvent.getPointerId(0);
        int findPointerIndex2 = motionEvent.findPointerIndex(this.i);
        if (findPointerIndex2 < 0) {
            return false;
        }
        this.f = motionEvent.getX(findPointerIndex2);
        if (getScrollX() != 0) {
            this.h = true;
            f2 = this.f;
        } else {
            this.h = false;
            return this.h;
        }
        this.g = f2;
        return this.h;
    }

    private boolean b(MotionEvent motionEvent, int i2, int i3) {
        float f2;
        float f3;
        float f4;
        int i4;
        if (i2 == 0) {
            this.i = motionEvent.getPointerId(0);
            b(i3);
        } else if (i2 != 1) {
            if (i2 == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.i);
                if (findPointerIndex < 0) {
                    Log.e("SpringBackLayout", "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                } else if (this.h) {
                    if (i3 == 2) {
                        f3 = motionEvent.getY(findPointerIndex);
                        f2 = Math.signum(f3 - this.e);
                        f4 = this.e;
                    } else {
                        f3 = motionEvent.getX(findPointerIndex);
                        f2 = Math.signum(f3 - this.g);
                        f4 = this.g;
                    }
                    b(true);
                    a(f2 * c(f3 - f4, i3), i3);
                }
            } else if (i2 == 3) {
                return false;
            } else {
                if (i2 == 5) {
                    int findPointerIndex2 = motionEvent.findPointerIndex(this.i);
                    if (findPointerIndex2 < 0) {
                        Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid active pointer id.");
                        return false;
                    }
                    if (i3 == 2) {
                        float y2 = motionEvent.getY(findPointerIndex2) - this.f8942d;
                        i4 = motionEvent.getActionIndex();
                        if (i4 < 0) {
                            Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                            return false;
                        }
                        this.f8942d = motionEvent.getY(i4) - y2;
                        this.e = this.f8942d;
                    } else {
                        float x2 = motionEvent.getX(findPointerIndex2) - this.f;
                        i4 = motionEvent.getActionIndex();
                        if (i4 < 0) {
                            Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                            return false;
                        }
                        this.f = motionEvent.getX(i4) - x2;
                        this.g = this.f;
                    }
                    this.i = motionEvent.getPointerId(i4);
                } else if (i2 == 6) {
                    d(motionEvent);
                }
            }
        } else if (motionEvent.findPointerIndex(this.i) < 0) {
            Log.e("SpringBackLayout", "Got ACTION_UP event but don't have an active pointer id.");
            return false;
        } else {
            if (this.h) {
                this.h = false;
                h(i3);
            }
            this.i = -1;
            return false;
        }
        return true;
    }

    private float c(float f2, int i2) {
        return b(Math.min(Math.abs(f2) / ((float) (i2 == 2 ? this.B : this.A)), 1.0f), i2);
    }

    private void c() {
        if (this.f8939a == null) {
            int i2 = this.f8940b;
            if (i2 != -1) {
                this.f8939a = findViewById(i2);
            } else {
                throw new IllegalArgumentException("invalid target Id");
            }
        }
        if (this.f8939a != null) {
            if (Build.VERSION.SDK_INT >= 21 && isEnabled()) {
                View view = this.f8939a;
                if ((view instanceof j) && !view.isNestedScrollingEnabled()) {
                    this.f8939a.setNestedScrollingEnabled(true);
                }
            }
            if (this.f8939a.getOverScrollMode() != 2) {
                this.f8939a.setOverScrollMode(2);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("fail to get target");
    }

    private void c(int i2) {
        int i3 = this.I;
        if (i3 != i2) {
            this.I = i2;
            for (a onStateChanged : this.G) {
                onStateChanged.onStateChanged(i3, i2);
            }
        }
    }

    private void c(boolean z2) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z2);
        }
    }

    private boolean c(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (f(1) || e(1)) {
            return (!f(1) || !e(1)) ? e(1) ? c(motionEvent, actionMasked, 1) : a(motionEvent, actionMasked, 1) : b(motionEvent, actionMasked, 1);
        }
        return false;
    }

    private boolean c(MotionEvent motionEvent, int i2, int i3) {
        float f2;
        float f3;
        float f4;
        int i4;
        if (i2 == 0) {
            this.i = motionEvent.getPointerId(0);
            b(i3);
        } else if (i2 != 1) {
            if (i2 == 2) {
                int findPointerIndex = motionEvent.findPointerIndex(this.i);
                if (findPointerIndex < 0) {
                    Log.e("SpringBackLayout", "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                } else if (this.h) {
                    if (i3 == 2) {
                        f3 = motionEvent.getY(findPointerIndex);
                        f2 = Math.signum(this.e - f3);
                        f4 = this.e;
                    } else {
                        f3 = motionEvent.getX(findPointerIndex);
                        f2 = Math.signum(this.g - f3);
                        f4 = this.g;
                    }
                    float c2 = f2 * c(f4 - f3, i3);
                    if (c2 > 0.0f) {
                        b(true);
                        a(-c2, i3);
                    } else {
                        a(0.0f, i3);
                        return false;
                    }
                }
            } else if (i2 == 3) {
                return false;
            } else {
                if (i2 == 5) {
                    int findPointerIndex2 = motionEvent.findPointerIndex(this.i);
                    if (findPointerIndex2 < 0) {
                        Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid active pointer id.");
                        return false;
                    }
                    if (i3 == 2) {
                        float y2 = motionEvent.getY(findPointerIndex2) - this.f8942d;
                        i4 = motionEvent.getActionIndex();
                        if (i4 < 0) {
                            Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                            return false;
                        }
                        this.f8942d = motionEvent.getY(i4) - y2;
                        this.e = this.f8942d;
                    } else {
                        float x2 = motionEvent.getX(findPointerIndex2) - this.f;
                        i4 = motionEvent.getActionIndex();
                        if (i4 < 0) {
                            Log.e("SpringBackLayout", "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                            return false;
                        }
                        this.f = motionEvent.getX(i4) - x2;
                        this.g = this.f;
                    }
                    this.i = motionEvent.getPointerId(i4);
                } else if (i2 == 6) {
                    d(motionEvent);
                }
            }
        } else if (motionEvent.findPointerIndex(this.i) < 0) {
            Log.e("SpringBackLayout", "Got ACTION_UP event but don't have an active pointer id.");
            return false;
        } else {
            if (this.h) {
                this.h = false;
                h(i3);
            }
            this.i = -1;
            return false;
        }
        return true;
    }

    private float d(float f2, int i2) {
        int i3 = i2 == 2 ? this.B : this.A;
        double d2 = (double) i3;
        return (float) (d2 - (Math.pow(d2, 0.6666666666666666d) * Math.pow((double) (((float) i3) - (f2 * 3.0f)), 0.3333333333333333d)));
    }

    private void d(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.i) {
            this.i = motionEvent.getPointerId(actionIndex == 0 ? 1 : 0);
        }
    }

    private boolean d() {
        return (this.x & 2) != 0;
    }

    private boolean d(int i2) {
        return this.v == i2;
    }

    private boolean e() {
        return (this.x & 1) != 0;
    }

    private boolean e(int i2) {
        if (i2 != 2) {
            return !this.f8939a.canScrollHorizontally(1);
        }
        View view = this.f8939a;
        return view instanceof ListView ? !g.a((ListView) view, 1) : !view.canScrollVertically(1);
    }

    private boolean e(MotionEvent motionEvent) {
        float f2;
        String str;
        boolean z2 = false;
        if (!f(2) && !e(2)) {
            return false;
        }
        if (f(2) && !e()) {
            return false;
        }
        if (e(2) && !d()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int i2 = this.i;
                    if (i2 == -1) {
                        str = "Got ACTION_MOVE event but don't have an active pointer id.";
                    } else {
                        int findPointerIndex = motionEvent.findPointerIndex(i2);
                        if (findPointerIndex < 0) {
                            str = "Got ACTION_MOVE event but have an invalid active pointer id.";
                        } else {
                            f2 = motionEvent.getY(findPointerIndex);
                            if (e(2) && f(2)) {
                                z2 = true;
                            }
                            if ((z2 || !f(2)) && (!z2 || f2 <= this.f8942d) ? !(this.f8942d - f2 <= ((float) this.f8941c) || this.h) : !(f2 - this.f8942d <= ((float) this.f8941c) || this.h)) {
                                this.h = true;
                                c(1);
                            }
                        }
                    }
                    Log.e("SpringBackLayout", str);
                    return false;
                } else if (actionMasked != 3) {
                    if (actionMasked == 6) {
                        d(motionEvent);
                    }
                }
                return this.h;
            }
            this.h = false;
            this.i = -1;
            return this.h;
        }
        this.i = motionEvent.getPointerId(0);
        int findPointerIndex2 = motionEvent.findPointerIndex(this.i);
        if (findPointerIndex2 < 0) {
            return false;
        }
        this.f8942d = motionEvent.getY(findPointerIndex2);
        if (getScrollY() != 0) {
            this.h = true;
            f2 = this.f8942d;
        } else {
            this.h = false;
            return this.h;
        }
        this.e = f2;
        return this.h;
    }

    private boolean f(int i2) {
        if (i2 != 2) {
            return !this.f8939a.canScrollHorizontally(-1);
        }
        View view = this.f8939a;
        return view instanceof ListView ? !g.a((ListView) view, -1) : !view.canScrollVertically(-1);
    }

    private boolean f(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (f(2) || e(2)) {
            return (!f(2) || !e(2)) ? e(2) ? c(motionEvent, actionMasked, 2) : a(motionEvent, actionMasked, 2) : b(motionEvent, actionMasked, 2);
        }
        return false;
    }

    private float g(int i2) {
        return b(1.0f, i2);
    }

    private void h(int i2) {
        a(0.0f, i2, true);
    }

    public void a(int i2) {
        this.l.c(i2);
    }

    public void a(int i2, int i3, int i4, int i5, @Nullable int[] iArr, int i6, @NonNull int[] iArr2) {
        this.l.a(i2, i3, i4, i5, iArr, i6, iArr2);
    }

    public void a(boolean z2) {
        super.requestDisallowInterceptTouchEvent(z2);
    }

    public boolean a(float f2, float f3) {
        this.C = f2;
        this.D = f3;
        return true;
    }

    public boolean a(int i2, int i3, @Nullable int[] iArr, @Nullable int[] iArr2, int i4) {
        return this.l.a(i2, i3, iArr, iArr2, i4);
    }

    public void b(boolean z2) {
        ViewParent parent = getParent();
        parent.requestDisallowInterceptTouchEvent(z2);
        while (parent != null) {
            if (parent instanceof SpringBackLayout) {
                ((SpringBackLayout) parent).a(z2);
            }
            parent = parent.getParent();
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.y.a()) {
            scrollTo(this.y.c(), this.y.d());
            if (!this.y.e()) {
                postInvalidateOnAnimation();
            } else {
                c(0);
            }
        }
    }

    public boolean dispatchNestedFling(float f2, float f3, boolean z2) {
        return this.l.a(f2, f3, z2);
    }

    public boolean dispatchNestedPreFling(float f2, float f3) {
        return this.l.a(f2, f3);
    }

    public boolean dispatchNestedPreScroll(int i2, int i3, int[] iArr, int[] iArr2) {
        return this.l.a(i2, i3, iArr, iArr2);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && this.I == 2) {
            c(1);
        }
        boolean dispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
        if (motionEvent.getActionMasked() == 1 && this.I != 2) {
            c(0);
        }
        return dispatchTouchEvent;
    }

    public int getSpringBackMode() {
        return this.x;
    }

    public boolean isNestedScrollingEnabled() {
        return this.l.b();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.F || !isEnabled() || this.p || this.q || (Build.VERSION.SDK_INT >= 21 && this.f8939a.isNestedScrollingEnabled())) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (!this.y.e() && actionMasked == 0) {
            this.y.b();
        }
        if (!e() && !d()) {
            return false;
        }
        int i2 = this.w;
        if ((i2 & 4) != 0) {
            a(motionEvent);
            if (d(2) && (this.w & 1) != 0 && ((float) getScrollX()) == 0.0f) {
                return false;
            }
            if (d(1) && (this.w & 2) != 0 && ((float) getScrollY()) == 0.0f) {
                return false;
            }
            if (d(2) || d(1)) {
                c(true);
            }
        } else {
            this.v = i2;
        }
        if (d(2)) {
            return e(motionEvent);
        }
        if (d(1)) {
            return b(motionEvent);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        this.f8939a.layout(paddingLeft, paddingTop, ((measuredWidth - getPaddingLeft()) - getPaddingRight()) + paddingLeft, ((measuredHeight - getPaddingTop()) - getPaddingBottom()) + paddingTop);
    }

    public void onMeasure(int i2, int i3) {
        c();
        int mode = View.MeasureSpec.getMode(i2);
        int mode2 = View.MeasureSpec.getMode(i3);
        int size = View.MeasureSpec.getSize(i2);
        int size2 = View.MeasureSpec.getSize(i3);
        measureChild(this.f8939a, i2, i3);
        if (size > this.f8939a.getMeasuredWidth()) {
            size = this.f8939a.getMeasuredWidth();
        }
        if (size2 > this.f8939a.getMeasuredHeight()) {
            size2 = this.f8939a.getMeasuredHeight();
        }
        if (mode != 1073741824) {
            size = this.f8939a.getMeasuredWidth();
        }
        if (mode2 != 1073741824) {
            size2 = this.f8939a.getMeasuredHeight();
        }
        setMeasuredDimension(size, size2);
    }

    public boolean onNestedFling(View view, float f2, float f3, boolean z2) {
        return dispatchNestedFling(f2, f3, z2);
    }

    public boolean onNestedPreFling(View view, float f2, float f3) {
        return dispatchNestedPreFling(f2, f3);
    }

    public void onNestedPreScroll(@NonNull View view, int i2, int i3, @NonNull int[] iArr, int i4) {
        if (this.F) {
            if (this.u == 2) {
                b(i3, iArr, i4);
            } else {
                b(i2, iArr, i4);
            }
        }
        int[] iArr2 = this.m;
        if (a(i2 - iArr[0], i3 - iArr[1], iArr2, (int[]) null, i4)) {
            iArr[0] = iArr[0] + iArr2[0];
            iArr[1] = iArr[1] + iArr2[1];
        }
    }

    public void onNestedScroll(View view, int i2, int i3, int i4, int i5) {
        onNestedScroll(view, i2, i3, i4, i5, 0, this.o);
    }

    public void onNestedScroll(@NonNull View view, int i2, int i3, int i4, int i5, int i6) {
        onNestedScroll(view, i2, i3, i4, i5, i6, this.o);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00b9, code lost:
        if (((float) (-r9)) <= r4) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00bb, code lost:
        r8.y.a(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0151, code lost:
        if (((float) r9) <= r4) goto L_0x00bb;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onNestedScroll(@androidx.annotation.NonNull android.view.View r16, int r17, int r18, int r19, int r20, int r21, @androidx.annotation.NonNull int[] r22) {
        /*
            r15 = this;
            r8 = r15
            int r0 = r8.u
            r9 = 0
            r10 = 2
            r11 = 1
            if (r0 != r10) goto L_0x000a
            r12 = r11
            goto L_0x000b
        L_0x000a:
            r12 = r9
        L_0x000b:
            if (r12 == 0) goto L_0x0010
            r13 = r18
            goto L_0x0012
        L_0x0010:
            r13 = r17
        L_0x0012:
            if (r12 == 0) goto L_0x0017
            r0 = r22[r11]
            goto L_0x0019
        L_0x0017:
            r0 = r22[r9]
        L_0x0019:
            r14 = r0
            int[] r5 = r8.n
            r0 = r15
            r1 = r17
            r2 = r18
            r3 = r19
            r4 = r20
            r6 = r21
            r7 = r22
            r0.a(r1, r2, r3, r4, r5, r6, r7)
            boolean r0 = r8.F
            if (r0 != 0) goto L_0x0031
            return
        L_0x0031:
            if (r12 == 0) goto L_0x0036
            r0 = r22[r11]
            goto L_0x0038
        L_0x0036:
            r0 = r22[r9]
        L_0x0038:
            int r0 = r0 - r14
            if (r12 == 0) goto L_0x003e
            int r0 = r20 - r0
            goto L_0x0040
        L_0x003e:
            int r0 = r19 - r0
        L_0x0040:
            if (r0 == 0) goto L_0x0043
            r9 = r0
        L_0x0043:
            if (r12 == 0) goto L_0x0047
            r1 = r10
            goto L_0x0048
        L_0x0047:
            r1 = r11
        L_0x0048:
            r2 = 4
            r3 = 0
            if (r9 >= 0) goto L_0x00ea
            boolean r4 = r15.f((int) r1)
            if (r4 == 0) goto L_0x00ea
            boolean r4 = r15.e()
            if (r4 == 0) goto L_0x00ea
            if (r21 == 0) goto L_0x00c5
            float r4 = r15.g(r1)
            float r5 = r8.D
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x00b1
            float r5 = r8.C
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x006b
            goto L_0x00b1
        L_0x006b:
            float r5 = r8.s
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x0072
            return
        L_0x0072:
            float r3 = r8.r
            float r4 = r4 - r3
            int r3 = r8.j
            if (r3 >= r2) goto L_0x0179
            int r2 = java.lang.Math.abs(r9)
            float r2 = (float) r2
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x008f
            float r0 = r8.r
            float r0 = r0 + r4
            r8.r = r0
            r0 = r22[r11]
            float r0 = (float) r0
            float r0 = r0 + r4
            int r0 = (int) r0
            r22[r11] = r0
            goto L_0x009e
        L_0x008f:
            float r2 = r8.r
            int r3 = java.lang.Math.abs(r9)
            float r3 = (float) r3
            float r2 = r2 + r3
            r8.r = r2
            r2 = r22[r11]
            int r2 = r2 + r0
            r22[r11] = r2
        L_0x009e:
            r15.c((int) r10)
            float r0 = r8.r
            float r0 = r15.c(r0, r1)
        L_0x00a7:
            r15.a((float) r0, (int) r1)
            int r0 = r8.j
            int r0 = r0 + r11
            r8.j = r0
            goto L_0x0179
        L_0x00b1:
            r8.E = r11
            if (r13 == 0) goto L_0x00c0
            int r0 = -r9
            float r0 = (float) r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 > 0) goto L_0x00c0
        L_0x00bb:
            miuix.springback.view.c r0 = r8.y
            r0.a(r9)
        L_0x00c0:
            r15.c((int) r10)
            goto L_0x0179
        L_0x00c5:
            miuix.springback.view.c r2 = r8.y
            boolean r2 = r2.e()
            if (r2 == 0) goto L_0x0179
            float r2 = r8.s
            int r3 = java.lang.Math.abs(r9)
            float r3 = (float) r3
            float r2 = r2 + r3
            r8.s = r2
            r15.c((int) r11)
            float r2 = r8.s
            float r2 = r15.c(r2, r1)
            r15.a((float) r2, (int) r1)
            r1 = r22[r11]
            int r1 = r1 + r0
            r22[r11] = r1
            goto L_0x0179
        L_0x00ea:
            if (r9 <= 0) goto L_0x0179
            boolean r4 = r15.e((int) r1)
            if (r4 == 0) goto L_0x0179
            boolean r4 = r15.d()
            if (r4 == 0) goto L_0x0179
            if (r21 == 0) goto L_0x0155
            float r4 = r15.g(r1)
            float r5 = r8.D
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x014a
            float r5 = r8.C
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x010b
            goto L_0x014a
        L_0x010b:
            float r5 = r8.t
            int r3 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x0112
            return
        L_0x0112:
            float r3 = r8.r
            float r4 = r4 - r3
            int r3 = r8.j
            if (r3 >= r2) goto L_0x0179
            int r2 = java.lang.Math.abs(r9)
            float r2 = (float) r2
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 > 0) goto L_0x012f
            float r0 = r8.r
            float r0 = r0 + r4
            r8.r = r0
            r0 = r22[r11]
            float r0 = (float) r0
            float r0 = r0 + r4
            int r0 = (int) r0
            r22[r11] = r0
            goto L_0x013e
        L_0x012f:
            float r2 = r8.r
            int r3 = java.lang.Math.abs(r9)
            float r3 = (float) r3
            float r2 = r2 + r3
            r8.r = r2
            r2 = r22[r11]
            int r2 = r2 + r0
            r22[r11] = r2
        L_0x013e:
            r15.c((int) r10)
            float r0 = r8.r
            float r0 = r15.c(r0, r1)
            float r0 = -r0
            goto L_0x00a7
        L_0x014a:
            r8.E = r11
            if (r13 == 0) goto L_0x00c0
            float r0 = (float) r9
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 > 0) goto L_0x00c0
            goto L_0x00bb
        L_0x0155:
            miuix.springback.view.c r2 = r8.y
            boolean r2 = r2.e()
            if (r2 == 0) goto L_0x0179
            float r2 = r8.t
            int r3 = java.lang.Math.abs(r9)
            float r3 = (float) r3
            float r2 = r2 + r3
            r8.t = r2
            r15.c((int) r11)
            float r2 = r8.t
            float r2 = r15.c(r2, r1)
            float r2 = -r2
            r15.a((float) r2, (int) r1)
            r1 = r22[r11]
            int r1 = r1 + r0
            r22[r11] = r1
        L_0x0179:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miuix.springback.view.SpringBackLayout.onNestedScroll(android.view.View, int, int, int, int, int, int[]):void");
    }

    public void onNestedScrollAccepted(View view, View view2, int i2) {
        this.k.a(view, view2, i2);
        startNestedScroll(i2 & 2);
    }

    public void onNestedScrollAccepted(@NonNull View view, @NonNull View view2, int i2, int i3) {
        if (this.F) {
            int i4 = 2;
            boolean z2 = this.u == 2;
            if (!z2) {
                i4 = 1;
            }
            float scrollY = (float) (z2 ? getScrollY() : getScrollX());
            if (i3 != 0) {
                if (scrollY == 0.0f) {
                    this.r = 0.0f;
                } else {
                    this.r = d(Math.abs(scrollY), i4);
                }
                this.p = true;
                this.j = 0;
            } else {
                if (scrollY == 0.0f) {
                    this.s = 0.0f;
                } else if (scrollY < 0.0f) {
                    this.s = d(Math.abs(scrollY), i4);
                } else {
                    this.s = 0.0f;
                    this.t = d(Math.abs(scrollY), i4);
                    this.q = true;
                }
                this.t = 0.0f;
                this.q = true;
            }
            this.D = 0.0f;
            this.C = 0.0f;
            this.E = false;
            this.y.b();
        }
        onNestedScrollAccepted(view, view2, i2);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i2, int i3, int i4, int i5) {
        super.onScrollChanged(i2, i3, i4, i5);
        for (a a2 : this.G) {
            a2.a(this, i2 - i4, i3 - i5);
        }
    }

    public boolean onStartNestedScroll(View view, View view2, int i2) {
        return isEnabled();
    }

    public boolean onStartNestedScroll(@NonNull View view, @NonNull View view2, int i2, int i3) {
        if (this.F) {
            this.u = i2;
            int i4 = 2;
            boolean z2 = this.u == 2;
            if (!z2) {
                i4 = 1;
            }
            if ((i4 & this.w) == 0 || !onStartNestedScroll(view, view, i2)) {
                return false;
            }
            float scrollY = (float) (z2 ? getScrollY() : getScrollX());
            if (!(i3 == 0 || scrollY == 0.0f || !(this.f8939a instanceof NestedScrollView))) {
                return false;
            }
        }
        if (this.l.a(i2, i3)) {
        }
        return true;
    }

    public void onStopNestedScroll(@NonNull View view, int i2) {
        this.k.a(view, i2);
        a(i2);
        if (this.F) {
            int i3 = 1;
            boolean z2 = this.u == 2;
            if (z2) {
                i3 = 2;
            }
            if (this.q) {
                this.q = false;
                float scrollY = (float) (z2 ? getScrollY() : getScrollX());
                if (this.p || scrollY == 0.0f) {
                    if (scrollY != 0.0f) {
                        c(2);
                        return;
                    }
                    return;
                }
            } else if (this.p) {
                this.p = false;
                if (this.E) {
                    if (this.y.e()) {
                        a(i3 == 2 ? this.D : this.C, i3, false);
                    }
                    postInvalidateOnAnimation();
                    return;
                }
            } else {
                return;
            }
            h(i3);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (!isEnabled() || this.p || this.q || (Build.VERSION.SDK_INT >= 21 && this.f8939a.isNestedScrollingEnabled())) {
            return false;
        }
        if (!this.y.e() && actionMasked == 0) {
            this.y.b();
        }
        if (d(2)) {
            return f(motionEvent);
        }
        if (d(1)) {
            return c(motionEvent);
        }
        return false;
    }

    public void requestDisallowInterceptTouchEvent(boolean z2) {
        if (!isEnabled() || !this.F) {
            super.requestDisallowInterceptTouchEvent(z2);
        }
    }

    public void setEnabled(boolean z2) {
        super.setEnabled(z2);
        View view = this.f8939a;
        if (view != null && (view instanceof j) && Build.VERSION.SDK_INT >= 21 && z2 != view.isNestedScrollingEnabled()) {
            this.f8939a.setNestedScrollingEnabled(z2);
        }
    }

    public void setNestedScrollingEnabled(boolean z2) {
        this.l.a(z2);
    }

    public void setOnSpringListener(b bVar) {
        this.H = bVar;
    }

    public void setScrollOrientation(int i2) {
        this.w = i2;
        this.z.f = i2;
    }

    public void setSpringBackEnable(boolean z2) {
        this.F = z2;
    }

    public void setSpringBackMode(int i2) {
        this.x = i2;
    }

    public void setTarget(@NonNull View view) {
        this.f8939a = view;
        if (Build.VERSION.SDK_INT >= 21) {
            View view2 = this.f8939a;
            if ((view2 instanceof j) && !view2.isNestedScrollingEnabled()) {
                this.f8939a.setNestedScrollingEnabled(true);
            }
        }
    }

    public boolean startNestedScroll(int i2) {
        return this.l.b(i2);
    }

    public void stopNestedScroll() {
        this.l.c();
    }
}
