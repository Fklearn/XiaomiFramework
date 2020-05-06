package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.FrameInfo;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.EventLog;
import android.util.Log;
import android.util.TimeUtils;
import android.view.animation.AnimationUtils;
import java.io.PrintWriter;

public final class Choreographer
{
  public static final int CALLBACK_ANIMATION = 1;
  public static final int CALLBACK_COMMIT = 4;
  public static final int CALLBACK_INPUT = 0;
  public static final int CALLBACK_INSETS_ANIMATION = 2;
  private static final int CALLBACK_LAST = 4;
  private static final String[] CALLBACK_TRACE_TITLES = { "input", "animation", "insets_animation", "traversal", "commit" };
  public static final int CALLBACK_TRAVERSAL = 3;
  private static final boolean DEBUG_FRAMES = false;
  private static final boolean DEBUG_JANK = false;
  private static final long DEFAULT_FRAME_DELAY = 10L;
  private static final Object FRAME_CALLBACK_TOKEN;
  private static final int MOTION_EVENT_ACTION_CANCEL = 3;
  private static final int MOTION_EVENT_ACTION_DOWN = 0;
  private static final int MOTION_EVENT_ACTION_MOVE = 2;
  private static final int MOTION_EVENT_ACTION_UP = 1;
  private static final int MSG_DO_FRAME = 0;
  private static final int MSG_DO_SCHEDULE_CALLBACK = 2;
  private static final int MSG_DO_SCHEDULE_VSYNC = 1;
  private static final boolean OPTS_INPUT = true;
  private static final int SKIPPED_FRAME_WARNING_LIMIT;
  private static final String TAG = "Choreographer";
  private static final boolean USE_FRAME_TIME;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769497L)
  private static final boolean USE_VSYNC;
  private static volatile Choreographer mMainInstance;
  private static volatile long sFrameDelay = 10L;
  private static boolean sIsNextFrameAtFront = false;
  private static final ThreadLocal<Choreographer> sSfThreadInstance;
  private static final ThreadLocal<Choreographer> sThreadInstance = new ThreadLocal()
  {
    protected Choreographer initialValue()
    {
      Looper localLooper = Looper.myLooper();
      if (localLooper != null)
      {
        Choreographer localChoreographer = new Choreographer(localLooper, 0, null);
        if (localLooper == Looper.getMainLooper()) {
          Choreographer.access$102(localChoreographer);
        }
        return localChoreographer;
      }
      throw new IllegalStateException("The current thread must have a looper!");
    }
  };
  private CallbackRecord mCallbackPool;
  @UnsupportedAppUsage
  private final CallbackQueue[] mCallbackQueues;
  private boolean mCallbacksRunning;
  private boolean mConsumedDown = false;
  private boolean mConsumedMove = false;
  private boolean mDebugPrintNextFrameTimeDelta;
  @UnsupportedAppUsage
  private final FrameDisplayEventReceiver mDisplayEventReceiver;
  private int mFPSDivisor = 1;
  FrameInfo mFrameInfo = new FrameInfo();
  @UnsupportedAppUsage
  private long mFrameIntervalNanos;
  private boolean mFrameScheduled;
  private final FrameHandler mHandler;
  private boolean mIsVsyncScheduled = false;
  @UnsupportedAppUsage
  private long mLastFrameTimeNanos;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private final Object mLock = new Object();
  private final Looper mLooper;
  private int mMotionEventType = -1;
  private int mTouchMoveNum = -1;
  
  static
  {
    sSfThreadInstance = new ThreadLocal()
    {
      protected Choreographer initialValue()
      {
        Looper localLooper = Looper.myLooper();
        if (localLooper != null) {
          return new Choreographer(localLooper, 1, null);
        }
        throw new IllegalStateException("The current thread must have a looper!");
      }
    };
    USE_VSYNC = SystemProperties.getBoolean("debug.choreographer.vsync", true);
    USE_FRAME_TIME = SystemProperties.getBoolean("debug.choreographer.frametime", true);
    SKIPPED_FRAME_WARNING_LIMIT = SystemProperties.getInt("debug.choreographer.skipwarning", 30);
    FRAME_CALLBACK_TOKEN = new Object()
    {
      public String toString()
      {
        return "FRAME_CALLBACK_TOKEN";
      }
    };
  }
  
  private Choreographer(Looper paramLooper, int paramInt)
  {
    this.mLooper = paramLooper;
    this.mHandler = new FrameHandler(paramLooper);
    if (USE_VSYNC) {
      paramLooper = new FrameDisplayEventReceiver(paramLooper, paramInt);
    } else {
      paramLooper = null;
    }
    this.mDisplayEventReceiver = paramLooper;
    this.mLastFrameTimeNanos = Long.MIN_VALUE;
    this.mFrameIntervalNanos = ((1.0E9F / getRefreshRate()));
    this.mCallbackQueues = new CallbackQueue[5];
    for (paramInt = 0; paramInt <= 4; paramInt++) {
      this.mCallbackQueues[paramInt] = new CallbackQueue(null);
    }
    setFPSDivisor(SystemProperties.getInt("debug.hwui.fps_divisor", 1));
  }
  
  private void dispose()
  {
    this.mDisplayEventReceiver.dispose();
  }
  
  public static long getFrameDelay()
  {
    return sFrameDelay;
  }
  
  public static Choreographer getInstance()
  {
    return (Choreographer)sThreadInstance.get();
  }
  
  public static Choreographer getMainThreadInstance()
  {
    return mMainInstance;
  }
  
  private static float getRefreshRate()
  {
    return DisplayManagerGlobal.getInstance().getDisplayInfo(0).getMode().getRefreshRate();
  }
  
  @UnsupportedAppUsage
  public static Choreographer getSfInstance()
  {
    return (Choreographer)sSfThreadInstance.get();
  }
  
  private boolean isRunningOnLooperThreadLocked()
  {
    boolean bool;
    if (Looper.myLooper() == this.mLooper) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private CallbackRecord obtainCallbackLocked(long paramLong, Object paramObject1, Object paramObject2)
  {
    CallbackRecord localCallbackRecord = this.mCallbackPool;
    if (localCallbackRecord == null)
    {
      localCallbackRecord = new CallbackRecord(null);
    }
    else
    {
      this.mCallbackPool = localCallbackRecord.next;
      localCallbackRecord.next = null;
    }
    localCallbackRecord.dueTime = paramLong;
    localCallbackRecord.action = paramObject1;
    localCallbackRecord.token = paramObject2;
    return localCallbackRecord;
  }
  
  private void postCallbackDelayedInternal(int paramInt, Object paramObject1, Object paramObject2, long paramLong)
  {
    synchronized (this.mLock)
    {
      long l = SystemClock.uptimeMillis();
      paramLong = l + paramLong;
      this.mCallbackQueues[paramInt].addCallbackLocked(paramLong, paramObject1, paramObject2);
      if ((paramLong > l) && (!sIsNextFrameAtFront))
      {
        paramObject1 = this.mHandler.obtainMessage(2, paramObject1);
        ((Message)paramObject1).arg1 = paramInt;
        ((Message)paramObject1).setAsynchronous(true);
        this.mHandler.sendMessageAtTime((Message)paramObject1, paramLong);
      }
      else
      {
        scheduleFrameLocked(l);
      }
      return;
    }
  }
  
  private void recycleCallbackLocked(CallbackRecord paramCallbackRecord)
  {
    paramCallbackRecord.action = null;
    paramCallbackRecord.token = null;
    paramCallbackRecord.next = this.mCallbackPool;
    this.mCallbackPool = paramCallbackRecord;
  }
  
  public static void releaseInstance()
  {
    Choreographer localChoreographer = (Choreographer)sThreadInstance.get();
    sThreadInstance.remove();
    localChoreographer.dispose();
  }
  
  private void removeCallbacksInternal(int paramInt, Object paramObject1, Object paramObject2)
  {
    synchronized (this.mLock)
    {
      this.mCallbackQueues[paramInt].removeCallbacksLocked(paramObject1, paramObject2);
      if ((paramObject1 != null) && (paramObject2 == null)) {
        this.mHandler.removeMessages(2, paramObject1);
      }
      return;
    }
  }
  
  private void scheduleFrameLocked(long paramLong)
  {
    if (!this.mFrameScheduled)
    {
      this.mFrameScheduled = true;
      if ((!this.mIsVsyncScheduled) && (System.nanoTime() - this.mLastFrameTimeNanos > this.mFrameIntervalNanos))
      {
        Object localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append("scheduleFrameLocked-mMotionEventType:");
        ((StringBuilder)localObject1).append(this.mMotionEventType);
        ((StringBuilder)localObject1).append(" mTouchMoveNum:");
        ((StringBuilder)localObject1).append(this.mTouchMoveNum);
        ((StringBuilder)localObject1).append(" mConsumedDown:");
        ((StringBuilder)localObject1).append(this.mConsumedDown);
        ((StringBuilder)localObject1).append(" mConsumedMove:");
        ((StringBuilder)localObject1).append(this.mConsumedMove);
        Trace.traceBegin(8L, ((StringBuilder)localObject1).toString());
        Trace.traceEnd(8L);
        label268:
        try
        {
          int i = this.mMotionEventType;
          if (i != 0)
          {
            if (i != 1) {
              if (i != 2)
              {
                if (i != 3) {
                  break label268;
                }
              }
              else
              {
                this.mConsumedDown = false;
                if ((this.mTouchMoveNum != 1) || (this.mConsumedMove)) {
                  break label268;
                }
                localObject1 = this.mHandler.obtainMessage(0);
                ((Message)localObject1).setAsynchronous(true);
                this.mHandler.sendMessageAtFrontOfQueue((Message)localObject1);
                this.mConsumedMove = true;
                return;
              }
            }
            this.mConsumedMove = false;
            this.mConsumedDown = false;
          }
          else
          {
            this.mConsumedMove = false;
            if (!this.mConsumedDown)
            {
              localObject1 = this.mHandler.obtainMessage(0);
              ((Message)localObject1).setAsynchronous(true);
              this.mHandler.sendMessageAtFrontOfQueue((Message)localObject1);
              this.mConsumedDown = true;
              return;
            }
          }
        }
        finally {}
      }
      Message localMessage;
      if ((USE_VSYNC) && (!sIsNextFrameAtFront))
      {
        if (isRunningOnLooperThreadLocked())
        {
          scheduleVsyncLocked();
        }
        else
        {
          localMessage = this.mHandler.obtainMessage(1);
          localMessage.setAsynchronous(true);
          this.mHandler.sendMessageAtFrontOfQueue(localMessage);
        }
      }
      else
      {
        paramLong = Math.max(this.mLastFrameTimeNanos / 1000000L + sFrameDelay, paramLong);
        localMessage = this.mHandler.obtainMessage(0);
        localMessage.setAsynchronous(true);
        if (sIsNextFrameAtFront)
        {
          setNextFrameAtFront(false);
          this.mHandler.sendMessageAtFrontOfQueue(localMessage);
        }
        else
        {
          this.mHandler.sendMessageAtTime(localMessage, paramLong);
        }
      }
    }
  }
  
  @UnsupportedAppUsage
  private void scheduleVsyncLocked()
  {
    this.mDisplayEventReceiver.scheduleVsync();
    this.mIsVsyncScheduled = true;
  }
  
  public static void setFrameDelay(long paramLong)
  {
    sFrameDelay = paramLong;
  }
  
  public static void setNextFrameAtFront(boolean paramBoolean)
  {
    if (SystemProperties.getBoolean("persist.sys.miui_optimization", true)) {
      sIsNextFrameAtFront = paramBoolean;
    }
  }
  
  public static long subtractFrameDelay(long paramLong)
  {
    long l = sFrameDelay;
    if (paramLong <= l) {
      paramLong = 0L;
    } else {
      paramLong -= l;
    }
    return paramLong;
  }
  
  void doCallbacks(int paramInt, long paramLong)
  {
    Object localObject2;
    synchronized (this.mLock)
    {
      long l1 = System.nanoTime();
      localObject2 = this.mCallbackQueues[paramInt].extractDueCallbacksLocked(l1 / 1000000L);
      if (localObject2 == null) {
        return;
      }
      this.mCallbacksRunning = true;
      if (paramInt == 4)
      {
        long l2 = l1 - paramLong;
        Trace.traceCounter(8L, "jitterNanos", (int)l2);
        if (l2 >= this.mFrameIntervalNanos * 2L)
        {
          paramLong = this.mFrameIntervalNanos;
          long l3 = this.mFrameIntervalNanos;
          paramLong = l1 - (l2 % paramLong + l3);
        }
      }
    }
    try
    {
      this.mLastFrameTimeNanos = paramLong;
      try
      {
        Trace.traceBegin(8L, CALLBACK_TRACE_TITLES[paramInt]);
        for (??? = localObject2; ??? != null; ??? = ((CallbackRecord)???).next) {
          ((CallbackRecord)???).run(paramLong);
        }
        synchronized (this.mLock)
        {
          this.mCallbacksRunning = false;
          do
          {
            ??? = ((CallbackRecord)localObject2).next;
            recycleCallbackLocked((CallbackRecord)localObject2);
            localObject2 = ???;
          } while (??? != null);
          Trace.traceEnd(8L);
          return;
        }
        Object localObject3;
        localObject5 = finally;
      }
      finally
      {
        synchronized (this.mLock)
        {
          this.mCallbacksRunning = false;
          do
          {
            ??? = localCallbackRecord.next;
            recycleCallbackLocked(localCallbackRecord);
            localObject3 = ???;
          } while (localObject3 != null);
          Trace.traceEnd(8L);
          throw ((Throwable)localObject8);
        }
      }
    }
    finally
    {
      for (;;) {}
    }
    throw ((Throwable)localObject5);
  }
  
  @UnsupportedAppUsage
  void doFrame(long paramLong, int paramInt)
  {
    long l1;
    long l2;
    synchronized (this.mLock)
    {
      this.mIsVsyncScheduled = false;
      if (!this.mFrameScheduled) {
        return;
      }
      l1 = System.nanoTime();
      l2 = l1 - paramLong;
      if (l2 >= this.mFrameIntervalNanos)
      {
        long l3 = l2 / this.mFrameIntervalNanos;
        if (l3 >= SKIPPED_FRAME_WARNING_LIMIT)
        {
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localStringBuilder.append("Skipped ");
          localStringBuilder.append(l3);
          localStringBuilder.append(" frames!  The application may be doing too much work on its main thread.");
          Log.i("Choreographer", localStringBuilder.toString());
          EventLog.writeEvent(30089, l3);
        }
        l3 = this.mFrameIntervalNanos;
        l2 = l1 - l2 % l3;
      }
      else
      {
        l2 = paramLong;
      }
    }
    try
    {
      if (l2 < this.mLastFrameTimeNanos)
      {
        scheduleVsyncLocked();
        return;
      }
      paramInt = this.mFPSDivisor;
      if (paramInt > 1) {
        try
        {
          l1 = l2 - this.mLastFrameTimeNanos;
          if ((l1 < this.mFrameIntervalNanos * this.mFPSDivisor) && (l1 > 0L))
          {
            scheduleVsyncLocked();
            return;
          }
        }
        finally {}
      }
      this.mFrameInfo.setVsync(paramLong, l2);
      this.mFrameScheduled = false;
      this.mLastFrameTimeNanos = l2;
      try
      {
        Trace.traceBegin(8L, "Choreographer#doFrame");
        AnimationUtils.lockAnimationClock(l2 / 1000000L);
        this.mFrameInfo.markInputHandlingStart();
        doCallbacks(0, l2);
        this.mFrameInfo.markAnimationsStart();
        doCallbacks(1, l2);
        doCallbacks(2, l2);
        this.mFrameInfo.markPerformTraversalsStart();
        doCallbacks(3, l2);
        doCallbacks(4, l2);
        return;
      }
      finally
      {
        AnimationUtils.unlockAnimationClock();
        Trace.traceEnd(8L);
      }
      localObject4 = finally;
    }
    finally
    {
      for (;;) {}
    }
    throw ((Throwable)localObject4);
  }
  
  void doScheduleCallback(int paramInt)
  {
    synchronized (this.mLock)
    {
      if (!this.mFrameScheduled)
      {
        long l = SystemClock.uptimeMillis();
        if (this.mCallbackQueues[paramInt].hasDueCallbacksLocked(l)) {
          scheduleFrameLocked(l);
        }
      }
      return;
    }
  }
  
  void doScheduleVsync()
  {
    synchronized (this.mLock)
    {
      if (this.mFrameScheduled) {
        scheduleVsyncLocked();
      }
      return;
    }
  }
  
  void dump(String paramString, PrintWriter paramPrintWriter)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("  ");
    localObject = ((StringBuilder)localObject).toString();
    paramPrintWriter.print(paramString);
    paramPrintWriter.println("Choreographer:");
    paramPrintWriter.print((String)localObject);
    paramPrintWriter.print("mFrameScheduled=");
    paramPrintWriter.println(this.mFrameScheduled);
    paramPrintWriter.print((String)localObject);
    paramPrintWriter.print("mLastFrameTime=");
    paramPrintWriter.println(TimeUtils.formatUptime(this.mLastFrameTimeNanos / 1000000L));
  }
  
  public long getFrameIntervalNanos()
  {
    return this.mFrameIntervalNanos;
  }
  
  @UnsupportedAppUsage
  public long getFrameTime()
  {
    return getFrameTimeNanos() / 1000000L;
  }
  
  @UnsupportedAppUsage
  public long getFrameTimeNanos()
  {
    synchronized (this.mLock)
    {
      if (this.mCallbacksRunning)
      {
        long l;
        if (USE_FRAME_TIME) {
          l = this.mLastFrameTimeNanos;
        } else {
          l = System.nanoTime();
        }
        return l;
      }
      IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
      localIllegalStateException.<init>("This method must only be called as part of a callback while a frame is in progress.");
      throw localIllegalStateException;
    }
  }
  
  public long getLastFrameTimeNanos()
  {
    synchronized (this.mLock)
    {
      long l;
      if (USE_FRAME_TIME) {
        l = this.mLastFrameTimeNanos;
      } else {
        l = System.nanoTime();
      }
      return l;
    }
  }
  
  public void postCallback(int paramInt, Runnable paramRunnable, Object paramObject)
  {
    postCallbackDelayed(paramInt, paramRunnable, paramObject, 0L);
  }
  
  public void postCallbackDelayed(int paramInt, Runnable paramRunnable, Object paramObject, long paramLong)
  {
    if (paramRunnable != null)
    {
      if ((paramInt >= 0) && (paramInt <= 4))
      {
        postCallbackDelayedInternal(paramInt, paramRunnable, paramObject, paramLong);
        return;
      }
      throw new IllegalArgumentException("callbackType is invalid");
    }
    throw new IllegalArgumentException("action must not be null");
  }
  
  public void postFrameCallback(FrameCallback paramFrameCallback)
  {
    postFrameCallbackDelayed(paramFrameCallback, 0L);
  }
  
  public void postFrameCallbackDelayed(FrameCallback paramFrameCallback, long paramLong)
  {
    if (paramFrameCallback != null)
    {
      postCallbackDelayedInternal(1, paramFrameCallback, FRAME_CALLBACK_TOKEN, paramLong);
      return;
    }
    throw new IllegalArgumentException("callback must not be null");
  }
  
  public void removeCallbacks(int paramInt, Runnable paramRunnable, Object paramObject)
  {
    if ((paramInt >= 0) && (paramInt <= 4))
    {
      removeCallbacksInternal(paramInt, paramRunnable, paramObject);
      return;
    }
    throw new IllegalArgumentException("callbackType is invalid");
  }
  
  public void removeFrameCallback(FrameCallback paramFrameCallback)
  {
    if (paramFrameCallback != null)
    {
      removeCallbacksInternal(1, paramFrameCallback, FRAME_CALLBACK_TOKEN);
      return;
    }
    throw new IllegalArgumentException("callback must not be null");
  }
  
  void setFPSDivisor(int paramInt)
  {
    int i = paramInt;
    if (paramInt <= 0) {
      i = 1;
    }
    this.mFPSDivisor = i;
    ThreadedRenderer.setFPSDivisor(i);
  }
  
  public void setMotionEventInfo(int paramInt1, int paramInt2)
  {
    try
    {
      this.mTouchMoveNum = paramInt2;
      this.mMotionEventType = paramInt1;
      return;
    }
    finally {}
  }
  
  private final class CallbackQueue
  {
    private Choreographer.CallbackRecord mHead;
    
    private CallbackQueue() {}
    
    @UnsupportedAppUsage
    public void addCallbackLocked(long paramLong, Object paramObject1, Object paramObject2)
    {
      Choreographer.CallbackRecord localCallbackRecord = Choreographer.this.obtainCallbackLocked(paramLong, paramObject1, paramObject2);
      paramObject2 = this.mHead;
      if (paramObject2 == null)
      {
        this.mHead = localCallbackRecord;
        return;
      }
      paramObject1 = paramObject2;
      if (paramLong < ((Choreographer.CallbackRecord)paramObject2).dueTime)
      {
        localCallbackRecord.next = ((Choreographer.CallbackRecord)paramObject2);
        this.mHead = localCallbackRecord;
        return;
      }
      while (((Choreographer.CallbackRecord)paramObject1).next != null)
      {
        if (paramLong < ((Choreographer.CallbackRecord)paramObject1).next.dueTime)
        {
          localCallbackRecord.next = ((Choreographer.CallbackRecord)paramObject1).next;
          break;
        }
        paramObject1 = ((Choreographer.CallbackRecord)paramObject1).next;
      }
      ((Choreographer.CallbackRecord)paramObject1).next = localCallbackRecord;
    }
    
    public Choreographer.CallbackRecord extractDueCallbacksLocked(long paramLong)
    {
      Choreographer.CallbackRecord localCallbackRecord1 = this.mHead;
      if ((localCallbackRecord1 != null) && (localCallbackRecord1.dueTime <= paramLong))
      {
        Object localObject = localCallbackRecord1;
        for (Choreographer.CallbackRecord localCallbackRecord2 = ((Choreographer.CallbackRecord)localObject).next; localCallbackRecord2 != null; localCallbackRecord2 = localCallbackRecord2.next)
        {
          if (localCallbackRecord2.dueTime > paramLong)
          {
            ((Choreographer.CallbackRecord)localObject).next = null;
            break;
          }
          localObject = localCallbackRecord2;
        }
        this.mHead = localCallbackRecord2;
        return localCallbackRecord1;
      }
      return null;
    }
    
    public boolean hasDueCallbacksLocked(long paramLong)
    {
      Choreographer.CallbackRecord localCallbackRecord = this.mHead;
      boolean bool;
      if ((localCallbackRecord != null) && (localCallbackRecord.dueTime <= paramLong)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void removeCallbacksLocked(Object paramObject1, Object paramObject2)
    {
      Object localObject1 = null;
      Choreographer.CallbackRecord localCallbackRecord;
      for (Object localObject2 = this.mHead; localObject2 != null; localObject2 = localCallbackRecord)
      {
        localCallbackRecord = ((Choreographer.CallbackRecord)localObject2).next;
        if (((paramObject1 != null) && (((Choreographer.CallbackRecord)localObject2).action != paramObject1)) || ((paramObject2 != null) && (((Choreographer.CallbackRecord)localObject2).token != paramObject2)))
        {
          localObject1 = localObject2;
        }
        else
        {
          if (localObject1 != null) {
            ((Choreographer.CallbackRecord)localObject1).next = localCallbackRecord;
          } else {
            this.mHead = localCallbackRecord;
          }
          Choreographer.this.recycleCallbackLocked((Choreographer.CallbackRecord)localObject2);
        }
      }
    }
  }
  
  private static final class CallbackRecord
  {
    public Object action;
    public long dueTime;
    public CallbackRecord next;
    public Object token;
    
    @UnsupportedAppUsage
    public void run(long paramLong)
    {
      if (this.token == Choreographer.FRAME_CALLBACK_TOKEN) {
        ((Choreographer.FrameCallback)this.action).doFrame(paramLong);
      } else {
        ((Runnable)this.action).run();
      }
    }
  }
  
  public static abstract interface FrameCallback
  {
    public abstract void doFrame(long paramLong);
  }
  
  private final class FrameDisplayEventReceiver
    extends DisplayEventReceiver
    implements Runnable
  {
    private int mFrame;
    private boolean mHavePendingVsync;
    private long mTimestampNanos;
    
    public FrameDisplayEventReceiver(Looper paramLooper, int paramInt)
    {
      super(paramInt);
    }
    
    public void onVsync(long paramLong1, long paramLong2, int paramInt)
    {
      long l = System.nanoTime();
      paramLong2 = paramLong1;
      if (paramLong1 > l)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Frame time is ");
        ((StringBuilder)localObject).append((float)(paramLong1 - l) * 1.0E-6F);
        ((StringBuilder)localObject).append(" ms in the future!  Check that graphics HAL is generating vsync timestamps using the correct timebase.");
        Log.w("Choreographer", ((StringBuilder)localObject).toString());
        paramLong2 = l;
      }
      if (this.mHavePendingVsync) {
        Log.w("Choreographer", "Already have a pending vsync event.  There should only be one at a time.");
      } else {
        this.mHavePendingVsync = true;
      }
      this.mTimestampNanos = paramLong2;
      this.mFrame = paramInt;
      Object localObject = Message.obtain(Choreographer.this.mHandler, this);
      ((Message)localObject).setAsynchronous(true);
      if (Choreographer.sIsNextFrameAtFront)
      {
        Choreographer.setNextFrameAtFront(false);
        Choreographer.this.mHandler.sendMessageAtFrontOfQueue((Message)localObject);
      }
      else
      {
        Choreographer.this.mHandler.sendMessageAtTime((Message)localObject, paramLong2 / 1000000L);
      }
    }
    
    public void run()
    {
      this.mHavePendingVsync = false;
      Choreographer.this.doFrame(this.mTimestampNanos, this.mFrame);
    }
  }
  
  private final class FrameHandler
    extends Handler
  {
    public FrameHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      int i = paramMessage.what;
      if (i != 0)
      {
        if (i != 1)
        {
          if (i == 2) {
            Choreographer.this.doScheduleCallback(paramMessage.arg1);
          }
        }
        else {
          Choreographer.this.doScheduleVsync();
        }
      }
      else {
        Choreographer.this.doFrame(System.nanoTime(), 0);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/Choreographer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */