package miui.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.ExtraNotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.MiuiSettings.SilenceMode;
import android.provider.Settings.Global;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.widget.SlidingButton;

public class RingerModeLayout
  extends LinearLayout
{
  private static final int ANIMATION_DURATION = 300;
  private static final String TAG = "RingerModeLayout";
  private int ContentHeight;
  private boolean mAnimating;
  private final Context mContext;
  private int mCurrentMode;
  private ViewGroup mDialogView;
  private H mHandler;
  private ImageView mHelpBtn;
  private View.OnClickListener mHelpButtonListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RingerModeLayout.this.mHelpBtn.setImageDrawable(RingerModeLayout.this.getResources().getDrawable(285671518));
      paramAnonymousView = ComponentName.unflattenFromString("com.android.settings/com.android.settings.Settings$MiuiSilentModeAcivity");
      if (paramAnonymousView == null) {
        return;
      }
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setComponent(paramAnonymousView);
      localIntent.setFlags(335544320);
      try
      {
        RingerModeLayout.this.mContext.startActivityAsUser(localIntent, UserHandle.CURRENT);
      }
      catch (Exception localException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("start activity exception, component = ");
        localStringBuilder.append(paramAnonymousView);
        Log.e("RingerModeLayout", localStringBuilder.toString());
      }
      RingerModeLayout.this.mVolumeDialog.dismiss();
    }
  };
  private Looper mLooper;
  private int mOrignalMode;
  private long mOrignalRemain;
  private View.OnClickListener mRadioButtonListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Uri localUri = ExtraNotificationManager.getConditionId(RingerModeLayout.this.mContext);
      int i = 0;
      if (paramAnonymousView.getId() == 285802645)
      {
        Log.d("RingerModeLayout", "set total mode");
        i = 1;
      }
      else if (paramAnonymousView.getId() == 285802644)
      {
        Log.d("RingerModeLayout", "set standard mode");
        i = 4;
      }
      MiuiSettings.SilenceMode.setSilenceMode(RingerModeLayout.this.mContext, i, localUri);
    }
  };
  private RadioGroup mRadioGroup;
  private boolean mRemainTextShown;
  private TextView mRemainTime_1;
  private TextView mRemainTime_2;
  private TextView mSelectedText;
  private boolean mShowing;
  private CompoundButton.OnCheckedChangeListener mSilenceButtonChangedListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      if (!RingerModeLayout.this.mShowing) {
        return;
      }
      if (paramAnonymousBoolean)
      {
        Log.d("RingerModeLayout", "sliding to true");
        RingerModeLayout.this.mVolumeDialog.setExpandedH(false);
        RingerModeLayout.this.expandSilenceModeContent(true);
        if (RingerModeLayout.this.mCurrentMode == 0)
        {
          int i = MiuiSettings.SilenceMode.getLastestQuietMode(RingerModeLayout.this.mContext);
          MiuiSettings.SilenceMode.setSilenceMode(RingerModeLayout.this.mContext, i, null);
        }
        RingerModeLayout.this.updateRadioGroup();
      }
      else
      {
        Log.d("RingerModeLayout", "sliding to false");
        RingerModeLayout.this.mRadioGroup.clearCheck();
        RingerModeLayout.access$202(RingerModeLayout.this, 0);
        RingerModeLayout.this.expandSilenceModeContent(false);
        MiuiSettings.SilenceMode.setSilenceMode(RingerModeLayout.this.mContext, 0, null);
        RingerModeLayout.this.mVolumeDialog.recheckAll();
        RingerModeLayout.this.mHandler.removeMessages(1);
        RingerModeLayout.this.mHandler.sendEmptyMessageDelayed(1, 50L);
      }
    }
  };
  private RelativeLayout mSilenceModeContent;
  private LinearLayout mSilenceModeExpandContent;
  public boolean mSilenceModeExpanded;
  private final SilenceModeObserver mSilenceModeObserver = new SilenceModeObserver();
  private TextView mSilenceModeTitle;
  private SlidingButton mSlidingButton;
  private RadioButton mStandardBtn;
  private RelativeLayout mTimeLabel;
  private View.OnClickListener mTimeLabelListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = Integer.parseInt((String)paramAnonymousView.getTag());
      RingerModeLayout.this.mTimeSeekbar.setProgress(i * 25);
      int j = RingerModeLayout.this.progressToMinute(i * 25);
      int k = MiuiSettings.SilenceMode.getZenMode(RingerModeLayout.this.mContext);
      RingerModeLayout.this.mHandler.removeMessages(1);
      ExtraNotificationManager.startCountDownSilenceMode(RingerModeLayout.this.mContext, k, j);
      if (i > 0) {
        RingerModeLayout.this.mHandler.sendEmptyMessageDelayed(1, 50L);
      }
    }
  };
  private List<TextView> mTimeList;
  private SeekBar.OnSeekBarChangeListener mTimeSeekBarChangedListener = new SeekBar.OnSeekBarChangeListener()
  {
    public void onProgressChanged(android.widget.SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      if (RingerModeLayout.this.mTimeLabel.getVisibility() == 0)
      {
        paramAnonymousInt = RingerModeLayout.this.getProgressLevel(paramAnonymousInt);
        if (!((TextView)RingerModeLayout.this.mTimeList.get(paramAnonymousInt)).equals(RingerModeLayout.this.mSelectedText))
        {
          RingerModeLayout.this.mSelectedText.setTextSize(1, 10.0F);
          RingerModeLayout.this.mSelectedText.setTextColor(RingerModeLayout.this.getResources().getColor(285540482));
          paramAnonymousSeekBar = RingerModeLayout.this;
          RingerModeLayout.access$902(paramAnonymousSeekBar, (TextView)paramAnonymousSeekBar.mTimeList.get(paramAnonymousInt));
          RingerModeLayout.this.mSelectedText.setTextSize(1, 12.0F);
          RingerModeLayout.this.mSelectedText.setTextColor(RingerModeLayout.this.getResources().getColor(285540483));
        }
      }
    }
    
    public void onStartTrackingTouch(android.widget.SeekBar paramAnonymousSeekBar)
    {
      RingerModeLayout.this.mTimeLabel.setVisibility(0);
      RingerModeLayout.this.mRemainTime_2.setVisibility(8);
      RingerModeLayout.this.mHandler.removeMessages(1);
    }
    
    public void onStopTrackingTouch(android.widget.SeekBar paramAnonymousSeekBar)
    {
      int i = RingerModeLayout.this.getProgressLevel(paramAnonymousSeekBar.getProgress());
      paramAnonymousSeekBar.setProgress(i * 25);
      i = RingerModeLayout.this.progressToMinute(i * 25);
      int j = MiuiSettings.SilenceMode.getZenMode(RingerModeLayout.this.mContext);
      RingerModeLayout.this.mHandler.removeMessages(1);
      ExtraNotificationManager.startCountDownSilenceMode(RingerModeLayout.this.mContext, j, i);
      if (paramAnonymousSeekBar.getProgress() > 0) {
        RingerModeLayout.this.mHandler.sendEmptyMessageDelayed(1, 50L);
      }
    }
  };
  private miui.widget.SeekBar mTimeSeekbar;
  private RadioButton mTotalBtn;
  private VolumeDialog mVolumeDialog;
  
  public RingerModeLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mContext = paramContext;
    this.mOrignalRemain = ExtraNotificationManager.getRemainTime(this.mContext);
    this.mOrignalMode = ExtraNotificationManager.getZenMode(this.mContext);
  }
  
  private void changeSilenceModeTitle(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      int i;
      if (this.mCurrentMode == 4) {
        i = 286130485;
      } else {
        i = 286130545;
      }
      this.mSilenceModeTitle.setText(i);
    }
    else
    {
      this.mSilenceModeTitle.setText(286130471);
    }
  }
  
  private int getProgressLevel(int paramInt)
  {
    if (paramInt <= 12) {
      paramInt = 0;
    } else if (Math.abs(paramInt - 25) <= 12) {
      paramInt = 1;
    } else if (Math.abs(paramInt - 50) <= 12) {
      paramInt = 2;
    } else if (Math.abs(paramInt - 75) <= 12) {
      paramInt = 3;
    } else {
      paramInt = 4;
    }
    return paramInt;
  }
  
  private int getXPosition(miui.widget.SeekBar paramSeekBar)
  {
    float f1 = this.mRemainTime_2.getPaint().measureText(this.mRemainTime_2.getText().toString());
    float f2 = ((LinearLayout.LayoutParams)paramSeekBar.getLayoutParams()).getMarginStart();
    float f3 = paramSeekBar.getMeasuredWidth() - paramSeekBar.getThumb().getIntrinsicWidth();
    return (int)(paramSeekBar.getProgress() * f3 / paramSeekBar.getMax() + paramSeekBar.getThumb().getIntrinsicWidth() / 2 - f1 / 2.0F + f2);
  }
  
  private boolean isSilenceModeEnabled()
  {
    boolean bool;
    if (this.mCurrentMode > 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private int progressToMinute(int paramInt)
  {
    int i = 0;
    if (paramInt <= 50) {
      i = paramInt / 25 * 30;
    } else if (paramInt <= 75) {
      i = 120;
    } else if (paramInt <= 100) {
      i = 480;
    }
    return i;
  }
  
  private int timeToMinute(long paramLong)
  {
    if (paramLong == 0L) {
      return 0;
    }
    if (paramLong <= 1800000L) {
      return 30;
    }
    if (paramLong <= 3600000L) {
      return 60;
    }
    if (paramLong <= 7200000L) {
      return 120;
    }
    return 480;
  }
  
  private int timeToProgress(long paramLong)
  {
    long l = 0L;
    if (paramLong <= 3600L) {
      l = paramLong / 72L;
    } else if (paramLong <= 7200L) {
      l = (paramLong - 3600L) / 144L + 50L;
    } else if (paramLong <= 28800L) {
      l = (paramLong - 3600L) / 864L + 75L;
    }
    return (int)l;
  }
  
  private String turnMillSecondsToHour(long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = (int)(paramLong / 3600000L);
    int j = (int)(paramLong % 3600000L);
    int k = j / 60000;
    j = j % 60000 / 1000;
    if (i > 0)
    {
      if (i < 10) {
        localStringBuilder.append("0");
      }
      localStringBuilder.append(i);
      localStringBuilder.append(":");
    }
    if (k < 10) {
      localStringBuilder.append("0");
    }
    localStringBuilder.append(k);
    localStringBuilder.append(":");
    if (j < 10) {
      localStringBuilder.append("0");
    }
    localStringBuilder.append(j);
    return localStringBuilder.toString();
  }
  
  private void updateRadioGroup()
  {
    if ((isSilenceModeEnabled()) && (this.mShowing))
    {
      RadioGroup localRadioGroup = this.mRadioGroup;
      int i;
      if (this.mCurrentMode == 4) {
        i = 285802644;
      } else {
        i = 285802645;
      }
      localRadioGroup.check(i);
      changeSilenceModeTitle(true);
    }
  }
  
  private void updateRemainText(boolean paramBoolean)
  {
    if (this.mRemainTextShown == paramBoolean) {
      return;
    }
    Log.d("RingerModeLayout", "updateRemainText...");
    this.mRemainTextShown = paramBoolean;
    float f1;
    float f2;
    if (paramBoolean)
    {
      f1 = 0.0F;
      f2 = 1.0F;
    }
    else
    {
      f1 = 1.0F;
      f2 = 0.0F;
    }
    ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { f1, f2 });
    localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        RingerModeLayout.this.mRemainTime_1.setAlpha(f);
      }
    });
    localValueAnimator.setDuration(300L);
    localValueAnimator.start();
  }
  
  private void updateRemainTimeSeekbar()
  {
    if (!this.mShowing) {
      return;
    }
    long l = ExtraNotificationManager.getRemainTime(this.mContext);
    if (l > 0L)
    {
      this.mTimeLabel.setVisibility(8);
      this.mRemainTime_2.setVisibility(0);
      updateRemainText(this.mSilenceModeExpanded ^ true);
      this.mTimeSeekbar.setProgress(timeToProgress(l / 1000L));
      this.mRemainTime_1.setText(this.mContext.getString(286130430, new Object[] { turnMillSecondsToHour(l) }));
      this.mRemainTime_2.setText(turnMillSecondsToHour(l));
      Object localObject = (LinearLayout.LayoutParams)this.mRemainTime_2.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject).setMarginStart(getXPosition(this.mTimeSeekbar));
      this.mRemainTime_2.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.mHandler.removeMessages(1);
      localObject = this.mHandler;
      ((H)localObject).sendMessageDelayed(((H)localObject).obtainMessage(1), 1000L);
    }
    else
    {
      this.mTimeLabel.setVisibility(0);
      this.mRemainTime_2.setVisibility(8);
      updateRemainText(false);
      this.mTimeSeekbar.setProgress(0);
    }
  }
  
  public void cleanUp()
  {
    this.mHandler.removeMessages(1);
    if (!this.mShowing) {
      return;
    }
    long l = ExtraNotificationManager.getRemainTime(this.mContext);
    int i = ExtraNotificationManager.getZenMode(this.mContext);
    if ((Math.abs(this.mOrignalRemain - l) >= '田') || (this.mOrignalMode != i))
    {
      int j = timeToMinute(l);
      MiuiSettings.SilenceMode.reportRingerModeInfo("silence_DND", MiuiSettings.SilenceMode.MISTAT_RINGERMODE_LIST[i], String.valueOf(j), System.currentTimeMillis());
    }
    this.mShowing = false;
    this.mSilenceModeObserver.unregister();
    this.mTimeSeekbar = null;
    this.mRemainTime_2 = null;
    this.mRadioGroup = null;
    this.mTimeList.clear();
  }
  
  public void expandSilenceModeContent(boolean paramBoolean)
  {
    Object localObject = this.mSilenceModeExpandContent;
    boolean bool;
    if ((localObject != null) && (((LinearLayout)localObject).isAttachedToWindow())) {
      bool = true;
    } else {
      bool = false;
    }
    this.mAnimating = bool;
    changeSilenceModeTitle(isSilenceModeEnabled());
    if ((this.mSilenceModeExpanded != paramBoolean) && (((paramBoolean) && (!this.mSlidingButton.isChecked())) || (this.mAnimating)))
    {
      this.mSilenceModeExpanded = paramBoolean;
      int i;
      int j;
      if (paramBoolean)
      {
        i = 0;
        j = this.ContentHeight;
      }
      else
      {
        i = this.mSilenceModeExpandContent.getHeight();
        j = 0;
      }
      localObject = ValueAnimator.ofInt(new int[] { i, j });
      ((ValueAnimator)localObject).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
          RingerModeLayout.this.mSilenceModeExpandContent.getLayoutParams().height = i;
          RingerModeLayout.this.mSilenceModeExpandContent.requestLayout();
        }
      });
      ((ValueAnimator)localObject).addListener(new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ViewGroup localViewGroup = (ViewGroup)RingerModeLayout.this.mDialogView.getParent();
          paramAnonymousAnimator = (ViewGroup.MarginLayoutParams)localViewGroup.getLayoutParams();
          paramAnonymousAnimator.height = -2;
          localViewGroup.setLayoutParams(paramAnonymousAnimator);
          RingerModeLayout.this.mVolumeDialog.rescheduleTimeout(true);
          RingerModeLayout.access$1502(RingerModeLayout.this, false);
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          paramAnonymousAnimator = (ViewGroup)RingerModeLayout.this.mDialogView.getParent();
          ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramAnonymousAnimator.getLayoutParams();
          localMarginLayoutParams.height = 1000;
          paramAnonymousAnimator.setLayoutParams(localMarginLayoutParams);
        }
      });
      ((ValueAnimator)localObject).setInterpolator(new DecelerateInterpolator());
      ((ValueAnimator)localObject).setDuration(300L);
      ((ValueAnimator)localObject).start();
      this.mHandler.removeMessages(1);
      this.mHandler.sendEmptyMessage(1);
      return;
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Silence mode content is alread ");
    ((StringBuilder)localObject).append(paramBoolean);
    Log.d("RingerModeLayout", ((StringBuilder)localObject).toString());
    this.mAnimating = false;
  }
  
  public void init()
  {
    Log.d("RingerModeLayout", "init...");
    this.mSilenceModeContent = ((RelativeLayout)findViewById(285802673));
    this.mSilenceModeExpandContent = ((LinearLayout)findViewById(285802674));
    this.mSilenceModeTitle = ((TextView)findViewById(285802675));
    this.mTimeSeekbar = ((miui.widget.SeekBar)findViewById(285802696));
    this.mRadioGroup = ((RadioGroup)findViewById(285802643));
    this.mStandardBtn = ((RadioButton)findViewById(285802644));
    this.mTotalBtn = ((RadioButton)findViewById(285802645));
    this.mHelpBtn = ((ImageView)findViewById(285802586));
    this.mSlidingButton = ((SlidingButton)findViewById(285802676));
    this.mRemainTime_1 = ((TextView)findViewById(285802648));
    this.mRemainTime_2 = ((TextView)findViewById(285802649));
    this.mTimeLabel = ((RelativeLayout)findViewById(285802695));
    this.mTimeList = new ArrayList();
    this.mTimeList.add((TextView)findViewById(285802503));
    this.mTimeList.add((TextView)findViewById(285802626));
    this.mTimeList.add((TextView)findViewById(285802707));
    this.mTimeList.add((TextView)findViewById(285802585));
    this.mTimeList.add((TextView)findViewById(285802583));
    Iterator localIterator = this.mTimeList.iterator();
    while (localIterator.hasNext()) {
      ((TextView)localIterator.next()).setOnClickListener(this.mTimeLabelListener);
    }
    this.mSelectedText = ((TextView)this.mTimeList.get(0));
    this.mSelectedText.setTextColor(getResources().getColor(285540483));
    this.mSelectedText.setTextSize(1, 12.0F);
    this.mRadioGroup.measure(0, 0);
    this.mStandardBtn.getLayoutParams().height = this.mRadioGroup.getMeasuredHeight();
    this.mTotalBtn.getLayoutParams().height = this.mRadioGroup.getMeasuredHeight();
    findViewById(285802669).getLayoutParams().height = this.mRadioGroup.getMeasuredHeight();
    this.mSilenceModeExpandContent.measure(0, 0);
    this.ContentHeight = this.mSilenceModeExpandContent.getMeasuredHeight();
    this.mHelpBtn.setOnClickListener(this.mHelpButtonListener);
    this.mShowing = true;
    this.mSilenceModeObserver.register();
    this.mSilenceModeContent.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((!RingerModeLayout.this.mAnimating) && (!RingerModeLayout.this.mVolumeDialog.mExpandAnimating))
        {
          paramAnonymousView = RingerModeLayout.this;
          paramAnonymousView.expandSilenceModeContent(paramAnonymousView.mSilenceModeExpanded ^ true);
          RingerModeLayout.this.mVolumeDialog.setExpandedH(false);
          return;
        }
      }
    });
    this.mCurrentMode = MiuiSettings.SilenceMode.getZenMode(this.mContext);
    this.mSlidingButton.setChecked(isSilenceModeEnabled());
    updateRadioGroup();
    this.mSlidingButton.setOnCheckedChangeListener(this.mSilenceButtonChangedListener);
    this.mTimeSeekbar.setOnSeekBarChangeListener(this.mTimeSeekBarChangedListener);
    this.mTotalBtn.setOnClickListener(this.mRadioButtonListener);
    this.mStandardBtn.setOnClickListener(this.mRadioButtonListener);
    this.mSilenceModeExpandContent.getLayoutParams().height = 0;
    this.mSilenceModeExpanded = false;
    this.mRemainTextShown = false;
    if (isSilenceModeEnabled())
    {
      int i;
      if (this.mCurrentMode == 4) {
        i = 286130485;
      } else {
        i = 286130545;
      }
      this.mSilenceModeTitle.setText(i);
    }
    else
    {
      this.mSilenceModeTitle.setText(286130471);
    }
    updateRemainTimeSeekbar();
  }
  
  public void setLooper(Looper paramLooper)
  {
    this.mLooper = paramLooper;
    this.mHandler = new H(this.mLooper);
  }
  
  public void setParentDialog(ViewGroup paramViewGroup)
  {
    this.mDialogView = paramViewGroup;
  }
  
  public void setVolumeDialog(VolumeDialog paramVolumeDialog)
  {
    this.mVolumeDialog = paramVolumeDialog;
  }
  
  private final class H
    extends Handler
  {
    private static final int UPDATE_EXPAND_CONTENT = 2;
    private static final int UPDATE_TIME = 1;
    
    public H(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (!RingerModeLayout.this.mShowing) {
        return;
      }
      int i = paramMessage.what;
      if (i != 1)
      {
        if (i == 2)
        {
          RingerModeLayout.this.updateRadioGroup();
          RingerModeLayout.this.mSlidingButton.setChecked(RingerModeLayout.this.isSilenceModeEnabled());
          RingerModeLayout.this.mVolumeDialog.recheckAll();
        }
      }
      else {
        RingerModeLayout.this.updateRemainTimeSeekbar();
      }
    }
  }
  
  private final class SilenceModeObserver
    extends ContentObserver
  {
    private final Uri silence_mode = Settings.Global.getUriFor("zen_mode");
    
    public SilenceModeObserver()
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      super.onChange(paramBoolean, paramUri);
      int i = MiuiSettings.SilenceMode.getZenMode(RingerModeLayout.this.mContext);
      paramUri = new StringBuilder();
      paramUri.append("Zenmode changeded ");
      paramUri.append(RingerModeLayout.this.mCurrentMode);
      paramUri.append(" -> ");
      paramUri.append(i);
      Log.i("RingerModeLayout", paramUri.toString());
      RingerModeLayout.access$202(RingerModeLayout.this, i);
      RingerModeLayout.this.mHandler.sendEmptyMessage(2);
    }
    
    public void register()
    {
      RingerModeLayout.this.mContext.getContentResolver().registerContentObserver(this.silence_mode, false, this, -1);
    }
    
    public void unregister()
    {
      RingerModeLayout.this.mContext.getContentResolver().unregisterContentObserver(this);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/view/RingerModeLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */