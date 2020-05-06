package android.view.textservice;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.PrintWriterPrinter;
import java.io.PrintWriter;
import java.util.ArrayList;

public final class SpellCheckerInfo
  implements Parcelable
{
  public static final Parcelable.Creator<SpellCheckerInfo> CREATOR = new Parcelable.Creator()
  {
    public SpellCheckerInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpellCheckerInfo(paramAnonymousParcel);
    }
    
    public SpellCheckerInfo[] newArray(int paramAnonymousInt)
    {
      return new SpellCheckerInfo[paramAnonymousInt];
    }
  };
  private static final String TAG = SpellCheckerInfo.class.getSimpleName();
  private final String mId;
  private final int mLabel;
  private final ResolveInfo mService;
  private final String mSettingsActivityName;
  private final ArrayList<SpellCheckerSubtype> mSubtypes = new ArrayList();
  
  /* Error */
  public SpellCheckerInfo(android.content.Context paramContext, ResolveInfo paramResolveInfo)
    throws org.xmlpull.v1.XmlPullParserException, java.io.IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 46	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: new 48	java/util/ArrayList
    //   8: dup
    //   9: invokespecial 49	java/util/ArrayList:<init>	()V
    //   12: putfield 51	android/view/textservice/SpellCheckerInfo:mSubtypes	Ljava/util/ArrayList;
    //   15: aload_0
    //   16: aload_2
    //   17: putfield 53	android/view/textservice/SpellCheckerInfo:mService	Landroid/content/pm/ResolveInfo;
    //   20: aload_2
    //   21: getfield 59	android/content/pm/ResolveInfo:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   24: astore_3
    //   25: aload_0
    //   26: new 61	android/content/ComponentName
    //   29: dup
    //   30: aload_3
    //   31: getfield 66	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   34: aload_3
    //   35: getfield 69	android/content/pm/ServiceInfo:name	Ljava/lang/String;
    //   38: invokespecial 72	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   41: invokevirtual 75	android/content/ComponentName:flattenToShortString	()Ljava/lang/String;
    //   44: putfield 77	android/view/textservice/SpellCheckerInfo:mId	Ljava/lang/String;
    //   47: aload_1
    //   48: invokevirtual 83	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   51: astore 4
    //   53: aconst_null
    //   54: astore_2
    //   55: aconst_null
    //   56: astore_1
    //   57: aload_3
    //   58: aload 4
    //   60: ldc 85
    //   62: invokevirtual 89	android/content/pm/ServiceInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   65: astore 5
    //   67: aload 5
    //   69: ifnull +409 -> 478
    //   72: aload 5
    //   74: astore_1
    //   75: aload 5
    //   77: astore_2
    //   78: aload 4
    //   80: aload_3
    //   81: getfield 93	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   84: invokevirtual 99	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   87: astore 4
    //   89: aload 5
    //   91: astore_1
    //   92: aload 5
    //   94: astore_2
    //   95: aload 5
    //   97: invokestatic 105	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   100: astore 6
    //   102: aload 5
    //   104: astore_1
    //   105: aload 5
    //   107: astore_2
    //   108: aload 5
    //   110: invokeinterface 111 1 0
    //   115: istore 7
    //   117: iload 7
    //   119: iconst_1
    //   120: if_icmpeq +12 -> 132
    //   123: iload 7
    //   125: iconst_2
    //   126: if_icmpeq +6 -> 132
    //   129: goto -27 -> 102
    //   132: aload 5
    //   134: astore_1
    //   135: aload 5
    //   137: astore_2
    //   138: ldc 113
    //   140: aload 5
    //   142: invokeinterface 116 1 0
    //   147: invokevirtual 122	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   150: ifeq +295 -> 445
    //   153: aload 5
    //   155: astore_1
    //   156: aload 5
    //   158: astore_2
    //   159: aload 4
    //   161: aload 6
    //   163: getstatic 128	com/android/internal/R$styleable:SpellChecker	[I
    //   166: invokevirtual 134	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   169: astore 8
    //   171: aload 5
    //   173: astore_1
    //   174: aload 5
    //   176: astore_2
    //   177: aload 8
    //   179: iconst_0
    //   180: iconst_0
    //   181: invokevirtual 140	android/content/res/TypedArray:getResourceId	(II)I
    //   184: istore 9
    //   186: aload 5
    //   188: astore_1
    //   189: aload 5
    //   191: astore_2
    //   192: aload 8
    //   194: iconst_1
    //   195: invokevirtual 144	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   198: astore 10
    //   200: aload 5
    //   202: astore_1
    //   203: aload 5
    //   205: astore_2
    //   206: aload 8
    //   208: invokevirtual 147	android/content/res/TypedArray:recycle	()V
    //   211: aload 5
    //   213: astore_1
    //   214: aload 5
    //   216: astore_2
    //   217: aload 5
    //   219: invokeinterface 150 1 0
    //   224: istore 11
    //   226: aload 5
    //   228: astore_1
    //   229: aload 5
    //   231: astore_2
    //   232: aload 5
    //   234: invokeinterface 111 1 0
    //   239: istore 7
    //   241: iload 7
    //   243: iconst_3
    //   244: if_icmpne +21 -> 265
    //   247: aload 5
    //   249: astore_1
    //   250: aload 5
    //   252: astore_2
    //   253: aload 5
    //   255: invokeinterface 150 1 0
    //   260: iload 11
    //   262: if_icmple +163 -> 425
    //   265: iload 7
    //   267: iconst_1
    //   268: if_icmpeq +157 -> 425
    //   271: iload 7
    //   273: iconst_2
    //   274: if_icmpne +148 -> 422
    //   277: aload 5
    //   279: astore_1
    //   280: aload 5
    //   282: astore_2
    //   283: ldc -104
    //   285: aload 5
    //   287: invokeinterface 116 1 0
    //   292: invokevirtual 122	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   295: ifeq +94 -> 389
    //   298: aload 5
    //   300: astore_1
    //   301: aload 5
    //   303: astore_2
    //   304: aload 4
    //   306: aload 6
    //   308: getstatic 155	com/android/internal/R$styleable:SpellChecker_Subtype	[I
    //   311: invokevirtual 134	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   314: astore 8
    //   316: aload 5
    //   318: astore_1
    //   319: aload 5
    //   321: astore_2
    //   322: new 157	android/view/textservice/SpellCheckerSubtype
    //   325: astore 12
    //   327: aload 5
    //   329: astore_1
    //   330: aload 5
    //   332: astore_2
    //   333: aload 12
    //   335: aload 8
    //   337: iconst_0
    //   338: iconst_0
    //   339: invokevirtual 140	android/content/res/TypedArray:getResourceId	(II)I
    //   342: aload 8
    //   344: iconst_1
    //   345: invokevirtual 144	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   348: aload 8
    //   350: iconst_4
    //   351: invokevirtual 144	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   354: aload 8
    //   356: iconst_2
    //   357: invokevirtual 144	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   360: aload 8
    //   362: iconst_3
    //   363: iconst_0
    //   364: invokevirtual 160	android/content/res/TypedArray:getInt	(II)I
    //   367: invokespecial 163	android/view/textservice/SpellCheckerSubtype:<init>	(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V
    //   370: aload 5
    //   372: astore_1
    //   373: aload 5
    //   375: astore_2
    //   376: aload_0
    //   377: getfield 51	android/view/textservice/SpellCheckerInfo:mSubtypes	Ljava/util/ArrayList;
    //   380: aload 12
    //   382: invokevirtual 166	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   385: pop
    //   386: goto +36 -> 422
    //   389: aload 5
    //   391: astore_1
    //   392: aload 5
    //   394: astore_2
    //   395: new 41	org/xmlpull/v1/XmlPullParserException
    //   398: astore 4
    //   400: aload 5
    //   402: astore_1
    //   403: aload 5
    //   405: astore_2
    //   406: aload 4
    //   408: ldc -88
    //   410: invokespecial 171	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   413: aload 5
    //   415: astore_1
    //   416: aload 5
    //   418: astore_2
    //   419: aload 4
    //   421: athrow
    //   422: goto -196 -> 226
    //   425: aload 5
    //   427: invokeinterface 174 1 0
    //   432: aload_0
    //   433: iload 9
    //   435: putfield 176	android/view/textservice/SpellCheckerInfo:mLabel	I
    //   438: aload_0
    //   439: aload 10
    //   441: putfield 178	android/view/textservice/SpellCheckerInfo:mSettingsActivityName	Ljava/lang/String;
    //   444: return
    //   445: aload 5
    //   447: astore_1
    //   448: aload 5
    //   450: astore_2
    //   451: new 41	org/xmlpull/v1/XmlPullParserException
    //   454: astore 4
    //   456: aload 5
    //   458: astore_1
    //   459: aload 5
    //   461: astore_2
    //   462: aload 4
    //   464: ldc -76
    //   466: invokespecial 171	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   469: aload 5
    //   471: astore_1
    //   472: aload 5
    //   474: astore_2
    //   475: aload 4
    //   477: athrow
    //   478: aload 5
    //   480: astore_1
    //   481: aload 5
    //   483: astore_2
    //   484: new 41	org/xmlpull/v1/XmlPullParserException
    //   487: astore 4
    //   489: aload 5
    //   491: astore_1
    //   492: aload 5
    //   494: astore_2
    //   495: aload 4
    //   497: ldc -74
    //   499: invokespecial 171	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   502: aload 5
    //   504: astore_1
    //   505: aload 5
    //   507: astore_2
    //   508: aload 4
    //   510: athrow
    //   511: astore_2
    //   512: goto +119 -> 631
    //   515: astore 5
    //   517: aload_2
    //   518: astore_1
    //   519: getstatic 32	android/view/textservice/SpellCheckerInfo:TAG	Ljava/lang/String;
    //   522: astore 6
    //   524: aload_2
    //   525: astore_1
    //   526: new 184	java/lang/StringBuilder
    //   529: astore 4
    //   531: aload_2
    //   532: astore_1
    //   533: aload 4
    //   535: invokespecial 185	java/lang/StringBuilder:<init>	()V
    //   538: aload_2
    //   539: astore_1
    //   540: aload 4
    //   542: ldc -69
    //   544: invokevirtual 191	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   547: pop
    //   548: aload_2
    //   549: astore_1
    //   550: aload 4
    //   552: aload 5
    //   554: invokevirtual 194	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   557: pop
    //   558: aload_2
    //   559: astore_1
    //   560: aload 6
    //   562: aload 4
    //   564: invokevirtual 197	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   567: invokestatic 203	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   570: pop
    //   571: aload_2
    //   572: astore_1
    //   573: new 41	org/xmlpull/v1/XmlPullParserException
    //   576: astore 4
    //   578: aload_2
    //   579: astore_1
    //   580: new 184	java/lang/StringBuilder
    //   583: astore 5
    //   585: aload_2
    //   586: astore_1
    //   587: aload 5
    //   589: invokespecial 185	java/lang/StringBuilder:<init>	()V
    //   592: aload_2
    //   593: astore_1
    //   594: aload 5
    //   596: ldc -51
    //   598: invokevirtual 191	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   601: pop
    //   602: aload_2
    //   603: astore_1
    //   604: aload 5
    //   606: aload_3
    //   607: getfield 66	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   610: invokevirtual 191	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   613: pop
    //   614: aload_2
    //   615: astore_1
    //   616: aload 4
    //   618: aload 5
    //   620: invokevirtual 197	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   623: invokespecial 171	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   626: aload_2
    //   627: astore_1
    //   628: aload 4
    //   630: athrow
    //   631: aload_1
    //   632: ifnull +9 -> 641
    //   635: aload_1
    //   636: invokeinterface 174 1 0
    //   641: aload_2
    //   642: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	643	0	this	SpellCheckerInfo
    //   0	643	1	paramContext	android.content.Context
    //   0	643	2	paramResolveInfo	ResolveInfo
    //   24	583	3	localServiceInfo	ServiceInfo
    //   51	578	4	localObject1	Object
    //   65	441	5	localXmlResourceParser	android.content.res.XmlResourceParser
    //   515	38	5	localException	Exception
    //   583	36	5	localStringBuilder	StringBuilder
    //   100	461	6	localObject2	Object
    //   115	160	7	i	int
    //   169	192	8	localTypedArray	android.content.res.TypedArray
    //   184	250	9	j	int
    //   198	242	10	str	String
    //   224	39	11	k	int
    //   325	56	12	localSpellCheckerSubtype	SpellCheckerSubtype
    // Exception table:
    //   from	to	target	type
    //   57	67	511	finally
    //   78	89	511	finally
    //   95	102	511	finally
    //   108	117	511	finally
    //   138	153	511	finally
    //   159	171	511	finally
    //   177	186	511	finally
    //   192	200	511	finally
    //   206	211	511	finally
    //   217	226	511	finally
    //   232	241	511	finally
    //   253	265	511	finally
    //   283	298	511	finally
    //   304	316	511	finally
    //   322	327	511	finally
    //   333	370	511	finally
    //   376	386	511	finally
    //   395	400	511	finally
    //   406	413	511	finally
    //   419	422	511	finally
    //   451	456	511	finally
    //   462	469	511	finally
    //   475	478	511	finally
    //   484	489	511	finally
    //   495	502	511	finally
    //   508	511	511	finally
    //   519	524	511	finally
    //   526	531	511	finally
    //   533	538	511	finally
    //   540	548	511	finally
    //   550	558	511	finally
    //   560	571	511	finally
    //   573	578	511	finally
    //   580	585	511	finally
    //   587	592	511	finally
    //   594	602	511	finally
    //   604	614	511	finally
    //   616	626	511	finally
    //   628	631	511	finally
    //   57	67	515	java/lang/Exception
    //   78	89	515	java/lang/Exception
    //   95	102	515	java/lang/Exception
    //   108	117	515	java/lang/Exception
    //   138	153	515	java/lang/Exception
    //   159	171	515	java/lang/Exception
    //   177	186	515	java/lang/Exception
    //   192	200	515	java/lang/Exception
    //   206	211	515	java/lang/Exception
    //   217	226	515	java/lang/Exception
    //   232	241	515	java/lang/Exception
    //   253	265	515	java/lang/Exception
    //   283	298	515	java/lang/Exception
    //   304	316	515	java/lang/Exception
    //   322	327	515	java/lang/Exception
    //   333	370	515	java/lang/Exception
    //   376	386	515	java/lang/Exception
    //   395	400	515	java/lang/Exception
    //   406	413	515	java/lang/Exception
    //   419	422	515	java/lang/Exception
    //   451	456	515	java/lang/Exception
    //   462	469	515	java/lang/Exception
    //   475	478	515	java/lang/Exception
    //   484	489	515	java/lang/Exception
    //   495	502	515	java/lang/Exception
    //   508	511	515	java/lang/Exception
  }
  
  public SpellCheckerInfo(Parcel paramParcel)
  {
    this.mLabel = paramParcel.readInt();
    this.mId = paramParcel.readString();
    this.mSettingsActivityName = paramParcel.readString();
    this.mService = ((ResolveInfo)ResolveInfo.CREATOR.createFromParcel(paramParcel));
    paramParcel.readTypedList(this.mSubtypes, SpellCheckerSubtype.CREATOR);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(PrintWriter paramPrintWriter, String paramString)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("mId=");
    ((StringBuilder)localObject).append(this.mId);
    paramPrintWriter.println(((StringBuilder)localObject).toString());
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("mSettingsActivityName=");
    ((StringBuilder)localObject).append(this.mSettingsActivityName);
    paramPrintWriter.println(((StringBuilder)localObject).toString());
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("Service:");
    paramPrintWriter.println(((StringBuilder)localObject).toString());
    localObject = this.mService;
    PrintWriterPrinter localPrintWriterPrinter = new PrintWriterPrinter(paramPrintWriter);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("  ");
    ((ResolveInfo)localObject).dump(localPrintWriterPrinter, localStringBuilder.toString());
    int i = getSubtypeCount();
    for (int j = 0; j < i; j++)
    {
      localObject = getSubtypeAt(j);
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("  Subtype #");
      localStringBuilder.append(j);
      localStringBuilder.append(":");
      paramPrintWriter.println(localStringBuilder.toString());
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("    locale=");
      localStringBuilder.append(((SpellCheckerSubtype)localObject).getLocale());
      localStringBuilder.append(" languageTag=");
      localStringBuilder.append(((SpellCheckerSubtype)localObject).getLanguageTag());
      paramPrintWriter.println(localStringBuilder.toString());
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString);
      localStringBuilder.append("    extraValue=");
      localStringBuilder.append(((SpellCheckerSubtype)localObject).getExtraValue());
      paramPrintWriter.println(localStringBuilder.toString());
    }
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public String getPackageName()
  {
    return this.mService.serviceInfo.packageName;
  }
  
  public ServiceInfo getServiceInfo()
  {
    return this.mService.serviceInfo;
  }
  
  public String getSettingsActivity()
  {
    return this.mSettingsActivityName;
  }
  
  public SpellCheckerSubtype getSubtypeAt(int paramInt)
  {
    return (SpellCheckerSubtype)this.mSubtypes.get(paramInt);
  }
  
  public int getSubtypeCount()
  {
    return this.mSubtypes.size();
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return this.mService.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    if ((this.mLabel != 0) && (paramPackageManager != null)) {
      return paramPackageManager.getText(getPackageName(), this.mLabel, this.mService.serviceInfo.applicationInfo);
    }
    return "";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mLabel);
    paramParcel.writeString(this.mId);
    paramParcel.writeString(this.mSettingsActivityName);
    this.mService.writeToParcel(paramParcel, paramInt);
    paramParcel.writeTypedList(this.mSubtypes);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/SpellCheckerInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */