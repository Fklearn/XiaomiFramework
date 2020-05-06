package a.j.a.a;

import android.graphics.drawable.Drawable;

class c implements Drawable.Callback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f163a;

    c(d dVar) {
        this.f163a = dVar;
    }

    public void invalidateDrawable(Drawable drawable) {
        this.f163a.invalidateSelf();
    }

    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        this.f163a.scheduleSelf(runnable, j);
    }

    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        this.f163a.unscheduleSelf(runnable);
    }
}
