package miuix.springback.view;

import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private int f8943a;

    /* renamed from: b  reason: collision with root package name */
    float f8944b;

    /* renamed from: c  reason: collision with root package name */
    float f8945c;

    /* renamed from: d  reason: collision with root package name */
    int f8946d = -1;
    int e;
    int f;
    private ViewGroup g;

    public a(ViewGroup viewGroup, int i) {
        this.g = viewGroup;
        this.f = i;
        this.f8943a = ViewConfiguration.get(viewGroup.getContext()).getScaledTouchSlop();
    }

    /* access modifiers changed from: package-private */
    public void a(MotionEvent motionEvent) {
        int findPointerIndex;
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked != 0) {
            int i = 1;
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    int i2 = this.f8946d;
                    if (i2 != -1 && (findPointerIndex = motionEvent.findPointerIndex(i2)) >= 0) {
                        float y = motionEvent.getY(findPointerIndex);
                        float x = motionEvent.getX(findPointerIndex);
                        float f2 = y - this.f8944b;
                        float f3 = x - this.f8945c;
                        if (Math.abs(f3) > ((float) this.f8943a) || Math.abs(f2) > ((float) this.f8943a)) {
                            if (Math.abs(f3) <= Math.abs(f2)) {
                                i = 2;
                            }
                            this.e = i;
                            return;
                        }
                        return;
                    }
                    return;
                } else if (actionMasked != 3) {
                    return;
                }
            }
            this.e = 0;
            this.g.requestDisallowInterceptTouchEvent(false);
            return;
        }
        this.f8946d = motionEvent.getPointerId(0);
        int findPointerIndex2 = motionEvent.findPointerIndex(this.f8946d);
        if (findPointerIndex2 >= 0) {
            this.f8944b = motionEvent.getY(findPointerIndex2);
            this.f8945c = motionEvent.getX(findPointerIndex2);
            this.e = 0;
        }
    }
}
