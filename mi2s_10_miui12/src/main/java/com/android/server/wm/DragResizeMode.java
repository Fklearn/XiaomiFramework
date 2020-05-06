package com.android.server.wm;

class DragResizeMode {
    static final int DRAG_RESIZE_MODE_DOCKED_DIVIDER = 1;
    static final int DRAG_RESIZE_MODE_FREEFORM = 0;

    DragResizeMode() {
    }

    static boolean isModeAllowedForStack(TaskStack stack, int mode) {
        if (mode != 0) {
            if (mode != 1) {
                return false;
            }
            return stack.inSplitScreenWindowingMode();
        } else if (stack.getWindowingMode() == 5) {
            return true;
        } else {
            return false;
        }
    }
}
