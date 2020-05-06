package android.view;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.miui.AppOpsUtils;
import android.os.SystemProperties;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import miui.os.Build;

public class ViewRootImplInjector
{
  private static final int AMOTION_EVENT_FLAG_DEBUGINPUT_DETAIL = 4194304;
  private static final int AMOTION_EVENT_FLAG_DEBUGINPUT_MAJAR = 2097152;
  private static final int DEBUG_INPUT_DETAIL = 2;
  private static final int DEBUG_INPUT_MAJAR = 1;
  private static final int DEBUG_INPUT_NO = 0;
  private static final int GESTURE_FINGER_COUNT = 3;
  private static final long LAST_CHECK_GESTURE_FINGER_DOWN_TIME_NO = -1L;
  private static final int LOG_MAX_REPEAT_COUNT = 5;
  private static final Set<String> PACKAGE_ALLOW_DRAW_IF_ANIMATING;
  private static final String PACKAGE_NAME_HOME = "com.miui.home";
  private static final String PACKAGE_NAME_SYSTEMUI = "com.android.systemui";
  private static final String TAG = "ViewRootImpl";
  private static int mDebugInput = 0;
  private static int mMoveCount = 0;
  private static long sLastCancelMotionDownTime = -1L;
  static ComponentName sLauncher;
  
  static
  {
    PACKAGE_ALLOW_DRAW_IF_ANIMATING = new HashSet() {};
    sLauncher = new ComponentName("com.miui.home", "com.miui.home.launcher.Launcher");
  }
  
  static boolean allowDrawIfAnimating(String paramString)
  {
    return PACKAGE_ALLOW_DRAW_IF_ANIMATING.contains(paramString);
  }
  
  public static void checkForThreeGesture(MotionEvent paramMotionEvent)
  {
    if (sLastCancelMotionDownTime == -1L)
    {
      if ((paramMotionEvent.getPointerCount() == 3) && (SystemProperties.getBoolean("sys.miui.screenshot", false)))
      {
        paramMotionEvent.setAction(3);
        sLastCancelMotionDownTime = paramMotionEvent.getDownTime();
        Log.d("ViewRootImpl", "cancle motionEvent because of threeGesture detecting start");
      }
    }
    else
    {
      if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
      {
        sLastCancelMotionDownTime = -1L;
        Log.d("ViewRootImpl", "cancle motionEvent because of threeGesture detecting end");
      }
      paramMotionEvent.setAction(3);
    }
  }
  
  private static void checkTouchInputLevel(int paramInt)
  {
    if ((0x200000 & paramInt) != 0) {
      mDebugInput = 1;
    } else if ((0x400000 & paramInt) != 0) {
      mDebugInput = 2;
    } else {
      mDebugInput = 0;
    }
  }
  
  private static String elementToString(StackTraceElement paramStackTraceElement)
  {
    StringBuilder localStringBuilder = new StringBuilder(80);
    localStringBuilder.append(paramStackTraceElement.getClassName());
    localStringBuilder.append('.');
    localStringBuilder.append(paramStackTraceElement.getMethodName());
    return localStringBuilder.toString();
  }
  
  private static String getMotionStr(MotionEvent paramMotionEvent)
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("[TouchInput][ViewRootImpl] ");
    localStringBuilder1.append("MotionEvent { action=");
    localStringBuilder1.append(MotionEvent.actionToString(paramMotionEvent.getAction()));
    int i = paramMotionEvent.getPointerCount();
    for (int j = 0; j < i; j++)
    {
      StringBuilder localStringBuilder2 = new StringBuilder();
      localStringBuilder2.append(", id[");
      localStringBuilder2.append(j);
      localStringBuilder2.append("]=");
      localStringBuilder1.append(localStringBuilder2.toString());
      localStringBuilder1.append(paramMotionEvent.getPointerId(j));
    }
    localStringBuilder1.append(", pointerCount=");
    localStringBuilder1.append(i);
    localStringBuilder1.append(", eventTime=");
    localStringBuilder1.append(paramMotionEvent.getEventTime());
    localStringBuilder1.append(", downTime=");
    localStringBuilder1.append(paramMotionEvent.getDownTime());
    localStringBuilder1.append(" }");
    return localStringBuilder1.toString();
  }
  
  static void logOnInputEvent(InputEvent paramInputEvent)
  {
    Object localObject;
    int i;
    if ((paramInputEvent instanceof KeyEvent))
    {
      localObject = (KeyEvent)paramInputEvent;
      i = ((KeyEvent)localObject).getKeyCode();
      if ((i != 25) && (i != 24) && (i != 4) && (i != 66))
      {
        int j = mDebugInput;
        if (((j == 1) || (j == 2)) && ((((KeyEvent)localObject).getAction() != 0) || ((i == 26) || (i == 3))))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("[TouchInput][ViewRootImpl] ");
          ((StringBuilder)localObject).append(paramInputEvent.toString());
          Log.d("ViewRootImpl", ((StringBuilder)localObject).toString());
        }
      }
      else if (((KeyEvent)localObject).getRepeatCount() <= 5)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("[TouchInput][ViewRootImpl] ");
        ((StringBuilder)localObject).append(paramInputEvent.toString());
        Log.d("ViewRootImpl", ((StringBuilder)localObject).toString());
      }
    }
    else if ((paramInputEvent instanceof MotionEvent))
    {
      localObject = (MotionEvent)paramInputEvent;
      checkTouchInputLevel(((MotionEvent)localObject).getFlags());
      i = mDebugInput;
      if (i == 1)
      {
        i = ((MotionEvent)localObject).getActionMasked();
        if (i == 2) {
          mMoveCount += 1;
        }
        if ((i == 0) || (i == 1) || (i == 5) || (i == 6))
        {
          paramInputEvent = new StringBuilder();
          paramInputEvent.append(getMotionStr((MotionEvent)localObject));
          paramInputEvent.append(" moveCount:");
          paramInputEvent.append(mMoveCount);
          Log.d("ViewRootImpl", paramInputEvent.toString());
          if (i == 1) {
            mMoveCount = 0;
          }
        }
      }
      else if (i == 2)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("[TouchInput][ViewRootImpl] ");
        ((StringBuilder)localObject).append(paramInputEvent.toString());
        Log.d("ViewRootImpl", ((StringBuilder)localObject).toString());
        return;
      }
    }
  }
  
  static boolean needUpdateWindowState(ViewRootImpl paramViewRootImpl, boolean paramBoolean)
  {
    if ((paramViewRootImpl != null) && (paramBoolean))
    {
      Context localContext = paramViewRootImpl.mContext;
      if (localContext == null) {
        return true;
      }
      paramViewRootImpl = localContext.getPackageName();
      if (sLauncher.getPackageName().equals(paramViewRootImpl))
      {
        paramViewRootImpl = (ActivityManager)localContext.getSystemService("activity");
        try
        {
          paramViewRootImpl = ((ActivityManager.RunningTaskInfo)paramViewRootImpl.getRunningTasks(1).get(0)).topActivity;
          if (paramViewRootImpl != null)
          {
            paramBoolean = sLauncher.getClassName().equals(paramViewRootImpl.getClassName());
            if (paramBoolean) {
              return false;
            }
          }
        }
        catch (Exception paramViewRootImpl) {}
      }
      return true;
    }
    return true;
  }
  
  public static void transformWindowType(View paramView, WindowManager.LayoutParams paramLayoutParams)
  {
    if ((!Build.IS_INTERNATIONAL_BUILD) && (!AppOpsUtils.isXOptMode()))
    {
      if (paramLayoutParams.type == 2005)
      {
        paramView = new ArrayList();
        paramView.add("android.view.ViewRootImplInjector.transformWindowType");
        paramView.add("android.view.ViewRootImpl.setView");
        paramView.add("android.view.WindowManagerGlobal.addView");
        paramView.add("android.view.WindowManagerImpl.addView");
        paramView.add("android.widget.Toast$TN.handleShow");
        try
        {
          Object localObject = new java/lang/Exception;
          ((Exception)localObject).<init>();
          localObject = ((Exception)localObject).getStackTrace();
          if (localObject.length > paramView.size()) {
            for (int i = 0; i < paramView.size(); i++) {
              if (!((String)paramView.get(i)).equals(elementToString(localObject[i])))
              {
                paramLayoutParams.type = 2003;
                return;
              }
            }
          }
        }
        catch (Exception paramView)
        {
          Log.e("ViewRootImpl", " transformWindowTye error ", paramView);
        }
      }
      return;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewRootImplInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */