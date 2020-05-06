package android.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

public class FloatPanelView
  extends FrameLayout
{
  public static final int DOWN_ARROW = -1;
  public static final int NO_ARROW = 0;
  public static final int UP_ARROW = 1;
  private ViewGroup mContent;
  private int mDirection;
  private ImageView mDownArrow;
  private int mLeftRoundCorner;
  private int mOffset;
  private int mRightRoundCorner;
  private ImageView mUpArrow;
  
  public FloatPanelView(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public FloatPanelView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    init();
  }
  
  public FloatPanelView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init();
  }
  
  private ImageView getImageView(int paramInt)
  {
    ImageView localImageView = new ImageView(getContext());
    localImageView.setImageResource(paramInt);
    return localImageView;
  }
  
  private void init()
  {
    this.mUpArrow = getImageView(285671899);
    this.mDownArrow = getImageView(285671898);
  }
  
  public ViewGroup getContent()
  {
    return this.mContent;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    Object localObject = this.mUpArrow;
    if ((localObject != null) && (this.mDownArrow != null) && (this.mContent != null))
    {
      ((ImageView)localObject).setVisibility(8);
      this.mDownArrow.setVisibility(8);
      localObject = null;
      paramInt4 = this.mDirection;
      if (paramInt4 == 1) {
        localObject = this.mUpArrow;
      } else if (paramInt4 == -1) {
        localObject = this.mDownArrow;
      }
      if (localObject != null)
      {
        int i = ((ImageView)localObject).getDrawable().getIntrinsicWidth();
        int j = ((ImageView)localObject).getDrawable().getIntrinsicHeight();
        if (this.mDirection == 1) {
          paramInt4 = paramInt2;
        } else {
          paramInt4 = this.mContent.getMeasuredHeight() + paramInt2;
        }
        ((ImageView)localObject).setVisibility(0);
        int k = (paramInt3 - paramInt1 - i) / 2 + paramInt1 + this.mOffset;
        if (k < this.mContent.getLeft() + this.mLeftRoundCorner)
        {
          paramInt1 = this.mContent.getLeft() + this.mLeftRoundCorner;
        }
        else
        {
          int m = this.mRightRoundCorner;
          paramInt1 = k;
          if (k > paramInt3 - i - m) {
            paramInt1 = paramInt3 - i - m;
          }
        }
        ((ImageView)localObject).layout(paramInt1, paramInt4, paramInt1 + i, paramInt4 + j);
        if (paramInt4 == paramInt2) {
          paramInt1 = j;
        } else {
          paramInt1 = paramInt2;
        }
        localObject = this.mContent;
        ((ViewGroup)localObject).layout(((ViewGroup)localObject).getLeft(), paramInt1, this.mContent.getLeft() + this.mContent.getMeasuredWidth(), this.mContent.getMeasuredHeight() + paramInt1);
      }
      else
      {
        localObject = this.mContent;
        ((ViewGroup)localObject).layout(((ViewGroup)localObject).getLeft(), this.mContent.getTop(), this.mContent.getLeft() + this.mContent.getMeasuredWidth(), this.mContent.getTop() + this.mContent.getMeasuredHeight());
      }
      return;
    }
    Log.e("FloatPanelView", "couldn't find view");
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    ImageView localImageView = null;
    int i = this.mDirection;
    if (i == 1) {
      localImageView = this.mUpArrow;
    } else if (i == -1) {
      localImageView = this.mDownArrow;
    }
    if (localImageView == null) {
      i = 0;
    } else {
      i = localImageView.getDrawable().getIntrinsicHeight();
    }
    this.mContent.measure(paramInt1, paramInt2);
    setMeasuredDimension(this.mContent.getMeasuredWidth(), this.mContent.getMeasuredHeight() + i);
  }
  
  public void setArrow(int paramInt)
  {
    if (paramInt != this.mDirection)
    {
      this.mDirection = paramInt;
      requestLayout();
    }
  }
  
  public void setContent(ViewGroup paramViewGroup)
  {
    if (paramViewGroup != this.mContent)
    {
      removeAllViews();
      if (paramViewGroup != null)
      {
        addView(paramViewGroup);
        this.mContent = paramViewGroup;
        addView(this.mUpArrow);
        addView(this.mDownArrow);
      }
    }
  }
  
  public void setLeftCorner(int paramInt)
  {
    if (paramInt != this.mLeftRoundCorner)
    {
      this.mLeftRoundCorner = paramInt;
      requestLayout();
    }
  }
  
  public void setOffset(int paramInt)
  {
    if (this.mOffset != paramInt)
    {
      this.mOffset = paramInt;
      requestLayout();
    }
  }
  
  public void setRightCorner(int paramInt)
  {
    if (paramInt != this.mRightRoundCorner)
    {
      this.mRightRoundCorner = paramInt;
      requestLayout();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/FloatPanelView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */