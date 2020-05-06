package org.egret.plugin.mi.android.util.launcher;

import java.io.File;

public class Md5Util
{
  public static boolean checkMd5(File paramFile, String paramString)
  {
    boolean bool1 = false;
    if ((paramFile != null) && (paramString != null))
    {
      paramFile = md5(paramFile);
      boolean bool2 = bool1;
      if (paramFile != null)
      {
        bool2 = bool1;
        if (paramFile.equals(paramString)) {
          bool2 = true;
        }
      }
      return bool2;
    }
    return false;
  }
  
  private static String getMd5String(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar1 = new char[16];
    char[] tmp6_5 = arrayOfChar1;
    tmp6_5[0] = 48;
    char[] tmp11_6 = tmp6_5;
    tmp11_6[1] = 49;
    char[] tmp16_11 = tmp11_6;
    tmp16_11[2] = 50;
    char[] tmp21_16 = tmp16_11;
    tmp21_16[3] = 51;
    char[] tmp26_21 = tmp21_16;
    tmp26_21[4] = 52;
    char[] tmp31_26 = tmp26_21;
    tmp31_26[5] = 53;
    char[] tmp36_31 = tmp31_26;
    tmp36_31[6] = 54;
    char[] tmp42_36 = tmp36_31;
    tmp42_36[7] = 55;
    char[] tmp48_42 = tmp42_36;
    tmp48_42[8] = 56;
    char[] tmp54_48 = tmp48_42;
    tmp54_48[9] = 57;
    char[] tmp60_54 = tmp54_48;
    tmp60_54[10] = 97;
    char[] tmp66_60 = tmp60_54;
    tmp66_60[11] = 98;
    char[] tmp72_66 = tmp66_60;
    tmp72_66[12] = 99;
    char[] tmp78_72 = tmp72_66;
    tmp78_72[13] = 100;
    char[] tmp84_78 = tmp78_72;
    tmp84_78[14] = 101;
    char[] tmp90_84 = tmp84_78;
    tmp90_84[15] = 102;
    tmp90_84;
    int i = paramArrayOfByte.length;
    char[] arrayOfChar2 = new char[i * 2];
    for (int j = 0; j < i; j++)
    {
      int k = paramArrayOfByte[j];
      arrayOfChar2[(j * 2)] = ((char)arrayOfChar1[(k >>> 4 & 0xF)]);
      arrayOfChar2[(j * 2 + 1)] = ((char)arrayOfChar1[(k & 0xF)]);
    }
    return new String(arrayOfChar2);
  }
  
  /* Error */
  public static String md5(File paramFile)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 52	java/io/File:exists	()Z
    //   4: ifne +5 -> 9
    //   7: aconst_null
    //   8: areturn
    //   9: aconst_null
    //   10: astore_1
    //   11: aconst_null
    //   12: astore_2
    //   13: aload_2
    //   14: astore_3
    //   15: aload_1
    //   16: astore 4
    //   18: ldc 54
    //   20: invokestatic 60	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
    //   23: astore 5
    //   25: aload_2
    //   26: astore_3
    //   27: aload_1
    //   28: astore 4
    //   30: new 62	java/io/FileInputStream
    //   33: astore 6
    //   35: aload_2
    //   36: astore_3
    //   37: aload_1
    //   38: astore 4
    //   40: aload 6
    //   42: aload_0
    //   43: invokespecial 65	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   46: aload 6
    //   48: astore_0
    //   49: aload_0
    //   50: astore_3
    //   51: aload_0
    //   52: astore 4
    //   54: sipush 1024
    //   57: newarray <illegal type>
    //   59: astore_2
    //   60: aload_0
    //   61: astore_3
    //   62: aload_0
    //   63: astore 4
    //   65: aload_0
    //   66: aload_2
    //   67: invokevirtual 69	java/io/FileInputStream:read	([B)I
    //   70: istore 7
    //   72: iload 7
    //   74: ifle +20 -> 94
    //   77: aload_0
    //   78: astore_3
    //   79: aload_0
    //   80: astore 4
    //   82: aload 5
    //   84: aload_2
    //   85: iconst_0
    //   86: iload 7
    //   88: invokevirtual 73	java/security/MessageDigest:update	([BII)V
    //   91: goto -31 -> 60
    //   94: aload_0
    //   95: astore_3
    //   96: aload_0
    //   97: astore 4
    //   99: aload 5
    //   101: invokevirtual 77	java/security/MessageDigest:digest	()[B
    //   104: invokestatic 79	org/egret/plugin/mi/android/util/launcher/Md5Util:getMd5String	([B)Ljava/lang/String;
    //   107: astore_2
    //   108: aload_0
    //   109: invokevirtual 82	java/io/FileInputStream:close	()V
    //   112: goto +8 -> 120
    //   115: astore_0
    //   116: aload_0
    //   117: invokevirtual 85	java/io/IOException:printStackTrace	()V
    //   120: aload_2
    //   121: areturn
    //   122: astore_0
    //   123: goto +34 -> 157
    //   126: astore_0
    //   127: aload 4
    //   129: astore_3
    //   130: aload_0
    //   131: invokevirtual 86	java/lang/Exception:printStackTrace	()V
    //   134: aload 4
    //   136: ifnull +19 -> 155
    //   139: aload 4
    //   141: invokevirtual 82	java/io/FileInputStream:close	()V
    //   144: goto +11 -> 155
    //   147: astore_0
    //   148: aload_0
    //   149: invokevirtual 85	java/io/IOException:printStackTrace	()V
    //   152: goto +3 -> 155
    //   155: aconst_null
    //   156: areturn
    //   157: aload_3
    //   158: ifnull +18 -> 176
    //   161: aload_3
    //   162: invokevirtual 82	java/io/FileInputStream:close	()V
    //   165: goto +11 -> 176
    //   168: astore_3
    //   169: aload_3
    //   170: invokevirtual 85	java/io/IOException:printStackTrace	()V
    //   173: goto +3 -> 176
    //   176: aload_0
    //   177: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	178	0	paramFile	File
    //   10	28	1	localObject1	Object
    //   12	109	2	localObject2	Object
    //   14	148	3	localObject3	Object
    //   168	2	3	localIOException	java.io.IOException
    //   16	124	4	localObject4	Object
    //   23	77	5	localMessageDigest	java.security.MessageDigest
    //   33	14	6	localFileInputStream	java.io.FileInputStream
    //   70	17	7	i	int
    // Exception table:
    //   from	to	target	type
    //   108	112	115	java/io/IOException
    //   18	25	122	finally
    //   30	35	122	finally
    //   40	46	122	finally
    //   54	60	122	finally
    //   65	72	122	finally
    //   82	91	122	finally
    //   99	108	122	finally
    //   130	134	122	finally
    //   18	25	126	java/lang/Exception
    //   30	35	126	java/lang/Exception
    //   40	46	126	java/lang/Exception
    //   54	60	126	java/lang/Exception
    //   65	72	126	java/lang/Exception
    //   82	91	126	java/lang/Exception
    //   99	108	126	java/lang/Exception
    //   139	144	147	java/io/IOException
    //   161	165	168	java/io/IOException
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/android/util/launcher/Md5Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */