package androidx.fragment.app;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.view.C0127e;
import androidx.lifecycle.f;
import androidx.lifecycle.g;
import androidx.lifecycle.h;
import androidx.lifecycle.i;
import androidx.lifecycle.k;
import androidx.lifecycle.p;
import androidx.lifecycle.u;
import androidx.lifecycle.v;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Fragment implements ComponentCallbacks, View.OnCreateContextMenuListener, i, v, androidx.savedstate.c {

    /* renamed from: a  reason: collision with root package name */
    static final Object f882a = new Object();
    boolean A;
    boolean B;
    boolean C;
    boolean D;
    boolean E = true;
    private boolean F;
    ViewGroup G;
    View H;
    View I;
    boolean J;
    boolean K = true;
    a L;
    Runnable M = new C0133c(this);
    boolean N;
    boolean O;
    float P;
    LayoutInflater Q;
    boolean R;
    f.b S = f.b.RESUMED;
    k T;
    @Nullable
    O U;
    p<i> V = new p<>();
    androidx.savedstate.b W;
    @LayoutRes
    private int X;

    /* renamed from: b  reason: collision with root package name */
    int f883b = 0;

    /* renamed from: c  reason: collision with root package name */
    Bundle f884c;

    /* renamed from: d  reason: collision with root package name */
    SparseArray<Parcelable> f885d;
    @Nullable
    Boolean e;
    @NonNull
    String f = UUID.randomUUID().toString();
    Bundle g;
    Fragment h;
    String i = null;
    int j;
    private Boolean k = null;
    boolean l;
    boolean m;
    boolean n;
    boolean o;
    boolean p;
    boolean q;
    int r;
    t s;
    C0141k t;
    @NonNull
    t u = new t();
    Fragment v;
    int w;
    int x;
    String y;
    boolean z;

    @SuppressLint({"BanParcelableUsage"})
    public static class SavedState implements Parcelable {
        @NonNull
        public static final Parcelable.Creator<SavedState> CREATOR = new C0136f();
        final Bundle mState;

        SavedState(Bundle bundle) {
            this.mState = bundle;
        }

        SavedState(@NonNull Parcel parcel, @Nullable ClassLoader classLoader) {
            Bundle bundle;
            this.mState = parcel.readBundle();
            if (classLoader != null && (bundle = this.mState) != null) {
                bundle.setClassLoader(classLoader);
            }
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeBundle(this.mState);
        }
    }

    static class a {

        /* renamed from: a  reason: collision with root package name */
        View f887a;

        /* renamed from: b  reason: collision with root package name */
        Animator f888b;

        /* renamed from: c  reason: collision with root package name */
        int f889c;

        /* renamed from: d  reason: collision with root package name */
        int f890d;
        int e;
        int f;
        Object g = null;
        Object h;
        Object i;
        Object j;
        Object k;
        Object l;
        Boolean m;
        Boolean n;
        androidx.core.app.i o;
        androidx.core.app.i p;
        boolean q;
        c r;
        boolean s;

        a() {
            Object obj = Fragment.f882a;
            this.h = obj;
            this.i = null;
            this.j = obj;
            this.k = null;
            this.l = obj;
            this.o = null;
            this.p = null;
        }
    }

    public static class b extends RuntimeException {
        public b(@NonNull String str, @Nullable Exception exc) {
            super(str, exc);
        }
    }

    interface c {
        void a();

        void b();
    }

    public Fragment() {
        ia();
    }

    @NonNull
    @Deprecated
    public static Fragment a(@NonNull Context context, @NonNull String str, @Nullable Bundle bundle) {
        try {
            Fragment fragment = (Fragment) C0140j.c(context.getClassLoader(), str).getConstructor(new Class[0]).newInstance(new Object[0]);
            if (bundle != null) {
                bundle.setClassLoader(fragment.getClass().getClassLoader());
                fragment.m(bundle);
            }
            return fragment;
        } catch (InstantiationException e2) {
            throw new b("Unable to instantiate fragment " + str + ": make sure class name exists, is public, and has an empty constructor that is public", e2);
        } catch (IllegalAccessException e3) {
            throw new b("Unable to instantiate fragment " + str + ": make sure class name exists, is public, and has an empty constructor that is public", e3);
        } catch (NoSuchMethodException e4) {
            throw new b("Unable to instantiate fragment " + str + ": could not find Fragment constructor", e4);
        } catch (InvocationTargetException e5) {
            throw new b("Unable to instantiate fragment " + str + ": calling Fragment constructor caused an exception", e5);
        }
    }

    private a ha() {
        if (this.L == null) {
            this.L = new a();
        }
        return this.L;
    }

    private void ia() {
        this.T = new k(this);
        this.W = androidx.savedstate.b.a((androidx.savedstate.c) this);
        if (Build.VERSION.SDK_INT >= 19) {
            this.T.a((h) new g() {
                public void a(@NonNull i iVar, @NonNull f.a aVar) {
                    View view;
                    if (aVar == f.a.ON_STOP && (view = Fragment.this.H) != null) {
                        view.cancelPendingInputEvents();
                    }
                }
            });
        }
    }

    @Nullable
    public Object A() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.k;
    }

    @Nullable
    public Object B() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        Object obj = aVar.l;
        return obj == f882a ? A() : obj;
    }

    /* access modifiers changed from: package-private */
    public int C() {
        a aVar = this.L;
        if (aVar == null) {
            return 0;
        }
        return aVar.f889c;
    }

    @Nullable
    public final Fragment D() {
        String str;
        Fragment fragment = this.h;
        if (fragment != null) {
            return fragment;
        }
        t tVar = this.s;
        if (tVar == null || (str = this.i) == null) {
            return null;
        }
        return tVar.j.get(str);
    }

    @Nullable
    public View E() {
        return this.H;
    }

    /* access modifiers changed from: package-private */
    public void F() {
        ia();
        this.f = UUID.randomUUID().toString();
        this.l = false;
        this.m = false;
        this.n = false;
        this.o = false;
        this.p = false;
        this.r = 0;
        this.s = null;
        this.u = new t();
        this.t = null;
        this.w = 0;
        this.x = 0;
        this.y = null;
        this.z = false;
        this.A = false;
    }

    /* access modifiers changed from: package-private */
    public boolean G() {
        a aVar = this.L;
        if (aVar == null) {
            return false;
        }
        return aVar.s;
    }

    /* access modifiers changed from: package-private */
    public final boolean H() {
        return this.r > 0;
    }

    /* access modifiers changed from: package-private */
    public boolean I() {
        a aVar = this.L;
        if (aVar == null) {
            return false;
        }
        return aVar.q;
    }

    public final boolean J() {
        t tVar = this.s;
        if (tVar == null) {
            return false;
        }
        return tVar.v();
    }

    /* access modifiers changed from: package-private */
    public void K() {
        this.u.w();
    }

    @CallSuper
    public void L() {
        this.F = true;
    }

    public void M() {
    }

    @CallSuper
    public void N() {
        this.F = true;
    }

    @CallSuper
    public void O() {
        this.F = true;
    }

    @CallSuper
    public void P() {
        this.F = true;
    }

    @CallSuper
    public void Q() {
        this.F = true;
    }

    @CallSuper
    public void R() {
        this.F = true;
    }

    @CallSuper
    public void S() {
        this.F = true;
    }

    /* access modifiers changed from: package-private */
    public void T() {
        this.u.a(this.t, (C0138h) new C0135e(this), this);
        this.F = false;
        a(this.t.f());
        if (!this.F) {
            throw new P("Fragment " + this + " did not call through to super.onAttach()");
        }
    }

    /* access modifiers changed from: package-private */
    public void U() {
        this.u.g();
        this.T.b(f.a.ON_DESTROY);
        this.f883b = 0;
        this.F = false;
        this.R = false;
        L();
        if (!this.F) {
            throw new P("Fragment " + this + " did not call through to super.onDestroy()");
        }
    }

    /* access modifiers changed from: package-private */
    public void V() {
        this.u.h();
        if (this.H != null) {
            this.U.a(f.a.ON_DESTROY);
        }
        this.f883b = 1;
        this.F = false;
        N();
        if (this.F) {
            a.h.a.a.a(this).a();
            this.q = false;
            return;
        }
        throw new P("Fragment " + this + " did not call through to super.onDestroyView()");
    }

    /* access modifiers changed from: package-private */
    public void W() {
        this.F = false;
        O();
        this.Q = null;
        if (!this.F) {
            throw new P("Fragment " + this + " did not call through to super.onDetach()");
        } else if (!this.u.u()) {
            this.u.g();
            this.u = new t();
        }
    }

    /* access modifiers changed from: package-private */
    public void X() {
        onLowMemory();
        this.u.i();
    }

    /* access modifiers changed from: package-private */
    public void Y() {
        this.u.j();
        if (this.H != null) {
            this.U.a(f.a.ON_PAUSE);
        }
        this.T.b(f.a.ON_PAUSE);
        this.f883b = 3;
        this.F = false;
        P();
        if (!this.F) {
            throw new P("Fragment " + this + " did not call through to super.onPause()");
        }
    }

    /* access modifiers changed from: package-private */
    public void Z() {
        boolean i2 = this.s.i(this);
        Boolean bool = this.k;
        if (bool == null || bool.booleanValue() != i2) {
            this.k = Boolean.valueOf(i2);
            d(i2);
            this.u.k();
        }
    }

    @NonNull
    @Deprecated
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public LayoutInflater a(@Nullable Bundle bundle) {
        C0141k kVar = this.t;
        if (kVar != null) {
            LayoutInflater i2 = kVar.i();
            t tVar = this.u;
            tVar.r();
            C0127e.a(i2, tVar);
            return i2;
        }
        throw new IllegalStateException("onGetLayoutInflater() cannot be executed until the Fragment is attached to the FragmentManager.");
    }

    @Nullable
    public View a(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        int i2 = this.X;
        if (i2 != 0) {
            return layoutInflater.inflate(i2, viewGroup, false);
        }
        return null;
    }

    @Nullable
    public Animation a(int i2, boolean z2, int i3) {
        return null;
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public Fragment a(@NonNull String str) {
        return str.equals(this.f) ? this : this.u.b(str);
    }

    @NonNull
    public f a() {
        return this.T;
    }

    /* access modifiers changed from: package-private */
    public void a(int i2) {
        if (this.L != null || i2 != 0) {
            ha().f890d = i2;
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, int i3) {
        if (this.L != null || i2 != 0 || i3 != 0) {
            ha();
            a aVar = this.L;
            aVar.e = i2;
            aVar.f = i3;
        }
    }

    public void a(int i2, int i3, @Nullable Intent intent) {
    }

    public void a(int i2, @NonNull String[] strArr, @NonNull int[] iArr) {
    }

    /* access modifiers changed from: package-private */
    public void a(Animator animator) {
        ha().f888b = animator;
    }

    @CallSuper
    @Deprecated
    public void a(@NonNull Activity activity) {
        this.F = true;
    }

    @CallSuper
    @Deprecated
    public void a(@NonNull Activity activity, @NonNull AttributeSet attributeSet, @Nullable Bundle bundle) {
        this.F = true;
    }

    @CallSuper
    public void a(@NonNull Context context) {
        this.F = true;
        C0141k kVar = this.t;
        Activity e2 = kVar == null ? null : kVar.e();
        if (e2 != null) {
            this.F = false;
            a(e2);
        }
    }

    @CallSuper
    public void a(@NonNull Context context, @NonNull AttributeSet attributeSet, @Nullable Bundle bundle) {
        this.F = true;
        C0141k kVar = this.t;
        Activity e2 = kVar == null ? null : kVar.e();
        if (e2 != null) {
            this.F = false;
            a(e2, attributeSet, bundle);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull Configuration configuration) {
        onConfigurationChanged(configuration);
        this.u.a(configuration);
    }

    public void a(@NonNull Menu menu) {
    }

    public void a(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
    }

    /* access modifiers changed from: package-private */
    public void a(View view) {
        ha().f887a = view;
    }

    public void a(@NonNull View view, @Nullable Bundle bundle) {
    }

    /* access modifiers changed from: package-private */
    public void a(c cVar) {
        ha();
        c cVar2 = this.L.r;
        if (cVar != cVar2) {
            if (cVar == null || cVar2 == null) {
                a aVar = this.L;
                if (aVar.q) {
                    aVar.r = cVar;
                }
                if (cVar != null) {
                    cVar.a();
                    return;
                }
                return;
            }
            throw new IllegalStateException("Trying to set a replacement startPostponedEnterTransition on " + this);
        }
    }

    public void a(@NonNull Fragment fragment) {
    }

    public void a(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        printWriter.print(str);
        printWriter.print("mFragmentId=#");
        printWriter.print(Integer.toHexString(this.w));
        printWriter.print(" mContainerId=#");
        printWriter.print(Integer.toHexString(this.x));
        printWriter.print(" mTag=");
        printWriter.println(this.y);
        printWriter.print(str);
        printWriter.print("mState=");
        printWriter.print(this.f883b);
        printWriter.print(" mWho=");
        printWriter.print(this.f);
        printWriter.print(" mBackStackNesting=");
        printWriter.println(this.r);
        printWriter.print(str);
        printWriter.print("mAdded=");
        printWriter.print(this.l);
        printWriter.print(" mRemoving=");
        printWriter.print(this.m);
        printWriter.print(" mFromLayout=");
        printWriter.print(this.n);
        printWriter.print(" mInLayout=");
        printWriter.println(this.o);
        printWriter.print(str);
        printWriter.print("mHidden=");
        printWriter.print(this.z);
        printWriter.print(" mDetached=");
        printWriter.print(this.A);
        printWriter.print(" mMenuVisible=");
        printWriter.print(this.E);
        printWriter.print(" mHasMenu=");
        printWriter.println(this.D);
        printWriter.print(str);
        printWriter.print("mRetainInstance=");
        printWriter.print(this.B);
        printWriter.print(" mUserVisibleHint=");
        printWriter.println(this.K);
        if (this.s != null) {
            printWriter.print(str);
            printWriter.print("mFragmentManager=");
            printWriter.println(this.s);
        }
        if (this.t != null) {
            printWriter.print(str);
            printWriter.print("mHost=");
            printWriter.println(this.t);
        }
        if (this.v != null) {
            printWriter.print(str);
            printWriter.print("mParentFragment=");
            printWriter.println(this.v);
        }
        if (this.g != null) {
            printWriter.print(str);
            printWriter.print("mArguments=");
            printWriter.println(this.g);
        }
        if (this.f884c != null) {
            printWriter.print(str);
            printWriter.print("mSavedFragmentState=");
            printWriter.println(this.f884c);
        }
        if (this.f885d != null) {
            printWriter.print(str);
            printWriter.print("mSavedViewState=");
            printWriter.println(this.f885d);
        }
        Fragment D2 = D();
        if (D2 != null) {
            printWriter.print(str);
            printWriter.print("mTarget=");
            printWriter.print(D2);
            printWriter.print(" mTargetRequestCode=");
            printWriter.println(this.j);
        }
        if (s() != 0) {
            printWriter.print(str);
            printWriter.print("mNextAnim=");
            printWriter.println(s());
        }
        if (this.G != null) {
            printWriter.print(str);
            printWriter.print("mContainer=");
            printWriter.println(this.G);
        }
        if (this.H != null) {
            printWriter.print(str);
            printWriter.print("mView=");
            printWriter.println(this.H);
        }
        if (this.I != null) {
            printWriter.print(str);
            printWriter.print("mInnerView=");
            printWriter.println(this.H);
        }
        if (i() != null) {
            printWriter.print(str);
            printWriter.print("mAnimatingAway=");
            printWriter.println(i());
            printWriter.print(str);
            printWriter.print("mStateAfterAnimating=");
            printWriter.println(C());
        }
        if (l() != null) {
            a.h.a.a.a(this).a(str, fileDescriptor, printWriter, strArr);
        }
        printWriter.print(str);
        printWriter.println("Child " + this.u + ":");
        t tVar = this.u;
        tVar.a(str + "  ", fileDescriptor, printWriter, strArr);
    }

    public void a(boolean z2) {
    }

    public boolean a(@NonNull MenuItem menuItem) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public void aa() {
        this.u.w();
        this.u.p();
        this.f883b = 4;
        this.F = false;
        Q();
        if (this.F) {
            this.T.b(f.a.ON_RESUME);
            if (this.H != null) {
                this.U.a(f.a.ON_RESUME);
            }
            this.u.l();
            this.u.p();
            return;
        }
        throw new P("Fragment " + this + " did not call through to super.onResume()");
    }

    @Nullable
    public Animator b(int i2, boolean z2, int i3) {
        return null;
    }

    /* access modifiers changed from: package-private */
    public void b(int i2) {
        ha().f889c = i2;
    }

    @CallSuper
    public void b(@Nullable Bundle bundle) {
        this.F = true;
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        this.u.w();
        this.q = true;
        this.U = new O();
        this.H = a(layoutInflater, viewGroup, bundle);
        if (this.H != null) {
            this.U.d();
            this.V.a(this.U);
        } else if (!this.U.e()) {
            this.U = null;
        } else {
            throw new IllegalStateException("Called getViewLifecycleOwner() but onCreateView() returned null");
        }
    }

    public void b(@NonNull Menu menu) {
    }

    public void b(boolean z2) {
    }

    /* access modifiers changed from: package-private */
    public boolean b(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        boolean z2 = false;
        if (this.z) {
            return false;
        }
        if (this.D && this.E) {
            z2 = true;
            a(menu, menuInflater);
        }
        return z2 | this.u.a(menu, menuInflater);
    }

    public boolean b(@NonNull MenuItem menuItem) {
        return false;
    }

    /* access modifiers changed from: package-private */
    public void ba() {
        this.u.w();
        this.u.p();
        this.f883b = 3;
        this.F = false;
        R();
        if (this.F) {
            this.T.b(f.a.ON_START);
            if (this.H != null) {
                this.U.a(f.a.ON_START);
            }
            this.u.m();
            return;
        }
        throw new P("Fragment " + this + " did not call through to super.onStart()");
    }

    @NonNull
    public final androidx.savedstate.a c() {
        return this.W.a();
    }

    @CallSuper
    public void c(@Nullable Bundle bundle) {
        this.F = true;
        k(bundle);
        if (!this.u.c(1)) {
            this.u.f();
        }
    }

    /* access modifiers changed from: package-private */
    public void c(@NonNull Menu menu) {
        if (!this.z) {
            if (this.D && this.E) {
                a(menu);
            }
            this.u.a(menu);
        }
    }

    public void c(boolean z2) {
    }

    /* access modifiers changed from: package-private */
    public boolean c(@NonNull MenuItem menuItem) {
        if (!this.z) {
            return a(menuItem) || this.u.a(menuItem);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void ca() {
        this.u.n();
        if (this.H != null) {
            this.U.a(f.a.ON_STOP);
        }
        this.T.b(f.a.ON_STOP);
        this.f883b = 2;
        this.F = false;
        S();
        if (!this.F) {
            throw new P("Fragment " + this + " did not call through to super.onStop()");
        }
    }

    @NonNull
    public LayoutInflater d(@Nullable Bundle bundle) {
        return a(bundle);
    }

    @NonNull
    public u d() {
        t tVar = this.s;
        if (tVar != null) {
            return tVar.g(this);
        }
        throw new IllegalStateException("Can't access ViewModels from detached fragment");
    }

    public void d(boolean z2) {
    }

    /* access modifiers changed from: package-private */
    public boolean d(@NonNull Menu menu) {
        boolean z2 = false;
        if (this.z) {
            return false;
        }
        if (this.D && this.E) {
            z2 = true;
            b(menu);
        }
        return z2 | this.u.b(menu);
    }

    /* access modifiers changed from: package-private */
    public boolean d(@NonNull MenuItem menuItem) {
        if (!this.z) {
            return (this.D && this.E && b(menuItem)) || this.u.b(menuItem);
        }
        return false;
    }

    @NonNull
    public final C0137g da() {
        C0137g f2 = f();
        if (f2 != null) {
            return f2;
        }
        throw new IllegalStateException("Fragment " + this + " not attached to an activity.");
    }

    /* access modifiers changed from: package-private */
    public void e() {
        a aVar = this.L;
        c cVar = null;
        if (aVar != null) {
            aVar.q = false;
            c cVar2 = aVar.r;
            aVar.r = null;
            cVar = cVar2;
        }
        if (cVar != null) {
            cVar.b();
        }
    }

    public void e(@NonNull Bundle bundle) {
    }

    /* access modifiers changed from: package-private */
    public void e(boolean z2) {
        b(z2);
        this.u.a(z2);
    }

    @NonNull
    public final Context ea() {
        Context l2 = l();
        if (l2 != null) {
            return l2;
        }
        throw new IllegalStateException("Fragment " + this + " not attached to a context.");
    }

    public final boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @Nullable
    public final C0137g f() {
        C0141k kVar = this.t;
        if (kVar == null) {
            return null;
        }
        return (C0137g) kVar.e();
    }

    @CallSuper
    public void f(@Nullable Bundle bundle) {
        this.F = true;
    }

    /* access modifiers changed from: package-private */
    public void f(boolean z2) {
        c(z2);
        this.u.b(z2);
    }

    @NonNull
    public final View fa() {
        View E2 = E();
        if (E2 != null) {
            return E2;
        }
        throw new IllegalStateException("Fragment " + this + " did not return a View from onCreateView() or this was called before onCreateView().");
    }

    /* access modifiers changed from: package-private */
    public void g(Bundle bundle) {
        this.u.w();
        this.f883b = 2;
        this.F = false;
        b(bundle);
        if (this.F) {
            this.u.e();
            return;
        }
        throw new P("Fragment " + this + " did not call through to super.onActivityCreated()");
    }

    /* access modifiers changed from: package-private */
    public void g(boolean z2) {
        ha().s = z2;
    }

    public boolean g() {
        Boolean bool;
        a aVar = this.L;
        if (aVar == null || (bool = aVar.n) == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public void ga() {
        t tVar = this.s;
        if (tVar == null || tVar.t == null) {
            ha().q = false;
        } else if (Looper.myLooper() != this.s.t.g().getLooper()) {
            this.s.t.g().postAtFrontOfQueue(new C0134d(this));
        } else {
            e();
        }
    }

    /* access modifiers changed from: package-private */
    public void h(Bundle bundle) {
        this.u.w();
        this.f883b = 1;
        this.F = false;
        this.W.a(bundle);
        c(bundle);
        this.R = true;
        if (this.F) {
            this.T.b(f.a.ON_CREATE);
            return;
        }
        throw new P("Fragment " + this + " did not call through to super.onCreate()");
    }

    public boolean h() {
        Boolean bool;
        a aVar = this.L;
        if (aVar == null || (bool = aVar.m) == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public final int hashCode() {
        return super.hashCode();
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public LayoutInflater i(@Nullable Bundle bundle) {
        this.Q = d(bundle);
        return this.Q;
    }

    /* access modifiers changed from: package-private */
    public View i() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.f887a;
    }

    /* access modifiers changed from: package-private */
    public Animator j() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.f888b;
    }

    /* access modifiers changed from: package-private */
    public void j(Bundle bundle) {
        e(bundle);
        this.W.b(bundle);
        Parcelable y2 = this.u.y();
        if (y2 != null) {
            bundle.putParcelable("android:support:fragments", y2);
        }
    }

    @NonNull
    public final C0142l k() {
        if (this.t != null) {
            return this.u;
        }
        throw new IllegalStateException("Fragment " + this + " has not been attached yet.");
    }

    /* access modifiers changed from: package-private */
    public void k(@Nullable Bundle bundle) {
        Parcelable parcelable;
        if (bundle != null && (parcelable = bundle.getParcelable("android:support:fragments")) != null) {
            this.u.a(parcelable);
            this.u.f();
        }
    }

    @Nullable
    public Context l() {
        C0141k kVar = this.t;
        if (kVar == null) {
            return null;
        }
        return kVar.f();
    }

    /* access modifiers changed from: package-private */
    public final void l(Bundle bundle) {
        SparseArray<Parcelable> sparseArray = this.f885d;
        if (sparseArray != null) {
            this.I.restoreHierarchyState(sparseArray);
            this.f885d = null;
        }
        this.F = false;
        f(bundle);
        if (!this.F) {
            throw new P("Fragment " + this + " did not call through to super.onViewStateRestored()");
        } else if (this.H != null) {
            this.U.a(f.a.ON_CREATE);
        }
    }

    @Nullable
    public Object m() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.g;
    }

    public void m(@Nullable Bundle bundle) {
        if (this.s == null || !J()) {
            this.g = bundle;
            return;
        }
        throw new IllegalStateException("Fragment already added and state has been saved");
    }

    /* access modifiers changed from: package-private */
    public androidx.core.app.i n() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.o;
    }

    @Nullable
    public Object o() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.i;
    }

    @CallSuper
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        this.F = true;
    }

    public void onCreateContextMenu(@NonNull ContextMenu contextMenu, @NonNull View view, @Nullable ContextMenu.ContextMenuInfo contextMenuInfo) {
        da().onCreateContextMenu(contextMenu, view, contextMenuInfo);
    }

    @CallSuper
    public void onLowMemory() {
        this.F = true;
    }

    /* access modifiers changed from: package-private */
    public androidx.core.app.i p() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        return aVar.p;
    }

    @Nullable
    public final C0142l q() {
        return this.s;
    }

    @Nullable
    public final Object r() {
        C0141k kVar = this.t;
        if (kVar == null) {
            return null;
        }
        return kVar.h();
    }

    /* access modifiers changed from: package-private */
    public int s() {
        a aVar = this.L;
        if (aVar == null) {
            return 0;
        }
        return aVar.f890d;
    }

    /* access modifiers changed from: package-private */
    public int t() {
        a aVar = this.L;
        if (aVar == null) {
            return 0;
        }
        return aVar.e;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        a.d.e.a.a(this, sb);
        sb.append(" (");
        sb.append(this.f);
        sb.append(")");
        if (this.w != 0) {
            sb.append(" id=0x");
            sb.append(Integer.toHexString(this.w));
        }
        if (this.y != null) {
            sb.append(" ");
            sb.append(this.y);
        }
        sb.append('}');
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    public int u() {
        a aVar = this.L;
        if (aVar == null) {
            return 0;
        }
        return aVar.f;
    }

    @Nullable
    public final Fragment v() {
        return this.v;
    }

    @Nullable
    public Object w() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        Object obj = aVar.j;
        return obj == f882a ? o() : obj;
    }

    @NonNull
    public final Resources x() {
        return ea().getResources();
    }

    public final boolean y() {
        return this.B;
    }

    @Nullable
    public Object z() {
        a aVar = this.L;
        if (aVar == null) {
            return null;
        }
        Object obj = aVar.h;
        return obj == f882a ? m() : obj;
    }
}
