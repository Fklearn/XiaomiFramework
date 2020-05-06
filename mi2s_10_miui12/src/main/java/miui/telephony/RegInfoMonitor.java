package miui.telephony;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.Rlog;
import com.android.internal.telephony.Phone;

public class RegInfoMonitor
{
  private static final boolean DEBUG = SystemProperties.getBoolean("persist.fakecell.settings.test", false);
  private static final String REPORT_CONTROL = "sys.reginfo.control";
  private static final int REPORT_LAC = 1;
  private static final int REPORT_REJECT = 2;
  private static final int RIL_REG_STATE_DENIED = 3;
  private static final int RIL_REG_STATE_DENIED_EMERGENCY_CALL_ENABLED = 13;
  private static final String TAG = "RegInfo";
  private static final RegInfoMonitor[] sInstances = new RegInfoMonitor[MiuiTelephony.PHONE_COUNT];
  private Context mContext;
  private boolean mLacChangedAfterDeny;
  private RegInfoRecord mRegInfo = new RegInfoRecord();
  private int mReportControl;
  private int mSlotId;
  private RegInfoRecord mTempRegInfo = new RegInfoRecord();
  
  private RegInfoMonitor(Context paramContext)
  {
    this.mContext = paramContext;
    this.mLacChangedAfterDeny = false;
  }
  
  public static RegInfoMonitor getInstance(Context paramContext, int paramInt)
  {
    try
    {
      if (sInstances[paramInt] == null)
      {
        RegInfoMonitor[] arrayOfRegInfoMonitor = sInstances;
        RegInfoMonitor localRegInfoMonitor = new miui/telephony/RegInfoMonitor;
        localRegInfoMonitor.<init>(paramContext);
        arrayOfRegInfoMonitor[paramInt] = localRegInfoMonitor;
        sInstances[paramInt].mSlotId = paramInt;
        paramContext = new java/lang/StringBuilder;
        paramContext.<init>();
        paramContext.append("RegInfoMonitor instance created for slot:");
        paramContext.append(paramInt);
        Rlog.d("RegInfo", paramContext.toString());
      }
      paramContext = sInstances[paramInt];
      return paramContext;
    }
    finally {}
  }
  
  private int[] getLacAndCid(CellIdentity paramCellIdentity)
  {
    int[] arrayOfInt = new int[2];
    int[] tmp5_4 = arrayOfInt;
    tmp5_4[0] = -1;
    int[] tmp9_5 = tmp5_4;
    tmp9_5[1] = -1;
    tmp9_5;
    if (paramCellIdentity == null) {
      return arrayOfInt;
    }
    int i = paramCellIdentity.getType();
    if (i != 1)
    {
      if (i != 3)
      {
        if (i != 4)
        {
          if (i == 5)
          {
            arrayOfInt[0] = ((CellIdentityTdscdma)paramCellIdentity).getCid();
            arrayOfInt[1] = ((CellIdentityTdscdma)paramCellIdentity).getLac();
          }
        }
        else
        {
          arrayOfInt[0] = ((CellIdentityWcdma)paramCellIdentity).getCid();
          arrayOfInt[1] = ((CellIdentityWcdma)paramCellIdentity).getLac();
        }
      }
      else
      {
        arrayOfInt[0] = ((CellIdentityLte)paramCellIdentity).getCi();
        arrayOfInt[1] = ((CellIdentityLte)paramCellIdentity).getTac();
      }
    }
    else
    {
      arrayOfInt[0] = ((CellIdentityGsm)paramCellIdentity).getCid();
      arrayOfInt[1] = ((CellIdentityGsm)paramCellIdentity).getLac();
    }
    return arrayOfInt;
  }
  
  public static void notifyRegInfoUpdate(Phone paramPhone, NetworkRegistrationInfo paramNetworkRegistrationInfo)
  {
    getInstance(paramPhone.getContext(), paramPhone.getPhoneId()).onRegInfoUpdate(paramNetworkRegistrationInfo);
  }
  
  public void onRegDenied(int paramInt1, int paramInt2)
  {
    Object localObject;
    if (DEBUG)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("onRegDenied: rejCode = ");
      ((StringBuilder)localObject).append(paramInt1);
      ((StringBuilder)localObject).append(", lac = ");
      ((StringBuilder)localObject).append(paramInt2);
      ((StringBuilder)localObject).append(", last lac = ");
      ((StringBuilder)localObject).append(this.mRegInfo.mLac);
      Rlog.d("RegInfo", ((StringBuilder)localObject).toString());
    }
    RegInfoRecord.access$302(this.mRegInfo, paramInt1);
    RegInfoRecord.access$402(this.mRegInfo, System.currentTimeMillis());
    if (paramInt1 == 0) {
      return;
    }
    if (this.mLacChangedAfterDeny == true)
    {
      localObject = new Intent("miui.action.metok.FALSE_STATION");
      ((Intent)localObject).putExtra("slot", this.mSlotId);
      ((Intent)localObject).putExtra("lac", this.mRegInfo.mLac);
      ((Intent)localObject).putExtra("cid", this.mRegInfo.mCid);
      ((Intent)localObject).putExtra("startTime", this.mRegInfo.mStartTime);
      ((Intent)localObject).putExtra("endTime", this.mRegInfo.mEndTime);
      ((Intent)localObject).putExtra("rejCode", this.mRegInfo.mRejCode);
      ((Intent)localObject).setPackage("com.xiaomi.joyose");
      this.mContext.sendBroadcast((Intent)localObject);
      if (DEBUG)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("network reject on:(");
        ((StringBuilder)localObject).append(this.mRegInfo.toString());
        ((StringBuilder)localObject).append(")");
        Rlog.d("RegInfo", ((StringBuilder)localObject).toString());
      }
      this.mLacChangedAfterDeny = false;
    }
  }
  
  public void onRegInfoUpdate(NetworkRegistrationInfo paramNetworkRegistrationInfo)
  {
    int i = SystemProperties.getInt("sys.reginfo.control", 3);
    this.mReportControl = i;
    if (i == 0) {
      return;
    }
    if (paramNetworkRegistrationInfo == null) {
      return;
    }
    int j = -1;
    int k = -1;
    boolean bool1 = false;
    int m = j;
    try
    {
      localObject = getLacAndCid(paramNetworkRegistrationInfo.getCellIdentity());
      m = j;
      int n = paramNetworkRegistrationInfo.getAccessNetworkTechnology();
      boolean bool2 = false;
      i1 = j;
      i = k;
      if (localObject != null)
      {
        i1 = j;
        i = k;
        m = j;
        if (localObject.length == 2)
        {
          i1 = localObject[0];
          i = localObject[1];
        }
      }
      if (n != 16)
      {
        bool1 = bool2;
        if (n != 2) {}
      }
      else
      {
        bool1 = true;
      }
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("error parsing type: ");
      ((StringBuilder)localObject).append(localNumberFormatException2);
      Rlog.d("RegInfo", ((StringBuilder)localObject).toString());
      i = k;
      i1 = m;
    }
    if ((i == this.mTempRegInfo.mLac) && (i1 != -1) && (i1 != this.mTempRegInfo.mCid))
    {
      RegInfoRecord.access$102(this.mTempRegInfo, i1);
      RegInfoRecord.access$202(this.mTempRegInfo, bool1);
    }
    else if (i != this.mTempRegInfo.mLac)
    {
      this.mTempRegInfo.logLacEnd();
      this.mRegInfo.from(this.mTempRegInfo);
      this.mTempRegInfo.reset();
      this.mTempRegInfo.logLacStart(i, i1, bool1);
      this.mLacChangedAfterDeny = true;
      if ((i != -1) && ((this.mReportControl & 0x1) != 0)) {
        onValidLacChanged(i, i1, bool1);
      }
    }
    int i1 = paramNetworkRegistrationInfo.getRegistrationState();
    if (((0x2 & this.mReportControl) != 0) && ((i1 == 3) || (i1 == 13))) {
      try
      {
        onRegDenied(paramNetworkRegistrationInfo.getRejectCause(), i);
      }
      catch (NumberFormatException localNumberFormatException1)
      {
        paramNetworkRegistrationInfo = new StringBuilder();
        paramNetworkRegistrationInfo.append("error parsing rejCode: ");
        paramNetworkRegistrationInfo.append(localNumberFormatException1);
        Rlog.d("RegInfo", paramNetworkRegistrationInfo.toString());
      }
    }
  }
  
  public void onValidLacChanged(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Object localObject = new Intent("miui.action.metok.LAC_CHANGED");
    ((Intent)localObject).putExtra("slot", this.mSlotId);
    ((Intent)localObject).putExtra("lac", paramInt1);
    ((Intent)localObject).putExtra("cid", paramInt2);
    ((Intent)localObject).putExtra("isGsm", paramBoolean);
    ((Intent)localObject).setPackage("com.xiaomi.joyose");
    this.mContext.sendBroadcast((Intent)localObject);
    if (DEBUG)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("broadcast LAC changed, lac = ");
      ((StringBuilder)localObject).append(paramInt1);
      ((StringBuilder)localObject).append(", cid = ");
      ((StringBuilder)localObject).append(paramInt2);
      ((StringBuilder)localObject).append(", isGsm = ");
      ((StringBuilder)localObject).append(paramBoolean);
      Rlog.d("RegInfo", ((StringBuilder)localObject).toString());
    }
  }
  
  private static class RegInfoRecord
  {
    private static final long INVALID_TIME = -10L;
    private static final int INVALID_VAL = -10;
    private int mCid;
    private long mEndTime;
    private boolean mIsGsm;
    private int mLac;
    private int mRejCode;
    private long mRejTimestamp;
    private long mStartTime;
    
    public RegInfoRecord()
    {
      reset();
    }
    
    public void from(RegInfoRecord paramRegInfoRecord)
    {
      this.mLac = paramRegInfoRecord.mLac;
      this.mCid = paramRegInfoRecord.mCid;
      this.mStartTime = paramRegInfoRecord.mStartTime;
      this.mEndTime = paramRegInfoRecord.mEndTime;
      this.mRejCode = paramRegInfoRecord.mRejCode;
      this.mRejTimestamp = paramRegInfoRecord.mRejTimestamp;
      this.mIsGsm = paramRegInfoRecord.mIsGsm;
    }
    
    public long getLacDuration()
    {
      long l1 = this.mStartTime;
      if (l1 != -10L)
      {
        long l2 = this.mEndTime;
        if (l2 != -10L) {
          return l2 - l1;
        }
      }
      return -10L;
    }
    
    public void logLacEnd()
    {
      this.mEndTime = System.currentTimeMillis();
    }
    
    public void logLacStart(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.mLac = paramInt1;
      this.mCid = paramInt2;
      this.mIsGsm = paramBoolean;
      this.mStartTime = System.currentTimeMillis();
    }
    
    public void reset()
    {
      this.mLac = -10;
      this.mCid = -10;
      this.mRejCode = -10;
      this.mStartTime = -10L;
      this.mEndTime = -10L;
      this.mRejTimestamp = -10L;
      this.mIsGsm = false;
    }
    
    public String toString()
    {
      return String.format("lac = %s, cid = %s, startTime = %s, endTime = %s, regCode = %s,rejTimestamp = %s", new Object[] { Integer.valueOf(this.mLac), Integer.valueOf(this.mCid), Long.valueOf(this.mStartTime), Long.valueOf(this.mEndTime), Integer.valueOf(this.mRejCode), Long.valueOf(this.mRejTimestamp) });
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/RegInfoMonitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */