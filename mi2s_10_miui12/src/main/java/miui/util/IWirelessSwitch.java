package miui.util;

public class IWirelessSwitch
{
  private static final String DEFAULT = "default";
  private static final int GET_WIRELESS_STATUS = 3;
  private static volatile IWirelessSwitch INSTANCE = null;
  private static final String INTERFACE_DESCRIPTOR = "vendor.xiaomi.hardware.wireless@1.0::IWirelessSwitch";
  private static final int IS_WIRELESS_SUPPORTED = 1;
  private static final String SERVICE_NAME = "vendor.xiaomi.hardware.wireless@1.0::IWirelessSwitch";
  private static final int SET_WIRELESS_ENABLED = 2;
  public static final int STATUS_BAD_VALUE = 3;
  public static final int STATUS_FAILURE_UNKNOWN = 1;
  public static final int STATUS_NOT_SUPPORTED = 2;
  public static final int STATUS_SUCCESS = 0;
  private static final String TAG = "IWirelessSwitch";
  public static final int WIRELESS_DISABLED = 1;
  public static final int WIRELESS_ENABLED = 0;
  public static final int WIRELESS_NOT_SUPPORTED = 2;
  public static final int WIRELESS_STATUS_UNKNOWN = 3;
  
  public static IWirelessSwitch getInstance()
  {
    if (INSTANCE == null) {
      try
      {
        if (INSTANCE == null)
        {
          IWirelessSwitch localIWirelessSwitch = new miui/util/IWirelessSwitch;
          localIWirelessSwitch.<init>();
          INSTANCE = localIWirelessSwitch;
        }
      }
      finally {}
    }
    return INSTANCE;
  }
  
  /* Error */
  public int getWirelessChargingStatus()
  {
    // Byte code:
    //   0: new 50	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 51	android/os/HwParcel:<init>	()V
    //   7: astore_1
    //   8: ldc 16
    //   10: ldc 8
    //   12: invokestatic 57	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_2
    //   16: aload_2
    //   17: ifnull +126 -> 143
    //   20: new 50	android/os/HwParcel
    //   23: astore_3
    //   24: aload_3
    //   25: invokespecial 51	android/os/HwParcel:<init>	()V
    //   28: aload_3
    //   29: ldc 16
    //   31: invokevirtual 61	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   34: aload_2
    //   35: iconst_3
    //   36: aload_3
    //   37: aload_1
    //   38: iconst_0
    //   39: invokeinterface 67 5 0
    //   44: aload_1
    //   45: invokevirtual 70	android/os/HwParcel:verifySuccess	()V
    //   48: aload_3
    //   49: invokevirtual 73	android/os/HwParcel:releaseTemporaryStorage	()V
    //   52: aload_1
    //   53: invokevirtual 76	android/os/HwParcel:readInt32	()I
    //   56: istore 4
    //   58: iload 4
    //   60: ifeq +44 -> 104
    //   63: iload 4
    //   65: iconst_1
    //   66: if_icmpeq +38 -> 104
    //   69: new 78	java/lang/StringBuilder
    //   72: astore_3
    //   73: aload_3
    //   74: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   77: aload_3
    //   78: ldc 81
    //   80: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   83: pop
    //   84: aload_3
    //   85: iload 4
    //   87: invokevirtual 88	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   90: pop
    //   91: ldc 29
    //   93: aload_3
    //   94: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   97: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   100: pop
    //   101: goto +35 -> 136
    //   104: new 78	java/lang/StringBuilder
    //   107: astore_3
    //   108: aload_3
    //   109: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   112: aload_3
    //   113: ldc 100
    //   115: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   118: pop
    //   119: aload_3
    //   120: iload 4
    //   122: invokevirtual 88	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   125: pop
    //   126: ldc 29
    //   128: aload_3
    //   129: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   132: invokestatic 103	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   135: pop
    //   136: aload_1
    //   137: invokevirtual 106	android/os/HwParcel:release	()V
    //   140: iload 4
    //   142: ireturn
    //   143: aload_1
    //   144: invokevirtual 106	android/os/HwParcel:release	()V
    //   147: goto +42 -> 189
    //   150: astore_3
    //   151: goto +48 -> 199
    //   154: astore_3
    //   155: new 78	java/lang/StringBuilder
    //   158: astore_2
    //   159: aload_2
    //   160: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   163: aload_2
    //   164: ldc 108
    //   166: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   169: pop
    //   170: aload_2
    //   171: aload_3
    //   172: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   175: pop
    //   176: ldc 29
    //   178: aload_2
    //   179: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   185: pop
    //   186: goto -43 -> 143
    //   189: ldc 29
    //   191: ldc 113
    //   193: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   196: pop
    //   197: iconst_3
    //   198: ireturn
    //   199: aload_1
    //   200: invokevirtual 106	android/os/HwParcel:release	()V
    //   203: aload_3
    //   204: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	205	0	this	IWirelessSwitch
    //   7	193	1	localHwParcel	android.os.HwParcel
    //   15	164	2	localObject1	Object
    //   23	106	3	localObject2	Object
    //   150	1	3	localObject3	Object
    //   154	50	3	localException	Exception
    //   56	85	4	i	int
    // Exception table:
    //   from	to	target	type
    //   8	16	150	finally
    //   20	58	150	finally
    //   69	101	150	finally
    //   104	136	150	finally
    //   155	186	150	finally
    //   8	16	154	java/lang/Exception
    //   20	58	154	java/lang/Exception
    //   69	101	154	java/lang/Exception
    //   104	136	154	java/lang/Exception
  }
  
  /* Error */
  public boolean isWirelessChargingSupported()
  {
    // Byte code:
    //   0: new 50	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 51	android/os/HwParcel:<init>	()V
    //   7: astore_1
    //   8: ldc 16
    //   10: ldc 8
    //   12: invokestatic 57	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_2
    //   16: aload_2
    //   17: ifnull +62 -> 79
    //   20: new 50	android/os/HwParcel
    //   23: astore_3
    //   24: aload_3
    //   25: invokespecial 51	android/os/HwParcel:<init>	()V
    //   28: aload_3
    //   29: ldc 16
    //   31: invokevirtual 61	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   34: aload_2
    //   35: iconst_1
    //   36: aload_3
    //   37: aload_1
    //   38: iconst_0
    //   39: invokeinterface 67 5 0
    //   44: aload_1
    //   45: invokevirtual 70	android/os/HwParcel:verifySuccess	()V
    //   48: aload_3
    //   49: invokevirtual 73	android/os/HwParcel:releaseTemporaryStorage	()V
    //   52: aload_1
    //   53: invokevirtual 76	android/os/HwParcel:readInt32	()I
    //   56: ifne +17 -> 73
    //   59: ldc 29
    //   61: ldc 117
    //   63: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   66: pop
    //   67: aload_1
    //   68: invokevirtual 106	android/os/HwParcel:release	()V
    //   71: iconst_0
    //   72: ireturn
    //   73: aload_1
    //   74: invokevirtual 106	android/os/HwParcel:release	()V
    //   77: iconst_1
    //   78: ireturn
    //   79: aload_1
    //   80: invokevirtual 106	android/os/HwParcel:release	()V
    //   83: goto +42 -> 125
    //   86: astore_3
    //   87: goto +48 -> 135
    //   90: astore_3
    //   91: new 78	java/lang/StringBuilder
    //   94: astore_2
    //   95: aload_2
    //   96: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   99: aload_2
    //   100: ldc 119
    //   102: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: pop
    //   106: aload_2
    //   107: aload_3
    //   108: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   111: pop
    //   112: ldc 29
    //   114: aload_2
    //   115: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   121: pop
    //   122: goto -43 -> 79
    //   125: ldc 29
    //   127: ldc 121
    //   129: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   132: pop
    //   133: iconst_0
    //   134: ireturn
    //   135: aload_1
    //   136: invokevirtual 106	android/os/HwParcel:release	()V
    //   139: aload_3
    //   140: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	141	0	this	IWirelessSwitch
    //   7	129	1	localHwParcel1	android.os.HwParcel
    //   15	100	2	localObject1	Object
    //   23	26	3	localHwParcel2	android.os.HwParcel
    //   86	1	3	localObject2	Object
    //   90	50	3	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   8	16	86	finally
    //   20	67	86	finally
    //   91	122	86	finally
    //   8	16	90	java/lang/Exception
    //   20	67	90	java/lang/Exception
  }
  
  /* Error */
  public int setWirelessChargingEnabled(boolean paramBoolean)
  {
    // Byte code:
    //   0: new 50	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 51	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 16
    //   10: ldc 8
    //   12: invokestatic 57	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +148 -> 165
    //   20: new 50	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 51	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 16
    //   34: invokevirtual 61	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 127	android/os/HwParcel:writeBool	(Z)V
    //   43: aload_3
    //   44: iconst_2
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 67 5 0
    //   54: aload_2
    //   55: invokevirtual 70	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 73	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 76	android/os/HwParcel:readInt32	()I
    //   67: istore 5
    //   69: iload 5
    //   71: ifne +50 -> 121
    //   74: new 78	java/lang/StringBuilder
    //   77: astore 4
    //   79: aload 4
    //   81: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   84: aload 4
    //   86: ldc -127
    //   88: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: pop
    //   92: aload 4
    //   94: iload_1
    //   95: invokevirtual 132	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   98: pop
    //   99: aload 4
    //   101: ldc -122
    //   103: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: pop
    //   107: ldc 29
    //   109: aload 4
    //   111: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   114: invokestatic 103	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   117: pop
    //   118: goto +40 -> 158
    //   121: new 78	java/lang/StringBuilder
    //   124: astore 4
    //   126: aload 4
    //   128: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   131: aload 4
    //   133: ldc -120
    //   135: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: pop
    //   139: aload 4
    //   141: iload 5
    //   143: invokevirtual 88	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   146: pop
    //   147: ldc 29
    //   149: aload 4
    //   151: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   154: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   157: pop
    //   158: aload_2
    //   159: invokevirtual 106	android/os/HwParcel:release	()V
    //   162: iload 5
    //   164: ireturn
    //   165: aload_2
    //   166: invokevirtual 106	android/os/HwParcel:release	()V
    //   169: goto +45 -> 214
    //   172: astore 4
    //   174: goto +50 -> 224
    //   177: astore 4
    //   179: new 78	java/lang/StringBuilder
    //   182: astore_3
    //   183: aload_3
    //   184: invokespecial 79	java/lang/StringBuilder:<init>	()V
    //   187: aload_3
    //   188: ldc -118
    //   190: invokevirtual 85	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: pop
    //   194: aload_3
    //   195: aload 4
    //   197: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   200: pop
    //   201: ldc 29
    //   203: aload_3
    //   204: invokevirtual 92	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   210: pop
    //   211: goto -46 -> 165
    //   214: ldc 29
    //   216: ldc -116
    //   218: invokestatic 98	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   221: pop
    //   222: iconst_1
    //   223: ireturn
    //   224: aload_2
    //   225: invokevirtual 106	android/os/HwParcel:release	()V
    //   228: aload 4
    //   230: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	231	0	this	IWirelessSwitch
    //   0	231	1	paramBoolean	boolean
    //   7	218	2	localHwParcel	android.os.HwParcel
    //   15	189	3	localObject1	Object
    //   23	127	4	localObject2	Object
    //   172	1	4	localObject3	Object
    //   177	52	4	localException	Exception
    //   67	96	5	i	int
    // Exception table:
    //   from	to	target	type
    //   8	16	172	finally
    //   20	69	172	finally
    //   74	118	172	finally
    //   121	158	172	finally
    //   179	211	172	finally
    //   8	16	177	java/lang/Exception
    //   20	69	177	java/lang/Exception
    //   74	118	177	java/lang/Exception
    //   121	158	177	java/lang/Exception
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/IWirelessSwitch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */