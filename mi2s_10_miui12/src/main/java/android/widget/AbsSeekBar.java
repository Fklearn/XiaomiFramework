package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.internal.R.styleable;
import com.android.internal.util.Preconditions;
import com.miui.internal.variable.api.Overridable;
import com.miui.internal.variable.api.v29.Android_Widget_AbsSeekBar.Extension;
import com.miui.internal.variable.api.v29.Android_Widget_AbsSeekBar.Interface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbsSeekBar
  extends ProgressBar
{
  private static final int NO_ALPHA = 255;
  @UnsupportedAppUsage
  private float mDisabledAlpha;
  private final List<Rect> mGestureExclusionRects = new ArrayList();
  private boolean mHasThumbBlendMode = false;
  private boolean mHasThumbTint = false;
  private boolean mHasTickMarkBlendMode = false;
  private boolean mHasTickMarkTint = false;
  @UnsupportedAppUsage
  private boolean mIsDragging;
  @UnsupportedAppUsage
  boolean mIsUserSeekable = true;
  private int mKeyProgressIncrement = 1;
  private int mScaledTouchSlop;
  @UnsupportedAppUsage
  private boolean mSplitTrack;
  private final Rect mTempRect = new Rect();
  @UnsupportedAppUsage
  private Drawable mThumb;
  private BlendMode mThumbBlendMode = null;
  private int mThumbOffset;
  private final Rect mThumbRect = new Rect();
  private ColorStateList mThumbTintList = null;
  private Drawable mTickMark;
  private BlendMode mTickMarkBlendMode = null;
  private ColorStateList mTickMarkTintList = null;
  private float mTouchDownX;
  @UnsupportedAppUsage
  float mTouchProgressOffset;
  private List<Rect> mUserGestureExclusionRects = Collections.emptyList();
  
  static
  {
    Android_Widget_AbsSeekBar.Extension.get().bindOriginal(new Android_Widget_AbsSeekBar.Interface()
    {
      public boolean onTouchEvent(AbsSeekBar paramAnonymousAbsSeekBar, MotionEvent paramAnonymousMotionEvent)
      {
        return paramAnonymousAbsSeekBar.originalOnTouchEvent(paramAnonymousMotionEvent);
      }
    });
  }
  
  public AbsSeekBar(Context paramContext)
  {
    super(paramContext);
  }
  
  public AbsSeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public AbsSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AbsSeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.SeekBar, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.SeekBar, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    setThumb(localTypedArray.getDrawable(0));
    if (localTypedArray.hasValue(4))
    {
      this.mThumbBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(4, -1), this.mThumbBlendMode);
      this.mHasThumbBlendMode = true;
    }
    if (localTypedArray.hasValue(3))
    {
      this.mThumbTintList = localTypedArray.getColorStateList(3);
      this.mHasThumbTint = true;
    }
    setTickMark(localTypedArray.getDrawable(5));
    if (localTypedArray.hasValue(7))
    {
      this.mTickMarkBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(7, -1), this.mTickMarkBlendMode);
      this.mHasTickMarkBlendMode = true;
    }
    if (localTypedArray.hasValue(6))
    {
      this.mTickMarkTintList = localTypedArray.getColorStateList(6);
      this.mHasTickMarkTint = true;
    }
    this.mSplitTrack = localTypedArray.getBoolean(2, false);
    setThumbOffset(localTypedArray.getDimensionPixelOffset(1, getThumbOffset()));
    boolean bool = localTypedArray.getBoolean(8, true);
    localTypedArray.recycle();
    if (bool)
    {
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Theme, 0, 0);
      this.mDisabledAlpha = paramAttributeSet.getFloat(3, 0.5F);
      paramAttributeSet.recycle();
    }
    else
    {
      this.mDisabledAlpha = 1.0F;
    }
    applyThumbTint();
    applyTickMarkTint();
    this.mScaledTouchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
  }
  
  private void applyThumbTint()
  {
    if ((this.mThumb != null) && ((this.mHasThumbTint) || (this.mHasThumbBlendMode)))
    {
      this.mThumb = this.mThumb.mutate();
      if (this.mHasThumbTint) {
        this.mThumb.setTintList(this.mThumbTintList);
      }
      if (this.mHasThumbBlendMode) {
        this.mThumb.setTintBlendMode(this.mThumbBlendMode);
      }
      if (this.mThumb.isStateful()) {
        this.mThumb.setState(getDrawableState());
      }
    }
  }
  
  private void applyTickMarkTint()
  {
    if ((this.mTickMark != null) && ((this.mHasTickMarkTint) || (this.mHasTickMarkBlendMode)))
    {
      this.mTickMark = this.mTickMark.mutate();
      if (this.mHasTickMarkTint) {
        this.mTickMark.setTintList(this.mTickMarkTintList);
      }
      if (this.mHasTickMarkBlendMode) {
        this.mTickMark.setTintBlendMode(this.mTickMarkBlendMode);
      }
      if (this.mTickMark.isStateful()) {
        this.mTickMark.setState(getDrawableState());
      }
    }
  }
  
  private void attemptClaimDrag()
  {
    if (this.mParent != null) {
      this.mParent.requestDisallowInterceptTouchEvent(true);
    }
  }
  
  private float getScale()
  {
    int i = getMin();
    int j = getMax() - i;
    float f;
    if (j > 0) {
      f = (getProgress() - i) / j;
    } else {
      f = 0.0F;
    }
    return f;
  }
  
  private void setHotspot(float paramFloat1, float paramFloat2)
  {
    Drawable localDrawable = getBackground();
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  private void setThumbPos(int paramInt1, Drawable paramDrawable, float paramFloat, int paramInt2)
  {
    int i = this.mPaddingLeft;
    int j = this.mPaddingRight;
    int k = paramDrawable.getIntrinsicWidth();
    int m = paramDrawable.getIntrinsicHeight();
    j = paramInt1 - i - j - k + this.mThumbOffset * 2;
    i = (int)(j * paramFloat + 0.5F);
    if (paramInt2 == Integer.MIN_VALUE)
    {
      localObject = paramDrawable.getBounds();
      paramInt2 = ((Rect)localObject).top;
      paramInt1 = ((Rect)localObject).bottom;
    }
    else
    {
      paramInt1 = paramInt2;
      m = paramInt2 + m;
      paramInt2 = paramInt1;
      paramInt1 = m;
    }
    if ((isLayoutRtl()) && (this.mMirrorForRtl)) {
      i = j - i;
    }
    m = i + k;
    Object localObject = getBackground();
    if (localObject != null)
    {
      j = this.mPaddingLeft - this.mThumbOffset;
      k = this.mPaddingTop;
      ((Drawable)localObject).setHotspotBounds(i + j, paramInt2 + k, m + j, paramInt1 + k);
    }
    paramDrawable.setBounds(i, paramInt2, m, paramInt1);
    updateGestureExclusionRects();
  }
  
  private void startDrag(MotionEvent paramMotionEvent)
  {
    setPressed(true);
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null) {
      invalidate(localDrawable.getBounds());
    }
    onStartTrackingTouch();
    trackTouchEvent(paramMotionEvent);
    attemptClaimDrag();
  }
  
  @UnsupportedAppUsage
  private void trackTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = Math.round(paramMotionEvent.getX());
    int j = Math.round(paramMotionEvent.getY());
    int k = getWidth();
    int m = k - this.mPaddingLeft - this.mPaddingRight;
    float f1 = 0.0F;
    float f2;
    if ((isLayoutRtl()) && (this.mMirrorForRtl))
    {
      if (i > k - this.mPaddingRight)
      {
        f2 = 0.0F;
      }
      else if (i < this.mPaddingLeft)
      {
        f2 = 1.0F;
      }
      else
      {
        f2 = (m - i + this.mPaddingLeft) / m;
        f1 = this.mTouchProgressOffset;
      }
    }
    else if (i < this.mPaddingLeft)
    {
      f2 = 0.0F;
    }
    else if (i > k - this.mPaddingRight)
    {
      f2 = 1.0F;
    }
    else
    {
      f2 = (i - this.mPaddingLeft) / m;
      f1 = this.mTouchProgressOffset;
    }
    float f3 = getMax() - getMin();
    float f4 = getMin();
    setHotspot(i, j);
    setProgressInternal(Math.round(f1 + (f3 * f2 + f4)), true, false);
  }
  
  private void updateGestureExclusionRects()
  {
    Drawable localDrawable = this.mThumb;
    if (localDrawable == null)
    {
      super.setSystemGestureExclusionRects(this.mUserGestureExclusionRects);
      return;
    }
    this.mGestureExclusionRects.clear();
    localDrawable.copyBounds(this.mThumbRect);
    this.mGestureExclusionRects.add(this.mThumbRect);
    this.mGestureExclusionRects.addAll(this.mUserGestureExclusionRects);
    super.setSystemGestureExclusionRects(this.mGestureExclusionRects);
  }
  
  private void updateThumbAndTrackPos(int paramInt1, int paramInt2)
  {
    int i = paramInt2 - this.mPaddingTop - this.mPaddingBottom;
    Drawable localDrawable1 = getCurrentDrawable();
    Drawable localDrawable2 = this.mThumb;
    int j = Math.min(this.mMaxHeight, i);
    if (localDrawable2 == null) {
      paramInt2 = 0;
    } else {
      paramInt2 = localDrawable2.getIntrinsicHeight();
    }
    int k;
    if (paramInt2 > j)
    {
      i = (i - paramInt2) / 2;
      k = (paramInt2 - j) / 2 + i;
      paramInt2 = i;
      i = k;
    }
    else
    {
      k = (i - j) / 2;
      i = k;
      paramInt2 = k + (j - paramInt2) / 2;
    }
    if (localDrawable1 != null) {
      localDrawable1.setBounds(0, i, paramInt1 - this.mPaddingRight - this.mPaddingLeft, i + j);
    }
    if (localDrawable2 != null) {
      setThumbPos(paramInt1, localDrawable2, getScale(), paramInt2);
    }
  }
  
  boolean canUserSetProgress()
  {
    boolean bool;
    if ((!isIndeterminate()) && (isEnabled())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  @UnsupportedAppUsage
  void drawThumb(Canvas paramCanvas)
  {
    if (this.mThumb != null)
    {
      int i = paramCanvas.save();
      paramCanvas.translate(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
      this.mThumb.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
    }
  }
  
  protected void drawTickMarks(Canvas paramCanvas)
  {
    if (this.mTickMark != null)
    {
      int i = getMax() - getMin();
      int j = 1;
      if (i > 1)
      {
        int k = this.mTickMark.getIntrinsicWidth();
        int m = this.mTickMark.getIntrinsicHeight();
        if (k >= 0) {
          k /= 2;
        } else {
          k = 1;
        }
        if (m >= 0) {
          j = m / 2;
        }
        this.mTickMark.setBounds(-k, -j, k, j);
        float f = (getWidth() - this.mPaddingLeft - this.mPaddingRight) / i;
        j = paramCanvas.save();
        paramCanvas.translate(this.mPaddingLeft, getHeight() / 2);
        for (k = 0; k <= i; k++)
        {
          this.mTickMark.draw(paramCanvas);
          paramCanvas.translate(f, 0.0F);
        }
        paramCanvas.restoreToCount(j);
      }
    }
  }
  
  void drawTrack(Canvas paramCanvas)
  {
    Drawable localDrawable = this.mThumb;
    if ((localDrawable != null) && (this.mSplitTrack))
    {
      Insets localInsets = localDrawable.getOpticalInsets();
      Rect localRect = this.mTempRect;
      localDrawable.copyBounds(localRect);
      localRect.offset(this.mPaddingLeft - this.mThumbOffset, this.mPaddingTop);
      localRect.left += localInsets.left;
      localRect.right -= localInsets.right;
      int i = paramCanvas.save();
      paramCanvas.clipRect(localRect, Region.Op.DIFFERENCE);
      super.drawTrack(paramCanvas);
      drawTickMarks(paramCanvas);
      paramCanvas.restoreToCount(i);
    }
    else
    {
      super.drawTrack(paramCanvas);
      drawTickMarks(paramCanvas);
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = getProgressDrawable();
    if ((localDrawable != null) && (this.mDisabledAlpha < 1.0F))
    {
      int i;
      if (isEnabled()) {
        i = 255;
      } else {
        i = (int)(this.mDisabledAlpha * 255.0F);
      }
      localDrawable.setAlpha(i);
    }
    localDrawable = this.mThumb;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
    localDrawable = this.mTickMark;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return AbsSeekBar.class.getName();
  }
  
  public int getKeyProgressIncrement()
  {
    return this.mKeyProgressIncrement;
  }
  
  public boolean getSplitTrack()
  {
    return this.mSplitTrack;
  }
  
  public Drawable getThumb()
  {
    return this.mThumb;
  }
  
  public int getThumbOffset()
  {
    return this.mThumbOffset;
  }
  
  public BlendMode getThumbTintBlendMode()
  {
    return this.mThumbBlendMode;
  }
  
  public ColorStateList getThumbTintList()
  {
    return this.mThumbTintList;
  }
  
  public PorterDuff.Mode getThumbTintMode()
  {
    Object localObject = this.mThumbBlendMode;
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public Drawable getTickMark()
  {
    return this.mTickMark;
  }
  
  public BlendMode getTickMarkTintBlendMode()
  {
    return this.mTickMarkBlendMode;
  }
  
  public ColorStateList getTickMarkTintList()
  {
    return this.mTickMarkTintList;
  }
  
  public PorterDuff.Mode getTickMarkTintMode()
  {
    Object localObject = this.mTickMarkBlendMode;
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
    }
    localDrawable = this.mTickMark;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    try
    {
      super.onDraw(paramCanvas);
      drawThumb(paramCanvas);
      return;
    }
    finally
    {
      paramCanvas = finally;
      throw paramCanvas;
    }
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (isEnabled())
    {
      int i = getProgress();
      if (i > getMin()) {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
      }
      if (i < getMax()) {
        paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
      }
    }
  }
  
  void onKeyChange() {}
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (isEnabled())
    {
      int i = this.mKeyProgressIncrement;
      if (paramInt != 21)
      {
        j = i;
        if (paramInt == 22) {
          break label58;
        }
        if (paramInt != 69)
        {
          j = i;
          if (paramInt == 70) {
            break label58;
          }
          j = i;
          if (paramInt == 81) {
            break label58;
          }
          break label95;
        }
      }
      int j = -i;
      label58:
      if (isLayoutRtl()) {
        j = -j;
      }
      if (setProgressInternal(getProgress() + j, true, true))
      {
        onKeyChange();
        return true;
      }
    }
    label95:
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    try
    {
      Drawable localDrawable = getCurrentDrawable();
      if (this.mThumb == null) {
        i = 0;
      } else {
        i = this.mThumb.getIntrinsicHeight();
      }
      int j = 0;
      int k = 0;
      if (localDrawable != null)
      {
        j = Math.max(this.mMinWidth, Math.min(this.mMaxWidth, localDrawable.getIntrinsicWidth()));
        k = Math.max(i, Math.max(this.mMinHeight, Math.min(this.mMaxHeight, localDrawable.getIntrinsicHeight())));
      }
      int m = this.mPaddingLeft;
      int n = this.mPaddingRight;
      int i = this.mPaddingTop;
      int i1 = this.mPaddingBottom;
      setMeasuredDimension(resolveSizeAndState(j + (m + n), paramInt1, 0), resolveSizeAndState(k + (i + i1), paramInt2, 0));
      return;
    }
    finally {}
  }
  
  public void onResolveDrawables(int paramInt)
  {
    super.onResolveDrawables(paramInt);
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    Drawable localDrawable = this.mThumb;
    if (localDrawable != null)
    {
      setThumbPos(getWidth(), localDrawable, getScale(), Integer.MIN_VALUE);
      invalidate();
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateThumbAndTrackPos(paramInt1, paramInt2);
  }
  
  void onStartTrackingTouch()
  {
    this.mIsDragging = true;
  }
  
  void onStopTrackingTouch()
  {
    this.mIsDragging = false;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (Android_Widget_AbsSeekBar.Extension.get().getExtension() != null) {
      return ((Android_Widget_AbsSeekBar.Interface)Android_Widget_AbsSeekBar.Extension.get().getExtension().asInterface()).onTouchEvent(this, paramMotionEvent);
    }
    return originalOnTouchEvent(paramMotionEvent);
  }
  
  void onVisualProgressChanged(int paramInt, float paramFloat)
  {
    super.onVisualProgressChanged(paramInt, paramFloat);
    if (paramInt == 16908301)
    {
      Drawable localDrawable = this.mThumb;
      if (localDrawable != null)
      {
        setThumbPos(getWidth(), localDrawable, paramFloat, Integer.MIN_VALUE);
        invalidate();
      }
    }
  }
  
  boolean originalOnTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mIsUserSeekable) && (isEnabled()))
    {
      int i = paramMotionEvent.getAction();
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2)
          {
            if (i == 3)
            {
              if (this.mIsDragging)
              {
                onStopTrackingTouch();
                setPressed(false);
              }
              invalidate();
            }
          }
          else if (this.mIsDragging) {
            trackTouchEvent(paramMotionEvent);
          } else if (Math.abs(paramMotionEvent.getX() - this.mTouchDownX) > this.mScaledTouchSlop) {
            startDrag(paramMotionEvent);
          }
        }
        else
        {
          if (this.mIsDragging)
          {
            trackTouchEvent(paramMotionEvent);
            onStopTrackingTouch();
            setPressed(false);
          }
          else
          {
            onStartTrackingTouch();
            trackTouchEvent(paramMotionEvent);
            onStopTrackingTouch();
          }
          invalidate();
        }
      }
      else if (isInScrollingContainer()) {
        this.mTouchDownX = paramMotionEvent.getX();
      } else {
        startDrag(paramMotionEvent);
      }
      return true;
    }
    return false;
  }
  
  public boolean performAccessibilityActionInternal(int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityActionInternal(paramInt, paramBundle)) {
      return true;
    }
    if (!isEnabled()) {
      return false;
    }
    if ((paramInt != 4096) && (paramInt != 8192))
    {
      if (paramInt != 16908349) {
        return false;
      }
      if (!canUserSetProgress()) {
        return false;
      }
      if ((paramBundle != null) && (paramBundle.containsKey("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE"))) {
        return setProgressInternal((int)paramBundle.getFloat("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE"), true, true);
      }
      return false;
    }
    if (!canUserSetProgress()) {
      return false;
    }
    int i = Math.max(1, Math.round((getMax() - getMin()) / 20.0F));
    int j = i;
    if (paramInt == 8192) {
      j = -i;
    }
    if (setProgressInternal(getProgress() + j, true, true))
    {
      onKeyChange();
      return true;
    }
    return false;
  }
  
  public void setKeyProgressIncrement(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = -paramInt;
    }
    this.mKeyProgressIncrement = paramInt;
  }
  
  public void setMax(int paramInt)
  {
    try
    {
      super.setMax(paramInt);
      paramInt = getMax() - getMin();
      if ((this.mKeyProgressIncrement == 0) || (paramInt / this.mKeyProgressIncrement > 20)) {
        setKeyProgressIncrement(Math.max(1, Math.round(paramInt / 20.0F)));
      }
      return;
    }
    finally {}
  }
  
  public void setMin(int paramInt)
  {
    try
    {
      super.setMin(paramInt);
      paramInt = getMax() - getMin();
      if ((this.mKeyProgressIncrement == 0) || (paramInt / this.mKeyProgressIncrement > 20)) {
        setKeyProgressIncrement(Math.max(1, Math.round(paramInt / 20.0F)));
      }
      return;
    }
    finally {}
  }
  
  public void setSplitTrack(boolean paramBoolean)
  {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setSystemGestureExclusionRects(List<Rect> paramList)
  {
    Preconditions.checkNotNull(paramList, "rects must not be null");
    this.mUserGestureExclusionRects = paramList;
    updateGestureExclusionRects();
  }
  
  public void setThumb(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mThumb;
    int i;
    if ((localDrawable != null) && (paramDrawable != localDrawable))
    {
      localDrawable.setCallback(null);
      i = 1;
    }
    else
    {
      i = 0;
    }
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if (canResolveLayoutDirection()) {
        paramDrawable.setLayoutDirection(getLayoutDirection());
      }
      this.mThumbOffset = (paramDrawable.getIntrinsicWidth() / 2);
      if ((i != 0) && ((paramDrawable.getIntrinsicWidth() != this.mThumb.getIntrinsicWidth()) || (paramDrawable.getIntrinsicHeight() != this.mThumb.getIntrinsicHeight()))) {
        requestLayout();
      }
    }
    this.mThumb = paramDrawable;
    applyThumbTint();
    invalidate();
    if (i != 0)
    {
      updateThumbAndTrackPos(getWidth(), getHeight());
      if ((paramDrawable != null) && (paramDrawable.isStateful())) {
        paramDrawable.setState(getDrawableState());
      }
    }
  }
  
  public void setThumbOffset(int paramInt)
  {
    this.mThumbOffset = paramInt;
    invalidate();
  }
  
  public void setThumbTintBlendMode(BlendMode paramBlendMode)
  {
    this.mThumbBlendMode = paramBlendMode;
    this.mHasThumbBlendMode = true;
    applyThumbTint();
  }
  
  public void setThumbTintList(ColorStateList paramColorStateList)
  {
    this.mThumbTintList = paramColorStateList;
    this.mHasThumbTint = true;
    applyThumbTint();
  }
  
  public void setThumbTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setThumbTintBlendMode(paramMode);
  }
  
  public void setTickMark(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mTickMark;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    this.mTickMark = paramDrawable;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      paramDrawable.setLayoutDirection(getLayoutDirection());
      if (paramDrawable.isStateful()) {
        paramDrawable.setState(getDrawableState());
      }
      applyTickMarkTint();
    }
    invalidate();
  }
  
  public void setTickMarkTintBlendMode(BlendMode paramBlendMode)
  {
    this.mTickMarkBlendMode = paramBlendMode;
    this.mHasTickMarkBlendMode = true;
    applyTickMarkTint();
  }
  
  public void setTickMarkTintList(ColorStateList paramColorStateList)
  {
    this.mTickMarkTintList = paramColorStateList;
    this.mHasTickMarkTint = true;
    applyTickMarkTint();
  }
  
  public void setTickMarkTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setTickMarkTintBlendMode(paramMode);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((paramDrawable != this.mThumb) && (paramDrawable != this.mTickMark) && (!super.verifyDrawable(paramDrawable))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AbsSeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */