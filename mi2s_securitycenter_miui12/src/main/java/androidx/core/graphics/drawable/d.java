package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

class d extends Drawable implements Drawable.Callback, c, b {

    /* renamed from: a  reason: collision with root package name */
    static final PorterDuff.Mode f728a = PorterDuff.Mode.SRC_IN;

    /* renamed from: b  reason: collision with root package name */
    private int f729b;

    /* renamed from: c  reason: collision with root package name */
    private PorterDuff.Mode f730c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f731d;
    f e;
    private boolean f;
    Drawable g;

    d(@Nullable Drawable drawable) {
        this.e = b();
        setWrappedDrawable(drawable);
    }

    d(@NonNull f fVar, @Nullable Resources resources) {
        this.e = fVar;
        a(resources);
    }

    private void a(@Nullable Resources resources) {
        Drawable.ConstantState constantState;
        f fVar = this.e;
        if (fVar != null && (constantState = fVar.f733b) != null) {
            setWrappedDrawable(constantState.newDrawable(resources));
        }
    }

    private boolean a(int[] iArr) {
        if (!a()) {
            return false;
        }
        f fVar = this.e;
        ColorStateList colorStateList = fVar.f734c;
        PorterDuff.Mode mode = fVar.f735d;
        if (colorStateList == null || mode == null) {
            this.f731d = false;
            clearColorFilter();
        } else {
            int colorForState = colorStateList.getColorForState(iArr, colorStateList.getDefaultColor());
            if (!(this.f731d && colorForState == this.f729b && mode == this.f730c)) {
                setColorFilter(colorForState, mode);
                this.f729b = colorForState;
                this.f730c = mode;
                this.f731d = true;
                return true;
            }
        }
        return false;
    }

    @NonNull
    private f b() {
        return new f(this.e);
    }

    /* access modifiers changed from: protected */
    public boolean a() {
        return true;
    }

    public void draw(@NonNull Canvas canvas) {
        this.g.draw(canvas);
    }

    public int getChangingConfigurations() {
        int changingConfigurations = super.getChangingConfigurations();
        f fVar = this.e;
        return changingConfigurations | (fVar != null ? fVar.getChangingConfigurations() : 0) | this.g.getChangingConfigurations();
    }

    @Nullable
    public Drawable.ConstantState getConstantState() {
        f fVar = this.e;
        if (fVar == null || !fVar.a()) {
            return null;
        }
        this.e.f732a = getChangingConfigurations();
        return this.e;
    }

    @NonNull
    public Drawable getCurrent() {
        return this.g.getCurrent();
    }

    public int getIntrinsicHeight() {
        return this.g.getIntrinsicHeight();
    }

    public int getIntrinsicWidth() {
        return this.g.getIntrinsicWidth();
    }

    public int getMinimumHeight() {
        return this.g.getMinimumHeight();
    }

    public int getMinimumWidth() {
        return this.g.getMinimumWidth();
    }

    public int getOpacity() {
        return this.g.getOpacity();
    }

    public boolean getPadding(@NonNull Rect rect) {
        return this.g.getPadding(rect);
    }

    @NonNull
    public int[] getState() {
        return this.g.getState();
    }

    public Region getTransparentRegion() {
        return this.g.getTransparentRegion();
    }

    public final Drawable getWrappedDrawable() {
        return this.g;
    }

    public void invalidateDrawable(@NonNull Drawable drawable) {
        invalidateSelf();
    }

    @RequiresApi(19)
    public boolean isAutoMirrored() {
        return this.g.isAutoMirrored();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r1.e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isStateful() {
        /*
            r1 = this;
            boolean r0 = r1.a()
            if (r0 == 0) goto L_0x000d
            androidx.core.graphics.drawable.f r0 = r1.e
            if (r0 == 0) goto L_0x000d
            android.content.res.ColorStateList r0 = r0.f734c
            goto L_0x000e
        L_0x000d:
            r0 = 0
        L_0x000e:
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isStateful()
            if (r0 != 0) goto L_0x001e
        L_0x0016:
            android.graphics.drawable.Drawable r0 = r1.g
            boolean r0 = r0.isStateful()
            if (r0 == 0) goto L_0x0020
        L_0x001e:
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.graphics.drawable.d.isStateful():boolean");
    }

    public void jumpToCurrentState() {
        this.g.jumpToCurrentState();
    }

    @NonNull
    public Drawable mutate() {
        if (!this.f && super.mutate() == this) {
            this.e = b();
            Drawable drawable = this.g;
            if (drawable != null) {
                drawable.mutate();
            }
            f fVar = this.e;
            if (fVar != null) {
                Drawable drawable2 = this.g;
                fVar.f733b = drawable2 != null ? drawable2.getConstantState() : null;
            }
            this.f = true;
        }
        return this;
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        Drawable drawable = this.g;
        if (drawable != null) {
            drawable.setBounds(rect);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int i) {
        return this.g.setLevel(i);
    }

    public void scheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    public void setAlpha(int i) {
        this.g.setAlpha(i);
    }

    @RequiresApi(19)
    public void setAutoMirrored(boolean z) {
        this.g.setAutoMirrored(z);
    }

    public void setChangingConfigurations(int i) {
        this.g.setChangingConfigurations(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.g.setColorFilter(colorFilter);
    }

    public void setDither(boolean z) {
        this.g.setDither(z);
    }

    public void setFilterBitmap(boolean z) {
        this.g.setFilterBitmap(z);
    }

    public boolean setState(@NonNull int[] iArr) {
        return a(iArr) || this.g.setState(iArr);
    }

    public void setTint(int i) {
        setTintList(ColorStateList.valueOf(i));
    }

    public void setTintList(ColorStateList colorStateList) {
        this.e.f734c = colorStateList;
        a(getState());
    }

    public void setTintMode(@NonNull PorterDuff.Mode mode) {
        this.e.f735d = mode;
        a(getState());
    }

    public boolean setVisible(boolean z, boolean z2) {
        return super.setVisible(z, z2) || this.g.setVisible(z, z2);
    }

    public final void setWrappedDrawable(Drawable drawable) {
        Drawable drawable2 = this.g;
        if (drawable2 != null) {
            drawable2.setCallback((Drawable.Callback) null);
        }
        this.g = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            setVisible(drawable.isVisible(), true);
            setState(drawable.getState());
            setLevel(drawable.getLevel());
            setBounds(drawable.getBounds());
            f fVar = this.e;
            if (fVar != null) {
                fVar.f733b = drawable.getConstantState();
            }
        }
        invalidateSelf();
    }

    public void unscheduleDrawable(@NonNull Drawable drawable, @NonNull Runnable runnable) {
        unscheduleSelf(runnable);
    }
}
