package androidx.core.graphics.drawable;

import android.graphics.drawable.Drawable;
import androidx.annotation.RestrictTo;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public interface c {
    Drawable getWrappedDrawable();

    void setWrappedDrawable(Drawable drawable);
}
