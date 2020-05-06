package android.webkit;

import android.util.EventLog;

public class EventLogTags
{
  public static final int BROWSER_DOUBLE_TAP_DURATION = 70102;
  public static final int BROWSER_SNAP_CENTER = 70150;
  public static final int BROWSER_ZOOM_LEVEL_CHANGE = 70101;
  public static final int EXP_DET_ATTEMPT_TO_CALL_OBJECT_GETCLASS = 70151;
  
  public static void writeBrowserDoubleTapDuration(int paramInt, long paramLong)
  {
    EventLog.writeEvent(70102, new Object[] { Integer.valueOf(paramInt), Long.valueOf(paramLong) });
  }
  
  public static void writeBrowserSnapCenter()
  {
    EventLog.writeEvent(70150, new Object[0]);
  }
  
  public static void writeBrowserZoomLevelChange(int paramInt1, int paramInt2, long paramLong)
  {
    EventLog.writeEvent(70101, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Long.valueOf(paramLong) });
  }
  
  public static void writeExpDetAttemptToCallObjectGetclass(String paramString)
  {
    EventLog.writeEvent(70151, paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */