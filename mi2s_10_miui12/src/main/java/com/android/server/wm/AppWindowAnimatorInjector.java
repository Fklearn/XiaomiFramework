package com.android.server.wm;

import android.view.animation.Animation;

class AppWindowAnimatorInjector {
    AppWindowAnimatorInjector() {
    }

    static void scaleCurrentDuration(Animation anim, WindowManagerService service) {
        int appTransition = service.getDefaultDisplayContentLocked().mAppTransition.getAppTransition();
        if (appTransition == 6 || appTransition == 7) {
            anim.scaleCurrentDuration(service.getTransitionAnimationScaleLocked() * 0.85f);
        } else {
            anim.scaleCurrentDuration(service.getTransitionAnimationScaleLocked());
        }
    }
}
