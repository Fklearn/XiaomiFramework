package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class ToggleButton
  extends CompoundButton
{
  private static final int NO_ALPHA = 255;
  private float mDisabledAlpha;
  private Drawable mIndicatorDrawable;
  private CharSequence mTextOff;
  private CharSequence mTextOn;
  
  public ToggleButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ToggleButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842827);
  }
  
  public ToggleButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ToggleButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ToggleButton, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ToggleButton, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mTextOn = localTypedArray.getText(1);
    this.mTextOff = localTypedArray.getText(2);
    this.mDisabledAlpha = localTypedArray.getFloat(0, 0.5F);
    syncTextState();
    localTypedArray.recycle();
  }
  
  private void syncTextState()
  {
    boolean bool = isChecked();
    CharSequence localCharSequence;
    if (bool)
    {
      localCharSequence = this.mTextOn;
      if (localCharSequence != null)
      {
        setText(localCharSequence);
        return;
      }
    }
    if (!bool)
    {
      localCharSequence = this.mTextOff;
      if (localCharSequence != null) {
        setText(localCharSequence);
      }
    }
  }
  
  private void updateReferenceToIndicatorDrawable(Drawable paramDrawable)
  {
    if ((paramDrawable instanceof LayerDrawable)) {
      this.mIndicatorDrawable = ((LayerDrawable)paramDrawable).findDrawableByLayerId(16908311);
    } else {
      this.mIndicatorDrawable = null;
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mIndicatorDrawable;
    if (localDrawable != null)
    {
      int i;
      if (isEnabled()) {
        i = 255;
      } else {
        i = (int)(this.mDisabledAlpha * 255.0F);
      }
      localDrawable.setAlpha(i);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return ToggleButton.class.getName();
  }
  
  public float getDisabledAlpha()
  {
    return this.mDisabledAlpha;
  }
  
  public CharSequence getTextOff()
  {
    return this.mTextOff;
  }
  
  public CharSequence getTextOn()
  {
    return this.mTextOn;
  }
  
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    updateReferenceToIndicatorDrawable(getBackground());
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    super.setBackgroundDrawable(paramDrawable);
    updateReferenceToIndicatorDrawable(paramDrawable);
  }
  
  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    syncTextState();
  }
  
  public void setTextOff(CharSequence paramCharSequence)
  {
    this.mTextOff = paramCharSequence;
  }
  
  public void setTextOn(CharSequence paramCharSequence)
  {
    this.mTextOn = paramCharSequence;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ToggleButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */