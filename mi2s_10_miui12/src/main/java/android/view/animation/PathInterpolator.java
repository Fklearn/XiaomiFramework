package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.PathParser;
import android.view.InflateException;
import com.android.internal.R.styleable;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

@HasNativeInterpolator
public class PathInterpolator
  extends BaseInterpolator
  implements NativeInterpolatorFactory
{
  private static final float PRECISION = 0.002F;
  private float[] mX;
  private float[] mY;
  
  public PathInterpolator(float paramFloat1, float paramFloat2)
  {
    initQuad(paramFloat1, paramFloat2);
  }
  
  public PathInterpolator(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    initCubic(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
  }
  
  public PathInterpolator(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext.getResources(), paramContext.getTheme(), paramAttributeSet);
  }
  
  public PathInterpolator(Resources paramResources, Resources.Theme paramTheme, AttributeSet paramAttributeSet)
  {
    if (paramTheme != null) {
      paramResources = paramTheme.obtainStyledAttributes(paramAttributeSet, R.styleable.PathInterpolator, 0, 0);
    } else {
      paramResources = paramResources.obtainAttributes(paramAttributeSet, R.styleable.PathInterpolator);
    }
    parseInterpolatorFromTypeArray(paramResources);
    setChangingConfiguration(paramResources.getChangingConfigurations());
    paramResources.recycle();
  }
  
  public PathInterpolator(Path paramPath)
  {
    initPath(paramPath);
  }
  
  private void initCubic(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.cubicTo(paramFloat1, paramFloat2, paramFloat3, paramFloat4, 1.0F, 1.0F);
    initPath(localPath);
  }
  
  private void initPath(Path paramPath)
  {
    paramPath = paramPath.approximate(0.002F);
    int i = paramPath.length / 3;
    if ((paramPath[1] == 0.0F) && (paramPath[2] == 0.0F) && (paramPath[(paramPath.length - 2)] == 1.0F) && (paramPath[(paramPath.length - 1)] == 1.0F))
    {
      this.mX = new float[i];
      this.mY = new float[i];
      float f1 = 0.0F;
      float f2 = 0.0F;
      int j = 0;
      int k = 0;
      while (k < i)
      {
        int m = j + 1;
        float f3 = paramPath[j];
        j = m + 1;
        float f4 = paramPath[m];
        float f5 = paramPath[j];
        if ((f3 == f2) && (f4 != f1)) {
          throw new IllegalArgumentException("The Path cannot have discontinuity in the X axis.");
        }
        if (f4 >= f1)
        {
          this.mX[k] = f4;
          this.mY[k] = f5;
          f1 = f4;
          f2 = f3;
          k++;
          j++;
        }
        else
        {
          throw new IllegalArgumentException("The Path cannot loop back on itself.");
        }
      }
      return;
    }
    throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1)");
  }
  
  private void initQuad(float paramFloat1, float paramFloat2)
  {
    Path localPath = new Path();
    localPath.moveTo(0.0F, 0.0F);
    localPath.quadTo(paramFloat1, paramFloat2, 1.0F, 1.0F);
    initPath(localPath);
  }
  
  private void parseInterpolatorFromTypeArray(TypedArray paramTypedArray)
  {
    if (paramTypedArray.hasValue(4))
    {
      paramTypedArray = paramTypedArray.getString(4);
      Object localObject = PathParser.createPathFromPathData(paramTypedArray);
      if (localObject != null)
      {
        initPath((Path)localObject);
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("The path is null, which is created from ");
        ((StringBuilder)localObject).append(paramTypedArray);
        throw new InflateException(((StringBuilder)localObject).toString());
      }
    }
    else
    {
      if (!paramTypedArray.hasValue(0)) {
        break label167;
      }
      if (!paramTypedArray.hasValue(1)) {
        break label157;
      }
      float f1 = paramTypedArray.getFloat(0, 0.0F);
      float f2 = paramTypedArray.getFloat(1, 0.0F);
      boolean bool = paramTypedArray.hasValue(2);
      if (bool != paramTypedArray.hasValue(3)) {
        break label147;
      }
      if (!bool) {
        initQuad(f1, f2);
      } else {
        initCubic(f1, f2, paramTypedArray.getFloat(2, 0.0F), paramTypedArray.getFloat(3, 0.0F));
      }
    }
    return;
    label147:
    throw new InflateException("pathInterpolator requires both controlX2 and controlY2 for cubic Beziers.");
    label157:
    throw new InflateException("pathInterpolator requires the controlY1 attribute");
    label167:
    throw new InflateException("pathInterpolator requires the controlX1 attribute");
  }
  
  public long createNativeInterpolator()
  {
    return NativeInterpolatorFactoryHelper.createPathInterpolator(this.mX, this.mY);
  }
  
  public float getInterpolation(float paramFloat)
  {
    if (paramFloat <= 0.0F) {
      return 0.0F;
    }
    if (paramFloat >= 1.0F) {
      return 1.0F;
    }
    int i = 0;
    int j = this.mX.length - 1;
    while (j - i > 1)
    {
      int k = (i + j) / 2;
      if (paramFloat < this.mX[k]) {
        j = k;
      } else {
        i = k;
      }
    }
    float[] arrayOfFloat = this.mX;
    float f = arrayOfFloat[j] - arrayOfFloat[i];
    if (f == 0.0F) {
      return this.mY[i];
    }
    paramFloat = (paramFloat - arrayOfFloat[i]) / f;
    arrayOfFloat = this.mY;
    f = arrayOfFloat[i];
    return (arrayOfFloat[j] - f) * paramFloat + f;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/PathInterpolator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */