package android.view;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.widget.FrameLayout;
import com.android.internal.R.styleable;
import dalvik.system.PathClassLoader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class LayoutInflater
{
  @UnsupportedAppUsage
  private static final int[] ATTRS_THEME = { 16842752 };
  private static final String ATTR_LAYOUT = "layout";
  private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();
  private static final String COMPILED_VIEW_DEX_FILE_NAME = "/compiled_view.dex";
  private static final boolean DEBUG = false;
  private static final StackTraceElement[] EMPTY_STACK_TRACE;
  private static final String TAG = LayoutInflater.class.getSimpleName();
  private static final String TAG_1995 = "blink";
  private static final String TAG_INCLUDE = "include";
  private static final String TAG_MERGE = "merge";
  private static final String TAG_REQUEST_FOCUS = "requestFocus";
  private static final String TAG_TAG = "tag";
  private static final String USE_PRECOMPILED_LAYOUT = "view.precompiled_layout_enabled";
  @UnsupportedAppUsage
  static final Class<?>[] mConstructorSignature;
  @UnsupportedAppUsage(maxTargetSdk=28, trackingBug=123769490L)
  private static final HashMap<String, Constructor<? extends View>> sConstructorMap;
  @UnsupportedAppUsage(maxTargetSdk=28)
  final Object[] mConstructorArgs = new Object[2];
  @UnsupportedAppUsage(maxTargetSdk=28)
  protected final Context mContext;
  @UnsupportedAppUsage
  private Factory mFactory;
  @UnsupportedAppUsage
  private Factory2 mFactory2;
  @UnsupportedAppUsage(maxTargetSdk=28)
  private boolean mFactorySet;
  private Filter mFilter;
  private HashMap<String, Boolean> mFilterMap;
  private ClassLoader mPrecompiledClassLoader;
  @UnsupportedAppUsage
  private Factory2 mPrivateFactory;
  private TypedValue mTempValue;
  private boolean mUseCompiledView;
  
  static
  {
    EMPTY_STACK_TRACE = new StackTraceElement[0];
    mConstructorSignature = new Class[] { Context.class, AttributeSet.class };
    sConstructorMap = new HashMap();
  }
  
  protected LayoutInflater(Context paramContext)
  {
    this.mContext = paramContext;
    initPrecompiledViews();
  }
  
  protected LayoutInflater(LayoutInflater paramLayoutInflater, Context paramContext)
  {
    this.mContext = paramContext;
    this.mFactory = paramLayoutInflater.mFactory;
    this.mFactory2 = paramLayoutInflater.mFactory2;
    this.mPrivateFactory = paramLayoutInflater.mPrivateFactory;
    setFilter(paramLayoutInflater.mFilter);
    initPrecompiledViews();
  }
  
  private void advanceToRootNode(XmlPullParser paramXmlPullParser)
    throws InflateException, IOException, XmlPullParserException
  {
    int i;
    do
    {
      i = paramXmlPullParser.next();
    } while ((i != 2) && (i != 1));
    if (i == 2) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramXmlPullParser.getPositionDescription());
    localStringBuilder.append(": No start tag found!");
    throw new InflateException(localStringBuilder.toString());
  }
  
  static final void consumeChildElements(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    int j;
    do
    {
      j = paramXmlPullParser.next();
    } while (((j != 3) || (paramXmlPullParser.getDepth() > i)) && (j != 1));
  }
  
  @UnsupportedAppUsage
  private View createViewFromTag(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    return createViewFromTag(paramView, paramString, paramContext, paramAttributeSet, false);
  }
  
  private void failNotAllowed(String paramString1, String paramString2, Context paramContext, AttributeSet paramAttributeSet)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getParserStateDescription(paramContext, paramAttributeSet));
    localStringBuilder.append(": Class not allowed to be inflated ");
    if (paramString2 != null)
    {
      paramContext = new StringBuilder();
      paramContext.append(paramString2);
      paramContext.append(paramString1);
      paramString1 = paramContext.toString();
    }
    localStringBuilder.append(paramString1);
    throw new InflateException(localStringBuilder.toString());
  }
  
  public static LayoutInflater from(Context paramContext)
  {
    paramContext = (LayoutInflater)paramContext.getSystemService("layout_inflater");
    if (paramContext != null) {
      return paramContext;
    }
    throw new AssertionError("LayoutInflater not found.");
  }
  
  private static String getParserStateDescription(Context paramContext, AttributeSet paramAttributeSet)
  {
    int i = Resources.getAttributeSetSourceResId(paramAttributeSet);
    if (i == 0) {
      return paramAttributeSet.getPositionDescription();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramAttributeSet.getPositionDescription());
    localStringBuilder.append(" in ");
    localStringBuilder.append(paramContext.getResources().getResourceName(i));
    return localStringBuilder.toString();
  }
  
  private void initPrecompiledViews()
  {
    initPrecompiledViews(false);
  }
  
  private void initPrecompiledViews(boolean paramBoolean)
  {
    this.mUseCompiledView = paramBoolean;
    if (!this.mUseCompiledView)
    {
      this.mPrecompiledClassLoader = null;
      return;
    }
    Object localObject1 = this.mContext.getApplicationInfo();
    if ((!((ApplicationInfo)localObject1).isEmbeddedDexUsed()) && (!((ApplicationInfo)localObject1).isPrivilegedApp()))
    {
      try
      {
        this.mPrecompiledClassLoader = this.mContext.getClassLoader();
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        ((StringBuilder)localObject1).append(this.mContext.getCodeCacheDir());
        ((StringBuilder)localObject1).append("/compiled_view.dex");
        localObject1 = ((StringBuilder)localObject1).toString();
        Object localObject3 = new java/io/File;
        ((File)localObject3).<init>((String)localObject1);
        if (((File)localObject3).exists())
        {
          localObject3 = new dalvik/system/PathClassLoader;
          ((PathClassLoader)localObject3).<init>((String)localObject1, this.mPrecompiledClassLoader);
          this.mPrecompiledClassLoader = ((ClassLoader)localObject3);
        }
      }
      finally
      {
        this.mUseCompiledView = false;
      }
      if (!this.mUseCompiledView) {
        this.mPrecompiledClassLoader = null;
      }
      return;
    }
    this.mUseCompiledView = false;
  }
  
  /* Error */
  @UnsupportedAppUsage
  private void parseInclude(XmlPullParser paramXmlPullParser, Context paramContext, View paramView, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    // Byte code:
    //   0: aload_3
    //   1: instanceof 273
    //   4: ifeq +601 -> 605
    //   7: aload_2
    //   8: aload 4
    //   10: getstatic 116	android/view/LayoutInflater:ATTRS_THEME	[I
    //   13: invokevirtual 277	android/content/Context:obtainStyledAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   16: astore 5
    //   18: aload 5
    //   20: iconst_0
    //   21: iconst_0
    //   22: invokevirtual 283	android/content/res/TypedArray:getResourceId	(II)I
    //   25: istore 6
    //   27: iload 6
    //   29: ifeq +9 -> 38
    //   32: iconst_1
    //   33: istore 7
    //   35: goto +6 -> 41
    //   38: iconst_0
    //   39: istore 7
    //   41: iload 7
    //   43: ifeq +17 -> 60
    //   46: new 285	android/view/ContextThemeWrapper
    //   49: dup
    //   50: aload_2
    //   51: iload 6
    //   53: invokespecial 288	android/view/ContextThemeWrapper:<init>	(Landroid/content/Context;I)V
    //   56: astore_2
    //   57: goto +3 -> 60
    //   60: aload 5
    //   62: invokevirtual 291	android/content/res/TypedArray:recycle	()V
    //   65: aload 4
    //   67: aconst_null
    //   68: ldc 28
    //   70: iconst_0
    //   71: invokeinterface 295 4 0
    //   76: istore 8
    //   78: iload 8
    //   80: istore 6
    //   82: iload 8
    //   84: ifne +64 -> 148
    //   87: aload 4
    //   89: aconst_null
    //   90: ldc 28
    //   92: invokeinterface 299 3 0
    //   97: astore 5
    //   99: aload 5
    //   101: ifnull +36 -> 137
    //   104: aload 5
    //   106: invokevirtual 304	java/lang/String:length	()I
    //   109: ifle +28 -> 137
    //   112: aload_2
    //   113: invokevirtual 224	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   116: aload 5
    //   118: iconst_1
    //   119: invokevirtual 307	java/lang/String:substring	(I)Ljava/lang/String;
    //   122: ldc_w 309
    //   125: aload_2
    //   126: invokevirtual 312	android/content/Context:getPackageName	()Ljava/lang/String;
    //   129: invokevirtual 316	android/content/res/Resources:getIdentifier	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
    //   132: istore 6
    //   134: goto +14 -> 148
    //   137: new 149	android/view/InflateException
    //   140: dup
    //   141: ldc_w 318
    //   144: invokespecial 177	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   147: athrow
    //   148: aload_0
    //   149: getfield 320	android/view/LayoutInflater:mTempValue	Landroid/util/TypedValue;
    //   152: ifnonnull +14 -> 166
    //   155: aload_0
    //   156: new 322	android/util/TypedValue
    //   159: dup
    //   160: invokespecial 323	android/util/TypedValue:<init>	()V
    //   163: putfield 320	android/view/LayoutInflater:mTempValue	Landroid/util/TypedValue;
    //   166: iload 6
    //   168: ifeq +32 -> 200
    //   171: aload_2
    //   172: invokevirtual 327	android/content/Context:getTheme	()Landroid/content/res/Resources$Theme;
    //   175: iload 6
    //   177: aload_0
    //   178: getfield 320	android/view/LayoutInflater:mTempValue	Landroid/util/TypedValue;
    //   181: iconst_1
    //   182: invokevirtual 333	android/content/res/Resources$Theme:resolveAttribute	(ILandroid/util/TypedValue;Z)Z
    //   185: ifeq +15 -> 200
    //   188: aload_0
    //   189: getfield 320	android/view/LayoutInflater:mTempValue	Landroid/util/TypedValue;
    //   192: getfield 337	android/util/TypedValue:resourceId	I
    //   195: istore 6
    //   197: goto +3 -> 200
    //   200: iload 6
    //   202: ifeq +350 -> 552
    //   205: aload_0
    //   206: iload 6
    //   208: aload_2
    //   209: invokevirtual 224	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   212: aload_3
    //   213: checkcast 273	android/view/ViewGroup
    //   216: iconst_1
    //   217: invokespecial 341	android/view/LayoutInflater:tryInflatePrecompiled	(ILandroid/content/res/Resources;Landroid/view/ViewGroup;Z)Landroid/view/View;
    //   220: ifnonnull +327 -> 547
    //   223: aload_2
    //   224: invokevirtual 224	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   227: iload 6
    //   229: invokevirtual 345	android/content/res/Resources:getLayout	(I)Landroid/content/res/XmlResourceParser;
    //   232: astore 5
    //   234: aload 5
    //   236: invokestatic 351	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   239: astore 9
    //   241: aload 5
    //   243: invokeinterface 354 1 0
    //   248: istore 6
    //   250: iload 6
    //   252: iconst_2
    //   253: if_icmpeq +12 -> 265
    //   256: iload 6
    //   258: iconst_1
    //   259: if_icmpeq +6 -> 265
    //   262: goto -21 -> 241
    //   265: iload 6
    //   267: iconst_2
    //   268: if_icmpne +225 -> 493
    //   271: aload 5
    //   273: invokeinterface 357 1 0
    //   278: astore 10
    //   280: ldc 48
    //   282: aload 10
    //   284: invokevirtual 361	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   287: istore 11
    //   289: iload 11
    //   291: ifeq +21 -> 312
    //   294: aload_0
    //   295: aload 5
    //   297: aload_3
    //   298: aload_2
    //   299: aload 9
    //   301: iconst_0
    //   302: invokevirtual 365	android/view/LayoutInflater:rInflate	(Lorg/xmlpull/v1/XmlPullParser;Landroid/view/View;Landroid/content/Context;Landroid/util/AttributeSet;Z)V
    //   305: goto +174 -> 479
    //   308: astore_1
    //   309: goto +229 -> 538
    //   312: aload_0
    //   313: aload_3
    //   314: aload 10
    //   316: aload_2
    //   317: aload 9
    //   319: iload 7
    //   321: invokevirtual 187	android/view/LayoutInflater:createViewFromTag	(Landroid/view/View;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;Z)Landroid/view/View;
    //   324: astore 10
    //   326: aload_3
    //   327: checkcast 273	android/view/ViewGroup
    //   330: astore 12
    //   332: aload_2
    //   333: aload 4
    //   335: getstatic 370	com/android/internal/R$styleable:Include	[I
    //   338: invokevirtual 277	android/content/Context:obtainStyledAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   341: astore_2
    //   342: aload_2
    //   343: iconst_0
    //   344: iconst_m1
    //   345: invokevirtual 283	android/content/res/TypedArray:getResourceId	(II)I
    //   348: istore 6
    //   350: aload_2
    //   351: iconst_1
    //   352: iconst_m1
    //   353: invokevirtual 373	android/content/res/TypedArray:getInt	(II)I
    //   356: istore 8
    //   358: aload_2
    //   359: invokevirtual 291	android/content/res/TypedArray:recycle	()V
    //   362: aconst_null
    //   363: astore_2
    //   364: aload 12
    //   366: aload 4
    //   368: invokevirtual 377	android/view/ViewGroup:generateLayoutParams	(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    //   371: astore_3
    //   372: aload_3
    //   373: astore_2
    //   374: goto +8 -> 382
    //   377: astore_1
    //   378: goto +160 -> 538
    //   381: astore_3
    //   382: aload_2
    //   383: ifnonnull +14 -> 397
    //   386: aload 12
    //   388: aload 9
    //   390: invokevirtual 377	android/view/ViewGroup:generateLayoutParams	(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    //   393: astore_2
    //   394: goto +3 -> 397
    //   397: aload 10
    //   399: aload_2
    //   400: invokevirtual 383	android/view/View:setLayoutParams	(Landroid/view/ViewGroup$LayoutParams;)V
    //   403: aload_0
    //   404: aload 5
    //   406: aload 10
    //   408: aload 9
    //   410: iconst_1
    //   411: invokevirtual 387	android/view/LayoutInflater:rInflateChildren	(Lorg/xmlpull/v1/XmlPullParser;Landroid/view/View;Landroid/util/AttributeSet;Z)V
    //   414: iload 6
    //   416: iconst_m1
    //   417: if_icmpeq +10 -> 427
    //   420: aload 10
    //   422: iload 6
    //   424: invokevirtual 391	android/view/View:setId	(I)V
    //   427: iload 8
    //   429: ifeq +37 -> 466
    //   432: iload 8
    //   434: iconst_1
    //   435: if_icmpeq +22 -> 457
    //   438: iload 8
    //   440: iconst_2
    //   441: if_icmpeq +6 -> 447
    //   444: goto +28 -> 472
    //   447: aload 10
    //   449: bipush 8
    //   451: invokevirtual 394	android/view/View:setVisibility	(I)V
    //   454: goto +18 -> 472
    //   457: aload 10
    //   459: iconst_4
    //   460: invokevirtual 394	android/view/View:setVisibility	(I)V
    //   463: goto +9 -> 472
    //   466: aload 10
    //   468: iconst_0
    //   469: invokevirtual 394	android/view/View:setVisibility	(I)V
    //   472: aload 12
    //   474: aload 10
    //   476: invokevirtual 398	android/view/ViewGroup:addView	(Landroid/view/View;)V
    //   479: aload 5
    //   481: invokeinterface 401 1 0
    //   486: goto +61 -> 547
    //   489: astore_1
    //   490: goto +48 -> 538
    //   493: new 149	android/view/InflateException
    //   496: astore_1
    //   497: new 161	java/lang/StringBuilder
    //   500: astore_3
    //   501: aload_3
    //   502: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   505: aload_3
    //   506: aload_2
    //   507: aload 9
    //   509: invokestatic 194	android/view/LayoutInflater:getParserStateDescription	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   512: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   515: pop
    //   516: aload_3
    //   517: ldc -85
    //   519: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   522: pop
    //   523: aload_1
    //   524: aload_3
    //   525: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   528: invokespecial 177	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   531: aload_1
    //   532: athrow
    //   533: astore_1
    //   534: goto +4 -> 538
    //   537: astore_1
    //   538: aload 5
    //   540: invokeinterface 401 1 0
    //   545: aload_1
    //   546: athrow
    //   547: aload_1
    //   548: invokestatic 403	android/view/LayoutInflater:consumeChildElements	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   551: return
    //   552: aload 4
    //   554: aconst_null
    //   555: ldc 28
    //   557: invokeinterface 299 3 0
    //   562: astore_2
    //   563: new 161	java/lang/StringBuilder
    //   566: dup
    //   567: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   570: astore_1
    //   571: aload_1
    //   572: ldc_w 405
    //   575: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   578: pop
    //   579: aload_1
    //   580: aload_2
    //   581: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: pop
    //   585: aload_1
    //   586: ldc_w 407
    //   589: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   592: pop
    //   593: new 149	android/view/InflateException
    //   596: dup
    //   597: aload_1
    //   598: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   601: invokespecial 177	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   604: athrow
    //   605: new 149	android/view/InflateException
    //   608: dup
    //   609: ldc_w 409
    //   612: invokespecial 177	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   615: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	616	0	this	LayoutInflater
    //   0	616	1	paramXmlPullParser	XmlPullParser
    //   0	616	2	paramContext	Context
    //   0	616	3	paramView	View
    //   0	616	4	paramAttributeSet	AttributeSet
    //   16	523	5	localObject1	Object
    //   25	398	6	i	int
    //   33	287	7	bool1	boolean
    //   76	366	8	j	int
    //   239	269	9	localAttributeSet	AttributeSet
    //   278	197	10	localObject2	Object
    //   287	3	11	bool2	boolean
    //   330	143	12	localViewGroup	ViewGroup
    // Exception table:
    //   from	to	target	type
    //   294	305	308	finally
    //   364	372	377	finally
    //   386	394	377	finally
    //   364	372	381	java/lang/RuntimeException
    //   312	362	489	finally
    //   397	403	489	finally
    //   403	414	533	finally
    //   420	427	533	finally
    //   447	454	533	finally
    //   457	463	533	finally
    //   466	472	533	finally
    //   472	479	533	finally
    //   493	533	533	finally
    //   234	241	537	finally
    //   241	250	537	finally
    //   271	289	537	finally
  }
  
  private void parseViewTag(XmlPullParser paramXmlPullParser, View paramView, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    paramAttributeSet = paramView.getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.ViewTag);
    paramView.setTag(paramAttributeSet.getResourceId(1, 0), paramAttributeSet.getText(0));
    paramAttributeSet.recycle();
    consumeChildElements(paramXmlPullParser);
  }
  
  private View tryInflatePrecompiled(int paramInt, Resources paramResources, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    if (!this.mUseCompiledView) {
      return null;
    }
    Trace.traceBegin(8L, "inflate (precompiled)");
    Object localObject1 = paramResources.getResourcePackageName(paramInt);
    Object localObject2 = paramResources.getResourceEntryName(paramInt);
    try
    {
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      localStringBuilder.append("");
      localStringBuilder.append((String)localObject1);
      localStringBuilder.append(".CompiledView");
      localObject2 = (View)Class.forName(localStringBuilder.toString(), false, this.mPrecompiledClassLoader).getMethod((String)localObject2, new Class[] { Context.class, Integer.TYPE }).invoke(null, new Object[] { this.mContext, Integer.valueOf(paramInt) });
      if ((localObject2 != null) && (paramViewGroup != null))
      {
        paramResources = paramResources.getLayout(paramInt);
        try
        {
          localObject1 = Xml.asAttributeSet(paramResources);
          advanceToRootNode(paramResources);
          localObject1 = paramViewGroup.generateLayoutParams((AttributeSet)localObject1);
          if (paramBoolean) {
            paramViewGroup.addView((View)localObject2, (ViewGroup.LayoutParams)localObject1);
          } else {
            ((View)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
          }
        }
        finally
        {
          paramResources.close();
        }
      }
      return (View)localObject2;
    }
    finally
    {
      Trace.traceEnd(8L);
    }
    return null;
  }
  
  private final boolean verifyClassLoader(Constructor<? extends View> paramConstructor)
  {
    ClassLoader localClassLoader = paramConstructor.getDeclaringClass().getClassLoader();
    if (localClassLoader == BOOT_CLASS_LOADER) {
      return true;
    }
    paramConstructor = this.mContext.getClassLoader();
    for (;;)
    {
      if (localClassLoader == paramConstructor) {
        return true;
      }
      paramConstructor = paramConstructor.getParent();
      if (paramConstructor == null) {
        return false;
      }
    }
  }
  
  public abstract LayoutInflater cloneInContext(Context paramContext);
  
  /* Error */
  public final View createView(Context paramContext, String paramString1, String paramString2, AttributeSet paramAttributeSet)
    throws ClassNotFoundException, InflateException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 511	java/util/Objects:requireNonNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 511	java/util/Objects:requireNonNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   9: pop
    //   10: getstatic 113	android/view/LayoutInflater:sConstructorMap	Ljava/util/HashMap;
    //   13: aload_2
    //   14: invokevirtual 514	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: checkcast 483	java/lang/reflect/Constructor
    //   20: astore 5
    //   22: aload 5
    //   24: astore 6
    //   26: aload 5
    //   28: ifnull +27 -> 55
    //   31: aload 5
    //   33: astore 6
    //   35: aload_0
    //   36: aload 5
    //   38: invokespecial 516	android/view/LayoutInflater:verifyClassLoader	(Ljava/lang/reflect/Constructor;)Z
    //   41: ifne +14 -> 55
    //   44: aconst_null
    //   45: astore 6
    //   47: getstatic 113	android/view/LayoutInflater:sConstructorMap	Ljava/util/HashMap;
    //   50: aload_2
    //   51: invokevirtual 519	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   54: pop
    //   55: aconst_null
    //   56: astore 7
    //   58: aconst_null
    //   59: astore 8
    //   61: aconst_null
    //   62: astore 9
    //   64: aload 8
    //   66: astore 5
    //   68: ldc2_w 427
    //   71: aload_2
    //   72: invokestatic 436	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   75: aload 6
    //   77: ifnonnull +177 -> 254
    //   80: aload_3
    //   81: ifnull +57 -> 138
    //   84: aload 8
    //   86: astore 5
    //   88: new 161	java/lang/StringBuilder
    //   91: astore 10
    //   93: aload 8
    //   95: astore 5
    //   97: aload 10
    //   99: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   102: aload 8
    //   104: astore 5
    //   106: aload 10
    //   108: aload_3
    //   109: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   112: pop
    //   113: aload 8
    //   115: astore 5
    //   117: aload 10
    //   119: aload_2
    //   120: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   123: pop
    //   124: aload 8
    //   126: astore 5
    //   128: aload 10
    //   130: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   133: astore 10
    //   135: goto +6 -> 141
    //   138: aload_2
    //   139: astore 10
    //   141: aload 8
    //   143: astore 5
    //   145: aload 10
    //   147: iconst_0
    //   148: aload_0
    //   149: getfield 129	android/view/LayoutInflater:mContext	Landroid/content/Context;
    //   152: invokevirtual 249	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   155: invokestatic 450	java/lang/Class:forName	(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;
    //   158: ldc_w 379
    //   161: invokevirtual 523	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   164: astore 10
    //   166: aload 10
    //   168: astore 5
    //   170: aload_0
    //   171: getfield 141	android/view/LayoutInflater:mFilter	Landroid/view/LayoutInflater$Filter;
    //   174: ifnull +39 -> 213
    //   177: aload 10
    //   179: ifnull +34 -> 213
    //   182: aload 10
    //   184: astore 5
    //   186: aload_0
    //   187: getfield 141	android/view/LayoutInflater:mFilter	Landroid/view/LayoutInflater$Filter;
    //   190: aload 10
    //   192: invokeinterface 527 2 0
    //   197: ifne +16 -> 213
    //   200: aload 10
    //   202: astore 5
    //   204: aload_0
    //   205: aload_2
    //   206: aload_3
    //   207: aload_1
    //   208: aload 4
    //   210: invokespecial 529	android/view/LayoutInflater:failNotAllowed	(Ljava/lang/String;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)V
    //   213: aload 10
    //   215: astore 5
    //   217: aload 10
    //   219: getstatic 106	android/view/LayoutInflater:mConstructorSignature	[Ljava/lang/Class;
    //   222: invokevirtual 533	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
    //   225: astore 11
    //   227: aload 10
    //   229: astore 5
    //   231: aload 11
    //   233: iconst_1
    //   234: invokevirtual 536	java/lang/reflect/Constructor:setAccessible	(Z)V
    //   237: aload 10
    //   239: astore 5
    //   241: getstatic 113	android/view/LayoutInflater:sConstructorMap	Ljava/util/HashMap;
    //   244: aload_2
    //   245: aload 11
    //   247: invokevirtual 540	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   250: pop
    //   251: goto +253 -> 504
    //   254: aload 6
    //   256: astore 11
    //   258: aload 7
    //   260: astore 10
    //   262: aload 8
    //   264: astore 5
    //   266: aload_0
    //   267: getfield 141	android/view/LayoutInflater:mFilter	Landroid/view/LayoutInflater$Filter;
    //   270: ifnull +234 -> 504
    //   273: aload 8
    //   275: astore 5
    //   277: aload_0
    //   278: getfield 542	android/view/LayoutInflater:mFilterMap	Ljava/util/HashMap;
    //   281: aload_2
    //   282: invokevirtual 514	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   285: checkcast 544	java/lang/Boolean
    //   288: astore 11
    //   290: aload 11
    //   292: ifnonnull +172 -> 464
    //   295: aload_3
    //   296: ifnull +57 -> 353
    //   299: aload 8
    //   301: astore 5
    //   303: new 161	java/lang/StringBuilder
    //   306: astore 10
    //   308: aload 8
    //   310: astore 5
    //   312: aload 10
    //   314: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   317: aload 8
    //   319: astore 5
    //   321: aload 10
    //   323: aload_3
    //   324: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   327: pop
    //   328: aload 8
    //   330: astore 5
    //   332: aload 10
    //   334: aload_2
    //   335: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   338: pop
    //   339: aload 8
    //   341: astore 5
    //   343: aload 10
    //   345: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   348: astore 10
    //   350: goto +6 -> 356
    //   353: aload_2
    //   354: astore 10
    //   356: aload 8
    //   358: astore 5
    //   360: aload 10
    //   362: iconst_0
    //   363: aload_0
    //   364: getfield 129	android/view/LayoutInflater:mContext	Landroid/content/Context;
    //   367: invokevirtual 249	android/content/Context:getClassLoader	()Ljava/lang/ClassLoader;
    //   370: invokestatic 450	java/lang/Class:forName	(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;
    //   373: ldc_w 379
    //   376: invokevirtual 523	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   379: astore 11
    //   381: aload 11
    //   383: ifnull +27 -> 410
    //   386: aload 11
    //   388: astore 5
    //   390: aload_0
    //   391: getfield 141	android/view/LayoutInflater:mFilter	Landroid/view/LayoutInflater$Filter;
    //   394: aload 11
    //   396: invokeinterface 527 2 0
    //   401: ifeq +9 -> 410
    //   404: iconst_1
    //   405: istore 12
    //   407: goto +6 -> 413
    //   410: iconst_0
    //   411: istore 12
    //   413: aload 11
    //   415: astore 5
    //   417: aload_0
    //   418: getfield 542	android/view/LayoutInflater:mFilterMap	Ljava/util/HashMap;
    //   421: aload_2
    //   422: iload 12
    //   424: invokestatic 547	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   427: invokevirtual 540	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   430: pop
    //   431: aload 11
    //   433: astore 10
    //   435: iload 12
    //   437: ifne +20 -> 457
    //   440: aload 11
    //   442: astore 5
    //   444: aload_0
    //   445: aload_2
    //   446: aload_3
    //   447: aload_1
    //   448: aload 4
    //   450: invokespecial 529	android/view/LayoutInflater:failNotAllowed	(Ljava/lang/String;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)V
    //   453: aload 11
    //   455: astore 10
    //   457: aload 6
    //   459: astore 11
    //   461: goto +43 -> 504
    //   464: aload 9
    //   466: astore 10
    //   468: aload 8
    //   470: astore 5
    //   472: aload 11
    //   474: getstatic 551	java/lang/Boolean:FALSE	Ljava/lang/Boolean;
    //   477: invokevirtual 552	java/lang/Boolean:equals	(Ljava/lang/Object;)Z
    //   480: ifeq -23 -> 457
    //   483: aload 8
    //   485: astore 5
    //   487: aload_0
    //   488: aload_2
    //   489: aload_3
    //   490: aload_1
    //   491: aload 4
    //   493: invokespecial 529	android/view/LayoutInflater:failNotAllowed	(Ljava/lang/String;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)V
    //   496: aload 7
    //   498: astore 10
    //   500: aload 6
    //   502: astore 11
    //   504: aload 10
    //   506: astore 5
    //   508: aload_0
    //   509: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   512: iconst_0
    //   513: aaload
    //   514: astore 6
    //   516: aload 10
    //   518: astore 5
    //   520: aload_0
    //   521: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   524: iconst_0
    //   525: aload_1
    //   526: aastore
    //   527: aload 10
    //   529: astore 5
    //   531: aload_0
    //   532: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   535: astore 8
    //   537: aload 8
    //   539: iconst_1
    //   540: aload 4
    //   542: aastore
    //   543: aload 11
    //   545: aload 8
    //   547: invokevirtual 556	java/lang/reflect/Constructor:newInstance	([Ljava/lang/Object;)Ljava/lang/Object;
    //   550: checkcast 379	android/view/View
    //   553: astore 11
    //   555: aload 11
    //   557: instanceof 558
    //   560: ifeq +22 -> 582
    //   563: aload 11
    //   565: checkcast 558	android/view/ViewStub
    //   568: aload_0
    //   569: aload 8
    //   571: iconst_0
    //   572: aaload
    //   573: checkcast 102	android/content/Context
    //   576: invokevirtual 560	android/view/LayoutInflater:cloneInContext	(Landroid/content/Context;)Landroid/view/LayoutInflater;
    //   579: invokevirtual 564	android/view/ViewStub:setLayoutInflater	(Landroid/view/LayoutInflater;)V
    //   582: aload 10
    //   584: astore 5
    //   586: aload_0
    //   587: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   590: iconst_0
    //   591: aload 6
    //   593: aastore
    //   594: ldc2_w 427
    //   597: invokestatic 479	android/os/Trace:traceEnd	(J)V
    //   600: aload 11
    //   602: areturn
    //   603: astore 11
    //   605: aload 10
    //   607: astore 5
    //   609: aload_0
    //   610: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   613: iconst_0
    //   614: aload 6
    //   616: aastore
    //   617: aload 10
    //   619: astore 5
    //   621: aload 11
    //   623: athrow
    //   624: astore_1
    //   625: goto +286 -> 911
    //   628: astore_2
    //   629: new 149	android/view/InflateException
    //   632: astore 10
    //   634: new 161	java/lang/StringBuilder
    //   637: astore_3
    //   638: aload_3
    //   639: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   642: aload_3
    //   643: aload_1
    //   644: aload 4
    //   646: invokestatic 194	android/view/LayoutInflater:getParserStateDescription	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   649: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   652: pop
    //   653: aload_3
    //   654: ldc_w 566
    //   657: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   660: pop
    //   661: aload 5
    //   663: ifnonnull +10 -> 673
    //   666: ldc_w 568
    //   669: astore_1
    //   670: goto +9 -> 679
    //   673: aload 5
    //   675: invokevirtual 569	java/lang/Class:getName	()Ljava/lang/String;
    //   678: astore_1
    //   679: aload_3
    //   680: aload_1
    //   681: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   684: pop
    //   685: aload 10
    //   687: aload_3
    //   688: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   691: aload_2
    //   692: invokespecial 572	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   695: aload 10
    //   697: getstatic 100	android/view/LayoutInflater:EMPTY_STACK_TRACE	[Ljava/lang/StackTraceElement;
    //   700: invokevirtual 576	android/view/InflateException:setStackTrace	([Ljava/lang/StackTraceElement;)V
    //   703: aload 10
    //   705: athrow
    //   706: astore_1
    //   707: aload_1
    //   708: athrow
    //   709: astore 6
    //   711: new 149	android/view/InflateException
    //   714: astore 10
    //   716: new 161	java/lang/StringBuilder
    //   719: astore 5
    //   721: aload 5
    //   723: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   726: aload 5
    //   728: aload_1
    //   729: aload 4
    //   731: invokestatic 194	android/view/LayoutInflater:getParserStateDescription	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   734: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   737: pop
    //   738: aload 5
    //   740: ldc_w 578
    //   743: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   746: pop
    //   747: aload_3
    //   748: ifnull +31 -> 779
    //   751: new 161	java/lang/StringBuilder
    //   754: astore_1
    //   755: aload_1
    //   756: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   759: aload_1
    //   760: aload_3
    //   761: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   764: pop
    //   765: aload_1
    //   766: aload_2
    //   767: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   770: pop
    //   771: aload_1
    //   772: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   775: astore_1
    //   776: goto +5 -> 781
    //   779: aload_2
    //   780: astore_1
    //   781: aload 5
    //   783: aload_1
    //   784: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   787: pop
    //   788: aload 10
    //   790: aload 5
    //   792: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   795: aload 6
    //   797: invokespecial 572	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   800: aload 10
    //   802: getstatic 100	android/view/LayoutInflater:EMPTY_STACK_TRACE	[Ljava/lang/StackTraceElement;
    //   805: invokevirtual 576	android/view/InflateException:setStackTrace	([Ljava/lang/StackTraceElement;)V
    //   808: aload 10
    //   810: athrow
    //   811: astore 6
    //   813: new 149	android/view/InflateException
    //   816: astore 5
    //   818: new 161	java/lang/StringBuilder
    //   821: astore 10
    //   823: aload 10
    //   825: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   828: aload 10
    //   830: aload_1
    //   831: aload 4
    //   833: invokestatic 194	android/view/LayoutInflater:getParserStateDescription	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   836: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   839: pop
    //   840: aload 10
    //   842: ldc_w 566
    //   845: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   848: pop
    //   849: aload_3
    //   850: ifnull +31 -> 881
    //   853: new 161	java/lang/StringBuilder
    //   856: astore_1
    //   857: aload_1
    //   858: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   861: aload_1
    //   862: aload_3
    //   863: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   866: pop
    //   867: aload_1
    //   868: aload_2
    //   869: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   872: pop
    //   873: aload_1
    //   874: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   877: astore_2
    //   878: goto +3 -> 881
    //   881: aload 10
    //   883: aload_2
    //   884: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   887: pop
    //   888: aload 5
    //   890: aload 10
    //   892: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   895: aload 6
    //   897: invokespecial 572	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   900: aload 5
    //   902: getstatic 100	android/view/LayoutInflater:EMPTY_STACK_TRACE	[Ljava/lang/StackTraceElement;
    //   905: invokevirtual 576	android/view/InflateException:setStackTrace	([Ljava/lang/StackTraceElement;)V
    //   908: aload 5
    //   910: athrow
    //   911: ldc2_w 427
    //   914: invokestatic 479	android/os/Trace:traceEnd	(J)V
    //   917: aload_1
    //   918: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	919	0	this	LayoutInflater
    //   0	919	1	paramContext	Context
    //   0	919	2	paramString1	String
    //   0	919	3	paramString2	String
    //   0	919	4	paramAttributeSet	AttributeSet
    //   20	889	5	localObject1	Object
    //   24	591	6	localObject2	Object
    //   709	87	6	localClassCastException	ClassCastException
    //   811	85	6	localNoSuchMethodException	NoSuchMethodException
    //   56	441	7	localObject3	Object
    //   59	511	8	arrayOfObject	Object[]
    //   62	403	9	localObject4	Object
    //   91	800	10	localObject5	Object
    //   225	376	11	localObject6	Object
    //   603	19	11	localObject7	Object
    //   405	31	12	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   543	582	603	finally
    //   68	75	624	finally
    //   88	93	624	finally
    //   97	102	624	finally
    //   106	113	624	finally
    //   117	124	624	finally
    //   128	135	624	finally
    //   145	166	624	finally
    //   170	177	624	finally
    //   186	200	624	finally
    //   204	213	624	finally
    //   217	227	624	finally
    //   231	237	624	finally
    //   241	251	624	finally
    //   266	273	624	finally
    //   277	290	624	finally
    //   303	308	624	finally
    //   312	317	624	finally
    //   321	328	624	finally
    //   332	339	624	finally
    //   343	350	624	finally
    //   360	381	624	finally
    //   390	404	624	finally
    //   417	431	624	finally
    //   444	453	624	finally
    //   472	483	624	finally
    //   487	496	624	finally
    //   508	516	624	finally
    //   520	527	624	finally
    //   531	537	624	finally
    //   586	594	624	finally
    //   609	617	624	finally
    //   621	624	624	finally
    //   629	661	624	finally
    //   673	679	624	finally
    //   679	703	624	finally
    //   703	706	624	finally
    //   707	709	624	finally
    //   711	747	624	finally
    //   751	776	624	finally
    //   781	808	624	finally
    //   808	811	624	finally
    //   813	849	624	finally
    //   853	878	624	finally
    //   881	908	624	finally
    //   908	911	624	finally
    //   68	75	628	java/lang/Exception
    //   88	93	628	java/lang/Exception
    //   97	102	628	java/lang/Exception
    //   106	113	628	java/lang/Exception
    //   117	124	628	java/lang/Exception
    //   128	135	628	java/lang/Exception
    //   145	166	628	java/lang/Exception
    //   170	177	628	java/lang/Exception
    //   186	200	628	java/lang/Exception
    //   204	213	628	java/lang/Exception
    //   217	227	628	java/lang/Exception
    //   231	237	628	java/lang/Exception
    //   241	251	628	java/lang/Exception
    //   266	273	628	java/lang/Exception
    //   277	290	628	java/lang/Exception
    //   303	308	628	java/lang/Exception
    //   312	317	628	java/lang/Exception
    //   321	328	628	java/lang/Exception
    //   332	339	628	java/lang/Exception
    //   343	350	628	java/lang/Exception
    //   360	381	628	java/lang/Exception
    //   390	404	628	java/lang/Exception
    //   417	431	628	java/lang/Exception
    //   444	453	628	java/lang/Exception
    //   472	483	628	java/lang/Exception
    //   487	496	628	java/lang/Exception
    //   508	516	628	java/lang/Exception
    //   520	527	628	java/lang/Exception
    //   531	537	628	java/lang/Exception
    //   586	594	628	java/lang/Exception
    //   609	617	628	java/lang/Exception
    //   621	624	628	java/lang/Exception
    //   68	75	706	java/lang/ClassNotFoundException
    //   88	93	706	java/lang/ClassNotFoundException
    //   97	102	706	java/lang/ClassNotFoundException
    //   106	113	706	java/lang/ClassNotFoundException
    //   117	124	706	java/lang/ClassNotFoundException
    //   128	135	706	java/lang/ClassNotFoundException
    //   145	166	706	java/lang/ClassNotFoundException
    //   170	177	706	java/lang/ClassNotFoundException
    //   186	200	706	java/lang/ClassNotFoundException
    //   204	213	706	java/lang/ClassNotFoundException
    //   217	227	706	java/lang/ClassNotFoundException
    //   231	237	706	java/lang/ClassNotFoundException
    //   241	251	706	java/lang/ClassNotFoundException
    //   266	273	706	java/lang/ClassNotFoundException
    //   277	290	706	java/lang/ClassNotFoundException
    //   303	308	706	java/lang/ClassNotFoundException
    //   312	317	706	java/lang/ClassNotFoundException
    //   321	328	706	java/lang/ClassNotFoundException
    //   332	339	706	java/lang/ClassNotFoundException
    //   343	350	706	java/lang/ClassNotFoundException
    //   360	381	706	java/lang/ClassNotFoundException
    //   390	404	706	java/lang/ClassNotFoundException
    //   417	431	706	java/lang/ClassNotFoundException
    //   444	453	706	java/lang/ClassNotFoundException
    //   472	483	706	java/lang/ClassNotFoundException
    //   487	496	706	java/lang/ClassNotFoundException
    //   508	516	706	java/lang/ClassNotFoundException
    //   520	527	706	java/lang/ClassNotFoundException
    //   531	537	706	java/lang/ClassNotFoundException
    //   586	594	706	java/lang/ClassNotFoundException
    //   609	617	706	java/lang/ClassNotFoundException
    //   621	624	706	java/lang/ClassNotFoundException
    //   68	75	709	java/lang/ClassCastException
    //   88	93	709	java/lang/ClassCastException
    //   97	102	709	java/lang/ClassCastException
    //   106	113	709	java/lang/ClassCastException
    //   117	124	709	java/lang/ClassCastException
    //   128	135	709	java/lang/ClassCastException
    //   145	166	709	java/lang/ClassCastException
    //   170	177	709	java/lang/ClassCastException
    //   186	200	709	java/lang/ClassCastException
    //   204	213	709	java/lang/ClassCastException
    //   217	227	709	java/lang/ClassCastException
    //   231	237	709	java/lang/ClassCastException
    //   241	251	709	java/lang/ClassCastException
    //   266	273	709	java/lang/ClassCastException
    //   277	290	709	java/lang/ClassCastException
    //   303	308	709	java/lang/ClassCastException
    //   312	317	709	java/lang/ClassCastException
    //   321	328	709	java/lang/ClassCastException
    //   332	339	709	java/lang/ClassCastException
    //   343	350	709	java/lang/ClassCastException
    //   360	381	709	java/lang/ClassCastException
    //   390	404	709	java/lang/ClassCastException
    //   417	431	709	java/lang/ClassCastException
    //   444	453	709	java/lang/ClassCastException
    //   472	483	709	java/lang/ClassCastException
    //   487	496	709	java/lang/ClassCastException
    //   508	516	709	java/lang/ClassCastException
    //   520	527	709	java/lang/ClassCastException
    //   531	537	709	java/lang/ClassCastException
    //   586	594	709	java/lang/ClassCastException
    //   609	617	709	java/lang/ClassCastException
    //   621	624	709	java/lang/ClassCastException
    //   68	75	811	java/lang/NoSuchMethodException
    //   88	93	811	java/lang/NoSuchMethodException
    //   97	102	811	java/lang/NoSuchMethodException
    //   106	113	811	java/lang/NoSuchMethodException
    //   117	124	811	java/lang/NoSuchMethodException
    //   128	135	811	java/lang/NoSuchMethodException
    //   145	166	811	java/lang/NoSuchMethodException
    //   170	177	811	java/lang/NoSuchMethodException
    //   186	200	811	java/lang/NoSuchMethodException
    //   204	213	811	java/lang/NoSuchMethodException
    //   217	227	811	java/lang/NoSuchMethodException
    //   231	237	811	java/lang/NoSuchMethodException
    //   241	251	811	java/lang/NoSuchMethodException
    //   266	273	811	java/lang/NoSuchMethodException
    //   277	290	811	java/lang/NoSuchMethodException
    //   303	308	811	java/lang/NoSuchMethodException
    //   312	317	811	java/lang/NoSuchMethodException
    //   321	328	811	java/lang/NoSuchMethodException
    //   332	339	811	java/lang/NoSuchMethodException
    //   343	350	811	java/lang/NoSuchMethodException
    //   360	381	811	java/lang/NoSuchMethodException
    //   390	404	811	java/lang/NoSuchMethodException
    //   417	431	811	java/lang/NoSuchMethodException
    //   444	453	811	java/lang/NoSuchMethodException
    //   472	483	811	java/lang/NoSuchMethodException
    //   487	496	811	java/lang/NoSuchMethodException
    //   508	516	811	java/lang/NoSuchMethodException
    //   520	527	811	java/lang/NoSuchMethodException
    //   531	537	811	java/lang/NoSuchMethodException
    //   586	594	811	java/lang/NoSuchMethodException
    //   609	617	811	java/lang/NoSuchMethodException
    //   621	624	811	java/lang/NoSuchMethodException
  }
  
  public final View createView(String paramString1, String paramString2, AttributeSet paramAttributeSet)
    throws ClassNotFoundException, InflateException
  {
    Context localContext1 = (Context)this.mConstructorArgs[0];
    Context localContext2 = localContext1;
    if (localContext1 == null) {
      localContext2 = this.mContext;
    }
    return createView(localContext2, paramString1, paramString2, paramAttributeSet);
  }
  
  @UnsupportedAppUsage
  View createViewFromTag(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet, boolean paramBoolean)
  {
    String str = paramString;
    if (paramString.equals("view")) {
      str = paramAttributeSet.getAttributeValue(null, "class");
    }
    paramString = paramContext;
    Object localObject;
    if (!paramBoolean)
    {
      localObject = paramContext.obtainStyledAttributes(paramAttributeSet, ATTRS_THEME);
      int i = ((TypedArray)localObject).getResourceId(0, 0);
      paramString = paramContext;
      if (i != 0) {
        paramString = new ContextThemeWrapper(paramContext, i);
      }
      ((TypedArray)localObject).recycle();
    }
    try
    {
      localObject = tryCreateView(paramView, str, paramString, paramAttributeSet);
      paramContext = (Context)localObject;
      if (localObject == null)
      {
        paramContext = this.mConstructorArgs[0];
        this.mConstructorArgs[0] = paramString;
        try
        {
          if (-1 == str.indexOf('.')) {
            paramView = onCreateView(paramString, paramView, str, paramAttributeSet);
          } else {
            paramView = createView(paramString, str, null, paramAttributeSet);
          }
          this.mConstructorArgs[0] = paramContext;
          paramContext = paramView;
        }
        finally
        {
          this.mConstructorArgs[0] = paramContext;
        }
      }
      return paramContext;
    }
    catch (Exception paramView)
    {
      paramContext = new StringBuilder();
      paramContext.append(getParserStateDescription(paramString, paramAttributeSet));
      paramContext.append(": Error inflating class ");
      paramContext.append(str);
      paramView = new InflateException(paramContext.toString(), paramView);
      paramView.setStackTrace(EMPTY_STACK_TRACE);
      throw paramView;
    }
    catch (ClassNotFoundException paramContext)
    {
      paramView = new StringBuilder();
      paramView.append(getParserStateDescription(paramString, paramAttributeSet));
      paramView.append(": Error inflating class ");
      paramView.append(str);
      paramView = new InflateException(paramView.toString(), paramContext);
      paramView.setStackTrace(EMPTY_STACK_TRACE);
      throw paramView;
    }
    catch (InflateException paramView)
    {
      throw paramView;
    }
  }
  
  public Context getContext()
  {
    return this.mContext;
  }
  
  public final Factory getFactory()
  {
    return this.mFactory;
  }
  
  public final Factory2 getFactory2()
  {
    return this.mFactory2;
  }
  
  public Filter getFilter()
  {
    return this.mFilter;
  }
  
  public View inflate(int paramInt, ViewGroup paramViewGroup)
  {
    boolean bool;
    if (paramViewGroup != null) {
      bool = true;
    } else {
      bool = false;
    }
    return inflate(paramInt, paramViewGroup, bool);
  }
  
  public View inflate(int paramInt, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    Object localObject = getContext().getResources();
    paramInt = LayoutInflaterMap.getResourceId(getContext(), paramInt);
    View localView = tryInflatePrecompiled(paramInt, (Resources)localObject, paramViewGroup, paramBoolean);
    if (localView != null) {
      return localView;
    }
    localObject = ((Resources)localObject).getLayout(paramInt);
    try
    {
      paramViewGroup = inflate((XmlPullParser)localObject, paramViewGroup, paramBoolean);
      return paramViewGroup;
    }
    finally
    {
      ((XmlResourceParser)localObject).close();
    }
  }
  
  public View inflate(XmlPullParser paramXmlPullParser, ViewGroup paramViewGroup)
  {
    boolean bool;
    if (paramViewGroup != null) {
      bool = true;
    } else {
      bool = false;
    }
    return inflate(paramXmlPullParser, paramViewGroup, bool);
  }
  
  /* Error */
  public View inflate(XmlPullParser paramXmlPullParser, ViewGroup paramViewGroup, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   4: astore 4
    //   6: aload 4
    //   8: monitorenter
    //   9: ldc2_w 427
    //   12: ldc_w 618
    //   15: invokestatic 436	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   18: aload_0
    //   19: getfield 129	android/view/LayoutInflater:mContext	Landroid/content/Context;
    //   22: astore 5
    //   24: aload_1
    //   25: invokestatic 351	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   28: astore 6
    //   30: aload_0
    //   31: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   34: iconst_0
    //   35: aaload
    //   36: checkcast 102	android/content/Context
    //   39: astore 7
    //   41: aload_0
    //   42: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   45: iconst_0
    //   46: aload 5
    //   48: aastore
    //   49: aload_2
    //   50: astore 8
    //   52: aload_0
    //   53: aload_1
    //   54: invokespecial 472	android/view/LayoutInflater:advanceToRootNode	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   57: aload_1
    //   58: invokeinterface 619 1 0
    //   63: astore 9
    //   65: ldc 48
    //   67: aload 9
    //   69: invokevirtual 361	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   72: istore 10
    //   74: iload 10
    //   76: ifeq +38 -> 114
    //   79: aload_2
    //   80: ifnull +21 -> 101
    //   83: iload_3
    //   84: ifeq +17 -> 101
    //   87: aload_0
    //   88: aload_1
    //   89: aload_2
    //   90: aload 5
    //   92: aload 6
    //   94: iconst_0
    //   95: invokevirtual 365	android/view/LayoutInflater:rInflate	(Lorg/xmlpull/v1/XmlPullParser;Landroid/view/View;Landroid/content/Context;Landroid/util/AttributeSet;Z)V
    //   98: goto +101 -> 199
    //   101: new 149	android/view/InflateException
    //   104: astore_1
    //   105: aload_1
    //   106: ldc_w 621
    //   109: invokespecial 177	android/view/InflateException:<init>	(Ljava/lang/String;)V
    //   112: aload_1
    //   113: athrow
    //   114: aload_0
    //   115: aload_2
    //   116: aload 9
    //   118: aload 5
    //   120: aload 6
    //   122: invokespecial 623	android/view/LayoutInflater:createViewFromTag	(Landroid/view/View;Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;
    //   125: astore 11
    //   127: aconst_null
    //   128: astore 9
    //   130: aload_2
    //   131: ifnull +30 -> 161
    //   134: aload_2
    //   135: aload 6
    //   137: invokevirtual 377	android/view/ViewGroup:generateLayoutParams	(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    //   140: astore 12
    //   142: aload 12
    //   144: astore 9
    //   146: iload_3
    //   147: ifne +14 -> 161
    //   150: aload 11
    //   152: aload 12
    //   154: invokevirtual 383	android/view/View:setLayoutParams	(Landroid/view/ViewGroup$LayoutParams;)V
    //   157: aload 12
    //   159: astore 9
    //   161: aload_0
    //   162: aload_1
    //   163: aload 11
    //   165: aload 6
    //   167: iconst_1
    //   168: invokevirtual 387	android/view/LayoutInflater:rInflateChildren	(Lorg/xmlpull/v1/XmlPullParser;Landroid/view/View;Landroid/util/AttributeSet;Z)V
    //   171: aload_2
    //   172: ifnull +15 -> 187
    //   175: iload_3
    //   176: ifeq +11 -> 187
    //   179: aload_2
    //   180: aload 11
    //   182: aload 9
    //   184: invokevirtual 475	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   187: aload_2
    //   188: ifnull +7 -> 195
    //   191: iload_3
    //   192: ifne +7 -> 199
    //   195: aload 11
    //   197: astore 8
    //   199: aload_0
    //   200: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   203: iconst_0
    //   204: aload 7
    //   206: aastore
    //   207: aload_0
    //   208: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   211: iconst_1
    //   212: aconst_null
    //   213: aastore
    //   214: ldc2_w 427
    //   217: invokestatic 479	android/os/Trace:traceEnd	(J)V
    //   220: aload 4
    //   222: monitorexit
    //   223: aload 8
    //   225: areturn
    //   226: astore_1
    //   227: goto +24 -> 251
    //   230: astore_1
    //   231: goto +86 -> 317
    //   234: astore_1
    //   235: goto +105 -> 340
    //   238: astore_1
    //   239: goto +12 -> 251
    //   242: astore_1
    //   243: goto +74 -> 317
    //   246: astore_1
    //   247: goto +93 -> 340
    //   250: astore_1
    //   251: new 149	android/view/InflateException
    //   254: astore_2
    //   255: new 161	java/lang/StringBuilder
    //   258: astore 8
    //   260: aload 8
    //   262: invokespecial 162	java/lang/StringBuilder:<init>	()V
    //   265: aload 8
    //   267: aload 5
    //   269: aload 6
    //   271: invokestatic 194	android/view/LayoutInflater:getParserStateDescription	(Landroid/content/Context;Landroid/util/AttributeSet;)Ljava/lang/String;
    //   274: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: pop
    //   278: aload 8
    //   280: ldc_w 625
    //   283: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: pop
    //   287: aload 8
    //   289: aload_1
    //   290: invokevirtual 628	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   293: invokevirtual 169	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   296: pop
    //   297: aload_2
    //   298: aload 8
    //   300: invokevirtual 174	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   303: aload_1
    //   304: invokespecial 572	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   307: aload_2
    //   308: getstatic 100	android/view/LayoutInflater:EMPTY_STACK_TRACE	[Ljava/lang/StackTraceElement;
    //   311: invokevirtual 576	android/view/InflateException:setStackTrace	([Ljava/lang/StackTraceElement;)V
    //   314: aload_2
    //   315: athrow
    //   316: astore_1
    //   317: new 149	android/view/InflateException
    //   320: astore_2
    //   321: aload_2
    //   322: aload_1
    //   323: invokevirtual 629	org/xmlpull/v1/XmlPullParserException:getMessage	()Ljava/lang/String;
    //   326: aload_1
    //   327: invokespecial 572	android/view/InflateException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   330: aload_2
    //   331: getstatic 100	android/view/LayoutInflater:EMPTY_STACK_TRACE	[Ljava/lang/StackTraceElement;
    //   334: invokevirtual 576	android/view/InflateException:setStackTrace	([Ljava/lang/StackTraceElement;)V
    //   337: aload_2
    //   338: athrow
    //   339: astore_1
    //   340: aload_0
    //   341: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   344: iconst_0
    //   345: aload 7
    //   347: aastore
    //   348: aload_0
    //   349: getfield 127	android/view/LayoutInflater:mConstructorArgs	[Ljava/lang/Object;
    //   352: iconst_1
    //   353: aconst_null
    //   354: aastore
    //   355: ldc2_w 427
    //   358: invokestatic 479	android/os/Trace:traceEnd	(J)V
    //   361: aload_1
    //   362: athrow
    //   363: astore_1
    //   364: aload 4
    //   366: monitorexit
    //   367: aload_1
    //   368: athrow
    //   369: astore_1
    //   370: goto -6 -> 364
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	373	0	this	LayoutInflater
    //   0	373	1	paramXmlPullParser	XmlPullParser
    //   0	373	2	paramViewGroup	ViewGroup
    //   0	373	3	paramBoolean	boolean
    //   4	361	4	arrayOfObject	Object[]
    //   22	246	5	localContext1	Context
    //   28	242	6	localAttributeSet	AttributeSet
    //   39	307	7	localContext2	Context
    //   50	249	8	localObject1	Object
    //   63	120	9	localObject2	Object
    //   72	3	10	bool	boolean
    //   125	71	11	localView	View
    //   140	18	12	localLayoutParams	ViewGroup.LayoutParams
    // Exception table:
    //   from	to	target	type
    //   161	171	226	java/lang/Exception
    //   179	187	226	java/lang/Exception
    //   161	171	230	org/xmlpull/v1/XmlPullParserException
    //   179	187	230	org/xmlpull/v1/XmlPullParserException
    //   87	98	234	finally
    //   101	114	234	finally
    //   114	127	234	finally
    //   134	142	234	finally
    //   150	157	234	finally
    //   87	98	238	java/lang/Exception
    //   101	114	238	java/lang/Exception
    //   114	127	238	java/lang/Exception
    //   134	142	238	java/lang/Exception
    //   150	157	238	java/lang/Exception
    //   87	98	242	org/xmlpull/v1/XmlPullParserException
    //   101	114	242	org/xmlpull/v1/XmlPullParserException
    //   114	127	242	org/xmlpull/v1/XmlPullParserException
    //   134	142	242	org/xmlpull/v1/XmlPullParserException
    //   150	157	242	org/xmlpull/v1/XmlPullParserException
    //   52	74	246	finally
    //   52	74	250	java/lang/Exception
    //   52	74	316	org/xmlpull/v1/XmlPullParserException
    //   161	171	339	finally
    //   179	187	339	finally
    //   251	314	339	finally
    //   314	316	339	finally
    //   317	337	339	finally
    //   337	339	339	finally
    //   9	49	363	finally
    //   199	220	369	finally
    //   220	223	369	finally
    //   340	363	369	finally
    //   364	367	369	finally
  }
  
  public View onCreateView(Context paramContext, View paramView, String paramString, AttributeSet paramAttributeSet)
    throws ClassNotFoundException
  {
    return onCreateView(paramView, paramString, paramAttributeSet);
  }
  
  protected View onCreateView(View paramView, String paramString, AttributeSet paramAttributeSet)
    throws ClassNotFoundException
  {
    return onCreateView(paramString, paramAttributeSet);
  }
  
  protected View onCreateView(String paramString, AttributeSet paramAttributeSet)
    throws ClassNotFoundException
  {
    return createView(paramString, "android.view.", paramAttributeSet);
  }
  
  void rInflate(XmlPullParser paramXmlPullParser, View paramView, Context paramContext, AttributeSet paramAttributeSet, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    int i = paramXmlPullParser.getDepth();
    int j = 0;
    for (;;)
    {
      int k = paramXmlPullParser.next();
      if (((k == 3) && (paramXmlPullParser.getDepth() <= i)) || (k == 1)) {
        break label212;
      }
      if (k == 2)
      {
        Object localObject = paramXmlPullParser.getName();
        if ("requestFocus".equals(localObject))
        {
          j = 1;
          consumeChildElements(paramXmlPullParser);
        }
        else if ("tag".equals(localObject))
        {
          parseViewTag(paramXmlPullParser, paramView, paramAttributeSet);
        }
        else if ("include".equals(localObject))
        {
          if (paramXmlPullParser.getDepth() != 0) {
            parseInclude(paramXmlPullParser, paramContext, paramView, paramAttributeSet);
          } else {
            throw new InflateException("<include /> cannot be the root element");
          }
        }
        else
        {
          if ("merge".equals(localObject)) {
            break;
          }
          localObject = createViewFromTag(paramView, (String)localObject, paramContext, paramAttributeSet);
          ViewGroup localViewGroup = (ViewGroup)paramView;
          ViewGroup.LayoutParams localLayoutParams = localViewGroup.generateLayoutParams(paramAttributeSet);
          rInflateChildren(paramXmlPullParser, (View)localObject, paramAttributeSet, true);
          localViewGroup.addView((View)localObject, localLayoutParams);
        }
      }
    }
    throw new InflateException("<merge /> must be the root element");
    label212:
    if (j != 0) {
      paramView.restoreDefaultFocus();
    }
    if (paramBoolean) {
      paramView.onFinishInflate();
    }
  }
  
  final void rInflateChildren(XmlPullParser paramXmlPullParser, View paramView, AttributeSet paramAttributeSet, boolean paramBoolean)
    throws XmlPullParserException, IOException
  {
    rInflate(paramXmlPullParser, paramView, paramView.getContext(), paramAttributeSet, paramBoolean);
  }
  
  public void setFactory(Factory paramFactory)
  {
    if (!this.mFactorySet)
    {
      if (paramFactory != null)
      {
        this.mFactorySet = true;
        Factory localFactory = this.mFactory;
        if (localFactory == null) {
          this.mFactory = paramFactory;
        } else {
          this.mFactory = new FactoryMerger(paramFactory, null, localFactory, this.mFactory2);
        }
        return;
      }
      throw new NullPointerException("Given factory can not be null");
    }
    throw new IllegalStateException("A factory has already been set on this LayoutInflater");
  }
  
  public void setFactory2(Factory2 paramFactory2)
  {
    if (!this.mFactorySet)
    {
      if (paramFactory2 != null)
      {
        this.mFactorySet = true;
        Factory localFactory = this.mFactory;
        if (localFactory == null)
        {
          this.mFactory2 = paramFactory2;
          this.mFactory = paramFactory2;
        }
        else
        {
          paramFactory2 = new FactoryMerger(paramFactory2, paramFactory2, localFactory, this.mFactory2);
          this.mFactory2 = paramFactory2;
          this.mFactory = paramFactory2;
        }
        return;
      }
      throw new NullPointerException("Given factory can not be null");
    }
    throw new IllegalStateException("A factory has already been set on this LayoutInflater");
  }
  
  public void setFilter(Filter paramFilter)
  {
    this.mFilter = paramFilter;
    if (paramFilter != null) {
      this.mFilterMap = new HashMap();
    }
  }
  
  public void setPrecompiledLayoutsEnabledForTesting(boolean paramBoolean)
  {
    initPrecompiledViews(paramBoolean);
  }
  
  @UnsupportedAppUsage
  public void setPrivateFactory(Factory2 paramFactory2)
  {
    Factory2 localFactory2 = this.mPrivateFactory;
    if (localFactory2 == null) {
      this.mPrivateFactory = paramFactory2;
    } else {
      this.mPrivateFactory = new FactoryMerger(paramFactory2, paramFactory2, localFactory2, localFactory2);
    }
  }
  
  @UnsupportedAppUsage(trackingBug=122360734L)
  public final View tryCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
  {
    if (paramString.equals("blink")) {
      return new BlinkLayout(paramContext, paramAttributeSet);
    }
    Object localObject1 = this.mFactory2;
    if (localObject1 != null)
    {
      localObject1 = ((Factory2)localObject1).onCreateView(paramView, paramString, paramContext, paramAttributeSet);
    }
    else
    {
      localObject1 = this.mFactory;
      if (localObject1 != null) {
        localObject1 = ((Factory)localObject1).onCreateView(paramString, paramContext, paramAttributeSet);
      } else {
        localObject1 = null;
      }
    }
    Object localObject2 = localObject1;
    if (localObject1 == null)
    {
      Factory2 localFactory2 = this.mPrivateFactory;
      localObject2 = localObject1;
      if (localFactory2 != null) {
        localObject2 = localFactory2.onCreateView(paramView, paramString, paramContext, paramAttributeSet);
      }
    }
    return (View)localObject2;
  }
  
  private static class BlinkLayout
    extends FrameLayout
  {
    private static final int BLINK_DELAY = 500;
    private static final int MESSAGE_BLINK = 66;
    private boolean mBlink;
    private boolean mBlinkState;
    private final Handler mHandler = new Handler(new Handler.Callback()
    {
      public boolean handleMessage(Message paramAnonymousMessage)
      {
        if (paramAnonymousMessage.what == 66)
        {
          if (LayoutInflater.BlinkLayout.this.mBlink)
          {
            paramAnonymousMessage = LayoutInflater.BlinkLayout.this;
            LayoutInflater.BlinkLayout.access$102(paramAnonymousMessage, paramAnonymousMessage.mBlinkState ^ true);
            LayoutInflater.BlinkLayout.this.makeBlink();
          }
          LayoutInflater.BlinkLayout.this.invalidate();
          return true;
        }
        return false;
      }
    });
    
    public BlinkLayout(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    private void makeBlink()
    {
      Message localMessage = this.mHandler.obtainMessage(66);
      this.mHandler.sendMessageDelayed(localMessage, 500L);
    }
    
    protected void dispatchDraw(Canvas paramCanvas)
    {
      if (this.mBlinkState) {
        super.dispatchDraw(paramCanvas);
      }
    }
    
    protected void onAttachedToWindow()
    {
      super.onAttachedToWindow();
      this.mBlink = true;
      this.mBlinkState = true;
      makeBlink();
    }
    
    protected void onDetachedFromWindow()
    {
      super.onDetachedFromWindow();
      this.mBlink = false;
      this.mBlinkState = true;
      this.mHandler.removeMessages(66);
    }
  }
  
  public static abstract interface Factory
  {
    public abstract View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet);
  }
  
  public static abstract interface Factory2
    extends LayoutInflater.Factory
  {
    public abstract View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet);
  }
  
  private static class FactoryMerger
    implements LayoutInflater.Factory2
  {
    private final LayoutInflater.Factory mF1;
    private final LayoutInflater.Factory2 mF12;
    private final LayoutInflater.Factory mF2;
    private final LayoutInflater.Factory2 mF22;
    
    FactoryMerger(LayoutInflater.Factory paramFactory1, LayoutInflater.Factory2 paramFactory21, LayoutInflater.Factory paramFactory2, LayoutInflater.Factory2 paramFactory22)
    {
      this.mF1 = paramFactory1;
      this.mF2 = paramFactory2;
      this.mF12 = paramFactory21;
      this.mF22 = paramFactory22;
    }
    
    public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet)
    {
      Object localObject = this.mF12;
      if (localObject != null) {
        localObject = ((LayoutInflater.Factory2)localObject).onCreateView(paramView, paramString, paramContext, paramAttributeSet);
      } else {
        localObject = this.mF1.onCreateView(paramString, paramContext, paramAttributeSet);
      }
      if (localObject != null) {
        return (View)localObject;
      }
      localObject = this.mF22;
      if (localObject != null) {
        paramView = ((LayoutInflater.Factory2)localObject).onCreateView(paramView, paramString, paramContext, paramAttributeSet);
      } else {
        paramView = this.mF2.onCreateView(paramString, paramContext, paramAttributeSet);
      }
      return paramView;
    }
    
    public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet)
    {
      View localView = this.mF1.onCreateView(paramString, paramContext, paramAttributeSet);
      if (localView != null) {
        return localView;
      }
      return this.mF2.onCreateView(paramString, paramContext, paramAttributeSet);
    }
  }
  
  public static abstract interface Filter
  {
    public abstract boolean onLoadClass(Class paramClass);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/LayoutInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */