package android.view.animation;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Matrix;
import android.graphics.Rect;
import java.io.PrintWriter;

public class Transformation
{
  public static final int TYPE_ALPHA = 1;
  public static final int TYPE_BOTH = 3;
  public static final int TYPE_IDENTITY = 0;
  public static final int TYPE_MATRIX = 2;
  protected float mAlpha;
  private Rect mClipRect = new Rect();
  private boolean mHasClipRect;
  protected Matrix mMatrix;
  protected int mTransformationType;
  
  public Transformation()
  {
    clear();
  }
  
  public void clear()
  {
    Matrix localMatrix = this.mMatrix;
    if (localMatrix == null) {
      this.mMatrix = new Matrix();
    } else {
      localMatrix.reset();
    }
    this.mClipRect.setEmpty();
    this.mHasClipRect = false;
    this.mAlpha = 1.0F;
    this.mTransformationType = 3;
  }
  
  public void compose(Transformation paramTransformation)
  {
    this.mAlpha *= paramTransformation.getAlpha();
    this.mMatrix.preConcat(paramTransformation.getMatrix());
    if (paramTransformation.mHasClipRect)
    {
      paramTransformation = paramTransformation.getClipRect();
      if (this.mHasClipRect) {
        setClipRect(this.mClipRect.left + paramTransformation.left, this.mClipRect.top + paramTransformation.top, this.mClipRect.right + paramTransformation.right, this.mClipRect.bottom + paramTransformation.bottom);
      } else {
        setClipRect(paramTransformation);
      }
    }
  }
  
  public float getAlpha()
  {
    return this.mAlpha;
  }
  
  public Rect getClipRect()
  {
    return this.mClipRect;
  }
  
  public Matrix getMatrix()
  {
    return this.mMatrix;
  }
  
  public int getTransformationType()
  {
    return this.mTransformationType;
  }
  
  public boolean hasClipRect()
  {
    return this.mHasClipRect;
  }
  
  public void postCompose(Transformation paramTransformation)
  {
    this.mAlpha *= paramTransformation.getAlpha();
    this.mMatrix.postConcat(paramTransformation.getMatrix());
    if (paramTransformation.mHasClipRect)
    {
      paramTransformation = paramTransformation.getClipRect();
      if (this.mHasClipRect) {
        setClipRect(this.mClipRect.left + paramTransformation.left, this.mClipRect.top + paramTransformation.top, this.mClipRect.right + paramTransformation.right, this.mClipRect.bottom + paramTransformation.bottom);
      } else {
        setClipRect(paramTransformation);
      }
    }
  }
  
  @UnsupportedAppUsage
  public void printShortString(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.print("{alpha=");
    paramPrintWriter.print(this.mAlpha);
    paramPrintWriter.print(" matrix=");
    this.mMatrix.printShortString(paramPrintWriter);
    paramPrintWriter.print('}');
  }
  
  public void set(Transformation paramTransformation)
  {
    this.mAlpha = paramTransformation.getAlpha();
    this.mMatrix.set(paramTransformation.getMatrix());
    if (paramTransformation.mHasClipRect)
    {
      setClipRect(paramTransformation.getClipRect());
    }
    else
    {
      this.mHasClipRect = false;
      this.mClipRect.setEmpty();
    }
    this.mTransformationType = paramTransformation.getTransformationType();
  }
  
  public void setAlpha(float paramFloat)
  {
    this.mAlpha = paramFloat;
  }
  
  public void setClipRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mClipRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mHasClipRect = true;
  }
  
  public void setClipRect(Rect paramRect)
  {
    setClipRect(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setTransformationType(int paramInt)
  {
    this.mTransformationType = paramInt;
  }
  
  public String toShortString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    toShortString(localStringBuilder);
    return localStringBuilder.toString();
  }
  
  public void toShortString(StringBuilder paramStringBuilder)
  {
    paramStringBuilder.append("{alpha=");
    paramStringBuilder.append(this.mAlpha);
    paramStringBuilder.append(" matrix=");
    this.mMatrix.toShortString(paramStringBuilder);
    paramStringBuilder.append('}');
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(64);
    localStringBuilder.append("Transformation");
    toShortString(localStringBuilder);
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/Transformation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */