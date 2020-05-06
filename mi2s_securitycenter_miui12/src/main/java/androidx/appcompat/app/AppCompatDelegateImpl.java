package androidx.appcompat.app;

import a.a.d.b;
import a.a.d.f;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.view.menu.j;
import androidx.appcompat.view.menu.s;
import androidx.appcompat.view.menu.t;
import androidx.appcompat.widget.ActionBarContextView;
import androidx.appcompat.widget.C0112o;
import androidx.appcompat.widget.ContentFrameLayout;
import androidx.appcompat.widget.Ia;
import androidx.appcompat.widget.Ja;
import androidx.appcompat.widget.L;
import androidx.appcompat.widget.P;
import androidx.appcompat.widget.va;
import androidx.core.view.C0126d;
import androidx.core.view.C0127e;
import androidx.core.view.D;
import androidx.core.view.E;
import androidx.core.view.ViewCompat;
import androidx.core.view.q;
import androidx.lifecycle.f;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;

@RestrictTo({RestrictTo.a.LIBRARY})
class AppCompatDelegateImpl extends AppCompatDelegate implements j.a, LayoutInflater.Factory2 {

    /* renamed from: d  reason: collision with root package name */
    private static final a.c.i<Class<?>, Integer> f245d = new a.c.i<>();
    private static final boolean e = (Build.VERSION.SDK_INT < 21);
    private static final int[] f = {16842836};
    private static boolean g = true;
    private static final boolean h;
    private ViewGroup A;
    private TextView B;
    private View C;
    private boolean D;
    private boolean E;
    boolean F;
    boolean G;
    boolean H;
    boolean I;
    boolean J;
    private boolean K;
    private PanelFeatureState[] L;
    private PanelFeatureState M;
    private boolean N;
    private boolean O;
    private boolean P;
    private boolean Q;
    boolean R;
    private int S;
    private int T;
    private boolean U;
    private boolean V;
    private e W;
    private e X;
    boolean Y;
    int Z;
    private final Runnable aa;
    private boolean ba;
    private Rect ca;
    private Rect da;
    private AppCompatViewInflater ea;
    final Object i;
    final Context j;
    Window k;
    private c l;
    final m m;
    ActionBar n;
    MenuInflater o;
    private CharSequence p;
    private L q;
    private a r;
    private k s;
    a.a.d.b t;
    ActionBarContextView u;
    PopupWindow v;
    Runnable w;
    D x;
    private boolean y;
    private boolean z;

    protected static final class PanelFeatureState {

        /* renamed from: a  reason: collision with root package name */
        int f246a;

        /* renamed from: b  reason: collision with root package name */
        int f247b;

        /* renamed from: c  reason: collision with root package name */
        int f248c;

        /* renamed from: d  reason: collision with root package name */
        int f249d;
        int e;
        int f;
        ViewGroup g;
        View h;
        View i;
        androidx.appcompat.view.menu.j j;
        androidx.appcompat.view.menu.h k;
        Context l;
        boolean m;
        boolean n;
        boolean o;
        public boolean p;
        boolean q = false;
        boolean r;
        Bundle s;

        @SuppressLint({"BanParcelableUsage"})
        private static class SavedState implements Parcelable {
            public static final Parcelable.Creator<SavedState> CREATOR = new x();
            int featureId;
            boolean isOpen;
            Bundle menuState;

            SavedState() {
            }

            static SavedState readFromParcel(Parcel parcel, ClassLoader classLoader) {
                SavedState savedState = new SavedState();
                savedState.featureId = parcel.readInt();
                boolean z = true;
                if (parcel.readInt() != 1) {
                    z = false;
                }
                savedState.isOpen = z;
                if (savedState.isOpen) {
                    savedState.menuState = parcel.readBundle(classLoader);
                }
                return savedState;
            }

            public int describeContents() {
                return 0;
            }

            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.featureId);
                parcel.writeInt(this.isOpen ? 1 : 0);
                if (this.isOpen) {
                    parcel.writeBundle(this.menuState);
                }
            }
        }

        PanelFeatureState(int i2) {
            this.f246a = i2;
        }

        /* access modifiers changed from: package-private */
        public t a(s.a aVar) {
            if (this.j == null) {
                return null;
            }
            if (this.k == null) {
                this.k = new androidx.appcompat.view.menu.h(this.l, a.a.g.abc_list_menu_item_layout);
                this.k.a(aVar);
                this.j.a((s) this.k);
            }
            return this.k.a(this.g);
        }

        /* access modifiers changed from: package-private */
        public void a(Context context) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme newTheme = context.getResources().newTheme();
            newTheme.setTo(context.getTheme());
            newTheme.resolveAttribute(a.a.a.actionBarPopupTheme, typedValue, true);
            int i2 = typedValue.resourceId;
            if (i2 != 0) {
                newTheme.applyStyle(i2, true);
            }
            newTheme.resolveAttribute(a.a.a.panelMenuListTheme, typedValue, true);
            int i3 = typedValue.resourceId;
            if (i3 == 0) {
                i3 = a.a.i.Theme_AppCompat_CompactMenu;
            }
            newTheme.applyStyle(i3, true);
            a.a.d.d dVar = new a.a.d.d(context, 0);
            dVar.getTheme().setTo(newTheme);
            this.l = dVar;
            TypedArray obtainStyledAttributes = dVar.obtainStyledAttributes(a.a.j.AppCompatTheme);
            this.f247b = obtainStyledAttributes.getResourceId(a.a.j.AppCompatTheme_panelBackground, 0);
            this.f = obtainStyledAttributes.getResourceId(a.a.j.AppCompatTheme_android_windowAnimationStyle, 0);
            obtainStyledAttributes.recycle();
        }

        /* access modifiers changed from: package-private */
        public void a(androidx.appcompat.view.menu.j jVar) {
            androidx.appcompat.view.menu.h hVar;
            androidx.appcompat.view.menu.j jVar2 = this.j;
            if (jVar != jVar2) {
                if (jVar2 != null) {
                    jVar2.b((s) this.k);
                }
                this.j = jVar;
                if (jVar != null && (hVar = this.k) != null) {
                    jVar.a((s) hVar);
                }
            }
        }

        public boolean a() {
            if (this.h == null) {
                return false;
            }
            if (this.i != null) {
                return true;
            }
            return this.k.b().getCount() > 0;
        }
    }

    private final class a implements s.a {
        a() {
        }

        public void a(androidx.appcompat.view.menu.j jVar, boolean z) {
            AppCompatDelegateImpl.this.b(jVar);
        }

        public boolean a(androidx.appcompat.view.menu.j jVar) {
            Window.Callback q = AppCompatDelegateImpl.this.q();
            if (q == null) {
                return true;
            }
            q.onMenuOpened(108, jVar);
            return true;
        }
    }

    class b implements b.a {

        /* renamed from: a  reason: collision with root package name */
        private b.a f251a;

        public b(b.a aVar) {
            this.f251a = aVar;
        }

        public void a(a.a.d.b bVar) {
            this.f251a.a(bVar);
            AppCompatDelegateImpl appCompatDelegateImpl = AppCompatDelegateImpl.this;
            if (appCompatDelegateImpl.v != null) {
                appCompatDelegateImpl.k.getDecorView().removeCallbacks(AppCompatDelegateImpl.this.w);
            }
            AppCompatDelegateImpl appCompatDelegateImpl2 = AppCompatDelegateImpl.this;
            if (appCompatDelegateImpl2.u != null) {
                appCompatDelegateImpl2.m();
                AppCompatDelegateImpl appCompatDelegateImpl3 = AppCompatDelegateImpl.this;
                D a2 = ViewCompat.a(appCompatDelegateImpl3.u);
                a2.a(0.0f);
                appCompatDelegateImpl3.x = a2;
                AppCompatDelegateImpl.this.x.a((E) new v(this));
            }
            AppCompatDelegateImpl appCompatDelegateImpl4 = AppCompatDelegateImpl.this;
            m mVar = appCompatDelegateImpl4.m;
            if (mVar != null) {
                mVar.b(appCompatDelegateImpl4.t);
            }
            AppCompatDelegateImpl.this.t = null;
        }

        public boolean a(a.a.d.b bVar, Menu menu) {
            return this.f251a.a(bVar, menu);
        }

        public boolean a(a.a.d.b bVar, MenuItem menuItem) {
            return this.f251a.a(bVar, menuItem);
        }

        public boolean b(a.a.d.b bVar, Menu menu) {
            return this.f251a.b(bVar, menu);
        }
    }

    class c extends a.a.d.j {
        c(Window.Callback callback) {
            super(callback);
        }

        /* access modifiers changed from: package-private */
        public final ActionMode a(ActionMode.Callback callback) {
            f.a aVar = new f.a(AppCompatDelegateImpl.this.j, callback);
            a.a.d.b a2 = AppCompatDelegateImpl.this.a((b.a) aVar);
            if (a2 != null) {
                return aVar.b(a2);
            }
            return null;
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            return AppCompatDelegateImpl.this.a(keyEvent) || super.dispatchKeyEvent(keyEvent);
        }

        public boolean dispatchKeyShortcutEvent(KeyEvent keyEvent) {
            return super.dispatchKeyShortcutEvent(keyEvent) || AppCompatDelegateImpl.this.b(keyEvent.getKeyCode(), keyEvent);
        }

        public void onContentChanged() {
        }

        public boolean onCreatePanelMenu(int i, Menu menu) {
            if (i != 0 || (menu instanceof androidx.appcompat.view.menu.j)) {
                return super.onCreatePanelMenu(i, menu);
            }
            return false;
        }

        public boolean onMenuOpened(int i, Menu menu) {
            super.onMenuOpened(i, menu);
            AppCompatDelegateImpl.this.h(i);
            return true;
        }

        public void onPanelClosed(int i, Menu menu) {
            super.onPanelClosed(i, menu);
            AppCompatDelegateImpl.this.i(i);
        }

        public boolean onPreparePanel(int i, View view, Menu menu) {
            androidx.appcompat.view.menu.j jVar = menu instanceof androidx.appcompat.view.menu.j ? (androidx.appcompat.view.menu.j) menu : null;
            if (i == 0 && jVar == null) {
                return false;
            }
            if (jVar != null) {
                jVar.c(true);
            }
            boolean onPreparePanel = super.onPreparePanel(i, view, menu);
            if (jVar != null) {
                jVar.c(false);
            }
            return onPreparePanel;
        }

        @RequiresApi(24)
        public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> list, Menu menu, int i) {
            androidx.appcompat.view.menu.j jVar;
            PanelFeatureState a2 = AppCompatDelegateImpl.this.a(0, true);
            if (a2 == null || (jVar = a2.j) == null) {
                super.onProvideKeyboardShortcuts(list, menu, i);
            } else {
                super.onProvideKeyboardShortcuts(list, jVar, i);
            }
        }

        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
            if (Build.VERSION.SDK_INT >= 23) {
                return null;
            }
            return AppCompatDelegateImpl.this.r() ? a(callback) : super.onWindowStartingActionMode(callback);
        }

        @RequiresApi(23)
        public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int i) {
            return (!AppCompatDelegateImpl.this.r() || i != 0) ? super.onWindowStartingActionMode(callback, i) : a(callback);
        }
    }

    private class d extends e {

        /* renamed from: c  reason: collision with root package name */
        private final PowerManager f254c;

        d(@NonNull Context context) {
            super();
            this.f254c = (PowerManager) context.getSystemService("power");
        }

        /* access modifiers changed from: package-private */
        public IntentFilter b() {
            if (Build.VERSION.SDK_INT < 21) {
                return null;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            return intentFilter;
        }

        public int c() {
            return (Build.VERSION.SDK_INT < 21 || !this.f254c.isPowerSaveMode()) ? 1 : 2;
        }

        public void d() {
            AppCompatDelegateImpl.this.k();
        }
    }

    @RestrictTo({RestrictTo.a.LIBRARY})
    @VisibleForTesting
    abstract class e {

        /* renamed from: a  reason: collision with root package name */
        private BroadcastReceiver f256a;

        e() {
        }

        /* access modifiers changed from: package-private */
        public void a() {
            BroadcastReceiver broadcastReceiver = this.f256a;
            if (broadcastReceiver != null) {
                try {
                    AppCompatDelegateImpl.this.j.unregisterReceiver(broadcastReceiver);
                } catch (IllegalArgumentException unused) {
                }
                this.f256a = null;
            }
        }

        /* access modifiers changed from: package-private */
        @Nullable
        public abstract IntentFilter b();

        /* access modifiers changed from: package-private */
        public abstract int c();

        /* access modifiers changed from: package-private */
        public abstract void d();

        /* access modifiers changed from: package-private */
        public void e() {
            a();
            IntentFilter b2 = b();
            if (b2 != null && b2.countActions() != 0) {
                if (this.f256a == null) {
                    this.f256a = new w(this);
                }
                AppCompatDelegateImpl.this.j.registerReceiver(this.f256a, b2);
            }
        }
    }

    private class f extends e {

        /* renamed from: c  reason: collision with root package name */
        private final C f258c;

        f(@NonNull C c2) {
            super();
            this.f258c = c2;
        }

        /* access modifiers changed from: package-private */
        public IntentFilter b() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.TIME_SET");
            intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
            intentFilter.addAction("android.intent.action.TIME_TICK");
            return intentFilter;
        }

        public int c() {
            return this.f258c.a() ? 2 : 1;
        }

        public void d() {
            AppCompatDelegateImpl.this.k();
        }
    }

    @RequiresApi(17)
    static class g {
        static void a(@NonNull Configuration configuration, @NonNull Configuration configuration2, @NonNull Configuration configuration3) {
            int i = configuration.densityDpi;
            int i2 = configuration2.densityDpi;
            if (i != i2) {
                configuration3.densityDpi = i2;
            }
        }
    }

    @RequiresApi(24)
    static class h {
        static void a(@NonNull Configuration configuration, @NonNull Configuration configuration2, @NonNull Configuration configuration3) {
            LocaleList locales = configuration.getLocales();
            LocaleList locales2 = configuration2.getLocales();
            if (!locales.equals(locales2)) {
                configuration3.setLocales(locales2);
                configuration3.locale = configuration2.locale;
            }
        }
    }

    @RequiresApi(26)
    static class i {
        static void a(@NonNull Configuration configuration, @NonNull Configuration configuration2, @NonNull Configuration configuration3) {
            int i = configuration.colorMode & 3;
            int i2 = configuration2.colorMode;
            if (i != (i2 & 3)) {
                configuration3.colorMode |= i2 & 3;
            }
            int i3 = configuration.colorMode & 12;
            int i4 = configuration2.colorMode;
            if (i3 != (i4 & 12)) {
                configuration3.colorMode |= i4 & 12;
            }
        }
    }

    private class j extends ContentFrameLayout {
        public j(Context context) {
            super(context);
        }

        private boolean a(int i2, int i3) {
            return i2 < -5 || i3 < -5 || i2 > getWidth() + 5 || i3 > getHeight() + 5;
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            return AppCompatDelegateImpl.this.a(keyEvent) || super.dispatchKeyEvent(keyEvent);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || !a((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            AppCompatDelegateImpl.this.e(0);
            return true;
        }

        public void setBackgroundResource(int i2) {
            setBackgroundDrawable(a.a.a.a.a.b(getContext(), i2));
        }
    }

    private final class k implements s.a {
        k() {
        }

        public void a(androidx.appcompat.view.menu.j jVar, boolean z) {
            androidx.appcompat.view.menu.j m = jVar.m();
            boolean z2 = m != jVar;
            AppCompatDelegateImpl appCompatDelegateImpl = AppCompatDelegateImpl.this;
            if (z2) {
                jVar = m;
            }
            PanelFeatureState a2 = appCompatDelegateImpl.a((Menu) jVar);
            if (a2 == null) {
                return;
            }
            if (z2) {
                AppCompatDelegateImpl.this.a(a2.f246a, a2, (Menu) m);
                AppCompatDelegateImpl.this.a(a2, true);
                return;
            }
            AppCompatDelegateImpl.this.a(a2, z);
        }

        public boolean a(androidx.appcompat.view.menu.j jVar) {
            Window.Callback q;
            if (jVar != null) {
                return true;
            }
            AppCompatDelegateImpl appCompatDelegateImpl = AppCompatDelegateImpl.this;
            if (!appCompatDelegateImpl.F || (q = appCompatDelegateImpl.q()) == null || AppCompatDelegateImpl.this.R) {
                return true;
            }
            q.onMenuOpened(108, jVar);
            return true;
        }
    }

    static {
        boolean z2 = false;
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21 && i2 <= 25) {
            z2 = true;
        }
        h = z2;
        if (e && !g) {
            Thread.setDefaultUncaughtExceptionHandler(new n(Thread.getDefaultUncaughtExceptionHandler()));
        }
    }

    AppCompatDelegateImpl(Activity activity, m mVar) {
        this(activity, (Window) null, mVar, activity);
    }

    AppCompatDelegateImpl(Dialog dialog, m mVar) {
        this(dialog.getContext(), dialog.getWindow(), mVar, dialog);
    }

    private AppCompatDelegateImpl(Context context, Window window, m mVar, Object obj) {
        Integer num;
        l F2;
        this.x = null;
        this.y = true;
        this.S = -100;
        this.aa = new o(this);
        this.j = context;
        this.m = mVar;
        this.i = obj;
        if (this.S == -100 && (this.i instanceof Dialog) && (F2 = F()) != null) {
            this.S = F2.i().b();
        }
        if (this.S == -100 && (num = f245d.get(this.i.getClass())) != null) {
            this.S = num.intValue();
            f245d.remove(this.i.getClass());
        }
        if (window != null) {
            a(window);
        }
        C0112o.c();
    }

    private void A() {
        if (this.k == null) {
            Object obj = this.i;
            if (obj instanceof Activity) {
                a(((Activity) obj).getWindow());
            }
        }
        if (this.k == null) {
            throw new IllegalStateException("We have not been given a Window");
        }
    }

    private e B() {
        if (this.X == null) {
            this.X = new d(this.j);
        }
        return this.X;
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void C() {
        /*
            r3 = this;
            r3.z()
            boolean r0 = r3.F
            if (r0 == 0) goto L_0x0033
            androidx.appcompat.app.ActionBar r0 = r3.n
            if (r0 == 0) goto L_0x000c
            goto L_0x0033
        L_0x000c:
            java.lang.Object r0 = r3.i
            boolean r1 = r0 instanceof android.app.Activity
            if (r1 == 0) goto L_0x001e
            androidx.appcompat.app.G r1 = new androidx.appcompat.app.G
            android.app.Activity r0 = (android.app.Activity) r0
            boolean r2 = r3.G
            r1.<init>(r0, r2)
        L_0x001b:
            r3.n = r1
            goto L_0x002a
        L_0x001e:
            boolean r1 = r0 instanceof android.app.Dialog
            if (r1 == 0) goto L_0x002a
            androidx.appcompat.app.G r1 = new androidx.appcompat.app.G
            android.app.Dialog r0 = (android.app.Dialog) r0
            r1.<init>(r0)
            goto L_0x001b
        L_0x002a:
            androidx.appcompat.app.ActionBar r0 = r3.n
            if (r0 == 0) goto L_0x0033
            boolean r1 = r3.ba
            r0.c(r1)
        L_0x0033:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.app.AppCompatDelegateImpl.C():void");
    }

    private boolean D() {
        if (!this.V && (this.i instanceof Activity)) {
            PackageManager packageManager = this.j.getPackageManager();
            if (packageManager == null) {
                return false;
            }
            try {
                ActivityInfo activityInfo = packageManager.getActivityInfo(new ComponentName(this.j, this.i.getClass()), Build.VERSION.SDK_INT >= 29 ? 269221888 : Build.VERSION.SDK_INT >= 24 ? 786432 : 0);
                this.U = (activityInfo == null || (activityInfo.configChanges & 512) == 0) ? false : true;
            } catch (PackageManager.NameNotFoundException e2) {
                Log.d("AppCompatDelegate", "Exception while getting ActivityInfo", e2);
                this.U = false;
            }
        }
        this.V = true;
        return this.U;
    }

    private void E() {
        if (this.z) {
            throw new AndroidRuntimeException("Window feature must be requested before adding content");
        }
    }

    @Nullable
    private l F() {
        Context context = this.j;
        while (context != null) {
            if (!(context instanceof l)) {
                if (!(context instanceof ContextWrapper)) {
                    break;
                }
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                return (l) context;
            }
        }
        return null;
    }

    @NonNull
    private static Configuration a(@NonNull Configuration configuration, @Nullable Configuration configuration2) {
        Configuration configuration3 = new Configuration();
        configuration3.fontScale = 0.0f;
        if (!(configuration2 == null || configuration.diff(configuration2) == 0)) {
            float f2 = configuration.fontScale;
            float f3 = configuration2.fontScale;
            if (f2 != f3) {
                configuration3.fontScale = f3;
            }
            int i2 = configuration.mcc;
            int i3 = configuration2.mcc;
            if (i2 != i3) {
                configuration3.mcc = i3;
            }
            int i4 = configuration.mnc;
            int i5 = configuration2.mnc;
            if (i4 != i5) {
                configuration3.mnc = i5;
            }
            if (Build.VERSION.SDK_INT >= 24) {
                h.a(configuration, configuration2, configuration3);
            } else if (!a.d.e.c.a(configuration.locale, configuration2.locale)) {
                configuration3.locale = configuration2.locale;
            }
            int i6 = configuration.touchscreen;
            int i7 = configuration2.touchscreen;
            if (i6 != i7) {
                configuration3.touchscreen = i7;
            }
            int i8 = configuration.keyboard;
            int i9 = configuration2.keyboard;
            if (i8 != i9) {
                configuration3.keyboard = i9;
            }
            int i10 = configuration.keyboardHidden;
            int i11 = configuration2.keyboardHidden;
            if (i10 != i11) {
                configuration3.keyboardHidden = i11;
            }
            int i12 = configuration.navigation;
            int i13 = configuration2.navigation;
            if (i12 != i13) {
                configuration3.navigation = i13;
            }
            int i14 = configuration.navigationHidden;
            int i15 = configuration2.navigationHidden;
            if (i14 != i15) {
                configuration3.navigationHidden = i15;
            }
            int i16 = configuration.orientation;
            int i17 = configuration2.orientation;
            if (i16 != i17) {
                configuration3.orientation = i17;
            }
            int i18 = configuration.screenLayout & 15;
            int i19 = configuration2.screenLayout;
            if (i18 != (i19 & 15)) {
                configuration3.screenLayout |= i19 & 15;
            }
            int i20 = configuration.screenLayout & PsExtractor.AUDIO_STREAM;
            int i21 = configuration2.screenLayout;
            if (i20 != (i21 & PsExtractor.AUDIO_STREAM)) {
                configuration3.screenLayout |= i21 & PsExtractor.AUDIO_STREAM;
            }
            int i22 = configuration.screenLayout & 48;
            int i23 = configuration2.screenLayout;
            if (i22 != (i23 & 48)) {
                configuration3.screenLayout |= i23 & 48;
            }
            int i24 = configuration.screenLayout & 768;
            int i25 = configuration2.screenLayout;
            if (i24 != (i25 & 768)) {
                configuration3.screenLayout |= i25 & 768;
            }
            if (Build.VERSION.SDK_INT >= 26) {
                i.a(configuration, configuration2, configuration3);
            }
            int i26 = configuration.uiMode & 15;
            int i27 = configuration2.uiMode;
            if (i26 != (i27 & 15)) {
                configuration3.uiMode |= i27 & 15;
            }
            int i28 = configuration.uiMode & 48;
            int i29 = configuration2.uiMode;
            if (i28 != (i29 & 48)) {
                configuration3.uiMode |= i29 & 48;
            }
            int i30 = configuration.screenWidthDp;
            int i31 = configuration2.screenWidthDp;
            if (i30 != i31) {
                configuration3.screenWidthDp = i31;
            }
            int i32 = configuration.screenHeightDp;
            int i33 = configuration2.screenHeightDp;
            if (i32 != i33) {
                configuration3.screenHeightDp = i33;
            }
            int i34 = configuration.smallestScreenWidthDp;
            int i35 = configuration2.smallestScreenWidthDp;
            if (i34 != i35) {
                configuration3.smallestScreenWidthDp = i35;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                g.a(configuration, configuration2, configuration3);
            }
        }
        return configuration3;
    }

    private void a(@NonNull Window window) {
        if (this.k == null) {
            Window.Callback callback = window.getCallback();
            if (!(callback instanceof c)) {
                this.l = new c(callback);
                window.setCallback(this.l);
                va a2 = va.a(this.j, (AttributeSet) null, f);
                Drawable c2 = a2.c(0);
                if (c2 != null) {
                    window.setBackgroundDrawable(c2);
                }
                a2.b();
                this.k = window;
                return;
            }
            throw new IllegalStateException("AppCompat has already installed itself into the Window");
        }
        throw new IllegalStateException("AppCompat has already installed itself into the Window");
    }

    private void a(PanelFeatureState panelFeatureState, KeyEvent keyEvent) {
        int i2;
        ViewGroup.LayoutParams layoutParams;
        if (!panelFeatureState.o && !this.R) {
            if (panelFeatureState.f246a == 0) {
                if ((this.j.getResources().getConfiguration().screenLayout & 15) == 4) {
                    return;
                }
            }
            Window.Callback q2 = q();
            if (q2 == null || q2.onMenuOpened(panelFeatureState.f246a, panelFeatureState.j)) {
                WindowManager windowManager = (WindowManager) this.j.getSystemService("window");
                if (windowManager != null && b(panelFeatureState, keyEvent)) {
                    if (panelFeatureState.g == null || panelFeatureState.q) {
                        ViewGroup viewGroup = panelFeatureState.g;
                        if (viewGroup == null) {
                            if (!b(panelFeatureState) || panelFeatureState.g == null) {
                                return;
                            }
                        } else if (panelFeatureState.q && viewGroup.getChildCount() > 0) {
                            panelFeatureState.g.removeAllViews();
                        }
                        if (!a(panelFeatureState) || !panelFeatureState.a()) {
                            panelFeatureState.q = true;
                            return;
                        }
                        ViewGroup.LayoutParams layoutParams2 = panelFeatureState.h.getLayoutParams();
                        if (layoutParams2 == null) {
                            layoutParams2 = new ViewGroup.LayoutParams(-2, -2);
                        }
                        panelFeatureState.g.setBackgroundResource(panelFeatureState.f247b);
                        ViewParent parent = panelFeatureState.h.getParent();
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).removeView(panelFeatureState.h);
                        }
                        panelFeatureState.g.addView(panelFeatureState.h, layoutParams2);
                        if (!panelFeatureState.h.hasFocus()) {
                            panelFeatureState.h.requestFocus();
                        }
                    } else {
                        View view = panelFeatureState.i;
                        if (!(view == null || (layoutParams = view.getLayoutParams()) == null || layoutParams.width != -1)) {
                            i2 = -1;
                            panelFeatureState.n = false;
                            WindowManager.LayoutParams layoutParams3 = new WindowManager.LayoutParams(i2, -2, panelFeatureState.f249d, panelFeatureState.e, 1002, 8519680, -3);
                            layoutParams3.gravity = panelFeatureState.f248c;
                            layoutParams3.windowAnimations = panelFeatureState.f;
                            windowManager.addView(panelFeatureState.g, layoutParams3);
                            panelFeatureState.o = true;
                            return;
                        }
                    }
                    i2 = -2;
                    panelFeatureState.n = false;
                    WindowManager.LayoutParams layoutParams32 = new WindowManager.LayoutParams(i2, -2, panelFeatureState.f249d, panelFeatureState.e, 1002, 8519680, -3);
                    layoutParams32.gravity = panelFeatureState.f248c;
                    layoutParams32.windowAnimations = panelFeatureState.f;
                    windowManager.addView(panelFeatureState.g, layoutParams32);
                    panelFeatureState.o = true;
                    return;
                }
                return;
            }
            a(panelFeatureState, true);
        }
    }

    private void a(boolean z2) {
        L l2 = this.q;
        if (l2 == null || !l2.a() || (ViewConfiguration.get(this.j).hasPermanentMenuKey() && !this.q.f())) {
            PanelFeatureState a2 = a(0, true);
            a2.q = true;
            a(a2, false);
            a(a2, (KeyEvent) null);
            return;
        }
        Window.Callback q2 = q();
        if (this.q.d() && z2) {
            this.q.b();
            if (!this.R) {
                q2.onPanelClosed(108, a(0, true).j);
            }
        } else if (q2 != null && !this.R) {
            if (this.Y && (this.Z & 1) != 0) {
                this.k.getDecorView().removeCallbacks(this.aa);
                this.aa.run();
            }
            PanelFeatureState a3 = a(0, true);
            androidx.appcompat.view.menu.j jVar = a3.j;
            if (jVar != null && !a3.r && q2.onPreparePanel(0, a3.i, jVar)) {
                q2.onMenuOpened(108, a3.j);
                this.q.c();
            }
        }
    }

    private boolean a(int i2, boolean z2, @Nullable Configuration configuration) {
        Configuration configuration2;
        int i3 = this.j.getApplicationContext().getResources().getConfiguration().uiMode & 48;
        boolean z3 = true;
        int i4 = i2 != 1 ? i2 != 2 ? i3 : 32 : 16;
        boolean D2 = D();
        boolean z4 = false;
        if ((h || i4 != i3) && !D2 && Build.VERSION.SDK_INT >= 17 && !this.O) {
            Object obj = this.i;
            if (obj instanceof ContextThemeWrapper) {
                ContextThemeWrapper contextThemeWrapper = (ContextThemeWrapper) obj;
                if (configuration != null) {
                    configuration2 = new Configuration(configuration);
                } else {
                    configuration2 = new Configuration();
                    configuration2.fontScale = 0.0f;
                }
                configuration2.uiMode = (configuration2.uiMode & -49) | i4;
                try {
                    contextThemeWrapper.applyOverrideConfiguration(configuration2);
                    z4 = true;
                } catch (IllegalStateException e2) {
                    if (i4 != i3) {
                        Log.w("AppCompatDelegate", "updateForNightMode. Calling applyOverrideConfiguration() failed with an exception. Will fall back to using Resources.updateConfiguration()", e2);
                    }
                }
            }
        }
        int i5 = this.j.getResources().getConfiguration().uiMode & 48;
        if (!z4 && i5 != i4 && z2 && !D2 && this.O && (Build.VERSION.SDK_INT >= 17 || this.P)) {
            Object obj2 = this.i;
            if (obj2 instanceof Activity) {
                androidx.core.app.a.b((Activity) obj2);
                z4 = true;
            }
        }
        if (z4 || i5 == i4) {
            z3 = z4;
        } else {
            b(i4, D2, configuration);
        }
        if (z3) {
            Object obj3 = this.i;
            if (obj3 instanceof l) {
                ((l) obj3).b(i2);
            }
        }
        return z3;
    }

    private boolean a(ViewParent viewParent) {
        if (viewParent == null) {
            return false;
        }
        View decorView = this.k.getDecorView();
        while (viewParent != null) {
            if (viewParent == decorView || !(viewParent instanceof View) || ViewCompat.r((View) viewParent)) {
                return false;
            }
            viewParent = viewParent.getParent();
        }
        return true;
    }

    private boolean a(PanelFeatureState panelFeatureState) {
        View view = panelFeatureState.i;
        if (view != null) {
            panelFeatureState.h = view;
            return true;
        } else if (panelFeatureState.j == null) {
            return false;
        } else {
            if (this.s == null) {
                this.s = new k();
            }
            panelFeatureState.h = (View) panelFeatureState.a((s.a) this.s);
            return panelFeatureState.h != null;
        }
    }

    private boolean a(PanelFeatureState panelFeatureState, int i2, KeyEvent keyEvent, int i3) {
        androidx.appcompat.view.menu.j jVar;
        boolean z2 = false;
        if (keyEvent.isSystem()) {
            return false;
        }
        if ((panelFeatureState.m || b(panelFeatureState, keyEvent)) && (jVar = panelFeatureState.j) != null) {
            z2 = jVar.performShortcut(i2, keyEvent, i3);
        }
        if (z2 && (i3 & 1) == 0 && this.q == null) {
            a(panelFeatureState, true);
        }
        return z2;
    }

    private boolean a(boolean z2, @Nullable Configuration configuration) {
        if (this.R) {
            return false;
        }
        int w2 = w();
        boolean a2 = a(g(w2), z2, configuration);
        if (w2 == 0) {
            o().e();
        } else {
            e eVar = this.W;
            if (eVar != null) {
                eVar.a();
            }
        }
        if (w2 == 3) {
            B().e();
        } else {
            e eVar2 = this.X;
            if (eVar2 != null) {
                eVar2.a();
            }
        }
        return a2;
    }

    private void b(int i2, boolean z2, @Nullable Configuration configuration) {
        Resources resources = this.j.getResources();
        Configuration configuration2 = new Configuration(resources.getConfiguration());
        if (configuration != null) {
            configuration2.updateFrom(configuration);
        }
        configuration2.uiMode = i2 | (resources.getConfiguration().uiMode & -49);
        resources.updateConfiguration(configuration2, (DisplayMetrics) null);
        if (Build.VERSION.SDK_INT < 26) {
            A.a(resources);
        }
        int i3 = this.T;
        if (i3 != 0) {
            this.j.setTheme(i3);
            if (Build.VERSION.SDK_INT >= 23) {
                this.j.getTheme().applyStyle(this.T, true);
            }
        }
        if (z2) {
            Object obj = this.i;
            if (obj instanceof Activity) {
                Activity activity = (Activity) obj;
                if (activity instanceof androidx.lifecycle.i) {
                    if (!((androidx.lifecycle.i) activity).a().a().a(f.b.STARTED)) {
                        return;
                    }
                } else if (!this.Q) {
                    return;
                }
                activity.onConfigurationChanged(configuration2);
            }
        }
    }

    private boolean b(PanelFeatureState panelFeatureState) {
        panelFeatureState.a(n());
        panelFeatureState.g = new j(panelFeatureState.l);
        panelFeatureState.f248c = 81;
        return true;
    }

    private boolean b(PanelFeatureState panelFeatureState, KeyEvent keyEvent) {
        L l2;
        L l3;
        L l4;
        if (this.R) {
            return false;
        }
        if (panelFeatureState.m) {
            return true;
        }
        PanelFeatureState panelFeatureState2 = this.M;
        if (!(panelFeatureState2 == null || panelFeatureState2 == panelFeatureState)) {
            a(panelFeatureState2, false);
        }
        Window.Callback q2 = q();
        if (q2 != null) {
            panelFeatureState.i = q2.onCreatePanelView(panelFeatureState.f246a);
        }
        int i2 = panelFeatureState.f246a;
        boolean z2 = i2 == 0 || i2 == 108;
        if (z2 && (l4 = this.q) != null) {
            l4.e();
        }
        if (panelFeatureState.i == null) {
            if (z2) {
                t();
            }
            if (panelFeatureState.j == null || panelFeatureState.r) {
                if (panelFeatureState.j == null && (!c(panelFeatureState) || panelFeatureState.j == null)) {
                    return false;
                }
                if (z2 && this.q != null) {
                    if (this.r == null) {
                        this.r = new a();
                    }
                    this.q.a(panelFeatureState.j, this.r);
                }
                panelFeatureState.j.s();
                if (!q2.onCreatePanelMenu(panelFeatureState.f246a, panelFeatureState.j)) {
                    panelFeatureState.a((androidx.appcompat.view.menu.j) null);
                    if (z2 && (l3 = this.q) != null) {
                        l3.a((Menu) null, this.r);
                    }
                    return false;
                }
                panelFeatureState.r = false;
            }
            panelFeatureState.j.s();
            Bundle bundle = panelFeatureState.s;
            if (bundle != null) {
                panelFeatureState.j.a(bundle);
                panelFeatureState.s = null;
            }
            if (!q2.onPreparePanel(0, panelFeatureState.i, panelFeatureState.j)) {
                if (z2 && (l2 = this.q) != null) {
                    l2.a((Menu) null, this.r);
                }
                panelFeatureState.j.r();
                return false;
            }
            panelFeatureState.p = KeyCharacterMap.load(keyEvent != null ? keyEvent.getDeviceId() : -1).getKeyboardType() != 1;
            panelFeatureState.j.setQwertyMode(panelFeatureState.p);
            panelFeatureState.j.r();
        }
        panelFeatureState.m = true;
        panelFeatureState.n = false;
        this.M = panelFeatureState;
        return true;
    }

    private boolean c(PanelFeatureState panelFeatureState) {
        Context context = this.j;
        int i2 = panelFeatureState.f246a;
        if ((i2 == 0 || i2 == 108) && this.q != null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(a.a.a.actionBarTheme, typedValue, true);
            Resources.Theme theme2 = null;
            if (typedValue.resourceId != 0) {
                theme2 = context.getResources().newTheme();
                theme2.setTo(theme);
                theme2.applyStyle(typedValue.resourceId, true);
                theme2.resolveAttribute(a.a.a.actionBarWidgetTheme, typedValue, true);
            } else {
                theme.resolveAttribute(a.a.a.actionBarWidgetTheme, typedValue, true);
            }
            if (typedValue.resourceId != 0) {
                if (theme2 == null) {
                    theme2 = context.getResources().newTheme();
                    theme2.setTo(theme);
                }
                theme2.applyStyle(typedValue.resourceId, true);
            }
            if (theme2 != null) {
                a.a.d.d dVar = new a.a.d.d(context, 0);
                dVar.getTheme().setTo(theme2);
                context = dVar;
            }
        }
        androidx.appcompat.view.menu.j jVar = new androidx.appcompat.view.menu.j(context);
        jVar.a((j.a) this);
        panelFeatureState.a(jVar);
        return true;
    }

    private boolean d(int i2, KeyEvent keyEvent) {
        if (keyEvent.getRepeatCount() != 0) {
            return false;
        }
        PanelFeatureState a2 = a(i2, true);
        if (!a2.o) {
            return b(a2, keyEvent);
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x006c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean e(int r4, android.view.KeyEvent r5) {
        /*
            r3 = this;
            a.a.d.b r0 = r3.t
            r1 = 0
            if (r0 == 0) goto L_0x0006
            return r1
        L_0x0006:
            r0 = 1
            androidx.appcompat.app.AppCompatDelegateImpl$PanelFeatureState r2 = r3.a((int) r4, (boolean) r0)
            if (r4 != 0) goto L_0x0043
            androidx.appcompat.widget.L r4 = r3.q
            if (r4 == 0) goto L_0x0043
            boolean r4 = r4.a()
            if (r4 == 0) goto L_0x0043
            android.content.Context r4 = r3.j
            android.view.ViewConfiguration r4 = android.view.ViewConfiguration.get(r4)
            boolean r4 = r4.hasPermanentMenuKey()
            if (r4 != 0) goto L_0x0043
            androidx.appcompat.widget.L r4 = r3.q
            boolean r4 = r4.d()
            if (r4 != 0) goto L_0x003c
            boolean r4 = r3.R
            if (r4 != 0) goto L_0x0063
            boolean r4 = r3.b((androidx.appcompat.app.AppCompatDelegateImpl.PanelFeatureState) r2, (android.view.KeyEvent) r5)
            if (r4 == 0) goto L_0x0063
            androidx.appcompat.widget.L r4 = r3.q
            boolean r4 = r4.c()
            goto L_0x006a
        L_0x003c:
            androidx.appcompat.widget.L r4 = r3.q
            boolean r4 = r4.b()
            goto L_0x006a
        L_0x0043:
            boolean r4 = r2.o
            if (r4 != 0) goto L_0x0065
            boolean r4 = r2.n
            if (r4 == 0) goto L_0x004c
            goto L_0x0065
        L_0x004c:
            boolean r4 = r2.m
            if (r4 == 0) goto L_0x0063
            boolean r4 = r2.r
            if (r4 == 0) goto L_0x005b
            r2.m = r1
            boolean r4 = r3.b((androidx.appcompat.app.AppCompatDelegateImpl.PanelFeatureState) r2, (android.view.KeyEvent) r5)
            goto L_0x005c
        L_0x005b:
            r4 = r0
        L_0x005c:
            if (r4 == 0) goto L_0x0063
            r3.a((androidx.appcompat.app.AppCompatDelegateImpl.PanelFeatureState) r2, (android.view.KeyEvent) r5)
            r4 = r0
            goto L_0x006a
        L_0x0063:
            r4 = r1
            goto L_0x006a
        L_0x0065:
            boolean r4 = r2.o
            r3.a((androidx.appcompat.app.AppCompatDelegateImpl.PanelFeatureState) r2, (boolean) r0)
        L_0x006a:
            if (r4 == 0) goto L_0x0083
            android.content.Context r5 = r3.j
            java.lang.String r0 = "audio"
            java.lang.Object r5 = r5.getSystemService(r0)
            android.media.AudioManager r5 = (android.media.AudioManager) r5
            if (r5 == 0) goto L_0x007c
            r5.playSoundEffect(r1)
            goto L_0x0083
        L_0x007c:
            java.lang.String r5 = "AppCompatDelegate"
            java.lang.String r0 = "Couldn't get audio manager"
            android.util.Log.w(r5, r0)
        L_0x0083:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.app.AppCompatDelegateImpl.e(int, android.view.KeyEvent):boolean");
    }

    private void k(int i2) {
        this.Z = (1 << i2) | this.Z;
        if (!this.Y) {
            ViewCompat.a(this.k.getDecorView(), this.aa);
            this.Y = true;
        }
    }

    private int l(int i2) {
        if (i2 == 8) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
            return 108;
        } else if (i2 != 9) {
            return i2;
        } else {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
            return 109;
        }
    }

    private void v() {
        ContentFrameLayout contentFrameLayout = (ContentFrameLayout) this.A.findViewById(16908290);
        View decorView = this.k.getDecorView();
        contentFrameLayout.a(decorView.getPaddingLeft(), decorView.getPaddingTop(), decorView.getPaddingRight(), decorView.getPaddingBottom());
        TypedArray obtainStyledAttributes = this.j.obtainStyledAttributes(a.a.j.AppCompatTheme);
        obtainStyledAttributes.getValue(a.a.j.AppCompatTheme_windowMinWidthMajor, contentFrameLayout.getMinWidthMajor());
        obtainStyledAttributes.getValue(a.a.j.AppCompatTheme_windowMinWidthMinor, contentFrameLayout.getMinWidthMinor());
        if (obtainStyledAttributes.hasValue(a.a.j.AppCompatTheme_windowFixedWidthMajor)) {
            obtainStyledAttributes.getValue(a.a.j.AppCompatTheme_windowFixedWidthMajor, contentFrameLayout.getFixedWidthMajor());
        }
        if (obtainStyledAttributes.hasValue(a.a.j.AppCompatTheme_windowFixedWidthMinor)) {
            obtainStyledAttributes.getValue(a.a.j.AppCompatTheme_windowFixedWidthMinor, contentFrameLayout.getFixedWidthMinor());
        }
        if (obtainStyledAttributes.hasValue(a.a.j.AppCompatTheme_windowFixedHeightMajor)) {
            obtainStyledAttributes.getValue(a.a.j.AppCompatTheme_windowFixedHeightMajor, contentFrameLayout.getFixedHeightMajor());
        }
        if (obtainStyledAttributes.hasValue(a.a.j.AppCompatTheme_windowFixedHeightMinor)) {
            obtainStyledAttributes.getValue(a.a.j.AppCompatTheme_windowFixedHeightMinor, contentFrameLayout.getFixedHeightMinor());
        }
        obtainStyledAttributes.recycle();
        contentFrameLayout.requestLayout();
    }

    private int w() {
        int i2 = this.S;
        return i2 != -100 ? i2 : AppCompatDelegate.a();
    }

    private void x() {
        e eVar = this.W;
        if (eVar != null) {
            eVar.a();
        }
        e eVar2 = this.X;
        if (eVar2 != null) {
            eVar2.a();
        }
    }

    private ViewGroup y() {
        ViewGroup viewGroup;
        TypedArray obtainStyledAttributes = this.j.obtainStyledAttributes(a.a.j.AppCompatTheme);
        if (obtainStyledAttributes.hasValue(a.a.j.AppCompatTheme_windowActionBar)) {
            if (obtainStyledAttributes.getBoolean(a.a.j.AppCompatTheme_windowNoTitle, false)) {
                b(1);
            } else if (obtainStyledAttributes.getBoolean(a.a.j.AppCompatTheme_windowActionBar, false)) {
                b(108);
            }
            if (obtainStyledAttributes.getBoolean(a.a.j.AppCompatTheme_windowActionBarOverlay, false)) {
                b(109);
            }
            if (obtainStyledAttributes.getBoolean(a.a.j.AppCompatTheme_windowActionModeOverlay, false)) {
                b(10);
            }
            this.I = obtainStyledAttributes.getBoolean(a.a.j.AppCompatTheme_android_windowIsFloating, false);
            obtainStyledAttributes.recycle();
            A();
            this.k.getDecorView();
            LayoutInflater from = LayoutInflater.from(this.j);
            if (this.J) {
                viewGroup = (ViewGroup) from.inflate(this.H ? a.a.g.abc_screen_simple_overlay_action_mode : a.a.g.abc_screen_simple, (ViewGroup) null);
                if (Build.VERSION.SDK_INT >= 21) {
                    ViewCompat.a((View) viewGroup, (q) new p(this));
                } else {
                    ((P) viewGroup).setOnFitSystemWindowsListener(new q(this));
                }
            } else if (this.I) {
                viewGroup = (ViewGroup) from.inflate(a.a.g.abc_dialog_title_material, (ViewGroup) null);
                this.G = false;
                this.F = false;
            } else if (this.F) {
                TypedValue typedValue = new TypedValue();
                this.j.getTheme().resolveAttribute(a.a.a.actionBarTheme, typedValue, true);
                int i2 = typedValue.resourceId;
                viewGroup = (ViewGroup) LayoutInflater.from(i2 != 0 ? new a.a.d.d(this.j, i2) : this.j).inflate(a.a.g.abc_screen_toolbar, (ViewGroup) null);
                this.q = (L) viewGroup.findViewById(a.a.f.decor_content_parent);
                this.q.setWindowCallback(q());
                if (this.G) {
                    this.q.a(109);
                }
                if (this.D) {
                    this.q.a(2);
                }
                if (this.E) {
                    this.q.a(5);
                }
            } else {
                viewGroup = null;
            }
            if (viewGroup != null) {
                if (this.q == null) {
                    this.B = (TextView) viewGroup.findViewById(a.a.f.title);
                }
                Ja.b(viewGroup);
                ContentFrameLayout contentFrameLayout = (ContentFrameLayout) viewGroup.findViewById(a.a.f.action_bar_activity_content);
                ViewGroup viewGroup2 = (ViewGroup) this.k.findViewById(16908290);
                if (viewGroup2 != null) {
                    while (viewGroup2.getChildCount() > 0) {
                        View childAt = viewGroup2.getChildAt(0);
                        viewGroup2.removeViewAt(0);
                        contentFrameLayout.addView(childAt);
                    }
                    viewGroup2.setId(-1);
                    contentFrameLayout.setId(16908290);
                    if (viewGroup2 instanceof FrameLayout) {
                        ((FrameLayout) viewGroup2).setForeground((Drawable) null);
                    }
                }
                this.k.setContentView(viewGroup);
                contentFrameLayout.setAttachListener(new r(this));
                return viewGroup;
            }
            throw new IllegalArgumentException("AppCompat does not support the current theme features: { windowActionBar: " + this.F + ", windowActionBarOverlay: " + this.G + ", android:windowIsFloating: " + this.I + ", windowActionModeOverlay: " + this.H + ", windowNoTitle: " + this.J + " }");
        }
        obtainStyledAttributes.recycle();
        throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
    }

    private void z() {
        if (!this.z) {
            this.A = y();
            CharSequence p2 = p();
            if (!TextUtils.isEmpty(p2)) {
                L l2 = this.q;
                if (l2 != null) {
                    l2.setWindowTitle(p2);
                } else if (t() != null) {
                    t().a(p2);
                } else {
                    TextView textView = this.B;
                    if (textView != null) {
                        textView.setText(p2);
                    }
                }
            }
            v();
            a(this.A);
            this.z = true;
            PanelFeatureState a2 = a(0, false);
            if (this.R) {
                return;
            }
            if (a2 == null || a2.j == null) {
                k(108);
            }
        }
    }

    public a.a.d.b a(@NonNull b.a aVar) {
        m mVar;
        if (aVar != null) {
            a.a.d.b bVar = this.t;
            if (bVar != null) {
                bVar.a();
            }
            b bVar2 = new b(aVar);
            ActionBar d2 = d();
            if (d2 != null) {
                this.t = d2.a((b.a) bVar2);
                a.a.d.b bVar3 = this.t;
                if (!(bVar3 == null || (mVar = this.m) == null)) {
                    mVar.a(bVar3);
                }
            }
            if (this.t == null) {
                this.t = b((b.a) bVar2);
            }
            return this.t;
        }
        throw new IllegalArgumentException("ActionMode callback can not be null.");
    }

    @Nullable
    public <T extends View> T a(@IdRes int i2) {
        z();
        return this.k.findViewById(i2);
    }

    public View a(View view, String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        AppCompatViewInflater appCompatViewInflater;
        boolean z2 = false;
        if (this.ea == null) {
            String string = this.j.obtainStyledAttributes(a.a.j.AppCompatTheme).getString(a.a.j.AppCompatTheme_viewInflaterClass);
            if (string == null) {
                appCompatViewInflater = new AppCompatViewInflater();
            } else {
                try {
                    this.ea = (AppCompatViewInflater) Class.forName(string).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                } catch (Throwable th) {
                    Log.i("AppCompatDelegate", "Failed to instantiate custom view inflater " + string + ". Falling back to default.", th);
                    appCompatViewInflater = new AppCompatViewInflater();
                }
            }
            this.ea = appCompatViewInflater;
        }
        if (e) {
            if (!(attributeSet instanceof XmlPullParser)) {
                z2 = a((ViewParent) view);
            } else if (((XmlPullParser) attributeSet).getDepth() > 1) {
                z2 = true;
            }
        }
        return this.ea.a(view, str, context, attributeSet, z2, e, true, Ia.b());
    }

    /* access modifiers changed from: protected */
    public PanelFeatureState a(int i2, boolean z2) {
        PanelFeatureState[] panelFeatureStateArr = this.L;
        if (panelFeatureStateArr == null || panelFeatureStateArr.length <= i2) {
            PanelFeatureState[] panelFeatureStateArr2 = new PanelFeatureState[(i2 + 1)];
            if (panelFeatureStateArr != null) {
                System.arraycopy(panelFeatureStateArr, 0, panelFeatureStateArr2, 0, panelFeatureStateArr.length);
            }
            this.L = panelFeatureStateArr2;
            panelFeatureStateArr = panelFeatureStateArr2;
        }
        PanelFeatureState panelFeatureState = panelFeatureStateArr[i2];
        if (panelFeatureState != null) {
            return panelFeatureState;
        }
        PanelFeatureState panelFeatureState2 = new PanelFeatureState(i2);
        panelFeatureStateArr[i2] = panelFeatureState2;
        return panelFeatureState2;
    }

    /* access modifiers changed from: package-private */
    public PanelFeatureState a(Menu menu) {
        PanelFeatureState[] panelFeatureStateArr = this.L;
        int length = panelFeatureStateArr != null ? panelFeatureStateArr.length : 0;
        for (int i2 = 0; i2 < length; i2++) {
            PanelFeatureState panelFeatureState = panelFeatureStateArr[i2];
            if (panelFeatureState != null && panelFeatureState.j == menu) {
                return panelFeatureState;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void a(int i2, PanelFeatureState panelFeatureState, Menu menu) {
        if (menu == null) {
            if (panelFeatureState == null && i2 >= 0) {
                PanelFeatureState[] panelFeatureStateArr = this.L;
                if (i2 < panelFeatureStateArr.length) {
                    panelFeatureState = panelFeatureStateArr[i2];
                }
            }
            if (panelFeatureState != null) {
                menu = panelFeatureState.j;
            }
        }
        if ((panelFeatureState == null || panelFeatureState.o) && !this.R) {
            this.l.a().onPanelClosed(i2, menu);
        }
    }

    public void a(Context context) {
        Configuration configuration = context.getApplicationContext().getResources().getConfiguration();
        Configuration configuration2 = context.getResources().getConfiguration();
        a(false, !configuration.equals(configuration2) ? a(configuration, configuration2) : null);
        this.O = true;
    }

    public void a(Configuration configuration) {
        ActionBar d2;
        if (this.F && this.z && (d2 = d()) != null) {
            d2.a(configuration);
        }
        C0112o.b().a(this.j);
        a(false, (Configuration) null);
    }

    public void a(Bundle bundle) {
        this.O = true;
        String str = null;
        a(false, (Configuration) null);
        A();
        Object obj = this.i;
        if (obj instanceof Activity) {
            try {
                str = androidx.core.app.h.b((Activity) obj);
            } catch (IllegalArgumentException unused) {
            }
            if (str != null) {
                ActionBar t2 = t();
                if (t2 == null) {
                    this.ba = true;
                } else {
                    t2.c(true);
                }
            }
        }
        this.P = true;
    }

    public void a(View view) {
        z();
        ViewGroup viewGroup = (ViewGroup) this.A.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view);
        this.l.a().onContentChanged();
    }

    public void a(View view, ViewGroup.LayoutParams layoutParams) {
        z();
        ((ViewGroup) this.A.findViewById(16908290)).addView(view, layoutParams);
        this.l.a().onContentChanged();
    }

    /* access modifiers changed from: package-private */
    public void a(ViewGroup viewGroup) {
    }

    /* access modifiers changed from: package-private */
    public void a(PanelFeatureState panelFeatureState, boolean z2) {
        ViewGroup viewGroup;
        L l2;
        if (!z2 || panelFeatureState.f246a != 0 || (l2 = this.q) == null || !l2.d()) {
            WindowManager windowManager = (WindowManager) this.j.getSystemService("window");
            if (!(windowManager == null || !panelFeatureState.o || (viewGroup = panelFeatureState.g) == null)) {
                windowManager.removeView(viewGroup);
                if (z2) {
                    a(panelFeatureState.f246a, panelFeatureState, (Menu) null);
                }
            }
            panelFeatureState.m = false;
            panelFeatureState.n = false;
            panelFeatureState.o = false;
            panelFeatureState.h = null;
            panelFeatureState.q = true;
            if (this.M == panelFeatureState) {
                this.M = null;
                return;
            }
            return;
        }
        b(panelFeatureState.j);
    }

    public void a(androidx.appcompat.view.menu.j jVar) {
        a(true);
    }

    public final void a(CharSequence charSequence) {
        this.p = charSequence;
        L l2 = this.q;
        if (l2 != null) {
            l2.setWindowTitle(charSequence);
        } else if (t() != null) {
            t().a(charSequence);
        } else {
            TextView textView = this.B;
            if (textView != null) {
                textView.setText(charSequence);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a(int i2, KeyEvent keyEvent) {
        boolean z2 = true;
        if (i2 == 4) {
            if ((keyEvent.getFlags() & 128) == 0) {
                z2 = false;
            }
            this.N = z2;
        } else if (i2 == 82) {
            d(0, keyEvent);
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean a(KeyEvent keyEvent) {
        View decorView;
        Object obj = this.i;
        boolean z2 = true;
        if (((obj instanceof C0126d.a) || (obj instanceof z)) && (decorView = this.k.getDecorView()) != null && C0126d.a(decorView, keyEvent)) {
            return true;
        }
        if (keyEvent.getKeyCode() == 82 && this.l.a().dispatchKeyEvent(keyEvent)) {
            return true;
        }
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() != 0) {
            z2 = false;
        }
        return z2 ? a(keyCode, keyEvent) : c(keyCode, keyEvent);
    }

    public boolean a(androidx.appcompat.view.menu.j jVar, MenuItem menuItem) {
        PanelFeatureState a2;
        Window.Callback q2 = q();
        if (q2 == null || this.R || (a2 = a((Menu) jVar.m())) == null) {
            return false;
        }
        return q2.onMenuItemSelected(a2.f246a, menuItem);
    }

    public int b() {
        return this.S;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0029  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public a.a.d.b b(@androidx.annotation.NonNull a.a.d.b.a r8) {
        /*
            r7 = this;
            r7.m()
            a.a.d.b r0 = r7.t
            if (r0 == 0) goto L_0x000a
            r0.a()
        L_0x000a:
            boolean r0 = r8 instanceof androidx.appcompat.app.AppCompatDelegateImpl.b
            if (r0 != 0) goto L_0x0014
            androidx.appcompat.app.AppCompatDelegateImpl$b r0 = new androidx.appcompat.app.AppCompatDelegateImpl$b
            r0.<init>(r8)
            r8 = r0
        L_0x0014:
            androidx.appcompat.app.m r0 = r7.m
            r1 = 0
            if (r0 == 0) goto L_0x0022
            boolean r2 = r7.R
            if (r2 != 0) goto L_0x0022
            a.a.d.b r0 = r0.a((a.a.d.b.a) r8)     // Catch:{ AbstractMethodError -> 0x0022 }
            goto L_0x0023
        L_0x0022:
            r0 = r1
        L_0x0023:
            if (r0 == 0) goto L_0x0029
            r7.t = r0
            goto L_0x0165
        L_0x0029:
            androidx.appcompat.widget.ActionBarContextView r0 = r7.u
            r2 = 0
            r3 = 1
            if (r0 != 0) goto L_0x00d6
            boolean r0 = r7.I
            if (r0 == 0) goto L_0x00b7
            android.util.TypedValue r0 = new android.util.TypedValue
            r0.<init>()
            android.content.Context r4 = r7.j
            android.content.res.Resources$Theme r4 = r4.getTheme()
            int r5 = a.a.a.actionBarTheme
            r4.resolveAttribute(r5, r0, r3)
            int r5 = r0.resourceId
            if (r5 == 0) goto L_0x0068
            android.content.Context r5 = r7.j
            android.content.res.Resources r5 = r5.getResources()
            android.content.res.Resources$Theme r5 = r5.newTheme()
            r5.setTo(r4)
            int r4 = r0.resourceId
            r5.applyStyle(r4, r3)
            a.a.d.d r4 = new a.a.d.d
            android.content.Context r6 = r7.j
            r4.<init>((android.content.Context) r6, (int) r2)
            android.content.res.Resources$Theme r6 = r4.getTheme()
            r6.setTo(r5)
            goto L_0x006a
        L_0x0068:
            android.content.Context r4 = r7.j
        L_0x006a:
            androidx.appcompat.widget.ActionBarContextView r5 = new androidx.appcompat.widget.ActionBarContextView
            r5.<init>(r4)
            r7.u = r5
            android.widget.PopupWindow r5 = new android.widget.PopupWindow
            int r6 = a.a.a.actionModePopupWindowStyle
            r5.<init>(r4, r1, r6)
            r7.v = r5
            android.widget.PopupWindow r5 = r7.v
            r6 = 2
            androidx.core.widget.i.a((android.widget.PopupWindow) r5, (int) r6)
            android.widget.PopupWindow r5 = r7.v
            androidx.appcompat.widget.ActionBarContextView r6 = r7.u
            r5.setContentView(r6)
            android.widget.PopupWindow r5 = r7.v
            r6 = -1
            r5.setWidth(r6)
            android.content.res.Resources$Theme r5 = r4.getTheme()
            int r6 = a.a.a.actionBarSize
            r5.resolveAttribute(r6, r0, r3)
            int r0 = r0.data
            android.content.res.Resources r4 = r4.getResources()
            android.util.DisplayMetrics r4 = r4.getDisplayMetrics()
            int r0 = android.util.TypedValue.complexToDimensionPixelSize(r0, r4)
            androidx.appcompat.widget.ActionBarContextView r4 = r7.u
            r4.setContentHeight(r0)
            android.widget.PopupWindow r0 = r7.v
            r4 = -2
            r0.setHeight(r4)
            androidx.appcompat.app.t r0 = new androidx.appcompat.app.t
            r0.<init>(r7)
            r7.w = r0
            goto L_0x00d6
        L_0x00b7:
            android.view.ViewGroup r0 = r7.A
            int r4 = a.a.f.action_mode_bar_stub
            android.view.View r0 = r0.findViewById(r4)
            androidx.appcompat.widget.ViewStubCompat r0 = (androidx.appcompat.widget.ViewStubCompat) r0
            if (r0 == 0) goto L_0x00d6
            android.content.Context r4 = r7.n()
            android.view.LayoutInflater r4 = android.view.LayoutInflater.from(r4)
            r0.setLayoutInflater(r4)
            android.view.View r0 = r0.a()
            androidx.appcompat.widget.ActionBarContextView r0 = (androidx.appcompat.widget.ActionBarContextView) r0
            r7.u = r0
        L_0x00d6:
            androidx.appcompat.widget.ActionBarContextView r0 = r7.u
            if (r0 == 0) goto L_0x0165
            r7.m()
            androidx.appcompat.widget.ActionBarContextView r0 = r7.u
            r0.c()
            a.a.d.e r0 = new a.a.d.e
            androidx.appcompat.widget.ActionBarContextView r4 = r7.u
            android.content.Context r4 = r4.getContext()
            androidx.appcompat.widget.ActionBarContextView r5 = r7.u
            android.widget.PopupWindow r6 = r7.v
            if (r6 != 0) goto L_0x00f1
            goto L_0x00f2
        L_0x00f1:
            r3 = r2
        L_0x00f2:
            r0.<init>(r4, r5, r8, r3)
            android.view.Menu r3 = r0.c()
            boolean r8 = r8.a((a.a.d.b) r0, (android.view.Menu) r3)
            if (r8 == 0) goto L_0x0163
            r0.i()
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            r8.a(r0)
            r7.t = r0
            boolean r8 = r7.u()
            r0 = 1065353216(0x3f800000, float:1.0)
            if (r8 == 0) goto L_0x012d
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            r1 = 0
            r8.setAlpha(r1)
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            androidx.core.view.D r8 = androidx.core.view.ViewCompat.a(r8)
            r8.a((float) r0)
            r7.x = r8
            androidx.core.view.D r8 = r7.x
            androidx.appcompat.app.u r0 = new androidx.appcompat.app.u
            r0.<init>(r7)
            r8.a((androidx.core.view.E) r0)
            goto L_0x0153
        L_0x012d:
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            r8.setAlpha(r0)
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            r8.setVisibility(r2)
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            r0 = 32
            r8.sendAccessibilityEvent(r0)
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            android.view.ViewParent r8 = r8.getParent()
            boolean r8 = r8 instanceof android.view.View
            if (r8 == 0) goto L_0x0153
            androidx.appcompat.widget.ActionBarContextView r8 = r7.u
            android.view.ViewParent r8 = r8.getParent()
            android.view.View r8 = (android.view.View) r8
            androidx.core.view.ViewCompat.v(r8)
        L_0x0153:
            android.widget.PopupWindow r8 = r7.v
            if (r8 == 0) goto L_0x0165
            android.view.Window r8 = r7.k
            android.view.View r8 = r8.getDecorView()
            java.lang.Runnable r0 = r7.w
            r8.post(r0)
            goto L_0x0165
        L_0x0163:
            r7.t = r1
        L_0x0165:
            a.a.d.b r8 = r7.t
            if (r8 == 0) goto L_0x0170
            androidx.appcompat.app.m r0 = r7.m
            if (r0 == 0) goto L_0x0170
            r0.a((a.a.d.b) r8)
        L_0x0170:
            a.a.d.b r8 = r7.t
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.app.AppCompatDelegateImpl.b(a.a.d.b$a):a.a.d.b");
    }

    public void b(Bundle bundle) {
        z();
    }

    public void b(View view, ViewGroup.LayoutParams layoutParams) {
        z();
        ViewGroup viewGroup = (ViewGroup) this.A.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view, layoutParams);
        this.l.a().onContentChanged();
    }

    /* access modifiers changed from: package-private */
    public void b(androidx.appcompat.view.menu.j jVar) {
        if (!this.K) {
            this.K = true;
            this.q.g();
            Window.Callback q2 = q();
            if (q2 != null && !this.R) {
                q2.onPanelClosed(108, jVar);
            }
            this.K = false;
        }
    }

    public boolean b(int i2) {
        int l2 = l(i2);
        if (this.J && l2 == 108) {
            return false;
        }
        if (this.F && l2 == 1) {
            this.F = false;
        }
        if (l2 == 1) {
            E();
            this.J = true;
            return true;
        } else if (l2 == 2) {
            E();
            this.D = true;
            return true;
        } else if (l2 == 5) {
            E();
            this.E = true;
            return true;
        } else if (l2 == 10) {
            E();
            this.H = true;
            return true;
        } else if (l2 == 108) {
            E();
            this.F = true;
            return true;
        } else if (l2 != 109) {
            return this.k.requestFeature(l2);
        } else {
            E();
            this.G = true;
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean b(int i2, KeyEvent keyEvent) {
        ActionBar d2 = d();
        if (d2 != null && d2.a(i2, keyEvent)) {
            return true;
        }
        PanelFeatureState panelFeatureState = this.M;
        if (panelFeatureState == null || !a(panelFeatureState, keyEvent.getKeyCode(), keyEvent, 1)) {
            if (this.M == null) {
                PanelFeatureState a2 = a(0, true);
                b(a2, keyEvent);
                boolean a3 = a(a2, keyEvent.getKeyCode(), keyEvent, 1);
                a2.m = false;
                if (a3) {
                    return true;
                }
            }
            return false;
        }
        PanelFeatureState panelFeatureState2 = this.M;
        if (panelFeatureState2 != null) {
            panelFeatureState2.n = true;
        }
        return true;
    }

    public MenuInflater c() {
        if (this.o == null) {
            C();
            ActionBar actionBar = this.n;
            this.o = new a.a.d.g(actionBar != null ? actionBar.h() : this.j);
        }
        return this.o;
    }

    public void c(int i2) {
        z();
        ViewGroup viewGroup = (ViewGroup) this.A.findViewById(16908290);
        viewGroup.removeAllViews();
        LayoutInflater.from(this.j).inflate(i2, viewGroup);
        this.l.a().onContentChanged();
    }

    public void c(Bundle bundle) {
        if (this.S != -100) {
            f245d.put(this.i.getClass(), Integer.valueOf(this.S));
        }
    }

    /* access modifiers changed from: package-private */
    public boolean c(int i2, KeyEvent keyEvent) {
        if (i2 == 4) {
            boolean z2 = this.N;
            this.N = false;
            PanelFeatureState a2 = a(0, false);
            if (a2 != null && a2.o) {
                if (!z2) {
                    a(a2, true);
                }
                return true;
            } else if (s()) {
                return true;
            }
        } else if (i2 == 82) {
            e(0, keyEvent);
            return true;
        }
        return false;
    }

    public ActionBar d() {
        C();
        return this.n;
    }

    public void d(@StyleRes int i2) {
        this.T = i2;
    }

    public void e() {
        LayoutInflater from = LayoutInflater.from(this.j);
        if (from.getFactory() == null) {
            C0127e.a(from, this);
        } else if (!(from.getFactory2() instanceof AppCompatDelegateImpl)) {
            Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
        }
    }

    /* access modifiers changed from: package-private */
    public void e(int i2) {
        a(a(i2, true), true);
    }

    public void f() {
        ActionBar d2 = d();
        if (d2 == null || !d2.i()) {
            k(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void f(int i2) {
        PanelFeatureState a2;
        PanelFeatureState a3 = a(i2, true);
        if (a3.j != null) {
            Bundle bundle = new Bundle();
            a3.j.b(bundle);
            if (bundle.size() > 0) {
                a3.s = bundle;
            }
            a3.j.s();
            a3.j.clear();
        }
        a3.r = true;
        a3.q = true;
        if ((i2 == 108 || i2 == 0) && this.q != null && (a2 = a(0, false)) != null) {
            a2.m = false;
            b(a2, (KeyEvent) null);
        }
    }

    /* access modifiers changed from: package-private */
    public int g(int i2) {
        e o2;
        if (i2 == -100) {
            return -1;
        }
        if (i2 != -1) {
            if (i2 != 0) {
                if (!(i2 == 1 || i2 == 2)) {
                    if (i2 == 3) {
                        o2 = B();
                    } else {
                        throw new IllegalStateException("Unknown value set for night mode. Please use one of the MODE_NIGHT values from AppCompatDelegate.");
                    }
                }
            } else if (Build.VERSION.SDK_INT >= 23 && ((UiModeManager) this.j.getSystemService(UiModeManager.class)).getNightMode() == 0) {
                return -1;
            } else {
                o2 = o();
            }
            return o2.c();
        }
        return i2;
    }

    public void g() {
        AppCompatDelegate.b((AppCompatDelegate) this);
        if (this.Y) {
            this.k.getDecorView().removeCallbacks(this.aa);
        }
        this.Q = false;
        this.R = true;
        ActionBar actionBar = this.n;
        if (actionBar != null) {
            actionBar.j();
        }
        x();
    }

    public void h() {
        ActionBar d2 = d();
        if (d2 != null) {
            d2.d(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void h(int i2) {
        ActionBar d2;
        if (i2 == 108 && (d2 = d()) != null) {
            d2.b(true);
        }
    }

    public void i() {
        this.Q = true;
        k();
        AppCompatDelegate.a((AppCompatDelegate) this);
    }

    /* access modifiers changed from: package-private */
    public void i(int i2) {
        if (i2 == 108) {
            ActionBar d2 = d();
            if (d2 != null) {
                d2.b(false);
            }
        } else if (i2 == 0) {
            PanelFeatureState a2 = a(i2, true);
            if (a2.o) {
                a(a2, false);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int j(int i2) {
        boolean z2;
        boolean z3;
        ActionBarContextView actionBarContextView = this.u;
        int i3 = 0;
        if (actionBarContextView == null || !(actionBarContextView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams)) {
            z2 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.u.getLayoutParams();
            z2 = true;
            if (this.u.isShown()) {
                if (this.ca == null) {
                    this.ca = new Rect();
                    this.da = new Rect();
                }
                Rect rect = this.ca;
                Rect rect2 = this.da;
                rect.set(0, i2, 0, 0);
                Ja.a(this.A, rect, rect2);
                if (marginLayoutParams.topMargin != (rect2.top == 0 ? i2 : 0)) {
                    marginLayoutParams.topMargin = i2;
                    View view = this.C;
                    if (view == null) {
                        this.C = new View(this.j);
                        this.C.setBackgroundColor(this.j.getResources().getColor(a.a.c.abc_input_method_navigation_guard));
                        this.A.addView(this.C, -1, new ViewGroup.LayoutParams(-1, i2));
                    } else {
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        if (layoutParams.height != i2) {
                            layoutParams.height = i2;
                            this.C.setLayoutParams(layoutParams);
                        }
                    }
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (this.C == null) {
                    z2 = false;
                }
                if (!this.H && z2) {
                    i2 = 0;
                }
            } else if (marginLayoutParams.topMargin != 0) {
                marginLayoutParams.topMargin = 0;
                z3 = true;
                z2 = false;
            } else {
                z3 = false;
                z2 = false;
            }
            if (z3) {
                this.u.setLayoutParams(marginLayoutParams);
            }
        }
        View view2 = this.C;
        if (view2 != null) {
            if (!z2) {
                i3 = 8;
            }
            view2.setVisibility(i3);
        }
        return i2;
    }

    public void j() {
        this.Q = false;
        AppCompatDelegate.b((AppCompatDelegate) this);
        ActionBar d2 = d();
        if (d2 != null) {
            d2.d(false);
        }
        if (this.i instanceof Dialog) {
            x();
        }
    }

    public boolean k() {
        return a(true, (Configuration) null);
    }

    /* access modifiers changed from: package-private */
    public void l() {
        androidx.appcompat.view.menu.j jVar;
        L l2 = this.q;
        if (l2 != null) {
            l2.g();
        }
        if (this.v != null) {
            this.k.getDecorView().removeCallbacks(this.w);
            if (this.v.isShowing()) {
                try {
                    this.v.dismiss();
                } catch (IllegalArgumentException unused) {
                }
            }
            this.v = null;
        }
        m();
        PanelFeatureState a2 = a(0, false);
        if (a2 != null && (jVar = a2.j) != null) {
            jVar.close();
        }
    }

    /* access modifiers changed from: package-private */
    public void m() {
        D d2 = this.x;
        if (d2 != null) {
            d2.a();
        }
    }

    /* access modifiers changed from: package-private */
    public final Context n() {
        ActionBar d2 = d();
        Context h2 = d2 != null ? d2.h() : null;
        return h2 == null ? this.j : h2;
    }

    /* access modifiers changed from: package-private */
    @NonNull
    @RestrictTo({RestrictTo.a.LIBRARY})
    public final e o() {
        if (this.W == null) {
            this.W = new f(C.a(this.j));
        }
        return this.W;
    }

    public final View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        return a(view, str, context, attributeSet);
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return onCreateView((View) null, str, context, attributeSet);
    }

    /* access modifiers changed from: package-private */
    public final CharSequence p() {
        Object obj = this.i;
        return obj instanceof Activity ? ((Activity) obj).getTitle() : this.p;
    }

    /* access modifiers changed from: package-private */
    public final Window.Callback q() {
        return this.k.getCallback();
    }

    public boolean r() {
        return this.y;
    }

    /* access modifiers changed from: package-private */
    public boolean s() {
        a.a.d.b bVar = this.t;
        if (bVar != null) {
            bVar.a();
            return true;
        }
        ActionBar d2 = d();
        return d2 != null && d2.f();
    }

    /* access modifiers changed from: package-private */
    public final ActionBar t() {
        return this.n;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.A;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean u() {
        /*
            r1 = this;
            boolean r0 = r1.z
            if (r0 == 0) goto L_0x0010
            android.view.ViewGroup r0 = r1.A
            if (r0 == 0) goto L_0x0010
            boolean r0 = androidx.core.view.ViewCompat.s(r0)
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.app.AppCompatDelegateImpl.u():boolean");
    }
}
