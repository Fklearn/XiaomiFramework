package androidx.fragment.app;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.C0142l;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.t;
import androidx.fragment.app.z;
import java.io.PrintWriter;
import java.util.ArrayList;

/* renamed from: androidx.fragment.app.a  reason: case insensitive filesystem */
final class C0131a extends z implements C0142l.a, t.e {
    final t s;
    boolean t;
    int u = -1;

    public C0131a(t tVar) {
        this.s = tVar;
    }

    private static boolean b(z.a aVar) {
        Fragment fragment = aVar.f958b;
        return fragment != null && fragment.l && fragment.H != null && !fragment.A && !fragment.z && fragment.I();
    }

    /* access modifiers changed from: package-private */
    public Fragment a(ArrayList<Fragment> arrayList, Fragment fragment) {
        ArrayList<Fragment> arrayList2 = arrayList;
        Fragment fragment2 = fragment;
        int i = 0;
        while (i < this.f953a.size()) {
            z.a aVar = this.f953a.get(i);
            int i2 = aVar.f957a;
            if (i2 != 1) {
                if (i2 == 2) {
                    Fragment fragment3 = aVar.f958b;
                    int i3 = fragment3.x;
                    Fragment fragment4 = fragment2;
                    int i4 = i;
                    boolean z = false;
                    for (int size = arrayList.size() - 1; size >= 0; size--) {
                        Fragment fragment5 = arrayList2.get(size);
                        if (fragment5.x == i3) {
                            if (fragment5 == fragment3) {
                                z = true;
                            } else {
                                if (fragment5 == fragment4) {
                                    this.f953a.add(i4, new z.a(9, fragment5));
                                    i4++;
                                    fragment4 = null;
                                }
                                z.a aVar2 = new z.a(3, fragment5);
                                aVar2.f959c = aVar.f959c;
                                aVar2.e = aVar.e;
                                aVar2.f960d = aVar.f960d;
                                aVar2.f = aVar.f;
                                this.f953a.add(i4, aVar2);
                                arrayList2.remove(fragment5);
                                i4++;
                            }
                        }
                    }
                    if (z) {
                        this.f953a.remove(i4);
                        i4--;
                    } else {
                        aVar.f957a = 1;
                        arrayList2.add(fragment3);
                    }
                    i = i4;
                    fragment2 = fragment4;
                } else if (i2 == 3 || i2 == 6) {
                    arrayList2.remove(aVar.f958b);
                    Fragment fragment6 = aVar.f958b;
                    if (fragment6 == fragment2) {
                        this.f953a.add(i, new z.a(9, fragment6));
                        i++;
                        fragment2 = null;
                    }
                } else if (i2 != 7) {
                    if (i2 == 8) {
                        this.f953a.add(i, new z.a(9, fragment2));
                        i++;
                        fragment2 = aVar.f958b;
                    }
                }
                i++;
            }
            arrayList2.add(aVar.f958b);
            i++;
        }
        return fragment2;
    }

    /* access modifiers changed from: package-private */
    public void a() {
        int size = this.f953a.size();
        for (int i = 0; i < size; i++) {
            z.a aVar = this.f953a.get(i);
            Fragment fragment = aVar.f958b;
            if (fragment != null) {
                fragment.a(this.f, this.g);
            }
            switch (aVar.f957a) {
                case 1:
                    fragment.a(aVar.f959c);
                    this.s.a(fragment, false);
                    break;
                case 3:
                    fragment.a(aVar.f960d);
                    this.s.o(fragment);
                    break;
                case 4:
                    fragment.a(aVar.f960d);
                    this.s.h(fragment);
                    break;
                case 5:
                    fragment.a(aVar.f959c);
                    this.s.t(fragment);
                    break;
                case 6:
                    fragment.a(aVar.f960d);
                    this.s.d(fragment);
                    break;
                case 7:
                    fragment.a(aVar.f959c);
                    this.s.b(fragment);
                    break;
                case 8:
                    this.s.s(fragment);
                    break;
                case 9:
                    this.s.s((Fragment) null);
                    break;
                case 10:
                    this.s.a(fragment, aVar.h);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + aVar.f957a);
            }
            if (!(this.q || aVar.f957a == 1 || fragment == null)) {
                this.s.l(fragment);
            }
        }
        if (!this.q) {
            t tVar = this.s;
            tVar.a(tVar.s, true);
        }
    }

    /* access modifiers changed from: package-private */
    public void a(int i) {
        if (this.h) {
            if (t.f937c) {
                Log.v("FragmentManager", "Bump nesting in " + this + " by " + i);
            }
            int size = this.f953a.size();
            for (int i2 = 0; i2 < size; i2++) {
                z.a aVar = this.f953a.get(i2);
                Fragment fragment = aVar.f958b;
                if (fragment != null) {
                    fragment.r += i;
                    if (t.f937c) {
                        Log.v("FragmentManager", "Bump nesting of " + aVar.f958b + " to " + aVar.f958b.r);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(Fragment.c cVar) {
        for (int i = 0; i < this.f953a.size(); i++) {
            z.a aVar = this.f953a.get(i);
            if (b(aVar)) {
                aVar.f958b.a(cVar);
            }
        }
    }

    public void a(String str, PrintWriter printWriter) {
        a(str, printWriter, true);
    }

    public void a(String str, PrintWriter printWriter, boolean z) {
        String str2;
        if (z) {
            printWriter.print(str);
            printWriter.print("mName=");
            printWriter.print(this.j);
            printWriter.print(" mIndex=");
            printWriter.print(this.u);
            printWriter.print(" mCommitted=");
            printWriter.println(this.t);
            if (this.f != 0) {
                printWriter.print(str);
                printWriter.print("mTransition=#");
                printWriter.print(Integer.toHexString(this.f));
                printWriter.print(" mTransitionStyle=#");
                printWriter.println(Integer.toHexString(this.g));
            }
            if (!(this.f954b == 0 && this.f955c == 0)) {
                printWriter.print(str);
                printWriter.print("mEnterAnim=#");
                printWriter.print(Integer.toHexString(this.f954b));
                printWriter.print(" mExitAnim=#");
                printWriter.println(Integer.toHexString(this.f955c));
            }
            if (!(this.f956d == 0 && this.e == 0)) {
                printWriter.print(str);
                printWriter.print("mPopEnterAnim=#");
                printWriter.print(Integer.toHexString(this.f956d));
                printWriter.print(" mPopExitAnim=#");
                printWriter.println(Integer.toHexString(this.e));
            }
            if (!(this.k == 0 && this.l == null)) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbTitleRes=#");
                printWriter.print(Integer.toHexString(this.k));
                printWriter.print(" mBreadCrumbTitleText=");
                printWriter.println(this.l);
            }
            if (!(this.m == 0 && this.n == null)) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbShortTitleRes=#");
                printWriter.print(Integer.toHexString(this.m));
                printWriter.print(" mBreadCrumbShortTitleText=");
                printWriter.println(this.n);
            }
        }
        if (!this.f953a.isEmpty()) {
            printWriter.print(str);
            printWriter.println("Operations:");
            int size = this.f953a.size();
            for (int i = 0; i < size; i++) {
                z.a aVar = this.f953a.get(i);
                switch (aVar.f957a) {
                    case 0:
                        str2 = "NULL";
                        break;
                    case 1:
                        str2 = "ADD";
                        break;
                    case 2:
                        str2 = "REPLACE";
                        break;
                    case 3:
                        str2 = "REMOVE";
                        break;
                    case 4:
                        str2 = "HIDE";
                        break;
                    case 5:
                        str2 = "SHOW";
                        break;
                    case 6:
                        str2 = "DETACH";
                        break;
                    case 7:
                        str2 = "ATTACH";
                        break;
                    case 8:
                        str2 = "SET_PRIMARY_NAV";
                        break;
                    case 9:
                        str2 = "UNSET_PRIMARY_NAV";
                        break;
                    case 10:
                        str2 = "OP_SET_MAX_LIFECYCLE";
                        break;
                    default:
                        str2 = "cmd=" + aVar.f957a;
                        break;
                }
                printWriter.print(str);
                printWriter.print("  Op #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.print(str2);
                printWriter.print(" ");
                printWriter.println(aVar.f958b);
                if (z) {
                    if (!(aVar.f959c == 0 && aVar.f960d == 0)) {
                        printWriter.print(str);
                        printWriter.print("enterAnim=#");
                        printWriter.print(Integer.toHexString(aVar.f959c));
                        printWriter.print(" exitAnim=#");
                        printWriter.println(Integer.toHexString(aVar.f960d));
                    }
                    if (aVar.e != 0 || aVar.f != 0) {
                        printWriter.print(str);
                        printWriter.print("popEnterAnim=#");
                        printWriter.print(Integer.toHexString(aVar.e));
                        printWriter.print(" popExitAnim=#");
                        printWriter.println(Integer.toHexString(aVar.f));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void a(boolean z) {
        for (int size = this.f953a.size() - 1; size >= 0; size--) {
            z.a aVar = this.f953a.get(size);
            Fragment fragment = aVar.f958b;
            if (fragment != null) {
                fragment.a(t.d(this.f), this.g);
            }
            switch (aVar.f957a) {
                case 1:
                    fragment.a(aVar.f);
                    this.s.o(fragment);
                    break;
                case 3:
                    fragment.a(aVar.e);
                    this.s.a(fragment, false);
                    break;
                case 4:
                    fragment.a(aVar.e);
                    this.s.t(fragment);
                    break;
                case 5:
                    fragment.a(aVar.f);
                    this.s.h(fragment);
                    break;
                case 6:
                    fragment.a(aVar.e);
                    this.s.b(fragment);
                    break;
                case 7:
                    fragment.a(aVar.f);
                    this.s.d(fragment);
                    break;
                case 8:
                    this.s.s((Fragment) null);
                    break;
                case 9:
                    this.s.s(fragment);
                    break;
                case 10:
                    this.s.a(fragment, aVar.g);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown cmd: " + aVar.f957a);
            }
            if (!(this.q || aVar.f957a == 3 || fragment == null)) {
                this.s.l(fragment);
            }
        }
        if (!this.q && z) {
            t tVar = this.s;
            tVar.a(tVar.s, true);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a(ArrayList<C0131a> arrayList, int i, int i2) {
        if (i2 == i) {
            return false;
        }
        int size = this.f953a.size();
        int i3 = -1;
        for (int i4 = 0; i4 < size; i4++) {
            Fragment fragment = this.f953a.get(i4).f958b;
            int i5 = fragment != null ? fragment.x : 0;
            if (!(i5 == 0 || i5 == i3)) {
                for (int i6 = i; i6 < i2; i6++) {
                    C0131a aVar = arrayList.get(i6);
                    int size2 = aVar.f953a.size();
                    for (int i7 = 0; i7 < size2; i7++) {
                        Fragment fragment2 = aVar.f953a.get(i7).f958b;
                        if ((fragment2 != null ? fragment2.x : 0) == i5) {
                            return true;
                        }
                    }
                }
                i3 = i5;
            }
        }
        return false;
    }

    public boolean a(ArrayList<C0131a> arrayList, ArrayList<Boolean> arrayList2) {
        if (t.f937c) {
            Log.v("FragmentManager", "Run: " + this);
        }
        arrayList.add(this);
        arrayList2.add(false);
        if (!this.h) {
            return true;
        }
        this.s.a(this);
        return true;
    }

    /* access modifiers changed from: package-private */
    public Fragment b(ArrayList<Fragment> arrayList, Fragment fragment) {
        for (int size = this.f953a.size() - 1; size >= 0; size--) {
            z.a aVar = this.f953a.get(size);
            int i = aVar.f957a;
            if (i != 1) {
                if (i != 3) {
                    switch (i) {
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            fragment = null;
                            break;
                        case 9:
                            fragment = aVar.f958b;
                            break;
                        case 10:
                            aVar.h = aVar.g;
                            break;
                    }
                }
                arrayList.add(aVar.f958b);
            }
            arrayList.remove(aVar.f958b);
        }
        return fragment;
    }

    @Nullable
    public String b() {
        return this.j;
    }

    /* access modifiers changed from: package-private */
    public boolean b(int i) {
        int size = this.f953a.size();
        for (int i2 = 0; i2 < size; i2++) {
            Fragment fragment = this.f953a.get(i2).f958b;
            int i3 = fragment != null ? fragment.x : 0;
            if (i3 != 0 && i3 == i) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean c() {
        for (int i = 0; i < this.f953a.size(); i++) {
            if (b(this.f953a.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void d() {
        if (this.r != null) {
            for (int i = 0; i < this.r.size(); i++) {
                this.r.get(i).run();
            }
            this.r = null;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("BackStackEntry{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.u >= 0) {
            sb.append(" #");
            sb.append(this.u);
        }
        if (this.j != null) {
            sb.append(" ");
            sb.append(this.j);
        }
        sb.append("}");
        return sb.toString();
    }
}
