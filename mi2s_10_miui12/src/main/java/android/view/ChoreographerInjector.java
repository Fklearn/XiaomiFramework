package android.view;

import android.app.ActivityThread;
import android.os.SystemProperties;

public class ChoreographerInjector
{
  private static final boolean DEBUG_TAG = false;
  private static final boolean FRAME_OPTS = SystemProperties.getBoolean("persist.sys.frame_opts", false);
  private static final String MONITOR_PACKAGE = getMonitorPackage();
  private static final String TAG = "ChoreographerInjector";
  private static final double[] increaseCountArr;
  private static double increaseRatio;
  private static int index;
  private static int insertFrameCounter;
  private static final Choreographer mChoreographer = Choreographer.getInstance();
  
  static
  {
    increaseCountArr = new double[] { 0.5D, 0.25D, 0.125D, 1.0D };
    double d = Math.random();
    double[] arrayOfDouble = increaseCountArr;
    index = (int)(d * arrayOfDouble.length);
    increaseRatio = arrayOfDouble[index];
    insertFrameCounter = 0;
  }
  
  private static String getMonitorPackage()
  {
    try
    {
      Object localObject = "乃乏乍与乁乎乔乕乔乕与乡乢久乎乃么乭乁乒之".toCharArray();
      for (int i = 0; i < localObject.length; i++) {
        localObject[i] = ((char)(char)(localObject[i] ^ 0x4E20));
      }
      localObject = new String((char[])localObject);
      return (String)localObject;
    }
    catch (Exception localException) {}
    return "null";
  }
  
  public static void monitorFrame()
  {
    if ((FRAME_OPTS) && (ActivityThread.currentPackageName() != null) && (MONITOR_PACKAGE.equals(ActivityThread.currentPackageName())))
    {
      double d = increaseRatio;
      int i;
      if (d >= 1.0D)
      {
        for (i = 0; i < increaseRatio; i++) {
          mChoreographer.doCallbacks(1, System.nanoTime());
        }
      }
      else if (d > 0.0D)
      {
        i = insertFrameCounter;
        if (i == 1.0D / d - 1.0D)
        {
          mChoreographer.doCallbacks(1, System.nanoTime());
          insertFrameCounter = 0;
          return;
        }
        insertFrameCounter = i + 1;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ChoreographerInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */