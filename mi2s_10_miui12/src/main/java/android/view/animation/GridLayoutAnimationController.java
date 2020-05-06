package android.view.animation;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.android.internal.R.styleable;
import java.util.Random;

public class GridLayoutAnimationController
  extends LayoutAnimationController
{
  public static final int DIRECTION_BOTTOM_TO_TOP = 2;
  public static final int DIRECTION_HORIZONTAL_MASK = 1;
  public static final int DIRECTION_LEFT_TO_RIGHT = 0;
  public static final int DIRECTION_RIGHT_TO_LEFT = 1;
  public static final int DIRECTION_TOP_TO_BOTTOM = 0;
  public static final int DIRECTION_VERTICAL_MASK = 2;
  public static final int PRIORITY_COLUMN = 1;
  public static final int PRIORITY_NONE = 0;
  public static final int PRIORITY_ROW = 2;
  private float mColumnDelay;
  private int mDirection;
  private int mDirectionPriority;
  private float mRowDelay;
  
  public GridLayoutAnimationController(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.GridLayoutAnimation);
    this.mColumnDelay = Animation.Description.parseValue(paramContext.peekValue(0)).value;
    this.mRowDelay = Animation.Description.parseValue(paramContext.peekValue(1)).value;
    this.mDirection = paramContext.getInt(2, 0);
    this.mDirectionPriority = paramContext.getInt(3, 0);
    paramContext.recycle();
  }
  
  public GridLayoutAnimationController(Animation paramAnimation)
  {
    this(paramAnimation, 0.5F, 0.5F);
  }
  
  public GridLayoutAnimationController(Animation paramAnimation, float paramFloat1, float paramFloat2)
  {
    super(paramAnimation);
    this.mColumnDelay = paramFloat1;
    this.mRowDelay = paramFloat2;
  }
  
  private int getTransformedColumnIndex(AnimationParameters paramAnimationParameters)
  {
    int i = getOrder();
    if (i != 1)
    {
      if (i != 2)
      {
        i = paramAnimationParameters.column;
      }
      else
      {
        if (this.mRandomizer == null) {
          this.mRandomizer = new Random();
        }
        i = (int)(paramAnimationParameters.columnsCount * this.mRandomizer.nextFloat());
      }
    }
    else {
      i = paramAnimationParameters.columnsCount - 1 - paramAnimationParameters.column;
    }
    int j = i;
    if ((this.mDirection & 0x1) == 1) {
      j = paramAnimationParameters.columnsCount - 1 - i;
    }
    return j;
  }
  
  private int getTransformedRowIndex(AnimationParameters paramAnimationParameters)
  {
    int i = getOrder();
    if (i != 1)
    {
      if (i != 2)
      {
        i = paramAnimationParameters.row;
      }
      else
      {
        if (this.mRandomizer == null) {
          this.mRandomizer = new Random();
        }
        i = (int)(paramAnimationParameters.rowsCount * this.mRandomizer.nextFloat());
      }
    }
    else {
      i = paramAnimationParameters.rowsCount - 1 - paramAnimationParameters.row;
    }
    int j = i;
    if ((this.mDirection & 0x2) == 2) {
      j = paramAnimationParameters.rowsCount - 1 - i;
    }
    return j;
  }
  
  public float getColumnDelay()
  {
    return this.mColumnDelay;
  }
  
  protected long getDelayForView(View paramView)
  {
    paramView = (AnimationParameters)paramView.getLayoutParams().layoutAnimationParameters;
    if (paramView == null) {
      return 0L;
    }
    int i = getTransformedColumnIndex(paramView);
    int j = getTransformedRowIndex(paramView);
    int k = paramView.rowsCount;
    int m = paramView.columnsCount;
    long l = this.mAnimation.getDuration();
    float f1 = this.mColumnDelay * (float)l;
    float f2 = this.mRowDelay * (float)l;
    if (this.mInterpolator == null) {
      this.mInterpolator = new LinearInterpolator();
    }
    int n = this.mDirectionPriority;
    if (n != 1)
    {
      if (n != 2)
      {
        l = (i * f1 + j * f2);
        f1 = m * f1 + k * f2;
      }
      else
      {
        l = (i * f1 + j * m * f1);
        f1 = m * f1 + k * m * f1;
      }
    }
    else
    {
      l = (j * f2 + i * k * f2);
      f1 = k * f2 + m * k * f2;
    }
    f2 = (float)l / f1;
    return (this.mInterpolator.getInterpolation(f2) * f1);
  }
  
  public int getDirection()
  {
    return this.mDirection;
  }
  
  public int getDirectionPriority()
  {
    return this.mDirectionPriority;
  }
  
  public float getRowDelay()
  {
    return this.mRowDelay;
  }
  
  public void setColumnDelay(float paramFloat)
  {
    this.mColumnDelay = paramFloat;
  }
  
  public void setDirection(int paramInt)
  {
    this.mDirection = paramInt;
  }
  
  public void setDirectionPriority(int paramInt)
  {
    this.mDirectionPriority = paramInt;
  }
  
  public void setRowDelay(float paramFloat)
  {
    this.mRowDelay = paramFloat;
  }
  
  public boolean willOverlap()
  {
    boolean bool;
    if ((this.mColumnDelay >= 1.0F) && (this.mRowDelay >= 1.0F)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  public static class AnimationParameters
    extends LayoutAnimationController.AnimationParameters
  {
    public int column;
    public int columnsCount;
    public int row;
    public int rowsCount;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/GridLayoutAnimationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */