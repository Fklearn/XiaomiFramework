package androidx.core.view;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.annotation.UiThread;
import androidx.core.view.C0123a;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewCompat {

    /* renamed from: a  reason: collision with root package name */
    private static final AtomicInteger f787a = new AtomicInteger(1);

    /* renamed from: b  reason: collision with root package name */
    private static Field f788b;

    /* renamed from: c  reason: collision with root package name */
    private static boolean f789c;

    /* renamed from: d  reason: collision with root package name */
    private static Field f790d;
    private static boolean e;
    private static WeakHashMap<View, String> f;
    private static WeakHashMap<View, D> g = null;
    private static Field h;
    private static boolean i = false;
    private static final int[] j = {a.d.b.accessibility_custom_action_0, a.d.b.accessibility_custom_action_1, a.d.b.accessibility_custom_action_2, a.d.b.accessibility_custom_action_3, a.d.b.accessibility_custom_action_4, a.d.b.accessibility_custom_action_5, a.d.b.accessibility_custom_action_6, a.d.b.accessibility_custom_action_7, a.d.b.accessibility_custom_action_8, a.d.b.accessibility_custom_action_9, a.d.b.accessibility_custom_action_10, a.d.b.accessibility_custom_action_11, a.d.b.accessibility_custom_action_12, a.d.b.accessibility_custom_action_13, a.d.b.accessibility_custom_action_14, a.d.b.accessibility_custom_action_15, a.d.b.accessibility_custom_action_16, a.d.b.accessibility_custom_action_17, a.d.b.accessibility_custom_action_18, a.d.b.accessibility_custom_action_19, a.d.b.accessibility_custom_action_20, a.d.b.accessibility_custom_action_21, a.d.b.accessibility_custom_action_22, a.d.b.accessibility_custom_action_23, a.d.b.accessibility_custom_action_24, a.d.b.accessibility_custom_action_25, a.d.b.accessibility_custom_action_26, a.d.b.accessibility_custom_action_27, a.d.b.accessibility_custom_action_28, a.d.b.accessibility_custom_action_29, a.d.b.accessibility_custom_action_30, a.d.b.accessibility_custom_action_31};
    private static a k = new a();

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FocusDirection {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FocusRealDirection {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FocusRelativeDirection {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NestedScrollType {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollAxis {
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollIndicators {
    }

    static class a implements ViewTreeObserver.OnGlobalLayoutListener, View.OnAttachStateChangeListener {

        /* renamed from: a  reason: collision with root package name */
        private WeakHashMap<View, Boolean> f791a = new WeakHashMap<>();

        a() {
        }

        @RequiresApi(19)
        private void a(View view) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        @RequiresApi(19)
        private void a(View view, boolean z) {
            boolean z2 = view.getVisibility() == 0;
            if (z != z2) {
                if (z2) {
                    ViewCompat.a(view, 16);
                }
                this.f791a.put(view, Boolean.valueOf(z2));
            }
        }

        @RequiresApi(19)
        public void onGlobalLayout() {
            for (Map.Entry next : this.f791a.entrySet()) {
                a((View) next.getKey(), ((Boolean) next.getValue()).booleanValue());
            }
        }

        @RequiresApi(19)
        public void onViewAttachedToWindow(View view) {
            a(view);
        }

        public void onViewDetachedFromWindow(View view) {
        }
    }

    static abstract class b<T> {

        /* renamed from: a  reason: collision with root package name */
        private final int f792a;

        /* renamed from: b  reason: collision with root package name */
        private final Class<T> f793b;

        /* renamed from: c  reason: collision with root package name */
        private final int f794c;

        b(int i, Class<T> cls, int i2) {
            this(i, cls, 0, i2);
        }

        b(int i, Class<T> cls, int i2, int i3) {
            this.f792a = i;
            this.f793b = cls;
            this.f794c = i3;
        }

        private boolean a() {
            return Build.VERSION.SDK_INT >= 19;
        }

        private boolean b() {
            return Build.VERSION.SDK_INT >= this.f794c;
        }

        /* access modifiers changed from: package-private */
        public abstract T a(View view);

        /* access modifiers changed from: package-private */
        public T b(View view) {
            if (b()) {
                return a(view);
            }
            if (!a()) {
                return null;
            }
            T tag = view.getTag(this.f792a);
            if (this.f793b.isInstance(tag)) {
                return tag;
            }
            return null;
        }
    }

    public interface c {
        boolean onUnhandledKeyEvent(View view, KeyEvent keyEvent);
    }

    static class d {

        /* renamed from: a  reason: collision with root package name */
        private static final ArrayList<WeakReference<View>> f795a = new ArrayList<>();
        @Nullable

        /* renamed from: b  reason: collision with root package name */
        private WeakHashMap<View, Boolean> f796b = null;

        /* renamed from: c  reason: collision with root package name */
        private SparseArray<WeakReference<View>> f797c = null;

        /* renamed from: d  reason: collision with root package name */
        private WeakReference<KeyEvent> f798d = null;

        d() {
        }

        private SparseArray<WeakReference<View>> a() {
            if (this.f797c == null) {
                this.f797c = new SparseArray<>();
            }
            return this.f797c;
        }

        static d a(View view) {
            d dVar = (d) view.getTag(a.d.b.tag_unhandled_key_event_manager);
            if (dVar != null) {
                return dVar;
            }
            d dVar2 = new d();
            view.setTag(a.d.b.tag_unhandled_key_event_manager, dVar2);
            return dVar2;
        }

        @Nullable
        private View b(View view, KeyEvent keyEvent) {
            WeakHashMap<View, Boolean> weakHashMap = this.f796b;
            if (weakHashMap != null && weakHashMap.containsKey(view)) {
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                        View b2 = b(viewGroup.getChildAt(childCount), keyEvent);
                        if (b2 != null) {
                            return b2;
                        }
                    }
                }
                if (c(view, keyEvent)) {
                    return view;
                }
            }
            return null;
        }

        private void b() {
            WeakHashMap<View, Boolean> weakHashMap = this.f796b;
            if (weakHashMap != null) {
                weakHashMap.clear();
            }
            if (!f795a.isEmpty()) {
                synchronized (f795a) {
                    if (this.f796b == null) {
                        this.f796b = new WeakHashMap<>();
                    }
                    for (int size = f795a.size() - 1; size >= 0; size--) {
                        View view = (View) f795a.get(size).get();
                        if (view == null) {
                            f795a.remove(size);
                        } else {
                            this.f796b.put(view, Boolean.TRUE);
                            for (ViewParent parent = view.getParent(); parent instanceof View; parent = parent.getParent()) {
                                this.f796b.put((View) parent, Boolean.TRUE);
                            }
                        }
                    }
                }
            }
        }

        private boolean c(@NonNull View view, @NonNull KeyEvent keyEvent) {
            ArrayList arrayList = (ArrayList) view.getTag(a.d.b.tag_unhandled_key_listeners);
            if (arrayList == null) {
                return false;
            }
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (((c) arrayList.get(size)).onUnhandledKeyEvent(view, keyEvent)) {
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean a(KeyEvent keyEvent) {
            int indexOfKey;
            WeakReference<KeyEvent> weakReference = this.f798d;
            if (weakReference != null && weakReference.get() == keyEvent) {
                return false;
            }
            this.f798d = new WeakReference<>(keyEvent);
            WeakReference weakReference2 = null;
            SparseArray<WeakReference<View>> a2 = a();
            if (keyEvent.getAction() == 1 && (indexOfKey = a2.indexOfKey(keyEvent.getKeyCode())) >= 0) {
                weakReference2 = a2.valueAt(indexOfKey);
                a2.removeAt(indexOfKey);
            }
            if (weakReference2 == null) {
                weakReference2 = a2.get(keyEvent.getKeyCode());
            }
            if (weakReference2 == null) {
                return false;
            }
            View view = (View) weakReference2.get();
            if (view != null && ViewCompat.r(view)) {
                c(view, keyEvent);
            }
            return true;
        }

        /* access modifiers changed from: package-private */
        public boolean a(View view, KeyEvent keyEvent) {
            if (keyEvent.getAction() == 0) {
                b();
            }
            View b2 = b(view, keyEvent);
            if (keyEvent.getAction() == 0) {
                int keyCode = keyEvent.getKeyCode();
                if (b2 != null && !KeyEvent.isModifierKey(keyCode)) {
                    a().put(keyCode, new WeakReference(b2));
                }
            }
            return b2 != null;
        }
    }

    @NonNull
    public static D a(@NonNull View view) {
        if (g == null) {
            g = new WeakHashMap<>();
        }
        D d2 = g.get(view);
        if (d2 != null) {
            return d2;
        }
        D d3 = new D(view);
        g.put(view, d3);
        return d3;
    }

    public static H a(@NonNull View view, H h2) {
        if (Build.VERSION.SDK_INT < 21) {
            return h2;
        }
        WindowInsets f2 = h2.f();
        WindowInsets dispatchApplyWindowInsets = view.dispatchApplyWindowInsets(f2);
        if (!dispatchApplyWindowInsets.equals(f2)) {
            f2 = new WindowInsets(dispatchApplyWindowInsets);
        }
        return H.a(f2);
    }

    private static b<Boolean> a() {
        return new x(a.d.b.tag_accessibility_heading, Boolean.class, 28);
    }

    public static void a(@NonNull View view, float f2) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.setElevation(f2);
        }
    }

    @RequiresApi(19)
    static void a(View view, int i2) {
        if (((AccessibilityManager) view.getContext().getSystemService("accessibility")).isEnabled()) {
            boolean z = d(view) != null;
            if (c(view) != 0 || (z && view.getVisibility() == 0)) {
                AccessibilityEvent obtain = AccessibilityEvent.obtain();
                obtain.setEventType(z ? 32 : 2048);
                obtain.setContentChangeTypes(i2);
                view.sendAccessibilityEventUnchecked(obtain);
            } else if (view.getParent() != null) {
                try {
                    view.getParent().notifySubtreeAccessibilityStateChanged(view, view, i2);
                } catch (AbstractMethodError e2) {
                    Log.e("ViewCompat", view.getParent().getClass().getSimpleName() + " does not fully implement ViewParent", e2);
                }
            }
        }
    }

    public static void a(@NonNull View view, int i2, int i3) {
        if (Build.VERSION.SDK_INT >= 23) {
            view.setScrollIndicators(i2, i3);
        }
    }

    public static void a(@NonNull View view, ColorStateList colorStateList) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.setBackgroundTintList(colorStateList);
            if (Build.VERSION.SDK_INT == 21) {
                Drawable background = view.getBackground();
                boolean z = (view.getBackgroundTintList() == null && view.getBackgroundTintMode() == null) ? false : true;
                if (background != null && z) {
                    if (background.isStateful()) {
                        background.setState(view.getDrawableState());
                    }
                    view.setBackground(background);
                }
            }
        } else if (view instanceof t) {
            ((t) view).setSupportBackgroundTintList(colorStateList);
        }
    }

    public static void a(@NonNull View view, PorterDuff.Mode mode) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.setBackgroundTintMode(mode);
            if (Build.VERSION.SDK_INT == 21) {
                Drawable background = view.getBackground();
                boolean z = (view.getBackgroundTintList() == null && view.getBackgroundTintMode() == null) ? false : true;
                if (background != null && z) {
                    if (background.isStateful()) {
                        background.setState(view.getDrawableState());
                    }
                    view.setBackground(background);
                }
            }
        } else if (view instanceof t) {
            ((t) view).setSupportBackgroundTintMode(mode);
        }
    }

    public static void a(@NonNull View view, @Nullable Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static void a(@NonNull View view, androidx.core.view.a.c cVar) {
        view.onInitializeAccessibilityNodeInfo(cVar.w());
    }

    public static void a(@NonNull View view, C0123a aVar) {
        if (aVar == null && (x(view) instanceof C0123a.C0012a)) {
            aVar = new C0123a();
        }
        view.setAccessibilityDelegate(aVar == null ? null : aVar.getBridge());
    }

    public static void a(@NonNull View view, q qVar) {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }
        if (qVar == null) {
            view.setOnApplyWindowInsetsListener((View.OnApplyWindowInsetsListener) null);
        } else {
            view.setOnApplyWindowInsetsListener(new u(qVar));
        }
    }

    public static void a(@NonNull View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postOnAnimation(runnable);
        } else {
            view.postDelayed(runnable, ValueAnimator.getFrameDelay());
        }
    }

    public static void a(@NonNull View view, Runnable runnable, long j2) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postOnAnimationDelayed(runnable, j2);
        } else {
            view.postDelayed(runnable, ValueAnimator.getFrameDelay() + j2);
        }
    }

    public static void a(@NonNull View view, String str) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.setTransitionName(str);
            return;
        }
        if (f == null) {
            f = new WeakHashMap<>();
        }
        f.put(view, str);
    }

    public static boolean a(@NonNull View view, int i2, Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 16) {
            return view.performAccessibilityAction(i2, bundle);
        }
        return false;
    }

    @UiThread
    static boolean a(View view, KeyEvent keyEvent) {
        if (Build.VERSION.SDK_INT >= 28) {
            return false;
        }
        return d.a(view).a(view, keyEvent);
    }

    public static H b(@NonNull View view, H h2) {
        if (Build.VERSION.SDK_INT < 21) {
            return h2;
        }
        WindowInsets f2 = h2.f();
        WindowInsets onApplyWindowInsets = view.onApplyWindowInsets(f2);
        if (!onApplyWindowInsets.equals(f2)) {
            f2 = new WindowInsets(onApplyWindowInsets);
        }
        return H.a(f2);
    }

    private static b<CharSequence> b() {
        return new w(a.d.b.tag_accessibility_pane_title, CharSequence.class, 8, 28);
    }

    @Nullable
    public static C0123a b(@NonNull View view) {
        View.AccessibilityDelegate x = x(view);
        if (x == null) {
            return null;
        }
        return x instanceof C0123a.C0012a ? ((C0123a.C0012a) x).f799a : new C0123a(x);
    }

    public static void b(@NonNull View view, int i2) {
        int i3 = Build.VERSION.SDK_INT;
        if (i3 < 19) {
            if (i3 < 16) {
                return;
            }
            if (i2 == 4) {
                i2 = 2;
            }
        }
        view.setImportantForAccessibility(i2);
    }

    @UiThread
    static boolean b(View view, KeyEvent keyEvent) {
        if (Build.VERSION.SDK_INT >= 28) {
            return false;
        }
        return d.a(view).a(keyEvent);
    }

    public static int c(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            return view.getAccessibilityLiveRegion();
        }
        return 0;
    }

    private static b<Boolean> c() {
        return new v(a.d.b.tag_screen_reader_focusable, Boolean.class, 28);
    }

    public static void c(@NonNull View view, int i2) {
        if (Build.VERSION.SDK_INT >= 26) {
            view.setImportantForAutofill(i2);
        }
    }

    @UiThread
    public static CharSequence d(View view) {
        return b().b(view);
    }

    public static ColorStateList e(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getBackgroundTintList();
        }
        if (view instanceof t) {
            return ((t) view).getSupportBackgroundTintList();
        }
        return null;
    }

    public static PorterDuff.Mode f(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getBackgroundTintMode();
        }
        if (view instanceof t) {
            return ((t) view).getSupportBackgroundTintMode();
        }
        return null;
    }

    @Nullable
    public static Display g(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 17) {
            return view.getDisplay();
        }
        if (r(view)) {
            return ((WindowManager) view.getContext().getSystemService("window")).getDefaultDisplay();
        }
        return null;
    }

    public static int h(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            return view.getImportantForAccessibility();
        }
        return 0;
    }

    @SuppressLint({"InlinedApi"})
    public static int i(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 26) {
            return view.getImportantForAutofill();
        }
        return 0;
    }

    public static int j(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 17) {
            return view.getLayoutDirection();
        }
        return 0;
    }

    public static int k(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            return view.getMinimumHeight();
        }
        if (!e) {
            try {
                f790d = View.class.getDeclaredField("mMinHeight");
                f790d.setAccessible(true);
            } catch (NoSuchFieldException unused) {
            }
            e = true;
        }
        Field field = f790d;
        if (field == null) {
            return 0;
        }
        try {
            return ((Integer) field.get(view)).intValue();
        } catch (Exception unused2) {
            return 0;
        }
    }

    public static int l(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            return view.getMinimumWidth();
        }
        if (!f789c) {
            try {
                f788b = View.class.getDeclaredField("mMinWidth");
                f788b.setAccessible(true);
            } catch (NoSuchFieldException unused) {
            }
            f789c = true;
        }
        Field field = f788b;
        if (field == null) {
            return 0;
        }
        try {
            return ((Integer) field.get(view)).intValue();
        } catch (Exception unused2) {
            return 0;
        }
    }

    @Nullable
    public static String m(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getTransitionName();
        }
        WeakHashMap<View, String> weakHashMap = f;
        if (weakHashMap == null) {
            return null;
        }
        return weakHashMap.get(view);
    }

    public static int n(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            return view.getWindowSystemUiVisibility();
        }
        return 0;
    }

    public static boolean o(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 15) {
            return view.hasOnClickListeners();
        }
        return false;
    }

    public static boolean p(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            return view.hasTransientState();
        }
        return false;
    }

    @UiThread
    public static boolean q(View view) {
        Boolean b2 = a().b(view);
        if (b2 == null) {
            return false;
        }
        return b2.booleanValue();
    }

    public static boolean r(@NonNull View view) {
        return Build.VERSION.SDK_INT >= 19 ? view.isAttachedToWindow() : view.getWindowToken() != null;
    }

    public static boolean s(@NonNull View view) {
        return Build.VERSION.SDK_INT >= 19 ? view.isLaidOut() : view.getWidth() > 0 && view.getHeight() > 0;
    }

    @UiThread
    public static boolean t(View view) {
        Boolean b2 = c().b(view);
        if (b2 == null) {
            return false;
        }
        return b2.booleanValue();
    }

    public static void u(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postInvalidateOnAnimation();
        } else {
            view.postInvalidate();
        }
    }

    public static void v(@NonNull View view) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 20) {
            view.requestApplyInsets();
        } else if (i2 >= 16) {
            view.requestFitSystemWindows();
        }
    }

    public static void w(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.stopNestedScroll();
        } else if (view instanceof k) {
            ((k) view).stopNestedScroll();
        }
    }

    @Nullable
    private static View.AccessibilityDelegate x(@NonNull View view) {
        return Build.VERSION.SDK_INT >= 29 ? view.getAccessibilityDelegate() : y(view);
    }

    @Nullable
    private static View.AccessibilityDelegate y(@NonNull View view) {
        if (i) {
            return null;
        }
        if (h == null) {
            try {
                h = View.class.getDeclaredField("mAccessibilityDelegate");
                h.setAccessible(true);
            } catch (Throwable unused) {
                i = true;
                return null;
            }
        }
        try {
            Object obj = h.get(view);
            if (obj instanceof View.AccessibilityDelegate) {
                return (View.AccessibilityDelegate) obj;
            }
            return null;
        } catch (Throwable unused2) {
            i = true;
            return null;
        }
    }
}
