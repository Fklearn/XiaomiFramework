package miui.notification;

import android.view.MotionEvent;

public interface Gefingerpoken {
    boolean onInterceptTouchEvent(MotionEvent motionEvent);

    boolean onTouchEvent(MotionEvent motionEvent);
}
