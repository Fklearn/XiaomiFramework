package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.RestrictTo;
import androidx.core.content.res.h;
import androidx.preference.z;

public final class PreferenceScreen extends PreferenceGroup {
    private boolean j = true;

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public PreferenceScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, h.a(context, B.preferenceScreenStyle, 16842891));
    }

    /* access modifiers changed from: protected */
    public boolean d() {
        return false;
    }

    public boolean g() {
        return this.j;
    }

    /* access modifiers changed from: protected */
    public void onClick() {
        z.b d2;
        if (getIntent() == null && getFragment() == null && c() != 0 && (d2 = getPreferenceManager().d()) != null) {
            d2.onNavigateToScreen(this);
        }
    }
}
