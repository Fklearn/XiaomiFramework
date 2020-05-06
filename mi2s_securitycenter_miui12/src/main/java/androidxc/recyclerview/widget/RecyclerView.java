package androidxc.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {

    public static abstract class a extends RecyclerView.u {
        public a(View view) {
            super(view);
        }
    }

    public RecyclerView(Context context) {
        super(context);
    }

    public RecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public a g(View view) {
        return (a) super.g(view);
    }
}
