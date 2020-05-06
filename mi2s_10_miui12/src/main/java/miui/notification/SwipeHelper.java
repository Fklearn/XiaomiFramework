package miui.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import com.android.server.wifi.scanner.ChannelHelper;

public class SwipeHelper implements Gefingerpoken {
    static final float ALPHA_FADE_END = 0.5f;
    public static float ALPHA_FADE_START = 0.0f;
    private static final boolean CONSTRAIN_SWIPE = true;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_INVALIDATE = false;
    private static final boolean DISMISS_IF_SWIPED_FAR_ENOUGH = true;
    private static final boolean FADE_OUT_DURING_SWIPE = true;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final int SNAP_ANIM_LEN = 150;
    static final String TAG = "com.android.systemui.SwipeHelper";
    public static final int X = 0;
    public static final int Y = 1;
    private static LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    private int DEFAULT_ESCAPE_ANIMATION_DURATION = ChannelHelper.SCAN_PERIOD_PER_CHANNEL_MS;
    private int MAX_DISMISS_VELOCITY = 2000;
    private int MAX_ESCAPE_ANIMATION_DURATION = 400;
    private float SWIPE_ESCAPE_VELOCITY = 100.0f;
    /* access modifiers changed from: private */
    public Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private View mCurrAnimView;
    /* access modifiers changed from: private */
    public View mCurrView;
    private float mDensityScale;
    private boolean mDragging;
    private Handler mHandler;
    private float mInitialTouchPos;
    /* access modifiers changed from: private */
    public View.OnLongClickListener mLongPressListener;
    /* access modifiers changed from: private */
    public boolean mLongPressSent;
    private long mLongPressTimeout;
    private float mMinAlpha = 0.0f;
    private float mPagingTouchSlop;
    private int mSwipeDirection;
    private VelocityTracker mVelocityTracker;
    private Runnable mWatchLongPress;

    public interface Callback {
        boolean canChildBeDismissed(View view);

        View getChildAtPosition(MotionEvent motionEvent);

        View getChildContentView(View view);

        void onBeginDrag(View view);

        void onChildDismissed(View view);

        void onDragCancelled(View view);
    }

    public SwipeHelper(int swipeDirection, Callback callback, float densityScale, float pagingTouchSlop) {
        this.mCallback = callback;
        this.mHandler = new Handler();
        this.mSwipeDirection = swipeDirection;
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mDensityScale = densityScale;
        this.mPagingTouchSlop = pagingTouchSlop;
        this.mLongPressTimeout = (long) (((float) ViewConfiguration.getLongPressTimeout()) * 1.5f);
    }

    public void setLongPressListener(View.OnLongClickListener listener) {
        this.mLongPressListener = listener;
    }

    public void setDensityScale(float densityScale) {
        this.mDensityScale = densityScale;
    }

    public void setPagingTouchSlop(float pagingTouchSlop) {
        this.mPagingTouchSlop = pagingTouchSlop;
    }

    private float getPos(MotionEvent ev) {
        return this.mSwipeDirection == 0 ? ev.getX() : ev.getY();
    }

    private float getTranslation(View v) {
        return this.mSwipeDirection == 0 ? v.getTranslationX() : v.getTranslationY();
    }

    private float getVelocity(VelocityTracker vt) {
        if (this.mSwipeDirection == 0) {
            return vt.getXVelocity();
        }
        return vt.getYVelocity();
    }

    private ObjectAnimator createTranslationAnimation(View v, float newPos) {
        return ObjectAnimator.ofFloat(v, this.mSwipeDirection == 0 ? "translationX" : "translationY", new float[]{newPos});
    }

    private float getPerpendicularVelocity(VelocityTracker vt) {
        if (this.mSwipeDirection == 0) {
            return vt.getYVelocity();
        }
        return vt.getXVelocity();
    }

    private void setTranslation(View v, float translate) {
        if (this.mSwipeDirection == 0) {
            v.setTranslationX(translate);
        } else {
            v.setTranslationY(translate);
        }
    }

    private float getSize(View v) {
        if (this.mSwipeDirection == 0) {
            return (float) v.getMeasuredWidth();
        }
        return (float) v.getMeasuredHeight();
    }

    public void setMinAlpha(float minAlpha) {
        this.mMinAlpha = minAlpha;
    }

    /* access modifiers changed from: private */
    public float getAlphaForOffset(View view) {
        float viewSize = getSize(view);
        float fadeSize = ALPHA_FADE_END * viewSize;
        float result = 1.0f;
        float pos = getTranslation(view);
        float f = ALPHA_FADE_START;
        if (pos >= viewSize * f) {
            result = 1.0f - ((pos - (f * viewSize)) / fadeSize);
        } else if (pos < (1.0f - f) * viewSize) {
            result = (((f * viewSize) + pos) / fadeSize) + 1.0f;
        }
        return Math.max(this.mMinAlpha, result);
    }

    public static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom()));
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void invalidateGlobalRegion(android.view.View r5, android.graphics.RectF r6) {
        /*
        L_0x0000:
            android.view.ViewParent r0 = r5.getParent()
            if (r0 == 0) goto L_0x0040
            android.view.ViewParent r0 = r5.getParent()
            boolean r0 = r0 instanceof android.view.View
            if (r0 == 0) goto L_0x0040
            android.view.ViewParent r0 = r5.getParent()
            r5 = r0
            android.view.View r5 = (android.view.View) r5
            android.graphics.Matrix r0 = r5.getMatrix()
            r0.mapRect(r6)
            float r0 = r6.left
            double r0 = (double) r0
            double r0 = java.lang.Math.floor(r0)
            int r0 = (int) r0
            float r1 = r6.top
            double r1 = (double) r1
            double r1 = java.lang.Math.floor(r1)
            int r1 = (int) r1
            float r2 = r6.right
            double r2 = (double) r2
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            float r3 = r6.bottom
            double r3 = (double) r3
            double r3 = java.lang.Math.ceil(r3)
            int r3 = (int) r3
            r5.invalidate(r0, r1, r2, r3)
            goto L_0x0000
        L_0x0040:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.notification.SwipeHelper.invalidateGlobalRegion(android.view.View, android.graphics.RectF):void");
    }

    public void removeLongPressCallback() {
        Runnable runnable = this.mWatchLongPress;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mWatchLongPress = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000e, code lost:
        if (r0 != 3) goto L_0x00a2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            int r0 = r7.getAction()
            r1 = 0
            if (r0 == 0) goto L_0x0058
            r2 = 1
            if (r0 == r2) goto L_0x004b
            r3 = 2
            if (r0 == r3) goto L_0x0012
            r2 = 3
            if (r0 == r2) goto L_0x004b
            goto L_0x00a2
        L_0x0012:
            android.view.View r1 = r6.mCurrView
            if (r1 == 0) goto L_0x00a2
            boolean r1 = r6.mLongPressSent
            if (r1 != 0) goto L_0x00a2
            android.view.VelocityTracker r1 = r6.mVelocityTracker
            r1.addMovement(r7)
            float r1 = r6.getPos(r7)
            float r3 = r6.mInitialTouchPos
            float r3 = r1 - r3
            float r4 = java.lang.Math.abs(r3)
            float r5 = r6.mPagingTouchSlop
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 <= 0) goto L_0x004a
            miui.notification.SwipeHelper$Callback r4 = r6.mCallback
            android.view.View r5 = r6.mCurrView
            r4.onBeginDrag(r5)
            r6.mDragging = r2
            float r2 = r6.getPos(r7)
            android.view.View r4 = r6.mCurrAnimView
            float r4 = r6.getTranslation(r4)
            float r2 = r2 - r4
            r6.mInitialTouchPos = r2
            r6.removeLongPressCallback()
        L_0x004a:
            goto L_0x00a2
        L_0x004b:
            r6.mDragging = r1
            r2 = 0
            r6.mCurrView = r2
            r6.mCurrAnimView = r2
            r6.mLongPressSent = r1
            r6.removeLongPressCallback()
            goto L_0x00a2
        L_0x0058:
            r6.mDragging = r1
            r6.mLongPressSent = r1
            miui.notification.SwipeHelper$Callback r1 = r6.mCallback
            android.view.View r1 = r1.getChildAtPosition(r7)
            r6.mCurrView = r1
            android.view.VelocityTracker r1 = r6.mVelocityTracker
            r1.clear()
            android.view.View r1 = r6.mCurrView
            if (r1 == 0) goto L_0x00a2
            miui.notification.SwipeHelper$Callback r2 = r6.mCallback
            android.view.View r1 = r2.getChildContentView(r1)
            r6.mCurrAnimView = r1
            miui.notification.SwipeHelper$Callback r1 = r6.mCallback
            android.view.View r2 = r6.mCurrView
            boolean r1 = r1.canChildBeDismissed(r2)
            r6.mCanCurrViewBeDimissed = r1
            android.view.VelocityTracker r1 = r6.mVelocityTracker
            r1.addMovement(r7)
            float r1 = r6.getPos(r7)
            r6.mInitialTouchPos = r1
            android.view.View$OnLongClickListener r1 = r6.mLongPressListener
            if (r1 == 0) goto L_0x00a2
            java.lang.Runnable r1 = r6.mWatchLongPress
            if (r1 != 0) goto L_0x0099
            miui.notification.SwipeHelper$1 r1 = new miui.notification.SwipeHelper$1
            r1.<init>()
            r6.mWatchLongPress = r1
        L_0x0099:
            android.os.Handler r1 = r6.mHandler
            java.lang.Runnable r2 = r6.mWatchLongPress
            long r3 = r6.mLongPressTimeout
            r1.postDelayed(r2, r3)
        L_0x00a2:
            boolean r1 = r6.mDragging
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.notification.SwipeHelper.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    public void dismissChild(final View view, float velocity) {
        float newPos;
        int duration;
        final View animView = this.mCallback.getChildContentView(view);
        final boolean canAnimViewBeDismissed = this.mCallback.canChildBeDismissed(view);
        if (velocity < 0.0f || ((velocity == 0.0f && getTranslation(animView) < 0.0f) || (velocity == 0.0f && getTranslation(animView) == 0.0f && this.mSwipeDirection == 1))) {
            newPos = -getSize(animView);
        } else {
            newPos = getSize(animView);
        }
        int duration2 = this.MAX_ESCAPE_ANIMATION_DURATION;
        if (velocity != 0.0f) {
            duration = Math.min(duration2, (int) ((Math.abs(newPos - getTranslation(animView)) * 1000.0f) / Math.abs(velocity)));
        } else {
            duration = this.DEFAULT_ESCAPE_ANIMATION_DURATION;
        }
        animView.setLayerType(2, (Paint) null);
        ObjectAnimator anim = createTranslationAnimation(animView, newPos);
        anim.setInterpolator(sLinearInterpolator);
        anim.setDuration((long) duration);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                SwipeHelper.this.mCallback.onChildDismissed(view);
                animView.setLayerType(0, (Paint) null);
            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if (canAnimViewBeDismissed) {
                    View view = animView;
                    view.setAlpha(SwipeHelper.this.getAlphaForOffset(view));
                }
                SwipeHelper.invalidateGlobalRegion(animView);
            }
        });
        anim.start();
    }

    public void snapChild(View view, float velocity) {
        final View animView = this.mCallback.getChildContentView(view);
        final boolean canAnimViewBeDismissed = this.mCallback.canChildBeDismissed(animView);
        ObjectAnimator anim = createTranslationAnimation(animView, 0.0f);
        anim.setDuration((long) SNAP_ANIM_LEN);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                if (canAnimViewBeDismissed) {
                    View view = animView;
                    view.setAlpha(SwipeHelper.this.getAlphaForOffset(view));
                }
                SwipeHelper.invalidateGlobalRegion(animView);
            }
        });
        anim.start();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0022, code lost:
        if (r0 != 4) goto L_0x0113;
     */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0107  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
            r14 = this;
            boolean r0 = r14.mLongPressSent
            r1 = 1
            if (r0 == 0) goto L_0x0006
            return r1
        L_0x0006:
            boolean r0 = r14.mDragging
            r2 = 0
            if (r0 != 0) goto L_0x000f
            r14.removeLongPressCallback()
            return r2
        L_0x000f:
            android.view.VelocityTracker r0 = r14.mVelocityTracker
            r0.addMovement(r15)
            int r0 = r15.getAction()
            r3 = 0
            if (r0 == r1) goto L_0x007f
            r4 = 2
            if (r0 == r4) goto L_0x0026
            r4 = 3
            if (r0 == r4) goto L_0x007f
            r2 = 4
            if (r0 == r2) goto L_0x0026
            goto L_0x0113
        L_0x0026:
            android.view.View r2 = r14.mCurrView
            if (r2 == 0) goto L_0x0113
            float r2 = r14.getPos(r15)
            float r4 = r14.mInitialTouchPos
            float r2 = r2 - r4
            miui.notification.SwipeHelper$Callback r4 = r14.mCallback
            android.view.View r5 = r14.mCurrView
            boolean r4 = r4.canChildBeDismissed(r5)
            if (r4 != 0) goto L_0x0066
            android.view.View r4 = r14.mCurrAnimView
            float r4 = r14.getSize(r4)
            r5 = 1041865114(0x3e19999a, float:0.15)
            float r5 = r5 * r4
            float r6 = java.lang.Math.abs(r2)
            int r6 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r6 < 0) goto L_0x0056
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x0053
            r3 = r5
            goto L_0x0054
        L_0x0053:
            float r3 = -r5
        L_0x0054:
            r2 = r3
            goto L_0x0066
        L_0x0056:
            float r3 = r2 / r4
            double r6 = (double) r3
            r8 = 4609753056924675352(0x3ff921fb54442d18, double:1.5707963267948966)
            double r6 = r6 * r8
            double r6 = java.lang.Math.sin(r6)
            float r3 = (float) r6
            float r2 = r5 * r3
        L_0x0066:
            android.view.View r3 = r14.mCurrAnimView
            r14.setTranslation(r3, r2)
            boolean r3 = r14.mCanCurrViewBeDimissed
            if (r3 == 0) goto L_0x0078
            android.view.View r3 = r14.mCurrAnimView
            float r4 = r14.getAlphaForOffset(r3)
            r3.setAlpha(r4)
        L_0x0078:
            android.view.View r3 = r14.mCurrView
            invalidateGlobalRegion(r3)
            goto L_0x0113
        L_0x007f:
            android.view.View r4 = r14.mCurrView
            if (r4 == 0) goto L_0x0113
            int r4 = r14.MAX_DISMISS_VELOCITY
            float r4 = (float) r4
            float r5 = r14.mDensityScale
            float r4 = r4 * r5
            android.view.VelocityTracker r5 = r14.mVelocityTracker
            r6 = 1000(0x3e8, float:1.401E-42)
            r5.computeCurrentVelocity(r6, r4)
            float r5 = r14.SWIPE_ESCAPE_VELOCITY
            float r6 = r14.mDensityScale
            float r5 = r5 * r6
            android.view.VelocityTracker r6 = r14.mVelocityTracker
            float r6 = r14.getVelocity(r6)
            android.view.VelocityTracker r7 = r14.mVelocityTracker
            float r7 = r14.getPerpendicularVelocity(r7)
            android.view.View r8 = r14.mCurrAnimView
            float r8 = r14.getTranslation(r8)
            float r8 = java.lang.Math.abs(r8)
            double r8 = (double) r8
            r10 = 4600877379321698714(0x3fd999999999999a, double:0.4)
            android.view.View r12 = r14.mCurrAnimView
            float r12 = r14.getSize(r12)
            double r12 = (double) r12
            double r12 = r12 * r10
            int r8 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r8 <= 0) goto L_0x00bf
            r8 = r1
            goto L_0x00c0
        L_0x00bf:
            r8 = r2
        L_0x00c0:
            float r9 = java.lang.Math.abs(r6)
            int r9 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r9 <= 0) goto L_0x00ec
            float r9 = java.lang.Math.abs(r6)
            float r10 = java.lang.Math.abs(r7)
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 <= 0) goto L_0x00ec
            int r9 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r9 <= 0) goto L_0x00da
            r9 = r1
            goto L_0x00db
        L_0x00da:
            r9 = r2
        L_0x00db:
            android.view.View r10 = r14.mCurrAnimView
            float r10 = r14.getTranslation(r10)
            int r10 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r10 <= 0) goto L_0x00e7
            r10 = r1
            goto L_0x00e8
        L_0x00e7:
            r10 = r2
        L_0x00e8:
            if (r9 != r10) goto L_0x00ec
            r9 = r1
            goto L_0x00ed
        L_0x00ec:
            r9 = r2
        L_0x00ed:
            miui.notification.SwipeHelper$Callback r10 = r14.mCallback
            android.view.View r11 = r14.mCurrView
            boolean r10 = r10.canChildBeDismissed(r11)
            if (r10 == 0) goto L_0x00fc
            if (r9 != 0) goto L_0x00fb
            if (r8 == 0) goto L_0x00fc
        L_0x00fb:
            r2 = r1
        L_0x00fc:
            if (r2 == 0) goto L_0x0107
            android.view.View r10 = r14.mCurrView
            if (r9 == 0) goto L_0x0103
            r3 = r6
        L_0x0103:
            r14.dismissChild(r10, r3)
            goto L_0x0113
        L_0x0107:
            miui.notification.SwipeHelper$Callback r3 = r14.mCallback
            android.view.View r10 = r14.mCurrView
            r3.onDragCancelled(r10)
            android.view.View r3 = r14.mCurrView
            r14.snapChild(r3, r6)
        L_0x0113:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.notification.SwipeHelper.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
