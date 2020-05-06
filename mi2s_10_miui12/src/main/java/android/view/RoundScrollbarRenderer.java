package android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

class RoundScrollbarRenderer
{
  private static final int DEFAULT_THUMB_COLOR = -1512723;
  private static final int DEFAULT_TRACK_COLOR = 1291845631;
  private static final int MAX_SCROLLBAR_ANGLE_SWIPE = 16;
  private static final int MIN_SCROLLBAR_ANGLE_SWIPE = 6;
  private static final int SCROLLBAR_ANGLE_RANGE = 90;
  private static final float WIDTH_PERCENTAGE = 0.02F;
  private final int mMaskThickness;
  private final View mParent;
  private final RectF mRect = new RectF();
  private final Paint mThumbPaint = new Paint();
  private final Paint mTrackPaint = new Paint();
  
  public RoundScrollbarRenderer(View paramView)
  {
    this.mThumbPaint.setAntiAlias(true);
    this.mThumbPaint.setStrokeCap(Paint.Cap.ROUND);
    this.mThumbPaint.setStyle(Paint.Style.STROKE);
    this.mTrackPaint.setAntiAlias(true);
    this.mTrackPaint.setStrokeCap(Paint.Cap.ROUND);
    this.mTrackPaint.setStyle(Paint.Style.STROKE);
    this.mParent = paramView;
    this.mMaskThickness = paramView.getContext().getResources().getDimensionPixelSize(17105047);
  }
  
  private static int applyAlpha(int paramInt, float paramFloat)
  {
    return Color.argb((int)(Color.alpha(paramInt) * paramFloat), Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt));
  }
  
  private static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2) {
      return paramFloat2;
    }
    if (paramFloat1 > paramFloat3) {
      return paramFloat3;
    }
    return paramFloat1;
  }
  
  private void setThumbColor(int paramInt)
  {
    if (this.mThumbPaint.getColor() != paramInt) {
      this.mThumbPaint.setColor(paramInt);
    }
  }
  
  private void setTrackColor(int paramInt)
  {
    if (this.mTrackPaint.getColor() != paramInt) {
      this.mTrackPaint.setColor(paramInt);
    }
  }
  
  public void drawRoundScrollbars(Canvas paramCanvas, float paramFloat, Rect paramRect)
  {
    if (paramFloat == 0.0F) {
      return;
    }
    float f1 = this.mParent.computeVerticalScrollRange();
    float f2 = this.mParent.computeVerticalScrollExtent();
    if ((f2 > 0.0F) && (f1 > f2))
    {
      float f3 = Math.max(0, this.mParent.computeVerticalScrollOffset());
      float f4 = this.mParent.computeVerticalScrollExtent();
      f2 = this.mParent.getWidth() * 0.02F;
      this.mThumbPaint.setStrokeWidth(f2);
      this.mTrackPaint.setStrokeWidth(f2);
      setThumbColor(applyAlpha(-1512723, paramFloat));
      setTrackColor(applyAlpha(1291845631, paramFloat));
      paramFloat = clamp(f4 / f1 * 90.0F, 6.0F, 16.0F);
      f1 = clamp((90.0F - paramFloat) * f3 / (f1 - f4) - 45.0F, -45.0F, 45.0F - paramFloat);
      f2 = f2 / 2.0F + this.mMaskThickness;
      this.mRect.set(paramRect.left + f2, paramRect.top + f2, paramRect.right - f2, paramRect.bottom - f2);
      paramCanvas.drawArc(this.mRect, -45.0F, 90.0F, false, this.mTrackPaint);
      paramCanvas.drawArc(this.mRect, f1, paramFloat, false, this.mThumbPaint);
      return;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/RoundScrollbarRenderer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */