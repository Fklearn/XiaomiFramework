package android.view.inputmethod;

import android.annotation.UnsupportedAppUsage;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Printer;
import java.io.IOException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public final class InputMethodInfo
  implements Parcelable
{
  public static final Parcelable.Creator<InputMethodInfo> CREATOR = new Parcelable.Creator()
  {
    public InputMethodInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new InputMethodInfo(paramAnonymousParcel);
    }
    
    public InputMethodInfo[] newArray(int paramAnonymousInt)
    {
      return new InputMethodInfo[paramAnonymousInt];
    }
  };
  static final String TAG = "InputMethodInfo";
  private final boolean mForceDefault;
  final String mId;
  private final boolean mIsAuxIme;
  final int mIsDefaultResId;
  final boolean mIsVrOnly;
  final ResolveInfo mService;
  final String mSettingsActivityName;
  @UnsupportedAppUsage
  private final InputMethodSubtypeArray mSubtypes;
  private final boolean mSupportsSwitchingToNextInputMethod;
  
  public InputMethodInfo(Context paramContext, ResolveInfo paramResolveInfo)
    throws XmlPullParserException, IOException
  {
    this(paramContext, paramResolveInfo, null);
  }
  
  /* Error */
  public InputMethodInfo(Context paramContext, ResolveInfo paramResolveInfo, List<InputMethodSubtype> paramList)
    throws XmlPullParserException, IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 53	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: aload_2
    //   6: putfield 55	android/view/inputmethod/InputMethodInfo:mService	Landroid/content/pm/ResolveInfo;
    //   9: aload_2
    //   10: getfield 61	android/content/pm/ResolveInfo:serviceInfo	Landroid/content/pm/ServiceInfo;
    //   13: astore 4
    //   15: aload_0
    //   16: aload_2
    //   17: invokestatic 65	android/view/inputmethod/InputMethodInfo:computeId	(Landroid/content/pm/ResolveInfo;)Ljava/lang/String;
    //   20: putfield 67	android/view/inputmethod/InputMethodInfo:mId	Ljava/lang/String;
    //   23: iconst_1
    //   24: istore 5
    //   26: iconst_1
    //   27: istore 6
    //   29: aload_0
    //   30: iconst_0
    //   31: putfield 69	android/view/inputmethod/InputMethodInfo:mForceDefault	Z
    //   34: aload_1
    //   35: invokevirtual 75	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
    //   38: astore_1
    //   39: aconst_null
    //   40: astore_2
    //   41: aconst_null
    //   42: astore 7
    //   44: new 77	java/util/ArrayList
    //   47: dup
    //   48: invokespecial 78	java/util/ArrayList:<init>	()V
    //   51: astore 8
    //   53: iload 6
    //   55: istore 9
    //   57: aload_1
    //   58: astore 10
    //   60: aload 7
    //   62: astore 10
    //   64: iload 5
    //   66: istore 9
    //   68: aload_1
    //   69: astore 11
    //   71: aload_2
    //   72: astore 11
    //   74: aload 4
    //   76: aload_1
    //   77: ldc 80
    //   79: invokevirtual 86	android/content/pm/ServiceInfo:loadXmlMetaData	(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;
    //   82: astore_2
    //   83: aload_2
    //   84: ifnull +836 -> 920
    //   87: iload 6
    //   89: istore 9
    //   91: aload_1
    //   92: astore 10
    //   94: aload_2
    //   95: astore 10
    //   97: iload 5
    //   99: istore 9
    //   101: aload_1
    //   102: astore 11
    //   104: aload_2
    //   105: astore 11
    //   107: aload_1
    //   108: aload 4
    //   110: getfield 90	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
    //   113: invokevirtual 96	android/content/pm/PackageManager:getResourcesForApplication	(Landroid/content/pm/ApplicationInfo;)Landroid/content/res/Resources;
    //   116: astore 12
    //   118: iload 6
    //   120: istore 9
    //   122: aload_1
    //   123: astore 10
    //   125: aload_2
    //   126: astore 10
    //   128: iload 5
    //   130: istore 9
    //   132: aload_1
    //   133: astore 11
    //   135: aload_2
    //   136: astore 11
    //   138: aload_2
    //   139: invokestatic 102	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   142: astore 13
    //   144: iload 6
    //   146: istore 9
    //   148: aload_1
    //   149: astore 10
    //   151: aload_2
    //   152: astore 10
    //   154: iload 5
    //   156: istore 9
    //   158: aload_1
    //   159: astore 11
    //   161: aload_2
    //   162: astore 11
    //   164: aload_2
    //   165: invokeinterface 108 1 0
    //   170: istore 14
    //   172: iload 14
    //   174: iconst_1
    //   175: if_icmpeq +12 -> 187
    //   178: iload 14
    //   180: iconst_2
    //   181: if_icmpeq +6 -> 187
    //   184: goto -40 -> 144
    //   187: iload 6
    //   189: istore 9
    //   191: aload_1
    //   192: astore 10
    //   194: aload_2
    //   195: astore 10
    //   197: iload 5
    //   199: istore 9
    //   201: aload_1
    //   202: astore 11
    //   204: aload_2
    //   205: astore 11
    //   207: ldc 110
    //   209: aload_2
    //   210: invokeinterface 114 1 0
    //   215: invokevirtual 120	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   218: ifeq +674 -> 892
    //   221: iload 6
    //   223: istore 9
    //   225: aload_1
    //   226: astore 10
    //   228: aload_2
    //   229: astore 10
    //   231: iload 5
    //   233: istore 9
    //   235: aload_1
    //   236: astore 11
    //   238: aload_2
    //   239: astore 11
    //   241: aload 12
    //   243: aload 13
    //   245: getstatic 126	com/android/internal/R$styleable:InputMethod	[I
    //   248: invokevirtual 132	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   251: astore 7
    //   253: iload 6
    //   255: istore 9
    //   257: aload_1
    //   258: astore 10
    //   260: aload_2
    //   261: astore 10
    //   263: iload 5
    //   265: istore 9
    //   267: aload_1
    //   268: astore 11
    //   270: aload_2
    //   271: astore 11
    //   273: aload 7
    //   275: iconst_1
    //   276: invokevirtual 138	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   279: astore 15
    //   281: iconst_1
    //   282: istore 6
    //   284: aload_1
    //   285: astore 10
    //   287: aload_1
    //   288: astore 10
    //   290: iload 6
    //   292: istore 9
    //   294: aload 7
    //   296: iconst_3
    //   297: iconst_0
    //   298: invokevirtual 142	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   301: istore 16
    //   303: aload_1
    //   304: astore 10
    //   306: aload_1
    //   307: astore 10
    //   309: iload 6
    //   311: istore 9
    //   313: aload 7
    //   315: iconst_0
    //   316: iconst_0
    //   317: invokevirtual 146	android/content/res/TypedArray:getResourceId	(II)I
    //   320: istore 17
    //   322: aload_1
    //   323: astore 10
    //   325: aload_1
    //   326: astore 10
    //   328: iload 6
    //   330: istore 9
    //   332: aload 7
    //   334: iconst_2
    //   335: iconst_0
    //   336: invokevirtual 142	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   339: istore 5
    //   341: aload_1
    //   342: astore 10
    //   344: aload_1
    //   345: astore 10
    //   347: iload 6
    //   349: istore 9
    //   351: aload 7
    //   353: invokevirtual 149	android/content/res/TypedArray:recycle	()V
    //   356: aload_1
    //   357: astore 10
    //   359: aload_1
    //   360: astore 10
    //   362: iload 6
    //   364: istore 9
    //   366: aload_2
    //   367: invokeinterface 152 1 0
    //   372: istore 14
    //   374: iload 6
    //   376: istore 9
    //   378: aload_1
    //   379: astore 10
    //   381: aload_2
    //   382: astore 10
    //   384: iload 6
    //   386: istore 9
    //   388: aload_1
    //   389: astore 11
    //   391: aload_2
    //   392: astore 11
    //   394: aload_2
    //   395: invokeinterface 108 1 0
    //   400: istore 18
    //   402: iload 18
    //   404: iconst_3
    //   405: if_icmpne +32 -> 437
    //   408: aload_2
    //   409: invokeinterface 152 1 0
    //   414: istore 19
    //   416: iload 19
    //   418: iload 14
    //   420: if_icmple +6 -> 426
    //   423: goto +14 -> 437
    //   426: goto +281 -> 707
    //   429: astore_1
    //   430: goto +638 -> 1068
    //   433: astore_1
    //   434: goto +541 -> 975
    //   437: iload 18
    //   439: iconst_1
    //   440: if_icmpeq +267 -> 707
    //   443: iload 18
    //   445: iconst_2
    //   446: if_icmpne +258 -> 704
    //   449: aload_1
    //   450: astore 10
    //   452: aload_1
    //   453: astore 10
    //   455: iload 6
    //   457: istore 9
    //   459: ldc -102
    //   461: aload_2
    //   462: invokeinterface 114 1 0
    //   467: invokevirtual 120	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   470: ifeq +210 -> 680
    //   473: aload_1
    //   474: astore 10
    //   476: aload_1
    //   477: astore 10
    //   479: iload 6
    //   481: istore 9
    //   483: aload 12
    //   485: aload 13
    //   487: getstatic 157	com/android/internal/R$styleable:InputMethod_Subtype	[I
    //   490: invokevirtual 132	android/content/res/Resources:obtainAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   493: astore 11
    //   495: aload_1
    //   496: astore 10
    //   498: aload_1
    //   499: astore 10
    //   501: iload 6
    //   503: istore 9
    //   505: new 159	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder
    //   508: astore 20
    //   510: aload_1
    //   511: astore 10
    //   513: aload_1
    //   514: astore 10
    //   516: iload 6
    //   518: istore 9
    //   520: aload 20
    //   522: invokespecial 160	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:<init>	()V
    //   525: iload 6
    //   527: istore 9
    //   529: aload 20
    //   531: aload 11
    //   533: iconst_0
    //   534: iconst_0
    //   535: invokevirtual 146	android/content/res/TypedArray:getResourceId	(II)I
    //   538: invokevirtual 164	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeNameResId	(I)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   541: aload 11
    //   543: iconst_1
    //   544: iconst_0
    //   545: invokevirtual 146	android/content/res/TypedArray:getResourceId	(II)I
    //   548: invokevirtual 167	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeIconResId	(I)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   551: aload 11
    //   553: bipush 9
    //   555: invokevirtual 138	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   558: invokevirtual 171	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setLanguageTag	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   561: aload 11
    //   563: iconst_2
    //   564: invokevirtual 138	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   567: invokevirtual 174	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeLocale	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   570: aload 11
    //   572: iconst_3
    //   573: invokevirtual 138	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   576: invokevirtual 177	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeMode	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   579: aload 11
    //   581: iconst_4
    //   582: invokevirtual 138	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   585: invokevirtual 180	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeExtraValue	(Ljava/lang/String;)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   588: aload 11
    //   590: iconst_5
    //   591: iconst_0
    //   592: invokevirtual 142	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   595: invokevirtual 184	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setIsAuxiliary	(Z)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   598: aload 11
    //   600: bipush 6
    //   602: iconst_0
    //   603: invokevirtual 142	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   606: invokevirtual 187	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setOverridesImplicitlyEnabledSubtype	(Z)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   609: aload 11
    //   611: bipush 7
    //   613: iconst_0
    //   614: invokevirtual 190	android/content/res/TypedArray:getInt	(II)I
    //   617: invokevirtual 193	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setSubtypeId	(I)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   620: aload 11
    //   622: bipush 8
    //   624: iconst_0
    //   625: invokevirtual 142	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   628: invokevirtual 196	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:setIsAsciiCapable	(Z)Landroid/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder;
    //   631: invokevirtual 200	android/view/inputmethod/InputMethodSubtype$InputMethodSubtypeBuilder:build	()Landroid/view/inputmethod/InputMethodSubtype;
    //   634: astore 11
    //   636: iload 6
    //   638: istore 9
    //   640: aload 11
    //   642: invokevirtual 206	android/view/inputmethod/InputMethodSubtype:isAuxiliary	()Z
    //   645: istore 21
    //   647: iload 21
    //   649: ifne +9 -> 658
    //   652: iconst_0
    //   653: istore 6
    //   655: goto +3 -> 658
    //   658: iload 6
    //   660: istore 9
    //   662: aload_2
    //   663: astore 10
    //   665: aload 8
    //   667: aload 11
    //   669: invokevirtual 209	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   672: pop
    //   673: goto -299 -> 374
    //   676: astore_1
    //   677: goto +298 -> 975
    //   680: iload 6
    //   682: istore 9
    //   684: new 40	org/xmlpull/v1/XmlPullParserException
    //   687: astore_1
    //   688: iload 6
    //   690: istore 9
    //   692: aload_1
    //   693: ldc -45
    //   695: invokespecial 214	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   698: iload 6
    //   700: istore 9
    //   702: aload_1
    //   703: athrow
    //   704: goto -330 -> 374
    //   707: aload_2
    //   708: invokeinterface 217 1 0
    //   713: aload 8
    //   715: invokevirtual 220	java/util/ArrayList:size	()I
    //   718: ifne +9 -> 727
    //   721: iconst_0
    //   722: istore 6
    //   724: goto +3 -> 727
    //   727: aload_3
    //   728: ifnull +108 -> 836
    //   731: aload_3
    //   732: invokeinterface 223 1 0
    //   737: istore 19
    //   739: iconst_0
    //   740: istore 14
    //   742: iload 14
    //   744: iload 19
    //   746: if_icmpge +90 -> 836
    //   749: aload_3
    //   750: iload 14
    //   752: invokeinterface 227 2 0
    //   757: checkcast 202	android/view/inputmethod/InputMethodSubtype
    //   760: astore_2
    //   761: aload 8
    //   763: aload_2
    //   764: invokevirtual 230	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
    //   767: ifne +13 -> 780
    //   770: aload 8
    //   772: aload_2
    //   773: invokevirtual 209	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   776: pop
    //   777: goto +53 -> 830
    //   780: new 232	java/lang/StringBuilder
    //   783: dup
    //   784: invokespecial 233	java/lang/StringBuilder:<init>	()V
    //   787: astore_1
    //   788: aload_1
    //   789: ldc -21
    //   791: invokevirtual 239	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   794: pop
    //   795: aload_1
    //   796: aload_2
    //   797: invokevirtual 242	android/view/inputmethod/InputMethodSubtype:getLocale	()Ljava/lang/String;
    //   800: invokevirtual 239	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   803: pop
    //   804: aload_1
    //   805: ldc -12
    //   807: invokevirtual 239	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   810: pop
    //   811: aload_1
    //   812: aload_2
    //   813: invokevirtual 247	android/view/inputmethod/InputMethodSubtype:getMode	()Ljava/lang/String;
    //   816: invokevirtual 239	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   819: pop
    //   820: ldc 15
    //   822: aload_1
    //   823: invokevirtual 250	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   826: invokestatic 256	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   829: pop
    //   830: iinc 14 1
    //   833: goto -91 -> 742
    //   836: aload_0
    //   837: new 258	android/view/inputmethod/InputMethodSubtypeArray
    //   840: dup
    //   841: aload 8
    //   843: invokespecial 261	android/view/inputmethod/InputMethodSubtypeArray:<init>	(Ljava/util/List;)V
    //   846: putfield 263	android/view/inputmethod/InputMethodInfo:mSubtypes	Landroid/view/inputmethod/InputMethodSubtypeArray;
    //   849: aload_0
    //   850: aload 15
    //   852: putfield 265	android/view/inputmethod/InputMethodInfo:mSettingsActivityName	Ljava/lang/String;
    //   855: aload_0
    //   856: iload 17
    //   858: putfield 267	android/view/inputmethod/InputMethodInfo:mIsDefaultResId	I
    //   861: aload_0
    //   862: iload 6
    //   864: putfield 269	android/view/inputmethod/InputMethodInfo:mIsAuxIme	Z
    //   867: aload_0
    //   868: iload 5
    //   870: putfield 271	android/view/inputmethod/InputMethodInfo:mSupportsSwitchingToNextInputMethod	Z
    //   873: aload_0
    //   874: iload 16
    //   876: putfield 273	android/view/inputmethod/InputMethodInfo:mIsVrOnly	Z
    //   879: return
    //   880: astore_1
    //   881: goto +187 -> 1068
    //   884: astore_1
    //   885: iload 9
    //   887: istore 6
    //   889: goto +86 -> 975
    //   892: iconst_1
    //   893: istore 6
    //   895: iload 6
    //   897: istore 9
    //   899: new 40	org/xmlpull/v1/XmlPullParserException
    //   902: astore_1
    //   903: iload 6
    //   905: istore 9
    //   907: aload_1
    //   908: ldc_w 275
    //   911: invokespecial 214	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   914: iload 6
    //   916: istore 9
    //   918: aload_1
    //   919: athrow
    //   920: iconst_1
    //   921: istore 6
    //   923: iload 6
    //   925: istore 9
    //   927: new 40	org/xmlpull/v1/XmlPullParserException
    //   930: astore_1
    //   931: iload 6
    //   933: istore 9
    //   935: aload_1
    //   936: ldc_w 277
    //   939: invokespecial 214	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   942: iload 6
    //   944: istore 9
    //   946: aload_1
    //   947: athrow
    //   948: astore_1
    //   949: goto +119 -> 1068
    //   952: astore_1
    //   953: iload 9
    //   955: istore 6
    //   957: goto +18 -> 975
    //   960: astore_1
    //   961: aload 10
    //   963: astore_2
    //   964: goto +104 -> 1068
    //   967: astore_1
    //   968: aload 11
    //   970: astore_2
    //   971: iload 9
    //   973: istore 6
    //   975: iload 6
    //   977: istore 9
    //   979: aload_2
    //   980: astore 10
    //   982: new 40	org/xmlpull/v1/XmlPullParserException
    //   985: astore_1
    //   986: iload 6
    //   988: istore 9
    //   990: aload_2
    //   991: astore 10
    //   993: new 232	java/lang/StringBuilder
    //   996: astore_3
    //   997: iload 6
    //   999: istore 9
    //   1001: aload_2
    //   1002: astore 10
    //   1004: aload_3
    //   1005: invokespecial 233	java/lang/StringBuilder:<init>	()V
    //   1008: iload 6
    //   1010: istore 9
    //   1012: aload_2
    //   1013: astore 10
    //   1015: aload_3
    //   1016: ldc_w 279
    //   1019: invokevirtual 239	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1022: pop
    //   1023: iload 6
    //   1025: istore 9
    //   1027: aload_2
    //   1028: astore 10
    //   1030: aload_3
    //   1031: aload 4
    //   1033: getfield 282	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
    //   1036: invokevirtual 239	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1039: pop
    //   1040: iload 6
    //   1042: istore 9
    //   1044: aload_2
    //   1045: astore 10
    //   1047: aload_1
    //   1048: aload_3
    //   1049: invokevirtual 250	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1052: invokespecial 214	org/xmlpull/v1/XmlPullParserException:<init>	(Ljava/lang/String;)V
    //   1055: iload 6
    //   1057: istore 9
    //   1059: aload_2
    //   1060: astore 10
    //   1062: aload_1
    //   1063: athrow
    //   1064: astore_1
    //   1065: aload 10
    //   1067: astore_2
    //   1068: aload_2
    //   1069: ifnull +9 -> 1078
    //   1072: aload_2
    //   1073: invokeinterface 217 1 0
    //   1078: aload_1
    //   1079: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1080	0	this	InputMethodInfo
    //   0	1080	1	paramContext	Context
    //   0	1080	2	paramResolveInfo	ResolveInfo
    //   0	1080	3	paramList	List<InputMethodSubtype>
    //   13	1019	4	localServiceInfo	ServiceInfo
    //   24	845	5	bool1	boolean
    //   27	1029	6	bool2	boolean
    //   42	310	7	localTypedArray	android.content.res.TypedArray
    //   51	791	8	localArrayList	java.util.ArrayList
    //   55	1003	9	bool3	boolean
    //   58	1008	10	localObject1	Object
    //   69	900	11	localObject2	Object
    //   116	368	12	localResources	Resources
    //   142	344	13	localAttributeSet	android.util.AttributeSet
    //   170	661	14	i	int
    //   279	572	15	str	String
    //   301	574	16	bool4	boolean
    //   320	537	17	j	int
    //   400	47	18	k	int
    //   414	333	19	m	int
    //   508	22	20	localInputMethodSubtypeBuilder	InputMethodSubtype.InputMethodSubtypeBuilder
    //   645	3	21	bool5	boolean
    // Exception table:
    //   from	to	target	type
    //   408	416	429	finally
    //   408	416	433	android/content/pm/PackageManager$NameNotFoundException
    //   408	416	433	java/lang/IndexOutOfBoundsException
    //   408	416	433	java/lang/NumberFormatException
    //   665	673	676	android/content/pm/PackageManager$NameNotFoundException
    //   665	673	676	java/lang/IndexOutOfBoundsException
    //   665	673	676	java/lang/NumberFormatException
    //   294	303	880	finally
    //   313	322	880	finally
    //   332	341	880	finally
    //   351	356	880	finally
    //   366	374	880	finally
    //   459	473	880	finally
    //   483	495	880	finally
    //   505	510	880	finally
    //   520	525	880	finally
    //   294	303	884	android/content/pm/PackageManager$NameNotFoundException
    //   294	303	884	java/lang/IndexOutOfBoundsException
    //   294	303	884	java/lang/NumberFormatException
    //   313	322	884	android/content/pm/PackageManager$NameNotFoundException
    //   313	322	884	java/lang/IndexOutOfBoundsException
    //   313	322	884	java/lang/NumberFormatException
    //   332	341	884	android/content/pm/PackageManager$NameNotFoundException
    //   332	341	884	java/lang/IndexOutOfBoundsException
    //   332	341	884	java/lang/NumberFormatException
    //   351	356	884	android/content/pm/PackageManager$NameNotFoundException
    //   351	356	884	java/lang/IndexOutOfBoundsException
    //   351	356	884	java/lang/NumberFormatException
    //   366	374	884	android/content/pm/PackageManager$NameNotFoundException
    //   366	374	884	java/lang/IndexOutOfBoundsException
    //   366	374	884	java/lang/NumberFormatException
    //   459	473	884	android/content/pm/PackageManager$NameNotFoundException
    //   459	473	884	java/lang/IndexOutOfBoundsException
    //   459	473	884	java/lang/NumberFormatException
    //   483	495	884	android/content/pm/PackageManager$NameNotFoundException
    //   483	495	884	java/lang/IndexOutOfBoundsException
    //   483	495	884	java/lang/NumberFormatException
    //   505	510	884	android/content/pm/PackageManager$NameNotFoundException
    //   505	510	884	java/lang/IndexOutOfBoundsException
    //   505	510	884	java/lang/NumberFormatException
    //   520	525	884	android/content/pm/PackageManager$NameNotFoundException
    //   520	525	884	java/lang/IndexOutOfBoundsException
    //   520	525	884	java/lang/NumberFormatException
    //   529	636	948	finally
    //   640	647	948	finally
    //   684	688	948	finally
    //   692	698	948	finally
    //   702	704	948	finally
    //   899	903	948	finally
    //   907	914	948	finally
    //   918	920	948	finally
    //   927	931	948	finally
    //   935	942	948	finally
    //   946	948	948	finally
    //   529	636	952	android/content/pm/PackageManager$NameNotFoundException
    //   529	636	952	java/lang/IndexOutOfBoundsException
    //   529	636	952	java/lang/NumberFormatException
    //   640	647	952	android/content/pm/PackageManager$NameNotFoundException
    //   640	647	952	java/lang/IndexOutOfBoundsException
    //   640	647	952	java/lang/NumberFormatException
    //   684	688	952	android/content/pm/PackageManager$NameNotFoundException
    //   684	688	952	java/lang/IndexOutOfBoundsException
    //   684	688	952	java/lang/NumberFormatException
    //   692	698	952	android/content/pm/PackageManager$NameNotFoundException
    //   692	698	952	java/lang/IndexOutOfBoundsException
    //   692	698	952	java/lang/NumberFormatException
    //   702	704	952	android/content/pm/PackageManager$NameNotFoundException
    //   702	704	952	java/lang/IndexOutOfBoundsException
    //   702	704	952	java/lang/NumberFormatException
    //   899	903	952	android/content/pm/PackageManager$NameNotFoundException
    //   899	903	952	java/lang/IndexOutOfBoundsException
    //   899	903	952	java/lang/NumberFormatException
    //   907	914	952	android/content/pm/PackageManager$NameNotFoundException
    //   907	914	952	java/lang/IndexOutOfBoundsException
    //   907	914	952	java/lang/NumberFormatException
    //   918	920	952	android/content/pm/PackageManager$NameNotFoundException
    //   918	920	952	java/lang/IndexOutOfBoundsException
    //   918	920	952	java/lang/NumberFormatException
    //   927	931	952	android/content/pm/PackageManager$NameNotFoundException
    //   927	931	952	java/lang/IndexOutOfBoundsException
    //   927	931	952	java/lang/NumberFormatException
    //   935	942	952	android/content/pm/PackageManager$NameNotFoundException
    //   935	942	952	java/lang/IndexOutOfBoundsException
    //   935	942	952	java/lang/NumberFormatException
    //   946	948	952	android/content/pm/PackageManager$NameNotFoundException
    //   946	948	952	java/lang/IndexOutOfBoundsException
    //   946	948	952	java/lang/NumberFormatException
    //   74	83	960	finally
    //   107	118	960	finally
    //   138	144	960	finally
    //   164	172	960	finally
    //   207	221	960	finally
    //   241	253	960	finally
    //   273	281	960	finally
    //   394	402	960	finally
    //   74	83	967	android/content/pm/PackageManager$NameNotFoundException
    //   74	83	967	java/lang/IndexOutOfBoundsException
    //   74	83	967	java/lang/NumberFormatException
    //   107	118	967	android/content/pm/PackageManager$NameNotFoundException
    //   107	118	967	java/lang/IndexOutOfBoundsException
    //   107	118	967	java/lang/NumberFormatException
    //   138	144	967	android/content/pm/PackageManager$NameNotFoundException
    //   138	144	967	java/lang/IndexOutOfBoundsException
    //   138	144	967	java/lang/NumberFormatException
    //   164	172	967	android/content/pm/PackageManager$NameNotFoundException
    //   164	172	967	java/lang/IndexOutOfBoundsException
    //   164	172	967	java/lang/NumberFormatException
    //   207	221	967	android/content/pm/PackageManager$NameNotFoundException
    //   207	221	967	java/lang/IndexOutOfBoundsException
    //   207	221	967	java/lang/NumberFormatException
    //   241	253	967	android/content/pm/PackageManager$NameNotFoundException
    //   241	253	967	java/lang/IndexOutOfBoundsException
    //   241	253	967	java/lang/NumberFormatException
    //   273	281	967	android/content/pm/PackageManager$NameNotFoundException
    //   273	281	967	java/lang/IndexOutOfBoundsException
    //   273	281	967	java/lang/NumberFormatException
    //   394	402	967	android/content/pm/PackageManager$NameNotFoundException
    //   394	402	967	java/lang/IndexOutOfBoundsException
    //   394	402	967	java/lang/NumberFormatException
    //   665	673	1064	finally
    //   982	986	1064	finally
    //   993	997	1064	finally
    //   1004	1008	1064	finally
    //   1015	1023	1064	finally
    //   1030	1040	1064	finally
    //   1047	1055	1064	finally
    //   1062	1064	1064	finally
  }
  
  public InputMethodInfo(ResolveInfo paramResolveInfo, boolean paramBoolean1, String paramString, List<InputMethodSubtype> paramList, int paramInt, boolean paramBoolean2)
  {
    this(paramResolveInfo, paramBoolean1, paramString, paramList, paramInt, paramBoolean2, true, false);
  }
  
  public InputMethodInfo(ResolveInfo paramResolveInfo, boolean paramBoolean1, String paramString, List<InputMethodSubtype> paramList, int paramInt, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    this.mService = paramResolveInfo;
    this.mId = new ComponentName(localServiceInfo.packageName, localServiceInfo.name).flattenToShortString();
    this.mSettingsActivityName = paramString;
    this.mIsDefaultResId = paramInt;
    this.mIsAuxIme = paramBoolean1;
    this.mSubtypes = new InputMethodSubtypeArray(paramList);
    this.mForceDefault = paramBoolean2;
    this.mSupportsSwitchingToNextInputMethod = paramBoolean3;
    this.mIsVrOnly = paramBoolean4;
  }
  
  InputMethodInfo(Parcel paramParcel)
  {
    this.mId = paramParcel.readString();
    this.mSettingsActivityName = paramParcel.readString();
    this.mIsDefaultResId = paramParcel.readInt();
    int i = paramParcel.readInt();
    boolean bool1 = true;
    boolean bool2;
    if (i == 1) {
      bool2 = true;
    } else {
      bool2 = false;
    }
    this.mIsAuxIme = bool2;
    if (paramParcel.readInt() == 1) {
      bool2 = bool1;
    } else {
      bool2 = false;
    }
    this.mSupportsSwitchingToNextInputMethod = bool2;
    this.mIsVrOnly = paramParcel.readBoolean();
    this.mService = ((ResolveInfo)ResolveInfo.CREATOR.createFromParcel(paramParcel));
    this.mSubtypes = new InputMethodSubtypeArray(paramParcel);
    this.mForceDefault = false;
  }
  
  public InputMethodInfo(String paramString1, String paramString2, CharSequence paramCharSequence, String paramString3)
  {
    this(buildDummyResolveInfo(paramString1, paramString2, paramCharSequence), false, paramString3, null, 0, false, true, false);
  }
  
  private static ResolveInfo buildDummyResolveInfo(String paramString1, String paramString2, CharSequence paramCharSequence)
  {
    ResolveInfo localResolveInfo = new ResolveInfo();
    ServiceInfo localServiceInfo = new ServiceInfo();
    ApplicationInfo localApplicationInfo = new ApplicationInfo();
    localApplicationInfo.packageName = paramString1;
    localApplicationInfo.enabled = true;
    localServiceInfo.applicationInfo = localApplicationInfo;
    localServiceInfo.enabled = true;
    localServiceInfo.packageName = paramString1;
    localServiceInfo.name = paramString2;
    localServiceInfo.exported = true;
    localServiceInfo.nonLocalizedLabel = paramCharSequence;
    localResolveInfo.serviceInfo = localServiceInfo;
    return localResolveInfo;
  }
  
  public static String computeId(ResolveInfo paramResolveInfo)
  {
    paramResolveInfo = paramResolveInfo.serviceInfo;
    return new ComponentName(paramResolveInfo.packageName, paramResolveInfo.name).flattenToShortString();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("mId=");
    ((StringBuilder)localObject).append(this.mId);
    ((StringBuilder)localObject).append(" mSettingsActivityName=");
    ((StringBuilder)localObject).append(this.mSettingsActivityName);
    ((StringBuilder)localObject).append(" mIsVrOnly=");
    ((StringBuilder)localObject).append(this.mIsVrOnly);
    ((StringBuilder)localObject).append(" mSupportsSwitchingToNextInputMethod=");
    ((StringBuilder)localObject).append(this.mSupportsSwitchingToNextInputMethod);
    paramPrinter.println(((StringBuilder)localObject).toString());
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("mIsDefaultResId=0x");
    ((StringBuilder)localObject).append(Integer.toHexString(this.mIsDefaultResId));
    paramPrinter.println(((StringBuilder)localObject).toString());
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append(paramString);
    ((StringBuilder)localObject).append("Service:");
    paramPrinter.println(((StringBuilder)localObject).toString());
    localObject = this.mService;
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("  ");
    ((ResolveInfo)localObject).dump(paramPrinter, localStringBuilder.toString());
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof InputMethodInfo)) {
      return false;
    }
    paramObject = (InputMethodInfo)paramObject;
    return this.mId.equals(((InputMethodInfo)paramObject).mId);
  }
  
  public ComponentName getComponent()
  {
    return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
  }
  
  public String getId()
  {
    return this.mId;
  }
  
  public int getIsDefaultResourceId()
  {
    return this.mIsDefaultResId;
  }
  
  public String getPackageName()
  {
    return this.mService.serviceInfo.packageName;
  }
  
  public ServiceInfo getServiceInfo()
  {
    return this.mService.serviceInfo;
  }
  
  public String getServiceName()
  {
    return this.mService.serviceInfo.name;
  }
  
  public String getSettingsActivity()
  {
    return this.mSettingsActivityName;
  }
  
  public InputMethodSubtype getSubtypeAt(int paramInt)
  {
    return this.mSubtypes.get(paramInt);
  }
  
  public int getSubtypeCount()
  {
    return this.mSubtypes.getCount();
  }
  
  public int hashCode()
  {
    return this.mId.hashCode();
  }
  
  public boolean isAuxiliaryIme()
  {
    return this.mIsAuxIme;
  }
  
  @UnsupportedAppUsage
  public boolean isDefault(Context paramContext)
  {
    if (this.mForceDefault) {
      return true;
    }
    try
    {
      if (getIsDefaultResourceId() == 0) {
        return false;
      }
      boolean bool = paramContext.createPackageContext(getPackageName(), 0).getResources().getBoolean(getIsDefaultResourceId());
      return bool;
    }
    catch (PackageManager.NameNotFoundException|Resources.NotFoundException paramContext) {}
    return false;
  }
  
  public boolean isSystem()
  {
    int i = this.mService.serviceInfo.applicationInfo.flags;
    boolean bool = true;
    if ((i & 0x1) == 0) {
      bool = false;
    }
    return bool;
  }
  
  public boolean isVrOnly()
  {
    return this.mIsVrOnly;
  }
  
  public Drawable loadIcon(PackageManager paramPackageManager)
  {
    return this.mService.loadIcon(paramPackageManager);
  }
  
  public CharSequence loadLabel(PackageManager paramPackageManager)
  {
    return this.mService.loadLabel(paramPackageManager);
  }
  
  public boolean supportsSwitchingToNextInputMethod()
  {
    return this.mSupportsSwitchingToNextInputMethod;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("InputMethodInfo{");
    localStringBuilder.append(this.mId);
    localStringBuilder.append(", settings: ");
    localStringBuilder.append(this.mSettingsActivityName);
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mId);
    paramParcel.writeString(this.mSettingsActivityName);
    paramParcel.writeInt(this.mIsDefaultResId);
    paramParcel.writeInt(this.mIsAuxIme);
    paramParcel.writeInt(this.mSupportsSwitchingToNextInputMethod);
    paramParcel.writeBoolean(this.mIsVrOnly);
    this.mService.writeToParcel(paramParcel, paramInt);
    this.mSubtypes.writeToParcel(paramParcel);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputMethodInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */