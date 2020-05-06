package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.media.Metadata;
import android.media.SubtitleController.Anchor;
import android.media.SubtitleTrack.RenderingWidget;
import android.media.SubtitleTrack.RenderingWidget.OnChangedListener;
import android.net.Uri;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import java.io.InputStream;
import java.util.Map;
import java.util.Vector;

public class VideoView
  extends SurfaceView
  implements MediaController.MediaPlayerControl, SubtitleController.Anchor
{
  private static final int STATE_ERROR = -1;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private static final int STATE_IDLE = 0;
  private static final int STATE_PAUSED = 4;
  private static final int STATE_PLAYBACK_COMPLETED = 5;
  private static final int STATE_PLAYING = 3;
  private static final int STATE_PREPARED = 2;
  private static final int STATE_PREPARING = 1;
  private static final String TAG = "VideoView";
  private AudioAttributes mAudioAttributes = new AudioAttributes.Builder().setUsage(1).setContentType(3).build();
  private int mAudioFocusType = 1;
  private AudioManager mAudioManager = (AudioManager)this.mContext.getSystemService("audio");
  private int mAudioSession;
  private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener()
  {
    public void onBufferingUpdate(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt)
    {
      VideoView.access$2002(VideoView.this, paramAnonymousInt);
    }
  };
  private boolean mCanPause;
  private boolean mCanSeekBack;
  private boolean mCanSeekForward;
  private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener()
  {
    public void onCompletion(MediaPlayer paramAnonymousMediaPlayer)
    {
      VideoView.access$202(VideoView.this, 5);
      VideoView.access$1202(VideoView.this, 5);
      if (VideoView.this.mMediaController != null) {
        VideoView.this.mMediaController.hide();
      }
      if (VideoView.this.mOnCompletionListener != null) {
        VideoView.this.mOnCompletionListener.onCompletion(VideoView.this.mMediaPlayer);
      }
      if (VideoView.this.mAudioFocusType != 0) {
        VideoView.this.mAudioManager.abandonAudioFocus(null);
      }
    }
  };
  @UnsupportedAppUsage
  private int mCurrentBufferPercentage;
  @UnsupportedAppUsage
  private int mCurrentState = 0;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener()
  {
    public boolean onError(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      paramAnonymousMediaPlayer = new StringBuilder();
      paramAnonymousMediaPlayer.append("Error: ");
      paramAnonymousMediaPlayer.append(paramAnonymousInt1);
      paramAnonymousMediaPlayer.append(",");
      paramAnonymousMediaPlayer.append(paramAnonymousInt2);
      Log.d("VideoView", paramAnonymousMediaPlayer.toString());
      VideoView.access$202(VideoView.this, -1);
      VideoView.access$1202(VideoView.this, -1);
      if (VideoView.this.mMediaController != null) {
        VideoView.this.mMediaController.hide();
      }
      if ((VideoView.this.mOnErrorListener != null) && (VideoView.this.mOnErrorListener.onError(VideoView.this.mMediaPlayer, paramAnonymousInt1, paramAnonymousInt2))) {
        return true;
      }
      if (VideoView.this.getWindowToken() != null)
      {
        VideoView.this.mContext.getResources();
        if (paramAnonymousInt1 == 200) {
          paramAnonymousInt1 = 17039381;
        } else {
          paramAnonymousInt1 = 17039377;
        }
        new AlertDialog.Builder(VideoView.this.mContext).setMessage(paramAnonymousInt1).setPositiveButton(17039376, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            if (VideoView.this.mOnCompletionListener != null) {
              VideoView.this.mOnCompletionListener.onCompletion(VideoView.this.mMediaPlayer);
            }
          }
        }).setCancelable(false).show();
      }
      return true;
    }
  };
  @UnsupportedAppUsage
  private Map<String, String> mHeaders;
  private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener()
  {
    public boolean onInfo(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (VideoView.this.mOnInfoListener != null) {
        VideoView.this.mOnInfoListener.onInfo(paramAnonymousMediaPlayer, paramAnonymousInt1, paramAnonymousInt2);
      }
      return true;
    }
  };
  @UnsupportedAppUsage
  private MediaController mMediaController;
  @UnsupportedAppUsage
  private MediaPlayer mMediaPlayer = null;
  private MediaPlayer.OnCompletionListener mOnCompletionListener;
  private MediaPlayer.OnErrorListener mOnErrorListener;
  private MediaPlayer.OnInfoListener mOnInfoListener;
  private MediaPlayer.OnPreparedListener mOnPreparedListener;
  private final Vector<Pair<InputStream, MediaFormat>> mPendingSubtitleTracks = new Vector();
  @UnsupportedAppUsage
  MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener()
  {
    public void onPrepared(MediaPlayer paramAnonymousMediaPlayer)
    {
      VideoView.access$202(VideoView.this, 2);
      Object localObject = paramAnonymousMediaPlayer.getMetadata(false, false);
      if (localObject != null)
      {
        VideoView localVideoView = VideoView.this;
        boolean bool;
        if ((((Metadata)localObject).has(1)) && (!((Metadata)localObject).getBoolean(1))) {
          bool = false;
        } else {
          bool = true;
        }
        VideoView.access$302(localVideoView, bool);
        localVideoView = VideoView.this;
        if ((((Metadata)localObject).has(2)) && (!((Metadata)localObject).getBoolean(2))) {
          bool = false;
        } else {
          bool = true;
        }
        VideoView.access$402(localVideoView, bool);
        localVideoView = VideoView.this;
        if ((((Metadata)localObject).has(3)) && (!((Metadata)localObject).getBoolean(3))) {
          bool = false;
        } else {
          bool = true;
        }
        VideoView.access$502(localVideoView, bool);
      }
      else
      {
        localObject = VideoView.this;
        VideoView.access$302((VideoView)localObject, VideoView.access$402((VideoView)localObject, VideoView.access$502((VideoView)localObject, true)));
      }
      if (VideoView.this.mOnPreparedListener != null) {
        VideoView.this.mOnPreparedListener.onPrepared(VideoView.this.mMediaPlayer);
      }
      if (VideoView.this.mMediaController != null) {
        VideoView.this.mMediaController.setEnabled(true);
      }
      VideoView.access$002(VideoView.this, paramAnonymousMediaPlayer.getVideoWidth());
      VideoView.access$102(VideoView.this, paramAnonymousMediaPlayer.getVideoHeight());
      int i = VideoView.this.mSeekWhenPrepared;
      if (i != 0) {
        VideoView.this.seekTo(i);
      }
      if ((VideoView.this.mVideoWidth != 0) && (VideoView.this.mVideoHeight != 0))
      {
        VideoView.this.getHolder().setFixedSize(VideoView.this.mVideoWidth, VideoView.this.mVideoHeight);
        if ((VideoView.this.mSurfaceWidth == VideoView.this.mVideoWidth) && (VideoView.this.mSurfaceHeight == VideoView.this.mVideoHeight)) {
          if (VideoView.this.mTargetState == 3)
          {
            VideoView.this.start();
            if (VideoView.this.mMediaController != null) {
              VideoView.this.mMediaController.show();
            }
          }
          else if ((!VideoView.this.isPlaying()) && ((i != 0) || (VideoView.this.getCurrentPosition() > 0)) && (VideoView.this.mMediaController != null))
          {
            VideoView.this.mMediaController.show(0);
          }
        }
      }
      else if (VideoView.this.mTargetState == 3)
      {
        VideoView.this.start();
      }
    }
  };
  @UnsupportedAppUsage
  SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
  {
    public void surfaceChanged(SurfaceHolder paramAnonymousSurfaceHolder, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      VideoView.access$1002(VideoView.this, paramAnonymousInt2);
      VideoView.access$1102(VideoView.this, paramAnonymousInt3);
      paramAnonymousInt1 = VideoView.this.mTargetState;
      int i = 1;
      if (paramAnonymousInt1 == 3) {
        paramAnonymousInt1 = 1;
      } else {
        paramAnonymousInt1 = 0;
      }
      if ((VideoView.this.mVideoWidth == paramAnonymousInt2) && (VideoView.this.mVideoHeight == paramAnonymousInt3)) {
        paramAnonymousInt2 = i;
      } else {
        paramAnonymousInt2 = 0;
      }
      if ((VideoView.this.mMediaPlayer != null) && (paramAnonymousInt1 != 0) && (paramAnonymousInt2 != 0))
      {
        if (VideoView.this.mSeekWhenPrepared != 0)
        {
          paramAnonymousSurfaceHolder = VideoView.this;
          paramAnonymousSurfaceHolder.seekTo(paramAnonymousSurfaceHolder.mSeekWhenPrepared);
        }
        VideoView.this.start();
      }
    }
    
    public void surfaceCreated(SurfaceHolder paramAnonymousSurfaceHolder)
    {
      VideoView.access$2102(VideoView.this, paramAnonymousSurfaceHolder);
      VideoView.this.openVideo();
    }
    
    public void surfaceDestroyed(SurfaceHolder paramAnonymousSurfaceHolder)
    {
      VideoView.access$2102(VideoView.this, null);
      if (VideoView.this.mMediaController != null) {
        VideoView.this.mMediaController.hide();
      }
      VideoView.this.release(true);
    }
  };
  private int mSeekWhenPrepared;
  MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener()
  {
    public void onVideoSizeChanged(MediaPlayer paramAnonymousMediaPlayer, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      VideoView.access$002(VideoView.this, paramAnonymousMediaPlayer.getVideoWidth());
      VideoView.access$102(VideoView.this, paramAnonymousMediaPlayer.getVideoHeight());
      if ((VideoView.this.mVideoWidth != 0) && (VideoView.this.mVideoHeight != 0))
      {
        VideoView.this.getHolder().setFixedSize(VideoView.this.mVideoWidth, VideoView.this.mVideoHeight);
        VideoView.this.requestLayout();
      }
    }
  };
  private SubtitleTrack.RenderingWidget mSubtitleWidget;
  private SubtitleTrack.RenderingWidget.OnChangedListener mSubtitlesChangedListener;
  private int mSurfaceHeight;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private SurfaceHolder mSurfaceHolder = null;
  private int mSurfaceWidth;
  @UnsupportedAppUsage
  private int mTargetState = 0;
  @UnsupportedAppUsage
  private Uri mUri;
  @UnsupportedAppUsage
  private int mVideoHeight = 0;
  @UnsupportedAppUsage
  private int mVideoWidth = 0;
  
  public VideoView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public VideoView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public VideoView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public VideoView(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    getHolder().addCallback(this.mSHCallback);
    getHolder().setType(3);
    setFocusable(true);
    setFocusableInTouchMode(true);
    requestFocus();
    this.mCurrentState = 0;
    this.mTargetState = 0;
  }
  
  private void attachMediaController()
  {
    if (this.mMediaPlayer != null)
    {
      Object localObject = this.mMediaController;
      if (localObject != null)
      {
        ((MediaController)localObject).setMediaPlayer(this);
        if ((getParent() instanceof View)) {
          localObject = (View)getParent();
        } else {
          localObject = this;
        }
        this.mMediaController.setAnchorView((View)localObject);
        this.mMediaController.setEnabled(isInPlaybackState());
      }
    }
  }
  
  private boolean isInPlaybackState()
  {
    MediaPlayer localMediaPlayer = this.mMediaPlayer;
    boolean bool = true;
    if (localMediaPlayer != null)
    {
      int i = this.mCurrentState;
      if ((i != -1) && (i != 0) && (i != 1)) {}
    }
    else
    {
      bool = false;
    }
    return bool;
  }
  
  private void measureAndLayoutSubtitleWidget()
  {
    int i = getWidth();
    int j = getPaddingLeft();
    int k = getPaddingRight();
    int m = getHeight();
    int n = getPaddingTop();
    int i1 = getPaddingBottom();
    this.mSubtitleWidget.setSize(i - j - k, m - n - i1);
  }
  
  /* Error */
  private void openVideo()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 350	android/widget/VideoView:mUri	Landroid/net/Uri;
    //   4: ifnull +561 -> 565
    //   7: aload_0
    //   8: getfield 131	android/widget/VideoView:mSurfaceHolder	Landroid/view/SurfaceHolder;
    //   11: ifnonnull +6 -> 17
    //   14: goto +551 -> 565
    //   17: aload_0
    //   18: iconst_0
    //   19: invokespecial 268	android/widget/VideoView:release	(Z)V
    //   22: aload_0
    //   23: getfield 135	android/widget/VideoView:mAudioFocusType	I
    //   26: istore_1
    //   27: iload_1
    //   28: ifeq +18 -> 46
    //   31: aload_0
    //   32: getfield 178	android/widget/VideoView:mAudioManager	Landroid/media/AudioManager;
    //   35: aconst_null
    //   36: aload_0
    //   37: getfield 194	android/widget/VideoView:mAudioAttributes	Landroid/media/AudioAttributes;
    //   40: iload_1
    //   41: iconst_0
    //   42: invokevirtual 354	android/media/AudioManager:requestAudioFocus	(Landroid/media/AudioManager$OnAudioFocusChangeListener;Landroid/media/AudioAttributes;II)I
    //   45: pop
    //   46: new 356	android/media/MediaPlayer
    //   49: astore_2
    //   50: aload_2
    //   51: invokespecial 357	android/media/MediaPlayer:<init>	()V
    //   54: aload_0
    //   55: aload_2
    //   56: putfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   59: aload_0
    //   60: invokevirtual 361	android/widget/VideoView:getContext	()Landroid/content/Context;
    //   63: astore_2
    //   64: new 363	android/media/SubtitleController
    //   67: astore_3
    //   68: aload_3
    //   69: aload_2
    //   70: aload_0
    //   71: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   74: invokevirtual 367	android/media/MediaPlayer:getMediaTimeProvider	()Landroid/media/MediaTimeProvider;
    //   77: aload_0
    //   78: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   81: invokespecial 370	android/media/SubtitleController:<init>	(Landroid/content/Context;Landroid/media/MediaTimeProvider;Landroid/media/SubtitleController$Listener;)V
    //   84: new 372	android/media/WebVttRenderer
    //   87: astore 4
    //   89: aload 4
    //   91: aload_2
    //   92: invokespecial 374	android/media/WebVttRenderer:<init>	(Landroid/content/Context;)V
    //   95: aload_3
    //   96: aload 4
    //   98: invokevirtual 378	android/media/SubtitleController:registerRenderer	(Landroid/media/SubtitleController$Renderer;)V
    //   101: new 380	android/media/TtmlRenderer
    //   104: astore 4
    //   106: aload 4
    //   108: aload_2
    //   109: invokespecial 381	android/media/TtmlRenderer:<init>	(Landroid/content/Context;)V
    //   112: aload_3
    //   113: aload 4
    //   115: invokevirtual 378	android/media/SubtitleController:registerRenderer	(Landroid/media/SubtitleController$Renderer;)V
    //   118: new 383	android/media/Cea708CaptionRenderer
    //   121: astore 4
    //   123: aload 4
    //   125: aload_2
    //   126: invokespecial 384	android/media/Cea708CaptionRenderer:<init>	(Landroid/content/Context;)V
    //   129: aload_3
    //   130: aload 4
    //   132: invokevirtual 378	android/media/SubtitleController:registerRenderer	(Landroid/media/SubtitleController$Renderer;)V
    //   135: new 386	android/media/ClosedCaptionRenderer
    //   138: astore 4
    //   140: aload 4
    //   142: aload_2
    //   143: invokespecial 387	android/media/ClosedCaptionRenderer:<init>	(Landroid/content/Context;)V
    //   146: aload_3
    //   147: aload 4
    //   149: invokevirtual 378	android/media/SubtitleController:registerRenderer	(Landroid/media/SubtitleController$Renderer;)V
    //   152: aload_0
    //   153: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   156: aload_3
    //   157: aload_0
    //   158: invokevirtual 391	android/media/MediaPlayer:setSubtitleAnchor	(Landroid/media/SubtitleController;Landroid/media/SubtitleController$Anchor;)V
    //   161: aload_0
    //   162: getfield 393	android/widget/VideoView:mAudioSession	I
    //   165: ifeq +17 -> 182
    //   168: aload_0
    //   169: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   172: aload_0
    //   173: getfield 393	android/widget/VideoView:mAudioSession	I
    //   176: invokevirtual 396	android/media/MediaPlayer:setAudioSessionId	(I)V
    //   179: goto +14 -> 193
    //   182: aload_0
    //   183: aload_0
    //   184: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   187: invokevirtual 399	android/media/MediaPlayer:getAudioSessionId	()I
    //   190: putfield 393	android/widget/VideoView:mAudioSession	I
    //   193: aload_0
    //   194: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   197: aload_0
    //   198: getfield 143	android/widget/VideoView:mPreparedListener	Landroid/media/MediaPlayer$OnPreparedListener;
    //   201: invokevirtual 403	android/media/MediaPlayer:setOnPreparedListener	(Landroid/media/MediaPlayer$OnPreparedListener;)V
    //   204: aload_0
    //   205: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   208: aload_0
    //   209: getfield 140	android/widget/VideoView:mSizeChangedListener	Landroid/media/MediaPlayer$OnVideoSizeChangedListener;
    //   212: invokevirtual 407	android/media/MediaPlayer:setOnVideoSizeChangedListener	(Landroid/media/MediaPlayer$OnVideoSizeChangedListener;)V
    //   215: aload_0
    //   216: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   219: aload_0
    //   220: getfield 146	android/widget/VideoView:mCompletionListener	Landroid/media/MediaPlayer$OnCompletionListener;
    //   223: invokevirtual 411	android/media/MediaPlayer:setOnCompletionListener	(Landroid/media/MediaPlayer$OnCompletionListener;)V
    //   226: aload_0
    //   227: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   230: aload_0
    //   231: getfield 152	android/widget/VideoView:mErrorListener	Landroid/media/MediaPlayer$OnErrorListener;
    //   234: invokevirtual 415	android/media/MediaPlayer:setOnErrorListener	(Landroid/media/MediaPlayer$OnErrorListener;)V
    //   237: aload_0
    //   238: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   241: aload_0
    //   242: getfield 149	android/widget/VideoView:mInfoListener	Landroid/media/MediaPlayer$OnInfoListener;
    //   245: invokevirtual 419	android/media/MediaPlayer:setOnInfoListener	(Landroid/media/MediaPlayer$OnInfoListener;)V
    //   248: aload_0
    //   249: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   252: aload_0
    //   253: getfield 155	android/widget/VideoView:mBufferingUpdateListener	Landroid/media/MediaPlayer$OnBufferingUpdateListener;
    //   256: invokevirtual 423	android/media/MediaPlayer:setOnBufferingUpdateListener	(Landroid/media/MediaPlayer$OnBufferingUpdateListener;)V
    //   259: aload_0
    //   260: iconst_0
    //   261: putfield 256	android/widget/VideoView:mCurrentBufferPercentage	I
    //   264: aload_0
    //   265: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   268: aload_0
    //   269: getfield 166	android/widget/VideoView:mContext	Landroid/content/Context;
    //   272: aload_0
    //   273: getfield 350	android/widget/VideoView:mUri	Landroid/net/Uri;
    //   276: aload_0
    //   277: getfield 425	android/widget/VideoView:mHeaders	Ljava/util/Map;
    //   280: invokevirtual 429	android/media/MediaPlayer:setDataSource	(Landroid/content/Context;Landroid/net/Uri;Ljava/util/Map;)V
    //   283: aload_0
    //   284: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   287: aload_0
    //   288: getfield 131	android/widget/VideoView:mSurfaceHolder	Landroid/view/SurfaceHolder;
    //   291: invokevirtual 433	android/media/MediaPlayer:setDisplay	(Landroid/view/SurfaceHolder;)V
    //   294: aload_0
    //   295: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   298: aload_0
    //   299: getfield 194	android/widget/VideoView:mAudioAttributes	Landroid/media/AudioAttributes;
    //   302: invokevirtual 437	android/media/MediaPlayer:setAudioAttributes	(Landroid/media/AudioAttributes;)V
    //   305: aload_0
    //   306: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   309: iconst_1
    //   310: invokevirtual 440	android/media/MediaPlayer:setScreenOnWhilePlaying	(Z)V
    //   313: aload_0
    //   314: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   317: invokevirtual 443	android/media/MediaPlayer:prepareAsync	()V
    //   320: aload_0
    //   321: getfield 125	android/widget/VideoView:mPendingSubtitleTracks	Ljava/util/Vector;
    //   324: invokevirtual 447	java/util/Vector:iterator	()Ljava/util/Iterator;
    //   327: astore_2
    //   328: aload_2
    //   329: invokeinterface 452 1 0
    //   334: ifeq +59 -> 393
    //   337: aload_2
    //   338: invokeinterface 456 1 0
    //   343: checkcast 458	android/util/Pair
    //   346: astore_3
    //   347: aload_0
    //   348: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   351: aload_3
    //   352: getfield 462	android/util/Pair:first	Ljava/lang/Object;
    //   355: checkcast 464	java/io/InputStream
    //   358: aload_3
    //   359: getfield 467	android/util/Pair:second	Ljava/lang/Object;
    //   362: checkcast 469	android/media/MediaFormat
    //   365: invokevirtual 473	android/media/MediaPlayer:addSubtitleSource	(Ljava/io/InputStream;Landroid/media/MediaFormat;)V
    //   368: goto +22 -> 390
    //   371: astore_3
    //   372: aload_0
    //   373: getfield 149	android/widget/VideoView:mInfoListener	Landroid/media/MediaPlayer$OnInfoListener;
    //   376: aload_0
    //   377: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   380: sipush 901
    //   383: iconst_0
    //   384: invokeinterface 479 4 0
    //   389: pop
    //   390: goto -62 -> 328
    //   393: aload_0
    //   394: iconst_1
    //   395: putfield 127	android/widget/VideoView:mCurrentState	I
    //   398: aload_0
    //   399: invokespecial 481	android/widget/VideoView:attachMediaController	()V
    //   402: aload_0
    //   403: getfield 125	android/widget/VideoView:mPendingSubtitleTracks	Ljava/util/Vector;
    //   406: invokevirtual 484	java/util/Vector:clear	()V
    //   409: return
    //   410: astore_2
    //   411: goto +145 -> 556
    //   414: astore_2
    //   415: new 486	java/lang/StringBuilder
    //   418: astore_3
    //   419: aload_3
    //   420: invokespecial 487	java/lang/StringBuilder:<init>	()V
    //   423: aload_3
    //   424: ldc_w 489
    //   427: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   430: pop
    //   431: aload_3
    //   432: aload_0
    //   433: getfield 350	android/widget/VideoView:mUri	Landroid/net/Uri;
    //   436: invokevirtual 496	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   439: pop
    //   440: ldc 51
    //   442: aload_3
    //   443: invokevirtual 500	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   446: aload_2
    //   447: invokestatic 506	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   450: pop
    //   451: aload_0
    //   452: iconst_m1
    //   453: putfield 127	android/widget/VideoView:mCurrentState	I
    //   456: aload_0
    //   457: iconst_m1
    //   458: putfield 129	android/widget/VideoView:mTargetState	I
    //   461: aload_0
    //   462: getfield 152	android/widget/VideoView:mErrorListener	Landroid/media/MediaPlayer$OnErrorListener;
    //   465: aload_0
    //   466: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   469: iconst_1
    //   470: iconst_0
    //   471: invokeinterface 511 4 0
    //   476: pop
    //   477: aload_0
    //   478: getfield 125	android/widget/VideoView:mPendingSubtitleTracks	Ljava/util/Vector;
    //   481: invokevirtual 484	java/util/Vector:clear	()V
    //   484: return
    //   485: astore_2
    //   486: new 486	java/lang/StringBuilder
    //   489: astore_3
    //   490: aload_3
    //   491: invokespecial 487	java/lang/StringBuilder:<init>	()V
    //   494: aload_3
    //   495: ldc_w 489
    //   498: invokevirtual 493	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   501: pop
    //   502: aload_3
    //   503: aload_0
    //   504: getfield 350	android/widget/VideoView:mUri	Landroid/net/Uri;
    //   507: invokevirtual 496	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   510: pop
    //   511: ldc 51
    //   513: aload_3
    //   514: invokevirtual 500	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   517: aload_2
    //   518: invokestatic 506	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   521: pop
    //   522: aload_0
    //   523: iconst_m1
    //   524: putfield 127	android/widget/VideoView:mCurrentState	I
    //   527: aload_0
    //   528: iconst_m1
    //   529: putfield 129	android/widget/VideoView:mTargetState	I
    //   532: aload_0
    //   533: getfield 152	android/widget/VideoView:mErrorListener	Landroid/media/MediaPlayer$OnErrorListener;
    //   536: aload_0
    //   537: getfield 133	android/widget/VideoView:mMediaPlayer	Landroid/media/MediaPlayer;
    //   540: iconst_1
    //   541: iconst_0
    //   542: invokeinterface 511 4 0
    //   547: pop
    //   548: aload_0
    //   549: getfield 125	android/widget/VideoView:mPendingSubtitleTracks	Ljava/util/Vector;
    //   552: invokevirtual 484	java/util/Vector:clear	()V
    //   555: return
    //   556: aload_0
    //   557: getfield 125	android/widget/VideoView:mPendingSubtitleTracks	Ljava/util/Vector;
    //   560: invokevirtual 484	java/util/Vector:clear	()V
    //   563: aload_2
    //   564: athrow
    //   565: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	566	0	this	VideoView
    //   26	15	1	i	int
    //   49	289	2	localObject1	Object
    //   410	1	2	localObject2	Object
    //   414	33	2	localIllegalArgumentException	IllegalArgumentException
    //   485	79	2	localIOException	java.io.IOException
    //   67	292	3	localObject3	Object
    //   371	1	3	localIllegalStateException	IllegalStateException
    //   418	96	3	localStringBuilder	StringBuilder
    //   87	61	4	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   347	368	371	java/lang/IllegalStateException
    //   46	179	410	finally
    //   182	193	410	finally
    //   193	328	410	finally
    //   328	347	410	finally
    //   347	368	410	finally
    //   372	390	410	finally
    //   393	402	410	finally
    //   415	477	410	finally
    //   486	548	410	finally
    //   46	179	414	java/lang/IllegalArgumentException
    //   182	193	414	java/lang/IllegalArgumentException
    //   193	328	414	java/lang/IllegalArgumentException
    //   328	347	414	java/lang/IllegalArgumentException
    //   347	368	414	java/lang/IllegalArgumentException
    //   372	390	414	java/lang/IllegalArgumentException
    //   393	402	414	java/lang/IllegalArgumentException
    //   46	179	485	java/io/IOException
    //   182	193	485	java/io/IOException
    //   193	328	485	java/io/IOException
    //   328	347	485	java/io/IOException
    //   347	368	485	java/io/IOException
    //   372	390	485	java/io/IOException
    //   393	402	485	java/io/IOException
  }
  
  @UnsupportedAppUsage
  private void release(boolean paramBoolean)
  {
    MediaPlayer localMediaPlayer = this.mMediaPlayer;
    if (localMediaPlayer != null)
    {
      localMediaPlayer.reset();
      this.mMediaPlayer.release();
      this.mMediaPlayer = null;
      this.mPendingSubtitleTracks.clear();
      this.mCurrentState = 0;
      if (paramBoolean) {
        this.mTargetState = 0;
      }
      if (this.mAudioFocusType != 0) {
        this.mAudioManager.abandonAudioFocus(null);
      }
    }
  }
  
  private void toggleMediaControlsVisiblity()
  {
    if (this.mMediaController.isShowing()) {
      this.mMediaController.hide();
    } else {
      this.mMediaController.show();
    }
  }
  
  public void addSubtitleSource(InputStream paramInputStream, MediaFormat paramMediaFormat)
  {
    MediaPlayer localMediaPlayer = this.mMediaPlayer;
    if (localMediaPlayer == null) {
      this.mPendingSubtitleTracks.add(Pair.create(paramInputStream, paramMediaFormat));
    } else {
      try
      {
        localMediaPlayer.addSubtitleSource(paramInputStream, paramMediaFormat);
      }
      catch (IllegalStateException paramInputStream)
      {
        this.mInfoListener.onInfo(this.mMediaPlayer, 901, 0);
      }
    }
  }
  
  public boolean canPause()
  {
    return this.mCanPause;
  }
  
  public boolean canSeekBackward()
  {
    return this.mCanSeekBack;
  }
  
  public boolean canSeekForward()
  {
    return this.mCanSeekForward;
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.mSubtitleWidget != null)
    {
      int i = paramCanvas.save();
      paramCanvas.translate(getPaddingLeft(), getPaddingTop());
      this.mSubtitleWidget.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return VideoView.class.getName();
  }
  
  public int getAudioSessionId()
  {
    if (this.mAudioSession == 0)
    {
      MediaPlayer localMediaPlayer = new MediaPlayer();
      this.mAudioSession = localMediaPlayer.getAudioSessionId();
      localMediaPlayer.release();
    }
    return this.mAudioSession;
  }
  
  public int getBufferPercentage()
  {
    if (this.mMediaPlayer != null) {
      return this.mCurrentBufferPercentage;
    }
    return 0;
  }
  
  public int getCurrentPosition()
  {
    if (isInPlaybackState()) {
      return this.mMediaPlayer.getCurrentPosition();
    }
    return 0;
  }
  
  public int getDuration()
  {
    if (isInPlaybackState()) {
      return this.mMediaPlayer.getDuration();
    }
    return -1;
  }
  
  public Looper getSubtitleLooper()
  {
    return Looper.getMainLooper();
  }
  
  public boolean isPlaying()
  {
    boolean bool;
    if ((isInPlaybackState()) && (this.mMediaPlayer.isPlaying())) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    SubtitleTrack.RenderingWidget localRenderingWidget = this.mSubtitleWidget;
    if (localRenderingWidget != null) {
      localRenderingWidget.onAttachedToWindow();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    SubtitleTrack.RenderingWidget localRenderingWidget = this.mSubtitleWidget;
    if (localRenderingWidget != null) {
      localRenderingWidget.onDetachedFromWindow();
    }
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    int i;
    if ((paramInt != 4) && (paramInt != 24) && (paramInt != 25) && (paramInt != 164) && (paramInt != 82) && (paramInt != 5) && (paramInt != 6)) {
      i = 1;
    } else {
      i = 0;
    }
    if ((isInPlaybackState()) && (i != 0) && (this.mMediaController != null)) {
      if ((paramInt != 79) && (paramInt != 85))
      {
        if (paramInt == 126)
        {
          if (!this.mMediaPlayer.isPlaying())
          {
            start();
            this.mMediaController.hide();
          }
          return true;
        }
        if ((paramInt != 86) && (paramInt != 127))
        {
          toggleMediaControlsVisiblity();
        }
        else
        {
          if (this.mMediaPlayer.isPlaying())
          {
            pause();
            this.mMediaController.show();
          }
          return true;
        }
      }
      else
      {
        if (this.mMediaPlayer.isPlaying())
        {
          pause();
          this.mMediaController.show();
        }
        else
        {
          start();
          this.mMediaController.hide();
        }
        return true;
      }
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mSubtitleWidget != null) {
      measureAndLayoutSubtitleWidget();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getDefaultSize(this.mVideoWidth, paramInt1);
    int j = getDefaultSize(this.mVideoHeight, paramInt2);
    int k = i;
    int m = j;
    if (this.mVideoWidth > 0)
    {
      k = i;
      m = j;
      if (this.mVideoHeight > 0)
      {
        int n = View.MeasureSpec.getMode(paramInt1);
        paramInt1 = View.MeasureSpec.getSize(paramInt1);
        int i1 = View.MeasureSpec.getMode(paramInt2);
        paramInt2 = View.MeasureSpec.getSize(paramInt2);
        if ((n == 1073741824) && (i1 == 1073741824))
        {
          i = this.mVideoWidth;
          j = this.mVideoHeight;
          if (i * paramInt2 < paramInt1 * j)
          {
            k = i * paramInt2 / j;
            m = paramInt2;
          }
          else
          {
            k = paramInt1;
            m = paramInt2;
            if (i * paramInt2 > paramInt1 * j)
            {
              m = j * paramInt1 / i;
              k = paramInt1;
            }
          }
        }
        else if (n == 1073741824)
        {
          i = this.mVideoHeight * paramInt1 / this.mVideoWidth;
          k = paramInt1;
          m = i;
          if (i1 == Integer.MIN_VALUE)
          {
            k = paramInt1;
            m = i;
            if (i > paramInt2)
            {
              k = paramInt1;
              m = paramInt2;
            }
          }
        }
        else if (i1 == 1073741824)
        {
          i = this.mVideoWidth * paramInt2 / this.mVideoHeight;
          k = i;
          m = paramInt2;
          if (n == Integer.MIN_VALUE)
          {
            k = i;
            m = paramInt2;
            if (i > paramInt1)
            {
              k = paramInt1;
              m = paramInt2;
            }
          }
        }
        else
        {
          k = this.mVideoWidth;
          m = this.mVideoHeight;
          j = k;
          i = m;
          if (i1 == Integer.MIN_VALUE)
          {
            j = k;
            i = m;
            if (m > paramInt2)
            {
              j = this.mVideoWidth * paramInt2 / this.mVideoHeight;
              i = paramInt2;
            }
          }
          k = j;
          m = i;
          if (n == Integer.MIN_VALUE)
          {
            k = j;
            m = i;
            if (j > paramInt1)
            {
              m = this.mVideoHeight * paramInt1 / this.mVideoWidth;
              k = paramInt1;
            }
          }
        }
      }
    }
    setMeasuredDimension(k, m);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getAction() == 0) && (isInPlaybackState()) && (this.mMediaController != null)) {
      toggleMediaControlsVisiblity();
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public boolean onTrackballEvent(MotionEvent paramMotionEvent)
  {
    if ((paramMotionEvent.getAction() == 0) && (isInPlaybackState()) && (this.mMediaController != null)) {
      toggleMediaControlsVisiblity();
    }
    return super.onTrackballEvent(paramMotionEvent);
  }
  
  public void pause()
  {
    if ((isInPlaybackState()) && (this.mMediaPlayer.isPlaying()))
    {
      this.mMediaPlayer.pause();
      this.mCurrentState = 4;
    }
    this.mTargetState = 4;
  }
  
  public int resolveAdjustedSize(int paramInt1, int paramInt2)
  {
    return getDefaultSize(paramInt1, paramInt2);
  }
  
  public void resume()
  {
    openVideo();
  }
  
  public void seekTo(int paramInt)
  {
    if (isInPlaybackState())
    {
      this.mMediaPlayer.seekTo(paramInt);
      this.mSeekWhenPrepared = 0;
    }
    else
    {
      this.mSeekWhenPrepared = paramInt;
    }
  }
  
  public void setAudioAttributes(AudioAttributes paramAudioAttributes)
  {
    if (paramAudioAttributes != null)
    {
      this.mAudioAttributes = paramAudioAttributes;
      return;
    }
    throw new IllegalArgumentException("Illegal null AudioAttributes");
  }
  
  public void setAudioFocusRequest(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2) && (paramInt != 3) && (paramInt != 4))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Illegal audio focus type ");
      localStringBuilder.append(paramInt);
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    this.mAudioFocusType = paramInt;
  }
  
  public void setMediaController(MediaController paramMediaController)
  {
    MediaController localMediaController = this.mMediaController;
    if (localMediaController != null) {
      localMediaController.hide();
    }
    this.mMediaController = paramMediaController;
    attachMediaController();
  }
  
  public void setOnCompletionListener(MediaPlayer.OnCompletionListener paramOnCompletionListener)
  {
    this.mOnCompletionListener = paramOnCompletionListener;
  }
  
  public void setOnErrorListener(MediaPlayer.OnErrorListener paramOnErrorListener)
  {
    this.mOnErrorListener = paramOnErrorListener;
  }
  
  public void setOnInfoListener(MediaPlayer.OnInfoListener paramOnInfoListener)
  {
    this.mOnInfoListener = paramOnInfoListener;
  }
  
  public void setOnPreparedListener(MediaPlayer.OnPreparedListener paramOnPreparedListener)
  {
    this.mOnPreparedListener = paramOnPreparedListener;
  }
  
  public void setSubtitleWidget(SubtitleTrack.RenderingWidget paramRenderingWidget)
  {
    if (this.mSubtitleWidget == paramRenderingWidget) {
      return;
    }
    boolean bool = isAttachedToWindow();
    SubtitleTrack.RenderingWidget localRenderingWidget = this.mSubtitleWidget;
    if (localRenderingWidget != null)
    {
      if (bool) {
        localRenderingWidget.onDetachedFromWindow();
      }
      this.mSubtitleWidget.setOnChangedListener(null);
    }
    this.mSubtitleWidget = paramRenderingWidget;
    if (paramRenderingWidget != null)
    {
      if (this.mSubtitlesChangedListener == null) {
        this.mSubtitlesChangedListener = new SubtitleTrack.RenderingWidget.OnChangedListener()
        {
          public void onChanged(SubtitleTrack.RenderingWidget paramAnonymousRenderingWidget)
          {
            VideoView.this.invalidate();
          }
        };
      }
      setWillNotDraw(false);
      paramRenderingWidget.setOnChangedListener(this.mSubtitlesChangedListener);
      if (bool)
      {
        paramRenderingWidget.onAttachedToWindow();
        requestLayout();
      }
    }
    else
    {
      setWillNotDraw(true);
    }
    invalidate();
  }
  
  public void setVideoPath(String paramString)
  {
    setVideoURI(Uri.parse(paramString));
  }
  
  public void setVideoURI(Uri paramUri)
  {
    setVideoURI(paramUri, null);
  }
  
  public void setVideoURI(Uri paramUri, Map<String, String> paramMap)
  {
    this.mUri = paramUri;
    this.mHeaders = paramMap;
    this.mSeekWhenPrepared = 0;
    openVideo();
    requestLayout();
    invalidate();
  }
  
  public void start()
  {
    if (isInPlaybackState())
    {
      this.mMediaPlayer.start();
      this.mCurrentState = 3;
    }
    this.mTargetState = 3;
  }
  
  public void stopPlayback()
  {
    MediaPlayer localMediaPlayer = this.mMediaPlayer;
    if (localMediaPlayer != null)
    {
      localMediaPlayer.stop();
      this.mMediaPlayer.release();
      this.mMediaPlayer = null;
      this.mCurrentState = 0;
      this.mTargetState = 0;
      this.mAudioManager.abandonAudioFocus(null);
    }
  }
  
  public void suspend()
  {
    release(false);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/VideoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */