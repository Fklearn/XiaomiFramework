package androidx.fragment.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.r;
import androidx.fragment.app.C0142l;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.f;
import androidx.lifecycle.u;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class t extends C0142l implements LayoutInflater.Factory2 {

    /* renamed from: c  reason: collision with root package name */
    static boolean f937c = false;

    /* renamed from: d  reason: collision with root package name */
    static final Interpolator f938d = new DecelerateInterpolator(2.5f);
    static final Interpolator e = new DecelerateInterpolator(1.5f);
    boolean A;
    boolean B;
    ArrayList<C0131a> C;
    ArrayList<Boolean> D;
    ArrayList<Fragment> E;
    Bundle F = null;
    SparseArray<Parcelable> G = null;
    ArrayList<f> H;
    private w I;
    Runnable J = new C0144n(this);
    ArrayList<e> f;
    boolean g;
    int h = 0;
    final ArrayList<Fragment> i = new ArrayList<>();
    final HashMap<String, Fragment> j = new HashMap<>();
    ArrayList<C0131a> k;
    ArrayList<Fragment> l;
    private OnBackPressedDispatcher m;
    private final androidx.activity.d n = new C0143m(this, false);
    ArrayList<C0131a> o;
    ArrayList<Integer> p;
    ArrayList<C0142l.c> q;
    private final CopyOnWriteArrayList<c> r = new CopyOnWriteArrayList<>();
    int s = 0;
    C0141k t;
    C0138h u;
    Fragment v;
    @Nullable
    Fragment w;
    boolean x;
    boolean y;
    boolean z;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        public final Animation f939a;

        /* renamed from: b  reason: collision with root package name */
        public final Animator f940b;

        a(Animator animator) {
            this.f939a = null;
            this.f940b = animator;
            if (animator == null) {
                throw new IllegalStateException("Animator cannot be null");
            }
        }

        a(Animation animation) {
            this.f939a = animation;
            this.f940b = null;
            if (animation == null) {
                throw new IllegalStateException("Animation cannot be null");
            }
        }
    }

    private static class b extends AnimationSet implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private final ViewGroup f941a;

        /* renamed from: b  reason: collision with root package name */
        private final View f942b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f943c;

        /* renamed from: d  reason: collision with root package name */
        private boolean f944d;
        private boolean e = true;

        b(@NonNull Animation animation, @NonNull ViewGroup viewGroup, @NonNull View view) {
            super(false);
            this.f941a = viewGroup;
            this.f942b = view;
            addAnimation(animation);
            this.f941a.post(this);
        }

        public boolean getTransformation(long j, Transformation transformation) {
            this.e = true;
            if (this.f943c) {
                return !this.f944d;
            }
            if (!super.getTransformation(j, transformation)) {
                this.f943c = true;
                r.a(this.f941a, this);
            }
            return true;
        }

        public boolean getTransformation(long j, Transformation transformation, float f) {
            this.e = true;
            if (this.f943c) {
                return !this.f944d;
            }
            if (!super.getTransformation(j, transformation, f)) {
                this.f943c = true;
                r.a(this.f941a, this);
            }
            return true;
        }

        public void run() {
            if (this.f943c || !this.e) {
                this.f941a.endViewTransition(this.f942b);
                this.f944d = true;
                return;
            }
            this.e = false;
            this.f941a.post(this);
        }
    }

    private static final class c {

        /* renamed from: a  reason: collision with root package name */
        final C0142l.b f945a;

        /* renamed from: b  reason: collision with root package name */
        final boolean f946b;
    }

    static class d {

        /* renamed from: a  reason: collision with root package name */
        public static final int[] f947a = {16842755, 16842960, 16842961};
    }

    interface e {
        boolean a(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2);
    }

    static class f implements Fragment.c {

        /* renamed from: a  reason: collision with root package name */
        final boolean f948a;

        /* renamed from: b  reason: collision with root package name */
        final C0131a f949b;

        /* renamed from: c  reason: collision with root package name */
        private int f950c;

        f(C0131a aVar, boolean z) {
            this.f948a = z;
            this.f949b = aVar;
        }

        public void a() {
            this.f950c++;
        }

        public void b() {
            this.f950c--;
            if (this.f950c == 0) {
                this.f949b.s.z();
            }
        }

        public void c() {
            C0131a aVar = this.f949b;
            aVar.s.a(aVar, this.f948a, false, false);
        }

        public void d() {
            boolean z = this.f950c > 0;
            t tVar = this.f949b.s;
            int size = tVar.i.size();
            for (int i = 0; i < size; i++) {
                Fragment fragment = tVar.i.get(i);
                fragment.a((Fragment.c) null);
                if (z && fragment.I()) {
                    fragment.ga();
                }
            }
            C0131a aVar = this.f949b;
            aVar.s.a(aVar, this.f948a, !z, true);
        }

        public boolean e() {
            return this.f950c == 0;
        }
    }

    t() {
    }

    private void B() {
        this.j.values().removeAll(Collections.singleton((Object) null));
    }

    private void C() {
        if (v()) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
    }

    private void D() {
        this.g = false;
        this.D.clear();
        this.C.clear();
    }

    private void E() {
        for (Fragment next : this.j.values()) {
            if (next != null) {
                if (next.i() != null) {
                    int C2 = next.C();
                    View i2 = next.i();
                    Animation animation = i2.getAnimation();
                    if (animation != null) {
                        animation.cancel();
                        i2.clearAnimation();
                    }
                    next.a((View) null);
                    a(next, C2, 0, 0, false);
                } else if (next.j() != null) {
                    next.j().end();
                }
            }
        }
    }

    private void F() {
        if (this.H != null) {
            while (!this.H.isEmpty()) {
                this.H.remove(0).d();
            }
        }
    }

    private void G() {
        ArrayList<e> arrayList = this.f;
        boolean z2 = true;
        if (arrayList == null || arrayList.isEmpty()) {
            androidx.activity.d dVar = this.n;
            if (q() <= 0 || !i(this.v)) {
                z2 = false;
            }
            dVar.a(z2);
            return;
        }
        this.n.a(true);
    }

    private int a(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3, a.c.d<Fragment> dVar) {
        int i4 = i3;
        for (int i5 = i3 - 1; i5 >= i2; i5--) {
            C0131a aVar = arrayList.get(i5);
            boolean booleanValue = arrayList2.get(i5).booleanValue();
            if (aVar.c() && !aVar.a(arrayList, i5 + 1, i3)) {
                if (this.H == null) {
                    this.H = new ArrayList<>();
                }
                f fVar = new f(aVar, booleanValue);
                this.H.add(fVar);
                aVar.a((Fragment.c) fVar);
                if (booleanValue) {
                    aVar.a();
                } else {
                    aVar.a(false);
                }
                i4--;
                if (i5 != i4) {
                    arrayList.remove(i5);
                    arrayList.add(i4, aVar);
                }
                a(dVar);
            }
        }
        return i4;
    }

    static a a(float f2, float f3) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f2, f3);
        alphaAnimation.setInterpolator(e);
        alphaAnimation.setDuration(220);
        return new a((Animation) alphaAnimation);
    }

    static a a(float f2, float f3, float f4, float f5) {
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(f2, f3, f2, f3, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(f938d);
        scaleAnimation.setDuration(220);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f4, f5);
        alphaAnimation.setInterpolator(e);
        alphaAnimation.setDuration(220);
        animationSet.addAnimation(alphaAnimation);
        return new a((Animation) animationSet);
    }

    private void a(a.c.d<Fragment> dVar) {
        int i2 = this.s;
        if (i2 >= 1) {
            int min = Math.min(i2, 3);
            int size = this.i.size();
            for (int i3 = 0; i3 < size; i3++) {
                Fragment fragment = this.i.get(i3);
                if (fragment.f883b < min) {
                    a(fragment, min, fragment.s(), fragment.t(), false);
                    if (fragment.H != null && !fragment.z && fragment.N) {
                        dVar.add(fragment);
                    }
                }
            }
        }
    }

    private void a(@NonNull Fragment fragment, @NonNull a aVar, int i2) {
        View view = fragment.H;
        ViewGroup viewGroup = fragment.G;
        viewGroup.startViewTransition(view);
        fragment.b(i2);
        Animation animation = aVar.f939a;
        if (animation != null) {
            b bVar = new b(animation, viewGroup, view);
            fragment.a(fragment.H);
            bVar.setAnimationListener(new C0146p(this, viewGroup, fragment));
            fragment.H.startAnimation(bVar);
            return;
        }
        Animator animator = aVar.f940b;
        fragment.a(animator);
        animator.addListener(new q(this, viewGroup, view, fragment));
        animator.setTarget(fragment.H);
        animator.start();
    }

    private void a(RuntimeException runtimeException) {
        Log.e("FragmentManager", runtimeException.getMessage());
        Log.e("FragmentManager", "Activity state:");
        PrintWriter printWriter = new PrintWriter(new a.d.e.b("FragmentManager"));
        C0141k kVar = this.t;
        if (kVar != null) {
            try {
                kVar.a("  ", (FileDescriptor) null, printWriter, new String[0]);
            } catch (Exception e2) {
                Log.e("FragmentManager", "Failed dumping state", e2);
            }
        } else {
            a("  ", (FileDescriptor) null, printWriter, new String[0]);
        }
        throw runtimeException;
    }

    private void a(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2) {
        int indexOf;
        int indexOf2;
        ArrayList<f> arrayList3 = this.H;
        int size = arrayList3 == null ? 0 : arrayList3.size();
        int i2 = 0;
        while (i2 < size) {
            f fVar = this.H.get(i2);
            if (arrayList == null || fVar.f948a || (indexOf2 = arrayList.indexOf(fVar.f949b)) == -1 || !arrayList2.get(indexOf2).booleanValue()) {
                if (fVar.e() || (arrayList != null && fVar.f949b.a(arrayList, 0, arrayList.size()))) {
                    this.H.remove(i2);
                    i2--;
                    size--;
                    if (arrayList == null || fVar.f948a || (indexOf = arrayList.indexOf(fVar.f949b)) == -1 || !arrayList2.get(indexOf).booleanValue()) {
                        fVar.d();
                    }
                }
                i2++;
            } else {
                this.H.remove(i2);
                i2--;
                size--;
            }
            fVar.c();
            i2++;
        }
    }

    private static void a(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3) {
        while (i2 < i3) {
            C0131a aVar = arrayList.get(i2);
            boolean z2 = true;
            if (arrayList2.get(i2).booleanValue()) {
                aVar.a(-1);
                if (i2 != i3 - 1) {
                    z2 = false;
                }
                aVar.a(z2);
            } else {
                aVar.a(1);
                aVar.a();
            }
            i2++;
        }
    }

    private boolean a(String str, int i2, int i3) {
        p();
        c(true);
        Fragment fragment = this.w;
        if (fragment != null && i2 < 0 && str == null && fragment.k().c()) {
            return true;
        }
        boolean a2 = a(this.C, this.D, str, i2, i3);
        if (a2) {
            this.g = true;
            try {
                c(this.C, this.D);
            } finally {
                D();
            }
        }
        G();
        o();
        B();
        return a2;
    }

    public static int b(int i2, boolean z2) {
        if (i2 == 4097) {
            return z2 ? 1 : 2;
        }
        if (i2 == 4099) {
            return z2 ? 5 : 6;
        }
        if (i2 != 8194) {
            return -1;
        }
        return z2 ? 3 : 4;
    }

    private void b(a.c.d<Fragment> dVar) {
        int size = dVar.size();
        for (int i2 = 0; i2 < size; i2++) {
            Fragment c2 = dVar.c(i2);
            if (!c2.l) {
                View fa = c2.fa();
                c2.P = fa.getAlpha();
                fa.setAlpha(0.0f);
            }
        }
    }

    private void b(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2, int i2, int i3) {
        int i4;
        int i5;
        ArrayList<C0131a> arrayList3 = arrayList;
        ArrayList<Boolean> arrayList4 = arrayList2;
        int i6 = i2;
        int i7 = i3;
        boolean z2 = arrayList3.get(i6).q;
        ArrayList<Fragment> arrayList5 = this.E;
        if (arrayList5 == null) {
            this.E = new ArrayList<>();
        } else {
            arrayList5.clear();
        }
        this.E.addAll(this.i);
        Fragment s2 = s();
        boolean z3 = false;
        for (int i8 = i6; i8 < i7; i8++) {
            C0131a aVar = arrayList3.get(i8);
            s2 = !arrayList4.get(i8).booleanValue() ? aVar.a(this.E, s2) : aVar.b(this.E, s2);
            z3 = z3 || aVar.h;
        }
        this.E.clear();
        if (!z2) {
            E.a(this, arrayList, arrayList2, i2, i3, false);
        }
        a(arrayList, arrayList2, i2, i3);
        if (z2) {
            a.c.d dVar = new a.c.d();
            a((a.c.d<Fragment>) dVar);
            int a2 = a(arrayList, arrayList2, i2, i3, (a.c.d<Fragment>) dVar);
            b((a.c.d<Fragment>) dVar);
            i4 = a2;
        } else {
            i4 = i7;
        }
        if (i4 != i6 && z2) {
            E.a(this, arrayList, arrayList2, i2, i4, true);
            a(this.s, true);
        }
        while (i6 < i7) {
            C0131a aVar2 = arrayList3.get(i6);
            if (arrayList4.get(i6).booleanValue() && (i5 = aVar2.u) >= 0) {
                b(i5);
                aVar2.u = -1;
            }
            aVar2.d();
            i6++;
        }
        if (z3) {
            x();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003b, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean b(java.util.ArrayList<androidx.fragment.app.C0131a> r5, java.util.ArrayList<java.lang.Boolean> r6) {
        /*
            r4 = this;
            monitor-enter(r4)
            java.util.ArrayList<androidx.fragment.app.t$e> r0 = r4.f     // Catch:{ all -> 0x003c }
            r1 = 0
            if (r0 == 0) goto L_0x003a
            java.util.ArrayList<androidx.fragment.app.t$e> r0 = r4.f     // Catch:{ all -> 0x003c }
            int r0 = r0.size()     // Catch:{ all -> 0x003c }
            if (r0 != 0) goto L_0x000f
            goto L_0x003a
        L_0x000f:
            java.util.ArrayList<androidx.fragment.app.t$e> r0 = r4.f     // Catch:{ all -> 0x003c }
            int r0 = r0.size()     // Catch:{ all -> 0x003c }
            r2 = r1
        L_0x0016:
            if (r1 >= r0) goto L_0x0028
            java.util.ArrayList<androidx.fragment.app.t$e> r3 = r4.f     // Catch:{ all -> 0x003c }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x003c }
            androidx.fragment.app.t$e r3 = (androidx.fragment.app.t.e) r3     // Catch:{ all -> 0x003c }
            boolean r3 = r3.a(r5, r6)     // Catch:{ all -> 0x003c }
            r2 = r2 | r3
            int r1 = r1 + 1
            goto L_0x0016
        L_0x0028:
            java.util.ArrayList<androidx.fragment.app.t$e> r5 = r4.f     // Catch:{ all -> 0x003c }
            r5.clear()     // Catch:{ all -> 0x003c }
            androidx.fragment.app.k r5 = r4.t     // Catch:{ all -> 0x003c }
            android.os.Handler r5 = r5.g()     // Catch:{ all -> 0x003c }
            java.lang.Runnable r6 = r4.J     // Catch:{ all -> 0x003c }
            r5.removeCallbacks(r6)     // Catch:{ all -> 0x003c }
            monitor-exit(r4)     // Catch:{ all -> 0x003c }
            return r2
        L_0x003a:
            monitor-exit(r4)     // Catch:{ all -> 0x003c }
            return r1
        L_0x003c:
            r5 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x003c }
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.t.b(java.util.ArrayList, java.util.ArrayList):boolean");
    }

    private void c(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (arrayList2 == null || arrayList.size() != arrayList2.size()) {
                throw new IllegalStateException("Internal error with the back stack records");
            }
            a(arrayList, arrayList2);
            int size = arrayList.size();
            int i2 = 0;
            int i3 = 0;
            while (i2 < size) {
                if (!arrayList.get(i2).q) {
                    if (i3 != i2) {
                        b(arrayList, arrayList2, i3, i2);
                    }
                    i3 = i2 + 1;
                    if (arrayList2.get(i2).booleanValue()) {
                        while (i3 < size && arrayList2.get(i3).booleanValue() && !arrayList.get(i3).q) {
                            i3++;
                        }
                    }
                    b(arrayList, arrayList2, i2, i3);
                    i2 = i3 - 1;
                }
                i2++;
            }
            if (i3 != size) {
                b(arrayList, arrayList2, i3, size);
            }
        }
    }

    private void c(boolean z2) {
        if (this.g) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        } else if (this.t == null) {
            throw new IllegalStateException("Fragment host has been destroyed");
        } else if (Looper.myLooper() == this.t.g().getLooper()) {
            if (!z2) {
                C();
            }
            if (this.C == null) {
                this.C = new ArrayList<>();
                this.D = new ArrayList<>();
            }
            this.g = true;
            try {
                a((ArrayList<C0131a>) null, (ArrayList<Boolean>) null);
            } finally {
                this.g = false;
            }
        } else {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }
    }

    public static int d(int i2) {
        if (i2 == 4097) {
            return 8194;
        }
        if (i2 != 4099) {
            return i2 != 8194 ? 0 : 4097;
        }
        return 4099;
    }

    /* JADX INFO: finally extract failed */
    private void e(int i2) {
        try {
            this.g = true;
            a(i2, false);
            this.g = false;
            p();
        } catch (Throwable th) {
            this.g = false;
            throw th;
        }
    }

    private void u(@Nullable Fragment fragment) {
        if (fragment != null && this.j.get(fragment.f) == fragment) {
            fragment.Z();
        }
    }

    private Fragment v(Fragment fragment) {
        ViewGroup viewGroup = fragment.G;
        View view = fragment.H;
        if (!(viewGroup == null || view == null)) {
            for (int indexOf = this.i.indexOf(fragment) - 1; indexOf >= 0; indexOf--) {
                Fragment fragment2 = this.i.get(indexOf);
                if (fragment2.G == viewGroup && fragment2.H != null) {
                    return fragment2;
                }
            }
        }
        return null;
    }

    private boolean w(Fragment fragment) {
        return (fragment.D && fragment.E) || fragment.u.d();
    }

    /* access modifiers changed from: package-private */
    public void A() {
        for (Fragment next : this.j.values()) {
            if (next != null) {
                n(next);
            }
        }
    }

    @Nullable
    public Fragment a(int i2) {
        for (int size = this.i.size() - 1; size >= 0; size--) {
            Fragment fragment = this.i.get(size);
            if (fragment != null && fragment.w == i2) {
                return fragment;
            }
        }
        for (Fragment next : this.j.values()) {
            if (next != null && next.w == i2) {
                return next;
            }
        }
        return null;
    }

    @Nullable
    public Fragment a(Bundle bundle, String str) {
        String string = bundle.getString(str);
        if (string == null) {
            return null;
        }
        Fragment fragment = this.j.get(string);
        if (fragment != null) {
            return fragment;
        }
        a((RuntimeException) new IllegalStateException("Fragment no longer exists for key " + str + ": unique id " + string));
        throw null;
    }

    @Nullable
    public Fragment a(@Nullable String str) {
        if (str != null) {
            for (int size = this.i.size() - 1; size >= 0; size--) {
                Fragment fragment = this.i.get(size);
                if (fragment != null && str.equals(fragment.y)) {
                    return fragment;
                }
            }
        }
        if (str == null) {
            return null;
        }
        for (Fragment next : this.j.values()) {
            if (next != null && str.equals(next.y)) {
                return next;
            }
        }
        return null;
    }

    @NonNull
    public C0140j a() {
        if (super.a() == C0142l.f920a) {
            Fragment fragment = this.v;
            if (fragment != null) {
                return fragment.s.a();
            }
            a(new s(this));
        }
        return super.a();
    }

    /* access modifiers changed from: package-private */
    public a a(Fragment fragment, int i2, boolean z2, int i3) {
        int b2;
        int s2 = fragment.s();
        boolean z3 = false;
        fragment.a(0);
        ViewGroup viewGroup = fragment.G;
        if (viewGroup != null && viewGroup.getLayoutTransition() != null) {
            return null;
        }
        Animation a2 = fragment.a(i2, z2, s2);
        if (a2 != null) {
            return new a(a2);
        }
        Animator b3 = fragment.b(i2, z2, s2);
        if (b3 != null) {
            return new a(b3);
        }
        if (s2 != 0) {
            boolean equals = "anim".equals(this.t.f().getResources().getResourceTypeName(s2));
            if (equals) {
                try {
                    Animation loadAnimation = AnimationUtils.loadAnimation(this.t.f(), s2);
                    if (loadAnimation != null) {
                        return new a(loadAnimation);
                    }
                    z3 = true;
                } catch (Resources.NotFoundException e2) {
                    throw e2;
                } catch (RuntimeException unused) {
                }
            }
            if (!z3) {
                try {
                    Animator loadAnimator = AnimatorInflater.loadAnimator(this.t.f(), s2);
                    if (loadAnimator != null) {
                        return new a(loadAnimator);
                    }
                } catch (RuntimeException e3) {
                    if (!equals) {
                        Animation loadAnimation2 = AnimationUtils.loadAnimation(this.t.f(), s2);
                        if (loadAnimation2 != null) {
                            return new a(loadAnimation2);
                        }
                    } else {
                        throw e3;
                    }
                }
            }
        }
        if (i2 == 0 || (b2 = b(i2, z2)) < 0) {
            return null;
        }
        switch (b2) {
            case 1:
                return a(1.125f, 1.0f, 0.0f, 1.0f);
            case 2:
                return a(1.0f, 0.975f, 1.0f, 0.0f);
            case 3:
                return a(0.975f, 1.0f, 0.0f, 1.0f);
            case 4:
                return a(1.0f, 1.075f, 1.0f, 0.0f);
            case 5:
                return a(0.0f, 1.0f);
            case 6:
                return a(1.0f, 0.0f);
            default:
                if (i3 == 0 && this.t.k()) {
                    i3 = this.t.j();
                }
                if (i3 == 0) {
                }
                return null;
        }
    }

    public void a(int i2, C0131a aVar) {
        synchronized (this) {
            if (this.o == null) {
                this.o = new ArrayList<>();
            }
            int size = this.o.size();
            if (i2 < size) {
                if (f937c) {
                    Log.v("FragmentManager", "Setting back stack index " + i2 + " to " + aVar);
                }
                this.o.set(i2, aVar);
            } else {
                while (size < i2) {
                    this.o.add((Object) null);
                    if (this.p == null) {
                        this.p = new ArrayList<>();
                    }
                    if (f937c) {
                        Log.v("FragmentManager", "Adding available back stack index " + size);
                    }
                    this.p.add(Integer.valueOf(size));
                    size++;
                }
                if (f937c) {
                    Log.v("FragmentManager", "Adding back stack index " + i2 + " with " + aVar);
                }
                this.o.add(aVar);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, boolean z2) {
        C0141k kVar;
        if (this.t == null && i2 != 0) {
            throw new IllegalStateException("No activity");
        } else if (z2 || i2 != this.s) {
            this.s = i2;
            int size = this.i.size();
            for (int i3 = 0; i3 < size; i3++) {
                l(this.i.get(i3));
            }
            for (Fragment next : this.j.values()) {
                if (next != null && ((next.m || next.A) && !next.N)) {
                    l(next);
                }
            }
            A();
            if (this.x && (kVar = this.t) != null && this.s == 4) {
                kVar.l();
                this.x = false;
            }
        }
    }

    public void a(@NonNull Configuration configuration) {
        for (int i2 = 0; i2 < this.i.size(); i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null) {
                fragment.a(configuration);
            }
        }
    }

    public void a(Bundle bundle, String str, Fragment fragment) {
        if (fragment.s == this) {
            bundle.putString(str, fragment.f);
            return;
        }
        a((RuntimeException) new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        throw null;
    }

    /* access modifiers changed from: package-private */
    public void a(Parcelable parcelable) {
        FragmentState fragmentState;
        if (parcelable != null) {
            FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
            if (fragmentManagerState.mActive != null) {
                for (Fragment next : this.I.c()) {
                    if (f937c) {
                        Log.v("FragmentManager", "restoreSaveState: re-attaching retained " + next);
                    }
                    Iterator<FragmentState> it = fragmentManagerState.mActive.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            fragmentState = null;
                            break;
                        }
                        fragmentState = it.next();
                        if (fragmentState.mWho.equals(next.f)) {
                            break;
                        }
                    }
                    if (fragmentState == null) {
                        if (f937c) {
                            Log.v("FragmentManager", "Discarding retained Fragment " + next + " that was not found in the set of active Fragments " + fragmentManagerState.mActive);
                        }
                        Fragment fragment = next;
                        a(fragment, 1, 0, 0, false);
                        next.m = true;
                        a(fragment, 0, 0, 0, false);
                    } else {
                        fragmentState.mInstance = next;
                        next.f885d = null;
                        next.r = 0;
                        next.o = false;
                        next.l = false;
                        Fragment fragment2 = next.h;
                        next.i = fragment2 != null ? fragment2.f : null;
                        next.h = null;
                        Bundle bundle = fragmentState.mSavedFragmentState;
                        if (bundle != null) {
                            bundle.setClassLoader(this.t.f().getClassLoader());
                            next.f885d = fragmentState.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                            next.f884c = fragmentState.mSavedFragmentState;
                        }
                    }
                }
                this.j.clear();
                Iterator<FragmentState> it2 = fragmentManagerState.mActive.iterator();
                while (it2.hasNext()) {
                    FragmentState next2 = it2.next();
                    if (next2 != null) {
                        Fragment instantiate = next2.instantiate(this.t.f().getClassLoader(), a());
                        instantiate.s = this;
                        if (f937c) {
                            Log.v("FragmentManager", "restoreSaveState: active (" + instantiate.f + "): " + instantiate);
                        }
                        this.j.put(instantiate.f, instantiate);
                        next2.mInstance = null;
                    }
                }
                this.i.clear();
                ArrayList<String> arrayList = fragmentManagerState.mAdded;
                if (arrayList != null) {
                    Iterator<String> it3 = arrayList.iterator();
                    while (it3.hasNext()) {
                        String next3 = it3.next();
                        Fragment fragment3 = this.j.get(next3);
                        if (fragment3 != null) {
                            fragment3.l = true;
                            if (f937c) {
                                Log.v("FragmentManager", "restoreSaveState: added (" + next3 + "): " + fragment3);
                            }
                            if (!this.i.contains(fragment3)) {
                                synchronized (this.i) {
                                    this.i.add(fragment3);
                                }
                            } else {
                                throw new IllegalStateException("Already added " + fragment3);
                            }
                        } else {
                            a((RuntimeException) new IllegalStateException("No instantiated fragment for (" + next3 + ")"));
                            throw null;
                        }
                    }
                }
                BackStackState[] backStackStateArr = fragmentManagerState.mBackStack;
                if (backStackStateArr != null) {
                    this.k = new ArrayList<>(backStackStateArr.length);
                    int i2 = 0;
                    while (true) {
                        BackStackState[] backStackStateArr2 = fragmentManagerState.mBackStack;
                        if (i2 >= backStackStateArr2.length) {
                            break;
                        }
                        C0131a instantiate2 = backStackStateArr2[i2].instantiate(this);
                        if (f937c) {
                            Log.v("FragmentManager", "restoreAllState: back stack #" + i2 + " (index " + instantiate2.u + "): " + instantiate2);
                            PrintWriter printWriter = new PrintWriter(new a.d.e.b("FragmentManager"));
                            instantiate2.a("  ", printWriter, false);
                            printWriter.close();
                        }
                        this.k.add(instantiate2);
                        int i3 = instantiate2.u;
                        if (i3 >= 0) {
                            a(i3, instantiate2);
                        }
                        i2++;
                    }
                } else {
                    this.k = null;
                }
                String str = fragmentManagerState.mPrimaryNavActiveWho;
                if (str != null) {
                    this.w = this.j.get(str);
                    u(this.w);
                }
                this.h = fragmentManagerState.mNextFragmentIndex;
            }
        }
    }

    public void a(@NonNull Menu menu) {
        if (this.s >= 1) {
            for (int i2 = 0; i2 < this.i.size(); i2++) {
                Fragment fragment = this.i.get(i2);
                if (fragment != null) {
                    fragment.c(menu);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull Fragment fragment) {
        if (v()) {
            if (f937c) {
                Log.v("FragmentManager", "Ignoring addRetainedFragment as the state is already saved");
            }
        } else if (this.I.a(fragment) && f937c) {
            Log.v("FragmentManager", "Updating retained Fragments: Added " + fragment);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v9, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: int} */
    /* JADX WARNING: type inference failed for: r11v1 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02ff  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:268:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(androidx.fragment.app.Fragment r19, int r20, int r21, int r22, boolean r23) {
        /*
            r18 = this;
            r6 = r18
            r7 = r19
            boolean r0 = r7.l
            r8 = 1
            if (r0 == 0) goto L_0x0011
            boolean r0 = r7.A
            if (r0 == 0) goto L_0x000e
            goto L_0x0011
        L_0x000e:
            r0 = r20
            goto L_0x0016
        L_0x0011:
            r0 = r20
            if (r0 <= r8) goto L_0x0016
            r0 = r8
        L_0x0016:
            boolean r1 = r7.m
            if (r1 == 0) goto L_0x002a
            int r1 = r7.f883b
            if (r0 <= r1) goto L_0x002a
            if (r1 != 0) goto L_0x0028
            boolean r0 = r19.H()
            if (r0 == 0) goto L_0x0028
            r0 = r8
            goto L_0x002a
        L_0x0028:
            int r0 = r7.f883b
        L_0x002a:
            boolean r1 = r7.J
            r9 = 3
            r10 = 2
            if (r1 == 0) goto L_0x0037
            int r1 = r7.f883b
            if (r1 >= r9) goto L_0x0037
            if (r0 <= r10) goto L_0x0037
            r0 = r10
        L_0x0037:
            androidx.lifecycle.f$b r1 = r7.S
            androidx.lifecycle.f$b r2 = androidx.lifecycle.f.b.CREATED
            if (r1 != r2) goto L_0x0042
            int r0 = java.lang.Math.min(r0, r8)
            goto L_0x004a
        L_0x0042:
            int r1 = r1.ordinal()
            int r0 = java.lang.Math.min(r0, r1)
        L_0x004a:
            r11 = r0
            int r0 = r7.f883b
            java.lang.String r12 = "FragmentManager"
            r13 = 0
            r14 = 0
            if (r0 > r11) goto L_0x0323
            boolean r0 = r7.n
            if (r0 == 0) goto L_0x005c
            boolean r0 = r7.o
            if (r0 != 0) goto L_0x005c
            return
        L_0x005c:
            android.view.View r0 = r19.i()
            if (r0 != 0) goto L_0x0068
            android.animation.Animator r0 = r19.j()
            if (r0 == 0) goto L_0x007c
        L_0x0068:
            r7.a((android.view.View) r14)
            r7.a((android.animation.Animator) r14)
            int r2 = r19.C()
            r3 = 0
            r4 = 0
            r5 = 1
            r0 = r18
            r1 = r19
            r0.a((androidx.fragment.app.Fragment) r1, (int) r2, (int) r3, (int) r4, (boolean) r5)
        L_0x007c:
            int r0 = r7.f883b
            if (r0 == 0) goto L_0x008e
            if (r0 == r8) goto L_0x01e7
            if (r0 == r10) goto L_0x008b
            if (r0 == r9) goto L_0x0088
            goto L_0x04d8
        L_0x0088:
            r0 = r9
            goto L_0x02fd
        L_0x008b:
            r0 = r10
            goto L_0x02dc
        L_0x008e:
            if (r11 <= 0) goto L_0x01e7
            boolean r0 = f937c
            if (r0 == 0) goto L_0x00a8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveto CREATED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x00a8:
            android.os.Bundle r0 = r7.f884c
            if (r0 == 0) goto L_0x0101
            androidx.fragment.app.k r1 = r6.t
            android.content.Context r1 = r1.f()
            java.lang.ClassLoader r1 = r1.getClassLoader()
            r0.setClassLoader(r1)
            android.os.Bundle r0 = r7.f884c
            java.lang.String r1 = "android:view_state"
            android.util.SparseArray r0 = r0.getSparseParcelableArray(r1)
            r7.f885d = r0
            android.os.Bundle r0 = r7.f884c
            java.lang.String r1 = "android:target_state"
            androidx.fragment.app.Fragment r0 = r6.a((android.os.Bundle) r0, (java.lang.String) r1)
            if (r0 == 0) goto L_0x00d0
            java.lang.String r0 = r0.f
            goto L_0x00d1
        L_0x00d0:
            r0 = r14
        L_0x00d1:
            r7.i = r0
            java.lang.String r0 = r7.i
            if (r0 == 0) goto L_0x00e1
            android.os.Bundle r0 = r7.f884c
            java.lang.String r1 = "android:target_req_state"
            int r0 = r0.getInt(r1, r13)
            r7.j = r0
        L_0x00e1:
            java.lang.Boolean r0 = r7.e
            if (r0 == 0) goto L_0x00ee
            boolean r0 = r0.booleanValue()
            r7.K = r0
            r7.e = r14
            goto L_0x00f8
        L_0x00ee:
            android.os.Bundle r0 = r7.f884c
            java.lang.String r1 = "android:user_visible_hint"
            boolean r0 = r0.getBoolean(r1, r8)
            r7.K = r0
        L_0x00f8:
            boolean r0 = r7.K
            if (r0 != 0) goto L_0x0101
            r7.J = r8
            if (r11 <= r10) goto L_0x0101
            r11 = r10
        L_0x0101:
            androidx.fragment.app.k r0 = r6.t
            r7.t = r0
            androidx.fragment.app.Fragment r1 = r6.v
            r7.v = r1
            if (r1 == 0) goto L_0x010e
            androidx.fragment.app.t r0 = r1.u
            goto L_0x0110
        L_0x010e:
            androidx.fragment.app.t r0 = r0.e
        L_0x0110:
            r7.s = r0
            androidx.fragment.app.Fragment r0 = r7.h
            java.lang.String r15 = " that does not belong to this FragmentManager!"
            java.lang.String r5 = " declared target fragment "
            java.lang.String r4 = "Fragment "
            if (r0 == 0) goto L_0x016b
            java.util.HashMap<java.lang.String, androidx.fragment.app.Fragment> r1 = r6.j
            java.lang.String r0 = r0.f
            java.lang.Object r0 = r1.get(r0)
            androidx.fragment.app.Fragment r1 = r7.h
            if (r0 != r1) goto L_0x0149
            int r0 = r1.f883b
            if (r0 >= r8) goto L_0x013e
            r2 = 1
            r3 = 0
            r16 = 0
            r17 = 1
            r0 = r18
            r9 = r4
            r4 = r16
            r10 = r5
            r5 = r17
            r0.a((androidx.fragment.app.Fragment) r1, (int) r2, (int) r3, (int) r4, (boolean) r5)
            goto L_0x0140
        L_0x013e:
            r9 = r4
            r10 = r5
        L_0x0140:
            androidx.fragment.app.Fragment r0 = r7.h
            java.lang.String r0 = r0.f
            r7.i = r0
            r7.h = r14
            goto L_0x016d
        L_0x0149:
            r9 = r4
            r10 = r5
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r7)
            r1.append(r10)
            androidx.fragment.app.Fragment r2 = r7.h
            r1.append(r2)
            r1.append(r15)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x016b:
            r9 = r4
            r10 = r5
        L_0x016d:
            java.lang.String r0 = r7.i
            if (r0 == 0) goto L_0x01aa
            java.util.HashMap<java.lang.String, androidx.fragment.app.Fragment> r1 = r6.j
            java.lang.Object r0 = r1.get(r0)
            r1 = r0
            androidx.fragment.app.Fragment r1 = (androidx.fragment.app.Fragment) r1
            if (r1 == 0) goto L_0x018a
            int r0 = r1.f883b
            if (r0 >= r8) goto L_0x01aa
            r2 = 1
            r3 = 0
            r4 = 0
            r5 = 1
            r0 = r18
            r0.a((androidx.fragment.app.Fragment) r1, (int) r2, (int) r3, (int) r4, (boolean) r5)
            goto L_0x01aa
        L_0x018a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r7)
            r1.append(r10)
            java.lang.String r2 = r7.i
            r1.append(r2)
            r1.append(r15)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x01aa:
            androidx.fragment.app.k r0 = r6.t
            android.content.Context r0 = r0.f()
            r6.b((androidx.fragment.app.Fragment) r7, (android.content.Context) r0, (boolean) r13)
            r19.T()
            androidx.fragment.app.Fragment r0 = r7.v
            if (r0 != 0) goto L_0x01c0
            androidx.fragment.app.k r0 = r6.t
            r0.a((androidx.fragment.app.Fragment) r7)
            goto L_0x01c3
        L_0x01c0:
            r0.a((androidx.fragment.app.Fragment) r7)
        L_0x01c3:
            androidx.fragment.app.k r0 = r6.t
            android.content.Context r0 = r0.f()
            r6.a((androidx.fragment.app.Fragment) r7, (android.content.Context) r0, (boolean) r13)
            boolean r0 = r7.R
            if (r0 != 0) goto L_0x01e0
            android.os.Bundle r0 = r7.f884c
            r6.c(r7, r0, r13)
            android.os.Bundle r0 = r7.f884c
            r7.h(r0)
            android.os.Bundle r0 = r7.f884c
            r6.b((androidx.fragment.app.Fragment) r7, (android.os.Bundle) r0, (boolean) r13)
            goto L_0x01e7
        L_0x01e0:
            android.os.Bundle r0 = r7.f884c
            r7.k(r0)
            r7.f883b = r8
        L_0x01e7:
            if (r11 <= 0) goto L_0x01ec
            r18.e((androidx.fragment.app.Fragment) r19)
        L_0x01ec:
            if (r11 <= r8) goto L_0x02db
            boolean r0 = f937c
            if (r0 == 0) goto L_0x0206
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveto ACTIVITY_CREATED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x0206:
            boolean r0 = r7.n
            if (r0 != 0) goto L_0x02c6
            int r0 = r7.x
            if (r0 == 0) goto L_0x027c
            r1 = -1
            if (r0 == r1) goto L_0x025d
            androidx.fragment.app.h r1 = r6.u
            android.view.View r0 = r1.a(r0)
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            if (r0 != 0) goto L_0x027d
            boolean r1 = r7.p
            if (r1 == 0) goto L_0x0220
            goto L_0x027d
        L_0x0220:
            android.content.res.Resources r0 = r19.x()     // Catch:{ NotFoundException -> 0x022b }
            int r1 = r7.x     // Catch:{ NotFoundException -> 0x022b }
            java.lang.String r0 = r0.getResourceName(r1)     // Catch:{ NotFoundException -> 0x022b }
            goto L_0x022d
        L_0x022b:
            java.lang.String r0 = "unknown"
        L_0x022d:
            java.lang.IllegalArgumentException r1 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "No view found for id 0x"
            r2.append(r3)
            int r3 = r7.x
            java.lang.String r3 = java.lang.Integer.toHexString(r3)
            r2.append(r3)
            java.lang.String r3 = " ("
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = ") for fragment "
            r2.append(r0)
            r2.append(r7)
            java.lang.String r0 = r2.toString()
            r1.<init>(r0)
            r6.a((java.lang.RuntimeException) r1)
            throw r14
        L_0x025d:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Cannot create fragment "
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = " for a container view with no id"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            r6.a((java.lang.RuntimeException) r0)
            throw r14
        L_0x027c:
            r0 = r14
        L_0x027d:
            r7.G = r0
            android.os.Bundle r1 = r7.f884c
            android.view.LayoutInflater r1 = r7.i(r1)
            android.os.Bundle r2 = r7.f884c
            r7.b((android.view.LayoutInflater) r1, (android.view.ViewGroup) r0, (android.os.Bundle) r2)
            android.view.View r1 = r7.H
            if (r1 == 0) goto L_0x02c4
            r7.I = r1
            r1.setSaveFromParentEnabled(r13)
            if (r0 == 0) goto L_0x029a
            android.view.View r1 = r7.H
            r0.addView(r1)
        L_0x029a:
            boolean r0 = r7.z
            if (r0 == 0) goto L_0x02a5
            android.view.View r0 = r7.H
            r1 = 8
            r0.setVisibility(r1)
        L_0x02a5:
            android.view.View r0 = r7.H
            android.os.Bundle r1 = r7.f884c
            r7.a((android.view.View) r0, (android.os.Bundle) r1)
            android.view.View r0 = r7.H
            android.os.Bundle r1 = r7.f884c
            r6.a((androidx.fragment.app.Fragment) r7, (android.view.View) r0, (android.os.Bundle) r1, (boolean) r13)
            android.view.View r0 = r7.H
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x02c0
            android.view.ViewGroup r0 = r7.G
            if (r0 == 0) goto L_0x02c0
            goto L_0x02c1
        L_0x02c0:
            r8 = r13
        L_0x02c1:
            r7.N = r8
            goto L_0x02c6
        L_0x02c4:
            r7.I = r14
        L_0x02c6:
            android.os.Bundle r0 = r7.f884c
            r7.g((android.os.Bundle) r0)
            android.os.Bundle r0 = r7.f884c
            r6.a((androidx.fragment.app.Fragment) r7, (android.os.Bundle) r0, (boolean) r13)
            android.view.View r0 = r7.H
            if (r0 == 0) goto L_0x02d9
            android.os.Bundle r0 = r7.f884c
            r7.l(r0)
        L_0x02d9:
            r7.f884c = r14
        L_0x02db:
            r0 = 2
        L_0x02dc:
            if (r11 <= r0) goto L_0x02fc
            boolean r0 = f937c
            if (r0 == 0) goto L_0x02f6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveto STARTED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x02f6:
            r19.ba()
            r6.f(r7, r13)
        L_0x02fc:
            r0 = 3
        L_0x02fd:
            if (r11 <= r0) goto L_0x04d8
            boolean r0 = f937c
            if (r0 == 0) goto L_0x0317
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveto RESUMED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x0317:
            r19.aa()
            r6.e(r7, r13)
            r7.f884c = r14
            r7.f885d = r14
            goto L_0x04d8
        L_0x0323:
            if (r0 <= r11) goto L_0x04d8
            if (r0 == r8) goto L_0x0407
            r1 = 2
            if (r0 == r1) goto L_0x0377
            r1 = 3
            if (r0 == r1) goto L_0x0354
            r1 = 4
            if (r0 == r1) goto L_0x0332
            goto L_0x04d8
        L_0x0332:
            if (r11 >= r1) goto L_0x0352
            boolean r0 = f937c
            if (r0 == 0) goto L_0x034c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "movefrom RESUMED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x034c:
            r19.Y()
            r6.d(r7, r13)
        L_0x0352:
            r0 = 3
            goto L_0x0355
        L_0x0354:
            r0 = r1
        L_0x0355:
            if (r11 >= r0) goto L_0x0375
            boolean r0 = f937c
            if (r0 == 0) goto L_0x036f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "movefrom STARTED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x036f:
            r19.ca()
            r6.g(r7, r13)
        L_0x0375:
            r0 = 2
            goto L_0x0378
        L_0x0377:
            r0 = r1
        L_0x0378:
            if (r11 >= r0) goto L_0x0407
            boolean r0 = f937c
            if (r0 == 0) goto L_0x0392
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "movefrom ACTIVITY_CREATED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x0392:
            android.view.View r0 = r7.H
            if (r0 == 0) goto L_0x03a5
            androidx.fragment.app.k r0 = r6.t
            boolean r0 = r0.b(r7)
            if (r0 == 0) goto L_0x03a5
            android.util.SparseArray<android.os.Parcelable> r0 = r7.f885d
            if (r0 != 0) goto L_0x03a5
            r18.r(r19)
        L_0x03a5:
            r19.V()
            r6.h(r7, r13)
            android.view.View r0 = r7.H
            if (r0 == 0) goto L_0x03f8
            android.view.ViewGroup r1 = r7.G
            if (r1 == 0) goto L_0x03f8
            r1.endViewTransition(r0)
            android.view.View r0 = r7.H
            r0.clearAnimation()
            androidx.fragment.app.Fragment r0 = r19.v()
            if (r0 == 0) goto L_0x03c9
            androidx.fragment.app.Fragment r0 = r19.v()
            boolean r0 = r0.m
            if (r0 != 0) goto L_0x03f8
        L_0x03c9:
            int r0 = r6.s
            r1 = 0
            if (r0 <= 0) goto L_0x03e9
            boolean r0 = r6.A
            if (r0 != 0) goto L_0x03e9
            android.view.View r0 = r7.H
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x03e9
            float r0 = r7.P
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x03e9
            r0 = r21
            r2 = r22
            androidx.fragment.app.t$a r0 = r6.a((androidx.fragment.app.Fragment) r7, (int) r0, (boolean) r13, (int) r2)
            goto L_0x03ea
        L_0x03e9:
            r0 = r14
        L_0x03ea:
            r7.P = r1
            if (r0 == 0) goto L_0x03f1
            r6.a((androidx.fragment.app.Fragment) r7, (androidx.fragment.app.t.a) r0, (int) r11)
        L_0x03f1:
            android.view.ViewGroup r0 = r7.G
            android.view.View r1 = r7.H
            r0.removeView(r1)
        L_0x03f8:
            r7.G = r14
            r7.H = r14
            r7.U = r14
            androidx.lifecycle.p<androidx.lifecycle.i> r0 = r7.V
            r0.a(r14)
            r7.I = r14
            r7.o = r13
        L_0x0407:
            if (r11 >= r8) goto L_0x04d8
            boolean r0 = r6.A
            if (r0 == 0) goto L_0x042e
            android.view.View r0 = r19.i()
            if (r0 == 0) goto L_0x041e
            android.view.View r0 = r19.i()
            r7.a((android.view.View) r14)
            r0.clearAnimation()
            goto L_0x042e
        L_0x041e:
            android.animation.Animator r0 = r19.j()
            if (r0 == 0) goto L_0x042e
            android.animation.Animator r0 = r19.j()
            r7.a((android.animation.Animator) r14)
            r0.cancel()
        L_0x042e:
            android.view.View r0 = r19.i()
            if (r0 != 0) goto L_0x04d4
            android.animation.Animator r0 = r19.j()
            if (r0 == 0) goto L_0x043c
            goto L_0x04d4
        L_0x043c:
            boolean r0 = f937c
            if (r0 == 0) goto L_0x0454
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "movefrom CREATED: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            android.util.Log.v(r12, r0)
        L_0x0454:
            boolean r0 = r7.m
            if (r0 == 0) goto L_0x0460
            boolean r0 = r19.H()
            if (r0 != 0) goto L_0x0460
            r0 = r8
            goto L_0x0461
        L_0x0460:
            r0 = r13
        L_0x0461:
            if (r0 != 0) goto L_0x046f
            androidx.fragment.app.w r1 = r6.I
            boolean r1 = r1.f(r7)
            if (r1 == 0) goto L_0x046c
            goto L_0x046f
        L_0x046c:
            r7.f883b = r13
            goto L_0x04a0
        L_0x046f:
            androidx.fragment.app.k r1 = r6.t
            boolean r2 = r1 instanceof androidx.lifecycle.v
            if (r2 == 0) goto L_0x047c
            androidx.fragment.app.w r1 = r6.I
            boolean r8 = r1.d()
            goto L_0x0491
        L_0x047c:
            android.content.Context r1 = r1.f()
            boolean r1 = r1 instanceof android.app.Activity
            if (r1 == 0) goto L_0x0491
            androidx.fragment.app.k r1 = r6.t
            android.content.Context r1 = r1.f()
            android.app.Activity r1 = (android.app.Activity) r1
            boolean r1 = r1.isChangingConfigurations()
            r8 = r8 ^ r1
        L_0x0491:
            if (r0 != 0) goto L_0x0495
            if (r8 == 0) goto L_0x049a
        L_0x0495:
            androidx.fragment.app.w r1 = r6.I
            r1.b(r7)
        L_0x049a:
            r19.U()
            r6.b((androidx.fragment.app.Fragment) r7, (boolean) r13)
        L_0x04a0:
            r19.W()
            r6.c((androidx.fragment.app.Fragment) r7, (boolean) r13)
            if (r23 != 0) goto L_0x04d8
            if (r0 != 0) goto L_0x04d0
            androidx.fragment.app.w r0 = r6.I
            boolean r0 = r0.f(r7)
            if (r0 == 0) goto L_0x04b3
            goto L_0x04d0
        L_0x04b3:
            r7.t = r14
            r7.v = r14
            r7.s = r14
            java.lang.String r0 = r7.i
            if (r0 == 0) goto L_0x04d8
            java.util.HashMap<java.lang.String, androidx.fragment.app.Fragment> r1 = r6.j
            java.lang.Object r0 = r1.get(r0)
            androidx.fragment.app.Fragment r0 = (androidx.fragment.app.Fragment) r0
            if (r0 == 0) goto L_0x04d8
            boolean r1 = r0.y()
            if (r1 == 0) goto L_0x04d8
            r7.h = r0
            goto L_0x04d8
        L_0x04d0:
            r18.k(r19)
            goto L_0x04d8
        L_0x04d4:
            r7.b((int) r11)
            goto L_0x04d9
        L_0x04d8:
            r8 = r11
        L_0x04d9:
            int r0 = r7.f883b
            if (r0 == r8) goto L_0x0505
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "moveToState: Fragment state for "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r1 = " not updated inline; expected state "
            r0.append(r1)
            r0.append(r8)
            java.lang.String r1 = " found "
            r0.append(r1)
            int r1 = r7.f883b
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Log.w(r12, r0)
            r7.f883b = r8
        L_0x0505:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.t.a(androidx.fragment.app.Fragment, int, int, int, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull Fragment fragment, @NonNull Context context, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).a(fragment, context, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.a((C0142l) this, fragment, context);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull Fragment fragment, @Nullable Bundle bundle, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).a(fragment, bundle, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.a((C0142l) this, fragment, bundle);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(@NonNull Fragment fragment, @NonNull View view, @Nullable Bundle bundle, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).a(fragment, view, bundle, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.a(this, fragment, view, bundle);
            }
        }
    }

    public void a(Fragment fragment, f.b bVar) {
        if (this.j.get(fragment.f) == fragment && (fragment.t == null || fragment.q() == this)) {
            fragment.S = bVar;
            return;
        }
        throw new IllegalArgumentException("Fragment " + fragment + " is not an active fragment of FragmentManager " + this);
    }

    public void a(Fragment fragment, boolean z2) {
        if (f937c) {
            Log.v("FragmentManager", "add: " + fragment);
        }
        j(fragment);
        if (fragment.A) {
            return;
        }
        if (!this.i.contains(fragment)) {
            synchronized (this.i) {
                this.i.add(fragment);
            }
            fragment.l = true;
            fragment.m = false;
            if (fragment.H == null) {
                fragment.O = false;
            }
            if (w(fragment)) {
                this.x = true;
            }
            if (z2) {
                m(fragment);
                return;
            }
            return;
        }
        throw new IllegalStateException("Fragment already added: " + fragment);
    }

    /* access modifiers changed from: package-private */
    public void a(C0131a aVar) {
        if (this.k == null) {
            this.k = new ArrayList<>();
        }
        this.k.add(aVar);
    }

    /* access modifiers changed from: package-private */
    public void a(C0131a aVar, boolean z2, boolean z3, boolean z4) {
        if (z2) {
            aVar.a(z4);
        } else {
            aVar.a();
        }
        ArrayList arrayList = new ArrayList(1);
        ArrayList arrayList2 = new ArrayList(1);
        arrayList.add(aVar);
        arrayList2.add(Boolean.valueOf(z2));
        if (z3) {
            E.a(this, (ArrayList<C0131a>) arrayList, (ArrayList<Boolean>) arrayList2, 0, 1, true);
        }
        if (z4) {
            a(this.s, true);
        }
        for (Fragment next : this.j.values()) {
            if (next != null && next.H != null && next.N && aVar.b(next.x)) {
                float f2 = next.P;
                if (f2 > 0.0f) {
                    next.H.setAlpha(f2);
                }
                if (z4) {
                    next.P = 0.0f;
                } else {
                    next.P = -1.0f;
                    next.N = false;
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: androidx.activity.e} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: androidx.fragment.app.Fragment} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: androidx.fragment.app.Fragment} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: androidx.fragment.app.Fragment} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(@androidx.annotation.NonNull androidx.fragment.app.C0141k r3, @androidx.annotation.NonNull androidx.fragment.app.C0138h r4, @androidx.annotation.Nullable androidx.fragment.app.Fragment r5) {
        /*
            r2 = this;
            androidx.fragment.app.k r0 = r2.t
            if (r0 != 0) goto L_0x004a
            r2.t = r3
            r2.u = r4
            r2.v = r5
            androidx.fragment.app.Fragment r4 = r2.v
            if (r4 == 0) goto L_0x0011
            r2.G()
        L_0x0011:
            boolean r4 = r3 instanceof androidx.activity.e
            if (r4 == 0) goto L_0x0028
            r4 = r3
            androidx.activity.e r4 = (androidx.activity.e) r4
            androidx.activity.OnBackPressedDispatcher r0 = r4.b()
            r2.m = r0
            if (r5 == 0) goto L_0x0021
            r4 = r5
        L_0x0021:
            androidx.activity.OnBackPressedDispatcher r0 = r2.m
            androidx.activity.d r1 = r2.n
            r0.a(r4, r1)
        L_0x0028:
            if (r5 == 0) goto L_0x0033
            androidx.fragment.app.t r3 = r5.s
            androidx.fragment.app.w r3 = r3.f(r5)
        L_0x0030:
            r2.I = r3
            goto L_0x0049
        L_0x0033:
            boolean r4 = r3 instanceof androidx.lifecycle.v
            if (r4 == 0) goto L_0x0042
            androidx.lifecycle.v r3 = (androidx.lifecycle.v) r3
            androidx.lifecycle.u r3 = r3.d()
            androidx.fragment.app.w r3 = androidx.fragment.app.w.a((androidx.lifecycle.u) r3)
            goto L_0x0030
        L_0x0042:
            androidx.fragment.app.w r3 = new androidx.fragment.app.w
            r4 = 0
            r3.<init>(r4)
            goto L_0x0030
        L_0x0049:
            return
        L_0x004a:
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException
            java.lang.String r4 = "Already attached"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.t.a(androidx.fragment.app.k, androidx.fragment.app.h, androidx.fragment.app.Fragment):void");
    }

    public void a(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        int size;
        int size2;
        int size3;
        int size4;
        String str2 = str + "    ";
        if (!this.j.isEmpty()) {
            printWriter.print(str);
            printWriter.print("Active Fragments in ");
            printWriter.print(Integer.toHexString(System.identityHashCode(this)));
            printWriter.println(":");
            for (Fragment next : this.j.values()) {
                printWriter.print(str);
                printWriter.println(next);
                if (next != null) {
                    next.a(str2, fileDescriptor, printWriter, strArr);
                }
            }
        }
        int size5 = this.i.size();
        if (size5 > 0) {
            printWriter.print(str);
            printWriter.println("Added Fragments:");
            for (int i2 = 0; i2 < size5; i2++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i2);
                printWriter.print(": ");
                printWriter.println(this.i.get(i2).toString());
            }
        }
        ArrayList<Fragment> arrayList = this.l;
        if (arrayList != null && (size4 = arrayList.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Fragments Created Menus:");
            for (int i3 = 0; i3 < size4; i3++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i3);
                printWriter.print(": ");
                printWriter.println(this.l.get(i3).toString());
            }
        }
        ArrayList<C0131a> arrayList2 = this.k;
        if (arrayList2 != null && (size3 = arrayList2.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Back Stack:");
            for (int i4 = 0; i4 < size3; i4++) {
                C0131a aVar = this.k.get(i4);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i4);
                printWriter.print(": ");
                printWriter.println(aVar.toString());
                aVar.a(str2, printWriter);
            }
        }
        synchronized (this) {
            if (this.o != null && (size2 = this.o.size()) > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack Indices:");
                for (int i5 = 0; i5 < size2; i5++) {
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i5);
                    printWriter.print(": ");
                    printWriter.println(this.o.get(i5));
                }
            }
            if (this.p != null && this.p.size() > 0) {
                printWriter.print(str);
                printWriter.print("mAvailBackStackIndices: ");
                printWriter.println(Arrays.toString(this.p.toArray()));
            }
        }
        ArrayList<e> arrayList3 = this.f;
        if (arrayList3 != null && (size = arrayList3.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Pending Actions:");
            for (int i6 = 0; i6 < size; i6++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i6);
                printWriter.print(": ");
                printWriter.println(this.f.get(i6));
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mHost=");
        printWriter.println(this.t);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.u);
        if (this.v != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.v);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.s);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.y);
        printWriter.print(" mStopped=");
        printWriter.print(this.z);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.A);
        if (this.x) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.x);
        }
    }

    public void a(boolean z2) {
        for (int size = this.i.size() - 1; size >= 0; size--) {
            Fragment fragment = this.i.get(size);
            if (fragment != null) {
                fragment.e(z2);
            }
        }
    }

    public boolean a(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        if (this.s < 1) {
            return false;
        }
        ArrayList<Fragment> arrayList = null;
        boolean z2 = false;
        for (int i2 = 0; i2 < this.i.size(); i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null && fragment.b(menu, menuInflater)) {
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                arrayList.add(fragment);
                z2 = true;
            }
        }
        if (this.l != null) {
            for (int i3 = 0; i3 < this.l.size(); i3++) {
                Fragment fragment2 = this.l.get(i3);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.M();
                }
            }
        }
        this.l = arrayList;
        return z2;
    }

    public boolean a(@NonNull MenuItem menuItem) {
        if (this.s < 1) {
            return false;
        }
        for (int i2 = 0; i2 < this.i.size(); i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null && fragment.c(menuItem)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean a(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2, String str, int i2, int i3) {
        int i4;
        ArrayList<C0131a> arrayList3 = this.k;
        if (arrayList3 == null) {
            return false;
        }
        if (str == null && i2 < 0 && (i3 & 1) == 0) {
            int size = arrayList3.size() - 1;
            if (size < 0) {
                return false;
            }
            arrayList.add(this.k.remove(size));
            arrayList2.add(true);
        } else {
            if (str != null || i2 >= 0) {
                i4 = this.k.size() - 1;
                while (i4 >= 0) {
                    C0131a aVar = this.k.get(i4);
                    if ((str != null && str.equals(aVar.b())) || (i2 >= 0 && i2 == aVar.u)) {
                        break;
                    }
                    i4--;
                }
                if (i4 < 0) {
                    return false;
                }
                if ((i3 & 1) != 0) {
                    while (true) {
                        i4--;
                        if (i4 < 0) {
                            break;
                        }
                        C0131a aVar2 = this.k.get(i4);
                        if ((str == null || !str.equals(aVar2.b())) && (i2 < 0 || i2 != aVar2.u)) {
                            break;
                        }
                    }
                }
            } else {
                i4 = -1;
            }
            if (i4 == this.k.size() - 1) {
                return false;
            }
            for (int size2 = this.k.size() - 1; size2 > i4; size2--) {
                arrayList.add(this.k.remove(size2));
                arrayList2.add(true);
            }
        }
        return true;
    }

    public Fragment b(@NonNull String str) {
        Fragment a2;
        for (Fragment next : this.j.values()) {
            if (next != null && (a2 = next.a(str)) != null) {
                return a2;
            }
        }
        return null;
    }

    public List<Fragment> b() {
        List<Fragment> list;
        if (this.i.isEmpty()) {
            return Collections.emptyList();
        }
        synchronized (this.i) {
            list = (List) this.i.clone();
        }
        return list;
    }

    public void b(int i2) {
        synchronized (this) {
            this.o.set(i2, (Object) null);
            if (this.p == null) {
                this.p = new ArrayList<>();
            }
            if (f937c) {
                Log.v("FragmentManager", "Freeing back stack index " + i2);
            }
            this.p.add(Integer.valueOf(i2));
        }
    }

    public void b(Fragment fragment) {
        if (f937c) {
            Log.v("FragmentManager", "attach: " + fragment);
        }
        if (fragment.A) {
            fragment.A = false;
            if (fragment.l) {
                return;
            }
            if (!this.i.contains(fragment)) {
                if (f937c) {
                    Log.v("FragmentManager", "add from attach: " + fragment);
                }
                synchronized (this.i) {
                    this.i.add(fragment);
                }
                fragment.l = true;
                if (w(fragment)) {
                    this.x = true;
                    return;
                }
                return;
            }
            throw new IllegalStateException("Fragment already added: " + fragment);
        }
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull Fragment fragment, @NonNull Context context, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).b(fragment, context, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.b((C0142l) this, fragment, context);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull Fragment fragment, @Nullable Bundle bundle, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).b(fragment, bundle, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.b((C0142l) this, fragment, bundle);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void b(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).b(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.a(this, fragment);
            }
        }
    }

    public void b(boolean z2) {
        for (int size = this.i.size() - 1; size >= 0; size--) {
            Fragment fragment = this.i.get(size);
            if (fragment != null) {
                fragment.f(z2);
            }
        }
    }

    public boolean b(@NonNull Menu menu) {
        if (this.s < 1) {
            return false;
        }
        boolean z2 = false;
        for (int i2 = 0; i2 < this.i.size(); i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null && fragment.d(menu)) {
                z2 = true;
            }
        }
        return z2;
    }

    public boolean b(@NonNull MenuItem menuItem) {
        if (this.s < 1) {
            return false;
        }
        for (int i2 = 0; i2 < this.i.size(); i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null && fragment.d(menuItem)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void c(Fragment fragment) {
        Animator animator;
        if (fragment.H != null) {
            a a2 = a(fragment, fragment.t(), !fragment.z, fragment.u());
            if (a2 == null || (animator = a2.f940b) == null) {
                if (a2 != null) {
                    fragment.H.startAnimation(a2.f939a);
                    a2.f939a.start();
                }
                fragment.H.setVisibility((!fragment.z || fragment.G()) ? 0 : 8);
                if (fragment.G()) {
                    fragment.g(false);
                }
            } else {
                animator.setTarget(fragment.H);
                if (!fragment.z) {
                    fragment.H.setVisibility(0);
                } else if (fragment.G()) {
                    fragment.g(false);
                } else {
                    ViewGroup viewGroup = fragment.G;
                    View view = fragment.H;
                    viewGroup.startViewTransition(view);
                    a2.f940b.addListener(new r(this, viewGroup, view, fragment));
                }
                a2.f940b.start();
            }
        }
        if (fragment.l && w(fragment)) {
            this.x = true;
        }
        fragment.O = false;
        fragment.a(fragment.z);
    }

    /* access modifiers changed from: package-private */
    public void c(@NonNull Fragment fragment, @Nullable Bundle bundle, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).c(fragment, bundle, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.c(this, fragment, bundle);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void c(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).c(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.b(this, fragment);
            }
        }
    }

    public boolean c() {
        C();
        return a((String) null, -1, 0);
    }

    /* access modifiers changed from: package-private */
    public boolean c(int i2) {
        return this.s >= i2;
    }

    public void d(Fragment fragment) {
        if (f937c) {
            Log.v("FragmentManager", "detach: " + fragment);
        }
        if (!fragment.A) {
            fragment.A = true;
            if (fragment.l) {
                if (f937c) {
                    Log.v("FragmentManager", "remove from detach: " + fragment);
                }
                synchronized (this.i) {
                    this.i.remove(fragment);
                }
                if (w(fragment)) {
                    this.x = true;
                }
                fragment.l = false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void d(@NonNull Fragment fragment, @NonNull Bundle bundle, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).d(fragment, bundle, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.d(this, fragment, bundle);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void d(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).d(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.c(this, fragment);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean d() {
        boolean z2 = false;
        for (Fragment next : this.j.values()) {
            if (next != null) {
                z2 = w(next);
                continue;
            }
            if (z2) {
                return true;
            }
        }
        return false;
    }

    public void e() {
        this.y = false;
        this.z = false;
        e(2);
    }

    /* access modifiers changed from: package-private */
    public void e(Fragment fragment) {
        if (fragment.n && !fragment.q) {
            fragment.b(fragment.i(fragment.f884c), (ViewGroup) null, fragment.f884c);
            View view = fragment.H;
            if (view != null) {
                fragment.I = view;
                view.setSaveFromParentEnabled(false);
                if (fragment.z) {
                    fragment.H.setVisibility(8);
                }
                fragment.a(fragment.H, fragment.f884c);
                a(fragment, fragment.H, fragment.f884c, false);
                return;
            }
            fragment.I = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void e(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).e(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.d(this, fragment);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public w f(@NonNull Fragment fragment) {
        return this.I.c(fragment);
    }

    public void f() {
        this.y = false;
        this.z = false;
        e(1);
    }

    /* access modifiers changed from: package-private */
    public void f(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).f(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.e(this, fragment);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @NonNull
    public u g(@NonNull Fragment fragment) {
        return this.I.d(fragment);
    }

    public void g() {
        this.A = true;
        p();
        e(0);
        this.t = null;
        this.u = null;
        this.v = null;
        if (this.m != null) {
            this.n.c();
            this.m = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void g(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).g(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.f(this, fragment);
            }
        }
    }

    public void h() {
        e(1);
    }

    public void h(Fragment fragment) {
        if (f937c) {
            Log.v("FragmentManager", "hide: " + fragment);
        }
        if (!fragment.z) {
            fragment.z = true;
            fragment.O = true ^ fragment.O;
        }
    }

    /* access modifiers changed from: package-private */
    public void h(@NonNull Fragment fragment, boolean z2) {
        Fragment fragment2 = this.v;
        if (fragment2 != null) {
            C0142l q2 = fragment2.q();
            if (q2 instanceof t) {
                ((t) q2).h(fragment, true);
            }
        }
        Iterator<c> it = this.r.iterator();
        while (it.hasNext()) {
            c next = it.next();
            if (!z2 || next.f946b) {
                next.f945a.g(this, fragment);
            }
        }
    }

    public void i() {
        for (int i2 = 0; i2 < this.i.size(); i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null) {
                fragment.X();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean i(@Nullable Fragment fragment) {
        if (fragment == null) {
            return true;
        }
        t tVar = fragment.s;
        return fragment == tVar.s() && i(tVar.v);
    }

    public void j() {
        e(3);
    }

    /* access modifiers changed from: package-private */
    public void j(Fragment fragment) {
        if (this.j.get(fragment.f) == null) {
            this.j.put(fragment.f, fragment);
            if (fragment.C) {
                if (fragment.B) {
                    a(fragment);
                } else {
                    p(fragment);
                }
                fragment.C = false;
            }
            if (f937c) {
                Log.v("FragmentManager", "Added fragment to active set " + fragment);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void k() {
        G();
        u(this.w);
    }

    /* access modifiers changed from: package-private */
    public void k(Fragment fragment) {
        if (this.j.get(fragment.f) != null) {
            if (f937c) {
                Log.v("FragmentManager", "Removed fragment from active set " + fragment);
            }
            for (Fragment next : this.j.values()) {
                if (next != null && fragment.f.equals(next.i)) {
                    next.h = fragment;
                    next.i = null;
                }
            }
            this.j.put(fragment.f, (Object) null);
            p(fragment);
            String str = fragment.i;
            if (str != null) {
                fragment.h = this.j.get(str);
            }
            fragment.F();
        }
    }

    public void l() {
        this.y = false;
        this.z = false;
        e(4);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        r0 = r0.H;
        r1 = r11.G;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void l(androidx.fragment.app.Fragment r11) {
        /*
            r10 = this;
            if (r11 != 0) goto L_0x0003
            return
        L_0x0003:
            java.util.HashMap<java.lang.String, androidx.fragment.app.Fragment> r0 = r10.j
            java.lang.String r1 = r11.f
            boolean r0 = r0.containsKey(r1)
            if (r0 != 0) goto L_0x003a
            boolean r0 = f937c
            if (r0 == 0) goto L_0x0039
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Ignoring moving "
            r0.append(r1)
            r0.append(r11)
            java.lang.String r11 = " to state "
            r0.append(r11)
            int r11 = r10.s
            r0.append(r11)
            java.lang.String r11 = "since it is not added to "
            r0.append(r11)
            r0.append(r10)
            java.lang.String r11 = r0.toString()
            java.lang.String r0 = "FragmentManager"
            android.util.Log.v(r0, r11)
        L_0x0039:
            return
        L_0x003a:
            int r0 = r10.s
            boolean r1 = r11.m
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0051
            boolean r1 = r11.H()
            if (r1 == 0) goto L_0x004d
            int r0 = java.lang.Math.min(r0, r2)
            goto L_0x0051
        L_0x004d:
            int r0 = java.lang.Math.min(r0, r3)
        L_0x0051:
            r6 = r0
            int r7 = r11.t()
            int r8 = r11.u()
            r9 = 0
            r4 = r10
            r5 = r11
            r4.a((androidx.fragment.app.Fragment) r5, (int) r6, (int) r7, (int) r8, (boolean) r9)
            android.view.View r0 = r11.H
            if (r0 == 0) goto L_0x00be
            androidx.fragment.app.Fragment r0 = r10.v(r11)
            if (r0 == 0) goto L_0x0082
            android.view.View r0 = r0.H
            android.view.ViewGroup r1 = r11.G
            int r0 = r1.indexOfChild(r0)
            android.view.View r4 = r11.H
            int r4 = r1.indexOfChild(r4)
            if (r4 >= r0) goto L_0x0082
            r1.removeViewAt(r4)
            android.view.View r4 = r11.H
            r1.addView(r4, r0)
        L_0x0082:
            boolean r0 = r11.N
            if (r0 == 0) goto L_0x00be
            android.view.ViewGroup r0 = r11.G
            if (r0 == 0) goto L_0x00be
            float r0 = r11.P
            r1 = 0
            int r4 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0096
            android.view.View r4 = r11.H
            r4.setAlpha(r0)
        L_0x0096:
            r11.P = r1
            r11.N = r3
            int r0 = r11.t()
            int r1 = r11.u()
            androidx.fragment.app.t$a r0 = r10.a((androidx.fragment.app.Fragment) r11, (int) r0, (boolean) r2, (int) r1)
            if (r0 == 0) goto L_0x00be
            android.view.animation.Animation r1 = r0.f939a
            if (r1 == 0) goto L_0x00b2
            android.view.View r0 = r11.H
            r0.startAnimation(r1)
            goto L_0x00be
        L_0x00b2:
            android.animation.Animator r1 = r0.f940b
            android.view.View r2 = r11.H
            r1.setTarget(r2)
            android.animation.Animator r0 = r0.f940b
            r0.start()
        L_0x00be:
            boolean r0 = r11.O
            if (r0 == 0) goto L_0x00c5
            r10.c((androidx.fragment.app.Fragment) r11)
        L_0x00c5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.fragment.app.t.l(androidx.fragment.app.Fragment):void");
    }

    public void m() {
        this.y = false;
        this.z = false;
        e(3);
    }

    /* access modifiers changed from: package-private */
    public void m(Fragment fragment) {
        a(fragment, this.s, 0, 0, false);
    }

    public void n() {
        this.z = true;
        e(2);
    }

    public void n(Fragment fragment) {
        if (!fragment.J) {
            return;
        }
        if (this.g) {
            this.B = true;
            return;
        }
        fragment.J = false;
        a(fragment, this.s, 0, 0, false);
    }

    /* access modifiers changed from: package-private */
    public void o() {
        if (this.B) {
            this.B = false;
            A();
        }
    }

    public void o(Fragment fragment) {
        if (f937c) {
            Log.v("FragmentManager", "remove: " + fragment + " nesting=" + fragment.r);
        }
        boolean z2 = !fragment.H();
        if (!fragment.A || z2) {
            synchronized (this.i) {
                this.i.remove(fragment);
            }
            if (w(fragment)) {
                this.x = true;
            }
            fragment.l = false;
            fragment.m = true;
        }
    }

    @Nullable
    public View onCreateView(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        Fragment fragment;
        AttributeSet attributeSet2 = attributeSet;
        String str2 = str;
        Fragment fragment2 = null;
        if (!"fragment".equals(str)) {
            return null;
        }
        String attributeValue = attributeSet2.getAttributeValue((String) null, "class");
        Context context2 = context;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet2, d.f947a);
        int i2 = 0;
        if (attributeValue == null) {
            attributeValue = obtainStyledAttributes.getString(0);
        }
        String str3 = attributeValue;
        int resourceId = obtainStyledAttributes.getResourceId(1, -1);
        String string = obtainStyledAttributes.getString(2);
        obtainStyledAttributes.recycle();
        if (str3 == null || !C0140j.b(context.getClassLoader(), str3)) {
            return null;
        }
        if (view != null) {
            i2 = view.getId();
        }
        if (i2 == -1 && resourceId == -1 && string == null) {
            throw new IllegalArgumentException(attributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + str3);
        }
        if (resourceId != -1) {
            fragment2 = a(resourceId);
        }
        if (fragment2 == null && string != null) {
            fragment2 = a(string);
        }
        if (fragment2 == null && i2 != -1) {
            fragment2 = a(i2);
        }
        if (f937c) {
            Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(resourceId) + " fname=" + str3 + " existing=" + fragment2);
        }
        if (fragment2 == null) {
            Fragment a2 = a().a(context.getClassLoader(), str3);
            a2.n = true;
            a2.w = resourceId != 0 ? resourceId : i2;
            a2.x = i2;
            a2.y = string;
            a2.o = true;
            a2.s = this;
            C0141k kVar = this.t;
            a2.t = kVar;
            a2.a(kVar.f(), attributeSet2, a2.f884c);
            a(a2, true);
            fragment = a2;
        } else if (!fragment2.o) {
            fragment2.o = true;
            C0141k kVar2 = this.t;
            fragment2.t = kVar2;
            fragment2.a(kVar2.f(), attributeSet2, fragment2.f884c);
            fragment = fragment2;
        } else {
            throw new IllegalArgumentException(attributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(resourceId) + ", tag " + string + ", or parent id 0x" + Integer.toHexString(i2) + " with another fragment for " + str3);
        }
        if (this.s >= 1 || !fragment.n) {
            m(fragment);
        } else {
            a(fragment, 1, 0, 0, false);
        }
        View view2 = fragment.H;
        if (view2 != null) {
            if (resourceId != 0) {
                view2.setId(resourceId);
            }
            if (fragment.H.getTag() == null) {
                fragment.H.setTag(string);
            }
            return fragment.H;
        }
        throw new IllegalStateException("Fragment " + str3 + " did not create a view.");
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return onCreateView((View) null, str, context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public void p(@NonNull Fragment fragment) {
        if (v()) {
            if (f937c) {
                Log.v("FragmentManager", "Ignoring removeRetainedFragment as the state is already saved");
            }
        } else if (this.I.e(fragment) && f937c) {
            Log.v("FragmentManager", "Updating retained Fragments: Removed " + fragment);
        }
    }

    /* JADX INFO: finally extract failed */
    public boolean p() {
        c(true);
        boolean z2 = false;
        while (b(this.C, this.D)) {
            this.g = true;
            try {
                c(this.C, this.D);
                D();
                z2 = true;
            } catch (Throwable th) {
                D();
                throw th;
            }
        }
        G();
        o();
        B();
        return z2;
    }

    public int q() {
        ArrayList<C0131a> arrayList = this.k;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public Bundle q(Fragment fragment) {
        Bundle bundle;
        if (this.F == null) {
            this.F = new Bundle();
        }
        fragment.j(this.F);
        d(fragment, this.F, false);
        if (!this.F.isEmpty()) {
            bundle = this.F;
            this.F = null;
        } else {
            bundle = null;
        }
        if (fragment.H != null) {
            r(fragment);
        }
        if (fragment.f885d != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray("android:view_state", fragment.f885d);
        }
        if (!fragment.K) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean("android:user_visible_hint", fragment.K);
        }
        return bundle;
    }

    /* access modifiers changed from: package-private */
    public LayoutInflater.Factory2 r() {
        return this;
    }

    /* access modifiers changed from: package-private */
    public void r(Fragment fragment) {
        if (fragment.I != null) {
            SparseArray<Parcelable> sparseArray = this.G;
            if (sparseArray == null) {
                this.G = new SparseArray<>();
            } else {
                sparseArray.clear();
            }
            fragment.I.saveHierarchyState(this.G);
            if (this.G.size() > 0) {
                fragment.f885d = this.G;
                this.G = null;
            }
        }
    }

    @Nullable
    public Fragment s() {
        return this.w;
    }

    public void s(Fragment fragment) {
        if (fragment == null || (this.j.get(fragment.f) == fragment && (fragment.t == null || fragment.q() == this))) {
            Fragment fragment2 = this.w;
            this.w = fragment;
            u(fragment2);
            u(this.w);
            return;
        }
        throw new IllegalArgumentException("Fragment " + fragment + " is not an active fragment of FragmentManager " + this);
    }

    /* access modifiers changed from: package-private */
    public void t() {
        p();
        if (this.n.b()) {
            c();
        } else {
            this.m.a();
        }
    }

    public void t(Fragment fragment) {
        if (f937c) {
            Log.v("FragmentManager", "show: " + fragment);
        }
        if (fragment.z) {
            fragment.z = false;
            fragment.O = !fragment.O;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        Object obj = this.v;
        if (obj == null) {
            obj = this.t;
        }
        a.d.e.a.a(obj, sb);
        sb.append("}}");
        return sb.toString();
    }

    public boolean u() {
        return this.A;
    }

    public boolean v() {
        return this.y || this.z;
    }

    public void w() {
        this.y = false;
        this.z = false;
        int size = this.i.size();
        for (int i2 = 0; i2 < size; i2++) {
            Fragment fragment = this.i.get(i2);
            if (fragment != null) {
                fragment.K();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void x() {
        if (this.q != null) {
            for (int i2 = 0; i2 < this.q.size(); i2++) {
                this.q.get(i2).onBackStackChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Parcelable y() {
        ArrayList<String> arrayList;
        int size;
        F();
        E();
        p();
        this.y = true;
        BackStackState[] backStackStateArr = null;
        if (this.j.isEmpty()) {
            return null;
        }
        ArrayList<FragmentState> arrayList2 = new ArrayList<>(this.j.size());
        boolean z2 = false;
        for (Fragment next : this.j.values()) {
            if (next != null) {
                if (next.s == this) {
                    FragmentState fragmentState = new FragmentState(next);
                    arrayList2.add(fragmentState);
                    if (next.f883b <= 0 || fragmentState.mSavedFragmentState != null) {
                        fragmentState.mSavedFragmentState = next.f884c;
                    } else {
                        fragmentState.mSavedFragmentState = q(next);
                        String str = next.i;
                        if (str != null) {
                            Fragment fragment = this.j.get(str);
                            if (fragment != null) {
                                if (fragmentState.mSavedFragmentState == null) {
                                    fragmentState.mSavedFragmentState = new Bundle();
                                }
                                a(fragmentState.mSavedFragmentState, "android:target_state", fragment);
                                int i2 = next.j;
                                if (i2 != 0) {
                                    fragmentState.mSavedFragmentState.putInt("android:target_req_state", i2);
                                }
                            } else {
                                a((RuntimeException) new IllegalStateException("Failure saving state: " + next + " has target not in fragment manager: " + next.i));
                                throw null;
                            }
                        }
                    }
                    if (f937c) {
                        Log.v("FragmentManager", "Saved state of " + next + ": " + fragmentState.mSavedFragmentState);
                    }
                    z2 = true;
                } else {
                    a((RuntimeException) new IllegalStateException("Failure saving state: active " + next + " was removed from the FragmentManager"));
                    throw null;
                }
            }
        }
        if (!z2) {
            if (f937c) {
                Log.v("FragmentManager", "saveAllState: no fragments!");
            }
            return null;
        }
        int size2 = this.i.size();
        if (size2 > 0) {
            arrayList = new ArrayList<>(size2);
            Iterator<Fragment> it = this.i.iterator();
            while (it.hasNext()) {
                Fragment next2 = it.next();
                arrayList.add(next2.f);
                if (next2.s != this) {
                    a((RuntimeException) new IllegalStateException("Failure saving state: active " + next2 + " was removed from the FragmentManager"));
                    throw null;
                } else if (f937c) {
                    Log.v("FragmentManager", "saveAllState: adding fragment (" + next2.f + "): " + next2);
                }
            }
        } else {
            arrayList = null;
        }
        ArrayList<C0131a> arrayList3 = this.k;
        if (arrayList3 != null && (size = arrayList3.size()) > 0) {
            backStackStateArr = new BackStackState[size];
            for (int i3 = 0; i3 < size; i3++) {
                backStackStateArr[i3] = new BackStackState(this.k.get(i3));
                if (f937c) {
                    Log.v("FragmentManager", "saveAllState: adding back stack #" + i3 + ": " + this.k.get(i3));
                }
            }
        }
        FragmentManagerState fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = arrayList2;
        fragmentManagerState.mAdded = arrayList;
        fragmentManagerState.mBackStack = backStackStateArr;
        Fragment fragment2 = this.w;
        if (fragment2 != null) {
            fragmentManagerState.mPrimaryNavActiveWho = fragment2.f;
        }
        fragmentManagerState.mNextFragmentIndex = this.h;
        return fragmentManagerState;
    }

    /* access modifiers changed from: package-private */
    public void z() {
        synchronized (this) {
            boolean z2 = false;
            boolean z3 = this.H != null && !this.H.isEmpty();
            if (this.f != null && this.f.size() == 1) {
                z2 = true;
            }
            if (z3 || z2) {
                this.t.g().removeCallbacks(this.J);
                this.t.g().post(this.J);
                G();
            }
        }
    }
}
