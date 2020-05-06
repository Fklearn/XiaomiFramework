package miui.util;

import android.os.SystemProperties;

public class ITouchFeature
{
  private static final String DEFAULT = "default";
  private static final int GET_MODE_CUR_VALUE = 2;
  private static final int GET_MODE_DEF_VALUE = 5;
  private static final int GET_MODE_MAX_VALUE = 3;
  private static final int GET_MODE_MIN_VALUE = 4;
  private static final int GET_MODE_VALUES = 7;
  private static volatile ITouchFeature INSTANCE = null;
  private static final String INTERFACE_DESCRIPTOR = "vendor.xiaomi.hardware.touchfeature@1.0::ITouchFeature";
  private static final int MODE_RESET = 6;
  private static final String SERVICE_NAME = "vendor.xiaomi.hardware.touchfeature@1.0::ITouchFeature";
  private static final int SET_MODE_EDGE_VALUE = 8;
  private static final int SET_MODE_VALUE = 1;
  private static final String TAG = "ITouchFeature";
  private static final int TOUCHFEATURE_DOUBLE_TAP = 1;
  private static final int TOUCHFEATURE_DRIVER_DEBUGLEVEL = 8;
  private static final int TOUCHFEATURE_EDGE_MODE = 4;
  private static final int TOUCHFEATURE_GLOBAL_TOUCH_DIRECTION = 2;
  public static final int TOUCH_ACTIVE_MODE = 1;
  public static final int TOUCH_DEBUG_LEVEL = 18;
  public static final int TOUCH_DOUBLETAP_MODE = 14;
  public static final int TOUCH_EDGE_FILTER = 7;
  public static final int TOUCH_EDGE_MODE = 15;
  public static final int TOUCH_GAME_MODE = 0;
  public static final int TOUCH_MODE_DIRECTION = 8;
  public static final int TOUCH_TOLERANCE = 3;
  public static final int TOUCH_UP_THRESHOLD = 2;
  public static final int TOUCH_WGH_MAX = 5;
  public static final int TOUCH_WGH_MIN = 4;
  public static final int TOUCH_WGH_STEP = 6;
  private int mTouchFeatureProperties = SystemProperties.getInt("ro.vendor.touchfeature.type", 0);
  
  public static ITouchFeature getInstance()
  {
    if (INSTANCE == null) {
      try
      {
        if (INSTANCE == null)
        {
          ITouchFeature localITouchFeature = new miui/util/ITouchFeature;
          localITouchFeature.<init>();
          INSTANCE = localITouchFeature;
        }
      }
      finally {}
    }
    return INSTANCE;
  }
  
  /* Error */
  public int[] getModeValues(int paramInt)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +100 -> 117
    //   20: new 84	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 85	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 24
    //   34: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: bipush 7
    //   46: aload 4
    //   48: aload_2
    //   49: iconst_0
    //   50: invokeinterface 105 5 0
    //   55: aload_2
    //   56: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   59: aload 4
    //   61: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   64: aload_2
    //   65: invokevirtual 115	android/os/HwParcel:readInt32Vector	()Ljava/util/ArrayList;
    //   68: astore 4
    //   70: aload 4
    //   72: invokevirtual 121	java/util/ArrayList:size	()I
    //   75: istore 5
    //   77: iload 5
    //   79: newarray <illegal type>
    //   81: astore_3
    //   82: iconst_0
    //   83: istore_1
    //   84: iload_1
    //   85: iload 5
    //   87: if_icmpge +24 -> 111
    //   90: aload_3
    //   91: iload_1
    //   92: aload 4
    //   94: iload_1
    //   95: invokevirtual 125	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   98: checkcast 127	java/lang/Integer
    //   101: invokevirtual 130	java/lang/Integer:intValue	()I
    //   104: iastore
    //   105: iinc 1 1
    //   108: goto -24 -> 84
    //   111: aload_2
    //   112: invokevirtual 133	android/os/HwParcel:release	()V
    //   115: aload_3
    //   116: areturn
    //   117: aload_2
    //   118: invokevirtual 133	android/os/HwParcel:release	()V
    //   121: goto +67 -> 188
    //   124: astore 4
    //   126: goto +74 -> 200
    //   129: astore 4
    //   131: goto +8 -> 139
    //   134: astore 4
    //   136: goto +17 -> 153
    //   139: ldc 34
    //   141: aload 4
    //   143: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   146: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   149: pop
    //   150: goto -33 -> 117
    //   153: new 145	java/lang/StringBuilder
    //   156: astore_3
    //   157: aload_3
    //   158: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   161: aload_3
    //   162: ldc -108
    //   164: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: pop
    //   168: aload_3
    //   169: aload 4
    //   171: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   174: pop
    //   175: ldc 34
    //   177: aload_3
    //   178: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   184: pop
    //   185: goto -68 -> 117
    //   188: ldc 34
    //   190: ldc -98
    //   192: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   195: pop
    //   196: iconst_4
    //   197: newarray <illegal type>
    //   199: areturn
    //   200: aload_2
    //   201: invokevirtual 133	android/os/HwParcel:release	()V
    //   204: aload 4
    //   206: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	207	0	this	ITouchFeature
    //   0	207	1	paramInt	int
    //   7	194	2	localHwParcel	android.os.HwParcel
    //   15	163	3	localObject1	Object
    //   23	70	4	localObject2	Object
    //   124	1	4	localObject3	Object
    //   129	1	4	localNoSuchElementException	java.util.NoSuchElementException
    //   134	71	4	localRemoteException	android.os.RemoteException
    //   75	13	5	i	int
    // Exception table:
    //   from	to	target	type
    //   8	16	124	finally
    //   20	82	124	finally
    //   90	105	124	finally
    //   139	150	124	finally
    //   153	185	124	finally
    //   8	16	129	java/util/NoSuchElementException
    //   20	82	129	java/util/NoSuchElementException
    //   90	105	129	java/util/NoSuchElementException
    //   8	16	134	android/os/RemoteException
    //   20	82	134	android/os/RemoteException
    //   90	105	134	android/os/RemoteException
  }
  
  /* Error */
  public int getTouchModeCurValue(int paramInt)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +103 -> 120
    //   20: new 84	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 85	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 24
    //   34: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: iconst_2
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 105 5 0
    //   54: aload_2
    //   55: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   67: istore_1
    //   68: iload_1
    //   69: ifge +45 -> 114
    //   72: new 145	java/lang/StringBuilder
    //   75: astore 4
    //   77: aload 4
    //   79: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   82: aload 4
    //   84: ldc -91
    //   86: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: pop
    //   90: aload 4
    //   92: iload_1
    //   93: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: ldc 34
    //   99: aload 4
    //   101: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   107: pop
    //   108: aload_2
    //   109: invokevirtual 133	android/os/HwParcel:release	()V
    //   112: iconst_m1
    //   113: ireturn
    //   114: aload_2
    //   115: invokevirtual 133	android/os/HwParcel:release	()V
    //   118: iload_1
    //   119: ireturn
    //   120: aload_2
    //   121: invokevirtual 133	android/os/HwParcel:release	()V
    //   124: goto +67 -> 191
    //   127: astore 4
    //   129: goto +72 -> 201
    //   132: astore 4
    //   134: goto +8 -> 142
    //   137: astore 4
    //   139: goto +17 -> 156
    //   142: ldc 34
    //   144: aload 4
    //   146: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   149: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: goto -33 -> 120
    //   156: new 145	java/lang/StringBuilder
    //   159: astore_3
    //   160: aload_3
    //   161: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   164: aload_3
    //   165: ldc -108
    //   167: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: pop
    //   171: aload_3
    //   172: aload 4
    //   174: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   177: pop
    //   178: ldc 34
    //   180: aload_3
    //   181: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   184: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   187: pop
    //   188: goto -68 -> 120
    //   191: ldc 34
    //   193: ldc -86
    //   195: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   198: pop
    //   199: iconst_m1
    //   200: ireturn
    //   201: aload_2
    //   202: invokevirtual 133	android/os/HwParcel:release	()V
    //   205: aload 4
    //   207: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	ITouchFeature
    //   0	208	1	paramInt	int
    //   7	195	2	localHwParcel	android.os.HwParcel
    //   15	166	3	localObject1	Object
    //   23	77	4	localObject2	Object
    //   127	1	4	localObject3	Object
    //   132	1	4	localNoSuchElementException	java.util.NoSuchElementException
    //   137	69	4	localRemoteException	android.os.RemoteException
    // Exception table:
    //   from	to	target	type
    //   8	16	127	finally
    //   20	68	127	finally
    //   72	108	127	finally
    //   142	153	127	finally
    //   156	188	127	finally
    //   8	16	132	java/util/NoSuchElementException
    //   20	68	132	java/util/NoSuchElementException
    //   72	108	132	java/util/NoSuchElementException
    //   8	16	137	android/os/RemoteException
    //   20	68	137	android/os/RemoteException
    //   72	108	137	android/os/RemoteException
  }
  
  /* Error */
  public int getTouchModeDefValue(int paramInt)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +103 -> 120
    //   20: new 84	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 85	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 24
    //   34: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: iconst_5
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 105 5 0
    //   54: aload_2
    //   55: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   67: istore_1
    //   68: iload_1
    //   69: ifge +45 -> 114
    //   72: new 145	java/lang/StringBuilder
    //   75: astore 4
    //   77: aload 4
    //   79: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   82: aload 4
    //   84: ldc -83
    //   86: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: pop
    //   90: aload 4
    //   92: iload_1
    //   93: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: ldc 34
    //   99: aload 4
    //   101: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   107: pop
    //   108: aload_2
    //   109: invokevirtual 133	android/os/HwParcel:release	()V
    //   112: iconst_m1
    //   113: ireturn
    //   114: aload_2
    //   115: invokevirtual 133	android/os/HwParcel:release	()V
    //   118: iload_1
    //   119: ireturn
    //   120: aload_2
    //   121: invokevirtual 133	android/os/HwParcel:release	()V
    //   124: goto +67 -> 191
    //   127: astore 4
    //   129: goto +72 -> 201
    //   132: astore 4
    //   134: goto +8 -> 142
    //   137: astore 4
    //   139: goto +17 -> 156
    //   142: ldc 34
    //   144: aload 4
    //   146: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   149: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: goto -33 -> 120
    //   156: new 145	java/lang/StringBuilder
    //   159: astore_3
    //   160: aload_3
    //   161: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   164: aload_3
    //   165: ldc -108
    //   167: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: pop
    //   171: aload_3
    //   172: aload 4
    //   174: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   177: pop
    //   178: ldc 34
    //   180: aload_3
    //   181: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   184: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   187: pop
    //   188: goto -68 -> 120
    //   191: ldc 34
    //   193: ldc -81
    //   195: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   198: pop
    //   199: iconst_m1
    //   200: ireturn
    //   201: aload_2
    //   202: invokevirtual 133	android/os/HwParcel:release	()V
    //   205: aload 4
    //   207: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	ITouchFeature
    //   0	208	1	paramInt	int
    //   7	195	2	localHwParcel	android.os.HwParcel
    //   15	166	3	localObject1	Object
    //   23	77	4	localObject2	Object
    //   127	1	4	localObject3	Object
    //   132	1	4	localNoSuchElementException	java.util.NoSuchElementException
    //   137	69	4	localRemoteException	android.os.RemoteException
    // Exception table:
    //   from	to	target	type
    //   8	16	127	finally
    //   20	68	127	finally
    //   72	108	127	finally
    //   142	153	127	finally
    //   156	188	127	finally
    //   8	16	132	java/util/NoSuchElementException
    //   20	68	132	java/util/NoSuchElementException
    //   72	108	132	java/util/NoSuchElementException
    //   8	16	137	android/os/RemoteException
    //   20	68	137	android/os/RemoteException
    //   72	108	137	android/os/RemoteException
  }
  
  /* Error */
  public int getTouchModeMaxValue(int paramInt)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +103 -> 120
    //   20: new 84	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 85	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 24
    //   34: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: iconst_3
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 105 5 0
    //   54: aload_2
    //   55: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   67: istore_1
    //   68: iload_1
    //   69: ifge +45 -> 114
    //   72: new 145	java/lang/StringBuilder
    //   75: astore 4
    //   77: aload 4
    //   79: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   82: aload 4
    //   84: ldc -78
    //   86: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: pop
    //   90: aload 4
    //   92: iload_1
    //   93: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: ldc 34
    //   99: aload 4
    //   101: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   107: pop
    //   108: aload_2
    //   109: invokevirtual 133	android/os/HwParcel:release	()V
    //   112: iconst_m1
    //   113: ireturn
    //   114: aload_2
    //   115: invokevirtual 133	android/os/HwParcel:release	()V
    //   118: iload_1
    //   119: ireturn
    //   120: aload_2
    //   121: invokevirtual 133	android/os/HwParcel:release	()V
    //   124: goto +67 -> 191
    //   127: astore 4
    //   129: goto +72 -> 201
    //   132: astore 4
    //   134: goto +8 -> 142
    //   137: astore 4
    //   139: goto +17 -> 156
    //   142: ldc 34
    //   144: aload 4
    //   146: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   149: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: goto -33 -> 120
    //   156: new 145	java/lang/StringBuilder
    //   159: astore_3
    //   160: aload_3
    //   161: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   164: aload_3
    //   165: ldc -108
    //   167: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: pop
    //   171: aload_3
    //   172: aload 4
    //   174: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   177: pop
    //   178: ldc 34
    //   180: aload_3
    //   181: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   184: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   187: pop
    //   188: goto -68 -> 120
    //   191: ldc 34
    //   193: ldc -76
    //   195: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   198: pop
    //   199: iconst_m1
    //   200: ireturn
    //   201: aload_2
    //   202: invokevirtual 133	android/os/HwParcel:release	()V
    //   205: aload 4
    //   207: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	ITouchFeature
    //   0	208	1	paramInt	int
    //   7	195	2	localHwParcel	android.os.HwParcel
    //   15	166	3	localObject1	Object
    //   23	77	4	localObject2	Object
    //   127	1	4	localObject3	Object
    //   132	1	4	localNoSuchElementException	java.util.NoSuchElementException
    //   137	69	4	localRemoteException	android.os.RemoteException
    // Exception table:
    //   from	to	target	type
    //   8	16	127	finally
    //   20	68	127	finally
    //   72	108	127	finally
    //   142	153	127	finally
    //   156	188	127	finally
    //   8	16	132	java/util/NoSuchElementException
    //   20	68	132	java/util/NoSuchElementException
    //   72	108	132	java/util/NoSuchElementException
    //   8	16	137	android/os/RemoteException
    //   20	68	137	android/os/RemoteException
    //   72	108	137	android/os/RemoteException
  }
  
  /* Error */
  public int getTouchModeMinValue(int paramInt)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +103 -> 120
    //   20: new 84	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 85	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 24
    //   34: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: iconst_4
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 105 5 0
    //   54: aload_2
    //   55: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   67: istore_1
    //   68: iload_1
    //   69: ifge +45 -> 114
    //   72: new 145	java/lang/StringBuilder
    //   75: astore 4
    //   77: aload 4
    //   79: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   82: aload 4
    //   84: ldc -73
    //   86: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   89: pop
    //   90: aload 4
    //   92: iload_1
    //   93: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: ldc 34
    //   99: aload 4
    //   101: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   107: pop
    //   108: aload_2
    //   109: invokevirtual 133	android/os/HwParcel:release	()V
    //   112: iconst_m1
    //   113: ireturn
    //   114: aload_2
    //   115: invokevirtual 133	android/os/HwParcel:release	()V
    //   118: iload_1
    //   119: ireturn
    //   120: aload_2
    //   121: invokevirtual 133	android/os/HwParcel:release	()V
    //   124: goto +70 -> 194
    //   127: astore 4
    //   129: goto +75 -> 204
    //   132: astore 4
    //   134: goto +7 -> 141
    //   137: astore_3
    //   138: goto +17 -> 155
    //   141: ldc 34
    //   143: aload 4
    //   145: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   148: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: goto -32 -> 120
    //   155: new 145	java/lang/StringBuilder
    //   158: astore 4
    //   160: aload 4
    //   162: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   165: aload 4
    //   167: ldc -108
    //   169: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: pop
    //   173: aload 4
    //   175: aload_3
    //   176: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   179: pop
    //   180: ldc 34
    //   182: aload 4
    //   184: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   190: pop
    //   191: goto -71 -> 120
    //   194: ldc 34
    //   196: ldc -71
    //   198: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   201: pop
    //   202: iconst_m1
    //   203: ireturn
    //   204: aload_2
    //   205: invokevirtual 133	android/os/HwParcel:release	()V
    //   208: aload 4
    //   210: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	211	0	this	ITouchFeature
    //   0	211	1	paramInt	int
    //   7	198	2	localHwParcel	android.os.HwParcel
    //   15	29	3	localIHwBinder	android.os.IHwBinder
    //   137	39	3	localRemoteException	android.os.RemoteException
    //   23	77	4	localObject1	Object
    //   127	1	4	localObject2	Object
    //   132	12	4	localNoSuchElementException	java.util.NoSuchElementException
    //   158	51	4	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   8	16	127	finally
    //   20	68	127	finally
    //   72	108	127	finally
    //   141	152	127	finally
    //   155	191	127	finally
    //   8	16	132	java/util/NoSuchElementException
    //   20	68	132	java/util/NoSuchElementException
    //   72	108	132	java/util/NoSuchElementException
    //   8	16	137	android/os/RemoteException
    //   20	68	137	android/os/RemoteException
    //   72	108	137	android/os/RemoteException
  }
  
  public boolean hasDoubleTapWakeUpSupport()
  {
    int i = this.mTouchFeatureProperties;
    boolean bool = true;
    if ((i & 0x1) == 0) {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasDriverDebugLevelSupport()
  {
    boolean bool;
    if ((this.mTouchFeatureProperties & 0x8) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasSupportEdgeMode()
  {
    boolean bool;
    if ((this.mTouchFeatureProperties & 0x4) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public boolean hasSupportGlobalTouchDirection()
  {
    boolean bool;
    if ((this.mTouchFeatureProperties & 0x2) != 0) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  /* Error */
  public boolean resetTouchMode(int paramInt)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +99 -> 116
    //   20: new 84	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 85	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 24
    //   34: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: bipush 6
    //   46: aload 4
    //   48: aload_2
    //   49: iconst_0
    //   50: invokeinterface 105 5 0
    //   55: aload_2
    //   56: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   59: aload 4
    //   61: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   64: aload_2
    //   65: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   68: istore_1
    //   69: iload_1
    //   70: ifeq +40 -> 110
    //   73: new 145	java/lang/StringBuilder
    //   76: astore_3
    //   77: aload_3
    //   78: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   81: aload_3
    //   82: ldc -62
    //   84: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: pop
    //   88: aload_3
    //   89: iload_1
    //   90: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   93: pop
    //   94: ldc 34
    //   96: aload_3
    //   97: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   100: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   103: pop
    //   104: aload_2
    //   105: invokevirtual 133	android/os/HwParcel:release	()V
    //   108: iconst_0
    //   109: ireturn
    //   110: aload_2
    //   111: invokevirtual 133	android/os/HwParcel:release	()V
    //   114: iconst_1
    //   115: ireturn
    //   116: aload_2
    //   117: invokevirtual 133	android/os/HwParcel:release	()V
    //   120: goto +67 -> 187
    //   123: astore_3
    //   124: goto +73 -> 197
    //   127: astore_3
    //   128: goto +7 -> 135
    //   131: astore_3
    //   132: goto +16 -> 148
    //   135: ldc 34
    //   137: aload_3
    //   138: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   141: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   144: pop
    //   145: goto -29 -> 116
    //   148: new 145	java/lang/StringBuilder
    //   151: astore 4
    //   153: aload 4
    //   155: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   158: aload 4
    //   160: ldc -108
    //   162: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   165: pop
    //   166: aload 4
    //   168: aload_3
    //   169: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   172: pop
    //   173: ldc 34
    //   175: aload 4
    //   177: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   180: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   183: pop
    //   184: goto -68 -> 116
    //   187: ldc 34
    //   189: ldc -60
    //   191: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   194: pop
    //   195: iconst_0
    //   196: ireturn
    //   197: aload_2
    //   198: invokevirtual 133	android/os/HwParcel:release	()V
    //   201: aload_3
    //   202: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	ITouchFeature
    //   0	203	1	paramInt	int
    //   7	191	2	localHwParcel	android.os.HwParcel
    //   15	82	3	localObject1	Object
    //   123	1	3	localObject2	Object
    //   127	1	3	localNoSuchElementException	java.util.NoSuchElementException
    //   131	71	3	localRemoteException	android.os.RemoteException
    //   23	153	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   8	16	123	finally
    //   20	69	123	finally
    //   73	104	123	finally
    //   135	145	123	finally
    //   148	184	123	finally
    //   8	16	127	java/util/NoSuchElementException
    //   20	69	127	java/util/NoSuchElementException
    //   73	104	127	java/util/NoSuchElementException
    //   8	16	131	android/os/RemoteException
    //   20	69	131	android/os/RemoteException
    //   73	104	131	android/os/RemoteException
  }
  
  /* Error */
  public boolean setEdgeMode(int paramInt1, java.util.ArrayList<Integer> paramArrayList, int paramInt2)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore 4
    //   9: ldc 24
    //   11: ldc 8
    //   13: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   16: astore 5
    //   18: aload 5
    //   20: ifnull +117 -> 137
    //   23: new 84	android/os/HwParcel
    //   26: astore 6
    //   28: aload 6
    //   30: invokespecial 85	android/os/HwParcel:<init>	()V
    //   33: aload 6
    //   35: ldc 24
    //   37: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   40: aload 6
    //   42: iload_1
    //   43: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   46: aload 6
    //   48: iload_3
    //   49: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   52: aload 6
    //   54: aload_2
    //   55: invokevirtual 202	android/os/HwParcel:writeInt32Vector	(Ljava/util/ArrayList;)V
    //   58: aload 5
    //   60: bipush 8
    //   62: aload 6
    //   64: aload 4
    //   66: iconst_0
    //   67: invokeinterface 105 5 0
    //   72: aload 4
    //   74: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   77: aload 6
    //   79: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   82: aload 4
    //   84: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   87: istore_1
    //   88: iload_1
    //   89: ifeq +41 -> 130
    //   92: new 145	java/lang/StringBuilder
    //   95: astore_2
    //   96: aload_2
    //   97: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   100: aload_2
    //   101: ldc -52
    //   103: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   106: pop
    //   107: aload_2
    //   108: iload_1
    //   109: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   112: pop
    //   113: ldc 34
    //   115: aload_2
    //   116: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   119: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   122: pop
    //   123: aload 4
    //   125: invokevirtual 133	android/os/HwParcel:release	()V
    //   128: iconst_0
    //   129: ireturn
    //   130: aload 4
    //   132: invokevirtual 133	android/os/HwParcel:release	()V
    //   135: iconst_1
    //   136: ireturn
    //   137: aload 4
    //   139: invokevirtual 133	android/os/HwParcel:release	()V
    //   142: goto +64 -> 206
    //   145: astore_2
    //   146: goto +70 -> 216
    //   149: astore_2
    //   150: goto +8 -> 158
    //   153: astore 5
    //   155: goto +16 -> 171
    //   158: ldc 34
    //   160: aload_2
    //   161: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   164: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   167: pop
    //   168: goto -31 -> 137
    //   171: new 145	java/lang/StringBuilder
    //   174: astore_2
    //   175: aload_2
    //   176: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   179: aload_2
    //   180: ldc -108
    //   182: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: pop
    //   186: aload_2
    //   187: aload 5
    //   189: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   192: pop
    //   193: ldc 34
    //   195: aload_2
    //   196: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   199: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   202: pop
    //   203: goto -66 -> 137
    //   206: ldc 34
    //   208: ldc -50
    //   210: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   213: pop
    //   214: iconst_0
    //   215: ireturn
    //   216: aload 4
    //   218: invokevirtual 133	android/os/HwParcel:release	()V
    //   221: aload_2
    //   222: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	223	0	this	ITouchFeature
    //   0	223	1	paramInt1	int
    //   0	223	2	paramArrayList	java.util.ArrayList<Integer>
    //   0	223	3	paramInt2	int
    //   7	210	4	localHwParcel1	android.os.HwParcel
    //   16	43	5	localIHwBinder	android.os.IHwBinder
    //   153	35	5	localRemoteException	android.os.RemoteException
    //   26	52	6	localHwParcel2	android.os.HwParcel
    // Exception table:
    //   from	to	target	type
    //   9	18	145	finally
    //   23	88	145	finally
    //   92	123	145	finally
    //   158	168	145	finally
    //   171	203	145	finally
    //   9	18	149	java/util/NoSuchElementException
    //   23	88	149	java/util/NoSuchElementException
    //   92	123	149	java/util/NoSuchElementException
    //   9	18	153	android/os/RemoteException
    //   23	88	153	android/os/RemoteException
    //   92	123	153	android/os/RemoteException
  }
  
  /* Error */
  public boolean setTouchMode(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: new 84	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 85	android/os/HwParcel:<init>	()V
    //   7: astore_3
    //   8: ldc 24
    //   10: ldc 8
    //   12: invokestatic 91	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore 4
    //   17: aload 4
    //   19: ifnull +110 -> 129
    //   22: new 84	android/os/HwParcel
    //   25: astore 5
    //   27: aload 5
    //   29: invokespecial 85	android/os/HwParcel:<init>	()V
    //   32: aload 5
    //   34: ldc 24
    //   36: invokevirtual 95	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   39: aload 5
    //   41: iload_1
    //   42: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   45: aload 5
    //   47: iload_2
    //   48: invokevirtual 99	android/os/HwParcel:writeInt32	(I)V
    //   51: aload 4
    //   53: iconst_1
    //   54: aload 5
    //   56: aload_3
    //   57: iconst_0
    //   58: invokeinterface 105 5 0
    //   63: aload_3
    //   64: invokevirtual 108	android/os/HwParcel:verifySuccess	()V
    //   67: aload 5
    //   69: invokevirtual 111	android/os/HwParcel:releaseTemporaryStorage	()V
    //   72: aload_3
    //   73: invokevirtual 163	android/os/HwParcel:readInt32	()I
    //   76: istore_1
    //   77: iload_1
    //   78: ifeq +45 -> 123
    //   81: new 145	java/lang/StringBuilder
    //   84: astore 5
    //   86: aload 5
    //   88: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   91: aload 5
    //   93: ldc -44
    //   95: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: pop
    //   99: aload 5
    //   101: iload_1
    //   102: invokevirtual 168	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   105: pop
    //   106: ldc 34
    //   108: aload 5
    //   110: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   116: pop
    //   117: aload_3
    //   118: invokevirtual 133	android/os/HwParcel:release	()V
    //   121: iconst_0
    //   122: ireturn
    //   123: aload_3
    //   124: invokevirtual 133	android/os/HwParcel:release	()V
    //   127: iconst_1
    //   128: ireturn
    //   129: aload_3
    //   130: invokevirtual 133	android/os/HwParcel:release	()V
    //   133: goto +72 -> 205
    //   136: astore 5
    //   138: goto +77 -> 215
    //   141: astore 5
    //   143: goto +8 -> 151
    //   146: astore 4
    //   148: goto +17 -> 165
    //   151: ldc 34
    //   153: aload 5
    //   155: invokevirtual 137	java/util/NoSuchElementException:toString	()Ljava/lang/String;
    //   158: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   161: pop
    //   162: goto -33 -> 129
    //   165: new 145	java/lang/StringBuilder
    //   168: astore 5
    //   170: aload 5
    //   172: invokespecial 146	java/lang/StringBuilder:<init>	()V
    //   175: aload 5
    //   177: ldc -108
    //   179: invokevirtual 152	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: pop
    //   183: aload 5
    //   185: aload 4
    //   187: invokevirtual 155	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   190: pop
    //   191: ldc 34
    //   193: aload 5
    //   195: invokevirtual 156	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   198: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   201: pop
    //   202: goto -73 -> 129
    //   205: ldc 34
    //   207: ldc -42
    //   209: invokestatic 143	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   212: pop
    //   213: iconst_0
    //   214: ireturn
    //   215: aload_3
    //   216: invokevirtual 133	android/os/HwParcel:release	()V
    //   219: aload 5
    //   221: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	222	0	this	ITouchFeature
    //   0	222	1	paramInt1	int
    //   0	222	2	paramInt2	int
    //   7	209	3	localHwParcel	android.os.HwParcel
    //   15	37	4	localIHwBinder	android.os.IHwBinder
    //   146	40	4	localRemoteException	android.os.RemoteException
    //   25	84	5	localObject1	Object
    //   136	1	5	localObject2	Object
    //   141	13	5	localNoSuchElementException	java.util.NoSuchElementException
    //   168	52	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   8	17	136	finally
    //   22	77	136	finally
    //   81	117	136	finally
    //   151	162	136	finally
    //   165	202	136	finally
    //   8	17	141	java/util/NoSuchElementException
    //   22	77	141	java/util/NoSuchElementException
    //   81	117	141	java/util/NoSuchElementException
    //   8	17	146	android/os/RemoteException
    //   22	77	146	android/os/RemoteException
    //   81	117	146	android/os/RemoteException
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/ITouchFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */