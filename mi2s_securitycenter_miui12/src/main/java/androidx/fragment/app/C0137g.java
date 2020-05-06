package androidx.fragment.app;

import a.c.j;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.c;
import androidx.activity.e;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.app.a;
import androidx.lifecycle.f;
import androidx.lifecycle.k;
import androidx.lifecycle.u;
import androidx.lifecycle.v;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* renamed from: androidx.fragment.app.g  reason: case insensitive filesystem */
public class C0137g extends c implements a.C0011a, a.c {
    final C0139i h = C0139i.a((C0141k<?>) new a());
    final k i = new k(this);
    boolean j;
    boolean k;
    boolean l = true;
    boolean m;
    boolean n;
    int o;
    j<String> p;

    /* renamed from: androidx.fragment.app.g$a */
    class a extends C0141k<C0137g> implements v, e {
        public a() {
            super(C0137g.this);
        }

        @Nullable
        public View a(int i) {
            return C0137g.this.findViewById(i);
        }

        @NonNull
        public f a() {
            return C0137g.this.i;
        }

        public void a(@NonNull Fragment fragment) {
            C0137g.this.a(fragment);
        }

        public void a(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
            C0137g.this.dump(str, fileDescriptor, printWriter, strArr);
        }

        @NonNull
        public OnBackPressedDispatcher b() {
            return C0137g.this.b();
        }

        public boolean b(@NonNull Fragment fragment) {
            return !C0137g.this.isFinishing();
        }

        public boolean c() {
            Window window = C0137g.this.getWindow();
            return (window == null || window.peekDecorView() == null) ? false : true;
        }

        @NonNull
        public u d() {
            return C0137g.this.d();
        }

        public C0137g h() {
            return C0137g.this;
        }

        @NonNull
        public LayoutInflater i() {
            return C0137g.this.getLayoutInflater().cloneInContext(C0137g.this);
        }

        public int j() {
            Window window = C0137g.this.getWindow();
            if (window == null) {
                return 0;
            }
            return window.getAttributes().windowAnimations;
        }

        public boolean k() {
            return C0137g.this.getWindow() != null;
        }

        public void l() {
            C0137g.this.h();
        }
    }

    static void a(int i2) {
        if ((i2 & -65536) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        }
    }

    private static boolean a(C0142l lVar, f.b bVar) {
        boolean z = false;
        for (Fragment next : lVar.b()) {
            if (next != null) {
                if (next.a().a().a(f.b.STARTED)) {
                    next.T.b(bVar);
                    z = true;
                }
                if (next.r() != null) {
                    z |= a(next.k(), bVar);
                }
            }
        }
        return z;
    }

    private void i() {
        do {
        } while (a(f(), f.b.CREATED));
    }

    /* access modifiers changed from: package-private */
    @Nullable
    public final View a(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        return this.h.a(view, str, context, attributeSet);
    }

    public void a(@NonNull Fragment fragment) {
    }

    /* access modifiers changed from: protected */
    @Deprecated
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public boolean a(@Nullable View view, @NonNull Menu menu) {
        return super.onPreparePanel(0, view, menu);
    }

    public void dump(@NonNull String str, @Nullable FileDescriptor fileDescriptor, @NonNull PrintWriter printWriter, @Nullable String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        printWriter.print(str);
        printWriter.print("Local FragmentActivity ");
        printWriter.print(Integer.toHexString(System.identityHashCode(this)));
        printWriter.println(" State:");
        String str2 = str + "  ";
        printWriter.print(str2);
        printWriter.print("mCreated=");
        printWriter.print(this.j);
        printWriter.print(" mResumed=");
        printWriter.print(this.k);
        printWriter.print(" mStopped=");
        printWriter.print(this.l);
        if (getApplication() != null) {
            a.h.a.a.a(this).a(str2, fileDescriptor, printWriter, strArr);
        }
        this.h.j().a(str, fileDescriptor, printWriter, strArr);
    }

    @NonNull
    public C0142l f() {
        return this.h.j();
    }

    /* access modifiers changed from: protected */
    public void g() {
        this.i.b(f.a.ON_RESUME);
        this.h.f();
    }

    @Deprecated
    public void h() {
        invalidateOptionsMenu();
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onActivityResult(int i2, int i3, @Nullable Intent intent) {
        this.h.k();
        int i4 = i2 >> 16;
        if (i4 != 0) {
            int i5 = i4 - 1;
            String a2 = this.p.a(i5);
            this.p.c(i5);
            if (a2 == null) {
                Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
                return;
            }
            Fragment a3 = this.h.a(a2);
            if (a3 == null) {
                Log.w("FragmentActivity", "Activity result no fragment exists for who: " + a2);
                return;
            }
            a3.a(i2 & 65535, i3, intent);
            return;
        }
        a.b a4 = androidx.core.app.a.a();
        if (a4 == null || !a4.a(this, i2, i3, intent)) {
            super.onActivityResult(i2, i3, intent);
        }
    }

    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.h.k();
        this.h.a(configuration);
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        this.h.a((Fragment) null);
        if (bundle != null) {
            this.h.a(bundle.getParcelable("android:support:fragments"));
            if (bundle.containsKey("android:support:next_request_index")) {
                this.o = bundle.getInt("android:support:next_request_index");
                int[] intArray = bundle.getIntArray("android:support:request_indicies");
                String[] stringArray = bundle.getStringArray("android:support:request_fragment_who");
                if (intArray == null || stringArray == null || intArray.length != stringArray.length) {
                    Log.w("FragmentActivity", "Invalid requestCode mapping in savedInstanceState.");
                } else {
                    this.p = new j<>(intArray.length);
                    for (int i2 = 0; i2 < intArray.length; i2++) {
                        this.p.c(intArray[i2], stringArray[i2]);
                    }
                }
            }
        }
        if (this.p == null) {
            this.p = new j<>();
            this.o = 0;
        }
        super.onCreate(bundle);
        this.i.b(f.a.ON_CREATE);
        this.h.b();
    }

    public boolean onCreatePanelMenu(int i2, @NonNull Menu menu) {
        return i2 == 0 ? super.onCreatePanelMenu(i2, menu) | this.h.a(menu, getMenuInflater()) : super.onCreatePanelMenu(i2, menu);
    }

    @Nullable
    public View onCreateView(@Nullable View view, @NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        View a2 = a(view, str, context, attributeSet);
        return a2 == null ? super.onCreateView(view, str, context, attributeSet) : a2;
    }

    @Nullable
    public View onCreateView(@NonNull String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        View a2 = a((View) null, str, context, attributeSet);
        return a2 == null ? super.onCreateView(str, context, attributeSet) : a2;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        this.h.c();
        this.i.b(f.a.ON_DESTROY);
    }

    public void onLowMemory() {
        super.onLowMemory();
        this.h.d();
    }

    public boolean onMenuItemSelected(int i2, @NonNull MenuItem menuItem) {
        if (super.onMenuItemSelected(i2, menuItem)) {
            return true;
        }
        if (i2 == 0) {
            return this.h.b(menuItem);
        }
        if (i2 != 6) {
            return false;
        }
        return this.h.a(menuItem);
    }

    @CallSuper
    public void onMultiWindowModeChanged(boolean z) {
        this.h.a(z);
    }

    /* access modifiers changed from: protected */
    @CallSuper
    public void onNewIntent(@SuppressLint({"UnknownNullness"}) Intent intent) {
        super.onNewIntent(intent);
        this.h.k();
    }

    public void onPanelClosed(int i2, @NonNull Menu menu) {
        if (i2 == 0) {
            this.h.a(menu);
        }
        super.onPanelClosed(i2, menu);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.k = false;
        this.h.e();
        this.i.b(f.a.ON_PAUSE);
    }

    @CallSuper
    public void onPictureInPictureModeChanged(boolean z) {
        this.h.b(z);
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        super.onPostResume();
        g();
    }

    public boolean onPreparePanel(int i2, @Nullable View view, @NonNull Menu menu) {
        return i2 == 0 ? a(view, menu) | this.h.b(menu) : super.onPreparePanel(i2, view, menu);
    }

    public void onRequestPermissionsResult(int i2, @NonNull String[] strArr, @NonNull int[] iArr) {
        this.h.k();
        int i3 = (i2 >> 16) & 65535;
        if (i3 != 0) {
            int i4 = i3 - 1;
            String a2 = this.p.a(i4);
            this.p.c(i4);
            if (a2 == null) {
                Log.w("FragmentActivity", "Activity result delivered for unknown Fragment.");
                return;
            }
            Fragment a3 = this.h.a(a2);
            if (a3 == null) {
                Log.w("FragmentActivity", "Activity result no fragment exists for who: " + a2);
                return;
            }
            a3.a(i2 & 65535, strArr, iArr);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        this.k = true;
        this.h.k();
        this.h.i();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        i();
        this.i.b(f.a.ON_STOP);
        Parcelable l2 = this.h.l();
        if (l2 != null) {
            bundle.putParcelable("android:support:fragments", l2);
        }
        if (this.p.a() > 0) {
            bundle.putInt("android:support:next_request_index", this.o);
            int[] iArr = new int[this.p.a()];
            String[] strArr = new String[this.p.a()];
            for (int i2 = 0; i2 < this.p.a(); i2++) {
                iArr[i2] = this.p.b(i2);
                strArr[i2] = this.p.d(i2);
            }
            bundle.putIntArray("android:support:request_indicies", iArr);
            bundle.putStringArray("android:support:request_fragment_who", strArr);
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.l = false;
        if (!this.j) {
            this.j = true;
            this.h.a();
        }
        this.h.k();
        this.h.i();
        this.i.b(f.a.ON_START);
        this.h.g();
    }

    public void onStateNotSaved() {
        this.h.k();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.l = true;
        i();
        this.h.h();
        this.i.b(f.a.ON_STOP);
    }

    public void startActivityForResult(@SuppressLint({"UnknownNullness"}) Intent intent, int i2) {
        if (!this.n && i2 != -1) {
            a(i2);
        }
        super.startActivityForResult(intent, i2);
    }

    public void startActivityForResult(@SuppressLint({"UnknownNullness"}) Intent intent, int i2, @Nullable Bundle bundle) {
        if (!this.n && i2 != -1) {
            a(i2);
        }
        super.startActivityForResult(intent, i2, bundle);
    }

    public void startIntentSenderForResult(@SuppressLint({"UnknownNullness"}) IntentSender intentSender, int i2, @Nullable Intent intent, int i3, int i4, int i5) {
        if (!this.m && i2 != -1) {
            a(i2);
        }
        super.startIntentSenderForResult(intentSender, i2, intent, i3, i4, i5);
    }

    public void startIntentSenderForResult(@SuppressLint({"UnknownNullness"}) IntentSender intentSender, int i2, @Nullable Intent intent, int i3, int i4, int i5, @Nullable Bundle bundle) {
        if (!this.m && i2 != -1) {
            a(i2);
        }
        super.startIntentSenderForResult(intentSender, i2, intent, i3, i4, i5, bundle);
    }
}
