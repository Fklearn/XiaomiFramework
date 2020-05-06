package miui.util;

import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Process;
import android.os.SystemProperties;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class DumpBitmapInfoUtils
{
  static final boolean ENABLE = MiuiFeatureUtils.isSystemFeatureSupported("DumpBitmapInfo", true);
  static int sBitmapThresholdSize;
  static WeakHashMap<Bitmap, CharSequence> sBitmapTitles;
  static int sCurrProcess;
  
  static
  {
    if (ENABLE) {
      sBitmapTitles = new WeakHashMap();
    }
  }
  
  /* Error */
  public static void dumpBitmapInfo(java.io.FileDescriptor paramFileDescriptor, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: getstatic 26	miui/util/DumpBitmapInfoUtils:ENABLE	Z
    //   3: ifne +4 -> 7
    //   6: return
    //   7: aload_1
    //   8: arraylength
    //   9: istore_2
    //   10: iconst_0
    //   11: istore_3
    //   12: iconst_0
    //   13: istore 4
    //   15: iconst_0
    //   16: istore 5
    //   18: iconst_0
    //   19: istore 6
    //   21: iconst_0
    //   22: istore 7
    //   24: iconst_0
    //   25: istore 8
    //   27: iload 8
    //   29: iload_2
    //   30: if_icmpge +150 -> 180
    //   33: aload_1
    //   34: iload 8
    //   36: aaload
    //   37: astore 9
    //   39: ldc 41
    //   41: aload 9
    //   43: invokevirtual 47	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   46: ifne +13 -> 59
    //   49: ldc 49
    //   51: aload 9
    //   53: invokevirtual 47	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   56: ifeq +6 -> 62
    //   59: iconst_1
    //   60: istore 7
    //   62: ldc 51
    //   64: aload 9
    //   66: invokevirtual 47	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   69: ifne +13 -> 82
    //   72: ldc 53
    //   74: aload 9
    //   76: invokevirtual 47	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   79: ifeq +6 -> 85
    //   82: iconst_1
    //   83: istore 6
    //   85: ldc 55
    //   87: aload 9
    //   89: invokevirtual 47	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   92: ifeq +6 -> 98
    //   95: iconst_1
    //   96: istore 5
    //   98: ldc 57
    //   100: aload 9
    //   102: invokevirtual 47	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   105: ifeq +6 -> 111
    //   108: iconst_1
    //   109: istore 4
    //   111: iload_3
    //   112: istore 10
    //   114: aload 9
    //   116: ldc 59
    //   118: invokevirtual 62	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   121: ifeq +50 -> 171
    //   124: aload 9
    //   126: ldc 64
    //   128: invokevirtual 62	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   131: ifeq +23 -> 154
    //   134: aload 9
    //   136: ldc 64
    //   138: invokevirtual 68	java/lang/String:length	()I
    //   141: invokevirtual 72	java/lang/String:substring	(I)Ljava/lang/String;
    //   144: bipush 16
    //   146: invokestatic 78	java/lang/Integer:parseInt	(Ljava/lang/String;I)I
    //   149: istore 10
    //   151: goto +20 -> 171
    //   154: aload 9
    //   156: ldc 59
    //   158: invokevirtual 68	java/lang/String:length	()I
    //   161: invokevirtual 72	java/lang/String:substring	(I)Ljava/lang/String;
    //   164: bipush 16
    //   166: invokestatic 78	java/lang/Integer:parseInt	(Ljava/lang/String;I)I
    //   169: istore 10
    //   171: iinc 8 1
    //   174: iload 10
    //   176: istore_3
    //   177: goto -150 -> 27
    //   180: iload 7
    //   182: ifne +9 -> 191
    //   185: iload 6
    //   187: ifne +4 -> 191
    //   190: return
    //   191: iload 5
    //   193: ifne +6 -> 199
    //   196: invokestatic 83	java/lang/System:gc	()V
    //   199: invokestatic 89	android/app/ActivityThread:currentApplication	()Landroid/app/Application;
    //   202: ifnull +75 -> 277
    //   205: invokestatic 93	android/app/ActivityThread:currentPackageName	()Ljava/lang/String;
    //   208: invokestatic 99	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   211: ifne +66 -> 277
    //   214: ldc 101
    //   216: invokestatic 93	android/app/ActivityThread:currentPackageName	()Ljava/lang/String;
    //   219: invokevirtual 105	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   222: ifeq +6 -> 228
    //   225: goto +52 -> 277
    //   228: invokestatic 89	android/app/ActivityThread:currentApplication	()Landroid/app/Application;
    //   231: invokevirtual 111	android/app/Application:getCacheDir	()Ljava/io/File;
    //   234: astore 9
    //   236: new 113	java/lang/StringBuilder
    //   239: dup
    //   240: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   243: astore_1
    //   244: aload_1
    //   245: ldc 116
    //   247: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   250: pop
    //   251: aload_1
    //   252: invokestatic 123	android/app/ActivityThread:currentProcessName	()Ljava/lang/String;
    //   255: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: pop
    //   259: new 125	java/io/File
    //   262: dup
    //   263: aload 9
    //   265: aload_1
    //   266: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   269: invokespecial 131	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   272: astore 9
    //   274: goto +14 -> 288
    //   277: new 125	java/io/File
    //   280: dup
    //   281: ldc -123
    //   283: invokespecial 136	java/io/File:<init>	(Ljava/lang/String;)V
    //   286: astore 9
    //   288: iload 6
    //   290: ifeq +33 -> 323
    //   293: aload 9
    //   295: invokevirtual 140	java/io/File:exists	()Z
    //   298: ifne +12 -> 310
    //   301: aload 9
    //   303: invokevirtual 143	java/io/File:mkdirs	()Z
    //   306: pop
    //   307: goto +16 -> 323
    //   310: aload 9
    //   312: invokestatic 149	libcore/io/IoUtils:deleteContents	(Ljava/io/File;)V
    //   315: goto +8 -> 323
    //   318: astore_1
    //   319: aload_1
    //   320: invokevirtual 152	java/lang/Exception:printStackTrace	()V
    //   323: new 154	com/android/internal/util/FastPrintWriter
    //   326: dup
    //   327: new 156	java/io/FileOutputStream
    //   330: dup
    //   331: aload_0
    //   332: invokespecial 159	java/io/FileOutputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   335: invokespecial 162	com/android/internal/util/FastPrintWriter:<init>	(Ljava/io/OutputStream;)V
    //   338: astore 11
    //   340: lconst_0
    //   341: lstore 12
    //   343: iconst_0
    //   344: istore_2
    //   345: iload 7
    //   347: istore 8
    //   349: iload 5
    //   351: istore 8
    //   353: iload 4
    //   355: istore 8
    //   357: getstatic 33	miui/util/DumpBitmapInfoUtils:sBitmapTitles	Ljava/util/WeakHashMap;
    //   360: astore 14
    //   362: iload 7
    //   364: istore 8
    //   366: iload 5
    //   368: istore 8
    //   370: iload 4
    //   372: istore 8
    //   374: aload 14
    //   376: monitorenter
    //   377: new 164	java/util/ArrayList
    //   380: astore_0
    //   381: aload_0
    //   382: invokespecial 165	java/util/ArrayList:<init>	()V
    //   385: getstatic 33	miui/util/DumpBitmapInfoUtils:sBitmapTitles	Ljava/util/WeakHashMap;
    //   388: invokevirtual 169	java/util/WeakHashMap:entrySet	()Ljava/util/Set;
    //   391: invokeinterface 175 1 0
    //   396: astore_1
    //   397: aload_1
    //   398: invokeinterface 180 1 0
    //   403: istore 15
    //   405: iload 15
    //   407: ifeq +51 -> 458
    //   410: aload_1
    //   411: invokeinterface 184 1 0
    //   416: checkcast 186	java/util/Map$Entry
    //   419: astore 16
    //   421: new 188	java/util/AbstractMap$SimpleEntry
    //   424: astore 17
    //   426: aload 17
    //   428: aload 16
    //   430: invokespecial 191	java/util/AbstractMap$SimpleEntry:<init>	(Ljava/util/Map$Entry;)V
    //   433: aload 17
    //   435: invokevirtual 194	java/util/AbstractMap$SimpleEntry:getKey	()Ljava/lang/Object;
    //   438: ifnull +13 -> 451
    //   441: aload_0
    //   442: aload 17
    //   444: invokevirtual 197	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   447: pop
    //   448: goto +3 -> 451
    //   451: goto -54 -> 397
    //   454: astore_0
    //   455: goto +691 -> 1146
    //   458: aload 14
    //   460: monitorexit
    //   461: iload 7
    //   463: istore 8
    //   465: iload 5
    //   467: istore 8
    //   469: iload 4
    //   471: istore 8
    //   473: new 6	miui/util/DumpBitmapInfoUtils$1
    //   476: astore_1
    //   477: iload 7
    //   479: istore 8
    //   481: iload 5
    //   483: istore 8
    //   485: iload 4
    //   487: istore 8
    //   489: aload_1
    //   490: invokespecial 198	miui/util/DumpBitmapInfoUtils$1:<init>	()V
    //   493: iload 7
    //   495: istore 8
    //   497: iload 5
    //   499: istore 8
    //   501: iload 4
    //   503: istore 8
    //   505: aload_0
    //   506: aload_1
    //   507: invokestatic 204	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   510: iload 7
    //   512: istore 8
    //   514: iload 5
    //   516: istore 8
    //   518: iload 4
    //   520: istore 8
    //   522: aload 11
    //   524: ldc -50
    //   526: iconst_1
    //   527: anewarray 4	java/lang/Object
    //   530: dup
    //   531: iconst_0
    //   532: getstatic 208	miui/util/DumpBitmapInfoUtils:sBitmapThresholdSize	I
    //   535: invokestatic 212	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   538: aastore
    //   539: invokevirtual 218	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   542: pop
    //   543: iload 7
    //   545: istore 8
    //   547: iload 5
    //   549: istore 8
    //   551: iload 4
    //   553: istore 8
    //   555: invokestatic 224	android/content/res/Resources:getSystem	()Landroid/content/res/Resources;
    //   558: invokevirtual 228	android/content/res/Resources:getPreloadedDrawables	()Landroid/util/LongSparseArray;
    //   561: invokevirtual 233	android/util/LongSparseArray:clone	()Landroid/util/LongSparseArray;
    //   564: astore 16
    //   566: iload 7
    //   568: istore 8
    //   570: iload 5
    //   572: istore 8
    //   574: iload 4
    //   576: istore 8
    //   578: aload_0
    //   579: invokevirtual 234	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   582: astore 17
    //   584: iload 4
    //   586: istore 10
    //   588: iload 7
    //   590: istore 4
    //   592: iload 4
    //   594: istore 8
    //   596: iload 5
    //   598: istore 8
    //   600: iload 10
    //   602: istore 8
    //   604: aload 17
    //   606: invokeinterface 180 1 0
    //   611: ifeq +395 -> 1006
    //   614: iload 4
    //   616: istore 8
    //   618: iload 5
    //   620: istore 8
    //   622: iload 10
    //   624: istore 8
    //   626: aload 17
    //   628: invokeinterface 184 1 0
    //   633: checkcast 186	java/util/Map$Entry
    //   636: astore 18
    //   638: iload 4
    //   640: istore 8
    //   642: iload 5
    //   644: istore 8
    //   646: iload 10
    //   648: istore 8
    //   650: aload 18
    //   652: invokeinterface 235 1 0
    //   657: checkcast 237	android/graphics/Bitmap
    //   660: astore_1
    //   661: iload 4
    //   663: istore 8
    //   665: iload 5
    //   667: istore 8
    //   669: iload 10
    //   671: istore 8
    //   673: aload_1
    //   674: invokevirtual 240	android/graphics/Bitmap:isRecycled	()Z
    //   677: ifeq +6 -> 683
    //   680: goto +92 -> 772
    //   683: iconst_0
    //   684: istore 15
    //   686: iconst_0
    //   687: istore 7
    //   689: iload 4
    //   691: istore 8
    //   693: iload 5
    //   695: istore 8
    //   697: iload 10
    //   699: istore 8
    //   701: aload 16
    //   703: invokevirtual 243	android/util/LongSparseArray:size	()I
    //   706: istore 8
    //   708: iload 7
    //   710: iload 8
    //   712: if_icmpge +47 -> 759
    //   715: aload 16
    //   717: iload 7
    //   719: invokevirtual 247	android/util/LongSparseArray:valueAt	(I)Ljava/lang/Object;
    //   722: checkcast 249	android/graphics/drawable/Drawable$ConstantState
    //   725: invokestatic 253	miui/util/DumpBitmapInfoUtils:getBitmapFromDrawableState	(Landroid/graphics/drawable/Drawable$ConstantState;)Landroid/graphics/Bitmap;
    //   728: astore 19
    //   730: aload_1
    //   731: astore 14
    //   733: aload 19
    //   735: aload 14
    //   737: if_acmpne +9 -> 746
    //   740: iconst_1
    //   741: istore 15
    //   743: goto +16 -> 759
    //   746: iinc 7 1
    //   749: aload 14
    //   751: astore_1
    //   752: goto -63 -> 689
    //   755: astore_0
    //   756: goto +404 -> 1160
    //   759: aload_1
    //   760: astore 14
    //   762: iload 15
    //   764: ifeq +11 -> 775
    //   767: iload 10
    //   769: ifne +6 -> 775
    //   772: goto -180 -> 592
    //   775: aload 14
    //   777: invokevirtual 256	android/graphics/Bitmap:getByteCount	()I
    //   780: istore 7
    //   782: lload 12
    //   784: iload 7
    //   786: i2l
    //   787: ladd
    //   788: lstore 12
    //   790: iinc 2 1
    //   793: aload 14
    //   795: aload 18
    //   797: invokeinterface 259 1 0
    //   802: checkcast 261	java/lang/CharSequence
    //   805: iconst_0
    //   806: iload 15
    //   808: invokestatic 265	miui/util/DumpBitmapInfoUtils:getBitmapMsg	(Landroid/graphics/Bitmap;Ljava/lang/CharSequence;ZZ)Ljava/lang/String;
    //   811: astore 19
    //   813: new 113	java/lang/StringBuilder
    //   816: astore_1
    //   817: aload_1
    //   818: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   821: aload_1
    //   822: ldc_w 267
    //   825: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   828: pop
    //   829: aload_1
    //   830: aload 19
    //   832: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   835: pop
    //   836: aload 11
    //   838: aload_1
    //   839: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   842: invokevirtual 270	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   845: iload 6
    //   847: ifeq +119 -> 966
    //   850: new 113	java/lang/StringBuilder
    //   853: astore_1
    //   854: aload_1
    //   855: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   858: aload_1
    //   859: iload_2
    //   860: invokevirtual 273	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   863: pop
    //   864: aload_1
    //   865: ldc_w 275
    //   868: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   871: pop
    //   872: aload 18
    //   874: invokeinterface 259 1 0
    //   879: checkcast 261	java/lang/CharSequence
    //   882: astore 18
    //   884: aload_1
    //   885: aload 14
    //   887: aload 18
    //   889: iconst_1
    //   890: iload 15
    //   892: invokestatic 265	miui/util/DumpBitmapInfoUtils:getBitmapMsg	(Landroid/graphics/Bitmap;Ljava/lang/CharSequence;ZZ)Ljava/lang/String;
    //   895: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   898: pop
    //   899: aload_1
    //   900: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   903: astore 18
    //   905: new 156	java/io/FileOutputStream
    //   908: astore_1
    //   909: new 125	java/io/File
    //   912: astore 19
    //   914: aload 19
    //   916: aload 9
    //   918: aload 18
    //   920: invokespecial 131	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   923: aload_1
    //   924: aload 19
    //   926: invokespecial 277	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   929: aload 14
    //   931: getstatic 283	android/graphics/Bitmap$CompressFormat:PNG	Landroid/graphics/Bitmap$CompressFormat;
    //   934: bipush 100
    //   936: aload_1
    //   937: invokevirtual 287	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   940: pop
    //   941: aload_1
    //   942: invokevirtual 290	java/io/FileOutputStream:close	()V
    //   945: goto +21 -> 966
    //   948: astore_1
    //   949: goto +4 -> 953
    //   952: astore_1
    //   953: aload_1
    //   954: aload 11
    //   956: invokevirtual 293	java/lang/Exception:printStackTrace	(Ljava/io/PrintWriter;)V
    //   959: aload_1
    //   960: invokevirtual 152	java/lang/Exception:printStackTrace	()V
    //   963: goto +3 -> 966
    //   966: iload_3
    //   967: ifeq +25 -> 992
    //   970: aload 14
    //   972: invokevirtual 296	java/lang/Object:hashCode	()I
    //   975: iload_3
    //   976: if_icmpne +16 -> 992
    //   979: aload 14
    //   981: invokevirtual 299	android/graphics/Bitmap:recycle	()V
    //   984: aload 11
    //   986: ldc_w 301
    //   989: invokevirtual 270	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   992: aload 11
    //   994: bipush 10
    //   996: invokevirtual 304	java/io/PrintWriter:print	(C)V
    //   999: goto -407 -> 592
    //   1002: astore_0
    //   1003: goto +157 -> 1160
    //   1006: aload 11
    //   1008: ldc_w 306
    //   1011: iconst_2
    //   1012: anewarray 4	java/lang/Object
    //   1015: dup
    //   1016: iconst_0
    //   1017: iload_2
    //   1018: invokestatic 212	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   1021: aastore
    //   1022: dup
    //   1023: iconst_1
    //   1024: lload 12
    //   1026: ldc2_w 307
    //   1029: ldiv
    //   1030: ldc2_w 307
    //   1033: ldiv
    //   1034: invokestatic 313	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1037: aastore
    //   1038: invokevirtual 218	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   1041: pop
    //   1042: iload 6
    //   1044: ifeq +82 -> 1126
    //   1047: new 113	java/lang/StringBuilder
    //   1050: astore_0
    //   1051: aload_0
    //   1052: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   1055: aload_0
    //   1056: ldc_w 315
    //   1059: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1062: pop
    //   1063: aload_0
    //   1064: aload 9
    //   1066: invokevirtual 318	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1069: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1072: pop
    //   1073: aload_0
    //   1074: ldc_w 320
    //   1077: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1080: pop
    //   1081: aload 11
    //   1083: aload_0
    //   1084: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1087: invokevirtual 270	java/io/PrintWriter:print	(Ljava/lang/String;)V
    //   1090: new 113	java/lang/StringBuilder
    //   1093: astore_0
    //   1094: aload_0
    //   1095: invokespecial 114	java/lang/StringBuilder:<init>	()V
    //   1098: aload_0
    //   1099: ldc_w 322
    //   1102: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1105: pop
    //   1106: aload_0
    //   1107: aload 9
    //   1109: invokevirtual 318	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1112: invokevirtual 120	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1115: pop
    //   1116: ldc 18
    //   1118: aload_0
    //   1119: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1122: invokestatic 328	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1125: pop
    //   1126: aload 11
    //   1128: ldc_w 320
    //   1131: iconst_0
    //   1132: anewarray 4	java/lang/Object
    //   1135: invokevirtual 218	java/io/PrintWriter:printf	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintWriter;
    //   1138: pop
    //   1139: aload 11
    //   1141: invokevirtual 331	java/io/PrintWriter:flush	()V
    //   1144: return
    //   1145: astore_0
    //   1146: aload 14
    //   1148: monitorexit
    //   1149: aload_0
    //   1150: athrow
    //   1151: astore_0
    //   1152: goto +8 -> 1160
    //   1155: astore_0
    //   1156: goto -10 -> 1146
    //   1159: astore_0
    //   1160: aload 11
    //   1162: invokevirtual 331	java/io/PrintWriter:flush	()V
    //   1165: aload_0
    //   1166: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1167	0	paramFileDescriptor	java.io.FileDescriptor
    //   0	1167	1	paramArrayOfString	String[]
    //   9	1009	2	i	int
    //   11	966	3	j	int
    //   13	677	4	k	int
    //   16	678	5	m	int
    //   19	1024	6	n	int
    //   22	763	7	i1	int
    //   25	688	8	i2	int
    //   37	1071	9	localObject1	Object
    //   112	656	10	i3	int
    //   338	823	11	localFastPrintWriter	com.android.internal.util.FastPrintWriter
    //   341	684	12	l	long
    //   360	787	14	localObject2	Object
    //   403	488	15	bool	boolean
    //   419	297	16	localObject3	Object
    //   424	203	17	localObject4	Object
    //   636	283	18	localObject5	Object
    //   728	197	19	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   310	315	318	java/lang/Exception
    //   410	448	454	finally
    //   715	730	755	finally
    //   884	945	948	java/lang/Exception
    //   850	884	952	java/lang/Exception
    //   775	782	1002	finally
    //   377	385	1145	finally
    //   385	397	1145	finally
    //   397	405	1145	finally
    //   458	461	1145	finally
    //   793	845	1151	finally
    //   850	884	1151	finally
    //   884	945	1151	finally
    //   953	963	1151	finally
    //   970	992	1151	finally
    //   992	999	1151	finally
    //   1006	1042	1151	finally
    //   1047	1126	1151	finally
    //   1126	1139	1151	finally
    //   1149	1151	1151	finally
    //   1146	1149	1155	finally
    //   357	362	1159	finally
    //   374	377	1159	finally
    //   473	477	1159	finally
    //   489	493	1159	finally
    //   505	510	1159	finally
    //   522	543	1159	finally
    //   555	566	1159	finally
    //   578	584	1159	finally
    //   604	614	1159	finally
    //   626	638	1159	finally
    //   650	661	1159	finally
    //   673	680	1159	finally
    //   701	708	1159	finally
  }
  
  private static String formatMsg(Bitmap paramBitmap, CharSequence paramCharSequence, boolean paramBoolean)
  {
    int i = paramBitmap.hashCode();
    int j = paramBitmap.getByteCount() / 1024;
    int k = paramBitmap.getWidth();
    int m = paramBitmap.getHeight();
    String str = "";
    if (paramBoolean) {
      paramBitmap = "preload";
    } else {
      paramBitmap = "";
    }
    if (paramCharSequence == null) {
      paramCharSequence = str;
    } else {
      paramCharSequence = paramCharSequence.toString();
    }
    return String.format("0x%8x %,6dk %dx%d %s %s", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(m), paramBitmap, paramCharSequence });
  }
  
  private static Bitmap getBitmapFromDrawableState(Drawable.ConstantState paramConstantState)
  {
    try
    {
      Object localObject = paramConstantState.getClass().getSimpleName();
      if (((String)localObject).equals("BitmapState"))
      {
        localObject = paramConstantState.getClass().getDeclaredField("mBitmap");
        ((Field)localObject).setAccessible(true);
        return (Bitmap)((Field)localObject).get(paramConstantState);
      }
      if (((String)localObject).equals("NinePatchState"))
      {
        localObject = paramConstantState.getClass().getDeclaredField("mNinePatch");
        ((Field)localObject).setAccessible(true);
        paramConstantState = (NinePatch)((Field)localObject).get(paramConstantState);
        if (paramConstantState == null) {
          return null;
        }
        paramConstantState = paramConstantState.getBitmap();
        return paramConstantState;
      }
      return null;
    }
    finally
    {
      paramConstantState.printStackTrace();
    }
    return null;
  }
  
  private static String getBitmapMsg(Bitmap paramBitmap, CharSequence paramCharSequence, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str1 = formatMsg(paramBitmap, paramCharSequence, paramBoolean2);
    if (!paramBoolean1) {
      return str1;
    }
    int i = str1.length() - 240;
    String str2 = str1;
    if (i > 0)
    {
      str2 = str1;
      if (paramCharSequence != null)
      {
        str2 = str1;
        if (paramCharSequence.length() > i) {
          str2 = formatMsg(paramBitmap, paramCharSequence.toString().substring(i), paramBoolean2);
        }
      }
    }
    paramBitmap = new StringBuilder();
    paramBitmap.append(str2.replace(' ', '_').replace('\\', '-').replace('/', '-'));
    paramBitmap.append(".png");
    return paramBitmap.toString();
  }
  
  private static boolean isTrackingNeeded(Bitmap paramBitmap)
  {
    if (sCurrProcess != Process.myPid())
    {
      sBitmapThresholdSize = SystemProperties.getInt("debug.bitmap_threshold_size", 100);
      sCurrProcess = Process.myPid();
    }
    boolean bool;
    if (paramBitmap.getWidth() * paramBitmap.getHeight() / 256 >= sBitmapThresholdSize) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static void putBitmap(Bitmap paramBitmap, CharSequence paramCharSequence)
  {
    if (!ENABLE) {
      return;
    }
    if (paramBitmap == null) {
      return;
    }
    try
    {
      if (!isTrackingNeeded(paramBitmap)) {
        return;
      }
      synchronized (sBitmapTitles)
      {
        sBitmapTitles.put(paramBitmap, paramCharSequence);
      }
      return;
    }
    finally
    {
      paramBitmap.printStackTrace();
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/DumpBitmapInfoUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */