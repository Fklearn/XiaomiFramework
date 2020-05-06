package android.view;

import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public final class DisplayCutout
{
  private static final String BOTTOM_MARKER = "@bottom";
  public static final int BOUNDS_POSITION_BOTTOM = 3;
  public static final int BOUNDS_POSITION_LEFT = 0;
  public static final int BOUNDS_POSITION_LENGTH = 4;
  public static final int BOUNDS_POSITION_RIGHT = 2;
  public static final int BOUNDS_POSITION_TOP = 1;
  private static final Object CACHE_LOCK = new Object();
  private static final String DP_MARKER = "@dp";
  public static final String EMULATION_OVERLAY_CATEGORY = "com.android.internal.display_cutout_emulation";
  public static final DisplayCutout NO_CUTOUT;
  private static final Pair<Path, DisplayCutout> NULL_PAIR;
  private static final String RIGHT_MARKER = "@right";
  private static final String TAG = "DisplayCutout";
  private static final Rect ZERO_RECT = new Rect();
  @GuardedBy({"CACHE_LOCK"})
  private static Pair<Path, DisplayCutout> sCachedCutout = NULL_PAIR;
  @GuardedBy({"CACHE_LOCK"})
  private static float sCachedDensity;
  @GuardedBy({"CACHE_LOCK"})
  private static int sCachedDisplayHeight;
  @GuardedBy({"CACHE_LOCK"})
  private static int sCachedDisplayWidth;
  @GuardedBy({"CACHE_LOCK"})
  private static String sCachedSpec;
  private final Bounds mBounds;
  private final Rect mSafeInsets;
  
  static
  {
    Rect localRect = ZERO_RECT;
    NO_CUTOUT = new DisplayCutout(localRect, localRect, localRect, localRect, localRect, false);
    NULL_PAIR = new Pair(null, null);
  }
  
  public DisplayCutout(Insets paramInsets, Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4)
  {
    this(paramInsets.toRect(), paramRect1, paramRect2, paramRect3, paramRect4, true);
  }
  
  private DisplayCutout(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, Rect paramRect5, boolean paramBoolean)
  {
    this.mSafeInsets = getCopyOrRef(paramRect1, paramBoolean);
    this.mBounds = new Bounds(paramRect2, paramRect3, paramRect4, paramRect5, paramBoolean, null);
  }
  
  private DisplayCutout(Rect paramRect, Bounds paramBounds)
  {
    this.mSafeInsets = paramRect;
    this.mBounds = paramBounds;
  }
  
  @Deprecated
  public DisplayCutout(Rect paramRect, List<Rect> paramList)
  {
    this(paramRect, extractBoundsFromList(paramRect, paramList), true);
  }
  
  private DisplayCutout(Rect paramRect, Rect[] paramArrayOfRect, boolean paramBoolean)
  {
    this.mSafeInsets = getCopyOrRef(paramRect, paramBoolean);
    this.mBounds = new Bounds(paramArrayOfRect, paramBoolean, null);
  }
  
  private static int atLeastZero(int paramInt)
  {
    if (paramInt < 0) {
      paramInt = 0;
    }
    return paramInt;
  }
  
  public static Rect[] extractBoundsFromList(Rect paramRect, List<Rect> paramList)
  {
    Rect[] arrayOfRect = new Rect[4];
    for (int i = 0; i < arrayOfRect.length; i++) {
      arrayOfRect[i] = ZERO_RECT;
    }
    if ((paramRect != null) && (paramList != null))
    {
      paramList = paramList.iterator();
      while (paramList.hasNext())
      {
        Rect localRect = (Rect)paramList.next();
        if (localRect.left == 0) {
          arrayOfRect[0] = localRect;
        } else if (localRect.top == 0) {
          arrayOfRect[1] = localRect;
        } else if (paramRect.right > 0) {
          arrayOfRect[2] = localRect;
        } else if (paramRect.bottom > 0) {
          arrayOfRect[3] = localRect;
        }
      }
    }
    return arrayOfRect;
  }
  
  @VisibleForTesting
  public static DisplayCutout fromBoundingRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    Rect[] arrayOfRect = new Rect[4];
    for (int i = 0; i < 4; i++)
    {
      Rect localRect = new android/graphics/Rect;
      if (paramInt5 == i) {
        localRect.<init>(paramInt1, paramInt2, paramInt3, paramInt4);
      } else {
        localRect.<init>();
      }
      arrayOfRect[i] = localRect;
    }
    return new DisplayCutout(ZERO_RECT, arrayOfRect, false);
  }
  
  public static DisplayCutout fromBounds(Rect[] paramArrayOfRect)
  {
    return new DisplayCutout(ZERO_RECT, paramArrayOfRect, false);
  }
  
  public static DisplayCutout fromResourcesRectApproximation(Resources paramResources, int paramInt1, int paramInt2)
  {
    return fromSpec(paramResources.getString(17039773), paramInt1, paramInt2, DisplayMetrics.DENSITY_DEVICE_STABLE / 160.0F);
  }
  
  @VisibleForTesting(visibility=VisibleForTesting.Visibility.PRIVATE)
  public static DisplayCutout fromSpec(String paramString, int paramInt1, int paramInt2, float paramFloat)
  {
    return (DisplayCutout)pathAndDisplayCutoutFromSpec(paramString, paramInt1, paramInt2, paramFloat).second;
  }
  
  private static Rect getCopyOrRef(Rect paramRect, boolean paramBoolean)
  {
    if (paramRect == null) {
      return ZERO_RECT;
    }
    if (paramBoolean) {
      return new Rect(paramRect);
    }
    return paramRect;
  }
  
  /* Error */
  private static Pair<Path, DisplayCutout> pathAndDisplayCutoutFromSpec(String paramString, int paramInt1, int paramInt2, float paramFloat)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 209	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   4: ifeq +7 -> 11
    //   7: getstatic 85	android/view/DisplayCutout:NULL_PAIR	Landroid/util/Pair;
    //   10: areturn
    //   11: getstatic 88	android/view/DisplayCutout:CACHE_LOCK	Ljava/lang/Object;
    //   14: astore 4
    //   16: aload 4
    //   18: monitorenter
    //   19: getstatic 211	android/view/DisplayCutout:sCachedSpec	Ljava/lang/String;
    //   22: astore 5
    //   24: aload_0
    //   25: aload 5
    //   27: invokevirtual 217	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   30: ifeq +34 -> 64
    //   33: getstatic 219	android/view/DisplayCutout:sCachedDisplayWidth	I
    //   36: iload_1
    //   37: if_icmpne +27 -> 64
    //   40: getstatic 221	android/view/DisplayCutout:sCachedDisplayHeight	I
    //   43: iload_2
    //   44: if_icmpne +20 -> 64
    //   47: getstatic 223	android/view/DisplayCutout:sCachedDensity	F
    //   50: fload_3
    //   51: fcmpl
    //   52: ifne +12 -> 64
    //   55: getstatic 90	android/view/DisplayCutout:sCachedCutout	Landroid/util/Pair;
    //   58: astore_0
    //   59: aload 4
    //   61: monitorexit
    //   62: aload_0
    //   63: areturn
    //   64: aload 4
    //   66: monitorexit
    //   67: aload_0
    //   68: invokevirtual 227	java/lang/String:trim	()Ljava/lang/String;
    //   71: astore 4
    //   73: aload 4
    //   75: ldc 48
    //   77: invokevirtual 231	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   80: ifeq +32 -> 112
    //   83: iload_1
    //   84: i2f
    //   85: fstore 6
    //   87: aload 4
    //   89: iconst_0
    //   90: aload 4
    //   92: invokevirtual 235	java/lang/String:length	()I
    //   95: ldc 48
    //   97: invokevirtual 235	java/lang/String:length	()I
    //   100: isub
    //   101: invokevirtual 239	java/lang/String:substring	(II)Ljava/lang/String;
    //   104: invokevirtual 227	java/lang/String:trim	()Ljava/lang/String;
    //   107: astore 4
    //   109: goto +9 -> 118
    //   112: iload_1
    //   113: i2f
    //   114: fconst_2
    //   115: fdiv
    //   116: fstore 6
    //   118: aload 4
    //   120: ldc 37
    //   122: invokevirtual 231	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   125: istore 7
    //   127: aload 4
    //   129: astore_0
    //   130: iload 7
    //   132: ifeq +21 -> 153
    //   135: aload 4
    //   137: iconst_0
    //   138: aload 4
    //   140: invokevirtual 235	java/lang/String:length	()I
    //   143: ldc 37
    //   145: invokevirtual 235	java/lang/String:length	()I
    //   148: isub
    //   149: invokevirtual 239	java/lang/String:substring	(II)Ljava/lang/String;
    //   152: astore_0
    //   153: aload_0
    //   154: ldc 21
    //   156: invokevirtual 242	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   159: ifeq +32 -> 191
    //   162: aload_0
    //   163: ldc 21
    //   165: iconst_2
    //   166: invokevirtual 246	java/lang/String:split	(Ljava/lang/String;I)[Ljava/lang/String;
    //   169: astore 4
    //   171: aload 4
    //   173: iconst_0
    //   174: aaload
    //   175: invokevirtual 227	java/lang/String:trim	()Ljava/lang/String;
    //   178: astore_0
    //   179: aload 4
    //   181: iconst_1
    //   182: aaload
    //   183: invokevirtual 227	java/lang/String:trim	()Ljava/lang/String;
    //   186: astore 4
    //   188: goto +6 -> 194
    //   191: aconst_null
    //   192: astore 4
    //   194: invokestatic 252	android/graphics/Region:obtain	()Landroid/graphics/Region;
    //   197: astore 8
    //   199: aload_0
    //   200: invokestatic 258	android/util/PathParser:createPathFromPathData	(Ljava/lang/String;)Landroid/graphics/Path;
    //   203: astore 9
    //   205: new 260	android/graphics/Matrix
    //   208: dup
    //   209: invokespecial 261	android/graphics/Matrix:<init>	()V
    //   212: astore 10
    //   214: iload 7
    //   216: ifeq +11 -> 227
    //   219: aload 10
    //   221: fload_3
    //   222: fload_3
    //   223: invokevirtual 265	android/graphics/Matrix:postScale	(FF)Z
    //   226: pop
    //   227: aload 10
    //   229: fload 6
    //   231: fconst_0
    //   232: invokevirtual 268	android/graphics/Matrix:postTranslate	(FF)Z
    //   235: pop
    //   236: aload 9
    //   238: aload 10
    //   240: invokevirtual 274	android/graphics/Path:transform	(Landroid/graphics/Matrix;)V
    //   243: new 68	android/graphics/Rect
    //   246: dup
    //   247: invokespecial 71	android/graphics/Rect:<init>	()V
    //   250: astore 5
    //   252: aload 9
    //   254: aload 8
    //   256: aload 5
    //   258: invokestatic 278	android/view/DisplayCutout:toRectAndAddToRegion	(Landroid/graphics/Path;Landroid/graphics/Region;Landroid/graphics/Rect;)V
    //   261: aload 5
    //   263: getfield 161	android/graphics/Rect:bottom	I
    //   266: istore 11
    //   268: aload 4
    //   270: ifnull +78 -> 348
    //   273: aload 4
    //   275: invokestatic 258	android/util/PathParser:createPathFromPathData	(Ljava/lang/String;)Landroid/graphics/Path;
    //   278: astore 12
    //   280: aload 10
    //   282: fconst_0
    //   283: iload_2
    //   284: i2f
    //   285: invokevirtual 268	android/graphics/Matrix:postTranslate	(FF)Z
    //   288: pop
    //   289: aload 12
    //   291: aload 10
    //   293: invokevirtual 274	android/graphics/Path:transform	(Landroid/graphics/Matrix;)V
    //   296: aload 9
    //   298: aload 12
    //   300: invokevirtual 282	android/graphics/Path:addPath	(Landroid/graphics/Path;)V
    //   303: new 68	android/graphics/Rect
    //   306: dup
    //   307: invokespecial 71	android/graphics/Rect:<init>	()V
    //   310: astore 4
    //   312: aload 12
    //   314: aload 8
    //   316: aload 4
    //   318: invokestatic 278	android/view/DisplayCutout:toRectAndAddToRegion	(Landroid/graphics/Path;Landroid/graphics/Region;Landroid/graphics/Rect;)V
    //   321: iload_2
    //   322: aload 4
    //   324: getfield 155	android/graphics/Rect:top	I
    //   327: isub
    //   328: istore 13
    //   330: goto +24 -> 354
    //   333: astore_0
    //   334: ldc 51
    //   336: ldc_w 284
    //   339: aload_0
    //   340: invokestatic 290	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   343: pop
    //   344: getstatic 85	android/view/DisplayCutout:NULL_PAIR	Landroid/util/Pair;
    //   347: areturn
    //   348: iconst_0
    //   349: istore 13
    //   351: aconst_null
    //   352: astore 4
    //   354: new 80	android/util/Pair
    //   357: dup
    //   358: aload 9
    //   360: new 2	android/view/DisplayCutout
    //   363: dup
    //   364: new 68	android/graphics/Rect
    //   367: dup
    //   368: iconst_0
    //   369: iload 11
    //   371: iconst_0
    //   372: iload 13
    //   374: invokespecial 168	android/graphics/Rect:<init>	(IIII)V
    //   377: aconst_null
    //   378: aload 5
    //   380: aconst_null
    //   381: aload 4
    //   383: iconst_0
    //   384: invokespecial 76	android/view/DisplayCutout:<init>	(Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Rect;Landroid/graphics/Rect;Z)V
    //   387: invokespecial 83	android/util/Pair:<init>	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   390: astore 5
    //   392: getstatic 88	android/view/DisplayCutout:CACHE_LOCK	Ljava/lang/Object;
    //   395: astore 4
    //   397: aload 4
    //   399: monitorenter
    //   400: aload_0
    //   401: putstatic 211	android/view/DisplayCutout:sCachedSpec	Ljava/lang/String;
    //   404: iload_1
    //   405: putstatic 219	android/view/DisplayCutout:sCachedDisplayWidth	I
    //   408: iload_2
    //   409: putstatic 221	android/view/DisplayCutout:sCachedDisplayHeight	I
    //   412: fload_3
    //   413: putstatic 223	android/view/DisplayCutout:sCachedDensity	F
    //   416: aload 5
    //   418: putstatic 90	android/view/DisplayCutout:sCachedCutout	Landroid/util/Pair;
    //   421: aload 4
    //   423: monitorexit
    //   424: aload 5
    //   426: areturn
    //   427: astore_0
    //   428: aload 4
    //   430: monitorexit
    //   431: aload_0
    //   432: athrow
    //   433: astore_0
    //   434: ldc 51
    //   436: ldc_w 292
    //   439: aload_0
    //   440: invokestatic 290	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   443: pop
    //   444: getstatic 85	android/view/DisplayCutout:NULL_PAIR	Landroid/util/Pair;
    //   447: areturn
    //   448: astore_0
    //   449: goto +4 -> 453
    //   452: astore_0
    //   453: aload 4
    //   455: monitorexit
    //   456: aload_0
    //   457: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	458	0	paramString	String
    //   0	458	1	paramInt1	int
    //   0	458	2	paramInt2	int
    //   0	458	3	paramFloat	float
    //   22	403	5	localObject2	Object
    //   85	145	6	f	float
    //   125	90	7	bool	boolean
    //   197	118	8	localRegion	Region
    //   203	156	9	localPath1	Path
    //   212	80	10	localMatrix	android.graphics.Matrix
    //   266	104	11	i	int
    //   278	35	12	localPath2	Path
    //   328	45	13	j	int
    // Exception table:
    //   from	to	target	type
    //   273	280	333	finally
    //   400	424	427	finally
    //   428	431	427	finally
    //   199	205	433	finally
    //   24	62	448	finally
    //   64	67	448	finally
    //   453	456	448	finally
    //   19	24	452	finally
  }
  
  public static Path pathFromResources(Resources paramResources, int paramInt1, int paramInt2)
  {
    return (Path)pathAndDisplayCutoutFromSpec(paramResources.getString(17039772), paramInt1, paramInt2, DisplayMetrics.DENSITY_DEVICE_STABLE / 160.0F).first;
  }
  
  private static void toRectAndAddToRegion(Path paramPath, Region paramRegion, Rect paramRect)
  {
    RectF localRectF = new RectF();
    paramPath.computeBounds(localRectF, false);
    localRectF.round(paramRect);
    paramRegion.op(paramRect, Region.Op.UNION);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof DisplayCutout))
    {
      paramObject = (DisplayCutout)paramObject;
      if ((!this.mSafeInsets.equals(((DisplayCutout)paramObject).mSafeInsets)) || (!this.mBounds.equals(((DisplayCutout)paramObject).mBounds))) {
        bool = false;
      }
      return bool;
    }
    return false;
  }
  
  public Rect getBoundingRectBottom()
  {
    return this.mBounds.getRect(3);
  }
  
  public Rect getBoundingRectLeft()
  {
    return this.mBounds.getRect(0);
  }
  
  public Rect getBoundingRectRight()
  {
    return this.mBounds.getRect(2);
  }
  
  public Rect getBoundingRectTop()
  {
    return this.mBounds.getRect(1);
  }
  
  public List<Rect> getBoundingRects()
  {
    ArrayList localArrayList = new ArrayList();
    for (Rect localRect : getBoundingRectsAll()) {
      if (!localRect.isEmpty()) {
        localArrayList.add(new Rect(localRect));
      }
    }
    return localArrayList;
  }
  
  public Rect[] getBoundingRectsAll()
  {
    return this.mBounds.getRects();
  }
  
  public int getSafeInsetBottom()
  {
    return this.mSafeInsets.bottom;
  }
  
  public int getSafeInsetLeft()
  {
    return this.mSafeInsets.left;
  }
  
  public int getSafeInsetRight()
  {
    return this.mSafeInsets.right;
  }
  
  public int getSafeInsetTop()
  {
    return this.mSafeInsets.top;
  }
  
  public Rect getSafeInsets()
  {
    return new Rect(this.mSafeInsets);
  }
  
  public int hashCode()
  {
    return this.mSafeInsets.hashCode() * 48271 + this.mBounds.hashCode();
  }
  
  public DisplayCutout inset(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (((paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == 0) && (paramInt4 == 0)) || (isBoundsEmpty())) {
      return this;
    }
    Rect localRect = new Rect(this.mSafeInsets);
    if ((paramInt2 > 0) || (localRect.top > 0)) {
      localRect.top = atLeastZero(localRect.top - paramInt2);
    }
    if ((paramInt4 > 0) || (localRect.bottom > 0)) {
      localRect.bottom = atLeastZero(localRect.bottom - paramInt4);
    }
    if ((paramInt1 > 0) || (localRect.left > 0)) {
      localRect.left = atLeastZero(localRect.left - paramInt1);
    }
    if ((paramInt3 > 0) || (localRect.right > 0)) {
      localRect.right = atLeastZero(localRect.right - paramInt3);
    }
    if ((paramInt1 == 0) && (paramInt2 == 0) && (this.mSafeInsets.equals(localRect))) {
      return this;
    }
    Rect[] arrayOfRect = this.mBounds.getRects();
    for (paramInt3 = 0; paramInt3 < arrayOfRect.length; paramInt3++) {
      if (!arrayOfRect[paramInt3].equals(ZERO_RECT)) {
        arrayOfRect[paramInt3].offset(-paramInt1, -paramInt2);
      }
    }
    return new DisplayCutout(localRect, arrayOfRect, false);
  }
  
  public boolean isBoundsEmpty()
  {
    return this.mBounds.isEmpty();
  }
  
  public boolean isEmpty()
  {
    return this.mSafeInsets.equals(ZERO_RECT);
  }
  
  public DisplayCutout replaceSafeInsets(Rect paramRect)
  {
    return new DisplayCutout(new Rect(paramRect), this.mBounds);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DisplayCutout{insets=");
    localStringBuilder.append(this.mSafeInsets);
    localStringBuilder.append(" boundingRect={");
    localStringBuilder.append(this.mBounds);
    localStringBuilder.append("}}");
    return localStringBuilder.toString();
  }
  
  public void writeToProto(ProtoOutputStream paramProtoOutputStream, long paramLong)
  {
    paramLong = paramProtoOutputStream.start(paramLong);
    this.mSafeInsets.writeToProto(paramProtoOutputStream, 1146756268033L);
    this.mBounds.getRect(0).writeToProto(paramProtoOutputStream, 1146756268035L);
    this.mBounds.getRect(1).writeToProto(paramProtoOutputStream, 1146756268036L);
    this.mBounds.getRect(2).writeToProto(paramProtoOutputStream, 1146756268037L);
    this.mBounds.getRect(3).writeToProto(paramProtoOutputStream, 1146756268038L);
    paramProtoOutputStream.end(paramLong);
  }
  
  private static class Bounds
  {
    private final Rect[] mRects;
    
    private Bounds(Rect paramRect1, Rect paramRect2, Rect paramRect3, Rect paramRect4, boolean paramBoolean)
    {
      this.mRects = new Rect[4];
      this.mRects[0] = DisplayCutout.getCopyOrRef(paramRect1, paramBoolean);
      this.mRects[1] = DisplayCutout.getCopyOrRef(paramRect2, paramBoolean);
      this.mRects[2] = DisplayCutout.getCopyOrRef(paramRect3, paramBoolean);
      this.mRects[3] = DisplayCutout.getCopyOrRef(paramRect4, paramBoolean);
    }
    
    private Bounds(Rect[] paramArrayOfRect, boolean paramBoolean)
    {
      if (paramArrayOfRect.length == 4)
      {
        int i;
        if (paramBoolean)
        {
          this.mRects = new Rect[4];
          for (i = 0; i < 4; i++) {
            this.mRects[i] = new Rect(paramArrayOfRect[i]);
          }
        }
        else
        {
          int j = paramArrayOfRect.length;
          i = 0;
          while (i < j) {
            if (paramArrayOfRect[i] != null)
            {
              i++;
            }
            else
            {
              localStringBuilder = new StringBuilder();
              localStringBuilder.append("rects must have non-null elements: rects=");
              localStringBuilder.append(Arrays.toString(paramArrayOfRect));
              throw new IllegalArgumentException(localStringBuilder.toString());
            }
          }
          this.mRects = paramArrayOfRect;
        }
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("rects must have exactly 4 elements: rects=");
      localStringBuilder.append(Arrays.toString(paramArrayOfRect));
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    
    private Rect getRect(int paramInt)
    {
      return new Rect(this.mRects[paramInt]);
    }
    
    private Rect[] getRects()
    {
      Rect[] arrayOfRect = new Rect[4];
      for (int i = 0; i < 4; i++) {
        arrayOfRect[i] = new Rect(this.mRects[i]);
      }
      return arrayOfRect;
    }
    
    private boolean isEmpty()
    {
      Rect[] arrayOfRect = this.mRects;
      int i = arrayOfRect.length;
      for (int j = 0; j < i; j++) {
        if (!arrayOfRect[j].isEmpty()) {
          return false;
        }
      }
      return true;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof Bounds))
      {
        paramObject = (Bounds)paramObject;
        return Arrays.deepEquals(this.mRects, ((Bounds)paramObject).mRects);
      }
      return false;
    }
    
    public int hashCode()
    {
      int i = 0;
      Rect[] arrayOfRect = this.mRects;
      int j = arrayOfRect.length;
      for (int k = 0; k < j; k++) {
        i = 48271 * i + arrayOfRect[k].hashCode();
      }
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Bounds=");
      localStringBuilder.append(Arrays.toString(this.mRects));
      return localStringBuilder.toString();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface BoundsPosition {}
  
  public static final class ParcelableWrapper
    implements Parcelable
  {
    public static final Parcelable.Creator<ParcelableWrapper> CREATOR = new Parcelable.Creator()
    {
      public DisplayCutout.ParcelableWrapper createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DisplayCutout.ParcelableWrapper(DisplayCutout.ParcelableWrapper.readCutoutFromParcel(paramAnonymousParcel));
      }
      
      public DisplayCutout.ParcelableWrapper[] newArray(int paramAnonymousInt)
      {
        return new DisplayCutout.ParcelableWrapper[paramAnonymousInt];
      }
    };
    private DisplayCutout mInner;
    
    public ParcelableWrapper()
    {
      this(DisplayCutout.NO_CUTOUT);
    }
    
    public ParcelableWrapper(DisplayCutout paramDisplayCutout)
    {
      this.mInner = paramDisplayCutout;
    }
    
    public static DisplayCutout readCutoutFromParcel(Parcel paramParcel)
    {
      int i = paramParcel.readInt();
      if (i == -1) {
        return null;
      }
      if (i == 0) {
        return DisplayCutout.NO_CUTOUT;
      }
      Rect localRect = (Rect)paramParcel.readTypedObject(Rect.CREATOR);
      Rect[] arrayOfRect = new Rect[4];
      paramParcel.readTypedArray(arrayOfRect, Rect.CREATOR);
      return new DisplayCutout(localRect, arrayOfRect, false, null);
    }
    
    public static void writeCutoutToParcel(DisplayCutout paramDisplayCutout, Parcel paramParcel, int paramInt)
    {
      if (paramDisplayCutout == null)
      {
        paramParcel.writeInt(-1);
      }
      else if (paramDisplayCutout == DisplayCutout.NO_CUTOUT)
      {
        paramParcel.writeInt(0);
      }
      else
      {
        paramParcel.writeInt(1);
        paramParcel.writeTypedObject(paramDisplayCutout.mSafeInsets, paramInt);
        paramParcel.writeTypedArray(paramDisplayCutout.mBounds.getRects(), paramInt);
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool;
      if (((paramObject instanceof ParcelableWrapper)) && (this.mInner.equals(((ParcelableWrapper)paramObject).mInner))) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public DisplayCutout get()
    {
      return this.mInner;
    }
    
    public int hashCode()
    {
      return this.mInner.hashCode();
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.mInner = readCutoutFromParcel(paramParcel);
    }
    
    public void set(ParcelableWrapper paramParcelableWrapper)
    {
      this.mInner = paramParcelableWrapper.get();
    }
    
    public void set(DisplayCutout paramDisplayCutout)
    {
      this.mInner = paramDisplayCutout;
    }
    
    public String toString()
    {
      return String.valueOf(this.mInner);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      writeCutoutToParcel(this.mInner, paramParcel, paramInt);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/DisplayCutout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */