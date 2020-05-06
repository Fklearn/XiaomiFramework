package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.policy.MiuiPhoneWindow;
import java.util.Formatter;
import java.util.Locale;

public class MediaController
  extends FrameLayout
{
  private static final int sDefaultTimeout = 3000;
  private final AccessibilityManager mAccessibilityManager;
  @UnsupportedAppUsage
  private View mAnchor;
  @UnsupportedAppUsage
  private final Context mContext;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private TextView mCurrentTime;
  @UnsupportedAppUsage
  private View mDecor;
  @UnsupportedAppUsage
  private WindowManager.LayoutParams mDecorLayoutParams;
  private boolean mDragging;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private TextView mEndTime;
  private final Runnable mFadeOut = new Runnable()
  {
    public void run()
    {
      MediaController.this.hide();
    }
  };
  @UnsupportedAppUsage
  private ImageButton mFfwdButton;
  @UnsupportedAppUsage
  private final View.OnClickListener mFfwdListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = MediaController.this.mPlayer.getCurrentPosition();
      MediaController.this.mPlayer.seekTo(i + 15000);
      MediaController.this.setProgress();
      MediaController.this.show(3000);
    }
  };
  StringBuilder mFormatBuilder;
  Formatter mFormatter;
  private boolean mFromXml;
  private final View.OnLayoutChangeListener mLayoutChangeListener = new View.OnLayoutChangeListener()
  {
    public void onLayoutChange(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5, int paramAnonymousInt6, int paramAnonymousInt7, int paramAnonymousInt8)
    {
      MediaController.this.updateFloatingWindowLayout();
      if (MediaController.this.mShowing) {
        MediaController.this.mWindowManager.updateViewLayout(MediaController.this.mDecor, MediaController.this.mDecorLayoutParams);
      }
    }
  };
  private boolean mListenersSet;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private ImageButton mNextButton;
  private View.OnClickListener mNextListener;
  @UnsupportedAppUsage
  private ImageButton mPauseButton;
  private CharSequence mPauseDescription;
  private final View.OnClickListener mPauseListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      MediaController.this.doPauseResume();
      MediaController.this.show(3000);
    }
  };
  private CharSequence mPlayDescription;
  @UnsupportedAppUsage
  private MediaPlayerControl mPlayer;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private ImageButton mPrevButton;
  private View.OnClickListener mPrevListener;
  @UnsupportedAppUsage
  private ProgressBar mProgress;
  @UnsupportedAppUsage
  private ImageButton mRewButton;
  @UnsupportedAppUsage
  private final View.OnClickListener mRewListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = MediaController.this.mPlayer.getCurrentPosition();
      MediaController.this.mPlayer.seekTo(i - 5000);
      MediaController.this.setProgress();
      MediaController.this.show(3000);
    }
  };
  @UnsupportedAppUsage
  private View mRoot;
  @UnsupportedAppUsage
  private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener()
  {
    public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      if (!paramAnonymousBoolean) {
        return;
      }
      long l = MediaController.this.mPlayer.getDuration();
      l = paramAnonymousInt * l / 1000L;
      MediaController.this.mPlayer.seekTo((int)l);
      if (MediaController.this.mCurrentTime != null) {
        MediaController.this.mCurrentTime.setText(MediaController.this.stringForTime((int)l));
      }
    }
    
    public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      MediaController.this.show(3600000);
      MediaController.access$602(MediaController.this, true);
      paramAnonymousSeekBar = MediaController.this;
      paramAnonymousSeekBar.removeCallbacks(paramAnonymousSeekBar.mShowProgress);
    }
    
    public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar)
    {
      MediaController.access$602(MediaController.this, false);
      MediaController.this.setProgress();
      MediaController.this.updatePausePlay();
      MediaController.this.show(3000);
      paramAnonymousSeekBar = MediaController.this;
      paramAnonymousSeekBar.post(paramAnonymousSeekBar.mShowProgress);
    }
  };
  private final Runnable mShowProgress = new Runnable()
  {
    public void run()
    {
      int i = MediaController.this.setProgress();
      if ((!MediaController.this.mDragging) && (MediaController.this.mShowing) && (MediaController.this.mPlayer.isPlaying()))
      {
        MediaController localMediaController = MediaController.this;
        localMediaController.postDelayed(localMediaController.mShowProgress, 1000 - i % 1000);
      }
    }
  };
  @UnsupportedAppUsage
  private boolean mShowing;
  private final View.OnTouchListener mTouchListener = new View.OnTouchListener()
  {
    public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
    {
      if ((paramAnonymousMotionEvent.getAction() == 0) && (MediaController.this.mShowing)) {
        MediaController.this.hide();
      }
      return false;
    }
  };
  private final boolean mUseFastForward;
  @UnsupportedAppUsage
  private Window mWindow;
  @UnsupportedAppUsage
  private WindowManager mWindowManager;
  
  public MediaController(Context paramContext)
  {
    this(paramContext, true);
  }
  
  public MediaController(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mRoot = this;
    this.mContext = paramContext;
    this.mUseFastForward = true;
    this.mFromXml = true;
    this.mAccessibilityManager = AccessibilityManager.getInstance(paramContext);
  }
  
  public MediaController(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mUseFastForward = paramBoolean;
    initFloatingWindowLayout();
    initFloatingWindow();
    this.mAccessibilityManager = AccessibilityManager.getInstance(paramContext);
  }
  
  private void disableUnsupportedButtons()
  {
    try
    {
      if ((this.mPauseButton != null) && (!this.mPlayer.canPause())) {
        this.mPauseButton.setEnabled(false);
      }
      if ((this.mRewButton != null) && (!this.mPlayer.canSeekBackward())) {
        this.mRewButton.setEnabled(false);
      }
      if ((this.mFfwdButton != null) && (!this.mPlayer.canSeekForward())) {
        this.mFfwdButton.setEnabled(false);
      }
      if ((this.mProgress != null) && (!this.mPlayer.canSeekBackward()) && (!this.mPlayer.canSeekForward())) {
        this.mProgress.setEnabled(false);
      }
    }
    catch (IncompatibleClassChangeError localIncompatibleClassChangeError) {}
  }
  
  private void doPauseResume()
  {
    if (this.mPlayer.isPlaying()) {
      this.mPlayer.pause();
    } else {
      this.mPlayer.start();
    }
    updatePausePlay();
  }
  
  private void initControllerView(View paramView)
  {
    Object localObject = this.mContext.getResources();
    this.mPlayDescription = ((Resources)localObject).getText(17040354);
    this.mPauseDescription = ((Resources)localObject).getText(17040353);
    this.mPauseButton = ((ImageButton)paramView.findViewById(16909248));
    localObject = this.mPauseButton;
    if (localObject != null)
    {
      ((ImageButton)localObject).requestFocus();
      this.mPauseButton.setOnClickListener(this.mPauseListener);
    }
    this.mFfwdButton = ((ImageButton)paramView.findViewById(16908922));
    localObject = this.mFfwdButton;
    int i = 0;
    int j;
    if (localObject != null)
    {
      ((ImageButton)localObject).setOnClickListener(this.mFfwdListener);
      if (!this.mFromXml)
      {
        localObject = this.mFfwdButton;
        if (this.mUseFastForward) {
          j = 0;
        } else {
          j = 8;
        }
        ((ImageButton)localObject).setVisibility(j);
      }
    }
    this.mRewButton = ((ImageButton)paramView.findViewById(16909328));
    localObject = this.mRewButton;
    if (localObject != null)
    {
      ((ImageButton)localObject).setOnClickListener(this.mRewListener);
      if (!this.mFromXml)
      {
        localObject = this.mRewButton;
        if (this.mUseFastForward) {
          j = i;
        } else {
          j = 8;
        }
        ((ImageButton)localObject).setVisibility(j);
      }
    }
    this.mNextButton = ((ImageButton)paramView.findViewById(16909167));
    localObject = this.mNextButton;
    if ((localObject != null) && (!this.mFromXml) && (!this.mListenersSet)) {
      ((ImageButton)localObject).setVisibility(8);
    }
    this.mPrevButton = ((ImageButton)paramView.findViewById(16909289));
    localObject = this.mPrevButton;
    if ((localObject != null) && (!this.mFromXml) && (!this.mListenersSet)) {
      ((ImageButton)localObject).setVisibility(8);
    }
    this.mProgress = ((ProgressBar)paramView.findViewById(16909121));
    localObject = this.mProgress;
    if (localObject != null)
    {
      if ((localObject instanceof SeekBar)) {
        ((SeekBar)localObject).setOnSeekBarChangeListener(this.mSeekListener);
      }
      this.mProgress.setMax(1000);
    }
    this.mEndTime = ((TextView)paramView.findViewById(16909500));
    this.mCurrentTime = ((TextView)paramView.findViewById(16909503));
    this.mFormatBuilder = new StringBuilder();
    this.mFormatter = new Formatter(this.mFormatBuilder, Locale.getDefault());
    installPrevNextListeners();
  }
  
  private void initFloatingWindow()
  {
    this.mWindowManager = ((WindowManager)this.mContext.getSystemService("window"));
    this.mWindow = new MiuiPhoneWindow(this.mContext);
    this.mWindow.setWindowManager(this.mWindowManager, null, null);
    this.mWindow.requestFeature(1);
    this.mDecor = this.mWindow.getDecorView();
    this.mDecor.setOnTouchListener(this.mTouchListener);
    this.mWindow.setContentView(this);
    this.mWindow.setBackgroundDrawableResource(17170445);
    this.mWindow.setVolumeControlStream(3);
    setFocusable(true);
    setFocusableInTouchMode(true);
    setDescendantFocusability(262144);
    requestFocus();
  }
  
  private void initFloatingWindowLayout()
  {
    this.mDecorLayoutParams = new WindowManager.LayoutParams();
    WindowManager.LayoutParams localLayoutParams = this.mDecorLayoutParams;
    localLayoutParams.gravity = 51;
    localLayoutParams.height = -2;
    localLayoutParams.x = 0;
    localLayoutParams.format = -3;
    localLayoutParams.type = 1000;
    localLayoutParams.flags |= 0x820020;
    localLayoutParams.token = null;
    localLayoutParams.windowAnimations = 0;
  }
  
  private void installPrevNextListeners()
  {
    ImageButton localImageButton = this.mNextButton;
    boolean bool1 = true;
    boolean bool2;
    if (localImageButton != null)
    {
      localImageButton.setOnClickListener(this.mNextListener);
      localImageButton = this.mNextButton;
      if (this.mNextListener != null) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      localImageButton.setEnabled(bool2);
    }
    localImageButton = this.mPrevButton;
    if (localImageButton != null)
    {
      localImageButton.setOnClickListener(this.mPrevListener);
      localImageButton = this.mPrevButton;
      if (this.mPrevListener != null) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      localImageButton.setEnabled(bool2);
    }
  }
  
  private int setProgress()
  {
    Object localObject = this.mPlayer;
    if ((localObject != null) && (!this.mDragging))
    {
      int i = ((MediaPlayerControl)localObject).getCurrentPosition();
      int j = this.mPlayer.getDuration();
      localObject = this.mProgress;
      if (localObject != null)
      {
        if (j > 0) {
          ((ProgressBar)localObject).setProgress((int)(i * 1000L / j));
        }
        int k = this.mPlayer.getBufferPercentage();
        this.mProgress.setSecondaryProgress(k * 10);
      }
      localObject = this.mEndTime;
      if (localObject != null) {
        ((TextView)localObject).setText(stringForTime(j));
      }
      localObject = this.mCurrentTime;
      if (localObject != null) {
        ((TextView)localObject).setText(stringForTime(i));
      }
      return i;
    }
    return 0;
  }
  
  private String stringForTime(int paramInt)
  {
    int i = paramInt / 1000;
    int j = i % 60;
    paramInt = i / 60 % 60;
    i /= 3600;
    this.mFormatBuilder.setLength(0);
    if (i > 0) {
      return this.mFormatter.format("%d:%02d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt), Integer.valueOf(j) }).toString();
    }
    return this.mFormatter.format("%02d:%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(j) }).toString();
  }
  
  private void updateFloatingWindowLayout()
  {
    int[] arrayOfInt = new int[2];
    this.mAnchor.getLocationOnScreen(arrayOfInt);
    this.mDecor.measure(View.MeasureSpec.makeMeasureSpec(this.mAnchor.getWidth(), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(this.mAnchor.getHeight(), Integer.MIN_VALUE));
    WindowManager.LayoutParams localLayoutParams = this.mDecorLayoutParams;
    localLayoutParams.width = this.mAnchor.getWidth();
    localLayoutParams.x = (arrayOfInt[0] + (this.mAnchor.getWidth() - localLayoutParams.width) / 2);
    localLayoutParams.y = (arrayOfInt[1] + this.mAnchor.getHeight() - this.mDecor.getMeasuredHeight());
  }
  
  @UnsupportedAppUsage
  private void updatePausePlay()
  {
    if ((this.mRoot != null) && (this.mPauseButton != null))
    {
      if (this.mPlayer.isPlaying())
      {
        this.mPauseButton.setImageResource(17301539);
        this.mPauseButton.setContentDescription(this.mPauseDescription);
      }
      else
      {
        this.mPauseButton.setImageResource(17301540);
        this.mPauseButton.setContentDescription(this.mPlayDescription);
      }
      return;
    }
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    int i = paramKeyEvent.getKeyCode();
    int j;
    if ((paramKeyEvent.getRepeatCount() == 0) && (paramKeyEvent.getAction() == 0)) {
      j = 1;
    } else {
      j = 0;
    }
    if ((i != 79) && (i != 85) && (i != 62))
    {
      if (i == 126)
      {
        if ((j != 0) && (!this.mPlayer.isPlaying()))
        {
          this.mPlayer.start();
          updatePausePlay();
          show(3000);
        }
        return true;
      }
      if ((i != 86) && (i != 127))
      {
        if ((i != 25) && (i != 24) && (i != 164) && (i != 27))
        {
          if ((i != 4) && (i != 82))
          {
            show(3000);
            return super.dispatchKeyEvent(paramKeyEvent);
          }
          if (j != 0) {
            hide();
          }
          return true;
        }
        return super.dispatchKeyEvent(paramKeyEvent);
      }
      if ((j != 0) && (this.mPlayer.isPlaying()))
      {
        this.mPlayer.pause();
        updatePausePlay();
        show(3000);
      }
      return true;
    }
    if (j != 0)
    {
      doPauseResume();
      show(3000);
      paramKeyEvent = this.mPauseButton;
      if (paramKeyEvent != null) {
        paramKeyEvent.requestFocus();
      }
    }
    return true;
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return MediaController.class.getName();
  }
  
  public void hide()
  {
    if (this.mAnchor == null) {
      return;
    }
    if (this.mShowing)
    {
      try
      {
        removeCallbacks(this.mShowProgress);
        this.mWindowManager.removeView(this.mDecor);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        Log.w("MediaController", "already removed");
      }
      this.mShowing = false;
    }
  }
  
  public boolean isShowing()
  {
    return this.mShowing;
  }
  
  protected View makeControllerView()
  {
    this.mRoot = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(17367184, null);
    initControllerView(this.mRoot);
    return this.mRoot;
  }
  
  public void onFinishInflate()
  {
    View localView = this.mRoot;
    if (localView != null) {
      initControllerView(localView);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (i != 0)
    {
      if (i != 1)
      {
        if (i == 3) {
          hide();
        }
      }
      else {
        show(3000);
      }
    }
    else {
      show(0);
    }
    return true;
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    show(3000);
    return false;
  }
  
  public void setAnchorView(View paramView)
  {
    View localView = this.mAnchor;
    if (localView != null) {
      localView.removeOnLayoutChangeListener(this.mLayoutChangeListener);
    }
    this.mAnchor = paramView;
    paramView = this.mAnchor;
    if (paramView != null) {
      paramView.addOnLayoutChangeListener(this.mLayoutChangeListener);
    }
    paramView = new FrameLayout.LayoutParams(-1, -1);
    removeAllViews();
    addView(makeControllerView(), paramView);
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    Object localObject = this.mPauseButton;
    if (localObject != null) {
      ((ImageButton)localObject).setEnabled(paramBoolean);
    }
    localObject = this.mFfwdButton;
    if (localObject != null) {
      ((ImageButton)localObject).setEnabled(paramBoolean);
    }
    localObject = this.mRewButton;
    if (localObject != null) {
      ((ImageButton)localObject).setEnabled(paramBoolean);
    }
    localObject = this.mNextButton;
    boolean bool1 = true;
    boolean bool2;
    if (localObject != null)
    {
      if ((paramBoolean) && (this.mNextListener != null)) {
        bool2 = true;
      } else {
        bool2 = false;
      }
      ((ImageButton)localObject).setEnabled(bool2);
    }
    localObject = this.mPrevButton;
    if (localObject != null)
    {
      if ((paramBoolean) && (this.mPrevListener != null)) {
        bool2 = bool1;
      } else {
        bool2 = false;
      }
      ((ImageButton)localObject).setEnabled(bool2);
    }
    localObject = this.mProgress;
    if (localObject != null) {
      ((ProgressBar)localObject).setEnabled(paramBoolean);
    }
    disableUnsupportedButtons();
    super.setEnabled(paramBoolean);
  }
  
  public void setMediaPlayer(MediaPlayerControl paramMediaPlayerControl)
  {
    this.mPlayer = paramMediaPlayerControl;
    updatePausePlay();
  }
  
  public void setPrevNextListeners(View.OnClickListener paramOnClickListener1, View.OnClickListener paramOnClickListener2)
  {
    this.mNextListener = paramOnClickListener1;
    this.mPrevListener = paramOnClickListener2;
    this.mListenersSet = true;
    if (this.mRoot != null)
    {
      installPrevNextListeners();
      paramOnClickListener1 = this.mNextButton;
      if ((paramOnClickListener1 != null) && (!this.mFromXml)) {
        paramOnClickListener1.setVisibility(0);
      }
      paramOnClickListener1 = this.mPrevButton;
      if ((paramOnClickListener1 != null) && (!this.mFromXml)) {
        paramOnClickListener1.setVisibility(0);
      }
    }
  }
  
  public void show()
  {
    show(3000);
  }
  
  public void show(int paramInt)
  {
    if ((!this.mShowing) && (this.mAnchor != null))
    {
      setProgress();
      ImageButton localImageButton = this.mPauseButton;
      if (localImageButton != null) {
        localImageButton.requestFocus();
      }
      disableUnsupportedButtons();
      updateFloatingWindowLayout();
      this.mWindowManager.addView(this.mDecor, this.mDecorLayoutParams);
      this.mShowing = true;
    }
    updatePausePlay();
    post(this.mShowProgress);
    if ((paramInt != 0) && (!this.mAccessibilityManager.isTouchExplorationEnabled()))
    {
      removeCallbacks(this.mFadeOut);
      postDelayed(this.mFadeOut, paramInt);
    }
  }
  
  public static abstract interface MediaPlayerControl
  {
    public abstract boolean canPause();
    
    public abstract boolean canSeekBackward();
    
    public abstract boolean canSeekForward();
    
    public abstract int getAudioSessionId();
    
    public abstract int getBufferPercentage();
    
    public abstract int getCurrentPosition();
    
    public abstract int getDuration();
    
    public abstract boolean isPlaying();
    
    public abstract void pause();
    
    public abstract void seekTo(int paramInt);
    
    public abstract void start();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/MediaController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */