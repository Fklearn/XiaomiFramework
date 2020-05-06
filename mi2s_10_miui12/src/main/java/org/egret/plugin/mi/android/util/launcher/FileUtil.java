package org.egret.plugin.mi.android.util.launcher;

import java.io.File;

public class FileUtil
{
  private static final int BUFFER_SIZE = 1024;
  
  /* Error */
  public static boolean Copy(File paramFile1, File paramFile2)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +286 -> 287
    //   4: aload_1
    //   5: ifnonnull +6 -> 11
    //   8: goto +279 -> 287
    //   11: aconst_null
    //   12: astore_2
    //   13: aconst_null
    //   14: astore_3
    //   15: aconst_null
    //   16: astore 4
    //   18: aconst_null
    //   19: astore 5
    //   21: aload_3
    //   22: astore 6
    //   24: aload 5
    //   26: astore 7
    //   28: aload_2
    //   29: astore 8
    //   31: aload 4
    //   33: astore 9
    //   35: new 18	java/io/FileInputStream
    //   38: astore 10
    //   40: aload_3
    //   41: astore 6
    //   43: aload 5
    //   45: astore 7
    //   47: aload_2
    //   48: astore 8
    //   50: aload 4
    //   52: astore 9
    //   54: aload 10
    //   56: aload_0
    //   57: invokespecial 21	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   60: aload 10
    //   62: astore_0
    //   63: aload_0
    //   64: astore 6
    //   66: aload 5
    //   68: astore 7
    //   70: aload_0
    //   71: astore 8
    //   73: aload 4
    //   75: astore 9
    //   77: new 23	java/io/FileOutputStream
    //   80: astore_3
    //   81: aload_0
    //   82: astore 6
    //   84: aload 5
    //   86: astore 7
    //   88: aload_0
    //   89: astore 8
    //   91: aload 4
    //   93: astore 9
    //   95: aload_3
    //   96: aload_1
    //   97: invokespecial 24	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   100: aload_3
    //   101: astore_1
    //   102: aload_0
    //   103: astore 6
    //   105: aload_1
    //   106: astore 7
    //   108: aload_0
    //   109: astore 8
    //   111: aload_1
    //   112: astore 9
    //   114: sipush 1024
    //   117: newarray <illegal type>
    //   119: astore 5
    //   121: aload_0
    //   122: astore 6
    //   124: aload_1
    //   125: astore 7
    //   127: aload_0
    //   128: astore 8
    //   130: aload_1
    //   131: astore 9
    //   133: aload_0
    //   134: aload 5
    //   136: iconst_0
    //   137: sipush 1024
    //   140: invokevirtual 28	java/io/FileInputStream:read	([BII)I
    //   143: istore 11
    //   145: iload 11
    //   147: ifle +27 -> 174
    //   150: aload_0
    //   151: astore 6
    //   153: aload_1
    //   154: astore 7
    //   156: aload_0
    //   157: astore 8
    //   159: aload_1
    //   160: astore 9
    //   162: aload_1
    //   163: aload 5
    //   165: iconst_0
    //   166: iload 11
    //   168: invokevirtual 32	java/io/FileOutputStream:write	([BII)V
    //   171: goto -50 -> 121
    //   174: aload_0
    //   175: invokevirtual 35	java/io/FileInputStream:close	()V
    //   178: aload_1
    //   179: invokevirtual 36	java/io/FileOutputStream:close	()V
    //   182: goto +8 -> 190
    //   185: astore_0
    //   186: aload_0
    //   187: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   190: iconst_1
    //   191: ireturn
    //   192: astore_0
    //   193: goto +55 -> 248
    //   196: astore_0
    //   197: aload 8
    //   199: astore 6
    //   201: aload 9
    //   203: astore 7
    //   205: aload_0
    //   206: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   209: aload 8
    //   211: ifnull +15 -> 226
    //   214: aload 8
    //   216: invokevirtual 35	java/io/FileInputStream:close	()V
    //   219: goto +7 -> 226
    //   222: astore_0
    //   223: goto +16 -> 239
    //   226: aload 9
    //   228: ifnull +18 -> 246
    //   231: aload 9
    //   233: invokevirtual 36	java/io/FileOutputStream:close	()V
    //   236: goto +10 -> 246
    //   239: aload_0
    //   240: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   243: goto +3 -> 246
    //   246: iconst_0
    //   247: ireturn
    //   248: aload 6
    //   250: ifnull +15 -> 265
    //   253: aload 6
    //   255: invokevirtual 35	java/io/FileInputStream:close	()V
    //   258: goto +7 -> 265
    //   261: astore_1
    //   262: goto +16 -> 278
    //   265: aload 7
    //   267: ifnull +18 -> 285
    //   270: aload 7
    //   272: invokevirtual 36	java/io/FileOutputStream:close	()V
    //   275: goto +10 -> 285
    //   278: aload_1
    //   279: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   282: goto +3 -> 285
    //   285: aload_0
    //   286: athrow
    //   287: iconst_0
    //   288: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	289	0	paramFile1	File
    //   0	289	1	paramFile2	File
    //   12	36	2	localObject1	Object
    //   14	87	3	localFileOutputStream	java.io.FileOutputStream
    //   16	76	4	localObject2	Object
    //   19	145	5	arrayOfByte	byte[]
    //   22	232	6	localObject3	Object
    //   26	245	7	localObject4	Object
    //   29	186	8	localObject5	Object
    //   33	199	9	localObject6	Object
    //   38	23	10	localFileInputStream	java.io.FileInputStream
    //   143	24	11	i	int
    // Exception table:
    //   from	to	target	type
    //   174	178	185	java/io/IOException
    //   178	182	185	java/io/IOException
    //   35	40	192	finally
    //   54	60	192	finally
    //   77	81	192	finally
    //   95	100	192	finally
    //   114	121	192	finally
    //   133	145	192	finally
    //   162	171	192	finally
    //   205	209	192	finally
    //   35	40	196	java/io/IOException
    //   54	60	196	java/io/IOException
    //   77	81	196	java/io/IOException
    //   95	100	196	java/io/IOException
    //   114	121	196	java/io/IOException
    //   133	145	196	java/io/IOException
    //   162	171	196	java/io/IOException
    //   214	219	222	java/io/IOException
    //   231	236	222	java/io/IOException
    //   253	258	261	java/io/IOException
    //   270	275	261	java/io/IOException
  }
  
  public static boolean CopyToRoot(File paramFile1, File paramFile2)
  {
    if ((paramFile1 != null) && (paramFile2 != null)) {
      return Copy(paramFile1, new File(paramFile2, paramFile1.getName()));
    }
    return false;
  }
  
  public static boolean Move(File paramFile1, File paramFile2)
  {
    boolean bool;
    if ((paramFile1 != null) && (paramFile2 != null) && (paramFile1.renameTo(paramFile2))) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public static boolean MoveToRoot(File paramFile1, File paramFile2)
  {
    if ((paramFile1 != null) && (paramFile2 != null)) {
      return Move(paramFile1, new File(paramFile2, paramFile1.getName()));
    }
    return false;
  }
  
  /* Error */
  public static String readFile(File paramFile)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: aconst_null
    //   5: astore_3
    //   6: aconst_null
    //   7: astore 4
    //   9: aload_2
    //   10: astore 5
    //   12: aload 4
    //   14: astore 6
    //   16: aload_1
    //   17: astore 7
    //   19: aload_3
    //   20: astore 8
    //   22: new 65	java/io/FileReader
    //   25: astore 9
    //   27: aload_2
    //   28: astore 5
    //   30: aload 4
    //   32: astore 6
    //   34: aload_1
    //   35: astore 7
    //   37: aload_3
    //   38: astore 8
    //   40: aload 9
    //   42: aload_0
    //   43: invokespecial 66	java/io/FileReader:<init>	(Ljava/io/File;)V
    //   46: aload 9
    //   48: astore_0
    //   49: aload_0
    //   50: astore 5
    //   52: aload 4
    //   54: astore 6
    //   56: aload_0
    //   57: astore 7
    //   59: aload_3
    //   60: astore 8
    //   62: new 68	java/io/BufferedReader
    //   65: astore_2
    //   66: aload_0
    //   67: astore 5
    //   69: aload 4
    //   71: astore 6
    //   73: aload_0
    //   74: astore 7
    //   76: aload_3
    //   77: astore 8
    //   79: aload_2
    //   80: aload_0
    //   81: invokespecial 71	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   84: aload_2
    //   85: astore 4
    //   87: aload_0
    //   88: astore 5
    //   90: aload 4
    //   92: astore 6
    //   94: aload_0
    //   95: astore 7
    //   97: aload 4
    //   99: astore 8
    //   101: new 73	java/lang/StringBuilder
    //   104: astore_2
    //   105: aload_0
    //   106: astore 5
    //   108: aload 4
    //   110: astore 6
    //   112: aload_0
    //   113: astore 7
    //   115: aload 4
    //   117: astore 8
    //   119: aload_2
    //   120: invokespecial 74	java/lang/StringBuilder:<init>	()V
    //   123: aload_0
    //   124: astore 5
    //   126: aload 4
    //   128: astore 6
    //   130: aload_0
    //   131: astore 7
    //   133: aload 4
    //   135: astore 8
    //   137: aload 4
    //   139: invokevirtual 77	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   142: astore_3
    //   143: aload_3
    //   144: ifnull +26 -> 170
    //   147: aload_0
    //   148: astore 5
    //   150: aload 4
    //   152: astore 6
    //   154: aload_0
    //   155: astore 7
    //   157: aload 4
    //   159: astore 8
    //   161: aload_2
    //   162: aload_3
    //   163: invokevirtual 81	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: pop
    //   167: goto -44 -> 123
    //   170: aload_0
    //   171: astore 5
    //   173: aload 4
    //   175: astore 6
    //   177: aload_0
    //   178: astore 7
    //   180: aload 4
    //   182: astore 8
    //   184: aload_2
    //   185: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: astore_3
    //   189: aload 4
    //   191: invokevirtual 85	java/io/BufferedReader:close	()V
    //   194: aload_0
    //   195: invokevirtual 86	java/io/FileReader:close	()V
    //   198: goto +8 -> 206
    //   201: astore_0
    //   202: aload_0
    //   203: invokevirtual 87	java/lang/Exception:printStackTrace	()V
    //   206: aload_3
    //   207: areturn
    //   208: astore_0
    //   209: goto +55 -> 264
    //   212: astore_0
    //   213: aload 7
    //   215: astore 5
    //   217: aload 8
    //   219: astore 6
    //   221: aload_0
    //   222: invokevirtual 87	java/lang/Exception:printStackTrace	()V
    //   225: aload 8
    //   227: ifnull +15 -> 242
    //   230: aload 8
    //   232: invokevirtual 85	java/io/BufferedReader:close	()V
    //   235: goto +7 -> 242
    //   238: astore_0
    //   239: goto +16 -> 255
    //   242: aload 7
    //   244: ifnull +18 -> 262
    //   247: aload 7
    //   249: invokevirtual 86	java/io/FileReader:close	()V
    //   252: goto +10 -> 262
    //   255: aload_0
    //   256: invokevirtual 87	java/lang/Exception:printStackTrace	()V
    //   259: goto +3 -> 262
    //   262: aconst_null
    //   263: areturn
    //   264: aload 6
    //   266: ifnull +16 -> 282
    //   269: aload 6
    //   271: invokevirtual 85	java/io/BufferedReader:close	()V
    //   274: goto +8 -> 282
    //   277: astore 5
    //   279: goto +16 -> 295
    //   282: aload 5
    //   284: ifnull +19 -> 303
    //   287: aload 5
    //   289: invokevirtual 86	java/io/FileReader:close	()V
    //   292: goto +11 -> 303
    //   295: aload 5
    //   297: invokevirtual 87	java/lang/Exception:printStackTrace	()V
    //   300: goto +3 -> 303
    //   303: aload_0
    //   304: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	305	0	paramFile	File
    //   1	34	1	localObject1	Object
    //   3	182	2	localObject2	Object
    //   5	202	3	str	String
    //   7	183	4	localObject3	Object
    //   10	206	5	localObject4	Object
    //   277	19	5	localException	Exception
    //   14	256	6	localObject5	Object
    //   17	231	7	localObject6	Object
    //   20	211	8	localObject7	Object
    //   25	22	9	localFileReader	java.io.FileReader
    // Exception table:
    //   from	to	target	type
    //   189	194	201	java/lang/Exception
    //   194	198	201	java/lang/Exception
    //   22	27	208	finally
    //   40	46	208	finally
    //   62	66	208	finally
    //   79	84	208	finally
    //   101	105	208	finally
    //   119	123	208	finally
    //   137	143	208	finally
    //   161	167	208	finally
    //   184	189	208	finally
    //   221	225	208	finally
    //   22	27	212	java/lang/Exception
    //   40	46	212	java/lang/Exception
    //   62	66	212	java/lang/Exception
    //   79	84	212	java/lang/Exception
    //   101	105	212	java/lang/Exception
    //   119	123	212	java/lang/Exception
    //   137	143	212	java/lang/Exception
    //   161	167	212	java/lang/Exception
    //   184	189	212	java/lang/Exception
    //   230	235	238	java/lang/Exception
    //   247	252	238	java/lang/Exception
    //   269	274	277	java/lang/Exception
    //   287	292	277	java/lang/Exception
  }
  
  /* Error */
  public static boolean writeFile(File paramFile, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: aconst_null
    //   3: astore_3
    //   4: aconst_null
    //   5: astore 4
    //   7: aconst_null
    //   8: astore 5
    //   10: aload_3
    //   11: astore 6
    //   13: aload 5
    //   15: astore 7
    //   17: aload_2
    //   18: astore 8
    //   20: aload 4
    //   22: astore 9
    //   24: new 91	java/io/FileWriter
    //   27: astore 10
    //   29: aload_3
    //   30: astore 6
    //   32: aload 5
    //   34: astore 7
    //   36: aload_2
    //   37: astore 8
    //   39: aload 4
    //   41: astore 9
    //   43: aload 10
    //   45: aload_0
    //   46: invokespecial 92	java/io/FileWriter:<init>	(Ljava/io/File;)V
    //   49: aload 10
    //   51: astore_0
    //   52: aload_0
    //   53: astore 6
    //   55: aload 5
    //   57: astore 7
    //   59: aload_0
    //   60: astore 8
    //   62: aload 4
    //   64: astore 9
    //   66: new 94	java/io/BufferedWriter
    //   69: astore_3
    //   70: aload_0
    //   71: astore 6
    //   73: aload 5
    //   75: astore 7
    //   77: aload_0
    //   78: astore 8
    //   80: aload 4
    //   82: astore 9
    //   84: aload_3
    //   85: aload_0
    //   86: invokespecial 97	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
    //   89: aload_3
    //   90: astore 5
    //   92: aload_0
    //   93: astore 6
    //   95: aload 5
    //   97: astore 7
    //   99: aload_0
    //   100: astore 8
    //   102: aload 5
    //   104: astore 9
    //   106: aload 5
    //   108: aload_1
    //   109: invokevirtual 100	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   112: aload 5
    //   114: invokevirtual 101	java/io/BufferedWriter:close	()V
    //   117: aload_0
    //   118: invokevirtual 102	java/io/FileWriter:close	()V
    //   121: goto +8 -> 129
    //   124: astore_0
    //   125: aload_0
    //   126: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   129: iconst_1
    //   130: ireturn
    //   131: astore_0
    //   132: goto +55 -> 187
    //   135: astore_0
    //   136: aload 8
    //   138: astore 6
    //   140: aload 9
    //   142: astore 7
    //   144: aload_0
    //   145: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   148: aload 9
    //   150: ifnull +15 -> 165
    //   153: aload 9
    //   155: invokevirtual 101	java/io/BufferedWriter:close	()V
    //   158: goto +7 -> 165
    //   161: astore_0
    //   162: goto +16 -> 178
    //   165: aload 8
    //   167: ifnull +18 -> 185
    //   170: aload 8
    //   172: invokevirtual 102	java/io/FileWriter:close	()V
    //   175: goto +10 -> 185
    //   178: aload_0
    //   179: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   182: goto +3 -> 185
    //   185: iconst_0
    //   186: ireturn
    //   187: aload 7
    //   189: ifnull +15 -> 204
    //   192: aload 7
    //   194: invokevirtual 101	java/io/BufferedWriter:close	()V
    //   197: goto +7 -> 204
    //   200: astore_1
    //   201: goto +16 -> 217
    //   204: aload 6
    //   206: ifnull +18 -> 224
    //   209: aload 6
    //   211: invokevirtual 102	java/io/FileWriter:close	()V
    //   214: goto +10 -> 224
    //   217: aload_1
    //   218: invokevirtual 39	java/io/IOException:printStackTrace	()V
    //   221: goto +3 -> 224
    //   224: aload_0
    //   225: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	226	0	paramFile	File
    //   0	226	1	paramString	String
    //   1	36	2	localObject1	Object
    //   3	87	3	localBufferedWriter1	java.io.BufferedWriter
    //   5	76	4	localObject2	Object
    //   8	105	5	localBufferedWriter2	java.io.BufferedWriter
    //   11	199	6	localObject3	Object
    //   15	178	7	localObject4	Object
    //   18	153	8	localObject5	Object
    //   22	132	9	localObject6	Object
    //   27	23	10	localFileWriter	java.io.FileWriter
    // Exception table:
    //   from	to	target	type
    //   112	117	124	java/io/IOException
    //   117	121	124	java/io/IOException
    //   24	29	131	finally
    //   43	49	131	finally
    //   66	70	131	finally
    //   84	89	131	finally
    //   106	112	131	finally
    //   144	148	131	finally
    //   24	29	135	java/io/IOException
    //   43	49	135	java/io/IOException
    //   66	70	135	java/io/IOException
    //   84	89	135	java/io/IOException
    //   106	112	135	java/io/IOException
    //   153	158	161	java/io/IOException
    //   170	175	161	java/io/IOException
    //   192	197	200	java/io/IOException
    //   209	214	200	java/io/IOException
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/android/util/launcher/FileUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */