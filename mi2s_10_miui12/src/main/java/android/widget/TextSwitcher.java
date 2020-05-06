package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class TextSwitcher
  extends ViewSwitcher
{
  public TextSwitcher(Context paramContext)
  {
    super(paramContext);
  }
  
  public TextSwitcher(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramView instanceof TextView))
    {
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
    }
    throw new IllegalArgumentException("TextSwitcher children must be instances of TextView");
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return TextSwitcher.class.getName();
  }
  
  public void setCurrentText(CharSequence paramCharSequence)
  {
    ((TextView)getCurrentView()).setText(paramCharSequence);
  }
  
  public void setText(CharSequence paramCharSequence)
  {
    ((TextView)getNextView()).setText(paramCharSequence);
    showNext();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TextSwitcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */