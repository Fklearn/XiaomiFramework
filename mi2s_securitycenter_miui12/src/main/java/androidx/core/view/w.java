package androidx.core.view;

import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

class w extends ViewCompat.b<CharSequence> {
    w(int i, Class cls, int i2, int i3) {
        super(i, cls, i2, i3);
    }

    /* access modifiers changed from: package-private */
    @RequiresApi(28)
    public CharSequence a(View view) {
        return view.getAccessibilityPaneTitle();
    }
}
