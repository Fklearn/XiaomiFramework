package android.view.inputmethod;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.util.Slog;
import java.util.List;

public class InputMethodSubtypeArray
{
  private static final String TAG = "InputMethodSubtypeArray";
  private volatile byte[] mCompressedData;
  private final int mCount;
  private volatile int mDecompressedSize;
  private volatile InputMethodSubtype[] mInstance;
  private final Object mLockObject = new Object();
  
  public InputMethodSubtypeArray(Parcel paramParcel)
  {
    this.mCount = paramParcel.readInt();
    if (this.mCount > 0)
    {
      this.mDecompressedSize = paramParcel.readInt();
      this.mCompressedData = paramParcel.createByteArray();
    }
  }
  
  @UnsupportedAppUsage
  public InputMethodSubtypeArray(List<InputMethodSubtype> paramList)
  {
    if (paramList == null)
    {
      this.mCount = 0;
      return;
    }
    this.mCount = paramList.size();
    this.mInstance = ((InputMethodSubtype[])paramList.toArray(new InputMethodSubtype[this.mCount]));
  }
  
  /* Error */
  private static byte[] compress(byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: new 79	java/io/ByteArrayOutputStream
    //   3: astore_1
    //   4: aload_1
    //   5: invokespecial 80	java/io/ByteArrayOutputStream:<init>	()V
    //   8: new 82	java/util/zip/GZIPOutputStream
    //   11: astore_2
    //   12: aload_2
    //   13: aload_1
    //   14: invokespecial 85	java/util/zip/GZIPOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   17: aload_2
    //   18: aload_0
    //   19: invokevirtual 89	java/util/zip/GZIPOutputStream:write	([B)V
    //   22: aload_2
    //   23: invokevirtual 92	java/util/zip/GZIPOutputStream:finish	()V
    //   26: aload_1
    //   27: invokevirtual 95	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   30: astore_0
    //   31: aconst_null
    //   32: aload_2
    //   33: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   36: aconst_null
    //   37: aload_1
    //   38: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   41: aload_0
    //   42: areturn
    //   43: astore_0
    //   44: aload_0
    //   45: athrow
    //   46: astore_3
    //   47: aload_0
    //   48: aload_2
    //   49: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   52: aload_3
    //   53: athrow
    //   54: astore_2
    //   55: aload_2
    //   56: athrow
    //   57: astore_0
    //   58: aload_2
    //   59: aload_1
    //   60: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   63: aload_0
    //   64: athrow
    //   65: astore_0
    //   66: ldc 8
    //   68: ldc 99
    //   70: aload_0
    //   71: invokestatic 105	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   74: pop
    //   75: aconst_null
    //   76: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	77	0	paramArrayOfByte	byte[]
    //   3	57	1	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   11	38	2	localGZIPOutputStream	java.util.zip.GZIPOutputStream
    //   54	5	2	localThrowable	Throwable
    //   46	7	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   17	31	43	finally
    //   44	46	46	finally
    //   8	17	54	finally
    //   31	36	54	finally
    //   47	54	54	finally
    //   55	57	57	finally
    //   0	8	65	java/lang/Exception
    //   36	41	65	java/lang/Exception
    //   58	65	65	java/lang/Exception
  }
  
  /* Error */
  private static byte[] decompress(byte[] paramArrayOfByte, int paramInt)
  {
    // Byte code:
    //   0: new 109	java/io/ByteArrayInputStream
    //   3: astore_2
    //   4: aload_2
    //   5: aload_0
    //   6: invokespecial 111	java/io/ByteArrayInputStream:<init>	([B)V
    //   9: new 113	java/util/zip/GZIPInputStream
    //   12: astore_0
    //   13: aload_0
    //   14: aload_2
    //   15: invokespecial 116	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   18: iload_1
    //   19: newarray <illegal type>
    //   21: astore_3
    //   22: iconst_0
    //   23: istore 4
    //   25: iload 4
    //   27: aload_3
    //   28: arraylength
    //   29: if_icmpge +35 -> 64
    //   32: aload_0
    //   33: aload_3
    //   34: iload 4
    //   36: aload_3
    //   37: arraylength
    //   38: iload 4
    //   40: isub
    //   41: invokevirtual 120	java/util/zip/GZIPInputStream:read	([BII)I
    //   44: istore 5
    //   46: iload 5
    //   48: ifge +6 -> 54
    //   51: goto +13 -> 64
    //   54: iload 4
    //   56: iload 5
    //   58: iadd
    //   59: istore 4
    //   61: goto -36 -> 25
    //   64: iload_1
    //   65: iload 4
    //   67: if_icmpeq +15 -> 82
    //   70: aconst_null
    //   71: aload_0
    //   72: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   75: aconst_null
    //   76: aload_2
    //   77: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   80: aconst_null
    //   81: areturn
    //   82: aconst_null
    //   83: aload_0
    //   84: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   87: aconst_null
    //   88: aload_2
    //   89: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   92: aload_3
    //   93: areturn
    //   94: astore 6
    //   96: aload 6
    //   98: athrow
    //   99: astore_3
    //   100: aload 6
    //   102: aload_0
    //   103: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   106: aload_3
    //   107: athrow
    //   108: astore_3
    //   109: aload_3
    //   110: athrow
    //   111: astore_0
    //   112: aload_3
    //   113: aload_2
    //   114: invokestatic 97	android/view/inputmethod/InputMethodSubtypeArray:$closeResource	(Ljava/lang/Throwable;Ljava/lang/AutoCloseable;)V
    //   117: aload_0
    //   118: athrow
    //   119: astore_0
    //   120: ldc 8
    //   122: ldc 122
    //   124: aload_0
    //   125: invokestatic 105	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   128: pop
    //   129: aconst_null
    //   130: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	131	0	paramArrayOfByte	byte[]
    //   0	131	1	paramInt	int
    //   3	111	2	localByteArrayInputStream	java.io.ByteArrayInputStream
    //   21	72	3	arrayOfByte	byte[]
    //   99	8	3	localObject	Object
    //   108	5	3	localThrowable1	Throwable
    //   23	45	4	i	int
    //   44	15	5	j	int
    //   94	7	6	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   18	22	94	finally
    //   25	46	94	finally
    //   96	99	99	finally
    //   9	18	108	finally
    //   70	75	108	finally
    //   82	87	108	finally
    //   100	108	108	finally
    //   109	111	111	finally
    //   0	9	119	java/lang/Exception
    //   75	80	119	java/lang/Exception
    //   87	92	119	java/lang/Exception
    //   112	119	119	java/lang/Exception
  }
  
