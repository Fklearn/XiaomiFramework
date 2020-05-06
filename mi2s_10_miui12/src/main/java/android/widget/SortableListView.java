package android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.android.server.wifi.hotspot2.anqp.Constants;
import com.miui.system.internal.R;

public class SortableListView extends MiuiListView {
    private static final int ANIMATION_DURATION = 200;
    private static final float SCROLL_BOUND = 0.25f;
    private static final int SCROLL_SPEED_MAX = 16;
    private static final int SNAPSHOT_ALPHA = 153;
    private static final String TAG = "SortableListView";
    /* access modifiers changed from: private */
    public int mDraggingFrom;
    /* access modifiers changed from: private */
    public int mDraggingItemHeight;
    /* access modifiers changed from: private */
    public int mDraggingItemWidth;
    /* access modifiers changed from: private */
    public int mDraggingTo;
    /* access modifiers changed from: private */
    public int mDraggingY;
    /* access modifiers changed from: private */
    public boolean mInterceptTouchForSorting;
    private int mItemUpperBound;
    /* access modifiers changed from: private */
    public int mOffsetYInDraggingItem;
    /* access modifiers changed from: private */
    public OnOrderChangedListener mOnOrderChangedListener;
    private View.OnTouchListener mOnTouchListener;
    private int mScrollBound;
    private int mScrollLowerBound;
    private int mScrollUpperBound;
    /* access modifiers changed from: private */
    public BitmapDrawable mSnapshot;
    /* access modifiers changed from: private */
    public Drawable mSnapshotBackgroundForOverUpperBound;
    /* access modifiers changed from: private */
    public Drawable mSnapshotShadow;
    /* access modifiers changed from: private */
    public int mSnapshotShadowPaddingBottom;
    /* access modifiers changed from: private */
    public int mSnapshotShadowPaddingTop;
    /* access modifiers changed from: private */
    public int[] mTmpLocation;

    public interface OnOrderChangedListener {
        void OnOrderChanged(int i, int i2);
    }

    public SortableListView(Context context) {
        this(context, (AttributeSet) null);
    }

    public SortableListView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842868);
    }

    public SortableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDraggingFrom = -1;
        this.mDraggingTo = -1;
        this.mItemUpperBound = -1;
        this.mTmpLocation = new int[2];
        this.mSnapshotShadow = context.getResources().getDrawable(R.drawable.sortable_list_dragging_item_shadow);
        this.mSnapshotShadow.setAlpha(SNAPSHOT_ALPHA);
        Rect padding = new Rect();
        this.mSnapshotShadow.getPadding(padding);
        this.mSnapshotShadowPaddingTop = padding.top;
        this.mSnapshotShadowPaddingBottom = padding.bottom;
        this.mOnTouchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int position;
                if (SortableListView.this.mOnOrderChangedListener != null && (event.getAction() & Constants.BYTE_MASK) == 0 && (position = SortableListView.this.getHittenItemPosition(event)) >= 0) {
                    int unused = SortableListView.this.mDraggingFrom = position;
                    int unused2 = SortableListView.this.mDraggingTo = position;
                    boolean unused3 = SortableListView.this.mInterceptTouchForSorting = true;
                    SortableListView sortableListView = SortableListView.this;
                    View view = sortableListView.getChildAt(position - sortableListView.getFirstVisiblePosition());
                    int unused4 = SortableListView.this.mDraggingItemWidth = view.getWidth();
                    int unused5 = SortableListView.this.mDraggingItemHeight = view.getHeight();
                    SortableListView sortableListView2 = SortableListView.this;
                    sortableListView2.getLocationOnScreen(sortableListView2.mTmpLocation);
                    int unused6 = SortableListView.this.mDraggingY = ((int) event.getRawY()) - SortableListView.this.mTmpLocation[1];
                    SortableListView sortableListView3 = SortableListView.this;
                    int unused7 = sortableListView3.mOffsetYInDraggingItem = sortableListView3.mDraggingY - view.getTop();
                    Bitmap snapshot = Bitmap.createBitmap(SortableListView.this.mDraggingItemWidth, SortableListView.this.mDraggingItemHeight, Bitmap.Config.ARGB_8888);
                    view.draw(new Canvas(snapshot));
                    SortableListView sortableListView4 = SortableListView.this;
                    BitmapDrawable unused8 = sortableListView4.mSnapshot = new BitmapDrawable(sortableListView4.getResources(), snapshot);
                    SortableListView.this.mSnapshot.setAlpha(SortableListView.SNAPSHOT_ALPHA);
                    SortableListView.this.mSnapshot.setBounds(view.getLeft(), 0, view.getRight(), SortableListView.this.mDraggingItemHeight);
                    if (SortableListView.this.mSnapshotBackgroundForOverUpperBound != null) {
                        SortableListView.this.mSnapshotBackgroundForOverUpperBound.setAlpha(SortableListView.SNAPSHOT_ALPHA);
                        SortableListView.this.mSnapshotBackgroundForOverUpperBound.setBounds(view.getLeft(), 0, view.getRight(), SortableListView.this.mDraggingItemHeight);
                    }
                    SortableListView.this.mSnapshotShadow.setBounds(view.getLeft(), -SortableListView.this.mSnapshotShadowPaddingTop, view.getRight(), SortableListView.this.mDraggingItemHeight + SortableListView.this.mSnapshotShadowPaddingBottom);
                    SortableListView sortableListView5 = SortableListView.this;
                    view.startAnimation(sortableListView5.createAnimation(sortableListView5.mDraggingItemWidth, SortableListView.this.mDraggingItemWidth, 0, 0));
                }
                return SortableListView.this.mInterceptTouchForSorting;
            }
        };
    }

    /* access modifiers changed from: private */
    public Animation createAnimation(int fromX, int toX, int fromY, int toY) {
        Animation result = new TranslateAnimation((float) fromX, (float) toX, (float) fromY, (float) toY);
        result.setDuration(200);
        result.setFillAfter(true);
        return result;
    }

    public void setItemUpperBound(int upper, Drawable snapshotShadow) {
        this.mItemUpperBound = upper;
        this.mSnapshotBackgroundForOverUpperBound = snapshotShadow;
    }

    public void setOnOrderChangedListener(OnOrderChangedListener l) {
        this.mOnOrderChangedListener = l;
    }

    public View.OnTouchListener getListenerForStartingSort() {
        return this.mOnTouchListener;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mScrollBound = Math.max(1, (int) (((float) h) * SCROLL_BOUND));
        int i = this.mScrollBound;
        this.mScrollUpperBound = i;
        this.mScrollLowerBound = h - i;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mDraggingFrom >= 0) {
            int offset = this.mDraggingY - this.mOffsetYInDraggingItem;
            int index = getHeaderViewsCount();
            if (index < getFirstVisiblePosition() || index > getLastVisiblePosition()) {
                index = getFirstVisiblePosition();
            }
            int offset2 = Math.max(offset, getChildAt(index - getFirstVisiblePosition()).getTop());
            int index2 = (getCount() - 1) - getFooterViewsCount();
            if (index2 < getFirstVisiblePosition() || index2 > getLastVisiblePosition()) {
                index2 = getLastVisiblePosition();
            }
            int offset3 = Math.min(offset2, getChildAt(index2 - getFirstVisiblePosition()).getBottom() - this.mDraggingItemHeight);
            canvas.translate(0.0f, (float) offset3);
            this.mSnapshotShadow.draw(canvas);
            this.mSnapshot.draw(canvas);
            Drawable drawable = this.mSnapshotBackgroundForOverUpperBound;
            if (drawable != null && this.mDraggingTo < this.mItemUpperBound) {
                drawable.draw(canvas);
            }
            canvas.translate(0.0f, (float) (-offset3));
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!this.mInterceptTouchForSorting) {
            return super.onInterceptTouchEvent(ev);
        }
        requestDisallowInterceptTouchEvent(true);
        onTouchEvent(ev);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0019, code lost:
        if (r0 != 5) goto L_0x00aa;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        /*
            r6 = this;
            boolean r0 = r6.mInterceptTouchForSorting
            if (r0 != 0) goto L_0x0009
            boolean r0 = super.onTouchEvent(r7)
            return r0
        L_0x0009:
            int r0 = r7.getAction()
            r0 = r0 & 255(0xff, float:3.57E-43)
            r1 = 1
            if (r0 == r1) goto L_0x007a
            r2 = 2
            if (r0 == r2) goto L_0x001d
            r2 = 3
            if (r0 == r2) goto L_0x007a
            r2 = 5
            if (r0 == r2) goto L_0x007a
            goto L_0x00aa
        L_0x001d:
            float r0 = r7.getY()
            int r0 = (int) r0
            boolean r2 = r6.mInterceptTouchForSorting
            if (r2 != 0) goto L_0x002c
            int r2 = r6.mDraggingY
            if (r0 != r2) goto L_0x002c
            goto L_0x00aa
        L_0x002c:
            int r2 = r6.getHittenItemPosition(r7)
            int r3 = r6.getHeaderViewsCount()
            if (r2 < r3) goto L_0x0041
            int r3 = r6.getCount()
            int r4 = r6.getFooterViewsCount()
            int r3 = r3 - r4
            if (r2 <= r3) goto L_0x0043
        L_0x0041:
            int r2 = r6.mDraggingTo
        L_0x0043:
            r6.updateDraggingToPisition(r2)
            r6.mDraggingY = r0
            r6.invalidate()
            r3 = 0
            int r4 = r6.mScrollLowerBound
            if (r0 <= r4) goto L_0x0058
            int r4 = r4 - r0
            int r4 = r4 * 16
            int r5 = r6.mScrollBound
            int r3 = r4 / r5
            goto L_0x0063
        L_0x0058:
            int r4 = r6.mScrollUpperBound
            if (r0 >= r4) goto L_0x0063
            int r4 = r4 - r0
            int r4 = r4 * 16
            int r5 = r6.mScrollBound
            int r3 = r4 / r5
        L_0x0063:
            if (r3 == 0) goto L_0x00aa
            int r4 = r6.getFirstVisiblePosition()
            int r4 = r2 - r4
            android.view.View r4 = r6.getChildAt(r4)
            if (r4 == 0) goto L_0x0079
            int r5 = r4.getTop()
            int r5 = r5 + r3
            r6.setSelectionFromTop(r2, r5)
        L_0x0079:
            goto L_0x00aa
        L_0x007a:
            int r0 = r6.mDraggingFrom
            if (r0 < 0) goto L_0x009f
            android.widget.SortableListView$OnOrderChangedListener r2 = r6.mOnOrderChangedListener
            if (r2 == 0) goto L_0x0099
            int r3 = r6.mDraggingTo
            if (r0 == r3) goto L_0x0099
            if (r3 < 0) goto L_0x0099
            int r3 = r6.getHeaderViewsCount()
            int r0 = r0 - r3
            int r3 = r6.mDraggingTo
            int r4 = r6.getHeaderViewsCount()
            int r3 = r3 - r4
            r2.OnOrderChanged(r0, r3)
            goto L_0x009f
        L_0x0099:
            int r0 = r6.mDraggingFrom
            r2 = 0
            r6.setViewAnimationByPisition(r0, r2)
        L_0x009f:
            r0 = 0
            r6.mInterceptTouchForSorting = r0
            r0 = -1
            r6.mDraggingFrom = r0
            r6.mDraggingTo = r0
            r6.invalidate()
        L_0x00aa:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.SortableListView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: private */
    public int getHittenItemPosition(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        int firstPosition = getFirstVisiblePosition();
        for (int i = getLastVisiblePosition(); i >= firstPosition; i--) {
            View view = getChildAt(i - firstPosition);
            if (view != null) {
                view.getLocationOnScreen(this.mTmpLocation);
                int[] iArr = this.mTmpLocation;
                if (((float) iArr[0]) <= x && ((float) (iArr[0] + view.getWidth())) >= x) {
                    int[] iArr2 = this.mTmpLocation;
                    if (((float) iArr2[1]) <= y && ((float) (iArr2[1] + view.getHeight())) >= y) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private void updateDraggingToPisition(int draggingTo) {
        if (draggingTo != this.mDraggingTo && draggingTo >= 0) {
            Log.d(TAG, "sort item from " + this.mDraggingFrom + " To " + draggingTo);
            if (this.mDraggingFrom < Math.max(this.mDraggingTo, draggingTo)) {
                while (true) {
                    int i = this.mDraggingTo;
                    if (i <= draggingTo || i <= this.mDraggingFrom) {
                        break;
                    }
                    Log.d(TAG, "item " + this.mDraggingTo + " set move down reverse animation");
                    int i2 = this.mDraggingTo;
                    this.mDraggingTo = i2 + -1;
                    setViewAnimationByPisition(i2, createAnimation(0, 0, -this.mDraggingItemHeight, 0));
                }
            }
            if (this.mDraggingFrom > Math.min(this.mDraggingTo, draggingTo)) {
                while (true) {
                    int i3 = this.mDraggingTo;
                    if (i3 >= draggingTo || i3 >= this.mDraggingFrom) {
                        break;
                    }
                    Log.d(TAG, "item " + this.mDraggingTo + " set move up reverse animation");
                    int i4 = this.mDraggingTo;
                    this.mDraggingTo = i4 + 1;
                    setViewAnimationByPisition(i4, createAnimation(0, 0, this.mDraggingItemHeight, 0));
                }
            }
            if (this.mDraggingFrom < Math.max(this.mDraggingTo, draggingTo)) {
                while (true) {
                    int i5 = this.mDraggingTo;
                    if (i5 >= draggingTo) {
                        break;
                    }
                    int i6 = i5 + 1;
                    this.mDraggingTo = i6;
                    setViewAnimationByPisition(i6, createAnimation(0, 0, 0, -this.mDraggingItemHeight));
                    Log.d(TAG, "item " + this.mDraggingTo + " set move up animation");
                }
            }
            if (this.mDraggingFrom > Math.min(this.mDraggingTo, draggingTo)) {
                while (true) {
                    int i7 = this.mDraggingTo;
                    if (i7 > draggingTo) {
                        int i8 = i7 - 1;
                        this.mDraggingTo = i8;
                        setViewAnimationByPisition(i8, createAnimation(0, 0, 0, this.mDraggingItemHeight));
                        Log.d(TAG, "item " + this.mDraggingTo + " set move down animation");
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private void setViewAnimationByPisition(int position, Animation animation) {
        setViewAnimation(getChildAt(position - getFirstVisiblePosition()), animation);
    }

    private void setViewAnimation(View view, Animation animation) {
        if (view != null) {
            if (animation != null) {
                view.startAnimation(animation);
            } else {
                view.clearAnimation();
            }
        }
    }

    /* access modifiers changed from: protected */
    public View obtainView(int position, boolean[] isScrap) {
        View view = super.obtainView(position, isScrap);
        Animation animation = null;
        int i = this.mDraggingFrom;
        if (i == position) {
            int i2 = this.mDraggingItemWidth;
            animation = createAnimation(i2, i2, 0, 0);
            Log.d(TAG, "item " + position + " set move out animation");
        } else if (i < position && position <= this.mDraggingTo) {
            animation = createAnimation(0, 0, 0, -this.mDraggingItemHeight);
            Log.d(TAG, "item " + position + " set move up animation");
        } else if (this.mDraggingFrom > position && position >= this.mDraggingTo) {
            animation = createAnimation(0, 0, 0, this.mDraggingItemHeight);
            Log.d(TAG, "item " + position + " set move down animation");
        }
        setViewAnimation(view, animation);
        return view;
    }
}
