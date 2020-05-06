package org.chromium.arc;

import android.util.EventLog;

public class EventLogTags
{
  public static final int ARC_SYSTEM_EVENT = 300000;
  
  public static void writeArcSystemEvent(String paramString)
  {
    EventLog.writeEvent(300000, paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/chromium/arc/EventLogTags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */