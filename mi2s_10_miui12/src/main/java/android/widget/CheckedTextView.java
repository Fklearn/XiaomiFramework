package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View.BaseSavedState;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R.styleable;

public class CheckedTextView
  extends TextView
  implements Checkable
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private int mBasePadding;
  private BlendMode mCheckMarkBlendMode = null;
  @UnsupportedAppUsage
  private Drawable mCheckMarkDrawable;
  @UnsupportedAppUsage
  private int mCheckMarkGravity = 8388613;
  private int mCheckMarkResource;
  private ColorStateList mCheckMarkTintList = null;
  private int mCheckMarkWidth;
  private boolean mChecked;
  private boolean mHasCheckMarkTint = false;
  private boolean mHasCheckMarkTintMode = false;
  private boolean mNeedRequestlayout;
  
  public CheckedTextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CheckedTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16843720);
  }
  
  public CheckedTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public CheckedTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CheckedTextView, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.CheckedTextView, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramContext = localTypedArray.getDrawable(1);
    if (paramContext != null) {
      setCheckMarkDrawable(paramContext);
    }
    if (localTypedArray.hasValue(3))
    {
      this.mCheckMarkBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(3, -1), this.mCheckMarkBlendMode);
      this.mHasCheckMarkTintMode = true;
    }
    if (localTypedArray.hasValue(2))
    {
      this.mCheckMarkTintList = localTypedArray.getColorStateList(2);
      this.mHasCheckMarkTint = true;
    }
    this.mCheckMarkGravity = localTypedArray.getInt(4, 8388613);
    setChecked(localTypedArray.getBoolean(0, false));
    localTypedArray.recycle();
    applyCheckMarkTint();
  }
  
  private void applyCheckMarkTint()
  {
    if ((this.mCheckMarkDrawable != null) && ((this.mHasCheckMarkTint) || (this.mHasCheckMarkTintMode)))
    {
      this.mCheckMarkDrawable = this.mCheckMarkDrawable.mutate();
      if (this.mHasCheckMarkTint) {
        this.mCheckMarkDrawable.setTintList(this.mCheckMarkTintList);
      }
      if (this.mHasCheckMarkTintMode) {
        this.mCheckMarkDrawable.setTintBlendMode(this.mCheckMarkBlendMode);
      }
      if (this.mCheckMarkDrawable.isStateful()) {
        this.mCheckMarkDrawable.setState(getDrawableState());
      }
    }
  }
  
  private boolean isCheckMarkAtStart()
  {
    boolean bool;
    if ((Gravity.getAbsoluteGravity(this.mCheckMarkGravity, getLayoutDirection()) & 0x7) == 3) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void setBasePadding(boolean paramBoolean)
  {
    if (paramBoolean) {
      this.mBasePadding = this.mPaddingLeft;
    } else {
      this.mBasePadding = this.mPaddingRight;
    }
  }
  
  private void setCheckMarkDrawableInternal(Drawable paramDrawable, int paramInt)
  {
    Drawable localDrawable = this.mCheckMarkDrawable;
    if (localDrawable != null)
    {
      localDrawable.setCallback(null);
      unscheduleDrawable(this.mCheckMarkDrawable);
    }
    localDrawable = this.mCheckMarkDrawable;
    boolean bool1 = true;
    boolean bool2;
    if (paramDrawable != localDrawable) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mNeedRequestlayout = bool2;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if (getVisibility() == 0) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      paramDrawable.setVisible(bool2, false);
      paramDrawable.setState(CHECKED_STATE_SET);
      setMinHeight(paramDrawable.getIntrinsicHeight());
      this.mCheckMarkWidth = paramDrawable.getIntrinsicWidth();
      paramDrawable.setState(getDrawableState());
    }
    else
    {
      this.mCheckMarkWidth = 0;
    }
    this.mCheckMarkDrawable = paramDrawable;
    this.mCheckMarkResource = paramInt;
    applyCheckMarkTint();
    resolvePadding();
  }
  
  private void updatePadding()
  {
    resetPaddingToInitialValues();
    int i;
    if (this.mCheckMarkDrawable != null) {
      i = this.mCheckMarkWidth + this.mBasePadding;
    } else {
      i = this.mBasePadding;
    }
    boolean bool1 = isCheckMarkAtStart();
    boolean bool2 = true;
    boolean bool3 = true;
    if (bool1)
    {
      bool1 = this.mNeedRequestlayout;
      if (this.mPaddingLeft == i) {
        bool3 = false;
      }
      this.mNeedRequestlayout = (bool1 | bool3);
      this.mPaddingLeft = i;
    }
    else
    {
      bool1 = this.mNeedRequestlayout;
      if (this.mPaddingRight != i) {
        bool3 = bool2;
      } else {
        bool3 = false;
      }
      this.mNeedRequestlayout = (bool1 | bool3);
      this.mPaddingRight = i;
    }
    if (this.mNeedRequestlayout)
    {
      requestLayout();
      this.mNeedRequestlayout = false;
    }
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    Drawable localDrawable = this.mCheckMarkDrawable;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mCheckMarkDrawable;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("text:checked", isChecked());
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return CheckedTextView.class.getName();
  }
  
  public Drawable getCheckMarkDrawable()
  {
    return this.mCheckMarkDrawable;
  }
  
  public BlendMode getCheckMarkTintBlendMode()
  {
    return this.mCheckMarkBlendMode;
  }
  
  public ColorStateList getCheckMarkTintList()
  {
    return this.mCheckMarkTintList;
  }
  
  public PorterDuff.Mode getCheckMarkTintMode()
  {
    Object localObject = this.mCheckMarkBlendMode;
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  protected void internalSetPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.internalSetPadding(paramInt1, paramInt2, paramInt3, paramInt4);
    setBasePadding(isCheckMarkAtStart());
  }
  
  @ViewDebug.ExportedProperty
  public boolean isChecked()
  {
    return this.mChecked;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    Drawable localDrawable = this.mCheckMarkDrawable;
    if (localDrawable != null) {
      localDrawable.jumpToCurrentState();
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
    Drawable localDrawable = this.mCheckMarkDrawable;
    if (localDrawable != null)
    {
      int i = getGravity() & 0x70;
      int j = localDrawable.getIntrinsicHeight();
      int k = 0;
      if (i != 16)
      {
        if (i == 80) {
          k = getHeight() - j;
        }
      }
      else {
        k = (getHeight() - j) / 2;
      }
      boolean bool = isCheckMarkAtStart();
      i = getWidth();
      int m = k + j;
      if (bool)
      {
        i = this.mBasePadding;
        j = this.mCheckMarkWidth + i;
      }
      else
      {
        j = i - this.mBasePadding;
        i = j - this.mCheckMarkWidth;
      }
      localDrawable.setBounds(this.mScrollX + i, k, this.mScrollX + j, m);
      localDrawable.draw(paramCanvas);
      paramCanvas = getBackground();
      if (paramCanvas != null) {
        paramCanvas.setHotspotBounds(this.mScrollX + i, k, this.mScrollX + j, m);
      }
    }
  }
  
  public void onInitializeAccessibilityEventInternal(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEventInternal(paramAccessibilityEvent);
    paramAccessibilityEvent.setChecked(this.mChecked);
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setCheckable(true);
    paramAccessibilityNodeInfo.setChecked(this.mChecked);
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setChecked(paramParcelable.checked);
    requestLayout();
  }
  
  public void onRtlPropertiesChanged(int paramInt)
  {
    super.onRtlPropertiesChanged(paramInt);
    updatePadding();
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.checked = isChecked();
    return localSavedState;
  }
  
  public void setCheckMarkDrawable(int paramInt)
  {
    if ((paramInt != 0) && (paramInt == this.mCheckMarkResource)) {
      return;
    }
    Drawable localDrawable;
    if (paramInt != 0) {
      localDrawable = getContext().getDrawable(paramInt);
    } else {
      localDrawable = null;
    }
    setCheckMarkDrawableInternal(localDrawable, paramInt);
  }
  
  public void setCheckMarkDrawable(Drawable paramDrawable)
  {
    setCheckMarkDrawableInternal(paramDrawable, 0);
  }
  
  public void setCheckMarkTintBlendMode(BlendMode paramBlendMode)
  {
    this.mCheckMarkBlendMode = paramBlendMode;
    this.mHasCheckMarkTintMode = true;
    applyCheckMarkTint();
  }
  
  public void setCheckMarkTintList(ColorStateList paramColorStateList)
  {
    this.mCheckMarkTintList = paramColorStateList;
    this.mHasCheckMarkTint = true;
    applyCheckMarkTint();
  }
  
  public void setCheckMarkTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setCheckMarkTintBlendMode(paramMode);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.mChecked != paramBoolean)
    {
      this.mChecked = paramBoolean;
      refreshDrawableState();
      notifyViewAccessibilityStateChangedIfNeeded(0);
    }
  }
  
  @RemotableViewMethod
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    Drawable localDrawable = this.mCheckMarkDrawable;
    if (localDrawable != null)
    {
      boolean bool;
      if (paramInt == 0) {
        bool = true;
      } else {
        bool = false;
      }
      localDrawable.setVisible(bool, false);
    }
  }
  
  public void toggle()
  {
    setChecked(this.mChecked ^ true);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((paramDrawable != this.mCheckMarkDrawable) && (!super.verifyDrawable(paramDrawable))) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public CheckedTextView.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new CheckedTextView.SavedState(paramAnonymousParcel, null);
      }
      
      public CheckedTextView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new CheckedTextView.SavedState[paramAnonymousInt];
      }
    };
    boolean checked;
    
    private SavedState(Parcel paramParcel)
    {
      super();
      this.checked = ((Boolean)paramParcel.readValue(null)).booleanValue();
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("CheckedTextView.SavedState{");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      localStringBuilder.append(" checked=");
      localStringBuilder.append(this.checked);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeValue(Boolean.valueOf(this.checked));
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CheckedTextView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */