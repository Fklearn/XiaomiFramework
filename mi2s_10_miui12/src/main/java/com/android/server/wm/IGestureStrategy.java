package com.android.server.wm;

import android.graphics.Rect;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

interface IGestureStrategy {
    public static final int TYPE_CANCEL = 2;
    public static final String TYPE_CANCEL_ANIMATION = "CANCEL";
    public static final int TYPE_HOME = 0;
    public static final String TYPE_HOME_ANIMATION = "HOME";
    public static final int TYPE_RECENTS = 1;
    public static final String TYPE_RECENTS_ANIMATION = "RECENTS";

    public static class WindowStateInfo {
        AppWindowToken mAppToken;
        boolean mHasShowStartingWindow;
        boolean mNeedClip;
        Rect mNowFrame = new Rect();
        int mNowPosX;
        int mNowPosY;
        float mNowScale;
        Rect mOriFrame = new Rect();
        int mOriPosX;
        int mOriPosY;
        int mTargetBottom;
        int mTargetPosX;
        int mTargetPosY;
        float mTargetScale;
    }

    boolean createAnimation(TreeSet<AppWindowToken> treeSet, ConcurrentHashMap<WindowState, WindowStateInfo> concurrentHashMap, Rect rect);

    void finishAnimation();

    int getAnimationType();

    boolean isAnimating();

    void onAnimationStart();

    boolean updateAnimation(long j);

    String getAnimationString() {
        int type = getAnimationType();
        if (type == 0) {
            return TYPE_HOME_ANIMATION;
        }
        if (type == 1) {
            return TYPE_RECENTS_ANIMATION;
        }
        if (type != 2) {
            return "UNKNOWN ANIMATION";
        }
        return TYPE_CANCEL_ANIMATION;
    }
}
