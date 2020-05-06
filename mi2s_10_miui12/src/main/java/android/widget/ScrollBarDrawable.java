package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Canvas;
import android.graphics.Canvas.EdgeType;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import com.android.internal.widget.ScrollBarUtils;

public class ScrollBarDrawable
  extends Drawable
  implements Drawable.Callback
{
  private int mAlpha = 255;
  private boolean mAlwaysDrawHorizontalTrack;
  private boolean mAlwaysDrawVerticalTrack;
  private boolean mBoundsChanged;
  private ColorFilter mColorFilter;
  private int mExtent;
  private boolean mHasSetAlpha;
  private boolean mHasSetColorFilter;
  private Drawable mHorizontalThumb;
  private Drawable mHorizontalTrack;
  private boolean mMutated;
  private int mOffset;
  private int mRange;
  private boolean mRangeChanged;
  private boolean mVertical;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123768422L)
  private Drawable mVerticalThumb;
  private Drawable mVerticalTrack;
  
  private void drawThumb(Canvas paramCanvas, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    if ((!this.mRangeChanged) && (!this.mBoundsChanged)) {
      i = 0;
    } else {
      i = 1;
    }
    Drawable localDrawable;
    if (paramBoolean)
    {
      if (this.mVerticalThumb != null)
      {
        localDrawable = this.mVerticalThumb;
        if (i != 0) {
          localDrawable.setBounds(paramRect.left, paramRect.top + paramInt1, paramRect.right, paramRect.top + paramInt1 + paramInt2);
        }
        localDrawable.draw(paramCanvas);
      }
    }
    else if (this.mHorizontalThumb != null)
    {
      localDrawable = this.mHorizontalThumb;
      if (i != 0) {
        localDrawable.setBounds(paramRect.left + paramInt1, paramRect.top, paramRect.left + paramInt1 + paramInt2, paramRect.bottom);
      }
      localDrawable.draw(paramCanvas);
    }
  }
  
  private void drawTrack(Canvas paramCanvas, Rect paramRect, boolean paramBoolean)
  {
    Drawable localDrawable;
    if (paramBoolean) {
      localDrawable = this.mVerticalTrack;
    } else {
      localDrawable = this.mHorizontalTrack;
    }
    if (localDrawable != null)
    {
      if (this.mBoundsChanged) {
        localDrawable.setBounds(paramRect);
      }
      localDrawable.draw(paramCanvas);
    }
  }
  
  private void propagateCurrentState(Drawable paramDrawable)
  {
    if (paramDrawable != null)
    {
      if (this.mMutated) {
        paramDrawable.mutate();
      }
      paramDrawable.setState(getState());
      paramDrawable.setCallback(this);
      if (this.mHasSetAlpha) {
        paramDrawable.setAlpha(this.mAlpha);
      }
      if (this.mHasSetColorFilter) {
        paramDrawable.setColorFilter(this.mColorFilter);
      }
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    boolean bool1 = this.mVertical;
    int i = this.mExtent;
    int j = this.mRange;
    boolean bool2;
    int k;
    if ((i > 0) && (j > i))
    {
      bool2 = true;
      k = 1;
    }
    else
    {
      if (bool1) {
        bool2 = this.mAlwaysDrawVerticalTrack;
      } else {
        bool2 = this.mAlwaysDrawHorizontalTrack;
      }
      k = 0;
    }
    Rect localRect = getBounds();
    if (paramCanvas.quickReject(localRect.left, localRect.top, localRect.right, localRect.bottom, Canvas.EdgeType.AA)) {
      return;
    }
    if (bool2) {
      drawTrack(paramCanvas, localRect, bool1);
    }
    if (k != 0)
    {
      if (bool1) {
        k = localRect.height();
      } else {
        k = localRect.width();
      }
      if (bool1) {
        m = localRect.width();
      } else {
        m = localRect.height();
      }
      int m = ScrollBarUtils.getThumbLength(k, m, i, j);
      drawThumb(paramCanvas, localRect, ScrollBarUtils.getThumbOffset(k, m, i, j, this.mOffset), m, bool1);
    }
  }
  
  public int getAlpha()
  {
    return this.mAlpha;
  }
  
  public boolean getAlwaysDrawHorizontalTrack()
  {
    return this.mAlwaysDrawHorizontalTrack;
  }
  
  public boolean getAlwaysDrawVerticalTrack()
  {
    return this.mAlwaysDrawVerticalTrack;
  }
  
  public ColorFilter getColorFilter()
  {
    return this.mColorFilter;
  }
  
  public Drawable getHorizontalThumbDrawable()
  {
    return this.mHorizontalThumb;
  }
  
  public Drawable getHorizontalTrackDrawable()
  {
    return this.mHorizontalTrack;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public int getSize(boolean paramBoolean)
  {
    int i = 0;
    int j = 0;
    if (paramBoolean)
    {
      localDrawable = this.mVerticalTrack;
      if (localDrawable != null)
      {
        j = localDrawable.getIntrinsicWidth();
      }
      else
      {
        localDrawable = this.mVerticalThumb;
        if (localDrawable != null) {
          j = localDrawable.getIntrinsicWidth();
        }
      }
      return j;
    }
    Drawable localDrawable = this.mHorizontalTrack;
    if (localDrawable != null)
    {
      j = localDrawable.getIntrinsicHeight();
    }
    else
    {
      localDrawable = this.mHorizontalThumb;
      j = i;
      if (localDrawable != null) {
        j = localDrawable.getIntrinsicHeight();
      }
    }
    return j;
  }
  
  public Drawable getVerticalThumbDrawable()
  {
    return this.mVerticalThumb;
  }
  
  public Drawable getVerticalTrackDrawable()
  {
    return this.mVerticalTrack;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    invalidateSelf();
  }
  
  public boolean isStateful()
  {
    Drawable localDrawable = this.mVerticalTrack;
    if ((localDrawable == null) || (!localDrawable.isStateful()))
    {
      localDrawable = this.mVerticalThumb;
      if ((localDrawable == null) || (!localDrawable.isStateful()))
      {
        localDrawable = this.mHorizontalTrack;
        if ((localDrawable == null) || (!localDrawable.isStateful()))
        {
          localDrawable = this.mHorizontalThumb;
          if (((localDrawable == null) || (!localDrawable.isStateful())) && (!super.isStateful())) {
            break label76;
          }
        }
      }
    }
    boolean bool = true;
    return bool;
    label76:
    bool = false;
    return bool;
  }
  
  public ScrollBarDrawable mutate()
  {
    if ((!this.mMutated) && (super.mutate() == this))
    {
      Drawable localDrawable = this.mVerticalTrack;
      if (localDrawable != null) {
        localDrawable.mutate();
      }
      localDrawable = this.mVerticalThumb;
      if (localDrawable != null) {
        localDrawable.mutate();
      }
      localDrawable = this.mHorizontalTrack;
      if (localDrawable != null) {
        localDrawable.mutate();
      }
      localDrawable = this.mHorizontalThumb;
      if (localDrawable != null) {
        localDrawable.mutate();
      }
      this.mMutated = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    this.mBoundsChanged = true;
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    boolean bool1 = super.onStateChange(paramArrayOfInt);
    Drawable localDrawable = this.mVerticalTrack;
    boolean bool2 = bool1;
    if (localDrawable != null) {
      bool2 = bool1 | localDrawable.setState(paramArrayOfInt);
    }
    localDrawable = this.mVerticalThumb;
    bool1 = bool2;
    if (localDrawable != null) {
      bool1 = bool2 | localDrawable.setState(paramArrayOfInt);
    }
    localDrawable = this.mHorizontalTrack;
    bool2 = bool1;
    if (localDrawable != null) {
      bool2 = bool1 | localDrawable.setState(paramArrayOfInt);
    }
    localDrawable = this.mHorizontalThumb;
    bool1 = bool2;
    if (localDrawable != null) {
      bool1 = bool2 | localDrawable.setState(paramArrayOfInt);
    }
    return bool1;
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    scheduleSelf(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    this.mAlpha = paramInt;
    this.mHasSetAlpha = true;
    Drawable localDrawable = this.mVerticalTrack;
    if (localDrawable != null) {
      localDrawable.setAlpha(paramInt);
    }
    localDrawable = this.mVerticalThumb;
    if (localDrawable != null) {
      localDrawable.setAlpha(paramInt);
    }
    localDrawable = this.mHorizontalTrack;
    if (localDrawable != null) {
      localDrawable.setAlpha(paramInt);
    }
    localDrawable = this.mHorizontalThumb;
    if (localDrawable != null) {
      localDrawable.setAlpha(paramInt);
    }
  }
  
  public void setAlwaysDrawHorizontalTrack(boolean paramBoolean)
  {
    this.mAlwaysDrawHorizontalTrack = paramBoolean;
  }
  
  public void setAlwaysDrawVerticalTrack(boolean paramBoolean)
  {
    this.mAlwaysDrawVerticalTrack = paramBoolean;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.mColorFilter = paramColorFilter;
    this.mHasSetColorFilter = true;
    Drawable localDrawable = this.mVerticalTrack;
    if (localDrawable != null) {
      localDrawable.setColorFilter(paramColorFilter);
    }
    localDrawable = this.mVerticalThumb;
    if (localDrawable != null) {
      localDrawable.setColorFilter(paramColorFilter);
    }
    localDrawable = this.mHorizontalTrack;
    if (localDrawable != null) {
      localDrawable.setColorFilter(paramColorFilter);
    }
    localDrawable = this.mHorizontalThumb;
    if (localDrawable != null) {
      localDrawable.setColorFilter(paramColorFilter);
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public void setHorizontalThumbDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mHorizontalThumb;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    propagateCurrentState(paramDrawable);
    this.mHorizontalThumb = paramDrawable;
  }
  
  public void setHorizontalTrackDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mHorizontalTrack;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    propagateCurrentState(paramDrawable);
    this.mHorizontalTrack = paramDrawable;
  }
  
  public void setParameters(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (this.mVertical != paramBoolean)
    {
      this.mVertical = paramBoolean;
      this.mBoundsChanged = true;
    }
    if ((this.mRange != paramInt1) || (this.mOffset != paramInt2) || (this.mExtent != paramInt3))
    {
      this.mRange = paramInt1;
      this.mOffset = paramInt2;
      this.mExtent = paramInt3;
      this.mRangeChanged = true;
    }
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28)
  public void setVerticalThumbDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mVerticalThumb;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    propagateCurrentState(paramDrawable);
    this.mVerticalThumb = paramDrawable;
  }
  
  public void setVerticalTrackDrawable(Drawable paramDrawable)
  {
    Drawable localDrawable = this.mVerticalTrack;
    if (localDrawable != null) {
      localDrawable.setCallback(null);
    }
    propagateCurrentState(paramDrawable);
    this.mVerticalTrack = paramDrawable;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ScrollBarDrawable: range=");
    localStringBuilder.append(this.mRange);
    localStringBuilder.append(" offset=");
    localStringBuilder.append(this.mOffset);
    localStringBuilder.append(" extent=");
    localStringBuilder.append(this.mExtent);
    String str;
    if (this.mVertical) {
      str = " V";
    } else {
      str = " H";
    }
    localStringBuilder.append(str);
    return localStringBuilder.toString();
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    unscheduleSelf(paramRunnable);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ScrollBarDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */