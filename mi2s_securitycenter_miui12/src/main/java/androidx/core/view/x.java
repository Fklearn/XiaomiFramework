package androidx.core.view;

import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

class x extends ViewCompat.b<Boolean> {
    x(int i, Class cls, int i2) {
        super(i, cls, i2);
    }

    /* access modifiers changed from: package-private */
    @RequiresApi(28)
    public Boolean a(View view) {
        return Boolean.valueOf(view.isAccessibilityHeading());
    }
}
