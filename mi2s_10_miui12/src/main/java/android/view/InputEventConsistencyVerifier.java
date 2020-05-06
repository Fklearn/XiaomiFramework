package android.view;

import android.annotation.UnsupportedAppUsage;
import android.os.Build;
import android.util.Log;

public final class InputEventConsistencyVerifier
{
  private static final String EVENT_TYPE_GENERIC_MOTION = "GenericMotionEvent";
  private static final String EVENT_TYPE_KEY = "KeyEvent";
  private static final String EVENT_TYPE_TOUCH = "TouchEvent";
  private static final String EVENT_TYPE_TRACKBALL = "TrackballEvent";
  public static final int FLAG_RAW_DEVICE_INPUT = 1;
  private static final boolean IS_ENG_BUILD = Build.IS_ENG;
  private static final int RECENT_EVENTS_TO_LOG = 5;
  private int mButtonsPressed;
  private final Object mCaller;
  private InputEvent mCurrentEvent;
  private String mCurrentEventType;
  private final int mFlags;
  private boolean mHoverEntered;
  private KeyState mKeyStateList;
  private int mLastEventSeq;
  private String mLastEventType;
  private int mLastNestingLevel;
  private final String mLogTag;
  private int mMostRecentEventIndex;
  private InputEvent[] mRecentEvents;
  private boolean[] mRecentEventsUnhandled;
  private int mTouchEventStreamDeviceId = -1;
  private boolean mTouchEventStreamIsTainted;
  private int mTouchEventStreamPointers;
  private int mTouchEventStreamSource;
  private boolean mTouchEventStreamUnhandled;
  private boolean mTrackballDown;
  private boolean mTrackballUnhandled;
  private StringBuilder mViolationMessage;
  
  @UnsupportedAppUsage
  public InputEventConsistencyVerifier(Object paramObject, int paramInt)
  {
    this(paramObject, paramInt, null);
  }
  
  public InputEventConsistencyVerifier(Object paramObject, int paramInt, String paramString)
  {
    this.mCaller = paramObject;
    this.mFlags = paramInt;
    if (paramString == null) {
      paramString = "InputEventConsistencyVerifier";
    }
    this.mLogTag = paramString;
  }
  
  private void addKeyState(int paramInt1, int paramInt2, int paramInt3)
  {
    KeyState localKeyState = KeyState.obtain(paramInt1, paramInt2, paramInt3);
    localKeyState.next = this.mKeyStateList;
    this.mKeyStateList = localKeyState;
  }
  
  private static void appendEvent(StringBuilder paramStringBuilder, int paramInt, InputEvent paramInputEvent, boolean paramBoolean)
  {
    paramStringBuilder.append(paramInt);
    paramStringBuilder.append(": sent at ");
    paramStringBuilder.append(paramInputEvent.getEventTimeNano());
    paramStringBuilder.append(", ");
    if (paramBoolean) {
      paramStringBuilder.append("(unhandled) ");
    }
    paramStringBuilder.append(paramInputEvent);
  }
  
  private void ensureActionButtonIsNonZeroForThisAction(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionButton() == 0)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("No action button set. Action button should always be non-zero for ");
      localStringBuilder.append(MotionEvent.actionToString(paramMotionEvent.getAction()));
      problem(localStringBuilder.toString());
    }
  }
  
  private void ensureHistorySizeIsZeroForThisAction(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getHistorySize();
    if (i != 0)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("History size is ");
      localStringBuilder.append(i);
      localStringBuilder.append(" but it should always be 0 for ");
      localStringBuilder.append(MotionEvent.actionToString(paramMotionEvent.getAction()));
      problem(localStringBuilder.toString());
    }
  }
  
  private void ensureMetaStateIsNormalized(int paramInt)
  {
    int i = KeyEvent.normalizeMetaState(paramInt);
    if (i != paramInt) {
      problem(String.format("Metastate not normalized.  Was 0x%08x but expected 0x%08x.", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) }));
    }
  }
  
  private void ensurePointerCountIsOneForThisAction(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getPointerCount();
    if (i != 1)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Pointer count is ");
      localStringBuilder.append(i);
      localStringBuilder.append(" but it should always be 1 for ");
      localStringBuilder.append(MotionEvent.actionToString(paramMotionEvent.getAction()));
      problem(localStringBuilder.toString());
    }
  }
  
  private KeyState findKeyState(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    Object localObject = null;
    for (KeyState localKeyState = this.mKeyStateList; localKeyState != null; localKeyState = localKeyState.next)
    {
      if ((localKeyState.deviceId == paramInt1) && (localKeyState.source == paramInt2) && (localKeyState.keyCode == paramInt3))
      {
        if (paramBoolean)
        {
          if (localObject != null) {
            ((KeyState)localObject).next = localKeyState.next;
          } else {
            this.mKeyStateList = localKeyState.next;
          }
          localKeyState.next = null;
        }
        return localKeyState;
      }
      localObject = localKeyState;
    }
    return null;
  }
  
  private void finishEvent()
  {
    Object localObject = this.mViolationMessage;
    if ((localObject != null) && (((StringBuilder)localObject).length() != 0))
    {
      if (!this.mCurrentEvent.isTainted())
      {
        localObject = this.mViolationMessage;
        ((StringBuilder)localObject).append("\n  in ");
        ((StringBuilder)localObject).append(this.mCaller);
        this.mViolationMessage.append("\n  ");
        appendEvent(this.mViolationMessage, 0, this.mCurrentEvent, false);
        if (this.mRecentEvents != null)
        {
          this.mViolationMessage.append("\n  -- recent events --");
          for (i = 0; i < 5; i++)
          {
            int j = (this.mMostRecentEventIndex + 5 - i) % 5;
            localObject = this.mRecentEvents[j];
            if (localObject == null) {
              break;
            }
            this.mViolationMessage.append("\n  ");
            appendEvent(this.mViolationMessage, i + 1, (InputEvent)localObject, this.mRecentEventsUnhandled[j]);
          }
        }
        Log.d(this.mLogTag, this.mViolationMessage.toString());
        this.mCurrentEvent.setTainted(true);
      }
      this.mViolationMessage.setLength(0);
    }
    if (this.mRecentEvents == null)
    {
      this.mRecentEvents = new InputEvent[5];
      this.mRecentEventsUnhandled = new boolean[5];
    }
    int i = (this.mMostRecentEventIndex + 1) % 5;
    this.mMostRecentEventIndex = i;
    localObject = this.mRecentEvents;
    if (localObject[i] != null) {
      localObject[i].recycle();
    }
    this.mRecentEvents[i] = this.mCurrentEvent.copy();
    this.mRecentEventsUnhandled[i] = false;
    this.mCurrentEvent = null;
    this.mCurrentEventType = null;
  }
  
  @UnsupportedAppUsage
  public static boolean isInstrumentationEnabled()
  {
    return IS_ENG_BUILD;
  }
  
  private void problem(String paramString)
  {
    if (this.mViolationMessage == null) {
      this.mViolationMessage = new StringBuilder();
    }
    if (this.mViolationMessage.length() == 0)
    {
      StringBuilder localStringBuilder = this.mViolationMessage;
      localStringBuilder.append(this.mCurrentEventType);
      localStringBuilder.append(": ");
    }
    else
    {
      this.mViolationMessage.append("\n  ");
    }
    this.mViolationMessage.append(paramString);
  }
  
  private boolean startEvent(InputEvent paramInputEvent, int paramInt, String paramString)
  {
    int i = paramInputEvent.getSequenceNumber();
    if ((i == this.mLastEventSeq) && (paramInt < this.mLastNestingLevel) && (paramString == this.mLastEventType)) {
      return false;
    }
    if (paramInt > 0)
    {
      this.mLastEventSeq = i;
      this.mLastEventType = paramString;
      this.mLastNestingLevel = paramInt;
    }
    else
    {
      this.mLastEventSeq = -1;
      this.mLastEventType = null;
      this.mLastNestingLevel = 0;
    }
    this.mCurrentEvent = paramInputEvent;
    this.mCurrentEventType = paramString;
    return true;
  }
  
  public void onGenericMotionEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!startEvent(paramMotionEvent, paramInt, "GenericMotionEvent")) {
      return;
    }
    try
    {
      ensureMetaStateIsNormalized(paramMotionEvent.getMetaState());
      paramInt = paramMotionEvent.getAction();
      int i = paramMotionEvent.getSource();
      int j = paramMotionEvent.getButtonState();
      int k = paramMotionEvent.getActionButton();
      if ((i & 0x2) != 0)
      {
        switch (paramInt)
        {
        default: 
          break;
        case 12: 
          ensureActionButtonIsNonZeroForThisAction(paramMotionEvent);
          if ((this.mButtonsPressed & k) != k)
          {
            paramMotionEvent = new java/lang/StringBuilder;
            paramMotionEvent.<init>();
            paramMotionEvent.append("Action button for ACTION_BUTTON_RELEASE event is ");
            paramMotionEvent.append(k);
            paramMotionEvent.append(", but it was either never pressed or has already been released.");
            problem(paramMotionEvent.toString());
          }
          this.mButtonsPressed &= k;
          if ((k == 32) && ((j & 0x2) == 0)) {
            this.mButtonsPressed &= 0xFFFFFFFD;
          } else if ((k == 64) && ((j & 0x4) == 0)) {
            this.mButtonsPressed &= 0xFFFFFFFB;
          }
          if (this.mButtonsPressed == j) {
            break label513;
          }
          problem(String.format("Reported button state differs from expected button state based on press and release events. Is 0x%08x but expected 0x%08x.", new Object[] { Integer.valueOf(j), Integer.valueOf(this.mButtonsPressed) }));
          break;
        case 11: 
          ensureActionButtonIsNonZeroForThisAction(paramMotionEvent);
          if ((this.mButtonsPressed & k) != 0)
          {
            paramMotionEvent = new java/lang/StringBuilder;
            paramMotionEvent.<init>();
            paramMotionEvent.append("Action button for ACTION_BUTTON_PRESS event is ");
            paramMotionEvent.append(k);
            paramMotionEvent.append(", but it has already been pressed and has yet to be released.");
            problem(paramMotionEvent.toString());
          }
          this.mButtonsPressed |= k;
          if ((k == 32) && ((j & 0x2) != 0)) {
            this.mButtonsPressed |= 0x2;
          } else if ((k == 64) && ((j & 0x4) != 0)) {
            this.mButtonsPressed |= 0x4;
          }
          if (this.mButtonsPressed == j) {
            break label513;
          }
          problem(String.format("Reported button state differs from expected button state based on press and release events. Is 0x%08x but expected 0x%08x.", new Object[] { Integer.valueOf(j), Integer.valueOf(this.mButtonsPressed) }));
          break;
        case 10: 
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
          if (!this.mHoverEntered) {
            problem("ACTION_HOVER_EXIT without prior ACTION_HOVER_ENTER");
          }
          this.mHoverEntered = false;
          break;
        case 9: 
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
          this.mHoverEntered = true;
          break;
        case 8: 
          ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
          break;
        case 7: 
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
          break;
        }
        problem("Invalid action for generic pointer event.");
      }
      else if ((i & 0x10) != 0)
      {
        if (paramInt != 2) {
          problem("Invalid action for generic joystick event.");
        } else {
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
        }
      }
      label513:
      return;
    }
    finally
    {
      finishEvent();
    }
  }
  
  public void onInputEvent(InputEvent paramInputEvent, int paramInt)
  {
    if ((paramInputEvent instanceof KeyEvent))
    {
      onKeyEvent((KeyEvent)paramInputEvent, paramInt);
    }
    else
    {
      paramInputEvent = (MotionEvent)paramInputEvent;
      if (paramInputEvent.isTouchEvent()) {
        onTouchEvent(paramInputEvent, paramInt);
      } else if ((paramInputEvent.getSource() & 0x4) != 0) {
        onTrackballEvent(paramInputEvent, paramInt);
      } else {
        onGenericMotionEvent(paramInputEvent, paramInt);
      }
    }
  }
  
  public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    if (!startEvent(paramKeyEvent, paramInt, "KeyEvent")) {
      return;
    }
    try
    {
      ensureMetaStateIsNormalized(paramKeyEvent.getMetaState());
      int i = paramKeyEvent.getAction();
      int j = paramKeyEvent.getDeviceId();
      int k = paramKeyEvent.getSource();
      paramInt = paramKeyEvent.getKeyCode();
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2)
          {
            paramKeyEvent = new java/lang/StringBuilder;
            paramKeyEvent.<init>();
            paramKeyEvent.append("Invalid action ");
            paramKeyEvent.append(KeyEvent.actionToString(i));
            paramKeyEvent.append(" for key event.");
            problem(paramKeyEvent.toString());
          }
        }
        else
        {
          paramKeyEvent = findKeyState(j, k, paramInt, true);
          if (paramKeyEvent == null) {
            problem("ACTION_UP but key was not down.");
          } else {
            paramKeyEvent.recycle();
          }
        }
      }
      else
      {
        KeyState localKeyState = findKeyState(j, k, paramInt, false);
        if (localKeyState != null)
        {
          if (localKeyState.unhandled) {
            localKeyState.unhandled = false;
          } else if (((0x1 & this.mFlags) == 0) && (paramKeyEvent.getRepeatCount() == 0)) {
            problem("ACTION_DOWN but key is already down and this event is not a key repeat.");
          }
        }
        else {
          addKeyState(j, k, paramInt);
        }
      }
      return;
    }
    finally
    {
      finishEvent();
    }
  }
  
  @UnsupportedAppUsage
  public void onTouchEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!startEvent(paramMotionEvent, paramInt, "TouchEvent")) {
      return;
    }
    int i = paramMotionEvent.getAction();
    if ((i != 0) && (i != 3) && (i != 4)) {
      paramInt = 0;
    } else {
      paramInt = 1;
    }
    if ((paramInt != 0) && ((this.mTouchEventStreamIsTainted) || (this.mTouchEventStreamUnhandled)))
    {
      this.mTouchEventStreamIsTainted = false;
      this.mTouchEventStreamUnhandled = false;
      this.mTouchEventStreamPointers = 0;
    }
    if (this.mTouchEventStreamIsTainted) {
      paramMotionEvent.setTainted(true);
    }
    try
    {
      ensureMetaStateIsNormalized(paramMotionEvent.getMetaState());
      int j = paramMotionEvent.getDeviceId();
      int k = paramMotionEvent.getSource();
      StringBuilder localStringBuilder;
      if ((paramInt == 0) && (this.mTouchEventStreamDeviceId != -1) && ((this.mTouchEventStreamDeviceId != j) || (this.mTouchEventStreamSource != k)))
      {
        localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append("Touch event stream contains events from multiple sources: previous device id ");
        localStringBuilder.append(this.mTouchEventStreamDeviceId);
        localStringBuilder.append(", previous source ");
        localStringBuilder.append(Integer.toHexString(this.mTouchEventStreamSource));
        localStringBuilder.append(", new device id ");
        localStringBuilder.append(j);
        localStringBuilder.append(", new source ");
        localStringBuilder.append(Integer.toHexString(k));
        problem(localStringBuilder.toString());
      }
      this.mTouchEventStreamDeviceId = j;
      this.mTouchEventStreamSource = k;
      paramInt = paramMotionEvent.getPointerCount();
      if ((k & 0x2) != 0)
      {
        if (i != 0)
        {
          if (i != 1)
          {
            if (i != 2)
            {
              if (i != 3)
              {
                if (i != 4)
                {
                  j = paramMotionEvent.getActionMasked();
                  k = paramMotionEvent.getActionIndex();
                  if (j == 5)
                  {
                    if (this.mTouchEventStreamPointers == 0)
                    {
                      problem("ACTION_POINTER_DOWN but no other pointers were down.");
                      this.mTouchEventStreamIsTainted = true;
                    }
                    if ((k >= 0) && (k < paramInt))
                    {
                      i = paramMotionEvent.getPointerId(k);
                      paramInt = 1 << i;
                      if ((this.mTouchEventStreamPointers & paramInt) != 0)
                      {
                        localStringBuilder = new java/lang/StringBuilder;
                        localStringBuilder.<init>();
                        localStringBuilder.append("ACTION_POINTER_DOWN specified pointer id ");
                        localStringBuilder.append(i);
                        localStringBuilder.append(" which is already down.");
                        problem(localStringBuilder.toString());
                        this.mTouchEventStreamIsTainted = true;
                      }
                      else
                      {
                        this.mTouchEventStreamPointers |= paramInt;
                      }
                    }
                    else
                    {
                      localStringBuilder = new java/lang/StringBuilder;
                      localStringBuilder.<init>();
                      localStringBuilder.append("ACTION_POINTER_DOWN index is ");
                      localStringBuilder.append(k);
                      localStringBuilder.append(" but the pointer count is ");
                      localStringBuilder.append(paramInt);
                      localStringBuilder.append(".");
                      problem(localStringBuilder.toString());
                      this.mTouchEventStreamIsTainted = true;
                    }
                    ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
                  }
                  else if (j == 6)
                  {
                    if ((k >= 0) && (k < paramInt))
                    {
                      paramInt = paramMotionEvent.getPointerId(k);
                      i = 1 << paramInt;
                      if ((this.mTouchEventStreamPointers & i) == 0)
                      {
                        localStringBuilder = new java/lang/StringBuilder;
                        localStringBuilder.<init>();
                        localStringBuilder.append("ACTION_POINTER_UP specified pointer id ");
                        localStringBuilder.append(paramInt);
                        localStringBuilder.append(" which is not currently down.");
                        problem(localStringBuilder.toString());
                        this.mTouchEventStreamIsTainted = true;
                      }
                      else
                      {
                        this.mTouchEventStreamPointers &= i;
                      }
                    }
                    else
                    {
                      localStringBuilder = new java/lang/StringBuilder;
                      localStringBuilder.<init>();
                      localStringBuilder.append("ACTION_POINTER_UP index is ");
                      localStringBuilder.append(k);
                      localStringBuilder.append(" but the pointer count is ");
                      localStringBuilder.append(paramInt);
                      localStringBuilder.append(".");
                      problem(localStringBuilder.toString());
                      this.mTouchEventStreamIsTainted = true;
                    }
                    ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
                  }
                  else
                  {
                    paramMotionEvent = new java/lang/StringBuilder;
                    paramMotionEvent.<init>();
                    paramMotionEvent.append("Invalid action ");
                    paramMotionEvent.append(MotionEvent.actionToString(i));
                    paramMotionEvent.append(" for touch event.");
                    problem(paramMotionEvent.toString());
                  }
                }
                else
                {
                  if (this.mTouchEventStreamPointers != 0) {
                    problem("ACTION_OUTSIDE but pointers are still down.");
                  }
                  ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
                  ensurePointerCountIsOneForThisAction(paramMotionEvent);
                  this.mTouchEventStreamIsTainted = false;
                }
              }
              else
              {
                this.mTouchEventStreamPointers = 0;
                this.mTouchEventStreamIsTainted = false;
              }
            }
            else
            {
              i = Integer.bitCount(this.mTouchEventStreamPointers);
              if (paramInt != i)
              {
                paramMotionEvent = new java/lang/StringBuilder;
                paramMotionEvent.<init>();
                paramMotionEvent.append("ACTION_MOVE contained ");
                paramMotionEvent.append(paramInt);
                paramMotionEvent.append(" pointers but there are currently ");
                paramMotionEvent.append(i);
                paramMotionEvent.append(" pointers down.");
                problem(paramMotionEvent.toString());
                this.mTouchEventStreamIsTainted = true;
              }
            }
          }
          else
          {
            ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
            ensurePointerCountIsOneForThisAction(paramMotionEvent);
            this.mTouchEventStreamPointers = 0;
            this.mTouchEventStreamIsTainted = false;
          }
        }
        else
        {
          if (this.mTouchEventStreamPointers != 0) {
            problem("ACTION_DOWN but pointers are already down.  Probably missing ACTION_UP from previous gesture.");
          }
          ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
          this.mTouchEventStreamPointers = (1 << paramMotionEvent.getPointerId(0));
        }
      }
      else {
        problem("Source was not SOURCE_CLASS_POINTER.");
      }
      return;
    }
    finally
    {
      finishEvent();
    }
  }
  
  public void onTrackballEvent(MotionEvent paramMotionEvent, int paramInt)
  {
    if (!startEvent(paramMotionEvent, paramInt, "TrackballEvent")) {
      return;
    }
    try
    {
      ensureMetaStateIsNormalized(paramMotionEvent.getMetaState());
      paramInt = paramMotionEvent.getAction();
      if ((paramMotionEvent.getSource() & 0x4) != 0)
      {
        if (paramInt != 0)
        {
          if (paramInt != 1)
          {
            if (paramInt != 2)
            {
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              localStringBuilder.append("Invalid action ");
              localStringBuilder.append(MotionEvent.actionToString(paramInt));
              localStringBuilder.append(" for trackball event.");
              problem(localStringBuilder.toString());
            }
            else
            {
              ensurePointerCountIsOneForThisAction(paramMotionEvent);
            }
          }
          else
          {
            if (!this.mTrackballDown)
            {
              problem("ACTION_UP but trackball is not down.");
            }
            else
            {
              this.mTrackballDown = false;
              this.mTrackballUnhandled = false;
            }
            ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
            ensurePointerCountIsOneForThisAction(paramMotionEvent);
          }
        }
        else
        {
          if ((this.mTrackballDown) && (!this.mTrackballUnhandled))
          {
            problem("ACTION_DOWN but trackball is already down.");
          }
          else
          {
            this.mTrackballDown = true;
            this.mTrackballUnhandled = false;
          }
          ensureHistorySizeIsZeroForThisAction(paramMotionEvent);
          ensurePointerCountIsOneForThisAction(paramMotionEvent);
        }
        if ((this.mTrackballDown) && (paramMotionEvent.getPressure() <= 0.0F)) {
          problem("Trackball is down but pressure is not greater than 0.");
        } else if ((!this.mTrackballDown) && (paramMotionEvent.getPressure() != 0.0F)) {
          problem("Trackball is up but pressure is not equal to 0.");
        }
      }
      else
      {
        problem("Source was not SOURCE_CLASS_TRACKBALL.");
      }
      return;
    }
    finally
    {
      finishEvent();
    }
  }
  
  @UnsupportedAppUsage
  public void onUnhandledEvent(InputEvent paramInputEvent, int paramInt)
  {
    if (paramInt != this.mLastNestingLevel) {
      return;
    }
    boolean[] arrayOfBoolean = this.mRecentEventsUnhandled;
    if (arrayOfBoolean != null) {
      arrayOfBoolean[this.mMostRecentEventIndex] = true;
    }
    if ((paramInputEvent instanceof KeyEvent))
    {
      paramInputEvent = (KeyEvent)paramInputEvent;
      paramInputEvent = findKeyState(paramInputEvent.getDeviceId(), paramInputEvent.getSource(), paramInputEvent.getKeyCode(), false);
      if (paramInputEvent != null) {
        paramInputEvent.unhandled = true;
      }
    }
    else
    {
      paramInputEvent = (MotionEvent)paramInputEvent;
      if (paramInputEvent.isTouchEvent()) {
        this.mTouchEventStreamUnhandled = true;
      } else if (((paramInputEvent.getSource() & 0x4) != 0) && (this.mTrackballDown)) {
        this.mTrackballUnhandled = true;
      }
    }
  }
  
  public void reset()
  {
    this.mLastEventSeq = -1;
    this.mLastNestingLevel = 0;
    this.mTrackballDown = false;
    this.mTrackballUnhandled = false;
    this.mTouchEventStreamPointers = 0;
    this.mTouchEventStreamIsTainted = false;
    this.mTouchEventStreamUnhandled = false;
    this.mHoverEntered = false;
    this.mButtonsPressed = 0;
    while (this.mKeyStateList != null)
    {
      KeyState localKeyState = this.mKeyStateList;
      this.mKeyStateList = localKeyState.next;
      localKeyState.recycle();
    }
  }
  
  private static final class KeyState
  {
    private static KeyState mRecycledList;
    private static Object mRecycledListLock = new Object();
    public int deviceId;
    public int keyCode;
    public KeyState next;
    public int source;
    public boolean unhandled;
    
    public static KeyState obtain(int paramInt1, int paramInt2, int paramInt3)
    {
      synchronized (mRecycledListLock)
      {
        KeyState localKeyState = mRecycledList;
        if (localKeyState != null) {
          mRecycledList = localKeyState.next;
        } else {
          localKeyState = new KeyState();
        }
        localKeyState.deviceId = paramInt1;
        localKeyState.source = paramInt2;
        localKeyState.keyCode = paramInt3;
        localKeyState.unhandled = false;
        return localKeyState;
      }
    }
    
    public void recycle()
    {
      synchronized (mRecycledListLock)
      {
        this.next = mRecycledList;
        mRecycledList = this.next;
        return;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/InputEventConsistencyVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */