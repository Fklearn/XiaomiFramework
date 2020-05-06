package org.egret.plugin.mi.android.util.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NetClass
{
  private static final int BUFFER_SIZE = 1024;
  private static final int TIME_OUT = 30000;
  
  /* Error */
  private void doRequest(String paramString1, String paramString2, java.io.OutputStream paramOutputStream, OnNetListener paramOnNetListener)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +1268 -> 1269
    //   4: aload_3
    //   5: ifnonnull +6 -> 11
    //   8: goto +1261 -> 1269
    //   11: new 25	java/net/URL
    //   14: dup
    //   15: aload_1
    //   16: invokespecial 28	java/net/URL:<init>	(Ljava/lang/String;)V
    //   19: astore 5
    //   21: aconst_null
    //   22: astore 6
    //   24: aconst_null
    //   25: astore 7
    //   27: aconst_null
    //   28: astore 8
    //   30: aconst_null
    //   31: astore 9
    //   33: aconst_null
    //   34: astore 10
    //   36: aconst_null
    //   37: astore 11
    //   39: aconst_null
    //   40: astore 12
    //   42: aload 8
    //   44: astore 13
    //   46: aload 12
    //   48: astore 14
    //   50: aload 9
    //   52: astore 15
    //   54: aload 11
    //   56: astore 16
    //   58: aload 5
    //   60: invokevirtual 32	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   63: checkcast 34	java/net/HttpURLConnection
    //   66: astore 5
    //   68: aload 5
    //   70: ifnonnull +223 -> 293
    //   73: aload 4
    //   75: ifnull +155 -> 230
    //   78: aload 5
    //   80: astore 7
    //   82: aload 8
    //   84: astore 13
    //   86: aload 12
    //   88: astore 14
    //   90: aload 5
    //   92: astore 6
    //   94: aload 9
    //   96: astore 15
    //   98: aload 11
    //   100: astore 16
    //   102: new 36	java/lang/StringBuilder
    //   105: astore_2
    //   106: aload 5
    //   108: astore 7
    //   110: aload 8
    //   112: astore 13
    //   114: aload 12
    //   116: astore 14
    //   118: aload 5
    //   120: astore 6
    //   122: aload 9
    //   124: astore 15
    //   126: aload 11
    //   128: astore 16
    //   130: aload_2
    //   131: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   134: aload 5
    //   136: astore 7
    //   138: aload 8
    //   140: astore 13
    //   142: aload 12
    //   144: astore 14
    //   146: aload 5
    //   148: astore 6
    //   150: aload 9
    //   152: astore 15
    //   154: aload 11
    //   156: astore 16
    //   158: aload_2
    //   159: ldc 39
    //   161: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: pop
    //   165: aload 5
    //   167: astore 7
    //   169: aload 8
    //   171: astore 13
    //   173: aload 12
    //   175: astore 14
    //   177: aload 5
    //   179: astore 6
    //   181: aload 9
    //   183: astore 15
    //   185: aload 11
    //   187: astore 16
    //   189: aload_2
    //   190: aload_1
    //   191: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   194: pop
    //   195: aload 5
    //   197: astore 7
    //   199: aload 8
    //   201: astore 13
    //   203: aload 12
    //   205: astore 14
    //   207: aload 5
    //   209: astore 6
    //   211: aload 9
    //   213: astore 15
    //   215: aload 11
    //   217: astore 16
    //   219: aload 4
    //   221: aload_2
    //   222: invokevirtual 47	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: invokeinterface 50 2 0
    //   230: aload 5
    //   232: ifnull +15 -> 247
    //   235: aload 5
    //   237: invokevirtual 53	java/net/HttpURLConnection:disconnect	()V
    //   240: goto +7 -> 247
    //   243: astore_1
    //   244: goto +27 -> 271
    //   247: iconst_0
    //   248: ifeq +11 -> 259
    //   251: new 55	java/lang/NullPointerException
    //   254: dup
    //   255: invokespecial 56	java/lang/NullPointerException:<init>	()V
    //   258: athrow
    //   259: iconst_0
    //   260: ifeq +32 -> 292
    //   263: new 55	java/lang/NullPointerException
    //   266: dup
    //   267: invokespecial 56	java/lang/NullPointerException:<init>	()V
    //   270: athrow
    //   271: aload_1
    //   272: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   275: aload 4
    //   277: ifnull +15 -> 292
    //   280: aload 4
    //   282: ldc 61
    //   284: invokeinterface 50 2 0
    //   289: goto +3 -> 292
    //   292: return
    //   293: aload 5
    //   295: astore 7
    //   297: aload 8
    //   299: astore 13
    //   301: aload 12
    //   303: astore 14
    //   305: aload 5
    //   307: astore 6
    //   309: aload 9
    //   311: astore 15
    //   313: aload 11
    //   315: astore 16
    //   317: aload 5
    //   319: sipush 30000
    //   322: invokevirtual 65	java/net/HttpURLConnection:setConnectTimeout	(I)V
    //   325: aload_2
    //   326: ifnull +338 -> 664
    //   329: aload 5
    //   331: astore 7
    //   333: aload 8
    //   335: astore 13
    //   337: aload 12
    //   339: astore 14
    //   341: aload 5
    //   343: astore 6
    //   345: aload 9
    //   347: astore 15
    //   349: aload 11
    //   351: astore 16
    //   353: aload 5
    //   355: iconst_1
    //   356: invokevirtual 69	java/net/HttpURLConnection:setDoOutput	(Z)V
    //   359: aload 5
    //   361: astore 7
    //   363: aload 8
    //   365: astore 13
    //   367: aload 12
    //   369: astore 14
    //   371: aload 5
    //   373: astore 6
    //   375: aload 9
    //   377: astore 15
    //   379: aload 11
    //   381: astore 16
    //   383: aload 5
    //   385: invokevirtual 73	java/net/HttpURLConnection:getOutputStream	()Ljava/io/OutputStream;
    //   388: astore 10
    //   390: aload 10
    //   392: ifnonnull +210 -> 602
    //   395: aload 4
    //   397: ifnull +155 -> 552
    //   400: aload 5
    //   402: astore 7
    //   404: aload 10
    //   406: astore 13
    //   408: aload 12
    //   410: astore 14
    //   412: aload 5
    //   414: astore 6
    //   416: aload 10
    //   418: astore 15
    //   420: aload 11
    //   422: astore 16
    //   424: new 36	java/lang/StringBuilder
    //   427: astore_2
    //   428: aload 5
    //   430: astore 7
    //   432: aload 10
    //   434: astore 13
    //   436: aload 12
    //   438: astore 14
    //   440: aload 5
    //   442: astore 6
    //   444: aload 10
    //   446: astore 15
    //   448: aload 11
    //   450: astore 16
    //   452: aload_2
    //   453: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   456: aload 5
    //   458: astore 7
    //   460: aload 10
    //   462: astore 13
    //   464: aload 12
    //   466: astore 14
    //   468: aload 5
    //   470: astore 6
    //   472: aload 10
    //   474: astore 15
    //   476: aload 11
    //   478: astore 16
    //   480: aload_2
    //   481: ldc 75
    //   483: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   486: pop
    //   487: aload 5
    //   489: astore 7
    //   491: aload 10
    //   493: astore 13
    //   495: aload 12
    //   497: astore 14
    //   499: aload 5
    //   501: astore 6
    //   503: aload 10
    //   505: astore 15
    //   507: aload 11
    //   509: astore 16
    //   511: aload_2
    //   512: aload_1
    //   513: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   516: pop
    //   517: aload 5
    //   519: astore 7
    //   521: aload 10
    //   523: astore 13
    //   525: aload 12
    //   527: astore 14
    //   529: aload 5
    //   531: astore 6
    //   533: aload 10
    //   535: astore 15
    //   537: aload 11
    //   539: astore 16
    //   541: aload 4
    //   543: aload_2
    //   544: invokevirtual 47	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   547: invokeinterface 50 2 0
    //   552: aload 5
    //   554: invokevirtual 53	java/net/HttpURLConnection:disconnect	()V
    //   557: aload 10
    //   559: ifnull +8 -> 567
    //   562: aload 10
    //   564: invokevirtual 80	java/io/OutputStream:close	()V
    //   567: iconst_0
    //   568: ifeq +11 -> 579
    //   571: new 55	java/lang/NullPointerException
    //   574: dup
    //   575: invokespecial 56	java/lang/NullPointerException:<init>	()V
    //   578: athrow
    //   579: goto +22 -> 601
    //   582: astore_1
    //   583: aload_1
    //   584: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   587: aload 4
    //   589: ifnull +12 -> 601
    //   592: aload 4
    //   594: ldc 61
    //   596: invokeinterface 50 2 0
    //   601: return
    //   602: aload 5
    //   604: astore 7
    //   606: aload 10
    //   608: astore 13
    //   610: aload 12
    //   612: astore 14
    //   614: aload 5
    //   616: astore 6
    //   618: aload 10
    //   620: astore 15
    //   622: aload 11
    //   624: astore 16
    //   626: aload 10
    //   628: aload_2
    //   629: invokevirtual 86	java/lang/String:getBytes	()[B
    //   632: invokevirtual 90	java/io/OutputStream:write	([B)V
    //   635: aload 5
    //   637: astore 7
    //   639: aload 10
    //   641: astore 13
    //   643: aload 12
    //   645: astore 14
    //   647: aload 5
    //   649: astore 6
    //   651: aload 10
    //   653: astore 15
    //   655: aload 11
    //   657: astore 16
    //   659: aload 10
    //   661: invokevirtual 80	java/io/OutputStream:close	()V
    //   664: aload 5
    //   666: astore 7
    //   668: aload 10
    //   670: astore 13
    //   672: aload 12
    //   674: astore 14
    //   676: aload 5
    //   678: astore 6
    //   680: aload 10
    //   682: astore 15
    //   684: aload 11
    //   686: astore 16
    //   688: aload 5
    //   690: invokevirtual 94	java/net/HttpURLConnection:getResponseCode	()I
    //   693: sipush 200
    //   696: if_icmpeq +91 -> 787
    //   699: aload 4
    //   701: ifnull +36 -> 737
    //   704: aload 5
    //   706: astore 7
    //   708: aload 10
    //   710: astore 13
    //   712: aload 12
    //   714: astore 14
    //   716: aload 5
    //   718: astore 6
    //   720: aload 10
    //   722: astore 15
    //   724: aload 11
    //   726: astore 16
    //   728: aload 4
    //   730: ldc 96
    //   732: invokeinterface 50 2 0
    //   737: aload 5
    //   739: invokevirtual 53	java/net/HttpURLConnection:disconnect	()V
    //   742: aload 10
    //   744: ifnull +8 -> 752
    //   747: aload 10
    //   749: invokevirtual 80	java/io/OutputStream:close	()V
    //   752: iconst_0
    //   753: ifeq +11 -> 764
    //   756: new 55	java/lang/NullPointerException
    //   759: dup
    //   760: invokespecial 56	java/lang/NullPointerException:<init>	()V
    //   763: athrow
    //   764: goto +22 -> 786
    //   767: astore_1
    //   768: aload_1
    //   769: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   772: aload 4
    //   774: ifnull +12 -> 786
    //   777: aload 4
    //   779: ldc 61
    //   781: invokeinterface 50 2 0
    //   786: return
    //   787: aload 5
    //   789: astore 7
    //   791: aload 10
    //   793: astore 13
    //   795: aload 12
    //   797: astore 14
    //   799: aload 5
    //   801: astore 6
    //   803: aload 10
    //   805: astore 15
    //   807: aload 11
    //   809: astore 16
    //   811: aload 5
    //   813: invokevirtual 99	java/net/HttpURLConnection:getContentLength	()I
    //   816: istore 17
    //   818: iconst_0
    //   819: istore 18
    //   821: aload 5
    //   823: astore 7
    //   825: aload 10
    //   827: astore 13
    //   829: aload 12
    //   831: astore 14
    //   833: aload 5
    //   835: astore 6
    //   837: aload 10
    //   839: astore 15
    //   841: aload 11
    //   843: astore 16
    //   845: aload 5
    //   847: invokevirtual 103	java/net/HttpURLConnection:getInputStream	()Ljava/io/InputStream;
    //   850: astore_1
    //   851: aload 5
    //   853: astore 7
    //   855: aload 10
    //   857: astore 13
    //   859: aload_1
    //   860: astore 14
    //   862: aload 5
    //   864: astore 6
    //   866: aload 10
    //   868: astore 15
    //   870: aload_1
    //   871: astore 16
    //   873: sipush 1024
    //   876: newarray <illegal type>
    //   878: astore_2
    //   879: aload 5
    //   881: astore 7
    //   883: aload 10
    //   885: astore 13
    //   887: aload_1
    //   888: astore 14
    //   890: aload 5
    //   892: astore 6
    //   894: aload 10
    //   896: astore 15
    //   898: aload_1
    //   899: astore 16
    //   901: aload_1
    //   902: aload_2
    //   903: invokevirtual 109	java/io/InputStream:read	([B)I
    //   906: istore 19
    //   908: iload 19
    //   910: ifle +128 -> 1038
    //   913: aload 4
    //   915: ifnull +37 -> 952
    //   918: aload 5
    //   920: astore 7
    //   922: aload 10
    //   924: astore 13
    //   926: aload_1
    //   927: astore 14
    //   929: aload 5
    //   931: astore 6
    //   933: aload 10
    //   935: astore 15
    //   937: aload_1
    //   938: astore 16
    //   940: invokestatic 115	org/egret/plugin/mi/android/util/launcher/ExecutorLab:getInstance	()Lorg/egret/plugin/mi/android/util/launcher/ExecutorLab;
    //   943: invokevirtual 119	org/egret/plugin/mi/android/util/launcher/ExecutorLab:isRunning	()Z
    //   946: ifne +6 -> 952
    //   949: goto +89 -> 1038
    //   952: aload 5
    //   954: astore 7
    //   956: aload 10
    //   958: astore 13
    //   960: aload_1
    //   961: astore 14
    //   963: aload 5
    //   965: astore 6
    //   967: aload 10
    //   969: astore 15
    //   971: aload_1
    //   972: astore 16
    //   974: aload_3
    //   975: aload_2
    //   976: iconst_0
    //   977: iload 19
    //   979: invokevirtual 122	java/io/OutputStream:write	([BII)V
    //   982: iload 18
    //   984: iload 19
    //   986: iadd
    //   987: istore 19
    //   989: iload 19
    //   991: istore 18
    //   993: aload 4
    //   995: ifnull -116 -> 879
    //   998: aload 5
    //   1000: astore 7
    //   1002: aload 10
    //   1004: astore 13
    //   1006: aload_1
    //   1007: astore 14
    //   1009: aload 5
    //   1011: astore 6
    //   1013: aload 10
    //   1015: astore 15
    //   1017: aload_1
    //   1018: astore 16
    //   1020: aload 4
    //   1022: iload 19
    //   1024: iload 17
    //   1026: invokeinterface 126 3 0
    //   1031: iload 19
    //   1033: istore 18
    //   1035: goto -156 -> 879
    //   1038: aload 5
    //   1040: invokevirtual 53	java/net/HttpURLConnection:disconnect	()V
    //   1043: aload 10
    //   1045: ifnull +8 -> 1053
    //   1048: aload 10
    //   1050: invokevirtual 80	java/io/OutputStream:close	()V
    //   1053: aload_1
    //   1054: invokevirtual 127	java/io/InputStream:close	()V
    //   1057: goto +126 -> 1183
    //   1060: astore_1
    //   1061: aload_1
    //   1062: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   1065: aload 4
    //   1067: ifnull +12 -> 1079
    //   1070: aload 4
    //   1072: ldc 61
    //   1074: invokeinterface 50 2 0
    //   1079: goto +104 -> 1183
    //   1082: astore_1
    //   1083: goto +101 -> 1184
    //   1086: astore_1
    //   1087: aload 6
    //   1089: astore 7
    //   1091: aload 15
    //   1093: astore 13
    //   1095: aload 16
    //   1097: astore 14
    //   1099: aload_1
    //   1100: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   1103: aload 4
    //   1105: ifnull +26 -> 1131
    //   1108: aload 6
    //   1110: astore 7
    //   1112: aload 15
    //   1114: astore 13
    //   1116: aload 16
    //   1118: astore 14
    //   1120: aload 4
    //   1122: aload_1
    //   1123: invokevirtual 128	java/io/IOException:toString	()Ljava/lang/String;
    //   1126: invokeinterface 50 2 0
    //   1131: aload 6
    //   1133: ifnull +15 -> 1148
    //   1136: aload 6
    //   1138: invokevirtual 53	java/net/HttpURLConnection:disconnect	()V
    //   1141: goto +7 -> 1148
    //   1144: astore_1
    //   1145: goto +26 -> 1171
    //   1148: aload 15
    //   1150: ifnull +8 -> 1158
    //   1153: aload 15
    //   1155: invokevirtual 80	java/io/OutputStream:close	()V
    //   1158: aload 16
    //   1160: ifnull -103 -> 1057
    //   1163: aload 16
    //   1165: invokevirtual 127	java/io/InputStream:close	()V
    //   1168: goto -111 -> 1057
    //   1171: aload_1
    //   1172: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   1175: aload 4
    //   1177: ifnull -98 -> 1079
    //   1180: goto -110 -> 1070
    //   1183: return
    //   1184: aload 7
    //   1186: ifnull +15 -> 1201
    //   1189: aload 7
    //   1191: invokevirtual 53	java/net/HttpURLConnection:disconnect	()V
    //   1194: goto +7 -> 1201
    //   1197: astore_2
    //   1198: goto +26 -> 1224
    //   1201: aload 13
    //   1203: ifnull +8 -> 1211
    //   1206: aload 13
    //   1208: invokevirtual 80	java/io/OutputStream:close	()V
    //   1211: aload 14
    //   1213: ifnull +32 -> 1245
    //   1216: aload 14
    //   1218: invokevirtual 127	java/io/InputStream:close	()V
    //   1221: goto +24 -> 1245
    //   1224: aload_2
    //   1225: invokevirtual 59	java/io/IOException:printStackTrace	()V
    //   1228: aload 4
    //   1230: ifnull +15 -> 1245
    //   1233: aload 4
    //   1235: ldc 61
    //   1237: invokeinterface 50 2 0
    //   1242: goto +3 -> 1245
    //   1245: aload_1
    //   1246: athrow
    //   1247: astore_1
    //   1248: aload_1
    //   1249: invokevirtual 129	java/net/MalformedURLException:printStackTrace	()V
    //   1252: aload 4
    //   1254: ifnull +14 -> 1268
    //   1257: aload 4
    //   1259: aload_1
    //   1260: invokevirtual 130	java/net/MalformedURLException:toString	()Ljava/lang/String;
    //   1263: invokeinterface 50 2 0
    //   1268: return
    //   1269: ldc -124
    //   1271: ldc -122
    //   1273: invokestatic 140	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   1276: pop
    //   1277: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1278	0	this	NetClass
    //   0	1278	1	paramString1	String
    //   0	1278	2	paramString2	String
    //   0	1278	3	paramOutputStream	java.io.OutputStream
    //   0	1278	4	paramOnNetListener	OnNetListener
    //   19	1020	5	localObject1	Object
    //   22	1115	6	localObject2	Object
    //   25	1165	7	localObject3	Object
    //   28	336	8	localObject4	Object
    //   31	345	9	localObject5	Object
    //   34	1015	10	localOutputStream	java.io.OutputStream
    //   37	805	11	localObject6	Object
    //   40	790	12	localObject7	Object
    //   44	1163	13	localObject8	Object
    //   48	1169	14	localObject9	Object
    //   52	1102	15	localObject10	Object
    //   56	1108	16	localObject11	Object
    //   816	209	17	i	int
    //   819	215	18	j	int
    //   906	126	19	k	int
    // Exception table:
    //   from	to	target	type
    //   235	240	243	java/io/IOException
    //   251	259	243	java/io/IOException
    //   263	271	243	java/io/IOException
    //   552	557	582	java/io/IOException
    //   562	567	582	java/io/IOException
    //   571	579	582	java/io/IOException
    //   737	742	767	java/io/IOException
    //   747	752	767	java/io/IOException
    //   756	764	767	java/io/IOException
    //   1038	1043	1060	java/io/IOException
    //   1048	1053	1060	java/io/IOException
    //   1053	1057	1060	java/io/IOException
    //   58	68	1082	finally
    //   102	106	1082	finally
    //   130	134	1082	finally
    //   158	165	1082	finally
    //   189	195	1082	finally
    //   219	230	1082	finally
    //   317	325	1082	finally
    //   353	359	1082	finally
    //   383	390	1082	finally
    //   424	428	1082	finally
    //   452	456	1082	finally
    //   480	487	1082	finally
    //   511	517	1082	finally
    //   541	552	1082	finally
    //   626	635	1082	finally
    //   659	664	1082	finally
    //   688	699	1082	finally
    //   728	737	1082	finally
    //   811	818	1082	finally
    //   845	851	1082	finally
    //   873	879	1082	finally
    //   901	908	1082	finally
    //   940	949	1082	finally
    //   974	982	1082	finally
    //   1020	1031	1082	finally
    //   1099	1103	1082	finally
    //   1120	1131	1082	finally
    //   58	68	1086	java/io/IOException
    //   102	106	1086	java/io/IOException
    //   130	134	1086	java/io/IOException
    //   158	165	1086	java/io/IOException
    //   189	195	1086	java/io/IOException
    //   219	230	1086	java/io/IOException
    //   317	325	1086	java/io/IOException
    //   353	359	1086	java/io/IOException
    //   383	390	1086	java/io/IOException
    //   424	428	1086	java/io/IOException
    //   452	456	1086	java/io/IOException
    //   480	487	1086	java/io/IOException
    //   511	517	1086	java/io/IOException
    //   541	552	1086	java/io/IOException
    //   626	635	1086	java/io/IOException
    //   659	664	1086	java/io/IOException
    //   688	699	1086	java/io/IOException
    //   728	737	1086	java/io/IOException
    //   811	818	1086	java/io/IOException
    //   845	851	1086	java/io/IOException
    //   873	879	1086	java/io/IOException
    //   901	908	1086	java/io/IOException
    //   940	949	1086	java/io/IOException
    //   974	982	1086	java/io/IOException
    //   1020	1031	1086	java/io/IOException
    //   1136	1141	1144	java/io/IOException
    //   1153	1158	1144	java/io/IOException
    //   1163	1168	1144	java/io/IOException
    //   1189	1194	1197	java/io/IOException
    //   1206	1211	1197	java/io/IOException
    //   1216	1221	1197	java/io/IOException
    //   11	21	1247	java/net/MalformedURLException
  }
  
  private void request(String paramString1, String paramString2, File paramFile, OnNetListener paramOnNetListener)
  {
    if (paramFile != null) {}
    try
    {
      paramString2 = new java/io/FileOutputStream;
      paramString2.<init>(paramFile);
      doRequest(paramString1, null, paramString2, paramOnNetListener);
      paramString2.close();
      if (paramOnNetListener != null) {
        if (!ExecutorLab.getInstance().isRunning()) {
          paramOnNetListener.onError("net thread is cancelled");
        } else {
          paramOnNetListener.onSuccess(null);
        }
      }
      return;
      paramFile = new java/io/ByteArrayOutputStream;
      paramFile.<init>();
      doRequest(paramString1, paramString2, paramFile, paramOnNetListener);
      paramFile.close();
      if (paramOnNetListener != null) {
        if (!ExecutorLab.getInstance().isRunning())
        {
          paramOnNetListener.onError("net thread is cancelled");
        }
        else
        {
          paramString1 = new java/lang/String;
          paramString1.<init>(paramFile.toByteArray());
          paramOnNetListener.onSuccess(paramString1);
        }
      }
    }
    catch (IOException paramString1)
    {
      paramString1.printStackTrace();
      if (paramOnNetListener != null) {
        paramOnNetListener.onError(paramString1.toString());
      }
    }
  }
  
  public String getRequest(String paramString)
  {
    return postRequest(paramString, null);
  }
  
  public void getRequest(String paramString, OnNetListener paramOnNetListener)
  {
    postRequest(paramString, null, paramOnNetListener);
  }
  
  public String postRequest(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new java/io/ByteArrayOutputStream;
      localByteArrayOutputStream.<init>();
      doRequest(paramString1, paramString2, localByteArrayOutputStream, null);
      localByteArrayOutputStream.close();
      paramString1 = new String(localByteArrayOutputStream.toByteArray());
      return paramString1;
    }
    catch (IOException paramString1)
    {
      paramString1.printStackTrace();
    }
    return null;
  }
  
  public void postRequest(String paramString1, String paramString2, OnNetListener paramOnNetListener)
  {
    if (paramString1 == null) {
      return;
    }
    request(paramString1, paramString2, null, paramOnNetListener);
  }
  
  public void writeResponseToFile(String paramString, File paramFile, OnNetListener paramOnNetListener)
  {
    if ((paramString != null) && (paramFile != null))
    {
      request(paramString, null, paramFile, paramOnNetListener);
      return;
    }
  }
  
  public static abstract interface OnNetListener
  {
    public abstract void onError(String paramString);
    
    public abstract void onProgress(int paramInt1, int paramInt2);
    
    public abstract void onSuccess(String paramString);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/android/util/launcher/NetClass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */