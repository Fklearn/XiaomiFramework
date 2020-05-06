package android.widget;

import android.animation.ObjectAnimator;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.MathUtils;
import android.util.Pools.SynchronizedPool;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.RangeInfo;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import com.android.internal.R.styleable;
import java.util.ArrayList;

@RemoteViews.RemoteView
public class ProgressBar
  extends View
{
  private static final int MAX_LEVEL = 10000;
  private static final int PROGRESS_ANIM_DURATION = 80;
  private static final DecelerateInterpolator PROGRESS_ANIM_INTERPOLATOR = new DecelerateInterpolator();
  private static final int TIMEOUT_SEND_ACCESSIBILITY_EVENT = 200;
  private final FloatProperty<ProgressBar> VISUAL_PROGRESS;
  private AccessibilityEventSender mAccessibilityEventSender;
  private boolean mAggregatedIsVisible;
  private AlphaAnimation mAnimation;
  private boolean mAttached;
  private int mBehavior;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private Drawable mCurrentDrawable;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=124052713L)
  private int mDuration;
  private boolean mHasAnimation;
  private boolean mInDrawing;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private boolean mIndeterminate;
  private Drawable mIndeterminateDrawable;
  private Interpolator mInterpolator;
  private int mMax;
  @UnsupportedAppUsage(maxTargetSdk=28)
  int mMaxHeight;
  private boolean mMaxInitialized;
  int mMaxWidth;
  private int mMin;
  @UnsupportedAppUsage(maxTargetSdk=28)
  int mMinHeight;
  private boolean mMinInitialized;
  @UnsupportedAppUsage(maxTargetSdk=28)
  int mMinWidth;
  @UnsupportedAppUsage
  boolean mMirrorForRtl;
  private boolean mNoInvalidate;
  @UnsupportedAppUsage(trackingBug=124049927L)
  private boolean mOnlyIndeterminate;
  private int mProgress;
  private Drawable mProgressDrawable;
  private ProgressTintInfo mProgressTintInfo;
  private final ArrayList<RefreshData> mRefreshData;
  private boolean mRefreshIsPosted;
  private RefreshProgressRunnable mRefreshProgressRunnable;
  int mSampleWidth;
  private int mSecondaryProgress;
  private boolean mShouldStartAnimationDrawable;
  private Transformation mTransformation;
  private long mUiThreadId;
  private float mVisualProgress;
  
  public ProgressBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ProgressBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842871);
  }
  
  public ProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    boolean bool = false;
    this.mSampleWidth = 0;
    this.mMirrorForRtl = false;
    this.mRefreshData = new ArrayList();
    this.VISUAL_PROGRESS = new FloatProperty("visual_progress")
    {
      public Float get(ProgressBar paramAnonymousProgressBar)
      {
        return Float.valueOf(paramAnonymousProgressBar.mVisualProgress);
      }
      
      public void setValue(ProgressBar paramAnonymousProgressBar, float paramAnonymousFloat)
      {
        paramAnonymousProgressBar.setVisualProgress(16908301, paramAnonymousFloat);
        ProgressBar.access$802(paramAnonymousProgressBar, paramAnonymousFloat);
      }
    };
    this.mUiThreadId = Thread.currentThread().getId();
    initProgressBar();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ProgressBar, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ProgressBar, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mNoInvalidate = true;
    paramAttributeSet = localTypedArray.getDrawable(8);
    if (paramAttributeSet != null) {
      if (needsTileify(paramAttributeSet)) {
        setProgressDrawableTiled(paramAttributeSet);
      } else {
        setProgressDrawable(paramAttributeSet);
      }
    }
    this.mDuration = localTypedArray.getInt(9, this.mDuration);
    this.mMinWidth = localTypedArray.getDimensionPixelSize(11, this.mMinWidth);
    this.mMaxWidth = localTypedArray.getDimensionPixelSize(0, this.mMaxWidth);
    this.mMinHeight = localTypedArray.getDimensionPixelSize(12, this.mMinHeight);
    this.mMaxHeight = localTypedArray.getDimensionPixelSize(1, this.mMaxHeight);
    this.mBehavior = localTypedArray.getInt(10, this.mBehavior);
    paramInt1 = localTypedArray.getResourceId(13, 17432587);
    if (paramInt1 > 0) {
      setInterpolator(paramContext, paramInt1);
    }
    setMin(localTypedArray.getInt(26, this.mMin));
    setMax(localTypedArray.getInt(2, this.mMax));
    setProgress(localTypedArray.getInt(3, this.mProgress));
    setSecondaryProgress(localTypedArray.getInt(4, this.mSecondaryProgress));
    paramContext = localTypedArray.getDrawable(7);
    if (paramContext != null) {
      if (needsTileify(paramContext)) {
        setIndeterminateDrawableTiled(paramContext);
      } else {
        setIndeterminateDrawable(paramContext);
      }
    }
    this.mOnlyIndeterminate = localTypedArray.getBoolean(6, this.mOnlyIndeterminate);
    this.mNoInvalidate = false;
    if ((this.mOnlyIndeterminate) || (localTypedArray.getBoolean(5, this.mIndeterminate))) {
      bool = true;
    }
    setIndeterminate(bool);
    this.mMirrorForRtl = localTypedArray.getBoolean(15, this.mMirrorForRtl);
    if (localTypedArray.hasValue(17))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mProgressBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(17, -1), null);
      this.mProgressTintInfo.mHasProgressTintMode = true;
    }
    if (localTypedArray.hasValue(16))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mProgressTintList = localTypedArray.getColorStateList(16);
      this.mProgressTintInfo.mHasProgressTint = true;
    }
    if (localTypedArray.hasValue(19))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mProgressBackgroundBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(19, -1), null);
      this.mProgressTintInfo.mHasProgressBackgroundTintMode = true;
    }
    if (localTypedArray.hasValue(18))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mProgressBackgroundTintList = localTypedArray.getColorStateList(18);
      this.mProgressTintInfo.mHasProgressBackgroundTint = true;
    }
    if (localTypedArray.hasValue(21))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mSecondaryProgressBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(21, -1), null);
      this.mProgressTintInfo.mHasSecondaryProgressTintMode = true;
    }
    if (localTypedArray.hasValue(20))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mSecondaryProgressTintList = localTypedArray.getColorStateList(20);
      this.mProgressTintInfo.mHasSecondaryProgressTint = true;
    }
    if (localTypedArray.hasValue(23))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mIndeterminateBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(23, -1), null);
      this.mProgressTintInfo.mHasIndeterminateTintMode = true;
    }
    if (localTypedArray.hasValue(22))
    {
      if (this.mProgressTintInfo == null) {
        this.mProgressTintInfo = new ProgressTintInfo(null);
      }
      this.mProgressTintInfo.mIndeterminateTintList = localTypedArray.getColorStateList(22);
      this.mProgressTintInfo.mHasIndeterminateTint = true;
    }
    localTypedArray.recycle();
    applyProgressTints();
    applyIndeterminateTint();
    if (getImportantForAccessibility() == 0) {
      setImportantForAccessibility(1);
    }
  }
  
  private void applyIndeterminateTint()
  {
    if ((this.mIndeterminateDrawable != null) && (this.mProgressTintInfo != null))
    {
      ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
      if ((localProgressTintInfo.mHasIndeterminateTint) || (localProgressTintInfo.mHasIndeterminateTintMode))
      {
        this.mIndeterminateDrawable = this.mIndeterminateDrawable.mutate();
        if (localProgressTintInfo.mHasIndeterminateTint) {
          this.mIndeterminateDrawable.setTintList(localProgressTintInfo.mIndeterminateTintList);
        }
        if (localProgressTintInfo.mHasIndeterminateTintMode) {
          this.mIndeterminateDrawable.setTintBlendMode(localProgressTintInfo.mIndeterminateBlendMode);
        }
        if (this.mIndeterminateDrawable.isStateful()) {
          this.mIndeterminateDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyPrimaryProgressTint()
  {
    if ((this.mProgressTintInfo.mHasProgressTint) || (this.mProgressTintInfo.mHasProgressTintMode))
    {
      Drawable localDrawable = getTintTarget(16908301, true);
      if (localDrawable != null)
      {
        if (this.mProgressTintInfo.mHasProgressTint) {
          localDrawable.setTintList(this.mProgressTintInfo.mProgressTintList);
        }
        if (this.mProgressTintInfo.mHasProgressTintMode) {
          localDrawable.setTintBlendMode(this.mProgressTintInfo.mProgressBlendMode);
        }
        if (localDrawable.isStateful()) {
          localDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyProgressBackgroundTint()
  {
    if ((this.mProgressTintInfo.mHasProgressBackgroundTint) || (this.mProgressTintInfo.mHasProgressBackgroundTintMode))
    {
      Drawable localDrawable = getTintTarget(16908288, false);
      if (localDrawable != null)
      {
        if (this.mProgressTintInfo.mHasProgressBackgroundTint) {
          localDrawable.setTintList(this.mProgressTintInfo.mProgressBackgroundTintList);
        }
        if (this.mProgressTintInfo.mHasProgressBackgroundTintMode) {
          localDrawable.setTintBlendMode(this.mProgressTintInfo.mProgressBackgroundBlendMode);
        }
        if (localDrawable.isStateful()) {
          localDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void applyProgressTints()
  {
    if ((this.mProgressDrawable != null) && (this.mProgressTintInfo != null))
    {
      applyPrimaryProgressTint();
      applyProgressBackgroundTint();
      applySecondaryProgressTint();
    }
  }
  
  private void applySecondaryProgressTint()
  {
    if ((this.mProgressTintInfo.mHasSecondaryProgressTint) || (this.mProgressTintInfo.mHasSecondaryProgressTintMode))
    {
      Drawable localDrawable = getTintTarget(16908303, false);
      if (localDrawable != null)
      {
        if (this.mProgressTintInfo.mHasSecondaryProgressTint) {
          localDrawable.setTintList(this.mProgressTintInfo.mSecondaryProgressTintList);
        }
        if (this.mProgressTintInfo.mHasSecondaryProgressTintMode) {
          localDrawable.setTintBlendMode(this.mProgressTintInfo.mSecondaryProgressBlendMode);
        }
        if (localDrawable.isStateful()) {
          localDrawable.setState(getDrawableState());
        }
      }
    }
  }
  
  private void doRefreshProgress(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    try
    {
      int i = this.mMax - this.mMin;
      float f;
      if (i > 0) {
        f = (paramInt2 - this.mMin) / i;
      } else {
        f = 0.0F;
      }
      if (paramInt1 == 16908301) {
        i = 1;
      } else {
        i = 0;
      }
      if ((i != 0) && (paramBoolean3))
      {
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(this, this.VISUAL_PROGRESS, new float[] { f });
        localObjectAnimator.setAutoCancel(true);
        localObjectAnimator.setDuration(80L);
        localObjectAnimator.setInterpolator(PROGRESS_ANIM_INTERPOLATOR);
        localObjectAnimator.start();
      }
      else
      {
        setVisualProgress(paramInt1, f);
      }
      if ((i != 0) && (paramBoolean2)) {
        onProgressRefresh(f, paramBoolean1, paramInt2);
      }
      return;
    }
    finally {}
  }
  
  private Drawable getTintTarget(int paramInt, boolean paramBoolean)
  {
    Object localObject = null;
    Drawable localDrawable1 = null;
    Drawable localDrawable2 = this.mProgressDrawable;
    if (localDrawable2 != null)
    {
      this.mProgressDrawable = localDrawable2.mutate();
      if ((localDrawable2 instanceof LayerDrawable)) {
        localDrawable1 = ((LayerDrawable)localDrawable2).findDrawableByLayerId(paramInt);
      }
      localObject = localDrawable1;
      if (paramBoolean)
      {
        localObject = localDrawable1;
        if (localDrawable1 == null) {
          localObject = localDrawable2;
        }
      }
    }
    return (Drawable)localObject;
  }
  
  private void initProgressBar()
  {
    this.mMin = 0;
    this.mMax = 100;
    this.mProgress = 0;
    this.mSecondaryProgress = 0;
    this.mIndeterminate = false;
    this.mOnlyIndeterminate = false;
    this.mDuration = 4000;
    this.mBehavior = 1;
    this.mMinWidth = 24;
    this.mMaxWidth = 48;
    this.mMinHeight = 24;
    this.mMaxHeight = 48;
  }
  
  private static boolean needsTileify(Drawable paramDrawable)
  {
    int i;
    int j;
    if ((paramDrawable instanceof LayerDrawable))
    {
      paramDrawable = (LayerDrawable)paramDrawable;
      i = paramDrawable.getNumberOfLayers();
      for (j = 0; j < i; j++) {
        if (needsTileify(paramDrawable.getDrawable(j))) {
          return true;
        }
      }
      return false;
    }
    if ((paramDrawable instanceof StateListDrawable))
    {
      paramDrawable = (StateListDrawable)paramDrawable;
      i = paramDrawable.getStateCount();
      for (j = 0; j < i; j++) {
        if (needsTileify(paramDrawable.getStateDrawable(j))) {
          return true;
        }
      }
      return false;
    }
    return (paramDrawable instanceof BitmapDrawable);
  }
  
  @UnsupportedAppUsage
  private void refreshProgress(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      if (this.mUiThreadId == Thread.currentThread().getId())
      {
        doRefreshProgress(paramInt1, paramInt2, paramBoolean1, true, paramBoolean2);
      }
      else
      {
        if (this.mRefreshProgressRunnable == null)
        {
          localObject1 = new android/widget/ProgressBar$RefreshProgressRunnable;
          ((RefreshProgressRunnable)localObject1).<init>(this, null);
          this.mRefreshProgressRunnable = ((RefreshProgressRunnable)localObject1);
        }
        Object localObject1 = RefreshData.obtain(paramInt1, paramInt2, paramBoolean1, paramBoolean2);
        this.mRefreshData.add(localObject1);
        if ((this.mAttached) && (!this.mRefreshIsPosted))
        {
          post(this.mRefreshProgressRunnable);
          this.mRefreshIsPosted = true;
        }
      }
      return;
    }
    finally {}
  }
  
  private void scheduleAccessibilityEventSender()
  {
    AccessibilityEventSender localAccessibilityEventSender = this.mAccessibilityEventSender;
    if (localAccessibilityEventSender == null) {
      this.mAccessibilityEventSender = new AccessibilityEventSender(null);
    } else {
      removeCallbacks(localAccessibilityEventSender);
    }
    postDelayed(this.mAccessibilityEventSender, 200L);
  }
  
  private void setVisualProgress(int paramInt, float paramFloat)
  {
    this.mVisualProgress = paramFloat;
    Drawable localDrawable1 = this.mCurrentDrawable;
    Drawable localDrawable2 = localDrawable1;
    if ((localDrawable1 instanceof LayerDrawable))
    {
      localDrawable1 = ((LayerDrawable)localDrawable1).findDrawableByLayerId(paramInt);
      localDrawable2 = localDrawable1;
      if (localDrawable1 == null) {
        localDrawable2 = this.mCurrentDrawable;
      }
    }
    if (localDrawable2 != null) {
      localDrawable2.setLevel((int)(10000.0F * paramFloat));
    } else {
      invalidate();
    }
    onVisualProgressChanged(paramInt, paramFloat);
  }
  
  private void swapCurrentDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mCurrentDrawable;
    this.mCurrentDrawable = paramDrawable;
    if (localDrawable != this.mCurrentDrawable)
    {
      if (localDrawable != null) {
        localDrawable.setVisible(false, false);
      }
      paramDrawable = this.mCurrentDrawable;
      if (paramDrawable != null)
      {
        boolean bool;
        if ((getWindowVisibility() == 0) && (isShown())) {
          bool = true;
        } else {
          bool = false;
        }
        paramDrawable.setVisible(bool, false);
      }
    }
  }
  
  @UnsupportedAppUsage
  private Drawable tileify(Drawable paramDrawable, boolean paramBoolean)
  {
    int i;
    Object localObject;
    int j;
    if ((paramDrawable instanceof LayerDrawable))
    {
      paramDrawable = (LayerDrawable)paramDrawable;
      i = paramDrawable.getNumberOfLayers();
      localObject = new Drawable[i];
      for (j = 0; j < i; j++)
      {
        int k = paramDrawable.getId(j);
        Drawable localDrawable = paramDrawable.getDrawable(j);
        if ((k != 16908301) && (k != 16908303)) {
          paramBoolean = false;
        } else {
          paramBoolean = true;
        }
        localObject[j] = tileify(localDrawable, paramBoolean);
      }
      localObject = new LayerDrawable((Drawable[])localObject);
      for (j = 0; j < i; j++)
      {
        ((LayerDrawable)localObject).setId(j, paramDrawable.getId(j));
        ((LayerDrawable)localObject).setLayerGravity(j, paramDrawable.getLayerGravity(j));
        ((LayerDrawable)localObject).setLayerWidth(j, paramDrawable.getLayerWidth(j));
        ((LayerDrawable)localObject).setLayerHeight(j, paramDrawable.getLayerHeight(j));
        ((LayerDrawable)localObject).setLayerInsetLeft(j, paramDrawable.getLayerInsetLeft(j));
        ((LayerDrawable)localObject).setLayerInsetRight(j, paramDrawable.getLayerInsetRight(j));
        ((LayerDrawable)localObject).setLayerInsetTop(j, paramDrawable.getLayerInsetTop(j));
        ((LayerDrawable)localObject).setLayerInsetBottom(j, paramDrawable.getLayerInsetBottom(j));
        ((LayerDrawable)localObject).setLayerInsetStart(j, paramDrawable.getLayerInsetStart(j));
        ((LayerDrawable)localObject).setLayerInsetEnd(j, paramDrawable.getLayerInsetEnd(j));
      }
      return (Drawable)localObject;
    }
    if ((paramDrawable instanceof StateListDrawable))
    {
      localObject = (StateListDrawable)paramDrawable;
      paramDrawable = new StateListDrawable();
      i = ((StateListDrawable)localObject).getStateCount();
      for (j = 0; j < i; j++) {
        paramDrawable.addState(((StateListDrawable)localObject).getStateSet(j), tileify(((StateListDrawable)localObject).getStateDrawable(j), paramBoolean));
      }
      return paramDrawable;
    }
    if ((paramDrawable instanceof BitmapDrawable))
    {
      paramDrawable = (BitmapDrawable)paramDrawable.getConstantState().newDrawable(getResources());
      paramDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
      if (this.mSampleWidth <= 0) {
        this.mSampleWidth = paramDrawable.getIntrinsicWidth();
      }
      if (paramBoolean) {
        return new ClipDrawable(paramDrawable, 3, 1);
      }
      return paramDrawable;
    }
    return paramDrawable;
  }
  
  private Drawable tileifyIndeterminate(Drawable paramDrawable)
  {
    Object localObject = paramDrawable;
    if ((paramDrawable instanceof AnimationDrawable))
    {
      AnimationDrawable localAnimationDrawable = (AnimationDrawable)paramDrawable;
      int i = localAnimationDrawable.getNumberOfFrames();
      localObject = new AnimationDrawable();
      ((AnimationDrawable)localObject).setOneShot(localAnimationDrawable.isOneShot());
      for (int j = 0; j < i; j++)
      {
        paramDrawable = tileify(localAnimationDrawable.getFrame(j), true);
        paramDrawable.setLevel(10000);
        ((AnimationDrawable)localObject).addFrame(paramDrawable, localAnimationDrawable.getDuration(j));
      }
      ((AnimationDrawable)localObject).setLevel(10000);
    }
    return (Drawable)localObject;
  }
  
  private void updateDrawableBounds(int paramInt1, int paramInt2)
  {
    int i = paramInt1 - (this.mPaddingRight + this.mPaddingLeft);
    int j = paramInt2 - (this.mPaddingTop + this.mPaddingBottom);
    paramInt1 = i;
    paramInt2 = j;
    int k = 0;
    int m = 0;
    Drawable localDrawable = this.mIndeterminateDrawable;
    int n = paramInt1;
    int i1 = paramInt2;
    if (localDrawable != null)
    {
      int i2 = paramInt1;
      i1 = paramInt2;
      int i3 = k;
      n = m;
      if (this.mOnlyIndeterminate)
      {
        i2 = paramInt1;
        i1 = paramInt2;
        i3 = k;
        n = m;
        if (!(localDrawable instanceof AnimationDrawable))
        {
          n = localDrawable.getIntrinsicWidth();
          i1 = this.mIndeterminateDrawable.getIntrinsicHeight();
          float f1 = n / i1;
          float f2 = i / j;
          i2 = paramInt1;
          i1 = paramInt2;
          i3 = k;
          n = m;
          if (f1 != f2) {
            if (f2 > f1)
            {
              paramInt1 = (int)(j * f1);
              n = (i - paramInt1) / 2;
              i2 = n + paramInt1;
              i1 = paramInt2;
              i3 = k;
            }
            else
            {
              paramInt2 = (int)(i * (1.0F / f1));
              i3 = (j - paramInt2) / 2;
              i1 = i3 + paramInt2;
              n = m;
              i2 = paramInt1;
            }
          }
        }
      }
      paramInt1 = i2;
      paramInt2 = n;
      if (isLayoutRtl())
      {
        paramInt1 = i2;
        paramInt2 = n;
        if (this.mMirrorForRtl)
        {
          paramInt2 = i - i2;
          paramInt1 = i - n;
        }
      }
      this.mIndeterminateDrawable.setBounds(paramInt2, i3, paramInt1, i1);
      n = paramInt1;
    }
    localDrawable = this.mProgressDrawable;
    if (localDrawable != null) {
      localDrawable.setBounds(0, 0, n, i1);
    }
  }
  
  private void updateDrawableState()
  {
    int[] arrayOfInt = getDrawableState();
    boolean bool1 = false;
    Drawable localDrawable = this.mProgressDrawable;
    boolean bool2 = bool1;
    if (localDrawable != null)
    {
      bool2 = bool1;
      if (localDrawable.isStateful()) {
        bool2 = false | localDrawable.setState(arrayOfInt);
      }
    }
    localDrawable = this.mIndeterminateDrawable;
    bool1 = bool2;
    if (localDrawable != null)
    {
      bool1 = bool2;
      if (localDrawable.isStateful()) {
        bool1 = bool2 | localDrawable.setState(arrayOfInt);
      }
    }
    if (bool1) {
      invalidate();
    }
  }
  
  void drawTrack(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mCurrentDrawable;
    if (localDrawable != null)
    {
      int i = paramCanvas.save();
      if ((isLayoutRtl()) && (this.mMirrorForRtl))
      {
        paramCanvas.translate(getWidth() - this.mPaddingRight, this.mPaddingTop);
        paramCanvas.scale(-1.0F, 1.0F);
      }
      else
      {
        paramCanvas.translate(this.mPaddingLeft, this.mPaddingTop);
      }
      long l = getDrawingTime();
      float f;
      if (this.mHasAnimation)
      {
        this.mAnimation.getTransformation(l, this.mTransformation);
        f = this.mTransformation.getAlpha();
      }
      try
      {
        this.mInDrawing = true;
        localDrawable.setLevel((int)(10000.0F * f));
        this.mInDrawing = false;
        postInvalidateOnAnimation();
      }
      finally
      {
        this.mInDrawing = false;
      }
      paramCanvas.restoreToCount(i);
      if ((this.mShouldStartAnimationDrawable) && ((localDrawable instanceof Animatable)))
      {
        ((Animatable)localDrawable).start();
        this.mShouldStartAnimationDrawable = false;
      }
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    Drawable localDrawable = this.mProgressDrawable;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
    localDrawable = this.mIndeterminateDrawable;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateDrawableState();
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("progress:max", getMax());
    paramViewHierarchyEncoder.addProperty("progress:progress", getProgress());
    paramViewHierarchyEncoder.addProperty("progress:secondaryProgress", getSecondaryProgress());
    paramViewHierarchyEncoder.addProperty("progress:indeterminate", isIndeterminate());
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ProgressBar.class.getName();
  }
  
  public Drawable getCurrentDrawable()
  {
    return this.mCurrentDrawable;
  }
  
  Shape getDrawableShape()
  {
    return new RoundRectShape(new float[] { 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F }, null, null);
  }
  
  public Drawable getIndeterminateDrawable()
  {
    return this.mIndeterminateDrawable;
  }
  
  public BlendMode getIndeterminateTintBlendMode()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mIndeterminateBlendMode;
    } else {
      localObject = null;
    }
    return (BlendMode)localObject;
  }
  
  public ColorStateList getIndeterminateTintList()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mIndeterminateTintList;
    } else {
      localObject = null;
    }
    return (ColorStateList)localObject;
  }
  
  public PorterDuff.Mode getIndeterminateTintMode()
  {
    Object localObject = getIndeterminateTintBlendMode();
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public Interpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public int getMax()
  {
    try
    {
      int i = this.mMax;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getMaxHeight()
  {
    return this.mMaxHeight;
  }
  
  public int getMaxWidth()
  {
    return this.mMaxWidth;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public int getMin()
  {
    try
    {
      int i = this.mMin;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getMinHeight()
  {
    return this.mMinHeight;
  }
  
  public int getMinWidth()
  {
    return this.mMinWidth;
  }
  
  public boolean getMirrorForRtl()
  {
    return this.mMirrorForRtl;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public int getProgress()
  {
    try
    {
      int i;
      if (this.mIndeterminate) {
        i = 0;
      } else {
        i = this.mProgress;
      }
      return i;
    }
    finally {}
  }
  
  public BlendMode getProgressBackgroundTintBlendMode()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mProgressBackgroundBlendMode;
    } else {
      localObject = null;
    }
    return (BlendMode)localObject;
  }
  
  public ColorStateList getProgressBackgroundTintList()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mProgressBackgroundTintList;
    } else {
      localObject = null;
    }
    return (ColorStateList)localObject;
  }
  
  public PorterDuff.Mode getProgressBackgroundTintMode()
  {
    Object localObject = getProgressBackgroundTintBlendMode();
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public Drawable getProgressDrawable()
  {
    return this.mProgressDrawable;
  }
  
  public BlendMode getProgressTintBlendMode()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mProgressBlendMode;
    } else {
      localObject = null;
    }
    return (BlendMode)localObject;
  }
  
  public ColorStateList getProgressTintList()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mProgressTintList;
    } else {
      localObject = null;
    }
    return (ColorStateList)localObject;
  }
  
  public PorterDuff.Mode getProgressTintMode()
  {
    Object localObject = getProgressTintBlendMode();
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public int getSecondaryProgress()
  {
    try
    {
      int i;
      if (this.mIndeterminate) {
        i = 0;
      } else {
        i = this.mSecondaryProgress;
      }
      return i;
    }
    finally {}
  }
  
  public BlendMode getSecondaryProgressTintBlendMode()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mSecondaryProgressBlendMode;
    } else {
      localObject = null;
    }
    return (BlendMode)localObject;
  }
  
  public ColorStateList getSecondaryProgressTintList()
  {
    Object localObject = this.mProgressTintInfo;
    if (localObject != null) {
      localObject = ((ProgressTintInfo)localObject).mSecondaryProgressTintList;
    } else {
      localObject = null;
    }
    return (ColorStateList)localObject;
  }
  
  public PorterDuff.Mode getSecondaryProgressTintMode()
  {
    Object localObject = getSecondaryProgressTintBlendMode();
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public final void incrementProgressBy(int paramInt)
  {
    try
    {
      setProgress(this.mProgress + paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void incrementSecondaryProgressBy(int paramInt)
  {
    try
    {
      setSecondaryProgress(this.mSecondaryProgress + paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (!this.mInDrawing) {
      if (verifyDrawable(paramDrawable))
      {
        paramDrawable = paramDrawable.getBounds();
        int i = this.mScrollX + this.mPaddingLeft;
        int j = this.mScrollY + this.mPaddingTop;
        invalidate(paramDrawable.left + i, paramDrawable.top + j, paramDrawable.right + i, paramDrawable.bottom + j);
      }
      else
      {
        super.invalidateDrawable(paramDrawable);
      }
    }
  }
  
  public boolean isAnimating()
  {
    boolean bool;
    if ((isIndeterminate()) && (getWindowVisibility() == 0) && (isShown())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @ViewDebug.ExportedProperty(category="progress")
  public boolean isIndeterminate()
  {
    try
    {
      boolean bool = this.mIndeterminate;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    Drawable localDrawable = this.mProgressDrawable;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
    }
    localDrawable = this.mIndeterminateDrawable;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mIndeterminate) {
      startAnimation();
    }
    if (this.mRefreshData != null) {
      try
      {
        int i = this.mRefreshData.size();
        for (int j = 0; j < i; j++)
        {
          RefreshData localRefreshData = (RefreshData)this.mRefreshData.get(j);
          doRefreshProgress(localRefreshData.id, localRefreshData.progress, localRefreshData.fromUser, true, localRefreshData.animate);
          localRefreshData.recycle();
        }
        this.mRefreshData.clear();
      }
      finally {}
    }
    this.mAttached = true;
  }
  
  protected void onDetachedFromWindow()
  {
    if (this.mIndeterminate) {
      stopAnimation();
    }
    Object localObject = this.mRefreshProgressRunnable;
    if (localObject != null)
    {
      removeCallbacks((Runnable)localObject);
      this.mRefreshIsPosted = false;
    }
    localObject = this.mAccessibilityEventSender;
    if (localObject != null) {
      removeCallbacks((Runnable)localObject);
    }
    super.onDetachedFromWindow();
    this.mAttached = false;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    try
    {
      super.onDraw(paramCanvas);
      drawTrack(paramCanvas);
      return;
    }
    finally
    {
      paramCanvas = finally;
      throw paramCanvas;
    }
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setItemCount(this.mMax - this.mMin);
    paramAccessibilityEvent.setCurrentItemIndex(this.mProgress);
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (!isIndeterminate()) {
      paramAccessibilityNodeInfo.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(0, getMin(), getMax(), getProgress()));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    try
    {
      Drawable localDrawable = this.mCurrentDrawable;
      if (localDrawable != null)
      {
        i = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, localDrawable.getIntrinsicWidth()));
        j = Math.max(this.mMinHeight, Math.min(this.mMaxHeight, localDrawable.getIntrinsicHeight()));
      }
      updateDrawableState();
      int k = this.mPaddingLeft;
      int m = this.mPaddingRight;
      int n = this.mPaddingTop;
      int i1 = this.mPaddingBottom;
      setMeasuredDimension(resolveSizeAndState(i + (k + m), paramInt1, 0), resolveSizeAndState(j + (n + i1), paramInt2, 0));
      return;
    }
    finally {}
  }
  
  void onProgressRefresh(float paramFloat, boolean paramBoolean, int paramInt)
  {
    if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
      scheduleAccessibilityEventSender();
    }
  }
  
  public void onResolveDrawables(int paramInt)
  {
    Drawable localDrawable = this.mCurrentDrawable;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
    localDrawable = this.mIndeterminateDrawable;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
    localDrawable = this.mProgressDrawable;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setProgress(paramParcelable.progress);
    setSecondaryProgress(paramParcelable.secondaryProgress);
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.progress = this.mProgress;
    localSavedState.secondaryProgress = this.mSecondaryProgress;
    return localSavedState;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    updateDrawableBounds(paramInt1, paramInt2);
  }
  
  public void onVisibilityAggregated(boolean paramBoolean)
  {
    super.onVisibilityAggregated(paramBoolean);
    if (paramBoolean != this.mAggregatedIsVisible)
    {
      this.mAggregatedIsVisible = paramBoolean;
      if (this.mIndeterminate) {
        if (paramBoolean) {
          startAnimation();
        } else {
          stopAnimation();
        }
      }
      Drawable localDrawable = this.mCurrentDrawable;
      if (localDrawable != null) {
        localDrawable.setVisible(paramBoolean, false);
      }
    }
  }
  
  void onVisualProgressChanged(int paramInt, float paramFloat) {}
  
  public void postInvalidate()
  {
    if (!this.mNoInvalidate) {
      super.postInvalidate();
    }
  }
  
  @RemotableViewMethod
  public void setIndeterminate(boolean paramBoolean)
  {
    try
    {
      if (((!this.mOnlyIndeterminate) || (!this.mIndeterminate)) && (paramBoolean != this.mIndeterminate))
      {
        this.mIndeterminate = paramBoolean;
        if (paramBoolean)
        {
          swapCurrentDrawable(this.mIndeterminateDrawable);
          startAnimation();
        }
        else
        {
          swapCurrentDrawable(this.mProgressDrawable);
          stopAnimation();
        }
      }
      return;
    }
    finally {}
  }
  
  public void setIndeterminateDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mIndeterminateDrawable;
    if (localDrawable != paramDrawable)
    {
      if (localDrawable != null)
      {
        localDrawable.setCallback(null);
        unscheduleDrawable(this.mIndeterminateDrawable);
      }
      this.mIndeterminateDrawable = paramDrawable;
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        paramDrawable.setLayoutDirection(getLayoutDirection());
        if (paramDrawable.isStateful()) {
          paramDrawable.setState(getDrawableState());
        }
        applyIndeterminateTint();
      }
      if (this.mIndeterminate)
      {
        swapCurrentDrawable(paramDrawable);
        postInvalidate();
      }
    }
  }
  
  public void setIndeterminateDrawableTiled(Drawable paramDrawable)
  {
    Drawable localDrawable = paramDrawable;
    if (paramDrawable != null) {
      localDrawable = tileifyIndeterminate(paramDrawable);
    }
    setIndeterminateDrawable(localDrawable);
  }
  
  public void setIndeterminateTintBlendMode(BlendMode paramBlendMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mIndeterminateBlendMode = paramBlendMode;
    localProgressTintInfo.mHasIndeterminateTintMode = true;
    applyIndeterminateTint();
  }
  
  @RemotableViewMethod
  public void setIndeterminateTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mIndeterminateTintList = paramColorStateList;
    localProgressTintInfo.mHasIndeterminateTint = true;
    applyIndeterminateTint();
  }
  
  public void setIndeterminateTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setIndeterminateTintBlendMode(paramMode);
  }
  
  public void setInterpolator(Context paramContext, int paramInt)
  {
    setInterpolator(AnimationUtils.loadInterpolator(paramContext, paramInt));
  }
  
  public void setInterpolator(Interpolator paramInterpolator)
  {
    this.mInterpolator = paramInterpolator;
  }
  
  @RemotableViewMethod
  public void setMax(int paramInt)
  {
    int i = paramInt;
    try
    {
      if (this.mMinInitialized)
      {
        i = paramInt;
        if (paramInt < this.mMin) {
          i = this.mMin;
        }
      }
      this.mMaxInitialized = true;
      if ((this.mMinInitialized) && (i != this.mMax))
      {
        this.mMax = i;
        postInvalidate();
        if (this.mProgress > i) {
          this.mProgress = i;
        }
        refreshProgress(16908301, this.mProgress, false, false);
      }
      else
      {
        this.mMax = i;
      }
      return;
    }
    finally {}
  }
  
  public void setMaxHeight(int paramInt)
  {
    this.mMaxHeight = paramInt;
    requestLayout();
  }
  
  public void setMaxWidth(int paramInt)
  {
    this.mMaxWidth = paramInt;
    requestLayout();
  }
  
  @RemotableViewMethod
  public void setMin(int paramInt)
  {
    int i = paramInt;
    try
    {
      if (this.mMaxInitialized)
      {
        i = paramInt;
        if (paramInt > this.mMax) {
          i = this.mMax;
        }
      }
      this.mMinInitialized = true;
      if ((this.mMaxInitialized) && (i != this.mMin))
      {
        this.mMin = i;
        postInvalidate();
        if (this.mProgress < i) {
          this.mProgress = i;
        }
        refreshProgress(16908301, this.mProgress, false, false);
      }
      else
      {
        this.mMin = i;
      }
      return;
    }
    finally {}
  }
  
  public void setMinHeight(int paramInt)
  {
    this.mMinHeight = paramInt;
    requestLayout();
  }
  
  public void setMinWidth(int paramInt)
  {
    this.mMinWidth = paramInt;
    requestLayout();
  }
  
  @RemotableViewMethod
  public void setProgress(int paramInt)
  {
    try
    {
      setProgressInternal(paramInt, false, false);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void setProgress(int paramInt, boolean paramBoolean)
  {
    setProgressInternal(paramInt, false, paramBoolean);
  }
  
  public void setProgressBackgroundTintBlendMode(BlendMode paramBlendMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mProgressBackgroundBlendMode = paramBlendMode;
    localProgressTintInfo.mHasProgressBackgroundTintMode = true;
    if (this.mProgressDrawable != null) {
      applyProgressBackgroundTint();
    }
  }
  
  @RemotableViewMethod
  public void setProgressBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mProgressBackgroundTintList = paramColorStateList;
    localProgressTintInfo.mHasProgressBackgroundTint = true;
    if (this.mProgressDrawable != null) {
      applyProgressBackgroundTint();
    }
  }
  
  public void setProgressBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setProgressBackgroundTintBlendMode(paramMode);
  }
  
  public void setProgressDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mProgressDrawable;
    if (localDrawable != paramDrawable)
    {
      if (localDrawable != null)
      {
        localDrawable.setCallback(null);
        unscheduleDrawable(this.mProgressDrawable);
      }
      this.mProgressDrawable = paramDrawable;
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        paramDrawable.setLayoutDirection(getLayoutDirection());
        if (paramDrawable.isStateful()) {
          paramDrawable.setState(getDrawableState());
        }
        int i = paramDrawable.getMinimumHeight();
        if (this.mMaxHeight < i)
        {
          this.mMaxHeight = i;
          requestLayout();
        }
        applyProgressTints();
      }
      if (!this.mIndeterminate)
      {
        swapCurrentDrawable(paramDrawable);
        postInvalidate();
      }
      updateDrawableBounds(getWidth(), getHeight());
      updateDrawableState();
      doRefreshProgress(16908301, this.mProgress, false, false, false);
      doRefreshProgress(16908303, this.mSecondaryProgress, false, false, false);
    }
  }
  
  public void setProgressDrawableTiled(Drawable paramDrawable)
  {
    Drawable localDrawable = paramDrawable;
    if (paramDrawable != null) {
      localDrawable = tileify(paramDrawable, false);
    }
    setProgressDrawable(localDrawable);
  }
  
  @RemotableViewMethod
  @UnsupportedAppUsage
  boolean setProgressInternal(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      boolean bool = this.mIndeterminate;
      if (bool) {
        return false;
      }
      paramInt = MathUtils.constrain(paramInt, this.mMin, this.mMax);
      int i = this.mProgress;
      if (paramInt == i) {
        return false;
      }
      this.mProgress = paramInt;
      refreshProgress(16908301, this.mProgress, paramBoolean1, paramBoolean2);
      return true;
    }
    finally {}
  }
  
  public void setProgressTintBlendMode(BlendMode paramBlendMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mProgressBlendMode = paramBlendMode;
    localProgressTintInfo.mHasProgressTintMode = true;
    if (this.mProgressDrawable != null) {
      applyPrimaryProgressTint();
    }
  }
  
  @RemotableViewMethod
  public void setProgressTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mProgressTintList = paramColorStateList;
    localProgressTintInfo.mHasProgressTint = true;
    if (this.mProgressDrawable != null) {
      applyPrimaryProgressTint();
    }
  }
  
  public void setProgressTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setProgressTintBlendMode(paramMode);
  }
  
  @RemotableViewMethod
  public void setSecondaryProgress(int paramInt)
  {
    try
    {
      boolean bool = this.mIndeterminate;
      if (bool) {
        return;
      }
      int i = paramInt;
      if (paramInt < this.mMin) {
        i = this.mMin;
      }
      paramInt = i;
      if (i > this.mMax) {
        paramInt = this.mMax;
      }
      if (paramInt != this.mSecondaryProgress)
      {
        this.mSecondaryProgress = paramInt;
        refreshProgress(16908303, this.mSecondaryProgress, false, false);
      }
      return;
    }
    finally {}
  }
  
  public void setSecondaryProgressTintBlendMode(BlendMode paramBlendMode)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mSecondaryProgressBlendMode = paramBlendMode;
    localProgressTintInfo.mHasSecondaryProgressTintMode = true;
    if (this.mProgressDrawable != null) {
      applySecondaryProgressTint();
    }
  }
  
  public void setSecondaryProgressTintList(ColorStateList paramColorStateList)
  {
    if (this.mProgressTintInfo == null) {
      this.mProgressTintInfo = new ProgressTintInfo(null);
    }
    ProgressTintInfo localProgressTintInfo = this.mProgressTintInfo;
    localProgressTintInfo.mSecondaryProgressTintList = paramColorStateList;
    localProgressTintInfo.mHasSecondaryProgressTint = true;
    if (this.mProgressDrawable != null) {
      applySecondaryProgressTint();
    }
  }
  
  public void setSecondaryProgressTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setSecondaryProgressTintBlendMode(paramMode);
  }
  
  @UnsupportedAppUsage
  void startAnimation()
  {
    if ((getVisibility() == 0) && (getWindowVisibility() == 0))
    {
      if ((this.mIndeterminateDrawable instanceof Animatable))
      {
        this.mShouldStartAnimationDrawable = true;
        this.mHasAnimation = false;
      }
      else
      {
        this.mHasAnimation = true;
        if (this.mInterpolator == null) {
          this.mInterpolator = new LinearInterpolator();
        }
        Object localObject = this.mTransformation;
        if (localObject == null) {
          this.mTransformation = new Transformation();
        } else {
          ((Transformation)localObject).clear();
        }
        localObject = this.mAnimation;
        if (localObject == null) {
          this.mAnimation = new AlphaAnimation(0.0F, 1.0F);
        } else {
          ((AlphaAnimation)localObject).reset();
        }
        this.mAnimation.setRepeatMode(this.mBehavior);
        this.mAnimation.setRepeatCount(-1);
        this.mAnimation.setDuration(this.mDuration);
        this.mAnimation.setInterpolator(this.mInterpolator);
        this.mAnimation.setStartTime(-1L);
      }
      postInvalidate();
      return;
    }
  }
  
  @UnsupportedAppUsage
  void stopAnimation()
  {
    this.mHasAnimation = false;
    Drawable localDrawable = this.mIndeterminateDrawable;
    if ((localDrawable instanceof Animatable))
    {
      ((Animatable)localDrawable).stop();
      this.mShouldStartAnimationDrawable = false;
    }
    postInvalidate();
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((paramDrawable != this.mProgressDrawable) && (paramDrawable != this.mIndeterminateDrawable) && (!super.verifyDrawable(paramDrawable))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private class AccessibilityEventSender
    implements Runnable
  {
    private AccessibilityEventSender() {}
    
    public void run()
    {
      ProgressBar.this.sendAccessibilityEvent(4);
    }
  }
  
  private static class ProgressTintInfo
  {
    boolean mHasIndeterminateTint;
    boolean mHasIndeterminateTintMode;
    boolean mHasProgressBackgroundTint;
    boolean mHasProgressBackgroundTintMode;
    boolean mHasProgressTint;
    boolean mHasProgressTintMode;
    boolean mHasSecondaryProgressTint;
    boolean mHasSecondaryProgressTintMode;
    BlendMode mIndeterminateBlendMode;
    ColorStateList mIndeterminateTintList;
    BlendMode mProgressBackgroundBlendMode;
    ColorStateList mProgressBackgroundTintList;
    BlendMode mProgressBlendMode;
    ColorStateList mProgressTintList;
    BlendMode mSecondaryProgressBlendMode;
    ColorStateList mSecondaryProgressTintList;
  }
  
  private static class RefreshData
  {
    private static final int POOL_MAX = 24;
    private static final Pools.SynchronizedPool<RefreshData> sPool = new Pools.SynchronizedPool(24);
    public boolean animate;
    public boolean fromUser;
    public int id;
    public int progress;
    
    public static RefreshData obtain(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
    {
      RefreshData localRefreshData1 = (RefreshData)sPool.acquire();
      RefreshData localRefreshData2 = localRefreshData1;
      if (localRefreshData1 == null) {
        localRefreshData2 = new RefreshData();
      }
      localRefreshData2.id = paramInt1;
      localRefreshData2.progress = paramInt2;
      localRefreshData2.fromUser = paramBoolean1;
      localRefreshData2.animate = paramBoolean2;
      return localRefreshData2;
    }
    
    public void recycle()
    {
      sPool.release(this);
    }
  }
  
  private class RefreshProgressRunnable
    implements Runnable
  {
    private RefreshProgressRunnable() {}
    
    public void run()
    {
      synchronized (ProgressBar.this)
      {
        int i = ProgressBar.this.mRefreshData.size();
        for (int j = 0; j < i; j++)
        {
          ProgressBar.RefreshData localRefreshData = (ProgressBar.RefreshData)ProgressBar.this.mRefreshData.get(j);
          ProgressBar.this.doRefreshProgress(localRefreshData.id, localRefreshData.progress, localRefreshData.fromUser, true, localRefreshData.animate);
          localRefreshData.recycle();
        }
        ProgressBar.this.mRefreshData.clear();
        ProgressBar.access$302(ProgressBar.this, false);
        return;
      }
    }
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public ProgressBar.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ProgressBar.SavedState(paramAnonymousParcel, null);
      }
      
      public ProgressBar.SavedState[] newArray(int paramAnonymousInt)
      {
        return new ProgressBar.SavedState[paramAnonymousInt];
      }
    };
    int progress;
    int secondaryProgress;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.progress = paramParcel.readInt();
      this.secondaryProgress = paramParcel.readInt();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.progress);
      paramParcel.writeInt(this.secondaryProgress);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ProgressBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */