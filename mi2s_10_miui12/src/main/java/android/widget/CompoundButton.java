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
import android.util.Log;
import android.view.View.BaseSavedState;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import com.android.internal.R.styleable;

public abstract class CompoundButton
  extends Button
  implements Checkable
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private static final String LOG_TAG = CompoundButton.class.getSimpleName();
  @UnsupportedAppUsage
  private boolean mBroadcasting;
  private BlendMode mButtonBlendMode = null;
  @UnsupportedAppUsage
  private Drawable mButtonDrawable;
  private ColorStateList mButtonTintList = null;
  private boolean mChecked;
  private boolean mCheckedFromResource = false;
  private boolean mHasButtonBlendMode = false;
  private boolean mHasButtonTint = false;
  @UnsupportedAppUsage
  private OnCheckedChangeListener mOnCheckedChangeListener;
  private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
  
  public CompoundButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CompoundButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public CompoundButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public CompoundButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CompoundButton, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.CompoundButton, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    paramContext = localTypedArray.getDrawable(1);
    if (paramContext != null) {
      setButtonDrawable(paramContext);
    }
    if (localTypedArray.hasValue(3))
    {
      this.mButtonBlendMode = Drawable.parseBlendMode(localTypedArray.getInt(3, -1), this.mButtonBlendMode);
      this.mHasButtonBlendMode = true;
    }
    if (localTypedArray.hasValue(2))
    {
      this.mButtonTintList = localTypedArray.getColorStateList(2);
      this.mHasButtonTint = true;
    }
    setChecked(localTypedArray.getBoolean(0, false));
    this.mCheckedFromResource = true;
    localTypedArray.recycle();
    applyButtonTint();
  }
  
  private void applyButtonTint()
  {
    if ((this.mButtonDrawable != null) && ((this.mHasButtonTint) || (this.mHasButtonBlendMode)))
    {
      this.mButtonDrawable = this.mButtonDrawable.mutate();
      if (this.mHasButtonTint) {
        this.mButtonDrawable.setTintList(this.mButtonTintList);
      }
      if (this.mHasButtonBlendMode) {
        this.mButtonDrawable.setTintBlendMode(this.mButtonBlendMode);
      }
      if (this.mButtonDrawable.isStateful()) {
        this.mButtonDrawable.setState(getDrawableState());
      }
    }
  }
  
  public void autofill(AutofillValue paramAutofillValue)
  {
    if (!isEnabled()) {
      return;
    }
    if (!paramAutofillValue.isToggle())
    {
      String str = LOG_TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramAutofillValue);
      localStringBuilder.append(" could not be autofilled into ");
      localStringBuilder.append(this);
      Log.w(str, localStringBuilder.toString());
      return;
    }
    setChecked(paramAutofillValue.getToggleValue());
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    Drawable localDrawable = this.mButtonDrawable;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mButtonDrawable;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    paramViewHierarchyEncoder.addProperty("checked", isChecked());
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return CompoundButton.class.getName();
  }
  
  public int getAutofillType()
  {
    int i;
    if (isEnabled()) {
      i = 2;
    } else {
      i = 0;
    }
    return i;
  }
  
  public AutofillValue getAutofillValue()
  {
    AutofillValue localAutofillValue;
    if (isEnabled()) {
      localAutofillValue = AutofillValue.forToggle(isChecked());
    } else {
      localAutofillValue = null;
    }
    return localAutofillValue;
  }
  
  public Drawable getButtonDrawable()
  {
    return this.mButtonDrawable;
  }
  
  public BlendMode getButtonTintBlendMode()
  {
    return this.mButtonBlendMode;
  }
  
  public ColorStateList getButtonTintList()
  {
    return this.mButtonTintList;
  }
  
  public PorterDuff.Mode getButtonTintMode()
  {
    Object localObject = this.mButtonBlendMode;
    if (localObject != null) {
      localObject = BlendMode.blendModeToPorterDuffMode((BlendMode)localObject);
    } else {
      localObject = null;
    }
    return (PorterDuff.Mode)localObject;
  }
  
  public int getCompoundPaddingLeft()
  {
    int i = super.getCompoundPaddingLeft();
    int j = i;
    if (!isLayoutRtl())
    {
      Drawable localDrawable = this.mButtonDrawable;
      j = i;
      if (localDrawable != null) {
        j = i + localDrawable.getIntrinsicWidth();
      }
    }
    return j;
  }
  
  public int getCompoundPaddingRight()
  {
    int i = super.getCompoundPaddingRight();
    int j = i;
    if (isLayoutRtl())
    {
      Drawable localDrawable = this.mButtonDrawable;
      j = i;
      if (localDrawable != null) {
        j = i + localDrawable.getIntrinsicWidth();
      }
    }
    return j;
  }
  
  public int getHorizontalOffsetForDrawables()
  {
    Drawable localDrawable = this.mButtonDrawable;
    int i;
    if (localDrawable != null) {
      i = localDrawable.getIntrinsicWidth();
    } else {
      i = 0;
    }
    return i;
  }
  
  @ViewDebug.ExportedProperty
  public boolean isChecked()
  {
    return this.mChecked;
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    Drawable localDrawable = this.mButtonDrawable;
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
    Drawable localDrawable1 = this.mButtonDrawable;
    int i;
    int j;
    if (localDrawable1 != null)
    {
      i = getGravity() & 0x70;
      j = localDrawable1.getIntrinsicHeight();
      int k = localDrawable1.getIntrinsicWidth();
      if (i != 16)
      {
        if (i != 80) {
          i = 0;
        } else {
          i = getHeight() - j;
        }
      }
      else {
        i = (getHeight() - j) / 2;
      }
      int m = i + j;
      if (isLayoutRtl()) {
        j = getWidth() - k;
      } else {
        j = 0;
      }
      if (isLayoutRtl()) {
        k = getWidth();
      }
      localDrawable1.setBounds(j, i, k, m);
      Drawable localDrawable2 = getBackground();
      if (localDrawable2 != null) {
        localDrawable2.setHotspotBounds(j, i, k, m);
      }
    }
    super.onDraw(paramCanvas);
    if (localDrawable1 != null)
    {
      i = this.mScrollX;
      j = this.mScrollY;
      if ((i == 0) && (j == 0))
      {
        localDrawable1.draw(paramCanvas);
      }
      else
      {
        paramCanvas.translate(i, j);
        localDrawable1.draw(paramCanvas);
        paramCanvas.translate(-i, -j);
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
  
  protected void onProvideStructure(ViewStructure paramViewStructure, int paramInt1, int paramInt2)
  {
    super.onProvideStructure(paramViewStructure, paramInt1, paramInt2);
    if (paramInt1 == 1) {
      paramViewStructure.setDataIsSensitive(true ^ this.mCheckedFromResource);
    }
  }
  
  public void onResolveDrawables(int paramInt)
  {
    super.onResolveDrawables(paramInt);
    Drawable localDrawable = this.mButtonDrawable;
    if (localDrawable != null) {
      localDrawable.setLayoutDirection(paramInt);
    }
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    paramParcelable = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramParcelable.getSuperState());
    setChecked(paramParcelable.checked);
    requestLayout();
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    localSavedState.checked = isChecked();
    return localSavedState;
  }
  
  public boolean performClick()
  {
    toggle();
    boolean bool = super.performClick();
    if (!bool) {
      playSoundEffect(0);
    }
    return bool;
  }
  
  public void setButtonDrawable(int paramInt)
  {
    Drawable localDrawable;
    if (paramInt != 0) {
      localDrawable = getContext().getDrawable(paramInt);
    } else {
      localDrawable = null;
    }
    setButtonDrawable(localDrawable);
  }
  
  public void setButtonDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mButtonDrawable;
    if (localDrawable != paramDrawable)
    {
      if (localDrawable != null)
      {
        localDrawable.setCallback(null);
        unscheduleDrawable(this.mButtonDrawable);
      }
      this.mButtonDrawable = paramDrawable;
      if (paramDrawable != null)
      {
        paramDrawable.setCallback(this);
        paramDrawable.setLayoutDirection(getLayoutDirection());
        if (paramDrawable.isStateful()) {
          paramDrawable.setState(getDrawableState());
        }
        boolean bool;
        if (getVisibility() == 0) {
          bool = true;
        } else {
          bool = false;
        }
        paramDrawable.setVisible(bool, false);
        setMinHeight(paramDrawable.getIntrinsicHeight());
        applyButtonTint();
      }
    }
  }
  
  public void setButtonTintBlendMode(BlendMode paramBlendMode)
  {
    this.mButtonBlendMode = paramBlendMode;
    this.mHasButtonBlendMode = true;
    applyButtonTint();
  }
  
  public void setButtonTintList(ColorStateList paramColorStateList)
  {
    this.mButtonTintList = paramColorStateList;
    this.mHasButtonTint = true;
    applyButtonTint();
  }
  
  public void setButtonTintMode(PorterDuff.Mode paramMode)
  {
    if (paramMode != null) {
      paramMode = BlendMode.fromValue(paramMode.nativeInt);
    } else {
      paramMode = null;
    }
    setButtonTintBlendMode(paramMode);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    if (this.mChecked != paramBoolean)
    {
      this.mCheckedFromResource = false;
      this.mChecked = paramBoolean;
      refreshDrawableState();
      notifyViewAccessibilityStateChangedIfNeeded(0);
      if (this.mBroadcasting) {
        return;
      }
      this.mBroadcasting = true;
      Object localObject = this.mOnCheckedChangeListener;
      if (localObject != null) {
        ((OnCheckedChangeListener)localObject).onCheckedChanged(this, this.mChecked);
      }
      localObject = this.mOnCheckedChangeWidgetListener;
      if (localObject != null) {
        ((OnCheckedChangeListener)localObject).onCheckedChanged(this, this.mChecked);
      }
      localObject = (AutofillManager)this.mContext.getSystemService(AutofillManager.class);
      if (localObject != null) {
        ((AutofillManager)localObject).notifyValueChanged(this);
      }
      this.mBroadcasting = false;
    }
  }
  
  public void setOnCheckedChangeListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mOnCheckedChangeListener = paramOnCheckedChangeListener;
  }
  
  void setOnCheckedChangeWidgetListener(OnCheckedChangeListener paramOnCheckedChangeListener)
  {
    this.mOnCheckedChangeWidgetListener = paramOnCheckedChangeListener;
  }
  
  public void toggle()
  {
    setChecked(this.mChecked ^ true);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    boolean bool;
    if ((!super.verifyDrawable(paramDrawable)) && (paramDrawable != this.mButtonDrawable)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static abstract interface OnCheckedChangeListener
  {
    public abstract void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean);
  }
  
  static class SavedState
    extends View.BaseSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public CompoundButton.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new CompoundButton.SavedState(paramAnonymousParcel, null);
      }
      
      public CompoundButton.SavedState[] newArray(int paramAnonymousInt)
      {
        return new CompoundButton.SavedState[paramAnonymousInt];
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
      localStringBuilder.append("CompoundButton.SavedState{");
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


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CompoundButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */