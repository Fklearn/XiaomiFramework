package androidx.recyclerview.widget;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.C0123a;
import androidx.core.view.C0130h;
import androidx.core.view.ViewCompat;
import androidx.core.view.a.c;
import androidx.core.view.y;
import androidx.customview.view.AbsSavedState;
import androidx.recyclerview.widget.C0178t;
import androidx.recyclerview.widget.L;
import androidx.recyclerview.widget.V;
import androidx.recyclerview.widget.ViewBoundsCheck;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerView extends ViewGroup implements androidx.core.view.s, androidx.core.view.i, androidx.core.view.j {

    /* renamed from: a  reason: collision with root package name */
    private static final int[] f1110a = {16843830};

    /* renamed from: b  reason: collision with root package name */
    static final boolean f1111b;

    /* renamed from: c  reason: collision with root package name */
    static final boolean f1112c = (Build.VERSION.SDK_INT >= 23);

    /* renamed from: d  reason: collision with root package name */
    static final boolean f1113d = (Build.VERSION.SDK_INT >= 16);
    static final boolean e = (Build.VERSION.SDK_INT >= 21);
    private static final boolean f = (Build.VERSION.SDK_INT <= 15);
    private static final boolean g = (Build.VERSION.SDK_INT <= 15);
    private static final Class<?>[] h;
    static final Interpolator i = new E();
    boolean A;
    private androidx.core.view.l Aa;
    boolean B;
    private final int[] Ba;
    boolean C;
    private final int[] Ca;
    @VisibleForTesting
    boolean D;
    final int[] Da;
    private int E;
    @VisibleForTesting
    final List<u> Ea;
    boolean F;
    private Runnable Fa;
    boolean G;
    private final V.b Ga;
    private boolean H;
    private int I;
    boolean J;
    private final AccessibilityManager K;
    private List<i> L;
    boolean M;
    boolean N;
    private int O;
    private int P;
    @NonNull
    private EdgeEffectFactory Q;
    private EdgeEffect R;
    private EdgeEffect S;
    private EdgeEffect T;
    private EdgeEffect U;
    ItemAnimator V;
    private int W;
    private int aa;
    private VelocityTracker ba;
    private int ca;
    private int da;
    private int ea;
    private int fa;
    private int ga;
    private j ha;
    private final int ia;
    private final p j;
    private final int ja;
    final n k;
    private float ka;
    private SavedState l;
    private float la;
    C0160a m;
    private boolean ma;
    C0163d n;
    final t na;
    final V o;
    C0178t oa;
    boolean p;
    C0178t.a pa;
    final Runnable q;
    final r qa;
    final Rect r;
    private l ra;
    private final Rect s;
    private List<l> sa;
    final RectF t;
    boolean ta;
    a u;
    boolean ua;
    @VisibleForTesting
    g v;
    private ItemAnimator.b va;
    o w;
    boolean wa;
    final ArrayList<f> x;
    L xa;
    private final ArrayList<k> y;
    private d ya;
    private k z;
    private final int[] za;

    public static class EdgeEffectFactory {

        @Retention(RetentionPolicy.SOURCE)
        public @interface EdgeDirection {
        }

        /* access modifiers changed from: protected */
        @NonNull
        public EdgeEffect a(@NonNull RecyclerView recyclerView, int i) {
            return new EdgeEffect(recyclerView.getContext());
        }
    }

    public static abstract class ItemAnimator {

        /* renamed from: a  reason: collision with root package name */
        private b f1114a = null;

        /* renamed from: b  reason: collision with root package name */
        private ArrayList<a> f1115b = new ArrayList<>();

        /* renamed from: c  reason: collision with root package name */
        private long f1116c = 120;

        /* renamed from: d  reason: collision with root package name */
        private long f1117d = 120;
        private long e = 250;
        private long f = 250;

        @Retention(RetentionPolicy.SOURCE)
        public @interface AdapterChanges {
        }

        public interface a {
            void a();
        }

        interface b {
            void a(@NonNull u uVar);
        }

        public static class c {

            /* renamed from: a  reason: collision with root package name */
            public int f1118a;

            /* renamed from: b  reason: collision with root package name */
            public int f1119b;

            /* renamed from: c  reason: collision with root package name */
            public int f1120c;

            /* renamed from: d  reason: collision with root package name */
            public int f1121d;

            @NonNull
            public c a(@NonNull u uVar) {
                a(uVar, 0);
                return this;
            }

            @NonNull
            public c a(@NonNull u uVar, int i) {
                View view = uVar.itemView;
                this.f1118a = view.getLeft();
                this.f1119b = view.getTop();
                this.f1120c = view.getRight();
                this.f1121d = view.getBottom();
                return this;
            }
        }

        static int a(u uVar) {
            int i = uVar.mFlags & 14;
            if (uVar.isInvalid()) {
                return 4;
            }
            if ((i & 4) != 0) {
                return i;
            }
            int oldPosition = uVar.getOldPosition();
            int adapterPosition = uVar.getAdapterPosition();
            return (oldPosition == -1 || adapterPosition == -1 || oldPosition == adapterPosition) ? i : i | 2048;
        }

        @NonNull
        public c a(@NonNull r rVar, @NonNull u uVar) {
            c h = h();
            h.a(uVar);
            return h;
        }

        @NonNull
        public c a(@NonNull r rVar, @NonNull u uVar, int i, @NonNull List<Object> list) {
            c h = h();
            h.a(uVar);
            return h;
        }

        public final void a() {
            int size = this.f1115b.size();
            for (int i = 0; i < size; i++) {
                this.f1115b.get(i).a();
            }
            this.f1115b.clear();
        }

        /* access modifiers changed from: package-private */
        public void a(b bVar) {
            this.f1114a = bVar;
        }

        public abstract boolean a(@NonNull u uVar, @Nullable c cVar, @NonNull c cVar2);

        public abstract boolean a(@NonNull u uVar, @NonNull u uVar2, @NonNull c cVar, @NonNull c cVar2);

        public boolean a(@NonNull u uVar, @NonNull List<Object> list) {
            return b(uVar);
        }

        public abstract void b();

        public abstract boolean b(@NonNull u uVar);

        public abstract boolean b(@NonNull u uVar, @NonNull c cVar, @Nullable c cVar2);

        public long c() {
            return this.f1116c;
        }

        public final void c(@NonNull u uVar) {
            e(uVar);
            b bVar = this.f1114a;
            if (bVar != null) {
                bVar.a(uVar);
            }
        }

        public abstract boolean c(@NonNull u uVar, @NonNull c cVar, @NonNull c cVar2);

        public long d() {
            return this.f;
        }

        public abstract void d(@NonNull u uVar);

        public long e() {
            return this.e;
        }

        public void e(@NonNull u uVar) {
        }

        public long f() {
            return this.f1117d;
        }

        public abstract boolean g();

        @NonNull
        public c h() {
            return new c();
        }

        public abstract void i();
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }

    @RestrictTo({RestrictTo.a.LIBRARY})
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new K();
        Parcelable mLayoutState;

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.mLayoutState = parcel.readParcelable(classLoader == null ? g.class.getClassLoader() : classLoader);
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        /* access modifiers changed from: package-private */
        public void copyFrom(SavedState savedState) {
            this.mLayoutState = savedState.mLayoutState;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeParcelable(this.mLayoutState, 0);
        }
    }

    public static abstract class a<VH extends u> {
        private boolean mHasStableIds = false;
        private final b mObservable = new b();

        public final void bindViewHolder(@NonNull VH vh, int i) {
            vh.mPosition = i;
            if (hasStableIds()) {
                vh.mItemId = getItemId(i);
            }
            vh.setFlags(1, 519);
            a.d.c.a.a("RV OnBindView");
            onBindViewHolder(vh, i, vh.getUnmodifiedPayloads());
            vh.clearPayload();
            ViewGroup.LayoutParams layoutParams = vh.itemView.getLayoutParams();
            if (layoutParams instanceof h) {
                ((h) layoutParams).f1133c = true;
            }
            a.d.c.a.a();
        }

        @NonNull
        public final VH createViewHolder(@NonNull ViewGroup viewGroup, int i) {
            try {
                a.d.c.a.a("RV CreateView");
                VH onCreateViewHolder = onCreateViewHolder(viewGroup, i);
                if (onCreateViewHolder.itemView.getParent() == null) {
                    onCreateViewHolder.mItemViewType = i;
                    return onCreateViewHolder;
                }
                throw new IllegalStateException("ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)");
            } finally {
                a.d.c.a.a();
            }
        }

        public abstract int getItemCount();

        public long getItemId(int i) {
            return -1;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public final boolean hasObservers() {
            return this.mObservable.a();
        }

        public final boolean hasStableIds() {
            return this.mHasStableIds;
        }

        public final void notifyDataSetChanged() {
            this.mObservable.b();
        }

        public final void notifyItemChanged(int i) {
            this.mObservable.b(i, 1);
        }

        public final void notifyItemChanged(int i, @Nullable Object obj) {
            this.mObservable.a(i, 1, obj);
        }

        public final void notifyItemInserted(int i) {
            this.mObservable.c(i, 1);
        }

        public final void notifyItemMoved(int i, int i2) {
            this.mObservable.a(i, i2);
        }

        public final void notifyItemRangeChanged(int i, int i2) {
            this.mObservable.b(i, i2);
        }

        public final void notifyItemRangeChanged(int i, int i2, @Nullable Object obj) {
            this.mObservable.a(i, i2, obj);
        }

        public final void notifyItemRangeInserted(int i, int i2) {
            this.mObservable.c(i, i2);
        }

        public final void notifyItemRangeRemoved(int i, int i2) {
            this.mObservable.d(i, i2);
        }

        public final void notifyItemRemoved(int i) {
            this.mObservable.d(i, 1);
        }

        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        }

        public abstract void onBindViewHolder(@NonNull VH vh, int i);

        public void onBindViewHolder(@NonNull VH vh, int i, @NonNull List<Object> list) {
            onBindViewHolder(vh, i);
        }

        @NonNull
        public abstract VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i);

        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        }

        public boolean onFailedToRecycleView(@NonNull VH vh) {
            return false;
        }

        public void onViewAttachedToWindow(@NonNull VH vh) {
        }

        public void onViewDetachedFromWindow(@NonNull VH vh) {
        }

        public void onViewRecycled(@NonNull VH vh) {
        }

        public void registerAdapterDataObserver(@NonNull c cVar) {
            this.mObservable.registerObserver(cVar);
        }

        public void setHasStableIds(boolean z) {
            if (!hasObservers()) {
                this.mHasStableIds = z;
                return;
            }
            throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
        }

        public void unregisterAdapterDataObserver(@NonNull c cVar) {
            this.mObservable.unregisterObserver(cVar);
        }
    }

    static class b extends Observable<c> {
        b() {
        }

        public void a(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((c) this.mObservers.get(size)).a(i, i2, 1);
            }
        }

        public void a(int i, int i2, @Nullable Object obj) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((c) this.mObservers.get(size)).a(i, i2, obj);
            }
        }

        public boolean a() {
            return !this.mObservers.isEmpty();
        }

        public void b() {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((c) this.mObservers.get(size)).a();
            }
        }

        public void b(int i, int i2) {
            a(i, i2, (Object) null);
        }

        public void c(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((c) this.mObservers.get(size)).b(i, i2);
            }
        }

        public void d(int i, int i2) {
            for (int size = this.mObservers.size() - 1; size >= 0; size--) {
                ((c) this.mObservers.get(size)).c(i, i2);
            }
        }
    }

    public static abstract class c {
        public void a() {
        }

        public void a(int i, int i2) {
        }

        public void a(int i, int i2, int i3) {
        }

        public void a(int i, int i2, @Nullable Object obj) {
            a(i, i2);
        }

        public void b(int i, int i2) {
        }

        public void c(int i, int i2) {
        }
    }

    public interface d {
        int a(int i, int i2);
    }

    private class e implements ItemAnimator.b {
        e() {
        }

        public void a(u uVar) {
            uVar.setIsRecyclable(true);
            if (uVar.mShadowedHolder != null && uVar.mShadowingHolder == null) {
                uVar.mShadowedHolder = null;
            }
            uVar.mShadowingHolder = null;
            if (!uVar.shouldBeKeptAsChild() && !RecyclerView.this.l(uVar.itemView) && uVar.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(uVar.itemView, false);
            }
        }
    }

    public static abstract class f {
        @Deprecated
        public void a(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView) {
        }

        public void a(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull r rVar) {
            a(canvas, recyclerView);
        }

        @Deprecated
        public void a(@NonNull Rect rect, int i, @NonNull RecyclerView recyclerView) {
            rect.set(0, 0, 0, 0);
        }

        public void a(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull r rVar) {
            a(rect, ((h) view.getLayoutParams()).a(), recyclerView);
        }

        @Deprecated
        public void b(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView) {
        }

        public void b(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull r rVar) {
            b(canvas, recyclerView);
        }
    }

    public static abstract class g {

        /* renamed from: a  reason: collision with root package name */
        C0163d f1123a;

        /* renamed from: b  reason: collision with root package name */
        RecyclerView f1124b;

        /* renamed from: c  reason: collision with root package name */
        private final ViewBoundsCheck.b f1125c = new I(this);

        /* renamed from: d  reason: collision with root package name */
        private final ViewBoundsCheck.b f1126d = new J(this);
        ViewBoundsCheck e = new ViewBoundsCheck(this.f1125c);
        ViewBoundsCheck f = new ViewBoundsCheck(this.f1126d);
        @Nullable
        q g;
        boolean h = false;
        boolean i = false;
        boolean j = false;
        private boolean k = true;
        private boolean l = true;
        int m;
        boolean n;
        private int o;
        private int p;
        private int q;
        private int r;

        public interface a {
            void a(int i, int i2);
        }

        public static class b {

            /* renamed from: a  reason: collision with root package name */
            public int f1127a;

            /* renamed from: b  reason: collision with root package name */
            public int f1128b;

            /* renamed from: c  reason: collision with root package name */
            public boolean f1129c;

            /* renamed from: d  reason: collision with root package name */
            public boolean f1130d;
        }

        public static int a(int i2, int i3, int i4) {
            int mode = View.MeasureSpec.getMode(i2);
            int size = View.MeasureSpec.getSize(i2);
            return mode != Integer.MIN_VALUE ? mode != 1073741824 ? Math.max(i3, i4) : size : Math.min(size, Math.max(i3, i4));
        }

        public static int a(int i2, int i3, int i4, int i5, boolean z) {
            int i6;
            int i7 = i2 - i4;
            int i8 = 0;
            int max = Math.max(0, i7);
            if (z) {
                if (i5 < 0) {
                    if (i5 == -1) {
                        if (i3 == Integer.MIN_VALUE || (i3 != 0 && i3 == 1073741824)) {
                            i6 = max;
                        } else {
                            i3 = 0;
                            i6 = 0;
                        }
                        i8 = i3;
                        max = i6;
                        return View.MeasureSpec.makeMeasureSpec(max, i8);
                    }
                    max = 0;
                    return View.MeasureSpec.makeMeasureSpec(max, i8);
                }
            } else if (i5 < 0) {
                if (i5 == -1) {
                    i8 = i3;
                } else {
                    if (i5 == -2) {
                        if (i3 == Integer.MIN_VALUE || i3 == 1073741824) {
                            i8 = Integer.MIN_VALUE;
                        }
                    }
                    max = 0;
                }
                return View.MeasureSpec.makeMeasureSpec(max, i8);
            }
            max = i5;
            i8 = 1073741824;
            return View.MeasureSpec.makeMeasureSpec(max, i8);
        }

        public static b a(@NonNull Context context, @Nullable AttributeSet attributeSet, int i2, int i3) {
            b bVar = new b();
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, a.i.c.RecyclerView, i2, i3);
            bVar.f1127a = obtainStyledAttributes.getInt(a.i.c.RecyclerView_android_orientation, 1);
            bVar.f1128b = obtainStyledAttributes.getInt(a.i.c.RecyclerView_spanCount, 1);
            bVar.f1129c = obtainStyledAttributes.getBoolean(a.i.c.RecyclerView_reverseLayout, false);
            bVar.f1130d = obtainStyledAttributes.getBoolean(a.i.c.RecyclerView_stackFromEnd, false);
            obtainStyledAttributes.recycle();
            return bVar;
        }

        private void a(int i2, @NonNull View view) {
            this.f1123a.a(i2);
        }

        private void a(View view, int i2, boolean z) {
            u h2 = RecyclerView.h(view);
            if (z || h2.isRemoved()) {
                this.f1124b.o.a(h2);
            } else {
                this.f1124b.o.g(h2);
            }
            h hVar = (h) view.getLayoutParams();
            if (h2.wasReturnedFromScrap() || h2.isScrap()) {
                if (h2.isScrap()) {
                    h2.unScrap();
                } else {
                    h2.clearReturnedFromScrapFlag();
                }
                this.f1123a.a(view, i2, view.getLayoutParams(), false);
            } else if (view.getParent() == this.f1124b) {
                int b2 = this.f1123a.b(view);
                if (i2 == -1) {
                    i2 = this.f1123a.a();
                }
                if (b2 == -1) {
                    throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.f1124b.indexOfChild(view) + this.f1124b.i());
                } else if (b2 != i2) {
                    this.f1124b.v.a(b2, i2);
                }
            } else {
                this.f1123a.a(view, i2, false);
                hVar.f1133c = true;
                q qVar = this.g;
                if (qVar != null && qVar.c()) {
                    this.g.a(view);
                }
            }
            if (hVar.f1134d) {
                h2.itemView.invalidate();
                hVar.f1134d = false;
            }
        }

        private void a(n nVar, int i2, View view) {
            u h2 = RecyclerView.h(view);
            if (!h2.shouldIgnore()) {
                if (!h2.isInvalid() || h2.isRemoved() || this.f1124b.u.hasStableIds()) {
                    a(i2);
                    nVar.c(view);
                    this.f1124b.o.d(h2);
                    return;
                }
                g(i2);
                nVar.b(h2);
            }
        }

        private static boolean b(int i2, int i3, int i4) {
            int mode = View.MeasureSpec.getMode(i3);
            int size = View.MeasureSpec.getSize(i3);
            if (i4 > 0 && i2 != i4) {
                return false;
            }
            if (mode == Integer.MIN_VALUE) {
                return size >= i2;
            }
            if (mode != 0) {
                return mode == 1073741824 && size == i2;
            }
            return true;
        }

        private int[] c(View view, Rect rect) {
            int[] iArr = new int[2];
            int o2 = o();
            int q2 = q();
            int r2 = r() - p();
            int h2 = h() - n();
            int left = (view.getLeft() + rect.left) - view.getScrollX();
            int top = (view.getTop() + rect.top) - view.getScrollY();
            int width = rect.width() + left;
            int height = rect.height() + top;
            int i2 = left - o2;
            int min = Math.min(0, i2);
            int i3 = top - q2;
            int min2 = Math.min(0, i3);
            int i4 = width - r2;
            int max = Math.max(0, i4);
            int max2 = Math.max(0, height - h2);
            if (k() != 1) {
                if (min == 0) {
                    min = Math.min(i2, max);
                }
                max = min;
            } else if (max == 0) {
                max = Math.max(min, i4);
            }
            if (min2 == 0) {
                min2 = Math.min(i3, max2);
            }
            iArr[0] = max;
            iArr[1] = min2;
            return iArr;
        }

        private boolean d(RecyclerView recyclerView, int i2, int i3) {
            View focusedChild = recyclerView.getFocusedChild();
            if (focusedChild == null) {
                return false;
            }
            int o2 = o();
            int q2 = q();
            int r2 = r() - p();
            int h2 = h() - n();
            Rect rect = this.f1124b.r;
            b(focusedChild, rect);
            return rect.left - i2 < r2 && rect.right - i2 > o2 && rect.top - i3 < h2 && rect.bottom - i3 > q2;
        }

        public void A() {
            this.h = true;
        }

        /* access modifiers changed from: package-private */
        public boolean B() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void C() {
            q qVar = this.g;
            if (qVar != null) {
                qVar.d();
            }
        }

        public boolean D() {
            return false;
        }

        public int a(int i2, n nVar, r rVar) {
            return 0;
        }

        public int a(@NonNull n nVar, @NonNull r rVar) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView == null || recyclerView.u == null || !a()) {
                return 1;
            }
            return this.f1124b.u.getItemCount();
        }

        public int a(@NonNull r rVar) {
            return 0;
        }

        @Nullable
        public View a(@NonNull View view, int i2, @NonNull n nVar, @NonNull r rVar) {
            return null;
        }

        public h a(Context context, AttributeSet attributeSet) {
            return new h(context, attributeSet);
        }

        public h a(ViewGroup.LayoutParams layoutParams) {
            return layoutParams instanceof h ? new h((h) layoutParams) : layoutParams instanceof ViewGroup.MarginLayoutParams ? new h((ViewGroup.MarginLayoutParams) layoutParams) : new h(layoutParams);
        }

        public void a(int i2) {
            a(i2, c(i2));
        }

        public void a(int i2, int i3) {
            View c2 = c(i2);
            if (c2 != null) {
                a(i2);
                c(c2, i3);
                return;
            }
            throw new IllegalArgumentException("Cannot move a child from non-existing index:" + i2 + this.f1124b.toString());
        }

        public void a(int i2, int i3, r rVar, a aVar) {
        }

        public void a(int i2, a aVar) {
        }

        public void a(int i2, @NonNull n nVar) {
            View c2 = c(i2);
            g(i2);
            nVar.b(c2);
        }

        public void a(Rect rect, int i2, int i3) {
            c(a(i2, rect.width() + o() + p(), m()), a(i3, rect.height() + q() + n(), l()));
        }

        public void a(Parcelable parcelable) {
        }

        public void a(View view) {
            a(view, -1);
        }

        public void a(View view, int i2) {
            a(view, i2, true);
        }

        public void a(@NonNull View view, int i2, int i3) {
            h hVar = (h) view.getLayoutParams();
            Rect i4 = this.f1124b.i(view);
            int i5 = i2 + i4.left + i4.right;
            int i6 = i3 + i4.top + i4.bottom;
            int a2 = a(r(), s(), o() + p() + hVar.leftMargin + hVar.rightMargin + i5, hVar.width, a());
            int a3 = a(h(), i(), q() + n() + hVar.topMargin + hVar.bottomMargin + i6, hVar.height, b());
            if (a(view, a2, a3, hVar)) {
                view.measure(a2, a3);
            }
        }

        public void a(@NonNull View view, int i2, int i3, int i4, int i5) {
            h hVar = (h) view.getLayoutParams();
            Rect rect = hVar.f1132b;
            view.layout(i2 + rect.left + hVar.leftMargin, i3 + rect.top + hVar.topMargin, (i4 - rect.right) - hVar.rightMargin, (i5 - rect.bottom) - hVar.bottomMargin);
        }

        public void a(@NonNull View view, int i2, h hVar) {
            u h2 = RecyclerView.h(view);
            if (h2.isRemoved()) {
                this.f1124b.o.a(h2);
            } else {
                this.f1124b.o.g(h2);
            }
            this.f1123a.a(view, i2, hVar, h2.isRemoved());
        }

        public void a(@NonNull View view, @NonNull Rect rect) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView == null) {
                rect.set(0, 0, 0, 0);
            } else {
                rect.set(recyclerView.i(view));
            }
        }

        /* access modifiers changed from: package-private */
        public void a(View view, androidx.core.view.a.c cVar) {
            u h2 = RecyclerView.h(view);
            if (h2 != null && !h2.isRemoved() && !this.f1123a.c(h2.itemView)) {
                RecyclerView recyclerView = this.f1124b;
                a(recyclerView.k, recyclerView.qa, view, cVar);
            }
        }

        public void a(@NonNull View view, @NonNull n nVar) {
            o(view);
            nVar.b(view);
        }

        public void a(@NonNull View view, boolean z, @NonNull Rect rect) {
            Matrix matrix;
            if (z) {
                Rect rect2 = ((h) view.getLayoutParams()).f1132b;
                rect.set(-rect2.left, -rect2.top, view.getWidth() + rect2.right, view.getHeight() + rect2.bottom);
            } else {
                rect.set(0, 0, view.getWidth(), view.getHeight());
            }
            if (!(this.f1124b == null || (matrix = view.getMatrix()) == null || matrix.isIdentity())) {
                RectF rectF = this.f1124b.t;
                rectF.set(rect);
                matrix.mapRect(rectF);
                rect.set((int) Math.floor((double) rectF.left), (int) Math.floor((double) rectF.top), (int) Math.ceil((double) rectF.right), (int) Math.ceil((double) rectF.bottom));
            }
            rect.offset(view.getLeft(), view.getTop());
        }

        public void a(@NonNull AccessibilityEvent accessibilityEvent) {
            RecyclerView recyclerView = this.f1124b;
            a(recyclerView.k, recyclerView.qa, accessibilityEvent);
        }

        /* access modifiers changed from: package-private */
        public void a(androidx.core.view.a.c cVar) {
            RecyclerView recyclerView = this.f1124b;
            a(recyclerView.k, recyclerView.qa, cVar);
        }

        public void a(@Nullable a aVar, @Nullable a aVar2) {
        }

        public void a(@NonNull n nVar) {
            for (int e2 = e() - 1; e2 >= 0; e2--) {
                a(nVar, e2, c(e2));
            }
        }

        public void a(@NonNull n nVar, @NonNull r rVar, int i2, int i3) {
            this.f1124b.c(i2, i3);
        }

        public void a(@NonNull n nVar, @NonNull r rVar, @NonNull View view, @NonNull androidx.core.view.a.c cVar) {
            int i2 = 0;
            int l2 = b() ? l(view) : 0;
            if (a()) {
                i2 = l(view);
            }
            cVar.b((Object) c.C0013c.a(l2, 1, i2, 1, false, false));
        }

        public void a(@NonNull n nVar, @NonNull r rVar, @NonNull AccessibilityEvent accessibilityEvent) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null && accessibilityEvent != null) {
                boolean z = true;
                if (!recyclerView.canScrollVertically(1) && !this.f1124b.canScrollVertically(-1) && !this.f1124b.canScrollHorizontally(-1) && !this.f1124b.canScrollHorizontally(1)) {
                    z = false;
                }
                accessibilityEvent.setScrollable(z);
                a aVar = this.f1124b.u;
                if (aVar != null) {
                    accessibilityEvent.setItemCount(aVar.getItemCount());
                }
            }
        }

        public void a(@NonNull n nVar, @NonNull r rVar, @NonNull androidx.core.view.a.c cVar) {
            if (this.f1124b.canScrollVertically(-1) || this.f1124b.canScrollHorizontally(-1)) {
                cVar.a(8192);
                cVar.g(true);
            }
            if (this.f1124b.canScrollVertically(1) || this.f1124b.canScrollHorizontally(1)) {
                cVar.a((int) MpegAudioHeader.MAX_FRAME_SIZE_BYTES);
                cVar.g(true);
            }
            cVar.a((Object) c.b.a(b(nVar, rVar), a(nVar, rVar), d(nVar, rVar), c(nVar, rVar)));
        }

        /* access modifiers changed from: package-private */
        public void a(RecyclerView recyclerView) {
            this.i = true;
            b(recyclerView);
        }

        public void a(@NonNull RecyclerView recyclerView, int i2, int i3) {
        }

        public void a(@NonNull RecyclerView recyclerView, int i2, int i3, int i4) {
        }

        public void a(@NonNull RecyclerView recyclerView, int i2, int i3, @Nullable Object obj) {
            c(recyclerView, i2, i3);
        }

        /* access modifiers changed from: package-private */
        public void a(RecyclerView recyclerView, n nVar) {
            this.i = false;
            b(recyclerView, nVar);
        }

        public void a(String str) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                recyclerView.a(str);
            }
        }

        public boolean a() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean a(int i2, @Nullable Bundle bundle) {
            RecyclerView recyclerView = this.f1124b;
            return a(recyclerView.k, recyclerView.qa, i2, bundle);
        }

        /* access modifiers changed from: package-private */
        public boolean a(View view, int i2, int i3, h hVar) {
            return view.isLayoutRequested() || !this.k || !b(view.getWidth(), i2, hVar.width) || !b(view.getHeight(), i3, hVar.height);
        }

        /* access modifiers changed from: package-private */
        public boolean a(@NonNull View view, int i2, @Nullable Bundle bundle) {
            RecyclerView recyclerView = this.f1124b;
            return a(recyclerView.k, recyclerView.qa, view, i2, bundle);
        }

        public boolean a(@NonNull View view, boolean z, boolean z2) {
            boolean z3 = this.e.a(view, 24579) && this.f.a(view, 24579);
            return z ? z3 : !z3;
        }

        public boolean a(h hVar) {
            return hVar != null;
        }

        /* JADX WARNING: Removed duplicated region for block: B:25:0x0075 A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean a(@androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.n r8, @androidx.annotation.NonNull androidx.recyclerview.widget.RecyclerView.r r9, int r10, @androidx.annotation.Nullable android.os.Bundle r11) {
            /*
                r7 = this;
                androidx.recyclerview.widget.RecyclerView r8 = r7.f1124b
                r9 = 0
                if (r8 != 0) goto L_0x0006
                return r9
            L_0x0006:
                r11 = 4096(0x1000, float:5.74E-42)
                r0 = 1
                if (r10 == r11) goto L_0x0042
                r11 = 8192(0x2000, float:1.14794E-41)
                if (r10 == r11) goto L_0x0012
                r2 = r9
                r3 = r2
                goto L_0x0073
            L_0x0012:
                r10 = -1
                boolean r8 = r8.canScrollVertically(r10)
                if (r8 == 0) goto L_0x0029
                int r8 = r7.h()
                int r11 = r7.q()
                int r8 = r8 - r11
                int r11 = r7.n()
                int r8 = r8 - r11
                int r8 = -r8
                goto L_0x002a
            L_0x0029:
                r8 = r9
            L_0x002a:
                androidx.recyclerview.widget.RecyclerView r11 = r7.f1124b
                boolean r10 = r11.canScrollHorizontally(r10)
                if (r10 == 0) goto L_0x0071
                int r10 = r7.r()
                int r11 = r7.o()
                int r10 = r10 - r11
                int r11 = r7.p()
                int r10 = r10 - r11
                int r10 = -r10
                goto L_0x006e
            L_0x0042:
                boolean r8 = r8.canScrollVertically(r0)
                if (r8 == 0) goto L_0x0057
                int r8 = r7.h()
                int r10 = r7.q()
                int r8 = r8 - r10
                int r10 = r7.n()
                int r8 = r8 - r10
                goto L_0x0058
            L_0x0057:
                r8 = r9
            L_0x0058:
                androidx.recyclerview.widget.RecyclerView r10 = r7.f1124b
                boolean r10 = r10.canScrollHorizontally(r0)
                if (r10 == 0) goto L_0x0071
                int r10 = r7.r()
                int r11 = r7.o()
                int r10 = r10 - r11
                int r11 = r7.p()
                int r10 = r10 - r11
            L_0x006e:
                r3 = r8
                r2 = r10
                goto L_0x0073
            L_0x0071:
                r3 = r8
                r2 = r9
            L_0x0073:
                if (r3 != 0) goto L_0x0078
                if (r2 != 0) goto L_0x0078
                return r9
            L_0x0078:
                androidx.recyclerview.widget.RecyclerView r1 = r7.f1124b
                r4 = 0
                r5 = -2147483648(0xffffffff80000000, float:-0.0)
                r6 = 1
                r1.a((int) r2, (int) r3, (android.view.animation.Interpolator) r4, (int) r5, (boolean) r6)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.g.a(androidx.recyclerview.widget.RecyclerView$n, androidx.recyclerview.widget.RecyclerView$r, int, android.os.Bundle):boolean");
        }

        public boolean a(@NonNull n nVar, @NonNull r rVar, @NonNull View view, int i2, @Nullable Bundle bundle) {
            return false;
        }

        public boolean a(@NonNull RecyclerView recyclerView, @NonNull View view, @NonNull Rect rect, boolean z) {
            return a(recyclerView, view, rect, z, false);
        }

        public boolean a(@NonNull RecyclerView recyclerView, @NonNull View view, @NonNull Rect rect, boolean z, boolean z2) {
            int[] c2 = c(view, rect);
            int i2 = c2[0];
            int i3 = c2[1];
            if ((z2 && !d(recyclerView, i2, i3)) || (i2 == 0 && i3 == 0)) {
                return false;
            }
            if (z) {
                recyclerView.scrollBy(i2, i3);
            } else {
                recyclerView.i(i2, i3);
            }
            return true;
        }

        @Deprecated
        public boolean a(@NonNull RecyclerView recyclerView, @NonNull View view, @Nullable View view2) {
            return x() || recyclerView.o();
        }

        public boolean a(@NonNull RecyclerView recyclerView, @NonNull r rVar, @NonNull View view, @Nullable View view2) {
            return a(recyclerView, view, view2);
        }

        public boolean a(@NonNull RecyclerView recyclerView, @NonNull ArrayList<View> arrayList, int i2, int i3) {
            return false;
        }

        public boolean a(Runnable runnable) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                return recyclerView.removeCallbacks(runnable);
            }
            return false;
        }

        public int b(int i2, n nVar, r rVar) {
            return 0;
        }

        public int b(@NonNull n nVar, @NonNull r rVar) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView == null || recyclerView.u == null || !b()) {
                return 1;
            }
            return this.f1124b.u.getItemCount();
        }

        public int b(@NonNull r rVar) {
            return 0;
        }

        @Nullable
        public View b(int i2) {
            int e2 = e();
            for (int i3 = 0; i3 < e2; i3++) {
                View c2 = c(i3);
                u h2 = RecyclerView.h(c2);
                if (h2 != null && h2.getLayoutPosition() == i2 && !h2.shouldIgnore() && (this.f1124b.qa.d() || !h2.isRemoved())) {
                    return c2;
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public void b(int i2, int i3) {
            this.q = View.MeasureSpec.getSize(i2);
            this.o = View.MeasureSpec.getMode(i2);
            if (this.o == 0 && !RecyclerView.f1112c) {
                this.q = 0;
            }
            this.r = View.MeasureSpec.getSize(i3);
            this.p = View.MeasureSpec.getMode(i3);
            if (this.p == 0 && !RecyclerView.f1112c) {
                this.r = 0;
            }
        }

        public void b(View view) {
            b(view, -1);
        }

        public void b(View view, int i2) {
            a(view, i2, false);
        }

        public void b(@NonNull View view, @NonNull Rect rect) {
            RecyclerView.a(view, rect);
        }

        public void b(@NonNull n nVar) {
            for (int e2 = e() - 1; e2 >= 0; e2--) {
                if (!RecyclerView.h(c(e2)).shouldIgnore()) {
                    a(e2, nVar);
                }
            }
        }

        @CallSuper
        public void b(RecyclerView recyclerView) {
        }

        public void b(@NonNull RecyclerView recyclerView, int i2, int i3) {
        }

        @CallSuper
        public void b(RecyclerView recyclerView, n nVar) {
            c(recyclerView);
        }

        public boolean b() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean b(View view, int i2, int i3, h hVar) {
            return !this.k || !b(view.getMeasuredWidth(), i2, hVar.width) || !b(view.getMeasuredHeight(), i3, hVar.height);
        }

        public int c(@NonNull n nVar, @NonNull r rVar) {
            return 0;
        }

        public int c(@NonNull r rVar) {
            return 0;
        }

        @Nullable
        public View c(int i2) {
            C0163d dVar = this.f1123a;
            if (dVar != null) {
                return dVar.c(i2);
            }
            return null;
        }

        @Nullable
        public View c(@NonNull View view) {
            View c2;
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView == null || (c2 = recyclerView.c(view)) == null || this.f1123a.c(c2)) {
                return null;
            }
            return c2;
        }

        public abstract h c();

        public void c(int i2, int i3) {
            this.f1124b.setMeasuredDimension(i2, i3);
        }

        public void c(@NonNull View view, int i2) {
            a(view, i2, (h) view.getLayoutParams());
        }

        /* access modifiers changed from: package-private */
        public void c(n nVar) {
            int e2 = nVar.e();
            for (int i2 = e2 - 1; i2 >= 0; i2--) {
                View c2 = nVar.c(i2);
                u h2 = RecyclerView.h(c2);
                if (!h2.shouldIgnore()) {
                    h2.setIsRecyclable(false);
                    if (h2.isTmpDetached()) {
                        this.f1124b.removeDetachedView(c2, false);
                    }
                    ItemAnimator itemAnimator = this.f1124b.V;
                    if (itemAnimator != null) {
                        itemAnimator.d(h2);
                    }
                    h2.setIsRecyclable(true);
                    nVar.a(c2);
                }
            }
            nVar.c();
            if (e2 > 0) {
                this.f1124b.invalidate();
            }
        }

        @Deprecated
        public void c(RecyclerView recyclerView) {
        }

        public void c(@NonNull RecyclerView recyclerView, int i2, int i3) {
        }

        public int d() {
            return -1;
        }

        public int d(@NonNull View view) {
            return ((h) view.getLayoutParams()).f1132b.bottom;
        }

        public int d(@NonNull r rVar) {
            return 0;
        }

        @Nullable
        public View d(@NonNull View view, int i2) {
            return null;
        }

        public void d(@Px int i2) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                recyclerView.c(i2);
            }
        }

        /* access modifiers changed from: package-private */
        public void d(int i2, int i3) {
            int e2 = e();
            if (e2 == 0) {
                this.f1124b.c(i2, i3);
                return;
            }
            int i4 = Integer.MAX_VALUE;
            int i5 = Integer.MIN_VALUE;
            int i6 = Integer.MIN_VALUE;
            int i7 = Integer.MAX_VALUE;
            for (int i8 = 0; i8 < e2; i8++) {
                View c2 = c(i8);
                Rect rect = this.f1124b.r;
                b(c2, rect);
                int i9 = rect.left;
                if (i9 < i4) {
                    i4 = i9;
                }
                int i10 = rect.right;
                if (i10 > i5) {
                    i5 = i10;
                }
                int i11 = rect.top;
                if (i11 < i7) {
                    i7 = i11;
                }
                int i12 = rect.bottom;
                if (i12 > i6) {
                    i6 = i12;
                }
            }
            this.f1124b.r.set(i4, i7, i5, i6);
            a(this.f1124b.r, i2, i3);
        }

        public void d(@NonNull RecyclerView recyclerView) {
        }

        public boolean d(@NonNull n nVar, @NonNull r rVar) {
            return false;
        }

        public int e() {
            C0163d dVar = this.f1123a;
            if (dVar != null) {
                return dVar.a();
            }
            return 0;
        }

        public int e(@NonNull View view) {
            return view.getBottom() + d(view);
        }

        public int e(@NonNull r rVar) {
            return 0;
        }

        public void e(@Px int i2) {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                recyclerView.d(i2);
            }
        }

        public void e(n nVar, r rVar) {
            Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
        }

        /* access modifiers changed from: package-private */
        public void e(RecyclerView recyclerView) {
            b(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), 1073741824));
        }

        public int f(@NonNull View view) {
            return view.getLeft() - k(view);
        }

        public int f(@NonNull r rVar) {
            return 0;
        }

        public void f(int i2) {
        }

        /* access modifiers changed from: package-private */
        public void f(RecyclerView recyclerView) {
            int i2;
            if (recyclerView == null) {
                this.f1124b = null;
                this.f1123a = null;
                i2 = 0;
                this.q = 0;
            } else {
                this.f1124b = recyclerView;
                this.f1123a = recyclerView.n;
                this.q = recyclerView.getWidth();
                i2 = recyclerView.getHeight();
            }
            this.r = i2;
            this.o = 1073741824;
            this.p = 1073741824;
        }

        public boolean f() {
            RecyclerView recyclerView = this.f1124b;
            return recyclerView != null && recyclerView.p;
        }

        public int g(@NonNull View view) {
            Rect rect = ((h) view.getLayoutParams()).f1132b;
            return view.getMeasuredHeight() + rect.top + rect.bottom;
        }

        @Nullable
        public View g() {
            View focusedChild;
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView == null || (focusedChild = recyclerView.getFocusedChild()) == null || this.f1123a.c(focusedChild)) {
                return null;
            }
            return focusedChild;
        }

        public void g(int i2) {
            if (c(i2) != null) {
                this.f1123a.e(i2);
            }
        }

        public void g(r rVar) {
        }

        @Px
        public int h() {
            return this.r;
        }

        public int h(@NonNull View view) {
            Rect rect = ((h) view.getLayoutParams()).f1132b;
            return view.getMeasuredWidth() + rect.left + rect.right;
        }

        public void h(int i2) {
        }

        public int i() {
            return this.p;
        }

        public int i(@NonNull View view) {
            return view.getRight() + m(view);
        }

        public int j() {
            RecyclerView recyclerView = this.f1124b;
            a adapter = recyclerView != null ? recyclerView.getAdapter() : null;
            if (adapter != null) {
                return adapter.getItemCount();
            }
            return 0;
        }

        public int j(@NonNull View view) {
            return view.getTop() - n(view);
        }

        public int k() {
            return ViewCompat.j(this.f1124b);
        }

        public int k(@NonNull View view) {
            return ((h) view.getLayoutParams()).f1132b.left;
        }

        @Px
        public int l() {
            return ViewCompat.k(this.f1124b);
        }

        public int l(@NonNull View view) {
            return ((h) view.getLayoutParams()).a();
        }

        @Px
        public int m() {
            return ViewCompat.l(this.f1124b);
        }

        public int m(@NonNull View view) {
            return ((h) view.getLayoutParams()).f1132b.right;
        }

        @Px
        public int n() {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                return recyclerView.getPaddingBottom();
            }
            return 0;
        }

        public int n(@NonNull View view) {
            return ((h) view.getLayoutParams()).f1132b.top;
        }

        @Px
        public int o() {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                return recyclerView.getPaddingLeft();
            }
            return 0;
        }

        public void o(View view) {
            this.f1123a.d(view);
        }

        @Px
        public int p() {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                return recyclerView.getPaddingRight();
            }
            return 0;
        }

        @Px
        public int q() {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                return recyclerView.getPaddingTop();
            }
            return 0;
        }

        @Px
        public int r() {
            return this.q;
        }

        public int s() {
            return this.o;
        }

        /* access modifiers changed from: package-private */
        public boolean t() {
            int e2 = e();
            for (int i2 = 0; i2 < e2; i2++) {
                ViewGroup.LayoutParams layoutParams = c(i2).getLayoutParams();
                if (layoutParams.width < 0 && layoutParams.height < 0) {
                    return true;
                }
            }
            return false;
        }

        public boolean u() {
            return this.i;
        }

        public boolean v() {
            return this.j;
        }

        public final boolean w() {
            return this.l;
        }

        public boolean x() {
            q qVar = this.g;
            return qVar != null && qVar.c();
        }

        @Nullable
        public Parcelable y() {
            return null;
        }

        public void z() {
            RecyclerView recyclerView = this.f1124b;
            if (recyclerView != null) {
                recyclerView.requestLayout();
            }
        }
    }

    public static class h extends ViewGroup.MarginLayoutParams {

        /* renamed from: a  reason: collision with root package name */
        u f1131a;

        /* renamed from: b  reason: collision with root package name */
        final Rect f1132b = new Rect();

        /* renamed from: c  reason: collision with root package name */
        boolean f1133c = true;

        /* renamed from: d  reason: collision with root package name */
        boolean f1134d = false;

        public h(int i, int i2) {
            super(i, i2);
        }

        public h(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public h(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public h(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public h(h hVar) {
            super(hVar);
        }

        public int a() {
            return this.f1131a.getLayoutPosition();
        }

        public boolean b() {
            return this.f1131a.isUpdated();
        }

        public boolean c() {
            return this.f1131a.isRemoved();
        }

        public boolean d() {
            return this.f1131a.isInvalid();
        }
    }

    public interface i {
        void a(@NonNull View view);

        void b(@NonNull View view);
    }

    public static abstract class j {
        public abstract boolean a(int i, int i2);
    }

    public interface k {
        void a(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent);

        void a(boolean z);

        boolean b(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent);
    }

    public static abstract class l {
        public void a(@NonNull RecyclerView recyclerView, int i) {
        }

        public void a(@NonNull RecyclerView recyclerView, int i, int i2) {
        }
    }

    public static class m {

        /* renamed from: a  reason: collision with root package name */
        SparseArray<a> f1135a = new SparseArray<>();

        /* renamed from: b  reason: collision with root package name */
        private int f1136b = 0;

        static class a {

            /* renamed from: a  reason: collision with root package name */
            final ArrayList<u> f1137a = new ArrayList<>();

            /* renamed from: b  reason: collision with root package name */
            int f1138b = 5;

            /* renamed from: c  reason: collision with root package name */
            long f1139c = 0;

            /* renamed from: d  reason: collision with root package name */
            long f1140d = 0;

            a() {
            }
        }

        private a b(int i) {
            a aVar = this.f1135a.get(i);
            if (aVar != null) {
                return aVar;
            }
            a aVar2 = new a();
            this.f1135a.put(i, aVar2);
            return aVar2;
        }

        /* access modifiers changed from: package-private */
        public long a(long j, long j2) {
            return j == 0 ? j2 : ((j / 4) * 3) + (j2 / 4);
        }

        @Nullable
        public u a(int i) {
            a aVar = this.f1135a.get(i);
            if (aVar == null || aVar.f1137a.isEmpty()) {
                return null;
            }
            ArrayList<u> arrayList = aVar.f1137a;
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (!arrayList.get(size).isAttachedToTransitionOverlay()) {
                    return arrayList.remove(size);
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public void a() {
            this.f1136b++;
        }

        /* access modifiers changed from: package-private */
        public void a(int i, long j) {
            a b2 = b(i);
            b2.f1140d = a(b2.f1140d, j);
        }

        /* access modifiers changed from: package-private */
        public void a(a aVar, a aVar2, boolean z) {
            if (aVar != null) {
                c();
            }
            if (!z && this.f1136b == 0) {
                b();
            }
            if (aVar2 != null) {
                a();
            }
        }

        public void a(u uVar) {
            int itemViewType = uVar.getItemViewType();
            ArrayList<u> arrayList = b(itemViewType).f1137a;
            if (this.f1135a.get(itemViewType).f1138b > arrayList.size()) {
                uVar.resetInternal();
                arrayList.add(uVar);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean a(int i, long j, long j2) {
            long j3 = b(i).f1140d;
            return j3 == 0 || j + j3 < j2;
        }

        public void b() {
            for (int i = 0; i < this.f1135a.size(); i++) {
                this.f1135a.valueAt(i).f1137a.clear();
            }
        }

        /* access modifiers changed from: package-private */
        public void b(int i, long j) {
            a b2 = b(i);
            b2.f1139c = a(b2.f1139c, j);
        }

        /* access modifiers changed from: package-private */
        public boolean b(int i, long j, long j2) {
            long j3 = b(i).f1139c;
            return j3 == 0 || j + j3 < j2;
        }

        /* access modifiers changed from: package-private */
        public void c() {
            this.f1136b--;
        }
    }

    public final class n {

        /* renamed from: a  reason: collision with root package name */
        final ArrayList<u> f1141a = new ArrayList<>();

        /* renamed from: b  reason: collision with root package name */
        ArrayList<u> f1142b = null;

        /* renamed from: c  reason: collision with root package name */
        final ArrayList<u> f1143c = new ArrayList<>();

        /* renamed from: d  reason: collision with root package name */
        private final List<u> f1144d = Collections.unmodifiableList(this.f1141a);
        private int e = 2;
        int f = 2;
        m g;
        private s h;

        public n() {
        }

        private void a(ViewGroup viewGroup, boolean z) {
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                if (childAt instanceof ViewGroup) {
                    a((ViewGroup) childAt, true);
                }
            }
            if (z) {
                if (viewGroup.getVisibility() == 4) {
                    viewGroup.setVisibility(0);
                    viewGroup.setVisibility(4);
                    return;
                }
                int visibility = viewGroup.getVisibility();
                viewGroup.setVisibility(4);
                viewGroup.setVisibility(visibility);
            }
        }

        private boolean a(@NonNull u uVar, int i2, int i3, long j) {
            uVar.mOwnerRecyclerView = RecyclerView.this;
            int itemViewType = uVar.getItemViewType();
            long nanoTime = RecyclerView.this.getNanoTime();
            if (j != Long.MAX_VALUE && !this.g.a(itemViewType, nanoTime, j)) {
                return false;
            }
            RecyclerView.this.u.bindViewHolder(uVar, i2);
            this.g.a(uVar.getItemViewType(), RecyclerView.this.getNanoTime() - nanoTime);
            e(uVar);
            if (!RecyclerView.this.qa.d()) {
                return true;
            }
            uVar.mPreLayoutPosition = i3;
            return true;
        }

        private void e(u uVar) {
            if (RecyclerView.this.n()) {
                View view = uVar.itemView;
                if (ViewCompat.h(view) == 0) {
                    ViewCompat.b(view, 1);
                }
                L l = RecyclerView.this.xa;
                if (l != null) {
                    C0123a a2 = l.a();
                    if (a2 instanceof L.a) {
                        ((L.a) a2).b(view);
                    }
                    ViewCompat.a(view, a2);
                }
            }
        }

        private void f(u uVar) {
            View view = uVar.itemView;
            if (view instanceof ViewGroup) {
                a((ViewGroup) view, false);
            }
        }

        public int a(int i2) {
            if (i2 >= 0 && i2 < RecyclerView.this.qa.a()) {
                return !RecyclerView.this.qa.d() ? i2 : RecyclerView.this.m.b(i2);
            }
            throw new IndexOutOfBoundsException("invalid position " + i2 + ". State item count is " + RecyclerView.this.qa.a() + RecyclerView.this.i());
        }

        /* access modifiers changed from: package-private */
        public u a(int i2, boolean z) {
            View b2;
            int size = this.f1141a.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                u uVar = this.f1141a.get(i4);
                if (uVar.wasReturnedFromScrap() || uVar.getLayoutPosition() != i2 || uVar.isInvalid() || (!RecyclerView.this.qa.h && uVar.isRemoved())) {
                    i4++;
                } else {
                    uVar.addFlags(32);
                    return uVar;
                }
            }
            if (z || (b2 = RecyclerView.this.n.b(i2)) == null) {
                int size2 = this.f1143c.size();
                while (i3 < size2) {
                    u uVar2 = this.f1143c.get(i3);
                    if (uVar2.isInvalid() || uVar2.getLayoutPosition() != i2 || uVar2.isAttachedToTransitionOverlay()) {
                        i3++;
                    } else {
                        if (!z) {
                            this.f1143c.remove(i3);
                        }
                        return uVar2;
                    }
                }
                return null;
            }
            u h2 = RecyclerView.h(b2);
            RecyclerView.this.n.f(b2);
            int b3 = RecyclerView.this.n.b(b2);
            if (b3 != -1) {
                RecyclerView.this.n.a(b3);
                c(b2);
                h2.addFlags(8224);
                return h2;
            }
            throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + h2 + RecyclerView.this.i());
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:16:0x0037  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x005c  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x005f  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x01a1  */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x01ca  */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x01cd  */
        /* JADX WARNING: Removed duplicated region for block: B:94:0x01fd  */
        /* JADX WARNING: Removed duplicated region for block: B:96:0x020b  */
        @androidx.annotation.Nullable
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.u a(int r17, boolean r18, long r19) {
            /*
                r16 = this;
                r6 = r16
                r3 = r17
                r0 = r18
                if (r3 < 0) goto L_0x0227
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r1 = r1.qa
                int r1 = r1.a()
                if (r3 >= r1) goto L_0x0227
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r1 = r1.qa
                boolean r1 = r1.d()
                r2 = 0
                r7 = 1
                r8 = 0
                if (r1 == 0) goto L_0x0027
                androidx.recyclerview.widget.RecyclerView$u r1 = r16.b((int) r17)
                if (r1 == 0) goto L_0x0028
                r4 = r7
                goto L_0x0029
            L_0x0027:
                r1 = r2
            L_0x0028:
                r4 = r8
            L_0x0029:
                if (r1 != 0) goto L_0x005d
                androidx.recyclerview.widget.RecyclerView$u r1 = r16.a((int) r17, (boolean) r18)
                if (r1 == 0) goto L_0x005d
                boolean r5 = r6.d((androidx.recyclerview.widget.RecyclerView.u) r1)
                if (r5 != 0) goto L_0x005c
                if (r0 != 0) goto L_0x005a
                r5 = 4
                r1.addFlags(r5)
                boolean r5 = r1.isScrap()
                if (r5 == 0) goto L_0x004e
                androidx.recyclerview.widget.RecyclerView r5 = androidx.recyclerview.widget.RecyclerView.this
                android.view.View r9 = r1.itemView
                r5.removeDetachedView(r9, r8)
                r1.unScrap()
                goto L_0x0057
            L_0x004e:
                boolean r5 = r1.wasReturnedFromScrap()
                if (r5 == 0) goto L_0x0057
                r1.clearReturnedFromScrapFlag()
            L_0x0057:
                r6.b((androidx.recyclerview.widget.RecyclerView.u) r1)
            L_0x005a:
                r1 = r2
                goto L_0x005d
            L_0x005c:
                r4 = r7
            L_0x005d:
                if (r1 != 0) goto L_0x0180
                androidx.recyclerview.widget.RecyclerView r5 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.a r5 = r5.m
                int r5 = r5.b((int) r3)
                if (r5 < 0) goto L_0x0148
                androidx.recyclerview.widget.RecyclerView r9 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$a r9 = r9.u
                int r9 = r9.getItemCount()
                if (r5 >= r9) goto L_0x0148
                androidx.recyclerview.widget.RecyclerView r9 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$a r9 = r9.u
                int r9 = r9.getItemViewType(r5)
                androidx.recyclerview.widget.RecyclerView r10 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$a r10 = r10.u
                boolean r10 = r10.hasStableIds()
                if (r10 == 0) goto L_0x0096
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$a r1 = r1.u
                long r10 = r1.getItemId(r5)
                androidx.recyclerview.widget.RecyclerView$u r1 = r6.a((long) r10, (int) r9, (boolean) r0)
                if (r1 == 0) goto L_0x0096
                r1.mPosition = r5
                r4 = r7
            L_0x0096:
                if (r1 != 0) goto L_0x00eb
                androidx.recyclerview.widget.RecyclerView$s r0 = r6.h
                if (r0 == 0) goto L_0x00eb
                android.view.View r0 = r0.a(r6, r3, r9)
                if (r0 == 0) goto L_0x00eb
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$u r1 = r1.g((android.view.View) r0)
                if (r1 == 0) goto L_0x00ce
                boolean r0 = r1.shouldIgnore()
                if (r0 != 0) goto L_0x00b1
                goto L_0x00eb
            L_0x00b1:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view."
                r1.append(r2)
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                java.lang.String r2 = r2.i()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x00ce:
                java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "getViewForPositionAndType returned a view which does not have a ViewHolder"
                r1.append(r2)
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                java.lang.String r2 = r2.i()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x00eb:
                if (r1 != 0) goto L_0x0101
                androidx.recyclerview.widget.RecyclerView$m r0 = r16.d()
                androidx.recyclerview.widget.RecyclerView$u r1 = r0.a((int) r9)
                if (r1 == 0) goto L_0x0101
                r1.resetInternal()
                boolean r0 = androidx.recyclerview.widget.RecyclerView.f1111b
                if (r0 == 0) goto L_0x0101
                r6.f((androidx.recyclerview.widget.RecyclerView.u) r1)
            L_0x0101:
                if (r1 != 0) goto L_0x0180
                androidx.recyclerview.widget.RecyclerView r0 = androidx.recyclerview.widget.RecyclerView.this
                long r0 = r0.getNanoTime()
                r10 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
                int r5 = (r19 > r10 ? 1 : (r19 == r10 ? 0 : -1))
                if (r5 == 0) goto L_0x011f
                androidx.recyclerview.widget.RecyclerView$m r10 = r6.g
                r11 = r9
                r12 = r0
                r14 = r19
                boolean r5 = r10.b(r11, r12, r14)
                if (r5 != 0) goto L_0x011f
                return r2
            L_0x011f:
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$a r5 = r2.u
                androidx.recyclerview.widget.RecyclerView$u r2 = r5.createViewHolder(r2, r9)
                boolean r5 = androidx.recyclerview.widget.RecyclerView.e
                if (r5 == 0) goto L_0x013a
                android.view.View r5 = r2.itemView
                androidx.recyclerview.widget.RecyclerView r5 = androidx.recyclerview.widget.RecyclerView.e((android.view.View) r5)
                if (r5 == 0) goto L_0x013a
                java.lang.ref.WeakReference r10 = new java.lang.ref.WeakReference
                r10.<init>(r5)
                r2.mNestedRecyclerView = r10
            L_0x013a:
                androidx.recyclerview.widget.RecyclerView r5 = androidx.recyclerview.widget.RecyclerView.this
                long r10 = r5.getNanoTime()
                androidx.recyclerview.widget.RecyclerView$m r5 = r6.g
                long r10 = r10 - r0
                r5.b(r9, r10)
                r10 = r2
                goto L_0x0181
            L_0x0148:
                java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "Inconsistency detected. Invalid item position "
                r1.append(r2)
                r1.append(r3)
                java.lang.String r2 = "(offset:"
                r1.append(r2)
                r1.append(r5)
                java.lang.String r2 = ").state:"
                r1.append(r2)
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r2 = r2.qa
                int r2 = r2.a()
                r1.append(r2)
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                java.lang.String r2 = r2.i()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x0180:
                r10 = r1
            L_0x0181:
                r9 = r4
                if (r9 == 0) goto L_0x01ba
                androidx.recyclerview.widget.RecyclerView r0 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r0 = r0.qa
                boolean r0 = r0.d()
                if (r0 != 0) goto L_0x01ba
                r0 = 8192(0x2000, float:1.14794E-41)
                boolean r1 = r10.hasAnyOfTheFlags(r0)
                if (r1 == 0) goto L_0x01ba
                r10.setFlags(r8, r0)
                androidx.recyclerview.widget.RecyclerView r0 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r0 = r0.qa
                boolean r0 = r0.k
                if (r0 == 0) goto L_0x01ba
                int r0 = androidx.recyclerview.widget.RecyclerView.ItemAnimator.a((androidx.recyclerview.widget.RecyclerView.u) r10)
                r0 = r0 | 4096(0x1000, float:5.74E-42)
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$ItemAnimator r2 = r1.V
                androidx.recyclerview.widget.RecyclerView$r r1 = r1.qa
                java.util.List r4 = r10.getUnmodifiedPayloads()
                androidx.recyclerview.widget.RecyclerView$ItemAnimator$c r0 = r2.a((androidx.recyclerview.widget.RecyclerView.r) r1, (androidx.recyclerview.widget.RecyclerView.u) r10, (int) r0, (java.util.List<java.lang.Object>) r4)
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                r1.a((androidx.recyclerview.widget.RecyclerView.u) r10, (androidx.recyclerview.widget.RecyclerView.ItemAnimator.c) r0)
            L_0x01ba:
                androidx.recyclerview.widget.RecyclerView r0 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r0 = r0.qa
                boolean r0 = r0.d()
                if (r0 == 0) goto L_0x01cd
                boolean r0 = r10.isBound()
                if (r0 == 0) goto L_0x01cd
                r10.mPreLayoutPosition = r3
                goto L_0x01e0
            L_0x01cd:
                boolean r0 = r10.isBound()
                if (r0 == 0) goto L_0x01e2
                boolean r0 = r10.needsUpdate()
                if (r0 != 0) goto L_0x01e2
                boolean r0 = r10.isInvalid()
                if (r0 == 0) goto L_0x01e0
                goto L_0x01e2
            L_0x01e0:
                r0 = r8
                goto L_0x01f5
            L_0x01e2:
                androidx.recyclerview.widget.RecyclerView r0 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.a r0 = r0.m
                int r2 = r0.b((int) r3)
                r0 = r16
                r1 = r10
                r3 = r17
                r4 = r19
                boolean r0 = r0.a(r1, r2, r3, r4)
            L_0x01f5:
                android.view.View r1 = r10.itemView
                android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
                if (r1 != 0) goto L_0x020b
                androidx.recyclerview.widget.RecyclerView r1 = androidx.recyclerview.widget.RecyclerView.this
                android.view.ViewGroup$LayoutParams r1 = r1.generateDefaultLayoutParams()
            L_0x0203:
                androidx.recyclerview.widget.RecyclerView$h r1 = (androidx.recyclerview.widget.RecyclerView.h) r1
                android.view.View r2 = r10.itemView
                r2.setLayoutParams(r1)
                goto L_0x021c
            L_0x020b:
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                boolean r2 = r2.checkLayoutParams(r1)
                if (r2 != 0) goto L_0x021a
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                android.view.ViewGroup$LayoutParams r1 = r2.generateLayoutParams((android.view.ViewGroup.LayoutParams) r1)
                goto L_0x0203
            L_0x021a:
                androidx.recyclerview.widget.RecyclerView$h r1 = (androidx.recyclerview.widget.RecyclerView.h) r1
            L_0x021c:
                r1.f1131a = r10
                if (r9 == 0) goto L_0x0223
                if (r0 == 0) goto L_0x0223
                goto L_0x0224
            L_0x0223:
                r7 = r8
            L_0x0224:
                r1.f1134d = r7
                return r10
            L_0x0227:
                java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "Invalid item position "
                r1.append(r2)
                r1.append(r3)
                java.lang.String r2 = "("
                r1.append(r2)
                r1.append(r3)
                java.lang.String r2 = "). Item count:"
                r1.append(r2)
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                androidx.recyclerview.widget.RecyclerView$r r2 = r2.qa
                int r2 = r2.a()
                r1.append(r2)
                androidx.recyclerview.widget.RecyclerView r2 = androidx.recyclerview.widget.RecyclerView.this
                java.lang.String r2 = r2.i()
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.n.a(int, boolean, long):androidx.recyclerview.widget.RecyclerView$u");
        }

        /* access modifiers changed from: package-private */
        public u a(long j, int i2, boolean z) {
            for (int size = this.f1141a.size() - 1; size >= 0; size--) {
                u uVar = this.f1141a.get(size);
                if (uVar.getItemId() == j && !uVar.wasReturnedFromScrap()) {
                    if (i2 == uVar.getItemViewType()) {
                        uVar.addFlags(32);
                        if (uVar.isRemoved() && !RecyclerView.this.qa.d()) {
                            uVar.setFlags(2, 14);
                        }
                        return uVar;
                    } else if (!z) {
                        this.f1141a.remove(size);
                        RecyclerView.this.removeDetachedView(uVar.itemView, false);
                        a(uVar.itemView);
                    }
                }
            }
            int size2 = this.f1143c.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    return null;
                }
                u uVar2 = this.f1143c.get(size2);
                if (uVar2.getItemId() == j && !uVar2.isAttachedToTransitionOverlay()) {
                    if (i2 == uVar2.getItemViewType()) {
                        if (!z) {
                            this.f1143c.remove(size2);
                        }
                        return uVar2;
                    } else if (!z) {
                        e(size2);
                        return null;
                    }
                }
            }
        }

        public void a() {
            this.f1141a.clear();
            i();
        }

        /* access modifiers changed from: package-private */
        public void a(int i2, int i3) {
            int size = this.f1143c.size();
            for (int i4 = 0; i4 < size; i4++) {
                u uVar = this.f1143c.get(i4);
                if (uVar != null && uVar.mPosition >= i2) {
                    uVar.offsetPosition(i3, true);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void a(int i2, int i3, boolean z) {
            int i4 = i2 + i3;
            for (int size = this.f1143c.size() - 1; size >= 0; size--) {
                u uVar = this.f1143c.get(size);
                if (uVar != null) {
                    int i5 = uVar.mPosition;
                    if (i5 >= i4) {
                        uVar.offsetPosition(-i3, z);
                    } else if (i5 >= i2) {
                        uVar.addFlags(8);
                        e(size);
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void a(View view) {
            u h2 = RecyclerView.h(view);
            h2.mScrapContainer = null;
            h2.mInChangeScrap = false;
            h2.clearReturnedFromScrapFlag();
            b(h2);
        }

        /* access modifiers changed from: package-private */
        public void a(a aVar, a aVar2, boolean z) {
            a();
            d().a(aVar, aVar2, z);
        }

        /* access modifiers changed from: package-private */
        public void a(m mVar) {
            m mVar2 = this.g;
            if (mVar2 != null) {
                mVar2.c();
            }
            this.g = mVar;
            if (this.g != null && RecyclerView.this.getAdapter() != null) {
                this.g.a();
            }
        }

        /* access modifiers changed from: package-private */
        public void a(s sVar) {
            this.h = sVar;
        }

        /* access modifiers changed from: package-private */
        public void a(@NonNull u uVar) {
            o oVar = RecyclerView.this.w;
            if (oVar != null) {
                oVar.a(uVar);
            }
            a aVar = RecyclerView.this.u;
            if (aVar != null) {
                aVar.onViewRecycled(uVar);
            }
            RecyclerView recyclerView = RecyclerView.this;
            if (recyclerView.qa != null) {
                recyclerView.o.h(uVar);
            }
        }

        /* access modifiers changed from: package-private */
        public void a(@NonNull u uVar, boolean z) {
            RecyclerView.b(uVar);
            View view = uVar.itemView;
            L l = RecyclerView.this.xa;
            if (l != null) {
                C0123a a2 = l.a();
                ViewCompat.a(view, a2 instanceof L.a ? ((L.a) a2).a(view) : null);
            }
            if (z) {
                a(uVar);
            }
            uVar.mOwnerRecyclerView = null;
            d().a(uVar);
        }

        /* access modifiers changed from: package-private */
        public View b(int i2, boolean z) {
            return a(i2, z, Long.MAX_VALUE).itemView;
        }

        /* access modifiers changed from: package-private */
        public u b(int i2) {
            int size;
            int b2;
            ArrayList<u> arrayList = this.f1142b;
            if (!(arrayList == null || (size = arrayList.size()) == 0)) {
                int i3 = 0;
                int i4 = 0;
                while (i4 < size) {
                    u uVar = this.f1142b.get(i4);
                    if (uVar.wasReturnedFromScrap() || uVar.getLayoutPosition() != i2) {
                        i4++;
                    } else {
                        uVar.addFlags(32);
                        return uVar;
                    }
                }
                if (RecyclerView.this.u.hasStableIds() && (b2 = RecyclerView.this.m.b(i2)) > 0 && b2 < RecyclerView.this.u.getItemCount()) {
                    long itemId = RecyclerView.this.u.getItemId(b2);
                    while (i3 < size) {
                        u uVar2 = this.f1142b.get(i3);
                        if (uVar2.wasReturnedFromScrap() || uVar2.getItemId() != itemId) {
                            i3++;
                        } else {
                            uVar2.addFlags(32);
                            return uVar2;
                        }
                    }
                }
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public void b() {
            int size = this.f1143c.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.f1143c.get(i2).clearOldPosition();
            }
            int size2 = this.f1141a.size();
            for (int i3 = 0; i3 < size2; i3++) {
                this.f1141a.get(i3).clearOldPosition();
            }
            ArrayList<u> arrayList = this.f1142b;
            if (arrayList != null) {
                int size3 = arrayList.size();
                for (int i4 = 0; i4 < size3; i4++) {
                    this.f1142b.get(i4).clearOldPosition();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void b(int i2, int i3) {
            int i4;
            int i5;
            int i6;
            int i7;
            if (i2 < i3) {
                i5 = i3;
                i4 = -1;
                i6 = i2;
            } else {
                i5 = i2;
                i4 = 1;
                i6 = i3;
            }
            int size = this.f1143c.size();
            for (int i8 = 0; i8 < size; i8++) {
                u uVar = this.f1143c.get(i8);
                if (uVar != null && (i7 = uVar.mPosition) >= i6 && i7 <= i5) {
                    if (i7 == i2) {
                        uVar.offsetPosition(i3 - i2, false);
                    } else {
                        uVar.offsetPosition(i4, false);
                    }
                }
            }
        }

        public void b(@NonNull View view) {
            u h2 = RecyclerView.h(view);
            if (h2.isTmpDetached()) {
                RecyclerView.this.removeDetachedView(view, false);
            }
            if (h2.isScrap()) {
                h2.unScrap();
            } else if (h2.wasReturnedFromScrap()) {
                h2.clearReturnedFromScrapFlag();
            }
            b(h2);
            if (RecyclerView.this.V != null && !h2.isRecyclable()) {
                RecyclerView.this.V.d(h2);
            }
        }

        /* access modifiers changed from: package-private */
        public void b(u uVar) {
            boolean z;
            boolean z2 = false;
            if (uVar.isScrap() || uVar.itemView.getParent() != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Scrapped or attached views may not be recycled. isScrap:");
                sb.append(uVar.isScrap());
                sb.append(" isAttached:");
                if (uVar.itemView.getParent() != null) {
                    z2 = true;
                }
                sb.append(z2);
                sb.append(RecyclerView.this.i());
                throw new IllegalArgumentException(sb.toString());
            } else if (uVar.isTmpDetached()) {
                throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + uVar + RecyclerView.this.i());
            } else if (!uVar.shouldIgnore()) {
                boolean doesTransientStatePreventRecycling = uVar.doesTransientStatePreventRecycling();
                a aVar = RecyclerView.this.u;
                if ((aVar != null && doesTransientStatePreventRecycling && aVar.onFailedToRecycleView(uVar)) || uVar.isRecyclable()) {
                    if (this.f <= 0 || uVar.hasAnyOfTheFlags(526)) {
                        z = false;
                    } else {
                        int size = this.f1143c.size();
                        if (size >= this.f && size > 0) {
                            e(0);
                            size--;
                        }
                        if (RecyclerView.e && size > 0 && !RecyclerView.this.pa.a(uVar.mPosition)) {
                            int i2 = size - 1;
                            while (i2 >= 0) {
                                if (!RecyclerView.this.pa.a(this.f1143c.get(i2).mPosition)) {
                                    break;
                                }
                                i2--;
                            }
                            size = i2 + 1;
                        }
                        this.f1143c.add(size, uVar);
                        z = true;
                    }
                    if (!z) {
                        a(uVar, true);
                        z2 = true;
                    }
                } else {
                    z = false;
                }
                RecyclerView.this.o.h(uVar);
                if (!z && !z2 && doesTransientStatePreventRecycling) {
                    uVar.mOwnerRecyclerView = null;
                }
            } else {
                throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle." + RecyclerView.this.i());
            }
        }

        /* access modifiers changed from: package-private */
        public View c(int i2) {
            return this.f1141a.get(i2).itemView;
        }

        /* access modifiers changed from: package-private */
        public void c() {
            this.f1141a.clear();
            ArrayList<u> arrayList = this.f1142b;
            if (arrayList != null) {
                arrayList.clear();
            }
        }

        /* access modifiers changed from: package-private */
        public void c(int i2, int i3) {
            int i4;
            int i5 = i3 + i2;
            for (int size = this.f1143c.size() - 1; size >= 0; size--) {
                u uVar = this.f1143c.get(size);
                if (uVar != null && (i4 = uVar.mPosition) >= i2 && i4 < i5) {
                    uVar.addFlags(2);
                    e(size);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void c(View view) {
            ArrayList<u> arrayList;
            u h2 = RecyclerView.h(view);
            if (!h2.hasAnyOfTheFlags(12) && h2.isUpdated() && !RecyclerView.this.a(h2)) {
                if (this.f1142b == null) {
                    this.f1142b = new ArrayList<>();
                }
                h2.setScrapContainer(this, true);
                arrayList = this.f1142b;
            } else if (!h2.isInvalid() || h2.isRemoved() || RecyclerView.this.u.hasStableIds()) {
                h2.setScrapContainer(this, false);
                arrayList = this.f1141a;
            } else {
                throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool." + RecyclerView.this.i());
            }
            arrayList.add(h2);
        }

        /* access modifiers changed from: package-private */
        public void c(u uVar) {
            (uVar.mInChangeScrap ? this.f1142b : this.f1141a).remove(uVar);
            uVar.mScrapContainer = null;
            uVar.mInChangeScrap = false;
            uVar.clearReturnedFromScrapFlag();
        }

        @NonNull
        public View d(int i2) {
            return b(i2, false);
        }

        /* access modifiers changed from: package-private */
        public m d() {
            if (this.g == null) {
                this.g = new m();
            }
            return this.g;
        }

        /* access modifiers changed from: package-private */
        public boolean d(u uVar) {
            if (uVar.isRemoved()) {
                return RecyclerView.this.qa.d();
            }
            int i2 = uVar.mPosition;
            if (i2 < 0 || i2 >= RecyclerView.this.u.getItemCount()) {
                throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + uVar + RecyclerView.this.i());
            } else if (!RecyclerView.this.qa.d() && RecyclerView.this.u.getItemViewType(uVar.mPosition) != uVar.getItemViewType()) {
                return false;
            } else {
                if (RecyclerView.this.u.hasStableIds()) {
                    return uVar.getItemId() == RecyclerView.this.u.getItemId(uVar.mPosition);
                }
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public int e() {
            return this.f1141a.size();
        }

        /* access modifiers changed from: package-private */
        public void e(int i2) {
            a(this.f1143c.get(i2), true);
            this.f1143c.remove(i2);
        }

        @NonNull
        public List<u> f() {
            return this.f1144d;
        }

        public void f(int i2) {
            this.e = i2;
            j();
        }

        /* access modifiers changed from: package-private */
        public void g() {
            int size = this.f1143c.size();
            for (int i2 = 0; i2 < size; i2++) {
                h hVar = (h) this.f1143c.get(i2).itemView.getLayoutParams();
                if (hVar != null) {
                    hVar.f1133c = true;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void h() {
            int size = this.f1143c.size();
            for (int i2 = 0; i2 < size; i2++) {
                u uVar = this.f1143c.get(i2);
                if (uVar != null) {
                    uVar.addFlags(6);
                    uVar.addChangePayload((Object) null);
                }
            }
            a aVar = RecyclerView.this.u;
            if (aVar == null || !aVar.hasStableIds()) {
                i();
            }
        }

        /* access modifiers changed from: package-private */
        public void i() {
            for (int size = this.f1143c.size() - 1; size >= 0; size--) {
                e(size);
            }
            this.f1143c.clear();
            if (RecyclerView.e) {
                RecyclerView.this.pa.a();
            }
        }

        /* access modifiers changed from: package-private */
        public void j() {
            g gVar = RecyclerView.this.v;
            this.f = this.e + (gVar != null ? gVar.m : 0);
            for (int size = this.f1143c.size() - 1; size >= 0 && this.f1143c.size() > this.f; size--) {
                e(size);
            }
        }
    }

    public interface o {
        void a(@NonNull u uVar);
    }

    private class p extends c {
        p() {
        }

        public void a() {
            RecyclerView.this.a((String) null);
            RecyclerView recyclerView = RecyclerView.this;
            recyclerView.qa.g = true;
            recyclerView.b(true);
            if (!RecyclerView.this.m.c()) {
                RecyclerView.this.requestLayout();
            }
        }

        public void a(int i, int i2, int i3) {
            RecyclerView.this.a((String) null);
            if (RecyclerView.this.m.a(i, i2, i3)) {
                b();
            }
        }

        public void a(int i, int i2, Object obj) {
            RecyclerView.this.a((String) null);
            if (RecyclerView.this.m.a(i, i2, obj)) {
                b();
            }
        }

        /* access modifiers changed from: package-private */
        public void b() {
            if (RecyclerView.f1113d) {
                RecyclerView recyclerView = RecyclerView.this;
                if (recyclerView.B && recyclerView.A) {
                    ViewCompat.a((View) recyclerView, recyclerView.q);
                    return;
                }
            }
            RecyclerView recyclerView2 = RecyclerView.this;
            recyclerView2.J = true;
            recyclerView2.requestLayout();
        }

        public void b(int i, int i2) {
            RecyclerView.this.a((String) null);
            if (RecyclerView.this.m.b(i, i2)) {
                b();
            }
        }

        public void c(int i, int i2) {
            RecyclerView.this.a((String) null);
            if (RecyclerView.this.m.c(i, i2)) {
                b();
            }
        }
    }

    public static abstract class q {

        public interface a {
        }

        public abstract int a();

        public abstract void a(int i);

        /* access modifiers changed from: package-private */
        public abstract void a(int i, int i2);

        /* access modifiers changed from: protected */
        public abstract void a(View view);

        public abstract boolean b();

        public abstract boolean c();

        /* access modifiers changed from: protected */
        public final void d() {
            throw null;
        }
    }

    public static class r {

        /* renamed from: a  reason: collision with root package name */
        int f1146a = -1;

        /* renamed from: b  reason: collision with root package name */
        private SparseArray<Object> f1147b;

        /* renamed from: c  reason: collision with root package name */
        int f1148c = 0;

        /* renamed from: d  reason: collision with root package name */
        int f1149d = 0;
        int e = 1;
        int f = 0;
        boolean g = false;
        boolean h = false;
        boolean i = false;
        boolean j = false;
        boolean k = false;
        boolean l = false;
        int m;
        long n;
        int o;
        int p;
        int q;

        public int a() {
            return this.h ? this.f1148c - this.f1149d : this.f;
        }

        /* access modifiers changed from: package-private */
        public void a(int i2) {
            if ((this.e & i2) == 0) {
                throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(i2) + " but it is " + Integer.toBinaryString(this.e));
            }
        }

        /* access modifiers changed from: package-private */
        public void a(a aVar) {
            this.e = 1;
            this.f = aVar.getItemCount();
            this.h = false;
            this.i = false;
            this.j = false;
        }

        public int b() {
            return this.f1146a;
        }

        public boolean c() {
            return this.f1146a != -1;
        }

        public boolean d() {
            return this.h;
        }

        public boolean e() {
            return this.l;
        }

        public String toString() {
            return "State{mTargetPosition=" + this.f1146a + ", mData=" + this.f1147b + ", mItemCount=" + this.f + ", mIsMeasuring=" + this.j + ", mPreviousLayoutItemCount=" + this.f1148c + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.f1149d + ", mStructureChanged=" + this.g + ", mInPreLayout=" + this.h + ", mRunSimpleAnimations=" + this.k + ", mRunPredictiveAnimations=" + this.l + '}';
        }
    }

    public static abstract class s {
        @Nullable
        public abstract View a(@NonNull n nVar, int i, int i2);
    }

    class t implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f1150a;

        /* renamed from: b  reason: collision with root package name */
        private int f1151b;

        /* renamed from: c  reason: collision with root package name */
        OverScroller f1152c;

        /* renamed from: d  reason: collision with root package name */
        Interpolator f1153d = RecyclerView.i;
        private boolean e = false;
        private boolean f = false;

        t() {
            this.f1152c = new OverScroller(RecyclerView.this.getContext(), RecyclerView.i);
        }

        private float a(float f2) {
            return (float) Math.sin((double) ((f2 - 0.5f) * 0.47123894f));
        }

        private int a(int i, int i2, int i3, int i4) {
            int i5;
            int abs = Math.abs(i);
            int abs2 = Math.abs(i2);
            boolean z = abs > abs2;
            int sqrt = (int) Math.sqrt((double) ((i3 * i3) + (i4 * i4)));
            int sqrt2 = (int) Math.sqrt((double) ((i * i) + (i2 * i2)));
            int width = z ? RecyclerView.this.getWidth() : RecyclerView.this.getHeight();
            int i6 = width / 2;
            float f2 = (float) width;
            float f3 = (float) i6;
            float a2 = f3 + (a(Math.min(1.0f, (((float) sqrt2) * 1.0f) / f2)) * f3);
            if (sqrt > 0) {
                i5 = Math.round(Math.abs(a2 / ((float) sqrt)) * 1000.0f) * 4;
            } else {
                if (!z) {
                    abs = abs2;
                }
                i5 = (int) (((((float) abs) / f2) + 1.0f) * 300.0f);
            }
            return Math.min(i5, 2000);
        }

        private void c() {
            RecyclerView.this.removeCallbacks(this);
            ViewCompat.a((View) RecyclerView.this, (Runnable) this);
        }

        /* access modifiers changed from: package-private */
        public void a() {
            if (this.e) {
                this.f = true;
            } else {
                c();
            }
        }

        public void a(int i, int i2) {
            RecyclerView.this.setScrollState(2);
            this.f1151b = 0;
            this.f1150a = 0;
            Interpolator interpolator = this.f1153d;
            Interpolator interpolator2 = RecyclerView.i;
            if (interpolator != interpolator2) {
                this.f1153d = interpolator2;
                this.f1152c = new OverScroller(RecyclerView.this.getContext(), RecyclerView.i);
            }
            this.f1152c.fling(0, 0, i, i2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            a();
        }

        public void a(int i, int i2, int i3, @Nullable Interpolator interpolator) {
            if (i3 == Integer.MIN_VALUE) {
                i3 = a(i, i2, 0, 0);
            }
            int i4 = i3;
            if (interpolator == null) {
                interpolator = RecyclerView.i;
            }
            if (this.f1153d != interpolator) {
                this.f1153d = interpolator;
                this.f1152c = new OverScroller(RecyclerView.this.getContext(), interpolator);
            }
            this.f1151b = 0;
            this.f1150a = 0;
            RecyclerView.this.setScrollState(2);
            this.f1152c.startScroll(0, 0, i, i2, i4);
            if (Build.VERSION.SDK_INT < 23) {
                this.f1152c.computeScrollOffset();
            }
            a();
        }

        public void b() {
            RecyclerView.this.removeCallbacks(this);
            this.f1152c.abortAnimation();
        }

        public void run() {
            int i;
            int i2;
            RecyclerView recyclerView = RecyclerView.this;
            if (recyclerView.v == null) {
                b();
                return;
            }
            this.f = false;
            this.e = true;
            recyclerView.b();
            OverScroller overScroller = this.f1152c;
            if (overScroller.computeScrollOffset()) {
                int currX = overScroller.getCurrX();
                int currY = overScroller.getCurrY();
                int i3 = currX - this.f1150a;
                int i4 = currY - this.f1151b;
                this.f1150a = currX;
                this.f1151b = currY;
                RecyclerView recyclerView2 = RecyclerView.this;
                int[] iArr = recyclerView2.Da;
                iArr[0] = 0;
                iArr[1] = 0;
                if (recyclerView2.a(i3, i4, iArr, (int[]) null, 1)) {
                    int[] iArr2 = RecyclerView.this.Da;
                    i3 -= iArr2[0];
                    i4 -= iArr2[1];
                }
                if (RecyclerView.this.getOverScrollMode() != 2) {
                    RecyclerView.this.b(i3, i4);
                }
                RecyclerView recyclerView3 = RecyclerView.this;
                if (recyclerView3.u != null) {
                    int[] iArr3 = recyclerView3.Da;
                    iArr3[0] = 0;
                    iArr3[1] = 0;
                    recyclerView3.a(i3, i4, iArr3);
                    RecyclerView recyclerView4 = RecyclerView.this;
                    int[] iArr4 = recyclerView4.Da;
                    i = iArr4[0];
                    i2 = iArr4[1];
                    i3 -= i;
                    i4 -= i2;
                    q qVar = recyclerView4.v.g;
                    if (qVar != null && !qVar.b() && qVar.c()) {
                        int a2 = RecyclerView.this.qa.a();
                        if (a2 == 0) {
                            qVar.d();
                        } else {
                            if (qVar.a() >= a2) {
                                qVar.a(a2 - 1);
                            }
                            qVar.a(i, i2);
                        }
                    }
                } else {
                    i2 = 0;
                    i = 0;
                }
                if (!RecyclerView.this.x.isEmpty()) {
                    RecyclerView.this.invalidate();
                }
                RecyclerView recyclerView5 = RecyclerView.this;
                int[] iArr5 = recyclerView5.Da;
                iArr5[0] = 0;
                iArr5[1] = 0;
                recyclerView5.a(i, i2, i3, i4, (int[]) null, 1, iArr5);
                int[] iArr6 = RecyclerView.this.Da;
                int i5 = i3 - iArr6[0];
                int i6 = i4 - iArr6[1];
                if (!(i == 0 && i2 == 0)) {
                    RecyclerView.this.d(i, i2);
                }
                if (!RecyclerView.this.awakenScrollBars()) {
                    RecyclerView.this.invalidate();
                }
                boolean z = overScroller.isFinished() || (((overScroller.getCurrX() == overScroller.getFinalX()) || i5 != 0) && ((overScroller.getCurrY() == overScroller.getFinalY()) || i6 != 0));
                q qVar2 = RecyclerView.this.v.g;
                if ((qVar2 != null && qVar2.b()) || !z) {
                    a();
                    RecyclerView recyclerView6 = RecyclerView.this;
                    C0178t tVar = recyclerView6.oa;
                    if (tVar != null) {
                        tVar.a(recyclerView6, i, i2);
                    }
                } else {
                    if (RecyclerView.this.getOverScrollMode() != 2) {
                        int currVelocity = (int) overScroller.getCurrVelocity();
                        int i7 = i5 < 0 ? -currVelocity : i5 > 0 ? currVelocity : 0;
                        if (i6 < 0) {
                            currVelocity = -currVelocity;
                        } else if (i6 <= 0) {
                            currVelocity = 0;
                        }
                        RecyclerView.this.a(i7, currVelocity);
                    }
                    if (RecyclerView.e) {
                        RecyclerView.this.pa.a();
                    }
                }
            }
            q qVar3 = RecyclerView.this.v.g;
            if (qVar3 != null && qVar3.b()) {
                qVar3.a(0, 0);
            }
            this.e = false;
            if (this.f) {
                c();
                return;
            }
            RecyclerView.this.setScrollState(0);
            RecyclerView.this.g(1);
        }
    }

    public static abstract class u {
        static final int FLAG_ADAPTER_FULLUPDATE = 1024;
        static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
        static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
        static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
        static final int FLAG_BOUND = 1;
        static final int FLAG_IGNORE = 128;
        static final int FLAG_INVALID = 4;
        static final int FLAG_MOVED = 2048;
        static final int FLAG_NOT_RECYCLABLE = 16;
        static final int FLAG_REMOVED = 8;
        static final int FLAG_RETURNED_FROM_SCRAP = 32;
        static final int FLAG_TMP_DETACHED = 256;
        static final int FLAG_UPDATE = 2;
        private static final List<Object> FULLUPDATE_PAYLOADS = Collections.emptyList();
        static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
        @NonNull
        public final View itemView;
        int mFlags;
        boolean mInChangeScrap = false;
        private int mIsRecyclableCount = 0;
        long mItemId = -1;
        int mItemViewType = -1;
        WeakReference<RecyclerView> mNestedRecyclerView;
        int mOldPosition = -1;
        RecyclerView mOwnerRecyclerView;
        List<Object> mPayloads = null;
        @VisibleForTesting
        int mPendingAccessibilityState = -1;
        int mPosition = -1;
        int mPreLayoutPosition = -1;
        n mScrapContainer = null;
        u mShadowedHolder = null;
        u mShadowingHolder = null;
        List<Object> mUnmodifiedPayloads = null;
        private int mWasImportantForAccessibilityBeforeHidden = 0;

        public u(@NonNull View view) {
            if (view != null) {
                this.itemView = view;
                return;
            }
            throw new IllegalArgumentException("itemView may not be null");
        }

        private void createPayloadsIfNeeded() {
            if (this.mPayloads == null) {
                this.mPayloads = new ArrayList();
                this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
            }
        }

        /* access modifiers changed from: package-private */
        public void addChangePayload(Object obj) {
            if (obj == null) {
                addFlags(FLAG_ADAPTER_FULLUPDATE);
            } else if ((FLAG_ADAPTER_FULLUPDATE & this.mFlags) == 0) {
                createPayloadsIfNeeded();
                this.mPayloads.add(obj);
            }
        }

        /* access modifiers changed from: package-private */
        public void addFlags(int i) {
            this.mFlags = i | this.mFlags;
        }

        /* access modifiers changed from: package-private */
        public void clearOldPosition() {
            this.mOldPosition = -1;
            this.mPreLayoutPosition = -1;
        }

        /* access modifiers changed from: package-private */
        public void clearPayload() {
            List<Object> list = this.mPayloads;
            if (list != null) {
                list.clear();
            }
            this.mFlags &= -1025;
        }

        /* access modifiers changed from: package-private */
        public void clearReturnedFromScrapFlag() {
            this.mFlags &= -33;
        }

        /* access modifiers changed from: package-private */
        public void clearTmpDetachFlag() {
            this.mFlags &= -257;
        }

        /* access modifiers changed from: package-private */
        public boolean doesTransientStatePreventRecycling() {
            return (this.mFlags & 16) == 0 && ViewCompat.p(this.itemView);
        }

        /* access modifiers changed from: package-private */
        public void flagRemovedAndOffsetPosition(int i, int i2, boolean z) {
            addFlags(8);
            offsetPosition(i2, z);
            this.mPosition = i;
        }

        public final int getAdapterPosition() {
            RecyclerView recyclerView = this.mOwnerRecyclerView;
            if (recyclerView == null) {
                return -1;
            }
            return recyclerView.c(this);
        }

        public final long getItemId() {
            return this.mItemId;
        }

        public final int getItemViewType() {
            return this.mItemViewType;
        }

        public final int getLayoutPosition() {
            int i = this.mPreLayoutPosition;
            return i == -1 ? this.mPosition : i;
        }

        public final int getOldPosition() {
            return this.mOldPosition;
        }

        @Deprecated
        public final int getPosition() {
            int i = this.mPreLayoutPosition;
            return i == -1 ? this.mPosition : i;
        }

        /* access modifiers changed from: package-private */
        public List<Object> getUnmodifiedPayloads() {
            if ((this.mFlags & FLAG_ADAPTER_FULLUPDATE) != 0) {
                return FULLUPDATE_PAYLOADS;
            }
            List<Object> list = this.mPayloads;
            return (list == null || list.size() == 0) ? FULLUPDATE_PAYLOADS : this.mUnmodifiedPayloads;
        }

        /* access modifiers changed from: package-private */
        public boolean hasAnyOfTheFlags(int i) {
            return (i & this.mFlags) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean isAdapterPositionUnknown() {
            return (this.mFlags & FLAG_ADAPTER_POSITION_UNKNOWN) != 0 || isInvalid();
        }

        /* access modifiers changed from: package-private */
        public boolean isAttachedToTransitionOverlay() {
            return (this.itemView.getParent() == null || this.itemView.getParent() == this.mOwnerRecyclerView) ? false : true;
        }

        /* access modifiers changed from: package-private */
        public boolean isBound() {
            return (this.mFlags & 1) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean isInvalid() {
            return (this.mFlags & 4) != 0;
        }

        public final boolean isRecyclable() {
            return (this.mFlags & 16) == 0 && !ViewCompat.p(this.itemView);
        }

        /* access modifiers changed from: package-private */
        public boolean isRemoved() {
            return (this.mFlags & 8) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean isScrap() {
            return this.mScrapContainer != null;
        }

        /* access modifiers changed from: package-private */
        public boolean isTmpDetached() {
            return (this.mFlags & FLAG_TMP_DETACHED) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean isUpdated() {
            return (this.mFlags & 2) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean needsUpdate() {
            return (this.mFlags & 2) != 0;
        }

        /* access modifiers changed from: package-private */
        public void offsetPosition(int i, boolean z) {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
            if (this.mPreLayoutPosition == -1) {
                this.mPreLayoutPosition = this.mPosition;
            }
            if (z) {
                this.mPreLayoutPosition += i;
            }
            this.mPosition += i;
            if (this.itemView.getLayoutParams() != null) {
                ((h) this.itemView.getLayoutParams()).f1133c = true;
            }
        }

        /* access modifiers changed from: package-private */
        public void onEnteredHiddenState(RecyclerView recyclerView) {
            int i = this.mPendingAccessibilityState;
            if (i == -1) {
                i = ViewCompat.h(this.itemView);
            }
            this.mWasImportantForAccessibilityBeforeHidden = i;
            recyclerView.a(this, 4);
        }

        /* access modifiers changed from: package-private */
        public void onLeftHiddenState(RecyclerView recyclerView) {
            recyclerView.a(this, this.mWasImportantForAccessibilityBeforeHidden);
            this.mWasImportantForAccessibilityBeforeHidden = 0;
        }

        /* access modifiers changed from: package-private */
        public void resetInternal() {
            this.mFlags = 0;
            this.mPosition = -1;
            this.mOldPosition = -1;
            this.mItemId = -1;
            this.mPreLayoutPosition = -1;
            this.mIsRecyclableCount = 0;
            this.mShadowedHolder = null;
            this.mShadowingHolder = null;
            clearPayload();
            this.mWasImportantForAccessibilityBeforeHidden = 0;
            this.mPendingAccessibilityState = -1;
            RecyclerView.b(this);
        }

        /* access modifiers changed from: package-private */
        public void saveOldPosition() {
            if (this.mOldPosition == -1) {
                this.mOldPosition = this.mPosition;
            }
        }

        /* access modifiers changed from: package-private */
        public void setFlags(int i, int i2) {
            this.mFlags = (i & i2) | (this.mFlags & (~i2));
        }

        public final void setIsRecyclable(boolean z) {
            int i;
            this.mIsRecyclableCount = z ? this.mIsRecyclableCount - 1 : this.mIsRecyclableCount + 1;
            int i2 = this.mIsRecyclableCount;
            if (i2 < 0) {
                this.mIsRecyclableCount = 0;
                Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
                return;
            }
            if (!z && i2 == 1) {
                i = this.mFlags | 16;
            } else if (z && this.mIsRecyclableCount == 0) {
                i = this.mFlags & -17;
            } else {
                return;
            }
            this.mFlags = i;
        }

        /* access modifiers changed from: package-private */
        public void setScrapContainer(n nVar, boolean z) {
            this.mScrapContainer = nVar;
            this.mInChangeScrap = z;
        }

        /* access modifiers changed from: package-private */
        public boolean shouldBeKeptAsChild() {
            return (this.mFlags & 16) != 0;
        }

        /* access modifiers changed from: package-private */
        public boolean shouldIgnore() {
            return (this.mFlags & FLAG_IGNORE) != 0;
        }

        /* access modifiers changed from: package-private */
        public void stopIgnoring() {
            this.mFlags &= -129;
        }

        public String toString() {
            String simpleName = getClass().isAnonymousClass() ? "ViewHolder" : getClass().getSimpleName();
            StringBuilder sb = new StringBuilder(simpleName + "{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
            if (isScrap()) {
                sb.append(" scrap ");
                sb.append(this.mInChangeScrap ? "[changeScrap]" : "[attachedScrap]");
            }
            if (isInvalid()) {
                sb.append(" invalid");
            }
            if (!isBound()) {
                sb.append(" unbound");
            }
            if (needsUpdate()) {
                sb.append(" update");
            }
            if (isRemoved()) {
                sb.append(" removed");
            }
            if (shouldIgnore()) {
                sb.append(" ignored");
            }
            if (isTmpDetached()) {
                sb.append(" tmpDetached");
            }
            if (!isRecyclable()) {
                sb.append(" not recyclable(" + this.mIsRecyclableCount + ")");
            }
            if (isAdapterPositionUnknown()) {
                sb.append(" undefined adapter position");
            }
            if (this.itemView.getParent() == null) {
                sb.append(" no parent");
            }
            sb.append("}");
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public void unScrap() {
            this.mScrapContainer.c(this);
        }

        /* access modifiers changed from: package-private */
        public boolean wasReturnedFromScrap() {
            return (this.mFlags & 32) != 0;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: java.lang.Class<?>[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    static {
        /*
            r0 = 1
            int[] r1 = new int[r0]
            r2 = 0
            r3 = 16843830(0x1010436, float:2.369658E-38)
            r1[r2] = r3
            f1110a = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 18
            if (r1 == r3) goto L_0x001c
            r3 = 19
            if (r1 == r3) goto L_0x001c
            r3 = 20
            if (r1 != r3) goto L_0x001a
            goto L_0x001c
        L_0x001a:
            r1 = r2
            goto L_0x001d
        L_0x001c:
            r1 = r0
        L_0x001d:
            f1111b = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 23
            if (r1 < r3) goto L_0x0027
            r1 = r0
            goto L_0x0028
        L_0x0027:
            r1 = r2
        L_0x0028:
            f1112c = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 16
            if (r1 < r3) goto L_0x0032
            r1 = r0
            goto L_0x0033
        L_0x0032:
            r1 = r2
        L_0x0033:
            f1113d = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r1 < r3) goto L_0x003d
            r1 = r0
            goto L_0x003e
        L_0x003d:
            r1 = r2
        L_0x003e:
            e = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 15
            if (r1 > r3) goto L_0x0048
            r1 = r0
            goto L_0x0049
        L_0x0048:
            r1 = r2
        L_0x0049:
            f = r1
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 > r3) goto L_0x0051
            r1 = r0
            goto L_0x0052
        L_0x0051:
            r1 = r2
        L_0x0052:
            g = r1
            r1 = 4
            java.lang.Class[] r1 = new java.lang.Class[r1]
            java.lang.Class<android.content.Context> r3 = android.content.Context.class
            r1[r2] = r3
            java.lang.Class<android.util.AttributeSet> r2 = android.util.AttributeSet.class
            r1[r0] = r2
            r0 = 2
            java.lang.Class r2 = java.lang.Integer.TYPE
            r1[r0] = r2
            r0 = 3
            r1[r0] = r2
            h = r1
            androidx.recyclerview.widget.E r0 = new androidx.recyclerview.widget.E
            r0.<init>()
            i = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.<clinit>():void");
    }

    public RecyclerView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.i.a.recyclerViewStyle);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.j = new p();
        this.k = new n();
        this.o = new V();
        this.q = new C(this);
        this.r = new Rect();
        this.s = new Rect();
        this.t = new RectF();
        this.x = new ArrayList<>();
        this.y = new ArrayList<>();
        this.E = 0;
        this.M = false;
        this.N = false;
        this.O = 0;
        this.P = 0;
        this.Q = new EdgeEffectFactory();
        this.V = new C0172m();
        this.W = 0;
        this.aa = -1;
        this.ka = Float.MIN_VALUE;
        this.la = Float.MIN_VALUE;
        boolean z2 = true;
        this.ma = true;
        this.na = new t();
        this.pa = e ? new C0178t.a() : null;
        this.qa = new r();
        this.ta = false;
        this.ua = false;
        this.va = new e();
        this.wa = false;
        this.za = new int[2];
        this.Ba = new int[2];
        this.Ca = new int[2];
        this.Da = new int[2];
        this.Ea = new ArrayList();
        this.Fa = new D(this);
        this.Ga = new F(this);
        setScrollContainer(true);
        setFocusableInTouchMode(true);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.ga = viewConfiguration.getScaledTouchSlop();
        this.ka = y.a(viewConfiguration, context);
        this.la = y.b(viewConfiguration, context);
        this.ia = viewConfiguration.getScaledMinimumFlingVelocity();
        this.ja = viewConfiguration.getScaledMaximumFlingVelocity();
        setWillNotDraw(getOverScrollMode() == 2);
        this.V.a(this.va);
        k();
        H();
        G();
        if (ViewCompat.h(this) == 0) {
            ViewCompat.b((View) this, 1);
        }
        this.K = (AccessibilityManager) getContext().getSystemService("accessibility");
        setAccessibilityDelegateCompat(new L(this));
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, a.i.c.RecyclerView, i2, 0);
        if (Build.VERSION.SDK_INT >= 29) {
            saveAttributeDataForStyleable(context, a.i.c.RecyclerView, attributeSet, obtainStyledAttributes, i2, 0);
        }
        String string = obtainStyledAttributes.getString(a.i.c.RecyclerView_layoutManager);
        if (obtainStyledAttributes.getInt(a.i.c.RecyclerView_android_descendantFocusability, -1) == -1) {
            setDescendantFocusability(262144);
        }
        this.p = obtainStyledAttributes.getBoolean(a.i.c.RecyclerView_android_clipToPadding, true);
        this.C = obtainStyledAttributes.getBoolean(a.i.c.RecyclerView_fastScrollEnabled, false);
        if (this.C) {
            a((StateListDrawable) obtainStyledAttributes.getDrawable(a.i.c.RecyclerView_fastScrollVerticalThumbDrawable), obtainStyledAttributes.getDrawable(a.i.c.RecyclerView_fastScrollVerticalTrackDrawable), (StateListDrawable) obtainStyledAttributes.getDrawable(a.i.c.RecyclerView_fastScrollHorizontalThumbDrawable), obtainStyledAttributes.getDrawable(a.i.c.RecyclerView_fastScrollHorizontalTrackDrawable));
        }
        obtainStyledAttributes.recycle();
        a(context, string, attributeSet, i2, 0);
        if (Build.VERSION.SDK_INT >= 21) {
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, f1110a, i2, 0);
            if (Build.VERSION.SDK_INT >= 29) {
                saveAttributeDataForStyleable(context, f1110a, attributeSet, obtainStyledAttributes2, i2, 0);
            }
            z2 = obtainStyledAttributes2.getBoolean(0, true);
            obtainStyledAttributes2.recycle();
        }
        setNestedScrollingEnabled(z2);
    }

    private void A() {
        int i2 = this.I;
        this.I = 0;
        if (i2 != 0 && n()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain();
            obtain.setEventType(2048);
            androidx.core.view.a.b.a(obtain, i2);
            sendAccessibilityEventUnchecked(obtain);
        }
    }

    private void B() {
        boolean z2 = true;
        this.qa.a(1);
        a(this.qa);
        this.qa.j = false;
        x();
        this.o.a();
        r();
        J();
        O();
        r rVar = this.qa;
        if (!rVar.k || !this.ua) {
            z2 = false;
        }
        rVar.i = z2;
        this.ua = false;
        this.ta = false;
        r rVar2 = this.qa;
        rVar2.h = rVar2.l;
        rVar2.f = this.u.getItemCount();
        a(this.za);
        if (this.qa.k) {
            int a2 = this.n.a();
            for (int i2 = 0; i2 < a2; i2++) {
                u h2 = h(this.n.c(i2));
                if (!h2.shouldIgnore() && (!h2.isInvalid() || this.u.hasStableIds())) {
                    this.o.c(h2, this.V.a(this.qa, h2, ItemAnimator.a(h2), h2.getUnmodifiedPayloads()));
                    if (this.qa.i && h2.isUpdated() && !h2.isRemoved() && !h2.shouldIgnore() && !h2.isInvalid()) {
                        this.o.a(d(h2), h2);
                    }
                }
            }
        }
        if (this.qa.l) {
            w();
            r rVar3 = this.qa;
            boolean z3 = rVar3.g;
            rVar3.g = false;
            this.v.e(this.k, rVar3);
            this.qa.g = z3;
            for (int i3 = 0; i3 < this.n.a(); i3++) {
                u h3 = h(this.n.c(i3));
                if (!h3.shouldIgnore() && !this.o.c(h3)) {
                    int a3 = ItemAnimator.a(h3);
                    boolean hasAnyOfTheFlags = h3.hasAnyOfTheFlags(8192);
                    if (!hasAnyOfTheFlags) {
                        a3 |= MpegAudioHeader.MAX_FRAME_SIZE_BYTES;
                    }
                    ItemAnimator.c a4 = this.V.a(this.qa, h3, a3, h3.getUnmodifiedPayloads());
                    if (hasAnyOfTheFlags) {
                        a(h3, a4);
                    } else {
                        this.o.a(h3, a4);
                    }
                }
            }
        }
        a();
        s();
        c(false);
        this.qa.e = 2;
    }

    private void C() {
        x();
        r();
        this.qa.a(6);
        this.m.b();
        this.qa.f = this.u.getItemCount();
        r rVar = this.qa;
        rVar.f1149d = 0;
        rVar.h = false;
        this.v.e(this.k, rVar);
        r rVar2 = this.qa;
        rVar2.g = false;
        this.l = null;
        rVar2.k = rVar2.k && this.V != null;
        this.qa.e = 4;
        s();
        c(false);
    }

    private void D() {
        this.qa.a(4);
        x();
        r();
        r rVar = this.qa;
        rVar.e = 1;
        if (rVar.k) {
            for (int a2 = this.n.a() - 1; a2 >= 0; a2--) {
                u h2 = h(this.n.c(a2));
                if (!h2.shouldIgnore()) {
                    long d2 = d(h2);
                    ItemAnimator.c a3 = this.V.a(this.qa, h2);
                    u a4 = this.o.a(d2);
                    if (a4 != null && !a4.shouldIgnore()) {
                        boolean b2 = this.o.b(a4);
                        boolean b3 = this.o.b(h2);
                        if (!b2 || a4 != h2) {
                            ItemAnimator.c f2 = this.o.f(a4);
                            this.o.b(h2, a3);
                            ItemAnimator.c e2 = this.o.e(h2);
                            if (f2 == null) {
                                a(d2, h2, a4);
                            } else {
                                a(a4, h2, f2, e2, b2, b3);
                            }
                        }
                    }
                    this.o.b(h2, a3);
                }
            }
            this.o.a(this.Ga);
        }
        this.v.c(this.k);
        r rVar2 = this.qa;
        rVar2.f1148c = rVar2.f;
        this.M = false;
        this.N = false;
        rVar2.k = false;
        rVar2.l = false;
        this.v.h = false;
        ArrayList<u> arrayList = this.k.f1142b;
        if (arrayList != null) {
            arrayList.clear();
        }
        g gVar = this.v;
        if (gVar.n) {
            gVar.m = 0;
            gVar.n = false;
            this.k.j();
        }
        this.v.g(this.qa);
        s();
        c(false);
        this.o.a();
        int[] iArr = this.za;
        if (k(iArr[0], iArr[1])) {
            d(0, 0);
        }
        K();
        M();
    }

    @Nullable
    private View E() {
        u b2;
        int i2 = this.qa.m;
        if (i2 == -1) {
            i2 = 0;
        }
        int a2 = this.qa.a();
        int i3 = i2;
        while (i3 < a2) {
            u b3 = b(i3);
            if (b3 == null) {
                break;
            } else if (b3.itemView.hasFocusable()) {
                return b3.itemView;
            } else {
                i3++;
            }
        }
        int min = Math.min(a2, i2);
        while (true) {
            min--;
            if (min < 0 || (b2 = b(min)) == null) {
                return null;
            }
            if (b2.itemView.hasFocusable()) {
                return b2.itemView;
            }
        }
    }

    private boolean F() {
        int a2 = this.n.a();
        for (int i2 = 0; i2 < a2; i2++) {
            u h2 = h(this.n.c(i2));
            if (h2 != null && !h2.shouldIgnore() && h2.isUpdated()) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint({"InlinedApi"})
    private void G() {
        if (ViewCompat.i(this) == 0) {
            ViewCompat.c(this, 8);
        }
    }

    private void H() {
        this.n = new C0163d(new G(this));
    }

    private boolean I() {
        return this.V != null && this.v.D();
    }

    private void J() {
        if (this.M) {
            this.m.f();
            if (this.N) {
                this.v.d(this);
            }
        }
        if (I()) {
            this.m.e();
        } else {
            this.m.b();
        }
        boolean z2 = false;
        boolean z3 = this.ta || this.ua;
        this.qa.k = this.D && this.V != null && (this.M || z3 || this.v.h) && (!this.M || this.u.hasStableIds());
        r rVar = this.qa;
        if (rVar.k && z3 && !this.M && I()) {
            z2 = true;
        }
        rVar.l = z2;
    }

    private void K() {
        View view;
        if (this.ma && this.u != null && hasFocus() && getDescendantFocusability() != 393216) {
            if (getDescendantFocusability() != 131072 || !isFocused()) {
                if (!isFocused()) {
                    View focusedChild = getFocusedChild();
                    if (!g || (focusedChild.getParent() != null && focusedChild.hasFocus())) {
                        if (!this.n.c(focusedChild)) {
                            return;
                        }
                    } else if (this.n.a() == 0) {
                        requestFocus();
                        return;
                    }
                }
                View view2 = null;
                u a2 = (this.qa.n == -1 || !this.u.hasStableIds()) ? null : a(this.qa.n);
                if (a2 != null && !this.n.c(a2.itemView) && a2.itemView.hasFocusable()) {
                    view2 = a2.itemView;
                } else if (this.n.a() > 0) {
                    view2 = E();
                }
                if (view2 != null) {
                    int i2 = this.qa.o;
                    if (((long) i2) == -1 || (view = view2.findViewById(i2)) == null || !view.isFocusable()) {
                        view = view2;
                    }
                    view.requestFocus();
                }
            }
        }
    }

    private void L() {
        boolean z2;
        EdgeEffect edgeEffect = this.R;
        if (edgeEffect != null) {
            edgeEffect.onRelease();
            z2 = this.R.isFinished();
        } else {
            z2 = false;
        }
        EdgeEffect edgeEffect2 = this.S;
        if (edgeEffect2 != null) {
            edgeEffect2.onRelease();
            z2 |= this.S.isFinished();
        }
        EdgeEffect edgeEffect3 = this.T;
        if (edgeEffect3 != null) {
            edgeEffect3.onRelease();
            z2 |= this.T.isFinished();
        }
        EdgeEffect edgeEffect4 = this.U;
        if (edgeEffect4 != null) {
            edgeEffect4.onRelease();
            z2 |= this.U.isFinished();
        }
        if (z2) {
            ViewCompat.u(this);
        }
    }

    private void M() {
        r rVar = this.qa;
        rVar.n = -1;
        rVar.m = -1;
        rVar.o = -1;
    }

    private void N() {
        VelocityTracker velocityTracker = this.ba;
        if (velocityTracker != null) {
            velocityTracker.clear();
        }
        g(0);
        L();
    }

    private void O() {
        u uVar = null;
        View focusedChild = (!this.ma || !hasFocus() || this.u == null) ? null : getFocusedChild();
        if (focusedChild != null) {
            uVar = d(focusedChild);
        }
        if (uVar == null) {
            M();
            return;
        }
        this.qa.n = this.u.hasStableIds() ? uVar.getItemId() : -1;
        this.qa.m = this.M ? -1 : uVar.isRemoved() ? uVar.mOldPosition : uVar.getAdapterPosition();
        this.qa.o = m(uVar.itemView);
    }

    private void P() {
        this.na.b();
        g gVar = this.v;
        if (gVar != null) {
            gVar.C();
        }
    }

    private String a(Context context, String str) {
        if (str.charAt(0) == '.') {
            return context.getPackageName() + str;
        } else if (str.contains(".")) {
            return str;
        } else {
            return RecyclerView.class.getPackage().getName() + '.' + str;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x003d  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0053  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(float r7, float r8, float r9, float r10) {
        /*
            r6 = this;
            r0 = 0
            int r1 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            r2 = 1065353216(0x3f800000, float:1.0)
            r3 = 1
            if (r1 >= 0) goto L_0x0021
            r6.f()
            android.widget.EdgeEffect r1 = r6.R
            float r4 = -r8
            int r5 = r6.getWidth()
            float r5 = (float) r5
            float r4 = r4 / r5
            int r5 = r6.getHeight()
            float r5 = (float) r5
            float r9 = r9 / r5
            float r9 = r2 - r9
        L_0x001c:
            androidx.core.widget.d.a(r1, r4, r9)
            r9 = r3
            goto L_0x0039
        L_0x0021:
            int r1 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r1 <= 0) goto L_0x0038
            r6.g()
            android.widget.EdgeEffect r1 = r6.T
            int r4 = r6.getWidth()
            float r4 = (float) r4
            float r4 = r8 / r4
            int r5 = r6.getHeight()
            float r5 = (float) r5
            float r9 = r9 / r5
            goto L_0x001c
        L_0x0038:
            r9 = 0
        L_0x0039:
            int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r1 >= 0) goto L_0x0053
            r6.h()
            android.widget.EdgeEffect r9 = r6.S
            float r1 = -r10
            int r2 = r6.getHeight()
            float r2 = (float) r2
            float r1 = r1 / r2
            int r2 = r6.getWidth()
            float r2 = (float) r2
            float r7 = r7 / r2
            androidx.core.widget.d.a(r9, r1, r7)
            goto L_0x006f
        L_0x0053:
            int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r1 <= 0) goto L_0x006e
            r6.e()
            android.widget.EdgeEffect r9 = r6.U
            int r1 = r6.getHeight()
            float r1 = (float) r1
            float r1 = r10 / r1
            int r4 = r6.getWidth()
            float r4 = (float) r4
            float r7 = r7 / r4
            float r2 = r2 - r7
            androidx.core.widget.d.a(r9, r1, r2)
            goto L_0x006f
        L_0x006e:
            r3 = r9
        L_0x006f:
            if (r3 != 0) goto L_0x0079
            int r7 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r7 != 0) goto L_0x0079
            int r7 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
            if (r7 == 0) goto L_0x007c
        L_0x0079:
            androidx.core.view.ViewCompat.u(r6)
        L_0x007c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.a(float, float, float, float):void");
    }

    private void a(long j2, u uVar, u uVar2) {
        int a2 = this.n.a();
        for (int i2 = 0; i2 < a2; i2++) {
            u h2 = h(this.n.c(i2));
            if (h2 != uVar && d(h2) == j2) {
                a aVar = this.u;
                if (aVar == null || !aVar.hasStableIds()) {
                    throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + h2 + " \n View Holder 2:" + uVar + i());
                }
                throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + h2 + " \n View Holder 2:" + uVar + i());
            }
        }
        Log.e("RecyclerView", "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + uVar2 + " cannot be found but it is necessary for " + uVar + i());
    }

    private void a(Context context, String str, AttributeSet attributeSet, int i2, int i3) {
        Constructor<? extends U> constructor;
        if (str != null) {
            String trim = str.trim();
            if (!trim.isEmpty()) {
                String a2 = a(context, trim);
                try {
                    Class<? extends U> asSubclass = Class.forName(a2, false, isInEditMode() ? getClass().getClassLoader() : context.getClassLoader()).asSubclass(g.class);
                    Object[] objArr = null;
                    try {
                        constructor = asSubclass.getConstructor(h);
                        objArr = new Object[]{context, attributeSet, Integer.valueOf(i2), Integer.valueOf(i3)};
                    } catch (NoSuchMethodException e2) {
                        constructor = asSubclass.getConstructor(new Class[0]);
                    }
                    constructor.setAccessible(true);
                    setLayoutManager((g) constructor.newInstance(objArr));
                } catch (NoSuchMethodException e3) {
                    e3.initCause(e2);
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Error creating LayoutManager " + a2, e3);
                } catch (ClassNotFoundException e4) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Unable to find LayoutManager " + a2, e4);
                } catch (InvocationTargetException e5) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + a2, e5);
                } catch (InstantiationException e6) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + a2, e6);
                } catch (IllegalAccessException e7) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Cannot access non-public constructor " + a2, e7);
                } catch (ClassCastException e8) {
                    throw new IllegalStateException(attributeSet.getPositionDescription() + ": Class is not a LayoutManager " + a2, e8);
                }
            }
        }
    }

    static void a(View view, Rect rect) {
        h hVar = (h) view.getLayoutParams();
        Rect rect2 = hVar.f1132b;
        rect.set((view.getLeft() - rect2.left) - hVar.leftMargin, (view.getTop() - rect2.top) - hVar.topMargin, view.getRight() + rect2.right + hVar.rightMargin, view.getBottom() + rect2.bottom + hVar.bottomMargin);
    }

    private void a(@NonNull View view, @Nullable View view2) {
        View view3 = view2 != null ? view2 : view;
        this.r.set(0, 0, view3.getWidth(), view3.getHeight());
        ViewGroup.LayoutParams layoutParams = view3.getLayoutParams();
        if (layoutParams instanceof h) {
            h hVar = (h) layoutParams;
            if (!hVar.f1133c) {
                Rect rect = hVar.f1132b;
                Rect rect2 = this.r;
                rect2.left -= rect.left;
                rect2.right += rect.right;
                rect2.top -= rect.top;
                rect2.bottom += rect.bottom;
            }
        }
        if (view2 != null) {
            offsetDescendantRectToMyCoords(view2, this.r);
            offsetRectIntoDescendantCoords(view, this.r);
        }
        this.v.a(this, view, this.r, !this.D, view2 == null);
    }

    private void a(@Nullable a aVar, boolean z2, boolean z3) {
        a aVar2 = this.u;
        if (aVar2 != null) {
            aVar2.unregisterAdapterDataObserver(this.j);
            this.u.onDetachedFromRecyclerView(this);
        }
        if (!z2 || z3) {
            u();
        }
        this.m.f();
        a aVar3 = this.u;
        this.u = aVar;
        if (aVar != null) {
            aVar.registerAdapterDataObserver(this.j);
            aVar.onAttachedToRecyclerView(this);
        }
        g gVar = this.v;
        if (gVar != null) {
            gVar.a(aVar3, this.u);
        }
        this.k.a(aVar3, this.u, z2);
        this.qa.g = true;
    }

    private void a(@NonNull u uVar, @NonNull u uVar2, @NonNull ItemAnimator.c cVar, @NonNull ItemAnimator.c cVar2, boolean z2, boolean z3) {
        uVar.setIsRecyclable(false);
        if (z2) {
            e(uVar);
        }
        if (uVar != uVar2) {
            if (z3) {
                e(uVar2);
            }
            uVar.mShadowedHolder = uVar2;
            e(uVar);
            this.k.c(uVar);
            uVar2.setIsRecyclable(false);
            uVar2.mShadowingHolder = uVar;
        }
        if (this.V.a(uVar, uVar2, cVar, cVar2)) {
            t();
        }
    }

    private void a(int[] iArr) {
        int a2 = this.n.a();
        if (a2 == 0) {
            iArr[0] = -1;
            iArr[1] = -1;
            return;
        }
        int i2 = Integer.MIN_VALUE;
        int i3 = Integer.MAX_VALUE;
        for (int i4 = 0; i4 < a2; i4++) {
            u h2 = h(this.n.c(i4));
            if (!h2.shouldIgnore()) {
                int layoutPosition = h2.getLayoutPosition();
                if (layoutPosition < i3) {
                    i3 = layoutPosition;
                }
                if (layoutPosition > i2) {
                    i2 = layoutPosition;
                }
            }
        }
        iArr[0] = i3;
        iArr[1] = i2;
    }

    private boolean a(MotionEvent motionEvent) {
        k kVar = this.z;
        if (kVar != null) {
            kVar.a(this, motionEvent);
            int action = motionEvent.getAction();
            if (action == 3 || action == 1) {
                this.z = null;
            }
            return true;
        } else if (motionEvent.getAction() == 0) {
            return false;
        } else {
            return b(motionEvent);
        }
    }

    private boolean a(View view, View view2, int i2) {
        int i3;
        if (view2 == null || view2 == this || c(view2) == null) {
            return false;
        }
        if (view == null || c(view) == null) {
            return true;
        }
        this.r.set(0, 0, view.getWidth(), view.getHeight());
        this.s.set(0, 0, view2.getWidth(), view2.getHeight());
        offsetDescendantRectToMyCoords(view, this.r);
        offsetDescendantRectToMyCoords(view2, this.s);
        char c2 = 65535;
        int i4 = this.v.k() == 1 ? -1 : 1;
        Rect rect = this.r;
        int i5 = rect.left;
        int i6 = this.s.left;
        if ((i5 < i6 || rect.right <= i6) && this.r.right < this.s.right) {
            i3 = 1;
        } else {
            Rect rect2 = this.r;
            int i7 = rect2.right;
            int i8 = this.s.right;
            i3 = ((i7 > i8 || rect2.left >= i8) && this.r.left > this.s.left) ? -1 : 0;
        }
        Rect rect3 = this.r;
        int i9 = rect3.top;
        int i10 = this.s.top;
        if ((i9 < i10 || rect3.bottom <= i10) && this.r.bottom < this.s.bottom) {
            c2 = 1;
        } else {
            Rect rect4 = this.r;
            int i11 = rect4.bottom;
            int i12 = this.s.bottom;
            if ((i11 <= i12 && rect4.top < i12) || this.r.top <= this.s.top) {
                c2 = 0;
            }
        }
        if (i2 == 1) {
            return c2 < 0 || (c2 == 0 && i3 * i4 <= 0);
        }
        if (i2 == 2) {
            return c2 > 0 || (c2 == 0 && i3 * i4 >= 0);
        }
        if (i2 == 17) {
            return i3 < 0;
        }
        if (i2 == 33) {
            return c2 < 0;
        }
        if (i2 == 66) {
            return i3 > 0;
        }
        if (i2 == 130) {
            return c2 > 0;
        }
        throw new IllegalArgumentException("Invalid direction: " + i2 + i());
    }

    static void b(@NonNull u uVar) {
        WeakReference<RecyclerView> weakReference = uVar.mNestedRecyclerView;
        if (weakReference != null) {
            Object obj = weakReference.get();
            while (true) {
                View view = (View) obj;
                while (true) {
                    if (view == null) {
                        uVar.mNestedRecyclerView = null;
                        return;
                    } else if (view != uVar.itemView) {
                        obj = view.getParent();
                        if (!(obj instanceof View)) {
                            view = null;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private boolean b(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int size = this.y.size();
        int i2 = 0;
        while (i2 < size) {
            k kVar = this.y.get(i2);
            if (!kVar.b(this, motionEvent) || action == 3) {
                i2++;
            } else {
                this.z = kVar;
                return true;
            }
        }
        return false;
    }

    private void c(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.aa) {
            int i2 = actionIndex == 0 ? 1 : 0;
            this.aa = motionEvent.getPointerId(i2);
            int x2 = (int) (motionEvent.getX(i2) + 0.5f);
            this.ea = x2;
            this.ca = x2;
            int y2 = (int) (motionEvent.getY(i2) + 0.5f);
            this.fa = y2;
            this.da = y2;
        }
    }

    @Nullable
    static RecyclerView e(@NonNull View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        if (view instanceof RecyclerView) {
            return (RecyclerView) view;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            RecyclerView e2 = e(viewGroup.getChildAt(i2));
            if (e2 != null) {
                return e2;
            }
        }
        return null;
    }

    private void e(u uVar) {
        View view = uVar.itemView;
        boolean z2 = view.getParent() == this;
        this.k.c(g(view));
        if (uVar.isTmpDetached()) {
            this.n.a(view, -1, view.getLayoutParams(), true);
        } else if (!z2) {
            this.n.a(view, true);
        } else {
            this.n.a(view);
        }
    }

    private androidx.core.view.l getScrollingChildHelper() {
        if (this.Aa == null) {
            this.Aa = new androidx.core.view.l(this);
        }
        return this.Aa;
    }

    static u h(View view) {
        if (view == null) {
            return null;
        }
        return ((h) view.getLayoutParams()).f1131a;
    }

    private boolean k(int i2, int i3) {
        a(this.za);
        int[] iArr = this.za;
        return (iArr[0] == i2 && iArr[1] == i3) ? false : true;
    }

    private int m(View view) {
        int id;
        loop0:
        while (true) {
            id = view.getId();
            while (true) {
                if (view.isFocused() || !(view instanceof ViewGroup) || !view.hasFocus()) {
                    return id;
                }
                view = ((ViewGroup) view).getFocusedChild();
                if (view.getId() != -1) {
                }
            }
        }
        return id;
    }

    private void z() {
        N();
        setScrollState(0);
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public u a(int i2, boolean z2) {
        int b2 = this.n.b();
        u uVar = null;
        for (int i3 = 0; i3 < b2; i3++) {
            u h2 = h(this.n.d(i3));
            if (h2 != null && !h2.isRemoved()) {
                if (z2) {
                    if (h2.mPosition != i2) {
                        continue;
                    }
                } else if (h2.getLayoutPosition() != i2) {
                    continue;
                }
                if (!this.n.c(h2.itemView)) {
                    return h2;
                }
                uVar = h2;
            }
        }
        return uVar;
    }

    public u a(long j2) {
        a aVar = this.u;
        u uVar = null;
        if (aVar != null && aVar.hasStableIds()) {
            int b2 = this.n.b();
            for (int i2 = 0; i2 < b2; i2++) {
                u h2 = h(this.n.d(i2));
                if (h2 != null && !h2.isRemoved() && h2.getItemId() == j2) {
                    if (!this.n.c(h2.itemView)) {
                        return h2;
                    }
                    uVar = h2;
                }
            }
        }
        return uVar;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        int b2 = this.n.b();
        for (int i2 = 0; i2 < b2; i2++) {
            u h2 = h(this.n.d(i2));
            if (!h2.shouldIgnore()) {
                h2.clearOldPosition();
            }
        }
        this.k.b();
    }

    /* access modifiers changed from: package-private */
    public void a(int i2) {
        g gVar = this.v;
        if (gVar != null) {
            gVar.f(i2);
        }
        e(i2);
        l lVar = this.ra;
        if (lVar != null) {
            lVar.a(this, i2);
        }
        List<l> list = this.sa;
        if (list != null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                this.sa.get(size).a(this, i2);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3) {
        if (i2 < 0) {
            f();
            if (this.R.isFinished()) {
                this.R.onAbsorb(-i2);
            }
        } else if (i2 > 0) {
            g();
            if (this.T.isFinished()) {
                this.T.onAbsorb(i2);
            }
        }
        if (i3 < 0) {
            h();
            if (this.S.isFinished()) {
                this.S.onAbsorb(-i3);
            }
        } else if (i3 > 0) {
            e();
            if (this.U.isFinished()) {
                this.U.onAbsorb(i3);
            }
        }
        if (i2 != 0 || i3 != 0) {
            ViewCompat.u(this);
        }
    }

    public final void a(int i2, int i3, int i4, int i5, int[] iArr, int i6, @NonNull int[] iArr2) {
        getScrollingChildHelper().a(i2, i3, i4, i5, iArr, i6, iArr2);
    }

    public void a(@Px int i2, @Px int i3, @Nullable Interpolator interpolator) {
        a(i2, i3, interpolator, Integer.MIN_VALUE);
    }

    public void a(@Px int i2, @Px int i3, @Nullable Interpolator interpolator, int i4) {
        a(i2, i3, interpolator, i4, false);
    }

    /* access modifiers changed from: package-private */
    public void a(@Px int i2, @Px int i3, @Nullable Interpolator interpolator, int i4, boolean z2) {
        g gVar = this.v;
        if (gVar == null) {
            Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (!this.G) {
            int i5 = 0;
            if (!gVar.a()) {
                i2 = 0;
            }
            if (!this.v.b()) {
                i3 = 0;
            }
            if (i2 != 0 || i3 != 0) {
                if (i4 == Integer.MIN_VALUE || i4 > 0) {
                    if (z2) {
                        if (i2 != 0) {
                            i5 = 1;
                        }
                        if (i3 != 0) {
                            i5 |= 2;
                        }
                        j(i5, 1);
                    }
                    this.na.a(i2, i3, i4, interpolator);
                    return;
                }
                scrollBy(i2, i3);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3, Object obj) {
        int i4;
        int b2 = this.n.b();
        int i5 = i2 + i3;
        for (int i6 = 0; i6 < b2; i6++) {
            View d2 = this.n.d(i6);
            u h2 = h(d2);
            if (h2 != null && !h2.shouldIgnore() && (i4 = h2.mPosition) >= i2 && i4 < i5) {
                h2.addFlags(2);
                h2.addChangePayload(obj);
                ((h) d2.getLayoutParams()).f1133c = true;
            }
        }
        this.k.c(i2, i3);
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3, boolean z2) {
        int i4 = i2 + i3;
        int b2 = this.n.b();
        for (int i5 = 0; i5 < b2; i5++) {
            u h2 = h(this.n.d(i5));
            if (h2 != null && !h2.shouldIgnore()) {
                int i6 = h2.mPosition;
                if (i6 >= i4) {
                    h2.offsetPosition(-i3, z2);
                } else if (i6 >= i2) {
                    h2.flagRemovedAndOffsetPosition(i2 - 1, -i3, z2);
                }
                this.qa.g = true;
            }
        }
        this.k.a(i2, i3, z2);
        requestLayout();
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3, @Nullable int[] iArr) {
        x();
        r();
        a.d.c.a.a("RV Scroll");
        a(this.qa);
        int a2 = i2 != 0 ? this.v.a(i2, this.k, this.qa) : 0;
        int b2 = i3 != 0 ? this.v.b(i3, this.k, this.qa) : 0;
        a.d.c.a.a();
        v();
        s();
        c(false);
        if (iArr != null) {
            iArr[0] = a2;
            iArr[1] = b2;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void a(StateListDrawable stateListDrawable, Drawable drawable, StateListDrawable stateListDrawable2, Drawable drawable2) {
        if (stateListDrawable == null || drawable == null || stateListDrawable2 == null || drawable2 == null) {
            throw new IllegalArgumentException("Trying to set fast scroller without both required drawables." + i());
        }
        Resources resources = getContext().getResources();
        new r(this, stateListDrawable, drawable, stateListDrawable2, drawable2, resources.getDimensionPixelSize(a.i.b.fastscroll_default_thickness), resources.getDimensionPixelSize(a.i.b.fastscroll_minimum_range), resources.getDimensionPixelOffset(a.i.b.fastscroll_margin));
    }

    /* access modifiers changed from: package-private */
    public void a(View view) {
        u h2 = h(view);
        j(view);
        a aVar = this.u;
        if (!(aVar == null || h2 == null)) {
            aVar.onViewAttachedToWindow(h2);
        }
        List<i> list = this.L;
        if (list != null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                this.L.get(size).b(view);
            }
        }
    }

    public void a(@NonNull f fVar) {
        a(fVar, -1);
    }

    public void a(@NonNull f fVar, int i2) {
        g gVar = this.v;
        if (gVar != null) {
            gVar.a("Cannot add item decoration during a scroll  or layout");
        }
        if (this.x.isEmpty()) {
            setWillNotDraw(false);
        }
        if (i2 < 0) {
            this.x.add(fVar);
        } else {
            this.x.add(i2, fVar);
        }
        p();
        requestLayout();
    }

    public void a(@NonNull k kVar) {
        this.y.add(kVar);
    }

    public void a(@NonNull l lVar) {
        if (this.sa == null) {
            this.sa = new ArrayList();
        }
        this.sa.add(lVar);
    }

    /* access modifiers changed from: package-private */
    public final void a(r rVar) {
        if (getScrollState() == 2) {
            OverScroller overScroller = this.na.f1152c;
            rVar.p = overScroller.getFinalX() - overScroller.getCurrX();
            rVar.q = overScroller.getFinalY() - overScroller.getCurrY();
            return;
        }
        rVar.p = 0;
        rVar.q = 0;
    }

    /* access modifiers changed from: package-private */
    public void a(u uVar, ItemAnimator.c cVar) {
        uVar.setFlags(0, 8192);
        if (this.qa.i && uVar.isUpdated() && !uVar.isRemoved() && !uVar.shouldIgnore()) {
            this.o.a(d(uVar), uVar);
        }
        this.o.c(uVar, cVar);
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull u uVar, @Nullable ItemAnimator.c cVar, @NonNull ItemAnimator.c cVar2) {
        uVar.setIsRecyclable(false);
        if (this.V.a(uVar, cVar, cVar2)) {
            t();
        }
    }

    /* access modifiers changed from: package-private */
    public void a(String str) {
        if (o()) {
            if (str == null) {
                throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling" + i());
            }
            throw new IllegalStateException(str);
        } else if (this.P > 0) {
            Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", new IllegalStateException("" + i()));
        }
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z2) {
        this.O--;
        if (this.O < 1) {
            this.O = 0;
            if (z2) {
                A();
                d();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a(int i2, int i3, MotionEvent motionEvent) {
        int i4;
        int i5;
        int i6;
        int i7;
        int i8 = i2;
        int i9 = i3;
        MotionEvent motionEvent2 = motionEvent;
        b();
        if (this.u != null) {
            int[] iArr = this.Da;
            iArr[0] = 0;
            iArr[1] = 0;
            a(i8, i9, iArr);
            int[] iArr2 = this.Da;
            int i10 = iArr2[0];
            int i11 = iArr2[1];
            i6 = i11;
            i7 = i10;
            i5 = i8 - i10;
            i4 = i9 - i11;
        } else {
            i7 = 0;
            i6 = 0;
            i5 = 0;
            i4 = 0;
        }
        if (!this.x.isEmpty()) {
            invalidate();
        }
        int[] iArr3 = this.Da;
        iArr3[0] = 0;
        iArr3[1] = 0;
        int i12 = i7;
        a(i7, i6, i5, i4, this.Ba, 0, iArr3);
        int[] iArr4 = this.Da;
        int i13 = i5 - iArr4[0];
        int i14 = i4 - iArr4[1];
        boolean z2 = (iArr4[0] == 0 && iArr4[1] == 0) ? false : true;
        int i15 = this.ea;
        int[] iArr5 = this.Ba;
        this.ea = i15 - iArr5[0];
        this.fa -= iArr5[1];
        int[] iArr6 = this.Ca;
        iArr6[0] = iArr6[0] + iArr5[0];
        iArr6[1] = iArr6[1] + iArr5[1];
        if (getOverScrollMode() != 2) {
            if (motionEvent2 != null && !C0130h.a(motionEvent2, 8194)) {
                a(motionEvent.getX(), (float) i13, motionEvent.getY(), (float) i14);
            }
            b(i2, i3);
        }
        int i16 = i12;
        if (!(i16 == 0 && i6 == 0)) {
            d(i16, i6);
        }
        if (!awakenScrollBars()) {
            invalidate();
        }
        return (!z2 && i16 == 0 && i6 == 0) ? false : true;
    }

    public boolean a(int i2, int i3, int[] iArr, int[] iArr2, int i4) {
        return getScrollingChildHelper().a(i2, i3, iArr, iArr2, i4);
    }

    /* access modifiers changed from: package-private */
    public boolean a(AccessibilityEvent accessibilityEvent) {
        if (!o()) {
            return false;
        }
        int a2 = accessibilityEvent != null ? androidx.core.view.a.b.a(accessibilityEvent) : 0;
        if (a2 == 0) {
            a2 = 0;
        }
        this.I = a2 | this.I;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean a(u uVar) {
        ItemAnimator itemAnimator = this.V;
        return itemAnimator == null || itemAnimator.a(uVar, uVar.getUnmodifiedPayloads());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean a(u uVar, int i2) {
        if (o()) {
            uVar.mPendingAccessibilityState = i2;
            this.Ea.add(uVar);
            return false;
        }
        ViewCompat.b(uVar.itemView, i2);
        return true;
    }

    public void addFocusables(ArrayList<View> arrayList, int i2, int i3) {
        g gVar = this.v;
        if (gVar == null || !gVar.a(this, arrayList, i2, i3)) {
            super.addFocusables(arrayList, i2, i3);
        }
    }

    @Nullable
    public u b(int i2) {
        u uVar = null;
        if (this.M) {
            return null;
        }
        int b2 = this.n.b();
        for (int i3 = 0; i3 < b2; i3++) {
            u h2 = h(this.n.d(i3));
            if (h2 != null && !h2.isRemoved() && c(h2) == i2) {
                if (!this.n.c(h2.itemView)) {
                    return h2;
                }
                uVar = h2;
            }
        }
        return uVar;
    }

    /* access modifiers changed from: package-private */
    public void b() {
        if (!this.D || this.M) {
            a.d.c.a.a("RV FullInvalidate");
            c();
            a.d.c.a.a();
        } else if (this.m.c()) {
            if (this.m.c(4) && !this.m.c(11)) {
                a.d.c.a.a("RV PartialInvalidate");
                x();
                r();
                this.m.e();
                if (!this.F) {
                    if (F()) {
                        c();
                    } else {
                        this.m.a();
                    }
                }
                c(true);
                s();
            } else if (this.m.c()) {
                a.d.c.a.a("RV FullInvalidate");
                c();
            } else {
                return;
            }
            a.d.c.a.a();
        }
    }

    /* access modifiers changed from: package-private */
    public void b(int i2, int i3) {
        boolean z2;
        EdgeEffect edgeEffect = this.R;
        if (edgeEffect == null || edgeEffect.isFinished() || i2 <= 0) {
            z2 = false;
        } else {
            this.R.onRelease();
            z2 = this.R.isFinished();
        }
        EdgeEffect edgeEffect2 = this.T;
        if (edgeEffect2 != null && !edgeEffect2.isFinished() && i2 < 0) {
            this.T.onRelease();
            z2 |= this.T.isFinished();
        }
        EdgeEffect edgeEffect3 = this.S;
        if (edgeEffect3 != null && !edgeEffect3.isFinished() && i3 > 0) {
            this.S.onRelease();
            z2 |= this.S.isFinished();
        }
        EdgeEffect edgeEffect4 = this.U;
        if (edgeEffect4 != null && !edgeEffect4.isFinished() && i3 < 0) {
            this.U.onRelease();
            z2 |= this.U.isFinished();
        }
        if (z2) {
            ViewCompat.u(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void b(View view) {
        u h2 = h(view);
        k(view);
        a aVar = this.u;
        if (!(aVar == null || h2 == null)) {
            aVar.onViewDetachedFromWindow(h2);
        }
        List<i> list = this.L;
        if (list != null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                this.L.get(size).a(view);
            }
        }
    }

    public void b(@NonNull f fVar) {
        g gVar = this.v;
        if (gVar != null) {
            gVar.a("Cannot remove item decoration during a scroll  or layout");
        }
        this.x.remove(fVar);
        if (this.x.isEmpty()) {
            setWillNotDraw(getOverScrollMode() == 2);
        }
        p();
        requestLayout();
    }

    public void b(@NonNull k kVar) {
        this.y.remove(kVar);
        if (this.z == kVar) {
            this.z = null;
        }
    }

    public void b(@NonNull l lVar) {
        List<l> list = this.sa;
        if (list != null) {
            list.remove(lVar);
        }
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull u uVar, @NonNull ItemAnimator.c cVar, @Nullable ItemAnimator.c cVar2) {
        e(uVar);
        uVar.setIsRecyclable(false);
        if (this.V.b(uVar, cVar, cVar2)) {
            t();
        }
    }

    /* access modifiers changed from: package-private */
    public void b(boolean z2) {
        this.N = z2 | this.N;
        this.M = true;
        q();
    }

    /* access modifiers changed from: package-private */
    public int c(u uVar) {
        if (uVar.hasAnyOfTheFlags(524) || !uVar.isBound()) {
            return -1;
        }
        return this.m.a(uVar.mPosition);
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0013 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    @androidx.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View c(@androidx.annotation.NonNull android.view.View r3) {
        /*
            r2 = this;
        L_0x0000:
            android.view.ViewParent r0 = r3.getParent()
            if (r0 == 0) goto L_0x0010
            if (r0 == r2) goto L_0x0010
            boolean r1 = r0 instanceof android.view.View
            if (r1 == 0) goto L_0x0010
            r3 = r0
            android.view.View r3 = (android.view.View) r3
            goto L_0x0000
        L_0x0010:
            if (r0 != r2) goto L_0x0013
            goto L_0x0014
        L_0x0013:
            r3 = 0
        L_0x0014:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.c(android.view.View):android.view.View");
    }

    /* access modifiers changed from: package-private */
    public void c() {
        String str;
        if (this.u == null) {
            str = "No adapter attached; skipping layout";
        } else if (this.v == null) {
            str = "No layout manager attached; skipping layout";
        } else {
            r rVar = this.qa;
            rVar.j = false;
            if (rVar.e == 1) {
                B();
            } else if (!this.m.d() && this.v.r() == getWidth() && this.v.h() == getHeight()) {
                this.v.e(this);
                D();
                return;
            }
            this.v.e(this);
            C();
            D();
            return;
        }
        Log.e("RecyclerView", str);
    }

    public void c(@Px int i2) {
        int a2 = this.n.a();
        for (int i3 = 0; i3 < a2; i3++) {
            this.n.c(i3).offsetLeftAndRight(i2);
        }
    }

    /* access modifiers changed from: package-private */
    public void c(int i2, int i3) {
        setMeasuredDimension(g.a(i2, getPaddingLeft() + getPaddingRight(), ViewCompat.l(this)), g.a(i3, getPaddingTop() + getPaddingBottom(), ViewCompat.k(this)));
    }

    /* access modifiers changed from: package-private */
    public void c(boolean z2) {
        if (this.E < 1) {
            this.E = 1;
        }
        if (!z2 && !this.G) {
            this.F = false;
        }
        if (this.E == 1) {
            if (z2 && this.F && !this.G && this.v != null && this.u != null) {
                c();
            }
            if (!this.G) {
                this.F = false;
            }
        }
        this.E--;
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof h) && this.v.a((h) layoutParams);
    }

    public int computeHorizontalScrollExtent() {
        g gVar = this.v;
        if (gVar != null && gVar.a()) {
            return this.v.a(this.qa);
        }
        return 0;
    }

    public int computeHorizontalScrollOffset() {
        g gVar = this.v;
        if (gVar != null && gVar.a()) {
            return this.v.b(this.qa);
        }
        return 0;
    }

    public int computeHorizontalScrollRange() {
        g gVar = this.v;
        if (gVar != null && gVar.a()) {
            return this.v.c(this.qa);
        }
        return 0;
    }

    public int computeVerticalScrollExtent() {
        g gVar = this.v;
        if (gVar != null && gVar.b()) {
            return this.v.d(this.qa);
        }
        return 0;
    }

    public int computeVerticalScrollOffset() {
        g gVar = this.v;
        if (gVar != null && gVar.b()) {
            return this.v.e(this.qa);
        }
        return 0;
    }

    public int computeVerticalScrollRange() {
        g gVar = this.v;
        if (gVar != null && gVar.b()) {
            return this.v.f(this.qa);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public long d(u uVar) {
        return this.u.hasStableIds() ? uVar.getItemId() : (long) uVar.mPosition;
    }

    @Nullable
    public u d(@NonNull View view) {
        View c2 = c(view);
        if (c2 == null) {
            return null;
        }
        return g(c2);
    }

    /* access modifiers changed from: package-private */
    public void d() {
        int i2;
        for (int size = this.Ea.size() - 1; size >= 0; size--) {
            u uVar = this.Ea.get(size);
            if (uVar.itemView.getParent() == this && !uVar.shouldIgnore() && (i2 = uVar.mPendingAccessibilityState) != -1) {
                ViewCompat.b(uVar.itemView, i2);
                uVar.mPendingAccessibilityState = -1;
            }
        }
        this.Ea.clear();
    }

    public void d(@Px int i2) {
        int a2 = this.n.a();
        for (int i3 = 0; i3 < a2; i3++) {
            this.n.c(i3).offsetTopAndBottom(i2);
        }
    }

    /* access modifiers changed from: package-private */
    public void d(int i2, int i3) {
        this.P++;
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        onScrollChanged(scrollX, scrollY, scrollX - i2, scrollY - i3);
        h(i2, i3);
        l lVar = this.ra;
        if (lVar != null) {
            lVar.a(this, i2, i3);
        }
        List<l> list = this.sa;
        if (list != null) {
            for (int size = list.size() - 1; size >= 0; size--) {
                this.sa.get(size).a(this, i2, i3);
            }
        }
        this.P--;
    }

    public boolean dispatchNestedFling(float f2, float f3, boolean z2) {
        return getScrollingChildHelper().a(f2, f3, z2);
    }

    public boolean dispatchNestedPreFling(float f2, float f3) {
        return getScrollingChildHelper().a(f2, f3);
    }

    public boolean dispatchNestedPreScroll(int i2, int i3, int[] iArr, int[] iArr2) {
        return getScrollingChildHelper().a(i2, i3, iArr, iArr2);
    }

    public boolean dispatchNestedScroll(int i2, int i3, int i4, int i5, int[] iArr) {
        return getScrollingChildHelper().a(i2, i3, i4, i5, iArr);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchThawSelfOnly(sparseArray);
    }

    /* access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> sparseArray) {
        dispatchFreezeSelfOnly(sparseArray);
    }

    public void draw(Canvas canvas) {
        boolean z2;
        boolean z3;
        int i2;
        float f2;
        super.draw(canvas);
        int size = this.x.size();
        boolean z4 = false;
        for (int i3 = 0; i3 < size; i3++) {
            this.x.get(i3).b(canvas, this, this.qa);
        }
        EdgeEffect edgeEffect = this.R;
        if (edgeEffect == null || edgeEffect.isFinished()) {
            z2 = false;
        } else {
            int save = canvas.save();
            int paddingBottom = this.p ? getPaddingBottom() : 0;
            canvas.rotate(270.0f);
            canvas.translate((float) ((-getHeight()) + paddingBottom), 0.0f);
            EdgeEffect edgeEffect2 = this.R;
            z2 = edgeEffect2 != null && edgeEffect2.draw(canvas);
            canvas.restoreToCount(save);
        }
        EdgeEffect edgeEffect3 = this.S;
        if (edgeEffect3 != null && !edgeEffect3.isFinished()) {
            int save2 = canvas.save();
            if (this.p) {
                canvas.translate((float) getPaddingLeft(), (float) getPaddingTop());
            }
            EdgeEffect edgeEffect4 = this.S;
            z2 |= edgeEffect4 != null && edgeEffect4.draw(canvas);
            canvas.restoreToCount(save2);
        }
        EdgeEffect edgeEffect5 = this.T;
        if (edgeEffect5 != null && !edgeEffect5.isFinished()) {
            int save3 = canvas.save();
            int width = getWidth();
            int paddingTop = this.p ? getPaddingTop() : 0;
            canvas.rotate(90.0f);
            canvas.translate((float) (-paddingTop), (float) (-width));
            EdgeEffect edgeEffect6 = this.T;
            z2 |= edgeEffect6 != null && edgeEffect6.draw(canvas);
            canvas.restoreToCount(save3);
        }
        EdgeEffect edgeEffect7 = this.U;
        if (edgeEffect7 == null || edgeEffect7.isFinished()) {
            z3 = z2;
        } else {
            int save4 = canvas.save();
            canvas.rotate(180.0f);
            if (this.p) {
                f2 = (float) ((-getWidth()) + getPaddingRight());
                i2 = (-getHeight()) + getPaddingBottom();
            } else {
                f2 = (float) (-getWidth());
                i2 = -getHeight();
            }
            canvas.translate(f2, (float) i2);
            EdgeEffect edgeEffect8 = this.U;
            if (edgeEffect8 != null && edgeEffect8.draw(canvas)) {
                z4 = true;
            }
            z3 = z4 | z2;
            canvas.restoreToCount(save4);
        }
        if (!z3 && this.V != null && this.x.size() > 0 && this.V.g()) {
            z3 = true;
        }
        if (z3) {
            ViewCompat.u(this);
        }
    }

    public boolean drawChild(Canvas canvas, View view, long j2) {
        return super.drawChild(canvas, view, j2);
    }

    /* access modifiers changed from: package-private */
    public void e() {
        int i2;
        int i3;
        EdgeEffect edgeEffect;
        if (this.U == null) {
            this.U = this.Q.a(this, 3);
            if (this.p) {
                edgeEffect = this.U;
                i3 = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                i2 = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            } else {
                edgeEffect = this.U;
                i3 = getMeasuredWidth();
                i2 = getMeasuredHeight();
            }
            edgeEffect.setSize(i3, i2);
        }
    }

    public void e(int i2) {
    }

    public boolean e(int i2, int i3) {
        g gVar = this.v;
        int i4 = 0;
        if (gVar == null) {
            Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
            return false;
        } else if (this.G) {
            return false;
        } else {
            boolean a2 = gVar.a();
            boolean b2 = this.v.b();
            if (!a2 || Math.abs(i2) < this.ia) {
                i2 = 0;
            }
            if (!b2 || Math.abs(i3) < this.ia) {
                i3 = 0;
            }
            if (i2 == 0 && i3 == 0) {
                return false;
            }
            float f2 = (float) i2;
            float f3 = (float) i3;
            if (!dispatchNestedPreFling(f2, f3)) {
                boolean z2 = a2 || b2;
                dispatchNestedFling(f2, f3, z2);
                j jVar = this.ha;
                if (jVar != null && jVar.a(i2, i3)) {
                    return true;
                }
                if (z2) {
                    if (a2) {
                        i4 = 1;
                    }
                    if (b2) {
                        i4 |= 2;
                    }
                    j(i4, 1);
                    int i5 = this.ja;
                    int max = Math.max(-i5, Math.min(i2, i5));
                    int i6 = this.ja;
                    this.na.a(max, Math.max(-i6, Math.min(i3, i6)));
                    return true;
                }
            }
            return false;
        }
    }

    public int f(@NonNull View view) {
        u h2 = h(view);
        if (h2 != null) {
            return h2.getAdapterPosition();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void f() {
        int i2;
        int i3;
        EdgeEffect edgeEffect;
        if (this.R == null) {
            this.R = this.Q.a(this, 0);
            if (this.p) {
                edgeEffect = this.R;
                i3 = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
                i2 = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            } else {
                edgeEffect = this.R;
                i3 = getMeasuredHeight();
                i2 = getMeasuredWidth();
            }
            edgeEffect.setSize(i3, i2);
        }
    }

    public void f(int i2) {
        if (!this.G) {
            y();
            g gVar = this.v;
            if (gVar == null) {
                Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
                return;
            }
            gVar.h(i2);
            awakenScrollBars();
        }
    }

    /* access modifiers changed from: package-private */
    public void f(int i2, int i3) {
        int b2 = this.n.b();
        for (int i4 = 0; i4 < b2; i4++) {
            u h2 = h(this.n.d(i4));
            if (h2 != null && !h2.shouldIgnore() && h2.mPosition >= i2) {
                h2.offsetPosition(i3, false);
                this.qa.g = true;
            }
        }
        this.k.a(i2, i3);
        requestLayout();
    }

    public View focusSearch(View view, int i2) {
        View view2;
        boolean z2;
        View d2 = this.v.d(view, i2);
        if (d2 != null) {
            return d2;
        }
        boolean z3 = this.u != null && this.v != null && !o() && !this.G;
        FocusFinder instance = FocusFinder.getInstance();
        if (!z3 || !(i2 == 2 || i2 == 1)) {
            View findNextFocus = instance.findNextFocus(this, view, i2);
            if (findNextFocus != null || !z3) {
                view2 = findNextFocus;
            } else {
                b();
                if (c(view) == null) {
                    return null;
                }
                x();
                view2 = this.v.a(view, i2, this.k, this.qa);
                c(false);
            }
        } else {
            if (this.v.b()) {
                int i3 = i2 == 2 ? TsExtractor.TS_STREAM_TYPE_HDMV_DTS : 33;
                z2 = instance.findNextFocus(this, view, i3) == null;
                if (f) {
                    i2 = i3;
                }
            } else {
                z2 = false;
            }
            if (!z2 && this.v.a()) {
                int i4 = (this.v.k() == 1) ^ (i2 == 2) ? 66 : 17;
                z2 = instance.findNextFocus(this, view, i4) == null;
                if (f) {
                    i2 = i4;
                }
            }
            if (z2) {
                b();
                if (c(view) == null) {
                    return null;
                }
                x();
                this.v.a(view, i2, this.k, this.qa);
                c(false);
            }
            view2 = instance.findNextFocus(this, view, i2);
        }
        if (view2 == null || view2.hasFocusable()) {
            return a(view, view2, i2) ? view2 : super.focusSearch(view, i2);
        }
        if (getFocusedChild() == null) {
            return super.focusSearch(view, i2);
        }
        a(view2, (View) null);
        return view;
    }

    public u g(@NonNull View view) {
        ViewParent parent = view.getParent();
        if (parent == null || parent == this) {
            return h(view);
        }
        throw new IllegalArgumentException("View " + view + " is not a direct child of " + this);
    }

    /* access modifiers changed from: package-private */
    public void g() {
        int i2;
        int i3;
        EdgeEffect edgeEffect;
        if (this.T == null) {
            this.T = this.Q.a(this, 2);
            if (this.p) {
                edgeEffect = this.T;
                i3 = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
                i2 = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
            } else {
                edgeEffect = this.T;
                i3 = getMeasuredHeight();
                i2 = getMeasuredWidth();
            }
            edgeEffect.setSize(i3, i2);
        }
    }

    public void g(int i2) {
        getScrollingChildHelper().c(i2);
    }

    /* access modifiers changed from: package-private */
    public void g(int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int i7;
        int b2 = this.n.b();
        if (i2 < i3) {
            i5 = i3;
            i4 = -1;
            i6 = i2;
        } else {
            i5 = i2;
            i6 = i3;
            i4 = 1;
        }
        for (int i8 = 0; i8 < b2; i8++) {
            u h2 = h(this.n.d(i8));
            if (h2 != null && (i7 = h2.mPosition) >= i6 && i7 <= i5) {
                if (i7 == i2) {
                    h2.offsetPosition(i3 - i2, false);
                } else {
                    h2.offsetPosition(i4, false);
                }
                this.qa.g = true;
            }
        }
        this.k.b(i2, i3);
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateDefaultLayoutParams() {
        g gVar = this.v;
        if (gVar != null) {
            return gVar.c();
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager" + i());
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        g gVar = this.v;
        if (gVar != null) {
            return gVar.a(getContext(), attributeSet);
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager" + i());
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        g gVar = this.v;
        if (gVar != null) {
            return gVar.a(layoutParams);
        }
        throw new IllegalStateException("RecyclerView has no LayoutManager" + i());
    }

    public CharSequence getAccessibilityClassName() {
        return "androidx.recyclerview.widget.RecyclerView";
    }

    @Nullable
    public a getAdapter() {
        return this.u;
    }

    public int getBaseline() {
        g gVar = this.v;
        return gVar != null ? gVar.d() : super.getBaseline();
    }

    /* access modifiers changed from: protected */
    public int getChildDrawingOrder(int i2, int i3) {
        d dVar = this.ya;
        return dVar == null ? super.getChildDrawingOrder(i2, i3) : dVar.a(i2, i3);
    }

    public boolean getClipToPadding() {
        return this.p;
    }

    @Nullable
    public L getCompatAccessibilityDelegate() {
        return this.xa;
    }

    @NonNull
    public EdgeEffectFactory getEdgeEffectFactory() {
        return this.Q;
    }

    @Nullable
    public ItemAnimator getItemAnimator() {
        return this.V;
    }

    public int getItemDecorationCount() {
        return this.x.size();
    }

    @Nullable
    public g getLayoutManager() {
        return this.v;
    }

    public int getMaxFlingVelocity() {
        return this.ja;
    }

    public int getMinFlingVelocity() {
        return this.ia;
    }

    /* access modifiers changed from: package-private */
    public long getNanoTime() {
        if (e) {
            return System.nanoTime();
        }
        return 0;
    }

    @Nullable
    public j getOnFlingListener() {
        return this.ha;
    }

    public boolean getPreserveFocusAfterLayout() {
        return this.ma;
    }

    @NonNull
    public m getRecycledViewPool() {
        return this.k.d();
    }

    public int getScrollState() {
        return this.W;
    }

    /* access modifiers changed from: package-private */
    public void h() {
        int i2;
        int i3;
        EdgeEffect edgeEffect;
        if (this.S == null) {
            this.S = this.Q.a(this, 1);
            if (this.p) {
                edgeEffect = this.S;
                i3 = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                i2 = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            } else {
                edgeEffect = this.S;
                i3 = getMeasuredWidth();
                i2 = getMeasuredHeight();
            }
            edgeEffect.setSize(i3, i2);
        }
    }

    public void h(@Px int i2, @Px int i3) {
    }

    public boolean hasNestedScrollingParent() {
        return getScrollingChildHelper().a();
    }

    /* access modifiers changed from: package-private */
    public Rect i(View view) {
        h hVar = (h) view.getLayoutParams();
        if (!hVar.f1133c) {
            return hVar.f1132b;
        }
        if (this.qa.d() && (hVar.b() || hVar.d())) {
            return hVar.f1132b;
        }
        Rect rect = hVar.f1132b;
        rect.set(0, 0, 0, 0);
        int size = this.x.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.r.set(0, 0, 0, 0);
            this.x.get(i2).a(this.r, view, this, this.qa);
            int i3 = rect.left;
            Rect rect2 = this.r;
            rect.left = i3 + rect2.left;
            rect.top += rect2.top;
            rect.right += rect2.right;
            rect.bottom += rect2.bottom;
        }
        hVar.f1133c = false;
        return rect;
    }

    /* access modifiers changed from: package-private */
    public String i() {
        return " " + super.toString() + ", adapter:" + this.u + ", layout:" + this.v + ", context:" + getContext();
    }

    public void i(@Px int i2, @Px int i3) {
        a(i2, i3, (Interpolator) null);
    }

    public boolean isAttachedToWindow() {
        return this.A;
    }

    public final boolean isLayoutSuppressed() {
        return this.G;
    }

    public boolean isNestedScrollingEnabled() {
        return getScrollingChildHelper().b();
    }

    public void j(@NonNull View view) {
    }

    public boolean j() {
        return !this.D || this.M || this.m.c();
    }

    public boolean j(int i2, int i3) {
        return getScrollingChildHelper().a(i2, i3);
    }

    /* access modifiers changed from: package-private */
    public void k() {
        this.m = new C0160a(new H(this));
    }

    public void k(@NonNull View view) {
    }

    /* access modifiers changed from: package-private */
    public void l() {
        this.U = null;
        this.S = null;
        this.T = null;
        this.R = null;
    }

    /* access modifiers changed from: package-private */
    public boolean l(View view) {
        x();
        boolean e2 = this.n.e(view);
        if (e2) {
            u h2 = h(view);
            this.k.c(h2);
            this.k.b(h2);
        }
        c(!e2);
        return e2;
    }

    public void m() {
        if (this.x.size() != 0) {
            g gVar = this.v;
            if (gVar != null) {
                gVar.a("Cannot invalidate item decorations during a scroll or layout");
            }
            p();
            requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean n() {
        AccessibilityManager accessibilityManager = this.K;
        return accessibilityManager != null && accessibilityManager.isEnabled();
    }

    public boolean o() {
        return this.O > 0;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004f, code lost:
        if (r0 >= 30.0f) goto L_0x0053;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onAttachedToWindow() {
        /*
            r4 = this;
            super.onAttachedToWindow()
            r0 = 0
            r4.O = r0
            r1 = 1
            r4.A = r1
            boolean r2 = r4.D
            if (r2 == 0) goto L_0x0014
            boolean r2 = r4.isLayoutRequested()
            if (r2 != 0) goto L_0x0014
            goto L_0x0015
        L_0x0014:
            r1 = r0
        L_0x0015:
            r4.D = r1
            androidx.recyclerview.widget.RecyclerView$g r1 = r4.v
            if (r1 == 0) goto L_0x001e
            r1.a((androidx.recyclerview.widget.RecyclerView) r4)
        L_0x001e:
            r4.wa = r0
            boolean r0 = e
            if (r0 == 0) goto L_0x0066
            java.lang.ThreadLocal<androidx.recyclerview.widget.t> r0 = androidx.recyclerview.widget.C0178t.f1254a
            java.lang.Object r0 = r0.get()
            androidx.recyclerview.widget.t r0 = (androidx.recyclerview.widget.C0178t) r0
            r4.oa = r0
            androidx.recyclerview.widget.t r0 = r4.oa
            if (r0 != 0) goto L_0x0061
            androidx.recyclerview.widget.t r0 = new androidx.recyclerview.widget.t
            r0.<init>()
            r4.oa = r0
            android.view.Display r0 = androidx.core.view.ViewCompat.g(r4)
            r1 = 1114636288(0x42700000, float:60.0)
            boolean r2 = r4.isInEditMode()
            if (r2 != 0) goto L_0x0052
            if (r0 == 0) goto L_0x0052
            float r0 = r0.getRefreshRate()
            r2 = 1106247680(0x41f00000, float:30.0)
            int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0052
            goto L_0x0053
        L_0x0052:
            r0 = r1
        L_0x0053:
            androidx.recyclerview.widget.t r1 = r4.oa
            r2 = 1315859240(0x4e6e6b28, float:1.0E9)
            float r2 = r2 / r0
            long r2 = (long) r2
            r1.e = r2
            java.lang.ThreadLocal<androidx.recyclerview.widget.t> r0 = androidx.recyclerview.widget.C0178t.f1254a
            r0.set(r1)
        L_0x0061:
            androidx.recyclerview.widget.t r0 = r4.oa
            r0.a((androidx.recyclerview.widget.RecyclerView) r4)
        L_0x0066:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.onAttachedToWindow():void");
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        C0178t tVar;
        super.onDetachedFromWindow();
        ItemAnimator itemAnimator = this.V;
        if (itemAnimator != null) {
            itemAnimator.b();
        }
        y();
        this.A = false;
        g gVar = this.v;
        if (gVar != null) {
            gVar.a(this, this.k);
        }
        this.Ea.clear();
        removeCallbacks(this.Fa);
        this.o.b();
        if (e && (tVar = this.oa) != null) {
            tVar.b(this);
            this.oa = null;
        }
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = this.x.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.x.get(i2).a(canvas, this, this.qa);
        }
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        float f2;
        float f3;
        if (this.v != null && !this.G && motionEvent.getAction() == 8) {
            if ((motionEvent.getSource() & 2) != 0) {
                f3 = this.v.b() ? -motionEvent.getAxisValue(9) : 0.0f;
                if (this.v.a()) {
                    f2 = motionEvent.getAxisValue(10);
                    if (!(f3 == 0.0f && f2 == 0.0f)) {
                        a((int) (f2 * this.ka), (int) (f3 * this.la), motionEvent);
                    }
                }
            } else {
                if ((motionEvent.getSource() & 4194304) != 0) {
                    float axisValue = motionEvent.getAxisValue(26);
                    if (this.v.b()) {
                        f3 = -axisValue;
                    } else if (this.v.a()) {
                        f2 = axisValue;
                        f3 = 0.0f;
                        a((int) (f2 * this.ka), (int) (f3 * this.la), motionEvent);
                    }
                }
                f3 = 0.0f;
                f2 = 0.0f;
                a((int) (f2 * this.ka), (int) (f3 * this.la), motionEvent);
            }
            f2 = 0.0f;
            a((int) (f2 * this.ka), (int) (f3 * this.la), motionEvent);
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z2;
        if (this.G) {
            return false;
        }
        this.z = null;
        if (b(motionEvent)) {
            z();
            return true;
        }
        g gVar = this.v;
        if (gVar == null) {
            return false;
        }
        boolean a2 = gVar.a();
        boolean b2 = this.v.b();
        if (this.ba == null) {
            this.ba = VelocityTracker.obtain();
        }
        this.ba.addMovement(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            if (this.H) {
                this.H = false;
            }
            this.aa = motionEvent.getPointerId(0);
            int x2 = (int) (motionEvent.getX() + 0.5f);
            this.ea = x2;
            this.ca = x2;
            int y2 = (int) (motionEvent.getY() + 0.5f);
            this.fa = y2;
            this.da = y2;
            if (this.W == 2) {
                getParent().requestDisallowInterceptTouchEvent(true);
                setScrollState(1);
                g(1);
            }
            int[] iArr = this.Ca;
            iArr[1] = 0;
            iArr[0] = 0;
            int i2 = a2 ? 1 : 0;
            if (b2) {
                i2 |= 2;
            }
            j(i2, 0);
        } else if (actionMasked == 1) {
            this.ba.clear();
            g(0);
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.aa);
            if (findPointerIndex < 0) {
                Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.aa + " not found. Did any MotionEvents get skipped?");
                return false;
            }
            int x3 = (int) (motionEvent.getX(findPointerIndex) + 0.5f);
            int y3 = (int) (motionEvent.getY(findPointerIndex) + 0.5f);
            if (this.W != 1) {
                int i3 = x3 - this.ca;
                int i4 = y3 - this.da;
                if (!a2 || Math.abs(i3) <= this.ga) {
                    z2 = false;
                } else {
                    this.ea = x3;
                    z2 = true;
                }
                if (b2 && Math.abs(i4) > this.ga) {
                    this.fa = y3;
                    z2 = true;
                }
                if (z2) {
                    setScrollState(1);
                }
            }
        } else if (actionMasked == 3) {
            z();
        } else if (actionMasked == 5) {
            this.aa = motionEvent.getPointerId(actionIndex);
            int x4 = (int) (motionEvent.getX(actionIndex) + 0.5f);
            this.ea = x4;
            this.ca = x4;
            int y4 = (int) (motionEvent.getY(actionIndex) + 0.5f);
            this.fa = y4;
            this.da = y4;
        } else if (actionMasked == 6) {
            c(motionEvent);
        }
        return this.W == 1;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
        a.d.c.a.a("RV OnLayout");
        c();
        a.d.c.a.a();
        this.D = true;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i2, int i3) {
        g gVar = this.v;
        if (gVar == null) {
            c(i2, i3);
            return;
        }
        boolean z2 = false;
        if (gVar.v()) {
            int mode = View.MeasureSpec.getMode(i2);
            int mode2 = View.MeasureSpec.getMode(i3);
            this.v.a(this.k, this.qa, i2, i3);
            if (mode == 1073741824 && mode2 == 1073741824) {
                z2 = true;
            }
            if (!z2 && this.u != null) {
                if (this.qa.e == 1) {
                    B();
                }
                this.v.b(i2, i3);
                this.qa.j = true;
                C();
                this.v.d(i2, i3);
                if (this.v.B()) {
                    this.v.b(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
                    this.qa.j = true;
                    C();
                    this.v.d(i2, i3);
                }
            }
        } else if (this.B) {
            this.v.a(this.k, this.qa, i2, i3);
        } else {
            if (this.J) {
                x();
                r();
                J();
                s();
                r rVar = this.qa;
                if (rVar.l) {
                    rVar.h = true;
                } else {
                    this.m.b();
                    this.qa.h = false;
                }
                this.J = false;
                c(false);
            } else if (this.qa.l) {
                setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
                return;
            }
            a aVar = this.u;
            if (aVar != null) {
                this.qa.f = aVar.getItemCount();
            } else {
                this.qa.f = 0;
            }
            x();
            this.v.a(this.k, this.qa, i2, i3);
            c(false);
            this.qa.h = false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i2, Rect rect) {
        if (o()) {
            return false;
        }
        return super.onRequestFocusInDescendants(i2, rect);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        Parcelable parcelable2;
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        this.l = (SavedState) parcelable;
        super.onRestoreInstanceState(this.l.getSuperState());
        g gVar = this.v;
        if (gVar != null && (parcelable2 = this.l.mLayoutState) != null) {
            gVar.a(parcelable2);
        }
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        SavedState savedState2 = this.l;
        if (savedState2 != null) {
            savedState.copyFrom(savedState2);
        } else {
            g gVar = this.v;
            savedState.mLayoutState = gVar != null ? gVar.y() : null;
        }
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        if (i2 != i4 || i3 != i5) {
            l();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00f8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r18) {
        /*
            r17 = this;
            r6 = r17
            r7 = r18
            boolean r0 = r6.G
            r8 = 0
            if (r0 != 0) goto L_0x01e9
            boolean r0 = r6.H
            if (r0 == 0) goto L_0x000f
            goto L_0x01e9
        L_0x000f:
            boolean r0 = r17.a((android.view.MotionEvent) r18)
            r9 = 1
            if (r0 == 0) goto L_0x001a
            r17.z()
            return r9
        L_0x001a:
            androidx.recyclerview.widget.RecyclerView$g r0 = r6.v
            if (r0 != 0) goto L_0x001f
            return r8
        L_0x001f:
            boolean r10 = r0.a()
            androidx.recyclerview.widget.RecyclerView$g r0 = r6.v
            boolean r11 = r0.b()
            android.view.VelocityTracker r0 = r6.ba
            if (r0 != 0) goto L_0x0033
            android.view.VelocityTracker r0 = android.view.VelocityTracker.obtain()
            r6.ba = r0
        L_0x0033:
            int r0 = r18.getActionMasked()
            int r1 = r18.getActionIndex()
            if (r0 != 0) goto L_0x0043
            int[] r2 = r6.Ca
            r2[r9] = r8
            r2[r8] = r8
        L_0x0043:
            android.view.MotionEvent r12 = android.view.MotionEvent.obtain(r18)
            int[] r2 = r6.Ca
            r3 = r2[r8]
            float r3 = (float) r3
            r2 = r2[r9]
            float r2 = (float) r2
            r12.offsetLocation(r3, r2)
            r2 = 1056964608(0x3f000000, float:0.5)
            if (r0 == 0) goto L_0x01b8
            if (r0 == r9) goto L_0x0176
            r3 = 2
            if (r0 == r3) goto L_0x008c
            r3 = 3
            if (r0 == r3) goto L_0x0087
            r3 = 5
            if (r0 == r3) goto L_0x006b
            r1 = 6
            if (r0 == r1) goto L_0x0066
            goto L_0x01de
        L_0x0066:
            r17.c((android.view.MotionEvent) r18)
            goto L_0x01de
        L_0x006b:
            int r0 = r7.getPointerId(r1)
            r6.aa = r0
            float r0 = r7.getX(r1)
            float r0 = r0 + r2
            int r0 = (int) r0
            r6.ea = r0
            r6.ca = r0
            float r0 = r7.getY(r1)
            float r0 = r0 + r2
            int r0 = (int) r0
            r6.fa = r0
            r6.da = r0
            goto L_0x01de
        L_0x0087:
            r17.z()
            goto L_0x01de
        L_0x008c:
            int r0 = r6.aa
            int r0 = r7.findPointerIndex(r0)
            if (r0 >= 0) goto L_0x00b2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Error processing scroll; pointer index for id "
            r0.append(r1)
            int r1 = r6.aa
            r0.append(r1)
            java.lang.String r1 = " not found. Did any MotionEvents get skipped?"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "RecyclerView"
            android.util.Log.e(r1, r0)
            return r8
        L_0x00b2:
            float r1 = r7.getX(r0)
            float r1 = r1 + r2
            int r13 = (int) r1
            float r0 = r7.getY(r0)
            float r0 = r0 + r2
            int r14 = (int) r0
            int r0 = r6.ea
            int r0 = r0 - r13
            int r1 = r6.fa
            int r1 = r1 - r14
            int r2 = r6.W
            if (r2 == r9) goto L_0x00fb
            if (r10 == 0) goto L_0x00df
            if (r0 <= 0) goto L_0x00d4
            int r2 = r6.ga
            int r0 = r0 - r2
            int r0 = java.lang.Math.max(r8, r0)
            goto L_0x00db
        L_0x00d4:
            int r2 = r6.ga
            int r0 = r0 + r2
            int r0 = java.lang.Math.min(r8, r0)
        L_0x00db:
            if (r0 == 0) goto L_0x00df
            r2 = r9
            goto L_0x00e0
        L_0x00df:
            r2 = r8
        L_0x00e0:
            if (r11 == 0) goto L_0x00f6
            if (r1 <= 0) goto L_0x00ec
            int r3 = r6.ga
            int r1 = r1 - r3
            int r1 = java.lang.Math.max(r8, r1)
            goto L_0x00f3
        L_0x00ec:
            int r3 = r6.ga
            int r1 = r1 + r3
            int r1 = java.lang.Math.min(r8, r1)
        L_0x00f3:
            if (r1 == 0) goto L_0x00f6
            r2 = r9
        L_0x00f6:
            if (r2 == 0) goto L_0x00fb
            r6.setScrollState(r9)
        L_0x00fb:
            r15 = r0
            r16 = r1
            int r0 = r6.W
            if (r0 != r9) goto L_0x01de
            int[] r0 = r6.Da
            r0[r8] = r8
            r0[r9] = r8
            if (r10 == 0) goto L_0x010c
            r1 = r15
            goto L_0x010d
        L_0x010c:
            r1 = r8
        L_0x010d:
            if (r11 == 0) goto L_0x0112
            r2 = r16
            goto L_0x0113
        L_0x0112:
            r2 = r8
        L_0x0113:
            int[] r3 = r6.Da
            int[] r4 = r6.Ba
            r5 = 0
            r0 = r17
            boolean r0 = r0.a((int) r1, (int) r2, (int[]) r3, (int[]) r4, (int) r5)
            if (r0 == 0) goto L_0x0142
            int[] r0 = r6.Da
            r1 = r0[r8]
            int r15 = r15 - r1
            r0 = r0[r9]
            int r16 = r16 - r0
            int[] r0 = r6.Ca
            r1 = r0[r8]
            int[] r2 = r6.Ba
            r3 = r2[r8]
            int r1 = r1 + r3
            r0[r8] = r1
            r1 = r0[r9]
            r2 = r2[r9]
            int r1 = r1 + r2
            r0[r9] = r1
            android.view.ViewParent r0 = r17.getParent()
            r0.requestDisallowInterceptTouchEvent(r9)
        L_0x0142:
            r0 = r16
            int[] r1 = r6.Ba
            r2 = r1[r8]
            int r13 = r13 - r2
            r6.ea = r13
            r1 = r1[r9]
            int r14 = r14 - r1
            r6.fa = r14
            if (r10 == 0) goto L_0x0154
            r1 = r15
            goto L_0x0155
        L_0x0154:
            r1 = r8
        L_0x0155:
            if (r11 == 0) goto L_0x0159
            r2 = r0
            goto L_0x015a
        L_0x0159:
            r2 = r8
        L_0x015a:
            boolean r1 = r6.a((int) r1, (int) r2, (android.view.MotionEvent) r7)
            if (r1 == 0) goto L_0x0167
            android.view.ViewParent r1 = r17.getParent()
            r1.requestDisallowInterceptTouchEvent(r9)
        L_0x0167:
            androidx.recyclerview.widget.t r1 = r6.oa
            if (r1 == 0) goto L_0x01de
            if (r15 != 0) goto L_0x016f
            if (r0 == 0) goto L_0x01de
        L_0x016f:
            androidx.recyclerview.widget.t r1 = r6.oa
            r1.a((androidx.recyclerview.widget.RecyclerView) r6, (int) r15, (int) r0)
            goto L_0x01de
        L_0x0176:
            android.view.VelocityTracker r0 = r6.ba
            r0.addMovement(r12)
            android.view.VelocityTracker r0 = r6.ba
            r1 = 1000(0x3e8, float:1.401E-42)
            int r2 = r6.ja
            float r2 = (float) r2
            r0.computeCurrentVelocity(r1, r2)
            r0 = 0
            if (r10 == 0) goto L_0x0192
            android.view.VelocityTracker r1 = r6.ba
            int r2 = r6.aa
            float r1 = r1.getXVelocity(r2)
            float r1 = -r1
            goto L_0x0193
        L_0x0192:
            r1 = r0
        L_0x0193:
            if (r11 == 0) goto L_0x019f
            android.view.VelocityTracker r2 = r6.ba
            int r3 = r6.aa
            float r2 = r2.getYVelocity(r3)
            float r2 = -r2
            goto L_0x01a0
        L_0x019f:
            r2 = r0
        L_0x01a0:
            int r3 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1))
            if (r3 != 0) goto L_0x01a8
            int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r0 == 0) goto L_0x01b0
        L_0x01a8:
            int r0 = (int) r1
            int r1 = (int) r2
            boolean r0 = r6.e(r0, r1)
            if (r0 != 0) goto L_0x01b3
        L_0x01b0:
            r6.setScrollState(r8)
        L_0x01b3:
            r17.N()
            r8 = r9
            goto L_0x01de
        L_0x01b8:
            int r0 = r7.getPointerId(r8)
            r6.aa = r0
            float r0 = r18.getX()
            float r0 = r0 + r2
            int r0 = (int) r0
            r6.ea = r0
            r6.ca = r0
            float r0 = r18.getY()
            float r0 = r0 + r2
            int r0 = (int) r0
            r6.fa = r0
            r6.da = r0
            if (r10 == 0) goto L_0x01d6
            r0 = r9
            goto L_0x01d7
        L_0x01d6:
            r0 = r8
        L_0x01d7:
            if (r11 == 0) goto L_0x01db
            r0 = r0 | 2
        L_0x01db:
            r6.j(r0, r8)
        L_0x01de:
            if (r8 != 0) goto L_0x01e5
            android.view.VelocityTracker r0 = r6.ba
            r0.addMovement(r12)
        L_0x01e5:
            r12.recycle()
            return r9
        L_0x01e9:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RecyclerView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: package-private */
    public void p() {
        int b2 = this.n.b();
        for (int i2 = 0; i2 < b2; i2++) {
            ((h) this.n.d(i2).getLayoutParams()).f1133c = true;
        }
        this.k.g();
    }

    /* access modifiers changed from: package-private */
    public void q() {
        int b2 = this.n.b();
        for (int i2 = 0; i2 < b2; i2++) {
            u h2 = h(this.n.d(i2));
            if (h2 != null && !h2.shouldIgnore()) {
                h2.addFlags(6);
            }
        }
        p();
        this.k.h();
    }

    /* access modifiers changed from: package-private */
    public void r() {
        this.O++;
    }

    /* access modifiers changed from: protected */
    public void removeDetachedView(View view, boolean z2) {
        u h2 = h(view);
        if (h2 != null) {
            if (h2.isTmpDetached()) {
                h2.clearTmpDetachFlag();
            } else if (!h2.shouldIgnore()) {
                throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + h2 + i());
            }
        }
        view.clearAnimation();
        b(view);
        super.removeDetachedView(view, z2);
    }

    public void requestChildFocus(View view, View view2) {
        if (!this.v.a(this, this.qa, view, view2) && view2 != null) {
            a(view, view2);
        }
        super.requestChildFocus(view, view2);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z2) {
        return this.v.a(this, view, rect, z2);
    }

    public void requestDisallowInterceptTouchEvent(boolean z2) {
        int size = this.y.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.y.get(i2).a(z2);
        }
        super.requestDisallowInterceptTouchEvent(z2);
    }

    public void requestLayout() {
        if (this.E != 0 || this.G) {
            this.F = true;
        } else {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void s() {
        a(true);
    }

    public void scrollBy(int i2, int i3) {
        g gVar = this.v;
        if (gVar == null) {
            Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
        } else if (!this.G) {
            boolean a2 = gVar.a();
            boolean b2 = this.v.b();
            if (a2 || b2) {
                if (!a2) {
                    i2 = 0;
                }
                if (!b2) {
                    i3 = 0;
                }
                a(i2, i3, (MotionEvent) null);
            }
        }
    }

    public void scrollTo(int i2, int i3) {
        Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
    }

    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (!a(accessibilityEvent)) {
            super.sendAccessibilityEventUnchecked(accessibilityEvent);
        }
    }

    public void setAccessibilityDelegateCompat(@Nullable L l2) {
        this.xa = l2;
        ViewCompat.a((View) this, (C0123a) this.xa);
    }

    public void setAdapter(@Nullable a aVar) {
        setLayoutFrozen(false);
        a(aVar, false, true);
        b(false);
        requestLayout();
    }

    public void setChildDrawingOrderCallback(@Nullable d dVar) {
        if (dVar != this.ya) {
            this.ya = dVar;
            setChildrenDrawingOrderEnabled(this.ya != null);
        }
    }

    public void setClipToPadding(boolean z2) {
        if (z2 != this.p) {
            l();
        }
        this.p = z2;
        super.setClipToPadding(z2);
        if (this.D) {
            requestLayout();
        }
    }

    public void setEdgeEffectFactory(@NonNull EdgeEffectFactory edgeEffectFactory) {
        a.d.e.f.a(edgeEffectFactory);
        this.Q = edgeEffectFactory;
        l();
    }

    public void setHasFixedSize(boolean z2) {
        this.B = z2;
    }

    public void setItemAnimator(@Nullable ItemAnimator itemAnimator) {
        ItemAnimator itemAnimator2 = this.V;
        if (itemAnimator2 != null) {
            itemAnimator2.b();
            this.V.a((ItemAnimator.b) null);
        }
        this.V = itemAnimator;
        ItemAnimator itemAnimator3 = this.V;
        if (itemAnimator3 != null) {
            itemAnimator3.a(this.va);
        }
    }

    public void setItemViewCacheSize(int i2) {
        this.k.f(i2);
    }

    @Deprecated
    public void setLayoutFrozen(boolean z2) {
        suppressLayout(z2);
    }

    public void setLayoutManager(@Nullable g gVar) {
        if (gVar != this.v) {
            y();
            if (this.v != null) {
                ItemAnimator itemAnimator = this.V;
                if (itemAnimator != null) {
                    itemAnimator.b();
                }
                this.v.b(this.k);
                this.v.c(this.k);
                this.k.a();
                if (this.A) {
                    this.v.a(this, this.k);
                }
                this.v.f((RecyclerView) null);
                this.v = null;
            } else {
                this.k.a();
            }
            this.n.c();
            this.v = gVar;
            if (gVar != null) {
                if (gVar.f1124b == null) {
                    this.v.f(this);
                    if (this.A) {
                        this.v.a(this);
                    }
                } else {
                    throw new IllegalArgumentException("LayoutManager " + gVar + " is already attached to a RecyclerView:" + gVar.f1124b.i());
                }
            }
            this.k.j();
            requestLayout();
        }
    }

    @Deprecated
    public void setLayoutTransition(LayoutTransition layoutTransition) {
        if (Build.VERSION.SDK_INT < 18) {
            if (layoutTransition == null) {
                suppressLayout(false);
                return;
            } else if (layoutTransition.getAnimator(0) == null && layoutTransition.getAnimator(1) == null && layoutTransition.getAnimator(2) == null && layoutTransition.getAnimator(3) == null && layoutTransition.getAnimator(4) == null) {
                suppressLayout(true);
                return;
            }
        }
        if (layoutTransition == null) {
            super.setLayoutTransition((LayoutTransition) null);
            return;
        }
        throw new IllegalArgumentException("Providing a LayoutTransition into RecyclerView is not supported. Please use setItemAnimator() instead for animating changes to the items in this RecyclerView");
    }

    public void setNestedScrollingEnabled(boolean z2) {
        getScrollingChildHelper().a(z2);
    }

    public void setOnFlingListener(@Nullable j jVar) {
        this.ha = jVar;
    }

    @Deprecated
    public void setOnScrollListener(@Nullable l lVar) {
        this.ra = lVar;
    }

    public void setPreserveFocusAfterLayout(boolean z2) {
        this.ma = z2;
    }

    public void setRecycledViewPool(@Nullable m mVar) {
        this.k.a(mVar);
    }

    public void setRecyclerListener(@Nullable o oVar) {
        this.w = oVar;
    }

    /* access modifiers changed from: package-private */
    public void setScrollState(int i2) {
        if (i2 != this.W) {
            this.W = i2;
            if (i2 != 2) {
                P();
            }
            a(i2);
        }
    }

    public void setScrollingTouchSlop(int i2) {
        int i3;
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        if (i2 != 0) {
            if (i2 != 1) {
                Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + i2 + "; using default value");
            } else {
                i3 = viewConfiguration.getScaledPagingTouchSlop();
                this.ga = i3;
            }
        }
        i3 = viewConfiguration.getScaledTouchSlop();
        this.ga = i3;
    }

    public void setViewCacheExtension(@Nullable s sVar) {
        this.k.a(sVar);
    }

    public boolean startNestedScroll(int i2) {
        return getScrollingChildHelper().b(i2);
    }

    public void stopNestedScroll() {
        getScrollingChildHelper().c();
    }

    public final void suppressLayout(boolean z2) {
        if (z2 != this.G) {
            a("Do not suppressLayout in layout or scroll");
            if (!z2) {
                this.G = false;
                if (!(!this.F || this.v == null || this.u == null)) {
                    requestLayout();
                }
                this.F = false;
                return;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            onTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
            this.G = true;
            this.H = true;
            y();
        }
    }

    /* access modifiers changed from: package-private */
    public void t() {
        if (!this.wa && this.A) {
            ViewCompat.a((View) this, this.Fa);
            this.wa = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void u() {
        ItemAnimator itemAnimator = this.V;
        if (itemAnimator != null) {
            itemAnimator.b();
        }
        g gVar = this.v;
        if (gVar != null) {
            gVar.b(this.k);
            this.v.c(this.k);
        }
        this.k.a();
    }

    /* access modifiers changed from: package-private */
    public void v() {
        u uVar;
        int a2 = this.n.a();
        for (int i2 = 0; i2 < a2; i2++) {
            View c2 = this.n.c(i2);
            u g2 = g(c2);
            if (!(g2 == null || (uVar = g2.mShadowingHolder) == null)) {
                View view = uVar.itemView;
                int left = c2.getLeft();
                int top = c2.getTop();
                if (left != view.getLeft() || top != view.getTop()) {
                    view.layout(left, top, view.getWidth() + left, view.getHeight() + top);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void w() {
        int b2 = this.n.b();
        for (int i2 = 0; i2 < b2; i2++) {
            u h2 = h(this.n.d(i2));
            if (!h2.shouldIgnore()) {
                h2.saveOldPosition();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void x() {
        this.E++;
        if (this.E == 1 && !this.G) {
            this.F = false;
        }
    }

    public void y() {
        setScrollState(0);
        P();
    }
}