  private static byte[] marshall(InputMethodSubtype[] paramArrayOfInputMethodSubtype)
  {
    Object localObject = null;
    try
    {
      Parcel localParcel = Parcel.obtain();
      localObject = localParcel;
      localParcel.writeTypedArray(paramArrayOfInputMethodSubtype, 0);
      localObject = localParcel;
      paramArrayOfInputMethodSubtype = localParcel.marshall();
      localParcel.recycle();
      return paramArrayOfInputMethodSubtype;
    }
    finally
    {
      if (localObject != null) {
        ((Parcel)localObject).recycle();
      }
    }
  }
  
  private static InputMethodSubtype[] unmarshall(byte[] paramArrayOfByte)
  {
    Object localObject = null;
    try
    {
      Parcel localParcel = Parcel.obtain();
      localObject = localParcel;
      localParcel.unmarshall(paramArrayOfByte, 0, paramArrayOfByte.length);
      localObject = localParcel;
      localParcel.setDataPosition(0);
      localObject = localParcel;
      paramArrayOfByte = (InputMethodSubtype[])localParcel.createTypedArray(InputMethodSubtype.CREATOR);
      localParcel.recycle();
      return paramArrayOfByte;
    }
    finally
    {
      if (localObject != null) {
        ((Parcel)localObject).recycle();
      }
    }
  }
  
  public InputMethodSubtype get(int paramInt)
  {
    if ((paramInt >= 0) && (this.mCount > paramInt))
    {
      InputMethodSubtype[] arrayOfInputMethodSubtype = this.mInstance;
      Object localObject1 = arrayOfInputMethodSubtype;
      if (arrayOfInputMethodSubtype == null) {
        synchronized (this.mLockObject)
        {
          arrayOfInputMethodSubtype = this.mInstance;
          localObject1 = arrayOfInputMethodSubtype;
          if (arrayOfInputMethodSubtype == null)
          {
            localObject1 = decompress(this.mCompressedData, this.mDecompressedSize);
            this.mCompressedData = null;
            this.mDecompressedSize = 0;
            if (localObject1 != null)
            {
              localObject1 = unmarshall((byte[])localObject1);
            }
            else
            {
              Slog.e("InputMethodSubtypeArray", "Failed to decompress data. Returns null as fallback.");
              localObject1 = new InputMethodSubtype[this.mCount];
            }
            this.mInstance = ((InputMethodSubtype[])localObject1);
          }
        }
      }
      return localObject2[paramInt];
    }
    throw new ArrayIndexOutOfBoundsException();
  }
  
  public int getCount()
  {
    return this.mCount;
  }
  
  public void writeToParcel(Parcel paramParcel)
  {
    int i = this.mCount;
    if (i == 0)
    {
      paramParcel.writeInt(i);
      return;
    }
    byte[] arrayOfByte1 = this.mCompressedData;
    int j = this.mDecompressedSize;
    byte[] arrayOfByte2 = arrayOfByte1;
    i = j;
    if (arrayOfByte1 == null)
    {
      arrayOfByte2 = arrayOfByte1;
      i = j;
      if (j == 0) {
        synchronized (this.mLockObject)
        {
          arrayOfByte1 = this.mCompressedData;
          j = this.mDecompressedSize;
          arrayOfByte2 = arrayOfByte1;
          i = j;
          if (arrayOfByte1 == null)
          {
            arrayOfByte2 = arrayOfByte1;
            i = j;
            if (j == 0)
            {
              arrayOfByte1 = marshall(this.mInstance);
              arrayOfByte2 = compress(arrayOfByte1);
              if (arrayOfByte2 == null)
              {
                i = -1;
                Slog.i("InputMethodSubtypeArray", "Failed to compress data.");
              }
              else
              {
                i = arrayOfByte1.length;
              }
              this.mDecompressedSize = i;
              this.mCompressedData = arrayOfByte2;
            }
          }
        }
      }
    }
    if ((arrayOfByte2 != null) && (i > 0))
    {
      paramParcel.writeInt(this.mCount);
      paramParcel.writeInt(i);
      paramParcel.writeByteArray(arrayOfByte2);
    }
    else
    {
      Slog.i("InputMethodSubtypeArray", "Unexpected state. Behaving as an empty array.");
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputMethodSubtypeArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */