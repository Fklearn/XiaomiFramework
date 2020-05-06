package androidx.appcompat.widget;

import android.graphics.Rect;
import androidx.annotation.RestrictTo;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public interface P {

    public interface a {
        void a(Rect rect);
    }

    void setOnFitSystemWindowsListener(a aVar);
}
