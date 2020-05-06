package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityThread;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R.styleable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@RemoteViews.RemoteView
public class DateTimeView
  extends TextView
{
  private static final int SHOW_MONTH_DAY_YEAR = 1;
  private static final int SHOW_TIME = 0;
  private static final ThreadLocal<ReceiverInfo> sReceiverInfo = new ThreadLocal();
  int mLastDisplay = -1;
  java.text.DateFormat mLastFormat;
  private String mNowText;
  private boolean mShowRelativeTime;
  Date mTime;
  long mTimeMillis;
  private long mUpdateTimeMillis;
  
  public DateTimeView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  @UnsupportedAppUsage
  public DateTimeView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    paramContext = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.DateTimeView, 0, 0);
    int i = paramContext.getIndexCount();
    for (int j = 0; j < i; j++) {
      if (paramContext.getIndex(j) == 0) {
        setShowRelativeTime(paramContext.getBoolean(j, false));
      }
    }
    paramContext.recycle();
  }
  
  private long computeNextMidnight(TimeZone paramTimeZone)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.setTimeZone(paramTimeZone);
    localCalendar.add(5, 1);
    localCalendar.set(11, 0);
    localCalendar.set(12, 0);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    return localCalendar.getTimeInMillis();
  }
  
  private static int dayDistance(TimeZone paramTimeZone, long paramLong1, long paramLong2)
  {
    return Time.getJulianDay(paramLong2, paramTimeZone.getOffset(paramLong2) / 1000) - Time.getJulianDay(paramLong1, paramTimeZone.getOffset(paramLong1) / 1000);
  }
  
  private java.text.DateFormat getTimeFormat()
  {
    return android.text.format.DateFormat.getTimeFormat(getContext());
  }
  
  public static void setReceiverHandler(Handler paramHandler)
  {
    ReceiverInfo localReceiverInfo1 = (ReceiverInfo)sReceiverInfo.get();
    ReceiverInfo localReceiverInfo2 = localReceiverInfo1;
    if (localReceiverInfo1 == null)
    {
      localReceiverInfo2 = new ReceiverInfo(null);
      sReceiverInfo.set(localReceiverInfo2);
    }
    localReceiverInfo2.setHandler(paramHandler);
  }
  
  private void updateNowText()
  {
    if (!this.mShowRelativeTime) {
      return;
    }
    this.mNowText = getContext().getResources().getString(17040588);
  }
  
  private void updateRelativeTime()
  {
    long l1 = System.currentTimeMillis();
    long l2 = Math.abs(l1 - this.mTimeMillis);
    int i;
    if (l1 >= this.mTimeMillis) {
      i = 1;
    } else {
      i = 0;
    }
    if (l2 < 60000L)
    {
      setText(this.mNowText);
      this.mUpdateTimeMillis = (this.mTimeMillis + 60000L + 1L);
      return;
    }
    int j;
    Object localObject;
    int k;
    if (l2 < 3600000L)
    {
      j = (int)(l2 / 60000L);
      localObject = getContext().getResources();
      if (i != 0) {
        k = 18153486;
      } else {
        k = 18153487;
      }
      localObject = String.format(((Resources)localObject).getQuantityString(k, j), new Object[] { Integer.valueOf(j) });
      l2 = 60000L;
      k = j;
    }
    else if (l2 < 86400000L)
    {
      j = (int)(l2 / 3600000L);
      localObject = getContext().getResources();
      if (i != 0) {
        k = 18153481;
      } else {
        k = 18153482;
      }
      localObject = String.format(((Resources)localObject).getQuantityString(k, j), new Object[] { Integer.valueOf(j) });
      l2 = 3600000L;
      k = j;
    }
    else if (l2 < 31449600000L)
    {
      TimeZone localTimeZone = TimeZone.getDefault();
      j = Math.max(Math.abs(dayDistance(localTimeZone, this.mTimeMillis, l1)), 1);
      localObject = getContext().getResources();
      if (i != 0) {
        k = 18153476;
      } else {
        k = 18153477;
      }
      localObject = String.format(((Resources)localObject).getQuantityString(k, j), new Object[] { Integer.valueOf(j) });
      if ((i == 0) && (j == 1))
      {
        l2 = 86400000L;
      }
      else
      {
        this.mUpdateTimeMillis = computeNextMidnight(localTimeZone);
        l2 = -1L;
      }
      k = j;
    }
    else
    {
      j = (int)(l2 / 31449600000L);
      localObject = getContext().getResources();
      if (i != 0) {
        k = 18153491;
      } else {
        k = 18153492;
      }
      localObject = String.format(((Resources)localObject).getQuantityString(k, j), new Object[] { Integer.valueOf(j) });
      l2 = 31449600000L;
      k = j;
    }
    if (l2 != -1L) {
      if (i != 0) {
        this.mUpdateTimeMillis = (this.mTimeMillis + (k + 1) * l2 + 1L);
      } else {
        this.mUpdateTimeMillis = (this.mTimeMillis - k * l2 + 1L);
      }
    }
    setText((CharSequence)localObject);
  }
  
  void clearFormatAndUpdate()
  {
    this.mLastFormat = null;
    update();
  }
  
  public boolean isShowRelativeTime()
  {
    return this.mShowRelativeTime;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    ReceiverInfo localReceiverInfo1 = (ReceiverInfo)sReceiverInfo.get();
    ReceiverInfo localReceiverInfo2 = localReceiverInfo1;
    if (localReceiverInfo1 == null)
    {
      localReceiverInfo2 = new ReceiverInfo(null);
      sReceiverInfo.set(localReceiverInfo2);
    }
    localReceiverInfo2.addView(this);
    if (this.mShowRelativeTime) {
      update();
    }
  }
  
  protected void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    updateNowText();
    update();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    ReceiverInfo localReceiverInfo = (ReceiverInfo)sReceiverInfo.get();
    if (localReceiverInfo != null) {
      localReceiverInfo.removeView(this);
    }
  }
  
  public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo paramAccessibilityNodeInfo)
  {
    super.onInitializeAccessibilityNodeInfoInternal(paramAccessibilityNodeInfo);
    if (this.mShowRelativeTime)
    {
      long l1 = System.currentTimeMillis();
      long l2 = Math.abs(l1 - this.mTimeMillis);
      int i;
      if (l1 >= this.mTimeMillis) {
        i = 1;
      } else {
        i = 0;
      }
      Object localObject;
      if (l2 < 60000L)
      {
        localObject = this.mNowText;
      }
      else
      {
        int j;
        if (l2 < 3600000L)
        {
          j = (int)(l2 / 60000L);
          localObject = getContext().getResources();
          if (i != 0) {
            i = 18153484;
          } else {
            i = 18153485;
          }
          localObject = String.format(((Resources)localObject).getQuantityString(i, j), new Object[] { Integer.valueOf(j) });
        }
        else if (l2 < 86400000L)
        {
          j = (int)(l2 / 3600000L);
          localObject = getContext().getResources();
          if (i != 0) {
            i = 18153479;
          } else {
            i = 18153480;
          }
          localObject = String.format(((Resources)localObject).getQuantityString(i, j), new Object[] { Integer.valueOf(j) });
        }
        else if (l2 < 31449600000L)
        {
          j = Math.max(Math.abs(dayDistance(TimeZone.getDefault(), this.mTimeMillis, l1)), 1);
          localObject = getContext().getResources();
          if (i != 0) {
            i = 18153474;
          } else {
            i = 18153475;
          }
          localObject = String.format(((Resources)localObject).getQuantityString(i, j), new Object[] { Integer.valueOf(j) });
        }
        else
        {
          j = (int)(l2 / 31449600000L);
          localObject = getContext().getResources();
          if (i != 0) {
            i = 18153489;
          } else {
            i = 18153490;
          }
          localObject = String.format(((Resources)localObject).getQuantityString(i, j), new Object[] { Integer.valueOf(j) });
        }
      }
      paramAccessibilityNodeInfo.setText((CharSequence)localObject);
    }
  }
  
  @RemotableViewMethod
  public void setShowRelativeTime(boolean paramBoolean)
  {
    this.mShowRelativeTime = paramBoolean;
    updateNowText();
    update();
  }
  
  @RemotableViewMethod
  @UnsupportedAppUsage
  public void setTime(long paramLong)
  {
    Time localTime = new Time();
    localTime.set(paramLong);
    this.mTimeMillis = localTime.toMillis(false);
    this.mTime = new Date(localTime.year - 1900, localTime.month, localTime.monthDay, localTime.hour, localTime.minute, 0);
    update();
  }
  
  @RemotableViewMethod
  public void setVisibility(int paramInt)
  {
    int i;
    if ((paramInt != 8) && (getVisibility() == 8)) {
      i = 1;
    } else {
      i = 0;
    }
    super.setVisibility(paramInt);
    if (i != 0) {
      update();
    }
  }
  
  @UnsupportedAppUsage
  void update()
  {
    if ((this.mTime != null) && (getVisibility() != 8))
    {
      if (this.mShowRelativeTime)
      {
        updateRelativeTime();
        return;
      }
      Object localObject = this.mTime;
      localObject = new Time();
      ((Time)localObject).set(this.mTimeMillis);
      ((Time)localObject).second = 0;
      ((Time)localObject).hour -= 12;
      long l1 = ((Time)localObject).toMillis(false);
      ((Time)localObject).hour += 12;
      long l2 = ((Time)localObject).toMillis(false);
      ((Time)localObject).hour = 0;
      ((Time)localObject).minute = 0;
      long l3 = ((Time)localObject).toMillis(false);
      ((Time)localObject).monthDay += 1;
      long l4 = ((Time)localObject).toMillis(false);
      ((Time)localObject).set(System.currentTimeMillis());
      ((Time)localObject).second = 0;
      long l5 = ((Time)localObject).normalize(false);
      int i;
      if (((l5 >= l3) && (l5 < l4)) || ((l5 >= l1) && (l5 < l2))) {
        i = 0;
      } else {
        i = 1;
      }
      if ((i == this.mLastDisplay) && (this.mLastFormat != null))
      {
        localObject = this.mLastFormat;
      }
      else
      {
        if (i != 0)
        {
          if (i == 1)
          {
            localObject = java.text.DateFormat.getDateInstance(3);
          }
          else
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("unknown display value: ");
            ((StringBuilder)localObject).append(i);
            throw new RuntimeException(((StringBuilder)localObject).toString());
          }
        }
        else {
          localObject = getTimeFormat();
        }
        this.mLastFormat = ((java.text.DateFormat)localObject);
      }
      setText(((java.text.DateFormat)localObject).format(this.mTime));
      if (i == 0)
      {
        if (l2 > l4) {
          l3 = l2;
        } else {
          l3 = l4;
        }
        this.mUpdateTimeMillis = l3;
      }
      else if (this.mTimeMillis < l5)
      {
        this.mUpdateTimeMillis = 0L;
      }
      else
      {
        if (l1 < l3) {
          l3 = l1;
        }
        this.mUpdateTimeMillis = l3;
      }
      return;
    }
  }
  
  private static class ReceiverInfo
  {
    private final ArrayList<DateTimeView> mAttachedViews = new ArrayList();
    private Handler mHandler = new Handler();
    private final ContentObserver mObserver = new ContentObserver(new Handler())
    {
      public void onChange(boolean paramAnonymousBoolean)
      {
        DateTimeView.ReceiverInfo.this.updateAll();
      }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (("android.intent.action.TIME_TICK".equals(paramAnonymousIntent.getAction())) && (System.currentTimeMillis() < DateTimeView.ReceiverInfo.this.getSoonestUpdateTime())) {
          return;
        }
        DateTimeView.ReceiverInfo.this.updateAll();
      }
    };
    
    static final Context getApplicationContextIfAvailable(Context paramContext)
    {
      paramContext = paramContext.getApplicationContext();
      if (paramContext == null) {
        paramContext = ActivityThread.currentApplication().getApplicationContext();
      }
      return paramContext;
    }
    
    public void addView(DateTimeView paramDateTimeView)
    {
      synchronized (this.mAttachedViews)
      {
        boolean bool = this.mAttachedViews.isEmpty();
        this.mAttachedViews.add(paramDateTimeView);
        if (bool) {
          register(getApplicationContextIfAvailable(paramDateTimeView.getContext()));
        }
        return;
      }
    }
    
    long getSoonestUpdateTime()
    {
      long l1 = Long.MAX_VALUE;
      synchronized (this.mAttachedViews)
      {
        int i = this.mAttachedViews.size();
        int j = 0;
        while (j < i)
        {
          long l2 = ((DateTimeView)this.mAttachedViews.get(j)).mUpdateTimeMillis;
          long l3 = l1;
          if (l2 < l1) {
            l3 = l2;
          }
          j++;
          l1 = l3;
        }
        return l1;
      }
    }
    
    void register(Context paramContext)
    {
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.TIME_TICK");
      localIntentFilter.addAction("android.intent.action.TIME_SET");
      localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
      localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
      paramContext.registerReceiver(this.mReceiver, localIntentFilter, null, this.mHandler);
    }
    
    public void removeView(DateTimeView paramDateTimeView)
    {
      synchronized (this.mAttachedViews)
      {
        if ((this.mAttachedViews.remove(paramDateTimeView)) && (this.mAttachedViews.isEmpty())) {
          unregister(getApplicationContextIfAvailable(paramDateTimeView.getContext()));
        }
        return;
      }
    }
    
    public void setHandler(Handler paramHandler)
    {
      this.mHandler = paramHandler;
      synchronized (this.mAttachedViews)
      {
        if (!this.mAttachedViews.isEmpty())
        {
          unregister(((DateTimeView)this.mAttachedViews.get(0)).getContext());
          register(((DateTimeView)this.mAttachedViews.get(0)).getContext());
        }
        return;
      }
    }
    
    void unregister(Context paramContext)
    {
      paramContext.unregisterReceiver(this.mReceiver);
    }
    
    void updateAll()
    {
      synchronized (this.mAttachedViews)
      {
        int i = this.mAttachedViews.size();
        for (int j = 0; j < i; j++)
        {
          DateTimeView localDateTimeView = (DateTimeView)this.mAttachedViews.get(j);
          _..Lambda.DateTimeView.ReceiverInfo.AVLnX7U5lTcE9jLnlKKNAT1GUeI localAVLnX7U5lTcE9jLnlKKNAT1GUeI = new android/widget/_$$Lambda$DateTimeView$ReceiverInfo$AVLnX7U5lTcE9jLnlKKNAT1GUeI;
          localAVLnX7U5lTcE9jLnlKKNAT1GUeI.<init>(localDateTimeView);
          localDateTimeView.post(localAVLnX7U5lTcE9jLnlKKNAT1GUeI);
        }
        return;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/DateTimeView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */