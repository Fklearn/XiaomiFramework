package miui.push;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Packet
{
  protected static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage().toLowerCase();
  private static String DEFAULT_XML_NS = null;
  public static final String ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE";
  public static final DateFormat XEP_0082_UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private static long id = 0L;
  private static String prefix;
  private String chId = null;
  private XMPPError error = null;
  private String from = null;
  private List<CommonPacketExtension> packetExtensions = new CopyOnWriteArrayList();
  private String packetID = null;
  private final Map<String, Object> properties = new HashMap();
  private String to = null;
  private String xmlns = DEFAULT_XML_NS;
  
  static
  {
    XEP_0082_UTC_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(StringUtils.randomString(5));
    localStringBuilder.append("-");
    prefix = localStringBuilder.toString();
  }
  
  public Packet() {}
  
  public Packet(Bundle paramBundle)
  {
    this.to = paramBundle.getString("ext_to");
    this.from = paramBundle.getString("ext_from");
    this.chId = paramBundle.getString("ext_chid");
    this.packetID = paramBundle.getString("ext_pkt_id");
    Parcelable[] arrayOfParcelable = paramBundle.getParcelableArray("ext_exts");
    if (arrayOfParcelable != null)
    {
      this.packetExtensions = new ArrayList(arrayOfParcelable.length);
      int i = arrayOfParcelable.length;
      for (int j = 0; j < i; j++)
      {
        CommonPacketExtension localCommonPacketExtension = CommonPacketExtension.parseFromBundle((Bundle)arrayOfParcelable[j]);
        if (localCommonPacketExtension != null) {
          this.packetExtensions.add(localCommonPacketExtension);
        }
      }
    }
    paramBundle = paramBundle.getBundle("ext_ERROR");
    if (paramBundle != null) {
      this.error = new XMPPError(paramBundle);
    }
  }
  
  public static String getDefaultLanguage()
  {
    return DEFAULT_LANGUAGE;
  }
  
  public static String nextID()
  {
    try
    {
      Object localObject1 = new java/lang/StringBuilder;
      ((StringBuilder)localObject1).<init>();
      ((StringBuilder)localObject1).append(prefix);
      long l = id;
      id = 1L + l;
      ((StringBuilder)localObject1).append(Long.toString(l));
      localObject1 = ((StringBuilder)localObject1).toString();
      return (String)localObject1;
    }
    finally
    {
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public static void setDefaultXmlns(String paramString)
  {
    DEFAULT_XML_NS = paramString;
  }
  
  public void addExtension(CommonPacketExtension paramCommonPacketExtension)
  {
    this.packetExtensions.add(paramCommonPacketExtension);
  }
  
  public void deleteProperty(String paramString)
  {
    try
    {
      Map localMap = this.properties;
      if (localMap == null) {
        return;
      }
      this.properties.remove(paramString);
      return;
    }
    finally {}
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {
      return true;
    }
    if ((paramObject != null) && (getClass() == paramObject.getClass()))
    {
      paramObject = (Packet)paramObject;
      Object localObject = this.error;
      if (localObject != null ? !localObject.equals(((Packet)paramObject).error) : ((Packet)paramObject).error != null) {
        return false;
      }
      localObject = this.from;
      if (localObject != null ? !((String)localObject).equals(((Packet)paramObject).from) : ((Packet)paramObject).from != null) {
        return false;
      }
      if (!this.packetExtensions.equals(((Packet)paramObject).packetExtensions)) {
        return false;
      }
      localObject = this.packetID;
      if (localObject != null ? !((String)localObject).equals(((Packet)paramObject).packetID) : ((Packet)paramObject).packetID != null) {
        return false;
      }
      localObject = this.chId;
      if (localObject != null ? !((String)localObject).equals(((Packet)paramObject).chId) : ((Packet)paramObject).chId != null) {
        return false;
      }
      localObject = this.properties;
      if (localObject != null ? !((Map)localObject).equals(((Packet)paramObject).properties) : ((Packet)paramObject).properties != null) {
        return false;
      }
      localObject = this.to;
      if (localObject != null ? !((String)localObject).equals(((Packet)paramObject).to) : ((Packet)paramObject).to != null) {
        return false;
      }
      localObject = this.xmlns;
      if (localObject != null)
      {
        if (!((String)localObject).equals(((Packet)paramObject).xmlns)) {
          break label277;
        }
      }
      else {
        if (((Packet)paramObject).xmlns == null) {
          break label279;
        }
      }
      label277:
      bool = false;
      label279:
      return bool;
    }
    return false;
  }
  
  public String getChannelId()
  {
    return this.chId;
  }
  
  public XMPPError getError()
  {
    return this.error;
  }
  
  public CommonPacketExtension getExtension(String paramString)
  {
    return getExtension(paramString, null);
  }
  
  public CommonPacketExtension getExtension(String paramString1, String paramString2)
  {
    Iterator localIterator = this.packetExtensions.iterator();
    while (localIterator.hasNext())
    {
      CommonPacketExtension localCommonPacketExtension = (CommonPacketExtension)localIterator.next();
      if (((paramString2 == null) || (paramString2.equals(localCommonPacketExtension.getNamespace()))) && (paramString1.equals(localCommonPacketExtension.getElementName()))) {
        return localCommonPacketExtension;
      }
    }
    return null;
  }
  
  public Collection<CommonPacketExtension> getExtensions()
  {
    try
    {
      if (this.packetExtensions == null)
      {
        localObject1 = Collections.emptyList();
        return (Collection<CommonPacketExtension>)localObject1;
      }
      Object localObject1 = new java/util/ArrayList;
      ((ArrayList)localObject1).<init>(this.packetExtensions);
      localObject1 = Collections.unmodifiableList((List)localObject1);
      return (Collection<CommonPacketExtension>)localObject1;
    }
    finally {}
  }
  
  /* Error */
  protected String getExtensionsXML()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 74	java/lang/StringBuilder
    //   5: astore_1
    //   6: aload_1
    //   7: invokespecial 76	java/lang/StringBuilder:<init>	()V
    //   10: aload_0
    //   11: invokevirtual 244	miui/push/Packet:getExtensions	()Ljava/util/Collection;
    //   14: invokeinterface 247 1 0
    //   19: astore_2
    //   20: aload_2
    //   21: invokeinterface 212 1 0
    //   26: ifeq +25 -> 51
    //   29: aload_1
    //   30: aload_2
    //   31: invokeinterface 216 1 0
    //   36: checkcast 249	miui/push/PacketExtension
    //   39: invokeinterface 252 1 0
    //   44: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: pop
    //   48: goto -28 -> 20
    //   51: aload_0
    //   52: getfield 117	miui/push/Packet:properties	Ljava/util/Map;
    //   55: ifnull +620 -> 675
    //   58: aload_0
    //   59: getfield 117	miui/push/Packet:properties	Ljava/util/Map;
    //   62: invokeinterface 255 1 0
    //   67: ifne +608 -> 675
    //   70: aload_1
    //   71: ldc_w 257
    //   74: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload_0
    //   79: invokevirtual 260	miui/push/Packet:getPropertyNames	()Ljava/util/Collection;
    //   82: invokeinterface 247 1 0
    //   87: astore_3
    //   88: aload_3
    //   89: invokeinterface 212 1 0
    //   94: ifeq +573 -> 667
    //   97: aload_3
    //   98: invokeinterface 216 1 0
    //   103: checkcast 41	java/lang/String
    //   106: astore_2
    //   107: aload_0
    //   108: aload_2
    //   109: invokevirtual 264	miui/push/Packet:getProperty	(Ljava/lang/String;)Ljava/lang/Object;
    //   112: astore 4
    //   114: aload_1
    //   115: ldc_w 266
    //   118: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: pop
    //   122: aload_1
    //   123: ldc_w 268
    //   126: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: pop
    //   130: aload_1
    //   131: aload_2
    //   132: invokestatic 271	miui/push/StringUtils:escapeForXML	(Ljava/lang/String;)Ljava/lang/String;
    //   135: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: pop
    //   139: aload_1
    //   140: ldc_w 273
    //   143: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: pop
    //   147: aload_1
    //   148: ldc_w 275
    //   151: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: pop
    //   155: aload 4
    //   157: instanceof 277
    //   160: ifeq +29 -> 189
    //   163: aload_1
    //   164: ldc_w 279
    //   167: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   170: pop
    //   171: aload_1
    //   172: aload 4
    //   174: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   177: pop
    //   178: aload_1
    //   179: ldc_w 284
    //   182: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: pop
    //   186: goto +440 -> 626
    //   189: aload 4
    //   191: instanceof 171
    //   194: ifeq +29 -> 223
    //   197: aload_1
    //   198: ldc_w 286
    //   201: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: pop
    //   205: aload_1
    //   206: aload 4
    //   208: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   211: pop
    //   212: aload_1
    //   213: ldc_w 284
    //   216: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   219: pop
    //   220: goto +406 -> 626
    //   223: aload 4
    //   225: instanceof 288
    //   228: ifeq +29 -> 257
    //   231: aload_1
    //   232: ldc_w 290
    //   235: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   238: pop
    //   239: aload_1
    //   240: aload 4
    //   242: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   245: pop
    //   246: aload_1
    //   247: ldc_w 284
    //   250: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: pop
    //   254: goto +372 -> 626
    //   257: aload 4
    //   259: instanceof 292
    //   262: ifeq +29 -> 291
    //   265: aload_1
    //   266: ldc_w 294
    //   269: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   272: pop
    //   273: aload_1
    //   274: aload 4
    //   276: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   279: pop
    //   280: aload_1
    //   281: ldc_w 284
    //   284: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   287: pop
    //   288: goto +338 -> 626
    //   291: aload 4
    //   293: instanceof 296
    //   296: ifeq +29 -> 325
    //   299: aload_1
    //   300: ldc_w 298
    //   303: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   306: pop
    //   307: aload_1
    //   308: aload 4
    //   310: invokevirtual 282	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   313: pop
    //   314: aload_1
    //   315: ldc_w 284
    //   318: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   321: pop
    //   322: goto +304 -> 626
    //   325: aload 4
    //   327: instanceof 41
    //   330: ifeq +35 -> 365
    //   333: aload_1
    //   334: ldc_w 300
    //   337: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   340: pop
    //   341: aload_1
    //   342: aload 4
    //   344: checkcast 41	java/lang/String
    //   347: invokestatic 271	miui/push/StringUtils:escapeForXML	(Ljava/lang/String;)Ljava/lang/String;
    //   350: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   353: pop
    //   354: aload_1
    //   355: ldc_w 284
    //   358: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   361: pop
    //   362: goto +264 -> 626
    //   365: aconst_null
    //   366: astore 5
    //   368: aconst_null
    //   369: astore 6
    //   371: aconst_null
    //   372: astore 7
    //   374: aconst_null
    //   375: astore 8
    //   377: aload 6
    //   379: astore_2
    //   380: aload 8
    //   382: astore 9
    //   384: aload 5
    //   386: astore 10
    //   388: aload 7
    //   390: astore 11
    //   392: new 302	java/io/ByteArrayOutputStream
    //   395: astore 12
    //   397: aload 6
    //   399: astore_2
    //   400: aload 8
    //   402: astore 9
    //   404: aload 5
    //   406: astore 10
    //   408: aload 7
    //   410: astore 11
    //   412: aload 12
    //   414: invokespecial 303	java/io/ByteArrayOutputStream:<init>	()V
    //   417: aload 12
    //   419: astore_2
    //   420: aload 8
    //   422: astore 9
    //   424: aload 12
    //   426: astore 10
    //   428: aload 7
    //   430: astore 11
    //   432: new 305	java/io/ObjectOutputStream
    //   435: astore 6
    //   437: aload 12
    //   439: astore_2
    //   440: aload 8
    //   442: astore 9
    //   444: aload 12
    //   446: astore 10
    //   448: aload 7
    //   450: astore 11
    //   452: aload 6
    //   454: aload 12
    //   456: invokespecial 308	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   459: aload 6
    //   461: astore 8
    //   463: aload 12
    //   465: astore_2
    //   466: aload 8
    //   468: astore 9
    //   470: aload 12
    //   472: astore 10
    //   474: aload 8
    //   476: astore 11
    //   478: aload 8
    //   480: aload 4
    //   482: invokevirtual 312	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   485: aload 12
    //   487: astore_2
    //   488: aload 8
    //   490: astore 9
    //   492: aload 12
    //   494: astore 10
    //   496: aload 8
    //   498: astore 11
    //   500: aload_1
    //   501: ldc_w 314
    //   504: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   507: pop
    //   508: aload 12
    //   510: astore_2
    //   511: aload 8
    //   513: astore 9
    //   515: aload 12
    //   517: astore 10
    //   519: aload 8
    //   521: astore 11
    //   523: aload_1
    //   524: aload 12
    //   526: invokevirtual 318	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   529: invokestatic 322	miui/push/StringUtils:encodeBase64	([B)Ljava/lang/String;
    //   532: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   535: pop
    //   536: aload 12
    //   538: astore_2
    //   539: aload 8
    //   541: astore 9
    //   543: aload 12
    //   545: astore 10
    //   547: aload 8
    //   549: astore 11
    //   551: aload_1
    //   552: ldc_w 284
    //   555: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   558: pop
    //   559: aload 8
    //   561: invokevirtual 325	java/io/ObjectOutputStream:close	()V
    //   564: goto +4 -> 568
    //   567: astore_2
    //   568: aload 12
    //   570: invokevirtual 326	java/io/ByteArrayOutputStream:close	()V
    //   573: goto +46 -> 619
    //   576: astore 10
    //   578: goto +59 -> 637
    //   581: astore 12
    //   583: aload 10
    //   585: astore_2
    //   586: aload 11
    //   588: astore 9
    //   590: aload 12
    //   592: invokevirtual 329	java/lang/Exception:printStackTrace	()V
    //   595: aload 11
    //   597: ifnull +12 -> 609
    //   600: aload 11
    //   602: invokevirtual 325	java/io/ObjectOutputStream:close	()V
    //   605: goto +4 -> 609
    //   608: astore_2
    //   609: aload 10
    //   611: ifnull +15 -> 626
    //   614: aload 10
    //   616: invokevirtual 326	java/io/ByteArrayOutputStream:close	()V
    //   619: goto +7 -> 626
    //   622: astore_2
    //   623: goto -4 -> 619
    //   626: aload_1
    //   627: ldc_w 331
    //   630: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   633: pop
    //   634: goto -546 -> 88
    //   637: aload 9
    //   639: ifnull +13 -> 652
    //   642: aload 9
    //   644: invokevirtual 325	java/io/ObjectOutputStream:close	()V
    //   647: goto +5 -> 652
    //   650: astore 9
    //   652: aload_2
    //   653: ifnull +11 -> 664
    //   656: aload_2
    //   657: invokevirtual 326	java/io/ByteArrayOutputStream:close	()V
    //   660: goto +4 -> 664
    //   663: astore_2
    //   664: aload 10
    //   666: athrow
    //   667: aload_1
    //   668: ldc_w 333
    //   671: invokevirtual 86	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   674: pop
    //   675: aload_1
    //   676: invokevirtual 91	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   679: astore_2
    //   680: aload_0
    //   681: monitorexit
    //   682: aload_2
    //   683: areturn
    //   684: astore_2
    //   685: aload_0
    //   686: monitorexit
    //   687: aload_2
    //   688: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	689	0	this	Packet
    //   5	671	1	localStringBuilder	StringBuilder
    //   19	520	2	localObject1	Object
    //   567	1	2	localException1	Exception
    //   585	1	2	localObject2	Object
    //   608	1	2	localException2	Exception
    //   622	35	2	localException3	Exception
    //   663	1	2	localException4	Exception
    //   679	4	2	str	String
    //   684	4	2	localObject3	Object
    //   87	11	3	localIterator	Iterator
    //   112	369	4	localObject4	Object
    //   366	39	5	localObject5	Object
    //   369	91	6	localObjectOutputStream1	java.io.ObjectOutputStream
    //   372	77	7	localObject6	Object
    //   375	185	8	localObjectOutputStream2	java.io.ObjectOutputStream
    //   382	261	9	localObject7	Object
    //   650	1	9	localException5	Exception
    //   386	160	10	localObject8	Object
    //   576	89	10	localObject9	Object
    //   390	211	11	localObject10	Object
    //   395	174	12	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   581	10	12	localException6	Exception
    // Exception table:
    //   from	to	target	type
    //   559	564	567	java/lang/Exception
    //   392	397	576	finally
    //   412	417	576	finally
    //   432	437	576	finally
    //   452	459	576	finally
    //   478	485	576	finally
    //   500	508	576	finally
    //   523	536	576	finally
    //   551	559	576	finally
    //   590	595	576	finally
    //   392	397	581	java/lang/Exception
    //   412	417	581	java/lang/Exception
    //   432	437	581	java/lang/Exception
    //   452	459	581	java/lang/Exception
    //   478	485	581	java/lang/Exception
    //   500	508	581	java/lang/Exception
    //   523	536	581	java/lang/Exception
    //   551	559	581	java/lang/Exception
    //   600	605	608	java/lang/Exception
    //   568	573	622	java/lang/Exception
    //   614	619	622	java/lang/Exception
    //   642	647	650	java/lang/Exception
    //   656	660	663	java/lang/Exception
    //   2	20	684	finally
    //   20	48	684	finally
    //   51	88	684	finally
    //   88	186	684	finally
    //   189	220	684	finally
    //   223	254	684	finally
    //   257	288	684	finally
    //   291	322	684	finally
    //   325	362	684	finally
    //   559	564	684	finally
    //   568	573	684	finally
    //   600	605	684	finally
    //   614	619	684	finally
    //   626	634	684	finally
    //   642	647	684	finally
    //   656	660	684	finally
    //   664	667	684	finally
    //   667	675	684	finally
    //   675	680	684	finally
  }
  
  public String getFrom()
  {
    return this.from;
  }
  
  public String getPacketID()
  {
    if ("ID_NOT_AVAILABLE".equals(this.packetID)) {
      return null;
    }
    if (this.packetID == null) {
      this.packetID = nextID();
    }
    return this.packetID;
  }
  
  public Object getProperty(String paramString)
  {
    try
    {
      Map localMap = this.properties;
      if (localMap == null) {
        return null;
      }
      paramString = this.properties.get(paramString);
      return paramString;
    }
    finally {}
  }
  
  public Collection<String> getPropertyNames()
  {
    try
    {
      if (this.properties == null)
      {
        localObject1 = Collections.emptySet();
        return (Collection<String>)localObject1;
      }
      Object localObject1 = new java/util/HashSet;
      ((HashSet)localObject1).<init>(this.properties.keySet());
      localObject1 = Collections.unmodifiableSet((Set)localObject1);
      return (Collection<String>)localObject1;
    }
    finally {}
  }
  
  public String getTo()
  {
    return this.to;
  }
  
  public String getXmlns()
  {
    return this.xmlns;
  }
  
  public int hashCode()
  {
    Object localObject = this.xmlns;
    int i = 0;
    int j;
    if (localObject != null) {
      j = ((String)localObject).hashCode();
    } else {
      j = 0;
    }
    localObject = this.packetID;
    int k;
    if (localObject != null) {
      k = ((String)localObject).hashCode();
    } else {
      k = 0;
    }
    localObject = this.to;
    int m;
    if (localObject != null) {
      m = ((String)localObject).hashCode();
    } else {
      m = 0;
    }
    localObject = this.from;
    int n;
    if (localObject != null) {
      n = ((String)localObject).hashCode();
    } else {
      n = 0;
    }
    localObject = this.chId;
    int i1;
    if (localObject != null) {
      i1 = ((String)localObject).hashCode();
    } else {
      i1 = 0;
    }
    int i2 = this.packetExtensions.hashCode();
    int i3 = this.properties.hashCode();
    localObject = this.error;
    if (localObject != null) {
      i = localObject.hashCode();
    }
    return ((((((j * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i;
  }
  
  public void removeExtension(CommonPacketExtension paramCommonPacketExtension)
  {
    this.packetExtensions.remove(paramCommonPacketExtension);
  }
  
  public void setChannelId(String paramString)
  {
    this.chId = paramString;
  }
  
  public void setError(XMPPError paramXMPPError)
  {
    this.error = paramXMPPError;
  }
  
  public void setFrom(String paramString)
  {
    this.from = paramString;
  }
  
  public void setPacketID(String paramString)
  {
    this.packetID = paramString;
  }
  
  public void setProperty(String paramString, Object paramObject)
  {
    try
    {
      if ((paramObject instanceof Serializable))
      {
        this.properties.put(paramString, paramObject);
        return;
      }
      paramString = new java/lang/IllegalArgumentException;
      paramString.<init>("Value must be serialiazble");
      throw paramString;
    }
    finally {}
  }
  
  public void setTo(String paramString)
  {
    this.to = paramString;
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle1 = new Bundle();
    if (!TextUtils.isEmpty(this.xmlns)) {
      localBundle1.putString("ext_ns", this.xmlns);
    }
    if (!TextUtils.isEmpty(this.from)) {
      localBundle1.putString("ext_from", this.from);
    }
    if (!TextUtils.isEmpty(this.to)) {
      localBundle1.putString("ext_to", this.to);
    }
    if (!TextUtils.isEmpty(this.packetID)) {
      localBundle1.putString("ext_pkt_id", this.packetID);
    }
    if (!TextUtils.isEmpty(this.chId)) {
      localBundle1.putString("ext_chid", this.chId);
    }
    Object localObject = this.error;
    if (localObject != null) {
      localBundle1.putBundle("ext_ERROR", ((XMPPError)localObject).toBundle());
    }
    localObject = this.packetExtensions;
    if (localObject != null)
    {
      localObject = new Bundle[((List)localObject).size()];
      int i = 0;
      Iterator localIterator = this.packetExtensions.iterator();
      while (localIterator.hasNext())
      {
        Bundle localBundle2 = ((CommonPacketExtension)localIterator.next()).toBundle();
        int j = i;
        if (localBundle2 != null)
        {
          localObject[i] = localBundle2;
          j = i + 1;
        }
        i = j;
      }
      localBundle1.putParcelableArray("ext_exts", (Parcelable[])localObject);
    }
    return localBundle1;
  }
  
  public abstract String toXML();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/push/Packet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */