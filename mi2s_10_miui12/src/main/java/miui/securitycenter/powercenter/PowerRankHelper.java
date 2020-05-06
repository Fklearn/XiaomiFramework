package miui.securitycenter.powercenter;

import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import com.android.internal.os.BatterySipper.DrainType;
import com.android.internal.os.BatteryStatsHelper;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PowerRankHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "PowerRankHelper";
  private final List<BatterySipper> mAppUsageList = new ArrayList();
  private Context mContext;
  private BatteryStatsHelper mHelper;
  private double mMaxPower = 1.0D;
  private double mMiscPower;
  private final List<BatterySipper> mMiscUsageList = new ArrayList();
  private final List<BatterySipper> mSystemAppUsageList = new ArrayList();
  private double mTotalPower;
  private UserManager mUm;
  
  public PowerRankHelper(Context paramContext)
  {
    this.mHelper = new BatteryStatsHelper(paramContext);
    this.mHelper.create((Bundle)null);
    this.mUm = ((UserManager)paramContext.getSystemService("user"));
    this.mContext = paramContext;
  }
  
  private void addEntry(BatterySipper paramBatterySipper)
  {
    if (paramBatterySipper.value > this.mMaxPower) {
      this.mMaxPower = paramBatterySipper.value;
    }
    this.mTotalPower += paramBatterySipper.value;
    this.mMiscPower += paramBatterySipper.value;
    this.mMiscUsageList.add(paramBatterySipper);
  }
  
  public List<BatterySipper> getAppUsageList()
  {
    return this.mAppUsageList;
  }
  
  public List<BatterySipper> getMiscUsageList()
  {
    return this.mMiscUsageList;
  }
  
  public double getMiscUsageTotal()
  {
    return this.mMiscPower;
  }
  
  public List<BatterySipper> getSystemAppUsageList()
  {
    return this.mSystemAppUsageList;
  }
  
  public double getUsageTotal()
  {
    return this.mTotalPower;
  }
  
  public void refreshStats()
  {
    this.mHelper.clearStats();
    this.mMaxPower = 0.0D;
    this.mTotalPower = 0.0D;
    this.mMiscPower = 0.0D;
    this.mAppUsageList.clear();
    this.mMiscUsageList.clear();
    this.mSystemAppUsageList.clear();
    this.mHelper.refreshStats(0, -1);
    try
    {
      Iterator localIterator = ((List)this.mHelper.getClass().getDeclaredMethod("getSystemAppUsageList", new Class[0]).invoke(this.mHelper, new Object[0])).iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (com.android.internal.os.BatterySipper)localIterator.next();
        if (((com.android.internal.os.BatterySipper)localObject1).drainType == BatterySipper.DrainType.APP)
        {
          localObject1 = BatterySipperHelper.makeBatterySipperForSystemApp(this.mContext, (com.android.internal.os.BatterySipper)localObject1);
          this.mSystemAppUsageList.add(localObject1);
        }
      }
    }
    catch (Exception localException) {}
    Object localObject1 = this.mHelper.getUsageList();
    BatterySipper localBatterySipper = BatterySipperHelper.makeBatterySipper(this.mContext, 10, null);
    localObject1 = ((List)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      Object localObject2 = (com.android.internal.os.BatterySipper)((Iterator)localObject1).next();
      if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.APP)
      {
        localObject2 = BatterySipperHelper.makeBatterySipper(this.mContext, 6, (com.android.internal.os.BatterySipper)localObject2);
        this.mTotalPower += ((BatterySipper)localObject2).value;
        this.mAppUsageList.add(localObject2);
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.PHONE)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 2, (com.android.internal.os.BatterySipper)localObject2));
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.SCREEN)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 5, (com.android.internal.os.BatterySipper)localObject2));
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.WIFI)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 3, (com.android.internal.os.BatterySipper)localObject2));
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.BLUETOOTH)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 4, (com.android.internal.os.BatterySipper)localObject2));
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.IDLE)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 0, (com.android.internal.os.BatterySipper)localObject2));
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.CELL)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 1, (com.android.internal.os.BatterySipper)localObject2));
      }
      else if (((com.android.internal.os.BatterySipper)localObject2).drainType == BatterySipper.DrainType.AMBIENT_DISPLAY)
      {
        addEntry(BatterySipperHelper.makeBatterySipper(this.mContext, 11, (com.android.internal.os.BatterySipper)localObject2));
      }
      else
      {
        BatterySipperHelper.addBatterySipper(localBatterySipper, (com.android.internal.os.BatterySipper)localObject2);
      }
    }
    if ((localBatterySipper.usageTime > 0L) && (localBatterySipper.value > 0.0D)) {
      addEntry(localBatterySipper);
    }
    if (this.mAppUsageList.size() >= 2) {
      Collections.sort(this.mAppUsageList);
    }
    if (this.mMiscUsageList.size() >= 2) {
      Collections.sort(this.mMiscUsageList);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/PowerRankHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */