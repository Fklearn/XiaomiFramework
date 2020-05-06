package com.android.server.wm;

interface ISurfaceRunner {
    void cancelAnimation();

    void startAnimation(IGestureStrategy iGestureStrategy);
}
