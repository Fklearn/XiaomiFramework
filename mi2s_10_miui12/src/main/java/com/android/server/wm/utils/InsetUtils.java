package com.android.server.wm.utils;

import android.graphics.Rect;

public class InsetUtils {
    private InsetUtils() {
    }

    public static void rotateInsets(Rect inOutInsets, int rotationDelta) {
        Rect r = inOutInsets;
        if (rotationDelta == 0) {
            return;
        }
        if (rotationDelta == 1) {
            r.set(r.top, r.right, r.bottom, r.left);
        } else if (rotationDelta == 2) {
            r.set(r.right, r.bottom, r.left, r.top);
        } else if (rotationDelta == 3) {
            r.set(r.bottom, r.left, r.top, r.right);
        } else {
            throw new IllegalArgumentException("Unknown rotation: " + rotationDelta);
        }
    }

    public static void addInsets(Rect inOutInsets, Rect insetsToAdd) {
        inOutInsets.left += insetsToAdd.left;
        inOutInsets.top += insetsToAdd.top;
        inOutInsets.right += insetsToAdd.right;
        inOutInsets.bottom += insetsToAdd.bottom;
    }

    public static void insetsBetweenFrames(Rect outerFrame, Rect innerFrame, Rect outInsets) {
        if (innerFrame == null) {
            outInsets.setEmpty();
            return;
        }
        int w = outerFrame.width();
        int h = outerFrame.height();
        outInsets.set(Math.min(w, Math.max(0, innerFrame.left - outerFrame.left)), Math.min(h, Math.max(0, innerFrame.top - outerFrame.top)), Math.min(w, Math.max(0, outerFrame.right - innerFrame.right)), Math.min(h, Math.max(0, outerFrame.bottom - innerFrame.bottom)));
    }
}
