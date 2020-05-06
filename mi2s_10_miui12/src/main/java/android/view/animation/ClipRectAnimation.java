package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import com.android.internal.R.styleable;

public class ClipRectAnimation
  extends Animation
{
  private int mFromBottomType = 0;
  private float mFromBottomValue;
  private int mFromLeftType = 0;
  private float mFromLeftValue;
  protected final Rect mFromRect = new Rect();
  private int mFromRightType = 0;
  private float mFromRightValue;
  private int mFromTopType = 0;
  private float mFromTopValue;
  private int mToBottomType = 0;
  private float mToBottomValue;
  private int mToLeftType = 0;
  private float mToLeftValue;
  protected final Rect mToRect = new Rect();
  private int mToRightType = 0;
  private float mToRightValue;
  private int mToTopType = 0;
  private float mToTopValue;
  
  public ClipRectAnimation(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    this(new Rect(paramInt1, paramInt2, paramInt3, paramInt4), new Rect(paramInt5, paramInt6, paramInt7, paramInt8));
  }
  
  public ClipRectAnimation(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ClipRectAnimation);
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(1));
    this.mFromLeftType = paramAttributeSet.type;
    this.mFromLeftValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(3));
    this.mFromTopType = paramAttributeSet.type;
    this.mFromTopValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(2));
    this.mFromRightType = paramAttributeSet.type;
    this.mFromRightValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(0));
    this.mFromBottomType = paramAttributeSet.type;
    this.mFromBottomValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(5));
    this.mToLeftType = paramAttributeSet.type;
    this.mToLeftValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(7));
    this.mToTopType = paramAttributeSet.type;
    this.mToTopValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(6));
    this.mToRightType = paramAttributeSet.type;
    this.mToRightValue = paramAttributeSet.value;
    paramAttributeSet = Animation.Description.parseValue(paramContext.peekValue(4));
    this.mToBottomType = paramAttributeSet.type;
    this.mToBottomValue = paramAttributeSet.value;
    paramContext.recycle();
  }
  
  public ClipRectAnimation(Rect paramRect1, Rect paramRect2)
  {
    if ((paramRect1 != null) && (paramRect2 != null))
    {
      this.mFromLeftValue = paramRect1.left;
      this.mFromTopValue = paramRect1.top;
      this.mFromRightValue = paramRect1.right;
      this.mFromBottomValue = paramRect1.bottom;
      this.mToLeftValue = paramRect2.left;
      this.mToTopValue = paramRect2.top;
      this.mToRightValue = paramRect2.right;
      this.mToBottomValue = paramRect2.bottom;
      return;
    }
    throw new RuntimeException("Expected non-null animation clip rects");
  }
  
  protected void applyTransformation(float paramFloat, Transformation paramTransformation)
  {
    paramTransformation.setClipRect(this.mFromRect.left + (int)((this.mToRect.left - this.mFromRect.left) * paramFloat), this.mFromRect.top + (int)((this.mToRect.top - this.mFromRect.top) * paramFloat), this.mFromRect.right + (int)((this.mToRect.right - this.mFromRect.right) * paramFloat), this.mFromRect.bottom + (int)((this.mToRect.bottom - this.mFromRect.bottom) * paramFloat));
  }
  
  public void initialize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.initialize(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mFromRect.set((int)resolveSize(this.mFromLeftType, this.mFromLeftValue, paramInt1, paramInt3), (int)resolveSize(this.mFromTopType, this.mFromTopValue, paramInt2, paramInt4), (int)resolveSize(this.mFromRightType, this.mFromRightValue, paramInt1, paramInt3), (int)resolveSize(this.mFromBottomType, this.mFromBottomValue, paramInt2, paramInt4));
    this.mToRect.set((int)resolveSize(this.mToLeftType, this.mToLeftValue, paramInt1, paramInt3), (int)resolveSize(this.mToTopType, this.mToTopValue, paramInt2, paramInt4), (int)resolveSize(this.mToRightType, this.mToRightValue, paramInt1, paramInt3), (int)resolveSize(this.mToBottomType, this.mToBottomValue, paramInt2, paramInt4));
  }
  
  public boolean willChangeTransformationMatrix()
  {
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/ClipRectAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */