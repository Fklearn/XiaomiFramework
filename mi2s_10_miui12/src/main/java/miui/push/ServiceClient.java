package miui.push;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ServiceClient
{
  private static ServiceClient sInstance;
  private Context mContext;
  
  private ServiceClient(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
  }
  
  private Intent createServiceIntent()
  {
    Intent localIntent = new Intent();
    localIntent.setPackage("com.xiaomi.xmsf");
    localIntent.setClassName("com.xiaomi.xmsf", "com.xiaomi.xmsf.push.service.XMPushService");
    String str = this.mContext.getPackageName();
    localIntent.putExtra(PushConstants.EXTRA_PACKAGE_NAME, str);
    return localIntent;
  }
  
  public static ServiceClient getInstance(Context paramContext)
  {
    if (sInstance == null) {
      sInstance = new ServiceClient(paramContext);
    }
    return sInstance;
  }
  
  private boolean hasNetwork()
  {
    int i = -1;
    Object localObject = (ConnectivityManager)this.mContext.getSystemService("connectivity");
    int j = i;
    if (localObject != null)
    {
      localObject = ((ConnectivityManager)localObject).getActiveNetworkInfo();
      j = i;
      if (localObject != null) {
        j = ((NetworkInfo)localObject).getType();
      }
    }
    boolean bool;
    if (j >= 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  private boolean serviceInstalled()
  {
    Object localObject = this.mContext.getPackageManager();
    try
    {
      localObject = ((PackageManager)localObject).getPackageInfo("com.xiaomi.xmsf", 4);
      return localObject != null;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException) {}
    return false;
  }
  
  public boolean batchSendMessage(Message[] paramArrayOfMessage)
  {
    if (!hasNetwork()) {
      return false;
    }
    Intent localIntent = createServiceIntent();
    Bundle[] arrayOfBundle = new Bundle[paramArrayOfMessage.length];
    for (int i = 0; i < paramArrayOfMessage.length; i++) {
      arrayOfBundle[i] = paramArrayOfMessage[i].toBundle();
    }
    if (arrayOfBundle.length > 0)
    {
      localIntent.setAction(PushConstants.ACTION_BATCH_SEND_MESSAGE);
      localIntent.putExtra("ext_packets", arrayOfBundle);
      this.mContext.startService(localIntent);
      return true;
    }
    return false;
  }
  
  public boolean closeChannel()
  {
    if (!serviceInstalled()) {
      return false;
    }
    Intent localIntent = createServiceIntent();
    localIntent.setAction(PushConstants.ACTION_CLOSE_CHANNEL);
    this.mContext.startService(localIntent);
    return true;
  }
  
  public boolean closeChannel(String paramString)
  {
    if (!serviceInstalled()) {
      return false;
    }
    Intent localIntent = createServiceIntent();
    localIntent.setAction(PushConstants.ACTION_CLOSE_CHANNEL);
    localIntent.putExtra(PushConstants.EXTRA_CHANNEL_ID, paramString);
    this.mContext.startService(localIntent);
    return true;
  }
  
  public boolean closeChannel(String paramString1, String paramString2)
  {
    if (!serviceInstalled()) {
      return false;
    }
    Intent localIntent = createServiceIntent();
    localIntent.setAction(PushConstants.ACTION_CLOSE_CHANNEL);
    localIntent.putExtra(PushConstants.EXTRA_CHANNEL_ID, paramString1);
    localIntent.putExtra(PushConstants.EXTRA_USER_ID, paramString2);
    this.mContext.startService(localIntent);
    return true;
  }
  
  public boolean forceReconnection()
  {
    if ((serviceInstalled()) && (hasNetwork()))
    {
      Intent localIntent = createServiceIntent();
      localIntent.setAction(PushConstants.ACTION_FORCE_RECONNECT);
      this.mContext.startService(localIntent);
      return true;
    }
    return false;
  }
  
  public int openChannel(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean, Map<String, String> paramMap1, Map<String, String> paramMap2)
  {
    if (!serviceInstalled()) {
      return 1;
    }
    Intent localIntent = createServiceIntent();
    localIntent.setAction(PushConstants.ACTION_OPEN_CHANNEL);
    localIntent.putExtra(PushConstants.EXTRA_USER_ID, paramString1);
    localIntent.putExtra(PushConstants.EXTRA_CHANNEL_ID, paramString2);
    localIntent.putExtra(PushConstants.EXTRA_TOKEN, paramString3);
    localIntent.putExtra(PushConstants.EXTRA_SECURITY, paramString5);
    localIntent.putExtra(PushConstants.EXTRA_AUTH_METHOD, paramString4);
    localIntent.putExtra(PushConstants.EXTRA_KICK, paramBoolean);
    int i;
    if (paramMap1 != null)
    {
      paramString3 = new StringBuilder();
      i = 1;
      paramString1 = paramMap1.entrySet().iterator();
      while (paramString1.hasNext())
      {
        paramString2 = (Map.Entry)paramString1.next();
        paramString3.append((String)paramString2.getKey());
        paramString3.append(":");
        paramString3.append((String)paramString2.getValue());
        if (i < paramMap1.size()) {
          paramString3.append(",");
        }
        i++;
      }
      if (!TextUtils.isEmpty(paramString3)) {
        localIntent.putExtra(PushConstants.EXTRA_CLIENT_ATTR, paramString3.toString());
      }
    }
    if (paramMap2 != null)
    {
      paramString2 = new StringBuilder();
      i = 1;
      paramString3 = paramMap2.entrySet().iterator();
      while (paramString3.hasNext())
      {
        paramString1 = (Map.Entry)paramString3.next();
        paramString2.append((String)paramString1.getKey());
        paramString2.append(":");
        paramString2.append((String)paramString1.getValue());
        if (i < paramMap2.size()) {
          paramString2.append(",");
        }
        i++;
      }
      if (!TextUtils.isEmpty(paramString2)) {
        localIntent.putExtra(PushConstants.EXTRA_CLOUD_ATTR, paramString2.toString());
      }
    }
    this.mContext.startService(localIntent);
    return 0;
  }
  
  public void resetConnection()
  {
    if (!serviceInstalled()) {
      return;
    }
    Intent localIntent = createServiceIntent();
    localIntent.setAction(PushConstants.ACTION_RESET_CONNECTION);
    this.mContext.startService(localIntent);
  }
  
  public boolean sendIQ(IQ paramIQ)
  {
    if ((serviceInstalled()) && (hasNetwork()))
    {
      Intent localIntent = createServiceIntent();
      paramIQ = paramIQ.toBundle();
      if (paramIQ != null)
      {
        localIntent.setAction(PushConstants.ACTION_SEND_IQ);
        localIntent.putExtra("ext_packet", paramIQ);
        this.mContext.startService(localIntent);
        return true;
      }
      return false;
    }
    return false;
  }
  
  public boolean sendMessage(Message paramMessage)
  {
    if ((serviceInstalled()) && (hasNetwork()))
    {
      Intent localIntent = createServiceIntent();
      paramMessage = paramMessage.toBundle();
      if (paramMessage != null)
      {
        localIntent.setAction(PushConstants.ACTION_SEND_MESSAGE);
        localIntent.putExtra("ext_packet", paramMessage);
        this.mContext.startService(localIntent);
        return true;
      }
      return false;
    }
    return false;
  }
  
  public boolean sendPresence(Presence paramPresence)
  {
    if ((serviceInstalled()) && (hasNetwork()))
    {
      Intent localIntent = createServiceIntent();
      paramPresence = paramPresence.toBundle();
      if (paramPresence != null)
      {
        localIntent.setAction(PushConstants.ACTION_SEND_PRESENCE);
        localIntent.putExtra("ext_packet", paramPresence);
        this.mContext.startService(localIntent);
        return true;
      }
      return false;
    }
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/ServiceClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */