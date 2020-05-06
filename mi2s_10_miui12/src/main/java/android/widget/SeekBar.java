package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;

public class SeekBar
  extends AbsSeekBar
{
  @UnsupportedAppUsage
  private OnSeekBarChangeListener mOnSeekBarChangeListener;
  
  public SeekBar(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SeekBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842875);
  }
  
  public SeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public SeekBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return SeekBar.class.getName();
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (canUserSetProgress()) {
      paramAccessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
    }
  }
  
  @UnsupportedAppUsage
  void onProgressRefresh(float paramFloat, boolean paramBoolean, int paramInt)
  {
    super.onProgressRefresh(paramFloat, paramBoolean, paramInt);
    OnSeekBarChangeListener localOnSeekBarChangeListener = this.mOnSeekBarChangeListener;
    if (localOnSeekBarChangeListener != null) {
      localOnSeekBarChangeListener.onProgressChanged(this, paramInt, paramBoolean);
    }
  }
  
  void onStartTrackingTouch()
  {
    super.onStartTrackingTouch();
    OnSeekBarChangeListener localOnSeekBarChangeListener = this.mOnSeekBarChangeListener;
    if (localOnSeekBarChangeListener != null) {
      localOnSeekBarChangeListener.onStartTrackingTouch(this);
    }
  }
  
  void onStopTrackingTouch()
  {
    super.onStopTrackingTouch();
    OnSeekBarChangeListener localOnSeekBarChangeListener = this.mOnSeekBarChangeListener;
    if (localOnSeekBarChangeListener != null) {
      localOnSeekBarChangeListener.onStopTrackingTouch(this);
    }
  }
  
  public void setOnSeekBarChangeListener(OnSeekBarChangeListener paramOnSeekBarChangeListener)
  {
    this.mOnSeekBarChangeListener = paramOnSeekBarChangeListener;
  }
  
  public static abstract interface OnSeekBarChangeListener
  {
    public abstract void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean);
    
    public abstract void onStartTrackingTouch(SeekBar paramSeekBar);
    
    public abstract void onStopTrackingTouch(SeekBar paramSeekBar);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SeekBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */