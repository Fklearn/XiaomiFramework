package miuix.recyclerview.widget;

import a.i.a;
import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.Q;

public class RecyclerView extends Q {
    public RecyclerView(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, a.recyclerViewStyle);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setItemAnimator(new j());
    }

    public boolean e(int i, int i2) {
        if (Math.abs(i) < 300) {
            i = 0;
        }
        if (Math.abs(i2) < 300) {
            i2 = 0;
        }
        if (i == 0 && i2 == 0) {
            return false;
        }
        return super.e(i, i2);
    }

    public boolean getSpringEnabled() {
        return super.getSpringEnabled();
    }

    public void setSpringEnabled(boolean z) {
        super.setSpringEnabled(z);
    }
}
