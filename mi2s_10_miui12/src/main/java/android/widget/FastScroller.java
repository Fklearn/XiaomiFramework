package android.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.IntProperty;
import android.util.MathUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroupOverlay;
import com.android.internal.R.styleable;

class FastScroller
{
  private static Property<View, Integer> BOTTOM = new IntProperty("bottom")
  {
    public Integer get(View paramAnonymousView)
    {
      return Integer.valueOf(paramAnonymousView.getBottom());
    }
    
    public void setValue(View paramAnonymousView, int paramAnonymousInt)
    {
      paramAnonymousView.setBottom(paramAnonymousInt);
    }
  };
  private static final int DURATION_CROSS_FADE = 50;
  private static final int DURATION_FADE_IN = 150;
  private static final int DURATION_FADE_OUT = 300;
  private static final int DURATION_RESIZE = 100;
  private static final long FADE_TIMEOUT = 1500L;
  private static Property<View, Integer> LEFT;
  private static final int MIN_PAGES = 4;
  private static final int OVERLAY_ABOVE_THUMB = 2;
  private static final int OVERLAY_AT_THUMB = 1;
  private static final int OVERLAY_FLOATING = 0;
  private static final int PREVIEW_LEFT = 0;
  private static final int PREVIEW_RIGHT = 1;
  private static Property<View, Integer> RIGHT;
  private static final int STATE_DRAGGING = 2;
  private static final int STATE_NONE = 0;
  private static final int STATE_VISIBLE = 1;
  private static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
  private static final int THUMB_POSITION_INSIDE = 1;
  private static final int THUMB_POSITION_MIDPOINT = 0;
  private static Property<View, Integer> TOP;
  private boolean mAlwaysShow;
  @UnsupportedAppUsage
  private final Rect mContainerRect = new Rect();
  private int mCurrentSection = -1;
  private AnimatorSet mDecorAnimation;
  private final Runnable mDeferHide = new Runnable()
  {
    public void run()
    {
      FastScroller.this.setState(0);
    }
  };
  private boolean mEnabled;
  private int mFirstVisibleItem;
  @UnsupportedAppUsage
  private int mHeaderCount;
  private float mInitialTouchY;
  private boolean mLayoutFromRight;
  private final AbsListView mList;
  private Adapter mListAdapter;
  @UnsupportedAppUsage
  private boolean mLongList;
  private boolean mMatchDragPosition;
  @UnsupportedAppUsage
  private final int mMinimumTouchTarget;
  private int mOldChildCount;
  private int mOldItemCount;
  private final ViewGroupOverlay mOverlay;
  private int mOverlayPosition;
  private long mPendingDrag = -1L;
  private AnimatorSet mPreviewAnimation;
  private final View mPreviewImage;
  private int mPreviewMinHeight;
  private int mPreviewMinWidth;
  private int mPreviewPadding;
  private final int[] mPreviewResId = new int[2];
  private final TextView mPrimaryText;
  private int mScaledTouchSlop;
  private int mScrollBarStyle;
  private boolean mScrollCompleted;
  private int mScrollbarPosition = -1;
  private final TextView mSecondaryText;
  private SectionIndexer mSectionIndexer;
  private Object[] mSections;
  private boolean mShowingPreview;
  private boolean mShowingPrimary;
  private int mState;
  private final Animator.AnimatorListener mSwitchPrimaryListener = new AnimatorListenerAdapter()
  {
    public void onAnimationEnd(Animator paramAnonymousAnimator)
    {
      paramAnonymousAnimator = FastScroller.this;
      FastScroller.access$102(paramAnonymousAnimator, paramAnonymousAnimator.mShowingPrimary ^ true);
    }
  };
  private final Rect mTempBounds = new Rect();
  private final Rect mTempMargins = new Rect();
  private int mTextAppearance;
  private ColorStateList mTextColor;
  private float mTextSize;
  @UnsupportedAppUsage
  private Drawable mThumbDrawable;
  @UnsupportedAppUsage
  private final ImageView mThumbImage;
  private int mThumbMinHeight;
  private int mThumbMinWidth;
  private float mThumbOffset;
  private int mThumbPosition;
  private float mThumbRange;
  @UnsupportedAppUsage
  private Drawable mTrackDrawable;
  @UnsupportedAppUsage
  private final ImageView mTrackImage;
  private boolean mUpdatingLayout;
  private int mWidth;
  
  static
  {
    LEFT = new IntProperty("left")
    {
      public Integer get(View paramAnonymousView)
      {
        return Integer.valueOf(paramAnonymousView.getLeft());
      }
      
      public void setValue(View paramAnonymousView, int paramAnonymousInt)
      {
        paramAnonymousView.setLeft(paramAnonymousInt);
      }
    };
    TOP = new IntProperty("top")
    {
      public Integer get(View paramAnonymousView)
      {
        return Integer.valueOf(paramAnonymousView.getTop());
      }
      
      public void setValue(View paramAnonymousView, int paramAnonymousInt)
      {
        paramAnonymousView.setTop(paramAnonymousInt);
      }
    };
    RIGHT = new IntProperty("right")
    {
      public Integer get(View paramAnonymousView)
      {
        return Integer.valueOf(paramAnonymousView.getRight());
      }
      
      public void setValue(View paramAnonymousView, int paramAnonymousInt)
      {
        paramAnonymousView.setRight(paramAnonymousInt);
      }
    };
  }
  
  @UnsupportedAppUsage
  public FastScroller(AbsListView paramAbsListView, int paramInt)
  {
    this.mList = paramAbsListView;
    this.mOldItemCount = paramAbsListView.getCount();
    this.mOldChildCount = paramAbsListView.getChildCount();
    Object localObject = paramAbsListView.getContext();
    this.mScaledTouchSlop = ViewConfiguration.get((Context)localObject).getScaledTouchSlop();
    this.mScrollBarStyle = paramAbsListView.getScrollBarStyle();
    boolean bool = true;
    this.mScrollCompleted = true;
    this.mState = 1;
    if (((Context)localObject).getApplicationInfo().targetSdkVersion < 11) {
      bool = false;
    }
    this.mMatchDragPosition = bool;
    this.mTrackImage = new ImageView((Context)localObject);
    this.mTrackImage.setScaleType(ImageView.ScaleType.FIT_XY);
    this.mThumbImage = new ImageView((Context)localObject);
    this.mThumbImage.setScaleType(ImageView.ScaleType.FIT_XY);
    this.mPreviewImage = new View((Context)localObject);
    this.mPreviewImage.setAlpha(0.0F);
    this.mPrimaryText = createPreviewTextView((Context)localObject);
    this.mSecondaryText = createPreviewTextView((Context)localObject);
    this.mMinimumTouchTarget = paramAbsListView.getResources().getDimensionPixelSize(17105154);
    setStyle(paramInt);
    localObject = paramAbsListView.getOverlay();
    this.mOverlay = ((ViewGroupOverlay)localObject);
    ((ViewGroupOverlay)localObject).add(this.mTrackImage);
    ((ViewGroupOverlay)localObject).add(this.mThumbImage);
    ((ViewGroupOverlay)localObject).add(this.mPreviewImage);
    ((ViewGroupOverlay)localObject).add(this.mPrimaryText);
    ((ViewGroupOverlay)localObject).add(this.mSecondaryText);
    getSectionsFromIndexer();
    updateLongList(this.mOldChildCount, this.mOldItemCount);
    setScrollbarPosition(paramAbsListView.getVerticalScrollbarPosition());
    postAutoHide();
  }
  
  private static Animator animateAlpha(View paramView, float paramFloat)
  {
    return ObjectAnimator.ofFloat(paramView, View.ALPHA, new float[] { paramFloat });
  }
  
  private static Animator animateBounds(View paramView, Rect paramRect)
  {
    return ObjectAnimator.ofPropertyValuesHolder(paramView, new PropertyValuesHolder[] { PropertyValuesHolder.ofInt(LEFT, new int[] { paramRect.left }), PropertyValuesHolder.ofInt(TOP, new int[] { paramRect.top }), PropertyValuesHolder.ofInt(RIGHT, new int[] { paramRect.right }), PropertyValuesHolder.ofInt(BOTTOM, new int[] { paramRect.bottom }) });
  }
  
  private static Animator animateScaleX(View paramView, float paramFloat)
  {
    return ObjectAnimator.ofFloat(paramView, View.SCALE_X, new float[] { paramFloat });
  }
  
  private void applyLayout(View paramView, Rect paramRect)
  {
    paramView.layout(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
    float f;
    if (this.mLayoutFromRight) {
      f = paramRect.right - paramRect.left;
    } else {
      f = 0.0F;
    }
    paramView.setPivotX(f);
  }
  
  private void beginDrag()
  {
    this.mPendingDrag = -1L;
    setState(2);
    if ((this.mListAdapter == null) && (this.mList != null)) {
      getSectionsFromIndexer();
    }
    AbsListView localAbsListView = this.mList;
    if (localAbsListView != null)
    {
      localAbsListView.requestDisallowInterceptTouchEvent(true);
      this.mList.reportScrollStateChange(1);
    }
    cancelFling();
  }
  
  private void cancelFling()
  {
    MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
    this.mList.onTouchEvent(localMotionEvent);
    localMotionEvent.recycle();
  }
  
  private void cancelPendingDrag()
  {
    this.mPendingDrag = -1L;
  }
  
  private TextView createPreviewTextView(Context paramContext)
  {
    ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-2, -2);
    paramContext = new TextView(paramContext);
    paramContext.setLayoutParams(localLayoutParams);
    paramContext.setSingleLine(true);
    paramContext.setEllipsize(TextUtils.TruncateAt.MIDDLE);
    paramContext.setGravity(17);
    paramContext.setAlpha(0.0F);
    paramContext.setLayoutDirection(this.mList.getLayoutDirection());
    return paramContext;
  }
  
  private float getPosFromItemCount(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject1 = this.mSectionIndexer;
    if ((localObject1 == null) || (this.mListAdapter == null)) {
      getSectionsFromIndexer();
    }
    if ((paramInt2 != 0) && (paramInt3 != 0))
    {
      Object localObject2;
      if (localObject1 != null)
      {
        localObject2 = this.mSections;
        if ((localObject2 != null) && (localObject2.length > 0))
        {
          i = 1;
          break label64;
        }
      }
      int i = 0;
      label64:
      if ((i != 0) && (this.mMatchDragPosition))
      {
        int j = this.mHeaderCount;
        i = paramInt1 - j;
        if (i < 0) {
          return 0.0F;
        }
        int k = paramInt3 - j;
        localObject2 = this.mList.getChildAt(0);
        if ((localObject2 != null) && (((View)localObject2).getHeight() != 0)) {
          f = (this.mList.getPaddingTop() - ((View)localObject2).getTop()) / ((View)localObject2).getHeight();
        } else {
          f = 0.0F;
        }
        j = ((SectionIndexer)localObject1).getSectionForPosition(i);
        int m = ((SectionIndexer)localObject1).getPositionForSection(j);
        paramInt3 = this.mSections.length;
        if (j < paramInt3 - 1)
        {
          if (j + 1 < paramInt3) {
            paramInt1 = ((SectionIndexer)localObject1).getPositionForSection(j + 1);
          } else {
            paramInt1 = k - 1;
          }
          paramInt1 -= m;
        }
        else
        {
          paramInt1 = k - m;
        }
        if (paramInt1 == 0) {
          f = 0.0F;
        } else {
          f = (i + f - m) / paramInt1;
        }
        float f = (j + f) / paramInt3;
        if ((i > 0) && (i + paramInt2 == k))
        {
          localObject1 = this.mList.getChildAt(paramInt2 - 1);
          paramInt3 = this.mList.getPaddingBottom();
          if (this.mList.getClipToPadding())
          {
            paramInt1 = ((View)localObject1).getHeight();
            paramInt2 = this.mList.getHeight() - paramInt3 - ((View)localObject1).getTop();
          }
          else
          {
            paramInt1 = ((View)localObject1).getHeight();
            paramInt2 = this.mList.getHeight() - ((View)localObject1).getTop();
            paramInt1 += paramInt3;
          }
          if ((paramInt2 > 0) && (paramInt1 > 0)) {
            f += (1.0F - f) * (paramInt2 / paramInt1);
          } else {}
        }
        return f;
      }
      if (paramInt2 == paramInt3) {
        return 0.0F;
      }
      return paramInt1 / (paramInt3 - paramInt2);
    }
    return 0.0F;
  }
  
  private float getPosFromMotionEvent(float paramFloat)
  {
    float f = this.mThumbRange;
    if (f <= 0.0F) {
      return 0.0F;
    }
    return MathUtils.constrain((paramFloat - this.mThumbOffset) / f, 0.0F, 1.0F);
  }
  
  private void getSectionsFromIndexer()
  {
    this.mSectionIndexer = null;
    Object localObject1 = this.mList.getAdapter();
    Object localObject2 = localObject1;
    if ((localObject1 instanceof HeaderViewListAdapter))
    {
      this.mHeaderCount = ((HeaderViewListAdapter)localObject1).getHeadersCount();
      localObject2 = ((HeaderViewListAdapter)localObject1).getWrappedAdapter();
    }
    if ((localObject2 instanceof ExpandableListConnector))
    {
      localObject1 = ((ExpandableListConnector)localObject2).getAdapter();
      if ((localObject1 instanceof SectionIndexer))
      {
        this.mSectionIndexer = ((SectionIndexer)localObject1);
        this.mListAdapter = ((Adapter)localObject2);
        this.mSections = this.mSectionIndexer.getSections();
      }
    }
    else if ((localObject2 instanceof SectionIndexer))
    {
      this.mListAdapter = ((Adapter)localObject2);
      this.mSectionIndexer = ((SectionIndexer)localObject2);
      this.mSections = this.mSectionIndexer.getSections();
    }
    else
    {
      this.mListAdapter = ((Adapter)localObject2);
      this.mSections = null;
    }
  }
  
  private static Animator groupAnimatorOfFloat(Property<View, Float> paramProperty, float paramFloat, View... paramVarArgs)
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    AnimatorSet.Builder localBuilder = null;
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramVarArgs[i], paramProperty, new float[] { paramFloat });
      if (localBuilder == null) {
        localBuilder = localAnimatorSet.play(localObjectAnimator);
      } else {
        localBuilder.with(localObjectAnimator);
      }
    }
    return localAnimatorSet;
  }
  
  private boolean isPointInside(float paramFloat1, float paramFloat2)
  {
    boolean bool;
    if ((isPointInsideX(paramFloat1)) && ((this.mTrackDrawable != null) || (isPointInsideY(paramFloat2)))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean isPointInsideX(float paramFloat)
  {
    float f1 = this.mThumbImage.getTranslationX();
    float f2 = this.mThumbImage.getLeft();
    float f3 = this.mThumbImage.getRight();
    f3 = this.mMinimumTouchTarget - (f3 + f1 - (f2 + f1));
    f2 = 0.0F;
    if (f3 > 0.0F) {
      f2 = f3;
    }
    boolean bool1 = this.mLayoutFromRight;
    boolean bool2 = true;
    boolean bool3 = true;
    if (bool1)
    {
      if (paramFloat < this.mThumbImage.getLeft() - f2) {
        bool3 = false;
      }
      return bool3;
    }
    if (paramFloat <= this.mThumbImage.getRight() + f2) {
      bool3 = bool2;
    } else {
      bool3 = false;
    }
    return bool3;
  }
  
  private boolean isPointInsideY(float paramFloat)
  {
    float f1 = this.mThumbImage.getTranslationY();
    float f2 = this.mThumbImage.getTop() + f1;
    float f3 = this.mThumbImage.getBottom() + f1;
    float f4 = this.mMinimumTouchTarget - (f3 - f2);
    f1 = 0.0F;
    if (f4 > 0.0F) {
      f1 = f4 / 2.0F;
    }
    boolean bool;
    if ((paramFloat >= f2 - f1) && (paramFloat <= f3 + f1)) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void layoutThumb()
  {
    Rect localRect = this.mTempBounds;
    measureViewToSide(this.mThumbImage, null, null, localRect);
    applyLayout(this.mThumbImage, localRect);
  }
  
  private void layoutTrack()
  {
    ImageView localImageView1 = this.mTrackImage;
    ImageView localImageView2 = this.mThumbImage;
    Rect localRect = this.mContainerRect;
    int i = Math.max(0, localRect.width());
    int j = Math.max(0, localRect.height());
    localImageView1.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeSafeMeasureSpec(j, 0));
    if (this.mThumbPosition == 1)
    {
      i = localRect.top;
      j = localRect.bottom;
    }
    else
    {
      i = localImageView2.getHeight() / 2;
      k = localRect.top;
      j = localRect.bottom - i;
      i = k + i;
    }
    int k = localImageView1.getMeasuredWidth();
    int m = localImageView2.getLeft() + (localImageView2.getWidth() - k) / 2;
    localImageView1.layout(m, i, m + k, j);
  }
  
  private void measureFloating(View paramView, Rect paramRect1, Rect paramRect2)
  {
    if (paramRect1 == null)
    {
      i = 0;
      j = 0;
      k = 0;
    }
    else
    {
      i = paramRect1.left;
      j = paramRect1.top;
      k = paramRect1.right;
    }
    paramRect1 = this.mContainerRect;
    int m = paramRect1.width();
    int n = Math.max(0, paramRect1.height());
    paramView.measure(View.MeasureSpec.makeMeasureSpec(Math.max(0, m - i - k), Integer.MIN_VALUE), View.MeasureSpec.makeSafeMeasureSpec(n, 0));
    int k = paramRect1.height();
    int i = paramView.getMeasuredWidth();
    int j = k / 10 + j + paramRect1.top;
    k = paramView.getMeasuredHeight();
    m = (m - i) / 2 + paramRect1.left;
    paramRect2.set(m, j, m + i, k + j);
  }
  
  private void measurePreview(View paramView, Rect paramRect)
  {
    Rect localRect = this.mTempMargins;
    localRect.left = this.mPreviewImage.getPaddingLeft();
    localRect.top = this.mPreviewImage.getPaddingTop();
    localRect.right = this.mPreviewImage.getPaddingRight();
    localRect.bottom = this.mPreviewImage.getPaddingBottom();
    if (this.mOverlayPosition == 0) {
      measureFloating(paramView, localRect, paramRect);
    } else {
      measureViewToSide(paramView, this.mThumbImage, localRect, paramRect);
    }
  }
  
  private void measureViewToSide(View paramView1, View paramView2, Rect paramRect1, Rect paramRect2)
  {
    int i;
    int j;
    int k;
    if (paramRect1 == null)
    {
      i = 0;
      j = 0;
      k = 0;
    }
    else
    {
      i = paramRect1.left;
      j = paramRect1.top;
      k = paramRect1.right;
    }
    paramRect1 = this.mContainerRect;
    int m = paramRect1.width();
    if (paramView2 != null) {
      if (this.mLayoutFromRight) {
        m = paramView2.getLeft();
      } else {
        m -= paramView2.getRight();
      }
    }
    int n = Math.max(0, paramRect1.height());
    m = Math.max(0, m - i - k);
    paramView1.measure(View.MeasureSpec.makeMeasureSpec(m, Integer.MIN_VALUE), View.MeasureSpec.makeSafeMeasureSpec(n, 0));
    n = Math.min(m, paramView1.getMeasuredWidth());
    if (this.mLayoutFromRight)
    {
      if (paramView2 == null) {
        m = paramRect1.right;
      } else {
        m = paramView2.getLeft();
      }
      m -= k;
      k = m - n;
    }
    else
    {
      if (paramView2 == null) {
        m = paramRect1.left;
      } else {
        m = paramView2.getRight();
      }
      k = m + i;
      m = k + n;
    }
    paramRect2.set(k, j, m, j + paramView1.getMeasuredHeight());
  }
  
  private void onStateDependencyChanged(boolean paramBoolean)
  {
    if (isEnabled())
    {
      if (isAlwaysShowEnabled())
      {
        setState(1);
      }
      else if (this.mState == 1)
      {
        postAutoHide();
      }
      else if (paramBoolean)
      {
        setState(1);
        postAutoHide();
      }
    }
    else {
      stop();
    }
    this.mList.resolvePadding();
  }
  
  private void postAutoHide()
  {
    this.mList.removeCallbacks(this.mDeferHide);
    this.mList.postDelayed(this.mDeferHide, 1500L);
  }
  
  private void refreshDrawablePressedState()
  {
    boolean bool;
    if (this.mState == 2) {
      bool = true;
    } else {
      bool = false;
    }
    this.mThumbImage.setPressed(bool);
    this.mTrackImage.setPressed(bool);
  }
  
  private void scrollTo(float paramFloat)
  {
    this.mScrollCompleted = false;
    int i = this.mList.getCount();
    Object localObject = this.mSections;
    int j;
    if (localObject == null) {
      j = 0;
    } else {
      j = localObject.length;
    }
    int i6;
    if ((localObject != null) && (j > 1))
    {
      int k = MathUtils.constrain((int)(j * paramFloat), 0, j - 1);
      int m = k;
      int n = this.mSectionIndexer.getPositionForSection(m);
      int i1 = m;
      int i2 = i;
      int i3 = n;
      int i4 = m;
      int i5 = m + 1;
      if (m < j - 1) {
        i2 = this.mSectionIndexer.getPositionForSection(m + 1);
      }
      i6 = i1;
      int i7 = i3;
      int i8 = i4;
      if (i2 == n)
      {
        i7 = i3;
        i3 = m;
        do
        {
          i6 = i1;
          i8 = i4;
          if (i3 <= 0) {
            break;
          }
          i6 = i3 - 1;
          m = this.mSectionIndexer.getPositionForSection(i6);
          if (m != n)
          {
            i8 = i6;
            i7 = m;
            break;
          }
          i3 = i6;
          i7 = m;
        } while (i6 != 0);
        i6 = 0;
        i8 = i4;
        i7 = m;
      }
      m = i5 + 1;
      for (i3 = i5; (m < j) && (this.mSectionIndexer.getPositionForSection(m) == i2); i3++) {
        m++;
      }
      float f1 = i8 / j;
      float f2 = i3 / j;
      float f3;
      if (i == 0) {
        f3 = Float.MAX_VALUE;
      } else {
        f3 = 0.125F / i;
      }
      if ((i8 != k) || (paramFloat - f1 >= f3)) {
        i7 = (int)((i2 - i7) * (paramFloat - f1) / (f2 - f1)) + i7;
      }
      i7 = MathUtils.constrain(i7, 0, i - 1);
      localObject = this.mList;
      if ((localObject instanceof ExpandableListView))
      {
        localObject = (ExpandableListView)localObject;
        ((ExpandableListView)localObject).setSelectionFromTop(((ExpandableListView)localObject).getFlatListPosition(ExpandableListView.getPackedPositionForGroup(this.mHeaderCount + i7)), 0);
      }
      else if ((localObject instanceof ListView))
      {
        ((ListView)localObject).setSelectionFromTop(this.mHeaderCount + i7, 0);
      }
      else
      {
        ((AbsListView)localObject).setSelection(this.mHeaderCount + i7);
      }
    }
    else
    {
      i6 = MathUtils.constrain((int)(i * paramFloat), 0, i - 1);
      localObject = this.mList;
      if ((localObject instanceof ExpandableListView))
      {
        localObject = (ExpandableListView)localObject;
        ((ExpandableListView)localObject).setSelectionFromTop(((ExpandableListView)localObject).getFlatListPosition(ExpandableListView.getPackedPositionForGroup(this.mHeaderCount + i6)), 0);
      }
      else if ((localObject instanceof ListView))
      {
        ((ListView)localObject).setSelectionFromTop(this.mHeaderCount + i6, 0);
      }
      else
      {
        ((AbsListView)localObject).setSelection(this.mHeaderCount + i6);
      }
      i6 = -1;
    }
    if (this.mCurrentSection != i6)
    {
      this.mCurrentSection = i6;
      boolean bool = transitionPreviewLayout(i6);
      if ((!this.mShowingPreview) && (bool)) {
        transitionToDragging();
      } else if ((this.mShowingPreview) && (!bool)) {
        transitionToVisible();
      }
    }
  }
  
  @UnsupportedAppUsage
  private void setState(int paramInt)
  {
    this.mList.removeCallbacks(this.mDeferHide);
    int i = paramInt;
    if (this.mAlwaysShow)
    {
      i = paramInt;
      if (paramInt == 0) {
        i = 1;
      }
    }
    if (i == this.mState) {
      return;
    }
    if (i != 0)
    {
      if (i != 1)
      {
        if (i == 2) {
          if (transitionPreviewLayout(this.mCurrentSection)) {
            transitionToDragging();
          } else {
            transitionToVisible();
          }
        }
      }
      else {
        transitionToVisible();
      }
    }
    else {
      transitionToHidden();
    }
    this.mState = i;
    refreshDrawablePressedState();
  }
  
  private void setThumbPos(float paramFloat)
  {
    paramFloat = this.mThumbRange * paramFloat + this.mThumbOffset;
    Object localObject = this.mThumbImage;
    ((ImageView)localObject).setTranslationY(paramFloat - ((ImageView)localObject).getHeight() / 2.0F);
    View localView = this.mPreviewImage;
    float f = localView.getHeight() / 2.0F;
    int i = this.mOverlayPosition;
    if (i != 1) {
      if (i != 2) {
        paramFloat = 0.0F;
      } else {
        paramFloat -= f;
      }
    }
    localObject = this.mContainerRect;
    int j = ((Rect)localObject).top;
    i = ((Rect)localObject).bottom;
    paramFloat = MathUtils.constrain(paramFloat, j + f, i - f) - f;
    localView.setTranslationY(paramFloat);
    this.mPrimaryText.setTranslationY(paramFloat);
    this.mSecondaryText.setTranslationY(paramFloat);
  }
  
  private void startPendingDrag()
  {
    this.mPendingDrag = (SystemClock.uptimeMillis() + TAP_TIMEOUT);
  }
  
  private boolean transitionPreviewLayout(int paramInt)
  {
    Object localObject1 = this.mSections;
    TextView localTextView = null;
    Object localObject2 = localTextView;
    if (localObject1 != null)
    {
      localObject2 = localTextView;
      if (paramInt >= 0)
      {
        localObject2 = localTextView;
        if (paramInt < localObject1.length)
        {
          localObject1 = localObject1[paramInt];
          localObject2 = localTextView;
          if (localObject1 != null) {
            localObject2 = localObject1.toString();
          }
        }
      }
    }
    Object localObject3 = this.mTempBounds;
    View localView = this.mPreviewImage;
    if (this.mShowingPrimary)
    {
      localObject1 = this.mPrimaryText;
      localTextView = this.mSecondaryText;
    }
    else
    {
      localObject1 = this.mSecondaryText;
      localTextView = this.mPrimaryText;
    }
    localTextView.setText((CharSequence)localObject2);
    measurePreview(localTextView, (Rect)localObject3);
    applyLayout(localTextView, (Rect)localObject3);
    Object localObject4 = this.mPreviewAnimation;
    if (localObject4 != null) {
      ((AnimatorSet)localObject4).cancel();
    }
    Animator localAnimator = animateAlpha(localTextView, 1.0F).setDuration(50L);
    localObject4 = animateAlpha((View)localObject1, 0.0F).setDuration(50L);
    ((Animator)localObject4).addListener(this.mSwitchPrimaryListener);
    ((Rect)localObject3).left -= localView.getPaddingLeft();
    ((Rect)localObject3).top -= localView.getPaddingTop();
    ((Rect)localObject3).right += localView.getPaddingRight();
    ((Rect)localObject3).bottom += localView.getPaddingBottom();
    localObject3 = animateBounds(localView, (Rect)localObject3);
    ((Animator)localObject3).setDuration(100L);
    this.mPreviewAnimation = new AnimatorSet();
    localObject4 = this.mPreviewAnimation.play((Animator)localObject4).with(localAnimator);
    ((AnimatorSet.Builder)localObject4).with((Animator)localObject3);
    int i = localView.getWidth() - localView.getPaddingLeft() - localView.getPaddingRight();
    paramInt = localTextView.getWidth();
    if (paramInt > i)
    {
      localTextView.setScaleX(i / paramInt);
      ((AnimatorSet.Builder)localObject4).with(animateScaleX(localTextView, 1.0F).setDuration(100L));
    }
    else
    {
      localTextView.setScaleX(1.0F);
    }
    i = ((TextView)localObject1).getWidth();
    if (i > paramInt) {
      ((AnimatorSet.Builder)localObject4).with(animateScaleX((View)localObject1, paramInt / i).setDuration(100L));
    }
    this.mPreviewAnimation.start();
    return TextUtils.isEmpty((CharSequence)localObject2) ^ true;
  }
  
  private void transitionToDragging()
  {
    Object localObject = this.mDecorAnimation;
    if (localObject != null) {
      ((AnimatorSet)localObject).cancel();
    }
    localObject = groupAnimatorOfFloat(View.ALPHA, 1.0F, new View[] { this.mThumbImage, this.mTrackImage, this.mPreviewImage }).setDuration(150L);
    Animator localAnimator = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0F, new View[] { this.mThumbImage, this.mTrackImage }).setDuration(150L);
    this.mDecorAnimation = new AnimatorSet();
    this.mDecorAnimation.playTogether(new Animator[] { localObject, localAnimator });
    this.mDecorAnimation.start();
    this.mShowingPreview = true;
  }
  
  private void transitionToHidden()
  {
    Object localObject = this.mDecorAnimation;
    if (localObject != null) {
      ((AnimatorSet)localObject).cancel();
    }
    localObject = groupAnimatorOfFloat(View.ALPHA, 0.0F, new View[] { this.mThumbImage, this.mTrackImage, this.mPreviewImage, this.mPrimaryText, this.mSecondaryText }).setDuration(300L);
    int i;
    if (this.mLayoutFromRight) {
      i = this.mThumbImage.getWidth();
    } else {
      i = -this.mThumbImage.getWidth();
    }
    float f = i;
    Animator localAnimator = groupAnimatorOfFloat(View.TRANSLATION_X, f, new View[] { this.mThumbImage, this.mTrackImage }).setDuration(300L);
    this.mDecorAnimation = new AnimatorSet();
    this.mDecorAnimation.playTogether(new Animator[] { localObject, localAnimator });
    this.mDecorAnimation.start();
    this.mShowingPreview = false;
  }
  
  private void transitionToVisible()
  {
    Object localObject = this.mDecorAnimation;
    if (localObject != null) {
      ((AnimatorSet)localObject).cancel();
    }
    localObject = groupAnimatorOfFloat(View.ALPHA, 1.0F, new View[] { this.mThumbImage, this.mTrackImage }).setDuration(150L);
    Animator localAnimator1 = groupAnimatorOfFloat(View.ALPHA, 0.0F, new View[] { this.mPreviewImage, this.mPrimaryText, this.mSecondaryText }).setDuration(300L);
    Animator localAnimator2 = groupAnimatorOfFloat(View.TRANSLATION_X, 0.0F, new View[] { this.mThumbImage, this.mTrackImage }).setDuration(150L);
    this.mDecorAnimation = new AnimatorSet();
    this.mDecorAnimation.playTogether(new Animator[] { localObject, localAnimator1, localAnimator2 });
    this.mDecorAnimation.start();
    this.mShowingPreview = false;
  }
  
  private void updateAppearance()
  {
    int i = 0;
    this.mTrackImage.setImageDrawable(this.mTrackDrawable);
    Object localObject = this.mTrackDrawable;
    if (localObject != null) {
      i = Math.max(0, ((Drawable)localObject).getIntrinsicWidth());
    }
    this.mThumbImage.setImageDrawable(this.mThumbDrawable);
    this.mThumbImage.setMinimumWidth(this.mThumbMinWidth);
    this.mThumbImage.setMinimumHeight(this.mThumbMinHeight);
    localObject = this.mThumbDrawable;
    int j = i;
    if (localObject != null) {
      j = Math.max(i, ((Drawable)localObject).getIntrinsicWidth());
    }
    this.mWidth = Math.max(j, this.mThumbMinWidth);
    i = this.mTextAppearance;
    if (i != 0)
    {
      this.mPrimaryText.setTextAppearance(i);
      this.mSecondaryText.setTextAppearance(this.mTextAppearance);
    }
    localObject = this.mTextColor;
    if (localObject != null)
    {
      this.mPrimaryText.setTextColor((ColorStateList)localObject);
      this.mSecondaryText.setTextColor(this.mTextColor);
    }
    float f = this.mTextSize;
    if (f > 0.0F)
    {
      this.mPrimaryText.setTextSize(0, f);
      this.mSecondaryText.setTextSize(0, this.mTextSize);
    }
    i = this.mPreviewPadding;
    this.mPrimaryText.setIncludeFontPadding(false);
    this.mPrimaryText.setPadding(i, i, i, i);
    this.mSecondaryText.setIncludeFontPadding(false);
    this.mSecondaryText.setPadding(i, i, i, i);
    refreshDrawablePressedState();
  }
  
  private void updateContainerRect()
  {
    AbsListView localAbsListView = this.mList;
    localAbsListView.resolvePadding();
    Rect localRect = this.mContainerRect;
    localRect.left = 0;
    localRect.top = 0;
    localRect.right = localAbsListView.getWidth();
    localRect.bottom = localAbsListView.getHeight();
    int i = this.mScrollBarStyle;
    if ((i == 16777216) || (i == 0))
    {
      localRect.left += localAbsListView.getPaddingLeft();
      localRect.top += localAbsListView.getPaddingTop();
      localRect.right -= localAbsListView.getPaddingRight();
      localRect.bottom -= localAbsListView.getPaddingBottom();
      if (i == 16777216)
      {
        i = getWidth();
        if (this.mScrollbarPosition == 2) {
          localRect.right += i;
        } else {
          localRect.left -= i;
        }
      }
    }
  }
  
  private void updateLongList(int paramInt1, int paramInt2)
  {
    boolean bool;
    if ((paramInt1 > 0) && (paramInt2 / paramInt1 >= 4)) {
      bool = true;
    } else {
      bool = false;
    }
    if (this.mLongList != bool)
    {
      this.mLongList = bool;
      onStateDependencyChanged(false);
    }
  }
  
  private void updateOffsetAndRange()
  {
    ImageView localImageView1 = this.mTrackImage;
    ImageView localImageView2 = this.mThumbImage;
    float f1;
    float f2;
    if (this.mThumbPosition == 1)
    {
      f1 = localImageView2.getHeight() / 2.0F;
      f2 = localImageView1.getTop() + f1;
      f1 = localImageView1.getBottom() - f1;
    }
    else
    {
      f2 = localImageView1.getTop();
      f1 = localImageView1.getBottom();
    }
    this.mThumbOffset = f2;
    this.mThumbRange = (f1 - f2);
  }
  
  public int getWidth()
  {
    return this.mWidth;
  }
  
  public boolean isAlwaysShowEnabled()
  {
    return this.mAlwaysShow;
  }
  
  public boolean isEnabled()
  {
    boolean bool;
    if ((this.mEnabled) && ((this.mLongList) || (this.mAlwaysShow))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean onInterceptHoverEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    int i = paramMotionEvent.getActionMasked();
    if (((i == 9) || (i == 7)) && (this.mState == 0) && (isPointInside(paramMotionEvent.getX(), paramMotionEvent.getY())))
    {
      setState(1);
      postAutoHide();
    }
    return false;
  }
  
  @UnsupportedAppUsage
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    int i = paramMotionEvent.getActionMasked();
    if (i != 0)
    {
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3) {
            break label145;
          }
        }
        else
        {
          if (!isPointInside(paramMotionEvent.getX(), paramMotionEvent.getY()))
          {
            cancelPendingDrag();
            break label145;
          }
          long l = this.mPendingDrag;
          if ((l < 0L) || (l > SystemClock.uptimeMillis())) {
            break label145;
          }
          beginDrag();
          scrollTo(getPosFromMotionEvent(this.mInitialTouchY));
          return onTouchEvent(paramMotionEvent);
        }
      }
      cancelPendingDrag();
    }
    else if (isPointInside(paramMotionEvent.getX(), paramMotionEvent.getY()))
    {
      if (!this.mList.isInScrollingContainer()) {
        return true;
      }
      this.mInitialTouchY = paramMotionEvent.getY();
      startPendingDrag();
    }
    label145:
    return false;
  }
  
  public void onItemCountChanged(int paramInt1, int paramInt2)
  {
    if ((this.mOldItemCount != paramInt2) || (this.mOldChildCount != paramInt1))
    {
      this.mOldItemCount = paramInt2;
      this.mOldChildCount = paramInt1;
      int i;
      if (paramInt2 - paramInt1 > 0) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i != 0) && (this.mState != 2)) {
        setThumbPos(getPosFromItemCount(this.mList.getFirstVisiblePosition(), paramInt1, paramInt2));
      }
      updateLongList(paramInt1, paramInt2);
    }
  }
  
  public PointerIcon onResolvePointerIcon(MotionEvent paramMotionEvent, int paramInt)
  {
    if ((this.mState != 2) && (!isPointInside(paramMotionEvent.getX(), paramMotionEvent.getY()))) {
      return null;
    }
    return PointerIcon.getSystemIcon(this.mList.getContext(), 1000);
  }
  
  public void onScroll(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = isEnabled();
    int i = 0;
    if (!bool)
    {
      setState(0);
      return;
    }
    if (paramInt3 - paramInt2 > 0) {
      i = 1;
    }
    if ((i != 0) && (this.mState != 2)) {
      setThumbPos(getPosFromItemCount(paramInt1, paramInt2, paramInt3));
    }
    this.mScrollCompleted = true;
    if (this.mFirstVisibleItem != paramInt1)
    {
      this.mFirstVisibleItem = paramInt1;
      if (this.mState != 2)
      {
        setState(1);
        postAutoHide();
      }
    }
  }
  
  public void onSectionsChanged()
  {
    this.mListAdapter = null;
  }
  
  @UnsupportedAppUsage
  public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    updateLayout();
  }
  
  @UnsupportedAppUsage
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled()) {
      return false;
    }
    int i = paramMotionEvent.getActionMasked();
    if (i != 0)
    {
      float f;
      if (i != 1)
      {
        if (i != 2)
        {
          if (i == 3) {
            cancelPendingDrag();
          }
        }
        else
        {
          if ((this.mPendingDrag >= 0L) && (Math.abs(paramMotionEvent.getY() - this.mInitialTouchY) > this.mScaledTouchSlop)) {
            beginDrag();
          }
          if (this.mState == 2)
          {
            f = getPosFromMotionEvent(paramMotionEvent.getY());
            setThumbPos(f);
            if (this.mScrollCompleted) {
              scrollTo(f);
            }
            return true;
          }
        }
      }
      else
      {
        if (this.mPendingDrag >= 0L)
        {
          beginDrag();
          f = getPosFromMotionEvent(paramMotionEvent.getY());
          setThumbPos(f);
          scrollTo(f);
        }
        if (this.mState == 2)
        {
          paramMotionEvent = this.mList;
          if (paramMotionEvent != null)
          {
            paramMotionEvent.requestDisallowInterceptTouchEvent(false);
            this.mList.reportScrollStateChange(0);
          }
          setState(1);
          postAutoHide();
          return true;
        }
      }
    }
    else if ((isPointInside(paramMotionEvent.getX(), paramMotionEvent.getY())) && (!this.mList.isInScrollingContainer()))
    {
      beginDrag();
      return true;
    }
    return false;
  }
  
  @UnsupportedAppUsage
  public void remove()
  {
    this.mOverlay.remove(this.mTrackImage);
    this.mOverlay.remove(this.mThumbImage);
    this.mOverlay.remove(this.mPreviewImage);
    this.mOverlay.remove(this.mPrimaryText);
    this.mOverlay.remove(this.mSecondaryText);
  }
  
  public void setAlwaysShow(boolean paramBoolean)
  {
    if (this.mAlwaysShow != paramBoolean)
    {
      this.mAlwaysShow = paramBoolean;
      onStateDependencyChanged(false);
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    if (this.mEnabled != paramBoolean)
    {
      this.mEnabled = paramBoolean;
      onStateDependencyChanged(true);
    }
  }
  
  public void setScrollBarStyle(int paramInt)
  {
    if (this.mScrollBarStyle != paramInt)
    {
      this.mScrollBarStyle = paramInt;
      updateLayout();
    }
  }
  
  public void setScrollbarPosition(int paramInt)
  {
    boolean bool = true;
    int i = paramInt;
    if (paramInt == 0)
    {
      if (this.mList.isLayoutRtl()) {
        paramInt = 1;
      } else {
        paramInt = 2;
      }
      i = paramInt;
    }
    if (this.mScrollbarPosition != i)
    {
      this.mScrollbarPosition = i;
      if (i == 1) {
        bool = false;
      }
      this.mLayoutFromRight = bool;
      paramInt = this.mPreviewResId[this.mLayoutFromRight];
      this.mPreviewImage.setBackgroundResource(paramInt);
      paramInt = Math.max(0, this.mPreviewMinWidth - this.mPreviewImage.getPaddingLeft() - this.mPreviewImage.getPaddingRight());
      this.mPrimaryText.setMinimumWidth(paramInt);
      this.mSecondaryText.setMinimumWidth(paramInt);
      paramInt = Math.max(0, this.mPreviewMinHeight - this.mPreviewImage.getPaddingTop() - this.mPreviewImage.getPaddingBottom());
      this.mPrimaryText.setMinimumHeight(paramInt);
      this.mSecondaryText.setMinimumHeight(paramInt);
      updateLayout();
    }
  }
  
  public void setStyle(int paramInt)
  {
    TypedArray localTypedArray = this.mList.getContext().obtainStyledAttributes(null, R.styleable.FastScroll, 16843767, paramInt);
    int i = localTypedArray.getIndexCount();
    for (paramInt = 0; paramInt < i; paramInt++)
    {
      int j = localTypedArray.getIndex(paramInt);
      switch (j)
      {
      default: 
        break;
      case 13: 
        this.mTrackDrawable = localTypedArray.getDrawable(j);
        break;
      case 12: 
        this.mThumbMinWidth = localTypedArray.getDimensionPixelSize(j, 0);
        break;
      case 11: 
        this.mThumbMinHeight = localTypedArray.getDimensionPixelSize(j, 0);
        break;
      case 10: 
        this.mThumbDrawable = localTypedArray.getDrawable(j);
        break;
      case 9: 
        this.mOverlayPosition = localTypedArray.getInt(j, 0);
        break;
      case 8: 
        this.mPreviewResId[1] = localTypedArray.getResourceId(j, 0);
        break;
      case 7: 
        this.mPreviewResId[0] = localTypedArray.getResourceId(j, 0);
        break;
      case 6: 
        this.mThumbPosition = localTypedArray.getInt(j, 0);
        break;
      case 5: 
        this.mPreviewMinHeight = localTypedArray.getDimensionPixelSize(j, 0);
        break;
      case 4: 
        this.mPreviewMinWidth = localTypedArray.getDimensionPixelSize(j, 0);
        break;
      case 3: 
        this.mPreviewPadding = localTypedArray.getDimensionPixelSize(j, 0);
        break;
      case 2: 
        this.mTextColor = localTypedArray.getColorStateList(j);
        break;
      case 1: 
        this.mTextSize = localTypedArray.getDimensionPixelSize(j, 0);
        break;
      case 0: 
        this.mTextAppearance = localTypedArray.getResourceId(j, 0);
      }
    }
    localTypedArray.recycle();
    updateAppearance();
  }
  
  public void stop()
  {
    setState(0);
  }
  
  public void updateLayout()
  {
    if (this.mUpdatingLayout) {
      return;
    }
    this.mUpdatingLayout = true;
    updateContainerRect();
    layoutThumb();
    layoutTrack();
    updateOffsetAndRange();
    Rect localRect = this.mTempBounds;
    measurePreview(this.mPrimaryText, localRect);
    applyLayout(this.mPrimaryText, localRect);
    measurePreview(this.mSecondaryText, localRect);
    applyLayout(this.mSecondaryText, localRect);
    if (this.mPreviewImage != null)
    {
      localRect.left -= this.mPreviewImage.getPaddingLeft();
      localRect.top -= this.mPreviewImage.getPaddingTop();
      localRect.right += this.mPreviewImage.getPaddingRight();
      localRect.bottom += this.mPreviewImage.getPaddingBottom();
      applyLayout(this.mPreviewImage, localRect);
    }
    this.mUpdatingLayout = false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/FastScroller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */