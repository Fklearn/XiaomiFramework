package miui.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.SystemProperties;
import android.telephony.Rlog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;
import com.android.internal.telephony.IccCard;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.uicc.IccRecords;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import miui.app.AlertDialog;
import miui.app.AlertDialog.Builder;

public class MiuiAisSimLockManager
{
  private static final String AIS_MCC_MNC = "52003";
  public static final boolean IS_AIS_SIMLOCK_BUILD = "100".equals(SystemProperties.get("persist.radio.ais_sim_lock", ""));
  private static final String TAG = "MiuiAisSimLockManager";
  private static MiuiAisSimLockManager sInstance;
  private Context mContext;
  private final SimLockBroadCastReceiver mReceiver = new SimLockBroadCastReceiver(null);
  private HashMap<Integer, AlertDialog> mSimDialogs = new HashMap();
  
  private MiuiAisSimLockManager(Context paramContext)
  {
    this.mContext = paramContext;
    paramContext = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
    paramContext.addAction("android.intent.action.AIRPLANE_MODE");
    paramContext.addAction("android.intent.action.BOOT_COMPLETED");
    this.mContext.registerReceiver(this.mReceiver, paramContext);
  }
  
  private void autoCheckAndShowSimLockDialog()
  {
    Iterator localIterator = SubscriptionManager.getDefault().getSubscriptionInfoList().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (SubscriptionInfo)localIterator.next();
      int i = ((SubscriptionInfo)localObject).getSlotId();
      if (((SubscriptionInfo)localObject).getIccId() != null)
      {
        localObject = PhoneFactory.getPhone(i);
        if (isRestricted((Phone)localObject)) {
          showSimLockDialog((Phone)localObject);
        }
      }
    }
  }
  
  private AlertDialog createDialog(final int paramInt)
  {
    Object localObject = String.format(this.mContext.getResources().getString(286130475), new Object[] { Integer.valueOf(paramInt + 1) });
    TextView localTextView = new TextView(this.mContext);
    localTextView.setText(286130474);
    localTextView.setGravity(3);
    localObject = new AlertDialog.Builder(this.mContext, 3).setTitle((CharSequence)localObject).setView(localTextView).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        MiuiAisSimLockManager.this.removeSimDialogs(paramInt);
      }
    }).setCancelable(false).create();
    ((AlertDialog)localObject).getWindow().setType(2003);
    return (AlertDialog)localObject;
  }
  
  public static MiuiAisSimLockManager getInstance()
  {
    return sInstance;
  }
  
  static void init(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new MiuiAisSimLockManager(paramContext);
    }
  }
  
  private void removeSimDialogs(int paramInt)
  {
    AlertDialog localAlertDialog = (AlertDialog)this.mSimDialogs.get(Integer.valueOf(paramInt));
    if (localAlertDialog != null)
    {
      localAlertDialog.dismiss();
      this.mSimDialogs.remove(Integer.valueOf(paramInt));
    }
  }
  
  public boolean isNonAisSimRestricted()
  {
    boolean bool1 = false;
    if (!IS_AIS_SIMLOCK_BUILD) {
      return false;
    }
    int i = 0;
    int j = 0;
    TelephonyManager localTelephonyManager = TelephonyManager.getDefault();
    int k = 0;
    while (k < MiuiTelephony.PHONE_COUNT)
    {
      int m = i;
      int n = j;
      if (localTelephonyManager.hasIccCard(k))
      {
        j++;
        String str = localTelephonyManager.getSimOperatorNumericForPhone(k);
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("mccmnc =");
        localStringBuilder.append(str);
        Rlog.d("MiuiAisSimLockManager", localStringBuilder.toString());
        if (!TextUtils.isEmpty(str))
        {
          m = i;
          n = j;
          if (!TextUtils.isEmpty(str))
          {
            m = i;
            n = j;
            if (str.equals("52003")) {}
          }
        }
        else
        {
          m = i + 1;
          n = j;
        }
      }
      k++;
      i = m;
      j = n;
    }
    boolean bool2;
    if ((j != 1) || (i != 1))
    {
      bool2 = bool1;
      if (j == 2)
      {
        bool2 = bool1;
        if (i != 2) {}
      }
    }
    else
    {
      bool2 = true;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("isRestricted =");
    localStringBuilder.append(bool2);
    localStringBuilder.append(",insertSimCount =");
    localStringBuilder.append(j);
    localStringBuilder.append(",nonAisSimCount=");
    localStringBuilder.append(i);
    Rlog.d("MiuiAisSimLockManager", localStringBuilder.toString());
    return bool2;
  }
  
  public boolean isRestricted(Phone paramPhone)
  {
    boolean bool1 = IS_AIS_SIMLOCK_BUILD;
    boolean bool2 = false;
    if (!bool1)
    {
      Rlog.d("MiuiAisSimLockManager", "isRestricted ais is off");
      return false;
    }
    if ((paramPhone != null) && (paramPhone.getIccCard() != null) && (paramPhone.getIccCard().getIccRecords() != null))
    {
      paramPhone = paramPhone.getIccCard().getIccRecords().getOperatorNumeric();
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("mccmnc :");
      localStringBuilder.append(paramPhone);
      Rlog.d("MiuiAisSimLockManager", localStringBuilder.toString());
      bool1 = bool2;
      if (!TextUtils.isEmpty(paramPhone))
      {
        bool1 = bool2;
        if (!paramPhone.equals("52003")) {
          bool1 = true;
        }
      }
      return bool1;
    }
    return false;
  }
  
  public void showSimLockDialog(Phone paramPhone)
  {
    if (paramPhone == null) {
      return;
    }
    int i = paramPhone.getPhoneId();
    paramPhone = (AlertDialog)this.mSimDialogs.get(Integer.valueOf(i));
    if (paramPhone != null)
    {
      if (paramPhone.isShowing()) {
        return;
      }
      paramPhone.dismiss();
      this.mSimDialogs.remove(Integer.valueOf(i));
    }
    paramPhone = createDialog(i);
    this.mSimDialogs.put(Integer.valueOf(i), paramPhone);
    paramPhone.show();
  }
  
  private final class SimLockBroadCastReceiver
    extends BroadcastReceiver
  {
    private SimLockBroadCastReceiver() {}
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = paramIntent.getAction();
      if ("android.intent.action.SIM_STATE_CHANGED".equals(paramContext))
      {
        paramContext = paramIntent.getStringExtra("ss");
        int i = paramIntent.getIntExtra("slot", -1);
        paramIntent = new StringBuilder();
        paramIntent.append("newIccState:");
        paramIntent.append(paramContext);
        paramIntent.append(",slotId =");
        paramIntent.append(i);
        Rlog.d("MiuiAisSimLockManager", paramIntent.toString());
        if ((i >= 0) && (i < MiuiTelephony.PHONE_COUNT))
        {
          paramIntent = PhoneFactory.getPhone(i);
          if ("ABSENT".equals(paramContext))
          {
            if (paramIntent != null) {
              MiuiAisSimLockManager.this.removeSimDialogs(paramIntent.getPhoneId());
            }
          }
          else if (("LOADED".equals(paramContext)) && (MiuiAisSimLockManager.this.isRestricted(paramIntent))) {
            MiuiAisSimLockManager.this.showSimLockDialog(paramIntent);
          }
        }
      }
      else if ("android.intent.action.AIRPLANE_MODE".equals(paramContext))
      {
        boolean bool = paramIntent.getBooleanExtra("state", false);
        paramContext = new StringBuilder();
        paramContext.append("airplaneModeOn =");
        paramContext.append(bool);
        Rlog.d("MiuiAisSimLockManager", paramContext.toString());
        if (!bool) {
          MiuiAisSimLockManager.this.autoCheckAndShowSimLockDialog();
        }
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/ds/t.jar!/miui/telephony/MiuiAisSimLockManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */