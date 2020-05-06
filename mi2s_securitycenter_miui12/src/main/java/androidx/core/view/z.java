package androidx.core.view;

import a.d.b;
import android.os.Build;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

public final class z {
    public static boolean a(@NonNull ViewGroup viewGroup) {
        if (Build.VERSION.SDK_INT >= 21) {
            return viewGroup.isTransitionGroup();
        }
        Boolean bool = (Boolean) viewGroup.getTag(b.tag_transition_group);
        return ((bool == null || !bool.booleanValue()) && viewGroup.getBackground() == null && ViewCompat.m(viewGroup) == null) ? false : true;
    }
}
