package android.view.animation;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class SpringInterpolator
  extends BaseInterpolator
  implements TimeInterpolator
{
  private static final float DEFAULT_DAMPING = 0.95F;
  private static final float DEFAULT_RESPONSE = 0.6F;
  private float c;
  private float c1;
  private float c2;
  private float damping = 0.95F;
  private float initial = -1.0F;
  private float k;
  private float m = 1.0F;
  private float r;
  private float response = 0.6F;
  private float w;
  
  public SpringInterpolator()
  {
    double d = Math.pow(6.283185307179586D / this.response, 2.0D);
    float f1 = this.m;
    this.k = ((float)(d * f1));
    this.c = ((float)(this.damping * 12.566370614359172D * f1 / this.response));
    float f2 = this.k;
    float f3 = this.c;
    f1 = (float)Math.sqrt(f1 * 4.0F * f2 - f3 * f3);
    f2 = this.m;
    this.w = (f1 / (f2 * 2.0F));
    this.r = (-(this.c / 2.0F * f2));
    f1 = this.initial;
    this.c1 = f1;
    this.c2 = ((0.0F - this.r * f1) / this.w);
    this.damping = 0.95F;
    this.response = 0.6F;
    refreshParams();
  }
  
  public SpringInterpolator(float paramFloat1, float paramFloat2)
  {
    double d = Math.pow(6.283185307179586D / this.response, 2.0D);
    float f1 = this.m;
    this.k = ((float)(d * f1));
    this.c = ((float)(this.damping * 12.566370614359172D * f1 / this.response));
    float f2 = this.k;
    float f3 = this.c;
    f1 = (float)Math.sqrt(f1 * 4.0F * f2 - f3 * f3);
    f2 = this.m;
    this.w = (f1 / (f2 * 2.0F));
    this.r = (-(this.c / 2.0F * f2));
    f1 = this.initial;
    this.c1 = f1;
    this.c2 = ((0.0F - this.r * f1) / this.w);
    this.damping = paramFloat1;
    this.response = paramFloat2;
    refreshParams();
  }
  
  public SpringInterpolator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext.getResources(), paramContext.getTheme(), paramAttributeSet);
  }
  
  public SpringInterpolator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    double d = Math.pow(6.283185307179586D / this.response, 2.0D);
    float f1 = this.m;
    this.k = ((float)(d * f1));
    this.c = ((float)(this.damping * 12.566370614359172D * f1 / this.response));
    float f2 = this.k;
    float f3 = this.c;
    f2 = (float)Math.sqrt(f1 * 4.0F * f2 - f3 * f3);
    f1 = this.m;
    this.w = (f2 / (f1 * 2.0F));
    this.r = (-(this.c / 2.0F * f1));
    f1 = this.initial;
    this.c1 = f1;
    this.c2 = ((0.0F - this.r * f1) / this.w);
    if (paramTheme != null) {
      paramResources = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.SpringInterpolator, 0, 0);
    } else {
      paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.SpringInterpolator);
    }
    this.damping = paramResources.getFloat(0, 0.95F);
    this.response = paramResources.getFloat(1, 0.95F);
    paramResources.recycle();
    refreshParams();
  }
  
  private void refreshParams()
  {
    double d = Math.pow(6.283185307179586D / this.response, 2.0D);
    float f1 = this.m;
    this.k = ((float)(d * f1));
    this.c = ((float)(this.damping * 12.566370614359172D * f1 / this.response));
    float f2 = this.k;
    float f3 = this.c;
    f1 = (float)Math.sqrt(f1 * 4.0F * f2 - f3 * f3);
    f3 = this.m;
    this.w = (f1 / (f3 * 2.0F));
    this.r = (-(this.c / 2.0F * f3));
    this.c2 = ((0.0F - this.r * this.initial) / this.w);
  }
  
  public float getDamping()
  {
    return this.damping;
  }
  
  public float getInterpolation(float paramFloat)
  {
    return (float)(Math.pow(2.718281828459045D, this.r * paramFloat) * (this.c1 * Math.cos(this.w * paramFloat) + this.c2 * Math.sin(this.w * paramFloat)) + 1.0D);
  }
  
  public float getResponse()
  {
    return this.response;
  }
  
  public SpringInterpolator setDamping(float paramFloat)
  {
    this.damping = paramFloat;
    refreshParams();
    return this;
  }
  
  public SpringInterpolator setResponse(float paramFloat)
  {
    this.response = paramFloat;
    refreshParams();
    return this;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/SpringInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */