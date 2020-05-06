package miui.util;

public class IMiOob
{
  private static final String DEFAULT = "default";
  private static volatile IMiOob INSTANCE = null;
  private static final String INTERFACE_DESCRIPTOR = "vendor.xiaomi.hardware.mioob@1.0::IMiOob";
  private static final int MIOOB_SET_BT_STATE = 1;
  private static final int MIOOB_SET_RX_CR = 2;
  private static final String SERVICE_NAME = "vendor.xiaomi.hardware.mioob@1.0::IMiOob";
  public static final int STATUS_SUCCESS = 0;
  private static final String TAG = "IMiOob";
  
  public static IMiOob getInstance()
  {
    if (INSTANCE == null) {
      try
      {
        if (INSTANCE == null)
        {
          IMiOob localIMiOob = new miui/util/IMiOob;
          localIMiOob.<init>();
          INSTANCE = localIMiOob;
        }
      }
      finally {}
    }
    return INSTANCE;
  }
  
  /* Error */
  public boolean setBtState(int paramInt)
  {
    // Byte code:
    //   0: new 41	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 42	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 13
    //   10: ldc 8
    //   12: invokestatic 48	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +73 -> 90
    //   20: new 41	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 42	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 13
    //   34: invokevirtual 52	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: iload_1
    //   40: invokevirtual 56	android/os/HwParcel:writeInt32	(I)V
    //   43: aload_3
    //   44: iconst_1
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 62 5 0
    //   54: aload_2
    //   55: invokevirtual 65	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 68	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 72	android/os/HwParcel:readInt32	()I
    //   67: ifeq +17 -> 84
    //   70: ldc 24
    //   72: ldc 74
    //   74: invokestatic 80	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   77: pop
    //   78: aload_2
    //   79: invokevirtual 83	android/os/HwParcel:release	()V
    //   82: iconst_0
    //   83: ireturn
    //   84: aload_2
    //   85: invokevirtual 83	android/os/HwParcel:release	()V
    //   88: iconst_1
    //   89: ireturn
    //   90: aload_2
    //   91: invokevirtual 83	android/os/HwParcel:release	()V
    //   94: goto +47 -> 141
    //   97: astore_3
    //   98: goto +53 -> 151
    //   101: astore_3
    //   102: new 85	java/lang/StringBuilder
    //   105: astore 4
    //   107: aload 4
    //   109: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   112: aload 4
    //   114: ldc 88
    //   116: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: pop
    //   120: aload 4
    //   122: aload_3
    //   123: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   126: pop
    //   127: ldc 24
    //   129: aload 4
    //   131: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   134: invokestatic 80	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   137: pop
    //   138: goto -48 -> 90
    //   141: ldc 24
    //   143: ldc 101
    //   145: invokestatic 80	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   148: pop
    //   149: iconst_0
    //   150: ireturn
    //   151: aload_2
    //   152: invokevirtual 83	android/os/HwParcel:release	()V
    //   155: aload_3
    //   156: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	157	0	this	IMiOob
    //   0	157	1	paramInt	int
    //   7	145	2	localHwParcel	android.os.HwParcel
    //   15	29	3	localIHwBinder	android.os.IHwBinder
    //   97	1	3	localObject1	Object
    //   101	55	3	localException	Exception
    //   23	107	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   8	16	97	finally
    //   20	78	97	finally
    //   102	138	97	finally
    //   8	16	101	java/lang/Exception
    //   20	78	101	java/lang/Exception
  }
  
  /* Error */
  public boolean setRxCr(String paramString)
  {
    // Byte code:
    //   0: new 41	android/os/HwParcel
    //   3: dup
    //   4: invokespecial 42	android/os/HwParcel:<init>	()V
    //   7: astore_2
    //   8: ldc 13
    //   10: ldc 8
    //   12: invokestatic 48	android/os/HwBinder:getService	(Ljava/lang/String;Ljava/lang/String;)Landroid/os/IHwBinder;
    //   15: astore_3
    //   16: aload_3
    //   17: ifnull +145 -> 162
    //   20: new 41	android/os/HwParcel
    //   23: astore 4
    //   25: aload 4
    //   27: invokespecial 42	android/os/HwParcel:<init>	()V
    //   30: aload 4
    //   32: ldc 13
    //   34: invokevirtual 52	android/os/HwParcel:writeInterfaceToken	(Ljava/lang/String;)V
    //   37: aload 4
    //   39: aload_1
    //   40: invokevirtual 106	android/os/HwParcel:writeString	(Ljava/lang/String;)V
    //   43: aload_3
    //   44: iconst_2
    //   45: aload 4
    //   47: aload_2
    //   48: iconst_0
    //   49: invokeinterface 62 5 0
    //   54: aload_2
    //   55: invokevirtual 65	android/os/HwParcel:verifySuccess	()V
    //   58: aload 4
    //   60: invokevirtual 68	android/os/HwParcel:releaseTemporaryStorage	()V
    //   63: aload_2
    //   64: invokevirtual 72	android/os/HwParcel:readInt32	()I
    //   67: istore 5
    //   69: iload 5
    //   71: ifeq +41 -> 112
    //   74: new 85	java/lang/StringBuilder
    //   77: astore_1
    //   78: aload_1
    //   79: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   82: aload_1
    //   83: ldc 108
    //   85: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: pop
    //   89: aload_1
    //   90: iload 5
    //   92: invokevirtual 111	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   95: pop
    //   96: ldc 24
    //   98: aload_1
    //   99: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   102: invokestatic 80	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   105: pop
    //   106: aload_2
    //   107: invokevirtual 83	android/os/HwParcel:release	()V
    //   110: iconst_0
    //   111: ireturn
    //   112: new 85	java/lang/StringBuilder
    //   115: astore 4
    //   117: aload 4
    //   119: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   122: aload 4
    //   124: ldc 113
    //   126: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: pop
    //   130: aload 4
    //   132: aload_1
    //   133: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   136: pop
    //   137: aload 4
    //   139: ldc 115
    //   141: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   144: pop
    //   145: ldc 24
    //   147: aload 4
    //   149: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   152: invokestatic 118	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   155: pop
    //   156: aload_2
    //   157: invokevirtual 83	android/os/HwParcel:release	()V
    //   160: iconst_1
    //   161: ireturn
    //   162: aload_2
    //   163: invokevirtual 83	android/os/HwParcel:release	()V
    //   166: goto +47 -> 213
    //   169: astore_1
    //   170: goto +53 -> 223
    //   173: astore_1
    //   174: new 85	java/lang/StringBuilder
    //   177: astore 4
    //   179: aload 4
    //   181: invokespecial 86	java/lang/StringBuilder:<init>	()V
    //   184: aload 4
    //   186: ldc 120
    //   188: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   191: pop
    //   192: aload 4
    //   194: aload_1
    //   195: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   198: pop
    //   199: ldc 24
    //   201: aload 4
    //   203: invokevirtual 99	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   206: invokestatic 80	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   209: pop
    //   210: goto -48 -> 162
    //   213: ldc 24
    //   215: ldc 122
    //   217: invokestatic 80	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   220: pop
    //   221: iconst_0
    //   222: ireturn
    //   223: aload_2
    //   224: invokevirtual 83	android/os/HwParcel:release	()V
    //   227: aload_1
    //   228: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	229	0	this	IMiOob
    //   0	229	1	paramString	String
    //   7	217	2	localHwParcel	android.os.HwParcel
    //   15	29	3	localIHwBinder	android.os.IHwBinder
    //   23	179	4	localObject	Object
    //   67	24	5	i	int
    // Exception table:
    //   from	to	target	type
    //   8	16	169	finally
    //   20	69	169	finally
    //   74	106	169	finally
    //   112	156	169	finally
    //   174	210	169	finally
    //   8	16	173	java/lang/Exception
    //   20	69	173	java/lang/Exception
    //   74	106	173	java/lang/Exception
    //   112	156	173	java/lang/Exception
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/IMiOob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */