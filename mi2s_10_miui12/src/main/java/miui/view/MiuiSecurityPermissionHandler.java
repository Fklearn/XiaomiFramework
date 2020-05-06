package miui.view;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.MobileDataUtils;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import miui.os.Build;
import miui.telephony.TelephonyManager;
import org.json.JSONObject;

public class MiuiSecurityPermissionHandler
{
  private static final boolean DEBUG = true;
  private static final int LISTEN_MODE_ACCOUNT = 1;
  private static final int LISTEN_MODE_WIFI = 2;
  private static final int NETWORK_ERROR = -2;
  private static final int PERMISSION_ACCOUNT_WHITELIST = 1;
  private static final int PERMISSION_ERROR = -1;
  private static final int PERMISSION_IMEIACCOUNT_WHITELIST = 3;
  private static final int PERMISSION_IMEI_WHITELIST = 2;
  private static final int POST_VERIFICATION_REQUEST = 0;
  private static final int POST_VERIFICATION_WATER_MARKER = 1;
  private static String TAG = "MiuiPermission";
  private static final int WATERMARKER_ACCOUNT_WHITELIST = 1;
  private static final int WATERMARKER_IMEI_ACCOUNT_WHITELIST = 3;
  private static final int WATERMARKER_IMEI_WHITELIST = 2;
  private static final int WATERMARKER_SHOW = 0;
  private static String sDefaultUrl;
  private static String sGlobalUrl = "https://update.intl.miui.com/updates/mi-vip.php";
  private boolean mBootComplete = false;
  private ContentObserver mContentObserver;
  private final Context mContext;
  private int mMiuiSecurityImeiFlag = 0;
  private boolean mNeedAddAccount = false;
  private boolean mNeedListenAccount = false;
  private boolean mOpenWifiOnce = false;
  private boolean mPermissionListenAccount = false;
  private View mPermissionView;
  private PermissionViewCallback mPermissionViewCallback;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      paramAnonymousContext = paramAnonymousIntent.getAction();
      if (paramAnonymousContext == null) {
        return;
      }
      Object localObject;
      if ("android.net.conn.CONNECTIVITY_CHANGE".equals(paramAnonymousIntent.getAction()))
      {
        paramAnonymousIntent = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
        if ((paramAnonymousIntent != null) && (NetworkInfo.State.CONNECTED == paramAnonymousIntent.getState()) && (paramAnonymousIntent.isAvailable()))
        {
          MiuiSecurityPermissionHandler.this.updateWaterMarkerAccount();
          String str = MiuiSecurityPermissionHandler.TAG;
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append(paramAnonymousIntent.getType());
          ((StringBuilder)localObject).append(" Connected!");
          Log.i(str, ((StringBuilder)localObject).toString());
        }
      }
      try
      {
        if (paramAnonymousContext.equals("miui.intent.action.FINISH_BOOTING"))
        {
          if (MiuiSecurityPermissionHandler.this.mNeedAddAccount) {
            MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onAddAccount();
          }
          if (MiuiSecurityPermissionHandler.this.mPermissionListenAccount) {
            MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onListenAccount(1);
          }
          MiuiSecurityPermissionHandler.access$502(MiuiSecurityPermissionHandler.this, true);
        }
        if (paramAnonymousContext.equals("android.intent.action.ACTION_SHUTDOWN"))
        {
          paramAnonymousContext = MiuiSecurityPermissionHandler.this.loadAccountId();
          if (((MiuiSecurityPermissionHandler.this.mRetPermission != 1) || (paramAnonymousContext == null)) && (MiuiSecurityPermissionHandler.this.mRetPermission != 2) && (MiuiSecurityPermissionHandler.this.mRetPermission != 3))
          {
            Settings.Global.putInt(MiuiSecurityPermissionHandler.this.mContext.getContentResolver(), "miui_account_login_check", 0);
          }
          else
          {
            localObject = MiuiSecurityPermissionHandler.TAG;
            paramAnonymousIntent = new StringBuilder();
            paramAnonymousIntent.append("MIUI_ACCOUNT_LOGIN_CHECK: mRetPermission:");
            paramAnonymousIntent.append(MiuiSecurityPermissionHandler.this.mRetPermission);
            paramAnonymousIntent.append(" account: ");
            paramAnonymousIntent.append(paramAnonymousContext);
            Log.i((String)localObject, paramAnonymousIntent.toString());
            Settings.Global.putInt(MiuiSecurityPermissionHandler.this.mContext.getContentResolver(), "miui_account_login_check", 1);
          }
        }
        return;
      }
      finally {}
    }
  };
  private int mRetPermission = -2;
  private int mRetWater = -2;
  private int responseResult = -2;
  
  static
  {
    sDefaultUrl = "https://update.miui.com/updates/mi-vip.php";
  }
  
  public MiuiSecurityPermissionHandler(Context paramContext, PermissionViewCallback paramPermissionViewCallback)
  {
    this.mContext = paramContext;
    if ((Build.IS_PRIVATE_BUILD) || (Build.IS_PRIVATE_WATER_MARKER))
    {
      registerPermissionViewCallback(paramPermissionViewCallback);
      registerNetWReceiver(paramContext);
      mayBringUpPermissionView();
    }
  }
  
  private void appendImei(OutputStreamWriter paramOutputStreamWriter)
  {
    Object localObject1 = TelephonyManager.getDefault().getImeiList();
    String str = null;
    if ((localObject1 != null) && (((List)localObject1).size() != 0))
    {
      Object localObject2 = hashSHA1((String)((List)localObject1).get(0));
      if (((List)localObject1).size() > 1) {
        str = hashSHA1((String)((List)localObject1).get(1));
      }
      if (localObject2 != null) {
        try
        {
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((StringBuilder)localObject1).append("&imei1=");
          ((StringBuilder)localObject1).append((String)localObject2);
          paramOutputStreamWriter.append(((StringBuilder)localObject1).toString());
        }
        catch (IOException paramOutputStreamWriter)
        {
          break label149;
        }
      }
      if (str != null)
      {
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        ((StringBuilder)localObject2).append("&imei2=");
        ((StringBuilder)localObject2).append(str);
        paramOutputStreamWriter.append(((StringBuilder)localObject2).toString());
        break label156;
        label149:
        paramOutputStreamWriter.printStackTrace();
      }
      label156:
      return;
    }
  }
  
  private String decryptData(String paramString)
  {
    SecretKeySpec localSecretKeySpec = new SecretKeySpec(generateRawKey("ODQ4NWFmYjdhNGE="), "AES");
    try
    {
      Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
      IvParameterSpec localIvParameterSpec = new javax/crypto/spec/IvParameterSpec;
      localIvParameterSpec.<init>("0102030405060708".getBytes());
      localCipher.init(2, localSecretKeySpec, localIvParameterSpec);
      paramString = new String(localCipher.doFinal(Base64.decode(paramString, 0)));
      return paramString;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
    return null;
  }
  
  private void doPermissionView()
  {
    int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "device_provisioned", 0);
    final boolean bool = true;
    if (i != 0) {
      i = 1;
    } else {
      i = 0;
    }
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "miui_account_login_check", 0) == 0) {
      bool = false;
    }
    if (i == 0)
    {
      this.mContentObserver = new ContentObserver(null)
      {
        public void onChange(boolean paramAnonymousBoolean)
        {
          super.onChange(paramAnonymousBoolean);
          this.val$handler.post(new Runnable()
          {
            public void run()
            {
              ContentResolver localContentResolver = MiuiSecurityPermissionHandler.this.mContext.getContentResolver();
              int i = 0;
              if (Settings.Secure.getInt(localContentResolver, "device_provisioned", 0) != 0) {
                i = 1;
              }
              if (i != 0)
              {
                if (!MiuiSecurityPermissionHandler.3.this.val$isLogin) {
                  MiuiSecurityPermissionHandler.this.createPermissionView();
                }
                MiuiSecurityPermissionHandler.this.mContext.getContentResolver().unregisterContentObserver(MiuiSecurityPermissionHandler.this.mContentObserver);
              }
            }
          });
        }
      };
      this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("device_provisioned"), false, this.mContentObserver);
    }
    else
    {
      if (bool) {
        return;
      }
      createPermissionView();
    }
  }
  
  private void doWaterMarker()
  {
    this.mMiuiSecurityImeiFlag = Settings.Global.getInt(this.mContext.getContentResolver(), "miui_permission_check", 0);
    if ((this.mMiuiSecurityImeiFlag & 0x2) != 0) {
      return;
    }
    final Account localAccount = loadAccountId();
    String str;
    if (localAccount == null) {
      str = null;
    } else {
      str = localAccount.name;
    }
    this.mRetWater = postVerificationWaterMarker(str);
    int i = this.mRetWater;
    if ((i == -2) || (i == 0))
    {
      if (Settings.Secure.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0) {
        i = 1;
      } else {
        i = 0;
      }
      if (i == 0)
      {
        this.mContentObserver = new ContentObserver(null)
        {
          public void onChange(boolean paramAnonymousBoolean)
          {
            super.onChange(paramAnonymousBoolean);
            this.val$handler.post(new Runnable()
            {
              public void run()
              {
                Object localObject1 = MiuiSecurityPermissionHandler.this.mContext.getContentResolver();
                int i = 0;
                if (Settings.Secure.getInt((ContentResolver)localObject1, "device_provisioned", 0) != 0) {
                  i = 1;
                }
                if (i != 0) {
                  try
                  {
                    Object localObject3 = MiuiSecurityPermissionHandler.2.this.val$account;
                    localObject1 = localObject3;
                    if (localObject3 == null) {
                      localObject1 = MiuiSecurityPermissionHandler.this.loadAccountId();
                    }
                    localObject3 = MiuiSecurityPermissionHandler.this;
                    MiuiSecurityPermissionHandler localMiuiSecurityPermissionHandler = MiuiSecurityPermissionHandler.this;
                    if (localObject1 == null) {
                      localObject1 = null;
                    } else {
                      localObject1 = ((Account)localObject1).name;
                    }
                    MiuiSecurityPermissionHandler.access$902((MiuiSecurityPermissionHandler)localObject3, localMiuiSecurityPermissionHandler.postVerificationWaterMarker((String)localObject1));
                    if ((-2 == MiuiSecurityPermissionHandler.this.mRetWater) || (MiuiSecurityPermissionHandler.this.mRetWater == 0)) {
                      MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onShowWaterMarker();
                    }
                  }
                  finally {}
                }
              }
            });
          }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("device_provisioned"), false, this.mContentObserver);
      }
    }
    try
    {
      if ((-2 == this.mRetWater) || (this.mRetWater == 0)) {
        this.mPermissionViewCallback.onShowWaterMarker();
      }
      return;
    }
    finally {}
  }
  
  private void enableWifiAndData()
  {
    this.mOpenWifiOnce = true;
    MobileDataUtils.getInstance().enableMobileData(this.mContext, true);
    WifiManager localWifiManager = (WifiManager)this.mContext.getSystemService("wifi");
    if (!localWifiManager.isWifiEnabled()) {
      localWifiManager.setWifiEnabled(true);
    }
  }
  
  private byte[] generateRawKey(String paramString)
  {
    paramString = Base64.decode(paramString.getBytes(), 0);
    if (paramString.length % 8 == 0) {
      return paramString;
    }
    byte[] arrayOfByte = new byte[paramString.length + 8 - paramString.length % 8];
    for (int i = 0; i < paramString.length; i++) {
      arrayOfByte[i] = ((byte)paramString[i]);
    }
    return arrayOfByte;
  }
  
  public static String hashSHA1(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA1");
      localMessageDigest.update(paramString.getBytes());
      paramString = Base64.encodeToString(localMessageDigest.digest(), 8).substring(0, 16);
      return paramString;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      paramString.printStackTrace();
    }
    return null;
  }
  
  private Account loadAccountId()
  {
    Account[] arrayOfAccount = AccountManager.get(this.mContext).getAccountsByType("com.xiaomi");
    if ((arrayOfAccount != null) && (arrayOfAccount.length > 0)) {
      return arrayOfAccount[0];
    }
    return null;
  }
  
  private void onOpenWifiSettingsButtonClicked()
  {
    Intent localIntent = new Intent("android.settings.WIFI_SETTINGS");
    localIntent.setPackage("com.android.settings");
    localIntent.putExtra("extra_show_on_finddevice_keyguard", true);
    localIntent.setFlags(268468224);
    try
    {
      this.mContext.startActivity(localIntent);
    }
    catch (ActivityNotFoundException localActivityNotFoundException)
    {
      String str = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("ActivityNotFoundException: ");
      localStringBuilder.append(localActivityNotFoundException);
      Log.d(str, localStringBuilder.toString());
    }
  }
  
  /* Error */
  private void postVerificationRequest(final View paramView, final Button paramButton, String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_3
    //   2: iconst_0
    //   3: invokespecial 474	miui/view/MiuiSecurityPermissionHandler:postVerificationResult	(Ljava/lang/String;I)V
    //   6: new 24	miui/view/MiuiSecurityPermissionHandler$7
    //   9: dup
    //   10: aload_0
    //   11: aload_2
    //   12: aload_1
    //   13: invokespecial 477	miui/view/MiuiSecurityPermissionHandler$7:<init>	(Lmiui/view/MiuiSecurityPermissionHandler;Landroid/widget/Button;Landroid/view/View;)V
    //   16: astore_1
    //   17: aload_2
    //   18: aload_1
    //   19: invokevirtual 483	android/widget/Button:post	(Ljava/lang/Runnable;)Z
    //   22: pop
    //   23: goto +49 -> 72
    //   26: astore_3
    //   27: goto +46 -> 73
    //   30: astore_3
    //   31: aload_3
    //   32: invokevirtual 301	java/lang/Exception:printStackTrace	()V
    //   35: aload_0
    //   36: getfield 95	miui/view/MiuiSecurityPermissionHandler:mOpenWifiOnce	Z
    //   39: ifne +19 -> 58
    //   42: new 22	miui/view/MiuiSecurityPermissionHandler$6
    //   45: astore_3
    //   46: aload_3
    //   47: aload_0
    //   48: aload_1
    //   49: invokespecial 486	miui/view/MiuiSecurityPermissionHandler$6:<init>	(Lmiui/view/MiuiSecurityPermissionHandler;Landroid/view/View;)V
    //   52: aload_2
    //   53: aload_3
    //   54: invokevirtual 483	android/widget/Button:post	(Ljava/lang/Runnable;)Z
    //   57: pop
    //   58: new 24	miui/view/MiuiSecurityPermissionHandler$7
    //   61: dup
    //   62: aload_0
    //   63: aload_2
    //   64: aload_1
    //   65: invokespecial 477	miui/view/MiuiSecurityPermissionHandler$7:<init>	(Lmiui/view/MiuiSecurityPermissionHandler;Landroid/widget/Button;Landroid/view/View;)V
    //   68: astore_1
    //   69: goto -52 -> 17
    //   72: return
    //   73: aload_2
    //   74: new 24	miui/view/MiuiSecurityPermissionHandler$7
    //   77: dup
    //   78: aload_0
    //   79: aload_2
    //   80: aload_1
    //   81: invokespecial 477	miui/view/MiuiSecurityPermissionHandler$7:<init>	(Lmiui/view/MiuiSecurityPermissionHandler;Landroid/widget/Button;Landroid/view/View;)V
    //   84: invokevirtual 483	android/widget/Button:post	(Ljava/lang/Runnable;)Z
    //   87: pop
    //   88: aload_3
    //   89: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	90	0	this	MiuiSecurityPermissionHandler
    //   0	90	1	paramView	View
    //   0	90	2	paramButton	Button
    //   0	90	3	paramString	String
    // Exception table:
    //   from	to	target	type
    //   0	6	26	finally
    //   31	58	26	finally
    //   0	6	30	java/lang/Exception
  }
  
  /* Error */
  private void postVerificationResult(String paramString, int paramInt)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 5
    //   8: aconst_null
    //   9: astore 6
    //   11: aconst_null
    //   12: astore 7
    //   14: aconst_null
    //   15: astore 8
    //   17: ldc_w 488
    //   20: aconst_null
    //   21: invokestatic 493	android/os/SystemProperties:get	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   24: astore 9
    //   26: aload 9
    //   28: invokestatic 396	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   31: ifeq +11 -> 42
    //   34: getstatic 496	miui/os/Build:DEVICE	Ljava/lang/String;
    //   37: astore 9
    //   39: goto +3 -> 42
    //   42: aload 4
    //   44: astore 10
    //   46: aload 6
    //   48: astore 11
    //   50: aload 8
    //   52: astore 12
    //   54: aload_3
    //   55: astore 13
    //   57: aload 5
    //   59: astore 14
    //   61: aload 7
    //   63: astore 15
    //   65: getstatic 499	miui/os/Build:IS_INTERNATIONAL_BUILD	Z
    //   68: ifeq +65 -> 133
    //   71: aload 4
    //   73: astore 10
    //   75: aload 6
    //   77: astore 11
    //   79: aload 8
    //   81: astore 12
    //   83: aload_3
    //   84: astore 13
    //   86: aload 5
    //   88: astore 14
    //   90: aload 7
    //   92: astore 15
    //   94: new 501	java/net/URL
    //   97: astore 16
    //   99: aload 4
    //   101: astore 10
    //   103: aload 6
    //   105: astore 11
    //   107: aload 8
    //   109: astore 12
    //   111: aload_3
    //   112: astore 13
    //   114: aload 5
    //   116: astore 14
    //   118: aload 7
    //   120: astore 15
    //   122: aload 16
    //   124: getstatic 80	miui/view/MiuiSecurityPermissionHandler:sGlobalUrl	Ljava/lang/String;
    //   127: invokespecial 502	java/net/URL:<init>	(Ljava/lang/String;)V
    //   130: goto +38 -> 168
    //   133: aload 4
    //   135: astore 10
    //   137: aload 6
    //   139: astore 11
    //   141: aload 8
    //   143: astore 12
    //   145: aload_3
    //   146: astore 13
    //   148: aload 5
    //   150: astore 14
    //   152: aload 7
    //   154: astore 15
    //   156: new 501	java/net/URL
    //   159: dup
    //   160: getstatic 84	miui/view/MiuiSecurityPermissionHandler:sDefaultUrl	Ljava/lang/String;
    //   163: invokespecial 502	java/net/URL:<init>	(Ljava/lang/String;)V
    //   166: astore 16
    //   168: aload 4
    //   170: astore 10
    //   172: aload 6
    //   174: astore 11
    //   176: aload 8
    //   178: astore 12
    //   180: aload_3
    //   181: astore 13
    //   183: aload 5
    //   185: astore 14
    //   187: aload 7
    //   189: astore 15
    //   191: aload 16
    //   193: invokevirtual 506	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   196: checkcast 508	java/net/HttpURLConnection
    //   199: astore 8
    //   201: aload 4
    //   203: astore 10
    //   205: aload 6
    //   207: astore 11
    //   209: aload 8
    //   211: astore 12
    //   213: aload_3
    //   214: astore 13
    //   216: aload 5
    //   218: astore 14
    //   220: aload 8
    //   222: astore 15
    //   224: aload 8
    //   226: sipush 5000
    //   229: invokevirtual 512	java/net/HttpURLConnection:setConnectTimeout	(I)V
    //   232: aload 4
    //   234: astore 10
    //   236: aload 6
    //   238: astore 11
    //   240: aload 8
    //   242: astore 12
    //   244: aload_3
    //   245: astore 13
    //   247: aload 5
    //   249: astore 14
    //   251: aload 8
    //   253: astore 15
    //   255: aload 8
    //   257: sipush 5000
    //   260: invokevirtual 515	java/net/HttpURLConnection:setReadTimeout	(I)V
    //   263: aload 4
    //   265: astore 10
    //   267: aload 6
    //   269: astore 11
    //   271: aload 8
    //   273: astore 12
    //   275: aload_3
    //   276: astore 13
    //   278: aload 5
    //   280: astore 14
    //   282: aload 8
    //   284: astore 15
    //   286: aload 8
    //   288: iconst_0
    //   289: invokevirtual 519	java/net/HttpURLConnection:setUseCaches	(Z)V
    //   292: aload 4
    //   294: astore 10
    //   296: aload 6
    //   298: astore 11
    //   300: aload 8
    //   302: astore 12
    //   304: aload_3
    //   305: astore 13
    //   307: aload 5
    //   309: astore 14
    //   311: aload 8
    //   313: astore 15
    //   315: aload 8
    //   317: iconst_1
    //   318: invokevirtual 522	java/net/HttpURLConnection:setDoOutput	(Z)V
    //   321: aload 4
    //   323: astore 10
    //   325: aload 6
    //   327: astore 11
    //   329: aload 8
    //   331: astore 12
    //   333: aload_3
    //   334: astore 13
    //   336: aload 5
    //   338: astore 14
    //   340: aload 8
    //   342: astore 15
    //   344: aload 8
    //   346: ldc_w 524
    //   349: invokevirtual 527	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
    //   352: aload 4
    //   354: astore 10
    //   356: aload 6
    //   358: astore 11
    //   360: aload 8
    //   362: astore 12
    //   364: aload_3
    //   365: astore 13
    //   367: aload 5
    //   369: astore 14
    //   371: aload 8
    //   373: astore 15
    //   375: new 242	java/io/OutputStreamWriter
    //   378: astore 16
    //   380: aload 4
    //   382: astore 10
    //   384: aload 6
    //   386: astore 11
    //   388: aload 8
    //   390: astore 12
    //   392: aload_3
    //   393: astore 13
    //   395: aload 5
    //   397: astore 14
    //   399: aload 8
    //   401: astore 15
    //   403: aload 16
    //   405: aload 8
    //   407: invokevirtual 531	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   410: invokespecial 534	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   413: aload 16
    //   415: astore 10
    //   417: aload 6
    //   419: astore 11
    //   421: aload 8
    //   423: astore 12
    //   425: aload 16
    //   427: astore 13
    //   429: aload 5
    //   431: astore 14
    //   433: aload 8
    //   435: astore 15
    //   437: new 230	java/lang/StringBuilder
    //   440: astore_3
    //   441: aload 16
    //   443: astore 10
    //   445: aload 6
    //   447: astore 11
    //   449: aload 8
    //   451: astore 12
    //   453: aload 16
    //   455: astore 13
    //   457: aload 5
    //   459: astore 14
    //   461: aload 8
    //   463: astore 15
    //   465: aload_3
    //   466: invokespecial 231	java/lang/StringBuilder:<init>	()V
    //   469: aload 16
    //   471: astore 10
    //   473: aload 6
    //   475: astore 11
    //   477: aload 8
    //   479: astore 12
    //   481: aload 16
    //   483: astore 13
    //   485: aload 5
    //   487: astore 14
    //   489: aload 8
    //   491: astore 15
    //   493: aload_3
    //   494: ldc_w 536
    //   497: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   500: pop
    //   501: aload 16
    //   503: astore 10
    //   505: aload 6
    //   507: astore 11
    //   509: aload 8
    //   511: astore 12
    //   513: aload 16
    //   515: astore 13
    //   517: aload 5
    //   519: astore 14
    //   521: aload 8
    //   523: astore 15
    //   525: aload_3
    //   526: aload_1
    //   527: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   530: pop
    //   531: aload 16
    //   533: astore 10
    //   535: aload 6
    //   537: astore 11
    //   539: aload 8
    //   541: astore 12
    //   543: aload 16
    //   545: astore 13
    //   547: aload 5
    //   549: astore 14
    //   551: aload 8
    //   553: astore 15
    //   555: aload_3
    //   556: ldc_w 538
    //   559: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   562: pop
    //   563: aload 16
    //   565: astore 10
    //   567: aload 6
    //   569: astore 11
    //   571: aload 8
    //   573: astore 12
    //   575: aload 16
    //   577: astore 13
    //   579: aload 5
    //   581: astore 14
    //   583: aload 8
    //   585: astore 15
    //   587: aload_3
    //   588: aload 9
    //   590: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   593: pop
    //   594: aload 16
    //   596: astore 10
    //   598: aload 6
    //   600: astore 11
    //   602: aload 8
    //   604: astore 12
    //   606: aload 16
    //   608: astore 13
    //   610: aload 5
    //   612: astore 14
    //   614: aload 8
    //   616: astore 15
    //   618: aload 16
    //   620: aload_3
    //   621: invokevirtual 240	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   624: invokevirtual 541	java/io/OutputStreamWriter:write	(Ljava/lang/String;)V
    //   627: aload 16
    //   629: astore 10
    //   631: aload 6
    //   633: astore 11
    //   635: aload 8
    //   637: astore 12
    //   639: aload 16
    //   641: astore 13
    //   643: aload 5
    //   645: astore 14
    //   647: aload 8
    //   649: astore 15
    //   651: getstatic 88	miui/view/MiuiSecurityPermissionHandler:TAG	Ljava/lang/String;
    //   654: astore_3
    //   655: aload 16
    //   657: astore 10
    //   659: aload 6
    //   661: astore 11
    //   663: aload 8
    //   665: astore 12
    //   667: aload 16
    //   669: astore 13
    //   671: aload 5
    //   673: astore 14
    //   675: aload 8
    //   677: astore 15
    //   679: new 230	java/lang/StringBuilder
    //   682: astore 4
    //   684: aload 16
    //   686: astore 10
    //   688: aload 6
    //   690: astore 11
    //   692: aload 8
    //   694: astore 12
    //   696: aload 16
    //   698: astore 13
    //   700: aload 5
    //   702: astore 14
    //   704: aload 8
    //   706: astore 15
    //   708: aload 4
    //   710: invokespecial 231	java/lang/StringBuilder:<init>	()V
    //   713: aload 16
    //   715: astore 10
    //   717: aload 6
    //   719: astore 11
    //   721: aload 8
    //   723: astore 12
    //   725: aload 16
    //   727: astore 13
    //   729: aload 5
    //   731: astore 14
    //   733: aload 8
    //   735: astore 15
    //   737: aload 4
    //   739: ldc_w 536
    //   742: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   745: pop
    //   746: aload 16
    //   748: astore 10
    //   750: aload 6
    //   752: astore 11
    //   754: aload 8
    //   756: astore 12
    //   758: aload 16
    //   760: astore 13
    //   762: aload 5
    //   764: astore 14
    //   766: aload 8
    //   768: astore 15
    //   770: aload 4
    //   772: aload_1
    //   773: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   776: pop
    //   777: aload 16
    //   779: astore 10
    //   781: aload 6
    //   783: astore 11
    //   785: aload 8
    //   787: astore 12
    //   789: aload 16
    //   791: astore 13
    //   793: aload 5
    //   795: astore 14
    //   797: aload 8
    //   799: astore 15
    //   801: aload 4
    //   803: ldc_w 538
    //   806: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   809: pop
    //   810: aload 16
    //   812: astore 10
    //   814: aload 6
    //   816: astore 11
    //   818: aload 8
    //   820: astore 12
    //   822: aload 16
    //   824: astore 13
    //   826: aload 5
    //   828: astore 14
    //   830: aload 8
    //   832: astore 15
    //   834: aload 4
    //   836: aload 9
    //   838: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   841: pop
    //   842: aload 16
    //   844: astore 10
    //   846: aload 6
    //   848: astore 11
    //   850: aload 8
    //   852: astore 12
    //   854: aload 16
    //   856: astore 13
    //   858: aload 5
    //   860: astore 14
    //   862: aload 8
    //   864: astore 15
    //   866: aload_3
    //   867: aload 4
    //   869: invokevirtual 240	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   872: invokestatic 544	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   875: pop
    //   876: aload 16
    //   878: astore 10
    //   880: aload 6
    //   882: astore 11
    //   884: aload 8
    //   886: astore 12
    //   888: aload 16
    //   890: astore 13
    //   892: aload 5
    //   894: astore 14
    //   896: aload 8
    //   898: astore 15
    //   900: aload_0
    //   901: aload 16
    //   903: invokespecial 546	miui/view/MiuiSecurityPermissionHandler:appendImei	(Ljava/io/OutputStreamWriter;)V
    //   906: aload 16
    //   908: astore 10
    //   910: aload 6
    //   912: astore 11
    //   914: aload 8
    //   916: astore 12
    //   918: aload 16
    //   920: astore 13
    //   922: aload 5
    //   924: astore 14
    //   926: aload 8
    //   928: astore 15
    //   930: aload 16
    //   932: invokevirtual 549	java/io/OutputStreamWriter:flush	()V
    //   935: aload 16
    //   937: astore 10
    //   939: aload 6
    //   941: astore 11
    //   943: aload 8
    //   945: astore 12
    //   947: aload 16
    //   949: astore 13
    //   951: aload 5
    //   953: astore 14
    //   955: aload 8
    //   957: astore 15
    //   959: aload 16
    //   961: invokevirtual 552	java/io/OutputStreamWriter:close	()V
    //   964: aconst_null
    //   965: astore 9
    //   967: aconst_null
    //   968: astore 16
    //   970: aload 16
    //   972: astore 10
    //   974: aload 6
    //   976: astore 11
    //   978: aload 8
    //   980: astore 12
    //   982: aload 9
    //   984: astore 13
    //   986: aload 5
    //   988: astore 14
    //   990: aload 8
    //   992: astore 15
    //   994: aload 8
    //   996: invokevirtual 555	java/net/HttpURLConnection:getResponseCode	()I
    //   999: sipush 200
    //   1002: if_icmpne +620 -> 1622
    //   1005: aload 16
    //   1007: astore 10
    //   1009: aload 6
    //   1011: astore 11
    //   1013: aload 8
    //   1015: astore 12
    //   1017: aload 9
    //   1019: astore 13
    //   1021: aload 5
    //   1023: astore 14
    //   1025: aload 8
    //   1027: astore 15
    //   1029: new 557	java/io/InputStreamReader
    //   1032: astore_1
    //   1033: aload 16
    //   1035: astore 10
    //   1037: aload 6
    //   1039: astore 11
    //   1041: aload 8
    //   1043: astore 12
    //   1045: aload 9
    //   1047: astore 13
    //   1049: aload 5
    //   1051: astore 14
    //   1053: aload 8
    //   1055: astore 15
    //   1057: aload_1
    //   1058: aload 8
    //   1060: invokevirtual 561	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   1063: invokespecial 564	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   1066: aload 16
    //   1068: astore 10
    //   1070: aload_1
    //   1071: astore 11
    //   1073: aload 8
    //   1075: astore 12
    //   1077: aload 9
    //   1079: astore 13
    //   1081: aload_1
    //   1082: astore 14
    //   1084: aload 8
    //   1086: astore 15
    //   1088: sipush 4096
    //   1091: newarray <illegal type>
    //   1093: astore 5
    //   1095: iconst_0
    //   1096: istore 17
    //   1098: aload 16
    //   1100: astore 10
    //   1102: aload_1
    //   1103: astore 11
    //   1105: aload 8
    //   1107: astore 12
    //   1109: aload 9
    //   1111: astore 13
    //   1113: aload_1
    //   1114: astore 14
    //   1116: aload 8
    //   1118: astore 15
    //   1120: aload_1
    //   1121: aload 5
    //   1123: iload 17
    //   1125: sipush 4096
    //   1128: iload 17
    //   1130: isub
    //   1131: invokevirtual 568	java/io/InputStreamReader:read	([CII)I
    //   1134: istore 18
    //   1136: iload 18
    //   1138: iconst_m1
    //   1139: if_icmpeq +13 -> 1152
    //   1142: iload 17
    //   1144: iload 18
    //   1146: iadd
    //   1147: istore 17
    //   1149: goto -51 -> 1098
    //   1152: aload 16
    //   1154: astore 10
    //   1156: aload_1
    //   1157: astore 11
    //   1159: aload 8
    //   1161: astore 12
    //   1163: aload 9
    //   1165: astore 13
    //   1167: aload_1
    //   1168: astore 14
    //   1170: aload 8
    //   1172: astore 15
    //   1174: aload_1
    //   1175: invokevirtual 569	java/io/InputStreamReader:close	()V
    //   1178: aconst_null
    //   1179: astore_1
    //   1180: aconst_null
    //   1181: astore 6
    //   1183: iload_2
    //   1184: iconst_1
    //   1185: if_icmpne +101 -> 1286
    //   1188: aload 16
    //   1190: astore 10
    //   1192: aload 6
    //   1194: astore 11
    //   1196: aload 8
    //   1198: astore 12
    //   1200: aload 9
    //   1202: astore 13
    //   1204: aload_1
    //   1205: astore 14
    //   1207: aload 8
    //   1209: astore 15
    //   1211: new 224	java/lang/String
    //   1214: astore_3
    //   1215: aload 16
    //   1217: astore 10
    //   1219: aload 6
    //   1221: astore 11
    //   1223: aload 8
    //   1225: astore 12
    //   1227: aload 9
    //   1229: astore 13
    //   1231: aload_1
    //   1232: astore 14
    //   1234: aload 8
    //   1236: astore 15
    //   1238: aload_3
    //   1239: aload 5
    //   1241: iconst_0
    //   1242: iload 17
    //   1244: invokespecial 572	java/lang/String:<init>	([CII)V
    //   1247: aload 16
    //   1249: astore 10
    //   1251: aload 6
    //   1253: astore 11
    //   1255: aload 8
    //   1257: astore 12
    //   1259: aload 9
    //   1261: astore 13
    //   1263: aload_1
    //   1264: astore 14
    //   1266: aload 8
    //   1268: astore 15
    //   1270: aload_0
    //   1271: aload_0
    //   1272: aload_0
    //   1273: aload_3
    //   1274: invokespecial 574	miui/view/MiuiSecurityPermissionHandler:decryptData	(Ljava/lang/String;)Ljava/lang/String;
    //   1277: invokespecial 577	miui/view/MiuiSecurityPermissionHandler:processWatermarResult	(Ljava/lang/String;)I
    //   1280: putfield 111	miui/view/MiuiSecurityPermissionHandler:responseResult	I
    //   1283: goto +339 -> 1622
    //   1286: iload_2
    //   1287: ifne +335 -> 1622
    //   1290: aload 16
    //   1292: astore 10
    //   1294: aload 6
    //   1296: astore 11
    //   1298: aload 8
    //   1300: astore 12
    //   1302: aload 9
    //   1304: astore 13
    //   1306: aload_1
    //   1307: astore 14
    //   1309: aload 8
    //   1311: astore 15
    //   1313: new 224	java/lang/String
    //   1316: astore_3
    //   1317: aload 16
    //   1319: astore 10
    //   1321: aload 6
    //   1323: astore 11
    //   1325: aload 8
    //   1327: astore 12
    //   1329: aload 9
    //   1331: astore 13
    //   1333: aload_1
    //   1334: astore 14
    //   1336: aload 8
    //   1338: astore 15
    //   1340: aload_3
    //   1341: aload 5
    //   1343: iconst_0
    //   1344: iload 17
    //   1346: invokespecial 572	java/lang/String:<init>	([CII)V
    //   1349: aload 16
    //   1351: astore 10
    //   1353: aload 6
    //   1355: astore 11
    //   1357: aload 8
    //   1359: astore 12
    //   1361: aload 9
    //   1363: astore 13
    //   1365: aload_1
    //   1366: astore 14
    //   1368: aload 8
    //   1370: astore 15
    //   1372: aload_0
    //   1373: aload_0
    //   1374: aload_3
    //   1375: invokespecial 574	miui/view/MiuiSecurityPermissionHandler:decryptData	(Ljava/lang/String;)Ljava/lang/String;
    //   1378: invokespecial 580	miui/view/MiuiSecurityPermissionHandler:processResult	(Ljava/lang/String;)V
    //   1381: aload 16
    //   1383: astore 10
    //   1385: aload 6
    //   1387: astore 11
    //   1389: aload 8
    //   1391: astore 12
    //   1393: aload 9
    //   1395: astore 13
    //   1397: aload_1
    //   1398: astore 14
    //   1400: aload 8
    //   1402: astore 15
    //   1404: getstatic 88	miui/view/MiuiSecurityPermissionHandler:TAG	Ljava/lang/String;
    //   1407: astore 5
    //   1409: aload 16
    //   1411: astore 10
    //   1413: aload 6
    //   1415: astore 11
    //   1417: aload 8
    //   1419: astore 12
    //   1421: aload 9
    //   1423: astore 13
    //   1425: aload_1
    //   1426: astore 14
    //   1428: aload 8
    //   1430: astore 15
    //   1432: new 230	java/lang/StringBuilder
    //   1435: astore_3
    //   1436: aload 16
    //   1438: astore 10
    //   1440: aload 6
    //   1442: astore 11
    //   1444: aload 8
    //   1446: astore 12
    //   1448: aload 9
    //   1450: astore 13
    //   1452: aload_1
    //   1453: astore 14
    //   1455: aload 8
    //   1457: astore 15
    //   1459: aload_3
    //   1460: invokespecial 231	java/lang/StringBuilder:<init>	()V
    //   1463: aload 16
    //   1465: astore 10
    //   1467: aload 6
    //   1469: astore 11
    //   1471: aload 8
    //   1473: astore 12
    //   1475: aload 9
    //   1477: astore 13
    //   1479: aload_1
    //   1480: astore 14
    //   1482: aload 8
    //   1484: astore 15
    //   1486: aload_3
    //   1487: ldc_w 582
    //   1490: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1493: pop
    //   1494: aload 16
    //   1496: astore 10
    //   1498: aload 6
    //   1500: astore 11
    //   1502: aload 8
    //   1504: astore 12
    //   1506: aload 9
    //   1508: astore 13
    //   1510: aload_1
    //   1511: astore 14
    //   1513: aload 8
    //   1515: astore 15
    //   1517: aload_3
    //   1518: aload_0
    //   1519: getfield 103	miui/view/MiuiSecurityPermissionHandler:mRetPermission	I
    //   1522: invokevirtual 585	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1525: pop
    //   1526: aload 16
    //   1528: astore 10
    //   1530: aload 6
    //   1532: astore 11
    //   1534: aload 8
    //   1536: astore 12
    //   1538: aload 9
    //   1540: astore 13
    //   1542: aload_1
    //   1543: astore 14
    //   1545: aload 8
    //   1547: astore 15
    //   1549: aload_3
    //   1550: ldc_w 587
    //   1553: invokevirtual 237	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1556: pop
    //   1557: aload 16
    //   1559: astore 10
    //   1561: aload 6
    //   1563: astore 11
    //   1565: aload 8
    //   1567: astore 12
    //   1569: aload 9
    //   1571: astore 13
    //   1573: aload_1
    //   1574: astore 14
    //   1576: aload 8
    //   1578: astore 15
    //   1580: aload_3
    //   1581: aload_0
    //   1582: getfield 101	miui/view/MiuiSecurityPermissionHandler:mRetWater	I
    //   1585: invokevirtual 585	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   1588: pop
    //   1589: aload 16
    //   1591: astore 10
    //   1593: aload 6
    //   1595: astore 11
    //   1597: aload 8
    //   1599: astore 12
    //   1601: aload 9
    //   1603: astore 13
    //   1605: aload_1
    //   1606: astore 14
    //   1608: aload 8
    //   1610: astore 15
    //   1612: aload 5
    //   1614: aload_3
    //   1615: invokevirtual 240	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1618: invokestatic 544	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1621: pop
    //   1622: iconst_0
    //   1623: ifeq +16 -> 1639
    //   1626: new 589	java/lang/NullPointerException
    //   1629: dup
    //   1630: invokespecial 590	java/lang/NullPointerException:<init>	()V
    //   1633: athrow
    //   1634: astore_1
    //   1635: aload_1
    //   1636: invokevirtual 250	java/io/IOException:printStackTrace	()V
    //   1639: iconst_0
    //   1640: ifeq +16 -> 1656
    //   1643: new 589	java/lang/NullPointerException
    //   1646: dup
    //   1647: invokespecial 590	java/lang/NullPointerException:<init>	()V
    //   1650: athrow
    //   1651: astore_1
    //   1652: aload_1
    //   1653: invokevirtual 250	java/io/IOException:printStackTrace	()V
    //   1656: aload 8
    //   1658: invokevirtual 593	java/net/HttpURLConnection:disconnect	()V
    //   1661: return
    //   1662: astore_1
    //   1663: goto +18 -> 1681
    //   1666: astore_1
    //   1667: aload 13
    //   1669: astore 10
    //   1671: aload 14
    //   1673: astore 11
    //   1675: aload 15
    //   1677: astore 12
    //   1679: aload_1
    //   1680: athrow
    //   1681: aload 10
    //   1683: ifnull +18 -> 1701
    //   1686: aload 10
    //   1688: invokevirtual 552	java/io/OutputStreamWriter:close	()V
    //   1691: goto +10 -> 1701
    //   1694: astore 10
    //   1696: aload 10
    //   1698: invokevirtual 250	java/io/IOException:printStackTrace	()V
    //   1701: aload 11
    //   1703: ifnull +18 -> 1721
    //   1706: aload 11
    //   1708: invokevirtual 569	java/io/InputStreamReader:close	()V
    //   1711: goto +10 -> 1721
    //   1714: astore 11
    //   1716: aload 11
    //   1718: invokevirtual 250	java/io/IOException:printStackTrace	()V
    //   1721: aload 12
    //   1723: ifnull +8 -> 1731
    //   1726: aload 12
    //   1728: invokevirtual 593	java/net/HttpURLConnection:disconnect	()V
    //   1731: aload_1
    //   1732: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1733	0	this	MiuiSecurityPermissionHandler
    //   0	1733	1	paramString	String
    //   0	1733	2	paramInt	int
    //   1	1614	3	localObject1	Object
    //   3	865	4	localStringBuilder	StringBuilder
    //   6	1607	5	localObject2	Object
    //   9	1585	6	localObject3	Object
    //   12	176	7	localObject4	Object
    //   15	1642	8	localHttpURLConnection	java.net.HttpURLConnection
    //   24	1578	9	str	String
    //   44	1643	10	localObject5	Object
    //   1694	3	10	localIOException1	IOException
    //   48	1659	11	localObject6	Object
    //   1714	3	11	localIOException2	IOException
    //   52	1675	12	localObject7	Object
    //   55	1613	13	localObject8	Object
    //   59	1613	14	localObject9	Object
    //   63	1613	15	localObject10	Object
    //   97	1493	16	localObject11	Object
    //   1096	249	17	i	int
    //   1134	13	18	j	int
    // Exception table:
    //   from	to	target	type
    //   1626	1634	1634	java/io/IOException
    //   1643	1651	1651	java/io/IOException
    //   65	71	1662	finally
    //   94	99	1662	finally
    //   122	130	1662	finally
    //   156	168	1662	finally
    //   191	201	1662	finally
    //   224	232	1662	finally
    //   255	263	1662	finally
    //   286	292	1662	finally
    //   315	321	1662	finally
    //   344	352	1662	finally
    //   375	380	1662	finally
    //   403	413	1662	finally
    //   437	441	1662	finally
    //   465	469	1662	finally
    //   493	501	1662	finally
    //   525	531	1662	finally
    //   555	563	1662	finally
    //   587	594	1662	finally
    //   618	627	1662	finally
    //   651	655	1662	finally
    //   679	684	1662	finally
    //   708	713	1662	finally
    //   737	746	1662	finally
    //   770	777	1662	finally
    //   801	810	1662	finally
    //   834	842	1662	finally
    //   866	876	1662	finally
    //   900	906	1662	finally
    //   930	935	1662	finally
    //   959	964	1662	finally
    //   994	1005	1662	finally
    //   1029	1033	1662	finally
    //   1057	1066	1662	finally
    //   1088	1095	1662	finally
    //   1120	1136	1662	finally
    //   1174	1178	1662	finally
    //   1211	1215	1662	finally
    //   1238	1247	1662	finally
    //   1270	1283	1662	finally
    //   1313	1317	1662	finally
    //   1340	1349	1662	finally
    //   1372	1381	1662	finally
    //   1404	1409	1662	finally
    //   1432	1436	1662	finally
    //   1459	1463	1662	finally
    //   1486	1494	1662	finally
    //   1517	1526	1662	finally
    //   1549	1557	1662	finally
    //   1580	1589	1662	finally
    //   1612	1622	1662	finally
    //   1679	1681	1662	finally
    //   65	71	1666	java/lang/Exception
    //   94	99	1666	java/lang/Exception
    //   122	130	1666	java/lang/Exception
    //   156	168	1666	java/lang/Exception
    //   191	201	1666	java/lang/Exception
    //   224	232	1666	java/lang/Exception
    //   255	263	1666	java/lang/Exception
    //   286	292	1666	java/lang/Exception
    //   315	321	1666	java/lang/Exception
    //   344	352	1666	java/lang/Exception
    //   375	380	1666	java/lang/Exception
    //   403	413	1666	java/lang/Exception
    //   437	441	1666	java/lang/Exception
    //   465	469	1666	java/lang/Exception
    //   493	501	1666	java/lang/Exception
    //   525	531	1666	java/lang/Exception
    //   555	563	1666	java/lang/Exception
    //   587	594	1666	java/lang/Exception
    //   618	627	1666	java/lang/Exception
    //   651	655	1666	java/lang/Exception
    //   679	684	1666	java/lang/Exception
    //   708	713	1666	java/lang/Exception
    //   737	746	1666	java/lang/Exception
    //   770	777	1666	java/lang/Exception
    //   801	810	1666	java/lang/Exception
    //   834	842	1666	java/lang/Exception
    //   866	876	1666	java/lang/Exception
    //   900	906	1666	java/lang/Exception
    //   930	935	1666	java/lang/Exception
    //   959	964	1666	java/lang/Exception
    //   994	1005	1666	java/lang/Exception
    //   1029	1033	1666	java/lang/Exception
    //   1057	1066	1666	java/lang/Exception
    //   1088	1095	1666	java/lang/Exception
    //   1120	1136	1666	java/lang/Exception
    //   1174	1178	1666	java/lang/Exception
    //   1211	1215	1666	java/lang/Exception
    //   1238	1247	1666	java/lang/Exception
    //   1270	1283	1666	java/lang/Exception
    //   1313	1317	1666	java/lang/Exception
    //   1340	1349	1666	java/lang/Exception
    //   1372	1381	1666	java/lang/Exception
    //   1404	1409	1666	java/lang/Exception
    //   1432	1436	1666	java/lang/Exception
    //   1459	1463	1666	java/lang/Exception
    //   1486	1494	1666	java/lang/Exception
    //   1517	1526	1666	java/lang/Exception
    //   1549	1557	1666	java/lang/Exception
    //   1580	1589	1666	java/lang/Exception
    //   1612	1622	1666	java/lang/Exception
    //   1686	1691	1694	java/io/IOException
    //   1706	1711	1714	java/io/IOException
  }
  
  private int postVerificationWaterMarker(String paramString)
  {
    try
    {
      postVerificationResult(paramString, 1);
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
    return this.responseResult;
  }
  
  private void processMiuiSecurityImeiFlag(int paramInt1, int paramInt2)
  {
    if (this.mMiuiSecurityImeiFlag != 3)
    {
      if ((paramInt1 == 2) || (paramInt1 == 3)) {
        this.mMiuiSecurityImeiFlag |= 0x1;
      }
      if ((paramInt2 == 2) || (paramInt2 == 3)) {
        this.mMiuiSecurityImeiFlag = (0x2 | this.mMiuiSecurityImeiFlag);
      }
      Settings.Global.putInt(this.mContext.getContentResolver(), "miui_permission_check", this.mMiuiSecurityImeiFlag);
    }
  }
  
  private void processResult(String paramString)
  {
    try
    {
      String str = TAG;
      Object localObject = new java/lang/StringBuilder;
      ((StringBuilder)localObject).<init>();
      ((StringBuilder)localObject).append("response:");
      ((StringBuilder)localObject).append(paramString);
      Log.i(str, ((StringBuilder)localObject).toString());
      localObject = new org/json/JSONObject;
      ((JSONObject)localObject).<init>(paramString);
      int i = ((JSONObject)localObject).getInt("Auth");
      int j = ((JSONObject)localObject).getInt("Watermark");
      this.mRetPermission = i;
      this.mRetWater = j;
      processMiuiSecurityImeiFlag(i, j);
      return;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  private int processWatermarResult(String paramString)
  {
    try
    {
      Object localObject = TAG;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localStringBuilder.append("response:");
      localStringBuilder.append(paramString);
      Log.i((String)localObject, localStringBuilder.toString());
      localObject = new org/json/JSONObject;
      ((JSONObject)localObject).<init>(paramString);
      int i = ((JSONObject)localObject).getInt("Auth");
      int j = ((JSONObject)localObject).getInt("Watermark");
      processMiuiSecurityImeiFlag(i, j);
      return j;
    }
    catch (Exception paramString)
    {
      paramString.printStackTrace();
    }
    return -2;
  }
  
  private void registerNetWReceiver(Context paramContext)
  {
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.setPriority(1000);
    localIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    localIntentFilter.addAction("miui.intent.action.FINISH_BOOTING");
    localIntentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
    paramContext.registerReceiver(this.mReceiver, localIntentFilter);
  }
  
  private void updateWaterMarkerAccount()
  {
    new Thread()
    {
      public void run()
      {
        try
        {
          int i = 10;
          for (;;)
          {
            if (i > 0) {}
            try
            {
              MiuiSecurityPermissionHandler localMiuiSecurityPermissionHandler = MiuiSecurityPermissionHandler.this;
              Object localObject1 = MiuiSecurityPermissionHandler.this;
              Object localObject2;
              if (this.val$account == null) {
                localObject2 = null;
              } else {
                localObject2 = this.val$account.name;
              }
              MiuiSecurityPermissionHandler.access$902(localMiuiSecurityPermissionHandler, ((MiuiSecurityPermissionHandler)localObject1).postVerificationWaterMarker((String)localObject2));
              if (-2 == MiuiSecurityPermissionHandler.this.mRetWater)
              {
                Log.d(MiuiSecurityPermissionHandler.TAG, " updateWaterMarkerAccount postVerificationWaterMarker NETWORK_ERROR!");
                wait(2000L);
                i--;
              }
              else
              {
                localObject1 = MiuiSecurityPermissionHandler.TAG;
                localObject2 = new java/lang/StringBuilder;
                ((StringBuilder)localObject2).<init>();
                ((StringBuilder)localObject2).append(" updateWaterMarkerAccount! mRetWater:");
                ((StringBuilder)localObject2).append(MiuiSecurityPermissionHandler.this.mRetWater);
                Log.d((String)localObject1, ((StringBuilder)localObject2).toString());
                if ((-2 != MiuiSecurityPermissionHandler.this.mRetWater) && (MiuiSecurityPermissionHandler.this.mRetWater != 0)) {
                  MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onHideWaterMarker();
                } else {
                  MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onShowWaterMarker();
                }
              }
            }
            finally {}
          }
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    }.start();
  }
  
  public void createPermissionView()
  {
    this.mMiuiSecurityImeiFlag = Settings.Global.getInt(this.mContext.getContentResolver(), "miui_permission_check", 0);
    if ((this.mMiuiSecurityImeiFlag & 0x1) != 0) {
      return;
    }
    final WindowManager localWindowManager = (WindowManager)this.mContext.getSystemService("window");
    Log.d(TAG, "createPermissionView!");
    if (this.mPermissionView == null)
    {
      this.mPermissionView = View.inflate(this.mContext, 285933622, null);
      localObject = new WindowManager.LayoutParams(-1, -1, 2016, 84018432, 1);
      ((WindowManager.LayoutParams)localObject).setTitle("Permission");
      localWindowManager.addView(this.mPermissionView, (ViewGroup.LayoutParams)localObject);
    }
    final Button localButton = (Button)this.mPermissionView.findViewById(285802501);
    Object localObject = (Button)this.mPermissionView.findViewById(285802627);
    localButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(final View paramAnonymousView)
      {
        paramAnonymousView = MiuiSecurityPermissionHandler.this.loadAccountId();
        localButton.setEnabled(false);
        new Thread()
        {
          public void run()
          {
            MiuiSecurityPermissionHandler localMiuiSecurityPermissionHandler = MiuiSecurityPermissionHandler.this;
            View localView = MiuiSecurityPermissionHandler.this.mPermissionView;
            Button localButton = MiuiSecurityPermissionHandler.4.this.val$action;
            Object localObject1 = paramAnonymousView;
            if (localObject1 == null) {
              localObject1 = "null";
            } else {
              localObject1 = ((Account)localObject1).name;
            }
            localMiuiSecurityPermissionHandler.postVerificationRequest(localView, localButton, (String)localObject1);
            if ((MiuiSecurityPermissionHandler.this.mRetPermission != -2) && (MiuiSecurityPermissionHandler.this.mRetPermission != -1)) {
              return;
            }
            try
            {
              if (paramAnonymousView == null) {
                if (MiuiSecurityPermissionHandler.this.mBootComplete)
                {
                  MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onListenAccount(1);
                  MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onAddAccount();
                }
                else
                {
                  MiuiSecurityPermissionHandler.access$402(MiuiSecurityPermissionHandler.this, true);
                  MiuiSecurityPermissionHandler.access$202(MiuiSecurityPermissionHandler.this, true);
                }
              }
              return;
            }
            finally {}
          }
        }.start();
      }
    });
    ((Button)localObject).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        try
        {
          if (MiuiSecurityPermissionHandler.this.mPermissionView != null)
          {
            Log.d(MiuiSecurityPermissionHandler.TAG, "wifi OnClick remove View!");
            MiuiSecurityPermissionHandler.this.mPermissionViewCallback.onListenAccount(2);
            localWindowManager.removeView(MiuiSecurityPermissionHandler.this.mPermissionView);
            MiuiSecurityPermissionHandler.access$1202(MiuiSecurityPermissionHandler.this, null);
          }
        }
        catch (Exception localException)
        {
          String str = MiuiSecurityPermissionHandler.TAG;
          paramAnonymousView = new StringBuilder();
          paramAnonymousView.append("wifi OnClick  removeView ex: ");
          paramAnonymousView.append(localException);
          Log.d(str, paramAnonymousView.toString());
        }
        MiuiSecurityPermissionHandler.this.onOpenWifiSettingsButtonClicked();
      }
    });
  }
  
  public void handleAccountLogin()
  {
    this.mPermissionViewCallback.onUnListenAccount(1);
    int i = this.mRetWater;
    if (i != -2)
    {
      if (i != 0)
      {
        if (i != 1)
        {
          if (i != 2) {
            if (i != 3) {
              break label67;
            }
          }
        }
        else {
          updateWaterMarkerAccount();
        }
      }
      else {
        updateWaterMarkerAccount();
      }
    }
    else {
      updateWaterMarkerAccount();
    }
    label67:
    if (Build.IS_PRIVATE_BUILD)
    {
      i = this.mRetPermission;
      if (i != -2)
      {
        if (i != -1)
        {
          if (i == 1) {
            createPermissionView();
          }
        }
        else {
          createPermissionView();
        }
      }
      else {
        createPermissionView();
      }
    }
  }
  
  public void handleAccountLogout()
  {
    if (Build.IS_PRIVATE_BUILD)
    {
      String str = TAG;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("handleAccountLogout mRetPermission:");
      localStringBuilder.append(this.mRetPermission);
      Log.d(str, localStringBuilder.toString());
      try
      {
        postVerificationResult(null, 0);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      int i = this.mRetPermission;
      if (i != -2)
      {
        if (i != -1)
        {
          if (i == 1) {
            this.mPermissionViewCallback.onListenAccount(1);
          }
        }
        else {
          this.mPermissionViewCallback.onListenAccount(1);
        }
      }
      else {
        this.mPermissionViewCallback.onListenAccount(1);
      }
    }
  }
  
  public void handleWifiSettingFinish()
  {
    if (Build.IS_PRIVATE_BUILD)
    {
      Log.d(TAG, "handleWifiSettingFinish!");
      this.mPermissionViewCallback.onUnListenAccount(2);
      createPermissionView();
    }
  }
  
  public void mayBringUpPermissionView()
  {
    if (!Build.IS_PRIVATE_BUILD)
    {
      if (Build.IS_PRIVATE_WATER_MARKER) {
        doWaterMarker();
      }
    }
    else
    {
      doWaterMarker();
      doPermissionView();
    }
  }
  
  public void registerPermissionViewCallback(PermissionViewCallback paramPermissionViewCallback)
  {
    this.mPermissionViewCallback = paramPermissionViewCallback;
  }
  
  public static abstract interface PermissionViewCallback
  {
    public abstract void onAddAccount();
    
    public abstract void onHideWaterMarker();
    
    public abstract void onListenAccount(int paramInt);
    
    public abstract void onListenPermission();
    
    public abstract void onShowWaterMarker();
    
    public abstract void onUnListenAccount(int paramInt);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/view/MiuiSecurityPermissionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */