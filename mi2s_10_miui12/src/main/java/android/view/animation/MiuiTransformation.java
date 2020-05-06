package android.view.animation;

public class MiuiTransformation
  extends Transformation
{
  protected float mRadius;
  
  public void clear()
  {
    super.clear();
    this.mRadius = 0.0F;
  }
  
  public void compose(Transformation paramTransformation)
  {
    super.compose(paramTransformation);
    if ((paramTransformation instanceof MiuiTransformation)) {
      this.mRadius *= ((MiuiTransformation)paramTransformation).getRadius();
    }
  }
  
  public float getRadius()
  {
    return this.mRadius;
  }
  
  public void set(Transformation paramTransformation)
  {
    super.set(paramTransformation);
    if ((paramTransformation instanceof MiuiTransformation)) {
      this.mRadius = ((MiuiTransformation)paramTransformation).getRadius();
    }
  }
  
  public void setRadius(float paramFloat)
  {
    this.mRadius = paramFloat;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/MiuiTransformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */