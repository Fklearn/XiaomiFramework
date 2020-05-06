package android.widget;

import android.content.Context;

class ToastInjector
{
  /* Error */
  static CharSequence addAppName(Context paramContext, CharSequence paramCharSequence)
  {
    // Byte code:
    //   0: aload_1
    //   1: astore_2
    //   2: getstatic 19	miui/os/Build:IS_INTERNATIONAL_BUILD	Z
    //   5: ifne +98 -> 103
    //   8: aload_1
    //   9: astore_2
    //   10: aload_0
    //   11: invokevirtual 25	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   14: getfield 31	android/content/pm/ApplicationInfo:flags	I
    //   17: iconst_1
    //   18: iand
    //   19: ifne +84 -> 103
    //   22: aload_0
    //   23: invokevirtual 25	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   26: getfield 34	android/content/pm/ApplicationInfo:labelRes	I
    //   29: istore_3
    //   30: iload_3
    //   31: ifeq +39 -> 70
    //   34: new 36	java/lang/StringBuilder
    //   37: astore_2
    //   38: aload_2
    //   39: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   42: aload_2
    //   43: aload_0
    //   44: invokevirtual 41	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   47: iload_3
    //   48: invokevirtual 47	android/content/res/Resources:getString	(I)Ljava/lang/String;
    //   51: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: pop
    //   55: aload_2
    //   56: ldc 53
    //   58: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: pop
    //   62: aload_2
    //   63: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: astore_0
    //   67: goto +6 -> 73
    //   70: ldc 59
    //   72: astore_0
    //   73: new 36	java/lang/StringBuilder
    //   76: astore_2
    //   77: aload_2
    //   78: invokespecial 37	java/lang/StringBuilder:<init>	()V
    //   81: aload_2
    //   82: aload_0
    //   83: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: pop
    //   87: aload_2
    //   88: aload_1
    //   89: invokeinterface 62 1 0
    //   94: invokevirtual 51	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: pop
    //   98: aload_2
    //   99: invokevirtual 57	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   102: astore_2
    //   103: aload_2
    //   104: areturn
    //   105: astore_0
    //   106: goto +8 -> 114
    //   109: astore_0
    //   110: aload_0
    //   111: invokevirtual 65	java/lang/Exception:printStackTrace	()V
    //   114: aload_1
    //   115: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	116	0	paramContext	Context
    //   0	116	1	paramCharSequence	CharSequence
    //   1	103	2	localObject	Object
    //   29	19	3	i	int
    // Exception table:
    //   from	to	target	type
    //   2	8	105	finally
    //   10	30	105	finally
    //   34	67	105	finally
    //   73	103	105	finally
    //   110	114	105	finally
    //   2	8	109	java/lang/Exception
    //   10	30	109	java/lang/Exception
    //   34	67	109	java/lang/Exception
    //   73	103	109	java/lang/Exception
  }
  
  static boolean interceptBackgroundToast(Toast paramToast, Context paramContext)
  {
    return true;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ToastInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */