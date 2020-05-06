package android.widget;

import android.animation.ObjectAnimator;
import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout.Builder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.TransformationMethod2;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R.styleable;
import java.util.List;

public class Switch
  extends CompoundButton
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private static final int MONOSPACE = 3;
  private static final int SANS = 1;
  private static final int SERIF = 2;
  private static final int THUMB_ANIMATION_DURATION = 250;
  private static final FloatProperty<Switch> THUMB_POS = new FloatProperty("thumbPos")
  {
    public Float get(Switch paramAnonymousSwitch)
    {
      return Float.valueOf(paramAnonymousSwitch.mThumbPosition);
    }
    
    public void setValue(Switch paramAnonymousSwitch, float paramAnonymousFloat)
    {
      paramAnonymousSwitch.setThumbPosition(paramAnonymousFloat);
    }
  };
  private static final int TOUCH_MODE_DOWN = 1;
  private static final int TOUCH_MODE_DRAGGING = 2;
  private static final int TOUCH_MODE_IDLE = 0;
  private boolean mHasThumbTint = false;
  private boolean mHasThumbTintMode = false;
  private boolean mHasTrackTint = false;
  private boolean mHasTrackTintMode = false;
  private int mMinFlingVelocity;
  @UnsupportedAppUsage
  private Layout mOffLayout;
  @UnsupportedAppUsage
  private Layout mOnLayout;
  private ObjectAnimator mPositionAnimator;
  private boolean mShowText;
  private boolean mSplitTrack;
  private int mSwitchBottom;
  @UnsupportedAppUsage
  private int mSwitchHeight;
  private int mSwitchLeft;
  @UnsupportedAppUsage
  private int mSwitchMinWidth;
  private int mSwitchPadding;
  private int mSwitchRight;
  private int mSwitchTop;
  private TransformationMethod2 mSwitchTransformationMethod;
  @UnsupportedAppUsage
  private int mSwitchWidth;
  private final Rect mTempRect = new Rect();
  private ColorStateList mTextColors;
  private CharSequence mTextOff;
  private CharSequence mTextOn;
  private TextPaint mTextPaint = new TextPaint(1);
  private BlendMode mThumbBlendMode = null;
  @UnsupportedAppUsage
  private Drawable mThumbDrawable;
  private float mThumbPosition;
  private int mThumbTextPadding;
  private ColorStateList mThumbTintList = null;
  @UnsupportedAppUsage
  private int mThumbWidth;
  private int mTouchMode;
  private int mTouchSlop;
  private float mTouchX;
  private float mTouchY;
  private BlendMode mTrackBlendMode = null;
  @UnsupportedAppUsage
  private Drawable mTrackDrawable;
  private ColorStateList mTrackTintList = null;
  private boolean mUseFallbackLineSpacing;
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  
  public Switch(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public Switch(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843839);
  }
  
  public Switch(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public Switch(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    Object localObject = getResources();
    this.mTextPaint.density = ((Resources)localObject).getDisplayMetrics().density;
    this.mTextPaint.setCompatibilityScaling(((Resources)localObject).getCompatibilityInfo().applicationScale);
    localObject = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.Switch, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.Switch, paramAttributeSet, (TypedArray)localObject, paramInt1, paramInt2);
    this.mThumbDrawable = ((TypedArray)localObject).getDrawable(2);
    paramAttributeSet = this.mThumbDrawable;
    if (paramAttributeSet != null) {
      paramAttributeSet.setCallback(this);
    }
    this.mTrackDrawable = ((TypedArray)localObject).getDrawable(4);
    paramAttributeSet = this.mTrackDrawable;
    if (paramAttributeSet != null) {
      paramAttributeSet.setCallback(this);
    }
    this.mTextOn = ((TypedArray)localObject).getText(0);
    this.mTextOff = ((TypedArray)localObject).getText(1);
    this.mShowText = ((TypedArray)localObject).getBoolean(11, true);
    this.mThumbTextPadding = ((TypedArray)localObject).getDimensionPixelSize(7, 0);
    this.mSwitchMinWidth = ((TypedArray)localObject).getDimensionPixelSize(5, 0);
    this.mSwitchPadding = ((TypedArray)localObject).getDimensionPixelSize(6, 0);
    this.mSplitTrack = ((TypedArray)localObject).getBoolean(8, false);
    boolean bool;
    if (paramContext.getApplicationInfo().targetSdkVersion >= 28) {
      bool = true;
    } else {
      bool = false;
    }
    this.mUseFallbackLineSpacing = bool;
    paramAttributeSet = ((TypedArray)localObject).getColorStateList(9);
    if (paramAttributeSet != null)
    {
      this.mThumbTintList = paramAttributeSet;
      this.mHasThumbTint = true;
    }
    paramAttributeSet = Drawable.parseBlendMode(((TypedArray)localObject).getInt(10, -1), null);
    if (this.mThumbBlendMode != paramAttributeSet)
    {
      this.mThumbBlendMode = paramAttributeSet;
      this.mHasThumbTintMode = true;
    }
    if ((this.mHasThumbTint) || (this.mHasThumbTintMode)) {
      applyThumbTint();
    }
    paramAttributeSet = ((TypedArray)localObject).getColorStateList(12);
    if (paramAttributeSet != null)
    {
      this.mTrackTintList = paramAttributeSet;
      this.mHasTrackTint = true;
    }
    paramAttributeSet = Drawable.parseBlendMode(((TypedArray)localObject).getInt(13, -1), null);
    if (this.mTrackBlendMode != paramAttributeSet)
    {
      this.mTrackBlendMode = paramAttributeSet;
      this.mHasTrackTintMode = true;
    }
    if ((this.mHasTrackTint) || (this.mHasTrackTintMode)) {
      applyTrackTint();
    }
    paramInt1 = ((TypedArray)localObject).getResourceId(3, 0);
    if (paramInt1 != 0) {
      setSwitchTextAppearance(paramContext, paramInt1);
    }
    ((TypedArray)localObject).recycle();
    paramContext = ViewConfiguration.get(paramContext);
    this.mTouchSlop = paramContext.getScaledTouchSlop();
    this.mMinFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
    refreshDrawableState();
    setChecked(isChecked());
  }
  
  private void animateThumbToCheckedState(boolean paramBoolean)
  {
    float f;
    if (paramBoolean) {
      f = 1.0F;
    } else {
      f = 0.0F;
    }
    this.mPositionAnimator = ObjectAnimator.ofFloat(this, THUMB_POS, new float[] { f });
    this.mPositionAnimator.setDuration(250L);
    this.mPositionAnimator.setAutoCancel(true);
    this.mPositionAnimator.start();
  }
  
  private void applyThumbTint()
  {
    if ((this.mThumbDrawable != null) && ((this.mHasThumbTint) || (this.mHasThumbTintMode)))
    {
      this.mThumbDrawable = this.mThumbDrawable.mutate();
      if (this.mHasThumbTint) {
        this.mThumbDrawable.setTintList(this.mThumbTintList);
      }
      if (this.mHasThumbTintMode) {
        this.mThumbDrawable.setTintBlendMode(this.mThumbBlendMode);
      }
      if (this.mThumbDrawable.isStateful()) {
        this.mThumbDrawable.setState(getDrawableState());
      }
    }
  }
  
  private void applyTrackTint()
  {
    if ((this.mTrackDrawable != null) && ((this.mHasTrackTint) || (this.mHasTrackTintMode)))
    {
      this.mTrackDrawable = this.mTrackDrawable.mutate();
      if (this.mHasTrackTint) {
        this.mTrackDrawable.setTintList(this.mTrackTintList);
      }
      if (this.mHasTrackTintMode) {
        this.mTrackDrawable.setTintBlendMode(this.mTrackBlendMode);
      }
      if (this.mTrackDrawable.isStateful()) {
        this.mTrackDrawable.setState(getDrawableState());
      }
    }
  }
  
  @UnsupportedAppUsage
  private void cancelPositionAnimator()
  {
    ObjectAnimator localObjectAnimator = this.mPositionAnimator;
    if (localObjectAnimator != null) {
      localObjectAnimator.cancel();
    }
  }
  
  private void cancelSuperTouch(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(3);
    super.onTouchEvent(paramMotionEvent);
    paramMotionEvent.recycle();
  }
  
  private boolean getTargetCheckedState()
  {
    boolean bool;
    if (this.mThumbPosition > 0.5F) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private int getThumbOffset()
  {
    float f;
    if (isLayoutRtl()) {
      f = 1.0F - this.mThumbPosition;
    } else {
      f = this.mThumbPosition;
    }
    return (int)(getThumbScrollRange() * f + 0.5F);
  }
  
  private int getThumbScrollRange()
  {
    Object localObject = this.mTrackDrawable;
    if (localObject != null)
    {
      Rect localRect = this.mTempRect;
      ((Drawable)localObject).getPadding(localRect);
      localObject = this.mThumbDrawable;
      if (localObject != null) {
        localObject = ((Drawable)localObject).getOpticalInsets();
      } else {
        localObject = Insets.NONE;
      }
      return this.mSwitchWidth - this.mThumbWidth - localRect.left - localRect.right - ((Insets)localObject).left - ((Insets)localObject).right;
    }
    return 0;
  }
  
  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    Drawable localDrawable = this.mThumbDrawable;
    boolean bool1 = false;
    if (localDrawable == null) {
      return false;
    }
    int i = getThumbOffset();
    this.mThumbDrawable.getPadding(this.mTempRect);
    int j = this.mSwitchTop;
    int k = this.mTouchSlop;
    int m = this.mSwitchLeft + i - k;
    int n = this.mThumbWidth;
    int i1 = this.mTempRect.left;
    i = this.mTempRect.right;
    int i2 = this.mTouchSlop;
    int i3 = this.mSwitchBottom;
    boolean bool2 = bool1;
    if (paramFloat1 > m)
    {
      bool2 = bool1;
      if (paramFloat1 < n + m + i1 + i + i2)
      {
        bool2 = bool1;
        if (paramFloat2 > j - k)
        {
          bool2 = bool1;
          if (paramFloat2 < i3 + i2) {
            bool2 = true;
          }
        }
      }
    }
    return bool2;
  }
  
  private Layout makeLayout(CharSequence paramCharSequence)
  {
    TransformationMethod2 localTransformationMethod2 = this.mSwitchTransformationMethod;
    if (localTransformationMethod2 != null) {
      paramCharSequence = localTransformationMethod2.getTransformation(paramCharSequence, this);
    }
    int i = (int)Math.ceil(Layout.getDesiredWidth(paramCharSequence, 0, paramCharSequence.length(), this.mTextPaint, getTextDirectionHeuristic()));
    return StaticLayout.Builder.obtain(paramCharSequence, 0, paramCharSequence.length(), this.mTextPaint, i).setUseLineSpacingFromFallbacks(this.mUseFallbackLineSpacing).build();
  }
  
  private void setSwitchTypefaceByIndex(int paramInt1, int paramInt2)
  {
    Typeface localTypeface = null;
    if (paramInt1 != 1)
    {
      if (paramInt1 != 2)
      {
        if (paramInt1 == 3) {
          localTypeface = Typeface.MONOSPACE;
        }
      }
      else {
        localTypeface = Typeface.SERIF;
      }
    }
    else {
      localTypeface = Typeface.SANS_SERIF;
    }
    setSwitchTypeface(localTypeface, paramInt2);
  }
  
  @UnsupportedAppUsage
  private void setThumbPosition(float paramFloat)
  {
    this.mThumbPosition = paramFloat;
    invalidate();
  }
  
  private void stopDrag(MotionEvent paramMotionEvent)
  {
    this.mTouchMode = 0;
    int i = paramMotionEvent.getAction();
    boolean bool1 = true;
    if ((i == 1) && (isEnabled())) {
      i = 1;
    } else {
      i = 0;
    }
    boolean bool2 = isChecked();
    if (i != 0)
    {
      this.mVelocityTracker.computeCurrentVelocity(1000);
      float f = this.mVelocityTracker.getXVelocity();
      if (Math.abs(f) > this.mMinFlingVelocity)
      {
        if (isLayoutRtl()) {
          if (f >= 0.0F) {
            break label101;
          }
        } else {
          if (f > 0.0F) {
            break label103;
          }
        }
        label101:
        bool1 = false;
      }
      else
      {
        label103:
        bool1 = getTargetCheckedState();
      }
    }
    else
    {
      bool1 = bool2;
    }
    if (bool1 != bool2) {
      playSoundEffect(0);
    }
    setChecked(bool1);
    cancelSuperTouch(paramMotionEvent);
  }
  
  public void draw(Canvas paramCanvas)
  {
    Rect localRect = this.mTempRect;
    int i = this.mSwitchLeft;
    int j = this.mSwitchTop;
    int k = this.mSwitchRight;
    int m = this.mSwitchBottom;
    int n = getThumbOffset() + i;
    Object localObject = this.mThumbDrawable;
    if (localObject != null) {
      localObject = ((Drawable)localObject).getOpticalInsets();
    } else {
      localObject = Insets.NONE;
    }
    Drawable localDrawable = this.mTrackDrawable;
    int i1 = n;
    if (localDrawable != null)
    {
      localDrawable.getPadding(localRect);
      int i2 = n + localRect.left;
      n = j;
      int i3 = m;
      int i4 = i;
      int i5 = n;
      int i6 = k;
      int i7 = i3;
      if (localObject != Insets.NONE)
      {
        i1 = i;
        if (((Insets)localObject).left > localRect.left) {
          i1 = i + (((Insets)localObject).left - localRect.left);
        }
        i = n;
        if (((Insets)localObject).top > localRect.top) {
          i = n + (((Insets)localObject).top - localRect.top);
        }
        n = k;
        if (((Insets)localObject).right > localRect.right) {
          n = k - (((Insets)localObject).right - localRect.right);
        }
        i4 = i1;
        i5 = i;
        i6 = n;
        i7 = i3;
        if (((Insets)localObject).bottom > localRect.bottom)
        {
          i7 = i3 - (((Insets)localObject).bottom - localRect.bottom);
          i6 = n;
          i5 = i;
          i4 = i1;
        }
      }
      this.mTrackDrawable.setBounds(i4, i5, i6, i7);
      i1 = i2;
    }
    localObject = this.mThumbDrawable;
    if (localObject != null)
    {
      ((Drawable)localObject).getPadding(localRect);
      k = i1 - localRect.left;
      i1 = this.mThumbWidth + i1 + localRect.right;
      this.mThumbDrawable.setBounds(k, j, i1, m);
      localObject = getBackground();
      if (localObject != null) {
        ((Drawable)localObject).setHotspotBounds(k, j, i1, m);
      }
    }
    super.draw(paramCanvas);
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    Drawable localDrawable = this.mThumbDrawable;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
    localDrawable = this.mTrackDrawable;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    boolean bool1 = false;
    Drawable localDrawable = this.mThumbDrawable;
    boolean bool2 = bool1;
    if (localDrawable != null)
    {
      bool2 = bool1;
      if (localDrawable.isStateful()) {
        bool2 = false | localDrawable.setState(arrayOfInt);
      }
    }
    localDrawable = this.mTrackDrawable;
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
  
  public CharSequence getAccessibilityClassName()
  {
    return Switch.class.getName();
  }
  
  public int getCompoundPaddingLeft()
  {
    if (!isLayoutRtl()) {
      return super.getCompoundPaddingLeft();
    }
    int i = super.getCompoundPaddingLeft() + this.mSwitchWidth;
    int j = i;
    if (!TextUtils.isEmpty(getText())) {
      j = i + this.mSwitchPadding;
    }
    return j;
  }
  
  public int getCompoundPaddingRight()
  {
    if (isLayoutRtl()) {
      return super.getCompoundPaddingRight();
    }
    int i = super.getCompoundPaddingRight() + this.mSwitchWidth;
    int j = i;
    if (!TextUtils.isEmpty(getText())) {
      j = i + this.mSwitchPadding;
    }
    return j;
  }
  
  public boolean getShowText()
  {
    return this.mShowText;
  }
  
  public boolean getSplitTrack()
  {
    return this.mSplitTrack;
  }
  
  public int getSwitchMinWidth()
  {
    return this.mSwitchMinWidth;
  }
  
  public int getSwitchPadding()
  {
    return this.mSwitchPadding;
  }
  
  public CharSequence getTextOff()
  {
    return this.mTextOff;
  }
  
  public CharSequence getTextOn()
  {
    return this.mTextOn;
  }
  
  public Drawable getThumbDrawable()
  {
    return this.mThumbDrawable;
  }
  
  public int getThumbTextPadding()
  {
    return this.mThumbTextPadding;
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
    Object localObject = getThumbTintBlendMode();
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public Drawable getTrackDrawable()
  {
    return this.mTrackDrawable;
  }
  
  public BlendMode getTrackTintBlendMode()
  {
    return this.mTrackBlendMode;
  }
  
  public ColorStateList getTrackTintList()
  {
    return this.mTrackTintList;
  }
  
  public PorterDuff.Mode getTrackTintMode()
  {
    Object localObject = getTrackTintBlendMode();
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
    Object localObject = this.mThumbDrawable;
    if (localObject != null) {
      ((Drawable)localObject).jumpToCurrentState();
    }
    localObject = this.mTrackDrawable;
    if (localObject != null) {
      ((Drawable)localObject).jumpToCurrentState();
    }
    localObject = this.mPositionAnimator;
    if ((localObject != null) && (((ObjectAnimator)localObject).isStarted()))
    {
      this.mPositionAnimator.end();
      this.mPositionAnimator = null;
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (isChecked()) {
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    }
    return arrayOfInt;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Object localObject1 = this.mTempRect;
    Object localObject2 = this.mTrackDrawable;
    if (localObject2 != null) {
      ((Drawable)localObject2).getPadding((Rect)localObject1);
    } else {
      ((Rect)localObject1).setEmpty();
    }
    int i = this.mSwitchTop;
    int j = this.mSwitchBottom;
    int k = ((Rect)localObject1).top;
    int m = ((Rect)localObject1).bottom;
    Object localObject3 = this.mThumbDrawable;
    Object localObject4;
    int n;
    if (localObject2 != null) {
      if ((this.mSplitTrack) && (localObject3 != null))
      {
        localObject4 = ((Drawable)localObject3).getOpticalInsets();
        ((Drawable)localObject3).copyBounds((Rect)localObject1);
        ((Rect)localObject1).left += ((Insets)localObject4).left;
        ((Rect)localObject1).right -= ((Insets)localObject4).right;
        n = paramCanvas.save();
        paramCanvas.clipRect((Rect)localObject1, Region.Op.DIFFERENCE);
        ((Drawable)localObject2).draw(paramCanvas);
        paramCanvas.restoreToCount(n);
      }
      else
      {
        ((Drawable)localObject2).draw(paramCanvas);
      }
    }
    int i1 = paramCanvas.save();
    if (localObject3 != null) {
      ((Drawable)localObject3).draw(paramCanvas);
    }
    if (getTargetCheckedState()) {
      localObject2 = this.mOnLayout;
    } else {
      localObject2 = this.mOffLayout;
    }
    if (localObject2 != null)
    {
      localObject4 = getDrawableState();
      localObject1 = this.mTextColors;
      if (localObject1 != null) {
        this.mTextPaint.setColor(((ColorStateList)localObject1).getColorForState((int[])localObject4, 0));
      }
      this.mTextPaint.drawableState = ((int[])localObject4);
      if (localObject3 != null)
      {
        localObject3 = ((Drawable)localObject3).getBounds();
        n = ((Rect)localObject3).left + ((Rect)localObject3).right;
      }
      else
      {
        n = getWidth();
      }
      int i2 = n / 2;
      n = ((Layout)localObject2).getWidth() / 2;
      m = (k + i + (j - m)) / 2;
      k = ((Layout)localObject2).getHeight() / 2;
      paramCanvas.translate(i2 - n, m - k);
      ((Layout)localObject2).draw(paramCanvas);
    }
    paramCanvas.restoreToCount(i1);
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    CharSequence localCharSequence1;
    if (isChecked()) {
      localCharSequence1 = this.mTextOn;
    } else {
      localCharSequence1 = this.mTextOff;
    }
    if (!TextUtils.isEmpty(localCharSequence1))
    {
      CharSequence localCharSequence2 = paramAccessibilityNodeInfo.getText();
      if (TextUtils.isEmpty(localCharSequence2))
      {
        paramAccessibilityNodeInfo.setText(localCharSequence1);
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localCharSequence2);
        localStringBuilder.append(' ');
        localStringBuilder.append(localCharSequence1);
        paramAccessibilityNodeInfo.setText(localStringBuilder);
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt2 = 0;
    paramInt1 = 0;
    if (this.mThumbDrawable != null)
    {
      Rect localRect = this.mTempRect;
      Object localObject = this.mTrackDrawable;
      if (localObject != null) {
        ((Drawable)localObject).getPadding(localRect);
      } else {
        localRect.setEmpty();
      }
      localObject = this.mThumbDrawable.getOpticalInsets();
      paramInt2 = Math.max(0, ((Insets)localObject).left - localRect.left);
      paramInt1 = Math.max(0, ((Insets)localObject).right - localRect.right);
    }
    if (isLayoutRtl())
    {
      paramInt3 = getPaddingLeft() + paramInt2;
      paramInt4 = this.mSwitchWidth + paramInt3 - paramInt2 - paramInt1;
    }
    else
    {
      paramInt4 = getWidth() - getPaddingRight() - paramInt1;
      paramInt3 = paramInt4 - this.mSwitchWidth + paramInt2 + paramInt1;
    }
    paramInt1 = getGravity() & 0x70;
    if (paramInt1 != 16)
    {
      if (paramInt1 != 80)
      {
        paramInt1 = getPaddingTop();
        paramInt2 = this.mSwitchHeight + paramInt1;
      }
      else
      {
        paramInt2 = getHeight() - getPaddingBottom();
        paramInt1 = paramInt2 - this.mSwitchHeight;
      }
    }
    else
    {
      paramInt1 = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2;
      paramInt2 = this.mSwitchHeight;
      paramInt1 -= paramInt2 / 2;
      paramInt2 += paramInt1;
    }
    this.mSwitchLeft = paramInt3;
    this.mSwitchTop = paramInt1;
    this.mSwitchBottom = paramInt2;
    this.mSwitchRight = paramInt4;
  }
  
  public void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mShowText)
    {
      if (this.mOnLayout == null) {
        this.mOnLayout = makeLayout(this.mTextOn);
      }
      if (this.mOffLayout == null) {
        this.mOffLayout = makeLayout(this.mTextOff);
      }
    }
    Object localObject = this.mTempRect;
    Drawable localDrawable = this.mThumbDrawable;
    int i;
    if (localDrawable != null)
    {
      localDrawable.getPadding((Rect)localObject);
      i = this.mThumbDrawable.getIntrinsicWidth() - ((Rect)localObject).left - ((Rect)localObject).right;
      j = this.mThumbDrawable.getIntrinsicHeight();
    }
    else
    {
      i = 0;
      j = 0;
    }
    if (this.mShowText) {
      k = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + this.mThumbTextPadding * 2;
    } else {
      k = 0;
    }
    this.mThumbWidth = Math.max(k, i);
    localDrawable = this.mTrackDrawable;
    if (localDrawable != null)
    {
      localDrawable.getPadding((Rect)localObject);
      i = this.mTrackDrawable.getIntrinsicHeight();
    }
    else
    {
      ((Rect)localObject).setEmpty();
      i = 0;
    }
    int m = ((Rect)localObject).left;
    int n = ((Rect)localObject).right;
    localObject = this.mThumbDrawable;
    int i1 = m;
    int k = n;
    if (localObject != null)
    {
      localObject = ((Drawable)localObject).getOpticalInsets();
      i1 = Math.max(m, ((Insets)localObject).left);
      k = Math.max(n, ((Insets)localObject).right);
    }
    k = Math.max(this.mSwitchMinWidth, this.mThumbWidth * 2 + i1 + k);
    int j = Math.max(i, j);
    this.mSwitchWidth = k;
    this.mSwitchHeight = j;
    super.onMeasure(paramInt1, paramInt2);
    if (getMeasuredHeight() < j) {
      setMeasuredDimension(getMeasuredWidthAndState(), j);
    }
  }
  
  public void onPopulateAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onPopulateAccessibilityEventInternal(paramAccessibilityEvent);
    CharSequence localCharSequence;
    if (isChecked()) {
      localCharSequence = this.mTextOn;
    } else {
      localCharSequence = this.mTextOff;
    }
    if (localCharSequence != null) {
      paramAccessibilityEvent.getText().add(localCharSequence);
    }
  }
  
  protected void onProvideStructure(ViewStructure paramViewStructure, int paramInt1, int paramInt2)
  {
    CharSequence localCharSequence1;
    if (isChecked()) {
      localCharSequence1 = this.mTextOn;
    } else {
      localCharSequence1 = this.mTextOff;
    }
    if (!TextUtils.isEmpty(localCharSequence1))
    {
      CharSequence localCharSequence2 = paramViewStructure.getText();
      if (TextUtils.isEmpty(localCharSequence2))
      {
        paramViewStructure.setText(localCharSequence1);
      }
      else
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(localCharSequence2);
        localStringBuilder.append(' ');
        localStringBuilder.append(localCharSequence1);
        paramViewStructure.setText(localStringBuilder);
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int i = paramMotionEvent.getActionMasked();
    float f2;
    float f3;
    if (i != 0)
    {
      if (i != 1) {
        if (i != 2)
        {
          if (i != 3) {
            break label325;
          }
        }
        else
        {
          i = this.mTouchMode;
          if (i != 0) {
            if (i != 1)
            {
              if (i == 2)
              {
                float f1 = paramMotionEvent.getX();
                i = getThumbScrollRange();
                f2 = f1 - this.mTouchX;
                if (i != 0) {
                  f2 /= i;
                } else if (f2 > 0.0F) {
                  f2 = 1.0F;
                } else {
                  f2 = -1.0F;
                }
                f3 = f2;
                if (isLayoutRtl()) {
                  f3 = -f2;
                }
                f2 = MathUtils.constrain(this.mThumbPosition + f3, 0.0F, 1.0F);
                if (f2 != this.mThumbPosition)
                {
                  this.mTouchX = f1;
                  setThumbPosition(f2);
                }
                return true;
              }
            }
            else
            {
              f2 = paramMotionEvent.getX();
              f3 = paramMotionEvent.getY();
              if ((Math.abs(f2 - this.mTouchX) > this.mTouchSlop) || (Math.abs(f3 - this.mTouchY) > this.mTouchSlop))
              {
                this.mTouchMode = 2;
                getParent().requestDisallowInterceptTouchEvent(true);
                this.mTouchX = f2;
                this.mTouchY = f3;
                return true;
              }
            }
          }
          break label325;
        }
      }
      if (this.mTouchMode == 2)
      {
        stopDrag(paramMotionEvent);
        super.onTouchEvent(paramMotionEvent);
        return true;
      }
      this.mTouchMode = 0;
      this.mVelocityTracker.clear();
    }
    else
    {
      f3 = paramMotionEvent.getX();
      f2 = paramMotionEvent.getY();
      if ((isEnabled()) && (hitThumb(f3, f2)))
      {
        this.mTouchMode = 1;
        this.mTouchX = f3;
        this.mTouchY = f2;
      }
    }
    label325:
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    paramBoolean = isChecked();
    if ((isAttachedToWindow()) && (isLaidOut()))
    {
      animateThumbToCheckedState(paramBoolean);
    }
    else
    {
      cancelPositionAnimator();
      float f;
      if (paramBoolean) {
        f = 1.0F;
      } else {
        f = 0.0F;
      }
      setThumbPosition(f);
    }
  }
  
  public void setShowText(boolean paramBoolean)
  {
    if (this.mShowText != paramBoolean)
    {
      this.mShowText = paramBoolean;
      requestLayout();
    }
  }
  
  public void setSplitTrack(boolean paramBoolean)
  {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setSwitchMinWidth(int paramInt)
  {
    this.mSwitchMinWidth = paramInt;
    requestLayout();
  }
  
  public void setSwitchPadding(int paramInt)
  {
    this.mSwitchPadding = paramInt;
    requestLayout();
  }
  
  public void setSwitchTextAppearance(Context paramContext, int paramInt)
  {
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramInt, R.styleable.TextAppearance);
    paramContext = localTypedArray.getColorStateList(3);
    if (paramContext != null) {
      this.mTextColors = paramContext;
    } else {
      this.mTextColors = getTextColors();
    }
    paramInt = localTypedArray.getDimensionPixelSize(0, 0);
    if ((paramInt != 0) && (paramInt != this.mTextPaint.getTextSize()))
    {
      this.mTextPaint.setTextSize(paramInt);
      requestLayout();
    }
    setSwitchTypefaceByIndex(localTypedArray.getInt(1, -1), localTypedArray.getInt(2, -1));
    if (localTypedArray.getBoolean(11, false))
    {
      this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
      this.mSwitchTransformationMethod.setLengthChangesAllowed(true);
    }
    else
    {
      this.mSwitchTransformationMethod = null;
    }
    localTypedArray.recycle();
  }
  
  public void setSwitchTypeface(Typeface paramTypeface)
  {
    if (this.mTextPaint.getTypeface() != paramTypeface)
    {
      this.mTextPaint.setTypeface(paramTypeface);
      requestLayout();
      invalidate();
    }
  }
  
  public void setSwitchTypeface(Typeface paramTypeface, int paramInt)
  {
    float f = 0.0F;
    boolean bool = false;
    if (paramInt > 0)
    {
      if (paramTypeface == null) {
        paramTypeface = Typeface.defaultFromStyle(paramInt);
      } else {
        paramTypeface = Typeface.create(paramTypeface, paramInt);
      }
      setSwitchTypeface(paramTypeface);
      int i;
      if (paramTypeface != null) {
        i = paramTypeface.getStyle();
      } else {
        i = 0;
      }
      paramInt = i & paramInt;
      paramTypeface = this.mTextPaint;
      if ((paramInt & 0x1) != 0) {
        bool = true;
      }
      paramTypeface.setFakeBoldText(bool);
      paramTypeface = this.mTextPaint;
      if ((paramInt & 0x2) != 0) {
        f = -0.25F;
      }
      paramTypeface.setTextSkewX(f);
    }
    else
    {
      this.mTextPaint.setFakeBoldText(false);
      this.mTextPaint.setTextSkewX(0.0F);
      setSwitchTypeface(paramTypeface);
    }
  }
  
  public void setTextOff(CharSequence paramCharSequence)
  {
    this.mTextOff = paramCharSequence;
    requestLayout();
  }
  
  public void setTextOn(CharSequence paramCharSequence)
  {
    this.mTextOn = paramCharSequence;
    requestLayout();
  }
  
  public void setThumbDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mThumbDrawable;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    this.mThumbDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    requestLayout();
  }
  
  public void setThumbResource(int paramInt)
  {
    setThumbDrawable(getContext().getDrawable(paramInt));
  }
  
  public void setThumbTextPadding(int paramInt)
  {
    this.mThumbTextPadding = paramInt;
    requestLayout();
  }
  
  public void setThumbTintBlendMode(BlendMode paramBlendMode)
  {
    this.mThumbBlendMode = paramBlendMode;
    this.mHasThumbTintMode = true;
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
  
  public void setTrackDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mTrackDrawable;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    this.mTrackDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
    }
    requestLayout();
  }
  
  public void setTrackResource(int paramInt)
  {
    setTrackDrawable(getContext().getDrawable(paramInt));
  }
  
  public void setTrackTintBlendMode(BlendMode paramBlendMode)
  {
    this.mTrackBlendMode = paramBlendMode;
    this.mHasTrackTintMode = true;
    applyTrackTint();
  }
  
  public void setTrackTintList(ColorStateList paramColorStateList)
  {
    this.mTrackTintList = paramColorStateList;
    this.mHasTrackTint = true;
    applyTrackTint();
  }
  
  public void setTrackTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setTrackTintBlendMode(paramMode);
  }
  
  public void toggle()
  {
    setChecked(isChecked() ^ true);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((!super.verifyDrawable(paramDrawable)) && (paramDrawable != this.mThumbDrawable) && (paramDrawable != this.mTrackDrawable)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/Switch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */