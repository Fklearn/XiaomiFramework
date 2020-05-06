package android.view;

import android.graphics.Insets;

public abstract interface WindowInsetsAnimationListener
{
  public abstract void onFinished(InsetsAnimation paramInsetsAnimation);
  
  public abstract WindowInsets onProgress(WindowInsets paramWindowInsets);
  
  public abstract void onStarted(InsetsAnimation paramInsetsAnimation);
  
  public static class InsetsAnimation
  {
    private final Insets mLowerBound;
    private final int mTypeMask;
    private final Insets mUpperBound;
    
    InsetsAnimation(int paramInt, Insets paramInsets1, Insets paramInsets2)
    {
      this.mTypeMask = paramInt;
      this.mLowerBound = paramInsets1;
      this.mUpperBound = paramInsets2;
    }
    
    public Insets getLowerBound()
    {
      return this.mLowerBound;
    }
    
    public int getTypeMask()
    {
      return this.mTypeMask;
    }
    
    public Insets getUpperBound()
    {
      return this.mUpperBound;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowInsetsAnimationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */