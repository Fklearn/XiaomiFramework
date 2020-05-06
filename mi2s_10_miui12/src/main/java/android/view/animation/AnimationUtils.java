package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AnimationUtils
{
  private static final int SEQUENTIALLY = 1;
  private static final int TOGETHER = 0;
  private static ThreadLocal<AnimationState> sAnimationState = new ThreadLocal()
  {
    protected AnimationUtils.AnimationState initialValue()
    {
      return new AnimationUtils.AnimationState(null);
    }
  };
  
  private static Animation createAnimationFromXml(Context paramContext, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    return createAnimationFromXml(paramContext, paramXmlPullParser, null, Xml.asAttributeSet(paramXmlPullParser));
  }
  
  private static Animation createAnimationFromXml(Context paramContext, XmlPullParser paramXmlPullParser, AnimationSet paramAnimationSet, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        break label263;
      }
      if (j == 2)
      {
        localObject = paramXmlPullParser.getName();
        if (((String)localObject).equals("set"))
        {
          localObject = new AnimationSet(paramContext, paramAttributeSet);
          createAnimationFromXml(paramContext, paramXmlPullParser, (AnimationSet)localObject, paramAttributeSet);
        }
        else if (((String)localObject).equals("alpha"))
        {
          localObject = new AlphaAnimation(paramContext, paramAttributeSet);
        }
        else if (((String)localObject).equals("scale"))
        {
          localObject = new ScaleAnimation(paramContext, paramAttributeSet);
        }
        else if (((String)localObject).equals("rotate"))
        {
          localObject = new RotateAnimation(paramContext, paramAttributeSet);
        }
        else if (((String)localObject).equals("translate"))
        {
          localObject = new TranslateAnimation(paramContext, paramAttributeSet);
        }
        else
        {
          if (!((String)localObject).equals("cliprect")) {
            break;
          }
          localObject = new ClipRectAnimation(paramContext, paramAttributeSet);
        }
        if (paramAnimationSet != null) {
          paramAnimationSet.addAnimation((Animation)localObject);
        }
      }
    }
    paramContext = new StringBuilder();
    paramContext.append("Unknown animation name: ");
    paramContext.append(paramXmlPullParser.getName());
    throw new RuntimeException(paramContext.toString());
    label263:
    return (Animation)localObject;
  }
  
  private static Interpolator createInterpolatorFromXml(Resources paramResources, Resources.Theme paramTheme, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        return localObject;
      }
      if (j == 2)
      {
        AttributeSet localAttributeSet = Xml.asAttributeSet(paramXmlPullParser);
        localObject = paramXmlPullParser.getName();
        if (((String)localObject).equals("linearInterpolator"))
        {
          localObject = new LinearInterpolator();
        }
        else if (((String)localObject).equals("accelerateInterpolator"))
        {
          localObject = new AccelerateInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else if (((String)localObject).equals("decelerateInterpolator"))
        {
          localObject = new DecelerateInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else if (((String)localObject).equals("accelerateDecelerateInterpolator"))
        {
          localObject = new AccelerateDecelerateInterpolator();
        }
        else if (((String)localObject).equals("cycleInterpolator"))
        {
          localObject = new CycleInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else if (((String)localObject).equals("anticipateInterpolator"))
        {
          localObject = new AnticipateInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else if (((String)localObject).equals("overshootInterpolator"))
        {
          localObject = new OvershootInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else if (((String)localObject).equals("anticipateOvershootInterpolator"))
        {
          localObject = new AnticipateOvershootInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else if (((String)localObject).equals("bounceInterpolator"))
        {
          localObject = new BounceInterpolator();
        }
        else if (((String)localObject).equals("pathInterpolator"))
        {
          localObject = new PathInterpolator(paramResources, paramTheme, localAttributeSet);
        }
        else
        {
          if (!((String)localObject).equals("springInterpolator")) {
            break;
          }
          localObject = new SpringInterpolator(paramResources, paramTheme, localAttributeSet);
        }
      }
    }
    paramResources = new StringBuilder();
    paramResources.append("Unknown interpolator name: ");
    paramResources.append(paramXmlPullParser.getName());
    throw new RuntimeException(paramResources.toString());
    return (Interpolator)localObject;
  }
  
  private static LayoutAnimationController createLayoutAnimationFromXml(Context paramContext, XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    return createLayoutAnimationFromXml(paramContext, paramXmlPullParser, Xml.asAttributeSet(paramXmlPullParser));
  }
  
  private static LayoutAnimationController createLayoutAnimationFromXml(Context paramContext, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    Object localObject = null;
    int i = paramXmlPullParser.getDepth();
    for (;;)
    {
      int j = paramXmlPullParser.next();
      if (((j == 3) && (paramXmlPullParser.getDepth() <= i)) || (j == 1)) {
        return localObject;
      }
      if (j == 2)
      {
        localObject = paramXmlPullParser.getName();
        if ("layoutAnimation".equals(localObject))
        {
          localObject = new LayoutAnimationController(paramContext, paramAttributeSet);
        }
        else
        {
          if (!"gridLayoutAnimation".equals(localObject)) {
            break;
          }
          localObject = new GridLayoutAnimationController(paramContext, paramAttributeSet);
        }
      }
    }
    paramContext = new StringBuilder();
    paramContext.append("Unknown layout animation name: ");
    paramContext.append((String)localObject);
    throw new RuntimeException(paramContext.toString());
    return (LayoutAnimationController)localObject;
  }
  
  public static long currentAnimationTimeMillis()
  {
    AnimationState localAnimationState = (AnimationState)sAnimationState.get();
    if (localAnimationState.animationClockLocked) {
      return Math.max(localAnimationState.currentVsyncTimeMillis, localAnimationState.lastReportedTimeMillis);
    }
    localAnimationState.lastReportedTimeMillis = SystemClock.uptimeMillis();
    return localAnimationState.lastReportedTimeMillis;
  }
  
  /* Error */
  public static Animation loadAnimation(Context paramContext, int paramInt)
    throws android.content.res.Resources.NotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aload_0
    //   8: invokevirtual 232	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   11: iload_1
    //   12: invokevirtual 238	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   15: astore 5
    //   17: aload 5
    //   19: astore 4
    //   21: aload 5
    //   23: astore_2
    //   24: aload 5
    //   26: astore_3
    //   27: aload_0
    //   28: aload 5
    //   30: invokestatic 240	android/view/animation/AnimationUtils:createAnimationFromXml	(Landroid/content/Context;Lorg/xmlpull/v1/XmlPullParser;)Landroid/view/animation/Animation;
    //   33: astore_0
    //   34: aload 5
    //   36: ifnull +10 -> 46
    //   39: aload 5
    //   41: invokeinterface 245 1 0
    //   46: aload_0
    //   47: areturn
    //   48: astore_0
    //   49: goto +150 -> 199
    //   52: astore_0
    //   53: aload_2
    //   54: astore 4
    //   56: new 226	android/content/res/Resources$NotFoundException
    //   59: astore 5
    //   61: aload_2
    //   62: astore 4
    //   64: new 99	java/lang/StringBuilder
    //   67: astore_3
    //   68: aload_2
    //   69: astore 4
    //   71: aload_3
    //   72: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   75: aload_2
    //   76: astore 4
    //   78: aload_3
    //   79: ldc -9
    //   81: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: pop
    //   85: aload_2
    //   86: astore 4
    //   88: aload_3
    //   89: iload_1
    //   90: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   93: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: aload_2
    //   98: astore 4
    //   100: aload 5
    //   102: aload_3
    //   103: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   106: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   109: aload_2
    //   110: astore 4
    //   112: aload 5
    //   114: aload_0
    //   115: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   118: pop
    //   119: aload_2
    //   120: astore 4
    //   122: aload 5
    //   124: athrow
    //   125: astore_0
    //   126: aload_3
    //   127: astore 4
    //   129: new 226	android/content/res/Resources$NotFoundException
    //   132: astore_2
    //   133: aload_3
    //   134: astore 4
    //   136: new 99	java/lang/StringBuilder
    //   139: astore 5
    //   141: aload_3
    //   142: astore 4
    //   144: aload 5
    //   146: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   149: aload_3
    //   150: astore 4
    //   152: aload 5
    //   154: ldc -9
    //   156: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: pop
    //   160: aload_3
    //   161: astore 4
    //   163: aload 5
    //   165: iload_1
    //   166: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   169: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: pop
    //   173: aload_3
    //   174: astore 4
    //   176: aload_2
    //   177: aload 5
    //   179: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   185: aload_3
    //   186: astore 4
    //   188: aload_2
    //   189: aload_0
    //   190: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   193: pop
    //   194: aload_3
    //   195: astore 4
    //   197: aload_2
    //   198: athrow
    //   199: aload 4
    //   201: ifnull +10 -> 211
    //   204: aload 4
    //   206: invokeinterface 245 1 0
    //   211: aload_0
    //   212: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	paramContext	Context
    //   0	213	1	paramInt	int
    //   1	197	2	localObject1	Object
    //   3	192	3	localObject2	Object
    //   5	200	4	localObject3	Object
    //   15	163	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	48	finally
    //   27	34	48	finally
    //   56	61	48	finally
    //   64	68	48	finally
    //   71	75	48	finally
    //   78	85	48	finally
    //   88	97	48	finally
    //   100	109	48	finally
    //   112	119	48	finally
    //   122	125	48	finally
    //   129	133	48	finally
    //   136	141	48	finally
    //   144	149	48	finally
    //   152	160	48	finally
    //   163	173	48	finally
    //   176	185	48	finally
    //   188	194	48	finally
    //   197	199	48	finally
    //   7	17	52	java/io/IOException
    //   27	34	52	java/io/IOException
    //   7	17	125	org/xmlpull/v1/XmlPullParserException
    //   27	34	125	org/xmlpull/v1/XmlPullParserException
  }
  
  /* Error */
  public static Interpolator loadInterpolator(Context paramContext, int paramInt)
    throws android.content.res.Resources.NotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aload_0
    //   8: invokevirtual 232	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   11: iload_1
    //   12: invokevirtual 238	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   15: astore 5
    //   17: aload 5
    //   19: astore 4
    //   21: aload 5
    //   23: astore_2
    //   24: aload 5
    //   26: astore_3
    //   27: aload_0
    //   28: invokevirtual 232	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   31: aload_0
    //   32: invokevirtual 264	android/content/Context:getTheme	()Landroid/content/res/Resources$Theme;
    //   35: aload 5
    //   37: invokestatic 266	android/view/animation/AnimationUtils:createInterpolatorFromXml	(Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;Lorg/xmlpull/v1/XmlPullParser;)Landroid/view/animation/Interpolator;
    //   40: astore_0
    //   41: aload 5
    //   43: ifnull +10 -> 53
    //   46: aload 5
    //   48: invokeinterface 245 1 0
    //   53: aload_0
    //   54: areturn
    //   55: astore_0
    //   56: goto +148 -> 204
    //   59: astore 5
    //   61: aload_2
    //   62: astore 4
    //   64: new 226	android/content/res/Resources$NotFoundException
    //   67: astore_0
    //   68: aload_2
    //   69: astore 4
    //   71: new 99	java/lang/StringBuilder
    //   74: astore_3
    //   75: aload_2
    //   76: astore 4
    //   78: aload_3
    //   79: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   82: aload_2
    //   83: astore 4
    //   85: aload_3
    //   86: ldc -9
    //   88: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: pop
    //   92: aload_2
    //   93: astore 4
    //   95: aload_3
    //   96: iload_1
    //   97: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   100: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: pop
    //   104: aload_2
    //   105: astore 4
    //   107: aload_0
    //   108: aload_3
    //   109: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   112: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   115: aload_2
    //   116: astore 4
    //   118: aload_0
    //   119: aload 5
    //   121: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   124: pop
    //   125: aload_2
    //   126: astore 4
    //   128: aload_0
    //   129: athrow
    //   130: astore_0
    //   131: aload_3
    //   132: astore 4
    //   134: new 226	android/content/res/Resources$NotFoundException
    //   137: astore_2
    //   138: aload_3
    //   139: astore 4
    //   141: new 99	java/lang/StringBuilder
    //   144: astore 5
    //   146: aload_3
    //   147: astore 4
    //   149: aload 5
    //   151: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   154: aload_3
    //   155: astore 4
    //   157: aload 5
    //   159: ldc -9
    //   161: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: pop
    //   165: aload_3
    //   166: astore 4
    //   168: aload 5
    //   170: iload_1
    //   171: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   174: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   177: pop
    //   178: aload_3
    //   179: astore 4
    //   181: aload_2
    //   182: aload 5
    //   184: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   190: aload_3
    //   191: astore 4
    //   193: aload_2
    //   194: aload_0
    //   195: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   198: pop
    //   199: aload_3
    //   200: astore 4
    //   202: aload_2
    //   203: athrow
    //   204: aload 4
    //   206: ifnull +10 -> 216
    //   209: aload 4
    //   211: invokeinterface 245 1 0
    //   216: aload_0
    //   217: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	218	0	paramContext	Context
    //   0	218	1	paramInt	int
    //   1	202	2	localObject1	Object
    //   3	197	3	localObject2	Object
    //   5	205	4	localObject3	Object
    //   15	32	5	localXmlResourceParser	android.content.res.XmlResourceParser
    //   59	61	5	localIOException	IOException
    //   144	39	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   7	17	55	finally
    //   27	41	55	finally
    //   64	68	55	finally
    //   71	75	55	finally
    //   78	82	55	finally
    //   85	92	55	finally
    //   95	104	55	finally
    //   107	115	55	finally
    //   118	125	55	finally
    //   128	130	55	finally
    //   134	138	55	finally
    //   141	146	55	finally
    //   149	154	55	finally
    //   157	165	55	finally
    //   168	178	55	finally
    //   181	190	55	finally
    //   193	199	55	finally
    //   202	204	55	finally
    //   7	17	59	java/io/IOException
    //   27	41	59	java/io/IOException
    //   7	17	130	org/xmlpull/v1/XmlPullParserException
    //   27	41	130	org/xmlpull/v1/XmlPullParserException
  }
  
  /* Error */
  public static Interpolator loadInterpolator(Resources paramResources, Resources.Theme paramTheme, int paramInt)
    throws android.content.res.Resources.NotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 5
    //   8: aload_0
    //   9: iload_2
    //   10: invokevirtual 238	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   13: astore 6
    //   15: aload 6
    //   17: astore 5
    //   19: aload 6
    //   21: astore_3
    //   22: aload 6
    //   24: astore 4
    //   26: aload_0
    //   27: aload_1
    //   28: aload 6
    //   30: invokestatic 266	android/view/animation/AnimationUtils:createInterpolatorFromXml	(Landroid/content/res/Resources;Landroid/content/res/Resources$Theme;Lorg/xmlpull/v1/XmlPullParser;)Landroid/view/animation/Interpolator;
    //   33: astore_0
    //   34: aload 6
    //   36: ifnull +10 -> 46
    //   39: aload 6
    //   41: invokeinterface 245 1 0
    //   46: aload_0
    //   47: areturn
    //   48: astore_0
    //   49: goto +151 -> 200
    //   52: astore 4
    //   54: aload_3
    //   55: astore 5
    //   57: new 226	android/content/res/Resources$NotFoundException
    //   60: astore_0
    //   61: aload_3
    //   62: astore 5
    //   64: new 99	java/lang/StringBuilder
    //   67: astore_1
    //   68: aload_3
    //   69: astore 5
    //   71: aload_1
    //   72: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   75: aload_3
    //   76: astore 5
    //   78: aload_1
    //   79: ldc -9
    //   81: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: pop
    //   85: aload_3
    //   86: astore 5
    //   88: aload_1
    //   89: iload_2
    //   90: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   93: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: aload_3
    //   98: astore 5
    //   100: aload_0
    //   101: aload_1
    //   102: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   105: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   108: aload_3
    //   109: astore 5
    //   111: aload_0
    //   112: aload 4
    //   114: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   117: pop
    //   118: aload_3
    //   119: astore 5
    //   121: aload_0
    //   122: athrow
    //   123: astore_3
    //   124: aload 4
    //   126: astore 5
    //   128: new 226	android/content/res/Resources$NotFoundException
    //   131: astore_1
    //   132: aload 4
    //   134: astore 5
    //   136: new 99	java/lang/StringBuilder
    //   139: astore_0
    //   140: aload 4
    //   142: astore 5
    //   144: aload_0
    //   145: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   148: aload 4
    //   150: astore 5
    //   152: aload_0
    //   153: ldc -9
    //   155: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: pop
    //   159: aload 4
    //   161: astore 5
    //   163: aload_0
    //   164: iload_2
    //   165: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   168: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: pop
    //   172: aload 4
    //   174: astore 5
    //   176: aload_1
    //   177: aload_0
    //   178: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   184: aload 4
    //   186: astore 5
    //   188: aload_1
    //   189: aload_3
    //   190: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   193: pop
    //   194: aload 4
    //   196: astore 5
    //   198: aload_1
    //   199: athrow
    //   200: aload 5
    //   202: ifnull +10 -> 212
    //   205: aload 5
    //   207: invokeinterface 245 1 0
    //   212: aload_0
    //   213: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	214	0	paramResources	Resources
    //   0	214	1	paramTheme	Resources.Theme
    //   0	214	2	paramInt	int
    //   1	118	3	localObject1	Object
    //   123	67	3	localXmlPullParserException	XmlPullParserException
    //   3	22	4	localObject2	Object
    //   52	143	4	localIOException	IOException
    //   6	200	5	localObject3	Object
    //   13	27	6	localXmlResourceParser	android.content.res.XmlResourceParser
    // Exception table:
    //   from	to	target	type
    //   8	15	48	finally
    //   26	34	48	finally
    //   57	61	48	finally
    //   64	68	48	finally
    //   71	75	48	finally
    //   78	85	48	finally
    //   88	97	48	finally
    //   100	108	48	finally
    //   111	118	48	finally
    //   121	123	48	finally
    //   128	132	48	finally
    //   136	140	48	finally
    //   144	148	48	finally
    //   152	159	48	finally
    //   163	172	48	finally
    //   176	184	48	finally
    //   188	194	48	finally
    //   198	200	48	finally
    //   8	15	52	java/io/IOException
    //   26	34	52	java/io/IOException
    //   8	15	123	org/xmlpull/v1/XmlPullParserException
    //   26	34	123	org/xmlpull/v1/XmlPullParserException
  }
  
  /* Error */
  public static LayoutAnimationController loadLayoutAnimation(Context paramContext, int paramInt)
    throws android.content.res.Resources.NotFoundException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aload_0
    //   8: invokevirtual 232	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   11: iload_1
    //   12: invokevirtual 238	android/content/res/Resources:getAnimation	(I)Landroid/content/res/XmlResourceParser;
    //   15: astore 5
    //   17: aload 5
    //   19: astore 4
    //   21: aload 5
    //   23: astore_2
    //   24: aload 5
    //   26: astore_3
    //   27: aload_0
    //   28: aload 5
    //   30: invokestatic 271	android/view/animation/AnimationUtils:createLayoutAnimationFromXml	(Landroid/content/Context;Lorg/xmlpull/v1/XmlPullParser;)Landroid/view/animation/LayoutAnimationController;
    //   33: astore_0
    //   34: aload 5
    //   36: ifnull +10 -> 46
    //   39: aload 5
    //   41: invokeinterface 245 1 0
    //   46: aload_0
    //   47: areturn
    //   48: astore_0
    //   49: goto +150 -> 199
    //   52: astore_0
    //   53: aload_2
    //   54: astore 4
    //   56: new 226	android/content/res/Resources$NotFoundException
    //   59: astore 5
    //   61: aload_2
    //   62: astore 4
    //   64: new 99	java/lang/StringBuilder
    //   67: astore_3
    //   68: aload_2
    //   69: astore 4
    //   71: aload_3
    //   72: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   75: aload_2
    //   76: astore 4
    //   78: aload_3
    //   79: ldc -9
    //   81: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: pop
    //   85: aload_2
    //   86: astore 4
    //   88: aload_3
    //   89: iload_1
    //   90: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   93: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   96: pop
    //   97: aload_2
    //   98: astore 4
    //   100: aload 5
    //   102: aload_3
    //   103: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   106: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   109: aload_2
    //   110: astore 4
    //   112: aload 5
    //   114: aload_0
    //   115: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   118: pop
    //   119: aload_2
    //   120: astore 4
    //   122: aload 5
    //   124: athrow
    //   125: astore_0
    //   126: aload_3
    //   127: astore 4
    //   129: new 226	android/content/res/Resources$NotFoundException
    //   132: astore_2
    //   133: aload_3
    //   134: astore 4
    //   136: new 99	java/lang/StringBuilder
    //   139: astore 5
    //   141: aload_3
    //   142: astore 4
    //   144: aload 5
    //   146: invokespecial 100	java/lang/StringBuilder:<init>	()V
    //   149: aload_3
    //   150: astore 4
    //   152: aload 5
    //   154: ldc -9
    //   156: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   159: pop
    //   160: aload_3
    //   161: astore 4
    //   163: aload 5
    //   165: iload_1
    //   166: invokestatic 253	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   169: invokevirtual 106	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: pop
    //   173: aload_3
    //   174: astore 4
    //   176: aload_2
    //   177: aload 5
    //   179: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokespecial 254	android/content/res/Resources$NotFoundException:<init>	(Ljava/lang/String;)V
    //   185: aload_3
    //   186: astore 4
    //   188: aload_2
    //   189: aload_0
    //   190: invokevirtual 258	android/content/res/Resources$NotFoundException:initCause	(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    //   193: pop
    //   194: aload_3
    //   195: astore 4
    //   197: aload_2
    //   198: athrow
    //   199: aload 4
    //   201: ifnull +10 -> 211
    //   204: aload 4
    //   206: invokeinterface 245 1 0
    //   211: aload_0
    //   212: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	paramContext	Context
    //   0	213	1	paramInt	int
    //   1	197	2	localObject1	Object
    //   3	192	3	localObject2	Object
    //   5	200	4	localObject3	Object
    //   15	163	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   7	17	48	finally
    //   27	34	48	finally
    //   56	61	48	finally
    //   64	68	48	finally
    //   71	75	48	finally
    //   78	85	48	finally
    //   88	97	48	finally
    //   100	109	48	finally
    //   112	119	48	finally
    //   122	125	48	finally
    //   129	133	48	finally
    //   136	141	48	finally
    //   144	149	48	finally
    //   152	160	48	finally
    //   163	173	48	finally
    //   176	185	48	finally
    //   188	194	48	finally
    //   197	199	48	finally
    //   7	17	52	java/io/IOException
    //   27	34	52	java/io/IOException
    //   7	17	125	org/xmlpull/v1/XmlPullParserException
    //   27	34	125	org/xmlpull/v1/XmlPullParserException
  }
  
  public static void lockAnimationClock(long paramLong)
  {
    AnimationState localAnimationState = (AnimationState)sAnimationState.get();
    localAnimationState.animationClockLocked = true;
    localAnimationState.currentVsyncTimeMillis = paramLong;
  }
  
  public static Animation makeInAnimation(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramContext = loadAnimation(paramContext, 17432578);
    } else {
      paramContext = loadAnimation(paramContext, 17432881);
    }
    paramContext.setInterpolator(new DecelerateInterpolator());
    paramContext.setStartTime(currentAnimationTimeMillis());
    return paramContext;
  }
  
  public static Animation makeInChildBottomAnimation(Context paramContext)
  {
    paramContext = loadAnimation(paramContext, 17432878);
    paramContext.setInterpolator(new AccelerateInterpolator());
    paramContext.setStartTime(currentAnimationTimeMillis());
    return paramContext;
  }
  
  public static Animation makeOutAnimation(Context paramContext, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramContext = loadAnimation(paramContext, 17432579);
    } else {
      paramContext = loadAnimation(paramContext, 17432884);
    }
    paramContext.setInterpolator(new AccelerateInterpolator());
    paramContext.setStartTime(currentAnimationTimeMillis());
    return paramContext;
  }
  
  public static void unlockAnimationClock()
  {
    ((AnimationState)sAnimationState.get()).animationClockLocked = false;
  }
  
  private static class AnimationState
  {
    boolean animationClockLocked;
    long currentVsyncTimeMillis;
    long lastReportedTimeMillis;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/animation/AnimationUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */