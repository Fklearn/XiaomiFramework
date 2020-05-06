package miui.securitycenter.powercenter;

import java.io.FileInputStream;
import java.io.IOException;

class BatteryStatsUtils
{
  private static final String TAG = "BatteryStatsHelper";
  
  /* Error */
  static com.android.internal.os.BatteryStatsImpl getBatteryStats()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_0
    //   2: aconst_null
    //   3: astore_1
    //   4: aconst_null
    //   5: astore_2
    //   6: aconst_null
    //   7: astore_3
    //   8: ldc 21
    //   10: invokestatic 27	android/os/ServiceManager:getService	(Ljava/lang/String;)Landroid/os/IBinder;
    //   13: invokestatic 33	com/android/internal/app/IBatteryStats$Stub:asInterface	(Landroid/os/IBinder;)Lcom/android/internal/app/IBatteryStats;
    //   16: astore 4
    //   18: aload_2
    //   19: astore 5
    //   21: aload 4
    //   23: invokeinterface 39 1 0
    //   28: astore 6
    //   30: aload_1
    //   31: astore 5
    //   33: aload 6
    //   35: ifnull +193 -> 228
    //   38: aload_2
    //   39: astore 5
    //   41: new 41	android/os/ParcelFileDescriptor$AutoCloseInputStream
    //   44: astore_1
    //   45: aload_2
    //   46: astore 5
    //   48: aload_1
    //   49: aload 6
    //   51: invokespecial 44	android/os/ParcelFileDescriptor$AutoCloseInputStream:<init>	(Landroid/os/ParcelFileDescriptor;)V
    //   54: aload_2
    //   55: astore 5
    //   57: invokestatic 50	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   60: astore 4
    //   62: aload_1
    //   63: aload 6
    //   65: invokevirtual 56	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   68: invokestatic 62	android/os/MemoryFile:getSize	(Ljava/io/FileDescriptor;)I
    //   71: invokestatic 66	miui/securitycenter/powercenter/BatteryStatsUtils:readFully	(Ljava/io/FileInputStream;I)[B
    //   74: astore 5
    //   76: aload 4
    //   78: aload 5
    //   80: iconst_0
    //   81: aload 5
    //   83: arraylength
    //   84: invokevirtual 70	android/os/Parcel:unmarshall	([BII)V
    //   87: aload 4
    //   89: iconst_0
    //   90: invokevirtual 74	android/os/Parcel:setDataPosition	(I)V
    //   93: getstatic 80	com/android/internal/os/BatteryStatsImpl:CREATOR	Landroid/os/Parcelable$Creator;
    //   96: aload 4
    //   98: invokeinterface 86 2 0
    //   103: checkcast 76	com/android/internal/os/BatteryStatsImpl
    //   106: astore 5
    //   108: aload 5
    //   110: astore_0
    //   111: aload_0
    //   112: astore 5
    //   114: aload 4
    //   116: invokevirtual 89	android/os/Parcel:recycle	()V
    //   119: aload_0
    //   120: astore 5
    //   122: aload_1
    //   123: invokevirtual 94	java/io/FileInputStream:close	()V
    //   126: aload_0
    //   127: astore 5
    //   129: goto +99 -> 228
    //   132: astore_2
    //   133: aload_0
    //   134: astore 5
    //   136: ldc 96
    //   138: ldc 98
    //   140: aload_2
    //   141: invokestatic 104	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   144: pop
    //   145: aload_0
    //   146: astore 5
    //   148: goto +80 -> 228
    //   151: astore_0
    //   152: goto +40 -> 192
    //   155: astore 5
    //   157: ldc 8
    //   159: ldc 106
    //   161: aload 5
    //   163: invokestatic 109	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   166: pop
    //   167: aload_2
    //   168: astore 5
    //   170: aload 4
    //   172: invokevirtual 89	android/os/Parcel:recycle	()V
    //   175: aload_2
    //   176: astore 5
    //   178: aload_1
    //   179: invokevirtual 94	java/io/FileInputStream:close	()V
    //   182: aload_3
    //   183: astore 5
    //   185: goto -56 -> 129
    //   188: astore_2
    //   189: goto -56 -> 133
    //   192: aload_2
    //   193: astore 5
    //   195: aload 4
    //   197: invokevirtual 89	android/os/Parcel:recycle	()V
    //   200: aload_2
    //   201: astore 5
    //   203: aload_1
    //   204: invokevirtual 94	java/io/FileInputStream:close	()V
    //   207: goto +16 -> 223
    //   210: astore_3
    //   211: aload_2
    //   212: astore 5
    //   214: ldc 96
    //   216: ldc 98
    //   218: aload_3
    //   219: invokestatic 104	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   222: pop
    //   223: aload_2
    //   224: astore 5
    //   226: aload_0
    //   227: athrow
    //   228: goto +13 -> 241
    //   231: astore_0
    //   232: ldc 8
    //   234: ldc 111
    //   236: aload_0
    //   237: invokestatic 104	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   240: pop
    //   241: aload 5
    //   243: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   1	145	0	localObject1	Object
    //   151	76	0	localObject2	Object
    //   231	6	0	localRemoteException	android.os.RemoteException
    //   3	201	1	localAutoCloseInputStream	android.os.ParcelFileDescriptor.AutoCloseInputStream
    //   5	50	2	localObject3	Object
    //   132	44	2	localIOException1	IOException
    //   188	36	2	localIOException2	IOException
    //   7	176	3	localObject4	Object
    //   210	9	3	localIOException3	IOException
    //   16	180	4	localObject5	Object
    //   19	128	5	localObject6	Object
    //   155	7	5	localIOException4	IOException
    //   168	74	5	localObject7	Object
    //   28	36	6	localParcelFileDescriptor	android.os.ParcelFileDescriptor
    // Exception table:
    //   from	to	target	type
    //   122	126	132	java/io/IOException
    //   62	108	151	finally
    //   157	167	151	finally
    //   62	108	155	java/io/IOException
    //   178	182	188	java/io/IOException
    //   203	207	210	java/io/IOException
    //   21	30	231	android/os/RemoteException
    //   41	45	231	android/os/RemoteException
    //   48	54	231	android/os/RemoteException
    //   57	62	231	android/os/RemoteException
    //   114	119	231	android/os/RemoteException
    //   122	126	231	android/os/RemoteException
    //   136	145	231	android/os/RemoteException
    //   170	175	231	android/os/RemoteException
    //   178	182	231	android/os/RemoteException
    //   195	200	231	android/os/RemoteException
    //   203	207	231	android/os/RemoteException
    //   214	223	231	android/os/RemoteException
    //   226	228	231	android/os/RemoteException
  }
  
  private static byte[] readFully(FileInputStream paramFileInputStream, int paramInt)
    throws IOException
  {
    int i = 0;
    Object localObject1 = new byte[paramInt];
    paramInt = i;
    for (;;)
    {
      i = paramFileInputStream.read((byte[])localObject1, paramInt, localObject1.length - paramInt);
      if (i <= 0) {
        return (byte[])localObject1;
      }
      paramInt += i;
      i = paramFileInputStream.available();
      Object localObject2 = localObject1;
      if (i > localObject1.length - paramInt)
      {
        localObject2 = new byte[paramInt + i];
        System.arraycopy(localObject1, 0, localObject2, 0, paramInt);
      }
      localObject1 = localObject2;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securitycenter/powercenter/BatteryStatsUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */