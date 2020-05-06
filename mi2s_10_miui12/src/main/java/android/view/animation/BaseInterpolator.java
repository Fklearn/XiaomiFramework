package android.view.animation;

public abstract class BaseInterpolator
  implements Interpolator
{
  private int mChangingConfiguration;
  
  public int getChangingConfiguration()
  {
    return this.mChangingConfiguration;
  }
  
  void setChangingConfiguration(int paramInt)
  {
    this.mChangingConfiguration = paramInt;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/BaseInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */