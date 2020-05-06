package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class f extends Drawable.ConstantState {

    /* renamed from: a  reason: collision with root package name */
    int f732a;

    /* renamed from: b  reason: collision with root package name */
    Drawable.ConstantState f733b;

    /* renamed from: c  reason: collision with root package name */
    ColorStateList f734c = null;

    /* renamed from: d  reason: collision with root package name */
    PorterDuff.Mode f735d = d.f728a;

    f(@Nullable f fVar) {
        if (fVar != null) {
            this.f732a = fVar.f732a;
            this.f733b = fVar.f733b;
            this.f734c = fVar.f734c;
            this.f735d = fVar.f735d;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean a() {
        return this.f733b != null;
    }

    public int getChangingConfigurations() {
        int i = this.f732a;
        Drawable.ConstantState constantState = this.f733b;
        return i | (constantState != null ? constantState.getChangingConfigurations() : 0);
    }

    @NonNull
    public Drawable newDrawable() {
        return newDrawable((Resources) null);
    }

    @NonNull
    public Drawable newDrawable(@Nullable Resources resources) {
        return Build.VERSION.SDK_INT >= 21 ? new e(this, resources) : new d(this, resources);
    }
}
