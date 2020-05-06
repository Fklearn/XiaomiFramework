package android.widget;

import android.content.Context;
import android.util.AttributeSet;

public class CheckBox
  extends CompoundButton
{
  public CheckBox(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CheckBox(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842860);
  }
  
  public CheckBox(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public CheckBox(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return CheckBox.class.getName();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/CheckBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */