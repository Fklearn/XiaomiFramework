package org.egret.plugin.mi.android.util.launcher;

import android.util.Log;
import java.io.File;

public class ZipClass
{
  private static final int BUFFER_SIZE = 1024;
  private static final String TAG = "ZipClass";
  
  /* Error */
  private boolean doUnzip(File paramFile1, File paramFile2, OnZipListener paramOnZipListener)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +1833 -> 1834
    //   4: aload_2
    //   5: ifnonnull +6 -> 11
    //   8: goto +1826 -> 1834
    //   11: aload_2
    //   12: invokevirtual 29	java/io/File:exists	()Z
    //   15: istore 4
    //   17: ldc 31
    //   19: astore 5
    //   21: iload 4
    //   23: ifne +84 -> 107
    //   26: aload_2
    //   27: invokevirtual 34	java/io/File:mkdirs	()Z
    //   30: ifne +77 -> 107
    //   33: new 36	java/lang/StringBuilder
    //   36: dup
    //   37: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   40: astore_1
    //   41: aload_1
    //   42: ldc 31
    //   44: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   47: pop
    //   48: aload_1
    //   49: aload_2
    //   50: invokevirtual 45	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   53: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: ldc 14
    //   59: aload_1
    //   60: invokevirtual 48	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   63: invokestatic 54	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   66: pop
    //   67: aload_3
    //   68: ifnull +37 -> 105
    //   71: new 36	java/lang/StringBuilder
    //   74: dup
    //   75: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   78: astore_1
    //   79: aload_1
    //   80: ldc 31
    //   82: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   85: pop
    //   86: aload_1
    //   87: aload_2
    //   88: invokevirtual 45	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   91: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   94: pop
    //   95: aload_3
    //   96: aload_1
    //   97: invokevirtual 48	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   100: invokeinterface 58 2 0
    //   105: iconst_0
    //   106: ireturn
    //   107: aconst_null
    //   108: astore 6
    //   110: aconst_null
    //   111: astore 7
    //   113: aconst_null
    //   114: astore 8
    //   116: aconst_null
    //   117: astore 9
    //   119: aconst_null
    //   120: astore 10
    //   122: aconst_null
    //   123: astore 11
    //   125: aconst_null
    //   126: astore 12
    //   128: aconst_null
    //   129: astore 13
    //   131: aconst_null
    //   132: astore 14
    //   134: aconst_null
    //   135: astore 15
    //   137: aconst_null
    //   138: astore 16
    //   140: aconst_null
    //   141: astore 17
    //   143: aload 7
    //   145: astore 18
    //   147: aload 10
    //   149: astore 19
    //   151: aload 15
    //   153: astore 20
    //   155: aload 6
    //   157: astore 21
    //   159: aload 11
    //   161: astore 22
    //   163: aload 16
    //   165: astore 23
    //   167: new 60	java/util/zip/ZipFile
    //   170: astore 24
    //   172: aload 7
    //   174: astore 18
    //   176: aload 10
    //   178: astore 19
    //   180: aload 15
    //   182: astore 20
    //   184: aload 6
    //   186: astore 21
    //   188: aload 11
    //   190: astore 22
    //   192: aload 16
    //   194: astore 23
    //   196: aload 24
    //   198: aload_1
    //   199: invokespecial 63	java/util/zip/ZipFile:<init>	(Ljava/io/File;)V
    //   202: aload 24
    //   204: astore 23
    //   206: aload 8
    //   208: astore 18
    //   210: aload 13
    //   212: astore 20
    //   214: aload 24
    //   216: astore 22
    //   218: aload 9
    //   220: astore 21
    //   222: aload 14
    //   224: astore 19
    //   226: aload 24
    //   228: invokevirtual 67	java/util/zip/ZipFile:size	()I
    //   231: istore 25
    //   233: iconst_0
    //   234: istore 26
    //   236: aload 24
    //   238: astore 23
    //   240: aload 8
    //   242: astore 18
    //   244: aload 13
    //   246: astore 20
    //   248: aload 24
    //   250: astore 22
    //   252: aload 9
    //   254: astore 21
    //   256: aload 14
    //   258: astore 19
    //   260: aload 24
    //   262: invokevirtual 71	java/util/zip/ZipFile:entries	()Ljava/util/Enumeration;
    //   265: astore 8
    //   267: aload 24
    //   269: astore 23
    //   271: aload 12
    //   273: astore 18
    //   275: aload 17
    //   277: astore 20
    //   279: aload 24
    //   281: astore 22
    //   283: aload 12
    //   285: astore 21
    //   287: aload 17
    //   289: astore 19
    //   291: aload 8
    //   293: invokeinterface 76 1 0
    //   298: istore 4
    //   300: iload 4
    //   302: ifeq +1067 -> 1369
    //   305: aload_3
    //   306: ifnull +113 -> 419
    //   309: aload 24
    //   311: astore 18
    //   313: aload 12
    //   315: astore 19
    //   317: aload 17
    //   319: astore 20
    //   321: aload 24
    //   323: astore 21
    //   325: aload 12
    //   327: astore 22
    //   329: aload 17
    //   331: astore 23
    //   333: invokestatic 82	org/egret/plugin/mi/android/util/launcher/ExecutorLab:getInstance	()Lorg/egret/plugin/mi/android/util/launcher/ExecutorLab;
    //   336: invokevirtual 85	org/egret/plugin/mi/android/util/launcher/ExecutorLab:isRunning	()Z
    //   339: ifne +80 -> 419
    //   342: aload 24
    //   344: astore 18
    //   346: aload 12
    //   348: astore 19
    //   350: aload 17
    //   352: astore 20
    //   354: aload 24
    //   356: astore 21
    //   358: aload 12
    //   360: astore 22
    //   362: aload 17
    //   364: astore 23
    //   366: aload_3
    //   367: ldc 87
    //   369: invokeinterface 58 2 0
    //   374: aload 24
    //   376: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   379: aload 12
    //   381: ifnull +8 -> 389
    //   384: aload 12
    //   386: invokevirtual 93	java/io/InputStream:close	()V
    //   389: aload 17
    //   391: ifnull +8 -> 399
    //   394: aload 17
    //   396: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   399: goto +18 -> 417
    //   402: astore_1
    //   403: aload_1
    //   404: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   407: aload_3
    //   408: aload_1
    //   409: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   412: invokeinterface 58 2 0
    //   417: iconst_0
    //   418: ireturn
    //   419: iload 26
    //   421: iconst_1
    //   422: iadd
    //   423: istore 27
    //   425: aload_3
    //   426: ifnull +37 -> 463
    //   429: aload 24
    //   431: astore 18
    //   433: aload 12
    //   435: astore 19
    //   437: aload 17
    //   439: astore 20
    //   441: aload 24
    //   443: astore 21
    //   445: aload 12
    //   447: astore 22
    //   449: aload 17
    //   451: astore 23
    //   453: aload_3
    //   454: iload 27
    //   456: iload 25
    //   458: invokeinterface 104 3 0
    //   463: aload 24
    //   465: astore 23
    //   467: aload 12
    //   469: astore 18
    //   471: aload 17
    //   473: astore 20
    //   475: aload 24
    //   477: astore 22
    //   479: aload 12
    //   481: astore 21
    //   483: aload 17
    //   485: astore 19
    //   487: aload 8
    //   489: invokeinterface 108 1 0
    //   494: checkcast 110	java/util/zip/ZipEntry
    //   497: astore 13
    //   499: aload 24
    //   501: astore 23
    //   503: aload 12
    //   505: astore 18
    //   507: aload 17
    //   509: astore 20
    //   511: aload 24
    //   513: astore 22
    //   515: aload 12
    //   517: astore 21
    //   519: aload 17
    //   521: astore 19
    //   523: new 25	java/io/File
    //   526: astore 9
    //   528: aload 24
    //   530: astore 23
    //   532: aload 12
    //   534: astore 18
    //   536: aload 17
    //   538: astore 20
    //   540: aload 24
    //   542: astore 22
    //   544: aload 12
    //   546: astore 21
    //   548: aload 17
    //   550: astore 19
    //   552: aload 9
    //   554: aload_2
    //   555: aload 13
    //   557: invokevirtual 113	java/util/zip/ZipEntry:getName	()Ljava/lang/String;
    //   560: invokespecial 116	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   563: aload 24
    //   565: astore 23
    //   567: aload 12
    //   569: astore 18
    //   571: aload 17
    //   573: astore 20
    //   575: aload 24
    //   577: astore 22
    //   579: aload 12
    //   581: astore 21
    //   583: aload 17
    //   585: astore 19
    //   587: aload 13
    //   589: invokevirtual 119	java/util/zip/ZipEntry:isDirectory	()Z
    //   592: istore 4
    //   594: iload 4
    //   596: ifeq +402 -> 998
    //   599: iload 27
    //   601: istore 26
    //   603: aload 24
    //   605: astore 18
    //   607: aload 12
    //   609: astore 19
    //   611: aload 17
    //   613: astore 20
    //   615: aload 24
    //   617: astore 21
    //   619: aload 12
    //   621: astore 22
    //   623: aload 17
    //   625: astore 23
    //   627: aload 9
    //   629: invokevirtual 34	java/io/File:mkdirs	()Z
    //   632: ifne -365 -> 267
    //   635: aload 24
    //   637: astore 18
    //   639: aload 12
    //   641: astore 19
    //   643: aload 17
    //   645: astore 20
    //   647: aload 24
    //   649: astore 21
    //   651: aload 12
    //   653: astore 22
    //   655: aload 17
    //   657: astore 23
    //   659: new 36	java/lang/StringBuilder
    //   662: astore_1
    //   663: aload 24
    //   665: astore 18
    //   667: aload 12
    //   669: astore 19
    //   671: aload 17
    //   673: astore 20
    //   675: aload 24
    //   677: astore 21
    //   679: aload 12
    //   681: astore 22
    //   683: aload 17
    //   685: astore 23
    //   687: aload_1
    //   688: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   691: aload 24
    //   693: astore 18
    //   695: aload 12
    //   697: astore 19
    //   699: aload 17
    //   701: astore 20
    //   703: aload 24
    //   705: astore 21
    //   707: aload 12
    //   709: astore 22
    //   711: aload 17
    //   713: astore 23
    //   715: aload_1
    //   716: aload 5
    //   718: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   721: pop
    //   722: aload 24
    //   724: astore 18
    //   726: aload 12
    //   728: astore 19
    //   730: aload 17
    //   732: astore 20
    //   734: aload 24
    //   736: astore 21
    //   738: aload 12
    //   740: astore 22
    //   742: aload 17
    //   744: astore 23
    //   746: aload_1
    //   747: aload 9
    //   749: invokevirtual 45	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   752: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   755: pop
    //   756: aload 24
    //   758: astore 18
    //   760: aload 12
    //   762: astore 19
    //   764: aload 17
    //   766: astore 20
    //   768: aload 24
    //   770: astore 21
    //   772: aload 12
    //   774: astore 22
    //   776: aload 17
    //   778: astore 23
    //   780: ldc 14
    //   782: aload_1
    //   783: invokevirtual 48	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   786: invokestatic 54	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   789: pop
    //   790: aload_3
    //   791: ifnull +158 -> 949
    //   794: aload 24
    //   796: astore 18
    //   798: aload 12
    //   800: astore 19
    //   802: aload 17
    //   804: astore 20
    //   806: aload 24
    //   808: astore 21
    //   810: aload 12
    //   812: astore 22
    //   814: aload 17
    //   816: astore 23
    //   818: new 36	java/lang/StringBuilder
    //   821: astore_1
    //   822: aload 24
    //   824: astore 18
    //   826: aload 12
    //   828: astore 19
    //   830: aload 17
    //   832: astore 20
    //   834: aload 24
    //   836: astore 21
    //   838: aload 12
    //   840: astore 22
    //   842: aload 17
    //   844: astore 23
    //   846: aload_1
    //   847: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   850: aload 24
    //   852: astore 18
    //   854: aload 12
    //   856: astore 19
    //   858: aload 17
    //   860: astore 20
    //   862: aload 24
    //   864: astore 21
    //   866: aload 12
    //   868: astore 22
    //   870: aload 17
    //   872: astore 23
    //   874: aload_1
    //   875: aload 5
    //   877: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   880: pop
    //   881: aload 24
    //   883: astore 18
    //   885: aload 12
    //   887: astore 19
    //   889: aload 17
    //   891: astore 20
    //   893: aload 24
    //   895: astore 21
    //   897: aload 12
    //   899: astore 22
    //   901: aload 17
    //   903: astore 23
    //   905: aload_1
    //   906: aload 9
    //   908: invokevirtual 45	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   911: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   914: pop
    //   915: aload 24
    //   917: astore 18
    //   919: aload 12
    //   921: astore 19
    //   923: aload 17
    //   925: astore 20
    //   927: aload 24
    //   929: astore 21
    //   931: aload 12
    //   933: astore 22
    //   935: aload 17
    //   937: astore 23
    //   939: aload_3
    //   940: aload_1
    //   941: invokevirtual 48	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   944: invokeinterface 58 2 0
    //   949: aload 24
    //   951: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   954: aload 12
    //   956: ifnull +8 -> 964
    //   959: aload 12
    //   961: invokevirtual 93	java/io/InputStream:close	()V
    //   964: aload 17
    //   966: ifnull +8 -> 974
    //   969: aload 17
    //   971: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   974: goto +22 -> 996
    //   977: astore_1
    //   978: aload_1
    //   979: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   982: aload_3
    //   983: ifnull +13 -> 996
    //   986: aload_3
    //   987: aload_1
    //   988: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   991: invokeinterface 58 2 0
    //   996: iconst_0
    //   997: ireturn
    //   998: aload 13
    //   1000: invokevirtual 123	java/util/zip/ZipEntry:getSize	()J
    //   1003: lstore 28
    //   1005: lload 28
    //   1007: l2i
    //   1008: istore 30
    //   1010: iconst_0
    //   1011: istore 26
    //   1013: aload 24
    //   1015: astore 22
    //   1017: aload 12
    //   1019: astore 21
    //   1021: aload 17
    //   1023: astore 18
    //   1025: aload 12
    //   1027: astore 19
    //   1029: aload 17
    //   1031: astore 20
    //   1033: aload 22
    //   1035: aload 13
    //   1037: invokevirtual 127	java/util/zip/ZipFile:getInputStream	(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;
    //   1040: astore 12
    //   1042: aload 12
    //   1044: astore 21
    //   1046: aload 17
    //   1048: astore 18
    //   1050: aload 12
    //   1052: astore 19
    //   1054: aload 17
    //   1056: astore 20
    //   1058: new 95	java/io/FileOutputStream
    //   1061: astore 23
    //   1063: aload 12
    //   1065: astore 21
    //   1067: aload 17
    //   1069: astore 18
    //   1071: aload 12
    //   1073: astore 19
    //   1075: aload 17
    //   1077: astore 20
    //   1079: aload 23
    //   1081: aload 9
    //   1083: invokespecial 128	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   1086: aload 23
    //   1088: astore 17
    //   1090: aload 12
    //   1092: astore 21
    //   1094: aload 17
    //   1096: astore 18
    //   1098: aload 12
    //   1100: astore 19
    //   1102: aload 17
    //   1104: astore 20
    //   1106: sipush 1024
    //   1109: newarray <illegal type>
    //   1111: astore 23
    //   1113: aload 12
    //   1115: astore 21
    //   1117: aload 17
    //   1119: astore 18
    //   1121: aload 12
    //   1123: astore 19
    //   1125: aload 17
    //   1127: astore 20
    //   1129: aload 12
    //   1131: aload 23
    //   1133: iconst_0
    //   1134: sipush 1024
    //   1137: invokevirtual 132	java/io/InputStream:read	([BII)I
    //   1140: istore 31
    //   1142: iload 31
    //   1144: iconst_m1
    //   1145: if_icmpeq +157 -> 1302
    //   1148: aload_3
    //   1149: ifnull +87 -> 1236
    //   1152: aload 12
    //   1154: astore 21
    //   1156: aload 17
    //   1158: astore 18
    //   1160: aload 12
    //   1162: astore 19
    //   1164: aload 17
    //   1166: astore 20
    //   1168: invokestatic 82	org/egret/plugin/mi/android/util/launcher/ExecutorLab:getInstance	()Lorg/egret/plugin/mi/android/util/launcher/ExecutorLab;
    //   1171: invokevirtual 85	org/egret/plugin/mi/android/util/launcher/ExecutorLab:isRunning	()Z
    //   1174: ifne +62 -> 1236
    //   1177: aload 12
    //   1179: astore 21
    //   1181: aload 17
    //   1183: astore 18
    //   1185: aload 12
    //   1187: astore 19
    //   1189: aload 17
    //   1191: astore 20
    //   1193: aload_3
    //   1194: ldc 87
    //   1196: invokeinterface 58 2 0
    //   1201: aload 22
    //   1203: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   1206: aload 12
    //   1208: invokevirtual 93	java/io/InputStream:close	()V
    //   1211: aload 17
    //   1213: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   1216: goto +18 -> 1234
    //   1219: astore_1
    //   1220: aload_1
    //   1221: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   1224: aload_3
    //   1225: aload_1
    //   1226: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   1229: invokeinterface 58 2 0
    //   1234: iconst_0
    //   1235: ireturn
    //   1236: aload 12
    //   1238: astore 21
    //   1240: aload 17
    //   1242: astore 18
    //   1244: aload 12
    //   1246: astore 19
    //   1248: aload 17
    //   1250: astore 20
    //   1252: aload 17
    //   1254: aload 23
    //   1256: iconst_0
    //   1257: iload 31
    //   1259: invokevirtual 136	java/io/FileOutputStream:write	([BII)V
    //   1262: iload 26
    //   1264: iload 31
    //   1266: iadd
    //   1267: istore 26
    //   1269: aload_3
    //   1270: ifnull +29 -> 1299
    //   1273: aload 12
    //   1275: astore 21
    //   1277: aload 17
    //   1279: astore 18
    //   1281: aload 12
    //   1283: astore 19
    //   1285: aload 17
    //   1287: astore 20
    //   1289: aload_3
    //   1290: iload 26
    //   1292: iload 30
    //   1294: invokeinterface 139 3 0
    //   1299: goto -186 -> 1113
    //   1302: aload 12
    //   1304: astore 21
    //   1306: aload 17
    //   1308: astore 18
    //   1310: aload 12
    //   1312: astore 19
    //   1314: aload 17
    //   1316: astore 20
    //   1318: aload 17
    //   1320: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   1323: aload 12
    //   1325: astore 21
    //   1327: aload 17
    //   1329: astore 18
    //   1331: aload 12
    //   1333: astore 19
    //   1335: aload 17
    //   1337: astore 20
    //   1339: aload 12
    //   1341: invokevirtual 93	java/io/InputStream:close	()V
    //   1344: aload 22
    //   1346: astore 24
    //   1348: iload 27
    //   1350: istore 26
    //   1352: goto -1085 -> 267
    //   1355: astore_1
    //   1356: aload 24
    //   1358: astore_2
    //   1359: goto +414 -> 1773
    //   1362: astore_2
    //   1363: aload 24
    //   1365: astore_1
    //   1366: goto +306 -> 1672
    //   1369: aload 24
    //   1371: astore_2
    //   1372: aload 12
    //   1374: astore 21
    //   1376: aload 17
    //   1378: astore 18
    //   1380: aload 12
    //   1382: astore 19
    //   1384: aload 17
    //   1386: astore 20
    //   1388: aload_2
    //   1389: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   1392: aload 12
    //   1394: astore 21
    //   1396: aload 17
    //   1398: astore 18
    //   1400: aload 12
    //   1402: astore 19
    //   1404: aload 17
    //   1406: astore 20
    //   1408: new 36	java/lang/StringBuilder
    //   1411: astore 5
    //   1413: aload 12
    //   1415: astore 21
    //   1417: aload 17
    //   1419: astore 18
    //   1421: aload 12
    //   1423: astore 19
    //   1425: aload 17
    //   1427: astore 20
    //   1429: aload 5
    //   1431: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   1434: aload 12
    //   1436: astore 21
    //   1438: aload 17
    //   1440: astore 18
    //   1442: aload 12
    //   1444: astore 19
    //   1446: aload 17
    //   1448: astore 20
    //   1450: aload 5
    //   1452: ldc -115
    //   1454: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1457: pop
    //   1458: aload 12
    //   1460: astore 21
    //   1462: aload 17
    //   1464: astore 18
    //   1466: aload 12
    //   1468: astore 19
    //   1470: aload 17
    //   1472: astore 20
    //   1474: aload 5
    //   1476: aload_1
    //   1477: invokevirtual 45	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1480: invokevirtual 41	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1483: pop
    //   1484: aload 12
    //   1486: astore 21
    //   1488: aload 17
    //   1490: astore 18
    //   1492: aload 12
    //   1494: astore 19
    //   1496: aload 17
    //   1498: astore 20
    //   1500: ldc 14
    //   1502: aload 5
    //   1504: invokevirtual 48	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1507: invokestatic 144	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
    //   1510: pop
    //   1511: aload_3
    //   1512: ifnull +25 -> 1537
    //   1515: aload 12
    //   1517: astore 21
    //   1519: aload 17
    //   1521: astore 18
    //   1523: aload 12
    //   1525: astore 19
    //   1527: aload 17
    //   1529: astore 20
    //   1531: aload_3
    //   1532: invokeinterface 147 1 0
    //   1537: aload_2
    //   1538: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   1541: aload 12
    //   1543: ifnull +8 -> 1551
    //   1546: aload 12
    //   1548: invokevirtual 93	java/io/InputStream:close	()V
    //   1551: aload 17
    //   1553: ifnull +8 -> 1561
    //   1556: aload 17
    //   1558: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   1561: goto +22 -> 1583
    //   1564: astore_1
    //   1565: aload_1
    //   1566: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   1569: aload_3
    //   1570: ifnull +13 -> 1583
    //   1573: aload_3
    //   1574: aload_1
    //   1575: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   1578: invokeinterface 58 2 0
    //   1583: iconst_1
    //   1584: ireturn
    //   1585: aload 24
    //   1587: astore_2
    //   1588: astore_1
    //   1589: aload 21
    //   1591: astore 12
    //   1593: aload 18
    //   1595: astore 17
    //   1597: goto +176 -> 1773
    //   1600: astore_2
    //   1601: aload 24
    //   1603: astore_1
    //   1604: aload 19
    //   1606: astore 12
    //   1608: aload 20
    //   1610: astore 17
    //   1612: goto +60 -> 1672
    //   1615: astore_1
    //   1616: aload 23
    //   1618: astore_2
    //   1619: aload 18
    //   1621: astore 12
    //   1623: aload 20
    //   1625: astore 17
    //   1627: goto +146 -> 1773
    //   1630: astore_2
    //   1631: aload 22
    //   1633: astore_1
    //   1634: aload 21
    //   1636: astore 12
    //   1638: aload 19
    //   1640: astore 17
    //   1642: goto +30 -> 1672
    //   1645: astore_1
    //   1646: aload 18
    //   1648: astore_2
    //   1649: aload 19
    //   1651: astore 12
    //   1653: aload 20
    //   1655: astore 17
    //   1657: goto +116 -> 1773
    //   1660: astore_2
    //   1661: aload 23
    //   1663: astore 17
    //   1665: aload 22
    //   1667: astore 12
    //   1669: aload 21
    //   1671: astore_1
    //   1672: aload_1
    //   1673: astore 18
    //   1675: aload 12
    //   1677: astore 19
    //   1679: aload 17
    //   1681: astore 20
    //   1683: aload_2
    //   1684: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   1687: aload_3
    //   1688: ifnull +24 -> 1712
    //   1691: aload_1
    //   1692: astore 18
    //   1694: aload 12
    //   1696: astore 19
    //   1698: aload 17
    //   1700: astore 20
    //   1702: aload_3
    //   1703: aload_2
    //   1704: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   1707: invokeinterface 58 2 0
    //   1712: aload_1
    //   1713: ifnull +14 -> 1727
    //   1716: aload_1
    //   1717: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   1720: goto +7 -> 1727
    //   1723: astore_1
    //   1724: goto +26 -> 1750
    //   1727: aload 12
    //   1729: ifnull +8 -> 1737
    //   1732: aload 12
    //   1734: invokevirtual 93	java/io/InputStream:close	()V
    //   1737: aload 17
    //   1739: ifnull +32 -> 1771
    //   1742: aload 17
    //   1744: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   1747: goto +24 -> 1771
    //   1750: aload_1
    //   1751: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   1754: aload_3
    //   1755: ifnull +16 -> 1771
    //   1758: aload_3
    //   1759: aload_1
    //   1760: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   1763: invokeinterface 58 2 0
    //   1768: goto +3 -> 1771
    //   1771: iconst_0
    //   1772: ireturn
    //   1773: aload_2
    //   1774: ifnull +14 -> 1788
    //   1777: aload_2
    //   1778: invokevirtual 90	java/util/zip/ZipFile:close	()V
    //   1781: goto +7 -> 1788
    //   1784: astore_2
    //   1785: goto +26 -> 1811
    //   1788: aload 12
    //   1790: ifnull +8 -> 1798
    //   1793: aload 12
    //   1795: invokevirtual 93	java/io/InputStream:close	()V
    //   1798: aload 17
    //   1800: ifnull +32 -> 1832
    //   1803: aload 17
    //   1805: invokevirtual 96	java/io/FileOutputStream:close	()V
    //   1808: goto +24 -> 1832
    //   1811: aload_2
    //   1812: invokevirtual 99	java/io/IOException:printStackTrace	()V
    //   1815: aload_3
    //   1816: ifnull +16 -> 1832
    //   1819: aload_3
    //   1820: aload_2
    //   1821: invokevirtual 100	java/io/IOException:toString	()Ljava/lang/String;
    //   1824: invokeinterface 58 2 0
    //   1829: goto +3 -> 1832
    //   1832: aload_1
    //   1833: athrow
    //   1834: ldc 14
    //   1836: ldc -107
    //   1838: invokestatic 54	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1841: pop
    //   1842: iconst_0
    //   1843: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1844	0	this	ZipClass
    //   0	1844	1	paramFile1	File
    //   0	1844	2	paramFile2	File
    //   0	1844	3	paramOnZipListener	OnZipListener
    //   15	580	4	bool	boolean
    //   19	1484	5	localObject1	Object
    //   108	77	6	localObject2	Object
    //   111	62	7	localObject3	Object
    //   114	374	8	localEnumeration	java.util.Enumeration
    //   117	965	9	localFile	File
    //   120	57	10	localObject4	Object
    //   123	66	11	localObject5	Object
    //   126	1668	12	localObject6	Object
    //   129	907	13	localZipEntry	java.util.zip.ZipEntry
    //   132	125	14	localObject7	Object
    //   135	46	15	localObject8	Object
    //   138	55	16	localObject9	Object
    //   141	1663	17	localObject10	Object
    //   145	1548	18	localObject11	Object
    //   149	1548	19	localObject12	Object
    //   153	1548	20	localObject13	Object
    //   157	1513	21	localObject14	Object
    //   161	1505	22	localObject15	Object
    //   165	1497	23	localObject16	Object
    //   170	1432	24	localObject17	Object
    //   231	226	25	i	int
    //   234	1117	26	j	int
    //   423	926	27	k	int
    //   1003	3	28	l	long
    //   1008	285	30	m	int
    //   1140	127	31	n	int
    // Exception table:
    //   from	to	target	type
    //   374	379	402	java/io/IOException
    //   384	389	402	java/io/IOException
    //   394	399	402	java/io/IOException
    //   949	954	977	java/io/IOException
    //   959	964	977	java/io/IOException
    //   969	974	977	java/io/IOException
    //   1201	1206	1219	java/io/IOException
    //   1206	1211	1219	java/io/IOException
    //   1211	1216	1219	java/io/IOException
    //   998	1005	1355	finally
    //   998	1005	1362	java/io/IOException
    //   1537	1541	1564	java/io/IOException
    //   1546	1551	1564	java/io/IOException
    //   1556	1561	1564	java/io/IOException
    //   1033	1042	1585	finally
    //   1058	1063	1585	finally
    //   1079	1086	1585	finally
    //   1106	1113	1585	finally
    //   1129	1142	1585	finally
    //   1168	1177	1585	finally
    //   1193	1201	1585	finally
    //   1252	1262	1585	finally
    //   1289	1299	1585	finally
    //   1318	1323	1585	finally
    //   1339	1344	1585	finally
    //   1388	1392	1585	finally
    //   1408	1413	1585	finally
    //   1429	1434	1585	finally
    //   1450	1458	1585	finally
    //   1474	1484	1585	finally
    //   1500	1511	1585	finally
    //   1531	1537	1585	finally
    //   1033	1042	1600	java/io/IOException
    //   1058	1063	1600	java/io/IOException
    //   1079	1086	1600	java/io/IOException
    //   1106	1113	1600	java/io/IOException
    //   1129	1142	1600	java/io/IOException
    //   1168	1177	1600	java/io/IOException
    //   1193	1201	1600	java/io/IOException
    //   1252	1262	1600	java/io/IOException
    //   1289	1299	1600	java/io/IOException
    //   1318	1323	1600	java/io/IOException
    //   1339	1344	1600	java/io/IOException
    //   1388	1392	1600	java/io/IOException
    //   1408	1413	1600	java/io/IOException
    //   1429	1434	1600	java/io/IOException
    //   1450	1458	1600	java/io/IOException
    //   1474	1484	1600	java/io/IOException
    //   1500	1511	1600	java/io/IOException
    //   1531	1537	1600	java/io/IOException
    //   226	233	1615	finally
    //   260	267	1615	finally
    //   291	300	1615	finally
    //   487	499	1615	finally
    //   523	528	1615	finally
    //   552	563	1615	finally
    //   587	594	1615	finally
    //   226	233	1630	java/io/IOException
    //   260	267	1630	java/io/IOException
    //   291	300	1630	java/io/IOException
    //   487	499	1630	java/io/IOException
    //   523	528	1630	java/io/IOException
    //   552	563	1630	java/io/IOException
    //   587	594	1630	java/io/IOException
    //   167	172	1645	finally
    //   196	202	1645	finally
    //   333	342	1645	finally
    //   366	374	1645	finally
    //   453	463	1645	finally
    //   627	635	1645	finally
    //   659	663	1645	finally
    //   687	691	1645	finally
    //   715	722	1645	finally
    //   746	756	1645	finally
    //   780	790	1645	finally
    //   818	822	1645	finally
    //   846	850	1645	finally
    //   874	881	1645	finally
    //   905	915	1645	finally
    //   939	949	1645	finally
    //   1683	1687	1645	finally
    //   1702	1712	1645	finally
    //   167	172	1660	java/io/IOException
    //   196	202	1660	java/io/IOException
    //   333	342	1660	java/io/IOException
    //   366	374	1660	java/io/IOException
    //   453	463	1660	java/io/IOException
    //   627	635	1660	java/io/IOException
    //   659	663	1660	java/io/IOException
    //   687	691	1660	java/io/IOException
    //   715	722	1660	java/io/IOException
    //   746	756	1660	java/io/IOException
    //   780	790	1660	java/io/IOException
    //   818	822	1660	java/io/IOException
    //   846	850	1660	java/io/IOException
    //   874	881	1660	java/io/IOException
    //   905	915	1660	java/io/IOException
    //   939	949	1660	java/io/IOException
    //   1716	1720	1723	java/io/IOException
    //   1732	1737	1723	java/io/IOException
    //   1742	1747	1723	java/io/IOException
    //   1777	1781	1784	java/io/IOException
    //   1793	1798	1784	java/io/IOException
    //   1803	1808	1784	java/io/IOException
  }
  
  public void unzip(File paramFile1, File paramFile2, OnZipListener paramOnZipListener)
  {
    if (paramOnZipListener == null) {
      Log.e("ZipClass", "listener is null");
    }
    doUnzip(paramFile1, paramFile2, paramOnZipListener);
  }
  
  public boolean unzip(File paramFile1, File paramFile2)
  {
    return doUnzip(paramFile1, paramFile2, null);
  }
  
  public static abstract interface OnZipListener
  {
    public abstract void onError(String paramString);
    
    public abstract void onFileProgress(int paramInt1, int paramInt2);
    
    public abstract void onProgress(int paramInt1, int paramInt2);
    
    public abstract void onSuccess();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/android/util/launcher/ZipClass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */