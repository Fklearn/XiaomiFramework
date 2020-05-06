package android.view;

import android.graphics.Insets;

public abstract interface WindowInsetsAnimationController
{
  public abstract void changeInsets(Insets paramInsets);
  
  public abstract void finish(int paramInt);
  
  public abstract Insets getCurrentInsets();
  
  public abstract Insets getHiddenStateInsets();
  
  public abstract Insets getShownStateInsets();
  
  public abstract int getTypes();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowInsetsAnimationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */