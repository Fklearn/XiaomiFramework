package androidx.core.view;

import android.view.View;
import android.view.ViewTreeObserver;
import androidx.annotation.NonNull;

public final class r implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {

    /* renamed from: a  reason: collision with root package name */
    private final View f831a;

    /* renamed from: b  reason: collision with root package name */
    private ViewTreeObserver f832b;

    /* renamed from: c  reason: collision with root package name */
    private final Runnable f833c;

    private r(View view, Runnable runnable) {
        this.f831a = view;
        this.f832b = view.getViewTreeObserver();
        this.f833c = runnable;
    }

    @NonNull
    public static r a(@NonNull View view, @NonNull Runnable runnable) {
        if (view == null) {
            throw new NullPointerException("view == null");
        } else if (runnable != null) {
            r rVar = new r(view, runnable);
            view.getViewTreeObserver().addOnPreDrawListener(rVar);
            view.addOnAttachStateChangeListener(rVar);
            return rVar;
        } else {
            throw new NullPointerException("runnable == null");
        }
    }

    public void a() {
        (this.f832b.isAlive() ? this.f832b : this.f831a.getViewTreeObserver()).removeOnPreDrawListener(this);
        this.f831a.removeOnAttachStateChangeListener(this);
    }

    public boolean onPreDraw() {
        a();
        this.f833c.run();
        return true;
    }

    public void onViewAttachedToWindow(View view) {
        this.f832b = view.getViewTreeObserver();
    }

    public void onViewDetachedFromWindow(View view) {
        a();
    }
}
