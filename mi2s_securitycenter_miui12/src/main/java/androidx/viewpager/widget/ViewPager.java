package androidx.viewpager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.Scroller;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.C0123a;
import androidx.core.view.ViewCompat;
import androidx.core.view.q;
import androidx.customview.view.AbsSavedState;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPager extends ViewGroup {

    /* renamed from: a  reason: collision with root package name */
    static final int[] f1283a = {16842931};

    /* renamed from: b  reason: collision with root package name */
    private static final Comparator<a> f1284b = new b();

    /* renamed from: c  reason: collision with root package name */
    private static final Interpolator f1285c = new c();

    /* renamed from: d  reason: collision with root package name */
    private static final h f1286d = new h();
    private boolean A;
    private int B = 1;
    private boolean C;
    private boolean D;
    private int E;
    private int F;
    private int G;
    private float H;
    private float I;
    private float J;
    private float K;
    private int L = -1;
    private VelocityTracker M;
    private int N;
    private int O;
    private int P;
    private int Q;
    private boolean R;
    private EdgeEffect S;
    private EdgeEffect T;
    private boolean U = true;
    private boolean V = false;
    private boolean W;
    private int aa;
    private List<e> ba;
    private e ca;
    private e da;
    private int e;
    private List<d> ea;
    private final ArrayList<a> f = new ArrayList<>();
    private f fa;
    private final a g = new a();
    private int ga;
    private final Rect h = new Rect();
    private int ha;
    a i;
    private ArrayList<View> ia;
    int j;
    private final Runnable ja = new d(this);
    private int k = -1;
    private int ka = 0;
    private Parcelable l = null;
    private ClassLoader m = null;
    private Scroller n;
    private boolean o;
    private g p;
    private int q;
    private Drawable r;
    private int s;
    private int t;
    private float u = -3.4028235E38f;
    private float v = Float.MAX_VALUE;
    private int w;
    private int x;
    private boolean y;
    private boolean z;

    @Inherited
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DecorView {
    }

    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new f();
        Parcelable adapterState;
        ClassLoader loader;
        int position;

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            classLoader = classLoader == null ? SavedState.class.getClassLoader() : classLoader;
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader);
            this.loader = classLoader;
        }

        public SavedState(@NonNull Parcelable parcelable) {
            super(parcelable);
        }

        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, i);
        }
    }

    static class a {

        /* renamed from: a  reason: collision with root package name */
        Object f1287a;

        /* renamed from: b  reason: collision with root package name */
        int f1288b;

        /* renamed from: c  reason: collision with root package name */
        boolean f1289c;

        /* renamed from: d  reason: collision with root package name */
        float f1290d;
        float e;

        a() {
        }
    }

    public static class b extends ViewGroup.LayoutParams {

        /* renamed from: a  reason: collision with root package name */
        public boolean f1291a;

        /* renamed from: b  reason: collision with root package name */
        public int f1292b;

        /* renamed from: c  reason: collision with root package name */
        float f1293c = 0.0f;

        /* renamed from: d  reason: collision with root package name */
        boolean f1294d;
        int e;
        int f;

        public b() {
            super(-1, -1);
        }

        public b(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ViewPager.f1283a);
            this.f1292b = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }

    class c extends C0123a {
        c() {
        }

        private boolean a() {
            a aVar = ViewPager.this.i;
            return aVar != null && aVar.a() > 1;
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            a aVar;
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(ViewPager.class.getName());
            accessibilityEvent.setScrollable(a());
            if (accessibilityEvent.getEventType() == 4096 && (aVar = ViewPager.this.i) != null) {
                accessibilityEvent.setItemCount(aVar.a());
                accessibilityEvent.setFromIndex(ViewPager.this.j);
                accessibilityEvent.setToIndex(ViewPager.this.j);
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, androidx.core.view.a.c cVar) {
            super.onInitializeAccessibilityNodeInfo(view, cVar);
            cVar.b((CharSequence) ViewPager.class.getName());
            cVar.g(a());
            if (ViewPager.this.canScrollHorizontally(1)) {
                cVar.a((int) MpegAudioHeader.MAX_FRAME_SIZE_BYTES);
            }
            if (ViewPager.this.canScrollHorizontally(-1)) {
                cVar.a(8192);
            }
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            ViewPager viewPager;
            int i2;
            if (super.performAccessibilityAction(view, i, bundle)) {
                return true;
            }
            if (i != 4096) {
                if (i != 8192 || !ViewPager.this.canScrollHorizontally(-1)) {
                    return false;
                }
                viewPager = ViewPager.this;
                i2 = viewPager.j - 1;
            } else if (!ViewPager.this.canScrollHorizontally(1)) {
                return false;
            } else {
                viewPager = ViewPager.this;
                i2 = viewPager.j + 1;
            }
            viewPager.setCurrentItem(i2);
            return true;
        }
    }

    public interface d {
        void a(@NonNull ViewPager viewPager, @Nullable a aVar, @Nullable a aVar2);
    }

    public interface e {
        void onPageScrollStateChanged(int i);

        void onPageScrolled(int i, float f, @Px int i2);

        void onPageSelected(int i);
    }

    public interface f {
        void transformPage(@NonNull View view, float f);
    }

    private class g extends DataSetObserver {
        g() {
        }

        public void onChanged() {
            ViewPager.this.a();
        }

        public void onInvalidated() {
            ViewPager.this.a();
        }
    }

    static class h implements Comparator<View> {
        h() {
        }

        /* renamed from: a */
        public int compare(View view, View view2) {
            b bVar = (b) view.getLayoutParams();
            b bVar2 = (b) view2.getLayoutParams();
            boolean z = bVar.f1291a;
            return z != bVar2.f1291a ? z ? 1 : -1 : bVar.e - bVar2.e;
        }
    }

    public ViewPager(@NonNull Context context) {
        super(context);
        b();
    }

    public ViewPager(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        b();
    }

    private int a(int i2, float f2, int i3, int i4) {
        if (Math.abs(i4) <= this.P || Math.abs(i3) <= this.N) {
            i2 += (int) (f2 + (i2 >= this.j ? 0.4f : 0.6f));
        } else if (i3 <= 0) {
            i2++;
        }
        if (this.f.size() <= 0) {
            return i2;
        }
        ArrayList<a> arrayList = this.f;
        return Math.max(this.f.get(0).f1288b, Math.min(i2, arrayList.get(arrayList.size() - 1).f1288b));
    }

    private Rect a(Rect rect, View view) {
        if (rect == null) {
            rect = new Rect();
        }
        if (view == null) {
            rect.set(0, 0, 0, 0);
            return rect;
        }
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        ViewParent parent = view.getParent();
        while ((parent instanceof ViewGroup) && parent != this) {
            ViewGroup viewGroup = (ViewGroup) parent;
            rect.left += viewGroup.getLeft();
            rect.right += viewGroup.getRight();
            rect.top += viewGroup.getTop();
            rect.bottom += viewGroup.getBottom();
            parent = viewGroup.getParent();
        }
        return rect;
    }

    private void a(int i2, int i3, int i4, int i5) {
        int min;
        if (i3 <= 0 || this.f.isEmpty()) {
            a b2 = b(this.j);
            min = (int) ((b2 != null ? Math.min(b2.e, this.v) : 0.0f) * ((float) ((i2 - getPaddingLeft()) - getPaddingRight())));
            if (min != getScrollX()) {
                a(false);
            } else {
                return;
            }
        } else if (!this.n.isFinished()) {
            this.n.setFinalX(getCurrentItem() * getClientWidth());
            return;
        } else {
            min = (int) ((((float) getScrollX()) / ((float) (((i3 - getPaddingLeft()) - getPaddingRight()) + i5))) * ((float) (((i2 - getPaddingLeft()) - getPaddingRight()) + i4)));
        }
        scrollTo(min, getScrollY());
    }

    private void a(int i2, boolean z2, int i3, boolean z3) {
        a b2 = b(i2);
        int clientWidth = b2 != null ? (int) (((float) getClientWidth()) * Math.max(this.u, Math.min(b2.e, this.v))) : 0;
        if (z2) {
            a(clientWidth, 0, i3);
            if (z3) {
                d(i2);
                return;
            }
            return;
        }
        if (z3) {
            d(i2);
        }
        a(false);
        scrollTo(clientWidth, 0);
        f(clientWidth);
    }

    private void a(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.L) {
            int i2 = actionIndex == 0 ? 1 : 0;
            this.H = motionEvent.getX(i2);
            this.L = motionEvent.getPointerId(i2);
            VelocityTracker velocityTracker = this.M;
            if (velocityTracker != null) {
                velocityTracker.clear();
            }
        }
    }

    private void a(a aVar, int i2, a aVar2) {
        int i3;
        int i4;
        a aVar3;
        a aVar4;
        int a2 = this.i.a();
        int clientWidth = getClientWidth();
        float f2 = clientWidth > 0 ? ((float) this.q) / ((float) clientWidth) : 0.0f;
        if (aVar2 != null) {
            int i5 = aVar2.f1288b;
            int i6 = aVar.f1288b;
            if (i5 < i6) {
                float f3 = aVar2.e + aVar2.f1290d + f2;
                int i7 = i5 + 1;
                int i8 = 0;
                while (i7 <= aVar.f1288b && i8 < this.f.size()) {
                    while (true) {
                        aVar4 = this.f.get(i8);
                        if (i7 > aVar4.f1288b && i8 < this.f.size() - 1) {
                            i8++;
                        }
                    }
                    while (i7 < aVar4.f1288b) {
                        f3 += this.i.a(i7) + f2;
                        i7++;
                    }
                    aVar4.e = f3;
                    f3 += aVar4.f1290d + f2;
                    i7++;
                }
            } else if (i5 > i6) {
                int size = this.f.size() - 1;
                float f4 = aVar2.e;
                while (true) {
                    i5--;
                    if (i5 < aVar.f1288b || size < 0) {
                        break;
                    }
                    while (true) {
                        aVar3 = this.f.get(size);
                        if (i5 < aVar3.f1288b && size > 0) {
                            size--;
                        }
                    }
                    while (i5 > aVar3.f1288b) {
                        f4 -= this.i.a(i5) + f2;
                        i5--;
                    }
                    f4 -= aVar3.f1290d + f2;
                    aVar3.e = f4;
                }
            }
        }
        int size2 = this.f.size();
        float f5 = aVar.e;
        int i9 = aVar.f1288b;
        int i10 = i9 - 1;
        this.u = i9 == 0 ? f5 : -3.4028235E38f;
        int i11 = a2 - 1;
        this.v = aVar.f1288b == i11 ? (aVar.e + aVar.f1290d) - 1.0f : Float.MAX_VALUE;
        int i12 = i2 - 1;
        while (i12 >= 0) {
            a aVar5 = this.f.get(i12);
            while (true) {
                i4 = aVar5.f1288b;
                if (i10 <= i4) {
                    break;
                }
                f5 -= this.i.a(i10) + f2;
                i10--;
            }
            f5 -= aVar5.f1290d + f2;
            aVar5.e = f5;
            if (i4 == 0) {
                this.u = f5;
            }
            i12--;
            i10--;
        }
        float f6 = aVar.e + aVar.f1290d + f2;
        int i13 = aVar.f1288b + 1;
        int i14 = i2 + 1;
        while (i14 < size2) {
            a aVar6 = this.f.get(i14);
            while (true) {
                i3 = aVar6.f1288b;
                if (i13 >= i3) {
                    break;
                }
                f6 += this.i.a(i13) + f2;
                i13++;
            }
            if (i3 == i11) {
                this.v = (aVar6.f1290d + f6) - 1.0f;
            }
            aVar6.e = f6;
            f6 += aVar6.f1290d + f2;
            i14++;
            i13++;
        }
        this.V = false;
    }

    private void a(boolean z2) {
        boolean z3 = this.ka == 2;
        if (z3) {
            setScrollingCacheEnabled(false);
            if (!this.n.isFinished()) {
                this.n.abortAnimation();
                int scrollX = getScrollX();
                int scrollY = getScrollY();
                int currX = this.n.getCurrX();
                int currY = this.n.getCurrY();
                if (!(scrollX == currX && scrollY == currY)) {
                    scrollTo(currX, currY);
                    if (currX != scrollX) {
                        f(currX);
                    }
                }
            }
        }
        this.A = false;
        boolean z4 = z3;
        for (int i2 = 0; i2 < this.f.size(); i2++) {
            a aVar = this.f.get(i2);
            if (aVar.f1289c) {
                aVar.f1289c = false;
                z4 = true;
            }
        }
        if (!z4) {
            return;
        }
        if (z2) {
            ViewCompat.a((View) this, this.ja);
        } else {
            this.ja.run();
        }
    }

    private boolean a(float f2, float f3) {
        return (f2 < ((float) this.F) && f3 > 0.0f) || (f2 > ((float) (getWidth() - this.F)) && f3 < 0.0f);
    }

    private void b(int i2, float f2, int i3) {
        e eVar = this.ca;
        if (eVar != null) {
            eVar.onPageScrolled(i2, f2, i3);
        }
        List<e> list = this.ba;
        if (list != null) {
            int size = list.size();
            for (int i4 = 0; i4 < size; i4++) {
                e eVar2 = this.ba.get(i4);
                if (eVar2 != null) {
                    eVar2.onPageScrolled(i2, f2, i3);
                }
            }
        }
        e eVar3 = this.da;
        if (eVar3 != null) {
            eVar3.onPageScrolled(i2, f2, i3);
        }
    }

    private void b(boolean z2) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            getChildAt(i2).setLayerType(z2 ? this.ga : 0, (Paint) null);
        }
    }

    private boolean b(float f2) {
        boolean z2;
        boolean z3;
        float f3 = this.H - f2;
        this.H = f2;
        float scrollX = ((float) getScrollX()) + f3;
        float clientWidth = (float) getClientWidth();
        float f4 = this.u * clientWidth;
        float f5 = this.v * clientWidth;
        boolean z4 = false;
        a aVar = this.f.get(0);
        ArrayList<a> arrayList = this.f;
        a aVar2 = arrayList.get(arrayList.size() - 1);
        if (aVar.f1288b != 0) {
            f4 = aVar.e * clientWidth;
            z2 = false;
        } else {
            z2 = true;
        }
        if (aVar2.f1288b != this.i.a() - 1) {
            f5 = aVar2.e * clientWidth;
            z3 = false;
        } else {
            z3 = true;
        }
        if (scrollX < f4) {
            if (z2) {
                this.S.onPull(Math.abs(f4 - scrollX) / clientWidth);
                z4 = true;
            }
            scrollX = f4;
        } else if (scrollX > f5) {
            if (z3) {
                this.T.onPull(Math.abs(scrollX - f5) / clientWidth);
                z4 = true;
            }
            scrollX = f5;
        }
        int i2 = (int) scrollX;
        this.H += scrollX - ((float) i2);
        scrollTo(i2, getScrollY());
        f(i2);
        return z4;
    }

    private void c(boolean z2) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z2);
        }
    }

    private static boolean c(@NonNull View view) {
        return view.getClass().getAnnotation(DecorView.class) != null;
    }

    private void d(int i2) {
        e eVar = this.ca;
        if (eVar != null) {
            eVar.onPageSelected(i2);
        }
        List<e> list = this.ba;
        if (list != null) {
            int size = list.size();
            for (int i3 = 0; i3 < size; i3++) {
                e eVar2 = this.ba.get(i3);
                if (eVar2 != null) {
                    eVar2.onPageSelected(i2);
                }
            }
        }
        e eVar3 = this.da;
        if (eVar3 != null) {
            eVar3.onPageSelected(i2);
        }
    }

    private void e(int i2) {
        e eVar = this.ca;
        if (eVar != null) {
            eVar.onPageScrollStateChanged(i2);
        }
        List<e> list = this.ba;
        if (list != null) {
            int size = list.size();
            for (int i3 = 0; i3 < size; i3++) {
                e eVar2 = this.ba.get(i3);
                if (eVar2 != null) {
                    eVar2.onPageScrollStateChanged(i2);
                }
            }
        }
        e eVar3 = this.da;
        if (eVar3 != null) {
            eVar3.onPageScrollStateChanged(i2);
        }
    }

    private void f() {
        this.C = false;
        this.D = false;
        VelocityTracker velocityTracker = this.M;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.M = null;
        }
    }

    private boolean f(int i2) {
        if (this.f.size() != 0) {
            a g2 = g();
            int clientWidth = getClientWidth();
            int i3 = this.q;
            int i4 = clientWidth + i3;
            float f2 = (float) clientWidth;
            int i5 = g2.f1288b;
            float f3 = ((((float) i2) / f2) - g2.e) / (g2.f1290d + (((float) i3) / f2));
            this.W = false;
            a(i5, f3, (int) (((float) i4) * f3));
            if (this.W) {
                return true;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        } else if (this.U) {
            return false;
        } else {
            this.W = false;
            a(0, 0.0f, 0);
            if (this.W) {
                return false;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
    }

    private a g() {
        int i2;
        int clientWidth = getClientWidth();
        float scrollX = clientWidth > 0 ? ((float) getScrollX()) / ((float) clientWidth) : 0.0f;
        float f2 = clientWidth > 0 ? ((float) this.q) / ((float) clientWidth) : 0.0f;
        a aVar = null;
        float f3 = 0.0f;
        float f4 = 0.0f;
        int i3 = 0;
        int i4 = -1;
        boolean z2 = true;
        while (i3 < this.f.size()) {
            a aVar2 = this.f.get(i3);
            if (!z2 && aVar2.f1288b != (i2 = i4 + 1)) {
                aVar2 = this.g;
                aVar2.e = f3 + f4 + f2;
                aVar2.f1288b = i2;
                aVar2.f1290d = this.i.a(aVar2.f1288b);
                i3--;
            }
            f3 = aVar2.e;
            float f5 = aVar2.f1290d + f3 + f2;
            if (!z2 && scrollX < f3) {
                return aVar;
            }
            if (scrollX < f5 || i3 == this.f.size() - 1) {
                return aVar2;
            }
            i4 = aVar2.f1288b;
            f4 = aVar2.f1290d;
            i3++;
            z2 = false;
            aVar = aVar2;
        }
        return aVar;
    }

    private int getClientWidth() {
        return (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
    }

    private void h() {
        int i2 = 0;
        while (i2 < getChildCount()) {
            if (!((b) getChildAt(i2).getLayoutParams()).f1291a) {
                removeViewAt(i2);
                i2--;
            }
            i2++;
        }
    }

    private boolean i() {
        this.L = -1;
        f();
        this.S.onRelease();
        this.T.onRelease();
        return this.S.isFinished() || this.T.isFinished();
    }

    private void j() {
        if (this.ha != 0) {
            ArrayList<View> arrayList = this.ia;
            if (arrayList == null) {
                this.ia = new ArrayList<>();
            } else {
                arrayList.clear();
            }
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                this.ia.add(getChildAt(i2));
            }
            Collections.sort(this.ia, f1286d);
        }
    }

    private void setScrollingCacheEnabled(boolean z2) {
        if (this.z != z2) {
            this.z = z2;
        }
    }

    /* access modifiers changed from: package-private */
    public float a(float f2) {
        return (float) Math.sin((double) ((f2 - 0.5f) * 0.47123894f));
    }

    /* access modifiers changed from: package-private */
    public a a(int i2, int i3) {
        a aVar = new a();
        aVar.f1288b = i2;
        aVar.f1287a = this.i.a((ViewGroup) this, i2);
        aVar.f1290d = this.i.a(i2);
        if (i3 < 0 || i3 >= this.f.size()) {
            this.f.add(aVar);
        } else {
            this.f.add(i3, aVar);
        }
        return aVar;
    }

    /* access modifiers changed from: package-private */
    public a a(View view) {
        while (true) {
            ViewParent parent = view.getParent();
            if (parent == this) {
                return b(view);
            }
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            view = (View) parent;
        }
    }

    /* access modifiers changed from: package-private */
    public void a() {
        int a2 = this.i.a();
        this.e = a2;
        boolean z2 = this.f.size() < (this.B * 2) + 1 && this.f.size() < a2;
        int i2 = this.j;
        int i3 = 0;
        boolean z3 = false;
        while (i3 < this.f.size()) {
            a aVar = this.f.get(i3);
            int a3 = this.i.a(aVar.f1287a);
            if (a3 != -1) {
                if (a3 == -2) {
                    this.f.remove(i3);
                    i3--;
                    if (!z3) {
                        this.i.b((ViewGroup) this);
                        z3 = true;
                    }
                    this.i.a((ViewGroup) this, aVar.f1288b, aVar.f1287a);
                    int i4 = this.j;
                    if (i4 == aVar.f1288b) {
                        i2 = Math.max(0, Math.min(i4, a2 - 1));
                    }
                } else {
                    int i5 = aVar.f1288b;
                    if (i5 != a3) {
                        if (i5 == this.j) {
                            i2 = a3;
                        }
                        aVar.f1288b = a3;
                    }
                }
                z2 = true;
            }
            i3++;
        }
        if (z3) {
            this.i.a((ViewGroup) this);
        }
        Collections.sort(this.f, f1284b);
        if (z2) {
            int childCount = getChildCount();
            for (int i6 = 0; i6 < childCount; i6++) {
                b bVar = (b) getChildAt(i6).getLayoutParams();
                if (!bVar.f1291a) {
                    bVar.f1293c = 0.0f;
                }
            }
            a(i2, false, true);
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0066  */
    @androidx.annotation.CallSuper
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(int r13, float r14, int r15) {
        /*
            r12 = this;
            int r0 = r12.aa
            r1 = 0
            r2 = 1
            if (r0 <= 0) goto L_0x006d
            int r0 = r12.getScrollX()
            int r3 = r12.getPaddingLeft()
            int r4 = r12.getPaddingRight()
            int r5 = r12.getWidth()
            int r6 = r12.getChildCount()
            r7 = r4
            r4 = r3
            r3 = r1
        L_0x001d:
            if (r3 >= r6) goto L_0x006d
            android.view.View r8 = r12.getChildAt(r3)
            android.view.ViewGroup$LayoutParams r9 = r8.getLayoutParams()
            androidx.viewpager.widget.ViewPager$b r9 = (androidx.viewpager.widget.ViewPager.b) r9
            boolean r10 = r9.f1291a
            if (r10 != 0) goto L_0x002e
            goto L_0x006a
        L_0x002e:
            int r9 = r9.f1292b
            r9 = r9 & 7
            if (r9 == r2) goto L_0x004f
            r10 = 3
            if (r9 == r10) goto L_0x0049
            r10 = 5
            if (r9 == r10) goto L_0x003c
            r9 = r4
            goto L_0x005e
        L_0x003c:
            int r9 = r5 - r7
            int r10 = r8.getMeasuredWidth()
            int r9 = r9 - r10
            int r10 = r8.getMeasuredWidth()
            int r7 = r7 + r10
            goto L_0x005b
        L_0x0049:
            int r9 = r8.getWidth()
            int r9 = r9 + r4
            goto L_0x005e
        L_0x004f:
            int r9 = r8.getMeasuredWidth()
            int r9 = r5 - r9
            int r9 = r9 / 2
            int r9 = java.lang.Math.max(r9, r4)
        L_0x005b:
            r11 = r9
            r9 = r4
            r4 = r11
        L_0x005e:
            int r4 = r4 + r0
            int r10 = r8.getLeft()
            int r4 = r4 - r10
            if (r4 == 0) goto L_0x0069
            r8.offsetLeftAndRight(r4)
        L_0x0069:
            r4 = r9
        L_0x006a:
            int r3 = r3 + 1
            goto L_0x001d
        L_0x006d:
            r12.b(r13, r14, r15)
            androidx.viewpager.widget.ViewPager$f r13 = r12.fa
            if (r13 == 0) goto L_0x00a1
            int r13 = r12.getScrollX()
            int r14 = r12.getChildCount()
        L_0x007c:
            if (r1 >= r14) goto L_0x00a1
            android.view.View r15 = r12.getChildAt(r1)
            android.view.ViewGroup$LayoutParams r0 = r15.getLayoutParams()
            androidx.viewpager.widget.ViewPager$b r0 = (androidx.viewpager.widget.ViewPager.b) r0
            boolean r0 = r0.f1291a
            if (r0 == 0) goto L_0x008d
            goto L_0x009e
        L_0x008d:
            int r0 = r15.getLeft()
            int r0 = r0 - r13
            float r0 = (float) r0
            int r3 = r12.getClientWidth()
            float r3 = (float) r3
            float r0 = r0 / r3
            androidx.viewpager.widget.ViewPager$f r3 = r12.fa
            r3.transformPage(r15, r0)
        L_0x009e:
            int r1 = r1 + 1
            goto L_0x007c
        L_0x00a1:
            r12.W = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.viewpager.widget.ViewPager.a(int, float, int):void");
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3, int i4) {
        int i5;
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(false);
            return;
        }
        Scroller scroller = this.n;
        if (scroller != null && !scroller.isFinished()) {
            i5 = this.o ? this.n.getCurrX() : this.n.getStartX();
            this.n.abortAnimation();
            setScrollingCacheEnabled(false);
        } else {
            i5 = getScrollX();
        }
        int i6 = i5;
        int scrollY = getScrollY();
        int i7 = i2 - i6;
        int i8 = i3 - scrollY;
        if (i7 == 0 && i8 == 0) {
            a(false);
            e();
            setScrollState(0);
            return;
        }
        setScrollingCacheEnabled(true);
        setScrollState(2);
        int clientWidth = getClientWidth();
        int i9 = clientWidth / 2;
        float f2 = (float) clientWidth;
        float f3 = (float) i9;
        float a2 = f3 + (a(Math.min(1.0f, (((float) Math.abs(i7)) * 1.0f) / f2)) * f3);
        int abs = Math.abs(i4);
        int min = Math.min(abs > 0 ? Math.round(Math.abs(a2 / ((float) abs)) * 1000.0f) * 4 : (int) (((((float) Math.abs(i7)) / ((f2 * this.i.a(this.j)) + ((float) this.q))) + 1.0f) * 100.0f), 600);
        this.o = false;
        this.n.startScroll(i6, scrollY, i7, i8, min);
        ViewCompat.u(this);
    }

    public void a(int i2, boolean z2) {
        this.A = false;
        a(i2, z2, false);
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, boolean z2, boolean z3) {
        a(i2, z2, z3, 0);
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, boolean z2, boolean z3, int i3) {
        a aVar = this.i;
        if (aVar == null || aVar.a() <= 0) {
            setScrollingCacheEnabled(false);
        } else if (z3 || this.j != i2 || this.f.size() == 0) {
            boolean z4 = true;
            if (i2 < 0) {
                i2 = 0;
            } else if (i2 >= this.i.a()) {
                i2 = this.i.a() - 1;
            }
            int i4 = this.B;
            int i5 = this.j;
            if (i2 > i5 + i4 || i2 < i5 - i4) {
                for (int i6 = 0; i6 < this.f.size(); i6++) {
                    this.f.get(i6).f1289c = true;
                }
            }
            if (this.j == i2) {
                z4 = false;
            }
            if (this.U) {
                this.j = i2;
                if (z4) {
                    d(i2);
                }
                requestLayout();
                return;
            }
            c(i2);
            a(i2, z2, i3, z4);
        } else {
            setScrollingCacheEnabled(false);
        }
    }

    public void a(@NonNull e eVar) {
        if (this.ba == null) {
            this.ba = new ArrayList();
        }
        this.ba.add(eVar);
    }

    public boolean a(int i2) {
        boolean d2;
        boolean z2;
        View findFocus = findFocus();
        boolean z3 = false;
        View view = null;
        if (findFocus != this) {
            if (findFocus != null) {
                ViewParent parent = findFocus.getParent();
                while (true) {
                    if (!(parent instanceof ViewGroup)) {
                        z2 = false;
                        break;
                    } else if (parent == this) {
                        z2 = true;
                        break;
                    } else {
                        parent = parent.getParent();
                    }
                }
                if (!z2) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(findFocus.getClass().getSimpleName());
                    for (ViewParent parent2 = findFocus.getParent(); parent2 instanceof ViewGroup; parent2 = parent2.getParent()) {
                        sb.append(" => ");
                        sb.append(parent2.getClass().getSimpleName());
                    }
                    Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + sb.toString());
                }
            }
            view = findFocus;
        }
        View findNextFocus = FocusFinder.getInstance().findNextFocus(this, view, i2);
        if (findNextFocus != null && findNextFocus != view) {
            if (i2 == 17) {
                int i3 = a(this.h, findNextFocus).left;
                int i4 = a(this.h, view).left;
                if (view != null && i3 >= i4) {
                    d2 = c();
                    z3 = d2;
                }
            } else if (i2 == 66) {
                int i5 = a(this.h, findNextFocus).left;
                int i6 = a(this.h, view).left;
                if (view != null && i5 <= i6) {
                    d2 = d();
                    z3 = d2;
                }
            }
            d2 = findNextFocus.requestFocus();
            z3 = d2;
        } else if (i2 == 17 || i2 == 1) {
            z3 = c();
        } else if (i2 == 66 || i2 == 2) {
            z3 = d();
        }
        if (z3) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i2));
        }
        return z3;
    }

    public boolean a(@NonNull KeyEvent keyEvent) {
        int i2;
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode != 21) {
                if (keyCode != 22) {
                    if (keyCode == 61) {
                        if (keyEvent.hasNoModifiers()) {
                            return a(2);
                        }
                        if (keyEvent.hasModifiers(1)) {
                            return a(1);
                        }
                    }
                } else if (keyEvent.hasModifiers(2)) {
                    return d();
                } else {
                    i2 = 66;
                }
            } else if (keyEvent.hasModifiers(2)) {
                return c();
            } else {
                i2 = 17;
            }
            return a(i2);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean a(View view, boolean z2, int i2, int i3, int i4) {
        int i5;
        View view2 = view;
        if (view2 instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view2;
            int scrollX = view.getScrollX();
            int scrollY = view.getScrollY();
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                int i6 = i3 + scrollX;
                if (i6 >= childAt.getLeft() && i6 < childAt.getRight() && (i5 = i4 + scrollY) >= childAt.getTop() && i5 < childAt.getBottom()) {
                    if (a(childAt, true, i2, i6 - childAt.getLeft(), i5 - childAt.getTop())) {
                        return true;
                    }
                }
            }
        }
        return z2 && view.canScrollHorizontally(-i2);
    }

    public void addFocusables(ArrayList<View> arrayList, int i2, int i3) {
        a b2;
        int size = arrayList.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i4 = 0; i4 < getChildCount(); i4++) {
                View childAt = getChildAt(i4);
                if (childAt.getVisibility() == 0 && (b2 = b(childAt)) != null && b2.f1288b == this.j) {
                    childAt.addFocusables(arrayList, i2, i3);
                }
            }
        }
        if ((descendantFocusability == 262144 && size != arrayList.size()) || !isFocusable()) {
            return;
        }
        if (((i3 & 1) != 1 || !isInTouchMode() || isFocusableInTouchMode()) && arrayList != null) {
            arrayList.add(this);
        }
    }

    public void addTouchables(ArrayList<View> arrayList) {
        a b2;
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() == 0 && (b2 = b(childAt)) != null && b2.f1288b == this.j) {
                childAt.addTouchables(arrayList);
            }
        }
    }

    public void addView(View view, int i2, ViewGroup.LayoutParams layoutParams) {
        if (!checkLayoutParams(layoutParams)) {
            layoutParams = generateLayoutParams(layoutParams);
        }
        b bVar = (b) layoutParams;
        bVar.f1291a |= c(view);
        if (!this.y) {
            super.addView(view, i2, layoutParams);
        } else if (bVar == null || !bVar.f1291a) {
            bVar.f1294d = true;
            addViewInLayout(view, i2, layoutParams);
        } else {
            throw new IllegalStateException("Cannot add pager decor view during layout");
        }
    }

    /* access modifiers changed from: package-private */
    public a b(int i2) {
        for (int i3 = 0; i3 < this.f.size(); i3++) {
            a aVar = this.f.get(i3);
            if (aVar.f1288b == i2) {
                return aVar;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public a b(View view) {
        for (int i2 = 0; i2 < this.f.size(); i2++) {
            a aVar = this.f.get(i2);
            if (this.i.a(view, aVar.f1287a)) {
                return aVar;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        setWillNotDraw(false);
        setDescendantFocusability(262144);
        setFocusable(true);
        Context context = getContext();
        this.n = new Scroller(context, f1285c);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        float f2 = context.getResources().getDisplayMetrics().density;
        this.G = viewConfiguration.getScaledPagingTouchSlop();
        this.N = (int) (400.0f * f2);
        this.O = viewConfiguration.getScaledMaximumFlingVelocity();
        this.S = new EdgeEffect(context);
        this.T = new EdgeEffect(context);
        this.P = (int) (25.0f * f2);
        this.Q = (int) (2.0f * f2);
        this.E = (int) (f2 * 16.0f);
        ViewCompat.a((View) this, (C0123a) new c());
        if (ViewCompat.h(this) == 0) {
            ViewCompat.b((View) this, 1);
        }
        ViewCompat.a((View) this, (q) new e(this));
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0060, code lost:
        if (r9 == r10) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0066, code lost:
        r8 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00c3, code lost:
        if (r15 >= 0) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00d1, code lost:
        if (r15 >= 0) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00df, code lost:
        if (r15 >= 0) goto L_0x00e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00ea, code lost:
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x013f, code lost:
        if (r4 < r0.f.size()) goto L_0x0141;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x014a, code lost:
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x015d, code lost:
        if (r4 < r0.f.size()) goto L_0x0141;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x016f, code lost:
        if (r4 < r0.f.size()) goto L_0x0141;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void c(int r18) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            int r2 = r0.j
            if (r2 == r1) goto L_0x000f
            androidx.viewpager.widget.ViewPager$a r2 = r0.b((int) r2)
            r0.j = r1
            goto L_0x0010
        L_0x000f:
            r2 = 0
        L_0x0010:
            androidx.viewpager.widget.a r1 = r0.i
            if (r1 != 0) goto L_0x0018
            r17.j()
            return
        L_0x0018:
            boolean r1 = r0.A
            if (r1 == 0) goto L_0x0020
            r17.j()
            return
        L_0x0020:
            android.os.IBinder r1 = r17.getWindowToken()
            if (r1 != 0) goto L_0x0027
            return
        L_0x0027:
            androidx.viewpager.widget.a r1 = r0.i
            r1.b((android.view.ViewGroup) r0)
            int r1 = r0.B
            int r4 = r0.j
            int r4 = r4 - r1
            r5 = 0
            int r4 = java.lang.Math.max(r5, r4)
            androidx.viewpager.widget.a r6 = r0.i
            int r6 = r6.a()
            int r7 = r6 + -1
            int r8 = r0.j
            int r8 = r8 + r1
            int r1 = java.lang.Math.min(r7, r8)
            int r7 = r0.e
            if (r6 != r7) goto L_0x01f2
            r7 = r5
        L_0x004a:
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r8 = r0.f
            int r8 = r8.size()
            if (r7 >= r8) goto L_0x0066
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r8 = r0.f
            java.lang.Object r8 = r8.get(r7)
            androidx.viewpager.widget.ViewPager$a r8 = (androidx.viewpager.widget.ViewPager.a) r8
            int r9 = r8.f1288b
            int r10 = r0.j
            if (r9 < r10) goto L_0x0063
            if (r9 != r10) goto L_0x0066
            goto L_0x0067
        L_0x0063:
            int r7 = r7 + 1
            goto L_0x004a
        L_0x0066:
            r8 = 0
        L_0x0067:
            if (r8 != 0) goto L_0x0071
            if (r6 <= 0) goto L_0x0071
            int r8 = r0.j
            androidx.viewpager.widget.ViewPager$a r8 = r0.a((int) r8, (int) r7)
        L_0x0071:
            r9 = 0
            if (r8 == 0) goto L_0x017f
            int r10 = r7 + -1
            if (r10 < 0) goto L_0x0081
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r11 = r0.f
            java.lang.Object r11 = r11.get(r10)
            androidx.viewpager.widget.ViewPager$a r11 = (androidx.viewpager.widget.ViewPager.a) r11
            goto L_0x0082
        L_0x0081:
            r11 = 0
        L_0x0082:
            int r12 = r17.getClientWidth()
            r13 = 1073741824(0x40000000, float:2.0)
            if (r12 > 0) goto L_0x008c
            r3 = r9
            goto L_0x0099
        L_0x008c:
            float r14 = r8.f1290d
            float r14 = r13 - r14
            int r15 = r17.getPaddingLeft()
            float r15 = (float) r15
            float r3 = (float) r12
            float r15 = r15 / r3
            float r3 = r14 + r15
        L_0x0099:
            int r14 = r0.j
            int r14 = r14 + -1
            r15 = r10
            r10 = r7
            r7 = r9
        L_0x00a0:
            if (r14 < 0) goto L_0x00f0
            int r16 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r16 < 0) goto L_0x00c6
            if (r14 >= r4) goto L_0x00c6
            if (r11 != 0) goto L_0x00ab
            goto L_0x00f0
        L_0x00ab:
            int r5 = r11.f1288b
            if (r14 != r5) goto L_0x00ec
            boolean r5 = r11.f1289c
            if (r5 != 0) goto L_0x00ec
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            r5.remove(r15)
            androidx.viewpager.widget.a r5 = r0.i
            java.lang.Object r11 = r11.f1287a
            r5.a((android.view.ViewGroup) r0, (int) r14, (java.lang.Object) r11)
            int r15 = r15 + -1
            int r10 = r10 + -1
            if (r15 < 0) goto L_0x00ea
            goto L_0x00e1
        L_0x00c6:
            if (r11 == 0) goto L_0x00d4
            int r5 = r11.f1288b
            if (r14 != r5) goto L_0x00d4
            float r5 = r11.f1290d
            float r7 = r7 + r5
            int r15 = r15 + -1
            if (r15 < 0) goto L_0x00ea
            goto L_0x00e1
        L_0x00d4:
            int r5 = r15 + 1
            androidx.viewpager.widget.ViewPager$a r5 = r0.a((int) r14, (int) r5)
            float r5 = r5.f1290d
            float r7 = r7 + r5
            int r10 = r10 + 1
            if (r15 < 0) goto L_0x00ea
        L_0x00e1:
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            java.lang.Object r5 = r5.get(r15)
            androidx.viewpager.widget.ViewPager$a r5 = (androidx.viewpager.widget.ViewPager.a) r5
            goto L_0x00eb
        L_0x00ea:
            r5 = 0
        L_0x00eb:
            r11 = r5
        L_0x00ec:
            int r14 = r14 + -1
            r5 = 0
            goto L_0x00a0
        L_0x00f0:
            float r3 = r8.f1290d
            int r4 = r10 + 1
            int r5 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r5 >= 0) goto L_0x0173
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0109
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            java.lang.Object r5 = r5.get(r4)
            androidx.viewpager.widget.ViewPager$a r5 = (androidx.viewpager.widget.ViewPager.a) r5
            goto L_0x010a
        L_0x0109:
            r5 = 0
        L_0x010a:
            if (r12 > 0) goto L_0x010e
            r7 = r9
            goto L_0x0116
        L_0x010e:
            int r7 = r17.getPaddingRight()
            float r7 = (float) r7
            float r11 = (float) r12
            float r7 = r7 / r11
            float r7 = r7 + r13
        L_0x0116:
            int r11 = r0.j
        L_0x0118:
            int r11 = r11 + 1
            if (r11 >= r6) goto L_0x0173
            int r12 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r12 < 0) goto L_0x014c
            if (r11 <= r1) goto L_0x014c
            if (r5 != 0) goto L_0x0125
            goto L_0x0173
        L_0x0125:
            int r12 = r5.f1288b
            if (r11 != r12) goto L_0x0172
            boolean r12 = r5.f1289c
            if (r12 != 0) goto L_0x0172
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r12 = r0.f
            r12.remove(r4)
            androidx.viewpager.widget.a r12 = r0.i
            java.lang.Object r5 = r5.f1287a
            r12.a((android.view.ViewGroup) r0, (int) r11, (java.lang.Object) r5)
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x014a
        L_0x0141:
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            java.lang.Object r5 = r5.get(r4)
            androidx.viewpager.widget.ViewPager$a r5 = (androidx.viewpager.widget.ViewPager.a) r5
            goto L_0x0172
        L_0x014a:
            r5 = 0
            goto L_0x0172
        L_0x014c:
            if (r5 == 0) goto L_0x0160
            int r12 = r5.f1288b
            if (r11 != r12) goto L_0x0160
            float r5 = r5.f1290d
            float r3 = r3 + r5
            int r4 = r4 + 1
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x014a
            goto L_0x0141
        L_0x0160:
            androidx.viewpager.widget.ViewPager$a r5 = r0.a((int) r11, (int) r4)
            int r4 = r4 + 1
            float r5 = r5.f1290d
            float r3 = r3 + r5
            java.util.ArrayList<androidx.viewpager.widget.ViewPager$a> r5 = r0.f
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x014a
            goto L_0x0141
        L_0x0172:
            goto L_0x0118
        L_0x0173:
            r0.a((androidx.viewpager.widget.ViewPager.a) r8, (int) r10, (androidx.viewpager.widget.ViewPager.a) r2)
            androidx.viewpager.widget.a r1 = r0.i
            int r2 = r0.j
            java.lang.Object r3 = r8.f1287a
            r1.b(r0, r2, r3)
        L_0x017f:
            androidx.viewpager.widget.a r1 = r0.i
            r1.a((android.view.ViewGroup) r0)
            int r1 = r17.getChildCount()
            r2 = 0
        L_0x0189:
            if (r2 >= r1) goto L_0x01b2
            android.view.View r3 = r0.getChildAt(r2)
            android.view.ViewGroup$LayoutParams r4 = r3.getLayoutParams()
            androidx.viewpager.widget.ViewPager$b r4 = (androidx.viewpager.widget.ViewPager.b) r4
            r4.f = r2
            boolean r5 = r4.f1291a
            if (r5 != 0) goto L_0x01af
            float r5 = r4.f1293c
            int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r5 != 0) goto L_0x01af
            androidx.viewpager.widget.ViewPager$a r3 = r0.b((android.view.View) r3)
            if (r3 == 0) goto L_0x01af
            float r5 = r3.f1290d
            r4.f1293c = r5
            int r3 = r3.f1288b
            r4.e = r3
        L_0x01af:
            int r2 = r2 + 1
            goto L_0x0189
        L_0x01b2:
            r17.j()
            boolean r1 = r17.hasFocus()
            if (r1 == 0) goto L_0x01f1
            android.view.View r1 = r17.findFocus()
            if (r1 == 0) goto L_0x01c6
            androidx.viewpager.widget.ViewPager$a r3 = r0.a((android.view.View) r1)
            goto L_0x01c7
        L_0x01c6:
            r3 = 0
        L_0x01c7:
            if (r3 == 0) goto L_0x01cf
            int r1 = r3.f1288b
            int r2 = r0.j
            if (r1 == r2) goto L_0x01f1
        L_0x01cf:
            r1 = 0
        L_0x01d0:
            int r2 = r17.getChildCount()
            if (r1 >= r2) goto L_0x01f1
            android.view.View r2 = r0.getChildAt(r1)
            androidx.viewpager.widget.ViewPager$a r3 = r0.b((android.view.View) r2)
            if (r3 == 0) goto L_0x01ee
            int r3 = r3.f1288b
            int r4 = r0.j
            if (r3 != r4) goto L_0x01ee
            r3 = 2
            boolean r2 = r2.requestFocus(r3)
            if (r2 == 0) goto L_0x01ee
            goto L_0x01f1
        L_0x01ee:
            int r1 = r1 + 1
            goto L_0x01d0
        L_0x01f1:
            return
        L_0x01f2:
            android.content.res.Resources r1 = r17.getResources()     // Catch:{ NotFoundException -> 0x01ff }
            int r2 = r17.getId()     // Catch:{ NotFoundException -> 0x01ff }
            java.lang.String r1 = r1.getResourceName(r2)     // Catch:{ NotFoundException -> 0x01ff }
            goto L_0x0207
        L_0x01ff:
            int r1 = r17.getId()
            java.lang.String r1 = java.lang.Integer.toHexString(r1)
        L_0x0207:
            java.lang.IllegalStateException r2 = new java.lang.IllegalStateException
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: "
            r3.append(r4)
            int r4 = r0.e
            r3.append(r4)
            java.lang.String r4 = ", found: "
            r3.append(r4)
            r3.append(r6)
            java.lang.String r4 = " Pager id: "
            r3.append(r4)
            r3.append(r1)
            java.lang.String r1 = " Pager class: "
            r3.append(r1)
            java.lang.Class<androidx.viewpager.widget.ViewPager> r1 = androidx.viewpager.widget.ViewPager.class
            r3.append(r1)
            java.lang.String r1 = " Problematic adapter: "
            r3.append(r1)
            androidx.viewpager.widget.a r1 = r0.i
            java.lang.Class r1 = r1.getClass()
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            r2.<init>(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.viewpager.widget.ViewPager.c(int):void");
    }

    /* access modifiers changed from: package-private */
    public boolean c() {
        int i2 = this.j;
        if (i2 <= 0) {
            return false;
        }
        a(i2 - 1, true);
        return true;
    }

    public boolean canScrollHorizontally(int i2) {
        if (this.i == null) {
            return false;
        }
        int clientWidth = getClientWidth();
        int scrollX = getScrollX();
        return i2 < 0 ? scrollX > ((int) (((float) clientWidth) * this.u)) : i2 > 0 && scrollX < ((int) (((float) clientWidth) * this.v));
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof b) && super.checkLayoutParams(layoutParams);
    }

    public void computeScroll() {
        this.o = true;
        if (this.n.isFinished() || !this.n.computeScrollOffset()) {
            a(true);
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int currX = this.n.getCurrX();
        int currY = this.n.getCurrY();
        if (!(scrollX == currX && scrollY == currY)) {
            scrollTo(currX, currY);
            if (!f(currX)) {
                this.n.abortAnimation();
                scrollTo(0, currY);
            }
        }
        ViewCompat.u(this);
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        a aVar = this.i;
        if (aVar == null || this.j >= aVar.a() - 1) {
            return false;
        }
        a(this.j + 1, true);
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || a(keyEvent);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        a b2;
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() == 0 && (b2 = b(childAt)) != null && b2.f1288b == this.j && childAt.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas) {
        a aVar;
        super.draw(canvas);
        int overScrollMode = getOverScrollMode();
        boolean z2 = false;
        if (overScrollMode == 0 || (overScrollMode == 1 && (aVar = this.i) != null && aVar.a() > 1)) {
            if (!this.S.isFinished()) {
                int save = canvas.save();
                int height = (getHeight() - getPaddingTop()) - getPaddingBottom();
                int width = getWidth();
                canvas.rotate(270.0f);
                canvas.translate((float) ((-height) + getPaddingTop()), this.u * ((float) width));
                this.S.setSize(height, width);
                z2 = false | this.S.draw(canvas);
                canvas.restoreToCount(save);
            }
            if (!this.T.isFinished()) {
                int save2 = canvas.save();
                int width2 = getWidth();
                int height2 = (getHeight() - getPaddingTop()) - getPaddingBottom();
                canvas.rotate(90.0f);
                canvas.translate((float) (-getPaddingTop()), (-(this.v + 1.0f)) * ((float) width2));
                this.T.setSize(height2, width2);
                z2 |= this.T.draw(canvas);
                canvas.restoreToCount(save2);
            }
        } else {
            this.S.finish();
            this.T.finish();
        }
        if (z2) {
            ViewCompat.u(this);
        }
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.r;
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
    }

    /* access modifiers changed from: package-private */
    public void e() {
        c(this.j);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new b();
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new b(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return generateDefaultLayoutParams();
    }

    @Nullable
    public a getAdapter() {
        return this.i;
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int i2, int i3) {
        if (this.ha == 2) {
            i3 = (i2 - 1) - i3;
        }
        return ((b) this.ia.get(i3).getLayoutParams()).f;
    }

    public int getCurrentItem() {
        return this.j;
    }

    public int getOffscreenPageLimit() {
        return this.B;
    }

    public int getPageMargin() {
        return this.q;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.U = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        removeCallbacks(this.ja);
        Scroller scroller = this.n;
        if (scroller != null && !scroller.isFinished()) {
            this.n.abortAnimation();
        }
        super.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f2;
        float f3;
        super.onDraw(canvas);
        if (this.q > 0 && this.r != null && this.f.size() > 0 && this.i != null) {
            int scrollX = getScrollX();
            int width = getWidth();
            float f4 = (float) width;
            float f5 = ((float) this.q) / f4;
            int i2 = 0;
            a aVar = this.f.get(0);
            float f6 = aVar.e;
            int size = this.f.size();
            int i3 = aVar.f1288b;
            int i4 = this.f.get(size - 1).f1288b;
            while (i3 < i4) {
                while (i3 > aVar.f1288b && i2 < size) {
                    i2++;
                    aVar = this.f.get(i2);
                }
                if (i3 == aVar.f1288b) {
                    float f7 = aVar.e;
                    float f8 = aVar.f1290d;
                    f2 = (f7 + f8) * f4;
                    f6 = f7 + f8 + f5;
                } else {
                    float a2 = this.i.a(i3);
                    f2 = (f6 + a2) * f4;
                    f6 += a2 + f5;
                }
                if (((float) this.q) + f2 > ((float) scrollX)) {
                    f3 = f5;
                    this.r.setBounds(Math.round(f2), this.s, Math.round(((float) this.q) + f2), this.t);
                    this.r.draw(canvas);
                } else {
                    Canvas canvas2 = canvas;
                    f3 = f5;
                }
                if (f2 <= ((float) (scrollX + width))) {
                    i3++;
                    f5 = f3;
                } else {
                    return;
                }
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = motionEvent;
        int action = motionEvent.getAction() & 255;
        if (action == 3 || action == 1) {
            i();
            return false;
        }
        if (action != 0) {
            if (this.C) {
                return true;
            }
            if (this.D) {
                return false;
            }
        }
        if (action == 0) {
            float x2 = motionEvent.getX();
            this.J = x2;
            this.H = x2;
            float y2 = motionEvent.getY();
            this.K = y2;
            this.I = y2;
            this.L = motionEvent2.getPointerId(0);
            this.D = false;
            this.o = true;
            this.n.computeScrollOffset();
            if (this.ka != 2 || Math.abs(this.n.getFinalX() - this.n.getCurrX()) <= this.Q) {
                a(false);
                this.C = false;
            } else {
                this.n.abortAnimation();
                this.A = false;
                e();
                this.C = true;
                c(true);
                setScrollState(1);
            }
        } else if (action == 2) {
            int i2 = this.L;
            if (i2 != -1) {
                int findPointerIndex = motionEvent2.findPointerIndex(i2);
                float x3 = motionEvent2.getX(findPointerIndex);
                float f2 = x3 - this.H;
                float abs = Math.abs(f2);
                float y3 = motionEvent2.getY(findPointerIndex);
                float abs2 = Math.abs(y3 - this.K);
                int i3 = (f2 > 0.0f ? 1 : (f2 == 0.0f ? 0 : -1));
                if (i3 != 0 && !a(this.H, f2)) {
                    if (a(this, false, (int) f2, (int) x3, (int) y3)) {
                        this.H = x3;
                        this.I = y3;
                        this.D = true;
                        return false;
                    }
                }
                if (abs > ((float) this.G) && abs * 0.5f > abs2) {
                    this.C = true;
                    c(true);
                    setScrollState(1);
                    this.H = i3 > 0 ? this.J + ((float) this.G) : this.J - ((float) this.G);
                    this.I = y3;
                    setScrollingCacheEnabled(true);
                } else if (abs2 > ((float) this.G)) {
                    this.D = true;
                }
                if (this.C && b(x3)) {
                    ViewCompat.u(this);
                }
            }
        } else if (action == 6) {
            a(motionEvent);
        }
        if (this.M == null) {
            this.M = VelocityTracker.obtain();
        }
        this.M.addMovement(motionEvent2);
        return this.C;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
        boolean z3;
        a b2;
        int i6;
        int i7;
        int childCount = getChildCount();
        int i8 = i4 - i2;
        int i9 = i5 - i3;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int scrollX = getScrollX();
        int i10 = paddingBottom;
        int i11 = 0;
        int i12 = paddingTop;
        int i13 = paddingLeft;
        for (int i14 = 0; i14 < childCount; i14++) {
            View childAt = getChildAt(i14);
            if (childAt.getVisibility() != 8) {
                b bVar = (b) childAt.getLayoutParams();
                if (bVar.f1291a) {
                    int i15 = bVar.f1292b;
                    int i16 = i15 & 7;
                    int i17 = i15 & 112;
                    if (i16 == 1) {
                        i6 = Math.max((i8 - childAt.getMeasuredWidth()) / 2, i13);
                    } else if (i16 == 3) {
                        i6 = i13;
                        i13 = childAt.getMeasuredWidth() + i13;
                    } else if (i16 != 5) {
                        i6 = i13;
                    } else {
                        i6 = (i8 - paddingRight) - childAt.getMeasuredWidth();
                        paddingRight += childAt.getMeasuredWidth();
                    }
                    if (i17 == 16) {
                        i7 = Math.max((i9 - childAt.getMeasuredHeight()) / 2, i12);
                    } else if (i17 == 48) {
                        i7 = i12;
                        i12 = childAt.getMeasuredHeight() + i12;
                    } else if (i17 != 80) {
                        i7 = i12;
                    } else {
                        i7 = (i9 - i10) - childAt.getMeasuredHeight();
                        i10 += childAt.getMeasuredHeight();
                    }
                    int i18 = i6 + scrollX;
                    childAt.layout(i18, i7, childAt.getMeasuredWidth() + i18, i7 + childAt.getMeasuredHeight());
                    i11++;
                }
            }
        }
        int i19 = (i8 - i13) - paddingRight;
        for (int i20 = 0; i20 < childCount; i20++) {
            View childAt2 = getChildAt(i20);
            if (childAt2.getVisibility() != 8) {
                b bVar2 = (b) childAt2.getLayoutParams();
                if (!bVar2.f1291a && (b2 = b(childAt2)) != null) {
                    float f2 = (float) i19;
                    int i21 = ((int) (b2.e * f2)) + i13;
                    if (bVar2.f1294d) {
                        bVar2.f1294d = false;
                        childAt2.measure(View.MeasureSpec.makeMeasureSpec((int) (f2 * bVar2.f1293c), 1073741824), View.MeasureSpec.makeMeasureSpec((i9 - i12) - i10, 1073741824));
                    }
                    childAt2.layout(i21, i12, childAt2.getMeasuredWidth() + i21, childAt2.getMeasuredHeight() + i12);
                }
            }
        }
        this.s = i12;
        this.t = i9 - i10;
        this.aa = i11;
        if (this.U) {
            z3 = false;
            a(this.j, false, 0, false);
        } else {
            z3 = false;
        }
        this.U = z3;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        b bVar;
        b bVar2;
        int i4;
        setMeasuredDimension(ViewGroup.getDefaultSize(0, i2), ViewGroup.getDefaultSize(0, i3));
        int measuredWidth = getMeasuredWidth();
        this.F = Math.min(measuredWidth / 10, this.E);
        int paddingLeft = (measuredWidth - getPaddingLeft()) - getPaddingRight();
        int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        int childCount = getChildCount();
        int i5 = measuredHeight;
        int i6 = paddingLeft;
        int i7 = 0;
        while (true) {
            boolean z2 = true;
            int i8 = 1073741824;
            if (i7 >= childCount) {
                break;
            }
            View childAt = getChildAt(i7);
            if (!(childAt.getVisibility() == 8 || (bVar2 = (b) childAt.getLayoutParams()) == null || !bVar2.f1291a)) {
                int i9 = bVar2.f1292b;
                int i10 = i9 & 7;
                int i11 = i9 & 112;
                boolean z3 = i11 == 48 || i11 == 80;
                if (!(i10 == 3 || i10 == 5)) {
                    z2 = false;
                }
                int i12 = Integer.MIN_VALUE;
                if (z3) {
                    i4 = Integer.MIN_VALUE;
                    i12 = 1073741824;
                } else {
                    i4 = z2 ? 1073741824 : Integer.MIN_VALUE;
                }
                int i13 = bVar2.width;
                if (i13 != -2) {
                    if (i13 == -1) {
                        i13 = i6;
                    }
                    i12 = 1073741824;
                } else {
                    i13 = i6;
                }
                int i14 = bVar2.height;
                if (i14 == -2) {
                    i14 = i5;
                    i8 = i4;
                } else if (i14 == -1) {
                    i14 = i5;
                }
                childAt.measure(View.MeasureSpec.makeMeasureSpec(i13, i12), View.MeasureSpec.makeMeasureSpec(i14, i8));
                if (z3) {
                    i5 -= childAt.getMeasuredHeight();
                } else if (z2) {
                    i6 -= childAt.getMeasuredWidth();
                }
            }
            i7++;
        }
        this.w = View.MeasureSpec.makeMeasureSpec(i6, 1073741824);
        this.x = View.MeasureSpec.makeMeasureSpec(i5, 1073741824);
        this.y = true;
        e();
        this.y = false;
        int childCount2 = getChildCount();
        for (int i15 = 0; i15 < childCount2; i15++) {
            View childAt2 = getChildAt(i15);
            if (childAt2.getVisibility() != 8 && ((bVar = (b) childAt2.getLayoutParams()) == null || !bVar.f1291a)) {
                childAt2.measure(View.MeasureSpec.makeMeasureSpec((int) (((float) i6) * bVar.f1293c), 1073741824), this.x);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i2, Rect rect) {
        int i3;
        int i4;
        a b2;
        int childCount = getChildCount();
        int i5 = -1;
        if ((i2 & 2) != 0) {
            i5 = childCount;
            i4 = 0;
            i3 = 1;
        } else {
            i4 = childCount - 1;
            i3 = -1;
        }
        while (i4 != i5) {
            View childAt = getChildAt(i4);
            if (childAt.getVisibility() == 0 && (b2 = b(childAt)) != null && b2.f1288b == this.j && childAt.requestFocus(i2, rect)) {
                return true;
            }
            i4 += i3;
        }
        return false;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        a aVar = this.i;
        if (aVar != null) {
            aVar.a(savedState.adapterState, savedState.loader);
            a(savedState.position, false, true);
            return;
        }
        this.k = savedState.position;
        this.l = savedState.adapterState;
        this.m = savedState.loader;
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.j;
        a aVar = this.i;
        if (aVar != null) {
            savedState.adapterState = aVar.b();
        }
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        if (i2 != i4) {
            int i6 = this.q;
            a(i2, i4, i6, i6);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x0151  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.R
            r1 = 1
            if (r0 == 0) goto L_0x0006
            return r1
        L_0x0006:
            int r0 = r8.getAction()
            r2 = 0
            if (r0 != 0) goto L_0x0014
            int r0 = r8.getEdgeFlags()
            if (r0 == 0) goto L_0x0014
            return r2
        L_0x0014:
            androidx.viewpager.widget.a r0 = r7.i
            if (r0 == 0) goto L_0x0155
            int r0 = r0.a()
            if (r0 != 0) goto L_0x0020
            goto L_0x0155
        L_0x0020:
            android.view.VelocityTracker r0 = r7.M
            if (r0 != 0) goto L_0x002a
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r7.M = r0
        L_0x002a:
            android.view.VelocityTracker r0 = r7.M
            r0.addMovement(r8)
            int r0 = r8.getAction()
            r0 = r0 & 255(0xff, float:3.57E-43)
            if (r0 == 0) goto L_0x012f
            if (r0 == r1) goto L_0x00e2
            r3 = 2
            if (r0 == r3) goto L_0x0073
            r3 = 3
            if (r0 == r3) goto L_0x0068
            r3 = 5
            if (r0 == r3) goto L_0x0058
            r3 = 6
            if (r0 == r3) goto L_0x0047
            goto L_0x014f
        L_0x0047:
            r7.a((android.view.MotionEvent) r8)
            int r0 = r7.L
            int r0 = r8.findPointerIndex(r0)
            float r8 = r8.getX(r0)
            r7.H = r8
            goto L_0x014f
        L_0x0058:
            int r0 = r8.getActionIndex()
            float r3 = r8.getX(r0)
            r7.H = r3
            int r8 = r8.getPointerId(r0)
            goto L_0x014d
        L_0x0068:
            boolean r8 = r7.C
            if (r8 == 0) goto L_0x014f
            int r8 = r7.j
            r7.a((int) r8, (boolean) r1, (int) r2, (boolean) r2)
            goto L_0x012a
        L_0x0073:
            boolean r0 = r7.C
            if (r0 != 0) goto L_0x00ce
            int r0 = r7.L
            int r0 = r8.findPointerIndex(r0)
            r3 = -1
            if (r0 != r3) goto L_0x0082
            goto L_0x012a
        L_0x0082:
            float r3 = r8.getX(r0)
            float r4 = r7.H
            float r4 = r3 - r4
            float r4 = java.lang.Math.abs(r4)
            float r0 = r8.getY(r0)
            float r5 = r7.I
            float r5 = r0 - r5
            float r5 = java.lang.Math.abs(r5)
            int r6 = r7.G
            float r6 = (float) r6
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x00ce
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x00ce
            r7.C = r1
            r7.c((boolean) r1)
            float r4 = r7.J
            float r3 = r3 - r4
            r5 = 0
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x00b7
            int r3 = r7.G
            float r3 = (float) r3
            float r4 = r4 + r3
            goto L_0x00bb
        L_0x00b7:
            int r3 = r7.G
            float r3 = (float) r3
            float r4 = r4 - r3
        L_0x00bb:
            r7.H = r4
            r7.I = r0
            r7.setScrollState(r1)
            r7.setScrollingCacheEnabled(r1)
            android.view.ViewParent r0 = r7.getParent()
            if (r0 == 0) goto L_0x00ce
            r0.requestDisallowInterceptTouchEvent(r1)
        L_0x00ce:
            boolean r0 = r7.C
            if (r0 == 0) goto L_0x014f
            int r0 = r7.L
            int r0 = r8.findPointerIndex(r0)
            float r8 = r8.getX(r0)
            boolean r8 = r7.b((float) r8)
            r2 = r2 | r8
            goto L_0x014f
        L_0x00e2:
            boolean r0 = r7.C
            if (r0 == 0) goto L_0x014f
            android.view.VelocityTracker r0 = r7.M
            r2 = 1000(0x3e8, float:1.401E-42)
            int r3 = r7.O
            float r3 = (float) r3
            r0.computeCurrentVelocity(r2, r3)
            int r2 = r7.L
            float r0 = r0.getXVelocity(r2)
            int r0 = (int) r0
            r7.A = r1
            int r2 = r7.getClientWidth()
            int r3 = r7.getScrollX()
            androidx.viewpager.widget.ViewPager$a r4 = r7.g()
            int r5 = r7.q
            float r5 = (float) r5
            float r2 = (float) r2
            float r5 = r5 / r2
            int r6 = r4.f1288b
            float r3 = (float) r3
            float r3 = r3 / r2
            float r2 = r4.e
            float r3 = r3 - r2
            float r2 = r4.f1290d
            float r2 = r2 + r5
            float r3 = r3 / r2
            int r2 = r7.L
            int r2 = r8.findPointerIndex(r2)
            float r8 = r8.getX(r2)
            float r2 = r7.J
            float r8 = r8 - r2
            int r8 = (int) r8
            int r8 = r7.a((int) r6, (float) r3, (int) r0, (int) r8)
            r7.a((int) r8, (boolean) r1, (boolean) r1, (int) r0)
        L_0x012a:
            boolean r2 = r7.i()
            goto L_0x014f
        L_0x012f:
            android.widget.Scroller r0 = r7.n
            r0.abortAnimation()
            r7.A = r2
            r7.e()
            float r0 = r8.getX()
            r7.J = r0
            r7.H = r0
            float r0 = r8.getY()
            r7.K = r0
            r7.I = r0
            int r8 = r8.getPointerId(r2)
        L_0x014d:
            r7.L = r8
        L_0x014f:
            if (r2 == 0) goto L_0x0154
            androidx.core.view.ViewCompat.u(r7)
        L_0x0154:
            return r1
        L_0x0155:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.viewpager.widget.ViewPager.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void removeView(View view) {
        if (this.y) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    public void setAdapter(@Nullable a aVar) {
        a aVar2 = this.i;
        if (aVar2 != null) {
            aVar2.a((DataSetObserver) null);
            this.i.b((ViewGroup) this);
            for (int i2 = 0; i2 < this.f.size(); i2++) {
                a aVar3 = this.f.get(i2);
                this.i.a((ViewGroup) this, aVar3.f1288b, aVar3.f1287a);
            }
            this.i.a((ViewGroup) this);
            this.f.clear();
            h();
            this.j = 0;
            scrollTo(0, 0);
        }
        a aVar4 = this.i;
        this.i = aVar;
        this.e = 0;
        if (this.i != null) {
            if (this.p == null) {
                this.p = new g();
            }
            this.i.a((DataSetObserver) this.p);
            this.A = false;
            boolean z2 = this.U;
            this.U = true;
            this.e = this.i.a();
            if (this.k >= 0) {
                this.i.a(this.l, this.m);
                a(this.k, false, true);
                this.k = -1;
                this.l = null;
                this.m = null;
            } else if (!z2) {
                e();
            } else {
                requestLayout();
            }
        }
        List<d> list = this.ea;
        if (list != null && !list.isEmpty()) {
            int size = this.ea.size();
            for (int i3 = 0; i3 < size; i3++) {
                this.ea.get(i3).a(this, aVar4, aVar);
            }
        }
    }

    public void setCurrentItem(int i2) {
        this.A = false;
        a(i2, !this.U, false);
    }

    public void setOffscreenPageLimit(int i2) {
        if (i2 < 1) {
            Log.w("ViewPager", "Requested offscreen page limit " + i2 + " too small; defaulting to " + 1);
            i2 = 1;
        }
        if (i2 != this.B) {
            this.B = i2;
            e();
        }
    }

    @Deprecated
    public void setOnPageChangeListener(e eVar) {
        this.ca = eVar;
    }

    public void setPageMargin(int i2) {
        int i3 = this.q;
        this.q = i2;
        int width = getWidth();
        a(width, width, i2, i3);
        requestLayout();
    }

    public void setPageMarginDrawable(@DrawableRes int i2) {
        setPageMarginDrawable(androidx.core.content.a.c(getContext(), i2));
    }

    public void setPageMarginDrawable(@Nullable Drawable drawable) {
        this.r = drawable;
        if (drawable != null) {
            refreshDrawableState();
        }
        setWillNotDraw(drawable == null);
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void setScrollState(int i2) {
        if (this.ka != i2) {
            this.ka = i2;
            if (this.fa != null) {
                b(i2 != 0);
            }
            e(i2);
        }
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.r;
    }
}
