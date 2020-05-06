package miui.util;

import android.content.pm.Signature;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CertificateUtils
{
  private static final boolean DEBUG_JAR = false;
  private static final String TAG = CertificateUtils.class.getSimpleName();
  private static WeakReference<byte[]> sReadBuffer;
  
  /* Error */
  public static boolean collectCertificates(java.io.File paramFile, java.util.Set<Signature> paramSet)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 47 1 0
    //   6: aconst_null
    //   7: astore_2
    //   8: aconst_null
    //   9: astore_3
    //   10: ldc 2
    //   12: monitorenter
    //   13: aload_2
    //   14: astore 4
    //   16: getstatic 49	miui/util/CertificateUtils:sReadBuffer	Ljava/lang/ref/WeakReference;
    //   19: astore 5
    //   21: aload_3
    //   22: astore 4
    //   24: aload 5
    //   26: ifnull +23 -> 49
    //   29: aload_2
    //   30: astore 4
    //   32: aconst_null
    //   33: putstatic 49	miui/util/CertificateUtils:sReadBuffer	Ljava/lang/ref/WeakReference;
    //   36: aload_2
    //   37: astore 4
    //   39: aload 5
    //   41: invokevirtual 55	java/lang/ref/WeakReference:get	()Ljava/lang/Object;
    //   44: checkcast 57	[B
    //   47: astore 4
    //   49: aload 4
    //   51: astore_2
    //   52: aload 4
    //   54: ifnonnull +26 -> 80
    //   57: sipush 8192
    //   60: newarray <illegal type>
    //   62: astore_2
    //   63: aload_2
    //   64: astore 4
    //   66: new 51	java/lang/ref/WeakReference
    //   69: astore 5
    //   71: aload_2
    //   72: astore 4
    //   74: aload 5
    //   76: aload_2
    //   77: invokespecial 60	java/lang/ref/WeakReference:<init>	(Ljava/lang/Object;)V
    //   80: aload_2
    //   81: astore 4
    //   83: ldc 2
    //   85: monitorexit
    //   86: getstatic 66	android/os/Build$VERSION:SDK_INT	I
    //   89: bipush 21
    //   91: if_icmplt +34 -> 125
    //   94: ldc 68
    //   96: iconst_2
    //   97: anewarray 4	java/lang/Object
    //   100: dup
    //   101: iconst_0
    //   102: aload_0
    //   103: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   106: aastore
    //   107: dup
    //   108: iconst_1
    //   109: iconst_1
    //   110: invokestatic 79	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   113: aastore
    //   114: invokestatic 85	miui/util/ReflectionUtils:newInstance	(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;
    //   117: checkcast 68	java/util/jar/JarFile
    //   120: astore 4
    //   122: goto +38 -> 160
    //   125: ldc 68
    //   127: iconst_3
    //   128: anewarray 4	java/lang/Object
    //   131: dup
    //   132: iconst_0
    //   133: aload_0
    //   134: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   137: aastore
    //   138: dup
    //   139: iconst_1
    //   140: iconst_1
    //   141: invokestatic 79	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   144: aastore
    //   145: dup
    //   146: iconst_2
    //   147: iconst_1
    //   148: invokestatic 79	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   151: aastore
    //   152: invokestatic 85	miui/util/ReflectionUtils:newInstance	(Ljava/lang/Class;[Ljava/lang/Object;)Ljava/lang/Object;
    //   155: checkcast 68	java/util/jar/JarFile
    //   158: astore 4
    //   160: aconst_null
    //   161: astore_3
    //   162: aload 4
    //   164: invokevirtual 89	java/util/jar/JarFile:entries	()Ljava/util/Enumeration;
    //   167: astore 6
    //   169: aload 6
    //   171: invokeinterface 95 1 0
    //   176: ifeq +297 -> 473
    //   179: aload 6
    //   181: invokeinterface 98 1 0
    //   186: checkcast 100	java/util/jar/JarEntry
    //   189: astore 7
    //   191: aload 7
    //   193: invokevirtual 103	java/util/jar/JarEntry:isDirectory	()Z
    //   196: ifeq +6 -> 202
    //   199: goto -30 -> 169
    //   202: aload 7
    //   204: invokevirtual 106	java/util/jar/JarEntry:getName	()Ljava/lang/String;
    //   207: ldc 108
    //   209: invokevirtual 114	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   212: ifeq +6 -> 218
    //   215: goto -46 -> 169
    //   218: aload 4
    //   220: aload 7
    //   222: aload_2
    //   223: invokestatic 118	miui/util/CertificateUtils:loadCertificates	(Ljava/util/jar/JarFile;Ljava/util/jar/JarEntry;[B)[Ljava/security/cert/Certificate;
    //   226: astore 8
    //   228: aload 8
    //   230: ifnonnull +71 -> 301
    //   233: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   236: astore_2
    //   237: new 120	java/lang/StringBuilder
    //   240: astore_1
    //   241: aload_1
    //   242: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   245: aload_1
    //   246: ldc 123
    //   248: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: pop
    //   252: aload_1
    //   253: aload_0
    //   254: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   257: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   260: pop
    //   261: aload_1
    //   262: ldc -127
    //   264: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   267: pop
    //   268: aload_1
    //   269: aload 7
    //   271: invokevirtual 106	java/util/jar/JarEntry:getName	()Ljava/lang/String;
    //   274: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: pop
    //   278: aload_1
    //   279: ldc -125
    //   281: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: pop
    //   285: aload_2
    //   286: aload_1
    //   287: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   290: invokestatic 140	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   293: pop
    //   294: aload 4
    //   296: invokevirtual 143	java/util/jar/JarFile:close	()V
    //   299: iconst_0
    //   300: ireturn
    //   301: aload_3
    //   302: ifnonnull +10 -> 312
    //   305: aload 8
    //   307: astore 9
    //   309: goto +158 -> 467
    //   312: iconst_0
    //   313: istore 10
    //   315: aload_3
    //   316: astore 9
    //   318: iload 10
    //   320: aload_3
    //   321: arraylength
    //   322: if_icmpge +145 -> 467
    //   325: iconst_0
    //   326: istore 11
    //   328: iconst_0
    //   329: istore 12
    //   331: iload 11
    //   333: istore 13
    //   335: iload 12
    //   337: aload 8
    //   339: arraylength
    //   340: if_icmpge +37 -> 377
    //   343: aload_3
    //   344: iload 10
    //   346: aaload
    //   347: ifnull +24 -> 371
    //   350: aload_3
    //   351: iload 10
    //   353: aaload
    //   354: aload 8
    //   356: iload 12
    //   358: aaload
    //   359: invokevirtual 149	java/security/cert/Certificate:equals	(Ljava/lang/Object;)Z
    //   362: ifeq +9 -> 371
    //   365: iconst_1
    //   366: istore 13
    //   368: goto +9 -> 377
    //   371: iinc 12 1
    //   374: goto -43 -> 331
    //   377: iload 13
    //   379: ifeq +20 -> 399
    //   382: aload_3
    //   383: arraylength
    //   384: aload 8
    //   386: arraylength
    //   387: if_icmpeq +6 -> 393
    //   390: goto +9 -> 399
    //   393: iinc 10 1
    //   396: goto -81 -> 315
    //   399: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   402: astore_1
    //   403: new 120	java/lang/StringBuilder
    //   406: astore_2
    //   407: aload_2
    //   408: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   411: aload_2
    //   412: ldc 123
    //   414: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   417: pop
    //   418: aload_2
    //   419: aload_0
    //   420: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   423: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   426: pop
    //   427: aload_2
    //   428: ldc -105
    //   430: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   433: pop
    //   434: aload_2
    //   435: aload 7
    //   437: invokevirtual 106	java/util/jar/JarEntry:getName	()Ljava/lang/String;
    //   440: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   443: pop
    //   444: aload_2
    //   445: ldc -125
    //   447: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   450: pop
    //   451: aload_1
    //   452: aload_2
    //   453: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   456: invokestatic 140	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   459: pop
    //   460: aload 4
    //   462: invokevirtual 143	java/util/jar/JarFile:close	()V
    //   465: iconst_0
    //   466: ireturn
    //   467: aload 9
    //   469: astore_3
    //   470: goto -301 -> 169
    //   473: aload 4
    //   475: invokevirtual 143	java/util/jar/JarFile:close	()V
    //   478: ldc 2
    //   480: monitorenter
    //   481: aload 5
    //   483: putstatic 49	miui/util/CertificateUtils:sReadBuffer	Ljava/lang/ref/WeakReference;
    //   486: ldc 2
    //   488: monitorexit
    //   489: aload_3
    //   490: ifnull +55 -> 545
    //   493: aload_3
    //   494: arraylength
    //   495: ifle +50 -> 545
    //   498: aload_3
    //   499: arraylength
    //   500: istore 13
    //   502: iconst_0
    //   503: istore 10
    //   505: iload 10
    //   507: iload 13
    //   509: if_icmpge +34 -> 543
    //   512: new 153	android/content/pm/Signature
    //   515: dup
    //   516: aload_3
    //   517: iload 10
    //   519: aaload
    //   520: invokevirtual 157	java/security/cert/Certificate:getEncoded	()[B
    //   523: invokespecial 160	android/content/pm/Signature:<init>	([B)V
    //   526: astore 4
    //   528: aload_1
    //   529: aload 4
    //   531: invokeinterface 163 2 0
    //   536: pop
    //   537: iinc 10 1
    //   540: goto -35 -> 505
    //   543: iconst_1
    //   544: ireturn
    //   545: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   548: astore_1
    //   549: new 120	java/lang/StringBuilder
    //   552: astore 4
    //   554: aload 4
    //   556: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   559: aload 4
    //   561: ldc 123
    //   563: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   566: pop
    //   567: aload 4
    //   569: aload_0
    //   570: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   573: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   576: pop
    //   577: aload 4
    //   579: ldc -91
    //   581: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   584: pop
    //   585: aload_1
    //   586: aload 4
    //   588: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   591: invokestatic 140	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   594: pop
    //   595: iconst_0
    //   596: ireturn
    //   597: astore_1
    //   598: ldc 2
    //   600: monitorexit
    //   601: aload_1
    //   602: athrow
    //   603: astore_1
    //   604: goto +32 -> 636
    //   607: astore_1
    //   608: goto +71 -> 679
    //   611: astore_1
    //   612: goto +110 -> 722
    //   615: astore_1
    //   616: goto +151 -> 767
    //   619: astore_1
    //   620: goto +192 -> 812
    //   623: astore_1
    //   624: goto +231 -> 855
    //   627: astore_1
    //   628: goto +270 -> 898
    //   631: astore_1
    //   632: goto -34 -> 598
    //   635: astore_1
    //   636: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   639: astore 4
    //   641: new 120	java/lang/StringBuilder
    //   644: dup
    //   645: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   648: astore_2
    //   649: aload_2
    //   650: ldc -89
    //   652: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   655: pop
    //   656: aload_2
    //   657: aload_0
    //   658: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   661: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   664: pop
    //   665: aload 4
    //   667: aload_2
    //   668: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   671: aload_1
    //   672: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   675: pop
    //   676: iconst_0
    //   677: ireturn
    //   678: astore_1
    //   679: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   682: astore 4
    //   684: new 120	java/lang/StringBuilder
    //   687: dup
    //   688: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   691: astore_2
    //   692: aload_2
    //   693: ldc -89
    //   695: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   698: pop
    //   699: aload_2
    //   700: aload_0
    //   701: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   704: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   707: pop
    //   708: aload 4
    //   710: aload_2
    //   711: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   714: aload_1
    //   715: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   718: pop
    //   719: iconst_0
    //   720: ireturn
    //   721: astore_1
    //   722: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   725: astore_2
    //   726: new 120	java/lang/StringBuilder
    //   729: dup
    //   730: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   733: astore 4
    //   735: aload 4
    //   737: ldc -89
    //   739: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   742: pop
    //   743: aload 4
    //   745: aload_0
    //   746: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   749: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   752: pop
    //   753: aload_2
    //   754: aload 4
    //   756: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   759: aload_1
    //   760: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   763: pop
    //   764: iconst_0
    //   765: ireturn
    //   766: astore_1
    //   767: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   770: astore_2
    //   771: new 120	java/lang/StringBuilder
    //   774: dup
    //   775: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   778: astore 4
    //   780: aload 4
    //   782: ldc -89
    //   784: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   787: pop
    //   788: aload 4
    //   790: aload_0
    //   791: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   794: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   797: pop
    //   798: aload_2
    //   799: aload 4
    //   801: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   804: aload_1
    //   805: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   808: pop
    //   809: iconst_0
    //   810: ireturn
    //   811: astore_1
    //   812: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   815: astore 4
    //   817: new 120	java/lang/StringBuilder
    //   820: dup
    //   821: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   824: astore_2
    //   825: aload_2
    //   826: ldc -89
    //   828: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   831: pop
    //   832: aload_2
    //   833: aload_0
    //   834: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   837: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   840: pop
    //   841: aload 4
    //   843: aload_2
    //   844: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   847: aload_1
    //   848: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   851: pop
    //   852: iconst_0
    //   853: ireturn
    //   854: astore_1
    //   855: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   858: astore 4
    //   860: new 120	java/lang/StringBuilder
    //   863: dup
    //   864: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   867: astore_2
    //   868: aload_2
    //   869: ldc -89
    //   871: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   874: pop
    //   875: aload_2
    //   876: aload_0
    //   877: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   880: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   883: pop
    //   884: aload 4
    //   886: aload_2
    //   887: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   890: aload_1
    //   891: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   894: pop
    //   895: iconst_0
    //   896: ireturn
    //   897: astore_1
    //   898: getstatic 22	miui/util/CertificateUtils:TAG	Ljava/lang/String;
    //   901: astore_2
    //   902: new 120	java/lang/StringBuilder
    //   905: dup
    //   906: invokespecial 121	java/lang/StringBuilder:<init>	()V
    //   909: astore 4
    //   911: aload 4
    //   913: ldc -89
    //   915: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   918: pop
    //   919: aload 4
    //   921: aload_0
    //   922: invokevirtual 73	java/io/File:getPath	()Ljava/lang/String;
    //   925: invokevirtual 127	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   928: pop
    //   929: aload_2
    //   930: aload 4
    //   932: invokevirtual 134	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   935: aload_1
    //   936: invokestatic 171	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   939: pop
    //   940: iconst_0
    //   941: ireturn
    //   942: astore_0
    //   943: ldc 2
    //   945: monitorexit
    //   946: aload_0
    //   947: athrow
    //   948: astore_0
    //   949: goto -6 -> 943
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	952	0	paramFile	java.io.File
    //   0	952	1	paramSet	java.util.Set<Signature>
    //   7	923	2	localObject1	Object
    //   9	508	3	localObject2	Object
    //   14	917	4	localObject3	Object
    //   19	463	5	localWeakReference	WeakReference
    //   167	13	6	localEnumeration	java.util.Enumeration
    //   189	247	7	localJarEntry	JarEntry
    //   226	159	8	arrayOfCertificate	Certificate[]
    //   307	161	9	localObject4	Object
    //   313	225	10	i	int
    //   326	6	11	j	int
    //   329	43	12	k	int
    //   333	177	13	m	int
    // Exception table:
    //   from	to	target	type
    //   481	489	597	finally
    //   528	537	603	java/lang/reflect/InvocationTargetException
    //   545	595	603	java/lang/reflect/InvocationTargetException
    //   601	603	603	java/lang/reflect/InvocationTargetException
    //   528	537	607	java/lang/IllegalAccessException
    //   545	595	607	java/lang/IllegalAccessException
    //   601	603	607	java/lang/IllegalAccessException
    //   528	537	611	java/lang/InstantiationException
    //   545	595	611	java/lang/InstantiationException
    //   601	603	611	java/lang/InstantiationException
    //   528	537	615	java/lang/NoSuchMethodException
    //   545	595	615	java/lang/NoSuchMethodException
    //   601	603	615	java/lang/NoSuchMethodException
    //   528	537	619	java/lang/RuntimeException
    //   545	595	619	java/lang/RuntimeException
    //   601	603	619	java/lang/RuntimeException
    //   528	537	623	java/io/IOException
    //   545	595	623	java/io/IOException
    //   601	603	623	java/io/IOException
    //   528	537	627	java/security/cert/CertificateEncodingException
    //   545	595	627	java/security/cert/CertificateEncodingException
    //   601	603	627	java/security/cert/CertificateEncodingException
    //   598	601	631	finally
    //   86	122	635	java/lang/reflect/InvocationTargetException
    //   125	160	635	java/lang/reflect/InvocationTargetException
    //   162	169	635	java/lang/reflect/InvocationTargetException
    //   169	199	635	java/lang/reflect/InvocationTargetException
    //   202	215	635	java/lang/reflect/InvocationTargetException
    //   218	228	635	java/lang/reflect/InvocationTargetException
    //   233	299	635	java/lang/reflect/InvocationTargetException
    //   318	325	635	java/lang/reflect/InvocationTargetException
    //   335	343	635	java/lang/reflect/InvocationTargetException
    //   350	365	635	java/lang/reflect/InvocationTargetException
    //   382	390	635	java/lang/reflect/InvocationTargetException
    //   399	465	635	java/lang/reflect/InvocationTargetException
    //   473	481	635	java/lang/reflect/InvocationTargetException
    //   493	502	635	java/lang/reflect/InvocationTargetException
    //   512	528	635	java/lang/reflect/InvocationTargetException
    //   86	122	678	java/lang/IllegalAccessException
    //   125	160	678	java/lang/IllegalAccessException
    //   162	169	678	java/lang/IllegalAccessException
    //   169	199	678	java/lang/IllegalAccessException
    //   202	215	678	java/lang/IllegalAccessException
    //   218	228	678	java/lang/IllegalAccessException
    //   233	299	678	java/lang/IllegalAccessException
    //   318	325	678	java/lang/IllegalAccessException
    //   335	343	678	java/lang/IllegalAccessException
    //   350	365	678	java/lang/IllegalAccessException
    //   382	390	678	java/lang/IllegalAccessException
    //   399	465	678	java/lang/IllegalAccessException
    //   473	481	678	java/lang/IllegalAccessException
    //   493	502	678	java/lang/IllegalAccessException
    //   512	528	678	java/lang/IllegalAccessException
    //   86	122	721	java/lang/InstantiationException
    //   125	160	721	java/lang/InstantiationException
    //   162	169	721	java/lang/InstantiationException
    //   169	199	721	java/lang/InstantiationException
    //   202	215	721	java/lang/InstantiationException
    //   218	228	721	java/lang/InstantiationException
    //   233	299	721	java/lang/InstantiationException
    //   318	325	721	java/lang/InstantiationException
    //   335	343	721	java/lang/InstantiationException
    //   350	365	721	java/lang/InstantiationException
    //   382	390	721	java/lang/InstantiationException
    //   399	465	721	java/lang/InstantiationException
    //   473	481	721	java/lang/InstantiationException
    //   493	502	721	java/lang/InstantiationException
    //   512	528	721	java/lang/InstantiationException
    //   86	122	766	java/lang/NoSuchMethodException
    //   125	160	766	java/lang/NoSuchMethodException
    //   162	169	766	java/lang/NoSuchMethodException
    //   169	199	766	java/lang/NoSuchMethodException
    //   202	215	766	java/lang/NoSuchMethodException
    //   218	228	766	java/lang/NoSuchMethodException
    //   233	299	766	java/lang/NoSuchMethodException
    //   318	325	766	java/lang/NoSuchMethodException
    //   335	343	766	java/lang/NoSuchMethodException
    //   350	365	766	java/lang/NoSuchMethodException
    //   382	390	766	java/lang/NoSuchMethodException
    //   399	465	766	java/lang/NoSuchMethodException
    //   473	481	766	java/lang/NoSuchMethodException
    //   493	502	766	java/lang/NoSuchMethodException
    //   512	528	766	java/lang/NoSuchMethodException
    //   86	122	811	java/lang/RuntimeException
    //   125	160	811	java/lang/RuntimeException
    //   162	169	811	java/lang/RuntimeException
    //   169	199	811	java/lang/RuntimeException
    //   202	215	811	java/lang/RuntimeException
    //   218	228	811	java/lang/RuntimeException
    //   233	299	811	java/lang/RuntimeException
    //   318	325	811	java/lang/RuntimeException
    //   335	343	811	java/lang/RuntimeException
    //   350	365	811	java/lang/RuntimeException
    //   382	390	811	java/lang/RuntimeException
    //   399	465	811	java/lang/RuntimeException
    //   473	481	811	java/lang/RuntimeException
    //   493	502	811	java/lang/RuntimeException
    //   512	528	811	java/lang/RuntimeException
    //   86	122	854	java/io/IOException
    //   125	160	854	java/io/IOException
    //   162	169	854	java/io/IOException
    //   169	199	854	java/io/IOException
    //   202	215	854	java/io/IOException
    //   218	228	854	java/io/IOException
    //   233	299	854	java/io/IOException
    //   318	325	854	java/io/IOException
    //   335	343	854	java/io/IOException
    //   350	365	854	java/io/IOException
    //   382	390	854	java/io/IOException
    //   399	465	854	java/io/IOException
    //   473	481	854	java/io/IOException
    //   493	502	854	java/io/IOException
    //   512	528	854	java/io/IOException
    //   86	122	897	java/security/cert/CertificateEncodingException
    //   125	160	897	java/security/cert/CertificateEncodingException
    //   162	169	897	java/security/cert/CertificateEncodingException
    //   169	199	897	java/security/cert/CertificateEncodingException
    //   202	215	897	java/security/cert/CertificateEncodingException
    //   218	228	897	java/security/cert/CertificateEncodingException
    //   233	299	897	java/security/cert/CertificateEncodingException
    //   318	325	897	java/security/cert/CertificateEncodingException
    //   335	343	897	java/security/cert/CertificateEncodingException
    //   350	365	897	java/security/cert/CertificateEncodingException
    //   382	390	897	java/security/cert/CertificateEncodingException
    //   399	465	897	java/security/cert/CertificateEncodingException
    //   473	481	897	java/security/cert/CertificateEncodingException
    //   493	502	897	java/security/cert/CertificateEncodingException
    //   512	528	897	java/security/cert/CertificateEncodingException
    //   16	21	942	finally
    //   32	36	942	finally
    //   39	49	942	finally
    //   57	63	942	finally
    //   66	71	942	finally
    //   74	80	942	finally
    //   83	86	942	finally
    //   943	946	948	finally
  }
  
  public static int compareSignatures(Signature[] paramArrayOfSignature1, Signature[] paramArrayOfSignature2)
  {
    if (paramArrayOfSignature1 == null)
    {
      if (paramArrayOfSignature2 == null) {
        i = 1;
      } else {
        i = -1;
      }
      return i;
    }
    if (paramArrayOfSignature2 == null) {
      return -2;
    }
    HashSet localHashSet = new HashSet();
    int j = paramArrayOfSignature1.length;
    for (int i = 0; i < j; i++) {
      localHashSet.add(paramArrayOfSignature1[i]);
    }
    paramArrayOfSignature1 = new HashSet();
    j = paramArrayOfSignature2.length;
    for (i = 0; i < j; i++) {
      paramArrayOfSignature1.add(paramArrayOfSignature2[i]);
    }
    if (localHashSet.equals(paramArrayOfSignature1)) {
      return 0;
    }
    return -3;
  }
  
  private static Certificate[] loadCertificates(JarFile paramJarFile, JarEntry paramJarEntry, byte[] paramArrayOfByte)
  {
    Object localObject = null;
    try
    {
      BufferedInputStream localBufferedInputStream = new java/io/BufferedInputStream;
      localBufferedInputStream.<init>(paramJarFile.getInputStream(paramJarEntry));
      while (localBufferedInputStream.read(paramArrayOfByte, 0, paramArrayOfByte.length) != -1) {}
      localBufferedInputStream.close();
      paramArrayOfByte = (byte[])localObject;
      if (paramJarEntry != null) {
        paramArrayOfByte = paramJarEntry.getCertificates();
      }
      return paramArrayOfByte;
    }
    catch (RuntimeException localRuntimeException)
    {
      localObject = TAG;
      paramArrayOfByte = new StringBuilder();
      paramArrayOfByte.append("Exception reading ");
      paramArrayOfByte.append(paramJarEntry.getName());
      paramArrayOfByte.append(" in ");
      paramArrayOfByte.append(paramJarFile.getName());
      Slog.w((String)localObject, paramArrayOfByte.toString(), localRuntimeException);
    }
    catch (IOException localIOException)
    {
      paramArrayOfByte = TAG;
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Exception reading ");
      ((StringBuilder)localObject).append(paramJarEntry.getName());
      ((StringBuilder)localObject).append(" in ");
      ((StringBuilder)localObject).append(paramJarFile.getName());
      Slog.w(paramArrayOfByte, ((StringBuilder)localObject).toString(), localIOException);
    }
    return null;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/CertificateUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */