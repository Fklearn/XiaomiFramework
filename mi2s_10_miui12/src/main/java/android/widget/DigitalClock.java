package android.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import java.util.Calendar;

@Deprecated
public class DigitalClock
  extends TextView
{
  Calendar mCalendar;
  String mFormat;
  private FormatChangeObserver mFormatChangeObserver;
  private Handler mHandler;
  private Runnable mTicker;
  private boolean mTickerStopped = false;
  
  public DigitalClock(Context paramContext)
  {
    super(paramContext);
    initClock();
  }
  
  public DigitalClock(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    initClock();
  }
  
  private void initClock()
  {
    if (this.mCalendar == null) {
      this.mCalendar = Calendar.getInstance();
    }
  }
  
  private void setFormat()
  {
    this.mFormat = DateFormat.getTimeFormatString(getContext());
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return DigitalClock.class.getName();
  }
  
  protected void onAttachedToWindow()
  {
    this.mTickerStopped = false;
    super.onAttachedToWindow();
    this.mFormatChangeObserver = new FormatChangeObserver();
    getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, this.mFormatChangeObserver);
    setFormat();
    this.mHandler = new Handler();
    this.mTicker = new Runnable()
    {
      public void run()
      {
        if (DigitalClock.this.mTickerStopped) {
          return;
        }
        DigitalClock.this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        DigitalClock localDigitalClock = DigitalClock.this;
        localDigitalClock.setText(DateFormat.format(localDigitalClock.mFormat, DigitalClock.this.mCalendar));
        DigitalClock.this.invalidate();
        long l = SystemClock.uptimeMillis();
        DigitalClock.this.mHandler.postAtTime(DigitalClock.this.mTicker, 1000L - l % 1000L + l);
      }
    };
    this.mTicker.run();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.mTickerStopped = true;
    getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
  }
  
  private class FormatChangeObserver
    extends ContentObserver
  {
    public FormatChangeObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      DigitalClock.this.setFormat();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DigitalClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */