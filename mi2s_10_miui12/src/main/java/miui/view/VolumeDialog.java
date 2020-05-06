package miui.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.MiuiSettings.SilenceMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.util.AudioManagerHelper;
import miui.util.CustomizeUtil;

public class VolumeDialog
{
  private static final int LAYOUT_TRANSITION_ANIMATION_DURATION = 200;
  private static final String STREAM_DEVICES_CHANGED_ACTION = "android.media.STREAM_DEVICES_CHANGED_ACTION";
  private static final String STREAM_MUTE_CHANGED_ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION";
  private static final String TAG = "VolumeDialog";
  private static final int TYPE_FM = 10;
  private static final int UPDATE_ANIMATION_DURATION = 80;
  private static final long USER_ATTEMPT_GRACE_PERIOD = 1000L;
  private static final int VIBRATE_DELAY = 300;
  private static final int VOLUME_ICON_TYPE_ALARM = 0;
  private static final int VOLUME_ICON_TYPE_BLUETOOTH = 1;
  private static final int VOLUME_ICON_TYPE_FM = 8;
  private static final int VOLUME_ICON_TYPE_HEADSET = 2;
  private static final int VOLUME_ICON_TYPE_HIFI = 7;
  private static final int VOLUME_ICON_TYPE_MEDIA = 3;
  private static final int VOLUME_ICON_TYPE_PHONE = 4;
  private static final int VOLUME_ICON_TYPE_RING = 5;
  private static final int VOLUME_ICON_TYPE_SPEAKER = 6;
  private static final Map<Integer, VolumeIconRes> sVolumeIconTypeMap = new HashMap();
  static VolumeSeekbarProgress sVolumeSeekbarProgress = new VolumeSeekbarProgress(285671948, 285671950, null);
  private final int[] STREAM_VOLUME_ALIAS_DEFAULT = { 0, 2, 2, 3, 4, 2, 6, 2, 2, 3, 10 };
  private int mActiveStream;
  private final AudioManager mAm;
  private BroadcastReceiver mBootBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.media.RINGER_MODE_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        int i = paramAnonymousIntent.getIntExtra("android.media.EXTRA_RINGER_MODE", -1);
        if (VolumeDialog.this.mRingerMode != i)
        {
          if ((VolumeDialog.this.mRingerMode != -1) && (i == 1)) {
            VolumeDialog.this.mHandler.sendMessageDelayed(VolumeDialog.this.mHandler.obtainMessage(10), 300L);
          }
          VolumeDialog.access$202(VolumeDialog.this, i);
        }
      }
    }
  };
  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if ((!"android.intent.action.SCREEN_OFF".equals(paramAnonymousContext)) && (!"android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(paramAnonymousContext)))
      {
        if ("miui.intent.TAKE_SCREENSHOT".equals(paramAnonymousContext))
        {
          if (!paramAnonymousIntent.getBooleanExtra("IsFinished", true))
          {
            VolumeDialog.access$4502(VolumeDialog.this, true);
            VolumeDialog.this.mHandler.sendEmptyMessageDelayed(11, 500L);
            if (SystemClock.uptimeMillis() - VolumeDialog.this.mDialogShowTime < 500L) {
              VolumeDialog.this.dismiss();
            }
          }
          else
          {
            VolumeDialog.this.mHandler.removeMessages(11);
            VolumeDialog.access$4502(VolumeDialog.this, false);
          }
        }
        else if ("android.media.STREAM_DEVICES_CHANGED_ACTION".equals(paramAnonymousContext))
        {
          int i = paramAnonymousIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
          VolumeDialog.this.streamDeviceChanged(i);
          return;
        }
      }
      else {
        VolumeDialog.this.dismiss();
      }
    }
  };
  private final View.OnClickListener mClickExpand = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (VolumeDialog.this.mExpandAnimating) {
        return;
      }
      paramAnonymousView = VolumeDialog.this;
      boolean bool1 = paramAnonymousView.mExpanded;
      boolean bool2 = true;
      paramAnonymousView.setExpandedH(bool1 ^ true);
      if (VolumeDialog.this.mExpanded == true)
      {
        paramAnonymousView = VolumeDialog.this;
        VolumeDialog.access$3402(paramAnonymousView, paramAnonymousView.mRingerModeLayout.mSilenceModeExpanded);
      }
      paramAnonymousView = VolumeDialog.this.mRingerModeLayout;
      if ((VolumeDialog.this.mExpanded) || (!VolumeDialog.this.mLastStatus)) {
        bool2 = false;
      }
      paramAnonymousView.expandSilenceModeContent(bool2);
    }
  };
  private long mCollapseTime;
  private final Context mContext;
  private VolumePanelDelegate mDelegate;
  private CustomDialog mDialog;
  private ViewGroup mDialogContentView;
  private long mDialogShowTime = -1L;
  private ViewGroup mDialogView;
  public boolean mExpandAnimating;
  private ValueAnimator mExpandAnimator;
  private ImageButton mExpandButton;
  public boolean mExpanded;
  private final H mHandler = new H();
  private boolean mInScreenshot = false;
  private boolean mLastStatus;
  private LayoutTransition mLayoutTransition;
  private int mRingerMode = -1;
  private RingerModeLayout mRingerModeLayout;
  private final List<VolumeRow> mRows = new ArrayList();
  private AlertDialog mSafetyWarning;
  private final Object mSafetyWarningLock = new Object();
  private boolean mShowing;
  private int mStatusBarHeight = -1;
  private final Vibrator mVibrator;
  private final List<View> mVolumeRowSpaces = new ArrayList();
  private final List<View> mVolumeRowViews = new ArrayList();
  
  static
  {
    sVolumeIconTypeMap.put(Integer.valueOf(0), new VolumeIconRes(285671546, 285671547, 285671545, null));
    sVolumeIconTypeMap.put(Integer.valueOf(1), new VolumeIconRes(285671551, 285671552, 285671550, null));
    sVolumeIconTypeMap.put(Integer.valueOf(2), new VolumeIconRes(285671559, 285671560, 285671558, null));
    sVolumeIconTypeMap.put(Integer.valueOf(3), new VolumeIconRes(285671567, 285671568, 285671566, null));
    sVolumeIconTypeMap.put(Integer.valueOf(4), new VolumeIconRes(285671573, 285671574, 285671572, null));
    sVolumeIconTypeMap.put(Integer.valueOf(5), new VolumeIconRes(285671577, 285671581, 285671576, null));
    sVolumeIconTypeMap.put(Integer.valueOf(6), new VolumeIconRes(285671584, 285671585, 285671583, null));
    Map localMap = sVolumeIconTypeMap;
    VolumeIconRes localVolumeIconRes;
    if ((!"scorpio".equals(Build.DEVICE)) && (!"lithium".equals(Build.DEVICE))) {
      localVolumeIconRes = new VolumeIconRes(285671563, 285671564, 285671562, null);
    } else {
      localVolumeIconRes = new VolumeIconRes(285671567, 285671568, 285671566, null);
    }
    localMap.put(Integer.valueOf(7), localVolumeIconRes);
    sVolumeIconTypeMap.put(Integer.valueOf(8), new VolumeIconRes(285671567, 285671568, 285671566, null));
  }
  
  public VolumeDialog(Context paramContext, VolumePanelDelegate paramVolumePanelDelegate)
  {
    this.mContext = paramContext;
    this.mDelegate = paramVolumePanelDelegate;
    this.mVibrator = ((Vibrator)this.mContext.getSystemService("vibrator"));
    this.mAm = ((AudioManager)this.mContext.getSystemService("audio"));
    paramContext = new IntentFilter();
    paramContext.addAction("android.media.RINGER_MODE_CHANGED");
    this.mContext.registerReceiverAsUser(this.mBootBroadcastReceiver, UserHandle.ALL, paramContext, null, null);
  }
  
  private void addRow(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    VolumeRow localVolumeRow = initRow(paramInt1, paramInt2, paramBoolean);
    if (!this.mRows.isEmpty())
    {
      View localView = new View(this.mContext);
      localView.setId(16908288);
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-1, this.mContext.getResources().getDimensionPixelSize(285606126));
      this.mDialogContentView.addView(localView, localLayoutParams);
      this.mVolumeRowSpaces.add(localView);
      VolumeRow.access$402(localVolumeRow, localView);
    }
    this.mDialogContentView.addView(localVolumeRow.view);
    this.mVolumeRowViews.add(localVolumeRow.view);
    this.mRows.add(localVolumeRow);
  }
  
  private void adjustDialogPosition()
  {
    if (CustomizeUtil.HAS_NOTCH)
    {
      Window localWindow = this.mDialog.getWindow();
      WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
      int i;
      if (this.mContext.getResources().getConfiguration().orientation == 1) {
        i = getStatusBarHeight();
      } else {
        i = 0;
      }
      localLayoutParams.y = i;
      localWindow.setAttributes(localLayoutParams);
    }
  }
  
  private int computeTimeoutH()
  {
    if ((!this.mExpanded) && (!this.mExpandAnimating))
    {
      RingerModeLayout localRingerModeLayout = this.mRingerModeLayout;
      if ((localRingerModeLayout == null) || (!localRingerModeLayout.mSilenceModeExpanded)) {
        return 3000;
      }
    }
    return 6000;
  }
  
  private void dismissH()
  {
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(1);
    if (!this.mShowing) {
      return;
    }
    this.mShowing = false;
    synchronized (this.mSafetyWarningLock)
    {
      if (this.mSafetyWarning != null) {
        this.mSafetyWarning.dismiss();
      }
      this.mDialog.dismiss();
      this.mExpandAnimator.cancel();
      this.mDelegate.notifyVolumeControllerVisible(false);
      this.mRingerModeLayout.cleanUp();
      this.mRingerModeLayout = null;
      this.mExpandAnimator = null;
      this.mDialog = null;
      this.mDialogContentView = null;
      this.mDialogView = null;
      this.mExpandButton = null;
      this.mRows.clear();
      this.mVolumeRowViews.clear();
      this.mVolumeRowSpaces.clear();
      return;
    }
  }
  
  private void expandVolumeBar(final boolean paramBoolean)
  {
    this.mExpandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        int i = ((Integer)paramAnonymousValueAnimator.getAnimatedValue()).intValue();
        VolumeDialog.this.mDialogContentView.getLayoutParams().height = i;
        VolumeDialog.this.mDialogContentView.requestLayout();
      }
    });
    this.mExpandAnimator.addListener(new Animator.AnimatorListener()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator) {}
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if (!paramBoolean) {
          VolumeDialog.this.updateRowsVisibilityByExpandH();
        }
        VolumeDialog.this.mDialogContentView.getLayoutParams().height = -2;
        VolumeDialog.this.mDialogContentView.requestLayout();
        ViewGroup localViewGroup = (ViewGroup)VolumeDialog.this.mDialogView.getParent();
        paramAnonymousAnimator = (ViewGroup.MarginLayoutParams)localViewGroup.getLayoutParams();
        paramAnonymousAnimator.height = -2;
        localViewGroup.setLayoutParams(paramAnonymousAnimator);
      }
      
      public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
      
      public void onAnimationStart(Animator paramAnonymousAnimator)
      {
        if (paramBoolean) {
          VolumeDialog.this.updateRowsVisibilityByExpandH();
        }
        ViewGroup localViewGroup = (ViewGroup)VolumeDialog.this.mDialogView.getParent();
        paramAnonymousAnimator = (ViewGroup.MarginLayoutParams)localViewGroup.getLayoutParams();
        paramAnonymousAnimator.height = 1000;
        localViewGroup.setLayoutParams(paramAnonymousAnimator);
        VolumeDialog.this.mDialogContentView.getLayoutParams().height = -2;
        VolumeDialog.this.mDialogContentView.measure(0, 0);
        VolumeDialog.VolumeRow.access$500((VolumeDialog.VolumeRow)VolumeDialog.this.mRows.get(0)).measure(0, 0);
        int i;
        if (paramBoolean) {
          i = VolumeDialog.this.mDialogContentView.getMeasuredHeight();
        } else {
          i = VolumeDialog.VolumeRow.access$500((VolumeDialog.VolumeRow)VolumeDialog.this.mRows.get(0)).getMeasuredHeight();
        }
        VolumeDialog.this.mExpandAnimator.setIntValues(new int[] { VolumeDialog.this.mDialogContentView.getHeight(), i });
      }
    });
    this.mExpandAnimator.setInterpolator(new DecelerateInterpolator());
    this.mExpandAnimator.setDuration(300L);
    this.mExpandAnimator.start();
  }
  
  private VolumeRow findRow(int paramInt)
  {
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      if (localVolumeRow.stream == paramInt) {
        return localVolumeRow;
      }
    }
    return null;
  }
  
  private int getConservativeCollapseDuration()
  {
    return 600;
  }
  
  private static int getImpliedLevel(android.widget.SeekBar paramSeekBar, int paramInt)
  {
    int i = paramSeekBar.getMax();
    int j = i / 100;
    if (paramInt == 0) {
      paramInt = 0;
    } else if (paramInt == i) {
      paramInt = i / 100;
    } else {
      paramInt = (int)(paramInt / i * (j - 1)) + 1;
    }
    return paramInt;
  }
  
  private int getMappedStream(int paramInt)
  {
    int[] arrayOfInt = this.STREAM_VOLUME_ALIAS_DEFAULT;
    if (paramInt >= arrayOfInt.length) {
      return 3;
    }
    return arrayOfInt[paramInt];
  }
  
  private int getStatusBarHeight()
  {
    if (this.mStatusBarHeight < 0) {
      this.mStatusBarHeight = this.mContext.getResources().getDimensionPixelSize(17105467);
    }
    return this.mStatusBarHeight;
  }
  
  private VolumeRow initRow(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    final VolumeRow localVolumeRow = new VolumeRow(null);
    VolumeRow.access$702(localVolumeRow, paramInt1);
    VolumeRow.access$802(localVolumeRow, paramInt2);
    VolumeRow.access$902(localVolumeRow, paramInt2);
    VolumeRow.access$1002(localVolumeRow, paramBoolean);
    VolumeRow.access$502(localVolumeRow, this.mDialog.getLayoutInflater().inflate(285933636, null));
    localVolumeRow.view.setTag(localVolumeRow);
    VolumeRow.access$1102(localVolumeRow, (miui.widget.SeekBar)localVolumeRow.view.findViewById(285802717));
    localVolumeRow.slider.setTag(localVolumeRow);
    localVolumeRow.slider.setOnSeekBarChangeListener(new VolumeSeekBarChangeListener(null));
    localVolumeRow.view.setOnTouchListener(new View.OnTouchListener()
    {
      private boolean dragging;
      private final Rect sliderHitRect = new Rect();
      
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        VolumeDialog.VolumeRow.access$1100(localVolumeRow).getHitRect(this.sliderHitRect);
        if ((!this.dragging) && (paramAnonymousMotionEvent.getActionMasked() == 0) && (paramAnonymousMotionEvent.getY() < this.sliderHitRect.top)) {
          this.dragging = true;
        }
        if (this.dragging)
        {
          paramAnonymousMotionEvent.offsetLocation(-this.sliderHitRect.left, -this.sliderHitRect.top);
          VolumeDialog.VolumeRow.access$1100(localVolumeRow).dispatchTouchEvent(paramAnonymousMotionEvent);
          if ((paramAnonymousMotionEvent.getActionMasked() == 1) || (paramAnonymousMotionEvent.getActionMasked() == 3)) {
            this.dragging = false;
          }
          return true;
        }
        return false;
      }
    });
    VolumeRow.access$1302(localVolumeRow, (ImageButton)localVolumeRow.view.findViewById(285802716));
    localVolumeRow.icon.setImageResource(((VolumeIconRes)sVolumeIconTypeMap.get(Integer.valueOf(paramInt2))).normalIconRes);
    return localVolumeRow;
  }
  
  private boolean isAttached()
  {
    ViewGroup localViewGroup = this.mDialogContentView;
    boolean bool;
    if ((localViewGroup != null) && (localViewGroup.isAttachedToWindow())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void orderVolumeRowsH()
  {
    int i = 0;
    int j = 0;
    while (j < this.mVolumeRowViews.size())
    {
      View localView = (View)this.mVolumeRowViews.get(j);
      Object localObject1 = null;
      int k = 1;
      if (j == 0)
      {
        localObject2 = findRow(this.mActiveStream);
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          Log.d("VolumeDialog", "terrible thing happens in orderVolumeRowsH");
          localObject1 = localObject2;
        }
      }
      int m = i;
      Object localObject2 = localObject1;
      int n = k;
      if (localObject1 == null)
      {
        localObject1 = this.mRows;
        m = i + 1;
        localObject1 = (VolumeRow)((List)localObject1).get(i);
        if (((VolumeRow)localObject1).stream == this.mActiveStream)
        {
          localObject1 = this.mRows;
          n = m + 1;
          localObject1 = (VolumeRow)((List)localObject1).get(m);
          m = n;
        }
        if (!((VolumeRow)localObject1).important)
        {
          n = 0;
          localObject2 = localObject1;
        }
        else
        {
          n = k;
          localObject2 = localObject1;
        }
      }
      i = 8;
      if (n != 0) {
        localView.setVisibility(0);
      } else {
        localView.setVisibility(8);
      }
      VolumeRow.access$502((VolumeRow)localObject2, localView);
      VolumeRow.access$1402((VolumeRow)localObject2, 0);
      VolumeRow.access$1302((VolumeRow)localObject2, (ImageButton)localView.findViewById(285802716));
      ((VolumeRow)localObject2).icon.setImageResource(((VolumeIconRes)sVolumeIconTypeMap.get(Integer.valueOf(((VolumeRow)localObject2).initIconsMapKey))).normalIconRes);
      VolumeRow.access$1102((VolumeRow)localObject2, (miui.widget.SeekBar)localView.findViewById(285802717));
      ((VolumeRow)localObject2).view.setTag(localObject2);
      ((VolumeRow)localObject2).slider.setTag(localObject2);
      if (j > 0)
      {
        VolumeRow.access$402((VolumeRow)localObject2, (View)this.mVolumeRowSpaces.get(j - 1));
        localObject1 = (View)this.mVolumeRowSpaces.get(j - 1);
        if (n != 0) {
          i = 0;
        }
        ((View)localObject1).setVisibility(i);
      }
      else
      {
        VolumeRow.access$402((VolumeRow)localObject2, null);
      }
      j++;
      i = m;
    }
  }
  
  private void prepareForCollapse()
  {
    this.mHandler.removeMessages(7);
    this.mCollapseTime = System.currentTimeMillis();
    updateDialogBottomMarginH();
    this.mHandler.sendEmptyMessageDelayed(7, getConservativeCollapseDuration());
  }
  
  private void recheckH(VolumeRow paramVolumeRow)
  {
    if (paramVolumeRow == null)
    {
      paramVolumeRow = this.mRows.iterator();
      while (paramVolumeRow.hasNext()) {
        updateVolumeRowH((VolumeRow)paramVolumeRow.next());
      }
    }
    else
    {
      updateVolumeRowH(paramVolumeRow);
    }
  }
  
  private void rescheduleTimeoutH()
  {
    this.mHandler.removeMessages(2);
    int i = computeTimeoutH();
    H localH = this.mHandler;
    localH.sendMessageDelayed(localH.obtainMessage(2), i);
  }
  
  private void showH(int paramInt)
  {
    if (this.mRows.size() == 0)
    {
      this.mDialog = new CustomDialog(this.mContext);
      localObject = this.mDialog.getWindow();
      ((Window)localObject).requestFeature(1);
      ((Window)localObject).setBackgroundDrawable(new ColorDrawable(0));
      ((Window)localObject).clearFlags(2);
      ((Window)localObject).addFlags(17039656);
      this.mDialog.setCanceledOnTouchOutside(true);
      this.mDialog.setContentView(285933635);
      WindowManager.LayoutParams localLayoutParams = ((Window)localObject).getAttributes();
      localLayoutParams.height = -2;
      localLayoutParams.width = -1;
      localLayoutParams.type = 2020;
      localLayoutParams.format = -3;
      localLayoutParams.setTitle("Volume Control");
      localLayoutParams.gravity = 48;
      localLayoutParams.windowAnimations = 286195717;
      ((Window)localObject).setAttributes(localLayoutParams);
      ((Window)localObject).setSoftInputMode(48);
      this.mDialogView = ((ViewGroup)this.mDialog.findViewById(285802712));
      this.mDialogContentView = ((ViewGroup)this.mDialog.findViewById(285802713));
      this.mExpandButton = ((ImageButton)this.mDialogView.findViewById(285802715));
      this.mExpandButton.setOnClickListener(this.mClickExpand);
      this.mRingerModeLayout = ((RingerModeLayout)this.mDialog.findViewById(285802659));
      this.mExpandAnimator = ValueAnimator.ofInt(new int[] { 0, 0 });
      this.mLayoutTransition = new LayoutTransition();
      this.mLayoutTransition.setDuration(200L);
      addRow(2, 5, true);
      addRow(3, 3, true);
      addRow(4, 0, true);
      addRow(0, 4, false);
      addRow(6, 4, false);
      if (AudioSystem.getNumStreamTypes() > 10) {
        addRow(10, 8, false);
      }
    }
    this.mDialogView.setBackgroundResource(0);
    this.mDialogView.setBackgroundResource(285671946);
    Object localObject = this.mDialogView.getLayoutParams();
    ((ViewGroup.LayoutParams)localObject).width = this.mContext.getResources().getDimensionPixelSize(285606124);
    if (((ViewGroup.LayoutParams)localObject).width == 0) {
      ((ViewGroup.LayoutParams)localObject).width = -1;
    }
    this.mDialogView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.mLastStatus = false;
    if (!this.mShowing)
    {
      if ((this.mAm.getMode() == 2) || (!MiuiSettings.SilenceMode.isSupported)) {
        this.mRingerModeLayout.setVisibility(8);
      }
      this.mRingerModeLayout.setVolumeDialog(this);
      this.mRingerModeLayout.setLooper(Looper.getMainLooper());
      this.mRingerModeLayout.setParentDialog(this.mDialogView);
      this.mRingerModeLayout.init();
    }
    adjustDialogPosition();
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("showH ");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(" ");
    ((StringBuilder)localObject).append(this.mActiveStream);
    ((StringBuilder)localObject).append(" ");
    ((StringBuilder)localObject).append(this.mShowing);
    Log.d("VolumeDialog", ((StringBuilder)localObject).toString());
    paramInt = getMappedStream(paramInt);
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    if ((!this.mShowing) || (this.mActiveStream != paramInt))
    {
      this.mActiveStream = paramInt;
      orderVolumeRowsH();
    }
    rescheduleTimeoutH();
    updateVolumeRowsH();
    if ((!this.mInScreenshot) && (!this.mShowing))
    {
      this.mExpanded = false;
      this.mExpandAnimating = false;
      updateExpandButtonH();
      updateRowsVisibilityByExpandH();
      this.mShowing = true;
      this.mDialogShowTime = SystemClock.uptimeMillis();
      this.mDialog.show();
      this.mDelegate.notifyVolumeControllerVisible(true);
      return;
    }
    if (this.mShowing)
    {
      updateExpandButtonH();
      updateRowsVisibilityByExpandH();
    }
  }
  
  private void showSafetyWarningH(int paramInt)
  {
    if ((this.mDelegate.showSafeVolumeDialogByFlags(paramInt)) || (this.mShowing)) {}
    synchronized (this.mSafetyWarningLock)
    {
      if (this.mSafetyWarning != null) {
        return;
      }
      Object localObject2 = new miui/view/VolumeDialog$SafetyWarningDialog;
      ((SafetyWarningDialog)localObject2).<init>(this, this.mContext);
      this.mSafetyWarning = ((AlertDialog)localObject2);
      this.mSafetyWarning.show();
      localObject2 = this.mSafetyWarning;
      DialogInterface.OnDismissListener local6 = new miui/view/VolumeDialog$6;
      local6.<init>(this);
      ((AlertDialog)localObject2).setOnDismissListener(local6);
      rescheduleTimeoutH();
      return;
    }
  }
  
  private void stateChangedH(int paramInt1, int paramInt2)
  {
    Object localObject = findRow(getMappedStream(paramInt1));
    if (localObject != null)
    {
      updateVolumeRowH((VolumeRow)localObject);
    }
    else
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("stateChangedH can not find volume row for stream ");
      ((StringBuilder)localObject).append(paramInt1);
      Log.e("VolumeDialog", ((StringBuilder)localObject).toString());
    }
  }
  
  private void streamDeviceChanged(int paramInt)
  {
    this.mHandler.obtainMessage(3, findRow(paramInt)).sendToTarget();
  }
  
  private void updateDialogBottomMarginH()
  {
    if (this.mDialogView == null) {
      return;
    }
    long l1 = System.currentTimeMillis();
    long l2 = this.mCollapseTime;
    int i;
    if ((l2 != 0L) && (l1 - l2 < getConservativeCollapseDuration())) {
      i = 1;
    } else {
      i = 0;
    }
    ViewGroup localViewGroup = (ViewGroup)this.mDialogView.getParent();
    ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localViewGroup.getLayoutParams();
    if (i != 0) {
      i = localViewGroup.getHeight();
    } else {
      i = -2;
    }
    localMarginLayoutParams.height = i;
    localViewGroup.setLayoutParams(localMarginLayoutParams);
  }
  
  private void updateExpandButtonH()
  {
    ImageButton localImageButton = this.mExpandButton;
    if (localImageButton == null) {
      return;
    }
    int i;
    if (this.mActiveStream == 0) {
      i = 4;
    } else {
      i = 0;
    }
    localImageButton.setVisibility(i);
    localImageButton = this.mExpandButton;
    if (this.mExpanded) {
      i = 285671945;
    } else {
      i = 285671947;
    }
    localImageButton.setImageResource(i);
    this.mExpandButton.setClickable(this.mExpandAnimating ^ true);
  }
  
  private void updateLayoutDirectionH(int paramInt)
  {
    ViewGroup localViewGroup = this.mDialogView;
    if (localViewGroup != null) {
      localViewGroup.setLayoutDirection(paramInt);
    }
  }
  
  private void updateRowsVisibilityByExpandH()
  {
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext())
    {
      VolumeRow localVolumeRow = (VolumeRow)localIterator.next();
      int i = 8;
      if (localVolumeRow.stream == this.mActiveStream) {
        i = 0;
      } else if (localVolumeRow.important) {
        if (this.mExpanded) {
          i = 0;
        } else {
          i = 8;
        }
      }
      localVolumeRow.view.setVisibility(i);
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("updateRowsVisibilityByExpandH ");
      localStringBuilder.append(localVolumeRow.stream);
      localStringBuilder.append(" ");
      localStringBuilder.append(i);
      localStringBuilder.append(" ");
      localStringBuilder.append(Integer.toHexString(System.identityHashCode(localVolumeRow.view)));
      Log.d("VolumeDialog", localStringBuilder.toString());
      if (localVolumeRow.space != null) {
        localVolumeRow.space.setVisibility(i);
      }
    }
  }
  
  private void updateVolumeRowH(VolumeRow paramVolumeRow)
  {
    Object localObject = StreamState.getStreamStateByStreamType(this.mContext, paramVolumeRow.stream, this.mDelegate);
    if (localObject == null) {
      return;
    }
    VolumeRow.access$2102(paramVolumeRow, (StreamState)localObject);
    if (((StreamState)localObject).level > 0) {
      VolumeRow.access$2302(paramVolumeRow, ((StreamState)localObject).level);
    }
    int i = paramVolumeRow.stream;
    int j = 0;
    if (i == 2) {
      i = 1;
    } else {
      i = 0;
    }
    if (((i == 0) || (this.mDelegate.getRingerMode() == 1)) || ((i != 0) && (this.mDelegate.getRingerMode() != 0))) {}
    i = ((StreamState)localObject).levelMax * 100;
    if (i != paramVolumeRow.slider.getMax()) {
      paramVolumeRow.slider.setMax(i);
    }
    int k = paramVolumeRow.initIconsMapKey;
    i = k;
    int m;
    if (paramVolumeRow.stream == this.mActiveStream)
    {
      i = this.mAm.getDevicesForStream(paramVolumeRow.stream);
      m = k;
      if (paramVolumeRow.stream == 0)
      {
        m = k;
        if (this.mAm.isSpeakerphoneOn()) {
          m = 6;
        }
      }
      if (((i & 0x4) != 0) || ((i & 0x8) != 0)) {
        m = 2;
      }
      i = m;
      if (paramVolumeRow.stream == 3)
      {
        i = m;
        if (AudioManagerHelper.isHiFiMode(this.mContext)) {
          i = 7;
        }
      }
    }
    VolumeRow.access$802(paramVolumeRow, i);
    if (((AudioManager)this.mContext.getSystemService("audio")).getStreamVolume(paramVolumeRow.stream) == 0) {
      m = 1;
    } else {
      m = 0;
    }
    k = m;
    if (Build.VERSION.SDK_INT < 23) {
      if (paramVolumeRow.stream != 6)
      {
        k = m;
        if (paramVolumeRow.stream != 0) {}
      }
      else
      {
        m = j;
        if (((StreamState)localObject).muted)
        {
          m = j;
          if (((StreamState)localObject).muteSupported) {
            m = 1;
          }
        }
        k = m;
      }
    }
    localObject = (VolumeIconRes)sVolumeIconTypeMap.get(Integer.valueOf(i));
    if (k != 0) {
      i = ((VolumeIconRes)localObject).mutedIconRes;
    } else {
      i = ((VolumeIconRes)localObject).normalIconRes;
    }
    if (i != paramVolumeRow.cachedIconRes)
    {
      VolumeRow.access$1402(paramVolumeRow, i);
      paramVolumeRow.icon.setImageResource(i);
    }
    localObject = sVolumeSeekbarProgress;
    if (k != 0) {
      i = ((VolumeSeekbarProgress)localObject).silentProgress;
    } else {
      i = ((VolumeSeekbarProgress)localObject).normalProgress;
    }
    if (i != paramVolumeRow.cachedProgressRes)
    {
      VolumeRow.access$2702(paramVolumeRow, i);
      paramVolumeRow.slider.setProgressDrawable(paramVolumeRow.slider.getResources().getDrawable(i));
    }
    if (k != 0) {
      i = this.mAm.getLastAudibleStreamVolume(paramVolumeRow.stream);
    } else {
      i = VolumeRow.access$2100(paramVolumeRow).level;
    }
    updateVolumeRowSliderH(paramVolumeRow, i);
  }
  
  private void updateVolumeRowSliderH(VolumeRow paramVolumeRow, int paramInt)
  {
    if (paramVolumeRow.tracking) {
      return;
    }
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("updateVolumeRowSliderH start ");
    ((StringBuilder)localObject).append(paramVolumeRow.stream);
    ((StringBuilder)localObject).append(" ");
    ((StringBuilder)localObject).append(paramInt);
    Log.d("VolumeDialog", ((StringBuilder)localObject).toString());
    int i = paramVolumeRow.slider.getProgress();
    int j = getImpliedLevel(paramVolumeRow.slider, i);
    int k;
    if (paramVolumeRow.view.getVisibility() == 0) {
      k = 1;
    } else {
      k = 0;
    }
    if (SystemClock.uptimeMillis() - paramVolumeRow.userAttempt < 1000L) {
      m = 1;
    } else {
      m = 0;
    }
    this.mHandler.removeMessages(3, paramVolumeRow);
    if ((this.mShowing) && (k != 0) && (m != 0))
    {
      localObject = this.mHandler;
      ((H)localObject).sendMessageAtTime(((H)localObject).obtainMessage(3, paramVolumeRow), paramVolumeRow.userAttempt + 1000L);
      return;
    }
    if ((paramInt == j) && (this.mShowing) && (k != 0)) {
      return;
    }
    int m = paramInt * 100;
    if (i != m) {
      if ((this.mShowing) && (k != 0))
      {
        if ((paramVolumeRow.anim != null) && (paramVolumeRow.anim.isRunning()) && (paramVolumeRow.animTargetProgress == m)) {
          return;
        }
        k = i;
        paramInt = k;
        if (paramVolumeRow.anim != null)
        {
          paramInt = k;
          if (paramVolumeRow.anim.isRunning())
          {
            localObject = new StringBuilder();
            ((StringBuilder)localObject).append("updateVolumeRowSliderH cancel animation: ");
            ((StringBuilder)localObject).append(paramVolumeRow.stream);
            Log.d("VolumeDialog", ((StringBuilder)localObject).toString());
            paramVolumeRow.anim.cancel();
            paramInt = paramVolumeRow.animTargetProgress;
          }
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("updateVolumeRowSliderH animation: ");
        ((StringBuilder)localObject).append(paramInt);
        ((StringBuilder)localObject).append(" ");
        ((StringBuilder)localObject).append(m);
        Log.d("VolumeDialog", ((StringBuilder)localObject).toString());
        VolumeRow.access$3002(paramVolumeRow, ObjectAnimator.ofInt(paramVolumeRow.slider, "progress", new int[] { paramInt, m }));
        paramVolumeRow.anim.setInterpolator(null);
        VolumeRow.access$3102(paramVolumeRow, m);
        paramVolumeRow.anim.setDuration(80L);
        paramVolumeRow.anim.start();
      }
      else
      {
        if (paramVolumeRow.anim != null) {
          paramVolumeRow.anim.cancel();
        }
        paramVolumeRow.slider.setProgress(m);
      }
    }
  }
  
  private void updateVolumeRowsH()
  {
    Iterator localIterator = this.mRows.iterator();
    while (localIterator.hasNext()) {
      updateVolumeRowH((VolumeRow)localIterator.next());
    }
  }
  
  private void vibrateH()
  {
    ((Vibrator)this.mContext.getSystemService("vibrator")).vibrate(300L);
  }
  
  public void dismiss()
  {
    this.mHandler.obtainMessage(2).sendToTarget();
  }
  
  public void dismiss(long paramLong)
  {
    this.mHandler.sendEmptyMessageDelayed(2, paramLong);
  }
  
  public boolean isShowing()
  {
    return this.mShowing;
  }
  
  public void masterMuteChanged(int paramInt) {}
  
  public void masterVolumeChanged(int paramInt) {}
  
  public void recheckAll()
  {
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void rescheduleTimeout(boolean paramBoolean)
  {
    this.mHandler.removeMessages(2);
    this.mHandler.removeMessages(5);
    if (paramBoolean) {
      this.mHandler.sendEmptyMessage(5);
    }
  }
  
  public void setExpandedH(boolean paramBoolean)
  {
    if (this.mExpanded == paramBoolean) {
      return;
    }
    this.mExpanded = paramBoolean;
    this.mExpandAnimating = isAttached();
    if (this.mExpandAnimating)
    {
      updateExpandButtonH();
      expandVolumeBar(paramBoolean);
    }
    this.mHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        VolumeDialog localVolumeDialog = VolumeDialog.this;
        localVolumeDialog.mExpandAnimating = false;
        localVolumeDialog.updateExpandButtonH();
      }
    }, getConservativeCollapseDuration());
    rescheduleTimeoutH();
  }
  
  public void show(int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("show ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(" ");
    localStringBuilder.append(paramInt2);
    Log.d("VolumeDialog", localStringBuilder.toString());
    this.mHandler.obtainMessage(1, paramInt1, paramInt2).sendToTarget();
  }
  
  public void showSafeWarningDialog(int paramInt)
  {
    this.mHandler.obtainMessage(9, paramInt, 0).sendToTarget();
  }
  
  public void stateChanged(int paramInt)
  {
    stateChanged(paramInt, this.mAm.getStreamVolume(paramInt));
  }
  
  public void stateChanged(int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("stateChanged ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(" ");
    localStringBuilder.append(paramInt2);
    Log.d("VolumeDialog", localStringBuilder.toString());
    this.mHandler.obtainMessage(6, paramInt1, paramInt2).sendToTarget();
  }
  
  public void updateLayoutDirection(int paramInt)
  {
    this.mHandler.obtainMessage(8, paramInt, 0).sendToTarget();
  }
  
  private final class CustomDialog
    extends Dialog
  {
    private float mDownX;
    private float mDownY;
    
    public CustomDialog(Context paramContext)
    {
      super();
    }
    
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
      Rect localRect = new Rect();
      if (VolumeDialog.this.mDialogView != null) {
        VolumeDialog.this.mDialogView.getHitRect(localRect);
      }
      if ((paramMotionEvent.getAction() == 0) && (!localRect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))) {
        VolumeDialog.this.dismissH();
      } else {
        VolumeDialog.this.rescheduleTimeoutH();
      }
      return super.dispatchTouchEvent(paramMotionEvent);
    }
    
    protected void onStart()
    {
      super.onStart();
      IntentFilter localIntentFilter = new IntentFilter();
      localIntentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
      localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
      localIntentFilter.addAction("android.media.STREAM_DEVICES_CHANGED_ACTION");
      localIntentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
      localIntentFilter.addAction("miui.intent.TAKE_SCREENSHOT");
      VolumeDialog.this.mContext.registerReceiverAsUser(VolumeDialog.this.mBroadcastReceiver, UserHandle.ALL, localIntentFilter, null, null);
    }
    
    protected void onStop()
    {
      super.onStop();
      VolumeDialog.this.mContext.unregisterReceiver(VolumeDialog.this.mBroadcastReceiver);
      VolumeDialog.this.mHandler.sendEmptyMessage(4);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (isShowing())
      {
        if (paramMotionEvent.getAction() == 4)
        {
          VolumeDialog.this.dismissH();
          return true;
        }
        if (paramMotionEvent.getActionMasked() == 0)
        {
          this.mDownX = paramMotionEvent.getX();
          this.mDownY = paramMotionEvent.getY();
        }
        else if (paramMotionEvent.getActionMasked() == 2)
        {
          float f1 = Math.abs(this.mDownX - paramMotionEvent.getX());
          float f2 = this.mDownY - paramMotionEvent.getY();
          if ((f1 < f2) && (f2 > ViewConfiguration.get(VolumeDialog.this.mContext).getScaledTouchSlop()))
          {
            VolumeDialog.this.dismissH();
            return true;
          }
        }
      }
      return false;
    }
  }
  
  private final class H
    extends Handler
  {
    private static final int DISMISS = 2;
    private static final int RECHECK = 3;
    private static final int RECHECK_ALL = 4;
    private static final int RESCHEDULE_TIMEOUT = 5;
    private static final int RESET_SCREENSHOT = 11;
    private static final int SHOW = 1;
    private static final int SHOW_SAFE_WARNING = 9;
    private static final int STATE_CHANGED = 6;
    private static final int UPDATE_BOTTOM_MARGIN = 7;
    private static final int UPDATE_LAYOUT_DIRECTION = 8;
    private static final int VIBRATE = 10;
    
    public H()
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        break;
      case 11: 
        Log.d("VolumeDialog", "RESET_SCREENSHOT");
        VolumeDialog.access$4502(VolumeDialog.this, false);
        break;
      case 10: 
        Log.d("VolumeDialog", "VIBRATE");
        VolumeDialog.this.vibrateH();
        break;
      case 9: 
        Log.d("VolumeDialog", "SHOW_SAFE_WARNING");
        VolumeDialog.this.showSafetyWarningH(paramMessage.arg1);
        break;
      case 8: 
        Log.d("VolumeDialog", "UPDATE_LAYOUT_DIRECTION");
        VolumeDialog.this.updateLayoutDirectionH(paramMessage.arg1);
        break;
      case 7: 
        Log.d("VolumeDialog", "UPDATE_BOTTOM_MARGIN");
        VolumeDialog.this.updateDialogBottomMarginH();
        break;
      case 6: 
        Log.d("VolumeDialog", "STATE_CHANGED");
        VolumeDialog.this.stateChangedH(paramMessage.arg1, paramMessage.arg2);
        break;
      case 5: 
        Log.d("VolumeDialog", "RESCHEDULE_TIMEOUT");
        VolumeDialog.this.rescheduleTimeoutH();
        break;
      case 4: 
        Log.d("VolumeDialog", "RECHECK_ALL");
        VolumeDialog.this.recheckH(null);
        break;
      case 3: 
        Log.d("VolumeDialog", "RECHECK");
        VolumeDialog.this.recheckH((VolumeDialog.VolumeRow)paramMessage.obj);
        break;
      case 2: 
        Log.d("VolumeDialog", "DISMISS");
        VolumeDialog.this.dismissH();
        break;
      case 1: 
        Log.d("VolumeDialog", "SHOW");
        VolumeDialog.this.showH(paramMessage.arg1);
      }
    }
  }
  
  private final class SafetyWarningDialog
    extends AlertDialog
  {
    public SafetyWarningDialog(Context paramContext)
    {
      super();
      getWindow().setType(2010);
      WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
      localLayoutParams.privateFlags |= 0x10;
      setMessage(paramContext.getString(286130214));
      setButton(-1, paramContext.getString(17039379), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          VolumeDialog.this.mDelegate.disableSafeMediaVolume();
        }
      });
      setButton(-2, paramContext.getString(17039369), (DialogInterface.OnClickListener)null);
      setIconAttribute(16843605);
      setOnDismissListener(new DialogInterface.OnDismissListener()
      {
        public void onDismiss(DialogInterface arg1)
        {
          synchronized (VolumeDialog.this.mSafetyWarningLock)
          {
            VolumeDialog.access$3302(VolumeDialog.this, null);
            return;
          }
        }
      });
    }
    
    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
      if ((24 != paramInt) && (25 != paramInt)) {
        return super.onKeyDown(paramInt, paramKeyEvent);
      }
      return true;
    }
    
    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
    {
      if ((24 != paramInt) && (25 != paramInt)) {
        return super.onKeyUp(paramInt, paramKeyEvent);
      }
      return true;
    }
  }
  
  private static final class StreamState
  {
    private int level;
    private int levelMax;
    private int levelMin;
    private boolean muteSupported;
    private boolean muted;
    
    public static StreamState getStreamStateByStreamType(Context paramContext, int paramInt, VolumeDialog.VolumePanelDelegate paramVolumePanelDelegate)
    {
      StreamState localStreamState = new StreamState();
      paramContext = (AudioManager)paramContext.getSystemService("audio");
      localStreamState.level = paramContext.getLastAudibleStreamVolume(paramInt);
      localStreamState.levelMax = paramContext.getStreamMaxVolume(paramInt);
      localStreamState.levelMin = paramVolumePanelDelegate.getStreamMinVolume(paramInt);
      localStreamState.muted = paramContext.isStreamMute(paramInt);
      localStreamState.muteSupported = paramVolumePanelDelegate.isStreamAffectedByMute(paramInt);
      if ((Build.VERSION.SDK_INT < 23) && ((paramInt == 6) || (paramInt == 0)))
      {
        localStreamState.level += 1;
        localStreamState.levelMax += 1;
      }
      return localStreamState;
    }
    
    public StreamState copy()
    {
      StreamState localStreamState = new StreamState();
      localStreamState.level = this.level;
      localStreamState.levelMin = this.levelMin;
      localStreamState.levelMax = this.levelMax;
      localStreamState.muted = this.muted;
      localStreamState.muteSupported = this.muteSupported;
      return localStreamState;
    }
  }
  
  private static class VolumeIconRes
  {
    int mutedIconRes;
    int normalIconRes;
    int selectedIconRes;
    
    private VolumeIconRes(int paramInt1, int paramInt2, int paramInt3)
    {
      this.normalIconRes = paramInt1;
      this.selectedIconRes = paramInt2;
      this.mutedIconRes = paramInt3;
    }
  }
  
  public static abstract interface VolumePanelDelegate
  {
    public abstract void disableSafeMediaVolume();
    
    public abstract int getMasterStreamType();
    
    public abstract int getRingerMode();
    
    public abstract int getStreamMinVolume(int paramInt);
    
    public abstract boolean isStreamAffectedByMute(int paramInt);
    
    public abstract void notifyVolumeControllerVisible(boolean paramBoolean);
    
    public abstract void setRingerMode(int paramInt);
    
    public abstract boolean showSafeVolumeDialogByFlags(int paramInt);
  }
  
  private static class VolumeRow
  {
    private ObjectAnimator anim;
    private int animTargetProgress;
    private int cachedIconRes;
    private int cachedProgressRes;
    private ImageButton icon;
    private int iconsMapKey;
    private boolean important;
    private int initIconsMapKey;
    private int lastLevel = 1;
    private miui.widget.SeekBar slider;
    private View space;
    private VolumeDialog.StreamState ss;
    private int stream;
    private boolean tracking;
    private long userAttempt;
    private View view;
  }
  
  private final class VolumeSeekBarChangeListener
    implements SeekBar.OnSeekBarChangeListener
  {
    private VolumeSeekBarChangeListener() {}
    
    private VolumeDialog.VolumeRow getVolumeRow(android.widget.SeekBar paramSeekBar)
    {
      if ((paramSeekBar.getTag() != null) && ((paramSeekBar.getTag() instanceof VolumeDialog.VolumeRow))) {
        return (VolumeDialog.VolumeRow)paramSeekBar.getTag();
      }
      return null;
    }
    
    public void onProgressChanged(android.widget.SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
    {
      VolumeDialog.VolumeRow localVolumeRow = getVolumeRow(paramSeekBar);
      if (localVolumeRow == null) {
        return;
      }
      if (!paramBoolean) {
        return;
      }
      if (VolumeDialog.VolumeRow.access$2100(localVolumeRow).levelMin > 0)
      {
        int i = VolumeDialog.VolumeRow.access$2100(localVolumeRow).levelMin * 100;
        if (paramInt < i) {
          paramSeekBar.setProgress(i);
        }
      }
      paramInt = VolumeDialog.getImpliedLevel(paramSeekBar, paramInt);
      VolumeDialog.VolumeRow.access$2902(localVolumeRow, SystemClock.uptimeMillis());
      paramSeekBar = new StringBuilder();
      paramSeekBar.append("VolumeBar:onProgressChanged ");
      paramSeekBar.append(localVolumeRow.stream);
      paramSeekBar.append(" ");
      paramSeekBar.append(paramInt);
      Log.d("VolumeDialog", paramSeekBar.toString());
      VolumeDialog.this.mAm.setStreamVolume(localVolumeRow.stream, paramInt, 1048576);
    }
    
    public void onStartTrackingTouch(android.widget.SeekBar paramSeekBar)
    {
      paramSeekBar = getVolumeRow(paramSeekBar);
      if (paramSeekBar == null) {
        return;
      }
      VolumeDialog.VolumeRow.access$1402(paramSeekBar, ((VolumeDialog.VolumeIconRes)VolumeDialog.sVolumeIconTypeMap.get(Integer.valueOf(paramSeekBar.iconsMapKey))).selectedIconRes);
      paramSeekBar.icon.setImageResource(paramSeekBar.cachedIconRes);
      VolumeDialog.VolumeRow.access$2802(paramSeekBar, true);
    }
    
    public void onStopTrackingTouch(android.widget.SeekBar paramSeekBar)
    {
      VolumeDialog.VolumeRow localVolumeRow = getVolumeRow(paramSeekBar);
      if (localVolumeRow == null) {
        return;
      }
      VolumeDialog.VolumeRow.access$2802(localVolumeRow, false);
      VolumeDialog.VolumeRow.access$2902(localVolumeRow, SystemClock.uptimeMillis());
      VolumeDialog.getImpliedLevel(paramSeekBar, paramSeekBar.getProgress());
      VolumeDialog.this.mHandler.sendMessageDelayed(VolumeDialog.this.mHandler.obtainMessage(3, localVolumeRow), 1000L);
    }
  }
  
  private static class VolumeSeekbarProgress
  {
    int normalProgress;
    int silentProgress;
    
    private VolumeSeekbarProgress(int paramInt1, int paramInt2)
    {
      this.normalProgress = paramInt1;
      this.silentProgress = paramInt2;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/view/VolumeDialog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */