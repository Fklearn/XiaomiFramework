package com.android.server.wm;

import android.graphics.Rect;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ClipRectAnimation;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.view.animation.TranslateYAnimation;

public class MultiWindowTransition {
    private static final int DEFAULT_APP_TRANSITION_DURATION = 300;
    private static final Rect INVALID_CLIP_RECT = new Rect(0, 0, 1, 1);
    private static final int MULTI_WINDOW_DEFAULT_OFFSET = 1;
    private static final int THUMBNAIL_APP_TRANSITION_DURATION = 336;
    private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_DOWN_COMPATIBLE = 2;
    private static final int THUMBNAIL_TRANSITION_ENTER_SCALE_UP_COMPATIBLE = 0;
    private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_DOWN_COMPATIBLE = 3;
    private static final int THUMBNAIL_TRANSITION_EXIT_SCALE_UP_COMPATIBLE = 1;
    public static final int TRANSIT_MULTI_WINDOW_ENTER_FROM_OTHER = 202;
    public static final int TRANSIT_MULTI_WINDOW_ENTER_FROM_TOP = 201;
    public static final int TRANSIT_MULTI_WINDOW_EXIT = 203;

    static Animation loadMultiWindowAnimation(int transit, int displayHeight, Rect containingFrame, Rect positionRect, boolean enter) {
        if (containingFrame == null || positionRect == null) {
            return null;
        }
        int top = containingFrame.top;
        int bottom = containingFrame.bottom;
        boolean isTopWin = top == 0 && bottom < displayHeight;
        boolean isBottomWin = top > 0 && bottom == displayHeight;
        switch (transit) {
            case TRANSIT_MULTI_WINDOW_ENTER_FROM_TOP /*201*/:
                if (!enter) {
                    return null;
                }
                if (isTopWin) {
                    return loadTopWindowAnimation(true, containingFrame);
                }
                if (isBottomWin) {
                    return loadTranslateYAnimation(true, (float) (bottom - top), 0.0f);
                }
                return null;
            case TRANSIT_MULTI_WINDOW_ENTER_FROM_OTHER /*202*/:
                if (!enter) {
                    return null;
                }
                if (isTopWin) {
                    return loadTopWindowAnimation(true, containingFrame);
                }
                if (isBottomWin) {
                    return loadClipRevealAnimation(containingFrame, positionRect);
                }
                return null;
            case TRANSIT_MULTI_WINDOW_EXIT /*203*/:
                if (enter) {
                    return null;
                }
                if (isTopWin) {
                    return loadTopWindowAnimation(false, containingFrame);
                }
                if (isBottomWin) {
                    return loadTranslateYAnimation(false, 0.0f, (float) (bottom - top));
                }
                return null;
            default:
                return null;
        }
    }

    static boolean isMultiWindowAppTransition(int transit) {
        return transit == 201 || transit == 202 || transit == 203;
    }

    static boolean needKeepTransition(int transit) {
        return transit == 6 || transit == 7 || isMultiWindowAppTransition(transit);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004d, code lost:
        if (r0 != 3) goto L_0x0153;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.view.animation.Animation createAspectScaledThumbnailEnterExitAnimationCompatibleLocked(int r26, int r27, android.graphics.Rect r28, android.graphics.Rect r29, int r30, int r31, int r32, int r33, int r34) {
        /*
            r0 = r26
            r1 = r28
            r2 = r29
            r3 = r30
            r4 = 0
            int r11 = r28.width()
            int r12 = r28.height()
            r5 = 1
            if (r31 <= 0) goto L_0x0017
            r6 = r31
            goto L_0x0018
        L_0x0017:
            r6 = r5
        L_0x0018:
            r13 = r6
            if (r32 <= 0) goto L_0x001e
            r6 = r32
            goto L_0x001f
        L_0x001e:
            r6 = r5
        L_0x001f:
            r14 = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r15 = r6
            android.graphics.Rect r6 = new android.graphics.Rect
            r6.<init>()
            r10 = r6
            r15.set(r1)
            r10.set(r1)
            r6 = 0
            r15.offsetTo(r6, r6)
            r10.offsetTo(r6, r6)
            int r7 = r15.top
            int r8 = r2.top
            int r7 = r7 + r8
            r15.top = r7
            r7 = 0
            r8 = 1065353216(0x3f800000, float:1.0)
            if (r0 == 0) goto L_0x0075
            r9 = 14
            if (r0 == r5) goto L_0x0063
            r6 = 2
            if (r0 == r6) goto L_0x0051
            r6 = 3
            if (r0 == r6) goto L_0x0075
            goto L_0x0153
        L_0x0051:
            if (r3 != r9) goto L_0x005b
            android.view.animation.AlphaAnimation r5 = new android.view.animation.AlphaAnimation
            r5.<init>(r7, r8)
            r4 = r5
            goto L_0x0153
        L_0x005b:
            android.view.animation.AlphaAnimation r5 = new android.view.animation.AlphaAnimation
            r5.<init>(r8, r8)
            r4 = r5
            goto L_0x0153
        L_0x0063:
            if (r3 != r9) goto L_0x006d
            android.view.animation.AlphaAnimation r5 = new android.view.animation.AlphaAnimation
            r5.<init>(r8, r7)
            r4 = r5
            goto L_0x0153
        L_0x006d:
            android.view.animation.AlphaAnimation r5 = new android.view.animation.AlphaAnimation
            r5.<init>(r8, r8)
            r4 = r5
            goto L_0x0153
        L_0x0075:
            if (r0 != 0) goto L_0x007a
            r16 = r5
            goto L_0x007c
        L_0x007a:
            r16 = 0
        L_0x007c:
            r6 = r16
            android.view.animation.AnimationSet r9 = new android.view.animation.AnimationSet
            r9.<init>(r5)
            float r8 = (float) r13
            int r5 = r2.left
            int r5 = r11 - r5
            int r7 = r2.right
            int r5 = r5 - r7
            float r5 = (float) r5
            float r8 = r8 / r5
            float r5 = (float) r14
            int r7 = r2.top
            int r7 = r12 - r7
            int r0 = r2.bottom
            int r7 = r7 - r0
            float r0 = (float) r7
            float r0 = r5 / r0
            float r5 = (float) r14
            float r5 = r5 / r0
            int r5 = (int) r5
            int r7 = r15.top
            int r7 = r7 + r5
            r15.bottom = r7
            android.view.animation.ScaleAnimation r7 = new android.view.animation.ScaleAnimation
            if (r6 == 0) goto L_0x00a7
            r18 = r8
            goto L_0x00a9
        L_0x00a7:
            r18 = 1065353216(0x3f800000, float:1.0)
        L_0x00a9:
            if (r6 == 0) goto L_0x00ae
            r19 = 1065353216(0x3f800000, float:1.0)
            goto L_0x00b0
        L_0x00ae:
            r19 = r8
        L_0x00b0:
            if (r6 == 0) goto L_0x00b5
            r20 = r0
            goto L_0x00b7
        L_0x00b5:
            r20 = 1065353216(0x3f800000, float:1.0)
        L_0x00b7:
            if (r6 == 0) goto L_0x00bc
            r21 = 1065353216(0x3f800000, float:1.0)
            goto L_0x00be
        L_0x00bc:
            r21 = r0
        L_0x00be:
            int r3 = r1.left
            float r3 = (float) r3
            r24 = r4
            float r4 = (float) r11
            r25 = 1073741824(0x40000000, float:2.0)
            float r4 = r4 / r25
            float r3 = r3 + r4
            int r4 = r2.left
            float r4 = (float) r4
            float r22 = r3 + r4
            int r3 = r1.top
            float r3 = (float) r3
            float r4 = (float) r12
            float r4 = r4 / r25
            float r3 = r3 + r4
            int r4 = r2.top
            float r4 = (float) r4
            float r23 = r3 + r4
            r17 = r7
            r17.<init>(r18, r19, r20, r21, r22, r23)
            r3 = r7
            int r4 = r1.left
            int r4 = r33 - r4
            float r4 = (float) r4
            float r7 = (float) r11
            float r7 = r7 / r25
            r17 = r5
            float r5 = (float) r11
            float r5 = r5 / r25
            float r5 = r5 * r8
            float r7 = r7 - r5
            int r5 = r1.top
            int r5 = r34 - r5
            float r5 = (float) r5
            float r1 = (float) r12
            float r1 = r1 / r25
            r18 = r8
            float r8 = (float) r12
            float r8 = r8 / r25
            float r8 = r8 * r0
            float r1 = r1 - r8
            float r8 = r4 - r7
            float r19 = r5 - r1
            if (r6 == 0) goto L_0x010c
            r20 = r0
            android.view.animation.ClipRectAnimation r0 = new android.view.animation.ClipRectAnimation
            r0.<init>(r15, r10)
            goto L_0x0113
        L_0x010c:
            r20 = r0
            android.view.animation.ClipRectAnimation r0 = new android.view.animation.ClipRectAnimation
            r0.<init>(r10, r15)
        L_0x0113:
            if (r6 == 0) goto L_0x0128
            r21 = r1
            android.view.animation.TranslateAnimation r1 = new android.view.animation.TranslateAnimation
            r22 = r4
            int r4 = r2.top
            float r4 = (float) r4
            float r4 = r19 - r4
            r23 = r5
            r5 = 0
            r1.<init>(r8, r5, r4, r5)
            goto L_0x0139
        L_0x0128:
            r21 = r1
            r22 = r4
            r23 = r5
            r5 = 0
            android.view.animation.TranslateAnimation r1 = new android.view.animation.TranslateAnimation
            int r4 = r2.top
            float r4 = (float) r4
            float r4 = r19 - r4
            r1.<init>(r5, r8, r5, r4)
        L_0x0139:
            r9.addAnimation(r3)
            int r4 = r15.height()
            int r5 = r10.height()
            if (r4 == r5) goto L_0x014a
            r9.addAnimation(r0)
        L_0x014a:
            r9.addAnimation(r1)
            r4 = r9
            r5 = 1
            r4.setZAdjustment(r5)
        L_0x0153:
            r8 = 336(0x150, double:1.66E-321)
            android.view.animation.PathInterpolator r0 = new android.view.animation.PathInterpolator
            r1 = 1050253722(0x3e99999a, float:0.3)
            r3 = 1036831949(0x3dcccccd, float:0.1)
            r5 = 0
            r6 = 1065353216(0x3f800000, float:1.0)
            r0.<init>(r1, r5, r3, r6)
            r5 = r4
            r6 = r11
            r7 = r12
            r1 = r10
            r10 = r0
            android.view.animation.Animation r0 = prepareThumbnailAnimationWithDuration(r5, r6, r7, r8, r10)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.MultiWindowTransition.createAspectScaledThumbnailEnterExitAnimationCompatibleLocked(int, int, android.graphics.Rect, android.graphics.Rect, int, int, int, int, int):android.view.animation.Animation");
    }

    static int getThumbnailTransitionState(boolean enter, boolean scaleUp) {
        if (enter) {
            if (scaleUp) {
                return 0;
            }
            return 2;
        } else if (scaleUp) {
            return 1;
        } else {
            return 3;
        }
    }

    private static Animation loadTranslateYAnimation(boolean enter, float fromY, float toY) {
        PathInterpolator pathInterpolator;
        TranslateYAnimation animation = new TranslateYAnimation(fromY, toY);
        if (enter) {
            pathInterpolator = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
        } else {
            pathInterpolator = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
        }
        animation.setInterpolator(pathInterpolator);
        animation.setDuration(300);
        animation.setZAdjustment(1);
        return animation;
    }

    private static Animation loadTopWindowAnimation(boolean enter, Rect containingFrame) {
        ClipRectAnimation clipRectAnimation;
        PathInterpolator pathInterpolator;
        int height = containingFrame.height();
        int width = containingFrame.width();
        if (enter) {
            clipRectAnimation = new ClipRectAnimation(0, 0, width, 1, 0, 0, width, height);
        } else {
            clipRectAnimation = new ClipRectAnimation(0, 0, width, height, 0, 0, width, 1);
        }
        ClipRectAnimation a = clipRectAnimation;
        a.setDuration(300);
        if (enter) {
            pathInterpolator = new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f);
        } else {
            pathInterpolator = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
        }
        a.setInterpolator(pathInterpolator);
        a.setZAdjustment(1);
        return a;
    }

    private static Animation loadClipRevealAnimation(Rect containingFrame, Rect positionRect) {
        int clipStartY;
        int translationY;
        Rect rect = containingFrame;
        Rect rect2 = positionRect;
        int clipRevealStartX = rect2.left;
        int clipRevealStartY = rect2.top;
        int clipRevealStartWidth = positionRect.width();
        int clipRevealStartHeight = positionRect.height();
        int appWidth = containingFrame.width();
        int appHeight = containingFrame.height();
        int halfWidth = clipRevealStartWidth / 2;
        int halfHeight = clipRevealStartHeight / 2;
        int centerY = clipRevealStartY + halfHeight;
        int clipStartX = ((clipRevealStartX + halfWidth) - halfWidth) - rect.left;
        int clipStartY2 = (centerY - halfHeight) - rect.top;
        if (rect.top > centerY - halfHeight) {
            translationY = (centerY - halfHeight) - rect.top;
            clipStartY = 0;
        } else {
            translationY = 0;
            clipStartY = clipStartY2;
        }
        int appHeight2 = appHeight;
        Animation clipAnim = new ClipRectAnimation(clipStartX, clipStartY, clipStartX + clipRevealStartWidth, clipStartY + clipRevealStartHeight, 0, 0, appWidth, appHeight);
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(clipAnim);
        int translationY2 = translationY;
        if (translationY2 != 0) {
            set.addAnimation(new TranslateYAnimation((float) translationY2, 0.0f));
        }
        set.setDuration(300);
        set.setZAdjustment(1);
        set.setInterpolator(new PathInterpolator(0.0f, 0.0f, 0.2f, 1.0f));
        int appHeight3 = appHeight2;
        set.initialize(appWidth, appHeight3, appWidth, appHeight3);
        return set;
    }

    private static Animation prepareThumbnailAnimationWithDuration(Animation a, int appWidth, int appHeight, long duration, Interpolator interpolator) {
        if (a == null) {
            return null;
        }
        if (duration > 0) {
            a.setDuration(duration);
        }
        if (interpolator != null) {
            a.setInterpolator(interpolator);
        }
        a.setFillAfter(true);
        a.initialize(appWidth, appHeight, appWidth, appHeight);
        return a;
    }

    static void adjustCropToStackBounds(Rect targetRect, Rect stackBounds, int surfaceX, int surfaceY, float scaleX, float scaleY) {
        if (targetRect != null && stackBounds != null) {
            targetRect.offset(surfaceX, surfaceY);
            Rect visibleRect = new Rect(targetRect.left, targetRect.top, (int) (((float) targetRect.left) + (((float) targetRect.width()) * scaleX)), (int) (((float) targetRect.top) + (((float) targetRect.height()) * scaleY)));
            if (visibleRect.left >= stackBounds.right || stackBounds.left >= visibleRect.right || visibleRect.top >= stackBounds.bottom || stackBounds.top >= visibleRect.bottom) {
                targetRect.set(INVALID_CLIP_RECT);
                return;
            }
            if (visibleRect.left < stackBounds.left) {
                visibleRect.left = stackBounds.left;
                targetRect.left += (int) (((float) (stackBounds.left - visibleRect.left)) / scaleX);
            }
            if (visibleRect.right > stackBounds.right) {
                visibleRect.right = stackBounds.right;
                targetRect.right = (int) (((float) targetRect.left) + (((float) visibleRect.width()) / scaleX));
            }
            if (visibleRect.top < stackBounds.top) {
                visibleRect.top = stackBounds.top;
                targetRect.top = stackBounds.top;
            }
            if (visibleRect.bottom > stackBounds.bottom) {
                visibleRect.bottom = stackBounds.bottom;
                targetRect.bottom = (int) (((float) targetRect.top) + (((float) visibleRect.height()) / scaleY));
            }
            targetRect.offset(-surfaceX, -surfaceY);
        }
    }
}
