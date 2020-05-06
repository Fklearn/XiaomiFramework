package androidx.core.view;

import android.view.MotionEvent;

/* renamed from: androidx.core.view.h  reason: case insensitive filesystem */
public final class C0130h {
    public static boolean a(MotionEvent motionEvent, int i) {
        return (motionEvent.getSource() & i) == i;
    }
}
