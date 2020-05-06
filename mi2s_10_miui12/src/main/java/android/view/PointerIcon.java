package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.R.styleable;

public final class PointerIcon
  implements Parcelable
{
  public static final Parcelable.Creator<PointerIcon> CREATOR = new Parcelable.Creator()
  {
    public PointerIcon createFromParcel(Parcel paramAnonymousParcel)
    {
      int i = paramAnonymousParcel.readInt();
      if (i == 0) {
        return PointerIcon.getNullIcon();
      }
      int j = paramAnonymousParcel.readInt();
      if (j != 0)
      {
        paramAnonymousParcel = new PointerIcon(i, null);
        PointerIcon.access$102(paramAnonymousParcel, j);
        return paramAnonymousParcel;
      }
      return PointerIcon.create((Bitmap)Bitmap.CREATOR.createFromParcel(paramAnonymousParcel), paramAnonymousParcel.readFloat(), paramAnonymousParcel.readFloat());
    }
    
    public PointerIcon[] newArray(int paramAnonymousInt)
    {
      return new PointerIcon[paramAnonymousInt];
    }
  };
  private static final String TAG = "PointerIcon";
  public static final int TYPE_ALIAS = 1010;
  public static final int TYPE_ALL_SCROLL = 1013;
  public static final int TYPE_ARROW = 1000;
  public static final int TYPE_CELL = 1006;
  public static final int TYPE_CONTEXT_MENU = 1001;
  public static final int TYPE_COPY = 1011;
  public static final int TYPE_CROSSHAIR = 1007;
  public static final int TYPE_CUSTOM = -1;
  public static final int TYPE_DEFAULT = 1000;
  public static final int TYPE_GRAB = 1020;
  public static final int TYPE_GRABBING = 1021;
  public static final int TYPE_HAND = 1002;
  public static final int TYPE_HELP = 1003;
  public static final int TYPE_HORIZONTAL_DOUBLE_ARROW = 1014;
  public static final int TYPE_NOT_SPECIFIED = 1;
  public static final int TYPE_NO_DROP = 1012;
  public static final int TYPE_NULL = 0;
  private static final int TYPE_OEM_FIRST = 10000;
  public static final int TYPE_SPOT_ANCHOR = 2002;
  public static final int TYPE_SPOT_HOVER = 2000;
  public static final int TYPE_SPOT_TOUCH = 2001;
  public static final int TYPE_TEXT = 1008;
  public static final int TYPE_TOP_LEFT_DIAGONAL_DOUBLE_ARROW = 1017;
  public static final int TYPE_TOP_RIGHT_DIAGONAL_DOUBLE_ARROW = 1016;
  public static final int TYPE_VERTICAL_DOUBLE_ARROW = 1015;
  public static final int TYPE_VERTICAL_TEXT = 1009;
  public static final int TYPE_WAIT = 1004;
  public static final int TYPE_ZOOM_IN = 1018;
  public static final int TYPE_ZOOM_OUT = 1019;
  private static final PointerIcon gNullIcon = new PointerIcon(0);
  private static final SparseArray<SparseArray<PointerIcon>> gSystemIconsByDisplay = new SparseArray();
  private static DisplayManager.DisplayListener sDisplayListener;
  private static boolean sUseLargeIcons = false;
  @UnsupportedAppUsage
  private Bitmap mBitmap;
  @UnsupportedAppUsage
  private Bitmap[] mBitmapFrames;
  @UnsupportedAppUsage
  private int mDurationPerFrame;
  @UnsupportedAppUsage
  private float mHotSpotX;
  @UnsupportedAppUsage
  private float mHotSpotY;
  private int mSystemIconResourceId;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  private final int mType;
  
  private PointerIcon(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public static PointerIcon create(Bitmap paramBitmap, float paramFloat1, float paramFloat2)
  {
    if (paramBitmap != null)
    {
      validateHotSpot(paramBitmap, paramFloat1, paramFloat2);
      PointerIcon localPointerIcon = new PointerIcon(-1);
      localPointerIcon.mBitmap = paramBitmap;
      localPointerIcon.mHotSpotX = paramFloat1;
      localPointerIcon.mHotSpotY = paramFloat2;
      return localPointerIcon;
    }
    throw new IllegalArgumentException("bitmap must not be null");
  }
  
  private Bitmap getBitmapFromDrawable(BitmapDrawable paramBitmapDrawable)
  {
    Bitmap localBitmap = paramBitmapDrawable.getBitmap();
    int i = paramBitmapDrawable.getIntrinsicWidth();
    int j = paramBitmapDrawable.getIntrinsicHeight();
    if ((i == localBitmap.getWidth()) && (j == localBitmap.getHeight())) {
      return localBitmap;
    }
    Rect localRect = new Rect(0, 0, localBitmap.getWidth(), localBitmap.getHeight());
    RectF localRectF = new RectF(0.0F, 0.0F, i, j);
    paramBitmapDrawable = Bitmap.createBitmap(i, j, localBitmap.getConfig());
    Canvas localCanvas = new Canvas(paramBitmapDrawable);
    Paint localPaint = new Paint();
    localPaint.setFilterBitmap(true);
    localCanvas.drawBitmap(localBitmap, localRect, localRectF, localPaint);
    return paramBitmapDrawable;
  }
  
  public static PointerIcon getDefaultIcon(Context paramContext)
  {
    return getSystemIcon(paramContext, 1000);
  }
  
  public static PointerIcon getNullIcon()
  {
    return gNullIcon;
  }
  
  public static PointerIcon getSystemIcon(Context paramContext, int paramInt)
  {
    if (paramContext != null)
    {
      if (paramInt == 0) {
        return gNullIcon;
      }
      if (sDisplayListener == null) {
        registerDisplayListener(paramContext);
      }
      int i = paramContext.getDisplayId();
      Object localObject1 = (SparseArray)gSystemIconsByDisplay.get(i);
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = new SparseArray();
        gSystemIconsByDisplay.put(i, localObject2);
      }
      localObject1 = (PointerIcon)((SparseArray)localObject2).get(paramInt);
      if (localObject1 != null) {
        return (PointerIcon)localObject1;
      }
      int j = getSystemIconTypeIndex(paramInt);
      i = j;
      if (j == 0) {
        i = getSystemIconTypeIndex(1000);
      }
      if (sUseLargeIcons) {
        j = 16974639;
      } else {
        j = 16974647;
      }
      localObject1 = paramContext.obtainStyledAttributes(null, R.styleable.Pointer, 0, j);
      i = ((TypedArray)localObject1).getResourceId(i, -1);
      ((TypedArray)localObject1).recycle();
      if (i == -1)
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Missing theme resources for pointer icon type ");
        ((StringBuilder)localObject2).append(paramInt);
        Log.w("PointerIcon", ((StringBuilder)localObject2).toString());
        if (paramInt == 1000) {
          paramContext = gNullIcon;
        } else {
          paramContext = getSystemIcon(paramContext, 1000);
        }
        return paramContext;
      }
      localObject1 = new PointerIcon(paramInt);
      if ((0xFF000000 & i) == 16777216) {
        ((PointerIcon)localObject1).mSystemIconResourceId = i;
      } else {
        ((PointerIcon)localObject1).loadResource(paramContext, paramContext.getResources(), i);
      }
      ((SparseArray)localObject2).append(paramInt, localObject1);
      return (PointerIcon)localObject1;
    }
    throw new IllegalArgumentException("context must not be null");
  }
  
  private static int getSystemIconTypeIndex(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      switch (paramInt)
      {
      default: 
        switch (paramInt)
        {
        default: 
          return 0;
        case 2002: 
          return 13;
        case 2001: 
          return 15;
        }
        return 14;
      case 1021: 
        return 8;
      case 1020: 
        return 7;
      case 1019: 
        return 23;
      case 1018: 
        return 22;
      case 1017: 
        return 17;
      case 1016: 
        return 18;
      case 1015: 
        return 19;
      case 1014: 
        return 11;
      case 1013: 
        return 1;
      case 1012: 
        return 12;
      case 1011: 
        return 5;
      case 1010: 
        return 0;
      case 1009: 
        return 20;
      case 1008: 
        return 16;
      case 1007: 
        return 6;
      }
      return 3;
    case 1004: 
      return 21;
    case 1003: 
      return 10;
    case 1002: 
      return 9;
    case 1001: 
      return 4;
    }
    return 2;
  }
  
  public static PointerIcon load(Resources paramResources, int paramInt)
  {
    if (paramResources != null)
    {
      PointerIcon localPointerIcon = new PointerIcon(-1);
      localPointerIcon.loadResource(null, paramResources, paramInt);
      return localPointerIcon;
    }
    throw new IllegalArgumentException("resources must not be null");
  }
  
  /* Error */
  private void loadResource(Context paramContext, Resources paramResources, int paramInt)
  {
    // Byte code:
    //   0: aload_2
    //   1: iload_3
    //   2: invokevirtual 306	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   5: astore 4
    //   7: aload 4
    //   9: ldc_w 308
    //   12: invokestatic 314	com/android/internal/util/XmlUtils:beginDocument	(Lorg/xmlpull/v1/XmlPullParser;Ljava/lang/String;)V
    //   15: aload_2
    //   16: aload 4
    //   18: getstatic 316	com/android/internal/R$styleable:PointerIcon	[I
    //   21: invokevirtual 320	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   24: astore 5
    //   26: aload 5
    //   28: iconst_0
    //   29: iconst_0
    //   30: invokevirtual 255	android/content/res/TypedArray:getResourceId	(II)I
    //   33: istore_3
    //   34: aload 5
    //   36: iconst_1
    //   37: fconst_0
    //   38: invokevirtual 324	android/content/res/TypedArray:getDimension	(IF)F
    //   41: fstore 6
    //   43: aload 5
    //   45: iconst_2
    //   46: fconst_0
    //   47: invokevirtual 324	android/content/res/TypedArray:getDimension	(IF)F
    //   50: fstore 7
    //   52: aload 5
    //   54: invokevirtual 258	android/content/res/TypedArray:recycle	()V
    //   57: aload 4
    //   59: invokeinterface 329 1 0
    //   64: iload_3
    //   65: ifeq +274 -> 339
    //   68: aload_1
    //   69: ifnonnull +12 -> 81
    //   72: aload_2
    //   73: iload_3
    //   74: invokevirtual 333	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   77: astore_1
    //   78: goto +9 -> 87
    //   81: aload_1
    //   82: iload_3
    //   83: invokevirtual 334	android/content/Context:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   86: astore_1
    //   87: aload_1
    //   88: astore_2
    //   89: aload_1
    //   90: instanceof 336
    //   93: ifeq +193 -> 286
    //   96: aload_1
    //   97: checkcast 336	android/graphics/drawable/AnimationDrawable
    //   100: astore_1
    //   101: aload_1
    //   102: invokevirtual 339	android/graphics/drawable/AnimationDrawable:getNumberOfFrames	()I
    //   105: istore 8
    //   107: aload_1
    //   108: iconst_0
    //   109: invokevirtual 342	android/graphics/drawable/AnimationDrawable:getFrame	(I)Landroid/graphics/drawable/Drawable;
    //   112: astore_2
    //   113: iload 8
    //   115: iconst_1
    //   116: if_icmpne +15 -> 131
    //   119: ldc 17
    //   121: ldc_w 344
    //   124: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   127: pop
    //   128: goto +158 -> 286
    //   131: aload_0
    //   132: aload_1
    //   133: iconst_0
    //   134: invokevirtual 347	android/graphics/drawable/AnimationDrawable:getDuration	(I)I
    //   137: putfield 349	android/view/PointerIcon:mDurationPerFrame	I
    //   140: aload_0
    //   141: iload 8
    //   143: iconst_1
    //   144: isub
    //   145: anewarray 166	android/graphics/Bitmap
    //   148: putfield 351	android/view/PointerIcon:mBitmapFrames	[Landroid/graphics/Bitmap;
    //   151: aload_2
    //   152: invokevirtual 354	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   155: istore 9
    //   157: aload_2
    //   158: invokevirtual 355	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
    //   161: istore 10
    //   163: iconst_1
    //   164: istore_3
    //   165: iload_3
    //   166: iload 8
    //   168: if_icmpge +118 -> 286
    //   171: aload_1
    //   172: iload_3
    //   173: invokevirtual 342	android/graphics/drawable/AnimationDrawable:getFrame	(I)Landroid/graphics/drawable/Drawable;
    //   176: astore 4
    //   178: aload 4
    //   180: instanceof 153
    //   183: ifeq +92 -> 275
    //   186: aload 4
    //   188: invokevirtual 354	android/graphics/drawable/Drawable:getIntrinsicWidth	()I
    //   191: iload 9
    //   193: if_icmpne +40 -> 233
    //   196: aload 4
    //   198: invokevirtual 355	android/graphics/drawable/Drawable:getIntrinsicHeight	()I
    //   201: iload 10
    //   203: if_icmpne +30 -> 233
    //   206: aload 4
    //   208: checkcast 153	android/graphics/drawable/BitmapDrawable
    //   211: astore 4
    //   213: aload_0
    //   214: getfield 351	android/view/PointerIcon:mBitmapFrames	[Landroid/graphics/Bitmap;
    //   217: iload_3
    //   218: iconst_1
    //   219: isub
    //   220: aload_0
    //   221: aload 4
    //   223: invokespecial 357	android/view/PointerIcon:getBitmapFromDrawable	(Landroid/graphics/drawable/BitmapDrawable;)Landroid/graphics/Bitmap;
    //   226: aastore
    //   227: iinc 3 1
    //   230: goto -65 -> 165
    //   233: new 260	java/lang/StringBuilder
    //   236: dup
    //   237: invokespecial 261	java/lang/StringBuilder:<init>	()V
    //   240: astore_1
    //   241: aload_1
    //   242: ldc_w 359
    //   245: invokevirtual 267	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: pop
    //   249: aload_1
    //   250: iload_3
    //   251: invokevirtual 270	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   254: pop
    //   255: aload_1
    //   256: ldc_w 361
    //   259: invokevirtual 267	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   262: pop
    //   263: new 144	java/lang/IllegalArgumentException
    //   266: dup
    //   267: aload_1
    //   268: invokevirtual 274	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   271: invokespecial 149	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   274: athrow
    //   275: new 144	java/lang/IllegalArgumentException
    //   278: dup
    //   279: ldc_w 363
    //   282: invokespecial 149	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   285: athrow
    //   286: aload_2
    //   287: instanceof 153
    //   290: ifeq +38 -> 328
    //   293: aload_0
    //   294: aload_2
    //   295: checkcast 153	android/graphics/drawable/BitmapDrawable
    //   298: invokespecial 357	android/view/PointerIcon:getBitmapFromDrawable	(Landroid/graphics/drawable/BitmapDrawable;)Landroid/graphics/Bitmap;
    //   301: astore_1
    //   302: aload_1
    //   303: fload 6
    //   305: fload 7
    //   307: invokestatic 136	android/view/PointerIcon:validateHotSpot	(Landroid/graphics/Bitmap;FF)V
    //   310: aload_0
    //   311: aload_1
    //   312: putfield 138	android/view/PointerIcon:mBitmap	Landroid/graphics/Bitmap;
    //   315: aload_0
    //   316: fload 6
    //   318: putfield 140	android/view/PointerIcon:mHotSpotX	F
    //   321: aload_0
    //   322: fload 7
    //   324: putfield 142	android/view/PointerIcon:mHotSpotY	F
    //   327: return
    //   328: new 144	java/lang/IllegalArgumentException
    //   331: dup
    //   332: ldc_w 365
    //   335: invokespecial 149	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   338: athrow
    //   339: new 144	java/lang/IllegalArgumentException
    //   342: dup
    //   343: ldc_w 367
    //   346: invokespecial 149	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   349: athrow
    //   350: astore_1
    //   351: goto +18 -> 369
    //   354: astore_2
    //   355: new 144	java/lang/IllegalArgumentException
    //   358: astore_1
    //   359: aload_1
    //   360: ldc_w 369
    //   363: aload_2
    //   364: invokespecial 372	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   367: aload_1
    //   368: athrow
    //   369: aload 4
    //   371: invokeinterface 329 1 0
    //   376: aload_1
    //   377: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	378	0	this	PointerIcon
    //   0	378	1	paramContext	Context
    //   0	378	2	paramResources	Resources
    //   0	378	3	paramInt	int
    //   5	365	4	localObject	Object
    //   24	29	5	localTypedArray	TypedArray
    //   41	276	6	f1	float
    //   50	273	7	f2	float
    //   105	64	8	i	int
    //   155	39	9	j	int
    //   161	43	10	k	int
    // Exception table:
    //   from	to	target	type
    //   7	57	350	finally
    //   355	369	350	finally
    //   7	57	354	java/lang/Exception
  }
  
  private static void registerDisplayListener(Context paramContext)
  {
    sDisplayListener = new DisplayManager.DisplayListener()
    {
      public void onDisplayAdded(int paramAnonymousInt) {}
      
      public void onDisplayChanged(int paramAnonymousInt)
      {
        PointerIcon.gSystemIconsByDisplay.remove(paramAnonymousInt);
      }
      
      public void onDisplayRemoved(int paramAnonymousInt)
      {
        PointerIcon.gSystemIconsByDisplay.remove(paramAnonymousInt);
      }
    };
    ((DisplayManager)paramContext.getSystemService(DisplayManager.class)).registerDisplayListener(sDisplayListener, null);
  }
  
  public static void setUseLargeIcons(boolean paramBoolean)
  {
    sUseLargeIcons = paramBoolean;
    gSystemIconsByDisplay.clear();
  }
  
  private static void validateHotSpot(Bitmap paramBitmap, float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 >= 0.0F) && (paramFloat1 < paramBitmap.getWidth()))
    {
      if ((paramFloat2 >= 0.0F) && (paramFloat2 < paramBitmap.getHeight())) {
        return;
      }
      throw new IllegalArgumentException("y hotspot lies outside of the bitmap area");
    }
    throw new IllegalArgumentException("x hotspot lies outside of the bitmap area");
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && ((paramObject instanceof PointerIcon)))
    {
      paramObject = (PointerIcon)paramObject;
      if (this.mType == ((PointerIcon)paramObject).mType)
      {
        int i = this.mSystemIconResourceId;
        if (i == ((PointerIcon)paramObject).mSystemIconResourceId) {
          return (i != 0) || ((this.mBitmap == ((PointerIcon)paramObject).mBitmap) && (this.mHotSpotX == ((PointerIcon)paramObject).mHotSpotX) && (this.mHotSpotY == ((PointerIcon)paramObject).mHotSpotY));
        }
      }
      return false;
    }
    return false;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=115609023L)
  public PointerIcon load(Context paramContext)
  {
    if (paramContext != null)
    {
      if ((this.mSystemIconResourceId != 0) && (this.mBitmap == null))
      {
        PointerIcon localPointerIcon = new PointerIcon(this.mType);
        localPointerIcon.mSystemIconResourceId = this.mSystemIconResourceId;
        localPointerIcon.loadResource(paramContext, paramContext.getResources(), this.mSystemIconResourceId);
        return localPointerIcon;
      }
      return this;
    }
    throw new IllegalArgumentException("context must not be null");
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mType);
    if (this.mType != 0)
    {
      paramParcel.writeInt(this.mSystemIconResourceId);
      if (this.mSystemIconResourceId == 0)
      {
        this.mBitmap.writeToParcel(paramParcel, paramInt);
        paramParcel.writeFloat(this.mHotSpotX);
        paramParcel.writeFloat(this.mHotSpotY);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/PointerIcon.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */