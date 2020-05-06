package com.android.server.wm;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.view.SurfaceControl;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.RadiusAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.view.animation.TranslateXAnimation;
import android.view.animation.TranslateYAnimation;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.lights.interpolater.PhysicBasedInterpolator;
import com.android.server.pm.PackageManagerService;
import com.miui.internal.transition.IMiuiAppTransitionAnimationHelper;
import com.miui.server.AccessController;
import java.util.ArrayList;
import miui.security.ISecurityManager;

public class AppTransitionInjector {
    static final int APP_TRANSITION_SPECS_PENDING_TIMEOUT = ("cactus".equals(Build.DEVICE) ? ScreenRotationAnimationInjector.COVER_EGE : 100);
    static final int BEZIER_ALLPAPER_OPEN_DURATION = 400;
    private static final ArrayList<String> BLACK_LIST_NOT_ALLOWED_SNAPSHOT = new ArrayList<>();
    private static final Interpolator CUBIC_EASE_OUT_INTERPOLATOR = (IS_E10 ? BerylliumConfig.FAST_OUT_SLOW_IN : new CubicEaseOutInterpolator());
    private static final int DEFAULT_ACTIVITY_SCALE_UP_DOWN_ALPHA_DURATION = 150;
    private static final int DEFAULT_ACTIVITY_SCALE_UP_DOWN_TRANSITION_DURATION = 350;
    private static final int DEFAULT_ACTIVITY_TRANSITION_DURATION = 500;
    private static final int DEFAULT_ALPHA_DURATION = (IS_E10 ? 88 : 210);
    private static final int DEFAULT_ALPHA_OFFSET = 40;
    private static final int DEFAULT_ANIMATION_DURATION;
    static final int DEFAULT_APP_TRANSITION_ROUND_CORNER_RADIUS = 60;
    private static final float DEFAULT_BACK_TO_SCREEN_CENTER_SCALE = 0.6f;
    private static final float DEFAULT_ENTER_ACTIVITY_END_ALPHA = 1.0f;
    private static final float DEFAULT_ENTER_ACTIVITY_START_ALPHA = 0.0f;
    private static final float DEFAULT_EXIT_ACTIVITY_END_ALPHA = 0.8f;
    private static final float DEFAULT_EXIT_ACTIVITY_START_ALPHA = 1.0f;
    private static final int DEFAULT_LAUNCH_FORM_HOME_DURATION = 300;
    private static final float DEFAULT_REENTER_ACTIVITY_END_ALPHA = 1.0f;
    private static final float DEFAULT_REENTER_ACTIVITY_START_ALPHA = 0.8f;
    private static final float DEFAULT_RETURN_ACTIVITY_END_ALPHA = 0.0f;
    private static final float DEFAULT_RETURN_ACTIVITY_START_ALPHA = 1.0f;
    private static final int DEFAULT_TASK_TRANSITION_DURATION = 650;
    private static final float DEFAULT_WALLPAPER_EXIT_SCALE_X = 0.4f;
    private static final float DEFAULT_WALLPAPER_EXIT_SCALE_Y = 0.4f;
    static final int DEFAULT_WALLPAPER_OPEN_DURATION = (IS_E10 ? 144 : 300);
    private static final int DEFAULT_WALLPAPER_TRANSITION_DURATION = 550;
    static int DISPLAY_ROUND_CORNER_RADIUS = 60;
    static final boolean IS_E10 = "beryllium".equals(Build.PRODUCT);
    private static final float LAUNCHER_DEFAULT_ALPHA = 1.0f;
    private static final float LAUNCHER_DEFAULT_SCALE = 1.0f;
    /* access modifiers changed from: private */
    public static final float LAUNCHER_TRANSITION_ALPHA;
    private static final float LAUNCHER_TRANSITION_SCALE = 0.8f;
    static final int NEXT_TRANSIT_TYPE_BACK_HOME = 102;
    static final int NEXT_TRANSIT_TYPE_LAUNCH_BACK_ROUNDED_VIEW = 104;
    static final int NEXT_TRANSIT_TYPE_LAUNCH_FROM_HOME = 101;
    static final int NEXT_TRANSIT_TYPE_LAUNCH_FROM_ROUNDED_VIEW = 103;
    static final int PENDING_EXECUTE_APP_TRANSITION_TIMEOUT = 100;
    private static final Interpolator PHYSIC_BASED_INTERPOLATOR = new PhysicBasedInterpolator(0.9f, 0.857f);
    private static final Interpolator PHYSIC_BASED_INTERPOLATOR_FOR_ALPHA = new PhysicBasedInterpolator(0.9f, 0.1f);
    private static final Interpolator QUART_EASE_OUT_INTERPOLATOR = new QuartEaseOutInterpolator();
    private static final Interpolator QUINT_EASE_OUT_INTERPOLATOR = new QuintEaseOutInterpolator();
    private static final String TAG = "AppTransitionInjector";
    static final int THUMBNAIL_ANIMATION_TIMEOUT_DURATION = 1000;
    private static final ArrayList<String> WHITE_LIST_ALLOW_CUSTOM_ANIMATION = new ArrayList<String>() {
        {
            add("com.android.quicksearchbox");
            add(AccessController.PACKAGE_SYSTEMUI);
            add("com.miui.newhome");
        }
    };
    static final ArrayList<String> WHITE_LIST_ALLOW_CUSTOM_APPLICATION_TRANSITION = new ArrayList<String>() {
        {
            add("com.android.incallui");
        }
    };
    private static Interpolator sActivityTransitionInterpolator = null;
    private static Rect sMiuiAnimSupportInset = new Rect();

    static {
        float f;
        int i = 144;
        if (!IS_E10) {
            i = 300;
        }
        DEFAULT_ANIMATION_DURATION = i;
        if (IS_E10) {
            f = 1.0f;
        } else {
            f = 0.0f;
        }
        LAUNCHER_TRANSITION_ALPHA = f;
        BLACK_LIST_NOT_ALLOWED_SNAPSHOT.add(AccessController.PACKAGE_CAMERA);
        BLACK_LIST_NOT_ALLOWED_SNAPSHOT.add("com.android.browser");
        BLACK_LIST_NOT_ALLOWED_SNAPSHOT.add(ActivityStackSupervisorInjector.MIUI_APP_LOCK_PACKAGE_NAME);
        BLACK_LIST_NOT_ALLOWED_SNAPSHOT.add("com.mi.globalbrowser");
    }

    static Animation createLaunchActivityFromRoundedViewAnimation(int transit, boolean enter, Rect appFrame, Rect positionRect, float radius) {
        Rect rect = positionRect;
        int appWidth = appFrame.width();
        int appHeight = appFrame.height();
        int startX = rect.left;
        int startY = rect.top;
        int startWidth = positionRect.width();
        int startHeight = positionRect.height();
        if (enter) {
            Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            Animation radiusAnimation = new RadiusAnimation(radius, (float) DISPLAY_ROUND_CORNER_RADIUS);
            float scaleX = ((float) startWidth) / ((float) appWidth);
            float scaleY = ((float) startHeight) / ((float) appHeight);
            ScaleAnimation scaleAnimation = new ScaleAnimation(scaleX, 1.0f, scaleY, 1.0f, ((float) startX) / (1.0f - scaleX), ((float) startY) / (1.0f - scaleY));
            scaleAnimation.initialize(appWidth, appHeight, appWidth, appHeight);
            AnimationSet set = new AnimationSet(false);
            set.addAnimation(alphaAnimation);
            set.addAnimation(radiusAnimation);
            set.addAnimation(scaleAnimation);
            alphaAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR_FOR_ALPHA);
            radiusAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
            scaleAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
            set.setZAdjustment(1);
            int i = appWidth;
            alphaAnimation.setDuration(150);
            radiusAnimation.setDuration(350);
            scaleAnimation.setDuration(350);
            return set;
        }
        float f = radius;
        int i2 = appWidth;
        AnimationSet set2 = new AnimationSet(false);
        Animation alphaAnimation2 = new AlphaAnimation(1.0f, 0.8f);
        set2.addAnimation(alphaAnimation2);
        alphaAnimation2.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
        set2.setZAdjustment(-1);
        alphaAnimation2.setDuration(350);
        set2.setFillAfter(true);
        return set2;
    }

    static Animation createBackActivityFromRoundedViewAnimation(boolean enter, Rect appFrame, Rect targetPositionRect, float radius) {
        Rect positionRect = new Rect(targetPositionRect);
        if (enter) {
            AnimationSet set = new AnimationSet(false);
            Animation alphaAnimation = new AlphaAnimation(0.8f, 1.0f);
            set.addAnimation(alphaAnimation);
            alphaAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
            set.setZAdjustment(-1);
            alphaAnimation.setDuration(350);
            return set;
        }
        Animation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
        Animation radiusAnimation = new RadiusAnimation((float) DISPLAY_ROUND_CORNER_RADIUS, radius);
        float scaleX = ((float) positionRect.width()) / ((float) appFrame.width());
        Animation scaleXAnimation = new ScaleXAnimation(1.0f, scaleX, ((float) positionRect.left) / (1.0f - scaleX));
        float scaleY = ((float) positionRect.height()) / ((float) appFrame.height());
        Animation scaleYAnimation = new ScaleYAnimation(1.0f, scaleY, ((float) positionRect.top) / (1.0f - scaleY));
        AnimationSet set2 = new AnimationSet(true);
        set2.addAnimation(alphaAnimation2);
        set2.addAnimation(radiusAnimation);
        set2.addAnimation(scaleXAnimation);
        set2.addAnimation(scaleYAnimation);
        alphaAnimation2.setInterpolator(PHYSIC_BASED_INTERPOLATOR_FOR_ALPHA);
        radiusAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
        scaleXAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
        scaleYAnimation.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
        set2.setZAdjustment(1);
        alphaAnimation2.setDuration(150);
        radiusAnimation.setDuration(350);
        scaleXAnimation.setDuration(350);
        scaleYAnimation.setDuration(350);
        return set2;
    }

    static Animation createBackActivityScaledToScreenCenter(boolean enter, Rect appFrame) {
        Rect positionRect = new Rect();
        int scaleWith = (int) (((float) appFrame.width()) * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE);
        int scaleHeight = (int) (((float) appFrame.height()) * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE);
        positionRect.left = appFrame.left + ((appFrame.width() - scaleWith) / 2);
        positionRect.top = appFrame.top + ((appFrame.height() - scaleHeight) / 2);
        positionRect.right = appFrame.right - ((appFrame.width() - scaleWith) / 2);
        positionRect.bottom = appFrame.bottom - ((appFrame.height() - scaleHeight) / 2);
        if (enter) {
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(new AlphaAnimation(0.8f, 1.0f));
            set.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
            set.setZAdjustment(-1);
            set.setDuration(350);
            return set;
        }
        Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        float scaleX = ((float) positionRect.width()) / ((float) appFrame.width());
        Animation scaleXAnimation = new ScaleXAnimation(1.0f, scaleX, ((float) positionRect.left) / (1.0f - scaleX));
        float scaleY = ((float) positionRect.height()) / ((float) appFrame.height());
        Animation scaleYAnimation = new ScaleYAnimation(1.0f, scaleY, ((float) positionRect.top) / (1.0f - scaleY));
        AnimationSet set2 = new AnimationSet(true);
        set2.addAnimation(alphaAnimation);
        set2.addAnimation(scaleXAnimation);
        set2.addAnimation(scaleYAnimation);
        set2.setInterpolator(PHYSIC_BASED_INTERPOLATOR);
        set2.setZAdjustment(1);
        set2.setDuration(350);
        return set2;
    }

    static void addAnimationListener(Animation a, final Handler handler, IRemoteCallback mAnimationStartCallback, IRemoteCallback mAnimationFinishCallback) {
        final IRemoteCallback exitStartCallback = mAnimationStartCallback;
        final IRemoteCallback exitFinishCallback = mAnimationFinishCallback;
        a.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                IRemoteCallback iRemoteCallback = exitStartCallback;
                if (iRemoteCallback != null) {
                    handler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppTransitionInjector$2$oCfrMIDDpMwaYKwSfAKoB4nVg.INSTANCE, iRemoteCallback));
                }
            }

            public void onAnimationEnd(Animation animation) {
                IRemoteCallback iRemoteCallback = exitFinishCallback;
                if (iRemoteCallback != null) {
                    handler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppTransitionInjector$2$TndPHMvl5iyUTky6hIYYIv1WUo.INSTANCE, iRemoteCallback));
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /* access modifiers changed from: private */
    public static void doAnimationCallback(IRemoteCallback callback) {
        try {
            callback.sendResult((Bundle) null);
        } catch (RemoteException e) {
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: android.view.animation.ScaleAnimation} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: android.view.animation.AlphaAnimation} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.view.animation.Animation createLaunchAppFromHomeAnimation(int r22, boolean r23, android.graphics.Rect r24, android.graphics.Rect r25) {
        /*
            r0 = r22
            r1 = r25
            if (r1 != 0) goto L_0x0008
            r2 = 0
            return r2
        L_0x0008:
            boolean r2 = IS_E10
            if (r2 == 0) goto L_0x0011
            android.view.animation.Animation r2 = com.android.server.wm.AppTransitionInjector.BerylliumConfig.createLaunchAppFromHomeAnimation(r22, r23, r24, r25)
            return r2
        L_0x0011:
            int r2 = r24.width()
            int r3 = r24.height()
            int r4 = r1.left
            int r5 = r1.top
            int r6 = r25.width()
            int r7 = r25.height()
            r8 = 1
            r9 = 300(0x12c, double:1.48E-321)
            r11 = 1065353216(0x3f800000, float:1.0)
            if (r23 == 0) goto L_0x005b
            float r12 = (float) r6
            float r13 = (float) r2
            float r12 = r12 / r13
            float r13 = (float) r7
            float r14 = (float) r3
            float r13 = r13 / r14
            android.view.animation.ScaleAnimation r21 = new android.view.animation.ScaleAnimation
            r16 = 1065353216(0x3f800000, float:1.0)
            r18 = 1065353216(0x3f800000, float:1.0)
            float r14 = (float) r4
            float r15 = r11 - r12
            float r19 = r14 / r15
            float r14 = (float) r5
            float r11 = r11 - r13
            float r20 = r14 / r11
            r14 = r21
            r15 = r12
            r17 = r13
            r14.<init>(r15, r16, r17, r18, r19, r20)
            r11 = r21
            r11.setDuration(r9)
            r11.setZAdjustment(r8)
            android.view.animation.Interpolator r8 = QUINT_EASE_OUT_INTERPOLATOR
            r11.setInterpolator(r8)
            r11.initialize(r2, r3, r2, r3)
            r8 = r11
            goto L_0x009c
        L_0x005b:
            r12 = 14
            if (r0 == r12) goto L_0x008a
            r12 = 15
            if (r0 != r12) goto L_0x0064
            goto L_0x008a
        L_0x0064:
            android.view.animation.ScaleAnimation r11 = new android.view.animation.ScaleAnimation
            r14 = 1065353216(0x3f800000, float:1.0)
            r15 = 1061997773(0x3f4ccccd, float:0.8)
            r16 = 1065353216(0x3f800000, float:1.0)
            r17 = 1061997773(0x3f4ccccd, float:0.8)
            float r12 = (float) r2
            r13 = 1073741824(0x40000000, float:2.0)
            float r18 = r12 / r13
            float r12 = (float) r3
            float r19 = r12 / r13
            r13 = r11
            r13.<init>(r14, r15, r16, r17, r18, r19)
            r11.setDuration(r9)
            android.view.animation.Interpolator r9 = CUBIC_EASE_OUT_INTERPOLATOR
            r11.setInterpolator(r9)
            r9 = -1
            r11.setZAdjustment(r9)
            r9 = r11
            goto L_0x0098
        L_0x008a:
            android.view.animation.AlphaAnimation r12 = new android.view.animation.AlphaAnimation
            r13 = 0
            r12.<init>(r11, r13)
            r11 = r12
            r11.setDetachWallpaper(r8)
            r11.setDuration(r9)
            r9 = r11
        L_0x0098:
            r9.setFillAfter(r8)
            r8 = r9
        L_0x009c:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransitionInjector.createLaunchAppFromHomeAnimation(int, boolean, android.graphics.Rect, android.graphics.Rect):android.view.animation.Animation");
    }

    static Animation loadFreeFormAnimation(WindowManagerService service, int transit, boolean enter, Rect frame) {
        if (transit != 8 && transit != 10 && transit != 9 && transit != 11 && transit != 0) {
            return null;
        }
        if (MiuiMultiWindowUtils.needAnimation) {
            return createFreeFormAppOpenAndExitAnimation(enter, frame);
        }
        MiuiMultiWindowUtils.needAnimation = true;
        return null;
    }

    static Animation createFreeFormAppOpenAndExitAnimation(boolean enter, Rect frame) {
        Rect rect = frame;
        AnimationSet set = new AnimationSet(false);
        if (enter) {
            Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setInterpolator(new PhysicBasedInterpolator(0.85f, 0.7f));
            float windowHeight = ((float) frame.height()) * MiuiMultiWindowUtils.sScale * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE;
            Animation scaleAnimation = new ScaleAnimation(DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 1.0f, DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 1.0f, 0, ((float) rect.left) + (((((float) frame.width()) * MiuiMultiWindowUtils.sScale) * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE) / 2.0f), 0, ((float) rect.top) + (windowHeight / 2.0f));
            scaleAnimation.setDuration(500);
            scaleAnimation.setInterpolator(new PhysicBasedInterpolator(0.85f, 0.7f));
            set.addAnimation(alphaAnimation);
            set.addAnimation(scaleAnimation);
            set.setZAdjustment(1);
        } else {
            Animation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
            alphaAnimation2.setDuration(500);
            alphaAnimation2.setInterpolator(new PhysicBasedInterpolator(0.85f, 0.7f));
            float windowHeight2 = ((float) frame.height()) * MiuiMultiWindowUtils.sScale * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE;
            Animation scaleAnimation2 = new ScaleAnimation(1.0f, DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 1.0f, DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 0, ((float) rect.left) + (((((float) frame.width()) * MiuiMultiWindowUtils.sScale) * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE) / 2.0f), 0, ((float) rect.top) + (windowHeight2 / 2.0f));
            scaleAnimation2.setDuration(500);
            scaleAnimation2.setInterpolator(new PhysicBasedInterpolator(0.85f, 0.7f));
            set.addAnimation(alphaAnimation2);
            set.addAnimation(scaleAnimation2);
            set.setZAdjustment(1);
        }
        return set;
    }

    static Animation createLaunchSmallFreeFormAppAnimation(Context context, Rect frame) {
        Rect corner = MiuiMultiWindowUtils.findNearestCorner(context, 0.0f, 0.0f, 2, frame.width() > frame.height());
        AnimationSet set = new AnimationSet(false);
        Animation translateXAnimation = new TranslateXAnimation(0.0f, (float) corner.left);
        translateXAnimation.setInterpolator(new PhysicBasedInterpolator(0.9f, 0.8f));
        translateXAnimation.setDuration(250);
        TranslateYAnimation translateYAnimation = new TranslateYAnimation(0.0f, (float) corner.top);
        translateYAnimation.setInterpolator(new PhysicBasedInterpolator(0.9f, 0.8571f));
        translateYAnimation.setDuration(350);
        Animation scaleXAnimation = new ScaleXAnimation(1.0f, MiuiMultiWindowUtils.SMALL_FREEFORM_WIDTH / ((float) frame.width()), (float) corner.left);
        scaleXAnimation.setInterpolator(new PhysicBasedInterpolator(0.9f, 0.8571f));
        scaleXAnimation.setDuration(350);
        Animation scaleYAnimation = new ScaleYAnimation(1.0f, MiuiMultiWindowUtils.SMALL_FREEFORM_HEIGHT / ((float) frame.height()), (float) corner.top);
        scaleYAnimation.setInterpolator(new PhysicBasedInterpolator(0.9f, 0.8571f));
        scaleYAnimation.setDuration(350);
        set.addAnimation(translateXAnimation);
        set.addAnimation(translateYAnimation);
        set.addAnimation(scaleXAnimation);
        set.addAnimation(scaleYAnimation);
        set.setZAdjustment(1);
        return set;
    }

    static Animation createWallPaperOpenAnimation(boolean enter, Rect appFrame, Rect positionRect) {
        return createWallPaperOpenAnimation(enter, appFrame, positionRect, (Rect) null);
    }

    static Animation createWallPaperOpenAnimation(boolean enter, Rect appFrame, Rect positionRect, Rect startRect) {
        return createWallPaperOpenAnimation(enter, appFrame, positionRect, startRect, 1);
    }

    static Animation createWallPaperOpenAnimation(boolean enter, Rect appFrame, Rect positionRect, int orientation) {
        return createWallPaperOpenAnimation(enter, appFrame, positionRect, (Rect) null, orientation);
    }

    static Animation createWallPaperOpenAnimation(boolean enter, Rect appFrame, Rect positionRect, Rect startRect, int orientation) {
        return createTransitionAnimation(enter, appFrame, positionRect, (Rect) null, orientation, (Point) null);
    }

    static Animation createTransitionAnimation(boolean enter, Rect appFrame, Rect targetPositionRect, Rect startRect, int orientation, Point inertia) {
        int targetWidth;
        int i;
        int i2;
        int i3;
        int insetBottom;
        int targetX;
        int insetRight;
        int targetY;
        int startCenterX;
        float f;
        float f2;
        int startY;
        int startX;
        Animation translateYAnimation;
        Animation translateXAnimation;
        AnimationSet set;
        Animation alphaAnimation;
        int targetX2;
        Interpolator interpolator;
        Interpolator interpolator2;
        Rect rect = appFrame;
        Rect rect2 = startRect;
        Rect positionRect = new Rect(targetPositionRect);
        boolean hasStartRect = rect2 != null && !startRect.isEmpty();
        boolean isPortrait = orientation == 1;
        int appWidth = appFrame.width();
        int appHeight = appFrame.height();
        boolean canFindPosition = !positionRect.isEmpty();
        int insetLeft = sMiuiAnimSupportInset.left;
        int insetTop = sMiuiAnimSupportInset.top;
        int insetRight2 = sMiuiAnimSupportInset.right;
        int insetBottom2 = sMiuiAnimSupportInset.bottom;
        if (hasStartRect) {
            rect2.offset(-rect.left, -rect.top);
        }
        if (canFindPosition) {
            positionRect.offset(-rect.left, -rect.top);
        }
        if (canFindPosition) {
            targetWidth = positionRect.width() - (insetLeft + insetRight2);
        } else {
            targetWidth = (int) (((float) appWidth) * 0.4f);
        }
        if (canFindPosition) {
            i = positionRect.height() - (insetTop + insetBottom2);
        } else {
            i = (int) (((float) appHeight) * 0.4f);
        }
        int targetHeight = i;
        int startX2 = hasStartRect ? rect2.left : 0;
        int startY2 = hasStartRect ? rect2.top : 0;
        if (hasStartRect) {
            i2 = startX2 + (startRect.width() / 2);
        } else {
            i2 = rect.left + (appFrame.width() / 2);
        }
        int startCenterX2 = i2;
        if (hasStartRect) {
            i3 = startY2 + (startRect.height() / 2);
        } else {
            i3 = rect.top + (appFrame.height() / 2);
        }
        int startCenterY = i3;
        if (canFindPosition) {
            insetBottom = insetBottom2;
            targetX = positionRect.left + insetLeft;
        } else {
            insetBottom = insetBottom2;
            targetX = (int) ((((float) appWidth) * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE) / 2.0f);
        }
        if (canFindPosition) {
            insetRight = insetRight2;
            targetY = positionRect.top + insetTop;
        } else {
            insetRight = insetRight2;
            targetY = (int) ((((float) appHeight) * DEFAULT_BACK_TO_SCREEN_CENTER_SCALE) / 2.0f);
        }
        int startCenterY2 = startCenterY;
        if (hasStartRect) {
            startCenterX = startCenterX2;
            f = ((float) startRect.width()) / ((float) appWidth);
        } else {
            startCenterX = startCenterX2;
            f = 1.0f;
        }
        float startScaleX = f;
        if (hasStartRect) {
            boolean z = hasStartRect;
            f2 = ((float) startRect.height()) / ((float) appHeight);
        } else {
            f2 = 1.0f;
        }
        float startScaleY = f2;
        int insetTop2 = insetTop;
        float scaleX = ((float) targetWidth) / ((float) appWidth);
        int targetHeight2 = targetHeight;
        float scaleY = ((float) targetHeight) / ((float) appHeight);
        if (enter) {
            int targetWidth2 = targetWidth;
            AnimationSet set2 = new AnimationSet(true);
            int insetLeft2 = insetLeft;
            ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, ((float) appWidth) / 2.0f, ((float) appHeight) / 2.0f);
            int appHeight2 = appHeight;
            scaleAnimation.setDuration((long) DEFAULT_WALLPAPER_OPEN_DURATION);
            if (!IS_E10) {
                set2.addAnimation(scaleAnimation);
            }
            set2.setInterpolator(CUBIC_EASE_OUT_INTERPOLATOR);
            set2.setZAdjustment(-1);
            Rect rect3 = positionRect;
            int i4 = startX2;
            int i5 = targetX;
            int i6 = targetY;
            int i7 = appWidth;
            int i8 = appHeight2;
            int i9 = insetRight;
            int i10 = insetTop2;
            int targetY2 = startCenterY2;
            int i11 = insetLeft2;
            int appWidth2 = startY2;
            int insetLeft3 = startCenterX;
            int startCenterX3 = targetWidth2;
            int targetWidth3 = insetBottom;
            return set2;
        }
        int targetWidth4 = targetWidth;
        int insetLeft4 = insetLeft;
        int appHeight3 = appHeight;
        if (canFindPosition) {
            AnimationSet set3 = new AnimationSet(false);
            Animation alphaAnimation2 = new AlphaAnimation(1.0f, 0.0f);
            AnimationSet set4 = set3;
            int appWidth3 = appWidth;
            alphaAnimation2.setStartOffset(40);
            alphaAnimation2.setDuration((long) DEFAULT_ALPHA_DURATION);
            alphaAnimation2.setInterpolator(CUBIC_EASE_OUT_INTERPOLATOR);
            if (inertia == null) {
                translateXAnimation = new TranslateXAnimation((float) startX2, (float) (rect.left + targetX));
                translateYAnimation = new TranslateYAnimation((float) startY2, (float) (rect.top + targetY));
                Animation scaleXAnimation = new ScaleXAnimation(startScaleX, scaleX);
                if (isPortrait) {
                    interpolator2 = CUBIC_EASE_OUT_INTERPOLATOR;
                } else {
                    interpolator2 = QUINT_EASE_OUT_INTERPOLATOR;
                }
                scaleXAnimation.setInterpolator(interpolator2);
                int targetX3 = targetX;
                int targetY3 = targetY;
                scaleXAnimation.setDuration((long) DEFAULT_WALLPAPER_OPEN_DURATION);
                Animation scaleYAnimation = new ScaleYAnimation(startScaleY, scaleY);
                scaleYAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
                startY = startY2;
                startX = startX2;
                scaleYAnimation.setDuration((long) DEFAULT_WALLPAPER_OPEN_DURATION);
                translateXAnimation.setDuration((long) DEFAULT_WALLPAPER_OPEN_DURATION);
                translateYAnimation.setDuration((long) DEFAULT_WALLPAPER_OPEN_DURATION);
                AnimationSet set5 = set4;
                set5.addAnimation(scaleXAnimation);
                set5.addAnimation(scaleYAnimation);
                Rect rect4 = positionRect;
                set = set5;
                alphaAnimation = alphaAnimation2;
                int i12 = appWidth3;
                targetX2 = targetX3;
                int i13 = appHeight3;
                int i14 = insetTop2;
                int i15 = startCenterY2;
                int i16 = targetY3;
                int i17 = insetLeft4;
                int targetY4 = insetRight;
                int insetLeft5 = startCenterX;
                int startCenterX4 = targetWidth4;
                int targetWidth5 = insetBottom;
            } else {
                startY = startY2;
                startX = startX2;
                float endCenterX = (float) (positionRect.left + insetLeft4 + (targetWidth4 / 2));
                int startCenterX5 = startCenterX;
                int i18 = targetWidth4;
                int i19 = insetBottom;
                Rect rect5 = positionRect;
                targetX2 = targetX;
                int i20 = targetY;
                int targetY5 = insetRight;
                int i21 = insetTop2;
                alphaAnimation = alphaAnimation2;
                int i22 = insetLeft4;
                float f3 = endCenterX;
                float f4 = endCenterX;
                int i23 = startCenterX5;
                set = set4;
                float endCenterY = (float) (positionRect.top + insetTop2 + (targetHeight2 / 2));
                translateXAnimation = new BezierXAnimation(startScaleX, scaleX, (float) startCenterX5, f3, inertia, appWidth3);
                translateYAnimation = new BezierYAnimation(startScaleY, scaleY, (float) startCenterY2, endCenterY, inertia, appHeight3);
                translateXAnimation.setDuration(400);
                translateYAnimation.setDuration(400);
            }
            if (isPortrait) {
                interpolator = QUART_EASE_OUT_INTERPOLATOR;
            } else {
                interpolator = QUINT_EASE_OUT_INTERPOLATOR;
            }
            translateXAnimation.setInterpolator(interpolator);
            translateYAnimation.setInterpolator(QUART_EASE_OUT_INTERPOLATOR);
            set.addAnimation(alphaAnimation);
            set.addAnimation(translateXAnimation);
            set.addAnimation(translateYAnimation);
            set.setZAdjustment(1);
            int i24 = targetX2;
            int i25 = startX;
            int i26 = startY;
            return set;
        }
        int startY3 = startY2;
        int startX3 = startX2;
        int targetX4 = targetX;
        int targetY6 = targetY;
        int i27 = appWidth;
        int i28 = appHeight3;
        int i29 = insetRight;
        int i30 = insetTop2;
        int targetY7 = startCenterY2;
        int i31 = insetLeft4;
        int insetLeft6 = startCenterX;
        int startCenterX6 = targetWidth4;
        int targetWidth6 = insetBottom;
        AnimationSet set6 = new AnimationSet(true);
        Animation scaleAnimation2 = new ScaleAnimation(startScaleX, 0.4f, startScaleY, 0.4f);
        int i32 = targetX4;
        Animation alphaAnimation3 = new AlphaAnimation(1.0f, 0.0f);
        set6.addAnimation(scaleAnimation2);
        set6.addAnimation(new TranslateAnimation((float) startX3, (float) targetX4, (float) startY3, (float) targetY6));
        set6.addAnimation(alphaAnimation3);
        set6.setDuration((long) DEFAULT_ANIMATION_DURATION);
        set6.setInterpolator(CUBIC_EASE_OUT_INTERPOLATOR);
        if (IS_E10) {
            set6.setZAdjustment(1);
        }
        return set6;
    }

    static Animation createDummyAnimation(float alpha) {
        Animation dummyAnimation = new AlphaAnimation(alpha, alpha);
        dummyAnimation.setDuration(300);
        return dummyAnimation;
    }

    static Animation createTaskOpenCloseTransition(boolean enter, Rect appFrame, boolean isOpenOrClose) {
        Animation scaleAnimation;
        Animation translateAnimation;
        AnimationSet set = new AnimationSet(true);
        int width = appFrame.width();
        int heigth = appFrame.height();
        if (isOpenOrClose) {
            if (enter) {
                translateAnimation = new TranslateAnimation(1, 1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
                scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, (float) (width / 2), (float) (heigth / 2));
                scaleAnimation.setStartOffset(150);
            } else {
                scaleAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, (float) (width / 2), (float) (heigth / 2));
                translateAnimation = new TranslateAnimation(1, 0.0f, 1, -1.0f, 1, 0.0f, 1, 0.0f);
            }
        } else if (enter) {
            translateAnimation = new TranslateAnimation(1, -1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, (float) (width / 2), (float) (heigth / 2));
            scaleAnimation.setStartOffset(150);
        } else {
            scaleAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, (float) (width / 2), (float) (heigth / 2));
            translateAnimation = new TranslateAnimation(1, 0.0f, 1, 1.0f, 1, 0.0f, 1, 0.0f);
        }
        sActivityTransitionInterpolator = new ActivityTranstionInterpolator(DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 0.99f);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);
        set.setInterpolator(sActivityTransitionInterpolator);
        set.setDuration(650);
        return set;
    }

    static Animation createActivityOpenCloseTransition(boolean enter, Rect appFrame, boolean isOpenOrClose) {
        Animation translateAnimation;
        AnimationSet set = new AnimationSet(true);
        if (isOpenOrClose) {
            if (enter) {
                translateAnimation = new TranslateAnimation(1, 1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            } else {
                translateAnimation = new TranslateAnimation(1, 0.0f, 1, -0.25f, 1, 0.0f, 1, 0.0f);
                set.addAnimation(new AlphaAnimation(1.0f, 0.5f));
            }
        } else if (enter) {
            translateAnimation = new TranslateAnimation(1, -0.25f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            set.addAnimation(new AlphaAnimation(0.5f, 1.0f));
        } else {
            translateAnimation = new TranslateAnimation(1, 0.0f, 1, 1.0f, 1, 0.0f, 1, 0.0f);
        }
        sActivityTransitionInterpolator = new ActivityTranstionInterpolator(0.8f, 0.95f);
        set.addAnimation(translateAnimation);
        set.setInterpolator(sActivityTransitionInterpolator);
        set.setDuration(500);
        return set;
    }

    static Animation createWallerOpenCloseTransitionAnimation(boolean enter, Rect appFrame, boolean isOpenOrClose) {
        AnimationSet set = new AnimationSet(true);
        int width = appFrame.width();
        int height = appFrame.height();
        if (enter) {
            Animation translateAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 1.0f, 1, 0.0f);
            Animation scaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, 1, 0.5f, 1, 0.5f);
            scaleAnimation.setStartOffset(220);
            set.addAnimation(scaleAnimation);
            set.addAnimation(translateAnimation);
        } else {
            set.addAnimation(new AlphaAnimation(1.0f, 1.0f));
        }
        sActivityTransitionInterpolator = new ActivityTranstionInterpolator(0.65f, 0.95f);
        set.setInterpolator(sActivityTransitionInterpolator);
        set.setDuration(550);
        return set;
    }

    static Animation createActivityOpenCloseOratateTransition(boolean enter, Rect appFrame, boolean isOpenOrClose, float xfrom, float xto, float yfrom, float yto) {
        TranslateAnimation localTranslateAnimation;
        AnimationSet localAnimationSet = new AnimationSet(true);
        if (isOpenOrClose) {
            if (enter) {
                localTranslateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
            } else {
                TranslateAnimation localTranslateAnimation2 = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
                localAnimationSet.addAnimation(new AlphaAnimation(1.0f, 0.5f));
                localTranslateAnimation = localTranslateAnimation2;
            }
        } else if (enter) {
            TranslateAnimation translateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
            localAnimationSet.addAnimation(new AlphaAnimation(0.5f, 1.0f));
            localTranslateAnimation = translateAnimation;
        } else {
            localTranslateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
        }
        sActivityTransitionInterpolator = new ActivityTranstionInterpolator(0.8f, 0.95f);
        localAnimationSet.addAnimation(localTranslateAnimation);
        localAnimationSet.setInterpolator(sActivityTransitionInterpolator);
        localAnimationSet.setDuration(500);
        return localAnimationSet;
    }

    static Animation createTaskOpenCloseOratateTransition(boolean enter, Rect appFrame, boolean isOpenOrClose, float xfrom, float xto, float yfrom, float yto) {
        ScaleAnimation localScaleAnimation;
        TranslateAnimation localTranslateAnimation;
        AnimationSet localAnimationSet = new AnimationSet(true);
        int i = appFrame.width();
        int j = appFrame.height();
        if (isOpenOrClose) {
            if (enter) {
                localTranslateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
                localScaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, (float) (i / 2), (float) (j / 2));
                localScaleAnimation.setStartOffset(150);
            } else {
                localScaleAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, (float) (i / 2), (float) (j / 2));
                localTranslateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
            }
        } else if (enter) {
            localTranslateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
            localScaleAnimation = new ScaleAnimation(0.9f, 1.0f, 0.9f, 1.0f, (float) (i / 2), (float) (j / 2));
            localScaleAnimation.setStartOffset(150);
        } else {
            localScaleAnimation = new ScaleAnimation(1.0f, 0.9f, 1.0f, 0.9f, (float) (i / 2), (float) (j / 2));
            localTranslateAnimation = new TranslateAnimation(1, xfrom, 1, xto, 1, yfrom, 1, yto);
        }
        sActivityTransitionInterpolator = new ActivityTranstionInterpolator(DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 0.99f);
        localAnimationSet.addAnimation(localScaleAnimation);
        localAnimationSet.addAnimation(localTranslateAnimation);
        localAnimationSet.setInterpolator(sActivityTransitionInterpolator);
        localAnimationSet.setDuration(650);
        return localAnimationSet;
    }

    static void notifyMiuiAnimationStart(IMiuiAppTransitionAnimationHelper helper) {
        if (helper != null) {
            try {
                helper.notifyMiuiAnimationStart();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static void notifyMiuiAnimationEnd(IMiuiAppTransitionAnimationHelper helper) {
        if (helper != null) {
            try {
                helper.notifyMiuiAnimationEnd();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static void calculateMiuiThumbnailSpec(Rect appRect, Rect thumbnailRect, Matrix curSpec, float alpha, SurfaceControl.Transaction t, SurfaceControl leash) {
        Matrix matrix = curSpec;
        SurfaceControl.Transaction transaction = t;
        SurfaceControl surfaceControl = leash;
        if (appRect == null || thumbnailRect == null || matrix == null || surfaceControl == null) {
            float f = alpha;
        } else if (transaction == null) {
            float f2 = alpha;
        } else {
            float[] tmp = new float[9];
            matrix.getValues(tmp);
            float curScaleX = tmp[0];
            float curScaleY = tmp[4];
            float curWidth = ((float) appRect.width()) * curScaleX;
            float curHeight = ((float) appRect.height()) * curScaleY;
            float newScaleX = curWidth / ((float) ((thumbnailRect.width() - sMiuiAnimSupportInset.left) - sMiuiAnimSupportInset.right));
            float newScaleY = curHeight / ((float) ((thumbnailRect.height() - sMiuiAnimSupportInset.top) - sMiuiAnimSupportInset.bottom));
            float curTranslateX = tmp[2] - (((float) sMiuiAnimSupportInset.left) * newScaleX);
            float curTranslateY = tmp[5] - (((float) sMiuiAnimSupportInset.top) * newScaleY);
            float f3 = tmp[3];
            float curTranslateY2 = curTranslateY;
            float curTranslateY3 = tmp[1];
            float[] fArr = tmp;
            t.setMatrix(leash, newScaleX, f3, curTranslateY3, newScaleY);
            transaction.setPosition(surfaceControl, curTranslateX, curTranslateY2);
            transaction.setAlpha(surfaceControl, alpha);
        }
    }

    static void calculateMiuiActivityThumbnailSpec(Rect appRect, Rect thumbnailRect, Matrix curSpec, float alpha, float radius, SurfaceControl.Transaction t, SurfaceControl leash) {
        Matrix matrix = curSpec;
        SurfaceControl.Transaction transaction = t;
        SurfaceControl surfaceControl = leash;
        if (appRect == null || thumbnailRect == null || matrix == null || surfaceControl == null) {
            float f = alpha;
        } else if (transaction == null) {
            float f2 = alpha;
        } else {
            float[] tmp = new float[9];
            matrix.getValues(tmp);
            float curTranslateX = tmp[2];
            float curTranslateY = tmp[5];
            transaction.setWindowCrop(surfaceControl, new Rect(0, 0, (int) (((float) appRect.width()) * tmp[0]), (int) (((float) appRect.height()) * tmp[4])));
            transaction.setPosition(surfaceControl, curTranslateX, curTranslateY);
            transaction.setAlpha(surfaceControl, alpha);
        }
    }

    static void calculateGestureThumbnailSpec(Rect appRect, Rect thumbnailRect, Matrix curSpec, float alpha, SurfaceControl.Transaction t, SurfaceControl leash) {
        Rect rect = appRect;
        Matrix matrix = curSpec;
        SurfaceControl.Transaction transaction = t;
        SurfaceControl surfaceControl = leash;
        if (rect == null || thumbnailRect == null || matrix == null || surfaceControl == null) {
            float f = alpha;
        } else if (transaction == null) {
            float f2 = alpha;
        } else {
            Matrix tmpMatrix = new Matrix(matrix);
            tmpMatrix.postTranslate((float) rect.left, (float) rect.top);
            float[] tmp = new float[9];
            tmpMatrix.getValues(tmp);
            float curScaleX = tmp[0];
            float curScaleY = tmp[4];
            float curWidth = ((float) appRect.width()) * curScaleX;
            float curHeight = ((float) appRect.height()) * curScaleY;
            float newScaleX = curWidth / ((float) ((thumbnailRect.width() - sMiuiAnimSupportInset.left) - sMiuiAnimSupportInset.right));
            float newScaleY = curHeight / ((float) ((thumbnailRect.height() - sMiuiAnimSupportInset.top) - sMiuiAnimSupportInset.bottom));
            float curThumbnailHeight = newScaleX * ((float) ((thumbnailRect.height() - sMiuiAnimSupportInset.top) - sMiuiAnimSupportInset.bottom));
            float curTranslateX = tmp[2] - (((float) sMiuiAnimSupportInset.left) * newScaleX);
            float curTranslateX2 = curTranslateX;
            t.setMatrix(leash, newScaleX, tmp[3], tmp[1], newScaleX);
            transaction.setPosition(surfaceControl, curTranslateX2, (tmp[5] - (((float) sMiuiAnimSupportInset.top) * newScaleY)) + ((curHeight - curThumbnailHeight) / 2.0f));
            transaction.setAlpha(surfaceControl, alpha);
        }
    }

    static void setMiuiAnimSupportInset(Rect inset) {
        if (inset == null) {
            sMiuiAnimSupportInset.setEmpty();
        } else {
            sMiuiAnimSupportInset.set(inset);
        }
    }

    static boolean allowCustomAnimation(ArraySet<AppWindowToken> closingApps) {
        WindowState win;
        if (closingApps == null) {
            return false;
        }
        int size = closingApps.size();
        for (int i = 0; i < size; i++) {
            AppWindowToken atoken = closingApps.valueAt(i);
            if (atoken != null && (win = atoken.findMainWindow()) != null && WHITE_LIST_ALLOW_CUSTOM_ANIMATION.contains(win.mAttrs.packageName)) {
                return true;
            }
        }
        return false;
    }

    static Animation loadDefaultAnimation(WindowManager.LayoutParams lp, int transit, boolean enter, Rect frame) {
        if (useDefaultAnimationAttr(lp)) {
            return loadDefaultAnimationNotCheck(lp, transit, enter, frame);
        }
        return null;
    }

    static Animation loadDefaultAnimationNotCheck(WindowManager.LayoutParams lp, int transit, boolean enter, Rect frame) {
        if (transit != 19) {
            switch (transit) {
                case 6:
                    return createActivityOpenCloseTransition(enter, frame, true);
                case 7:
                    return createActivityOpenCloseTransition(enter, frame, false);
                case 8:
                    break;
                case 9:
                    return createTaskOpenCloseTransition(enter, frame, false);
                case 10:
                    return createTaskOpenCloseTransition(enter, frame, true);
                case 11:
                    return createTaskOpenCloseTransition(enter, frame, false);
                case 12:
                    if (enter) {
                        return createWallerOpenCloseTransitionAnimation(enter, frame, false);
                    }
                    return null;
                default:
                    return null;
            }
        }
        return createTaskOpenCloseTransition(enter, frame, true);
    }

    static boolean useDefaultAnimationAttr(WindowManager.LayoutParams lp, int resId) {
        if (lp == null) {
            return false;
        }
        String packageName = lp.packageName != null ? lp.packageName : PackageManagerService.PLATFORM_PACKAGE_NAME;
        if ((-16777216 & resId) == 16777216) {
            packageName = PackageManagerService.PLATFORM_PACKAGE_NAME;
        }
        return PackageManagerService.PLATFORM_PACKAGE_NAME.equals(packageName);
    }

    static boolean useDefaultAnimationAttr(WindowManager.LayoutParams lp) {
        if (lp == null) {
            return false;
        }
        String packageName = lp.packageName != null ? lp.packageName : PackageManagerService.PLATFORM_PACKAGE_NAME;
        if ((-16777216 & lp.windowAnimations) == 16777216) {
            packageName = PackageManagerService.PLATFORM_PACKAGE_NAME;
        }
        return PackageManagerService.PLATFORM_PACKAGE_NAME.equals(packageName);
    }

    static boolean isNextAppTransitionWallpaperClose(int transit) {
        return transit == 12;
    }

    static long recalculateClipRevealTranslateYDuration(long duration) {
        return duration - 50;
    }

    private static class CubicEaseOutInterpolator implements Interpolator {
        private CubicEaseOutInterpolator() {
        }

        public float getInterpolation(float t) {
            float f = t - 1.0f;
            float t2 = f;
            return (f * t2 * t2) + 1.0f;
        }
    }

    private static class QuartEaseOutInterpolator implements Interpolator {
        private QuartEaseOutInterpolator() {
        }

        public float getInterpolation(float t) {
            float f = t - 1.0f;
            float t2 = f;
            return -((((f * t2) * t2) * t2) - 1.0f);
        }
    }

    private static class QuintEaseOutInterpolator implements Interpolator {
        private QuintEaseOutInterpolator() {
        }

        public float getInterpolation(float t) {
            float f = t - 1.0f;
            float t2 = f;
            return (f * t2 * t2 * t2 * t2) + 1.0f;
        }
    }

    private static class ActivityTranstionInterpolator implements Interpolator {
        private static float c = 1.0f;
        private static float c1 = 1.0f;
        private static float c2 = 1.0f;
        private static float initial = 1.0f;
        private static float k = 1.0f;
        private static float m = 1.0f;
        private static float mDamping = 1.0f;
        private static float mResponse = 1.0f;
        private static float r = 1.0f;
        private static float w = 1.0f;

        public ActivityTranstionInterpolator(float response, float damping) {
            mDamping = damping;
            mResponse = response;
            initial = -1.0f;
            double pow = Math.pow(6.283185307179586d / ((double) mResponse), 2.0d);
            float f = m;
            k = (float) (pow * ((double) f));
            c = (float) (((((double) mDamping) * 12.566370614359172d) * ((double) f)) / ((double) mResponse));
            float f2 = f * 4.0f * k;
            float f3 = c;
            float f4 = m;
            w = ((float) Math.sqrt((double) (f2 - (f3 * f3)))) / (f4 * 2.0f);
            r = -((c / 2.0f) * f4);
            float f5 = initial;
            c1 = f5;
            c2 = (0.0f - (r * f5)) / w;
        }

        public float getInterpolation(float input) {
            float res = (float) ((Math.pow(2.718281828459045d, (double) (r * input)) * ((((double) c1) * Math.cos((double) (w * input))) + (((double) c2) * Math.sin((double) (w * input))))) + 1.0d);
            float res2 = 1.0f;
            if (input != 1.0f) {
                res2 = res;
            }
            return res2;
        }
    }

    private static class ScaleXAnimation extends Animation {
        private float mFromX;
        private float mPivotX;
        private float mToX;

        public ScaleXAnimation(float fromX, float toX) {
            this.mFromX = fromX;
            this.mToX = toX;
            this.mPivotX = 0.0f;
        }

        public ScaleXAnimation(float fromX, float toX, float pivotX) {
            this.mFromX = fromX;
            this.mToX = toX;
            this.mPivotX = pivotX;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float sx = 1.0f;
            float scale = getScaleFactor();
            if (!(this.mFromX == 1.0f && this.mToX == 1.0f)) {
                float f = this.mFromX;
                sx = f + ((this.mToX - f) * interpolatedTime);
            }
            if (this.mPivotX == 0.0f) {
                t.getMatrix().setScale(sx, 1.0f);
            } else {
                t.getMatrix().setScale(sx, 1.0f, this.mPivotX * scale, 0.0f);
            }
        }
    }

    private static class ScaleYAnimation extends Animation {
        private float mFromY;
        private float mPivotY;
        private float mToY;

        public ScaleYAnimation(float fromY, float toY) {
            this.mFromY = fromY;
            this.mToY = toY;
            this.mPivotY = 0.0f;
        }

        public ScaleYAnimation(float fromY, float toY, float pivotY) {
            this.mFromY = fromY;
            this.mToY = toY;
            this.mPivotY = pivotY;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float sy = 1.0f;
            float scale = getScaleFactor();
            if (!(this.mFromY == 1.0f && this.mToY == 1.0f)) {
                float f = this.mFromY;
                sy = f + ((this.mToY - f) * interpolatedTime);
            }
            if (this.mPivotY == 0.0f) {
                t.getMatrix().setScale(1.0f, sy);
            } else {
                t.getMatrix().setScale(1.0f, sy, 0.0f, this.mPivotY * scale);
            }
        }
    }

    private static class BezierXAnimation extends Animation {
        private float mEndX;
        private float mFromX;
        private float mInertiaX;
        private float mPivotX;
        private float mStartX;
        private float mToX;
        private int mWidth;

        public BezierXAnimation(float fromX, float toX, float startX, float endX, Point inertia, int width) {
            this(fromX, toX, 0.0f, startX, endX, inertia, width);
        }

        public BezierXAnimation(float fromX, float toX, float pivotX, float startX, float endX, Point inertia, int width) {
            this.mStartX = startX;
            this.mEndX = endX;
            this.mInertiaX = (float) (((double) startX) + (((double) inertia.x) * 1.2d));
            this.mFromX = fromX;
            this.mToX = toX;
            this.mPivotX = pivotX;
            this.mWidth = width;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float sx = 1.0f;
            float scale = getScaleFactor();
            if (!(this.mFromX == 1.0f && this.mToX == 1.0f)) {
                float f = this.mFromX;
                sx = f + ((this.mToX - f) * interpolatedTime);
            }
            if (this.mPivotX == 0.0f) {
                t.getMatrix().setScale(sx, 1.0f);
            } else {
                t.getMatrix().setScale(sx, 1.0f, this.mPivotX * scale, 0.0f);
            }
            t.getMatrix().postTranslate(((float) (((Math.pow((double) (1.0f - interpolatedTime), 2.0d) * ((double) this.mStartX)) + (Math.pow((double) interpolatedTime, 2.0d) * ((double) this.mEndX))) + ((double) (((interpolatedTime * 2.0f) * (1.0f - interpolatedTime)) * this.mInertiaX)))) - ((((float) this.mWidth) * sx) / 2.0f), 0.0f);
        }
    }

    private static class BezierYAnimation extends Animation {
        private float mEndY;
        private float mFromY;
        private int mHeight;
        private float mInertiaY;
        private float mPivotY;
        private float mStartY;
        private float mToY;

        public BezierYAnimation(float fromY, float toY, float startY, float endY, Point inertia, int height) {
            this(fromY, toY, 0.0f, startY, endY, inertia, height);
        }

        public BezierYAnimation(float fromY, float toY, float pivotY, float startY, float endY, Point inertia, int height) {
            this.mFromY = fromY;
            this.mToY = toY;
            this.mPivotY = pivotY;
            this.mStartY = startY;
            this.mEndY = endY;
            this.mInertiaY = (float) (((double) startY) + (((double) inertia.y) * 1.5d));
            this.mHeight = height;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation t) {
            float sy = 1.0f;
            float scale = getScaleFactor();
            if (!(this.mFromY == 1.0f && this.mToY == 1.0f)) {
                float f = this.mFromY;
                sy = f + ((this.mToY - f) * interpolatedTime);
            }
            if (this.mPivotY == 0.0f) {
                t.getMatrix().setScale(1.0f, sy);
            } else {
                t.getMatrix().setScale(1.0f, sy, 0.0f, this.mPivotY * scale);
            }
            t.getMatrix().postTranslate(0.0f, ((float) (((Math.pow((double) (1.0f - interpolatedTime), 2.0d) * ((double) this.mStartY)) + (Math.pow((double) interpolatedTime, 2.0d) * ((double) this.mEndY))) + ((double) ((((1.0f - interpolatedTime) * 2.0f) * interpolatedTime) * this.mInertiaY)))) - ((((float) this.mHeight) * sy) / 2.0f));
        }
    }

    public static Rect getMiuiAnimSupportInset() {
        return sMiuiAnimSupportInset;
    }

    private static class BerylliumConfig {
        private static final int DEFAULT_ALPHA_DURATION = 88;
        protected static final int DEFAULT_ANIMATION_DURATION = 144;
        private static final float DEFAULT_LAUNCHER_TRANSITION_ALPHA = 1.0f;
        private static final int DEFAULT_LAUNCH_FORM_HOME_DURATION = 176;
        protected static final int DEFAULT_WALLPAPER_OPEN_DURATION = 144;
        protected static final Interpolator FAST_OUT_SLOW_IN = new PathInterpolator(0.0f, AppTransitionInjector.DEFAULT_BACK_TO_SCREEN_CENTER_SCALE, 0.4f, 1.0f);
        private static final Rect FULL_SCREEN_DISPLAY_FRAME = new Rect(0, 0, 1080, 2246);

        private BerylliumConfig() {
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: android.view.animation.AlphaAnimation} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: android.view.animation.AlphaAnimation} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: android.view.animation.AnimationSet} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: android.view.animation.AlphaAnimation} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static android.view.animation.Animation createLaunchAppFromHomeAnimation(int r7, boolean r8, android.graphics.Rect r9, android.graphics.Rect r10) {
            /*
                r0 = 176(0xb0, double:8.7E-322)
                r2 = 1065353216(0x3f800000, float:1.0)
                r3 = 1
                if (r8 == 0) goto L_0x0035
                android.view.animation.AlphaAnimation r4 = new android.view.animation.AlphaAnimation
                float r5 = com.android.server.wm.AppTransitionInjector.LAUNCHER_TRANSITION_ALPHA
                r4.<init>(r5, r2)
                r2 = r4
                r4 = 88
                r2.setDuration(r4)
                android.view.animation.ClipRectAnimation r4 = new android.view.animation.ClipRectAnimation
                android.graphics.Rect r5 = FULL_SCREEN_DISPLAY_FRAME
                r4.<init>(r10, r5)
                r4.setDuration(r0)
                android.view.animation.AnimationSet r0 = new android.view.animation.AnimationSet
                r0.<init>(r3)
                r0.addAnimation(r2)
                r0.addAnimation(r4)
                android.view.animation.Interpolator r1 = FAST_OUT_SLOW_IN
                r0.setInterpolator(r1)
                r0.setZAdjustment(r3)
                goto L_0x006e
            L_0x0035:
                r4 = 14
                if (r7 == r4) goto L_0x005a
                r4 = 15
                if (r7 != r4) goto L_0x003e
                goto L_0x005a
            L_0x003e:
                android.view.animation.AnimationSet r4 = new android.view.animation.AnimationSet
                r4.<init>(r3)
                android.view.animation.AlphaAnimation r5 = new android.view.animation.AlphaAnimation
                float r6 = com.android.server.wm.AppTransitionInjector.LAUNCHER_TRANSITION_ALPHA
                r5.<init>(r2, r6)
                r2 = r5
                r2.setDuration(r0)
                r4.addAnimation(r2)
                android.view.animation.Interpolator r0 = FAST_OUT_SLOW_IN
                r4.setInterpolator(r0)
                r0 = r4
                goto L_0x006b
            L_0x005a:
                android.view.animation.AlphaAnimation r4 = new android.view.animation.AlphaAnimation
                float r5 = com.android.server.wm.AppTransitionInjector.LAUNCHER_TRANSITION_ALPHA
                r4.<init>(r2, r5)
                r2 = r4
                r2.setDetachWallpaper(r3)
                r2.setDuration(r0)
                r0 = r2
            L_0x006b:
                r0.setFillAfter(r3)
            L_0x006e:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.AppTransitionInjector.BerylliumConfig.createLaunchAppFromHomeAnimation(int, boolean, android.graphics.Rect, android.graphics.Rect):android.view.animation.Animation");
        }
    }

    static boolean disableSnapshot(String pkg) {
        if (BLACK_LIST_NOT_ALLOWED_SNAPSHOT.contains(pkg)) {
            return true;
        }
        return false;
    }

    static boolean disableSnapshotForApplock(String pkg, int uid) {
        IBinder b = ServiceManager.getService("security");
        if (b == null) {
            return false;
        }
        try {
            int userId = UserHandle.getUserId(uid);
            ISecurityManager service = ISecurityManager.Stub.asInterface(b);
            return service.haveAccessControlPassword(userId) && service.getApplicationAccessControlEnabledAsUser(pkg, userId) && !service.checkAccessControlPassAsUser(pkg, (Intent) null, userId);
        } catch (Exception e) {
            Slog.e(TAG, "start window checkAccessControlPass error: ", e);
            return false;
        }
    }

    static void initDisplayRoundCorner(Context context) {
        int result;
        if (context == null) {
            result = 60;
        } else {
            int top = 0;
            int bottom = 0;
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("rounded_corner_radius_top", "dimen", PackageManagerService.PLATFORM_PACKAGE_NAME);
            if (resourceId > 0) {
                top = resources.getDimensionPixelSize(resourceId);
            }
            int resourceId2 = resources.getIdentifier("rounded_corner_radius_bottom", "dimen", PackageManagerService.PLATFORM_PACKAGE_NAME);
            if (resourceId2 > 0) {
                bottom = resources.getDimensionPixelSize(resourceId2);
            }
            result = top > bottom ? bottom : top;
            if (result <= 0) {
                result = 60;
            }
        }
        if (result == 60) {
            Slog.w(TAG, "use default round corner Radius");
        }
        DISPLAY_ROUND_CORNER_RADIUS = result;
    }
}
