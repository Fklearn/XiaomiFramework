package miui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class CircleProgressView
  extends FrameLayout
{
  private static int MAX_PROGRESS = 100;
  private static String TAG = "CircleProgressView";
  private int mAngle;
  private RectF mArcRect;
  private int mCurProgress;
  private int mMaxProgress = MAX_PROGRESS;
  private Bitmap mMemBitmap;
  private Canvas mMemCanvas;
  private Paint mPaint;
  private Drawable mProgressDrawable;
  
  public CircleProgressView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CircleProgressView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public CircleProgressView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setBackgroundResource(285671507);
    setProgressResource(285671508);
    this.mPaint = new Paint();
    this.mPaint.setStyle(Paint.Style.FILL);
    this.mPaint.setAntiAlias(true);
    this.mPaint.setColor(0);
    this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
  }
  
  public int getMaxProgress()
  {
    return this.mMaxProgress;
  }
  
  public int getProgress()
  {
    return this.mCurProgress;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.mProgressDrawable != null)
    {
      this.mMemBitmap.eraseColor(0);
      this.mProgressDrawable.draw(this.mMemCanvas);
      Canvas localCanvas = this.mMemCanvas;
      RectF localRectF = this.mArcRect;
      int i = this.mAngle;
      localCanvas.drawArc(localRectF, 270 - i, i, true, this.mPaint);
      paramCanvas.drawBitmap(this.mMemBitmap, (getWidth() - this.mMemBitmap.getWidth()) / 2, (getHeight() - this.mMemBitmap.getHeight()) / 2, null);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    setMeasuredDimension(Math.max(getMeasuredWidth(), getSuggestedMinimumWidth()), Math.max(getMeasuredHeight(), getSuggestedMinimumHeight()));
  }
  
  public void setMaxProgress(int paramInt)
  {
    if ((paramInt > 0) && (this.mMaxProgress != paramInt))
    {
      this.mMaxProgress = paramInt;
      setProgress(this.mCurProgress);
    }
  }
  
  public void setProgress(int paramInt)
  {
    this.mCurProgress = Math.min(paramInt, this.mMaxProgress);
    this.mCurProgress = Math.max(0, this.mCurProgress);
    paramInt = this.mMaxProgress;
    paramInt = (paramInt - this.mCurProgress) * 360 / paramInt;
    if (paramInt != this.mAngle)
    {
      String str = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("progress:");
      localStringBuilder.append(this.mCurProgress);
      Log.i(str, localStringBuilder.toString());
      this.mAngle = paramInt;
      invalidate();
    }
  }
  
  public void setProgressResource(int paramInt)
  {
    this.mProgressDrawable = this.mContext.getResources().getDrawable(paramInt);
    paramInt = this.mProgressDrawable.getIntrinsicWidth();
    int i = this.mProgressDrawable.getIntrinsicHeight();
    this.mProgressDrawable.setBounds(0, 0, paramInt, i);
    this.mMemBitmap = Bitmap.createBitmap(paramInt, i, Bitmap.Config.ARGB_8888);
    this.mMemCanvas = new Canvas(this.mMemBitmap);
    this.mArcRect = new RectF(0.0F, 0.0F, paramInt, i);
    requestLayout();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/widget/CircleProgressView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */