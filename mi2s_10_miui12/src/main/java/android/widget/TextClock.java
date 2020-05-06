package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R.styleable;
import java.util.Calendar;
import java.util.TimeZone;
import libcore.icu.LocaleData;

@RemoteViews.RemoteView
public class TextClock
  extends TextView
{
  @Deprecated
  public static final CharSequence DEFAULT_FORMAT_12_HOUR = "h:mm a";
  @Deprecated
  public static final CharSequence DEFAULT_FORMAT_24_HOUR = "H:mm";
  private CharSequence mDescFormat;
  private CharSequence mDescFormat12;
  private CharSequence mDescFormat24;
  @ViewDebug.ExportedProperty
  private CharSequence mFormat;
  private CharSequence mFormat12;
  private CharSequence mFormat24;
  private ContentObserver mFormatChangeObserver;
  @ViewDebug.ExportedProperty
  private boolean mHasSeconds;
  private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (TextClock.this.mStopTicking) {
        return;
      }
      if ((TextClock.this.mTimeZone == null) && ("android.intent.action.TIMEZONE_CHANGED".equals(paramAnonymousIntent.getAction())))
      {
        paramAnonymousContext = paramAnonymousIntent.getStringExtra("time-zone");
        TextClock.this.createTime(paramAnonymousContext);
      }
      else if ((!TextClock.this.mShouldRunTicker) && (("android.intent.action.TIME_TICK".equals(paramAnonymousIntent.getAction())) || ("android.intent.action.TIME_SET".equals(paramAnonymousIntent.getAction()))))
      {
        return;
      }
      TextClock.this.onTimeChanged();
    }
  };
  private boolean mRegistered;
  private boolean mShouldRunTicker;
  private boolean mShowCurrentUserTime;
  private boolean mStopTicking;
  private final Runnable mTicker = new Runnable()
  {
    public void run()
    {
      if (TextClock.this.mStopTicking) {
        return;
      }
      TextClock.this.onTimeChanged();
      long l = SystemClock.uptimeMillis();
      TextClock.this.getHandler().postAtTime(TextClock.this.mTicker, 1000L - l % 1000L + l);
    }
  };
  private Calendar mTime;
  private String mTimeZone;
  
  public TextClock(Context paramContext)
  {
    super(paramContext);
    init();
  }
  
  public TextClock(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TextClock(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public TextClock(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.TextClock, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.TextClock, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    try
    {
      this.mFormat12 = localTypedArray.getText(0);
      this.mFormat24 = localTypedArray.getText(1);
      this.mTimeZone = localTypedArray.getString(2);
      localTypedArray.recycle();
      init();
      return;
    }
    finally
    {
      localTypedArray.recycle();
    }
  }
  
  private static CharSequence abc(CharSequence paramCharSequence1, CharSequence paramCharSequence2, CharSequence paramCharSequence3)
  {
    if (paramCharSequence1 == null) {
      if (paramCharSequence2 == null) {
        paramCharSequence1 = paramCharSequence3;
      } else {
        paramCharSequence1 = paramCharSequence2;
      }
    }
    return paramCharSequence1;
  }
  
  private void chooseFormat()
  {
    boolean bool = is24HourModeEnabled();
    LocaleData localLocaleData = LocaleData.get(getContext().getResources().getConfiguration().locale);
    if (bool)
    {
      this.mFormat = abc(this.mFormat24, this.mFormat12, localLocaleData.timeFormat_Hm);
      this.mDescFormat = abc(this.mDescFormat24, this.mDescFormat12, this.mFormat);
    }
    else
    {
      this.mFormat = abc(this.mFormat12, this.mFormat24, localLocaleData.timeFormat_hm);
      this.mDescFormat = abc(this.mDescFormat12, this.mDescFormat24, this.mFormat);
    }
    bool = this.mHasSeconds;
    this.mHasSeconds = DateFormat.hasSeconds(this.mFormat);
    if ((this.mShouldRunTicker) && (bool != this.mHasSeconds)) {
      if (bool) {
        getHandler().removeCallbacks(this.mTicker);
      } else {
        this.mTicker.run();
      }
    }
  }
  
  private void createTime(String paramString)
  {
    if (paramString != null) {
      this.mTime = Calendar.getInstance(TimeZone.getTimeZone(paramString));
    } else {
      this.mTime = Calendar.getInstance();
    }
  }
  
  private void init()
  {
    if ((this.mFormat12 == null) || (this.mFormat24 == null))
    {
      LocaleData localLocaleData = LocaleData.get(getContext().getResources().getConfiguration().locale);
      if (this.mFormat12 == null) {
        this.mFormat12 = localLocaleData.timeFormat_hm;
      }
      if (this.mFormat24 == null) {
        this.mFormat24 = localLocaleData.timeFormat_Hm;
      }
    }
    createTime(this.mTimeZone);
    chooseFormat();
  }
  
  @UnsupportedAppUsage
  private void onTimeChanged()
  {
    this.mTime.setTimeInMillis(System.currentTimeMillis());
    setText(DateFormat.format(this.mFormat, this.mTime));
    setContentDescription(DateFormat.format(this.mDescFormat, this.mTime));
  }
  
  private void registerObserver()
  {
    if (this.mRegistered)
    {
      if (this.mFormatChangeObserver == null) {
        this.mFormatChangeObserver = new FormatChangeObserver(getHandler());
      }
      ContentResolver localContentResolver = getContext().getContentResolver();
      Uri localUri = Settings.System.getUriFor("time_12_24");
      if (this.mShowCurrentUserTime) {
        localContentResolver.registerContentObserver(localUri, true, this.mFormatChangeObserver, -1);
      } else {
        localContentResolver.registerContentObserver(localUri, true, this.mFormatChangeObserver, UserHandle.myUserId());
      }
    }
  }
  
  private void registerReceiver()
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_TICK");
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    getContext().registerReceiverAsUser(this.mIntentReceiver, Process.myUserHandle(), localIntentFilter, null, getHandler());
  }
  
  private void unregisterObserver()
  {
    if (this.mFormatChangeObserver != null) {
      getContext().getContentResolver().unregisterContentObserver(this.mFormatChangeObserver);
    }
  }
  
  private void unregisterReceiver()
  {
    getContext().unregisterReceiver(this.mIntentReceiver);
  }
  
  public void disableClockTick()
  {
    this.mStopTicking = true;
  }
  
  protected void encodeProperties(ViewHierarchyEncoder paramViewHierarchyEncoder)
  {
    super.encodeProperties(paramViewHierarchyEncoder);
    Object localObject1 = getFormat12Hour();
    Object localObject2 = null;
    if (localObject1 == null) {
      localObject1 = null;
    } else {
      localObject1 = ((CharSequence)localObject1).toString();
    }
    paramViewHierarchyEncoder.addProperty("format12Hour", (String)localObject1);
    localObject1 = getFormat24Hour();
    if (localObject1 == null) {
      localObject1 = null;
    } else {
      localObject1 = ((CharSequence)localObject1).toString();
    }
    paramViewHierarchyEncoder.addProperty("format24Hour", (String)localObject1);
    localObject1 = this.mFormat;
    if (localObject1 == null) {
      localObject1 = localObject2;
    } else {
      localObject1 = ((CharSequence)localObject1).toString();
    }
    paramViewHierarchyEncoder.addProperty("format", (String)localObject1);
    paramViewHierarchyEncoder.addProperty("hasSeconds", this.mHasSeconds);
  }
  
  @UnsupportedAppUsage
  public CharSequence getFormat()
  {
    return this.mFormat;
  }
  
  @ViewDebug.ExportedProperty
  public CharSequence getFormat12Hour()
  {
    return this.mFormat12;
  }
  
  @ViewDebug.ExportedProperty
  public CharSequence getFormat24Hour()
  {
    return this.mFormat24;
  }
  
  public String getTimeZone()
  {
    return this.mTimeZone;
  }
  
  public boolean is24HourModeEnabled()
  {
    if (this.mShowCurrentUserTime) {
      return DateFormat.is24HourFormat(getContext(), ActivityManager.getCurrentUser());
    }
    return DateFormat.is24HourFormat(getContext());
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!this.mRegistered)
    {
      this.mRegistered = true;
      registerReceiver();
      registerObserver();
      createTime(this.mTimeZone);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mRegistered)
    {
      unregisterReceiver();
      unregisterObserver();
      this.mRegistered = false;
    }
  }
  
  public void onVisibilityAggregated(boolean paramBoolean)
  {
    super.onVisibilityAggregated(paramBoolean);
    if ((!this.mShouldRunTicker) && (paramBoolean))
    {
      this.mShouldRunTicker = true;
      if (this.mHasSeconds) {
        this.mTicker.run();
      } else {
        onTimeChanged();
      }
    }
    else if ((this.mShouldRunTicker) && (!paramBoolean))
    {
      this.mShouldRunTicker = false;
      getHandler().removeCallbacks(this.mTicker);
    }
  }
  
  public void refresh()
  {
    onTimeChanged();
    invalidate();
  }
  
  public void setContentDescriptionFormat12Hour(CharSequence paramCharSequence)
  {
    this.mDescFormat12 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  public void setContentDescriptionFormat24Hour(CharSequence paramCharSequence)
  {
    this.mDescFormat24 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  @RemotableViewMethod
  public void setFormat12Hour(CharSequence paramCharSequence)
  {
    this.mFormat12 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  @RemotableViewMethod
  public void setFormat24Hour(CharSequence paramCharSequence)
  {
    this.mFormat24 = paramCharSequence;
    chooseFormat();
    onTimeChanged();
  }
  
  public void setShowCurrentUserTime(boolean paramBoolean)
  {
    this.mShowCurrentUserTime = paramBoolean;
    chooseFormat();
    onTimeChanged();
    unregisterObserver();
    registerObserver();
  }
  
  @RemotableViewMethod
  public void setTimeZone(String paramString)
  {
    this.mTimeZone = paramString;
    createTime(paramString);
    onTimeChanged();
  }
  
  private class FormatChangeObserver
    extends ContentObserver
  {
    public FormatChangeObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean)
    {
      TextClock.this.chooseFormat();
      TextClock.this.onTimeChanged();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      TextClock.this.chooseFormat();
      TextClock.this.onTimeChanged();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TextClock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */