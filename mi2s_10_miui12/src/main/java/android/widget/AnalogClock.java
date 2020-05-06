package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import com.android.internal.R.styleable;
import java.util.TimeZone;

@RemoteViews.RemoteView
@Deprecated
public class AnalogClock
  extends View
{
  private boolean mAttached;
  private Time mCalendar;
  private boolean mChanged;
  @UnsupportedAppUsage
  private Drawable mDial;
  private int mDialHeight;
  private int mDialWidth;
  private float mHour;
  @UnsupportedAppUsage
  private Drawable mHourHand;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.TIMEZONE_CHANGED"))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("time-zone");
        AnalogClock.access$002(AnalogClock.this, new Time(TimeZone.getTimeZone(paramAnonymousContext).getID()));
      }
      AnalogClock.this.onTimeChanged();
      AnalogClock.this.invalidate();
    }
  };
  @UnsupportedAppUsage
  private Drawable mMinuteHand;
  private float mMinutes;
  
  public AnalogClock(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AnalogClock(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public AnalogClock(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public AnalogClock(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext.getResources();
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AnalogClock, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.AnalogClock, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mDial = localTypedArray.getDrawable(0);
    if (this.mDial == null) {
      this.mDial = paramContext.getDrawable(17302115);
    }
    this.mHourHand = localTypedArray.getDrawable(1);
    if (this.mHourHand == null) {
      this.mHourHand = paramContext.getDrawable(17302116);
    }
    this.mMinuteHand = localTypedArray.getDrawable(2);
    if (this.mMinuteHand == null) {
      this.mMinuteHand = paramContext.getDrawable(17302117);
    }
    this.mCalendar = new Time();
    this.mDialWidth = this.mDial.getIntrinsicWidth();
    this.mDialHeight = this.mDial.getIntrinsicHeight();
  }
  
  private void onTimeChanged()
  {
    this.mCalendar.setToNow();
    int i = this.mCalendar.hour;
    int j = this.mCalendar.minute;
    int k = this.mCalendar.second;
    this.mMinutes = (j + k / 60.0F);
    this.mHour = (i + this.mMinutes / 60.0F);
    this.mChanged = true;
    updateContentDescription(this.mCalendar);
  }
  
  private void updateContentDescription(Time paramTime)
  {
    setContentDescription(DateUtils.formatDateTime(this.mContext, paramTime.toMillis(false), 129));
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!this.mAttached)
    {
      this.mAttached = true;
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIME_TICK");
      localIntentFilter.addAction("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      getContext().registerReceiverAsUser(this.mIntentReceiver, Process.myUserHandle(), localIntentFilter, null, getHandler());
    }
    this.mCalendar = new Time();
    onTimeChanged();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mAttached)
    {
      getContext().unregisterReceiver(this.mIntentReceiver);
      this.mAttached = false;
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    boolean bool = this.mChanged;
    if (bool) {
      this.mChanged = false;
    }
    int i = this.mRight - this.mLeft;
    int j = this.mBottom - this.mTop;
    int k = i / 2;
    int m = j / 2;
    Drawable localDrawable = this.mDial;
    int n = localDrawable.getIntrinsicWidth();
    int i1 = localDrawable.getIntrinsicHeight();
    int i2 = 0;
    if ((i < n) || (j < i1))
    {
      i2 = 1;
      float f = Math.min(i / n, j / i1);
      paramCanvas.save();
      paramCanvas.scale(f, f, k, m);
    }
    if (bool) {
      localDrawable.setBounds(k - n / 2, m - i1 / 2, n / 2 + k, i1 / 2 + m);
    }
    localDrawable.draw(paramCanvas);
    paramCanvas.save();
    paramCanvas.rotate(this.mHour / 12.0F * 360.0F, k, m);
    localDrawable = this.mHourHand;
    if (bool)
    {
      n = localDrawable.getIntrinsicWidth();
      i1 = localDrawable.getIntrinsicHeight();
      localDrawable.setBounds(k - n / 2, m - i1 / 2, n / 2 + k, m + i1 / 2);
    }
    localDrawable.draw(paramCanvas);
    paramCanvas.restore();
    paramCanvas.save();
    paramCanvas.rotate(this.mMinutes / 60.0F * 360.0F, k, m);
    localDrawable = this.mMinuteHand;
    if (bool)
    {
      n = localDrawable.getIntrinsicWidth();
      i1 = localDrawable.getIntrinsicHeight();
      localDrawable.setBounds(k - n / 2, m - i1 / 2, n / 2 + k, m + i1 / 2);
    }
    localDrawable.draw(paramCanvas);
    paramCanvas.restore();
    if (i2 != 0) {
      paramCanvas.restore();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = View.MeasureSpec.getMode(paramInt2);
    int m = View.MeasureSpec.getSize(paramInt2);
    float f1 = 1.0F;
    float f2 = 1.0F;
    float f3 = f1;
    if (i != 0)
    {
      i = this.mDialWidth;
      f3 = f1;
      if (j < i) {
        f3 = j / i;
      }
    }
    f1 = f2;
    if (k != 0)
    {
      k = this.mDialHeight;
      f1 = f2;
      if (m < k) {
        f1 = m / k;
      }
    }
    f3 = Math.min(f3, f1);
    setMeasuredDimension(resolveSizeAndState((int)(this.mDialWidth * f3), paramInt1, 0), resolveSizeAndState((int)(this.mDialHeight * f3), paramInt2, 0));
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mChanged = true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AnalogClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */