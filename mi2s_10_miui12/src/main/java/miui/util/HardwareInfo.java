package miui.util;

import android.os.Process;
import android.util.Log;
import com.miui.daemon.performance.PerfShielderManager;
import com.miui.whetstone.WhetstoneActivityManager;
import java.util.HashMap;
import miui.os.FileUtils;
import miui.os.SystemProperties;

public class HardwareInfo
{
  private static boolean DEBUG_MEMORY_PERFORMANCE = false;
  private static int DEBUG_MEMORY_PERFORMANCE_MASK = 0;
  private static final long GB = 1073741824L;
  private static final long MB = 1048576L;
  private static final String TAG = "HardwareInfo";
  private static HashMap<String, Long> sDevice2Memory;
  private static HashMap<String, Long> sDevice2MemoryAdjust;
  private static long sTotalMemory = 0L;
  private static long sTotalPhysicalMemory;
  
  static
  {
    boolean bool = true;
    DEBUG_MEMORY_PERFORMANCE_MASK = 1;
    if ((android.os.Build.TYPE.equalsIgnoreCase("user")) && ((SystemProperties.getInt("persist.sys.mem_perf_debug", 0) & DEBUG_MEMORY_PERFORMANCE_MASK) == 0)) {
      bool = false;
    }
    DEBUG_MEMORY_PERFORMANCE = bool;
    sDevice2Memory = new HashMap();
    sDevice2Memory.put("hwu9200", Long.valueOf(1073741824L));
    sDevice2Memory.put("hwu9500", Long.valueOf(1073741824L));
    sDevice2Memory.put("maguro", Long.valueOf(1073741824L));
    sDevice2Memory.put("ville", Long.valueOf(1073741824L));
    sDevice2Memory.put("LT26i", Long.valueOf(1073741824L));
    sDevice2Memory.put("ventana", Long.valueOf(1073741824L));
    sDevice2Memory.put("stuttgart", Long.valueOf(1073741824L));
    sDevice2Memory.put("t03g", Long.valueOf(2147483648L));
    sDevice2Memory.put("pisces", Long.valueOf(2147483648L));
    sDevice2Memory.put("HM2014501", Long.valueOf(1073741824L));
    sDevice2Memory.put("leo", Long.valueOf(4294967296L));
    sDevice2Memory.put("HM2014011", Long.valueOf(1073741824L));
    sDevice2Memory.put("HM2013022", Long.valueOf(1073741824L));
    sDevice2Memory.put("HM2013023", Long.valueOf(1073741824L));
    sDevice2MemoryAdjust = new HashMap();
    sDevice2MemoryAdjust.put("lcsh92_wet_xm_td", Long.valueOf(-536870912L));
    sDevice2MemoryAdjust.put("lcsh92_wet_xm_kk", Long.valueOf(-536870912L));
  }
  
  private static long getAndroidCacheMemory()
  {
    long l1 = WhetstoneActivityManager.getAndroidCachedEmptyProcessMemory().longValue();
    if (DEBUG_MEMORY_PERFORMANCE)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("get CacheMemory ");
      localStringBuilder.append(l1);
      localStringBuilder.append("KB");
      Log.i("HardwareInfo", localStringBuilder.toString());
    }
    long l2 = 0L;
    if (l1 > 0L) {
      l2 = l1;
    }
    return l2;
  }
  
  public static long getFreeMemory()
  {
    return PerfShielderManager.getFreeMemory().longValue();
  }
  
  public static long getLowMemoryLimitation()
  {
    try
    {
      String str = FileUtils.readFileAsString("/sys/module/lowmemorykiller/parameters/minfree");
      int i = Integer.parseInt(str.trim().substring(str.lastIndexOf(',') + 1));
      return i * 4 * 1024;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return 0L;
  }
  
  public static long getTotalMemory()
  {
    if (sTotalMemory == 0L) {
      sTotalMemory = Process.getTotalMemory();
    }
    return sTotalMemory;
  }
  
  public static long getTotalPhysicalMemory()
  {
    if (sTotalPhysicalMemory == 0L) {
      if (sDevice2Memory.containsKey(miui.os.Build.DEVICE))
      {
        sTotalPhysicalMemory = ((Long)sDevice2Memory.get(miui.os.Build.DEVICE)).longValue();
      }
      else
      {
        sTotalPhysicalMemory = ((getTotalMemory() / 1024L + 102400L) / 524288L + 1L) * 512L * 1024L * 1024L;
        if (sDevice2MemoryAdjust.containsKey(miui.os.Build.BOARD)) {
          sTotalPhysicalMemory += ((Long)sDevice2MemoryAdjust.get(miui.os.Build.BOARD)).longValue();
        }
      }
    }
    return sTotalPhysicalMemory;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/HardwareInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */