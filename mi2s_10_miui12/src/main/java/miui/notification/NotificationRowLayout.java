package miui.notification;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import com.miui.system.internal.R;
import java.util.HashMap;
import miui.notification.SwipeHelper;

public class NotificationRowLayout extends LinearLayout implements SwipeHelper.Callback {
    private static final int APPEAR_ANIM_LEN = 250;
    private static final boolean DEBUG = false;
    private static final int DISAPPEAR_ANIM_LEN = 250;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final String TAG = "NotificationRowLayout";
    boolean mAnimateBounds;
    HashMap<View, ValueAnimator> mAppearingViews;
    private Context mContext;
    HashMap<View, ValueAnimator> mDisappearingViews;
    private LayoutTransition mRealLayoutTransition;
    boolean mRemoveViews;
    private SwipeHelper mSwipeHelper;
    Rect mTmpRect;

    public NotificationRowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationRowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimateBounds = true;
        this.mTmpRect = new Rect();
        this.mAppearingViews = new HashMap<>();
        this.mDisappearingViews = new HashMap<>();
        this.mRemoveViews = true;
        this.mContext = context;
        this.mRealLayoutTransition = new LayoutTransition();
        this.mRealLayoutTransition.setAnimateParentHierarchy(true);
        setLayoutTransitionsEnabled(true);
        setOrientation(1);
        this.mSwipeHelper = new SwipeHelper(0, this, getResources().getDisplayMetrics().density, (float) ViewConfiguration.get(this.mContext).getScaledPagingTouchSlop());
    }

    public void setLongPressListener(View.OnLongClickListener listener) {
        this.mSwipeHelper.setLongPressListener(listener);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            this.mSwipeHelper.removeLongPressCallback();
        }
    }

    public void setAnimateBounds(boolean anim) {
        this.mAnimateBounds = anim;
    }

    private void logLayoutTransition() {
        StringBuilder sb = new StringBuilder();
        sb.append("layout ");
        sb.append(this.mRealLayoutTransition.isChangingLayout() ? "is " : "is not ");
        sb.append("in transition and animations ");
        sb.append(this.mRealLayoutTransition.isRunning() ? "are " : "are not ");
        sb.append("running.");
        Log.v(TAG, sb.toString());
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.mSwipeHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.mSwipeHelper.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    public boolean canChildBeDismissed(View v) {
        View veto = v.findViewById(R.id.veto);
        return (veto == null || veto.getVisibility() == 8) ? false : true;
    }

    public void onChildDismissed(View v) {
        View veto = v.findViewById(R.id.veto);
        if (veto != null && veto.getVisibility() != 8 && this.mRemoveViews) {
            veto.performClick();
        }
    }

    public void onBeginDrag(View v) {
        requestDisallowInterceptTouchEvent(true);
    }

    public void onDragCancelled(View v) {
    }

    public View getChildAtPosition(MotionEvent ev) {
        return getChildAtPosition(ev.getX(), ev.getY());
    }

    public View getChildAtRawPosition(float touchX, float touchY) {
        int[] location = new int[2];
        getLocationOnScreen(location);
        return getChildAtPosition(touchX - ((float) location[0]), touchY - ((float) location[1]));
    }

    public View getChildAtPosition(float touchX, float touchY) {
        int count = getChildCount();
        int y = 0;
        for (int childIdx = 0; childIdx < count; childIdx++) {
            View slidingChild = getChildAt(childIdx);
            if (slidingChild.getVisibility() != 8) {
                y += slidingChild.getMeasuredHeight();
                if (touchY > 0.0f && touchY < ((float) y)) {
                    return slidingChild;
                }
            }
        }
        return null;
    }

    public View getChildContentView(View v) {
        return v;
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mSwipeHelper.setDensityScale(getResources().getDisplayMetrics().density);
        this.mSwipeHelper.setPagingTouchSlop((float) ViewConfiguration.get(this.mContext).getScaledPagingTouchSlop());
    }

    public void setViewRemoval(boolean removeViews) {
        this.mRemoveViews = removeViews;
    }

    public void setLayoutTransitionsEnabled(boolean b) {
        if (b) {
            setLayoutTransition(this.mRealLayoutTransition);
            return;
        }
        if (this.mRealLayoutTransition.isRunning()) {
            this.mRealLayoutTransition.endChangingAnimations();
            this.mRealLayoutTransition.cancel();
        }
        setLayoutTransition((LayoutTransition) null);
    }

    public void dismissRowAnimated(View child) {
        dismissRowAnimated(child, 0);
    }

    public void dismissRowAnimated(View child, int vel) {
        this.mSwipeHelper.dismissChild(child, (float) vel);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void onDraw(Canvas c) {
        super.onDraw(c);
    }
}
