package d.a.b;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import java.util.Map;
import java.util.WeakHashMap;

public class m implements View.OnTouchListener {

    /* renamed from: a  reason: collision with root package name */
    private WeakHashMap<View, View.OnTouchListener> f8678a = new WeakHashMap<>();

    /* renamed from: b  reason: collision with root package name */
    private int[] f8679b = new int[2];

    /* renamed from: c  reason: collision with root package name */
    private float f8680c = Float.MAX_VALUE;

    /* renamed from: d  reason: collision with root package name */
    private float f8681d = Float.MAX_VALUE;
    private int e;

    m(AbsListView absListView) {
        this.e = ViewConfiguration.get(absListView.getContext()).getScaledTouchSlop();
    }

    private void a(MotionEvent motionEvent, boolean z) {
        for (Map.Entry next : this.f8678a.entrySet()) {
            View view = (View) next.getKey();
            ((View.OnTouchListener) next.getValue()).onTouch(view, !z && j.a(view, this.f8679b, motionEvent) ? motionEvent : null);
        }
    }

    public void a(View view, View.OnTouchListener onTouchListener) {
        this.f8678a.put(view, onTouchListener);
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean z;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.f8680c = motionEvent.getRawX();
            this.f8681d = motionEvent.getRawY();
        } else if (actionMasked != 2) {
            this.f8681d = Float.MAX_VALUE;
            this.f8680c = Float.MAX_VALUE;
        } else if (motionEvent.getRawY() - this.f8681d > ((float) this.e) || motionEvent.getRawX() - this.f8680c > ((float) this.e)) {
            z = true;
            a(motionEvent, z);
            return false;
        }
        z = false;
        a(motionEvent, z);
        return false;
    }
}
