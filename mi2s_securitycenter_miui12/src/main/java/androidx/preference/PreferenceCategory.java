package androidx.preference;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import androidx.core.content.a;
import androidx.core.content.res.h;
import androidx.core.view.a.c;

public class PreferenceCategory extends PreferenceGroup {
    public PreferenceCategory(Context context) {
        this(context, (AttributeSet) null);
    }

    public PreferenceCategory(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, h.a(context, B.preferenceCategoryStyle, 16842892));
    }

    public PreferenceCategory(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public boolean isEnabled() {
        return false;
    }

    public void onBindViewHolder(A a2) {
        TextView textView;
        super.onBindViewHolder(a2);
        int i = Build.VERSION.SDK_INT;
        if (i >= 28) {
            a2.itemView.setAccessibilityHeading(true);
        } else if (i < 21) {
            TypedValue typedValue = new TypedValue();
            if (getContext().getTheme().resolveAttribute(B.colorAccent, typedValue, true) && (textView = (TextView) a2.b(16908310)) != null && textView.getCurrentTextColor() == a.a(getContext(), C.preference_fallback_accent_color)) {
                textView.setTextColor(typedValue.data);
            }
        }
    }

    @Deprecated
    public void onInitializeAccessibilityNodeInfo(c cVar) {
        c.C0013c e;
        super.onInitializeAccessibilityNodeInfo(cVar);
        if (Build.VERSION.SDK_INT < 28 && (e = cVar.e()) != null) {
            cVar.b((Object) c.C0013c.a(e.c(), e.d(), e.a(), e.b(), true, e.e()));
        }
    }

    public boolean shouldDisableDependents() {
        return !super.isEnabled();
    }
}
