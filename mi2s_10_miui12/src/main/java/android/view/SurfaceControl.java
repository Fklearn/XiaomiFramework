package android.view;

import android.annotation.UnsupportedAppUsage;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.graphics.ColorSpace.Named;
import android.graphics.GraphicBuffer;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.DisplayedContentSample;
import android.hardware.display.DisplayedContentSamplingAttributes;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import dalvik.system.CloseGuard;
import java.io.Closeable;
import java.util.Objects;
import libcore.util.NativeAllocationRegistry;

public final class SurfaceControl
  implements Parcelable
{
  public static final Parcelable.Creator<SurfaceControl> CREATOR = new Parcelable.Creator()
  {
    public SurfaceControl createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SurfaceControl(paramAnonymousParcel, null);
    }
    
    public SurfaceControl[] newArray(int paramAnonymousInt)
    {
      return new SurfaceControl[paramAnonymousInt];
    }
  };
  public static final int CURSOR_WINDOW = 8192;
  private static final int FLAG_BLUR = 16;
  private static final int FLAG_BLUR_CURRENT = 8;
  private static final int FLAG_CAST = 64;
  private static final int FLAG_RECORD_HIDE = 32;
  public static final int FX_SURFACE_CONTAINER = 524288;
  public static final int FX_SURFACE_DIM = 131072;
  public static final int FX_SURFACE_MASK = 983040;
  public static final int FX_SURFACE_NORMAL = 0;
  @UnsupportedAppUsage
  public static final int HIDDEN = 4;
  private static final int INTERNAL_DATASPACE_DISPLAY_P3 = 143261696;
  private static final int INTERNAL_DATASPACE_SCRGB = 411107328;
  private static final int INTERNAL_DATASPACE_SRGB = 142671872;
  public static final int METADATA_OWNER_UID = 1;
  public static final int METADATA_TASK_ID = 3;
  public static final int METADATA_WINDOW_TYPE = 2;
  public static final int NON_PREMULTIPLIED = 256;
  public static final int OPAQUE = 1024;
  public static final int POWER_MODE_DOZE = 1;
  public static final int POWER_MODE_DOZE_SUSPEND = 3;
  public static final int POWER_MODE_NORMAL = 2;
  public static final int POWER_MODE_OFF = 0;
  public static final int POWER_MODE_ON_SUSPEND = 4;
  public static final int PROTECTED_APP = 2048;
  public static final int SECURE = 128;
  private static final int SURFACE_HIDDEN = 1;
  private static final int SURFACE_OPAQUE = 2;
  private static final String TAG = "SurfaceControl";
  public static final int WINDOW_TYPE_DONT_SCREENSHOT = 441731;
  static Transaction sGlobalTransaction;
  static long sTransactionNestCount = 0L;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  @GuardedBy({"mSizeLock"})
  private int mHeight;
  private String mName;
  long mNativeObject;
  private final Object mSizeLock = new Object();
  @GuardedBy({"mSizeLock"})
  private int mWidth;
  
  public SurfaceControl()
  {
    this.mCloseGuard.open("release");
  }
  
  private SurfaceControl(Parcel paramParcel)
  {
    readFromParcel(paramParcel);
    this.mCloseGuard.open("release");
  }
  
  public SurfaceControl(SurfaceControl paramSurfaceControl)
  {
    this.mName = paramSurfaceControl.mName;
    this.mWidth = paramSurfaceControl.mWidth;
    this.mHeight = paramSurfaceControl.mHeight;
    this.mNativeObject = paramSurfaceControl.mNativeObject;
    paramSurfaceControl.mCloseGuard.close();
    paramSurfaceControl.mNativeObject = 0L;
    this.mCloseGuard.open("release");
  }
  
  /* Error */
  private SurfaceControl(SurfaceSession paramSurfaceSession, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, Surface paramSurface, SparseIntArray paramSparseIntArray)
    throws Surface.OutOfResourcesException, IllegalArgumentException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 109	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: invokestatic 115	dalvik/system/CloseGuard:get	()Ldalvik/system/CloseGuard;
    //   8: putfield 117	android/view/SurfaceControl:mCloseGuard	Ldalvik/system/CloseGuard;
    //   11: aload_0
    //   12: new 4	java/lang/Object
    //   15: dup
    //   16: invokespecial 109	java/lang/Object:<init>	()V
    //   19: putfield 119	android/view/SurfaceControl:mSizeLock	Ljava/lang/Object;
    //   22: aload_2
    //   23: ifnull +248 -> 271
    //   26: iload 6
    //   28: iconst_4
    //   29: iand
    //   30: ifne +45 -> 75
    //   33: new 151	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 152	java/lang/StringBuilder:<init>	()V
    //   40: astore 9
    //   42: aload 9
    //   44: ldc -102
    //   46: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload 9
    //   52: aload_2
    //   53: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: ldc 82
    //   59: aload 9
    //   61: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   64: new 164	java/lang/Throwable
    //   67: dup
    //   68: invokespecial 165	java/lang/Throwable:<init>	()V
    //   71: invokestatic 171	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   74: pop
    //   75: aload_0
    //   76: aload_2
    //   77: putfield 135	android/view/SurfaceControl:mName	Ljava/lang/String;
    //   80: aload_0
    //   81: iload_3
    //   82: putfield 137	android/view/SurfaceControl:mWidth	I
    //   85: aload_0
    //   86: iload 4
    //   88: putfield 139	android/view/SurfaceControl:mHeight	I
    //   91: invokestatic 177	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   94: astore 9
    //   96: aload 8
    //   98: ifnull +93 -> 191
    //   101: aload 8
    //   103: invokevirtual 183	android/util/SparseIntArray:size	()I
    //   106: ifle +85 -> 191
    //   109: aload 9
    //   111: aload 8
    //   113: invokevirtual 183	android/util/SparseIntArray:size	()I
    //   116: invokevirtual 187	android/os/Parcel:writeInt	(I)V
    //   119: iconst_0
    //   120: istore 10
    //   122: iload 10
    //   124: aload 8
    //   126: invokevirtual 183	android/util/SparseIntArray:size	()I
    //   129: if_icmpge +49 -> 178
    //   132: aload 9
    //   134: aload 8
    //   136: iload 10
    //   138: invokevirtual 191	android/util/SparseIntArray:keyAt	(I)I
    //   141: invokevirtual 187	android/os/Parcel:writeInt	(I)V
    //   144: aload 9
    //   146: iconst_4
    //   147: invokestatic 197	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   150: invokestatic 203	java/nio/ByteOrder:nativeOrder	()Ljava/nio/ByteOrder;
    //   153: invokevirtual 207	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   156: aload 8
    //   158: iload 10
    //   160: invokevirtual 210	android/util/SparseIntArray:valueAt	(I)I
    //   163: invokevirtual 213	java/nio/ByteBuffer:putInt	(I)Ljava/nio/ByteBuffer;
    //   166: invokevirtual 217	java/nio/ByteBuffer:array	()[B
    //   169: invokevirtual 221	android/os/Parcel:writeByteArray	([B)V
    //   172: iinc 10 1
    //   175: goto -53 -> 122
    //   178: aload 9
    //   180: iconst_0
    //   181: invokevirtual 224	android/os/Parcel:setDataPosition	(I)V
    //   184: goto +7 -> 191
    //   187: astore_1
    //   188: goto +76 -> 264
    //   191: aload 7
    //   193: ifnull +13 -> 206
    //   196: aload 7
    //   198: getfield 227	android/view/Surface:mNativeObject	J
    //   201: lstore 11
    //   203: goto +6 -> 209
    //   206: lconst_0
    //   207: lstore 11
    //   209: aload_0
    //   210: aload_1
    //   211: aload_2
    //   212: iload_3
    //   213: iload 4
    //   215: iload 5
    //   217: iload 6
    //   219: lload 11
    //   221: aload 9
    //   223: invokestatic 231	android/view/SurfaceControl:nativeCreatewithParentSurface	(Landroid/view/SurfaceSession;Ljava/lang/String;IIIIJLandroid/os/Parcel;)J
    //   226: putfield 141	android/view/SurfaceControl:mNativeObject	J
    //   229: aload 9
    //   231: invokevirtual 234	android/os/Parcel:recycle	()V
    //   234: aload_0
    //   235: getfield 141	android/view/SurfaceControl:mNativeObject	J
    //   238: lconst_0
    //   239: lcmp
    //   240: ifeq +13 -> 253
    //   243: aload_0
    //   244: getfield 117	android/view/SurfaceControl:mCloseGuard	Ldalvik/system/CloseGuard;
    //   247: ldc 121
    //   249: invokevirtual 125	dalvik/system/CloseGuard:open	(Ljava/lang/String;)V
    //   252: return
    //   253: new 147	android/view/Surface$OutOfResourcesException
    //   256: dup
    //   257: ldc -20
    //   259: invokespecial 238	android/view/Surface$OutOfResourcesException:<init>	(Ljava/lang/String;)V
    //   262: athrow
    //   263: astore_1
    //   264: aload 9
    //   266: invokevirtual 234	android/os/Parcel:recycle	()V
    //   269: aload_1
    //   270: athrow
    //   271: new 149	java/lang/IllegalArgumentException
    //   274: dup
    //   275: ldc -16
    //   277: invokespecial 241	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   280: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	281	0	this	SurfaceControl
    //   0	281	1	paramSurfaceSession	SurfaceSession
    //   0	281	2	paramString	String
    //   0	281	3	paramInt1	int
    //   0	281	4	paramInt2	int
    //   0	281	5	paramInt3	int
    //   0	281	6	paramInt4	int
    //   0	281	7	paramSurface	Surface
    //   0	281	8	paramSparseIntArray	SparseIntArray
    //   40	225	9	localObject	Object
    //   120	53	10	i	int
    //   201	19	11	l	long
    // Exception table:
    //   from	to	target	type
    //   101	119	187	finally
    //   122	144	187	finally
    //   144	172	187	finally
    //   178	184	187	finally
    //   196	203	187	finally
    //   209	229	263	finally
  }
  
  /* Error */
  private SurfaceControl(SurfaceSession paramSurfaceSession, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, SurfaceControl paramSurfaceControl, SparseIntArray paramSparseIntArray)
    throws Surface.OutOfResourcesException, IllegalArgumentException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 109	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: invokestatic 115	dalvik/system/CloseGuard:get	()Ldalvik/system/CloseGuard;
    //   8: putfield 117	android/view/SurfaceControl:mCloseGuard	Ldalvik/system/CloseGuard;
    //   11: aload_0
    //   12: new 4	java/lang/Object
    //   15: dup
    //   16: invokespecial 109	java/lang/Object:<init>	()V
    //   19: putfield 119	android/view/SurfaceControl:mSizeLock	Ljava/lang/Object;
    //   22: aload_2
    //   23: ifnull +248 -> 271
    //   26: iload 6
    //   28: iconst_4
    //   29: iand
    //   30: ifne +45 -> 75
    //   33: new 151	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 152	java/lang/StringBuilder:<init>	()V
    //   40: astore 9
    //   42: aload 9
    //   44: ldc -102
    //   46: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: pop
    //   50: aload 9
    //   52: aload_2
    //   53: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: ldc 82
    //   59: aload 9
    //   61: invokevirtual 162	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   64: new 164	java/lang/Throwable
    //   67: dup
    //   68: invokespecial 165	java/lang/Throwable:<init>	()V
    //   71: invokestatic 171	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   74: pop
    //   75: aload_0
    //   76: aload_2
    //   77: putfield 135	android/view/SurfaceControl:mName	Ljava/lang/String;
    //   80: aload_0
    //   81: iload_3
    //   82: putfield 137	android/view/SurfaceControl:mWidth	I
    //   85: aload_0
    //   86: iload 4
    //   88: putfield 139	android/view/SurfaceControl:mHeight	I
    //   91: invokestatic 177	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   94: astore 9
    //   96: aload 8
    //   98: ifnull +93 -> 191
    //   101: aload 8
    //   103: invokevirtual 183	android/util/SparseIntArray:size	()I
    //   106: ifle +85 -> 191
    //   109: aload 9
    //   111: aload 8
    //   113: invokevirtual 183	android/util/SparseIntArray:size	()I
    //   116: invokevirtual 187	android/os/Parcel:writeInt	(I)V
    //   119: iconst_0
    //   120: istore 10
    //   122: iload 10
    //   124: aload 8
    //   126: invokevirtual 183	android/util/SparseIntArray:size	()I
    //   129: if_icmpge +49 -> 178
    //   132: aload 9
    //   134: aload 8
    //   136: iload 10
    //   138: invokevirtual 191	android/util/SparseIntArray:keyAt	(I)I
    //   141: invokevirtual 187	android/os/Parcel:writeInt	(I)V
    //   144: aload 9
    //   146: iconst_4
    //   147: invokestatic 197	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   150: invokestatic 203	java/nio/ByteOrder:nativeOrder	()Ljava/nio/ByteOrder;
    //   153: invokevirtual 207	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   156: aload 8
    //   158: iload 10
    //   160: invokevirtual 210	android/util/SparseIntArray:valueAt	(I)I
    //   163: invokevirtual 213	java/nio/ByteBuffer:putInt	(I)Ljava/nio/ByteBuffer;
    //   166: invokevirtual 217	java/nio/ByteBuffer:array	()[B
    //   169: invokevirtual 221	android/os/Parcel:writeByteArray	([B)V
    //   172: iinc 10 1
    //   175: goto -53 -> 122
    //   178: aload 9
    //   180: iconst_0
    //   181: invokevirtual 224	android/os/Parcel:setDataPosition	(I)V
    //   184: goto +7 -> 191
    //   187: astore_1
    //   188: goto +76 -> 264
    //   191: aload 7
    //   193: ifnull +13 -> 206
    //   196: aload 7
    //   198: getfield 141	android/view/SurfaceControl:mNativeObject	J
    //   201: lstore 11
    //   203: goto +6 -> 209
    //   206: lconst_0
    //   207: lstore 11
    //   209: aload_0
    //   210: aload_1
    //   211: aload_2
    //   212: iload_3
    //   213: iload 4
    //   215: iload 5
    //   217: iload 6
    //   219: lload 11
    //   221: aload 9
    //   223: invokestatic 249	android/view/SurfaceControl:nativeCreate	(Landroid/view/SurfaceSession;Ljava/lang/String;IIIIJLandroid/os/Parcel;)J
    //   226: putfield 141	android/view/SurfaceControl:mNativeObject	J
    //   229: aload 9
    //   231: invokevirtual 234	android/os/Parcel:recycle	()V
    //   234: aload_0
    //   235: getfield 141	android/view/SurfaceControl:mNativeObject	J
    //   238: lconst_0
    //   239: lcmp
    //   240: ifeq +13 -> 253
    //   243: aload_0
    //   244: getfield 117	android/view/SurfaceControl:mCloseGuard	Ldalvik/system/CloseGuard;
    //   247: ldc 121
    //   249: invokevirtual 125	dalvik/system/CloseGuard:open	(Ljava/lang/String;)V
    //   252: return
    //   253: new 147	android/view/Surface$OutOfResourcesException
    //   256: dup
    //   257: ldc -20
    //   259: invokespecial 238	android/view/Surface$OutOfResourcesException:<init>	(Ljava/lang/String;)V
    //   262: athrow
    //   263: astore_1
    //   264: aload 9
    //   266: invokevirtual 234	android/os/Parcel:recycle	()V
    //   269: aload_1
    //   270: athrow
    //   271: new 149	java/lang/IllegalArgumentException
    //   274: dup
    //   275: ldc -16
    //   277: invokespecial 241	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   280: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	281	0	this	SurfaceControl
    //   0	281	1	paramSurfaceSession	SurfaceSession
    //   0	281	2	paramString	String
    //   0	281	3	paramInt1	int
    //   0	281	4	paramInt2	int
    //   0	281	5	paramInt3	int
    //   0	281	6	paramInt4	int
    //   0	281	7	paramSurfaceControl	SurfaceControl
    //   0	281	8	paramSparseIntArray	SparseIntArray
    //   40	225	9	localObject	Object
    //   120	53	10	i	int
    //   201	19	11	l	long
    // Exception table:
    //   from	to	target	type
    //   101	119	187	finally
    //   122	144	187	finally
    //   144	172	187	finally
    //   178	184	187	finally
    //   196	203	187	finally
    //   209	229	263	finally
  }
  
  private void assignNativeObject(long paramLong)
  {
    if (this.mNativeObject != 0L) {
      release();
    }
    this.mNativeObject = paramLong;
  }
  
  public static ScreenshotGraphicBuffer captureLayers(IBinder paramIBinder, Rect paramRect, float paramFloat)
  {
    return nativeCaptureLayers(getInternalDisplayToken(), paramIBinder, paramRect, paramFloat, null);
  }
  
  public static ScreenshotGraphicBuffer captureLayersExcluding(IBinder paramIBinder, Rect paramRect, float paramFloat, IBinder[] paramArrayOfIBinder)
  {
    return nativeCaptureLayers(getInternalDisplayToken(), paramIBinder, paramRect, paramFloat, paramArrayOfIBinder);
  }
  
  private void checkNotReleased()
  {
    if (this.mNativeObject != 0L) {
      return;
    }
    throw new NullPointerException("mNativeObject is null. Have you called release() already?");
  }
  
  public static boolean clearAnimationFrameStats()
  {
    return nativeClearAnimationFrameStats();
  }
  
  @UnsupportedAppUsage
  public static void closeTransaction()
  {
    try
    {
      if (sTransactionNestCount == 0L)
      {
        Log.e("SurfaceControl", "Call to SurfaceControl.closeTransaction without matching openTransaction");
      }
      else
      {
        long l = sTransactionNestCount - 1L;
        sTransactionNestCount = l;
        if (l > 0L) {
          return;
        }
      }
      sGlobalTransaction.apply();
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public static IBinder createDisplay(String paramString, boolean paramBoolean)
  {
    if (paramString != null) {
      return nativeCreateDisplay(paramString, paramBoolean);
    }
    throw new IllegalArgumentException("name must not be null");
  }
  
  @UnsupportedAppUsage
  public static void destroyDisplay(IBinder paramIBinder)
  {
    if (paramIBinder != null)
    {
      nativeDestroyDisplay(paramIBinder);
      return;
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static int getActiveColorMode(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetActiveColorMode(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static int getActiveConfig(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetActiveConfig(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static int[] getAllowedDisplayConfigs(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetAllowedDisplayConfigs(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static boolean getAnimationFrameStats(WindowAnimationFrameStats paramWindowAnimationFrameStats)
  {
    return nativeGetAnimationFrameStats(paramWindowAnimationFrameStats);
  }
  
  public static ColorSpace[] getCompositionColorSpaces()
  {
    int[] arrayOfInt = nativeGetCompositionDataspaces();
    ColorSpace localColorSpace = ColorSpace.get(ColorSpace.Named.SRGB);
    ColorSpace[] arrayOfColorSpace = new ColorSpace[2];
    arrayOfColorSpace[0] = localColorSpace;
    arrayOfColorSpace[1] = localColorSpace;
    if (arrayOfInt.length == 2) {
      for (int i = 0; i < 2; i++)
      {
        int j = arrayOfInt[i];
        if (j != 143261696)
        {
          if (j == 411107328) {
            arrayOfColorSpace[i] = ColorSpace.get(ColorSpace.Named.EXTENDED_SRGB);
          }
        }
        else {
          arrayOfColorSpace[i] = ColorSpace.get(ColorSpace.Named.DISPLAY_P3);
        }
      }
    }
    return arrayOfColorSpace;
  }
  
  public static boolean getDisplayBrightnessSupport(IBinder paramIBinder)
  {
    return nativeGetDisplayBrightnessSupport(paramIBinder);
  }
  
  public static int[] getDisplayColorModes(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetDisplayColorModes(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  @UnsupportedAppUsage
  public static PhysicalDisplayInfo[] getDisplayConfigs(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetDisplayConfigs(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static DisplayPrimaries getDisplayNativePrimaries(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetDisplayNativePrimaries(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static DisplayedContentSample getDisplayedContentSample(IBinder paramIBinder, long paramLong1, long paramLong2)
  {
    if (paramIBinder != null) {
      return nativeGetDisplayedContentSample(paramIBinder, paramLong1, paramLong2);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static DisplayedContentSamplingAttributes getDisplayedContentSamplingAttributes(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetDisplayedContentSamplingAttributes(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static Display.HdrCapabilities getHdrCapabilities(IBinder paramIBinder)
  {
    if (paramIBinder != null) {
      return nativeGetHdrCapabilities(paramIBinder);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static IBinder getInternalDisplayToken()
  {
    long[] arrayOfLong = getPhysicalDisplayIds();
    if (arrayOfLong.length == 0) {
      return null;
    }
    return getPhysicalDisplayToken(arrayOfLong[0]);
  }
  
  public static long[] getPhysicalDisplayIds()
  {
    return nativeGetPhysicalDisplayIds();
  }
  
  public static IBinder getPhysicalDisplayToken(long paramLong)
  {
    return nativeGetPhysicalDisplayToken(paramLong);
  }
  
  public static boolean getProtectedContentSupport()
  {
    return nativeGetProtectedContentSupport();
  }
  
  @Deprecated
  public static void mergeToGlobalTransaction(Transaction paramTransaction)
  {
    try
    {
      sGlobalTransaction.merge(paramTransaction);
      return;
    }
    finally {}
  }
  
  private static native void nativeApplyTransaction(long paramLong, boolean paramBoolean);
  
  private static native ScreenshotGraphicBuffer nativeCaptureLayers(IBinder paramIBinder1, IBinder paramIBinder2, Rect paramRect, float paramFloat, IBinder[] paramArrayOfIBinder);
  
  private static native boolean nativeClearAnimationFrameStats();
  
  private static native boolean nativeClearContentFrameStats(long paramLong);
  
  private static native long nativeCopyFromSurfaceControl(long paramLong);
  
  private static native long nativeCreate(SurfaceSession paramSurfaceSession, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, Parcel paramParcel)
    throws Surface.OutOfResourcesException;
  
  private static native IBinder nativeCreateDisplay(String paramString, boolean paramBoolean);
  
  private static native long nativeCreateTransaction();
  
  private static native long nativeCreatewithParentSurface(SurfaceSession paramSurfaceSession, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, Parcel paramParcel)
    throws Surface.OutOfResourcesException;
  
  private static native void nativeDeferTransactionUntil(long paramLong1, long paramLong2, IBinder paramIBinder, long paramLong3);
  
  private static native void nativeDeferTransactionUntilSurface(long paramLong1, long paramLong2, long paramLong3, long paramLong4);
  
  private static native void nativeDestroy(long paramLong);
  
  private static native void nativeDestroyDisplay(IBinder paramIBinder);
  
  private static native void nativeDisconnect(long paramLong);
  
  private static native int nativeGetActiveColorMode(IBinder paramIBinder);
  
  private static native int nativeGetActiveConfig(IBinder paramIBinder);
  
  private static native int[] nativeGetAllowedDisplayConfigs(IBinder paramIBinder);
  
  private static native boolean nativeGetAnimationFrameStats(WindowAnimationFrameStats paramWindowAnimationFrameStats);
  
  private static native int[] nativeGetCompositionDataspaces();
  
  private static native boolean nativeGetContentFrameStats(long paramLong, WindowContentFrameStats paramWindowContentFrameStats);
  
  private static native boolean nativeGetDisplayBrightnessSupport(IBinder paramIBinder);
  
  private static native int[] nativeGetDisplayColorModes(IBinder paramIBinder);
  
  private static native PhysicalDisplayInfo[] nativeGetDisplayConfigs(IBinder paramIBinder);
  
  private static native DisplayPrimaries nativeGetDisplayNativePrimaries(IBinder paramIBinder);
  
  private static native DisplayedContentSample nativeGetDisplayedContentSample(IBinder paramIBinder, long paramLong1, long paramLong2);
  
  private static native DisplayedContentSamplingAttributes nativeGetDisplayedContentSamplingAttributes(IBinder paramIBinder);
  
  private static native IBinder nativeGetHandle(long paramLong);
  
  private static native Display.HdrCapabilities nativeGetHdrCapabilities(IBinder paramIBinder);
  
  private static native long nativeGetNativeTransactionFinalizer();
  
  private static native long[] nativeGetPhysicalDisplayIds();
  
  private static native IBinder nativeGetPhysicalDisplayToken(long paramLong);
  
  private static native boolean nativeGetProtectedContentSupport();
  
  private static native boolean nativeGetTransformToDisplayInverse(long paramLong);
  
  private static native void nativeMergeTransaction(long paramLong1, long paramLong2);
  
  private static native long nativeReadFromParcel(Parcel paramParcel);
  
  private static native void nativeRelease(long paramLong);
  
  private static native void nativeReparent(long paramLong1, long paramLong2, long paramLong3);
  
  private static native void nativeReparentChildren(long paramLong1, long paramLong2, IBinder paramIBinder);
  
  private static native ScreenshotGraphicBuffer nativeScreenshot(IBinder paramIBinder, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2, boolean paramBoolean3);
  
  private static native boolean nativeSetActiveColorMode(IBinder paramIBinder, int paramInt);
  
  private static native boolean nativeSetActiveConfig(IBinder paramIBinder, int paramInt);
  
  private static native boolean nativeSetAllowedDisplayConfigs(IBinder paramIBinder, int[] paramArrayOfInt);
  
  private static native void nativeSetAlpha(long paramLong1, long paramLong2, float paramFloat);
  
  private static native void nativeSetAnimationTransaction(long paramLong);
  
  private static native void nativeSetBlurCrop(long paramLong1, long paramLong2, Rect paramRect1, Rect paramRect2);
  
  private static native void nativeSetBlurMode(long paramLong1, long paramLong2, int paramInt);
  
  private static native void nativeSetBlurRatio(long paramLong1, long paramLong2, float paramFloat);
  
  private static native void nativeSetCastMode(long paramLong1, long paramLong2, IBinder paramIBinder, boolean paramBoolean);
  
  private static native void nativeSetColor(long paramLong1, long paramLong2, float[] paramArrayOfFloat);
  
  private static native void nativeSetColorSpaceAgnostic(long paramLong1, long paramLong2, boolean paramBoolean);
  
  private static native void nativeSetColorTransform(long paramLong1, long paramLong2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2);
  
  private static native void nativeSetCornerRadius(long paramLong1, long paramLong2, float paramFloat);
  
  private static native void nativeSetDiffScreenProjection(long paramLong, IBinder paramIBinder, int paramInt);
  
  private static native boolean nativeSetDisplayBrightness(IBinder paramIBinder, float paramFloat);
  
  private static native void nativeSetDisplayLayerStack(long paramLong, IBinder paramIBinder, int paramInt);
  
  private static native void nativeSetDisplayPowerMode(IBinder paramIBinder, int paramInt);
  
  private static native void nativeSetDisplayProjection(long paramLong, IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9);
  
  private static native void nativeSetDisplaySize(long paramLong, IBinder paramIBinder, int paramInt1, int paramInt2);
  
  private static native void nativeSetDisplaySurface(long paramLong1, IBinder paramIBinder, long paramLong2);
  
  private static native boolean nativeSetDisplayedContentSamplingEnabled(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2);
  
  private static native void nativeSetEarlyWakeup(long paramLong);
  
  private static native void nativeSetFlags(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  private static native void nativeSetGeometry(long paramLong1, long paramLong2, Rect paramRect1, Rect paramRect2, long paramLong3);
  
  private static native void nativeSetGeometryAppliesWithResize(long paramLong1, long paramLong2);
  
  private static native void nativeSetGlobalShadowSettings(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float paramFloat2, float paramFloat3);
  
  private static native void nativeSetInputWindowInfo(long paramLong1, long paramLong2, InputWindowHandle paramInputWindowHandle);
  
  private static native void nativeSetLastFrame(long paramLong1, long paramLong2, IBinder paramIBinder, boolean paramBoolean);
  
  private static native void nativeSetLayer(long paramLong1, long paramLong2, int paramInt);
  
  private static native void nativeSetLayerStack(long paramLong1, long paramLong2, int paramInt);
  
  private static native void nativeSetMatrix(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  private static native void nativeSetMetadata(long paramLong1, long paramLong2, int paramInt, Parcel paramParcel);
  
  private static native void nativeSetOverrideScalingMode(long paramLong1, long paramLong2, int paramInt);
  
  private static native void nativeSetPosition(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2);
  
  private static native void nativeSetRelativeLayer(long paramLong1, long paramLong2, IBinder paramIBinder, int paramInt);
  
  private static native void nativeSetScreenProjection(long paramLong1, long paramLong2, int paramInt);
  
  private static native void nativeSetShadowRadiusParas(long paramLong1, long paramLong2, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5);
  
  private static native void nativeSetSize(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  private static native void nativeSetTransparentRegionHint(long paramLong1, long paramLong2, Region paramRegion);
  
  private static native void nativeSetWindowCrop(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private static native void nativeSeverChildren(long paramLong1, long paramLong2);
  
  private static native void nativeSyncInputWindows(long paramLong);
  
  private static native void nativeTransferTouchFocus(long paramLong, IBinder paramIBinder1, IBinder paramIBinder2);
  
  private static native void nativeWriteToParcel(long paramLong, Parcel paramParcel);
  
  /* Error */
  @UnsupportedAppUsage
  public static void openTransaction()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 501	android/view/SurfaceControl:sGlobalTransaction	Landroid/view/SurfaceControl$Transaction;
    //   6: ifnonnull +15 -> 21
    //   9: new 25	android/view/SurfaceControl$Transaction
    //   12: astore_0
    //   13: aload_0
    //   14: invokespecial 651	android/view/SurfaceControl$Transaction:<init>	()V
    //   17: aload_0
    //   18: putstatic 501	android/view/SurfaceControl:sGlobalTransaction	Landroid/view/SurfaceControl$Transaction;
    //   21: ldc 2
    //   23: monitorenter
    //   24: getstatic 102	android/view/SurfaceControl:sTransactionNestCount	J
    //   27: lconst_1
    //   28: ladd
    //   29: putstatic 102	android/view/SurfaceControl:sTransactionNestCount	J
    //   32: ldc 2
    //   34: monitorexit
    //   35: ldc 2
    //   37: monitorexit
    //   38: return
    //   39: astore_0
    //   40: ldc 2
    //   42: monitorexit
    //   43: aload_0
    //   44: athrow
    //   45: astore_0
    //   46: ldc 2
    //   48: monitorexit
    //   49: aload_0
    //   50: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	6	0	localTransaction	Transaction
    //   39	5	0	localObject1	Object
    //   45	5	0	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   24	35	39	finally
    //   40	43	39	finally
    //   3	21	45	finally
    //   21	24	45	finally
    //   35	38	45	finally
    //   43	45	45	finally
    //   46	49	45	finally
  }
  
  private static void rotateCropForSF(Rect paramRect, int paramInt)
  {
    if ((paramInt == 1) || (paramInt == 3))
    {
      paramInt = paramRect.top;
      paramRect.top = paramRect.left;
      paramRect.left = paramInt;
      paramInt = paramRect.right;
      paramRect.right = paramRect.bottom;
      paramRect.bottom = paramInt;
    }
  }
  
  @UnsupportedAppUsage
  public static Bitmap screenshot(Rect paramRect, int paramInt1, int paramInt2, int paramInt3)
  {
    return screenshot(paramRect, paramInt1, paramInt2, false, paramInt3);
  }
  
  @UnsupportedAppUsage
  public static Bitmap screenshot(Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    IBinder localIBinder = getInternalDisplayToken();
    if (localIBinder == null)
    {
      Log.w("SurfaceControl", "Failed to take screenshot because internal display is disconnected");
      return null;
    }
    int i = 3;
    int j;
    if (paramInt3 != 1)
    {
      j = paramInt3;
      if (paramInt3 != 3) {}
    }
    else
    {
      if (paramInt3 == 1) {
        paramInt3 = i;
      } else {
        paramInt3 = 1;
      }
      j = paramInt3;
    }
    rotateCropForSF(paramRect, j);
    paramRect = screenshotToBuffer(localIBinder, paramRect, paramInt1, paramInt2, paramBoolean, j);
    if (paramRect == null)
    {
      Log.w("SurfaceControl", "Failed to take screenshot");
      return null;
    }
    return Bitmap.wrapHardwareBuffer(paramRect.getGraphicBuffer(), paramRect.getColorSpace());
  }
  
  public static void screenshot(IBinder paramIBinder, Surface paramSurface)
  {
    screenshot(paramIBinder, paramSurface, new Rect(), 0, 0, false, 0);
  }
  
  public static void screenshot(IBinder paramIBinder, Surface paramSurface, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (paramSurface != null)
    {
      paramIBinder = screenshotToBuffer(paramIBinder, paramRect, paramInt1, paramInt2, paramBoolean, paramInt3);
      try
      {
        paramSurface.attachAndQueueBuffer(paramIBinder.getGraphicBuffer());
      }
      catch (RuntimeException paramSurface)
      {
        paramIBinder = new StringBuilder();
        paramIBinder.append("Failed to take screenshot - ");
        paramIBinder.append(paramSurface.getMessage());
        Log.w("SurfaceControl", paramIBinder.toString());
      }
      return;
    }
    throw new IllegalArgumentException("consumer must not be null");
  }
  
  public static void screenshot(IBinder paramIBinder, Surface paramSurface, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2)
  {
    if (paramSurface != null)
    {
      paramIBinder = screenshotToBuffer(paramIBinder, paramRect, paramInt1, paramInt2, paramBoolean1, paramInt3, paramBoolean2);
      try
      {
        paramSurface.attachAndQueueBuffer(paramIBinder.getGraphicBuffer());
      }
      catch (RuntimeException paramIBinder)
      {
        paramSurface = new StringBuilder();
        paramSurface.append("Failed to take screenshot - ");
        paramSurface.append(paramIBinder.getMessage());
        Log.w("SurfaceControl", paramSurface.toString());
      }
      return;
    }
    throw new IllegalArgumentException("consumer must not be null");
  }
  
  public static void screenshot(IBinder paramIBinder, Surface paramSurface, boolean paramBoolean)
  {
    screenshot(paramIBinder, paramSurface, new Rect(), 0, 0, false, 0, paramBoolean);
  }
  
  public static ScreenshotGraphicBuffer screenshotToBuffer(IBinder paramIBinder, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (paramIBinder != null) {
      return screenshotToBuffer(paramIBinder, paramRect, paramInt1, paramInt2, paramBoolean, paramInt3, true);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  private static ScreenshotGraphicBuffer screenshotToBuffer(IBinder paramIBinder, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean1, int paramInt3, boolean paramBoolean2)
  {
    if (paramIBinder != null) {
      return nativeScreenshot(paramIBinder, paramRect, paramInt1, paramInt2, paramBoolean1, paramInt3, false, paramBoolean2);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static ScreenshotGraphicBuffer screenshotToBufferWithSecureLayersUnsafe(IBinder paramIBinder, Rect paramRect, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (paramIBinder != null) {
      return nativeScreenshot(paramIBinder, paramRect, paramInt1, paramInt2, paramBoolean, paramInt3, true, true);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static boolean setActiveColorMode(IBinder paramIBinder, int paramInt)
  {
    if (paramIBinder != null) {
      return nativeSetActiveColorMode(paramIBinder, paramInt);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static boolean setActiveConfig(IBinder paramIBinder, int paramInt)
  {
    if (paramIBinder != null) {
      return nativeSetActiveConfig(paramIBinder, paramInt);
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static boolean setAllowedDisplayConfigs(IBinder paramIBinder, int[] paramArrayOfInt)
  {
    if (paramIBinder != null)
    {
      if (paramArrayOfInt != null) {
        return nativeSetAllowedDisplayConfigs(paramIBinder, paramArrayOfInt);
      }
      throw new IllegalArgumentException("allowedConfigs must not be null");
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static void setAnimationTransaction()
  {
    try
    {
      sGlobalTransaction.setAnimationTransaction();
      return;
    }
    finally {}
  }
  
  public static boolean setDisplayBrightness(IBinder paramIBinder, float paramFloat)
  {
    Objects.requireNonNull(paramIBinder);
    if ((!Float.isNaN(paramFloat)) && (paramFloat <= 1.0F) && ((paramFloat >= 0.0F) || (paramFloat == -1.0F))) {
      return nativeSetDisplayBrightness(paramIBinder, paramFloat);
    }
    throw new IllegalArgumentException("brightness must be a number between 0.0f and 1.0f, or -1 to turn the backlight off.");
  }
  
  @UnsupportedAppUsage
  public static void setDisplayLayerStack(IBinder paramIBinder, int paramInt)
  {
    try
    {
      sGlobalTransaction.setDisplayLayerStack(paramIBinder, paramInt);
      return;
    }
    finally {}
  }
  
  public static void setDisplayPowerMode(IBinder paramIBinder, int paramInt)
  {
    if (paramIBinder != null)
    {
      nativeSetDisplayPowerMode(paramIBinder, paramInt);
      return;
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  @UnsupportedAppUsage
  public static void setDisplayProjection(IBinder paramIBinder, int paramInt, Rect paramRect1, Rect paramRect2)
  {
    try
    {
      sGlobalTransaction.setDisplayProjection(paramIBinder, paramInt, paramRect1, paramRect2);
      return;
    }
    finally {}
  }
  
  public static void setDisplaySize(IBinder paramIBinder, int paramInt1, int paramInt2)
  {
    try
    {
      sGlobalTransaction.setDisplaySize(paramIBinder, paramInt1, paramInt2);
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public static void setDisplaySurface(IBinder paramIBinder, Surface paramSurface)
  {
    try
    {
      sGlobalTransaction.setDisplaySurface(paramIBinder, paramSurface);
      return;
    }
    finally {}
  }
  
  public static boolean setDisplayedContentSamplingEnabled(IBinder paramIBinder, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramIBinder != null)
    {
      if (paramInt1 >> 4 == 0) {
        return nativeSetDisplayedContentSamplingEnabled(paramIBinder, paramBoolean, paramInt1, paramInt2);
      }
      throw new IllegalArgumentException("invalid componentMask when enabling sampling");
    }
    throw new IllegalArgumentException("displayToken must not be null");
  }
  
  public static void setGlobalShadowSettings(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    validateColorArg(paramArrayOfFloat1);
    validateColorArg(paramArrayOfFloat2);
    nativeSetGlobalShadowSettings(paramArrayOfFloat1, paramArrayOfFloat2, paramFloat1, paramFloat2, paramFloat3);
  }
  
  private static void validateColorArg(float[] paramArrayOfFloat)
  {
    if (paramArrayOfFloat.length == 4)
    {
      int i = paramArrayOfFloat.length;
      int j = 0;
      while (j < i)
      {
        float f = paramArrayOfFloat[j];
        if ((f >= 0.0F) && (f <= 1.0F)) {
          j++;
        } else {
          throw new IllegalArgumentException("Color must be specified as a float array with four values to represent r, g, b, a in range [0..1]");
        }
      }
      return;
    }
    throw new IllegalArgumentException("Color must be specified as a float array with four values to represent r, g, b, a in range [0..1]");
  }
  
  public boolean clearContentFrameStats()
  {
    checkNotReleased();
    return nativeClearContentFrameStats(this.mNativeObject);
  }
  
  public void copyFrom(SurfaceControl paramSurfaceControl)
  {
    this.mName = paramSurfaceControl.mName;
    this.mWidth = paramSurfaceControl.mWidth;
    this.mHeight = paramSurfaceControl.mHeight;
    assignNativeObject(nativeCopyFromSurfaceControl(paramSurfaceControl.mNativeObject));
  }
  
  public void deferTransactionUntil(IBinder paramIBinder, long paramLong)
  {
    try
    {
      sGlobalTransaction.deferTransactionUntil(this, paramIBinder, paramLong);
      return;
    }
    finally {}
  }
  
  public void deferTransactionUntil(Surface paramSurface, long paramLong)
  {
    try
    {
      sGlobalTransaction.deferTransactionUntilSurface(this, paramSurface, paramLong);
      return;
    }
    finally {}
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void detachChildren()
  {
    try
    {
      sGlobalTransaction.detachChildren(this);
      return;
    }
    finally {}
  }
  
  public void disconnect()
  {
    long l = this.mNativeObject;
    if (l != 0L) {
      nativeDisconnect(l);
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      if (this.mCloseGuard != null) {
        this.mCloseGuard.warnIfOpen();
      }
      if (this.mNativeObject != 0L) {
        nativeRelease(this.mNativeObject);
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public boolean getContentFrameStats(WindowContentFrameStats paramWindowContentFrameStats)
  {
    checkNotReleased();
    return nativeGetContentFrameStats(this.mNativeObject, paramWindowContentFrameStats);
  }
  
  public IBinder getHandle()
  {
    return nativeGetHandle(this.mNativeObject);
  }
  
  public int getHeight()
  {
    synchronized (this.mSizeLock)
    {
      int i = this.mHeight;
      return i;
    }
  }
  
  public int getWidth()
  {
    synchronized (this.mSizeLock)
    {
      int i = this.mWidth;
      return i;
    }
  }
  
  @UnsupportedAppUsage
  public void hide()
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.hide(this);
      return;
    }
    finally {}
  }
  
  public boolean isValid()
  {
    boolean bool;
    if (this.mNativeObject != 0L) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    if (paramParcel != null)
    {
      this.mName = paramParcel.readString();
      this.mWidth = paramParcel.readInt();
      this.mHeight = paramParcel.readInt();
      long l = 0L;
      if (paramParcel.readInt() != 0) {
        l = nativeReadFromParcel(paramParcel);
      }
      assignNativeObject(l);
      return;
    }
    throw new IllegalArgumentException("source must not be null");
  }
  
  public void release()
  {
    long l = this.mNativeObject;
    if (l != 0L)
    {
      nativeRelease(l);
      this.mNativeObject = 0L;
    }
    this.mCloseGuard.close();
  }
  
  public void remove()
  {
    long l = this.mNativeObject;
    if (l != 0L)
    {
      nativeDestroy(l);
      this.mNativeObject = 0L;
    }
    this.mCloseGuard.close();
  }
  
  public void reparent(SurfaceControl paramSurfaceControl)
  {
    try
    {
      sGlobalTransaction.reparent(this, paramSurfaceControl);
      return;
    }
    finally {}
  }
  
  public void reparentChildren(IBinder paramIBinder)
  {
    try
    {
      sGlobalTransaction.reparentChildren(this, paramIBinder);
      return;
    }
    finally {}
  }
  
  public void setAlpha(float paramFloat)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setAlpha(this, paramFloat);
      return;
    }
    finally {}
  }
  
  public void setBlur(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setBlur(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setBlurCrop(Rect paramRect1, Rect paramRect2)
  {
    checkNotReleased();
    sGlobalTransaction.setBlurCrop(this, paramRect1, paramRect2);
  }
  
  public void setBlurCurrent(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setBlurCurrent(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setBlurMode(int paramInt)
  {
    checkNotReleased();
    sGlobalTransaction.setBlurMode(this, paramInt);
  }
  
  public void setBlurRatio(float paramFloat)
  {
    checkNotReleased();
    sGlobalTransaction.setBlurRatio(this, paramFloat);
  }
  
  public void setBufferSize(int paramInt1, int paramInt2)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setBufferSize(this, paramInt1, paramInt2);
      return;
    }
    finally {}
  }
  
  public void setCastMode(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setCastMode(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setColor(float[] paramArrayOfFloat)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setColor(this, paramArrayOfFloat);
      return;
    }
    finally {}
  }
  
  public void setColorSpaceAgnostic(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setColorSpaceAgnostic(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setColorTransform(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setColorTransform(this, paramArrayOfFloat1, paramArrayOfFloat2);
      return;
    }
    finally {}
  }
  
  public void setCornerRadius(float paramFloat)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setCornerRadius(this, paramFloat);
      return;
    }
    finally {}
  }
  
  public void setFlagsFromSV(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setFlagsFromSV(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setGeometryAppliesWithResize()
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setGeometryAppliesWithResize(this);
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public void setLayer(int paramInt)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setLayer(this, paramInt);
      return;
    }
    finally {}
  }
  
  public void setLayerStack(int paramInt)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setLayerStack(this, paramInt);
      return;
    }
    finally {}
  }
  
  public void setMatrix(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setMatrix(this, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      return;
    }
    finally {}
  }
  
  public void setMatrix(Matrix paramMatrix, float[] paramArrayOfFloat)
  {
    checkNotReleased();
    paramMatrix.getValues(paramArrayOfFloat);
    try
    {
      sGlobalTransaction.setMatrix(this, paramArrayOfFloat[0], paramArrayOfFloat[3], paramArrayOfFloat[1], paramArrayOfFloat[4]);
      sGlobalTransaction.setPosition(this, paramArrayOfFloat[2], paramArrayOfFloat[5]);
      return;
    }
    finally {}
  }
  
  public void setOpaque(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setOpaque(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setOverrideScalingMode(int paramInt)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setOverrideScalingMode(this, paramInt);
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public void setPosition(float paramFloat1, float paramFloat2)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setPosition(this, paramFloat1, paramFloat2);
      return;
    }
    finally {}
  }
  
  public void setRecordHide(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setRecordHide(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setRelativeLayer(SurfaceControl paramSurfaceControl, int paramInt)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setRelativeLayer(this, paramSurfaceControl, paramInt);
      return;
    }
    finally {}
  }
  
  public void setSecure(boolean paramBoolean)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setSecure(this, paramBoolean);
      return;
    }
    finally {}
  }
  
  public void setShadowRadiusParas(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setShadowRadiusParas(this, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
      return;
    }
    finally {}
  }
  
  public void setTransparentRegionHint(Region paramRegion)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setTransparentRegionHint(this, paramRegion);
      return;
    }
    finally {}
  }
  
  public void setWindowCrop(int paramInt1, int paramInt2)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setWindowCrop(this, paramInt1, paramInt2);
      return;
    }
    finally {}
  }
  
  public void setWindowCrop(Rect paramRect)
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.setWindowCrop(this, paramRect);
      return;
    }
    finally {}
  }
  
  @UnsupportedAppUsage
  public void show()
  {
    checkNotReleased();
    try
    {
      sGlobalTransaction.show(this);
      return;
    }
    finally {}
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Surface(name=");
    localStringBuilder.append(this.mName);
    localStringBuilder.append(")/@0x");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mName);
    paramParcel.writeInt(this.mWidth);
    paramParcel.writeInt(this.mHeight);
    if (this.mNativeObject == 0L) {
      paramParcel.writeInt(0);
    } else {
      paramParcel.writeInt(1);
    }
    nativeWriteToParcel(this.mNativeObject, paramParcel);
    if ((paramInt & 0x1) != 0) {
      release();
    }
  }
  
  public void writeToProto(ProtoOutputStream paramProtoOutputStream, long paramLong)
  {
    paramLong = paramProtoOutputStream.start(paramLong);
    paramProtoOutputStream.write(1120986464257L, System.identityHashCode(this));
    paramProtoOutputStream.write(1138166333442L, this.mName);
    paramProtoOutputStream.end(paramLong);
  }
  
  public static class Builder
  {
    private int mFlags = 4;
    private int mFormat = -1;
    private int mHeight;
    private SparseIntArray mMetadata;
    private String mName;
    private SurfaceControl mParent;
    private SurfaceSession mSession;
    private int mWidth;
    
    public Builder() {}
    
    public Builder(SurfaceSession paramSurfaceSession)
    {
      this.mSession = paramSurfaceSession;
    }
    
    private boolean isColorLayerSet()
    {
      boolean bool;
      if ((this.mFlags & 0x20000) == 131072) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private boolean isContainerLayerSet()
    {
      boolean bool;
      if ((this.mFlags & 0x80000) == 524288) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    private Builder setFlags(int paramInt1, int paramInt2)
    {
      this.mFlags = (this.mFlags & paramInt2 | paramInt1);
      return this;
    }
    
    private void unsetBufferSize()
    {
      this.mWidth = 0;
      this.mHeight = 0;
    }
    
    public SurfaceControl build()
    {
      int i = this.mWidth;
      if (i >= 0)
      {
        int j = this.mHeight;
        if (j >= 0)
        {
          if (((i <= 0) && (j <= 0)) || ((!isColorLayerSet()) && (!isContainerLayerSet()))) {
            return new SurfaceControl(this.mSession, this.mName, this.mWidth, this.mHeight, this.mFormat, this.mFlags, this.mParent, this.mMetadata, null);
          }
          throw new IllegalStateException("Only buffer layers can set a valid buffer size.");
        }
      }
      throw new IllegalStateException("width and height must be positive or unset");
    }
    
    public SurfaceControl build(Surface paramSurface)
    {
      int i = this.mWidth;
      if (i >= 0)
      {
        int j = this.mHeight;
        if (j >= 0)
        {
          if (((i <= 0) && (j <= 0)) || ((!isColorLayerSet()) && (!isContainerLayerSet()))) {
            return new SurfaceControl(this.mSession, this.mName, this.mWidth, this.mHeight, this.mFormat, this.mFlags, paramSurface, this.mMetadata, null);
          }
          throw new IllegalStateException("Only buffer layers can set a valid buffer size.");
        }
      }
      throw new IllegalStateException("width and height must be positive or unset");
    }
    
    public Builder setBufferSize(int paramInt1, int paramInt2)
    {
      if ((paramInt1 >= 0) && (paramInt2 >= 0))
      {
        this.mWidth = paramInt1;
        this.mHeight = paramInt2;
        return setFlags(0, 983040);
      }
      throw new IllegalArgumentException("width and height must be positive");
    }
    
    public Builder setColorLayer()
    {
      unsetBufferSize();
      return setFlags(131072, 983040);
    }
    
    public Builder setContainerLayer()
    {
      unsetBufferSize();
      return setFlags(524288, 983040);
    }
    
    public Builder setFlags(int paramInt)
    {
      this.mFlags = paramInt;
      return this;
    }
    
    public Builder setFormat(int paramInt)
    {
      this.mFormat = paramInt;
      return this;
    }
    
    public Builder setMetadata(int paramInt1, int paramInt2)
    {
      if (this.mMetadata == null) {
        this.mMetadata = new SparseIntArray();
      }
      this.mMetadata.put(paramInt1, paramInt2);
      return this;
    }
    
    public Builder setName(String paramString)
    {
      this.mName = paramString;
      return this;
    }
    
    public Builder setOpaque(boolean paramBoolean)
    {
      if (paramBoolean) {
        this.mFlags |= 0x400;
      } else {
        this.mFlags &= 0xFBFF;
      }
      return this;
    }
    
    public Builder setParent(SurfaceControl paramSurfaceControl)
    {
      this.mParent = paramSurfaceControl;
      return this;
    }
    
    public Builder setProtected(boolean paramBoolean)
    {
      if (paramBoolean) {
        this.mFlags |= 0x800;
      } else {
        this.mFlags &= 0xF7FF;
      }
      return this;
    }
    
    public Builder setSecure(boolean paramBoolean)
    {
      if (paramBoolean) {
        this.mFlags |= 0x80;
      } else {
        this.mFlags &= 0xFF7F;
      }
      return this;
    }
  }
  
  public static final class CieXyz
  {
    public float X;
    public float Y;
    public float Z;
  }
  
  public static final class DisplayPrimaries
  {
    public SurfaceControl.CieXyz blue;
    public SurfaceControl.CieXyz green;
    public SurfaceControl.CieXyz red;
    public SurfaceControl.CieXyz white;
  }
  
  public static final class PhysicalDisplayInfo
  {
    @UnsupportedAppUsage
    public long appVsyncOffsetNanos;
    @UnsupportedAppUsage
    public float density;
    @UnsupportedAppUsage
    public int height;
    @UnsupportedAppUsage
    public long presentationDeadlineNanos;
    @UnsupportedAppUsage
    public float refreshRate;
    @UnsupportedAppUsage
    public boolean secure;
    @UnsupportedAppUsage
    public int width;
    @UnsupportedAppUsage
    public float xDpi;
    @UnsupportedAppUsage
    public float yDpi;
    
    @UnsupportedAppUsage
    public PhysicalDisplayInfo() {}
    
    public PhysicalDisplayInfo(PhysicalDisplayInfo paramPhysicalDisplayInfo)
    {
      copyFrom(paramPhysicalDisplayInfo);
    }
    
    public void copyFrom(PhysicalDisplayInfo paramPhysicalDisplayInfo)
    {
      this.width = paramPhysicalDisplayInfo.width;
      this.height = paramPhysicalDisplayInfo.height;
      this.refreshRate = paramPhysicalDisplayInfo.refreshRate;
      this.density = paramPhysicalDisplayInfo.density;
      this.xDpi = paramPhysicalDisplayInfo.xDpi;
      this.yDpi = paramPhysicalDisplayInfo.yDpi;
      this.secure = paramPhysicalDisplayInfo.secure;
      this.appVsyncOffsetNanos = paramPhysicalDisplayInfo.appVsyncOffsetNanos;
      this.presentationDeadlineNanos = paramPhysicalDisplayInfo.presentationDeadlineNanos;
    }
    
    public boolean equals(PhysicalDisplayInfo paramPhysicalDisplayInfo)
    {
      boolean bool;
      if ((paramPhysicalDisplayInfo != null) && (this.width == paramPhysicalDisplayInfo.width) && (this.height == paramPhysicalDisplayInfo.height) && (this.refreshRate == paramPhysicalDisplayInfo.refreshRate) && (this.density == paramPhysicalDisplayInfo.density) && (this.xDpi == paramPhysicalDisplayInfo.xDpi) && (this.yDpi == paramPhysicalDisplayInfo.yDpi) && (this.secure == paramPhysicalDisplayInfo.secure) && (this.appVsyncOffsetNanos == paramPhysicalDisplayInfo.appVsyncOffsetNanos) && (this.presentationDeadlineNanos == paramPhysicalDisplayInfo.presentationDeadlineNanos)) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool;
      if (((paramObject instanceof PhysicalDisplayInfo)) && (equals((PhysicalDisplayInfo)paramObject))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public int hashCode()
    {
      return 0;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("PhysicalDisplayInfo{");
      localStringBuilder.append(this.width);
      localStringBuilder.append(" x ");
      localStringBuilder.append(this.height);
      localStringBuilder.append(", ");
      localStringBuilder.append(this.refreshRate);
      localStringBuilder.append(" fps, density ");
      localStringBuilder.append(this.density);
      localStringBuilder.append(", ");
      localStringBuilder.append(this.xDpi);
      localStringBuilder.append(" x ");
      localStringBuilder.append(this.yDpi);
      localStringBuilder.append(" dpi, secure ");
      localStringBuilder.append(this.secure);
      localStringBuilder.append(", appVsyncOffset ");
      localStringBuilder.append(this.appVsyncOffsetNanos);
      localStringBuilder.append(", bufferDeadline ");
      localStringBuilder.append(this.presentationDeadlineNanos);
      localStringBuilder.append("}");
      return localStringBuilder.toString();
    }
  }
  
  public static class ScreenshotGraphicBuffer
  {
    private final ColorSpace mColorSpace;
    private final boolean mContainsSecureLayers;
    private final GraphicBuffer mGraphicBuffer;
    
    public ScreenshotGraphicBuffer(GraphicBuffer paramGraphicBuffer, ColorSpace paramColorSpace, boolean paramBoolean)
    {
      this.mGraphicBuffer = paramGraphicBuffer;
      this.mColorSpace = paramColorSpace;
      this.mContainsSecureLayers = paramBoolean;
    }
    
    private static ScreenshotGraphicBuffer createFromNative(int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong, int paramInt5, boolean paramBoolean)
    {
      return new ScreenshotGraphicBuffer(GraphicBuffer.createFromExisting(paramInt1, paramInt2, paramInt3, paramInt4, paramLong), ColorSpace.get(ColorSpace.Named.values()[paramInt5]), paramBoolean);
    }
    
    public boolean containsSecureLayers()
    {
      return this.mContainsSecureLayers;
    }
    
    public ColorSpace getColorSpace()
    {
      return this.mColorSpace;
    }
    
    public GraphicBuffer getGraphicBuffer()
    {
      return this.mGraphicBuffer;
    }
  }
  
  public static class Transaction
    implements Closeable
  {
    public static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(Transaction.class.getClassLoader(), SurfaceControl.access$300(), 512L);
    Runnable mFreeNativeResources = sRegistry.registerNativeAllocation(this, this.mNativeObject);
    private long mNativeObject = SurfaceControl.access$400();
    private final ArrayMap<SurfaceControl, Point> mResizedSurfaces = new ArrayMap();
    
    private void applyResizedSurfaces()
    {
      int i = this.mResizedSurfaces.size() - 1;
      while (i >= 0)
      {
        Point localPoint = (Point)this.mResizedSurfaces.valueAt(i);
        SurfaceControl localSurfaceControl = (SurfaceControl)this.mResizedSurfaces.keyAt(i);
        synchronized (localSurfaceControl.mSizeLock)
        {
          SurfaceControl.access$702(localSurfaceControl, localPoint.x);
          SurfaceControl.access$802(localSurfaceControl, localPoint.y);
          i--;
        }
      }
      this.mResizedSurfaces.clear();
    }
    
    public void apply()
    {
      apply(false);
    }
    
    public void apply(boolean paramBoolean)
    {
      applyResizedSurfaces();
      SurfaceControl.nativeApplyTransaction(this.mNativeObject, paramBoolean);
    }
    
    public void close()
    {
      this.mFreeNativeResources.run();
      this.mNativeObject = 0L;
    }
    
    @UnsupportedAppUsage
    public Transaction deferTransactionUntil(SurfaceControl paramSurfaceControl, IBinder paramIBinder, long paramLong)
    {
      if (paramLong < 0L) {
        return this;
      }
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeDeferTransactionUntil(this.mNativeObject, paramSurfaceControl.mNativeObject, paramIBinder, paramLong);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction deferTransactionUntilSurface(SurfaceControl paramSurfaceControl, Surface paramSurface, long paramLong)
    {
      if (paramLong < 0L) {
        return this;
      }
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeDeferTransactionUntilSurface(this.mNativeObject, paramSurfaceControl.mNativeObject, paramSurface.mNativeObject, paramLong);
      return this;
    }
    
    public Transaction detachChildren(SurfaceControl paramSurfaceControl)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSeverChildren(this.mNativeObject, paramSurfaceControl.mNativeObject);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction hide(SurfaceControl paramSurfaceControl)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetFlags(this.mNativeObject, paramSurfaceControl.mNativeObject, 1, 1);
      return this;
    }
    
    public Transaction merge(Transaction paramTransaction)
    {
      if (this == paramTransaction) {
        return this;
      }
      this.mResizedSurfaces.putAll(paramTransaction.mResizedSurfaces);
      paramTransaction.mResizedSurfaces.clear();
      SurfaceControl.nativeMergeTransaction(this.mNativeObject, paramTransaction.mNativeObject);
      return this;
    }
    
    public Transaction remove(SurfaceControl paramSurfaceControl)
    {
      reparent(paramSurfaceControl, null);
      paramSurfaceControl.release();
      return this;
    }
    
    public Transaction reparent(SurfaceControl paramSurfaceControl1, SurfaceControl paramSurfaceControl2)
    {
      paramSurfaceControl1.checkNotReleased();
      long l = 0L;
      if (paramSurfaceControl2 != null)
      {
        paramSurfaceControl2.checkNotReleased();
        l = paramSurfaceControl2.mNativeObject;
      }
      SurfaceControl.nativeReparent(this.mNativeObject, paramSurfaceControl1.mNativeObject, l);
      return this;
    }
    
    public Transaction reparentChildren(SurfaceControl paramSurfaceControl, IBinder paramIBinder)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeReparentChildren(this.mNativeObject, paramSurfaceControl.mNativeObject, paramIBinder);
      return this;
    }
    
    public Transaction setAlpha(SurfaceControl paramSurfaceControl, float paramFloat)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetAlpha(this.mNativeObject, paramSurfaceControl.mNativeObject, paramFloat);
      return this;
    }
    
    public Transaction setAnimationTransaction()
    {
      SurfaceControl.nativeSetAnimationTransaction(this.mNativeObject);
      return this;
    }
    
    public Transaction setBlur(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      long l1 = this.mNativeObject;
      long l2 = paramSurfaceControl.mNativeObject;
      int i;
      if (paramBoolean) {
        i = 16;
      } else {
        i = 0;
      }
      SurfaceControl.nativeSetFlags(l1, l2, i, 16);
      return this;
    }
    
    public Transaction setBlurCrop(SurfaceControl paramSurfaceControl, Rect paramRect1, Rect paramRect2)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetBlurCrop(this.mNativeObject, paramSurfaceControl.mNativeObject, paramRect1, paramRect2);
      return this;
    }
    
    public Transaction setBlurCurrent(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      long l1 = this.mNativeObject;
      long l2 = paramSurfaceControl.mNativeObject;
      int i;
      if (paramBoolean) {
        i = 8;
      } else {
        i = 0;
      }
      SurfaceControl.nativeSetFlags(l1, l2, i, 8);
      return this;
    }
    
    public Transaction setBlurMode(SurfaceControl paramSurfaceControl, int paramInt)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetBlurMode(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt);
      return this;
    }
    
    public Transaction setBlurRatio(SurfaceControl paramSurfaceControl, float paramFloat)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetBlurRatio(this.mNativeObject, paramSurfaceControl.mNativeObject, paramFloat);
      return this;
    }
    
    public Transaction setBufferSize(SurfaceControl paramSurfaceControl, int paramInt1, int paramInt2)
    {
      paramSurfaceControl.checkNotReleased();
      this.mResizedSurfaces.put(paramSurfaceControl, new Point(paramInt1, paramInt2));
      SurfaceControl.nativeSetSize(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt1, paramInt2);
      return this;
    }
    
    public Transaction setCastMode(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      IBinder localIBinder = SurfaceControl.getInternalDisplayToken();
      SurfaceControl.nativeSetCastMode(this.mNativeObject, paramSurfaceControl.mNativeObject, localIBinder, paramBoolean);
      long l1 = this.mNativeObject;
      long l2 = paramSurfaceControl.mNativeObject;
      int i;
      if (paramBoolean) {
        i = 64;
      } else {
        i = 0;
      }
      SurfaceControl.nativeSetFlags(l1, l2, i, 64);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction setColor(SurfaceControl paramSurfaceControl, float[] paramArrayOfFloat)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetColor(this.mNativeObject, paramSurfaceControl.mNativeObject, paramArrayOfFloat);
      return this;
    }
    
    public Transaction setColorSpaceAgnostic(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetColorSpaceAgnostic(this.mNativeObject, paramSurfaceControl.mNativeObject, paramBoolean);
      return this;
    }
    
    public Transaction setColorTransform(SurfaceControl paramSurfaceControl, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetColorTransform(this.mNativeObject, paramSurfaceControl.mNativeObject, paramArrayOfFloat1, paramArrayOfFloat2);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction setCornerRadius(SurfaceControl paramSurfaceControl, float paramFloat)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetCornerRadius(this.mNativeObject, paramSurfaceControl.mNativeObject, paramFloat);
      return this;
    }
    
    public Transaction setDiffScreenProjection(IBinder paramIBinder, boolean paramBoolean)
    {
      if (paramIBinder != null)
      {
        SurfaceControl.nativeSetDiffScreenProjection(this.mNativeObject, paramIBinder, paramBoolean);
        return this;
      }
      throw new IllegalArgumentException("displayToken must not be null");
    }
    
    public Transaction setDisplayLayerStack(IBinder paramIBinder, int paramInt)
    {
      if (paramIBinder != null)
      {
        SurfaceControl.nativeSetDisplayLayerStack(this.mNativeObject, paramIBinder, paramInt);
        return this;
      }
      throw new IllegalArgumentException("displayToken must not be null");
    }
    
    public Transaction setDisplayProjection(IBinder paramIBinder, int paramInt, Rect paramRect1, Rect paramRect2)
    {
      if (paramIBinder != null)
      {
        if (paramRect1 != null)
        {
          if (paramRect2 != null)
          {
            SurfaceControl.nativeSetDisplayProjection(this.mNativeObject, paramIBinder, paramInt, paramRect1.left, paramRect1.top, paramRect1.right, paramRect1.bottom, paramRect2.left, paramRect2.top, paramRect2.right, paramRect2.bottom);
            return this;
          }
          throw new IllegalArgumentException("displayRect must not be null");
        }
        throw new IllegalArgumentException("layerStackRect must not be null");
      }
      throw new IllegalArgumentException("displayToken must not be null");
    }
    
    public Transaction setDisplaySize(IBinder paramIBinder, int paramInt1, int paramInt2)
    {
      if (paramIBinder != null)
      {
        if ((paramInt1 > 0) && (paramInt2 > 0))
        {
          SurfaceControl.nativeSetDisplaySize(this.mNativeObject, paramIBinder, paramInt1, paramInt2);
          return this;
        }
        throw new IllegalArgumentException("width and height must be positive");
      }
      throw new IllegalArgumentException("displayToken must not be null");
    }
    
    public Transaction setDisplaySurface(IBinder paramIBinder, Surface paramSurface)
    {
      if (paramIBinder != null)
      {
        if (paramSurface != null) {
          synchronized (paramSurface.mLock)
          {
            SurfaceControl.nativeSetDisplaySurface(this.mNativeObject, paramIBinder, paramSurface.mNativeObject);
          }
        }
        SurfaceControl.nativeSetDisplaySurface(this.mNativeObject, paramIBinder, 0L);
        return this;
      }
      throw new IllegalArgumentException("displayToken must not be null");
    }
    
    public Transaction setEarlyWakeup()
    {
      SurfaceControl.nativeSetEarlyWakeup(this.mNativeObject);
      return this;
    }
    
    public Transaction setFlagsFromSV(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      long l1 = this.mNativeObject;
      long l2 = paramSurfaceControl.mNativeObject;
      int i;
      if (paramBoolean) {
        i = 64;
      } else {
        i = 0;
      }
      SurfaceControl.nativeSetFlags(l1, l2, i, 64);
      return this;
    }
    
    public Transaction setGeometry(SurfaceControl paramSurfaceControl, Rect paramRect1, Rect paramRect2, int paramInt)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetGeometry(this.mNativeObject, paramSurfaceControl.mNativeObject, paramRect1, paramRect2, paramInt);
      return this;
    }
    
    public Transaction setGeometryAppliesWithResize(SurfaceControl paramSurfaceControl)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetGeometryAppliesWithResize(this.mNativeObject, paramSurfaceControl.mNativeObject);
      return this;
    }
    
    public Transaction setInputWindowInfo(SurfaceControl paramSurfaceControl, InputWindowHandle paramInputWindowHandle)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetInputWindowInfo(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInputWindowHandle);
      return this;
    }
    
    public Transaction setLastFrame(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      IBinder localIBinder = SurfaceControl.getInternalDisplayToken();
      SurfaceControl.nativeSetLastFrame(this.mNativeObject, paramSurfaceControl.mNativeObject, localIBinder, paramBoolean);
      return this;
    }
    
    public Transaction setLayer(SurfaceControl paramSurfaceControl, int paramInt)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetLayer(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt);
      return this;
    }
    
    @UnsupportedAppUsage(maxTargetSdk=26)
    public Transaction setLayerStack(SurfaceControl paramSurfaceControl, int paramInt)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetLayerStack(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction setMatrix(SurfaceControl paramSurfaceControl, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetMatrix(this.mNativeObject, paramSurfaceControl.mNativeObject, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction setMatrix(SurfaceControl paramSurfaceControl, Matrix paramMatrix, float[] paramArrayOfFloat)
    {
      paramMatrix.getValues(paramArrayOfFloat);
      setMatrix(paramSurfaceControl, paramArrayOfFloat[0], paramArrayOfFloat[3], paramArrayOfFloat[1], paramArrayOfFloat[4]);
      setPosition(paramSurfaceControl, paramArrayOfFloat[2], paramArrayOfFloat[5]);
      return this;
    }
    
    public Transaction setMetadata(SurfaceControl paramSurfaceControl, int paramInt1, int paramInt2)
    {
      Parcel localParcel = Parcel.obtain();
      localParcel.writeInt(paramInt2);
      try
      {
        setMetadata(paramSurfaceControl, paramInt1, localParcel);
        return this;
      }
      finally
      {
        localParcel.recycle();
      }
    }
    
    public Transaction setMetadata(SurfaceControl paramSurfaceControl, int paramInt, Parcel paramParcel)
    {
      SurfaceControl.nativeSetMetadata(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt, paramParcel);
      return this;
    }
    
    public Transaction setOpaque(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      if (paramBoolean) {
        SurfaceControl.nativeSetFlags(this.mNativeObject, paramSurfaceControl.mNativeObject, 2, 2);
      } else {
        SurfaceControl.nativeSetFlags(this.mNativeObject, paramSurfaceControl.mNativeObject, 0, 2);
      }
      return this;
    }
    
    public Transaction setOverrideScalingMode(SurfaceControl paramSurfaceControl, int paramInt)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetOverrideScalingMode(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction setPosition(SurfaceControl paramSurfaceControl, float paramFloat1, float paramFloat2)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetPosition(this.mNativeObject, paramSurfaceControl.mNativeObject, paramFloat1, paramFloat2);
      return this;
    }
    
    public Transaction setRecordHide(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      long l1 = this.mNativeObject;
      long l2 = paramSurfaceControl.mNativeObject;
      int i;
      if (paramBoolean) {
        i = 32;
      } else {
        i = 0;
      }
      SurfaceControl.nativeSetFlags(l1, l2, i, 32);
      return this;
    }
    
    public Transaction setRelativeLayer(SurfaceControl paramSurfaceControl1, SurfaceControl paramSurfaceControl2, int paramInt)
    {
      paramSurfaceControl1.checkNotReleased();
      SurfaceControl.nativeSetRelativeLayer(this.mNativeObject, paramSurfaceControl1.mNativeObject, paramSurfaceControl2.getHandle(), paramInt);
      return this;
    }
    
    public Transaction setScreenProjection(SurfaceControl paramSurfaceControl, int paramInt)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetScreenProjection(this.mNativeObject, paramSurfaceControl.mNativeObject, paramInt);
      return this;
    }
    
    public Transaction setSecure(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      if (paramBoolean) {
        SurfaceControl.nativeSetFlags(this.mNativeObject, paramSurfaceControl.mNativeObject, 128, 128);
      } else {
        SurfaceControl.nativeSetFlags(this.mNativeObject, paramSurfaceControl.mNativeObject, 0, 128);
      }
      return this;
    }
    
    public Transaction setShadowRadiusParas(SurfaceControl paramSurfaceControl, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetShadowRadiusParas(this.mNativeObject, paramSurfaceControl.mNativeObject, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5);
      return this;
    }
    
    public Transaction setTransparentRegionHint(SurfaceControl paramSurfaceControl, Region paramRegion)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetTransparentRegionHint(this.mNativeObject, paramSurfaceControl.mNativeObject, paramRegion);
      return this;
    }
    
    public Transaction setVisibility(SurfaceControl paramSurfaceControl, boolean paramBoolean)
    {
      paramSurfaceControl.checkNotReleased();
      if (paramBoolean) {
        return show(paramSurfaceControl);
      }
      return hide(paramSurfaceControl);
    }
    
    public Transaction setWindowCrop(SurfaceControl paramSurfaceControl, int paramInt1, int paramInt2)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetWindowCrop(this.mNativeObject, paramSurfaceControl.mNativeObject, 0, 0, paramInt1, paramInt2);
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction setWindowCrop(SurfaceControl paramSurfaceControl, Rect paramRect)
    {
      paramSurfaceControl.checkNotReleased();
      if (paramRect != null) {
        SurfaceControl.nativeSetWindowCrop(this.mNativeObject, paramSurfaceControl.mNativeObject, paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
      } else {
        SurfaceControl.nativeSetWindowCrop(this.mNativeObject, paramSurfaceControl.mNativeObject, 0, 0, 0, 0);
      }
      return this;
    }
    
    @UnsupportedAppUsage
    public Transaction show(SurfaceControl paramSurfaceControl)
    {
      paramSurfaceControl.checkNotReleased();
      SurfaceControl.nativeSetFlags(this.mNativeObject, paramSurfaceControl.mNativeObject, 0, 1);
      return this;
    }
    
    public Transaction syncInputWindows()
    {
      SurfaceControl.nativeSyncInputWindows(this.mNativeObject);
      return this;
    }
    
    public Transaction transferTouchFocus(IBinder paramIBinder1, IBinder paramIBinder2)
    {
      SurfaceControl.nativeTransferTouchFocus(this.mNativeObject, paramIBinder1, paramIBinder2);
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/SurfaceControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */